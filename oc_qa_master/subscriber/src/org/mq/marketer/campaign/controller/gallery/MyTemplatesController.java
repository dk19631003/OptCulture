package org.mq.marketer.campaign.controller.gallery;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.SystemTemplates;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.MyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.SystemTemplatesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SpamChecker;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkforge.ckez.CKeditor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;


@SuppressWarnings("rawtypes")
public class MyTemplatesController extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private String userName = null;
	private String usersParentDirectory = null;
	private UserActivitiesDao userActivitiesDao = null;
	private UserActivitiesDaoForDML userActivitiesDaoForDML = null;
	private Listbox myTemplatesListId,newEditorTemplatesListId ;
	private MyTemplatesDao myTemplatesDao;
	private MyTemplatesDaoForDML myTemplatesDaoForDML;
	private Long userId;
	private Session sessionScope = null;
	private Window newMyTemplatesSubWinId,testTempWinId;
	private Textbox newMyTemplatesSubWinId$newFolderTbId,testTempWinId$testTempTbId;
	private Label newMyTemplatesSubWinId$folderErrorMsgLblId,testTempWinId$msgLblId;
	private MyTemplates myTemplates = null;
	private SystemTemplatesDao systemTemplatesDao;
	Window testWinId;
	Label testWinId$msgLblId;
	Textbox emailNameTbId,testWinId$testTbId;
	private Button testWinId$sendTestMailBtnId;
	CKeditor myTempltCkEditorId;
	private Textbox  cSubTb;
	private Users currentUser;
	private Listbox templatePerPageLBId;
	private Paging templatePagingId;
	
	
	public MyTemplatesController(){
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Templates","",style,true);
		//userName = GetUser.getUserName();
		userName = GetUser.getUserObj().getUserName();
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		myTemplatesDao=(MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
		myTemplatesDaoForDML=(MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
		 systemTemplatesDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
		
		sessionScope = Sessions.getCurrent();
		 myTemplates =(MyTemplates)sessionScope.getAttribute("myTemplates"); 
		 /*if(userActivitiesDao != null) {
		     userActivitiesDao.addToActivityList(ActivityEnum.VISIT_GALLERY_VIEW,GetUser.getUserObj());
		 }*/
		 if(userActivitiesDaoForDML != null) {
		     userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_GALLERY_VIEW,GetUser.getLoginUserObj());
		 }
	}
	
	Image manageFolderImg = new Image("/img/theme/arrow.png");
	Image manageNewEditorFolderImg = new Image("/img/theme/arrow.png");
	private final String  USER_TEMPLATES_DIRECTORY= "userTemplatesDirectory"; 
	private final String  USER_NEW_EDITOR_TEMPLATES_DIRECTORY= "userNewEditorTemplatesDirectory"; 
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		userId = GetUser.getUserObj().getUserId();
		currentUser = GetUser.getUserObj();
		// display default folder my templates
		Components.removeAllChildren(newEditorTemplatesListId);
		Components.removeAllChildren(myTemplatesListId);
		//myTempGbId.setOpen(true);

		String newEditorfolderPathStr = usersParentDirectory+
				File.separator+ GetUser.getUserObj().getUserName()+PropertyUtil.getPropertyValue("userNewEditorTemplatesDirectory")+File.separator;
		 File newEditorfolder = new File(newEditorfolderPathStr);
		 logger.info("file size is"+newEditorfolder.listFiles().length);
		 logger.info("is directory "+newEditorfolder.isDirectory());
		 
		 
		 if(newEditorfolder.isDirectory() && newEditorfolder.listFiles().length == 0){
			 String dirName= Constants.NEWEDITOR_TEMPLATES_FOLDERS_DEFAULT;
			 File newDir= new File(newEditorfolderPathStr+dirName);
			 newDir.mkdir();
			 logger.info("entered in to default folders"+newDir.getName());
				String dirNameAuto=Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS;
				 File autoDir=new File(newEditorfolderPathStr+dirNameAuto);
				 autoDir.mkdir();
			 
				 logger.info("entered in to draft folders"+autoDir.getName());
		 }
		
		 if(newEditorfolder.isDirectory() && newEditorfolder.listFiles().length >= 0){
			File[] foldstr=newEditorfolder.listFiles();
			for (File file : foldstr) {
				if(!file.getName().equals(Constants.MYTEMPATES_FOLDERS_DRAFTS)){
					
					String dirNameAuto=Constants.MYTEMPATES_FOLDERS_DRAFTS;
					 File autoDir=new File(newEditorfolderPathStr+dirNameAuto);
					 autoDir.mkdir();
				}
					
				
			}
			
		 }
		 
		 
		 String folderPathStr = usersParentDirectory+
					File.separator+ GetUser.getUserObj().getUserName()+PropertyUtil.getPropertyValue(USER_TEMPLATES_DIRECTORY)+File.separator;
		 File folderFile = new File(folderPathStr);
		 logger.info("file size is"+folderFile.listFiles().length);
		 logger.info("is directory "+folderFile.isDirectory());
		 
		 if(folderFile.isDirectory() && folderFile.listFiles().length == 0){
			 String dirName= Constants.MYTEMPATES_FOLDERS_DEFAULT;
			 File newDir= new File(folderPathStr+dirName);
			 newDir.mkdir();
			 logger.info("entered in to default folders"+newDir.getName());
				String dirNameAuto=Constants.MYTEMPATES_FOLDERS_DRAFTS;
				 File autoDir=new File(folderPathStr+dirNameAuto);
				 autoDir.mkdir();
			 
				 logger.info("entered in to draft folders"+autoDir.getName());
		 }
		
		 if(folderFile.isDirectory() && folderFile.listFiles().length >= 0){
			File[] foldstr=folderFile.listFiles();
			for (File file : foldstr) {
				if(!file.getName().equals(Constants.MYTEMPATES_FOLDERS_DRAFTS)){
					
					String dirNameAuto=Constants.MYTEMPATES_FOLDERS_DRAFTS;
					 File autoDir=new File(folderPathStr+dirNameAuto);
					 autoDir.mkdir();
				}
					
				
			}
			
		 }
		 getNewEditorTemplatesFromDb(userId);
		 if(newEditorTemplatesListId.getItemCount() > 0) newEditorTemplatesListId.setSelectedIndex(0);
		 manageNewEditorFolderImg.addEventListener("onClick", this);
		 onSelect$newEditorTemplatesListId();
		 
		 
		 getMyTemplatesFromDb(userId);
		 if(myTemplatesListId.getItemCount() > 0) myTemplatesListId.setSelectedIndex(0);
		 manageFolderImg.addEventListener("onClick", this);
		 //onSelect$myTemplatesListId();
		//display system templates
		 //testing here :
		 String beekey = PropertyUtil.getPropertyValueFromDB("beeFileManagerKey");
		Clients.evalJavaScript("beekey='"+beekey+"';");
		
		//getSystemTemplatesFromDb();
		 

	}//doAfterCompose()
	
	//private Popup myPopupId;
	private Menupopup manageFolderMpId;
	public void getNewEditorTemplatesFromDb(Long userId){
		

		
		try {
			Components.removeAllChildren(newEditorTemplatesListId);
			File myTemp = null;
			File[] templateList = null;
			String myTempPathStr = usersParentDirectory+
					File.separator+ userName+PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY)+File.separator;

			myTemp= new File(myTempPathStr);
			templateList = myTemp.listFiles();
			logger.debug("myTemp.listFiles().length"+myTemp.listFiles().length);
			List<File> fileList= new ArrayList<File>();
			for(File file:templateList) {

				if(file.getName().equalsIgnoreCase(Constants.NEWEDITOR_TEMPLATES_FOLDERS_DEFAULT)||
						file.getName().equalsIgnoreCase(Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS)){	
					logger.debug("------in if case----"+file.getName());
					String folderName=file.getName();//.equals(Constants.NEWEDITOR_TEMPLATES_FOLDERS_DEFAULT) ? (Constants.NEWEDITOR_TEMPLATES_FOLDERS_DEFAULT_FOLDER_NAME ): file.getName();						
					List<MyTemplates> tempList =myTemplatesDao.findDefaultMyTemplates(userId, folderName, Constants.NEWEDITOR_TEMPLATES_PARENT);
					//int totCount = myTemplatesDao.findTotDefaultMyTemplates(userId,folderName);
					Listitem item = new Listitem();//

					item.setValue(tempList);
					item.setAttribute("dirPath", PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY));
					item.setAttribute("parentDir", Constants.NEWEDITOR_TEMPLATES_PARENT);
					Listcell cell = new Listcell();

					Label tempLbl = new Label(folderName+" ("+tempList.size() +")");
					tempLbl.setStyle("width: 90%; display: inline-block;");

					cell.appendChild(tempLbl);

					//cell.appendChild(tempImg);

					cell.setParent(item);						 
					item.setParent(newEditorTemplatesListId);
				}else {
					fileList.add(file);
				}
			}


			int size=fileList.size();
			int i=0;
			File[] fileArray=new File[size];
			for (File file : fileList) {
				fileArray[i]=file;
				i++;
			}
			Arrays.sort(fileArray);

			for (Object obj: fileArray) {
				final File template = (File)obj;
				logger.debug("template.getName()-----"+template);
				String folderName=template.getName();

				List<MyTemplates> tempList =myTemplatesDao.findDefaultMyTemplates(userId, folderName, Constants.NEWEDITOR_TEMPLATES_PARENT);
				//int totCount = myTemplatesDao.findTotDefaultMyTemplates(userId,folderName);
				Listitem item = new Listitem();//

				item.setValue(tempList);
				item.setAttribute("dirPath", PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY));
				
				item.setAttribute("parentDir", Constants.NEWEDITOR_TEMPLATES_PARENT);
				Listcell cell = new Listcell();

				Label tempLbl = new Label(folderName+"("+tempList.size() +")");
				tempLbl.setStyle("width: 90%; display: inline-block;");

				cell.appendChild(tempLbl);

				//cell.appendChild(tempImg);

				cell.setParent(item);

				item.setParent(newEditorTemplatesListId);
			}
			

			
			/*try {
				List<TemplateCategory> tempCategoryList;
				
				TemplateCategoryDao templateCategoryDao = (TemplateCategoryDao)SpringUtil.getBean("templateCategoryDao");
				tempCategoryList = templateCategoryDao.findNewEditorCategories();
				Listitem li;
				if(tempCategoryList != null && tempCategoryList.size() > 0 ) {
					for (TemplateCategory templateCategory: tempCategoryList) {
						
					//	logger.info("tempCategory name"+templateCategory.getCategoryName());	
						if(!templateCategory.getCategoryName().equals(Constants.TEMPLATE_CATEGORY_GENERIC))continue;
						li = new Listitem();
						
						li.setLabel(templateCategory.getCategoryName());
						li.setValue(templateCategory);
						//li.addEventListener("onClick", this);
						li.setParent(newEditorTemplatesListId);
						
					}
				//	systemTemplatesListId.setSelectedIndex(0);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}
			*/

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
		
		
		
		
	}
	public void getMyTemplatesFromDb(Long userId){
		
		try {
			Components.removeAllChildren(myTemplatesListId);
			File myTemp = null;
			File[] templateList = null;
			String myTempPathStr = usersParentDirectory+
					File.separator+ userName+PropertyUtil.getPropertyValue(USER_TEMPLATES_DIRECTORY)+File.separator;

			myTemp= new File(myTempPathStr);
			templateList = myTemp.listFiles();
			logger.debug("myTemp.listFiles().length"+myTemp.listFiles().length);
			List<File> fileList= new ArrayList<File>();
			for(File file:templateList) {

				if(file.getName().equalsIgnoreCase(Constants.MYTEMPATES_FOLDERS_DEFAULT)||
						file.getName().equalsIgnoreCase(Constants.MYTEMPATES_FOLDERS_DRAFTS)){	
					logger.debug("------in if case----"+file.getName());
					String folderName=file.getName();						
					List<MyTemplates> tempList =myTemplatesDao.findDefaultMyTemplates(userId, folderName, Constants.MYTEMPATES_PARENT);
					//int totCount = myTemplatesDao.findTotDefaultMyTemplates(userId,folderName);
					Listitem item = new Listitem();//

					item.setValue(tempList);
					item.setAttribute("dirPath", PropertyUtil.getPropertyValue(USER_TEMPLATES_DIRECTORY));
					item.setAttribute("parentDir", Constants.MYTEMPATES_PARENT);
					Listcell cell = new Listcell();

					Label tempLbl = new Label(folderName+" ("+tempList.size() +")");
					tempLbl.setStyle("width: 90%; display: inline-block;");

					cell.appendChild(tempLbl);

					//cell.appendChild(tempImg);

					cell.setParent(item);						 
					item.setParent(myTemplatesListId);
				}else {
					fileList.add(file);
				}
			}


			int size=fileList.size();
			int i=0;
			File[] fileArray=new File[size];
			for (File file : fileList) {
				fileArray[i]=file;
				i++;
			}
			Arrays.sort(fileArray);

			for (Object obj: fileArray) {
				final File template = (File)obj;
				logger.debug("template.getName()-----"+template);
				String folderName=template.getName();

				List<MyTemplates> tempList =myTemplatesDao.findDefaultMyTemplates(userId, folderName, Constants.MYTEMPATES_PARENT);
				//int totCount = myTemplatesDao.findTotDefaultMyTemplates(userId,folderName);
				Listitem item = new Listitem();//

				item.setValue(tempList);
				item.setAttribute("dirPath", PropertyUtil.getPropertyValue(USER_TEMPLATES_DIRECTORY));
				item.setAttribute("parentDir", Constants.MYTEMPATES_PARENT);
				Listcell cell = new Listcell();

				Label tempLbl = new Label(folderName+" ("+tempList.size() +")");
				tempLbl.setStyle("width: 90%; display: inline-block;");

				cell.appendChild(tempLbl);

				//cell.appendChild(tempImg);

				cell.setParent(item);

				item.setParent(myTemplatesListId);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
		
		}//getMyTemplatesFromDb()
		
	
	
	//private Listbox systemTemplatesListId;
	
	/*public void getSystemTemplatesFromDb(){
		
				try {
					List<TemplateCategory> tempCategoryList;
					
					TemplateCategoryDao templateCategoryDao = (TemplateCategoryDao)SpringUtil.getBean("templateCategoryDao");
					tempCategoryList = templateCategoryDao.findCategories();
					Listitem li;
					if(tempCategoryList != null && tempCategoryList.size() > 0 ) {
						for (TemplateCategory templateCategory: tempCategoryList) {
							
						//	logger.info("tempCategory name"+templateCategory.getCategoryName());	
							if(templateCategory.getCategoryName().equals(Constants.TEMPLATE_CATEGORY_MYTEMPLATES) ||
									templateCategory.getCategoryName().equals(Constants.TEMPLATE_CATEGORY_GENERIC))continue;
							li = new Listitem();
							
							li.setLabel(templateCategory.getCategoryName());
							li.setValue(templateCategory);
							li.addEventListener("onClick", this);
							li.setParent(systemTemplatesListId);
							
						}
					//	systemTemplatesListId.setSelectedIndex(0);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);;
				}
				
	}//getSystemTemplatesFromDb()
*/	
private Div templListDivId;
	/**
	 * Gets the templates previews of selected category
	 * @param categoryListLbId
	 * @param templListDivId
	 */
	/*public void onSelect$systemTemplatesListId() {
		try{
			//logger.debug(" -- just entered --");
			logger.info(" -- just entered system templates --");
			myTemplatesListId.clearSelection();
			Components.removeAllChildren(templListDivId);
			TemplateCategory templateCategory = (TemplateCategory)systemTemplatesListId.getSelectedItem().getValue();
			final String categoryName = templateCategory.getDirName();
			String categoryPathStr ;
			File category = null;
			File[] templateList = null;
			List<File[]> templateFileList = new ArrayList<File[]>();
			String templatePrevStr = null;
			String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
			String preview_Path = null;
			
			templateFileList.clear();
			categoryPathStr = PropertyUtil.getPropertyValue("templateParent")+
			File.separator+categoryName;
			category = new File(categoryPathStr);
			templateList = category.listFiles();
			templateFileList.add(templateList);
			preview_Path =appUrl+"SystemData/Templates/"+categoryName;
			templatePrevStr = "/SystemData/Templates/"+categoryName;
			List<SystemTemplates> systemTempList = systemTemplatesDao.findByCategoryName(templateCategory.getCategoryName());	
			
		//	logger.debug("--4--" + category.exists() + "  " + category.isDirectory());
			
			Image templateImg = null;
			
			
			Toolbarbutton preview,edit;
			Label templateName;
			Hbox hbox;
			String preview_Src ="";
		   final String useIconPathStr = "/img/theme/use_icon.png";
			String previewIconPathStr = "/img/theme/preview_icon.png";
			//final String deleteIconPathStr = "/img/theme/action_delete.gif";
			String vbStyle = "templatePrviewVb";
			String custom_temp_default_preview = "/img/custom_temp_preview.gif";
			
			if(templateFileList.size() == 0 ) {
				//logger.warn(">>>>>>>>>>>>>>>>>>>> Template list is empty/null");
			}
			
		
			
			for (SystemTemplates systemTemplates : systemTempList) {
				boolean isFound = false;
			for(File template:templateList) {
				
					//final File template = (File)obj;
				if(!template.getName().equals(systemTemplates.getDirName())) {
					//logger.info("systemTemplate ::"+systemTemplates.getDirName()+" template.getName() "+template.getName());
					continue;
				}
				
				isFound = true;
				
					if(template.isDirectory()){
						
					//	logger.debug(template.getPath());
						final Vbox vbox = new Vbox();
						vbox.setSclass(vbStyle);
						vbox.setAlign("center");
						
						templateImg = new Image(templatePrevStr+"/"+template.getName()+"/preview.gif");
						
						preview_Src = preview_Path+"/"+template.getName()+"/email.html";
						
						if(preview_Src.contains("href='")){
							preview_Src = preview_Src.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
							
						}
						if(preview_Src.contains("href=\"")){
							preview_Src = preview_Src.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
						}
						preview = new Toolbarbutton("");
						preview.setImage(previewIconPathStr);
						preview.setTooltiptext("Preview");
						
						// preview using javascript
						preview.setWidgetListener("onClick", "previewByUrl('"+preview_Src+"')");
						//preview.setAction("onclick:previewByUrl('"+preview_Src+"')");
						
						
						edit = new Toolbarbutton();
						edit.setImage(useIconPathStr);
						edit.setTooltiptext("Use this layout");
						edit.setAttribute("type", "SystemTemplate");
						edit.setAttribute("Template", systemTemplates);
						edit.addEventListener("onClick", this);
						
					
						
						templateName = new Label(template.getName());
						templateName.setMaxlength(30);
						hbox = new Hbox();
						hbox.setWidth("160px");
						hbox.setPack("center");
						//hbox.setAlign("center");
						hbox.setSclass("st_hb");
						
						//templateName.setParent(hbox);
						preview.setParent(hbox);
						edit.setParent(hbox);
						
						templateImg.setParent(vbox);
						templateName.setParent(vbox);
						hbox.setParent(vbox);
						vbox.setParent(templListDivId);
					}
					
					
					if(isFound) break;
			}
		}
		
		
		}catch(Exception e){
			//logger.error("** Exception :", (Throwable)e);
		}
	} // onSelect$systemTemplatesListId
*/	
	/**
	 * Gets the templates previews of selected category
	 * @param myTemplatesListId
	 * @param templListDivId
	 */
	public void onSelect$myTemplatesListId() {
		
		try {
			Listitem selLi = myTemplatesListId.getSelectedItem();
			logger.info("folder"+selLi);
			//manageFolderImg.setParent(null);
			if(!((Label)((Listcell)selLi.getFirstChild()).getFirstChild()).getValue().startsWith(Constants.MYTEMPATES_FOLDERS_DRAFTS)){
					//&& !((Label)((Listcell)selLi.getFirstChild()).getFirstChild()).getValue().startsWith(Constants.MYTEMPATES_FOLDERS_DRAFTS)){
			manageFolderImg.setParent(selLi.getFirstChild());
			
			manageFolderImg.setAttribute("parentDir", Constants.MYTEMPATES_PARENT);
			}
			List children = selLi.getChildren(); 
			String folderName = ((Label)((Listcell)children.get(0)).getFirstChild()).getValue();
			folderName = folderName.contains("(") ? folderName.substring(0,folderName.indexOf("(")) : folderName;
			folderName = folderName.trim();
			List<MyTemplates> MyTemplateslist = myTemplatesDao.findTemplatesFromUserIdAndFolderName(userId, folderName);
			templatePagingId.setActivePage(0);
			templatePagingId.setTotalSize(MyTemplateslist.size());
			int pageSize = Integer.parseInt(templatePerPageLBId.getSelectedItem().getLabel());
			templatePagingId.setPageSize(pageSize);
			displayTemplates(selLi, true, PropertyUtil.getPropertyValue(USER_TEMPLATES_DIRECTORY), 0, pageSize);
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);;
		}
	} // onSelect$myTemplatesListId()
	
	public void onPaging$templatePagingId(Event event) {
		try {
			Listitem selLi = newEditorTemplatesListId.getSelectedItem();
			int desiredPage = templatePagingId.getActivePage();
			int pSize = templatePagingId.getPageSize();
			int ofs = desiredPage * pSize;
			displayTemplates(selLi, true, PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY), ofs, Integer.parseInt(templatePerPageLBId.getSelectedItem().getLabel()));
		} catch (Exception e) {
			logger.error("Exception ::" , e);;
		}
	}

	public void onSelect$templatePerPageLBId() {
		try {
				templatePagingId.setActivePage(0);
				int pageSize = Integer.parseInt(templatePerPageLBId.getSelectedItem().getLabel());
				templatePagingId.setPageSize(pageSize);
				int count = Integer.parseInt(templatePerPageLBId.getSelectedItem().getLabel());
				displayTemplates(newEditorTemplatesListId.getSelectedItem(), true, PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY), 0, count);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}
	
	public void onSelect$newEditorTemplatesListId() {
		
		try {
			
			Listitem selLi = newEditorTemplatesListId.getSelectedItem();
			
			if(selLi.getLabel().equalsIgnoreCase(Constants.TEMPLATE_CATEGORY_GENERIC)){/*
				List<SystemTemplates> systemTempList = systemTemplatesDao.findByCategoryName(Constants.TEMPLATE_CATEGORY_GENERIC);
				
				
				Image templateImg = null;
				List<File[]> templateFileList = new ArrayList<File[]>();
				templateFileList.clear();
				File[] templateList = null;
				String templatePrevStr = null;
				String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
				String preview_Path = null;
				String categoryPathStr = PropertyUtil.getPropertyValue("templateParent")+
				File.separator+Constants.TEMPLATE_CATEGORY_GENERIC;
				File category = new File(categoryPathStr);
				templateList = category.listFiles();
				templateFileList.add(templateList);
				preview_Path =appUrl+"SystemData/Templates/"+Constants.TEMPLATE_CATEGORY_GENERIC;
				templatePrevStr = "/SystemData/Templates/"+Constants.TEMPLATE_CATEGORY_GENERIC;
				
				
				Toolbarbutton preview,edit;
				Label templateName;
				Hbox hbox;
				String preview_Src ="";
			   final String useIconPathStr = "/img/theme/use_icon.png";
				String previewIconPathStr = "/img/theme/preview_icon.png";
				//final String deleteIconPathStr = "/img/theme/action_delete.gif";
				String vbStyle = "templatePrviewVb";
				String custom_temp_default_preview = "/img/custom_temp_preview.gif";
				
				if(templateFileList.size() == 0 ) {
					//logger.warn(">>>>>>>>>>>>>>>>>>>> Template list is empty/null");
				}
				
				for (SystemTemplates systemTemplates : systemTempList) {
					boolean isFound = false;
				for(File template:templateList) {
					
						//final File template = (File)obj;
					if(!template.getName().equals(systemTemplates.getDirName())) {
						//logger.info("systemTemplate ::"+systemTemplates.getDirName()+" template.getName() "+template.getName());
						continue;
					}
					
					isFound = true;
					
						if(template.isDirectory()){
							
						//	logger.debug(template.getPath());
							final Vbox vbox = new Vbox();
							vbox.setSclass(vbStyle);
							vbox.setAlign("center");
							
							templateImg = new Image(templatePrevStr+"/"+template.getName()+"/preview.gif");
							
							preview_Src = preview_Path+"/"+template.getName()+"/email.html";
							
							if(preview_Src.contains("href='")){
								preview_Src = preview_Src.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
								
							}
							if(preview_Src.contains("href=\"")){
								preview_Src = preview_Src.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
							}
							preview = new Toolbarbutton("");
							preview.setImage(previewIconPathStr);
							preview.setTooltiptext("Preview");
							
							// preview using javascript
							preview.setWidgetListener("onClick", "previewByUrl('"+preview_Src+"')");
							//preview.setAction("onclick:previewByUrl('"+preview_Src+"')");
							
							
							edit = new Toolbarbutton();
							edit.setImage(useIconPathStr);
							edit.setTooltiptext("Use this layout");
							edit.setAttribute("type", "SystemTemplate");
							edit.setAttribute("Template", systemTemplates);
							edit.addEventListener("onClick", this);
							
						
							
							templateName = new Label(template.getName());
							templateName.setMaxlength(30);
							hbox = new Hbox();
							hbox.setWidth("160px");
							hbox.setPack("center");
							//hbox.setAlign("center");
							hbox.setSclass("st_hb");
							
							//templateName.setParent(hbox);
							preview.setParent(hbox);
							edit.setParent(hbox);
							
							templateImg.setParent(vbox);
							templateName.setParent(vbox);
							hbox.setParent(vbox);
							vbox.setParent(templListDivId);
						}
						
						
						if(isFound) break;
				}
			}
			*/}else{
				logger.info("folder"+selLi);
				//manageFolderImg.setParent(null);
				if(!((Label)((Listcell)selLi.getFirstChild()).getFirstChild()).getValue().startsWith(Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS)){
				manageNewEditorFolderImg.setParent(selLi.getFirstChild());
				manageNewEditorFolderImg.setAttribute("parentDir", Constants.NEWEDITOR_TEMPLATES_PARENT);
				}
				List children = selLi.getChildren(); 
				String folderName = ((Label)((Listcell)children.get(0)).getFirstChild()).getValue();
				folderName = folderName.contains("(") ? folderName.substring(0,folderName.indexOf("(")) : folderName;
				folderName = folderName.trim();
				List<MyTemplates> MyTemplateslist = myTemplatesDao.findTemplatesFromUserIdAndFolderName(userId, folderName);
				templatePagingId.setActivePage(0);
				if(MyTemplateslist!=null && !MyTemplateslist.isEmpty()) {
					templatePagingId.setTotalSize(MyTemplateslist.size());
					templatePagingId.setPageSize(MyTemplateslist.size());	
				}
				int pageSize = Integer.parseInt(templatePerPageLBId.getSelectedItem().getLabel());
				templatePagingId.setPageSize(pageSize);
				displayTemplates(selLi, true, PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY), 0, pageSize);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
		
		
		
	} // onSelect$myTemplatesListId()
	private Window previewIframeWin; 
	private  Iframe previewIframeWin$iframeId;
	public void displayTemplates(Listitem item, boolean isRemoveAll, String dir, int start, int pageSize){

		try{
			logger.info(" -- just entered my templates --");
			//systemTemplatesListId.clearSelection();
			
			List children = item.getChildren(); 
			String folderName = ((Label)((Listcell)children.get(0)).getFirstChild()).getValue();
			//String folderName = (String)item.getLabel();
			logger.info("folder name"+folderName);
			//((Listcell)children.get(1)).setImage("/img/theme/preview_icon.png");
			
			//((Listcell)children.get(1)).setVisible(true);
			folderName = folderName.contains("(") ? folderName.substring(0,folderName.indexOf("(")) : folderName;
			folderName = folderName.trim();
			item.setAttribute("folderName", folderName);
			//List<MyTemplates> myTemplates = (List<MyTemplates>)item.getValue();
			List<MyTemplates> myTemplates = (List<MyTemplates>)myTemplatesDao.findTemplatesFromUserIdAndFolderName(userId, folderName, start,pageSize);
		//	logger.info("my templte obj is"+myTemplates);
		//	List<MyTemplates> myTemplates = myTemplatesDao.findDefaultMyTemplates(userId,folderName);
		//	final String categoryName = myTemplates.getName();
			if(isRemoveAll) Components.removeAllChildren(templListDivId);
			String categoryPathStr ;
			File category = null;
			File[] templateList = null;
			List<File[]> templateFileList = new ArrayList<File[]>();
			String templatePrevStr = null;
			String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
			String preview_Path = null;
			
			final String parentDir = dir.contains(Constants.MYTEMPATES_PARENT) ? Constants.MYTEMPATES_PARENT :  Constants.NEWEDITOR_TEMPLATES_PARENT;
			//String userName = GetUser.getUserName();
			String userName = GetUser.getUserObj().getUserName();
				//String userInfo = GetUser.getUserInfo();
			templateFileList.clear();
			categoryPathStr = usersParentDirectory+
			File.separator+ userName+dir+File.separator+folderName;
			logger.debug("--4--" + categoryPathStr);
			category = new File(categoryPathStr);
			templateList = category.listFiles();
			//templateFileList.addAll(templateList);
			preview_Path = appUrl +"UserData/"+userName+dir+File.separator+folderName;
			templatePrevStr = "/UserData/"+userName+dir+File.separator+folderName;
			
			
		//	logger.debug("--4--" + category.exists() + "  " + category.isDirectory());
			
			Image templateImg = null;
		
			Toolbarbutton preview,edit,deleteTb,copyTb,testMailTb;
			Label templateName;
			Hbox hbox;
			String preview_Src ="";
			final String useIconPathStr = "/images/Edit_icn.png";
			String previewIconPathStr = "/img/theme/preview_icon.png";
			final String testmailIcon="/img/email_page.gif";
			final String deleteIconPathStr = "/img/theme/action_delete.gif";
			String vbStyle = "templatePrviewVb";
			String custom_temp_default_preview = "/img/custom_temp_preview.gif";
			String pathTojs= PropertyUtil.getPropertyValueFromDB("pathToPhantomjs");
			
			
			if( templateFileList.size() == 0 ) {
			//	logger.warn(">>>>>>>>>>>>>>>>>>>> Template list is empty/null");
			}
			
			// loop for generating the templates icons for selection along with preview and use buttons
			
			
			
			
			for (MyTemplates mytemplate : myTemplates) {
				boolean isFound = false;
			for(File templateLst:templateList) {
				
				final File template = templateLst;
				logger.info("mytemplate.getName() "+mytemplate.getName() + "template.getName() " + template.getName());
					
					if(!mytemplate.getName().equals(template.getName())) {
						//logger.info("myTemplate ::"+mytemplate.getName()+" template.getName() "+template.getName());
						continue;
					}
					
					isFound = true;
					if(template.isDirectory()){
						
					//	logger.debug(template.getPath());
						final Vbox vbox = new Vbox();
						vbox.setSclass(vbStyle);
						vbox.setStyle("margin-left: 3px !important;");
						vbox.setAlign("center");
						
						String data = new String(Files.readAllBytes(Paths.get(template.getPath()+File.separator+"email.html"))); 
						try {
							File imgPathfile = new File(template.getPath()+File.separator+"email.png");
							String htmlCont = Utility.mergeTagsForPreviewAndTestMail(mytemplate.getContent(),"preview");
							if(!data.trim().equals(htmlCont.trim()) || !imgPathfile.exists()) {
								if(imgPathfile.exists()) {
									imgPathfile.delete();
								}
								FileWriter fw = new FileWriter(template.getPath()+File.separator+"email.html");
								fw.write(htmlCont);
								fw.close();
								generateThumbnil(template,pathTojs);
								//BufferedImage finalImage = scale(ImageIO.read(imgPathfile),150,220);
								//ImageIO.write(finalImage, "png", imgPathfile);
							}
							
						}catch(Exception e) {
							logger.error("thumbnail error :"+e);
						}

						
						if(! new File(template.getPath()+File.separator+"preview.gif").exists()) {
							//templateImg = new Image(custom_temp_default_preview);
							templateImg = new Image(templatePrevStr.concat(File.separator+mytemplate.getName()+"/email.png"));
							templateImg.setWidth("150px");
							templateImg.setHeight("220px");
							templateImg.setStyle("border: #CCCCCC 0.2px solid;");
						}
					else{
						templateImg = new Image(templatePrevStr+"/"+template.getName()+"/preview.gif");
					}
						
						
						preview_Src = preview_Path+"/"+template.getName()+"/email.html";
						
						/*if(preview_Src.contains("href='")){
							preview_Src = preview_Src.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
							
						}
						if(preview_Src.contains("href=\"")){
							preview_Src = preview_Src.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
						}*/
						preview = new Toolbarbutton("");
						preview.setImage(previewIconPathStr);
						preview.setTooltiptext("Preview");
						preview.setAttribute("type", "PreviewTemplate");
						preview.setAttribute("Template", mytemplate);
						// preview using javascript
						preview.addEventListener("onClick", this);
						//preview.setAction("onclick:previewByUrl('"+preview_Src+"')");
						
						testMailTb =new Toolbarbutton("");
						testMailTb.setImage(testmailIcon);
						testMailTb.setTooltiptext("Send Test Email");
						testMailTb.setAttribute("SendTestMail", mytemplate);
						testMailTb.setAttribute("type", "SendTestMail");
						testMailTb.setTooltiptext("Send Test Email");
						testMailTb.addEventListener("onClick", this);
						
						
						edit = new Toolbarbutton();
						edit.setImage(useIconPathStr);
						edit.setTooltiptext("Edit");
						edit.setAttribute("Template", mytemplate);
						edit.setAttribute("type", "MyTemplate");
						edit.addEventListener("onClick", this);
						
						copyTb = new Toolbarbutton();
						copyTb.setImage("/img/copy.gif");
						copyTb.setTooltiptext("Copy");
						copyTb.setAttribute("CopyTemplate", mytemplate);
						copyTb.setAttribute("type", "CopyMyTemplate");
						copyTb.addEventListener("onClick", this);
						
						//declared as final to use in inner class
							//declared as final to use in inner class
						final String selectedTemplateStr = "/"+template.getName();
							
							
								
						
						
						templateName = new Label(template.getName());
						templateName.setMaxlength(30);
						hbox = new Hbox();
						hbox.setWidth("160px");
						hbox.setPack("center");
						//hbox.setAlign("center");
						hbox.setSclass("st_hb");
						
						//templateName.setParent(hbox);
						preview.setParent(hbox);
						testMailTb.setParent(hbox);
						edit.setParent(hbox);
						copyTb.setParent(hbox);
						
						hbox.setWidth("190px");
						hbox.setPack("center");
//					hbox.setWidths("30px 100px 30px 30px");
						deleteTb = new Toolbarbutton();
						deleteTb.setImage(deleteIconPathStr);
						deleteTb.setTooltiptext("Delete this from My Folders");
						deleteTb.setAttribute("DeleteFolder", folderName);
						
						/**
						 * add onClick event listener to delete when clicked
						 */
						deleteTb.addEventListener("onClick", new EventListener() {
							
							@Override
							public void onEvent(Event event) throws Exception {//how we can allow the othr user's template deletion
								try {
									/*int confirm = Messagebox.show("Are you sure you want to delete the template - "
											+ template.getName() + "?",
											"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);*/
									int confirm = Messagebox.show("This template may be in use in an auto-email message."
											+ " Deleting it may cause auto-communication to behave in unexpected way.\n"
											+ "Are you sure you want to continue?","Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
									
									if(confirm != 1){
										return;
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Exception ::" , e);;
								}
								 String folderNameStr = (String)event.getTarget().getAttribute("DeleteFolder");
								template.delete();
								templListDivId.removeChild(vbox);
								/*MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
								myTemplatesDao.deleteByUserIdAndNameAndFolder(userId,template.getName(),folderNameStr);*/
								MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
								myTemplatesDaoForDML.deleteByUserIdAndNameAndFolder(userId,template.getName(),folderNameStr, parentDir);
								FileUtils.deleteDirectory(template);
								MessageUtil.setMessage("Template deleted successfully", "color:blue","TOP");
								Redirect.goTo(PageListEnum.EMPTY);
								Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
							}
							
						});
						deleteTb.setParent(hbox);
						
						
						templateImg.setParent(vbox);
						templateName.setParent(vbox);
						hbox.setParent(vbox);
						vbox.setParent(templListDivId);
						
						
					}
				
				if(isFound) break;
			}
					
					
			}
	
		}catch(Exception e){
			logger.error("Exception ::" , e);;
			//logger.error("** Exception :", (Throwable)e);
		}
	
		
		
		
	}
	
	public static void generateThumbnil(File template, String pathTojs) {
		try {
			ProcessBuilder pb = new ProcessBuilder(pathTojs+"bin/phantomjs",pathTojs+"htmltoImage/htmltoImage.js", template.getAbsolutePath().concat("/email.html"), template.getAbsolutePath().concat("/email.png"));
			Process p = pb.start();
			int exitVal = p.waitFor(); // do a wait here to prevent it running for ever
			if (exitVal != 0) {
				logger.error("EXIT-STATUS - " + p.toString());
			}
			p.destroy();
		}catch (Exception e) {
			logger.error("loading image error");
		}
		
	}
	

	//private Groupbox myTempGbId,sysTemGbId;
	private Toolbarbutton viewAllTemplatesTBarBtnId;
	private Button createTemplateTBarBtnId;
	
	private Tabbox tabBoxId;
	public void onSelect$tabBoxId(){
		
		try {
			
			if(tabBoxId.getSelectedIndex() == 0) {
				//Components.removeAllChildren(newEditorTemplatesListId);
				getNewEditorTemplatesFromDb(userId);
				if(newEditorTemplatesListId.getItemCount() > 0) {
					newEditorTemplatesListId.setSelectedIndex(0);
					onSelect$newEditorTemplatesListId();
					
				}
				//onSelect$myTemplatesListId();
			}
			else if(tabBoxId.getSelectedIndex() == 1) {
				//Components.removeAllChildren(myTemplatesListId);
				getMyTemplatesFromDb(userId);
				if(myTemplatesListId.getItemCount() > 0) {
					myTemplatesListId.setSelectedIndex(0);
					onSelect$myTemplatesListId();
					
				}
				//onSelect$myTemplatesListId();
			}/*else if(tabBoxId.getSelectedIndex() == 2){
				Components.removeAllChildren(systemTemplatesListId);
				getSystemTemplatesFromDb();
				if(systemTemplatesListId.getItemCount() > 0) {
					
					systemTemplatesListId.setSelectedIndex(0);
					onSelect$systemTemplatesListId();
					
				}
				//systemTemplatesListId.setSelectedIndex(0);
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	}//onClick$tabBoxId()
	private Textbox winId$templatNameTbId;
	private Window winId;
	private Label winId$resLbId;
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		Object eventTarget = event.getTarget();
		
		if (eventTarget instanceof Toolbarbutton) {
			
			Toolbarbutton tb = (Toolbarbutton)eventTarget;
			String type = (String)tb.getAttribute("type");
			boolean isEdit=false;
			sessionScope.removeAttribute("isTemplateEdit");
			if(type.equalsIgnoreCase("MyTemplate")) {
				
			MyTemplates mytemplate = (MyTemplates)tb.getAttribute("Template");
			//sessionScope.setAttribute("Template", mytemplate);
			isEdit= true;
			
			
			String editorType = mytemplate.getEditorType();
			PageListEnum  page = null;
			logger.info("editor type is"+editorType);
			if(editorType.equalsIgnoreCase("blockEditor")) {
				
				//mytemplate.setContent(null);
				
				page = PageListEnum.GALLERY_MY_TEMPALTES_BLOCK_EDITOR;
			}
			else if(editorType.equalsIgnoreCase("plainTextEditor")){
				page = PageListEnum.GALLERY_MY_TEMPALTES_PLAIN_EDITOR;
			}
			else if(editorType.equalsIgnoreCase("plainHtmlEditor")){
				page = PageListEnum.GALLERY_MY_TEMPALTES_HTML_EDITOR;
			}
			else if(editorType.equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)){
				page = PageListEnum.GALLERY_MY_TEMPALTES_BEE_EDITOR;
			}
			sessionScope.setAttribute( "editorType", editorType);
			sessionScope.setAttribute("Template", mytemplate);
			
			sessionScope.setAttribute("isTemplateEdit",isEdit);
			Redirect.goTo(page);
			
			}else if(type.equalsIgnoreCase("SystemTemplate")) {
				
				SystemTemplates systemTemplate = (SystemTemplates)tb.getAttribute("Template");
				sessionScope.setAttribute("Template", systemTemplate);
				
				//String editorType = "blockEditor";
				//sessionScope.setAttribute("editorType", editorType);
				
				if(systemTemplate.getJsonText()==null)
					{
					
					String editorType = "blockEditor";
					sessionScope.setAttribute("editorType", editorType);
					Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_BLOCK_EDITOR);
				} else
					{
					String editorType = "beeEditor";
					sessionScope.setAttribute("editorType", editorType);
					Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_BEE_EDITOR);
				}
				
				//Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_BLOCK_EDITOR);
			}
			else if(type.equalsIgnoreCase("CopyMyTemplate")) {
				
				MyTemplates mytemplate = (MyTemplates)tb.getAttribute("CopyTemplate");
				winId.setAttribute("TemplateCopy", mytemplate);
				isEdit= true;
				
				
				String editorType = mytemplate.getEditorType();
				winId$resLbId.setValue("");
				
				winId.setVisible(true);
				winId.setPosition("center");
				String nameStr=mytemplate.getName();
				if(nameStr.length() > 17 ){
				
					nameStr = nameStr.substring(0, 16);
					String copyname = ("Copy of "+nameStr).trim();
					winId$templatNameTbId.setValue(copyname);
					//logger.info("copyname...."+copyname);
					//winId$templatNameTbId.setValue("Copy of "+nameStr);
				}else{
					String copyname = ("Copy of "+nameStr).trim();
					//logger.info("copyname...."+copyname);
					winId$templatNameTbId.setValue(copyname);
					
					//winId$templatNameTbId.setValue("Copy of "+nameStr);
				}
				winId.doHighlighted();
				getMyTempCopy(userId, editorType);
				
				
				/*PageListEnum  page = null;
				logger.debug("editor type is"+editorType);
				if(editorType.equalsIgnoreCase("blockEditor")) {
					
					//mytemplate.setContent(null);
					
					page = PageListEnum.GALLERY_MY_TEMPALTES_BLOCK_EDITOR;
				}
				else if(editorType.equalsIgnoreCase("plainTextEditor")){
					page = PageListEnum.GALLERY_MY_TEMPALTES_PLAIN_EDITOR;
				}
				else if(editorType.equalsIgnoreCase("plainHtmlEditor")){
					page = PageListEnum.GALLERY_MY_TEMPALTES_HTML_EDITOR;
				}*/
				sessionScope.setAttribute("editorType", editorType);
				sessionScope.setAttribute("Template", mytemplate);
				
				sessionScope.setAttribute("isTemplateEdit",isEdit);
				//Redirect.goTo(page);
			}
			else if(type.equalsIgnoreCase("PreviewTemplate")){
				MyTemplates mytemplate = (MyTemplates)tb.getAttribute("Template");
				String htmlContent = Utility.mergeTagsForPreviewAndTestMail(mytemplate.getContent(),"preview");
				Utility.showPreview(previewIframeWin$iframeId,userName,htmlContent);
				previewIframeWin.setVisible(true);
				
			}
			else if(type.equalsIgnoreCase("SendTestMail")){
				logger.info("sendTestMail Entrance.......");
				MyTemplates mytemplate = (MyTemplates)tb.getAttribute("SendTestMail");
				logger.info("sendTestMail Value......." + mytemplate.getContent());
				if(mytemplate!=null){
					testWinId.setAttribute("TestMailTemplate", mytemplate);
					testWinId$testTbId.setValue("");
					testWinId.setVisible(true);
					testWinId.setPosition("center");
					testWinId.doHighlighted();
					logger.info("send test email exit........");
				}
				
				
			
		}
		
		else if (eventTarget instanceof Image) {
			Image img = (Image)eventTarget;
			manageFolderMpId.open(img,"after_start"); 
			manageFolderMpId.setAttribute("target", img);
		}
		}

		
	}
	
	public void onClick$sendTestMailBtnId$testWinId() {		
		String emailId = testWinId$testTbId.getValue();
		boolean isValid = validateEmailAddr(emailId);
		if(isValid){
			testWinId$sendTestMailBtnId.setDisabled(true);
			MyTemplates mytemplates=(MyTemplates)testWinId.getAttribute("TestMailTemplate");

			testWinId$msgLblId.setValue("");
			String emailId1 = testWinId$testTbId.getText();

			String[] emailArr = emailId1.split(",");
			for (String email : emailArr) {
				if(mytemplates!=null) {
					sendTestMail(mytemplates.getContent(), email);
				}else {
					break;
				}
			}//for
			testWinId.setVisible(false);
			testWinId$sendTestMailBtnId.setDisabled(false);

		}
	}

	public void onClick$cancelSendTestMailBtnId$testWinId() {
		testWinId$msgLblId.setValue("");
		testWinId$msgLblId.setValue("");
		testWinId.setVisible(false);
	}

	protected boolean sendTestMail(String htmlStuff,String emailId) {

		EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");

		try {
			if( htmlStuff == null || htmlStuff.isEmpty() ){
				MessageUtil.setMessage("There is no content to send a test mail.", "color:red");

				return false;
			}

			if(logger.isDebugEnabled())logger.debug("Sending the test mail....");

			MessageUtil.clearMessage();
			String isValidPhStr = null;
			isValidPhStr = Utility.validatePh(htmlStuff, currentUser);

			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				return false;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				return false;

			}

			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
			if(isValidCouponAndBarcode != null){
				return false;
			}


			long size =	Utility.validateHtmlSize(htmlStuff);
			if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				MessageUtil.setMessage(msgcontent, "color:Blue");
			}

			if(!checkSpam(htmlStuff)){
				return false;
			}

			String[] emailArr = null;

			emailArr = emailId.split(",");
			for (String email : emailArr) {

				if(!Utility.validateEmail(email.trim())) {

					Messagebox.show("Please enter a valid email address. '"+email+"' is invalid.", "Error", Messagebox.OK, Messagebox.ERROR);
					return false;
				}

			}//for

			//Check whether user is expired or not
			if(Calendar.getInstance().after(currentUser.getPackageExpiryDate())){
				logger.debug("Current User::"+currentUser.getUserId()+" is expired, hence cannot send test mail");
				MessageUtil.setMessage("Your account validity period has expired. \n Please renew your subscription to continue.", "color:red", "TOP");
				return false;
			}
			//TODO
			
			htmlStuff = Utility.mergeTagsForPreviewAndTestMail(htmlStuff,"testMail");
			
			for (String email : emailArr) {
				logger.info("Test Mail "+ email);
				Utility.sendInstantMail(null,"My Template", htmlStuff,
						Constants.EQ_TYPE_TEST_OPTIN_MAIL, email, null );

			}//for

			Messagebox.show("Test mail will be sent in a moment.", "Information", 
					Messagebox.OK, Messagebox.INFORMATION);

			return true;
		} catch (Exception e) {
			logger.error("Exception : ", e);
			return true;
		}

	}
	protected String getSpamResult(String htmlStuff) {

		if(htmlStuff == null || htmlStuff.isEmpty()) return null;

		String sub = "My Template";
		String emlFilePath = PropertyUtil.getPropertyValue("usersParentDirectory") + 
				"/" + userName + "/message.eml";
		if(logger.isDebugEnabled())logger.debug("Eml file Path : " + emlFilePath);

		StringBuffer response = (new SpamChecker()).checkSpam(sub, htmlStuff, emlFilePath, true); 
		return ( response==null?null:response.toString() );
	}
	public boolean validateEmailAddr(String emailId) {
		try {
			boolean isValid = true;			
			if(emailId != null && emailId.trim().length() > 0) {

				if(logger.isDebugEnabled())logger.debug("Sending the test mail....");

				MessageUtil.clearMessage();
				testWinId$msgLblId.setValue("");
				String[] emailArr = null;
				if(isValid) {

					emailArr = emailId.split(",");
					for (String email : emailArr) {

						if(!Utility.validateEmail(email.trim())){
							testWinId$msgLblId.setValue("Invalid Email address:'"+email+"'");
							isValid = false;
							break;
						}
						else {
							//Testing invalid emaildomains -APP-308
							String result = PurgeList.checkForValidDomainByEmailId(email);
							if(!result.equalsIgnoreCase("ACtive")) {
								testWinId$msgLblId.setValue("Invalid Email address:'"+email+"'");
								isValid = false;
								break;
							}
						}

					}//for
				}

			}else {
				isValid = false;
				testWinId$msgLblId.setValue("Invalid Email address");
				testWinId$msgLblId.setVisible(true);
			}
			return isValid;
		}
		catch(Exception e) {
			logger.error("** Exception : " , e);
			return false;
		}

	}



	
