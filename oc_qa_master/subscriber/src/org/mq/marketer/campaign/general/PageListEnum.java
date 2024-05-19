package org.mq.marketer.campaign.general;

public enum PageListEnum {
	
	
	//Admin
	ADMIN_BCRM_SETTINGS("admin/BCRMSettings", RightsEnum.MenuItem_AdminBCRMSettings_READ),
	ADMIN_CREATE_USER("admin/createUser", RightsEnum.MenuItem_CreateUser_READ),
	ADMIN_DASHBOARD("admin/dashBoard", RightsEnum.MenuItem_AdminDashboard_READ),
	ADMIN_DIGI_RECEIPT_JSON_CONFIG("admin/digitalReceiptJSONConfig", RightsEnum.MenuItem_DigitalReceiptsSettings_READ),
	ADMIN_EDIT_USER("admin/editUser", RightsEnum.MenuItem_ListUsers_READ),
	ADMIN_EDIT_VMTA("admin/editVmta", RightsEnum.MenuItem_AddEditVmta_READ),
	ADMIN_LATEST_SALES_DETAILS("admin/latestSalesDetails",RightsEnum.MenuItem_LatestSalesDetails_READ),
	ADMIN_LIST_SETTINGS("admin/ListSettings",RightsEnum.MenuItem_ListSettings_READ),
	ADMIN_LIST_USERS("admin/listUsers", RightsEnum.MenuItem_ListUsers_READ),
	ADMIN_MANAGE_USERS("admin/manageUsersAdmin", RightsEnum.MenuItem_ManageUsersAdmin_READ),
	ADMIN_MISSING_SALES_RECEIPTS("admin/missingSalesAndReceipts",RightsEnum.MenuItem_MissingSalesANdReceipts_READ),
	ADMIN_POS_SETTINGS("admin/POSSettings", RightsEnum.MenuItem_AdminPOSSettings_READ),
	ADMIN_MANAGE_RIGHTS("admin/rightsManagement", RightsEnum.MenuItem_ManageRights_READ),
	ADMIN_SPARKBASE_SETTINGS("admin/sparkBaseSettings", RightsEnum.MenuItem_SparkBaseSettings_READ),
	ADMIN_LOYALTY_SETTINGS("admin/loyaltySettings", RightsEnum.MenuItem_LoyaltySettings_READ),
	ADMIN_USER_SMTP("admin/userSMTP",null),
	ADMIN_VIEW_COUPONS("admin/viewCoupons", RightsEnum.MenuItem_Promocodes_READ),
	ADMIN_VIEW_TICKETS("admin/viewTickets", RightsEnum.MenuItem_VIEW_TICKETS_READ),
	ADMIN_SMS_GATEWYS("admin/createSMSGateway", RightsEnum.MenuItem_VIEW_SMS_GATEWYA_READ),
	ADMIN_OPT_SYNC_USER("admin/createOptSyncUser",RightsEnum.MenuItem_OPT_SYNC_USER_READ),
	ADMIN_VIEW_CREATECOUPONS("admin/createCoupon", RightsEnum.MenuItem_CreatePromocodes_READ),
	ADMIN_VIEW_CREATECOUPONSUCCEEDING("admin/createCouponSucceeding", RightsEnum.MenuItem_CreatePromocodesSucceeding_READ),
	ADMIN_VIEW_OFFERNOTIFICATION("admin/offersNotificationWeb", RightsEnum.MenuItem_Offer_Notification_READ),
	
	ADMIN_SOCIAL_MY_CAMPAIGNS("social/mySocialCampaigns",RightsEnum.MenuItem_SOCIAL_READ),
	ADMIN_SOCIAL_CREATE_CAMPAIGNS("social/createCampaign",RightsEnum.MenuItem_SOCIAL_READ),
	ADMIN_SOCIAL_NETWORK_SETTINGS("social/networkSettings",RightsEnum.MenuItem_SOCIAL_READ),
	ADMIN_SOCIAL_GEN_SHORT_CODE("social/generateCustomShortCode",RightsEnum.MenuItem_SOCIAL_READ),
	
	

	//Campaigns 

