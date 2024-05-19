package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampReportLists;
import org.mq.marketer.campaign.beans.WACampReportLists;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class WACampReportListsDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	 
	public WACampReportListsDao() {}
	
	private JdbcTemplate jdbcTemplate;
	 
	 public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public WACampReportLists findBywaCampReportId(Long waCampRepId) {
		List<WACampReportLists> tempCampReportLists = null;
		
		tempCampReportLists = getHibernateTemplate().find("FROM WACampReportLists where waCampaignReportId="+waCampRepId);
		
		//logger.info("got the list of size====>"+tempCampReportLists.size());
		if(tempCampReportLists!=null && tempCampReportLists.size()>0) {
			return (WACampReportLists)tempCampReportLists.get(0);
		}
		
		return null;
		
		
	}
	
	
public WACampReportLists findByCampReportId(Long campRepId) {
		
		List<WACampReportLists> tempCampReportLists = null;
		
		tempCampReportLists = getHibernateTemplate().find("FROM WACampReportLists where waCampaignReportId="+campRepId);
		if(tempCampReportLists!=null && tempCampReportLists.size()>0) {
			return (WACampReportLists)tempCampReportLists.get(0);
		}
		
		return null;
		
		
	}
	
}
