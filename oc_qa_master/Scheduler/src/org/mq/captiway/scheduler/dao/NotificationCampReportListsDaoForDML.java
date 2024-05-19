package org.mq.captiway.scheduler.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.NotificationCampReportLists;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationCampReportListsDaoForDML extends AbstractSpringDaoForDML {

	public NotificationCampReportListsDaoForDML() {}
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	 
	
	private JdbcTemplate jdbcTemplate;
	 
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	
	public void saveOrUpdate(NotificationCampReportLists notificationCampReportLists) {
        super.saveOrUpdate(notificationCampReportLists);
        logger.info("saved successfully.");
    }

	public void delete(NotificationCampReportLists notificationCampReportLists) {
    super.delete(notificationCampReportLists);
}
	
	
}
