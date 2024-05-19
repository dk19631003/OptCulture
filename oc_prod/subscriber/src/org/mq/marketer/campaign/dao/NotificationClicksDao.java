package org.mq.marketer.campaign.dao;



import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Clicks;
import org.mq.marketer.campaign.beans.NotificationClicks;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings({"unchecked","serial"})
public class NotificationClicksDao extends AbstractSpringDao implements Serializable {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
    public NotificationClicksDao() {}
    
    private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public NotificationClicks find(Long id) {
        return (NotificationClicks) super.find(NotificationClicks.class, id);
    }

   /* public void saveOrUpdate(Clicks clicks) {
        super.saveOrUpdate(clicks);
    }

    public void delete(Clicks clicks) {
        super.delete(clicks);
    }
*/
    public List findAll() {
        return super.findAll(NotificationClicks.class);
    }
    
    public List<Object[]> getNotificationClickInfoBySentId(Long sentId){
		return getHibernateTemplate().find("select clickUrl,clickDate from Clicks where sentId="+sentId);
	}
    
    public List<Object[]> getNotificationClickList(long crId,Set<String> urls,int requiredUrlCount){
    	
    	List<Object[]> clickList = new ArrayList<Object[]>();
    	List<Object[]> list;
		
    	try {
			int count = 0;
			String queryString ;
			//= "SELECT   count(distinct sentId), count(sentId) FROM Clicks where clickUrl=? 
			//sentId in (SELECT sentId FROM CampaignSent where crId=? and clicks>0)";
			
/*			for(String url:urls) {
				
				queryString = 
					" SELECT '"+url+"' , COUNT(DISTINCT sentId), COUNT(sentId) FROM Clicks " +
					" WHERE clickUrl='"+url+"' AND sentId IN" +
					" (SELECT sentId FROM CampaignSent WHERE campaignReport="+crId+" AND clicks>0)";
				
				list = getHibernateTemplate().find(queryString);
				if(list.size() > 0){
					clickList.add(list.get(0));
				}
				if(++count == requiredUrlCount)
					break;	
			}
*/			

				
/*			queryString = 
				" SELECT clickUrl , COUNT(DISTINCT sentId), COUNT(sentId) FROM Clicks " +
				" WHERE sentId IN" +
				" (SELECT sentId FROM CampaignSent WHERE campaignReport="+crId+" AND clicks>0) group by clickUrl";
*/

			queryString = 
					" SELECT clickUrl , COUNT(DISTINCT sentId), COUNT(sentId) FROM Clicks WHERE crId="+crId+" GROUP BY clickUrl ";

				
				list = getHibernateTemplate().find(queryString);

				for (Object[] obj : list) {
					
					clickList.add(obj);
					if(++count == requiredUrlCount) break;	
				} // for obj
			
		} catch (Exception e) {
			logger.error("**Exception: Problem has occured while getting click list. "+ e +"**");
		}
		return clickList;
    }
    
    public Long getTotClickCount(Long crId,String type){
    	if(type.equals("total"))
    		//return (Long)getHibernateTemplate().find("select count(clickUrl) from Clicks where sentId in (select sentId from CampaignSent where campaignReport=" + crId +")").get(0);
    		return (Long)getHibernateTemplate().find("select count(clickUrl) from NotificationClicks where notificationCrId=" + crId ).get(0);
    	
    	else {
    		//return (Long)getHibernateTemplate().find("select count(distinct sentId, clickUrl) from Clicks where sentId in (select sentId from CampaignSent where campaignReport="+crId+")").get(0);
		//return (Long)getHibernateTemplate().find("select count(distinct cl.sentId) from Clicks as cl where cl.sentId in (select cs.sentId from CampaignSent as cs where cs.campaignReport="+crId+")").get(0);
    	
    		String qry="select clickUrl, count(distinct sentId) from NotificationClicks where notificationCrId ="+ crId +" group by clickUrl ";
    		
    		// "select count(distinct cl.sentId, cl.clickUrl) from Clicks as cl,  CampaignSent cs where cl.sentId=cs.sentId AND cs.clicks > 0 AND cs.campaignReport="+crId+")"
    		List tempList = getHibernateTemplate().find(qry);
    		Object urlAry[];
    		long tot=0;
    		for (Object object : tempList) {
				urlAry = (Object[])object;
    			logger.info("List="+urlAry[0] +"  , "+urlAry[1]);
    			try {
					tot += Long.parseLong(urlAry[1].toString());
				} catch (Exception e) {
					logger.error("Exception ::" , e);
				}
			} // for
    		return tot;
    	}
    }
    
