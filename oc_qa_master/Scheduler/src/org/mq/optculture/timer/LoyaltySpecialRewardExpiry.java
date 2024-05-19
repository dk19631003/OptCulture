package org.mq.optculture.timer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SpecialReward;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.optculture.data.dao.SpecialRewardDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.scheduling.annotation.Scheduled;

public class LoyaltySpecialRewardExpiry {
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	//@Scheduled(cron="0 0/5 * 1/1 * ?")  //for every 5 minutes
	//@Scheduled(cron="0 0 0 1 1/1 ?")    //for every month 1st at 00:00hrs
	@Scheduled(cron="0 0 0 2 * ?") 
	public void run_task() {
		try {
			logger.info("Loyalty special reward expiry timer started...");
			SpecialRewardDao specialRewardDao = (SpecialRewardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPECIAL_REWARD_DAO);
			List<SpecialReward> specialRewards = specialRewardDao.findAllForExpiry();//can get chunks wise
			int SpecialRewardExpiryWorkersCnt = Integer.parseInt(PropertyUtil.getPropertyValueFromDB("SpecialRewardExpiryWorkersCnt"));
			if(specialRewards == null || specialRewards.isEmpty()) {
				
				logger.debug("No special rewards found ");
				return;
			}
			 ExecutorService executor = Executors.newFixedThreadPool(SpecialRewardExpiryWorkersCnt);
			 for (SpecialReward specialReward : specialRewards) {
				
				Thread worker = new Thread(new SpecialRewardExpiryThread(specialReward));
				executor.execute(worker);
				
				/*Iterator<Thread> iter;
				iter = lisfOfThreads.iterator();
				
				while (iter.hasNext()) {
					try {
						iter.next().join();
					}
					catch (InterruptedException e) {
						logger.error("Exception while joining threads... ", e);
					}
				}*/
				
			}
			executor.shutdown();
			while (!executor.isTerminated()) {
				//logger.debug("something is wrong in reward expiry ==please check");
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		
	}
}
