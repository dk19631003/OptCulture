/**
 * 
 */
package org.mq.optculture.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mq.captiway.scheduler.dao.NotificationCampReportListsDaoForDML;


/**
 * @author manjunath.nunna
 *
 */
public interface OCConstants {
	
	public static final String JSON_VALUE				= "jsonValue";
	public static final String MAGENTO_BUSINESS_SERVICE = "magentoBusinessServiceImpl";
	public static final String GATEWAY_BUSINESS_SERVICE = "gatewayBusinessServiceImpl";
	public static final String EQUENCE_HTTPDLR_SERVICE = "EquenceHTTPDLRService";
	
	public static final String WA_BUSINESS_SERVICE = "WABusinessServiceImpl";
	
	public static final String SMSMB_BUSINESS_SERVICE = "SMSBusinessServiceImpl";

	
	String MAGENTO_SERVICE_ACTION_PROMO_ENQUIRY_REQUEST= "promo_enquiry_request";
	
	String MAGENTO_SERVICE_RESPONSE_STATUS_SUCCESS= "success";
	String MAGENTO_SERVICE_RESPONSE_STATUS_FAILURE="failure";
	
	String USER_ALERT_CAMPAIGN_EXPIRED_STATUS_ACTIVE = "A";
	String USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SENT = "S";
		
	String EMAILQUEUEDAO = "emailQueueDao";
	String CUSTOMTEMPLATES_DAO = "customTemplatesDao";
	String EMAILQUEUE_DAO_ForDML="emailQueueDaoForDML";
	String CHARACTERCODESDAO = "CharacterCodesDao";
	String COUPONCODES_DAO = "couponCodesDao";
	
	
	
	String REFERRALCODES_DAO = "referralCodesIssuedDao";
	String REFERRAL_STATUS_ACTIVE = "Active";
	String REFERRAL_STATUS_RUNNING = "Running";

	String REFERRAL_PROGRAM_DAO ="referralProgramDao";

	String COUPONCODES_DAOForDML = "couponCodesDaoForDML";
	
	String REFERRALCODES_DAOFORDML="referralCodesIssuedDaoForDML";
									
	
	String COUPONS_DAO = "couponsDao";
	String COUPONS_DAOForDML = "couponsDaoForDML";
	String USERS_DAO = "usersDao";
	String USERS_DAOForDML = "usersDaoForDML";
	String COUPON_DICOUNT_GENERATE_DAO = "coupDiscGenDao";
	String CONTACTS_DAO = "contactsDao";
	String CONTACTS_DAO_FOR_DML = "contactsDaoForDML";
	String POSMAPPING_DAO = "posMappingDao";
	String SMSSETTINGS_DAO = "smsSettingsDao";
	String MAILINGLIST_DAO = "mailingListDao";
	String MAILINGLIST_DAO_FOR_DML = "mailingListDaoForDML";
	String ORGSMSKEYWORDS_DAO = "orgSMSkeywordsDao";
	String ORGSMSKEYWORDS_DAO_FOR_DML = "orgSMSkeywordsDaoForDML";
	String CLICKATELLSMSINBOUND_DAO = "clickaTellSMSInboundDao";
	String CLICKATELLSMSINBOUND_DAO_FOR_DML = "clickaTellSMSInboundDaoForDML";
	String USERSMSGATEWAY_DAO = "userSMSGatewayDao";
	String OCSMSGATEWAY_DAO = "OCSMSGatewayDao";
	String SMS_SUPPRESSED_CONTACTS_DAO = "smsSuppressedContactsDao";
	String SMS_SUPPRESSED_CONTACTS_DAO_FOR_DML = "smsSuppressedContactsDaoForDML";
	String SMS_CAMPAIGNREPORT_DAO = "smsCampaignReportDao";
	String SMS_CAMPAIGNREPORT_DAO_FOR_DML = "smsCampaignReportDaoForDML";
	String SMS_CAMPAIGNSENT_DAO = "smsCampaignSentDao";
	String SMS_CAMPAIGNSENT_DAO_ForDML = "smsCampaignSentDaoForDML";
	String SMS_CAMPAIGNREPORT_DAO_ForDML="smsCampaignReportDaoForDML";
	String SMS_CLICKS_DAO = "smsClicksDao";
	String SMS_CLICKS_DAO_ForDML = "smsClicksDaoForDML";
	String SMSBOUNCES_DAO = "smsBouncesDao";
	String SMSBOUNCES_DAO_ForDML = "smsBouncesDaoForDML";
	String URLSHORTCODEMAPPING_DAO = "urlShortCodeMappingDao";
	String SMSCAMPAIGNURL_DAO = "SMSCampaignUrlDao";
	String SMSCAMPAIGNURL_DAO_FOR_DML = "SMSCampaignUrlDaoForDML";
	String SMSCAMPAIGNSENTURLSHORTCODE_DAO = "smsCampaignSentUrlShortCodeDao";
	String SMSCAMPAIGNSENTURLSHORTCODE_DAO_FOR_DML = "smsCampaignSentUrlShortCodeDaoForDML";
	String AUTOSMS_SENTURLSHORTCODE_DAO = "autoSMSSentUrlShortCodeDao";
	String AUTOSMS_SENTURLSHORTCODE_DAO_FOR_DML = "autoSMSSentUrlShortCodeDaoForDML";
	String AUTOSMS_URL_DAO_FOR_DML = "autosmsURLDaoForDml";
	String DR_SMS_SENT_DAO = "drSmsSentDao";
	String DR_SMS_SENT_DAO_ForDML = "drSmsSentDaoForDML";
	String DR_SMS_CHANNEL_SENT_DAO = "drSmsChannelSentDao";
	String DR_SMS_CHANNEL_SENT_DAO_ForDML = "drSmsChannelSentDaoForDML";
	String REISSUE_PERKS_SERVICE_ENDPOINT = "reIssuePerksServiceEndPoint";
	