	CAMPAIGN_CAMPAIGN_LIST("campaign/CampaignList", RightsEnum.MenuItem_MyEmailsCampaigns_READ),
	CAMPAIGN_CREATE("campaign/CampCreateIndex", RightsEnum.MenuItem_CreateEmail_READ),
	CAMPAIGN_FINAL("campaign/CampFinal", RightsEnum.MenuItem_CreateEmail_READ),
	CAMPAIGN_LAYOUT("campaign/CampLayout", RightsEnum.MenuItem_CreateEmail_READ),
	CAMPAIGN_MLIST("campaign/CampMlist", RightsEnum.MenuItem_CreateEmail_READ),
	CAMPAIGN_REPORT("campaign/CampReport", RightsEnum.MenuItem_EmailCampaignReports_READ),
	CAMPAIGN_SETTINGS("campaign/CampSettings", RightsEnum.MenuItem_CreateEmail_READ), 
	CAMPAIGN_TEXT_MESSAGE("campaign/CampTextMsg", RightsEnum.MenuItem_CreateEmail_READ),
	CAMPAIGN_DIGITAL_RECEIPTS("campaign/DigitalReciepts", RightsEnum.MenuItem_DigitalReceipt_READ), 
	CAMPAIGN_DIGITAL_RECEIPT_SETTINGS("campaign/DigitalRecieptSettings", RightsEnum.MenuItem_DigitalReceipt_READ), 
	CAMPAIGN_BLOCK_EDITOR("campaign/Editor",RightsEnum.MenuItem_CreateEmail_READ),
	CAMPAIGN_EVENT_TRIGGER_EMAIL("campaign/EventTriggerEmail",RightsEnum.MenuItem_Event_Triggers_READ),
	CAMPAIGN_LANGUAGE_SMS_IFRAME("campaign/LanguageSMSIframe", null), 
	CAMPAIGN_PLAIN_EDITOR("campaign/plainEditor", RightsEnum.MenuItem_CreateEmail_READ),
	CAMPAIGN_SELECT_TEMPLATE("campaign/selectTemplate", RightsEnum.MenuItem_CreateEmail_READ), 
	CAMPAIGN_SMSCAMPAIGN_LIST("campaign/SMSCampaignList",RightsEnum.MenuItem_MySMSCampaigns_READ),
	CAMPAIGN_SMS_CREATE("campaign/SMSCampCreateIndex", RightsEnum.MenuItem_CreateSMS_READ),
	CAMPAIGN_SMS_REPORT("campaign/SMSCampReports", RightsEnum.MenuItem_SMSCampaignReports_READ),
	
	CAMPAIGN_WA_REPORT("campaign/WACampReports", RightsEnum.MenuItem_SMSCampaignReports_READ),
	CAMPAIGN_SMS_SETTINGS("campaign/SmsCampSettings", RightsEnum.MenuItem_CreateSMS_READ), 
	CAMPAIGN_TRIGGER_PAGE("campaign/triggerPage",RightsEnum.MenuItem_Event_Triggers_READ), 
	CAMPAIGN_HTML_EDITOR("campaign/uploadHTML", RightsEnum.MenuItem_CreateEmail_READ),
	CAMPAIGN_VIEW("campaign/View", RightsEnum.MenuItem_MyEmailsCampaigns_READ), 
	CAMPAIGN_VIEW_SMS_CAMPAIGNS("campaign/ViewSMSCampaigns", RightsEnum.MenuItem_MySMSCampaigns_READ),
	CAMPAIGN_DIGITAL_RECEIPTS_REPORTS("campaign/drReport", RightsEnum.MenuItem_DigitalReceipt_READ),
	CAMPAIGN_VIEW_OFFERNOTIFICATION_LIST("contact/notificationWebList", RightsEnum.MenuItem_Notification_READ),
	NOTIFICATION_WEB("contact/notificationWeb", RightsEnum.MenuItem_Notification_READ),
	NOTIFICATION_WEB_iNDEX("contact/notificationWebIndex",  RightsEnum.MenuItem_Notification_READ),
	
	//APP-4288
	CAMPAIGN_WACAMPAIGN_LIST("campaign/WACampaignList",RightsEnum.MenuItem_MyWACampaigns_READ),


    //beeEditor
	CAMPAIGN_HTML_BEE_EDITOR("campaign/Bee", RightsEnum.MenuItem_CreateEmail_READ),
	
	//EReciepts
	CAMPAIGN_E_RECEIPTS("campaign/ERecieptsLaunchingPage", RightsEnum.MenuItem_E_Receipt_Templates_READ), 
	CAMPAIGN_E_RECEIPT_SETTINGS("campaign/ERecieptsSettings", RightsEnum.MenuItem_E_Receipt_TemplatesSettings_READ),
	CAMPAIGN_E_RECEIPT_BEE_EDITOR("campaign/ERecieptsBee", RightsEnum.MenuItem_E_Receipt_DnD_Editor_READ),
	//TODO change to legacy editor
	CAMPAIGN_E_RECEIPT_LEGACY_EDITOR("campaign/EReceiptsLegacyEditor", RightsEnum.MenuItem_E_Receipt_Legacy_Editor_READ),
	
	//Connect 
	CONNECT_FACEBOOK_CONNECT("connect/facebookConnect", RightsEnum.MenuItem_MySMCampaigns_READ),
	CONNECT_FACEBOOK_CONNECTED("connect/facebookConnected",  RightsEnum.MenuItem_MySMCampaigns_READ),
	CONNECT_STATUS("connect/status",  RightsEnum.MenuItem_MySMCampaigns_READ),
	CONNECT_TWITTER_CONNECT("connect/twitterConnect",  RightsEnum.MenuItem_MySMCampaigns_READ),
	CONNECT_TWITTER_CONNECTED("connect/twitterConnected", RightsEnum.MenuItem_MySMCampaigns_READ),




	//Contact 

