package org.mq.marketer.campaign.dao;

import java.text.SimpleDateFormat;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.NotificationCampaignSent;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationCampaignSentDaoForDML extends AbstractSpringDaoForDML {
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final  Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private JdbcTemplate jdbcTemplate;
	
	public NotificationCampaignSentDaoForDML() {
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(NotificationCampaignSent notificationCampaignSent) {
		super.saveOrUpdate(notificationCampaignSent);
	}

	public void delete(NotificationCampaignSent notificationCampaignSent) {
		super.delete(notificationCampaignSent);
	}

	public void updateNotificationRead(String sentId) {
		try {
			String queryStr = "update NotificationCampaignSent set notificationRead = true where sentId="+sentId;
			executeUpdate(queryStr);
			logger.info("updated successfully !");
		}catch (Exception e) {
			logger.error("Exception while updating sent status in pushnotification"+e);
		}
		
	}
	public int updateNotificationCampaignSent(String type, Long cr_id, Long startId, Long endId, Set<Long> openSentIdsSet) {
		String queryStr = null;
		//long id = idL.longValue();
		 String sentIdStr = Utility.getIdsAsString(openSentIdsSet);
		 
		 if(sentIdStr.isEmpty()) return 0;
		
		
		String fromtable = "";
		String tableId = "";
		
		
		if(type.equals(Constants.CS_TYPE_CLICKS)){
			
			fromtable = "notificationClicks";
			tableId = "click_id" ;
			
		}
			
			
			queryStr = 
					"UPDATE Notification_campaign_sent cs " +
					" JOIN (SELECT count("+tableId+") as cnt, sent_id as sent_id FROM "+fromtable+
					" WHERE sent_id IN("+sentIdStr+") GROUP BY sent_id) o " +
					" ON cs.sent_id = o.sent_id" +
					"	SET cs.clicks=o.cnt where cs.notification_cr_id="+cr_id.longValue();

		logger.info(" Notification update query ::"+queryStr);
		return ( (queryStr == null)? 0 : jdbcTemplate.update(queryStr));
		
	}
	
	
	   
	
}
