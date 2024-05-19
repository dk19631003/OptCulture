package org.mq.marketer.campaign.controller.contacts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.OrgSMSkeywords;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.ReferralProgram;

import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.SmsQueue;
import org.mq.marketer.campaign.beans.TransactionalTemplates;
import org.mq.marketer.campaign.beans.UrlShortCodeMapping;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.layout.EditorController;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.AutoSMSDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.OrgSMSkeywordsDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SmsQueueDao;
import org.mq.marketer.campaign.dao.SmsQueueDaoForDML;
import org.mq.marketer.campaign.dao.TransactionalTemplatesDao;
import org.mq.marketer.campaign.dao.TransactionalTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.UrlShortCodeMappingDao;
import org.mq.marketer.campaign.dao.UrlShortCodeMappingDaoForDML;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.ReferralProgramDao;

import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ManageAutoSmsController extends GenericForwardComposer{

	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Set<Long> listIdsSet; 
	private Listbox typeOfAutoSMSListLbId,insertCouponLbId,insertKeywordLbId,insertMergetagsLbId,multipleSenderIdsLbId;
	private Textbox linkURLTxtBoxId,caretPosTB,SMSMsgTbId,charCountTbId,mblNoTxtBoxId,senderIdTbId,
	createNewTemplateWinId$newTempNameTbId,testSmsWinId$smsTbId;
	private Label createNewTemplateWinId$msgLblId,headerLblId,optOutLblId;
	private Combobox msgListCmbBxId;
	private Users currentUser;
	private MailingListDao mailingListDao;
	private CouponsDao couponsDao;
	
	private ReferralProgramDao referralprogramDao;

	private OrgSMSkeywordsDao orgSMSkeywordsDao;
	private UrlShortCodeMappingDao urlShortCodeMappingDao; 
	private UrlShortCodeMappingDaoForDML urlShortCodeMappingDaoForDML;
	private AutoSMSDao autoSMSDao;
	private AutoSMSDaoForDML autoSMSDaoForDML;
	private SMSSettingsDao smsSettingsDao;
	private TransactionalTemplatesDao  transactionalTemplatesDao;
	private TransactionalTemplatesDaoForDML  transactionalTemplatesDaoForDML;
	private EmailQueueDao emailQueueDao ;
	private EmailQueueDaoForDML emailQueueDaoForDML ;
	private List<SMSSettings> smsSettings;
	private Window createNewTemplateWinId,testSmsWinId;
	private Div outBoundedDivId,multipleSenderIdDivId,insertKeywrdDivId,insertCouponDivId;
	private Button deleteBtnId;
	private List<AutoSMS> configuredAutoSMSList;
	//private LoyaltyProgramService ltyPrgmSevice;
	private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
	private static final String LOYALTYREGISTRATION="loyaltyRegistration";
	private static final String GIFTCARDISSUANCE="giftCardIssuance";
	private static final String TIERUPGRADATION="tierUpgradation";
	private static final String EARNEDBONUS="earningBonus";
	private static final String EARNEDREWARDEXPIRATION="earnedRewardExpiration";
	private static final String MEMBERSHIPEXPIRATION="membershipExpiration";
	private static final String GIFTAMOUNTEXPIRATION="giftAmountExpiration";
	private static final String GIFTCARDEXPIRATION="giftCardExpiration";
	private static final String FEEDBACKFORM="feedbackform";
	private static final String SPECIALREWARDS="specialRewards";
	private static final String WELCOMESMS="welcomeSms";
	private static final String LOYALTYADJUSTMENT="loyaltyAdjustment";
	private static final String LOYALTYISSUANCE="loyaltyIssuance";
	private static final String LOYALTYREDEMPTION="loyaltyRedemption";
	private static final String ERECEIPTMESSAGES="E-RECEIPTMESSAGE ";
	private static final String OTPMESSAGES="OTPMESSAGE";
	private static final String REDEMPTIONOTP="redemptionOtp";
	private static final String ISSUECOUPONSMS="IssueCouponSMS";
	
	
	private List<UserSMSSenderId> listOfMultipleSenderIds;
	private MyListener myListener = new MyListener();

	public ManageAutoSmsController () {

		try {
			smsSettingsDao = (SMSSettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			currentUser = GetUser.getUserObj();
			session = Sessions.getCurrent();
			captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)ServiceLocator.getInstance().getBeanByName("captiwayToSMSApiGateway");
			//ltyPrgmSevice = new LoyaltyProgramService();
			if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType())) { 
				smsSettings = smsSettingsDao.findByUser(currentUser.getUserId());
			}
			listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
		} catch (Exception e) {
			logger.debug("Exception ::",e);
		}
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
		super.doAfterCompose(comp);

		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Auto-SMS","",style,true);

	//	SMSMsgTbId.setCtrlKeys("^v");
		//SMSMsgTbId.addEventListener(Events.ON_CTRL_KEY, myListener);
	//	SMSMsgTbId.addEventListener(Events.ON_RIGHT_CLICK, myListener);

		fetchConfiguredAutoSMS();
		// populate template list and types

		String  typeOfAutoSmsStr= PropertyUtil.getPropertyValueFromDB("AutoSms");
		logger.info("autoTypeStr>>>>>>>>>>>>"+typeOfAutoSmsStr);
		if(typeOfAutoSmsStr == null){
			return;
		}
		String[] autoTypeStr = typeOfAutoSmsStr.split(Constants.ADDR_COL_DELIMETER);
		Listitem li = null;
		String value = null;
		/*for (String eachType : autoTypeStr) {

			value = null;
			boolean isSbToOc=true;
			if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REGISTRATION) ){
				value=OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REGISTRATION;
			}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTISSUE;
				isSbToOc=false;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_TIER_UPGRADATION)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_TIERUPGRADE;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REWARDAMTEXPIRY;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_LOYALTYMEMBSHIPEXPIRY;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_BONUS)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_BONUS;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTAMTEXPIRY;
				isSbToOc=false;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTMEMBSHIPEXPIRY;
				isSbToOc=false;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_WEB_FORMS)) {
				value =OCConstants.FEEDBACK_WEBFORM;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_SPECIAL_REWARDS)) {
				value =OCConstants.SPECIALREWARDS;
			}
			
			if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
				if(isSbToOc){ 
					li = new Listitem(eachType, value);
				}else continue;
			}
			else {
				li = new Listitem(eachType, value);
			}
			
			
			//li = new Listitem(eachType, value);
			li.setParent(typeOfAutoSMSListLbId);

		}
		if(typeOfAutoSMSListLbId.getItemCount() > 0){
			typeOfAutoSMSListLbId.setSelectedIndex(0);
		}*/
		typeOfAutoSMSListLbId.clearSelection();
		for (String eachType : autoTypeStr) {
			value = null;
			if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REGISTRATION) ){
				value=OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REGISTRATION;
			}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTISSUE;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_TIER_UPGRADATION)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_TIERUPGRADE;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_REWARDAMTEXPIRY;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_LOYALTYMEMBSHIPEXPIRY;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_BONUS)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_BONUS;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTAMTEXPIRY;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
				value =OCConstants.LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTMEMBSHIPEXPIRY;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_WEB_FORMS)) {
				value =OCConstants.FEEDBACK_WEBFORM;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_SPECIAL_REWARDS)) {
				value =OCConstants.SPECIALREWARDS;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_WELCOME_SMS)) {
				value =OCConstants.WELCOMESMS;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT)) {
				value =OCConstants.LOYALTYADJUSTMENT;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_ISSUANCE)) {
				value =OCConstants.LOYALTYISSUANCE;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REDEMPTION)) {
				value =OCConstants.LOYALTYREDEMPTION;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_ERECEIPT_MESSAGE)) {
				value =OCConstants.ERECEIPTMESSAGES;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_OTP_MESSAGE)) {
				value =OCConstants.OTPMESSAGES;
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_REDEMPTION_OTP)) {
				value =OCConstants.REDEMPTIONOTP;
		
			}else if(eachType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_ISSUECOUPON_SMS)) {
			
				value =OCConstants.ISSUECOUPONSMS;
		
			}
		
			
			li = new Listitem(eachType, value);
			li.setParent(typeOfAutoSMSListLbId);
			

		}
		if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
			for (Listitem item : typeOfAutoSMSListLbId.getItems()) {
					if (item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE) || 
							item.getLabel().equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION) || 
							item.getLabel().equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
						item.setVisible(false);
					}
					
				}
			}

		if(typeOfAutoSMSListLbId.getItemCount() > 0){
			typeOfAutoSMSListLbId.setSelectedIndex(0);
		}
		
		//set senderId
		try{
			
			listOfMultipleSenderIds = getMultipleSenderIds();
			if(listOfMultipleSenderIds.size() >= 2){
				populateMultipleSenderIds();// multiple senderIds
			}
			else
			{
				UserSMSSenderId userSMSSenderId = getsenderIds();
				if(userSMSSenderId != null) {
					senderIdTbId.setText(userSMSSenderId.getSenderId());
					senderIdTbId.setDisabled(true);
				}
			}
		}catch(Exception e){
			logger.error("Exception  >>> ",e);
		}

		//UserSMSSenderId userSMSSenderId = getsenderIds();
		//if(userSMSSenderId != null) {
		//		senderIdTbId.setText(userSMSSenderId.getSenderId());
		//		senderIdTbId.setDisabled(true);
		

		//differentiate the Indian/US customer

	/*	if(currentUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {
			outBoundedDivId.setVisible(false);
		}else {*/
		if(currentUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {
			insertKeywrdDivId.setVisible(false);
		}
			outBoundedDivId.setVisible(true);

			//set message header
			if(smsSettings != null) {

				SMSSettings optinSettings = null;
				SMSSettings optOutSettings = null;
				SMSSettings helpSettings = null;

				String messageHeader = Constants.STRING_NILL;
				for (SMSSettings eachSMSSetting : smsSettings) {

					if(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN.equalsIgnoreCase(eachSMSSetting.getType())) optinSettings = eachSMSSetting;
					else if(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT.equalsIgnoreCase(eachSMSSetting.getType())) optOutSettings = eachSMSSetting;
					else if(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP.equalsIgnoreCase(eachSMSSetting.getType())) helpSettings = eachSMSSetting;

				}
				if(optinSettings != null && messageHeader.isEmpty()) messageHeader = optinSettings.getMessageHeader();
				else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
				else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();

				/*headerLblId.setValue(messageHeader);
				if(optOutSettings != null) {
					optOutLblId.setValue("Reply "+optOutSettings.getKeyword()+" 2 Optout" );
				}*/
				getCharCount("");
			}

			//populate the merge-tags,coupons,keywords

			mailingListDao =(MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
			if(listIdsSet != null && listIdsSet.size() > 0){

				// Set Editor PH values.
				Set<MailingList> set = new HashSet<MailingList>();
				set.addAll(mailingListDao.findByIds(listIdsSet));
				getPlaceHolderList(set);
			}
			getCoupons();
			getKeywords();
		//}

		onSelect$typeOfAutoSMSListLbId();
		onSelect$msgListCmbBxId();

		AutoSMS autoSMS = null;

		//edit mode
		String from =(String)session.getAttribute("fromAddNewBtn");
		String types=(String)session.getAttribute("typeOfSms");

		if(from != null && types != null ) {
			for (Listitem item : typeOfAutoSMSListLbId.getItems()) {
				logger.info("value is"+item.getLabel()+" = " +types);

				if(item.getLabel().equals(types)) {

					typeOfAutoSMSListLbId.setSelectedItem(item);
					break;
				}
			}//for

			autoSMS = (AutoSMS)session.getAttribute("editSmsTemplate");

			if(from.equalsIgnoreCase("loyalty/loyaltyAutoCommunication") ) {

				int index=0;
				String type = LOYALTYREGISTRATION;
				if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REGISTRATION)) {
					type = LOYALTYREGISTRATION;
					index = 0;
				}
				if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE)) {
					type = GIFTCARDISSUANCE;
					index = 1;
				}
				if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_TIER_UPGRADATION)) {
					type = TIERUPGRADATION;
					index = 2;
				}
				if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_BONUS)) {
					type = EARNEDBONUS;
					index = 3;
				}
				if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION)) {
					type = EARNEDREWARDEXPIRATION;
					index = 4;
				}
				if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION)) {
					type = GIFTAMOUNTEXPIRATION;
					index = 5;
				}
				if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION)) {
					type = MEMBERSHIPEXPIRATION;
					index = 6;
				}
				if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
					type = GIFTCARDEXPIRATION;
					index = 7;
				}
				if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_WEB_FORMS)) {
					type = FEEDBACKFORM;
					index = 8;
				}if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_SPECIAL_REWARDS)) {
					type = SPECIALREWARDS;
					index = 9;
				}if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_WELCOME_SMS)) {
					type = WELCOMESMS;
					index = 10;
				}if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT)) {
					type = LOYALTYADJUSTMENT;
					index = 11;
				}if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_ISSUANCE)) {
					type = LOYALTYISSUANCE;
					index = 12;
				}if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REDEMPTION)) {
					type = LOYALTYREDEMPTION;
					index = 13;
				}if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_ERECEIPT_MESSAGE)) {
					type = ERECEIPTMESSAGES;
					index = 14;
				}
				if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_OTP_MESSAGE)) {
					type = OTPMESSAGES;
					index = 15;
				}
				if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_REDEMPTION_OTP)) {
					type = REDEMPTIONOTP;
					index = 16;
				}
				if(types.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_ISSUECOUPON_SMS)) {
					type = ISSUECOUPONSMS;
					index = 17;
				}
				
				
				
				
				typeOfAutoSMSListLbId.setSelectedIndex(index);
				onSelect$typeOfAutoSMSListLbId();
				for (Comboitem item : msgListCmbBxId.getItems()) {
					if(autoSMS != null) {
						if(item.getLabel().equals(autoSMS.getTemplateName())) {

							msgListCmbBxId.setSelectedItem(item);
							break;
						}
					}
				}//for

				if(autoSMS != null) {

					SMSMsgTbId.setValue(autoSMS.getMessageContent());
					getCharCount(autoSMS.getMessageContent());
					deleteBtnId.setVisible(true);
					
					
					for(Listitem item : multipleSenderIdsLbId.getItems()) {
						 if(item.getLabel().equals(autoSMS.getSenderId())) {
							 item.setSelected(true);
							 break;
						 }
						 
						 
					 }
					
					if(multipleSenderIdsLbId.getSelectedIndex()!=-1){
						onSelect$multipleSenderIdsLbId();
					}

				}else {
					String messageContent = PropertyUtil.getPropertyValueFromDB(typeOfAutoSMSListLbId.getSelectedItem().getValue().toString());
					SMSMsgTbId.setValue(messageContent);
					getCharCount(SMSMsgTbId.getValue());
					deleteBtnId.setVisible(false);
				}

				String mode = (String) session.getAttribute("SmsMode");
				if(mode.equalsIgnoreCase("edit")) {
					typeOfAutoSMSListLbId.setDisabled(true);
					msgListCmbBxId.setDisabled(true);
					multipleSenderIdsLbId.setDisabled(true);
				}else if(mode.equalsIgnoreCase("add")){
					typeOfAutoSMSListLbId.setDisabled(false);
					msgListCmbBxId.setDisabled(false);
				}
			}

		}
		
		
		Listitem selectedAutoSmsListType = typeOfAutoSMSListLbId.getSelectedItem();
		String autoSmsType = selectedAutoSmsListType.getLabel();
		
		/*AutoSMS returnedAutoSMS = defaultSettingsForPreConfiguredAutoSMS(autoSmsType);
		if(returnedAutoSMS != null){
			multipleSenderIdsLbId.setDisabled(true);
		}*/
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void populateMultipleSenderIds() {
		// TODO Auto-generated method stub
		Components.removeAllChildren(multipleSenderIdsLbId);
		Listitem aSenderIdListItem = null;
		List<UserSMSSenderId> multipleSenderIdList = listOfMultipleSenderIds;
		for(UserSMSSenderId aUserSMSSenderId : multipleSenderIdList){
			aSenderIdListItem = new Listitem(aUserSMSSenderId.getSenderId());
			aSenderIdListItem.setParent(multipleSenderIdsLbId);
		}

		if(multipleSenderIdsLbId.getChildren().size() >=2 ){
			multipleSenderIdDivId.setVisible(true);
			multipleSenderIdsLbId.setSelectedIndex(0);
			onSelect$multipleSenderIdsLbId();

		}else{
			multipleSenderIdDivId.setVisible(false);
		}

	}

	
	//doAfterCompose()

	public UserSMSSenderId  getsenderIds() {
		List<UserSMSSenderId> retSenderIds =  null;
		try{
			UserSMSSenderIdDao userSMSSenderIdDao = (UserSMSSenderIdDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USER_SMS_SENDER_ID_DAO);
			logger.info("the logged user id is====>"+currentUser.getUserId());
			String type = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(currentUser.getCountryType());
			retSenderIds = userSMSSenderIdDao.findSenderIdBySMSType(currentUser.getUserId(), type);
			if(retSenderIds == null) return null;
			return (UserSMSSenderId) retSenderIds.get(0);
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return (UserSMSSenderId) retSenderIds.get(0);
	}//getsenderIds()
	
	//getMultipleSenderIds
	public List<UserSMSSenderId> getMultipleSenderIds(){
		List<UserSMSSenderId> retSenderIds =  null;
		try{
			UserSMSSenderIdDao userSMSSenderIdDao = (UserSMSSenderIdDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USER_SMS_SENDER_ID_DAO);
			logger.info("the logged user id is====>"+currentUser.getUserId());
			String type = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(currentUser.getCountryType());
			retSenderIds = userSMSSenderIdDao.findSenderIdBySMSType(currentUser.getUserId(), type);
			if(retSenderIds == null) return null;
			return retSenderIds;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return retSenderIds;

	}
	public  List<String> getPlaceHolderList(Set<MailingList> mlistSet) {

		try {
			logger.debug("-- Just Entered --");
			MLCustomFieldsDao mlCustomFieldsDao = (MLCustomFieldsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ML_CUSTOM_FIELDS_DAO);
			logger.debug("Got Ml Set of size :" + mlistSet.size());

			POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
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
				logger.info("the current user is a oc user");
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

			} // for

			logger.debug("-- Exit --");
			return placeHoldersList;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}

	public void  getCoupons() {

		try {
			couponsDao = (CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			
			
		// ref List<Coupons> couponsList = couponsDao.findCouponsinautosmsByOrgId(currentUser.getUserOrganization().getUserOrgId());

		//referred coupons no need to come 
			
			List<Coupons> couponsList = couponsDao.findCouponsinautosmsByOrgId(currentUser.getUserOrganization().getUserOrgId());
	
			
				referralprogramDao=(ReferralProgramDao) SpringUtil.getBean(OCConstants.REFERRAL_PROGRAM_DAO);
			
			
			if(couponsList == null || couponsList.size() == 0) {
				logger.debug("got no coupons for this org");
				return;
			}
		//	Coupons coup=couponsDao.findrefCouponsByOrgId2(currentUser.getUserOrganization().getUserOrgId());
		
			ReferralProgram refprgm=referralprogramDao.findReferalprogramByUserId(currentUser.getUserId());
			
			logger.info("refprgm value is"+refprgm);
			Listitem item  = null;

			for (Coupons coupons : couponsList) {

				item = new Listitem(coupons.getCouponName(), coupons);
				item.setParent(insertCouponLbId);

			}
			if(refprgm != null && refprgm.getCouponId() != null) {
	
				item = new Listitem("Referralcode", refprgm);
				item.setParent(insertCouponLbId);
				}
				
				
			
		
		}
		catch(Exception e) {
			logger.error("Exception ::" , e);
		}

	}//getCoupons


	public void  getKeywords() {

		try {
			orgSMSkeywordsDao = (OrgSMSkeywordsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ORGSMSKEYWORDS_DAO);
			List<OrgSMSkeywords> keywordsList = orgSMSkeywordsDao.getUserOrgSMSKeyWords(currentUser.getUserOrganization().getUserOrgId());

			if(keywordsList == null || keywordsList.size() == 0) {
				logger.debug("got no keywords for this org");
				return;
			}

			Listitem item  = null;

			for (OrgSMSkeywords keyword : keywordsList) {

				item = new Listitem(keyword.getKeyword(), keyword);
				item.setParent(insertKeywordLbId);

			}
		}
		catch(Exception e) {
			logger.error("Exception ::" , e);
		}

	}//getKeywords

	public void onSelect$insertMergetagsLbId() {

		if(insertMergetagsLbId.getSelectedIndex() == 0) {
			return;
		}
		insertText((String)insertMergetagsLbId.getSelectedItem().getValue());
		insertMergetagsLbId.setSelectedIndex(0);

	}//onSelect$insertMergetagsLbId()

	public void onClick$insertLinkBtnId() {

		try {
			logger.debug("---URL need to be Short Coded---");

			String enteredURL = linkURLTxtBoxId.getValue();

			if(enteredURL.trim().equals("") || enteredURL.equalsIgnoreCase("http://") ||
					(enteredURL.trim().equals("Use Url Shortener".trim())) ) {
				MessageUtil.setMessage("Please provide a valid URL.", "red", "top");
				logger.error("Exception : Link URl field is empty .");
				return;
			}
			if( !( enteredURL.startsWith("http://") || enteredURL.startsWith("https://")) ) {   //APP-3490

				enteredURL = "http://"+enteredURL;

			}

			String mappingUrl = GetUser.getUserId()+"|"+System.currentTimeMillis()+"|"+enteredURL;
			String insertedUrl = PropertyUtil.getPropertyValue("ApplicationShortUrl");

			//List<StringBuffer> retList =  Utility.getSixDigitURLCode(mappingUrl);
			List<StringBuffer> retList =  Utility.getEightDigitURLCode(mappingUrl);
			UrlShortCodeMapping urlShortCodeMapping = null;
			if(retList != null && retList.size() > 0) {

				//check whether any returned  shordcode exists in DB

				for (StringBuffer shortCode : retList) {

					String urlShortCode = "U"+shortCode;
					urlShortCodeMapping = new UrlShortCodeMapping(urlShortCode, enteredURL, "", GetUser.getUserObj().getUserId());

					try {
						urlShortCodeMappingDao = (UrlShortCodeMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.URL_SHORTCODE_MAPPING_DAO);
						urlShortCodeMappingDaoForDML = (UrlShortCodeMappingDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.URL_SHORTCODE_MAPPING_DAO_FOR_DML);
						urlShortCodeMappingDaoForDML.saveOrUpdate(urlShortCodeMapping);

						insertedUrl += urlShortCode;
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
			insertText(insertedUrl);
			linkURLTxtBoxId.setValue("Use Url Shortener");
		} catch (Exception e) {
			logger.debug("Exception **",e);
		}

	}//onClick$insertLinkBtnId$insertLinkWinId



	public void insertText(String  value){

		logger.info("insertText");

		String cp = caretPosTB.getValue();
		if (cp == null || cp.length() == 0) {
			cp = "0";
		}
		try{
			int caretPos = Integer.parseInt(cp);
			if (caretPos != -1) {
				String currentValue = SMSMsgTbId.getValue();
				String newValue = "";
				if(currentValue != null && currentValue.trim().length() >0 ){

					newValue = currentValue.substring(0, caretPos) + value + currentValue.substring(caretPos);
				}else newValue = value;

				SMSMsgTbId.setValue(newValue);

				int charCount = SMSMsgTbId.getValue().length();
				if(charCount>160) {
					int msgcount = charCount/160;
					charCountTbId.setValue(""+charCount+" / "+(msgcount+1));

				}//if
				else {
					charCountTbId.setValue(""+charCount+" / "+1);
				}//else

				SMSMsgTbId.focus();

			}
		}catch(Exception e) {
			logger.error("Exception ::" , e);
		}
	}//insertText()

	public void onChanging$SMSMsgTbId(InputEvent event){
		try{

			getCharCount( event.getValue());


		}catch (Exception e) {
			logger.debug("Exception **",e);
		}
	}//onChanging$SMSMsgTbId(-)

	public void getCharCount(String msgContent) {
		try {
			int charCount = msgContent.length();

			/*if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType()) && smsSettings != null) {

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

				//charCount = charCount + (messageHeader != null ? messageHeader.length() : 0);//TODO set perfection
				if(messageHeader!=null) messageHeader=messageHeader +" ";
				charCount = charCount + (messageHeader != null ? messageHeader.length() : 0);//TODO set perfection
				if(optOutSettings != null) {
					charCount = charCount + 1+(optOutLblId.getValue().length());//("Reply "+optOutSettings.getKeyword()+" 2 Optout" );
				}
				logger.info("charCount-----***"+charCount);

			}*/
			if(charCount>160) {
				int msgcount = charCount/160;
				charCountTbId.setValue(""+charCount+"/"+(msgcount+1));
			}//if
			else {
				charCountTbId.setValue(""+charCount+" / "+1);
			}//else


		} catch (Exception e) {
			logger.debug("Exception while getting the character count",e);
		}//catch

	}//getCharCount


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
		
		
		
	//onSelect$insertCouponLbId()

	public void onSelect$insertKeywordLbId() {

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

				shortCode = currentUser.getCountryCarrier().toString() + shortCode;
			}

			if(shortCode.length() > 10) shortCode = "+"+shortCode;


			keyword = keyword + " to " +shortCode;
			insertText(keyword);
		}
		insertKeywordLbId.setSelectedIndex(0);

	}//onSelect$insertKeywordLbId()

	public void onClick$newBtnId(Event event) {
		createNewTemplateWinId$msgLblId.setValue("");
		createNewTemplateWinId.setVisible(true);
		createNewTemplateWinId.setPosition("center");
		createNewTemplateWinId.doHighlighted();
	}//onClick$newBtnId

	public void onClick$updateBtnId() {
		createNewTemplateWinId$msgLblId.setValue("");
		try {
			AutoSMS autoSMS = msgListCmbBxId.getSelectedItem().getValue();
			logger.info("selected item is====>"+msgListCmbBxId.getSelectedItem());

			if(autoSMS == null) {
				createNewTemplateWinId.setVisible(true);
				createNewTemplateWinId.setPosition("center");
				createNewTemplateWinId.doHighlighted();
			}
			else {

				if(!validateFields()){
					return;
				}

				if(currentUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA) && 
						!autoSMS.getMessageContent().equalsIgnoreCase(SMSMsgTbId.getValue())) {

					transactionalTemplatesDao = (TransactionalTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.TRANSACTIONAL_TEMPLATES_DAO);
					transactionalTemplatesDaoForDML = (TransactionalTemplatesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.TRANSACTIONAL_TEMPLATES_DAO_ForDML);

					String tempName = "";
					TransactionalTemplates transactionalTemplates = null;
					if(autoSMS.getTemplateName().contains("Version")) {
						String templateName = autoSMS.getTemplateName().substring(0,autoSMS.getTemplateName().indexOf(" Version"));
						transactionalTemplates = transactionalTemplatesDao.findTempByName(currentUser.getUserId(),templateName);
					}else {
						transactionalTemplates = transactionalTemplatesDao.findTempByName(currentUser.getUserId(),autoSMS.getTemplateName());
					}

					if(transactionalTemplates == null ) {
						tempName =  autoSMS.getTemplateName()+" Version 2";
					}
					else{
						String[] valueArry = transactionalTemplates.getTemplateName().split(" ");
						int value = Integer.parseInt(valueArry[valueArry.length-1]);
						value = value + 1;
						valueArry[valueArry.length-1] = value+"";
						for(String name : valueArry) {
							if(tempName.isEmpty()) {
								tempName = name;
							}else {
								tempName += " "+name;
							}
						}
					}

					AutoSMS updatedAutoSms = new AutoSMS();
					updatedAutoSms.setUserId(currentUser.getUserId());
					updatedAutoSms.setMessageContent(SMSMsgTbId.getValue());
					updatedAutoSms.setTemplateName(tempName);
					updatedAutoSms.setMessageType(SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(currentUser.getCountryType()));
					updatedAutoSms.setAutoSmsType(getTemplateType());
					updatedAutoSms.setOrgId(currentUser.getUserOrganization().getUserOrgId());
					updatedAutoSms.setCreatedBy(currentUser.getUserId());
					updatedAutoSms.setCreatedDate(Calendar.getInstance());
					updatedAutoSms.setSenderId(senderIdTbId.getValue());
					updatedAutoSms.setStatus(OCConstants.AUTO_SMS_TEMPLATE_STATUS_PENDING);
					autoSMSDao = (AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
					autoSMSDaoForDML = (AutoSMSDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_DAO_FOR_DML);
					//autoSMSDao.saveOrUpdate(updatedAutoSms);
					autoSMSDaoForDML.saveOrUpdate(updatedAutoSms);

					MessageUtil.setMessage("Edited template will be saved as "+tempName+" ", "green");

					TransactionalTemplates transactTempObj = new TransactionalTemplates();
					transactTempObj.setStatus(0);
					transactTempObj.setTemplateName(tempName);
					transactTempObj.setUserId(currentUser.getUserId());
					transactTempObj.setOrgId(currentUser.getUserOrganization().getUserOrgId());
					transactTempObj.setTemplateContent(SMSMsgTbId.getValue());
					transactTempObj.setType(OCConstants.TRANSACTIONAL_TEMPLATE_TYPE_AS);
					transactTempObj.setCreatedBy(currentUser.getUserId());
					transactTempObj.setCreatedDate(Calendar.getInstance());
					//transactionalTemplatesDao.saveOrUpdate(transactTempObj);
					transactionalTemplatesDaoForDML.saveOrUpdate(transactTempObj);

					String toEmailId = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SUPPORT_EMAILID);
					
					String currUserName = currentUser.getUserName();

					String templateTypeStr = PropertyUtil.getPropertyValueFromDB("TransactionalTemplate");
					if(templateTypeStr != null ) {
						//templateTypeStr = templateTypeStr.replace("<Template content>", SMSMsgTbId.getValue());
						//APP- 3515
						templateTypeStr = templateTypeStr.replace("[Template content]",SMSMsgTbId.getValue()).replace("[Username]",
								Utility.getOnlyUserName(currUserName)).replace("[orgID]",Utility.getOnlyOrgId(currUserName));
					}else templateTypeStr = SMSMsgTbId.getValue();

					emailQueueDao =  (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
					emailQueueDaoForDML =  (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
					EmailQueue emailQueue = new EmailQueue("Transactional Template", templateTypeStr,
							Constants.EQ_TYPE_LOW_SMS_CREDITS, "Active", toEmailId,Calendar.getInstance(), currentUser);
					//emailQueueDao.saveOrUpdate(emailQueue);
					emailQueueDaoForDML.saveOrUpdate(emailQueue);

				}
				else {
					autoSMSDao = (AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
					autoSMSDaoForDML = (AutoSMSDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_DAO_FOR_DML);
					autoSMS.setUserId(currentUser.getUserId());
					autoSMS.setAutoSmsType(getTemplateType());
					autoSMS.setMessageContent(SMSMsgTbId.getValue());
					autoSMS.setOrgId(currentUser.getUserOrganization().getUserOrgId());
					autoSMS.setModifiedBy(currentUser.getUserId());
					autoSMS.setModifiedDate(Calendar.getInstance());
					autoSMS.setSenderId(senderIdTbId.getValue());
					//autoSMSDao.saveOrUpdate(autoSMS);
					autoSMSDaoForDML.saveOrUpdate(autoSMS);
					MessageUtil.clearMessage();
					MessageUtil.setMessage("Template saved successfully.","green", "top");
				}
				onSelect$typeOfAutoSMSListLbId();
				getCharCount(SMSMsgTbId.getValue());
			}
		}  catch (Exception e) {

			logger.error("Exception **",e);
		}
	}//onClick$updateBtnId()


	public boolean validateFields() {

		try {
			if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType()) && 	smsSettings == null) {

				MessageUtil.setMessage("No SMS settings found for your user account.\n Please contact Admin to enable SMS feature.", "color:red;");
				return false;

			}

			// validating date placeholder
			String isValidPhStr = Utility.validatePh(SMSMsgTbId.getValue().trim(),GetUser.getUserObj());

			if(isValidPhStr != null) {

				MessageUtil.setMessage("Placeholder "+isValidPhStr+ " is invalid please replace with proper placeholder values.", "color:red;", "top");
				return false;
			}


			//validate coupon placeholders with couponId and userId
			logger.debug("messageContent :"+SMSMsgTbId.getValue().trim());
			couponsDao= (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			Set<String> couponPhSet = findCoupPlaceholders(SMSMsgTbId.getValue().trim());
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


						Long orgId = currentUser.getUserOrganization().getUserOrgId();

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
				List<Coupons> inValidCoupList = couponsDao.isExpireOrPauseCoupList(couponIdStr, currentUser.getUserOrganization().getUserOrgId());

				if(inValidCoupList != null){

					String inValidCoupNames = "";
					if(inValidCoupList != null && inValidCoupList.size() >0) {

						for (Coupons coupons : inValidCoupList) {
							inValidCoupNames += inValidCoupNames.trim().length() >0 ? ","+coupons.getCouponName() : coupons.getCouponName();
						}
						MessageUtil.setMessage(	"The discount code "+inValidCoupNames+" used in this is either expired or in paused status. " +
								" \n Please change the status of this discount code.",
								"color:red", "TOP");
						return false;
					}

				}

			}

			if(SMSMsgTbId.getValue() != null && SMSMsgTbId.getValue().trim().length() == 0) {
				MessageUtil.setMessage("SMS message cannot be left empty. Please provide message text.", "color:red;", "top");
				return false;
			}
		String otpCode="[OTP]";
			String msg=SMSMsgTbId.getValue();
			if(SMSMsgTbId!=null && !msg.isEmpty()) {
				logger.info("entered validation");
				if ((typeOfAutoSMSListLbId.getSelectedIndex() == 15 || typeOfAutoSMSListLbId.getSelectedIndex() == 16) && msg.indexOf(otpCode) < 0 ) {
					MessageUtil.setMessage("In order for the auto-sms to function correctly, please add 'OTP Code ' merge-tag in the email content.", "top");
					return false;
				}
			}		





			//APP-3361
			String msg1=SMSMsgTbId.getValue();
			logger.info("asd value"+msg1);
			if(!Utility.validateName(msg1))
			{
				if( Messagebox.show("There are some special characters in this message. Do you want to continue?", "Continue?",
						Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
				
					return false;
				}	
				//MessageUtil.setMessage("Special characters are there in your message content", "top");
			}
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
			return false;
		}
		return true;
	}//validateFields()

	public void onClick$submitBtnId$createNewTemplateWinId() {

		try {
			autoSMSDao = (AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			autoSMSDaoForDML = (AutoSMSDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_DAO_FOR_DML);

			if(createNewTemplateWinId$newTempNameTbId.getValue().trim().length() == 0) {
				createNewTemplateWinId$msgLblId.setValue("Please enter template name.");
				return;
			}
			String templateName = createNewTemplateWinId$newTempNameTbId.getValue().trim();
			boolean isExistFlag = autoSMSDao.isTemplateNameExistByUserId(currentUser.getUserId(),templateName);

			if(isExistFlag){
				MessageUtil.setMessage("Template already exists with the given name", "color:red", "TOP");
				return;
			}

			if(!validateFields()){
				return;
			}

			
			if(currentUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {
				AutoSMS autoSMS = new AutoSMS();

				autoSMS.setUserId(currentUser.getUserId());
				autoSMS.setMessageContent(SMSMsgTbId.getValue());
				autoSMS.setTemplateName(createNewTemplateWinId$newTempNameTbId.getValue());
				autoSMS.setMessageType(SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(currentUser.getCountryType()));//TODO
				autoSMS.setAutoSmsType(getTemplateType());//call getTemplate()
				autoSMS.setOrgId(currentUser.getUserOrganization().getUserOrgId());
				autoSMS.setCreatedBy(currentUser.getUserId());
				autoSMS.setCreatedDate(Calendar.getInstance());
				autoSMS.setSenderId(senderIdTbId.getValue());
				autoSMS.setStatus(OCConstants.AUTO_SMS_TEMPLATE_STATUS_PENDING);
				//autoSMSDao.saveOrUpdate(autoSMS);
				autoSMSDaoForDML.saveOrUpdate(autoSMS);

				createNewTemplateWinId.setVisible(false);
				createNewTemplateWinId$newTempNameTbId.setValue("");
				createNewTemplateWinId$msgLblId.setValue("");
				MessageUtil.setMessage("Template saved successfully.", "green");

				transactionalTemplatesDao = (TransactionalTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.TRANSACTIONAL_TEMPLATES_DAO);
				transactionalTemplatesDaoForDML = (TransactionalTemplatesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.TRANSACTIONAL_TEMPLATES_DAO_ForDML);
				
				TransactionalTemplates transactTempObj = new TransactionalTemplates();
				transactTempObj.setStatus(0);
				transactTempObj.setTemplateName(templateName);
				transactTempObj.setUserId(currentUser.getUserId());
				transactTempObj.setOrgId(currentUser.getUserOrganization().getUserOrgId());
				transactTempObj.setTemplateContent(SMSMsgTbId.getValue());
				transactTempObj.setType(OCConstants.TRANSACTIONAL_TEMPLATE_TYPE_AS);
				transactTempObj.setCreatedBy(currentUser.getUserId());
				transactTempObj.setCreatedDate(Calendar.getInstance());
				//transactionalTemplatesDao.saveOrUpdate(transactTempObj);
				transactionalTemplatesDaoForDML.saveOrUpdate(transactTempObj);

				String toEmailId = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SUPPORT_EMAILID);
				
				String currUserName = currentUser.getUserName();
				
				String templateTypeStr = PropertyUtil.getPropertyValueFromDB("TransactionalTemplate");
				if(templateTypeStr != null ) {

					//templateTypeStr = templateTypeStr.replace("<Template content>", SMSMsgTbId.getValue());
					//APP - 3515
					templateTypeStr = templateTypeStr.replace("[Template content]",SMSMsgTbId.getValue()).replace("[Username]",
							Utility.getOnlyUserName(currUserName)).replace("[orgID]",Utility.getOnlyOrgId(currUserName));
					
				}else templateTypeStr = SMSMsgTbId.getValue();

				emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
				emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);

				EmailQueue emailQueue = new EmailQueue("Transactional Template", templateTypeStr,
						Constants.EQ_TYPE_LOW_SMS_CREDITS, "Active", toEmailId,Calendar.getInstance(), currentUser);
				//emailQueueDao.saveOrUpdate(emailQueue);
				emailQueueDaoForDML.saveOrUpdate(emailQueue);

			}
			else {

				AutoSMS autoSMS = new AutoSMS();
				autoSMS.setUserId(currentUser.getUserId());
				autoSMS.setMessageContent(SMSMsgTbId.getValue());
				autoSMS.setTemplateName(createNewTemplateWinId$newTempNameTbId.getValue());
				autoSMS.setMessageType(SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(currentUser.getCountryType()));//TODO
				autoSMS.setAutoSmsType(getTemplateType());//call getTemplate()
				autoSMS.setOrgId(currentUser.getUserOrganization().getUserOrgId());
				autoSMS.setCreatedBy(currentUser.getUserId());
				autoSMS.setCreatedDate(Calendar.getInstance());
				autoSMS.setSenderId(senderIdTbId.getValue());
				autoSMS.setStatus(OCConstants.AUTO_SMS_TEMPLATE_STATUS_APPROVED);
				//autoSMSDao.saveOrUpdate(autoSMS);
				autoSMSDaoForDML.saveOrUpdate(autoSMS);

				createNewTemplateWinId.setVisible(false);
				createNewTemplateWinId$newTempNameTbId.setValue("");
				createNewTemplateWinId$msgLblId.setValue("");
				MessageUtil.setMessage("Template saved successfully.", "green");
			}

			onSelect$typeOfAutoSMSListLbId();
			getCharCount(SMSMsgTbId.getValue());

		} catch (Exception e) {
			logger.error("Exception ::", e);
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
	}//findCoupPlaceholders()


	public String getTemplateType() {

		String type = null;

		if(typeOfAutoSMSListLbId.getSelectedIndex() == 0) {
			type = LOYALTYREGISTRATION;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 1){
			type = GIFTCARDISSUANCE;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 2){
			type = TIERUPGRADATION;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 3){
			type = EARNEDBONUS;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 4){
			type = EARNEDREWARDEXPIRATION;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 5){
			type = GIFTAMOUNTEXPIRATION;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 6){
			type = MEMBERSHIPEXPIRATION;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 7){
			type = GIFTCARDEXPIRATION;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 8){
			type = FEEDBACKFORM;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 9){
			type = SPECIALREWARDS;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 10){
			type = WELCOMESMS;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 11){
			type = LOYALTYADJUSTMENT;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 12){
			type = LOYALTYISSUANCE;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 13){
			type = LOYALTYREDEMPTION;
		}else if(typeOfAutoSMSListLbId.getSelectedIndex() == 14){
			type = ERECEIPTMESSAGES;
		}
		else if(typeOfAutoSMSListLbId.getSelectedIndex() == 15){
			type = OTPMESSAGES;
		}
		else if(typeOfAutoSMSListLbId.getSelectedIndex() == 16){
			type = REDEMPTIONOTP;
		}
		else if(typeOfAutoSMSListLbId.getSelectedIndex() == 17){
			type = ISSUECOUPONSMS;
		}
	
		
		return type;
	}//getTemplateType()


	public void onSelect$typeOfAutoSMSListLbId(){

		try {
			
			multipleSenderIdsLbId.setDisabled(false);
			autoSMSDaoForDML = (AutoSMSDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_DAO_FOR_DML);
			autoSMSDao = (AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			transactionalTemplatesDao = (TransactionalTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.TRANSACTIONAL_TEMPLATES_DAO);
			transactionalTemplatesDaoForDML = (TransactionalTemplatesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.TRANSACTIONAL_TEMPLATES_DAO_ForDML);
			Components.removeAllChildren(msgListCmbBxId);
			
			if(typeOfAutoSMSListLbId.getSelectedCount() == 0) {
				MessageUtil.setMessage("There are no sms types found.", "green");
				return;
			}
			

			List<String> placeHoldersList1 = new ArrayList<String>(); 
			
			List<String> placeHoldersList2 = new ArrayList<String>(); 

			
			Listitem item =null;
			
			//Components.removeAllChildren(insertMergetagsLbId);

			if(typeOfAutoSMSListLbId.getSelectedIndex()==15 || typeOfAutoSMSListLbId.getSelectedIndex()==16  ) {
		
			logger.info("value oftype"+typeOfAutoSMSListLbId);
		
			//insertCouponLbId.setVisible(false);
			insertKeywrdDivId.setVisible(false);

			insertCouponDivId.setVisible(false);
	
			
	
				//insertMergetagsLbId.setVisible(false);
			Components.removeAllChildren(insertMergetagsLbId);

		
			
			
			placeHoldersList1.addAll(Constants.OTP_TAGS);
			
		
				
				
				
				for (String placeHolder : placeHoldersList1) {
				
				logger.info("entering");

				String[] phTokenArr =  placeHolder.split("::"); 
				String key = phTokenArr[0];
				StringBuffer value = new StringBuffer(phTokenArr[1]);
		
		


			item =  new Listitem(key,value.toString());
			
		
			item.setParent(insertMergetagsLbId);
			insertMergetagsLbId.setSelectedIndex(0);

			}
		
		}
			
		else if(typeOfAutoSMSListLbId.getSelectedIndex()==14) {
				logger.info("entering ereceipt");
				Components.removeAllChildren(insertMergetagsLbId);

				placeHoldersList2.addAll(Constants.ERECEIPT_TAGS);
			
				for (String placeHolder : placeHoldersList2) {
					
					logger.info("entering");

					String[] phTokenArr =  placeHolder.split("::"); 
					String key = phTokenArr[0];
					StringBuffer value = new StringBuffer(phTokenArr[1]);
			
			


				item =  new Listitem(key,value.toString());
				
			
				item.setParent(insertMergetagsLbId);
				insertMergetagsLbId.setSelectedIndex(0);
			
				}
			}		
			
		else {	
			
				insertCouponDivId.setVisible(true);
				insertKeywrdDivId.setVisible(true);
				
				
				Components.removeAllChildren(insertMergetagsLbId);
				item =  new Listitem("Insert Merge Tags","");
				
				
				item.setParent(insertMergetagsLbId);
				insertMergetagsLbId.setSelectedIndex(0);
				Set<MailingList> set = new HashSet<MailingList>();
				set.addAll(mailingListDao.findByIds(listIdsSet));
				getPlaceHolderList(set);
				//insertMergetagsLbId.setSelectedIndex(0);
		
			}
			
			
			Listitem selItem = typeOfAutoSMSListLbId.getSelectedItem();
			if(selItem == null) {
				logger.info("There are no items selected to select "+selItem);
				return;
			}

			String type = getTemplateType() ;

			if(currentUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)) {
				int status = 1;
				List<AutoSMS> list = autoSMSDao.getTemplatesByType(currentUser.getUserId(),type);
				List<TransactionalTemplates>  templateList = transactionalTemplatesDao.findTemplatesByType(currentUser.getUserId(),status);
				List<AutoSMS> autoSMSList = new ArrayList<AutoSMS>();
				if(list != null && list.size() >0 && templateList != null && templateList.size() >0 ) {
					for(TransactionalTemplates transTemp : templateList) {
						for(AutoSMS autoSMS : list) {
							if(transTemp.getTemplateContent().equalsIgnoreCase(autoSMS.getMessageContent())) {
								autoSMS.setStatus(OCConstants.AUTO_SMS_TEMPLATE_STATUS_APPROVED);
								autoSMS.setTemplateRegisteredId(transTemp.getTemplateRegisteredId());
								autoSMSList.add(autoSMS);
							}
						}
					}
				}
				//autoSMSDao.saveByCollection(autoSMSList);
				autoSMSDaoForDML.saveByCollection(autoSMSList);

				List<AutoSMS> autoList = autoSMSDao.getTemplatesByType(currentUser.getUserId(),type);

				logger.debug("autoList ::"+autoList);

				Comboitem li = new Comboitem();
				li.setLabel("Default Message");
				li.setParent(msgListCmbBxId);

				if(autoList != null && autoList.size() > 0) {

					for(AutoSMS autoSMS : autoList) {
						Comboitem comboitem = new Comboitem();
						comboitem.setLabel(autoSMS.getTemplateName());
						comboitem.setValue(autoSMS);
						comboitem.setDescription(autoSMS.getStatus().equalsIgnoreCase(OCConstants.AUTO_SMS_TEMPLATE_STATUS_PENDING)? "Pending" : "Approved");
						comboitem.setParent(msgListCmbBxId);

					}
				}
			}
				else {
					List<AutoSMS> list = autoSMSDao.getTemplatesByType(currentUser.getUserId(),type);

					Comboitem li = new Comboitem();
					li.setLabel("Default Message");
					li.setParent(msgListCmbBxId);

					if(list != null && list.size() > 0) {

						for(AutoSMS autoSMS : list) {
							Comboitem comboitem = new Comboitem();
							comboitem.setLabel(autoSMS.getTemplateName());
							comboitem.setValue(autoSMS);
							comboitem.setParent(msgListCmbBxId);

						}
					}
				}
				
			msgListCmbBxId.setSelectedIndex(0);
			onSelect$msgListCmbBxId();
			
			disableMultipleSenderIdsLbId();
		} catch (Exception e) {
			logger.debug("Exception ::" , e);
		}

	}//onSelect$typeOfAutoSMSListLbId()

	public void onSelect$msgListCmbBxId() {

		
		multipleSenderIdsLbId.setDisabled(false);
		AutoSMS autoSMS = msgListCmbBxId.getSelectedItem().getValue();

		if(autoSMS == null) {
			String messageContent = PropertyUtil.getPropertyValueFromDB(typeOfAutoSMSListLbId.getSelectedItem().getValue().toString());
			deleteBtnId.setVisible(false);
			SMSMsgTbId.setValue(messageContent);
			getCharCount(SMSMsgTbId.getValue());
			
			if(multipleSenderIdDivId.isVisible()){
				multipleSenderIdsLbId.setSelectedIndex(0);
				onSelect$multipleSenderIdsLbId();
			}
		}
		else {
			
			try{
				 for(Listitem item : multipleSenderIdsLbId.getItems()) {
					 if(item.getLabel().equals(autoSMS.getSenderId())) {
						 item.setSelected(true);
						 break;
					 }
					 
					 
				 }
				 
				 //onSelect$multipleSenderIdsLbId();
				 if(multipleSenderIdsLbId.getSelectedIndex()!=-1){
						onSelect$multipleSenderIdsLbId();
				 }
			}catch(Exception e){
				logger.error("Exception >>> ",e);
			}
			deleteBtnId.setVisible(true);
			SMSMsgTbId.setValue(autoSMS.getMessageContent());
			getCharCount(autoSMS.getMessageContent());
		}
		
		Listitem selectedAutoSmsListType = typeOfAutoSMSListLbId.getSelectedItem(); 
		String autoSmsType = selectedAutoSmsListType.getLabel();
		
		/*AutoSMS returnedAutoSMS = defaultSettingsForPreConfiguredAutoSMS(autoSmsType);
		if(returnedAutoSMS != null){
			multipleSenderIdsLbId.setDisabled(true);
		}*/
		
		disableMultipleSenderIdsLbId();
		
	}//onSelect$multipleSenderIdsLbId()
	public void onSelect$multipleSenderIdsLbId() {
		// TODO Auto-generated method stub
		String selectedSenderID = multipleSenderIdsLbId.getSelectedItem().getLabel();
		if(selectedSenderID != null){
			senderIdTbId.setText(selectedSenderID);
			senderIdTbId.setDisabled(true);

		}
	}

	
	
	public void onClick$linkURLTxtBoxId() {

		if(linkURLTxtBoxId.getValue().equals("Use Url Shortener")) {
			linkURLTxtBoxId.setValue("");
		}

	}//onClick$linkURLTxtBoxId()

	public void onClick$mblNoTxtBoxId() {

		if(mblNoTxtBoxId.getValue().equals("Enter Mobile Number(s)...")) {
			mblNoTxtBoxId.setValue("");
		}

	}//onClick$mblNoTxtBoxId()

	public void onChange$caretPosTB(){

		logger.debug("---just entered----");
	}//onChange$caretPosTB()


	public void onBlur$caretPosTB() {

		logger.debug("-----just entered: onBlur event----");
	}//onBlur$caretPosTB()

	public void onClick$addNewCouponTBId() {

		Redirect.goTo(PageListEnum.ADMIN_VIEW_COUPONS);
	}//onClick$addNewCouponTBId()

	public void onClick$addNewKeywrdTBId() {
		Redirect.goTo(PageListEnum.SMS_CAMP_SETUP);

	}//onClick$addNewKeywrdTBId()

	public void onClick$sendTestSmsBtnId() {
		
		//String msg = "Test SMS : "+SMSMsgTbId.getText();
		String msg = SMSMsgTbId.getText();

		if(sendTestSMS (mblNoTxtBoxId.getValue(), msg)) {

			mblNoTxtBoxId.setValue("Enter Mobile Number(s)...");
		}

	}//onClick$sendTestSmsBtnId()

	public boolean sendTestSMS(String mblNumber, String sendingMsg) {

		try{
			MessageUtil.clearMessage();
			String mblNumArr[];
			long mblNum = 0;

			if( mblNumber == null || mblNumber.trim().equals("")) {
				MessageUtil.setMessage("Please provide mobile number to send a test SMS.", "color:red;","top");

				return true;

			}

			if(sendingMsg == null || !(sendingMsg.trim().length()>0) ){ // if test SMS is  empty

				MessageUtil.setMessage("SMS message cannot be left empty. Please provide message text.", "color:red;", "top");
				return false;
			}

			String senderId = senderIdTbId.getValue().trim();

			if(senderId == null || senderId.length() == 0) {//if SMS sender id is not selected

				MessageUtil.setMessage("Sender ID is required. Please contact Admin to get appropriate sender ID.","color:red","TOP");
				return false;
			}


			UserSMSGatewayDao userSMSGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			OCSMSGatewayDao OCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);

			UserSMSGateway userSmsGateway = null;
			OCSMSGateway ocgateway = null;
			String userSMSDetails = Constants.STRING_NILL;
			String msgType = "";
			String accountType = null;
			int mobCnt =0;
			int charCount =0;
			int usedCount =0;

			msgType = currentUser.getCountryType();

			UserOrganization userOrganization = currentUser.getUserOrganization();

			if(userOrganization == null){
				MessageUtil.setMessage("User does not belong to organization. Please contact Administrator.", "color:red;","top");
				return false;
			}
			
			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(msgType);
			userSmsGateway = userSMSGatewayDao.findByUserId(currentUser.getUserId(), accountType);
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

			logger.debug(" my sms details are ====>"+userSMSDetails);

			try {
				if(mblNumber.contains(",")){ // if having multiple mobile numbers to sent test SMS

					mblNumArr = mblNumber.split(",");
					mblNumber = "";
					mobCnt = mblNumArr.length;
					for (String mobileNum : mblNumArr) {

						mobileNum = mobileNum.trim();
						UserOrganization organization = currentUser!=null ? currentUser.getUserOrganization(): null;
						mobileNum = Utility.phoneParse(mobileNum,organization);

						if(mobileNum != null) {
						if(organization.isRequireMobileValidation()){

							if(!mobileNum.startsWith(currentUser.getCountryCarrier().toString()) &&
									( (mobileNum.length() >= userOrganization.getMinNumberOfDigits()) && (mobileNum.length() <= userOrganization.getMaxNumberOfDigits()))) {
								mobileNum = currentUser.getCountryCarrier().toString()+mobileNum;
							}
						}
							mblNum = Long.parseLong(mobileNum);
							if(mblNumber.length()>0)	mblNumber += ",";

							mblNumber +=  mblNum;
						}else{
							MessageUtil.setMessage("Please provide valid mobile number for "+mobileNum,"color:red","TOP");
							return false;

						}
					}//for
				}
				else {

					mblNumber = mblNumber.trim();
					UserOrganization organization = currentUser!=null ? currentUser.getUserOrganization(): null;
					mblNumber = Utility.phoneParse(mblNumber,organization);
					if(mblNumber != null) {
					if(organization.isRequireMobileValidation()){
						if(!mblNumber.startsWith(currentUser.getCountryCarrier().toString()) &&
								( (mblNumber.length() >= userOrganization.getMinNumberOfDigits()) && (mblNumber.length() <= userOrganization.getMaxNumberOfDigits()))) {

							mblNumber = currentUser.getCountryCarrier().toString()+mblNumber;
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
					//testSMSTbId.setValue("Enter Mobile Number(s)...");
					mblNoTxtBoxId.setValue("Enter Mobile Number(s)...");
					return false;
				}

				if(currentUser.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_INDIA)){

					if(!((String) accountType).equalsIgnoreCase(Constants.SMS_TYPE_TRANSACTIONAL)  ){

						MessageUtil.setMessage("If mobile number is in NDNC list, test SMS won't be sent. ","color:blue;");
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception :::",e);
			}


			/***split,truncate,ignore  or send the SMS message as it is depend on the selected sending option ******/

			logger.info("get the user sms count=====>"+currentUser.getSmsCount());
			if(currentUser.getSmsCount()==0){

				MessageUtil.setMessage("SMS credits are not available. Please renew your account.", "color:red;","top");
				//testSMSTbId.setValue("Enter Mobile Number(s)...");
				mblNoTxtBoxId.setValue("Enter Mobile Number(s)...");
				return false; 

			}
			
			//adding header and footer for test msg content (harshi)
			String messageHeader = Constants.STRING_NILL;
			//String msgContent="Test SMS"+"\n";
			/*logger.info("before add headers");
			if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType())) {
				logger.info("entered add headers");
				SMSSettingsDao smsSettingsDao = (SMSSettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
				List<SMSSettings> smsSettings = smsSettingsDao.findByUser(currentUser.getUserId());
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
					
					sendingMsg = messageHeader != null ? msgContent+messageHeader +" "+sendingMsg : sendingMsg;
					if(optOutSettings != null) {
						sendingMsg = sendingMsg+ (optOutSettings.getKeyword() != null ?
								("\n" + "Reply " + optOutSettings.getKeyword() + " 2 Optout")
										: "\n"+ PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));

					}

				}
			}
*/			//sendingMsg = msgContent+sendingMsg;
			captiwayToSMSApiGateway.sendToSMSApi(ocgateway, sendingMsg, 
					""+mblNumber, accountType, "9848495956", ""+mblNumber, "1", senderId,null);
			
			/**
		     * Update the Used SMS count
		     */
		    try{
		     //UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		     UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
		     //usersDao.updateUsedSMSCount(currentUser.getUserId(), mobCnt);
		     //usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), mobCnt);
		     charCount = sendingMsg.length();
			    usedCount=1;
			     if(charCount>160) usedCount = charCount/160 + 1;
			     usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(),usedCount* mobCnt);
		    }catch(Exception exception){
		     logger.error("Exception while updating the Used SMS count",exception);
		    }
		    
		    /**
		     * Update the SMS Queue
		     */
		    SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
		    smsQueueHelper.updateSMSQueue(mblNumber, sendingMsg, Constants.SMS_MSG_TYPE_AUTOSMS, currentUser,senderId);
		    
			MessageUtil.setMessage("Test SMS will be sent in a moment.", "color:green;", "top");
			testSmsWinId.setVisible(false);
		}catch (NumberFormatException e) {
			MessageUtil.setMessage("Please provide valid mobile number to send test SMS.", "color:red;","top");
			logger.error("** Exception",e);
		}catch (Exception e) {
			logger.error("** Exception while sending a test SMS",e);
		}

		return true;
	}//sendTestSMS()
	
	/**
	 * 
	 * @param mblNum
	 * @param sendingMsg
	 * @param messageType
	 * @param smsCampaign
	 */
	private void updateSMSQueue(String mblNum,String msgContent , String messageType , SMSCampaigns smsCampaign) {
		logger.debug(">>>>>>> Started ViewSMSCampaign :: updateSMSQueue <<<<<<< ");

		if(mblNum == null){
			logger.error("Error While Updating SMS Queue as the no Mobile Number exist's");
			return;
		}

		List<SmsQueue> smsQueues = new ArrayList<SmsQueue>();
		String []mobArray = mblNum.split(",");

		SmsQueueDao smsQueueDao = null;
		SmsQueueDaoForDML smsQueueDaoForDML = null;

		try{
			smsQueueDao = (SmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_QUEUE_DAO);
			smsQueueDaoForDML = (SmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_QUEUE_DAO_ForDML);

		}
		catch(Exception exception){
			logger.error("Error While creating SmsQueueDao Object....:",exception);
		}

		for(String mobileNumber : mobArray){

			if(smsQueueDao != null){
				SmsQueue smsQueue = new SmsQueue();
				smsQueue.setMessage(msgContent);
				smsQueue.setMsgType(messageType);
				smsQueue.setToMobilePhone(mobileNumber);
				smsQueue.setUser(currentUser);
				smsQueue.setSentDate(MyCalendar.getNewCalendar());
				smsQueue.setStatus(Constants.CAMP_STATUS_ACTIVE);
				//smsQueue.setSmsCampaigns(smsCampaign);
				//Add to list
				smsQueues.add(smsQueue);
			}
		}//for each
		/**
		 * Storing to DB 
		 */

		if(smsQueueDaoForDML != null){
			//smsQueueDao.saveByCollection(smsQueues);
			smsQueueDaoForDML.saveByCollection(smsQueues);

		}

		logger.debug(">>>>>>> Completed ViewSMSCampaign :: updateSMSQueue <<<<<<< ");
	}//updateSMSQueue



	public void onClick$deleteBtnId() {

		try {
			AutoSMS autoSMS =  msgListCmbBxId.getSelectedItem().getValue();

			LoyaltyAutoCommDao loyaltyAutoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
			List<LoyaltyAutoComm> autoCommList = loyaltyAutoCommDao.findByUserId(currentUser.getUserId());
			boolean isExists = false;
			if(autoCommList != null && autoCommList.size() > 0) {

				for (LoyaltyAutoComm loyaltyAutoComm : autoCommList) {
					if(typeOfAutoSMSListLbId.getSelectedItem().getLabel().equalsIgnoreCase(OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REGISTRATION)) {
						if(loyaltyAutoComm.getRegSmsTmpltId().toString().equalsIgnoreCase(autoSMS.getAutoSmsId().toString())) {
							isExists = true;
							break;
						}
					}
					else if(typeOfAutoSMSListLbId.getSelectedItem().getLabel().equalsIgnoreCase(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE)) {
						if(loyaltyAutoComm.getGiftCardIssuanceSmsTmpltId().toString().equalsIgnoreCase(autoSMS.getAutoSmsId().toString())) {
							isExists = true;
							break;
						}
					}
					else if(typeOfAutoSMSListLbId.getSelectedItem().getLabel().equalsIgnoreCase(OCConstants.AUTO_SMS_TEMPLATE_TYPE_TIER_UPGRADATION)) {
						if(loyaltyAutoComm.getTierUpgdSmsTmpltId().toString().equalsIgnoreCase(autoSMS.getAutoSmsId().toString())) {
							isExists = true;
							break;
						}
					}
					else if(typeOfAutoSMSListLbId.getSelectedItem().getLabel().equalsIgnoreCase(OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_BONUS)) {
						if(loyaltyAutoComm.getThreshBonusSmsTmpltId().toString().equalsIgnoreCase(autoSMS.getAutoSmsId().toString())) {
							isExists = true;
							break;
						}
					}
					else if(typeOfAutoSMSListLbId.getSelectedItem().getLabel().equalsIgnoreCase(OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION)) {
						if(loyaltyAutoComm.getRewardExpirySmsTmpltId().toString().equalsIgnoreCase(autoSMS.getAutoSmsId().toString())) {
							isExists = true;
							break;
						}
					}
					else if(typeOfAutoSMSListLbId.getSelectedItem().getLabel().equalsIgnoreCase(OCConstants.AUTO_SMS_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION)) {
						if(loyaltyAutoComm.getMbrshipExpirySmsTmpltId().toString().equalsIgnoreCase(autoSMS.getAutoSmsId().toString())) {
							isExists = true;
							break;
						}
					}
					else if(typeOfAutoSMSListLbId.getSelectedItem().getLabel().equalsIgnoreCase(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION)) {
						if(loyaltyAutoComm.getGiftAmtExpirySmsTmpltId().toString().equalsIgnoreCase(autoSMS.getAutoSmsId().toString())) {
							isExists = true;
							break;
						}
					}
					else if(typeOfAutoSMSListLbId.getSelectedItem().getLabel().equalsIgnoreCase(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
						if(loyaltyAutoComm.getGiftMembrshpExpirySmsTmpltId().toString().equalsIgnoreCase(autoSMS.getAutoSmsId().toString())) {
							isExists = true;
							break;
						}
					}

				}
			}

			if(isExists) {

				MessageUtil.setMessage("Configured templates cannot be deleted", "color:red", "TOP");

			}else {
				AutoSMSDao autoSMSDao = (AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
				AutoSMSDaoForDML autoSMSDaoForDML = (AutoSMSDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_DAO_FOR_DML);
				//autoSMSDao.delete(autoSMS);
				autoSMSDaoForDML.delete(autoSMS);
				MessageUtil.setMessage("Template deleted successfully", "color:blue", "TOP");
				onSelect$typeOfAutoSMSListLbId();
			}

		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}

	}//onClick$deleteBtnId()

	public void onClick$sendTestMsgBtnId() {
		testSmsWinId.setVisible(true);
		testSmsWinId.setPosition("center");
		testSmsWinId.doHighlighted();
		testSmsWinId.doModal();
	}

	public void onClick$cancelSendTestSmsBtnId$testSmsWinId() {
		testSmsWinId.setVisible(false);
		testSmsWinId$smsTbId.setValue("");
	}

	public void onClick$sendTestSmsBtnId$testSmsWinId() {
		
		//String msg = "Test SMS : "+SMSMsgTbId.getValue();
		String msg = SMSMsgTbId.getValue();
		sendTestSMS(testSmsWinId$smsTbId.getValue(),msg);
		testSmsWinId$smsTbId.setValue("");
	}


	private class MyListener implements EventListener {

		@Override
		public void onEvent(Event event) throws Exception {

			if(event.getTarget() instanceof Textbox) {

				if(event.getName().equals(Events.ON_CTRL_KEY) || event.getName().equals(Events.ON_RIGHT_CLICK) ) {
					MessageUtil.setMessage("Right-click and CTRL+V actions have been disabled.", "color:red;");
					return;
				}
			}
		}
	}
	private void fetchConfiguredAutoSMS(){
		try{
			
			
			LoyaltyAutoCommDao loyaltyAutoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
			List<LoyaltyAutoComm> autoCommList = loyaltyAutoCommDao.multipleLoyaltyAutoCommfindByUserId(currentUser.getUserId());
			AutoSMSDao autoSMSDao = (AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			
			
			if(autoCommList == null || autoCommList.size() == 0) return;
			
			String commaSeparatedAutoSmsIds = "";
			
			for(LoyaltyAutoComm aLoyaltyAutoComm  :  autoCommList){
				
				
				
				
				if(aLoyaltyAutoComm.getRegSmsTmpltId() != null && aLoyaltyAutoComm.getRegSmsTmpltId() > 0){
					commaSeparatedAutoSmsIds += aLoyaltyAutoComm.getRegSmsTmpltId();
					commaSeparatedAutoSmsIds += ",";
				}
				
				if(aLoyaltyAutoComm.getGiftCardIssuanceSmsTmpltId() != null && aLoyaltyAutoComm.getGiftCardIssuanceSmsTmpltId() > 0){
					commaSeparatedAutoSmsIds += aLoyaltyAutoComm.getGiftCardIssuanceSmsTmpltId();
					commaSeparatedAutoSmsIds += ",";
				}
				
				if(aLoyaltyAutoComm.getTierUpgdSmsTmpltId() != null && aLoyaltyAutoComm.getTierUpgdSmsTmpltId() > 0){
					commaSeparatedAutoSmsIds += aLoyaltyAutoComm.getTierUpgdSmsTmpltId();
					commaSeparatedAutoSmsIds += ",";
				}
				
				if(aLoyaltyAutoComm.getThreshBonusSmsTmpltId() != null && aLoyaltyAutoComm.getThreshBonusSmsTmpltId() > 0){
					commaSeparatedAutoSmsIds += aLoyaltyAutoComm.getThreshBonusSmsTmpltId();
					commaSeparatedAutoSmsIds += ",";
				}
				
				if(aLoyaltyAutoComm.getRewardExpirySmsTmpltId() != null && aLoyaltyAutoComm.getRewardExpirySmsTmpltId() > 0){
					commaSeparatedAutoSmsIds += aLoyaltyAutoComm.getRewardExpirySmsTmpltId();
					commaSeparatedAutoSmsIds += ",";
				}
				
				if(aLoyaltyAutoComm.getMbrshipExpirySmsTmpltId() != null && aLoyaltyAutoComm.getMbrshipExpirySmsTmpltId() > 0){
					commaSeparatedAutoSmsIds += aLoyaltyAutoComm.getMbrshipExpirySmsTmpltId();
					commaSeparatedAutoSmsIds += ",";
				}
				
				if(aLoyaltyAutoComm.getGiftAmtExpirySmsTmpltId() != null && aLoyaltyAutoComm.getGiftAmtExpirySmsTmpltId() > 0){
					commaSeparatedAutoSmsIds += aLoyaltyAutoComm.getGiftAmtExpirySmsTmpltId();
					commaSeparatedAutoSmsIds += ",";
				}
				if(aLoyaltyAutoComm.getGiftMembrshpExpirySmsTmpltId() != null && aLoyaltyAutoComm.getGiftMembrshpExpirySmsTmpltId() > 0){
					commaSeparatedAutoSmsIds += aLoyaltyAutoComm.getGiftMembrshpExpirySmsTmpltId();
					commaSeparatedAutoSmsIds += ",";
				}
				if(aLoyaltyAutoComm.getAdjustmentAutoSmsTmplId() != null && aLoyaltyAutoComm.getAdjustmentAutoSmsTmplId() > 0){
					commaSeparatedAutoSmsIds += aLoyaltyAutoComm.getAdjustmentAutoSmsTmplId();
					commaSeparatedAutoSmsIds += ",";
				}
				if(aLoyaltyAutoComm.getIssuanceAutoSmsTmplId() != null && aLoyaltyAutoComm.getIssuanceAutoSmsTmplId() > 0){
					commaSeparatedAutoSmsIds += aLoyaltyAutoComm.getIssuanceAutoSmsTmplId();
					commaSeparatedAutoSmsIds += ",";
				}
				if(aLoyaltyAutoComm.getRedemptionAutoSmsTmplId() != null && aLoyaltyAutoComm.getRedemptionAutoSmsTmplId() > 0){
					commaSeparatedAutoSmsIds += aLoyaltyAutoComm.getRedemptionAutoSmsTmplId();
					commaSeparatedAutoSmsIds += ",";
				}
				
				
				
				
				
				//commaSeparatedAutoSmsIds += aLoyaltyAutoComm.getProgramId();
				
			}
			
			if(commaSeparatedAutoSmsIds.endsWith(",")){
				commaSeparatedAutoSmsIds = commaSeparatedAutoSmsIds.substring(0, commaSeparatedAutoSmsIds.length() - 1);
			}
			
			if(!commaSeparatedAutoSmsIds.isEmpty())configuredAutoSMSList = autoSMSDao.getAutoSmsTemplateByIds(commaSeparatedAutoSmsIds);
		}catch(Exception e){
			logger.error("Exception >>> ",e);
		}
		//auto sms ids
	}
	
	private void disableMultipleSenderIdsLbId(){
		
		try{
			
			AutoSMS autoSMS = msgListCmbBxId.getSelectedItem().getValue();
			if(autoSMS == null) return; // indicates Default Message
			
			String selectedSenderId = autoSMS.getSenderId();
			
			if(configuredAutoSMSList == null || configuredAutoSMSList.size() == 0) return; // indicates no configured sms
			
			for(AutoSMS aAutoSMS : configuredAutoSMSList){
				
				if(selectedSenderId.equals(aAutoSMS.getSenderId())){
					multipleSenderIdsLbId.setDisabled(true);
					return;
				}
				
			}
			
			
		}catch(Exception e){
			logger.error("Exception >>> ",e);
		}
		
		
		
	}
	
	/*private AutoSMS defaultSettingsForPreConfiguredAutoSMS(String autoSMSType){
		AutoSMS autoSMS = null;
		try{
			
			
			LoyaltyAutoCommDao loyaltyAutoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
			List<LoyaltyAutoComm> autoCommList = loyaltyAutoCommDao.findByUserId(currentUser.getUserId());
			
			
			
			
			
			
			
			Long prgmId = (Long) session.getAttribute("programId");
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			LoyaltyAutoComm loyaltyAutoComm=ltyPrgmSevice.getAutoCommunicationObj(prgmId);
			
			
			
			if(loyaltyAutoComm == null) return null;
			
			
			
			
			if(autoSMSType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REGISTRATION) ){
				Long regSmsTmpltId = loyaltyAutoComm.getRegSmsTmpltId();
				if(regSmsTmpltId != null && regSmsTmpltId != -1 ) {
					autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(regSmsTmpltId);
				}
				
			}else if(autoSMSType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE)) {
				
				Long giftCardIssuanceSmsTmpltId = loyaltyAutoComm.getGiftCardIssuanceSmsTmpltId();
				if(giftCardIssuanceSmsTmpltId != null && giftCardIssuanceSmsTmpltId != -1 ) {
					autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(giftCardIssuanceSmsTmpltId);
				}
				
			}else if(autoSMSType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_TIER_UPGRADATION)) {
				
				Long tierUpgdSmsTmpltId = loyaltyAutoComm.getTierUpgdSmsTmpltId();
				if(tierUpgdSmsTmpltId != null && tierUpgdSmsTmpltId != -1 ) {
					autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(tierUpgdSmsTmpltId);
				}
				
				
			}else if(autoSMSType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION)) {
				
				Long rewardExpirySmsTmpltId = loyaltyAutoComm.getRewardExpirySmsTmpltId();
				if(rewardExpirySmsTmpltId != null && rewardExpirySmsTmpltId != -1 ) {
					autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(rewardExpirySmsTmpltId);
				}
				
			}else if(autoSMSType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION)) {
				
				Long mbrshipExpirySmsTmpltId = loyaltyAutoComm.getMbrshipExpirySmsTmpltId();
				if(mbrshipExpirySmsTmpltId != null && mbrshipExpirySmsTmpltId != -1 ) {
					autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(mbrshipExpirySmsTmpltId);
				}
				
				
			}else if(autoSMSType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_EARNED_BONUS)) {
				
				Long threshBonusSmsTmpltId = loyaltyAutoComm.getThreshBonusSmsTmpltId();
				if(threshBonusSmsTmpltId != null && threshBonusSmsTmpltId != -1 ) {
					autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(threshBonusSmsTmpltId);
				}
				
			}else if(autoSMSType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION)) {
				
				Long giftAmtExpirySmsTmpltId = loyaltyAutoComm.getGiftAmtExpirySmsTmpltId();
				if(giftAmtExpirySmsTmpltId != null && giftAmtExpirySmsTmpltId != -1 ) {
					autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(giftAmtExpirySmsTmpltId);
				}
				
				
			}else if(autoSMSType.equals(OCConstants.AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
				Long giftCardExpirySmsTmpltId = loyaltyAutoComm.getGiftMembrshpExpirySmsTmpltId();
				if(giftCardExpirySmsTmpltId != null && giftCardExpirySmsTmpltId != -1 ) {
					autoSMS = ltyPrgmSevice.getAutoSmsTemplateById(giftCardExpirySmsTmpltId);
				}
			}
			
			logger.info("Returning autosms >>> "+autoSMS);
			return autoSMS;
			
		}catch(Exception e){
			logger.error("Exception >>> ",e);
			return autoSMS;
		}
	}*/

}
