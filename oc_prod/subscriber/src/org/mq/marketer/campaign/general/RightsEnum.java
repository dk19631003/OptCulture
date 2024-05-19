package org.mq.marketer.campaign.general;

public enum RightsEnum {
	
	ROLE_ADMIN(11, "OLD", ""),
	ROLE_USER(12, "OLD", ""),
	ROLE_USER_BASIC(13, "OLD", ""),
	ROLE_USER_POWER(14, "OLD", ""),
	ROLE_USER_ADMIN(15, "OLD", ""),
	
	
	//Menu level View
	
	Menu_Contacts_VIEW(101, "Menu", ""),
	Menu_Campaigns_VIEW(102, "Menu", ""),
	Menu_MarketingTools_VIEW(103, "Menu", ""),
	Menu_Reports_VIEW(104, "Menu", ""),
	Menu_Assets_VIEW(105, "MenuItem", ""),
	Menu_MyAccounts_VIEW(106, "Menu", ""),
	Menu_Adminstrator_VIEW(107, "Menu", ""),
	Menu_Loyalty_VIEW(108, "Menu", ""),
	Menu_Events_VIEW(109, "Menu", ""),
	Menu_E_Receipt_VIEW(110,"Menu",""), 

	MenuItem_Dashboard_VIEW(1001, "MenuItem", ""),
	// Tab level view
	
	Tab_Optintel_VIEW(9001, "Tab",""),
	Tab_Loyalty_VIEW(9002, "Tab" ,""),
	
	//Contact 
	
	MenuItem_Lists_VIEW(2001, "MenuItem", ""),
	MenuItem_ViewContactSegments_VIEW(2002, "MenuItem", ""),
	MenuItem_AddImport_Contacts_VIEW(2003, "MenuItem", ""),
	MenuItem_CreateSegment_VIEW(2004, "MenuItem", ""),
	MenuItem_Webforms_VIEW(2005, "MenuItem", ""),
	MenuItem_Suppressed_Contacts_VIEW(2006, "MenuItem", ""),
	MenuItem_ParentalConsentApprovals_VIEW(2007, "MenuItem", ""),
	MenuItem_CreateBCRMSegemnt_VIEW(2008, "MenuItem", ""),
	MenuItem_ViewBCRMSegments_VIEW(2009, "MenuItem", ""),
	MeanuItem_ViewDownload_VIEW(2010,"MenuItem", ""),
	MenuItem_Single_View_Of_Contacts_VIEW(2011,"MenuItem", ""),
	//Campaigns
	
	MenuItem_CreateEmail_VIEW(3001, "MenuItem", ""),
	MenuItem_CreateSMS_VIEW(3002, "MenuItem", ""),
	MenuItem_CreateSMCampaign_VIEW(3003, "MenuItem", ""),
	MenuItem_MyEmailsCampaigns_VIEW(3004, "MenuItem", ""),
	MenuItem_MySMSCampaigns_VIEW(3005, "MenuItem", ""),
	MenuItem_MySMCampaigns_VIEW(3006, "MenuItem", ""),
	//APP-4288
	MenuItem_MyWACampaigns_VIEW(3007, "MenuItem", ""),

	
	
	
	//Marketing Tools
	
	MenuItem_Promocodes_VIEW(4001, "MenuItem", ""),
	MenuItem_DigitalReceipt_VIEW(4002, "MenuItem", ""),
	MenuItem_Event_Triggers_VIEW(4003, "MenuItem", ""),
	MenuItem_ManageKeywords_VIEW(4004, "MenuItem", ""),
	MenuItem_ManageAutoEmails_VIEW(4005, "MenuItem", ""),
	MenuItem_SMSSettings_VIEW(4006, "MenuItem", ""),
	MenuItem_ProgramBuilder_VIEW(4007, "MenuItem", ""),
	MenuItem_Optin_SMSSettings_VIEW(4008, "MenuItem", ""),
	MenuItem_ManageAutoSMS_VIEW(4009, "MenuItem", ""),
	MenuItem_ManageAutoEmail_BEE_VIEW(4010, "MenuItem", ""),
	MenuItem_ManageAutoEmail_plain_VIEW(4011, "MenuItem", ""),
	MenuItem_CreatePromocodes_VIEW(4012, "MenuItem", ""),
	MenuItem_CreatePromocodesSucceeding_VIEW(4013, "MenuItem", ""),
	MenuItem_Notification_VIEW(4014, "MenuItem", ""),
	MenuItem_Offer_Notification_VIEW(4015, "MenuItem", ""),
	