	//wa
	String WA_CAMPAIGNSENT_DAO = "waCampaignSentDao";
	String WA_CAMPAIGNSENT_DAO_ForDML = "waCampaignSentDaoForDML";
	String WA_CAMPAIGNREPORT_DAO_FOR_DML = "waCampaignReportDaoForDML";
	
	//String CAPTIWAYTOSMSAPIGATEWAY_SERVICE = "captiwayToSMSApiGateway";
	String OCMEDIA_SERVICE = "ocMediaServiceImpl";
	String USERSMSSENDERID_DAO = "userSMSSenderIdDao";
	String DIGITAL_RECEIPT_USER_SETTINGS_DAO = "digitalReceiptUserSettingsDao";
	String SMSGATEWAYSESSIONMONITOR = "sessionMonitor";
	String SUPPRESSEDCONTACTS_DAO="suppressedContactsDao";
	String SUPPRESSEDCONTACTS_DAO_FOR_DML = "suppressedContactsDaoForDML";
	String USER_CAMPAIGN_EXPIRATION_DAO = "userCampaignExpirationDao";
	String USER_CAMPAIGN_EXPIRATION_DAO_FOR_DML = "userCampaignExpirationDaoForDML";

	String CAMPAIGNS_DAO = "campaignsDao";
	String CAMPAIGN_REPORT_DAO = "campaignReportDao";
	//SMS in-bound / miscall optin request parameter names
	String AUTO_SMS_QUEUE_DAO = "autoSmsQueueDao";
	String AUTO_SMS_QUEUE_DAO_FOR_DML = "autoSmsQueueDaoForDML";
	String EVENT_TRIGGER_DAO = "eventTriggerDao";
	String EVENT_TRIGGER_EVENTS_DAO = "eventTriggerEventsDao";
	String CONTACTS_LOYALTY_DAO = "contactsLoyaltyDao";
	String CONTACTS_LOYALTY_DAO_FOR_DML = "contactsLoyaltyDaoForDML";
	String EMAILQUEUE_DAO = "emailQueueDao";
	String MESSAGES_DAO = "messagesDao";
	//APP-4253
	String SMS_SUPPRESSEDCONTACT_DAO = "smsSuppressedContactsDao";  
	String SMS_SUPPRESSEDCONTACT_DAO_FOR_DML = "smsSuppressedContactsDaoForDML";
	
	/**
	 * Loyalty DAO
	 */
	String LOYALTY_PROGRAM_TIER_DAO="loyaltyProgramTierDao";
	String LOYALTY_THRESHOLD_BONUS_DAO = "loyaltyThresholdBonusDao";
	String LOYALTY_PROGRAM_DAO = "loyaltyProgramDao";
	String LOYALTY_TRANSACTION_CHILD_DAO = "loyaltyTransactionChildDao";
	String LOYALTY_SETTINGS_DAO = "loyaltySettingsDao";
	String LOYALTY_TRANSACTION_EXPIRY_DAO = "loyaltyTransactionExpiryDao";
	String LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML = "loyaltyTransactionExpiryDaoForDML";
	String LTY_SETTINGS_ACTIVITY_LOGS_DAO = "ltySettingsActivityLogsDao";
	String LTY_SETTINGS_ACTIVITY_LOGS_DAO_FOR_DML = "ltySettingsActivityLogsDaoForDML";
	String LOYALTY_BALANCE_DAO="loyaltyBalanceDao";
	String SPECIAL_REWARD_DAO_FOR_DML = "specialRewardDaoForDML";
	String SPECIAL_REWARD_DAO = "specialRewardDao";
	/**
	 * Added for SmsQueueDao
	 */
	String SMS_QUEUE_DAO = "smsQueueDao";
	String SMS_QUEUE_DAO_For_DML = "smsQueueDaoForDML";
	
