package org.mq.captiway.scheduler.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusConsoleListener;
import org.bulatnig.smpp.pdu.Npi;
import org.bulatnig.smpp.pdu.Ton;
import org.mq.captiway.scheduler.SMPPSampleApp;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;
import org.smpp.Data;
import org.smpp.ServerPDUEvent;
import org.smpp.ServerPDUEventListener;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.TimeoutException;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.Address;
import org.smpp.pdu.AddressRange;
import org.smpp.pdu.BindReceiver;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransciever;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.DeliverSMResp;
import org.smpp.pdu.EnquireLink;
import org.smpp.pdu.EnquireLinkResp;
import org.smpp.pdu.PDU;
import org.smpp.pdu.PDUException;
import org.smpp.pdu.QuerySM;
import org.smpp.pdu.QuerySMResp;
import org.smpp.pdu.Response;
import org.smpp.pdu.SubmitSM;
import org.smpp.pdu.SubmitSMResp;
import org.smpp.pdu.Unbind;
import org.smpp.pdu.UnbindResp;
import org.smpp.pdu.ValueNotSetException;
import org.smpp.pdu.WrongLengthOfStringException;
import org.smpp.util.DefaultServerPDUEventListener;

public class SMSCConnector {//implements ServerPDUEventListener{

public SMSCConnector() {}
	
private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 
	//private ApplicationContext context;
	private String systemId;
	

	private String systemType;
	private String password;
	//private boolean isTransactional;
	private boolean isEnded;
	private String userId;
	private String accountPwd;
	private String ip;
	private boolean pullReports;
	


	public boolean isPullReports() {
		return pullReports;
	}

	public void setPullReports(boolean pullReports) {
		this.pullReports = pullReports;
	}

	private String port;
	/*public SMSCConnector(Session sess) {
		
		this.sess = sess;
	}*/
	
	public boolean isEnded() {
		return isEnded;
	}

