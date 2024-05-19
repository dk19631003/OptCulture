package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.RewardReferraltype;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
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
	
	
	public void saveOrUpdate(RewardReferraltype rewardreferralType) {
		super.saveOrUpdate(rewardreferralType);
	}
	
	 public void delete(RewardReferraltype rewardreferralType){
		   super.delete(rewardreferralType);
	   }
	 
		
}



