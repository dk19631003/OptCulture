package org.mq.captiway.scheduler.dao;

import org.mq.captiway.scheduler.beans.SparkBaseCard;
import org.springframework.jdbc.core.JdbcTemplate;

public class SparkBaseCardDaoForDML extends AbstractSpringDaoForDML{
	
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	  public void saveOrUpdate(SparkBaseCard sparkBaseCard) {
	        super.saveOrUpdate(sparkBaseCard);
	    }
	    
	    public void delete(SparkBaseCard sparkBaseCard) {
	        super.delete(sparkBaseCard);
	    }

}