	public void setEnded(boolean isEnded) {
		this.isEnded = isEnded;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	
	/*public boolean isTransactional() {
		return isTransactional;
	}

	public void setTransactional(boolean isTransactional) {
		this.isTransactional = isTransactional;
	}*/

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAccountPwd() {
		return accountPwd;
	}

	public void setAccountPwd(String accountPwd) {
		this.accountPwd = accountPwd;
	}

	 private SMSCampaignSentDao smsCampaignSentDao;
	 private SMSCampaignSentDaoForDML smsCampaignSentDaoForDML;
	// private int smsCount;
	public SMSCConnector( OCSMSGateway ocsmsGateway, boolean isEnded) {
		
		//this.context = context;
		//this.isTransactional = isTransactional;
		this.isEnded = isEnded;
		//smsCount = 0;
		//this.context = context;
		this.systemId = ocsmsGateway.getSystemId();
		this.systemType = ocsmsGateway.getSystemType();
		this.password = ocsmsGateway.getSystemPwd();
		this.userId = ocsmsGateway.getUserId();
		this.accountPwd = ocsmsGateway.getPwd();
		this.ip = ocsmsGateway.getIp();
		this.port = ocsmsGateway.getPort();
		this.pullReports = ocsmsGateway.isPullReports();
		//if(isTransactional) {
			
			/*systemId = "amith";
			systemType = "amith";
			password = "amith123";
			userId = "amith.lulla@optculture.com";
			accountPwd = "amithl";*/
		/*}else {
			
			systemId = "culture";
			systemType = "culture";
			password = "opt123";
			userId = "mallika.naidu@optculture.com";
			accountPwd = "mallika";
			
		}*/
		
		//smsCampaignSentDao = (SMSCampaignSentDao)context.getBean("smsCampaignSentDao");
		
	}
	
public SMSCConnector(OCSMSGateway ocsmsGateway) {
		
		
		//smsCount = 0;
		//this.context = context;
		this.systemId = ocsmsGateway.getSystemId();
		this.systemType = ocsmsGateway.getSystemType();
		this.password = ocsmsGateway.getSystemPwd();
		this.userId = ocsmsGateway.getUserId();
		this.accountPwd = ocsmsGateway.getPwd();
		this.ip = ocsmsGateway.getIp();
		this.port = ocsmsGateway.getPort();
		this.pullReports = ocsmsGateway.isPullReports();
		
		//this.isTransactional = isTransactional;
		//if(isTransactional) {
			
			/*this.systemId = "amith";
			this.systemType = "amith";
			this.password = "amith123";
			this.userId = "amith.lulla@optculture.com";
			this.accountPwd = "amithl";*/
		//}
		/*else {
			
			systemId = "culture";
			systemType = "culture";
			password = "opt123";
			userId = "mallika.naidu@optculture.com";
			accountPwd = "mallika";
			
		}
		*/
		//smsCampaignSentDao = (SMSCampaignSentDao)context.getBean("smsCampaignSentDao");
		//smsCampaignSentDao = (SMSCampaignSentDao)context.getBean("smsCampaignSentDao");
	}


/*public SMSCConnector() {
	
	//this.context = context;
	this.isTransactional = isTransactional;
	if(isTransactional) {
		
		systemId = "amith";
		systemType = "amith";
		password = "amith123";
		userId = "amith.lulla@optculture.com";
		accountPwd = "amithl";
	}else {
		
		systemId = "culture";
		systemType = "culture";
		password = "opt123";
		userId = "mallika.naidu@optculture.com";
		accountPwd = "mallika";
		
	}
	
	//smsCampaignSentDao = (SMSCampaignSentDao)context.getBean("smsCampaignSentDao");
	
}*/

	
	
	/*private  Map<Long, String> updateStatusMap = new LinkedHashMap<Long, String>();
	private  Set<String> dlrReciepts = new HashSet<String>();
	
	private  Session sess = null;
	*/
	
	/*@Override
		public void handleEvent(ServerPDUEvent event) {
			// TODO Auto-generated method stub
			try {
				// TODO Auto-generated method stub
				//super.handleEvent(event);
				
				PDU receivedPDU = event.getPDU();
				logger.debug("got an event"+receivedPDU+" receivedPDU ::"+receivedPDU.debugString()
						);
				if(receivedPDU instanceof DeliverSM) {
					
					DeliverSM receipt = (DeliverSM)receivedPDU;
					if(isPullReports()) {
						logger.warn("No need of pulling reports "+receipt.debugString());
						return;
					}
					logger.debug(receipt.getShortMessage());
					logger.debug(receipt.getShortMessageData()+"  "+receipt.getSequenceNumber());
					//logger.debug("got elivery recpt from SMSC "+((DeliverSM)receivedPDU).getMessageState());
					
					dlrReciepts.add(receipt.getShortMessage()+Constants.STRING_WHITESPACE+
							Constants.SMS_DLR_SEQ_NUMBER_TOKEN+Constants.DELIMETER_COLON+receipt.getSequenceNumber());
					
					if(dlrReciepts.size() >= 100) {
						
						processDlrReciepts();
						
					}//if
					
					if(sess != null) {
						UnbindResp resp = sess.unbind();
						logger.debug("UnBind Response..............." + resp.debugString());
					}
					
					
					
					
				}if(receivedPDU instanceof SubmitSMResp) {
					
					logger.debug("got elivery recpt from SMSC "+((SubmitSMResp)receivedPDU).getMessageId());
					SubmitSMResp submitResponse = (SubmitSMResp)receivedPDU;
					updateStatusMap.put(new Long(submitResponse.getSequenceNumber()), submitResponse.getMessageId());
					if( (smsCount >= 100 || isEnded) && smsCount == updateStatusMap.size() ) {
		        		
						
						processUpdateStatusMap();
		        		
		        			
	        		}//if
					
					
					
					
				}//if
				
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
			}finally {
				if(sess != null) {
					 try {
						UnbindResp resp = sess.unbind();
						 logger.debug("UnBind Response..............." + resp.debugString());
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
				
			}
			
	
		}*/
	
	
	public  void processDlrReciepts(Set<String> dlrReciepts ) throws BaseServiceException{
		//id:1114022511523684840 sub:001 dlvrd:001 submit date:1402251152 done date:1402251152 stat:DELIVRD err:001 Text:
		if(dlrReciepts == null|| dlrReciepts.size() == 0) {
			logger.warn("Some error got, as this is called to process but no entries found.");
			return;
		}
		if(smsCampaignSentDao == null) {
			
			try {
				smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNSENT_DAO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("no sms sent dao found ");
			}
		}
		String[] dlrmsgTokenArr = null;
		String sentIdsStr = Constants.STRING_NILL;
		
		Map<Long, String> updateSentDlrMap = new HashMap<Long, String>();
		
		for (String recptMessage : dlrReciepts) {
			String msgID =  null;
			String seqNum = null;
			String statusCode = null;
			dlrmsgTokenArr = recptMessage.split(Constants.STRING_WHITESPACE);
			for (String token : dlrmsgTokenArr) {
				
					String[] tokenArr = token.split(Constants.DELIMETER_COLON);
				if(tokenArr[0].equals(Constants.SMS_DLR_STATUSCODE_TOKEN)) {
					
					statusCode = tokenArr[1];
				}else if(tokenArr[0].equals(Constants.SMS_DLR_SEQ_NUMBER_TOKEN)) {
					
					seqNum = tokenArr[1];
					
					if(sentIdsStr.length() > 0) sentIdsStr += Constants.DELIMETER_COMMA;
					sentIdsStr += seqNum;
					
				}else if(tokenArr[0].equals(Constants.SMS_DLR_MSGID_TOKEN)) {
					
					msgID = tokenArr[1];
				}
				
				
			}
			
			updateSentDlrMap.put(Long.parseLong(seqNum), statusCode);
			
		}
		
		if(sentIdsStr == null || sentIdsStr.isEmpty()) {
			
			//TODO empty the dlrrecptset may be in synchronized block
			return;
		}
		List<SMSCampaignSent> sentList = smsCampaignSentDao.findByIds(sentIdsStr);
		if(sentList == null ){
			//TODO
			return;
		}
		
		for (SMSCampaignSent smsCampaignSent : sentList) {
			
			
			
			
		}
		
		
	}
	
	
	public synchronized int processUpdateStatusMap(SubmitSMResp submitSMRespPDUObj, Map<Long, String> updateStatusMap, boolean isForceUpdate) throws BaseServiceException{
		
		if(submitSMRespPDUObj != null)updateStatusMap.put(new Long(submitSMRespPDUObj.getSequenceNumber()), submitSMRespPDUObj.getMessageId());
		
		if(updateStatusMap == null || updateStatusMap.size() <= 0) return 0;
		
		if(!( updateStatusMap.size() >= 100 || this.isEnded() )  && !isForceUpdate ) {
			
				return 0;
		}//if
		
		
		int updatedCount = 0;
		Set<Long> sentIdKeySet = updateStatusMap.keySet();
		String sentIdsStr = Constants.STRING_NILL;
		for (Long sentIdL : sentIdKeySet) {
			
			sentIdsStr += sentIdsStr.isEmpty() ? sentIdL.longValue()+sentIdsStr 
					: Constants.DELIMETER_COMMA+sentIdL.longValue();
			
		}
		if(smsCampaignSentDao == null) {
			
			try {
				smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNSENT_DAO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("no sms sent dao found ");
			}
		}
		
		 logger.debug(" sentIdsStr "+sentIdsStr );
		List<SMSCampaignSent> retList = smsCampaignSentDao.findByIds(sentIdsStr);
		
		if(retList != null && retList.size() > 0 && retList.size() == sentIdKeySet.size()) {
			
			 logger.debug(" update msgids "+retList.size()  );
			
			List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
			
			for (SMSCampaignSent smsCampaignSent : retList) {
				
				smsCampaignSent.setApiMsgId(updateStatusMap.get(smsCampaignSent.getSentId()));
				listToBeUpdated.add(smsCampaignSent);
				
			}
			
			if(listToBeUpdated.size() > 0) {
				
				updatedCount = listToBeUpdated.size();
				//smsCampaignSentDao.saveByCollection(listToBeUpdated);
				smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);
				listToBeUpdated.clear();
				updateStatusMap.clear();
				/*updateStatusMap.clear();
				smsCount = 0;*/
			}	
				       				
		}
		
		return updatedCount;
		
	}//processUpdateStatusMap
	
