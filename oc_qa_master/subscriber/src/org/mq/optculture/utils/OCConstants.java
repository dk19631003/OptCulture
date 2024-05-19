/**
 * 
 */
package org.mq.optculture.utils;

/**
 * @author manjunath.nunna
 *
 */
public interface OCConstants {
	
	public static final String JSON_VALUE				= "jsonVal";
	public static final String JSON_DR_VALUE				= "jsonValue";
	public static final String MAGENTO_BUSINESS_SERVICE = "magentoBusinessServiceImpl";
	public static final String LOYALTY_INQUIRY_BUSINESS_SERVICE = "loyaltyInquiryService";
	public static final String LOYALTY_INQUIRY_OC_BUSINESS_SERVICE = "loyaltyInquiryOCService";
	public static final String LOYALTY_MEMBER_LOGIN_BUSINESS_SERVICE = "loyaltyMemberLoginService";
	public static final String OC_USER_LOGIN_BUSINESS_SERVICE = "ocUserLoginService";
	public static final String GET_PAGE_URL_BUSINESS_SERVICE = "getPageURLService";
	public static final String LOYALTY_MEMBER_LOGOUT_BUSINESS_SERVICE = "loyaltyMemberLogoutService";
	public static final String LOYALTY_ISSUANCE_BUSINESS_SERVICE = "loyaltyIssuanceService";
	public static final String ASYNC_LOYALTY_ISSUANCE_BUSINESS_SERVICE = "AsyncLoyaltyIssuanceService";
	public static final String ASYNC_LOYALTY_TRX_DAO = "asyncLoyaltyTrxDao";
	public static final String SMSGATEWAYSESSIONMONITOR = "sessionMonitor";
	public static final String ASYNC_LOYALTY_TRX_DAO_FOR_DML = "asyncLoyaltyTrxDaoForDML";
	public static final String LOYALTY_ISSUANCE_OC_BUSINESS_SERVICE = "loyaltyIssuanceOCService";
	public static final String LOYALTY_REDEMPTION_BUSINESS_SERVICE = "loyaltyRedemptionService";
	public static final String LOYALTY_REDEMPTION_OC_BUSINESS_SERVICE = "loyaltyRedemptionOCService";
	public static final String LOYALTY_ENROLMENT_BUSINESS_SERVICE = "loyaltyEnrolmentService";
	//public static final String LOYALTY_ENROLMENT_OC_BUSINESS_SERVICE = "loyaltyEnrolmentOCService";//Old
	public static final String LOYALTY_ENROLMENT_OC_BUSINESS_SERVICE = "loyaltyOCEnrolmentService";//New APP-1326
	public static final String LOYALTY_TRANSFER_MEMBERSHIP_BUSINESS_SERVICE = "loyaltyTransferService";
	public static final String LOYALTY_OTP_OC_BUSINESS_SERVICE = "loyaltyOTPOCService";
	public static final String LOYALTY_FETCHDATA_BUSINESS_SERVICE = "loyaltyFetchDataService";
	public static final String LOYALTY_DATA_BUSINESS_SERVICE_IMPL = "loyaltyDataBusinessServiceImpl";
	public static final String PURCHASE_HISTORY_SERVICE = "purchaseHistoryServiceImpl";
	public static final String COUPONS_HISTORY_SERVICE = "couponsHistoryServiceImpl";
	public static final String STORE_INQUIRY_SERVICE = "storeInquiryServiceImpl";
	public static final String WEB_PORTAL_BRANDING_SERVICE = "webPortalBrandingServiceImpl";
	public static final String LTYTRX_HISTORY_SERVICE = "ltyTrxHistoryServiceImpl";
	public static final String LOYALTY_ENROLLMENT_HISTORY_SERVICE_IMPL = "loyaltyEnrollmentHistoryServiceImpl";
	public static final String LOYALTY_DATA_OC_SERVICE_IMPL = "loyaltyDataOCServiceImpl";
	public static final String LOYALTY_RETURN_TRANSACTION_OC_BUSINESS_SERVICE = "loyaltyReturnTransactionOCService";
	public static final String LOYALTY_RETURN_TRANSACTION_SB_BUSINESS_SERVICE = "SBloyaltyReturnService";
	public static final String PUSH_NOTIFICATION_SERVICE = "pushNotificationService";

	
	public static final String EQUENCE_BUSINESS_SERVICE="equenceBusinessServiceImpl";
	public static final String SYNAPSE_BUSINESS_SERVICE="synapseBusinessServiceImpl";
	public static final String DR_TO_LTY_EXTRACTION_SERVICE="drToLtyExtractionService";
	public static final String DR_TO_LTY_EXTRACTION_IMPL="drToLtyExtractionImpl";
	public static final String GATEWAY_BUSINESS_SERVICE = "gatewayBusinessServiceImpl";
	public static final String POS_DR_BUSINESS_SERVICE = "posDRBusinessServiceImpl";
	public static final String SEND_DR_BUSINESS_SERVICE = "sendDRBusinessServiceImpl";
	public static final String PROCESS_DR_BUSINESS_SERVICE = "processDigitalReceiptServiceImpl";
	public static final String RESET_PASSWORD_BUSINESS_SERVICE = "resetPasswordBusinessServiceImpl";
	public static final String DR_REPORT_BUSINESS_SERVICE = "drReportBusinessServiceImpl";
	public static final String SPARKBASE_TOKEN_SERVICE = "sparkBaseTokenServiceImpl";
	public static final String COUPON_CODE_ENQUIRY_BUSINESS_SERVICE = "couponCodeEnquiryServiceImpl";
	public static final String REFERRAL_CODE_ENQUIRY_BUSINESS_SERVICE = "ReferralCodeEnquiryServiceImpl";

	public static final String COUPON_CODE_REDEEMED_BUSINESS_SERVICE = "couponCodeRedeemedServiceImpl";
	public static final String CONTACT_SUBSCRIPTION_BUSINESS_SERVICE = "contactSubscriptionServiceImpl";	
	public static final String GENERAL_MAIL_SENDER_BUSINESS_SERVICE="generalMailSenderServiceImpl";
	public static final String UPDATE_CONTACTS_BUSINESS_SERVICE="updateContactsBusinessServiceImpl";
	public static final String ISSUE_COUPON_BUSINESS_SERVICE="issueCouponServiceImpl";
	public static final String IMPORT_CONTACTS_BUSINESS_SERVICE="importContactsBusinessServiceImpl"; //change 2.5.3.0
	public static final String EVENTS_BUSINESS_SERVICE="EventsServiceBusinessImpl";
	public static final String FAQ_BUSINESS_SERVICE="FaqServiceBusinessImpl";
	public static final String TERMS_BUSINESS_SERVICE="TermsServiceBusinessImpl";
	public static final String UPDATE_SKU_BUSINESS_SERVICE="updateSkuBusinessServiceImpl";
	public static final String REISSUEPERKS_BUSINESS_SERVICE="ReIssuePerksServiceBusinessImpl";
	
	//Nexon APP-4908
	public static final String NEXON_STORE_ENQUIRY_BUSINESS_SERVICE = "NexonStoreEnquiryServiceImpl";
	public static final String NEXON_REST_SERVICE_REQUEST = "NexonStoreEnquiryRequest";
	//public static final String STORE_MAP_DAO = "storeMapDao";
	//public static final String STORE_MAP_DAO_FOR_DML = "storeMapDaoForDML";
	
	public static final String CAMPAIGN_REPORT_BUSINESS_SERVICE = "campaignReportBusinessServiceImpl";
	public static final String GENERIC_UNSUBSCRIBE_BUSINESS_SERVICE = "genericUnsubscribeBusinessServiceImpl";
	public static final String AUTO_EMAIL_REPORT_BUSINESS_SERVICE = "autoEmailReportBusinessServiceImpl";
	String UPDATE_EMAIL_REPORTS = "updateEmailReports";
	
	public static final String OPT_SYNC_UPDATE_SERVICE = "updateOptSyncDataServiceImpl";
	
	
	String MAGENTO_SERVICE_ACTION_PROMO_ENQUIRY_REQUEST= "promoEnquiryRequest";
	String LOYALTY_SERVICE_ACTION_INQUIRY = "loyalty_inquiry";
	String LOYALTY_SERVICE_ACTION_LOGIN = "loyalty_member_login";
	String LOYALTY_SERVICE_ACTION_USER_LOGIN = "loyalty_member_user_login";
	String GET_PAGE_URL_ACTION ="get_page_url";
	String LOYALTY_SERVICE_ACTION_LOGOUT = "loyalty_member_logout";
	String LOYALTY_SERVICE_ACTION_ISSUANCE = "loyalty_issuance";
	String LOYALTY_SERVICE_ACTION_REDEMPTION = "loyalty_redemption";
	String LOYALTY_SERVICE_ACTION_ENROLMENT = "loyalty_enrolment";
	String LOYALTY_SERVICE_ACTION_TRANSFER = "loyalty_transfer";
	String LOYALTY_SERVICE_ACTION_OTP = "loyalty_otp";
	String LOYALTY_XML_ACTION_ENROLMENT = "loyalty_xml_enrolment";
	String LOYALTY_XML_ACTION_ISSUANCE = "loyalty_xml_issuance";
	String LOYALTY_SERVICE_ACTION_FETCHDATA = "loyalty_fetchdata";
	String LOYALTY_SERVICE_ACTION_RETURN = "loyalty_return";
	String DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL = "dr_xml_sendemail";
	String DR_ONLINE_MODE = "online";
	String DR_OFFLINE_MODE = "offline";
	String DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL = "dr_service_sendemail";

	String COUPON_CODE_ENQUIRY_REQUEST="couponCodeEnquiryRequest";
	String REFERRAL_CODE_ENQUIRY_REQUEST="referralCodeEnquiryRequest";
	String COUPON_CODE_REDEEMED_REQUEST="couponCodeRedeemedRequest";
	String GENERAL_MAIL_SENDER_REQUEST="generalMailSenderRequest";
	String UPDATE_CONTACTS_SERVICE_REQUEST="updateContactsServiceRequest";
	String UPDATE_SKU_SERVICE_REQUEST="updateSkuServiceRequest";
	String PURCHASE_HISTORY_SERVICE_REQUEST="purchaseHistoryServiceRequest";
	String COUPONS_HISTORY_SERVICE_REQUEST="couponsHistoryServiceRequest";
	String LTYTRX_HISTORY_SERVICE_REQUEST="LtyTrxHistoryServiceRequest";
	String WEB_PORTAL_BRANDING_SERVICE_REQUEST = "webPortalBrandingServiceRequest";
	
	//changes start 2.5.3.0
	String IMPORT_CONTACTS_SERVICE_REQUEST ="importContactsServiceRequest";
	
