package org.mq.marketer.campaign.controller;



public enum ActivityEnum {
	 
	VISIT_PAGES(-2,"Visited Pages", "Visited pages logging activities", null,true),
		VISIT_GENERAL(-1,"General Pages", "General pages logging activities", VISIT_PAGES,true),
			VISIT_RM_HOME	(	200	, "Home Page", "Visited Home Page", VISIT_GENERAL,true),
		VISIT_CAMPAIGN(-1,"Campaign Pages", "Campaign pages logging activities", VISIT_PAGES,true),	
			VISIT_CAMPAIGN_CAMPAIGNS_LIST 	(	201	, "Email List Page", "Visited my mails page.", VISIT_CAMPAIGN,true),
			VISIT_CAMPAIGN_CREATE 	(	202	, "Create Email Page", "Visited create new mail page.", VISIT_CAMPAIGN,true),
			VISIT_CAMPAIGN_FINAL 	(	203	, "Email settings/final Page", "Visited mail final settings page.", VISIT_CAMPAIGN,true),
			VISIT_CAMPAIGN_LAYOUT 	(	204	, "Email layout page", "Visited email layout page", VISIT_CAMPAIGN,true),
			VISIT_CAMPAIGN_MLIST 	(	205	, "Email Mailing list Page", "Visited email mailing list page.", VISIT_CAMPAIGN,true),
			VISIT_CAMPAIGN_REPORT 	(	206	, "Email Report Page", "Visited email report page.", VISIT_CAMPAIGN,true),
			VISIT_CAMPAIGN_DETAILED_REPORT 	(	207	, "Email Report Page", "Visited email detail report page.", VISIT_CAMPAIGN,true),
			VISIT_CAMPAIGN_TEXT_MESSAGE 	(	208	, "Email Text Message Page", "Visited text message page", VISIT_CAMPAIGN,true),
			VISIT_CAMPAIGN_BLOCK_EDITOR 	(	209	, "Email Block Editor Page", "Visited block editor page.", VISIT_CAMPAIGN,true),
			VISIT_CAMPAIGN_PLAIN_EDITOR 	(	210	, "Email Plain Editor Page", "Visited plain editor page.", VISIT_CAMPAIGN,true),
			VISIT_CAMPAIGN_HTML_BEE_EDITOR 	(	224	, "Email BEE Editor Page", "Visited BEE editor page.", VISIT_CAMPAIGN,true),
			VISIT_CAMPAIGN_HTML_EDITOR 	(	211	, "Email HTML Editor Page", "Visited html editor page", VISIT_CAMPAIGN,true),
		VISIT_CONTACT(-1,"Contact Pages", "Contact pages logging activities", VISIT_PAGES,true),
			VISIT_CONTACT_ADDSINGLE 	(	212	, "Add Single Contact Page", "Visited contacts add single page.", VISIT_CONTACT,true),
			VISIT_CONTACT_UPLOAD_CONSENT 	(	213	, "Contact Consent Page", "Visited consent page.", VISIT_CONTACT,true),
			VISIT_CONTACT_CONTACT_VIEW 	(	214	, "View Contacts Page", "Visited view contacts page", VISIT_CONTACT,true),
			VISIT_CONTACT_LIST_VIEW 	(	215	, "Contact List View Page", "Visited contact list page", VISIT_CONTACT,true),
			VISIT_CONTACT_UPLOAD 	(	216	, "Contact Upload Page", "Visited contacts upload page.", VISIT_CONTACT,true),
			VISIT_CONTACT_UPLOAD_FILE 	(	217	, "Contacts Upload File Page", "Visited file upload page.", VISIT_CONTACT,true),
			VISIT_CONTACT_MANAGECONTACTS (218,"Manage contacts","Visited manage contacts page.",VISIT_CONTACT,true),
			VISIT_CONTACT_SUBSCRIPTION (219,"Subscription Form","Visited subscription form page.",VISIT_CONTACT,true),
			VISIT_CONTACT_SUBSCRIPTION_SETTGS (220,"Subscription Form Setting","Visited subscription form settings page.",VISIT_CONTACT,true),
		VISIT_GALLERY(-1,"Gallery Pages", "Gallery pages logging activities", VISIT_PAGES,true),	
			VISIT_GALLERY_VIEW 	(	221	, "Gallery View Page", "Visited gallery page", VISIT_GALLERY,true),
			VISIT_MESSAGE_INBOX 	(	222	, "Messages Inbox Page", "Visited messages page", VISIT_GALLERY,true),
			VISIT_UPDATE_PASSWORD 	(	223	, "Update Password Page", "Visited update password page.", VISIT_GALLERY,true),
		
