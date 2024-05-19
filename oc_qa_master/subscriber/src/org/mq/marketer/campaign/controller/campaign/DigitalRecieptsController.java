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

public class DigitalRecieptsController extends GenericForwardComposer implements EventListener, ListitemRenderer {

	Listbox digitalTemplatesListId;
	String digitalRecieptTemplatesPath;
	CKeditor digitalRecieptsCkEditorId;
	DigitalReceiptMyTemplatesDao digiRecptMyTemplatesDao;
	DigitalReceiptMyTemplatesDaoForDML digiRecptMyTemplatesDaoForDML;
	MailingListDao mailingListDao;
	Window winId, testDRWinId, makeACopyWinId, viewDetailsWinId, renameTemplateWinId;
	Textbox winId$templatNameTbId, testDRWinId$testDRTbId;
	Tab myTemplatesTabId, digiRcptReportsTabId;
	Listbox digiRecptReportListId, myTemplatesListId;
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
	private final String DEFAULT_TEMPLATE_NAME = "No template selected.";
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
	
	public DigitalRecieptsController() {
		// TODO Auto-generated constructor stub
		session = Sessions.getCurrent();
		isAdmin = (Boolean)session.getAttribute("isAdmin");
		 clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		onSave=false;
		currentUser = GetUser.getUserObj();
		this.campaignsDao = (CampaignsDao) SpringUtil.getBean("campaignsDao");
		emailQueueDao = (EmailQueueDao) SpringUtil.getBean("emailQueueDao");
		usersDao = (UsersDao) SpringUtil.getBean("usersDao");
		drSentDao = (DRSentDao) SpringUtil.getBean("drSentDao");
		drSentDaoForDML = (DRSentDaoForDML) SpringUtil.getBean("drSentDaoForDML");
		PageUtil.setHeader("Digital Receipts", "", "", true);
		digiRecptMyTemplatesDao = (DigitalReceiptMyTemplatesDao) SpringUtil.getBean("digitalReceiptMyTemplatesDao");
		digiRecptMyTemplatesDaoForDML = (DigitalReceiptMyTemplatesDaoForDML) SpringUtil.getBean("digitalReceiptMyTemplatesDaoForDML");
		mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
		digitalReceiptUserSettingsDao= (DigitalReceiptUserSettingsDao) SpringUtil.getBean("digitalReceiptUserSettingsDao");
		digitalReceiptUserSettingsDaoForDML= (DigitalReceiptUserSettingsDaoForDML) SpringUtil.getBean("digitalReceiptUserSettingsDaoForDML");

		this.userFromEmailIdDao = (UserFromEmailIdDao) SpringUtil.getBean("userFromEmailIdDao");
		this.userFromEmailIdDaoForDML = (UserFromEmailIdDaoForDML) SpringUtil.getBean("userFromEmailIdDaoForDML");


		digitalRecieptTemplatesPath = PropertyUtil.getPropertyValue("digitalRecieptTemplatesFolder");

		if (digitalRecieptTemplatesPath == null) {
			logger.debug("Digital reciepts Template Folder Does Not Exist ... returning . :: "+ digitalRecieptTemplatesPath);
			return;
		}

		File digitalTemplatesDirectory = new File(digitalRecieptTemplatesPath);
		templateNamesArr = digitalTemplatesDirectory.list();
		
		if (templateNamesArr == null || templateNamesArr.length < 1) {
			logger.debug("Digital reciepts Directory is not having any files ... returning  ");
			return;
		}
		Arrays.sort(templateNamesArr);

		Listitem li = null;

		for (int i = 0; i < templateNamesArr.length; i++) {
			
			Listcell lc = new Listcell();
			lc.setLabel(templateNamesArr[i]);
			li = new Listitem();
			lc.setParent(li);
			li.setValue(templateNamesArr[i]);
			li.setTooltiptext(templateNamesArr[i]);
			li.addEventListener("onClick", this);
			li.setParent(digitalTemplatesListId);
		}

		// Set My templates to the list
		List<DigitalReceiptMyTemplate> myTemplatesList = digiRecptMyTemplatesDao.findAllByUserId(GetUser.getUserId());

		if (myTemplatesList != null && myTemplatesList.size() > 0) {
			
			for (DigitalReceiptMyTemplate digitalReceiptMyTemplate : myTemplatesList) {
				
				Listcell lc = new Listcell();
				li = new Listitem();
				Label label = new Label(digitalReceiptMyTemplate.getName());
				label.setStyle("width: 90%; display: inline-block;");
				lc.appendChild(label);
				//lc.setLabel(digitalReceiptMyTemplate.getName());
				//li = new Listitem();
				lc.setParent(li);
				li.setValue(digitalReceiptMyTemplate.getName());
				li.setTooltiptext(digitalReceiptMyTemplate.getName());
				li.setAttribute("myTemplateObj", digitalReceiptMyTemplate);
				li.addEventListener("onClick", this);
				li.setParent(myTemplatesListId);
			}
		}

		String selectedTemplate = digitalReceiptUserSettingsDao.findUserSelectedTemplate(GetUser.getUserId());

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
		//digitalTemplatesListId.setSelectedIndex(0);
		if(myTemplatesListId.getItemCount() > 0 ){
			myTemplatesListId.setSelectedIndex(0);
			setEditorContent(myTemplatesListId.getSelectedItem());
		}

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
		
		/*if(isAdmin) {
			EditorController.getCouponsList();
		}*/

		defaultDigiSettings();

		/*String company = currentUser.getCompanyName();
		logger.info("company name is "+company);

		if (company != null) {
			cFromNameTb.setValue(company);
		}*/
		manageActionsImg.addEventListener("onClick", this);
		
		session.setAttribute("EditorType","ckEditor");
		// Clients.evalJavaScript("var ocCouponsArr = [];");
		Clients.evalJavaScript("var ocImageCouponsArr = [];");
	}

