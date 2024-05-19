package org.mq.marketer.campaign.controller.service;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bulatnig.smpp.pdu.Npi;
import org.bulatnig.smpp.pdu.Ton;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.smpp.Connection;
import org.smpp.ServerPDUEvent;
import org.smpp.ServerPDUEventListener;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.AddressRange;
import org.smpp.pdu.BindReceiver;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.DeliverSMResp;
import org.smpp.pdu.EnquireLink;
import org.smpp.pdu.EnquireLinkResp;
import org.smpp.pdu.PDU;
import org.smpp.pdu.SubmitSMResp;
import org.smpp.pdu.ValueNotSetException;

public class GatewaySessionProvider {

	private static GatewaySessionProvider gatewaySessionProvider;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER); 
	
	public   Map<Long, String> updateStatusMap ;
	public synchronized Map<Long, String> getUpdateStatusMap() {
		if(updateStatusMap == null) {
			updateStatusMap = new LinkedHashMap<Long, String>();
		}
		return updateStatusMap;
	}
	private   Set<String> CMDlrReciepts ;
	public synchronized Set<String> getCMDlrReciepts() {
		if(CMDlrReciepts == null) {
			
			CMDlrReciepts = new HashSet<String>();
		}
		return CMDlrReciepts;
	}
	public void setUpdateStatusMap(Map<Long, String> updateStatusMap) {
		this.updateStatusMap = updateStatusMap;
	}

	private static Session CMComPrmotionalGatewaySession;
	public static Session getCMComPrmotionalGatewaySession() {
		return CMComPrmotionalGatewaySession;
	}
	public void setCMComPrmotionalGatewaySession(Session cMComPrmotionalGatewaySession) {
		CMComPrmotionalGatewaySession = cMComPrmotionalGatewaySession;
	}
	public static Session getCMComTransactionalGatewaySession() {
		return CMComTransactionalGatewaySession;
	}
	public void setCMComTransactionalGatewaySession(Session cMComTransactionalGatewaySession) {
		CMComTransactionalGatewaySession = cMComTransactionalGatewaySession;
	}

	private static Session CMComTransactionalGatewaySession;
	public static GatewaySessionProvider getInstance(List<OCSMSGateway> gatewayList) throws Exception{
		logger.debug("======entered getInstance()===>1");
		
		if (gatewaySessionProvider == null) {
            
			synchronized (GatewaySessionProvider.class) {
				
	            if (gatewaySessionProvider == null) {
	            	logger.debug("======entered getInstance()===>2");
            		gatewaySessionProvider = new GatewaySessionProvider();
            		logger.debug("======entered getInstance()===>3");
            		if(gatewayList != null){
            			logger.debug("======entered getInstance()===>4");
	            		for (OCSMSGateway ocSMSGateway : gatewayList) {
	            			try {
	            				logger.debug("======entered CM TR===>5"+ocSMSGateway.getGatewayName());
	            				if(ocSMSGateway.getGatewayName().equals(Constants.USER_SMSTOOL_CM)) {
	            					String accType = ocSMSGateway.getAccountType();
	            					if(accType.equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){
	            						logger.debug("======entered CM TR===>6");
	            						CMComTransactionalGatewaySession = getSessionObj(ocSMSGateway);
	            						ServerPDUEventListener sessionListener = gatewaySessionProvider.createSessionListener(CMComTransactionalGatewaySession, ocSMSGateway);

	            						gatewaySessionProvider.bind(CMComTransactionalGatewaySession, sessionListener, ocSMSGateway);
	            					}else if(accType.equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
	            						
	            						CMComPrmotionalGatewaySession = getSessionObj(ocSMSGateway);
	            						ServerPDUEventListener sessionListener = gatewaySessionProvider.createSessionListener(CMComPrmotionalGatewaySession, ocSMSGateway);

	            						gatewaySessionProvider.bind(CMComPrmotionalGatewaySession, sessionListener, ocSMSGateway);
	            					}
	            				}
	            						
	            			}catch (Exception e) {
								// TODO: handle exception
							}
	            		}
            		}
                }//if 
            }//synchronized
        	
	    }//if
		     
		return gatewaySessionProvider;
	}

	public static  Session getSessionObj(OCSMSGateway ocsmsGateway) throws Exception{
//		logger.debug("===== Trying for  getsession for====="+ocsmsGateway.getUserId());
		Session sess = null;
		TCPIPConnection connection = null;
		logger.info("ocsmsGateway.getIp()"+ocsmsGateway.getIp()+"\tocsmsGateway.getPort()"+ocsmsGateway.getPort());
	    connection = new TCPIPConnection(ocsmsGateway.getIp(), Integer.parseInt(ocsmsGateway.getPort()));
	    //connection.setReceiveTimeout(3000);//no need to set timeout cause only one connection per account will exists and that should be keep open
	    connection.open();
	    sess = new Session(connection);
//	    logger.debug("===== Got the in getsession for====="+sess.getDebug());
	    return sess;
	}
