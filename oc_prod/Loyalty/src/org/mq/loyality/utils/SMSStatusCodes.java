package org.mq.loyality.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.dao.CountryCodeDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SMSStatusCodes {
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
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
	
	public static final Map<String,String> clickaTellStatusCodesMap = new HashMap<String, String>();
	public static final Map<String,String> clickaTellStatusMessagesMap = new HashMap<String, String>();
	
	public static final Map<String, String> usCampTypeMap = new LinkedHashMap<String, String>();
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
	
	//@Autowired
	//public static  CountryCodeDao countryCodeDao;
	//public static Properties countryCodes;
	public static final Map<String, String> genericCampValueMap = new HashMap<String, String>();
	public static final Map<String, String> genericCampTypeMap = new LinkedHashMap<String, String>();
	public static final Map<String, Boolean> smsProgramlookupOverUserMap = new HashMap<String, Boolean>();
	
	static {
		
		try {
			optInMap.put(Constants.SMS_COUNTRY_INDIA, true);
			optInMap.put(Constants.SMS_COUNTRY_US, false);
			optInMap.put(Constants.SMS_COUNTRY_PAKISTAN, false);
			//Added for UAE
			optInMap.put(Constants.SMS_COUNTRY_UAE, false);
			
			optOutFooterMap.put(Constants.SMS_COUNTRY_INDIA, false);
			optOutFooterMap.put(Constants.SMS_COUNTRY_US, true);
			optOutFooterMap.put(Constants.SMS_COUNTRY_PAKISTAN, false);
			//Added for UAE
			optOutFooterMap.put(Constants.SMS_COUNTRY_UAE, false);
			
			userOptinMediumMap.put(Constants.SMS_COUNTRY_INDIA, true);
			userOptinMediumMap.put(Constants.SMS_COUNTRY_US, false);
			userOptinMediumMap.put(Constants.SMS_COUNTRY_PAKISTAN, true);//TODO
			//Added for UAE
			userOptinMediumMap.put(Constants.SMS_COUNTRY_UAE, true);
			
			
			setCountryCode.put(Constants.SMS_COUNTRY_INDIA, true);
			setCountryCode.put(Constants.SMS_COUNTRY_US, false);
			setCountryCode.put(Constants.SMS_COUNTRY_PAKISTAN, true);
			//Added for UAE
			setCountryCode.put(Constants.SMS_COUNTRY_UAE, true);
			
			optInTypeMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_ACCOUNT_TYPE_OPTIN);
			
			defaultSMSTypeMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
			defaultSMSTypeMap.put(Constants.SMS_COUNTRY_US, Constants.SMS_TYPE_OUTBOUND);
			defaultSMSTypeMap.put(Constants.SMS_COUNTRY_PAKISTAN, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
			//Added for UAE
			defaultSMSTypeMap.put(Constants.SMS_COUNTRY_UAE, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);// uae added 16th june
			
			defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL);
			defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_US, Constants.SMS_TYPE_OUTBOUND);
			defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_PAKISTAN, Constants.SMS_TYPE_PROMOTIONAL);
			//Added for UAE
			defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_UAE, Constants.SMS_TYPE_PROMOTIONAL);
			
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
			
			indiaCampTypeMap.put(Constants.SMS_TYPE_NAME_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL+Constants.ADDR_COL_DELIMETER+"N");
			indiaCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"N");
			indiaCampTypeMap.put(Constants.SMS_TYPE_NAME_TRANSACTIONAL, Constants.SMS_TYPE_TRANSACTIONAL+Constants.ADDR_COL_DELIMETER+"Y");
			
			pakistanCampTypeMap.put(Constants.SMS_TYPE_NAME_BROADCAST, Constants.SMS_TYPE_PROMOTIONAL+Constants.ADDR_COL_DELIMETER+"Y");
			pakistanCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"Y");
			
			usCampTypeMap.put(Constants.SMS_TYPE_NAME_OUTBOUND, Constants.SMS_TYPE_OUTBOUND+Constants.ADDR_COL_DELIMETER+"Y");
			usCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"Y");
			
			//Added for UAE
			uaeCampTypeMap.put(Constants.SMS_TYPE_NAME_BROADCAST, Constants.SMS_TYPE_PROMOTIONAL+Constants.ADDR_COL_DELIMETER+"Y");
			uaeCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"Y");
			
			campTypeMap.put(Constants.SMS_COUNTRY_INDIA, indiaCampTypeMap);
			
			campTypeMap.put(Constants.SMS_COUNTRY_US, usCampTypeMap);
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
			
			countryCampValueMap.put(Constants.SMS_COUNTRY_INDIA, indCampValueMap);
			countryCampValueMap.put(Constants.SMS_COUNTRY_US, usCampValueMap);
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
			
			keywordRuleMap.put(Constants.SMS_COUNTRY_INDIA, new String[] { "[a-z|A-Z|0-9|\\@|\\.|\\-|\\_|\\s|\\,]*","0","0"," @ - _ . , and white space"});//first param is for regularexpression
			keywordRuleMap.put(Constants.SMS_COUNTRY_US, new String[] { "[a-z|A-Z]+","4","10","only alphabet"});//its mean no need of length restriction
			
			smsProgramlookupOverUserMap.put(Constants.SMS_COUNTRY_INDIA, false);
			smsProgramlookupOverUserMap.put(Constants.SMS_COUNTRY_US, true);
			
			
			
			
			/*countryCodes = countryCodeDao.findAllCountryCodes();
			Enumeration enm = countryCodes.propertyNames();
			while(enm.hasMoreElements()){
				String currentCountry = (String)enm.nextElement();
				optInMap.put(currentCountry, false);
				optOutFooterMap.put(currentCountry, false);
				userOptinMediumMap.put(currentCountry, true);
				setCountryCode.put(currentCountry, true);
				defaultSMSTypeMap.put(currentCountry, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
				defaultSMSOptinGatewayTypeMap.put(currentCountry, Constants.SMS_TYPE_PROMOTIONAL);
				genericCampTypeMap.put(Constants.SMS_TYPE_NAME_BROADCAST, Constants.SMS_TYPE_PROMOTIONAL+Constants.ADDR_COL_DELIMETER+"Y");
				genericCampTypeMap.put(Constants.SMS_TYPE_NAME_2_WAY, Constants.SMS_TYPE_2_WAY+Constants.ADDR_COL_DELIMETER+"Y");
				campTypeMap.put(currentCountry, genericCampTypeMap);
				
				genericCampValueMap.put(Constants.SMS_TYPE_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
				genericCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_PROMOTIONAL);
				countryCampValueMap.put(currentCountry, genericCampValueMap);
				
				
				
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Statuscodes exception ", e);
		}
		
		
		
}
	
	
	
	
	
	
}
