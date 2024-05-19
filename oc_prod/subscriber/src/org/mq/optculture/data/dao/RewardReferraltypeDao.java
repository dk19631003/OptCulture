package org.mq.optculture.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyMemberItemQtyCounter;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.ReferralProgram;
import org.mq.marketer.campaign.beans.RewardReferraltype;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.model.events.Events;
import org.mq.optculture.model.updatecontacts.Loyalty;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class RewardReferraltypeDao extends AbstractSpringDao{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	 public RewardReferraltype getMilestonesListByRefIdnew(Long referralid) {
			
			List<RewardReferraltype> tierList = executeQuery(" FROM RewardReferraltype WHERE referralid = "+referralid.longValue());
			if(tierList != null && tierList.size() > 0) 
				
				return (RewardReferraltype) tierList;
			
			else return null;
		}
		


	 public List<RewardReferraltype> getMilestonesListByRefId(Long referralid) {
			
			List<RewardReferraltype> tierList = executeQuery(" FROM RewardReferraltype WHERE referralid = "+referralid.longValue());
		
			logger.info("tierlist is"+tierList);
			if(tierList != null && tierList.size() > 0) 
				
				return tierList;
			
			else return null;
		}
		


	 public int getMilestonessize(Long referralid) {
			
			List<RewardReferraltype> tierList = executeQuery(" FROM RewardReferraltype WHERE referralid = "+referralid.longValue());
		
			logger.info("tierlist is"+tierList);
			if(tierList != null && tierList.size() > 0) 
				
				return tierList.size();
			
			else return 0;
		}
		
















}


