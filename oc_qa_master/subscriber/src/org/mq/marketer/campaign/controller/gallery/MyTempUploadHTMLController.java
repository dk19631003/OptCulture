package org.mq.marketer.campaign.controller.gallery;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.SystemTemplates;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.MyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.SystemTemplatesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.SpamChecker;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


@SuppressWarnings("serial")
public class MyTempUploadHTMLController extends MyTempEditorController {
	
	private Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Textbox htmlTbId;
	private Textbox testEmailTbId;
	private Listbox phLbId, cphLbId;
	private Groupbox coupGbId;
	private Timer htmlTimerId;
	private Label autoSaveLbId;

	String htmlStuff;
	public MyTempUploadHTMLController(){
		String style = "font-weight:bold;font-size:15px;color:#313031;" +"font-family:Arial,Helvetica,sans-serif;align:left";
		
		PageUtil.setHeader("My Templates","",style,true);
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		
		
		if((myTemplates ==null && systemTemplates == null) && currentUser == null){
			Redirect.goTo(PageListEnum.RM_HOME);
			return;
		}
		
		
		if(isEdit != null){
			
			
			editorType = myTemplates.getEditorType();
			htmlStuff = myTemplates.getContent();
			
			
			
				
		}else {
			logger.debug("----create new tempalte------");
			htmlStuff = myTemplates != null ? myTemplates.getContent() : "";
			
			
		}
			htmlTbId.setValue(myTemplates.getContent()!=null?myTemplates.getContent():"");
			
			EditorController.getPlaceHolderList(null,false,phLbId);
		
	
		
		Set<String> userRoleSet = (Set<String>)Sessions.getCurrent().getAttribute("userRoleSet");
		//logger.debug("userRoleSet ::"+userRoleSet);
			//PageListEnum plEnum = PageListEnum.ADMIN_VIEW_COUPONS;
			if(userRoleSet.contains(RightsEnum.MenuItem_Promocodes_VIEW.name())) {
			
			List<String> couponPhList = EditorController.getCouponsList();
			cphLbId.setItemRenderer(new phListRenderer());
			cphLbId.setModel(new SimpleListModel(couponPhList));
			coupGbId.setVisible(true);
		}
		Clients.evalJavaScript("parent.window.scrollTo(0,0)");
	}
	
	public void onTimer$htmlTimerId() {
		
		TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		try {
		if(isEdit != null) {
			htmlStuff=myTemplates.getContent();
		
			if(myTemplates != null && htmlStuff != null) {
				
					htmlStuff =htmlTbId.getValue();
					
					
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
				autoSaveLbId.setValue("Last auto-saved at: " +MyCalendar.calendarToString(Calendar.getInstance(),
						MyCalendar.FORMAT_SCHEDULE_TIME,tz));
				
			}
		}
			
		else{
			htmlStuff= htmlTbId.getValue();
			autoSaveLbId.setValue("Last auto-saved at: " +MyCalendar.calendarToString(Calendar.getInstance(),
					MyCalendar.FORMAT_SCHEDULE_TIME,tz));
		}
		
		
	}catch(DataIntegrityViolationException ie){
		logger.error(" Exception1 :"+ ie +" **");
		return ;
	}catch (Exception e) {
		logger.error("** Exception :"+ e.getMessage() +" **");
		return ;
	}
	}
	
