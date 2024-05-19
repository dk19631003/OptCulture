package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ResetPasswordToken;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;


public class ResetPasswordTokenDaoForDML extends AbstractSpringDaoForDML {
	private static final Logger logger = LogManager
			.getLogger(Constants.SUBSCRIBER_LOGGER);
public ResetPasswordTokenDaoForDML(){}
	/*
	public ResetPasswordToken find(Long id){
		return (ResetPasswordToken)super.find(ResetPasswordToken.class, id);
	}*/
	
	public void saveOrUpdate(ResetPasswordToken token){
		super.saveOrUpdate(token);
	}
	
	public void delete(ResetPasswordToken token){
		super.delete(token);
	}
/*	
	public ResetPasswordToken findByTokenValue(String token) {
		try {
			
			String query = " FROM ResetPasswordToken WHERE tokenValue = '"+token+"' ";
			
			List<ResetPasswordToken> list =  executeQuery(query);
			if(list!=null && list.size()>0) {
				logger.debug("list ::"+list.size());
				return list.get(0);
			}
			else return null;
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
		
	}*/

}
