package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class SegmentRulesDaoForDML extends AbstractSpringDaoForDML{
	
	public SegmentRulesDaoForDML() {}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void saveOrUpdate(SegmentRules segmentRules) {
		super.saveOrUpdate(segmentRules);
	}
	public void delete(SegmentRules segmentRules) {
		super.delete(segmentRules);
	}
	
	public void deleteSharedEmailCampaign(String mlIdStr, String campaignId) {
		
		try {
			String sql = " DELETE FROM mlists_campaigns WHERE list_id IN ('"+mlIdStr+"') AND campaign_id IN ( '"+campaignId+"' ) ";
			logger.info(""+sql);
			jdbcTemplate.update(sql);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
			
	}	
public void deleteSharedSMSCampaign(String mlIdStr, String SMScampaignIds) {
		
		try {
			String sql = " DELETE FROM mlists_sms_campaigns WHERE list_id IN ('"+mlIdStr+"') AND sms_campaign_id IN ('"+SMScampaignIds+"') ";
			logger.info(""+sql);
			jdbcTemplate.update(sql);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
			
	}	
public void deleteSharedAssociation(Long segRuleId) {
		
		try {
			String sql = " DELETE FROM segments_domains WHERE seg_rule_id="+segRuleId+" ";
			
			jdbcTemplate.update(sql);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
	}

}
