package org.mq.captiway.scheduler.services;



import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.MVaayooHTTPSample;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.optculture.exception.BaseServiceException;
import org.smpp.Connection;
import org.smpp.ServerPDUEvent;
import org.smpp.ServerPDUEventListener;
import org.smpp.Session;
import org.smpp.TimeoutException;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.PDU;
import org.smpp.pdu.PDUException;
import org.smpp.pdu.SubmitSMResp;
import org.smpp.pdu.UnbindResp;
import org.smpp.pdu.ValueNotSetException;
import org.springframework.context.ApplicationContext;

public class MVaayooApi {

	
	
	public static Map<String, String> MVaayooStatusCodes ;
	static{
		
		if(MVaayooStatusCodes == null) MVaayooStatusCodes =  new HashMap<String, String>();
		MVaayooStatusCodes.put("D", SMSStatusCodes.CLICKATELL_STATUS_RECEIVED);
		MVaayooStatusCodes.put("E", SMSStatusCodes.CLICKATELL_STATUS_MESSAGE_EXPIRED);
		MVaayooStatusCodes.put("C", SMSStatusCodes.MVAYOO_STATUS_CANCELLED);
		MVaayooStatusCodes.put("S", SMSStatusCodes.MVAYOO_STATUS_PENDING);
		MVaayooStatusCodes.put("I", SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
		MVaayooStatusCodes.put("N", Constants.SMS_SUPP_TYPE_DND);
		MVaayooStatusCodes.put("X", SMSStatusCodes.CLICKATELL_STATUS_INVALID_NUMBER);
		MVaayooStatusCodes.put("U", SMSStatusCodes.CLICKATELL_STATUS_DELIVERY_ERROR);
		MVaayooStatusCodes.put("DU", SMSStatusCodes.MVAYOO_STATUS_DUPLICATE);
		MVaayooStatusCodes.put("F", SMSStatusCodes.CLICKATELL_STATUS_DELIVERY_ERROR);
		
		
		/*StatusMpppingsMap.put( SMSStatusCodes.CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT, "Delivered");
		StatusMpppingsMap.put(SMSStatusCodes.CLICKATELL_STATUS_MESSAGE_EXPIRED, "Expired");
		StatusMpppingsMap.put("C", "Cancelled");
		StatusMpppingsMap.put("S", "Pending");
		StatusMpppingsMap.put("N", "Cancelled");
		StatusMpppingsMap.put("X", "Cancelled");
		StatusMpppingsMap.put("U", "Failed");
		*/
		 /**
		  * D: Delivered
			E: Expired (Out of coverage area, Switched off, Insufficient Memory, Tower station Failure, No routing info available from HLR)
			C: Cancelled (Absent Subscriber, Memory Capacity Exceeded, Mobile Equipment Error, Network Error, Barring, NDNC Failed)
			S: Pending at operator end (out of coverage area, inbox is full, handset is switched off,..)
			N: Cancelled due to DND
			X: Cancelled due to DND at operator end.
			U: Failed due to Number Portability issue.
		  */
		
		/**
		 * D: Delivered

			S: Pending at operator’s end
			
			E: Expired
			
			I: Invalid
			
			N: DND
			
			X: DND at operator’s end/invalid 10 digit number
			
			U: Undelivered
			
			DU: Duplicate
		 */
		
	}
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 
	//private ApplicationContext applicationContext;
	private OCSMSGateway ocsmsGateway;
	SMSCConnector SMSCConnectorObj;
	private Session MvaayooSession;
	private int smsCount;
private static Map<Long, String> updateStatusMap ;
	
	public static synchronized Map<Long, String> getUpdateStatusMap() {
		if(updateStatusMap == null) {
			updateStatusMap = new LinkedHashMap<Long, String>();
		}
		return updateStatusMap;
	}

	private  final String checkBalUrl =  "http://api.mvaayoo.com/mvaayooapi/APIUtil";
	//private final String reportUrl = "http://api.mvaayoo.com/apidlvr/APIDlvReport";
	
	public MVaayooApi(OCSMSGateway ocsmsGateway) {
		
		//this.applicationContext = applicationContext;
		this.ocsmsGateway = ocsmsGateway;
		this.smsCount = 0;
		/*smsStructureList = new ArrayList<String>();
		smsCount = 0;
		sentIdsSet = new LinkedHashSet<String>();*/
	}//MVaayooApi
	
	
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
	
	public void sendSMSOverSMPP(String content, String mobilenumber, Long sentId, String senderId) {
		
		if(SMSCConnectorObj == null ) {
			SMSCConnectorObj = new SMSCConnector(ocsmsGateway, false);
			logger.info("SMSCConnectorObj returning null:::::::::::::::::sendTransactionalSMSOverSMPP:::::::::::::::::::::::::;");
		}
		
		if(MvaayooSession == null || !MvaayooSession.isBound() || !MvaayooSession.isOpened()) {
			
			try {
				MvaayooSession = SMSCConnectorObj.getSession();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error while getting session with unicel", e);
			}
		}
		
		if(MvaayooSession != null && !MvaayooSession.isBound() ) {
			MVaayooSMSCPDUListener listener = null;
			try {
				listener = new MVaayooSMSCPDUListener(MvaayooSession);
				SMSCConnectorObj.bind(MvaayooSession, new MVaayooSMSCPDUListener(MvaayooSession));
			} catch (Exception e) {
				try {
					// TODO Auto-generated catch block
					if (e instanceof IOException || e instanceof SocketException){
					    //IOException relate to the brokenpipe issue 
					    //we need to close existing sessions and connections
					    //restablish session
						Connection connection = MvaayooSession.getConnection();
					    if (connection != null){
					    	connection.close();
					    }
					    MvaayooSession = SMSCConnectorObj.getSession();
					    SMSCConnectorObj.bind(MvaayooSession, listener);
					}else{
						logger.error("Exception not related to connectivity: ", e);
						throw new Exception();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					logger.error("Error related to connectivity", e);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					logger.error("Exception not related to connectivity: ", e);
				}
			}
			
		}
		
		
		if(MvaayooSession != null && MvaayooSession.isBound()) {
			
			try {
				smsCount ++;
				SMSCConnectorObj.submitSm(MvaayooSession, content, mobilenumber, sentId, senderId);
			} catch (Exception e) {
				try {
					// TODO Auto-generated catch block
					if (e instanceof IOException || e instanceof SocketException){
					    //IOException relate to the brokenpipe issue 
					    //we need to close existing sessions and connections
					    //restablish session
						Connection connection = MvaayooSession.getConnection();
					    if (connection != null){
					    	connection.close();
					    }
					    MvaayooSession = SMSCConnectorObj.getSession();
					    SMSCConnectorObj.bind(MvaayooSession, new MVaayooSMSCPDUListener(MvaayooSession));
					    SMSCConnectorObj.submitSm(MvaayooSession, content, mobilenumber, sentId, senderId);
					}else{
						logger.error("Exception not related to connectivity: ", e);
						throw new Exception();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					logger.error("Error related to connectivity", e);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					logger.error("Exception not related to connectivity: ", e);
				}
			}
			
		}
		
		/*logger.debug("=====started sending====");
		SMSCConnectorObj.submitSMS(content, mobilenumber, sentId, senderId);
		logger.debug("=====ended here====");*/
	}
	
	
	public void sendTransactionalSMSOverSMPP(String content, String mobilenumber, Long sentId, String senderId) {
				
		if(SMSCConnectorObj == null ) {
			SMSCConnectorObj = new SMSCConnector(ocsmsGateway, false);
			logger.debug("SMSCConnectorObj returning null:::::::::::::::::sendTransactionalSMSOverSMPP:::::::::::::::::::::::::;");
		}
		
		if(MvaayooSession == null || !MvaayooSession.isBound() || !MvaayooSession.isOpened()) {
			
			try {
				MvaayooSession = SMSCConnectorObj.getSession();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error while getting session with unicel", e);
			}
		}
		
		if(MvaayooSession != null && !MvaayooSession.isBound() ) {
			
			try {
				logger.debug("============senidng bind request=====");
				SMSCConnectorObj.bind(MvaayooSession, new MVaayooSMSCPDUListener(MvaayooSession));
				logger.debug("============ bind response====="+MvaayooSession.isBound());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error while binding with Mvaayoo", e);
			}
			
		}
		
		
		if(MvaayooSession != null && MvaayooSession.isBound()) {
			
			try {
				smsCount ++;
				logger.debug("============smsCount====="+smsCount);
				logger.debug("============senidng submitSm request=====");
				SMSCConnectorObj.submitSm(MvaayooSession, content, mobilenumber, sentId, senderId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error while sending subit_sm PDU to unicel", e);
			}
			
		}
		
		/*logger.debug("=====started sending====");
		SMSCConnectorObj.submitSMS(content, mobilenumber, sentId, senderId);
		logger.debug("=====ended here====");*/
	}
	
	public String sendSMSOverHTTP(String content, String mobilenumber, String senderID) {
		
		MVaayooHTTPSample httpSampleApp = new MVaayooHTTPSample(ocsmsGateway.getUserId(), ocsmsGateway.getPwd());
		logger.debug("=====started sending====");
		
		String response = httpSampleApp.test(content, mobilenumber, senderID);
		logger.debug("=====ended here====");
		return response;
	}
	
	public void  pingMVaayooToSendRestOfSMS() throws BaseServiceException{
		
		logger.debug("=====started sending====");
		if(SMSCConnectorObj == null ) SMSCConnectorObj = new SMSCConnector(ocsmsGateway, true);
		
		SMSCConnectorObj.setEnded(true);
		SMSCConnectorObj.doFinalUpdate(updateStatusMap);
		
		//SMPPLogicaSampleAPP.getReports();
		logger.debug("=====ended here====");
		 
		//return retList;
	}
	
	public void unbindSession() {
				
		if(SMSCConnectorObj == null ){
			SMSCConnectorObj = new SMSCConnector( ocsmsGateway);
			logger.info("SMSCConnectorObj Object returned Null>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
		SMSCConnectorObj.setEnded(true);
		SMSCConnectorObj.unbindSession(MvaayooSession);
	}
	
	
	public Map<String, String> fetchReports(String msgIdStr) {
		
		if(SMSCConnectorObj == null ) SMSCConnectorObj = new SMSCConnector(ocsmsGateway);
		//SMSCConnectorObj.setTransactional(isTransactional);
		return SMSCConnectorObj.getReports(msgIdStr, ocsmsGateway.getPullReportsURL());
	}
	private class MVaayooSMSCPDUListener implements ServerPDUEventListener{
		private Session mvaayooSession;
		
		public MVaayooSMSCPDUListener() {}
		
		public MVaayooSMSCPDUListener(Session mvaayooSession) {
			
			this.mvaayooSession = mvaayooSession;
			
		}
		
		@Override
		public void handleEvent(ServerPDUEvent event) {
			// TODO Auto-generated method stub

			// TODO Auto-generated method stub
			boolean gotexception = false;
			try {
				// TODO Auto-generated method stub
				//super.handleEvent(event);
				
				PDU receivedPDU = event.getPDU();
				logger.debug("got an event"+receivedPDU+" receivedPDU ::"+receivedPDU.debugString()
						);
				if(receivedPDU instanceof SubmitSMResp) {
					logger.debug("got elivery recpt from SMSC "+((SubmitSMResp)receivedPDU).getMessageId());
					SubmitSMResp submitResponse = (SubmitSMResp)receivedPDU;
					SMSCConnectorObj.processUpdateStatusMap(submitResponse, getUpdateStatusMap(), false);
					
					/*
					
					logger.debug("got elivery recpt from SMSC "+((SubmitSMResp)receivedPDU).getMessageId());
					SubmitSMResp submitResponse = (SubmitSMResp)receivedPDU;
					synchronized (updateStatusMap) {
						
						updateStatusMap.put(new Long(submitResponse.getSequenceNumber()), submitResponse.getMessageId());
					}
					if( (smsCount >= 100 || SMSCConnectorObj.isEnded()) && smsCount == updateStatusMap.size() ) {
		        		
						
						int updatedCount = SMSCConnectorObj.processUpdateStatusMap(updateStatusMap);
						if(updatedCount == updateStatusMap.size()) {
							updateStatusMap.clear();
							smsCount = 0;
						}
		        			
	        		}//if
					
				*/}//if
				
			} 
			catch(Exception e){
				logger.error(e);
				gotexception = true;
			}
			/*catch (ValueNotSetException e) {
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
			}*/finally {
				if(gotexception && mvaayooSession != null && mvaayooSession.isBound()) {
					 try {
						UnbindResp resp = mvaayooSession.unbind();
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
}//
