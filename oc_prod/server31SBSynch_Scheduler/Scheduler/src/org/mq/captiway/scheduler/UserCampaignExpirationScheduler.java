package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.UserCampaignExpiration;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.UserCampaignExpirationDao;
import org.mq.captiway.scheduler.dao.UserCampaignExpirationDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.services.UserAlertSenderThread;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class UserCampaignExpirationScheduler extends TimerTask{

	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	
	@Override
	public void run() {
		try {
			logger.debug(" ===UserCampaignExpirationScheduler is started running=== ");
			ServiceLocator locator = ServiceLocator.getInstance();

			UserCampaignExpirationDao userCampaignExpirationDao = null;
			UserCampaignExpirationDaoForDML userCampaignExpirationDaoForDML = null;

			UsersDao usersDao = null;
			//CampaignsDao campaignsDao = null;
			
			try {
				
				userCampaignExpirationDao = (UserCampaignExpirationDao)locator.getDAOByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO);
				userCampaignExpirationDaoForDML = (UserCampaignExpirationDaoForDML)locator.getDAOForDMLByName(OCConstants.USER_CAMPAIGN_EXPIRATION_DAO_FOR_DML);

				usersDao = (UsersDao)locator.getDAOByName(OCConstants.USERS_DAO);
				//campaignsDao = (CampaignsDao)locator.getDAOByName(OCConstants.CAMPAIGNS_DAO);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception while fetching the Dao object", e);
				
			}
			 
			List<Long> userIdsList = null;
			
			try {
				
				userIdsList = userCampaignExpirationDao.findUsers();
				
			} catch (BaseDAOException e) {
				// TODO Auto-generated catch block
				logger.error("got Exception while fetching the users ", e);
			}
			
			if(userIdsList == null || userIdsList.size() == 0) return;
			
			for (Long userId : userIdsList) {
				//if(userId.longValue() != 16) continue;
				logger.debug(" ===UserCampaignExpirationScheduler in for=== "+userId.longValue());
				List<UserCampaignExpiration> userExpireCampaigns = userCampaignExpirationDao.findBy(userId);
				List<UserCampaignExpiration> updateUserExpireCampaigns = new ArrayList<UserCampaignExpiration>(); 
				
				if(userExpireCampaigns == null || userExpireCampaigns.size() == 0) continue;
				logger.debug(" ===UserCampaignExpirationScheduler in size=== "+userExpireCampaigns.size());
				for (UserCampaignExpiration userCampaignExpiration : userExpireCampaigns) {
					
					Calendar expiredOn = userCampaignExpiration.getSentOn();
					
					Calendar now = Calendar.getInstance();
					
					if(now.equals(expiredOn) || now.after(expiredOn)) {
						logger.debug(" ===UserCampaignExpirationScheduler matched=== ");
						userCampaignExpiration.setStatus(OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_SENT);
						userCampaignExpiration.setModifiedDate(now);
						updateUserExpireCampaigns.add(userCampaignExpiration);
						UserAlertSenderThread senderThread = new UserAlertSenderThread(userCampaignExpiration);
						senderThread.start();
						
						
					}//if
					
				} //for
				logger.debug(" ===UserCampaignExpirationScheduler saving=== ");
			//	if(updateUserExpireCampaigns.size() > 0) userCampaignExpirationDao.saveByCollection(updateUserExpireCampaigns);
				if(updateUserExpireCampaigns.size() > 0) userCampaignExpirationDaoForDML.saveByCollection(updateUserExpireCampaigns);

			}//for
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception ", e);
		}
		
	}
	
}
