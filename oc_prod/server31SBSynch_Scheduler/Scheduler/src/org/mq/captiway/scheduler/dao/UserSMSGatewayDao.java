package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.UserSMSGateway;
import org.mq.captiway.scheduler.utility.Constants;


public class UserSMSGatewayDao extends AbstractSpringDao{

	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public UserSMSGateway findByUserId(Long userId, String accountType) {
		
		String qry = "FROM UserSMSGateway WHERE userId="+userId.longValue()+" AND accountType='"+accountType+"'";
		
		logger.debug("QRY ::"+qry);
		
		List<UserSMSGateway> retList =  executeQuery(qry);
		
		if(retList != null && retList.size() > 0) {
			
			return retList.get(0);
			
		}else{
			
			return null;
		}
		
	}
	
	
}
