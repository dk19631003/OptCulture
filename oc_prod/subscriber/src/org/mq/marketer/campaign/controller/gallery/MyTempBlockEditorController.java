package org.mq.marketer.campaign.controller.gallery;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MyCalendar;
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
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkex.zul.Colorbox;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class MyTempBlockEditorController  extends MyTempEditorController{


	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	//private SystemTemplates st;
    
	String htmlStuff;
	String htmlPreviewStuff;
	String selectedTemplate;
	String templateName;
	//String categoryName;
	
	Div appCenterDivId;
	Textbox htmlStuffId,htmlStuffId1,htmlStuffId2,htmlStuffAId,spamChekTbId,testMailTbId;
	Textbox testEmailTbId ;
	Html html = new Html();
	Toolbarbutton addBlockBtnId,reloadTlbBtnId;
	Popup editPopup;
	Button saveAsDraftBtnId,nextBtnId,backBtId;
	Toolbarbutton saveNewTBarBtnId,saveEditBtnId, previewTBarBtnId;
//	Listbox phLbId;
	Window winId;
	Colorbox cboxId;
	Timer myTempCkAutoSaveTimerId;
	Label autoSaveLbId;
//	private boolean isAdmin;
	
	//appCenterDivId,htmlStuffId,testEmailTbId,saveAsDraftBtnId,nextBtnId,saveNewTBarBtnId,saveEditBtnId,phLbId, addBlockBtnId, editPopup
	
	public MyTempBlockEditorController(){
		String style = "font-weight:bold;font-size:15px;color:#313031;" +"font-family:Arial,Helvetica,sans-serif;align:left";
		
		PageUtil.setHeader("My Templates","",style,true);
		
		try {
		
			UserActivitiesDao userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
			UserActivitiesDaoForDML userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
			/*if(userActivitiesDao != null) {
			  userActivitiesDao.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_BLOCK_EDITOR,GetUser.getUserObj());
			}*/
			if(userActivitiesDaoForDML != null) {
				  userActivitiesDaoForDML.addToActivityList(ActivityEnum.VISIT_CAMPAIGN_BLOCK_EDITOR,GetUser.getLoginUserObj());
			}
//			isAdmin = (Boolean)Sessions.getCurrent().getAttribute("isAdmin");
		} catch (Exception e) {
			logger.error("Exception : error occured while loading the controller **",e);
		}
	}

	
	
	/*public void init(Div appCenterDivId,Textbox htmlStuffId,
			Textbox testEmailTbId,Button saveAsDraftBtnId, Button nextBtnId, Button saveNewTBarBtnId, 
			Button saveEditBtnId, Listbox phLbId, Toolbarbutton addBlockBtnId , Menupopup editPopup ) {*/
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		try{
			
			super.doAfterCompose(comp);
			
			logger.info("-- just entered --"+myTemplates);
			if((myTemplates ==null && systemTemplates == null) || currentUser == null){
				Redirect.goTo(PageListEnum.RM_HOME);
				return;
			}
			/*this.appCenterDivId = appCenterDivId;
			this.htmlStuffId= htmlStuffId;
			this.testEmailTbId= testEmailTbId;
			this.addBlockBtnId = addBlockBtnId;
			this.editPopup = editPopup;*/
			
			
			/*phLbId.setItemRenderer(new phListRenderer());
			phLbId.setModel(new SimpleListModel(placeHoldersList));
			*/

			if(logger.isDebugEnabled())logger.debug(" isEdit :"+isEdit);
			SystemTemplatesDao systemTemplatesDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
			MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			//boolean isView = true;
			if(isEdit != null){
				//isView = false;
				editorType = myTemplates.getEditorType();
				htmlStuff = myTemplates.getContent();
				
				
				
					
			}else {
				logger.debug("----create new tempalte------");
				if(myTemplates != null){
					
					htmlStuff =  myTemplates.getContent() ;
				}else {
					
					htmlStuff =  systemTemplates .getHtmlText();
					getBlocks(systemTemplates.getName());
				}
				
				
				
			}
			html.setId("htmlId");
			if(htmlStuff == null) {
				logger.debug("html content is null from the session");
				return;
			}
			htmlPreviewStuff = htmlStuff;
			html.setContent (MyTempEditorController.createEditorTags(htmlStuff));
			html.setParent(appCenterDivId);
			
			getPlaceHolderList();
			EditorController.getCouponsList();
			
		}catch(Exception e){
			logger.error(" Exception :", (Throwable)e);
		}
		
		//setting for this attribute to enabling tinyMce type of button in imageLibrary.zul
		sessionScope.setAttribute("EditorType","TinyMCEEditor");
	}
	
	//public void saveEmail(String htmlStuff){ 
	@SuppressWarnings("unchecked")
	private void getBlocks(String templateName){
		try{
			SystemTemplatesDao systemTemplatesDao = (SystemTemplatesDao)SpringUtil.getBean("systemTemplatesDao");
			//Menupopup editPopup = new Menupopup();//(Menupopup)Utility.getComponentById("editPopup");
			
			if(logger.isDebugEnabled())logger.debug("Getting the available blocks list for the layout :"+templateName);
			if(logger.isDebugEnabled())logger.debug("Getting the available blocks list for the layout :"+ systemTemplates.getDirName());
			List blocksList = systemTemplatesDao.findDivisions(templateName, systemTemplates.getDirName());
			if(logger.isDebugEnabled())logger.debug("blockList size :"+blocksList.size());
			String blocks = "";
			String[] blocksArray;
			if(blocksList.size()>0){
				blocks = (String)blocksList.get(0);
				if(logger.isDebugEnabled())logger.debug("availableBlocks from Database as a single linen :"+blocks);
				blocksArray = StringUtils.split(blocks,",");
				for(String blockString:blocksArray){
			        String id =blockString+"DivId";
			        String blck=blockString+"Block";
			        
			        Menuitem mi = new Menuitem();
			        mi.setWidgetListener("onClick", "addNewBlock('"+id+"',"+blck+")");
/*			        String action= "onclick:addNewBlock('"+id+"',"+blck+")";
			        mi.setAction(action);
*/			        mi.setLabel(blockString);
			        mi.setParent(editPopup);
				}
				if(blocksArray.length != 0){
					addBlockBtnId.setVisible(true);
					addBlockBtnId.setPopup(editPopup);
				}
			}else{
				if(logger.isDebugEnabled())logger.debug(" -- 1 --");
				addBlockBtnId.setVisible(false);
			}
		}catch (Exception e) {
			logger.error(" Error while getting the blocks for the selected Template :",e);
		}
	}
	
	
	
	
	
	
	private Textbox winId$templatNameTbId;
	
