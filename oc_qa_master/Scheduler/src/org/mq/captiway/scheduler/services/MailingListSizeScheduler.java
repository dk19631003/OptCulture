package org.mq.captiway.scheduler.services;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CampaignSchedule;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.MailingListDaoForDML;
import org.mq.captiway.scheduler.dao.PurgeDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.utils.OCConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MailingListSizeScheduler extends TimerTask implements ApplicationContextAware {
	private static final Logger logger =  LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private ApplicationContext applicationContext;
	private boolean isRunning;
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	private MailingListDao mailingListDao;
	MailingListDaoForDML mailingListDaoForDML;
	public MailingListDao getMailingListDao() {
		return mailingListDao;
	}
	public void setMailingListDao(MailingListDao mailingListDao) {
		this.mailingListDao = mailingListDao;
	}
	public MailingListDaoForDML getMailingListDaoForDML() {
		return mailingListDaoForDML;
	}
	public void setMailingListDaoForDML(MailingListDaoForDML mailingListDaoForDML) {
		this.mailingListDaoForDML = mailingListDaoForDML;
	}
	public boolean isRunning() {
		return isRunning;
	}
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	@Override
	public void run() {
		try{
		logger.info("---started----");
		isRunning = true;
		updateSizeOfUserLists();
		}catch (Exception e) {
			logger.info("Exception :: ",e);
		}finally{
		updateSizeOfUserLists();
		isRunning=false;
		}
		logger.info("---end----");
	}
	
	private void updateSizeOfUserLists(){
		
		mailingListDao = ((MailingListDao)applicationContext.getBean("mailingListDao"));
		mailingListDaoForDML = ((MailingListDaoForDML)applicationContext.getBean("mailingListDaoForDML"));
		MyCalendar myCal = new MyCalendar();
		try{
			List<MailingList> lastModifiedList = mailingListDao.getLastModifiedList(myCal.toString());
			if(logger.isDebugEnabled()) logger.debug("lastModifiedList="+lastModifiedList.size());
			String listIds=Constants.STRING_NILL;
			
			if(lastModifiedList !=null && lastModifiedList.size()>0){
				for (MailingList mailingList : lastModifiedList) {
					if(listIds.length() > 0) listIds += Constants.DELIMETER_COMMA;
					listIds += mailingList.getListId().toString();
				}
				List<MailingList> modifyList = mailingListDao.findByIds(listIds);
				logger.info("modifyList.size "+modifyList.size());
				if(modifyList!=null && modifyList.size()>0){
					for (MailingList mailingList : modifyList) {
						mailingListDaoForDML.udpateListSize(mailingList.getUsers().getUserId().longValue(),mailingList.getMlBit(),mailingList.getListId());
					}
				}
			}
			}catch(Exception e){
				logger.error("Exception",e);
			}
				
	}
}
