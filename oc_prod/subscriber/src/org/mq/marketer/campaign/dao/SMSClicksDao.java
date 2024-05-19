package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class SMSClicksDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	 @SuppressWarnings("unchecked")
	public List<Object[]> getClickRateByCrId(Long crId, String startDateStr, String endDateStr) throws Exception {
	    	
	    	if(startDateStr==null || endDateStr== null) {
	    		return getHibernateTemplate().find("SELECT SUBSTRING(time(clickDate),1,2)," +
	    				" COUNT(sentId),COUNT(DISTINCT sentId) FROM SMSClicks WHERE sentId IN" +
	    				"(SELECT sentId FROM SMSCampaignSent WHERE smsCampaignReport="+crId+") " +
	    						"GROUP BY SUBSTRING(time(clickDate),1,2))");  
	    	} else {
	    		return getHibernateTemplate().find("SELECT SUBSTRING(time(clickDate),1,2), " +
	    				"COUNT(sentId),COUNT(DISTINCT sentId) FROM SMSClicks WHERE sentId IN" +
	    				"(SELECT sentId FROM SMSCampaignSent WHERE smsCampaignReport="+crId+")AND " +
	    						"clickDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' " +
	    								"GROUP BY SUBSTRING(time(clickDate),1,2))");
	    	}
		 
		 
	    }
	
	public List<Object[]> getClicksByCampUrlId() throws Exception{
		
		List<Object[]> retList= null;
		String queryStr = "SELECT smsCampUrlId ,COUNT(DISTINCT sentId), COUNT(clickId) FROM SMSClicks  GROUP BY smsCampUrlId ";

			
		retList = getHibernateTemplate().find(queryStr);
		
		
		return retList;
				
	}
	public Long getTotClickCount(Long crId)throws Exception{
		
		return (Long)getHibernateTemplate().find("select count(clickUrl) FROM SMSClicks where crId=" + crId ).get(0);
		
	}
	
	   public List<Object[]> getClickInfoBySentId(Long sentId)throws Exception{
			return getHibernateTemplate().find("select clickUrl,clickDate from SMSClicks where sentId="+sentId);
		}
}