	String EVENTS_SERVICE_REQUEST ="EventsServiceRequest";
	String FAQ_SERVICE_REQUEST ="FaqServiceRequest";
	String TERMS_SERVICE_REQUEST ="TermsServiceRequest";
	String REISSUEPERKS_SERVICE_REQUEST ="ReIssuePerksServiceRequest";
	
	String REFERRAL_SERVICE_REQUEST ="ReferralServiceRequest";

	
	String IMPORT_TYPE_LOOKUP="Lookup";
	String IMPORT_TYPE_BULK="Bulk";
	
	String IMPORT_REPORT_TYPE_BASIC ="Basic";
	String IMPORT_REPORT_TYPE_COMPLETE ="Complete";
	
	String IMPORT_CONTACT_TYPE_ALL="All";
	String IMPORT_CONTACT_TYPE_ACTIVE="Active";
	String IMPORT_CONTACT_TYPE_SUPPRESSED="Suppressed";
	String IMPORT_CONTACT_TYPE_SUPPERESSEDEMAIL="SuppressedEmail";
	String IMPORT_CONTACT_TYPE_SUPPERESSEDPHONE="SuppressedPhone";
	//changes end 2.5.3.0
	
	//APP-4288
	String WACampaigns_DAO_FOR_DML="waCampaignsDaoForDML";
	
	
	String DR_REPORT_ACTION_TYPE_OPEN="open";
	String DR_REPORT_ACTION_TYPE_OPEN_SMS="openSMS";
	String DR_REPORT_ACTION_TYPE_CLICK="click";
	String DR_REPORT_ACTION_TYPE_CLICK_SMS="clickSMS";
	String DR_REPORT_ACTION_TYPE_WEBPAGE="webpage";
	String DR_REPORT_ACTION_TYPE_DR_SMS="drsms";
	String DR_REPORT_ACTION_TYPE_WA="wa";
	String DR_REPORT_ACTION_TYPE_BARCODE="barcode";

	String CONTACT_SUBSCRIPTION_REQUEST = "contactSubscriptionRequest";
	String GENERAL_MAIL_SENDER="generalMailSender";
	String GENERAL_MAIL_SENDER_PLACEHOLDER_BODY="[body]";
	
	// dao's
	String CHARACTERCODESDAO = "CharacterCodesDao";
	String COUPONCODES_DAO = "couponCodesDao";
	String REFERRALCODES_DAO = "referralCodesIssuedDao";
	
	String NOTIFICATION_DAO="notificationDao";
	String NOTIFICATION_SCHEDULE_DAO="notificationScheduleDao";
	
	String COUPONCODES_DAOForDML = "couponCodesDaoForDML";
	String COUPONS_DAO = "couponsDao";
	String COUPONS_DAOForDML = "couponsDaoForDML";
	String USERS_DAO = "usersDao";
	String ZONE_TEMPLATE_SETTINGS_DAO ="zoneTemplateSettingsDao";
	String FAQ_DAO = "FAQDao";
	String USERS_DAOForDML = "usersDaoForDML";
	String RESET_PASSWORD_TOKEN_DAO = "resetPasswordTokenDao";
	String RESET_PASSWORD_TOKEN_DAO_FOR_DML = "resetPasswordTokenDaoForDML";
	String COUPON_DICOUNT_GENERATE_DAO = "coupDiscGenDao";
	String COUPON_DICOUNT_GENERATE_DAO_FOR_DML = "coupDiscGenDaoForDML";
	String LOYALTYMEMBERSESSIONID_DAO = "loyaltyMemberSessionIDDao";
	String LOYALTYMEMBERSESSIONID_DAO_FOR_DML = "loyaltyMemberSessionIDDaoForDML";
	String CONTACTS_DAO = "contactsDao";
	String CONTACTS_DAO_FOR_DML = "contactsDaoForDML";
	String APPLICATION_PROPERTIES_DAO="applicationPropertiesDao";
	String APPLICATION_PROPERTIES_DAO_FOR_DML="applicationPropertiesDaoForDML";
	//changes 2.5.3.0 start
	String SMS_SUPPRESSEDCONTACT_DAO = "smsSuppressedContactsDao";  
	String SMS_SUPPRESSEDCONTACT_DAO_FOR_DML = "smsSuppressedContactsDaoForDML";
	//changes 2.5.3.0 end
	String SMS_SUPPRESSED_CONTACTS_DAO = "suppressedContactsDao";
	String SMS_SUPPRESSED_CONTACTS_DAO_FOR_DML = "suppressedContactsDaoForDML";
	String POSMAPPING_DAO = "posMappingDao";
	String SPARKBASE_LOCATIONDETAILS_DAO = "sparkBaseLocationDetailsDao";
	String CONTACTS_LOYALTY_DAO = "contactsLoyaltyDao";
	String CONTACTS_LOYALTY_DAO_FOR_DML = "contactsLoyaltyDaoForDML";
	//SpecialReward changes
	String LOYALTY_BALANCE_DAO = "loyaltyBalanceDao";
	String LOYALTY_BALANCE_DAO_FOR_DML = "loyaltyBalanceDaoForDML";
	String SPARKBASECARD_DAO = "sparkBaseCardDao";
	String SPARKBASECARD_DAO_FOR_DML = "sparkBaseCardDaoForDML";
	String DR_SENT_DAO = "drSentDao";
	String DR_SENT_DAO_ForDML = "drSentDaoForDML";
	String DR_SMS_SENT_DAO = "drSmsSentDao";
	String DR_SMS_SENT_DAO_ForDML = "drSmsSentDaoForDML";
	String DR_SMS_Channel_SENT_DAO = "drSmsChannelSentDao";
	String DR_SMS_Channel_SENT_DAO_For_DML = "drSmsChannelSentDaoForDML";
	String CUSTOMTEMPLATES_DAO = "customTemplatesDao";
	String CUSTOMTEMPLATES_DAO_ForDML = "customTemplatesDaoForDML";
	String AUTO_SMS_DAO = "autoSMSDao";
	String AUTOSMS_URL_DAO_FOR_DML = "autosmsURLDaoForDml";
	String AUTOSMS_SENTURLSHORTCODE_DAO_FOR_DML = "autoSMSSentUrlShortCodeDaoForDML";
	String AUTO_SMS_DAO_FOR_DML = "autoSMSDaoForDML";
	String EMAILQUEUE_DAO = "emailQueueDao";
	String EMAILQUEUE_DAO_ForDML = "emailQueueDaoForDML";
	String EVENTS_DAO = "EventsDao";
	String EVENTS_DAO_ForDML = "EventsDaoForDML";
	//String FAQ_DAO_ForDML = "FAQDaoForDML";
	String SMSSETTINGS_DAO = "smsSettingsDao";
	String MAILINGLIST_DAO = "mailingListDao";
	String MAILINGLIST_DAO_FOR_DML = "mailingListDaoForDML";
	String LOYALTY_TRANSACTION_DAO = "loyaltyTransactionDao";
	String LOYALTY_TRANSACTION_DAO_FOR_DML = "loyaltyTransactionDaoForDML";
	String LOYALTY_PROMOTRXLOG_DAO = "promoTrxLogDao";
	String LOYALTY_PROMOTRXLOG_DAO_FOR_DML = "promoTrxLogDaoForDML";
	String LOYALTY_CARD_FINDER="loyaltyCardFinder";
	String MYTEMPLATES_DAO="myTemplatesDao";
	String CUSTOMERFEEDBACK_DAO="customerFeedbackDao";
	String CUSTOMERFEEDBACK_DAO_FOR_DML="customerFeedbackDaoForDML";
	
	
	String Store_Operator="Store Operator";// APP - 1260
	String PURGELIST = "purgeList";
	String CAPTIWAYTOSMSAPIGATEWAY = "captiwayToSMSApiGateway";
	String DIGITAL_RECEIPTS_JSON_DAO = "digitalReceiptsJSONDao";
	String DIGITAL_RECEIPTS_JSON_DAOForDML = "digitalReceiptsJSONDaoForDML";
	String DIGITAL_RECEIPT_USER_SETTINGS_DAO = "digitalReceiptUserSettingsDao";
	String DIGITAL_RECEIPT_MYTEMPLATES_DAO = "digitalReceiptMyTemplatesDao";
	String DIGITAL_RECEIPT_MYTEMPLATES_DAO_ForDML = "digitalReceiptMyTemplatesDaoForDML";
	String RETAILPRO_SALES_DAO = "retailProSalesDao";
	String ORGANIZATION_STORES_DAO = "organizationStoresDao";
	String TRANSACTIONAL_TEMPLATES_DAO = "transactionalTemplatesDao";
	String TRANSACTIONAL_TEMPLATES_DAO_ForDML = "transactionalTemplatesDaoForDML";
	String FAQ_DAO_ForDML = "FAQDaoForDML";
	String USER_SMS_SENDER_ID_DAO = "userSMSSenderIdDao";
	String ML_CUSTOM_FIELDS_DAO = "mlCustomFieldsDao";
	String URL_SHORTCODE_MAPPING_DAO = "urlShortCodeMappingDao";
	String URL_SHORTCODE_MAPPING_DAO_FOR_DML = "urlShortCodeMappingDaoForDML";
	String AUTO_SMS_QUEUE_DAO = "autoSmsQueueDao";
	String AUTO_SMS_QUEUE_DAO_FOR_DML = "autoSmsQueueDaoForDML";
	String UPDATE_OPTSYNC_DATA_DAO = "updateOptSyncDataDao";
	String UPDATE_OPTSYNC_DATA_DAO_FOR_DML = "updateOptSyncDataDaoForDML";
	String USER_CAMPAIGN_EXPIRATION_DAO = "userCampaignExpirationDao";
	String USER_CAMPAIGN_EXPIRATION_DAO_FOR_DML = "userCampaignExpirationDaoForDML";
	String ORGSMSKEYWORDS_DAO = "orgSMSkeywordsDao";
	String CLICKATELLSMSINBOUND_DAO = "clickaTellSMSInboundDao";
	String CLICKATELLSMSINBOUND_DAO_FOR_DML = "clickaTellSMSInboundDaoForDML";
	String USERSMSGATEWAY_DAO = "userSMSGatewayDao";
	String OCSMSGATEWAY_DAO = "OCSMSGatewayDao";
	String SKU_FILE_DAO = "skuFileDao";
	String SKU_FILE_DAO_FOR_DML = "skuFileDaoForDML";
	String MESSAGES_DAO = "messagesDao";
	String MESSAGES_DAO_FOR_DML = "messagesDaoForDML";
	String FORM_MAPPING_DAO = "formMappingDao";
	String CUSTOM_FIELD_DAO = "cfDataDao";
	String CUSTOM_FIELD_DAO_FORDML = "cfDataDaoForDML";
//	String CONTACT_LOYALITY_DAO = "contactsLoyaltyDao";
	String SPARKBASE_LOC_DAO = "sparkBaseLocationDetailsDao";
	String SPARKBASE_CARD_DAO = "sparkBaseCardDao";
	String SPARKBASE_CARD_DAO_FOR_DML = "sparkBaseCardDaoForDML";
	String SPARKBASE_TRANSACTIONS_DAO = "sparkBaseTransactionsDao";
	String UNSUBSCRIBE_DAO = "unsubscribesDao";
	String UNSUBSCRIBE_DAO_FOR_DML = "unsubscribesDaoForDML";
	String CUSTOM_TEMP_DAO = "customTemplatesDao";
	String CONTACT_PARENTAL_CONSENT_DAO = "contactParentalConsentDao";
	String CONTACT_PARENTAL_CONSENT_DAOForDML = "contactParentalConsentDaoForDML";
	String PURGE_LIST = "purgeList";
	String EVENT_TRIGGER_EVENTS_OBSERVABLE = "eventTriggerEventsObservable";
	String EVENT_TRIGGER_EVENTS_OBSERVER = "eventTriggerEventsObserver";
	String CAPTIWAY_TO_SMS_API_GATEWAY = "captiwayToSMSApiGateway";
	String EVENT_TRIGGER_DAO = "eventTriggerDao";
	String CAMPAIGN_SENT_DAO = "campaignSentDao";
	String CAMPAIGN_SENT_DAO_FOR_DML = "campaignSentDaoForDML";
	String SMS_CAMPAIGN_SENT_DAO = "smsCampaignSentDao";
	String SMS_CAMPAIGN_SENT_DAO_FOR_DML = "smsCampaignSentDaoForDML";
	String CAMPAIGN_REPORT_DAO = "campaignReportDao";
	String SMS_CAMPAIGNREPORT_DAO = "smsCampaignReportDao";
	String UNSUBSCRIBES_DAO = "unsubscribesDao";
	String UNSUBSCRIBES_DAO_FOR_DML = "unsubscribesDaoForDML";
	String SMSBOUNCES_DAO="smsBouncesDao";
	String SMSBOUNCES_DAO_FOR_DML="smsBouncesDaoForDML";
	String SMS_CAMPAIGNREPORT_DAO_FOR_DML="smsCampaignReportDaoForDML";
	
	
	String ORGANIZATION_ZONE_DAO = "zoneDao"; 
	String ORGANIZATION_ZONE_DAO_FOR_DML = "zoneDaoForDML";
	