	public Session getSession() throws Exception{
		
		Session sess = null;
		
		
		TCPIPConnection connection = null;
	    
	    /*connection = 
	        new TCPIPConnection("59.162.167.36", 29000);*/
	    connection = 
    	        new TCPIPConnection(this.getIp(), Integer.parseInt(this.getPort()));
	    connection.setReceiveTimeout(3000);
	    sess = new Session(connection);
		
	    return sess;
	}
	
	public void bind(Session mySession, ServerPDUEventListener mylistener) throws Exception{
		
		AddressRange addRange = new AddressRange();
		addRange.setNpi((byte)Npi.UNKNOWN);
		addRange.setTon((byte)Ton.ALPHANUMERIC);
		
		final BindRequest request = new BindReceiver();
	   /* request.setSystemId("amith");
	    request.setPassword("amith123");
	    request.setSystemType("amith");*/
	    request.setSystemId(this.getSystemId());
	    request.setPassword(this.getPassword());
	   // request.setSystemType("amith");
	    if(this.getSystemType() != null)request.setSystemType(this.getSystemType());
	    request.setAddressRange(addRange);
	    request.setInterfaceVersion((byte) 0x34);
	    logger.debug("Send bind request...");
	    final BindResponse response = mySession.bind(request, mylistener);
	    logger.debug(" bind response..."+response);
		
	}
	
public void bindReq(Session mySession, ServerPDUEventListener mylistener) throws Exception{
		
		AddressRange addRange = new AddressRange();
		addRange.setNpi((byte)Npi.UNKNOWN);
		addRange.setTon((byte)Ton.ALPHANUMERIC);
		
		final BindRequest request = new BindTransmitter();
	   /* request.setSystemId("amith");
	    request.setPassword("amith123");
	    request.setSystemType("amith");*/
	    request.setSystemId(this.getSystemId());
	    request.setPassword(this.getPassword());
	   // request.setSystemType("amith");
	    if(this.getSystemType() != null)request.setSystemType(this.getSystemType());
	    request.setAddressRange(addRange);
	    request.setInterfaceVersion((byte) 0x34);
	    logger.debug("Send bind request...");
	    final BindResponse response = mySession.bind(request, mylistener);
	    logger.debug(" bind response..."+response);
		
	}
public void submitSmReq(Session mysession, String content, String mobile, Long sentId, String senderId) throws Exception{
	
	final SubmitSM submitRequest = new SubmitSM();
	 if(senderId != null)submitRequest.setSourceAddr(createAddress(senderId));
	    
	   // request.setSourceAddr(createAddress("OPTCLT"));
	    submitRequest.setDestAddr(mobile);
	    submitRequest.setRegisteredDelivery((byte)1);//registered_delivery" field is used to request for delivery confirmation(s). This field can only be set at PDU level, not a bind level.
	    submitRequest.setShortMessage(content);
	   // submitRequest.setScheduleDeliveryTime("");
	    submitRequest.setReplaceIfPresentFlag((byte) 0);
	    submitRequest.setEsmClass((byte) 0);
	    submitRequest.setProtocolId((byte) 0);
	    submitRequest.setPriorityFlag((byte) 0);
	    
	    if(!isPullReports())submitRequest.setRegisteredDelivery((byte) 1);
	    
	    submitRequest.setDataCoding((byte) 0);
	    submitRequest.setSmDefaultMsgId((byte) 0);
	    submitRequest.setSequenceNumber(sentId.intValue());
	    logger.debug("=======Request========" + submitRequest.debugString());
	    
	    if( !mysession.isOpened() || !mysession.isBound()) {
	    	
	    	logger.debug("============ no connection ======="+mysession.isOpened()+"  "+mysession.isBound()+" ");
	    	
	    }
	    
	  mysession.submit(submitRequest);

	
}

	public void submitSm(Session mysession, String content, String mobile, Long sentId, String senderId) throws Exception{
		
		final SubmitSM submitRequest = new SubmitSM();
		 if(senderId != null)submitRequest.setSourceAddr(createAddress(senderId));
 	    
  	   // request.setSourceAddr(createAddress("OPTCLT"));
  	    submitRequest.setDestAddr(mobile);
  	    submitRequest.setRegisteredDelivery((byte)1);//registered_delivery" field is used to request for delivery confirmation(s). This field can only be set at PDU level, not a bind level.
  	    submitRequest.setShortMessage(content);
  	   // submitRequest.setScheduleDeliveryTime("");
  	    submitRequest.setReplaceIfPresentFlag((byte) 0);
  	    submitRequest.setEsmClass((byte) 0);
  	    submitRequest.setProtocolId((byte) 0);
  	    submitRequest.setPriorityFlag((byte) 0);
  	    
  	    if(!isPullReports())submitRequest.setRegisteredDelivery((byte) 1);
  	    
  	    submitRequest.setDataCoding((byte) 0);
  	    submitRequest.setSmDefaultMsgId((byte) 0);
  	    submitRequest.setSequenceNumber(sentId.intValue());
  	    logger.debug("=======Request========" + submitRequest.debugString());
  	    
  	    if( !mysession.isOpened() || !mysession.isBound()) {
  	    	
  	    	logger.debug("============ no connection ======="+mysession.isOpened()+"  "+mysession.isBound()+" ");
  	    	
  	    }
  	    
  	  mysession.submit(submitRequest);

  	  logger.debug("Message is Sent Successfully");
	}
	
