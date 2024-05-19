package org.mq.captiway.scheduler.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.POSFileLogs;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class POSFileLogDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public POSFileLogDaoForDML() { }

	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    /*public POSFileLogs find(Long id) {
        return (POSFileLogs) super.find(POSFileLogs.class, id);
    }*/

    public void saveOrUpdate(POSFileLogs posFileLogs) {
        super.saveOrUpdate(posFileLogs);
    }

    public void delete(POSFileLogs posFileLogs) {
        super.delete(posFileLogs);
    }

    

    public void saveByCollection(Collection<POSFileLogs> posFileLogsCollection){
    	super.saveOrUpdateAll(posFileLogsCollection);
    }

	/*public Calendar getLastFetchedTime(Long userId) {
		
		logger.debug("inside get last fetched time for Sales Data Alert Mail");
		
		String qry = "select fetchedTime from POSFileLogs  where userId = " + userId + " and fileType = 'Sales' order by fetchedTime desc";
		
		logger.debug("exit get last fetched time for Sales Data Alert Mail");
		 List list = executeQuery(qry);
		   if(list == null || list.size() == 0) return null;
		return (Calendar) executeQuery(qry, 0, 1).get(0);
		
	}*/
	
}