/*	public void onClick$saveNewTBarBtnId() {
		
		try {
			if(logger.isDebugEnabled())logger.debug("-- just entered --");

			
			if(isEdit != null) {
				if(winId$htmlStuffId3.getAttribute("isModified") == null){
					
					winId$htmlStuffId3.setValue(winId$htmlStuffId3.getValue()+" ");
				}
				Clients.evalJavaScript("save(zk.Widget.$('$htmlStuffId3'))");
				return;
			}
			htmlStuff = htmlStuffId.getValue();
			htmlPreviewStuff = htmlStuffId.getValue();
			
			logger.debug("---save in my template toolbar btn clicked----");
			winId$htmlStuffId3.setValue("");
			winId$resLbId.setValue("");
			winId$templatNameTbId.setValue("");
			launchEditorDivId.setVisible(true);
			winId.setVisible(true);
			winId.setPosition("center");
			winId.doHighlighted();
			getMyTemplatesFromDb(currentUser.getUserId());
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}
	*/
	
	
	
	
	public void onClick$nextBtnId() {
		
		onChange$htmlStuffId();
		
	}
	
	
	TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
	
	public void onChange$htmlStuffAId() {
		
		
		if(isEdit != null && isEdit) {
			
				if(myTemplates != null && htmlStuff != null) {
					
						htmlStuff =htmlStuffAId.getValue();
						
						myTemplates.setModifiedDate(Calendar.getInstance());
						myTemplates.setContent(htmlStuff);
					
						/**
						 * creates a html file and saves in user/MyTemplate directory
						 */
						
						if(htmlStuff.contains("href='")){
							htmlStuff = htmlStuff.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
							
						}
						if(htmlStuff.contains("href=\"")){
							htmlStuff = htmlStuff.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
						}
							
						/**
						 * creates a html file and saves in user/MyTemplate directory
						 */
						String myTemplateFilePathStr = 
								
								PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
								PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+myTemplates.getFolderName()+
								File.separator+myTemplates.getName()+File.separator+"email.html";
								
						try {
							File myTemplateFile = new File(myTemplateFilePathStr);
							File parentDir = myTemplateFile.getParentFile();
							
							if(parentDir.exists() ) {
								FileUtils.deleteDirectory(parentDir);
							}
							
							parentDir.mkdirs();
							
							//TODO Have to copy image files if exists
							BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
							bw.write(htmlStuff);
							bw.flush();
							bw.close();
						
					} catch (IOException e) {
						logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
						MessageUtil.setMessage("Encountered a problem while updating the template.", "color:red", "TOP");
					}
					
						
					/*MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
					myTemplatesDao.saveOrUpdate(myTemplates);*/
					MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
					myTemplatesDaoForDML.saveOrUpdate(myTemplates);	
					logger.debug("after saving the template");
					autoSaveLbId.setValue("Last auto-saved at :"+MyCalendar.calendarToString(Calendar.getInstance(),
							MyCalendar.FORMAT_SCHEDULE_TIME,tz));
			}
		}
		else {
			logger.info("----in new save part of autosave");
			htmlStuff =htmlStuffAId.getValue();
			htmlStuffId.setValue(htmlStuff);
			htmlPreviewStuff = htmlStuffAId.getValue();
			String foldernName=Constants.MYTEMPATES_FOLDERS_DRAFTS;
			String timeStamp = MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, tz);
			String  name = Constants.MYTEMPATES_FOLDERS_FOLDERNAME+timeStamp;
			if(super.autoSaveInMyTemplates(name,htmlStuffAId.getValue(),editorType,foldernName)) {
				autoSaveLbId.setValue("Last auto-saved at :"+MyCalendar.calendarToString(Calendar.getInstance(),
						MyCalendar.FORMAT_SCHEDULE_TIME,tz));
				isEdit=true;
				myTemplates = (MyTemplates)sessionScope.getAttribute("Template");
				logger.info("---------after isedit------"+myTemplates);
			}
			
		}
		htmlStuffAId.setValue("");
	}
	
	
	public void onChange$htmlStuffId(){ 
		try {
			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			
			if(isEdit != null) {
				
				if(myTemplates.getFolderName().equals(Constants.MYTEMPATES_FOLDERS_DRAFTS)) {
					logger.info("------in autosave save button---");
					//if(winId.getAttribute("autosave")==null)winId.setAttribute("autosave","t");
					htmlStuff= htmlStuffAId.getValue();
					htmlPreviewStuff = htmlStuffAId.getValue();
					winId$htmlStuffId3.setValue("");
					winId$resLbId.setValue("");
					winId$templatNameTbId.setValue(myTemplates.getName());
					winId.setVisible(true);
					winId.setPosition("center");
					getMyTemplatesFromDb(currentUser.getUserId());
					winId.doHighlighted();
				}
				else {
					
				if(myTemplates != null && htmlStuff != null) {
					
					myTemplates = (MyTemplates)sessionScope.getAttribute("Template");
					int confirm = Messagebox.show("Are you sure, you want to modify the template?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != Messagebox.OK){
						return;
					}else {
						htmlStuff =htmlStuffId.getValue();
						if(htmlStuff == null || htmlStuff.isEmpty()) {
							
							MessageUtil.setMessage("Template content can not be empty.", "color:red;");
							return;
							
						}
						String isValidPhStr = null;
						isValidPhStr = Utility.validatePh(htmlStuff, currentUser);
						
						if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
							
							MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
							return ;
						}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
							
							MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Update Preference Link' placeholder from your content.", "color:red");
							return;
							
						}
						
						String isValidCCDim = null;
						isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
						if(isValidCCDim != null){
							return ;
						}
						String isValidCouponAndBarcode = null;
						isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
						if(isValidCouponAndBarcode != null){
							return;
						}
						
						/*if(Utility.validateHtmlSize(htmlStuff)) {
							MessageUtil.setMessage("HTML size cannot exceed 100kb. " +
									"Please remove some content.", "color:red", "TOP");
							return ;
						}*/
						
						long size = Utility.validateHtmlSize(htmlStuff);
						if(size >100) {
							String msgcontent = OCConstants.HTML_VALIDATION;
							msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
							MessageUtil.setMessage(msgcontent, "color:Blue");
						}
						
						myTemplates.setModifiedDate(Calendar.getInstance());
						myTemplates.setContent(htmlStuff);
					
						/**
						 * creates a html file and saves in user/MyTemplate directory
						 */
						
						if(htmlStuff.contains("href='")){
							htmlStuff = htmlStuff.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
							
						}
						if(htmlStuff.contains("href=\"")){
							htmlStuff = htmlStuff.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
						}
							
						/**
						 * creates a html file and saves in user/MyTemplate directory
						 */
						String myTemplateFilePathStr = 
								
								PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
								PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+myTemplates.getFolderName()+
								File.separator+myTemplates.getName()+File.separator+"email.html";
								
							
						try {
							File myTemplateFile = new File(myTemplateFilePathStr);
							File parentDir = myTemplateFile.getParentFile();
							
							if(parentDir.exists() ) {
								FileUtils.deleteDirectory(parentDir);
							}
							
							parentDir.mkdirs();
							
							//TODO Have to copy image files if exists
							BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
							bw.write(htmlStuff);
							bw.flush();
							bw.close();
						
							
							MessageUtil.setMessage("Template updated successfully.", "color:blue", "TOP");
						
					} catch (IOException e) {
						logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
						MessageUtil.setMessage("Encountered a problem while updating the template.", "color:red", "TOP");
					}
					
						
					/*MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
					myTemplatesDao.saveOrUpdate(myTemplates);*/
					MyTemplatesDaoForDML myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
					myTemplatesDaoForDML.saveOrUpdate(myTemplates);						
					Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
				}
				
			}
				
			} 
			}
			else{
				
				//if(winId.getAttribute("autosave")==null)winId.setAttribute("autosave","f");
				logger.info("------in normal save button--");
				htmlStuff= htmlStuffId.getValue();
				htmlPreviewStuff = htmlStuffId.getValue();
				winId$htmlStuffId3.setValue("");
				winId$resLbId.setValue("");
				winId$templatNameTbId.setValue("");
				winId.setVisible(true);
				winId.setPosition("center");
				getMyTemplatesFromDb(currentUser.getUserId());
				winId.doHighlighted();
				
			}
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
		htmlStuffId.setValue("");
	}
	
	
	
	//public void saveInMyTemplates(Window winId,String name,Textbox htmlStuffId3){
	public void onChange$htmlStuffId3$winId() throws Exception {
		//if(logger.isDebugEnabled())logger.debug("-- just entered --"+isAutoSave);
			
			String content = winId$htmlStuffId3.getValue().trim();
			htmlPreviewStuff = winId$htmlStuffId3.getValue().trim();
			/*if(isEdit != null) {

					if(myTemplates.getFolderName().equals(Constants.MYTEMPATES_FOLDERS_DRAFTS)) {
						logger.info("------in autosave save button---");
						//if(winId.getAttribute("autosave")==null)winId.setAttribute("autosave","t");
						htmlStuff= htmlStuffId.getValue();
						htmlStuffId.setValue(htmlStuff);
						htmlPreviewStuff = htmlStuffId.getValue();
						winId$htmlStuffId3.setValue("");
						winId$resLbId.setValue("");
						winId$templatNameTbId.setValue(myTemplates.getName());
						winId.setVisible(true);
						winId.setPosition("center");
						getMyTemplatesFromDb(currentUser.getUserId());
						winId.doHighlighted();
					}
					else {
				if(myTemplates != null && content != null && ! content.isEmpty()) {
					int confirm = Messagebox.show("Are you sure, you want to modify the template?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					
					winId$htmlStuffId3.setAttribute("isModified", "YES");
					
					if(confirm != Messagebox.OK){

						 winId$htmlStuffId3.removeAttribute("isModified");
						return;
					}else  {
						
						String isValidPhStr = null;
						
						isValidPhStr = Utility.validatePh(content, currentUser);
						
						if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
							
							MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
							return ;
						}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
							
							MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
							return;
							
						}
						
						String isValidCCDim = null;
						isValidCCDim = Utility.validateCouponDimensions(content);
						if(isValidCCDim != null){
							return ;
						}
						String isValidCouponAndBarcode = null;
						isValidCouponAndBarcode = Utility.validateCCPh(content, currentUser);
						if(isValidCouponAndBarcode != null){
							return;
						}
						
						if(Utility.validateHtmlSize(content)) {
							MessageUtil.setMessage("HTML size cannot exceed 100kb. " +
									"Please remove some content.", "color:red", "TOP");
							return ;
						}
						
						
						myTemplates.setModifiedDate(Calendar.getInstance());
						myTemplates.setContent(content);
					
						

						*//**
						 * creates a html file and saves in user/MyTemplate directory
						 *//*
						
						if(htmlPreviewStuff.contains("href='")){
							htmlPreviewStuff = htmlPreviewStuff.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
							
						}
						if(htmlPreviewStuff.contains("href=\"")){
							htmlPreviewStuff = htmlPreviewStuff.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none;\"");
						}
						
						String myTemplateFilePathStr = 
								PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
								PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+myTemplates.getFolderName()+
								File.separator+myTemplates.getName()+File.separator+"email.html";
								
							
						try {
							File myTemplateFile = new File(myTemplateFilePathStr);
							File parentDir = myTemplateFile.getParentFile();
							
							if(parentDir.exists() ) {
								FileUtils.deleteDirectory(parentDir);
							}
							
							parentDir.mkdirs();
							
							//TODO Have to copy image files if exists
							BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
							bw.write(htmlPreviewStuff);
							bw.flush();
							bw.close();
						
							
							MessageUtil.setMessage("Template updated successfully.", "color:blue", "TOP");
						
						
					} catch (IOException e) {
						logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
						MessageUtil.setMessage("Encountered a problem while updating the template.", "color:red", "TOP");
					}
					
						
					MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
					myTemplatesDao.saveOrUpdate(myTemplates);
					
					Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
					
					
				}
				
			}
					}	
			}*//*else{*/
			
				
				Textbox templatNameTbId =  (Textbox) winId.getFellowIfAny("templatNameTbId"); 
				//Textbox htmlStuffId3 = (Textbox) winId.getFellowIfAny("htmlStuffId3");
				
				super.saveInMyTemplates(winId,templatNameTbId.getValue(),content,editorType,  winId$myTemplatesListId);
				
				winId$htmlStuffId3.setValue("");
				//templatNameTbId.setValue("");
			
			//}
	}
	
	// public void gotoPlainMsg(String htmlStuff){
	public void onChange$htmlStuffId2() throws Exception {
		String htmlStuff = htmlStuffId2.getValue();
		String isValidPhStr = null;
		isValidPhStr = Utility.validatePh(htmlStuff, currentUser);
		
		if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
			return ;
		}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
			return;
			
		}
		
		
		
		String isValidCCDim = null;
		isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
		if(isValidCCDim != null){
			return;
		}
		
		String isValidCouponAndBarcode = null;
		isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
		if(isValidCouponAndBarcode != null){
			return;
		}
		super.gotoPlainMsg(htmlStuff, systemTemplates);
	}
	
	
	//public void saveAsDraft(String htmlStuff){ 
	public void onChange$htmlStuffId1() throws Exception {
		String htmlStuff = htmlStuffId1.getValue();
		//super.saveAsDraft(htmlStuff, st);
	}
	
	
	
	//public void reload(){
	public void onClick$reloadTlbBtnId() throws Exception {
		try{
			int confirm = Messagebox.show("Do you want to reload the email?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1){
				return;
			}else{
				Components.removeAllChildren(appCenterDivId);
				html.setId("htmlId");
				html.setContent (htmlStuff);
				Div myDiv = new Div();
				myDiv.setId("HTMLDivId");
				html.setParent (myDiv);
				myDiv.setParent(appCenterDivId);
			}
		}catch (Exception e) {
			logger.error("** Exception : " + e + "**");
		}
	}

	private  Iframe previewIframeWin$iframeId;
	private Window previewIframeWin; 
	public void onClick$previewTBarBtnId() throws Exception {
		try{
			
			if(htmlPreviewStuff != null){
				
				Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlPreviewStuff);
				previewIframeWin.setVisible(true);
			}
		}catch(Exception e){
			logger.error("Exception ::" , e);;
		}
		
	}
	
	// Added after Holiday templats images problem
	public void onBlur$testEmailTbId() throws Exception {
		 
		 String mail=testEmailTbId.getValue();
		 ////////logger.debug("here in on blur method mail id "+mail);
		 if(mail.equals("Email Address...") || mail.equals("")){
			 testEmailTbId.setValue("Email Address...");
			 
		 }
	 }
	
	public void onClick$sendBtnId() throws Exception {
		
		logger.debug("in onclick.... send ");
		//////logger.debug("inside onclick");
		String emailId=testEmailTbId.getValue();
		if( emailId.equals("Email Address...") || emailId.isEmpty() ){
			MessageUtil.setMessage("There is no mail id to send a test mail.", "color:red");
			testEmailTbId.setValue("Email Address...");
			testEmailTbId.focus();
			return;
		}
		else if(super.sendTestMail(testMailTbId.getValue(), testEmailTbId.getValue())) {
			
			testEmailTbId.setValue("Email Address...");
		}
	}
	public void onFocus$testEmailTbId() throws Exception {
	 	String mail=testEmailTbId.getValue(); 
		if(mail.equals("Email Address...") || mail.equals("")){
			 testEmailTbId.setValue("");
			 
		 } 
	 }
	
	
	
	//public void sendTestMail(Textbox testMailTbId) throws Exception {
	/*public void onChange$testMailTbId() throws Exception {
		if(super.sendTestMail(testMailTbId.getValue(), testEmailTbId.getValue())) {
		testMailTbId.setValue("");
		testEmailTbId.setValue("Email Address...");
		}
	}*/
	
	// public void checkSpam(Textbox spamChekTbId){
	public void onChange$spamChekTbId() throws Exception {
		super.checkSpam(spamChekTbId.getValue(), true);
		spamChekTbId.setValue("");
	}
	
	public void onClick$backBtId() throws Exception {
		super.back();
	}
	
	public void onChange$cboxId() throws Exception {
		 Clients.evalJavaScript("parent.changeBgColor('"+cboxId.getColor()+"');");
	}
	
	Div launchEditorDivId;