	String CUSTOMERSALESUPDATEDATA_DAO = "customerSalesUpdateDataDao";
	
	//OptSync DAO
	String UPDATEOPTSYNCDATA_DAO="updateOptSyncDataDao";
	
	//LoyaltyDaos
	String LOYALTY_PROGRAM_TRANS_DAO = "loyaltyProgramTransDao";
	String LOYALTY_PROGRAM_TRANS_DAO_FOR_DML = "loyaltyProgramTransDaoForDML";
	//String LOYALTY_PROGRAM_TIER_DAO = "loyaltyProgramTierDao";
	//String LOYALTY_PROGRAM_DAO = "loyaltyProgramDao";
	String CONTACTS_LOYALTY_STAGE_DAO = "contactsLoyaltyStageDao";
	String CONTACTS_LOYALTY_STAGE_DAO_FOR_DML = "contactsLoyaltyStageDaoForDML";
	String LOYALTY_PROGRAM_DAO = "loyaltyProgramDao";
	String LOYALTY_PROGRAM_DAO_FOR_DML = "loyaltyProgramDaoForDML";
	String LOYALTY_CARD_SET_DAO = "loyaltyCardSetDao";
	String LOYALTY_CARD_SET_DAO_FOR_DML = "loyaltyCardSetDaoForDML";
	String LOYALTY_CARDS_DAO = "loyaltyCardsDao";
	String LOYALTY_CARDS_DAO_FOR_DML = "loyaltyCardsDaoForDML";
	String LOYALTY_PROGRAM_TIER_DAO = "loyaltyProgramTierDao";
	String LOYALTY_PROGRAM_TIER_DAO_FOR_DML = "loyaltyProgramTierDaoForDML";
	String LOYALTY_THRESHOLD_BONUS_DAO = "loyaltyThresholdBonusDao";
	String LOYALTY_THRESHOLD_BONUS_DAO_FOR_DML = "loyaltyThresholdBonusDaoForDML";
	String LOYALTY_PROGRAM_EXCLUSION_DAO = "loyaltyProgramExclusionDao";
	String LOYALTY_PROGRAM_EXCLUSION_DAO_FOR_DML = "loyaltyProgramExclusionDaoForDML";
	String LOYALTY_AUTO_COMM_DAO = "loyaltyAutoCommDao";
	String LOYALTY_AUTO_COMM_DAO_FOR_DML = "loyaltyAutoCommDaoForDML";
	String LOYALTY_TRANSACTION_PARENT_DAO = "loyaltyTransactionParentDao";
	String LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML = "loyaltyTransactionParentDaoForDML";
	String LOYALTY_TRANSACTION_CHILD_DAO = "loyaltyTransactionChildDao";
	String LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML = "loyaltyTransactionChildDaoForDML";
	String LOYALTY_TRANSACTION_EXPIRY_DAO = "loyaltyTransactionExpiryDao";
	String LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML = "loyaltyTransactionExpiryDaoForDML";
	String OTP_GENERATEDCODES_DAO = "OTPGeneratedCodesDao";
	String OTP_GENERATEDCODES_DAO_FOR_DML = "OTPGeneratedCodesDaoForDML";
	String REISSUE_ON_EXPIRY_DAO = "reIssuePerksOnExpiryDao";
	String REISSUE_ON_EXPIRY_DAOForDML = "reIssuePerksOnExpiryDaoForDML";

	String LOYALTY_SETTINGS_DAO = "loyaltySettingsDao";
	String LOYALTY_SETTINGS_DAO_FOR_DML = "loyaltySettingsDaoForDML";
	String LOYALTY_THRESHOLD_ALERTS_DAO = "loyaltyThresholdAlertsDao";
	String LOYALTY_THRESHOLD_ALERTS_DAO_FOR_DML = "loyaltyThresholdAlertsDaoForDML";

	String LTY_SETTINGS_ACTIVITY_LOGS_DAO = "ltySettingsActivityLogsDao";
	String LTY_SETTINGS_ACTIVITY_LOGS_DAO_FOR_DML = "ltySettingsActivityLogsDaoForDML";
	String USER_EMAIL_ALERT_DAO = "userEmailAlertDao";
	String USER_EMAIL_ALERT_DAO_FOR_DML = "userEmailAlertDaoForDML";
	String VALUE_CODES_DAO="valueCodesDao";
	String VALUE_CODES_DAO_FOR_DML="valueCodesDaoForDML";
	String SPECIAL_REWARDS_DAO="specialRewardsDao";
	String REFERRAL_PROGRAM_DAO="referralProgramDao";
	

	String REWARD_REFERRAL_TYPE_DAO="rewardreferralTypeDao";

	
	String REFERRAL_CODES_REDEEMED_DAO="referralCodesRedeemedDao";
	
	String SPECIAL_REWARDS_DAO_FOR_DML="specialRewardsDaoForDML";
String LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO = "loyaltyMemberItemQtyCounterDao";
	String LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO_FOR_DML = "loyaltyMemberItemQtyCounterDaoforDML";
	String REFERRAL_PROGRAM_DAO_FOR_DML="referralProgramDaoForDML";
	
	String REFERRAL_CODES_REDEEMED_DAO_FOR_DML="referralCodesRedeemedDaoForDML";
	
	String REFERRALCODES_DAOFORDML="referralCodesIssuedDaoForDML";
	
	
	String REWARD_REFERRAL_TYPE_DAO_FOR_DML="rewardreferralTypeDaoForDML";

	
	
	// LoyaltyDaos

	
	
	//Issuance thread flag.
	String Issuance_DB_Operations = "issuanceDBOperations";
	String Issuance_DB_Gift_Activated_Operations = "issuanceDBGiftActivatedOperations";
	String Issuance_DB_Gift_Inventory_Operations = "issuanceDBGiftInventoryOperations";
	String Issuance_DB_PERK_Operations = "issuanceDBPerkOperations";
	
	/**
	 * Added for SmsQueueDao
	 */
	String SMS_QUEUE_DAO = "smsQueueDao";
	String SMS_QUEUE_DAO_ForDML = "smsQueueDaoForDML";
	String JSON_RESPONSE_SUCCESS_MESSAGE = "Success";
	String JSON_RESPONSE_FAILURE_MESSAGE = "Failure";
	String JSON_RESPONSE_IGNORED_MESSAGE = "Ignored";
	
	//APP-4288
	String WA_QUEUE_DAO = "waQueueDao";
	String WA_QUEUE_DAO_ForDML = "waQueueDaoForDML";
	
	String LOYALTY_POINTS = "Points";
	String LOYALTY_USD = "USD";
	String LOYALTY_ENROLL_STATUS_SUCCESS = "Success";
	String LOYALTY_ENROLL_STATUS_FAILURE = "Failure";
	String LOYALTY_TRANSACTION_STATUS_NEW = "New";
	String LOYALTY_TRANSACTION_STATUS_PROCESSED = "Processed";
	String LOYALTY_TRANSACTION_STATUS_PAUSED="Paused";
	String LOYALTY_TRANSACTION_STATUS_FAILED = "Failed";
	String LOYALTY_TRANSACTION_STATUS_PURCHASE_IMMEDIATE = "PurchaseImmediate";
	String LOYALTY_TRANSACTION_STATUS_PURCHASE = "Purchase";
	String LOYALTY_TRANSACTION_STATUS_BONUS = "Bonus";
	String LOYALTY_TRANSACTION_STATUS_GIFT_IMMEDIATE = "GiftImmediate";
	
	
	String LOYALTY_TRANSACTION_ENROLMENT = "Enrolment";
	String LOYALTY_TRANSACTION_INQUIRY = "Inquiry";
	String LOYALTY_TRANSACTION_ISSUANCE = "Issuance";
	String LOYALTY_TRANSACTION_REDEMPTION = "Redemption";
	String LOYALTY_TRANSACTION_ENROLENQUIRY = "EnrolEnquiry";
	String LOYALTY_TRANSACTION_EXPIRY = "Expiry";
	String LOYALTY_TRANSACTION_RETURN = "Return";
	String TRANSACTION_ADD_UPDATE_CONTACT = "AddUpdate";
	String LOYALTY_TRANSACTION_OTP = "OTP";
	String LOYALTY_TRANSACTION_ADJUSTMENT = "Adjustment";
	String LOYALTY_TRANSACTION_LOGIN = "login";
	String LOYALTY_TRANSACTION_USER_LOGIN = "UserLogin";
	String LOYALTY_TRANSACTION_EVENTS = "Events";