	/**
	 * Added for notificationDao
	 */
	String MESSAGES_DAO_FOR_DML= "messagesDaoForDML";
	String NOTIFICATION_QUEUE= "notificationQueue";
	String NOTIFICATION_DAO="notificationDao";
	String NOTIFICATION_DAO_FOR_DML="notificationDaoForDML";
	String NOTIFICATION_SCHEDULE_DAO = "notificationScheduleDao";
	String NOTIFICATION_SCHEDULE_DAO_FOR_DML="notificationScheduleDaoForDML";
	String SEGMENTRULES_DAO_FOR_DML="segmentRulesDaoForDML";
	String SEGMENTRULES_DAO="segmentRulesDao";
    String NOTIFICATION_CAMPAIGNREPORT_DAO_FOR_DML="notificationCampaignReportDaoForDML";
    String NOTIFICATION_CAMPAIGN_SENT_DAO="notificationCampaignSentDao";
    String NOTIFICATION_CAMPAIGN_SENT_DAO_FOR_DML="notificationCampaignSentDaoForDML";
    String NOTIFICATION_SUPPRESSED_DAO_FOR_DML = "notificationSuppressedDaoForDML";
    String NOTIFICATION_CAMP_REPORTLISTS_DAO_FOR_DML = "notificationCampReportListsDaoForDML";

    
	String REQUEST_PARAM_TYPE = "type";
	String REQUEST_PARAM_TYPE_VALUE_SMS = "IncomingSMS";
	String REQUEST_PARAM_TYPE_VALUE_MISSEDCALL = "MissedCall";
	String REQUEST_PARAM_TYPE_VALUE_DLR = "DLR";
	String REQUEST_PARAM_SOURCE = "Source";
	String REQUEST_PARAM_DEST = "dest";
	String REQUEST_PARAM_MISSEDCALL_STIME = "Stime";
	String REQUEST_PARAM_MSG_STIME = "stime";
	String REQUEST_PARAM_MSG_DTIME = "dtime";
	String REQUEST_PARAM_MSG = "msg";//MM/DD/YY HH:MM:SS PM/AM
	String REQUEST_PARAM_STATUS = "status";
	String REQUEST_PARAM_REASON = "reason";
	String REQUEST_PARAM_URLSHORTCODE= "code";
	String REQUEST_PARAM_ACTION_SHORTCODE = "shortCode";
	String REQUEST_PARAM_MSGID_SID = "sid";
	String REQUEST_PARAM_MSGID_APIMSGID = "apiMsgId";
	String REQUEST_PARAM_MSGID_CLIENTMSGID = "cliMsgId";
	//Dest Source Stime msg= "type=SMS/MissedCall send dest stime=MM/DD/YY HH:MM:SS PM/AM

	String LOYALTY_TRANS_TYPE_RETURN = "Return";
	String LOYALTY_TRANSACTION_ISSUANCE = "Issuance";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_GIFT = "Gift";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD = "Reward";
	
	//added for equence
	String REQUEST_PARAM_MSGID="msg_id";
	String REQUEST_PARAM_MRID="mr_id";
	String REQUEST_PARAM_MOBILE_NO="mobile_no";
	String REQUEST_PARAM_MESSAGE="message";
	//String REQUEST_PARAM_SUBMITTED_TIME="msg_sent_dttime"
	String REQUEST_PARAM_DELIVERED_TIME="sms_delv_dttime";
	String REQUEST_PARAM_EQ_STATUS="sms_delv_status";
	String REQUEST_PARAM_EQ_REASON="remarks";
	
	
	
	String SMS_PROGRAM_KEYWORD_TYPE_OPTIN = "Optin";
	String SMS_PROGRAM_KEYWORD_TYPE_OPTOUT = "OptOut";
	String SMS_PROGRAM_KEYWORD_TYPE_HELP = "Help";
	
