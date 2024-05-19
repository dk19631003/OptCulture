package org.mq.captiway.scheduler.services;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.dao.PurgeDao;
import org.mq.captiway.scheduler.utility.Constants;

public class PurgeScheduler  extends TimerTask{
	private static final Logger logger =  LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private PurgeDao purgeDao;
	private boolean isRunning;

	public PurgeDao getPurgeDao() {
		return purgeDao;
	}

	public void setPurgeDao(PurgeDao purgeDao) {
		this.purgeDao = purgeDao;
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
		logger.info("---started purging----");
		isRunning = true;
		purgeUserLists();
		}catch (Exception e) {
			logger.info("Exception :: ",e);
		}finally{
		purgeUserLists();
		isRunning=false;
		}
		logger.info("---endede purging----");
	}
	
	private void purgeUserLists(){
		List<Long> userIds = purgeDao.getUsers();
		if(userIds.size()>0){
		ExecutorService executor = Executors.newFixedThreadPool(userIds.size()>10?10:userIds.size());
		for(Long userId:userIds){
			try{
			Thread worker = new PurgeThread(userId);
			executor.execute(worker);
			}catch (Exception e) {
				logger.info("Exception ::",e);
			}
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		}
				
	}
	
}
