package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.SMSBounces;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSSuppressedContacts;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.OCSMSGatewayDao;
import org.mq.captiway.scheduler.dao.SMSBouncesDao;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSSuppressedContactsDao;
import org.mq.captiway.scheduler.services.SMSCampaignDeliveryReportsHandler;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.optculture.business.helper.SMSDlrReportUpdateThread;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.service.GatewaySessionHelper;
import org.mq.optculture.service.GatewaySessionProvider;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.smpp.pdu.SubmitSMResp;
/**
 * 
 * @author proumyaa
 * job is </BR>1.to check all gateway sessions are alive.
 * 2.update the MIDs and DLR. 
 *
 */

public class SMSGatewaySessionMonitor extends TimerTask{
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 
	
	
	private volatile boolean isRunning;
	public boolean isRunning() {
		//logger.debug("isRunning ::"+isRunning);
		return isRunning;
	}
	
	@Override
	public void run() {
		logger.debug(">>>>>>> Started SMSGatewaySessionMonitor :: run <<<<<<< ");
		try {
			this.isRunning = true;
			//			logger.debug("=====session monitor started=====");
			OCSMSGatewayDao ocSMSGatewayDao = null;
			try {
				ocSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new Exception("Exception while fetching the DAO "+OCConstants.OCSMSGATEWAY_DAO);
			}
			List<OCSMSGateway> gatewayList = null;
			
			try {
				gatewayList = ocSMSGatewayDao.findBy(Constants.SMS_SENDING_MODE_SMPP);
			} catch (Exception e) {
				throw e;
			}
			GatewaySessionProvider sessionProvider = GatewaySessionProvider.getInstance(gatewayList);//by this time everything will be done
			if(sessionProvider == null) return;

			if(gatewayList == null || gatewayList.size() == 0) return ;
			logger.debug("=====gatewayList====="+gatewayList.size());
			//keep all the sessions alive 
			//GatewaySessionHelper gatewaySessionHelper = new GatewaySessionHelper();
			//gatewaySessionHelper.checkSessionsAlive(gatewayList, sessionProvider);
			sessionProvider.checkSessionsAlive(gatewayList, sessionProvider);
			/*logger.info("After checkSessionsAlive ....................");
    		logger.debug("=====perform updations=====");*/
			performDlrUpdations(sessionProvider);



		} catch (Exception e) {
			logger.error("Exception while processing...", e);
		}finally{

			isRunning = false;

		}

		logger.debug(">>>>>>> Completed SMSGatewaySessionMonitor :: run <<<<<<< ");

	}//run
	
	public  void performDlrUpdations(GatewaySessionProvider sessionProvider) {
		logger.debug(">>>>>>> Started SMSGatewaySessionMonitor :: performDlrUpdations <<<<<<< ");
		//logger.debug("=====in performDlrUpdations for=====");
		//process the map & dlr through a thread
		Set<String> unicelDlrReciepts = new HashSet<String>();
		Set<String> CMDlrReciepts = new HashSet<String>();
		Set<String> infoBipDlrReciepts = new HashSet<String>();
		
		unicelDlrReciepts.addAll(sessionProvider.getUnicelDlrReciepts());
		infoBipDlrReciepts.addAll(sessionProvider.getInfoBipDlrReciepts());
		CMDlrReciepts.addAll(sessionProvider.getCMDlrReciepts());
		logger.debug("===== dlrReciepts ====="+unicelDlrReciepts.size() + " & " +infoBipDlrReciepts.size());
		
		sessionProvider.getUnicelDlrReciepts().removeAll(unicelDlrReciepts);
		sessionProvider.getInfoBipDlrReciepts().removeAll(infoBipDlrReciepts);
		sessionProvider.getCMDlrReciepts().removeAll(CMDlrReciepts);
		Map<Long, String> updateStatusMap = new HashMap<Long, String>();
		updateStatusMap.putAll(sessionProvider.getUpdateStatusMap());
			
		Set<Long> sentIDs = updateStatusMap.keySet();
		for (Long sentID : sentIDs) {
			
			sessionProvider.getUpdateStatusMap().remove(sentID);
			
		}
		
		
		SMSDlrReportUpdateThread dlrProcessThread = new SMSDlrReportUpdateThread(unicelDlrReciepts, infoBipDlrReciepts,CMDlrReciepts, updateStatusMap);
		dlrProcessThread.start();
		logger.debug(">>>>>>> Completed SMSGatewaySessionMonitor :: performDlrUpdations <<<<<<< ");
	}
	
	
	
	

}
