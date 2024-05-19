package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.RewardReferraltype;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.springframework.jdbc.core.JdbcTemplate;

public class RewardReferraltypeDaoForDML extends AbstractSpringDaoForDML{
	public RewardReferraltypeDaoForDML() {
	}
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	public void saveOrUpdate(RewardReferraltype rewardreferralType) {
		
		logger.info("referral value is"+rewardreferralType);
		super.saveOrUpdate(rewardreferralType);
	}
	
	 public void delete(RewardReferraltype rewardreferralType){
		   super.delete(rewardreferralType);
	   }
	 public void deleteProgramIdsByreferralId(Long referralId)
	 {
			String query="DELETE FROM reward_on_referral_type where referral_id="+referralId;
			jdbcTemplate.execute(query);
		}
	public void deleteRewardReferralByRefId(Long referralId) {
		String query = "DELETE FROM reward_on_referral_type where ref_id=" + referralId;
		jdbcTemplate.execute(query);
	}
		
}


