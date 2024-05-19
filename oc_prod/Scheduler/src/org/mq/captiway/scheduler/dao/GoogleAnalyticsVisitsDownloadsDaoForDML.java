package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.GoogleAnalyticsVisitsDownloads;
import org.mq.captiway.scheduler.utility.Constants;




public class GoogleAnalyticsVisitsDownloadsDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	public GoogleAnalyticsVisitsDownloadsDaoForDML(){}
	
	/*private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}*/
/*	  
    public List<GoogleAnalyticsVisitsDownloads> findStatus(String status) {
        if(logger.isDebugEnabled()) logger.debug("----just enterd here----");
        
        return getHibernateTemplate().find(" FROM GoogleAnalyticsVisitsDownloads WHERE status= '" + status + "' " );
        
        
    }*/
    public void saveOrUpdate(GoogleAnalyticsVisitsDownloads googleAnalyticsVisitsDownloads) {
    	super.saveOrUpdate(googleAnalyticsVisitsDownloads);
    }
	   
}