public Session getSessionBy(OCSMSGateway ocSMSGateway) {
	if(ocSMSGateway.getGatewayName().equals(Constants.USER_SMSTOOL_CM)) {

		if(ocSMSGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){
		//	logger.debug(">>>>>>> Completed GatewaySessionProvider :: getSessionBy <<<<<<< ");
			return getCMComPrmotionalGatewaySession();

		}else if(ocSMSGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
			//logger.debug(">>>>>>> Completed GatewaySessionProvider :: getSessionBy <<<<<<< ");
			return getCMComTransactionalGatewaySession();

		}
	}
	return null;
}
public synchronized void checkSessionsAlive(List<OCSMSGateway> gatewayList, GatewaySessionProvider sessionProvider) throws Exception{
	logger.debug("======entered checkSessionsAlive===>"+(gatewayList == null));
	
	if(gatewayList == null) {
		
		if(sessionProvider == null) {
			logger.error("Returning as sessionPr");
			return;
		}
		OCSMSGatewayDao ocSMSGatewayDao = null;
    	try {
    		ocSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("Exception while fetching the DAO "+OCConstants.OCSMSGATEWAY_DAO);
		}
    	
    	try {
    		/*
    		 *  here it will return all gateways whose mode is SMPP
    		 *  we need to keep sessionAliveFor them. 
    		 */
			gatewayList = ocSMSGatewayDao.find();
		} catch (Exception e) {
			throw e;
		}
    	
    	if(gatewayList == null || gatewayList.size() == 0) return ;
		
	}
	
	
	//TODO check for the sessions not null but not binded
	for (OCSMSGateway eachGateway : gatewayList) {
		try {
			/*
			 * If Session is not alive for the gateway we need to make the session alive 
			 */
			//	logger.info(eachGateway.getGatewayName()+" Checking Session is Alive or not");
			if(eachGateway.isEnableSessionAlive()){

				Session session = sessionProvider.getSessionObject(eachGateway);

				if( session == null){
					logger.debug(eachGateway.getGatewayName()+" ====TR session is not available===="+eachGateway.getUserId());
				//	logger.debug("====TR session is not available====");
					//unicelTransactionalGatewaySession = getSession(eachGateway);
					session = sessionProvider.getSession(eachGateway);
					sessionProvider.setSessionObject(session, eachGateway);
					ServerPDUEventListener sessionListener = sessionProvider.createSessionListener(session, eachGateway);

					sessionProvider.bind(session, sessionListener, eachGateway);
				//	logger.debug(eachGateway.getGatewayName()+"====TR session is not available===="+session.isBound());

				}else if(!session.isBound()){
			//		logger.debug(eachGateway.getGatewayName()+" ====TR session.isBound is not available===="+eachGateway.getUserId());
					ServerPDUEventListener sessionListener = sessionProvider.createSessionListener(session, eachGateway);

					sessionProvider.bind(session, sessionListener, eachGateway);
					//logger.debug("====TR session is not available===="+session.isBound());
				}else{
			//		logger.debug("EnquireLInk  is session bound  ===="+session.isBound());
					EnquireLinkResp resp;
					try {
						EnquireLink enquireLink = new EnquireLink();
						resp = session.enquireLink(enquireLink);
			//			logger.debug("EnquireLInk  is ===="+resp);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						if (e instanceof IOException || e instanceof SocketException){
							//IOException relate to the brokenpipe issue 
							//we need to close existing sessions and connections
							//restablish session
							Connection connection = session.getConnection();
							if (connection != null){
								connection.close();
							}

							session = sessionProvider.getSession(eachGateway);
							sessionProvider.setSessionObject(session, eachGateway);
							ServerPDUEventListener sessionListener = sessionProvider.createSessionListener(session, eachGateway);

							sessionProvider.bind(session, sessionListener, eachGateway);

						}else{

							logger.error("Exception not related to connectivity?????", e); 
						}//else
					}//Catch

				}//if session=Null


			}//sessionNotAlive
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception >>>>>>>>>>>>>>>>>>>>", e);
		}


	}//LoopTillGatewayList
	
}//CheckGateway

