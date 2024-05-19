package org.mq.captiway.scheduler.utility;

import java.util.ArrayList;
import java.util.List;

public interface Constants {

	/**
	 * Campaign Report status 'Sent'/'Sending'
	 */
	String CR_DLVR_STATUS_FETCHED = "F";
	String CR_DLVR_STATUS_ACTIVE = "A";
	
	String CR_STATUS_SENT = "Sent";
	String CR_STATUS_SENDING = "Sending";
	
	String CR_TYPE_SENT = "sent";
	String CR_TYPE_OPENS = "opens";
	String CR_TYPE_CLICKS = "clicks";
	String CR_TYPE_UNSUBSCRIBES = "unsubscribes";
	String CR_TYPE_RESUBSCRIBES = "resubscribe";
	String CR_TYPE_BOUNCES = "bounces";
	String CR_TYPE_SPAM = "spams";

	String CS_TYPE_OPENS = "opens";
	String CS_TYPE_CLICKS = "clicks";
	String CS_TYPE_STATUS = "status";

	/**
	 * CampaignSent status 
	 */
	String CAMP_STATUS_DRAFT = "Draft";
	String CAMP_STATUS_RUNNING = "Running";
	String CS_STATUS_SUCCESS = "Success";
	String CS_STATUS_FAILURE = "Failure";
	String CS_STATUS_BOUNCED = "Bounced";
	String CS_STATUS_SPAMMED = "Spammed";
	String CS_STATUS_UNSUBSCRIBED = "Unsubscribed";
	
	String CAMP_EDTYPE_EXTERNAL_EMAIL = "ExternalEmail";
	
	
	/**
	 * Campaign Address Type
	 */
	String CAMP_ADDRESS_TYPE_USER = "User";
	String CAMP_ADDRESS_TYPE_STORE = "Store";
	String CAMP_ADDRESS_TYPE_CONTACT_HOME_STORE = "ContactHomeStore";
	String CAMP_ADDRESS_TYPE_CONTACT_LAST_PURCHASED_STORE = "ContactLastPurchasedStore";
	
	
	/**
	 * EmailQueue email types
	 */
	String EQ_TYPE_SENDNOW = "SendNow";
	String EQ_TYPE_FEEDBACK = "Feedback";
	String EQ_TYPE_TESTMAIL = "TestMail";
	String EQ_TYPE_TEST_OPTIN_MAIL = "TestOptInMail";
	String EQ_TYPE_TEST_DIGITALRCPT = "TestDR";
	String EQ_TYPE_TEST_PARENTAL_MAIL = "TestParentalMail";
	String EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL = "LoyaltyDetails";
	String EQ_TYPE_WELCOME_MAIL = "WelcomeEmail";
	String EQ_TYPE_SUPPORT_ALERT = "SupportAlert";
	String EQ_TYPE_FORGOT_PASSWORD = "ForgotPassword";
	String EQ_TYPE_LOW_SMS_CREDITS = "lowSMSCredits";
	String EQ_TYPE_USER_MAIL_VERIFY   = "UserEmailIdVerify";
	String EQ_TYPE_DIGITALRECIEPT = "DigitalReceipt";
	String EQ_TYPE_NEW_USER_DETAILS = "NewUserDetails";
	String EQ_STATUS_ACTIVE = "Active";
	String EQ_STATUS_PAUSE = "Paused";
	String EQ_STATUS_SENT   = "Sent";
	String EQ_STATUS_ALL   = "All";
	String EQ_STATUS_FAILURE = "Failure";
	String EQ_STATUS_RESUBSCRIPTION = "Resubscription";
	String EQ_TYPE_KEYWORD_ALERT= "KeywordAlert";
	String EQ_TYPE_LOYALTY_GIFT_CARD_ISSUANCE_MAIL = "LoyaltyGiftCardIssuance";
	String EQ_TYPE_LOYALTY_TIER_UPGRADATION_MAIL = "LoyaltyTierUpgradation";
	String EQ_TYPE_LOYALTY_EARNING_BONUS_MAIL = "LoyaltyEarningBonus";
	String EQ_TYPE_LOYALTY_REWARD_EXPIRY_MAIL = "LoyaltyRewardExpiry";
	String EQ_TYPE_LOYALTY_MEMBERSHIP_EXPIRY_MAIL = "LoyaltyMembershipExpiry";
	String EQ_TYPE_LOYALTY_GIFT_CARD_EXPIRY_MAIL = "LoyaltyGiftCardExpiry";
	String EQ_TYPE_LOYALTY_GIFT_AMOUNT_EXPIRY_MAIL = "LoyaltyGiftAmountExpiry";
	String EQ_TYPE_LOYALTY_OC_ALERTS = "LoyaltyOCAlerts";
	
	
	/**
	 * Score Type 
	 */
	String SCORE_PAGE_VISIT="Page Visited";
	String SCORE_DOWNLOAD="Downloaded";
	String SCORE_SOURCE_OF_VISIT="Source of visit";
	String SCORE_EMAIL_OPEN = "Email Opened";
	String SCORE_EMAIL_CLICK = "Email Clicked";
	String SCORE_EMAIL_NOTOPEN = "Email Not Opened";
	String SCORE_EMAIL_UNSUBSCRIBED = "Email Unsubscribed";
	String SCORE_FORM_SUBMIT = "Form Submitted";
	String SCORE_FORM_ABND = "Form Abondoned";
	String SCORE_FORM_F_RATIO="Form Fill Ratio";
	
	/**
	 * GoogleAnalytics Type
	 */
	
