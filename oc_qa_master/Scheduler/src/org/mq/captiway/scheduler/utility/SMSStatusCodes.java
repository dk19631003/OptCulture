package org.mq.captiway.scheduler.utility;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.mq.captiway.scheduler.dao.CountryCodeDao;

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
	public static final String CLICKATELL_STATUS_RECEIVED_BY_GATEWAY ="Received by gateway";
	public static final String CLICKATELL_STATUS_ROUTING_ERROR ="Routing error";
	public static final String CLICKATELL_STATUS_MESSAGE_EXPIRED ="Expired";
	public static final String CLICKATELL_STATUS_DELIVERY_DELAYED ="delayed delivery";
	public static final String CLICKATELL_STATUS_OUT_OF_CREDITS ="Out of credit";
	public static final String CLICKATELL_STATUS_NO_CREDITS = "No credit left";
	public static final String CLICKATELL_STATUS_MAX_ALLOWED_CREDITS = "Max allowed credit";
	public static final String MVAYOO_STATUS_DUPLICATE = "Duplicate";
	public static final String MVAYOO_STATUS_PENDING = "Pending";
	public static final String MVAYOO_STATUS_CANCELLED = "Cancelled";
	public static final String MVAYOO_STATUS_DND_INVALID_10_DIGIT = "NDNC / Invalid 10 digit number ";

	 public static final String INFOCOMM_REPORTS_DELIVERED ="Delivered";
	 public static final String INFOCOMM_REPORTS_USERID_OR_PASSWORD_INCORRECT ="User Id or Password is Incorrect";
	 public static final String INFOCOMM_REPORTS_UNKNOWN_ERROR ="Unknown Error";
	 public static final String INFOCOMM_REPORTS_DELIVERY_RECEIPT_NOT_ENABLED ="Delivery Receipt not enabled";
	 public static final String INFOCOMM_REPORTS__REFERENCE_ID_NULL_BLANK ="Reference ID can’t be null or blank";
	 public static final String INFOCOMM_REPORTS_NO_RECORD_FOUND ="No Record Found";
	 public static final String INFOCOMM_REPORTS_FAILED="failed";
	
	public static final String CLICKATELL_STATUS_OPTED_OUT ="Opted-out";
	public static final String CLICKATELL_STATUS_INVALID_NUMBER ="Invalid Number";
	public static final String CLICKATELL_STATUS_BLOCKED ="Blocked";
	public static final String CLICKATELL_STATUS_OTHER = "Others";
	public static final String CLICKATELL_STATUS_CANCELLED_MESSAGE_DELIVERY = "Clickatell cancelled message delivery";
	public static final String CLICKATELL_MAXIMIM_MT_LIMIT_EXCEEDED = "Maximum MT limit exceeded";
	public static final String CLICKATELL_STATUS_MESSAGE_PARTS_EXCEEDED = "Maximum message parts exceeded";
	
	public static final String CLICKATELL_STATUS_CAN_NOT_ROUTE_MESSAGE = "Cannot route message";
	public static final String CLICKATELL_STATUS_NUMBER_DELISTED = "Number delisted";
	
	public static final String CLICKATELL_STATUS_BOUNCED = "Bounced";
	
	public static final Map<String,String> clickaTellStatusCodesMap = new HashMap<String, String>();
	public static final Map<String,String> clickaTellStatusMessagesMap = new HashMap<String, String>();
	public static final Map<String,String> infocommStatusCodesMap = new HashMap<String, String>();
	
	public static Set<String> DeliveredSet = new HashSet<String>();
	public static Set<String> BouncedSet = new HashSet<String>();
	public static Set<String> SuppressedSet = new HashSet<String>();
	
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


	public static final String MB_DLR_STATUS_DELIVERED = "Delivered";
	public static final String MB_DLR_STATUS_BOUNCED = "Bounced";


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
	
	
	
	//Added for INFOBIP_PERMANANT 
	//Permanent Status 
	public static final String INFOBIP_PERMANANT_DELIVERD = "Delivered";
	public static final String INFOBIP_PERMANANT_EC_UNKNOWN_SUBSCRIBER= "Unknown";
	public static final String INFOBIP_PERMANANT_EC_ILLEGAL_SUBSCRIBER = "Unknown";
	public static final String INFOBIP_PERMANANT_EC_TELESERVICE_NOT_PROVISIONED= "Unknown";
	public static final String INFOBIP_PERMANANT_EC_ILLEGAL_EQUIPMENT = "Unknown";
	public static final String INFOBIP_PERMANANT_EC_DATA_MISSING ="Unknown";
	public static final String INFOBIP_PERMANANT_EC_UNEXPECTED_DATA_VALUE="Protocol Error";
	public static final String INFOBIP_PERMANANT_EC_IMSI_BLACKLISTED = "Black-listed";
	public static final String INFOBIP_PERMANANT_EC_DEST_ADDRESS_BLACKLISTED="Black-listed";
	public static final String INFOBIP_PERMANANT_EC_INVALIDPDUFORMAT="Protocol Error";
	public static final String INFOBIP_PERMANANT_EC_CANCELLED="Pending";
	public static final String INFOBIP_PERMANANT_EC_VALIDITYEXPIRED="Expired";
	public static final String INFOBIP_PERMANANT_EC_BEARER_SERVICE_NOT_PROVISIONED="Bearer Service Not Provisioned";
	public static final String INFOBIP_PERMANANT_EC_RESOURCE_LIMITATION="Resource Limitation";
	public static final String INFOBIP_PERMANANT_EC_USSD_BUSY = "Ussd Busy";
	
	public static final String INFOBIP_PERMANANT_EC_NOTSUBMITTEDTOSMPPCHANNEL="Not Submitted To Smpp Channel";
	public static final String INFOBIP_PERMANANT_VOICE_ANSWERED="Call answered by human";
	public static final String INFOBIP_PERMANANT_VOICE_ANSWERED_MACHINE="Call answered by machine";
	public static final String INFOBIP_PERMANANT_EC_VOICE_USER_BUSY="User was busy during call attempt";
	public static final String INFOBIP_PERMANANT_EC_VOICE_NO_ANSWER="User was notified, but did not answer call";
	public static final String INFOBIP_PERMANANT_EC_VOICE_ERROR_DOWNLOADING_FILE="File provided for call could not be downloaded";
	public static final String INFOBIP_PERMANANT_EC_VOICE_ERROR_UNSUPPORTED_AUDIO_FORMAT="Format of file provided for call is not supported";
	
	
	
	//Temporary Status
	
	public static final String INFOBIP_TEMPORARY_EC_UNIDENTIFIED_SUBSCRIBER="Pending-Absent subscriber";
	public static final String INFOBIP_TEMPORARY_EC_ABSENT_SUBSCRIBER_SM="Pending-Absent subscriber";
	public static final String INFOBIP_TEMPORARY_EC_CALL_BARRED="Roaming Rejected";
	public static final String INFOBIP_TEMPORARY_EC_FACILITY_NOT_SUPPORTED="Unknown";
	public static final String INFOBIP_TEMPORARY_EC_ABSENT_SUBSCRIBER="Pending-Absent subscriber";
	public static final String INFOBIP_TEMPORARY_EC_SUBSCRIBER_BUSY_FOR_MT_SMS="Out Of Memory";
	public static final String INFOBIP_TEMPORARY_EC_SM_DELIVERY_FAILURE="Undeliverable";
	public static final String INFOBIP_TEMPORARY_EC_MESSAGE_WAITING_LIST_FULL="Delayed Delivery";
	public static final String INFOBIP_TEMPORARY_EC_SYSTEM_FAILURE="Failed";
	public static final String INFOBIP_TEMPORARY_EC_SM_DF_MEMORYCAPACITYEXCEEDED="Out Of Memory";
	public static final String INFOBIP_TEMPORARY_EC_SM_DF_EQUIPMENTPROTOCOLERROR="Protocol Error";
	public static final String INFOBIP_TEMPORARY_EC_SM_DF_EQUIPMENTNOTSM_EQUIPPED="Delayed Delivery";
	public static final String INFOBIP_TEMPORARY_EC_SM_DF_UNKNOWNSERVICECENTRE="Undeliverable";
	public static final String INFOBIP_TEMPORARY_EC_SM_DF_SC_CONGESTION ="Delayed Delivery";
	public static final String INFOBIP_TEMPORARY_EC_SM_DF_INVALIDSME_ADDRESS="Protocol Error";
	public static final String INFOBIP_TEMPORARY_EC_SM_DF_SUBSCRIBERNOTSC_SUBSCRIBER="Delayed Delivery";
	public static final String INFOBIP_TEMPORARY_EC_PROVIDER_GENERAL_ERROR="Provider Error";
	public static final String INFOBIP_TEMPORARY_EC_NO_RESPONSE="Unknown";
	public static final String INFOBIP_TEMPORARY_EC_SERVICE_COMPLETION_FAILURE="Failed";
	public static final String INFOBIP_TEMPORARY_EC_UNEXPECTED_RESPONSE_FROM_PEER="Failed";
	public static final String INFOBIP_TEMPORARY_EC_MISTYPED_PARAMETER="Unknown";
	public static final String INFOBIP_TEMPORARY_EC_NOT_SUPPORTED_SERVICE="Provider Error";
	public static final String INFOBIP_TEMPORARY_EC_DUPLICATED_INVOKE_ID="Unknown";
	public static final String INFOBIP_TEMPORARY_EC_INITIATING_RELEASE="Unknown";
	public static final String INFOBIP_TEMPORARY_EC_OR_APPCONTEXTNOTSUPPORTED="Unknown";
	public static final String INFOBIP_TEMPORARY_EC_OR_INVALIDDESTINATIONREFERENCE="Undeliverable";
	public static final String INFOBIP_TEMPORARY_EC_OR_INVALIDORIGINATINGREFERENCE="Undeliverable";
	public static final String INFOBIP_TEMPORARY_EC_OR_ENCAPSULATEDAC_NOTSUPPORTED="Undeliverable";
	public static final String INFOBIP_TEMPORARY_EC_OR_TRANSPORTPROTECTIONNOTADEQUATE="Protocol Error";
	public static final String INFOBIP_TEMPORARY_EC_OR_POTENTIALVERSIONINCOMPATIBILITY="Protocol Error";
	public static final String INFOBIP_TEMPORARY_EC_OR_REMOTENODENOTREACHABLE="Undeliverable";
	public static final String INFOBIP_TEMPORARY_EC_NNR_NOTRANSLATIONFORANADDRESSOFSUCHNATUR="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_NOTRANSLATIONFORTHISSPECIFICADDRESS="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_SUBSYSTEMCONGESTION="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_SUBSYSTEMFAILURE="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_UNEQUIPPEDUSER="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_MTPFAILURE="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_NETWORKCONGESTION="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_UNQUALIFIED="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_ERRORINMESSAGETRANSPORTXUDT="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_ERRORINLOCALPROCESSINGXUDT="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_DESTINATIONCANNOTPERFORMREASSEMBLYXUDT="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_SCCPfailure	="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_HOPCOUNTERVIOLATION	="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_SEGMENTATIONNOTSUPPORTED="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_NNR_SEGMENTATIONFAILURE="Network Error";
	public static final String INFOBIP_TEMPORARY_EC_UA_USERSPECIFICREASON="Unknown";
	public static final String INFOBIP_TEMPORARY_EC_UA_USERRESOURCELIMITATION="Provider Error";
	public static final String INFOBIP_TEMPORARY_EC_UA_RESOURCEUNAVAILABLE="Provider Error";
	public static final String INFOBIP_TEMPORARY_EC_UA_APPLICATIONPROCEDURECANCELLATION="Provider Error";
	public static final String INFOBIP_TEMPORARY_EC_PA_PROVIDERMALFUNCTION="Protocol Error";
	public static final String INFOBIP_TEMPORARY_EC_PA_SUPPORTINGDIALOGORTRANSACTIONREALEASED="Protocol Error";
	public static final String INFOBIP_TEMPORARY_EC_PA_RESSOURCELIMITATION="Protocol Error";
	public static final String INFOBIP_TEMPORARY_EC_PA_MAINTENANCEACTIVITY="Protocol Error";
	public static final String INFOBIP_TEMPORARY_EC_PA_VERSIONINCOMPATIBILITY="Protocol Error";
	public static final String INFOBIP_TEMPORARY_EC_PA_ABNORMALMAPDIALOG="Protocol Error";
	public static final String INFOBIP_TEMPORARY_EC_NC_RESPONSEREJECTEDBYPEER="Undeliverable";
	public static final String INFOBIP_TEMPORARY_EC_NC_ABNORMALEVENTRECEIVEDFROMPEER="Unknown";
	public static final String INFOBIP_TEMPORARY_EC_NC_MESSAGECANNOTBEDELIVEREDTOPEER="Unknown";
	public static final String INFOBIP_TEMPORARY_EC_NC_PROVIDEROUTOFINVOKE ="Provider Error";
	public static final String INFOBIP_TEMPORARY_EC_TIME_OUT="Delayed Delivery";
	public static final String INFOBIP_TEMPORARY_EC_INVALIDMSCADDRESS="Provider Error";
	public static final String INFOBIP_TEMPORARY_EC_SS_INCOMPATIBILITY="SS Incompatibility";
	public static final String INFOBIP_TEMPORARY_EC_UNKNOWN_ALPHABET="Unknown Alphabet";
	public static final String INFOBIP_TEMPORARY_EC_INVALID_RESPONSE_RECEIVED="Invalid Response Received";
	public static final String INFOBIP_TEMPORARY_EC_OR_NOREASONGIVEN="No Reason Given";
	public static final String INFOBIP_TEMPORARY_EC_NC_ABNORMALEVENTDETECTEDBYPEER="Abnormal Event Detected By Peer";
	public static final String INFOBIP_TEMPORARY_EC_NOTSUBMITTEDTOGMSC="Not Submitted To GMSC";
	public static final String INFOBIP_TEMPORARY_EC_UNKNOWN_ERROR="Unknown Error";
	
	
	
	
	
	
	
	
	
	
	
	
	public static final Map<String, Boolean> optInMap = new HashMap<String, Boolean>();
	public static final Map<String, Boolean> optOutFooterMap = new HashMap<String, Boolean>();
	
	public static final Map<String, Boolean> checkGlobalOptOutMap = new HashMap<String, Boolean>();
	public static final Map<String, String> defaultAccountMap = new HashMap<String, String>();
	public static final Map<String, String> optInTypeMap = new HashMap<String, String>();
	public static final Map<String, Boolean> userOptinMediumMap = new HashMap<String, Boolean>();
	public static final Map<String, String> defaultSMSTypeMap = new HashMap<String, String>();
	public static final Map<String, String> defaultSMSOptinGatewayTypeMap = new HashMap<String, String>();
	 
	public static final Map<String, Map<String, String>> countryCampValueMap = new HashMap<String, Map<String, String>>();
	public static final Map<String, String> indCampValueMap = new HashMap<String, String>();
	public static final Map<String, String> usCampValueMap = new HashMap<String, String>();
	public static final Map<String, String> canadaCampValueMap = new HashMap<String, String>();
	public static final Map<String, String> saCampValueMap = new HashMap<String, String>();//APP-3819
	public static final Map<String, String> singaporeCampValueMap = new HashMap<String, String>();//APP-4688

	public static final Map<String, Boolean> smsProgramlookupOverUserMap = new HashMap<String, Boolean>();
	public static final Map<String, String> pakistanCampValueMap = new HashMap<String, String>();
	//Added for UAE
	public static final Map<String, String> uaeCampValueMap = new HashMap<String, String>();
	public static Properties countryCodes;
	public static CountryCodeDao countryCodeDao;
	public static final Map<String, String> genericCampTypeMap = new LinkedHashMap<String, String>();
	public static final Map<String, String> genericCampValueMap = new HashMap<String, String>();
	
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
		optInMap.put(Constants.SMS_COUNTRY_SINGAPORE, false);//APP-4688
		optInMap.put(Constants.SMS_COUNTRY_PAKISTAN, false);
		optInMap.put(Constants.SMS_COUNTRY_UAE, false);
		
		optOutFooterMap.put(Constants.SMS_COUNTRY_INDIA, false);
		optOutFooterMap.put(Constants.SMS_COUNTRY_US, true);
		optOutFooterMap.put(Constants.SMS_COUNTRY_CANADA, true);
		optOutFooterMap.put(Constants.SMS_COUNTRY_SA, true);
		optOutFooterMap.put(Constants.SMS_COUNTRY_SINGAPORE, true);//APP-4688
		optOutFooterMap.put(Constants.SMS_COUNTRY_PAKISTAN, false);
		optOutFooterMap.put(Constants.SMS_COUNTRY_UAE,true);
		
		//optin keyword response related
		userOptinMediumMap.put(Constants.SMS_COUNTRY_INDIA, true);
		userOptinMediumMap.put(Constants.SMS_COUNTRY_US, false);
		userOptinMediumMap.put(Constants.SMS_COUNTRY_CANADA, false);
		userOptinMediumMap.put(Constants.SMS_COUNTRY_SA, false);
		userOptinMediumMap.put(Constants.SMS_COUNTRY_SINGAPORE, false);//APP-4688
		userOptinMediumMap.put(Constants.SMS_COUNTRY_PAKISTAN, false);
		userOptinMediumMap.put(Constants.SMS_COUNTRY_UAE, false);
		
		//keyword response related
		checkGlobalOptOutMap.put(Constants.SMS_COUNTRY_INDIA, false);
		checkGlobalOptOutMap.put(Constants.SMS_COUNTRY_US, true);
		checkGlobalOptOutMap.put(Constants.SMS_COUNTRY_PAKISTAN, false);
		checkGlobalOptOutMap.put(Constants.SMS_COUNTRY_UAE, false);
		//keyword response related
		defaultAccountMap.put(Constants.SMS_COUNTRY_INDIA, Constants.USER_SMSTOOL_UNICEL+
				Constants.ADDR_COL_DELIMETER+Constants.UNICEL_DEF_USERID+
				Constants.ADDR_COL_DELIMETER+PropertyUtil.getPropertyValueFromDB(Constants.UNICEL_DEF_PWD)+
				Constants.ADDR_COL_DELIMETER+Constants.UNICEL_DEF_SENDERID);
		//keyword response related
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
		defaultAccountMap.put(Constants.SMS_COUNTRY_SINGAPORE, Constants.USER_SMSTOOL_CLICKATELL+
				Constants.ADDR_COL_DELIMETER+Constants.CLICKATELL_DEF_USERID+
				Constants.ADDR_COL_DELIMETER+PropertyUtil.getPropertyValueFromDB(Constants.CLICKATELL_DEF_PWD)+
				Constants.ADDR_COL_DELIMETER+Constants.CLICKATELL_DEF_SENDERID+
				Constants.ADDR_COL_DELIMETER+Constants.CLICKATELL_DEF_APIID);//APP-4688
		//keyword response related
		defaultAccountMap.put(Constants.SMS_COUNTRY_UAE, Constants.USER_SMSTOOL_INFOBIP+
				Constants.ADDR_COL_DELIMETER+Constants.INFOBIP_DEF_USERID+
				Constants.ADDR_COL_DELIMETER+PropertyUtil.getPropertyValueFromDB(Constants.INFOBIP_DEF_PWD)+
				Constants.ADDR_COL_DELIMETER+Constants.INFOBIP_DEF_SENDERID+
				Constants.ADDR_COL_DELIMETER+null);
		
		
		
		
		
		//keyword response related
		optInTypeMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_ACCOUNT_TYPE_OPTIN);
		
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_US, Constants.SMS_TYPE_OUTBOUND);
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_CANADA, Constants.SMS_TYPE_OUTBOUND);
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_SA, Constants.SMS_TYPE_OUTBOUND);
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_SINGAPORE, Constants.SMS_TYPE_OUTBOUND);//APP-4688
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_PAKISTAN, Constants.SMS_TYPE_PROMOTIONAL);
		defaultSMSTypeMap.put(Constants.SMS_COUNTRY_UAE, Constants.SMS_TYPE_PROMOTIONAL);
		
		//keyword response related
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_INDIA, Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL);
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_US, Constants.SMS_TYPE_OUTBOUND);
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_CANADA, Constants.SMS_TYPE_OUTBOUND);
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_SA, Constants.SMS_TYPE_OUTBOUND);
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_SINGAPORE, Constants.SMS_TYPE_OUTBOUND);//APP-4688
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_PAKISTAN, Constants.SMS_TYPE_PROMOTIONAL);
		defaultSMSOptinGatewayTypeMap.put(Constants.SMS_COUNTRY_UAE,  Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL);
		
	//TODO need to get username,password,feedid from DB
		clickaTellStatusCodesMap.put("001",CLICKATELL_STATUS_REPORTDELAY);
		clickaTellStatusCodesMap.put("002",CLICKATELL_STATUS_QUEUED);
		clickaTellStatusCodesMap.put("003",CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT);
		clickaTellStatusCodesMap.put("004",CLICKATELL_STATUS_RECEIVED);
		clickaTellStatusCodesMap.put("005",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("006",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("007",CLICKATELL_STATUS_DELIVERY_ERROR);
		clickaTellStatusCodesMap.put("008",CLICKATELL_STATUS_RECEIVED_BY_GATEWAY);
		clickaTellStatusCodesMap.put("009",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("010",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("011",CLICKATELL_STATUS_DELIVERY_DELAYED);
		clickaTellStatusCodesMap.put("012",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("013",CLICKATELL_STATUS_CANCELLED_MESSAGE_DELIVERY);
		clickaTellStatusCodesMap.put("014",CLICKATELL_MAXIMIM_MT_LIMIT_EXCEEDED);
		clickaTellStatusCodesMap.put("105",CLICKATELL_STATUS_INVALID_NUMBER);
		clickaTellStatusCodesMap.put("113",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("114",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("121",CLICKATELL_STATUS_BLOCKED);
		clickaTellStatusCodesMap.put("122",CLICKATELL_STATUS_OPTED_OUT);
		clickaTellStatusCodesMap.put("128",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("301",CLICKATELL_STATUS_OTHER);
		clickaTellStatusCodesMap.put("302",CLICKATELL_STATUS_OTHER);
		
		
		clickaTellStatusMessagesMap.put("001",CLICKATELL_STATUS_REPORTDELAY);
		clickaTellStatusMessagesMap.put("002",CLICKATELL_STATUS_QUEUED);
		clickaTellStatusMessagesMap.put("003",CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT);
		clickaTellStatusMessagesMap.put("004",CLICKATELL_STATUS_RECEIVED);
		clickaTellStatusMessagesMap.put("005", CLICKATELL_STATUS_MESSAGE_ERROR);
		clickaTellStatusMessagesMap.put("006", CLICKATELL_STATUS_USER_CANCELLED);
		clickaTellStatusMessagesMap.put("007",CLICKATELL_STATUS_DELIVERY_ERROR);
		clickaTellStatusMessagesMap.put("008",CLICKATELL_STATUS_RECEIVED_BY_GATEWAY);
		clickaTellStatusMessagesMap.put("009", CLICKATELL_STATUS_ROUTING_ERROR);
		clickaTellStatusMessagesMap.put("010", CLICKATELL_STATUS_MESSAGE_EXPIRED);
		clickaTellStatusMessagesMap.put("011",CLICKATELL_STATUS_DELIVERY_DELAYED);
		clickaTellStatusMessagesMap.put("012", CLICKATELL_STATUS_OUT_OF_CREDITS);
		clickaTellStatusMessagesMap.put("013",CLICKATELL_STATUS_CANCELLED_MESSAGE_DELIVERY);
		clickaTellStatusMessagesMap.put("014",CLICKATELL_MAXIMIM_MT_LIMIT_EXCEEDED);
		clickaTellStatusMessagesMap.put("105",CLICKATELL_STATUS_INVALID_NUMBER);
		clickaTellStatusMessagesMap.put("113", CLICKATELL_STATUS_MESSAGE_PARTS_EXCEEDED );
		clickaTellStatusMessagesMap.put("114", CLICKATELL_STATUS_CAN_NOT_ROUTE_MESSAGE);
		clickaTellStatusMessagesMap.put("121",CLICKATELL_STATUS_BLOCKED);
		clickaTellStatusMessagesMap.put("122",CLICKATELL_STATUS_OPTED_OUT);
		clickaTellStatusMessagesMap.put("128", CLICKATELL_STATUS_NUMBER_DELISTED);
		clickaTellStatusMessagesMap.put("301", CLICKATELL_STATUS_NO_CREDITS);
		clickaTellStatusMessagesMap.put("302", CLICKATELL_STATUS_MAX_ALLOWED_CREDITS);
		
		infocommStatusCodesMap.put("delivered", INFOCOMM_REPORTS_DELIVERED);
		infocommStatusCodesMap.put("3", INFOCOMM_REPORTS_USERID_OR_PASSWORD_INCORRECT);
		infocommStatusCodesMap.put("11", INFOCOMM_REPORTS_UNKNOWN_ERROR);
		infocommStatusCodesMap.put("31", INFOCOMM_REPORTS_DELIVERY_RECEIPT_NOT_ENABLED);
		infocommStatusCodesMap.put("32", INFOCOMM_REPORTS__REFERENCE_ID_NULL_BLANK);
		infocommStatusCodesMap.put("33", INFOCOMM_REPORTS_NO_RECORD_FOUND);
		infocommStatusCodesMap.put("failed", INFOCOMM_REPORTS_FAILED);
		
		indCampValueMap.put(Constants.SMS_TYPE_TRANSACTIONAL, Constants.SMS_TYPE_TRANSACTIONAL);
		indCampValueMap.put(Constants.SMS_TYPE_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
		indCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_PROMOTIONAL);
		indCampValueMap.put(Constants.SMS_SENDING_TYPE_OPTIN, Constants.SMS_SENDING_TYPE_OPTIN);
		
		usCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_OUTBOUND);
		usCampValueMap.put(Constants.SMS_TYPE_OUTBOUND, Constants.SMS_TYPE_OUTBOUND);
		
		// added for canada integration
		canadaCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_OUTBOUND);
		canadaCampValueMap.put(Constants.SMS_TYPE_OUTBOUND, Constants.SMS_TYPE_OUTBOUND);
		
		saCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_OUTBOUND);
		saCampValueMap.put(Constants.SMS_TYPE_OUTBOUND, Constants.SMS_TYPE_OUTBOUND);
		
		singaporeCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_OUTBOUND);//APP-4688
		singaporeCampValueMap.put(Constants.SMS_TYPE_OUTBOUND, Constants.SMS_TYPE_OUTBOUND);

		pakistanCampValueMap.put(Constants.SMS_TYPE_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
		pakistanCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_PROMOTIONAL);
		//Added for UAE
		uaeCampValueMap.put(Constants.SMS_TYPE_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
		uaeCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_PROMOTIONAL);
		uaeCampValueMap.put(Constants.SMS_TYPE_TRANSACTIONAL, Constants.SMS_TYPE_TRANSACTIONAL);
		
		
		DeliveredSet.add(CLICKATELL_STATUS_RECEIVED);
		DeliveredSet.add(CLICKATELL_STATUS_RECEIVED_BY_GATEWAY);
		DeliveredSet.add(CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT);
		
		BouncedSet.add(CLICKATELL_STATUS_BLOCKED);
		BouncedSet.add(CLICKATELL_STATUS_INVALID_NUMBER);
		BouncedSet.add(CLICKATELL_STATUS_DELIVERY_ERROR);
		BouncedSet.add(CLICKATELL_STATUS_OTHER);
		BouncedSet.add(CLICKATELL_STATUS_CANCELLED_MESSAGE_DELIVERY);
		BouncedSet.add(CLICKATELL_MAXIMIM_MT_LIMIT_EXCEEDED);
		BouncedSet.add(CLICKATELL_STATUS_ROUTING_ERROR);
		
		SuppressedSet.add(CLICKATELL_STATUS_INVALID_NUMBER);
		SuppressedSet.add(CLICKATELL_STATUS_NUMBER_DELISTED);
		SuppressedSet.add(CLICKATELL_STATUS_OPTED_OUT);
		SuppressedSet.add(CLICKATELL_STATUS_ROUTING_ERROR);
		
		countryCampValueMap.put(Constants.SMS_COUNTRY_INDIA, indCampValueMap);
		countryCampValueMap.put(Constants.SMS_COUNTRY_US, usCampValueMap);
		countryCampValueMap.put(Constants.SMS_COUNTRY_CANADA, canadaCampValueMap);
		countryCampValueMap.put(Constants.SMS_COUNTRY_SA, saCampValueMap);
		countryCampValueMap.put(Constants.SMS_COUNTRY_SINGAPORE, singaporeCampValueMap);//APP-4688
		countryCampValueMap.put(Constants.SMS_COUNTRY_PAKISTAN, pakistanCampValueMap);
		
		//Added for UAE
		countryCampValueMap.put(Constants.SMS_COUNTRY_UAE, uaeCampValueMap);
		
		
		smsProgramlookupOverUserMap.put(Constants.SMS_COUNTRY_INDIA, false);
		smsProgramlookupOverUserMap.put(Constants.SMS_COUNTRY_US, true);
		smsProgramlookupOverUserMap.put(Constants.SMS_COUNTRY_CANADA, true);

		
		
		
		Enumeration enm = countryCodes.propertyNames();
		while(enm.hasMoreElements()){
			String currentCountry = (String)enm.nextElement();
			
			optInMap.put(currentCountry, false);
			
			optOutFooterMap.put(currentCountry,false);
			
			userOptinMediumMap.put(currentCountry, false);
			
			checkGlobalOptOutMap.put(currentCountry, false);
			
			defaultAccountMap.put(currentCountry, Constants.USER_SMSTOOL_INFOBIP+
					Constants.ADDR_COL_DELIMETER+Constants.INFOBIP_DEF_USERID+
					Constants.ADDR_COL_DELIMETER+PropertyUtil.getPropertyValueFromDB(Constants.INFOBIP_DEF_PWD)+
					Constants.ADDR_COL_DELIMETER+Constants.INFOBIP_DEF_SENDERID+
					Constants.ADDR_COL_DELIMETER+null);
			defaultSMSTypeMap.put(currentCountry, Constants.SMS_TYPE_PROMOTIONAL);
			defaultSMSOptinGatewayTypeMap.put(currentCountry,  Constants.SMS_TYPE_PROMOTIONAL);
			
			genericCampValueMap.put(Constants.SMS_TYPE_PROMOTIONAL, Constants.SMS_TYPE_PROMOTIONAL);
			genericCampValueMap.put(Constants.SMS_TYPE_2_WAY, Constants.SMS_TYPE_PROMOTIONAL);
			
			
			countryCampValueMap.put(currentCountry, genericCampValueMap);
		}
		
		
		
		
	
}
	
	
	
	
	
	
}
