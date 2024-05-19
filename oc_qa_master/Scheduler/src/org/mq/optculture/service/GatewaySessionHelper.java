package org.mq.optculture.service;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bulatnig.smpp.pdu.Npi;
import org.bulatnig.smpp.pdu.Ton;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.dao.OCSMSGatewayDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.smpp.Connection;
import org.smpp.ServerPDUEventListener;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.AddressRange;
import org.smpp.pdu.BindReceiver;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.EnquireLink;
import org.smpp.pdu.EnquireLinkResp;

public class GatewaySessionHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 
	
	public synchronized void checkSessionsAlive(List<OCSMSGateway> gatewayList, GatewaySessionProvider sessionProvider) throws Exception{
		logger.debug("======entered checkSessionsAlive===>"+(gatewayList == null));
		
		if(gatewayList == null) {
			
			if(sessionProvider == null) return;
			OCSMSGatewayDao ocSMSGatewayDao = null;
        	try {
        		ocSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new Exception("Exception while fetching the DAO "+OCConstants.OCSMSGATEWAY_DAO);
			}
        	
        	try {
				gatewayList = ocSMSGatewayDao.findBy(Constants.SMS_SENDING_MODE_SMPP);
			} catch (Exception e) {
				throw e;
			}
        	
        	if(gatewayList == null || gatewayList.size() == 0) return ;
			
		}
		
		//TODO check for the sessions not null but not binded
		for (OCSMSGateway eachGateway : gatewayList) {
			
			
			
    		Session session = sessionProvider.getSessionObject(eachGateway);
    		if( session == null){
				
				logger.debug("====TR session is not available====");
				//unicelTransactionalGatewaySession = getSession(eachGateway);
				session = sessionProvider.getSession(eachGateway);
				sessionProvider.setSessionObject(session, eachGateway);
				ServerPDUEventListener sessionListener = sessionProvider.createSessionListener(session, eachGateway);
				
				sessionProvider.bind(session, sessionListener, eachGateway);
				logger.debug("====TR session is not available===="+session.isBound());
				
			}else if(!session.isBound()){
				
				ServerPDUEventListener sessionListener = sessionProvider.createSessionListener(session, eachGateway);
				
				sessionProvider.bind(session, sessionListener, eachGateway);
				logger.debug("====TR session is not available===="+session.isBound());
			}else{
				logger.debug("EnquireLInk  is session bound  ===="+session.isBound());
				EnquireLinkResp resp;
				try {
					EnquireLink enquireLink = new EnquireLink();
					resp = session.enquireLink(enquireLink);
					logger.debug("EnquireLInk  is ===="+resp);
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
	    			
		            }
				}
				
				
			}
		
    		
    		
    	/*	if(eachGateway.getAccountType().equals(Constants.SMS_TYPE_TRANSACTIONAL)) {
    			
    			if(eachGateway.getGatewayName().equals(OCConstants.SMS_GATEWAY_UNICEL)) {
    				session = sessionProvider.getUnicelTransactionalGatewaySession();
	    			if( session == null){
	    				
	    				logger.debug("====TR session is not available====");
	    				//unicelTransactionalGatewaySession = getSession(eachGateway);
	    				session = getSession(eachGateway);
	    				sessionProvider.setUnicelTransactionalGatewaySession(session);//create the session
	    				ServerPDUEventListener trSessionListener = sessionProvider.createSessionListener(session, eachGateway, Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL);
	    				
	    				bind(session, trSessionListener, eachGateway);
	    				logger.debug("====TR session is not available===="+session.isBound());
	    				
	    			}else if(!session.isBound()){
	    				
	    				ServerPDUEventListener trSessionListener = sessionProvider.createSessionListener(session, eachGateway, Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL);
	    				
	    				bind(session, trSessionListener, eachGateway);
	    				logger.debug("====TR session is not available===="+session.isBound());
	    			}else{
	    				logger.debug("EnquireLInk  is session bound  ===="+session.isBound());
	    				EnquireLinkResp resp;
						try {
							EnquireLink enquireLink = new EnquireLink();
							resp = session.enquireLink(enquireLink);
							logger.debug("EnquireLInk  is ===="+resp);
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
				                
				                session = getSession(eachGateway);
				                sessionProvider.setUnicelTransactionalGatewaySession(session);//create the session
			    				ServerPDUEventListener trSessionListener = sessionProvider.createSessionListener(session, eachGateway, Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL);
			    				
			    				bind(session, trSessionListener, eachGateway);
			    			
				            }
						}
	    				
	    				
	    			}
    			
    			}
    			
    		}else if(eachGateway.getAccountType().equals(Constants.SMS_TYPE_PROMOTIONAL)) {
    			
    			if(eachGateway.getGatewayName().equals(OCConstants.SMS_GATEWAY_UNICEL)){
    				session = sessionProvider.getUnicelPrmotionalGatewaySession();
    				
    				if( session == null){
	    				logger.debug("====PR session is not available====");
	    				
	    				session = getSession(eachGateway);
	    				sessionProvider.setUnicelPrmotionalGatewaySession(session);
	    				ServerPDUEventListener promotionalListener = 
    							sessionProvider.createSessionListener(session, eachGateway, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
    					
    					bind(session, promotionalListener, eachGateway);
    					
	    				
    				}else if(!session.isBound()) {
    					
    					ServerPDUEventListener promotionalListener = 
    							sessionProvider.createSessionListener(session, eachGateway, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
    					
    					bind(session, promotionalListener, eachGateway);
    					
    				}else {

	    				logger.debug("EnquireLInk  is session bound  ===="+session.isBound());
	    				EnquireLinkResp resp;
						try {
							EnquireLink enquireLink = new EnquireLink();
							resp = session.enquireLink(enquireLink);
							logger.debug("EnquireLInk  is ===="+resp);
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
				                
				                session = getSession(eachGateway);
			    				sessionProvider.setUnicelPrmotionalGatewaySession(session);
			    				ServerPDUEventListener promotionalListener = 
		    							sessionProvider.createSessionListener(session, eachGateway, Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL);
		    					
		    					bind(session, promotionalListener, eachGateway);
		    					
			    			
				            }
						}
    					
    				}
				}
    			
    		}else if(eachGateway.getAccountType().equals(Constants.SMS_SENDING_TYPE_OPTIN)) {
    			
    			if(eachGateway.getGatewayName().equals(OCConstants.SMS_GATEWAY_UNICEL) ) {
    				
    				if(eachGateway.getUserId().equals(OCConstants.SMS_GATEWAY_UNICEL_OPTIN_USER_TRESMODE)){
    				
    					session = sessionProvider.getUnicelTresModeOptinGatewaySession();
    					if(session == null) {
    						logger.debug("====op-tresmode session is not available====");
    						session = getSession(eachGateway);
    						sessionProvider.setUnicelTresModeOptinGatewaySession(session);
    						ServerPDUEventListener tresmodeOptinSessionListener = sessionProvider.createSessionListener(session,
    								eachGateway, Constants.SMS_ACCOUNT_TYPE_OPTIN);
    						
    						bind(session, tresmodeOptinSessionListener, eachGateway);
    						
    					}else if(!session.isBound()) {
    						
    						ServerPDUEventListener tresmodeOptinSessionListener = sessionProvider.createSessionListener(session,
    								eachGateway, Constants.SMS_ACCOUNT_TYPE_OPTIN);
    						
    						bind(session, tresmodeOptinSessionListener, eachGateway);
    						
    					}else{
    	    				logger.debug("EnquireLInk  is session bound  ===="+session.isBound());
    	    				EnquireLinkResp resp;
    						try {
    							EnquireLink enquireLink = new EnquireLink();
    							resp = session.enquireLink(enquireLink);
    							logger.debug("EnquireLInk  is ===="+resp);
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
    				                
    				                session = getSession(eachGateway);
    				                sessionProvider.setUnicelTresModeOptinGatewaySession(session);
    	    						ServerPDUEventListener tresmodeOptinSessionListener = sessionProvider.createSessionListener(session,
    	    								eachGateway, Constants.SMS_ACCOUNT_TYPE_OPTIN);
    	    						
    	    						bind(session, tresmodeOptinSessionListener, eachGateway);
    	    						
    			    			
    				            }
    						}
        					
        				
    						
    						
    					}
    					
    				}else if(eachGateway.getUserId().equals(OCConstants.SMS_GATEWAY_UNICEL_OPTIN_USER_MAGOPTIN)){ 
    					
    					session = sessionProvider.getUnicelOptinGatewaySession();
    					if( session == null) {
    				
    						logger.debug("====op-magoptin session is not available====");
    						session = getSession(eachGateway);
    						sessionProvider.setUnicelOptinGatewaySession(session);
    						ServerPDUEventListener optinSessionListener =
    								sessionProvider.createSessionListener(session, eachGateway, Constants.SMS_ACCOUNT_TYPE_OPTIN);
    						
    						bind(session, optinSessionListener, eachGateway);
    						
    					
    					}else if(!session.isBound()) {
    						
    						ServerPDUEventListener optinSessionListener =
    								sessionProvider.createSessionListener(session, eachGateway, Constants.SMS_ACCOUNT_TYPE_OPTIN);
    						
    						bind(session, optinSessionListener, eachGateway);
    						
    						
    					}else{
    						

    	    				logger.debug("EnquireLInk  is session bound  ===="+session.isBound());
    	    				EnquireLinkResp resp;
    						try {
    							EnquireLink enquireLink = new EnquireLink();
    							resp = session.enquireLink(enquireLink);
    							logger.debug("EnquireLInk  is ===="+resp);
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
    				                
    				                session = getSession(eachGateway);
    				                sessionProvider.setUnicelOptinGatewaySession(session);
    	    						ServerPDUEventListener optinSessionListener =
    	    								sessionProvider.createSessionListener(session, eachGateway, Constants.SMS_ACCOUNT_TYPE_OPTIN);
    	    						
    	    						bind(session, optinSessionListener, eachGateway);
    	    						
    	    						
    			    			
    				            }
    						}
    						
    					}
    				}
    			}	
    			
    		}//else if
*/    		
		}//for
		
	}
	
	/*public Session getSession(OCSMSGateway ocsmsGateway) throws Exception{
		logger.debug("=====in getsession for====="+ocsmsGateway.getUserId());
		Session sess = null;
		TCPIPConnection connection = null;
	    connection = new TCPIPConnection(ocsmsGateway.getIp(), Integer.parseInt(ocsmsGateway.getPort()));
	    //connection.setReceiveTimeout(3000);//no need to set timeout cause only one connection per account will exists and that should be keep open
	    connection.open();
	    sess = new Session(connection);
	    logger.debug("=====in getsession for====="+sess.getDebug());
	    return sess;
	}
	
	public void bind(Session mySession, ServerPDUEventListener mylistener, OCSMSGateway ocSMSGateway) throws Exception {
		logger.debug("=====in bind for====="+ocSMSGateway.getUserId());
		AddressRange addRange = new AddressRange();
		addRange.setNpi((byte)Npi.UNKNOWN);
		addRange.setTon((byte)Ton.ALPHANUMERIC);
		final BindRequest request = new BindReceiver();
	    request.setSystemId(ocSMSGateway.getSystemId());
	    request.setPassword(ocSMSGateway.getSystemPwd());
	    if(ocSMSGateway.getSystemType() != null)request.setSystemType(ocSMSGateway.getSystemType());
	    request.setAddressRange(addRange);
	    request.setInterfaceVersion((byte) 0x34);
	    logger.debug("Send bind request...");
	    final BindResponse response = mySession.bind(request, mylistener);
	    logger.debug(" bind response..."+response.debugString()+ " bind successful ?? "+mySession.isBound());
		
	}*/
	
	
}