	String GA_PAGE_VISIT="visit";
	String GA_DOWNLOADED="downloaded";
	String GA_SOURCEOFVISIT="Source of visit";
	String GA_ACTION_TYPE="new";
	String GA_ACTION_UPDATE_TYPE="update";
	
	
	/**
	 * userScoresetting SourceOf visit Condition Type
	 */
	String SRCVISITCONTAINS = "contains";
	String SRCVISITDNTCONTAINS = "Does not contains";
	String SRCVISITALLTHESEWRDS = "All these words";
	String SRCVISITANYOFTHESEWRDS = "Any of these words";
	
	/**
	 * userScoreSetting pagevisited Condition Type
	 */
	
	String PVISITCONTAINS = "contains";
	String PVISITREGEX = "Reg Exp";
	String PVISITALLVISIT = "All Visit";
	
	
	/**
	 * Headers and QueryString params
	 */
	String QS_SENTID = "sentId";
	String QS_UNSUBID = "unsubId";
	String QS_CID = "cId";
	String QS_USERID = "uid";
	String QS_CRID = "crId";
	String QS_USERAGENT = "user-agent";
	String QS_URL = "url";
	String QS_EMAIL = "email";
	String QS_EMAIL_ID = "emailId";
	
	/**
	 * PropertyUtil keys
	 */
	String PROPS_KEY_SUPPORT_EMAILID = "SupportEmailId";
	String PROPS_KEY_SENDGRID_MULTIMAIL_USER_ID = "SendGridMultiMailUserId";
	String PROPS_KEY_SENDGRID_MULTIMAIL_USER_PWD = "SendGridMultiMailUserPwd";
	String PROPS_KEY_SENDGRID_SINGLEMAIL_USER_ID = "SendGridSingleMailUserId";
	String PROPS_KEY_SENDGRID_SINGLEMAIL_USER_PWD = "SendGridSingleMailUserPwd";
	
	String PROPS_KEY_SENDGRID_THREAD_COUNT = "SendGridThreadCount";
	String PROPS_KEY_APPLICATION_IP = "schedulerIp";
	String PROPS_KEY_SERVERNAME = PropertyUtil.getPropertyValue(Constants.PROPS_KEY_APPLICATION_IP);
	//String PROPS_KEY_SUBSCRIBER_APPLICATION_IP = "schedulerIp";
	
	
	/**
	 * No segmented contacts while sending campaign constants
	 */
	String NO_SEGMENTED_CONTACTS_SUBJECT = "OptCulture Alert: Email sent to 0 contacts (Username: <USERNAME>)";
	String NO_SEGMENTED_CONTACTS_EMAIL_CONTENT = "NoSegmentedContactsEmailContent";			
	
	
	String SUBJECT_USERCAMPAIGNEXPIRATION = "subCampaignExpiration";//TODO
	/**
	 *AutoProgram status constans
	 */
	String AUTO_PROGRAM_STATUS_ACTIVE = "Active";
	String AUTO_PROGRAM_STATUS_DRAFT = "Draft";
	
	/**
	 * AutoProgram email activity report considerations dates
	 */
	String AUTO_PROGRAM_EMAILREP_FROM_CREATED = "CreatedDate";
	String AUTO_PROGRAM_EMAILREP_FROM_MODIFIED = "ModifiedDate";
	
	
	/**
	 * Contact status
	 */
	String CON_STATUS_ACTIVE = "Active"; 
	
	/**
	 * Contacts status
	 */
	String CONT_STATUS_ACTIVE = "Active";
	String CONT_STATUS_OPTIN_PENDING = "Optin pending";
	String CONT_STATUS_PARENTAL_PENDING = "Parental pending";
	String CONT_PARENTAL_STATUS_PENDING_APPROVAL= "Pending Approval";
	String CONT_PARENTAL_STATUS_APPROVED= "Approved";
	String CONT_STATUS_INVALID_EMAIL = "Invalid Email";
	String CONT_STATUS_INVALID_DOMAIN = "Invalid Domain";
	String CONT_STATUS_NOT_A_MAIL_SERVER = "Not a Mail Server";
	String CONT_STATUS_BOUNCED = "Bounced";
	/**
	 * TempConstants
	 */
	
	String _STATUS = "status";
	String _CATEGORIES = "categories";
	
	/**
	 * CampaignReport message responses
	 */
	
	String _UNSUBSCRIBE_REQUEST_MSG = "";
		/*" Please select the categories from which you dont want to receive \n" +
		" any emails from this sender";*/
	
	String _UNSUBSCRIBE_ALREADY_MSG = "";
		/*" You have already unsubscribed from the following categories.\n" +
		" If you want to resubscribe un select the categories and click on submit.";*/
	
	String _UNSUBSCRIBE_RESPONSE_MSG = 
		" Thank you very much for your response.\n" +
	" If you want to resubscribe click on re-subscribe.";
		/*" You have successfully unsubscribed from the following categories.\n" +*/
	
	String _RESUBSCRIBE_RESPONSE_MSG = 
		"Thank you very much for your interest to receive the emails from this user.";
	
	String DELIMETER_COLON = ":";
	String DELIMETER_COMMA = ",";
	char DELIMETER_PIPE = '|';
	String DELIMETER_DOUBLEPIPE ="||";
	String DELIMETER_DOUBLECOLON = "::";
	String DELIMETER_UNDERSCORE = "_";
	String CF_TOKEN = "CF:";
	String CF_START_TAG = "|^";
	String CF_END_TAG = "^|";
	String CF_COL_DELIMETER = ";=;"; 
	String CF_NAME_VALUE_SEPARATOR = "=:=";
	String ADDR_COL_DELIMETER = ";=;"; 
	String UDF_TOKEN = "UDF_";
	/**
	 * Suppress Contacts type
	 */
	
	String SUPP_TYPE_BOUNCED = "bouncedcontact";
	String SUPP_TYPE_USERADDED = "useraddedcontact";
	
	
	String SMS_TYPE_TRANSACTIONAL = "TR";
	String SMS_TYPE_PROMOTIONAL = "PR";
	String SMS_TYPE_2_WAY = "TW";
	String SMS_SENDING_TYPE_OPTIN = "OP";
	String SMS_TYPE_OUTBOUND = "OB";
	
	
	
