package org.mq.marketer.campaign.dao;

import java.io.Serializable;

import org.mq.marketer.campaign.beans.GiftCards;
import org.springframework.jdbc.core.JdbcTemplate;

public class GiftCardsDaoForDML extends AbstractSpringDaoForDML implements Serializable{
	
	public GiftCardsDaoForDML() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void saveOrUpdate(GiftCards giftCards) {
        super.saveOrUpdate(giftCards);
    }
	
	public void UpdateGiftCardBalances() {
		
		
	}

}
