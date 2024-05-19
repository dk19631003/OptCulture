package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CampReportLists;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.beans.CampaignSchedule;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignSchedule;
import org.mq.captiway.scheduler.beans.SMSCampaigns;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.CampReportListsDao;
import org.mq.captiway.scheduler.dao.CampaignReportDao;
import org.mq.captiway.scheduler.dao.CampaignScheduleDao;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignScheduleDao;
import org.mq.captiway.scheduler.dao.SMSCampaignsDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.scheduling.annotation.Scheduled;

public class WeeklyReportSnapshot
{
	  private static final Logger logger = LogManager.getLogger("scheduler");
	  private CampaignReportDao campaignReportDao;
	  private CampaignsDao campaignsDao;
	  private CampaignScheduleDao campaignScheduleDao;
	  private SMSCampaignScheduleDao smsCampaignScheduleDao;
	  private SMSCampaignsDao smsCampaignsDao;
	  private SMSCampaignReportDao smsCampaignReportDao;
	  private UsersDao usersDao;
	  private EmailQueueDao emailQueueDao;
	  private EmailQueueDaoForDML emailQueueDaoForDML;
	  private String rowStart = "<tr>";
	  private String rowStart1 = "<tr bgcolor= #f6f5f5>";
	  private String rowEnd = "</tr>";
	  private String colStart = "<td style=\"border:1px solid black;padding: 0px 10px;font-family:Arial;font-size: 14 ;line-height: 150%;word-spacing: normal;\">";
	  private String ColEnd = "</td>";
	  
