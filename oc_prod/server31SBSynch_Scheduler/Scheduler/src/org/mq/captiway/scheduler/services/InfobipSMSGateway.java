/**
 * 
 */
package org.mq.captiway.scheduler.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.SMSBounces;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSSuppressedContacts;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.SMSBouncesDao;
import org.mq.captiway.scheduler.dao.SMSBouncesDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.service.GatewaySessionProvider;
import org.mq.optculture.utils.ServiceLocator;
import org.smpp.Connection;
import org.smpp.ServerPDUEvent;
import org.smpp.ServerPDUEventListener;
import org.smpp.Session;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.PDU;
import org.smpp.pdu.SubmitSMResp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author vinod.bokare
 *
 */
public class InfobipSMSGateway {

	/**
	 * 
	 */
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	//public static Map<String,String> infobipDlrMap;
	public static Map<Integer,String> infobipDlrMap;
	public static Set<String> deliveredStatusSet ;
	public static Set<String> suppressedStatusSet;
	private static Map<Long, String> updateStatusMap ;
	private final String urlStr = "http://api.infobip.com/api/v3/sendsms/plain?";
	private final String queryString  = "user=<USERNAME>&pass=<PWD>&sender=<SENDERID>&SMSText=<MESSAGE>&concat=<CONCAT>&GSM=<TO>";

	public static synchronized Map<Long, String> getUpdateStatusMap() {
		if(updateStatusMap == null) {
			updateStatusMap = new LinkedHashMap<Long, String>();
		}
		return updateStatusMap;
	}
	private  Set<String> dlrReciepts = new HashSet<String>();


