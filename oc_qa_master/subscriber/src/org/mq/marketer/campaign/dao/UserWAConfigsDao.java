package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.UserWAConfigs;
import org.mq.marketer.campaign.beans.WATemplates;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserWAConfigsDao extends AbstractSpringDao{
	
	

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public UserWAConfigs findByUserId(Long userId) {
		
		String qry = "FROM UserWAConfigs WHERE userId="+userId.longValue();
		
		List<UserWAConfigs> retList =  executeQuery(qry);
		
		if(retList != null && retList.size() > 0) {
			
			return retList.get(0);
			
		}else{
			
			return null;
		}
		
	}
	
	public  UserWAConfigs findWAConfigsByProvider(Long userId, String provider) throws Exception{
		
		try {
			List<UserWAConfigs> configsList = null;
			
			String qry = "FROM UserWAConfigs where userId="+userId+" and provider='"+provider+"' order by 1 desc";
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
	}
}
