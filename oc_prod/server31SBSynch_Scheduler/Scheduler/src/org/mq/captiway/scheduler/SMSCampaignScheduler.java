package org.mq.captiway.scheduler;

import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SMSCampaignSchedule;
import org.mq.captiway.scheduler.beans.SMSDeliveryReport;
import org.mq.captiway.scheduler.dao.SMSCampaignScheduleDao;
import org.mq.captiway.scheduler.dao.SMSCampaignScheduleDaoForDML;
import org.mq.captiway.scheduler.dao.SMSDeliveryReportDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SMSCampaignScheduler extends TimerTask implements ApplicationContextAware {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private SMSCampaignScheduleDao smsCampaignScheduleDao;
	private SMSCampaignScheduleDaoForDML smsCampaignScheduleDaoForDML;
	
	private org.mq.captiway.scheduler.SMSQueue smsQueue;
	
	private SMSDeliveryReportDao smsDeliveryReportDao;
	private ApplicationContext applicationContext;
	
	public SMSDeliveryReportDao getSmsDeliveryReportDao() {
		return smsDeliveryReportDao;
	}

	public void setSmsDeliveryReportDao(SMSDeliveryReportDao smsDeliveryReportDao) {
		this.smsDeliveryReportDao = smsDeliveryReportDao;
	}

	public SMSCampaignScheduleDao getSmsCampaignScheduleDao() {
		return smsCampaignScheduleDao;
	}

	public void setSmsCampaignScheduleDao(
			SMSCampaignScheduleDao smsCampaignScheduleDao) {
		this.smsCampaignScheduleDao = smsCampaignScheduleDao;
	}
	
	public SMSCampaignScheduleDaoForDML getSmsCampaignScheduleDaoForDML() {
		return smsCampaignScheduleDaoForDML;
	}

	public void setSmsCampaignScheduleDaoForDML(
			SMSCampaignScheduleDaoForDML smsCampaignScheduleDaoForDML) {
		this.smsCampaignScheduleDaoForDML = smsCampaignScheduleDaoForDML;
	}

	public SMSQueue getSmsQueue() {
		return smsQueue;
	}

	public void setSmsQueue(SMSQueue smsQueue) {
		this.smsQueue = smsQueue;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	
	
	private SMSCampaignSubmitter smsCampaignSubmitter;
	
	@Override
	public void run(){
		logger.debug(">>>>>>> Started SMSCampaignScheduler :: run <<<<<<< ");
		if(logger.isDebugEnabled()) logger.debug("-------just entered to send Active SMS Campaigns------");
		try{
			MyCalendar myCal = new MyCalendar();
			//Step 1: Get Active Schedules
			List<SMSCampaignSchedule> smsList = smsCampaignScheduleDao.getActiveList(myCal.toString());
			//List<SMSDeliveryReport> smsDlrList = smsDeliveryReportDao.getActiveList(""+(myCal.toString()));
			if(logger.isDebugEnabled()) logger.debug("smsList="+smsList);
			//if(logger.isDebugEnabled()) logger.debug("smsDlrList="+smsDlrList);
			//Step 2: If Active Schedules are greater then zero add to Sms Queue
			if(smsList.size()>0){
				smsQueue.addCollection(smsList);
			}
			/*if(smsDlrList.size()>0) {
				smsQueue.addCollection(smsDlrList);
			}*/
			
			//Step 3:Update expired or disabled users campaign status
			//smsCampaignScheduleDao.updateDisabledUsersSMSCampaignStatus(myCal.toString());
			smsCampaignScheduleDaoForDML.updateDisabledUsersSMSCampaignStatus(myCal.toString());
			
			if(smsQueue.getQueueSize() > 0) {
				
				SMSCampaignSubmitter.startSMSCampaignSubmitter(applicationContext);
				
				
			}
			
			/*if(smsQueue.getQueueSize()>0){
				
				if(smsCampaignSubmitter == null || !smsCampaignSubmitter.isRunning()){
					smsCampaignSubmitter = new SMSCampaignSubmitter(applicationContext);
					smsCampaignSubmitter.start();
				}
			}*/
		}catch(Exception e){
			if(logger.isErrorEnabled()) logger.error("**Exception while getting the SMS campaign Schedule objects",e);
			logger.error("Exception ::::" , e);
		}
		logger.debug(">>>>>>> Completed SMSCampaignScheduler :: run <<<<<<< ");
	}
	
	
}