	CONTACT_ADDSINGLE("contact/AddSingle", RightsEnum.MenuItem_AddImport_Contacts_READ),
	CONTACT_ADDSINGLE_NEW("contact/AddSingleNew", RightsEnum.MenuItem_AddImport_Contacts_READ),
	CONTACT_UPLOAD_CONSENT("contact/consent",RightsEnum.MenuItem_AddImport_Contacts_READ),
	CONTACT_CONTACT_DETAILS("contact/contactDetails", RightsEnum.MenuItem_Lists_READ),
	CONTACT_CONTACT_VIEW("contact/contacts", RightsEnum.MenuItem_Lists_READ),
//	CONTACT_CUSTOM_MLIST_WELCOME_MSG("contact/customMListWelcomeMsg", RightsEnum.MenuItem_DoubleOptin_READ),
	CONTACT_LIST_EDIT("contact/edit", RightsEnum.MenuItem_Lists_READ),
	CONTACT_EDIT_CONTACT("contact/editContact", RightsEnum.MenuItem_Lists_READ),
	CONTACT_FIELD_SELECTION("contact/FieldSelection", RightsEnum.MenuItem_AddImport_Contacts_READ),
	CONTACT_LIST_SEGMENTATION("contact/ListSegmentation", RightsEnum.MenuItem_CreateEmail_READ),
	CONTACT_MANAGE_AUTO_EMAILS("contact/manageAutoEmails", RightsEnum.MenuItem_ManageAutoEmails_READ),
	CONTACT_MANAGE_AUTO_SMS("contact/manageAutoSms", RightsEnum.MenuItem_ManageAutoSMS_READ),
	CONTACT_MANAGE_BCRM_SEGMENTS("contact/manageBcrmSegments", RightsEnum.MenuItem_CreateBCRMSegemnt_READ),
	CONTACT_MANAGE_CONTACTS("contact/manageContacts", RightsEnum.MenuItem_Suppressed_Contacts_READ),
	CONTACT_MANAGE_SEGMENTS("contact/manageSegments", RightsEnum.MenuItem_CreateSegment_READ),
	CONTACT_CREATE_SEGMENTS("contact/createSegments", RightsEnum.MenuItem_CreateSegment_READ),
	CONTACT_LIST_VIEW("contact/myLists", RightsEnum.MenuItem_Lists_READ),
	CONTACT_Single_View_Of_Contacts_VIEW("contact/singleViewContact", RightsEnum.MenuItem_Single_View_Of_Contacts_READ),
	CONTACT_PARENTAL_CONSENT_MSG("contact/parentalConsentMsg", RightsEnum.MenuItem_ParentalConsentApprovals_READ),
//	CONTACT_PROMOTE_CENTER("contact/promoteCenter",RightsEnum.MenuItem_AdminWebform_READ),
	CONTACT_SCORE("contact/Score", RightsEnum.MenuItem_ScoreSEttings_READ),
//	CONTACT_SUBSCRIPTION_FORM("contact/subscriptionForm", RightsEnum.MenuItem_AdminWebform_READ),
//	CONTACT_SUBSCRIPTION_FORM_SETTINGS("contact/subscriptionFormSettings", RightsEnum.MenuItem_AdminWebform_READ),
	CONTACT_SUPPRESS_CONTACTS("contact/suppressContacts", RightsEnum.MenuItem_Suppressed_Contacts_READ),
	CONTACT_SUPPRESSION_CONTACTS("contact/suppressionContacts", RightsEnum.MenuItem_Suppressed_Contacts_READ),
	CONTACT_UPLOAD("contact/upload", RightsEnum.MenuItem_AddImport_Contacts_READ),
	CONTACT_UPLOAD_CSV_SETTINGS("contact/uploadCSVSettings", RightsEnum.MenuItem_AddImport_Contacts_READ),
	CONTACT_UPLOAD_FILE("contact/uploadFile", RightsEnum.MenuItem_AddImport_Contacts_READ),
	CONTACT_VIEW_HOMESPASSED_SEGMENTS("contact/viewHomesPassedSegments", RightsEnum.MenuItem_ViewBCRMSegments_READ),
	CONTACT_VIEW_SEGMENTED_CONTACTS("contact/viewSegmentedContacts", RightsEnum.MenuItem_ViewContactSegments_READ),
	CONTACT_VIEW_SEGMENTS("contact/viewSegments", RightsEnum.MenuItem_ViewContactSegments_READ),
	CONTACT_WEBFORM("contact/webform", RightsEnum.MenuItem_Webforms_READ),
	CONTACT_FILE_DOWNLOAD("contact/download", RightsEnum.MeanuItem_ViewDownload_READ),
	CONTACT_MANAGE_AUTO_EMAILS_BEE("contact/manageAutoEmailBee", RightsEnum.MenuItem_ManageAutoEmail_BEE_READ),
	AUTOEMAIL_HTML_BEE_EDITOR("campaign/BeeAutoEmail", RightsEnum.MenuItem_ManageAutoEmail_BEE_READ),
	//Custom 
	CUSTOM_MULTI_LINE_MSGBOX("custom/MultiLineMessageBox", null),

