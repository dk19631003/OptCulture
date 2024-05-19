package org.mq.captiway.scheduler.dao;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Notification;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationDaoForDML extends AbstractSpringDaoForDML{
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

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

	public void updateNotificationStatus(Long notificationId) {
		try {
    	String qryStr = 
    		" UPDATE Notification c SET c.status = ( SELECT IF(count(cs.status)>0,'Running','Sent')" +
    		" FROM Notification_schedule cs WHERE cs.notification_Id="+notificationId+" AND cs.status=0)" +
    		" WHERE c.notification_Id="+notificationId ;
    	int result = executeJdbcUpdateQuery(qryStr);
    	
    	if(result <= 0) {
    		if(logger.isWarnEnabled()) logger.warn("notification Campaign status could not be updated for the notification id :"+notificationId);
    	}
		}catch (Exception e) {
			logger.error("updateNotificationStatus error"+e);
		}
    	
    }
}
