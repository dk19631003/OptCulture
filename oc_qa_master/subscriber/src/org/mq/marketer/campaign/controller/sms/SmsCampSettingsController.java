package org.mq.marketer.campaign.controller.sms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.mq.marketer.campaign.beans.CampaignSchedule;
import org.mq.marketer.campaign.beans.SMSCampaignSchedule;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.CountryReceivingNumbers;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.OrgSMSkeywords;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.ReferralProgram;
import org.mq.marketer.campaign.beans.SMSCampaignSchedule;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.SmsQueue;
import org.mq.marketer.campaign.beans.TransactionalTemplates;
import org.mq.marketer.campaign.beans.UrlShortCodeMapping;
import org.mq.marketer.campaign.beans.UserCampaignCategories;
import org.mq.marketer.campaign.beans.UserCampaignExpiration;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.SubscriptionDetails;
import org.mq.marketer.campaign.controller.campaign.CampFinalController;
import org.mq.marketer.campaign.controller.contacts.SegmentEnum;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.CampaignsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CountryReceivingNumbersDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.OrgSMSkeywordsDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSCampaignScheduleDao;
import org.mq.marketer.campaign.dao.SMSCampaignScheduleDaoForDML;
import org.mq.marketer.campaign.dao.SMSCampaignsDao;
import org.mq.marketer.campaign.dao.SMSCampaignsDaoForDML;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.SmsQueueDao;
import org.mq.marketer.campaign.dao.TransactionalTemplatesDao;
import org.mq.marketer.campaign.dao.TransactionalTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.UrlShortCodeMappingDao;
import org.mq.marketer.campaign.dao.UrlShortCodeMappingDaoForDML;
import org.mq.marketer.campaign.dao.UserCampaignCategoriesDao;
import org.mq.marketer.campaign.dao.UserCampaignExpirationDao;
import org.mq.marketer.campaign.dao.UserCampaignExpirationDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
//import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.ClickHouseSalesQueryGenerator;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.RightsEnum;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.data.dao.ReferralProgramDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;



public class SmsCampSettingsController extends GenericForwardComposer {
	
	static final String HTTPS_TOKEN = "https://";
	
	private UserSMSSenderIdDao userSMSSenderIdDao;
	private MailingListDao mailingListDao;
	private SMSCampaignsDao smsCampaignsDao;
	private SMSCampaignsDaoForDML smsCampaignsDaoForDML;
	private ReferralProgramDao referralprogramDao;

	private CampaignsDao campaignsDao;
	private CampaignsDaoForDML campaignsDaoForDML;
	private SegmentRulesDao segmentRulesDao;
	private ContactsDao contactsDao;
	private SMSCampaignScheduleDao smsCampaignScheduleDao;
	private SMSCampaignScheduleDaoForDML smsCampaignScheduleDaoForDML;
	private CouponsDao couponsDao;
	private CouponCodesDao couponCodesDao;
	private OrgSMSkeywordsDao orgSMSkeywordsDao;
	private SMSSettingsDao smsSettingsDao;
	private List<SMSSettings> smsSettings;
	private TransactionalTemplatesDao  transactionalTemplatesDao;
	private TransactionalTemplatesDaoForDML  transactionalTemplatesDaoForDML;
	private EmailQueueDao emailQueueDao ;
	private EmailQueueDaoForDML emailQueueDaoForDML ;
	private UserCampaignCategoriesDao userCampaignCategoriesDao;
	private CountryReceivingNumbersDao countryReceivingNumbersDao;
	
	private Radio entireListRId, optInRId;
	
	private Image chooseDateTimeImgId, autoSenIdImg;
	private Div multipleSenderIdDivId;
	private Div navDivId, createNewSegDivId, selectedListDivId, dispRuleDivId,
				frqDivId, prtDtDivId, previewWin$contentDivId, listNamesDivId, insertKeywrdDivId;
	private Div tranSactinalMsgDivId,outBoundedDivId,sendCampLblDivId;//transactionTypeDivId
	
	private Html previewWin$html;
	private Window previewWin;
	private Long userId;
	private A step1AId,step2AId,step3AId;
	//private Set<Long> userIdsSet = GetUser.getUsersSet();
	
	private Set<Long> listIdsSet; 
	private Set<Long> segmentIdsSet; 
	
	private Label recipentsSourceLblId;
	private MyDatebox prtDtBxId;
	private MyDatebox startDtBxId;
	private MyDatebox endDtBxId;
	private MyDatebox resendOptionWinId$resendOptionDbId;
	
	private Textbox smsCampNameTbId, senderIdTbId, SMSMsgTbId, mblNoTxtBoxId, testSMSTbId, charCountTbId,transTempCharCountTxtBxId, caretPosTB,transCaretPosTB, linkURLTxtBoxId,createTransTempWinId$linkURLTxtBoxId_createNewTx,
					templateIdTxtBoxId;
	private Label smsCampNameStatusLblId, senderIdLblId, campNameLblId, campTypeLblId, schErrorLblId,resendOptionWinId$errMsgLblId, headerLblId,categoryNameLblId,optInTextDispLblId;
	
	private Listbox frqLbId, resendOptionWinId$resendOptionLbId, insertMergetagsLbId, insertCouponLbId, insertKeywordLbId, insertBarcodeLbId,multipleSenderIdsLbId,
				createTransTempWinId$insertMergetagsLbId_createNewTx,createTransTempWinId$insertCouponLbId_createNewTx,createTransTempWinId$insertBarcodeLbId_createNewTx;
	
	private Rows schedGrdRowsId;
	
	private Radiogroup msgTypeRgId;

	private Combobox transTempCmbBxId;
	//private boolean nameExists;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
	private Users currUser;
	private SMSCampaigns smsCampaign;
	private String isEdit;
	private String draftStatus;
	private Session session;
	private List<Calendar> tempList;
	private  List<SMSCampaignSchedule> smsCampScheduleList;//contains all the SMSCampaignSchedule objects
	private Map<Calendar, Rows> rowsMap;
	private Map<Calendar, Row> rowMap;
	private List<SMSCampaignSchedule> tempSmsCampScheduleList;
	private  static String ERROR_STYLE = "border:1px solid #DD7870;";
	private  static String NORMAL_STYLE = "border:1px solid #7F9DB9;";
	
	/****** will be utilized to know the actions performing with schedules ******/
	private static String TB_ACTION_DELETE = "DELTE";
	private static String TB_ACTION_EDIT = "EDIT";
	private static String TB_ACTION_RESEND = "RESEND";
	private static String TB_ACTION = "TOOLBUTTON ACTION";
	private final String ATTRIBUTE_SOURCE = "Source";
	
	private static byte MAX_RESEND_LEVEL = 2;
	private final String  SENDERID_ATTRIBUTE = "SENDERID";
	private final String  SENDERID_ATTRIBUTE_REQUIRED = "REQUIRED";
	
	private MyListener myListener = new MyListener();
	private UserCampaignExpirationDao userCampaignExpirationDao;
	private UserCampaignExpirationDaoForDML userCampaignExpirationDaoForDML;
	private UrlShortCodeMappingDao urlShortCodeMappingDao; 
	private UrlShortCodeMappingDaoForDML urlShortCodeMappingDaoForDML; 
	private Div includedPromoCodesDivId,campTypeDiv,optInDiv,finalCampaignType,approveTemplateDivId;
	//String senderId = "";
	private Listbox categoryLbId;
	private boolean pastDate;
	
	private boolean areExpiredPromosInAKeyword;
	private String keywordsUsed;
	private Textbox createTransTempWinId$transTempEditId;
	private Toolbarbutton editNewTempTBId;
	private boolean displayPRTemplateApproval;
	
	public SmsCampSettingsController() {
		
		userId = GetUser.getUserId();
		this.userSMSSenderIdDao = (UserSMSSenderIdDao)SpringUtil.getBean("userSMSSenderIdDao");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		smsCampaignsDao = (SMSCampaignsDao)SpringUtil.getBean("smsCampaignsDao");
		smsCampaignsDaoForDML = (SMSCampaignsDaoForDML)SpringUtil.getBean("smsCampaignsDaoForDML");
		segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
		contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		smsCampaignScheduleDao = (SMSCampaignScheduleDao)SpringUtil.getBean("smsCampaignScheduleDao");
		smsCampaignScheduleDaoForDML = (SMSCampaignScheduleDaoForDML)SpringUtil.getBean("smsCampaignScheduleDaoForDML");
		urlShortCodeMappingDao = (UrlShortCodeMappingDao)SpringUtil.getBean("urlShortCodeMappingDao");
		urlShortCodeMappingDaoForDML = (UrlShortCodeMappingDaoForDML)SpringUtil.getBean("urlShortCodeMappingDaoForDML");
		couponsDao = (CouponsDao)SpringUtil.getBean("couponsDao");
		couponCodesDao = (CouponCodesDao)SpringUtil.getBean("couponCodesDao");
		orgSMSkeywordsDao = (OrgSMSkeywordsDao)SpringUtil.getBean("orgSMSkeywordsDao");
		campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		campaignsDaoForDML = (CampaignsDaoForDML)SpringUtil.getBean("campaignsDaoForDML");

		transactionalTemplatesDao = (TransactionalTemplatesDao)SpringUtil.getBean("transactionalTemplatesDao");
		transactionalTemplatesDaoForDML = (TransactionalTemplatesDaoForDML )SpringUtil.getBean("transactionalTemplatesDaoForDML");
		smsSettingsDao = (SMSSettingsDao)SpringUtil.getBean("smsSettingsDao");
		emailQueueDao = (EmailQueueDao)SpringUtil.getBean("emailQueueDao");
		emailQueueDaoForDML = (EmailQueueDaoForDML)SpringUtil.getBean("emailQueueDaoForDML");
		countryReceivingNumbersDao =(CountryReceivingNumbersDao)SpringUtil.getBean("countryReceivingNumbersDao");
		smsCampScheduleList = new ArrayList<SMSCampaignSchedule>();
		rowsMap = new HashMap<Calendar, Rows>();
		rowMap = new HashMap<Calendar, Row>();
		tempSmsCampScheduleList = new ArrayList<SMSCampaignSchedule>();
		
		pastDate = false;
		areExpiredPromosInAKeyword = false;
		keywordsUsed = "";
		
		captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)SpringUtil.getBean("captiwayToSMSApiGateway");
		currUser = GetUser.getUserObj();
		
		session = Sessions.getCurrent();
		//smsCampaign = session.getAttribute("")
		isEdit = (String)session.getAttribute("editSmsCampaign"); 
		smsCampaign = (SMSCampaigns)session.getAttribute("smsCampaign");
		draftStatus = (String)session.getAttribute("smsDraftStatus");
		//org.mq.marketer.campaign.controller.sms.SmsCampSettingsController-getsenderIds
		
		if(SMSStatusCodes.optOutFooterMap.get(currUser.getCountryType())) { 
			
			smsSettings = smsSettingsDao.findByUser(currUser.getUserId());
		}
		/*smsSettingsDao = (SMSSettingsDao)SpringUtil.getBean("smsSettingsDao");
		if(currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_US)) {
			smsSettings = smsSettingsDao.findByUser(currUser.getUserId());
			
		}*/
		userCampaignCategoriesDao =(UserCampaignCategoriesDao)SpringUtil.getBean("userCampaignCategoriesDao");
		tempList = new ArrayList<Calendar>();
		 String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		 PageUtil.setHeader("Create SMS","",style,true);
		 
		 
		//added for sharing
			listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
			segmentIdsSet = (Set<Long>)session.getAttribute(Constants.SEGMENTIDS_SET);
		 
		
			if(session.getAttribute("isPasswordRequired")!=null) displayPRTemplateApproval  = (Boolean)session.getAttribute("isPasswordRequired");
	}
	
	private List<Div> divList = new ArrayList<Div>();
	private List<A> anchList = new ArrayList<A>();
	private List<UserSMSSenderId> multipleSenderIdList;
	private Map<String, String> multipleSenderIds_MsgTypeMap = new HashMap<String, String>();
	
	private Button saveAsDraftBtnId, saveAsDraftStep1BtnId, gotoStep2BtnId, step1BackBtnId, gotoStep3BtnId, saveAsDraftStep2BtnId,
				   approveTemplateIdBtnId;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		
		super.doAfterCompose(comp);
		
	//	SMSMsgTbId.setCtrlKeys("^v");
	//	SMSMsgTbId.addEventListener(Events.ON_CTRL_KEY, myListener);
	//	SMSMsgTbId.addEventListener(Events.ON_RIGHT_CLICK, myListener);
		
	
		
		
		Div mainNavDivId =(Div)Utility.getComponentById("mainNavDivId");
		Components.removeAllChildren(mainNavDivId);
		
		navDivId.setParent(mainNavDivId);
		navDivId.setVisible(true);
		mainNavDivId.setVisible(true);
		
		divList.add(step1DivId);
		divList.add(step2DivId);
		divList.add(step3DivId);
		
		
		anchList.add(step1AId);
		anchList.add(step2AId);
		anchList.add(step3AId);
		
		
		goToNextBtnId.setAttribute("stepNum", getVisibleDiv());
		
		//set default  date values
		MyCalendar currentCal = new MyCalendar((TimeZone) sessionScope.get("clientTimeZone"));
		currentCal.set(MyCalendar.MINUTE, currentCal.get(MyCalendar.MINUTE) + 15);
		prtDtBxId.setValue(currentCal);
		startDtBxId.setValue(new MyCalendar(currentCal));
		endDtBxId.setValue(new MyCalendar(currentCal));
		resendOptionWinId$resendOptionDbId.setValue(new MyCalendar(currentCal));
		
		List<UserSMSSenderId> senderIdList = getsenderIds();
		multipleSenderIdList = senderIdList;
		Map<String , String> senderIDMap = new HashMap<String, String>();// this map contains sms type and sender id as key-value pair
		String transactionalSenderId="";
		if(senderIdList != null) {
			
			for (UserSMSSenderId userSMSSenderId : senderIdList) {
				
				if(senderIDMap.containsKey(userSMSSenderId.getSmsType())) continue;
					
				senderIDMap.put(userSMSSenderId.getSmsType(), userSMSSenderId.getSenderId());
				
				if(userSMSSenderId.getSmsType().equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)) transactionalSenderId= userSMSSenderId.getSenderId();
				
				if(optInRId.getAttribute(SENDERID_ATTRIBUTE) == null && SMSStatusCodes.optInMap.get(currUser.getCountryType()) && 
						userSMSSenderId.getSmsType().equals(Constants.SMS_ACCOUNT_TYPE_OPTIN)) {
					optInRId.setAttribute(SENDERID_ATTRIBUTE, userSMSSenderId.getSenderId());
				}
			}
			
			if(currUser.getOptinRoute() != null && currUser.getOptinRoute().equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL) ){
				
				if(optInRId.getAttribute(SENDERID_ATTRIBUTE) == null && SMSStatusCodes.optInMap.get(currUser.getCountryType())) {
					optInRId.setAttribute(SENDERID_ATTRIBUTE, transactionalSenderId);
				
					}
			
				}
		}
		
		UserSMSGatewayDao userSMSGatewayDao = (UserSMSGatewayDao)SpringUtil.getBean("userSMSGatewayDao");
		OCSMSGatewayDao OCSMSGatewayDao = (OCSMSGatewayDao)SpringUtil.getBean("OCSMSGatewayDao");
		
		CountryReceivingNumbersDao countryReceivingNumbersDao =(CountryReceivingNumbersDao)SpringUtil.getBean("countryReceivingNumbersDao");
		List<UserSMSGateway> userSmsGateway = userSMSGatewayDao.findAllBy(currUser.getUserId(),"'PR'");
		String ocSMSGatewayIdsStr = Constants.STRING_NILL;
		if(userSmsGateway != null) {
			for (UserSMSGateway userSMSGateway : userSmsGateway) {
				
				if(!ocSMSGatewayIdsStr.isEmpty()) ocSMSGatewayIdsStr += Constants.DELIMETER_COMMA;
				
				ocSMSGatewayIdsStr += userSMSGateway.getGatewayId().longValue();
			}
		}
		
		Map<String, String> msgTypeMap = new HashMap<String, String>();
		
		msgTypeMap = SMSStatusCodes.campTypeMap.get(currUser.getCountryType());
		multipleSenderIds_MsgTypeMap = msgTypeMap;
		Set<String> mapKey = msgTypeMap.keySet();
		Radio rad;
		for(String key : mapKey) {
			if(ocSMSGatewayIdsStr.length()!=0){
				OCSMSGateway ocgateway = OCSMSGatewayDao.findById(ocSMSGatewayIdsStr);
				
				boolean isTwoWayEnable=true;
				if(key.equalsIgnoreCase(Constants.SMS_TYPE_NAME_2_WAY)){
					CountryReceivingNumbers countryReceivingNumbers = countryReceivingNumbersDao.getReceivingNumByCountryAndGateway(ocgateway.getCountryName(),ocgateway.getId());
					if(countryReceivingNumbers==null) isTwoWayEnable=false;
				}
				if(!isTwoWayEnable) continue;
			}

			rad = new Radio();
			rad.setLabel(key);// contains TWO-WAY or PROMOTIONAL etc...
			rad.setValue(msgTypeMap.get(key).split(Constants.ADDR_COL_DELIMETER)[0]);// contains TW or PR  etc...
			rad.setAttribute(SENDERID_ATTRIBUTE, senderIDMap.get(SMSStatusCodes.countryCampValueMap.get(currUser.getCountryType()).get(rad.getValue().toString())));// contains sender id
			logger.info("rad.getvalue()"+rad.getValue().toString());
			rad.setAttribute(SENDERID_ATTRIBUTE_REQUIRED, msgTypeMap.get(key).split(Constants.ADDR_COL_DELIMETER)[1]);// contains Y or N
			rad.setParent(msgTypeRgId);
			
		}
		msgTypeRgId.setSelectedIndex(0);
		
		//make opt-in visible
		List<SMSSettings> settings = smsSettingsDao.findByUser(currUser.getUserId());
				//if(settings!=null){
					if(settings!=null){
					for (SMSSettings eachSMSSetting : settings) {
			  			
			  			if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)
			  					&& eachSMSSetting.isEnable()) {
			  				optInDiv.setVisible(true);
			  				break;
			  			}
					}
				}else if(currUser.getOptinRoute()!=null){
					optInDiv.setVisible(true);
				}
				
		try{
			//added for UAE multiple sender ids
			/*if(currUser.getCountryCarrier() == Short.parseShort(Constants.SMS_COUNTRY_CODE_UAE)){
				populateMultipleSenderIdsLbId_MakeItVisibleInCase_2_OrMoreSenderIDs();// multiple wala
			}*/
			
			populateMultipleSenderIdsLbId_MakeItVisibleInCase_2_OrMoreSenderIDs();// multiple wala
			
		}catch(Exception e){
			logger.error("Exception  >>> ",e);
		}
		
		onCheck$msgTypeRgId();
		
		//senderIdTbId.setAttribute(SENDERID_ATTRIBUTE_REQUIRED, senderId);//Value(getsenderIds());
		
		senderIdTbId.setText(msgTypeRgId.getSelectedItem().
				getAttribute(SENDERID_ATTRIBUTE_REQUIRED).toString().equals("N") ? 
						Constants.TWO_WAY_PROMO_TEXT_ID : msgTypeRgId.getSelectedItem().
						getAttribute(SENDERID_ATTRIBUTE).toString());
		
		senderIdTbId.setAttribute(SENDERID_ATTRIBUTE, msgTypeRgId.getSelectedItem().
				getAttribute(SENDERID_ATTRIBUTE).toString());
		
		if(msgTypeRgId.getSelectedItem().
				getAttribute(SENDERID_ATTRIBUTE_REQUIRED).toString().equals("N") ) {
			
			autoSenIdImg.setVisible(true);
		}
		senderIdTbId.setDisabled(true);
		/*if(currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)){
			
			senderIdTbId.setText(Constants.TWO_WAY_PROMO_TEXT_ID);
		}*/
		
		if(SMSStatusCodes.optOutFooterMap.get(currUser.getCountryType())) {
				
			if(smsSettings != null) {
				
					
				SMSSettings optinSettings = null;
		  		SMSSettings optOutSettings = null;
		  		SMSSettings helpSettings = null;
		  		
		  		String messageHeader = Constants.STRING_NILL;
		  		for (SMSSettings eachSMSSetting : smsSettings) {
		  			
		  			if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
		  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
		  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
		  			
		  		}
				if(optinSettings != null && messageHeader.isEmpty()) messageHeader = optinSettings.getMessageHeader();
				else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
				else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
				
				headerLblId.setValue(messageHeader);
				//String optoutKeyWord = smsSettings.getOptoutKeyword();
				if(optOutSettings != null) {
					if(!currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_UAE))
					optOutLblId.setValue("Reply "+optOutSettings.getKeyword()+" 2 Optout" );
					else
						optOutLblId.setValue(optOutSettings.getKeyword());
				}else{
					optOutLblId.setValue(PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
				}
				
				getCharCount("");
			}else{
			getCharCount("");
			}
			//twoWayMsgTypeRId.setVisible(true);
			sendTypeRgId.setVisible(true);
			//transactionalMsgTypeRId.setVisible(false);
			tranSactinalMsgDivId.setVisible(false);
			outBoundedDivId.setVisible(true);
					
			if((SMSStatusCodes.campTypeMap.get(currUser.getCountryType())).containsKey(Constants.SMS_TYPE_NAME_TRANSACTIONAL)){
				//optOutLblId.setValue("");
				transacTempSettings();
				transTempCmbBxId.setSelectedIndex(0);
				
			}	
			// sender id us  call map for readios
		}else { 
				
				headerLblId.setValue("");
				optOutLblId.setValue("");
				transacTempSettings();
				transTempCmbBxId.setSelectedIndex(0);
				
				/*if(currUser.getMsgChkType() != null && currUser.getMsgChkType().equals(Constants.SMS_TYPE_TRANSACTIONAL)) {
					
				//	transactionalMsgTypeRId.setVisible(true);
				//	transactionalMsgTypeRId.setChecked(true);
					tranSactinalMsgDivId.setVisible(false);
					outBoundedDivId.setVisible(true);
					
					
				}*/
				
				
				//	transactionalMsgTypeRId.setVisible(false);
				//	transactionalMsgTypeRId.setChecked(false);
				
				
			//	outBoundMsgTypeRId.setLabel("Promotional");
			//	twoWayMsgTypeRId.setVisible(false); 
				/*sendCampLblDivId.setVisible(false);
				sendTypeRgId.setVisible(false);
				SMSMsgTbId.setReadonly(true);*/
				
				//Get and set the Transactional Templates
				
				// sender id india  call map for readios if promo no sender id some alert is there for this
				// if transactional send to invisible
				// if optin contact selected in india then check for optin account in us ok
		}

		//TODO :1.enable save, back, save as draft options well
		if( (isEdit!=null && isEdit.equalsIgnoreCase("edit") ) || 
				(draftStatus != null  && !draftStatus.equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO) )) {
			
			saveAsDraftStep1BtnId.setVisible(false);
			//saveAsDraftBtnIdAtTop.setVisible(false);
			gotoStep2BtnId.setLabel("Save");
			//saveBtnIdAtTop.setLabel("Save");
			step1BackBtnId.setVisible(true);
			
			saveAsDraftStep2BtnId.setVisible(false);
			gotoStep3BtnId.setLabel("Save");
			
			
		}else {
			
			saveAsDraftStep1BtnId.setVisible(true);
			//saveAsDraftBtnIdAtTop.setVisible(true);
			gotoStep2BtnId.setLabel("Next");
			//saveBtnIdAtTop.setLabel("Next");
			step1BackBtnId.setVisible(false);
		}
		
		if(listIdsSet != null && listIdsSet.size() > 0){
			
			// Set Editor PH values.
			Set<MailingList> set = new HashSet<MailingList>();
			//set.addAll(mailingListDao.findAllByUser(userIdsSet));
			
			set.addAll(mailingListDao.findByIds(listIdsSet));
			getPlaceHolderList(set);
		}
		
		getCouponsIfAny();
		getKeywordsIfAny();
		
		
		
		//Added for Campaign categories
		
		
		if(currUser.getSubscriptionEnable() ){
     		getCampCategorties();
     	
     	}else{
     		categoryLbId.setDisabled(true);
     	}
		
		// one
		
		helpImgId.setVisible(categoryLbId.isDisabled() && !((String) msgTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL));
     	/*onCheck$outBoundMsgTypeRId();
     	onCheck$transactionalMsgTypeRId();
     	
		*/
		
		if(smsCampaign != null) {
			
			editSMSCampaign(smsCampaign);
			
		}
		
		
		try{
 			
 			Set<String> userRoleSet = (Set<String>)session.getAttribute("userRoleSet");
 			//logger.debug("userRoleSet ::"+userRoleSet);
 			
 			if(userRoleSet != null) {
 				
 				createNewSegDivId.setVisible(userRoleSet.contains(RightsEnum.MenuItem_CreateSegment_VIEW));
 				/*if(!userRoleSet.contains(Constants.ROLE_USER_BASIC)) {
 					
 					createNewSegDivId.setVisible(true);
 					
 				}//if
*/ 				
 				
 			}//if
 			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::" , e);
		}
		
		
		
		try{
			boolean isSenderIdSelectionNotAllowed = isCampaignDraftedInLastStep_OR_Sent(smsCampaign);
			if(isSenderIdSelectionNotAllowed){
				multipleSenderIdsLbId.setDisabled(true);
			}else{
				//else let it be as enabled
			}
		}catch(Exception e){
			logger.error("Exception >>> ",e);
		}
		
		
		
		//added in 2.4.6
		if(smsCampaign != null)
			populateListOfIncludedPromoCodesLabel();
		logger.info("DoAfterCompose of SenderTB Value........"+senderIdTbId.getValue());
		
		try{
			logger.info("DoAfterCompose of actual Sender id  Value........"+(senderIdTbId.getAttribute(SENDERID_ATTRIBUTE).toString()));
		}catch(Exception e){
			logger.error("Exception >>> ",e);
		}
		
		smsCampNameTbId.setFocus(true);
		LBFilterEventListener.lbFilterSetup(dispsegmentsLbId); 
		LBFilterEventListener.lbFilterSetup(dispMlListsLBoxId);
		
		/*if(currUser.getCountryType().equalsIgnoreCase("India")||currUser.getCountryType().equalsIgnoreCase("UAE")){
			campTypeDiv.setVisible(false);
			optInDiv.setVisible(false);
			finalCampaignType.setVisible(false);
		}*/
		/*if(currUser.getCountryType().equalsIgnoreCase("UAE")){
			campTypeDiv.setVisible(false);
			optInDiv.setVisible(false);
			finalCampaignType.setVisible(false);
		}
		if(currUser.getCountryType().equalsIgnoreCase("India")) optInDiv.setVisible(false);*/
		if(smsCampaign != null) {
		templateIdTxtBoxId.setValue(smsCampaign.getTemplateRegisteredId()!=null && !smsCampaign.getTemplateRegisteredId().isEmpty()?
				smsCampaign.getTemplateRegisteredId():"");
		}
	} //doAfterCompose
	
	public void onChanging$searchBoxId(InputEvent event) {
		int count=dispsegmentsLbId.getItemCount();
			for (; count > 0; count--) {
				dispsegmentsLbId.removeItemAt(count - 1);
			}
		String key = event.getValue();
		logger.info("got the key ::" + key);
		List<SegmentRules> segList = segmentRulesDao.searchBy(segmentIdsSet,key);
 		if(segList != null) {
 			
 			Listitem listItem=null;
	 		for (SegmentRules segRule : segList) {
	 			listItem = new Listitem();
	 			listItem.setValue(segRule);
	 			listItem.setLabel(segRule.getSegRuleName()); 
	 			listItem.setParent(dispsegmentsLbId); 
			}	
		}
 		enableSegment(); 
	}
	public void onChanging$searchBoxIdforlist(InputEvent event){
 			int count=dispMlListsLBoxId.getItemCount();
 			for (; count > 0; count--) {
 				dispMlListsLBoxId.removeItemAt(count - 1);
 			}
		String key = event.getValue();
          Listitem listItem;
          List<MailingList> mlList=  mailingListDao.searchMailBy(listIdsSet,key);
          if(mlList != null) {
	 		for (MailingList mailingList : mlList) {
	 			listItem = new Listitem();
	 			listItem.setValue(mailingList);
	 			listItem.setLabel(mailingList.getListName());
	 			listItem.setParent(dispMlListsLBoxId); 
			}
 		}
          enableMailingList();
	}
	
	
	//added in 2.4.6
	private void populateListOfIncludedPromoCodesLabel() {
		
		
		
		
		try {
			Components.removeAllChildren(includedPromoCodesDivId);
			Set<String> phSet =  new HashSet<String>();
			
			String keywordsInThisCampaign = "";
			String content = "";
			
			if(smsCampaign == null){
				
				if(SMSMsgTbId.getValue().trim().length() == 0) {
					Label label1 = new Label("Discount Code(s) used in campaign:   ");
					label1.setParent(includedPromoCodesDivId);
					Label label = new Label("--");
					label.setParent(includedPromoCodesDivId);
					
					return;
					
				}else{
					phSet = findCoupPlaceholders(SMSMsgTbId.getValue());
					
					content = SMSMsgTbId.getValue();
					if(content != null)
						keywordsInThisCampaign = getKeywordsInMessage(SMSMsgTbId.getValue(), currUser.getUserOrganization().getUserOrgId());
					else
						keywordsInThisCampaign = getKeywordsInMessage("", currUser.getUserOrganization().getUserOrgId());
					
				}
				
			}else{
				phSet = findCoupPlaceholders(smsCampaign.getMessageContent());
				
				content = smsCampaign.getMessageContent();
				if(content != null)
					keywordsInThisCampaign = getKeywordsInMessage(smsCampaign.getMessageContent(), currUser.getUserOrganization().getUserOrgId());
				else
					keywordsInThisCampaign = getKeywordsInMessage("", currUser.getUserOrganization().getUserOrgId());
			}
			
			
			
			String[] keywordsArray = keywordsInThisCampaign.split(",");
			
			if(keywordsArray != null){
				logger.info("keywordsArray >>>>>>> "+Arrays.deepToString(keywordsArray));
			}
			
			if(keywordsArray != null && keywordsArray.length > 0){
				
				List<OrgSMSkeywords> keywordsList = orgSMSkeywordsDao.getUserOrgSMSKeyWords(currUser.getUserOrganization().getUserOrgId());
				
				if(keywordsList != null && keywordsList.size() >0 ){
					
					for(String aKeywordOfThisCampaign : keywordsArray){
						
						
						for(OrgSMSkeywords aKeywordFromOrg : keywordsList){
							if(aKeywordOfThisCampaign.equalsIgnoreCase(aKeywordFromOrg.getKeyword())){
								phSet.addAll(findCoupPlaceholders(aKeywordFromOrg.getAutoResponse()));
								break;
							}
							
						}
					}
				}
				
			}
			
			
			
			//phSet = findCoupPlaceholders(smsCampaign.getMessageContent());
			String couponIdStr = "";
			logger.info("coupon ph set: "+phSet);
			
			for(String ph : phSet){
				if(ph.startsWith("CC_")){
					
					String[] phStr = ph.split("_");
					
					couponIdStr += couponIdStr.trim().length() <= 0 ? phStr[1] : ","+phStr[1];
				}
				
			}//for
			
			
			logger.info("couponIdStr >>> "+couponIdStr);
			
			if(couponIdStr.trim().length() == 0){
				Label label1 = new Label("Discount Code(s) used in campaign:   ");
				label1.setParent(includedPromoCodesDivId);
				Label label = new Label("--");
				label.setParent(includedPromoCodesDivId);
				
				return;
			}else{
				CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
				CouponCodesDao couponCodesDao= (CouponCodesDao)SpringUtil.getBean("couponCodesDao");

				Long orgId = currUser.getUserOrganization().getUserOrgId();
				
				List<Coupons> listOfCoupons = couponsDao.findCouponsByCoupIdsAndOrgId(couponIdStr, orgId);
				
				Label label1 = new Label("Discount Code(s) used in campaign:   ");
				label1.setParent(includedPromoCodesDivId);
				int numberOfCoupons=0;
				if(listOfCoupons != null && listOfCoupons.size() >0) {
					for (Coupons coupons : listOfCoupons) {
						
						numberOfCoupons++;
						Label label = new Label(coupons.getCouponName());
						label.setStyle("cursor:pointer;color:blue;text-decoration: underline");
						label.addEventListener("onClick", new MyListener());
						label.setParent(includedPromoCodesDivId);
						

						if( numberOfCoupons < listOfCoupons.size() ){
							
							Label label2 = new Label(", ");
							label2.setParent(includedPromoCodesDivId);
						}
						
						
					}
					
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	
	}
	
	
	private void populateMultipleSenderIdsLbId_MakeItVisibleInCase_2_OrMoreSenderIDs(){
		
		Components.removeAllChildren(multipleSenderIdsLbId);
		String msgType = msgTypeRgId.getSelectedItem().getValue();
		String smsType = (SMSStatusCodes.countryCampValueMap.get(currUser.getCountryType())).get(msgType);
		
		
		msgType = smsType;
		Listitem aSenderIdListItem = null;
		
		
				for(UserSMSSenderId aUserSMSSenderId : multipleSenderIdList){
					
					if(msgType.equalsIgnoreCase(aUserSMSSenderId.getSmsType())){
						
						aSenderIdListItem = new Listitem(aUserSMSSenderId.getSenderId());
						aSenderIdListItem.setParent(multipleSenderIdsLbId);
					}
					
					
				}
				
				
				if(multipleSenderIdsLbId.getChildren().size() >=2 ){
					multipleSenderIdDivId.setVisible(true);
					multipleSenderIdsLbId.setSelectedIndex(0);
					onSelect$multipleSenderIdsLbId();
				}else{
					multipleSenderIdDivId.setVisible(false);
				}
				
				
				
		
		
	}
	
	private void setRadioButtonAttributesForMultipleSenderIds(String msgType){
		
		String smsType = (SMSStatusCodes.countryCampValueMap.get(currUser.getCountryType())).get(msgType);
		
		
		
		Components.removeAllChildren(multipleSenderIdsLbId);
		
		Listitem aSenderIdListItem = null;
		
		
				for(UserSMSSenderId aUserSMSSenderId : multipleSenderIdList){
					
					if(smsType.equalsIgnoreCase(aUserSMSSenderId.getSmsType())){
						
						aSenderIdListItem = new Listitem(aUserSMSSenderId.getSenderId());
						aSenderIdListItem.setParent(multipleSenderIdsLbId);
					}
					
					
				}
				
				
				if(multipleSenderIdsLbId.getChildren().size() >=2 ){
					multipleSenderIdDivId.setVisible(true);
				}else{
					multipleSenderIdDivId.setVisible(false);
				}
				
				
				if(multipleSenderIdDivId.isVisible()){
					
					 for(Listitem item : multipleSenderIdsLbId.getItems()) {
						 if(item.getLabel().equals(smsCampaign.getSenderId())) {
							 item.setSelected(true);
							 break;
						 }
						 
						 
					 }
					
					onSelect$multipleSenderIdsLbId();
				}
	}
	
	
	public void onSelect$multipleSenderIdsLbId(){
		
		String selectedSenderID = multipleSenderIdsLbId.getSelectedItem().getLabel();
		
		
		
		Radio selectedRadioButton = msgTypeRgId.getSelectedItem();
		String key = selectedRadioButton.getLabel();
		
		selectedRadioButton.setAttribute(SENDERID_ATTRIBUTE, selectedSenderID);
		selectedRadioButton.setAttribute(SENDERID_ATTRIBUTE_REQUIRED, multipleSenderIds_MsgTypeMap.get(key).split(Constants.ADDR_COL_DELIMETER)[1]);
		
		/*msgTypeRgId.getSelectedItem().setAttribute(SENDERID_ATTRIBUTE, selectedSenderID);
		msgTypeRgId.getSelectedItem().setAttribute(SENDERID_ATTRIBUTE_REQUIRED, "Y");*/
		
		
		
		
		String msgType = msgTypeRgId.getSelectedItem().getValue();
		String smsType = (SMSStatusCodes.countryCampValueMap.get(currUser.getCountryType())).get(msgType);
		
		if(SMSStatusCodes.optInMap.get(currUser.getCountryType()) && smsType.equals(Constants.SMS_ACCOUNT_TYPE_OPTIN)) {
			optInRId.setAttribute(SENDERID_ATTRIBUTE, selectedSenderID);
		}
		
		
		
	
		
		setSenderID();
	}
	
	private boolean arePendingKeywordsForThisCampaign(){
		try{
			
			String keywordsInThisCampaign = "";
			String content = null;
			
			if(smsCampaign == null){
				content = SMSMsgTbId.getValue();
				if(content != null)
					keywordsInThisCampaign = getKeywordsInMessage(SMSMsgTbId.getValue(), currUser.getUserOrganization().getUserOrgId());
				else
					keywordsInThisCampaign = getKeywordsInMessage("", currUser.getUserOrganization().getUserOrgId());
			}else{
				content = smsCampaign.getMessageContent();
				if(content != null)
					keywordsInThisCampaign = getKeywordsInMessage(smsCampaign.getMessageContent(), currUser.getUserOrganization().getUserOrgId());
				else
					keywordsInThisCampaign = getKeywordsInMessage("", currUser.getUserOrganization().getUserOrgId());
			}
			
			
			String[] keywordsArray = keywordsInThisCampaign.split(",");
			
			String pendingKeywords = "";
			if(keywordsArray != null && keywordsArray.length > 0){
				
				List<OrgSMSkeywords> pendingKeywordsList = orgSMSkeywordsDao.findNonActiveKeywordBy(currUser.getUserOrganization().getUserOrgId());
				
				if(pendingKeywordsList != null && pendingKeywordsList.size() >0 ){
					
					for(String aKeywordOfThisCampaign : keywordsArray){
						
						
						for(OrgSMSkeywords aKeywordFromOrg : pendingKeywordsList){
							
							if(aKeywordOfThisCampaign.equalsIgnoreCase(aKeywordFromOrg.getKeyword())){
								pendingKeywords += pendingKeywords.trim().length() >0 ? ","+aKeywordFromOrg.getKeyword() : aKeywordFromOrg.getKeyword();
							}
							
						}
					}
				}
				
			}
			if(pendingKeywords.trim().length() != 0){
				MessageUtil.setMessage(	"Keywords "+pendingKeywords+" used in this campaign are in pending status. " ,
						"color:red", "TOP");
				return true;
			}
			
			return false;
			
			
		}catch(Exception e){
			logger.error("Exception occured >>> ",e);
			return true;
		}
	}
	private boolean areValidPromosIncludedInKeywordsForThisCampaign(){
		
		try{
			
			
			Set<String> phSet =  new HashSet<String>();
			
			String content = null;
			String keywordsInThisCampaign = "";
			
			if(smsCampaign == null){
				content = SMSMsgTbId.getValue();
				if(content != null)
					keywordsInThisCampaign = getKeywordsInMessage(SMSMsgTbId.getValue(), currUser.getUserOrganization().getUserOrgId());
				else
					keywordsInThisCampaign = getKeywordsInMessage("", currUser.getUserOrganization().getUserOrgId());
			}else{
				content = smsCampaign.getMessageContent();
				if(content != null)
					keywordsInThisCampaign = getKeywordsInMessage(smsCampaign.getMessageContent(), currUser.getUserOrganization().getUserOrgId());
				else
					keywordsInThisCampaign = getKeywordsInMessage("", currUser.getUserOrganization().getUserOrgId());
			}
			
			
			
			String[] keywordsArray = keywordsInThisCampaign.split(",");
			String keywords = "";
			if(keywordsArray != null){
				logger.info("Arrays.deepToString(keywordsArray) >>>>>>>> "+Arrays.deepToString(keywordsArray));
			}
			
			
			if(keywordsArray != null && keywordsArray.length > 0){
				
				List<OrgSMSkeywords> keywordsList = orgSMSkeywordsDao.getUserOrgSMSKeyWords(currUser.getUserOrganization().getUserOrgId());
				
				if(keywordsList != null && keywordsList.size() >0 ){
					
					for(String aKeywordOfThisCampaign : keywordsArray){
						
						
						for(OrgSMSkeywords aKeywordFromOrg : keywordsList){
							
							if(aKeywordOfThisCampaign.equalsIgnoreCase(aKeywordFromOrg.getKeyword())){
								keywords += keywords.trim().length() >0 ? ","+aKeywordFromOrg.getKeyword() : aKeywordFromOrg.getKeyword();
								phSet.addAll(findCoupPlaceholders(aKeywordFromOrg.getAutoResponse()));
								break;
							}
							
						}
					}
				}
				
			}
			
			
			String couponIdStr = "";
			if(phSet.size() > 0){
				
				
				for(String ph : phSet){
					
					if(ph.startsWith("CC_")){
						
						String[] phStr = ph.split("_");
						/*if(phStr.length > 2){
							
							MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
							return false;
						}*/
						
						couponIdStr += couponIdStr.trim().isEmpty() ? phStr[1].trim() : ","+phStr[1].trim();
						Long couponId = null;
						try{
						couponId = Long.parseLong(phStr[1]);
						}catch(Exception e){
							MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
							return false;
						}
						
						if(couponId != null){
							
							CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
							Long orgId = currUser.getUserOrganization().getUserOrgId();
							
							List<Coupons> couponsList = couponsDao.findCouponsByCoupIdsAndOrgId(""+couponId, orgId);
							if(couponsList == null){
								MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
								return false;
							}
						}
					}
					
				}
				
				if(!couponIdStr.trim().isEmpty()){
					logger.debug("couponIdStr :"+couponIdStr);
					List<Coupons> inValidCoupList = couponsDao.isExpireOrPauseCoupList(couponIdStr, currUser.getUserOrganization().getUserOrgId());
					
					if(inValidCoupList != null){
						
						String inValidCoupNames = "";
						if(inValidCoupList != null && inValidCoupList.size() >0) {
							
							for (Coupons coupons : inValidCoupList) {
								inValidCoupNames += inValidCoupNames.trim().length() >0 ? ","+coupons.getCouponName() : coupons.getCouponName();
							}
							MessageUtil.setMessage(	"The Discount Code(s) "+inValidCoupNames+" used in this campaign and came through keyword(s) "+keywords+" has/have either expired or paused status. " +
									" \n Please change the status of this Discount Code.",
									"color:red", "TOP");
							return false;
						}
						
					}
					
				}
				
				
			}
			
			return true;
		}catch(Exception e){
			logger.error("Exception so invalidating scheduling >>> ",e);
			return false;
		}
	}
	
	private String getExpiredOrPausedPromosOfThisKeyword(String checkThisKeyword){
		
		try{


			if(checkThisKeyword == null){
				logger.error("keyword passed to this method is null... returning with empty string");
				return "";
			}

			List<OrgSMSkeywords> keywordsList = orgSMSkeywordsDao.getUserOrgSMSKeyWords(currUser.getUserOrganization().getUserOrgId());

			OrgSMSkeywords reqdKeyword = null;

			if(keywordsList != null && keywordsList.size() > 0){
				for (OrgSMSkeywords keyword : keywordsList) {


					if(checkThisKeyword.equalsIgnoreCase(keyword.getKeyword())){
						reqdKeyword = keyword;
						break;
					}


				}
			}


			if(reqdKeyword == null){
				logger.error("required keyword not found in db... returning with empty string");
				return "";
			}


			if(reqdKeyword.getAutoResponse() == null){
				logger.error("required keyword has auto response value as null... returning empty string");
				return "";
			}




			String couponIds = getCouponIdsInvolvedInAText(reqdKeyword.getAutoResponse());

			if(couponIds == null || couponIds.trim().length() == 0){
				logger.error("required keyword has no coupons involved or has with invalid place holders");
				return "";
			}






			String invalidCoupNames = "";


			logger.debug("couponIdStr :"+couponIds);
			List<Coupons> coupList = couponsDao.isExpireOrPauseCoupList(couponIds, currUser.getUserOrganization().getUserOrgId());

			if(coupList != null){


				if(coupList != null && coupList.size() >0) {

					for (Coupons coupons : coupList) {
						invalidCoupNames += invalidCoupNames.trim().length() >0 ? ","+coupons.getCouponName() : coupons.getCouponName();
					}

				}

			}


			logger.info("returning coupNames >>> "+invalidCoupNames);
			return invalidCoupNames;





		}catch(Exception e){
			logger.error("Some exception occured... returning with empty string", e);
			return "";
		}
		
	}
	
	private String getPromosOfThisKeyword(String checkThisKeyword){
		
		
		try{
			
			
			if(checkThisKeyword == null){
				logger.error("keyword passed to this method is null... returning with empty string");
				return "";
			}
			
			List<OrgSMSkeywords> keywordsList = orgSMSkeywordsDao.getUserOrgSMSKeyWords(currUser.getUserOrganization().getUserOrgId());
			
			OrgSMSkeywords reqdKeyword = null;
			
			if(keywordsList != null && keywordsList.size() > 0){
				for (OrgSMSkeywords keyword : keywordsList) {
					
					
					if(checkThisKeyword.equalsIgnoreCase(keyword.getKeyword())){
						reqdKeyword = keyword;
						break;
					}
					
					
				}
			}
			
			
			if(reqdKeyword == null){
				logger.error("required keyword not found in db... returning with empty string");
				return "";
			}
			
			
			if(reqdKeyword.getAutoResponse() == null){
				logger.error("required keyword has auto response value as null... returning empty string");
				return "";
			}
			
			
			
			
			String couponIds = getCouponIdsInvolvedInAText(reqdKeyword.getAutoResponse());
			
			if(couponIds == null || couponIds.trim().length() == 0){
				logger.error("required keyword has no coupons involved or has with invalid place holders");
				return "";
			}
			
			
			
			
			
			
			String coupNames = "";
    		
    		
				logger.debug("couponIdStr :"+couponIds);
				List<Coupons> coupList = couponsDao.findCouponsByCoupIdsAndOrgId(couponIds, currUser.getUserOrganization().getUserOrgId());
				
				if(coupList != null){
					
					
					if(coupList != null && coupList.size() >0) {
						
						for (Coupons coupons : coupList) {
							coupNames += coupNames.trim().length() >0 ? ","+coupons.getCouponName() : coupons.getCouponName();
						}
						
					}
					
				}
				
			
			logger.info("returning coupNames >>> "+coupNames);
			return coupNames;
			
			
			
			
			
		}catch(Exception e){
			logger.error("Some exception occured... returning with empty string", e);
			return "";
		}
		
	}

	
	private String getCouponIdsInvolvedInAText(String text){
		
		try{
			
			if(text == null){
				logger.error("null text passed");
				return "";
			}
			
			if(text.trim().length() == 0){
				logger.error("empty text passed");
				return "";
			}
			
			
			Set<String> phSet = new HashSet<String>();
			
			
			
			phSet = findCoupPlaceholders(text);
			
			if(phSet.size() == 0){
				logger.error("no coupons in the text");
				return "";
			}
			
			
    		String couponIdStr = "";
    		for(String ph : phSet){
    			
    			if(ph.startsWith("CC_")){
    				
    				String[] phStr = ph.split("_");
    				if(phStr.length > 2){
    					
    					
    					logger.error("Invalid Placeholder: "+ph);
    					continue;
    				}
    				
    				couponIdStr += couponIdStr.trim().isEmpty() ? phStr[1].trim() : ","+phStr[1].trim();
    				
    				
    			}
    			
    		}
    		
    		logger.info("returning couponIdStr >>>>>>>> "+couponIdStr);
    		return couponIdStr;
			
			
		}catch(Exception e){
			logger.error("Exception occured >>> ",e);
			return "";
		}
		
		
	}
    private String isAnyPromoExpiredOrPaused(Set<String> phSet){
    	
    	
    	
    	
    	
    	try{
    		
    		CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
			Long orgId = currUser.getUserOrganization().getUserOrgId();
    		
    		String couponIdStr = "";
    		for(String ph : phSet){
    			
    			if(ph.startsWith("CC_")){
    				
    				String[] phStr = ph.split("_");
    				if(phStr.length > 2){
    					
    					
    					logger.error("Invalid Placeholder: "+ph);
    					continue;
    				}
    				
    				couponIdStr += couponIdStr.trim().isEmpty() ? phStr[1].trim() : ","+phStr[1].trim();
    				Long couponId = null;
    				try{
    				couponId = Long.parseLong(phStr[1]);
    				}catch(Exception e){
    					
    					
    					logger.error("Exception while parsing to long for phStr[1] : "+phStr[1]+" in ph "+ph);
    					continue;
    				}
    				
    				if(couponId != null){
    					
    					
    					
    					List<Coupons> couponsList = couponsDao.findCouponsByCoupIdsAndOrgId(""+couponId, orgId);
    					if(couponsList == null){
    						logger.error("couponsList is null for couponId : "+couponId+" and orgId "+orgId+", invoved ph is "+ph);
    						continue;
    					}
    				}
    			}
    			
    		}
    		
    		
    		
    		
    		String inValidCoupNames = "";
    		
    		if(!couponIdStr.trim().isEmpty()){
				logger.debug("couponIdStr :"+couponIdStr);
				List<Coupons> inValidCoupList = couponsDao.isExpireOrPauseCoupList(couponIdStr, currUser.getUserOrganization().getUserOrgId());
				
				if(inValidCoupList != null){
					
					
					if(inValidCoupList != null && inValidCoupList.size() >0) {
						
						for (Coupons coupons : inValidCoupList) {
							inValidCoupNames += inValidCoupNames.trim().length() >0 ? ","+coupons.getCouponName() : coupons.getCouponName();
						}
						
					}
					
				}
				
			}
    		
    		logger.debug("inValidCoupNames :"+inValidCoupNames);
    		logger.debug("couponIdStr :"+couponIdStr);
    		
    		
    		return inValidCoupNames;
    	}catch(Exception e){
    		logger.error("Exception occured, returning no coupons with expired or pending status>>> ",e);
    		return "";
    	}
    	
    	
    	
    	
    }
    public void onClick$addNewCouponTBId_createNewTx$createTransTempWinId() {
    	onClick$addNewCouponTBId();
    }

	public void onClick$addNewCouponTBId() {
		
		Redirect.goTo(PageListEnum.ADMIN_VIEW_COUPONS);
		
		
	}
	
	 public void onClick$addNewBarcodeTBId_createNewTx$createTransTempWinId() {
		 onClick$addNewBarcodeTBId();
    }

	
	public void onClick$addNewBarcodeTBId(){
		
		Redirect.goTo(PageListEnum.ADMIN_VIEW_COUPONS);
	}
	
	public void onClick$addNewKeywrdTBId() {
		
		if(isOptedForSMSKeywords()) {
			
			Redirect.goTo(PageListEnum.SMS_CAMP_SETUP);
			
		}
		
		
	}
	
	public void onSelect$insertBarcodeLbId_createNewTx$createTransTempWinId(){
		if(createTransTempWinId$insertBarcodeLbId_createNewTx.getSelectedIndex() <= 0){
			return;
		}
		Coupons selCoupon = createTransTempWinId$insertBarcodeLbId_createNewTx.getSelectedItem().getValue();
		String appShortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL).trim();
		String replaceStr = appShortUrl+Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
							+Constants.CF_END_TAG;
		insertTextForAddTemplatePopup(replaceStr);
		createTransTempWinId$insertBarcodeLbId_createNewTx.setSelectedIndex(0);
	}
	
	public void onSelect$insertCouponLbId_createNewTx$createTransTempWinId() {
		if(createTransTempWinId$insertCouponLbId_createNewTx.getSelectedIndex() <= 0) {
			return ;
		}
		
		String replaceStr ="";
		logger.info("entering onselect method");
		logger.info("selected dc value is"+createTransTempWinId$insertCouponLbId_createNewTx.getSelectedItem().getLabel());
	
		try {
		if(((String) createTransTempWinId$insertCouponLbId_createNewTx.getSelectedItem().getLabel()).equalsIgnoreCase("Referralcode")) {
			
			
			ReferralProgram selCouponref = createTransTempWinId$insertCouponLbId_createNewTx.getSelectedItem().getValue();
			
			
			try {
			replaceStr = Constants.CF_START_TAG+Constants.REF_TAG+Constants.CC_TOKEN+selCouponref.getCouponId()
			+Constants.CF_END_TAG;
			
			insertTextForAddTemplatePopup(replaceStr);
			createTransTempWinId$insertCouponLbId_createNewTx.setSelectedIndex(0);
			
			
			
			
			}catch(Exception e) {
				
				MessageUtil.setMessage("There is no Referral Discountcode which belongs to active Referral Program", "top");

			}
			
			
		
		}	
				
		else {	
			
			Coupons selCoupon = createTransTempWinId$insertCouponLbId_createNewTx.getSelectedItem().getValue();

			replaceStr = Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
								+Constants.CF_END_TAG;
			
			insertTextForAddTemplatePopup(replaceStr);
			createTransTempWinId$insertCouponLbId_createNewTx.setSelectedIndex(0);
			
		}
		}catch(Exception e) {
			
			logger.info("Exception on select dc ",e);
		}
		
		
		
		
		
	}

	
	public void onSelect$insertMergetagsLbId_createNewTx$createTransTempWinId() {
		if(createTransTempWinId$insertMergetagsLbId_createNewTx.getSelectedIndex() == 0) {
			return;
		}
		insertTextForAddTemplatePopup((String)createTransTempWinId$insertMergetagsLbId_createNewTx.getSelectedItem().getValue());
		//createTransTempWinId$insertMergetagsLbId_createNewTx.setSelectedIndex(0);
	}
	
	public boolean isOptedForSMSKeywords() {
		//logger.debug("current USer ::"+currUser);
		
		if(currUser == null) currUser = GetUser.getUserObj();
	
		CountryReceivingNumbersDao countryReceivingNumbersDao =(CountryReceivingNumbersDao)SpringUtil.getBean("countryReceivingNumbersDao");
		List<CountryReceivingNumbers> recevingNumList = countryReceivingNumbersDao.findBy(currUser.getCountryType());//getReceivingNumByCountry(countryMap.get(country), typeMap.get(type));
		if(recevingNumList == null) {
			MessageUtil.setMessage("You have not opted for SMS keywords, Please contact admin.", "color:red;");
			return false;
		}
		return true;
	}
	
	
	public void  getCouponsIfAny() {
//		List<Coupons> couponsCampList = couponsDao.findCouponsByUserId(currUser.getUserId());
		List<Coupons> couponsCampList = couponsDao.findCouponsByStatus(currUser.getUserOrganization().getUserOrgId());
		referralprogramDao=(ReferralProgramDao) SpringUtil.getBean(OCConstants.REFERRAL_PROGRAM_DAO);

		ReferralProgram refprgm=referralprogramDao.findReferalprogramByUserId(currUser.getUserId());

		
		if(couponsCampList == null || couponsCampList.size() == 0) {
			
			logger.debug("got no coupons for this org");
			return;
		}
		
		Listitem item  = null;
		
		for (Coupons coupons : couponsCampList) {
			
			item = new Listitem(coupons.getCouponName(), coupons);
			item.setParent(insertCouponLbId);
			item = new Listitem(coupons.getCouponName(), coupons);
			item.setParent(createTransTempWinId$insertCouponLbId_createNewTx);
			
		}
		
		for(Coupons coupon :couponsCampList){
			
			item = new Listitem(coupon.getCouponName(), coupon);
			Listitem itemBarcode = new Listitem(coupon.getCouponName(), coupon);
			if(coupon.getEnableBarcode()){
				item.setParent(insertBarcodeLbId);
				itemBarcode.setParent(createTransTempWinId$insertBarcodeLbId_createNewTx);
			}
		}
		if(refprgm != null && refprgm.getCouponId() != null) {
			
			item = new Listitem("Referralcode", refprgm);
			item.setParent(insertCouponLbId);
			item = new Listitem("Referralcode", refprgm);
			item.setParent(createTransTempWinId$insertCouponLbId_createNewTx);
			
			}
			
		
	}//getCouponsIfAny
	public void onSelect$insertCouponLbId() {

		if(insertCouponLbId.getSelectedIndex() <= 0) {
			logger.debug("selected 0");
			return ;
		}

		if(insertCouponLbId.getSelectedItem().getLabel().equalsIgnoreCase("Referralcode")) {
		
			String replaceStr ="";
			ReferralProgram selCouponref = insertCouponLbId.getSelectedItem().getValue();
			
			
			try {
			replaceStr = Constants.CF_START_TAG+Constants.REF_TAG+Constants.CC_TOKEN+selCouponref.getCouponId()
			+Constants.CF_END_TAG;
			
			}catch(Exception e) {
				
				MessageUtil.setMessage("There is no Referral Discountcode which belongs to active Referral Program", "top");

			}
			
			insertText(replaceStr);
			
			insertCouponLbId.setSelectedIndex(0);
		
		}	
				
		else {	
			
			Coupons selCoupon = insertCouponLbId.getSelectedItem().getValue();

		
		//Coupons selCoupon = insertCouponLbId.getSelectedItem().getValue();
		String replaceStr = Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
				+Constants.CF_END_TAG;
		insertText(replaceStr);
		insertCouponLbId.setSelectedIndex(0);
		}
	}//onSelect$insertCouponLbId()
		
		
		
	public void onSelect$insertCouponLbId11() {
		
		
		if(insertCouponLbId.getSelectedIndex() <= 0) {
			
			logger.debug("selected 0");
			return ;
			
		}
		
		Coupons selCoupon = insertCouponLbId.getSelectedItem().getValue();
		
		String replaceStr = Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
							+Constants.CF_END_TAG;
		
		insertText(replaceStr);
		
		insertCouponLbId.setSelectedIndex(0);
		
	}
	
	public void onSelect$insertBarcodeLbId(){
		
		if(insertBarcodeLbId.getSelectedIndex() <= 0){
			return;
		}
		
		Coupons selCoupon = insertBarcodeLbId.getSelectedItem().getValue();
		
		String appShortUrl = PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL).trim();
		
		String replaceStr = appShortUrl+Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
							+Constants.CF_END_TAG;
		
		insertText(replaceStr);
		
		insertBarcodeLbId.setSelectedIndex(0);
	}
	
	public void  getKeywordsIfAny() {
		
		
		
		List<OrgSMSkeywords> keywordsList = orgSMSkeywordsDao.getUserOrgSMSKeyWords(currUser.getUserOrganization().getUserOrgId());
		if(keywordsList == null || keywordsList.size() == 0) {
			
			logger.debug("got no keywords for this org");
			return;
		}
		
		Listitem item  = null;
		
		for (OrgSMSkeywords keyword : keywordsList) {
			
			item = new Listitem(keyword.getKeyword(), keyword);
			item.setParent(insertKeywordLbId);
			
		}
		
	}//getCouponsIfAny
	
	public void onSelect$insertKeywordLbId() {
		
		areExpiredPromosInAKeyword = false;
		if(insertKeywordLbId.getSelectedIndex() <= 0) {
			
			logger.debug("selected 0");
			return ;
			
		}
		
		OrgSMSkeywords selKeyword = insertKeywordLbId.getSelectedItem().getValue();
		
		/*String replaceStr = Constants.CF_START_TAG+Constants.CC_TOKEN+selCoupon.getCouponId()
							+"_"+selCoupon.getCouponName()+Constants.CF_END_TAG;*/
		if(Constants.SMS_KEYWORD_EXPIRED.equalsIgnoreCase(selKeyword.getStatus())) {
			MessageUtil.setMessage("Keyword Expired, Please edit keyword to extend validity.", "red", "top");
		}
		else {
			String keyword = selKeyword.getKeyword();
			
			String shortCode = selKeyword.getShortCode();
			if(shortCode.length() == 10 ){//&& !(shortCode.startsWith(currUser.getCountryCarrier().toString()))){
				
				shortCode = currUser.getCountryCarrier().toString() + shortCode;
			}
			
			if(shortCode.length() > 10) shortCode = "+"+shortCode;
					
			
			String promos = "";
			promos = getExpiredOrPausedPromosOfThisKeyword(keyword);
			if(promos.trim().length() > 0){
				areExpiredPromosInAKeyword = true; 
			}else{ 
				areExpiredPromosInAKeyword = false;
			} 
			
			
			keyword = keyword + " to " +shortCode;
			insertText(keyword);
			
			
			
		}
		insertKeywordLbId.setSelectedIndex(0);
		
	}
	
	public  List<String> getPlaceHolderList(Set<MailingList> mlistSet) {
		
		try {
			logger.debug("-- Just Entered --");
			MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			logger.debug("Got Ml Set of size :" + mlistSet.size());
			
			POSMappingDao posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
			List<String> placeHoldersList = new ArrayList<String>(); 
			
            
			
			List<POSMapping> contactsGENList = posMappingDao.findOnlyByGenType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
			
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

			
			Map<String , String> StoreDefaultPHValues = EditorController.getDefaultStorePhValue(placeHoldersList);
			
			//Changes to add mapped UDF fields as placeholders
			//1.get all the pos mapped UDFs from the user pos settings(table:pos_mappings)
			List<POSMapping> contactsUDFList = posMappingDao.findOnlyByType(Constants.POS_MAPPING_TYPE_CONTACTS, GetUser.getUserId() );
			
			
				
				if(contactsUDFList != null) {
					
					//2.prepare merge tag and add to placeHoldersList
					//format : display lable :: |^GEN_<UDF>^|
					for (POSMapping posMapping : contactsUDFList) {
						
						String udfStr;
						if(posMapping.getDefaultPhValue()==null || posMapping.getDefaultPhValue().trim().isEmpty()) {
						
						udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
										Constants.DELIMETER_DOUBLECOLON +
										Constants.CF_START_TAG + Constants.UDF_TOKEN +
										posMapping.getCustomFieldName()  + Constants.DELIMETER_SPACE + Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE + Constants.DEFUALT_TOKEN+Constants.CF_END_TAG ;
						
						
						}
						else {
							 udfStr = Constants.UDF_TOKEN + posMapping.getDisplayLabel() +
									Constants.DELIMETER_DOUBLECOLON +
									Constants.CF_START_TAG + Constants.UDF_TOKEN +
									posMapping.getCustomFieldName()+ Constants.DELIMETER_SPACE + Constants.DELIMETER_SLASH + Constants.DELIMETER_SPACE + Constants.DEFUALT_TOKEN + posMapping.getDefaultPhValue() + Constants.CF_END_TAG ;
					
						
						}
						placeHoldersList.add(udfStr);
					}//for
					
					
					
				}//if
			//END
			
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
			
			Listitem item = null;
			for (String placeHolder : placeHoldersList) {
				
				if(placeHolder.trim().startsWith("--") || placeHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				
				if(placeHolder.startsWith("Unsubscribe Link") || placeHolder.startsWith("Web-Page Version Link") ||
						placeHolder.startsWith("Share on Twitter") || placeHolder.startsWith("Share on Facebook") ||
						placeHolder.startsWith("Forward To Friend") || placeHolder.startsWith("Subscriber Preference Link") ){
					continue;
				}
				
				String[] phTokenArr =  placeHolder.split("::"); 
				String key = phTokenArr[0];
				StringBuffer value = new StringBuffer(phTokenArr[1]);
//				logger.debug("key ::"+key+" value ::"+value);
				if(StoreDefaultPHValues.containsKey(placeHolder)) {
					
					value.insert(value.lastIndexOf("^"), StoreDefaultPHValues.get(placeHolder));
//					logger.info(" store ::"+placeHolder + " ====== value == "+value );
					placeHolder = placeHolder.replace(phTokenArr[1], value.toString());
				}
				
				
				for (POSMapping posMapping : contactsGENList) {
					
					if(!key.equalsIgnoreCase(posMapping.getCustomFieldName()) || posMapping.getCustomFieldName().startsWith("UDF")  ) continue;
					
					if(posMapping.getDefaultPhValue() == null || posMapping.getDefaultPhValue().isEmpty() ) break;
					
					value.insert(value.lastIndexOf("^"),  posMapping.getDefaultPhValue() );
					logger.debug(" value ::"+value);
				}
				
				item =  new Listitem(key,value.toString());
				item.setParent(insertMergetagsLbId);
				item = new Listitem(key,value.toString());
				item.setParent(createTransTempWinId$insertMergetagsLbId_createNewTx);
				
				
			} // for
			
			
		/*	// Populate js variable 'phArr' with the place holders for all Editors
			
			Clients.evalJavaScript("var phArr = [];");
			int jsInd=0;
			for (String placeHolder : placeHoldersList) {
				if(placeHolder.trim().startsWith("--") || placeHolder.toLowerCase().contains(("place holder"))) { //Ignore
					continue;
				}
				Clients.evalJavaScript("phArr["+ (jsInd++) +"]=\""+placeHolder+"\";");
			} // for
			*/
			logger.debug("-- Exit --");
			return placeHoldersList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	/**
	 * updates the current cursor position
	 */
	
	public void onChange$caretPosTB(){
		logger.debug("---just entered----");
	}
	
	
	public void onBlur$caretPosTB() {
		
		logger.debug("-----just entered: onBlur event----");
		
	}
	
	public void onSelect$insertMergetagsLbId() {
		
		if(insertMergetagsLbId.getSelectedIndex() == 0) {
			
			return;
		}
		
		
		insertText((String)insertMergetagsLbId.getSelectedItem().getValue());
		insertMergetagsLbId.setSelectedIndex(0);
		
	}
	
	public void onClick$insertLinkBtnId() {
		
		logger.debug("---URL need to be Short Coded---");
		
		String enteredURL = linkURLTxtBoxId.getValue();
		
		if(enteredURL.equals("") || enteredURL.equalsIgnoreCase("http://") ||
				(enteredURL.equals("Use Url Shortener".trim())) ) {
			MessageUtil.setMessage("Please provide a valid URL.", "red", "top");
			logger.error("Exception : Link URl field is empty .");
			return;
		}
		if(!(enteredURL.startsWith("http://")||enteredURL.startsWith(HTTPS_TOKEN))) { //APP-3490
			
			enteredURL = "http://"+enteredURL;
			
		}
		
		String mappingUrl = GetUser.getUserId()+"|"+System.currentTimeMillis()+"|"+enteredURL;
		String insetedUrl = PropertyUtil.getPropertyValue("ApplicationShortUrl");
		
		//List<StringBuffer> retList =  Utility.getSixDigitURLCode(mappingUrl);
		List<StringBuffer> retList =  Utility.getEightDigitURLCode(mappingUrl);
		UrlShortCodeMapping urlShortCodeMapping = null;
		if(retList != null && retList.size() > 0) {
			
			//check whether any returned  shordcode exists in DB
			
			for (StringBuffer shortCode : retList) {
				
				String urlShortCode = "U"+shortCode;
				urlShortCodeMapping = new UrlShortCodeMapping(urlShortCode, enteredURL, "", GetUser.getUserObj().getUserId());
				
				try {
					
					urlShortCodeMappingDaoForDML.saveOrUpdate(urlShortCodeMapping);
					
					insetedUrl += urlShortCode;
					break;
				}catch (DataIntegrityViolationException e) {
					// TODO: handle exception
					logger.error("given Short code is already exists in DB.....",e);
					continue;
					
				}catch (ConstraintViolationException e) {
					// TODO: handle exception
					logger.error("given Short code is already exists in DB.....",e);
					continue;
				}
				
				
				
			}//for
			
		}//if
		
		//should insert the link short code in the message content
		
		
		insertText(insetedUrl);
		linkURLTxtBoxId.setValue("Use Url Shortener");
		
	}//onClick$insertLinkBtnId$insertLinkWinId
	
	
	
	public void onClick$insertLinkBtnIdCreateNewTx$createTransTempWinId() {
		logger.debug("---URL need to be Short Coded---");
		String enteredURL = createTransTempWinId$linkURLTxtBoxId_createNewTx.getValue();
		
		if(enteredURL.equals("") || enteredURL.equalsIgnoreCase("http://") ||
				(enteredURL.equals("Use Url Shortener".trim())) ) {
			MessageUtil.setMessage("Please provide a valid URL.", "red", "top");
			logger.error("Exception : Link URl field is empty .");
			return;
		}
		if(!(enteredURL.startsWith("http://")||enteredURL.startsWith(HTTPS_TOKEN))) {
			enteredURL = "http://"+enteredURL;
		}
		String mappingUrl = GetUser.getUserId()+"|"+System.currentTimeMillis()+"|"+enteredURL;
		String insetedUrl = PropertyUtil.getPropertyValue("ApplicationShortUrl");
		
		//List<StringBuffer> retList =  Utility.getSixDigitURLCode(mappingUrl);
		List<StringBuffer> retList =  Utility.getEightDigitURLCode(mappingUrl);
		UrlShortCodeMapping urlShortCodeMapping = null;
		if(retList != null && retList.size() > 0) {
			
			//check whether any returned  shordcode exists in DB
			for (StringBuffer shortCode : retList) {
				
				String urlShortCode = "U"+shortCode;
				urlShortCodeMapping = new UrlShortCodeMapping(urlShortCode, enteredURL, "", GetUser.getUserObj().getUserId());
				
				try {
					urlShortCodeMappingDaoForDML.saveOrUpdate(urlShortCodeMapping);
					insetedUrl += urlShortCode;
					break;
				}catch (DataIntegrityViolationException e) {
					logger.error("given Short code is already exists in DB.....",e);
					continue;
					
				}catch (ConstraintViolationException e) {
					logger.error("given Short code is already exists in DB.....",e);
					continue;
				}
			}//for
			
		}//if
		
		insertTextForAddTemplatePopup(insetedUrl);
		createTransTempWinId$linkURLTxtBoxId_createNewTx.setValue("Use Url Shortener");
	}
	
	public void insertTextForAddTemplatePopup(String  value) {
		logger.info("insertText");
		String cp = createTransTempWinId$transCaretPosTB.getValue();
		if (cp == null || cp.length() == 0) {
		cp = "0";
		}
		try{
		int caretPos = Integer.parseInt(cp);
		if (caretPos != -1) {
		String currentValue = createTransTempWinId$transTempContentTxtBxId.getValue();
		int charCount = currentValue.length();
		String msg=addingHeaderFoorter(currentValue, charCount);
		String newValue = "";
		if(currentValue != null && currentValue.length() >0 ){
			newValue = currentValue.substring(0, caretPos) + value + currentValue.substring(caretPos);
		}else newValue = value;
			int ccount = value.length()+msg.length();
		
		createTransTempWinId$transTempContentTxtBxId.setValue(newValue);
		if(ccount>160) {
			int msgcount = ccount/160;
			createTransTempWinId$transTempCharCountTxtBxId.setValue(""+ccount+" / "+(msgcount+1));
		}else {
			createTransTempWinId$transTempCharCountTxtBxId.setValue(""+ccount+" / "+1);
		}
			createTransTempWinId$transTempContentTxtBxId.focus();
		}
		}catch(Exception e) {
		logger.error("Exception ::" , e);
		}
	}
	
	public void insertText(String  value){
		logger.info("insertText");
		//String value = item.getValue();
		
		String cp = caretPosTB.getValue();
		if (cp == null || cp.length() == 0) {
		cp = "0";
		}
		try{
		int caretPos = Integer.parseInt(cp);
		if (caretPos != -1) {
		String currentValue = SMSMsgTbId.getValue();
		int charCount = currentValue.length();
		String msg=addingHeaderFoorter(currentValue, charCount);
		String newValue = "";
		if(currentValue != null && currentValue.length() >0 ){
			newValue = currentValue.substring(0, caretPos) + value + currentValue.substring(caretPos);
		}else newValue = value;
		
		//SMSMsgTbId.setValue(newValue);
		
		//int ccount = SMSMsgTbId.getValue().length()+msg.length();
		int ccount = value.length()+msg.length();
		
		SMSMsgTbId.setValue(newValue);
		//logger.info("the length is====>"+charCount);
		/*if(charCount>160) {
		int msgcount = charCount/160;
		charCountTbId.setValue(""+charCount+" / "+(msgcount+1));
		
		}//if
		else {
		charCountTbId.setValue(""+charCount+" / "+1);
		}//else
*/		
		if(ccount>160) {
			int msgcount = ccount/160;
			charCountTbId.setValue(""+ccount+" / "+(msgcount+1));
			
			}//if
			else {
			charCountTbId.setValue(""+ccount+" / "+1);
			}//else
		//sessionScope.put("messageContent", SMSMsgTbId.getValue());
		//personalizationTagsWinId.setVisible(false);
		SMSMsgTbId.focus();
		
		}
		}catch(Exception e) {
		logger.error("Exception ::" , e);
		}
		}
	
	/**
	 * gives the char count of SMS
	 * @param event
	 */
	public void onChanging$SMSMsgTbId(InputEvent event){
		try{
			getCharCount( event.getValue());
			
			
		}catch (Exception e) {
			logger.debug("Exception **",e);
		}
	}//onChanging$SMSMsgTbId(-)
	
	
	
	/**
	 * takes the existing SMS Campaign object and set the values in  view
	 * @param smsCampaign
	 */
	public void editSMSCampaign(SMSCampaigns smsCampaign) {
		try{
		logger.debug("-----just entered for editting the SMS camapign------");
		Calendar cal = Calendar.getInstance();
		
		String msgContent = smsCampaign.getMessageContent();
		SMSMsgTbId.setValue(msgContent);
		
		String msgType = smsCampaign.getMessageType();
		//byte msgSizeOption = smsCampaign.getMessageSizeOption();
		
		smsCampNameTbId.setValue(smsCampaign.getSmsCampaignName());
		smsCampNameTbId.setDisabled(true);
		
		/******* set character count textbox value****/
		
		//logger.info("the message content length is=====>"+msgContent.length());
		getCharCount(msgContent);
		
		//Added for campaign category
		
		// Added for campaign categories
		if(!currUser.getSubscriptionEnable())categoryLbId.setDisabled(true);
		Long category = smsCampaign.getCategory();
		UserCampaignCategories userCmapObj = null;
		if(category != null){
			
			 userCmapObj =userCampaignCategoriesDao.findByCatId(category, currUser.getUserId());
		}
		
		String categoryName ="";
		if(userCmapObj != null){
			 categoryName = userCmapObj.getCategoryName();
			 for(Listitem item : categoryLbId.getItems()) {
				 
				 if(item.getLabel().equals(categoryName)) {
					 item.setSelected(true);
					 break;
				 }
				 
				 
			 }
			 
		}
	
		/*Long category = smsCampaign.getCategory();
		
		for(Listitem item : categoryLbId.getItems()) {
			
			if(item.getLabel().equals(category)) {
				item.setSelected(true);
				break;
			}
			
			
		}*/
		
		int trancIndex = -1, promoIndex = -1, outBoundIndex = -1, twoWayIndex = -1;	
		int index = 0;
		List<Radio> msgTypeRadBtns = msgTypeRgId.getItems();
		for(Radio msgTypeBtn : msgTypeRadBtns) {
			if(msgTypeBtn.getValue().equals(Constants.SMS_TYPE_TRANSACTIONAL)) trancIndex = index;
			if(msgTypeBtn.getValue().equals(Constants.SMS_TYPE_OUTBOUND)) outBoundIndex = index;
			if(msgTypeBtn.getValue().equals(Constants.SMS_TYPE_2_WAY)) twoWayIndex = index;
			if(msgTypeBtn.getValue().equals(Constants.SMS_TYPE_PROMOTIONAL)) promoIndex = index;
			index++;
		}
		SMSMsgTbId.setReadonly(false);
		outBoundedDivId.setVisible(true);
		tranSactinalMsgDivId.setVisible(false);
		insertKeywrdDivId.setVisible(false);
		if(msgType.equalsIgnoreCase(Constants.SMS_TYPE_OUTBOUND)) { // specifies the message type is 'outbound'
			
			//outBoundMsgTypeRId.setChecked(true);
			if(outBoundIndex !=-1) msgTypeRgId.setSelectedIndex(outBoundIndex);
			//tranSactinalMsgDivId.setVisible(false);
			//outBoundedDivId.setVisible(true);
			
		}
		else if(msgType.equalsIgnoreCase(Constants.SMS_TYPE_2_WAY)) { // specifies the message type is '2-way'
			
			//twoWayMsgTypeRId.setChecked(true);
			if(twoWayIndex !=-1) msgTypeRgId.setSelectedIndex(twoWayIndex);
			
			if(currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) autoSenIdImg.setVisible(true);
			insertKeywrdDivId.setVisible(true);
			
			//tranSactinalMsgDivId.setVisible(false);
			
		}else if(msgType.equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL) ) {
			// two
			//transactionalMsgTypeRId.setChecked(true);
			if(trancIndex !=-1) msgTypeRgId.setSelectedIndex(trancIndex);
			tranSactinalMsgDivId.setVisible(true);
			outBoundedDivId.setVisible(false);
			SMSMsgTbId.setReadonly(true);
			sendTypeRgId.setVisible(false);
			optInTextDispLblId.setVisible(false);
			sendCampLblDivId.setVisible(false);
			if(transTempCmbBxId.getSelectedIndex() == 0) {
				editNewTempTBId.setVisible(false);
			}
			
		}else if(msgType.equalsIgnoreCase(Constants.SMS_TYPE_PROMOTIONAL) ) {
			// two
			//transactionalMsgTypeRId.setChecked(true);
			if(promoIndex !=-1) msgTypeRgId.setSelectedIndex(promoIndex);
			if(currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) autoSenIdImg.setVisible(true);
			//tranSactinalMsgDivId.setVisible(true);
			//outBoundedDivId.setVisible(false);
			//SMSMsgTbId.setReadonly(true);
			
			if(displayPRTemplateApproval && currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {
				//added for equence.
				approveTemplateDivId.setVisible(true);
			}
		}
		
		//onCheck$msgTypeRgId();
		
		try{
			
			
			setRadioButtonAttributesForMultipleSenderIds(msgType);
			
			
			
			
		}catch(Exception ex){
			logger.error("Exception >>> ",ex);
		}
		
		
		
		senderIdTbId.setAttribute(SENDERID_ATTRIBUTE, msgTypeRgId.getSelectedItem().
				getAttribute(SENDERID_ATTRIBUTE).toString());
		/****** to set the selection of sender id *****/
		senderIdTbId.setValue(msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE_REQUIRED).
				toString().equals("N") ? Constants.TWO_WAY_PROMO_TEXT_ID : msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE).
						toString());
		
		boolean sendingType = smsCampaign.isEnableEntireList();
		if(sendingType) {
			
			sendTypeRgId.setSelectedItem(entireListRId);
			
		}
		else{
			
			sendTypeRgId.setSelectedItem(optInRId);
		}
		onCheck$sendTypeRgId();
		
		
		
		logger.debug("----SMS_CAMP_DRAFT_STATUS_STEP_TWO----");
		configureDivDefaultChanges(step2DivId);
	
		/****get the existing schedules(if any) ********/
		smsCampScheduleList = smsCampaignScheduleDao.getBySmsCampaignId(smsCampaign.getSmsCampaignId());
		//logger.debug("schedule list ::"+smsCampScheduleList.size());

		if(smsCampScheduleList.size()>0) {
			//show the rows in the grid according to the existing schedules
			loadSchedule();
			/**
			 * Process Active & Archived Schedules
			 */
			//Active
			List<SMSCampaignSchedule> activeCampSchedList = getActiveCampScheduleList(smsCampScheduleList);
			if(activeCampSchedList != null && activeCampSchedList.size() > 0){
				createRowUpComingListBox(activeCampSchedList.get(0), 0, true);
				createDivUpComingCampaigns(true, activeCampSchedList.get(0));
			}
			else{
				createDivUpComingCampaigns(false, null);
			}
			
			//Archived
			List<SMSCampaignSchedule> archiveCampSchedList = getArchivedCampScheduleList(smsCampScheduleList);
			
			if(archiveCampSchedList != null &&  archiveCampSchedList.size() > 0){
				drawArchivedDiv(archiveCampSchedList.get(0));
				drawSentListBox(archiveCampSchedList.get(archiveCampSchedList.size()-1));
			}
			else{
				campaignSentDivId.setVisible(false);
			}
		}
		
		
		
		if(draftStatus != null && draftStatus.equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO)) {
			
			Clients.evalJavaScript("changeStep(2, true);");
		}
		
		if(smsCampaign.getDraftStatus().equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_THREE) ||
				smsCampaign.getDraftStatus().equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_COMPLETE) ){
			
			logger.debug("----SMS_CAMP_DRAFT_STATUS_STEP_THREE----");
			
			
			senderIdLblId.setValue((msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE_REQUIRED).
					toString().equals("N") && !(sendTypeRgId.getSelectedItem().getId().equalsIgnoreCase("optInRId")) )? Constants.TWO_WAY_PROMO_TEXT_ID : msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE).
							toString());
			
			if(sendTypeRgId.getSelectedItem().getId().equalsIgnoreCase("optInRId"))
				senderIdLblId.setValue(senderIdTbId.getAttribute(SENDERID_ATTRIBUTE).toString());
			
			//senderIdLblId.setValue(smsCampaign.getSenderId());
			
			campNameLblId.setValue(smsCampaign.getSmsCampaignName());
			
			// Added for campaign categories..
			if(currUser.getSubscriptionEnable()){
				if( smsCampaign.getCategory() != null ){
					
					Long catId= smsCampaign.getCategory();
					UserCampaignCategories userCmapaignObj =userCampaignCategoriesDao.findByCatId(catId, currUser.getUserId());
					if(userCmapObj != null){
						
						String categoryNameStr =userCmapaignObj.getCategoryName();
						categoryNameLblId.setValue(categoryNameStr);
						
					}
				}	
				
			}
			
			if(smsCampaign.getMessageType() != null ) {
				String messageType = "";
				
				if(smsCampaign.getMessageType().equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)) {
					messageType = Constants.SMS_TYPE_NAME_TRANSACTIONAL;
				}else if(smsCampaign.getMessageType().equalsIgnoreCase(Constants.SMS_TYPE_2_WAY)) {
					messageType = Constants.SMS_TYPE_NAME_2_WAY;
				}else if(smsCampaign.getMessageType().equalsIgnoreCase(Constants.SMS_TYPE_OUTBOUND)) {
					messageType = Constants.SMS_TYPE_NAME_OUTBOUND;
				}
				else if(smsCampaign.getMessageType().equalsIgnoreCase(Constants.SMS_TYPE_PROMOTIONAL)) {
					messageType = Constants.SMS_TYPE_NAME_PROMOTIONAL;
				}
					
				campTypeLblId.setValue(messageType);
				
			}
			
			setMlistLinks();
			
			step2AId.setSclass("req_step_completed");
			Clients.evalJavaScript("changeStep(3, true);");
			//onClick$gotoStep3BtnId();
		}
		
		//set the modified date to SMS campaign
		smsCampaign.setModifiedDate(cal);
		
		
		
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		
	}//editSMSCampaign
	
	
	/**
	 * loads the existing schedules for the selected SMS campaign to view call only when we are 
	 * editing the existing SMS Campaign
	 */
	
	public void loadSchedule() {
		try{
			
			logger.debug("----just entered----");
			Long id = null;
			if(smsCampScheduleList == null){
				return;
			}
			SMSCampaignSchedule tempSchedule = null;
			logger.debug("No of the schedules loaded from DB are :"+smsCampScheduleList.size());
			
			/****** creates the row for each schedule *******/
			for( SMSCampaignSchedule smsCampaignSchedule : smsCampScheduleList){
				logger.debug("first time");
				tempList.add(smsCampaignSchedule.getScheduledDate());
				/****** create rows(children if any) according to the existing schedules********/
				createRow(smsCampaignSchedule,null);
				
			}//for each
			
			Row tempRow;
			
			for( SMSCampaignSchedule smsCampaignSchedule: smsCampScheduleList) {
				
				tempRow = rowMap.get(smsCampaignSchedule.getScheduledDate());
				
				logger.debug("tempRow is====>"+tempRow);

				if(smsCampaignSchedule.getParentId() == null) {
					

					tempRow.setParent(schedGrdRowsId);
				}
				else {
					id= smsCampaignSchedule.getParentId();
					tempSchedule = smsCampaignScheduleDao.findById(id);
					logger.debug("parent for this SMS campaign schedules is=>"+smsCampaignSchedule.getParentId());
					tempRow.setParent(rowsMap.get(tempSchedule.getScheduledDate()));
					
				}//else
				
			} // for each

			
			
		}catch ( Exception e) {
			
			logger.error("** Exception while creating a row ",e);
		}
		
	}// loadSchedule()
	
	
	
	
	Label warnLblId;
	/**
	 * caluculates the actual character count of 
	 * the SMS campaign and sets this value to the SMS msg related  textbox
	 * @param msgContent specifies the actual msg content.
	 */
	public void getCharCount(String msgContent) {
		try {
				int charCount = msgContent.length();
				//logger.info("the length is====>"+charCount);
				/*if(smsSettings != null) {
					
					charCount = charCount + smsSettings.getMessageHeader().length();
					logger.debug("header of length " + smsSettings.getMessageHeader().length());
				}*/
				if(SMSStatusCodes.optOutFooterMap.get(currUser.getCountryType()) && smsSettings != null) {
					
					SMSSettings optinSettings = null;
			  		SMSSettings optOutSettings = null;
			  		SMSSettings helpSettings = null;
			  		
			  		String messageHeader = Constants.STRING_NILL;
			  		for (SMSSettings eachSMSSetting : smsSettings) {
			  			
			  			if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
			  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
			  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
			  			
			  		}
					if(optinSettings != null && messageHeader.isEmpty()){
						messageHeader = optinSettings.getMessageHeader();
						msgContent = messageHeader + "\n" + msgContent;
					}
					else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
					else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
					
					//charCount = charCount + (messageHeader != null ? messageHeader.length()+1 : 0);//TODO set perfection
					
					//headerLblId.setValue(messageHeader);
					//String optoutKeyWord = smsSettings.getOptoutKeyword();
					//if(entireListRId.isChecked() && optOutSettings != null)
					if(!msgTypeRgId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)
						&& entireListRId.isChecked() && optOutSettings != null){
						//charCount = charCount + 1+(optOutLblId.getValue().length());//("Reply "+optOutSettings.getKeyword()+" 2 Optout" );
						if(!currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {
						msgContent = msgContent+ (optOutSettings.getKeyword() != null ?
								("\n"+"Reply " + optOutSettings.getKeyword() + " 2 Optout")
										: "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
						}else {
							msgContent = msgContent+ (optOutSettings.getKeyword() != null ?
									("\n"+optOutSettings.getKeyword())
											: "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
							
						}

					}
					charCount = msgContent.length();
					logger.info("charCount-----***"+charCount);
					
					//if(smsSettings==null && entireListRId.isChecked()) 
					if(smsSettings==null &&!(msgTypeRgId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL))
						&& entireListRId.isChecked() && optOutSettings != null){
						msgContent = msgContent+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent");

					}
				}
				
				charCount = msgContent.length();
				
				if(charCount>160) {
					//warnLblId.setVisible(true);
					int msgcount = charCount/160;
					charCountTbId.setValue(""+charCount+"/"+(msgcount+1));
					/*charCountTbId.setValue(""+(smsCampaign.getMessageContent().
							substring(msgcount*160, charCount)).length()+"/"+(msgcount+1));*/
				}//if
				else {
					//warnLblId.setVisible(false);
					charCountTbId.setValue(""+charCount+" / "+1);
				}//else
			
			
		} catch (Exception e) {
				logger.debug("Exception while getting the character count",e);
		}//catch
		
	}//getCharCount

	
	public List<UserSMSSenderId>  getsenderIds() {
		String senderIds = Constants.STRING_NILL;
		List<UserSMSSenderId> retSenderIds =  null;
		try{
			logger.info("the logged user id is====>"+userId);
			//Wrong code
			/*List<UserSMSSenderId> retSenderIds = userSMSSenderIdDao.findSenderIdBySMSType(this.userId, 
					SMSStatusCodes.countryCampValueMap.get(currUser.getCountryType()).get(msgTypeRgId.getSelectedItem().getValue().toString()));
			if(retSenderIds != null && retSenderIds.size() > 0) return retSenderIds.get(0).getSenderId();
			*/
			
			retSenderIds = userSMSSenderIdDao.findByUserId(userId);
			
			
		}catch (Exception e) {

		logger.error("Exception ::" , e);
		}
		
		return retSenderIds;
		
	}
	

	Div step1DivId,step2DivId,step3DivId;
	
	
	public List<MailingList> getMailingLists() {
		List<MailingList> mlLists = null;
		try {
			//mlLists = mailingListDao.findAllByUser(userIdsSet);
			mlLists = mailingListDao.findByIds(listIdsSet); 
		}catch ( Exception e) {
			logger.error("Exception ::" , e);
		}
		return mlLists;
	}//getMailingLists()
	
	private Div buttonsDivId;
	public void onClick$proceedBtnId() {
		
		buttonsDivId.setVisible(true);
		onClick$goToNextBtnId();
		//buttonsDivId.setVisible(true); 
		step1DivId.setVisible(false);
		step2DivId.setVisible(true);
		step1AId.setSclass("req_step_completed");
		step2AId.setSclass("req_step_current");
	}
	
	private Button goToNextBtnId, backButtonId;
	
	public void onClick$saveAsDraftStep1BtnId() {
		if(smsCampaign!=null && smsCampaign.getTemplateRegisteredId()!=null && (templateIdTxtBoxId.getText()==null || templateIdTxtBoxId.getText().isEmpty())) {
			MessageUtil.setMessage("Template Registered ID cannot be made empty.", "color:red");
				return;
			}
		/*
		 * if(displayPRTemplateApproval &&
		 * currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {
		 * if(smsCampaign.getMessageType().equalsIgnoreCase(Constants.
		 * SMS_TYPE_PROMOTIONAL)) { if(smsCampaign.getTemplateRegisteredId()==null &&
		 * approveTemplateDivId.isVisible() && (templateIdTxtBoxId.getText()==null ||
		 * templateIdTxtBoxId.getText().isEmpty())){ MessageUtil.
		 * setMessage("Promotional message content needs to be approved. Please contact OC Support."
		 * , "color:red"); return; } } }else {
		 * if(smsCampaign.getTemplateRegisteredId()==null &&
		 * msgType.equalsIgnoreCase(Constants.SMS_TYPE_PROMOTIONAL)) { MessageUtil.
		 * setMessage("Promotional message content needs to be approved. Please contact OC Support."
		 * , "color:red"); return; } }
		 */
		
		if (!validateCurrDiv(step1DivId, true))
					return;
		
	}
	
	public void onClick$gotoStep2BtnId() {
		
			
		if(smsCampaign!=null && smsCampaign.getTemplateRegisteredId()!=null && (templateIdTxtBoxId.getText()==null || templateIdTxtBoxId.getText().isEmpty())) {
			MessageUtil.setMessage("Template Registered ID cannot be made empty.", "color:red");
			return;
		}
			//String specialCharactersString = "!@#$%&";
					//"!@#$%&*()'+,-./:;<=>?[]^_`{|}";
		String msg=SMSMsgTbId.getValue();
		logger.info("asd value"+msg);
		if(!Utility.validateName(msg))
		{
			if( Messagebox.show("There are some special characters in this message. Do you want to continue?", "Continue?",
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
			
				return;
			}	
			//MessageUtil.setMessage("Special characters are there in your message content", "top");
		}
		
		if(! validateCurrDiv(step1DivId, false)) {
			return;
		}
		populateListOfIncludedPromoCodesLabel();
		
		//configureDivDefaultChanges(step1DivId);
		
	/*	if( SMSStatusCodes.optInMap.get(currUser.getCountryType()) && sendTypeRgId.isVisible() && optInRId.isChecked())
			Messagebox.show("Promotional messages will not be sent to customers in NDNC list unless they have not Opted-IN.", 
					"Send to Opt-in contacts", Messagebox.OK, Messagebox.INFORMATION); 
			*/
		
	}

	
	public void onClick$gotoStep3BtnId() {
		
		if(! validateCurrDiv(step2DivId, false)) Redirect.goTo(PageListEnum.CAMPAIGN_SMSCAMPAIGN_LIST); //return;
		populateListOfIncludedPromoCodesLabel();
		
	}
	
	
	public void onClick$goToNextBtnId() {
		
		try {
			
			if(currUser!=null && currUser.getSubscriptionEnable() && smsCampaign.getCategory() ==null
					&& !smsCampaign.getMessageType().equals(Constants.SMS_TYPE_TRANSACTIONAL)){
				MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
						"\n please go to the 1st step and select a campaign category first and try again.", "color:red");
				return;
			}
  if( smsCampaign.getMessageType().equals(Constants.SMS_TYPE_PROMOTIONAL) && currUser.getCountryType().equals(Constants.SMS_COUNTRY_INDIA)) {
				
				if(smsCampaign.getTemplateRegisteredId()==null || ( displayPRTemplateApproval && smsCampaign.getTemplateRegisteredId()==null && approveTemplateDivId.isVisible() && (templateIdTxtBoxId.getText()==null ||
						templateIdTxtBoxId.getText().isEmpty()))){
					MessageUtil.setMessage("Promotional message content needs to be approved. Please contact OC Support.", "color:red");
					return;
				}
				
			}/*else {
				if(smsCampaign.getTemplateRegisteredId()==null && smsCampaign.getMessageType().equals(Constants.SMS_TYPE_PROMOTIONAL)) {
					MessageUtil.setMessage("Promotional message content needs to be approved. Please contact OC Support.", "color:red");
					return;
				}
			}*/
			// for validating atleast on contact is present or not
			if(!otherCampSettings()) return ;
			
			
			if(schedGrdRowsId.getChildren().size()>0){ // if added active schedules are there
				/*smsCampaign.setStatus(Constants.CAMP_STATUS_ACTIVE);
				smsCampaignsDao.saveOrUpdate(smsCampaign);*/
				
				if(invalidPromoCodes()) return;
				if(!areValidPromosIncludedInKeywordsForThisCampaign()) return;
				
				//if(arePendingKeywordsForThisCampaign()) return;
				
				saveSmsCampSchedule(schedGrdRowsId); // saves the SMSCampaignSchedules according to the resend level(if any)
				
				if(activeCount > 0){
					if(Constants.SMS_TYPE_2_WAY.equalsIgnoreCase(smsCampaign.getMessageType())) {
						if(IsUsingExpKeyword(smsCampaign.getMessageContent(),null)) {
							
							return;
						}
						
					}
					
					//if(invalidPromoCodes()) return;
					// exp promos check
					
					
					if(pastDate){
						pastDate = false;
						return;
					}
					MessageUtil.setMessage("SMS scheduled successfully.", "color:green;");
				}
				
				
				//TODO need to do same as campfinal
				
				else if(activeCount == 0) {
					
					try {
						if( Messagebox.show("There are no active schedules. " +
								"Do you want to continue?", "Confirm", 
								Messagebox.OK|Messagebox.CANCEL, Messagebox.QUESTION)
								== Messagebox.CANCEL) {
							return;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::" , e);
					}
				}
				setSmsCampStatus(); // set the status to SMSCampaign based on the SMSCampaignSchedule(s) status
	
				//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_SMS_CAMPAIGNS);
				Redirect.goTo(PageListEnum.CAMPAIGN_SMSCAMPAIGN_LIST);
				
			}
			else{
				
				MessageUtil.setMessage("No active SMS campaign schedules are present." +
						"Please add at least one schedule.", "color:red", "top");
				return ;
			}//else
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}

	}
	
	/**
	 * navigates through grid 
	 * takes the SMS campaign Schedule 
	 * saves into DB based on resend level 
	 * @param rows
	 */
	
	public void saveSmsCampSchedule(Rows rows) {
		
		try{
			logger.debug("----just entered-----");
			if(rows != null){
				/******** for resend level 0 ******/
				Grid grid = null;
				List<Component> childRowList = null;
				List<Component> rowList = new ArrayList<Component>();
				List<SMSCampaignSchedule> passList = new ArrayList<SMSCampaignSchedule>();
				List<SMSCampaignSchedule> activeList = new ArrayList<SMSCampaignSchedule>();
				
				rowList = rows.getChildren();
				SMSCampaignSchedule smsCampaignSchedule;
				
				
				UserCampaignExpiration triggeredEmail = getTriggeredAlertEmail();
				Calendar expiredOn = null;
				if(triggeredEmail != null){

					expiredOn = triggeredEmail.getSentOn();
					if(expiredOn != null)
					expiredOn.add(Calendar.DAY_OF_YEAR, 7);

				}

				int expiredSchedules = 0 ;
				byte expiredCount = 0;
				List<String> expriedScheduleDates = new ArrayList<String>();
				/*****save the SMSCampaignSchedules whose resend level is 0 *****/
				for(Component eachComp : rowList) {
					Row row =(Row)eachComp;
					if(row.isVisible()){
						smsCampaignSchedule = (SMSCampaignSchedule)row.getValue();
						logger.info("Id .........."+smsCampaignSchedule.getSmsCsId()+":::"+smsCampaignSchedule.getStatusStr());
						if(smsCampaignSchedule.getStatus() == 0){
							activeCount++;
							
							
						}else if(smsCampaignSchedule.getStatus() == 1){
							logger.info("Sent .........."+smsCampaignSchedule.getSmsCsId()+":::"+smsCampaignSchedule.getStatusStr());
							sentCount++;
						}
						if(smsCampaignSchedule.getResendLevel()==0) {
							passList.add(smsCampaignSchedule);
							
						}//if
						
					}//if
				}//for
				
				
				boolean isDraft =false,draftStatus=false;//Calendar ;//expiredOn = null;
				Calendar currentCal = Calendar.getInstance();
				for(SMSCampaignSchedule campSchedule : passList){


					//if campSchedule status is sent no need to do anything
					if(campSchedule.getStatus() == 1) {
						sentCount++;
						logger.info("Campaign Status is Sent No Need to do Anything.");
						continue;
					}

					//if draft and campSchedule status is active changing it to draft
					if(draftStatus && (campSchedule.getStatus() == 0 || campSchedule.getStatus() == 2 )) {
						campSchedule.setStatus((byte)2);
						if(expiredOn != null && campSchedule.equals(expiredOn)){

							//find the schedule which is last but one in the periodical schedules
							triggeredEmail.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_DRAFT);
							triggeredEmail.setModifiedDate(Calendar.getInstance());
							userCampaignExpirationDaoForDML.saveOrUpdate(triggeredEmail);

						}
						continue;
					}

					//if submit and campSchedule status is draft changing it to active
					if(!draftStatus && (campSchedule.getStatus() == 0 || campSchedule.getStatus() == 2 )) {
						//campSchedule.setStatus((byte)0);
						//activeCount++;
						//TODO if status is draft schedule date is before current date make status as expired.

						if( campSchedule.getStatus() == 2 && campSchedule.getScheduledDate().before(currentCal)){
							logger.info("Setting Status as Expired");
							isDraft = true;
							campSchedule.setStatus((byte)7);
							expiredSchedules ++;
							//expriedScheduleDates.add(MyCalendar.calendarToString(campSchedule.getScheduledDate(), MyCalendar.FORMAT_DATEONLY));
							expriedScheduleDates.add(MyCalendar.calendarToString(campSchedule.getScheduledDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
							expiredCount++;
						}
						else{
							logger.info("Setting Status as Active");
							campSchedule.setStatus((byte)0);
							activeCount++;
							activeList.add(campSchedule);
						}

						if(campSchedule.getStatus() == 2 && expiredOn != null && campSchedule.equals(expiredOn)){

							//find the schedule which is last but one in the periodical schedules
							Calendar now = Calendar.getInstance();
							int onOrAfter = now.compareTo(campSchedule.getScheduledDate()) ;
							String status = onOrAfter <= 0 ? OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_ACTIVE : 
								OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_EXPIRED;
							triggeredEmail.setStatus(status);
							triggeredEmail.setModifiedDate(Calendar.getInstance());
							userCampaignExpirationDaoForDML.saveOrUpdate(triggeredEmail);

						}
					}
					boolean flag = ((campSchedule.getStatus() == 0) && campSchedule.getScheduledDate().before(currentCal)); 

					// checks whether the scheduled date is less than current date.
					if(flag  || (!flag && isDraft)){
						if(campSchedule.getStatus() == 0 &&
								campSchedule.getScheduledDate().before(currentCal)) {
							logger.info("campSchedule.getScheduledDate() :::::;"+MyCalendar.calendarToString(campSchedule.getScheduledDate(), MyCalendar.FORMAT_SCHEDULE_TIME));
							logger.info("currentCalcurrentCal :::"+MyCalendar.calendarToString(currentCal, MyCalendar.FORMAT_SCHEDULE_TIME));
							schErrorLblId.setValue("Schedule dates can not be past dates");
							/*MessageUtil.setMessage("Schedule dates cannot be past dates.", 
								"color:red", "TOP");*/
							pastDate = true;
							return;
						}
					}
				
					
				}
				
				/*if(expiredSchedules > 0 && expriedScheduleDates.size() > 0){
					MessageUtil.setMessage(activeCount+" schedule(s) between "+ MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" "
							+ "to "+ MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+""
									+ " have been activated\n while "+expiredSchedules+" "
											+ "unsent, past schedule(s) between "+expriedScheduleDates.get(0)
											+" & "+expriedScheduleDates.get(expriedScheduleDates.size()-1)+" have expired.",  "color:blue");
				}*/
				
				if(expiredSchedules > 0 && expriedScheduleDates.size() > 0){
					MessageUtil.setMessage(getRequiredMessage(expriedScheduleDates,activeList,expiredSchedules),  "color:blue");
					//MessageUtil.setMessage(getRequiredMessage(expriedScheduleDates, activeList, expiredSchedules, activeCount),  "color:blue");
				}
				
				
				smsCampaignScheduleDaoForDML.saveByCollection(passList);

			}//if
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::" , e);
		}//catch
	}//saveSmsCampSchedule(-) 
	
	private String getRequiredMessage(List<String> expriedScheduleDates, List<SMSCampaignSchedule> passList, int expiredSchedules){
		
		StringBuffer reqdMSgStrBfr = new StringBuffer();
		
/*		if(expiredSchedules == 1 && activeCount == 1){
			
			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" has been activated\nwhile" +
					" 1 unsent, past schedule on "+ expriedScheduleDates.get(0) +" has expired ");
			
		}else if(expiredSchedules == 1 && activeCount > 1){
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY) +" have been activated\nwhile"+
					" 1 unsent, past schedule on "+ expriedScheduleDates.get(0) +" has expired ");
			
		}else if(expiredSchedules > 1 && activeCount == 1){
			
			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" has been activated\nwhile " +
					expiredSchedules+" unsent, past schedules between "+ expriedScheduleDates.get(0) +" & " +expriedScheduleDates.get(expriedScheduleDates.size()-1)+" has expired ");
			
		}else if(expiredSchedules > 1 && activeCount > 1){
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY) +" have been activated\nwhile " +
					expiredSchedules+" unsent, past schedules between "+ expriedScheduleDates.get(0) +" & " +expriedScheduleDates.get(expriedScheduleDates.size()-1)+" has expired ");
			
		}else if(expiredSchedules == 0 && activeCount == 1){
			
			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" has been activated");
			
		}else if(expiredSchedules == 0 && activeCount > 1){
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY) +" have been activated ");
			
		}else if(expiredSchedules == 1 && activeCount == 0){
			
			reqdMSgStrBfr.append("1 unsent, past schedule on "+ expriedScheduleDates.get(0) +" has expired ");
			
		}else if(expiredSchedules > 1 && activeCount == 0){
			
			reqdMSgStrBfr.append(expiredSchedules+" unsent, past schedules between "+ expriedScheduleDates.get(0) +" & " +expriedScheduleDates.get(expriedScheduleDates.size()-1)+" has expired ");
		}
*/
			if(expiredSchedules == 1 && activeCount == 1){
			
			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" has been activated\nwhile" +
					" 1 unsent, past schedule on "+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" has expired ");
			
		}else if(expiredSchedules == 1 && activeCount > 1){
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" to "
			+MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" have been activated\nwhile"+
					" 1 unsent, past schedule on "+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" has expired ");
			
		}else if(expiredSchedules > 1 && activeCount == 1){
			
			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+
			" has been activated\nwhile "+expiredSchedules+" unsent, past schedules between "+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" & "
			+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(expriedScheduleDates.size()-1)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))  +" has expired ");
			
		}else if(expiredSchedules > 1 && activeCount > 1){
			/*logger.info("expiredSchedules and expriedScheduleDates "+expiredSchedules,expriedScheduleDates);
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY)+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY) +" have been activated\nwhile " +
					expiredSchedules+" unsent, past schedules between "+ expriedScheduleDates.get(0) +" & " +expriedScheduleDates.get(expriedScheduleDates.size()-1)+" has expired ");*/
			
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" have been activated\nwhile " +
					expiredSchedules+" unsent, past schedules between "+ MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" & "
			+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(expriedScheduleDates.size()-1)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+"has expired");
			
		}else if(expiredSchedules == 0 && activeCount == 1){
			
			reqdMSgStrBfr.append("1 schedule on "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" has been activated");
			
		}else if(expiredSchedules == 0 && activeCount > 1){
			
			reqdMSgStrBfr.append(activeCount+" schedules between "+MyCalendar.calendarToString(passList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" to "+
					MyCalendar.calendarToString(passList.get(passList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" have been activated ");
			
		}else if(expiredSchedules == 1 && activeCount == 0){
			
			reqdMSgStrBfr.append("1 unsent, past schedule on "+  MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" has expired ");
			
		}else if(expiredSchedules > 1 && activeCount == 0){
			
			reqdMSgStrBfr.append(expiredSchedules+" unsent, past schedules between "+ MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(0)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")) +" & "
			+MyCalendar.calendarToString(MyCalendar.string2Calendar(expriedScheduleDates.get(expriedScheduleDates.size()-1)), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))+" has expired ");
		}

		logger.info("reqdMSg >>> "+reqdMSgStrBfr);
		
		return reqdMSgStrBfr.toString();
	}
	private UserCampaignExpiration getTriggeredAlertEmail() {

		if(userCampaignExpirationDao == null) {

			try {
				userCampaignExpirationDao = (UserCampaignExpirationDao)ServiceLocator.
						getInstance().getDAOByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO);
				userCampaignExpirationDaoForDML = (UserCampaignExpirationDaoForDML)ServiceLocator.
						getInstance().getDAOForDMLByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO_FOR_DML);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}
		}

		List<UserCampaignExpiration> existingTriggerList = userCampaignExpirationDao.
				findBy(smsCampaign.getUsers().getUserId(), smsCampaign.getSmsCampaignId());

		UserCampaignExpiration userCampaignExpiration = null;

		if(existingTriggerList != null && existingTriggerList.size() > 0){

			userCampaignExpiration = existingTriggerList.get(0);

		}

		return userCampaignExpiration;
	}

	// used to set the actual status for SMSCampaign
	int activeCount; 
	int sentCount;
	public void setSmsCampStatus() {
		
		try{
			 if(activeCount >0 && sentCount > 0){
					smsCampaign.setStatus(Constants.CAMP_STATUS_RUNNING);
				}
				else if(activeCount == 0 && sentCount > 0) {
					smsCampaign.setStatus(Constants.CAMP_STATUS_SENT);
				}
				else if(activeCount >0 && sentCount == 0){
					smsCampaign.setStatus(Constants.CAMP_STATUS_ACTIVE);
				}else{
					smsCampaign.setStatus(Constants.CAMP_STATUS_DRAFT);
				}
			 if(session!=null || currUser != null){
				 //smsCampaignsDao.saveOrUpdate(smsCampaign);
				 smsCampaignsDaoForDML.saveOrUpdate(smsCampaign);
			 }
		}catch (Exception e) {
			logger.error("**Exception while setting the status to the SMS campaign",e);
		}
	}//setSmsCampStatus
	
	
	private Radiogroup configurelistRgId;
	private Div mlDivId, segDivId;
	
	public void onCheck$configurelistRgId() {
			
					if(configurelistRgId.getSelectedIndex() == 0){
				mlDivId.setVisible(true);
				segDivId.setVisible(false);
				
			}else if(configurelistRgId.getSelectedIndex() == 1) {
				
				mlDivId.setVisible(false);
				segDivId.setVisible(true);
				
				
			}
			
			
			
		}
	
	
		
	
	public void onCheck$msgTypeRgId() {
		if(displayPRTemplateApproval && currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)
				&& msgTypeRgId.getSelectedItem().getValue().equals(Constants.SMS_TYPE_PROMOTIONAL)) {
			//added for equence.
			approveTemplateDivId.setVisible(true);
		}else {
			approveTemplateDivId.setVisible(false);
		}
		logger.info("msgTypeRgIdmsgTypeRgId");
		Radio selectedRadio = msgTypeRgId.getSelectedItem();
		String msgType = selectedRadio.getValue();
		
		try{
			
			
				populateMultipleSenderIdsLbId_MakeItVisibleInCase_2_OrMoreSenderIDs();
			
			
				multipleSenderIdsLbId.setDisabled(false);
			
			
			
		}catch(Exception e){
			logger.error("Exception >>> ",e );
		}
		
		
		
		
		
		
		
		
		if(smsCampaign != null) {
			String setMsg = smsCampaign.getMessageContent();
			
			if(msgType.equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)) {
				
				SMSMsgTbId.setText(setMsg);
				int charCount = SMSMsgTbId.getValue().length();
				if(charCount>160) {
						
					int msgcount = charCount/160;
					charCountTbId.setValue(""+charCount+" / "+(msgcount+1));
					
				}//if
				else {
					charCountTbId.setValue(""+charCount+" / "+1);
				}
			}
			else {
				
				SMSMsgTbId.setText("");
				insertText("");
			}
		}
		else {
			
			SMSMsgTbId.setText("");
			insertText("");
		}
			
		
		tranSactinalMsgDivId.setVisible(msgType.equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL));
				
		if(msgType.equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL) && selectedRadio.getAttribute(SENDERID_ATTRIBUTE) == null) {
			MessageUtil.setMessage("Transactional message type settings not found.", "color:red;");
			msgTypeRgId.setSelectedIndex(0);
			onCheck$msgTypeRgId();
			return;
		}
		
		//
		
		setSenderID();
		//
		
		
		/*senderIdTbId.setText(selectedRadio.getAttribute(SENDERID_ATTRIBUTE_REQUIRED).toString().equals("N") ? 
				Constants.TWO_WAY_PROMO_TEXT_ID : selectedRadio.getAttribute(SENDERID_ATTRIBUTE).toString());
		//five
		senderIdTbId.setAttribute(SENDERID_ATTRIBUTE, selectedRadio.getAttribute(SENDERID_ATTRIBUTE).toString());
		*/
		autoSenIdImg.setVisible(selectedRadio.getAttribute(SENDERID_ATTRIBUTE_REQUIRED).toString().equals("N"));
		sendTypeRgId.setVisible(true);
		
		if(msgType.equalsIgnoreCase(Constants.SMS_TYPE_OUTBOUND)) {
			insertKeywrdDivId.setVisible(false);
			outBoundedDivId.setVisible(true);
			SMSMsgTbId.setReadonly(false);
			categoryLbId.setDisabled(!currUser.getSubscriptionEnable() || categoryLbId.getItemCount() == 1 );
			helpImgId.setVisible(categoryLbId.isDisabled());
			//senderIdTbId.setValue(getsenderIds());
			sendCampLblDivId.setVisible(true);
			//tranSactinalMsgDivId.setVisible(false);
			//autoSenIdImg.setVisible(false);
		}
		else if( msgType.equalsIgnoreCase(Constants.SMS_TYPE_2_WAY) ) {
			
			insertKeywrdDivId.setVisible(true);
			//tranSactinalMsgDivId.setVisible(false);
			outBoundedDivId.setVisible(true);
			SMSMsgTbId.setReadonly(false);
			categoryLbId.setDisabled(!currUser.getSubscriptionEnable() || categoryLbId.getItemCount() == 1);
			helpImgId.setVisible(categoryLbId.isDisabled());
			//senderIdTbId.setValue(senderId);
			//senderIdTbId.setText(Constants.TWO_WAY_PROMO_TEXT_ID);
			//if(currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) autoSenIdImg.setVisible(true);
			//sendTypeRgId.setVisible(true);
			sendCampLblDivId.setVisible(true);
	
		}else if(msgType.equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL) ){
			
				tranSactinalMsgDivId.setVisible(true);
				transTempCmbBxId.setSelectedIndex(0);
				editNewTempTBId.setVisible(false);
				outBoundedDivId.setVisible(false);
				SMSMsgTbId.setReadonly(true);
				if(smsCampaign == null || (smsCampaign != null && smsCampaign.getMessageType() != null 
						&& !smsCampaign.getMessageType().equals(Constants.SMS_TYPE_TRANSACTIONAL))){
					SMSMsgTbId.setValue("");
					charCountTbId.setValue("");
				}
				categoryLbId.setSelectedIndex(0);
				categoryLbId.setDisabled(true);
				// three
				helpImgId.setVisible(categoryLbId.isDisabled() && 
						!((String) msgTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase(Constants.SMS_TYPE_NAME_TRANSACTIONAL));
				sendTypeRgId.setVisible(false);
				sendCampLblDivId.setVisible(false);
				optInTextDispLblId.setVisible(false);
				entireListRId.setChecked(true);
				//senderIdTbId.setValue(getsenderIds());
				optOutLblId.setValue("");
				headerLblId.setValue("");
				
		}
		else if(msgType.equalsIgnoreCase(Constants.SMS_TYPE_PROMOTIONAL)) {
			insertKeywrdDivId.setVisible(false);
			outBoundedDivId.setVisible(true);
			SMSMsgTbId.setReadonly(false);
			categoryLbId.setDisabled(!currUser.getSubscriptionEnable() || categoryLbId.getItemCount() == 1 );
			helpImgId.setVisible(categoryLbId.isDisabled());
			//senderIdTbId.setText(Constants.TWO_WAY_PROMO_TEXT_ID);
			//autoSenIdImg.setVisible(true);
			//sendTypeRgId.setVisible(true);
			sendCampLblDivId.setVisible(true);
			//tranSactinalMsgDivId.setVisible(false);
		}
		
		if(sendTypeRgId.isVisible()) {
			sendTypeRgId.setSelectedIndex(0);
			onCheck$sendTypeRgId();
		}
		
		logger.info("End of SenderTB Value........"+senderIdTbId.getValue());
	}
	
	/**
	 * setSenderID
	 * based on the selected radio msgTypeRgId, its sets sender id in the sender id text box.
	 * and setting SENDERID_ATTRIBUTE with same sender id.
	 */
	private void setSenderID() {
		Radio selectedRadio = msgTypeRgId.getSelectedItem();
		String msgType = selectedRadio.getValue();
		String accountType = null;
		if(!(msgTypeRgId.getSelectedIndex() == -1)) {

			msgType = msgTypeRgId.getSelectedItem().getValue();
			accountType = SMSStatusCodes.countryCampValueMap.get(currUser.getCountryType()).get(msgType);
			if(SMSStatusCodes.optInMap.get(currUser.getCountryType()) && sendTypeRgId.isVisible()) {

				accountType = sendTypeRgId.getSelectedItem().equals(optInRId) ? 
						Constants.SMS_ACCOUNT_TYPE_OPTIN : accountType;

			}
		}

		
		
		//Added For UAE
		/*if(currUser.getCountryCarrier() == Short.parseShort(Constants.SMS_COUNTRY_CODE_UAE)){
			
			

			UserSMSGatewayDao userSMSGatewayDao = null;
			try {
				userSMSGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("Userid"+userId +"\t accountType .."+accountType);
			UserSMSGateway userSmsGateway = userSMSGatewayDao.findByUserId(userId, accountType);

			if(userSmsGateway == null) {
				logger.debug("Error while getting userSmsGateway details...");
				MessageUtil.setMessage("No SMS set up found for your account.Please contact Admin.","color:red","TOP");
				return ;
			}

			OCSMSGatewayDao ocsmsGatewayDao = null;
			try {
				ocsmsGatewayDao = (OCSMSGatewayDao) ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OCSMSGateway ocgateway = ocsmsGatewayDao.findById(userSmsGateway.getGatewayId());
			if(ocgateway == null) {
				logger.debug("Error while getting ocgateway details...");
				MessageUtil.setMessage("No SMS set up found for your account.Please contact Admin.","color:red","TOP");
				return ;
			}
			if(ocgateway.getTwoWaySenderID() == null || ocgateway.getTwoWaySenderID().isEmpty()){
				logger.debug("Error while getting ocgateway details...");
				MessageUtil.setMessage("No SMS set up found for your account.Please contact Admin.","color:red","TOP");
				return ;
			}
			
			
			if(Constants.SMS_TYPE_2_WAY.equalsIgnoreCase((msgTypeRgId.getSelectedItem().getValue()).toString())){
				
				senderIdTbId.setText(ocgateway.getTwoWaySenderID());
				senderIdTbId.setAttribute(SENDERID_ATTRIBUTE, ocgateway.getTwoWaySenderID());
			}
			else{
				senderIdTbId.setText(selectedRadio.getAttribute(SENDERID_ATTRIBUTE_REQUIRED).toString().equals("N") ? 
						Constants.TWO_WAY_PROMO_TEXT_ID : selectedRadio.getAttribute(SENDERID_ATTRIBUTE).toString());
				senderIdTbId.setAttribute(SENDERID_ATTRIBUTE, selectedRadio.getAttribute(SENDERID_ATTRIBUTE).toString());
			}
			//five
			
			
			

		}
		else{ //Except UAE
			senderIdTbId.setText(selectedRadio.getAttribute(SENDERID_ATTRIBUTE_REQUIRED).toString().equals("N") ? 
					Constants.TWO_WAY_PROMO_TEXT_ID : selectedRadio.getAttribute(SENDERID_ATTRIBUTE).toString());
			//five
			senderIdTbId.setAttribute(SENDERID_ATTRIBUTE, selectedRadio.getAttribute(SENDERID_ATTRIBUTE).toString());
		}*/

		
		
		
		
		senderIdTbId.setText(selectedRadio.getAttribute(SENDERID_ATTRIBUTE_REQUIRED).toString().equals("N") ? 
				Constants.TWO_WAY_PROMO_TEXT_ID : selectedRadio.getAttribute(SENDERID_ATTRIBUTE).toString());
		//five
		senderIdTbId.setAttribute(SENDERID_ATTRIBUTE, selectedRadio.getAttribute(SENDERID_ATTRIBUTE).toString());
	}



	public List getSegmentRules() {
		List segLists = null;
		try {
			//segLists = segmentRulesDao.findByUser(userIdsSet);
			segLists = segmentRulesDao.findByIds(segmentIdsSet); 
		}catch (Exception e) {
			logger.error("** Exception : " + e);
		}
		return segLists;
	}
	
	
	
	
	
	private Listbox dispMlListsLBoxId, dispsegmentsLbId;
	
	public void configureDivDefaultChanges(Div currDiv) {
		
		
		if(currDiv.getId().equals(step2DivId.getId())) {
			
			if(dispMlListsLBoxId.getItemCount() > 0 || dispsegmentsLbId.getItemCount() > 0) return;
			List<MailingList> mlList = getMailingLists();
			if (mlList != null && mlList.size() < 1){
				MessageUtil.setMessage("Please create a contact list.",
						"color:red", "TOP");
				return;
			}
			Listitem listItem;
			
			if(mlList != null ) {
				for (MailingList mailingList : mlList) {
					listItem = new Listitem();
					listItem.setValue(mailingList);
					listItem.setLabel(mailingList.getListName());
					listItem.setParent(dispMlListsLBoxId); 
				}
			}
			
			List<SegmentRules> segList = getSegmentRules(); 
			
			if(segList != null) {
				for (SegmentRules segRule : segList) {
					listItem = new Listitem();
					listItem.setValue(segRule);
					listItem.setLabel(segRule.getSegRuleName());
					listItem.setParent(dispsegmentsLbId); 
				}
			
			}
			if(smsCampaign != null && isEdit !=null ) {
				
				String listType = smsCampaign.getListType();
				if(listType != null) {
					if(listType.equals("Total")) {
						
						enableMailingList();
						
					}else{
						
						enableSegment();
						
					
					}
				
				}//if
			}//if
			
		}
		
	}
	
	
	
	public void enableSegment() {
		
		
		String segRule = smsCampaign.getListType();
		logger.debug("Segment Rule :"+ segRule);
		if(segRule != null && !segRule.equalsIgnoreCase("Total") && segRule.startsWith("Segment")) {
			
			String segRuleId = segRule.split(""+Constants.DELIMETER_COLON)[1];
			List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleId);
			if(segmenRules == null) {
				
				MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
				configurelistRgId.setSelectedIndex(1);
				onCheck$configurelistRgId();
				return ;
				
				
			}
			
			String segRuleIds= "";
			String listIdsStr = "";
			String dispSegRule = "";
			listnamesStr = "";
			
			for (SegmentRules segmentRules : segmenRules) {
				
				if(segmentRules == null) {
					
					MessageUtil.setMessage("One of the segments configured to this campaign no longer exists .You might have deleted it.", "color:red;");
					configurelistRgId.setSelectedIndex(1);
					onCheck$configurelistRgId();
					continue ;
					
				}
				
				for(int i=0; i<dispsegmentsLbId.getItemCount(); i++){
					
					if(dispsegmentsLbId.getItemAtIndex(i).getLabel().equals(segmentRules.getSegRuleName()) ){
						dispsegmentsLbId.addItemToSelection(dispsegmentsLbId.getItemAtIndex(i));
						
						
					}//if
					
				}//for
				
				if(dispSegRule.length() > 0) dispSegRule += "& \n";
				dispSegRule += dispRule(segmentRules.getSegRule());
				
				if(listIdsStr.length() > 0) listIdsStr += ",";
				listIdsStr += segmentRules.getSegmentMlistIdsStr();
				
				
			}//for
			
			selRuleLblId.setValue(dispSegRule);
			
			List<MailingList> mlList = mailingListDao.findByIds(listIdsStr);
			
			if(mlList == null) {
				
				logger.debug("Configured Segment's target list is no longer exist.You might have deleted it.");
				configurelistRgId.setSelectedIndex(1);
				onCheck$configurelistRgId();
				return ;
				
				
			}
			
			for (MailingList mailingList : mlList) {
				
				if(listnamesStr == null) listnamesStr = mailingList.getListName();
				
				if(listnamesStr != null && listnamesStr.length() > 0) listnamesStr += ", ";
				listnamesStr += mailingList.getListName();
				
			}//for
			
			selRuleListLblId.setValue(listnamesStr);
			
			
			if(selRuleLblId.getValue().length() != 0) {
				dispRuleDivId.setVisible(true);
			}else{
				dispRuleDivId.setVisible(false);
			}
			
			
			configurelistRgId.setSelectedIndex(1);
			onCheck$configurelistRgId();
			
		}
		
	}
	
	public void enableMailingList() {
		
				
		
		String mlListName = "";
		String listNamesStr="";
		configurelistRgId.setSelectedIndex(0);
		Vector<String> mlListNamesVector = new Vector<String>();
		//Vector<String> senderIdsVector = new Vector<String>();
		
		Set<MailingList> mailingListsSet = smsCampaign.getMailingLists();
		
		//senderIdsVector.add(smsCampaign.getSenderId());
		
		for(MailingList mailingList : mailingListsSet){
			mlListName = mailingList.getListName();
			mlListNamesVector.add(mlListName);
		}
		
		/***** to set the selection of configured mailing lists *******/
		for(int i=0; i<dispMlListsLBoxId.getItemCount(); i++){
			
			if(mlListNamesVector.contains(dispMlListsLBoxId.getItemAtIndex(i).getLabel())){
				dispMlListsLBoxId.addItemToSelection(dispMlListsLBoxId.getItemAtIndex(i));
				if(listNamesStr.length() != 0) listNamesStr+=",";
				listNamesStr += dispMlListsLBoxId.getItemAtIndex(i).getLabel() ;
				
			}
		}
		
		selMlLblId.setValue(listNamesStr);
		
		if(selMlLblId.getValue().length() != 0) {
			selectedListDivId.setVisible(true);
		}else{
			selectedListDivId.setVisible(false);
		}
		
		configurelistRgId.setSelectedIndex(0);
		onCheck$configurelistRgId();
		
	}
	
	
	public boolean sendTestSMS(String mblNumber, String sendingMsg) {
		
		try{
			MessageUtil.clearMessage();
			String mblNumArr[];
			long mblNum = 0;
			int charCount =0;
			int usedCount =0;
			
			
			//logger.debug("SMS msg length is====>"+sendingMsg.length());
			if( mblNumber == null || mblNumber.trim().equals("")) {
				MessageUtil.setMessage("Please provide mobile number to send a test SMS.", "color:red;","top");
				
				return true;
				
			}
			
			if(sendingMsg == null || !(sendingMsg.trim().length()>0) ){ // if test SMS is  empty
				
				MessageUtil.setMessage("SMS message cannot be left empty. Please provide message text.", "color:red;", "top");
				return false;
			}
			
			//String senderId = senderIdTbId.getValue().trim();
			String senderId = (String)senderIdTbId.getAttribute(SENDERID_ATTRIBUTE);
			
			if(senderId == null || senderId.length() == 0) {//if SMS sender id is not selected
				
				MessageUtil.setMessage("Sender ID is required. Please contact Admin to get appropriate sender ID.","color:red","TOP");
				return false;
			}
			
			if(msgTypeRgId.getSelectedItem() == null) {
				
				MessageUtil.setMessage("Please select the option specifying SMS message type.","color:red","TOP");
				return false; 
				
			}
			
			
			UserSMSGatewayDao userSMSGatewayDao = (UserSMSGatewayDao)SpringUtil.getBean("userSMSGatewayDao");
			OCSMSGatewayDao OCSMSGatewayDao = (OCSMSGatewayDao)SpringUtil.getBean("OCSMSGatewayDao");
			//Added for UAE
			UserOrganization userOrganization = currUser.getUserOrganization();

			if(userOrganization == null){
				MessageUtil.setMessage("User does not belong to any organization.", "color:red;","top");
				return false;
			}
			
			
			UserSMSGateway userSmsGateway = null;
			OCSMSGateway ocgateway = null;
			String userSMSDetails = Constants.STRING_NILL;
			String msgType = "";
			String accountType = null;
			int mobCnt =0;
			if(!(msgTypeRgId.getSelectedIndex() == -1)) {
				
				msgType = msgTypeRgId.getSelectedItem().getValue();
				accountType = SMSStatusCodes.countryCampValueMap.get(currUser.getCountryType()).get(msgType);
				if(SMSStatusCodes.optInMap.get(currUser.getCountryType()) && sendTypeRgId.isVisible()) {
					
					 accountType = sendTypeRgId.getSelectedItem().equals(optInRId) ? 
							 Constants.SMS_ACCOUNT_TYPE_OPTIN : accountType;
					logger.info("accountTypeaccountType"+accountType);
				}
				
				/*if(outBoundMsgTypeRId.isChecked()) {
					 msgType = "1";
					 accountType = sendTypeRgId.getSelectedItem().equals(optInRId) ? 
							 Constants.SMS_ACCOUNT_TYPE_OPTIN : Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL; 
					
				}//outbound mesage
				else if(twoWayMsgTypeRId.isChecked()) {
//					smsCampaign.setMessageType((byte)2);
					msgType = "2";
					accountType =  Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL;
				}//2-way
				else if(transactionalMsgTypeRId.isChecked()) {
//					smsCampaign.setMessageType((byte)2);
					msgType = Constants.SMS_TYPE_TRANSACTIONAL;
					accountType =  Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL;
				}//Transactinal Message Type
*/				userSmsGateway = userSMSGatewayDao.findByUserId(userId, accountType);
				if(userSmsGateway == null) {
					
					logger.debug("Error while getting userSmsGateway details...");
					MessageUtil.setMessage("No SMS set up found for your account.Please contact Admin.","color:red","TOP");
					return false;
					
				}
				
				ocgateway = OCSMSGatewayDao.findById(userSmsGateway.getGatewayId());
				if(ocgateway == null) {
					
					logger.debug("Error while getting ocgateway details...");
					MessageUtil.setMessage("No SMS set up found for your account.Please contact Admin.","color:red","TOP");
					return false;
					
				}
				
				//userSMSDetails = userSmsGateway.getAccountDetails();
			}	
			logger.debug(" my sms details are ====>"+userSMSDetails);
			
			try {
				if(mblNumber.contains(",")){ // if having multiple mobile numbers to sent test SMS
					
					mblNumArr = mblNumber.split(",");
					mblNumber = "";
					mobCnt = mblNumArr.length;
					for (String mobileNum : mblNumArr) {
						
						mobileNum = mobileNum.trim();
						
						mobileNum = Utility.phoneParse(mobileNum,userOrganization);
						
						/*if(mobileNum != null) {
							//added for UAE
							if(!mobileNum.startsWith(currUser.getCountryCarrier().toString()) &&
									( (mobileNum.length() >= userOrganization.getMinNumberOfDigits()) && (mobileNum.length() <= userOrganization.getMaxNumberOfDigits()))
									) {
								mobileNum = currUser.getCountryCarrier().toString()+mobileNum;
							}
							mblNum = Long.parseLong(mobileNum);
							if(mblNumber.length()>0)	mblNumber += ",";
							
							mblNumber +=  mblNum;
						}else{
							MessageUtil.setMessage("Please provide valid mobile number for "+mobileNum,"color:red","TOP");
							return false;
							
						}*/
						if(mobileNum != null) {
							//added for UAE
							if(userOrganization.isRequireMobileValidation()){
							if(!mobileNum.startsWith(currUser.getCountryCarrier().toString()) &&
									( (mobileNum.length() >= userOrganization.getMinNumberOfDigits()) && (mobileNum.length() <= userOrganization.getMaxNumberOfDigits()))
									) {
								mobileNum = currUser.getCountryCarrier().toString()+mobileNum;
							}
							}
							mblNum = Long.parseLong(mobileNum);
							if(mblNumber.length()>0)	mblNumber += ",";
							
							mblNumber +=  mblNum;
						}else{
							MessageUtil.setMessage("Please provide valid mobile number for "+mobileNum,"color:red","TOP");
							return false;
							
						}
					}
				}
				else {
					
					mblNumber = mblNumber.trim();
					mblNumber = Utility.phoneParse(mblNumber,userOrganization);
					/*if(mblNumber != null) {
						if(!mblNumber.startsWith(currUser.getCountryCarrier().toString()) &&
								( (mblNumber.length() >= userOrganization.getMinNumberOfDigits()) && (mblNumber.length() <= userOrganization.getMaxNumberOfDigits()))
								) {
							
							mblNumber = currUser.getCountryCarrier().toString()+mblNumber;
						}
						mblNum = Long.parseLong(mblNumber);
					}
					else{
						
						MessageUtil.setMessage("Please provide valid mobile number.","color:red","TOP");
						return true;
					
					}*/
					if(mblNumber != null) {
						if(userOrganization.isRequireMobileValidation()){
						if(!mblNumber.startsWith(currUser.getCountryCarrier().toString()) &&
								( (mblNumber.length() >= userOrganization.getMinNumberOfDigits()) && (mblNumber.length() <= userOrganization.getMaxNumberOfDigits()))
								) {
							
							mblNumber = currUser.getCountryCarrier().toString()+mblNumber;
						}
						}
						mblNum = Long.parseLong(mblNumber);
					}
					else{
						
						MessageUtil.setMessage("Please provide valid mobile number.","color:red","TOP");
						return true;
					
					}
					
					mobCnt = 1;
				}//else 
			} catch (NumberFormatException e1) {

				MessageUtil.setMessage("Please provide valid mobile number.","color:red","TOP");
				return true;
			
			}
			
			logger.debug("mobile numbers to send Test SMS are====>"+mblNumber);
			
			 try {
				int confirm = Messagebox.show("Are you sure you want to send the test SMS?","Send test SMS?",
						 		Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != Messagebox.OK){
						testSMSTbId.setValue("Enter Mobile Number(s)...");
						mblNoTxtBoxId.setValue("Enter Mobile Number(s)...");
						return false;
					}
					
					if(currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)){
					
						if(!((String) msgType).equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)  ){
							
							MessageUtil.setMessage("If mobile number is in NDNC list, test SMS won't be sent. ","color:blue;");
						}
				
					}
					
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception :::",e);
			}
			
				
			/***split,truncate,ignore  or send the SMS message as it is depend on the selected sending option ******/
			
			logger.info("get the user sms count=====>"+currUser.getSmsCount());
			if(currUser.getSmsCount()==0){
				
				MessageUtil.setMessage("SMS credits are not available. Please renew your account.", "color:red;","top");
				testSMSTbId.setValue("Enter Mobile Number(s)...");
				mblNoTxtBoxId.setValue("Enter Mobile Number(s)...");
				return false; 
				
			}
			
			
			ArrayList<String> msgContentLst = null;
			//msgContentLst = splitSMSMessage(sendingMsg, 170);//if msg type is 'English' spilt upto 160 characters
			
			//sendingMsg = StringEscapeUtils.escapeHtml(sendingMsg);
			/*captiwayToSMSApiGateway.sendToSMSApi(currUser.getUserSMSTool(), sendingMsg, 
					""+mblNumber, msgType, "9848495956", ""+mblNumber, "1", senderId);
			*/
			//adding header and footer for test msg content (harshi)
			String messageHeader = Constants.STRING_NILL;
			String msgContent="Test SMS"+"\n";
			if(SMSStatusCodes.optOutFooterMap.get(currUser.getCountryType())) {
				logger.info("after add headers"+sendingMsg);
				SMSSettingsDao smsSettingsDao = (SMSSettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
				List<SMSSettings> smsSettings = smsSettingsDao.findByUser(userId);
				if(smsSettings != null) {

					SMSSettings optinSettings = null;
					SMSSettings optOutSettings = null;
					SMSSettings helpSettings = null;
					
					for (SMSSettings eachSMSSetting : smsSettings) {

						if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
						else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
						else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;

					}
					if(optinSettings != null && messageHeader.isEmpty()) messageHeader = optinSettings.getMessageHeader();
					else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
					else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
					
					sendingMsg = messageHeader != null ? msgContent+messageHeader+"\n"+sendingMsg : sendingMsg;
					//if(entireListRId.isChecked() && optOutSettings != null)
					if(!msgTypeRgId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)
						&& entireListRId.isChecked() && optOutSettings != null){
						if(!currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {
						sendingMsg = sendingMsg+ (optOutSettings.getKeyword() != null ?
								("\n" + "Reply " + optOutSettings.getKeyword() + " 2 Optout")
										: "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
						}else {
							sendingMsg = sendingMsg+ (optOutSettings.getKeyword() != null ?
									("\n" +optOutSettings.getKeyword())
											: "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
						}

					}

				}
			}

			
			
			logger.info("sendingMsg after--- "+sendingMsg);
			charCount = sendingMsg.length();
		    usedCount=1;
		     if(charCount>160) usedCount = charCount/160 + 1;
			captiwayToSMSApiGateway.sendToSMSApi(ocgateway, sendingMsg, 
					""+mblNumber, msgType, "9848495956", ""+mblNumber, "1", senderId,smsCampaign.getTemplateRegisteredId());
			
			/**
		     * Update the Used SMS count
		     */
		    try{
		     UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		     UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
		     //usersDao.updateUsedSMSCount(userId, mobCnt);
		     		
		     usersDaoForDML.updateUsedSMSCount(userId, usedCount*mobCnt);
		    }catch(Exception exception){
		     logger.error("Exception while updating the Used SMS count",exception);
		    }
			
			MessageUtil.setMessage("Test SMS will be sent in a moment.", "color:green;", "top");
			
			
			/**
			 * Update SmsQueue
			 */
			SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
			
			
			smsQueueHelper.updateSMSQueue(mblNumber, sendingMsg, Constants.SMS_MSG_TYPE_TEST, currUser,senderId);
			
			/*mblNoTxtBoxId.setValue("");
			SMSMsgTbId.setValue("");*/
		}catch (NumberFormatException e) {
			MessageUtil.setMessage("Please provide valid mobile number to send test SMS.", "color:red;","top");
			logger.error("** Exception",e);
		}catch (Exception e) {
			logger.error("** Exception while sending a test SMS",e);
		}
		
		return true;
		
		
	}
	
	
	
		
	/**
	 * sends a test SMS in step 1
	 */
	
	public void onClick$sendTestSmsBtnId() { 
		
		//String msg = "Test SMS : " +SMSMsgTbId.getText();
		if(smsCampaign==null){
			MessageUtil.setMessage("The campaign has to be saved atleast once to send a Test SMS.", "color:red;");
			return;
		}
		String msg = smsCampaign.getMessageContent();
		
		if(sendTestSMS (mblNoTxtBoxId.getValue(),msg)) {
			
			mblNoTxtBoxId.setValue("Enter Mobile Number(s)...");
		}
		
	}//onClick$sendTestSmsBtnId()
	
	
	/**
	 * sends a test SMS in step 3
	 */
	
	public void onClick$okBtnId() { 
		
		if(smsCampaign == null) {
			
			logger.debug("no Root obj found");
			return;
			
		}
		//String sendingMsg = "Test SMS : " +smsCampaign.getMessageContent();
		String sendingMsg = smsCampaign.getMessageContent();
		if(sendTestSMS (testSMSTbId.getValue(), sendingMsg)) {
			
			testSMSTbId.setValue("Enter Mobile Number(s)...");
		}
		
	}//onClick$sendTestSmsBtnId()
	
	
	
	
	/**
	 * split the SMS to be send 
	 * @param msgContent is actual msg content to be split
	 * @param size specifies the number of characters upto which extent the msg to be split
	 * @return List of message tokens
	 */
	private ArrayList<String> splitSMSMessage(String msgContent, int size) {
		
		try {
			ArrayList<String> retList = new ArrayList<String>();
			
			int skipCounter=0;
			
			do {
				skipCounter++;
				
				if(msgContent.length() > size-10) {
					
					int endInd = msgContent.indexOf(' ', size-20);
					
					if(endInd==-1 || endInd > size-5) {
						endInd = msgContent.lastIndexOf(' ', size-20);
					}
					
					if(endInd==-1) {
						logger.debug("No Spaces in the Given Token");
						break;
					}

					/*
					 * While splitting, if it is inside the Place holder
					 * then find out for a new space before/after from that position for split
					 */  
					
					
					if(msgContent.lastIndexOf("|^", endInd) != -1 && 
							msgContent.indexOf("^|", endInd) != -1 &&
							(endInd > msgContent.lastIndexOf("|^", endInd)) && 
							(endInd < msgContent.indexOf("^|", endInd)) )  {
						
						
						int phStInd = msgContent.lastIndexOf("|^", endInd);
						int phEnInd = msgContent.indexOf("^|", endInd);
						
						int tempEndInd=endInd;
						
						if(msgContent.substring(phStInd+2, endInd).indexOf("^|")==-1) {
							endInd = msgContent.lastIndexOf(' ', phStInd);
						}
						
						if(endInd==-1 && (msgContent.substring(tempEndInd, phEnInd).indexOf("|^")==-1)) {
							endInd = msgContent.indexOf(' ', phEnInd+1);
						}
						if(endInd==-1)	{
							break;
						}
						
					} // if
					
					String tempStr = msgContent.substring(0,endInd);
					retList.add(tempStr);
					
					msgContent = msgContent.substring(endInd+1);
					
					
				} //if
				
				if(skipCounter>40) {
					break;
				}
			} while(msgContent.length() > size-10);
			
			if(msgContent.length() > 0) {
				retList.add(msgContent);
			}
			
			return retList;
			
		} catch (Exception e) {
			logger.error("** Error occured while submitting the SMS campaigns",e);
			return null;
		}
		
	}//splitSMSMessage
	
	
	
	
	
	/**
	 * checks whether the given SMS campaign name exists or not,if exists dont allows the user further
	 */
	public void onBlur$smsCampNameTbId(){
		try {
			
			MessageUtil.clearMessage();
			/*******check the entered name is valid or not********/
			String emailName = Utility.condense(smsCampNameTbId.getValue().trim());
			if(emailName.trim().equals("")){
				// if name is empty
				smsCampNameStatusLblId.setStyle("color:red");
				smsCampNameStatusLblId.setValue("name should not be empty");
				return;
			}
			else if(!Utility.validateName(emailName)){ // check for special characters
				smsCampNameStatusLblId.setStyle("color:red");
				smsCampNameStatusLblId.setValue("Special characters not allowed");
				return;
			}//else
			
			/*******check the entered name is already exists or not********/
			boolean nameExists =  smsCampaignsDao.checkName(emailName,userId);
			if(nameExists){ // if name already exists
				smsCampNameStatusLblId.setStyle("color:red");
				smsCampNameStatusLblId.setValue("Already exists");
				return;
			}
			else{ // if name is new
				smsCampNameStatusLblId.setStyle("color:#023849");
				smsCampNameStatusLblId.setValue("Available");
			}//else
			
		} catch ( Exception e) {
			logger.error("** Exception : Error occured at sms campaign textbox validation. ",e);
		}//catch
		
	}//onBlur$smsCampNameTbId()
	
	public boolean validateCurrDiv(Div currDiv, boolean draft) {
		
		
		
		try {
			
			
			if(SMSStatusCodes.optOutFooterMap.get(currUser.getCountryType()) && 
					smsSettings == null) {
				
				MessageUtil.setMessage("No SMS settings found for your user account.\n Please contact Admin to enable SMS feature.", "color:red;");
				return false;
				
			}
			
			
			// Not allowoing for Promotion SMS creation  
			/*if(((String) msgTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase(Constants.SMS_TYPE_NAME_OUTBOUND)) {
				MessageUtil.setMessage("Please Contact to Admin for creating Promotional SMS ","color:red","TOP");
				return false;
				
			}*/
			
			if(currUser.getSubscriptionEnable()){
				
				if(!categoryLbId.isDisabled()) {
					
					if(categoryLbId.getSelectedItem().getLabel().equals("Select Category") || categoryLbId.getSelectedItem().getLabel().isEmpty() ){
						
						MessageUtil.setMessage("Please select campaign category.", "color:red", "TOP");
						return false;
						
					}
				}
			}
			
			//logger.info("currDiv :: "+currDiv);
			
			if(currDiv.getId().equals(step1DivId.getId())) {
				
				String smsCampName = smsCampNameTbId.getValue().trim();
				
				/**** validates SMS campaign name *****/
				if(smsCampNameTbId.isValid()) {
					
					if(smsCampName.trim().length() == 0) {
						
						MessageUtil.setMessage("Enter SMS campaign name. " +
								"Name cannot be left empty.","color:red","TOP");
						return false;
						
						
					}
					if(!Utility.validateName(smsCampName)) {
						
						MessageUtil.setMessage("Enter valid SMS campaign name. " +
								"Special characters are not allowed.","color:red","TOP");
						
						return false;
					}
					if(smsCampaign == null) {
						boolean nameExists =  smsCampaignsDao.checkName(smsCampName,userId);
						
						if(nameExists){
							
							MessageUtil.setMessage("This SMS campaign name is already in use. " +
									"Please choose a different name.", "color:red", "TOP");
							smsCampNameTbId.setFocus(true);
							return false;
						} // else if
					
					}
					
				} // if
				else{
					MessageUtil.setMessage("Provide valid SMS campaign name.", "color:red", "TOP");
					smsCampNameTbId.setFocus(true);
					return false;
				} // else
				
				/***** validates senderId *****/
				if(senderIdTbId.getText().trim().length() == 0) {
					
					MessageUtil.setMessage("There are no sender ID(s) to send an SMS. SMS cannot be sent.","color:red","TOP");
					return false;
				}
			
				/***** validates messagetype ******/
				
				//Check if its Not Transactional sms type
				logger.debug("SMS Campaign is  :: "+smsCampaign);
				if(smsCampaign != null){
					logger.debug(" condition checking  is  :"+!(smsCampaign.getMessageType().equals(Constants.SMS_TYPE_TRANSACTIONAL)));
					
					if(!(smsCampaign.getMessageType().equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)) && ((String) msgTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)){
						MessageUtil.setMessage("SMS campaign cannot change to transactional type. ", "color:red;", "top");
						return false;
					}
				}
				
				
				
				if(msgTypeRgId.getSelectedItem() == null) {
					
					MessageUtil.setMessage("Please select the option specifying SMS message type.","color:red","TOP");
					return false; 
					
				}// six
				/*else if(msgTypeRgId.getSelectedIndex() == 0) {
					MessageUtil.setMessage("Please select the option specifying SMS message type.","color:red","TOP");
					return false; 
				}else if(msgTypeRgId.getSelectedIndex() == 2 ){
					MessageUtil.setMessage("Please select the option specifying SMS message type.","color:red","TOP");
					return false; 
				}*/
			logger.debug("msgType getSelected Index = "+msgTypeRgId.getSelectedIndex());
				/*****validate message content**********+*********/
				String messageContent = SMSMsgTbId.getValue();
				
				if(messageContent != null && messageContent.trim().length() == 0 && !((String) msgTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)) {
					
					MessageUtil.setMessage("SMS message cannot be left empty. Please provide message text.", "color:red;", "top");
					return false;
										
				}else if(((String) msgTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL) && messageContent != null && messageContent.trim().length() == 0 && messageContent.isEmpty()) {
					MessageUtil.setMessage("Transactional SMS message cannot be left empty. Please select a message template.", "color:red;", "top");
					return false;
				}
				
				// validating date placeholder
				String isValidPhStr = Utility.validatePh(messageContent.trim(),GetUser.getUserObj());
				
				if(isValidPhStr != null) {
							
							MessageUtil.setMessage("Placeholder "+isValidPhStr+ " is invalid please replace with proper placeholder values.", "color:red;", "top");
							return false;
				}
				//decide if any other validations are require??????????????/
				
				//validate coupon placeholders with couponId and userId
				logger.debug("messageContent :"+messageContent);
				Set<String> couponPhSet = findCoupPlaceholders(messageContent.trim());
				logger.debug("couponPhSet :"+couponPhSet);
				String couponIdStr = "";
				for(String ph : couponPhSet){
					
					if(ph.startsWith("CC_")){
						
						String[] phStr = ph.split("_");
						if(phStr.length > 2){
							
							MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
							return false;
						}
						
						couponIdStr += couponIdStr.trim().isEmpty() ? phStr[1].trim() : ","+phStr[1].trim();
						Long couponId = null;
						try{
						couponId = Long.parseLong(phStr[1]);
						}catch(Exception e){
							MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
							return false;
						}
						
						if(couponId != null){
							
							CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
							Long orgId = currUser.getUserOrganization().getUserOrgId();
							
							List<Coupons> couponsList = couponsDao.findCouponsByCoupIdsAndOrgId(""+couponId, orgId);
							if(couponsList == null){
								MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
								return false;
							}
						}
					}
					
				}
				
				if(!couponIdStr.trim().isEmpty()){
					logger.debug("couponIdStr :"+couponIdStr);
					List<Coupons> inValidCoupList = couponsDao.isExpireOrPauseCoupList(couponIdStr, currUser.getUserOrganization().getUserOrgId());
					
					if(inValidCoupList != null){
						
						String inValidCoupNames = "";
						if(inValidCoupList != null && inValidCoupList.size() >0) {
							
							for (Coupons coupons : inValidCoupList) {
								inValidCoupNames += inValidCoupNames.trim().length() >0 ? ","+coupons.getCouponName() : coupons.getCouponName();
							}
							MessageUtil.setMessage(	"The Discount Code "+inValidCoupNames+" used in this campaign has either expired or in paused status. " +
									" \n Please change the status of this Discount Code.",
									"color:red", "TOP");
							return false;
						}
						
					}
					
				}
				
				Calendar cal = Calendar.getInstance();
				
				if(smsCampaign == null){
					smsCampaign = new SMSCampaigns(cal, currUser);
					smsCampaign.setModifiedDate(cal);
					
				}
				smsCampaign.setSmsCampaignName(smsCampName);
				smsCampaign.setSenderId(senderIdTbId.getAttribute(SENDERID_ATTRIBUTE).toString());//senderIdTbId.getText().trim());
				
				/*********** Added for campaign categories**********/
				Long categoryName =null;
				if(!currUser.getSubscriptionEnable())categoryLbId.setDisabled(true);
				
				if(currUser.getSubscriptionEnable()){
					Listitem catgoryItem = categoryLbId.getSelectedItem();
					
					if(catgoryItem.getIndex() != 0) {
						UserCampaignCategories userCampaignCategories = (UserCampaignCategories)catgoryItem.getValue();
						categoryName = userCampaignCategories.getId();
					}
					
				}
				smsCampaign.setCategory(categoryName);
				
				/**********************ended************* ***/
				
				
				if(!(msgTypeRgId.getSelectedIndex() == -1)) {
					
					smsCampaign.setMessageType((String) msgTypeRgId.getSelectedItem().getValue());
					/*if(((String) msgTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase(Constants.SMS_TYPE_OUTBOUND)) {
						smsCampaign.setMessageType(Constants.SMS_TYPE_OUTBOUND);
					}//outbound mesage
					else if(((String) msgTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase(Constants.SMS_TYPE_2_WAY)) {
						smsCampaign.setMessageType(Constants.SMS_TYPE_2_WAY);
					}//2-way seven
					else if(((String) msgTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)) {
						smsCampaign.setMessageType(Constants.SMS_TYPE_TRANSACTIONAL);
					}//Transactional sms
					else if(((String) msgTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase(Constants.SMS_TYPE_PROMOTIONAL)) {
						smsCampaign.setMessageType(Constants.SMS_TYPE_PROMOTIONAL);
					}//Transactional sms
*/					
				}	
				
				Radio selectedRadio = sendTypeRgId.getSelectedItem();
				if(selectedRadio.getId().equalsIgnoreCase("entireListRId")) {
					
					smsCampaign.setEnableEntireList(true);
				}
				else if(selectedRadio.getId().equalsIgnoreCase("optInRId") ) {
					
					smsCampaign.setEnableEntireList(false);
				}
				
				
				smsCampaign.setMessageSizeOption((byte)8);
				smsCampaign.setMessageContent(messageContent);
				if(SMSMsgTbId.getAttribute("templateRegisteredId")!=null)smsCampaign.setTemplateRegisteredId((SMSMsgTbId.getAttribute("templateRegisteredId").toString()));
				if(approveTemplateDivId.isVisible() && templateIdTxtBoxId.getText()!=null && !templateIdTxtBoxId.getText().isEmpty())smsCampaign.setTemplateRegisteredId(templateIdTxtBoxId.getText());
				logger.info("isEdit ==== >"+isEdit);
				if(gotoStep2BtnId.getLabel().equalsIgnoreCase("Next")) {
					
					smsCampaign.setStatus(Constants.CAMP_STATUS_DRAFT);
					
					if(smsCampaign.getDraftStatus() == null ){
						smsCampaign.setDraftStatus(Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO);
					}
					//smsCampaignsDao.saveOrUpdate(smsCampaign);
					if(SMSMsgTbId.getAttribute("templateRegisteredId")!=null)smsCampaign.setTemplateRegisteredId((SMSMsgTbId.getAttribute("templateRegisteredId").toString()));
					if(approveTemplateDivId.isVisible() && templateIdTxtBoxId.getText()!=null && !templateIdTxtBoxId.getText().isEmpty())smsCampaign.setTemplateRegisteredId(templateIdTxtBoxId.getText());
					smsCampaignsDaoForDML.saveOrUpdate(smsCampaign);
				}
				if(isEdit!=null){
					if(isEdit.equalsIgnoreCase("edit") || (draftStatus != null && draftStatus.equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_THREE))){
						Clients.evalJavaScript("changeStep(3, true);");
						try{
							if(smsCampaign.getMessageType() != null ) {

								String msgType = smsCampaign.getMessageType();

								Set<String> msgTypeSet = SMSStatusCodes.campTypeMap.get(currUser.getCountryType()).keySet();
								String campTypeLablValue = Constants.STRING_NILL;
								String availableMsgType = Constants.STRING_NILL;



								for (String type : msgTypeSet) {

									availableMsgType = (SMSStatusCodes.campTypeMap.get(currUser.getCountryType()).get(type));
									availableMsgType = availableMsgType.substring(0, availableMsgType.indexOf(Constants.ADDR_COL_DELIMETER));

									if(msgType.equals(availableMsgType)) {

										campTypeLablValue = type;
										break;

									}
								}//for

								logger.info("campTypeLablValue >>>>>>>>> "+campTypeLablValue);
								campTypeLblId.setValue(campTypeLablValue);
								
								senderIdLblId.setValue((msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE_REQUIRED).
										toString().equals("N") && !(sendTypeRgId.getSelectedItem().getId().equalsIgnoreCase("optInRId")) )? Constants.TWO_WAY_PROMO_TEXT_ID : msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE).
												toString());
								
								
								if(sendTypeRgId.getSelectedItem().getId().equalsIgnoreCase("optInRId"))
									senderIdLblId.setValue(senderIdTbId.getAttribute(SENDERID_ATTRIBUTE).toString());
								
								logger.info("type set 1>>>>>>>>> ");

							}

						}catch(Exception e){
							logger.error("Exception >>>>>>>>>>> ",e);
						}
						
						// Added for campaign categories..
						
						Long categoryId= smsCampaign.getCategory();
						logger.info("category is "+categoryId);
						UserCampaignCategories userCmapObj  = null;
						if(categoryId != null){
							
							userCmapObj =userCampaignCategoriesDao.findById(categoryId.toString());
						}
						if(currUser.getSubscriptionEnable() &&(userCmapObj != null && userCmapObj.getIsVisible())){
							
							categoryNameLblId.setValue(userCmapObj.getCategoryName());
							
						}else categoryNameLblId.setValue("");
						
						
						
						if(SMSMsgTbId.getAttribute("templateRegisteredId")!=null)smsCampaign.setTemplateRegisteredId((SMSMsgTbId.getAttribute("templateRegisteredId").toString()));
						if(approveTemplateDivId.isVisible() && templateIdTxtBoxId.getText()!=null && !templateIdTxtBoxId.getText().isEmpty())smsCampaign.setTemplateRegisteredId(templateIdTxtBoxId.getText());
						//smsCampaignsDao.saveOrUpdate(smsCampaign);
						smsCampaignsDaoForDML.saveOrUpdate(smsCampaign);
					}
					else if(isEdit.equalsIgnoreCase("view")){
						Clients.evalJavaScript("changeStep(2, true);");
						try{
							
							if(smsCampaign.getMessageType() != null ) {
								
								String msgType = smsCampaign.getMessageType();
								
								Set<String> msgTypeSet = SMSStatusCodes.campTypeMap.get(currUser.getCountryType()).keySet();
								String campTypeLablValue = Constants.STRING_NILL;
								String availableMsgType = Constants.STRING_NILL;
								
								for (String type : msgTypeSet) {
									
									availableMsgType = (SMSStatusCodes.campTypeMap.get(currUser.getCountryType()).get(type));
									availableMsgType = availableMsgType.substring(0, availableMsgType.indexOf(Constants.ADDR_COL_DELIMETER));
									
									if(msgType.equals(availableMsgType)) {
										
										campTypeLablValue = type;
										break;
										
									}
								}//for
								
								logger.info("campTypeLablValue >>>>>>>>> "+campTypeLablValue);
								campTypeLblId.setValue(campTypeLablValue);
								
								
								senderIdLblId.setValue((msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE_REQUIRED).
										toString().equals("N") && !(sendTypeRgId.getSelectedItem().getId().equalsIgnoreCase("optInRId")) )? Constants.TWO_WAY_PROMO_TEXT_ID : msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE).
												toString());
								
								
								if(sendTypeRgId.getSelectedItem().getId().equalsIgnoreCase("optInRId"))
									senderIdLblId.setValue(senderIdTbId.getAttribute(SENDERID_ATTRIBUTE).toString());
								
								
								logger.info("type set 1>>>>>>>>> ");
								
							}
							
							
						}catch(Exception e){
							logger.error("Exception >>>>>>>> ",e);
						}
						
						
					}
					
					
				}else{
					/*session.removeAttribute("editSmsCampaign");
					session.removeAttribute("smsDraftStatus");
					*/
					configureDivDefaultChanges(step2DivId);
					if(!draft){
						Clients.evalJavaScript("changeStep(2, true);");
					}else{
						smsCampaign.setStatus(Constants.CAMP_STATUS_DRAFT);
						//session.removeAttribute("smsCampaign");
						if(smsCampaign.getDraftStatus() == null )smsCampaign.setDraftStatus(Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO);
						//smsCampaignsDao.saveOrUpdate(smsCampaign);
						if(SMSMsgTbId.getAttribute("templateRegisteredId")!=null)smsCampaign.setTemplateRegisteredId((SMSMsgTbId.getAttribute("templateRegisteredId").toString()));
						if(approveTemplateDivId.isVisible() && templateIdTxtBoxId.getText()!=null && !templateIdTxtBoxId.getText().isEmpty())smsCampaign.setTemplateRegisteredId(templateIdTxtBoxId.getText());
						smsCampaignsDaoForDML.saveOrUpdate(smsCampaign);
						//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_SMS_CAMPAIGNS);
						Redirect.goTo(PageListEnum.CAMPAIGN_SMSCAMPAIGN_LIST);
					}
				}
				
				/*goToNextBtnId.setLabel("Next");
				backButtonId.setVisible(true);*/
				//Clients.evalJavaScript("changeStep(2, true);");
				return true;
				
				
			}//if div 1
			
			else if(currDiv.getId().equals(step2DivId.getId())) {
				
				
				if(configurelistRgId.getSelectedIndex() == 0) {
					
					if(saveMlist() == false) return false;
				}
				else if(configurelistRgId.getSelectedIndex() == 1) {
					
					if(saveSegRule() == false) return false;
					
				}
				
				if(smsCampaign == null) {
					
					logger.debug("need to create the Root Obj prior to the 3rd step");
					return false;
					
				}
				
				logger.debug("----SMS_CAMP_DRAFT_STATUS_STEP_THREE validate div ----");
				
				
				senderIdLblId.setValue((msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE_REQUIRED).
						toString().equals("N") && !(sendTypeRgId.getSelectedItem().getId().equalsIgnoreCase("optInRId")) )? Constants.TWO_WAY_PROMO_TEXT_ID : msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE).
								toString());
								
				//senderIdLblId.setValue(smsCampaign.getSenderId());
				/*
				if(msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE_REQUIRED).toString().equals("N")) {
					
					if(sendTypeRgId.getSelectedItem().getId().equalsIgnoreCase("optInRId"))
						senderIdLblId.setValue(senderIdTbId.getAttribute(SENDERID_ATTRIBUTE).toString());
				}*/
				
				if(sendTypeRgId.getSelectedItem().getId().equalsIgnoreCase("optInRId"))
					senderIdLblId.setValue(senderIdTbId.getAttribute(SENDERID_ATTRIBUTE).toString());
				
				campNameLblId.setValue(smsCampaign.getSmsCampaignName());
				// Added for campaign categories..
				
				Long categoryId= smsCampaign.getCategory();
				logger.info("category is "+categoryId);
				UserCampaignCategories userCmapObj  = null;
				if(categoryId != null){
					
					userCmapObj =userCampaignCategoriesDao.findById(categoryId.toString());
				}
				if(currUser.getSubscriptionEnable() &&(userCmapObj != null && userCmapObj.getIsVisible())){
					
					categoryNameLblId.setValue(userCmapObj.getCategoryName());
					
				}else categoryNameLblId.setValue("");
				
				
				if(smsCampaign.getMessageType() != null ) {
					
					campTypeLblId.setValue(smsCampaign.getMessageType().equalsIgnoreCase(Constants.SMS_TYPE_OUTBOUND) ? "Outbound Message" : "2-Way Messaging");
					
					String messageType = "";
					
					if(smsCampaign.getMessageType().equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)) {
						messageType = Constants.SMS_TYPE_NAME_TRANSACTIONAL;
					}else if(smsCampaign.getMessageType().equalsIgnoreCase(Constants.SMS_TYPE_2_WAY)) {
						messageType = Constants.SMS_TYPE_NAME_2_WAY;
					}else if(smsCampaign.getMessageType().equalsIgnoreCase(Constants.SMS_TYPE_OUTBOUND)) {
						messageType = Constants.SMS_TYPE_NAME_OUTBOUND;
					}
					else if(smsCampaign.getMessageType().equalsIgnoreCase(Constants.SMS_TYPE_PROMOTIONAL)) {
						messageType = Constants.SMS_TYPE_NAME_PROMOTIONAL;
					}
					campTypeLblId.setValue(messageType);
					
					
				}
				smsCampaign.setDraftStatus(Constants.SMS_CAMP_DRAFT_STATUS_STEP_THREE);
				setMlistLinks();
				goToNextBtnId.setLabel("Schedule");
				backButtonId.setVisible(false);
				Clients.evalJavaScript("changeStep(3, true);");
				if(SMSMsgTbId.getAttribute("templateRegisteredId")!=null)smsCampaign.setTemplateRegisteredId((SMSMsgTbId.getAttribute("templateRegisteredId").toString()));
				if(approveTemplateDivId.isVisible() && templateIdTxtBoxId.getText()!=null && !templateIdTxtBoxId.getText().isEmpty())smsCampaign.setTemplateRegisteredId(templateIdTxtBoxId.getText());
				//smsCampaignsDao.saveOrUpdate(smsCampaign);
				smsCampaignsDaoForDML.saveOrUpdate(smsCampaign);
				
				
			}//if div 2


				
				return true;
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return false;
		}
		
		
	}
	
public void onClick$createNewSegAnchId() {
		
		Redirect.goTo(PageListEnum.CONTACT_MANAGE_SEGMENTS);
		
	}
	/**
	 * configures the schedules for a specific day
	 */
	 
	public void onClick$prtDtBtnId() {
		
		if(currUser.getSmsCount()==0){
			
			MessageUtil.setMessage("SMS credits are not available. Please renew your account.", "color:red;", "top");
			return;
			
		}
		//smsCampaign = (SMSCampaigns)session.getAttribute("smsCampaign");
		
		if(smsCampaign == null) { // to have the SMSCampaign obj's id 
			
			logger.debug("no Root object found");
			return;
			
		}//if
		
		if(Constants.SMS_TYPE_2_WAY.equalsIgnoreCase(smsCampaign.getMessageType())) {
			if(IsUsingExpKeyword(smsCampaign.getMessageContent(), prtDtBxId.getServerValue())) {
				
				return;
			}
		}		
		Calendar cal = null;
		schErrorLblId.setValue("");
		schErrorLblId.setVisible(true);
		try {
			
			//STARTS 12, to make similar as email campaign schedules 
			//Following were not commented earlier, i.e. 1 hour gap test is commented, to make similar like email schedules
			/*cal = prtDtBxId.getServerValue();
			
			//************** Test for 1 hour gap between scheduled dates ******************
			
			for (Calendar tempCal : tempList) {
				logger.debug("Dates are as ............"+ MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_SCHEDULE_TIME));
				logger.info("tempCal.compareTo(cal)="+tempCal.compareTo(cal));
			}
			
			for (Calendar tempCal : tempList) {
				
				//logger.info("tempCal.compareTo(cal)="+tempCal.compareTo(cal));
				
				
				if(Math.abs(tempCal.getTimeInMillis() - cal.getTimeInMillis()) < 3600000 ) {
					schErrorLblId.setValue(" Date must be at least one hour after the previous Active schedule date.");
					logger.debug("Resend date must be at least one hour after the actual schedule date.");
					return;
				}
			} // for
			
			*/
			
			//ENDS 12, to make similar as email campaign schedules
			//STARTS 13, to make similar as email campaign schedules 
			
			//TimeZone clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
			//Calendar temCal = new  MyCalendar(clientTimeZone);
//			Calendar cal = Calendar.getInstance();
			schErrorLblId.setValue("");
				
			if(prtDtBxId.getValue() == null) {
				schErrorLblId.setValue(" Please select the date");
				return;
			}


			//MyCalendar tempCal= (MyCalendar)prtDtBxId.getClientValue();
			Calendar tempCal= prtDtBxId.getServerValue();
			//tempCal.setTimeZone(clientTimeZone);

			/*tempCal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hoursListBxId.getSelectedItem().getLabel().trim()));
				tempCal.set(Calendar.MINUTE, Integer.parseInt(mintsListBxTd.getSelectedItem().getLabel().trim()));*/
			logger.info("get Hour :" +tempCal.get(Calendar.HOUR_OF_DAY));
			//cal = prtDtBxId.getServerValue();
			addDateToGrid(tempCal);
			
			//ENDS 13, to make similar as email campaign schedules 
			
		} 
		catch (Exception e) {
			logger.warn("Exception : Date is not selected" + e);
//			MessageUtil.setMessage("Please select the date.", "color:red", "TOP");
			schErrorLblId.setValue(" Please select the date");
			return;
		}
		
		//addDateToGrid(cal);
		//STARTS 14, to make similar to email campaign schedules
		//following line was not commented earlier
		//createRow(cal,schedGrdRowsId);
		//ENDS 14, to make similar to email campaign schedules
		
	} // onClick$prtDtBtnId
	
	
	/**
	 * sets the periodical schedules to the SMS campaign
	 */
	public void onClick$frqBtnId() {
		
		try {
			
			if(smsCampaign == null){
				
					MessageUtil.setMessage("Please fill the entries for SMS campaign properly before it is scheduled.", "color:red", "top");
					return;
			}
			
			if(Constants.SMS_TYPE_2_WAY.equalsIgnoreCase(smsCampaign.getMessageType())) {
				if(IsUsingExpKeyword(smsCampaign.getMessageContent(), startDtBxId.getServerValue())) {
					
					return;
				}
			}
			schErrorLblId.setValue("");
			Calendar startCal = null;
			Calendar endCal = null;
			
			try {
				startCal = startDtBxId.getServerValue();
				endCal = endDtBxId.getServerValue();
				
				//************** Test for 1 hour gap between scheduled dates ******************
				
				//STARTS 1, change  w.r.t similarities with email campaign schedules(change date 16th jan 2016)
				//initially this for loop was not commented(change date 16th jan 2016)
				/*for (Calendar tempCal : tempList) {
					
					//logger.info("tempCal.compareTo(cal)="+tempCal.compareTo(startCal));
					
					
					if((Math.abs(tempCal.getTimeInMillis() - startCal.getTimeInMillis()) < 3600000 ) || 
							(Math.abs(tempCal.getTimeInMillis() - endCal.getTimeInMillis()) < 3600000 ) ){
						schErrorLblId.setValue(" date must be at least one hour after the previous Active schedule date.");
						logger.debug("Resend date must be at least one hour after the actual schedule date.");
						return;
					}
					
				}*/
				
				//ENDS 1, change  w.r.t similarities with email campaign schedules(change date 16th jan 2016) 
			
			} 
			catch (Exception e) {
				logger.warn("Exception : startDate / endDate is not selected" + e);
				schErrorLblId.setValue(" Please select the dates");

				return;
			}
			
			if(startCal == null || endCal == null) {
				schErrorLblId.setValue("Please select the dates");
				return;
			}
			if(startCal.compareTo(Calendar.getInstance()) < 0 ) {
				schErrorLblId.setValue("Start date should be future date");
				return;
			}
			if(	endCal.compareTo(Calendar.getInstance()) < 0 ) {
				schErrorLblId.setValue("End date should be future date");
				return;
			}
			if(endCal.compareTo(startCal) < 0) {
				schErrorLblId.setValue("End date should be after the start date");
				return;
			}
			
			//STARTS 2, change  w.r.t similarities with email campaign schedules(change date 16th jan 2016)
			// Following block was not there, now added for same dates entered in start and end cal, w.r.t similarities with email campaign schedules(change date 16th jan 2016) -- rajeev
			if(startDtBxId.getServerValue().compareTo( endDtBxId.getServerValue()) == 0) {
				schErrorLblId.setValue("Start and end schedules cannot be same.");
				//				MessageUtil.setMessage("Start and end date should not be same.", "color:red", "TOP");
				return ;
			}
			//ENDS 2, change  w.r.t similarities with email campaign schedules(change date 16th jan 2016) 
			
			/*
			List<SMSSMSCampaignSchedule> smsCampScheduleList = 
						addDates(startDtBxId.getServerValue(), endDtBxId.getServerValue(),
						Integer.parseInt((String)frqLbId.getSelectedItem().getValue()),
						((Integer)frqLbId.getSelectedItem().getAttribute("step")).byteValue());
			
			if(smsCampScheduleList != null) {
				
				for (SMSCampaignSchedule smsCampSchedule:smsCampScheduleList) {
					createRow(smsCampSchedule, schedGrdRowsId);
				}
				
				tempList.add(startCal);
				tempList.add(endCal);
			}//if
			
			
		} 
		catch (NumberFormatException e) {
			logger.error("Exception ::" , e);
		}
	}//onClick$frqBtnId
	*/
			
			//TODO Same Day Check

			SMSCampaignSchedule campaignSchedule1 = addDateCheck(startCal, null, (byte)0);

			if(campaignSchedule1 == null){
				//MessageUtil.setMessage("Schedule added on same date and time.\n Please select a different time.", "color:red", "TOP");
				return ;
			}
			int confirm = 0;
			boolean isSameDay = false;
			List<String> dates = new ArrayList<String>();
			Calendar cal = Calendar.getInstance();
			//cal.setTimeZone((TimeZone) sessionScope.get("clientTimeZone"));
			List<String> nowSch = getSchedulesDates(startDtBxId.getServerValue(), endDtBxId.getServerValue(),
					Integer.parseInt((String)frqLbId.getSelectedItem().getValue()),
					((Integer)frqLbId.getSelectedItem().getAttribute("step")).byteValue());
			
			List<SMSCampaignSchedule> activeDraftCampScheduleList =  getActiveCampScheduleList(smsCampScheduleList);
/*			
			for(String now : nowSch){
				for (SMSCampaignSchedule campSched : activeDraftCampScheduleList) {
					//String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));

					String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
					//logger.debug("nownow :"+now+" : sameSchsameSch"+sameSch+" :now.equals(sameSch)"+now.equals(sameSch));
					if( now.equals(sameSch) && (campSched.getStatus() == 0 
							||  campSched.getStatus() == 2 )){

						dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));
					}
				}
			}
		*/
			
			
			//Like email-----starts
			for(String now : nowSch){
				for (SMSCampaignSchedule campSched : activeDraftCampScheduleList) {
					//String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));

					String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
					//logger.debug("nownow :"+now+" : sameSchsameSch"+sameSch+" :now.equals(sameSch)"+now.equals(sameSch));
					if( now.equals(sameSch) && (campSched.getStatus() == 0 
							||  campSched.getStatus() == 2 )){

						dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));
					}
				}
				
				
			}
		
			
			logger.info("dates size is >>>>>>>>> "+(dates != null ? dates.size() : "dates is null")); // remove this after testing
			
			for(String now : nowSch){
				for (SMSCampaignSchedule campSched : activeDraftCampScheduleList) {
					//String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));

					String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
				//	logger.debug("nownow :"+now+" : sameSchsameSch"+sameSch+" :now.equals(sameSch)"+now.equals(sameSch));
					if( now.equals(sameSch) && (campSched.getStatus() == 0 
							||  campSched.getStatus() == 2 )){

						//dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));

						/*if(campScheduleList != null && campScheduleList.size()>0){*/
						if(activeDraftCampScheduleList != null && activeDraftCampScheduleList.size() > 0){
							confirm = Messagebox.show("Schedule(s) already exist on  "+MyCalendar.calendarToString(activeDraftCampScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
									+" to " +  MyCalendar.calendarToString(activeDraftCampScheduleList.get(activeDraftCampScheduleList.size()-1).getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
									+ " for following time: "
									+"\n "+StringUtils.collectionToCommaDelimitedString(dates)+" \n Do you want to continue & add more?", 
									"Same Day Schedule Found", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						}
						isSameDay = true; 
						break;
						//}
					}
				}
				if(isSameDay) break;
			}
			logger.info("completed for isSameDay .......:"+isSameDay);
			if(confirm == 2){
				logger.info("returning");
				return;
			}

			

			if(smsCampScheduleList != null && (!isSameDay ||(confirm == 1 && isSameDay))) {
				
				List<SMSCampaignSchedule> campaignScheduleList = 
						addDates(startDtBxId.getServerValue(), endDtBxId.getServerValue(),
								Integer.parseInt((String)frqLbId.getSelectedItem().getValue()),
								((Integer)frqLbId.getSelectedItem().getAttribute("step")).byteValue());
				
				if(campaignScheduleList !=null && campaignScheduleList.size() >0){
					createDivUpComingCampaigns(true,campaignScheduleList.get(campaignScheduleList.size()-1));
					MessageUtil.setMessage("You have added "+campaignScheduleList.size()+" schedules to be sent between "
							+ ""+MyCalendar.calendarToString(campaignScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone"))
							+" to \n"+MyCalendar.calendarToString(campaignScheduleList.get(campaignScheduleList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone"))
							+".\n All your active schedules can be viewed by clicking on \n  \'View All Upcoming Schedules\' link.", "color:blue");


					frqBtnId.setAttribute(LATEST_SCH_ON, endCal);
					frqBtnId.setAttribute(START_SCH_ON, startCal);
					frqBtnId.setAttribute(FREEQUENCY, frqLbId.getSelectedItem().getLabel());
				}
				else{
					MessageUtil.setMessage("Please select valid date.", "color:red");
					logger.error("Invalid date...........");
				}
			}
			
			//Like email-----ends
			//from 3376-3499 i commented --rajeev date 13th jan 2016 
			/*if(nowSch != null && nowSch.size() > 0){
				for(String now : nowSch){
					for (SMSCampaignSchedule campSched : activeDraftCampScheduleList) {
						//String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));

						//String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
						String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"));
						logger.debug("nownow :"+now+" : sameSchsameSch"+sameSch+" :now.equals(sameSch)"+now.equals(sameSch));
						logger.debug("campSched.getStatus() : "+campSched.getStatus());
						if( now.equals(sameSch) && (campSched.getStatus() == 0 
								||  campSched.getStatus() == 2 )){

							//dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));

							if(campScheduleList != null && campScheduleList.size()>0){
							if(activeDraftCampScheduleList != null && activeDraftCampScheduleList.size() > 0){
								confirm = Messagebox.show("Schedule(s) already exist on  "+MyCalendar.calendarToString(activeDraftCampScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
										+" to " +  MyCalendar.calendarToString(activeDraftCampScheduleList.get(activeDraftCampScheduleList.size()-1).getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
										+ " for following time: "
										+"\n "+StringUtils.collectionToCommaDelimitedString(dates)+" \n Do you want to continue & add more?", 
										"Same Day Schedule Found", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							}
							isSameDay = true; 
							break;
							//}
						}
					}
					if(isSameDay) break;
				}
			
			}
			logger.info("completed for isSameDay .......:"+isSameDay);
			if(confirm == 2){
				logger.info("returning");
				return;
			}

			

			if(smsCampScheduleList != null && (!isSameDay ||(confirm == 1 && isSameDay))) {
				
				Calendar calExpCheck =  endDtBxId.getServerValue();
				
				if(!checkAvialbleAndExpiryStatus(calExpCheck,true)){
					return;
				}
				
				//mine
				
				
				
				List<String> extraSchStringList = new ArrayList<String>();
				if(confirm == 1 && isSameDay){
					boolean isNew = true;
					
					
					String sameSch = "";
					if(nowSch != null && nowSch.size() > 0){
						for(String now : nowSch){
							isNew = true;
							for (SMSCampaignSchedule campSched : activeDraftCampScheduleList) {
								

								sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"));
								//	logger.debug("nownow :"+now+" : sameSchsameSch"+sameSch+" :now.equals(sameSch)"+now.equals(sameSch));
								
								logger.debug("nownow :"+now+" : sameSchsameSch"+sameSch+" :now.equals(sameSch)"+now.equals(sameSch));
								logger.debug("campSched.getStatus() : "+campSched.getStatus());
								
								if( now.equals(sameSch) && (campSched.getStatus() == 0 
										||  campSched.getStatus() == 2 )){

									isNew = false;
									break;
								}
							}
							
							if(isNew && (activeDraftCampScheduleList.size()>0)){
								extraSchStringList.add(now);
							}
							
						}
					
					}
					
					
					
					
					
					
					
				}
				
				
				//mine
				
				
				
				
				
				
				
				List<SMSCampaignSchedule> campaignScheduleList = 
						addDates(startDtBxId.getServerValue(), endDtBxId.getServerValue(),
								Integer.parseInt((String)frqLbId.getSelectedItem().getValue()),
								((Integer)frqLbId.getSelectedItem().getAttribute("step")).byteValue(), extraSchStringList);
				
				if(campaignScheduleList !=null && campaignScheduleList.size() >0){
					createDivUpComingCampaigns(true,campaignScheduleList.get(campaignScheduleList.size()-1));
					MessageUtil.setMessage("You have added "+campaignScheduleList.size()+" schedules to be sent between "
							+ ""+MyCalendar.calendarToString(campaignScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone"))
							+" to \n"+MyCalendar.calendarToString(campaignScheduleList.get(campaignScheduleList.size()-1).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone"))
							+".\n All your active schedules can be viewed by clicking on \n  \'View All Upcoming Schedules\' link.", "color:blue");


					frqBtnId.setAttribute(LATEST_SCH_ON, endCal);
					frqBtnId.setAttribute(START_SCH_ON, startCal);
					frqBtnId.setAttribute(FREEQUENCY, frqLbId.getSelectedItem().getLabel());
				}
				else{
					MessageUtil.setMessage("Please select valid date.", "color:red");
					logger.error("Invalid date...........");
				}
			}*/
		} 
		catch (NumberFormatException e) {
			logger.error("Exception ::", e);
		}
	}
	

	
	private boolean checkAvialbleAndExpiryStatus(Calendar calExpCheck, boolean loadDBSch) {
		
		if(!loadDBSch){
			int available = 0;
			try {
				SubscriptionDetails subDetails = new SubscriptionDetails();
				available = subDetails.getSMSStatus(calExpCheck);
			} catch (Exception e1) {
				logger.error("** Exception : Problem while getting the subscription details", e1);
				return false;
			}
			if(available <=0) { 
				MessageUtil.setMessage("SMS cannot be scheduled as you have \n " +
						"reached your SMS credits limit or your account has expired.", 
						"color:red", "TOP");
				return false;
			}
		}

		if(calExpCheck.after(currUser.getPackageExpiryDate())) {
			MessageUtil.setMessage("Schedule date cannot be after your package expiry date.", "color:red", "TOP");
			return false;
		}
		return true;
	}



	/**
	 * getSchedulesDates
	 * @param startDtCal
	 * @param endDtCal
	 * @param frequency
	 * @param step
	 * @return List<String>
	 */
	private List<String> getSchedulesDates(Calendar startDtCal, Calendar endDtCal, 
			Integer frequency, Byte step) {

		if(logger.isDebugEnabled()) {
			logger.debug("-------- just entered getSchedulesDates---------");
		}

		if(startDtCal.compareTo(endDtCal) == 0) {
			schErrorLblId.setValue("Start and end date should not be same");
			//			MessageUtil.setMessage("Start and end date should not be same.", "color:red", "TOP");
			return null;
		}

		List<String> csList = new ArrayList<String>();

		SMSCampaignSchedule startDtCS = new SMSCampaignSchedule(
				startDtCal, smsCampaign.getSmsCampaignId(), (byte)0, (byte)0,currUser.getUserId() );
		startDtCS.setResendLevel((byte)0);
		startDtCS.setUserId(currUser.getUserId());
		//startDtCS.setSmsCsId(smsCampaignScheduleDao.getCurrentId());

		// add the starting date in the schedule final list 
		// if it doesn't contain with the same date

		if( !smsCampScheduleList.contains(startDtCS) ) {
			//campScheduleList.add(startDtCS);
			//csList.add(MyCalendar.calendarToString(startDtCS.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
			csList.add(MyCalendar.calendarToString(startDtCS.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")));
			
		}

		SMSCampaignSchedule nextDtCS;
		Calendar nextDate = Calendar.getInstance();
		Calendar nextDateTemp;
		nextDate.setTime(startDtCal.getTime());
        
		
		
		nextDate.set(frequency, startDtCal.get(frequency)+step);

		/** Generates the dates between the given dates and frequency **/
		while(nextDate.compareTo(endDtCal) <= 0 ) {

			nextDateTemp = Calendar.getInstance();
			nextDateTemp.setTime(nextDate.getTime());

			nextDtCS = new SMSCampaignSchedule(nextDateTemp, smsCampaign.getSmsCampaignId(), (byte)0, (byte)0, currUser.getUserId());

			nextDtCS.setResendLevel((byte)0);
			nextDtCS.setUserId(currUser.getUserId());
            
			//STARTS 9, to make similar as email campaign schedules
			//nextDtCS.setSmsCsId(smsCampaignScheduleDao.getCurrentId());
			//ENDS 9, to make similar as email campaign schedules
			if( !smsCampScheduleList.contains(nextDtCS) ) {
				//csList.add(MyCalendar.calendarToString(nextDtCS.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
				csList.add(MyCalendar.calendarToString(nextDtCS.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")));
				//campScheduleList.add(nextDtCS);
			}
			nextDate.set(frequency, nextDate.get(frequency)+step);


		}// while

		if(logger.isDebugEnabled()) {
			logger.debug("--------before exiting getSchedulesDates ---------");
		}
		return csList;
	}//getSchedulesDates


	private Button frqBtnId;
	private final String LATEST_SCH_ON = "ENDCAL";
	private final String START_SCH_ON = "STARTCAL";
	private final String FREEQUENCY = "FREQ";
	private Row currentRow;
	private Window resendOptionWinId;
	private Include resendEditorWinId$resendIncId;
	private Window resendEditorWinId,createTransTempWinId;
	private Textbox createTransTempWinId$transTempNameTxtBxId,createTransTempWinId$transTempContentTxtBxId,createTransTempWinId$transCaretPosTB,
				createTransTempWinId$transTempCharCountTxtBxId;
	
	
	/**
	 * prepare schedules to resend the existing  SMS campaign
	 */
	
	public void onClick$resendBtnId$resendOptionWinId() {
		try {
			logger.debug("----just entered to resend the SMS campaigns----");
			schErrorLblId.setValue("");
			byte criteria = Byte.parseByte(
					(String)resendOptionWinId$resendOptionLbId.getSelectedItem().getValue());
			
			SMSCampaignSchedule smsCampSchedule = (SMSCampaignSchedule)currentRow.getValue();
			Calendar resendTempCal = resendOptionWinId$resendOptionDbId.getServerValue();
			
			if (smsCampSchedule.getScheduledDate().getTimeInMillis() +(60*60*1000) > 
			 										resendTempCal.getTimeInMillis()) {
				
				resendOptionWinId$errMsgLblId.setValue(
						" Resend date must be atleast one hour after the actual schedule date");
				resendOptionWinId$errMsgLblId.setVisible(true);
				return;
			}//if
			
			//************** Test for 1 hour gap between scheduled dates ******************
			logger.debug("the time list size is==>"+tempList.size());
			for (Calendar tempCal : tempList) {
				
				logger.debug("the existing time is===>"+(tempCal.getTimeInMillis() - resendTempCal.getTimeInMillis()));
				if(Math.abs(tempCal.getTimeInMillis() - resendTempCal.getTimeInMillis()) < 3600000 ) {
					resendOptionWinId$errMsgLblId.setValue("Resend date must be at least one hour after the actual schedule date.");
					resendOptionWinId$errMsgLblId.setVisible(true);
					logger.debug("Resend date must be at least one hour after the actual schedule date.");
					return;
				}
			} // for
			
			tempList.add(resendTempCal);
			
			
			/******* add schedule and create a row  *******/
			SMSCampaignSchedule smsNewCampSchedule = addDate(resendTempCal, smsCampSchedule, criteria);
			
			if(smsNewCampSchedule == null) {
				resendOptionWinId$errMsgLblId.setValue("Selected date already exists");
				resendOptionWinId$errMsgLblId.setVisible(true);
				return;
			}
			
			resendOptionWinId$errMsgLblId.setVisible(false);
			resendOptionWinId.setVisible(false);
			
			//******** create rows (children if any) according to the given schedules **************
			createRow(smsNewCampSchedule, rowsMap.get(smsCampSchedule.getScheduledDate()));
			Detail detail = (Detail)((Rows)rowsMap.get(smsCampSchedule.getScheduledDate())).getParent().getParent(); 
			((Grid)((Rows)rowsMap.get(smsCampSchedule.getScheduledDate())).getParent()).setVisible(true);
			detail.setStyle("display:block;");
			detail.setOpen(true);
			
		} catch (NumberFormatException e) {
			logger.error("Exception while adding child schedules",e);
			logger.error("Exception ::" , e);
		}//catch
		
	}//onClick$resendBtnId$resendOptionWinId()
	
	
	/**
	 * Creates the list of SMSCampaignSchedule objects with the given frequency and if any</BR>
	 * date is already scheduled ignores that SMSCampaignSchedule.
	 * @param startDtCal
	 * @param endDtCal
	 * @param frequency
	 * @param step
	 * @return List of SMSCampaignSchedule objects
	 */
	private List<SMSCampaignSchedule> addDates(Calendar startDtCal, Calendar endDtCal, 
			Integer frequency, Byte step) {
		
		if(logger.isDebugEnabled()) {
			logger.debug("-------- just entered---------");
		}
		
		if(startDtCal.compareTo(endDtCal) == 0) {
			schErrorLblId.setValue("Start and end date should not be same");
			return null;
		}
		
		List<SMSCampaignSchedule> csList = new ArrayList<SMSCampaignSchedule>();
		
		SMSCampaignSchedule startDtCS = new SMSCampaignSchedule(startDtCal,
				smsCampaign.getSmsCampaignId(), (byte)0, (byte)0, currUser.getUserId() );
		startDtCS.setResendLevel((byte)0);
		
		//START 10, to make similar to email campaign schedules
		//startDtCS.setSmsCsId(smsCampaignScheduleDao.getCurrentId());
		//ENDS 10, to make similar to email  campaign schedules
		
		// add the starting date in the schedule "" list 
		// if it doesn't contain with the same date
		
		//logger.debug("-------- smsCampScheduleList.size()---------"+smsCampScheduleList.size());
		String sameSch="";
		
		/*if(extraSchStringList.size() == 0){
			if( !smsCampScheduleList.contains(startDtCS) ) { 
				if(!checkAlreadyThereWithinOneHr(smsCampScheduleList,startDtCS)){
					smsCampScheduleList.add(startDtCS);
					csList.add(startDtCS);
				}
			}
		}else{
			sameSch = MyCalendar.calendarToString(startDtCS.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"));
			if(extraSchStringList.contains(sameSch)){
				
				if(!checkAlreadyThereWithinOneHr(smsCampScheduleList,startDtCS)){
					smsCampScheduleList.add(startDtCS);
					csList.add(startDtCS);
				}
				
				
			}
		}*/
		
		//STARTS 11, to make similar to email campaign
		int count = activeCampaingsListlbId.getItemCount();
		boolean isCreditOrExipry   = createRowUpComingListBox(startDtCS,count,false);
		//createDivUpComingCampaigns(true,startDtCS);
		logger.info("campSchedule "+startDtCS.getSmsCsId()+": isCreditOrExipry :"+isCreditOrExipry);
		if(isCreditOrExipry == false){
			logger.error("Your Credit Limits are Exipred please contact support");
			return null;
		}
		/*else{
			createDivUpComingCampaigns(true,startDtCS);
			logger.info("**campSchedule "+startDtCS.getCsId()+": isCreditOrExipry :"+isCreditOrExipry);
		}*/
		
		if( !smsCampScheduleList.contains(startDtCS) ) {
			//startDtCS.setSmsCsId(null);
			smsCampScheduleList.add(startDtCS);
			csList.add(startDtCS);
		}
		
		Row row = new Row();
		row.setValue(startDtCS);
			
		if(schedGrdRowsId != null) {
			row.setParent(schedGrdRowsId);
		}
		
		SMSCampaignSchedule nextDtCS;
		Calendar nextDate = Calendar.getInstance();
		Calendar nextDateTemp;
		nextDate.setTime(startDtCal.getTime());
		
		nextDate.set(frequency, startDtCal.get(frequency)+step);
		
		/** Generates the dates between the given dates and frequency **/
		while(nextDate.compareTo(endDtCal) <= 0 ) {
			
			nextDateTemp = Calendar.getInstance();
			nextDateTemp.setTime(nextDate.getTime());
			
			nextDtCS = new SMSCampaignSchedule(nextDateTemp,
					smsCampaign.getSmsCampaignId(), (byte)0, (byte)0, currUser.getUserId() );
			nextDtCS.setResendLevel((byte)0);
			//nextDtCS.setSmsCsId(smsCampaignScheduleDao.getCurrentId());
			
			 count = activeCampaingsListlbId.getItemCount();
			 isCreditOrExipry   = createRowUpComingListBox(nextDtCS,count,false);
			 
			logger.info("campSchedule "+nextDtCS.getSmsCsId()+": isCreditOrExipry :"+isCreditOrExipry);
			if(isCreditOrExipry == false){
				logger.error("Your Credit Limits are Exipred please contact support");
				break;
			}
			else{
				//createDivUpComingCampaigns(true,nextDtCS);
				logger.info("**campSchedule "+nextDtCS.getSmsCsId()+": isCreditOrExipry :"+isCreditOrExipry);
			}
			
			//nextDtCS.setResendLevel((byte)0);
			
			if( !smsCampScheduleList.contains(nextDtCS) ) {
				
				csList.add(nextDtCS);
				smsCampScheduleList.add(nextDtCS);
				createRow(nextDtCS, schedGrdRowsId);
			}
			nextDate.set(frequency, nextDate.get(frequency)+step);
			
			
		}// while
		
		
		logger.debug("size of csList is====> "+csList != null ? csList.size() : "csList is null.");
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------before exiting---------");
		}
		return csList;
		
		//ENDS 11, to make similar to email campaign
		
		/*int count = activeCampaingsListlbId.getItemCount();
		boolean isCreditOrExipry   = createRowUpComingListBox(startDtCS,count,false); 
		logger.debug("-------- smsCampScheduleList.size()---------"+smsCampScheduleList.size()); // remove it after testing
		logger.debug("-------- activeCampaingsListlbId.getItemCount()()---------"+activeCampaingsListlbId.getItemCount()); // remove it after testing
		if(extraSchStringList.size() == 0){
			
			    if(activeCampaingsListlbId.getItemCount() == 0){
			    	isCreditOrExipry   = createRowUpComingListBox(startDtCS,count,false); 
			    	 Row row = new Row();
					 row.setValue(startDtCS);
						
						if(schedGrdRowsId != null) {
							row.setParent(schedGrdRowsId);
						}
			    }else{
			    	Row row = new Row();
					 row.setValue(startDtCS);
						
						if(schedGrdRowsId != null) {
							row.setParent(schedGrdRowsId);
						}
			    }
			
		}else{
			sameSch = MyCalendar.calendarToString(startDtCS.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"));
			if(extraSchStringList.contains(sameSch)){
				
				
				
				if(!checkAlreadyThereWithinOneHr(smsCampScheduleList,startDtCS)){
					//isCreditOrExipry   = createRowUpComingListBox(startDtCS,count,false); 
					 Row row = new Row();
					 row.setValue(startDtCS);
						
						if(schedGrdRowsId != null) {
							row.setParent(schedGrdRowsId);
						}
				}
				
			}
		}
		
		SMSCampaignSchedule nextDtCS;
		Calendar nextDate = Calendar.getInstance();
		Calendar nextDateTemp;
		nextDate.setTime(startDtCal.getTime());
		
		nextDate.set(frequency, startDtCal.get(frequency)+step);
		
		*//** Generates the dates between the given dates and frequency **//*
		while(nextDate.compareTo(endDtCal) <= 0 ) {
			
			nextDateTemp = Calendar.getInstance();
			nextDateTemp.setTime(nextDate.getTime());
			
			nextDtCS = new SMSCampaignSchedule(nextDateTemp, smsCampaign.getSmsCampaignId(),
					(byte)0, (byte)0, currUser.getUserId());
			
			nextDtCS.setResendLevel((byte)0);
			
			
			if(extraSchStringList.size() == 0){
				
				if( !smsCampScheduleList.contains(nextDtCS) ) { //here 3
					
					if(!checkAlreadyThereWithinOneHr(smsCampScheduleList,nextDtCS)){
						csList.add(nextDtCS);
						smsCampScheduleList.add(nextDtCS);
						createRow(nextDtCS, schedGrdRowsId);
					}
					
					
					
				}
				
			}else{
				sameSch = MyCalendar.calendarToString(nextDtCS.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"));
				if(extraSchStringList.contains(sameSch)){
					
					if(!checkAlreadyThereWithinOneHr(smsCampScheduleList,nextDtCS)){
						csList.add(nextDtCS);
						smsCampScheduleList.add(nextDtCS);
						createRow(nextDtCS, schedGrdRowsId);
					}
					
					
				}
			}
			
			nextDate.set(frequency, nextDate.get(frequency)+step);
			
		}// while
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------before exiting---------");
		}
		logger.debug("size of list is====>"+csList.size());
		return csList;
		*/
	}//addDates

	
	
	private boolean checkAlreadyThereWithinOneHr(List<SMSCampaignSchedule> smsCampScheduleList,SMSCampaignSchedule toBeChekedSMSCampaignSchedule){
		try{
			
			for(SMSCampaignSchedule aSMSCampaignSchedule : smsCampScheduleList){
				logger.debug("-------- toBeChekedSMSCampaignSchedule.getScheduledDate())---------"+toBeChekedSMSCampaignSchedule.getScheduledDate());
				logger.debug("-------- aSMSCampaignSchedule.getScheduledDate())---------"+aSMSCampaignSchedule.getScheduledDate());
				
				if(toBeChekedSMSCampaignSchedule.getScheduledDate().getTimeInMillis() - aSMSCampaignSchedule.getScheduledDate().getTimeInMillis() >= 3600000 ){
					//i.e. check with others that new schedule is going to conflict within one hr rule.
					
				}else{ //'within one hr rule' rule violated
					return true;
				}
				
			}
			
			/*if(smsCampScheduleList.size() > 0){
				List<Calendar> calendarList = new ArrayList<Calendar>();
				for(SMSCampaignSchedule aSMSCampaignSchedule : smsCampScheduleList){

					calendarList.add(aSMSCampaignSchedule.getScheduledDate());
				}

				Collections.sort(calendarList);

				Calendar lastScheduleDate = calendarList.get(calendarList.size() - 1);

				if((toBeChekedSMSCampaignSchedule.getScheduledDate().getTimeInMillis() - lastScheduleDate.getTimeInMillis()) >= 3600000 ){
					return false;
				}else{
					return true;
				}
			}
			else{
				return false;
			}*/
		
			return false;
			//loop is over implies that we didn't find any existing schedule in smsCampScheduleList, which is conflicting with toBeChekedSMSCampaignSchedule.
			
			
		}catch(Exception e){
			logger.error("exception:::::::::::::::::"+e);
		}
		return true;
	}
	public void onClick$testSMSTbId() {
		
		if(testSMSTbId.getValue().equals("Enter Mobile Number(s)...")) {
			
			testSMSTbId.setValue("");
			
		}
		
	}
	
	public void onClick$linkURLTxtBoxId() {
		
		if(linkURLTxtBoxId.getValue().equals("Use Url Shortener")) {
			
			linkURLTxtBoxId.setValue("");
			
		}
		
	}
	
	
	
	
	
	public void onClick$mblNoTxtBoxId() {
		
		if(mblNoTxtBoxId.getValue().equals("Enter Mobile Number(s)...")) {
			
			mblNoTxtBoxId.setValue("");
			
		}
		
	}
	
	
	/**
	 * prepares the view for schedules
	 * @param cal
	 */
	private void addDateToGrid(Calendar cal) {
		logger.debug(">>>>>>> Started SmsCampSettingsController :: addDateToGrid <<<<<<< ");
		//STARTS 14, changed to make similar like email campaign schedules
		Calendar currCal = Calendar.getInstance();
		//currCal.setTimeZone((TimeZone) sessionScope.get("clientTimeZone"));
		//ENDS 14, changed to make similar like email campaign schedules
		
		//STARTS 15, changed to make similar like email campaign schedules
		//following was not commented earlier
		/*if(cal == null || cal.compareTo(Calendar.getInstance()) < 0){
			schErrorLblId.setValue("Please select a future date and time");
			return;
		}*/
		//ENDS 15, changed to make similar like email campaign schedules
		
		if(cal == null || cal.compareTo(currCal) < 0){
			schErrorLblId.setValue("Please select a future date and time");
			return;
		}

		SMSCampaignSchedule smsCampSchedule = addDateCheck(cal, null,(byte)0);

		if(smsCampSchedule == null) {
			schErrorLblId.setValue("Date already sheduled");
			return;
		}
		
		//createRow(smsCampSchedule, schedGrdRowsId);
		
		int confirm = 0;
		boolean isSameDay = false;

		List<String> dates = new ArrayList<String>();

		for (SMSCampaignSchedule campSched : smsCampScheduleList) {

			/*String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"));
			String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")); 

			if( now.equals(sameSch) && (campSched.getStatus() == 0 
					||  campSched.getStatus() == 2 )){
				dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
			}*/
			
			String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
			String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")); 
			
			if( now.equals(sameSch) && (campSched.getStatus() == 0 
					||  campSched.getStatus() == 2 )){
				dates.add(" "+MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_TIME,(TimeZone) sessionScope.get("clientTimeZone")));
			}
		}

		for (SMSCampaignSchedule campSched : smsCampScheduleList) {

			/*String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"));
			String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")); 

			if( now.equals(sameSch) && (campSched.getStatus() == 0 
					|| campSched.getStatus() == 2 )){

				confirm = Messagebox.show("Schedule(s) already exist on "+ MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"))
						+ " for following time:"
						+"\n "+StringUtils.collectionToCommaDelimitedString(dates)+" \n Do you want to continue & add more?", 
						"Same Day Schedule Found", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);


				isSameDay = true; 
				break;
			}*/	
			
			String now = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone"));
			String sameSch = MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_DATEONLY,(TimeZone) sessionScope.get("clientTimeZone")); 
			
			if( now.equals(sameSch) && (campSched.getStatus() == 0 
					 || campSched.getStatus() == 2 )){
				
				confirm = Messagebox.show("Schedule(s) already exist on "+ MyCalendar.calendarToString(campSched.getScheduledDate(), MyCalendar.FORMAT_MDATEONLY,(TimeZone) sessionScope.get("clientTimeZone"))
						+ " for following time:"
						+"\n "+StringUtils.collectionToCommaDelimitedString(dates)+" \n Do you want to continue & add more?", 
						"Same Day Schedule Found", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				
				
				isSameDay = true; 
				break;
			}
			
		}

		if(confirm == 2){
			logger.debug("Message box cancel is clicked.");
			return;
		}

		if(!isSameDay ||(confirm == 1 && isSameDay)) {

			SMSCampaignSchedule campSchedule = addDate(cal, null,(byte)0);

			if(campSchedule == null) {
				//schErrorLblId.setValue("Date already scheduled");
				MessageUtil.setMessage("Date already scheduled.", "color:red", "TOP");
				return;
			}

			//createRow(campSchedule, schedGrdRowsId,false);
			//TODO Create ListBox First element
			int count = activeCampaingsListlbId.getItemCount();
			boolean isCreditOrExipry = createRowUpComingListBox(campSchedule,count,false);
			if(isCreditOrExipry == false){
				logger.error("Your Credit Limits are Exipred please contact support");
				return;
			}
			//newlyAddedSchedule.add(campSchedule);
			persistentCamp = false;
			createDivUpComingCampaigns(true,campSchedule);
			createRow(campSchedule, schedGrdRowsId);
			MessageUtil.setMessage("You have added 1 schedule to be sent on "+MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"))+".\n All your active schedules can be viewed by clicking on \n \'View All Upcoming Schedules\' link.", "color:blue");
		}
		tempList.add(cal);
		logger.debug(">>>>>>> Completed SmsCampSettingsController :: addDateToGrid <<<<<<< ");
	}//addDateToGrid
	
	
	
	
	/**
	 * creates the smsCampaignSchedule object with the specified date 
	 * if with the same date no any schedule is existed.
	 * @param selectedDtCal
	 * @param parentId
	 * @param criteria
	 * @return SMSCampaignSchedule object
	 */
	private SMSCampaignSchedule addDate( Calendar selectedDtCal, 
						 SMSCampaignSchedule parentCampSchedule,  byte criteria) {	
		
		logger.debug("-------- just entered---------");
		//STARTS 16, changes to make similar with email camoaign schedules
		//initially following block was commented.
		selectedDtCal.set(Calendar.SECOND,0);
		selectedDtCal.set(Calendar.MILLISECOND,0);
		//ENDS 16, changes to make similar with email camoaign schedules
		
	    Calendar tempCal = Calendar.getInstance();
	    //STARTS 17, changes to make similar with email camoaign schedules
		//tempCal.setTimeZone((TimeZone) sessionScope.get("clientTimeZone"));
		//ENDS 17, changes to make similar with email camoaign schedules
		tempCal.setTime(selectedDtCal.getTime());
		
		Long smsCsId = smsCampaignScheduleDao.getCurrentId();
		
		if(logger.isDebugEnabled()) {
			logger.debug(">>>>>>>>> Generated smsCsId :"+smsCsId);
		}
		
		/*if(logger.isDebugEnabled()) {
			logger.debug(">>>>>>>>> Generated Id :"+parentCampSchedule.getSmsCsId());
		}*/
		//STARTS 18, changes to make similar with email camoaign schedules
		//Following was not commented earlier, in place of that, next line to create new smsCampSchedule is added
		SMSCampaignSchedule smsCampSchedule = new SMSCampaignSchedule(tempCal,criteria, currUser.getUserId());
		//SMSCampaignSchedule smsCampSchedule = new SMSCampaignSchedule(smsCsId,tempCal, criteria);
		//ENDS 18, changes to make similar with email camoaign schedules
		
		if(parentCampSchedule != null) { // if parent is not exists
			
			if(logger.isDebugEnabled()) {
				logger.debug(">>>>>> parent Id :"+parentCampSchedule.getSmsCsId()+" "+parentCampSchedule.getResendLevel());
			}
			
			
			//STARTS 19, changes to make similar with email camoaign schedules
			//Following was not commented earlier, in place of that, next line is added.
			//smsCampSchedule.setParentId(null);
			smsCampSchedule.setParentId(parentCampSchedule.getSmsCsId());
			//ENDS 19, changes to make similar with email camoaign schedules
			
			
			
			smsCampSchedule.setResendLevel((byte)(parentCampSchedule.getResendLevel()+1));
		} 
		else {
			smsCampSchedule.setResendLevel((byte)0);
		}// else if parentCampSchedule
		
		smsCampSchedule.setSmsCampaignId(smsCampaign.getSmsCampaignId());
		if(smsCampScheduleList.contains(smsCampSchedule)) {
			return null;
		}
		//logger.info("smsCampaign is====>"+smsCampaign+"");
		//TODO 
		
		
		
		smsCampSchedule.setStatus((byte)0);
		//STARTS 20, changes to make similar with email campaign schedules
		smsCampSchedule.setUserId(currUser.getUserId());
		//STARTS 20, changes to make similar with email campaign schedules
		//smsCampaignScheduleDao.saveOrUpdate(smsCampSchedule);
		//smsCampSchedule.setSmsCampaignId(null);
		smsCampScheduleList.add(smsCampSchedule);
		logger.info("Added one Schedule");
		
		
		if(logger.isDebugEnabled()) {
			logger.debug("-------- before returning---------");
		}
		return smsCampSchedule;
		
	}//addDate
	
	/**
	 * create a row for each schedule of SMS campaign and sets the 
	 * SMSCampaignSchedule object to the created row 
	 * @param smsCampaignSchedule 
	 * @param rows
	 */
	private void createRow( SMSCampaignSchedule smsCampaignSchedule,  Rows rows) {

		if (logger.isDebugEnabled()) {
			logger.debug("------------- just entered----------"+smsCampaignSchedule.getResendLevel());
		}

		int available = 0;
		try {
			SubscriptionDetails subDetails = new SubscriptionDetails();
			available = subDetails.getSMSStatus(smsCampaignSchedule.getScheduledDate());
		} catch (Exception e1) {
			logger.error("** Exception : Problem while getting the subscription details", e1);
			return;
		}
		
		if(available <= 0) {
			MessageUtil.setMessage("SMS cannot be scheduled as you have " +
					"reached your SMS credits limit or your account has expired.", 
					"color:red", "TOP");
			return;
		}
		
		//Create Row
		
		
		try {
			 Row row = new Row();
			row.setValue(smsCampaignSchedule);
			
			if(rows != null) {
				row.setParent(rows);
			}
			else {
				rowMap.put(smsCampaignSchedule.getScheduledDate(), row);
			}
			
			if(smsCampaignSchedule.getResendLevel() < MAX_RESEND_LEVEL) {
				Detail detail = new Detail();
				detail.setOpen(false);
				detail.setParent(row);	
				detail.setStyle("display:none;");
				detail.addEventListener("onOpen", this);
				
				Grid grid = new Grid();
				Columns cols = new Columns();
				cols.setParent(grid);
				grid.setVisible(false);
				grid.setParent(detail);
				int padding = 3;
				if((smsCampaignSchedule.getResendLevel()+1) < MAX_RESEND_LEVEL){
					Column col = new Column();
					col.setWidth("3%");
					col.setParent(cols);
					padding = 0;
				}
				Column col = new Column("Date");
				col.setWidth((27 + padding) + "%");
				col.setParent(cols);
	
				col = new Column("Status");
				col.setWidth("18%");
				col.setParent(cols);
				
				col = new Column("Resend criteria");
				col.setWidth("17%");
				col.setParent(cols);
	
				col = new Column("Actions");
				col.setWidth("35%");
				col.setParent(cols);
				
				Rows tempRows = new org.zkoss.zul.Rows();
				tempRows.setParent(grid);
				if(smsCampaignSchedule.getSmsCsId() == null){
					
				}
				//logger.debug("rows map is===>"+rowsMap);
				rowsMap.put(smsCampaignSchedule.getScheduledDate(), tempRows);
			
			}// if resend level

			Label tempLabel = new Label(smsCampaignSchedule.getDateStrByTimeZone(null,
									(TimeZone) session.getAttribute("clientTimeZone")));
			tempLabel.setParent(row);
			
			tempLabel = new Label(smsCampaignSchedule.getStatusStr());
			tempLabel.setParent(row);

			
			/**
			 * criteria will be 0 for root schedules for
			 * re send schedules only criteria will be > 0
			 *
			 **/
			
			if(smsCampaignSchedule.getCriteria() > 0) {
				tempLabel = new Label();
				if (smsCampaignSchedule.getCriteria() == 1) {
					tempLabel.setValue("Not opens");
				}
				else if (smsCampaignSchedule.getCriteria() == 2) {
					tempLabel.setValue("Not clicked");
				}
				tempLabel.setParent(row);
				
			}// if campaignSchedule.getParent()
			
			Hbox hbox = new Hbox();
			hbox.setParent(row);
			
			Toolbarbutton tbButton;
				
			/*if(smsCampaignSchedule.getResendLevel() < MAX_RESEND_LEVEL) {
				tbButton = new Toolbarbutton("Add Resend");
				tbButton.setImage("/img/icons/add_icon.png");
				tbButton.setTooltiptext("Resend the email after some days");
				tbButton.setAttribute(TB_ACTION, TB_ACTION_RESEND);
				tbButton.addEventListener("onClick",myListener);
				tbButton.setParent(hbox);
			}

			if(smsCampaignSchedule.getResendLevel()>0){
				tbButton = new Toolbarbutton("Edit content");
				tbButton.setTooltiptext("Use different content for this schedule");
				tbButton.setImage("/img/icons/small_edit_icon.png");
				tbButton.setAttribute(TB_ACTION, TB_ACTION_EDIT);
				tbButton.addEventListener("onClick", myListener);
				
				tbButton.setParent(hbox);
			}*/

			/** * Delete toolbar button** */
			tbButton = new Toolbarbutton("Delete");
			tbButton.setTooltiptext("Delete from schedule");
			tbButton.setImage("/img/action_delete.gif");
			tbButton.setAttribute(TB_ACTION, SmsCampSettingsController.TB_ACTION_DELETE);
			tbButton.addEventListener("onClick",myListener);
			
			tbButton.setParent(hbox);

			if(rows != null) {
				rows.invalidate();
			}
			
		} catch ( Exception e) {
			
			logger.error("** Exception : while creating a row ", e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("------------- before exit----------");
		}

	}// createRow()
	
	
	
	private void setMlistLinks() {
		
		if(smsCampaign == null) return;
		
		if(smsCampaign.getListType().startsWith("Segment:"))
		{
			Components.removeAllChildren(listNamesDivId);
			String segment=smsCampaign.getListType();
			segment=segment.replace("Segment:", "");
			Hbox mlHbox = new Hbox();
			mlHbox.setSpacing("10px");
			List<SegmentRules> segmentRules=segmentRulesDao.findById(segment);
			A mlLink;
			if(segmentRules!=null){
			for(SegmentRules segmentRule:segmentRules)
			{
				mlLink = new A(segmentRule.getSegRuleName());
				mlLink.setAttribute(ATTRIBUTE_SOURCE, segmentRule);
				mlLink.addEventListener("onClick", new MyListener());
				mlLink.setParent(mlHbox);
				
			}
			}
			mlHbox.setParent(listNamesDivId);
			recipentsSourceLblId.setValue("Selected Segments(s):");
		}
		else	
		{
		Components.removeAllChildren(listNamesDivId);
		Hbox mlHbox = new Hbox();
		mlHbox.setSpacing("10px");
		Set<MailingList> mlset = smsCampaign.getMailingLists();
		A mlLink;
		for (MailingList mailingList : mlset) {
			mlLink = new A(mailingList.getListName());
			mlLink.setAttribute(ATTRIBUTE_SOURCE, mailingList);
			mlLink.addEventListener("onClick", new MyListener());
			mlLink.setParent(mlHbox);
		}
		mlHbox.setParent(listNamesDivId);
		recipentsSourceLblId.setValue("Selected Contact List(s):");
	}
	}
	public void onClick$msgPreviewImgId() {
		
		
		
		
		if(smsCampaign != null) {
			
		
			previewWin$html.setContent(smsCampaign.getMessageContent());
			previewWin.setVisible(true);
			
		}
		
		
	}
	
	
	public void onClick$editMsgImgId() throws Exception {
		isEdit = "edit";

		saveAsDraftStep1BtnId.setVisible(false);
		//saveAsDraftBtnIdAtTop.setVisible(false);
		gotoStep2BtnId.setLabel("Save");
		//saveBtnIdAtTop.setLabel("Save");
		step1BackBtnId.setVisible(true);
		Clients.evalJavaScript("changeStep(1, true);");		
		SMSMsgTbId.setFocus(true);
		
	}
	
	
	public void onClick$editSmsListImgId() throws Exception {
		isEdit = "edit";
		saveAsDraftStep2BtnId.setVisible(false);
		gotoStep3BtnId.setLabel("Save");
		Clients.evalJavaScript("changeStep(2, true);");		
		
	}
	
	
	public void onCheck$sendPeriodicallyId() throws Exception {
		frqDivId.setVisible(!frqDivId.isVisible());
		chooseDateTimeImgId.setVisible(false);
		//frqDtDivId.setVisible(!frqDtDivId.isVisible());
		prtDtDivId.setVisible(!prtDtDivId.isVisible());
	}//onCheck$sendPeriodicallyId()
	
	public void onCheck$prtDateRadioId() throws Exception {
		
		chooseDateTimeImgId.setVisible(true);
		frqDivId.setVisible(!frqDivId.isVisible());
		//frqDtDivId.setVisible(!frqDtDivId.isVisible());
		prtDtDivId.setVisible(!prtDtDivId.isVisible());
		
	}//onCheck$prtDateRadioId()
	
	
	
	
	public void onClick$step1BackBtnId() {
		
		if(isEdit!=null){
			Clients.evalJavaScript("changeStep(3,true)");
		}else{
			Redirect.goTo(PageListEnum.RM_HOME);
		}
	}
	
	public void onClick$backStep1ButtonId() {
		
		if(isEdit!=null){
			if(isEdit.equalsIgnoreCase("edit"))
				Clients.evalJavaScript("changeStep(3,true)");
			else if(isEdit.equalsIgnoreCase("view")){
				/*session.removeAttribute("editSmsCampaign");
				session.removeAttribute("smsDraftStatus");*/
				//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_SMS_CAMPAIGNS);
				Redirect.goTo(PageListEnum.CAMPAIGN_SMSCAMPAIGN_LIST);
			}
		}else{
			Clients.evalJavaScript("changeStep(1,true)");
		}
		
	}
	
	
	/**
	 * saves the SMS campaign to DB which Should not have Active schedules
	 */
	
	public void onClick$saveAsDraftBtnId() {
		
		int rowCount = 0;
		Row row = null;
		
		//saveCampaign(true);
		/**
		 * if atleast one schedule is there with status 'scheduled',ignore that schedule
		 * and save the SMS campaign but not SMSCampaignSchedule(with status as 'draft')(unlike we done in Campaigns... )
		 */
		for(SMSCampaignSchedule smsCampaignSchedule : smsCampScheduleList) {
			
			if(smsCampaignSchedule.getStatus() == 0){
				rowCount++;
			}
			
		}//for
		if(rowCount != 0) {
			//MessageUtil.setMessage(" A campaign with active schedules cannot be saved as draft. Please delete all active schedules 				first.", "color:red");
			MessageUtil.setMessage("A campaign with upcoming schedules \n cannot be saved as draft."
					+ "\n Please delete all active schedules first.", "color:red");
			return;
		}

		/*if(rowCount>0){
			try {
				int confirm = Messagebox.show("Active schedules will be ignored. Do you want to continue?", "Save As Draft ?", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm != 1){
					MessageUtil.clearMessage();
					return ;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}
		}//if
*/		
		//save the SMS campaign
		if(session!=null || currUser != null){
			smsCampaign.setStatus(Constants.CAMP_STATUS_DRAFT);
			smsCampaign.setDraftStatus(Constants.SMS_CAMP_DRAFT_STATUS_STEP_COMPLETE);
			//smsCampaignsDao.saveOrUpdate(smsCampaign);
			smsCampaignsDaoForDML.saveOrUpdate(smsCampaign);
			for(SMSCampaignSchedule campaignSchedule : smsCampScheduleList){
				if(campaignSchedule.getStatus() == 0 || campaignSchedule.getStatus() == 2){
				campaignSchedule.setStatus((byte)2);
				}
				else{
					continue;
				}
			}
			smsCampaignScheduleDaoForDML.saveByCollection(smsCampScheduleList);
			MessageUtil.setMessage("SMS campaign is saved successfully.", "color:green;", "top");
			
			//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_SMS_CAMPAIGNS);
			Redirect.goTo(PageListEnum.CAMPAIGN_SMSCAMPAIGN_LIST);
			//Utility.getXcontents().invalidate();
			
		}
		else{
			MessageUtil.setMessage("Problem encountered while saving. Please re-login and try again.", "color:red", "TOP");
			logger.error("** Exception : session is null ** ");
		}//else
		
	}//onClick$saveAsDraftBtnId
	
	public void onClick$saveAsDraftStep2BtnId() throws Exception {
		
		if(configurelistRgId.getSelectedIndex() == 0) {
			
			if(saveMlist()==false)
			Redirect.goTo(PageListEnum.CAMPAIGN_SMSCAMPAIGN_LIST);	
			//return;
		}
		else if(configurelistRgId.getSelectedIndex() == 1) {
			
			if(saveSegRule() == false) return;
			
		}
		
		
		
		//saveMlist();//modified aftyer POS
		//Redirect.goTo(PageListEnum.CAMPAIGN_VIEW_SMS_CAMPAIGNS);
		Redirect.goTo(PageListEnum.CAMPAIGN_SMSCAMPAIGN_LIST);
	}
	
	public String getDraftStatus(Div currDiv) {
		
		
		String draftStatus = null;
		if(currDiv.getId().equals(step1DivId.getId())) {
			
			draftStatus = Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO;
			
		}
		else if(currDiv.getId().equals(step2DivId.getId())) {
			
			draftStatus = Constants.SMS_CAMP_DRAFT_STATUS_STEP_THREE;
			
		}else {
			
			draftStatus = Constants.SMS_CAMP_DRAFT_STATUS_STEP_THREE;
		}
		
		return draftStatus;
		
	}//getDraftStatus
	
	
	
	public boolean saveMlist() {
		
		int num = dispMlListsLBoxId.getItemCount();
		if (num == 0) {
			MessageUtil.setMessage(
					"Please create a contact list to send" +
					" your campaigns to it.","color:red", "TOP");
			return false;
		}
		int mlcount = dispMlListsLBoxId.getSelectedIndex();
		if (mlcount == -1) {
			MessageUtil.setMessage("Select at least one list.", "color:red","TOP");
			return false;
		}
		
		Set lists = dispMlListsLBoxId.getSelectedItems();
		logger.debug(" No of Mailing lists selected :"+lists.size());
		
		
		Set mlSet = new HashSet();
		Listitem li;
		MailingList ml = null;
		
		String listIdsStr = "";
		/*long emailCount = 0;
		long totalCount = 0;
		long unpurgedCount = 0;*/
		
		for (Object obj : lists) {
			li = (Listitem) obj;
			ml = (MailingList) li.getValue();
			
			if(listIdsStr.length() != 0) { 
				listIdsStr+=",";
			}	
			listIdsStr += ml.getListId();
			mlSet.add(ml);
		}
		
		smsCampaign.setListType("Total");
		smsCampaign.setMailingLists(mlSet);
		
		if(!otherCampSettings()) {
			return false;
		}
		
		
		return true;
		
	}
	
	private String listnamesStr;
	public boolean saveSegRule() {
		
		int num = dispsegmentsLbId.getItemCount();
		if(num == 0) {
			MessageUtil.setMessage(
					"Please create a segment first \n so that you can configure " +
					"it to your campaigns.","color:red", "TOP");
			return false;
			
		}
		
		int mlcount = dispsegmentsLbId.getSelectedCount();
		if (mlcount == 0) {
			MessageUtil.setMessage("Please select at least one segment.", "color:red","TOP");
			return false;
		}
		
		
		Set<Listitem> selRules = dispsegmentsLbId.getSelectedItems();
		String segRuleIds= "";
		String listIdsStr = "";
		for (Listitem listitem : selRules) {
			
			SegmentRules segmentRule = (SegmentRules)listitem.getValue();

			
			if(segRuleIds.length() > 0) {
				
				segRuleIds += ",";
				
			}//if
			segRuleIds += segmentRule.getSegRuleId().longValue();
			
			if(listIdsStr.length() > 0) listIdsStr+= ",";
			listIdsStr += segmentRule.getSegmentMlistIdsStr();
			
		}//for
		
		
		smsCampaign.setListType("Segment"+Constants.DELIMETER_COLON+segRuleIds);
		
		//can avoid setting the set of mailing lists to the campaign
		Set<MailingList> mlSet = new HashSet<MailingList>();
		
		listnamesStr = "";
		
		
		List<MailingList> mlList = mailingListDao.findByIds(listIdsStr);
		
		if(mlList == null) {
			
			MessageUtil.setMessage("Configured segment's target list no longer exists. You might have deleted it.", "color:red;");
			configurelistRgId.setSelectedIndex(1);
			onCheck$configurelistRgId();
			return false;
			
			
		}
		
		for (MailingList mailingList : mlList) {
			
			mlSet.add(mailingList);
			if(listnamesStr == null) listnamesStr = mailingList.getListName();
			
			if(listnamesStr != null && listnamesStr.length() > 0) listnamesStr += ", ";
			listnamesStr += mailingList.getListName();
			
		}//for
		
		
		//mlSet.add(segmentRule.getMailingList());
		
		smsCampaign.setMailingLists(mlSet);
		
		if(!otherCampSettings()) {
			return false;
		}
		
		return true;
		
		
	}
	
	public boolean otherCampSettings() {
		
		long smsCount = 0;
		long totalCount = 0;
		//long unpurgedCount = 0;
		Set<MailingList> mlSet = smsCampaign.getMailingLists();
		totalCount = contactsDao.getAllMobileCount(mlSet, smsCampaign.isEnableEntireList());
		
		
		//campaign.setCouponFlag(false);
		MessageUtil.clearMessage();
		String segmentStr = smsCampaign.getListType();
		
		if(segmentStr.startsWith("Segment")) {
			
			String segRuleIds = segmentStr.split(""+Constants.DELIMETER_COLON)[1];
			List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);
			
			if(segmenRules == null) {
				
				MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
				configurelistRgId.setSelectedIndex(1);
				onCheck$configurelistRgId();
				return false;
				
				
			}//if
			String tempQry = "";
			logger.debug("size ::"+segmenRules.size());
			for (SegmentRules segmentRules : segmenRules) {
				
				Set<MailingList> mlistSet = new HashSet<MailingList>();
				List<MailingList> mlList = mailingListDao.findByIds(segmentRules.getSegmentMlistIdsStr());
				if(mlList == null) {
					logger.debug("continue");
					continue;
				}
				
				mlistSet.addAll(mlList);
				long mlsbit = Utility.getMlsBit(mlistSet);
				//ClickHouse changes
				if(!currUser.isEnableClickHouseDBFlag())
					segmentStr = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE, mlsbit);
				else
					segmentStr = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE, mlsbit);

				if(segmentStr == null) {
					MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
					continue;
				}
				if(SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRules.getSegRule())) {
					String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRules.getSegRule());
					
					if(csCampIds != null ) {
						String crIDs = Constants.STRING_NILL;
						//CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
						List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRules.getUserId(), csCampIds);
						if(campList != null) {
							for (Object[] crArr : campList) {
								
								if(!crIDs.isEmpty()) crIDs += Constants.DELIMETER_COMMA;
								crIDs += ((Long)crArr[0]).longValue();
								
							}
						}
						
						segmentStr = segmentStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
					}
				}
				
				
				segmentStr = segmentStr.replace("<MOBILEOPTIN>", !(smsCampaign.isEnableEntireList()) ? " AND c.mobile_opt_in=1" : "");
				logger.info(" Generated Query :"+segmentStr);
				
				if(tempQry.length() > 0) tempQry += " UNION ";
				
				tempQry += segmentStr;
//				contactsDao.insertSegmentedContacts(tempQry);
				
				
				
				
			}//for
			logger.debug(tempQry);
			if(!currUser.isEnableClickHouseDBFlag())
				smsCount = contactsDao.getSegmentedContactsCount(tempQry);
			else
				smsCount = contactsDao.getSegmentedContactsCountFromCH(tempQry);
			
			/*if(unpurgedCount > 0) {
				Messagebox.show("Selected mailing list(s) have "+ unpurgedCount +" unpurged contacts.","Info", Messagebox.OK, Messagebox.INFORMATION);
			}		 
			*/
			/*if(smsCount == 0) {
				MessageUtil.setMessage("Your segment returned 0 unique contacts of "+ totalCount + " available contacts." , "color:red", "TOP");
				return false;
			}*/
			if(smsCount == 0) {
				logger.info("Segment returned 0 unique contacts of "+ totalCount + " available contacts.");
			}
		
		}//if segment
		else {
			//smsCount = contactsDao.getUniqueSMSCount(ml_ids_str);
//			totalCount = contactsDao.getAllMobileCount(mlSet, smsCampaign.isEnableEntireList());
			smsCount = totalCount;
			if(totalCount == 0) {
				MessageUtil.setMessage("Your selection returned 0 unique contacts of available contacts.", "color:red", "TOP");
				return false;
			}
		}
		
		/*if(segmentStr.startsWith("Segment")) {
			
			segmentStr = ( (SegmentRules)dispsegmentsLbId.getSelectedItem().getValue()).getSegRule();
			
			
		}
		
		boolean isSegment = (segmentStr == null ?false:(segmentStr.equals("Total")?false:true));
		
		String ml_ids_str ="";
		Set<MailingList> mlSet = smsCampaign.getMailingLists();
		for(MailingList mailingList : mlSet){
			
			if(ml_ids_str.length()!=0) 	ml_ids_str +=  ",";
			ml_ids_str +=  mailingList.getListId();
		}
		
		long mlsbit = Utility.getMlsBit(mlSet);
		//unpurgedCount = contactsDao.getAllUnpurgedCount(ml_ids_str);
		totalCount = contactsDao.getAllMobileCount(mlSet, smsCampaign.isEnableEntireList());
		if(isSegment) {

			logger.debug("segment rule::"+segmentStr);
			
			segmentStr = SalesQueryGenerator.generateSMSListSegmentQuery(segmentStr,mlsbit);
			if(segmentStr == null) {
				MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
				return false;
			}
			
			
			segmentStr = segmentStr.replace("<MOBILEOPTIN>", !(smsCampaign.isEnableEntireList()) ? " AND c.mobile_opt_in=1" : "");
			logger.info(" Generated Query :"+segmentStr);
			//if(smsCampaign.isEnableEntireList())
			smsCount = contactsDao.getSegmentedContactsCount(segmentStr);
			
			if(unpurgedCount > 0) {
				Messagebox.show("Selected mailing list(s) have "+ unpurgedCount +" unpurged contacts.","Info", Messagebox.OK, Messagebox.INFORMATION);
			}		 
			
			if(smsCount == 0) {
				MessageUtil.setMessage("Your segment returned 0 unique contacts of "+ totalCount + " available contacts." , "color:red", "TOP");
				return false;
			}
		}
		else {
			//smsCount = contactsDao.getUniqueSMSCount(ml_ids_str);
//			totalCount = contactsDao.getAllMobileCount(mlSet, smsCampaign.isEnableEntireList());
			smsCount = totalCount;
			if(totalCount == 0) {
				MessageUtil.setMessage("Your selection returned 0 unique contacts of available contacts.", "color:red", "TOP");
				return false;
			}
		}*/
		
		// Check if user email count is sufficient to send campaign
		UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		ArrayList<String> msgContentLst = null;
		SubscriptionDetails subDetails = new SubscriptionDetails();
		int userAvailableSMSCount = subDetails.getSMSStatus(Calendar.getInstance());
		if(usersDao != null) {
			
			//SubscriptionDetails subDetails = new SubscriptionDetails();
			//int userAvailableSMSCount = subDetails.getSMSStatus(Calendar.getInstance());
			
			String msgContent=smsCampaign.getMessageContent();
			if(SMSStatusCodes.optOutFooterMap.get(currUser.getCountryType())) {
				
				if(smsSettings != null) {
					
						
					SMSSettings optinSettings = null;
			  		SMSSettings optOutSettings = null;
			  		SMSSettings helpSettings = null;
			  		
			  		String messageHeader = Constants.STRING_NILL;
			  		for (SMSSettings eachSMSSetting : smsSettings) {
			  			
			  			if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
			  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
			  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
			  			
			  		}
					if(optinSettings != null && messageHeader.isEmpty()) messageHeader = optinSettings.getMessageHeader();
					else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
					else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
					
					msgContent= messageHeader != null ? messageHeader+"\n"+msgContent : msgContent;
					//if(entireListRId.isChecked() && optOutSettings != null)
					if(!msgTypeRgId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)
						&& entireListRId.isChecked() && optOutSettings != null){
						if(!currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {
						msgContent = msgContent+ (optOutSettings.getKeyword() != null ?
								("\n" + "Reply " + optOutSettings.getKeyword() + " 2 Optout")
										: "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
						}else {
							msgContent = msgContent+ (optOutSettings.getKeyword() != null ?
									("\n"+optOutSettings.getKeyword())
											: "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
						}
					}
				}
			}
			if (msgContent != null && msgContent.contains("|^")) {

				msgContentLst = splitSMSMessage(msgContent, 160);

			} else {

				msgContentLst = splitSMSMessage(msgContent, 170);
			}
			
			//logger.debug("User available limit is :"+userAvailableEmlCount + " UserId:"+userId);
			if(userAvailableSMSCount == -1) {
				
				 MessageUtil.setMessage("Your account validity period has expired. Please renew your subscription to continue.", "color:red", "TOP");
			 	 return false;
			}
			if(msgContentLst.size()>1){
				if(msgContentLst!=null && smsCount > (userAvailableSMSCount * msgContentLst.size())) {
					 MessageUtil.setMessage("Insufficient credits: You have "+userAvailableSMSCount+" SMS credits while the campaign needs "+(smsCount * msgContentLst
								.size())+" credits \n"
										+ "as the character count in the SMS exceeds 160 characters and \n"
										+ "Total recipients count is "+smsCount+".", "color:red", "TOP");
					 return false;
				}
			}else if(msgContentLst.size()==1){ 
				if(msgContentLst!=null && smsCount > (userAvailableSMSCount * msgContentLst.size())) {
					 MessageUtil.setMessage("Insufficient credits: You have "+userAvailableSMSCount+" SMS credits while the campaign needs "+(smsCount * msgContentLst
								.size())+" credits. \n"
										+ "Total recipients count is "+smsCount+".", "color:red", "TOP");
					 return false;
				}
			}
			else if(smsCount > userAvailableSMSCount) {
				 MessageUtil.setMessage("Configured to " + smsCount + " mobile numbers. Your available SMS credits limit is " + userAvailableSMSCount+ ".\n " +
					 		"Please contact support@optculture.com to request additional credits.", "color:red", "TOP");
				 return false;
			}
		} else {
			logger.error("** Exception : UsersDao is null. Could not perform user email Count validation. **");
			return false;
		}
		
		if(isEdit!=null) {
			if(isEdit.equalsIgnoreCase("view")) {
				smsCampaign.setStatus("Draft");
				//campaign.setDraftStatus("CampLayout");
				
				
				if(draftStatus != null && draftStatus.equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO)){
					smsCampaign.setDraftStatus(Constants.SMS_CAMP_DRAFT_STATUS_STEP_THREE);
				}
				 //
				
			}
			else if(isEdit.equalsIgnoreCase("edit")){
				smsCampaign.setModifiedDate(Calendar.getInstance());
			}
		}
		else {
			smsCampaign.setStatus("Draft");
			// campaign.setDraftStatus("CampLayout");
			
			if(draftStatus != null && draftStatus.equals(Constants.SMS_CAMP_DRAFT_STATUS_STEP_TWO)) {
				smsCampaign.setDraftStatus(Constants.SMS_CAMP_DRAFT_STATUS_STEP_THREE);
			}

		}
		
		if(msgContentLst.size()==1){
			if(userAvailableSMSCount < (smsCount * msgContentLst.size())){
	    		MessageUtil.setMessage("You have "+userAvailableSMSCount+" SMS credit(s).\n"
	    				+ "This campaign might use "+(smsCount * msgContentLst.size())+" credit(s) after replacing placeholders(if any).\n"
					+"Total recipients count is "+smsCount+".", "color:red");
	    		return false;
		    	}else{
		    		MessageUtil.setMessage("You have "+userAvailableSMSCount+" SMS credit(s).\n"
		    				+ "This campaign might use "+(smsCount * msgContentLst.size())+" credit(s) after replacing placeholders(if any).\n"
						+"Total recipients count is "+smsCount+".", "color:blue");
			}
	    }else if(msgContentLst.size()>1){
	    	if(userAvailableSMSCount < (smsCount * msgContentLst.size())){
	    		MessageUtil.setMessage("You have "+userAvailableSMSCount+" SMS credit(s).\n"
	    				+ "This campaign uses "+(smsCount * msgContentLst.size())+" credit(s)\n"
						+"as the character count in the SMS exceeds 160 characters.\n"
						+ "Total recipients count is "+smsCount+".", "color:red");
	    		return false;
	    	}else{
			MessageUtil.setMessage("You have "+userAvailableSMSCount+" SMS credit(s).\n"
					+ "This campaign uses "+(smsCount * msgContentLst.size())+"credit(s)\n"
					+"as the character count in the SMS exceeds 160 characters.\n"
					+ "Total recipients count is "+smsCount+".", "color:blue");
	    	}
		}
		/*if(userAvailableSMSCount<=(smsCount * msgContentLst.size())){
			MessageUtil.setMessage("You have "+userAvailableSMSCount+" SMS credit(s).This campaign uses "+(smsCount * msgContentLst
					.size())+" credit(s) as the character count in the SMS exceeds 160 characters and total recipients count is "+smsCount+".", "color:red");
			return false;
		}else{
			MessageUtil.setMessage("You have "+userAvailableSMSCount+" SMS credit(s).This campaign uses "+(smsCount * msgContentLst
					.size())+" credit(s) and total recipients count is "+smsCount+".", "color:blue");
		}*/
			
		try {
			int confirm = Messagebox.show("Total "+smsCount+" unique contacts have been configured. Do you want to continue?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm != 1){
				return false;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("Exception ::",e1);
			return false;
		}
		//smsCampaignsDao.saveOrUpdate(smsCampaign);
		smsCampaignsDaoForDML.saveOrUpdate(smsCampaign);
		return true;
		
		
	}
	/*private void setMlistLinks() {
		//if(campaign == null) return;
		Hbox mlHbox = new Hbox();
		mlHbox.setSpacing("10px");
		//Set<MailingList> mlset = campaign.getMailingLists();
		A mlLink;
		for (MailingList mailingList : mlset) {
			mlLink = new A(mailingList.getListName());
			mlLink.setAttribute("mailingList", mailingList);
			//mlLink.addEventListener("onClick", new MyListener());
			mlLink.setParent(mlHbox);
		}
		mlHbox.setParent(listNamesDivId);
	}
	*/
	
	
	
	/*public void onClick$step1AId() {
		onClick$goToNextBtnId();
		
	}
	
	public void onClick$step2AId() {
		onClick$goToNextBtnId();
		
	}
	
	
	public void onClick$step3AId() {
		onClick$goToNextBtnId();
		
	}*/
	
	
	public Div getVisibleDiv() {
		
		Div div = null;
		for(Div viblediv : divList) {
			
			if(viblediv.isVisible()) {
			
			
				return viblediv;
			
			}//if
		
		}//for
		return null;
	}
	
	public A getCurrAnch() {
		//Div div = null;
		for(A currAnch : anchList) {
			
			if(currAnch.getSclass().equals("req_step_current")) {
			
			
				return currAnch;
			
			}//if
		
		}//for
		return null;
	}
	
	public void onSelect$dispsegmentsLbId() {
		
		Set<Listitem> selRules = dispsegmentsLbId.getSelectedItems();
		String segRuleIds= "";
		String listIdsStr = "";
		String dispSegRule = "";
		listnamesStr = "";
		
		if(selRules.size() == 0 ) {
			
			selRuleLblId.setValue(dispSegRule);
			selRuleListLblId.setValue(listnamesStr);
			return;
		}
		
		for (Listitem listitem : selRules) {
			
			SegmentRules segmentRule = (SegmentRules)listitem.getValue();

			
			if(listIdsStr.length() > 0) listIdsStr += ",";
			listIdsStr += segmentRule.getSegmentMlistIdsStr();
			
			String segRule = segmentRule.getSegRule();
			if(segRule != null) {
				
				if(dispSegRule.length() > 0) dispSegRule += "& \n";
				dispSegRule += dispRule(segRule);
				
			}
			
			
		}//for
		
		selRuleLblId.setValue(dispSegRule);
		List<MailingList> mlList = mailingListDao.findByIds(listIdsStr);
		
		if(mlList == null) {
			
			MessageUtil.setMessage("Configured segment's target list no longer exists. You might have deleted it.", "color:red;");
			configurelistRgId.setSelectedIndex(1);
			onCheck$configurelistRgId();
			return ;
			
			
		}
		
		for (MailingList mailingList : mlList) {
			
			if(listnamesStr == null) listnamesStr = mailingList.getListName();
			
			if(listnamesStr != null && listnamesStr.length() > 0) listnamesStr += ", ";
			listnamesStr += mailingList.getListName();
			
		}//for
		
		
		
		selRuleListLblId.setValue(listnamesStr);
		
		if(selRuleLblId.getValue().length() != 0) {
			dispRuleDivId.setVisible(true);
		}else{
			dispRuleDivId.setVisible(false);
		}
		
		/*SegmentRules segmentRules = (SegmentRules)dispsegmentsLbId.getSelectedItem().getValue();
		String segRule = segmentRules.getSegRule();
		String dispSegRule = null;
		
		if(segRule != null) {
			
			dispSegRule = dispRule(segRule);
			selRuleLblId.setValue(dispSegRule);
			
			listnamesStr = "";
			String listIdsStr = segmentRules.getSegmentMlistIdsStr();
			
			List<MailingList> mlList = mailingListDao.findByIds(listIdsStr);
			
			if(mlList == null) {
				
				MessageUtil.setMessage("Configured segment's target list is longer exists. You might have deleted it.", "color:red;");
				configurelistRgId.setSelectedIndex(1);
				onCheck$configurelistRgId();
				return ;
				
				
			}
			
			for (MailingList mailingList : mlList) {
				
				if(listnamesStr == null) listnamesStr = mailingList.getListName();
				
				if(listnamesStr != null && listnamesStr.length() > 0) listnamesStr += ", ";
				listnamesStr += mailingList.getListName();
				
			}//for
			
			
			selRuleListLblId.setValue(listnamesStr);
			
			
			
		}//if
		
		if(selRuleListLblId.getValue().length() != 0) {
			dispRuleDivId.setVisible(true);
		}else{
			dispRuleDivId.setVisible(false);
		}*/
		
		
	}//onSelect$dispsegmentsLbId()
	

public String dispRule(String rule) {
	String dispRule = "";
	String option=null;
	String campaignId = null;
	String campName = "";
	if(rule != null) {
		
		
		String[] rowsArr = rule.split("\\|\\|");
		String[] columnsArr; 
		
		
		columnsArr = rowsArr[0].split(":");
		if(columnsArr.length > 0) {
			
			if(columnsArr[0].trim().equalsIgnoreCase("Any") ) { 
				option = "OR";
			} 
			else {
				option = "AND";
			}
			
			if(columnsArr.length > 2) {
				
				campaignId = columnsArr[2];
				if(campaignId != null && !campaignId.isEmpty()) {
					
					List<Campaigns> campLst = campaignsDao.getCampaignById((campaignId));
					if(campLst != null) { 
					for (Campaigns campaigns : campLst) {
						
						if(!campName.isEmpty()) campName += ", ";
						campName +=  (campaigns != null ? campaigns.getCampaignName() : "");
					}
					}
				}
				
				//StringTokenizer tokenizerr = new StringTokenizer(columnsArr, Constants.DELIMETER_COMMA);
				//numOfCampaigns = tokenizerr.countTokens();
			}//if
			
		}//if
		
		
		String[] tempStrArr = null;
		String fieldNameStr = null;
		String itemStr = null;
		String dataTypeStr = null;
		String constraintStr = null;
		String data1 = null;
		String data2 = null;
		String data = "";
		String[] tokenArr = null;
		
		for(int i=1;i<rowsArr.length;i++) {
			
			tokenArr = rowsArr[i].split("<OR>");
			String innerRule = "";
			for (String token : tokenArr) {
				
				columnsArr = token.split("\\|");
				if(innerRule.length()>0) innerRule += " "+"OR"+" ";
				
				itemStr = columnsArr[0].trim();
				fieldNameStr = columnsArr[1].trim();
				tempStrArr = columnsArr[2].trim().split(":");
				dataTypeStr = tempStrArr[0].toUpperCase().trim();
				constraintStr = tempStrArr[1];
				
				data = data1 = (columnsArr.length>3)?columnsArr[3]:"";
				
				
				logger.debug("fieldNameStr :"+fieldNameStr);
				logger.debug("dataTypeStr :"+dataTypeStr);
				logger.debug("constraintStr :"+constraintStr);
				logger.debug("data1 :"+data1);
				logger.debug("data2 :"+data2);
				
				/*if(fieldNameStr.trim().startsWith("cs.")) {
					
					if((columnsArr.length>5)) {
						campaignId = columnsArr[5].trim().length()>0 ? columnsArr[5] : null;
						data2 = columnsArr[4].trim().length()>0 ? columnsArr[4] : "";;
					}
					
					else if(columnsArr.length==5 ){
						
						campaignId = columnsArr[4].trim().length()>0 ? columnsArr[4] : null;
						
					}
					
					
					if(campaignId != null) {
						
						List<Campaigns> campLst = campaignsDao.getCampaignById((campaignId));
						if(campLst == null) continue;
						
						for (Campaigns campaigns : campLst) {
							
							if(!campName.isEmpty()) campName += ", ";
							campName +=  (campaigns != null ? campaigns.getCampaignName() : "");
						}
						
					}
					
					
					if(data2 != null && campName.trim().length()>0){
						data = data1+" , "+data2+ " IN Campaign: "+campName;
					}else if(data2 == null && campName.trim().length()>0) {
						data = data1+ " IN Campaign: "+campName;
					}
					
				}*/
				if(itemStr.equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem()) 
						|| itemStr.equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) ) {
					
					/*if((columnsArr.length>5)) {
						campaignId = columnsArr[5].trim().length()>0 ? columnsArr[5] : null;
						data2 = columnsArr[4].trim().length()>0 ? columnsArr[4] : "";;
					}
					
					else if(columnsArr.length==5 ){
						
						campaignId = columnsArr[4].trim().length()>0 ? columnsArr[4] : null;
						
					}*/
					
					SegmentEnum retEnum = SegmentEnum.getEnumByColumn(fieldNameStr);
					
					if(retEnum != null) {
						
						fieldNameStr = retEnum.getParentEnum().getDispLabel();
						
						constraintStr = retEnum.getDispLabel() +  " IN Campaign(s): "+campName;
						data = "";
					}//if
					
					
					
					/*if(data2 != null && campName.trim().length()>0){
						data = data1+" , "+data2+ " IN Campaign: "+campName;
					}else if(data2 == null && campName.trim().length()>0) {
						data = data1+ " IN Campaign: "+campName;
					}*/
					
				}
				else{
					
					data2 = (columnsArr.length>4)?columnsArr[4]:"";
					if(data2 != null ){
						data = data1+" , "+data2;
					}
				}
				
				
				innerRule += "("+fieldNameStr+" "+constraintStr+" "+data+")";
				
				
			}//for 
			
			
			if(dispRule.length()>0) dispRule += " "+option+" ";
			dispRule += "("+innerRule+")";
			
			
			
		} // outer for
		
		
	
	}
	
	
	
	return dispRule;
	
}



private Label selMlLblId, selRuleListLblId, selRuleLblId;
public void onSelect$dispMlListsLBoxId() throws Exception {
	
	logger.debug("-- just entered --");
	
	
	
	
	Set<Listitem> selectedItems = dispMlListsLBoxId.getSelectedItems();
	
	
	String listNamesStr="";
	
	for (Listitem li : selectedItems) {
		
		MailingList mailingList = (MailingList) li.getValue();			
		if(listNamesStr.length() != 0) listNamesStr+=",";
		listNamesStr += mailingList.getListName() ;
	}
	
	selMlLblId.setValue(listNamesStr);
	
	//visibility div of Selected list
	if(selMlLblId.getValue().length() != 0) {
		selectedListDivId.setVisible(true);
	}else{
		selectedListDivId.setVisible(false);
	}

}
private Window viewSegRuleWinId;
private Label viewSegRuleWinId$segRuleLblId;

/*private class MyListener implements EventListener {

	@Override
	public void onEvent(Event event) throws Exception {
		
		String action = (String)event.getTarget().getAttribute(
						SmsCampSettingsController.TB_ACTION);
		Rows rows;
		SMSCampaignSchedule smsCampSchedule;
		if(event.getTarget() instanceof Textbox) {
			
			if(event.getName().equals(Events.ON_CTRL_KEY) || event.getName().equals(Events.ON_RIGHT_CLICK) ) {
				MessageUtil.setMessage("Right-click and CTRL+V actions have been disabled.", "color:red;");
				return;
				
				
			}
			
			
		}
		else if( event.getTarget() instanceof A) {
			
				Object srcObject = event.getTarget().getAttribute(ATTRIBUTE_SOURCE);
			if(srcObject instanceof SegmentRules){
			
				SegmentRules segmentRule = (SegmentRules)srcObject;
				String str=segmentRule.getSegRuleToView();
				viewSegRuleWinId$segRuleLblId.setValue(str);
				viewSegRuleWinId.setVisible(true);
			
			}
			else if(srcObject instanceof MailingList){
				
				MailingList mailingList=(MailingList) srcObject;
				Sessions.getCurrent().setAttribute("mailingList",mailingList);
			
				if(Sessions.getCurrent().getAttribute("viewType") != null )
				 			Sessions.getCurrent().removeAttribute("viewType");
			
				Redirect.goTo(PageListEnum.CONTACT_CONTACT_VIEW);
				}
		}
		else if( event.getTarget() instanceof Toolbarbutton && action != null) {
		
		
			currentRow = (Row) event.getTarget().getParent().getParent();
			smsCampSchedule = (SMSCampaignSchedule)currentRow.getValue();
			
			if(action.equals(SmsCampSettingsController.TB_ACTION_RESEND)) {
			
				// if clicks on 'Add Resend'
				resendOptionWinId$errMsgLblId.setVisible(false);
				resendOptionWinId.setVisible(true);
				
			}
			else if(action.equals(SmsCampSettingsController.TB_ACTION_DELETE)) {
			
				//if clicks on Delete the schedule
				*//**
				*  Deletes the row from the rows and removes corresponding
				*  schedule object from the list when user clicks on delete link 
				*//*
				//logger.debug("----1----");
				temp;List.clear();
				rows = (Rows)currentRow.getParent();
				//logger.info("currentRow children are====>"+((Detail)currentRow.getChildren().get(0)).getChildren());
				
				try {
					int confirm = Messagebox.show("Are you sure you want to delete the schedule?","Delete Schedule?",
							 		Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
						if(confirm != 1){
							return ;
						}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);
				}
				
				to delete the children calendar objects whose parent is going to be  get deleted *
				if((currentRow.getChildren().get(0)) instanceof Detail) {
					//logger.debug("----2----");
					Detail childDetail = (Detail)currentRow.getChildren().get(0);
					if(childDetail.getChildren().get(0) instanceof Grid) {
						//logger.debug("----3----");
					List<Component> childGridRowsLst = ((Component)((Grid)childDetail.getChildren().get(0)).
													getChildren().get(1)).getChildren();
					for(Component childRowComp : childGridRowsLst) {
						Row childRow=(Row)childRowComp; 
						//logger.debug("----4----");
						tempList.remove(((SMSCampaignSchedule)childRow.getValue()).getScheduledDate());
						
					}
						
						
					}
				}
				
				remove date(Calendar object) which is asked to get deleted
				tempList.remove(smsCampSchedule.getScheduledDate());
				
				rows.removeChild(currentRow);
				
				if(rows.getChildren().size() == 0 && rows.getParent().getParent() instanceof Detail) {
					 Detail detail = (Detail)(rows.getParent().getParent()); 
					rows.getParent().setVisible(false);
					detail.setOpen(false);
					detail.setStyle("display:none");
				}//if
				
				//rows.invalidate();
				add to this list if this schedule is already exist and not the new one
				if(smsCampSchedule.getSmsCsId() != null) {
				
					tempSmsCampScheduleList.add(smsCampSchedule);
					
					this call helps to  delete already existing schedules
					removeCampScheduleFromList(smsCampSchedule);
				}
				//logger.debug("----5---"+tempSmsCampScheduleList.size());
				
				
				
				
				
				if(tempSmsCampScheduleList.size()>0) {
					for(SMSCampaignSchedule smscmpschedule : tempSmsCampScheduleList){
						logger.info("id for smsCampschedule is===>"+smscmpschedule.getSmsCsId());
						if(smscmpschedule.getSmsCsId() != null)
							continue;
						else 
							return;
					}
					for(SMSCampaignSchedule tempschedule : tempSmsCampScheduleList) {
						smsCampScheduleList.removeAll(tempSmsCampScheduleList);
					}
					delete the existing schedules(and which are asked to be get deleted) from DB
					smsCampaignScheduleDao.deleteByCollection(tempSmsCampScheduleList);
				}
				rowsMap.remove(smsCampSchedule.getScheduledDate());
				rowMap.remove(smsCampSchedule.getScheduledDate());
				//setSmsCampStatus();
				
				if(schedGrdRowsId.getChildren().size() == 0) {
					
					if(smsCampaign.getDraftStatus() == null) {
						smsCampaign.setDraftStatus(Constants.SMS_CAMP_DRAFT_STATUS_STEP_COMPLETE);
					}
					smsCampaign.setStatus(Constants.CAMP_STATUS_DRAFT);
					smsCampaignsDao.saveOrUpdate(smsCampaign);
					
					
				}
				
				if(smsCampScheduleList.size()>0) {
					//show the rows in the grid according to the existing schedules
				//	loadSchedule();
					*//**
					 * Process Active & Archived Schedules
					 *//*
					//Active
					List<SMSCampaignSchedule> activeCampSchedList = getActiveCampScheduleList(smsCampScheduleList);
					if(activeCampSchedList != null && activeCampSchedList.size() > 0){
						createRowUpComingListBox(activeCampSchedList.get(0), 0, true);
						createDivUpComingCampaigns(true, activeCampSchedList.get(0));
					}
					else{
						createDivUpComingCampaigns(false, null);
					}
					
					//Archived
					List<SMSCampaignSchedule> archiveCampSchedList = getArchivedCampScheduleList(smsCampScheduleList);
					
					if(archiveCampSchedList != null &&  archiveCampSchedList.size() > 0){
						drawArchivedDiv(archiveCampSchedList.get(0));
						drawSentListBox(archiveCampSchedList.get(0));
					}
					else{
						campaignSentDivId.setVisible(false);
					}
				}
				
			}// else if
			else if(action.equals(SmsCampSettingsController.TB_ACTION_EDIT)) {
			
				// if clicks on Edit the content of 'Re send schedule'
				resendEditorWinId.setVisible(true);
				 HttpServletRequest request = (HttpServletRequest)
									Executions.getCurrent().getNativeRequest();
				request.removeAttribute("smsCampSchedule");
				request.setAttribute("smsCampSchedule", smsCampSchedule);
				resendEditorWinId$resendIncId.setSrc("zul/Empty.zul");
				
				resendEditorWinId$resendIncId.setSrc(
					"/zul/campaign/plainEditor.zul?source=schedule");
			
			}//else if
			
		 }// if toolbarbutton event
		
		else if(event.getTarget() instanceof Detail) {
		
			Detail detail = (Detail) event.getTarget();
			Grid subGrid = (Grid)detail.getFirstChild();
			rows = subGrid.getRows();
			
			if(rows.getChildren().size() == 0) {
				rows.getParent().setVisible(false);
			}
			else {
				 List list = rows.getChildren();
				
					for( Object obj:list){
						Row row=(Row)obj;
						SMSCampaignSchedule smsCampShcedule=(SMSCampaignSchedule)row.getValue();
						
						List<Object[]> childList=smsCampaignScheduleDao.getAllChidren(smsCampShcedule.getSmsCsId(),smsCampShcedule.getSmsCampaignId());
						if(childList!=null) {
							logger.debug("/list size is"+childList.size()+"*****"+((Detail)row.getChildren().get(0)).isOpen());
							detail=row.getDetailChild();
							detail.setStyle("display:block;");
							detail.addEventListener("onOpen", this);
							//Grid grid=(Grid)detail.getChildren().get(0);
							
							//((Detail)row.getChildren().get(0)).setOpen(true);//detail.setStyle("display:block;");
							
							
						} //if
					}//for
				
				rows.getParent().setVisible(true);
			}//else
		}//else if
		
	}
	
}
*/




private boolean areTheseSchedulesEqual(SMSCampaignSchedule smsCampSchedule1, SMSCampaignSchedule smsCampSchedule2){
	
	try{
		boolean scheduledDateChek = false;
		
		boolean criteriaChek = false;
		
		boolean parentIdChek = false;
		
		boolean resendLevelChek = false;
		
		boolean smsCampaignIdChek = false;
		
		boolean statusChek = false;
		
		
		if( smsCampSchedule1.getScheduledDate() == null &&  smsCampSchedule2.getScheduledDate() == null){
			scheduledDateChek = true;
		}else if(smsCampSchedule1.getScheduledDate() != null &&  smsCampSchedule2.getScheduledDate() != null){
			scheduledDateChek = smsCampSchedule1.getScheduledDate().equals(smsCampSchedule2.getScheduledDate());
		}else {
			scheduledDateChek = false;
		}
		
		
		
		//primitive data type byte
		criteriaChek = smsCampSchedule1.getCriteria() == smsCampSchedule2.getCriteria();
		
		
		if( smsCampSchedule1.getParentId() == null &&  smsCampSchedule2.getParentId() == null){
			parentIdChek = true;
		}else if(smsCampSchedule1.getParentId() != null &&  smsCampSchedule2.getParentId() != null){
			parentIdChek = smsCampSchedule1.getParentId().equals(smsCampSchedule2.getParentId());
		}else {
			parentIdChek = false;
		}
		
		
		//primitive data type byte
		resendLevelChek = smsCampSchedule1.getResendLevel() == smsCampSchedule2.getResendLevel();
		
		
		
		if( smsCampSchedule1.getSmsCampaignId() == null &&  smsCampSchedule2.getSmsCampaignId() == null){
			smsCampaignIdChek = true;
		}else if(smsCampSchedule1.getSmsCampaignId() != null &&  smsCampSchedule2.getSmsCampaignId() != null){
			smsCampaignIdChek = smsCampSchedule1.getSmsCampaignId().equals(smsCampSchedule2.getSmsCampaignId());
		}else {
			smsCampaignIdChek = false;
		}
		
		
		//primitive data type byte
	    statusChek = smsCampSchedule1.getStatus() == smsCampSchedule2.getStatus();
		
		
		if(scheduledDateChek && criteriaChek && parentIdChek && resendLevelChek && smsCampaignIdChek && statusChek){
			return true;
		}else{
			return false;
		}
				
				
				
		
		
		
	}catch(Exception e){
		logger.info("exception>>>>>>>>>>>>>>>"+e.getStackTrace());
	}
	
	return false;
}

/**
 * removes the schedules from the DB
 * @param smsCampSchedule
 */
private void removeCampScheduleFromList(SMSCampaignSchedule smsCampSchedule) {
	
	try {	
			/*remove those schedules which are selected for deletion from global schedulelist*/
		//TODO need to solve concurrentModificationException here
			//smsCampScheduleList.remove(smsCampSchedule);
			resendOptionWinId$errMsgLblId.setValue("");
			//logger.debug("---6---"+smsCampScheduleList.size()+" ----"+tempList.size());
			/**
			 * Delete the schedule
			 */
			//smsCampaignScheduleDao.delete(smsCampSchedule);
			smsCampScheduleList.remove(smsCampSchedule);
			
			//tempList.remove(smsCampSchedule);
			tempList.remove(smsCampSchedule.getScheduledDate());
			
			
			
			
			if(schedGrdRowsId != null){
				Row row = null;
				List<Component> schedGrdRowsIdChildrenList = schedGrdRowsId.getChildren();
				if(schedGrdRowsIdChildrenList != null && schedGrdRowsIdChildrenList.size() > 0){
					for(Component aChild : schedGrdRowsIdChildrenList){
						row = (Row)aChild;
						if(row != null){
							
							
							
							if((row.getValue() != null) && areTheseSchedulesEqual((SMSCampaignSchedule)(row.getValue()), smsCampSchedule)){
								logger.info("Got to delete>>>>>>>>>>>>>>>>>");
								schedGrdRowsId.removeChild(aChild);
								break;
							}
						}
					}
				}
			}
			
			
			
			
			SMSCampaignSchedule tempCS;
			
			/*recursively call this method if any of other schedule is having the parent id as the schedule about to be deleted*/
			
			for (Iterator<SMSCampaignSchedule> iterator = smsCampScheduleList.listIterator();
														iterator.hasNext();) {
				tempCS = iterator.next();
				logger.debug("the values are===>"+tempCS.getParentId()+"   "+smsCampSchedule.getSmsCsId());
				
				/*executes only for existing schedules*/
				if(tempCS.getParentId() != null && (smsCampSchedule.getSmsCsId() != null) &&
						(tempCS.getParentId().longValue() == smsCampSchedule.getSmsCsId().longValue())) {
					//logger.debug("----7----");
					/*this is may not to do*/
					if(tempList.contains(tempCS.getScheduledDate())) {
						tempList.remove(tempCS.getScheduledDate());
						
					}
					if(tempCS.getSmsCsId() != null) {
						tempSmsCampScheduleList.add(tempCS);
					}
					//logger.info("the delete list size inside is===>"+tempSmsCampScheduleList.size());
					removeCampScheduleFromList(tempCS);
				}//if
				
				
			}//for
			
			//tempSmsCampScheduleList.add(smsCampSchedule);
			//logger.info("the delete list size is===>"+tempSmsCampScheduleList.size());
			
			//tempList.remove(smsCampSchedule.getScheduledDate());
			//logger.debug("----8---");
			
	} 
	catch (Exception e) {
		logger.error("Exception while removing the SMSCampaign schedules",e);
	}
	
}// removeCampScheduleFromList()






/**
 * configures the schedules for a specific day
 */
 
/*public void onClick$prtDtBtnId() {
	
	//smsCampaign = (SMSCampaigns)session.getAttribute("smsCampaign");
	
	if(smsCampaign == null) { // to have the SMSCampaign obj's id 
		
		
		
	}//if
			else {
		logger.debug("getting the existing object");
		smsCampaign = (SMSCampaigns)session.getAttribute("smsCampaign");
	}
			
	Calendar cal = null;
	schErrorLblId.setValue("");
	try {
		cal = prtDtBxId.getServerValue();
		
		//************** Test for 1 hour gap between scheduled dates ******************
		for (Calendar tempCal : tempList) {
			
			//logger.info("tempCal.compareTo(cal)="+tempCal.compareTo(cal));
			
			
			if(Math.abs(tempCal.getTimeInMillis() - cal.getTimeInMillis()) < 3600000 ) {
				schErrorLblId.setValue("Resend date must be atleast one hour after the actual shcedule date.");
				logger.debug("Resend date must be atleast one hour after the actual shcedule date.");
				return;
			}
		} // for
		
		tempList.add(cal);
	} 
	catch (Exception e) {
		logger.warn("Exception : Date is not selected" + e);
//		MessageUtil.setMessage("Please select the date.", "color:red", "TOP");
		schErrorLblId.setValue(" Please select the date");
		return;
	}
	
	addDateToGrid(cal);
	//createRow(cal,schedGrdRowsId);
	
} // onClick$prtDtBtnId
*/

private Radiogroup sendTypeRgId;
private Label optOutLblId;
//private Boolean SEND_TYPE_ALL = true;

public void onCheck$sendTypeRgId() {
	//what about header here?
	Radio selectedRadio = sendTypeRgId.getSelectedItem();
	optInTextDispLblId.setVisible(!msgTypeRgId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL));
	if(SMSStatusCodes.optOutFooterMap.get(currUser.getCountryType())  && smsSettings != null) {

		
		SMSSettings optinSettings = null;
  		SMSSettings optOutSettings = null;
  		SMSSettings helpSettings = null;
  		
  		String messageHeader = Constants.STRING_NILL;
  		for (SMSSettings eachSMSSetting : smsSettings) {
  			
  			if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
  			
  		}
		if(optinSettings != null && messageHeader.isEmpty()) messageHeader = optinSettings.getMessageHeader();
		else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
		else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
		
		//String optoutKeyWord = smsSettings.getOptoutKeyword();
		//if(entireListRId.isChecked() && optOutSettings != null) 
		if(!msgTypeRgId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL) 
						&& entireListRId.isChecked() && optOutSettings != null){
			
			if(!currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {
			optOutLblId.setValue("Reply "+optOutSettings.getKeyword()+" 2 Optout" );
			optOutLblId.setVisible(true);
			}
				else {
					optOutLblId.setValue(optOutSettings.getKeyword());
					optOutLblId.setVisible(true);
				}
			
		}else{
			
			optOutLblId.setValue("");
			optOutLblId.setVisible(false);
		}
		getCharCount(SMSMsgTbId.getValue());
	}else{
		getCharCount(SMSMsgTbId.getValue());
	}
	/*MessageUtil.setMessage("SMS settings are not found for your account.\n Please contact Admin to enable SMS features.", "color:red;");
		return;*/
	senderIdTbId.setValue(msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE_REQUIRED).toString().equals("N") ? 
			Constants.TWO_WAY_PROMO_TEXT_ID : msgTypeRgId.getSelectedItem().getAttribute(SENDERID_ATTRIBUTE).toString());
	senderIdTbId.setAttribute(SENDERID_ATTRIBUTE, msgTypeRgId.getSelectedItem().
			getAttribute(SENDERID_ATTRIBUTE).toString());
	setSenderID();
	
	//logger.info("$$$$$$$$$$##########33"+senderIdTbId.getValue());	
	
	
		if(selectedRadio.getId().equalsIgnoreCase("entireListRId") ) {

			if(SMSStatusCodes.optInMap.get(currUser.getCountryType())) {
				optInTextDispLblId.setValue("Promotional messages will not be sent to customers in NDNC list.");
			}
			optOutLblId.setVisible(true);
			
		}
		else if(selectedRadio.getId().equalsIgnoreCase("optInRId")) {
			logger.debug("inside optin " +selectedRadio.getId());
			
			if(SMSStatusCodes.optInMap.get(currUser.getCountryType())) {
				
				Object optinSenderID = optInRId.getAttribute(SENDERID_ATTRIBUTE);
				if(optinSenderID == null) {
					
					MessageUtil.setMessage(Constants.OPT_IN_ACC_MESSAGE, "color:red", "TOP");
					if(sendTypeRgId.isVisible()) sendTypeRgId.setSelectedIndex(0);
				}else{
					senderIdTbId.setValue(optinSenderID.toString());
					senderIdTbId.setAttribute(SENDERID_ATTRIBUTE, optinSenderID.toString());
					optInTextDispLblId.setValue("Promotional messages will be sent to all Opted-In customers, even if they are in NDNC list.");
					
				}
			}
			
			optOutLblId.setVisible(false);
			
		}
		
	
}

public void onBlur$mblNoTxtBoxId() throws Exception {
	 
	 String mobNum=mblNoTxtBoxId.getValue();
	 //logger.debug("here in on blur method mail id "+mail);
	 if(mobNum.equals("Enter Mobile Number(s)...") || mobNum.equals("")){
		 mblNoTxtBoxId.setValue("Enter Mobile Number(s)...");
		 
	 }
}


public void onBlur$testSMSTbId() throws Exception {
	 
	 String mobNum=testSMSTbId.getValue();
	 //logger.debug("here in on blur method mail id "+mail);
	 if(mobNum.equals("Enter Mobile Number(s)...") || mobNum.equals("")) {
		 testSMSTbId.setValue("Enter Mobile Number(s)...");
		 
	 }
}


private static String getKeywordsInMessage(String messageContent, Long orgId){
	
	String keywords = Constants.STRING_NILL;
	
	try{
		
		OrgSMSkeywordsDao orgSMSkeywordsDao = (OrgSMSkeywordsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGSMSKEYWORDS_DAO);
		List<OrgSMSkeywords> retKeyword = orgSMSkeywordsDao.getUserOrgSMSKeyWords(orgId);
		boolean isFound = false;
		
		
		if(retKeyword != null) {
			for (OrgSMSkeywords OrgSMSkeyword : retKeyword) {
				
				String shortCode = OrgSMSkeyword.getShortCode();
				boolean isPlus = (shortCode.length() > 10);
				//\\bkeyword\\b\\s+\\bto\\b\\s+[^\\s\\w]*\\b919490927928\\b
				String pattern = "\\b"+OrgSMSkeyword.getKeyword()+"\\b\\s+\\bto\\b\\s+"+(isPlus ? "[^\\s\\w]*" : "" )+"\\b"+OrgSMSkeyword.getShortCode();
				int options = 0;
				options |= 128; 	//This option is for Case insensitive
				options |= 32;
				  Pattern p = Pattern.compile(pattern, options);
				  Matcher m = p.matcher(messageContent);
				  isFound = false;
				  while (m.find()) {
					  
					  isFound = true;
				  }
				
				if(isFound) {
					if(!keywords.isEmpty()) keywords += Constants.DELIMETER_COMMA;
					
					keywords += OrgSMSkeyword.getKeyword();
					
				}
				
			}
		}
		
		logger.info("keywords are >>> "+keywords);
		
		return keywords;
	}catch(Exception e){
		logger.error("Exception so returning empty string >>> ",e);
		return "";
	}
	
	
}


public static Set<String> findCoupPlaceholders(String content) {
	//logger.debug("entered... findcouponplaceholder..");
	content = content.replace("|^", "[").replace("^|", "]");
	String cfpattern = "\\[([^\\[]*?)\\]";
	
	Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
	Matcher m = r.matcher(content);
	//logger.debug("after matcher....");
	String ph = null;
	Set<String> totalPhSet = new HashSet<String>();

	try {
		while(m.find()) {
			
			ph = m.group(1); //.toUpperCase()
			//logger.info("Ph holder :" + ph);

			
			 if(ph.startsWith("CC_")) {
				 totalPhSet.add(ph);
			 }
		}
			
	}catch (Exception e) {
		// TODO: handle exception
		return totalPhSet;
	}
	
	return totalPhSet;
}
/*********** Added for campaign categories**********/

		private Image helpImgId;
		private Popup help2;
		public void getCampCategorties(){
		
				List<UserCampaignCategories> userCategoriesList= userCampaignCategoriesDao.findCatByUserId(currUser.getUserId());
				
				//TODO chk with mallika
				Listitem campCategory = null;
				if(userCategoriesList == null || userCategoriesList.size() == 0 ){
					
					categoryLbId.setDisabled(true);
					
				
					
				}
				else{
					
					for (UserCampaignCategories userCampaignCategories : userCategoriesList) {
						campCategory = new Listitem(userCampaignCategories.getCategoryName(),userCampaignCategories);
						campCategory.setParent(categoryLbId);
						
					}
					
				/*	helpImgId.setVisible(false);
					help2.close();*/
					categoryLbId.setDisabled(false);
					//categoryLbId.setDisabled( ((String) msgTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase(Constants.SMS_TYPE_NAME_TRANSACTIONAL));
					
				}
				// eight
				
				categoryLbId.setDisabled( ((String) msgTypeRgId.getSelectedItem().getValue()).equalsIgnoreCase(Constants.SMS_TYPE_NAME_TRANSACTIONAL));
				
				helpImgId.setVisible(categoryLbId.isDisabled());
				if(categoryLbId.getItemCount() > 0 ) categoryLbId.setSelectedIndex(0);
			}

		/********************Ended*******************/
		public void onClick$addNewTempTBId() {
			logger.debug("just click addNewTempTBId >>> ");
			createTransTempWinId.setTitle("Create New Transactional Template");
			createTransTempWinId$transTempNameTxtBxId.setValue("");
			createTransTempWinId$transTempNameTxtBxId.setStyle(NORMAL_STYLE);
			createTransTempWinId$transTempContentTxtBxId.setValue("");
			createTransTempWinId$transTempContentTxtBxId.setStyle(NORMAL_STYLE);
			createTransTempWinId$transTempCharCountTxtBxId.setValue("");
			createTransTempWinId$transTempCharCountTxtBxId.setStyle(NORMAL_STYLE);
			createTransTempWinId$transTempEditId.setValue(Constants.STRING_NILL);
			createTransTempWinId.doHighlighted();
			//setVisible(true);
			
		}
		
		public void onClick$editNewTempTBId() {
			logger.debug("just click editNewTempTBId >>> ");
			createTransTempWinId.setTitle("Edit Transactional Template");
			createTransTempWinId$transTempNameTxtBxId.setValue("");
			createTransTempWinId$transTempNameTxtBxId.setStyle(NORMAL_STYLE);
			createTransTempWinId$transTempContentTxtBxId.setValue("");
			createTransTempWinId$transTempContentTxtBxId.setStyle(NORMAL_STYLE);
			createTransTempWinId$transTempEditId.setValue("");
			//createTransTempWinId$insertMergetagsLbId_createNewTx.setSelectedIndex(0);
			//createTransTempWinId$insertCouponLbId_createNewTx.setSelectedIndex(0);
			//createTransTempWinId$insertBarcodeLbId_createNewTx.setSelectedIndex(0);
			if(transTempCmbBxId.getSelectedIndex() == 0) {
				return;
			}else{
				TransactionalTemplates transacTempObj = (TransactionalTemplates)transTempCmbBxId.getSelectedItem().getValue();
					if(transacTempObj!=null) {
						createTransTempWinId$transTempNameTxtBxId.setValue(transacTempObj.getTemplateName());
						createTransTempWinId$transTempEditId.setValue(transacTempObj.getTransactionId().toString());
						createTransTempWinId$transTempContentTxtBxId.setValue(transacTempObj.getTemplateContent());
						getCharCountOfTransTempConent(transacTempObj.getTemplateContent());
					}
			}
			
			createTransTempWinId$transTempCharCountTxtBxId.setStyle(NORMAL_STYLE);
			createTransTempWinId.doHighlighted();
		}


	public void onClick$transTempCancelBtnId$createTransTempWinId() {
		logger.debug("just click onClick$transTempCancelBtnId$createTransTempWinId >>> ");
		createTransTempWinId$transTempNameTxtBxId.setValue("");
		createTransTempWinId$transTempNameTxtBxId.setStyle(NORMAL_STYLE);
		createTransTempWinId$transTempContentTxtBxId.setValue("");
		createTransTempWinId$transTempContentTxtBxId.setStyle(NORMAL_STYLE);
		createTransTempWinId$transTempCharCountTxtBxId.setValue("");
		createTransTempWinId$transTempCharCountTxtBxId.setStyle(NORMAL_STYLE);
		createTransTempWinId.setVisible(false);
		
	}// onClick$transTempCancelBtnId$createTransTempWinId
	
	public void onClick$transTempSaveBtnId$createTransTempWinId() {
		logger.debug("just click onClick$transTempSaveBtnId$createTransTempWinId >>> ");
		
		createTransTempWinId$transTempNameTxtBxId.setStyle(NORMAL_STYLE);
		createTransTempWinId$transTempContentTxtBxId.setStyle(NORMAL_STYLE);
		String templateName = createTransTempWinId$transTempNameTxtBxId.getValue().trim();
		String contentStr = createTransTempWinId$transTempContentTxtBxId.getValue().trim();
		if(templateName.trim().length() == 0) {
			createTransTempWinId$transTempNameTxtBxId.setStyle(ERROR_STYLE);
			MessageUtil.setMessage("Please provide Template Name.", "color:red", "TOP");
			return;
		}else if(contentStr.trim().length() == 0) {
			createTransTempWinId$transTempContentTxtBxId.setStyle(ERROR_STYLE);
			
			MessageUtil.setMessage("Please provide Template Content.", "color:red", "TOP");
			return;
		}
		
		logger.debug("transactionalTemplatesDao value is ::"+transactionalTemplatesDao);
		
		/*boolean isExistFlag = transactionalTemplatesDao.isTemplateNameExistByUserId(currUser.getUserId(),templateName);
		
		if(isExistFlag){
			MessageUtil.setMessage("Template already exists with the given name", "color:red", "TOP");
			createTransTempWinId$transTempNameTxtBxId.setStyle(ERROR_STYLE);
			return;
		}
		
		TransactionalTemplates transactTempObj = new TransactionalTemplates();*/
		TransactionalTemplates transactTempObj = null;
		boolean setIsEdited = false;
		if(createTransTempWinId$transTempEditId!=null && createTransTempWinId$transTempEditId.getValue() !=null && !createTransTempWinId$transTempEditId.getValue().isEmpty()) {
			transactTempObj = transactionalTemplatesDao.find(Long.parseLong(createTransTempWinId$transTempEditId.getValue()));
			setIsEdited = true;
				if(!transactTempObj.getTemplateName().equals(createTransTempWinId$transTempNameTxtBxId.getValue())) {
					transactTempObj = new TransactionalTemplates();
				}
		}else {
			boolean isExistFlag = transactionalTemplatesDao.isTemplateNameExistByUserId(currUser.getUserId(),templateName);
			
			if(isExistFlag){
				MessageUtil.setMessage("Template already exists with the given name", "color:red", "TOP");
				createTransTempWinId$transTempNameTxtBxId.setStyle(ERROR_STYLE);
				return;
			}
			transactTempObj = new TransactionalTemplates();
		}
		transactTempObj.setStatus(0);
		transactTempObj.setTemplateName(templateName);
		transactTempObj.setUserId(currUser.getUserId());
		transactTempObj.setOrgId(currUser.getUserOrganization().getUserOrgId());
		transactTempObj.setTemplateContent(contentStr);
		//transactionalTemplatesDao.saveOrUpdate(transactTempObj);
		transactionalTemplatesDaoForDML.saveOrUpdate(transactTempObj);

		
		
		Messagebox.show("An email has been sent to Optculture Admin for template approval. "
				+ "Once approved, \n the template will be automatically activated into your account for use. ", "info", Messagebox.OK, Messagebox.INFORMATION); 
		MessageUtil.setMessage("Template saved successfully.", "color:green;");
		
		
		String toEmailId = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SUPPORT_EMAILID);
		
		String templateTypeStr = PropertyUtil.getPropertyValueFromDB("TransactionalTemplate");
		//APP-2378
		String currUserName = currUser.getUserName();
		
		if (templateTypeStr != null) {
			//you have to replace the entire placeholder here.
			templateTypeStr = templateTypeStr.replace("[Template content]", contentStr).replace("[Username]", 
					Utility.getOnlyUserName(currUserName)).replace("[orgID]", Utility.getOnlyOrgId(currUserName));
		}else templateTypeStr = contentStr ;
		logger.info("Transactional template content : "+ contentStr);
		
		EmailQueue emailQueue = new EmailQueue("Transactional Template", templateTypeStr,
				Constants.EQ_TYPE_LOW_SMS_CREDITS, "Active", toEmailId,Calendar.getInstance(), currUser);
    	emailQueueDaoForDML.saveOrUpdate(emailQueue);
    	
    	
    	onClick$transTempCancelBtnId$createTransTempWinId();
    	
    	if(setIsEdited = true) {
    		SMSMsgTbId.setValue(Constants.STRING_NILL);
    		transacTempSettings();
    		setIsEdited = false;
    	}else {
	    	// set Transactional templates
	    	Comboitem comboItemObj = new Comboitem(templateName);
	    	comboItemObj.setDescription("Pending");
	    	comboItemObj.setValue(transactTempObj);
	    	comboItemObj.setParent(transTempCmbBxId);
    	}
    	//transacTempSettings();
		
		
	}// onClick$transTempCancelBtnId$createTransTempWinId
	
	public void onSelect$transTempCmbBxId(){
		logger.debug("Templates selection started"+transTempCmbBxId.getSelectedItem().getValue());
		if(transTempCmbBxId.getSelectedIndex() == 0) {
			editNewTempTBId.setVisible(false);
			SMSMsgTbId.setValue("");
			getCharCount("");
//			SMSMsgTbId.setReadonly(false);
		}else{
			editNewTempTBId.setVisible(true);
			TransactionalTemplates transacTempObj = (TransactionalTemplates)transTempCmbBxId.getSelectedItem().getValue();
			if(transacTempObj.getStatus() == 0){
				SMSMsgTbId.setValue("");
				getCharCount("");
//				SMSMsgTbId.setReadonly(false);
				MessageUtil.setMessage("Template is not approved.", "color:red", "TOP");
				return;
			}
			SMSMsgTbId.setValue(transacTempObj.getTemplateContent());
			SMSMsgTbId.setAttribute("templateRegisteredId", transacTempObj.getTemplateRegisteredId());
			getCharCount(transacTempObj.getTemplateContent());
			SMSMsgTbId.setReadonly(true);
			//Clients.evalJavaScript("updateCaretPosition()");
//			onChange$caretPosTB();
//			SMSMsgTbId.setReadonly(true);
		}
	}
	
	private void transacTempSettings() {
		
		try {
			Components.removeAllChildren(transTempCmbBxId);
			
			//set the TransactionalTemplates
			List<TransactionalTemplates>  templateList = transactionalTemplatesDao.findTemplatesByOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId());
			Comboitem combItem = null;
			combItem = new Comboitem("--select--");
			combItem.setParent(transTempCmbBxId);
			
			if(templateList != null && templateList.size() >0) {
				
				for (TransactionalTemplates eachObj : templateList) {
					combItem = new Comboitem(eachObj.getTemplateName());
					combItem.setDescription(eachObj.getStatus() == 0? "Pending" : "Approved");
					combItem.setValue(eachObj);
					combItem.setParent(transTempCmbBxId);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception",e);
		}
	}
	
	public void getCharCountOfTransTempConent(String msgContent) {
		try {
				int charCount = msgContent.length();
//				logger.info("the length is====>"+charCount);
				createTransTempWinId$transCaretPosTB.setValue(""+charCount);
				if(charCount>160) {
					//warnLblId.setVisible(true);
					int msgcount = charCount/160;
					createTransTempWinId$transTempCharCountTxtBxId.setValue(""+charCount+"/"+(msgcount+1));
					/*charCountTbId.setValue(""+(smsCampaign.getMessageContent().
							substring(msgcount*160, charCount)).length()+"/"+(msgcount+1));*/
				}//if
				else {
					//warnLblId.setVisible(false);
					createTransTempWinId$transTempCharCountTxtBxId.setValue(""+charCount+" / "+1);
				}//else
			
			
		} catch (Exception e) {
				logger.debug("Exception while getting the character count",e);
		}//catch
		
	}//getCharCount
	//createTransTempWinId$transCaretPosTB
	public void onChange$transCaretPosTB$createTransTempWinId(){
		logger.debug("-----just entered:123 onChange event----");
		//Clients.evalJavaScript("updateTranTemCaretPosition();");
		//getCharCountOfTransTempConent(createTransTempWinId$transTempContentTxtBxId.getValue());
	}
	
	public void onBlur$transCaretPosTB$createTransTempWinId() {
		
		logger.debug("-----just entered:123 onBlur event----");
		
	}
	/**
	 * gives the char count of SMS
	 * @param event
	 */
	public void onChanging$transTempContentTxtBxId$createTransTempWinId(InputEvent event){
		try{
			//getCharCount( event.getValue());
			getCharCountOfTransTempConent(event.getValue());
			
			
		}catch (Exception e) {
			logger.debug("Exception **",e);
		}
	}//onChanging$SMSMsgTbId(-)
	
	public void onBlur$linkURLTxtBoxId(){
		String defaultText = linkURLTxtBoxId.getText();
		if(defaultText.isEmpty() || defaultText.length() == 0) {
			linkURLTxtBoxId.setText("Use Url Shortener");
		}
	}//
	
	private boolean IsUsingExpKeyword(String messageContent, Calendar schDate) {
		
		boolean expStatus = false;
		List<OrgSMSkeywords> keywordsList = orgSMSkeywordsDao.getUserOrgEXPSMSKeyWords(GetUser.getUserObj().getUserOrganization().getUserOrgId());
		
		if(keywordsList != null) {
			boolean isFound = false;
			String pendingKeywords = Constants.STRING_NILL;
			String expKeywords = Constants.STRING_NILL;
			for (OrgSMSkeywords OrgSMSkeyword : keywordsList) {
				isFound = false;
				String shortCode = OrgSMSkeyword.getShortCode();
				boolean isPlus = (shortCode.length() > 10);
				String pattern = "\\b"+OrgSMSkeyword.getKeyword()+"\\b\\s+\\bto\\b\\s+"+(isPlus ? "[^\\s\\w]*" : "" )+"\\b"+OrgSMSkeyword.getShortCode();//OrgSMSkeyword.getKeyword()+" to "+(isPlus ? "+" : "" )+OrgSMSkeyword.getShortCode();
				int options = 0;
				options |= 128; 	//This option is for Case insensitive
				options |= 32;
				Pattern p = Pattern.compile(pattern, options);
				Matcher m = p.matcher(messageContent);
				
				while (m.find()) {
					
					isFound = true;
				}
				
				if(isFound) {
					if(Constants.KEYWORD_STATUS_PENDING.equalsIgnoreCase(OrgSMSkeyword.getStatus()) && 
							schDate != null && OrgSMSkeyword.getStartFrom().after(schDate)) {
						
						if(!pendingKeywords.isEmpty()) pendingKeywords += Constants.DELIMETER_COMMA+" ";
						
						pendingKeywords += "'"+OrgSMSkeyword.getKeyword()+"'";
						
					}else if(Constants.SMS_KEYWORD_EXPIRED.equalsIgnoreCase(OrgSMSkeyword.getStatus())) {
						if(!expKeywords.isEmpty()) expKeywords += Constants.DELIMETER_COMMA+" ";
						
						expKeywords += "'"+OrgSMSkeyword.getKeyword()+"'";
					}
				}
				
			}//for
			if(!pendingKeywords.isEmpty() || !expKeywords.isEmpty()) {
				
				String Mesg =  "Keyword(s) "+ (!pendingKeywords.isEmpty() ? pendingKeywords : (!expKeywords.isEmpty() ? expKeywords : "")) + 
				" used in SMS Campaign is not Active, SMS Campaign can not be sent";
				MessageUtil.setMessage(Mesg,"color:red", "TOP");
				expStatus = true;
				
			}//if
			
		}
		/*for(OrgSMSkeywords keyword : keywordsList) {
			String expKeword = keyword.getKeyword() + " to " +keyword.getShortCode();
			if(Constants.SMS_KEYWORD_EXPIRED.equalsIgnoreCase(keyword.getStatus())) {
				if(messageContent.contains(expKeword)){
					MessageUtil.setMessage("Keyword "+ keyword.getKeyword() + " used in SMS Campaign has Expired, SMS Campaign can not be scheduled","color:red", "TOP");
					expStatus = true;
				}
			}
			else if(Constants.KEYWORD_STATUS_PENDING.equalsIgnoreCase(keyword.getStatus())) {
				if(messageContent.contains(expKeword)){
					if(schDate != null) {
						if(keyword.getStartFrom().after(schDate)) {
							
							MessageUtil.setMessage("Keyword "+ keyword.getKeyword() + " used in SMS Campaign is not Active, SMS Campaign can not be scheduled","color:red", "TOP");
							expStatus = true;
						}
					}
				}
			}

			
		}*/
		
		return expStatus;
	}
	
	public boolean invalidPromoCodes() {
		boolean isValid = false;
		
		String messageContent = smsCampaign.getMessageContent();

						Set<String> couponPhSet = findCoupPlaceholders(messageContent.trim());
						logger.debug("couponPhSet :"+couponPhSet);
						String couponIdStr = "";
						for(String ph : couponPhSet){
							
							if(ph.startsWith("CC_")){
								
								String[] phStr = ph.split("_");
								if(phStr.length > 2){
									
									MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
									return true;
								}
								
								couponIdStr += couponIdStr.trim().isEmpty() ? phStr[1].trim() : ","+phStr[1].trim();
								Long couponId = null;
								try{
								couponId = Long.parseLong(phStr[1]);
								}catch(Exception e){
									MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
									return true;
								}
								
								if(couponId != null){
									
									CouponsDao couponsDao= (CouponsDao)SpringUtil.getBean("couponsDao");
									Long orgId = currUser.getUserOrganization().getUserOrgId();
									
									List<Coupons> couponsList = couponsDao.findCouponsByCoupIdsAndOrgId(""+couponId, orgId);
									if(couponsList == null){
										MessageUtil.setMessage("Invalid Placeholder: "+ph, "color:red;", "top");
										return true;
									}
								}
							}
							
						}

							if(!couponIdStr.trim().isEmpty()){
							logger.debug("couponIdStr :"+couponIdStr);
							List<Coupons> inValidCoupList = couponsDao.isExpireOrPauseCoupList(couponIdStr, currUser.getUserOrganization().getUserOrgId());
							
							if(inValidCoupList != null){
								
								String inValidCoupNames = "";
								if(inValidCoupList != null && inValidCoupList.size() >0) {
									
									for (Coupons coupons : inValidCoupList) {
										inValidCoupNames += inValidCoupNames.trim().length() >0 ? ","+coupons.getCouponName() : coupons.getCouponName();
									}
									MessageUtil.setMessage(	"The Discount Code "+inValidCoupNames+" used in this campaign has either expired or in paused status. " +
											" \n Please change the status of this Discount Code.",
											"color:red", "TOP");
									return true;
								}
								
							}
							
						}

						//By Lavanya
						
						if(!couponIdStr.isEmpty()){

							List<Coupons> coupList = couponsDao.findCouponsByCoupIdsAndOrgId(couponIdStr,currUser.getUserOrganization().getUserOrgId());

							String segmentStr = smsCampaign.getListType();

							Set <MailingList> mlset = smsCampaign.getMailingLists();
							
							if(segmentStr != null && mlset == null || mlset.size() == 0){

								MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
								return true;
							}
							else if(segmentStr == null && mlset == null || mlset.size() == 0)
							{
								MessageUtil.setMessage("SMS campaign is not configured to any other mailing list."
										+ "\n Please configure at least one mailing list.", "color:red;");
								return true;
							}

							Long smscount =getSMSCount(segmentStr ,mlset);
							logger.info("################## smscount : "+smscount);
							//logger.debug("promo size is  :: "+coupList.size());
							//Long availPromoCount = 0l;
							
							if(coupList != null && coupList.size() >0){

								for (Coupons eachPromoObj : coupList) {
									logger.debug("eachPromoObj.getAvailable() is  ::"+eachPromoObj.getAvailable());

									if(!eachPromoObj.getAutoIncrCheck()){
										long count = couponCodesDao.findIssuedCoupCodeByCoup(eachPromoObj.getCouponId());
										logger.debug("count is "+count);
										long availCCount = eachPromoObj.getTotalQty() - count;
										logger.debug("availCCount is ************ "+availCCount);
										if(availCCount < smscount) {
											MessageUtil.setMessage("SMS cannot be sent as configured  "+eachPromoObj.getCouponName()+" available Discount Codes count" +
													" \n exceeds the sms send limit.Please  increase Discount Codes issued limit.", "color:red;");
											return true;
										}
									}



								}

							}

						}


		return isValid;
	}
	
	//By Lavanya
	public Long getSMSCount(String segmentStr, Set <MailingList> mlset){
		long smsCount=0;
		long totalCount = contactsDao.getAllSMSCount(mlset);
		//long unpurgedCount = contactsDao.getAllUnpurgedMobileCount(mlset);
		if(segmentStr.startsWith("Segment")) {
			
			String segRuleIds = segmentStr.split(""+Constants.DELIMETER_COLON)[1];
			List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);
			
			if(segmenRules == null) {
				
				MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
				
				return null;
				
				
			}//if
			String tempQry = "";
			logger.debug("size ::"+segmenRules.size());
			for (SegmentRules segmentRules : segmenRules) {
				
				Set<MailingList> mlistSet = new HashSet<MailingList>();
				List<MailingList> mlList = mailingListDao.findByIds(segmentRules.getSegmentMlistIdsStr());
				if(mlList == null) {
					logger.debug("continue");
					continue;
				}
				
				mlistSet.addAll(mlList);
				long mlsbit = Utility.getMlsBit(mlistSet);
				//ClickHouse changes
				if(!currUser.isEnableClickHouseDBFlag())
					segmentStr = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE,mlsbit);
				else
					segmentStr = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_MOBILE,mlsbit);
				
				if(segmentStr == null) {
					MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
					continue;
				}
				if(SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRules.getSegRule())) {
					String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRules.getSegRule());
					
					if(csCampIds != null ) {
						String crIDs = Constants.STRING_NILL;
						//CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
						List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRules.getUserId(), csCampIds);
						if(campList != null) {
							for (Object[] crArr : campList) {
								
								if(!crIDs.isEmpty()) crIDs += Constants.DELIMETER_COMMA;
								crIDs += ((Long)crArr[0]).longValue();
								
							}
						}
						
						segmentStr = segmentStr.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
					}
				}
				
				segmentStr = segmentStr.replace("<MOBILEOPTIN>", !(smsCampaign.isEnableEntireList()) ? " AND c.mobile_opt_in=1" : "");
				logger.info(" Generated Query :"+segmentStr);
				
				if(tempQry.length() > 0) tempQry += " UNION ";
				
				tempQry += segmentStr;
//				contactsDao.insertSegmentedContacts(tempQry);
				
				
				
				
			}//for
			
			if(!currUser.isEnableClickHouseDBFlag())
				smsCount = contactsDao.getSegmentedContactsCount(tempQry);
			else
				smsCount = contactsDao.getSegmentedContactsCountFromCH(tempQry);
			
				 
			
			if(smsCount == 0) {
				
				MessageUtil.setMessage( "Your segment returned 0 active unique contacts of "+ totalCount + " available contacts.","color:red", "TOP");
				return null;
				//MessageUtil.setMessage("Your segment returned 0 active unique contacts of "+ totalCount + " available contacts." , "color:red", "TOP");
				//return false;
			}
			
		
		}//if segment
		else {
			smsCount = contactsDao.getUniqueMobileCount(mlset,"Active");
			
			
			if(smsCount == 0) {
				MessageUtil.setMessage("Your selection returned 0 active unique contacts of "+ totalCount + " available contacts. ", "color:red", "TOP");
				return null;
			}
		}
		
		
		return smsCount;
	}

	//Added for 2.4.6
	private Window viewAllArchivedSchedulesWinId;
	private Listbox viewAllArchivedSchedulesWinId$campListlbId;
	private Button submitBtnId$viewAllArchivedSchedulesWinId;
	private A viewAllArchivedSchedAnchId;
	private A viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId;
	private Listbox sentCampaingsListlbId;
	private Div campaignSentDivId;
	private Label viewAllArchivedSchedulesWinId$noRecordsArchivedLbId,campaignSentLbId;
	private Listbox activeCampaingsListlbId;
	private Div campaignActiveTillDivId;
	private Label campActiveTillDateLbId,numOfTimeCampActiveLbId;
	private A viewAllActiveSchedAnchId;
	private Window viewAllActiveSchedulesWinId;
	private Bandbox viewAllActiveSchedulesWinId$campActionsBandBoxId;
	private Listbox viewAllActiveSchedulesWinId$campListlbId;
	private Button submitBtnId$viewAllActiveSchedulesWinId;
	private Bandbox campActionsBandBoxId$viewAllActiveSchedulesWinId;
	private Label viewAllActiveSchedulesWinId$bulkDeleteLbId;	
	private Label viewAllActiveSchedulesWinId$noRecordsActiveLbId ;
	private A viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId;
	private int activeSchCount = 100;
	private int sentSchCount = 100;
	private boolean persistentCamp = true;
			/**
			 * createRowUpComingListBox
			 * @param campaignSchedule
			 * @param count
			 * @param loadDBSch
			 * @return
			 */
			private boolean createRowUpComingListBox(SMSCampaignSchedule campaignSchedule,int count, boolean loadDBSch){


				logger.debug(">>>>>>> Started  createRowUpComingListBox :: ");
				try {
					//			logger.debug(">>>>>>> Started  createRowUpComingListBox :: ");			
					if(!loadDBSch){
						int available = 0;
						try {
							SubscriptionDetails subDetails = new SubscriptionDetails();
							available = subDetails.getSMSStatus(campaignSchedule.getScheduledDate());
						} catch (Exception e1) {
							logger.error("** Exception : Problem while getting the subscription details", e1);
							return false;
						}
						if(available <=0) {
							MessageUtil.setMessage("SMS cannot be scheduled as you have \n " +
									"reached your SMS credits limit or your account has expired.", 
									"color:red", "TOP");
							return false;
						}
					}

					if(campaignSchedule.getScheduledDate().after(currUser.getPackageExpiryDate())) {
						MessageUtil.setMessage("Schedule date cannot be after your package expiry date.", "color:red", "TOP");
						return false;
					}

					Listitem li = new Listitem();
					Listcell lc = new Listcell();

					if(0 == campaignSchedule.getStatus() || 2 == campaignSchedule.getStatus()){

						//Schedule Date
						li.setValue(campaignSchedule);
						lc.setParent(li);
						lc = new Listcell();
						lc.setLabel(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
						//logger.info(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
						lc.setParent(li);

						//Status
						lc = new Listcell();
						lc.setLabel(campaignSchedule.getStatusStr());
						lc.setParent(li);

						//Delete
						lc = new Listcell();
						Hbox hbox = new Hbox();
						Image delImg = new Image("/img/action_delete.gif");
						delImg.setTooltiptext("Delete");
						delImg.setStyle("cursor:pointer;");
						delImg.setAttribute("delete", "campScheduleDelete");
						delImg.addEventListener("onClick", new MyListener());
						delImg.setParent(hbox);

						hbox.setParent(lc);	
						lc.setParent(li);

						li.setHeight("30px");


						if(count == 0){
							li.setSelected(true);
							li.setParent(activeCampaingsListlbId);
							logger.info("setting first element");
						}
						else{
							logger.info("Set window as parent or hide");

							try{
								//Listbox listbox = (Listbox) activeCampaingsListlbId.getChildren();
								//Listitem listitem = listbox.getItemAtIndex(0);

								SMSCampaignSchedule campSchedChkDate =   (SMSCampaignSchedule) ((Listitem)activeCampaingsListlbId.getSelectedItem()).getValue();

								//			SMSCampaignSchedule campSchedChkDate = (SMSCampaignSchedule) listItem;
								logger.info("When list is not null ::"+MyCalendar.calendarToString(campSchedChkDate.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT));
								logger.info("campaignSchedule ::"+MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT));
								if(campaignSchedule.getScheduledDate().before(campSchedChkDate.getScheduledDate())){
									logger.debug("true");


									int count1 =  activeCampaingsListlbId.getItemCount();
									for(; count1>0; count1--) {
										activeCampaingsListlbId.removeItemAt(count-1);
									}

									if(0 == campaignSchedule.getStatus() || 2 == campaignSchedule.getStatus()){
										li = new Listitem();
										li.setSelected(true);
										//Schedule Date
										li.setValue(campaignSchedule);
										lc.setParent(li);
										lc = new Listcell();
										lc.setLabel(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
										//logger.info(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
										lc.setParent(li);

										//Status
										lc = new Listcell();
										lc.setLabel(campaignSchedule.getStatusStr());
										lc.setParent(li);

										//Delete
										lc = new Listcell();
										hbox = new Hbox();
										delImg = new Image("/img/action_delete.gif");
										delImg.setTooltiptext("Delete");
										delImg.setStyle("cursor:pointer;");
										delImg.setAttribute("delete", "campScheduleDelete");
										delImg.addEventListener("onClick", new MyListener());
										delImg.setParent(hbox);

										hbox.setParent(lc);	
										lc.setParent(li);

										li.setHeight("30px");


										li.setParent(activeCampaingsListlbId);

										li.setParent(activeCampaingsListlbId);

										logger.info("Set window as parent or hide");
									}
								}
							}
							catch(Exception e){
								logger.info("Exception ::",e);
							}
						}
					}
					logger.debug("<<<<< Completed createRowUpComingListBox .");
					return true;
				} catch (Exception e) {
					logger.error("Exception ..........:",e);
					logger.debug("<<<<< Completed createRowUpComingListBox .");
					return false;
				}

			}//createRowUpComingListBox
			
			
			/**
			 * This method return's active or draft campScheuleList.
			 * @param campScheduleList2
			 * @return activeOrDraftList
			 */
			private List<SMSCampaignSchedule> getActiveCampScheduleList(List<SMSCampaignSchedule> campScheduleList2) {
				//		logger.debug(">>>>>>> Started  getActiveCampScheduleList :: ");
				List<SMSCampaignSchedule> activeOrDraftList = new ArrayList<SMSCampaignSchedule>();
				for (SMSCampaignSchedule campaignSchedule : campScheduleList2) {
					if(campaignSchedule.getStatus() == 0  || campaignSchedule.getStatus() == 2 ) {
						activeOrDraftList.add(campaignSchedule);
					}
				}
				//		logger.debug("<<<<< Completed getActiveCampScheduleList .");
				return activeOrDraftList;
			}
			
			/**
			 * This method returns size for active or draft campaign's.
			 * @param campScheduleList2
			 * @return size
			 */
			private String getSize(List<SMSCampaignSchedule> campScheduleList2) {
				//logger.debug(">>>>>>> Started  getSize :: ");
				int activeCount = 0;
				for (SMSCampaignSchedule campaignSchedule : campScheduleList2) {
					if(campaignSchedule.getStatus() == 0  || campaignSchedule.getStatus() == 2 ) {
						activeCount++;
					}
				}
				//logger.debug("<<<<< Completed getSize .");
				return activeCount+"";
			}//getSize
			
			
			/**
			 * This method handles on click on viewAllActiveSchedAnchId
			 */
			public void onClick$viewAllActiveSchedAnchId(){
				logger.debug(">>>>>>> Started  onClick$viewAllActiveSchedAnchId :: ");
				try {
					viewAllActiveSchedulesWinId.setVisible(true);
					viewAllActiveSchedulesWinId.doHighlighted();
					viewAllActiveSchedulesWinId.setVisible(true);
					viewAllActiveSchedulesWinId$bulkDeleteLbId.setVisible(false);
					viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(true);
					int count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
					for(; count>0; count--) {
						viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
					}
					noOfTimeRedraw = 0;
					activeSchCount = 100;
					redraw(0,activeSchCount);
					viewAllActiveSchedulesWinId.setStyle("scroll:auto;");
				} catch (Exception e) {
					logger.error("Exception ",e);
				}
				logger.debug("<<<<< Completed onClick$viewAllActiveSchedAnchId .");
			}//onClick$viewAllActiveSchedAnchId
			
			/**
			 * onClick$submitBtnId$viewAllActiveSchedulesWinId
			 */
			public void onClick$submitBtnId$viewAllActiveSchedulesWinId(){
				logger.debug(">>>>>>> Started  onClick$submitBtnId$viewAllActiveSchedulesWinId :: ");
				int count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
				for(; count>0; count--) {
					viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
				}
				viewAllActiveSchedulesWinId.setClosable(false);
				viewAllActiveSchedulesWinId.setVisible(false);
				viewAllActiveSchedulesWinId$bulkDeleteLbId.setVisible(false);
				persistentCamp = true;
				noOfTimeRedraw = 0; 
				
				logger.debug("<<<<< Completed onClick$submitBtnId$viewAllActiveSchedulesWinId .");
			}//onClick$submitBtnId$viewAllActiveSchedulesWinId
			
			
			/**
			 * This method helps to redraw and populate email campaigns.
			 * @param startIndex
			 * @param size
			 */
			int noOfTimeRedraw=0;
			boolean reverseFlag = true;
			List<SMSCampaignSchedule> campaignScheduleList = null;
			public void redraw(int startIndex, int size) {
					logger.debug(">>>>>>> Started  redraw :: ");
				logger.info("Redraw form "+startIndex +"--"+size);
				try {
					MessageUtil.clearMessage();
					//System.gc();
					viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(true);
					//campaignScheduleList  = campScheduleList;
					campaignScheduleList  = getActiveCampScheduleList(smsCampScheduleList);


					if(campaignScheduleList == null || campaignScheduleList.size()<=0){
						logger.info("NO more campaigns");
						viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(true);
						viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(false);
						return;
					}

					Collections.sort(campaignScheduleList);

					
					if(campaignScheduleList != null && campaignScheduleList.size() >0){
						logger.info("*****"+campaignScheduleList.size());
						//		logger.info("Start DAte"+campaignScheduleList.get(0).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"));
						//	logger.info("End Date"+MyCalendar.calendarToString(campaignScheduleList.get(campaignScheduleList.size()-1).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
						if(startIndex == 0){
							//					logger.info("No of time redraw ::"+noOfTimeRedraw);
							noOfTimeRedraw ++;
							Listitem li;
							Listcell lc;
							int index =0;
							//for (int i = 0; i <= size; i++) { // 0 -100
							for (int i = 0; i < campaignScheduleList.size(); i++) { 
								//logger.info("Started loop"+i);
								if(i == 101){
									logger.info(i+"..Camp list reach size break......"+size);
									break;
								}
								else{
									index=i;
									//	logger.info("in else"+i);
									li = new Listitem();
									lc = new Listcell();
									//Active and Draft Schedules
									//if(0 == campaignScheduleList.get(i).getStatus() || 2 == campaignScheduleList.get(i).getStatus()){
									viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(false);
									//Schedule Date
									li.setValue(campaignScheduleList.get(i));
									lc.setParent(li);
									lc = new Listcell();
									logger.info(MyCalendar.calendarToString(campaignScheduleList.get(i).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
									lc.setLabel(MyCalendar.calendarToString(campaignScheduleList.get(i).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));

									lc.setParent(li);

									//Status
									lc = new Listcell();
									lc.setLabel(campaignScheduleList.get(i).getStatusStr());
									lc.setParent(li);

									//Delete
									lc = new Listcell();
									Hbox hbox = new Hbox();
									Image delImg = new Image("/img/action_delete.gif");
									delImg.setTooltiptext("Delete");
									delImg.setStyle("cursor:pointer;");
									delImg.setAttribute("delete", "campScheduleDelete");
									delImg.addEventListener("onClick", new MyListener());
									delImg.setParent(hbox);

									hbox.setParent(lc);	
									lc.setParent(li);

									li.setHeight("30px");
									li.setParent(viewAllActiveSchedulesWinId$campListlbId);

									//}//internal if
								}
								logger.debug("Completed for loop till size  ::"+index);
							}//for loop

						}//first time
						else if(startIndex !=0 ){


							//					logger.info("No of time redraw ::"+noOfTimeRedraw);
							noOfTimeRedraw ++;
							Listitem li;
							Listcell lc;
							logger.info(startIndex+" Start Index & Size ::"+size);
							logger.info("startIndex == campaignScheduleList.size()"+(startIndex == campaignScheduleList.size()));
							for (int i =  startIndex+1; i <= size; i++) {
								//	logger.info("value for index :"+i+" : list size :: "+campaignScheduleList.size());
								if( campaignScheduleList.size() <= i){
									logger.info("NO more campaigns");
									viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(true);
									viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(false);
									logger.info("Camp list reach size break......");
									break;
								}

								li = new Listitem();
								lc = new Listcell();
								//Active and Draft Schedules
								//if(0 == campaignScheduleList.get(i).getStatus() || 2 == campaignScheduleList.get(i).getStatus()){
								viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(false);
								//Schedule Date

								li.setValue(campaignScheduleList.get(i));
								lc.setParent(li);
								lc = new Listcell();
								/*logger.info("::"+i);*/

								logger.info(MyCalendar.calendarToString(campaignScheduleList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
								lc.setLabel(MyCalendar.calendarToString(campaignScheduleList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
								lc.setParent(li);

								//Status
								lc = new Listcell();
								lc.setLabel(campaignScheduleList.get(i).getStatusStr());
								lc.setParent(li);

								//Delete
								lc = new Listcell();
								Hbox hbox = new Hbox();
								Image delImg = new Image("/img/action_delete.gif");
								delImg.setTooltiptext("Delete");
								delImg.setStyle("cursor:pointer;");
								delImg.setAttribute("delete", "campScheduleDelete");
								delImg.addEventListener("onClick", new MyListener());
								delImg.setParent(hbox);

								hbox.setParent(lc);	
								lc.setParent(li);

								li.setHeight("30px");
								li.setParent(viewAllActiveSchedulesWinId$campListlbId);

								//}//internal if
							}//for loop


						}//more than one time
						else {
							logger.info("NO more campaigns");
							viewAllActiveSchedulesWinId$noRecordsActiveLbId.setVisible(true);
							viewAllActiveSchedulesWinId$viewMoreActiveSchedAnchId.setVisible(false);
						}

					}

				} catch (WrongValueException e) {
					logger.error("WrongValueException ", e);
				} catch (Exception e) {
					logger.error("Exception ", e);
				}
					logger.debug("<<<<< Completed redraw .");
			}//redraw


			private List<SMSCampaignSchedule> sentCampaignScheduleList = null;
			/**
			 * redrawSent
			 * @param startIndex
			 * @param size
			 */
			boolean redrawFlag = true;
			public void redrawSent(int startIndex,int size){
				logger.debug(">>>>>>> Started  redrawSent :: ");
				try{
					sentCampaignScheduleList = getArchivedCampScheduleList(smsCampScheduleList);

					if(sentCampaignScheduleList == null ){
						viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(true);
						viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(false);
						logger.error("No Campaing's existing.........");
						return;
					}
					
					if(redrawFlag){
					logger.info("reversing list");
					Collections.reverse(sentCampaignScheduleList);
					}
				
				

					if(sentCampaignScheduleList != null && sentCampaignScheduleList.size()>0){	
						if(startIndex == 0){
							Listitem li;
							Listcell lc;
							for(int i=0;i<=sentCampaignScheduleList.size()-1;i++ ){

								if(i == size){
									logger.info(i+"..Camp list reach size break......"+size);
									break;
								}
								else{
									li = new Listitem();
									lc = new Listcell();
									viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(false);
									viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(true);
									viewAllArchivedSchedAnchId.setVisible(true);
									li.setValue(sentCampaignScheduleList.get(i));
									lc.setParent(li);
									//Date
									lc = new Listcell();
									lc.setLabel(MyCalendar.calendarToString(sentCampaignScheduleList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
									lc.setParent(li);
									//Status
									lc = new Listcell();
									lc.setLabel(sentCampaignScheduleList.get(i).getStatusStr());
									lc.setParent(li);

									li.setHeight("30px");
									li.setParent(viewAllArchivedSchedulesWinId$campListlbId);
								}
							}

						}


						else if(startIndex != 0 ){
							Listitem li;
							Listcell lc;
							for(int i =  startIndex; i <= size; i++){
								if( sentCampaignScheduleList.size() <= i){
									logger.info("NO more campaigns");
									viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(true);
									viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(false);
									logger.info("Camp list reach size break......");
									break;
								}

								li = new Listitem();
								lc = new Listcell();
								li = new Listitem();
								lc = new Listcell();
								viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(false);
								viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(true);
								viewAllArchivedSchedAnchId.setVisible(true);
								li.setValue(sentCampaignScheduleList.get(i));
								lc.setParent(li);
								//Date
								lc = new Listcell();
								lc.setLabel(MyCalendar.calendarToString(sentCampaignScheduleList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
								lc.setParent(li);
								//Status
								lc = new Listcell();
								lc.setLabel(sentCampaignScheduleList.get(i).getStatusStr());
								lc.setParent(li);

								li.setHeight("30px");
								li.setParent(viewAllArchivedSchedulesWinId$campListlbId);
							}

						}
					}
					else{
						viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(true);
						viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(false);
						logger.error("No Campaing's existing.........");
					}
				}
				catch(Exception exception){
					logger.error("Exception",exception);
				}
				logger.debug("<<<<< Completed redrawSent .");
			}//redrawSent
			
			private List<SMSCampaignSchedule> getArchivedCampScheduleList(
					List<SMSCampaignSchedule> campScheduleList2) {
				List<SMSCampaignSchedule> archiveCampSchedule = new ArrayList<SMSCampaignSchedule>();
				for (SMSCampaignSchedule campaignSchedule : campScheduleList2) {
					if(!(campaignSchedule.getStatus() == 0)  && !( campaignSchedule.getStatus() == 2 )) {
						logger.info(campaignSchedule.getDateStrByTimeZone(MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
						archiveCampSchedule.add(campaignSchedule);
					}
				}
//				logger.debug("<<<<< Completed getArchivedCampScheduleList .");
				return archiveCampSchedule;
			}



			/**
			 * drawSentListBox
			 * @param campaignSchedule
			 */
			private void drawSentListBox(SMSCampaignSchedule campaignSchedule) {
				logger.debug(">>>>>>> Started  drawSentListBox :: ");
				Listitem li = new Listitem();
				Listcell lc = new Listcell();
				viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(false);
				viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(true);
				viewAllArchivedSchedAnchId.setVisible(true);
				li.setValue(campaignSchedule);
				lc.setParent(li);
				//Date
				lc = new Listcell();
				lc.setLabel(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
				lc.setParent(li);
				//Status
				lc = new Listcell();
				lc.setLabel(campaignSchedule.getStatusStr());
				lc.setParent(li);

				li.setHeight("30px");
				logger.info(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
				li.setParent(sentCampaingsListlbId);
				logger.debug("<<<<< Completed drawSentListBox .");
			}//drawSentListBox
			
			
			/**
			 * onSelect$campListlbId$viewAllActiveSchedulesWinId
			 */
			public void onSelect$campListlbId$viewAllActiveSchedulesWinId() {
				//		logger.debug(">>>>>>> Started  campListlbId$viewAllActiveSchedulesWinId:: ");
				if(viewAllActiveSchedulesWinId$campListlbId.getSelectedCount() == 0){
					viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(true);
					viewAllActiveSchedulesWinId$campActionsBandBoxId.setButtonVisible(false);
				}else if(viewAllActiveSchedulesWinId$campListlbId.getSelectedCount() > 0) {
					viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(false);
					viewAllActiveSchedulesWinId$campActionsBandBoxId.setButtonVisible(true);
				}
				//		logger.debug("<<<<< Completed onSelect$campListlbId$viewAllActiveSchedulesWinId .");
			}//onSelect$campListlbId$viewAllActiveSchedulesWinId
			/**
			 * onClick$delSelectedId$viewAllActiveSchedulesWinId
			 */
			public void onClick$delSelectedId$viewAllActiveSchedulesWinId()  {
				//		logger.debug(">>>>>>> Started  onClick$delSelectedId$viewAllActiveSchedulesWinId :: ");
				try {
					int count = viewAllActiveSchedulesWinId$campListlbId.getSelectedCount();
					if(logger.isDebugEnabled()) 
						logger.debug("Number of selected campaigns schedules to delete :" + count);
					if(count == 0) {
						Messagebox.show("Please select at least one schedule to delete.", "Information" , 
								Messagebox.OK, Messagebox.INFORMATION);
						return;
					}
					String msg = "You have chosen "+count+" schedules to delete. Do you want to continue?";
					boolean found = false;
					Set<Listitem> selList = viewAllActiveSchedulesWinId$campListlbId.getSelectedItems();
					logger.info("Selected list of items are ..:"+viewAllActiveSchedulesWinId$campListlbId.getSelectedCount());
					SMSCampaignSchedule campaignSchedule;
					List<SMSCampaignSchedule> campaignSchedules = new ArrayList<SMSCampaignSchedule>();
					for (Listitem li : selList) {
						campaignSchedule = (SMSCampaignSchedule)li.getValue();
						campaignSchedules.add(campaignSchedule);
						
						if(campaignSchedule.getStatus() == OCConstants.ACTIVE_EMAIL_STATUS || 
								campaignSchedule.getStatus() == OCConstants.DRAFT_EMAIL_STATUS) {
							found = true;
						}
					}
					if(found) {
						//msg = "Some of the selected campaigns are 'Active/Running', \n do you want to continue?";
						msg = "You have chosen "+count+" schedules to delete. Do you want to continue?";
						logger.info("msgmsg"+msg);
					}
					try {
						int confirm =Messagebox.show(msg, "Delete Confirmation",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

						if(confirm == Messagebox.OK){
							if(deleteCampaignsSchedules(campaignSchedules).equals("success")){
								smsCampaign.setStatus(getCampaignStatus(smsCampaign));
								//smsCampaignsDao.saveOrUpdate(smsCampaign);
								smsCampaignsDaoForDML.saveOrUpdate(smsCampaign);
								MessageUtil.setMessage(count+" schedule(s) deleted successfully.", "green", "TOP");
							}
						}
						else {
							viewAllActiveSchedulesWinId$campActionsBandBoxId.setDisabled(true);
						}
					} catch (Exception e) {
						logger.error("Exception ::", e);
					}	
					viewAllActiveSchedulesWinId$campListlbId.clearSelection();
					logger.info("Item count ::"+viewAllActiveSchedulesWinId$campListlbId.getItemCount());
				} catch (Exception ex) {
					logger.error("** Exception :" , ex);
				}
				//		logger.debug("<<<<< Completed onClick$delSelectedId$viewAllActiveSchedulesWinId .");
			}//onClick$delSelectedId$viewAllActiveSchedulesWinId
			
			private String getCampaignStatus(SMSCampaigns campaign) {

				if(smsCampScheduleList == null || smsCampScheduleList.size() == 0) {
					logger.debug("All Active or Draft Schedules are deleted.");
					return Constants.CAMP_STATUS_DRAFT;
				}
				
				SMSCampaignSchedule latestCampaignSchedule = null;

				for (SMSCampaignSchedule campaignSchedule : smsCampScheduleList) {
					if(latestCampaignSchedule == null) latestCampaignSchedule = campaignSchedule;

					if(campaignSchedule.getScheduledDate().after(latestCampaignSchedule.getScheduledDate())){
						latestCampaignSchedule = campaignSchedule;
					}
				}//for

				if(latestCampaignSchedule.getStatus() == 0 || latestCampaignSchedule.getStatus() == 1 || latestCampaignSchedule.getStatus() == 2) 
					return latestCampaignSchedule.getStatusStr();
				else if(latestCampaignSchedule.getStatus() >= 3)
					return "Schedule Failure";
				else
					return "Draft";

			}

			/**
			 * This method helps to delete campaignScheduleList
			 * @param campaignSchedules
			 * @return message
			 */
			private String deleteCampaignsSchedules(List<SMSCampaignSchedule> campaignSchedules) {
					logger.debug(">>>>>>> Started  deleteCampaignsSchedules :: ");
				try{
					logger.debug(">>>>>>> Started  deleteCampaignsSchedules :: "+campaignSchedules.size());
					//smsCampaignScheduleDao.deleteByCollection(campaignSchedules);
					smsCampaignScheduleDaoForDML.deleteByCollection(campaignSchedules);

					//Performance Problem to DB hits in Loop, Need to Fixed.

					for (SMSCampaignSchedule campaignSchedule : campaignSchedules) {

						Calendar lblDate = (Calendar) campActiveTillDateLbId.getAttribute("setLbDate");
						if(campaignSchedule.getScheduledDate().equals(lblDate)){
							logger.info("Removing attribute");
							campActiveTillDateLbId.removeAttribute("setLbDate");
						}
						removeCampScheduleFromList(campaignSchedule);
						rowsMap.remove(campaignSchedule.getSmsCsId());
						rowMap.remove(campaignSchedule.getSmsCsId());
						//TODO arrange campShcedList, Clear ListBoxes

					}// For Loop

					// Redrawing Lists
					int count = activeCampaingsListlbId.getItemCount();
					for(; count>0; count--) {
						activeCampaingsListlbId.removeItemAt(count-1);
					}
					count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
					for(; count>0; count--) {
						viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
					}
					if(smsCampScheduleList != null && smsCampScheduleList.size() > 0){

						List<SMSCampaignSchedule> activeCampScheduleList = getActiveCampScheduleList(smsCampScheduleList);
						if(activeCampScheduleList.size() > 0){
							createRowUpComingListBox(activeCampScheduleList.get(0), 0, true);
							createDivUpComingCampaigns(true,activeCampScheduleList.get(0));
							noOfTimeRedraw = 0;
							activeSchCount = 100;
							redraw(0, activeSchCount);
						}
						else{
							createDivUpComingCampaigns(false,null);
						}
						//redraw(0, activeSchCount);
					}
					else{
						createDivUpComingCampaigns(false,null);
					}

					// Triggered Email
/*
					UserCampaignExpiration triggeredAlert = getTriggeredAlertEmail();
					for (SMSCampaignSchedule campaignSchedule : campaignSchedules) {
						if(triggeredAlert != null){
							Calendar expireOn = triggeredAlert.getSentOn();
							if(expireOn !=null){
								expireOn.add(Calendar.DAY_OF_YEAR, 7);
								if(campaignSchedule.getScheduledDate().equals(expireOn)){

									//find the schedule which is last but one in the periodical schedules
									//just mark the schedule as delete one
									logger.debug("in deleted ====="+expireOn.get(Calendar.DATE));
									triggeredAlert.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SCHEDULE_DELEATED);
									triggeredAlert.setModifiedDate(Calendar.getInstance());
									userCampaignExpirationDao.saveOrUpdate(triggeredAlert);
								}
							}
						}
					}

*/
					//			logger.debug("<<<<< Completed deleteCampaignsSchedules .");
					return "success";
				} catch (Exception ex) {
					logger.error("** Exception :" , ex);
					//			logger.debug("<<<<< Completed deleteCampaignsSchedules .");
					return null;
				}
			}//deleteCampaignsSchedules

			/**
			 * This method handles the on click view more event
			 */
			public void onClick$viewMoreActiveSchedAnchId$viewAllActiveSchedulesWinId(){
				//		logger.debug(">>>>>>> Started  onClick$viewMoreActiveSchedAnchId$viewAllActiveSchedulesWinId :: ");
				noOfTimeRedraw = noOfTimeRedraw + 1;
				redraw(activeSchCount,activeSchCount+100);
				activeSchCount = activeSchCount + 100;
				logger.info("now records are " + activeSchCount);
				//		logger.debug("<<<<< Completed onClick$viewMoreActiveSchedAnchId$viewAllActiveSchedulesWinId .");

			}//onClick$viewMoreActiveSchedAnchId$viewAllActiveSchedulesWinId
			
			
			/**
			 * This method handles on click on viewAllArchivedSchedAnchId
			 */
			public void onClick$viewAllArchivedSchedAnchId(){
				//		logger.debug(">>>>>>> Started  onClick$viewAllArchivedSchedAnchId :: ");
				try {
					viewAllArchivedSchedulesWinId.setVisible(true);
					viewAllArchivedSchedulesWinId.doHighlighted();
					viewAllArchivedSchedulesWinId.setVisible(true);
					sentSchCount = 100;
					int count =  viewAllArchivedSchedulesWinId$campListlbId.getItemCount();
					for(; count>0; count--) {
						viewAllArchivedSchedulesWinId$campListlbId.removeItemAt(count-1);
					}
					redrawSent(0,sentSchCount);
					viewAllArchivedSchedulesWinId.setStyle("scroll:auto;");
				} catch (Exception e) {
					logger.error("Exception ",e);
				}
				//		logger.debug("<<<<< Completed onClick$viewAllArchivedSchedAnchId .");
			}//onClick$viewAllArchivedSchedAnchId
			
			/**
			 * onClick$submitBtnId$viewAllArchivedSchedulesWinId
			 */
			public void onClick$submitBtnId$viewAllArchivedSchedulesWinId(){
			//	logger.debug(">>>>>>> Started  onClick$submitBtnId$viewAllArchivedSchedulesWinId:: ");
				int count =  viewAllArchivedSchedulesWinId$campListlbId.getItemCount();
				for(; count>0; count--) {
					viewAllArchivedSchedulesWinId$campListlbId.removeItemAt(count-1);
				}
				viewAllArchivedSchedulesWinId.setClosable(false);
				viewAllArchivedSchedulesWinId.setVisible(false);
			//	logger.debug("<<<<< Completed onClick$submitBtnId$viewAllArchivedSchedulesWinId .");
			}//onClick$submitBtnId$viewAllArchivedSchedulesWinId

			/**
			 * onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId
			 */
			public void onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId(){
				//		logger.debug(">>>>>>> Started  onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId :: ");
				redrawSent (sentSchCount,sentSchCount+100);
				sentSchCount = sentSchCount + 100;
				logger.info("now records are " + sentSchCount);
				//		logger.debug("<<<<< Completed onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId .");
			}//onClick$viewMoreArchievedSchedAnchId$viewAllArchivedSchedulesWinId
			
			/**
			 * 
			 * @param startIndex
			 * @param size
			 */
			public void redrawSent1(int startIndex, int size) {/*
			//	logger.debug(">>>>>>> Started  redrawSent :: ");
				logger.info("Redrawing sent with start : "+startIndex+"\t End index :"+size);
				//TimeZone timeZone = (TimeZone) sessionScope.get("clientTimeZone");
				List<SMSCampaignSchedule> campaignScheduleSentList =campaignScheduleDao.findAllSentSchedules(startIndex,size,campaign.getCampaignId());

				if(campaignScheduleSentList != null && campaignScheduleSentList.size() >0){
					logger.info("Archived CampSched List size ..:"+campaignScheduleSentList.size());
					logger.info(MyCalendar.calendarToString(campaignScheduleSentList.get(campaignScheduleSentList.size()-1).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT));
					Listitem li;
					Listcell lc;
			//		logger.info("before for loop"+campaignScheduleList.size());
					for (int i = (campaignScheduleList.size()-1);i >=0; i--) {
					for (int i = 0;i <= campaignScheduleSentList.size()-1; i++) {
						li = new Listitem();
						lc = new Listcell();
						//For Not Active and Draft Schedules
						if(!(0 == campaignScheduleSentList.get(i).getStatus()) && !( 2 == campaignScheduleSentList.get(i).getStatus())){
							//Insufficient Credits / Subscription Expired,Insufficient Promo-codes,Promo-code Expired / Paused
						//	logger.info("..................."+campaignScheduleList.get(i).getStatus());
							viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(false);
							viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(true);
							viewAllArchivedSchedAnchId.setVisible(true);
							li.setValue(campaignScheduleSentList.get(i));
							lc.setParent(li);
							//Date
							lc = new Listcell();
							lc.setLabel(MyCalendar.calendarToString(campaignScheduleSentList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT));
							lc.setParent(li);
							//Status
							lc = new Listcell();
							lc.setLabel(campaignScheduleSentList.get(i).getStatusStr());
							lc.setParent(li);

							li.setHeight("30px");
							int count =  sentCampaingsListlbId.getItemCount();
						//	logger.info("countcount :"+count);
							if(count == 0){
								logger.info(MyCalendar.calendarToString(campaignScheduleSentList.get(i).getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT));
								li.setParent(sentCampaingsListlbId);
							}else{
								li.setParent(viewAllArchivedSchedulesWinId$campListlbId);
							}
						}//if

					}//for
					logger.info("Completed For Loop");
					
				}//if list is not null
				else{
					viewAllArchivedSchedulesWinId$noRecordsArchivedLbId.setVisible(true);
					viewAllArchivedSchedulesWinId$viewMoreArchievedSchedAnchId.setVisible(false);
					logger.error("No Campaing's existing.........");
				}
//				logger.debug("<<<<< Completed redrawSent .");
			*/}//redrawSent

			/**
			 * This method draw Archived Schedules
			 * @param campaignSchedule
			 */
			private void drawArchivedDiv(SMSCampaignSchedule campaignSchedule){
//				logger.debug(">>>>>>> Started  drawArchivedDiv :: ");
				if(!(0 == campaignSchedule.getStatus()) && !( 2 == campaignSchedule.getStatus())){
					//	logger.info("campaignSentDivId.setVisible(true)");
					campaignSentDivId.setVisible(true);
					campaignSentLbId.setValue(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
					logger.info(MyCalendar.calendarToString(campaignSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT),(TimeZone) sessionScope.get("clientTimeZone"));
					campaignSentLbId.setStyle(OCConstants.FONT_BOLD);
				}
				else{
					campaignSentDivId.setVisible(false);
				}
//				logger.debug("<<<<< Completed drawArchivedDiv .");
			}//drawArchivedDiv
	
			private void createDivUpComingCampaigns(boolean flag, SMSCampaignSchedule schedule) {
				logger.debug(">>>>>>> Started  createDivUpComingCampaigns :: ");
				
				
				List<SMSCampaignSchedule> activeOrDraftCampScheduleList = getActiveCampScheduleList(smsCampScheduleList);
				
				SMSCampaignSchedule lastSchedule = getLastSchedule(activeOrDraftCampScheduleList);
				
				if(lastSchedule != null){
					campActiveTillDateLbId.setValue(MyCalendar.calendarToString(lastSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
					campActiveTillDateLbId.setAttribute("setLbDate",lastSchedule.getScheduledDate());
					campaignActiveTillDivId.setVisible(true);
					String size = (smsCampScheduleList != null && smsCampScheduleList.size() > 0) ? getSize(smsCampScheduleList) : 1+"";  
					//int allActiveSchedules = getAcitveScheduleSize(campScheduleList);
					
					numOfTimeCampActiveLbId.setValue(size +" schedule(s)");
					numOfTimeCampActiveLbId.setStyle(OCConstants.FONT_BOLD);
					logger.info("persistentCamp Date .....: "+MyCalendar.calendarToString(lastSchedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
					
				}
				else{
					campaignActiveTillDivId.setVisible(false);
				}
				
				
				/*if(activeOrDraftCampScheduleList.size() > 0){
					
					if(persistentCamp){
					campActiveTillDateLbId.setValue(MyCalendar.calendarToString(activeOrDraftCampScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
					campActiveTillDateLbId.setAttribute("setLbDate",activeOrDraftCampScheduleList.get(0).getScheduledDate());
					//logger.info("persistentCamp Date .....: "+MyCalendar.calendarToString(activeOrDraftCampScheduleList.get(0).getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT, (TimeZone) sessionScope.get("clientTimeZone")));
					}
					else{
						
						try {
							Calendar lblDate = (Calendar) campActiveTillDateLbId.getAttribute("setLbDate");
						//	logger.info(".........:"+lblDate);
							if(lblDate != null ){
								//Calendar cal = MyCalendar.string2Calendar(lblDate);
							//	logger.info("calcal.....:"+lblDate);
								if(schedule != null && lblDate.before(schedule.getScheduledDate())){
									//logger.info("cal.after(schedule) time ::"+MyCalendar.calendarToString(schedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone"))); //schedule.getDateStrByTimeZone(MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
									campActiveTillDateLbId.setValue(Constants.STRING_NILL);
									campActiveTillDateLbId.setValue(MyCalendar.calendarToString(schedule.getScheduledDate(), MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
									campActiveTillDateLbId.setAttribute("setLbDate",schedule.getScheduledDate());
									
								}
								else{
									//logger.info("cal time ::"+MyCalendar.calendarToString(lblDate,MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
									campActiveTillDateLbId.setValue(MyCalendar.calendarToString(lblDate,MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
									campActiveTillDateLbId.setAttribute("setLbDate",lblDate);
								}
								
							}
							else{
								//logger.info("Setting Camp Schedule first time ::"+MyCalendar.calendarToString(schedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
								campActiveTillDateLbId.setValue(MyCalendar.calendarToString(schedule.getScheduledDate(),MyCalendar.FORMAT_DATE_FORMAT,(TimeZone) sessionScope.get("clientTimeZone")));
								campActiveTillDateLbId.setAttribute("setLbDate",schedule.getScheduledDate());
							}
							//return true;
						} catch (Exception e) {
							logger.error("Exception :",e);
						}
						
						
							}
					campActiveTillDateLbId.setStyle(OCConstants.FONT_BOLD);

					String size = (campScheduleList != null && campScheduleList.size() > 0) ? getSize(campScheduleList) : 1+"";  
					//int allActiveSchedules = getAcitveScheduleSize(campScheduleList);
					
					numOfTimeCampActiveLbId.setValue(size +" schedule(s)");
					numOfTimeCampActiveLbId.setStyle(OCConstants.FONT_BOLD);
					campaignActiveTillDivId.setVisible(flag);
				}
				campaignActiveTillDivId.setVisible(flag);*/
				logger.debug("<<<<< Completed createDivUpComingCampaigns .");
				
				/*for (SMSCampaignSchedule campaignSchedule : activeOrDraftCampScheduleList) {
					if(campaignSchedule.getStatus() == 2){
					//	campaignActiveTillDivId.setVisible(false);
					}
				}*/
			}



			private SMSCampaignSchedule getLastSchedule(List<SMSCampaignSchedule> activeOrDraftCampScheduleList) {
				SMSCampaignSchedule lastestCampaignSchedule = null;
				
				for (SMSCampaignSchedule smsCampaignSchedule : activeOrDraftCampScheduleList) {
					
					if(lastestCampaignSchedule == null ) lastestCampaignSchedule = smsCampaignSchedule ;
					
					if(smsCampaignSchedule.getScheduledDate().after(lastestCampaignSchedule.getScheduledDate())){
						lastestCampaignSchedule = smsCampaignSchedule;
					}
				}
				
				return lastestCampaignSchedule;
			}


	private SMSCampaignSchedule addDateCheck(Calendar selectedDtCal,
			SMSCampaignSchedule parentCampSchedule, byte criteria) {	
		
		logger.debug("-------- just entered---------");
		
		selectedDtCal.set(Calendar.SECOND,0);
		selectedDtCal.set(Calendar.MILLISECOND,0);

		 Calendar tempCal = Calendar.getInstance();
		 //STARTS 3, to make similar to email campaign schedules
		 //tempCal.setTimeZone((TimeZone) sessionScope.get("clientTimeZone"));
		//ENDS 3, to make similar to email campaign schedules
		tempCal.setTime(selectedDtCal.getTime());
		
		//STARTS 4, to make similar to email campaign schedules
		logger.info("######==========>"+tempCal.get(Calendar.HOUR_OF_DAY));
		//ENDS 4, to make similar to email campaign schedules
		
		
		//Long smsCsId = smsCampaignScheduleDao.getCurrentId();
		
		
		/*if(logger.isDebugEnabled()) {
			logger.debug(">>>>>>>>> Generated Id :"+parentCampSchedule.getSmsCsId());
		}*/
		
		
		//STARTS 5, to make similar to email campaign schedules
		//Following is commented and in place of that next line is written.
		SMSCampaignSchedule smsCampSchedule = new SMSCampaignSchedule(tempCal,criteria, currUser.getUserId()); //--1
		//SMSCampaignSchedule smsCampSchedule = new SMSCampaignSchedule(smsCsId, tempCal, criteria);
		//ENDS 5, to make similar to email campaign schedules
		
		if(parentCampSchedule != null) { // if parent is not exists
			
			if(logger.isDebugEnabled()) {
				logger.debug(">>>>>> parent Id :"+parentCampSchedule.getSmsCsId()+" "+parentCampSchedule.getResendLevel());
			}
			
			//STARTS 6, to make similar to email campaign schedules
			//earlier, commented line was there.
			//smsCampSchedule.setParentId(null);
			smsCampSchedule.setParentId(parentCampSchedule.getSmsCsId());
			//ENDS 6, to make similar to email campaign schedules
			
			smsCampSchedule.setResendLevel((byte)(parentCampSchedule.getResendLevel()+1));
		} 
		else {
			smsCampSchedule.setResendLevel((byte)0);
		}// else if parentCampSchedule
		
		if(smsCampScheduleList.contains(smsCampSchedule)) {
			MessageUtil.setMessage("Schedule added on same date and time.\n Please select a different time.", "color:red", "TOP");
			return null;
		}
		//logger.info("smsCampaign is====>"+smsCampaign+"");
		//TODO 
		
		//STARTS 7, to make similar to email campaign schedules
		int available = 0;
		try {
			SubscriptionDetails subDetails = new SubscriptionDetails();
			available = subDetails.getSMSStatus(smsCampSchedule.getScheduledDate());
		} catch (Exception e1) {
			logger.error("** Exception : Problem while getting the subscription details", e1);
			return null;
		}
		if(available <=0) {
			MessageUtil.setMessage("SMS cannot be scheduled as you have \n " +
					"reached your sms credits limit or your account has expired.", 
					"color:red", "TOP");
			return null;
		}

        try{
        	if(smsCampSchedule != null && smsCampSchedule.getScheduledDate().after(currUser.getPackageExpiryDate())) {
    			MessageUtil.setMessage("Schedule date cannot be after your package expiry date.", "color:red", "TOP");
    			return null;
    		}
        }catch(Exception e){
        	logger.error("Exception >>>>>>>> ", e);
        }
		
		//ENDS 7, to make similar to email campaign schedules
		
		smsCampSchedule.setSmsCampaignId(smsCampaign.getSmsCampaignId());
		
		smsCampSchedule.setStatus((byte)0);
		
		//STARTS 8, to make similar with email campaign schedules
		try{
		  smsCampSchedule.setUserId(currUser.getUserId());
		}catch(Exception e){
			logger.error("Exception >>>>>>>> ", e);
		}
		//ENDS 8, to make similar with email campaign schedules
		
		//smsCampaignScheduleDao.saveOrUpdate(smsCampSchedule);
		//smsCampScheduleList.add(smsCampSchedule);
		//logger.info("Added one Schedule");
		
		if(logger.isDebugEnabled()) {
			logger.debug("-------- before returning---------");
		}
		return smsCampSchedule;
		
	}

	
	private class MyListener implements EventListener {

		@Override
		public void onEvent(Event event) throws Exception {

			String action = (String)event.getTarget().getAttribute(
					SmsCampSettingsController.TB_ACTION);
			Rows rows;
			SMSCampaignSchedule campSchedule;

			if(event.getTarget() instanceof Image) {
				logger.info("Deleting on click of Image Event");
				Image img = (Image)event.getTarget();
				String imageEventName = img.getAttribute("delete").toString();
				Listitem li = (Listitem)img.getParent().getParent().getParent();

				campSchedule = (SMSCampaignSchedule)li.getValue();
				if("campScheduleDelete".equals(imageEventName)){
					/**
					 *  Deletes the row from the rows and removes corresponding
					 *  schedule object from the list when user clicks on delete link 
					 */
					int confirm = Messagebox.show("Are you sure you want to delete the schedule?", 
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

					if(confirm == Messagebox.OK) {
						try {
							logger.info(" >>> 1 >>>>>"+campSchedule.getSmsCsId());
							
							
							// Added 2.3.11 
							//find the triggered email & then compare the 
							//scheduleDate with sent on if it is matched then reduce the time of triggered email
							
							
							UserCampaignExpiration triggeredAlert = getTriggeredAlertEmail();
							if(triggeredAlert != null){
								
								Calendar expireOn = triggeredAlert.getSentOn();
								expireOn.add(Calendar.DAY_OF_YEAR, 7);
								if(campSchedule.getScheduledDate().equals(expireOn)){
									
									//find the schedule which is last but one in the periodical schedules
									//just mark the schedule as delete one
									logger.debug("in deleted ====="+expireOn.get(Calendar.DATE));
									triggeredAlert.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SCHEDULE_DELEATED);
									triggeredAlert.setModifiedDate(Calendar.getInstance());
									userCampaignExpirationDaoForDML.saveOrUpdate(triggeredAlert);
								}
							}
							
							if(campSchedule.getSmsCsId() != null)
								//smsCampaignScheduleDao.deleteByCampSchId(campSchedule.getSmsCsId());
								smsCampaignScheduleDaoForDML.deleteByCampSchId(campSchedule.getSmsCsId());
							
							//smsCampaignScheduleDao.deleteByCampSchId(campSchedule.getSmsCsId());

//							rows = (Rows)currentRow.getParent();
//							rows.removeChild(currentRow);

//							if(rows.getChildren().size() == 0 && rows.getParent().getParent() instanceof Detail) {
//								Detail detail = (Detail)(rows.getParent().getParent()); 
//								rows.getParent().setVisible(false);
//								detail.setOpen(false);
//								detail.setStyle("display:none");
//							}

							//rows.invalidate();
							Calendar lblDate = (Calendar) campActiveTillDateLbId.getAttribute("setLbDate");
							if(campSchedule.getScheduledDate().equals(lblDate)){
								logger.info("Removing attribute");
								campActiveTillDateLbId.removeAttribute("setLbDate");
							}
							removeCampScheduleFromList(campSchedule);

							smsCampaign.setStatus(getCampaignStatus(smsCampaign));
							campaignsDaoForDML.saveOrUpdate(smsCampaign);
							rowsMap.remove(campSchedule.getSmsCsId());
							rowMap.remove(campSchedule.getSmsCsId());
							//TODO arrange campShcedList, Clear ListBoxes
							MessageUtil.setMessage("Schedule deleted successfully.", "green", "TOP");
							
							int count = activeCampaingsListlbId.getItemCount();
							for(; count>0; count--) {
								activeCampaingsListlbId.removeItemAt(count-1);
							}
							count =  viewAllActiveSchedulesWinId$campListlbId.getItemCount();
							for(; count>0; count--) {
								viewAllActiveSchedulesWinId$campListlbId.removeItemAt(count-1);
							}
							if(smsCampScheduleList != null && smsCampScheduleList.size() > 0){

								List<SMSCampaignSchedule> activeCampScheduleList = getActiveCampScheduleList(smsCampScheduleList);
								if(activeCampScheduleList.size() > 0){
									createRowUpComingListBox(activeCampScheduleList.get(0), 0, true);
									createDivUpComingCampaigns(true,activeCampScheduleList.get(0));
									noOfTimeRedraw = 0;
									activeSchCount = 100;
									redraw(0, activeSchCount);
								}
								else{
									createDivUpComingCampaigns(false,null);
								}
								//redraw(0, activeSchCount);
							}
							else{
								createDivUpComingCampaigns(false,null);
							}

							
							
							/*if(smsCampScheduleList != null && smsCampScheduleList.size() > 0){

								List<SMSCampaignSchedule> activeCampScheduleList = getActiveCampScheduleList(smsCampScheduleList);
								if(activeCampScheduleList.size() > 0){
									createRowUpComingListBox(activeCampScheduleList.get(0), 0, true);
									//createDivUpComingCampaigns(true);
								}
								else{
//									createDivUpComingCampaigns(false);
								}
								redraw(0, activeSchCount);
							}
							else{
//								createDivUpComingCampaigns(false);
							}
*/
						} catch (Exception e) {
							logger.error("Exception ::", e);
						}
					}
				}
			}
			else if( event.getTarget() instanceof A) {

				Object srcObject = event.getTarget().getAttribute(ATTRIBUTE_SOURCE);
				if(srcObject instanceof SegmentRules) {

					SegmentRules segmentRule = (SegmentRules)srcObject;
					String str= segmentRule.getSegRuleToView();
					viewSegRuleWinId$segRuleLblId.setValue(str);
					viewSegRuleWinId.setVisible(true);


				}else if(srcObject instanceof MailingList) {

					MailingList mailingList=(MailingList) srcObject;
					Sessions.getCurrent().setAttribute("mailingList",mailingList);

					if(Sessions.getCurrent().getAttribute("viewType") != null )
						Sessions.getCurrent().removeAttribute("viewType");

					Redirect.goTo(PageListEnum.CONTACT_CONTACT_VIEW);


				}
			}
			else if( event.getTarget() instanceof Toolbarbutton && action != null) {

//				Commented for 2.3.11
				currentRow = (Row) event.getTarget().getParent().getParent();
				campSchedule = (SMSCampaignSchedule)currentRow.getValue();

				if(action.equals(SmsCampSetupController.TB_ACTION_RESEND)) {

					// if clicks on 'Add Resend'
				//	errMsgLblId.setVisible(false);
					resendOptionWinId.setVisible(true);

				}
				else if(action.equals(SmsCampSettingsController.TB_ACTION_DELETE)) {

					//if clicks on Delete the schedule
//					*//**
//					 *  Deletes the row from the rows and removes corresponding
//					 *  schedule object from the list when user clicks on delete link 
//					 *//*
					int confirm = Messagebox.show("Are you sure you want to delete the schedule?", 
							"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);

					if(confirm == Messagebox.OK) {

						try {

							//find the triggeredemail & then compare the 
							//scheduleDate with sent on if it is matched then reduce the time of triggered email
							
							
							/*UserCampaignExpiration triggeredAlert = getTriggeredAlertEmail();
							if(triggeredAlert != null){
								
								Calendar expireOn = triggeredAlert.getSentOn();
								expireOn.add(Calendar.DAY_OF_YEAR, 7);
								if(campSchedule.getScheduledDate().equals(expireOn)){
									
									//find the schedule which is last but one in the periodical schedules
									//just mark the schedule as delete one
									logger.debug("in deleted ====="+expireOn.get(Calendar.DATE));
									triggeredAlert.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SCHEDULE_DELEATED);
									triggeredAlert.setModifiedDate(Calendar.getInstance());
									userCampaignExpirationDao.saveOrUpdate(triggeredAlert);
								}
							}*/
							logger.info(" >>> 1 >>>>>"+campSchedule.getSmsCsId());
							//smsCampaignScheduleDao.deleteByCampaignId(campSchedule.getSmsCsId());
							smsCampaignScheduleDaoForDML.deleteByCampaignId(campSchedule.getSmsCsId());


							rows = (Rows)currentRow.getParent();
							rows.removeChild(currentRow);

							if(rows.getChildren().size() == 0 && rows.getParent().getParent() instanceof Detail) {
								Detail detail = (Detail)(rows.getParent().getParent()); 
								rows.getParent().setVisible(false);
								detail.setOpen(false);
								detail.setStyle("display:none");
							}

							//rows.invalidate();
							removeCampScheduleFromList(campSchedule);

							smsCampaign.setStatus(getCampaignStatus(smsCampaign));
							campaignsDaoForDML.saveOrUpdate(smsCampaign);


							rowsMap.remove(campSchedule.getSmsCsId());
							rowMap.remove(campSchedule.getSmsCsId());
							MessageUtil.setMessage("Schedule deleted successfully.", "green", "TOP");

						} catch (Exception e) {
							logger.error("Exception ::", e);
						}

					}



				}
				else if(action.equals(SmsCampSettingsController.TB_ACTION_EDIT)) {/*

					// if clicks on Edit the content of 'Re send schedule'
					resendEditorWinId.setVisible(true);

					HttpServletRequest request = (HttpServletRequest)
							Executions.getCurrent().getNativeRequest();
					request.removeAttribute("campSchedule");
					request.setAttribute("campSchedule", campSchedule);
					request.setAttribute("source", "schedule");
					resendEditorWinId$resendIncId.setSrc("zul/Empty.zul");

					resendEditorWinId$resendIncId.setSrc(
							"/zul/campaign/plainEditor.zul");

				*/}


			}// if toolbarbutton event

			else if(event.getTarget() instanceof Detail) {

				//Commented for 2.3.11
				Detail detail = (Detail) event.getTarget();
				Grid subGrid = (Grid)detail.getFirstChild();
				rows = subGrid.getRows();

				if(rows.getChildren().size() == 0) {
					rows.getParent().setVisible(false);
				}else {
					List list = rows.getChildren();

					for(Object obj:list){
						Row row=(Row)obj;
						SMSCampaignSchedule campShcedule=(SMSCampaignSchedule)row.getValue();

						List<Object[]> childList=smsCampaignScheduleDao.getAllChidren(campShcedule.getSmsCsId(),campShcedule.getSmsCampaignId());
						if(childList!=null) {
							logger.debug("/list size is"+childList.size()+"*****"+((Detail)row.getChildren().get(0)).isOpen());
							detail=row.getDetailChild();
							detail.setStyle("display:block;");
							detail.addEventListener("onOpen", myListener);
							//Grid grid=(Grid)detail.getChildren().get(0);

							//((Detail)row.getChildren().get(0)).setOpen(true);//detail.setStyle("display:block;");


						} //if
					}//for

					rows.getParent().setVisible(true);
				}//else
			}//else if
			
			else if(event.getTarget() instanceof Label){
				
				logger.info("included promo code clicked>>>>>>>>>>>>>>>>");
				Redirect.goTo(PageListEnum.ADMIN_VIEW_COUPONS);
			
			}
		}//onEvent
		
		private UserCampaignExpiration getTriggeredAlertEmail() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getCampaignStatus(SMSCampaigns campaign) {
			


			//String status = campaign.getStatus();

		//	SMSCampaignScheduleDao campaignScheduleDao = (SMSCampaignScheduleDao)SpringUtil.getBean("campaignScheduleDao");

			//	List<SMSCampaignSchedule> scheduleList = campaignScheduleDao.findAllByCampaignId(campaign.getCampaignId(), campaign.getUsers().getUserId());
			//TimeZone clientTimeZone = (TimeZone) sessionScope.get("clientTimeZone");



			if(smsCampScheduleList == null || smsCampScheduleList.size() == 0) {
				logger.debug("All Active or Draft Schedules are deleted.");
				return Constants.CAMP_STATUS_DRAFT;
			}

			//boolean draftStatus = campaign.getStatus().equalsIgnoreCase("Draft") ;
			//logger.info("scheduleList.size()="+scheduleList.size()+"   scheduleList="+scheduleList);
			Calendar startCal =null;
			Calendar endCal = null;


			//logger.info("Schedule List status###" + ((SMSCampaignSchedule)(scheduleList.get(0))).getStatus());
			SMSCampaignSchedule latestSMSCampaignSchedule = null;

			for (SMSCampaignSchedule campaignSchedule : smsCampScheduleList) {
				if(latestSMSCampaignSchedule == null) latestSMSCampaignSchedule = campaignSchedule;

				if(campaignSchedule.getScheduledDate().after(latestSMSCampaignSchedule.getScheduledDate())){
					latestSMSCampaignSchedule = campaignSchedule;
				}

				if(campaignSchedule.getStatus() != 0 ) continue;

				if(startCal== null && endCal== null){
					startCal = campaignSchedule.getScheduledDate();
					endCal = campaignSchedule.getScheduledDate();


				}

				if(endCal != null && endCal.before(campaignSchedule.getScheduledDate())){
					endCal = campaignSchedule.getScheduledDate();
				}
				if(startCal.after(campaignSchedule.getScheduledDate())){
					startCal = campaignSchedule.getScheduledDate();
				}
			}//for



			if(latestSMSCampaignSchedule.getStatus() == 0 || latestSMSCampaignSchedule.getStatus() == 1 || latestSMSCampaignSchedule.getStatus() == 2) 
				return latestSMSCampaignSchedule.getStatusStr();
			else if(latestSMSCampaignSchedule.getStatus() >= 3)
				return "Schedule Failure";
			else
				return "Draft";

		}
		
		
		
		
		
		
	}// class EventListener

	
	
	private boolean isCampaignDraftedInLastStep_OR_Sent(SMSCampaigns smsCampaign){ 
		
		if(smsCampaign == null)
			return false;
		
		List<SMSCampaignSchedule> smsCampaignScheduleList = smsCampaignScheduleDao.getBySmsCampaignId(smsCampaign.getSmsCampaignId());  
		
		if(smsCampaignScheduleList == null || smsCampaignScheduleList.size() == 0){
			return false;
		}
		
		for(SMSCampaignSchedule aSMSCampaignSchedule : smsCampaignScheduleList){
			if(aSMSCampaignSchedule.getStatus() == 1 || aSMSCampaignSchedule.getStatus() == 2){
				return true;
			}
		}

		return false;
		
	}
	
	public String addingHeaderFoorter(String msgContent,int charCount){
        if(SMSStatusCodes.optOutFooterMap.get(currUser.getCountryType()) && smsSettings != null) {
            
            logger.info("msgContent.length()"+msgContent.length());
            SMSSettings optinSettings = null;
              SMSSettings optOutSettings = null;
              SMSSettings helpSettings = null;
              
              String messageHeader = Constants.STRING_NILL;
              for (SMSSettings eachSMSSetting : smsSettings) {
                  
                  if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
                  else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
                  else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
                  
              }
            if(optinSettings != null && messageHeader.isEmpty()){
                messageHeader = optinSettings.getMessageHeader();
                msgContent = messageHeader + "\n" + msgContent;
            }
            else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
            else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
            
            //charCount = charCount + (messageHeader != null ? messageHeader.length()+1 : 0);//TODO set perfection
            
            //headerLblId.setValue(messageHeader);
            //String optoutKeyWord = smsSettings.getOptoutKeyword();
            //if(entireListRId.isChecked() && optOutSettings != null) 
            if(!msgTypeRgId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL) 
						&& entireListRId.isChecked() && optOutSettings != null){
                //charCount = charCount + 1+(optOutLblId.getValue().length());//("Reply "+optOutSettings.getKeyword()+" 2 Optout" );
            	if(!currUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_UAE)) {
                msgContent = msgContent+ (optOutSettings.getKeyword() != null ?
                        ("\n"+"Reply " + optOutSettings.getKeyword() + " 2 Optout")
                                : "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
            	}else {
            		 msgContent = msgContent+ (optOutSettings.getKeyword() != null ?
                             ("\n"+ optOutSettings.getKeyword())
                                     : "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
            	}

            }
            //charCount = msgContent.length();
            
            //if(smsSettings==null && entireListRId.isChecked()) 
            if(smsSettings==null &&!(msgTypeRgId.getSelectedItem().getValue().toString().equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL))
						&& entireListRId.isChecked() && optOutSettings != null){
                msgContent = msgContent+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent");

            }
            charCount = msgContent.length();
            logger.info("charCount-----***"+charCount);
        }
        return msgContent;
    }
}
