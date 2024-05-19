package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.UserWAConfigs;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;


public class UserWAConfigsDao extends AbstractSpringDao{


	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	public UserWAConfigs findByUserId(Long userId) {

		String qry = "FROM UserWAConfigs WHERE userId="+userId.longValue();

		//logger.debug("QRY ::"+qry);

		List<UserWAConfigs> retList =  executeQuery(qry);

		if(retList != null && retList.size() > 0) {

			return retList.get(0);

		}else{

			return null;
		}

	}//findByUserId

	public  UserWAConfigs findWAConfigsByProvider(Long userId, String provider) throws Exception{
		
		try {
			List<UserWAConfigs> configsList = null;
			
			String subQry = "";
			subQry = (provider == null || provider.isEmpty())? "" : " AND provider='"+provider+"'" ;
			
			String qry = "FROM UserWAConfigs where userId="+userId+subQry+" order by 1 desc";
			logger.debug("UserWAConfigsDao.findWAConfigsByProvider qry :: "+qry);
			configsList =  executeQuery(qry);
			
			if(configsList != null && configsList.size() > 0 ){
				return configsList.get(0);
			}else{
				return null;
			}
			
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}//findWAConfigsByProvider

	
}
