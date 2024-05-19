package org.mq.marketer.campaign.controller.gallery;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.EmailContent;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SystemTemplates;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.PrepareFinalHtml;
import org.mq.marketer.campaign.controller.contacts.EditcontactController;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MultiLineMessageBox;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignScheduleDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.MyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.CampaignStepsEnum;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SpamChecker;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;


@SuppressWarnings("serial")
public class MyTempEditorController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	Boolean isEdit;
	String editorType = null;
	Session sessionScope = null;
	String userName = null;
	//Users user = null;
	Users currentUser;
	//Campaigns campaign = null;
	UserActivitiesDao userActivitiesDao;
	 MyTemplatesDao myTemplatesDao;
	 MyTemplatesDaoForDML myTemplatesDaoForDML;
	MyTemplates myTemplates;
	SystemTemplates systemTemplates;
	private static LoyaltyProgramDao loyaltyProgramDao;
	private static LoyaltyProgramTierDao loyaltyProgramTierDao;
	private static LoyaltyThresholdBonusDao loyaltyThresholdBonusDao;
	private static  String  userCurrencySymbol = "$ "; 
	private String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
	static final String APP_MAIN_URL = "https://app.optculture.com/subscriber/";
	static final String APP_MAIN_URL_HTTP = "http://app.optculture.com/subscriber/";
	static final String MAILCONTENT_URL = "http://mailcontent.info/subscriber/";
	static final String MAILHANDLER_URL = "http://mailhandler01.info/subscriber/";
	private static final String ImageServer_Url = PropertyUtil.getPropertyValue("ImageServerUrl");
	
	public MyTempEditorController(){
	
	}

	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			// TODO Auto-generated method stub
			super.doAfterCompose(comp);
			
			
			sessionScope = Sessions.getCurrent();
			editorType = (String)sessionScope.getAttribute("editorType");
			
			logger.debug("-------------- Editor Type : "+editorType);
			
			isEdit = (Boolean)sessionScope.getAttribute("isTemplateEdit");
			Object template = sessionScope.getAttribute("Template");
			if(template instanceof MyTemplates) myTemplates = (MyTemplates)template;
			else if(template instanceof SystemTemplates) systemTemplates = (SystemTemplates)template;
			userName = GetUser.getUserName();
			//user = GetUser.getUserObj();
			currentUser = GetUser.getUserObj();
			userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
		
	}
	protected void gotoPlainMsg(String htmlStuff,SystemTemplates st) {
		
		MessageUtil.clearMessage();
		String isValidPhStr = null;
		isValidPhStr = Utility.validatePh(htmlStuff, currentUser);
		
		if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
			return ;
		}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
			return;
			
		}
		
		/*String isValidCCDim = null;
		isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
		if(isValidCCDim != null){
			return;
		}*/
		String isValidCouponAndBarcode = null;
		isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
		if(isValidCouponAndBarcode != null){
			return;
		}
		
		/*if(Utility.validateHtmlSize(htmlStuff)) {
			MessageUtil.setMessage("HTML size cannot exceed 100kb.",
					"color:red","TOP");
			return;
		}*/
		
		long size =	Utility.validateHtmlSize(htmlStuff);
		if(size >100) {
			String msgcontent = OCConstants.HTML_VALIDATION;
			msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
			MessageUtil.setMessage(msgcontent, "color:Blue");
		}
		
		//if(!saveEmail(htmlStuff,st, null, null, false)) return;
		setFromPage();
		
		/*		
		String textMessage  = HTMLUtility.getTextFromHtml(htmlStuff);
		if(campaign != null) {
			campaign.setTextMessage(textMessage);
		}
		*/
		
		if(isEdit!=null){
			
				sessionScope.removeAttribute("isTemplateEdit");
				String editorType=myTemplates.getEditorType();
				if(editorType.equals("plainTextEditor")){
					Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_PLAIN_EDITOR);

				}else if(editorType.equals("blockEditor")){
					
					Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_BLOCK_EDITOR);
				}else if(editorType.equals("plainHtmlEditor")){
					
					Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_HTML_EDITOR);
				}else if(editorType.equals("beeEditor")){
					
					Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_BEE_EDITOR);
				}
			
		}else
			Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES_BLOCK_EDITOR);
	}
	
	
	
	protected void saveInMyTemplates(Window winId, String name, String content, String editorType, Listbox myTemplatesListId) {
		String foldernName="";
		Label resLbId = (Label)winId.getFellow("resLbId");
		int confirm = -1;
		name = name.trim();
		logger.info("name"+name);
		try{
			if(content == null || content.isEmpty()) {
				
				MessageUtil.setMessage("Template content can not be empty.", "color:red;");
				return;
				
			}
			String isValidPhStr = null;
			isValidPhStr = Utility.validatePh(content, currentUser);
			
			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				return ;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				return ;
				
			}
			
			/*String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(content);
			if(isValidCCDim != null){
				return ;
			}*/
			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(content, currentUser);
			if(isValidCouponAndBarcode != null){
				return;
			}
			
			/*if(Utility.validateHtmlSize(content)) {
				MessageUtil.setMessage("HTML size cannot exceed 100kb. " +
						"Please remove some content.", "color:red", "TOP");
				return ;
			}*/
			
			long size =	Utility.validateHtmlSize(content);
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
			Object obj  = sessionScope.getAttribute("Template");
			
			
			MyTemplates myTemplate = obj == null ?  null : (obj instanceof MyTemplates ? (MyTemplates)obj : null); 
			
			
			myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
			if(myTemplatesDao == null)
				myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			foldernName= myTemplatesListId.getSelectedItem().getLabel();
			
			if(myTemplates==null) {
			
			foldernName = foldernName.contains("(") ? foldernName.substring(0,foldernName.indexOf("(")) : foldernName;
			
			myTemplate = myTemplatesDao.findByUserNameAndTempNameInFolder(currentUser.getUserId(),name,foldernName);
			
			if(myTemplate != null) {
				
				try {
					
					confirm = Messagebox.show("The name already exists. Do you want to replace it?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != Messagebox.OK ) {
						return;

					}else {
						myTemplate.setContent(content);
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);;
				}
			}
			else {
				Calendar cal = MyCalendar.getNewCalendar();
			  foldernName= myTemplatesListId.getSelectedItem().getLabel();
				myTemplate = new MyTemplates(name, content, cal, editorType, currentUser,foldernName, Constants.MYTEMPATES_PARENT);
			}
			//content = PrepareFinalHtml.replaceImgURL(content, currentUser.getUserName());
			content = content.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
					.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
					.replace(MAILHANDLER_URL, ImageServer_Url);
			myTemplate.setContent(content);
			//myTemplatesDao.saveOrUpdate(myTemplate);
			myTemplatesDaoForDML.saveOrUpdate(myTemplate);
			}
			
			else if(myTemplates != null && myTemplates.getFolderName().equalsIgnoreCase(Constants.MYTEMPATES_FOLDERS_DRAFTS) && !myTemplates.getFolderName().equals(myTemplatesListId.getSelectedItem().getLabel())) {
				
				logger.info("-------in move--------");
				 foldernName= myTemplatesListId.getSelectedItem().getLabel();
				 
				 String folderPath = PropertyUtil.getPropertyValue("usersParentDirectory")+
							File.separator+ userName+PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+foldernName+File.separator;
				 
				 String foldernamestr1=myTemplates.getFolderName();
				 String oldname=myTemplates.getName();
				 String folderpathold= PropertyUtil.getPropertyValue("usersParentDirectory")+
							File.separator+ userName+PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+foldernamestr1+File.separator;
					
				 foldernName = foldernName.replaceAll(" +", "_");
					File newFile = new File(folderPath+name);
					if(!newFile.exists()) {
						File changeFile = new File(folderpathold+oldname);
						FileUtils.moveDirectory(changeFile, newFile);
						//content = PrepareFinalHtml.replaceImgURL(content, currentUser.getUserName());
						content = content.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
								.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
								.replace(MAILHANDLER_URL, ImageServer_Url);
						myTemplate.setContent(content);
						myTemplate.setName(name);
						myTemplate.setFolderName(foldernName);
						//myTemplatesDao.saveOrUpdate(myTemplate);
						myTemplatesDaoForDML.saveOrUpdate(myTemplate);
					}
					
			}
			 else if(myTemplates.getFolderName().equals(myTemplatesListId.getSelectedItem().getLabel()) && myTemplatesListId.getSelectedItem().getLabel().equalsIgnoreCase(Constants.MYTEMPATES_FOLDERS_DRAFTS)) {
				 
				 if(myTemplate.getName().equalsIgnoreCase(name)) {
				 confirm = Messagebox.show("The name already exists. Do you want to replace it?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != Messagebox.OK ) {
						return ;

					}else {
						myTemplate.setContent(content);
					}
				 }
				 else {
					 myTemplate.setContent(content);
					 myTemplate.setName(name);
				 }
				 //content = PrepareFinalHtml.replaceImgURL(content, currentUser.getUserName());
				 content= content.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
					.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
					.replace(MAILHANDLER_URL, ImageServer_Url);
				 myTemplate.setContent(content);
				 //myTemplatesDao.saveOrUpdate(myTemplate);
				 myTemplatesDaoForDML.saveOrUpdate(myTemplate);	
				 
			 }
			 
			sessionScope.setAttribute("Template", myTemplate);
			 
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
				PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
				PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+foldernName+
				File.separator+name+File.separator+"email.html";
			try {
				File myTemplateFile = new File(myTemplateFilePathStr);
				File parentDir = myTemplateFile.getParentFile();
				
				if(confirm == 1) {
					FileUtils.deleteDirectory(parentDir);
				}
				
				parentDir.mkdirs();
				
				//TODO Have to copy image files if exists
				BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
				
				bw.write(content);
				bw.flush();
				bw.close();
				String msgStr = name +" saved in "+foldernName+" successfully.";
				if(confirm == 1) {
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
			Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
			return ;
			
		} catch(DataIntegrityViolationException die) {
			//MessageUtil.setMessage("Name already exists in My Templates.", "color:red", "TOP");
			resLbId.setValue("Template with given name already exist in the selected folder,Please provide another name");
			resLbId.setVisible(true);
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);
		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
		}
		return ;
	}
	
	
	
	
	
	
	
	// for Bee Editor
	
	protected boolean saveInMyTemplates(Window winId, String name, String content, String editorType, Listbox myTemplatesListId, String jsoncontent) {
		String foldernName="";
		Label resLbId = (Label)winId.getFellow("resLbId");
		int confirm = -1;
		name = name.trim();
		logger.info("name"+name);
		try{
			if(content == null || content.isEmpty() && jsoncontent == null || jsoncontent.isEmpty()) {
				
				MessageUtil.setMessage("Template content can not be empty.", "color:red;");
				return false;
				
			}
			String isValidPhStr = null;
			isValidPhStr = Utility.validatePh(content, currentUser);
			
			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				return false;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				return false;
				
			}
			
			/*String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(content);
			if(isValidCCDim != null){
				return ;
			}*/
			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(content, currentUser);
			if(isValidCouponAndBarcode != null){
				return false;
			}
			
			/*if(Utility.validateHtmlSize(content)) {
				MessageUtil.setMessage("HTML size cannot exceed 100kb. " +
						"Please remove some content.", "color:red", "TOP");
				return false;
			}*/
			
			long size =	Utility.validateHtmlSize(content);
			if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				MessageUtil.setMessage(msgcontent, "color:Blue");
			}
			
			if(name == null || name.trim().length() == 0){
				//MessageUtil.setMessage("Please provide a name to save in My Templates folder.", "color:red", "TOP");
				resLbId.setValue("Please provide a name to save in my templates");
				resLbId.setVisible(true);
				return false;
			}else if(!Utility.validateName(name)) {
				resLbId.setValue("Please provide a valid name without any special characters ");
				resLbId.setVisible(true);
				return false;
			}
			else {
				resLbId.setVisible(false);
			}
			/**
			 * creates a MyTemplate object and saves in DB
			 */
			Object obj  = sessionScope.getAttribute("Template");
			
			
			MyTemplates myTemplate = obj == null ?  null : (obj instanceof MyTemplates ? (MyTemplates)obj : null); 
			
			
			myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
			if(myTemplatesDao == null)
				myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
			foldernName= myTemplatesListId.getSelectedItem().getLabel();
			foldernName = foldernName.contains("(") ? foldernName.substring(0,foldernName.indexOf("(")) : foldernName;
			foldernName = foldernName.trim();
			if(myTemplates==null) {
			
			
			myTemplate = myTemplatesDao.findByUserNameAndTempNameInFolder(currentUser.getUserId(),name,foldernName, Constants.NEWEDITOR_TEMPLATES_PARENT);
			
			if(myTemplate != null) {
				
				try {
					
					confirm = Messagebox.show("The name already exists. Do you want to replace it?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != Messagebox.OK ) {
						return false;

					}else {
						myTemplate.setContent(content);
						myTemplate.setJsoncontent(jsoncontent);
						
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);;
				}
			}
			else {
				Calendar cal = MyCalendar.getNewCalendar();
			  //foldernName= myTemplatesListId.getSelectedItem().getLabel();
				myTemplate = new MyTemplates(name, content, cal, editorType, currentUser,foldernName,jsoncontent, Constants.NEWEDITOR_TEMPLATES_PARENT);
			}
			//content = PrepareFinalHtml.replaceImgURL(content, currentUser.getUserName());
			content = content.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
					.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
					.replace(MAILHANDLER_URL, ImageServer_Url);
			myTemplate.setContent(content);
			//myTemplatesDao.saveOrUpdate(myTemplate);
			myTemplatesDaoForDML.saveOrUpdate(myTemplate);
			}
			
			else if(myTemplates != null && myTemplates.getFolderName().equalsIgnoreCase(Constants.NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS) &&
					!myTemplates.getFolderName().equals(myTemplatesListId.getSelectedItem().getLabel())) {
				
				logger.info("-------in move--------");
				// foldernName= myTemplatesListId.getSelectedItem().getLabel();
				 
				 String folderPath = PropertyUtil.getPropertyValue("usersParentDirectory")+
							File.separator+ userName+PropertyUtil.getPropertyValue("userNewEditorTemplatesDirectory")+
							File.separator+foldernName+File.separator;
				 
				 String foldernamestr1=myTemplates.getFolderName();
				 String oldname=myTemplates.getName();
				 String folderpathold= PropertyUtil.getPropertyValue("usersParentDirectory")+
							File.separator+ userName+PropertyUtil.getPropertyValue("userNewEditorTemplatesDirectory")+
							File.separator+foldernamestr1+File.separator;
					
				// foldernName = foldernName.replaceAll(" +", "_");
					File newFile = new File(folderPath+name);
					if(!newFile.exists()) {
						File changeFile = new File(folderpathold+oldname);
						FileUtils.moveDirectory(changeFile, newFile);
						//content = PrepareFinalHtml.replaceImgURL(content, currentUser.getUserName());
						content = content.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
								.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
								.replace(MAILHANDLER_URL, ImageServer_Url);
						myTemplate.setContent(content);
						myTemplate.setJsoncontent(jsoncontent);
						myTemplate.setName(name);
						myTemplate.setFolderName(foldernName);
						//myTemplatesDao.saveOrUpdate(myTemplate);
						myTemplatesDaoForDML.saveOrUpdate(myTemplate);
					}
					
			}
			 else if(myTemplates.getFolderName().equals(myTemplatesListId.getSelectedItem().getLabel()) && myTemplatesListId.getSelectedItem().getLabel().equalsIgnoreCase(Constants.MYTEMPATES_FOLDERS_DRAFTS)) {
				 
				 if(myTemplate.getName().equalsIgnoreCase(name)) {
				 confirm = Messagebox.show("The name already exists. Do you want to replace it?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != Messagebox.OK ) {
						return false;

					}else {
						myTemplate.setContent(content);
						myTemplate.setJsoncontent(jsoncontent);
					}
				 }
				 else {
						//Calendar cal = MyCalendar.getNewCalendar();
						  //foldernName= myTemplatesListId.getSelectedItem().getLabel();
						//	myTemplate = new MyTemplates(name, content, cal, editorType, currentUser,foldernName,jsoncontent, Constants.NEWEDITOR_TEMPLATES_PARENT);
						
					 myTemplate.setContent(content);
					 myTemplate.setJsoncontent(jsoncontent);
					 myTemplate.setName(name);
				 }
				 //myTemplatesDao.saveOrUpdate(myTemplate);
				 //content = PrepareFinalHtml.replaceImgURL(content, currentUser.getUserName());
				 content = content.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
				 myTemplate.setContent(content);
				 myTemplatesDaoForDML.saveOrUpdate(myTemplate);	
				 
			 }
			 
			sessionScope.setAttribute("Template", myTemplate);
			 
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
				PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
				PropertyUtil.getPropertyValue("userNewEditorTemplatesDirectory")+File.separator+foldernName+
				File.separator+name+File.separator+"email.html";
			try {
				File myTemplateFile = new File(myTemplateFilePathStr);
				File parentDir = myTemplateFile.getParentFile();
				
				if(confirm == 1) {
					FileUtils.deleteDirectory(parentDir);
				}
				
				parentDir.mkdirs();
				
				//TODO Have to copy image files if exists
				BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
				
				bw.write(content);
				//bw.write(jsoncontent);
				bw.flush();
				bw.close();
				String msgStr = name +" saved in "+foldernName+" successfully.";
				if(confirm == 1) {
					msgStr = name +" updated successfully in "+foldernName;
				}
				MessageUtil.setMessage(msgStr, "color:blue", "TOP");
				
			/*	if(userActivitiesDao != null) {
	        		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SAVED_TO_MYTEMPLATE_p1campaignName, currentUser,campaign.getCampaignName());
				}*/
			} catch (IOException e) {
				logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
				return false;
			}
			
			//TODO need to write a code for generating image file of html for preview purpose

			winId.setVisible(false);
			//Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
			return true;
			
		} catch(DataIntegrityViolationException die) {
			//MessageUtil.setMessage("Name already exists in My Templates.", "color:red", "TOP");
			resLbId.setValue("Template with given name already exist in the selected folder,Please provide another name");
			resLbId.setVisible(true);
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);
			return false;
		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
			return false;
		}
	}
	
	
	
	
	
	
	
	protected Boolean autoSaveInMyTemplates(String name,String content, String editorType,String foldernName) {
		
		int confirm = -1;
		
		MyTemplates myTemplate = null;
		
		myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
		if(myTemplatesDao == null)
			myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
		
		foldernName = foldernName.contains("(") ? foldernName.substring(0,foldernName.indexOf("(")) : foldernName;
		
		myTemplate = myTemplatesDao.findByUserNameAndTempNameInFolder(currentUser.getUserId(),name,foldernName);
		logger.info("----in autosave save for the first time-----"+myTemplate);
		if(myTemplate == null) {
			Calendar cal = MyCalendar.getNewCalendar();
			myTemplate = new MyTemplates(name, content, cal, editorType, currentUser,foldernName, Constants.MYTEMPATES_PARENT);
			logger.info("----in autosave save for the first time 1-----"+myTemplate);
		}
		//content = PrepareFinalHtml.replaceImgURL(content, currentUser.getUserName());
		content = content.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
				.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
				.replace(MAILHANDLER_URL, ImageServer_Url);
		myTemplate.setContent(content);
		//myTemplatesDao.saveOrUpdate(myTemplate);
		myTemplatesDaoForDML.saveOrUpdate(myTemplate);
		
		sessionScope.setAttribute("Template", myTemplate);
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
			PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
			PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+foldernName+
			File.separator+name+File.separator+"email.html";
		logger.info("end of save in mytemplates");
		try {
			File myTemplateFile = new File(myTemplateFilePathStr);
			File parentDir = myTemplateFile.getParentFile();
			
			if(confirm == 1) {
				FileUtils.deleteDirectory(parentDir);
			}
			
			parentDir.mkdirs();
			
			//TODO Have to copy image files if exists
			BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
			bw.write(content);
			bw.flush();
			bw.close();
			return true;
	}
		catch (IOException e) {
			logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
			return false;
		}
	}
	
	
	// For Bee Editor
	
	protected Boolean autoSaveInMyTemplates(String name,String content, String editorType,String foldernName, String jsoncontent) {
		
		int confirm = -1;
		
		MyTemplates myTemplate = null;
		
		myTemplatesDaoForDML = (MyTemplatesDaoForDML)SpringUtil.getBean("myTemplatesDaoForDML");
		if(myTemplatesDao == null)
			myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
		
		foldernName = foldernName.contains("(") ? foldernName.substring(0,foldernName.indexOf("(")) : foldernName;
		foldernName = foldernName.trim();
		myTemplate = myTemplatesDao.findByUserNameAndTempNameInFolder(currentUser.getUserId(),name,foldernName,Constants.NEWEDITOR_TEMPLATES_PARENT );
		logger.info("----in autosave save for the first time-----"+myTemplate);
		if(myTemplate == null) {
			Calendar cal = MyCalendar.getNewCalendar();
			myTemplate = new MyTemplates(name, content, cal, editorType, currentUser,foldernName, jsoncontent, Constants.NEWEDITOR_TEMPLATES_PARENT );
			logger.info("----in autosave save for the first time 1-----"+myTemplate);
		}
		//content = PrepareFinalHtml.replaceImgURL(content, currentUser.getUserName());
		content = content.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
				.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
				.replace(MAILHANDLER_URL, ImageServer_Url);
		myTemplate.setContent(content);
		//myTemplatesDao.saveOrUpdate(myTemplate);
		myTemplatesDaoForDML.saveOrUpdate(myTemplate);
		
		sessionScope.setAttribute("Template", myTemplate);
		/**
		 * creates a html file and saves in user/MyTemplate directory
		 */
		
		String myTemplateFilePathStr = 
			PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
			PropertyUtil.getPropertyValue("userNewEditorTemplatesDirectory")+File.separator+foldernName+
			File.separator+name+File.separator+"email.html";
		logger.info("end of save in mytemplates");
		try {
			File myTemplateFile = new File(myTemplateFilePathStr);
			File parentDir = myTemplateFile.getParentFile();
			
			if(confirm == 1) {
				FileUtils.deleteDirectory(parentDir);
			}
			
			parentDir.mkdirs();
			
			//TODO Have to copy image files if exists
			BufferedWriter bw = new BufferedWriter(new FileWriter(myTemplateFile));
			bw.write(content);
			//bw.write(jsoncontent);
			bw.flush();
			bw.close();
			return true;
	}
		catch (IOException e) {
			logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
			return false;
		}
	}
	
	
	
	
	private Listbox myTemplatesListId;
	protected void saveInMyTemplates(Window winId, String name, String content, String editorType) {
		String foldernName="";
		Label resLbId = (Label)winId.getFellow("resLbId");
		int override = -1;
		try{
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
			
			
			myTemplate = myTemplatesDao.findByUserNameAndTempNameInFolder(currentUser.getUserId(),name,foldernName);
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
				myTemplate = new MyTemplates(name, content, cal, editorType, currentUser,foldernName, Constants.MYTEMPATES_PARENT);
			}
			//content = PrepareFinalHtml.replaceImgURL(content, currentUser.getUserName());
			content = content.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
					.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
					.replace(MAILHANDLER_URL, ImageServer_Url);
			myTemplate.setContent(content);
			//myTemplatesDao.saveOrUpdate(myTemplate);
			myTemplatesDaoForDML.saveOrUpdate(myTemplate);
			
			/**
			 * creates a html file and saves in user/MyTemplate directory
			 */
			String myTemplateFilePathStr = 
				PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
				File.separator+"Email"+File.separator+"MyTemplates"+File.separator+"My Folders"+File.separator+foldernName+
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
				String msgStr = name +" saved in My templates successfully.";
				if(override == 1) {
					msgStr = name +" updated successfully in My templates";
				}
				MessageUtil.setMessage(msgStr, "color:blue", "TOP");
				
				/*if(userActivitiesDao != null) {
	        		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SAVED_TO_MYTEMPLATE_p1campaignName, currentUser,campaign.getCampaignName());
				}*/
			} catch (IOException e) {
				logger.error("** Exception : MyTemplates file creation failed",(Throwable)e);
			}
			
			//TODO need to write a code for generating image file of html for preview purpose

			winId.setVisible(false);
		} catch(DataIntegrityViolationException die) {
			//MessageUtil.setMessage("Name already exists in My Templates.", "color:red", "TOP");
			resLbId.setValue("Unable to save in my templates");
			resLbId.setVisible(true);
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);
		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
		}
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
			
			/*String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(htmlStuff);
			if(isValidCCDim != null){
				return false;
			}*/
			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(htmlStuff, currentUser);
			if(isValidCouponAndBarcode != null){
				return false;
			}
			
			/*if(Utility.validateHtmlSize(htmlStuff)) {
				
				Messagebox.show("HTML size cannot exceed 100kb. Please reduce" +
						" the size and try again.", "Error", Messagebox.OK, Messagebox.ERROR);
				return false;
			}*/

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
					
					//Utility.sendTestMail(campaign, htmlStuff, email);
					
					
				}//for
				
				//Check whether user is expired or not
				if(Calendar.getInstance().after(currentUser.getPackageExpiryDate())){
					logger.debug("Current User::"+currentUser.getUserId()+" is expired, hence cannot send test mail");
					MessageUtil.setMessage("Your account validity period has expired. Please renew your subscription to continue.", "color:red", "TOP");
					return false;
				}
				//TODO
				for (String email : emailArr) {
					
				//	Utility.sendTestMail(campaign, htmlStuff, email);
					//no campaign / customtemplate object hence it ahould go as test auto emails
					Utility.sendInstantMail(null, "My Template", htmlStuff,
							Constants.EQ_TYPE_TEST_OPTIN_MAIL, email, null );
					
					
				}//for
			/*	if(userActivitiesDao != null) {
            		userActivitiesDao.addToActivityList(ActivityEnum.CAMP_SENT_TSTMAIL_p1campaignName, GetUser.getUserObj(), campaign.getCampaignName());
				}*/
				
				
				Messagebox.show("Test mail will be sent in a moment.", "Information", 
						Messagebox.OK, Messagebox.INFORMATION);
				
			return true;
		} catch (Exception e) {
			logger.error("Exception : ", e);
			return true;
		}
	
	}

	
	public static Set<String> getPlaceHolderList() {
		return  EditorController.getPlaceHolderList(null);
	}
	
	/*public static List<String> getMilestonesList() {
		return  EditorController.getMilestones();
	}
	*/
	
	
	public static String getMilestones()
	{

		try {
			List<String> displayList= new ArrayList<String>(); 			
			List<LoyaltyProgram> progTList = new ArrayList<LoyaltyProgram>(); 
			List<LoyaltyProgramTier> tierList = new ArrayList<LoyaltyProgramTier>();
			List<LoyaltyProgram> progList = new ArrayList<LoyaltyProgram>(); 
			List<LoyaltyThresholdBonus> bonusList = new ArrayList<LoyaltyThresholdBonus>();

			
			loyaltyProgramDao = (LoyaltyProgramDao)SpringUtil.getBean(OCConstants.LOYALTY_PROGRAM_DAO);
			progTList = loyaltyProgramDao.getTierEnabledProgListByUserId(GetUser.getUserObj().getUserId());
			progList = loyaltyProgramDao.getProgListByUserId(GetUser.getUserObj().getUserId()) ;
			
			if(progTList!=null){
				for (LoyaltyProgram loyaltyProgram : progTList) {
						loyaltyProgramTierDao = (LoyaltyProgramTierDao)SpringUtil.getBean(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
						tierList = loyaltyProgramTierDao.getTierListByPrgmId(loyaltyProgram.getProgramId());
							//	tierList1.addAll(tierList);
						String programName= loyaltyProgram.getProgramName();
						if(programName.contains("'"))
						{
							programName = programName.replace("'", "\'");
						}
						logger.info("programName============="+programName);
				
					if(tierList!=null) {
				
						for (LoyaltyProgramTier tier : tierList) {

								String textTierStr="";
								String textTierName="";
								String textTierType="";
								String Type="LPT";
								textTierType=tier.getTierType();
								if(!textTierType.equals("Tier 1")){
									textTierStr = programName + " " + textTierType + Constants.DELIMETER_DOUBLECOLON + 
											Constants.CF_START_TAG + "MLS"+ "_" + Type + "_" +  tier.getTierId() + Constants.CF_END_TAG;;
							
											displayList.add(textTierStr);
								}
						} // for
					} // if
				} // for					
			} // if
			  
			if(progList!= null){
				String bonusType;
				for(LoyaltyProgram loyaltyProgram : progList)
				{
					loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)SpringUtil.getBean(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);				
					bonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(loyaltyProgram.getProgramId());
					String programName= loyaltyProgram.getProgramName();
					if(programName.contains("'")){
						programName= programName.replace("'", "\'");		
					}
					String textBonusStr = " ";
					if(bonusList!= null){ 
						for (LoyaltyThresholdBonus bonus : bonusList) {
							if(bonus.getRegistrationFlag()=='N'){
							String Type="LB";
							String currSymbol = Utility.countryCurrencyMap.get(GetUser.getUserObj().getCountryType());
							if(currSymbol != null && !currSymbol.isEmpty())  userCurrencySymbol  = currSymbol + " ";			
							if(!bonus.getExtraBonusType().equalsIgnoreCase("Points")){
								bonusType =  userCurrencySymbol;
					            }
							bonusType = (bonus.getExtraBonusType().equalsIgnoreCase("Points")) ? bonus.getExtraBonusType() : userCurrencySymbol ; 
				
				
				textBonusStr =  programName +
									" " + "Bonus of "+ bonus.getExtraBonusValue()+ " "  + bonusType + 
									 Constants.DELIMETER_DOUBLECOLON + Constants.CF_START_TAG 
									+ "MLS"+ "_" + Type + "_" +  bonus.getThresholdBonusId() + Constants.CF_END_TAG;;
						
						
					
						displayList.add(textBonusStr);
				   }
				}
			  }
			}
		}
			
			
			
			
			
			CouponsDao couponsDao =  (CouponsDao)SpringUtil.getBean("couponsDao");
			List<Coupons> couponList = couponsDao.findCouponsByOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId());
			logger.info("couponList---------------------"+couponList);
			
			if(couponList!=null) {
				String textPromoStr = "";
				String Type = "LP";
				for (Coupons coupon : couponList) {
					//logger.info("just a coupon---------------------"+coupon);
					try{
					if(coupon.getLoyaltyPoints()!=null && coupon.getLoyaltyPoints().byteValue()==1)
					{
						if(coupon.getRequiredLoyltyPoits()>0){
					textPromoStr =  coupon.getCouponName()  + Constants.DELIMETER_DOUBLECOLON + 
							Constants.CF_START_TAG + "MLS"+ "_" + Type + "_" +  coupon.getCouponId() + Constants.CF_END_TAG;;
							
							displayList.add(textPromoStr);
						}
					}
					else continue;
					//logger.info("loyalty points....................._>"+coupon.getLoyaltyPoints());
					}
					catch (Exception e) {
						logger.error("Exception ::" , e);
				}
				}
			}
		//logger.info("displayList---------------------"+displayList);

			Clients.evalJavaScript("var specialLinks79 = [];");					
			/*StringBuffer specialLinksBuffer1 = new StringBuffer();
			for (String couponPlaceHolder : displayList) {
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				String[] coupPHArr = couponPlaceHolder.split(Constants.DELIMETER_DOUBLECOLON );
				String name = coupPHArr[0];
				String value = coupPHArr[1];	
				
				if(specialLinksBuffer1.length() > 0) specialLinksBuffer1.append(",");
				specialLinksBuffer1.append("{type: 'Milestones',label: '" +name+ "', link: 'BEEFREEPROMOCODE_START"+value+"BEEFREEPROMOCODE_END'}"); 
				
				logger.info("names that will be displayed"+name);
				logger.info("value of the merge tag-----------"+value);
				logger.info("specialLinksBuffer1---------------"+specialLinksBuffer1);
			} // for
*/			
			
			//String specialLink2= specialLinksBuffer1.toString();
			//String specialLink3 = specialLink2;
			//Clients.evalJavaScript("specialLinks79 = ["+specialLink2+"];");
			//logger.info("specialLink3------------------"+specialLink2);
			//logger.debug("displayList-------------------->"+displayList);

			logger.debug("-- Exit --");
			StringBuffer buffer = new StringBuffer();		
			for(String placeholder :displayList )
			{
			String[] phTokenArr =  placeholder.split("::"); 
			String name = phTokenArr[0];
			StringBuffer value = new StringBuffer(phTokenArr[1]);
			if(buffer.length() > 0) buffer.append(",");
			name=StringEscapeUtils.escapeJavaScript(name);
			buffer.append("{type: 'Milestones', label: '"+name+"', link: 'BEEFREEPROMOCODE_START"+value+"BEEFREEPROMOCODE_END'}"); 

			}
			String str1=buffer.toString();
			logger.debug("-- Exit --");
			return str1;
			
			//return displayList;
			
			
			
			
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	
		
		
		
	
	}
	
	

	
	
	public static List<String> getCouponsList() {
		try {
			// **************** Populate Coupon codes as a Place holder *********************
			// add("CC_coupon Name::|^CC_123_couponName^|");
			
			List<String> couponsPhList = new ArrayList<String>();
			List<String> imageCouponsPhList = new ArrayList<String>();
			CouponsDao couponsDao =  (CouponsDao)SpringUtil.getBean("couponsDao");
			List<Coupons> couponList = couponsDao.findCouponsByOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId());
//			List<Coupons> couponList = couponsDao.findCouponsByUserId(GetUser.getUserId());
			if(couponList!=null) {
				
				String textcouponStr="";
				String imagecouponStr="";
				for (Coupons coupon : couponList) {
					
					textcouponStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
							Constants.CF_START_TAG + Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
							Constants.CF_END_TAG;
					
					
					if(coupon.getEnableBarcode() && coupon.getBarcodeType() != null && coupon.getBarcodeWidth() != null
							&& coupon.getBarcodeHeight() != null){
						
						/*couponStr = Constants.CC_TOKEN + coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON + 
								Constants.CF_START_TAG + Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
								"_"+coupon.getBarcodeType()+"_"+coupon.getBarcodeWidth()
								+"_"+coupon.getBarcodeHeight()+Constants.CF_END_TAG;*/

						
						String couponIdStr = Constants.CC_TOKEN + coupon.getCouponId() +"_"+coupon.getCouponName()+ 	
						"_"+coupon.getBarcodeType()+"_"+coupon.getBarcodeWidth()
						+"_"+coupon.getBarcodeHeight();
						
						//generate image dynamically and set
						
						
						//generate barcode image
						BitMatrix bitMatrix = null;
						
						String COUPON_CODE_URL = null;
						//String COUPON_CODE_URL_IMG = null;
						String ccPreviewUrl = null;
						
						String message = "Test:"+coupon.getCouponName();
						String barcodeType = coupon.getBarcodeType().trim();
						int width = coupon.getBarcodeWidth().intValue();
						int height = coupon.getBarcodeHeight().intValue();
						
						if(barcodeType.equals(Constants.COUP_BARCODE_QR)){
							
							bitMatrix = new QRCodeWriter().encode(message, BarcodeFormat.QR_CODE, width, height,null);
							String bcqrImg = GetUser.getUserName()+File.separator+
									"Preview"+File.separator+"QRCODE"+File.separator+couponIdStr+".png";
							
							COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcqrImg;
							//COUPON_CODE_URL_IMG = "/subscriber/UserData"+File.separator+bcqrImg;
							ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcqrImg;
						}
						else if(barcodeType.equals(Constants.COUP_BARCODE_AZTEC)){
							
							bitMatrix = new AztecWriter().encode(message, BarcodeFormat.AZTEC, width, height);
							String bcazImg = GetUser.getUserName()+File.separator+
									"Preview"+File.separator+"AZTEC"+File.separator+couponIdStr+".png";
							
							COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcazImg;
							
							ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcazImg;
						}
						else if(barcodeType.equals(Constants.COUP_BARCODE_LINEAR)){
							
							bitMatrix = new Code128Writer().encode(message, BarcodeFormat.CODE_128, width, height,null);
							String bclnImg = GetUser.getUserName()+File.separator+
									"Preview"+File.separator+"LINEAR"+File.separator+couponIdStr+".png";
							
							COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg;
							
							ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
						}
						else if(barcodeType.equals(Constants.COUP_BARCODE_DATAMATRIX)){
							
							bitMatrix = new DataMatrixWriter().encode(message, BarcodeFormat.DATA_MATRIX, width, height,null);
							String bcdmImg = GetUser.getUserName()+File.separator+
									"Preview"+File.separator+"DATAMATRIX"+File.separator+couponIdStr+".png";
							
							COUPON_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bcdmImg;
							
							ccPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bcdmImg;
						}
						
						/*if(bitMatrix == null){
							return;
						}*/
						File myTemplateFile = new File(COUPON_CODE_URL);
						File parentDir = myTemplateFile.getParentFile();
						 if(!parentDir.exists()) {
								
								parentDir.mkdir();
							}

						
						if(!myTemplateFile.exists()) {
							
							MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(
				            		new File(COUPON_CODE_URL)));	
						}
						
						imagecouponStr = coupon.getCouponName() + Constants.DELIMETER_DOUBLECOLON +
								"<img id="+couponIdStr +
										" src="+ccPreviewUrl+" width="+coupon.getBarcodeWidth() +
										" height="+coupon.getBarcodeHeight()+" />";
						imageCouponsPhList.add(imagecouponStr);
						
					}///subscriber/images/CC_test_150_150.png
					
					couponsPhList.add(textcouponStr);
					
				} // for
				
				//couponsPhList.add("CC_TEST::<img id=dummyid src=http://localhost:8080/subscriber/images/CC_test_150_150.png /> ");
				
			} // if
			
						
			// populate Coupons JS Array
			Clients.evalJavaScript("var ocCouponsArr = [];");
			int jsInd=0;
			for (String couponPlaceHolder : couponsPhList) {
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				Clients.evalJavaScript("ocCouponsArr["+ (jsInd++) +"]=\""+couponPlaceHolder+"\";");
			} // for
			
			Clients.evalJavaScript("var ocImageCouponsArr = [];");
			int jsInd1=0;
			for (String couponPlaceHolder : imageCouponsPhList) {
				if(couponPlaceHolder.trim().startsWith("--") || couponPlaceHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				Clients.evalJavaScript("ocImageCouponsArr["+ (jsInd1++) +"]=\""+couponPlaceHolder+"\";");
			} // for
			
			
			logger.debug("-- Exit --");
			return couponsPhList;
		} catch (Exception e) {
			logger.error("Exception ::" , e);;
			return null;
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
	
	public void checkSpam(String htmlStuff, boolean doWindow){
		logger.debug("-- Just Entered --");
		
		String response = getSpamResult(htmlStuff); 
		logger.debug("Spam Report : \n" + response );
		
		if(response==null) {
			MessageUtil.setMessage("Unable to get SPAM report.", "color:blue", "TOP");
			return;
		}
		
		String output;
		try {
			output = response.substring(response.indexOf("Content analysis details"));
		} catch (NumberFormatException e1) {
			logger.debug("NumberFormatException : ",(Throwable)e1);
			output = "Problem while getting spam report"; 
		}
		
		
		String legend = "If Spam Score Is:\n\t 0  to  2 : Good;" +
		"\n\t 2  to  4 : Not bad;\n\tabove 4 : Bad.\n\n" ;
		output=output.replace(output.substring(25, 29),"Spam Score is ");
		output=output.replace(", 5.0 required)", "");
		output =  legend + output;
		
		
		try {
			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show(output, "Spam Info", MultiLineMessageBox.OK, "INFORMATION", false);
		} catch (InterruptedException e2) {
			logger.debug("Exeption : ", (Throwable)e2);
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
	
	public void back(){
		Redirect.goTo(PageListEnum.GALLERY_MY_TEMPALTES);
	}
	
	public void cancelEmailWizard(){
		try{
			int confirm = Messagebox.show("Do you want to cancel editing of email?",
					"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm == 1){
				sessionScope.removeAttribute("emailLayout");
				sessionScope.removeAttribute("selectedTemplate");
				Redirect.goTo(PageListEnum.CAMPAIGN_LAYOUT);
			}
		}catch (Exception e) {
		}
	}
	
	protected void setFromPage(){
		if(editorType!=null){
			if(editorType.equalsIgnoreCase("blockEditor")){
				PageUtil.setFromPage("Editor");
			}else if(editorType.equalsIgnoreCase("plainTextEditor")){
				PageUtil.setFromPage("plainEditor");
			}else if(editorType.equalsIgnoreCase("beeEditor")){
				PageUtil.setFromPage("beeEditor");
			}else
				PageUtil.setFromPage("uploadHTML");
		}
	}
	
	public static String removeEditorTags(String content) {
		StringBuffer finalHTML = new StringBuffer(content);
		String pattern = "<div[^>]*?name=\"TMCEeditableDiv\"[^>]*?>";
		Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(finalHTML);
		StringBuffer removeEditor = new StringBuffer();
		if(logger.isDebugEnabled())logger.debug("---------------------" + pattern + "------------------------------");
		while (m.find()) {
			String replaceStr = "<div name=\"TMCEeditableDiv\" style=\"padding-top:5px;\">";
			logger.debug ("Replaced String :" + replaceStr);
			m.appendReplacement(removeEditor, replaceStr);
		}
		if(logger.isDebugEnabled())logger.debug("---------------------editor stuff is replaced------------------------------");
		m.appendTail(removeEditor);
		return removeEditor.toString();
	}
	
	public static String createEditorTags(String content){
		StringBuffer finalHTML = new StringBuffer(content);
		String pattern = "<div[^>]*?name=\"TMCEeditableDiv\"[^>]*?>";
		Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(finalHTML);
		long timeInMillis = System.currentTimeMillis();
		StringBuffer contentWithTags = new StringBuffer();
		if(logger.isDebugEnabled())logger.debug("---------------------" + pattern + "------------------------------");
		while (m.find()) {
			String replaceStr = PropertyUtil.getPropertyValue("tmceEditableDivSyntax");
			if(replaceStr!=null) {
				replaceStr = replaceStr.replace("|^divId^|", (timeInMillis++) +"");
				logger.debug ("Replaced String :" + replaceStr);
				m.appendReplacement(contentWithTags, replaceStr);
			}
		}
		if(logger.isDebugEnabled())logger.debug("---------------------editor stuff is replaced------------------------------");
		m.appendTail(contentWithTags);
		return contentWithTags.toString();
	}
	
	public static List<String> getDigitalReciptPlaceHolderList(Set<MailingList> mlistSet) {
		
		try {
			logger.debug("-- Just Entered --");
			MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			logger.debug("Got Ml Set of size :" + mlistSet.size());
			
			List<String> placeHoldersList = new ArrayList<String>(); 
			placeHoldersList.addAll(Constants.PLACEHOLDERS_LIST);
			Users user = GetUser.getUserObj();
			
			
			if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				placeHoldersList.removeIf(e -> e.contains("Loyalty Gift Balance"));
			}
			if(user.getloyaltyServicetype() != null && user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_OC) )
			{
				placeHoldersList.removeIf(e -> e.contains("Loyalty Membership Pin"));
			}
			
			if(user.getloyaltyServicetype() != null && !user.getloyaltyServicetype().equals(OCConstants.LOYALTY_SERVICE_TYPE_SB) )
			{
				//logger.info("the current user is a oc user");
				placeHoldersList.addAll(Constants.OCPLACEHOLDERS_LIST);
			}

			
			//Changes to add mapped UDF fields as placeholders
			//1.get all the pos mapped UDFs from the user pos settings(table:pos_mappings)
			POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
			List<POSMapping> contactsUDFList = posMappingDao.findOnlyByType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
			if(contactsUDFList != null) {
				
				//2.prepare merge tag and add to placeHoldersList
				//format : display lable :: |^GEN_<UDF>^|
				for (POSMapping posMapping : contactsUDFList) {
					
					
					
					String udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
									Constants.DELIMETER_DOUBLECOLON +
									Constants.CF_START_TAG + Constants.UDF_TOKEN +
									posMapping.getCustomFieldName() + Constants.CF_END_TAG ;
					
					placeHoldersList.add(udfStr);
					
				}//for
				
				
				
			}//if
			
			
			
			for (MailingList mailingList : mlistSet) {
				if(!mailingList.isCustField())  continue;
				
				List<MLCustomFields> mlcust = mlCustomFieldsDao.findAllByList(mailingList);
				String custField ;
				for (MLCustomFields customField : mlcust) {
					custField = Constants.CF_TOKEN + customField.getCustFieldName() 
								+ Constants.DELIMETER_DOUBLECOLON + Constants.CF_START_TAG + 
								Constants.CF_TOKEN + 
								customField.getCustFieldName().toLowerCase() + Constants.CF_END_TAG;
					
					if(placeHoldersList.contains(custField)) continue;
					placeHoldersList.add(custField);
				}
				
			} // for
			
			// Populate js variable 'phArr' with the place holders for all Editors
			Clients.evalJavaScript("var phArr = [];");
			int jsInd=0;
			for (String placeHolder : placeHoldersList) {
				if(placeHolder.trim().startsWith("--") || placeHolder.toLowerCase().contains("place holder") || 
					placeHolder.startsWith("Unsubscribe Link") || placeHolder.startsWith("Web-Page Version Link") ||
					placeHolder.startsWith("Store")) { //Ignore
					continue;
				}
				
				Clients.evalJavaScript("phArr["+ (jsInd++) +"]=\""+placeHolder+"\";");
			} // for
						
			
			logger.debug("-- Exit --");
			return placeHoldersList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
			return null;
		}
	}
	
	
		
	
	public class phListRenderer implements ListitemRenderer {

		@Override
		public void render(Listitem item, Object data,int arg2) throws Exception {
			// TODO Auto-generated method stub
			if(data instanceof String) {
				String phStr = (String)data;
				if(phStr.indexOf(Constants.DELIMETER_DOUBLECOLON) < 0) return;
				String[] tokens = phStr.split(Constants.DELIMETER_DOUBLECOLON);
				item.setLabel(tokens[0]);
				item.setValue(tokens[1]);
			}
		}
	}

}