	 // @SuppressWarnings("deprecation")
	//@Scheduled(cron="0 0,30 * * * *")
	  public void run_task()
	  {
		logger.info("Entered into weeklyReportSnapShot");
	    try
	    {
	    	ServiceLocator locator = ServiceLocator.getInstance();
	    	this.campaignReportDao = ((CampaignReportDao)locator.getDAOByName("campaignReportDao"));
	    	this.campaignsDao = ((CampaignsDao)locator.getDAOByName("campaignsDao"));
	    	this.campaignScheduleDao = ((CampaignScheduleDao)locator.getDAOByName("campaignScheduleDao"));
	    	this.smsCampaignReportDao = ((SMSCampaignReportDao)locator.getDAOByName("smsCampaignReportDao"));
	    	this.smsCampaignsDao = ((SMSCampaignsDao)locator.getDAOByName("smsCampaignsDao"));
	    	this.smsCampaignScheduleDao = ((SMSCampaignScheduleDao)locator.getDAOByName("smsCampaignScheduleDao"));
	    	this.usersDao = ((UsersDao)locator.getDAOByName("usersDao"));
	    	this.emailQueueDao = ((EmailQueueDao)locator.getDAOByName("emailQueueDao"));
	    	this.emailQueueDaoForDML = ((EmailQueueDaoForDML)locator.getDAOForDMLByName("emailQueueDaoForDML"));

	    	String subject = PropertyUtil.getPropertyValueFromDB("weeklyReportSubject");
	    	String message = PropertyUtil.getPropertyValueFromDB("weeklyReportMailContent");

	    	String weeklySuccessCampaignsTemp = PropertyUtil.getPropertyValueFromDB("weeklyReportSuccessTable");
	    	String weeklyFailedCampaignsTemp = PropertyUtil.getPropertyValueFromDB("weeklyReportFailedTable");

	    	String type = "Weekly Campaign Report";
	    	String status = "Active";

	    	int serverTimeZoneValInt = Integer.parseInt(PropertyUtil.getPropertyValueFromDB("ServerTimeZoneValue"));
	    	int timezoneDiffrenceMinutesInt = 0;

	    	List<Users> users = this.usersDao.getAllUsers();
	    	for (Users user : users)
	    	{
	    		String timezoneDiffrenceMinutes = user.getClientTimeZone();
	    		if (timezoneDiffrenceMinutes != null) {
	    			timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
	    		}
	    		timezoneDiffrenceMinutesInt -= serverTimeZoneValInt;


	    		Calendar currCal = Calendar.getInstance();
	    		currCal.add(12, timezoneDiffrenceMinutesInt);

	    		Date today = currCal.getTime();

	    		int day = today.getDay();
	    		int hours = today.getHours();
	    		int mins = today.getMinutes();

	    		Date sendingTime = user.getWeeklyReportTime();

	    		boolean sendMail = false;
	    		if (sendingTime != null)
	    		{
	    			int sendingDay = user.getWeeklyReportDay();
	    			int sendingHours = sendingTime.getHours();
	    			int sendingMins = sendingTime.getMinutes();
	    			if (sendingDay == day)
	    			{
	    				boolean isMailAlreadySentToday = this.emailQueueDao.isMailSentToday(type, user.getUserId(), timezoneDiffrenceMinutesInt);
	    				if (sendingHours == hours)
	    				{
	    					if ((sendingMins <= mins) && 
	    							(!isMailAlreadySentToday)) {
	    						sendMail = true;
	    					}
	    				}
	    				else if (sendingHours < hours) {
	    					if (!isMailAlreadySentToday) {
	    						sendMail = true;
	    					}
	    				}
	    			}
	    			if (sendMail)
	    			{
	    				Calendar from = Calendar.getInstance();
	    				from.add(12, timezoneDiffrenceMinutesInt);
	    				from.add(5, -7);
	    				from.set(11, 0);
	    				from.set(12, 0);
	    				from.set(13, 0);

	    				Calendar till = Calendar.getInstance();
	    				till.add(12, timezoneDiffrenceMinutesInt);
	    				till.add(5, -1);
	    				till.set(11, 23);
	    				till.set(12, 59);
	    				till.set(13, 59);

	    				String fromtoDate = "";

	    				fromtoDate = MyCalendar.calendarToString(from, "dd MMM, yyyy") + " to " + MyCalendar.calendarToString(till, "dd MMM, yyyy");
	    				from.add(12, -timezoneDiffrenceMinutesInt);
	    				till.add(12, -timezoneDiffrenceMinutesInt);

	    				List<CampaignSchedule> campaignSchedulesLastWeek = this.campaignScheduleDao.getCampaignSentLastWeek(MyCalendar.calendarToString(from, "yyyy-MM-dd HH:mm:ss"), MyCalendar.calendarToString(till, "yyyy-MM-dd HH:mm:ss"), user.getUserId());
	    				List<SMSCampaignSchedule> smsCampaignSchedulesLastWeek = this.smsCampaignScheduleDao.getSMSCampaignSentLastWeek(MyCalendar.calendarToString(from, "yyyy-MM-dd HH:mm:ss"), MyCalendar.calendarToString(till, "yyyy-MM-dd HH:mm:ss"), user.getUserId());
	    				if ((campaignSchedulesLastWeek != null) || (smsCampaignSchedulesLastWeek != null))
	    				{
	    					String campaignSentIds = null;
	    					String smsCampaignSentIds = null;
	    					String emailSubject = null;
	    					String emailContent = "";

	    					List<CampaignSchedule> failedCampaignSchedules = new ArrayList();
	    					List<SMSCampaignSchedule> failedSMSCampaignSchedules = new ArrayList();
	    					
	    					if(campaignSchedulesLastWeek != null && user.isWeeklyReportTypeEmail()){
	    						for (CampaignSchedule campaignSchedule : campaignSchedulesLastWeek) {
	    							if (campaignSchedule.getStatus() == 1)
	    							{
	    								if (campaignSchedule.getCrId() != null) {
	    									if (campaignSentIds == null) {
	    										campaignSentIds = campaignSchedule.getCrId().toString();
	    									} else {
	    										campaignSentIds = campaignSentIds + "," + campaignSchedule.getCrId();
	    									}
	    								}
	    							}
	    							else if ((campaignSchedule.getStatus() != 0) && (campaignSchedule.getStatus() != 1) && (campaignSchedule.getStatus() != 2)) {
	    								failedCampaignSchedules.add(campaignSchedule);
	    							}
	    						}
	    					}
	    					
	    					if(smsCampaignSchedulesLastWeek != null && user.isWeeklyReportTypeSMS()){
	    						for (SMSCampaignSchedule smsCampaignSchedule : smsCampaignSchedulesLastWeek) {
	    							if (smsCampaignSchedule.getStatus() == 1)
	    							{
	    								if (smsCampaignSchedule.getCrId() != null) {
	    									if (smsCampaignSentIds == null) {
	    										smsCampaignSentIds = smsCampaignSchedule.getCrId().toString();
	    									} else {
	    										smsCampaignSentIds = smsCampaignSentIds + "," + smsCampaignSchedule.getCrId();
	    									}
	    								}
	    							}
	    							else if ((smsCampaignSchedule.getStatus() != 0) && (smsCampaignSchedule.getStatus() != 1) && (smsCampaignSchedule.getStatus() != 2)) {
	    								failedSMSCampaignSchedules.add(smsCampaignSchedule);
	    							}
	    						}
	    					}
	    					
	    					String sentCampMessageContent = "";
	    					String failedCampMessageContent = "";
	    					String smsSentCampMessageContent = "";
	    					String failedsmsCampMessageContent = "";
	    					if ((smsCampaignSentIds != null) || (campaignSentIds != null))
	    					{
	    						List<CampaignReport> campReportList = this.campaignReportDao.getWeeklyCampaignReports(user.getUserId(), campaignSentIds);
	    						List<SMSCampaignReport> smsCampReportList = this.smsCampaignReportDao.getWeeklySMSCampaignReports(user.getUserId(), smsCampaignSentIds);
	    						
	    						sentCampMessageContent = prepareSentCampaignData(campReportList, smsCampReportList, user.getUserId(), timezoneDiffrenceMinutesInt);
	    						
	    						if ((!sentCampMessageContent.isEmpty()) && (sentCampMessageContent.length() > 0)) {
	    							emailContent = emailContent + weeklySuccessCampaignsTemp.replace("<sent_files_data>", sentCampMessageContent);
	    						}
	    					}
	    					logger.info("----size of failedCampaignSchedules is-----"+ failedCampaignSchedules.size());
	    					logger.info("----size of failedSMSCampaignSchedules is-----"+ failedSMSCampaignSchedules.size());
	    					if ((failedCampaignSchedules.size() > 0) || (failedSMSCampaignSchedules.size() > 0))
	    					{
	    						failedCampMessageContent = prepareFailedCampaignData(failedCampaignSchedules, failedSMSCampaignSchedules, timezoneDiffrenceMinutesInt);
	    						emailContent = emailContent + weeklyFailedCampaignsTemp.replace("<failed_files_data>", failedCampMessageContent);
	    					}
	    					/*if(smsCampaignSentIds != null){
	    						logger.info("-----smsCampaignSentIds not null-------");
	    						List<SMSCampaignReport> smsCampReportList = this.smsCampaignReportDao.getWeeklySMSCampaignReports(user.getUserId(), smsCampaignSentIds);
	    						smsSentCampMessageContent = prepareSentSMSCampaignData(smsCampReportList, user.getUserId(), timezoneDiffrenceMinutesInt);
	    						if ((!smsSentCampMessageContent.isEmpty()) && (smsSentCampMessageContent.length() > 0)) {
	    							emailContent = emailContent + weeklySuccessCampaignsTemp.replace("<sent_files_data>", smsSentCampMessageContent);
	    						}
	    					}
	    					if (failedSMSCampaignSchedules.size() > 0)
	    					{
	    						failedsmsCampMessageContent = prepareFailedSMSCampaignData(failedSMSCampaignSchedules, timezoneDiffrenceMinutesInt);
	    						emailContent = emailContent + weeklyFailedCampaignsTemp.replace("<failed_files_data>", failedsmsCampMessageContent);
	    					}*/
	    					
	    					if ((!emailContent.isEmpty()) && (emailContent.length() > 0))
	    					{
	    						logger.info("-----emailContent not null--------");
	    						emailSubject = subject.replace("<fromtodate>", fromtoDate);
	    						String userName = Utility.getOnlyUserName(user.getUserName());

	    						String emailMessage = message.replace("<user_first_name>", user.getFirstName().trim())
	    								.replace("<user_name>", userName)
	    								.replace("<OrganizationName>", user.getUserOrganization().getOrganizationName())
	    								.replace("<campaign_snapshot_tables>", emailContent);

	    						EmailQueue alertEmailQueue = new EmailQueue(emailSubject, emailMessage, type, status, user.getWeeklyReportEmailId(), new Date(), user);
	    						//this.emailQueueDao.saveOrUpdate(alertEmailQueue);
	    						this.emailQueueDaoForDML.saveOrUpdate(alertEmailQueue);
	    					}
	    				}
	    			}
	    		}
	    	}
	    }
	    catch (Exception e)
	    {
	      logger.error("Exception", e);
	    }
	    logger.debug("exit run of weekly report");
	  }
	  
