package org.mq.captiway.scheduler.dao;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.DRSMSSent;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class DRSMSSentDaoForDML extends AbstractSpringDaoForDML {


	private JdbcTemplate jdbcTemplate;
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public void saveOrUpdate(DRSMSSent drSmsSent) {
		super.saveOrUpdate(drSmsSent);
	}

	public void delete(DRSMSSent drSmsSent) {
		super.delete(drSmsSent);
	}
		
	public void updateclicks(Long id) {
		try {
			String updateQuery = "update DRSMSSent set clicks = clicks+1 where id = "+id+"";
			executeUpdate(updateQuery);
		}catch (Exception e) {
			logger.info("Exception while updating clicks Based OnSms QueueId "+e);
		}
	}
}

