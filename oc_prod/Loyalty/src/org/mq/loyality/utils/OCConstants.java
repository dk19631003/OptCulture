/**
 * 
 */
package org.mq.loyality.utils;

/**
 * @author manjunath.nunna
 *
 */
public interface OCConstants {
	
	public static final String SAVE_ACTION 				= "SAVE";
	public static final String TEST_SERVICE 			= "testService";
	public static final String TEST_DAO 				= "testDAO";
	public static final String SUBSCRIBER_LOGGER 		= "subscriberLogger";
	public static final String SCHEDULER_LOGGER 		= "schedulerLogger";
	
	String LOYALTY_TRANSACTION_ENROLMENT = "Enrolment";
	String LOYALTY_TRANSACTION_INQUIRY = "Inquiry";
	String LOYALTY_TRANSACTION_ISSUANCE = "Issuance";
	String LOYALTY_TRANSACTION_REDEMPTION = "Redemption";
	String LOYALTY_TRANSACTION_ADJUSTMENT = "Adjustment";
	String LOYALTY_TRANSACTION_ENROLENQUIRY = "EnrolEnquiry";
	String LOYALTY_TRANSACTION_EXPIRY = "Expiry";
		
	String LOYALTY_PROGRAM_TIER1 = "Tier 1";
	String LOYALTY_LIFETIME_POINTS = "LifetimePoints";
	String LOYALTY_LIFETIME_PURCHASE_VALUE = "LifetimePurchaseValue";
	String LOYALTY_CUMULATIVE_PURCHASE_VALUE = "CumulativePurchaseValue";
	
	//end entered amount types in transactionchild table
	String OTP_GENERATED_CODE_STATUS_ACTIVE = "Active";
	String OTP_GENERATED_CODE_STATUS_EXPIRED = "Expired";
	
	String SMS_PROGRAM_KEYWORD_TYPE_OPTIN = "Optin";
	String SMS_PROGRAM_KEYWORD_TYPE_OPTOUT = "OptOut";
	String SMS_PROGRAM_KEYWORD_TYPE_HELP = "Help";
	
	String LOYALTY_MEMBERSHIP_TYPE_CARD = "Card";
	String LOYALTY_MEMBERSHIP_TYPE_MOBILE = "Mobile";
	
	char FLAG_YES = 'Y';
	char FLAG_NO = 'N';
	
	
	

}
