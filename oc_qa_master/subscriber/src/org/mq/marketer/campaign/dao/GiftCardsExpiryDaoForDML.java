package org.mq.marketer.campaign.dao;

import java.io.Serializable;

import org.mq.marketer.campaign.beans.GiftCardsExpiry;
import org.springframework.jdbc.core.JdbcTemplate;

public class GiftCardsExpiryDaoForDML extends AbstractSpringDaoForDML implements Serializable{
	
	public GiftCardsExpiryDaoForDML() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void saveOrUpdate(GiftCardsExpiry giftCardsExpiry) {
        super.saveOrUpdate(giftCardsExpiry);
    }

}
