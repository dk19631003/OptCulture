package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.UserEmailAlert;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserEmailAlertDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void saveOrUpdate(UserEmailAlert userEmailAlert) {
		super.saveOrUpdate(userEmailAlert);
	}
	
/*	public UserEmailAlert findByUserId(Long userId) {
		logger.debug(">>>>>>>>>>>>> entered in UserEmailAlertfindByUserId");
		UserEmailAlert userEmailAlert = null;

		String query = " FROM UserEmailAlert WHERE userId = " + userId;

		List<UserEmailAlert> list = getHibernateTemplate().find(query);

		if(list != null && list.size() > 0){
			userEmailAlert = list.get(0);
		}
		logger.debug("<<<<<<<<<<<<< completed UserEmailAlertfindByUserId ");
		return userEmailAlert;
	}
	
	public List<UserEmailAlert> findListByUserId(Long userId) {
		
		String query = " FROM UserEmailAlert WHERE userId = " + userId +" AND enabled=true" ;
		logger.info("Query for UserEmailAlertList Is -----"+query);
		List<UserEmailAlert> list = getHibernateTemplate().find(query);
		
		if(list != null && list.size() > 0) return list;
		
		return null;
			
	}

	public List<UserEmailAlert> getAllUsers() {
		String query = " FROM UserEmailAlert WHERE enabled=true ORDER BY userId DESC" ;
		logger.info("Query for UserEmailAlertList Is -----"+query);
		List<UserEmailAlert> list = getHibernateTemplate().find(query);
		
		if(list != null && list.size() > 0) return list;
		
		return null;
	}*/
	
}


/*package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.beans.UserEmailAlert;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserEmailAlertDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void saveOrUpdate(UserEmailAlert userEmailAlert) {
		super.saveOrUpdate(userEmailAlert);
	}
	
	public UserEmailAlert findByUserId(Long userId) {
		logger.debug(">>>>>>>>>>>>> entered in UserEmailAlertfindByUserId");
		UserEmailAlert userEmailAlert = null;

		String query = " FROM UserEmailAlert WHERE userId = " + userId;

		List<UserEmailAlert> list = getHibernateTemplate().find(query);

		if(list != null && list.size() > 0){
			userEmailAlert = list.get(0);
		}
		logger.debug("<<<<<<<<<<<<< completed UserEmailAlertfindByUserId ");
		return userEmailAlert;
	}
	
	public List<UserEmailAlert> findListByUserId(Long userId) {
		
		String query = " FROM UserEmailAlert WHERE userId = " + userId;
		logger.info("Query for UserEmailAlertList Is -----"+query);
		List<UserEmailAlert> list = getHibernateTemplate().find(query);
		
		if(list != null && list.size() > 0) return list;
		
		return null;
			
	}


}*/