public Session getSessionObject(OCSMSGateway ocSMSGateway) {
	 if(ocSMSGateway.getGatewayName().equals(Constants.USER_SMSTOOL_CM)) {

		if(ocSMSGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){
		//	logger.debug(">>>>>>> Completed GatewaySessionProvider :: getSessionBy <<<<<<< ");
			return getCMComTransactionalGatewaySession();

		}else if(ocSMSGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
			//logger.debug(">>>>>>> Completed GatewaySessionProvider :: getSessionBy <<<<<<< ");
			return getCMComPrmotionalGatewaySession();

		}
	}
	 return null;
}
public ServerPDUEventListener createSessionListener(Session session, OCSMSGateway ocSMSGateway) {
	String sessionType = ocSMSGateway.getAccountType();
	if(ocSMSGateway.getGatewayName().equals(Constants.USER_SMSTOOL_CM)) {

		if(sessionType.equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){

			return new CMSMSCTransactionalPDUListener(session, ocSMSGateway);

		}else if(sessionType.equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {

			return new CMSMSCPromotionalPDUListener(session, ocSMSGateway);
		}
	}
	return null;
}
private class CMSMSCPromotionalPDUListener implements ServerPDUEventListener{
	
	private Session CMPromotionalSession;
	private OCSMSGateway ocSMSGateway;
	
	
	public CMSMSCPromotionalPDUListener() {}
	
	public CMSMSCPromotionalPDUListener(Session CMPromotionalSession, OCSMSGateway ocSMSGateway) {
		
		this.CMPromotionalSession = CMPromotionalSession;
		this.ocSMSGateway = ocSMSGateway;
		
	}
	
	@Override
	public void handleEvent(ServerPDUEvent event) {
			
		PDU receivedPDU = event.getPDU();
			
		procesReceivedPDU(receivedPDU, ocSMSGateway,CMPromotionalSession);	
	}
	}			

private class CMSMSCTransactionalPDUListener implements ServerPDUEventListener{
	
	private Session CMTransactionalSession;
	private OCSMSGateway ocSMSGateway;
	
	
	private  Map<Long, String> updateStatusMap = new LinkedHashMap<Long, String>();
	private  Set<String> dlrReciepts = new HashSet<String>();
	
	public CMSMSCTransactionalPDUListener() {}
	
	public CMSMSCTransactionalPDUListener(Session CMTransactionalSession, OCSMSGateway ocSMSGateway) {
		
		this.CMTransactionalSession = CMTransactionalSession;
		this.ocSMSGateway = ocSMSGateway;
		
	}
	
	@Override
	public void handleEvent(ServerPDUEvent event) {
		
		PDU receivedPDU = event.getPDU();
			logger.debug("from UnicelSMSCTransactionalPDUListener ---> procesReceivedPDU");
		procesReceivedPDU(receivedPDU, ocSMSGateway,CMTransactionalSession);	
		


	}
	
	
}
public void procesReceivedPDU(PDU receivedPDU, OCSMSGateway ocSMSGateway,Session session) {

	try {
		
		logger.debug("got an event"+receivedPDU+" receivedPDU ::"+receivedPDU.debugString());
		if(receivedPDU instanceof DeliverSM) {
			
			DeliverSM receipt = (DeliverSM)receivedPDU;
			
			/**
			 * Processing delivery response
			 */
			DeliverSMResp deliverSMResp = new DeliverSMResp();
			logger.info("receipt.getSequenceNumber()"+receipt.getSequenceNumber());
			deliverSMResp.setSequenceNumber(receipt.getSequenceNumber());
			
			submitDeliverSmResp(deliverSMResp,session);
			
			if(ocSMSGateway.isPullReports()) {//for MVaayoo this was needed coz SMSC used to send me the deliver_sm which is not required
				logger.warn("No need of pulling reports "+receipt.debugString());
				return;
			}
			logger.debug("Short Messages"+receipt.getShortMessage());
			/*	logger.debug(receipt.getShortMessageData()+"  "+receipt.getSequenceNumber());
		*/	//logger.debug("got elivery recpt from SMSC "+((DeliverSM)receivedPDU).getMessageState());
			//synchronized (dlrReciepts) {
			boolean isSmsGatewayMonitor = false;
			if(Constants.USER_SMSTOOL_CM.equalsIgnoreCase(ocSMSGateway.getGatewayName())){
				CMDlrReciepts.add(receipt.getShortMessage());
		//		logger.debug(Constants.USER_SMSTOOL_UNICEL+"..............."+receipt.getShortMessage());
				if(CMDlrReciepts.size() >100 ){
					isSmsGatewayMonitor = true;
				}
			}
			
			if(isSmsGatewayMonitor) {
				
			//	logger.debug("Performing Delivery reports");
				SMSGatewaySessionMonitor sessionMonitorThread = (SMSGatewaySessionMonitor)ServiceLocator.getInstance().getBeanByName(OCConstants.SMSGATEWAYSESSIONMONITOR);
				sessionMonitorThread.performDlrUpdations(GatewaySessionProvider.getInstance(null));
			}
			/*SMSGatewaySessionMonitor sessionMonitorThread = (SMSGatewaySessionMonitor)ServiceLocator.getInstance().getBeanByName(OCConstants.SMSGATEWAYSESSIONMONITOR);
			sessionMonitorThread.performDlrUpdations(GatewaySessionProvider.getInstance());
*/
		}if(receivedPDU instanceof SubmitSMResp) {
			///logger.debug("Performing SubmitSMResp reports");
			logger.debug("got delivery recpt from SMSC "+((SubmitSMResp)receivedPDU).getMessageId());
			SubmitSMResp submitResponse = (SubmitSMResp)receivedPDU;
			logger.info("submitResponse.getCommandStatus()"+submitResponse.getCommandStatus());
			
			if(submitResponse.getCommandStatus() == ((0x0000000b))){
				updateStatusMap.put(new Long(submitResponse.getSequenceNumber()), submitResponse.getMessageId());
				if(updateStatusMap.size() >= 100) {
					
					SMSGatewaySessionMonitor sessionMonitorThread = (SMSGatewaySessionMonitor)ServiceLocator.getInstance().getBeanByName(OCConstants.SMSGATEWAYSESSIONMONITOR);
					sessionMonitorThread.performDlrUpdations(GatewaySessionProvider.getInstance(null));
				}
				
			}
			else{
				logger.debug(submitResponse.getSequenceNumber());
				getUpdateStatusMap().put(new Long(submitResponse.getSequenceNumber()), submitResponse.getMessageId());
				if(updateStatusMap.size() >= 100) {

					SMSGatewaySessionMonitor sessionMonitorThread = (SMSGatewaySessionMonitor)ServiceLocator.getInstance().getBeanByName(OCConstants.SMSGATEWAYSESSIONMONITOR);
					sessionMonitorThread.performDlrUpdations(GatewaySessionProvider.getInstance(null));
				}

			}
			
			
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
public void setSessionObject(Session session, OCSMSGateway ocSMSGateway) {
	if(ocSMSGateway.getGatewayName().equals(Constants.USER_SMSTOOL_CM)) {
		String accType = ocSMSGateway.getAccountType();
		if(accType.equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){
			
			this.setCMComPrmotionalGatewaySession(session);;
			
		}else if(accType.equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
			
			this.setCMComTransactionalGatewaySession(session);
			
		}
	}//Added for InfoBip
}
public Session getSession(OCSMSGateway ocsmsGateway) throws Exception{
//	logger.debug("===== Trying for  getsession for====="+ocsmsGateway.getUserId());
	Session sess = null;
	TCPIPConnection connection = null;
	logger.info("ocsmsGateway.getIp()"+ocsmsGateway.getIp()+"\tocsmsGateway.getPort()"+ocsmsGateway.getPort());
    connection = new TCPIPConnection(ocsmsGateway.getIp(), Integer.parseInt(ocsmsGateway.getPort()));
    //connection.setReceiveTimeout(3000);//no need to set timeout cause only one connection per account will exists and that should be keep open
    connection.open();
    sess = new Session(connection);
//    logger.debug("===== Got the in getsession for====="+sess.getDebug());
    return sess;
}
/*
public static void main(String[] args) {
	GatewaySessionProvider gatewaySessionProvider = new GatewaySessionProvider();
	OCSMSGateway ocsmsGateway = new OCSMSGateway();
	ocsmsGateway.setIp("smpp1.unicel.in");
	ocsmsGateway.setPort("51612");
	try {
		System.out.println(gatewaySessionProvider.getSession(ocsmsGateway));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}*/
public void bind(Session mySession, ServerPDUEventListener mylistener, OCSMSGateway ocSMSGateway) throws Exception {
	try{
		//logger.debug("=====in bind for====="+ocSMSGateway.getUserId());
		AddressRange addRange = new AddressRange();
		addRange.setNpi((byte)Npi.UNKNOWN);
		addRange.setTon((byte)Ton.ALPHANUMERIC);
		final BindRequest request = new BindReceiver();
	    request.setSystemId(ocSMSGateway.getSystemId());
	    request.setPassword(ocSMSGateway.getSystemPwd());
	    if(ocSMSGateway.getSystemType() != null)request.setSystemType(ocSMSGateway.getSystemType());
	    request.setAddressRange(addRange);
	    request.setInterfaceVersion((byte) 0x34);
	  //  logger.debug("Send bind request...");
	    final BindResponse response = mySession.bind(request, mylistener);
	  //  logger.debug(" bind response..."+response.debugString()+ " bind successful ?? "+mySession.isBound());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		if (e instanceof IOException || e instanceof SocketException){
            //IOException relate to the brokenpipe issue 
            //we need to close existing sessions and connections
            //restablish session
			Connection connection = mySession.getConnection();
            if (connection != null){
            	connection.close();
            }
            mySession = this.getSession(ocSMSGateway);
            this.setSessionObject(mySession, ocSMSGateway);
			ServerPDUEventListener sessionListener = this.createSessionListener(mySession, ocSMSGateway);
			this.bind(mySession, sessionListener, ocSMSGateway);
        }else{
        	logger.error("Exception not related to connectivity: ", e);
        	throw new Exception();
        }
	}
}
public void submitDeliverSmResp(DeliverSMResp deliverSMResp, Session session){
	logger.debug(">>>>>>> Started GatewaySessionProvider :: submitDeliverSmResp <<<<<<< ");
	try {
		session.respond(deliverSMResp);
	} catch (ValueNotSetException e) {
		logger.error("Exception while process deliver response",e);
	} catch (WrongSessionStateException e) {
		logger.error("Exception while process deliver response",e);
	} catch (IOException e) {
		logger.error("Exception while process deliver response",e);
	} catch (Exception e) {
		logger.error("Exception while process deliver response",e);
	}
	logger.debug(">>>>>>> Completed GatewaySessionProvider :: submitDeliverSmResp <<<<<<< ");
}

}