	//Gallery 
	GALLERY_FILE_BROWSER("gallery/fileBrowser", RightsEnum.MenuItem_MyImages_READ),
	GALLERY_VIEW("gallery/gallery",  RightsEnum.MenuItem_MyImages_READ),
	GALLERY_IMAGE_LIBRARY("gallery/ImageLibrary",  RightsEnum.MenuItem_MyImages_READ),
	GALLERY_NEW_GALLERY("gallery/newGallery",  RightsEnum.MenuItem_MyImages_READ),
	GALLERY_MY_TEMPALTES("gallery/myTemplates",  RightsEnum.MenuItem_MyEmailTemplates_READ),
    GALLERY_MY_TEMPALTES_LAYOUT("gallery/myTemplatesLayout",  RightsEnum.MenuItem_MyEmailTemplates_READ),
    GALLERY_MY_TEMPALTES_PLAIN_EDITOR("gallery/myTempPlainEditor",  RightsEnum.MenuItem_MyEmailTemplates_READ),
    GALLERY_MY_TEMPALTES_BLOCK_EDITOR("gallery/myTempBlockEditor",  RightsEnum.MenuItem_MyEmailTemplates_READ),
    GALLERY_MY_TEMPALTES_HTML_EDITOR("gallery/myTempHtmlEditor",  RightsEnum.MenuItem_MyEmailTemplates_READ),
    GALLERY_MY_TEMPALTES_BEE_EDITOR("gallery/myTempBeeEditor",  RightsEnum.MenuItem_MyEmailTemplates_READ),
    GALLERY_MY_TEMPALTES_AUTOEMAIL("gallery/autoEmailPlainEditor",  RightsEnum.MenuItem_ManageAutoEmail_plain_READ),
	
	//General
	GENERAL_ACTIVITY_COMPONENT("general/activityComponent", null),
	GENERAL_EVENT_COMPONENT("general/eventComponent", null),
	GENERAL_FEEDBACK("general/feedback", RightsEnum.MenuItem_FEEDBACK_READ),
	GENERAL_PROGRAM_DESIGNER("general/programdesigner", null),
	GENERAL_SWITCH_COMPONENT("general/switchComponent", null),
	

	//Message 

	MESSAGE_INBOX("message/inbox", RightsEnum.MenuItem_Dashboard_READ),
	MESSAGE_VIEW("message/messages", RightsEnum.MenuItem_Dashboard_READ),
	MESSAGE_TRASH("message/trash", RightsEnum.MenuItem_Dashboard_READ),


	// Mqs 
	MQS("mqs", null),

	//My Account 

	MYACCOUNT_MY_SHARINGS("myAccount/mySharings", RightsEnum.MenuItem_PowerShare_READ),
	MYACCOUNT_RECENT_ACTIVITY("myAccount/recentActivity", RightsEnum.MenuItem_RecentActivities_READ),
	MYACCOUNT_USER_DETAILS("myAccount/userDetails", RightsEnum.MenuItem_MyProfile_READ),

	//Program 

	PROGRAM_AUTO_PROGRAM_CUST_ACTIVATED_EVENT("program/AutoProgramCustActivatedEvent", RightsEnum.MenuItem_ProgramBuilder_READ),
	PROGRAM_AUTO_PROGRAM_ELAPSE_TIMER_EVENT("program/AutoProgramElapseTimerEvent", RightsEnum.MenuItem_ProgramBuilder_READ),
	PROGRAM_AUTO_PROGRAM_EMAIL_CAMPAIGN_ACTIVATY("program/AutoProgramEmailCampaignActivity", RightsEnum.MenuItem_ProgramBuilder_READ),
	PROGRAM_AUTO_PROGRAM_SETTINGS("program/AutoProgramSettings", RightsEnum.MenuItem_ProgramBuilder_READ),
	PROGRAM_AUTO_PROGRAM_SMS_CAMPAIGN_ACTIVATY("program/AutoProgramSMSCampaignActivity", RightsEnum.MenuItem_ProgramBuilder_READ),
	PROGRAM_AUTO_PROGRAM_SWITCH_ALLOCATION("program/AutoProgramSwitchAllocation", RightsEnum.MenuItem_ProgramBuilder_READ),
	PROGRAM_AUTO_PROGRAM_SWITCH_DATA("program/AutoProgramSwitchData",RightsEnum.MenuItem_ProgramBuilder_READ),
	PROGRAM_AUTO_PROGRAM_TARGET_TIMER_EVENT("program/AutoProgramTargetTimerEvent", RightsEnum.MenuItem_ProgramBuilder_READ),
	PROGRAM_PROGRAM_ANALYSIS("program/ProgramAnalysis", RightsEnum.MenuItem_ProgramBuilder_READ),

	// Report 

