package org.mq.optculture.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.timer.LoyaltyRewardExpiryThread;
import org.mq.optculture.timer.MembershipProvider;

public class LoyaltyRewardExpiryUtility {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Users user;

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}
	
	public boolean runExpiry(){
		
		try {
			LoyaltyProgramDao programDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			List<LoyaltyProgram> listOfPrograms = programDao.fetchUserPrograms(user.getUserId());
			
			
			if(listOfPrograms == null || listOfPrograms.isEmpty()){
				
				logger.debug("No programs currently found, so returning");
				return false;
			}
			
			 List<Thread> lisfOfThreads = new ArrayList<Thread>();
				for (LoyaltyProgram loyaltyProgram : listOfPrograms) {
					
					MembershipProvider provider = new MembershipProvider(loyaltyProgram);
					
					Map<Long, LoyaltyProgramTier> tierMap = prepareTierMapForProgram(loyaltyProgram);
					for(int i = 0; i <= 10; i++){
				
						Thread th = new Thread(new LoyaltyRewardExpiryThread(provider, loyaltyProgram, tierMap));
						lisfOfThreads.add(th);
						th.start();
						
					}
				
				
					Iterator<Thread> iter;
					iter = lisfOfThreads.iterator();
					
					while (iter.hasNext()) {
						try {
							iter.next().join();
						}
						catch (InterruptedException e) {
							logger.error("Exception while joining threads... ", e);
						}
					}
				}
			return true;
		} catch (Exception e) {
			logger.error("Exception ", e);
			return false;
			
		}
	}
private List<LoyaltyProgramTier> getAllTiers(Long programID) throws Exception {
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getAllTierByProgramId(programID);
	}
public Map<Long, LoyaltyProgramTier> prepareTierMapForProgram(LoyaltyProgram program){
		
		Map<Long, LoyaltyProgramTier> tierMap = null;
			try{
				List<LoyaltyProgramTier> retTier = getAllTiers(program.getProgramId());
				
				if(retTier != null && retTier.size() > 0){
					tierMap = new HashMap<Long, LoyaltyProgramTier>();
					
					for(LoyaltyProgramTier tier : retTier){
						tierMap.put(tier.getTierId(), tier);
					}
				}
				
			}catch(Exception e){
				logger.error("Exception in fetching tiers...",e);
			}
			return tierMap;
			
			
		}
	
}