	  public String prepareSentCampaignData(List<CampaignReport> campReportList, List<SMSCampaignReport> smsCampReportList, Long userId, int timezoneDiffrenceMinutesInt)
	  {
	    String messageContent = "";
	    int count = 1;
	    if(campReportList != null && campReportList.size() > 0){
	    	for (CampaignReport campaignReport : campReportList)
	    	{
	    		if (count % 2 == 0) {
	    			messageContent = messageContent + this.rowStart1;
	    		} else {
	    			messageContent = messageContent + this.rowStart;
	    		}
	    		messageContent = messageContent + this.colStart + campaignReport.getCampaignName() + this.ColEnd;

	    		messageContent = messageContent + this.colStart + "Email" + this.ColEnd;

	    		Calendar sentDate = campaignReport.getSentDate();
	    		sentDate.add(12, timezoneDiffrenceMinutesInt);

	    		messageContent = messageContent + this.colStart + MyCalendar.calendarToString(sentDate, "dd MMM, yyyy HH:mm aaa") + this.ColEnd;


	    		//   messageContent = messageContent + this.colStart + getCampRepLists(campaignReport.getCrId()) + this.ColEnd;


	    		long sent = campaignReport.getSent();

	    		messageContent = messageContent + this.colStart + (sent - campaignReport.getBounces()) + this.ColEnd;

	    		messageContent = messageContent + this.colStart + campaignReport.getBounces() + this.ColEnd;
	    		messageContent = messageContent + this.colStart + campaignReport.getOpens() + this.ColEnd;
	    		messageContent = messageContent + this.colStart + campaignReport.getClicks() + this.ColEnd;

	    		messageContent = messageContent + this.rowEnd;
	    		count++;
	    	}
	    }
	    
	    if(smsCampReportList != null && smsCampReportList.size() > 0){
	    	for (SMSCampaignReport campaignReport : smsCampReportList)
	    	{
	    		if (count % 2 == 0) {
	    			messageContent = messageContent + this.rowStart1;
	    		} else {
	    			messageContent = messageContent + this.rowStart;
	    		}
	    		messageContent = messageContent + this.colStart + campaignReport.getSmsCampaignName() + this.ColEnd;

	    		messageContent = messageContent + this.colStart + "SMS" + this.ColEnd;

	    		Calendar sentDate = campaignReport.getSentDate();
	    		sentDate.add(12, timezoneDiffrenceMinutesInt);

	    		messageContent = messageContent + this.colStart + MyCalendar.calendarToString(sentDate, "dd MMM, yyyy HH:mm aaa") + this.ColEnd;

	    		//     messageContent = messageContent + this.colStart + getCampRepLists(campaignReport.getSmsCrId()) + this.ColEnd;

	    		long sent = campaignReport.getSent();

	    		messageContent = messageContent + this.colStart + (sent - campaignReport.getBounces()) + this.ColEnd;

	    		messageContent = messageContent + this.colStart + campaignReport.getBounces() + this.ColEnd;
	    		messageContent = messageContent + this.colStart + "--" + this.ColEnd;
	    		messageContent = messageContent + this.colStart + campaignReport.getClicks() + this.ColEnd;

	    		messageContent = messageContent + this.rowEnd;
	    		count++;
	    	}
	    }
	    
	    return messageContent;
	  }
	  
