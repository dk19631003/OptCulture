package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.NotificationSchedule;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationScheduleDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public NotificationScheduleDao(){}
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<NotificationSchedule> findActiveOrDraftNotificationSchedules(Long notificationId) {
		logger.debug(">>>> Started findByNotificationCampaignId.");
		
		List<NotificationSchedule> notificationScheduleList = null;
		String queryString = "FROM NotificationSchedule WHERE notificationId="+notificationId+" AND status IN(0,2)";
		
		notificationScheduleList = executeQuery(queryString);
		
		if(notificationScheduleList != null && notificationScheduleList.size() > 0){
			
			logger.debug(">>>> Completed findByNotificationCampaignId.");
			return notificationScheduleList;
		}
		
		logger.debug(">>>> Completed findByNotificationCampaignId, but NotificationScheduleList is either null or of zero size.");
		return notificationScheduleList;
	}

	@SuppressWarnings("unchecked")
	public List<NotificationSchedule> getByNotificationCampaignId(Long notificationId) {
		try {
			return executeQuery(" FROM NotificationSchedule WHERE notificationId="+notificationId+" ORDER BY scheduledDate");
		}catch (Exception e) {
			logger.error("Exception "+e);
		}
		return null;
	}

	private Long id;
	public synchronized Long getCurrentId() {
		try {
			if(id == null) {
				
				List list = executeQuery("SELECT MAX(notificationCsId) FROM NotificationSchedule ") ;
				logger.info(" List :"+list);
				this.id = (list != null && list.size() > 0 && list.get(0) != null) ? (Long)list.get(0):0 ;
				
			}
			return ++id;
		} catch (DataAccessException e) {
			logger.error("** Exception : while getting the current id ", e);
			return id+100000;
		}
		
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getAllChidren(Long smsCsId, Long notificationId) {
		List<Object[]> list=null;
			list=executeQuery("select cs.notificationCsId from NotificationSchedule cs where cs.notificationCsId "+ 
					"in(select cs1.parentId from NotificationSchedule cs1 where cs1.parentId="+smsCsId+"and cs1.notificationId="+notificationId+")");
			logger.debug("number of children are************"+list.size());
			if(list != null && list.size() > 0) {
				return list;
			}else {
				return null;
		}
		
	
	}

	public NotificationSchedule findById(Long id) {
		NotificationSchedule campaignSchedule = null;
		String quString = "from NotificationSchedule where notificationCsId="+id;
		try{
			campaignSchedule =(NotificationSchedule) executeQuery(quString).get(0);
			return campaignSchedule;
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public NotificationSchedule findByNotificationCampRepId(Long notificationCampRepId) {
		List<NotificationSchedule> list= executeQuery("FROM NotificationSchedule WHERE notificationCrId ="+notificationCampRepId);
		
		if(list != null && list.size() > 0) {
			return (NotificationSchedule)list.get(0);
		}
		else {
			return null;
		}
	
	}

}