	String SMS_SENDING_MODE_SMPP = "SMPP";
	String SMS_SENDING_MODE_HTTP = "HTTP";
	
	
	/**
	 * SMS suppressed contacts
	 */
	String SMS_SUPP_TYPE_DND = "NDNC Rejected";
	String SMS_SUPP_TYPE_INVALID_MOBILE = "Invalid Mobile Number";
	String SMS_SUPP_TYPE_INVALID_MOBILE_NUMBER = "Invalid Number";
	String SMS_SUPP_TYPE_UNSUBSCRIBED = "Unsubscribed";
	String SMS_SENT_STATUS_STATUS_PENDING = "Status Pending";
	
	
	
	/**
	 * Pmta Submitter source type Added for EventTrigger
	 */
	
	String SOURCE_CAMPAIGN = "CampaignSchedule";
	String SOURCE_TRIGGER = "EventTrigger";
	String SOURCE_MARKETING_PROGRAM= "MarketingProgram";
	String SOURCE_TESTEMAIL = "TestEmail";
	String SOURCE_DOUBLEOPTIN = "DoubleOptin";
	
	
	
	/**
	 * source type for SMS submitter
	 */
	
	String SOURCE_SMS_CAMPAIGN = "SMSCampaignSchedule";
	
	
	
	
	
	/**
	 * EventTrigger 4 Trigger types
	 * 
	 * For Clicks type, campaign name and urls are separated using pipe delimiter
	 * Ex: TestCampaign|www.google.com|www.ask.com will be entered in eventField of eventTrigger table 
	 */
	
	String ET_TYPE_CONTACT_DATE = "CDATE";
	String ET_TYPE_SPECIFIC_DATE = "SDATE";
	String ET_TYPE_CAMPAIGN_CLICK = "CAMPCLICK";
	String ET_TYPE_CAMPAIGN_OPEN = "CAMPOPEN";
	String DELIMITER_PIPE = "|";
	
	
	int ET_TYPE_ON_PRODUCT = 1;
	int ET_TYPE_ON_PURCHASE = 2;
	int ET_TYPE_ON_PURCHASE_AMOUNT = 4;
	int ET_TYPE_ON_CONTACT_DATE = 8;
	int ET_TYPE_ON_CONTACT_OPTIN_MEDIUM = 16;
	int ET_TYPE_ON_CAMPAIGN_OPENED = 32;
	int ET_TYPE_ON_CAMPAIGN_CLICKED = 64;
	int ET_TYPE_ON_LOYALTY = 128;
	int ET_TYPE_ON_LOYALTY_ISSUANCE = 256;
	int ET_TYPE_ON_LOYALTY_REDEMPTION = 512;
	int ET_TYPE_ON_LOYALTY_ADJUSTMENT = 1024;
	int ET_TYPE_ON_CONTACT_ADDED = 2048;
	int ET_TYPE_ON_GIFT = 4096;
	int ET_TYPE_ON_GIFT_ISSUANCE = 8192;
	int ET_TYPE_ON_GIFT_REDEMPTION = 16384;
	
	
	
	
	/**
	 *  Event Trigger Flags Options (compared with 32 bit options_flag in EVENTRIGGER DB)
	 *  each flag is given 3 bits for future use and
	 *  the value of each flag constant is sum of 3 bits
	 *  if (ET_TRIGGER_IS_ACTIVE_FLAG & options_flag = ET_TRIGGER_IS_ACTIVE_FLAG) => trigger is active
	 *  ET- EventTrigger
	 */
	
	//1-3 bits default=active
	int ET_TRIGGER_IS_ACTIVE_FLAG = 1;  //0x1 
	int ET_TRIGGER_EVENTS_SEND_EMAIL_CAMPAIGN_FLAG = 1;
	int ET_TRIGGER_EVENTS_SEND_SMS_CAMPAIGN_FLAG = 2;
	
	/*
	 * 4-6 bits by default recurs annually
	 */
	int ET_REPEAT_ANNUAL = 8; //0x8

	/*
	 * 7-9 bits
	 */
	int ET_SEND_CAMPAIGN_FLAG = 128; //0x40
	int ET_SEND_SMS_CAMPAIGN_FLAG = 256;  //0x80 adding for sms functionality
	/*
	 * 10-12 bits
	 * if set we will filter by trigger type in campiagn_reports table else by trigger/campaign name
	 */
	int ET_FILTER_BY_TRIGGER_TYPE_FLAG = 1024; //0x0200
	
	/*
	 * 13-15 bits
	 */
	int ET_ADD_CONTACTS_TO_ML_FLAG = 8192; // 0x1000 

	/*
	 * 16-18 bits
	 * either ET_OVERWRITE_IF_CONTACTS_EXISTS or ET_MERGE_IF_CONTACTS_EXISTS
	 *  will be set at one time but not both
	 */
	int ET_OVERWRITE_IF_CONTACTS_EXISTS = 65536; //0x08000
	int ET_MERGE_IF_CONTACTS_EXISTS = 131072; //0x10000
	
	//int ET_MERGE_CUSTOMFIELDS = 262144; //0x20000 
		
	/*
	 * 19-21 bits 
	 */
	int ET_REMOVE_FROM_CURRENT_ML_FLAG = 524288; 	
	
	
	/**
	 * Event trigger Events status for campaign and sms. 
	 * each flag is given 3 bits for future use and.
	 * the value of each flag constant is sum of 3 bits.
	 * 
	 */
	int ET_EMAIL_STATUS_NO_ACTION = 0;
	int ET_EMAIL_STATUS_ACTIVE = 1;
	int ET_EMAIL_STATUS_RUNNING = 4;
	
