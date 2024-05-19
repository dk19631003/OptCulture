package org.mq.marketer.campaign.controller.service;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.SMSBouncesDao;
import org.mq.marketer.campaign.dao.SMSBouncesDaoForDML;
import org.mq.marketer.campaign.dao.SMSCampaignReportDao;
import org.mq.marketer.campaign.dao.SMSCampaignReportDaoForDML;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.ServiceLocator;
import org.smpp.Connection;
import org.smpp.ServerPDUEvent;
import org.smpp.ServerPDUEventListener;
import org.smpp.Session;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.PDU;
import org.smpp.pdu.SubmitSMResp;
	
public class CMComSMSGateway {


public CMComSMSGateway() {}
private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
private OCSMSGateway ocsmsGateway;
private SMSCampaignSentDao smsCampaignSentDao;
private SMSCampaignSentDaoForDML smsCampaignSentDaoForDML ;
private SMSBouncesDao smsBouncesDao;
private SMSBouncesDaoForDML smsBouncesDaoForDML;
private SMSCampaignReportDao smsCampaignReportDao;
private SMSCampaignReportDaoForDML smsCampaignReportDaoForDML;
private SMSSuppressedContactsDao smsSuppressedContactsDao;
private ContactsDao contactsDao;
private ContactsDaoForDML contactsDaoForDML;
private SMSCConnector SMSCConnectorObj;
private GatewaySessionProvider sessionProvider;
private Session cmcomSession;
public static Map<String,String> CMComDlrMap;
public static Set<String> deliveredStatusSet ;
private int smsCount=0;
static {
	

	
	CMComDlrMap = new HashMap<String, String>();
	
	CMComDlrMap.put("UNKNOWN",SMSStatusCodes.SMPP_DLR_STATUS_UNKNOWN ); 
	CMComDlrMap.put("REJECTD", "Rejected");
	CMComDlrMap.put("DELIVRD", SMSStatusCodes.SMPP_DLR_STATUS_DELIVERED);
	CMComDlrMap.put("ACCEPTD", "Accepted");
	CMComDlrMap.put("UNDELIV",SMSStatusCodes.SMPP_DLR_STATUS_FAILED );
	CMComDlrMap.put("EXPIRED", "Expired" );
	CMComDlrMap.put("DELETED", "Deleted" );
	
	
	
	deliveredStatusSet = new HashSet<String>();
	deliveredStatusSet.add(SMSStatusCodes.SMPP_DLR_STATUS_DELIVERED);
	deliveredStatusSet.add("Accepted");
	
	
	

}
	public CMComSMSGateway(OCSMSGateway ocsmsGateway) {
		
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
	private class CMComSMSCPDUListener implements ServerPDUEventListener{
		private Session unicelSession;
		private OCSMSGateway ocSMSGateway;
		
		public CMComSMSCPDUListener() {}
		
		public CMComSMSCPDUListener(Session unicelSession, OCSMSGateway ocsmsGateway) {
			
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
public void sendSMSOverSMPP(String content, String mobilenumber, Long sentId, String senderId) throws BaseServiceException{
		logger.debug("===sending over CM=="+ocsmsGateway.getSystemId()+" "+senderId);
		if(SMSCConnectorObj == null ) SMSCConnectorObj = new SMSCConnector(ocsmsGateway);
		try {
			if(sessionProvider == null) sessionProvider = GatewaySessionProvider.getInstance(null);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("some error occured can not continue ", e1);
			throw new BaseServiceException("Exception, try after sometime");
		}
		if(cmcomSession == null || !cmcomSession.isBound() || !cmcomSession.isOpened()) {
			
			try {
				cmcomSession = SMSCConnectorObj.getSession();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error while getting session with unicel", e);
			}
		}
		
		if(cmcomSession != null && !cmcomSession.isBound() ) {
			
			CMComSMSCPDUListener listener = null;//to avoid growing objcts in heap
			try {
				listener = new CMComSMSCPDUListener(cmcomSession, ocsmsGateway);
				SMSCConnectorObj.bindReq(cmcomSession, listener);
			} catch (Exception e) {
				try {
					// TODO Auto-generated catch block
					if (e instanceof IOException || e instanceof SocketException){
					    //IOException relate to the brokenpipe issue 
					    //we need to close existing sessions and connections
					    //restablish session
						Connection connection = cmcomSession.getConnection();
					    if (connection != null){
					    	connection.close();
					    }
					    cmcomSession = SMSCConnectorObj.getSession();
					    SMSCConnectorObj.bindReq(cmcomSession, listener);
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
		
		
		if(cmcomSession != null && cmcomSession.isBound()) {
			
			try {
				smsCount ++;
				SMSCConnectorObj.submitSm(cmcomSession, content, mobilenumber, sentId, senderId);
			} catch (Exception e) {
				try {
					if (e instanceof IOException || e instanceof SocketException){
					    //IOException relate to the brokenpipe issue 
					    //we need to close existing sessions and connections
					    //restablish session
						Connection connection = cmcomSession.getConnection();
					    if (connection != null){
					    	connection.close();
					    }
					    cmcomSession = SMSCConnectorObj.getSession();
					    SMSCConnectorObj.bindReq(cmcomSession, new CMComSMSCPDUListener(cmcomSession, ocsmsGateway));
					    SMSCConnectorObj.submitSm(cmcomSession, content, mobilenumber, sentId, senderId);
					}
				} catch (IOException ioe1) {
					logger.debug("Exception", ioe1);
				} catch (Exception e1) {
					logger.debug("Exception", e1);
				}
			}
			
		}
	}

}
