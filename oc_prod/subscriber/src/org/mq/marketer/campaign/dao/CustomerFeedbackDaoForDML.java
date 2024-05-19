package org.mq.marketer.campaign.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CustomerFeedback;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class CustomerFeedbackDaoForDML extends AbstractSpringDaoForDML{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void saveOrUpdate(CustomerFeedback customerfeedback) {
		super.saveOrUpdate(customerfeedback);
	}
	
	public void delete(CustomerFeedback customerfeedback) {
		super.delete(customerfeedback);
	}
	
}
