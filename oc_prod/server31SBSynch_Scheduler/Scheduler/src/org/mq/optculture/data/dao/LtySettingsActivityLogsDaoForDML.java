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
import org.mq.captiway.scheduler.dao.AbstractSpringDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.optculture.utils.OCConstants;

public class LtySettingsActivityLogsDaoForDML extends AbstractSpringDaoForDML implements Serializable{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	public LtySettingsActivityLogsDaoForDML() {
		// TODO Auto-generated constructor stub
	}

	public void saveOrUpdate(LtySettingsActivityLogs ltySettingsActivityLogs) {
		super.saveOrUpdate(ltySettingsActivityLogs);
	}

	public void saveByCollection(List<LtySettingsActivityLogs> logsList) {
    	super.saveOrUpdateAll(logsList);
    }
	
	

}