    public List<Object[]> getClickRateByCrId(Long crId, String startDateStr, String endDateStr) {
    	
    	if(startDateStr==null || endDateStr== null) {
    		return getHibernateTemplate().find("SELECT SUBSTRING(time(clickDate),1,2)," +
    				" COUNT(sentId),COUNT(DISTINCT sentId) FROM NotificationClicks WHERE sentId IN" +
    				"(SELECT sentId FROM NotificationCampaignSent WHERE notificationCampaignReport="+crId+") " +
    						"GROUP BY SUBSTRING(time(clickDate),1,2))");  
    	} else {
    		return getHibernateTemplate().find("SELECT SUBSTRING(time(clickDate),1,2), " +
    				"COUNT(sentId),COUNT(DISTINCT sentId) FROM NotificationClicks WHERE sentId IN" +
    				"(SELECT sentId FROM NotificationCampaignSent WHERE notificationCampaignReport="+crId+")AND " +
    						"clickDate BETWEEN '"+startDateStr+"' AND '"+endDateStr+"' " +
    								"GROUP BY SUBSTRING(time(clickDate),1,2))");
    	}
    }
    
    
public List<Object[]> getUrlAndClickList(Long crId){
    	
	 long starttime = System.currentTimeMillis();
    	List<Object[]> clickList = new ArrayList<Object[]>();
    	List<Object[]> list;
		
    	try {
			
			String queryString ;
			

			queryString = 
					" SELECT clickUrl , COUNT(clickId) FROM Clicks WHERE crId="+crId+" GROUP BY clickUrl ";

				
				list = getHibernateTemplate().find(queryString);

				for (Object[] obj : list) {
					
					clickList.add(obj);
				} 
			
		} catch (Exception e) {
			logger.error("**Exception: Problem has occured while getting click list. "+ e +"**");
		}
    	/*logger.debug("total time taken :: ClicksDao :: getUrlAndClickList   "
				+ (System.currentTimeMillis() - starttime));*/
		return clickList;
    }

	
	public List<Object[]> getClickedUrls(StringBuffer crIds) {
		
		if(crIds==null|| crIds.toString().trim().length()==0)
		{
			logger.info("crids :"+crIds);
		return null;
		}
		long starttime = System.currentTimeMillis();
		List<Object[]> tempUrlLists = null;
		
		/*String query = "SELECT distinct cu.cr_id, cu.url, click_count FROM campaign_urls cu left outer join"
				+ " (SELECT cr_id, click_Url , COUNT(click_Id) as click_count "+
													" FROM clicks WHERE cr_Id in ( "+crIds.toString()+" ) GROUP BY cr_id, click_Url ) "
															+ "as derived on derived.click_Url= cu.url and derived.cr_id=cu.cr_id where cu.cr_id in( "+crIds.toString()+" ) order by 1 desc";//and clickDate between '"+ fromDateStr + "' AND '" + toDateStr + "'");
		*/
		
		
		/*String query = " SELECT  c.cr_id, cu.click_Url, COUNT(click_Id) FROM campaign_report c ,"
						+ " clicks cl WHERE c.cr_id=cl.cr_id  AND c.cr_Id in ( "+crIds.toString()+" ) GROUP BY c.cr_id, cl.click_Url " ;
		
		*/
		
		String query = " SELECT  cr_id, click_Url, COUNT(click_Id) FROM "
				+ " clicks cl WHERE cr_Id in ( "+crIds.toString()+" ) GROUP BY cr_id, cl.click_Url " ;

       logger.info(" url query:"+query);
		/*String query = "SELECT c.cr_id, click_Url , COUNT(click_Id) as click_count FROM clicks c  left outer join " +
						"(select * from campaign_urls where cr_id in ( "+crIds.toString()+" ) )  as inne on inne.url= c.click_url "+
							"WHERE c.cr_Id in ( "+crIds.toString()+" ) GROUP BY c.cr_id, c.click_Url";*/
		
		tempUrlLists	= jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException  {
				 
				 Object[] object =  new Object[3];
				 try {
					 object[0] = rs.getLong(1);
					 object[1] = Utility.decodeUrl(rs.getString(2));
					 object[2] = rs.getLong(3);
					 
					return object;
				} catch (EncoderException e) {
					// TODO Auto-generated catch block
					logger.error("Not encoded properly");
				} catch (DecoderException e) {
					// TODO Auto-generated catch block
					logger.error("Not decoded properly");
				}
				 return object;
			 }
			
		});
		logger.debug("query "+query+" total time taken :: ClicksDao :: getClickedUrls   "
				+ (System.currentTimeMillis() - starttime));
		/*if(tempUrlLists != null && tempUrlLists.size() >0)
		{*/
			return tempUrlLists;
		/*}
		
		return null;*/
	}
	public List<Object[]> getCampUrls(String crIds) {
		List<Object[]> tempUrlLists = null;
		
		String query = "SELECT cr_id , url FROM campaign_urls WHERE cr_id IN("+crIds+")";
		logger.info("====URL ====="+query);
		if(crIds==null|| crIds.toString().trim().length()==0)
		{
			logger.info("crids :"+crIds);
		return null;
		}
		
		tempUrlLists	= jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object[] object =  new Object[3];
				 
				 try {
					 object[0] = rs.getLong(1);
					 object[1] = Utility.decodeUrl(rs.getString(2));
					 
				} catch (EncoderException e) {
					logger.error("Not encoded properly");
				} catch (DecoderException e) {
					// TODO Auto-generated catch block
					logger.error("Not decoded properly");
				}
				 return object;
			 }
			
		});
		return tempUrlLists;
	}
	public List<String> getCampaignUrls(Long crId) {
		
	long starttime = System.currentTimeMillis();
	List<String> campUrlLists = null;
	
		//campUrlLists = getHibernateTemplate().find("SELECT distinct url FROM campaign_urls WHERE cr_id="+crId
		String qry="SELECT distinct url FROM campaign_urls WHERE cr_id="+crId;
		
		//logger.debug("see this query "+qry);
//		campUrlLists =getJdbcTemplate().q
		
		campUrlLists =getJdbcTemplate().query(qry, new RowMapper() {			
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				//logger.debug("see this query inside row mapper  ");
				String url=new String(rs.getString("url"));
				return url;
			}
		});
		
		if(campUrlLists!=null && campUrlLists.size()>0) {
			/*logger.debug("total time taken :: ClicksDao :: getCampaignUrls   "
					+ (System.currentTimeMillis() - starttime));*/
			return campUrlLists;
		}
		
		logger.debug("total time taken :: ClicksDao :: getCampaignUrls   "
				+ (System.currentTimeMillis() - starttime));
		return null;
		
	}
	
	public List<Object[]> getClicksCount(Long crId){
		
		 long starttime = System.currentTimeMillis();
	   	List<Object[]> clickList = new ArrayList<Object[]>();
	   	List<Object[]> list;
			
	   	try {
				
				String queryString ;
				

				queryString = 
						" SELECT COUNT(clickId) FROM NotificationClicks WHERE notificationCrId="+crId+" ";
				
				logger.info("Clicks count Ravali"+queryString);

					
					list = getHibernateTemplate().find(queryString);

					for (Object[] obj : list) {
						
						clickList.add(obj);
					} 
				
			} catch (Exception e) {
				logger.error("**Exception: Problem has occured while getting click list. "+ e +"**");
			}
	   	/*logger.debug("total time taken :: ClicksDao :: getUrlAndClickList   "
					+ (System.currentTimeMillis() - starttime));*/
			return clickList;
	   }
	
