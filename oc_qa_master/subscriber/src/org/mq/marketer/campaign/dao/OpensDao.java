


package org.mq.marketer.campaign.dao;


import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Opens;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({ "unchecked", "unused"})
public class OpensDao extends AbstractSpringDao {
    public OpensDao() {}
    private SessionFactory sessionFactory;

    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
    public Opens find(Long id) {
        return (Opens) super.find(Opens.class, id);
    }

    /*public void saveOrUpdate(Opens opens) {
        super.saveOrUpdate(opens);
    }

    public void delete(Opens opens) {
        super.delete(opens);
    }*/

    public List findAll() {
        return super.findAll(Opens.class);
    }
    
    public List<Calendar> getOpenTimeInfo(Long sentId){
		return getHibernateTemplate().find("select openDate from Opens where sentId = "+sentId +" order by openDate desc");
	}
   
    public List<Map<String, Object>> getDesktopReportByCrId(Long crId,String ecids) {

/*    	return getHibernateTemplate().find("SELECT  o.osFamily, count(o.emailClient) " + 
				" FROM Opens o, CampaignSent cs WHERE o.sentId=cs.sentId and cs.campaignReport="+crId+
				"	and o.emailClient in(SELECT ec.id FROM EmailClient ec WHERE ec.userAgent='Browser' or ec.userAgent='Email Client') " + 
					" GROUP BY  o.osFamily ORDER BY 2 DESC");
*/

    	/*String qry="SELECT t1.osf as osfamily, count(t1.tot_count) as osfcount FROM " +
				"( SELECT  o.os_Family as osf, count(o.os_Family) as tot_count " +
				" FROM opens o, campaign_sent cs WHERE o.sent_id=cs.sent_id and cs.cr_id=" + crId.longValue() +
				" and o.email_client in(select distinct ec.id from email_clients ec " +
				" WHERE ec.email_client='"+Constants.UA_TYPE+"' AND ec.user_agent IN('Email_Client','Browser','Email Client','Other') ) " +
				" group by o.os_Family, o.sent_id, o.email_client ) as t1 " +
				" group by t1.osf  order by 2 desc";*/
    	//app-3644
    	String qry="SELECT t1.osf as osfamily, count(t1.tot_count) as osfcount FROM " +
    			"( SELECT  o.os_Family as osf, count(o.os_Family) as tot_count " +
    			" FROM opens o, campaign_sent cs WHERE o.sent_id=cs.sent_id and cs.cr_id=" + crId.longValue() +
    			" and o.email_client in("+ecids+") " +
    			" group by o.os_Family, o.sent_id, o.email_client ) as t1 " +
    			" group by t1.osf  order by 2 desc";
    	logger.info("getDesktopReportByCrId query::"+qry);
    	
    	
    	return jdbcTemplate.queryForList(qry);
    	

    	   }

