package org.mq.marketer.campaign.dao;

import java.io.Serializable;

import org.mq.marketer.campaign.beans.GiftCardSkus;
import org.springframework.jdbc.core.JdbcTemplate;

public class GiftCardSkusDaoForDML extends AbstractSpringDaoForDML implements Serializable{
	
	public GiftCardSkusDaoForDML() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void saveOrUpdate(GiftCardSkus giftCardSkus) {
        super.saveOrUpdate(giftCardSkus);
    }

}
