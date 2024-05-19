package org.mq.marketer.campaign.dao;

import java.util.Collection;
import java.util.List;

import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.ReferralcodesRedeemed;
import org.mq.marketer.campaign.beans.RewardReferraltype;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.springframework.jdbc.core.JdbcTemplate;

public class ReferralcodesRedeemedDaoForDML extends AbstractSpringDaoForDML{
	public ReferralcodesRedeemedDaoForDML() {
	}
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	


	public void saveOrUpdate(ReferralcodesRedeemed referralcodesredeemed) {
		super.saveOrUpdate(referralcodesredeemed);
	}
	
	 public void delete(ReferralcodesRedeemed referralcodesredeemed){
		   super.delete(referralcodesredeemed);
	   }
	 
	 public void updatetableByreferralId(Long referredcid,String refcodE,Long orgid,String refstatus)
	 {
			String query=" UPDATE ReferralcodesRedeemed  SET referredcid="+referredcid+", refcode="+refcodE+",orgId="+orgid+","
					+ "refcodestatus="+refstatus+" WHERE referral_id=1";
					
			executeUpdate(query);
			//jdbcTemplate.execute(query);
		}



}



