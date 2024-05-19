package org.mq.marketer.campaign.dao;

import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.springframework.jdbc.core.JdbcTemplate;

public class SparkBaseLocationDetailsDaoForDML extends AbstractSpringDaoForDML{ 
	
	
	
	
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
