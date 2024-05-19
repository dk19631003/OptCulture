package org.mq.marketer.campaign.controller.campaign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DigitalReceiptMyTemplate;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.UserFromEmailId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DRSentDaoForDML;
import org.mq.marketer.campaign.dao.DigitalReceiptMyTemplatesDao;
import org.mq.marketer.campaign.dao.DigitalReceiptMyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDaoForDML;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.zkforge.ckez.CKeditor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class EReceiptsLegacyController extends GenericForwardComposer implements EventListener{

	Listbox digitalTemplatesListId;
	String digitalRecieptTemplatesPath;
	CKeditor digitalRecieptsCkEditorId;
	DigitalReceiptMyTemplatesDao digiRecptMyTemplatesDao;
	DigitalReceiptMyTemplatesDaoForDML digiRecptMyTemplatesDaoForDML;
	MailingListDao mailingListDao;
	Window winId, testDRWinId, makeACopyWinId, viewDetailsWinId, renameTemplateWinId;
	Textbox winId$templatNameTbId, testDRWinId$testDRTbId;
	Tab myTemplatesTabId, digiRcptReportsTabId;
	Listbox digiRecptReportListId;
	Label selectedTemplateLblId, testDRWinId$msgLblId, viewDetailsWinId$templateNameLblId, viewDetailsWinId$creationDateLblId, viewDetailsWinId$lastAccLblId;
	// Listbox digiRcptSettingLstId1;
	Grid templateAndJSONMappingGridId;
	Button addFieldBtnId;
	Textbox jsonKeyFieldTbxId;
	// Radiogroup templateTypeGrpId;
	Tabbox tabBoxId;
	
	/**
	 * Digital Receipt settings
	 * 
	 */
	
	Image manageActionsImg = new Image("/img/theme/arrow.png");
	
	private Popup regEmailPopupId;
	private Textbox cFromNameTb, cSubTb, cFromEmailTb, cWebLinkTextTb,
			cPhoneTbId;
	private Button cancelBtnId, submitBt1nId, renameTempltBtnId$renameTemplateWinId;
	private Combobox cFromEmailCb;
	private Checkbox toNameChkId, cWebPageCb, enableSendingChkBxId, shippingAmntChkBxId, feeAmntChkBxId, taxAmntChkBxId, discAmntChkBxId,totalAmntChkBxId;
	private Radiogroup cPermRemRb,frmEmaildynamicOrNotRgId,frmNamedynamicOrNotRgId;
	private Div permRemDivId;
	private Div persToDivId, cWebLinkHboxId;
	private Textbox permRemTextId;
	private Listbox phLbId;
	private Textbox cWebLinkUrlTextTb,makeACopyWinId$templatNameTbId,renameTemplateWinId$newTempltNameTbId;
	

	// digiRcptSettingLstId2,digiRcptSettingLstId3,digiRcptSettingLstId4,digiRcptSettingLstId5,
	// digiRcptSettingLstId6;

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	private static final String SYSTEM_TEMPLATE = "SYSTEM:";
	private static final String MY_TEMPLATE = "MY_TEMPLATE:";
	private final String DEFAULT_TEMPLATE_NAME = "";
	String[] templateNamesArr;
	private Users currentUser;
	String[] phArray = { "firstName", "lastName", "fullName" };
	private UserFromEmailIdDao userFromEmailIdDao;
	private UserFromEmailIdDaoForDML userFromEmailIdDaoForDML;
	//private DigitalReceiptUserSettings digitalReceiptUserSettings;
	private DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao;
	private DigitalReceiptUserSettingsDaoForDML digitalReceiptUserSettingsDaoForDML;
	private DRSentDao drSentDao;
	private DRSentDaoForDML drSentDaoForDML;
	private CampaignsDao campaignsDao;
	private EmailQueueDao emailQueueDao;
	private UsersDao usersDao;
	private boolean isAdmin;
	private Session session;
	private Menupopup manageActionsMpId;
	private boolean onSave;
	TimeZone clientTimeZone ;

	DigitalReceiptMyTemplate digitalReceiptMyTemplate;
	Boolean isEdit,isDraftEdit;
	private String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
	static final String APP_MAIN_URL = "https://qcapp.optculture.com/subscriber/";
	static final String APP_MAIN_URL_HTTP = "http://qcapp.optculture.com/subscriber/";
	static final String MAILCONTENT_URL = "http://mailcontent.info/subscriber/";
	static final String MAILHANDLER_URL = "http://mailhandler01.info/subscriber/";
	private static final String ImageServer_Url = PropertyUtil.getPropertyValue("ImageServerUrl");

	
	
	public EReceiptsLegacyController() {
		// TODO Auto-generated constructor stub
		session = Sessions.getCurrent();
		isAdmin = (Boolean)session.getAttribute("isAdmin");
		 clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
				
		isEdit = (Boolean)session.getAttribute("isDRTemplateEdit");
		
		Object dRTemplate = session.getAttribute("DRTemplate");
		if(dRTemplate instanceof DigitalReceiptMyTemplate) digitalReceiptMyTemplate = (DigitalReceiptMyTemplate)dRTemplate;
		else return;
			
		onSave=false;
		currentUser = GetUser.getUserObj();
		this.campaignsDao = (CampaignsDao) SpringUtil.getBean("campaignsDao");
		emailQueueDao = (EmailQueueDao) SpringUtil.getBean("emailQueueDao");
		usersDao = (UsersDao) SpringUtil.getBean("usersDao");
		drSentDao = (DRSentDao) SpringUtil.getBean("drSentDao");
		drSentDaoForDML = (DRSentDaoForDML) SpringUtil.getBean("drSentDaoForDML");
		//PageUtil.setHeader("Digital Receipts", "", "", true);
		PageUtil.setHeader("Legacy Editor", "", "", true);
		digiRecptMyTemplatesDao = (DigitalReceiptMyTemplatesDao) SpringUtil.getBean("digitalReceiptMyTemplatesDao");
		digiRecptMyTemplatesDaoForDML = (DigitalReceiptMyTemplatesDaoForDML) SpringUtil.getBean("digitalReceiptMyTemplatesDaoForDML");
		mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
		digitalReceiptUserSettingsDao= (DigitalReceiptUserSettingsDao) SpringUtil.getBean("digitalReceiptUserSettingsDao");
		digitalReceiptUserSettingsDaoForDML= (DigitalReceiptUserSettingsDaoForDML) SpringUtil.getBean("digitalReceiptUserSettingsDaoForDML");

		this.userFromEmailIdDao = (UserFromEmailIdDao) SpringUtil.getBean("userFromEmailIdDao");
		this.userFromEmailIdDaoForDML = (UserFromEmailIdDaoForDML) SpringUtil.getBean("userFromEmailIdDaoForDML");


/*		digitalRecieptTemplatesPath = PropertyUtil.getPropertyValue("digitalRecieptTemplatesFolder");

		if (digitalRecieptTemplatesPath == null) {
			logger.debug("Digital reciepts Template Folder Does Not Exist ... returning . :: "+ digitalRecieptTemplatesPath);
			return;
		}*/

/*		String selectedTemplate = digitalReceiptUserSettingsDao.findUserSelectedTemplate(GetUser.getUserId());

		if (selectedTemplate == null) {
			selectedTemplateLblId.setValue(DEFAULT_TEMPLATE_NAME);
			selectedTemplateLblId.setAttribute("templateName", DEFAULT_TEMPLATE_NAME);
		} else if (selectedTemplate.startsWith(SYSTEM_TEMPLATE)) {

			selectedTemplateLblId.setAttribute("templateName", selectedTemplate);
			selectedTemplateLblId.setValue(selectedTemplate.substring(SYSTEM_TEMPLATE.length()));
		} else if (selectedTemplate.startsWith(MY_TEMPLATE)) {
			selectedTemplateLblId.setAttribute("templateName", selectedTemplate);
			selectedTemplateLblId.setValue(selectedTemplate.substring(MY_TEMPLATE.length()));
		}
*/		
		
		
		
		if (digitalReceiptMyTemplate != null) {
			String templateName = digitalReceiptMyTemplate.getName();
			if (templateName == null) {
				selectedTemplateLblId.setValue(DEFAULT_TEMPLATE_NAME);
				selectedTemplateLblId.setAttribute("templateName", DEFAULT_TEMPLATE_NAME);
			} else if (templateName.startsWith(SYSTEM_TEMPLATE)) {
				selectedTemplateLblId.setAttribute("templateName", templateName);
				selectedTemplateLblId.setValue(templateName.substring(SYSTEM_TEMPLATE.length()));
			} else if (templateName.startsWith(MY_TEMPLATE)) {
				selectedTemplateLblId.setAttribute("templateName", templateName);
				selectedTemplateLblId.setValue(templateName.substring(MY_TEMPLATE.length()));
			}else {
				selectedTemplateLblId.setAttribute("templateName", templateName);
				selectedTemplateLblId.setValue(templateName!=null?templateName:"");
			}
		}
		
		
		setEditorContent();
		
		// Set Editor PH values.
		MailingList mailingList = mailingListDao.findPOSMailingList(GetUser.getUserObj());
		if(mailingList != null ) {
			Set<MailingList> set = new HashSet<MailingList>();
			set.add(mailingList);
			//List<String> placeHoldersList = 
			//EditorController.getPlaceHolderList(set);
			EditorController.getDigitalReciptPlaceHolderList(set);
		}
		
		EditorController.getCouponsList();
		
		Clients.evalJavaScript("var ocImageCouponsArr = [];");
	}


	
	private void setEditorContent() {

		try {
			logger.info("Setting editor content");
			
			if (digitalReceiptMyTemplate!=null) { // My Templates

				DigitalReceiptMyTemplate dRMyTemplate = digitalReceiptMyTemplate;
				String modifiedPHAsCommentStr =null;
				if(dRMyTemplate!=null)
					modifiedPHAsCommentStr= dRMyTemplate.getContent();

				if (modifiedPHAsCommentStr != null && modifiedPHAsCommentStr.length() > 1) {
					modifiedPHAsCommentStr = modifiedPHAsCommentStr.replace("##BEGIN ITEMS##", "<!-- ##BEGIN ITEMS## -->")
																	.replace("##END ITEMS##", "<!-- ##END ITEMS## -->");
					modifiedPHAsCommentStr = modifiedPHAsCommentStr.replace("##BEGIN PAYMENTS##", "<!-- ##BEGIN PAYMENTS## -->")
																   .replace("##END PAYMENTS##","<!-- ##END PAYMENTS## -->");
				}

				digitalRecieptsCkEditorId.setValue(modifiedPHAsCommentStr);
			}

			logger.info("templates is set ..done !!");
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}

	}

		
	public void onClick$updateTemplateContentBtnId() {
		
		saveDR();

	}
	
	public void onClick$backBtnId() {
		Redirect.goTo(PageListEnum.EMPTY);
		Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPTS);
	} 
	
	
	public void saveDR() {

		try {

			if(digitalReceiptMyTemplate!=null){
			
			logger.info("digi List :"+ digitalReceiptMyTemplate.getName());
			
			String editorContent = digitalRecieptsCkEditorId.getValue();
						
			String isValidPhStr = Utility.validatePh(editorContent, currentUser);
			
			if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				return ;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				return;
				
			}
			
			String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(editorContent, currentUser);
			if(isValidCouponAndBarcode != null){
				return ;
			}
			// Bar code validation
			/*String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(editorContent);
			if(isValidCCDim != null){
				return ;
			}*/
			
			if(editorContent.isEmpty()) {
				MessageUtil.setMessage("Content cannot be Empty.","red", "top");
				return;
			}
			
			editorContent = editorContent.replace("<!-- ##BEGIN ITEMS## -->","##BEGIN ITEMS##")
					 .replace("<!-- ##END ITEMS## -->","##END ITEMS##");
			editorContent = editorContent.replace("<!-- ##BEGIN PAYMENTS## -->","##BEGIN PAYMENTS##")
					 .replace("<!-- ##END PAYMENTS## -->","##END PAYMENTS##");

			editorContent = editorContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
					.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
					.replace(MAILHANDLER_URL, ImageServer_Url);
			DigitalReceiptMyTemplate dRMyTemplate = digitalReceiptMyTemplate;
			dRMyTemplate.setContent(editorContent);
			dRMyTemplate.setModifiedDate(Calendar.getInstance());
			dRMyTemplate.setUserId(currentUser.getUserId());

			dRMyTemplate.setModifiedby(currentUser.getUserId());

			boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(dRMyTemplate.getMyTemplateId());
			if(isConfigured){
				int confirm = Messagebox.show("This template is configured for e-Receipts and\n changes done will immediately reflect in sent\n emails." + "Do you want to continue?", "Information", 
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != 1) return;

			} 
				digiRecptMyTemplatesDaoForDML.saveOrUpdate(dRMyTemplate);

				MessageUtil.setMessage("Template saved successfully.","green", "top");
				
				Redirect.goTo(PageListEnum.EMPTY);
				Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPTS);
		}	
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}

	
	public void onClick$sendTestBtnId() {
		//take the content think about ph logic first
		//to be clarified , do we need validations or not...
		
		/*boolean isValid = validateDRContent();
		if(!isValid) return;
		*/
		testDRWinId$msgLblId.setValue("");
		 boolean isValid = true;
		 if(Utility.validateHtmlSize(digitalRecieptsCkEditorId.getValue())>100) {
				
				Messagebox.show("HTML size cannot exceed 100kb. Please reduce" +
						"the size and try again.", "Error", Messagebox.OK, Messagebox.ERROR);
				isValid = false;	
			}
		 
			if(isValid ) {
				
				 isValid = validateDRContent();
				 
			}
			//Check whether user is expired or not
			if(isValid && Calendar.getInstance().after(currentUser.getPackageExpiryDate())){
				testDRWinId.setVisible(false);
				logger.debug("Current User, with userId:: "+currentUser.getUserId()+" has been expired, hence cannot send test mail");
				MessageUtil.setMessage("Your account validity period has expired. \n Please renew your subscription to continue.", "color:red", "TOP");
				isValid = false;
			}

		
		if(isValid) {
			testDRWinId$testDRTbId.setValue("");
			testDRWinId.setVisible(true);
			testDRWinId.setPosition("center");
			testDRWinId.doHighlighted();
		}
		
	}
	

	public void onClick$sendTestMailBtnId$testDRWinId() {
		 String emailId = testDRWinId$testDRTbId.getValue();
		
		boolean isValid = sendTestMail(emailId);
		if(isValid){
			testDRWinId$testDRTbId.setValue("");
		}
		
	}
	public void onClick$cancelSendTestMailBtnId$testDRWinId() {
		
		testDRWinId$msgLblId.setValue("");
		testDRWinId$msgLblId.setValue("");
		testDRWinId.setVisible(false);
		
	}
	private boolean sendTestMail(String emailId) {

		try {
			boolean isValid = true;			
			if(emailId != null && emailId.trim().length() > 0) {
				
				if(logger.isDebugEnabled())logger.debug("Sending the test mail....");
				
				MessageUtil.clearMessage();
				testDRWinId$msgLblId.setValue("");
				String[] emailArr = null;
				if(isValid) {
					
					emailArr = emailId.split(",");
					for (String email : emailArr) {
						
						if(!Utility.validateEmail(email.trim())){
							testDRWinId$msgLblId.setValue("Invalid Email address:'"+email+"'");
							isValid = false;
							break;
						}
						
						//Utility.sendTestMail(campaign, campaign.getHtmlText(), email);
						
						
					}//for
				}
				
				if(isValid) {
					for (String email : emailArr) {
						
						Utility.sendInstantMail(null, Constants.EQ_TYPE_TEST_DIGITALRCPT, digitalRecieptsCkEditorId.getValue(),
							Constants.EQ_TYPE_TEST_DIGITALRCPT, email, null );
					
			//		Utility.sendTestMail(campaign, campaign.getHtmlText(), email);
					/*String postData = "UserRequest=enable";
					URL url = new URL("http://localhost:8080/Scheduler/simpleMailSender.mqrm");
					
					HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
					
					urlconnection.setRequestMethod("POST");
					urlconnection.setRequestProperty("Content-Type","text/html");
					urlconnection.setDoOutput(true);
					
					OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
					out.write(postData);
					out.flush();
					out.close();*/
					
					
					}//for
					testDRWinId.setVisible(false);
					MessageUtil.setMessage("Test email will be sent in a moment.", "color:blue", "TOP");
					
				}		
				
				
			}else {
				isValid = false;
				testDRWinId$msgLblId.setValue("Invalid Email address");
				testDRWinId$msgLblId.setVisible(true);
			}
			return isValid;
		}
		catch(Exception e) {
			logger.error("** Exception : " , e);
			return false;
		}
		
	
		
	}
	
	public boolean validateDRContent() {
		
		String ckEditorContent = digitalRecieptsCkEditorId.getValue();
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
			// Bar code validation
			/*String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(editorContent);
			if(isValidCCDim != null){
				return ;
			}*/
		 }
		 return isValid;
		// winId.setVisible(true);
	}

	
	
	
	
	
	
	
	
	
	// private String[] allPlaceHoldersArr;

