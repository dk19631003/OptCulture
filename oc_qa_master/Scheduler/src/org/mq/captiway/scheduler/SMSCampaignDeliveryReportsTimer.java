package org.mq.captiway.scheduler;

import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.services.SMSCampaignDeliveryReportsHandler;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SMSCampaignDeliveryReportsTimer extends TimerTask implements ApplicationContextAware{

	ApplicationContext context;
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		// TODO Auto-generated method stub
		this.context = context;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		SMSCampaignDeliveryReportsHandler handler = new SMSCampaignDeliveryReportsHandler();
		
		boolean isActiveDone = handler.runHandlerForActiveDlrReports();
		if(!isActiveDone) {
			
			logger.error("Some error while fetching the active reports");
		}
		
		boolean isExpiryDone = handler.runHandlerForExpiredDlrReports();
		if(!isExpiryDone) {
			
			logger.error("Some error while fetching the expired reports");
		}
		
	}
	
	
}