	//SMS in-bound / miscall optin request parameter names
	
	String REQUEST_PARAM_TYPE = "type";
	String REQUEST_PARAM_TYPE_VALUE_SMS = "SMS";
	String REQUEST_PARAM_TYPE_VALUE_MISSEDCALL = "MissedCall";
	String REQUEST_PARAM_SOURCE = "Source";
	String REQUEST_PARAM_DEST = "Dest";
	String REQUEST_PARAM_MISSEDCALL_STIME = "Stime";
	String REQUEST_PARAM__MSG_STIME = "stime";
	String REQUEST_PARAM__MSG = "msg";//MM/DD/YY HH:MM:SS PM/AM
	
	//Dest Source Stime msg= "type=SMS/MissedCall send dest stime=MM/DD/YY HH:MM:SS PM/AM
	
	String SMS_PROGRAM_KEYWORD_TYPE_OPTIN = "Optin";
	String SMS_PROGRAM_KEYWORD_TYPE_OPTOUT = "OptOut";
	String SMS_PROGRAM_KEYWORD_TYPE_HELP = "Help";
	
	// added for shortern Url
	
	String SHORTURL_TYPE_SHORTCODE = "S";
	String SHORTURL_TYPE_BARCODE_TYPE_MULTIPLE = "BM";
	String SHORTURL_TYPE_BARCODE_TYPE_SINGLE = "BS";
	
	
	String SHORTURL_TYPE_BARCODE_STRING="Barcode as URL";
	
	String AUTO_SMS_OPTIN_MESSAGE = "AutoSMSOptinMessage";
	String SMS_OPTIN_MESSAGE_DYNAMIC_KEYWORD = "[SMS_OPTIN_MESSAGE_DYNAMIC_KEYWORD]";
	String SMS_OPTIN_MESSAGE_DYNAMIC_KEYWORD_RECVING_NUMBER = "[SMS_OPTIN_MESSAGE_DYNAMIC_KEYWORD_RECVING_NUMBER]";
	
	String SMS_OPTIN_MESSAGE_MISSED_CALL_NUMBER = "[SMS_OPTIN_MESSAGE_MISSED_CALL_NUMBER]";
	String FORM_MAPPING_SPLIT_DELIMETER = ";=;";
	
	String EVENT_IMAGES_DELIMETER=";=;"; 

	//Module level error message flags
	String ERROR_PROMO_FLAG = "P";
	String ERROR_CONTACTS_FLAG = "C";
	String ERROR_IMPORT_FLAG = "I"; //changes 2.5.3.0
	String ERROR_SKU_FLAG = "S";
	String ERROR_GENERALMAILSENDER_FLAG = "G";
	String ERROR_LOYALTY_FLAG = "L";
	String ERROR_DR_FLAG = "D";
	String ERROR_MOBILEAPP_FLAG = "M";
	String ERROR_PUSH_NOTIFICATION = "PN";
	String ERROR_EVENTS_FLAG="E";
	String ERROR_FAQ_FLAG="F";
	
	String APPEND_SERVER_TIMEZONE = " EST";
	String DB_SERVER_TIMEZONE = "ServerTimeZoneValue";
	String ERROR_OPTSYNC_FLAG = "OS";
	
	int OPT_SYNC_TIMER_PERIOD = 10;
	int DOWN_ALERT_TIME_SPAN_DAY = 1440;

	String LOYALTY_ONLINE_MODE = "online";
	String LOYALTY_OFFLINE_MODE = "offline";
	String LOYALTY_MODE_MANUAL_BULK_ENROLLMENT = "MBE";


	
	//Added for campaign reports update
	String URL_TYPE_OLD_REQUESTED_ACTION = "requestedAction";
	String URL_TYPE_NEW_REQUESTED_ACTION = "action";
	
	String CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE = "failure";
	
	String RESPONSE_TYPE_OPEN_IMAGE = "openImage";
	String RESPONSE_TYPE_CLICK_REDIRECT = "clickRedirect";
	String RESPONSE_TYPE_WEB_LINK = "webLink";
	String RESPONSE_TYPE_RESUBSCRIBE = "resubscribe";
	String RESPONSE_TYPE_UNSUBSCRIBE_REQ = "unsubscribeReq";
	String RESPONSE_TYPE_UNSUBSCRIBE_UPDATE = "unsubscribeUpdate";
	String RESPONSE_TYPE_FARWARD_UPDATE = "farwardUpdate";
	String RESPONSE_TYPE_UPDATE_SUBSCRPTION = "updateSubscrption";
	String RESPONSE_TYPE_REDIRECT_SHARE = "redirectShare";
	String RESPONSE_TYPE_SHARE_LINK = "shareLink";
	String RESPONSE_TYPE_BARCODE = "barCode";
	
	String UPDATE_REPORT_ACTION_OPEN = "open";
	String UPDATE_REPORT_ACTION_CLICK = "click";
	String UPDATE_REPORT_ACTION_NOTIFICATIONCLICK = "notificationClick";
	String UPDATE_REPORT_ACTION_WEBPAGE = "webpage";
	String UPDATE_REPORT_ACTION_RESUBSCRIBE = "ReSubscribe";
	String UPDATE_REPORT_ACTION_UNSUBSCRIBE = "unsubscribe";
	String UPDATE_REPORT_ACTION_UNSUB_REASON = "unsubReason";
	String UPDATE_REPORT_ACTION_FARWARD = "farward";
	String UPDATE_REPORT_ACTION_UPDATE_SUBSCRPTION = "updateSubscrption";
	String UPDATE_REPORT_ACTION_TWEET_ON_TWITTER = "tweetOnTwitter";
	String UPDATE_REPORT_ACTION_SHARED_ON_FB = "sharedOnFb";
	String UPDATE_REPORT_ACTION_SHARE_LINK = "shareLink";
	String UPDATE_REPORT_ACTION_COUPONCODE = "couponcode";
	
	String REDIRECT_UNSUB_LINK = "/view/unsubscribe.jsp";
	String REDIRECT_GENERIC_UNSUB_PINTO_RANCH_LINK = "/view/unsubscribePintoRanch.jsp";
	String REDIRECT_UPDATE_SUBS_ERROR_LINK = "/view/updateSubsError.jsp";
	String REDIRECT_UPDATE_SUBS_LINK = "/zul/updateSubscriptions.zul";
	
	String Form_HID = "hId";
	String WELCOME_MAIL_TEMPLATE = "welcomeMsgTemplate";
	String ORG_NAME_PLACEHOLDER = "[OrganisationName]";
	String REPLY_TO_MAIL = "[senderReplyToEmailID]";
	String EMAIL_STATUS_ACTIVE = "Active";
	String REFERRAL_STATUS_ACTIVE = "Active";
	String REFERRAL_STATUS_RUNNING = "Running";

	
	String EMAIL_SUBJECT_WELCOME_EMAIL = " Welcome Mail.";
	String EMAIL_SUBJECT_FEEDBACK_EMAIL = " Feedback Mail.";
	String EMAIL_SUBJECT_REQUIRE_PARENTAL_CONSENT = "Require Parental Consent.";
	String PARENTAL_CONSENT_MAIL_TEMPLATE = "parentalConsentMsgtemplate";
	String LOYALTY_MAIL_TEMPLATE = "loyaltyOptinMsgTemplate";
	String RESUBSCRIBE_LINK = "ResubscribeLink";
	String RESUBSCRIPTION_TEMPLATE = "ResubscriptionTemaplate";
	String SUBSCRIBER_MAIL_ID_PH = "[SubscriberEmailID]";
	String URL_PH ="[url]";
	String SENDER_NAME_PH = "[senderName]";
	String RESUBSCRIPTION_MAIL_SUBJECT = "Resubscription link";
	String RESUBSCRIPTION_MAIL_TYPE = "Resubscription";
	String CARD_TYPE_LOYALTY = "loyalty";
	String FORM_MAPPING_NULL_URL = "https://app.optculture.com";
	public static final String PROPS_KEY_OPENTRACKURL = "OpenTrackUrl";
	
	//OptSync Status Constants
	String OPT_SYNC_STATUS_NEW="N";
	String OPT_SYNC_STATUS_ACTIVE="A";
	String OPT_SYNC_STATUS_DEACTIVE ="D";
	
	String OPT_SYNC_PLUGIN_STATUS_ACTIVE="A";
	String OPT_SYNC_PLUGIN_STATUS_INACTIVE="I";
	
	String Opt_SYNC_ENABLE_MOINTORING="Y";
	String Opt_SYNC_DISABLE_MOINTORING="N";
	String ENABLE="enable";
	String DISABLE="disable";
	
	String OPTINL_MAIL_SENDER="optSyncMailSender";
	
	String OPTSYNC_ALERT_MAIL_SUBJECT="OptCulture Alert: OptSync Down (Username:[USERNAME])";
	String OPTSYNC_ALERT_MAIL_SUBJECT_PH ="[USERNAME]";
	
	String OPTSYNC_UP_ALERT_MAIL_SUBJECT="OptCulture Alert: OptSync UP (Username:[USERNAME])";
	
	//new column in user organization
	String STATUS_ACTIVE="A";
	String STATUS_DELETE="D";

	String LOYALTY_TRANS_TYPE_ENROLLMENT = "Enrollment";
	String TRANS_TYPE_PURCHASE_HISTORY = "PurchaseHistory";
	String LOYALTY_TRANS_TYPE_TRANSFER = "Transfer";
	
	String LOYALTY_TRANS_TYPE_ENROLLMENTHISTORY = "EnrollmentHistory";
	String LOYALTY_TRANS_TYPE_ISSUANCE = "Issuance";
	String LOYALTY_TRANS_TYPE_BONUS = "Bonus";
	String LOYALTY_TRANS_TYPE_REDEMPTION = "Redemption";
	String LOYALTY_TRANS_TYPE_ADJUSTMENT = "Adjustment";
	String LOYALTY_TRANS_TYPE_CHANGE_TIER = "Tier adjustment";
	String LOYALTY_TRANS_TYPE_UPGRADE_TIER = "Tier Upgrade";
	String LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD = "Add";
	String LOYALTY_TRANS_TYPE_ADJUSTMENT_SUB = "Sub";
	//String GIFT_TRANS_TYPE_ISSUANCE = "Gift Issuance";
	//String GIFT_TRANS_TYPE_REDEMPTION = "Gift Redemption";
	String LOYALTY_TRANS_TYPE_INQUIRY = "Inquiry";
	String LOYALTY_MEMBER_LOGIN = "Login";
	String LOYALTY_MEMBER_LOGOUT = "Logout";
	String GET_PAGE_URL = "GetPageURL";
	String LOYALTY_TRANS_TYPE_EXPIRY = "Expiry";
	String LOYALTY_TRANS_TYPE_OTP = "OTP";
	String LOYALTY_TRANS_TYPE_RETURN = "Return";
	String TRANS_TYPE_STOREINQUIRY="StoreInquiry";
	String TRANS_TYPE_WEB_PORTAL_BRANDING_REQ="WebPortalBranding";
	