//	private Window winId;
	//htmlStuffId3,resLbId
	private Textbox winId$htmlStuffId3;
	private Label winId$resLbId;
	
	private Listbox winId$myTemplatesListId; 
	public void getMyTemplatesFromDb(Long userId){
		String userTemplatesDirectory = PropertyUtil.getPropertyValue("userTemplatesDirectory");
		Components.removeAllChildren(winId$myTemplatesListId);
		//MyTemplatesDao myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
		
		File myTemp = null;
		File[] templateList = null;
		 String myTempPathStr = PropertyUtil.getPropertyValue("usersParentDirectory")+
					File.separator+ currentUser.getUserName()+userTemplatesDirectory;
		
		 myTemp= new File(myTempPathStr);
		 templateList = myTemp.listFiles();
		 for (Object obj: templateList) {
				final File template = (File)obj;
				Listitem item = new Listitem();
				String folderName=template.getName();
				
				 item.setLabel(folderName);
				 
				item.setParent(winId$myTemplatesListId);
				
				
	}
		 
		 if(winId$myTemplatesListId.getItemCount() > 0) {
			 List<Listitem> foldersList = winId$myTemplatesListId.getItems();
			 int index=0;
			 if(myTemplates!=null) {
				 for(Listitem li: foldersList) {
						
						if(li.getLabel().equalsIgnoreCase(Constants.MYTEMPATES_FOLDERS_DRAFTS)) {
							index=li.getIndex();
							winId$myTemplatesListId.setSelectedIndex(index);
							 break;
						}
				 	} 
			}
			 else {
				 winId$myTemplatesListId.setSelectedIndex(index);
			}
				
		 }
		 

}
}
