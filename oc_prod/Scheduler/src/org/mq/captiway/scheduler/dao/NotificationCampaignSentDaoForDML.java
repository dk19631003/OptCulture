package org.mq.captiway.scheduler.dao;

import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.NotificationCampaignSent;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationCampaignSentDaoForDML extends AbstractSpringDaoForDML {
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
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

	public void saveByCollection(Collection<NotificationCampaignSent> campList) {
		super.saveOrUpdateAll(campList);
	}
	
	
	public void updateInitialStatus(String sentId, String status) {
		try {
			String queryStr = "update NotificationCampaignSent set status ='"+status+"' where sentId="+sentId;
			executeUpdate(queryStr);
			logger.info("updated successfully !");
		}catch (Exception e) {
			logger.error("Exception while updating sent status in pushnotification"+e);
		}
		
	}
	
}