	CAMP_MODULE(-1, "Email Module", "Campaign module logging activites",null,true),
		
		CAMP_CRE_SETT_p1campaignName(0, "Email Creation", "Email setting created for email name: [1].", CAMP_MODULE,true), 
		CAMP_EDIT_SETT_p1campaignName(1, "Email Editing",  "Edited email settings for email name : [1].", CAMP_MODULE,true),
		
		CAMP_CRE_ML_SETTG_p1MLName_p2campaignName(2, "Add Mailing List", "", CAMP_MODULE,true),
		CAMP_CRE_ML_EDIT_p1MLName_p2campaignName(3, "Edit Mailing List", "", CAMP_MODULE,true),
		
		CAMP_SAVED_TO_MYTEMPLATE_p1campaignName(4, "Add To Mytemplate", "Saved campaign templated to my template : [1]", CAMP_MODULE,true),
		
		CAMP_CRE_ADD_TEMPLATE_p1campaignName(5, "Email Template", "Email template configured/selected successfully. : [1]", CAMP_MODULE,true),
		CAMP_CRE_EDIT_TEMPLATE_p1type(6, "Edit Email Template", "Email [1] template edited .", CAMP_MODULE,true),
		
		CAMP_CRE_ADD_TEXTMSG_p2campaignName(7, "Add Text Message", "Saved new text message for the email [1]", CAMP_MODULE,true),
		CAMP_CRE_EDIT_TEXTMSG_p2campaignName(8, "Edit Text Message", "Edited text message for the email [1]", CAMP_MODULE,true),
		
		CAMP_CRE_SUBMIT_p1campaignName(9, "Submit Campaign", "Email [1] Configuration completed status is Active.", CAMP_MODULE,true),
		CAMP_CRE_SAVE_AS_DRAFT_p1campaignName(10, "Save As Draft", "Email [1] saved as Draft.", CAMP_MODULE,true),
		
		//CAMP_SCHEDULED_SENDNOW(6, "", "", CAMP_MODULE,true),
		//CAMP_SCHEDULED_SENDAGN(7, "", "", CAMP_MODULE,true),
		
		CAMP_SENT_TSTMAIL_p1campaignName(11, "Email Test Mail", "Configured a test mail for the campaign : [1]", CAMP_MODULE,true),
		CAMP_TEMPLATE_ZIPIMP_p1campaignName(12, "Zip Import", "Import email template from zip for the email [1]", CAMP_MODULE,true),
	
	CONTS_MODULE(-1,"Contacts Module", "Contact Module logging activities", null,true),
		CONTS_CRE_ML_p1mlName(21, "Create mailing list", "New Mailing list created  : [mlName]. ", CONTS_MODULE,true), 
		CONTS_CRE_CONT_p1contactNo_p2mlName(22, "Add contact", "Added [1] contact(s) has been added to mailing list : [2].", CONTS_MODULE,true),
		CONTS_ML_CONFIGURED_p1mlName_p2campaignName(23, "Configure mailing list", "Successfully configured mailing List [1] to email [2].", CONTS_MODULE,true),
		CONTS_DEL_CONT_p1contacts_p2mlName(24, "Delete contacts", "Deleted [1] contact(s) from mailing List : [2].", CONTS_MODULE,true),
		CONTS_DEL_ML_p1mlName(25, "Delete mailing list", "Deleted [1] mailing list(s) successfully.", CONTS_MODULE,true),
		CONTS_CPY_ML_p1mlName(26, "Copy Mailing list", "Created duplicate copy of mailing list : [1].", CONTS_MODULE,true),
		
