package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.ErrorLogDao;
import org.mq.captiway.scheduler.dao.ErrorLogDaoForDML;
import org.mq.captiway.scheduler.dao.MessagesDao;
import org.mq.captiway.scheduler.services.ExternalSMTPSender;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class EmailQueueScheduler extends TimerTask implements ApplicationContextAware{
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public EmailQueueScheduler(){
		isRunning = false;
	}
	
	private SessionFactory sessionFactory = null;
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private EmailQueueDao emailQueueDao = null;
	private EmailQueueDaoForDML emailQueueDaoForDML = null;
	public EmailQueueDaoForDML getEmailQueueDaoForDML() {
		return emailQueueDaoForDML;
	}
	public void setEmailQueueDaoForDML(EmailQueueDaoForDML emailQueueDaoForDML) {
		this.emailQueueDaoForDML = emailQueueDaoForDML;
	}
	public EmailQueueDao getEmailQueueDao() {
		return emailQueueDao;
	}
	public void setEmailQueueDao(EmailQueueDao emailQueueDao) {
		this.emailQueueDao = emailQueueDao;
	}
	
	private CampaignsDao campaignsDao = null;
	public CampaignsDao getCampaignsDao() {
		return campaignsDao;
	}
	public void setCampaignsDao(CampaignsDao campaignsDao) {
		this.campaignsDao = campaignsDao;
	}
	
	private MessagesDao messagesDao = null;
	public MessagesDao getMessagesDao() {
		return messagesDao;
	}
	public void setMessagesDao(MessagesDao messagesDao) {
		this.messagesDao = messagesDao;
	}

	private ErrorLogDao errorLogDao;
	public ErrorLogDao getErrorLogDao() {
		return errorLogDao;
	}
	public void setErrorLogDao(ErrorLogDao errorLogDao) {
		this.errorLogDao = errorLogDao;
	}

	private ErrorLogDaoForDML errorLogDaoForDML;
	
	public ErrorLogDaoForDML getErrorLogDaoForDML() {
		return errorLogDaoForDML;
	}
	public void setErrorLogDaoForDML(ErrorLogDaoForDML errorLogDaoForDML) {
		this.errorLogDaoForDML = errorLogDaoForDML;
	}

	private volatile boolean isRunning;
	public boolean isRunning() {
		
		return isRunning;
	}
	
	ApplicationContext context;
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
	
	private Queue<EmailQueue> queue = new LinkedList<EmailQueue>();
	private SimpleMailSender mailSender;
	
	public void run() {
		
		if(logger.isInfoEnabled()) logger.info(" -- just entered --");
		try{
			
			isRunning = true;
			List<EmailQueue> emailQueueList = emailQueueDao.findByTypeAndStatus(Constants.EQ_STATUS_ALL,
					Constants.EQ_STATUS_ACTIVE);
			if(logger.isInfoEnabled()) logger.info(" Active Count in EmailQueue : "+emailQueueList.size());
			
			if(emailQueueList.size() <= 0)
				return;
			
			ExternalSMTPSender externalSMTPSender =  new ExternalSMTPSender(context);
			
			//List<EmailQueue> tempEmailQueList = new ArrayList<EmailQueue>();
			
			for(EmailQueue emailQueue : emailQueueList ) {
				
				emailQueue.setStatus(Constants.EQ_STATUS_SENT);
				//emailQueue.setStatus(Constants.EQ_STATUS_SUBMITTED);
				
			}
			
			try {
				//emailQueueDao.saveByCollection(emailQueueList);
				emailQueueDaoForDML.saveByCollection(emailQueueList);
				//emailQueueDao.saveByCollection(tempEmailQueList);
				
			} 
			catch (Exception e) {
				logger.error(" ** Exception : while updating the status of EmailQueue" +
						" after scheduling", e);
			}
			
			queue.addAll(emailQueueList);
			
			if((mailSender == null || mailSender.isRunning == false) && queue.size() > 0) {
				mailSender = new SimpleMailSender(context, queue);
				mailSender.start();
			}
			
		} catch(Exception e) {
			logger.error(" ** Exception  : Root ", e );
		}
		finally{
			
			isRunning = false;
			
		}
		
		//System.gc();
	}
	
}
