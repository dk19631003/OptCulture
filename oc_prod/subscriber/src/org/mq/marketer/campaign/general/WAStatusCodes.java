package org.mq.marketer.campaign.general;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class WAStatusCodes {
	public static Set<String> DeliveredSet = new HashSet<String>();
	public static Set<String> BouncedSet = new HashSet<String>();
	public static Set<String> ReadSet = new HashSet<String>();
	
	static {


		DeliveredSet.add("delivered");//MB
		DeliveredSet.add("2");//CM

		BouncedSet.add("failed");//MB
		BouncedSet.add("3");//CM
		
		ReadSet.add("read");//MB
		ReadSet.add("4");//CM

	}
  
	
	public static final String WA_STATUS_RECEIVED = "Delivered";
	public static final String WA_STATUS_DELIVERED_TO_RECEPIENT = "Pending";
	public static final String WA_STATUS_DELIVERY_ERROR = "Undelivered";
	public static final String MB_DLR_STATUS_ACCEPTED = "accepted";
	public static final String MB_DLR_STATUS_QUEUED = "pending";
	public static final String MB_DLR_STATUS_TRANSMITTED = "transmitted";
	public static final String MB_DLR_STATUS_SENT = "sent";
	public static final String MB_DLR_STATUS_DELIVERED = "delivered";
	public static final String MB_DLR_STATUS_READ = "read";

	public static final String MB_DLR_STATUS_FAILED = "failed";
	
	public static final String WA_STATUS_BOUNCED = "Bounced";
	public static final String WA_STATUS_DELIVERED="Delivered";
	public static final String WA_STATUS_SUBMITTED="Submitted";
	
	
	
	
	public static final Map<String,Map<String, String>> campTypeMap = new HashMap<String,Map<String, String>>();
	
}