		CONTS_EDIT_ML_p1mlName(27, "Edit Mailing List", "Modified mailing list [1] successfully.", CONTS_MODULE,true),
		CONTS_EDIT_CONT_p1contactName(28, "Edit Contact", "Modified contact [1] successfully.", CONTS_MODULE,true),
		
		CONTS_ADDED_SUPPCONTS(29, "Add suppress contacts", "Contacts added to suppress contacts list .", CONTS_MODULE,true),
		CONTS_DEL_SUPPCONTS(30, "Delete suppress contacts", "Suppress contacts deleted.", CONTS_MODULE,true),
		
		CONTS_CON_STATUS_ACT(31, "Change status active", "Contact status updated to active .",CONTS_MODULE,true),
		CONTS_CON_STATUS_INACT(32, "Change status inactive", "Contact status updated to inactive .",CONTS_MODULE,true),
		
		CONTS_ADD_WELCOM_MSG_p1templateName(33, "Add email Template", "New Double optin welcome message added : [1] .",CONTS_MODULE,true),
		CONTS_EDT_WELCOM_MSG_p1templateName(34, "Add new welcome message", "Edited Double optin welcome message : [1] .",CONTS_MODULE,true),
		
	GALLERY_MODULE(-1, "Gallery Module", "Gallery module logging activities", null,true),
		GALLRY_CRE_FOLDER_p1folderName(41, "Create Folder", "Created new folder in gallery : [1]", GALLERY_MODULE,true),
		GALLRY_UPLOAD_IMG_p1galleryName(42, "Upload image", "Uploaded new image in  gallery : [1]", GALLERY_MODULE,true),
		GALLRY_DEL_FOLDER_p1folderName(43, "Delete Folder", "Deleted folder in gallery : [1]", GALLERY_MODULE,true),
		GALLRY_DEL_IMG_p1imageName(44, "Delete Image", "Deleted image in gallery : [1]", GALLERY_MODULE,true),
		
	USR_MODULE(-1, "User Module", "User module logging activities", null,true),
		USR_LOGGED_IN(51, "Log in", "Just Logged in.",USR_MODULE,true),
		USR_NEW_FR_EML_p1emailName(52, "Add from email", "Added new from email : [1].",USR_MODULE,true),
		USR_CHG_PSWRD(53, "Change password", "Updated password",USR_MODULE,true),
	
	OC_ADMIN_MODULE(-1, "OC Admin Module", "admin module logging activities", null,false),
		ADMIN_LOGGED_IN(54, "Admin Log in", "You logged into another user-account [username: [1] and org_ID: [2]].",OC_ADMIN_MODULE,false),
	
	LOYALTY_MODULE(-1, "Loyalty Module", "Loyalty module logging activities", null,false),
	   LOYALTY_ADMIN_PASSWORD_BYPASS(55, "Loyalty Program Password Bypass", "[1] [2] loyalty program settings of account with : [3] [loyalty program name:[4]].",LOYALTY_MODULE,false),
	   LOYALTY_CUSTOMER_LOOP_UP_PASSWORD_BYPASS(56, "Loyalty customer loop-up Password Bypass", "[1] [2] membership# [3] from loyalty program [4] belonging to : [5].",LOYALTY_MODULE,false);
	
	//You [action] membership# [#] from loyalty program [name] belonging to username: xyz and org_ID: pq.
	
		
	private int code;
	private String operation;
	private String desc;
	private ActivityEnum parent;
	private Boolean isVisible;
	
	/**
	 * 
	 * @param c
	 * @param operation
	 * @param desc
	 * @param parent
	 */
	private ActivityEnum(int code, String operation, String desc, ActivityEnum parent,Boolean isVisible) {
		this.code = code;
		this.desc=desc;
		this.parent=parent;
		this.operation = operation;
		this.isVisible = isVisible;
	}

	public ActivityEnum getParent() {
		return parent;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getOperation() {
		return operation;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public Boolean getIsVisible() {
		return isVisible;
	}
	
} 


