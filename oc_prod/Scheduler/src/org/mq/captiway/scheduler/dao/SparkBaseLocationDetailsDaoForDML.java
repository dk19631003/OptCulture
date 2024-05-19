package org.mq.captiway.scheduler.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SparkBaseLocationDetails;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class SparkBaseLocationDetailsDaoForDML extends AbstractSpringDaoForDML { 
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(SparkBaseLocationDetails sparkBaseLocationDetails) {
        super.saveOrUpdate(sparkBaseLocationDetails);
    }
    
    public void delete(SparkBaseLocationDetails sparkBaseLocationDetails) {
    	logger.info("delete the objet");
        super.delete(sparkBaseLocationDetails);
    }
}