	MenuItem_ReferalSettings_VIEW(4016, "MenuItem", ""),
	MenuItem_REFERALCampaign_VIEW(4017, "MenuItem", ""),

	MenuItem_MYREFERALPrograms_VIEW(4018,"MenuItem",""),
	
	MenuItem_Referalreview_VIEW(4019,"MenuItem",""),
	MenuItem_REFERRALReports_VIEW(4020,"MenuItem",""),
	
	// Reports
	
	MenuItem_EmailCampaignReports_VIEW(5001, "MenuItem", ""),
	MenuItem_SMSCampaignReports_VIEW(5002, "MenuItem", ""),
	MenuItem_SMSKeywordUsageReports_VIEW(5003, "MenuItem", ""),
	MenuItem_PromocodeReports_VIEW(5004, "MenuItem", ""),
	MenuItem_OptIntelReports_VIEW(5005, "MenuItem", ""),
	MenuItem_LoyaltyEnrollmentReports_VIEW(5006, "MenuItem", ""),
	MenuItem_LoyaltyReports_VIEW(5007, "MenuItem", ""),
	MenuItem_Digital_Receipts_Reports_VIEW(5008, "MenuItem", ""),
	MenuItem_Event_Trigger_New_Reports_VIEW(5009, "MenuItem", ""),
	MenuItem_Event_Trigger_New_ET_Reports_VIEW(5010,"MenuItem",""),
	MenuItem_E_Receipts_Reports_VIEW(5011,"MenuItem",""),
	MenuItem_AUTO_SMS_REPORTS_VIEW(5012,"MenuItem",""),
	MenuItem_Notification_report_VIEW(5013,"MenuItem",""),
	MenuItem_RedemeedReports_VIEW(5014,"MenuItem",""),
	//whatsapp
	MenuItem_WACampaignReports_VIEW(5015, "MenuItem", ""),
	MenuItem_DetailedWACampaignReports_VIEW(5016, "MenuItem", ""),
	//Assets
	
	MenuItem_MyImages_VIEW(6001, "MenuItem", ""),
	MenuItem_MyEmailTemplates_VIEW(6002, "MenuItem", ""),
	
	//My Account
	
	MenuItem_MyProfile_VIEW(7001, "MenuItem", ""),
	MenuItem_MySettings_VIEW(7002, "MenuItem", ""),
	//MenuItem_POSSettings_VIEW(7003, "MenuItem", ""),
	MenuItem_ListSettings_VIEW(7004, "MenuItem", ""),
	MenuItem_PowerShare_VIEW(7005, "MenuItem", ""),
	MenuItem_SuperShare_VIEW(7006, "MenuItem", ""),
	MenuItem_ManageUsers_VIEW(7007, "MenuItem", ""),
	MenuItem_ManageStores_VIEW(7008, "MenuItem", ""),
	MenuItem_RecentActivities_VIEW(7009, "MenuItem", ""),
	//MenuItem_BCRMSettings_VIEW(7010, "MenuItem", ""),
	MenuItem_ManageBilling_VIEW(7011, "MenuItem", ""),
	
	MenuItem_Subscriber_Settings_VIEW(7012, "MenuItem", ""),
	MenuItem_OPT_SYNC_VIEW(7013, "MenuItem", ""),
	MenuItem_FTP_SETTINGS_VIEW(7014,"MenuItem",""),
	MenuItem_ManageZones_VIEW(7015, "MenuItem", ""),
	MenuItem_Faq_VIEW(7016, "MenuItem", ""),
	MenuItem_Terms_VIEW(7017, "MenuItem", ""),
	MenuItem_ManageItems_VIEW(7018, "MenuItem", ""),
	
