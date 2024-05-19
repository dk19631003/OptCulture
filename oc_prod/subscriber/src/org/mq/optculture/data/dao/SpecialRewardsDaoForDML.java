package org.mq.optculture.data.dao;

import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.springframework.jdbc.core.JdbcTemplate;

public class SpecialRewardsDaoForDML extends AbstractSpringDaoForDML{
	public SpecialRewardsDaoForDML() {
	}
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	public void saveOrUpdate(SpecialReward specialRewards) {
		super.saveOrUpdate(specialRewards);
	}
	
	 public void delete(SpecialReward specialRewards){
		   super.delete(specialRewards);
	   }

	public void deleteProgramIdsByrewardId(Long rewardId){
		String query="DELETE FROM spreward_program where sprule_id="+rewardId;
		jdbcTemplate.execute(query);
	}
	public void updateSpecialRewardStatus(Long rewardId, String status){
		try {
			executeUpdate("UPDATE SpecialReward SET statusSpecialReward='"+status+"' WHERE rewardId="+rewardId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
	}
	
}