	String LOYALTY_PROGRAM_TRANS_STATUS_NEW = "New";
	String LOYALTY_PROGRAM_TRANS_STATUS_WORKING = "Working";
	String LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED = "Processed";
	String LOYALTY_PROGRAM_TRANS_STATUS_SUSPENDED = "Suspended";
	
	String LOYALTY_SERVICE_TYPE_SB = "SB";
	String LOYALTY_SERVICE_TYPE_OC = "OC";
	String LOYALTY_SERVICE_TYPE_SBTOOC = "SBToOC";
	
	String LOYALTY_PROGRAM_TIER1 = "Tier 1";
	String LOYALTY_PROGRAM_TIER_TYPE_PENDING = "Pending";
	String LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH = "Month";
	String LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR = "Year";
	
	//Loyalty Program
	char FLAG_YES = 'Y';
	char FLAG_NO = 'N';
	String LOYALTY_TYPE_PERKS = "Perks";
	String LOYALTY_TYPE_POINTS = "Points";
	String LOYALTY_TYPE_CURRENCY = "Currency";
	String LOYALTY_TYPE_VOID = "Void";
	String LOYALTY_TYPE_AMOUNT = "Amount";
	String LOYALTY_TYPE_LPV= "LPV";
	String THRESHOLD_TYPE_TIER = "Tier";
	String LOYALTY_TYPE_PERCENTAGE = "Percentage";
	String LOYALTY_TYPE_VALUE = "Value";
	String PERKS_EXP_TYPE_QUARTER = "Quarter";
	String PERKS_EXP_TYPE_HALFYEAR = "Half Year";
	String PERKS_EXP_TYPE_YEAR = "Year";
	String PERKS_EXP_TYPE_MONTH = "Month";
	String LOYALTY_TYPE_DAY = "Day";
	String LOYALTY_TYPE_HOUR = "Hour" ;
	String LOYALTY_TYPE_YEAR = "Year" ;
	String LOYALTY_TYPE_MONTH = "Month" ;
	String LOYALTY_TYPE_PURCHASE = "Purchase";
	String LOYALTY_TYPE_GIFT = "Gift";
	String LOYALTY_TYPE_REWARD = "Reward";
	String REWARD_TYPE_PERK = "Perk";
	String REWARD_TYPE_LOYALTY = "Loyalty";
	String LOYALTY_TYPE_REVERSAL = "Reversal";
	String LOYALTY_TYPE_STORE_CREDIT = "Store Credit";
	String LOYALTY_RETURN_REQUEST_TYPE_INQUIRY = "Inquiry";
	
	String LOYALTY_CARDSET_TYPE_VIRTUAL = "Virtual";
	String LOYALTY_CARDSET_TYPE_PHYSICAL = "Physical";
	String LOYALTY_CARDSET_TYPE_CUSTOM = "Custom";
	String LOYALTY_CARDSET_GEN_TYPE_SEQUENTIAL = "Sequential";
	String LOYALTY_CARDSET_GEN_TYPE_RANDOM = "Random";
	String LOYALTY_CARDSET_STATUS_ACTIVE = "Active";
	String LOYALTY_CARDSET_STATUS_SUSPENDED = "Suspended";
	String LOYALTY_PROGRAM_STATUS_ACTIVE = "Active";
	String LOYALTY_PROGRAM_STATUS_SUSPENDED = "Suspended";
	
	String LOYALTY_PROGRAM_STATUS_DRAFT = "Draft";
	
	String EVENT_STATUS_ACTIVE = "Active";
	String EVENT_STATUS_SUSPENDED = "Suspended";
	String EVENT_STATUS_RUNNING="Running";
	
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
	String LOYALTY_REG_REQUISITE_STREET = "street";
	
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
	String LOYALTY_CUMULATIVE_POINTS = "CumulativePoints";
	String LOYALTY_CUMULATIVE_VISITS = "CumulativeVisits";
	String LOYALTY_SINGLE_PURCHASE_VALUE = "SinglePurchaseValue";
	String LOYALTY_REWARD_TIER_UPGRADE = "RewardTierUpgrade";
	String LOYALTY_DRAFT_STATUS_COMPLETE = "Complete";
	String LOYALTY_DRAFT_STATUS_INCOMPLETE = "Incomplete";
	
	String LOYALTY_PROMO_EXCLUSION_ALL = "ALL";
	
	String CONTACTS_LOYALTY_STATUS_ACTIVE = "Active";
	String CONTACTS_LOYALTY_STATUS_EXPIRED = "Expired";
	
	
	String LOYALTY_MEMBERSHIP_STATUS_ACTIVE = "Active";
	String LOYALTY_MEMBERSHIP_STATUS_EXPIRED = "Expired";
	String LOYALTY_MEMBERSHIP_STATUS_SUSPENDED = "Suspended";
	String LOYALTY_MEMBERSHIP_STATUS_CLOSED = "Closed";//user for transfer
	String LOYALTY_MEMBERSHIP_STATUS_TRANSFERING = "Transfering";
	
	String LOYALTY_MEMBERSHIP_TYPE_CARD = "Card";
	String LOYALTY_MEMBERSHIP_TYPE_MOBILE = "Mobile";
	String LOYALTY_MEMBERSHIP_TYPE_DYNAMIC = "Dynamic"; // Card Number given by user at the time of enrollment
	//String LOYALTY_MEMBERSHIP_TYPE_PERK = "Perk";
	
	String LOYALTY_CARD_STATUS_ACTIVATED = "Activated";
	//String LOYALTY_CARD_STATUS_EXPIRED = "Expired";
	String LOYALTY_CARD_STATUS_ENROLLED = "Enrolled";
	String LOYALTY_CARD_STATUS_INVENTORY = "Inventory";
	String LOYALTY_CARD_STATUS_INVENTORY_TRANSFERED = "Inventory/Transfered";//intermediate status to avoid 2 concurrent request b/w enrollment & transfer
	String LOYALTY_CARD_STATUS_SELECTED = "Selected";
	
	String LOYALTY_CONVERSION_TYPE_AUTO = "Auto";
	String LOYALTY_CONVERSION_TYPE_ONDEMAND = "Ondemand";
	
	// start entered amount types in transactionchild table
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PERKS = "Perks";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT = "Gift";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE = "Purchase";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD = "Reward";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL_UI = "Issuance Reversal";//APP-2081
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL_UI = "Redemption Reversal";
	//String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_BONUS = "Bonus";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM = "PointsRedeem";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM = "AmountRedeem";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSADJUSTMENT = "PointsAdjustment";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTADJUSTMENT = "AmountAdjustment";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTS_EXP = "PointsExp";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNT_EXP = "AmountExp";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL = "IssuanceReversal";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD_REVERSAL = "RewardReversal";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL = "RedemptionReversal";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT = "StoreCredit";
	String LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_GIFT = "Gift";
	String LOYALTY_MEMBERSHIP_REWARD_FLAG_G = "G"; //FOR GIFT CARD
	String LOYALTY_MEMBERSHIP_REWARD_FLAG_L = "L"; //FOR LOYALTY CARD
	String LOYALTY_MEMBERSHIP_REWARD_FLAG_GL = "GL"; //FOR GIFT TO LOYALTY
	
	String LOYALTY_EXPIRY_TYPE_POINTS = "Points";
	String LOYALTY_EXPIRY_TYPE_AMOUNT = "Amount";
	String LOYALTY_EXPIRY_TYPE_GIFT = "Gift";
	
	String LOYALTY_TRANSACTION_SOURCE_TYPE_STORE = "Store";
	String LOYALTY_TRANSACTION_SOURCE_TYPE_AUTO = "Auto";
	String LOYALTY_TRANSACTION_SOURCE_TYPE_MANUAL = "Manual";
	String LOYALTY_TRANSACTION_SOURCE_TYPE_WEBFORM = "Webform";
	String LOYALTY_TRANSACTION_SOURCE_TYPE_E_COMM = "eComm";
	String LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP = "LoyaltyApp";
	String LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP = "Mobile_App";
	String LOYALTY_TRANSACTION_SOURCE_TYPE_DR_EXTRACTION = "DR";
	
	
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
	String LOYALTY_ENROLLHISTORY_STORE_ALL = "All";
	
	//end entered amount types in transactionchild table
	String OTP_GENERATED_CODE_STATUS_ACTIVE = "Active";
	String OTP_GENERATED_CODE_STATUS_EXPIRED = "Expired";
	String OTP_GENERATED_CODE_STATUS_USED = "Used";

	//OTP RequestTypes
	String OTP_REQUSET_TYPE_ISSUE="Issue";
	String OTP_REQUSET_TYPE_ACKNOWLEDGE="Acknowledge";
	String OTP_REQUSET_TYPE_VALIDATE="Validate";
	String OTP_REQUSET_TYPE_INQUIRY="Inquiry";
	
	String LOYALTY_MEMBERSHIP_MOBILE_VALID = "Valid";
	String LOYALTY_MEMBERSHIP_MOBILE_INVALID = "Invalid";
	
	
	String HTML_VALIDATION = "Currently, your email's content is around \n [size] kbs which is larger than the recommended \n size of 100kbs per the email-sending best \n practices. "
			+ "To avoid email landing in recipient's \n spam folder, please redesign your content to \n reduce size.\n Note: Images don't contribute to email's size.";
	
	
	
	//Added for auto-sms
	
	String ASQ_STATUS_ACTIVE = "Active";
	String ASQ_STATUS_SENT   = "Sent";
	String ASQ_STATUS_FAILURE   = "Failure";
	
	String ASQ_STATUS_NEW = "New";
	String ASQ_STATUS_PROCESSED = "Processed";
	//String ASQ_STATUS_FAILURE = "Failure";
	
	String TRANSACTIONAL_TEMPLATE_TYPE_AS = "AS";
	String AUTO_SMS_MESSAGE_TYPE_TW = "TW";
	String AUTO_SMS_MESSAGE_TYPE_TR = "TR";

	String AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REGISTRATION= "Loyalty Enrollment";
	String AUTO_SMS_TEMPLATE_TYPE_TIER_UPGRADATION="Tier Upgradation";
	String AUTO_SMS_TEMPLATE_TYPE_EARNED_BONUS="Earning Bonus";
	String AUTO_SMS_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION="Reward Expiration";
	String AUTO_SMS_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION = "Membership Expiration";
	String AUTO_SMS_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION = "Gift Amount Expiration";
	String AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION="Gift-Card Expiration";
	String AUTO_SMS_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE="Gift-Card Issuance";

