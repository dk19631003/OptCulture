package org.mq.marketer.campaign.dao;

import java.io.Serializable;

import org.springframework.jdbc.core.JdbcTemplate;

public class GiftCardsHistoryDao extends AbstractSpringDao implements Serializable{
	
	public GiftCardsHistoryDao() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
