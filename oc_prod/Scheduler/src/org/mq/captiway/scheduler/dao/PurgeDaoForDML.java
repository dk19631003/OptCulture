package org.mq.captiway.scheduler.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.DomainStatus;
import org.mq.captiway.scheduler.beans.PurgeQueue;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.springframework.dao.DataIntegrityViolationException;

public class PurgeDaoForDML  extends AbstractSpringDaoForDML {
	private static final Logger logger =  LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	 public void saveOrUpdateAll(Collection collection){
		 for(Object domainStatus : collection){
			 try{
			 saveOrUpdate(domainStatus);
			 }catch (DataIntegrityViolationException e) {
				logger.info("domain already exist :: "+domainStatus);
			}catch(Exception e){
				logger.info("Exception while adding new domain:: ",e);
			}
		 }
	 }
	 public void initiatePurge(Long userId, Long listId) {
		 String queryStr = "insert into purge_queue(user_id,list_id,status,created_date) VALUES ("+ userId+ ","+ listId+ ",'"
					+ Constants.PURGE_STATUS_INCOMPLETED+ "',"	+  "now()) ON DUPLICATE KEY UPDATE  status='"+ Constants.PURGE_STATUS_INCOMPLETED+ "'";
			executeJdbcQuery(queryStr);
		}
}