	String REQUEST_PARAM_TEXT = "text";
	String REQUEST_PARAM_FROM = "from";
	String REQUEST_PARAM_TO = "to";
	String REQUEST_PARAM_TIMESTAMP = "timestamp";
	String IMAGE_FORMAT_PNG = "png";
	String SMS_URL_PATTERN = "SMSShortUrlPattern";
	String SHORTURL_CODE_PREFIX_U = "U";
	String SHORTURL_CODE_PREFIX_S = "S";
	String SHORTURL_CODE_PREFIX_a = "a";
	String SHORTURL_CODE_PREFIX_DR = "DR";
	String SHORTURL_CODE_PREFIX_COUPONPH = "[CC_";
	String SHORTURL_TYPE_SHORTCODE = "S";
	String SHORTURL_TYPE_BARCODE_TYPE_MULTIPLE = "BM";
	String SHORTURL_TYPE_BARCODE_TYPE_SINGLE = "BS";
	String SHORTURL_CODE_PREFIX_BARCODE_TYPE_LINEAR = "L";
	String SHORTURL_CODE_PREFIX_BARCODE_TYPE_QR = "Q";
	String SHORTURL_CODE_PREFIX_BARCODE_TYPE_DATAMATRIX = "D";
	String SHORTURL_CODE_PREFIX_BARCODE_TYPE_AZETEC = "A";
	String MOBILE_OPTIN_SOURCE_KEYWORD = "Opt-in Keyword";
	String MOBILE_OPTIN_SOURCE_MISSEDCALL = "Missed-call";// (in India)
	String MOBILE_OPTIN_SOURCE_WEBFORM = "Web-form";
	String MOBILE_OPTIN_SOURCE_PHYSICALFORM = "physical-form";
	Set<String> GLOBAL_OPTOUT_KEYWORDS = new HashSet<String>() {
		{
			add("STOP");
			add("END");
			add("CANCEL");
			add("UNSUBSCRIBE");
			add("QUIT");
			add("STOP ALL");
		    
		}
	};
	
	String SMS_KEYWORD_EXPIRED = "Expired";
	
	
	String SMS_GATEWAY_UNICEL = "Unicel";
	String SMS_GATEWAY_UNICEL_OPTIN_USER_TRESMODE = "Tresmode";
	String SMS_GATEWAY_UNICEL_OPTIN_USER_MAGOPTIN = "magoptin";
	String SMS_GATEWAY_MVaayoo = "MVaayoo";
	//Added for InfoBip
	String SMS_GATEWAY_INFOBIP = "Infobip";
	String SMS_GATEWAY_CM = "CMCom";
	String INFOBIP_ACC_RISSERVICES="risservices";
	String INFOBIP_ACC_ZAKS="ZAKS";
	String INFOBIP_ACC_HOF="amith";
	String INFOBIP_ACC_USER1="InfobipUser1";
	String INFOBIP_ACC_USER2="InfobipUser2";
	String INFOBIP_ACC_USER3="InfobipUser3";
	String INFOBIP_ACC_USER4="InfobipUser4";
	String INFOBIP_ACC_USER5="InfobipUser5";
	
	String INFOBIP_ACC_USER6="InfobipUser6";
	String INFOBIP_ACC_USER7="InfobipUser7";
	String INFOBIP_ACC_USER8="InfobipUser8";
	String INFOBIP_ACC_USER9="InfobipUser9";
	String INFOBIP_ACC_USER10="InfobipUser10";	
	String INFOBIP_ACC_USER_OPTAFRICA="optafrica";
	
	
	
	String LOYALTY_TRANS_TYPE_ENROLLMENT = "Enrollment";
	String LOYALTY_TRANS_TYPE_ENROLLMENTHISTORY = "EnrollmentHistory";
	String LOYALTY_TRANS_TYPE_ISSUANCE = "Issuance";
	String LOYALTY_TRANS_TYPE_BONUS = "Bonus";
	String LOYALTY_TRANS_TYPE_REDEMPTION = "Redemption";
	String LOYALTY_TRANS_TYPE_ADJUSTMENT = "Adjustment";
	String LOYALTY_TRANSACTION_RETURN = "Return";
	String LOYALTY_TRANSACTION_REVERSAL = "Reversal";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL = "IssuanceReversal";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL = "RedemptionReversal";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT = "StoreCredit";
	//String GIFT_TRANS_TYPE_ISSUANCE = "Gift Issuance";
	//String GIFT_TRANS_TYPE_REDEMPTION = "Gift Redemption";
	String LOYALTY_TRANS_TYPE_INQUIRY = "Inquiry";
	String LOYALTY_TRANS_TYPE_EXPIRY = "Expiry";
	
	String LOYALTY_PROGRAM_TRANS_STATUS_NEW = "New";
	String LOYALTY_PROGRAM_TRANS_STATUS_WORKING = "Working";
	String LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED = "Processed";
	String LOYALTY_PROGRAM_TRANS_STATUS_SUSPENDED = "Suspended";
	
	String LOYALTY_SERVICE_TYPE_SB = "SB";
	String LOYALTY_SERVICE_TYPE_OC = "OC";
	
	String LOYALTY_PROGRAM_TIER1 = "Tier 1";
	String LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH = "Month";
	String LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR = "Year";
	
