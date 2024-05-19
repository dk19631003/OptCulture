package org.mq.captiway.scheduler;

import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CampaignSchedule;
import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.CampaignScheduleDao;
import org.mq.captiway.scheduler.dao.CampaignScheduleDaoForDML;
import org.mq.captiway.scheduler.dao.CustomFieldDataDao;
import org.mq.captiway.scheduler.dao.EventTriggerDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.Utility;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CampaignScheduler extends TimerTask  implements ApplicationContextAware {


	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private CampaignScheduleDao campaignScheduleDao;
	private CampaignScheduleDaoForDML campaignScheduleDaoForDML;

	public CampaignScheduleDaoForDML getCampaignScheduleDaoForDML() {
		return campaignScheduleDaoForDML;
	}

	public void setCampaignScheduleDaoForDML(CampaignScheduleDaoForDML campaignScheduleDaoForDML) {
		this.campaignScheduleDaoForDML = campaignScheduleDaoForDML;
	}

	public CampaignScheduleDao getCampaignScheduleDao() {
		return campaignScheduleDao;
	}

	public void setCampaignScheduleDao(CampaignScheduleDao campaignScheduleDao) {
		this.campaignScheduleDao = campaignScheduleDao;
	}	
	
	private PMTAQueue pmtaQueue ;
	
	public PMTAQueue getPmtaQueue() {
		return pmtaQueue;
	}
	
	public void setPmtaQueue(PMTAQueue queue) {
		this.pmtaQueue = queue;
	}

	private MailingListDao mailingListDao;
	
	public MailingListDao getMailingListDao() {
		return mailingListDao;
	}

	public void setMailingListDao(MailingListDao mailingListDao) {
		this.mailingListDao = mailingListDao;
	}
	private EventTriggerDao eventTriggerDao; //Event Trigger
	
	public EventTriggerDao getEventTriggerDao() {
		return eventTriggerDao;
	}

	public void setEventTriggerDao(EventTriggerDao eventTriggerDao) {
		this.eventTriggerDao = eventTriggerDao;
	
	}
	
	private CustomFieldDataDao customFieldDataDao;
	
	
	public CustomFieldDataDao getCustomFieldDataDao() {
		return customFieldDataDao;
	}

	public void setCustomFieldDataDao(CustomFieldDataDao customFieldDataDao) {
		this.customFieldDataDao = customFieldDataDao;
	}

	private ApplicationContext applicationContext;
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
//	private PmtaMailmergeSubmitter mailSubmitter;
	
	@Override
	public void run() {
		
		if(logger.isInfoEnabled()) logger.info("------- just entered CampaignScheduler to send active campaigns/triggers etc -------");
		
		MyCalendar myCal = new MyCalendar();
		
		List<CampaignSchedule> activeCampList;
		
		try {
			
			//need to process all the event requests (SendGrid  responses) before any campaign getting processed
			
			try {
				Utility.processExternalSMTPEvents(applicationContext);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception while updating the bounces");
			}
			
		
			
			// Get active Campaigns
			activeCampList = campaignScheduleDao.getActiveList(myCal.toString());
			
			if(activeCampList != null && activeCampList.size() > 0 ){				
				if(logger.isInfoEnabled()) logger.info(">>>> Num of Active campaign schedules found:"+activeCampList.size());
				pmtaQueue.addCollection(activeCampList);
			}
			else {
				if(logger.isInfoEnabled()) logger.info(">>>> No Active campaign schedules found:");
			}
			
			// Get active Double optin lists
			List<MailingList> list = mailingListDao.findConfirmOptInList();
			if(list != null && list.size() > 0){
				if(logger.isInfoEnabled()) logger.info(">>>> No. of Active Mailing Lists found: "+list.size());
				pmtaQueue.addCollection(list);
			}
			else {
				if(logger.isInfoEnabled()) logger.info(">>>> No. of Active Mailing Lists found: 0");
			}
			
			//Update expired or disabled users campaign status
			//campaignScheduleDao.updateDisabledUsersCampaignStatus(myCal.toString());
			campaignScheduleDaoForDML.updateDisabledUsersCampaignStatus(myCal.toString());
			
			
			
			
			
			//Event Trigger begins here
			/*List<EventTrigger> activeEventTriggerList = eventTriggerDao.getActiveTriggerList(); //EventTrigger begin

			if(activeEventTriggerList != null && activeEventTriggerList.size() > 0) {
				if(logger.isInfoEnabled()) logger.info(">>>> Active Triggers found. Adding :"+activeEventTriggerList.size()+" EventTrigger objects to pmtaQueue");
				pmtaQueue.addCollection(activeEventTriggerList);
			}
			else {
				
				if(logger.isInfoEnabled()) logger.info(">>>> No Active Triggers found to process.");
			}*/
			
			if(pmtaQueue.getQueueSize() > 0 ) {
				PmtaMailmergeSubmitter.startPmtaMailmergeSubmitter(applicationContext);
			}
			
/*			if(mailSubmitter == null || !mailSubmitter.isRunning())	{
					if(pmtaQueue.getQueueSize() > 0) {
					 mailSubmitter = new PmtaMailmergeSubmitter(applicationContext);
					 mailSubmitter.start();
					}
			}
*/
			/*if(activeCampList.size() > 0 || list.size() > 0) {
				
				pmtaQueue.addCollection(activeCampList);
				pmtaQueue.addCollection(list);
			
				if(mailSubmitter == null || !mailSubmitter.isRunning()) {
					mailSubmitter = new PmtaMailmergeSubmitter(applicationContext);
					mailSubmitter.start();
				}
			} //commented during Event Trigger*/
			//Event Trigger ends here
			
		} 
		catch (RuntimeException e) {
			logger.error("** Exception : while getting the active list from CampaignSchedule/MailingList/EventTrigger", e);
		}
		
		if(logger.isInfoEnabled()) logger.info("---------- before exiting CampaignScheduler.java--------");
		
	}
	
}