	//Administrator
	
	MenuItem_CreateUser_VIEW(8001, "MenuItem", ""),
	MenuItem_ListUsers_VIEW(8002, "MenuItem", ""),
	MenuItem_ManageRights_VIEW(8003, "MenuItem", ""),
	MenuItem_AdminPOSSettings_VIEW(8004, "MenuItem", ""),
	MenuItem_AdminBCRMSettings_VIEW(8005, "MenuItem", ""),
	MenuItem_MissingSalesANdReceipts_VIEW(8006, "MenuItem", ""),
	MenuItem_SparkBaseSettings_VIEW(8007, "MenuItem", ""),
	MenuItem_DigitalReceiptsSettings_VIEW(8008, "MenuItem", ""),
	MenuItem_AdminDashboard_VIEW(8009, "MenuItem", ""),
	MenuItem_LatestSalesDetails_VIEW(8010, "MenuItem", ""),
	MenuItem_AddEditVmta_VIEW(8011, "MenuItem", ""),
	MenuItem_ScoreSEttings_VIEW(8012, "MenuItem", ""),
	MenuItem_VIEW_TICKETS_VIEW(8013, "MenuItem", ""),
	MenuItem_VIEW_SMS_GATEWYA_VIEW(8014, "MenuItem", ""),
	MenuItem_OPT_SYNC_USER_VIEW(8015, "MenuItem", ""),
	MenuItem_SOCIAL_VIEW(8016, "MenuItem", ""),
	MenuItem_LoyaltySettings_VIEW(8017, "MenuItem", ""),
	
	MenuItem_EMPTY_VIEW(30000, "MenuItem", ""),
	MenuItem_HEADER_VIEW(30001, "MenuItem", ""),
	MenuItem_FEEDBACK_VIEW(30002, "MenuItem", ""),
	MenuItem_FOOTER_VIEW(30003, "MenuItem", ""),
	
	MenuItem_UPDATE_SUBSCRIPTION_VIEW(30004, "MenuItem", ""),
	MenuItem_SUPPORT_VIEW(30005, "MenuItem", ""),
	MenuItem_ManageUsersAdmin_READ(30006, "MenuItem", ""),
	
	
	// Loyalty
	MenuItem_Create_Loyalty_VIEW(9003, "MenuItem", ""),
	MenuItem_My_Loyalty_VIEW(9004, "MenuItem", ""),
	MenuItem_Loyalty_Customer_LookUp_VIEW(9005, "MenuItem", ""),
	MenuItem_Loyalty_Dashboard_VIEW(9006, "MenuItem", ""),
	MenuItem_Loyalty_Fraud_Alert_VIEW(9007,"MenuItem",""),
	MenuItem_Loyalty_Transfer_Card_VIEW(9008, "MenuItem", ""),
	Menu_Lty_Transfer_Card_VIEW(9009,"MenuItem",""),
	MenuItem_Loyalty_Menu_Customer_LookUp_VIEW(9010,"MebuItem",""),
	MenuItem_Loyalty_Create_Special_Rewards_VIEW(9011,"MenuItem",""),
	MenuItem_Loyalty_My_Special_Rewards_VIEW(9012,"MenuItem",""),
	MenuItem_FilesUpload_VIEW(9013,"MenuItem",""),
	MenuItem_Loyalty_Menu_Customer_LookUp_Fbb_VIEW(9014,"MenuItem",""),
	MenuItem_RewardReports_VIEW(9015,"MenuItem",""),
	MenuItem_LtyROIReports_VIEW(9016,"MenuItem",""),
	MenuItem_Value_codes(9017,"MenuItem",""),
	MenuItem_LiabilityReports_VIEW(9018,"MenuItem",""),
	MenuItem_Loyalty_Menu_Customer_LookUp_And_Redeem_VIEW(9019,"MebuItem",""),
	MenuItem_AddMember_VIEW(9020,"MenuItem",""),
        //Menu level Vie
		
		
	
