package org.mq.marketer.campaign.dao;

import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.UserDesignedCustomRows;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({"unused"})
public class UserDesignedCustomRowsDaoForDML extends AbstractSpringDaoForDML{


	JdbcTemplate jdbcTemplate;
	
	public UserDesignedCustomRowsDaoForDML() {}
	
    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(UserDesignedCustomRows userDesignedCustomRows) {
        super.saveOrUpdate(userDesignedCustomRows);
    }

}

