package org.mq.marketer.campaign.controller.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoEmailClicks;
import org.mq.marketer.campaign.dao.AutoEmailClicksDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.autoEmail.AutoEmailReportBusinessServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class UpdateAutoEmailReports extends TimerTask implements ApplicationContextAware{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private EmailQueueDao emailQueueDao;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	private AutoEmailClicksDao autoEmailClicksDao;
	private ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
		emailQueueDao = (EmailQueueDao)context.getBean("emailQueueDao");
		emailQueueDaoForDML = (EmailQueueDaoForDML)context.getBean("emailQueueDaoForDML");
		autoEmailClicksDao = (AutoEmailClicksDao)context.getBean("autoEmailclicksDao");
	}

	@Override
	public void run() {
		isRunning = true;
		try{
			
			 List<Long> opensList = new ArrayList<Long>();
			 List<AutoEmailClicks> clicksList = new ArrayList<AutoEmailClicks>();
			
			if(AutoEmailReportBusinessServiceImpl.opensQueue.size() > 0){
				opensList.addAll(AutoEmailReportBusinessServiceImpl.opensQueue);
				for(Long eachOpen : opensList){
					//emailQueueDao.updateEmailQueue(Constants.CR_TYPE_OPENS, eachOpen);
					emailQueueDaoForDML.updateEmailQueue(Constants.CR_TYPE_OPENS, eachOpen);
				}
				synchronized (AutoEmailReportBusinessServiceImpl.opensQueue) {
					
					AutoEmailReportBusinessServiceImpl.opensQueue.removeAll(opensList);
				}
			}
			
			if(AutoEmailReportBusinessServiceImpl.clicksQueue.size() > 0){
				clicksList.addAll(AutoEmailReportBusinessServiceImpl.clicksQueue);
				Long eqId = null;
				AutoEmailClicks autoEmailClicks = null;
				for(AutoEmailClicks eachClick : clicksList){
					eqId = eachClick.getEqId();
					//emailQueueDao.updateEmailQueue(Constants.CR_TYPE_CLICKS, eqId);
					emailQueueDaoForDML.updateEmailQueue(Constants.CR_TYPE_CLICKS, eqId);
					autoEmailClicks = autoEmailClicksDao.getClickByUrl(eqId, eachClick.getClickUrl());
					if(autoEmailClicks == null){
						eachClick.setClickCount(1);
						emailQueueDaoForDML.saveOrUpdate(eachClick);
					}else{
						autoEmailClicks.setClickCount(autoEmailClicks.getClickCount()+1);
						emailQueueDaoForDML.saveOrUpdate(autoEmailClicks);
					}
					
				}
				synchronized (AutoEmailReportBusinessServiceImpl.clicksQueue) {
					
					AutoEmailReportBusinessServiceImpl.clicksQueue.removeAll(clicksList);
				}
			}
			
			
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		finally{
			
			isRunning = false;
			
		}
		
	}
	
	private volatile boolean isRunning;
	public boolean isRunning() {
		//logger.debug("isRunning ::"+isRunning);
		return isRunning;
	}
}
