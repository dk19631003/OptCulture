package org.mq.optculture.service;

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
import org.mq.captiway.scheduler.SMSGatewaySessionMonitor;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.dao.OCSMSGatewayDao;
import org.mq.captiway.scheduler.services.SMSCConnector;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.CouponProvider;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.smpp.Connection;
import org.smpp.Data;
import org.smpp.ServerPDUEvent;
import org.smpp.ServerPDUEventListener;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.Address;
import org.smpp.pdu.AddressRange;
import org.smpp.pdu.BindReceiver;
import org.smpp.pdu.BindRequest;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransciever;
import org.smpp.pdu.DeliverSM;
import org.smpp.pdu.DeliverSMResp;
import org.smpp.pdu.EnquireLink;
import org.smpp.pdu.EnquireLinkResp;
import org.smpp.pdu.PDU;
import org.smpp.pdu.SubmitSM;
import org.smpp.pdu.SubmitSMResp;
import org.smpp.pdu.ValueNotSetException;
import org.smpp.pdu.WrongLengthOfStringException;

public class GatewaySessionProvider {

	private static GatewaySessionProvider gatewaySessionProvider;
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 
	
	
	public   Map<Long, String> updateStatusMap ;
	public synchronized Map<Long, String> getUpdateStatusMap() {
		if(updateStatusMap == null) {
			updateStatusMap = new LinkedHashMap<Long, String>();
		}
		return updateStatusMap;
	}
	public void setUpdateStatusMap(Map<Long, String> updateStatusMap) {
		this.updateStatusMap = updateStatusMap;
	}

	private   Set<String> infoBipDlrReciepts ;
	private   Set<String> CMDlrReciepts ;
	public synchronized Set<String> getCMDlrReciepts() {
		if(CMDlrReciepts == null) {
			
			CMDlrReciepts = new HashSet<String>();
		}
		return CMDlrReciepts;
	}
	private   Set<String> unicelDlrReciepts ;
	public synchronized Set<String> getUnicelDlrReciepts() {
		if(unicelDlrReciepts == null) {
			
			unicelDlrReciepts = new HashSet<String>();
		}
		return unicelDlrReciepts;
	}

	public void setUnicelDlrReciepts(Set<String> unicelDlrReciepts) {
		this.unicelDlrReciepts = unicelDlrReciepts;
	}
	
	public synchronized Set<String> getInfoBipDlrReciepts() {
		if(infoBipDlrReciepts == null) {
			
			infoBipDlrReciepts = new HashSet<String>();
		}
		return infoBipDlrReciepts;
	}

	public void setInfoBipDlrReciepts(Set<String> infoBipDlrReciepts) {
		this.infoBipDlrReciepts = infoBipDlrReciepts;
	}
	
	private Session unicelOptinGatewaySession;
	private Session unicelTresModeOptinGatewaySession;
	public Session getUnicelTresModeOptinGatewaySession() {
		return unicelTresModeOptinGatewaySession;
	}


