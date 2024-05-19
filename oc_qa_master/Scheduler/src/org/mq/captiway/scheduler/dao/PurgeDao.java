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

public class PurgeDao  extends AbstractSpringDao {
	private static final Logger logger =  LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	public List<Long> getUsers() {
		List<Long> userIds= new ArrayList<Long>(0);
		try{
			String qry = "SELECT DISTINCT userId FROM PurgeQueue WHERE status='"+Constants.PURGE_STATUS_INCOMPLETED+"'";
			userIds = executeQuery(qry);
			
		}catch (Exception e) {
			logger.info("Exception :: ",e);
		}
		return userIds;
	}

	public  List<PurgeQueue> getAllPurgePendingByUserId(Long userId) {
		List<PurgeQueue> purge = new ArrayList<PurgeQueue>(0);
		try{
			String qry = "FROM PurgeQueue WHERE userId="+userId+" AND status='"+Constants.PURGE_STATUS_INCOMPLETED+"'";
			purge = executeQuery(qry);
			
			
		}catch (Exception e) {
			logger.info("Exception :: ",e);
		}
		return purge;
		
	}
	
	public List<DomainStatus> getAllDomainswithStatus(){
		List<DomainStatus> domains = new ArrayList<DomainStatus>(0);
		try{
			String qry = "FROM DomainStatus";
			domains = executeQuery(qry);
		}catch (Exception e) {
			logger.info("Exception :: ",e);
		}
		return domains;
	}

}
