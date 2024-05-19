package org.mq.optculture.timer;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.LoyaltyProgram;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.beans.LoyaltyProgramTier;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.scheduling.annotation.Scheduled;

public class LoyaltyPerkExpiryScheduler { 
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	//@Scheduled(cron="0 0/5 * 1/1 * ?")  //for every 5 minutes
	@Scheduled(cron="0 0 0 1 1/1 ?")    //for every month 1st at 00:00hrs
	//@Scheduled(cron="0 30 0 10 1/1 ?")    //for every month 10th at 00:30hrs
	public void run_task() {
		try {
			logger.info("Loyalty perk reward expiry timer started...");
			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			List<LoyaltyProgram> listOfPrograms = loyaltyProgramDao.fetchAllPerkPrograms();
			int SpecialRewardExpiryWorkersCnt = Integer.parseInt(PropertyUtil.getPropertyValueFromDB("SpecialRewardExpiryWorkersCnt"));
			logger.info("expiry workers count"+SpecialRewardExpiryWorkersCnt);
            if((listOfPrograms == null || listOfPrograms.isEmpty()) ){
				
				logger.debug("No programs currently found, so returning");
				return;
			}
			
            if(listOfPrograms != null && !listOfPrograms.isEmpty()) {
            	
            	for (LoyaltyProgram loyaltyProgram : listOfPrograms) {
            		
            		List<LoyaltyProgramTier> retTier = tierForProgram(loyaltyProgram);
            		ExecutorService executor = Executors.newFixedThreadPool(retTier.size());
            		
            		for (LoyaltyProgramTier tier : retTier) {
            			
            			Thread worker = new Thread(new PerkExpirySchedularThread(tier,loyaltyProgram));
        				executor.execute(worker);
            			
            		}
            		
            		executor.shutdown();
            		while (!executor.isTerminated()) {
            			//logger.debug("something is wrong in reward expiry ==please check");
            		}
            		
            	}
            }
           
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
	}
	
    public List<LoyaltyProgramTier> tierForProgram(LoyaltyProgram program){
		
		List<LoyaltyProgramTier> tierList = null;
			try{
				tierList = getAllTiers(program.getProgramId());
				logger.info("loyalty program tier list>>>"+tierList.size());
				if(tierList==null || tierList.size()<=0) return null;
				
			}catch(Exception e){
				logger.error("Exception in fetching tiers...",e);
			}
			return tierList;
			
			
		}
    
    private List<LoyaltyProgramTier> getAllTiers(Long programID) throws Exception {
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.fetchTiersByProgramId(programID);
	}

}
