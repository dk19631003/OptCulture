package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.NotificationCampaignReport;
import org.mq.marketer.campaign.beans.NotificationCampaignSent;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class NotificationCampaignSentDao extends AbstractSpringDao{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public NotificationCampaignSentDao() {}
  
    public NotificationCampaignSent find(Long id) {
        return (NotificationCampaignSent) super.find(NotificationCampaignSent.class, id);
    }


    public List findAll() {
        return super.findAll(NotificationCampaignSent.class);
    }
	
    
    public int getCount(Long notificationCampRepId, String status) {
    	
    	String qry = "SELECT count(sentId) FROM NotificationCampaignSent where notificationCampaignReport="+notificationCampRepId+" AND status IN("+status+")";
		int count = ((Long)executeQuery(qry).get(0)).intValue();	
		return count;
    }
    
 public List<NotificationCampaignSent> getNotificationByContactId(Long userId,String instanceId,String mobileNumber,Calendar fromCal,Calendar toCal,int firstResult,int maxResults) {
    	try {
    		
    		String fromStr = MyCalendar.calendarToString(fromCal, MyCalendar.FORMAT_DATETIME_STYEAR);
			String toStr = MyCalendar.calendarToString(toCal, MyCalendar.FORMAT_DATETIME_STYEAR);
			
			/*
			 * String newQuery =
			 * "FROM NotificationCampaignSent ncs WHERE ncs.notificationCampaignReport.userId = '"
			 * +userId+"'" + " AND ncs.instanceId = '"+instanceId+"' AND ncs.mobileNumber='"
			 * +mobileNumber+"' AND " +
			 * " ncs.notificationCampaignReport.sentDate BETWEEN '"+fromStr+"' AND '"
			 * +toStr+"' " +
			 * " ORDER BY ncs.notificationCampaignReport.notificationCrId DESC";
			 */
			
			String newQuery = 
					  " FROM NotificationCampaignSent ncs "
					+ " WHERE ncs.contactId IN "
					+ " (SELECT co.contactId from Contacts co WHERE co.contactId IN "
					+ " (SELECT cl.contact.contactId from ContactsLoyalty cl WHERE cl.userId = '"+userId+"' and cl.mobilePhone = '"+mobileNumber+"') "
					+ " AND co.instanceId IS NOT NULL AND co.deviceType IS NOT NULL ) "
					+ " AND ncs.notificationCampaignReport.sentDate BETWEEN '"+fromStr+"' AND '"+toStr+"' " 
					//+ " AND ncs.status = 'Sent'"
					+ " ORDER BY 1 DESC";
			
	    	List<NotificationCampaignSent> notificationCampaignSentList =  executeQuery(newQuery, firstResult, maxResults);	
			return notificationCampaignSentList;
    	}catch (Exception e) {
    		logger.error("** Error : " + e.getMessage() + " **");
		}
		return null;
    }
 
 
 public String getNotificationUnreadCountByContactId(String mobileNumber,Long userId,Calendar fromCal,Calendar toCal) {
 	try {
 		
 			String fromStr = MyCalendar.calendarToString(fromCal, MyCalendar.FORMAT_DATETIME_STYEAR);
			String toStr = MyCalendar.calendarToString(toCal, MyCalendar.FORMAT_DATETIME_STYEAR);
 		
			String newQuery = "SELECT COUNT(*) FROM NotificationCampaignSent ncs WHERE ncs.contactId IN "
					+ " (SELECT co.contactId from Contacts co WHERE co.contactId IN "
					+ " (SELECT cl.contact.contactId from ContactsLoyalty cl WHERE userId = '"+userId+"' and mobilePhone = '"+mobileNumber+"')"
					+ " AND co.instanceId IS NOT NULL AND co.deviceType IS NOT NULL) "
					+ " AND ncs.notificationCampaignReport.sentDate BETWEEN '"+fromStr+"' AND '"+toStr+"'"
					//+ " AND ncs.status = 'Sent' "
					+ " AND ( ncs.notificationRead is null or ncs.notificationRead = false)";
			
	    	String notificationUnreadCount =  executeQuery(newQuery).toString();	
			return notificationUnreadCount;
 	}catch (Exception e) {
 		logger.error("** Error : " + e.getMessage() + " **");
		}
		return null;
 }
    
    public List<Object[]> getAllMobileNumsByCrId(Long notificationCrId,int firstResult,int maxResults) {
		  try {
			  String query = "select sentId,mobileNumber,opens,clicks,status from NotificationCampaignSent as cs where  cs.notificationCampaignReport =" + notificationCrId;

			  List cList = executeQuery(query, firstResult, maxResults);
			  logger.info("list is---->"+cList+" "+cList.size());
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }
    
    public List<Object[]> getClickedMobilesByCrId(Long crId,int firstResult,int maxResults) {
		  try {
			  String query = "select cs.sentId,cs.mobileNumber,cs.clicks,c.clickUrl from NotificationCampaignSent as cs, NotificationClicks  c where  "
			  		+ "cs.notificationCampaignReport =" + crId +" and cs.clicks>0 and cs.sentId=c.sentId and c.clickDate=(SELECT max(ck.clickDate) "
			  				+ "FROM NotificationClicks ck where ck.sentId=cs.sentId) group by cs.mobileNumber  order by cs.clicks desc";
			  	
			  logger.info("clicks by mobileno query"+query);
			  List cList = executeQuery(query, firstResult, maxResults);
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }
	
	public List<String> getUndeliveredResonsByCampReportId(Long notificationCampReportId) {
		List<String> reasons = null;
		try {
			
			String query = "SELECT distinct(status) from NotificationCampaignSent where notificationCampaignReport ="+notificationCampReportId+
							" and status not in('" + Constants.SMS_CAMPAIGN_SUCCESS + "')";
			reasons = executeQuery(query);
			reasons.add(0, "All");
				return reasons;
			
		} catch (Exception e) {
			logger.error("Exception :::",e);
			return null;
		}
	}
	
	public List<NotificationCampaignSent> getCampSentByUndeliveredCategory(Long notificationCampReportId,String status) {
		
		List<NotificationCampaignSent> notificationCampSentList = null;
		String query ="";
		try {
			if(status.equalsIgnoreCase(Constants.SMS_CAMPAIGN_SENT_STATUS_ALL)){
				query = "from NotificationCampaignSent where notificationCampaignReport ="+notificationCampReportId+
							" and status not in('" + Constants.SMS_CAMPAIGN_SUCCESS + "')";
			}else {
				
				query = " from NotificationCampaignSent where notificationCampaignReport ="+notificationCampReportId+
						" and status = '"+status+"'";
				
			}
			
			notificationCampSentList = executeQuery(query);
				return notificationCampSentList;
			
		} catch (Exception e) {
			logger.error("Exception :::",e);
			return null;
		}
		
	}
	
	
	public List<Object[]> getRepByStatus(Long notificationCampRepId, String status, int startFrom, int count) {
		
		String qry = "SELECT mobileNumber,clicks, status From NotificationCampaignSent WHERE notificationCampaignReport="+notificationCampRepId+
						" AND status IN ("+status+")";
		logger.info("query :" +qry);
		return executeQuery(qry, startFrom, count );
		
	}//getRepByStatus
	
	
	
	public List<String> getDelivered(Long notificationCampRepId, int startFrom, int count) {
		String queryStr = "select mobileNumber from NotificationCampaignSent where notificationCampaignReport="+notificationCampRepId+" AND status='"+SMSStatusCodes.CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT+"'";
		
		List<String> list = (List<String>)executeQuery(queryStr, startFrom, count);
		
		return list;
	}
	
	
	
	
	  public int findTotClicksByCrid(Long crId) throws Exception{
		  
		  
		String  queryString = "SELECT SUM(clicks) FROM NotificationCampaignSent WHERE notificationCampaignReport="+crId.longValue() ;
		return ((Long) executeQuery(queryString).get(0)).intValue();
		  
	  }
	  
	  
	  public List<Object[]> getAllSentCategories(Long notificationCampRepId) {
			 
			 /*String queryStr = "select  status, count(sent_id) from sms_campaign_sent where status Not " +
			 		"IN('"+ Constants.SMS_CAMPAIGN_STATUS_BOUNCE + "','" + Constants.SMS_CAMPAIGN_STATUS_PENDING + "','" +
			 		SMSStatusCodes.CLICKATELL_STATUS_RECEIVED+ "') And sms_cr_id =" + smsCampRepId + " group by status order by count(sent_id)  desc";*/
		  String queryStr = "select  status, count(sent_id) from Notification_campaign_sent where status Not " +
			 		"IN('"+ Constants.CAMP_STATUS_SENT + "','" +
			 		SMSStatusCodes.CLICKATELL_STATUS_RECEIVED+ "') And notification_cr_id =" + notificationCampRepId + " group by status order by count(sent_id)  desc";
			 
			 List<Object[]> catList = new ArrayList<Object[]>();
			 catList = jdbcTemplate.query(queryStr,	new RowMapper(){
	 			Object[] obj;
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						obj = new Object[2];
						obj[0] = rs.getString(1);
						obj[1] = rs.getLong(2);
						
						
						return obj;
					}
	 			
	 		});
	 		return catList;
			 
		 }
	  
	  public List getCategoryPercentageByCrId( Long campRepId) {
	    	try {
	    		List<Object[]> catList = new ArrayList<Object[]>();
	    		String queryStr = "SELECT status, COUNT(sent_id)" +
	    				" FROM  Notification_campaign_sent  " +
	    				"WHERE notification_cr_id="+campRepId+
	    				"  GROUP BY  status";
	    		logger.info("Query :" + queryStr);
	    		catList = jdbcTemplate.query(queryStr,	new RowMapper(){
	    			Object[] obj;
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						obj = new Object[2];
						obj[0] = rs.getString(1);
						obj[1] = rs.getLong(2);
						
						
						return obj;
					}
	    			
	    		});
	    		return catList;
	    	} catch (Exception e) {
	    		logger.error("** Error : " + e.getMessage() + " **");
	    		return null;
			}
	    }
	  public List findSentAndNotificationCrIdsBySent(Set<Long> sentIdsSet) {
			 
			 String sentIdStr = Utility.getIdsAsString(sentIdsSet);
			 
			 if(sentIdStr.isEmpty()) return null;
			 
			 String qry = " SELECT DISTINCT sentId, notificationCampaignReport.notificationCrId FROM NotificationCampaignSent WHERE sentId IN("+sentIdStr+")";
			 logger.info("qry ::"+qry);
			 List crIds = executeQuery(qry);
			 if(crIds != null && crIds.size() > 0) {
				 return crIds;
			 }
			 else {
				 return null;
			 }
			 
			 
	}
	  
	  public List<Long> findNotificationCrIdsBySent(Set<Long> sentIdsSet) {
			 
			 String sentIdStr = Utility.getIdsAsString(sentIdsSet);
			 
			 if(sentIdStr.isEmpty()) return null;
			 
			 String qry = " SELECT DISTINCT notificationCampaignReport.notificationCrId FROM NotificationCampaignSent WHERE sentId IN("+sentIdStr+")";
			 logger.info("qry ::"+qry);
			 List<Long> crIds = executeQuery(qry);
			 if(crIds != null && crIds.size() > 0) {
				 
				
				 return crIds;
				 
			 }else {
				 
				 return null;
			 }
			 
			 
		 }
	  
}
