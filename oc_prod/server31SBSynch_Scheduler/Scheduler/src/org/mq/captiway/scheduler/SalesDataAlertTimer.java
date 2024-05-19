package org.mq.captiway.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.UserPosFTPSettings;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.POSFileLogDao;
import org.mq.captiway.scheduler.dao.UserPosFTPSettingsDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;


public class SalesDataAlertTimer extends TimerTask{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	public SalesDataAlertTimer(){
		// TODO Auto-generated constructor stub
	}

	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	private UserPosFTPSettingsDao userPosFtpSettingsdao = null;
	private POSFileLogDao posFileLogDao = null;
	private EmailQueueDao emailQueueDao = null;
	private EmailQueueDaoForDML emailQueueDaoForDML = null;
	private UsersDao usersDao = null;
	//	UserPosFTPSettings userPosFTPSettings = new UserPosFTPSettings();


	public void run(){

		logger.debug("inside run of sales data alert");
		try {

			ServiceLocator locator = ServiceLocator.getInstance();
			userPosFtpSettingsdao = (UserPosFTPSettingsDao)locator.getDAOByName("userPosFTPSettingsDao");
			posFileLogDao = (POSFileLogDao)locator.getDAOByName("posFileLogDao");
			usersDao = (UsersDao)locator.getDAOByName("usersDao");
			emailQueueDao = (EmailQueueDao)locator.getDAOByName("emailQueueDao");
			emailQueueDaoForDML = (EmailQueueDaoForDML)locator.getDAOForDMLByName("emailQueueDaoForDML");

			String type = Constants.EQ_TYPE_SALES_DATA_NOT_RECEIVED_ALERT;
			String status = Constants.MAIL_SENT_STATUS_ACTIVE;
			String message = PropertyUtil.getPropertyValueFromDB("salesDataNotReceivedMessageTemplate");
			String subject = PropertyUtil.getPropertyValueFromDB("salesDataNotReceivedSubject");
			String supportMailId = PropertyUtil.getPropertyValueFromDB("AlertToEmailId");
			String bccEmailId = null;

			// getting ftp setting fro users enabled with sales data alert

			List<UserPosFTPSettings> userPosFTPSettings = userPosFtpSettingsdao.getFtpSettingsforSalesDataAlertMail();

			logger.info(" got number of users for sales data alert" +userPosFTPSettings.size());

			for(UserPosFTPSettings userSetting : userPosFTPSettings){
				
				if(userSetting.isCheckAlert()) {

						long userId = userSetting.getUserId();
		
		
						Calendar lastFetchedTime = posFileLogDao.getLastFetchedTime(userId);
		
						//logger.debug("last fetched time for user " + userSetting.getUserId() + " is " + lastFetchedTime.toString());
		
						if(lastFetchedTime != null) {
		
							int processCheckPeroid = userSetting.getCheckProcessPeriod();
		
							if(processCheckPeroid >=1 && processCheckPeroid <=7) {
		
								Calendar todayDate = Calendar.getInstance();
		
								long numOfDays = (todayDate.getTimeInMillis() - lastFetchedTime.getTimeInMillis())/(60*60*24*1000);
								
								// need to set getCheckProcessPeriod in db as its 
		
								lastFetchedTime.add(Calendar.DAY_OF_MONTH, processCheckPeroid);
		
								if(todayDate.after(lastFetchedTime)){
									
									Date latMailSent = emailQueueDao.getLastSentDate(type, userId);
									boolean isMailSent = true;
									
									if(latMailSent != null) {
										
										Calendar lastMailSentDate = Calendar.getInstance();
										logger.info("before mail sent " + latMailSent);
										
										lastMailSentDate.setTime(latMailSent);
										
										
										if(lastFetchedTime.after(lastMailSentDate))
											isMailSent = false;
										
										else {
										
											lastMailSentDate.add(Calendar.DAY_OF_MONTH, 7);
											if(lastMailSentDate.before(todayDate))	isMailSent = false;
										
										}
									}
									else {
										
										logger.info("last mail sent date is null");
										isMailSent = false;
									}
									
									if(!isMailSent) {
										// send a mail
										String toEmailIds = userSetting.getAlertEmailAddress();
										String toEmailIdStr = null;
		
										if(!toEmailIds.isEmpty()) {
											Users user = usersDao.findByUserId(userId);
		
											if(user != null) {
		
												/*if( (!toEmailIds.isEmpty()) && toEmailIds.contains(Constants.ADDR_COL_DELIMETER)) {
		
													String mailIdsArray[] = toEmailIds.split(Constants.ADDR_COL_DELIMETER);
		
													for(String mailId : mailIdsArray) {
		
														if(supportMailId.equalsIgnoreCase(mailId)) 
															bccEmailId = mailId;
														else {
															if(toEmailIdStr == null ) toEmailIdStr = mailId;
															else toEmailIdStr += Constants.ADDR_COL_DELIMETER + mailId;
														}
													}
		
												}
												else*/
													toEmailIdStr = toEmailIds;
		
		
		
												String userName = Utility.getOnlyUserName(user.getUserName());
		
												String emailSubject = subject.replace(Constants.SALES_DATA_NOT_RECEIVED_ALERT_PH_USER_NAME, userName);
												
		
												String emailMessage  = message.replace(Constants.SALES_DATA_NOT_RECEIVED_ALERT_PH_USER_NAME, userName)
														.replace(Constants.SALES_DATA_NOT_RECEIVED_ALERT_PH_ORG_ID, user.getUserOrganization().getOrgExternalId().toString())
														.replace(Constants.SALES_DATA_NOT_RECEIVED_ALERT_PH_NUMBER_OF_DAYS, numOfDays+"");
		
												EmailQueue alertEmailQueue = new EmailQueue(emailSubject, emailMessage, type, status, toEmailIdStr, bccEmailId, new Date(), user);
												//emailQueueDao.saveOrUpdate(alertEmailQueue);
												emailQueueDaoForDML.saveOrUpdate(alertEmailQueue);
		
												logger.info("sales alert mail is saved on email queue");
											}
											
										}
										
									}
									
								}
							}
							
						}
						
					}
		
				}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			
				logger.error("Exception :: ",e);
		}
		logger.debug("exit run of sales data alert");

	}

}
