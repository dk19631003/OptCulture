package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.WACampReportLists;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class WACampReportListsDaoForDML extends AbstractSpringDaoForDML {

	public WACampReportListsDaoForDML() {}
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	 
	
	private JdbcTemplate jdbcTemplate;
	 
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	
	public void saveOrUpdate(WACampReportLists waCampReportLists) {
        super.saveOrUpdate(waCampReportLists);
    }

	public void delete(WACampReportLists waCampReportLists) {
    super.delete(waCampReportLists);
}
	
	
	
	/*public SMSCampReportLists findBySmsCampReportId(Long smsCampRepId) {
		//logger.debug("----just entered with smsCampaignReportId----"+smsCampRepId);
		List<SMSCampReportLists> tempCampReportLists = null;
		
		tempCampReportLists = getHibernateTemplate().find("FROM SMSCampReportLists where smsCampaignReportId="+smsCampRepId);
		
		//logger.debug("got the list of size====>"+tempCampReportLists.size());
		if(tempCampReportLists!=null && tempCampReportLists.size()>0) {
			return (SMSCampReportLists)tempCampReportLists.get(0);
		}
		
		return null;
		
		
	}*/
	
	
	
}