	// private String[] allPlaceHoldersArr;

	private void setEditorContent(Listitem li) {

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
						
						Utility.sendInstantMail(null, cSubTb.getValue(), digitalRecieptsCkEditorId.getValue(),
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
		
		
		editorContent = editorContent.replace("<!--##BEGIN ITEMS##-->","##BEGIN ITEMS##")
									 .replace("<!--##END ITEMS##-->","##END ITEMS##");
		editorContent = editorContent.replace("<!--##BEGIN PAYMENTS##-->","##BEGIN PAYMENTS##")
									 .replace("<!--##END PAYMENTS##-->","##END PAYMENTS##");

		String templateName = winId$templatNameTbId.getValue();
		digitalReceiptMyTemplate = new DigitalReceiptMyTemplate(templateName, editorContent,Calendar.getInstance(), GetUser.getUserId());

		winId.setVisible(false);
		digitalReceiptMyTemplate.setModifiedDate(Calendar.getInstance());
		//digiRecptMyTemplatesDao.saveOrUpdate(digitalReceiptMyTemplate);
		digiRecptMyTemplatesDaoForDML.saveOrUpdate(digitalReceiptMyTemplate);
		MessageUtil.setMessage("Template '" + templateName + "' saved successfully.", "green", "top");
		
		tabBoxId.setSelectedIndex(0);
		onClick$tabBoxId();
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

	private Window previewWin;
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
		/*if (event.getTarget() instanceof Toolbarbutton) {
			Listitem li = (Listitem) ((Toolbarbutton) event.getTarget())
					.getParent().getParent();
			EmailQueue emailQueue = (EmailQueue) li
					.getAttribute("digiRcptEmailQueueObj");
			if (emailQueue != null) {
				String emailContent = emailQueue.getMessage();
				previewWin$html.setContent(emailContent);
				previewWin.setVisible(true);
			}
		}*/

	}

	@Override
	public void render(Listitem li, Object arg1, int arg2) throws Exception {
		// TODO Auto-generated method stub
		String tempStr = (String) arg1;
		li.setLabel(tempStr);
		li.setValue(tempStr);

		logger.info("LI VAL :" + li + " tempStr=" + tempStr + " arg2 "+ arg2);
	}
	
	// config to sys temp
	public void onClick$sysTemplateBtnId() {
		try {
			String templateName = digitalTemplatesListId.getSelectedItem().getValue();
			templateName = SYSTEM_TEMPLATE + templateName;
			String val = digitalReceiptUserSettingsDao.findUserSelectedTemplate(GetUser.getUserId());
			if (val == null) {
				//digitalReceiptUserSettingsDao.addDigiRecptUserSelectedTemplate(GetUser.getUserId(), templateName);
				digitalReceiptUserSettingsDaoForDML.addDigiRecptUserSelectedTemplate(GetUser.getUserId(), templateName);
			} else {
				//digitalReceiptUserSettingsDao.updateDigiUserSettings(GetUser.getUserId(),templateName);
				digitalReceiptUserSettingsDaoForDML.updateDigiUserSettings(GetUser.getUserId(),templateName);

			}
			
			String isValidPhStr = Utility.validatePh(digitalRecieptsCkEditorId.getValue(), currentUser);
			
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
			
			
			selectedTemplateLblId.setValue(templateName);
			selectedTemplateLblId.setAttribute("templateName", templateName);
			MessageUtil.setMessage("This template has been configured\n sucessfully. All future digital receipts\n will be sent with this template.",
					"green", "top");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}

	// config to my temp
	public void onClick$myTemplateBtnId() {

		try {

			logger.info("digi List :"+ myTemplatesListId.getSelectedIndex());
			if(myTemplatesListId.getSelectedItem()==null){
				MessageUtil.setMessage("No template found to save as my choice.", "color:blue");
				return;
			}
			String templateName = myTemplatesListId.getSelectedItem().getValue();

			templateName = MY_TEMPLATE + templateName;
			String val = digitalReceiptUserSettingsDao.findUserSelectedTemplate(GetUser.getUserId());
			if (val == null) {
				//digitalReceiptUserSettingsDao.addDigiRecptUserSelectedTemplate(GetUser.getUserId(), templateName);
				digitalReceiptUserSettingsDaoForDML.addDigiRecptUserSelectedTemplate(GetUser.getUserId(), templateName);
			} else {
				//digitalReceiptUserSettingsDao.updateDigiUserSettings(GetUser.getUserId(), templateName);
				digitalReceiptUserSettingsDaoForDML.updateDigiUserSettings(GetUser.getUserId(), templateName);
			}
			
			String isValidPhStr = Utility.validatePh(digitalRecieptsCkEditorId.getValue(), currentUser);
			
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
			
			selectedTemplateLblId.setValue(templateName);
			selectedTemplateLblId.setAttribute("templateName", templateName);
			MessageUtil.setMessage("This template has been configured\n sucessfully. All future digital receipts\n will be sent with this template.",
					"green", "top");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}

	public void onClick$configTemplateBtnId() {

		if (tabBoxId.getSelectedIndex() == 0) {

			onClick$myTemplateBtnId();
		} else if (tabBoxId.getSelectedIndex() == 1) {

			onClick$sysTemplateBtnId();
			
		}
	}

	private Toolbarbutton updateTemplateAId;
	private Toolbarbutton submitBtnId;

	public void onClick$updateTemplateAId() {

		if (tabBoxId.getSelectedIndex() == 0) {

			DigitalReceiptMyTemplate digitalReceiptMyTemplate = null;
			logger.info("============"+myTemplatesListId.getSelectedItem());
			if(myTemplatesListId.getSelectedItem()!=null)
			digitalReceiptMyTemplate = (DigitalReceiptMyTemplate) myTemplatesListId.getSelectedItem().getAttribute("myTemplateObj");
			else{
				MessageUtil.setMessage("No template found in the editor to save.", "color:blue");
				return;
			}
			if (digitalReceiptMyTemplate != null) {
				
			//	DRSentDao drSentDao = (DRSentDao)SpringUtil.getBean("drSentDao");
				int configCount = drSentDao.findConfiguredTemplateCount(currentUser.getUserId(),digitalReceiptMyTemplate.getName() );
				
				
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
				
				if(configCount > 0) {
					
					int confirm = Messagebox.show("Changes in the template will be reflected in all digital " +
							"receipts sent from now. \n Do you want to continue?", "Update Template",
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					
					
					if(confirm != 1) return;
					
					
					
				}
				
				editorContent = editorContent.replace("<!--##BEGIN ITEMS##-->","##BEGIN ITEMS##")
											 .replace("<!--##END ITEMS##-->","##END ITEMS##");
				editorContent = editorContent.replace("<!--##BEGIN PAYMENTS##-->", "##BEGIN PAYMENTS##")
											 .replace("<!--##END PAYMENTS##-->", "##END PAYMENTS##");

				digitalReceiptMyTemplate.setContent(editorContent);
				digitalReceiptMyTemplate.setModifiedDate(Calendar.getInstance());
				//digiRecptMyTemplatesDao.saveOrUpdate(digitalReceiptMyTemplate);
				digiRecptMyTemplatesDaoForDML.saveOrUpdate(digitalReceiptMyTemplate);
				MessageUtil.setMessage("Template modified successfully.","green", "top");
			} else {
				MessageUtil.setMessage("Failed to update template.", "red","top");
			}
			
			Redirect.goTo(PageListEnum.EMPTY);
			Redirect.goTo(PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS);
		}

	}

	public void onClick$tabBoxId() {

		try {
			logger.info("--Just Entered ---");

			if (tabBoxId.getSelectedIndex() == 0) { // My templates

				Components.removeAllChildren(myTemplatesListId);
				// Set My templates to the list
				List<DigitalReceiptMyTemplate> myTemplatesList = digiRecptMyTemplatesDao.findAllByUserId(GetUser.getUserId());

				if (myTemplatesList == null || myTemplatesList.size() < 1) {
					logger.info(">>>> No Mytemplates Exist ...");
					setEditorContent(new Listitem());
					return;
				}

				Listitem li;
				for (DigitalReceiptMyTemplate digitalReceiptMyTemplate : myTemplatesList) {
					
					Listcell lc = new Listcell();
					li = new Listitem();
					Label label = new Label(digitalReceiptMyTemplate.getName());
					label.setStyle("width: 90%; display: inline-block;");
					lc.appendChild(label);
					
					//Listcell lc = new Listcell();
					//lc.setLabel(digitalReceiptMyTemplate.getName());
					//li = new Listitem();
					lc.setParent(li);
					li.setValue(digitalReceiptMyTemplate.getName());
					li.setTooltiptext(digitalReceiptMyTemplate.getName());
					li.setAttribute("myTemplateObj", digitalReceiptMyTemplate);
					li.addEventListener("onClick", this);
					li.setParent(myTemplatesListId);
				}

				myTemplatesListId.setSelectedIndex(0);
				
				setEditorContent(myTemplatesListId.getSelectedItem());
				updateTemplateAId.setVisible(true);
				submitBtnId.setVisible(false);

			} else if (tabBoxId.getSelectedIndex() == 1) { // system

				Listitem li;
				Components.removeAllChildren(digitalTemplatesListId);
				
				for (int i = 0; i < templateNamesArr.length; i++) {
					logger.debug("Template Name : " + templateNamesArr[i]);
					Listcell lc = new Listcell();
					lc.setLabel(templateNamesArr[i]);
					li = new Listitem();
					lc.setParent(li);
					li.setTooltiptext(templateNamesArr[i]);
					li.setValue(templateNamesArr[i]);
					li.addEventListener("onClick", this);
					li.setParent(digitalTemplatesListId);
				}

				digitalTemplatesListId.setSelectedIndex(0);
				updateTemplateAId.setVisible(false);
				submitBtnId.setVisible(true);
				setEditorContent(digitalTemplatesListId.getSelectedItem());
			} 
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}

	

	public void defaultDigiSettings() {


		List<UserFromEmailId> userFromEmailIdList = userFromEmailIdDao.getEmailsByUserId(currentUser.getUserId());
		logger.info("List size : " + userFromEmailIdList.size());
		
		Components.removeAllChildren(cFromEmailCb);
        
		
		
		List<String> listOfEmailIds = new ArrayList<String>();
		
		for(UserFromEmailId anUserFromEmailId: userFromEmailIdList){
			if(anUserFromEmailId.getEmailId() != null){
				
				if( !listOfEmailIds.contains(anUserFromEmailId.getEmailId())){
					listOfEmailIds.add(anUserFromEmailId.getEmailId());
				}
			}
		}
		
		if (currentUser != null && currentUser.getEmailId() != null) {
			cFromEmailCb.appendItem(currentUser.getEmailId());

		}

		if (listOfEmailIds.size() > 0) {
			for (Object obj : listOfEmailIds) {
				String anEmailId = (String) obj;
				cFromEmailCb.appendItem(anEmailId);

			}
		}
		
		
		/*if (userFromEmailIdList.size() > 0) {
			for (Object obj : userFromEmailIdList) {
				UserFromEmailId userFromEmailId = (UserFromEmailId) obj;
				cFromEmailCb.appendItem(userFromEmailId.getEmailId());

			}
		}*/

		if (!(cFromEmailCb.getItemCount() > 0)) {
			cFromEmailCb.appendItem("No emails registered.");

		}
		cFromEmailCb.setSelectedIndex(0);

		
		cFromNameTb.setValue(currentUser.getCompanyName());
		
		DigitalReceiptUserSettings digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(currentUser.getUserId());
		
		if(digitalReceiptUserSettings == null) {
			
			return;
		}
		
		// default  user subject 
		if(digitalReceiptUserSettings.getSubject() != null){
			cSubTb.setValue(digitalReceiptUserSettings.getSubject());
		}
		
		if(digitalReceiptUserSettings.getFromName() != null){
			cFromNameTb.setValue(digitalReceiptUserSettings.getFromName());
		}
			
		String fromEmailId = digitalReceiptUserSettings.getFromEmail();
		for (int index = 0; index < cFromEmailCb.getItemCount(); index++) {

			// logger.debug(cFromEmailCb.getItemAtIndex(index).getLabel() +
			// " == " + fromEmailId);
			if (cFromEmailCb.getItemAtIndex(index).getLabel().equals(fromEmailId)) {
				cFromEmailCb.setSelectedIndex(index);
			}
		}
		
		enableSendingChkBxId.setChecked(digitalReceiptUserSettings.isEnabled());
		shippingAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeShipping());
		feeAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeFee());
		taxAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeTax());
		discAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeGlobalDiscount());
		totalAmntChkBxId.setChecked(digitalReceiptUserSettings.isIncludeTotalAmount());
		
		
		frmEmaildynamicOrNotRgId.setSelectedIndex(digitalReceiptUserSettings.isIncludeDynamicFrmEmail() ? 1 : 0);
		
		frmNamedynamicOrNotRgId.setSelectedIndex(digitalReceiptUserSettings.isIncludeDynamicFrmName() ? 1 : 0);
		
		// web page version
		cWebPageCb.setChecked(digitalReceiptUserSettings.isWebLinkFlag());
		if (digitalReceiptUserSettings.isWebLinkFlag()) {
			cWebLinkHboxId.setVisible(true);
			cWebLinkTextTb
					.setValue(digitalReceiptUserSettings.getWebLinkText());
			cWebLinkUrlTextTb.setValue(digitalReceiptUserSettings.getWebLinkUrlText());
		} else {
			cWebLinkHboxId.setVisible(false);

		}
		// permission reminder
		cPermRemRb.setSelectedIndex(digitalReceiptUserSettings.isPermissionRemainderFlag() ? 0 : 1);
		if (digitalReceiptUserSettings.isPermissionRemainderFlag()) {
			permRemDivId.setVisible(true);
			permRemTextId.setValue(digitalReceiptUserSettings.getPermissionRemainderText());
		} else {

			permRemDivId.setVisible(false);
		}

		// personalize to field

		toNameChkId.setChecked(digitalReceiptUserSettings.isPersonalizeTo());
		if (digitalReceiptUserSettings.isPersonalizeTo()) {
			persToDivId.setVisible(true);
			String to = digitalReceiptUserSettings.getToName();
			for (int i = 0; i < phArray.length; i++) {
				if (phArray[i].equalsIgnoreCase(to)) {
					phLbId.setSelectedIndex(i);
				}
			}
		} else {
			persToDivId.setVisible(false);

		}

	}// defaultDigiSettings()

	public void onClick$regFrmEmlAnchId() {

		regEmailPopupId.setAttribute("flagStr", "From-Email");
	}
	
	public void onClick$manageStoresLink1AnchId() {
		String redirectToStr = PageListEnum.USERADMIN_ORG_STORES.getPagePath();
		
		PageUtil.setFromPage(PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS.getPagePath());
		Redirect.goTo(redirectToStr);
	}
	
	public void onClick$manageStoresLink2AnchId() {
		onClick$manageStoresLink1AnchId();
	}

	Popup help, help1;

	public void onFocus$cSubTb() {

		// cSubTb.setPopup(help);
		help.open(cSubTb, "end_after");

	}

	public void onClick$cancelBtnId() {

		regEmailPopupId.close();

	}

	public void onClick$saveBtnId() {
		//myTemplatesListId,
		
	DigitalReceiptUserSettings digitalReceiptUserSettings  = digitalReceiptUserSettingsDao.findByUserId(currentUser.getUserId());
	
	if(digitalReceiptUserSettings == null) {
		
		digitalReceiptUserSettings = new DigitalReceiptUserSettings();
		
	}
   // check for subject and it is valid or not 
	if(cSubTb.isValid() && !cSubTb.getValue().isEmpty()){
		
		digitalReceiptUserSettings.setSubject(cSubTb.getValue());
	}
	else{
		MessageUtil.setMessage("Please provide subject. Subject should not be left empty.", "color:red", "TOP");
		cSubTb.setFocus(true);
		return;
	}

	//  check for from name n it is valid or not
	if( cFromNameTb.isValid() &&  Utility.validateFromName(cFromNameTb.getValue())){
		
		if(frmNamedynamicOrNotRgId.getSelectedIndex()==0 )
				digitalReceiptUserSettings.setFromName(cFromNameTb.getValue());
	}
	else{
		MessageUtil.setMessage("Provide valid 'From Name'. Special characters are not allowed.", "color:red", "TOP");
		cFromNameTb.setFocus(true);
		return;
	}
	
	// check for from email address n display all the fromEmails n those fromEmails are valid or not 
	
	
	if(cFromEmailCb.getSelectedItem().getLabel().indexOf('@') < 0) {
		MessageUtil.setMessage("Register a 'From Email' to create an email.","color:red", "TOP");
	}
	else if(!(cFromEmailCb.getSelectedIndex()==-1)) {
		if(frmEmaildynamicOrNotRgId.getSelectedIndex() == 0)
				digitalReceiptUserSettings.setFromEmail(cFromEmailCb.getSelectedItem().getLabel());
	}
	else {
		MessageUtil.setMessage("Provide valid 'From Email'.", "color:red", "TOP");
		cFromEmailCb.setFocus(true);
		return;
	}
	
	digitalReceiptUserSettings.setEnabled(enableSendingChkBxId.isChecked());
	digitalReceiptUserSettings.setIncludeShipping(shippingAmntChkBxId.isChecked());
	digitalReceiptUserSettings.setIncludeFee(feeAmntChkBxId.isChecked());
	digitalReceiptUserSettings.setIncludeTax(taxAmntChkBxId.isChecked());
	digitalReceiptUserSettings.setIncludeGlobalDiscount(discAmntChkBxId.isChecked());
	digitalReceiptUserSettings.setIncludeTotalAmount(totalAmntChkBxId.isChecked());
	
	
	int selIndxEml = frmEmaildynamicOrNotRgId.getSelectedIndex();
	int selIndxName = frmNamedynamicOrNotRgId.getSelectedIndex();
	
	digitalReceiptUserSettings.setIncludeDynamicFrmEmail(selIndxEml == 0 ? false : true);
	digitalReceiptUserSettings.setIncludeDynamicFrmName(selIndxName == 0 ? false : true );


	
	// ***** optional settings **************
	
	//web page version 
	
	if(cWebPageCb.isChecked()){
		digitalReceiptUserSettings.setWebLinkFlag(true);
		digitalReceiptUserSettings.setWebLinkText(cWebLinkTextTb.getValue());
		if(!cWebLinkUrlTextTb.getValue().equals("")){
			digitalReceiptUserSettings.setWebLinkUrlText(cWebLinkUrlTextTb.getValue());
		}else{
			MessageUtil.setMessage("Provide web-link Url text.", "color:red", "TOP");
			cWebLinkUrlTextTb.setFocus(true);
			return;
		}
	}else{
		digitalReceiptUserSettings.setWebLinkFlag(false);
	}
	
	//permission reminder 
	
	if(cPermRemRb.getSelectedIndex() == 0){
		if(!permRemTextId.getValue().trim().equals("")){
       digitalReceiptUserSettings.setPermissionRemainderFlag(true);
       digitalReceiptUserSettings.setPermissionRemainderText(permRemTextId.getValue());
		}
	}else{
		digitalReceiptUserSettings.setPermissionRemainderFlag(false);
	}
	
	//personalize to field
	
	if(toNameChkId.isChecked()){
		digitalReceiptUserSettings.setPersonalizeTo(true);
		if(phLbId.getSelectedIndex()>0){
			digitalReceiptUserSettings.setToName((String)phLbId.getSelectedItem().getValue());
		}else{
			phLbId.setSelectedIndex(0);
			digitalReceiptUserSettings.setToName((String)phLbId.getSelectedItem().getValue());
		}
	}else{
		digitalReceiptUserSettings.setPersonalizeTo(false);
	}
	MessageUtil.clearMessage();
	

	
		
		digitalReceiptUserSettings.setSubject(cSubTb.getValue().trim());
		
		if(frmNamedynamicOrNotRgId.getSelectedIndex()==0)
			digitalReceiptUserSettings.setFromName(cFromNameTb.getValue().trim());
		
		
		digitalReceiptUserSettings.setUserId(currentUser.getUserId());
		
		if(frmEmaildynamicOrNotRgId.getSelectedIndex()==0)
			digitalReceiptUserSettings.setFromEmail(cFromEmailCb.getValue().trim());
		
		digitalReceiptUserSettings.setCreatedDate(Calendar.getInstance());
		
	
		
		//Set The Template Name
		logger.info("===============================digitalReceiptUserSettings.getSelectedTemplateName()        "+digitalReceiptUserSettings.getSelectedTemplateName());
		if(digitalReceiptUserSettings.getSelectedTemplateName() != null) {
			
			String selTemplateName = ((String)selectedTemplateLblId.getAttribute("templateName")).trim();
			if(selTemplateName.equals(DEFAULT_TEMPLATE_NAME)){
				
				digitalReceiptUserSettings.setSelectedTemplateName(SYSTEM_TEMPLATE+digitalTemplatesListId.getSelectedItem().getValue());
			}
			else{
				
				digitalReceiptUserSettings.setSelectedTemplateName(selTemplateName);
				
			}
			
		}
		else{
			MessageUtil.setMessage("To enable digital receipts, you would need to first create a template and save it as your choice.","color:blue;");
			enableSendingChkBxId.setChecked(false);
		return;
		}
		try {
			int confirm = Messagebox.show(
					"Are you sure you want to save the digital receipt settings?", "Prompt",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

			if (confirm == Messagebox.OK) {

				try {
					//digitalReceiptUserSettingsDao.saveOrUpdate(digitalReceiptUserSettings);
					digitalReceiptUserSettingsDaoForDML.saveOrUpdate(digitalReceiptUserSettings);

				}catch(Exception e){
					
					logger.error("Exception ::", e);
				}
				MessageUtil.setMessage("Digital receipt settings saved successfully.","color:green;");

	
			}
	
	
	
		}catch(Exception e1){
			logger.error("Exception ::", e1);
		}
	}
	
	
	public void onClick$submitBt1nId() {
		try {
			
			if(frmEmaildynamicOrNotRgId.getSelectedIndex() == 0){
				registerNewFromEmail(cFromEmailTb,(String)regEmailPopupId.getAttribute("flagStr"));
			}
			
			
		} catch (Exception e) {
			
			logger.error("Exception ::", e);
		}
	}
	
	
	public void registerNewFromEmail(Textbox cFromEmailTb,String flagStr) throws Exception {
		String newFromEmail = cFromEmailTb.getValue();
		
		if(newFromEmail.trim().equals("")){
			regEmailPopupId.close();
			MessageUtil.setMessage("Email field cannot be left empty.", "color:red", "TOP");
			return;
		}
		
		if(!Utility.validateEmail(newFromEmail.trim())) {
			regEmailPopupId.close();
			MessageUtil.setMessage("Please enter a valid email address.", "color:red", "TOP");
			cFromEmailTb.setValue("");
			return;
	 	}
		
		try {
			if(newFromEmail.equalsIgnoreCase(currentUser.getEmailId())) {
				MessageUtil.setMessage("Given email address already exists.", "color:red", "TOP");
				cFromEmailTb.setValue("");
				return;
			}
			UserFromEmailIdDao userFromEmailIdDao = (UserFromEmailIdDao)SpringUtil.getBean("userFromEmailIdDao");
			UserFromEmailIdDaoForDML userFromEmailIdDaoForDML = (UserFromEmailIdDaoForDML)SpringUtil.getBean("userFromEmailIdDaoForDML");
			EmailQueueDao emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
			UserFromEmailId userFromEmailId =  userFromEmailIdDao.checkEmailId(newFromEmail, currentUser.getUserId());
			String confirmationURL = PropertyUtil.getPropertyValue("confirmationURL") + "?requestedAction=fromEmail&userId=" + currentUser.getUserId() + "&email=" + newFromEmail;
			if(userFromEmailId == null) {
				userFromEmailId = new UserFromEmailId(currentUser, newFromEmail, 0);
				//userFromEmailIdDao.saveOrUpdate(userFromEmailId);
				userFromEmailIdDaoForDML.saveOrUpdate(userFromEmailId);

				addVerificationMailToQueue(flagStr, confirmationURL, newFromEmail);
				
				/*String emailStr = "Hi " + user.getFirstName() + ",<br/> You recently added a new email address  to your "+flagStr+".<br/>" 
						+ " To verify that you own this email address, simply click on the link below :<br/> " 
						+ "  <a href='"+ confirmationURL +"'>"+ confirmationURL + "</a>" 
						+ "<br/><br/> If you can't click the link, you can verify your email address by copy/paste(or typing) "
						+ "the above URL address into the browser.<br/>Regards,<br/>The Captiway Team";
				EmailQueue emailQueue = new EmailQueue("Captiway - Register new "+flagStr,emailStr, Constants.EQ_TYPE_USER_MAIL_VERIFY,"Active",newFromEmail,MyCalendar.getNewCalendar(),user);
			 	emailQueueDao.saveOrUpdate(emailQueue);
				regEmailPopupId.close();
				MessageUtil.clearMessage();
				Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n Follow the instructions in the email and this new "+flagStr+" will be verified.");
				cFromEmailTb.setValue("");*/
			} else if(userFromEmailId.getStatus() == 0) {
				
				//MessageUtil.setMessage("Given email ID approval is pending.", "color:red", "TOP");
				 try {
					int confirm = Messagebox.show("The given email address is pending for approval . Do you want to resend the verification?","Send Verification ?",
						 		Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1) {
						regEmailPopupId.close();
						cFromEmailTb.setValue("");
						return ;
					}
				} catch (Exception e) {
					logger.error("Exception ::", e);
				}
				
				addVerificationMailToQueue(flagStr, confirmationURL, newFromEmail);
				
				/*String emailStr = "Hi " + user.getFirstName() + ",<br/> You recently added a new email address  to your "+flagStr+".<br/>" 
				+ " To verify that you own this email address, simply click on the link below :<br/> " 
				+ "  <a href='"+ confirmationURL +"'>"+ confirmationURL + "</a>" 
				+ "<br/><br/> If you can't click the link, you can verify your email address by copy/paste(or typing) "
				+ "the above URL address into the browser.<br/>Regards,<br/>The Captiway Team";
				EmailQueue emailQueue = new EmailQueue("Captiway - Register new "+flagStr,emailStr, Constants.EQ_TYPE_USER_MAIL_VERIFY,"Active",newFromEmail,MyCalendar.getNewCalendar(),user);
			 	emailQueueDao.saveOrUpdate(emailQueue);
				regEmailPopupId.close();
				MessageUtil.clearMessage();
				Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n Follow the instructions in the email and this new "+flagStr+" will be verified.");
				cFromEmailTb.setValue("");
				*/
			} 
			else {
				regEmailPopupId.close();
				MessageUtil.setMessage("Given email address already exists.", "color:red", "TOP");
				cFromEmailTb.setValue("");
			}
		} catch (Exception e) {
			regEmailPopupId.close();
			//logger.error("Exception :"+e);
		}
	}// registerNewFromEmail()

	private void addVerificationMailToQueue(String flagStr, String confirmationURL, String newFromEmail) {
		
		try {
			String branding = GetUser.getUserObj().getUserOrganization().getBranding();

			String brandStr="OptCulture";
			if(branding!=null && branding.equalsIgnoreCase("CAP")) {
				brandStr="Captiway";
			}
			
			String emailStr = "Hi " + currentUser.getFirstName() + ",<br/> You recently added a new email address  to your "+flagStr+".<br/>" 
			+ " To verify that you own this email address, simply click on the link below :<br/> " 
			+ "  <a href='"+ confirmationURL +"'>"+ confirmationURL + "</a>" 
			+ "<br/><br/> If you can't click the link, you can verify your email address by copy/paste(or typing) "
			+ "the above URL address into the browser.<br/>Regards,<br/>The "+brandStr+" Team";
			
			
			
			Utility.sendInstantMail(null,brandStr+" - Register new "+flagStr, emailStr,
					Constants.EQ_TYPE_USER_MAIL_VERIFY,newFromEmail, null);
			/*EmailQueue emailQueue = new EmailQueue(brandStr+" - Register new "+flagStr,emailStr, Constants.EQ_TYPE_USER_MAIL_VERIFY,"Active",newFromEmail,MyCalendar.getNewCalendar(),currentUser);
			emailQueueDao.saveOrUpdate(emailQueue);*/
			regEmailPopupId.close();
			MessageUtil.clearMessage();
			Messagebox.show("We have sent you a confirmation email to "+newFromEmail+".\n " +
					"Follow the instructions in the email and this new "+flagStr+" will be verified.", "Register email", Messagebox.OK, Messagebox.INFORMATION);
			cFromEmailTb.setValue("");
		} catch (WrongValueException e) {
			logger.error("Exception ::", e);
		}catch (Exception e) {

	//	logger.error("** Exception while sending verification",e);
		
		
		}
		
		
	}
	
// DR reports moved to reports menu
	
	/*private Include viewReportsIncId;
	public void onClick$digiRcptReportsTabId() {
//		session.setAttribute("viewType", "contact");
	viewReportsIncId.setSrc(null);
	viewReportsIncId.setSrc("/zul/campaign/drReport.zul");
	//Redirect.goTo(PageListEnum.CAMPAIGN_DIGITAL_RECEIPTS_REPORTS);
		
		
	}
	*/
	
	
	public void onClick$deleteTemplateMId(){


		if (tabBoxId.getSelectedIndex() == 0) {

			DigitalReceiptMyTemplate digitalReceiptMyTemplate;
			digitalReceiptMyTemplate = (DigitalReceiptMyTemplate) myTemplatesListId.getSelectedItem().getAttribute("myTemplateObj");

			if (digitalReceiptMyTemplate != null) {
				long count=digitalReceiptUserSettingsDao.findTemplateByName(currentUser.getUserId(),digitalReceiptMyTemplate.getName());
				if(count>0){
					MessageUtil.setMessage("This template can't be deleted as it is configured to be sent as digital receipt.", "blue","top");
					return;
				}
				int confirm = Messagebox.show("Do you want to delete the template '" + myTemplatesListId.getSelectedItem().getValue() + "' permanently?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != 1) return;
					
				//digiRecptMyTemplatesDao.delete(digitalReceiptMyTemplate);
				digiRecptMyTemplatesDaoForDML.delete(digitalReceiptMyTemplate);
				String selectedTemplate = digitalReceiptUserSettingsDao.findUserSelectedTemplate(GetUser.getUserId());
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
				
				MessageUtil.setMessage("Template deleted successfully.","green", "top");	
				 onClick$tabBoxId();
					
				}
				
			 else {
					MessageUtil.setMessage("Failed to delete the template.", "red","top");
				}
				
				
			}
		}//onClick$deleteTemplateMId()
	
	
public void onClick$viewDetailsMId(){
	DigitalReceiptMyTemplate  digitalReceiptMyTemplate =(DigitalReceiptMyTemplate)myTemplatesListId.getSelectedItem().getAttribute("myTemplateObj");
	
	viewDetailsWinId$templateNameLblId.setValue(digitalReceiptMyTemplate.getName());
	
	
	String createdDateLbl = MyCalendar.calendarToString(digitalReceiptMyTemplate.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR, clientTimeZone);
	viewDetailsWinId$creationDateLblId.setValue(createdDateLbl);
    
	String modifedDateLbl;
	if(digitalReceiptMyTemplate.getModifiedDate() != null){
		modifedDateLbl = MyCalendar.calendarToString(digitalReceiptMyTemplate.getModifiedDate(),MyCalendar.FORMAT_DATETIME_STYEAR, clientTimeZone);
	}
	else{
		modifedDateLbl ="---";
	}
		
	
	viewDetailsWinId$lastAccLblId.setValue(modifedDateLbl);
	
	viewDetailsWinId.doHighlighted();
	}//onClick$viewDetailsMId()

    public void onClick$saveAsMId(){
    	onSave=false;
    	makeACopyWinId.setTitle("Save As");
    	onSave=true;
    	onClick$makeCopyMId();
    	
    }
    public void onClick$makeCopyMId(){
    	if(onSave == false){
    		makeACopyWinId.setTitle("Make A Copy");
    	}
    	String ckEditorContent = digitalRecieptsCkEditorId.getValue();
		if (ckEditorContent == null || ckEditorContent.length() < 1) {
			MessageUtil.setMessage("Please provide content to save.", "red","top");
			onSave=false;
			return;
		}
		
		 String isValidPhStr = Utility.validatePh(ckEditorContent, currentUser);
		
		 if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
				onSave=false;
				return ;
			}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
				
				MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
				onSave=false;
				return;
				
			}
		 
		 String isValidCouponAndBarcode = null;
			isValidCouponAndBarcode = Utility.validateCCPh(ckEditorContent, currentUser);
			if(isValidCouponAndBarcode != null){
				onSave=false;
				return ;
			}
			// Bar code validation
			/*String isValidCCDim = null;
			isValidCCDim = Utility.validateCouponDimensions(editorContent);
			if(isValidCCDim != null){
				return ;
			}*/

		// winId.setVisible(true);
			makeACopyWinId$templatNameTbId.setValue("");
			makeACopyWinId.setVisible(true);
			makeACopyWinId.setPosition("center");
			makeACopyWinId.doHighlighted();
	
	}//onClick$makeCopyMId()
    public void onClick$sendTestReceiptMId(){
    	onClick$sendTestBtnId();
	}//onClick$sendTestReceiptMId()
    
    
    public void onClick$saveAsMyChoiceMId(){
    	onClick$configTemplateBtnId();
    }//onClick$saveAsMyChoiceMId()
    
    
    
    
    public void onClick$saveInMyTempBtnId$makeACopyWinId() {

    	makeACopyWinId.setVisible(true);
    	if (makeACopyWinId$templatNameTbId.getValue().trim().equals("")) {
			MessageUtil.setMessage("Template name cannot be left empty.", "red","top");
			onSave=false;
			return;
		}

		DigitalReceiptMyTemplate digitalReceiptMyTemplate = digiRecptMyTemplatesDao.findByUserNameAndTemplateName(GetUser.getUserId(),makeACopyWinId$templatNameTbId.getValue());

		if (digitalReceiptMyTemplate != null) {

			MessageUtil.setMessage("Template name already exists, please choose another name.","red", "top");
			onSave=false;
			return;
		}

		logger.info("saving the object");

		// Replace PH comments
		String editorContent = digitalRecieptsCkEditorId.getValue();
		
		String isValidPhStr = Utility.validatePh(editorContent, currentUser);
		
		if(isValidPhStr != null && !isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("Invalid Placeholder: "+isValidPhStr, "red");
			onSave=false;
			return ;
		}else if(isValidPhStr != null && isValidPhStr.equals("GEN_updatePreferenceLink")){
			
			MessageUtil.setMessage("You have currently disabled subscriber preference center setting.\n To continue, either enable this setting or remove \n 'Subscriber Preference Link' placeholder from your content.", "color:red");
			onSave=false;
			return;
			
		}
		
		
		String isValidCouponAndBarcode = null;
		isValidCouponAndBarcode = Utility.validateCCPh(digitalRecieptsCkEditorId.getValue(), currentUser);
		if(isValidCouponAndBarcode != null){
			onSave=false;
			return ;
		}
		// Bar code validation
		/*String isValidCCDim = null;
		isValidCCDim = Utility.validateCouponDimensions(editorContent);
		if(isValidCCDim != null){
			return ;
		}*/
		
		
		editorContent = editorContent.replace("<!--##BEGIN ITEMS##-->","##BEGIN ITEMS##")
									 .replace("<!--##END ITEMS##-->","##END ITEMS##");
		editorContent = editorContent.replace("<!--##BEGIN PAYMENTS##-->","##BEGIN PAYMENTS##")
									 .replace("<!--##END PAYMENTS##-->","##END PAYMENTS##");

		digitalReceiptMyTemplate = new DigitalReceiptMyTemplate(makeACopyWinId$templatNameTbId.getValue(), editorContent,Calendar.getInstance(), GetUser.getUserId());

		makeACopyWinId.setVisible(false);
		digitalReceiptMyTemplate.setModifiedDate(Calendar.getInstance());
		//digiRecptMyTemplatesDao.saveOrUpdate(digitalReceiptMyTemplate);
		digiRecptMyTemplatesDaoForDML.saveOrUpdate(digitalReceiptMyTemplate);
		MessageUtil.setMessage("Template saved successfully.", "green", "top");
		onSave=false;
		onClick$tabBoxId();
	}
	
	
    public void onClick$renameTemplateMId(){
    	//renameTemplateWinId$newTempltNameTbId.setValue("");
    	String selectedTemplateName;
    	selectedTemplateName = myTemplatesListId.getSelectedItem().getValue().toString();
    	
		renameTemplateWinId$newTempltNameTbId.setValue(selectedTemplateName);
    	renameTemplateWinId.setVisible(true);
    	renameTemplateWinId.setPosition("center");
    	renameTemplateWinId.doHighlighted();
    }//onClick$renameTemplateMId()
    
    
    public void onClick$cancelTempltBtnId$renameTemplateWinId(){
    	renameTemplateWinId.setVisible(false);
    }//onClick$renameTemplateMId()
    
    
    public void onClick$renameTempltBtnId$renameTemplateWinId(){
    	
    	
    	String currentConfiguredTemplate = digitalReceiptUserSettingsDao.findUserSelectedTemplate(GetUser.getUserId());
    	if(currentConfiguredTemplate != null){
    		currentConfiguredTemplate = currentConfiguredTemplate.substring(MY_TEMPLATE.length());
    	}
    	//String currentConfiguredTemplate = selectedTemplateLblId.getValue().substring(12);
    	//because from 12 onwards actual name starts, as length of MY_TEMPLATE: is 12. 
    	String selectedTemplateName = myTemplatesListId.getSelectedItem().getValue().toString();
    	
    	
    	if(renameTemplateWinId$newTempltNameTbId.getValue().trim().isEmpty()){
    		MessageUtil.setMessage("New template name can't be empty, please provide a name.","red", "top");
    		renameTemplateWinId$newTempltNameTbId.setFocus(true);
			return;
    	}
    	DigitalReceiptMyTemplate digitalReceiptMyTemplate = digiRecptMyTemplatesDao.findByUserNameAndTemplateName(GetUser.getUserId(),renameTemplateWinId$newTempltNameTbId.getValue().trim());
        if (digitalReceiptMyTemplate != null) {

			MessageUtil.setMessage("Template name already exists, please choose another name.","red", "top");
			return;
		}
        
        logger.info("-------------------->myTemplatesListId.getSelectedItem().toString().trim() = "+myTemplatesListId.getSelectedItem().getValue().toString().trim());
        
        try {
			String tempName="MY_TEMPLATE:"+myTemplatesListId.getSelectedItem().getValue().toString();
			Long userId=GetUser.getUserId();
			String now = MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR);
			//digiRecptMyTemplatesDao.setNewTemplateName(userId, renameTemplateWinId$newTempltNameTbId.getText().trim(), myTemplatesListId.getSelectedItem().getValue().toString(), now);
			digiRecptMyTemplatesDaoForDML.setNewTemplateName(userId, renameTemplateWinId$newTempltNameTbId.getText().trim(), myTemplatesListId.getSelectedItem().getValue().toString(), now);
			//digitalReceiptUserSettingsDao.setNewTemplateName(userId, renameTemplateWinId$newTempltNameTbId.getText().trim(),tempName);
			digitalReceiptUserSettingsDaoForDML.setNewTemplateName(userId, renameTemplateWinId$newTempltNameTbId.getText().trim(),tempName);

      					
			//drSentDao.setNewTemplateName(userId, renameTemplateWinId$newTempltNameTbId.getText().trim(),tempName);
			drSentDaoForDML.setNewTemplateName(userId, renameTemplateWinId$newTempltNameTbId.getText().trim(),tempName);
			
			renameTemplateWinId.setVisible(false);
			if(selectedTemplateName.equals(currentConfiguredTemplate))
				selectedTemplateLblId.setValue(renameTemplateWinId$newTempltNameTbId.getText().trim());
			
			MessageUtil.setMessage("Template name changed.","", "top");
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        onClick$tabBoxId();
    }//onClick$renameTempltBtnId$renameTemplateWinId()
	
	
	
	
	
	

}