	REPORT_BOUNCE_REPORT("report/bounceReport", RightsEnum.MenuItem_EmailCampaignReports_READ),
	REPORT_CLICKURL("report/clickURL", RightsEnum.MenuItem_EmailCampaignReports_READ),
	REPORT_CLIENTUSAGE("report/clientUsage", RightsEnum.MenuItem_EmailCampaignReports_READ),
	REPORT_COUPON_REPORTS("report/couponReports", RightsEnum.MenuItem_PromocodeReports_READ),
	REPORT_REFERRAL_REPORTS("report/referralReports", RightsEnum.MenuItem_REFERRALReports_READ),
	REPORT_COUPONS("report/coupons", RightsEnum.MenuItem_PromocodeReports_READ),
	REPORT_REDEEMED("report/ReferralRedeemed", RightsEnum.MenuItem_RedemeedReports_READ),
	REPORT_DETAILED_REPORT("report/detailedReport", RightsEnum.MenuItem_EmailCampaignReports_READ),
	REPORT_DETAILED_SMS_REPORTS("report/DetailedSmsCampaignReport", RightsEnum.MenuItem_SMSCampaignReports_READ),
	REPORT_OPENS_CLICKS_OVER_TIME("report/opensClicksOverTime", RightsEnum.MenuItem_EmailCampaignReports_READ),
	REPORT_OPTINTEL_REPORTS("report/optIntelReports", RightsEnum.MenuItem_OptIntelReports_READ),
	REPORT_RECIPIENT_ACTIVITY_REPORT("report/recipientActivityReport", RightsEnum.MenuItem_EmailCampaignReports_READ),
	REPORT_REPORT("report/Report", RightsEnum.MenuItem_EmailCampaignReports_READ),
	REPORT_AUTO_EMAIL_REPORT("report/autoEmailReports", RightsEnum.MenuItem_Auto_EmailCampaignReports_READ),
	REPORT_AUTO_EMAIL_DETAILED_REPORT("report/detailedAutoEmailReport", RightsEnum.MenuItem_Detailed_Auto_EmailCampaignReports_READ),
	REPORT_EVENT_TRIGGER_REPORT("report/etReport", RightsEnum.MenuItem_Event_Triggers_READ),
	REPORT_SMS_BOUNCE_REPORT("report/smsBounceReport",  RightsEnum.MenuItem_SMSCampaignReports_READ),
	REPORT_SMS_CAMPAIGN_REPORTS("report/SMSCampaignReports",  RightsEnum.MenuItem_SMSCampaignReports_READ),
	//whatsapp
	REPORT_WA_CAMPAIGN_REPORTS("report/WACampaignReports",  RightsEnum.MenuItem_WACampaignReports_READ),
	REPORT_DETAILED_WA_REPORTS("report/DetailedWACampaignReport", RightsEnum.MenuItem_WACampaignReports_READ),
	REPORT_WA_SNAPSHOT("report/WASnapShot", RightsEnum.MenuItem_WACampaignReports_READ),
	REPORT_WA_OPENS_CLICKS("report/WAOpensClicks", RightsEnum.MenuItem_WACampaignReports_READ),
	REPORT_WA_RECIPIENT_ACTIVITY_REPORT("report/WARecipientActivityReport", RightsEnum.MenuItem_WACampaignReports_READ),
	REPORT_WA_SUPPRESS_CONTACT("report/WASuppressContact", RightsEnum.MenuItem_WACampaignReports_READ),
	REPORT_WA_UNDELIVERED_REPORT("report/WAUndeliveredReport", RightsEnum.MenuItem_WACampaignReports_READ),
	REPORT_WA_BOUNCE_REPORT("report/WABounceReports",  RightsEnum.MenuItem_WACampaignReports_READ),
	REPORT_WA_ClICK_URL_REPORTS("report/WAClickURL",  RightsEnum.MenuItem_WACampaignReports_READ),
	
	REPORT_SMS_KEYWORD_REPORTS("report/smsKeywordReport",  RightsEnum.MenuItem_SMSKeywordUsageReports_READ),
	REPORT_SMS_OPENS_CLICKS("report/smsOpensClicks", RightsEnum.MenuItem_SMSCampaignReports_READ),
	REPORT_SMS_RECIPIENT_ACTIVITY_REPORT("report/SMSRecipientActivityReport", RightsEnum.MenuItem_SMSCampaignReports_READ),
	REPORT_SMS_SNAPSHOT("report/SMSSnapShot", RightsEnum.MenuItem_SMSCampaignReports_READ),
	REPORT_SMS_SUPPRESS_CONTACT("report/smsSuppressContact", RightsEnum.MenuItem_SMSCampaignReports_READ),
	REPORT_SMS_UNDELIVERED_REPORT("report/SMSUndeliveredReport", RightsEnum.MenuItem_SMSCampaignReports_READ),
	REPORT_SNAPSHOT("report/snapShot", RightsEnum.MenuItem_EmailCampaignReports_READ),
	REPORT_SUPPRESSED_CONTACT_REPORT("report/SuppressedContactsReports", RightsEnum.MenuItem_EmailCampaignReports_READ),
	REPORT_VIEW_LOYALTY_CARDS("report/viewLoyaltyCards", RightsEnum.MenuItem_LoyaltyEnrollmentReports_READ),
	REPORT_SMS_KEYWORD_RESPONSE_REPORTS("report/smsKeywordResponseReport",  RightsEnum.MenuItem_SMSKeywordUsageReports_READ),
	REPORT_SMS_ClICK_URL_REPORTS("report/SMSClickURL",  RightsEnum.MenuItem_SMSCampaignReports_READ),
	REPORT_DIGITAL_RECEIPTS_REPORTS("report/drReport",RightsEnum.MenuItem_Digital_Receipts_Reports_READ),
	REPORT_EVENT_TRIGGER_NEW_REPORTS("report/eventTriggerNewReport",RightsEnum.MenuItem_Event_Trigger_New_Reports_READ),
	REPORT_EVENT_TRIGGER_NEW_ET_REPORTS("report/etNewReport", RightsEnum.MenuItem_Event_Trigger_New_ET_Reports_READ),
	REPORT_E_RECEIPTS_REPORTS("report/eReceiptReport",RightsEnum.MenuItem_E_Receipts_Reports_READ),
	REPORT_AUTO_SMS_REPORT("report/autoSmsReports", RightsEnum.MenuItem_AUTO_SMS_REPORTS_READ),
	REPORT_AUTO_SMS_DETAILED_REPORT("report/detailedAutoSmsReport", RightsEnum.MenuItem_Detailed_Auto_SMS_READ),
	NOTIFICATION_REPORT("report/NotificationReports", RightsEnum.MenuItem_Notification_report_READ),
	REPORT_DETAILED_NOTIFICATION_REPORTS("report/DetailedNotifCampReport", RightsEnum.MenuItem_Notification_report_READ),
	