	//Loyalty Program
	char FLAG_YES = 'Y';
	char FLAG_NO = 'N';
	String LOYALTY_TYPE_POINTS = "Points";
	String LOYALTY_TYPE_CURRENCY = "Currency";
	String LOYALTY_TYPE_AMOUNT = "Amount";
	String LOYALTY_TYPE_PERCENTAGE = "Percentage";
	String LOYALTY_TYPE_VALUE = "Value";
	String LOYALTY_TYPE_DAY = "Day";
	String LOYALTY_TYPE_HOUR = "Hour" ;
	String LOYALTY_TYPE_YEAR = "Year" ;
	String LOYALTY_TYPE_MONTH = "Month" ;
	String LOYALTY_TYPE_PURCHASE = "Purchase";
	String LOYALTY_TYPE_GIFT = "Gift";
	String LOYALTY_TYPE_REWARD = "Reward";
	String REWARD_TYPE_PERK = "Perk";
	String PERKS_EXP_TYPE_QUARTER = "Quarter";
	String PERKS_EXP_TYPE_HALFYEAR = "Half Year";
	String PERKS_EXP_TYPE_YEAR = "Year";
	String PERKS_EXP_TYPE_MONTH = "Month";
	
	String LOYALTY_CARDSET_TYPE_VIRTUAL = "Virtual";
	String LOYALTY_CARDSET_TYPE_PHYSICAL = "Physical";
	String LOYALTY_CARDSET_GEN_TYPE_SEQUENTIAL = "Sequential";
	String LOYALTY_CARDSET_GEN_TYPE_RANDOM = "Random";
	String LOYALTY_CARDSET_STATUS_ACTIVE = "Active";
	String LOYALTY_CARDSET_STATUS_SUSPENDED = "Suspended";
	String LOYALTY_PROGRAM_STATUS_ACTIVE = "Active";
	String LOYALTY_PROGRAM_STATUS_SUSPENDED = "Suspended";
	String LOYALTY_PROGRAM_STATUS_DRAFT = "Draft";
	
	String LOYALTY_REG_REQUISITE_FIRSTNAME = "firstName";
	String LOYALTY_REG_REQUISITE_LASTNAME = "lastName";
	String LOYALTY_REG_REQUISITE_MOBILEPHONE = "mobilePhone";
	String LOYALTY_REG_REQUISITE_EMAILID = "emailId";
	String LOYALTY_REG_REQUISITE_ADDRESSONE = "addressOne";
	String LOYALTY_REG_REQUISITE_CITY = "city";
	String LOYALTY_REG_REQUISITE_STATE = "state";
	String LOYALTY_REG_REQUISITE_ZIP = "zip";
	String LOYALTY_REG_REQUISITE_COUNTRY = "country";
	String LOYALTY_REG_REQUISITE_BIRTHDAY = "birthDay";
	String LOYALTY_REG_REQUISITE_ANNIVERSARY = "anniversary";
	String LOYALTY_REG_REQUISITE_GENDER = "gender";
	
	String LOYALTY_PRODUCT_ITEMCATEGORY = "itemCategory";
	String LOYALTY_PRODUCT_DEPARTMENTCODE = "departmentCode";
	String LOYALTY_PRODUCT_CLASS = "classCode";
	String LOYALTY_PRODUCT_SUBCLASS = "subClassCode";
	String LOYALTY_PRODUCT_DCS = "DCS";
	String LOYALTY_PRODUCT_VENDORCODE = "vendorCode";
	String LOYALTY_PRODUCT_SKUNUMBER = "sku";
	
	String LOYALTY_LIFETIME_POINTS = "LifetimePoints";
	String LOYALTY_LIFETIME_PURCHASE_VALUE = "LifetimePurchaseValue";
	String LOYALTY_CUMULATIVE_PURCHASE_VALUE = "CumulativePurchaseValue";
	String LOYALTY_DRAFT_STATUS_COMPLETE = "Complete";
	String LOYALTY_DRAFT_STATUS_INCOMPLETE = "Incomplete";
	
	String LOYALTY_PROMO_EXCLUSION_ALL = "ALL";
	
	String CONTACTS_LOYALTY_STATUS_ACTIVE = "Active";
	String CONTACTS_LOYALTY_STATUS_EXPIRED = "Expired";
	
	
	String LOYALTY_MEMBERSHIP_STATUS_ACTIVE = "Active";
	String LOYALTY_MEMBERSHIP_STATUS_EXPIRED = "Expired";
	String LOYALTY_MEMBERSHIP_STATUS_SUSPENDED = "Suspended";
	String LOYALTY_MEMBERSHIP_STATUS_CLOSED = "Closed";
	
	String LOYALTY_MEMBERSHIP_TYPE_CARD = "Card";
	String LOYALTY_MEMBERSHIP_TYPE_MOBILE = "Mobile";
	
