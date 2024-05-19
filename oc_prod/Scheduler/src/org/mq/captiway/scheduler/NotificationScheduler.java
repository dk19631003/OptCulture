package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Notification;
import org.mq.captiway.scheduler.beans.NotificationSchedule;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.NotificationDao;
import org.mq.captiway.scheduler.dao.NotificationScheduleDao;
import org.mq.captiway.scheduler.dao.NotificationScheduleDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class NotificationScheduler extends TimerTask implements ApplicationContextAware{
	//use servicelocator , change method names , cross check the inside column names
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private NotificationScheduleDao notificationScheduleDao;
	private NotificationScheduleDaoForDML notificationScheduleDaoForDML;
	
	private NotificationQueue notificationQueue;
	
	private ApplicationContext applicationContext;
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public NotificationScheduleDao getNotificationScheduleDao() {
		return notificationScheduleDao;
	}

	public void setNotificationScheduleDao(NotificationScheduleDao notificationScheduleDao) {
		this.notificationScheduleDao = notificationScheduleDao;
	}

	public NotificationScheduleDaoForDML getNotificationScheduleDaoForDML() {
		return notificationScheduleDaoForDML;
	}

	public void setNotificationScheduleDaoForDML(NotificationScheduleDaoForDML notificationScheduleDaoForDML) {
		this.notificationScheduleDaoForDML = notificationScheduleDaoForDML;
	}

	public NotificationQueue getNotificationQueue() {
		return notificationQueue;
	}

	public void setNotificationQueue(NotificationQueue notificationQueue) {
		this.notificationQueue = notificationQueue;
	}

	@Override
	public void run(){
		logger.debug(">>>>>>> Started NotificationScheduler :: run <<<<<<< ");
		if(logger.isDebugEnabled()) logger.debug("-------just entered to send Active notification Campaigns------");
		try{
			ServiceLocator locator = ServiceLocator.getInstance();
			MyCalendar myCal = new MyCalendar();
			List<NotificationSchedule> inactiveNotificationList = notificationScheduleDao.getInactiveList(myCal.toString());
			UsersDao usersDao = (UsersDao)locator.getDAOByName(OCConstants.USERS_DAO);
			NotificationDao notificationDao = ((NotificationDao)locator.getDAOByName(OCConstants.NOTIFICATION_DAO));//use servicelocator
			
			if(logger.isDebugEnabled()) logger.debug("inactiveNotificationList="+inactiveNotificationList);
			if(inactiveNotificationList.size()>0){
				for (NotificationSchedule notificationCampaignSchedule : inactiveNotificationList) {
					notificationCampaignSchedule.setStatus((byte) 9);
					notificationScheduleDaoForDML.saveOrUpdate(notificationCampaignSchedule);
					Users user=usersDao.findByUserId(notificationCampaignSchedule.getUserId());
					Notification notification=notificationDao.findByCampaignId(notificationCampaignSchedule.getNotificationId());
					Utility.sendCampaignFailureAlertMailToSupport(user,"","",notification.getNotificationName(),notificationCampaignSchedule.getScheduledDate(),
							"User Disabled/Expired","","","");
					
				}
			}
			List<NotificationSchedule> activeForLongCampList =new ArrayList<NotificationSchedule>();
			
			//Change status of campaigns active for more than 3 hrs
			String hrs=PropertyUtil.getPropertyValueFromDB(OCConstants.NUM_OF_HRS_BEFORE_CAMP_EXPIRES);
			NotificationScheduleDao notificationScheduleDao = ((NotificationScheduleDao)locator.getDAOByName(OCConstants.NOTIFICATION_SCHEDULE_DAO));
			activeForLongCampList=notificationScheduleDao.getActiveForLongCampList(myCal.toString(),Integer.parseInt(hrs));
			
			if(activeForLongCampList!=null && activeForLongCampList.size()>0){
				for (NotificationSchedule noticationCampaignSchedule : activeForLongCampList) {
					noticationCampaignSchedule.setStatus((byte) 9);
					logger.info("made to expire "+noticationCampaignSchedule.getNotificationId());
					notificationScheduleDaoForDML.saveOrUpdate(noticationCampaignSchedule);
					Users user=usersDao.findByUserId(noticationCampaignSchedule.getUserId());
					Notification notification=notificationDao.findByCampaignId(noticationCampaignSchedule.getNotificationId());
					Utility.sendCampaignFailureAlertMailToSupport(user,"","",notification.getNotificationName(),noticationCampaignSchedule.getScheduledDate(),
							"Schedule Failure","","","");
				}
			}
			List<NotificationSchedule> notificationList = notificationScheduleDao.getActiveList(myCal.toString());
			if(notificationList.size()>0){
				notificationQueue.addCollection(notificationList);
			}
			notificationScheduleDaoForDML.updateDisabledUsersNotificationCampaignStatus(myCal.toString());
			
			if(notificationQueue.getQueueSize() > 0) {
				NotificationCampSubmitter.startNotificationCampSubmitter(applicationContext);
			
			}
		}catch(Exception e){
			e.printStackTrace();
			if(logger.isErrorEnabled()) logger.error("**Exception while getting the notification campaign Schedule objects",e);
			logger.error("Exception ::::" , e);
		}
		logger.debug(">>>>>>> Completed notificationScheduler :: run <<<<<<< ");
	}
	
}
