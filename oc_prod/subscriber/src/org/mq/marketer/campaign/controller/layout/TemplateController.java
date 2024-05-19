package org.mq.marketer.campaign.controller.layout;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.SystemTemplates;
import org.mq.marketer.campaign.beans.TemplateCategory;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.MyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.SystemTemplatesDao;
import org.mq.marketer.campaign.dao.TemplateCategoryDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.CampaignStepsEnum;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

@SuppressWarnings({ "unchecked", "serial" })
public class TemplateController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	//private TemplateCategoryDao templateCategoryDao = null;
	private Campaigns campaign = null;
	private String isEdit = null;
//	Include xcontents = (Include)Utility.getComponentById("xcontents");
	//private String userName ;
	//private Users user;
	private Users currentUser;
	private Session sessionScope;
	private CampaignScheduleDao campaignScheduleDao;
	
	//private Set<Long> userIdsSet = GetUser.getUsersSet();
	
	private Listbox categoryListLbId, systemTemplatesListId,newEditorTemplatesListId, mynewTemplatesListId;
	private Div templListDivId,iconDescriptionDivId;
	
	public TemplateController() {
		
		String style = "font-weight:bold;font-size:15px;color:#313031;" +
						"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Create Email (Step 3 of 6)","",style,true);
		
		sessionScope = Sessions.getCurrent();
		campaign = (Campaigns) sessionScope.getAttribute("campaign");
 		isEdit = (String)sessionScope.getAttribute("editCampaign");
		
		if(isEdit!=null){
			if(campaign == null){
				//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
			}
		}else{
			/*if(Utility.isSomethingWrong(campaign, CampaignStepsEnum.CampLayout.getPos())){
				MessageUtil.setMessage(OCConstants.CAMPAIGN_MSG, "color:blue;");
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
				
			}*/
		}
		currentUser = GetUser.getUserObj();
		//userName = user.getUserName();
		
		 UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		 UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		 /*if(userActivitiesDao != null) {
	      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_LAYOUT,GetUser.getUserObj());
		 }*/
		 if(userActivitiesDaoForDML != null) {
		      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_LAYOUT,GetUser.getLoginUserObj());
			 }
		 campaignScheduleDao = (CampaignScheduleDao)SpringUtil.getBean("campaignScheduleDao");
	}
	
	//public List getCategories(){
	public void doAfterCompose(org.zkoss.zk.ui.Component comp) throws Exception {

		try{
			
			super.doAfterCompose(comp);
			Long userId = GetUser.getUserObj().getUserId();
			String userName = GetUser.getUserName();
			String usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
			 String folderPathStr = usersParentDirectory+File.separator+ GetUser.getUserObj().getUserName()+PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator;
			 File folderFile = new File(folderPathStr);
			
			 
			 if(folderFile.isDirectory() && folderFile.listFiles().length == 0){
				 String dirName= Constants.MYTEMPATES_FOLDERS_DEFAULT;
				 File newDir= new File(folderPathStr+dirName);
				 if(!newDir.exists())
				 newDir.mkdir();
				 logger.info("entered in to default folders"+newDir.getName());
					String dirNameAuto=Constants.MYTEMPATES_FOLDERS_DRAFTS;
					 File autoDir=new File(folderPathStr+dirNameAuto);
					 if(!autoDir.exists())
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
			 
			 
			 //bee default folder structure
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
			 
			getMyTemplatesFromDb(userId);
			 //if(zipImport$myTemplatesListId.getItemCount() > 0) zipImport$myTemplatesListId.setSelectedIndex(0);
			 //manageFolderImg.addEventListener("onClick", this);
			 //onSelect$zipImport$myTemplatesListId();
			//display system templates
			
			//getSystemTemplatesFromDb();
			
			
			
			Utility.breadCrumbFrom(3);
	 		Campaigns tempCamp = (Campaigns)sessionScope.getAttribute("campaign");
	 		
	 		if(tempCamp!=null && tempCamp.getEditorType()!=null) {
	 			sessionScope.setAttribute( "editorType", tempCamp.getEditorType());
	 			logger.info("tempCamp.getEditorType()==="+tempCamp.getEditorType());
	 		}
			
			//get the total TemplateCategory Name from the DB
			//111getTemPlatesCategoryFromDB();
			
		}catch(Exception e){
			logger.error("** Exception :",e);
			//return tempCategoryList;
		}
	}

	private void getTemPlatesCategoryFromDB() {
		
		List<TemplateCategory> tempCategoryList;
		Components.removeAllChildren(templListDivId);
		Components.removeAllChildren(categoryListLbId);
		TemplateCategoryDao templateCategoryDao = (TemplateCategoryDao)SpringUtil.getBean("templateCategoryDao");
		tempCategoryList = templateCategoryDao.findCategories();
		Listitem li;
		if(tempCategoryList != null) {
			for (TemplateCategory templateCategory: tempCategoryList) {
				//added for BEE no need to show the generic template category here
				//if(templateCategory.getCategoryName().equalsIgnoreCase(Constants.TEMPLATE_CATEGORY_GENERIC)) continue;
				if(templateCategory.getCategoryName().equalsIgnoreCase(Constants.TEMPLATE_CATEGORY_MYTEMPLATES)) {
				li = new Listitem();
				
				li.setValue(templateCategory);
				li.setLabel(templateCategory.getCategoryName());
				li.setParent(categoryListLbId);
				}
			}
		}
	}// getTemPlatesCategoryFromDB
	/*private void getTemPlatesCategoryFromDBForDAndDEditor() {
		
		List<TemplateCategory> tempCategoryList;
		
		TemplateCategoryDao templateCategoryDao = (TemplateCategoryDao)SpringUtil.getBean("templateCategoryDao");
		tempCategoryList = templateCategoryDao.findCategories();
		Listitem li;
		if(tempCategoryList != null) {
			for (TemplateCategory templateCategory: tempCategoryList) {
				//added for BEE no need to show the generic template category here
				if(templateCategory.getCategoryName().equalsIgnoreCase(Constants.TEMPLATE_CATEGORY_GENERIC)) continue;
				li = new Listitem();
				
				li.setValue(templateCategory);
				li.setLabel(templateCategory.getCategoryName());
				li.setParent(categoryListLbId);
			}
		}
	}// getTemPlatesCategoryFromDB
*/	
	private void getTemPlatesCategoryFromDBForDAndDEditor() {
		
		List<TemplateCategory> tempCategoryList;
		Components.removeAllChildren(systemTemplatesListId);
		TemplateCategoryDao templateCategoryDao = (TemplateCategoryDao)SpringUtil.getBean("templateCategoryDao");
		tempCategoryList = templateCategoryDao.findNewEditorCategories();
		Listitem li;
		if(tempCategoryList != null) {
			
			for (TemplateCategory templateCategory: tempCategoryList) {
				if(!templateCategory.getCategoryName().equalsIgnoreCase(Constants.TEMPLATE_CATEGORY_GENERIC)) continue;
				li = new Listitem();
				
				li.setValue(templateCategory);
				li.setLabel(templateCategory.getCategoryName());
				li.setParent(systemTemplatesListId);
			}
			if(systemTemplatesListId.getItems().size() >0){
				systemTemplatesListId.setSelectedIndex(0);
				onSelect$systemTemplatesListId();
			}
			
			
			/*for (TemplateCategory templateCategory: tempCategoryList) {
				
				if(templateCategory.getCategoryName().equalsIgnoreCase(Constants.TEMPLATE_CATEGORY_GENERIC)) continue;
				li = new Listitem();
				
				li.setValue(templateCategory);
				li.setLabel(templateCategory.getCategoryName());
				li.setParent(newEditorTemplatesListId);
			}*/
			/*if(systemTemplatesListId.getItems().size() >0) systemTemplatesListId.setSelectedIndex(0);
			onSelect$newEditorTemplatesListId();*/
		}
		
		getNewEditorTemplatesFromDb(GetUser.getUserId());
	}// getNewTemPlatesCategoryFromDB
	public void getNewEditorTemplatesFromDb(long userId) {
		
		try {
			Components.removeAllChildren(newEditorTemplatesListId);
			File myTemp = null;
			File[] templateList = null;
			MyTemplatesDao myTemplatesDao = 
					(MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			String myTempPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
					File.separator+ GetUser.getUserName()+PropertyUtil.getPropertyValue("userNewEditorTemplatesDirectory")+File.separator;

			myTemp= new File(myTempPathStr);
			templateList = myTemp.listFiles();
			logger.debug("myTemp.listFiles().length"+myTemp.listFiles().length);
			List<File> fileList= new ArrayList<File>();
			for(File file:templateList) {

						
				logger.debug("------in if case----"+file.getName());
				String folderName=file.getName();						
				List<MyTemplates> tempList =myTemplatesDao.findDefaultMyTemplates(userId, folderName, Constants.NEWEDITOR_TEMPLATES_PARENT);
				//int totCount = myTemplatesDao.findTotDefaultMyTemplates(userId,folderName);
				Listitem item = new Listitem();//

				item.setValue(tempList);
				//venkat
				item.setLabel(folderName);
				item.setParent(newEditorTemplatesListId);
			}
			if(newEditorTemplatesListId.getItems().size() >0){
				//newEditorTemplatesListId.setSelectedIndex(0);
				//onSelect$newEditorTemplatesListId();
			}


			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
		
		
		
		
	
		
	}
	private Tabbox tabBoxId;
	private Tab GenericTempTId;
	public void onSelect$tabBoxId() {
		
		if(tabBoxId.getSelectedTab().getId().equals(GenericTempTId)) {
			
			systemTemplatesListId.setSelectedIndex(0);
			onSelect$systemTemplatesListId();
		}else{
			systemTemplatesListId.setSelectedIndex(-1);
			
		}
		
		
	}
	
	public void onSelect$newEditorTemplatesListId(){
		
		
		Components.removeAllChildren(templListDivId);
		String categoryPathStr ;
		File category = null;
		File[] templateList = null;
		List<File[]> templateFileList = new ArrayList<File[]>();
		String templatePrevStr = null;
		String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
		String preview_Path = null;
		String uName = null;
		templatesGBoxId.setVisible(true);
		iconDescriptionDivId.setVisible(true);
		String userTemplatesDirectory = PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY);
		
		String folderName = newEditorTemplatesListId.getSelectedItem().getLabel();
		templateFileList.clear();
		 String myTempPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
					File.separator+ currentUser.getUserName()+userTemplatesDirectory+File.separator+folderName;
		logger.debug("myTempPathStr ===>"+myTempPathStr);
		 category= new File(myTempPathStr);
		 templatePrevStr = "/UserData/"+currentUser.getUserName()+userTemplatesDirectory+File.separator+folderName;
		 templateList = category.listFiles();
		
		
		//myTempDivId.setVisible(true);
		//myTemplatesListId.setVisible(true);
		 preview_Path =  appUrl + "/UserData/"+currentUser.getUserName()+userTemplatesDirectory+File.separator+folderName;
		/*preview_Path =  appUrl +"UserData/"+ currentUser.getUserName()+File.separator+userTemplatesDirectory+
				File.separator+categoryName;*/
		//templatePrevStr = "/UserData/"+ currentUser.getUserName()+userTemplatesDirectory+categoryName;
		//getmyNewEditorTemplatesFromDb(currentUser.getUserId());
		/*category = new File(myTempPathStr);
		templateList = category.listFiles();*/
		
		Image templateImg = null;
		
		Toolbarbutton preview,edit,deleteTb;
		Label templateName;
		Hbox hbox;
		String preview_Src ="";
		final String useIconPathStr = "/img/theme/use_icon.png";
		String previewIconPathStr = "/img/theme/preview_icon.png";
		final String deleteIconPathStr = "/img/theme/action_delete.gif";
		String vbStyle = "templatePrviewVb";
		String custom_temp_default_preview = "/img/custom_temp_preview.gif";
		

		
		try {
			Listitem selLi = newEditorTemplatesListId.getSelectedItem();
			List<MyTemplates> myTemplates = (List<MyTemplates>)selLi.getValue();
			
			for (MyTemplates mytemplate : myTemplates) {
				

				boolean isFound = false;
			for(File templateLst:templateList) {
				
			
					final File template = templateLst;
					
					if(!mytemplate.getName().equals(template.getName())) {
						logger.info("myTemplate ::"+mytemplate.getName()+" template.getName() "+template.getName());
						continue;
					}
					
					isFound = true;
					if(template.isDirectory()){
						
						logger.debug(template.getPath());
						final Vbox vbox = new Vbox();
						vbox.setSclass(vbStyle);
						vbox.setAlign("center");
						
						
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
						
						// preview using javascript
						preview.setWidgetListener("onClick", "previewByUrl('"+preview_Src+"')");
						//preview.setAction("onclick:previewByUrl('"+preview_Src+"')");
						
						edit = new Toolbarbutton();
						edit.setImage(useIconPathStr);
						edit.setTooltiptext("Use this layout");
						edit.setAttribute("MyTemplate", mytemplate);
						edit.setAttribute("parentDir", Constants.NEWEDITOR_TEMPLATES_PARENT);
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
						
						hbox.setWidth("190px");
						hbox.setPack("center");
	//				hbox.setWidths("30px 100px 30px 30px");
						deleteTb = new Toolbarbutton();
						deleteTb.setImage(deleteIconPathStr);
						deleteTb.setTooltiptext("Delete this from my templates");
						
						/**
						 * add onClick event listener to delete when clicked
						 */
						deleteTb.addEventListener("onClick", new EventListener() {
							
							@Override
							public void onEvent(Event event) throws Exception {//how we can allow the othr user's template deletion
								try {
									int confirm = Messagebox.show("Are you sure you want to delete the template - "
											+ template.getName() + "?",
											"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
									
									if(confirm != 1){
										return;
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Exception ::" , e);;
								}
								
								template.delete();
								templListDivId.removeChild(vbox);
								/*MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
								myTemplatesDao.deleteByUserIdAndName(currentUser.getUserId(),template.getName());*/
								MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
								myTemplatesDaoForDML.deleteByUserIdAndName(currentUser.getUserId(),template.getName(), Constants.NEWEDITOR_TEMPLATES_PARENT);
								FileUtils.deleteDirectory(template);
								
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
			logger.info("folder"+selLi);
			//manageFolderImg.setParent(null);
			
			//displayTemplates(selLi, true, USER_NEW_EDITOR_TEMPLATES_DIRECTORY);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	
		
	}
	private final String  USER_NEW_EDITOR_TEMPLATES_DIRECTORY= "userNewEditorTemplatesDirectory"; 
	public void onSelect$systemTemplatesListId(){

		try{
			logger.debug(" -- just entered --");
			
			TemplateCategory templateCategory = (TemplateCategory)systemTemplatesListId.getSelectedItem().getValue();
			final String categoryName = templateCategory.getDirName();
			Components.removeAllChildren(templListDivId);
			String categoryPathStr ;
			File category = null;
			File[] templateList = null;
			List<File[]> templateFileList = new ArrayList<File[]>();
			String templatePrevStr = null;
			String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
			String preview_Path = null;
			String uName = null;
			templatesGBoxId.setVisible(true);
			iconDescriptionDivId.setVisible(true);
			//String userTemplatesDirectory = PropertyUtil.getPropertyValue(USER_NEW_EDITOR_TEMPLATES_DIRECTORY);
			/**
			 * if selected option is MyTemplates displays the user specific templates if any,
			 * else displays the system templates.
			 */
			logger.debug("selected category :"+categoryName);
			
			//TODO need to decide for multi user acc
			UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
			//List<Users> userList = usersDao.findAllByIds(userIdsSet);
			
			//this only for gtting all the domain user's templates
			
			List<Users> userList = new ArrayList<Users>();
			
			userList.add(GetUser.getUserObj());
			
			if(categoryName.equalsIgnoreCase(Constants.TEMPLATE_CATEGORY_GENERIC_DIR_NAME)) {
				myTempDivId.setVisible(false);
				myTemplatesListId.setVisible(false);
				templateFileList.clear();
				categoryPathStr = PropertyUtil.getPropertyValue("templateParent")+File.separator+categoryName;
				category = new File(categoryPathStr);
				templateList = category.listFiles();
				templateFileList.add(templateList);
				preview_Path =  appUrl + "SystemData/Templates/"+categoryName;
				templatePrevStr = "/SystemData/Templates/"+categoryName;
				
			}/*else if(categoryName.equalsIgnoreCase("t") || categoryName.equalsIgnoreCase("GenericTemplates") ) {
				
			}
			else {
				myTempDivId.setVisible(false);
				myTemplatesListId.setVisible(false);
				templateFileList.clear();
				categoryPathStr = PropertyUtil.getPropertyValue("templateParent")+
				File.separator+categoryName;
				category = new File(categoryPathStr);
				templateList = category.listFiles();
				templateFileList.add(templateList);
				preview_Path =  appUrl + "SystemData/Templates/"+categoryName;
				templatePrevStr = "/SystemData/Templates/"+categoryName;
				
			}*/
			logger.debug("--4--" + category.exists() + "  " + category.isDirectory());
			
			Image templateImg = null;
			
			
			Toolbarbutton preview,use,deleteTb;
			Label templateName;
			Hbox hbox;
			String preview_Src ="";
			final String useIconPathStr = "/img/theme/use_icon.png";
			String previewIconPathStr = "/img/theme/preview_icon.png";
			final String deleteIconPathStr = "/img/theme/action_delete.gif";
			String vbStyle = "templatePrviewVb";
			String custom_temp_default_preview = "/img/custom_temp_preview.gif";
			
			
			if( templateFileList.size() == 0 ) {
				logger.warn(">>>>>>>>>>>>>>>>>>>> Template list is empty/null");
			}
			
			// loop for generating the templates icons for selection along with preview and use buttons
			for(File[] templateLst:templateFileList) {
				
				for (Object obj: templateLst) {
					final File template = (File)obj;
					
					if(template.isDirectory()){
						
						//logger.debug(template.getPath());
						final Vbox vbox = new Vbox();
						vbox.setSclass(vbStyle);
						vbox.setAlign("center");
						
						/*if(categoryName.equalsIgnoreCase("MyTemplates")) {
							if(! new File(template.getPath()+File.separator+"preview.gif").exists()) {
								templateImg = new Image(custom_temp_default_preview);
							}
						}
						else{*/
							templateImg = new Image(templatePrevStr+"/"+template.getName()+"/preview.gif");
						//}
						
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
						
						// preview using javascript
						preview.setWidgetListener("onClick", "previewByUrl('"+preview_Src+"')");
						//preview.setAction("onclick:previewByUrl('"+preview_Src+"')");
						
						use = new Toolbarbutton();
						use.setImage(useIconPathStr);
						use.setTooltiptext("Use this layout");
						
						//declared as final to use in inner class
						final String selectedTemplateStr = categoryName+"/"+template.getName();
						
						/**
						 * add onClick event listener for loading this template to Editor
						 */
						use.addEventListener("onClick", new EventListener() {
							
							@Override
							public void onEvent(Event event) throws Exception {
								try{
									
									Calendar currentTimePlus3hours = getCurrentTimePlus3Huors();
									
									List<CampaignSchedule> campScheduleList = campaignScheduleDao.findAllByUpComingActiveSchedules(campaign.getCampaignId());
									if(campScheduleList != null && campScheduleList.size() > 0){
										long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0 && campaignSchedule.getScheduledDate().before(currentTimePlus3hours)).count(); 
										if(activeCount != 0) {
											//MessageUtil.setMessage("Template of a campaign with upcoming schedules cannot be changed.Please delete all active schedules first.", "color:red");
											//return;
											if(	Messagebox.show(" This campaign has upcoming schedule/s. Do you want to continue to change the template? ", 
													"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES ) {
												return;
											}
										}
									}
									Toolbarbutton tb = (Toolbarbutton)event.getTarget();
									String targetImage = tb.getImage();
									if(targetImage != null) {
										logger.info("campaign...."+campaign.getEditorType()+"selectedTemplateStr...."+selectedTemplateStr);
										if(campaign.getHtmlText()!=null){
											
											if(	Messagebox.show(" This campaign's current email template (text and images) will be deleted and overwritten \n with the selected template. Do you want to continue?", 
													"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES ) {
												return;
											}
										}
										sessionScope.removeAttribute("isEdit");
										//sessionScope.removeAttribute("editCampaign");
										sessionScope.removeAttribute("isTextMsgBack");
										sessionScope.setAttribute("selectedTemplate", selectedTemplateStr);
										
										/*if(categoryName.equalsIgnoreCase("MyTemplates")) {//here it may not means the current user 
											MyTemplatesDao myTemplatesDao = 
												(MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
											String editorType = myTemplatesDao.getEditorTypeByNameAndUser(currentUser.getUserId(), template.getName());
											//String editorType = (String)sessionScope.getAttribute("editorType");
											if(editorType.equalsIgnoreCase("blockEditor")) {
												
												campaign.setHtmlText(null);
												campaign.setTextMessage(null);
												sessionScope.setAttribute("campaign", campaign);
												
												sessionScope.setAttribute( "editorType", editorType);
												Redirect.goTo(PageListEnum.CAMPAIGN_BLOCK_EDITOR);
											}
											else if(editorType.equalsIgnoreCase("plainTextEditor")){
												sessionScope.setAttribute( "editorType", editorType);
												Redirect.goTo(PageListEnum.CAMPAIGN_PLAIN_EDITOR);
											}
											else if(editorType.equalsIgnoreCase("plainHtmlEditor")){
												sessionScope.setAttribute( "editorType", editorType);
												Redirect.goTo(PageListEnum.CAMPAIGN_HTML_EDITOR);
											}
											
										} 
										else {*/
											
											campaign.setHtmlText(null);
											campaign.setTextMessage(null);
											
											SystemTemplatesDao systemTemplatesDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
											//String[] temp = StringUtils.split(selectedTemplateStr, '/');
											SystemTemplates valuesOfSystemTemplates=systemTemplatesDao.findByNameAndCategory(template.getName(), categoryName);
											
											if(valuesOfSystemTemplates!=null && valuesOfSystemTemplates.getJsonText()!=null){
												sessionScope.setAttribute("campaign", campaign);
												sessionScope.setAttribute( "editorType", "beeEditor");
												Redirect.goTo(PageListEnum.CAMPAIGN_HTML_BEE_EDITOR);
											}
											else{
											sessionScope.setAttribute("campaign", campaign);
											sessionScope.setAttribute( "editorType", "blockEditor");
											Redirect.goTo(PageListEnum.CAMPAIGN_BLOCK_EDITOR);
											}
										}
									//}
									
								}catch (Exception e) {
									logger.error("** Exception :", (Throwable)e);
								}
							}
							
						});
						
						templateName = new Label(template.getName());
						templateName.setMaxlength(30);
						hbox = new Hbox();
						hbox.setWidth("160px");
						hbox.setPack("center");
						//hbox.setAlign("center");
						hbox.setSclass("st_hb");
						
						//templateName.setParent(hbox);
						preview.setParent(hbox);
						use.setParent(hbox);
						
						if(categoryName.equalsIgnoreCase("MyTemplates")) {
							hbox.setWidth("190px");
							hbox.setPack("center");
//						hbox.setWidths("30px 100px 30px 30px");
							deleteTb = new Toolbarbutton();
							deleteTb.setImage(deleteIconPathStr);
							deleteTb.setTooltiptext("Delete this from my templates");
							
							/**
							 * add onClick event listener to delete when clicked
							 */
							deleteTb.addEventListener("onClick", new EventListener() {
								
								@Override
								public void onEvent(Event event) throws Exception {//how we can allow the othr user's template deletion
									try {
										int confirm = Messagebox.show("Are you sure you want to delete the template - "
												+ template.getName() + "?",
												"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
										
										if(confirm != 1){
											return;
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										logger.error("Exception ::" , e);;
									}
									
									template.delete();
									templListDivId.removeChild(vbox);
									/*MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
									myTemplatesDao.deleteByUserIdAndName(currentUser.getUserId(),template.getName());*/
									MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
									myTemplatesDaoForDML.deleteByUserIdAndName(currentUser.getUserId(),template.getName(), Constants.NEWEDITOR_TEMPLATES_PARENT);
									FileUtils.deleteDirectory(template);
									
								}
								
							});
							deleteTb.setParent(hbox);
							
						}
						
						templateImg.setParent(vbox);
						templateName.setParent(vbox);
						hbox.setParent(vbox);
						vbox.setParent(templListDivId);
					}
				}// for
					
					
					
			}
	
		}catch(Exception e){
			logger.error("** Exception :", (Throwable)e);
		}
	
		
	}
	
	public Calendar getCurrentTimePlus3Huors() {
		Calendar currentTimePlus3hours = Calendar.getInstance();
		currentTimePlus3hours.add(Calendar.HOUR, +3);
		return currentTimePlus3hours;
	}
	
	private Div myTempDivId;
	private Listbox myTemplatesListId;
	/**
	 * Gets the templates previews of selected category
	 * @param categoryListLbId
	 * @param templListDivId
	 */
	public void onSelect$categoryListLbId() {
		try{
			logger.debug(" -- just entered --");
			List<CampaignSchedule> campScheduleList = campaignScheduleDao.findAllByUpComingActiveSchedules(campaign.getCampaignId());
			if(campScheduleList != null && campScheduleList.size() > 0){
				Calendar currentTimePlus3hours = getCurrentTimePlus3Huors();
				long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0 && campaignSchedule.getScheduledDate().before(currentTimePlus3hours)).count(); 
				if(activeCount != 0) {
					//MessageUtil.setMessage("Template of a campaign with upcoming schedules cannot be changed.Please delete all active schedules first.", "color:red");
					//return;
					if(	Messagebox.show(" This campaign has upcoming schedule/s. Do you want to continue to change the template? ", 
							"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES ) {
						return;
					}
				}
			}
			TemplateCategory templateCategory = (TemplateCategory)
			categoryListLbId.getSelectedItem().getValue();
			final String categoryName = templateCategory.getDirName();
			Components.removeAllChildren(templListDivId);
			String categoryPathStr ;
			File category = null;
			File[] templateList = null;
			List<File[]> templateFileList = new ArrayList<File[]>();
			String templatePrevStr = null;
			String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
			String preview_Path = null;
			String uName = null;
			templatesGBoxId.setVisible(true);
			iconDescriptionDivId.setVisible(true);
			String userTemplatesDirectory = PropertyUtil.getPropertyValue("userTemplatesDirectory");
			/**
			 * if selected option is MyTemplates displays the user specific templates if any,
			 * else displays the system templates.
			 */
			logger.debug("selected category :"+categoryName);
			
			//TODO need to decide for multi user acc
			UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
			//List<Users> userList = usersDao.findAllByIds(userIdsSet);
			
			//this only for gtting all the domain user's templates
			
			List<Users> userList = new ArrayList<Users>();
			
			userList.add(GetUser.getUserObj());
			
			if(categoryName.equalsIgnoreCase("MyTemplates")) {
				
				templateFileList.clear();
				 String myTempPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
							File.separator+ currentUser.getUserName()+userTemplatesDirectory;
				
				 category= new File(myTempPathStr);
				 templateList = category.listFiles();
				
				
				myTempDivId.setVisible(true);
				myTemplatesListId.setVisible(true);
				/*preview_Path =  appUrl +"UserData/"+ currentUser.getUserName()+File.separator+userTemplatesDirectory+
						File.separator+categoryName;*/
				templatePrevStr = "/UserData/"+ currentUser.getUserName()+userTemplatesDirectory+categoryName;
				getMyTemplatesFromDb(currentUser.getUserId());
				/*category = new File(myTempPathStr);
				templateList = category.listFiles();*/
				onSelect$myTemplatesListId();
				
			}else if(categoryName.equalsIgnoreCase("t") || categoryName.equalsIgnoreCase("GenericTemplates") ) {
				
				
				
			}
			else {
				myTempDivId.setVisible(false);
				myTemplatesListId.setVisible(false);
				templateFileList.clear();
				categoryPathStr = PropertyUtil.getPropertyValue("templateParent")+
				File.separator+categoryName;
				category = new File(categoryPathStr);
				templateList = category.listFiles();
				templateFileList.add(templateList);
				preview_Path =  appUrl + "SystemData/Templates/"+categoryName;
				templatePrevStr = "/SystemData/Templates/"+categoryName;
				
			}
			logger.debug("--4--" + category.exists() + "  " + category.isDirectory());
			
			Image templateImg = null;
			
			
			Toolbarbutton preview,use,deleteTb;
			Label templateName;
			Hbox hbox;
			String preview_Src ="";
			final String useIconPathStr = "/img/theme/use_icon.png";
			String previewIconPathStr = "/img/theme/preview_icon.png";
			final String deleteIconPathStr = "/img/theme/action_delete.gif";
			String vbStyle = "templatePrviewVb";
			String custom_temp_default_preview = "/img/custom_temp_preview.gif";
			
			
			if( templateFileList.size() == 0 ) {
				logger.warn(">>>>>>>>>>>>>>>>>>>> Template list is empty/null");
			}
			
			// loop for generating the templates icons for selection along with preview and use buttons
			for(File[] templateLst:templateFileList) {
				
				for (Object obj: templateLst) {
					final File template = (File)obj;
					
					if(template.isDirectory()){
						
						//logger.debug(template.getPath());
						final Vbox vbox = new Vbox();
						vbox.setSclass(vbStyle);
						vbox.setAlign("center");
						
						if(categoryName.equalsIgnoreCase("MyTemplates")) {
							if(! new File(template.getPath()+File.separator+"preview.gif").exists()) {
								templateImg = new Image(custom_temp_default_preview);
							}
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
						
						// preview using javascript
						preview.setWidgetListener("onClick", "previewByUrl('"+preview_Src+"')");
						//preview.setAction("onclick:previewByUrl('"+preview_Src+"')");
						
						use = new Toolbarbutton();
						use.setImage(useIconPathStr);
						use.setTooltiptext("Use this layout");
						
						//declared as final to use in inner class
						final String selectedTemplateStr = categoryName+"/"+template.getName();
						
						/**
						 * add onClick event listener for loading this template to Editor
						 */
						use.addEventListener("onClick", new EventListener() {
							
							@Override
							public void onEvent(Event event) throws Exception {
								try{
									Calendar currentTimePlus3hours = getCurrentTimePlus3Huors();
									
									List<CampaignSchedule> campScheduleList = campaignScheduleDao.findAllByUpComingActiveSchedules(campaign.getCampaignId());
									if(campScheduleList != null && campScheduleList.size() > 0){
										long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0 && campaignSchedule.getScheduledDate().before(currentTimePlus3hours)).count(); 
										if(activeCount != 0) {
											//MessageUtil.setMessage("Template of a campaign with upcoming schedules cannot be changed.Please delete all active schedules first.", "color:red");
											//return;
											if(	Messagebox.show(" This campaign has upcoming schedule/s. Do you want to continue to change the template? ", 
													"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES ) {
												return;
											}
										}
									}
									Toolbarbutton tb = (Toolbarbutton)event.getTarget();
									String targetImage = tb.getImage();
									if(targetImage != null) {
										logger.info("campaign...."+campaign.getEditorType()+"selectedTemplateStr...."+selectedTemplateStr);
										if(campaign.getHtmlText()!=null){
											
											if(	Messagebox.show(" This campaign's current email template (text and images) will be deleted and overwritten \n with the selected template. Do you want to continue?", 
													"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES ) {
												return;
											}
										}
										sessionScope.removeAttribute("isEdit");
										//sessionScope.removeAttribute("editCampaign");
										sessionScope.removeAttribute("isTextMsgBack");
										sessionScope.setAttribute("selectedTemplate", selectedTemplateStr);
										
										/*if(categoryName.equalsIgnoreCase("MyTemplates")) {//here it may not means the current user 
											MyTemplatesDao myTemplatesDao = 
												(MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
											String editorType = myTemplatesDao.getEditorTypeByNameAndUser(currentUser.getUserId(), template.getName());
											//String editorType = (String)sessionScope.getAttribute("editorType");
											if(editorType.equalsIgnoreCase("blockEditor")) {
												
												campaign.setHtmlText(null);
												campaign.setTextMessage(null);
												sessionScope.setAttribute("campaign", campaign);
												
												sessionScope.setAttribute( "editorType", editorType);
												Redirect.goTo(PageListEnum.CAMPAIGN_BLOCK_EDITOR);
											}
											else if(editorType.equalsIgnoreCase("plainTextEditor")){
												sessionScope.setAttribute( "editorType", editorType);
												Redirect.goTo(PageListEnum.CAMPAIGN_PLAIN_EDITOR);
											}
											else if(editorType.equalsIgnoreCase("plainHtmlEditor")){
												sessionScope.setAttribute( "editorType", editorType);
												Redirect.goTo(PageListEnum.CAMPAIGN_HTML_EDITOR);
											}
											
										} 
										else {*/
											
											campaign.setHtmlText(null);
											campaign.setTextMessage(null);
											
											SystemTemplatesDao systemTemplatesDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
											//String[] temp = StringUtils.split(selectedTemplateStr, '/');
											SystemTemplates valuesOfSystemTemplates=systemTemplatesDao.findByNameAndCategory(template.getName(), categoryName);
											
											if(valuesOfSystemTemplates!=null && valuesOfSystemTemplates.getJsonText()!=null){
												sessionScope.setAttribute("campaign", campaign);
												sessionScope.setAttribute( "editorType", "beeEditor");
												Redirect.goTo(PageListEnum.CAMPAIGN_HTML_BEE_EDITOR);
											}
											else{
											sessionScope.setAttribute("campaign", campaign);
											
											sessionScope.setAttribute( "editorType", "blockEditor");
											Redirect.goTo(PageListEnum.CAMPAIGN_BLOCK_EDITOR);
											}
										}
									//}
									
								}catch (Exception e) {
									logger.error("** Exception :", (Throwable)e);
								}
							}
							
						});
						
						templateName = new Label(template.getName());
						templateName.setMaxlength(30);
						hbox = new Hbox();
						hbox.setWidth("160px");
						hbox.setPack("center");
						//hbox.setAlign("center");
						hbox.setSclass("st_hb");
						
						//templateName.setParent(hbox);
						preview.setParent(hbox);
						use.setParent(hbox);
						
						if(categoryName.equalsIgnoreCase("MyTemplates")) {
							hbox.setWidth("190px");
							hbox.setPack("center");
//						hbox.setWidths("30px 100px 30px 30px");
							deleteTb = new Toolbarbutton();
							deleteTb.setImage(deleteIconPathStr);
							deleteTb.setTooltiptext("Delete this from my templates");
							
							/**
							 * add onClick event listener to delete when clicked
							 */
							deleteTb.addEventListener("onClick", new EventListener() {
								
								@Override
								public void onEvent(Event event) throws Exception {//how we can allow the othr user's template deletion
									try {
										int confirm = Messagebox.show("Are you sure you want to delete the template - "
												+ template.getName() + "?",
												"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
										
										if(confirm != 1){
											return;
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										logger.error("Exception ::" , e);;
									}
									
									template.delete();
									templListDivId.removeChild(vbox);
									/*MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
									myTemplatesDao.deleteByUserIdAndName(currentUser.getUserId(),template.getName());*/
									MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
									myTemplatesDaoForDML.deleteByUserIdAndName(currentUser.getUserId(),template.getName(), Constants.MYTEMPATES_PARENT);
									FileUtils.deleteDirectory(template);
									
								}
								
							});
							deleteTb.setParent(hbox);
							
						}
						
						templateImg.setParent(vbox);
						templateName.setParent(vbox);
						hbox.setParent(vbox);
						vbox.setParent(templListDivId);
					}
				}// for
					
					
					
			}
	
		}catch(Exception e){
			logger.error("** Exception :", (Throwable)e);
		}
	} // onSelect$categoryListLbId


	
	public void onClick$templateTbId() {
		goTo(0);
	} //onClick$templateTbId
	
	
	public void onClick$plainEditorTbId() {
		try {
			List<CampaignSchedule> campScheduleList = campaignScheduleDao.findAllByUpComingActiveSchedules(campaign.getCampaignId());
			if(campScheduleList != null && campScheduleList.size() > 0){
				Calendar currentTimePlus3hours = getCurrentTimePlus3Huors();
				long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0 && campaignSchedule.getScheduledDate().before(currentTimePlus3hours)).count(); 
				if(activeCount != 0) {
					
					if(	Messagebox.show(" This campaign has upcoming schedule/s. Do you want to continue to change the template? ", 
							"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES ) {
						return;
					}
					
					//MessageUtil.setMessage("Template of a campaign with upcoming schedules cannot be changed.Please delete all active schedules first.", "color:red");
					//return;
				}
			}
			Campaigns campaign = (Campaigns)sessionScope.getAttribute("campaign");
			if(campaign!=null && campaign.getHtmlText() !=null) {
				
				int confirm = Messagebox.show("Do you want to retain earlier saved email content?", 
						"Confirm", Messagebox.CANCEL | Messagebox.YES | Messagebox.NO , Messagebox.QUESTION);
				
				
				if (confirm == Messagebox.CANCEL) {
					return;
				} 
				else if (confirm ==Messagebox.NO) {
					campaign.setHtmlText("");
					sessionScope.setAttribute("campaign", campaign);
				}
			}
			
			goTo(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	} //onClick$plainEditorTbId
	
	
	public void onClick$beeEditor() {
		try {
			
			
			
			/*
			Campaigns campaign = (Campaigns)sessionScope.getAttribute("campaign");
			sessionScope.removeAttribute("selectedTemplate");
			
			ApplicationPropertiesDao appPropDao = (ApplicationPropertiesDao)SpringUtil.getBean("applicationPropertiesDao");
			
			// call it from constants file Default_JSON
			ApplicationProperties appProp= appPropDao.findByPropertyKey(Constants.DEFAULT_JSON);
			//String defaultJson="";
			//EditorController.getPlaceHolderList(campaign.getMailingLists());
			//EditorController.getCouponsList();
			//11111if(campaign!=null && campaign.getHtmlText() !=null) {
			if(campaign==null || campaign.getJsonContent()==null){
				//logger.info("First time from Scrtacth...:"+appProp.getValue());
				JSONObject jsontemplate = (JSONObject)JSONValue.parse(appProp.getValue());
				//logger.info("Second time from Scrtacth...:"+jsontemplate);
				Clients.evalJavaScript("var mytemplates ="+jsontemplate+";");
                   
			}
			else if(campaign!=null && campaign.getJsonContent() !=null) {
				JSONObject jsontemplate = (JSONObject)JSONValue.parse(campaign.getJsonContent());
				Clients.evalJavaScript("var mytemplates ="+jsontemplate+";");
				
				int confirm = Messagebox.show("Do you want to retain earlier saved email content?", 
						"Confirm", Messagebox.CANCEL | Messagebox.YES | Messagebox.NO , Messagebox.QUESTION);
				
				
				if (confirm == Messagebox.CANCEL) {
					return;
				} 
				else if (confirm ==Messagebox.NO) {
					//1111campaign.setHtmlText("");
					campaign.setJsonContent(appProp.getValue());
					sessionScope.setAttribute("campaign", campaign);
				}
			}
					
			*/
			goTo(3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	} //onClick$BeeEditor
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void onClick$copyTextTbId() {
		try {
			List<CampaignSchedule> campScheduleList = campaignScheduleDao.findAllByUpComingActiveSchedules(campaign.getCampaignId());
			if(campScheduleList != null && campScheduleList.size() > 0){
				Calendar currentTimePlus3hours = getCurrentTimePlus3Huors();
				long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0 && campaignSchedule.getScheduledDate().before(currentTimePlus3hours)).count(); 
				if(activeCount != 0) {
					//MessageUtil.setMessage("Template of a campaign with upcoming schedules cannot be changed.Please delete all active schedules first.", "color:red");
					//return;
					if(	Messagebox.show(" This campaign has upcoming schedule/s. Do you want to continue to change the template? ", 
							"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES ) {
						return;
					}
				}
			}
			Campaigns campaign = (Campaigns)sessionScope.getAttribute("campaign");
			if(campaign!=null && campaign.getHtmlText() !=null) {
				
				int confirm = Messagebox.show("Do you want to retain earlier saved email content?", 
						"Confirm", Messagebox.YES | Messagebox.NO | Messagebox.CANCEL, Messagebox.QUESTION);
				
				
				if (confirm == Messagebox.CANCEL) {
					return;
				} 
				else if (confirm ==Messagebox.NO) {
					campaign.setHtmlText("");
					sessionScope.setAttribute("campaign", campaign);
				}
			}
			
			goTo(2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	} //onClick$copyTextTbId
	
	private Button templateTbId;
	private Button beeEditor;
	private Div templateListDivId, newTemplatesCatDivId;
	private Groupbox categoryGBoxId,templatesGBoxId;
	private Caption captionId;
	private void goTo(int option) {
		
		MessageUtil.clearMessage();
		
		try{
			
			PageUtil.setFromPage("campaign/CampLayout");
			
			
			if(option == 0){
				//Components.removeAllChildren(categoryListLbId);
				getTemPlatesCategoryFromDB();
				captionId.setLabel("Category");
				templateTbId.setSclass("edit_opt_btn_current");
				beeEditor.setSclass("edit_opt_btn");
				templateListDivId.setVisible(true);
				newTemplatesCatDivId.setVisible(false);
				categoryListLbId.setVisible(true);
				//templListDivId.setVisible(true);
//				categoryGBoxId.setVisible(true);
				
				//emailOpnIncId.setSrc("/zul/campaign/selectTemplate.zul");
			}else if(option == 1){
				sessionScope.setAttribute("editorType", "plainTextEditor");
				sessionScope.removeAttribute("selectedTemplate");
				Redirect.goTo(PageListEnum.CAMPAIGN_PLAIN_EDITOR);
			}else if(option == 2){
				sessionScope.setAttribute("editorType", "plainHtmlEditor");
				sessionScope.removeAttribute("selectedTemplate");
				Redirect.goTo(PageListEnum.CAMPAIGN_HTML_EDITOR);
			}
			else if(option == 3){
				sessionScope.setAttribute("editorType", "beeEditor");
				/*Components.removeAllChildren(categoryListLbId);
				getNewTemPlatesCategoryFromDB();
				captionId.setLabel("");
				templateTbId.setSclass("edit_opt_btn_current");
				templateListDivId.setVisible(true);
				*/
				beeEditor.setSclass("edit_opt_btn_current");
				templateTbId.setSclass("edit_opt_btn");
				templateListDivId.setVisible(true);
				newTemplatesCatDivId.setVisible(true);
				categoryListLbId.setVisible(false);
				getTemPlatesCategoryFromDBForDAndDEditor();
				//Redirect.goTo(PageListEnum.CAMPAIGN_HTML_BEE_EDITOR);
			}
		}
		catch (Exception e) {
			
		}
	} // goTo
	
	public void onClick$backBtnId() {
		
		MessageUtil.clearMessage();
		String isEdit = (String)sessionScope.getAttribute("editCampaign");
		session.removeAttribute("editCampaign");
		if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("edit"))
				Redirect.goTo(PageListEnum.CAMPAIGN_FINAL);
			else if(isEdit.equalsIgnoreCase("view")){
				session.removeAttribute("editCampaign");
				//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
				Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
			}
		}else{
			Redirect.goTo(PageListEnum.CAMPAIGN_MLIST);
		}
	} // onClick$backBtnId
	public void getMyTemplatesFromDb(Long userId){
		String userTemplatesDirectory = PropertyUtil.getPropertyValue("userTemplatesDirectory");
			MyTemplatesDao myTemplatesDao =(MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			Components.removeAllChildren(myTemplatesListId);
			File myTemp = null;
			File[] templateList = null;
			 String myTempPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
						File.separator+ currentUser.getUserName()+userTemplatesDirectory;
			
			 myTemp= new File(myTempPathStr);
			 templateList = myTemp.listFiles();
			 for (Object obj: templateList) {
					final File template = (File)obj;
					String folderName=template.getName();
					//List<MyTemplates> tempList =myTemplatesDao.findDefaultMyTemplates(userId,folderName, "MyTemplates");
					List<MyTemplates> tempList =myTemplatesDao.findDefaultMyTemplates(userId,folderName, Constants.MYTEMPATES_PARENT);
					// int totCount = myTemplatesDao.findTotDefaultMyTemplates(userId,folderName);
					 Listitem item = new Listitem(folderName,tempList);
					 
					
					
					item.setParent(myTemplatesListId);
					
			 }
		
		myTemplatesListId.setSelectedIndex(0);
			
	}//getMyTemplatesFromDb()
	/**
	 * Gets the templates previews of selected category
	 * @param myTemplatesListId
	 * @param templListDivId
	 */
	public void onSelect$myTemplatesListId() {
		try{
			String folderName = myTemplatesListId.getSelectedItem().getLabel();
			logger.debug(" -- just entered my templates --"+folderName);
			folderName = folderName.contains("(") ? folderName.substring(0,folderName.indexOf("(")) : folderName;
			folderName = folderName.trim();
			List<MyTemplates> myTemplates = (List<MyTemplates>)myTemplatesListId.getSelectedItem().getValue();
		//	final String categoryName = myTemplates.getName();
			Components.removeAllChildren(templListDivId);
			String userTemplatesDirectory = PropertyUtil.getPropertyValue("userTemplatesDirectory");
			String categoryPathStr ;
			File category = null;
			File[] templateList = null;
			List<File[]> templateFileList = new ArrayList<File[]>();
			String templatePrevStr = null;
			String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
			String preview_Path = null;
			
			
			String userName = GetUser.getUserObj().getUserName();
				//String userInfo = GetUser.getUserInfo();
			templateFileList.clear();
			categoryPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
			File.separator+ userName+userTemplatesDirectory+File.separator+folderName;
			
			logger.info("categoryPathStr path"+categoryPathStr);
			category = new File(categoryPathStr);
			templateList = category.listFiles();
			//templateFileList.addAll(templateList);
			preview_Path =  appUrl + "/UserData/"+userName+userTemplatesDirectory+File.separator+folderName;
			templatePrevStr = "/UserData/"+userName+userTemplatesDirectory+File.separator+folderName;
			
			
		//	logger.debug("--4--" + category.exists() + "  " + category.isDirectory());
			
			Image templateImg = null;
		
			Toolbarbutton preview,edit,deleteTb;
			Label templateName;
			Hbox hbox;
			String preview_Src ="";
			final String useIconPathStr = "/img/theme/use_icon.png";
			String previewIconPathStr = "/img/theme/preview_icon.png";
			final String deleteIconPathStr = "/img/theme/action_delete.gif";
			String vbStyle = "templatePrviewVb";
			String custom_temp_default_preview = "/img/custom_temp_preview.gif";
			
			
			if( templateFileList.size() == 0 ) {
			logger.warn(">>>>>>>>>>>>>>>>>>>> Template list is empty/null");
			}
			
			// loop for generating the templates icons for selection along with preview and use buttons
			
			
			
			
			for (MyTemplates mytemplate : myTemplates) {
				boolean isFound = false;
			for(File templateLst:templateList) {
				
			
					final File template = templateLst;
					
					if(!mytemplate.getName().equals(template.getName())) {
						logger.info("myTemplate ::"+mytemplate.getName()+" template.getName() "+template.getName());
						continue;
					}
					
					isFound = true;
					if(template.isDirectory()){
						
					//	logger.debug(template.getPath());
						final Vbox vbox = new Vbox();
						vbox.setSclass(vbStyle);
						vbox.setAlign("center");
						
						
						if(! new File(template.getPath()+File.separator+"preview.gif").exists()) {
							templateImg = new Image(custom_temp_default_preview);
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
						
						// preview using javascript
						preview.setWidgetListener("onClick", "previewByUrl('"+preview_Src+"')");
						//preview.setAction("onclick:previewByUrl('"+preview_Src+"')");
						
						edit = new Toolbarbutton();
						edit.setImage(useIconPathStr);
						edit.setTooltiptext("Use this layout");
						edit.setAttribute("MyTemplate", mytemplate);
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
						
						hbox.setWidth("190px");
						hbox.setPack("center");
	//				hbox.setWidths("30px 100px 30px 30px");
						deleteTb = new Toolbarbutton();
						deleteTb.setImage(deleteIconPathStr);
						deleteTb.setTooltiptext("Delete this from my templates");
						
						/**
						 * add onClick event listener to delete when clicked
						 */
						deleteTb.addEventListener("onClick", new EventListener() {
							
							@Override
							public void onEvent(Event event) throws Exception {//how we can allow the othr user's template deletion
								try {
									int confirm = Messagebox.show("Are you sure you want to delete the template - "
											+ template.getName() + "?",
											"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
									
									if(confirm != 1){
										return;
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Exception ::" , e);;
								}
								
								template.delete();
								templListDivId.removeChild(vbox);
								/*MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
								myTemplatesDao.deleteByUserIdAndName(currentUser.getUserId(),template.getName());*/
								MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
								myTemplatesDaoForDML.deleteByUserIdAndName(currentUser.getUserId(),template.getName(), Constants.MYTEMPATES_PARENT);
								FileUtils.deleteDirectory(template);
								
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
	} // onSelect$myTemplatesListId()
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		Object eventTarget = event.getTarget();
	
	
		if (eventTarget instanceof Toolbarbutton) {
			
				Toolbarbutton tb = (Toolbarbutton)eventTarget;
				MyTemplates mytemplate = (MyTemplates)tb.getAttribute("MyTemplate");
				String parentDir = (String)tb.getAttribute("parentDir");
				if(mytemplate != null) {
					
					Calendar currentTimePlus3hours = getCurrentTimePlus3Huors();
					
					List<CampaignSchedule> campScheduleList = campaignScheduleDao.findAllByUpComingActiveSchedules(campaign.getCampaignId());
					if(campScheduleList != null && campScheduleList.size() > 0){
						long activeCount = campScheduleList.stream().filter(campaignSchedule -> campaignSchedule.getStatus() == 0 && campaignSchedule.getScheduledDate().before(currentTimePlus3hours)).count(); 
						if(activeCount != 0) {
							//MessageUtil.setMessage("Template of a campaign with upcoming schedules cannot be changed.Please delete all active schedules first.", "color:red");
							//return;
							if(	Messagebox.show(" This campaign has upcoming schedule/s. Do you want to continue to change the template? ", 
									"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES ) {
								return;
							}
						}
					}
					
					/*if(mytemplate.getContent() != null){
						
						if(	Messagebox.show("Earlier saved email & text content will be ignored. Do you want to continue?", 
								"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES ) {
							return;
						}
					}
					sessionScope.removeAttribute("isEdit");
					sessionScope.removeAttribute("editCampaign");
					sessionScope.removeAttribute("isTextMsgBack");
					//sessionScope.setAttribute( "selectedTemplate", selectedTemplateStr);
					
					String editorType = mytemplate.getEditorType();
					if(editorType.equalsIgnoreCase("blockEditor")) {
						
						//mytemplate.setContent(null);
						sessionScope.setAttribute("MyTemplate", mytemplate);
						
						sessionScope.setAttribute( "editorType", editorType);
						Redirect.goTo(PageListEnum.CAMPAIGN_BLOCK_EDITOR);
					}
					else if(editorType.equalsIgnoreCase("plainTextEditor")){
						sessionScope.setAttribute( "editorType", editorType);
						Redirect.goTo(PageListEnum.CAMPAIGN_PLAIN_EDITOR);
					}
					else if(editorType.equalsIgnoreCase("plainHtmlEditor")){
						sessionScope.setAttribute( "editorType", editorType);
						Redirect.goTo(PageListEnum.CAMPAIGN_HTML_EDITOR);
					}
					sessionScope.setAttribute( "editorType", editorType);
					
					//campaign.setHtmlText(null);
				//	campaign.setTextMessage(null);
					sessionScope.setAttribute("campaign", campaign);
					
					//Redirect.goTo(PageListEnum.CAMPAIGN_BLOCK_EDITOR);
*/					
					if(campaign.getHtmlText()!=null){
						
						if(	Messagebox.show(" This campaign's current email template (text and images) will be deleted and overwritten \n with the selected template. Do you want to continue?", 
								"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES ) {
							return;
						}
					}
					sessionScope.removeAttribute("isEdit");
					//sessionScope.removeAttribute("editCampaign");
					sessionScope.removeAttribute("isTextMsgBack");
					String selectedTemplateStr = null;
					if(parentDir != null) {
						selectedTemplateStr = "MyTemplates"+"/"+mytemplate.getFolderName()+"/"+mytemplate.getName();
					}else{
						selectedTemplateStr = "MyTemplates"+"/"+mytemplate.getFolderName()+"/"+mytemplate.getName();
					}
					//final String selectedTemplateStr = "MyTemplates"+"/"+mytemplate.getFolderName()+"/"+mytemplate.getName();
					sessionScope.setAttribute("selectedTemplate", selectedTemplateStr);
					
					campaign.setHtmlText(null);
					campaign.setJsonContent(null);
					campaign.setTextMessage(null);
					String editorType = mytemplate.getEditorType();
						
						if(editorType.equalsIgnoreCase("blockEditor")) {
							
							sessionScope.setAttribute("campaign", campaign);
							
							sessionScope.setAttribute("editorType", editorType);
							Redirect.goTo(PageListEnum.CAMPAIGN_BLOCK_EDITOR);
						}
						else if(editorType.equalsIgnoreCase("plainTextEditor")){
							sessionScope.setAttribute( "editorType", editorType);
							Redirect.goTo(PageListEnum.CAMPAIGN_PLAIN_EDITOR);
						}
						else if(editorType.equalsIgnoreCase("beeEditor")){
							sessionScope.setAttribute( "editorType", editorType);
							Redirect.goTo(PageListEnum.CAMPAIGN_HTML_BEE_EDITOR);
						}
						else if(editorType.equalsIgnoreCase("plainHtmlEditor")){
							sessionScope.setAttribute( "editorType", editorType);
							Redirect.goTo(PageListEnum.CAMPAIGN_HTML_EDITOR);
						}
					
			}
				
			}
	
	
		
		
		
		
	}



}
