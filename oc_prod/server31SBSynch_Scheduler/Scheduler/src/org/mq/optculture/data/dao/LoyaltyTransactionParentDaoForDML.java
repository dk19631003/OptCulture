package org.mq.optculture.data.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.LoyaltyTransactionParent;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.dao.AbstractSpringDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyTransactionParentDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public LoyaltyTransactionParentDaoForDML() {
	}
	
	public void saveOrUpdate(LoyaltyTransactionParent loyaltyTransactionParent) {
		super.saveOrUpdate(loyaltyTransactionParent);
	}
	
}