	String LOYALTY_CARD_STATUS_ACTIVATED = "Activated";
	//String LOYALTY_CARD_STATUS_EXPIRED = "Expired";
	String LOYALTY_CARD_STATUS_ENROLLED = "Enrolled";
	String LOYALTY_CARD_STATUS_INVENTORY = "Inventory";
	String LOYALTY_CARD_STATUS_SELECTED = "Selected";
	
	String LOYALTY_CONVERSION_TYPE_AUTO = "Auto";
	String LOYALTY_CONVERSION_TYPE_ONDEMAND = "Ondemand";
	
	// start entered amount types in transactionchild table
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT = "Gift";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE = "Purchase";
	//String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_BONUS = "Bonus";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM = "PointsRedeem";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM = "AmountRedeem";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSADJUSTMENT = "PointsAdjustment";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTADJUSTMENT = "AmountAdjustment";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTS_EXP = "PointsExp";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNT_EXP = "AmountExp";
	String LOYALTY_MEMBERSHIP_REWARD_FLAG_G = "G"; //FOR GIFT CARD
	String LOYALTY_MEMBERSHIP_REWARD_FLAG_L = "L"; //FOR LOYALTY CARD
	String LOYALTY_MEMBERSHIP_REWARD_FLAG_GL = "GL"; //FOR GIFT TO LOYALTY
	
	String LOYALTY_EXPIRY_TYPE_POINTS = "Points";
	String LOYALTY_EXPIRY_TYPE_AMOUNT = "Amount";
	String LOYALTY_EXPIRY_TYPE_GIFT = "Gift";
	
	String LOYALTY_TRANSACTION_SOURCE_TYPE_STORE = "Store";
	String LOYALTY_TRANSACTION_SOURCE_TYPE_AUTO = "Auto";
	String LOYALTY_TRANSACTION_SOURCE_TYPE_MANUAL = "Manual";
	
	String LOYALTY_ENROLLHISTORY_TYPE_DETAILED = "Detailed";
	String LOYALTY_ENROLLHISTORY_TYPE_SUMMARY = "Summary";
	String LOYALTY_ENROLLHISTORY_SOURCE_ALL = "All";
	String LOYALTY_ENROLLHISTORY_SOURCE_STORE = "Store";
	String LOYALTY_ENROLLHISTORY_SOURCE_WEBFORM = "Webform";
	String LOYALTY_ENROLLHISTORY_MODE_ALL = "All";
	String LOYALTY_ENROLLHISTORY_MODE_Online = "Online";
	String LOYALTY_ENROLLHISTORY_MODE_Offline = "Offline";
	String LOYALTY_ENROLLHISTORY_SERVICETYPE_OC = "OC";
	String LOYALTY_ENROLLHISTORY_SERVICETYPE_SB = "SB";
	String LOYALTY_ENROLLHISTORY_SERVICETYPE_ALL = "All";
	
	//end entered amount types in transactionchild table
	
	//Added for auto-sms
	String ASQ_STATUS_ACTIVE = "Active";
	String ASQ_STATUS_SENT   = "Sent";
	String ASQ_STATUS_FAILURE   = "Failure";
	String ASQ_STATUS_PROCESSING   = "Processing";

	String TRANSACTIONAL_TEMPLATE_TYPE_AS = "AS";
	String AUTO_SMS_MESSAGE_TYPE_TW = "TW";
	String AUTO_SMS_MESSAGE_TYPE_TR = "TR";
	
	
     String AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REGISTRATION= "Loyalty Registration";
     String AUTO_SMS_TEMPLATE_TYPE_TIER_UPGRADATION="Tier Upgradation";
     String AUTO_SMS_TEMPLATE_TYPE_EARNED_BONUS="Earning Bonus";
     String AUTO_SMS_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION="Earned Reward Expiration";
     String AUTO_SMS_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION = "Membership Expiration";
     String AUTO_SMS_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION = "Gift Amount Expiration";
     String AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION="Gift Card Expiration";
     String AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE="Gift Card Issuance";
	
     String AUTO_SMS_TEMPLATE_STATUS_APPROVED = "A";
     String AUTO_SMS_TEMPLATE_STATUS_PENDING = "P";
	
     String AUTOSMS_OBJECTS_QUEUE = "autoSMSObjectsQueue";
     
