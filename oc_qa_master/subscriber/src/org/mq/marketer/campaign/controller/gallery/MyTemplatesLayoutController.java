package org.mq.marketer.campaign.controller.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.SystemTemplates;
import org.mq.marketer.campaign.beans.TemplateCategory;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.layout.TemplateController;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.MyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.SystemTemplatesDao;
import org.mq.marketer.campaign.dao.TemplateCategoryDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

@SuppressWarnings("rawtypes")
public class MyTemplatesLayoutController extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Users currentUser;
	private Session sessionScope;
	private MyTemplatesDao myTemplatesDao;
	private MyTemplatesDaoForDML myTemplatesDaoForDML;
	private SystemTemplatesDao systemTemplatesDao;
	//private MyTemplates myTemplates = null;
	
	
	private Listbox categoryListLbId;
	private Div templListDivId,iconDescriptionDivId;

	public MyTemplatesLayoutController(){
		String style = "font-weight:bold;font-size:15px;color:#313031;" +
				"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("My Templates","",style,true);

		sessionScope = Sessions.getCurrent();
		currentUser = GetUser.getUserObj();
		// myTemplates =(MyTemplates)sessionScope.getAttribute("Template"); 
		 UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		 UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		 /*if(userActivitiesDao != null) {
	      	userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_LAYOUT,GetUser.getUserObj());
		 }*/
		 if(userActivitiesDaoForDML != null) {
		      	userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_LAYOUT,GetUser.getLoginUserObj());
		 }
		 myTemplatesDao= (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
		 myTemplatesDaoForDML= (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
		 systemTemplatesDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
	}
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		//get the total TemplateCategory Name from the DB
		getTemPlatesCategoryFromDB();
		onSelect$categoryListLbId();
	}//doAfterCompose()
	private void getTemPlatesCategoryFromDB() {
		
		List<TemplateCategory> tempCategoryList;
		
		TemplateCategoryDao templateCategoryDao = (TemplateCategoryDao)SpringUtil.getBean("templateCategoryDao");
		tempCategoryList = templateCategoryDao.findCategories();
		Listitem li;
		if(tempCategoryList != null) {
			for (TemplateCategory templateCategory: tempCategoryList) {
				
				li = new Listitem();
				li.setValue(templateCategory);
				li.setLabel(templateCategory.getCategoryName());
				li.setParent(categoryListLbId);
			}
			
		}
		
		if(categoryListLbId.getItemCount() > 0 ) categoryListLbId.setSelectedIndex(0) ; 
		
	}// getTemPlatesCategoryFromDB
	
	public void onClick$backBtnId() {
		
		
		Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
		
	}//onClick$backBtnId()
	
	
	private Groupbox templatesGBoxId;
	private Listbox myTemplatesListId;
	private Div myTempDivId;
	private Label myTempLblId;
	/**
	 * Gets the templates previews of selected category
	 * @param categoryListLbId
	 * @param templListDivId
	 */
	public void onSelect$categoryListLbId() {
		try{
			logger.info(" -- just entered --");
			
			TemplateCategory templateCategory = (TemplateCategory)
			categoryListLbId.getSelectedItem().getValue();
			final String categoryName = templateCategory.getDirName();
			logger.info("categoryName is"+categoryName);
			Components.removeAllChildren(templListDivId);
			String categoryPathStr ;
			File category = null;
			File[] templateList = null;
		//	List<File[]> templateFileList = new ArrayList<File[]>();
			String templatePrevStr = null;
			String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
			String preview_Path = null;
			templatesGBoxId.setVisible(true);
			iconDescriptionDivId.setVisible(true);
			String userTemplatesDirectory = PropertyUtil.getPropertyValue("userTemplatesDirectory");
			/**
			 * if selected option is MyTemplates displays the user specific templates if any,
			 * else displays the system templates.
			 */
			
			
			if(categoryName.equalsIgnoreCase("MyTemplates")) {
				
			//	File myTemp = null;
				 String myTempPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
							File.separator+ currentUser.getUserName()+userTemplatesDirectory;
				
				 category= new File(myTempPathStr);
				 templateList = category.listFiles();
				
				
				myTempDivId.setVisible(true);
				myTemplatesListId.setVisible(true);
				preview_Path = myTempPathStr+File.separator+ currentUser.getUserName()+File.separator+userTemplatesDirectory+
						File.separator+categoryName;
				templatePrevStr = "/UserData/"+ currentUser.getUserName()+userTemplatesDirectory+categoryName;
				getMyTemplatesFromDb(currentUser.getUserId());
				onSelect$myTemplatesListId();
				return;
					
					
				}
			
				logger.info("hi ");
				myTempDivId.setVisible(false);
				myTemplatesListId.setVisible(false);
				//templateFileList.clear();
				categoryPathStr = PropertyUtil.getPropertyValue("templateParent")+
				File.separator+categoryName;
				logger.info("path is "+categoryPathStr);
				category = new File(categoryPathStr);
				templateList = category.listFiles();
				//templateFileList.add(templateList);
				preview_Path =  appUrl + "SystemData/Templates/"+categoryName;
				templatePrevStr = "/SystemData/Templates/"+categoryName;
				List<SystemTemplates> systemTempList = systemTemplatesDao.findByCategoryName(templateCategory.getCategoryName());
				
				
				
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
			
			
			if( templateList.length == 0 ) {
				logger.info("size of"+templateList.length );
				//logger.warn(">>>>>>>>>>>>>>>>>>>> Template list is empty/null");
			}
			
			// loop for generating the templates icons for selection along with preview and use buttons
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
						
						/*if(categoryName.equalsIgnoreCase("MyTemplates")) {
							if(! new File(template.getPath()+File.separator+"preview.gif").exists()) {
								templateImg = new Image(custom_temp_default_preview);
							}
						}
						else{*/
						templateImg = new Image(templatePrevStr+"/"+template.getName()+"/preview.gif");
						
						preview_Src = preview_Path+"/"+template.getName()+"/email.html";
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
						
						//declared as final to use in inner class
					//	final String selectedTemplateStr = categoryName+"/"+template.getName();
						
						/**
						 * add onClick event listener for loading this template to Editor
						 *//*
						edit.addEventListener("onClick", new EventListener() {
							
							@Override
							public void onEvent(Event event) throws Exception {
								try{
									Toolbarbutton tb = (Toolbarbutton)event.getTarget();
									String targetImage = tb.getImage();
									
										
										if(categoryName.equalsIgnoreCase("MyTemplates")) {//here it may not means the current user 
											MyTemplatesDao myTemplatesDao = 
												(MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
											String editorType = myTemplatesDao.getEditorTypeByNameAndUser(currentUser.getUserId(), template.getName());
											
											if(editorType.equalsIgnoreCase("blockEditor")) {
												
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
										else {
											
											
											sessionScope.setAttribute( "editorType", "blockEditor");
											Redirect.goTo(PageListEnum.CAMPAIGN_BLOCK_EDITOR);
										}
									
									
								}catch (Exception e) {
								//	logger.error("** Exception :", (Throwable)e);
								}
							}
							
						});*/
						
						templateName = new Label(template.getName());
						templateName.setMaxlength(11);
						hbox = new Hbox();
						hbox.setWidth("160px");
						hbox.setPack("center");
						//hbox.setAlign("center");
						hbox.setSclass("st_hb");
						
						templateName.setParent(hbox);
						preview.setParent(hbox);
						edit.setParent(hbox);
						
						templateImg.setParent(vbox);
						hbox.setParent(vbox);
						vbox.setParent(templListDivId);
					}
					
					
					if(isFound) break;
			}
		}
	
		}catch(Exception e){
			//logger.error("** Exception :", (Throwable)e);
		}
	} // onSelect$categoryListLbId

	private Button templateTbId;
	private Div templateListDivId;
	private Groupbox categoryGBoxId;
	private void goTo(int option) {
		
		MessageUtil.clearMessage();
		
		try{
			
			PageUtil.setFromPage("gallery/myTemplatesLayout");
			logger.info("editor type is plainTextEditor");
			
			if(option == 0){
				templateTbId.setSclass("edit_opt_btn_current");
				templateListDivId.setVisible(true);
//				categoryGBoxId.setVisible(true);
				
				//emailOpnIncId.setSrc("/zul/campaign/selectTemplate.zul");
			}else if(option == 1){
				sessionScope.setAttribute("editorType","plainTextEditor");
				//sessionScope.remove( "selectedTemplate");
				Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_PLAIN_EDITOR);
				logger.info("hi 2222");
			}else if(option == 2){
				sessionScope.setAttribute("editorType", "plainHtmlEditor");
				Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_HTML_EDITOR);
			}
		}
		catch (Exception e) {
			
		}
	} // goTo
	
	public void onClick$templateTbId() {
		goTo(0);
	} //onClick$templateTbId
	public void getMyTemplatesFromDb(Long userId){
		Components.removeAllChildren(myTemplatesListId);
		File myTemp = null;
		File[] templateList = null;
		 String myTempPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
					File.separator+ currentUser.getUserName()+PropertyUtil.getPropertyValue("userTemplatesDirectory");
		
		 myTemp= new File(myTempPathStr);
		 templateList = myTemp.listFiles();
		 for (Object obj: templateList) {
				final File template = (File)obj;
				String folderName=template.getName();
				List<MyTemplates> tempList =myTemplatesDao.findDefaultMyTemplates(userId,folderName,Constants.MYTEMPATES_PARENT);
				// int totCount = myTemplatesDao.findTotDefaultMyTemplates(userId,folderName);
				 Listitem item = new Listitem(folderName,tempList);
				 
				
				
				item.setParent(myTemplatesListId);
				
		 }
		
	
	myTemplatesListId.setSelectedIndex(0);
		
  }//getMyTemplatesFromDb()
	
	public void onClick$plainEditorTbId() {
		try {
			Object object =(Object)sessionScope.getAttribute("Template");
			if(object instanceof MyTemplates){
				MyTemplates myTemplates=(MyTemplates)object;
				if(myTemplates!=null && myTemplates.getContent() !=null) {
					
					int confirm = Messagebox.show("Do you want to retain earlier saved email content?", 
							"Confirm", Messagebox.CANCEL | Messagebox.YES | Messagebox.NO , Messagebox.QUESTION);
					
					
					if (confirm == Messagebox.CANCEL) {
						return;
					} 
					else if (confirm ==Messagebox.NO) {
						myTemplates.setContent("");
						sessionScope.setAttribute("Template", myTemplates);
					}
				}
				logger.info("mytem");
			}/*else if(object instanceof SystemTemplates){
				SystemTemplates systemTemplates=(SystemTemplates)object;
				
			}*/
			goTo(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} //onClick$plainEditorTbId
	
	public void onClick$copyTextTbId() {
		try {
			/*MyTemplates myTemplates = (MyTemplates)sessionScope.getAttribute("myTemplates");
			if(myTemplates!=null && myTemplates.getContent() !=null) {
				
				int confirm = Messagebox.show("Do you want to retain earlier saved email content?", 
						"Confirm", Messagebox.YES | Messagebox.NO | Messagebox.CANCEL, Messagebox.QUESTION);
				
				
				if (confirm == Messagebox.CANCEL) {
					return;
				} 
				else if (confirm ==Messagebox.NO) {
					myTemplates.setContent("");
					sessionScope.setAttribute("myTemplates", myTemplates);
				}
			}
			*/
			goTo(2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	} //onClick$copyTextTbId
	
	/**
	 * Gets the templates previews of selected category
	 * @param myTemplatesListId
	 * @param templListDivId
	 */
	public void onSelect$myTemplatesListId() {
		try{
			String folderName = myTemplatesListId.getSelectedItem().getLabel();
			logger.info(" -- just entered my templates --"+folderName);
			folderName = folderName.contains("(") ? folderName.substring(0,folderName.indexOf("(")) : folderName;
			List<MyTemplates> myTemplates = (List<MyTemplates>)myTemplatesListId.getSelectedItem().getValue();
		
			Components.removeAllChildren(templListDivId);
			String categoryPathStr ;
			File category = null;
			File[] templateList = null;
			List<File[]> templateFileList = new ArrayList<File[]>();
			String templatePrevStr = null;
			String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
			String preview_Path = null;
			
			
			String userName = GetUser.getUserObj().getUserName();
			templateFileList.clear();
			String userTemplatesDirectory = PropertyUtil.getPropertyValue("userTemplatesDirectory");
			
			categoryPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
			File.separator+ userName+userTemplatesDirectory+folderName;
			category = new File(categoryPathStr);
			templateList = category.listFiles();
			preview_Path =  appUrl + "/UserData/"+userName+userTemplatesDirectory+folderName;
			templatePrevStr = "/UserData/"+userName+userTemplatesDirectory+folderName;
			
			
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
				
				logger.info("my temp size is"+templateLst);
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
						preview = new Toolbarbutton("");
						preview.setImage(previewIconPathStr);
						preview.setTooltiptext("Preview");
						
						// preview using javascript
						preview.setWidgetListener("onClick", "previewByUrl('"+preview_Src+"')");
						//preview.setAction("onclick:previewByUrl('"+preview_Src+"')");
						
						edit = new Toolbarbutton();
						edit.setImage(useIconPathStr);
						edit.setTooltiptext("Edit");
						edit.setAttribute("Template", mytemplate);
						edit.setAttribute("type", "MyTemplate");
						edit.addEventListener("onClick", this);
						//declared as final to use in inner class
							//declared as final to use in inner class
						final String selectedTemplateStr = "/"+template.getName();
							
							
								
						
						
						templateName = new Label(template.getName());
						templateName.setMaxlength(11);
						hbox = new Hbox();
						hbox.setWidth("160px");
						hbox.setPack("center");
						//hbox.setAlign("center");
						hbox.setSclass("st_hb");
						
						templateName.setParent(hbox);
						preview.setParent(hbox);
						edit.setParent(hbox);
						
						hbox.setWidth("190px");
						hbox.setPack("center");
//					hbox.setWidths("30px 100px 30px 30px");
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
									logger.error("Exception ::" , e);
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
						hbox.setParent(vbox);
						vbox.setParent(templListDivId);
						
						
					}
				
					if(isFound) break;
			}
					
					
			}
	
		}catch(Exception e){
			logger.error("Exception ::" , e);
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
				String type = (String)tb.getAttribute("type");
				
				if(type.equalsIgnoreCase("MyTemplate")) {
				MyTemplates mytemplate = (MyTemplates)tb.getAttribute("Template");
				sessionScope.setAttribute("Template", mytemplate);
				
				}else if(type.equalsIgnoreCase("SystemTemplate")) {
					
					SystemTemplates systemTemplate = (SystemTemplates)tb.getAttribute("Template");
					sessionScope.setAttribute("Template", systemTemplate);
					
				}
					
					/*if(mytemplate.getContent() != null){
						
						if(	Messagebox.show("Earlier saved email & text content will be ignored. Do you want to continue?", 
								"Confirm", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) != Messagebox.YES ) {
							return;
						}
					}*/
					sessionScope.removeAttribute("isTemplateEdit");
					
					//sessionScope.setAttribute( "selectedTemplate", selectedTemplateStr);
					
						
					/*myTemplates.setContent(null);
					sessionScope.setAttribute("myTemplates", myTemplates);
					
					String editorType = mytemplate.getEditorType();
					sessionScope.setAttribute( "editorType", editorType);
					Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_LAYOUT);*/
					String editorType = "blockEditor";
					sessionScope.setAttribute("editorType", editorType);
					Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_BLOCK_EDITOR);
					
					
				
			}
	
	
		
		
		
		
	}
	
	
		
}