	int ET_EMAIL_STATUS_SENT = 15;
	int ET_EMAIL_STATUS_PAUSED = 2;
	int ET_EMAIL_STATUS_DELETED = 3;
	
	
	int ET_SMS_STATUS_NO_ACTION = 0;
	int ET_SMS_STATUS_ACTIVE = 1;
	int ET_SMS_STATUS_RUNNING = 4;
	
	int ET_SMS_STATUS_SENT = 15;
	int ET_SMS_STATUS_PAUSED = 2;
	int ET_SMS_STATUS_DELETED = 3;
	
	
	
	/**
	 * for identifying user Configured SMS API
	 */
	String USER_SMSTOOL_CELLNEXT = "CellNext";
	String USER_SMSTOOL_SMSCOUNTRY = "SMSCountry";
	String USER_SMSTOOL_NETCORE = "NetCore";
	String USER_SMSTOOL_CLICKATELL = "ClickaTell";
	String USER_SMSTOOL_MVAYOO = "MVaayoo";
	String USER_SMSTOOL_UNICEL = "Unicel";
	String USER_SMSTOOL_PAKISTAN = "SMS4Connect";
	String USER_SMSTOOL_OUTREACH = "Outreach";
	//Added For InfoBip
	String USER_SMSTOOL_INFOBIP = "Infobip";

	
	String MAILINGLIST_STATUS_ACTIVE = "Active";
	String MAILINGLIST_STATUS_PURGING = "Purging";

	 String CONT_STATUS_PURGE_PENDING = "Validation pending";
	 
	 
	 /**
	  * User account Types
	  */
	 String USER_ACCOUNT_TYPE_PRIMARY="Primary";
	 String USER_ACCOUNT_TYPE_SHARED="Shared";
	 String USER_AND_ORG_SEPARATOR = "__org__"; 
	 
	 /**
	  * MialingList type
	  */
	 
	 String MAILINGLIST_TYPE_ADDED_MANUALLY = "Normal";
	 String MAILINGLIST_TYPE_WEB_FORMS = "webForms";
	 String MAILINGLIST_TYPE_POS = "POS";

	 String MAILINGLIST_TYPE_HOMESPASSED = "BCRM";
	
/**
	  * OPTINTEL MAPPING TYPES
	  */
	 String POS_MAPPING_TYPE_SALES = "Sales";
	 String POS_MAPPING_TYPE_SKU = "SKU";
	 String POS_MAPPING_TYPE_CONTACTS = "Contacts";
	 String POS_MAPPING_TYPE_HOMES_PASSED= "BCRM";

	 String CONTACT_RESUBSCRIBE = "ReSubscribe";
	 
	 
	 /**
	  * Client Type
	  */
	 String CLIENT_TYPE_BCRM = "BCRM";
	 String CLIENT_TYPE_POS = "POS";
	 
	 
	 
	 /**
	  * Contact optin medium types
	  */
	 String CONTACT_OPTIN_MEDIUM_POS = "POS";
	 String CONTACT_OPTIN_MEDIUM_MOBILE = "Mobile Opt-In";
	 String CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY = "AddedManually";
	 String CONTACT_OPTIN_MEDIUM_WEBFORM = "WebForm";
	 String CONTACT_OPTIN_MEDIUM_EXTERNALMAIL = "ExternaEmail";
	 String CONTACT_OPTIN_MEDIUM_ECOMMERCE = "eComm";

	 /**
	  * Campaign Place holders Type
	  */
	 String CAMPAIGN_PH_STORENAME = "storeName";
	 String CAMPAIGN_PH_STOREMANAGER = "storeManager";
	 String CAMPAIGN_PH_STOREEMAIL = "storeEmail";
	 String CAMPAIGN_PH_STOREPHONE = "storePhone";
	 String CAMPAIGN_PH_STORESTREET = "storeStreet";
	 String CAMPAIGN_PH_STORECITY = "storeCity";
	 String CAMPAIGN_PH_STORESTATE = "storeState";
	 String CAMPAIGN_PH_STOREZIP = "storeZip";
	
	 //Old Loyalty Place Holders.
	  String CAMPAIGN_PH_GIFTCARD_BALANCE = "loyaltygiftcardBalance"; 		//CAMPAIGN_PH_LOYALTY_CURRENCY_BALANCE
	  String CAMPAIGN_PH_LOYALTY_CARDNUMBER = "loyaltyCardNumber";  			//CAMPAIGN_PH_LOYALTY_MEMBERSHIP_NUMBER
	  String CAMPAIGN_PH_LOYALTYCARDPIN = "loyaltyCardPin";				//same as SparkBase
	  String CAMPAIGN_PH_LOYALTY_REFRESHEDON ="loyaltyRefreshedOn";			//same as SparkBase

