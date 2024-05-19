package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CampaignSchedule;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

	public class EmailCampaignDraftStatusCheckTimer extends TimerTask  implements ApplicationContextAware {


		private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
		
		private CampaignsDao campaignsDao;

		public CampaignsDao getCampaignsDao() {
			return campaignsDao;
		}

		public void setCampaignsDao(CampaignsDao campaignsDao) {
			this.campaignsDao = campaignsDao;
		}	
		
		private UsersDao usersDao;
		public UsersDao getUsersDao() {
			return usersDao;
		}
		public void setUsersDao(UsersDao usersDao) {
			this.usersDao = usersDao;
		}
		
		private ApplicationContext applicationContext;
		@Override
		public void setApplicationContext(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}
		
//		private PmtaMailmergeSubmitter mailSubmitter;
		
		@Override
		public void run() {
			
			if(logger.isInfoEnabled()) logger.info("------- just entered EmailCampaignDraftStatusCheckTimer -------");
			
			MyCalendar myCal = new MyCalendar();
			
			try {
				
				UsersDao usersDao = (UsersDao)applicationContext.getBean("usersDao");
				List<Users> usersList = usersDao.findActiveUsers();
				CampaignsDao campaignsDao = ((CampaignsDao)applicationContext.getBean("campaignsDao"));
				StringBuffer mailContentSb = new StringBuffer();
				String usersText = "Username: ";
				for(Users user:usersList) {
					
					List<Object[]> campaignList = campaignsDao.getDraftStatusCampigns(user.getUserId());
					String campaignNames = Constants.STRING_NILL;
					if(campaignList!=null && campaignList.size()>0) {
						logger.info("campaigns exist "+campaignList.size());
						if(mailContentSb.length()>0) mailContentSb.append("<br/><br/>");
						mailContentSb.append(usersText).append(user.getUserName()).append("<br/>");
							for (Object[] crArr : campaignList) {
								if(!campaignNames.isEmpty()) campaignNames += Constants.DELIMETER_COMMA;
								
								campaignNames += crArr[0].toString();
								
								
							}
							mailContentSb.append("Campaigns: ").append(campaignNames);
					}
					logger.info("No campaigns found with draft status and active schedules : "+user.getUserId());
 				}
				if(mailContentSb.length()>0){
					Utility.sendCampaignDraftAlertMailToSupport(mailContentSb);
				}
				
			} 
			catch (RuntimeException e) {
				logger.error("** RuntimeException **", e);
			}catch(Exception e) {
				logger.error("** Exception **", e);
			}
			
			if(logger.isInfoEnabled()) logger.info("---------- before exiting EmailCampaignDraftStatusCheckTimer.java--------");
			
		}

}