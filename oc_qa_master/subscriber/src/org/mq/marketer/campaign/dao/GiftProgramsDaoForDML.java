package org.mq.marketer.campaign.dao;

import java.io.Serializable;

import org.mq.marketer.campaign.beans.GiftPrograms;
import org.springframework.jdbc.core.JdbcTemplate;

public class GiftProgramsDaoForDML extends AbstractSpringDaoForDML implements Serializable{
	
	public GiftProgramsDaoForDML() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void saveOrUpdate(GiftPrograms giftPrograms) {
        super.saveOrUpdate(giftPrograms);
    }

}
