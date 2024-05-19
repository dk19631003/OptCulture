package org.mq.marketer.campaign.dao;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class SMSCampaignsDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public SMSCampaignsDaoForDML(){}
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(SMSCampaigns smsCampaigns) {
        super.saveOrUpdate(smsCampaigns);
    }

    public void delete(SMSCampaigns smsCampaigns) {
        super.delete(smsCampaigns);
    }

    /*public List findAll() {
        return super.findAll(SMSCampaigns.class);
    }
	
    
    public List<SMSCampaigns> findAllByUser(Long userId) {
    	
    	
    	try {
			return getHibernateTemplate().find("from SMSCampaigns where users=" + userId);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    	
    }
    
    
    /**
     * this methos checks whether the given smsCampaign name is already exists or not.
     * @param smsCampName specifies the given smscampaign name.
     * @param userId specifies the logged in user id.
     * @return a boolean variable true if name already exists false if not.
     */
    /*public boolean checkName(String smsCampName, Long userId){
    	try{
    		 List list = getHibernateTemplate().find("From SMSCampaigns where smsCampaignName = '" + smsCampName + "' and users= " + userId);
    		
			if(list.size()>0){
				return true;
			}else{
				return false;
			}
    	}catch (Exception e) {
			// TODO: handle exception
    		logger.error("execption while verifying the sms campaign name",e);
    		return false;
		}
    }
    
    public List<SMSCampaigns> getSmsCampaignsByStatus(Long userId, String status, int firstResult, int count){
		String query=null;
		String qry=null;
		List<SMSCampaigns> SmsCampList=null;
		try{
		if(status.equals("All")){
			logger.debug("just entered to fetch all the sms campaigns");
			query="from SMSCampaigns where users=" + userId+" order by modifiedDate desc";
		  }else {	
//			  "SELECT  new MailingList(m.listId,m.listName," +
//				"(select count(c.mailingList) from Contacts c where m.listId=c.mailingList)," +
//				"(SELECT COUNT(purged) FROM Contacts c1 WHERE m.listId=c1.mailingList and (purged=false))"+
//				",m.description,m.custField, m.lastModifiedDate,m.users) FROM MailingList m  " +
//				"where m.users=" + userId + " order by m.createdDate";
			//qry="select new Campaigns(c.campaignName,(select m.listName from MailingList m where m.listId in()))"
			query="from SMSCampaigns where users=" + userId+ " and status like '" + status + "' order by modifiedDate desc" ;
		  }
		SmsCampList=executeQuery(query, firstResult, count);
		
		
	    }catch(Exception e) {
	    	  logger.error("exception while retrieving campaign list",(Throwable)e);
	    	  return null;
	      }
	
	return SmsCampList;
	
	}*/
    
    
    public void deleteByCollection(Collection list){
		getHibernateTemplate().deleteAll(list);
	}
    
    
    
 /*public SMSCampaigns findByCampaignId(Long smsCampaignId) {
    	
    	List<SMSCampaigns> campList =  getHibernateTemplate().find(
    			" FROM SMSCampaigns WHERE smsCampaignId = "+smsCampaignId);
    	
    	if(campList == null || campList.size() == 0) {
    		return null;
    	}
    	else {
    		return campList.get(0);
    	}
    }
    
 /**
  * added for EventTrigger sms_campaign feature
  * @param smsId
  * @param userId
  * @return
  */
 /*public SMSCampaigns getSmsCampaignBySmsId(Long smsId, Long userId){
 
 	try{
 		return (SMSCampaigns)getHibernateTemplate().find("FROM SMSCampaigns WHERE smsCampaignId = "+smsId+" AND users = "+userId).get(0);
 	}
 	catch(Exception e) {
 		
 		logger.debug(" **Exception while getting sms campiagn from DB ");
 		return null;
 	}
 }*/
 
 public int deleteBySmsCampaignId(String smsCampaignId) {
		return getHibernateTemplate().bulkUpdate(
				"DELETE FROM SMSCampaigns WHERE smsCampaignId in("+smsCampaignId+")");
	}
 
 public int deleteByCampaignIdFromIntermediateTable(String smsCampaignId) {
 	
 	String qry = "DELETE FROM mlists_sms_campaigns WHERE sms_campaign_id in("+smsCampaignId+")";
 	return getJdbcTemplate().update(qry);
 	
 }
 
 
 
 /*public int getCount(Long userId, String status) {
 	try{
 		
 		String query = null;
 		//String userIdsStr = Utility.getUserIdsAsString(userIds);
 		
 		if(status.equals("All")) {
 			query=" SELECT count(*) FROM SMSCampaigns WHERE users IN ( "+userId+" ) ";
 		}
 		else {	
 			query=" SELECT count(*) FROM SMSCampaigns WHERE users IN ( "+userId+" ) and status LIKE '" + status + "'" ;
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
 public SMSCampaigns findIfSegmentAssociates(String searchStr){
 	
 	String hql = "FROM SMSCampaigns WHERE listType='"+searchStr+"'";
 	
 	List<SMSCampaigns> segAssocitedCampList = getHibernateTemplate().find(hql);
 	
 	if(segAssocitedCampList.size() > 0) {
 		
 		return (SMSCampaigns)segAssocitedCampList.get(0);
 		
 	}
 	
 	return null;
 }
 
 public List<SMSCampaigns> getCampaignsByCampaignName(String campaignName,Long userId, int firstResult, int maxResult,String orderby_colName,String desc_Asc) throws Exception{
		String query = null;
		List<SMSCampaigns> campList = null;
		try{
			
			query="FROM SMSCampaigns WHERE users IN ( "+userId+" ) and smsCampaignName like '"+"%"+campaignName+"%"+"' order by "+orderby_colName+" "+ desc_Asc;
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
 	
 		      query=" SELECT count(*) FROM SMSCampaigns WHERE users IN ( "+userId+" ) and smsCampaignName LIKE '%"+campaignName+"%'" ;
 		
 		
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
	
	public List<SMSCampaigns> getCampaignsBetweenCreationDates(String fromDateString, String toDateString, Long userId, int firstResult, int maxResult,String orderby_colName,String desc_Asc) throws Exception{
		String query = null;
		List<SMSCampaigns> campList = null;
		try{
			query="FROM SMSCampaigns WHERE users IN ( "+userId+" ) and createdDate between '"+fromDateString+"' AND '"+toDateString+"' order by "+orderby_colName+" "+  desc_Asc;
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
		List<SMSCampaigns> campList = null;
		try{
			query=" SELECT COUNT(*) FROM SMSCampaigns WHERE users IN ( "+userId+" ) and createdDate between '"+fromDateString+"' AND '"+toDateString+"'";
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
	public List<SMSCampaigns> getCampaignsByStatus(Long userId,  int firstResult, int maxResult,String orderby_colName,String desc_Asc) throws Exception{
		String query=null;
		List<SMSCampaigns> campList=null;
		try{
			query="FROM SMSCampaigns WHERE users in (" + userId+" ) order by "+orderby_colName+" "+  desc_Asc;
		    campList=executeQuery(query, firstResult, maxResult);
	    }catch(Exception e) {
	    	  logger.error("exception while retrieving campaign list",(Throwable)e);
	      }
	   return campList;

	}*/
    
}
