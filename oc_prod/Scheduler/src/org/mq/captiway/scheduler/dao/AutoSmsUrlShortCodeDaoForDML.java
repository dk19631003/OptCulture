package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.AutosmsSenturlShortcode;
import org.springframework.jdbc.core.JdbcTemplate;

public class AutoSmsUrlShortCodeDaoForDML extends AbstractSpringDaoForDML{

	public AutoSmsUrlShortCodeDaoForDML() {
	}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	 public void saveOrUpdate(AutosmsSenturlShortcode autosmsUrlShortCode) {
      super.saveOrUpdate(autosmsUrlShortCode);
  }
	
	 
	 public void saveByCollection(List<AutosmsSenturlShortcode> autosmsUrlShortCodeList) {
      super.saveOrUpdateAll(autosmsUrlShortCodeList);
  }
}
