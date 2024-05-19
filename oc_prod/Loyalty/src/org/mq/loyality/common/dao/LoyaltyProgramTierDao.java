package org.mq.loyality.common.dao;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.loyality.common.hbmbean.LoyaltyProgramTier;
import org.mq.loyality.exception.LoyaltyProgramException;
import org.mq.loyality.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LoyaltyProgramTierDao {

	@Autowired
	private SessionFactory sessionFactory;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	public LoyaltyProgramTierDao() {
	}
	  public LoyaltyProgramTier getTierById(Long tierId) {
		try{
			
			logger.info("tier_id is ::: "+tierId.longValue());
			List<LoyaltyProgramTier> tierList= sessionFactory.getCurrentSession().createQuery("FROM LoyaltyProgramTier WHERE tierId ="+tierId.longValue()).list();
			if( tierList!=null && tierList.size() > 0) 
				return tierList.get(0);
			else return null;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public List<LoyaltyProgramTier> fetchTiersByProgramId(Long programId) throws LoyaltyProgramException{
		
		List<LoyaltyProgramTier> listOfTiers = null;
		
		try{
			
			String queryStr = " FROM LoyaltyProgramTier WHERE programId = "+programId;
			return sessionFactory.getCurrentSession().find(queryStr);
			
		}catch(Exception e){
			throw new LoyaltyProgramException("fetch tiers failed");
		}
	}

	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