	/*public void submitSMS(String content, String mobile, Long sentId, String senderId) {
		//Session sess = null;
    	try {
    		AddressRange addRange = new AddressRange();
    		addRange.setNpi((byte)Npi.UNKNOWN);
    		addRange.setTon((byte)Ton.ALPHANUMERIC);
//    		addRange.setAddressRange("9*");
    		if(sess == null || !sess.isBound()) {
    			
    			TCPIPConnection connection = null;
	    	    final BindRequest request = new BindReceiver();
	    	    request.setSystemId("amith");
	    	    request.setPassword("amith123");
	    	    request.setSystemType("amith");
	    	    request.setSystemId(getSystemId());
	    	    request.setPassword(getPassword());
	    	    if(getSystemType() != null)request.setSystemType(getSystemType());
	    	    request.setAddressRange(addRange);
	    	    request.setInterfaceVersion((byte) 0x34);
	    	    connection = 
	    	        new TCPIPConnection("59.162.167.36", 29000);
	    	    connection = 
		    	        new TCPIPConnection(getIp(), Integer.parseInt(getPort()));
	    	    connection.setReceiveTimeout(3000);
	    	    sess = new Session(connection);
	    	    
	    	    logger.debug("Send bind request...");
	    	    final BindResponse response = sess.bind(request, new SMSCConnector(sess));
	    	    logger.debug("Bind response " + response.debugString());
    		}
    	   
    		
    		
    	    final SubmitSM submitRequest = new SubmitSM();
    	    //request.setServiceType("amith");
    	    
    	    if(senderId != null)submitRequest.setSourceAddr(createAddress(senderId));
    	    
    	   // request.setSourceAddr(createAddress("OPTCLT"));
    	    submitRequest.setDestAddr(mobile);
    	    submitRequest.setRegisteredDelivery((byte)1);//registered_delivery" field is used to request for delivery confirmation(s). This field can only be set at PDU level, not a bind level.
    	    submitRequest.setShortMessage(content);
    	   // submitRequest.setScheduleDeliveryTime("");
    	    submitRequest.setReplaceIfPresentFlag((byte) 0);
    	    submitRequest.setEsmClass((byte) 0);
    	    submitRequest.setProtocolId((byte) 0);
    	    submitRequest.setPriorityFlag((byte) 0);
    	    
    	    if(!isPullReports())submitRequest.setRegisteredDelivery((byte) 1);
    	    
    	    submitRequest.setDataCoding((byte) 0);
    	    submitRequest.setSmDefaultMsgId((byte) 0);
    	    submitRequest.setSequenceNumber(sentId.intValue());
    	    logger.debug("Request............." + submitRequest.debugString());
    	    
    	    if( !sess.isOpened() || !sess.isBound()) {
    	    	
    	    	logger.debug("============ no connection ======="+sess.isOpened()+"  "+sess.isBound()+" ");
    	    	
    	    }
    	    
    	    sess.submit(submitRequest);

    	  
    	} catch (Throwable e) {
    	    logger.error("Exception",e);   
    	}

    	
}*/
		
	private static Address createAddress(String address) 
	    throws WrongLengthOfStringException {
		Address addressInst = new Address();
		addressInst.setTon((byte) Ton.ALPHANUMERIC); // national ton
		addressInst.setNpi((byte) Npi.UNKNOWN); // numeric plan indicator
		addressInst.setAddress(address, Data.SM_ADDR_LEN);
		return addressInst;
	}
		
		
	public static void query_SM() {


     	
     	
     	Session sess = null;
     	TCPIPConnection connection = null;
     	try {
     		AddressRange addRange = new AddressRange();
     		addRange.setNpi((byte)1);
     		addRange.setTon((byte)1);
//     		addRange.setAddressRange("9*");
     		
     		 
     		
     	    final BindRequest request = new BindTransciever();
     	   /* request.setSystemId("amith");
     	    request.setPassword("amith123");
     	    request.setSystemType("amith");*/
     	   request.setSystemId("magpromo");
    	    request.setPassword("S)t3Y(y@");
    	   // request.setSystemType("amith");
     	    request.setAddressRange(addRange);
     	    request.setInterfaceVersion((byte) 0x34);
     	      connection = 
     	        new TCPIPConnection("smpp1.unicel.in", 51612);
     	    connection.setReceiveTimeout(3000);
     	    sess = new Session(connection);
     	    logger.debug("");
     	   logger.debug("Send bind request...");
     	    final BindResponse response = sess.bind(request);
     	    if(response.getCommandStatus() == Data.ESME_RALYBND) {
     	    	UnbindResp resp =  sess.unbind();
     	    	
     	    	logger.debug("response..."+resp.debugString());
     	    	
     	    }
     	    
     	    EnquireLink link = new EnquireLink();
     	   logger.debug("Bind response " +response.getCommandStatus()+sess.isBound()+ response.debugString()+(response.getCommandStatus() == Data.ESME_ROK) );
     	    //Thread.sleep(12000);
     	} catch (Exception ex){
            //Analyze what type of exception was
            if (ex instanceof IOException || ex instanceof SocketException){
                //IOException relate to the brokenpipe issue you are facing
                //you need to close existing sessions and connections
                //restablish session
                if (connection!=null){
                    try {
						connection.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error("Exception",e);
					}
                }
                //This is a recursive call, I encourage you to elaborate
                //a little bit this method implementing a counter so you
                //don't end up in an infinite loop
               
            } else {
                //LOG whatever other exception thrown
            	logger.debug("in Exception...");
            	logger.error("Exception :::", ex);
            } 
    }finally{
     		/*try {
     			logger.debug("closing the session");
					sess.close();
				} catch (WrongSessionStateException e) {
					// TODO Auto-generated catch block
					logger.error("Exception",e);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception",e);
				}*/
     	}
     	
     	
     	try {
     	    final QuerySM request = new QuerySM();
     	    //request.setServiceType("amith");
     	   /* request.setSourceAddr(createAddress("OPTCLT"));
     	    request.setDestAddr("9490927928");
     	    request.setShortMessage("Thanking you for shopping at Tresmode, we value your relationship with us and look forward to serving you again.");
     	 */ 
     	    //request.setMessageId("1114022416450310030");
     	   //request.setMessageId("1114022417583674870");
     	  request.setMessageId("1114022418013086280");
     	   logger.debug("Request............." + request.debugString());
     	    final QuerySMResp response = sess.query(request);//(request);//submit(request);
     	    Thread.sleep(6000);
     	   logger.debug("Submit response " + response.debugString() + 
     	        ", message id " + response.getMessageId()+ " state ::"+response.getMessageState());
//     	    Thread.sleep(6000);
     	    UnbindResp resp = sess.unbind();
     	   logger.debug("UnBind Response..............." + resp.debugString());
     	} catch (Throwable e) {
     	    logger.error("Exception :::", e);   
     	}
		
	}
	
