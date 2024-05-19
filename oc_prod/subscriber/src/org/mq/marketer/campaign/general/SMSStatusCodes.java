package org.mq.marketer.campaign.general;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.mq.marketer.campaign.dao.CountryCodeDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;



public class SMSStatusCodes {

	public static final String NETCORE_ERROR_MOBILENUMINVALID = "Mobile number invalid";
	public static final String NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_UNAVAILABLE = "NDNC status is unavailable";
	public static final String NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_REGISTERED = "User registered with NDNC";
	public static final String NETCORE_ERROR_MSGTOUSERREJECTED_NDNC_STATUS_UNKNOWN = "NDNC status unknown";
	public static final String NETCORE_ERROR_EMPTY_FEEDID_OR_MOBNUM = "FeedId or mobile number is empty";
	public static final String NETCORE_SUCCESS = "success";
	
	
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
	
	public static final String SYNAPSE_STATUS_DELIVERED="Delivered";
	public static final String SYNAPSE_STATUS_EXPIRED="Expired";
	public static final String SYNAPSE_STATUS_DELETED="Deleted";
	public static final String SYNAPSE_STATUS_UNDELIVERED="Undelivered";
	public static final String SYNAPSE_STATUS_ACCEPTED="Accepted";
	public static final String SYNAPSE_STATUS_UNKNOWN="Unknown";
	public static final String SYNAPSE_STATUS_REJECTED="Rejected";
	public static final String SYNAPSE_STATUS_ENROUTE="Enroute";
	
	public static Set<String> DeliveredSet = new HashSet<String>();
	public static Set<String> BouncedSet = new HashSet<String>();
	public static Set<String> SuppressedSet = new HashSet<String>();
	
	public static final Map<String,String> clickaTellStatusCodesMap = new HashMap<String, String>();
	public static final Map<String,String> clickaTellStatusMessagesMap = new HashMap<String, String>();
	public static final Map<String,String> equenceStatusCodesMap = new HashMap<String, String>();
	public static final Map<String,String> synapseStatusCodesMap = new HashMap<String, String>();
	
	public static final Map<String, String> usCampTypeMap = new LinkedHashMap<String, String>();
	public static final Map<String, String> canadaCampTypeMap = new LinkedHashMap<String, String>();
	public static final Map<String, String> saCampTypeMap = new LinkedHashMap<String, String>();
	public static final Map<String, String> indiaCampTypeMap = new LinkedHashMap<String, String>();
	public static final Map<String, String> pakistanCampTypeMap = new LinkedHashMap<String, String>();
	//Added for UAE
	public static final Map<String, String> uaeCampTypeMap = new LinkedHashMap<String, String>();
	
	public static final Map<String,Map<String, String>> campTypeMap = new HashMap<String,Map<String, String>>();
	
	public static final Map<String, Boolean> optInMap = new HashMap<String, Boolean>();
	public static final Map<String, Boolean> optOutFooterMap = new HashMap<String, Boolean>();
	public static final Map<String, Boolean> userOptinMediumMap = new HashMap<String, Boolean>();
	public static final Map<String, Boolean> setCountryCode = new HashMap<String, Boolean>();
	
public static final Map<String, String> optInTypeMap = new HashMap<String, String>();
	
	public static final Map<String, Map<String, String>> countryCampValueMap = new HashMap<String, Map<String, String>>();
	
	public static final Map<String, String> indCampValueMap = new HashMap<String, String>();
	public static final Map<String, String> usCampValueMap = new HashMap<String, String>();
	public static final Map<String, String> pakistanCampValueMap = new HashMap<String, String>(); 
	public static final Map<String, String> uaeCampValueMap = new HashMap<String, String>();
	public static final Map<String, String> defaultSMSTypeMap = new HashMap<String, String>();
	public static final Map<String, String> defaultSMSOptinGatewayTypeMap = new HashMap<String, String>();
	public static final Map<String, String> defaultAccountMap = new HashMap<String, String>();
	public static final Map<String, String[]> keywordRuleMap = new HashMap<String, String[]>();
	public static Properties countryCodes;
	public static CountryCodeDao countryCodeDao;
	public static final Map<String, String> genericCampTypeMap = new LinkedHashMap<String, String>();
	public static final Map<String, String> genericCampValueMap = new HashMap<String, String>();
	
	public static final Map<String, Boolean> smsProgramlookupOverUserMap = new HashMap<String, Boolean>();
	
