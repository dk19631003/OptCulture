package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampReportLists;
import org.mq.marketer.campaign.beans.SMSCampReportLists;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class SMSCampReportListsDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	 
	public SMSCampReportListsDao() {}
	
	private JdbcTemplate jdbcTemplate;
	 
	 public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public SMSCampReportLists findBySmsCampReportId(Long smsCampRepId) {
		//logger.info("----just entered with smsCampaignReportId----"+smsCampRepId);
		List<SMSCampReportLists> tempCampReportLists = null;
		
		tempCampReportLists = getHibernateTemplate().find("FROM SMSCampReportLists where smsCampaignReportId="+smsCampRepId);
		
		//logger.info("got the list of size====>"+tempCampReportLists.size());
		if(tempCampReportLists!=null && tempCampReportLists.size()>0) {
			return (SMSCampReportLists)tempCampReportLists.get(0);
		}
		
		return null;
		
		
	}
	
	
public SMSCampReportLists findByCampReportId(Long campRepId) {
		
		List<SMSCampReportLists> tempCampReportLists = null;
		
		tempCampReportLists = getHibernateTemplate().find("FROM SMSCampReportLists where smsCampaignReportId="+campRepId);
		if(tempCampReportLists!=null && tempCampReportLists.size()>0) {
			return (SMSCampReportLists)tempCampReportLists.get(0);
		}
		
		return null;
		
		
	}
	
}