	//SMS 


	SMS_CREATE_SMS("sms/CreateSMS", RightsEnum.MenuItem_CreateSMS_READ),
	SMS_CAMP_CREATE_iNDEX("sms/SMSCampCreateIndex",  RightsEnum.MenuItem_CreateSMS_READ),
	SMS_CAMP_SETTINGS("sms/SmsCampSettings",  RightsEnum.MenuItem_CreateSMS_READ),
	SMS_CAMP_SETUP("sms/smsCampSetup",  RightsEnum.MenuItem_ManageKeywords_READ),
	SMS_SMS_SETTINGS("sms/SMSSettings",  RightsEnum.MenuItem_SMSSettings_READ),
	SMS_OPTIN_SMS_SETTINGS("sms/optinSMSSettings",  RightsEnum.MenuItem_Optin_SMSSettings_READ),
	
	//whatsapp
	WA_CAMP_CREATE_iNDEX("wa/WACampCreateIndex",  RightsEnum.MenuItem_CreateWA_READ),
	WA_CAMP_SETTINGS("wa/WACampSettings",  RightsEnum.MenuItem_CreateWA_READ),	
	

	//Social 

	SOCIAL_CREATE_CAMPAIGN("social/createCampaign",  RightsEnum.MenuItem_MySMCampaigns_READ),
	SOCIAL_GENERATE_CUSTOM_SHORTCODE("social/generateCustomShortCode",  RightsEnum.MenuItem_MySMCampaigns_READ),
	SOCIAL_MY_SOCIAL_CAMPAIGNS("social/mySocialCampaigns",  RightsEnum.MenuItem_MySMCampaigns_READ),
	SOCIAL_NETWORK_SETTINGS("social/networkSettings", RightsEnum.MenuItem_MySMCampaigns_READ),

	//test
	TEST_REACH("test/reach", null),

	// User Admin

	USERADMIN_MANAGE_USER("useradmin/manageUsers",  RightsEnum.MenuItem_ManageUsers_READ),
	USERADMIN_FAQ("useradmin/faq",  RightsEnum.MenuItem_Faq_READ),
	USERADMIN_TERMS("useradmin/terms",  RightsEnum.MenuItem_Terms_READ),
	USERADMIN_MY_SHARINGS("useradmin/MySharings",  RightsEnum.MenuItem_SuperShare_READ),
	USERADMIN_ORG_STORES("useradmin/organizationStores",  RightsEnum.MenuItem_ManageStores_READ),
	USERADMIN_MANAGE_BILLING_DETAILS("useradmin/billingDetails",  RightsEnum.MenuItem_ManageBilling_READ),
	USERADMIN_SUBSCRIBER_SETTINGS("useradmin/subscriberSettings",  RightsEnum.MenuItem_Subscriber_Settings_READ),
	USERADMIN_VIEW_PREFERENCE_SETTINGS("useradmin/updateViewSubscriptions",  RightsEnum.MenuItem_Subscriber_Settings_READ),
	USERADMIN_OPT_SYNC("useradmin/manageOptSyncUsers",  RightsEnum.MenuItem_OPT_SYNC_READ),
	USERADMIN_FTP_SETTINGS("useradmin/ftpSettings",RightsEnum.MenuItem_FTP_SETTINGS_READ),
	USERADMIN_MANAGE_ZONES("useradmin/zone",  RightsEnum.MenuItem_ManageZones_READ),
	USERADMIN_MANAGE_ITEMS("useradmin/manageItems",  RightsEnum.MenuItem_ManageItems_READ),

	LOYALTY_DASHBOARD("LoyaltyDashboard",  RightsEnum.Tab_Loyalty_READ),
	OPTINTEL_DASHBOARD("OptintelDashboard", RightsEnum.Tab_Optintel_READ),


	RM_HOME("RMHome",  RightsEnum.MenuItem_Dashboard_READ),
	HEADER("header", null),
	UPDATE_PASSWORD("myAccount/userDetails", RightsEnum.MenuItem_MyProfile_READ),
	EMPTY("Empty", RightsEnum.MenuItem_EMPTY_READ),
	UPDATE_SUBSCRIPTION("updateSubscriptions",RightsEnum.MenuItem_UPDATE_SUBSCRIPTION_READ),
	SUPPORT("support",RightsEnum.MenuItem_SUPPORT_READ),
	SUPPORT_VIEW("supportView",RightsEnum.MenuItem_SUPPORT_READ),
	
