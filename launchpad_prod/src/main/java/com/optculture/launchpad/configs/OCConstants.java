package com.optculture.launchpad.configs;

public class OCConstants {
	
	//Loyalty Related.
	public static final String LOYALTY_MEMBERSHIP_REWARD_FLAG_G = "G";
	public static final String LOYALTY_MEMBERSHIP_REWARD_FLAG_L = "L";
	public static final String LOYALTY_MEMBERSHIP_REWARD_FLAG_GL = "GL";
	public static final String LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH = "Month";
	public static final String LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR = "Year";
	
	//Loyalty Enable Flag
	 public static  char FLAG_YES = 'Y';
	 public static char FLAG_NO = 'N';
	 
	 //SMTP 
	 public static final String SMTP_SENDGRID = "SendGrid";
	 public static final String SMTP_AMAZONSES= "AmazonSES";
	 public static final String SMTP_MAILERCLOUD= "MailerCloud";
	 
	 //Communication Related
	 public static final String EMAIL_SOURCE_TYPE = "Communication";
	 
	 public static final String COUP_GENT_CAMPAIGN_TYPE_SINGLE_EMAIL = "AutoEmail";

	 
	 //Communication Report statuses
	 public static final String CR_STATUS_SENDING = "Sending";
	 public static final String CR_STATUS_SENT = "Sent";
	 public static final String CR_TYPE_BOUNCES ="bounces";
	 public static final String CR_TYPE_SPAM ="spams";
	
	 //Param included while sending Campaign/DR delivery status
	 public static final String URL_PARAM_EMAIL = "Email";
	 public static final String URL_PARAM_EMAIL_TYPE = "EmailType";
	 public static final String URL_PARAM_EQ_ID = "EQID";
	 public static final String URL_PARAM_SES_TYPE = "SES";
	 public static final String URL_PARAM_EVENT = "event";
	 public static final String URL_PARAM_USERID = "userId";
	 public static final String URL_PARAM_SERVERNAME = "ServerName";
	 public static final String URL_PARAM_STATUS = "status";
	 public static final String URL_PARAM_SENTID = "sentId";
	 public static final String URL_PARAM_CRID = "crId";
	 public static final String URL_PARAM_TYPE = "type";
	 public static final String URL_PARAM_REASON="reason";
	 public static final String EQ_TYPE_DIGITALRECIEPT = "DigitalReceipt";
	 public static final String DLR_TYPE_EMAIL= "email";
	 
	 //delivery type 
	 public static final String EXTERNAL_SMTP_EVENTS_TYPE_DELIVERED = "delivered";
	 public static final  String EXTERNAL_SMTP_EVENTS_TYPE_DROPPED = "dropped";
	 public static final String EXTERNAL_SMTP_EVENTS_TYPE_BOUNCE = "bounce";
	 public static final String EXTERNAL_SMTP_EVENTS_TYPE_SPAMREPORT= "spamreport";
	 public static final String EXTERNAL_SMTP_EVENTS_TYPE_BLOCKED = "blocked";
	 public static final String EXTERNAL_SMTP_EVENTS_TYPE_EXPIRED = "expired";
	 
	 //schedule statuses
	 public static final String CS_STATUS_SUCCESS = "Success";
	 public static final String CS_STATUS_BOUNCED = "bouncedcontact";
	
	 
	//Suppressed type 
	 public static final String SUPP_TYPE_BOUNCED = "bouncedcontact";
	 public static final String CONT_STATUS_REPORT_AS_SPAM = "Reported as Spam";
	 
	//contact email status type
	public static final String CONT_STATUS_ACTIVE = "Active"; 
	public static final String CONT_STATUS_BOUNCED = "Bounced";
	public static final String CS_STATUS_SPAMMED = "Spammed";
	
	
	//CommunicationType
	public static final String EMAIL_COMMUNICATION = "Email";
	public static final String SMS_COMMUNICATION = "SMS";
	public static final String WA_COMMUNICATION = "WhatsApp";

	
	//Delimiters
	public static final String STRING_NILL = "";

	
	// replacing strings
	public static final String CONTACT_LOYALTY_TYPE_POS= "POS";
	public static final String CONTACT_LOYALTY_TYPE_STORE = "Store";
	

}
