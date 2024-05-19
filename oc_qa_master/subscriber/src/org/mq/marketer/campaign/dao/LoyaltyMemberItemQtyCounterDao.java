package org.mq.marketer.campaign.dao;

import java.util.List;
import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.marketer.campaign.beans.LoyaltyMemberItemQtyCounter;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyMemberItemQtyCounterDao extends AbstractSpringDao implements Serializable{ 

	public LoyaltyMemberItemQtyCounterDao() {}
	 private SessionFactory sessionFactory;
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	 public List<LoyaltyMemberItemQtyCounter> findItemsCounter(String SPRuleID, Long loyaltyID ){
	    	
	    	try {	
	        	String qry = "FROM LoyaltyMemberItemQtyCounter WHERE SPRuleID in("+SPRuleID+") AND loyaltyID="+loyaltyID;	
	        	
	        	logger.debug("qry ==="+qry);
	        	return executeQuery(qry);
	        }
	        catch(Exception e) {
	        	logger.error("e===>"+e);
	        	return null;
	        }
	    	
	    	
	    }
	
	
}