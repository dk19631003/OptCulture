package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SalesUpdateErrorLog;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.exception.BaseDAOException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;


public class SalesUpdateErrorLogDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	} 
	
	public void saveByCollection(List<SalesUpdateErrorLog> updateList) {
		
		super.saveOrUpdateAll(updateList);
	}
	
	/*public List<SalesUpdateErrorLog> getAllFailures() {
		
		try {
			String qry = " FROM SalesUpdateErrorLog WHERE status='F' ";
			
			List<SalesUpdateErrorLog> retList = executeQuery(qry);
			
			return retList;
		} catch (Exception e) {
			logger.error("Exception while fetching the failured ", e);
		}
		
		return null;
	}*/
	
/*	public void reRunFailures(String query) throws Exception{
		
		try {
			jdbcTemplate.execute(query);
			
			
		} catch(DeadlockLoserDataAccessException e){
			
			throw new DeadlockLoserDataAccessException("deadlock occured", new Throwable());
		}
		catch (Exception e) {
			logger.error("Exception while fetching the failured ", e);
		}
		
		
	}*/
	
}
