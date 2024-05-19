package org.mq.marketer.campaign.controller.campaign;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.DigitalReceiptMyTemplate;
import org.mq.marketer.campaign.beans.UserFromEmailId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.PrepareFinalHtml;
import org.mq.marketer.campaign.controller.gallery.MyTempEditorController;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DRSentDaoForDML;
import org.mq.marketer.campaign.dao.DigitalReceiptMyTemplatesDao;
import org.mq.marketer.campaign.dao.DigitalReceiptMyTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDaoForDML;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDao;
import org.mq.marketer.campaign.dao.UserFromEmailIdDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.dao.UsersDomainsDao;
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
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

public class EReceiptsBeeEditorController extends GenericForwardComposer implements EventListener {

	Listbox digitalTemplatesListId;
	String digitalRecieptTemplatesPath;
	CKeditor digitalRecieptsCkEditorId;
	DigitalReceiptMyTemplatesDao digiRecptMyTemplatesDao;
	DigitalReceiptMyTemplatesDaoForDML digiRecptMyTemplatesDaoForDML;
	MailingListDao mailingListDao;
	Window winId, testDRWinId, makeACopyWinId, viewDetailsWinId, renameTemplateWinId;
	Textbox winId$templatNameTbId, testDRWinId$testDRTbId;
	Tab myTemplatesTabId, digiRcptReportsTabId;
	Listbox digiRecptReportListId, myTemplatesListId,dispSBSLBoxId, dispStoreLBoxId, mytemplatesLbId, tempsettingsLbId;
	Label selectedTemplateLblId, testDRWinId$msgLblId, viewDetailsWinId$templateNameLblId,winId$errorMsgLblId,
	viewDetailsWinId$creationDateLblId, viewDetailsWinId$lastAccLblId,regFrmEmlAnchId,regRepEmlAnchId;
	
	// Listbox digiRcptSettingLstId1;
	Grid templateAndJSONMappingGridId;
	Button addFieldBtnId, saveBtnId;
	Textbox jsonKeyFieldTbxId;
	// Radiogroup templateTypeGrpId;
	Tabbox tabBoxId;
	Toolbarbutton sendTestBtnId,saveasdraftBtnId;


	final String  STORE_WISE_DR_SETTINGS_OBJ="DRObj";

	/**
	 * Digital Receipt settings
	 * 
	 */

	Image manageActionsImg = new Image("/img/theme/arrow.png");

	
	// digiRcptSettingLstId2,digiRcptSettingLstId3,digiRcptSettingLstId4,digiRcptSettingLstId5,
	// digiRcptSettingLstId6;

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	static final int DR_BARCODE_WIDTH = Integer.parseInt(PropertyUtil.getPropertyValue("drBarcodeWidth"));//get width from db application properties table
	static final int DR_BARCODE_HEIGHT = Integer.parseInt(PropertyUtil.getPropertyValue("drBarcodeHeight"));//get height from db application properties table

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
	private UsersDaoForDML usersDaoForDML;
	private Session session;
	private Menupopup manageActionsMpId;
	private boolean onSave;
	private OrganizationStoresDao organizationDao;
	TimeZone clientTimeZone ;
	private Groupbox enableStoreWiseGBID;
	private UsersDomainsDao domainDao;

	/*static final int DR_BARCODE_WIDTH = Integer.parseInt(PropertyUtil.getPropertyValue("drBarcodeWidth"));//get width from db application properties table
	static final int DR_BARCODE_HEIGHT = Integer.parseInt(PropertyUtil.getPropertyValue("drBarcodeHeight"));//get height from db application properties table
	 */
	//Editor

	Label autoSaveLbId,acclbId;
	Toolbarbutton beeSaveBtnId,submitBtnId;
	//private Toolbarbutton submitBtnId;

	private Timer autoSaveTimerId;
	private Textbox htmlTextBoxId,newLabelCreationWinId$newLabelName;
	private Textbox jsonTextBoxId;
	private Textbox caretPosSE, jsonAutoSaveTextBoxId,nextButtonJsonTextBoxId,autoSaveJsonTextBoxId,sendMethodHtmlTextBoxId,errormsg;
	private String beeHtmlContent,beeJsonContent,beeHTMLTestMailContent,beeAutosaveJson;

	private Listbox winId$myTempListId;

	private boolean next,isAutoSave,ismyTemplates,isSendTestMail,isSaveAsTemplate,publishandclose,newTemplate,saveasdraftAndClose,save,saveasdraft;

	private final String SAVE = "save";
	private String isFrom="";
	private String userName,beeclientkey;
	private Label tempLbName;

	DigitalReceiptMyTemplate digitalReceiptMyTemplateTest;

	Window testWinId;
	Label testWinId$msgLblId;
	Textbox emailNameTbId,testWinId$testTbId;

	Toolbarbutton saveInMyTemplateTB;

	private Button testWinId$sendTestMailBtnId;


	Boolean isEdit,isDraftEdit,initialAutoSave;
	String htmlStuff,jsonStuff,editorType;

	DigitalReceiptMyTemplate digitalReceiptMyTemplate;


	//public static Session autoSaveSession;
	private Session autoSaveSession;
	private String accessAccIds;
	private static String ITUser="ITUser";
	private Session sessionScope = null;
	private String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
	static final String APP_MAIN_URL = "https://qcapp.optculture.com/subscriber/";
	static final String APP_MAIN_URL_HTTP = "http://qcapp.optculture.com/subscriber/";
	static final String MAILCONTENT_URL = "http://mailcontent.info/subscriber/";
	static final String MAILHANDLER_URL = "http://mailhandler01.info/subscriber/";
	private static final String ImageServer_Url = PropertyUtil.getPropertyValue("ImageServerUrl");
	
	public EReceiptsBeeEditorController() {
		// TODO Auto-generated constructor stub

		session = Sessions.getCurrent();
		sessionScope = Sessions.getCurrent();
		autoSaveSession = Sessions.getCurrent();
		//PageUtil.setHeader("Digital Receipts", "", "", true);
		PageUtil.setHeader("Drag and Drop Editor", "", "", true);
		clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
		this.campaignsDao = (CampaignsDao) SpringUtil.getBean("campaignsDao");
		emailQueueDao = (EmailQueueDao) SpringUtil.getBean("emailQueueDao");
		usersDao = (UsersDao) SpringUtil.getBean("usersDao");
		drSentDao = (DRSentDao) SpringUtil.getBean("drSentDao");
		drSentDaoForDML = (DRSentDaoForDML) SpringUtil.getBean("drSentDaoForDML");
		digiRecptMyTemplatesDao = (DigitalReceiptMyTemplatesDao) SpringUtil.getBean("digitalReceiptMyTemplatesDao");
		digiRecptMyTemplatesDaoForDML = (DigitalReceiptMyTemplatesDaoForDML) SpringUtil.getBean("digitalReceiptMyTemplatesDaoForDML");
		mailingListDao = (MailingListDao) SpringUtil.getBean("mailingListDao");
		digitalReceiptUserSettingsDao= (DigitalReceiptUserSettingsDao) SpringUtil.getBean("digitalReceiptUserSettingsDao");
		digitalReceiptUserSettingsDaoForDML= (DigitalReceiptUserSettingsDaoForDML) SpringUtil.getBean("digitalReceiptUserSettingsDaoForDML");
		organizationDao=(OrganizationStoresDao) SpringUtil.getBean("organizationStoresDao");
		//venkata commented the line
		//configTemplateBtnId.setVisible(!currentUser.isEnableStoreWiseTemplates());
		this.domainDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
		this.userFromEmailIdDao = (UserFromEmailIdDao) SpringUtil.getBean("userFromEmailIdDao");
		this.userFromEmailIdDaoForDML = (UserFromEmailIdDaoForDML) SpringUtil.getBean("userFromEmailIdDaoForDML");
		this.usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean(OCConstants.USERS_DAOForDML);

	}


	public void onChange$nextButtonJsonTextBoxId() {
		logger.info("onchange$jsonTextBoxId");
		saveJSONHTMLContent();
	}

	public void onChange$errormsg() {
		//logger.info("hi i am printing");
		errormsg.setValue("");
		MessageUtil.setMessage("You have encountered an error viewing this page, please refresh to try again. \n If you continue having viewing difficulties, please submit a ticket to support@optculture.com", "color:blue");
	}

