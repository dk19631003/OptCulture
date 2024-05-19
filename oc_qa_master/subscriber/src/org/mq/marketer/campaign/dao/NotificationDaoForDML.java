package org.mq.marketer.campaign.dao;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Notification;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public NotificationDaoForDML() {
	}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(Notification notification) {
		super.saveOrUpdate(notification);
		logger.info("saved notification successfully.");
	}

	public void delete(Notification notification) {
		super.delete(notification);
	}

	@SuppressWarnings("rawtypes")
	public void deleteByCollection(Collection list) {
		getHibernateTemplate().deleteAll(list);
	}

	public int deleteByNotificationCampaignId(String notificationId) {
		return getHibernateTemplate().bulkUpdate("DELETE FROM Notification WHERE notificationId in(" + notificationId + ")");
	}

	public int deleteByCampaignIdFromIntermediateTable(String notificationId) {
		String qry = "DELETE FROM mlists_notification WHERE notification_id in(" + notificationId + ")";
		return getJdbcTemplate().update(qry);

	}

}