   //Auto-SMS Queue Types
    String ASQ_TYPE_LOYALTY_DETAILS = "LoyaltyDetails";
  	String ASQ_TYPE_LOYALTY_GIFT_CARD_ISSUANCE = "LoyaltyGiftCardIssuance";
  	String ASQ_TYPE_LOYALTY_TIER_UPGRADATION = "LoyaltyTierUpgradation";
  	String ASQ_TYPE_LOYALTY_EARNING_BONUS = "LoyaltyEarningBonus";
  	String ASQ_TYPE_LOYALTY_REWARD_EXPIRY = "LoyaltyRewardExpiry";
  	String ASQ_TYPE_LOYALTY_MEMBERSHIP_EXPIRY = "LoyaltyMembershipExpiry";
  	String ASQ_TYPE_LOYALTY_GIFT_CARD_EXPIRY = "LoyaltyGiftCardExpiry";
  	String ASQ_TYPE_LOYALTY_GIFT_AMOUNT_EXPIRY = "LoyaltyGiftAmountExpiry";
	
  	//oc loyalty event trigger status
  	
  	 String LOYALTY_TRANSACTION_ET_STATUS_NEW="New";
	 String LOYALTY_TRANSACTION_ET_STATUS_PROCESSED="Processed";
	 String LOYALTY_TRANSACTION_ET_STATUS_UNPROCESSED="Unprocessed";
	 
	 
	 String LOYALTY_ADJUSTMENT = "Loyalty Adjustment";
	 String LOYALTY_ISSUANCE = "Loyalty Issuance";
	 String LOYALTY_REDEMPTION = "Loyalty Redemption";
	 String LOYALTY_GIFT_ISSUANCE = "Gift Issuance";
	 String LOYALTY_GIFT_REDEMPTION = "Gift Redemption";

	 //OCLOYALTY URL STRING
	 //String LOYALTY_LOGIN_URL= "www.getyourbalance.com/";
	 //One or More Years or Months
	 String MORETHANONEOCCURENCE="(s)";

	String MSG_CAMP_EXPIRATION = "msgCampExpiration";
	
	String CAMP_EXPIRATION_EMAIL_PH_USERNAME = "[UserName]";
	String CAMP_EXPIRATION_EMAIL_PH_CAMPNAME = "[campName]";
	String CAMP_EXPIRATION_EMAIL_PH_STARTDATE = "[startDate]";
	String CAMP_EXPIRATION_EMAIL_PH_ENDDATE = "[endDate]";
	String CAMP_EXPIRATION_EMAIL_PH_FREEQUENCY = "[freequency]";
	String CAMP_EXPIRATION_EMAIL_PH_LASTSENTON = "[lastSentOn]";
	String CAMP_EXPIRATION_EMAIL_PH_SENTCOUNT = "[sent]";
	String CAMP_EXPIRATION_EMAIL_PH_UNDELIVERED = "[undelivered]";
	String CAMP_EXPIRATION_EMAIL_PH_OPENS = "[opens]";
	String CAMP_EXPIRATION_EMAIL_PH_CLICKS = "[clicks]";
	
	/**
	 * No SUFFICIENT CREDITS while sending campaign
	 */
	String NO_SUFFICIENT_CREDITS_CAMPAIGN_SUBJECT = "InsufficientCreditsSubject";
	String NO_SUFFICIENT_CREDITS_CAMPAIGN_EMAIL_CONTENT = "InsufficientCreditsContent";
	
	// added after BCC email ID
	String BCC_EMAIL_ID_STR="bccEmailStr";
	String SMS_KEYWORD_MSG_HELP = "HelpMsg";
	String SMS_KEYWORD_MSG_STOP = "StopMsg";
	String SMS_KEYWORD_HELP_CLICKATEL = "ClickatellHelpKeyWord";
	String SMS_GLOBALKEYWAORDS_CLICKATEL = "ClickatellGlobalKeywords";
	
	public String LTY_ACTIVITY_LOG_TYPE_PROGRAM = "P";
	public String LTY_ACTIVITY_LOG_TYPE_TIER = "T";
//	public String LTY_ACTIVITY_LOG_TYPE_REGISTRATION_BONUS = "RB";
	public String LTY_ACTIVITY_LOG_TYPE_BONUS = "B";
	public String LTY_ACTIVITY_LOG_TYPE_LOYALTY_REWARD_VALIDITY = "LRV";
	public String LTY_ACTIVITY_LOG_TYPE_GIFT_AMOUNT_VALIDITY = "GAV";
	public String LTY_ACTIVITY_LOG_TYPE_LOYALTY_MEMBERSHIP_VALIDITY = "LMV";
	public String LTY_ACTIVITY_LOG_TYPE_GIFT_CARD_VALIDITY = "GCV";
	
	public String LTY_SETTING_REPORT_FRQ_DAY = "D";
	public String LTY_SETTING_REPORT_FRQ_WEEK = "W";
	public String LTY_SETTING_REPORT_FRQ_MONTH ="M";
	public String LTY_SETTING_REPORT_TYPE ="Loyalty Report";
	public String LTY_SETTNG_REPORT_TRIGGER_F = "FM";
	public String LTY_SETTNG_REPORT_TRIGGER_L = "LM";
	public String LOYALTY_FRAUD_ALERT_REPORT="FraudAlert Report";
	//added for pos settings
	
