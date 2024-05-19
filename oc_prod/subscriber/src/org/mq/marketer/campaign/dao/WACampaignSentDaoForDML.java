package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.WACampaignSent;
import org.mq.marketer.campaign.controller.service.ClickaTellApi;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class WACampaignSentDaoForDML extends AbstractSpringDaoForDML{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private SessionFactory sessionFactory;
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public WACampaignSentDaoForDML() {}
  

    public void saveOrUpdate(WACampaignSent waCampaignSent) {
        super.saveOrUpdate(waCampaignSent);
    }

    public void delete(WACampaignSent waCampaignSent) {
        super.delete(waCampaignSent);
    }

    public void updateStatus(String status,String refId,String mobile) {	
		
    	
		if(mobile.startsWith("91")&& mobile.length() == 12) {
			
			
			mobile = mobile.substring(2);
		}
    	String updateQry = " UPDATE wa_campaign_sent SET status='"+status+"' WHERE api_msg_id='"+refId+"' AND mobile_number like '%"+mobile+"%'";
		
		logger.debug(updateQry);		
		executeJdbcUpdateQuery(updateQry);		
	
	}
    public void updateBounceStatus(String status,Long sentId) {	
		
		String updateQry = " UPDATE wa_campaign_sent SET status='"+status+"' WHERE sent_id="+sentId+"";
		
		logger.debug(updateQry);		
		executeJdbcUpdateQuery(updateQry);		
	
	}
    
    public void setUniqueMsgId(String msgId,String mrId,String mobile) {	
		
	         String updateQry = " UPDATE wa_campaign_sent SET api_msg_id='"+msgId+"' WHERE api_msg_id='"+mrId+"' AND mobile_number like '%"+mobile+"%'";
		
		logger.info(updateQry);		
		executeJdbcUpdateQuery(updateQry);		
	
	}

	
	public void updateWaCampSentStatus(String qryStr) {
		
		getJdbcTemplate().execute(qryStr);
		
		
		
	}
	
	
	

}