	LOYALTY_FILES_UPLOAD("loyalty/etlFilesUpload",  RightsEnum.MenuItem_FilesUpload_READ),
	LOYALTY_PROGRAMS_LIST("loyalty/loyaltyProgram",  RightsEnum.MenuItem_My_Loyalty_READ),
	LOYALTY_CREATE_INDEX("loyalty/loyaltyCreateIndex", RightsEnum.MenuItem_Create_Loyalty_READ),
	LOYALTY_CREATE_PROGRAM("loyalty/createLoyaltyProgram",  RightsEnum.MenuItem_Create_Loyalty_READ),
	LOYALTY_CUSTOMER_LOOKUP("loyalty/loyaltyCustomerLookUp", RightsEnum.MenuItem_Loyalty_Customer_LookUp_READ),
	LOYALTY_CUSTOMER_LOOKUP_AND_REDEEM("loyalty/loyaltyCustomerLookUpAndRedeem", RightsEnum.MenuItem_Loyalty_Menu_Customer_LookUp_And_Redeem_READ),
	CUSTOMER_LOOKUP("loyalty/customerLookUp", RightsEnum.MenuItem_Loyalty_Menu_Customer_LookUp_READ),
	CUSTOMER_LOOKUP_FBB("loyalty/loyaltyCustomerLookUpFbb", RightsEnum.MenuItem_Loyalty_Menu_Customer_LookUp_Fbb_READ),
	LOYALTY_TRANSFER_CARD("loyalty/transferCard", RightsEnum.MenuItem_Loyalty_Transfer_Card_READ),
	Lty_MENU_TRANSFER_CARD("loyalty/loyaltyTransferCard", RightsEnum.Menu_Lty_Transfer_Card_READ),
	LOYALTY_DASHBOARD_LOYALTY("loyalty/LoyaltyDashboard", RightsEnum.MenuItem_Loyalty_Dashboard_READ),
	LOYALTY_ADD_CARDS("loyalty/addLoyaltyCards",  RightsEnum.MenuItem_Create_Loyalty_READ),
	LOYALTY_RULES("loyalty/loyaltyRules",  RightsEnum.MenuItem_Create_Loyalty_READ),
	LOYALTY_PERK_RULES("loyalty/loyaltyPerkRules",  RightsEnum.MenuItem_Create_Loyalty_READ),
	LOYALTY_ADDITIONAL_SETTINGS("loyalty/loyaltyAdditionalSettings",  RightsEnum.MenuItem_Create_Loyalty_READ),
	LOYALTY_AUTO_COMMUNICATION("loyalty/loyaltyAutoCommunication",  RightsEnum.MenuItem_Create_Loyalty_READ),
    LOYALTY_PROGRAM_OVERVIEW("loyalty/loyaltyPrgmOverview",  RightsEnum.MenuItem_LoyaltyReports_READ),
    LOYALTY_PROGRAM_DETAILED_REPORT("report/loyaltyDetailedReport",  RightsEnum.MenuItem_LoyaltyReports_READ),
    LOYALTY_PROGRAM_REPORTS("report/ltyProgramReports",  RightsEnum.MenuItem_LoyaltyReports_READ),
    LOYALTY_PROGRAM_FRAUD_ALERT("loyalty/fraudAlertsView",RightsEnum.MenuItem_Loyalty_Fraud_Alert_READ),
	LOYALTY_SPECIAL_REWARDS("loyalty/createSpecialRewards",RightsEnum.MenuItem_Loyalty_Create_Special_Rewards_READ),
	LOYALTY_MY_SPECIAL_REWARDS("loyalty/mySpecialRewards",RightsEnum.MenuItem_Loyalty_My_Special_Rewards_READ),
	LOYALTY_VALUE_CODES("loyalty/valueCodes",RightsEnum.MenuItem_Value_codes),
	LOYALTY_REWARDS_REPORTS("loyalty/rewardsReports",RightsEnum.MenuItem_RewardReports_VIEW),
	LOYALTY_DETAILED_REWARDS_REPORTS("loyalty/detailedRewardsReports",RightsEnum.MenuItem_RewardReports_VIEW),
	LOYALTY_ROI_REPORTS("loyalty/ltyROIReport",RightsEnum.MenuItem_LtyROIReports_VIEW),
	LOYALTY_DETAILED_ROI_REPORTS("loyalty/detailedLtyROIReport",RightsEnum.MenuItem_LtyROIReports_VIEW),
	LOYALTY_LIABILITY_REPORTS("loyalty/liabilityReports",  RightsEnum.MenuItem_LiabilityReports_VIEW),
	LOYALTY_ADD_MEMBER("loyalty/addMember",  RightsEnum.MenuItem_AddMember_VIEW),
	