    public List<Map<String, Object>> getPhoneReportByCrId(Long crId, String ecids) {
    	
    	/*return getHibernateTemplate().find("SELECT  o.osFamily, count(o.emailClient) " + 
    										" FROM Opens o, CampaignSent cs WHERE o.sentId=cs.sentId and cs.campaignReport="+crId+
    										"	and o.emailClient in(SELECT ec.id FROM EmailClient ec WHERE ec.userAgent='Mobile Browser') " + 
    											" GROUP BY  o.osFamily ORDER BY 2 DESC");*/
		/*    	String qry="SELECT t1.osf as osfamily, count(t1.tot_count) as osfcount FROM " +
		    			"( SELECT  o.os_Family as osf, count(o.os_Family) as tot_count " +
		    			" FROM opens o, campaign_sent cs WHERE o.sent_id=cs.sent_id and cs.cr_id=" + crId.longValue() +
		    			" and o.email_client in(select distinct ec.id from email_clients ec " +
		    			" WHERE  ec.email_client='"+Constants.UA_TYPE+"' AND  ec.user_agent IN('Mobile Browser', 'MOBILE_BROWSER' )) " +
		    			" group by o.os_Family, o.sent_id, o.email_client) as t1 " +
		    			" group by t1.osf order by 2 desc";
		*/
    	//app-3644
    	String qry="SELECT t1.osf as osfamily, count(t1.tot_count) as osfcount FROM " +
    			"( SELECT  o.os_Family as osf, count(o.os_Family) as tot_count " +
    			" FROM opens o, campaign_sent cs WHERE o.sent_id=cs.sent_id and cs.cr_id=" + crId.longValue() +
    			" and o.email_client in("+ecids+") " +
    			" group by o.os_Family, o.sent_id, o.email_client) as t1 " +
    			" group by t1.osf order by 2 desc";
    	logger.info("getPhoneReportByCrId query::"+qry);
    	return jdbcTemplate.queryForList(qry);
    }
public List<Map<String, Object>> getEmailClientReportByCrId(Long crId,String ecids) {
    	
    	/*return getHibernateTemplate().find("SELECT  o.uaFamily, count(o.emailClient) " + 
									" FROM Opens o, CampaignSent cs WHERE o.sentId=cs.sentId and cs.campaignReport="+crId+
									" and o.emailClient in(SELECT ec.id FROM EmailClient ec WHERE ec.userAgent='Email Client') " + 
										" GROUP BY  o.uaFamily ORDER BY 2 DESC");*/	
		/*	String qry="SELECT t1.uaf as uafamily, count(t1.tot_count) as uafcount FROM " +
					"( SELECT  o.ua_Family as uaf, count(o.ua_Family) as tot_count " +
					" FROM opens o, campaign_sent cs WHERE o.sent_id=cs.sent_id and cs.cr_id=" + crId.longValue() +
					" and o.email_client in(select distinct ec.id from email_clients ec " +
					" WHERE ec.email_client='"+Constants.UA_TYPE+"' AND ec.user_agent in('Email Client','Email_Client','Browser')) " +
					" GROUP BY o.ua_Family, o.sent_id, o.email_client) as t1 " +
					" GROUP BY t1.uaf ORDER BY 2 DESC";*/
	//app-3644
	String qry="SELECT t1.uaf as uafamily, count(t1.tot_count) as uafcount FROM " +
			"( SELECT  o.ua_Family as uaf, count(o.ua_Family) as tot_count " +
			" FROM opens o, campaign_sent cs WHERE o.sent_id=cs.sent_id and cs.cr_id=" + crId.longValue() +
			" and o.email_client in("+ecids+") " +
			" GROUP BY o.ua_Family, o.sent_id, o.email_client) as t1 " +
			" GROUP BY t1.uaf ORDER BY 2 DESC";
	logger.info("getEmailClientReportByCrId query::"+qry);
	return jdbcTemplate.queryForList(qry);
	
}
	
	
public List<Map<String, Object>> getMobileBrowserReportByCrId(Long crId,String ecids) {
	
	/*return getHibernateTemplate().find("SELECT  o.uaFamily, count(o.emailClient) " + 
								" FROM Opens o, CampaignSent cs WHERE o.sentId=cs.sentId and cs.campaignReport="+crId+
								" and o.emailClient in(SELECT ec.id FROM EmailClient ec WHERE ec.userAgent='Mobile Browser') " + 
									" GROUP BY  o.uaFamily ORDER BY 2 DESC");*/	
	/*	String qry="SELECT t1.uaf as uafamily, count(t1.tot_count) as uafcount FROM " +
				"( SELECT  o.ua_Family as uaf, count(o.ua_Family) as tot_count " +
				" FROM opens o, campaign_sent cs WHERE o.sent_id=cs.sent_id and cs.cr_id=" + crId.longValue() +
				" and o.email_client in(select distinct ec.id from email_clients ec " +
				" where ec.email_client='"+Constants.UA_TYPE+"' AND ec.user_agent IN('Mobile Browser', 'MOBILE_BROWSER' )) " +
				" group by o.ua_Family, o.sent_id, o.email_client) as t1 " +
				" group by t1.uaf order by 2 desc";*/
	//app-3644
	String qry="SELECT t1.uaf as uafamily, count(t1.tot_count) as uafcount FROM " +
			"( SELECT  o.ua_Family as uaf, count(o.ua_Family) as tot_count " +
			" FROM opens o, campaign_sent cs WHERE o.sent_id=cs.sent_id and cs.cr_id=" + crId.longValue() +
			" and o.email_client in("+ecids+") " +
			" group by o.ua_Family, o.sent_id, o.email_client) as t1 " +
			" group by t1.uaf order by 2 desc";
	logger.info("getMobileBrowserReportByCrId query::"+qry);
	return jdbcTemplate.queryForList(qry);
}
public List<Map<String, Object>> getBrowserReportByCrId(Long crId,String ecids) {
	
	/*return getHibernateTemplate().find("SELECT  o.uaFamily, count(o.emailClient) " + 
								" FROM Opens o, CampaignSent cs WHERE o.sentId=cs.sentId and cs.campaignReport="+crId+
								" and o.emailClient in(SELECT ec.id FROM EmailClient ec WHERE ec.userAgent='Browser') "+ 
									" GROUP BY  o.uaFamily ORDER BY 2 DESC");*/
	
	/*	String qry="SELECT t1.uaf as uafamily, count(t1.tot_count) as uafcount FROM " +
				"( SELECT  o.ua_Family as uaf, count(o.ua_Family) as tot_count " +
				" FROM opens o, campaign_sent cs WHERE o.sent_id=cs.sent_id and cs.cr_id=" + crId.longValue() +
				" and o.email_client in(select distinct ec.id from email_clients ec " +
				" where ec.email_client='"+Constants.UA_TYPE+"' AND ec.user_agent='Browser' ) " +
				" group by o.ua_Family, o.sent_id, o.email_client) as t1 " +
				" group by t1.uaf order by 2 desc ";*/
	//app-3644
	String qry="SELECT t1.uaf as uafamily, count(t1.tot_count) as uafcount FROM " +
			"( SELECT  o.ua_Family as uaf, count(o.ua_Family) as tot_count " +
			" FROM opens o, campaign_sent cs WHERE o.sent_id=cs.sent_id and cs.cr_id=" + crId.longValue() +
			" and o.email_client in("+ecids+") " +
			" group by o.ua_Family, o.sent_id, o.email_client) as t1 " +
			" group by t1.uaf order by 2 desc ";
	logger.info("getBrowserReportByCrId query::"+qry);
	return jdbcTemplate.queryForList(qry);
	
}
    