	public static void delivery_SM() {

     	
     	
     	Session sess = null;
     	TCPIPConnection connection = null;
     	try {
     		AddressRange addRange = new AddressRange();
     		addRange.setNpi((byte)1);
     		addRange.setTon((byte)1);
//     		addRange.setAddressRange("9*");
     		
     		 
     		
     	    final BindRequest request = new BindTransciever();
     	   /* request.setSystemId("amith");
     	    request.setPassword("amith123");
     	    request.setSystemType("amith");*/
     	   request.setSystemId("magpromo");
    	    request.setPassword("S)t3Y(y@");
    	   // request.setSystemType("amith");
     	    request.setAddressRange(addRange);
     	    request.setInterfaceVersion((byte) 0x34);
     	      connection = 
     	        new TCPIPConnection("smpp1.unicel.in", 51612);
     	    connection.setReceiveTimeout(3000);
     	    sess = new Session(connection);
     	   logger.debug("Send bind request...");
     	    final BindResponse response = sess.bind(request);
     	    if(response.getCommandStatus() == Data.ESME_RALYBND) {
     	    	UnbindResp resp =  sess.unbind();
     	    	
     	    	logger.debug("response..."+resp.debugString());
     	    	
     	    }
     	    
     	    EnquireLink link = new EnquireLink();
     	   logger.debug("Bind response " +response.getCommandStatus()+sess.isBound()+ response.debugString()+(response.getCommandStatus() == Data.ESME_ROK) );
     	    //Thread.sleep(12000);
     	} catch (Exception ex){
            //Analyze what type of exception was
            if (ex instanceof IOException || ex instanceof SocketException){
                //IOException relate to the brokenpipe issue you are facing
                //you need to close existing sessions and connections
                //restablish session
                if (connection!=null){
                    try {
						connection.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error("Exception",e);
					}
                }
                //This is a recursive call, I encourage you to elaborate
                //a little bit this method implementing a counter so you
                //don't end up in an infinite loop
               
            } else {
                //LOG whatever other exception thrown
            	logger.debug("in Exception...");
            	logger.error("Exception :::", ex);
            } 
    }finally{
     		/*try {
     			logger.debug("closing the session");
					sess.close();
				} catch (WrongSessionStateException e) {
					// TODO Auto-generated catch block
					logger.error("Exception",e);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception",e);
				}*/
     	}
     	
     	
     	try {
     	    final DeliverSM request = new DeliverSM();
     	   // request.setServiceType("amith");
     	   /* request.setSourceAddr(createAddress("OPTCLT"));
     	    request.setDestAddr("9490927928");
     	    request.setShortMessage("Thanking you for shopping at Tresmode, we value your relationship with us and look forward to serving you again.");
     	 */ 
     	    request.setReceiptedMessageId("1114022416450310030");
     	    request.setEsmClass((byte) 0);
     	    request.setProtocolId((byte) 0);
     	    request.setPriorityFlag((byte) 0);
     	    request.setDataCoding((byte) 0);
     	   logger.debug("Request............." + request.debugString());
     	    final DeliverSMResp response = sess.deliver(request);//submit(request);
     	    Thread.sleep(6000);
     	   logger.debug("Submit response " + response.debugString() + 
     	        ", message id " + response.getMessageId());
//     	    Thread.sleep(6000);
     	    UnbindResp resp = sess.unbind();
     	   logger.debug("UnBind Response..............." + resp.debugString());
     	} catch (Throwable e) {
     	    logger.error("Exception :::", e);   
     	}
     	

     	

		
		
	}

		
	
	
	 public static void main(String args[]) {

	     	
	     	Session sess = null;
	     	 Session sess1 = null;
	     	TCPIPConnection connection = null;
	     	try {
	     		AddressRange addRange = new AddressRange();
	     		addRange.setNpi((byte)1);
	     		addRange.setTon((byte)1);
//	     		addRange.setAddressRange("9*");
	     		
	     		
	     		
	     	    final BindRequest request = new BindReceiver();
	     	    request.setSystemId("magtrans");
	     	    request.setPassword("8&6hyg%6");
	     	    //request.setSystemType("magpromo");
	     	    //request.setAddressRange(addRange);
	     	    request.setInterfaceVersion((byte) 0x34);
	     	      connection = 
	     	        new TCPIPConnection("smpp1.unicel.in", 52612);
	     	    connection.setReceiveTimeout(3000);
	     	    sess = new Session(connection );
	     	    
	     	   // sess1 = new Session(connection );
	     	    
	     	   mainMethodListener connector = new SMSCConnector().new mainMethodListener(sess);
	     	    
	     	    logger.debug("Send bind request...");
	     	    final BindResponse response = sess.bind(request,  connector);
	     	   logger.debug("response..."+response.debugString());
	     	   /* if(response.getCommandStatus() == Data.ESME_RALYBND) {
	     	    	//UnbindResp resp =  sess.unbind();
	     	    	
	     	    	//logger.debug("response..."+resp.debugString());
	     	    	
	     	    }*/
	     	   //final BindResponse response1 = sess1.bind(request,  connector);
	     	 // logger.debug("response..."+response1.debugString());
	     	   
	     	    /*if(response1.getCommandStatus() == Data.ESME_RALYBND) {
	     	    	//UnbindResp resp =  sess.unbind();
	     	    	
	     	    	//logger.debug("response..."+resp.debugString());
	     	    	
	     	    }*/
	     	    EnquireLink link = new EnquireLink();
	     	    logger.debug("Bind response " +response.getCommandStatus()+sess.isBound()+ response.debugString()+(response.getCommandStatus() == Data.ESME_ROK) );
	     	    //Thread.sleep(12000);
	     	} catch (Exception ex){
	            //Analyze what type of exception was
	            if (ex instanceof IOException || ex instanceof SocketException){
	                //IOException relate to the brokenpipe issue you are facing
	                //you need to close existing sessions and connections
	                //restablish session
	                /*if (connection!=null){
	                    try {
							connection.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							logger.error("Exception :::", e);
						}
	                }*/
	                //This is a recursive call, I encourage you to elaborate
	                //a little bit this method implementing a counter so you
	                //don't end up in an infinite loop
	               
	            } else {
	                //LOG whatever other exception thrown
	            	logger.debug("in Exception...");
	            	logger.error("Exception :::", ex);
	            } 
	    }finally{/*
	     		try {
	     			logger.debug("closing the session");
						sess.close();
					} catch (WrongSessionStateException e) {
						// TODO Auto-generated catch block
						logger.error("Exception",e);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception",e);
					}
	     	*/}
	     	
	     	
	     	try {
	     	    final SubmitSM request = new SubmitSM();
	     	    
	     	   // request.setServiceType("amith");
	     	    request.setSourceAddr(createAddress("TRMODE"));
	     	    request.setDestAddr("9052346000");
	     	    request.setShortMessage("Tresmode thanks you for your patronage. Stay connected! To get regular updates on events and offers in your region, give a missed call on 09611755995 now.");
	     	   // request.setScheduleDeliveryTime("");
	     	    request.setReplaceIfPresentFlag((byte) 0);
	     	    request.setEsmClass((byte) 0);
	     	    request.setProtocolId((byte) 0);
	     	    request.setPriorityFlag((byte) 0);
	     	    request.setRegisteredDelivery((byte) 1);
	     	    request.setDataCoding((byte) 0);
	     	    request.setSmDefaultMsgId((byte) 0);
	     	    request.setSequenceNumber(100);//my own sequence number i is not taking y?
	     	    logger.debug("Request............." + request.debugString());
//	     	    Response resp = sess.submit(request);
	     	   
	     	    sess.submit(request);
	     	   // sess1.submit(request);
	     	  // Thread.sleep(6000);
	     	   /* logger.debug("Submit response " + response.debugString() + 
	     	        ", message id " + response.getMessageId());*/
	     	    //Thread.sleep(60000);
	     	    /*
	     	     * Send bind request...
Bind response 0true(bindresp: (pdu: 25 80000001 0 1) SMPPSERV) true
Request.............(submit: (pdu: 0 4 0 [2]) (addr: 0 0 )  (addr: 0 0 9490927928)  (sm: enc: ASCII msg: Test - Promotional SMS)  (opt: ) ) 
receipt (deliver: (pdu: 178 5 0 419559640) (addr: 0 0 Alerts)  (addr: 0 0 919490927928)  (sm: msg: id:1114022518040426310 sub:001 dlvrd:001 submit date:1402251804 done date:0000000000 stat:EXP-MSG-Q-EXD err:037 Text:Test - Pro)  (opt: ) ) 
receipt shortmessage id:1114022518040426310 sub:001 dlvrd:001 submit date:1402251804 done date:0000000000 stat:EXP-MSG-Q-EXD err:037 Text:Test - Pro
receipt sequencenum 419559640
got elivery recpt from SMSC 1114022518310161360

	     	     */
	     	    
	     	   
	     	} catch (Throwable e) {
	     	    logger.error("Exception :::", e);   
	     	}finally{
	     		try {
					Thread.sleep(6000);
					sess.unbind();
				} catch (ValueNotSetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PDUException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WrongSessionStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	     	}
	     	

	     	
	     	
			
			
			
			
			
		     	

     	

		 
	 //delivery_SM();//(pdu: 36 80000005 0 2) 1114022416464114150 ) , message id 1114022416464114150
	 
		// query_SM();//(pdu: 39 80000003 0 2) 1114022416450310030  0 0 ) , message id 1114022416450310030
	 }
	
	
	 public Map<String, String> getReports(String msgIdStr, String targetUrl) {
		 if(targetUrl == null || targetUrl.isEmpty()) return null;
			Map<String, String> responseStatusCodeMap = new LinkedHashMap<String, String>();
		 try {
				/*
				 * http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052346000&dcs=0&msgtxt=This is Test message&state=4
				 * report response is======>Status=0,oa=56263,da=919490927928,report=S,sdate=2013-12-06 12:03:24,rdate=2013-12-06 12:03:26,tid=59071138631160420375,msg=Thanks%2Bfor%2Bshopping%2Bwith%2BTresmode-promotion 
				 */
			 
			 //59071138659470666581
			 //59071138659474066595
			 
			 /**
			  * D: Delivered
				E: Expired (Out of coverage area, Switched off, Insufficient Memory, Tower station Failure, No routing info available from HLR)
				C: Cancelled (Absent Subscriber, Memory Capacity Exceeded, Mobile Equipment Error, Network Error, Barring, NDNC Failed)
				S: Pending at operator end (out of coverage area, inbox is full, handset is switched off,..)
				N: Cancelled due to DND
				X: Cancelled due to DND at operator end.
				U: Failed due to Number Portability issue.
			  */
				
			 	
			 
				String postData = "";
				
				
				String[] msgIdsArr = msgIdStr.split(Constants.DELIMETER_COMMA);
				
				boolean isAddComma = false;
				boolean isConnected = false;
				
			
				
				
				for (int i = 0; i < msgIdsArr.length;i++) {
					isConnected = false;
					if(i == 0) {
						
						postData = "user="+userId+":"+accountPwd+"&tid=";
						
						//postData = "user=amith.lulla@optculture.com:amithl&tid=";
						//postData = "user=mallika.naidu@optculture.com:mallika&tid=";
						isAddComma = false;
					}
					if(isAddComma) postData += Constants.DELIMETER_COMMA;
					
					
					if(postData.length() + msgIdsArr[i].length() <= 150) {
						responseStatusCodeMap.put(msgIdsArr[i], null);
						logger.debug("put ======>"+msgIdsArr[i]);
						postData += msgIdsArr[i];
						isAddComma = true;
					}else{
						
						try{
							isConnected = true;
							logger.debug("before sending post data is===="+postData);
							
							URL url = new URL(targetUrl);
							
							HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
							
							urlconnection.setConnectTimeout(20000);
							urlconnection.setReadTimeout(20000);
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
							logger.debug("report response is======>"+response);
							
							if(response != null && response.startsWith("Status=0,")) {
								
								/*boolean successResponse =  response.startsWith("Status=0,");
								if(successResponse)
									*/
								String status = Constants.STRING_NILL;;
								String tid = Constants.STRING_NILL;
								
								String[] OATokenArr = response.split("oa=");
								logger.debug("token arr size======>"+OATokenArr.length);
								for (String oaToken : OATokenArr) {
									
									
									String[] responseTokenArr = oaToken.split(Constants.DELIMETER_COMMA);
									for (String token : responseTokenArr) {
										
										if(token.startsWith("report")){
											
											status = token.split("=")[1];
											
										}else if(token.startsWith("tid") ){
											
											tid = token.split("=")[1];
										}
										
										if(!status.isEmpty() && !tid.isEmpty() && responseStatusCodeMap.containsKey(tid)) {
											
											responseStatusCodeMap.put(tid, status);
											 status = Constants.STRING_NILL;;
											 tid = Constants.STRING_NILL;
											
										}
										
									}//for
									
									
								}
								
								
							}//if
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							logger.error("Exception",e);
						} catch (ProtocolException e) {
							// TODO Auto-generated catch block
							logger.error("Exception",e);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							logger.error("Exception",e);
						}catch (Exception e) {
							// TODO: handle exception
							logger.error("Exception",e);
						}
						
						postData = "user="+this.getUserId()+":"+this.getAccountPwd()+"&tid="+msgIdsArr[i];
						//postData = "user=amith.lulla@optculture.com:amithl&tid="+msgIdsArr[i];
						isAddComma = true;
						
					}//else
					
				}//for
				if(!isConnected) {
					try{
						isConnected = true;
						logger.debug("before sending post data is===="+postData);
						
						URL url = new URL(targetUrl);
						
						HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
						
						urlconnection.setConnectTimeout(20000);
						urlconnection.setReadTimeout(20000);
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
						logger.debug("report response is======>"+response);
						
						if(response != null && response.startsWith("Status=0,")) {
							
							/*boolean successResponse =  response.startsWith("Status=0,");
							if(successResponse)
								*/
							String status = Constants.STRING_NILL;;
							String tid = Constants.STRING_NILL;
							
							String[] OATokenArr = response.split("oa=");
							logger.debug("token arr size======>"+OATokenArr.length);
							for (String oaToken : OATokenArr) {
								
								
								String[] responseTokenArr = oaToken.split(Constants.DELIMETER_COMMA);
								for (String token : responseTokenArr) {
									
									if(token.startsWith("report")){
										
										status = token.split("=")[1];
										
									}else if(token.startsWith("tid") ){
										
										tid = token.split("=")[1];
									}
									logger.debug("there in map======>"+(tid));
									if(!status.isEmpty() && !tid.isEmpty() && responseStatusCodeMap.containsKey(tid)) {
										logger.debug("there in map======>"+responseStatusCodeMap.containsKey(tid));
										responseStatusCodeMap.put(tid, status);
										 status = Constants.STRING_NILL;;
										 tid = Constants.STRING_NILL;
										
									}
									
								}//for
								
								
							}
							
							
						}//if
						
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						logger.error("Exception",e);
					} catch (ProtocolException e) {
						// TODO Auto-generated catch block
						logger.error("Exception",e);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error("Exception",e);
					}catch (Exception e) {
						// TODO: handle exception
						logger.error("Exception",e);
					}
					
					postData = "user="+this.getUserId()+":"+this.getAccountPwd()+"&tid=";
					//postData = "user=amith.lulla@optculture.com:amithl&tid=";
					isAddComma = false;
					
					
					
					
				}
				
		 }catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception",e);
			}
				
				
				//59071138631160420375
				//postData += "user=amith.lulla@optculture.com:amithl&tid=59093138622453710607 ";
				//postData += "user=mallika.naidu@optculture.com:mallika&tid=59071138631160420375 ";
				
				
			return responseStatusCodeMap;
		 
		 
		 
		 
		 
		 
		 
	 }
	 
	 
 public boolean getBalance(int totalCount, String targetUrl ) throws BaseServiceException {
		 
		 
		 //http://api.mvaayoo.com/mvaayooapi/APIUtil?user=uid:pw&type=0
	 		if(targetUrl == null || targetUrl.isEmpty()) return false;
	 		if(true) return true;//as mvaayoo's balance api responsetime is very long we simply are skipping this
		 try {
				/*
				 * http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052346000&dcs=0&msgtxt=This is Test message&state=4 
				 */
			 //Status=0,Credit balance is99989
				String postData = "";
				
				//postData += "user=amith.lulla@optculture.com:amithl&type=0";
				postData += "user="+this.getUserId()+":"+this.getAccountPwd()+"&type=0";
				
				URL url = new URL(targetUrl);
				
				HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
				
				urlconnection.setConnectTimeout(20000);
				urlconnection.setReadTimeout(20000);
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
				logger.debug("balence response is======>"+response);
				
				if(response == null) {
					
					//logger.error("exception while fetching balence");
					return false;
					
				}//if
				
				String creditsStr = response.replace("Status=0,Credit balance is", "");
				try {
					
					int credits = Integer.parseInt(creditsStr);
					if(credits <= totalCount) {
						
						EmailQueueDao emailQueueDao = null;
						EmailQueueDaoForDML emailQueueDaoForDML = null;
						try {
							emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
							emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDao");
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							throw new BaseServiceException("Exception while getting emailQueueDao");
						}
							
						try {
							
							String message = PropertyUtil.getPropertyValueFromDB(Constants.SMS_LOW_CREDITS_WARN_TEXT);
							String emailId = PropertyUtil.getPropertyValueFromDB("SupportEmailId");
							
						 	EmailQueue emailQueue = new EmailQueue("Ran out of SMS Credits", message, 
						 			Constants.EQ_TYPE_LOW_SMS_CREDITS, "Active", emailId, new Date());
						 	
						 	//emailQueueDao.saveOrUpdate(emailQueue);
							emailQueueDaoForDML.saveOrUpdate(emailQueue);
					 	}
						catch (Exception e) {
							// TODO: handle exception
							logger.error("Exception",e);
							return false;
						}
						
							
						
						return false;
						
					}
					
					
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					
					
					logger.error("Exception",e);
					return false;
				}
				
				
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				logger.error("Exception",e);
				return false;
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				logger.error("Exception",e);
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception",e);
				return false;
			}catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception",e);
				return false;
			}
			
		 
		 