	static {
		//Delivery Set
		deliveredStatusSet = new HashSet<String>();
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_DELIVERD);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_UNIDENTIFIED_SUBSCRIBER);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_ABSENT_SUBSCRIBER_SM);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_CALL_BARRED);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_FACILITY_NOT_SUPPORTED);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_ABSENT_SUBSCRIBER);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_SUBSCRIBER_BUSY_FOR_MT_SMS);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DELIVERY_FAILURE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_MESSAGE_WAITING_LIST_FULL);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_SYSTEM_FAILURE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_MEMORYCAPACITYEXCEEDED);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_EQUIPMENTPROTOCOLERROR);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_EQUIPMENTNOTSM_EQUIPPED);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_UNKNOWNSERVICECENTRE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_SC_CONGESTION );
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_INVALIDSME_ADDRESS);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_SUBSCRIBERNOTSC_SUBSCRIBER);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_PROVIDER_GENERAL_ERROR);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NO_RESPONSE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_SERVICE_COMPLETION_FAILURE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_UNEXPECTED_RESPONSE_FROM_PEER);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_MISTYPED_PARAMETER);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NOT_SUPPORTED_SERVICE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_DUPLICATED_INVOKE_ID);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_INITIATING_RELEASE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_APPCONTEXTNOTSUPPORTED);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_INVALIDDESTINATIONREFERENCE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_INVALIDORIGINATINGREFERENCE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_ENCAPSULATEDAC_NOTSUPPORTED);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_TRANSPORTPROTECTIONNOTADEQUATE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_POTENTIALVERSIONINCOMPATIBILITY);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_REMOTENODENOTREACHABLE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_NOTRANSLATIONFORANADDRESSOFSUCHNATUR);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_NOTRANSLATIONFORTHISSPECIFICADDRESS);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SUBSYSTEMCONGESTION);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SUBSYSTEMFAILURE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_UNEQUIPPEDUSER);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_MTPFAILURE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_NETWORKCONGESTION);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_UNQUALIFIED);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_ERRORINMESSAGETRANSPORTXUDT);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_ERRORINLOCALPROCESSINGXUDT);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_DESTINATIONCANNOTPERFORMREASSEMBLYXUDT);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SCCPfailure);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_HOPCOUNTERVIOLATION);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SEGMENTATIONNOTSUPPORTED);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SEGMENTATIONFAILURE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_UA_USERSPECIFICREASON);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_UA_USERRESOURCELIMITATION);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_UA_RESOURCEUNAVAILABLE);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_UA_APPLICATIONPROCEDURECANCELLATION);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_PROVIDERMALFUNCTION);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_SUPPORTINGDIALOGORTRANSACTIONREALEASED);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_RESSOURCELIMITATION);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_MAINTENANCEACTIVITY);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_VERSIONINCOMPATIBILITY);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_ABNORMALMAPDIALOG);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_RESPONSEREJECTEDBYPEER);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_ABNORMALEVENTRECEIVEDFROMPEER);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_MESSAGECANNOTBEDELIVEREDTOPEER);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_PROVIDEROUTOFINVOKE );
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_TIME_OUT);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_INVALIDMSCADDRESS);
		
		//added on 15th dec 2015-- temp starts
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_SS_INCOMPATIBILITY);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_UNKNOWN_ALPHABET);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_UNKNOWN_ERROR);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_INVALID_RESPONSE_RECEIVED);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_NOREASONGIVEN);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_ABNORMALEVENTDETECTEDBYPEER);
		deliveredStatusSet.add(SMSStatusCodes.INFOBIP_TEMPORARY_EC_NOTSUBMITTEDTOGMSC);
		//added on 15th dec 2015-- temp ends
		
		
		

		//UnDelivery Set
		suppressedStatusSet = new HashSet<String>();
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_UNKNOWN_SUBSCRIBER);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_ILLEGAL_SUBSCRIBER);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_TELESERVICE_NOT_PROVISIONED);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_ILLEGAL_EQUIPMENT);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_DATA_MISSING);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_UNEXPECTED_DATA_VALUE);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_IMSI_BLACKLISTED);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_DEST_ADDRESS_BLACKLISTED);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_INVALIDPDUFORMAT);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_CANCELLED);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_VALIDITYEXPIRED);
		
		
		//added on date 15th dec 2015--perm starts
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_BEARER_SERVICE_NOT_PROVISIONED);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_RESOURCE_LIMITATION);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_USSD_BUSY);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_NOTSUBMITTEDTOSMPPCHANNEL);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_VOICE_ANSWERED);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_VOICE_ANSWERED_MACHINE);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_VOICE_USER_BUSY);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_VOICE_NO_ANSWER);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_VOICE_ERROR_DOWNLOADING_FILE);
		suppressedStatusSet.add(SMSStatusCodes.INFOBIP_PERMANANT_EC_VOICE_ERROR_UNSUPPORTED_AUDIO_FORMAT);
		//added on date 15th dec 2015--perm ends
		
		

		//InfoBip provided permanent status
		/*infobipDlrMap = new HashMap<String, String>();

		infobipDlrMap.put("000",SMSStatusCodes.INFOBIP_PERMANANT_DELIVERD);
		infobipDlrMap.put("0000",SMSStatusCodes.INFOBIP_PERMANANT_DELIVERD);
		infobipDlrMap.put("001",SMSStatusCodes.INFOBIP_PERMANANT_EC_UNKNOWN_SUBSCRIBER);
		infobipDlrMap.put("009",SMSStatusCodes.INFOBIP_PERMANANT_EC_ILLEGAL_SUBSCRIBER);
		infobipDlrMap.put("011",SMSStatusCodes.INFOBIP_PERMANANT_EC_TELESERVICE_NOT_PROVISIONED);
		infobipDlrMap.put("012",SMSStatusCodes.INFOBIP_PERMANANT_EC_ILLEGAL_EQUIPMENT);
		infobipDlrMap.put("035",SMSStatusCodes.INFOBIP_PERMANANT_EC_DATA_MISSING);
		infobipDlrMap.put("036",SMSStatusCodes.INFOBIP_PERMANANT_EC_UNEXPECTED_DATA_VALUE);
		infobipDlrMap.put("1",SMSStatusCodes.INFOBIP_PERMANANT_EC_UNKNOWN_SUBSCRIBER);
		infobipDlrMap.put("9",SMSStatusCodes.INFOBIP_PERMANANT_EC_ILLEGAL_SUBSCRIBER);
		infobipDlrMap.put("11",SMSStatusCodes.INFOBIP_PERMANANT_EC_TELESERVICE_NOT_PROVISIONED);
		infobipDlrMap.put("12",SMSStatusCodes.INFOBIP_PERMANANT_EC_ILLEGAL_EQUIPMENT);
		infobipDlrMap.put("35",SMSStatusCodes.INFOBIP_PERMANANT_EC_DATA_MISSING);
		infobipDlrMap.put("36",SMSStatusCodes.INFOBIP_PERMANANT_EC_UNEXPECTED_DATA_VALUE);
		infobipDlrMap.put("2049",SMSStatusCodes.INFOBIP_PERMANANT_EC_IMSI_BLACKLISTED);
		infobipDlrMap.put("2050",SMSStatusCodes.INFOBIP_PERMANANT_EC_DEST_ADDRESS_BLACKLISTED);
		infobipDlrMap.put("4096",SMSStatusCodes.INFOBIP_PERMANANT_EC_INVALIDPDUFORMAT);
		infobipDlrMap.put("4100",SMSStatusCodes.INFOBIP_PERMANANT_EC_CANCELLED);
		infobipDlrMap.put("4101",SMSStatusCodes.INFOBIP_PERMANANT_EC_VALIDITYEXPIRED);
		
		
		//added on date 15th dec 2015--perm starts
		infobipDlrMap.put("10",SMSStatusCodes.INFOBIP_PERMANANT_EC_BEARER_SERVICE_NOT_PROVISIONED);
		infobipDlrMap.put("51",SMSStatusCodes.INFOBIP_PERMANANT_EC_RESOURCE_LIMITATION);
		infobipDlrMap.put("72",SMSStatusCodes.INFOBIP_PERMANANT_EC_USSD_BUSY);
		infobipDlrMap.put("4102",SMSStatusCodes.INFOBIP_PERMANANT_EC_NOTSUBMITTEDTOSMPPCHANNEL);
		infobipDlrMap.put("5000",SMSStatusCodes.INFOBIP_PERMANANT_VOICE_ANSWERED);
		infobipDlrMap.put("5001",SMSStatusCodes.INFOBIP_PERMANANT_VOICE_ANSWERED_MACHINE);
		infobipDlrMap.put("5002",SMSStatusCodes.INFOBIP_PERMANANT_EC_VOICE_USER_BUSY);
		infobipDlrMap.put("5003",SMSStatusCodes.INFOBIP_PERMANANT_EC_VOICE_NO_ANSWER);
		infobipDlrMap.put("5004",SMSStatusCodes.INFOBIP_PERMANANT_EC_VOICE_ERROR_DOWNLOADING_FILE);
		infobipDlrMap.put("5005",SMSStatusCodes.INFOBIP_PERMANANT_EC_VOICE_ERROR_UNSUPPORTED_AUDIO_FORMAT);
		//added on date 15th dec 2015--perm ends
		
		
		

		//InfoBip provided intermediate status 
		infobipDlrMap.put("5",SMSStatusCodes.INFOBIP_TEMPORARY_EC_UNIDENTIFIED_SUBSCRIBER);
		infobipDlrMap.put("6",SMSStatusCodes.INFOBIP_TEMPORARY_EC_ABSENT_SUBSCRIBER_SM);
		infobipDlrMap.put("13",SMSStatusCodes.INFOBIP_TEMPORARY_EC_CALL_BARRED);
		infobipDlrMap.put("21",SMSStatusCodes.INFOBIP_TEMPORARY_EC_FACILITY_NOT_SUPPORTED);
		infobipDlrMap.put("27",SMSStatusCodes.INFOBIP_TEMPORARY_EC_ABSENT_SUBSCRIBER);
		infobipDlrMap.put("31",SMSStatusCodes.INFOBIP_TEMPORARY_EC_SUBSCRIBER_BUSY_FOR_MT_SMS);
		infobipDlrMap.put("32",SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DELIVERY_FAILURE);
		infobipDlrMap.put("33",SMSStatusCodes.INFOBIP_TEMPORARY_EC_MESSAGE_WAITING_LIST_FULL);
		infobipDlrMap.put("34",SMSStatusCodes.INFOBIP_TEMPORARY_EC_SYSTEM_FAILURE);
		infobipDlrMap.put("256",SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_MEMORYCAPACITYEXCEEDED);
		infobipDlrMap.put("257",SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_EQUIPMENTPROTOCOLERROR);
		infobipDlrMap.put("258",SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_EQUIPMENTNOTSM_EQUIPPED);
		infobipDlrMap.put("259",SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_UNKNOWNSERVICECENTRE);
		infobipDlrMap.put("260",SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_SC_CONGESTION );
		infobipDlrMap.put("261",SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_INVALIDSME_ADDRESS);
		infobipDlrMap.put("262",SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_SUBSCRIBERNOTSC_SUBSCRIBER);
		infobipDlrMap.put("500",SMSStatusCodes.INFOBIP_TEMPORARY_EC_PROVIDER_GENERAL_ERROR);
		infobipDlrMap.put("502",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NO_RESPONSE);
		infobipDlrMap.put("503",SMSStatusCodes.INFOBIP_TEMPORARY_EC_SERVICE_COMPLETION_FAILURE);
		infobipDlrMap.put("504",SMSStatusCodes.INFOBIP_TEMPORARY_EC_UNEXPECTED_RESPONSE_FROM_PEER);
		infobipDlrMap.put("507",SMSStatusCodes.INFOBIP_TEMPORARY_EC_MISTYPED_PARAMETER);
		infobipDlrMap.put("508",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NOT_SUPPORTED_SERVICE);
		infobipDlrMap.put("509",SMSStatusCodes.INFOBIP_TEMPORARY_EC_DUPLICATED_INVOKE_ID);
		infobipDlrMap.put("511",SMSStatusCodes.INFOBIP_TEMPORARY_EC_INITIATING_RELEASE);
		infobipDlrMap.put("1024",SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_APPCONTEXTNOTSUPPORTED);
		infobipDlrMap.put("1025",SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_INVALIDDESTINATIONREFERENCE);
		infobipDlrMap.put("1026",SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_INVALIDORIGINATINGREFERENCE);
		infobipDlrMap.put("1027",SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_ENCAPSULATEDAC_NOTSUPPORTED);
		infobipDlrMap.put("1028",SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_TRANSPORTPROTECTIONNOTADEQUATE);
		infobipDlrMap.put("1030",SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_POTENTIALVERSIONINCOMPATIBILITY);
		infobipDlrMap.put("1031",SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_REMOTENODENOTREACHABLE);
		infobipDlrMap.put("1152",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_NOTRANSLATIONFORANADDRESSOFSUCHNATUR);
		infobipDlrMap.put("1153",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_NOTRANSLATIONFORTHISSPECIFICADDRESS);
		infobipDlrMap.put("1154",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SUBSYSTEMCONGESTION);
		infobipDlrMap.put("1155",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SUBSYSTEMFAILURE);
		infobipDlrMap.put("1156",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_UNEQUIPPEDUSER);
		infobipDlrMap.put("1157",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_MTPFAILURE);
		infobipDlrMap.put("1158",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_NETWORKCONGESTION);
		infobipDlrMap.put("1159",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_UNQUALIFIED);
		infobipDlrMap.put("1160",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_ERRORINMESSAGETRANSPORTXUDT);
		infobipDlrMap.put("1161",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_ERRORINLOCALPROCESSINGXUDT);
		infobipDlrMap.put("1162",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_DESTINATIONCANNOTPERFORMREASSEMBLYXUDT);
		infobipDlrMap.put("1163",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SCCPfailure);
		infobipDlrMap.put("1164",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_HOPCOUNTERVIOLATION);
		infobipDlrMap.put("1165",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SEGMENTATIONNOTSUPPORTED);
		infobipDlrMap.put("1166",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SEGMENTATIONFAILURE);
		infobipDlrMap.put("1281",SMSStatusCodes.INFOBIP_TEMPORARY_EC_UA_USERSPECIFICREASON);
		infobipDlrMap.put("1282",SMSStatusCodes.INFOBIP_TEMPORARY_EC_UA_USERRESOURCELIMITATION);
		infobipDlrMap.put("1283",SMSStatusCodes.INFOBIP_TEMPORARY_EC_UA_RESOURCEUNAVAILABLE);
		infobipDlrMap.put("1284",SMSStatusCodes.INFOBIP_TEMPORARY_EC_UA_APPLICATIONPROCEDURECANCELLATION);
		infobipDlrMap.put("1536",SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_PROVIDERMALFUNCTION);
		infobipDlrMap.put("1537",SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_SUPPORTINGDIALOGORTRANSACTIONREALEASED);
		infobipDlrMap.put("1538",SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_RESSOURCELIMITATION);
		infobipDlrMap.put("1539",SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_MAINTENANCEACTIVITY);
		infobipDlrMap.put("1540",SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_VERSIONINCOMPATIBILITY);
		infobipDlrMap.put("1541",SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_ABNORMALMAPDIALOG);
		infobipDlrMap.put("1793",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_RESPONSEREJECTEDBYPEER);
		infobipDlrMap.put("1794",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_ABNORMALEVENTRECEIVEDFROMPEER);
		infobipDlrMap.put("1795",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_MESSAGECANNOTBEDELIVEREDTOPEER);
		infobipDlrMap.put("1796",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_PROVIDEROUTOFINVOKE );
		infobipDlrMap.put("2048",SMSStatusCodes.INFOBIP_TEMPORARY_EC_TIME_OUT);
		infobipDlrMap.put("2051",SMSStatusCodes.INFOBIP_TEMPORARY_EC_INVALIDMSCADDRESS);
		
		
		//added on date 15th dec 2015-- temp starts
		infobipDlrMap.put("20",SMSStatusCodes.INFOBIP_TEMPORARY_EC_SS_INCOMPATIBILITY);
		infobipDlrMap.put("71",SMSStatusCodes.INFOBIP_TEMPORARY_EC_UNKNOWN_ALPHABET);
		infobipDlrMap.put("255",SMSStatusCodes.INFOBIP_TEMPORARY_EC_UNKNOWN_ERROR);
		infobipDlrMap.put("501",SMSStatusCodes.INFOBIP_TEMPORARY_EC_INVALID_RESPONSE_RECEIVED);
		infobipDlrMap.put("1029",SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_NOREASONGIVEN);
		infobipDlrMap.put("1792",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_ABNORMALEVENTDETECTEDBYPEER);
		infobipDlrMap.put("4097",SMSStatusCodes.INFOBIP_TEMPORARY_EC_NOTSUBMITTEDTOGMSC);*/
		//added on date 15th dec 2015-- temp ends
		
		
		
		
		

		infobipDlrMap = new HashMap<Integer, String>();
		infobipDlrMap.put(0,SMSStatusCodes.INFOBIP_PERMANANT_DELIVERD);
		//infobipDlrMap.put(0,SMSStatusCodes.INFOBIP_PERMANANT_DELIVERD);
		infobipDlrMap.put(1,SMSStatusCodes.INFOBIP_PERMANANT_EC_UNKNOWN_SUBSCRIBER);
		infobipDlrMap.put(2,SMSStatusCodes.INFOBIP_PERMANANT_EC_ILLEGAL_SUBSCRIBER);
		infobipDlrMap.put(11,SMSStatusCodes.INFOBIP_PERMANANT_EC_TELESERVICE_NOT_PROVISIONED);
		infobipDlrMap.put(12,SMSStatusCodes.INFOBIP_PERMANANT_EC_ILLEGAL_EQUIPMENT);
		infobipDlrMap.put(35,SMSStatusCodes.INFOBIP_PERMANANT_EC_DATA_MISSING);
		infobipDlrMap.put(36,SMSStatusCodes.INFOBIP_PERMANANT_EC_UNEXPECTED_DATA_VALUE);
		//infobipDlrMap.put(1,SMSStatusCodes.INFOBIP_PERMANANT_EC_UNKNOWN_SUBSCRIBER);
		infobipDlrMap.put(9,SMSStatusCodes.INFOBIP_PERMANANT_EC_ILLEGAL_SUBSCRIBER);
		//infobipDlrMap.put(11,SMSStatusCodes.INFOBIP_PERMANANT_EC_TELESERVICE_NOT_PROVISIONED);
		//infobipDlrMap.put(12,SMSStatusCodes.INFOBIP_PERMANANT_EC_ILLEGAL_EQUIPMENT);
		//infobipDlrMap.put(35,SMSStatusCodes.INFOBIP_PERMANANT_EC_DATA_MISSING);
		//infobipDlrMap.put(36,SMSStatusCodes.INFOBIP_PERMANANT_EC_UNEXPECTED_DATA_VALUE);
		infobipDlrMap.put(2049,SMSStatusCodes.INFOBIP_PERMANANT_EC_IMSI_BLACKLISTED);
		infobipDlrMap.put(2050,SMSStatusCodes.INFOBIP_PERMANANT_EC_DEST_ADDRESS_BLACKLISTED);
		infobipDlrMap.put(4096,SMSStatusCodes.INFOBIP_PERMANANT_EC_INVALIDPDUFORMAT);
		infobipDlrMap.put(4100,SMSStatusCodes.INFOBIP_PERMANANT_EC_CANCELLED);
		infobipDlrMap.put(4101,SMSStatusCodes.INFOBIP_PERMANANT_EC_VALIDITYEXPIRED);
		
		
		//added on date 15th dec 2015--perm starts
		infobipDlrMap.put(10,SMSStatusCodes.INFOBIP_PERMANANT_EC_BEARER_SERVICE_NOT_PROVISIONED);
		infobipDlrMap.put(51,SMSStatusCodes.INFOBIP_PERMANANT_EC_RESOURCE_LIMITATION);
		infobipDlrMap.put(72,SMSStatusCodes.INFOBIP_PERMANANT_EC_USSD_BUSY);
		infobipDlrMap.put(4102,SMSStatusCodes.INFOBIP_PERMANANT_EC_NOTSUBMITTEDTOSMPPCHANNEL);
		infobipDlrMap.put(5000,SMSStatusCodes.INFOBIP_PERMANANT_VOICE_ANSWERED);
		infobipDlrMap.put(5001,SMSStatusCodes.INFOBIP_PERMANANT_VOICE_ANSWERED_MACHINE);
		infobipDlrMap.put(5002,SMSStatusCodes.INFOBIP_PERMANANT_EC_VOICE_USER_BUSY);
		infobipDlrMap.put(5003,SMSStatusCodes.INFOBIP_PERMANANT_EC_VOICE_NO_ANSWER);
		infobipDlrMap.put(5004,SMSStatusCodes.INFOBIP_PERMANANT_EC_VOICE_ERROR_DOWNLOADING_FILE);
		infobipDlrMap.put(5005,SMSStatusCodes.INFOBIP_PERMANANT_EC_VOICE_ERROR_UNSUPPORTED_AUDIO_FORMAT);
		//added on date 15th dec 2015--perm ends
		
		
		

		//InfoBip provided intermediate status 
		infobipDlrMap.put(5,SMSStatusCodes.INFOBIP_TEMPORARY_EC_UNIDENTIFIED_SUBSCRIBER);
		infobipDlrMap.put(6,SMSStatusCodes.INFOBIP_TEMPORARY_EC_ABSENT_SUBSCRIBER_SM);
		infobipDlrMap.put(13,SMSStatusCodes.INFOBIP_TEMPORARY_EC_CALL_BARRED);
		infobipDlrMap.put(21,SMSStatusCodes.INFOBIP_TEMPORARY_EC_FACILITY_NOT_SUPPORTED);
		infobipDlrMap.put(27,SMSStatusCodes.INFOBIP_TEMPORARY_EC_ABSENT_SUBSCRIBER);
		infobipDlrMap.put(31,SMSStatusCodes.INFOBIP_TEMPORARY_EC_SUBSCRIBER_BUSY_FOR_MT_SMS);
		infobipDlrMap.put(32,SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DELIVERY_FAILURE);
		infobipDlrMap.put(33,SMSStatusCodes.INFOBIP_TEMPORARY_EC_MESSAGE_WAITING_LIST_FULL);
		infobipDlrMap.put(34,SMSStatusCodes.INFOBIP_TEMPORARY_EC_SYSTEM_FAILURE);
		infobipDlrMap.put(256,SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_MEMORYCAPACITYEXCEEDED);
		infobipDlrMap.put(257,SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_EQUIPMENTPROTOCOLERROR);
		infobipDlrMap.put(258,SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_EQUIPMENTNOTSM_EQUIPPED);
		infobipDlrMap.put(259,SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_UNKNOWNSERVICECENTRE);
		infobipDlrMap.put(260,SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_SC_CONGESTION );
		infobipDlrMap.put(261,SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_INVALIDSME_ADDRESS);
		infobipDlrMap.put(262,SMSStatusCodes.INFOBIP_TEMPORARY_EC_SM_DF_SUBSCRIBERNOTSC_SUBSCRIBER);
		infobipDlrMap.put(500,SMSStatusCodes.INFOBIP_TEMPORARY_EC_PROVIDER_GENERAL_ERROR);
		infobipDlrMap.put(502,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NO_RESPONSE);
		infobipDlrMap.put(503,SMSStatusCodes.INFOBIP_TEMPORARY_EC_SERVICE_COMPLETION_FAILURE);
		infobipDlrMap.put(504,SMSStatusCodes.INFOBIP_TEMPORARY_EC_UNEXPECTED_RESPONSE_FROM_PEER);
		infobipDlrMap.put(507,SMSStatusCodes.INFOBIP_TEMPORARY_EC_MISTYPED_PARAMETER);
		infobipDlrMap.put(508,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NOT_SUPPORTED_SERVICE);
		infobipDlrMap.put(509,SMSStatusCodes.INFOBIP_TEMPORARY_EC_DUPLICATED_INVOKE_ID);
		infobipDlrMap.put(511,SMSStatusCodes.INFOBIP_TEMPORARY_EC_INITIATING_RELEASE);
		infobipDlrMap.put(1024,SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_APPCONTEXTNOTSUPPORTED);
		infobipDlrMap.put(1025,SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_INVALIDDESTINATIONREFERENCE);
		infobipDlrMap.put(1026,SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_INVALIDORIGINATINGREFERENCE);
		infobipDlrMap.put(1027,SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_ENCAPSULATEDAC_NOTSUPPORTED);
		infobipDlrMap.put(1028,SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_TRANSPORTPROTECTIONNOTADEQUATE);
		infobipDlrMap.put(1030,SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_POTENTIALVERSIONINCOMPATIBILITY);
		infobipDlrMap.put(1031,SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_REMOTENODENOTREACHABLE);
		infobipDlrMap.put(1152,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_NOTRANSLATIONFORANADDRESSOFSUCHNATUR);
		infobipDlrMap.put(1153,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_NOTRANSLATIONFORTHISSPECIFICADDRESS);
		infobipDlrMap.put(1154,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SUBSYSTEMCONGESTION);
		infobipDlrMap.put(1155,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SUBSYSTEMFAILURE);
		infobipDlrMap.put(1156,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_UNEQUIPPEDUSER);
		infobipDlrMap.put(1157,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_MTPFAILURE);
		infobipDlrMap.put(1158,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_NETWORKCONGESTION);
		infobipDlrMap.put(1159,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_UNQUALIFIED);
		infobipDlrMap.put(1160,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_ERRORINMESSAGETRANSPORTXUDT);
		infobipDlrMap.put(1161,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_ERRORINLOCALPROCESSINGXUDT);
		infobipDlrMap.put(1162,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_DESTINATIONCANNOTPERFORMREASSEMBLYXUDT);
		infobipDlrMap.put(1163,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SCCPfailure);
		infobipDlrMap.put(1164,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_HOPCOUNTERVIOLATION);
		infobipDlrMap.put(1165,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SEGMENTATIONNOTSUPPORTED);
		infobipDlrMap.put(1166,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NNR_SEGMENTATIONFAILURE);
		infobipDlrMap.put(1281,SMSStatusCodes.INFOBIP_TEMPORARY_EC_UA_USERSPECIFICREASON);
		infobipDlrMap.put(1282,SMSStatusCodes.INFOBIP_TEMPORARY_EC_UA_USERRESOURCELIMITATION);
		infobipDlrMap.put(1283,SMSStatusCodes.INFOBIP_TEMPORARY_EC_UA_RESOURCEUNAVAILABLE);
		infobipDlrMap.put(1284,SMSStatusCodes.INFOBIP_TEMPORARY_EC_UA_APPLICATIONPROCEDURECANCELLATION);
		infobipDlrMap.put(1536,SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_PROVIDERMALFUNCTION);
		infobipDlrMap.put(1537,SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_SUPPORTINGDIALOGORTRANSACTIONREALEASED);
		infobipDlrMap.put(1538,SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_RESSOURCELIMITATION);
		infobipDlrMap.put(1539,SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_MAINTENANCEACTIVITY);
		infobipDlrMap.put(1540,SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_VERSIONINCOMPATIBILITY);
		infobipDlrMap.put(1541,SMSStatusCodes.INFOBIP_TEMPORARY_EC_PA_ABNORMALMAPDIALOG);
		infobipDlrMap.put(1793,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_RESPONSEREJECTEDBYPEER);
		infobipDlrMap.put(1794,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_ABNORMALEVENTRECEIVEDFROMPEER);
		infobipDlrMap.put(1795,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_MESSAGECANNOTBEDELIVEREDTOPEER);
		infobipDlrMap.put(1796,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_PROVIDEROUTOFINVOKE );
		infobipDlrMap.put(2048,SMSStatusCodes.INFOBIP_TEMPORARY_EC_TIME_OUT);
		infobipDlrMap.put(2051,SMSStatusCodes.INFOBIP_TEMPORARY_EC_INVALIDMSCADDRESS);
		
		
		//added on date 15th dec 2015-- temp starts
		infobipDlrMap.put(20,SMSStatusCodes.INFOBIP_TEMPORARY_EC_SS_INCOMPATIBILITY);
		infobipDlrMap.put(71,SMSStatusCodes.INFOBIP_TEMPORARY_EC_UNKNOWN_ALPHABET);
		infobipDlrMap.put(255,SMSStatusCodes.INFOBIP_TEMPORARY_EC_UNKNOWN_ERROR);
		infobipDlrMap.put(501,SMSStatusCodes.INFOBIP_TEMPORARY_EC_INVALID_RESPONSE_RECEIVED);
		infobipDlrMap.put(1029,SMSStatusCodes.INFOBIP_TEMPORARY_EC_OR_NOREASONGIVEN);
		infobipDlrMap.put(1792,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NC_ABNORMALEVENTDETECTEDBYPEER);
		infobipDlrMap.put(4097,SMSStatusCodes.INFOBIP_TEMPORARY_EC_NOTSUBMITTEDTOGMSC);
		//added on date 15th dec 2015-- temp ends
		
		
		logger.debug("infobipDlrMap size >>>>>>>>>>>>>>>>>>>>> "+infobipDlrMap.size());
	}

	private OCSMSGateway ocsmsGateway;
	private SMSCampaignSentDao smsCampaignSentDao;
	private SMSCampaignSentDaoForDML smsCampaignSentDaoForDML;
	private SMSBouncesDao smsBouncesDao;
	private SMSBouncesDaoForDML smsBouncesDaoForDML;
	private SMSCampaignDeliveryReportsHandler smsDlrHandler;
	private SMSCampaignReportDao smsCampaignReportDao;
	private SMSCampaignReportDaoForDML smsCampaignReportDaoForDML;
	private SMSSuppressedContactsDao smsSuppressedContactsDao;
	private ContactsDao contactsDao;
	private ContactsDaoForDML contactsDaoForDML;
	private SMSCConnector SMSCConnectorObj;
	private GatewaySessionProvider sessionProvider;
	private Session infobipSession;
	private int smsCount=0;

	public InfobipSMSGateway() {
		// TODO Auto-generated constructor stub
	}

	public InfobipSMSGateway(OCSMSGateway ocsmsGateway){
		this.ocsmsGateway = ocsmsGateway;
		//this.smsCount = 0;
		try {
			ServiceLocator serviceLocator = ServiceLocator.getInstance();
			smsCampaignSentDao = (SMSCampaignSentDao)serviceLocator.getDAOByName("smsCampaignSentDao");
			smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)serviceLocator.getDAOForDMLByName("smsCampaignSentDaoForDML");
			smsBouncesDao = (SMSBouncesDao)serviceLocator.getDAOByName("smsBouncesDao");
			smsBouncesDaoForDML = (SMSBouncesDaoForDML)serviceLocator.getDAOForDMLByName("smsBouncesDaoForDML");
			smsCampaignReportDao = (SMSCampaignReportDao)serviceLocator.getDAOByName("smsCampaignReportDao");
			smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)serviceLocator.getDAOForDMLByName("smsCampaignReportDaoForDML");
			smsSuppressedContactsDao = (SMSSuppressedContactsDao)serviceLocator.getDAOByName("smsSuppressedContactsDao");
			contactsDao = (ContactsDao)serviceLocator.getDAOByName("contactsDao");
			contactsDaoForDML  = (ContactsDaoForDML)serviceLocator.getDAOForDMLByName("contactsDaoForDML");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception while getting the dao ", e);
		}

	}//Constructor



	/**
	 * InfobipSMSCPDUListener 
	 * @author vinod.bokare
	 *
	 */
	private class InfobipSMSCPDUListener implements ServerPDUEventListener{
		private Session infobipSession;
		private OCSMSGateway ocSMSGateway;
		public InfobipSMSCPDUListener() {}

		public InfobipSMSCPDUListener(Session infobipSession, OCSMSGateway ocsmsGateway) {
			this.infobipSession = infobipSession;
			this.ocSMSGateway = ocsmsGateway;
		}


		@Override
		public void handleEvent(ServerPDUEvent event) {
			//System.out.println("Infobip handleEvent ");
			logger.debug(">>>>>>> Started InfobipSMSGateway.InfobipSMSCPDUListener :: handleEvent <<<<<<< ");
			try {
				//super.handleEvent(event);

				PDU receivedPDU = event.getPDU();
				logger.debug("got an event"+receivedPDU+" receivedPDU ::"+receivedPDU.debugString()
						);
				if(receivedPDU instanceof DeliverSM) {
					//TODO
					logger.info("InfoBip receivedPDU is an Instance of DeliverSM");
//					SMSCConnectorObj.submitDeliverSmResp(receivedPDU,infobipSession);

				}if(receivedPDU instanceof SubmitSMResp) {
					logger.info("InfoBip receivedPDU is an Instance of SubmitSMResp");
					//System.out.println("InfoBip receivedPDU is an Instance of SubmitSMResp");
//					SMSCConnectorObj.submitDeliverSmResp(receivedPDU,infobipSession);
					sessionProvider.procesReceivedPDU(receivedPDU, ocSMSGateway,infobipSession);

				}//if
			} 
			catch(Exception e){
				logger.error("Exception ----", e);
				//e.printStackTrace();

			}
			finally {}
			logger.debug(">>>>>>> Completed InfobipSMSGateway.InfobipSMSCPDUListener :: handleEvent <<<<<<< ");
		}//handleEvent
	}//inner class InfobipSMSCPDUListener



	public String sendOverHTTP(String UserId, String Password,String content, String mobile,String senderId ) {

		logger.debug(">>>>>>> Started InfobipSMSGateway :: sendOverHTTP <<<<<<< ");
		try {
			/*
			 * http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052346000&dcs=0&msgtxt=This is Test message&state=4 
			 */

			String postData = "";
			String data = URLEncoder.encode(content, "UTF-8"); 

			postData += this.queryString.replace("<USERNAME>", UserId ).replace("<PWD>",  URLEncoder.encode(Password, "UTF-8"))
					.replace("<SENDERID>", senderId).replace("<TO>", mobile)
					.replace("<MESSAGE>", data).replace("<CONCAT>", content.trim().length() <= 160 ? "0" : "1");


		//	logger.debug("postData======>"+postData);

			URL url = new URL(this.urlStr);

			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();

			urlconnection.setRequestMethod("POST");
			urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlconnection.setDoOutput(true);

			OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
			out.write(postData);
			out.flush();
			out.close();

			BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));

			String decodedString;
			String response = "";
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			in.close();
			logger.debug("response is======>"+response);

			/**
			 * Prepare the response by processing the XML
			 */
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(response.getBytes("utf-8"))));;//dBuilder.parse(response);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("result");
			String msgId =Constants.STRING_NILL;
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				//	System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;


					msgId = eElement.getElementsByTagName("messageid").item(0).getTextContent();

				}
			}
			logger.info("msgId : " + msgId);

			logger.debug(">>>>>>> Completed InfobipSMSGateway :: sendOverHTTP <<<<<<< ");
			return msgId;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
			return null;
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::",e);
			return null;
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception :::",e);
			return null;
		}

		
	}

	/**
	 * 
	 * @param content
	 * @param mobilenumber
	 * @param sentId
	 * @param senderId
	 * @throws BaseServiceException
	 */
	public void sendSMSOverSMPP(String content, String mobilenumber, Long sentId, String senderId) throws BaseServiceException{
		logger.debug(">>>>>>> Started InfobipSMSGateway :: sendSMSOverSMPP <<<<<<< ");

		if(SMSCConnectorObj == null ) SMSCConnectorObj = new SMSCConnector(ocsmsGateway);
		try {
			if(sessionProvider == null) sessionProvider = GatewaySessionProvider.getInstance();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("some error occured can not continue ", e1);
			throw new BaseServiceException("Exception, try after sometime");
		}
		logger.debug("infobipSession == null ||"+infobipSession == null);//+" !infobipSession.isBound() ||"+!infobipSession.isBound()+" !infobipSession.isOpened()"+ !infobipSession.isOpened());
		if(infobipSession == null || !infobipSession.isBound() || !infobipSession.isOpened()) {

			try {
				infobipSession = SMSCConnectorObj.getSession();
				logger.info("Got InfobipSession .........."+infobipSession);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error while getting session with unicel", e);
			}
		}

		if(infobipSession != null && !infobipSession.isBound() ) {

			InfobipSMSCPDUListener listener = null;//to avoid growing objcts in heap
			try {
				listener = new InfobipSMSCPDUListener(infobipSession, ocsmsGateway);
				SMSCConnectorObj.bindReq(infobipSession, listener);
			} catch (Exception e) {
				try {
					// TODO Auto-generated catch block
					if (e instanceof IOException || e instanceof SocketException){
						//IOException relate to the brokenpipe issue 
						//we need to close existing sessions and connections
						//restablish session
						Connection connection = infobipSession.getConnection();
						if (connection != null){
							connection.close();
						}
						infobipSession = SMSCConnectorObj.getSession();
						SMSCConnectorObj.bindReq(infobipSession, listener);
					}else{
						logger.error("Exception not related to connectivity: ", e);
						throw new Exception();
					}
				} catch (IOException e1) {
					logger.error("Error related to connectivity", e);
				} catch (Exception e1) {
					logger.error("Exception not related to connectivity: ", e);
				}
			}

		}


		if(infobipSession != null && infobipSession.isBound()) {

			try {
				smsCount ++;
				SMSCConnectorObj.submitSm(infobipSession, content, mobilenumber, sentId, senderId);
			} catch (Exception e) {
				try {
					if (e instanceof IOException || e instanceof SocketException){
						//IOException relate to the brokenpipe issue 
						//we need to close existing sessions and connections
						//restablish session
						Connection connection = infobipSession.getConnection();
						if (connection != null){
							connection.close();
						}
						infobipSession = SMSCConnectorObj.getSession();
						SMSCConnectorObj.bindReq(infobipSession, new InfobipSMSCPDUListener(infobipSession, ocsmsGateway));
						SMSCConnectorObj.submitSm(infobipSession, content, mobilenumber, sentId, senderId);
					}
				} catch (IOException ioe1) {
					logger.debug("Exception", ioe1);
				} catch (Exception e1) {
					logger.debug("Exception", e1);
				}
			}

		}
		logger.debug(">>>>>>> Completed InfobipSMSGateway :: sendSMSOverSMPP <<<<<<< ");		
	}//sendSMSOverSMPP

	/**
	 * 
	 */
	public void unbindSession() {

		if(SMSCConnectorObj == null ){
			SMSCConnectorObj = new SMSCConnector(ocsmsGateway);
			logger.info("SMSCConnectorObj Object returned Null>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
		SMSCConnectorObj.setEnded(true);
		SMSCConnectorObj.unbindSession(infobipSession);//TODO need to unbind
	}


	public synchronized void processDlrReciepts() throws Exception{
	logger.debug(">>>>>>> Started InfobipSMSGateway :: processDlrReciepts <<<<<<< ");
		if(dlrReciepts == null|| dlrReciepts.size() == 0) {
			logger.warn("Some error got, as this is called to process but no entries found.");
			return;
		}
		//process the msg_idsmap first
		Map<Long, String> updateStatusMap = getUpdateStatusMap();
		if(updateStatusMap != null && updateStatusMap.size() > 0 ){
			SMSCConnectorObj.processUpdateStatusMap(null, updateStatusMap, true);
		}

		String[] dlrmsgTokenArr = null;
		String sentIdsStr = Constants.STRING_NILL;

		//Map<String, String> updateSentDlrMap = new HashMap<String, String>();
		Map<String, Integer> updateSentDlrMap = new HashMap<String, Integer>();

		for (String recptMessage : dlrReciepts) {

			String msgID =  null;
			String seqNum = null;
			String statusCode = null;
			Integer infobipIntegralStatusCode = null;
			dlrmsgTokenArr = recptMessage.split(Constants.STRING_WHITESPACE);
			for (String token : dlrmsgTokenArr) {
				//logger.debug("token ::"+token);
				String[] tokenArr = token.split(Constants.DELIMETER_COLON);
				if(tokenArr[0].equals(Constants.SMS_DLR_STATUSCODE_TOKEN)) {

					statusCode = tokenArr[1];
					infobipIntegralStatusCode = Integer.parseInt(statusCode.trim());
					
				}else if(tokenArr[0].equals(Constants.SMS_DLR_MSGID_TOKEN)) {

					msgID = tokenArr[1];
					if(sentIdsStr.length() > 0) sentIdsStr += Constants.DELIMETER_COMMA;
					sentIdsStr += "'"+msgID+"'";
				}
			}//for

			//updateSentDlrMap.put(msgID, statusCode);
			updateSentDlrMap.put(msgID, infobipIntegralStatusCode);

		}//for

		if(sentIdsStr == null || sentIdsStr.isEmpty()) {
			logger.debug("No sent items found for msgIds== "+sentIdsStr);
			return;
		}
		List<SMSCampaignSent> sentList = smsCampaignSentDao.findByMsgIds(sentIdsStr);
		if(sentList == null ){
			//TODO
			logger.debug("No sent items found for msgIds== "+sentIdsStr);

			return;
		}
		List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
		List<SMSBounces> smsBounceList = new ArrayList<SMSBounces>();
		List<String> contactMobilesList = new ArrayList<String>();
		List<SMSSuppressedContacts> suppressedContactsList = new ArrayList<SMSSuppressedContacts>(); 

		Long smsCrID = null;
		Users user  =  null;
		SMSCampaignReport smscr = null;

		for (SMSCampaignSent smsCampaignSent : sentList) {

			try {

				if(smsCampaignSent.getApiMsgId()  == null ) continue;

				String status = infobipDlrMap.get(updateSentDlrMap.get(smsCampaignSent.getApiMsgId()));
				logger.debug("status for "+smsCampaignSent.getApiMsgId() + " status  "+status);
				smscr = smsCampaignSent.getSmsCampaignReport();

				if(smsCrID == null || !smscr.getSmsCrId().equals(smsCrID)){ 

					if(listToBeUpdated.size() > 0 && !smscr.getSmsCrId().equals(smsCrID)){
						updateDlrChunk(listToBeUpdated, smsBounceList, suppressedContactsList, contactMobilesList, user, smsCrID);
					}
					smsCrID = smscr.getSmsCrId();
					user  = smscr.getUser();

				}

				smsCampaignSent.setStatus(status);
				
				if(!deliveredStatusSet.contains(status)) {

					smsCampaignSent.setStatus(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
					SMSBounces newBounce= new SMSBounces();
					newBounce.setCrId(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
					newBounce.setSentId(smsCampaignSent);
					newBounce.setMessage(status);
					newBounce.setMobile(smsCampaignSent.getMobileNumber());
					newBounce.setCategory(status);
					newBounce.setBouncedDate(Calendar.getInstance());
					smsBounceList.add(newBounce);

					if(suppressedStatusSet.contains(status)) {
						SMSSuppressedContacts suppressedContact = new SMSSuppressedContacts();
						suppressedContact.setUser(user);
						suppressedContact.setMobile(smsCampaignSent.getMobileNumber());
						suppressedContact.setType(status);
						suppressedContact.setReason(status);

						suppressedContact.setSuppressedtime(Calendar.getInstance());

						suppressedContactsList.add(suppressedContact);
						if(status.equals(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER)) {

							contactMobilesList.add(smsCampaignSent.getMobileNumber());

						}//if

					}//if

				}//if

				listToBeUpdated.add(smsCampaignSent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("Exception in process dlrreceipts ");

			}

		}//for

		try {
			updateDlrChunk(listToBeUpdated, smsBounceList, suppressedContactsList, contactMobilesList, user, smsCrID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("Exception in process dlrreceipts ");
		}
		synchronized (dlrReciepts) {
			dlrReciepts.clear();

		}
		logger.debug(">>>>>>> Completed InfobipSMSGateway :: processDlrReciepts <<<<<<< ");
	}

	/**
	 * updateDlrChunk
	 * @param listToBeUpdated
	 * @param smsBounceList
	 * @param suppressedContactsList
	 * @param contactMobilesList
	 * @param user
	 * @param smsCrId
	 * @throws Exception
	 */
	private void updateDlrChunk(List<SMSCampaignSent> listToBeUpdated, List<SMSBounces> smsBounceList,
			List<SMSSuppressedContacts> suppressedContactsList, List<String> contactMobilesList,
			Users user, Long smsCrId) throws Exception{
		logger.debug(">>>>>>> Started InfobipSMSGateway :: updateDlrChunk <<<<<<< ");
		try {
			ServiceLocator serviceLocator = ServiceLocator.getInstance();
			if(smsCampaignSentDao == null)smsCampaignSentDao = (SMSCampaignSentDao)serviceLocator.getDAOByName("smsCampaignSentDao");
			if(smsBouncesDao == null)smsBouncesDao = (SMSBouncesDao)serviceLocator.getDAOByName("smsBouncesDao");
			if(smsCampaignReportDao == null)smsCampaignReportDao = (SMSCampaignReportDao)serviceLocator.getDAOByName("smsCampaignReportDao");
			if(smsCampaignReportDaoForDML == null)smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)serviceLocator.getDAOForDMLByName("smsCampaignReportDaoForDML");
			if(smsSuppressedContactsDao == null)smsSuppressedContactsDao = (SMSSuppressedContactsDao)serviceLocator.getDAOByName("smsSuppressedContactsDao");
			if(contactsDao == null)contactsDao = (ContactsDao)serviceLocator.getDAOByName("contactsDao");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception while getting the dao ", e);
			throw new Exception("dao not found");
		}

		if(listToBeUpdated.size() > 0) {
			logger.debug("=============entered into sentUpdate ==========="+listToBeUpdated.size());
			//smsCampaignSentDao.saveByCollection(listToBeUpdated);
			smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);
			listToBeUpdated.clear();

			if(smsBounceList.size() > 0) {
				logger.debug("=============entered into bounceUpdate ==========="+smsBounceList.size());
				//smsBouncesDao.saveByCollection(smsBounceList);
				smsBouncesDaoForDML.saveByCollection(smsBounceList);
				smsBounceList.clear();
			}


			if(suppressedContactsList.size() > 0) {
				SMSCampaignDeliveryReportsHandler handler = new SMSCampaignDeliveryReportsHandler();
				logger.debug("=============entered into suppressedUpdate ==========="+suppressedContactsList.size());
				handler.addToSuppressedContacts(user, suppressedContactsList);//;(user, suppressedContactsList);

			}

			if(contactMobilesList.size() > 0) {

				try {
					String mobileStr = Constants.STRING_NILL;
					for (String mobile : contactMobilesList) {

						if(mobile.startsWith(user.getCountryCarrier().toString())) {

							mobile = mobile.substring(user.getCountryCarrier().toString().length());

						}

						if(!mobileStr.isEmpty()) mobileStr += "|";

						mobileStr += mobile;

					}
					logger.debug("=============entered into contactUpdate ==========="+mobileStr);
					contactsDaoForDML.updatemobileStatusForMultipleContacts(mobileStr, 
							SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER, user.getUserId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception while updating contacts ", e);

				}
				contactMobilesList.clear();
			}
			//TODO need to update bounce count in sms reports
			//int updateCount = smsCampaignReportDao.updateBounceReport(smsCrId);
			int updateCount = smsCampaignReportDaoForDML.updateBounceReport(smsCrId);
			logger.debug("bounced count  ::"+updateCount);
			//smsCampaignSentDao.saveByCollection(listToBeUpdated);

		}//if
		logger.debug(">>>>>>> Completed InfobipSMSGateway :: updateDlrChunk <<<<<<< ");

	}
	/**
	 * Checks balance with the provider 
	 * @param totalCount
	 * @param ocsmsGateway
	 * @return true/false
	 */
	public boolean getBalance(int totalCount, OCSMSGateway ocsmsGateway) {
		logger.debug(">>>>>>> Started InfobipSMSGateway :: getBalance <<<<<<< ");
		String targetUrl = ocsmsGateway.getPostpaidBalURL()+"?username="+ocsmsGateway.getUserId().trim()+"&password="+ocsmsGateway.getPwd().trim()+"&cmd=CREDITS";
		logger.info("InfoBip Target Url ......"+targetUrl+"\t totalCount"+totalCount);
		//System.out.println("InfoBip Target Url ......"+targetUrl);
		try {
			URL url = new URL(targetUrl);
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();

			urlconnection.setRequestMethod("GET");
			urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlconnection.setDoOutput(true);

			BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));

			String decodedString = Constants.STRING_NILL;
			String response = Constants.STRING_NILL;
			while ((decodedString = in.readLine()) != null) {
				response += decodedString;
			}
			in.close();

			if(response == null) return false;

			double balance = 0.0;

			try{
				balance = Double.parseDouble(response);
			}
			catch(NumberFormatException e){
				logger.error("Infobip Error while processing balance ....",e);
			}
			//System.out.println("InfoBip Balance -----"+balance);
			boolean canProceed = true;
/*
			*//**
			 * I have done this for you already � and made it 15.3846  - 
			 * but when you multiple 15,3846 * with price (0.065)  = 0,999999 
			 *//*
			balance = balance * 0.065 ;
			
			if(balance <= totalCount){
				canProceed = false;
			}//if
*/			
			try{
				balance = Double.parseDouble(response);
			//	logger.info("AED Avaliable "+balance);
				/*balance = balance*(0.065 * 153846);
			//	logger.info("AED Avaliable After Conversion "+balance);*/
				balance = balance - (totalCount * 0.065);
				
			}
			catch(NumberFormatException e){
				logger.error("Infobip Error while processing balance ....",e);
			}

			if(balance <=  0.0){
				canProceed = false;
			}//if
			logger.debug("InfoBip Balance -----"+balance +"\t canProceed..:"+canProceed);
			if( !canProceed ) {

				try {

					String message = PropertyUtil.getPropertyValueFromDB(Constants.SMS_LOW_CREDITS_WARN_TEXT);
					String emailId = PropertyUtil.getPropertyValueFromDB("SupportEmailId");

					EmailQueue emailQueue = new EmailQueue("Ran out of SMS Credits-InfoBip", message, 
							Constants.EQ_TYPE_LOW_SMS_CREDITS, "Active", emailId, new Date());
					EmailQueueDao emailQueueDao = null;
					EmailQueueDaoForDML emailQueueDaoForDML = null;
					try {
						emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
						emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						throw new BaseServiceException("InfoBip Exception while getting emailQueueDao");
					}

					//emailQueueDao.saveOrUpdate(emailQueue);
					emailQueueDaoForDML.saveOrUpdate(emailQueue);
				} catch(Exception e) {
					logger.error("InfoBip exception while saving email queue object");
					return canProceed;
				}//catch

			}//if
			logger.debug(">>>>>>> Completed InfobipSMSGateway :: getBalance <<<<<<< ");
			return canProceed;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("InfoBip Exception", e);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("InfoBip Exception", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("InfoBip Exception", e);
		}
		logger.debug(">>>>>>> Completed InfobipSMSGateway :: getBalance <<<<<<< ");
		return false;
	}//getBalance


	/**
	 * 
	 */
	public void  pingInfobipToSendRestOfSMS() {
		logger.debug(">>>>>>> Started InfobipSMSGateway :: pingInfobipToSendRestOfSMS <<<<<<< ");
		try {

			try {

				//if(sessionProvider == null) sessionProvider = GatewaySessionProvider.getInstance();
				//if(dlrReciepts.size() > 0) processDlrReciepts();
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("Exception in final update ");
			}

			//logger.debug("=====started sending====");
			//SMPPLogicaSampleAPP.getReports();
			logger.debug("=====ended here====");

			//return retList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		logger.debug(">>>>>>> Completed InfobipSMSGateway :: pingInfobipToSendRestOfSMS <<<<<<< ");
	}

	/*public static void main(String[] args) {
		InfobipSMSGateway infobipSMSGateway = new InfobipSMSGateway();
		OCSMSGateway ocsmsGateway = new OCSMSGateway();
		//"http://api2.infobip.com/api/command?username=risservices&password=Test2368&cmd=CREDITS"
		ocsmsGateway.setUserId("risservices");
		ocsmsGateway.setPwd("Test2368");
		ocsmsGateway.setPostpaidBalURL("http://api2.infobip.com/api/command");
		logger.debug("-----------------"+infobipSMSGateway.getBalance(5, ocsmsGateway));
	}*/
}//EOF
