package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.AutosmsUrls;
import org.springframework.jdbc.core.JdbcTemplate;

public class AutoSmsUrlDaoForDML extends AbstractSpringDaoForDML {

	public AutoSmsUrlDaoForDML() {
	}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(AutosmsUrls autosmsUrls) {
		super.saveOrUpdate(autosmsUrls);
	}

	public void saveByCollection(List<AutosmsUrls> autosmsUrlsList) {
		super.saveOrUpdateAll(autosmsUrlsList);
	}
}
