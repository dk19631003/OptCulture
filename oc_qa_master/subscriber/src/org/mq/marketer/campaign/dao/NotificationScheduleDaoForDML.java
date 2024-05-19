package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.NotificationSchedule;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationScheduleDaoForDML extends AbstractSpringDaoForDML {
	
	public NotificationScheduleDaoForDML() {
		}
	
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
	
	
	public void deleteByCollection(List<NotificationSchedule> notificationScheduleList) {
		logger.debug(">>>>>>> Started SMSCampaignScheduleDao :: deleteByCollection <<<<<<< ");
		getHibernateTemplate().deleteAll(notificationScheduleList);
		logger.debug(">>>>>>> Completed SMSCampaignScheduleDao :: deleteByCollection <<<<<<< ");
		
	}
	 
	public int deleteByCampaignId(Long notificationId) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM NotificationSchedule WHERE notificationId="+notificationId);
	}
	
	public int deleteByCampaignId(String notificationIds) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM NotificationSchedule WHERE notificationId in("+notificationIds+")");
	}

	public void deleteByCampSchId(Long notificationCsId) {
		logger.debug(">>>>>>> Started NotificationScheduledaoForDml :: deleteByCampSchId <<<<<<< ");
		 String qry  = " DELETE FROM NotificationSchedule where notificationCsId ="+notificationCsId+ " or parentId ="+notificationCsId;
		 logger.info(">>>>>>>>>>> qry is  ::"+qry);
		 getHibernateTemplate().bulkUpdate(qry);
		 logger.debug(">>>>>>> Completed NotificationScheduleDaoForDml :: deleteByCampSchId <<<<<<< ");
	 }
	
	
}