public void onClick$viewAllMyTempImgId(){
		

		try {
			
			Components.removeAllChildren(templListDivId);
			for (Listitem item : myTemplatesListId.getItems()) {
				
				//displayTemplates(item, false, PropertyUtil.getPropertyValue(USER_TEMPLATES_DIRECTORY) );
				displayTemplates(item, false, PropertyUtil.getPropertyValue(USER_TEMPLATES_DIRECTORY), 0, 0);
				
			}
			
			
		
		} catch (Exception e) {
			logger.error("Error occured from the getAllImages method ",e);
		}
	
	}
private boolean sendTestMail(String emailId) {

	try {
		boolean isValid = true;			
		if(emailId != null && emailId.trim().length() > 0) {

			if(logger.isDebugEnabled())logger.debug("Sending the test mail....");

			MessageUtil.clearMessage();
			testWinId$msgLblId.setValue("");
			String[] emailArr = null;
			if(isValid) {

				emailArr = emailId.split(",");
				for (String email : emailArr) {

					if(!Utility.validateEmail(email.trim())){
						testTempWinId$msgLblId.setValue("Invalid Email address:'"+email+"'");
						isValid = false;
						break;
					}


				}//for
			}

			if(isValid) {
				for (String email : emailArr) {

					Utility.sendInstantMail(null, cSubTb.getValue(), myTempltCkEditorId.getValue(),
							Constants.EQ_TYPE_TEST_OPTIN_MAIL, email, null );

				}//for
				testTempWinId.setVisible(false);
				MessageUtil.setMessage("Test email will be sent in a moment.", "color:blue", "TOP");

			}		

		}else {
			isValid = false;
			testTempWinId$msgLblId.setValue("Invalid Email address");
			testTempWinId$msgLblId.setVisible(true);
		}
		return isValid;
	}
	catch(Exception e) {
		logger.error("** Exception : " , e);
		return false;
	}

}
protected boolean checkSpam(String htmlStuff) {
	logger.debug("-- Just Entered --");

	boolean result = false;

	String response = getSpamResult(htmlStuff); 
	logger.debug("Spam Report : \n" + response );

	if(response==null) {
		return true;
	}

	try {
		if(response.indexOf("(")<0) {
			logger.debug("Problem while getting spam report");
			return result;
		}
		String output = response.substring(response.indexOf("Content analysis details"));
		String scoreStr = output.substring(output.indexOf("(")+1,output.indexOf(")"));
		String[] scores = scoreStr.split(" ");
		for (String token : scores) {
			logger.debug("Token : " + token);

		}

		float hit = Float.parseFloat(scores[0]);
		if(0 <= hit && hit <= 2) {
			result = true;
		}
		else if(2 < hit && hit <= 4) {
			int confirm = Messagebox.show("Email has spam content. Do you want to continue saving the email?\n" +
					"Click on \"Check for Spam score\" for more details.",
					"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			result = (confirm == 1);
		}
		else if(hit > 4) {
			MessageUtil.setMessage("Email has SPAM content. Click on \"Check for Spam score\" for more details.", "color:red", "TOP");
			result = false;
		}

		logger.debug(" Result : "+ result);
	} catch (NumberFormatException e) {
		logger.debug("NumberFormatException : ",(Throwable)e);
	}catch (IndexOutOfBoundsException e) {
		logger.debug("IndexOutOfBoundsException : ",(Throwable)e);
	} catch (Exception e) {
		logger.debug("Exception : ",(Throwable)e);
	}
	return result;
}

	
public boolean validateTempContent() {

	String ckEditorContent = myTempltCkEditorId.getValue();
	boolean isValid = true;
	if (ckEditorContent == null || ckEditorContent.length() < 1) {
		MessageUtil.setMessage("Please provide content to save.", "red","top");
		isValid = false;
	}

	String isValidPhStr = Utility.validatePh(ckEditorContent, currentUser);

	if(isValid) {

		if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){

			MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
			isValid = false;
		}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){

			MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
			isValid = false;

		}

	}
	if(isValid) {
		String isValidCouponAndBarcode = null;
		isValidCouponAndBarcode = Utility.validateCCPh(ckEditorContent, currentUser);
		if(isValidCouponAndBarcode != null){
			isValid = false;
		}
	}
	return isValid;
}