	 //New loyalty place holders
	 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_NUMBER="loyaltyMembershipNumber ";
	 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PIN="loyaltyMembershipPin";
	 String CAMPAIGN_PH_LOYALTY_MEMBER_TIER="loyaltyMemberTier";
	 String CAMPAIGN_PH_LOYALTY_MEMBER_STATUS="loyaltyMemberStatus";
	 String CAMPAIGN_PH_LOYALTY_ENROLLMENT_DATE ="loyaltyEnrollmentDate";
	 String CAMPAIGN_PH_LOYALTY_ENROLLMENT_SOURCE ="loyaltyEnrollmentSource";
	 String CAMPAIGN_PH_LOYALTY_ENROLLMENT_STORE ="loyaltyEnrollmentStore";
	 String CAMPAIGN_PH_LOYALTY_REGISTERED_PHONE ="loyaltyRegisteredPhone";
	 String CAMPAIGN_PH_LOYALTY_POINTS_BALANCE="loyaltyPointsBalance";
	 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_CURRENCY_BALANCE="loyaltyMembershipCurrencyBalance";
	 String CAMPAIGN_PH_LOYALTY_GIFT_BALANCE ="loyaltyGiftBalance"; 
	 String CAMPAIGN_PH_LOYALTY_HOLD_BALANCE  ="loyaltyHoldBalance";
	 String CAMPAIGN_PH_LOYALTY_REWARD_ACTIVATION_PERIOD ="loyaltyRewardActivationPeriod";
	 String CAMPAIGN_PH_LOYALTY_LAST_EARNED_VALUE ="loyaltyLastEarnedValue";
	 String CAMPAIGN_PH_LOYALTY_LAST_REDEEMED_VALUE ="loyaltyLastRedeemedValue";
	 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PASSWORD ="loyaltyMembershipPassword";
	 String CAMPAIGN_PH_LOYALTY_LOGIN_URL ="loyaltyLoginUrl";
	 String CAMPAIGN_PH_ORGANIZATION_NAME ="organizationName";
	 String CAMPAIGN_PH_LOYALTY_REWARD_EXPIRATION_PERIOD ="loyaltyRewardExpirationPeriod";
	 String CAMPAIGN_PH_LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD ="loyaltyGiftAmountExpirationPeriod";
	 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_EXPIRATION_DATE ="loyaltyMembershipExpirationDate";
	 String CAMPAIGN_PH_LOYALTY_GIFT_CARD_EXPIRATION_DATE ="loyaltyGiftCardExpirationDate";
	 String CAMPAIGN_PH_LOYALTY_LAST_BONUS_VALUE ="loyaltyLastBonusValue";
	 String CAMPAIGN_PH_REWARD_EXPIRING_VALUE ="loyaltyRewardExpiringValue";
	 String CAMPAIGN_PH_GIFT_AMOUNT_EXPIRING_VALUE ="loyaltyGiftAmountExpiringValue";

	 String CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS = "lastPurchaseStoreAddress";
	 String CAMPAIGN_PH_LASTPURCHASE_DATE = "lastPurchaseDate";
	 String CAMPAIGN_PH_UNSUBSCRIBE_LINK = "unsubscribeLink";
	 String CAMPAIGN_PH_WEBPAGE_VERSION_LINK = "webPageVersionLink";
	 String DATE_PH_DATE_today = "DATE_today";
	 String DATE_PH_DATE_tomorrow = "DATE_tomorrow";
	 String DATE_PH_DATE_ = "DATE_";
	 String DATE_PH_DAYS = "_days";
	 String SYMBOL_PH_SYM = "SYM_";
	 String COUPON_CC = "CC_";
	 
	 // Add campaign Place holders for Forward to Friend
	 String CAMPAIGN_PH_FARWRADFRIEND_LINK = "forwardToFriendLink";
	 
	 
	 
	 /***
	  * Coupon Type
	  */
	 String COUP_GENT_TYPE_SINGLE = "single";
	 String COUP_GENT_TYPE_MULTIPLE = "multiple";
	 String COUP_GENT_CODE_STATUS_ACTIVE = "Active";
	 String COUP_GENT_CAMPAIGN_TYPE_EMAIL	= "Email";
	 String COUP_GENT_CAMPAIGN_TYPE_SMS = "SMS";
	 String COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL	= "AutoEmail";
	 String COUP_GENT_CAMPAIGN_TYPE_SINGLE_SMS	= "AutoSMS";
	 
	 String COUP_STATUS_ACTIVE = "Active";
	 String COUP_STATUS_RUNNING = "Running";
	 String COUP_STATUS_EXPIRED = "Expired";
	 String COUP_STATUS_PAUSED = "Paused";
	 
	 String COUP_EXPIRY_TYPE_DYNAMIC = "D";
	 String COUP_EXPIRY_TYPE_STATIC = "S";
	 
	 String COUP_DYN_ISSUANCE = "I";
	 String COUP_DYN_BDAY = "B";
	 String COUP_DYN_ANNV = "A";
	 
	 // Coupon Bar Code types
	 List<String> barcodeSquareTypes = new ArrayList<String>(){
		 {
			add("QR");
			add("DM");
			add("AZ");
			 
		 }
	 };
	 List<String> barcodeTypes = new ArrayList<String>(){
		 {
			add("QR");
			add("LN");
			add("DM");
			add("AZ");
			 
		 }
	 };
	 String COUP_BARCODE_QR = "QR";
	 String COUP_BARCODE_AZTEC = "AZ";
	 String COUP_BARCODE_LINEAR = "LN";
	 String COUP_BARCODE_DATAMATRIX = "DM";

	 String COUP_CODE_STATUS_ACTIVE = "Active";
	 String COUP_CODE_STATUS_INVENTORY = "Inventory";

 	String SEGMENT_ON_EXTERNALID = "SEGMENT_ON_EXTERNALID";
	String SEGMENT_ON_EMAIL = "SEGMENT_ON_EMAIL";
	String SEGMENT_ON_MOBILE = "SEGMENT_ON_MOBILE";

	  String TYPE_SALES_TO_CONTACTS="Sales to Contacts";
	  String TYPE_SALES_TO_Inventory="Sales to Inventory";
	  String SMS_WRONG_KEYWORD_RESPONSE = "SMSWrongKeywordResponseText";
	  String SMS_EXPIRED_KEYWORD_RESPONSE = "SMSExpiredKeywordResponseText";
	  String SMS_LOW_CREDITS_WARN_TEXT = "lowSMSCreditsWarnText";
	  
	  String CON_MOBILE_STATUS_ACTIVE = "Active";
	  String CON_MOBILE_STATUS_NOT_A_MOBILE = "Not Available"; 
	  String CON_MOBILE_STATUS_OPTED_OUT = "Opted-out"; 
	  String CON_MOBILE_STATUS_OPTIN_PENDING = "Opt-in Pending"; 
	  
