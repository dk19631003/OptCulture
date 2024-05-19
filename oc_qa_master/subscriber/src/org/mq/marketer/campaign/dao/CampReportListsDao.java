package org.mq.marketer.campaign.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampReportLists;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class CampReportListsDao extends AbstractSpringDao implements Serializable {
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	 
	public CampReportListsDao() {}
	
	 private JdbcTemplate jdbcTemplate;
	 
	 public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
 	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	
	public CampReportLists findByCampReportId(Long campRepId) {
		
		List<CampReportLists> tempCampReportLists = null;
		
		tempCampReportLists = getHibernateTemplate().find("FROM CampReportLists where campaignReportId="+campRepId);
		if(tempCampReportLists!=null && tempCampReportLists.size()>0) {
			return (CampReportLists)tempCampReportLists.get(0);
		}
		
		return null;
		
		
	}
	
}
