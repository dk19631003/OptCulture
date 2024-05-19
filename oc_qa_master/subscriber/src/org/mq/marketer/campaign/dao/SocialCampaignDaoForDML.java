package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SocialCampaign;
import org.mq.marketer.campaign.beans.SocialCampaignSchedule;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

	public class SocialCampaignDaoForDML extends AbstractSpringDaoForDML {
	
		JdbcTemplate jdbcTemplate;
		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		
		public SocialCampaignDaoForDML() {
			// TODO Auto-generated constructor stub
		}
		
		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}

		/*public List<SocialCampaign> findByUserId(Long userId) {
			return getHibernateTemplate().find(" from SocialCampaign where userId='"+ userId+"' ORDER BY createdDate DESC ");
		}*/

	    public void saveOrUpdate(SocialCampaign socialCampaign) {
	        super.saveOrUpdate(socialCampaign);
	    }

	    public void delete(SocialCampaign socialCampaign) {
	        super.delete(socialCampaign);
	    }

	   /* public List findAll() {
	        return super.findAll(SocialCampaign.class);
	    }
	    
	    public List<SocialCampaign> findAllByUserIdAndStatus(String userId, String status) {

	    	List<SocialCampaign> list = getHibernateTemplate().find(" FROM SocialCampaign WHERE userId='"+ userId + "' AND campaignStatus='"+ status+"'");
	    	return list;
	    }*/
	    
	    public String updateCampaignStatusById(Long socialCampaignId) {
			
	    	// sets Campaign to SENT status
	    	String queryStr = "UPDATE  social_campaigns SET campaign_status='"+ Constants.SOCIAL_CAMP_STATUS_SENT +
	    	                "' WHERE campaign_id="+ socialCampaignId +
							" AND (SELECT count(*) FROM social_campaign_schedules WHERE campaign_id="+ socialCampaignId +") =" +
							"(SELECT count(*) FROM social_campaign_schedules WHERE campaign_id ="+ socialCampaignId + 
							" AND schedule_status = '"+ Constants.SOCIAL_SCHEDULE_STATUS_SENT +"') ";
	    	
	    	int flag =  jdbcTemplate.update(queryStr);
	    	
	    	if(flag == 1) {
	    		logger.debug("** Campaign Id : "+ socialCampaignId + " status changed to "+ Constants.SOCIAL_CAMP_STATUS_SENT);
	    		return Constants.SOCIAL_CAMP_STATUS_SENT;
	    	}
	    	
	    	String queryStr2 = "UPDATE  social_campaigns SET campaign_status='"+ Constants.SOCIAL_CAMP_STATUS_RUNNING +
            "' WHERE campaign_id="+ socialCampaignId +
			" AND (SELECT count(*) FROM social_campaign_schedules WHERE campaign_id="+ socialCampaignId +")  > " +
			"(SELECT count(*) FROM social_campaign_schedules WHERE campaign_id ="+ socialCampaignId + 
			" AND schedule_status = '"+ Constants.SOCIAL_CAMP_STATUS_SENT +"') ";

	    	 flag =   jdbcTemplate.update(queryStr2);
	    	 
	    	 if(flag == 1) {
		    		logger.debug("** Campaign Id : "+ socialCampaignId + " status changed to "+ Constants.SOCIAL_CAMP_STATUS_RUNNING);
		    		return Constants.SOCIAL_CAMP_STATUS_RUNNING;
	    	 }
	    	 
	    	 return "Error : flag = "+flag;
		}
	    
	   /* public List<SocialCampaign> findAllCampToBeSent() {
	    	
	    	List<SocialCampaign> list = getHibernateTemplate().find(" FROM SocialCampaign WHERE  campaignStatus='"+ Constants.SOCIAL_CAMP_STATUS_ACTIVE +
	    			"' OR campaignStatus='"+ Constants.SOCIAL_CAMP_STATUS_RUNNING +"' ");
	    	
	    	return list;
	    }
	    
	    public SocialCampaign findCampaignByName(Long userId, String campName) {
	    	
	    	List<SocialCampaign> list = getHibernateTemplate().find(" FROM SocialCampaign WHERE  userId="+ userId + " AND campaignName='" + campName + "'");
	    	return list.get(0);
	    }
	    */
	    
	}