	String AUTO_SMS_TEMPLATE_STATUS_APPROVED = "A";
	String AUTO_SMS_TEMPLATE_STATUS_PENDING = "P";
	
	//Auto-SMS Queue Types
	String ASQ_TYPE_LOYALTY_DETAILS = "LoyaltyDetails";
	String ASQ_STATUS_PROCESSING   = "Processing";
 	String ASQ_TYPE_LOYALTY_GIFT_CARD_ISSUANCE = "LoyaltyGiftCardIssuance";
 	String ASQ_TYPE_LOYALTY_SPECIAL_REWWARD = "LoyaltySpecialReward";//added for specialRewards
 	String ASQ_TYPE_LOYALTY_TIER_UPGRADATION = "LoyaltyTierUpgradation";
 	String ASQ_TYPE_LOYALTY_EARNING_BONUS = "LoyaltyEarningBonus";
 	String ASQ_TYPE_LOYALTY_REWARD_EXPIRY = "LoyaltyRewardExpiry";
 	String ASQ_TYPE_LOYALTY_MEMBERSHIP_EXPIRY = "LoyaltyMembershipExpiry";
 	String ASQ_TYPE_LOYALTY_GIFT_CARD_EXPIRY = "LoyaltyGiftCardExpiry";
 	String ASQ_TYPE_LOYALTY_GIFT_AMOUNT_EXPIRY = "LoyaltyGiftAmountExpiry";
 	String ASQ_TYPE_LOYALTY_TRANSFER_MEMBERSHIP = "LoyaltyTransferMembership";
 	String ASQ_TYPE_LOYALTY_ISSUANCE = "loyaltyIssuance";
 	String ASQ_TYPE_LOYALTY_REDEMPTION = "loyaltyRedemption";
 	String ASQ_TYPE_OTP_MESSAGE = "OTPMESSAGE";

	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_REGISTRATION = "loyaltyOptinMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTISSUE = "giftCardIssMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_ADJUSTMENT = "adjustmentAutoEmail";
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_ISSUANCE = "issuanceAutoEmail";
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_REDEMPTION = "redemptionAutoEmail";
	//otpnew
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_OTPMESSAGE = "otpmessageAutoEmail";
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_REDEMPTIONOTP = "redemptionotpAutoEmail";
	
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_TIERUPGRADE = "tierUpgradationMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_BONUS = "earnedThresholdBonusMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_REWARDAMTEXPIRY = "rewardExpMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTAMTEXPIRY = "giftAmountExpMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_LOYALTYMEMBSHIPEXPIRY = "memExpMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_GIFTMEMBSHIPEXPIRY = "giftCardExpMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_TRANSFER = "transferMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_EMAIL_TRANSFER_INVENTORY = "transferInventoryMsgTemplate";
	
	String AUTO_EMAIL_TEMPLATE_TYPE_SPECIAL_REWARDS="specialRewards";

	String AUTO_SMS_TEMPLATE_TYPE_WEB_FORMS="FeedBack Form";
	String AUTO_SMS_TEMPLATE_TYPE_SPECIAL_REWARDS="Special Rewards";
	String AUTO_SMS_TEMPLATE_TYPE_WELCOME_SMS="Welcome SMS";
	String AUTO_SMS_TEMPLATE_TYPE_LOYALTY_ADJUSTMENT="Loyalty Adjustment";
	String AUTO_SMS_TEMPLATE_TYPE_LOYALTY_ISSUANCE="Loyalty Issuance";
	String AUTO_SMS_TEMPLATE_TYPE_LOYALTY_REDEMPTION="Loyalty Redemption";
	String AUTO_SMS_TEMPLATE_TYPE_OTP_MESSAGE="OTP MESSAGE";//CREATED
	String AUTO_SMS_TEMPLATE_TYPE_ERECEIPT_MESSAGE="E receipt Messages";//CREATED
	String AUTO_SMS_TEMPLATE_TYPE_REDEMPTION_OTP="Redemption OTP";//CREATED
	String AUTO_SMS_TEMPLATE_TYPE_ISSUECOUPON_SMS="IssueCoupon SMS";//CREATED

	
	//String LOYALTY_DEFAULT_TEMPLATE_SMS_OTPMESSAGE = "sendOtpMessagesTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_SMS_ADJUSTMENT = "adjustmentAutoSms";
	String LOYALTY_DEFAULT_TEMPLATE_SMS_REGISTRATION = "loyaltyRegSmsMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTISSUE = "giftCrdIssSmsMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_SMS_TIERUPGRADE = "tierUpgrdSmsMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_SMS_BONUS = "earnBonusSmsMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_SMS_REWARDAMTEXPIRY = "rewardExpSmsMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTAMTEXPIRY = "giftAmtExpSmsMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_SMS_LOYALTYMEMBSHIPEXPIRY = "memshpExpSmsMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_SMS_GIFTMEMBSHIPEXPIRY = "giftCrdExpSmsMsgTemplate";
	String LOYALTY_DEFAULT_TEMPLATE_SMS_TRANSFER = "transferSMSMsgTemplate";
	String CAMPAIGN_PH_LOYALTY_POINTS_BALANCE="loyaltyPointsBalance";
	String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_CURRENCY_BALANCE="loyaltyMembershipCurrencyBalance";
	String CAMPAIGN_PH_LOYALTY_LAST_BONUS_VALUE ="loyaltyLastBonusValue";
	String CAMPAIGN_PH_LOYALTY_LAST_THRESHOLD_LEVEL="loyaltyLastThresholdLevel";
	String FEEDBACK_WEBFORM ="feedbackform";
	String SPECIALREWARDS="specialRewards";
	String WELCOMESMS ="welcomeSms";
	String LOYALTYADJUSTMENT ="loyaltyAdjustment";
	String LOYALTYISSUANCE ="loyaltyIssuance";
	String LOYALTYREDEMPTION ="loyaltyRedemption";
	String OTPMESSAGES="OtpMessage";//CREATED
	String ERECEIPTMESSAGES="E-RECEIPTMESSAGE";
	String REDEMPTIONOTP="RedemptionOtp";
	String ISSUECOUPONSMS="IssueCouponSMS";
	//Card generation flag
	String LOYALTY_CARD_GENERATION_FLAG_Y = "Y";
	String LOYALTY_CARD_GENERATION_FLAG_N = "N";
	
	
	
	//SparkBase Transaction type
	
	String SPARKBASE_TRANSACTION_TYPE_LOYALTY_ISSUANCE = "Loyalty Issuance";
	
	
	//OCLOYALTY URL STRING
//	String LOYALTY_LOGIN_URL= "www.getyourbalance.com/";
	
	//One or More Years or Months
	String MORETHANONEOCCURENCE="(s)";
	
	//Loyalty type for web portal
	String LOYALTY_SETTINGS_TYPE_CARD_NUMBER = "Card Number";
	String LOYALTY_SETTINGS_TYPE_MOBILE_NUMBER = "Mobile Number";
	
	//Added for Survey_of_April_2017
	
	String SURVEY_LIST_NAME = "Survey_of_April_2017";
	
	//Added for mailinglist status 
	
	String MAILINGLIST_STATUS_ACTIVE = "Active";
	
	
	//oc loyalty event trigger status
  	
 	 String LOYALTY_TRANSACTION_ET_STATUS_NEW="New";
	 String LOYALTY_TRANSACTION_ET_STATUS_PROCESSED="Processed";
	 String LOYALTY_TRANSACTION_ET_STATUS_UNPROCESSED="Unprocessed";
	 
	 
	 String LOYALTY_ADJUSTMENT = "Loyalty Adjustment";
	 String LOYALTY_ISSUANCE = "Loyalty Issuance";
	 String LOYALTY_ISSUANCE_DIFFERENCE = "Loyalty Issuance Difference";
	 String LOYALTY_RETURN = "Loyalty Return";
	 String LOYALTY_REDEMPTION = "Loyalty Redemption";
	 String LOYALTY_GIFT_ISSUANCE = "Gift Issuance";
	 String LOYALTY_GIFT_REDEMPTION = "Gift Redemption";
	
	String USER_ALERT_CAMPAIGN_EXPIRED_STATUS_ACTIVE = "A";
	String USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SCHEDULE_DELEATED = "SDL";
	String USER_ALERT_CAMPAIGN_EXPIRED_STATUS_CAMPAIGN_DELEATED = "CDL";
	String USER_ALERT_CAMPAIGN_EXPIRED_STATUS_DRAFT = "D";
	String USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SENT = "S";
	String USER_ALERT_CAMPAIGN_EXPIRED_STATUS_EXPIRED = "E";
	
	String USER_ALERT_EMAIL_TYPE_CAMP_EXPIRATION = "CE";
	
	String MSG_CAMP_EXPIRATION = "msgCampExpiration";
	//Email Id length
	public static final int MAX_EMAIL_ID_LENGTH = 50;
	
	//Added 2.3.11 
	public static final String CAMPAIGN_STATUS_ACTIVE = "Active";
	public static final String CAMPAIGN_STATUS_SENT = "Sent";
	public static final String CAMPAIGN_STATUS_DRAFT = "Draft";
	public static final byte SENT_EMAIL_STATUS = 1;
	public static final byte ACTIVE_EMAIL_STATUS = 0;
	public static final byte DRAFT_EMAIL_STATUS=2;
	public static final String FONT_BOLD = "font-weight:bold;";
	
	 public String LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE = "percentage";
	 public String LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_VALUE = "value";
	
	 
	public String LTY_ACTIVITY_LOG_TYPE_PROGRAM = "P";
	public String LTY_ACTIVITY_LOG_TYPE_TIER = "T";
	public String LTY_ACTIVITY_LOG_TYPE_SPEC="S";
	// public String LTY_ACTIVITY_LOG_TYPE_REGISTRATION_BONUS = "RB";
	public String LTY_ACTIVITY_LOG_TYPE_BONUS = "B";
	public String LTY_ACTIVITY_LOG_TYPE_LOYALTY_REWARD_VALIDITY = "LRV";
	public String LTY_ACTIVITY_LOG_TYPE_GIFT_AMOUNT_VALIDITY = "GAV";
	public String LTY_ACTIVITY_LOG_TYPE_LOYALTY_MEMBERSHIP_VALIDITY = "LMV";
	public String LTY_ACTIVITY_LOG_TYPE_GIFT_CARD_VALIDITY = "GCV";
	
	
	String LOYALTY_TYPE_ISSUANCE ="Loyalty Issuance";
	String LOYALTY_TYPE_REDEMPTION ="Loyalty Redemption";
	
	String ASYNC_LOYALTY_SERVICE_ACTION_INQUIRY = "async_loyalty_inquiry";
	public static final String ASYNC_LOYALTY_INQUIRY_BUSINESS_SERVICE = "asyncLoyaltyInquiryService";
	