	MenuItem_Dashboard_READ(11001, "MenuItem", ""),
	// Tab level read
	
		Tab_Optintel_READ(19001, "Tab",""),
		Tab_Loyalty_READ(19002, "Tab" ,""),

	//Contact 

	MenuItem_Lists_READ(12001, "MenuItem", ""),
	MenuItem_ViewContactSegments_READ(12002, "MenuItem", ""),
	MenuItem_AddImport_Contacts_READ(12003, "MenuItem", ""),
	MenuItem_CreateSegment_READ(12004, "MenuItem", ""),
	MenuItem_Webforms_READ(12005, "MenuItem", ""),
	MenuItem_Suppressed_Contacts_READ(12006, "MenuItem", ""),
	MenuItem_ParentalConsentApprovals_READ(12007, "MenuItem", ""),
	MenuItem_CreateBCRMSegemnt_READ(12008, "MenuItem", ""),
	MenuItem_ViewBCRMSegments_READ(12009, "MenuItem", ""),
	MeanuItem_ViewDownload_READ(12010,"MenuItem", ""),
	MenuItem_Single_View_Of_Contacts_READ(12011,"MenuItem", ""),

	//Campaigns

	MenuItem_CreateEmail_READ(13001, "MenuItem", ""),
	MenuItem_CreateSMS_READ(13002, "MenuItem", ""),
	MenuItem_CreateSMCampaign_READ(13003, "MenuItem", ""),
	MenuItem_MyEmailsCampaigns_READ(13004, "MenuItem", ""),
	MenuItem_MySMSCampaigns_READ(13005, "MenuItem", ""),
	MenuItem_MySMCampaigns_READ(13006, "MenuItem", ""),
	//app-4288
	MenuItem_MyWACampaigns_READ(13007, "MenuItem", ""),

	
	//whatsapp
	MenuItem_CreateWA_VIEW(22001, "MenuItem", ""),
	MenuItem_CreateWA_READ(220001, "MenuItem", ""),


	//Marketing Tools

	MenuItem_Promocodes_READ(14001, "MenuItem", ""),
	MenuItem_DigitalReceipt_READ(14002, "MenuItem", ""),
	MenuItem_Event_Triggers_READ(14003, "MenuItem", ""),
	MenuItem_ManageKeywords_READ(14004, "MenuItem", ""),
	MenuItem_ManageAutoEmails_READ(14005, "MenuItem", ""),
	MenuItem_SMSSettings_READ(14006, "MenuItem", ""),
	MenuItem_ProgramBuilder_READ(14007, "MenuItem", ""),
	MenuItem_Optin_SMSSettings_READ(14008, "MenuItem", ""),
	MenuItem_ManageAutoSMS_READ(14009, "MenuItem", ""),
	MenuItem_ManageAutoEmail_BEE_READ(14010, "MenuItem", ""),
	MenuItem_ManageAutoEmail_plain_READ(14011, "MenuItem", ""),
	MenuItem_CreatePromocodes_READ(14012, "MenuItem", ""),
	MenuItem_CreatePromocodesSucceeding_READ(14013, "MenuItem", ""),
	MenuItem_Notification_READ(14014, "MenuItem", ""),
	MenuItem_Offer_Notification_READ(14015, "MenuItem", ""),

	MenuItem_REFERALSettings_READ(14016, "MenuItem", ""),
	MenuItem_REFERALCampaign_READ(14017, "MenuItem", ""),
	
	MenuItem_MYREFERALPrograms_READ(14018,"MenuItem",""),

	MenuItem_Referalreview_READ(14019,"MenuItem",""),
	MenuItem_REFERRALReports_READ(14020,"MenuItem",""),
	// Reports

