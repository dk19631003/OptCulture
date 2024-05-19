package org.mq.captiway.scheduler.dao;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.DRSMSChannelSent;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class DRSMSChannelSentDaoForDML extends AbstractSpringDaoForDML {


	private JdbcTemplate jdbcTemplate;
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public void saveOrUpdate(DRSMSChannelSent drSmsChannelSent) {
		super.saveOrUpdate(drSmsChannelSent);
	}

	public void delete(DRSMSChannelSent drSmsChannelSent) {
		super.delete(drSmsChannelSent);
	}
		

}