	static {
		
		try{
			countryCodeDao=(CountryCodeDao) ServiceLocator.getInstance().getDAOByName("countryCodeDao");
			countryCodes = countryCodeDao.findAllCountryCodes();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		optInMap.put(Constants.SMS_COUNTRY_INDIA, true);
		optInMap.put(Constants.SMS_COUNTRY_US, false);
		optInMap.put(Constants.SMS_COUNTRY_CANADA, false);
		optInMap.put(Constants.SMS_COUNTRY_SA, false);
		optInMap.put(Constants.SMS_COUNTRY_PAKISTAN, false);
		//Added for UAE
		optInMap.put(Constants.SMS_COUNTRY_UAE, false);
		
		optOutFooterMap.put(Constants.SMS_COUNTRY_INDIA, false);
		optOutFooterMap.put(Constants.SMS_COUNTRY_US, true);
		optOutFooterMap.put(Constants.SMS_COUNTRY_CANADA, true);
		optOutFooterMap.put(Constants.SMS_COUNTRY_SA, true);
		optOutFooterMap.put(Constants.SMS_COUNTRY_PAKISTAN, false);
		//Added for UAE
		optOutFooterMap.put(Constants.SMS_COUNTRY_UAE, true);
		
		userOptinMediumMap.put(Constants.SMS_COUNTRY_INDIA, true);
		userOptinMediumMap.put(Constants.SMS_COUNTRY_US, false);
		userOptinMediumMap.put(Constants.SMS_COUNTRY_CANADA, false);
		userOptinMediumMap.put(Constants.SMS_COUNTRY_SA, false);
		userOptinMediumMap.put(Constants.SMS_COUNTRY_PAKISTAN, true);//TODO
		//Added for UAE
		userOptinMediumMap.put(Constants.SMS_COUNTRY_UAE, true);
		
		
		setCountryCode.put(Constants.SMS_COUNTRY_INDIA, true);
		setCountryCode.put(Constants.SMS_COUNTRY_US, false);
		setCountryCode.put(Constants.SMS_COUNTRY_CANADA, false);
		setCountryCode.put(Constants.SMS_COUNTRY_SA, false);

		setCountryCode.put(Constants.SMS_COUNTRY_PAKISTAN, true);
		//Added for UAE
		setCountryCode.put(Constants.SMS_COUNTRY_UAE, true);
		
		optInTypeMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_ACCOUNT_TYPE_OPTIN);
		
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_US, Constants.SMS_TYPE_OUTBOUND);
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_CANADA, Constants.SMS_TYPE_OUTBOUND);
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_SA, Constants.SMS_TYPE_OUTBOUND);//APP-3819
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_PAKISTAN, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
		//Added for UAE
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_UAE, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);// uae added 16th june
		
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL);
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_US, Constants.SMS_TYPE_OUTBOUND);
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_CANADA, Constants.SMS_TYPE_OUTBOUND);
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_SA, Constants.SMS_TYPE_OUTBOUND);
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_PAKISTAN, Constants.SMS_TYPE_PROMOTIONAL);
		//Added for UAE
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_UAE, Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL);
		
	//TODO need to get username,password,feedid from DB
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
		
		synapseStatusCodesMap.put("DELIVRD", SYNAPSE_STATUS_DELIVERED);
		synapseStatusCodesMap.put("EXPIRED", SYNAPSE_STATUS_EXPIRED);
		synapseStatusCodesMap.put("DELETED", SYNAPSE_STATUS_DELETED);
		synapseStatusCodesMap.put("UNDELIV", SYNAPSE_STATUS_UNDELIVERED);
		synapseStatusCodesMap.put("ACCEPTD", SYNAPSE_STATUS_ACCEPTED);
		synapseStatusCodesMap.put("UNKNOWN", SYNAPSE_STATUS_UNKNOWN);
		synapseStatusCodesMap.put("REJECTD", SYNAPSE_STATUS_REJECTED);
		synapseStatusCodesMap.put("ENROUTE", SYNAPSE_STATUS_ENROUTE);
		
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
		
		indiaCampTypeMap.put(Constants.SMS_TYPE_NAME_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL+Constants.ADDR_COL_DELIMETER+"N");
		indiaCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"N");
		indiaCampTypeMap.put(Constants.SMS_TYPE_NAME_TRANSACTIONAL, Constants.SMS_TYPE_TRANSACTIONAL+Constants.ADDR_COL_DELIMETER+"Y");
		
		pakistanCampTypeMap.put(Constants.SMS_TYPE_NAME_BROADCAST, Constants.SMS_TYPE_PROMOTIONAL+Constants.ADDR_COL_DELIMETER+"Y");
		pakistanCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"Y");
		
		usCampTypeMap.put(Constants.SMS_TYPE_NAME_OUTBOUND, Constants.SMS_TYPE_OUTBOUND+Constants.ADDR_COL_DELIMETER+"Y");
		usCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"Y");

		canadaCampTypeMap.put(Constants.SMS_TYPE_NAME_OUTBOUND, Constants.SMS_TYPE_OUTBOUND+Constants.ADDR_COL_DELIMETER+"Y");
		canadaCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"Y");

		saCampTypeMap.put(Constants.SMS_TYPE_NAME_OUTBOUND, Constants.SMS_TYPE_OUTBOUND+Constants.ADDR_COL_DELIMETER+"Y");
		saCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"Y");

		//Added for UAE
		uaeCampTypeMap.put(Constants.SMS_TYPE_NAME_BROADCAST, Constants.SMS_TYPE_PROMOTIONAL+Constants.ADDR_COL_DELIMETER+"Y");
		uaeCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"Y");
		uaeCampTypeMap.put(Constants.SMS_TYPE_NAME_TRANSACTIONAL, Constants.SMS_TYPE_TRANSACTIONAL+Constants.ADDR_COL_DELIMETER+"Y");
		
		campTypeMap.put(Constants.SMS_COUNTRY_INDIA, indiaCampTypeMap);
		
		campTypeMap.put(Constants.SMS_COUNTRY_US, usCampTypeMap);
		campTypeMap.put(Constants.SMS_COUNTRY_CANADA, canadaCampTypeMap);
		campTypeMap.put(Constants.SMS_COUNTRY_SA, saCampTypeMap);
		campTypeMap.put(Constants.SMS_COUNTRY_PAKISTAN, pakistanCampTypeMap);
		//Added for UAE
		campTypeMap.put(Constants.SMS_COUNTRY_UAE, uaeCampTypeMap);
	
		indCampValueMap.put(Constants.SMS_TYPE_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
		indCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_PROMOTIONAL);
		indCampValueMap.put(Constants.SMS_TYPE_TRANSACTIONAL, Constants.SMS_TYPE_TRANSACTIONAL);
		indCampValueMap.put(Constants.SMS_SENDING_TYPE_OPTIN, Constants.SMS_SENDING_TYPE_OPTIN);
		
		usCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_OUTBOUND);
		usCampValueMap.put(Constants.SMS_TYPE_OUTBOUND, Constants.SMS_TYPE_OUTBOUND);
		
		pakistanCampValueMap.put(Constants.SMS_TYPE_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
		pakistanCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_PROMOTIONAL);
		//Added for UAE
		uaeCampValueMap.put(Constants.SMS_TYPE_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
		uaeCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_PROMOTIONAL);
		uaeCampValueMap.put(Constants.SMS_TYPE_TRANSACTIONAL, Constants.SMS_TYPE_TRANSACTIONAL);
		
		countryCampValueMap.put(Constants.SMS_COUNTRY_INDIA, indCampValueMap);
		countryCampValueMap.put(Constants.SMS_COUNTRY_US, usCampValueMap);
		countryCampValueMap.put(Constants.SMS_COUNTRY_CANADA, usCampValueMap);
		countryCampValueMap.put(Constants.SMS_COUNTRY_SA, usCampValueMap);
		countryCampValueMap.put(Constants.SMS_COUNTRY_PAKISTAN, pakistanCampValueMap);
		countryCampValueMap.put(Constants.SMS_COUNTRY_UAE, uaeCampValueMap);
		
		defaultAccountMap.put(Constants.SMS_COUNTRY_INDIA, Constants.USER_SMSTOOL_UNICEL+
				Constants.ADDR_COL_DELIMETER+Constants.UNICEL_DEF_USERID+
				Constants.ADDR_COL_DELIMETER+PropertyUtil.getPropertyValueFromDB(Constants.UNICEL_DEF_PWD)+
				Constants.ADDR_COL_DELIMETER+Constants.UNICEL_DEF_SENDERID);
		
		defaultAccountMap.put(Constants.SMS_COUNTRY_US, Constants.USER_SMSTOOL_CLICKATELL+
				Constants.ADDR_COL_DELIMETER+Constants.CLICKATELL_DEF_USERID+
				Constants.ADDR_COL_DELIMETER+PropertyUtil.getPropertyValueFromDB(Constants.CLICKATELL_DEF_PWD)+
				Constants.ADDR_COL_DELIMETER+Constants.CLICKATELL_DEF_SENDERID+
				Constants.ADDR_COL_DELIMETER+Constants.CLICKATELL_DEF_APIID);
		defaultAccountMap.put(Constants.SMS_COUNTRY_CANADA, Constants.USER_SMSTOOL_CLICKATELL+
				Constants.ADDR_COL_DELIMETER+Constants.CLICKATELL_DEF_USERID+
				Constants.ADDR_COL_DELIMETER+PropertyUtil.getPropertyValueFromDB(Constants.CLICKATELL_DEF_PWD)+
				Constants.ADDR_COL_DELIMETER+Constants.CLICKATELL_DEF_SENDERID+
				Constants.ADDR_COL_DELIMETER+Constants.CLICKATELL_DEF_APIID);
		defaultAccountMap.put(Constants.SMS_COUNTRY_SA, Constants.USER_SMSTOOL_CLICKATELL+
				Constants.ADDR_COL_DELIMETER+Constants.CLICKATELL_DEF_USERID+
				Constants.ADDR_COL_DELIMETER+PropertyUtil.getPropertyValueFromDB(Constants.CLICKATELL_DEF_PWD)+
				Constants.ADDR_COL_DELIMETER+Constants.CLICKATELL_DEF_SENDERID+
				Constants.ADDR_COL_DELIMETER+Constants.CLICKATELL_DEF_APIID);
		//keyword response related
		defaultAccountMap.put(Constants.SMS_COUNTRY_UAE, Constants.USER_SMSTOOL_INFOBIP+
				Constants.ADDR_COL_DELIMETER+Constants.INFOBIP_DEF_USERID+
				Constants.ADDR_COL_DELIMETER+PropertyUtil.getPropertyValueFromDB(Constants.INFOBIP_DEF_PWD)+
				Constants.ADDR_COL_DELIMETER+Constants.INFOBIP_DEF_SENDERID+
				Constants.ADDR_COL_DELIMETER+null);

		
		
		keywordRuleMap.put(Constants.SMS_COUNTRY_INDIA, new String[] { "[a-z|A-Z|0-9|\\@|\\.|\\-|\\_|\\s|\\,]*","0","0"," @ - _ . , and white space"});//first param is for regularexpression
		keywordRuleMap.put(Constants.SMS_COUNTRY_US, new String[] { "[a-z|A-Z]+","4","10","only alphabet"});//its mean no need of length restriction
		keywordRuleMap.put(Constants.SMS_COUNTRY_CANADA, new String[] { "[a-z|A-Z]+","4","10","only alphabet"});//APP-3818
		keywordRuleMap.put(Constants.SMS_COUNTRY_SA, new String[] { "[a-z|A-Z]+","4","10","only alphabet"});//APP-3819
		keywordRuleMap.put(Constants.SMS_COUNTRY_UAE, new String[] { "[a-z|A-Z|0-9|\\@|\\.|\\-|\\_|\\s|\\,]*","0","0"," @ - _ . , and white space"});
		
		smsProgramlookupOverUserMap.put(Constants.SMS_COUNTRY_INDIA, false);
		smsProgramlookupOverUserMap.put(Constants.SMS_COUNTRY_US, true);
		smsProgramlookupOverUserMap.put(Constants.SMS_COUNTRY_CANADA, true);
		smsProgramlookupOverUserMap.put(Constants.SMS_COUNTRY_SA, true);
		smsProgramlookupOverUserMap.put(Constants.SMS_COUNTRY_UAE, false);
		
		
		
		Enumeration enm = countryCodes.propertyNames();
		while(enm.hasMoreElements()){
			String currentCountry = (String)enm.nextElement();
			optInMap.put(currentCountry, false);
			optOutFooterMap.put(currentCountry, false);
			userOptinMediumMap.put(currentCountry, true);
			setCountryCode.put(currentCountry, true);
			defaultSMSTypeMap.put(currentCountry, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
			//defaultSMSTypeMap.put(Constants.SMS_COUNTRY_CANADA, Constants.SMS_TYPE_OUTBOUND); //app-3818
			defaultSMSOptinGatewayTypeMap.put(currentCountry, Constants.SMS_TYPE_PROMOTIONAL);
			genericCampTypeMap.put(Constants.SMS_TYPE_NAME_BROADCAST, Constants.SMS_TYPE_PROMOTIONAL+Constants.ADDR_COL_DELIMETER+"Y");
			genericCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"Y");
			campTypeMap.put(currentCountry, genericCampTypeMap);
			//campTypeMap.put(Constants.SMS_COUNTRY_CANADA, canadaCampTypeMap); //app-3818
			
			genericCampValueMap.put(Constants.SMS_TYPE_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
			genericCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_PROMOTIONAL);
			countryCampValueMap.put(currentCountry, genericCampValueMap);
			
			defaultAccountMap.put(currentCountry, Constants.USER_SMSTOOL_INFOBIP+
					Constants.ADDR_COL_DELIMETER+Constants.INFOBIP_DEF_USERID+
					Constants.ADDR_COL_DELIMETER+PropertyUtil.getPropertyValueFromDB(Constants.INFOBIP_DEF_PWD)+
					Constants.ADDR_COL_DELIMETER+Constants.INFOBIP_DEF_SENDERID+
					Constants.ADDR_COL_DELIMETER+null);
			keywordRuleMap.put(currentCountry, new String[] { "[a-z|A-Z|0-9|\\@|\\.|\\-|\\_|\\s|\\,]*","0","0"," @ - _ . , and white space"});
			smsProgramlookupOverUserMap.put(currentCountry, false);
			
		}
		
		
		
}
	
	
	public static final String SMPP_DLR_STATUS_DELIVERED = "Delivered";
	public static final String SMPP_DLR_STATUS_UNKNOWN = "Unknown"; 
	public static final String SMPP_DLR_STATUS_QUEUED = "Pending";
	public static final String SMPP_DLR_STATUS_FAILED = "Failed"; 
	public static final String SMPP_DLR_STATUS_REJECTED = "Rejected"; 
	public static final String SMPP_DLR_STATUS_ACCEPTED = "Accepted"; 
	public static final String SMPP_DLR_STATUS_UNDELIVERABLE = "Undeliverable";
	public static final String SMPP_DLR_STATUS_DELETED = "Deleted"; 
	public static final String SMPP_DLR_STATUS_EXPIRED = "Expired"; 
	
	public static final String SMPP_DLR_STATUS_PENDING_HANDSET_BUSY = "Pending-Handset busy";
	public static final String SMPP_DLR_STATUS_PENDING_SMS_TIMEOUT = "Pending-SMS timeout";
	public static final String SMPP_DLR_STATUS_PENDING_NW_TIMEOUT = "Pending-N/W timeout";
	public static final String SMPP_DLR_STATUS_PENDING_NW_FAIL = "Pending-N/W failure";
	public static final String SMPP_DLR_STATUS_PENDING_OUTOFMEMORY ="Pending-Out of memory";
	
	//Absent
	public static final String SMPP_DLR_STATUS_PENDING_ABSENT_SUBSCRIBER ="Pending-Absent subscriber";





	public static final String SMPP_DLR_STATUS_NDNC_FAILED = "NDNC Rejected";
	public static final String SMPP_DLR_STATUS_BLACKLIST  = "Black-listed";
	public static final String SMPP_DLR_STATUS_WHITELIST  = "White-listed";
	public static final String SMPP_DLR_STATUS_MS_OUTOFMEMORY  = "Out Of Memory";
	public static final String SMPP_DLR_STATUS_NETWORK_FAILURE  = "Network Error";
	public static final String SMPP_DLR_STATUS_PROTOCOL_FAILURE  = "Protocol Error";
	public static final String SMPP_DLR_STATUS_PROVIDER_FAILURE  = "Provider Error";
	public static final String SMPP_DLR_STATUS_TOOMANY_MSGS  = "Delayed Delivery";
	public static final String SMPP_DLR_STATUS_ROAMING_REJECTED  = "Roaming Rejected";
	public static final String SMPP_DLR_STATUS_PREPAID_REJECTED  = "Prepaid Reject"; 

	public static final String SMPP_DLR_STATUS_PROMO_BLOCKED  = "Promo_blocked";
	public static final String SMPP_DLR_STATUS_NIGHT_PROMO_BLOCKED  = "Night Expiry";
	
	
	
	
}