	public String USER_POS_FTP_SETTINGS_DAO_FOR_DML = "userPosFTPSettingsDaoForDML";
	
	//SMS not submitted status
	public String SMS_NOT_SUBMITTED="NotSubmitted";
	
	/**
	 * The scheduled campaign failed to send
	 */
	String CAMPAIGN_FAILED_TO_SEND_SUBJECT = "smsCampaignFailedToSendSubject";
	String SMS_CAMPAIGN_FAILED_TO_SEND_CONTENT= "smsCampaignFailedToSendContent";
	String EMAIL_CAMPAIGN_FAILED_TO_SEND_CONTENT = "emailCampaignFailedToSendContent";
	String SMS_CAMPAIGN_NOT_SUBMITTED_CONTENT = "smsCampaignNotSubmittedContent";
	String CAMPAIGN_WITH_NEGATIVE_EMAIL_CREDIT = "campaignWithNegativeEmailCredit";
	String CAMPAIGN_WITH_NEGATIVE_EMAIL_CREDIT_BODY = "campaignWithNegativeEmailCreditBody";
	String NOTIFICATION_CAMPAIGN_FAILED_TO_SEND_CONTENT= "notificationCampaignFailedToSendContent";
	String CAMPAIGN_DRAFT_SCH_ACTIVE_ALERT_SUBJECT = "campaignDraftSchActiveAlertSubject";
	String CAMPAIGN_DRAFT_SCH_ACTIVE_CONTENT = "campaignDraftSchActiveAlertContent";
	
	String CAMPAIGN_NAME = "<CAMPAIGNNAME>";
	String ERROR_STATUS = "<ERROR_STATUS>";
	String SCHEDULED_DATE = "<REQUEST_DATE>";
	String SUBJECT_LINE = "<SUBJECT_LINE>";
	String CONFIGURED_COUNT = "<CONFIGURED_COUNT>";
	String NOT_SUBMITTED_COUNT ="<NOT_SUBMITTED_COUNT>";
	String USER_ORGID= "[orgid]";
	String USER_FNAME= "[fname]";
	
	String NUM_OF_HRS_BEFORE_CAMP_EXPIRES="NumOfHrsBeforeCampExpires";
	
	String DR_ONLINE_MODE = "online";
	String DR_OFFLINE_MODE = "offline";
	String DR_EXTRACTION_THREADS_KEY = "DRExtractionThreadCount";
	String AutoSMSPrefix = "A";
	String DR_SOURCE_TYPE_PRISM = "Prism";
	String DR_SOURCE_TYPE_OPTDR = "OptDR";
	String DR_SOURCE_TYPE_Magento = "Magento";
	String DR_SOURCE_TYPE_WooCommerce = "wooCommerce";
	String DR_SOURCE_TYPE_Shopify = "Shopify";
	String DR_SOURCE_TYPE_HeartLand = "HeartLand";
	String DR_SOURCE_TYPE_ORION = "Orion";
	
	String DR_RECEIPT_TYPE_SALE = "Sale";
    String DR_RECEIPT_TYPE_RETURN = "Return";
    String DR_PROMO_DISCOUNT_TYPE_ITEM = "Item";
    String DR_PROMO_DISCOUNT_TYPE_RECEIPT = "Receipt";
	String Mail_To_Support = "MailToSupport";
	String DRToLtyFailureSubject = "DRToLtyFailureSubject";
	String DRToLtyFailureMessage = "DRToLtyFailureMessage";
	String DOCSID="<DOCSID>";
	String DOC_DATE = "<DOC_DATE>";
	String USERNAME = "<USERNAME>";
	
	String NOTIFICATION_IMAGE_TYPE_PICTURE = "picture";
	String NOTIFICATION_IMAGE_TYPE_INBOX = "inbox";

	String segmentQryPrefiX = "SELECT DISTINCT c.*<SELECTFIELDS>";
	String replacedSegmentQryPrefiX = "SELECT DISTINCT c.*,loyalty.card_number, loyalty.card_pin, loyalty.loyalty_balance, loyalty.giftcard_balance, loyalty.gift_balance,"
			+ " loyalty.holdpoints_balance, loyalty.holdAmount_balance";
	String replaceLtySelectStr = "loyalty.card_number, loyalty.card_pin, loyalty.loyalty_balance, loyalty.giftcard_balance, loyalty.gift_balance,"
			+ " loyalty.holdpoints_balance, loyalty.holdAmount_balance ";

	
}
