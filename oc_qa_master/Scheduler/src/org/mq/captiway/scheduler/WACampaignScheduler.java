package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.WACampaignsSchedule;
import org.mq.captiway.scheduler.dao.WACampaignsScheduleDao;
import org.mq.captiway.scheduler.dao.WACampaignsScheduleDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.optculture.utils.OCConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class WACampaignScheduler extends TimerTask implements ApplicationContextAware {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private WACampaignsScheduleDao waCampaignsScheduleDao;
	private WACampaignsScheduleDaoForDML waCampaignsScheduleDaoForDML;

	private org.mq.captiway.scheduler.WAQueue waQueue;

	private ApplicationContext applicationContext;

	public WACampaignsScheduleDao getWaCampaignsScheduleDao() {
		return waCampaignsScheduleDao;
	}

	public void setWaCampaignsScheduleDao(WACampaignsScheduleDao waCampaignsScheduleDao) {
		this.waCampaignsScheduleDao = waCampaignsScheduleDao;
	}

	public WACampaignsScheduleDaoForDML getWaCampaignsScheduleDaoForDML() {
		return waCampaignsScheduleDaoForDML;
	}

	public void setWaCampaignsScheduleDaoForDML(WACampaignsScheduleDaoForDML waCampaignsScheduleDaoForDML) {
		this.waCampaignsScheduleDaoForDML = waCampaignsScheduleDaoForDML;
	}

	public org.mq.captiway.scheduler.WAQueue getWaQueue() {
		return waQueue;
	}

	public void setWaQueue(org.mq.captiway.scheduler.WAQueue waQueue) {
		this.waQueue = waQueue;
	}

	private WACampaignSubmitter waCampaignSubmitter;
	public WACampaignSubmitter getWaCampaignSubmitter() {
		return waCampaignSubmitter;
	}

	public void setWaCampaignSubmitter(WACampaignSubmitter waCampaignSubmitter) {
		this.waCampaignSubmitter = waCampaignSubmitter;
	}

	public static Logger getLogger() {
		return logger;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void run(){
		logger.debug(">>>>>>>>>>>>>> Started WACampaignScheduler :: run <<<<<<<<<<<<<< ");
		try{
			MyCalendar myCal = new MyCalendar();
			List<WACampaignsSchedule> inactiveWAList = waCampaignsScheduleDao.getInactiveList(myCal.toString()); //wcs.scheduledDate > user.packageExpiryDate OR usr.enabled = 0

			if(logger.isDebugEnabled()) logger.debug("inactiveWAList="+inactiveWAList);
			if(inactiveWAList.size()>0){
				for (WACampaignsSchedule waCampaignsSchedule : inactiveWAList) {
					waCampaignsSchedule.setStatus((byte) 9);
					waCampaignsScheduleDaoForDML.saveOrUpdate(waCampaignsSchedule);

					// TODO send WhatsApp Campaign Failure Alert Mail to Support
					//Utility.sendCampaignFailureAlertMailToSupport()

				}
			}
			List<WACampaignsSchedule> activeForLongCampList =new ArrayList<WACampaignsSchedule>();

			//Change status of campaigns active for more than 3 hrs
			String hrs=PropertyUtil.getPropertyValueFromDB(OCConstants.NUM_OF_HRS_BEFORE_CAMP_EXPIRES);
			WACampaignsScheduleDao waCampaignsScheduleDao = ((WACampaignsScheduleDao)applicationContext.getBean("waCampaignsScheduleDao"));
			activeForLongCampList=waCampaignsScheduleDao.getActiveForLongCampList(myCal.toString(),Integer.parseInt(hrs));

			if(activeForLongCampList!=null && activeForLongCampList.size()>0){
				for (WACampaignsSchedule waCampaignsSchedule : activeForLongCampList) {
					waCampaignsSchedule.setStatus((byte) 9);
					logger.info("made to expire "+waCampaignsSchedule.getWaCampaignId());
					waCampaignsScheduleDaoForDML.saveOrUpdate(waCampaignsSchedule);

					// TODO send WhatsApp Campaign Failure Alert Mail to Support
					//Utility.sendCampaignFailureAlertMailToSupport()

				}
			}
			List<WACampaignsSchedule> activeWAList = waCampaignsScheduleDao.getActiveList(myCal.toString());

			if(activeWAList.size()>0){
				waQueue.addCollection(activeWAList);
			}
			logger.info("waQueue Size :"+waQueue.getQueueSize());
			waCampaignsScheduleDaoForDML.updateDisabledUsersWACampaignStatus(myCal.toString());

			if(waQueue.getQueueSize() > 0) {

				WACampaignSubmitter.startWACampaignSubmitter(applicationContext);


			}

		}catch(Exception e){
			if(logger.isErrorEnabled()) logger.error("Exception while getting the WA campaign Schedule objects ::::" , e);
		}
		logger.debug(">>>>>>> Completed WACampaignScheduler :: run <<<<<<< ");
	}


}