	 /* public String prepareSentSMSCampaignData(List<SMSCampaignReport> smsCampReportList, Long userId, int timezoneDiffrenceMinutesInt)
	  {
	    String messageContent = "";
	    int count = 1;
	    for (SMSCampaignReport campaignReport : smsCampReportList)
	    {
	      if (count % 2 == 0) {
	        messageContent = messageContent + this.rowStart1;
	      } else {
	        messageContent = messageContent + this.rowStart;
	      }
	      messageContent = messageContent + this.colStart + campaignReport.getSmsCampaignName() + this.ColEnd;
	      
	      messageContent = messageContent + this.colStart + "SMS" + this.ColEnd;
	      
	      Calendar sentDate = campaignReport.getSentDate();
	      sentDate.add(12, timezoneDiffrenceMinutesInt);

	      messageContent = messageContent + this.colStart + MyCalendar.calendarToString(sentDate, "dd MMM, yyyy HH:mm aaa") + this.ColEnd;
	      

	 //     messageContent = messageContent + this.colStart + getCampRepLists(campaignReport.getSmsCrId()) + this.ColEnd;
	      

	      long sent = campaignReport.getSent();
	      
	      messageContent = messageContent + this.colStart + (sent - campaignReport.getBounces()) + this.ColEnd;
	      
	      messageContent = messageContent + this.colStart + campaignReport.getBounces() + this.ColEnd;
	      messageContent = messageContent + this.colStart + "--" + this.ColEnd;
	      messageContent = messageContent + this.colStart + campaignReport.getClicks() + this.ColEnd;
	      
	      messageContent = messageContent + this.rowEnd;
	      count++;
	    }
	    return messageContent;
	  }*/
	  