	  String SMS_KEYWORD_SETTINGS_TYPE_OPT_IN = "Opt-in";
	  String SMS_KEYWORD_SETTINGS_TYPE_HELP = "Help";
	  String SMS_KEYWORD_SETTINGS_TYPE_OPT_OUT = "OptOut";
	  
	  String URL_TOKEN_QUESTIONMARK = "?";
	  String URL_TOKEN_AMBERSENT = "&";
	  String URL_TOKEN_EQUALTO = "=";
	  String URL_PARAM_EVENT = "event";
	  String URL_PARAM_SENTID = "sentId";
	  String URL_PARAM_CRID = "crId";
	  String URL_PARAM_USERID = "userId";	
	  String URL_PARAM_SERVERNAME = "ServerName";
	  String URL_PARAM_EMAIL = "email";
	  String URL_PARAM_STATUS = "status";
	  String URL_PARAM_TYPE = "type";
	  String URL_PARAM_REASON = "reason";
	  String URL_PARAM_EMAIL_TYPE = "EmailType";

	  String URL_PARAM_REQUESTED_ACTION= "requestedAction";
	  String URL_PARAM_URL=  "url";
	  String URL_PARAM_UNSUBID= "unsubId";
	  String URL_PARAM_CID=  "cId";
	  
	  
	  String EXTERNAL_SMTP_EVENTS_TYPE_DROPPED = "dropped";
	  String EXTERNAL_SMTP_EVENTS_TYPE_BOUNCE = "bounce";
	  String EXTERNAL_SMTP_EVENTS_TYPE_SPAMREPORT= "spamreport";
	  String EXTERNAL_SMTP_EVENTS_TYPE_BLOCKED = "blocked";
	  String EXTERNAL_SMTP_EVENTS_TYPE_EXPIRED = "expired";
	  
	
	  
	  
	  String SMS_SENDERID = "SMSSenderId";
	  
	  String QRY_COLUMNS_CONTACTS = " c.cid,  c.email_id, c.first_name, " +
				" c.last_name, c.created_date, c.purged, c.email_status," +
				" c.last_status_change, c.last_mail_date, c.address_one, " +
				" c.address_two, c.city, c.state, c.country, c.pin, c.phone, " +
				" c.optin, c.subscription_type, c.optin_status, c.external_id, c.optin_medium, c.gender, c.birth_day," +
				" c.anniversary_day, c.udf1, c.udf2, c.udf3, c.udf4, c.udf5, c.udf6, c.udf7, c.udf8, c.udf9, " +
				" c.udf10 ,c.udf11 ,c.udf12 ,c.udf13 ,c.udf14, c.udf15, c.opted_into, c.optin_per_type, c.home_store,c.loyalty_customer, c.hp_id,   " +
				" c.mobile_status,c.user_id,c.zip,c.mobile_phone,c.home_phone,c.last_sms_date,c.mobile_opt_in, c.mlbits,c.categories,c.last_mail_span,c.last_sms_span "; 
				
	  
	  String QRY_COLUMNS_TEMP_CONTACTS = " cid,  email_id, first_name, " +
				" last_name, created_date, purged, email_status," +
				" last_status_change, last_mail_date, address_one, " +
				" address_two, city, state, country, pin, phone, " +
				" optin, subscription_type, optin_status, external_id, optin_medium, gender, birth_day," +
				" anniversary_day, udf1, udf2, udf3, udf4, udf5, udf6, udf7, udf8, udf9, " +
				" udf10 , udf11 , udf12 , udf13 , udf14, udf15, opted_into, optin_per_type, home_store, loyalty_customer, hp_id,   " +
				" mobile_status, user_id, zip, mobile_phone, home_phone, last_sms_date, mobile_opt_in, mlbits,categories,last_mail_span, last_sms_span  ";
	  
	  
	  String SENDGRID_HEADER_UNIQUE_ARGS = "\"unique_args\": {\"Email\": \""+ "<SOURCE>" +"\", \"crId\" : \""+  "<CRID>" +"\"," +
	  "  \"userId\": \""+ "<USERID>" +"\", \"ServerName\": \""+ "<SERVERNAME>" +"\" }," ;
	  
	  String SENDGRID_HEADER_TO_SUBSTITUES = " \"to\": [ <TOEMAILS> ] ,  \"sub\": { <PLACEHOLDERS> }   " ;
	  
	  
	  String SENDGRID_HEADER_JSON = "{ " +SENDGRID_HEADER_UNIQUE_ARGS+ SENDGRID_HEADER_TO_SUBSTITUES+"}";
	  String DOUBLE_QUOTE = "\"";
	  String COMMA =",";
	  String SUBSTITUTETAG = "<SUBTAG_";
	  String GT = ">";
	  String COLON = ":";
	  String RIGHT_SQUARE_BRACKET = "]";
	  String LEFT_SQUARE_BRACKET = "[";
	  
	  String PLACEHOLDER_CRID = "[crId]";
	  String PLACEHOLDER_USERID = "[userId]";
	  String PLACEHOLDER_SENDEREMAIL = "[senderEmail]";
	  String PLACEHOLDER_SENTID = "[sentId]";
	  
	  String STRING_NILL = "";
	  String STRING_WHITESPACE = " ";
	  
	  String STRING_NOTAVAILABLE = "Not Available";
	   
	  String SCHEDULER_LOGGER = "scheduler";
	  String FILE_PROCESS_LOGGER = "fileProcess";
	  
	  String INTERACTION_CAMPAIGN_CRID_PH = "<CAMPAIGNCRIDS>";
		String INTERACTION_CAMPAIGN_IDS_PH = "<CAMPAIGNIDS>";
      String APP_SHORTNER_URL = "ApplicationShortUrl";
      
      
      String SERVER_TIMEZONE_VALUE = "ServerTimeZoneValue";
      
