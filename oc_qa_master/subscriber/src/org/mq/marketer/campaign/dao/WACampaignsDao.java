package org.mq.marketer.campaign.dao;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.WACampaign;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class WACampaignsDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public WACampaignsDao(){}
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public List findAll() {
        return super.findAll(WACampaign.class);
    }
	
    
    @SuppressWarnings("unchecked")
	public List<WACampaign> findAllByUser(Long userId) {
    	
    	
    	try {
			return getHibernateTemplate().find("from WACampaign where users=" + userId);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
    
    public boolean checkName(String waCampName, Long userId){
    	try{
    		 List list = getHibernateTemplate().find("From WACampaign where waCampaignName = '" + waCampName + "' and users= " + userId);
    		
			if(list.size()>0){
				return true;
			}else{
				return false;
			}
    	}catch (Exception e) {
			// TODO: handle exception
    		logger.error("execption while verifying the wa campaign name",e);
    		return false;
		}
    }
    
    public List<WACampaign> getWACampaignsByStatus(Long userId, String status, int firstResult, int count){
		String query=null;
		String qry=null;
		List<WACampaign> WaCampList=null;
		try{
		if(status.equals("All")){
			logger.debug("just entered to fetch all the wa campaigns");
			query="from WACampaign where users=" + userId+" order by modifiedDate desc";
		  }else {	
			query="from WACampaign where users=" + userId+ " and status like '" + status + "' order by modifiedDate desc" ;
		  }
		WaCampList=executeQuery(query, firstResult, count);
		
		
	    }catch(Exception e) {
	    	  logger.error("exception while retrieving campaign list",(Throwable)e);
	    	  return null;
	      }
	
	return WaCampList;
	
	}
    
    
    
 public WACampaign findByCampaignId(Long waCampaignId) {
    	
    	List<WACampaign> campList =  getHibernateTemplate().find(
    			" FROM WACampaign WHERE waCampaignId = "+waCampaignId);
    	
    	if(campList == null || campList.size() == 0) {
    		return null;
    	}
    	else {
    		return campList.get(0);
    	}
    }
    
 public WACampaign getWACampaignByWAId(Long waId, Long userId){
 
 	try{
 		return (WACampaign)getHibernateTemplate().find("FROM WACampaign WHERE waCampaignId = "+waId+" AND users = "+userId).get(0);
 	}
 	catch(Exception e) {
 		
 		logger.debug(" **Exception while getting WA campiagn from DB ");
 		return null;
 	}
 }
 
 
 
 public int getCount(Long userId, String status) {
 	try{
 		
 		String query = null;
 		//String userIdsStr = Utility.getUserIdsAsString(userIds);
 		
 		if(status.equals("All")) {
 			query=" SELECT count(*) FROM WACampaign WHERE users IN ( "+userId+" ) ";
 		}
 		else {	
 			query=" SELECT count(*) FROM WACampaign WHERE users IN ( "+userId+" ) and status LIKE '" + status + "'" ;
 		}
 		
 		List list = getHibernateTemplate().find(query);
 		if(list.size()>0)
 			return ((Long)list.get(0)).intValue();
 		else
 			return 0;
 	}catch(Exception e){
 		logger.error("**Exception :"+e+"**");
 		return 0;
 	}
 }
 public List<WACampaign> findIfSegmentAssociates(String searchStr, Long segId){
	 String hql = "FROM WACampaign WHERE  listType like '"+searchStr+"' OR listType like 'S%:"+segId+",%' OR listType like 'S%,"+segId+",%' OR listType like 'S%,"+segId+"' ";

 	logger.info("hql query"+hql);
 	
 	List<WACampaign> segAssocitedWACampList = executeQuery(hql);
 	
 	if(segAssocitedWACampList.size() > 0) {
 		
 		return (List<WACampaign>)segAssocitedWACampList;

 	}
 	
 	return null;
 }
 
 public List<WACampaign> getCampaignsByCampaignName(String campaignName,Long userId, int firstResult, int maxResult,String orderby_colName,String desc_Asc) throws Exception{
		String query = null;
		List<WACampaign> campList = null;
		try{
			
			query="FROM WACampaign WHERE users IN ( "+userId+" ) and waCampaignName like '"+"%"+campaignName+"%"+"' order by "+orderby_colName+" "+ desc_Asc;
			campList = executeQuery(query, firstResult, maxResult);
		}catch(Exception e) {
			logger.error("exception while retrieving campaign list",(Throwable)e);
		}
		logger.info("inside getCampaignsByCampaignName() campList.size() = "+campList.size());
		return campList;
	}
	
	public int getCountByCampaignName(String campaignName, Long userId) {
		
			try{
 		
 		      String query = null;
 	
 		      query=" SELECT count(*) FROM WACampaign WHERE users IN ( "+userId+" ) and waCampaignName LIKE '%"+campaignName+"%'" ;
 		
 		
 		      List list = getHibernateTemplate().find(query);
 		    if(list.size()>0)
 			     return ((Long)list.get(0)).intValue();
 		   else
 			    return 0;
 	      }catch(Exception e){
 		        logger.error("**Exception :"+e+"**");
 		        return 0;
 	}
		
	
		
		
	}
	
	public List<WACampaign> getCampaignsBetweenCreationDates(String fromDateString, String toDateString, Long userId, int firstResult, int maxResult,String orderby_colName,String desc_Asc) throws Exception{
		String query = null;
		List<WACampaign> campList = null;
		try{
			query="FROM WACampaign WHERE users IN ( "+userId+" ) and createdDate between '"+fromDateString+"' AND '"+toDateString+"' order by "+orderby_colName+" "+  desc_Asc;
			logger.info(query);
			campList=executeQuery(query, firstResult, maxResult);
		}
		catch(Exception e) {
			logger.error("exception while retrieving campaign list",(Throwable)e);
		}
		return campList;
	}
	
	public int getCountByCreationDate(String fromDateString, String toDateString, Long userId){
		String query = null;
		List<WACampaign> campList = null;
		try{
			query=" SELECT COUNT(*) FROM WACampaign WHERE users IN ( "+userId+" ) and createdDate between '"+fromDateString+"' AND '"+toDateString+"'";
			logger.info(query);
			List list = executeQuery(query);
    		if(list.size()>0)
    			return ((Long)list.get(0)).intValue();
    		else
    			return 0;
		}
		catch(Exception e) {
			logger.error("exception while retrieving campaign list",(Throwable)e);
		}
		return campList.size();
	}
	public List<WACampaign> getCampaignsByStatus(Long userId,  int firstResult, int maxResult,String orderby_colName,String desc_Asc) throws Exception{
		String query=null;
		List<WACampaign> campList=null;
		try{
			query="FROM WACampaign WHERE users in (" + userId+" ) order by "+orderby_colName+" "+  desc_Asc;
		    campList=executeQuery(query, firstResult, maxResult);
	    }catch(Exception e) {
	    	  logger.error("exception while retrieving campaign list",(Throwable)e);
	      }
	   return campList;

	}
	 public List<WACampaign> getWACampaignsByUserIDStatus(Long userId, String status){
		 String hql = "FROM WACampaign WHERE users IN ( "+userId+" ) and status in ('" + status + "','Running')" ;

		 	logger.info("hql query"+hql);
		 	
		 	List<WACampaign> activeWACampList = executeQuery(hql);
		 	
		 	if(activeWACampList.size() > 0) {
		 		
		 		return (List<WACampaign>)activeWACampList;

		 	}
		 	
		 	return null;
		 }
	
}