/*	private void setEditorContent(Listitem li) {

		try {
			logger.info("Tab box index is : "+ tabBoxId.getSelectedIndex());
			if (tabBoxId.getSelectedIndex() == 0) { // My Templates

				DigitalReceiptMyTemplate digitalReceiptMyTemplate = digiRecptMyTemplatesDao	.findByUserNameAndTemplateName(GetUser.getUserId(),	(String) li.getValue());
				manageActionsImg.setParent(li.getFirstChild());
				String modifiedPHAsCommentStr =null;
				if(digitalReceiptMyTemplate!=null)
					modifiedPHAsCommentStr= digitalReceiptMyTemplate.getContent();

				if (modifiedPHAsCommentStr != null && modifiedPHAsCommentStr.length() > 1) {
					modifiedPHAsCommentStr = modifiedPHAsCommentStr.replace("##BEGIN ITEMS##", "<!--##BEGIN ITEMS##-->")
																	.replace("##END ITEMS##", "<!--##END ITEMS##-->");
					modifiedPHAsCommentStr = modifiedPHAsCommentStr.replace("##BEGIN PAYMENTS##", "<!--##BEGIN PAYMENTS##-->")
																   .replace("##END PAYMENTS##","<!--##END PAYMENTS##-->");
				}

				digitalRecieptsCkEditorId.setValue(modifiedPHAsCommentStr);
				// digitalRecieptSettingHboxId.setVisible(true);
				// selectedTemplateLblId.setValue(li.getValue()+"");
				return;
			}

			logger.info("element clicked...path : "+ digitalRecieptTemplatesPath + "/" + li.getValue() + "/"
					+ "index.html");
			File tempFile = new File(digitalRecieptTemplatesPath + "/"+ li.getValue() + "/" + "index.html");

			if (!tempFile.exists()) {
				logger.debug("Index file in the selected Folder does not Exist ... returning");
				return;
			}

			BufferedReader br = new BufferedReader(new FileReader(tempFile));

			StringBuffer sb = new StringBuffer();
			String tempStr = null;
			while ((tempStr = br.readLine()) != null) {
				sb.append(tempStr);
			}
			String templateStr = sb.toString();

			templateStr = templateStr.replace("##BEGIN ITEMS##","<!--##BEGIN ITEMS##-->")
									 .replace("##END ITEMS##","<!--##END ITEMS##-->");
			
			templateStr = templateStr.replace("##BEGIN PAYMENTS##","<!--##BEGIN PAYMENTS##-->")
									 .replace("##END PAYMENTS##","<!--##END PAYMENTS##-->");

			digitalRecieptsCkEditorId.setValue(templateStr);
			// digitalRecieptSettingHboxId.setVisible(true);
			// selectedTemplateLblId.setValue(li.getValue()+"");

			logger.info("templates is set ..done !!");
		} catch (FileNotFoundException e) {
			logger.error("Exception ::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}

	}*/


