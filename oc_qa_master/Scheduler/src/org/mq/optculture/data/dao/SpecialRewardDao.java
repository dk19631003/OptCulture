package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SpecialReward;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.utility.Constants;

public class SpecialRewardDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public List<SpecialReward> findAllForExpiry(){
		
		try {
			String query = " FROM SpecialReward WHERE statusSpecialReward='Active' AND "
					+ "rewardExpiryType IS NOT NULL AND rewardExpiryValue IS NOT NULL and "
					+ "createdBy IN(SELECT userId FROM Users WHERE Date(now())<=Date(packageExpiryDate) AND enabled=true) ORDER BY createdBy";
			
			List<SpecialReward> retList = executeQuery(query);
			
			return retList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return null;
	}
}
