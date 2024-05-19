package org.mq.marketer.campaign.dao;

import org.mq.marketer.campaign.beans.ZoneTemplateSettings;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class ZoneTemplateSettingsDaoForDML extends AbstractSpringDaoForDML{


	JdbcTemplate jdbcTemplate;

	public ZoneTemplateSettingsDaoForDML() {}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(ZoneTemplateSettings zoneTemplateSettings) {
		super.saveOrUpdate(zoneTemplateSettings);
	}

	public void delete(ZoneTemplateSettings zoneTemplateSettings) {
		super.delete(zoneTemplateSettings);
	}




}

