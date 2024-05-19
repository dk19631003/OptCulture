package org.mq.captiway.scheduler;


import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.business.helper.WorkerThread;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class OptCultureCSVFileUpload extends TimerTask implements ApplicationContextAware {
	
	private static final  Logger logger = LogManager.getLogger(Constants.FILE_PROCESS_LOGGER);
	
	
	public OptCultureCSVFileUpload() {}
	
	public static final int FIXEDTHREADPOOLSIZE = 4;
	private ApplicationContext context;

	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
	private UsersDao usersDao = null;
	private MailingListDao mailingListDao = null;
	
	
	public void run() {
		
		long startTime = System.currentTimeMillis();
		logger.debug("....=======> Started  run :: " + startTime);
		try {
			usersDao = (UsersDao)context.getBean("usersDao");
			mailingListDao = (MailingListDao)context.getBean("mailingListDao");
			
			List<Users> usersList = usersDao.findUsersByAccType("Primary");
			
			if(usersList == null || usersList.size() == 0) {
				if(logger.isDebugEnabled()) logger.debug("No primary type users exists");
				return;
			}
			
			//Spanning of Threads
			
			/**
			 * Create 4 Threads & Process each User's all files in one thread
			 * Ref : https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
			 */
			 ExecutorService executor = Executors.newFixedThreadPool(FIXEDTHREADPOOLSIZE);
			 
			for (Users users : usersList) {
				//to find out the POS and BCRM type of list
				List<MailingList> mListLst= mailingListDao.findListTypeMailingList(users.getUserId());
				
				if(mListLst == null ) {
					logger.info("No POS / BCRM type mailing list exist  for this user "+users.getUserName());
					continue;
				}
				if( mListLst.size() >= 2) {
					logger.info("got more than two mailing list of type POS / BCRM  exist  for this user "+users.getUserName());
					continue;
				}
				
				
				for (MailingList mailingList : mListLst) {
					Runnable worker = new WorkerThread(users, mailingList,context);
					executor.execute(worker);
					
				}//for each mailing list
				
			}//for each users
			/**
			 * The shutdown() method will allow previously submitted tasks
			 * to execute before terminating, while the shutdownNow() method
			 * prevents waiting tasks from starting and attempts to stop
			 * currently executing tasks. Upon termination, an executor has
			 * no tasks actively executing, no tasks awaiting execution, and
			 * no new tasks can be submitted. An unused ExecutorService
			 * should be shut down to allow reclamation of its resources.
			 */
			executor.shutdown();
			while (!executor.isTerminated()) {
			}
			logger.debug("Finished all threads");
			logger.debug("OptCultureCSVFileUpload Thread end here ..");
		} catch(Exception e) {
			
			logger.error("Exception ::::" , e);
		}
		logger.debug("<------------- Completed run in : "
				+ (System.currentTimeMillis() - startTime) + " Millisecond");
	} //run
		
	
} //Class
	
	