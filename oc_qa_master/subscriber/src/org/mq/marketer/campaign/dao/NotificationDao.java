package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Notification;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public NotificationDao(){}
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@SuppressWarnings("unchecked")
    public List<Notification> findAll() {
        return super.findAll(Notification.class);
    }
	
    @SuppressWarnings("unchecked")
    public List<Notification> findAllByUser(Long userId) {
    	try {
			return executeQuery("from Notification where userId=" + userId);
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
    
    /**
     * this method checks whether the given notification name is already exists or not.
     * @param notificationCampName specifies the given notification name.
     * @param userId specifies the logged in user id.
     * @return a boolean variable true if name already exists false if not.
     */
    @SuppressWarnings("unchecked")
    public boolean checkName(String notificationName, Long userId){
    	try{
    		 List<Notification> list = executeQuery("From Notification where notificationName = '" + notificationName + "' and userId= " + userId);
    		
			if(list.size()>0){
				return true;
			}else{
				return false;
			}
    	}catch (Exception e) {
			// TODO: handle exception
    		logger.error("execption while verifying the notification campaign name",e);
    		return false;
		}
    }
    
 @SuppressWarnings("unchecked")  
 public Notification findByCampaignId(Long notificationId) {
    	
    	
		List<Notification> notifyList =  executeQuery(
    			" FROM Notification WHERE notificationId = "+notificationId);
    	
    	if(notifyList == null || notifyList.size() == 0) {
    		return null;
    	}
    	else {
    		return notifyList.get(0);
    	}
    }

public int getCount(Long userId, String status) {
try{
 		
 		String query = null;
 		//String userIdsStr = Utility.getUserIdsAsString(userIds);
 		
 		if(status.equals("All")) {
 			query=" SELECT count(*) FROM Notification WHERE userId IN ( "+userId+" ) ";
 		}
 		else {	
 			query=" SELECT count(*) FROM Notification WHERE userId IN ( "+userId+" ) and status LIKE '" + status + "'" ;
 		}
 		
 		List list = executeQuery(query);
 		if(list.size()>0)
 			return ((Long)list.get(0)).intValue();
 		else
 			return 0;
 	}catch(Exception e){
 		logger.error("**Exception :"+e+"**");
 		return 0;
 	}
}

@SuppressWarnings("unchecked")
public List<Notification> getCampaignsByCampaignName(String campaignName,Long userId, int firstResult, int maxResult,String orderby_colName,String desc_Asc) {
	String query = null;
	List<Notification> campList = null;
	try{
		
		query="FROM Notification WHERE userId IN ( "+userId+" ) and notificationName like '"+"%"+campaignName+"%"+"' order by "+orderby_colName+" "+ desc_Asc;
		campList = executeQuery(query, firstResult, maxResult);
	}catch(Exception e) {
		logger.error("exception while retrieving campaign list",(Throwable)e);
	}
	logger.info("inside getCampaignsByCampaignName() campList.size() = "+campList.size());
	return campList;
}

@SuppressWarnings("unchecked")
public List<Notification> getCampaignsBetweenCreationDates(String fromDateString, String toDateString, Long userId, int firstResult, int maxResult,String orderby_colName,String desc_Asc) throws Exception{
	String query = null;
	List<Notification> campList = null;
	try{
		query="FROM Notification WHERE userId IN ( "+userId+" ) and createdDate between '"+fromDateString+"' AND '"+toDateString+"' order by "+orderby_colName+" "+  desc_Asc;
		logger.info(query);
		campList=executeQuery(query, firstResult, maxResult);
	}
	catch(Exception e) {
		logger.error("exception while retrieving campaign list",(Throwable)e);
	}
	return campList;
}

@SuppressWarnings("unchecked")
public List<Notification> getCampaignsByStatus(Long userId,  int firstResult, int maxResult,String orderby_colName,String desc_Asc) throws Exception{
		String query=null;
		List<Notification> campList=null;
		try{
			query="FROM Notification WHERE userId in (" + userId +" ) order by "+orderby_colName+" "+  desc_Asc;
		    campList=executeQuery(query, firstResult, maxResult);
	    }catch(Exception e) {
	    	  logger.error("exception while retrieving campaign list",(Throwable)e);
	      }
	   return campList;
	}

 @SuppressWarnings("unchecked")
public List<Notification> getNotificationCampaignsByStatus(Long userId, String status, int firstResult, int count){
		String query=null;
		List<Notification> notificationCampList=null;
		try{
		if(status.equals("All")){
			logger.debug("just entered to fetch all the notification campaigns");
			query="from Notification where userId=" + userId+" order by modifiedDate desc";
		  }else {	
			query="from Notification where userId=" + userId+ " and status like '" + status + "' order by modifiedDate desc" ;
		  }
		notificationCampList=executeQuery(query, firstResult, count);
		
		
	    }catch(Exception e) {
	    	  logger.error("exception while retrieving campaign list",(Throwable)e);
	    	  return null;
	      }
	
	return notificationCampList;
	
	}

 public int getCountByCampaignName(String campaignName, Long userId) {
		
			try{
 		
 		      String query = null;
 	
 		      query=" SELECT count(*) FROM Notification WHERE userId IN ( "+userId+" ) and notificationName LIKE '%"+campaignName+"%'" ;
 		
 		
 		      List list = executeQuery(query);
 		    if(list.size()>0)
 			     return ((Long)list.get(0)).intValue();
 		   else
 			    return 0;
 	      }catch(Exception e){
 		        logger.error("**Exception :"+e+"**");
 		        return 0;
 	}
		
	
		
		
	}

public int getCountByCreationDate(String fromDateString, String toDateString, Long userId){
		String query = null;
		List<Notification> campList = null;
		try{
			query=" SELECT COUNT(*) FROM Notification WHERE userId IN ( "+userId+" ) and createdDate between '"+fromDateString+"' AND '"+toDateString+"'";
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
    
}
