package org.mq.captiway.scheduler.services;

import java.io.BufferedReader;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.SMSGatewaySessionMonitor;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.SMSBounces;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSSuppressedContacts;
import org.mq.captiway.scheduler.beans.SuppressedContacts;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.SMSBouncesDao;
import org.mq.captiway.scheduler.dao.SMSBouncesDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.service.GatewaySessionHelper;
import org.mq.optculture.service.GatewaySessionProvider;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.smpp.Connection;
import org.smpp.Data;
import org.smpp.ServerPDUEvent;
import org.smpp.ServerPDUEventListener;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.TimeoutException;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.AddressRange;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransciever;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.EnquireLink;
import org.smpp.pdu.PDU;
import org.smpp.pdu.PDUException;
import org.smpp.pdu.SubmitSM;
import org.smpp.pdu.SubmitSMResp;
import org.smpp.pdu.UnbindResp;
import org.smpp.pdu.ValueNotSetException;
import org.springframework.context.ApplicationContext;

public class UnicelSMSGateway {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	/*private String userID ;
	private String pwd ;
	private String port;
	
	private String ip;*/
	public static Map<String, String> unicellDlrMap ;
	public static Set<String> deliveredStatusSet ;
	public static Set<String> suppressedStatusSet;
	private static Map<Long, String> updateStatusMap ;
	
	public static synchronized Map<Long, String> getUpdateStatusMap() {
		if(updateStatusMap == null) {
			updateStatusMap = new LinkedHashMap<Long, String>();
		}
		return updateStatusMap;
	}


	private String currentSubmitMID;
	private String currentDelievredMID;
	