	MenuItem_EmailCampaignReports_READ(15001, "MenuItem", ""),
	MenuItem_SMSCampaignReports_READ(15002, "MenuItem", ""),
	MenuItem_SMSKeywordUsageReports_READ(15003, "MenuItem", ""),
	MenuItem_PromocodeReports_READ(15004, "MenuItem", ""),
	MenuItem_OptIntelReports_READ(15005, "MenuItem", ""),
	MenuItem_LoyaltyEnrollmentReports_READ(15006, "MenuItem", ""),
	MenuItem_LoyaltyReports_READ(15007, "MenuItem", ""),
	MenuItem_Digital_Receipts_Reports_READ(15008,"MenuItem",""),
	MenuItem_Event_Trigger_New_Reports_READ(15009,"MenuItem",""),
	MenuItem_Event_Trigger_New_ET_Reports_READ(15010,"MenuItem",""),
	MenuItem_Auto_EmailCampaignReports_READ(15011, "MenuItem", ""),
	MenuItem_Detailed_Auto_EmailCampaignReports_READ(15012, "MenuItem", ""),
	MenuItem_E_Receipts_Reports_READ(15013, "MenuItem", ""),
	MenuItem_AUTO_SMS_REPORTS_READ(15014,"MenuItem",""),
	MenuItem_Detailed_Auto_SMS_READ(15015,"MenuItem",""),
	MenuItem_Notification_report_READ(15016,"MenuItem",""),
	MenuItem_RedemeedReports_READ(15017,"MenuItem",""),
	//whatsapp
	MenuItem_WACampaignReports_READ(15018, "MenuItem", ""),
	MenuItem_DetailedWACampaignReports_READ(15019, "MenuItem", ""),
	
	//Assets

	MenuItem_MyImages_READ(16001, "MenuItem", ""),
	MenuItem_MyEmailTemplates_READ(16002, "MenuItem", ""),

	//My Account

	MenuItem_MyProfile_READ(17001, "MenuItem", ""),
	MenuItem_MySettings_READ(17002, "MenuItem", ""),
	//MenuItem_POSSettings_READ(17003, "MenuItem", ""),
	MenuItem_ListSettings_READ(17004, "MenuItem", ""),
	MenuItem_PowerShare_READ(17005, "MenuItem", ""),
	MenuItem_SuperShare_READ(17006, "MenuItem", ""),
	MenuItem_ManageUsers_READ(17007, "MenuItem", ""),
	MenuItem_ManageStores_READ(17008, "MenuItem", ""),
	MenuItem_RecentActivities_READ(17009, "MenuItem", ""),
	//MenuItem_BCRMSettings_READ(17010, "MenuItem", ""),
	MenuItem_ManageBilling_READ(17011, "MenuItem", ""),
	MenuItem_Subscriber_Settings_READ(17012, "MenuItem", ""),
	MenuItem_OPT_SYNC_READ(17013, "MenuItem", ""),
	MenuItem_FTP_SETTINGS_READ(17014,"MenuItem",""),
	MenuItem_ManageZones_READ(17015, "MenuItem", ""),
	MenuItem_Faq_READ(17016, "MenuItem", ""),
	MenuItem_Terms_READ(17017, "MenuItem", ""),
        MenuItem_ManageItems_READ(17018, "MenuItem", ""),
	
	
	//Administrator

	MenuItem_CreateUser_READ(18001, "MenuItem", ""),
	MenuItem_ListUsers_READ(18002, "MenuItem", ""),
	MenuItem_ManageRights_READ(18003, "MenuItem", ""),
	MenuItem_AdminPOSSettings_READ(18004, "MenuItem", ""),
	MenuItem_AdminBCRMSettings_READ(18005, "MenuItem", ""),
	MenuItem_MissingSalesANdReceipts_READ(18006, "MenuItem", ""),
	MenuItem_SparkBaseSettings_READ(18007, "MenuItem", ""),
	MenuItem_DigitalReceiptsSettings_READ(18008, "MenuItem", ""),
	MenuItem_AdminDashboard_READ(18009, "MenuItem", ""),
	MenuItem_LatestSalesDetails_READ(18010, "MenuItem", ""),
	MenuItem_AddEditVmta_READ(18011, "MenuItem", ""),
	MenuItem_ScoreSEttings_READ(18012, "MenuItem", ""),
	MenuItem_VIEW_TICKETS_READ(18013, "MenuItem", ""),
	MenuItem_VIEW_SMS_GATEWYA_READ(18014, "MenuItem", ""),
	MenuItem_OPT_SYNC_USER_READ(18015, "MenuItem", ""),
	MenuItem_SOCIAL_READ(18016, "MenuItem", ""),
	MenuItem_LoyaltySettings_READ(18017, "MenuItem", ""),
	
