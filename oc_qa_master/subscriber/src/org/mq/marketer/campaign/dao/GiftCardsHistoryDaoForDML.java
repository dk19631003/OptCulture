package org.mq.marketer.campaign.dao;

import java.io.Serializable;

import org.mq.marketer.campaign.beans.GiftCardsHistory;
import org.springframework.jdbc.core.JdbcTemplate;

public class GiftCardsHistoryDaoForDML extends AbstractSpringDaoForDML implements Serializable{
	
	public GiftCardsHistoryDaoForDML() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void saveOrUpdate(GiftCardsHistory giftCardsHistory) {
        super.saveOrUpdate(giftCardsHistory);
    }

}