      Long CAMP_CATEGORY_TRANSACTIONAL_ID= 60000000l;
      
      String SMTP_RECIEPIENT_EMAIL_ID = "development@optculture.com";
      
      String CATEGORY_NO_CATEGORY = "NoCategory";
	  
      
      //added for Digital Receipts transaction data processing
	  String USERS_ACC_TYPE_PRIMARY="Primary";
	  String DR_JSON_PROCESS_STATUS_NEW="New";
	  String DR_JSON_PROCESS_STATUS_PROCESSED="Processed";
	  String DR_JSON_PROCESS_STATUS_UNPROCESSED="Unprocessed";
	  
	  String COUP_PROVIDER_FOR_SUBSCRIBER_URL = "CouponProviderUrl";
	  String KEYWORD_STATUS_EXPIRED =  "Expired";
	  String KEYWORD_STATUS_PENDING = "Pending";
	  String KEYWORD_STATUS_ACTIVE = "Active";
	  /**
		  *  Sparkbase Card Status
		  */
		 String SPARKBASE_CARD_STATUS_INVENTORY = "inventory";
		 String SPARKBASE_CARD_STATUS_ACTIVATED = "activated";
		 String SPARKBASE_CARD_STATUS_CLOSED = "closed";
		 String SPARKBASE_CARD_STATUS_EXPIRED = "expired";
		 String SPARKBASE_CARD_STATUS_LOST = "lost";
		 String SPARKBASE_CARD_STATUS_SUSPENDED = "suspended";
		 String SPARKBASE_CARD_TYPE_PHYSICAL = "physical";
		 String SPARKBASE_CARD_TYPE_VIRTUAL = "virtual";
		 String SPARKBASE_CARD_FROMSOURCE_POS= "pos";
		 String SPARKBASE_CARD_FROMSOURCE_UPLOAD = "upload";
		 
		 /**
		  *  Sparkbase Location Type
		  */
		 String SB_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE = "percentage";
		 String SB_CARDS_AVAILABLE_COUNT_TYPE_COUNT = "count";
		 
		 String SB_TRANSACTION_INQUIRY = "Inquiry";
		 String SB_TRANSACTION_ENROLLMENT = "Enrollment";
		 String SB_TRANSACTION_ACCOUNT_HISTORY = "Account History";
		 String SB_TRANSACTION_LOYALTY_ISSUANCE = "Loyalty Issuance";
		 String SB_TRANSACTION_LOYALTY_REDEMPTION = "Loyalty Redemption";
		 String SB_TRANSACTION_GIFT_ISSUANCE = "Gift Issuance";
		 String SB_TRANSACTION_GIFT_REDEMPTION = "Gift Redemption";
		 String SB_TRANSACTION_ADJUSTMENT = "Adjustment";
		 
		 String SPARKBASE_TRANSACTION_STATUS_NEW="New";
		 String SPARKBASE_TRANSACTION_STATUS_PROCESSED="Processed";
		 String SPARKBASE_TRANSACTION_STATUS_UNPROCESSED="Unprocessed";
		 String EQ_TYPE_LOYALTY_EMAIL_ALERTS ="LoyaltyEmailAlerts";
		 String EQ_TYPE_LOYALTY_TRANSFER_MEMBERSHIP_MAIL = "LoyaltyTransferMembership";
		 String SPARKBASE_TRANSACTION_STATUS_SUSPEND = "Suspend";
		 
		 /**
		  * contact loyalty type
		  */

		 String CONTACT_LOYALTY_TYPE_WEBFORM = "WebForm";
		 String CONTACT_LOYALTY_TYPE_POS = "POS";
		 String CONTACT_LOYALTY_TYPE_RESTAPI = "RESTAPI";
		 String CONTACT_LOYALTY_TYPE_STORE = "Store";
		 String CONTACT_LOYALTY_TYPE_OFFLINE = "Offline";

		 
		String SPARKBASE_CARD_COMMENTS_LOCATION_INVALID = "locinvalid";
		String SPARKBASE_CARD_COMMENTS_INVENTORY = "inventory";
		String SPARKBASE_CARD_COMMENTS_SBERROR = "sberror";
		String SPARKBASE_CARD_COMMENTS_SBLOYALTY_ADDED = "sbloyalty";
		
		
		String SMS_DLR_STATUSCODE_TOKEN="err";
		String SMS_DLR_MSGID_TOKEN="id";
		String SMS_DLR_SEQ_NUMBER_TOKEN="seq";
		
		String SMS_COUNTRY_INDIA = "India";
		String SMS_COUNTRY_PAKISTAN = "Pakistan";
		String SMS_COUNTRY_US = "US";
		
		String SMS_COUNTRY_CANADA = "Canada";
		String SMS_COUNTRY_KUWAIT = "Kuwait";
		
		//Added for UAE
		String SMS_COUNTRY_UAE="UAE";
		String SMS_ACCOUNT_TYPE_OPTIN = "OP";
		String SMS_ACCOUNT_TYPE_TRANSACTIONAL = "TR";
		String SMS_ACCOUNT_TYPE_PROMOTIONAL = "PR";
		
		String KEYWORD_EXP_MAIL_TYPE = "KeywordAlert";
		String KEYWORD_EXP_ALERT_SUB = "keywordExpAlertSubject";
		String KEYWORD_EXP_ALERT_MAIL_CONTENT = "keywordExpAlertMsgContent";
		
		String KEYWORD_EXP_ALERT_SUB_PH = "[keywords]";
		String KEYWORD_EXP_ALERT_MAIL_CONTENT_PH = "[keywordExpDate]";
		
		String USER_FIRST_NAME_PH = "[fname]";
		String USER_LAST_NAME_PH = "[lname]";
		
		String MAIL_SENT_STATUS_ACTIVE = "Active";

