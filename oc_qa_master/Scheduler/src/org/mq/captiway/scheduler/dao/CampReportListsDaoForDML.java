package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.CampReportLists;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.springframework.jdbc.core.JdbcTemplate;

public class CampReportListsDaoForDML extends AbstractSpringDaoForDML {
	
	
	public CampReportListsDaoForDML() {
		
	}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	public void saveOrUpdate(CampReportLists campReportLists) {
	        super.saveOrUpdate(campReportLists);
	    }

    public void delete(CampReportLists campReportLists) {
        super.delete(campReportLists);
    }

	public void deleteByCampRepId(Long campRepId) {
		String qry = "DELETE FROM campreports_lists where cr_id="+campRepId;
		
		executeJdbcUpdateQuery(qry);
		
		
		
	}
	
/*	public CampReportLists findByCampReportId(Long campRepId) {
		
		List<CampReportLists> tempCampReportLists = null;
		
		tempCampReportLists = getHibernateTemplate().find("FROM CampReportLists where campaignReportId="+campRepId);
		if(tempCampReportLists!=null && tempCampReportLists.size()>0) {
			return (CampReportLists)tempCampReportLists.get(0);
		}
		
		return null;
		
		
	}*/
	
	
}
