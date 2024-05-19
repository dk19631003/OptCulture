package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.utility.Constants;

import org.springframework.dao.DataAccessException;

public class TempActivityDataDao extends AbstractSpringDao{

	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public TempActivityDataDao() {}
	
	
	
	/*public int deleteTempActivityContactsData(String label, Long compId) {
		int deleteCount = 0;
		
		String jdbcQry = "DELETE FROM temp_activity_data where label ='"+label+"" +
		 			 	 "' AND component_id="+compId;

		
		try {
			deleteCount = executeJdbcUpdateQuery(jdbcQry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		}
		
		return deleteCount;
		
		
	}*/
	
	public List<String> getContactIdFromLabel(String label) {
		List<String> cidList = null; 
		
		String qry = "SELECT contactId FROM TempActivityData WHERE label='"+label+"'";
		
		try {
			cidList = getHibernateTemplate().find(qry);
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
		}
		
		return cidList;
		
		
		
		
	}
	
	
	
}