public int getCount(Long notificationCampRepId) {
    	
    	String qry = "SELECT count(clickId) FROM NotificationClicks where notificationCrId="+notificationCampRepId+"";
    	
    	logger.info("Unique click count"+qry);
		int count = ((Long)executeQuery(qry).get(0)).intValue();	
		return count;
    }

public List<Object[]> getClickList(long crId,String urls,int requiredUrlCount){
	
	List<Object[]> clickList = new ArrayList<Object[]>();
	List<Object[]> list;
	
	try {
		int count = 0;
		String queryString ;

		queryString = 
				" SELECT clickUrl , COUNT(DISTINCT sentId), COUNT(sentId) FROM NotificationClicks WHERE notificationCrId="+crId+" GROUP BY clickUrl ";

			
			list = getHibernateTemplate().find(queryString);

			for (Object[] obj : list) {
				
				clickList.add(obj);
				if(++count == requiredUrlCount) break;	
			} // for obj
		
	} catch (Exception e) {
		logger.error("**Exception: Problem has occured while getting click list. "+ e +"**");
	}
	return clickList;
}

public List<Object[]> getClickInfoBySentId(Long sentId)throws Exception{
	return getHibernateTemplate().find("select clickUrl,clickDate from NotificationClicks where sentId="+sentId);
}


    
}