	LOYALTY_REFERAL_SETTINGS("loyalty/ReferralSettings",  RightsEnum.MenuItem_REFERALSettings_READ),
	LOYALTY_MYREFERAL_PROGRAMS("loyalty/myreferralprograms", RightsEnum.MenuItem_MYREFERALPrograms_READ),
	LOYALTY_REFERRAL_REPORTS("loyalty/referralreports", RightsEnum.MenuItem_REFERRALReports_READ),
	
	
	CREATE_EVENT("ocevents/CreateOCEvent",RightsEnum.MenuItem_Create_Event_READ),
	MANAGE_EVENT("ocevents/ManageOCEvent",RightsEnum.MenuItem_Manage_Event_READ),
	/*
	
	CAMPAIGN_VIEW("campaign/View", RightsEnum.MenuItem_MyEmails_READ ), 
	CAMPAIGN_CAMPAIGN_LIST("campaign/CampaignList", RightsEnum.MenuItem_MyEmails_READ ),
	CAMPAIGN_SMSCAMPAIGN_LIST("campaign/SMSCampaignList", RightsEnum.MenuItem_MySMSCampaigns_READ ),
	CAMPAIGN_CREATE("campaign/CampCreateIndex.zul", RightsEnum.MenuItem_CreateEmail_READ ),
	CAMPAIGN_FINAL("campaign/CampFinal", RightsEnum.MenuItem_CreateEmail_READ ),
	CAMPAIGN_LAYOUT("campaign/CampLayout", RightsEnum.MenuItem_CreateEmail_READ ),
	CAMPAIGN_MLIST("campaign/CampMlist", RightsEnum.MenuItem_CreateEmail_READ ),
	CAMPAIGN_REPORT("campaign/CampReport", null ),
	CAMPAIGN_SETTINGS( "campaign/CampSettings", RightsEnum.MenuItem_CreateEmail_READ ), 
	CAMPAIGN_TEXT_MESSAGE("campaign/CampTextMsg", RightsEnum.MenuItem_CreateEmail_READ ),
	CAMPAIGN_BLOCK_EDITOR("campaign/Editor", RightsEnum.MenuItem_CreateEmail_READ ),
	CAMPAIGN_PLAIN_EDITOR("campaign/plainEditor", RightsEnum.MenuItem_CreateEmail_READ ),
	CAMPAIGN_HTML_EDITOR("campaign/uploadHTML", RightsEnum.MenuItem_CreateEmail_READ ),
	
	CONTACT_ADDSINGLE("contact/AddSingle", RightsEnum.MenuItem_AddImport_Contacts_READ ),
	CONTACT_UPLOAD_CONSENT("contact/consent", null ),
	CONTACT_CONTACT_VIEW("contact/contacts", null ),
	COATACT_LIST_EDIT("contact/edit", null ),
	CONTACT_EDIT_CONTACT("contact/editContact", null ),
	CONTACT_FIELD_SELECTION("contact/FieldSelection", null ),
	CONTACT_LIST_VIEW("contact/myLists", RightsEnum.MenuItem_Lists_READ ),
	CONTACT_UPLOAD("contact/upload", RightsEnum.MenuItem_AddImport_Contacts_READ ),
	CONTACT_UPLOAD_FILE("contact/uploadFile", null ),
	CONTACT_MANAGE_SEGMENTS("contact/manageSegments", RightsEnum.MenuItem_CreateSegment_READ ),
	CONTACT_VIEW_SEGMENTS("contact/viewSegments", RightsEnum.MenuItem_ViewContactSegments_READ ),
	CONTACT_VIEW_HOMESPASSED_SEGMENTS("contact/viewHomesPassedSegments", null ),
	
	GALLERY_VIEW("gallery/gallery", null ),
	GALLERY_IMAGE_LIBRARY("gallery/ImageLibrary", null ),
	
	MESSAGE_INBOX("message/inbox", null ),
	MESSAGE_VIEW("message/messages", null ),
	MESSAGE_TRASH("message/trash", null ),
	
	REPORT_BOUNCE("report/Bounce", null ),
	REPORT_CLICKS("report/Clicks", null ),
	REPORT_DETAILED_REPORT("report/detailedReport", null ),
	REPORT_NOT_OPENS("report/NotOpens", null ),
	REPORT_OPENS("report/Opens", null ),
	REPORT_REPORT("report/Report", null ),
	REPORT_REPORT_RES("report/ReportRes", null ),
	REPORT_UNSUBSCRIBE("report/Unsubscribe", null ),
	
	ADMIN_CREATE_USER("admin/createUser", null ),
	ADMIN_LIST_USERS("admin/listUsers", null ),
	ADMIN_EDIT_USER("admin/editUser", null ),
	ADMIN_EDIT_VMTA("admin/editVmta", null ),
	ADMIN_RECENT_ACTIVITIES("admin/dashBoard", null ),
	ADMIN_MANAGE_RIGHTS("admin/rightsManagement", RightsEnum.MenuItem_ManageRights_READ ),
	
	
	SOCIAL_GENERATE_CUSTOM_SHORTCODE("social/generateCustomShortCode", null ),
	
	RM_HOME("RMHome", RightsEnum.MenuItem_Dashboard_READ ),
	HEADER("header", null ),
	UPDATE_PASSWORD("myAccount/userDetails", null ),
	FEEDBACK("general/feedback", null ),
	MQS("mqs", null )*/
	
	;
	
	
	String pagePath;
	RightsEnum pageRightEnum;
	
	private PageListEnum(String pagePath, RightsEnum pageRightEnum) {
		this.pagePath = pagePath;
		this.pageRightEnum = pageRightEnum;
	}
	
	public String getPagePath() {
		return pagePath;
	}

	public RightsEnum getPageRightEnum() {
		return pageRightEnum;
	}

	public static PageListEnum getEnumByPagePath(String pagePath) {
		
		PageListEnum[] childEnum = PageListEnum.values();
		for (PageListEnum eachEnum : childEnum) {
			if(eachEnum.getPagePath().equals(pagePath)) {
				return eachEnum;
			}//if
		}//for
		return null;
	}
	
}