		 return true;
		 
		 
		 
		 
	 
		 
		 
	 }
 
 public void doFinalUpdate(Map<Long, String> updateStatusMap) throws BaseServiceException{
	 logger.debug("=====in doFinalUpdate====="+isEnded()+"  "+updateStatusMap.size()+" FOR THread "+Thread.currentThread().getName());
	 if(isEnded()) {
         	
		 if(updateStatusMap.size() == 0) return;
		 
		 processUpdateStatusMap(null,updateStatusMap,false);//process left sentids
     	
         			
		 //smsCount = 0;
		 isEnded = false;
		
	 }
	 
 }
 
 
 public void unbindSession(Session mySession) {
	 
	 try {
		 logger.debug(" in UnBind "+mySession);
		 if(mySession != null) {
			UnbindResp resp = mySession.unbind();
			mySession.close();
			mySession = null;
			//logger.debug("UnBind Response..............." + resp.debugString());
		 }
		} catch (ValueNotSetException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		} catch (PDUException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		} catch (WrongSessionStateException e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception while connecting to SMSC",e);
		}
	 
 }
	
 public class mainMethodListener implements ServerPDUEventListener {
	 
	 public mainMethodListener() {}
	 private Session session ;
	 
	 public mainMethodListener(Session session) {
		 
		 this.session = session;
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
					
					DeliverSM receipt = (DeliverSM)receivedPDU;
					logger.debug("receipt "+receipt.debugString());
					logger.debug("receipt shortmessage "+receipt.getShortMessage());
					logger.debug("receipt sequencenum "+receipt.getSequenceNumber());
					
					if(session != null) {
						UnbindResp resp = session.unbind();
						logger.debug("UnBind Response..............." + resp.debugString());
					}
					
					
					
					
				}if(receivedPDU instanceof SubmitSMResp) {
					
					logger.debug("got delivery recpt from SMSC "+((SubmitSMResp)receivedPDU).getMessageId());
					SubmitSMResp submitResponse = (SubmitSMResp)receivedPDU;
					
					logger.debug("submitResponse "+submitResponse.debugString());
					/*updateStatusMap.put(new Long(submitResponse.getSequenceNumber()), submitResponse.getMessageId());
					if( (smsCount >= 100 || isEnded) && smsCount == updateStatusMap.size() ) {
		        		
						
						processUpdateStatusMap();
		        		
		        			
	        		}//if
*/					
					
					
					
				}//if
				
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
			}finally {
				if(session != null) {
					 try {
						UnbindResp resp = session.unbind();
						 logger.debug("UnBind Response..............." + resp.debugString());
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
				
			}
			
	
		
	}
	 
 }
 
 
}
