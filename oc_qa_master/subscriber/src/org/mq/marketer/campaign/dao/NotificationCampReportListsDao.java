package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.NotificationCampReportLists;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationCampReportListsDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	 
	public NotificationCampReportListsDao() {}
	
	private JdbcTemplate jdbcTemplate;
	 
	 public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@SuppressWarnings("unchecked")
	public NotificationCampReportLists findByNotificationCampReportId(Long notificationCampRepId) {
		logger.info("notificationCampRepId ::"+notificationCampRepId);
		List<NotificationCampReportLists> tempCampReportLists = null;
		tempCampReportLists = executeQuery("FROM NotificationCampReportLists where notificationCampaignReportId="+notificationCampRepId);
		if(tempCampReportLists!=null && tempCampReportLists.size()>0) {
			return (NotificationCampReportLists)tempCampReportLists.get(0);
		}
		return null;
		
		
	}
	
}
