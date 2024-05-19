package org.mq.captiway.scheduler.dao;

import java.util.Collection;
import java.util.List;

import org.mq.captiway.scheduler.beans.CouponCodes;
//import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
//import org.mq.marketer.campaign.beans.RewardReferraltype;
//import org.mq.marketer.campaign.beans.SpecialReward;
//import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.captiway.scheduler.beans.ReferralcodesIssued;
import org.springframework.jdbc.core.JdbcTemplate;

public class ReferralcodesIssuedDaoForDML extends AbstractSpringDaoForDML{
	public ReferralcodesIssuedDaoForDML() {
	}
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	 public void saveByCollection(Collection<ReferralcodesIssued> referralCodesCollection){
	    	super.saveOrUpdateAll(referralCodesCollection);
	    }
	
	
	
	
	public void saveOrUpdate(ReferralcodesIssued referrlcodes) {
		super.saveOrUpdate(referrlcodes);
	}
	
	 public void delete(ReferralcodesIssued referrlcodes){
		   super.delete(referrlcodes);
	   }
	 
		
}



