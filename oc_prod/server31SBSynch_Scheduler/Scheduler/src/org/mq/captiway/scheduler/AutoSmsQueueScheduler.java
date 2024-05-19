package org.mq.captiway.scheduler;

import java.util.List;
import java.util.TimerTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.AutoSmsQueue;
import org.mq.captiway.scheduler.dao.AutoSmsQueueDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class AutoSmsQueueScheduler extends TimerTask {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public AutoSmsQueueScheduler(){
		
	}

	private AutoSmsSender smsSender;
	
	public void run() {
		
		if(logger.isInfoEnabled()) logger.info(" -- just entered AutoSmsQueueScheduler--");
		try{
			
			AutoSMSObjectsQueue autoSMSObjectsQueue = (AutoSMSObjectsQueue) ServiceLocator.getInstance().getBeanByName(OCConstants.AUTOSMS_OBJECTS_QUEUE);
			AutoSmsQueueDao autoSmsQueueDao = (AutoSmsQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
			List<AutoSmsQueue> smsQueueList = autoSmsQueueDao.findByStatus(OCConstants.ASQ_STATUS_ACTIVE);
			if(logger.isInfoEnabled()) logger.info(" Active Count in EmailQueue : "+smsQueueList.size());
			
			if(smsQueueList != null && smsQueueList.size() > 0) {
				autoSMSObjectsQueue.addCollection(smsQueueList);
			}
			else {
				return;
			}
			
			if((smsSender == null || smsSender.isRunning == false) && autoSMSObjectsQueue.getQueueSize() > 0) {
				smsSender = new AutoSmsSender(autoSMSObjectsQueue);
				smsSender.start();
			}
			
		} catch(Exception e) {
			logger.error(" ** Exception  : Root ", e );
		}
		
		System.gc();
	}

	
	
}