	public static final String ASYNC_LOYALTY_ENROLMENT_BUSINESS_SERVICE = "asyncLoyaltyEnrolmentService";
	String LOYALTY_ENROLLMENT = "Loyalty Enrollment";
	
	public String LTY_SETTING_REPORT_FRQ_DAY = "D";
	public String LTY_SETTING_REPORT_FRQ_WEEK = "W";
	public String LTY_SETTING_REPORT_FRQ_MONTH ="M";
	public String LTY_SETTING_REPORT_TYPE ="Loyalty Report";
	public String LTY_SETTNG_REPORT_TRIGGER_F = "FM";
	public String LTY_SETTNG_REPORT_TRIGGER_L = "LM";
	
	public static final String LOYALTY_CARD_GENERATION_TYPE_CUSTOM ="Custom";
	public static final String LOYALTY_CARD_GENERATION_TYPE_SYSTEM ="System";
	public static final String LOYALTY_CARD_GENERATION_TYPE_DYNAMIC ="Dynamic";
	
	 String LOYALTY_PROGRAM_TYPE_CARD = "Card";
	 String LOYALTY_PROGRAM_TYPE_MOBILE = "Mobile";
	 String LOYALTY_PROGRAM_TYPE_DYNAMIC = "Dynamic";
	 
	 
	 
	 /*
	  * REMOTE_SERVER_URL
	  * 
	  */
	 
	 public static final String REMOTE_IMAGE_DIRECTORY_IP = "REMOTE_IMAGE_DIRECTORY_IP";
	  public static final String REMOTE_IMAGE_DIRECTORY_PORT = "REMOTE_IMAGE_DIRECTORY_PORT";
	  
	  public static final String REMOTE_IMAGE_DIRECTORY_USER = "REMOTE_IMAGE_DIRECTORY_USER";
	  public static final String REMOTE_IMAGE_DIRECTORY_PASSWORD = "REMOTE_IMAGE_DIRECTORY_PASSWORD";
	  
	 // public static final String TOKEN_UNDERSCORE = "-";
	  public static final String TOKEN_UNDERSCORE = "_";

	  public static final String POSVERSION_V8 = "V8";
	  public static final String POSVERSION_V9 = "V9";
	  public static final String POSVERSION_OTHER = "Other";

	  //Bee client uuid   X-BEE-Uid: user_name(id for BEE for billing)
	  public static final String BEE_UID_KEY= "X-BEE-Uid";
	  
	  
	  String DnD_DIGITAL_RECEIPT_TEMPLATES = "Drag_Drop_DRTEMPLATES";
	  String LEGACY_DIGITAL_RECEIPT_TEMPLATES = "Legacy_DRTEMPLATES";
	  
	  String DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DEFAULT = "My Templates in Drag & Drop Editor";
	  //String DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DRAFT = "Drafts";
	  public static final String DR_LEGACY_EDITOR_FOLDER= "My Templates in Legacy Templates";
	  
	  public static final String DRAG_DROP_EDITOR_DR_TEMPLATES_FOLDER_DRAFT= "Draft";
	  
	  String DRAG_AND_DROP_EDITOR = "beeEditor";
	  String LEGACY_EDITOR = "ckEditor";
	  

		//added for equence
		String REQUEST_PARAM_MSGID="msg_id";
		String REQUEST_PARAM_MRID="mr_id";
		String REQUEST_PARAM_MOBILE_NO="mobile_no";
		String REQUEST_PARAM_MESSAGE="message";
		//String REQUEST_PARAM_SUBMITTED_TIME="msg_sent_dttime"
		String REQUEST_PARAM_DELIVERED_TIME="sms_delv_dttime";
		String REQUEST_PARAM_EQ_STATUS="sms_delv_status";
		String REQUEST_PARAM_EQ_REASON="remarks";
		String REQUEST_PARAM_TYPE_VALUE_DLR = "DLR";
		String REQUEST_PARAM_MSGID_SID = "sid";
		public static final String EQUENCE_HTTPDLR_SERVICE = "equenceHTTPDLRService";
		
		String GENERATE_REPORT_SETTING_DAO = "generateReportSettingDao";
		String GENERATE_REPORT_SETTING_DAO_FOR_DML = "generateReportSettingDaoForDML";
		String FTP_SETTINGS_TYPE_DR = "DR";
		String DR_EXTRACTION_THREADS_KEY = "DRExtractionThreadCount";

		String segmentQryPrefiX = "SELECT DISTINCT c.*<SELECTFIELDS>";
		String CHsegmentQryPrefiX = "SELECT DISTINCT any(c.cid)<SELECTFIELDS>";
		String replacedSegmentQryPrefiX = "SELECT DISTINCT c.*,loyalty.card_number, loyalty.card_pin, loyalty.loyalty_balance, loyalty.giftcard_balance, loyalty.gift_balance,"
				+ " loyalty.holdpoints_balance, loyalty.holdAmount_balance";
		String replaceLtySelectStr = "loyalty.card_number, loyalty.card_pin, loyalty.loyalty_balance, loyalty.giftcard_balance, loyalty.gift_balance,"
				+ " loyalty.holdpoints_balance, loyalty.holdAmount_balance ";
  		String AutoSMSPrefix="A";
  		String DRSMSPrefix="DR";

  		String RE_ISSUE_PERKS_REQUEST_ID = "ReIssuePerks";
  		String DR_LTY_EXTRACTION_REQUEST_ID = "DRToLty";
        String DR_RECEIPT_TYPE_SALE = "Sale";
        String DR_RECEIPT_TYPE_RETURN = "Return";
        String DR_PROMO_DISCOUNT_TYPE_ITEM = "Item";
        String DR_PROMO_DISCOUNT_TYPE_RECEIPT = "Receipt";
        String WEB_PORTAL_BRANDING_REQUEST_ID = "WebPortalBranding";
        String DR_LTY_PROMO_REDEMPTION_REQUEST_ID = "DRToPromoRedeem";
	    String CONTACT_REQUEST_ID = "ContReqID";
		String DR_TO_LTY_EXTRACTION = "DRToLoyalty";
		String DR_SOURCE_TYPE_PRISM = "Prism";
		String DR_SOURCE_TYPE_OPTDR = "OptDR";
		String DR_SOURCE_TYPE_Magento = "Magento";
		String DR_SOURCE_TYPE_WooCommerce = "wooCommerce";
		String DR_SOURCE_TYPE_Shopify = "Shopify";
		String DR_SOURCE_TYPE_HEARTLAND = "HeartLand";
		String DR_SOURCE_TYPE_ORION = "Orion";
		
		String ETL_INVALID_EMAIL_TEMPLATE="etl_invalidFile_email_template";
		String ETL_INVALID_RECEIPTS_EMAIL_TEMPLATE="etl_invalidReceipts_email_template";
		String ETL_MAIL_TIMESTAMP="[timestamp]";
		String ETL_MAIL_FILE_NAME="[filename]";
		String ETL_MAIL_USER_NAME="[username]";
		String ETL_MAIL_ORG_ID="[orgid]";
		String ETL_MAIL_FILE_LOCATION="[filelocation]";
		String ETL_MAIL_COMMENTS="[comments]";
		String ETL_TOTAL_RECEIPTS="[totalReceipts]";
		String ETL_FAILED_RECEIPTS="[failedReceipts]";
		String ETL_PROCESSED_RECEIPTS="[processedReceipts]";
		String ETL_DEFAULT_COMMENTS = "Processed All Receipts";
		String ETL_ITEMS_NOT_FOUND = "Items information not found for these receipts:";
		String ETL_INVALID_MOBILE_NUMBER = "invalid mobile number found for these receipts:";
		String ETL_INVALID_DATE = "invalid date found for these receipts:";
		
		/* for CSV Files upload */
		String Ambica_INVALID_EMAIL_TEMPLATE="ambica_invalidFile_email_template";
		String Ambica_INVALID_RECEIPTS_EMAIL_TEMPLATE="ambica_invalidReceipts_email_template";
		String Ambica_MAIL_TIMESTAMP="[timestamp]";
		String Ambica_MAIL_FILE_NAME="[filename]";
		String Ambica_MAIL_USER_NAME="[username]";
		String Ambica_MAIL_ORG_ID="[orgid]";
		String Ambica_MAIL_FILE_LOCATION="[filelocation]";
		String Ambica_MAIL_COMMENTS="[comments]";
		String Ambica_TOTAL_RECEIPTS="[totalReceipts]";
		String Ambica_FAILED_RECEIPTS="[failedReceipts]";
		String Ambica_PROCESSED_RECEIPTS="[processedReceipts]";
		String Ambica_DEFAULT_COMMENTS = "Processed All Receipts";
		String Ambica_ITEMS_NOT_FOUND = "Items information not found for these receipts:";
		String Ambica_INVALID_MOBILE_NUMBER = "invalid mobile number found for these receipts:";
		String Ambica_INVALID_DATE = "invalid date found for these receipts:";
		
		String Bonus_Earn_Type_Amount = "Amount";
		String Bonus_Earn_Type_Points = "Points";
		
		String DUMMY_STORE_NUMBER = "Web Redemption";
		String Mail_To_Support = "MailToSupport";
		String DRToLtyFailureSubject = "DRToLtyFailureSubject";
		String DRToLtyFailureMessage = "DRToLtyFailureMessage";
		String DRToPromoRedeemFailureSubject = "DRToPromoRedeemFailureSubject";
		String DRToPromoRedeemFailureMessage = "DRToPromoRedeemFailureMessage";
		String TRANSACTION_TYPE="<TRANSACTION>";
		String DOCSID="<DOCSID>";
		String DOC_DATE = "<DOC_DATE>";
		String USER_ORGID= "[orgid]";
		String USERNAME = "<USERNAME>";
		String USER_FIRST_NAME_PH="[fname]";
		String REASON="<REASON>";
		String COUPON="<Coupon>";
		String DECISION_FLAG_YES = "Y";
		String DECISION_FLAG_NO = "N";
		String LOYALTY_TRANSACTION_RETURN_INQUIRY = "ReturnInquiry";
		
		String POS_MAPPING_POS_ATTRIBUTE_SKU = "SKU";
		String POS_MAPPING_POS_ATTRIBUTE_UDF = "udf";
		String POS_MAPPING_POS_ATTRIBUTE_SUBSIDIARY_NUMBER="subsidiaryNumber";
		String POS_MAPPING_POS_ATTRIBUTE_DESCRIPTION="description";
		
		//Coupon History Merge tags
		String CAMPAIGN_PH_STARTSWITH_STORE = "store";
		String CAMPAIGN_PH_STARTSWITH_LOYALTY = "loyalty";
		String CAMPAIGN_PH_STARTSWITH_LASETPURCHASE = "lastPurchase";
	 	String CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE = "contactlastpurchasedstore";
	 	String CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE="contacthomestore";
	 	String CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE_ADDRESS="contactHomestoreAddress";
		
