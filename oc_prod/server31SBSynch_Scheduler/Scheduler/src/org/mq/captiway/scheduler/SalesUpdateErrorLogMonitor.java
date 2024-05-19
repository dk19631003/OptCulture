package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CampaignSchedule;
import org.mq.captiway.scheduler.beans.SalesUpdateErrorLog;
import org.mq.captiway.scheduler.dao.SalesUpdateErrorLogDao;
import org.mq.captiway.scheduler.dao.SalesUpdateErrorLogDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DeadlockLoserDataAccessException;


public class SalesUpdateErrorLogMonitor extends TimerTask{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private volatile boolean isRunning;
	public boolean isRunning() {
		
		return isRunning;
	}
	 @Override
	public void run() {
		
		 try {
			logger.debug("======monitoring started====== ");
			 isRunning = true;
			 SalesUpdateErrorLogDao salesUpdateErrorLogDao = null;
			 SalesUpdateErrorLogDaoForDML salesUpdateErrorLogDaoForDML = null;

			 
			 try {
				salesUpdateErrorLogDao = (SalesUpdateErrorLogDao)ServiceLocator.getInstance().getDAOByName("salesUpdateErrorLogDao");
				salesUpdateErrorLogDaoForDML = (SalesUpdateErrorLogDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("salesUpdateErrorLogDaoForDML");

			 } catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("exception ", e);
			}
			 
			 if(salesUpdateErrorLogDao == null) return;
			 
			 List<SalesUpdateErrorLog> retList = salesUpdateErrorLogDao.getAllFailures();
			 
			 if(retList == null || retList.isEmpty()) {
				 logger.debug("No F records found");
				 return;
			 }
			 logger.debug(" record fetch count ::"+retList.size());
			 String failedquery = null;
			 List<SalesUpdateErrorLog> updateList = new ArrayList<SalesUpdateErrorLog>();
			 for (SalesUpdateErrorLog salesUpdateErrorLog : retList) {
				 
				 failedquery = salesUpdateErrorLog.getQuery();
				 salesUpdateErrorLog.setLastFetchedTime(Calendar.getInstance());
				 if(failedquery == null){
					 logger.debug("failedquery is null for ::"+salesUpdateErrorLog.getId().longValue());
					 salesUpdateErrorLog.setStatus("Q");
					 updateList.add(salesUpdateErrorLog);
					 continue;
				 }
				 
				 if(salesUpdateErrorLog.getCount() > 5){
					 
					 salesUpdateErrorLog.setStatus("K");
					 updateList.add(salesUpdateErrorLog);
					continue;
				 }
				 
				 int updatedCount = 0;
				try {
					salesUpdateErrorLogDao.reRunFailures(failedquery);
					// throw new DeadlockLoserDataAccessException("deadlock occured", new Throwable());
				} catch (Exception e) {
					logger.debug("in catch of Exception "+e.getClass().getName());
					if( e instanceof org.springframework.dao.DeadlockLoserDataAccessException) {
						logger.debug("in catch of  DeadlockLoserDataAccessException");
						salesUpdateErrorLog.setStatus("F");
						salesUpdateErrorLog.setCount(salesUpdateErrorLog.getCount()+1);
						 updateList.add(salesUpdateErrorLog);
						continue;
					}
				}
				 
				 logger.debug("====updated for user==="+salesUpdateErrorLog.getUserId().longValue()+"======"+updatedCount);
				 salesUpdateErrorLog.setStatus("S");
				
				 updateList.add(salesUpdateErrorLog);
			 }//for
			 if(updateList.size() > 0) {
				 
				// salesUpdateErrorLogDao.saveByCollection(updateList);
				 salesUpdateErrorLogDaoForDML.saveByCollection(updateList);

			 }
			 logger.debug("======monitoring end====== ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}finally{
			
			isRunning = false;
		}
		 
		
	}
}
