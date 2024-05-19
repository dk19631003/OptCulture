package com.optculture.api.general;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StatusCodes {

	public static Set<String> DeliveredSet = new HashSet<String>();
	public static Set<String> BouncedSet = new HashSet<String>();
	public static Set<String> ReadSet = new HashSet<String>();
	public static Set<String> SuppressedSet = new HashSet<String>();

	
	
	//SMS 
	public static final Map<String,String> equenceStatusCodesMap = new HashMap<String, String>();
	public static final Map<String,String> clickaTellStatusCodesMap = new HashMap<String, String>();
	public static final Map<String,String> clickaTellStatusMessagesMap = new HashMap<String, String>();
	public static final Map<String,String> synapseStatusCodesMap = new HashMap<String, String>();

	
	

	public static final String EQUENCE_STATUS_DELIVERED="Delivered";
	public static final String EQUENCE_STATUS_PERMANENT_FAILURE="Permanent Failure"; //b s
	public static final String EQUENCE_STATUS_MOBILE_SWITCH_OFF="Mobile Switch Off";
	public static final String MOBILE_NOT_REACHABLE="Mobile Not Reachable";
	public static final String HANDSET_BUSY_FOR_SMS="Handset Busy for SMS";
	public static final String SIM_MEMORY_FULL="Sim memory Full";
	public static final String SYSTEM_FAILURE="System Failure";//b
	public static final String NO_RESPONSE_FROM_NETWORK="No Response from Network";//b
	public static final String APPLICATION_ERROR="Application Error";//b
	public static final String DND_REJECT="DND Reject";//b s
	public static final String BLACKLIST_REJECT="Blacklist Reject";//b s
	public static final String INSUFFICIENT_CREDIT_REJECTION="Insufficient Credit rejection";
	public static final String PROMO_TIME_REJECT="Promo Time Reject";
	public static final String SPAM_REJECTION="Spam Rejection";
	public static final String TEMPLATE_REJECTION="Template Rejection";
	public static final String SENDER_ID_REJECTION="SenderId Rejection";
	public static final String DUPLICATE_SMS_REJECT="Duplicate SMS Reject";
	public static final String MESSAGE_QUEUE_EXCEEDED_REJECTION="Message Queue Exceded Rejection";
	public static final String EQUENCE_TELECOM_REPORT_PENDING="Submitted";
	public static final String EQUENCE_DLT_MISCELLANEOUS_REJECT="DLT Miscellaneous Reject";
	public static final String EQUENCE_DLT_TEMPLATE_REJECT="DLT Template Reject";

	


	
	
	public static final String CLICKATELL_STATUS_REPORTDELAY = "Reporting is Delayed ";
	public static final String CLICKATELL_STATUS_QUEUED = "Attempting Redelivery";
	public static final String CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT = "Pending";
	public static final String CLICKATELL_STATUS_RECEIVED = "Delivered";
	public static final String CLICKATELL_STATUS_MESSAGE_ERROR = "Error with message";
	public static final String CLICKATELL_STATUS_USER_CANCELLED ="User cancelled message delivery";
	public static final String CLICKATELL_STATUS_DELIVERY_ERROR ="Undelivered";
	public static final String CLICKATELL_STATUS_RECEIVED_BY_GATEWAY ="Message received by gateway";
	public static final String CLICKATELL_STATUS_ROUTING_ERROR ="Routing error";
	public static final String CLICKATELL_STATUS_MESSAGE_EXPIRED ="Message expired";
	public static final String CLICKATELL_STATUS_DELIVERY_DELAYED ="delayed delivery";
	public static final String CLICKATELL_STATUS_OUT_OF_CREDITS ="Out of credit";
	public static final String CLICKATELL_STATUS_NO_CREDITS = "No credit left";
	public static final String CLICKATELL_STATUS_MAX_ALLOWED_CREDITS = "Max allowed credit";
	public static final String CLICKATELL_STATUS_OPTED_OUT ="Opted-out";
	public static final String CLICKATELL_STATUS_INVALID_NUMBER ="Invalid Number";
	public static final String CLICKATELL_STATUS_BLOCKED ="Blocked";
	public static final String CLICKATELL_STATUS_OTHER = "Others";
	public static final String CLICKATELL_STATUS_MESSAGE_PARTS_EXCEEDED = "Maximum message parts exceeded";
	public static final String CLICKATELL_STATUS_CAN_NOT_ROUTE_MESSAGE = "Cannot route message";
	public static final String CLICKATELL_STATUS_NUMBER_DELISTED = "Number delisted";
	public static final String CLICKATELL_STATUS_BOUNCED = "Bounced";
	public static final String EQUENCE_STATUS_BOUNCED = "Bounced";
	public static final String SYNAPSE_STATUS_BOUNCED = "Bounced";
	
	
	public static final String SYNAPSE_STATUS_DELIVERED="Delivered";
	public static final String SYNAPSE_STATUS_EXPIRED="Expired";
	public static final String SYNAPSE_STATUS_DELETED="Deleted";
	public static final String SYNAPSE_STATUS_UNDELIVERED="Undelivered";
	public static final String SYNAPSE_STATUS_ACCEPTED="Accepted";
	public static final String SYNAPSE_STATUS_UNKNOWN="Unknown";
	public static final String SYNAPSE_STATUS_REJECTED="Rejected";
	public static final String SYNAPSE_STATUS_ENROUTE="Enroute";
	
	
	
	static {

		DeliveredSet.add("delivered");//Meta
		DeliveredSet.add("DELIVERED");//Gupshup
		DeliveredSet.add("2");//CM

		BouncedSet.add("failed");//Meta
		BouncedSet.add("FAILED");//Gupshup
		BouncedSet.add("3");//CM

		ReadSet.add("read");//Meta
		ReadSet.add("READ");//Gupshup
		ReadSet.add("4");//CM
		
		//SMS
		
		DeliveredSet.add(EQUENCE_STATUS_DELIVERED);
		DeliveredSet.add(EQUENCE_TELECOM_REPORT_PENDING);
		DeliveredSet.add(SYNAPSE_STATUS_DELIVERED);

		BouncedSet.add(EQUENCE_STATUS_MOBILE_SWITCH_OFF);
		BouncedSet.add(MOBILE_NOT_REACHABLE);
		BouncedSet.add(HANDSET_BUSY_FOR_SMS);
		BouncedSet.add(SIM_MEMORY_FULL);
		BouncedSet.add(NO_RESPONSE_FROM_NETWORK);
		BouncedSet.add(EQUENCE_STATUS_PERMANENT_FAILURE);
		BouncedSet.add(SYSTEM_FAILURE);
		BouncedSet.add(APPLICATION_ERROR);
		BouncedSet.add(DND_REJECT);
		BouncedSet.add(BLACKLIST_REJECT);
		BouncedSet.add(PROMO_TIME_REJECT);
		BouncedSet.add(DUPLICATE_SMS_REJECT);
		BouncedSet.add(MESSAGE_QUEUE_EXCEEDED_REJECTION);
		BouncedSet.add(INSUFFICIENT_CREDIT_REJECTION);
		BouncedSet.add(SPAM_REJECTION);
		BouncedSet.add(TEMPLATE_REJECTION);
		BouncedSet.add(SENDER_ID_REJECTION);
		BouncedSet.add(SYNAPSE_STATUS_EXPIRED);
		BouncedSet.add(SYNAPSE_STATUS_DELETED);
		BouncedSet.add(SYNAPSE_STATUS_UNDELIVERED);
		//Accepted??
		//Enroute??
		BouncedSet.add(SYNAPSE_STATUS_UNKNOWN);
		BouncedSet.add(SYNAPSE_STATUS_REJECTED);

		SuppressedSet.add(EQUENCE_STATUS_PERMANENT_FAILURE);
		SuppressedSet.add(DND_REJECT);
		SuppressedSet.add(BLACKLIST_REJECT);
		SuppressedSet.add(SYNAPSE_STATUS_REJECTED);

		
		equenceStatusCodesMap.put("0", EQUENCE_TELECOM_REPORT_PENDING);
		equenceStatusCodesMap.put("1", EQUENCE_STATUS_DELIVERED);
		equenceStatusCodesMap.put("10",EQUENCE_STATUS_PERMANENT_FAILURE);
		equenceStatusCodesMap.put("27",EQUENCE_STATUS_MOBILE_SWITCH_OFF);
		equenceStatusCodesMap.put("28",MOBILE_NOT_REACHABLE);
		equenceStatusCodesMap.put("31",HANDSET_BUSY_FOR_SMS);
		equenceStatusCodesMap.put("32",SIM_MEMORY_FULL);
		equenceStatusCodesMap.put("34",SYSTEM_FAILURE);
		equenceStatusCodesMap.put("75",NO_RESPONSE_FROM_NETWORK);
		equenceStatusCodesMap.put("80",APPLICATION_ERROR);
		equenceStatusCodesMap.put("101",DND_REJECT);
		equenceStatusCodesMap.put("102",BLACKLIST_REJECT);
		equenceStatusCodesMap.put("103",INSUFFICIENT_CREDIT_REJECTION);
		equenceStatusCodesMap.put("104",PROMO_TIME_REJECT);
		equenceStatusCodesMap.put("105",SPAM_REJECTION);
		equenceStatusCodesMap.put("106",TEMPLATE_REJECTION);
		equenceStatusCodesMap.put("107",SENDER_ID_REJECTION);
		equenceStatusCodesMap.put("108",DUPLICATE_SMS_REJECT);
		equenceStatusCodesMap.put("109",MESSAGE_QUEUE_EXCEEDED_REJECTION);
		equenceStatusCodesMap.put("301",EQUENCE_DLT_TEMPLATE_REJECT);
		equenceStatusCodesMap.put("302",EQUENCE_DLT_MISCELLANEOUS_REJECT);


		
		clickaTellStatusCodesMap.put("001",CLICKATELL_STATUS_REPORTDELAY);
		clickaTellStatusCodesMap.put("002",CLICKATELL_STATUS_QUEUED);
		clickaTellStatusCodesMap.put("003",CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT);
		clickaTellStatusCodesMap.put("004",CLICKATELL_STATUS_RECEIVED);
		clickaTellStatusCodesMap.put("005",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("006",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("009",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("007",CLICKATELL_STATUS_DELIVERY_ERROR);
		clickaTellStatusCodesMap.put("008",CLICKATELL_STATUS_RECEIVED_BY_GATEWAY);
		clickaTellStatusCodesMap.put("010",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("011",CLICKATELL_STATUS_DELIVERY_DELAYED);
		clickaTellStatusCodesMap.put("012",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("113",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("114",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("301",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("302",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("128",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("122",CLICKATELL_STATUS_OPTED_OUT);
		clickaTellStatusCodesMap.put("121",CLICKATELL_STATUS_BLOCKED);
		clickaTellStatusCodesMap.put("105",CLICKATELL_STATUS_INVALID_NUMBER);

		clickaTellStatusMessagesMap.put("005", CLICKATELL_STATUS_MESSAGE_ERROR);
		clickaTellStatusMessagesMap.put("006", CLICKATELL_STATUS_USER_CANCELLED);
		clickaTellStatusMessagesMap.put("009", CLICKATELL_STATUS_ROUTING_ERROR);
		clickaTellStatusMessagesMap.put("010", CLICKATELL_STATUS_MESSAGE_EXPIRED);
		clickaTellStatusMessagesMap.put("012", CLICKATELL_STATUS_OUT_OF_CREDITS);
		clickaTellStatusMessagesMap.put("113", CLICKATELL_STATUS_MESSAGE_PARTS_EXCEEDED );
		clickaTellStatusMessagesMap.put("114", CLICKATELL_STATUS_CAN_NOT_ROUTE_MESSAGE);
		clickaTellStatusMessagesMap.put("128", CLICKATELL_STATUS_NUMBER_DELISTED);
		clickaTellStatusMessagesMap.put("301", CLICKATELL_STATUS_NO_CREDITS);
		clickaTellStatusMessagesMap.put("302", CLICKATELL_STATUS_MAX_ALLOWED_CREDITS);
		
		
		synapseStatusCodesMap.put("DELIVRD", SYNAPSE_STATUS_DELIVERED);
		synapseStatusCodesMap.put("EXPIRED", SYNAPSE_STATUS_EXPIRED);
		synapseStatusCodesMap.put("DELETED", SYNAPSE_STATUS_DELETED);
		synapseStatusCodesMap.put("UNDELIV", SYNAPSE_STATUS_UNDELIVERED);
		synapseStatusCodesMap.put("ACCEPTD", SYNAPSE_STATUS_ACCEPTED);
		synapseStatusCodesMap.put("UNKNOWN", SYNAPSE_STATUS_UNKNOWN);
		synapseStatusCodesMap.put("REJECTD", SYNAPSE_STATUS_REJECTED);
		synapseStatusCodesMap.put("ENROUTE", SYNAPSE_STATUS_ENROUTE);

	}

	public static final String STATUS_BOUNCED = "Bounced";
	public static final String STATUS_DELIVERED="Delivered";
	public static final String STATUS_SUBMITTED="Sent";
	public static final String STATUS_READ="Read";
	public static final String STATUS_SUPPRESSED = "Suppressed";
	public static final String STATUS_UNDELIVERED="Undelivered";

	



}
