package org.mq.marketer.campaign.dao;

import org.mq.marketer.campaign.beans.UserWAConfigs;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserWAConfigsDaoForDML extends AbstractSpringDaoForDML{



	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(UserWAConfigs userWAConfigs) {
		super.saveOrUpdate(userWAConfigs);
	}

	public void delete(UserWAConfigs userWAConfigs) {
		super.delete(userWAConfigs);
	}

}