	// Loyalty
	MenuItem_Create_Loyalty_READ(19003, "MenuItem", ""),
	MenuItem_My_Loyalty_READ(19004, "MenuItem", ""),
	MenuItem_Loyalty_Customer_LookUp_READ(19005,"MenuItem",""),
	MenuItem_Loyalty_Dashboard_READ(19006,"MenuItem",""),
	MenuItem_Loyalty_Fraud_Alert_READ(19007,"MenuItem",""),
	MenuItem_Loyalty_Transfer_Card_READ(19008,"MenuItem",""),
	Menu_Lty_Transfer_Card_READ(19009,"MenuItem",""),
	MenuItem_Loyalty_Menu_Customer_LookUp_READ(19010,"MenuItem",""),
	MenuItem_Loyalty_Create_Special_Rewards_READ(19011,"MenuItem",""),/*Changes 2.8*/
	MenuItem_Loyalty_My_Special_Rewards_READ(19012,"MenuItem",""),
	MenuItem_FilesUpload_READ(19013,"MenuItem",""),
	MenuItem_Loyalty_Menu_Customer_LookUp_Fbb_READ(19014,"MenuItem",""),
	MenuItem_Loyalty_Menu_Customer_LookUp_And_Redeem_READ(19015,"MenuItem",""),
	MenuItem_AddMember_READ(19016,"MenuItem",""),
	MenuItem_EMPTY_READ(130000, "MenuItem", ""),
	MenuItem_HEADER_READ(130001, "MenuItem", ""),
	MenuItem_FEEDBACK_READ(130002, "MenuItem", ""),
	MenuItem_FOOTER_READ(130003, "MenuItem", ""),
	MenuItem_UPDATE_SUBSCRIPTION_READ(130004, "MenuItem", ""),
	MenuItem_SUPPORT_READ(130005, "MenuItem", ""),
	

	
	
	// E-Receipts
	MenuItem_E_Receipt_Templates_VIEW(20001, "MenuItem", ""),
	MenuItem_E_Receipt_TemplatesSettings_VIEW(20002, "MenuItem", ""),
	MenuItem_E_Receipt_DnD_Editor_VIEW(20003, "MenuItem", ""),
	MenuItem_E_Receipt_Legacy_Editor_VIEW(20004, "MenuItem", ""),
	
	MenuItem_E_Receipt_Templates_READ(120001, "MenuItem", ""),
	MenuItem_E_Receipt_TemplatesSettings_READ(120002, "MenuItem", ""),
	MenuItem_E_Receipt_DnD_Editor_READ(120003, "MenuItem", ""),
	MenuItem_E_Receipt_Legacy_Editor_READ(120004, "MenuItem", ""),

	//Events
	MenuItem_Create_Event_READ(21001, "MenuItem", ""),
	MenuItem_Create_Event_VIEW(21002, "MenuItem", ""),
	MenuItem_Manage_Event_READ(21003, "MenuItem", ""),
	MenuItem_Manage_Event_VIEW(21004, "MenuItem", ""),
	
	
	
	;
	
	private  long right_id;
	private String type;
	private String description;
	
	private RightsEnum(long right_id, String type, String description) {
		this.right_id = right_id;
		this.type = type;
		this.description = description;
	}

	public long getRight_id() {
		return right_id;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}
	
}
