package org.mq.marketer.campaign.dao;

import java.util.List;

public class WAOpensDao extends AbstractSpringDao{

	
	
	 public List<Object[]> getOpenRateByCrId(Long crId, String startDateStr, String endDateStr){
	    	
	    	if(startDateStr==null || endDateStr== null) {
	    		// SELECT substring(time(open_date),1,2) as dd, count(sent_id), count(distinct(sent_id))  FROM opens where sent_id in(select sent_id from campaign_sent where cr_id=235) group by substring(time(open_date),1,2)

	    		return getHibernateTemplate().find( "SELECT SUBSTRING(time(o.openDate),1,2), " +
	    				"COUNT(o.sentId), COUNT(DISTINCT o.sentId) FROM WAOpens o, WACampaignSent cs " +
	    				"WHERE cs.waCampaignReport="+crId+ " AND " +
	    				"o.sentId = cs.sentId " +
	    				"GROUP BY SUBSTRING(time(o.openDate),1,2) ORDER BY SUBSTRING(time(o.openDate),1,2)");  
	    	
	    	}
	    	else {

	    		return getHibernateTemplate().find( "SELECT SUBSTRING(time(o.openDate),1,2), " +
	    				"COUNT(o.sentId), COUNT(DISTINCT o.sentId) FROM WAOpens o, WACampaignSent cs " +
	    				"WHERE cs.waCampaignReport="+crId+ " AND " +
	    				"o.sentId = cs.sentId AND " +
	    				"o.openDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' " +
	    				"GROUP BY SUBSTRING(time(o.openDate),1,2) ORDER BY SUBSTRING(time(o.openDate),1,2)");  
	    	}
	    }
	
	
	
}