		String UNICEL_DEF_USERID = "magpromo";
		String UNICEL_DEF_PWD = "UnicelPromoPwd";
		String UNICEL_DEF_SENDERID = "Alerts";
		
		String CLICKATELL_DEF_USERID = "optculture10";
		String CLICKATELL_DEF_PWD = "ClickaTellPwd";
		String CLICKATELL_DEF_SENDERID = "888555";
		String CLICKATELL_DEF_APIID = "3389885";
		
		String INFOBIP_DEF_USERID = "risservices";
		String INFOBIP_DEF_PWD = "InfoBipPromoPwd";
		String INFOBIP_DEF_SENDERID = "ZAKS";
		//String INFOBIP_DEF_APIID = null;
		
		String SMS_TYPE_CAMPAIGN = "C";
		String SMS_TYPE_AUTORESPONSE = "A";
		
		String  EQ_TYPE_OPTIN_REQUEST_TYPE="Optin Change Content Request";
		
		// for handling the campaigns already sent
		String OLD_TRACK_URL = "oldTrackUrl";
		String NEW_TRACK_URL = "newTrackUrl";
		
		
		String EQ_TYPE_OPT_SYN_PROMO_REDEMTION = "OptSynch Promoredemtion";
		String EQ_TYPE_OPT_SYN_LOYALTY_ISSUANCE = "OptSync LoyaltyIssuance";
		String EQ_TYPE_OPT_SYN_LOYALTY_ENROLL = "OptSync LoyaltyEnrolment";
		String EQ_TYPE_OPT_SYN_DIGI_RECEIPT = "OptSync DigitalReceipt";
		String EQ_TYPE_FILE_EXPORT = "File Export";
		
		// added for auto alert mail send to support, when campaign scheduled on segments sent to '0' contacts
		String ALERT_MESSAGE_PH_USERNAME = "<USERNAME>";
		String ALERT_MESSAGE_PH_EMAILCAMPAIGNNAME =  "<EMAILCAMPAIGNNAME>";
		String ALERT_MESSAGE_PH_SEGMENTNAMES = "<SEGMENTNAMES>";
		String ALERT_MESSAGE_PH_ORGNAME = "<ORGNAME>";
		String ALERT_MESSAGE_PH_SCHEDULEDON= "<SCHEDULEDON>";
		String ALERT_FROM_EMAILID = "AlertFromEmailId";
		String ALERT_TO_EMAILID = "AlertToEmailId";
		String CONT_STATUS_REPORT_AS_SPAM="Reported as Spam";
		String CONT_STATUS_UNSUBSCRIBED = "Unsubscribed";
		
		String EQ_TYPE_SALES_DATA_NOT_RECEIVED_ALERT = "Sales Data Not Receved Alert";
		String SALES_DATA_NOT_RECEIVED_ALERT_PH_NUMBER_OF_DAYS  = "<number_of_days> ";
		String SALES_DATA_NOT_RECEIVED_ALERT_PH_ORG_ID  = "<org_id>";
		String SALES_DATA_NOT_RECEIVED_ALERT_PH_USER_NAME  = "<username>";
		
		String WEEKLY_REPORT_SUBJECT_TOFROM_DATES = "<fromtodate>";
		String WEEKLY_REPORT_USER_FIRST_NAME = "<user_first_name>";
		String WEEKLY_SUCESS_CAMPAIGNS = "<sent_files_data>";
		String WEEKLY_FAILED_CAMPAIGNS = "<failed_files_data>";
		String EQ_TYPE_WEEKLY_CAMP_REPORT = "Weekly Campaign Report";
		String WEEKLY_REPORT_SNAPSHOT_TABLES = "<campaign_snapshot_tables>";
		
		String LOYALTY_DAILY_WEEKLY_REPORT = "Loyalty Report";
		
		String SMS_TYPE_OF_ACK_TRANSACTIONID = "TID";
		String SMS_TYPE_OF_ACK_MSGID = "MID";
		
		//VMTA Names
		String SMTP_SENDGRIDAPI="SendGridAPI";
		 
		String SMTP_HOST_NAME = "SMTPHost";
		String SMTP_HOST_PORT = "SMTPPort";
		
		String SMTP_KEY_PROTOCOL = "mail.transport.protocol";
		String SMTP_KEY_HOST = "mail.smtp.host";
		String SMTP_KEY_PORT = "mail.smtp.port";
		String SMTP_KEY_AUTH = "mail.smtp.auth";
		
		String SMTP_VALUE_PROTOCOL = "smtp";
		String SMTP_VALUE_HOST = PropertyUtil.getPropertyValueFromDB(SMTP_HOST_NAME);
		String SMTP_VALUE_PORT = PropertyUtil.getPropertyValueFromDB(SMTP_HOST_PORT);
		String SMTP_VALUE_AUTH = "true";
		/**
		 * Added for sms queue
		 */
		String CAMP_STATUS_ACTIVE = "Active";
		
		
		String AUTO_EMAIL_CLICK_TRACK_URL = "|^clickUrl^|";
		String AUTO_EMAIL_OPEN_TRACK_URL = "|^AutoEmailOpenTrackUrl^|";
		
		 //SMS Queue 2.4.5
		 String SMS_MSG_TYPE_TEST = "Test SMS";
		 String SMS_MSG_TYPE_OPTIN = "OPTIN SMS";
		 String SMS_MSG_TYPE_EXPORT = "Export SMS";
		 String SMS_MSG_TYPE_AUTOSMS = "Auto SMS";
		 String SMS_MSG_TYPE_ALERTSMS = "Alert SMS";
		 String SMS_MSG_TYPE_OTPSMS = "OTP SMS";
		String SMS_MSG_TYPE_AUTORESPONSE = "Auto Response SMS";
		String PURGE_STATUS_COMPLETED = "Complete";
		String PURGE_STATUS_INCOMPLETED = "InComplete";
}