	  public String prepareFailedCampaignData(List<CampaignSchedule> campaignScheduleList, List<SMSCampaignSchedule> smsCampaignScheduleList, int timezoneDiffrenceMinutesInt)
	  {
	    String messageContent = "";
	    int count = 1;
	    
	    if(campaignScheduleList != null && campaignScheduleList.size() > 0){
	    	for (CampaignSchedule campaignSchedule : campaignScheduleList)
	    	{
	    		if (count % 2 == 0) {
	    			messageContent = messageContent + this.rowStart1;
	    		} else {
	    			messageContent = messageContent + this.rowStart;
	    		}
	    		Campaigns campaignNew = this.campaignsDao.findByCampaignId(campaignSchedule.getCampaignId());

	    		messageContent = messageContent + this.colStart + campaignNew.getCampaignName() + this.ColEnd;

	    		messageContent = messageContent + this.colStart + "Email" + this.ColEnd;

	    		Calendar scheduledDate = campaignSchedule.getScheduledDate();
	    		scheduledDate.add(12, timezoneDiffrenceMinutesInt);

	    		messageContent = messageContent + this.colStart + MyCalendar.calendarToString(scheduledDate, "dd MMM, yyyy HH:mm aaa") + this.ColEnd;

	    		String campstatus = campaignSchedule.getStatusStr();

	    		messageContent = messageContent + this.colStart + campstatus + this.ColEnd;

	    		messageContent = messageContent + this.rowEnd;
	    		count++;
	    	}
	    }
	    
	    if(smsCampaignScheduleList != null && smsCampaignScheduleList.size() > 0){
	    	for (SMSCampaignSchedule campaignSchedule : smsCampaignScheduleList)
	    	{
	    		if (count % 2 == 0) {
	    			messageContent = messageContent + this.rowStart1;
	    		} else {
	    			messageContent = messageContent + this.rowStart;
	    		}
	    		SMSCampaigns smsCampaignNew = this.smsCampaignsDao.findByCampaignId(campaignSchedule.getSmsCampaignId());

	    		messageContent = messageContent + this.colStart + smsCampaignNew.getSmsCampaignName() + this.ColEnd;

	    		messageContent = messageContent + this.colStart + "SMS" + this.ColEnd;

	    		Calendar scheduledDate = campaignSchedule.getScheduledDate();
	    		scheduledDate.add(12, timezoneDiffrenceMinutesInt);

	    		messageContent = messageContent + this.colStart + MyCalendar.calendarToString(scheduledDate, "dd MMM, yyyy HH:mm aaa") + this.ColEnd;

	    		String campstatus = campaignSchedule.getStatusStr();

	    		messageContent = messageContent + this.colStart + campstatus + this.ColEnd;

	    		messageContent = messageContent + this.rowEnd;
	    		count++;
	    	}
	    }
	    
	    return messageContent;
	  }
	  
	 /* public String prepareFailedSMSCampaignData(List<SMSCampaignSchedule> campaignScheduleList, int timezoneDiffrenceMinutesInt)
	  {
	    String messageContent = "";
	    int count = 1;
	    for (SMSCampaignSchedule campaignSchedule : campaignScheduleList)
	    {
	      if (count % 2 == 0) {
	        messageContent = messageContent + this.rowStart1;
	      } else {
	        messageContent = messageContent + this.rowStart;
	      }
	      SMSCampaigns smsCampaignNew = this.smsCampaignsDao.findByCampaignId(campaignSchedule.getSmsCampaignId());
	      
	      messageContent = messageContent + this.colStart + smsCampaignNew.getSmsCampaignName() + this.ColEnd;
	      
	      messageContent = messageContent + this.colStart + "SMS" + this.ColEnd;
	      
	      Calendar scheduledDate = campaignSchedule.getScheduledDate();
	      scheduledDate.add(12, timezoneDiffrenceMinutesInt);
	      
	      messageContent = messageContent + this.colStart + MyCalendar.calendarToString(scheduledDate, "dd MMM, yyyy HH:mm aaa") + this.ColEnd;
	      
	      String campstatus = campaignSchedule.getStatusStr();
	      
	      messageContent = messageContent + this.colStart + campstatus + this.ColEnd;
	      
	      messageContent = messageContent + this.rowEnd;
	      count++;
	    }
	    return messageContent;
	  }*/
	  
	  private String getCampRepLists(Long campRepId)
	  {
	    String listsName = "";
	    
	    ServiceLocator locator = ServiceLocator.getInstance();
	    try
	    {
	      CampReportListsDao campReportListsDao = (CampReportListsDao)locator.getDAOByName("campReportListsDao");
	      

	      CampReportLists tempCampRepLists = campReportListsDao.findByCampReportId(campRepId);
	      if (tempCampRepLists != null) {
	        listsName = tempCampRepLists.getListsName();
	      }
	    }
	    catch (Exception e)
	    {
	      logger.error("Exception", e);
	    }
	    return listsName;
	  }
}
