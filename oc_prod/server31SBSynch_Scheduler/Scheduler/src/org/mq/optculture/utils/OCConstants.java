/**
 * 
 */
package org.mq.optculture.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author manjunath.nunna
 *
 */
public interface OCConstants {
	
	public static final String JSON_VALUE				= "jsonValue";
	public static final String MAGENTO_BUSINESS_SERVICE = "magentoBusinessServiceImpl";
	public static final String GATEWAY_BUSINESS_SERVICE = "gatewayBusinessServiceImpl";
	
	String MAGENTO_SERVICE_ACTION_PROMO_ENQUIRY_REQUEST= "promo_enquiry_request";
	
	String MAGENTO_SERVICE_RESPONSE_STATUS_SUCCESS= "success";
	String MAGENTO_SERVICE_RESPONSE_STATUS_FAILURE="failure";
	
	String USER_ALERT_CAMPAIGN_EXPIRED_STATUS_ACTIVE = "A";
	String USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SENT = "S";
		
	String EMAILQUEUEDAO = "emailQueueDao";
	String EMAILQUEUE_DAO_ForDML="emailQueueDaoForDML";
	String CHARACTERCODESDAO = "CharacterCodesDao";
	String COUPONCODES_DAO = "couponCodesDao";
	String COUPONCODES_DAOForDML = "couponCodesDaoForDML";
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
	
	/**
	 * Loyalty DAO
	 */
	String LOYALTY_PROGRAM_TIER_DAO="loyaltyProgramTierDao";
	String LOYALTY_PROGRAM_DAO = "loyaltyProgramDao";
	String LOYALTY_TRANSACTION_CHILD_DAO = "loyaltyTransactionChildDao";
	String LOYALTY_SETTINGS_DAO = "loyaltySettingsDao";
	String LOYALTY_TRANSACTION_EXPIRY_DAO = "loyaltyTransactionExpiryDao";
	String LTY_SETTINGS_ACTIVITY_LOGS_DAO = "ltySettingsActivityLogsDao";
	String LTY_SETTINGS_ACTIVITY_LOGS_DAO_FOR_DML = "ltySettingsActivityLogsDaoForDML";
	/**
	 * Added for SmsQueueDao
	 */
	String SMS_QUEUE_DAO = "smsQueueDao";
	String SMS_QUEUE_DAO_For_DML = "smsQueueDaoForDML";

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
	String INFOBIP_ACC_RISSERVICES="risservices";
	String INFOBIP_ACC_ZAKS="ZAKS";
	String INFOBIP_ACC_HOF="amith";
	String INFOBIP_ACC_USER1="InfobipUser1";
	String INFOBIP_ACC_USER2="InfobipUser2";
	String INFOBIP_ACC_USER3="InfobipUser3";
	String INFOBIP_ACC_USER4="InfobipUser4";
	String INFOBIP_ACC_USER5="InfobipUser5";
	
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
	 
	 
	 String LOYALTY_ADJUSTMENT = "Loyaty Adjustment";
	 String LOYALTY_ISSUANCE = "Loyaty Issuance";
	 String LOYALTY_REDEMPTION = "Loyaty Redemption";
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
	
	//added for pos settings
	
	public String USER_POS_FTP_SETTINGS_DAO_FOR_DML = "userPosFTPSettingsDaoForDML";


}
