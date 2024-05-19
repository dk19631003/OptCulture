package org.mq.marketer.campaign.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Constants {

	String UA_TYPE = "UA_TYPE";
	String UA_OSFAMILY = "UA_OSFAMILY";
	String UA_UAFAMILY = "UA_UAFAMILY";
		
	
	String SEGMENT_ON_EXTERNALID = "SEGMENT_ON_EXTERNALID";
	String SEGMENT_ON_EMAIL = "SEGMENT_ON_EMAIL";
	String SEGMENT_ON_MOBILE = "SEGMENT_ON_MOBILE";
	String SEGMENT_ON_NOTIFICATION = "SEGMENT_ON_NOTIFICATION";
	
	String USERIDS_SET = "USERIDS_SET";
	String ROLE_USER = "ROLE_USER";
	String ROLE_ADMIN = "ROLE_ADMIN";
	String ROLE_USER_BASIC = "ROLE_USER_BASIC";
	String ROLE_USER_POWER = "ROLE_USER_POWER";
	String ROLE_USER_SUPER = "ROLE_USER_ADMIN";
	String ROLE_USER_BILLING_ADMIN = "Billing Admin";
	String ROLE_USER_OC_LOYALTY="Loyalty_User";
	String ROLE_USER_SB_LOYALTY="SB_User";
	
	
	
	long MEGABYTE = 1024l * 1024l;
	long KILOBYTE = 1024l;
	
	/**
	 * Campaign status
	 */
	String CAMP_STATUS_DRAFT = "Draft";
	String CAMP_STATUS_ACTIVE = "Active";
	String CAMP_STATUS_RUNNING = "Running";
	String CAMP_STATUS_SUSPEND = "Suspend";
	String CAMP_STATUS_SENT = "Sent";
	String CAMP_STATUS_INSUFFICIENT_FUND = "paused";
	
	/**
	 * Campaign Address Type
	 */
	String CAMP_ADDRESS_TYPE_USER = "User";
	String CAMP_ADDRESS_TYPE_STORE = "Store";
	String CAMP_ADDRESS_TYPE_CONTACT_HOME_STORE = "ContactHomeStore";
	String CAMP_ADDRESS_TYPE_CONTACT_LAST_PURCHASED_STORE = "ContactLastPurchasedStore";
	
	/*
	 * campaign alignment
	 */
	String ALIGNMENT_CENTER ="center";
	String ALIGNMENT_LEFT ="left";
	String ALIGNMENT_RIGHT = "right";
	
	/**
	 * contact loyalty type
	 */
	
	String CONTACT_LOYALTY_TYPE_WEBFORM = "WebForm";
	String CONTACT_LOYALTY_TYPE_POS = "POS";
	String CONTACT_LOYALTY_TYPE_RESTAPI = "RESTAPI";
	String CONTACT_LOYALTY_TYPE_STORE = "Store";
	String CONTACT_LOYALTY_TYPE_OFFLINE = "Offline";
	
	/**
	 * Date and symbol placeholders constants
	 */
	 String DATE_PH_DATE_today = "DATE_today";
	 String DATE_PH_DATE_tomorrow = "DATE_tomorrow";
	 String DATE_PH_DATE_ = "DATE_";
	 String DATE_PH_DAYS = "_days";
	 String SYMBOL_PH_SYM = "SYM_";
	
	/**
	 * Campaign Report status 'Sent'/'Sending'
	 */
	String CR_STATUS_SENT = "Sent";
	String CR_STATUS_SENDING = "Sending";
	
	String CR_TYPE_SENT = "sent";
	String CR_TYPE_OPENS = "opens";
	String CR_TYPE_CLICKS = "clicks";
	String CR_TYPE_UNSUBSCRIBES = "unsubscribes";
	String CR_TYPE_BOUNCES = "bounces";
	String CR_TYPE_SPAM = "spams";

	String CS_TYPE_OPENS = "opens";
	String CS_TYPE_CLICKS = "clicks";
	String CS_TYPE_STATUS = "status";

	/**
	 * CampaignSent status 
	 */
	String CS_STATUS_SUBMITTED ="Submitted";
	String CS_STATUS_SUCCESS = "Success";
	String CS_STATUS_FAILURE = "Failure";
	String CS_STATUS_BOUNCED = "Bounced";
	String CS_STATUS_SPAMMED = "Spammed";
	String CS_STATUS_UNSUBSCRIBED = "Unsubscribed";
	String _CAMPAIGNSENT = "CampaignSent";
	
	String CAMP_EDTYPE_EXTERNAL_EMAIL = "ExternalEmail";
	
	/**
	 * EmailQueue email types
	 */
	String EQ_TYPE_SENDNOW = "SendNow";
	String EQ_TYPE_FEEDBACK = "Feedback";
	String EQ_TYPE_TESTMAIL = "TestMail";
	String EQ_TYPE_TEST_DIGITALRCPT = "TestDR";
	String EQ_TYPE_TEST_OPTIN_MAIL = "TestOptInMail";
	String EQ_TYPE_TEST_PARENTAL_MAIL = "TestParentalMail";
	String EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL = "LoyaltyDetails";
	String EQ_TYPE_WELCOME_MAIL = "WelcomeEmail";
	String EQ_TYPE_FORGOT_PASSWORD = "ForgotPassword";
	String EQ_TYPE_USER_MAIL_VERIFY   = "UserEmailIdVerify";
	String EQ_STATUS_ACTIVE = "Active";
	String EQ_STATUS_SENT   = "Sent";
	String EQ_STATUS_FAILURE = "Failure";
	String EQ_TYPE_LOYALTY_EMAIL_ALERTS ="LoyaltyEmailAlerts";
	String EQ_TYPE_NEW_USER_DETAILS = "NewUserDetails";
	String EQ_TYPE_FEEDBACK_MAIL = "FeedBackEmail";
	String EQ_TYPE_SPECIAL_REWARDS = "specialRewards";
	String EQ_TYPE_OTP_MAIL="OTPMail";
	String EQ_TYPE_OTP_MAILS="OTPMESSAGE";
	
	String EQ_TYPE_LOYALTY_ADJUSTMENT = "loyaltyAdjustment";
	String EQ_TYPE_LOYALTY_ISSUANCE = "loyaltyIssuance";
	String EQ_TYPE_LOYALTY_REDEMPTION = "loyaltyRedemption";
	
	//ocloyalty
	String EQ_TYPE_LOYALTY_GIFT_CARD_ISSUANCE_MAIL = "LoyaltyGiftCardIssuance";
	String EQ_TYPE_LOYALTY_TIER_UPGRADATION_MAIL = "LoyaltyTierUpgradation";
	String EQ_TYPE_LOYALTY_EARNING_BONUS_MAIL = "LoyaltyEarningBonus";
	String EQ_TYPE_LOYALTY_REWARD_EXPIRY_MAIL = "LoyaltyRewardExpiry";
	String EQ_TYPE_LOYALTY_MEMBERSHIP_EXPIRY_MAIL = "LoyaltyMembershipExpiry";
	String EQ_TYPE_LOYALTY_GIFT_CARD_EXPIRY_MAIL = "LoyaltyGiftCardExpiry";
	String EQ_TYPE_LOYALTY_GIFT_AMOUNT_EXPIRY_MAIL = "LoyaltyGiftAmountExpiry";
	String EQ_TYPE_LOYALTY_TRANSFER_MEMBERSHIP_MAIL = "LoyaltyTransferMembership";
	String EQ_TYPE_LOYALTY_OC_ALERTS = "LoyaltyOCAlerts";
	String EQ_TYPE_ISSUE_COUPON = "IssueCoupon";
	
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
	 *AutoProgram status constans
	 */
	String AUTO_PROGRAM_STATUS_ACTIVE = "Active";
	String AUTO_PROGRAM_STATUS_DRAFT = "Draft";
	String AUTO_PROGRAM_SWITCHALLOC_CRITERIA_EACHRUN = "EachRun";
	String AUTO_PROGRAM_SWITCHALLOC_CRITERIA_OVERALL = "OverAll";
	
	
	/**
	 * AutoProgram email activity report considerations dates
	 */
	String AUTO_PROGRAM_EMAILREP_FROM_CREATED = "CreatedDate";
	String AUTO_PROGRAM_EMAILREP_FROM_MODIFIED = "ModifiedDate";
	
	
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
	String QS_FULL_NAME="fullName";
	
	/**
	 * PropertyUtil keys
	 */
	String PROPS_KEY_SUPPORT_EMAILID = "SupportEmailId";
	
	
	/**
	 * Contact status
	 */
	String CON_STATUS_ACTIVE = "Active"; 
	
	/**
	 * Contacts status
	 */
	String CONT_STATUS_ACTIVE = "Active";
	String CONT_STATUS_UNSUBSCRIBED = "Unsubscribed";
	String CONT_STATUS_OPTIN_PENDING = "Optin pending";
	String CONT_STATUS_PARENTAL_PENDING = "Parental pending";
	String CONT_STATUS_PURGE_PENDING = "Validation pending";
	String CONT_STATUS_INVALID_EMAIL = "Invalid Email";
	String CONT_STATUS_INVALID_DOMAIN = "Invalid Domain";
	String CONT_STATUS_NOT_A_MAIL_SERVER = "Not a Mail Server";
	String CONT_PARENTAL_STATUS_PENDING_APPROVAL= "Pending Approval";
	String CONT_PARENTAL_STATUS_APPROVED= "Approved";
	String CONT_STATUS_INACTIVE = "Inactive";
	String CONT_STATUS_BOUNCED = "Bounced";
	String CONT_STATUS_SUPPRESSED = "Suppressed";
	
	char DELIMETER_COLON = ':';
	String DELIMETER_DOUBLECOLON = "::";
	String DELIMETER_SLASH = "/";
	String DELIMETER_SPACE = " ";
	String CF_TOKEN = "CF_";
	String CC_TOKEN = "CC_";
	String UDF_TOKEN = "UDF_";
	String GEN_TOKEN = "GEN_";
	String DEFUALT_TOKEN = "DEFAULT=";
	String REF_TAG="REF_";
	String VALUECODE_TOKEN = "VC_";
	
	String CF_START_TAG = "|^";
	String CF_END_TAG = "^|";
	String ADDR_COL_DELIMETER = ";=;"; 
	String DELIMETER_COMMA = ",";
	String DELIMETER_SEMICOLON = ";";
	String DELIMETER_HIPHEN = "-";
	String FORM_MAPPING_DELIMETER = "_:_";
	
	
	 String CAMPAIGN_PH_UNSUBSCRIBE_LINK = "[GEN_unsubscribeLink]";
	 String CAMPAIGN_PH_WEBPAGE_VERSION_LINK = "[GEN_webPageVersionLink]";
	String CAMPAIGN_PH_SHARE_TWEET_LINK = "[GEN_shareOnTwitter]";
	String CAMPAIGN_PH_SHARE_FACEBOOK_LINK = "[GEN_shareOnFacebook]";
	String CAMPAIGN_PH_FORWRADFRIEND_LINK = "[GEN_forwardToFriend]";
		
	// add subscription preference center
	String CAMPAIGN_PH__UPDATE_PREFERENCE_LINK = "[GEN_updatePreferenceLink]";
	
	String DEFAULT_JSON = "Default_JSON";
	String DEFAULT_JSON_VALUE2= "{\"page\":{\"template\":{\"name\":\"template-base\",\"type\":\"basic\",\"version\":\"0.0.1\"},\"body\":{\"content\":{\"style\":{\"color\":\"#000000\",\"font-family\":\"Arial, 'Helvetica Neue', Helvetica, sans-serif\"},\"computedStyle\":{\"messageBackgroundColor\":\"transparent\",\"linkColor\":\"#0068A5\",\"messageWidth\":\"500px\"}},\"webFonts\":[],\"container\":{\"style\":{\"background-color\":\"#FFFFFF\"}},\"type\":\"mailup-bee-page-proprerties\"},\"title\":\"Empty Bee Template\",\"description\":\"Template for BEE - Empty\",\"rows\":[{\"content\":{\"style\":{\"color\":\"#000000\",\"width\":\"500px\",\"background-color\":\"transparent\"}},\"container\":{\"style\":{\"background-color\":\"transparent\"}},\"columns\":[{\"style\":{\"padding-right\":\"0px\",\"padding-left\":\"0px\",\"border-right\":\"0px dotted transparent\",\"padding-top\":\"5px\",\"background-color\":\"transparent\",\"border-bottom\":\"0px dotted transparent\",\"border-top\":\"0px dotted transparent\",\"padding-bottom\":\"5px\",\"border-left\":\"0px dotted transparent\"},\"grid-columns\":12,\"modules\":[{\"descriptor\":{},\"type\":\"mailup-bee-newsletter-modules-empty\"}]}],\"type\":\"one-column-empty\"}]}}";
	String DEFAULT_JSON_VALUE="{\"page\":{\"template\":{\"name\":\"template-base\",\"type\":\"basic\",\"version\":\"0.0.1\"},\"body\":{\"content\":{\"style\":{\"color\":\"#000000\",\"font-family\":\"Arial, 'Helvetica Neue', Helvetica, sans-serif\"},\"computedStyle\":{\"messageBackgroundColor\":\"transparent\",\"linkColor\":\"#0068A5\",\"messageWidth\":\"500px\"}},\"container\":{\"style\":{\"background-color\":\"#FFFFFF\"}},\"type\":\"mailup-bee-page-proprerties\",\"webFonts\":[]},\"title\":\"Empty Bee Template\",\"description\":\"Template for BEE - Empty\",\"rows\":[{\"content\":{\"style\":{\"color\":\"#000000\",\"width\":\"500px\",\"background-color\":\"transparent\"}},\"container\":{\"style\":{\"background-color\":\"transparent\"}},\"columns\":[{\"style\":{\"border-right\":\"0px dotted transparent\",\"padding-left\":\"0px\",\"padding-right\":\"0px\",\"padding-top\":\"5px\",\"border-bottom\":\"0px dotted transparent\",\"background-color\":\"transparent\",\"border-top\":\"0px dotted transparent\",\"padding-bottom\":\"5px\",\"border-left\":\"0px dotted transparent\"},\"grid-columns\":12,\"modules\":[{\"descriptor\":{},\"type\":\"mailup-bee-newsletter-modules-empty\"}]}],\"type\":\"one-column-empty\"}]}}";
	//This is bcz default beejson is not constant elements shuffls 
	String DEFAULT_JSON_CHECK_TEXTBOX= "mailup-bee-newsletter-modules-text";
	String DEFAULT_JSON_CHECK_IMAGE= "mailup-bee-newsletter-modules-image";
	String DEFAULT_JSON_CHECK_VIDEO= "mailup-bee-newsletter-modules-video";
	String DEFAULT_JSON_CHECK_BUTTON= "mailup-bee-newsletter-modules-button";
	String DEFAULT_JSON_CHECK_DIVIDER= "mailup-bee-newsletter-modules-divider";
	String DEFAULT_JSON_CHECK_MERGE_CONTENT= "mailup-bee-newsletter-modules-merge-content";
	String DEFAULT_JSON_CHECK_HTML= "mailup-bee-newsletter-modules-html";
	String DEFAULT_JSON_CHECK_SOCIAL= "mailup-bee-newsletter-modules-social";
	
	//Please Don't modify the existing constants value as they are needed in couponHistory, You can add new though! :) 
	List<String> OTP_TAGS = new ArrayList<String>() {
		{
			add("Insert Merge Tags::select");

			add("OTPCode::[OTP]");
			add("Organization Name::[Org Name]");
		
		}

	};

	
	List<String> ERECEIPT_TAGS = new ArrayList<String>() {
		{
			add("Insert Merge Tags::select");

			add("CustomerName::[Customer Name]");
			add("Store Name::[Store Name]");
		//	add("Receipt Amount::[Receipt Amount]");
		//	add("Link ::[Link]");
			
			add("LoyaltyMembershipCurrencyBalance::[loyaltyMembershipCurrencyBalance]");
			add("LoyaltyPointsBalance::[loyaltypointsBalance]");

			
			
		//	add("Loyalty points Earned::[LoyaltyEarnedToday]");
		//	add("Lifetime Points::[LifeTimePoints]");

	
		}

	};

	List<String> PLACEHOLDERS_LIST = new ArrayList<String>() {
		{
			add("-- Place Holder --::select");
			add("Email::|^GEN_email / DEFAULT=^|");
			add("First Name::|^GEN_firstName / DEFAULT=^|");
			add("Last Name::|^GEN_lastName / DEFAULT=^|");
			add("Street::|^GEN_addressOne / DEFAULT=^|");
			add("Address Line Two::|^GEN_addressTwo / DEFAULT=^|");
			add("City::|^GEN_city / DEFAULT=^|");
			add("State::|^GEN_state / DEFAULT=^|");
			add("Country::|^GEN_country / DEFAULT=^|");
			add("Postal Code::|^GEN_pin / DEFAULT=^|");
			add("Mobile::|^GEN_phone / DEFAULT=^|");
			add("Gender::|^GEN_gender / DEFAULT=^|");
			add("Birthday::|^GEN_birthday / DEFAULT=^|");
			add("Anniversary::|^GEN_anniversary / DEFAULT=^|");
			add("Contact Creation Date::|^GEN_createdDate / DEFAULT=^|");
			add("Organization Name::|^GEN_organizationName / DEFAULT=Not Available^|");
			add("Company Address::|^GEN_companyAddress^|");
			add("Contact's Homestore Address::|^GEN_contactHomestoreAddress^|");
			add("Contact's Homestore Name::|^GEN_storeName / DEFAULT=Not Available^|");
			add("Contact's Homestore Manager::|^GEN_storeManager / DEFAULT=^|");
			add("Contact's Homestore Phone::|^GEN_storePhone / DEFAULT=^|");
			add("Contact's Homestore Email::|^GEN_storeEmail / DEFAULT=^|");
			add("Contact's Homestore Street::|^GEN_storeStreet / DEFAULT=^|");
			add("Contact's Homestore City::|^GEN_storeCity / DEFAULT=^|");
			add("Contact's Homestore State::|^GEN_storeState / DEFAULT=^|");
			add("Contact's Homestore Zip::|^GEN_storeZip / DEFAULT=^|");
			add("Last Purchase Store Address::|^GEN_ContactLastPurchasedStore^|");
			add("Last Purchase Date::|^GEN_lastPurchaseDate / DEFAULT=Not Available^|");
			add("Last Purchase Amount::|^GEN_lastPurchaseAmount / DEFAULT=Not Available^|");
			add("Last Purchase Store Name::|^GEN_lastPurchaseStoreName / DEFAULT=Not Available^|");
			add("Last Purchase Store Manager::|^GEN_lastPurchaseStoreManager / DEFAULT=Not Available^|");
			add("Last Purchase Store Phone::|^GEN_lastPurchaseStorePhone / DEFAULT=Not Available^|");
			add("Last Purchase Store Email::|^GEN_lastPurchaseStoreEmail / DEFAULT=Not Available^|");
			add("Last Purchase Store Street::|^GEN_lastPurchaseStoreStreet / DEFAULT=Not Available^|");
			add("Last Purchase Store City::|^GEN_lastPurchaseStoreCity / DEFAULT=Not Available^|");
			add("Last Purchase Store State::|^GEN_lastPurchaseStoreState / DEFAULT=Not Available^|");
			add("Last Purchase Store Zip::|^GEN_lastPurchaseStoreZip  / DEFAULT=Not Available^|");
			//add("Loyalty Points Balance::|^GEN_loyaltyPointsBalance / DEFAULT=Not Available^|");
			//add("Gift-Card Balance::|^GEN_loyaltygiftcardBalance / DEFAULT=Not Available^|");
			//add("Loyatly Card Number::|^GEN_loyaltyCardNumber / DEFAULT=Not Available^|");
			//add("Loyalty Card Pin::|^GEN_loyaltyCardPin / DEFAULT=Not Available^|");
			//add("Loyalty Refreshed On::|^GEN_loyaltyRefreshedOn / DEFAULT=Not Available^|");
			add("Loyalty Membership Number::|^GEN_loyaltyMembershipNumber / DEFAULT=Not Available^|");
			add("Loyalty Membership Pin::|^GEN_loyaltyMembershipPin / DEFAULT=Not Available^|");
			//add("Loyalty Member Tier::|^GEN_loyaltyMemberTier / DEFAULT=Not Available^|");
	        //add("Loyalty Member Status::|^GEN_loyaltyMemberStatus / DEFAULT=Not Available^|");
	        add("Loyalty Enrollment Date::|^GEN_loyaltyEnrollmentDate/ DEFAULT=Not Available^|");
	        add("Loyalty Enrollment Source::|^GEN_loyaltyEnrollmentSource/ DEFAULT=Not Available^|");
	        add("Loyalty Enrollment Store::|^GEN_loyaltyEnrollmentStore / DEFAULT=Not Available^|");
	        //add("Loyalty Registered Phone::|^GEN_loyaltyRegisteredPhone / DEFAULT=Not Available^|");
	        add("Loyalty Points Balance::|^GEN_loyaltyPointsBalance / DEFAULT=Not Available^|");
	        add("Loyalty Membership Currency Balance::|^GEN_loyaltyMembershipCurrencyBalance / DEFAULT=Not Available^|");
	        add("Loyalty Gift Balance::|^GEN_loyaltyGiftBalance / DEFAULT=Not Available^|");
	        add("Loyalty LifeTime Purchase Value::|^GEN_loyaltyLifetimePurchaseValue / DEFAULT=Not Available^|");
	        add("Loyalty Last Threshold Level::|^GEN_loyaltyLastThresholdLevel / DEFAULT=Not Available^|");
	        add("Loyalty Lifetime Points::|^GEN_loyaltyLifetimePoints / DEFAULT=Not Available^|");
	        add("Loyalty Redeemable Currency Balance::|^GEN_loyaltyRC / DEFAULT=Not Available^|");
	        add("Loyalty Nudge::|^GEN_loyaltyNudge / DEFAULT=Not Available^|");
	        add("Frequency Buyer program Status::|^GEN_FBPS / DEFAULT=Not Available^|");
	        add("Rewards Promotions Status::|^GEN_RPS / DEFAULT=Not Available^|");






	        //add("Loyalty Hold Balance::|^GEN_loyaltyHoldBalance / DEFAULT=Not Available^|");
	        //add("Loyalty Reward Activation Period::|^GEN_loyaltyRewardActivationPeriod / DEFAULT=Not Available^|");
	        //add("Loyalty Last Earned Value::|^GEN_loyaltyLastEarnedValue / DEFAULT=Not Available^|");
	        //add("Loyalty Last Redeemed Value::|^GEN_loyaltyLastRedeemedValue / DEFAULT=Not Available^|");
	        //add("Loyalty Membership Password::|^GEN_loyaltyMembershipPassword / DEFAULT=Not Available^|");
	        //add("Loyalty Login Url::|^GEN_loyaltyLoginUrl / DEFAULT=Not Available^|");
	        //add("Loyalty Reward Expiration Period::|^GEN_loyaltyRewardExpirationPeriod / DEFAULT=Not Available^|");
	        //add("Loyalty Gift Amount Expiration Period::|^GEN_loyaltyGiftAmountExpirationPeriod / DEFAULT=Not Available^|");
	        //add("Loyalty Membership Expiration Date::|^GEN_loyaltyMembershipExpirationDate / DEFAULT=Not Available^|");
	        //add("Loyalty Gift-card Expiration Date::|^GEN_loyaltyGiftCardExpirationDate / DEFAULT=Not Available^|");
	        //add("Loyalty Last Bonus Value::|^GEN_loyaltyLastBonusValue / DEFAULT=Not Available^|");
	        //add("Loyalty Reward Expiring Value::|^GEN_loyaltyRewardExpiringValue / DEFAULT=Not Available^|");
	        //add("Loyalty Gift Amount Expiring Value::|^GEN_loyaltyGiftAmountExpiringValue / DEFAULT=Not Available^|");
	        
			add("Unsubscribe Link::|^GEN_unsubscribeLink^|");
			add("Sender Email Address::|^GEN_senderEmailAddress^|");
			add("Web-Page Version Link::|^GEN_webPageVersionLink^|");
			add("Date_Today::|^DATE_today^|");
			add("Date_Tomorrow::|^DATE_tomorrow^|");
			add("Date_7_days::|^DATE_7_days^|");
			add("Date_30_days::|^DATE_30_days^|");
			add("Date_X_days::|^DATE_X_days^|");
			add("Share on Twitter::|^GEN_shareOnTwitter^|");
			add("Share on Facebook::|^GEN_shareOnFacebook^|");
            add("Forward To Friend::|^GEN_forwardToFriend^|");
            //add("Subscriber Preference Link::|^GEN_updatePreferenceLink^|");
            add("Update Subscription Preference::|^GEN_updatePreferenceLink^|");
            add("Confirm Subscription Link::|^GEN_confirmSubscriptionLink^|");
       //     add("Recommendation URL1::|^REC_RecoProductLink_1^|");
         //   add("Image Description1::|^REC_ImageDescription_1^|");
         //   add("Recommended Image1::|^REC_RecommendedImage_1^|");
          //  add("Recommendation URL2::|^REC_RecoProductLink_2^|");
         //   add("Image Description2::|^REC_ImageDescription_2^|");
          //  add("Recommended Image2::|^REC_RecommendedImage_2^|");
          //  add("Recommendation URL3::|^REC_RecoProductLink_3^|");
          //  add("Image Description3::|^REC_ImageDescription_3^|");
           // add("Recommended Image3::|^REC_RecommendedImage_3^|");
          //  add("Recommendation URL4::|^REC_RecoProductLink_4^|");
          //  add("Image Description4::|^REC_ImageDescription_4^|");
          //  add("Recommended Image4::|^REC_RecommendedImage_4^|");
            //for custom footer
           /* add("Org Unit::|^GEN_orgUnitOC^|");*/
            /*add("Store Footer Address::|^GEN_storeAddressFooter^|");*/
            /*add("Company OR Store Address One::|^GEN_companyORStoreAddressOne^|");
            add("Company OR Store Address Two::|^GEN_companyORStoreAddressTwo^|");
            add("Company OR Store City::|^GEN_companyORStoreCity^|");
            add("Company OR Store State::|^GEN_companyORStoreState^|");
            add("Company OR Store Pin::|^GEN_companyORStorePin^|");
            add("Company OR Store Phone::|^GEN_companyORStorePhone^|");*/
		}
	};
	
	List<String> OCPLACEHOLDERS_LIST = new ArrayList<String>() {
		{
			add("Loyalty Member Tier::|^GEN_loyaltyMemberTier / DEFAULT=Not Available^|");
			add("Loyalty Member Status::|^GEN_loyaltyMemberStatus / DEFAULT=Not Available^|");  
			add("Loyalty Hold Balance::|^GEN_loyaltyHoldBalance / DEFAULT=Not Available^|");	        
			add("Loyalty Reward Activation Period::|^GEN_loyaltyRewardActivationPeriod / DEFAULT=Not Available^|");	        
			add("Loyalty Last Earned Value::|^GEN_loyaltyLastEarnedValue / DEFAULT=Not Available^|");	        
			add("Loyalty Last Redeemed Value::|^GEN_loyaltyLastRedeemedValue / DEFAULT=Not Available^|");	        
			add("Loyalty Membership Password::|^GEN_loyaltyMembershipPassword / DEFAULT=Not Available^|");	        
			add("Loyalty Login Url::|^GEN_loyaltyLoginUrl / DEFAULT=Not Available^|");	        
			add("Loyalty Reward Expiration Period::|^GEN_loyaltyRewardExpirationPeriod / DEFAULT=Not Available^|");	        
			add("Loyalty Gift Amount Expiration Period::|^GEN_loyaltyGiftAmountExpirationPeriod / DEFAULT=Not Available^|");	        
			add("Loyalty Membership Expiration Date::|^GEN_loyaltyMembershipExpirationDate / DEFAULT=Not Available^|");	        
			add("Loyalty Gift-card Expiration Date::|^GEN_loyaltyGiftCardExpirationDate / DEFAULT=Not Available^|");	        
			add("Loyalty Last Bonus Value::|^GEN_loyaltyLastBonusValue / DEFAULT=Not Available^|");	        
			add("Loyalty Reward Expiring Value::|^GEN_loyaltyRewardExpiringValue / DEFAULT=Not Available^|");	        
			add("Loyalty Gift Amount Expiring Value::|^GEN_loyaltyGiftAmountExpiringValue / DEFAULT=Not Available^|");
			add("Loyalty Registered Phone::|^GEN_loyaltyRegisteredPhone / DEFAULT=Not Available^|");
			add("ReceiptAmount::|^GEN_receiptAmount / DEFAULT=Not Available^|");
            
		}
	};
	// Headers for Bulk enrollments
		List<String> BULK_ENROLL_LIST = new ArrayList<String>() {
			{
				add("Home Store");
				add("Card Number");
				add("Customer Id");
				add("First Name");
				add("Last Name");
				add("Mobile");
				add("Email");
				add("Street");
				add("Address2");
				add("City");
				add("State");
				add("Zip");
				add("Country");
				add("Birthday");
				add("Gender");
				add("Created Date");
			}
		};
	
	List<String> BULK_TRANS_LIST = new ArrayList<String>() {
		{
			add("Transaction Type");
			add("Store Number");
			add("Customer Id");
			add("Docsid");
			add("Card Number");
			add("Entered Amount");
			add("Created Date");
		}
	};

	
	
	
	String PH_CAMPAIGN_ADDRESS_CONTACT_HOMESTORE = "|^GEN_ContactHomeStore^|";
	String PH_CAMPAIGN_ADDRESS_CONTACT_LAST_PURCHASED_STORE = "|^GEN_ContactLastPurchasedStore^|";
	/**
	 * Suppress Contacts type
	 */
	Map<String, String> SUPPTYPE_MAP = new HashMap<String, String>() {
		{
			put("SUPP_TYPE_BOUNCED","bouncedcontact");
			put("SUPP_TYPE_USERADDED","useraddedcontact");
		}	
	}; 
	
	String SUPP_TYPE_BOUNCED = "bouncedcontact";
	String SUPP_TYPE_USERADDED = "useraddedcontact";
	
	String SMS_SUPP_TYPE_USERADDED = "User Added Number";
	String SMS_SUPP_TYPE_INVALID = "Invalid Number";
	String SMS_SUPP_TYPE_OPTED_OUT = "Opted-out";
	String SMS_SUPP_TYPE_DND = "User registered with NDNC";
	String SMS_DLR_STATUSCODE_TOKEN="err";
	String SMS_DLR_STATUS_TOKEN="stat";
	String SMS_DLR_MSGID_TOKEN="id";
	String SMS_DLR_SEQ_NUMBER_TOKEN="seq";
	
	
	/**
	 * Pmta Submitter source type
	 */
	
	String SOURCE_CAMPAIGN = "CampaignSchedule";
	String SOURCE_TRIGGER = "EventTrigger";
	String sOURCE_MARKETING_PROGRAM= "MarketingProgram";
	String SOURCE_NOTIFICATION_CAMPAIGN = "NotificationCampaignSchedule";
	
	
	
	/**
	 * sourcetype for SMS submitter
	 */
	
	String SOURCE_SMS_CAMPAIGN = "SMSCampaignSchedule";
	//whatsapp
	String SOURCE_WA_CAMPAIGN = "WACampaignsSchedule";
        String WA_MSG_TYPE_TEST = "Test WAMsg";
	
	/**
	 * Event trigger Events status for campaign and sms. 
	 * each flag is given 3 bits for future use and.
	 * the value of each flag constant is sum of 3 bits.
	 * 
	 */
	int ET_EMAIL_STATUS_NO_ACTION = 0;
	int ET_EMAIL_STATUS_ACTIVE = 1;
	int ET_EMAIL_STATUS_SENT = 15;
	int ET_EMAIL_STATUS_PAUSED = 2;
	int ET_EMAIL_STATUS_DELETED = 3;
	
	
	int ET_SMS_STATUS_NO_ACTION = 0;
	int ET_SMS_STATUS_ACTIVE = 1;
	int ET_SMS_STATUS_SENT = 15;
	int ET_SMS_STATUS_PAUSED = 2;
	int ET_SMS_STATUS_DELETED = 3;
	
	
	
	String SMS_ACCOUNT_TYPE_TRANSACTIONAL = "TR";
	String SMS_ACCOUNT_TYPE_PROMOTIONAL = "PR";
	String SMS_ACCOUNT_TYPE_OPTIN = "OP";
	
	
	
	/**
	 *  Event Trigger Flags Options (compared with 32 bit options_flag in EVENTRIGGER DB)
	 *  each flag is given 3 bits for future use and
	 *  the value of each flag constant is sum of 3 bits
	 *  if (ET_TRIGGER_IS_ACTIVE_FLAG & options_flag = ET_TRIGGER_IS_ACTIVE_FLAG) => trigger is active
	 *  ET- EventTrigger
	 */
	
	
	//1-3 bits default=active
	int ET_TRIGGER_IS_ACTIVE_FLAG = 1;  //0x1  
	/*int ET_TRIGGER_EVENTS_SEND_EMAIL_CAMPAIGN_FLAG = 1;
	int ET_TRIGGER_EVENTS_SEND_SMS_CAMPAIGN_FLAG = 2;
	*/
	
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
	
	int ET_MERGE_CUSTOMFIELDS = 262144; //0x20000 
		
	/*
	 * 19-21 bits 
	 */
	int ET_REMOVE_FROM_CURRENT_ML_FLAG = 524288; 	
	
	String ET_TYPE_CONTACT_DATE = "CDATE";
	String ET_TYPE_SPECIFIC_DATE = "SDATE";
	String ET_TYPE_CAMPAIGN_CLICK = "CAMPCLICK";
	String ET_TYPE_CAMPAIGN_OPEN = "CAMPOPEN";
	String DELIMITER_PIPE = "|";
	String DELIMITER_DOUBLE_PIPE = "||";

	String MAILINGLIST_STATUS_ACTIVE = "Active";
	String MAILINGLIST_STATUS_PURGING = "Purging";
	
	String MAILINGLIST_CONFIGURED_TYPE_CAMPAIGN="campaign";
	String MAILINGLIST_CONFIGURED_TYPE_SMS="SMS";
	String MAILINGLIST_CONFIGURED_TYPE_EVENT_TRIGGER="ET";
	
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
	int ET_TYPE_ON_LOYALTY_DIFFERENCE = 32768;
	int ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE = 65536;
	//int ET_TYPE_ON_GIFT_DIFFERENCE = 16384;
	
	
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
	String USER_SMSTOOL_BSMS_ITS_PAKISTAN = "BsmsItsPakistan";
	String USER_SMSTOOL_CM = "CMCom";
	
	//Added For InfoBip
	String USER_SMSTOOL_INFOBIP = "Infobip";
	String USER_SMSTOOL_EQUENCE= "Equence";
	String USER_SMSTOOL_SYNAPSE="Synapse";
	String USER_SMSTOOL_INFOCOMM = "Infocomm";
	String USER_SMSTOOL_MESSAGEBIRD="Messagebird";
	String USER_SMSTOOL_MIM="MyInboxMedia";

	/*
	 *  SendGrid API v2 json list.
	 */
	
	String SG_ADD_NEW_USER = "customer.add.json";
	String SG_EDIT_USER = "reseller.manage.json";
	String SG_DELETE_USER = "reseller.delete.json";
	String SG_GET_ALL_USERS = "reseller.manage.json";
	String SG_ENABLE_USER = "reseller.manage.json";
	String SG_DISABLE_USER = "reseller.manage.json";
	String SG_DISABLE_USER_APP_ACCESS = "reseller.manage.xml";
	
	
	/*
	*  Social Accounts  
	*/
	
	int SOCIAL_ADD_FB = 1;
	int SOCIAL_ADD_LINKEDIN = 2;
	int SOCIAL_ADD_TWITTER = 4;
	
	String SOCIAL_CAMP_STATUS_SENT = "SENT";
	String SOCIAL_CAMP_STATUS_ACTIVE = "ACTIVE";
	String SOCIAL_CAMP_STATUS_RUNNING = "RUNNING";
	String SOCIAL_CAMP_STATUS_DRAFT = "DRAFT";
	
	String SOCIAL_SCHEDULE_STATUS_SENT = "SENT";
	String SOCIAL_SCHEDULE_STATUS_POSTNOW = "Post Now";
	String SOCIAL_SCHEDULE_STATUS_SCHEDULE = "Schedule";
	String SOCIAL_SCHEDULE_STATUS_FAILED = "FAILED";
	
	String SOCIAL_FACEBOOK_FB = "FB";
	String SOCIAL_TWITTER_TWIT = "TWIT";
	String SOCIAL_LINKEDIN_LNKIN = "LNKIN";
	
	String SOCIAL_FAILURE_ACTION_STATUS_SENT = "SENT";
	String SOCIAL_FAILURE_ACTION_STATUS_INFO = "INFO";
	String SOCIAL_FAILURE_ACTION_STATUS_RETRY = "RETRY";
		   	

	/**
	 * URL Short code Generation
	 */
	
	 String URL_SHORT_CODE_TYPE_CUSTOM = "Custom";
	 String URL_SHORT_CODE_TYPE_SMS = "SMS";
	 String URL_SHORT_CODE_TYPE_SOCIAL = "Social";

	 /**
	  * User account Types
	  */
	 String USER_ACCOUNT_TYPE_PRIMARY="Primary";
	 String USER_ACCOUNT_TYPE_SHARED="Shared";
	 String USER_ACCOUNT_TYPE_CUSTOM="Custom";
	 String USER_ACCOUNT_TYPE_STANDARD="Standard";
	 String USER_AND_ORG_SEPARATOR = "__org__";
	 String USER_DETAIL_SEPARATOR="__";
	 
	 
	 
	 /**
	  * OPTINTEL MAPPING TYPES
	  */
	 String POS_MAPPING_TYPE_SALES = "Sales";
	 String POS_MAPPING_TYPE_SKU = "SKU";
	 String POS_MAPPING_TYPE_CONTACTS = "Contacts";
	 String POS_MAPPING_TYPE_HOMES_PASSED= "BCRM";
	 //Changes 2.5.4.0
	 String POS_MAPPING_TYPE_LOYALTY = "Loyalty";
	 String POS_MAPPING_TYPE_LOYALTY_AND_CONTACT = "Both";
	 /**
	  * MialingList type
	  */
	 
	 String MAILINGLIST_TYPE_ADDED_MANUALLY = "Normal";
	 String MAILINGLIST_TYPE_WEB_FORMS = "webForms";
	 String MAILINGLIST_TYPE_POS = "POS";
	 String MAILINGLIST_TYPE_HOMESPASSED = "BCRM";
	 String MAILINGLIST_TYPE_OPTIN_LIST = "Mobile Opt-in List";
	 
	 
	 
	 /**
	  *  Sparkbase Card Status
	  */
	 String SPARKBASE_CARD_STATUS_INVENTORY = "inventory";
	 String SPARKBASE_CARD_STATUS_ACTIVATED = "activated";
	 String SPARKBASE_CARD_STATUS_SELECTED = "selected";
	 String LOYALTY_CARD_STATUS_SELECTED = "selected";
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
	 
	 /**
	  * Contact DashBoard
	  *  //,
	  */
	 String LIMIT ="limit"; 
	 String VIEW_ALL = "viewAll"; 
	 
	 /***
	  * Loylty dashBoard
	  */
	 String LOY_TYPE_BOTH = "both"; //TODO check this  String value
	 String LOY_TYPE_LOYLTY = "Loyalty"; //TODO check this  String value
	 
	 String CARD_TYPE_GIFT_CARD = "Gift Card";
	 String TRENDS_LOACTION_ID = "LOCATION_ID";
	 String TRENDS_EMP_ID = "EMP_ID";

	 
	 /**
	  * Contact optin medium types
	  */
	 String CONTACT_OPTIN_MEDIUM_POS = "POS";
	 String CONTACT_OPTIN_MEDIUM_MOBILE = "Mobile Opt-In";
	 String CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY = "AddedManually";
	 String CONTACT_OPTIN_MEDIUM_WEBFORM = "WebForm";
	 String CONTACT_OPTIN_MEDIUM_EXTERNALMAIL = "ExternaEmail";
	 String CONTACT_OPTIN_MEDIUM_ECOMMERCE = "eComm";
	 String CONTACT_OPTIN_MEDIUM_OFFLINE = "Offline";
//	 String CONTACT_OPTIN_MEDIUM_MANUAL_UPLOAD = "Manual Upload";
	 
	 
	 String CUSTOM_TEMPLATE_TYPE_DOUBLEOPTIN= "Double Opt-in";
	 String CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT= "Parental Consent";
     String CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN= "Loyalty Enrollment";
     String CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL="Welcome Message";
     String CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION="Tier Upgradation";
     String CUSTOM_TEMPLATE_TYPE_EARNED_BONUS="Earning Bonus";
     String CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION="Reward Expiration";
     String CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION="Membership Expiration";
     String CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION="Gift Amount Expiration";
     String CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION="Gift-Card Expiration";
     String CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE="Gift-Card Issuance";
     String CUSTOM_TEMPLATE_TYPE_FEEDBACK_FORM="FeedBack Form";
     String CUSTOM_TEMPLATE_TYPE_SPECIAL_REWARDS="Reward Issuance";
     String CUSTOM_TEMPLATE_TYPE_WELCOME_SMS="Welcome SMS";
     String CUSTOM_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT="Loyalty Adjustment";
     String CUSTOM_TEMPLATE_TYPE_LOYALTY_ISSUANCE="Loyalty Issuance";
     String CUSTOM_TEMPLATE_TYPE_LOYALTY_REDEMPTION="Loyalty Redemption";
     String CUSTOM_TEMPLATE_TYPE_OTP_MESSAGE="OTP MESSAGE";
     String CUSTOM_TEMPLATE_TYPE_REDEMPTION_OTP="Redemption OTP";
     /**
	  * Segment Type
	  */
	 String SEGMENT_TYPE_HOMESPASSED= "BCRM";
	 String SEGMENT_TYPE_CONTACT = "Contact";


	 /**
	  * Client Type
	  */
	 String CLIENT_TYPE_BCRM = "BCRM";
	 String CLIENT_TYPE_POS = "POS";


	 /***
	  * Coupon Type
	  */
	 String COUP_GENT_TYPE_SINGLE = "single";
	 String COUP_GENT_TYPE_MULTIPLE = "multiple";

	 String COUP_CAMP_SMS_TYPE = "SMS";
	 String COUP_CAMP_EMAIL_TYPE = "EMAIL";
	 String COUP_CAMP_AUTO_EMAIL_TYPE = "AUTO EMAIL";
	 String COUP_CAMP_CONTACT_TYPE = "Contact";
	 
	 String COUP_STATUS_ACTIVE = "Active";
	 String COUP_STATUS_RUNNING = "Running";
	 String COUP_STATUS_EXPIRED = "Expired";
	 String COUP_STATUS_PAUSED = "Paused";
	 
	 String EVENTS_STATUS_ACTIVE = "Active";
	 String EVENTS_STATUS_RUNNING = "Running";
	 String EVENTS_STATUS_EXPIRED = "Expired";
	 String EVENTS_STATUS_SUSPENDED = "Suspended";
	 
	 	 
	 String COUP_STATUS_DRAFT = "Draft";
	 
	 String COUP_VALIDITY_PERIOD_STATIC ="S";
	 String COUP_VALIDITY_PERIOD_DYNAMIC = "D";
	 
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
	 
	 String OC_PH_TYPES = "GEN_, DATE_, UDF_, CC_, SYM_";
	 

	 String SMS_CAMP_DRAFT_STATUS_STEP_TWO = "smsCampMlStep";
	 String SMS_CAMP_DRAFT_STATUS_STEP_THREE = "smsCampFinalStep";
	 String SMS_CAMP_DRAFT_STATUS_STEP_ONE = "";
	 
     String SMS_CAMP_DRAFT_STATUS_STEP_COMPLETE= "Complete";

	  //APP-4288
	 String WA_CAMP_DRAFT_STATUS_STEP_TWO = "WACampMlStep";
	 String WA_CAMP_DRAFT_STATUS_STEP_THREE = "WACampFinalStep";
	 String WA_CAMP_DRAFT_STATUS_STEP_ONE = "";

     String WA_CAMP_DRAFT_STATUS_STEP_COMPLETE= "Complete";

	 String COUP_CODE_STATUS_ACTIVE = "Active";
	 String COUP_CODE_STATUS_INVENTORY = "Inventory";
	 String COUP_CODE_STATUS_RUNNING = "Running";
	 String COUP_CODE_STATUS_ISSUED="Issued";
	 String COUP_CODE_STATUS_REDEEMED ="Redeemed";
	 String COUP_CODE_STATUS_EXPIRED = "Expired";

	 String SMS_ORG_KEYWORD_STATUS_APPROVED = "Approved";
	 String SMS_ORG_KEYWORD_STATUS_PENDING_APPROVAL = "Pending Approval";
	 
	 String EDITOR_TYPE_PLAIN = "PlainEditor";
	 String EDITOR_TYPE_TEXT = "TextEditor";
	 String EDITOR_TYPE_BEE = "beeEditor";
	 
	 String SB_TRANSACTION_INQUIRY = "Inquiry";
	 String SB_TRANSACTION_ENROLLMENT = "Enrollment";
	 String SB_TRANSACTION_ACCOUNT_HISTORY = "Account History";
	 String SB_TRANSACTION_LOYALTY_ISSUANCE = "Loyalty Issuance";
	 String SB_TRANSACTION_LOYALTY_REDEMPTION = "Loyalty Redemption";
	 String SB_TRANSACTION_GIFT_ISSUANCE = "Gift Issuance";
	 String SB_TRANSACTION_GIFT_REDEMPTION = "Gift Redemption";
	 String SB_TRANSACTION_ADJUSTMENT = "Adjustment";
	 
	  String TYPE_SALES_TO_CONTACTS="Sales to Contacts";
	  String TYPE_SALES_TO_Inventory="Sales to Inventory";
	  
	  
	  String TYPE_EMAIL_CAMPAIGN="Email";
	  String TYPE_SMS_CAMPAIGN="SMS";
	  
	  String CON_MOBILE_STATUS_ACTIVE = "Active"; 
	  String CON_MOBILE_STATUS_NOT_A_MOBILE = "Not Available";
	  String CON_MOBILE_STATUS_OPTIN_PENDING = "Optin Pending";
	  String CON_MOBILE_STATUS_PENDING="Pending";
	  
	  
	  /*String SMS_SETTINGS_FIELD = "keyword";
	  String SMS_KEYWORD_SETTINGS_TYPE_HELP = "helpKeyword";
	  String SMS_KEYWORD_SETTINGS_TYPE_OPT_OUT = "optoutKeyword";*/
	  String SMS_LOW_CREDITS_WARN_TEXT = "lowSMSCreditsWarnText";
	  
	  String EQ_TYPE_LOW_SMS_CREDITS = "lowSMSCredits";
	  String FILTER_SHOW_GRANTED = "FILTER_SHOW_GRANTED";
	  //String SMS_SENDERID = "SMSSenderId";
	  String COUP_PROVIDER_FOR_SUBSCRIBER_URL = "CouponProviderUrl"; 
	  



// webform 
	  String WEBFORM_TYPE_SIGNUP="Sign-up";
	  String WEBFORM_TYPE_LOYALTY_CARD="Loyalty Card Verification";
	  String WEBFORM_TYPE_LOYALTY_SIGNUP = "Loyalty Sign-up";
	  
	  String WEBFORM_TYPE_SIGNUP_LOYALTY_ALL="All Loayalty Customers";
	  String WEBFORM_TYPE_SIGNUP_LOYALTY_CHECKED=" Checked Loyalty Customers";
	  String FEED_BACK_WEB_FORM="FeebBack Form";
	  
	  // Roles groups
	  
	  
	  String  SECROLE_TYPE_CUSTOM="Custom";
	  
	  String SECGROUP_TYPE_ADMIN="Admin";
	  String  SECGROUP_TYPE_CUSTOM="Custom";
	  
	  
	  //added for sharing
	  String SHARED_ITEM_LISTS = "Lists";
	  String SHARED_ITEM_SEGMENTS = "Segments";
	  String MY_SHARED_ITEM_SEGMENTS = "MySegments";
	  String OTHERS_SHARED_ITEM_SEGMENTS = "OthersSegments";
	  String MY_SHARED_ITEM_LISTS = "MyLists";
	  String OTHERS_SHARED_ITEM_LISTS = "OthersLists";
	  String LISTIDS_SET = "LISTIDS_SET";
	  String SEGMENTIDS_SET = "SEGMENTIDS_SET";
	  
	  
	  
	  String  SECROLE_TYPE_ALL="All";
	  String  SECROLE_TYPE_ADMIN="Admin";
	  String  SECROLE_TYPE_OPT_ADMIN="OC Admin";
	  String  SECROLE_TYPE_BCRM_ADMIN="BCRM Admin";
	  
	  
	  
	  // added for digi receipt 
	  
	  String PROPS_KEY_APPLICATION_IP = "subscriberIp";
	  String EQ_TYPE_DIGITALRECIEPT = "DigitalReceipt";
	  
	  /**
		 * PropertyUtil keys
		 */
		String PROPS_KEY_SENDGRID_MULTIMAIL_USER_ID = "SendGridMultiMailUserId";
		String PROPS_KEY_SENDGRID_MULTIMAIL_USER_PWD = "SendGridMultiMailUserPwd";
		String PROPS_KEY_SENDGRID_SINGLEMAIL_USER_ID = "SendGridSingleMailUserId";
		String PROPS_KEY_SENDGRID_SINGLEMAIL_USER_PWD = "SendGridSingleMailUserPwd";
		
		String PROPS_KEY_SENDGRID_THREAD_COUNT = "SendGridThreadCount";
	  
	  
	  //added for Digital Receipts transaction data processing
	  String USERS_ACC_TYPE_PRIMARY="Primary";
	  String DR_JSON_PROCESS_STATUS_NEW="New";
	  String DR_JSON_PROCESS_STATUS_PROCESSED="Processed";
	  String DR_JSON_PROCESS_STATUS_UNPROCESSED="Unprocessed";

	
			
			
		String SMS_KEYWORD_STOP = "STOP";
		String SMS_KEYWORD_STOPALL = "STOP ALL";
		String SMS_KEYWORD_END = "END";
		String SMS_KEYWORD_CANCEL = "CANCEL";
		String SMS_KEYWORD_UNSUBSCRIBE = "UNSUBSCRIBE";
		String SMS_KEYWORD_QUIT = "QUIT";
		String SMS_KEYWORD_HELP = "HELP";
			
		/**
		 * userScoreSetting pagevisited Condition Type
		 */
		
		String PVISITCONTAINS = "contains";
		String PVISITREGEX = "Reg Exp";
		String PVISITALLVISIT = "All Visit";
		
		/**
		 * userScoresetting SourceOf visit Condition Type
		 */
		String SRCVISITCONTAINS = "contains";
		String SRCVISITDNTCONTAINS = "Does not contains";
		String SRCVISITALLTHESEWRDS = "All these words";
		String SRCVISITANYOFTHESEWRDS = "Any of these words";
		
		/**
		 * TempConstants
		 */
		
		String _STATUS = "status";
		String _CATEGORIES = "categories";
		
		
		 String CONTACT_RESUBSCRIBE = "ReSubscribe";
		 String _UNSUBSCRIBE_ALREADY_MSG = "";
		 String _UNSUBSCRIBE_REQUEST_MSG = "";
		 /**
		  * Added for Unsubscribe Performance.
		  */
		 String UNSUBSCRIBE_REQUEST_TYPE = "unsubReqType";
		 String UNSUBSCRIBE_REQUEST_VALUE_UNSUB = "unsubscribe";
		 String UNSUBSCRIBE_REQUEST_VALUE_RESUB = "reSubscribe";
		 String UNSUBSCRIBE_REQUEST_VALUE_RESUB_UPDATE = "resubUpdate";
		 String UNSUBSCRIBE_REQUEST_VALUE_UNSUB_UPDATE = "unsubUpdate";
		 String _UNSUBSCRIBE_RESPONSE_MSG = 
					" Thank you very much for your response.\n" +
				" If you want to resubscribe click on re-subscribe.";
					/*" You have successfully unsubscribed from the following categories.\n" +*/
				
		 String _RESUBSCRIBE_RESPONSE_MSG = 
					"Thank you very much for your interest to receive the emails from this user.";
		 
		 
		 // dr status
		 String DR_STATUS_BOUNCED= "Bounced";
		 String DR_STATUS_SPAMMED= "Spammed";
		 String DR_STATUS_SUCCESS = "Success";
		 String DR_STATUS_SUBMITTED = "Submitted";
		 String DR_STATUS_FAILURE = "Failure";
		 String DR_STATUS_DROPPED= "dropped";
		 String DR_STATUS_BOUNCE= "bounce";
		 String DR_STATUS_SPAME= "spamreport";
		 String DR_STATUS_PENDING= "Status Pending";
		 
		 String DR_STATUS_OPENED= "Openes";
		 String DR_STATUS_CLICKED= "Clickes";
		 
		 String DR_CLICK_URL = "|^DRClickTrackUrl^|";
		 String DR_OPEN_TRACK_URL = "|^DROpenTrackUrl^|";
		 String DR_STATUS_SENT   = "Sent";
		 
		 String DR_SENTID = "sentId";
		 String DR_URL = "url";
		 String DR_CLICK_TRACK_URL = "|^clickUrl^|";
		 
		 String PROMO_OC_LOYALTY = "OC-LOYALTY";
		 String PROMO_ALL = "ALL";
		 String PROMO_BALANCES = "BALANCES";
		 String PROMO_SPECIFIC = "SPECIFIC";
		 String PROMO_DISCOUNTS = "DISCOUNTS";
		 
		 String TWEET_ON_TWITTER = "tweetOnTwitter";
		 String SHARE_ON_FB = "sharedOnFb";
		 
		 String STRING_NILL = "";
		 String STRING_WHITESPACE = " ";
		 String STRING_NOTAVAILABLE = "Not Available";
		 String SCS_STATUS_BOUNCED = "Bounced";
		String SCS_STATUS_SUCCESS = "Success";
		
		 // default folder for my templates
		String DEFAULT_MYTEMPLATES_FOLDER="Default";
		String SUBSCRIBER_LOGGER = "subscriber";
		String CIM_MERCHANT_LOGIN_NAME = "CIMMerchantLoginName";
		String CIM_MERCHANT_TRANSACTION_KEY = "CIMMerchantTransactionKey";
		
 //Added for Referral profaram
		
	
		String REFERRAL_TEMPLATE_NAME="Referral_Success";		
		String CUST_TEMP_WELCOME_TYPE= "webformWelcomeEmail";
		
		String INTERACTION_CAMPAIGN_CRID_PH = "<CAMPAIGNCRIDS>";
		String INTERACTION_CAMPAIGN_IDS_PH = "<CAMPAIGNIDS>";
		String APP_SHORTNER_URL = "ApplicationShortUrl";
		
		 String SERVER_TIMEZONE_VALUE = "ServerTimeZoneValue";
	// Added for Subscriber preference center
		
		String CAMP_CATEGORY_PERSONAL ="Personalized Offers";
		String CAMP_CATEGORY_EVENTS ="Events";
		
		 String CON_MOBILE_STATUS_OPTED_OUT = "Opted-out"; 
		String UNSUB_EMAIL_RAESON="Unsubscribed from Subscriber Preference Center";
		String OPTOUT_MOBILE_RAESON="Opted-out from Subscriber Preference Center";
		
		String EQ_TYPE_SPC_ALERT="Subscriber Preference Alert";
		String EQ_TYPE_SUPPORT_ALERT = "SupportAlert";
				
		
		String CAMP_CATEGORY_TRANSACTIONAL="Transactional";
		Long CAMP_CATEGORY_TRANSACTIONAL_ID = 60000000l;
		String CATEGORY_NO_CATEGORY = "NoCategory";
		
		
		//Added for reset pwd token
		String TOKEN_STATUS_ACTIVE = "Active";
		String TOKEN_STATUS_EXPIRED = "Expired";
		
		String FORGOT_PLACEHOLDERS_FNAME= "[fname]";
		String FORGOT_PLACEHOLDERS_URL= "[url]";
		String FORGOT_PLACEHOLDERS_USER_NAME="<user-name>";
		String FORGOT_PLACEHOLDERS_ORG_NAME="<organization-name>";
 
		String NEWEDITOR_TEMPLATES_FOLDERS_DEFAULT = "Default_Folder";
		//String NEWEDITOR_TEMPLATES_FOLDERS_DEFAULT_FOLDER_NAME = "My Templates";
		String NEWEDITOR_TEMPLATES_FOLDERS_DRAFTS = "Drafts";
		//String NEWEDITOR_TEMPLATES_FOLDERS_FOLDERNAME = "Unknown_" ;
		String NEWEDITOR_TEMPLATES_FOLDERS_FOLDERNAME = "Template of " ;
		String NEWEDITOR_TEMPLATES_PARENT="New_Editor_Templates";
		
		String MYTEMPATES_FOLDERS_DEFAULT = "Default_Folder";
		//String MYTEMPATES_FOLDERS_DEFAULT_FOLDER_NAME = "My Templates";
		String MYTEMPATES_FOLDERS_DRAFTS = "Drafts";
		String MYTEMPATES_FOLDERS_FOLDERNAME = "Unknown_" ;
		String MYTEMPATES_PARENT="My_Folders";
		String MYTEMPATES_NEW_EDITOR_PARENT="New_Editor_Templates";
		
		String TEMPLATE_CATEGORY_MYTEMPLATES = "My Templates";
		String TEMPLATE_CATEGORY_GENERIC = "Basic Templates";
		String TEMPLATE_CATEGORY_GENERIC_DIR_NAME = "Generic_Templates";
		String TEMPLATE_CATEGORY_MYNEWEDITOR = "My NewEditor Templates";
		
		String SPARKBASE_TRANSACTION_STATUS_NEW="New";
		String SPARKBASE_TRANSACTION_STATUS_PROCESSED="Processed";
		String SPARKBASE_TRANSACTION_STATUS_UNPROCESSED="Unprocessed";
		String SPARKBASE_TRANSACTION_STATUS_SUSPEND = "Suspend";
		
		String SPARKBASE_CARD_COMMENTS_LOCATION_INVALID = "locinvalid";
		String SPARKBASE_CARD_COMMENTS_INVENTORY = "inventory";
		String SPARKBASE_CARD_COMMENTS_SBERROR = "sberror";
		String SPARKBASE_CARD_COMMENTS_SBLOYALTY_ADDED = "sbloyalty";
		String SPARKBASE_CARD_COMMENTS_ACTIVATED_IN_SB = "activeinsb";
		
		 String SMS_WRONG_KEYWORD_RESPONSE = "SMSWrongKeywordResponseText";
		// export email campaign report header fields
		 
		public static String notifiactionCampaignHeaderFields[]={"Date Sent","Notification Name","List(s)","Sent","Unique Clicks%","Delivered","Failed"}; 
		public static String emailCampaignHeaderFields[] = {"Email Name","Date Sent","Subject ","List(s)","Sent","Opens %","Clicks %","Unsubs %","Marked as Spam %","Dropped %","Hard Bounces %","Soft Bounce %","Blocked Bounce %","Other Bounce %","Unique Opens ","Unique Clicks ","Unsubs ","Marked as Spam ","Dropped","Hard Bounces ","Soft Bounce","Blocked Bounce","Other Bounce"};
		public static String smsCampaignHeaderFields[]={"Date Sent","SMS Name","List(s)","Sent","Unique Clicks%","Bounced%","Source Type"};
	//
		public static String waCampaignHeaderFields[]={"Date Sent","SMS Name","List(s)","Sent","Unique Clicks%","Bounced%","Source Type"};

		// added for support 
		
		String SUPPORT_ACK_MAIL  ="supportAcknowledgement";
		String SUPPORT_MAIL = "supportMail";
		//String SUPPORT_EMAIL_ADDRESS  ="support@optculture.com";
		String SUPPORT_ORG_NAME = "Optculture Support Team";
		
		
		String SUPPORT_USER_NAME="[userName]";
		String SUPPORT_USER_CLEINT_NAME="[ClientName]";
		String SUPPORT_USER_CONTACT_NAME="[ContactName]";
		String SUPPORT_USER_CONTACT_EMAIL="[ContactEmail]";
		String SUPPORT_USER_CONTACT_PHONE="[ContactPhone]";
		String SUPPORT_USER_DESCRIPTION="[Description]";
		String SUPPORT_USER_CAPTCHA="[Captcha]";
		String SUPPORT_USER_ORG_NAME="[OrganizationName]";
		String SUPPORT_SENDER_NAME="[senderName]";
		String SUPPORT_TYPE="[Type]";
		String SUPPORT_PRODUCT_AREA_TYPE="[ProductAreaType]";
		String SUPPORT_PRODUCT_AREA="[ProductArea]";
		
		String SUPPORT_TYPE_BUG="Report Bug";
		String SUPPORT_TYPE_FEATURE="Feature Request";
		String SUPPORT_TYPE_SERVICE="Service Request";
		String SUPPORT_TYPE_TECH="Technical Questions";
		
		String SUPPORT_PRODUCT_TYPE_WEB="Web Application";
		String SUPPORT_PRODUCT_TYPE_POS="POS Integration";
		
		String EMAIL_TYPE_SUPPORT="Support";
		String EMAIL_TYPE_SUPPORT_ACK="Support Acknowledgement";
		
		String EMAIL_TYPE_SUPPORT_SUBJECT  ="Support Request";
		String EMAIL_TYPE_SUPPORT_ACK_SUBJECT="Support Acknowledgement Response ";
		String EMAIL_TYPE_SUPPORT_SENDER_NAME="Dev Team";
		
		String SUPPORT_STATUS_ACTIVE="Active";
		
		String DEV_EMAIL_ID="development@optculture.com";
	
		String LOYALTY_ALERTS_PLACEHOLDERS_FNAME= "[fname]";
		String LOYALTY_ALERTS_PLACEHOLDERS_USERNAME= "[username]";
		String LOYALTY_ALERTS_PLACEHOLDERS_NOOFCARDS= "[noofcards]";
		String LOYALTY_ALERTS_PLACEHOLDERS_LOCATIONID= "[locationid]";

		// Added for SMS
		String SMS_TYPE_TRANSACTIONAL = "TR";
		String SMS_TYPE_PROMOTIONAL = "PR";
		String SMS_TYPE_2_WAY = "TW";
		String SMS_SENDING_TYPE_OPTIN = "OP";
		String SMS_TYPE_OUTBOUND = "OB";
		String SMS_TYPE_ALERT = "Alerts";
		
		String SMS_TYPE_NAME_TRANSACTIONAL = "Transactional";
		String SMS_TYPE_NAME_PROMOTIONAL = "Promotional";
		String SMS_TYPE_NAME_BROADCAST = "Broadcast";
		String SMS_TYPE_NAME_2_WAY = "Two-Way";
		String SMS_TYPE_NAME_OUTBOUND = "OutBound";
		String SMS_TYPE_NAME_OPTIN = "Opt-in";
		
		String SMS_COUNTRY_INDIA = "India";
		String SMS_COUNTRY_US = "US";
		String SMS_COUNTRY_PAKISTAN = "Pakistan";
		String SMS_COUNTRY_UAE = "UAE";
		String SMS_COUNTRY_PHILIPPINES = "Philippines";
		String SMS_COUNTRY_MYANMAR = "Myanmar";
		String SMS_COUNTRY_CANADA = "Canada";
		String SMS_COUNTRY_KUWAIT = "Kuwait";
		String SMS_COUNTRY_QATAR = "Qatar";
		String SMS_COUNTRY_PANAMA = "Panama";
		String SMS_COUNTRY_SA = "South Africa";
		String SMS_COUNTRY_SINGAPORE = "Singapore";//APP-4688
		


		String SMS_GATEWAY_MODE_SMPP="SMPP";
		String SMS_GATEWAY_MODE_HTTP="HTTP";
		
		String SMS_COUNTRY_CODE_INDIA = "91";
		String SMS_COUNTRY_CODE_US = "1";
		String SMS_COUNTRY_CODE_PAKISTAN = "92";
		String SMS_COUNTRY_CODE_UAE = "971";
		String SMS_COUNTRY_CODE_CANADA = "1";
		String SMS_COUNTRY_CODE_SA = "27";
		String SMS_COUNTRY_CODE_SINGAPORE = "65";//APP-4688
		String SMS_COUNTRY_CODE_QATAR = "974";
		String SMS_COUNTRY_CODE_KUWAIT = "965";

		
		String SMS_GATEWAY_STATUS_ACTIVE = "Active";

		String SMS_OPTIN_LIST="SMS Opt-in List";
		String SMS_OPTIN_EXIST_LIST="SMS_Opt-in_List";
		
		String NEW_USER_DETAILS_PLACEHOLDERS_FNAME= "[fname]";
		String NEW_USER_DETAILS_PLACEHOLDERS_USERNAME= "[username]";
		String NEW_USER_DETAILS_PLACEHOLDERS_PASSWORD= "[password]";
		String NEW_USER_DETAILS_PLACEHOLDERS_ORGID= "[orgid]";
		String MAX_KEYWORD_LIMIT_REACHED = "You have reached your max keyword limit. Please contact Admin to increase the limit.";
		String EXP_DATE_BEFORE_START = "Keyword validity expiry date is before start date.";
		String TO_DATE_MORE_THAN_SIX_MONTHS = "Keyword validity period  should not be more than 180 days.";
		String TWO_WAY_PROMO_TEXT_ID = "Auto generated";
		String OPT_IN_ACC_MESSAGE = "You do not have Opt-In account to send SMS to only opt-in contacts. Please contact Admin .";
		String NO_RECV_NUM = "Keyword can not be created without receiving number.\n Please contact Admin to have keyword receiving number .";
		
		String UNICEL_DEF_USERID = "magpromo";
		String UNICEL_DEF_PWD = "UnicelPromoPwd";
		String UNICEL_DEF_SENDERID = "Alerts";
		
		String CLICKATELL_DEF_USERID = "optculture10";
		String CLICKATELL_DEF_PWD = "ClickaTellPwd";
		String CLICKATELL_DEF_SENDERID = "888555";
		String CLICKATELL_DEF_APIID = "3389885";
		//Added for infobip
		String INFOBIP_DEF_USERID = "risservices";
		String INFOBIP_DEF_PWD = "InfoBipPromoPwd";
		String INFOBIP_DEF_SENDERID = "INFOBIP";
	//	String INFOBIP_DEF_APIID = null;
		
		// sms queries constants 
		String SMS_CAMPAIGN_SENT_STATUS_ALL = "All";
		String SMS_CAMPAIGN_SUCCESS = "Success";
		String NDNC_REJECTED = "NDNC Rejected";
		String SMS_CAMPAIGN_STATUS_BOUNCE = "Bounced";
		String SMS_CAMPAIGN_STATUS_PENDING = "Status Pending";
		String SMS_CAMPAIGN_STATUS_RECEIVED = "Received";
		String MOBNUM_VALID_FOR_SEARCH = "[0-9]*";
		String SMS_KEYWORD_EXPIRED = "Expired";
		
		// WA queries constants 
		String WA_CAMPAIGN_SENT_STATUS_ALL = "All";
		String WA_CAMPAIGN_SUCCESS = "Success";
		String WA_CAMPAIGN_STATUS_BOUNCE = "Bounced";
		String WA_CAMPAIGN_STATUS_PENDING = "Status Pending";
		String WA_CAMPAIGN_STATUS_RECEIVED = "Received";
//		String MOBNUM_VALID_FOR_SEARCH = "[0-9]*";
		String WA_KEYWORD_EXPIRED = "Expired";
		
		
		String OPTIN_REQUEST_SUBJECT="Request to Change Optin Content ";
		
		String EQ_TYPE_OPTIN_REQUEST_TYPE="Optin Change Content Request";
		String KEYWORD_STATUS_EXPIRED =  "Expired";
		String KEYWORD_STATUS_PENDING = "Pending";
		String KEYWORD_STATUS_ACTIVE = "Active";
		
		// for handling the campaigns already sent
		String OLD_TRACK_URL = "oldTrackUrl";
		String NEW_TRACK_URL = "newTrackUrl";
		
		String RECEVING_NUMBER_TYPE_PROMOTIONAL="PR";
		String RECEVING_NUMBER_TYPE_OPTIN="OP";
		String RECEVING_NUMBER_TYPE_MISSEDCALL="MC";
		
		String RECEVING_NUMBER_TYPE_NAME_MISSEDCALL="Missed Call";
		
		String OPT_SYN_PROMO_REDEMTION = "OptSynch Promoredemtion";
		String OPT_SYN_LOYALTY_ENROLMENT = "OptSync LoyaltyEnrolment";
		String OPT_SYN_LOYALTY_ISSUANCE = "OptSync LoyaltyIssuance";
		String OPT_SYN_DIGITAL_RECEIPT = "OptSync DigitalReceipt";

		String LOYALTY_STAGE_PENDING = "Pending";
		String LOYALTY_STAGE_COMPLETED = "Completed";
		
		String EXPORT_FILE_PROCESSING ="processing";
		String EXPORT_FILE_COMPLETED ="completed";
		String EXPORT_FILE_DELETED ="deleted";
		String EXPORT_FILE_TYPE_SEGMENT="segment";
		
		String COUP_GENT_CAMPAIGN_TYPE_EMAIL="Email";
		String COUP_GENT_CAMPAIGN_TYPE_SMS ="SMS";
	
		String LTY_NAME_PATTERN = "[\\w\\s.',&-]+";
		String COUP_GENT_CAMPAIGN_TYPE_DR="Digital Receipt";
		
		//OptSyncTimerExpiry PH
		
		String OPTSYNC_DOWN_TIME_STR="[DownTimeStr]";
		String OPTSYNC_ID="[OptSync ID]";
		String OPTSYNC_NAME="[OptSync Name]";
		String OPTSYNC_USER_NAME="[UserName]";
		String OPTSYNC_ORGID="[OrgID]";
		String OPTSYNC_SENDER_NAME="[senderName]";
		String OPTSYNC_SENDER_NAME_VALUE="Team OptCulture";
		String OPTSYNC_DOWN_TIME_PROPERTY ="optSyncExpiryText";
		String OPTSYNC_UP_ALERT_PROPERTY ="optSyncUPAlertMsg";
		String OPTSYNC_HIT_TIME="[HitTime]";
		String OPTSYNC_DOWN_TIME="[DownTime]";
		
		
		String USER_ROLE_SUPER_USER ="Super User";
		String USER_ROLE_CUSTOM_USER ="Custom_User";
		String USER_ROLE_OCADMIN="OC Admin";
		String USER_ROLE_STORE_OPERATOR ="StoreOperatorRole";
		
		String NO_PROMOTION_STR = "Promotion Not Available";
		
		//CreateOptSyncUserController Constant's
		int MAX_EMAIL_LENGTH=50;
		
		//Added for Customer Lookup screen
		String FULL_NAME="fullName";
		String FIRST_NAME="first_name";
		String LAST_NAME="last_name";
		String MOBILE_PHONE ="mobile_phone";
		String EMAIL_ID ="email_id";
		String CARD_NUMBER="card_number";
		String NOT_APPLICABLE="N/A";
		String PHONE_NUMBER="Phone Number";
		String MEMBERSHIP_NUMBER="Membership Number";
		String EMAIL_ADDRESS="Email Address";
		String BUTTON_LABEL_SUSPEND ="Suspend";
		String BUTTON_LABEL_ACTIVATE ="Activate";
		
		
		// DR place holders
		String DR_ITEM_INVC_ITEM_PRC="Items.InvcItemPrc";
		String DR_ITEM_EXTPRC="Items.ExtPrc";
		String DR_ITEM_DOCI_TEM_DISC_AMT="Items.DocItemDiscAmt";
		String DR_ITEM_DOC_ITEM_DISC="Items.DocItemDisc";
		String DR_ITEM_TAX="Items.Tax";
		String DR_RECEIPT_AMOUNT ="#Receipt.Amount#";
		String DR_RECEIPT_FOOTER ="#Receipt.Footer#";
		String DR_RECEIPT_TOTALSAVING ="#Receipt.TotalSavings#";
		String APP_DIRECTORY="appDirectory";
		String DR_DOCSID ="DocSID";
		String DR_ITEM_DOC_ITEM_ORG_PRC="Items.DocItemOrigPrc";
		String DR_ITEM_DOC_ITEM_EXT_ORG_PRC="Items.ExtOrigPrc";
		
		
		String DR_RECEIPT_OBJ ="Receipt";
		String ALERT_FROM_EMAILID = "AlertFromEmailId";
		String ALERT_TO_EMAILID = "AlertToEmailId";
		
		// Added after 2.3.9
		 String ALERTS_SENDING_MANUALLY="M";
		 String ALERTS_SENDING_AUTOMATICALLY="A";
		 String OPTSYNC_OBJ="OptSyncOBj";
		 String CONT_STATUS_REPORT_AS_SPAM="Reported as Spam";
		 //Added in 2.3.11
		 String SUPP_TYPE_SPAMMED = "Spammed";
		 
		 //Added for 2.3.12
		 String VMTA_SENDGRIDAPI ="SendGridAPI";
		 String SMTP_HOST_NAME = "SMTPHost";
		 String SMTP_HOST_PORT = "SMTPPort";

		 String SMTP_KEY_PROTOCOL = "mail.transport.protocol";
		 String SMTP_KEY_HOST = "mail.smtp.host";
		 String SMTP_KEY_PORT = "mail.smtp.port";
		 String SMTP_KEY_AUTH = "mail.smtp.auth";
		 
		 
		 String SENDING_TYPE_SINGLE = "AutoEmail";//PropertyUtil.getPropertyValueFromDB("Single");
		 String SENDING_TYPE_BULK = "CampaignSchedule";//PropertyUtil.getPropertyValueFromDB("Bulk");
		 String VMTA_AMAZONSES ="AmazonSES";

		 String SMTP_VALUE_PROTOCOL = "smtp";
		 String SMTP_VALUE_HOST = PropertyUtil.getPropertyValueFromDB(SMTP_HOST_NAME);
		 String SMTP_VALUE_PORT = PropertyUtil.getPropertyValueFromDB(SMTP_HOST_PORT);
		 String SMTP_VALUE_AUTH = "true";
		 
		 public static String emailCampaignDbFields[] = {"domain","first_name","mobile_status ",
			 "subscription_type","external_id","udf10","udf11","udf12","udf13","udf14","udf15",
			 "home_store","zip","mobile_phone","udf1","udf2","udf3","udf4","udf5","udf6","udf7","udf8","udf9"};

		 //SMS Queue 2.4.5
		 String SMS_MSG_TYPE_TEST = "Test SMS";
		 String SMS_MSG_TYPE_OPTIN = "OPTIN SMS";
		 String SMS_MSG_TYPE_EXPORT = "Export SMS";
		 String SMS_MSG_TYPE_AUTOSMS = "Auto SMS";
		 String SMS_MSG_TYPE_ALERTSMS = "Alert SMS";
		 String SMS_MSG_TYPE_OTPSMS = "OTP SMS";
		 String SMS_MSG_TYPE_DRSMS = "DR SMS";
		 String PURGE_STATUS_COMPLETED = "Complete";
		 String PURGE_STATUS_INCOMPLETED = "InComplete";
		 public static String smsCampaignDbFields[]={"last_name","domain","first_name","external_id","udf11","udf10","home_store"};
		 String SMS_MSG_TYPE_TEST2 = "Test SMS";
		 String SMS_MSG_TYPE_OPTIN2 = "OPTIN SMS";
		 String SMS_MSG_TYPE_EXPORT2 = "Export SMS";
		 String SMS_MSG_TYPE_AUTOSMS2 = "Auto SMS";
		 String SMS_MSG_TYPE_ALERTSMS2 = "Alert SMS";
		 String SMS_MSG_TYPE_OTPSMS2 = "OTP SMS";
		 String PURGE_STATUS_COMPLETED2 = "Complete";
		 String PURGE_STATUS_INCOMPLETED2 = "InComplete";
		 
		 String BEE_CLIENT_KEYVALUE = "beeClientKeyvalue";
		 String BEE_CLIENT_KEYVALUE_DR = "beeClientKeyvalueDR";
		 
		 String CONFIRM_SUBSCRIPTION_LINK = "|^GEN_confirmSubscriptionLink^|";
		 
		 String ITEM_QUANTITY_ALL = "ALL";
		 String ITEM_QUANTITY_ONE = "1";
		 
		 String HIGHEST_PRICED_ITEM_WITH_DISCOUNT="HPIWD";
		 String HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT="HPIWOD";
		 String ALL_ELIGIBLE_ITEMS="ALLEI";
		 String LOWEST_PRICED_ITEMS_WITH_DISCOUNT="LPIWD";
		 String LOWEST_PRICED_ITEMS_WITH_OUT_DISCOUNT="LPIWOD";
		 
		 String FOOTER_UNSUBSCRIBE_LINK = "|^GEN_unsubscribeLink^|";
		 String FOOTER_UPDATE_PREFERENCE_LINK = "|^GEN_updatePreferenceLink^|";
		 String SENDER_FOOTER_EMAIL_ADDRESS_AUTOEMAIL = "|^GEN_senderEmailAddress^|";
		 
		 String SMS_STATUS_DELIVERED = "Delivered";
		 String SMS_COUNTRY_OMAN ="Oman";
		 
		 String MENUITEM_LOYALTY_MENU_CUSTOMER_LOOKUP_FBB_VIEW = "MenuItem_Loyalty_Menu_Customer_LookUp_Fbb_VIEW";
		 String MENUITEM_LOYALTY_MENU_CUSTOMER_LOOKUP_AND_REDEEM_VIEW = "MenuItem_Loyalty_Menu_Customer_LookUp_And_Redeem_VIEW";
		 String MENUITEM_LOYALTY_MENU_CUSTOMER_LOOKUP_VIEW="MenuItem_Loyalty_Menu_Customer_LookUp_VIEW";
		 String MENUITEM_DASHBOARD_VIEW="MenuItem_Dashboard_VIEW";
		 
		 String DRBC_TOKEN = "DRBC_";
		 String DRBC_DOCSID_TOKEN="DRBC_DOCSID_";
		 
		 String SMS_SENDING_MODE_SMPP = "SMPP";
		 
		 String CNI = "CNI"; // credit note issued
		 
		 }
