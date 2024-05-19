package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.LtySettingsActivityLogs;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.optculture.utils.OCConstants;

public class LtySettingsActivityLogsDao extends AbstractSpringDao implements Serializable{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	public LtySettingsActivityLogsDao() {
	}
	
	public List<LtySettingsActivityLogs> fetchAllLogsToBeSent() {
		
		List<LtySettingsActivityLogs> logsList = null;
		try{
			String queryStr = " FROM LtySettingsActivityLogs WHERE sendEmailFlag = '" + OCConstants.FLAG_YES + "' " +
					" ORDER BY programId, createdDate ASC";
			logger.info(" fetchAllLogsToBeSent queryStr = "+queryStr);
			logsList  = (List<LtySettingsActivityLogs>)getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			logger.error(">>> Exception in fetchCurrentInActiveTrans dao >>>", e);
			//throw new LoyaltyProgramException("Fetch transactions failed.");
		}
		return logsList;
		
	}

	public Calendar getLastModifiedDateByPrgmId(Long programId) {
		List logsList = null;
		try{
			String queryStr = "SELECT createdDate FROM LtySettingsActivityLogs WHERE sendEmailFlag = '" + OCConstants.FLAG_YES + "' " +
					" AND programId = "+ programId.longValue() +" ORDER BY createdDate DESC";
			logger.info(" getLastModifiedDateByPrgmId queryStr = "+queryStr);
			logsList  = getHibernateTemplate().find(queryStr);
		}catch(Exception e){
			logger.error(">>> Exception in fetchCurrentInActiveTrans dao >>>", e);
			//throw new LoyaltyProgramException("Fetch transactions failed.");
		}
		if(logsList != null && logsList.size() > 0){
//			String modifiedDate = MyCalendar.calendarToString((Calendar) logsList.get(0), MyCalendar.FORMAT_DATETIME_WITH_DELIMETER);
			return (Calendar) logsList.get(0);
		}
		return null;
		
	}

}