public void onClick$sendTestMailBtnId$testTempWinId() {
	String emailId = testTempWinId$testTempTbId.getValue();

	boolean isValid = sendTestMail(emailId);
	if(isValid){
		testTempWinId$testTempTbId.setValue("");
	}

}
public void onClick$cancelSendTestMailBtnId$testTempWinId() {

	testTempWinId$msgLblId.setValue("");
	testTempWinId$msgLblId.setValue("");
	testTempWinId.setVisible(false);

}
	
	public void onClick$viewAllImgId(){
		

		try {
			
			Components.removeAllChildren(templListDivId);
			for (Listitem item : newEditorTemplatesListId.getItems()) {
				
				//displayTemplates(item, false, PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY) );
				displayTemplates(item, false, PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY), 0, 0);
				
			}
			
			
		
		} catch (Exception e) {
			logger.error("Error occured from the getAllImages method ",e);
		}
	
	}
	
	public void onClick$createTemplateTBarBtnId(){
		
		sessionScope.removeAttribute("isTemplateEdit");
		sessionScope.removeAttribute("Template");
		
		Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_BEE_EDITOR);
	}
	
	
	public void onClick$okBtnId$newMyTemplatesSubWinId() {
		
		MessageUtil.clearMessage();
		try {
			String folderNameAttr = (String)newMyTemplatesSubWinId$okBtnId.getAttribute("folderName");
			String parentDir = (String)newMyTemplatesSubWinId$okBtnId.getAttribute("parentDir");
			String folderName = newMyTemplatesSubWinId$newFolderTbId.getValue().trim();
			String parentFolder = parentDir.contains(Constants.NEWEDITOR_TEMPLATES_PARENT) ? Constants.NEWEDITOR_TEMPLATES_PARENT : Constants.MYTEMPATES_PARENT;
			String folderPath = usersParentDirectory+File.separator+userName+parentDir+File.separator;
			if(folderName.trim().isEmpty() || folderName == null) {
				newMyTemplatesSubWinId$folderErrorMsgLblId.setValue("Please provide the Folder Name");
				//MessageUtil.setMessage("Provide name for the new folder.", "color:red", "TOP");
				return;
			}
			else if(!Utility.validateName(folderName)){
				newMyTemplatesSubWinId$folderErrorMsgLblId.setValue("Provide a valid name with out any special characters");
				//MessageUtil.setMessage("Provide a valid name without any special characters.", "color:red", "TOP");
				return;
			}
			logger.debug("exist folder name"+folderNameAttr+"::replace with:"+folderName+" path "+folderPath);
			//replace (_) instead of whitespace
			folderName = folderName.replaceAll(" +", "_");
			
			File myfolder = new File(folderPath);
			File[] fileArr = myfolder.listFiles();
			for (File file : fileArr) {
				
				if(file.getName().equalsIgnoreCase(folderName)) {
					newMyTemplatesSubWinId$folderErrorMsgLblId.setValue("Provide another name For folder.");
					//MessageUtil.setMessage("Provide a valid name without any special characters.", "color:red", "TOP");
					return;
					
					
				}
				
				
			}
			
			File newFile = new File(folderPath+folderName);
			//logger.info("new file name is"+newFile.getName());
			String msgStr="";
			if(!newFile.exists()){
				try{
//					logger.info("File existed and make di"+newFile.mkdir());
					int size = 0;
					
					if(folderNameAttr != null) {
						
						File changeFile = new File(folderPath+folderNameAttr);
						FileUtils.moveDirectory(changeFile, newFile);
						
							size = newFile.listFiles().length; 
							FileUtils.deleteDirectory(changeFile);
						
						//myTemplatesDao.updateFolderName(userId, folderName, folderNameAttr);
						myTemplatesDaoForDML.updateFolderName(userId, folderName, folderNameAttr, parentFolder);
						Listitem newItem = newEditorTemplatesListId.getSelectedItem();
						((Label)((Listcell)newItem.getFirstChild()).getFirstChild()).setValue(folderName+"("+size+")");
						newItem.setAttribute("folderName", folderName);
						
						msgStr =" updated";
					}
					else {
						
						 msgStr =" created";
						newFile.mkdir();
						Listitem newItem = new Listitem();
						Listcell cell = new Listcell();
						Label label = new Label(folderName+"("+size+")");
						label.setStyle("width: 90%; display: inline-block;");
						
						cell.appendChild(label);
						cell.setParent(newItem);
						newItem.setValue(new ArrayList<MyTemplates>());
						newItem.setParent(newEditorTemplatesListId);
						
					}
					//(new Listitem(folderName+"("+size+")", new ArrayList<MyTemplates>())).setParent(myTemplatesListId);
					
					newMyTemplatesSubWinId$okBtnId.removeAttribute("folderName");
					
					
					newMyTemplatesSubWinId.setVisible(false);
					//folderOpnDivId.setVisible(true);
					
					
					
					MessageUtil.setMessage("New folder is" +msgStr+"  with the name : '" + folderName + "'", "color:blue", "TOP");
					
					/*if(userActivitiesDao != null) {
	            		userActivitiesDao.addToActivityList(ActivityEnum.GALLRY_CRE_FOLDER_p1folderName, GetUser.getUserObj(),folderName);
					}*/
					if(userActivitiesDaoForDML != null) {
	            		userActivitiesDaoForDML.addToActivityList(ActivityEnum.GALLRY_CRE_FOLDER_p1folderName, GetUser.getLoginUserObj(),folderName);
					}
				}catch (Exception e) {
					newMyTemplatesSubWinId$folderErrorMsgLblId.setValue("Problem in creating the new folder");
					logger.error("Exception ::" , e);;
					return;
//					MessageUtil.setMessage("Problem experienced while creating the new folder.", "color:red", "TOP");
				}
			}else{
				
				//if()
			
				newMyTemplatesSubWinId$folderErrorMsgLblId.setValue("Folder name already exist");
				return;
//				MessageUtil.setMessage("Folder name already exists.", "color:red", "TOP");
			}
		} catch (Exception e) {
			logger.error("problem occured from the createNewFolder method ",e);
		}
		
		
		
		
		} // onClick$okBtnId$newGallerySubWinId
	
	private Div newMyTemplatesSubWinId$createFolderDivId;
	private Button newMyTemplatesSubWinId$okBtnId; 
	
	public void onClick$folderImgId() {
		
		makeDisplayWindow(true, null, PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY));
		
		
	}
	
	
	public void makeDisplayWindow(boolean isNew, String folderName, String parentDir) {
		
		if(isNew) {
			
			newMyTemplatesSubWinId.setTitle("Folder Creation");
			newMyTemplatesSubWinId$okBtnId.setLabel("Add Folder");
			
		}else {
			logger.info("Change Folder Name");
			newMyTemplatesSubWinId.setTitle("Modify Folder");
			newMyTemplatesSubWinId$okBtnId.setLabel("Change Folder Name");
			newMyTemplatesSubWinId$okBtnId.setAttribute("folderName", folderName);
		}
		newMyTemplatesSubWinId$newFolderTbId.setValue("");
		newMyTemplatesSubWinId$folderErrorMsgLblId.setValue(""); //clear the errormsg from newGallerySubWinId  if any
		//newMyTemplatesSubWinId$okBtnId.setAttribute("folderName", (String)myTemplatesListId.getSelectedItem().getAttribute("folderName"));
		newMyTemplatesSubWinId$okBtnId.setAttribute("parentDir",parentDir);
		
		newMyTemplatesSubWinId$createFolderDivId.setVisible(true);
		newMyTemplatesSubWinId.doHighlighted();
		
		
		
	}
	private Window viewAllFolderWinId;
	private Label viewAllFolderWinId$folderNameLblId,viewAllFolderWinId$tempCountLblId,viewAllFolderWinId$lastAccLblId;
	/*public void getAllFolderView(String folderName,long userId) {
		
		
		
		try {
			Components.removeAllChildren(viewAllFolderWinId$viewGrpId);
			 String folderStr = usersParentDirectory+
					File.separator+ userName+PropertyUtil.getPropertyValue(USER_TEMPLATES_DIRECTORY);
			File categoryStr = new File(folderStr);
			 File[]  templateList = categoryStr.listFiles();
			 viewAllFolderWinId.setVisible(true);
			 viewAllFolderWinId.doHighlighted();
			// viewAllFolderWinId.setPosition("center");
			
			 if(templateList != null && templateList.length > 0) {
					logger.debug("Log size : "+ templateList.length);
					 
					int totSize = myTemplatesDao.findTotDefaultMyTemplates(userId,folderName);
					logger.info("folder name is"+folderName);
					 for (File tempFile : templateList) {
						 logger.debug("name is"+tempFile.getName());
						if(folderName.equals(tempFile.getName())){
							viewAllFolderWinId$folderNameLblId.setValue(folderName);
							viewAllFolderWinId$tempCountLblId.setValue(""+totSize);
							 
							 long mls = tempFile.lastModified();
							 Date date = new Date();
							 date.setTime(mls);
							 Calendar cal= Calendar.getInstance();
							 cal.setTime(date);
							 
							 viewAllFolderWinId$lastAccLblId.setValue(MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR));
							 logger.info("date is"+MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR));
						}
					}
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}*/
		/*
		try {
			Components.removeAllChildren(viewAllFolderWinId$viewAllFolderRowsId);
			 String folderStr = usersParentDirectory+
					File.separator+ userName+PropertyUtil.getPropertyValue(USER_TEMPLATES_DIRECTORY);
			File categoryStr = new File(folderStr);
			 File[]  templateList = categoryStr.listFiles();
			 viewAllFolderWinId.setVisible(true);
			 viewAllFolderWinId.doHighlighted();
			 viewAllFolderWinId.setPosition("center");
			if(templateList != null && templateList.length > 0) {
				logger.debug("Log size : "+ templateList.length);
				 
				int totSize = myTemplatesDao.findTotDefaultMyTemplates(userId,folderName);
				logger.info("folder name is"+folderName);
				
				 for (File tempFile : templateList) {
					 logger.debug("name is"+tempFile.getName());
					if(folderName.equals(tempFile.getName())){
						 Row row = new Row();
						
						 Label lbl = new Label(folderName);
						 lbl.setParent(row);
						 
						 long mls = tempFile.lastModified();
						 Date date = new Date();
						 date.setTime(mls);
						 Calendar cal= Calendar.getInstance();
						 cal.setTime(date);
						 
						 lbl = new Label(MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR));
						 
						 lbl= new Label(""+totSize);
						
						 lbl.setParent(row);
						 row.setParent(viewAllFolderWinId$viewAllFolderRowsId);
					}
					 
					 
					
				}
				 
			 } 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	} */
	private Menuitem changeFolderMId,deleteFolderMId,viewFolderMId;
	public void onClick$changeFolderMId(){
		Listitem selLi = (Listitem)((Listcell)((Image)manageFolderMpId.getAttribute("target")).getParent()).getParent();
		String folderName = (String)selLi.getAttribute("folderName");
		String parentDir = (String)selLi.getAttribute("dirPath");
		logger.debug("parentDir==="+parentDir);
		makeDisplayWindow(false, folderName, parentDir);//need a parent folder
	}
	public void onClick$deleteFolderMId(){
		try {
		
	//String usersParentDirectory = usersParentDirectory;
		String userName = GetUser.getUserObj().getUserName();

			//String folderNameStr = (((Label)((Listcell)myTemplatesListId.getSelectedItem().getFirstChild()).getFirstChild()).getValue();
		//String folderNameStr =( (Label)((Listcell)((Listitem)manageFolderImg.getParent()).getFirstChild()).getFirstChild()).getValue();	
		Listitem selLi = (Listitem)((Listcell)((Image)manageFolderMpId.getAttribute("target")).getParent()).getParent();
		String folderNameStr = (String)selLi.getAttribute("folderName");
		logger.debug("folder name str"+folderNameStr);
			//folderNameStr = folderNameStr.substring(0, folderNameStr.indexOf("("));
		
			String dirPath = (String)selLi.getAttribute("dirPath");
			
			int confirm = Messagebox.show("Do you want to  delete the folder: '" +folderNameStr + "' permanently?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm == 1) {
				
				String delDirPath = usersParentDirectory+
				File.separator+ userName+dirPath+File.separator+folderNameStr.trim();
				
				/*String delDirPath = usersParentDirectory+
				File.separator+ userName+PropertyUtil.getPropertyValue(USER_TEMPLATES_DIRECTORY)+File.separator+folderNameStr.trim();
				*/logger.debug("delDirPath is"+delDirPath);
				
				//myTemplatesDao.deleteByFolderName(userId, folderNameStr.trim());
				myTemplatesDaoForDML.deleteByFolderName(userId, folderNameStr.trim(), (String)selLi.getAttribute("parentDir"));
				
				File delFile = new File(delDirPath);
				//delete directory 
				FileUtils.deleteDirectory(delFile);
				//delFile.delete();
				Listbox lb  = (Listbox)selLi.getParent();
				lb.removeChild(selLi);
			//	myTemplatesDao.deleteFolder(userId,folderNameStr);
				logger.debug(userName + " - " + folderNameStr + "Folder deleted successfully");
				MessageUtil.setMessage("Folder deleted successfully.", "color:blue", "TOP");
			
				Components.removeAllChildren(templListDivId);
				lb.setSelectedIndex(0);
				
				//displayTemplates(lb.getSelectedItem(), true, dirPath);
				displayTemplates(lb.getSelectedItem(), true, dirPath, 0, 0);
			 } // if 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
		
		
		}
	public void onClick$viewFolderMId(){
		try {
			Listitem selLi = (Listitem)((Listcell)((Image)manageFolderMpId.getAttribute("target")).getParent()).getParent();
			String folderName = (String)selLi.getAttribute("folderName");
			String parentDir = (String)selLi.getAttribute("dirPath");
			
			//String folderName = (String)myTemplatesListId.getSelectedItem().getAttribute("folderName");
			//if(folderName.equals(Constants.MYTEMPATES_FOLDERS_DEFAULT))
			String foldersize = ((Label)selLi.getFirstChild().getFirstChild()).getValue();
			foldersize = foldersize.substring(foldersize.indexOf("(")+1 ,foldersize.indexOf(")"));
			viewAllFolderWinId$folderNameLblId.setValue(folderName);
			viewAllFolderWinId$tempCountLblId.setValue(foldersize);
			String categoryPathStr = usersParentDirectory+
					File.separator+ userName+parentDir+File.separator+folderName;
			
			File tempFile = new File(categoryPathStr);
			 long mls = tempFile.lastModified();
			 Date date = new Date();
			 date.setTime(mls);
			 Calendar cal= Calendar.getInstance();
			 cal.setTime(date);
			 
			 viewAllFolderWinId$lastAccLblId.setValue(MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR));
			 logger.info("date is"+MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STYEAR));
				 
			 viewAllFolderWinId.doHighlighted();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}	
		
	}
	private Listbox winId$myTempListId;
	public void getMyTempCopy(Long userId, String editor){
		String userTemplatesDirectory = PropertyUtil.getPropertyValue(editor.equals(Constants.EDITOR_TYPE_BEE) ? USER_NEW_EDITOR_TEMPLATES_DIRECTORY : USER_TEMPLATES_DIRECTORY);
		try {
			Components.removeAllChildren(winId$myTempListId);
			//MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			
			File myTemp = null;
			File[] templateList = null;
			
			 String myTempPathStr = usersParentDirectory+
						File.separator+userName+userTemplatesDirectory+File.separator;
			
			 myTemp= new File(myTempPathStr);
			 templateList = myTemp.listFiles();
			 for (Object obj: templateList) {
					final File template = (File)obj;
					Listitem item = new Listitem();
					String folderName=template.getName();
					
					 item.setLabel(folderName);
					 
					item.setParent(winId$myTempListId);
					
					
			 	}
			 
			if(winId$myTempListId.getItemCount() > 0) winId$myTempListId.setSelectedIndex(0);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}

	}
	public void onClick$myTemplatesSubmtBtnId$winId(){
		
		try{
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			MyTemplates myTemplates=(MyTemplates)winId.getAttribute("TemplateCopy");
			Textbox nameTbId = (Textbox)winId.getFellowIfAny("templatNameTbId");
			if(myTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)){
				saveInMyTemplates(winId,nameTbId.getValue(),myTemplates.getContent(),myTemplates.getEditorType(),winId$myTempListId,myTemplates.getJsoncontent());
			}
			else
				saveInMyTemplates(winId,nameTbId.getValue(),myTemplates.getContent(),myTemplates.getEditorType(),winId$myTempListId);
		}catch (Exception e) {
			logger.error("** Exception :", e );
		}
	}
	
	public  void saveInMyTemplates(Window winId, String name, String content, String editorType, Listbox myTemplatesListId) {
			String foldernName="";
			Label resLbId = (Label)winId.getFellow("resLbId");
			int override = -1;
			name = name.trim();
			logger.debug("name"+name);
			try{
				if(content == null || content.isEmpty()) {
					
					MessageUtil.setMessage("Template content can not be empty.", "color:red;");
					return;
					
				}
				String isValidPhStr = null;
				isValidPhStr = Utility.validatePh(content, GetUser.getUserObj());
				
				if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
					
					MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
					return ;
				}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
					
					MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
					return ;
					
				}
				
				String isValidCCDim = null;
				isValidCCDim = Utility.validateCouponDimensions(content);
				if(isValidCCDim != null){
					return ;
				}
				String isValidCouponAndBarcode = null;
				isValidCouponAndBarcode = Utility.validateCCPh(content, GetUser.getUserObj());
				if(isValidCouponAndBarcode != null){
					return;
				}
				
				/*if(Utility.validateHtmlSize(content)) {
					MessageUtil.setMessage("HTML size cannot exceed 100kb. " +
							"Please remove some content.", "color:red", "TOP");
					return ;
				}*/
				
				long size = Utility.validateHtmlSize(content);
				if(size >100) {
					String msgcontent = OCConstants.HTML_VALIDATION;
					msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
					MessageUtil.setMessage(msgcontent, "color:Blue");
				}
				
				if(name == null || name.trim().length() == 0){
					//MessageUtil.setMessage("Please provide a name to save in My Templates folder.", "color:red", "TOP");
					resLbId.setValue("Please provide a name to save in my templates");
					resLbId.setVisible(true);
					return;
				}else if(!Utility.validateName(name)) {
					resLbId.setValue("Please provide a valid name without any special characters ");
					resLbId.setVisible(true);
					return;
				}
				else {
					resLbId.setVisible(false);
				}
				/**
				 * creates a MyTemplate object and saves in DB
				 */
				MyTemplates myTemplate = null;
				myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
				if(myTemplatesDao == null)
					myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
				foldernName= myTemplatesListId.getSelectedItem().getLabel();
				
				foldernName = foldernName.contains("(") ? foldernName.substring(0,foldernName.indexOf("(")) : foldernName;
				foldernName = foldernName.trim();
				myTemplate = myTemplatesDao.findByUserNameAndTempNameInFolder(GetUser.getUserId(),name,foldernName, Constants.MYTEMPATES_PARENT);
				if(myTemplate != null) {
					
					try {
						override = Messagebox.show("The name already exists. Do you want to replace it?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(override == 1 ) {
							myTemplate.setContent(content);

						}else {
							return;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::" , e);;
					}
				}
				else {
					Calendar cal = MyCalendar.getNewCalendar();
				  foldernName= myTemplatesListId.getSelectedItem().getLabel();
					myTemplate = new MyTemplates(name, content, cal, editorType, GetUser.getUserObj(),foldernName, Constants.MYTEMPATES_PARENT);
				}
				//myTemplatesDao.saveOrUpdate(myTemplate);
				myTemplatesDaoForDML.saveOrUpdate(myTemplate);
				
				/**
				 * creates a html file and saves in user/MyTemplate directory
				 */
				
				if(content.contains("href='")){
					content = content.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
					
				}
				if(content.contains("href=\"")){
					content = content.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
				}
				
				String myTemplateFilePathStr = 
					PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+GetUser.getUserObj().getUserName()+
					PropertyUtil.getPropertyValue(USER_TEMPLATES_DIRECTORY)+File.separator+foldernName+
					File.separator+name+File.separator+"email.html";
				try {
					File myTemplateFile = new File(myTemplateFilePathStr);
					File parentDir = myTemplateFile.getParentFile();
					
					if(override == 1) {
						FileUtils.deleteDirectory(parentDir);
					}
					
					parentDir.mkdirs();
					
					//TODO Have to copy image files if exists
					BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
					
					bw.write(content);
					bw.flush();
					bw.close();
					String msgStr = name +" saved in "+foldernName+" successfully.";
					if(override == 1) {
						msgStr = name +" updated successfully in "+foldernName;
					}
					MessageUtil.setMessage(msgStr, "color:blue", "TOP");
					
				/*	if(userActivitiesDao != null) {
		        		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SAVED_TO_MYTEMPLATE_p1campaignName, currentUser,campaign.getCampaignName());
					}*/
				} catch (IOException e) {
					logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
				}
				
				//TODO need to write a code for generating image file of html for preview purpose

				winId.setVisible(false);
				Redirect.goTo(PageListEnum.EMPTY);
				Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
			} catch(DataIntegrityViolationException die) {
				//MessageUtil.setMessage("Name already exists in My Templates.", "color:red", "TOP");
				resLbId.setValue("Template with given name already exist in the selected folder,Please provide another name");
				resLbId.setVisible(true);
				logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);
			} catch(Exception e) {
				logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
			}
		}
		

	
	// For Bee Editor
	
	public  void saveInMyTemplates(Window winId, String name, String content, String editorType, Listbox myTemplatesListId, String jsoncontent) {
		String foldernName="";
		Label resLbId = (Label)winId.getFellow("resLbId");
		int override = -1;
		//logger.debug("name"+name);
		name = name.trim();
		logger.debug("name"+name);
		try{
			if(content == null || content.isEmpty()) {
				
				MessageUtil.setMessage("Template content can not be empty.", "color:red;");
				return;
				
			}
			String isValidPhStr = null;
			isValidPhStr = Utility.validatePh(content, GetUser.getUserObj());
			
			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				return ;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				return ;
				
			}
			
			String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(content);
			if(isValidCCDim != null){
				return ;
			}
			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(content, GetUser.getUserObj());
			if(isValidCouponAndBarcode != null){
				return;
			}
			
			/*if(Utility.validateHtmlSize(content)) {
				MessageUtil.setMessage("HTML size cannot exceed 100kb. " +
						"Please remove some content.", "color:red", "TOP");
				return ;
			}*/
			
			long size = Utility.validateHtmlSize(content);
			if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				MessageUtil.setMessage(msgcontent, "color:Blue");
			}
			
			if(name == null || name.trim().length() == 0){
				//MessageUtil.setMessage("Please provide a name to save in My Templates folder.", "color:red", "TOP");
				resLbId.setValue("Please provide a name to save in my templates");
				resLbId.setVisible(true);
				return;
			}else if(!Utility.validateName(name)) {
				resLbId.setValue("Please provide a valid name without any special characters ");
				resLbId.setVisible(true);
				return;
			}
			else {
				resLbId.setVisible(false);
			}
			/**
			 * creates a MyTemplate object and saves in DB
			 */
			MyTemplates myTemplate = null;
			myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
			if(myTemplatesDao == null)
				myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			foldernName= myTemplatesListId.getSelectedItem().getLabel();
			
			foldernName = foldernName.contains("(") ? foldernName.substring(0,foldernName.indexOf("(")) : foldernName;
			foldernName = foldernName.trim();
			myTemplate = myTemplatesDao.findByUserNameAndTempNameInFolder(GetUser.getUserId(),name,foldernName, Constants.NEWEDITOR_TEMPLATES_PARENT);
			if(myTemplate != null) {
				
				try {
					override = Messagebox.show("The name already exists. Do you want to replace it?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(override == 1 ) {
						myTemplate.setContent(content);
						myTemplate.setJsoncontent(jsoncontent);

					}else {
						return;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);;
				}
			}
			else {
				Calendar cal = MyCalendar.getNewCalendar();
			  foldernName= myTemplatesListId.getSelectedItem().getLabel();
				//myTemplate = new MyTemplates(name, content, cal, editorType, GetUser.getUserObj(),foldernName, Constants.NEWEDITOR_TEMPLATES_PARENT);
			  myTemplate = new MyTemplates(name, content, cal, editorType, GetUser.getUserObj(), foldernName, jsoncontent, Constants.NEWEDITOR_TEMPLATES_PARENT);
			}
			//myTemplatesDao.saveOrUpdate(myTemplate);
			myTemplatesDaoForDML.saveOrUpdate(myTemplate);
			
			/**
			 * creates a html file and saves in user/MyTemplate directory
			 */
			
			if(content.contains("href='")){
				content = content.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
				
			}
			if(content.contains("href=\"")){
				content = content.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
			}
			
			String myTemplateFilePathStr = 
				PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+GetUser.getUserObj().getUserName()+
				PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY)+File.separator+foldernName+
				File.separator+name+File.separator+"email.html";
			try {
				File myTemplateFile = new File(myTemplateFilePathStr);
				File parentDir = myTemplateFile.getParentFile();
				
				if(override == 1) {
					FileUtils.deleteDirectory(parentDir);
				}
				
				parentDir.mkdirs();
				
				//TODO Have to copy image files if exists
				BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
				
				bw.write(content);
			//	bw.write(jsoncontent);
				bw.flush();
				bw.close();
				String msgStr = name +" saved in "+foldernName+" successfully.";
				if(override == 1) {
					msgStr = name +" updated successfully in "+foldernName;
				}
				MessageUtil.setMessage(msgStr, "color:blue", "TOP");
				
			/*	if(userActivitiesDao != null) {
	        		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SAVED_TO_MYTEMPLATE_p1campaignName, currentUser,campaign.getCampaignName());
				}*/
			} catch (IOException e) {
				logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
			}
			
			//TODO need to write a code for generating image file of html for preview purpose

			winId.setVisible(false);
			Redirect.goTo(PageListEnum.EMPTY);
			Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
		} catch(DataIntegrityViolationException die) {
			//MessageUtil.setMessage("Name already exists in My Templates.", "color:red", "TOP");
			resLbId.setValue("Template with given name already exist in the selected folder,Please provide another name");
			resLbId.setVisible(true);
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);
		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
		}
	}

	
	
	
}