	 //public boolean saveHTMLEmail(boolean draft){
	public void onClick$saveNewBtnId(){
		
		try{
			

			if(logger.isDebugEnabled())logger.debug("-- just entered --");
			
			//htmlStuff = htmlStuffId.getValue();
			if(isEdit != null) {
				htmlStuff=myTemplates.getContent();
			
				if(myTemplates != null && htmlStuff != null) {
					
					int confirm = Messagebox.show("Are you sure, you want to modify the template?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1){
						return ;
					}else {
						htmlStuff =htmlTbId.getValue();
						
						String isValidPhStr = null;
						isValidPhStr = Utility.validatePh(htmlStuff, currentUser);
						
						if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
							
							MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
							return ;
						}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
							
							MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either  enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
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
						
						/*String myTemplateFilePathStr = 
							PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+currentUser.getUserName()+
							PropertyUtil.getPropertyValue("userTemplatesDirectory")+File.separator+foldernName+
							File.separator+myTemplates.getName()+File.separator+"email.html";
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
							bw.close();*/
							
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
				
				
			
				
			}else{
				htmlStuff= htmlTbId.getValue();
			}
			
			
			
			
	
			
		
			
			/* // TODO : Need to check this again.
		
			htmlText = htmlTbId.getValue();
			
			String isValidPhStr = null;
			isValidPhStr = Utility.validatePh(htmlText, currentUser);
			
			if(isValidPhStr != null){
				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				return false;
			}
			
			String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(htmlText);
			if(isValidCCDim != null){
				return false;
			}
			
			if(htmlText.trim().length() == 0){
				MessageUtil.setMessage("Email cannot be empty. Paste your HTML code.", "color:red", "TOP");
				return false;
			} else if(Utility.validateHtmlSize(htmlText)) {
				MessageUtil.setMessage("HTML size cannot exceed 100kb.", "color:red","TOP");
				return false;
			}
			if(!checkSpam(htmlText)){
				return false;
			}

			logger.debug("htmlText :"+htmlText);
			
			SystemTemplates st = systemTemplateDao.findByName("plainHtmlEditor");
			campaign.setEditorType("plainHtmlEditor");
			campaign.setTemplate(st);
			campaign.setHtmlText(htmlText);
			campaign.setPrepared(false);
			
			MyTemplates myTemplates = (MyTemplates)sessionScope.getAttribute("MyTemplate");
			myTemplates.setEditorType("plainHtmlEditor");
			myTemplates.setContent(htmlText);
			
			
			
			
			if(isEdit!=null){
				myTemplates.setModifiedDate(Calendar.getInstance());
				}
			
			myTemplatesDao.saveOrUpdate(myTemplates);
			if(draft){
				MessageUtil.setMessage("Your HTML code has been saved successfully.", "color:blue", "TOP");
			}
			return true;*/
		}catch(DataIntegrityViolationException ie){
			logger.error(" Exception1 :"+ ie +" **");
			return ;
		}catch (Exception e) {
			logger.error("** Exception :"+ e.getMessage() +" **");
			return ;
		}
	}
	
	
	

	
	public void onClick$nextBtnId() throws Exception {
		onClick$saveNewBtnId();
	}
	
	
	
	//public void back(){
	public void onClick$backBtnId() throws Exception {
		super.back();
	}
	
	
	
	//public void sendTestMail(String htmlStuff){
	public void onClick$testMailGoBtnId() throws Exception {
		
	
		if(super.sendTestMail(htmlTbId.getValue(), testEmailTbId.getValue())) {
			testEmailTbId.setValue("Email Address...");
			}
		
	}

	public void onClick$spamScrBtnId(){
		super.checkSpam(htmlTbId.getValue(), true);
	}
	
	
	

	
	//public void reload(){
	public void onClick$reloadTlbBtnId() {
		
			 try{
				 logger.debug("reloadBtn Id called");
					int confirm = Messagebox.show("Do you want to reload the email?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1){
						return;
					}else{
						htmlTbId.setValue("");
					//if( !htmlStuff.trim().isEmpty()) {
					String	htmlStuff = null;
						htmlStuff = myTemplates.getContent();
						htmlTbId.setValue(htmlStuff);
					}
			
		}catch (Exception e) {
			logger.error("** Exception : " + e + "**");
		}
	}
	
	private Window previewIframeWin; 
	private  Iframe previewIframeWin$iframeId;
	public void onClick$htmlPreviewImgId() {
		logger.debug("entered into html preview....");
		String htmlContent="";
		if(isEdit != null) {
			htmlContent=myTemplates.getContent();
			 //String htmlContent=campaign.getHtmlText();
			
		}else{
			htmlContent =htmlTbId.getValue();
			
		}
		Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlContent);
		previewIframeWin.setVisible(true);
		
		
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