	public void setUnicelTresModeOptinGatewaySession(
			Session unicelTresModeOptinGatewaySession) {
		this.unicelTresModeOptinGatewaySession = unicelTresModeOptinGatewaySession;
		
		
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
	
	private Session unicelPrmotionalGatewaySession;
	private Session unicelTransactionalGatewaySession;
	
	public Session getUnicelOptinGatewaySession() {
		
		
		return unicelOptinGatewaySession;
	}


	public void setUnicelOptinGatewaySession(Session unicelOptinGatewaySession) {
		this.unicelOptinGatewaySession = unicelOptinGatewaySession;
	}


	public Session getUnicelPrmotionalGatewaySession() {
		return unicelPrmotionalGatewaySession;
	}


	public void setUnicelPrmotionalGatewaySession(
			Session unicelPrmotionalGatewaySession) {
		this.unicelPrmotionalGatewaySession = unicelPrmotionalGatewaySession;
	}


	public Session getUnicelTransactionalGatewaySession() {
		
		return unicelTransactionalGatewaySession;
	}


	public void setUnicelTransactionalGatewaySession(
			Session unicelTransactionalGatewaySession) {
		this.unicelTransactionalGatewaySession = unicelTransactionalGatewaySession;
		
		
	}

	
	private Session mVaayooGatewaySession;


	public Session getmVaayooGatewaySession() {
		return mVaayooGatewaySession;
	}


	public void setmVaayooGatewaySession(Session mVaayooGatewaySession) {
		this.mVaayooGatewaySession = mVaayooGatewaySession;
	}


	//Added for InfoBip
	private Session infobipPrmotionalGatewaySession;
	//Added for infobip pool
	private Session infobipPrmotionalGatewaySession_Zaks;
	private Session infobipPrmotionalGatewaySession_HOF;
	private Session infobipPrmotionalGatewaySession_User1;
	private Session infobipPrmotionalGatewaySession_User2;
	private Session infobipPrmotionalGatewaySession_User3;
	private Session infobipPrmotionalGatewaySession_User4;
	private Session infobipPrmotionalGatewaySession_User5;
	
	private Session infobipPrmotionalGatewaySession_User6;
	public Session getInfobipPrmotionalGatewaySession_User6() {
		return infobipPrmotionalGatewaySession_User6;
	}
	public void setInfobipPrmotionalGatewaySession_User6(
			Session infobipPrmotionalGatewaySession_User6) {
		this.infobipPrmotionalGatewaySession_User6 = infobipPrmotionalGatewaySession_User6;
	}
	public Session getInfobipPrmotionalGatewaySession_User7() {
		return infobipPrmotionalGatewaySession_User7;
	}
	public void setInfobipPrmotionalGatewaySession_User7(
			Session infobipPrmotionalGatewaySession_User7) {
		this.infobipPrmotionalGatewaySession_User7 = infobipPrmotionalGatewaySession_User7;
	}
	public Session getInfobipPrmotionalGatewaySession_User8() {
		return infobipPrmotionalGatewaySession_User8;
	}
	public void setInfobipPrmotionalGatewaySession_User8(
			Session infobipPrmotionalGatewaySession_User8) {
		this.infobipPrmotionalGatewaySession_User8 = infobipPrmotionalGatewaySession_User8;
	}
	public Session getInfobipPrmotionalGatewaySession_User9() {
		return infobipPrmotionalGatewaySession_User9;
	}
	public void setInfobipPrmotionalGatewaySession_User9(
			Session infobipPrmotionalGatewaySession_User9) {
		this.infobipPrmotionalGatewaySession_User9 = infobipPrmotionalGatewaySession_User9;
	}
	public Session getInfobipPrmotionalGatewaySession_User10() {
		return infobipPrmotionalGatewaySession_User10;
	}
	public void setInfobipPrmotionalGatewaySession_User10(
			Session infobipPrmotionalGatewaySession_User10) {
		this.infobipPrmotionalGatewaySession_User10 = infobipPrmotionalGatewaySession_User10;
	}

	private Session infobipPrmotionalGatewaySession_User7;
	private Session infobipPrmotionalGatewaySession_User8;
	private Session infobipPrmotionalGatewaySession_User9;
	private Session infobipPrmotionalGatewaySession_User10;
	private Session infobipPrmotionalGatewaySession_OptAfrica;
	public Session getInfobipPrmotionalGatewaySession_OptAfrica() {
		return infobipPrmotionalGatewaySession_OptAfrica;
	}
	public void setInfobipPrmotionalGatewaySession_OptAfrica(
			Session infobipPrmotionalGatewaySession_OptAfrica) {
		this.infobipPrmotionalGatewaySession_OptAfrica = infobipPrmotionalGatewaySession_OptAfrica;
	}
	/**
	 * 
	 * @return infobipPrmotionalGatewaySession
	 */
	public Session getInfobipPrmotionalGatewaySession() {
		
		return infobipPrmotionalGatewaySession;
	}
	
	/**
	 * 
	 * @param infobipPrmotionalGatewaySession
	 */
	public void setInfobipPrmotionalGatewaySession(
			Session infobipPrmotionalGatewaySession) {
		this.infobipPrmotionalGatewaySession = infobipPrmotionalGatewaySession;
	}
	
	
	/**
	 * @return the infobipPrmotionalGatewaySession_Zaks
	 */
	public Session getInfobipPrmotionalGatewaySession_Zaks() {
		return infobipPrmotionalGatewaySession_Zaks;
	}
	/**
	 * @param infobipPrmotionalGatewaySession_Zaks the infobipPrmotionalGatewaySession_Zaks to set
	 */
	public void setInfobipPrmotionalGatewaySession_Zaks(
			Session infobipPrmotionalGatewaySession_Zaks) {
		this.infobipPrmotionalGatewaySession_Zaks = infobipPrmotionalGatewaySession_Zaks;
	}
	/**
	 * @return the infobipPrmotionalGatewaySession_HOF
	 */
	public Session getInfobipPrmotionalGatewaySession_HOF() {
		return infobipPrmotionalGatewaySession_HOF;
	}
	/**
	 * @param infobipPrmotionalGatewaySession_HOF the infobipPrmotionalGatewaySession_HOF to set
	 */
	public void setInfobipPrmotionalGatewaySession_HOF(
			Session infobipPrmotionalGatewaySession_HOF) {
		this.infobipPrmotionalGatewaySession_HOF = infobipPrmotionalGatewaySession_HOF;
	}
	/**
	 * @return the infobipPrmotionalGatewaySession_User1
	 */
	public Session getInfobipPrmotionalGatewaySession_User1() {
		return infobipPrmotionalGatewaySession_User1;
	}
	/**
	 * @param infobipPrmotionalGatewaySession_User1 the infobipPrmotionalGatewaySession_User1 to set
	 */
	public void setInfobipPrmotionalGatewaySession_User1(
			Session infobipPrmotionalGatewaySession_User1) {
		this.infobipPrmotionalGatewaySession_User1 = infobipPrmotionalGatewaySession_User1;
	}
	/**
	 * @return the infobipPrmotionalGatewaySession_User2
	 */
	public Session getInfobipPrmotionalGatewaySession_User2() {
		return infobipPrmotionalGatewaySession_User2;
	}
	/**
	 * @param infobipPrmotionalGatewaySession_User2 the infobipPrmotionalGatewaySession_User2 to set
	 */
	public void setInfobipPrmotionalGatewaySession_User2(
			Session infobipPrmotionalGatewaySession_User2) {
		this.infobipPrmotionalGatewaySession_User2 = infobipPrmotionalGatewaySession_User2;
	}
	/**
	 * @return the infobipPrmotionalGatewaySession_User3
	 */
	public Session getInfobipPrmotionalGatewaySession_User3() {
		return infobipPrmotionalGatewaySession_User3;
	}
	/**
	 * @param infobipPrmotionalGatewaySession_User3 the infobipPrmotionalGatewaySession_User3 to set
	 */
	public void setInfobipPrmotionalGatewaySession_User3(
			Session infobipPrmotionalGatewaySession_User3) {
		this.infobipPrmotionalGatewaySession_User3 = infobipPrmotionalGatewaySession_User3;
	}
	/**
	 * @return the infobipPrmotionalGatewaySession_User4
	 */
	public Session getInfobipPrmotionalGatewaySession_User4() {
		return infobipPrmotionalGatewaySession_User4;
	}
	/**
	 * @param infobipPrmotionalGatewaySession_User4 the infobipPrmotionalGatewaySession_User4 to set
	 */
	public void setInfobipPrmotionalGatewaySession_User4(
			Session infobipPrmotionalGatewaySession_User4) {
		this.infobipPrmotionalGatewaySession_User4 = infobipPrmotionalGatewaySession_User4;
	}
	/**
	 * @return the infobipPrmotionalGatewaySession_User5
	 */
	public Session getInfobipPrmotionalGatewaySession_User5() {
		return infobipPrmotionalGatewaySession_User5;
	}
	/**
	 * @param infobipPrmotionalGatewaySession_User5 the infobipPrmotionalGatewaySession_User5 to set
	 */
	public void setInfobipPrmotionalGatewaySession_User5(
			Session infobipPrmotionalGatewaySession_User5) {
		this.infobipPrmotionalGatewaySession_User5 = infobipPrmotionalGatewaySession_User5;
	}
	private GatewaySessionProvider(){
		
		
	}
	
	
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
	            				if(ocSMSGateway.getGatewayName().equals(OCConstants.SMS_GATEWAY_CM)) {
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
	
	
	public  Session getSessionBy(OCSMSGateway ocSMSGateway) {
		//logger.debug(">>>>>>> Started GatewaySessionProvider :: getSessionBy <<<<<<< ");
		if(ocSMSGateway.getGatewayName().equals(OCConstants.SMS_GATEWAY_UNICEL)) {

			if(ocSMSGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){
			//	logger.debug(">>>>>>> Completed GatewaySessionProvider :: getSessionBy <<<<<<< ");
				return getUnicelTransactionalGatewaySession();

			}else if(ocSMSGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
				//logger.debug(">>>>>>> Completed GatewaySessionProvider :: getSessionBy <<<<<<< ");
				return getUnicelPrmotionalGatewaySession();

			}else if(ocSMSGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_OPTIN)) {
				if(ocSMSGateway.getUserId().equals(OCConstants.SMS_GATEWAY_UNICEL_OPTIN_USER_TRESMODE)) {
					//logger.debug(">>>>>>> Completed GatewaySessionProvider :: getSessionBy <<<<<<< ");
					return getUnicelTresModeOptinGatewaySession();

				}else if(ocSMSGateway.getUserId().equals(OCConstants.SMS_GATEWAY_UNICEL_OPTIN_USER_MAGOPTIN)) {
					//logger.debug(">>>>>>> Completed GatewaySessionProvider :: getSessionBy <<<<<<< ");
					return 	getUnicelOptinGatewaySession();
				}

			}
		}else if(ocSMSGateway.getGatewayName().equals(OCConstants.SMS_GATEWAY_CM)) {

			if(ocSMSGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){
			//	logger.debug(">>>>>>> Completed GatewaySessionProvider :: getSessionBy <<<<<<< ");
				return getCMComTransactionalGatewaySession();

			}else if(ocSMSGateway.getAccountType().equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
				//logger.debug(">>>>>>> Completed GatewaySessionProvider :: getSessionBy <<<<<<< ");
				return getCMComPrmotionalGatewaySession();

			}
		}
		//Added for InfoBip
		else if(OCConstants.SMS_GATEWAY_INFOBIP.equals(ocSMSGateway.getGatewayName())){
			
			if(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL.equals(ocSMSGateway.getAccountType())){
				
				if(OCConstants.INFOBIP_ACC_RISSERVICES.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession();
					
				}else if(OCConstants.INFOBIP_ACC_ZAKS.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession_Zaks();
					
				}else if(OCConstants.INFOBIP_ACC_HOF.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession_HOF();
					
				}else if(OCConstants.INFOBIP_ACC_USER1.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession_User1();
					
				}else if(OCConstants.INFOBIP_ACC_USER2.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession_User2();
					
				}else if(OCConstants.INFOBIP_ACC_USER3.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession_User3();
					
				}else if(OCConstants.INFOBIP_ACC_USER4.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession_User4();
					
				}else if(OCConstants.INFOBIP_ACC_USER5.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					
					return getInfobipPrmotionalGatewaySession_User5();
				}else if(OCConstants.INFOBIP_ACC_USER6.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession_User6();
				}else if(OCConstants.INFOBIP_ACC_USER7.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession_User7();
				}else if(OCConstants.INFOBIP_ACC_USER8.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession_User8();
				}else if(OCConstants.INFOBIP_ACC_USER9.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession_User9();
				}else if(OCConstants.INFOBIP_ACC_USER10.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession_User10();
				}else if(OCConstants.INFOBIP_ACC_USER_OPTAFRICA.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return getInfobipPrmotionalGatewaySession_OptAfrica();
				}
				else{
					return getInfobipPrmotionalGatewaySession();
				}

				
			}
		}
		//logger.debug(">>>>>>> Completed GatewaySessionProvider :: getSessionBy <<<<<<< ");
		return null;

	}
	
	public ServerPDUEventListener createSessionListener(Session session, OCSMSGateway ocSMSGateway) {
			logger.debug(ocSMSGateway.getGatewayName()+"=====in createSessionListener for====="+ocSMSGateway.getSystemId());
		String sessionType = ocSMSGateway.getAccountType();
		if(ocSMSGateway.getGatewayName().equals(OCConstants.SMS_GATEWAY_UNICEL)) {

			if(sessionType.equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){

				return new UnicelSMSCTransactionalPDUListener(session, ocSMSGateway);

			}else if(sessionType.equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {

				return new UnicelSMSCPromotionalPDUListener(session, ocSMSGateway);
			}else if(sessionType.equals(Constants.SMS_ACCOUNT_TYPE_OPTIN)) {

				if(ocSMSGateway.getUserId().equals(OCConstants.SMS_GATEWAY_UNICEL_OPTIN_USER_TRESMODE)) {
					return new UnicelSMSCTresModeOptinPDUListener(session, ocSMSGateway);

				}else if(ocSMSGateway.getUserId().equals(OCConstants.SMS_GATEWAY_UNICEL_OPTIN_USER_MAGOPTIN)) {

					return 	new UnicelSMSCOptinPDUListener(session, ocSMSGateway);
				}

			}
		} //Added for InfoBip
		else if(ocSMSGateway.getGatewayName().equals(OCConstants.SMS_GATEWAY_CM)) {

			if(sessionType.equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){

				return new CMSMSCTransactionalPDUListener(session, ocSMSGateway);

			}else if(sessionType.equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {

				return new CMSMSCPromotionalPDUListener(session, ocSMSGateway);
			}
		}
		else if(OCConstants.SMS_GATEWAY_INFOBIP.equalsIgnoreCase(ocSMSGateway.getGatewayName())){
			logger.info("ocSMSGateway.getGatewayName() ..:"+ocSMSGateway.getGatewayName()+":..ocSMSGateway.getUserId():"+ocSMSGateway.getUserId());
			
			if(OCConstants.INFOBIP_ACC_RISSERVICES.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener(session,ocSMSGateway);
				
			}else if(OCConstants.INFOBIP_ACC_ZAKS.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_ZAKS(session,ocSMSGateway);
				
			}else if(OCConstants.INFOBIP_ACC_HOF.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_HOF(session,ocSMSGateway);
				
			}else if(OCConstants.INFOBIP_ACC_USER1.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_USER1(session,ocSMSGateway);
				
			}else if(OCConstants.INFOBIP_ACC_USER2.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_USER2(session,ocSMSGateway);
				
			}else if(OCConstants.INFOBIP_ACC_USER3.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_USER3(session,ocSMSGateway);
				
			}else if(OCConstants.INFOBIP_ACC_USER4.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_USER4(session,ocSMSGateway);
				
			}else if(OCConstants.INFOBIP_ACC_USER5.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_USER5(session,ocSMSGateway);
			}else if(OCConstants.INFOBIP_ACC_USER6.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_USER6(session,ocSMSGateway);
			}else if(OCConstants.INFOBIP_ACC_USER7.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_USER7(session,ocSMSGateway);
			}else if(OCConstants.INFOBIP_ACC_USER8.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_USER8(session,ocSMSGateway);
			}else if(OCConstants.INFOBIP_ACC_USER9.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_USER9(session,ocSMSGateway);
			}else if(OCConstants.INFOBIP_ACC_USER10.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_USER10(session,ocSMSGateway);
			}else if(OCConstants.INFOBIP_ACC_USER_OPTAFRICA.equalsIgnoreCase(ocSMSGateway.getUserId())){
				
				return new InfoBipSMSCPromotionalPDUListener_OPTAFRICA(session,ocSMSGateway);
			}
			else{
				return new InfoBipSMSCPromotionalPDUListener(session,ocSMSGateway);
			}

		}

		return null;

		
	}
	
	public void setSessionObject(Session session, OCSMSGateway ocSMSGateway) {
		//logger.debug("=====in createSessionListener for====="+ocSMSGateway.getUserId());
		if(ocSMSGateway.getGatewayName().equals(OCConstants.SMS_GATEWAY_CM)) {
			String accType = ocSMSGateway.getAccountType();
			if(accType.equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){
				
				this.setCMComTransactionalGatewaySession(session);
				
			}else if(accType.equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
				
				this.setCMComPrmotionalGatewaySession(session);;
				
			}
		}//Added for InfoBip
		else if(ocSMSGateway.getGatewayName().equals(OCConstants.SMS_GATEWAY_UNICEL)) {
			String accType = ocSMSGateway.getAccountType();
			if(accType.equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){
				
				this.setUnicelTransactionalGatewaySession(session);
				
			}else if(accType.equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
				
				this.setUnicelPrmotionalGatewaySession(session);
				
			}else if(accType.equals(Constants.SMS_ACCOUNT_TYPE_OPTIN)) {
				
				if(ocSMSGateway.getUserId().equals(OCConstants.SMS_GATEWAY_UNICEL_OPTIN_USER_TRESMODE)) {
					this.setUnicelTresModeOptinGatewaySession(session);
					
				}else if(ocSMSGateway.getUserId().equals(OCConstants.SMS_GATEWAY_UNICEL_OPTIN_USER_MAGOPTIN)) {
					
					this.setUnicelOptinGatewaySession(session);
				}
				
			}
		}
		
		else if(OCConstants.SMS_GATEWAY_INFOBIP.equals(ocSMSGateway.getGatewayName())){
			String accType = ocSMSGateway.getAccountType();
			if(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL.equals(accType)){
				if(OCConstants.INFOBIP_ACC_RISSERVICES.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					this.setInfobipPrmotionalGatewaySession(session);
					
				}else if(OCConstants.INFOBIP_ACC_ZAKS.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_Zaks(session);

				}else if(OCConstants.INFOBIP_ACC_HOF.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_HOF(session);

				}else if(OCConstants.INFOBIP_ACC_USER1.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_User1(session);

				}else if(OCConstants.INFOBIP_ACC_USER2.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_User2(session);

				}else if(OCConstants.INFOBIP_ACC_USER3.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_User3(session);

				}else if(OCConstants.INFOBIP_ACC_USER4.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_User4(session);

				}else if(OCConstants.INFOBIP_ACC_USER5.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_User5(session);
				}else if(OCConstants.INFOBIP_ACC_USER6.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_User6(session);
				}else if(OCConstants.INFOBIP_ACC_USER7.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_User7(session);
				}else if(OCConstants.INFOBIP_ACC_USER8.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_User8(session);
				}else if(OCConstants.INFOBIP_ACC_USER9.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_User9(session);
				}else if(OCConstants.INFOBIP_ACC_USER10.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_User10(session);
				}else if(OCConstants.INFOBIP_ACC_USER_OPTAFRICA.equalsIgnoreCase(ocSMSGateway.getUserId())){

					 this.setInfobipPrmotionalGatewaySession_OptAfrica(session);
				}
				else{
					this.setInfobipPrmotionalGatewaySession(session);
				}
			}
		}
	}//setSessionObject
	
	
	public Session getSessionObject(OCSMSGateway ocSMSGateway) {
		//logger.debug(ocSMSGateway.getGatewayName()+"=====in createSessionListener for====="+ocSMSGateway.getUserId());
		if(ocSMSGateway.getGatewayName().equals(OCConstants.SMS_GATEWAY_CM)) {
			
			String accType = ocSMSGateway.getAccountType();
			if(accType.equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){
				
				return getCMComTransactionalGatewaySession();
				
			}else if(accType.equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
				
				return this.getCMComPrmotionalGatewaySession();
				
			}
			
			
		}
		else if(ocSMSGateway.getGatewayName().equals(OCConstants.SMS_GATEWAY_UNICEL)) {
			String accType = ocSMSGateway.getAccountType();
			if(accType.equals(Constants.SMS_ACCOUNT_TYPE_TRANSACTIONAL)){
				
				return this.getUnicelTransactionalGatewaySession();
				
			}else if(accType.equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
				
				return this.getUnicelPrmotionalGatewaySession();
				
			}else if(accType.equals(Constants.SMS_ACCOUNT_TYPE_OPTIN)) {
				
				if(ocSMSGateway.getUserId().equals(OCConstants.SMS_GATEWAY_UNICEL_OPTIN_USER_TRESMODE)) {
					return this.getUnicelTresModeOptinGatewaySession();
					
				}else if(ocSMSGateway.getUserId().equals(OCConstants.SMS_GATEWAY_UNICEL_OPTIN_USER_MAGOPTIN)) {
					 
					return this.getUnicelOptinGatewaySession();
				}
				
			}
		}//Added for InfoBip
		else if(OCConstants.SMS_GATEWAY_INFOBIP.equals(ocSMSGateway.getGatewayName())){
			String accType = ocSMSGateway.getAccountType();
			
			if(accType.equals(Constants.SMS_ACCOUNT_TYPE_PROMOTIONAL)) {
				if(OCConstants.INFOBIP_ACC_RISSERVICES.equalsIgnoreCase(ocSMSGateway.getUserId())){
					
					return this.getInfobipPrmotionalGatewaySession();
					
				}else if(OCConstants.INFOBIP_ACC_ZAKS.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_Zaks();

				}else if(OCConstants.INFOBIP_ACC_HOF.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_HOF();

				}else if(OCConstants.INFOBIP_ACC_USER1.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_User1();

				}else if(OCConstants.INFOBIP_ACC_USER2.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_User2();

				}else if(OCConstants.INFOBIP_ACC_USER3.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_User3();

				}else if(OCConstants.INFOBIP_ACC_USER4.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_User4();

				}else if(OCConstants.INFOBIP_ACC_USER5.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_User5();
				}else if(OCConstants.INFOBIP_ACC_USER6.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_User6();
				}else if(OCConstants.INFOBIP_ACC_USER7.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_User7();
				}else if(OCConstants.INFOBIP_ACC_USER8.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_User8();
				}else if(OCConstants.INFOBIP_ACC_USER9.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_User9();
				}else if(OCConstants.INFOBIP_ACC_USER10.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_User10();
				}else if(OCConstants.INFOBIP_ACC_USER_OPTAFRICA.equalsIgnoreCase(ocSMSGateway.getUserId())){

					return this. getInfobipPrmotionalGatewaySession_OptAfrica();
				}
				else{
					return this.getInfobipPrmotionalGatewaySession();
				}
			}
		}
		
		return null;
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
				if(Constants.USER_SMSTOOL_INFOBIP.equalsIgnoreCase(ocSMSGateway.getGatewayName())){
					infoBipDlrReciepts.add(receipt.getShortMessage());
			//		logger.debug(Constants.USER_SMSTOOL_INFOBIP+"..............."+receipt.getShortMessage());
					if(infoBipDlrReciepts.size() >100 ){
						isSmsGatewayMonitor = true;
					}
				}
				else if(Constants.USER_SMSTOOL_UNICEL.equalsIgnoreCase(ocSMSGateway.getGatewayName())){
					unicelDlrReciepts.add(receipt.getShortMessage());
			//		logger.debug(Constants.USER_SMSTOOL_UNICEL+"..............."+receipt.getShortMessage());
					if(unicelDlrReciepts.size() >100 ){
						isSmsGatewayMonitor = true;
					}
				}else if(Constants.USER_SMSTOOL_CM.equalsIgnoreCase(ocSMSGateway.getGatewayName())){
					CMDlrReciepts.add(receipt.getShortMessage());
			//		logger.debug(Constants.USER_SMSTOOL_UNICEL+"..............."+receipt.getShortMessage());
					if(CMDlrReciepts.size() >100 ){
						isSmsGatewayMonitor = true;
					}
				}
				//dlrReciepts.add(receipt.getShortMessage());
				//}	
				//TODO need to enable
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
					updateStatusMap.put(new Long(submitResponse.getSequenceNumber()), submitResponse.getMessageId());
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

	private class UnicelSMSCPromotionalPDUListener implements ServerPDUEventListener{
		
		private Session unicelPromotionalSession;
		private OCSMSGateway ocSMSGateway;
		
		
		public UnicelSMSCPromotionalPDUListener() {}
		
		public UnicelSMSCPromotionalPDUListener(Session unicelPromotionalSession, OCSMSGateway ocSMSGateway) {
			
			this.unicelPromotionalSession = unicelPromotionalSession;
			this.ocSMSGateway = ocSMSGateway;
			
		}
		
		@Override
		public void handleEvent(ServerPDUEvent event) {
				
			PDU receivedPDU = event.getPDU();
				
			procesReceivedPDU(receivedPDU, ocSMSGateway,unicelPromotionalSession);	
		}
		}			
	
	/**
	 * Added For InfoBip PromotionalPDUListener
	 * @author vinod.bokare
	 */
	private class InfoBipSMSCPromotionalPDUListener implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener(Session session, OCSMSGateway ocSMSGateway) {
			logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener :: handleEvent <<<<<<< ");
		}

	} //End of InfoBipSMSCPromotionalPDUListener
	
	/**
	 * Added For InfoBip PromotionalPDUListener_ZAKS
	 * @author vinod.bokare
	 */
	private class InfoBipSMSCPromotionalPDUListener_ZAKS implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_ZAKS(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_ZAKS(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_ZAKS :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			
			
			
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_ZAKS :: handleEvent <<<<<<< ");
		}

	} //End of InfoBipSMSCPromotionalPDUListener_ZAKS 
	
	/**
	 * Added For InfoBip PromotionalPDUListener_HOF
	 * @author vinod.bokare
	 */
	private class InfoBipSMSCPromotionalPDUListener_HOF implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_HOF(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_HOF(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_HOF :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_HOF :: handleEvent <<<<<<< ");
		}

	} //End of InfoBipSMSCPromotionalPDUListener_HOF
	
	/**
	 * Added For InfoBip PromotionalPDUListener
	 * @author vinod.bokare
	 */
	private class InfoBipSMSCPromotionalPDUListener_USER1 implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_USER1(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_USER1(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER1 :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER1 :: handleEvent <<<<<<< ");
		}

	} //End of InfoBipSMSCPromotionalPDUListener_USER1
	
	/**
	 * Added For InfoBip PromotionalPDUListener
	 * @author vinod.bokare
	 */
	private class InfoBipSMSCPromotionalPDUListener_USER2 implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_USER2(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_USER2(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER2 :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER2 :: handleEvent <<<<<<< ");
		}

	} //End of InfoBipSMSCPromotionalPDUListener_USER3
	
	/**
	 * Added For InfoBip PromotionalPDUListener
	 * @author vinod.bokare
	 */
	private class InfoBipSMSCPromotionalPDUListener_USER3 implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_USER3(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_USER3(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER3 :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER3 :: handleEvent <<<<<<< ");
		}

	} //End of InfoBipSMSCPromotionalPDUListener_USER3
	
	/**
	 * Added For InfoBip PromotionalPDUListener
	 * @author vinod.bokare
	 */
	private class InfoBipSMSCPromotionalPDUListener_USER4 implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_USER4(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_USER4(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER4 :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER4 :: handleEvent <<<<<<< ");
		}

	} //End of InfoBipSMSCPromotionalPDUListener_USER4
	
	/**
	 * Added For InfoBip PromotionalPDUListener_USER5
	 * @author vinod.bokare
	 */
	private class InfoBipSMSCPromotionalPDUListener_USER5 implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_USER5(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_USER5(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER5 :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER5 :: handleEvent <<<<<<< ");
		}

	} //End of InfoBipSMSCPromotionalPDUListener_USER4
	
	
	
	private class InfoBipSMSCPromotionalPDUListener_USER6 implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_USER6(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_USER6(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER6 :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER6 :: handleEvent <<<<<<< ");
		}

	}
	
	private class InfoBipSMSCPromotionalPDUListener_USER7 implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_USER7(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_USER7(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER7 :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER7 :: handleEvent <<<<<<< ");
		}

	}
	
	
	private class InfoBipSMSCPromotionalPDUListener_USER8 implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_USER8(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_USER8(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER8 :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER8 :: handleEvent <<<<<<< ");
		}

	}
	
	
	private class InfoBipSMSCPromotionalPDUListener_USER9 implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_USER9(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_USER9(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER9 :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER9 :: handleEvent <<<<<<< ");
		}

	}
	
	private class InfoBipSMSCPromotionalPDUListener_USER10 implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_USER10(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_USER10(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER10 :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_USER10 :: handleEvent <<<<<<< ");
		}

	}
	
	
	private class InfoBipSMSCPromotionalPDUListener_OPTAFRICA implements ServerPDUEventListener{

		private Session infoBipPromotionalSession;
		private OCSMSGateway ocSMSGateway;

		public InfoBipSMSCPromotionalPDUListener_OPTAFRICA(){}

		/**
		 * @param session
		 * @param ocSMSGateway
		 */
		public InfoBipSMSCPromotionalPDUListener_OPTAFRICA(Session session, OCSMSGateway ocSMSGateway) {
			//logger.debug("InfoBipSMSCPromotionalPDUListener is created whose gateway name is ..."+ocSMSGateway.getGatewayName());
			this.infoBipPromotionalSession = session;
			this.ocSMSGateway = ocSMSGateway ;
		}

		@Override
		public void handleEvent(ServerPDUEvent event) {
			logger.debug(">>>>>>> Started GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_OPTAFRICA :: handleEvent <<<<<<< ");
			PDU receivedPDU = event.getPDU();
			procesReceivedPDU(receivedPDU, ocSMSGateway,infoBipPromotionalSession);	
			logger.debug(">>>>>>> Completed GatewaySessionProvider.InfoBipSMSCPromotionalPDUListener_OPTAFRICA :: handleEvent <<<<<<< ");
		}

	}
	
	private class UnicelSMSCOptinPDUListener implements ServerPDUEventListener{
		
		private Session unicelOptinSession;
		private OCSMSGateway ocSMSGateway;
		
		
		private  Map<Long, String> updateStatusMap = new LinkedHashMap<Long, String>();
		private  Set<String> dlrReciepts = new HashSet<String>();
		
		public UnicelSMSCOptinPDUListener() {}
		
		public UnicelSMSCOptinPDUListener(Session unicelOptinSession, OCSMSGateway ocSMSGateway) {
			
			this.unicelOptinSession = unicelOptinSession;
			this.ocSMSGateway = ocSMSGateway;
			
			
		}
		
		@Override
		public void handleEvent(ServerPDUEvent event) {
			
			PDU receivedPDU = event.getPDU();
				
			procesReceivedPDU(receivedPDU, ocSMSGateway,unicelOptinSession);	
	
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
				logger.debug("from CMSMSCTransactionalPDUListener ---> procesReceivedPDU");
			procesReceivedPDU(receivedPDU, ocSMSGateway,CMTransactionalSession);	
			


		}
		
		
	}

	
	
private class UnicelSMSCTransactionalPDUListener implements ServerPDUEventListener{
	
	private Session unicelTransactionalSession;
	private OCSMSGateway ocSMSGateway;
	
	
	private  Map<Long, String> updateStatusMap = new LinkedHashMap<Long, String>();
	private  Set<String> dlrReciepts = new HashSet<String>();
	
	public UnicelSMSCTransactionalPDUListener() {}
	
	public UnicelSMSCTransactionalPDUListener(Session unicelTransactionalSession, OCSMSGateway ocSMSGateway) {
		
		this.unicelTransactionalSession = unicelTransactionalSession;
		this.ocSMSGateway = ocSMSGateway;
		
	}
	
	@Override
	public void handleEvent(ServerPDUEvent event) {
		
		PDU receivedPDU = event.getPDU();
			logger.debug("from UnicelSMSCTransactionalPDUListener ---> procesReceivedPDU");
		procesReceivedPDU(receivedPDU, ocSMSGateway,unicelTransactionalSession);	
		


	}
	
	
}

private class UnicelSMSCTresModeOptinPDUListener implements ServerPDUEventListener {
	
	private Session unicelTresModeOptinSession;
	private OCSMSGateway ocSMSGateway;
	
	private  Map<Long, String> updateStatusMap = new LinkedHashMap<Long, String>();
	private  Set<String> dlrReciepts = new HashSet<String>();
	
	public UnicelSMSCTresModeOptinPDUListener() {}
	
	public UnicelSMSCTresModeOptinPDUListener(Session unicelTresModeOptinSession, OCSMSGateway ocSMSGateway) {
		
		this.unicelTresModeOptinSession = unicelTresModeOptinSession;
		this.ocSMSGateway = ocSMSGateway;
		
	}
	
	@Override
	public void handleEvent(ServerPDUEvent event) {
		
		PDU receivedPDU = event.getPDU();
			
		procesReceivedPDU(receivedPDU, ocSMSGateway,unicelTresModeOptinSession);	
		


	}
	
	
}

	


	private  Address createAddress(String address) 
	    throws WrongLengthOfStringException {
		Address addressInst = new Address();
		addressInst.setTon((byte) Ton.ALPHANUMERIC); // national ton
		addressInst.setNpi((byte) Npi.UNKNOWN); // numeric plan indicator
		addressInst.setAddress(address, Data.SM_ADDR_LEN);
		return addressInst;
	}
		

public void submitSm(Session mysession, String content, String mobile, Long sentId, String senderId, OCSMSGateway ocsmsGateway) throws Exception{
	
	final SubmitSM submitRequest = new SubmitSM();//why final?
	try {
		 if(senderId != null)submitRequest.setSourceAddr(createAddress(senderId));
		    
		   // request.setSourceAddr(createAddress("OPTCLT"));
		    submitRequest.setDestAddr(mobile);
		   //submitRequest.setRegisteredDelivery((byte)1);//registered_delivery" field is used to request for delivery confirmation(s). This field can only be set at PDU level, not a bind level.
		    submitRequest.setShortMessage(content);
		   // submitRequest.setScheduleDeliveryTime("");
		    submitRequest.setReplaceIfPresentFlag((byte) 0);
		    submitRequest.setEsmClass((byte) 0);
		    submitRequest.setProtocolId((byte) 0);
		    submitRequest.setPriorityFlag((byte) 0);
		    
		    if(!ocsmsGateway.isPullReports()) submitRequest.setRegisteredDelivery((byte) 1);
		    
		    submitRequest.setDataCoding((byte) 0);
		    submitRequest.setSmDefaultMsgId((byte) 0);
		    submitRequest.setSequenceNumber(sentId.intValue());
		    logger.debug("=======Request========" + submitRequest.debugString());
		    
		    
		  mysession.submit(submitRequest);
		  
		  logger.debug("message submitted suscessfully");
		  
	} catch (Exception e) {
		// TODO Auto-generated catch block
		if (e instanceof IOException || e instanceof SocketException){
            //IOException relate to the brokenpipe issue 
            //we need to close existing sessions and connections
            //restablish session
			Connection connection = mysession.getConnection();
            if (connection != null){
            	connection.close();
            }
            
            mysession = getSession(ocsmsGateway);
            this.setSessionObject(mysession, ocsmsGateway);//create the session
			ServerPDUEventListener sessionListener = this.createSessionListener(mysession, ocsmsGateway);
			
			bind(mysession, sessionListener, ocsmsGateway);
			mysession.submit(submitRequest);
		
        }
	}

	
}
	
/**
 * submit DeliverSmResp
 * @param deliverSMResp
 * @param session
 */
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
public static  Session getSessionObj(OCSMSGateway ocsmsGateway) throws Exception{
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
		logger.debug("=====in bind for====="+ocSMSGateway.getUserId());
		AddressRange addRange = new AddressRange();
		addRange.setNpi((byte)Npi.UNKNOWN);
		addRange.setTon((byte)Ton.ALPHANUMERIC);
		final BindRequest request = new BindTransciever();
	    request.setSystemId(ocSMSGateway.getSystemId());
	    request.setPassword(ocSMSGateway.getSystemPwd());
	    if(ocSMSGateway.getSystemType() != null)request.setSystemType(ocSMSGateway.getSystemType());
	    request.setAddressRange(addRange);
	    request.setInterfaceVersion((byte) 0x34);
	  //  logger.debug("Send bind request...");
	    final BindResponse response = mySession.bind(request, mylistener);
	   logger.debug(" bind response..."+response.debugString()+ " bind successful ?? "+mySession.isBound());
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
			gatewayList = ocSMSGatewayDao.findBy(Constants.SMS_SENDING_MODE_SMPP);
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
			logger.debug("eachGateway ==="+eachGateway.getGatewayName()+" for "+eachGateway.getSystemId()+!eachGateway.isEnableSessionAlive());
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

}