	public void onChange$jsonTextBoxId() {
		logger.info("onchange$jsonTextBoxId");
		saveJSONHTMLContent();
	}
	public void saveJSONHTMLContent(){
		//System.out.println("dummy event Jsonnnnnnnnnnnnnnnnnnn.....JSON........"+caretPosJB.getValue());
		beeJsonContent = jsonTextBoxId.getValue();
		beeHtmlContent = htmlTextBoxId.getValue();
		htmlTextBoxId.setValue("EMPTY_HTML");
		jsonTextBoxId.setValue("EMPTY_JSON");

		//SpecialLinks AnchorTag replace with Promocode Value

		//logger.info("Before replace Promocode"+beeHtmlContent);
		String[] beeHtmlContentParts = beeHtmlContent.split("<a");
		//System.out.println("beeJsonContent.................:"+beeJsonContent);
		//System.out.println("Constants.DEFAULT_JSON_VALUE2..:"+Constants.DEFAULT_JSON_VALUE2);
		if(beeJsonContent==null||!(beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_HTML)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_DIVIDER)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_VIDEO))){ 
			if(next||isSaveAsTemplate||isSendTestMail||publishandclose||saveasdraftAndClose){
				next=false;
				//Venkata commented
				isSaveAsTemplate=false;
				isSendTestMail=false;
				publishandclose=false;
				saveasdraftAndClose=false;
				MessageUtil.setMessage("e-Reciept content cannot be empty", "color:red");
				if(winId.isVisible()){
					winId.setVisible(false);
				}
				if(testWinId.isVisible()){
					testWinId$sendTestMailBtnId.setDisabled(false);
					testWinId.setVisible(false);
				}
				//Venkata commented
				//zipImport.setVisible(false);
				//testWinId.setVisible(false);
				return;
			}
		}

		if(beeHtmlContent!=null&!beeHtmlContent.equalsIgnoreCase("EMPTY_HTML")){
			for(int i=0;i<=beeHtmlContentParts.length-1;i++){
				String anchorTagStr="<a"+beeHtmlContentParts[i];
				//System.out.println("anchorTagStr.."+anchorTagStr);
				String promocodeLink="<a"+StringUtils.substringBetween(anchorTagStr, "<a", "</a>")+"</a>";
				//System.out.println("promocodeLink.."+promocodeLink);
				if(promocodeLink.contains("BEEFREEPROMOCODE_START")&&promocodeLink.contains("BEEFREEPROMOCODE_END")){
					String promocodeValue=StringUtils.substringBetween(beeHtmlContent, "BEEFREEPROMOCODE_START", "BEEFREEPROMOCODE_END");
					//System.out.println("promocodeValue.."+promocodeValue);
					beeHtmlContent=beeHtmlContent.replace(promocodeLink, promocodeValue);
					//logger.info("Html after Replace.."+beeHtmlContent);
				}
				logger.info("----------.....................------------");
			}
			//logger.info("After replace Promocode and before replacing barcode"+beeHtmlContent);
			beeHtmlContent= beeHtmlContent.replace("&lt;img", "<img");
			beeHtmlContent= beeHtmlContent.replace("/&gt;", "/>");
			//logger.info("After replace Barcode &lt;img and /&gt;"+beeHtmlContent);
			beeHtmlContent=Utility.replaceTextBarcodeHeightWidth(beeHtmlContent);
		} 

		long size =	Utility.validateHtmlSize(beeHtmlContent);
		if(size >100) {
			String msgcontent = OCConstants.HTML_VALIDATION;
			msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
			MessageUtil.setMessage(msgcontent, "color:Blue");
		}
		String	tempName=winId$templatNameTbId.getValue().trim();
		if(isSaveAsTemplate){
						
				isSaveAsTemplate=false;
				if(saveasdraftAndClose){
					saveasdraftAndClose=false;
					saveasdraftAndCloseBtnIdInMyTemplates(tempName, beeHtmlContent,beeJsonContent);
				}else
					savePublishInMyTemplates(tempName,beeHtmlContent,beeJsonContent);
			
		} 
		else if(isSendTestMail){
			isSendTestMail=false;
			testWinId$msgLblId.setValue("");
			/*if(Utility.validateHtmlSize(beeHtmlContent)) {
				Messagebox.show("HTML size cannot exceed 100kb. Please reduce" +
						"the size and try again.", "Error", Messagebox.OK, Messagebox.ERROR);
			}*/
			String emailId = testWinId$testTbId.getText();

			String[] emailArr = emailId.split(",");
			for (String email : emailArr) {

				sendTestMail(beeHtmlContent, email);
			}//for
			Messagebox.show("Test mail will be sent in a moment.", "Information", 
					Messagebox.OK, Messagebox.INFORMATION);
			testWinId.setVisible(false);
			testWinId$sendTestMailBtnId.setDisabled(false);
		}
		else if(isAutoSave){
			isAutoSave=false;
			boolean isautosaved = false;
			//Venkata commented
			//saveEmail(beeHtmlContent,null, null, null, false,beeJsonContent);

			//Venkata 
			//saveDRTemplate(beeHtmlContent,beeJsonContent);
			if(beeJsonContent!=null && (beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_HTML)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_DIVIDER)||beeJsonContent.contains(Constants.DEFAULT_JSON_CHECK_VIDEO))) 
				isautosaved = autoSaveDRTemplate(beeHtmlContent,beeJsonContent);


			if(isautosaved){
				TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
				autoSaveLbId.setValue("Last auto-saved at: "+ (MyCalendar.calendarToString(Calendar.getInstance(),MyCalendar.FORMAT_SCHEDULE_TIME,tz)).substring(13));
			}
		}else if(publishandclose){
			
			savePublishDRTemplate(beeHtmlContent,beeJsonContent,tempName);
			
		}else if(saveasdraftAndClose){
			
			saveasdraftAndCloseDRTemplateandClose(beeHtmlContent,beeJsonContent,tempName);
			
		}
	}

	
	public boolean isDraft(String templateName){
		if(templateName.contains(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DRAFT+"_"))
			return true;
		return false;
	}
	
	public void onChange$caretPosSE() {

		beeHTMLTestMailContent =  caretPosSE.getValue();
		caretPosSE.setValue("EMPTY_HTML");

		//SpecialLinks AnchorTag replace with Promocode Value

		//logger.info("Before replace Promocode"+beeHTMLTestMailContent);
		String[] beeHtmlContentParts = beeHTMLTestMailContent.split("<a");

		if(beeHTMLTestMailContent!=null&!beeHTMLTestMailContent.equalsIgnoreCase("EMPTY_HTML")){
			for(int i=0;i<=beeHtmlContentParts.length-1;i++){
				String anchorTagStr="<a"+beeHtmlContentParts[i];
				//System.out.println("anchorTagStr.."+anchorTagStr);
				String promocodeLink="<a"+StringUtils.substringBetween(anchorTagStr, "<a", "</a>")+"</a>";
				//System.out.println("promocodeLink.."+promocodeLink);
				if(promocodeLink.contains("BEEFREEPROMOCODE_START")&&promocodeLink.contains("BEEFREEPROMOCODE_END")){
					String promocodeValue=StringUtils.substringBetween(beeHTMLTestMailContent, "BEEFREEPROMOCODE_START", "BEEFREEPROMOCODE_END");
					//System.out.println("promocodeValue.."+promocodeValue);
					beeHTMLTestMailContent=beeHTMLTestMailContent.replace(promocodeLink, promocodeValue);
					//logger.info("Html after Replace.."+beeHTMLTestMailContent);
				}
				//logger.info("----------.....................------------");
			}
			//logger.info("After replace Promocode and before replacing barcode"+beeHTMLTestMailContent);
			beeHTMLTestMailContent= beeHTMLTestMailContent.replace("&lt;img", "<img");
			beeHTMLTestMailContent= beeHTMLTestMailContent.replace("/&gt;", "/>");
			//logger.info("After replace Barcode &lt;img and /&gt;"+beeHTMLTestMailContent);

		} 

		if(!beeHTMLTestMailContent.isEmpty()&& caretPosSE.getAttribute("action").equals("test")) {
			//venkata commented
			//testWinId$msgLblId.setValue("");
			/*if(Utility.validateHtmlSize(beeHTMLTestMailContent)) {

				Messagebox.show("HTML size cannot exceed 100kb. Please reduce" +
						"the size and try again.", "Error", Messagebox.OK, Messagebox.ERROR);
			}*/

			//venkata commented
			/*String emailId = testWinId$testTbId.getText();

			String[] emailArr = emailId.split(",");
			for (String email : emailArr) {

				super.sendTestMail(beeHTMLTestMailContent, email);
			}//for
			testWinId.setVisible(false);
			 */			

		}

	}

	
	public void onChange$jsonAutoSaveTextBoxId() {
		logger.info(" ======inside jsonAutoSaveTextBoxId()");
		isAutoSave=true;
		Clients.evalJavaScript("bee.save()"); 
	}


	private String  beeAutosaveJsonContent;
	public void onChange$autoSaveJsonTextBoxId(){
		logger.info("onChange$autoSaveJsonTextBoxId just enterd : "+System.currentTimeMillis());
		//	isAutoSave=true;
		beeAutosaveJsonContent =  autoSaveJsonTextBoxId.getValue();
		autoSaveJsonTextBoxId.setValue("EMPTY_JSON");
		if(beeAutosaveJsonContent!=null&!beeAutosaveJsonContent.equalsIgnoreCase("EMPTY_JSON")){
			logger.info("Just before ending onChange$autoSaveJsonTextBoxId and just before calling bee.send() : "+System.currentTimeMillis());
			Clients.evalJavaScript("bee.send()");
		} 
	}


	public void onChange$sendMethodHtmlTextBoxId(){
		logger.info("onChange$sendMethodHtmlTextBoxId just enterd : "+System.currentTimeMillis());
		String beeAutosaveJsonContentLocal =beeAutosaveJsonContent;
		String beeHtmlContentLocal = sendMethodHtmlTextBoxId.getValue();
		sendMethodHtmlTextBoxId.setValue("EMPTY_HTML");

		String[] beeHtmlContentParts = beeHtmlContentLocal.split("<a");
		if(beeHtmlContentLocal!=null&!beeHtmlContentLocal.equalsIgnoreCase("EMPTY_HTML")){
			for(int i=0;i<=beeHtmlContentParts.length-1;i++){
				String anchorTagStr="<a"+beeHtmlContentParts[i];
				//System.out.println("anchorTagStr.."+anchorTagStr);
				String promocodeLink="<a"+StringUtils.substringBetween(anchorTagStr, "<a", "</a>")+"</a>";
				//System.out.println("promocodeLink.."+promocodeLink);
				if(promocodeLink.contains("BEEFREEPROMOCODE_START")&&promocodeLink.contains("BEEFREEPROMOCODE_END")){
					String promocodeValue=StringUtils.substringBetween(beeHtmlContentLocal, "BEEFREEPROMOCODE_START", "BEEFREEPROMOCODE_END");
					//System.out.println("promocodeValue.."+promocodeValue);
					beeHtmlContentLocal=beeHtmlContentLocal.replace(promocodeLink, promocodeValue);
					//logger.info("Html after Replace.."+beeHtmlContentLocal);
				}
				//logger.info("----------.....................------------");
			}
			//logger.info("After replace Promocode and before replacing barcode"+beeHtmlContentLocal);
			beeHtmlContentLocal= beeHtmlContentLocal.replace("&lt;img", "<img");
			beeHtmlContentLocal= beeHtmlContentLocal.replace("/&gt;", "/>");
			//logger.info("After replace Barcode &lt;img and /&gt;"+beeAutosaveJson);
			beeHtmlContentLocal=Utility.replaceTextBarcodeHeightWidth(beeHtmlContentLocal);
		}



		if(beeAutosaveJsonContentLocal!=null & !beeAutosaveJsonContentLocal.equalsIgnoreCase("EMPTY_JSON") & beeHtmlContentLocal!=null & !beeHtmlContentLocal.equalsIgnoreCase("EMPTY_HTML")){

			boolean isautosaved = false;
			beeAutosaveJsonContent ="EMPTY_HTML";
			logger.info("just before calling saveEmail: "+System.currentTimeMillis());
			//logger.info("beeAutosaveJsonContentLocal: "+beeAutosaveJsonContentLocal);

			//venkataRatna commented 
			//sessionScope.removeAttribute("selectedTemplate");
			//saveEmail(beeHtmlContentLocal,null, null, null, false,beeAutosaveJsonContentLocal);

			//logger.info("just after calling saveEmail: "+System.currentTimeMillis());
			if(beeAutosaveJsonContentLocal!=null && (beeAutosaveJsonContentLocal.contains(Constants.DEFAULT_JSON_CHECK_TEXTBOX)||beeAutosaveJsonContentLocal.contains(Constants.DEFAULT_JSON_CHECK_IMAGE)||beeAutosaveJsonContentLocal.contains(Constants.DEFAULT_JSON_CHECK_MERGE_CONTENT)||beeAutosaveJsonContentLocal.contains(Constants.DEFAULT_JSON_CHECK_HTML)||beeAutosaveJsonContentLocal.contains(Constants.DEFAULT_JSON_CHECK_BUTTON)||beeAutosaveJsonContentLocal.contains(Constants.DEFAULT_JSON_CHECK_SOCIAL)||beeAutosaveJsonContentLocal.contains(Constants.DEFAULT_JSON_CHECK_DIVIDER)||beeAutosaveJsonContentLocal.contains(Constants.DEFAULT_JSON_CHECK_VIDEO))) 
				isautosaved =autoSaveDRTemplate(beeHtmlContentLocal,beeAutosaveJsonContentLocal);

			if(isautosaved){
				TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
				autoSaveLbId.setValue("Last auto-saved at: "+ (MyCalendar.calendarToString(Calendar.getInstance(),MyCalendar.FORMAT_SCHEDULE_TIME,tz)).substring(13));
			}
		}
		else{
			beeAutosaveJsonContent ="EMPTY_HTML";
		}
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			super.doAfterCompose(comp);
			onSave=false;
			currentUser = GetUser.getUserObj();
			userName = GetUser.getUserName();
			logger.info("userName ::"+userName);
			
			
			beeclientkey = PropertyUtil.getPropertyValueFromDB(Constants.BEE_CLIENT_KEYVALUE_DR);
			
			DigitalReceiptMyTemplate DRTemplate= (DigitalReceiptMyTemplate) sessionScope.getAttribute("DRTemplate");
			if(DRTemplate!=null)
				tempLbName.setValue(DRTemplate.getName());

			isEdit = (Boolean)session.getAttribute("isDRTemplateEdit");
			isDraftEdit = (Boolean)session.getAttribute("isDraftTemplateEdit");

			Object dRTemplate = session.getAttribute("DRTemplate");
			if(dRTemplate instanceof DigitalReceiptMyTemplate) digitalReceiptMyTemplate = (DigitalReceiptMyTemplate)dRTemplate;

			if(isEdit != null && isEdit == true){

				logger.debug("In edit mode==="+digitalReceiptMyTemplate.getJsonContent());
				editorType = digitalReceiptMyTemplate.getEditorType();
				htmlStuff = digitalReceiptMyTemplate.getContent();
				jsonStuff = digitalReceiptMyTemplate.getJsonContent();

			}else if(isDraftEdit != null && isDraftEdit == true){
				logger.debug("In Edit Draft mode==="+digitalReceiptMyTemplate.getJsonContent());
				editorType = digitalReceiptMyTemplate.getEditorType();
				htmlStuff = digitalReceiptMyTemplate.getAutoSaveHtmlContent();
				jsonStuff = digitalReceiptMyTemplate.getAutoSaveJsonContent();
			}
			else {
				logger.debug("----create new tempalte------");
				if(digitalReceiptMyTemplate != null){
					editorType = digitalReceiptMyTemplate.getEditorType();
					htmlStuff = digitalReceiptMyTemplate.getContent();
					jsonStuff = digitalReceiptMyTemplate.getJsonContent();
				}
				else {
					htmlStuff = "";
					if(jsonStuff==null) {
						jsonStuff = Constants.DEFAULT_JSON_VALUE;
					}
				}
			}
			
			MyTempEditorController.getPlaceHolderList();
			EditorController.getCouponsList();
			getInvcDRBRCode();
			

			JSONObject jsontemplate = (JSONObject)JSONValue.parse(jsonStuff);
			Clients.evalJavaScript("var mytemplates ="+jsontemplate+";");
			Clients.evalJavaScript("var beeclientkey ='"+beeclientkey+"';");
			logger.info("userName :::"+userName);
			//Clients.evalJavaScript("var ocImageCouponsArr = [];");
			//Clients.evalJavaScript("var barCodes79 = [];");
			Clients.evalJavaScript("initialcall();");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);;
		}
	}


	public void savePublishDRTemplate(String beeHtmlContent,String beeJsonContent,String templateName) {
		try{
			logger.info(" =====inside saveDRTemplate 33======");	

			if((initialAutoSave!=null && initialAutoSave==true ) && ((isEdit!=null || isDraftEdit!=null) && digitalReceiptMyTemplate!=null) &&  
					digitalReceiptMyTemplate.getName()!= null && digitalReceiptMyTemplate.getAutoSaveHtmlContent() !=null){ 

				winId$templatNameTbId.setValue("");
				winId.setVisible(true);
				winId.setPosition("center");
				winId.doHighlighted();
			}
			else if((isEdit!=null || isDraftEdit!=null) && digitalReceiptMyTemplate!=null) {

				DigitalReceiptMyTemplate dReceiptMyTemplate = digitalReceiptMyTemplate;

				if(digitalReceiptMyTemplate != null) {
					
					boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(digitalReceiptMyTemplate.getMyTemplateId());

					String editorContent = beeHtmlContent;

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
					if(isConfigured){

						int confirm = Messagebox.show("Changes done in the template will be published \n" + "and will reflect in all e-Receipts\n " +"sent in future. " + "Do you want to continue?", "Information", 
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1) {
							publishandclose = false;
							return;
						}

					} else {

						int confirm = Messagebox.show("Changes done in the template will be published \n" + "and draft version will be deleted. \n" + "Do you want to continue?", "Information", 
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1) {
							publishandclose = false;
							return;
						}
					}

					editorContent = editorContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
							.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
					editorContent = editorContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
							.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
					editorContent = editorContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
							.replace("<div id=\"##END REF##\"></div>","##END REF##");
					editorContent = editorContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
							.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");

					logger.info(" publish Template Name  "+templateName);
					//editorContent = PrepareFinalHtml.replaceImgURL(editorContent, currentUser.getUserName());
					editorContent = editorContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					beeJsonContent = beeJsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					if(templateName!=null && !templateName.isEmpty())
						dReceiptMyTemplate.setName(templateName);
					dReceiptMyTemplate.setJsonContent(beeJsonContent);
					dReceiptMyTemplate.setContent(editorContent);
					dReceiptMyTemplate.setModifiedDate(Calendar.getInstance());

					dReceiptMyTemplate.setAutoSaveHtmlContent(null);
					dReceiptMyTemplate.setAutoSaveJsonContent(null);

					dReceiptMyTemplate.setModifiedby(currentUser.getUserId());


					//TODO SETFOLDERNAME
					
					try {
						digiRecptMyTemplatesDaoForDML.saveOrUpdate(dReceiptMyTemplate);

						/****
						 * Navigate to respective folder in DR_lanchingScreen
						 ***/
						session.setAttribute("newTemplate",true);
						session.setAttribute("newTemplatefolder",dReceiptMyTemplate.getFolderName());

					} catch (Exception e) {
						autoSaveSession.removeAttribute("autoSaveDRTemplate");
						Redirect.goTo(PageListEnum.EMPTY);
						Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPTS);
						return;
					}

					session.setAttribute("DRTemplate", dReceiptMyTemplate);
					autoSaveSession.removeAttribute("autoSaveDRTemplate");

					MessageUtil.setMessage("Template saved successfully.","green", "top");
				} else {
					MessageUtil.setMessage("Failed to update template.", "red","top");
				}

				if(publishandclose){
					session.removeAttribute("isDRTemplateEdit");
					session.removeAttribute("isDraftTemplateEdit");

					session.removeAttribute("DRTemplate");

					if(digitalReceiptMyTemplate!=null)
						//digiRecptMyTemplatesDaoForDML.setTemplateFlag(digitalReceiptMyTemplate.getMyTemplateId(),false);

					Redirect.goTo(PageListEnum.EMPTY);
					Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPTS);
				}

			}else{
				newTemplate= true;
				save = true;
				saveAs();

			}
		}
		catch(DataIntegrityViolationException die) {
			MessageUtil.setMessage("Template with given name already exist, Please provide another name", "red","top");
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);

		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);

		}

	}	 

	
	public void saveasdraftAndCloseDRTemplateandClose(String beeHtmlContent,String beeJsonContent,String templateName) {
		try{
			logger.info(" =====inside saveDRTemplate 33======");	

			if((initialAutoSave!=null && initialAutoSave==true ) && ((isEdit!=null || isDraftEdit!=null) && digitalReceiptMyTemplate!=null) &&  
					digitalReceiptMyTemplate.getName()!= null && digitalReceiptMyTemplate.getAutoSaveHtmlContent() !=null){ 

				winId$templatNameTbId.setValue("");
				winId.setVisible(true);
				winId.setPosition("center");
				winId.doHighlighted();
			}
			else if((isEdit!=null || isDraftEdit!=null) && digitalReceiptMyTemplate!=null) {

				DigitalReceiptMyTemplate dReceiptMyTemplate = digitalReceiptMyTemplate;

				if(digitalReceiptMyTemplate != null) {
					
					boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(digitalReceiptMyTemplate.getMyTemplateId());

					String editorContent = beeHtmlContent;

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
					if(isConfigured){

						int confirm = Messagebox.show("Changes done in the template will be saved as \n" + "draft and will not affect published version. \n" + "Do you want to continue?", "Information", 
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1) return;

					} else {

						int confirm = Messagebox.show("Changes done in the template will be saved as \n" + "draft. " + "Do you want to continue?", "Information", 
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1) return;
					}

					editorContent = editorContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
							.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
					editorContent = editorContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
							.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
					editorContent = editorContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
							.replace("<div id=\"##END REF##\"></div>","##END REF##");
					editorContent = editorContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
							.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");

					logger.info(" publish Template Name  "+templateName);

					if(templateName!=null && !templateName.isEmpty())
						dReceiptMyTemplate.setName(templateName);
					
					/*if(templateName!=null && !templateName.isEmpty()){
						
						if(templateName.contains(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DRAFT+"_"))
							
							dReceiptMyTemplate.setName(templateName);
						}else
							dReceiptMyTemplate.setName(templateName);
					 */
					
					//dReceiptMyTemplate.setJsonContent(beeJsonContent);
					//dReceiptMyTemplate.setContent(editorContent);
					//editorContent = PrepareFinalHtml.replaceImgURL(editorContent, currentUser.getUserName());
					editorContent = editorContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					beeJsonContent = beeJsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					dReceiptMyTemplate.setModifiedDate(Calendar.getInstance());

					dReceiptMyTemplate.setEditorType(Constants.EDITOR_TYPE_BEE);
					dReceiptMyTemplate.setFolderName(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);


					dReceiptMyTemplate.setAutoSaveHtmlContent(editorContent);
					dReceiptMyTemplate.setAutoSaveJsonContent(beeJsonContent);

					dReceiptMyTemplate.setModifiedby(currentUser.getUserId());


					//TODO SETFOLDERNAME
					
					try {
						
						//save_close_draft
						digiRecptMyTemplatesDaoForDML.saveOrUpdate(dReceiptMyTemplate);

						/****
						 * Navigate to respective folder in DR_lanchingScreen
						 ***/
						session.setAttribute("newTemplate",true);
						session.setAttribute("newTemplatefolder",dReceiptMyTemplate.getFolderName());

					} catch (Exception e) {
						autoSaveSession.removeAttribute("autoSaveDRTemplate");
						Redirect.goTo(PageListEnum.EMPTY);
						Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPTS);
						return;
					}

					session.setAttribute("DRTemplate", dReceiptMyTemplate);
					autoSaveSession.removeAttribute("autoSaveDRTemplate");

					MessageUtil.setMessage("Template saved successfully.","green", "top");
				} else {
					MessageUtil.setMessage("Failed to update template.", "red","top");
				}

				if(saveasdraftAndClose){
					
					if(saveasdraft) {
						saveasdraft = false;
					}else {

						session.removeAttribute("isDRTemplateEdit");
						session.removeAttribute("isDraftTemplateEdit");

						session.removeAttribute("DRTemplate");

						if(digitalReceiptMyTemplate!=null)
							//digiRecptMyTemplatesDaoForDML.setTemplateFlag(digitalReceiptMyTemplate.getMyTemplateId(),false);

						Redirect.goTo(PageListEnum.EMPTY);
						Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPTS);
					}
				}

			}else{
				newTemplate= true;
				save = true;
				saveAs();

			}
		}
		catch(DataIntegrityViolationException die) {
			MessageUtil.setMessage("Template with given name already exist,Please provide another name", "red","top");
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);

		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);

		}

	}	 

	
	
	
	public void savePublishInMyTemplates(String dRTemplateName,String beeHtmlContent,String JsonContent){

		logger.info("====inside saveInMyTemplates 11========= ");
		try {

			if((initialAutoSave!=null && initialAutoSave==true ) && ((isEdit!=null || isDraftEdit!=null) && digitalReceiptMyTemplate!=null)){
				//TODO 03_03_2019
				//int configCount = drSentDao.findConfiguredTemplateCount(accessAccIds,dRTemplateName.trim());

				String editorContent = beeHtmlContent;

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
				/*if(configCount > 0) {

					int confirm = Messagebox.show("Changes in the template will be reflected in all digital " +
							"receipts sent from now. \n Do you want to continue?", "Update Template",
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);


					if(confirm != 1) return;
				}*/

				editorContent = editorContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
						.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
				editorContent = editorContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
						.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
				editorContent = editorContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
						.replace("<div id=\"##END REF##\"></div>","##END REF##");
				editorContent = editorContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
						.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");

				DigitalReceiptMyTemplate dReceiptMyTemplate = digitalReceiptMyTemplate;
				//editorContent = PrepareFinalHtml.replaceImgURL(editorContent, currentUser.getUserName());
				editorContent = editorContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				JsonContent = JsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				dReceiptMyTemplate.setName(dRTemplateName.trim());
				dReceiptMyTemplate.setContent(editorContent);
				dReceiptMyTemplate.setJsonContent(JsonContent);
				
				dReceiptMyTemplate.setModifiedDate(Calendar.getInstance());

				dReceiptMyTemplate.setAutoSaveHtmlContent(null);
				dReceiptMyTemplate.setAutoSaveJsonContent(null);
				dReceiptMyTemplate.setModifiedby(currentUser.getUserId());

				digiRecptMyTemplatesDaoForDML.saveOrUpdate(dReceiptMyTemplate);

				/****
				 * Navigate to respective folder in DR_lanchingScreen
				 ***/
				session.setAttribute("newTemplate",true);
				MessageUtil.setMessage("Template saved successfully.","green", "top");
				initialAutoSave=false;
			}
			else if ((isEdit!=null || isDraftEdit!=null) && digitalReceiptMyTemplate != null) {

				if (digitalReceiptMyTemplate != null) {
					logger.info(" =========window is highlighted  ======== ");
					winId$templatNameTbId.setValue("");
					winId$errorMsgLblId.setValue("");
					winId.setVisible(true);
					winId.setPosition("center");
					winId.doHighlighted();
					return;
				}
				if(digitalReceiptMyTemplate != null) {
					//TODO 03_03_2019
					//int configCount = drSentDao.findConfiguredTemplateCount(accessAccIds,dRTemplateName.trim());


					String editorContent = beeHtmlContent;

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
					/*if(configCount > 0) {

						int confirm = Messagebox.show("Changes in the template will be reflected in all digital " +
								"receipts sent from now. \n Do you want to continue?", "Update Template",
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

						if(confirm != 1) return;
					}*/
					DigitalReceiptMyTemplate dRMyTemplates = digitalReceiptMyTemplate;
					boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(dRMyTemplates.getMyTemplateId());
					if(dRMyTemplates.getJsonContent() != null && dRMyTemplates.getContent() != null){/*
						if(!isConfigured){
							Messagebox.show("Changes done in the template will be saved as " + "draft and will not affect published version.", "Information", 
									Messagebox.OK, Messagebox.QUESTION);
						}else {
							Messagebox.show("Changes done in the template will be saved as " + "draft and will not reflect in digital receipts sent currently.", "Information",
									Messagebox.OK, Messagebox.QUESTION);
						}
					*/
						
						if(isConfigured){

							int confirm = Messagebox.show("Changes done in the template will be published \n" + "and will reflect in all e-Receipts sent in future. \n" + "Do you want to continue?", "Information", 
									Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if(confirm != 1) return;

						} else {

							int confirm = Messagebox.show("Changes done in the template will be published \n" + "and draft version will be deleted. \n" + "Do you want to continue?", "Information", 
									Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if(confirm != 1) return;
						}
					
					}

					editorContent = editorContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
							.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
					editorContent = editorContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
							.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
					editorContent = editorContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
							.replace("<div id=\"##END REF##\"></div>","##END REF##");
					editorContent = editorContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
							.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");
					//editorContent = PrepareFinalHtml.replaceImgURL(editorContent, currentUser.getUserName());
					editorContent = editorContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					JsonContent = JsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					DigitalReceiptMyTemplate dReceiptMyTemplate = digitalReceiptMyTemplate;
					dReceiptMyTemplate.setName(dRTemplateName.trim());
					/*dReceiptMyTemplate.setContent(digitalReceiptMyTemplate.getContent());
					dReceiptMyTemplate.setJsonContent(digitalReceiptMyTemplate.getJsonContent());
					*/
					dReceiptMyTemplate.setContent(editorContent);
					dReceiptMyTemplate.setJsonContent(JsonContent);
					
					dReceiptMyTemplate.setEditorType(Constants.EDITOR_TYPE_BEE);
					//	dReceiptMyTemplate.setFolderName(folderName);
					dReceiptMyTemplate.setModifiedDate(Calendar.getInstance());

					/*dReceiptMyTemplate.setAutoSaveHtmlContent(editorContent);
					dReceiptMyTemplate.setAutoSaveJsonContent(JsonContent);
					*/
					dReceiptMyTemplate.setAutoSaveHtmlContent(null);
					dReceiptMyTemplate.setAutoSaveJsonContent(null);
					
					dReceiptMyTemplate.setModifiedby(currentUser.getUserId());

					digiRecptMyTemplatesDaoForDML.saveOrUpdate(dReceiptMyTemplate);

					/****
					 * Navigate to respective folder in DR_lanchingScreen
					 ***/
					session.setAttribute("newTemplate",true);
					//	session.setAttribute("newTemplatefolder",folderName);

					//autoSaveSession.removeAttribute("autoSaveDRTemplate");
					session.removeAttribute("isDRTemplateEdit");
					isDraftEdit=true;
					isEdit = false;

					MessageUtil.setMessage("Template saved successfully.","green", "top");
				} else {
					MessageUtil.setMessage("Failed to save template.", "red","top");
				}
				initialAutoSave=false;
			}else{

				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
						.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
						.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
						.replace("<div id=\"##END REF##\"></div>","##END REF##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
						.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");
				DigitalReceiptMyTemplate dRMyTemplate = new DigitalReceiptMyTemplate(dRTemplateName.trim(), null,Calendar.getInstance(), currentUser.getUserId(),currentUser.getUserOrganization().getUserOrgId());
				
				//dRMyTemplate.setJsonContent(null);
				dRMyTemplate.setEditorType(Constants.EDITOR_TYPE_BEE);
				dRMyTemplate.setFolderName(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);

				//dRMyTemplate.setAutoSaveHtmlContent(beeHtmlContent);
				//dRMyTemplate.setAutoSaveJsonContent(JsonContent);
				dRMyTemplate.setModifiedDate(Calendar.getInstance());
				dRMyTemplate.setModifiedby(currentUser.getUserId());
				
				boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(dRMyTemplate.getMyTemplateId());
				/*if(dRMyTemplate.getJsonContent() != null && dRMyTemplate.getContent() != null){
					if(isConfigured){

						Messagebox.show("Changes done in the template will be saved as " + "draft and will not affect published version.", "Information", 
								Messagebox.OK, Messagebox.QUESTION);
					}else {
						Messagebox.show("Changes done in the template will be saved as " + "draft and will not reflect in digital receipts sent currently.", "Information",
								Messagebox.OK, Messagebox.QUESTION);
					}
					dRMyTemplate.setAutoSaveHtmlContent(beeHtmlContent);
					dRMyTemplate.setAutoSaveJsonContent(JsonContent);
				}else{
					
						dRMyTemplate.setJsonContent(JsonContent);
						dRMyTemplate.setContent(beeHtmlContent);
					
				}*/

				if(dRMyTemplate.getJsonContent() != null && dRMyTemplate.getContent() != null){
					if(isConfigured){

						int confirm = Messagebox.show("Changes done in the template will be published \n" + "and will reflect in all e-Receipts sent in future. \n" + "Do you want to continue?", "Information", 
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1) return;

					} else {

						int confirm = Messagebox.show("Changes done in the template will be published \n" + "and draft version will be deleted. \n" + "Do you want to continue?", "Information", 
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1) return;
					}
											
				}
				
				//beeHtmlContent = PrepareFinalHtml.replaceImgURL(beeHtmlContent, currentUser.getUserName());
				beeHtmlContent = beeHtmlContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				JsonContent = JsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				dRMyTemplate.setJsonContent(JsonContent);
				dRMyTemplate.setContent(beeHtmlContent);
			
				
				dRMyTemplate.setAutoSaveHtmlContent(null);
				dRMyTemplate.setAutoSaveJsonContent(null);
				
				
				digiRecptMyTemplatesDaoForDML.saveOrUpdate(dRMyTemplate);

				/****
				 * Navigate to respective folder in DR_lanchingScreen
				 ***/
				session.setAttribute("newTemplate",true);
				//session.setAttribute("newTemplatefolder",folderName);

				autoSaveSession.removeAttribute("autoSaveDRTemplate");
				session.removeAttribute("TempSubAccount"); 

				MessageUtil.setMessage("Template saved successfully.","green", "top");
				session.removeAttribute("isDRTemplateEdit");
				isDraftEdit=true;
				isEdit = false;
				digitalReceiptMyTemplate = dRMyTemplate;
				/*if(newTemplate){
					isEdit = (Boolean)session.getAttribute("isDRTemplateEdit");
						Object dRTemplate = session.getAttribute("DRTemplate");
						if(dRTemplate instanceof DigitalReceiptMyTemplate) digitalReceiptMyTemplate = (DigitalReceiptMyTemplate)dRTemplate;

					newTemplate=false;
					isEdit=true;
					digitalReceiptMyTemplate = dRMyTemplate;
				}*/
			}

			winId.setVisible(false);
				saveasdraftAndClose=false;
				isEdit=false;
				
				if(saveasdraft) {
					saveasdraft = false;
				}else {
					Redirect.goTo(PageListEnum.EMPTY);
					Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPTS);
				}
	

		} catch(DataIntegrityViolationException die) {
			MessageUtil.setMessage("Template with given name already exist,Please provide another name", "red","top");
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);

		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);

		}

	}

	public void saveasdraftAndCloseBtnIdInMyTemplates(String dRTemplateName,String beeHtmlContent,String JsonContent){

		logger.info("====inside saveInMyTemplates 11========= ");
		try {

			if((initialAutoSave!=null && initialAutoSave==true ) && ((isEdit!=null || isDraftEdit!=null) && digitalReceiptMyTemplate!=null)){
				//TODO 03_03_2019
				//int configCount = drSentDao.findConfiguredTemplateCount(accessAccIds,dRTemplateName.trim());

				String editorContent = beeHtmlContent;

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
				/*if(configCount > 0) {

					int confirm = Messagebox.show("Changes in the template will be reflected in all digital " +
							"receipts sent from now. \n Do you want to continue?", "Update Template",
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);


					if(confirm != 1) return;
				}*/

				editorContent = editorContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
						.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
				editorContent = editorContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
						.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
				editorContent = editorContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
						.replace("<div id=\"##END REF##\"></div>","##END REF##");
				editorContent = editorContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
						.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");

				DigitalReceiptMyTemplate dReceiptMyTemplate = digitalReceiptMyTemplate;
				//editorContent= PrepareFinalHtml.replaceImgURL(editorContent, currentUser.getUserName());
				editorContent = editorContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				JsonContent = JsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				dReceiptMyTemplate.setName(dRTemplateName.trim());
				dReceiptMyTemplate.setContent(null);
				dReceiptMyTemplate.setJsonContent(null);
				
				dReceiptMyTemplate.setModifiedDate(Calendar.getInstance());
				
				dReceiptMyTemplate.setAutoSaveHtmlContent(editorContent);
				dReceiptMyTemplate.setAutoSaveJsonContent(JsonContent);
				dReceiptMyTemplate.setModifiedby(currentUser.getUserId());

				digiRecptMyTemplatesDaoForDML.saveOrUpdate(dReceiptMyTemplate);

				/****
				 * Navigate to respective folder in DR_lanchingScreen
				 ***/
				session.setAttribute("newTemplate",true);
				MessageUtil.setMessage("Template saved successfully.","green", "top");
				initialAutoSave=false;
			}
			else if ((isEdit!=null || isDraftEdit!=null) && digitalReceiptMyTemplate != null) {

				if (digitalReceiptMyTemplate != null) {
					logger.info(" =========window is highlighted  ======== ");
					winId$templatNameTbId.setValue("");
					winId$errorMsgLblId.setValue("");
					winId.setVisible(true);
					winId.setPosition("center");
					winId.doHighlighted();
					saveasdraftAndClose=true;
					return;
				}
				if(digitalReceiptMyTemplate != null) {
					//TODO 03_03_2019
					//int configCount = drSentDao.findConfiguredTemplateCount(accessAccIds,dRTemplateName.trim());


					String editorContent = beeHtmlContent;

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
					/*if(configCount > 0) {

						int confirm = Messagebox.show("Changes in the template will be reflected in all digital " +
								"receipts sent from now. \n Do you want to continue?", "Update Template",
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

						if(confirm != 1) return;
					}*/
					DigitalReceiptMyTemplate dRMyTemplates = digitalReceiptMyTemplate;
					boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(dRMyTemplates.getMyTemplateId());
					if(dRMyTemplates.getJsonContent() != null && dRMyTemplates.getContent() != null){
						if(!isConfigured){
							Messagebox.show("Changes done in the template will be saved as \n" + "draft and will not affect published version.", "Information", 
									Messagebox.OK, Messagebox.QUESTION);
						}else {
							Messagebox.show("Changes done in the template will be saved as \n" + "draft and will not reflect in e-Receipts sent currently.", "Information",
									Messagebox.OK, Messagebox.QUESTION);
						}
					}

					editorContent = editorContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
							.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
					editorContent = editorContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
							.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
					editorContent = editorContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
							.replace("<div id=\"##END REF##\"></div>","##END REF##");
					editorContent = editorContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
							.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");

					DigitalReceiptMyTemplate dReceiptMyTemplate = digitalReceiptMyTemplate;
					//editorContent= PrepareFinalHtml.replaceImgURL(editorContent, currentUser.getUserName());
					editorContent = editorContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					JsonContent = JsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					dReceiptMyTemplate.setName(dRTemplateName.trim());
					
					dReceiptMyTemplate.setContent(digitalReceiptMyTemplate.getContent());
					dReceiptMyTemplate.setJsonContent(digitalReceiptMyTemplate.getJsonContent());
					dReceiptMyTemplate.setEditorType(Constants.EDITOR_TYPE_BEE);
					//	dReceiptMyTemplate.setFolderName(folderName);
					dReceiptMyTemplate.setModifiedDate(Calendar.getInstance());

					dReceiptMyTemplate.setAutoSaveHtmlContent(editorContent);
					dReceiptMyTemplate.setAutoSaveJsonContent(JsonContent);
					dReceiptMyTemplate.setModifiedby(currentUser.getUserId());

					digiRecptMyTemplatesDaoForDML.saveOrUpdate(dReceiptMyTemplate);

					/****
					 * Navigate to respective folder in DR_lanchingScreen
					 ***/
					session.setAttribute("newTemplate",true);
					//	session.setAttribute("newTemplatefolder",folderName);

					//autoSaveSession.removeAttribute("autoSaveDRTemplate");
					session.removeAttribute("isDRTemplateEdit");
					isDraftEdit=true;
					isEdit = false;

					MessageUtil.setMessage("Template saved successfully.","green", "top");
				} else {
					MessageUtil.setMessage("Failed to save template.", "red","top");
				}
				initialAutoSave=false;
			}else{

				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
						.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
						.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
						.replace("<div id=\"##END REF##\"></div>","##END REF##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
						.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");
				DigitalReceiptMyTemplate dRMyTemplate = new DigitalReceiptMyTemplate(dRTemplateName.trim(), null,Calendar.getInstance(), currentUser.getUserId(),currentUser.getUserOrganization().getUserOrgId());
				
				dRMyTemplate.setEditorType(Constants.EDITOR_TYPE_BEE);
				dRMyTemplate.setFolderName(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);

				//dRMyTemplate.setAutoSaveHtmlContent(beeHtmlContent);
				//dRMyTemplate.setAutoSaveJsonContent(JsonContent);
				dRMyTemplate.setModifiedDate(Calendar.getInstance());
				dRMyTemplate.setModifiedby(currentUser.getUserId());
				dRMyTemplate.setUserId(currentUser.getUserId());
				
				
				boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(dRMyTemplate.getMyTemplateId());
				if(dRMyTemplate.getJsonContent() != null && dRMyTemplate.getContent() != null){
					if(isConfigured){

						Messagebox.show("Changes done in the template will be saved as \n" + "draft and will not affect published version.", "Information", 
								Messagebox.OK, Messagebox.QUESTION);
					}else {
						Messagebox.show("Changes done in the template will be saved as \n" + "draft and will not reflect in e-Receipts sent currently.", "Information",
								Messagebox.OK, Messagebox.QUESTION);
					}
					dRMyTemplate.setAutoSaveHtmlContent(beeHtmlContent);
					dRMyTemplate.setAutoSaveJsonContent(JsonContent);
				}else{
								
						dRMyTemplate.setAutoSaveHtmlContent(beeHtmlContent);
						dRMyTemplate.setAutoSaveJsonContent(JsonContent);
				}
				//beeHtmlContent = PrepareFinalHtml.replaceImgURL(beeHtmlContent, currentUser.getUserName());
				beeHtmlContent = beeHtmlContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				JsonContent = JsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				dRMyTemplate.setAutoSaveJsonContent(JsonContent);
				dRMyTemplate.setAutoSaveHtmlContent(beeHtmlContent);
				digiRecptMyTemplatesDaoForDML.saveOrUpdate(dRMyTemplate);

				/****
				 * Navigate to respective folder in DR_lanchingScreen
				 ***/
				session.setAttribute("newTemplate",true);
				//session.setAttribute("newTemplatefolder",folderName);

				autoSaveSession.removeAttribute("autoSaveDRTemplate");
				session.removeAttribute("TempSubAccount");

				MessageUtil.setMessage("Template saved successfully.","green", "top");
				session.removeAttribute("isDRTemplateEdit");
				isDraftEdit=true;
				isEdit = false;
				digitalReceiptMyTemplate = dRMyTemplate;
				/*if(newTemplate){
					isEdit = (Boolean)session.getAttribute("isDRTemplateEdit");
						Object dRTemplate = session.getAttribute("DRTemplate");
						if(dRTemplate instanceof DigitalReceiptMyTemplate) digitalReceiptMyTemplate = (DigitalReceiptMyTemplate)dRTemplate;

					newTemplate=false;
					isEdit=true;
					digitalReceiptMyTemplate = dRMyTemplate;
				}*/
			}

			winId.setVisible(false);

			
			
			
			saveasdraftAndClose=false;
			isEdit=false;
			
			if(saveasdraft) {
				saveasdraft = false;
			}else {
				Redirect.goTo(PageListEnum.EMPTY);
				Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPTS);
			}

		} catch(DataIntegrityViolationException die) {
			MessageUtil.setMessage("Template with given name already exist,Please provide another name", "red","top");
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);

		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);

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
		}
		return isValid;
	}

	public void onClick$myTemplatesSubmtBtnId$winId(){
		winId$errorMsgLblId.setValue("");
		String tempName=winId$templatNameTbId.getValue().trim();
		logger.info(" Template Name  "+tempName);
		if(tempName==null || tempName.isEmpty()){
			winId$errorMsgLblId.setValue("Template Name can't be empty.");
			return;
		}
		if(!Utility.validateName(tempName)){
			winId$errorMsgLblId.setValue("Provide valid Template Name.\n Special characters are not allowed.");
			return;
		}
		boolean isExist= digiRecptMyTemplatesDao.isDRwithTemplateName_EditorType(currentUser.getUserId(), tempName, OCConstants.DRAG_AND_DROP_EDITOR);
		if(isExist){
			winId$errorMsgLblId.setValue("Template Name already exists.\n Please provide another name.");	
			return;
		}
		/*DigitalReceiptMyTemplate drTemp= digiRecptMyTemplatesDao.findBySubAccountAndTemplateName(account.getAccountId(), tempName);
		 if(drTemp!=null && !drTemp.getName().equals(tempName) ){
			 winId$errorMsgLblId.setValue("Template Name already exists in selected sub-account.\n Please provide another name.");	
			 return;
		 }*/
		/*if (save==true){
			dnext=true;
		}else{
			isSaveAsTemplate=true;
		}*/
		isSaveAsTemplate=true;
		Clients.evalJavaScript("bee.save()");
	}

	public void saveAs(){
		try {
			isFrom="";
			if(!(initialAutoSave!=null && initialAutoSave==true ) && 
					(isEdit != null || isDraftEdit!=null)  && 
					digitalReceiptMyTemplate.getName()!=null) {
				if(logger.isDebugEnabled())logger.debug("-- just entered --");
				logger.info("------in autosave save button---");
				winId$templatNameTbId.setValue("");
				winId$templatNameTbId.setAttribute("setSelect", "true");
				isSaveAsTemplate=true;
				Clients.evalJavaScript("bee.save()");
			} else if(save == true){
				logger.info("------in normal save button--");		
				logger.info(" =========window is highlighted  ======== ");
				winId$errorMsgLblId.setValue("");
				winId$templatNameTbId.setValue("");
				winId.setVisible(true);
				winId.setPosition("center");
				winId.doHighlighted();
			} else{
				logger.info("------in normal save button--");		
				logger.info(" =========window is highlighted  ======== ");
				winId$errorMsgLblId.setValue("");
				winId$templatNameTbId.setValue("");
				if(digitalReceiptMyTemplate!=null)
					winId$templatNameTbId.setValue(digitalReceiptMyTemplate.getName());
				winId.setVisible(true);
				winId.setPosition("center");
				winId.doHighlighted();
			}
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	}

	public void onClick$plainPreviewImgId() {
		logger.info("entered into html preview....");
		Clients.evalJavaScript("bee.preview()");
	}	 

	public void onClick$sendTestBtnId() {
		//take the content think about ph logic first
		//to be clarified , do we need validations or not...

		testWinId$msgLblId.setValue("");
		testWinId.setVisible(false);

		testWinId$testTbId.setValue("");
		testWinId.setVisible(true);
		testWinId.setPosition("center");
		testWinId.doHighlighted();
	}

	public void onClick$sendTestMailBtnId$testWinId() {		
		String emailId = testWinId$testTbId.getValue();
		boolean isValid = validateEmailAddr(emailId);
		if(isValid){
			testWinId$sendTestMailBtnId.setDisabled(true);
			isSendTestMail=true;
			Clients.evalJavaScript("bee.save()");
		}
	}

	public void onClick$cancelSendTestMailBtnId$testWinId() {

		testWinId$msgLblId.setValue("");
		testWinId$msgLblId.setValue("");
		testWinId.setVisible(false);

	}

	// changes here
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
						}else {
							//Testing for invalid email domains-APP-308
							String result = PurgeList.checkForValidDomainByEmailId(email.trim());
							if(!result.equalsIgnoreCase("Active")) {
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

	public void onClick$publishandcloseBtnId() {

		try {
			publishandclose = true;
			Clients.evalJavaScript("bee.save()");
		}
		catch (Exception e) {
			logger.error("** Exception :", e);
		}
	}
	
	public void onClick$saveasdraftAndCloseBtnId(){
		logger.info("entered save as draft & close mode");
		saveasdraftAndClose=true;
		Clients.evalJavaScript("bee.save()");
	}
	
	public void onClick$saveasdraftBtnId(){
		logger.info("entered save as draft");
		saveasdraft=true;
		saveasdraftAndClose=true;
		Clients.evalJavaScript("bee.save()");
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

			/*long size =	Utility.validateHtmlSize(htmlStuff);
			if(size >100) {
				String msgcontent = OCConstants.HTML_VALIDATION;
				msgcontent = msgcontent.replace("[size]", size+Constants.STRING_NILL);
				MessageUtil.setMessage(msgcontent, "color:Blue");
			}*/

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
			for (String email : emailArr) {

				Utility.sendInstantMail(null, Constants.EQ_TYPE_TEST_DIGITALRCPT, htmlStuff,
						Constants.EQ_TYPE_TEST_DIGITALRCPT, email, null );

			}//for
			return true;
		} catch (Exception e) {
			logger.error("Exception : ", e);
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

	protected String getSpamResult(String htmlStuff) {

		if(htmlStuff == null || htmlStuff.isEmpty()) return null;

		String sub = "My Template";
		String emlFilePath = PropertyUtil.getPropertyValue("usersParentDirectory") + 
				"/" + userName + "/message.eml";
		if(logger.isDebugEnabled())logger.debug("Eml file Path : " + emlFilePath);

		StringBuffer response = (new SpamChecker()).checkSpam(sub, htmlStuff, emlFilePath, true); 
		return ( response==null?null:response.toString() );
	}

	//This method is to call 
	public void autoSaveDRTemplate_onclick_of_ok_button_autosave(String dRTemplateName,String beeHtmlContent,String folderName,String JsonContent){

		try {
			if ((isEdit!=null || isDraftEdit!=null)  && digitalReceiptMyTemplate != null) {
				if (digitalReceiptMyTemplate != null) {

					//TODO 03_03_2019
					//int configCount = drSentDao.findConfiguredTemplateCount(accessAccIds,dRTemplateName.trim());
					int configCount = 0;
					String editorContent = beeHtmlContent;
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
					if(configCount > 0) {
						int confirm = Messagebox.show("Changes in the template will be reflected in all digital " +
								"receipts sent from now. \n Do you want to continue?", "Update Template",
								Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1) return;
					}
					editorContent = editorContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
							.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
					editorContent = editorContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
							.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
					editorContent = editorContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
							.replace("<div id=\"##END REF##\"></div>","##END REF##");
					editorContent = editorContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
							.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");
					//editorContent = PrepareFinalHtml.replaceImgURL(editorContent, currentUser.getUserName());
					editorContent = editorContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					beeJsonContent = beeJsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					DigitalReceiptMyTemplate dReceiptMyTemplate = new DigitalReceiptMyTemplate(dRTemplateName.trim(), editorContent,Calendar.getInstance(), currentUser.getUserId(), currentUser.getUserOrganization().getUserOrgId());

					dReceiptMyTemplate.setJsonContent(beeJsonContent);
					dReceiptMyTemplate.setEditorType(Constants.EDITOR_TYPE_BEE);
					dReceiptMyTemplate.setFolderName(folderName);
					dReceiptMyTemplate.setModifiedDate(Calendar.getInstance());

					dReceiptMyTemplate.setAutoSaveHtmlContent(null);
					dReceiptMyTemplate.setAutoSaveJsonContent(null);

					dReceiptMyTemplate.setModifiedby(currentUser.getUserId());

					digiRecptMyTemplatesDaoForDML.saveOrUpdate(dReceiptMyTemplate);

					autoSaveSession.removeAttribute("autoSaveDRTemplate");
					session.removeAttribute("TempSubAccount");

					MessageUtil.setMessage("Template saved successfully.","green", "top");
				} else {
					MessageUtil.setMessage("Failed to save template.", "red","top");
				}
			}else{

				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
						.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
						.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
						.replace("<div id=\"##END REF##\"></div>","##END REF##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
						.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");
				//beeHtmlContent = PrepareFinalHtml.replaceImgURL(beeHtmlContent, currentUser.getUserName());
				beeHtmlContent = beeHtmlContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				beeJsonContent = beeJsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				DigitalReceiptMyTemplate dRMyTemplate = new DigitalReceiptMyTemplate(dRTemplateName.trim(), beeHtmlContent,Calendar.getInstance(), currentUser.getUserId(), currentUser.getUserOrganization().getUserOrgId());

				dRMyTemplate.setJsonContent(beeJsonContent);
				dRMyTemplate.setEditorType(Constants.EDITOR_TYPE_BEE);
				dRMyTemplate.setFolderName(folderName);
				dRMyTemplate.setModifiedDate(Calendar.getInstance());
				dRMyTemplate.setAutoSaveHtmlContent(null);
				dRMyTemplate.setAutoSaveJsonContent(null);

				dRMyTemplate.setModifiedby(currentUser.getUserId());
				digiRecptMyTemplatesDaoForDML.saveOrUpdate(dRMyTemplate);

				autoSaveSession.removeAttribute("autoSaveDRTemplate");
				session.removeAttribute("TempSubAccount");

				MessageUtil.setMessage("Template saved successfully.","green", "top");
				if(newTemplate){
					newTemplate=false;
					isEdit=true;
					digitalReceiptMyTemplate = dRMyTemplate;
				}
			}
			winId.setVisible(false);
		} catch(DataIntegrityViolationException die) {
			MessageUtil.setMessage("Template with given name already exist in the selected folder,Please provide another name", "red","top");
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);
		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);
		}
	}

	public boolean autoSaveDRTemplate(String beeHtmlContent,String beeJsonContent){

		try {
			if ((isEdit!=null || isDraftEdit!=null)&& digitalReceiptMyTemplate != null) {
				DigitalReceiptMyTemplate dReceiptMyTemplate = digitalReceiptMyTemplate;

				//int configCount = drSentDao.findConfiguredTemplateCount(currentUser.getUserOrganization().getUserOrgId(),dRTemplateName.trim());
				String editorContent = beeHtmlContent;
				/*String isValidPhStr = Utility.validatePh(editorContent, currentUser);
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
						if(configCount > 0) {
							int confirm = Messagebox.show("Changes in the template will be reflected in all digital " +
									"receipts sent from now. \n Do you want to continue?", "Update Template",
									Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if(confirm != 1) return;
						}*/

				editorContent = editorContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
						.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
				editorContent = editorContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
						.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
				editorContent = editorContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
						.replace("<div id=\"##END REF##\"></div>","##END REF##");
				editorContent = editorContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
						.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");
				//editorContent = PrepareFinalHtml.replaceImgURL(editorContent, currentUser.getUserName());
				editorContent = editorContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				beeJsonContent = beeJsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				dReceiptMyTemplate.setAutoSaveHtmlContent(editorContent);
				dReceiptMyTemplate.setAutoSaveJsonContent(beeJsonContent);

				dReceiptMyTemplate.setOnAutoModifiedDate(Calendar.getInstance());
				dReceiptMyTemplate.setModifiedby(currentUser.getUserId());

				digiRecptMyTemplatesDaoForDML.saveOrUpdate(dReceiptMyTemplate);

				autoSaveSession.setAttribute("autoSaveDRTemplate",dReceiptMyTemplate);
				session.removeAttribute("isDRTemplateEdit");
				isDraftEdit=true;
				return true;
			}else{

				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
						.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
						.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
						.replace("<div id=\"##END REF##\"></div>","##END REF##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
						.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");


				//	String folderName=OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DRAFT;
				String timeStamp = MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
				String  dRTemplateName = OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DRAFT+"_"+timeStamp;
				//beeHtmlContent = PrepareFinalHtml.replaceImgURL(beeHtmlContent, currentUser.getUserName());
				beeHtmlContent = beeHtmlContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				beeJsonContent = beeJsonContent.replace(appUrl, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
						.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
						.replace(MAILHANDLER_URL, ImageServer_Url);
				//In first AutoSave OriginalHtml,OriginalJSON content should be null
				DigitalReceiptMyTemplate dRMyTemplate = new DigitalReceiptMyTemplate(dRTemplateName.trim(), null,Calendar.getInstance(), currentUser.getUserId(),currentUser.getUserOrganization().getUserOrgId());

				dRMyTemplate.setJsonContent(null);
				dRMyTemplate.setEditorType(Constants.EDITOR_TYPE_BEE);
				dRMyTemplate.setFolderName(OCConstants.DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT);
				dRMyTemplate.setModifiedDate(Calendar.getInstance());
				dRMyTemplate.setAutoSaveHtmlContent(beeHtmlContent);
				dRMyTemplate.setAutoSaveJsonContent(beeJsonContent);

				dRMyTemplate.setModifiedby(currentUser.getUserId());

				digiRecptMyTemplatesDaoForDML.saveOrUpdate(dRMyTemplate);

				autoSaveSession.removeAttribute("autoSaveDRTemplate");
				session.removeAttribute("TempSubAccount");
				session.removeAttribute("isDRTemplateEdit");
			
				isDraftEdit = true;
				digitalReceiptMyTemplate = dRMyTemplate;
				initialAutoSave=true;

				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			return false;
		}
	}
	
	
	/*public String getEditorRoleSet(){

	try {
		Clients.evalJavaScript("var userrole;");
		String userRole=Constants.STRING_NILL;
		Set<String> userRoleSet = (Set<String>)Sessions.getCurrent().getAttribute("userRoleSet");
		if(userRoleSet.contains("BEE_ROLEHASH_LOCK_AND_UNLOCK_A_HTMLBLOCK")){
			userRole=	PropertyUtil.getPropertyValueFromDB("BEE_ROLEHASH_LOCK_AND_UNLOCK_A_HTMLBLOCK");
			Clients.evalJavaScript("userrole = \""+userRole+"\";");
		}
		else if(userRoleSet.contains("BEE_ROLEHASH_LOCKED")){
			userRole=	PropertyUtil.getPropertyValueFromDB("BEE_ROLEHASH_LOCKED");
			Clients.evalJavaScript("userrole = \""+userRole+"\";");
		}
		logger.info(" ===========user role :: "+userRole);
		return userRole;
	}catch (Exception e) {
		logger.error("Exception ", e);
		return "empty";
	}		
	}*/
	
/*	public void saveInMyTemplate(String dRTemplateName,String beeHtmlContent,String JsonContent){
		logger.info(" ======inside saveInMyTemplate 22======= ");

		try {

			logger.info("publish Temaplate Name "+dRTemplateName);


			if ((isEdit!=null || isDraftEdit!=null) && digitalReceiptMyTemplate != null) {

				beeHtmlContent = beeHtmlContent.replace("<!--##BEGIN ITEMS##-->","##BEGIN ITEMS##")
						.replace("<!--##END ITEMS##-->","##END ITEMS##");
				beeHtmlContent = beeHtmlContent.replace("<!--##BEGIN PAYMENTS##-->", "##BEGIN PAYMENTS##")
						.replace("<!--##END PAYMENTS##-->", "##END PAYMENTS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
						.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
						.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
						.replace("<div id=\"##END REF##\"></div>","##END REF##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
						.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");

				DigitalReceiptMyTemplate dRMyTemplate = digitalReceiptMyTemplate;
				dRMyTemplate.setName(dRTemplateName);
				dRMyTemplate.setContent(beeHtmlContent);
				dRMyTemplate.setModifiedDate(Calendar.getInstance());
				dRMyTemplate.setUserId(currentUser.getUserId());
				dRMyTemplate.setJsonContent(JsonContent);
				dRMyTemplate.setEditorType(Constants.EDITOR_TYPE_BEE);
				//	dRMyTemplate.setFolderName(folderName);

				dRMyTemplate.setAutoSaveHtmlContent(null);
				dRMyTemplate.setAutoSaveJsonContent(null);

				dRMyTemplate.setModifiedby(currentUser.getUserId());

				boolean isConfigured = digitalReceiptUserSettingsDao.isTemplateConfigured(dRMyTemplate.getMyTemplateId());
				if(isConfigured){

					int confirm = Messagebox.show("Changes done in the template will be published " + "and will reflect in all digital receipts sent in future. " + "Do you want to continue?", "Information", 
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1) return;

				} else {

					int confirm = Messagebox.show("Changes done in the template will be published " + "and draft version will be deleted. " + "Do you want to continue?", "Information", 
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1) return;
				}
				if(dRMyTemplate.getJsonContent() != null && dRMyTemplate.getContent() != null){

					digiRecptMyTemplatesDaoForDML.saveOrUpdate(dRMyTemplate);

					*//****
					 * Following Attributes to navigate the respective folder in DR_lanchingScreen
					 ***//*
					session.setAttribute("newTemplate",true);
					//session.setAttribute("newTemplatefolder",folderName);

					autoSaveSession.removeAttribute("autoSaveDRTemplate");

					MessageUtil.setMessage("Template saved successfully.","green", "top");
					if(newTemplate){
						isEdit = (Boolean)session.getAttribute("isDRTemplateEdit");
						Object dRTemplate = session.getAttribute("DRTemplate");
						if(dRTemplate instanceof DigitalReceiptMyTemplate) digitalReceiptMyTemplate = (DigitalReceiptMyTemplate)dRTemplate;

						newTemplate=false;
						isEdit=true;
						digitalReceiptMyTemplate = dRMyTemplate;
					}

					winId.setVisible(false);
					if(publishandclose){
						publishandclose=false;
						session.removeAttribute("isDRTemplateEdit");
						session.removeAttribute("isDraftTemplateEdit");

						session.removeAttribute("DRTemplate");
						session.removeAttribute("TempSubAccount"); 
						Redirect.goTo(PageListEnum.EMPTY);
						Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPTS);
					}
				}
			}
			else {
				beeHtmlContent = beeHtmlContent.replace("<!--##BEGIN ITEMS##-->","##BEGIN ITEMS##")
						.replace("<!--##END ITEMS##-->","##END ITEMS##");
				beeHtmlContent = beeHtmlContent.replace("<!--##BEGIN PAYMENTS##-->", "##BEGIN PAYMENTS##")
						.replace("<!--##END PAYMENTS##-->", "##END PAYMENTS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN ITEMS##\"></div>","##BEGIN ITEMS##")
						.replace("<div id=\"##END ITEMS##\"></div>","##END ITEMS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN PAYMENTS##\"></div>", "##BEGIN PAYMENTS##")
						.replace("<div id=\"##END PAYMENTS##\"></div>", "##END PAYMENTS##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN REF##\"></div>","##BEGIN REF##")
						.replace("<div id=\"##END REF##\"></div>","##END REF##");
				beeHtmlContent = beeHtmlContent.replace("<div id=\"##BEGIN CHANGE PAYMENTS##\"></div>", "##BEGIN CHANGE PAYMENTS##")
						.replace("<div id=\"##END CHANGE PAYMENTS##\"></div>", "##END CHANGE PAYMENTS##");

				DigitalReceiptMyTemplate dRMyTemplates = new DigitalReceiptMyTemplate();

				dRMyTemplates.setName(dRTemplateName);
				dRMyTemplates.setContent(beeHtmlContent);
				dRMyTemplates.setCreatedDate(Calendar.getInstance());
				dRMyTemplates.setModifiedDate(Calendar.getInstance());
				dRMyTemplates.setUserId(currentUser.getUserId());
				dRMyTemplates.setCreatedBy(currentUser.getUserId());

				dRMyTemplates.setJsonContent(JsonContent);
				dRMyTemplates.setEditorType(Constants.EDITOR_TYPE_BEE);
				//	dRMyTemplates.setFolderName(folderName);

				dRMyTemplates.setAutoSaveHtmlContent(null);
				dRMyTemplates.setAutoSaveJsonContent(null);

				dRMyTemplates.setModifiedby(currentUser.getUserId());

				boolean isConfigureds = digitalReceiptUserSettingsDao.isTemplateConfigured(dRMyTemplates.getMyTemplateId());
				if(!isConfigureds){

					int confirm = Messagebox.show("Changes done in the template will be published " + "and will reflect in all digital receipts sent in future. " + "Do you want to continue?", "Information", 
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1) return;

				} else {

					int confirm = Messagebox.show("Changes done in the template will be published " + "and draft version will be deleted. " + "Do you want to continue?", "Information", 
							Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1) return;
				}
				if(dRMyTemplates.getJsonContent() != null && dRMyTemplates.getContent() != null){

					digiRecptMyTemplatesDaoForDML.saveOrUpdate(dRMyTemplates);

					*//****
					 * Following Attributes to navigate the respective folder in DR_lanchingScreen
					 ***//*
					session.setAttribute("newTemplate",true);
					//	session.setAttribute("newTemplatefolder",folderName);


					autoSaveSession.removeAttribute("autoSaveDRTemplate");

					MessageUtil.setMessage("Template saved successfully.","green", "top");
					if(newTemplate){
						isEdit = (Boolean)session.getAttribute("isDRTemplateEdit");
							Object dRTemplate = session.getAttribute("DRTemplate");
							if(dRTemplate instanceof DigitalReceiptMyTemplate) digitalReceiptMyTemplate = (DigitalReceiptMyTemplate)dRTemplate;

						newTemplate=false;
						isEdit=true;
						digitalReceiptMyTemplate = dRMyTemplates;
					}

					winId.setVisible(false);
					if(publishandclose){
						session.removeAttribute("isDRTemplateEdit");
						session.removeAttribute("isDraftTemplateEdit");

						session.removeAttribute("DRTemplate");

						//concurrence issue
						if(digitalReceiptMyTemplate!=null)
							//digiRecptMyTemplatesDaoForDML.setTemplateFlag(digitalReceiptMyTemplate.getMyTemplateId(),false);

							Redirect.goTo(PageListEnum.EMPTY);
						Redirect.goTo(PageListEnum.CAMPAIGN_E_RECEIPTS);
					}
				}
			}
		}
		catch(DataIntegrityViolationException die) {
			MessageUtil.setMessage("Template with given name already exist in the selected sub account,Please provide another name", "red","top");
			logger.error("** Exception : while saving template in to MyTemplates", (Throwable)die);

		} catch(Exception e) {
			logger.error("** Exception : while saving the template in MyTemplates", (Throwable)e);

		}
	}*/
	public static List<String> getInvcDRBRCode(){

		try {
			Clients.evalJavaScript("var barCodes80 = [];");
			List<String> couponsPhList = new ArrayList<String>();
			List<String> imageCouponsPhList = new ArrayList<String>();
			List<String> imageCouponsPhBeeList = new ArrayList<String>();
			CouponsDao couponsDao =  (CouponsDao)SpringUtil.getBean("couponsDao");
			
			if(true){	
				//BARCODE_LINEAR
				BitMatrix bitMatrixINVC = null;
				String invcBarcode_message = "DUMMY";
				String invcId = Constants.DRBC_TOKEN+"barInvcId";//CC_2739_testpromo1_LN_200_50
				String couponIdStr = Constants.DRBC_TOKEN + "invcIdStr_"+invcBarcode_message+ 	
						"_LN_"+DR_BARCODE_WIDTH
						+"_"+DR_BARCODE_HEIGHT;
				
				String docsId = Constants.DRBC_DOCSID_TOKEN+"barDocsId";//CC_2739_testpromo1_LN_200_50
				String couponIdStr1 = Constants.DRBC_DOCSID_TOKEN + "docsIdStr_"+invcBarcode_message+ 	
						"_LN_"+DR_BARCODE_WIDTH
						+"_"+DR_BARCODE_HEIGHT;
				 
				bitMatrixINVC = new Code128Writer().encode(invcBarcode_message, BarcodeFormat.CODE_128, DR_BARCODE_WIDTH, DR_BARCODE_HEIGHT,null);
				String bclnImg = GetUser.getUserName()+File.separator+
						"Preview"+File.separator+"LINEAR"+File.separator+couponIdStr+".png";

				String bclnImg1 = GetUser.getUserName()+File.separator+
						"Preview"+File.separator+"LINEAR"+File.separator+couponIdStr1+".png";
				
				String INVC_BARCODE_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg;
				logger.info("INVC_BARCODE_CODE_URL :"+INVC_BARCODE_CODE_URL);
				String drBcPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg;
				logger.info("drBcPreviewUrl :"+drBcPreviewUrl);
				
				String DOCSID_BARCODE_CODE_URL = PropertyUtil.getPropertyValue("usersParentDirectory")+File.separator+bclnImg1;
				logger.info("DOCSID_BARCODE_CODE_URL :"+DOCSID_BARCODE_CODE_URL);
				String drBcDocsIDPreviewUrl = PropertyUtil.getPropertyValue("ApplicationUrl")+"UserData"+File.separator+bclnImg1;
				logger.info("drBcDocsIDPreviewUrl :"+drBcDocsIDPreviewUrl);

				File myTemplateFile = new File(INVC_BARCODE_CODE_URL);
				File parentDir = myTemplateFile.getParentFile();
				if(!parentDir.exists()) {

					parentDir.mkdir();
				}

				File myTemplateFile1 = new File(DOCSID_BARCODE_CODE_URL);
				File parentDir1 = myTemplateFile1.getParentFile();
				if(!parentDir1.exists()) {

					parentDir1.mkdir();
				}

				if(!myTemplateFile.exists()) {

					MatrixToImageWriter.writeToStream(bitMatrixINVC, "png", new FileOutputStream(
							new File(INVC_BARCODE_CODE_URL)));	
				}
				
				if(!myTemplateFile1.exists()) {

					MatrixToImageWriter.writeToStream(bitMatrixINVC, "png", new FileOutputStream(
							new File(DOCSID_BARCODE_CODE_URL)));	
				}


				String invcBarCodeStr ="For INVC" + Constants.DELIMETER_DOUBLECOLON +
						"<img id="+invcId+" src="+drBcPreviewUrl+" width="+DR_BARCODE_WIDTH +
						" height="+DR_BARCODE_HEIGHT+" />";

				String docsIdBarCodeStr ="For DOCSID" + Constants.DELIMETER_DOUBLECOLON +
						"<img id="+docsId+" src="+drBcDocsIDPreviewUrl+" width="+DR_BARCODE_WIDTH +
						" height="+DR_BARCODE_HEIGHT+" />";


				imageCouponsPhList.add(invcBarCodeStr);
				imageCouponsPhList.add(docsIdBarCodeStr);
				
				String[] imagePHArr= invcBarCodeStr.split(Constants.DELIMETER_DOUBLECOLON );
				String InvcImgName = imagePHArr[0];
				String InvcImgValue = imagePHArr[1];
				
				String[] imagePHArr1= docsIdBarCodeStr.split(Constants.DELIMETER_DOUBLECOLON );
				String docsIDImgName1 = imagePHArr1[0];
				String docsIDImgValue1 = imagePHArr1[1];

				String barCodeImg="{name: '" +InvcImgName+ "', value: '"+InvcImgValue+"'}";
				String barCodeImg1="{name: '" +docsIDImgName1+ "', value: '"+docsIDImgValue1+"'}";

				String barcodeImages= barCodeImg+","+barCodeImg1;
				//logger.info("barcodeImages..........."+barcodeImages);
				Clients.evalJavaScript("barCodes80 = ["+barcodeImages+"];");	
				//Clients.evalJavaScript("var specialLinks79 = [];");

			}
	return couponsPhList;
		} catch (Exception e) {
			logger.error("Exception ::" , e);;
			return null;
		}

	}

}