		String CAMPAIGN_PH_STORENAME = "storeName";
		String CAMPAIGN_PH_STOREMANAGER = "storeManager";
		String CAMPAIGN_PH_STOREEMAIL = "storeEmail";
		String CAMPAIGN_PH_STOREPHONE = "storePhone";
		String CAMPAIGN_PH_STORESTREET = "storeStreet";
		String CAMPAIGN_PH_STORECITY = "storeCity";
		String CAMPAIGN_PH_STORESTATE = "storeState";
		String CAMPAIGN_PH_STOREZIP = "storeZip";
		
		String CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS = "lastPurchaseStoreAddress";
		String CAMPAIGN_PH_LASTPURCHASE_DATE = "lastPurchaseDate";
		String CAMPAIGN_PH_LASTPURCHASE_AMOUNT = "lastPurchaseAmount";
		String CAMPAIGN_PH_UNSUBSCRIBE_LINK = "unsubscribeLink";
		String CAMPAIGN_PH_WEBPAGE_VERSION_LINK = "webPageVersionLink";
		
		 //Old Loyalty Place Holders.
		 String CAMPAIGN_PH_GIFTCARD_BALANCE = "loyaltygiftcardBalance";
		 String CAMPAIGN_PH_LOYALTY_CARDNUMBER = "loyaltyCardNumber";
		 String CAMPAIGN_PH_LOYALTYCARDPIN = "loyaltyCardPin";				//same as SparkBase
		 String CAMPAIGN_PH_LOYALTY_REFRESHEDON ="loyaltyRefreshedOn";			//same as SparkBase

		 //New loyalty place holders
		 //TODO String CAMPAIGN_PH_LOYALTY_URL="loyaltyUrl";								
		 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_NUMBER="loyaltyMembershipNumber";
		 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PIN="loyaltyMembershipPin";
		 String CAMPAIGN_PH_LOYALTY_MEMBER_TIER="loyaltyMemberTier";
		 String CAMPAIGN_PH_LOYALTY_MEMBER_STATUS="loyaltyMemberStatus";
		 String CAMPAIGN_PH_LOYALTY_ENROLLMENT_DATE ="loyaltyEnrollmentDate";
		 String CAMPAIGN_PH_LOYALTY_ENROLLMENT_SOURCE ="loyaltyEnrollmentSource";
		 String CAMPAIGN_PH_LOYALTY_ENROLLMENT_STORE ="loyaltyEnrollmentStore";
		 String CAMPAIGN_PH_LOYALTY_REGISTERED_PHONE ="loyaltyRegisteredPhone";
		 String CAMPAIGN_PH_LOYALTY_GIFT_BALANCE ="loyaltyGiftBalance";
		 String CAMPAIGN_PH_LOYALTY_HOLD_BALANCE  ="loyaltyHoldBalance";
		 String CAMPAIGN_PH_LOYALTY_REWARD_ACTIVATION_PERIOD ="loyaltyRewardActivationPeriod";
		 String CAMPAIGN_PH_LOYALTY_LAST_EARNED_VALUE ="loyaltyLastEarnedValue";
		 String CAMPAIGN_PH_LOYALTY_LAST_REDEEMED_VALUE ="loyaltyLastRedeemedValue";
		 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PASSWORD = "loyaltyMembershipPassword";
		 String CAMPAIGN_PH_LOYALTY_LOGIN_URL ="loyaltyLoginUrl";
		// String CAMPAIGN_PH_ORGANIZATION_NAME ="organizationName";
		 String CAMPAIGN_PH_LOYALTY_REWARD_EXPIRATION_PERIOD ="loyaltyRewardExpirationPeriod";
		 String CAMPAIGN_PH_LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD ="loyaltyGiftAmountExpirationPeriod";
		 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_EXPIRATION_DATE ="loyaltyMembershipExpirationDate";
		 String CAMPAIGN_PH_LOYALTY_GIFT_CARD_EXPIRATION_DATE ="loyaltyGiftCardExpirationDate";
		 String CAMPAIGN_PH_REWARD_EXPIRING_VALUE ="loyaltyRewardExpiringValue";
		 String CAMPAIGN_PH_GIFT_AMOUNT_EXPIRING_VALUE ="loyaltyGiftAmountExpiringValue";
	
		 String SESSION_EDIT_EVENT = "editEvent";
		 String SESSION_EDIT_REFERRAL="editReferral";
		 public String COUPON_EXPIRY_TYPE_DYNAMIC ="D"; 	
		 public String DR_STATUS_IGNORED ="Ignored";
		 
		 //Special reward attributes
		 public String SP_RULE_ATTR_ITEM_FACTOR = "[#ItemFactor#]";
		 public String SP_RULE_ATTR_ACCROSS_MULTIPLE_PURCHASE= "M";
		 
		  public String REPORT_ACTION_TYPE_PDF="pdf";
		  public String REPORT_ACTION_TYPE_PDF_SMS="pdfSMS";
		  public String REPORT_ACTION_TYPE_PDF_WA="pdfWA"; //OPS-391
		  public static final String DR="DR";
		  public static final String campaign="campaign";
		  public static final String PDF="PDF";
		  public static final String HTML="HTML";

		  String DR_RECEIPTID = "receiptId";
		// public static final String DR="DR";
		  public static final String DR_PDF="PDF";
		  public static final String DR_HTML="HTML";
		  public static final String DR_BARCODE="Barcode";
		  
		  String MenuItem_Dashboard_READ="MenuItem_Dashboard_READ"; 
		  String MenuItem_AdminDashboard_READ="MenuItem_AdminDashboard_READ";
		  String MENUITEM_LOYALTY_MENU_STANDARD_STORE_OPERATOR_CUSTOMER_LOOKUP_VIEW = "Menu_Standard_Store_Operator_Customer_Lookup_READ";
		  String MENUITEM_LOYALTY_MENU_STANDARD_STORE_OPERATOR_CUSTOMER_LOOKUP_READ = "Menu_Standard_Store_Operator_Customer_Lookup_VIEW";
		  String LOYALTY_MEMBER_SESSION_STATUS_ACTIVE = "A";
		  String CAMPAIGN_MSG = "This step is completed, perhaps from another tab/browser. \n "				 
				  + "You will now be redirected to the Campaigns page.";
		  String SHORTURL_CODE_PREFIX_DR = "DR"; // added for DR-SMS
		  public static final String SELECTED_EXPIRY_INFO_TYPE_POINTS="Points";
		  public static final String SELECTED_EXPIRY_INFO_TYPE_AMOUNT="Amount";
		  public static final String SELECTED_EXPIRY_INFO_TYPE_REWARD="Reward";
		  //Colors
		  String DISABLED_CHECKBOK_TEXT_COLOR = "#808080"; 
		  String ENABLED_CHECKBOK_TEXT_COLOR  = "#000000";
		  
		  String PROPS_KEY_QECODE_HTML_TEMPLATE = "QRCodeHTMLTemplate";
			String PROPS_KEY_QECODE_TO_URL = "QRCodeToURL";
			String ONE_QR_CODE_HTML = "OneQRCodeHtml";
		String AUTO_COMM_TYPE_SMS = "SMS";
		String AUTO_COMM_TYPE_WA = "WA";
		
		String Gupshup_Endpoint = "https://media.smsgupshup.com/GatewayAPI/rest";
		String ALERT_MESSAGE_PH_USERNAME = "<USERNAME>";
		String CAMPAIGN_NAME = "<CAMPAIGNNAME>";
		String ERROR_STATUS = "<ERROR_STATUS>";
		String SCHEDULED_DATE = "<REQUEST_DATE>";
		String SUBJECT_LINE = "<SUBJECT_LINE>";
		String CONFIGURED_COUNT = "<CONFIGURED_COUNT>";
		String NOT_SUBMITTED_COUNT ="<NOT_SUBMITTED_COUNT>";
		String USER_FNAME= "[fname]";
		String COUP_GENT_CAMPAIGN_TYPE_SINGLE_SMS	= "AutoSMS";
		String COUP_GENT_CAMPAIGN_TYPE_PUSH_NOTIFICATION = "PN";
		String COUP_GENT_CAMPAIGN_TYPE_WHATSAPP = "WA";


		String CAMPAIGN_FAILED_TO_SEND_SUBJECT = "smsCampaignFailedToSendSubject";
		String SMS_CAMPAIGN_FAILED_TO_SEND_CONTENT= "smsCampaignFailedToSendContent";
		String EMAIL_CAMPAIGN_FAILED_TO_SEND_CONTENT = "emailCampaignFailedToSendContent";
		String SMS_CAMPAIGN_NOT_SUBMITTED_CONTENT = "smsCampaignNotSubmittedContent";
		String CAMPAIGN_WITH_NEGATIVE_EMAIL_CREDIT = "campaignWithNegativeEmailCredit";
		String CAMPAIGN_WITH_NEGATIVE_EMAIL_CREDIT_BODY = "campaignWithNegativeEmailCreditBody";
		String NOTIFICATION_CAMPAIGN_FAILED_TO_SEND_CONTENT= "notificationCampaignFailedToSendContent";
		String CAMPAIGN_DRAFT_SCH_ACTIVE_ALERT_SUBJECT = "campaignDraftSchActiveAlertSubject";
		String CAMPAIGN_DRAFT_SCH_ACTIVE_CONTENT = "campaignDraftSchActiveAlertContent";
		String SHORTURL_CODE_PREFIX_COUPONPH = "[CC_";
		String SHORTURL_CODE_PREFIX_U = "U";
		String SHORTURL_CODE_PREFIX_S = "S";
		String SHORTURL_CODE_PREFIX_a = "a";
		String SMS_SENT_STATUS_STATUS_PENDING = "Status Pending";
		String SMS_URL_PATTERN = "SMSShortUrlPattern";
		
		//Gift Cards
		String GIFT_CARDS_DAO = "giftCardsDao";
		String GIFT_PROGRAMS_DAO = "giftProgramsDao";
		String GIFT_CARD_SKUS_DAO = "giftCardSkusDao";
		String GIFT_CARDS_HISTORY_DAO = "giftCardsHistoryDao";
		String GIFT_CARDS_EXPIRY_DAO = "giftCardsExpiryDao";
		String GIFT_CARDS_DAO_FORDML = "giftCardsDaoForDML";
		String GIFT_PROGRAMS_DAO_FORDML = "giftProgramsDaoForDML";
		String GIFT_CARD_SKUS_DAO_FORDML = "giftCardSkusDaoForDML";
		String GIFT_CARDS_HISTORY_DAO_FORDML = "giftCardsHistoryDaoForDML";
		String GIFT_CARDS_EXPIRY_DAO_FORDML = "giftCardsExpiryDaoForDML";
		

}
