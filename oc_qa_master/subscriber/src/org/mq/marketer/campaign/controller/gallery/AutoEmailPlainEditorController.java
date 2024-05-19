package org.mq.marketer.campaign.controller.gallery;

import java.util.Timer;

import javax.mail.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.dao.CustomTemplatesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.zkforge.ckez.CKeditor;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;


@SuppressWarnings("serial")
public class AutoEmailPlainEditorController  extends MyTempEditorController{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		
		Textbox emailNameTbId;
		Textbox testEmailTbId;
		Html html = new Html();
		Toolbarbutton addBlockBtnId;
		String userName;
		String usersParentDirectory;
		String htmlFilePath;
		CKeditor ckEditorId = null;
		CKeditor fckEditorId;
		String htmlStuff;
		String htmlPreviewStuff;
		Timer mytempAutoSaveId;
		Label autoSaveLbId;
		private CustomTemplates autoEmailTemplate;
		private CustomTemplatesDaoForDML customTemplateDaoForDML;
		private String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
		static final String APP_MAIN_URL = "https://qcapp.optculture.com/subscriber/";
		static final String APP_MAIN_URL_HTTP = "http://qcapp.optculture.com/subscriber/";
		static final String MAILCONTENT_URL = "http://mailcontent.info/subscriber/";
		static final String MAILHANDLER_URL = "http://mailhandler01.info/subscriber/";
		private static final String ImageServer_Url = PropertyUtil.getPropertyValue("ImageServerUrl");

		

		public AutoEmailPlainEditorController() {
			String style = "font-weight:bold;font-size:15px;color:#313031;" +"font-family:Arial,Helvetica,sans-serif;align:left";
			PageUtil.setHeader("Auto Email Custom Templates","",style,true);
			userName = GetUser.getUserName();
			customTemplateDaoForDML = (CustomTemplatesDaoForDML)SpringUtil.getBean("customTemplatesDaoForDML");
		}
		
		@Override
		public void doAfterCompose(Component comp) throws Exception {
			try {
				super.doAfterCompose(comp);
				logger.debug(" in plain editor");
				if(logger.isDebugEnabled())logger.debug("-- just entered --"+isEdit);
				if((myTemplates ==null && systemTemplates == null) && currentUser == null){
					Redirect.goTo(PageListEnum.RM_HOME);
					return;
				}
				getPlaceHolderList();
				EditorController.getCouponsList();
				autoEmailTemplate = (CustomTemplates) sessionScope.getAttribute("autoEmailOldTemplateRedirect");
				if(autoEmailTemplate!= null) {
					fckEditorId.setValue(autoEmailTemplate.getHtmlText());
				}
				String autoEmailTemplateDefault = (String) sessionScope.getAttribute("autoEmailOldTemplateRedirectDefault");
				if(autoEmailTemplateDefault!= null) {
					fckEditorId.setValue(autoEmailTemplateDefault);
				}
				
			}catch(Exception e) {
				logger.error(" Exception : ", e );
			}
		}
		
		public void onClick$saveBtnId() {
			htmlStuff =fckEditorId.getValue();
			htmlStuff = htmlStuff.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
				.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
				.replace(MAILHANDLER_URL, ImageServer_Url);
			autoEmailTemplate.setHtmlText(htmlStuff);
			if(autoEmailTemplate.getHtmlText()!= null && !autoEmailTemplate.getHtmlText().isEmpty()) {
				customTemplateDaoForDML.saveOrUpdate(autoEmailTemplate);
				Messagebox.show(""+autoEmailTemplate.getTemplateName()+" saved successfully.");
			}else {
				MessageUtil.setMessage("Empty Template Not saved. ", "color:red;");
			}
		}
		

		public void onClose$winId(Event event) {
			self.setVisible(false);
			event.stopPropagation();
		}
		
		 public void onClick$testEmailGoBtnId() throws Exception {
			
			 String emailId=testEmailTbId.getValue();
			 if( emailId.equals("Email Address...") || emailId.isEmpty() ){
					MessageUtil.setMessage("There is no mail id to send a test mail.", "color:red");
					testEmailTbId.setValue("Email Address...");
				}
			
			 else if(super.sendTestMail(fckEditorId.getValue(), testEmailTbId.getValue())) {
				testEmailTbId.setValue("Email Address...");
				}
			 
			 
		}
		 
		// changes here
		 public void onBlur$testEmailTbId() throws Exception {
			 String mail=testEmailTbId.getValue();
			 if(mail.equals("Email Address...") || mail.equals("")){
				 testEmailTbId.setValue("Email Address...");
				 
			 }
		 }
		 
		 // changes here
		 public void onFocus$testEmailTbId() throws Exception {
			 	String mail=testEmailTbId.getValue(); 
				if(mail.equals("Email Address...") || mail.equals("")){
					 testEmailTbId.setValue("");
					 
				 } 
			 }
		 
		 public void onClick$reloadTblId() throws Exception {
			 try{
				 logger.debug("reloadBtn Id called");
					int confirm = Messagebox.show("Do you want to reload the email?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1){
						return;
					}else{
					fckEditorId.setValue("");
					String	htmlStuff = null;
						htmlStuff = myTemplates.getContent();
						fckEditorId.setValue(htmlStuff);
					}
				}catch (Exception e) {
					logger.error("** Exception : " , e );
				}
		 }
		 
		 public void onClick$backBtnId() throws Exception {
			Sessions.getCurrent().setAttribute("dispalyCreateNew", true);
			Long templateId = null;
			if(Sessions.getCurrent().getAttribute("beeBackButtonAutoPoplate")!= null) {
				templateId = (Long)Sessions.getCurrent().getAttribute("beeBackButtonAutoPoplate");
			}
			Sessions.getCurrent().setAttribute("beeBackButtonAutoPoplateInCreateTemplate",templateId);
			Redirect.goTo(PageListEnum.CONTACT_MANAGE_AUTO_EMAILS_BEE);
		 }
		 
		 
		 private Window previewIframeWin; 
			private  Iframe previewIframeWin$iframeId;
			public void onClick$plainPreviewImgId() {
				logger.debug("entered into html preview....");
				String htmlContent="";
				if(isEdit != null) {
					htmlContent=myTemplates.getContent();
				}else{
					htmlContent =fckEditorId.getValue();
				}
				Utility.showPreview(previewIframeWin$iframeId,currentUser.getUserName(), htmlContent);
				previewIframeWin.setVisible(true);
				
				
			}
	} 
