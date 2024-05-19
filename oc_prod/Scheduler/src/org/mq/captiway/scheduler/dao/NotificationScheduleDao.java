package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.NotificationSchedule;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class NotificationScheduleDao extends AbstractSpringDao{
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
		public NotificationScheduleDao() {}
		private JdbcTemplate jdbcTemplate;
	    
	    public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
	
	@SuppressWarnings("unchecked")
	public List<NotificationSchedule> getInactiveList(String currentDateStr) {
		try {
			return executeQuery(" SELECT DISTINCT ncs " +
					" FROM NotificationSchedule ncs, Users usr " +
					" WHERE ncs.userId = usr.userId AND ncs.userId IS NOT NULL AND ncs.scheduledDate <= '" + currentDateStr + "' " +
					" AND ncs.status = 0 AND ( DATE(ncs.scheduledDate) > DATE(usr.packageExpiryDate) OR usr.enabled = 0) ");
		}catch (Exception e) {
			logger.error("NotificationScheduleDao getInactiveList() :"+e);
			return null;
		}
	}

	public List<NotificationSchedule> getActiveForLongCampList(String currentDateStr, int hrs) {
		String qry= " SELECT * FROM Notification_schedule ncs, users usr " +
				" WHERE ncs.user_id = usr.user_id AND ncs.status = 0 "
				+ "AND (DATE_ADD(ncs.scheduled_date,INTERVAL "+hrs+" HOUR)) < '" + currentDateStr + "' "
						+ "AND ( DATE(ncs.scheduled_date) <= DATE(usr.package_expiry_date) AND usr.enabled = 1)";
			logger.info("jdbcTemplate>>>>>>>"+jdbcTemplate);
			logger.info("qry>>>>>>>"+qry);
			
			List<NotificationSchedule> list = null;
			try {
				list = jdbcTemplate.query(qry, new RowMapper<NotificationSchedule>() {
			        public NotificationSchedule mapRow(ResultSet rs, int rowNum) throws SQLException {
			        	NotificationSchedule notificationSchedule = new NotificationSchedule();
			        	notificationSchedule.setNotificationCsId(rs.getLong("notification_cs_id"));
			        	notificationSchedule.setStatus(rs.getByte("status"));
			        	notificationSchedule.setNotificationId(rs.getLong("notification_Id"));
			        	 if(rs.getDate("scheduled_date") != null) {
				            	Calendar cal = Calendar.getInstance();
				            	cal.setTime(rs.getTimestamp("scheduled_date"));
				            	notificationSchedule.setScheduledDate(cal);
				            } 
			        	Users user  = new Users();
						user.setUserId(rs.getLong("user_id"));
						notificationSchedule.setUserId(user.getUserId());
			            return notificationSchedule;
			        }
			    });
			} catch (Exception e) {
				logger.error("Exception ***", e);
			}
			return list;
		}
	
	@SuppressWarnings("unchecked")
	public List<NotificationSchedule> getActiveList(String currentDateStr) {
		try {
		return executeQuery(
				" SELECT DISTINCT ncs " +
				" FROM NotificationSchedule ncs, Users usr " +
				" WHERE ncs.userId = usr.userId AND ncs.scheduledDate <= '" + currentDateStr + "' " +
				" AND ncs.status = 0 AND DATE(ncs.scheduledDate) <= DATE(usr.packageExpiryDate) AND usr.enabled = 1 ");
		}catch (Exception e) {
			logger.error("NotificationScheduleDao getActiveList() :"+e);
			return null;
		}
		
	}

	@SuppressWarnings("unchecked")
	public boolean isScheduledCampaignByCSId(Long notificationCsId) {
		List<NotificationSchedule> list = executeQuery("FROM NotificationSchedule WHERE notificationCsId="+notificationCsId +" AND status !=2");
		if(list != null && list.size() >0) {
			return true;
		}
		return false;
	}

}
