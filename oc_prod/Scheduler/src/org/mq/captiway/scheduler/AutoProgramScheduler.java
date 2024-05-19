package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.AutoProgram;
import org.mq.captiway.scheduler.dao.AutoProgramComponentsDao;
import org.mq.captiway.scheduler.dao.AutoProgramComponentsDaoForDML;
import org.mq.captiway.scheduler.dao.AutoProgramDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AutoProgramScheduler extends TimerTask implements ApplicationContextAware{

	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private MailingListDao mailingListDao;
	
	public MailingListDao getMailingListDao() {
		return mailingListDao;
	}

	public void setMailingListDao(MailingListDao mailingListDao) {
		this.mailingListDao = mailingListDao;
	}

	private AutoProgramDao autoProgramDao;
	
	public AutoProgramDao getAutoProgramDao() {
		return autoProgramDao;
	}

	public void setAutoProgramDao(AutoProgramDao autoProgramDao) {
		this.autoProgramDao = autoProgramDao;
	}

	private AutoProgramComponentsDao autoProgramComponentsDao;
	
	public AutoProgramComponentsDao getAutoProgramComponentsDao() {
		return autoProgramComponentsDao;
	}

	public void setAutoProgramComponentsDao(
			AutoProgramComponentsDao autoProgramComponentsDao) {
		this.autoProgramComponentsDao = autoProgramComponentsDao;
	}

	private AutoProgramComponentsDaoForDML autoProgramComponentsDaoForDML;
	
	public AutoProgramComponentsDaoForDML getAutoProgramComponentsDaoForDML() {
		return autoProgramComponentsDaoForDML;
	}

	public void setAutoProgramComponentsDaoForDML(
			AutoProgramComponentsDaoForDML autoProgramComponentsDaoForDML) {
		this.autoProgramComponentsDaoForDML = autoProgramComponentsDaoForDML;
	}

	private ProgramQueue programQueue;
	
	public ProgramQueue getProgramQueue() {
		return programQueue;
	}

	public void setProgramQueue(ProgramQueue programQueue) {
		this.programQueue = programQueue;
	}

	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	private AutoProgramPublisher autoProgrampublisher;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()) logger.debug("started running the timertask");
		
		MyCalendar myCal = new MyCalendar();
		
		List<AutoProgram> activeProgramsList = new ArrayList<AutoProgram>();
		activeProgramsList = autoProgramDao.getActivePrograms(myCal.toString());
		
		
		if(activeProgramsList.size()>0)
			programQueue.addCollection(activeProgramsList);
			
		if(programQueue.getQueueSize() > 0) {
			
			// need to start the AutoProgramPublisher if it is not already running
			
			AutoProgramPublisher.startAutoProgramPublisher(applicationContext);
			
			/*if(autoProgrampublisher == null || !(autoProgrampublisher.isRunning())) {
				logger.debug("entered to start the thread");
				autoProgrampublisher = new AutoProgramPublisher(applicationContext);
				
				autoProgrampublisher.start();
			}*/
			
			
		}//if
		if(logger.isDebugEnabled()) logger.debug("the number of Active programs are===>"+activeProgramsList.size());
		
		
		
	}//run
	
}//AutoProgramScheduler
