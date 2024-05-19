package org.mq.captiway.scheduler.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.NotificationSchedule;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationScheduleDaoForDML extends AbstractSpringDaoForDML{
		
		private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
		public NotificationScheduleDaoForDML() {}
		 private JdbcTemplate jdbcTemplate;
		    
		    public JdbcTemplate getJdbcTemplate() {
				return jdbcTemplate;
			}

			public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
				this.jdbcTemplate = jdbcTemplate;
			}
		
		public void saveOrUpdate(NotificationSchedule notificationSchedule) {
			super.saveOrUpdate(notificationSchedule);
		}

		public void delete(NotificationSchedule notificationSchedule) {
		    super.delete(notificationSchedule);
		}
		
		 
		public int deleteByCampaignId(Long notificationId) {
			return getHibernateTemplate().bulkUpdate(
					"DELETE FROM NotificationSchedule WHERE notificationId="+notificationId);
		}
		
		
		public int updateDisabledUsersNotificationCampaignStatus(String currentDateStr) {
			try {
				String queryStr = "UPDATE Notification_schedule ncs "+
								" JOIN users usr " +
								" ON ncs.user_id = usr.user_id " +
								" SET ncs.status = 3 " +
								" WHERE ncs.scheduled_date <= ' " + currentDateStr + " ' AND ncs.status = 0 " +
								" AND (DATE(ncs.scheduled_date) > DATE(usr.package_expiry_date) OR usr.enabled = 0 );";
				return ( (queryStr == null)? 0 : jdbcTemplate.update(queryStr) );
			} catch(Exception e) {
				if(logger.isErrorEnabled()) logger.error("** Exception while updating the notification campaign status of expired/disabled users", e);
				return 0;
			}
		}
	
}
