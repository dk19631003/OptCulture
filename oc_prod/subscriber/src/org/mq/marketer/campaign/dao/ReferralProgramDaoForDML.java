package org.mq.marketer.campaign.dao;

import org.mq.marketer.campaign.beans.ReferralProgram;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.springframework.jdbc.core.JdbcTemplate;

public class ReferralProgramDaoForDML extends AbstractSpringDaoForDML{
	public ReferralProgramDaoForDML() {
	}
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	public void saveOrUpdate(ReferralProgram referralProgram) {
		super.saveOrUpdate(referralProgram);
	}
	
	 public void delete(ReferralProgram referralProgram){
		   super.delete(referralProgram);
	   }
	 
	
	 public void deleteProgramIdsByreferralId(Long referralId)
	 {
			String query="DELETE FROM referral_program where referral_id="+referralId;
			jdbcTemplate.execute(query);
		}


	
}

