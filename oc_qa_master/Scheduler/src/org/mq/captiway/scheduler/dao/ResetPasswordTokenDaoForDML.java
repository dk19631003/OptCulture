package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ResetPasswordToken;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;


public class ResetPasswordTokenDaoForDML extends AbstractSpringDaoForDML {
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
public ResetPasswordTokenDaoForDML(){}
	
/*	public ResetPasswordToken find(Long id){
		return (ResetPasswordToken)super.find(ResetPasswordToken.class, id);
	}
	*/
	public void saveOrUpdate(ResetPasswordToken token){
		super.saveOrUpdate(token);
	}
	
	public void delete(ResetPasswordToken token){
		super.delete(token);
	}
	
	/*public ResetPasswordToken findByTokenValue(String token) {
		try {
			
			String query = " FROM ResetPasswordToken WHERE tokenValue = '"+token+"' ";
			
			List<ResetPasswordToken> list =  executeQuery(query);
			logger.debug("list ::"+list.size());
			if(list!=null && list.size()>0) {
				return list.get(0);
			}
			else return null;
		} catch (DataAccessException e) {
			return null;
		}
		
	}*/

}
