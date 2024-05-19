package org.mq.marketer.campaign.dao;

import java.util.List;
import java.io.Serializable;

import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.marketer.campaign.beans.LoyaltyMemberItemQtyCounter;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyMemberItemQtyCounterDaoforDML extends AbstractSpringDaoForDML implements Serializable{ 

	public LoyaltyMemberItemQtyCounterDaoforDML() {}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public void saveOrUpdate(LoyaltyMemberItemQtyCounter loyaltyMemberItemQtyCounter) {
		super.saveOrUpdate(loyaltyMemberItemQtyCounter);
	}
	
	
	public int updateCurrQty(Long loyaltyID, Long specialRewardID, int qty, int requiredQty){
		
		
		try {
			String query = "UPDATE loyalty_membership_item_quantinty set qty=if(qty=0,"+(requiredQty-qty)+",("+requiredQty+"+qty-"+qty+")) WHERE loyalty_id="+loyaltyID+" AND sp_rule_id="+specialRewardID;
			return executeJdbcUpdateQuery(query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("Exception ", e);
			return 0;
		}
		
		
	}
	
	
}