/*	public void onClick$sendTestBtnId() {
		//take the content think about ph logic first
		//to be clarified , do we need validations or not...
		
		boolean isValid = validateDRContent();
		if(!isValid) return;
		
		testDRWinId$msgLblId.setValue("");
		 boolean isValid = true;
		 if(Utility.validateHtmlSize(digitalRecieptsCkEditorId.getValue())>100) {
				
				Messagebox.show("HTML size cannot exceed 100kb. Please reduce" +
						"the size and try again.", "Error", Messagebox.OK, Messagebox.ERROR);
				isValid = false;	
			}
		 
			if(isValid ) {
				
				 isValid = validateDRContent();
				 
			}
			//Check whether user is expired or not
			if(isValid && Calendar.getInstance().after(currentUser.getPackageExpiryDate())){
				testDRWinId.setVisible(false);
				logger.debug("Current User, with userId:: "+currentUser.getUserId()+" has been expired, hence cannot send test mail");
				MessageUtil.setMessage("Your account validity period has expired. Please renew your subscription to continue.", "color:red", "TOP");
				isValid = false;
			}
			if(isValid) {
				
				DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(currentUser.getUserId());
				
				if(digitalReceiptUserSettings == null) {
					MessageUtil.setMessage("Test receipt can not be sent without settings.\n"
							+ "Please save settings to get latest values in the email.", "color:red", "TOP");
					isValid = false;
				}
				
				if(isValid && cSubTb.isValid() && cSubTb.getValue().trim().isEmpty()){
					
					MessageUtil.setMessage("Please provide subject. Subject should not be left empty.", "color:red", "TOP");
					isValid = false;
				}
				

				//  check for from name n it is valid or not
				if(isValid && cFromNameTb.isValid() && frmNamedynamicOrNotRgId.getSelectedIndex() == 0) {
					if(cFromNameTb.getValue().trim().isEmpty() ) {
						
						MessageUtil.setMessage("Provide 'From Name' in settings page.", "color:red", "TOP");
						isValid = false;
						
					}
					
					if(isValid) {
						
						isValid = Utility.validateFromName(cFromNameTb.getValue());
						if(!isValid) {
							
							MessageUtil.setMessage("Provide valid 'From Name' in settings page.Special characters are not allowed.", "color:red", "TOP");
							isValid = false;
						}
					}
				}
				
				
			}
		
		if(isValid) {
			testDRWinId$testDRTbId.setValue("");
			testDRWinId.setVisible(true);
			testDRWinId.setPosition("center");
			testDRWinId.doHighlighted();
		}
		
	}
	*/
	
	public void onClick$submitBtnId() {

		boolean isValid = validateDRContent();
		if(!isValid) return;
		winId$templatNameTbId.setValue("");
		winId.setVisible(true);
		winId.setPosition("center");
		winId.doHighlighted();

	}

	public void onClick$saveInMyTempBtnId$winId() {

		if (winId$templatNameTbId.getValue().trim().equals("")) {
			MessageUtil.setMessage("Template name cannot be left empty.", "red","top");
			return;
		}

		DigitalReceiptMyTemplate digitalReceiptMyTemplate = digiRecptMyTemplatesDao.findByUserNameAndTemplateName(GetUser.getUserId(),winId$templatNameTbId.getValue());

		if (digitalReceiptMyTemplate != null) {

			MessageUtil.setMessage("Template name already exists, please choose another name.","red", "top");
			return;
		}

		logger.info("saving the object");

		// Replace PH comments
		String editorContent = digitalRecieptsCkEditorId.getValue();
		
		String isValidPhStr = Utility.validatePh(editorContent, currentUser);
		
		if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
			return ;
		}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
			return;
			
		}
		
		
		String isValidCouponAndBarcode = null;
		isValidCouponAndBarcode = Utility.validateCCPh(digitalRecieptsCkEditorId.getValue(), currentUser);
		if(isValidCouponAndBarcode != null){
			return ;
		}
		// Bar code validation
		/*String isValidCCDim = null;
		isValidCCDim = Utility.validateCouponDimensions(editorContent);
		if(isValidCCDim != null){
			return ;
		}*/
		
		
		editorContent = editorContent.replace("<!-- ##BEGIN ITEMS## -->","##BEGIN ITEMS##")
									 .replace("<!-- ##END ITEMS## -->","##END ITEMS##");
		editorContent = editorContent.replace("<!-- ##BEGIN PAYMENTS## -->","##BEGIN PAYMENTS##")
									 .replace("<!-- ##END PAYMENTS## -->","##END PAYMENTS##");
		editorContent = editorContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
				.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
				.replace(MAILHANDLER_URL, ImageServer_Url);
		String templateName = winId$templatNameTbId.getValue();
		digitalReceiptMyTemplate = new DigitalReceiptMyTemplate(templateName, editorContent,Calendar.getInstance(), GetUser.getUserId());

		winId.setVisible(false);
		digitalReceiptMyTemplate.setModifiedDate(Calendar.getInstance());
		//digiRecptMyTemplatesDao.saveOrUpdate(digitalReceiptMyTemplate);
		digiRecptMyTemplatesDaoForDML.saveOrUpdate(digitalReceiptMyTemplate);
		MessageUtil.setMessage("Template '" + templateName + "' saved successfully.", "green", "top");
		
	}

	/*
	 * public void onClick$myTemplatesTabId() {
	 * logger.info("just entered ..");
	 * 
	 * List<MyTemplates> myTemplatesList =
	 * myTemplatesDao.getAllByEditorType(GetUser
	 * .getUserId(),"DigitalRecieptEditor");
	 * 
	 * if(myTemplatesList == null && myTemplatesList.size() < 1) {
	 * logger.info
	 * ("No Digital reciept templates exist for the current user .."); return; }
	 * 
	 * Listitem li; Listcell lc; String pattern = "MM/DD/YYY hh:mm";
	 * SimpleDateFormat sdf = new SimpleDateFormat();
	 * Components.removeAllChildren(digitalRecieptListId);
	 * 
	 * for (MyTemplates myTemplates : myTemplatesList) { li = new Listitem();
	 * 
	 * lc = new Listcell(myTemplates.getName()); lc.setParent(li);
	 * 
	 * lc = new Listcell(sdf.format(myTemplates.getCreatedDate().getTime()));
	 * lc.setParent(li);
	 * 
	 * lc = new Listcell();
	 * 
	 * Image img = new Image("/img/email_edit.gif");
	 * img.addEventListener("onClick", this); img.setStyle("padding:10px;");
	 * img.setAttribute("edit", li); img.setParent(lc);
	 * 
	 * Image img2 = new Image("/img/action_delete.gif");
	 * img2.addEventListener("onClick", this); img2.setParent(lc);
	 * img2.setAttribute("delete", li); img2.setStyle("padding:10px;");
	 * lc.setParent(li);
	 * 
	 * li.setParent(digitalRecieptListId); }
	 * 
	 * }
	 */

	/*public void onClick$digiRcptReportsTabId() {

		try {
			TimeZone tz = (TimeZone) session.getAttribute("clientTimeZone");
			// Components.removeAllChildren(digiRecptReportListId.getItems());

			Listheader lhr = new Listheader();
			Listhead lh = new Listhead();

			lhr.setLabel("Email To");
			lhr.setParent(lh);

			lhr = new Listheader("Sent Date");
			lhr.setParent(lh);

			lhr = new Listheader("Status");
			lhr.setParent(lh);

			lhr = new Listheader("View");
			lhr.setParent(lh);

			lh.setParent(digiRecptReportListId);

			EmailQueueDao emailQueueDao = (EmailQueueDao) SpringUtil
					.getBean("emailQueueDao");
			List<EmailQueue> digiRceptList = emailQueueDao.findByType(
					GetUser.getUserId(), "DigitalReciept");

			if (digiRceptList.size() < 1) {
				logger.debug("*** No digital reciepts records found ..***");
				return;
			}

			Listitem li;
			Listcell lc;
			for (EmailQueue emailQueue : digiRceptList) {
				li = new Listitem();

				lc = new Listcell(emailQueue.getToEmailId());
				lc.setParent(li);

				lc = new Listcell(MyCalendar.calendarToString(
						emailQueue.getSentDate(),
						MyCalendar.FORMAT_DATETIME_STDATE, tz));
				lc.setParent(li);

				lc = new Listcell(emailQueue.getStatus());
				lc.setParent(li);

				lc = new Listcell();
				Toolbarbutton tlbarBtn = new Toolbarbutton();
				tlbarBtn.addEventListener("onClick", this);
				tlbarBtn.setImage("/img/digi_receipt_Icons/View-receipt_icn.png");
				tlbarBtn.setParent(lc);
				lc.setParent(li);

				li.setParent(digiRecptReportListId);
				li.setAttribute("digiRcptEmailQueueObj", emailQueue);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}*/

/*	private Window previewWin;
	private Html previewWin$html;

	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);

		if (event.getTarget() instanceof Listitem) {
			Listitem li = (Listitem) event.getTarget();

			setEditorContent(li);

		}
		if (event.getTarget() instanceof Image) {
			logger.info("img clicked ...");
			Image img = (Image) event.getTarget();
			manageActionsMpId.open(img,"after_start"); 
			if (img.getAttribute("templateSettgRowId") != null) {

				Rows tempRows = (Rows) ((Row) img.getAttribute("templateSettgRowId")).getParent();
				tempRows.removeChild((Row) img.getAttribute("templateSettgRowId"));
			}
		}
		if (event.getTarget() instanceof Toolbarbutton) {
			Listitem li = (Listitem) ((Toolbarbutton) event.getTarget())
					.getParent().getParent();
			EmailQueue emailQueue = (EmailQueue) li
					.getAttribute("digiRcptEmailQueueObj");
			if (emailQueue != null) {
				String emailContent = emailQueue.getMessage();
				previewWin$html.setContent(emailContent);
				previewWin.setVisible(true);
			}
		}

	}*/



	


}