    public List<Object[]> getClientReportByCrId(Long crId) {
    	
    	return getHibernateTemplate().find("SELECT o.emailClient, o.uaFamily, count(o.emailClient) " + 
									" FROM Opens o, CampaignSent cs WHERE o.sentId=cs.sentId and cs.campaignReport="+crId+" " + 
										" GROUP BY o.emailClient, o.uaFamily ORDER BY 3 DESC");
    }
/*return getHibernateTemplate().find(" select (SELECT emailClient from EmailClient where emailClientId=o.emailClient), count(o.sentId) " +
    			"from Opens o, CampaignSent cs where o.sentId=cs.sentId and cs.campaignReport="+crId+" " +
    					"group by o.emailClient order by count(DISTINCT o.sentId) desc");*/
    	
	 	/*return getHibernateTemplate().find(" select (SELECT emailClient from EmailClient where " +
			"emailClientId=o.emailClient) , count(o.sentId) " +
			"from Opens o where o.sentId in(select sentId from CampaignSent where campaignReport="+crId+") " +
					"group by o.emailClient order by count(DISTINCT o.sentId) desc");*/
    
    
    
    public List<Object[]> getOpenRateByCrId(Long crId, String startDateStr, String endDateStr){
    	
    	if(startDateStr==null || endDateStr== null) {
    		// SELECT substring(time(open_date),1,2) as dd, count(sent_id), count(distinct(sent_id))  FROM opens where sent_id in(select sent_id from campaign_sent where cr_id=235) group by substring(time(open_date),1,2)

    		return getHibernateTemplate().find( "SELECT SUBSTRING(time(o.openDate),1,2), " +
    				"COUNT(o.sentId), COUNT(DISTINCT o.sentId) FROM Opens o, CampaignSent cs " +
    				"WHERE cs.campaignReport="+crId+ " AND " +
    				"o.sentId = cs.sentId " +
    				"GROUP BY SUBSTRING(time(o.openDate),1,2) ORDER BY SUBSTRING(time(o.openDate),1,2)");  
    	
    	}
    	else {

    		return getHibernateTemplate().find( "SELECT SUBSTRING(time(o.openDate),1,2), " +
    				"COUNT(o.sentId), COUNT(DISTINCT o.sentId) FROM Opens o, CampaignSent cs " +
    				"WHERE cs.campaignReport="+crId+ " AND " +
    				"o.sentId = cs.sentId AND " +
    				"o.openDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' " +
    				"GROUP BY SUBSTRING(time(o.openDate),1,2) ORDER BY SUBSTRING(time(o.openDate),1,2)");  
    	}
    }
/*this method gets the opens/clicks reports(dashboard)*/
    public List<Object[]> getOpensByCrId(long crId,String domain,int firstResult, int maxResults, int index) {
    	List opensList=null;
    	String query=null;
    	try {
    	if(index==0) {
    	 query=   "select cs.emailId,o.openDate,cs.status FROM Opens o, CampaignSent cs where o.sentId=cs.sentId and "
						+"o.sentId in (SELECT cc.sentId FROM CampaignSent cc where cc.campaignReport="+crId+") "
						+"and cs.emailId like '%"+ domain +"%' group by cs.emailId "
						+"order by o.openDate desc";
		

			opensList=executeQuery(query,firstResult,maxResults);
			logger.debug("size of the list obj is"+opensList.size());
    	    } else if(index==1) {
    	    	query="select cs.emailId,c.clickDate,c.clickUrl,cs.status FROM Clicks c, CampaignSent cs where c.sentId=cs.sentId and "
					+"c.sentId in (SELECT cc.sentId FROM CampaignSent cc where cc.campaignReport="+crId+") "
					+"and cs.emailId like '%"+ domain +"%' group by cs.emailId "
					+"order by c.clickDate desc";
    	    	opensList=executeQuery(query,firstResult,maxResults);
    			logger.debug("size of the list obj is"+opensList.size());
	
    	    }
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return opensList;
    	
    }

    public List<Object[]> findAllOpensWithNoClient(Set<Long> sentIds) {
    	
    	String sentIdsStr = Utility.getIdsAsString(sentIds);
    	
    	if(sentIdsStr.isEmpty()) return null;
    	
    	String qry = " SELECT DISTINCT cs.emailId, o FROM Opens o, CampaignSent cs WHERE o.sentId IN("+sentIdsStr+")  AND o.sentId=cs.sentId";
    	
    	List<Object[]> opens = executeQuery(qry);
    	
    	if(opens != null && opens.size() > 0) {
    		
    		return opens;
    	}
    	
    	else return null;
    	
    	
    }

    public List<Object[]> findNoOpens(Set<Long> openSentIdsSet) {
    	
    	String sentIdsStr = Utility.getIdsAsString(openSentIdsSet);
    	
    	if(sentIdsStr.isEmpty()) return null;
    	
    	String qry = "SELECT c.sentId,c.clickDate,cs.emailId  FROM Clicks c , CampaignSent cs where c.sentId NOT IN("+sentIdsStr+") AND c.sentId=cs.sentId";
    	
    	List<Object[]> retList = executeQuery(qry);
    	
    	if(retList != null && retList.size() > 0) return retList;
    	else return null;
    	
    }//findNoOpens
    


}
