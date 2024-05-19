package org.mq.optculture.data.dao;

import java.io.Serializable;

import org.mq.captiway.scheduler.beans.LoyaltyFraudAlert;
import org.mq.captiway.scheduler.dao.AbstractSpringDaoForDML;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyFraudAlertDaoForDML extends AbstractSpringDaoForDML implements Serializable {
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void saveOrUpdate(LoyaltyFraudAlert fraudAlert) {
       super.saveOrUpdate(fraudAlert);
    }

}
