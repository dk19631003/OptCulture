package org.mq.marketer.campaign.controller.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class SMSGatewaySessionMonitor extends TimerTask{
private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER); 
	
	
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
				gatewayList = ocSMSGatewayDao.find();
			} catch (Exception e) {
				throw e;
			}

			if(gatewayList == null || gatewayList.size() == 0) return ;
			logger.debug("=====gatewayList====="+gatewayList.size());
			GatewaySessionProvider sessionProvider = GatewaySessionProvider.getInstance(gatewayList);//by this time everything will be done
			if(sessionProvider == null) return;
			
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
		Set<String> infoBipDlrReciepts = new HashSet<String>();
		
		unicelDlrReciepts.addAll(sessionProvider.getCMDlrReciepts());
		
		logger.debug("===== dlrReciepts ====="+unicelDlrReciepts.size() + " & " +infoBipDlrReciepts.size());
		
		sessionProvider.getCMDlrReciepts().removeAll(unicelDlrReciepts);
		
		Map<Long, String> updateStatusMap = new HashMap<Long, String>();
		updateStatusMap.putAll(sessionProvider.getUpdateStatusMap());
			
		Set<Long> sentIDs = updateStatusMap.keySet();
		for (Long sentID : sentIDs) {
			
			sessionProvider.getUpdateStatusMap().remove(sentID);
			
		}
		
		
		SMSDlrReportUpdateThread dlrProcessThread = new SMSDlrReportUpdateThread(unicelDlrReciepts,  updateStatusMap);
		dlrProcessThread.start();
		logger.debug(">>>>>>> Completed SMSGatewaySessionMonitor :: performDlrUpdations <<<<<<< ");
	}
	
	
	
}