	private  Set<String> dlrReciepts = new HashSet<String>();
	private int smsCount=0;
	/**
	 * 110
PENDG-ABS-SUB
PENDG-MEM- EXCD
120
The message is rejected because there was no paging response, the IMSI record is
marked detached, or the MS is subject to
roaming restrictions at the first attempt.
Message rejected because the MS doesn't have enough memory when at the first
attempt.
PENDG-NW- FAILR
130
Message rejected due to network failure at the first attempt.
PENDG-NW- TMOUT
Message rejected due to network or protocol failure at the first attempt.
140
PENDG-SMS- TMOUT Message rejected due to network or protocol failure at the first attempt.
150
PENDG-HDST- BUSY
160
The message is rejected because of congestion encountered at the visited MSC at
first attempt.
200 REMOTE NODE NOT
   REACHABLE
  Remote HLR or VLR route is not defined on adjacent node
210/190 

	 */
	
	
	
	
	static {
		
		unicellDlrMap = new HashMap<String, String>();
		
		unicellDlrMap.put("-01",SMSStatusCodes.SMPP_DLR_STATUS_UNKNOWN ); 
		unicellDlrMap.put("000", SMSStatusCodes.SMPP_DLR_STATUS_QUEUED);
		unicellDlrMap.put("001", SMSStatusCodes.SMPP_DLR_STATUS_DELIVERED);
		unicellDlrMap.put("002", SMSStatusCodes.SMPP_DLR_STATUS_FAILED);
		unicellDlrMap.put("004",SMSStatusCodes.SMPP_DLR_STATUS_NDNC_FAILED );
		unicellDlrMap.put("005", SMSStatusCodes.SMPP_DLR_STATUS_BLACKLIST );
		unicellDlrMap.put("007", SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
		unicellDlrMap.put("006", SMSStatusCodes.SMPP_DLR_STATUS_WHITELIST);
		unicellDlrMap.put("008", SMSStatusCodes.SMPP_DLR_STATUS_PREPAID_REJECTED);
		unicellDlrMap.put("009", SMSStatusCodes.SMPP_DLR_STATUS_NIGHT_PROMO_BLOCKED);
		unicellDlrMap.put("031",SMSStatusCodes.SMPP_DLR_STATUS_ROAMING_REJECTED );
		unicellDlrMap.put("032", SMSStatusCodes.SMPP_DLR_STATUS_MS_OUTOFMEMORY);
		unicellDlrMap.put("033", SMSStatusCodes.SMPP_DLR_STATUS_NETWORK_FAILURE);
		unicellDlrMap.put("034", SMSStatusCodes.SMPP_DLR_STATUS_PROTOCOL_FAILURE);
		unicellDlrMap.put("035", SMSStatusCodes.SMPP_DLR_STATUS_PROTOCOL_FAILURE);
		unicellDlrMap.put("036", SMSStatusCodes.SMPP_DLR_STATUS_PROVIDER_FAILURE);
		unicellDlrMap.put("037", SMSStatusCodes.SMPP_DLR_STATUS_TOOMANY_MSGS);
		unicellDlrMap.put("044", SMSStatusCodes.SMPP_DLR_STATUS_PROMO_BLOCKED);
		
		//Unicel provided intermediate status
		unicellDlrMap.put("110", SMSStatusCodes.SMPP_DLR_STATUS_PENDING_ABSENT_SUBSCRIBER);
		unicellDlrMap.put("120", SMSStatusCodes.SMPP_DLR_STATUS_PENDING_OUTOFMEMORY);
		unicellDlrMap.put("130", SMSStatusCodes.SMPP_DLR_STATUS_PENDING_NW_FAIL);
		unicellDlrMap.put("140", SMSStatusCodes.SMPP_DLR_STATUS_PENDING_NW_TIMEOUT);
		unicellDlrMap.put("150", SMSStatusCodes.SMPP_DLR_STATUS_PENDING_SMS_TIMEOUT);
		unicellDlrMap.put("160", SMSStatusCodes.SMPP_DLR_STATUS_PENDING_HANDSET_BUSY);
		unicellDlrMap.put("190", "User Abort");
		unicellDlrMap.put("200", "Node not reachable");
		unicellDlrMap.put("210", "User Abort");
		
		deliveredStatusSet = new HashSet<String>();
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_DELIVERED);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_QUEUED);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_TOOMANY_MSGS);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_PENDING_ABSENT_SUBSCRIBER);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_PENDING_OUTOFMEMORY);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_PENDING_NW_FAIL);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_PENDING_NW_TIMEOUT);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_PENDING_SMS_TIMEOUT);
		deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_PENDING_HANDSET_BUSY);
		
		suppressedStatusSet = new HashSet<String>();
		suppressedStatusSet.add(SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
		suppressedStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_BLACKLIST);
		
		
	}
	
	
	//private ApplicationContext context;
	private OCSMSGateway ocsmsGateway;
	 private SMSCampaignSentDao smsCampaignSentDao;
	 private SMSCampaignSentDaoForDML smsCampaignSentDaoForDML ;
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
	private Session unicellSession;
	/*private final String reportUrl = "";
	private final String checkBalUrl = "";*/
	private final String urlStr = "http://api.unicel.in/SendSMS/sendmsg.php";
	private final String queryString = "uname=<USERNAME>&pass=<PWD>&send=<SENDERID>&dest=<TO>&msg=<MESSAGE>&concat=<CONCAT>";
	
	public UnicelSMSGateway() {}
	
	public UnicelSMSGateway(OCSMSGateway ocsmsGateway) {
		
		this.ocsmsGateway = ocsmsGateway;
		//this.smsCount = 0;
		try {
			ServiceLocator serviceLocator = ServiceLocator.getInstance();
			smsCampaignSentDao = (SMSCampaignSentDao)serviceLocator.getDAOByName("smsCampaignSentDao");
			smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML )serviceLocator.getDAOForDMLByName("smsCampaignSentDaoForDML");
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
	}

	/*public UnicelSMSGateway(OCSMSGateway ocsmsGateway) {
		
		this.userID = userID;
		this.pwd = pwd;
		this.port = port;
		this.ip = ip;
		
		
		//this.context = context;
		this.ocsmsGateway = ocsmsGateway;
		this.smsCount = 0;
		
		smsCampaignSentDao = (SMSCampaignSentDao)context.getBean("smsCampaignSentDao");
		smsBouncesDao = (SMSBouncesDao)context.getBean("smsBouncesDao");
		smsCampaignReportDao = (SMSCampaignReportDao)context.getBean("smsCampaignReportDao");
		smsSuppressedContactsDao = (SMSSuppressedContactsDao)context.getBean("smsSuppressedContactsDao");
		contactsDao = (ContactsDao)context.getBean("contactsDao");
	}*/
	
	/*public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		
		this.pwd = pwd;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
*/
	public String sendOverHTTP(String UserID, String pwd, String content, String mobile, String senderId) {
			
		try {
			/*
			 * http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052346000&dcs=0&msgtxt=This is Test message&state=4 
			 */
			
			String postData = "";
			String data = URLEncoder.encode(content, "UTF-8"); 
			
			
			postData += this.queryString.replace("<USERNAME>", UserID ).replace("<PWD>", URLEncoder.encode(pwd, "UTF-8"))
				     .replace("<SENDERID>", senderId).replace("<TO>", mobile)
				     .replace("<MESSAGE>", data).replace("<CONCAT>", content.trim().length() <= 160 ? "0" : "1");
	
			
			logger.debug("postData======>"+postData);
			
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
			
			String msgID = response;
			return msgID;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::", e);
			return null;
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::", e);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception :::", e);
			return null;
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception :::", e);
			return null;
		}
		
	}
	
	public void sendSMSOverSMPP(String content, String mobilenumber, Long sentId, String senderId) throws BaseServiceException{
		
		if(SMSCConnectorObj == null ) SMSCConnectorObj = new SMSCConnector(ocsmsGateway);
		try {
			if(sessionProvider == null) sessionProvider = GatewaySessionProvider.getInstance(null);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("some error occured can not continue ", e1);
			throw new BaseServiceException("Exception, try after sometime");
		}
		if(unicellSession == null || !unicellSession.isBound() || !unicellSession.isOpened()) {
			
			try {
				unicellSession = SMSCConnectorObj.getSession();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error while getting session with unicel", e);
			}
		}
		
		if(unicellSession != null && !unicellSession.isBound() ) {
			
			UnicelSMSCPDUListener listener = null;//to avoid growing objcts in heap
			try {
				listener = new UnicelSMSCPDUListener(unicellSession, ocsmsGateway);
				SMSCConnectorObj.bindReq(unicellSession, listener);
			} catch (Exception e) {
				try {
					// TODO Auto-generated catch block
					if (e instanceof IOException || e instanceof SocketException){
					    //IOException relate to the brokenpipe issue 
					    //we need to close existing sessions and connections
					    //restablish session
						Connection connection = unicellSession.getConnection();
					    if (connection != null){
					    	connection.close();
					    }
					    unicellSession = SMSCConnectorObj.getSession();
					    SMSCConnectorObj.bindReq(unicellSession, listener);
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
		
		
		if(unicellSession != null && unicellSession.isBound()) {
			
			try {
				smsCount ++;
				SMSCConnectorObj.submitSm(unicellSession, content, mobilenumber, sentId, senderId);
			} catch (Exception e) {
				try {
					if (e instanceof IOException || e instanceof SocketException){
					    //IOException relate to the brokenpipe issue 
					    //we need to close existing sessions and connections
					    //restablish session
						Connection connection = unicellSession.getConnection();
					    if (connection != null){
					    	connection.close();
					    }
					    unicellSession = SMSCConnectorObj.getSession();
					    SMSCConnectorObj.bindReq(unicellSession, new UnicelSMSCPDUListener(unicellSession, ocsmsGateway));
					    SMSCConnectorObj.submitSm(unicellSession, content, mobilenumber, sentId, senderId);
					}
				} catch (IOException ioe1) {
					logger.debug("Exception", ioe1);
				} catch (Exception e1) {
					logger.debug("Exception", e1);
				}
			}
			
		}
		/*logger.debug("=====started sending====");
		SMSCConnectorObj.submitSMS(content, mobilenumber, sentId, senderId);
		logger.debug("=====ended here====");*/
	
		
		
		/*
		//singleton session 
		logger.debug("======entered sendSMSOverSMPP===>3");
		//if(SMSCConnectorObj == null ) SMSCConnectorObj = new SMSCConnector(ocsmsGateway);
		
		try {
			if(sessionProvider == null) sessionProvider = GatewaySessionProvider.getInstance();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("some error occured can not continue ", e1);
			throw new BaseServiceException("Exception, try after sometime");
		}
		if(unicellSession == null || !unicellSession.isBound() || !unicellSession.isOpened()) {
			
			try {
				unicellSession = sessionProvider.getSessionBy(ocsmsGateway);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error while getting session with unicel", e);
			}
		}
		logger.debug("======got unicellSession ===>5"+unicellSession + " bound? "+ unicellSession.isBound() +" opened?  "+ unicellSession.isOpened());
		if(unicellSession != null && !unicellSession.isBound() ) {
			
			try {
				//if(unicellSession == null || !unicellSession.isBound()) {
					
				GatewaySessionHelper gatewaySessionHelper = new GatewaySessionHelper();
				try {
					gatewaySessionHelper.checkSessionsAlive(null, sessionProvider);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("====error ====", e);
				}
					
			//	}
				//sessionProvider.bind(unicellSession, new UnicelSMSCPDUListener(unicellSession));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error while binding with unicel", e);
			}
			
		}
		
		
		if(unicellSession != null && unicellSession.isBound()) {
			
			try {
				smsCount ++;
				sessionProvider.submitSm(unicellSession, content, mobilenumber, sentId, senderId, ocsmsGateway);//Sm(unicellSession, content, mobilenumber, sentId, senderId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error while sending subit_sm PDU to unicel", e);
			}
			
		}
		//commented already
		logger.debug("=====started sending====");
		SMSCConnectorObj.submitSMS(content, mobilenumber, sentId, senderId);
		logger.debug("=====ended here====");
		//end
	*/}

	public  boolean getBalance(int totalCount) throws BaseServiceException{
		
		if(SMSCConnectorObj == null ) {
			logger.info("SMSCConnectorObj returend null ------------------------------------getBalance-------------");
			SMSCConnectorObj = new SMSCConnector(ocsmsGateway, false);
		}
		
		
		//SMPPLogicaSampleAPP SMSCConnectorObj = new SMPPLogicaSampleAPP(applicationContext, isTransactional, false);
		//TODO
		
		/*SMSCConnectorObj.setTransactional(isTransactional);
		SMSCConnectorObj.*/
		return SMSCConnectorObj.getBalance(totalCount, ocsmsGateway.getPostpaidBalURL());
		
	} 
	
	public void unbindSession() {
		
		if(SMSCConnectorObj == null ){
			SMSCConnectorObj = new SMSCConnector(ocsmsGateway);
			logger.info("SMSCConnectorObj Object returned Null>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
		SMSCConnectorObj.setEnded(true);
		SMSCConnectorObj.unbindSession(unicellSession);//TODO need to unbind
	}
	
	public void  pingUnicelToSendRestOfSMS() {
		
		try {
			//if(SMSCConnectorObj == null ) SMSCConnectorObj = new SMSCConnector(ocsmsGateway, true);
			
			//SMSCConnectorObj.setEnded(true);
			//SMSCConnectorObj.doFinalUpdate(updateStatusMap);
			
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
	}
	
	
	
	
	public synchronized void processDlrReciepts() throws Exception{
		//id:1114022511523684840 sub:001 dlvrd:001 submit date:1402251152 done date:1402251152 stat:DELIVRD err:001 Text:
		logger.debug("-------in process----"+dlrReciepts.size());
		
		if(dlrReciepts == null|| dlrReciepts.size() == 0) {
			logger.warn("Some error got, as this is called to process but no entries found.");
			return;
		}
		//process the msg_idsmap first
		Map<Long, String> updateStatusMap = getUpdateStatusMap();
		if(updateStatusMap != null && updateStatusMap.size() > 0 ){
			SMSCConnectorObj.processUpdateStatusMap(null, updateStatusMap, true);
			/*Map<Long, String> tempUpdateStatusMap = new HashMap<Long, String>();
			tempUpdateStatusMap.putAll(updateStatusMap);
			tempUpdateStatusMap.clear();*/
		}
		
		String[] dlrmsgTokenArr = null;
		String sentIdsStr = Constants.STRING_NILL;
		
		Map<String, String> updateSentDlrMap = new HashMap<String, String>();
		
		for (String recptMessage : dlrReciepts) {
			
			String msgID =  null;
			String seqNum = null;
			String statusCode = null;
			dlrmsgTokenArr = recptMessage.split(Constants.STRING_WHITESPACE);
			for (String token : dlrmsgTokenArr) {
				//logger.debug("token ::"+token);
					String[] tokenArr = token.split(Constants.DELIMETER_COLON);
				if(tokenArr[0].equals(Constants.SMS_DLR_STATUSCODE_TOKEN)) {
					
					statusCode = tokenArr[1];
				}else if(tokenArr[0].equals(Constants.SMS_DLR_MSGID_TOKEN)) {
					
					msgID = tokenArr[1];
					if(sentIdsStr.length() > 0) sentIdsStr += Constants.DELIMETER_COMMA;
					sentIdsStr += "'"+msgID+"'";
				}
				
				
			}//for
			
			updateSentDlrMap.put(msgID, statusCode);
			
		}//for
		
		if(sentIdsStr == null || sentIdsStr.isEmpty()) {
			logger.debug("No sent items found for msgIds== "+sentIdsStr);
			
			//TODO empty the dlrrecptset may be in synchronized block
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
					
				String status = unicellDlrMap.get(updateSentDlrMap.get(smsCampaignSent.getApiMsgId()));
				
				
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
				/*boolean isNonDelivered = true;
				for (String  dstate : deliveredStatusSet) {
					
					if(status.equalsIgnoreCase(dstate)) {
						isNonDelivered = false;
					}
				}*/
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
	}
	
	private void updateDlrChunk(List<SMSCampaignSent> listToBeUpdated, List<SMSBounces> smsBounceList,
			List<SMSSuppressedContacts> suppressedContactsList, List<String> contactMobilesList,
			Users user, Long smsCrId) throws Exception{
		logger.debug("=============entered into update chunk===========");
		try {
			ServiceLocator serviceLocator = ServiceLocator.getInstance();
			if(smsCampaignSentDao == null)smsCampaignSentDao = (SMSCampaignSentDao)serviceLocator.getDAOByName("smsCampaignSentDao");
			if(smsBouncesDao == null)smsBouncesDao = (SMSBouncesDao)serviceLocator.getDAOByName("smsBouncesDao");
			if(smsCampaignReportDao == null)smsCampaignReportDao = (SMSCampaignReportDao)serviceLocator.getDAOByName("smsCampaignReportDao");
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
		
		
	}
	
	//public void sendSMS(String content, String mobile, String senderId ) {}
	private class UnicelSMSCPDUListener implements ServerPDUEventListener{
		private Session unicelSession;
		private OCSMSGateway ocSMSGateway;
		
		public UnicelSMSCPDUListener() {}
		
		public UnicelSMSCPDUListener(Session unicelSession, OCSMSGateway ocsmsGateway) {
			
			this.unicelSession = unicelSession;
			this.ocSMSGateway = ocsmsGateway;
			
		}
		
		@Override
		public void handleEvent(ServerPDUEvent event) {
			// TODO Auto-generated method stub

			// TODO Auto-generated method stub
			try {
				// TODO Auto-generated method stub
				//super.handleEvent(event);
				
				PDU receivedPDU = event.getPDU();
				logger.debug("got an event"+receivedPDU+" receivedPDU ::"+receivedPDU.debugString()
						);
				if(receivedPDU instanceof DeliverSM) {
					
					/*DeliverSM receipt = (DeliverSM)receivedPDU;
					if(ocsmsGateway.isPullReports()) {
						logger.warn("No need of pulling reports "+receipt.debugString());
						return;
					}
					logger.debug(receipt.getShortMessage());
					logger.debug(receipt.getShortMessageData()+"  "+receipt.getSequenceNumber());
					//logger.debug("got elivery recpt from SMSC "+((DeliverSM)receivedPDU).getMessageState());
					synchronized (dlrReciepts) {
						
						dlrReciepts.add(receipt.getShortMessage());
						processDlrReciepts();
					}*/
					//TODO after deciding when to add and process
					/*if(dlrReciepts.size() >= 100) {
					//if(smsCount == dlrReciepts.size()) {
						logger.debug("calling for process ");
						processDlrReciepts();
						
					}//if
*/					
					/*if(currentDelievredMID != null && currentSubmitMID != null && currentDelievredMID.equals(currentSubmitMID)){
						//TODO decide when to unbind
						if(unicelSession != null) {
							UnbindResp resp = unicelSession.unbind();
							logger.debug("UnBind Response..............." + resp.debugString());
						}
					
					}//if
*/					
					
				}if(receivedPDU instanceof SubmitSMResp) {
					sessionProvider.procesReceivedPDU(receivedPDU, ocSMSGateway,unicelSession);
					/*logger.debug("got elivery recpt from SMSC "+((SubmitSMResp)receivedPDU).getMessageId());
					SubmitSMResp submitResponse = (SubmitSMResp)receivedPDU;*/
					//SMSCConnectorObj.processUpdateStatusMap(submitResponse, getUpdateStatusMap(), false);
					
					/*synchronized (updateStatusMap) {
						
						updateStatusMap.put(new Long(submitResponse.getSequenceNumber()), submitResponse.getMessageId());
					}
					if( updateStatusMap.size() >= 100 || SMSCConnectorObj.isEnded()  ) {
						Map<Long, String> tempUpdateStatusMap = new HashMap<Long, String>();
						tempUpdateStatusMap.putAll(updateStatusMap);
						SMSCConnectorObj.processUpdateStatusMap(tempUpdateStatusMap);
						tempUpdateStatusMap.clear();
						synchronized (updateStatusMap) {
							updateStatusMap.clear();
						}
		        			
	        		}//if
*/					
				}//if
				
			} 
			catch(Exception e){
				logger.error("Exception ----", e);
				
			}
			/*catch (ValueNotSetException e) {
				logger.error("Exception :::", e);
			} catch (TimeoutException e) {
				logger.error("Exception :::", e);
			} catch (PDUException e) {
				logger.error("Exception :::", e);
			} catch (WrongSessionStateException e) {
				logger.error("Exception :::", e);
			} catch (IOException e) {
				logger.error("Exception :::", e);
			}*/finally { /* //TODO after decided when to close session
				if(unicelSession != null) {
					 try {
						//UnbindResp resp = unicelSession.unbind();
						// logger.debug("UnBind Response..............." + resp.debugString());
					} catch (ValueNotSetException e) {
						// TODO Auto-generated catch block
						logger.error("Exception :::", e);
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						logger.error("Exception :::", e);
					} catch (PDUException e) {
						// TODO Auto-generated catch block
						logger.error("Exception :::", e);
					} catch (WrongSessionStateException e) {
						// TODO Auto-generated catch block
						logger.error("Exception :::", e);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error("Exception :::", e);
					}
					}
				
			*/}
			
	
		
		}
		
		
	}
	
	
	public static void main(String[] args) {
		String status = unicellDlrMap.get("034");
		logger.debug("status is "+status);
		if(!deliveredStatusSet.contains(status)) {
			logger.debug("not a delivered status");
		}
	}
}