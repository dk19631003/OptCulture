package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactSpecificDateEvents;
import org.mq.marketer.campaign.beans.EventTriggerEvents;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ContactSpecificDateEventsDao extends AbstractSpringDao{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public ContactSpecificDateEventsDao() {
		
		
	}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

/**
 * 
 * 	Update TotalSize  
 * @param userId
 * @param eventTrigId
 * @param fromDate
 * @param endDate
 * @param isEmail
 * @return
 */
	
	 public int findSearchEmailCount(Long userId, Long eventTrigId, String fromDate,String toDate,String key) {
		

 		try {
			String subQry="SELECT COUNT(tot_count) FROM ( SELECT COUNT(e.event_id) as tot_count " +
							" FROM contact_specific_date_events e, campaign_sent cs WHERE e.user_id ="+userId.longValue()+
							" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.camp_sent_id IS NOT NULL  AND e.camp_cr_id=cs.cr_id " +
							" AND e.camp_sent_id = cs.sent_id AND e.campaign_sent_date BETWEEN '"+fromDate+"' AND '"+toDate+"' " +
							" AND cs.email_id IS NOT NULL ";
			if(key != null){

				subQry += " AND cs.email_id LIKE '%"+key+"%'";
			}
			
			String query  = subQry +" GROUP BY cs.email_id  ) as temp_count";
			int count = jdbcTemplate.queryForInt(query);
			return count;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
 		
	 }
	 
	 public int findSearchEmailCountNew(Long userId, Long eventTrigId, String fromDate,String toDate,String key, String emailSatusTobeFetched) {
			

	 		try {
	 			String openClicksQry="";
				String subQry=  "SELECT COUNT(e.event_id) as tot_count " +
								" FROM contact_specific_date_events e, campaign_sent cs WHERE e.user_id ="+userId.longValue()+
								" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.camp_sent_id IS NOT NULL  AND e.camp_cr_id=cs.cr_id " +
								" AND e.camp_sent_id = cs.sent_id AND e.campaign_sent_date BETWEEN '"+fromDate+"' AND '"+toDate+"' " +
								" AND cs.email_id IS NOT NULL " +
								" AND cs.status in ("+emailSatusTobeFetched+") ";
				
				
				
				
				if((emailSatusTobeFetched.contains("Bounced") && ! emailSatusTobeFetched.contains("special_condtion_for_all"))){ 

					if( !emailSatusTobeFetched.contains("Unsubscribed") && !emailSatusTobeFetched.contains("Spammed")  && !emailSatusTobeFetched.contains("Success")){
						subQry += "  AND cs.status='Bounced'  ";
					}else {
						subQry += "  AND cs.status='Bounced'  AND cs.status='Unsubscribed'  AND cs.status='Spammed' AND cs.status='Success'   ";
					}

				}
				
				
				
				
				
				
				
				if(emailSatusTobeFetched.contains("_fetch_clicks_also_") && !emailSatusTobeFetched.contains("_fetch_opens_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
					openClicksQry += " AND cs.clicks is not null AND cs.clicks !=0  ";
					
				}
				else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && !emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
					openClicksQry += " AND cs.opens is not null AND cs.opens !=0  ";
				}
				else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
					openClicksQry += " AND ((cs.opens is not null AND cs.opens !=0)   AND (cs.clicks is not null AND cs.clicks !=0))   ";
				}
				
				
				subQry += openClicksQry;
				
				if(key != null){

					subQry += " AND cs.email_id LIKE '%"+key+"%'";
				}
				
				String query  = subQry ;
				logger.info("query >>> "+query);
				int count = jdbcTemplate.queryForInt(query);
				return count;
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return 0;
			}
	 		
		 } 
	
	public int findSearchSmsCount(Long userId, Long eventTrigId, String fromDate,String toDate,String key) {
 		
 		

 		try {
 			String subQry="SELECT COUNT(tot_count) FROM (SELECT COUNT(e.event_id) as tot_count " +
 							" FROM contact_specific_date_events e, sms_campaign_sent scs WHERE e.user_id ="+userId.longValue()+
 							" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.sms_sent_id IS NOT NULL  AND e.sms_cr_id=scs.sms_cr_id " +
 							" AND e.sms_sent_id = scs.sent_id AND e.sms_sent_date BETWEEN '"+fromDate+"' AND '"+toDate+"' " +
 							" AND scs.mobile_number IS NOT NULL  ";
 			if(key != null){

 				subQry += " AND scs.mobile_number LIKE '%"+key+"%'";
 			}
 			
 			String query  = subQry +" GROUP BY scs.mobile_number ) as temp_count ";
 			
 			int count = jdbcTemplate.queryForInt(query);
 			return count;
 			
 		} catch (DataAccessException e) {
 			// TODO Auto-generated catch block
 			logger.error("Exception ::" , e);
 			return 0;
 		}
 	
 	
	}
	
    public int findSearchSmsCountNew(Long userId, Long eventTrigId, String fromDate,String toDate,String key, byte considerDeliveredStatus) {
 		
 		

 		try {
 			/*String subQry="SELECT COUNT(tot_count) FROM (SELECT COUNT(e.event_id) as tot_count " +
 							" FROM contact_specific_date_events e, sms_campaign_sent scs WHERE e.user_id ="+userId.longValue()+
 							" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.sms_sent_id IS NOT NULL  AND e.sms_cr_id=scs.sms_cr_id " +
 							" AND e.sms_sent_id = scs.sent_id AND e.sms_sent_date BETWEEN '"+fromDate+"' AND '"+toDate+"' " +
 							" AND scs.mobile_number IS NOT NULL  ";
 			if(key != null){

 				subQry += " AND scs.mobile_number LIKE '%"+key+"%'";
 			}
 			
 			String query  = subQry +" ) as temp_count ";
 			
 			int count = jdbcTemplate.queryForInt(query);
 			return count;*/
 			
 			
 			
 			String subQry = " SELECT COUNT(e.event_id) as tot_count  FROM contact_specific_date_events e, sms_campaign_sent scs WHERE e.user_id ="+userId.longValue()+
					" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.sms_sent_id IS NOT NULL  AND e.sms_cr_id=scs.sms_cr_id " +
					" AND e.sms_sent_id = scs.sent_id AND e.sms_sent_date BETWEEN '"+fromDate+"' AND '"+toDate+"' " +
					" AND scs.mobile_number IS NOT NULL  " ;
					
 			
 			
 			if(considerDeliveredStatus == 0){
				subQry += " AND scs.status not in ('Delivered','Success','Status Pending') ";
			}else if(considerDeliveredStatus == 1){
				subQry += " AND scs.status in ('Delivered','Success') ";
			}
 			
 			
 			if(key != null){

 				subQry += " AND scs.mobile_number LIKE '%"+key+"%'";
 			}
	
 			String query  = subQry;
 			
 			int count = jdbcTemplate.queryForInt(query);
 			return count;
 			
 			
 			
 			
 			
 		} catch (DataAccessException e) {
 			// TODO Auto-generated catch block
 			logger.error("Exception ::" , e);
 			return 0;
 		}
 	
 	
	}
	
	public int findAllCampaignEventsCount(Long userId, Long eventTrigId, String fromDate, String endDate , boolean isEmail) {
		
		try {
			
			String subQry = "";
			if(isEmail) {
				
				subQry = " AND campaignSentDate BETWEEN '"+fromDate+"' AND '"+endDate+"' AND campSentId is NOT NULL ";
				
			}else{
				
				subQry = " AND smsSentDate BETWEEN '"+fromDate+"' AND '"+endDate+"' AND smsSentId is NOT NULL ";
				
			}
			
			
			String hql = " SELECT COUNT(eventId) FROM  ContactSpecificDateEvents WHERE userId ="+userId.longValue()+
						" AND eventTriggerId="+eventTrigId.longValue()+subQry;
			
			
			List retList = executeQuery(hql);
			
			if(retList != null && retList.size() > 0) return ((Long)retList.get(0)).intValue();
			else return 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return 0;
		}
		
	}
	
/**
 * Update Sent Count for Email Campaign
 * @param userId
 * @param eventTrigId
 * @param fromDate
 * @param endDate
 * @return
 */
	
	
	
	public long findAllEmailSentReport(Long userId, Long eventTrigId, String fromDate, String endDate) {
	
		String qry = " SELECT COUNT(e.eventId) FROM  ContactSpecificDateEvents e, CampaignSent cs WHERE e.userId ="+userId.longValue()+
						" AND e.eventTriggerId="+eventTrigId.longValue()+" AND e.campSentId IS NOT NULL  AND e.campCrId=cs.campaignReport.crId " +
						" AND e.campSentId = cs.sentId AND e.campaignSentDate BETWEEN '"+fromDate+"' AND '"+endDate+"'";
		
		List retList = executeQuery(qry);
		
		if(retList != null && retList.size() > 0) return ((Long)retList.get(0)).intValue();
		else return 0;
	}
	
/**
 * Update Bounced,Delivered,Spam Count For Email Campaigns
 * @param userId
 * @param eventTrigId
 * @param fromDate
 * @param endDate
 * @param status
 * @return
 */
	public long findAllEmailReport(Long userId, Long eventTrigId, String fromDate, String endDate, String status) {
	
		String qry = " SELECT COUNT(e.eventId) FROM  ContactSpecificDateEvents e, CampaignSent cs WHERE e.userId ="+userId.longValue()+
						" AND e.eventTriggerId="+eventTrigId.longValue()+" AND e.campSentId IS NOT NULL  AND e.campCrId=cs.campaignReport.crId " +
						" AND e.campSentId = cs.sentId AND e.campaignSentDate BETWEEN '"+fromDate+"' AND '"+endDate+"' AND cs.status='"+status+"'";
		
		List retList = executeQuery(qry);
		
		if(retList != null && retList.size() > 0) return ((Long)retList.get(0)).intValue();
		else return 0;
	}
	
/**
 * Update Open,Click Count For Email Campaigns
 * @param userId
 * @param eventTrigId
 * @param fromDate
 * @param endDate
 * @param status
 * @return
 */

	public List<Map<String, Object>> findUniqueOpensAndClicks(Long userId, Long eventTrigId, String fromDate, String endDate){
	
		try {
			String qry = " SELECT SUM(IF(cs.opens>0,1,0)) as uniqueOpens ,SUM(IF(cs.clicks>0,1,0))  as uniqueClicks FROM  contact_specific_date_events e, campaign_sent cs WHERE e.user_id ="+userId.longValue()+
			" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.camp_sent_id IS NOT NULL  AND e.camp_cr_id=cs.cr_id " +
			" AND e.camp_sent_id = cs.sent_id AND e.campaign_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' AND cs.status='Success'";
	
			logger.info("qry ::"+qry);
			
			List<Map<String, Object>> retList = jdbcTemplate.queryForList(qry);
			
			if(retList != null && retList.size() > 0) return retList;
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	}
 
	
	public List<Map<String, Object>> findUniqueSMSClicks(Long userId, Long eventTrigId, String fromDate, String endDate){
		
		try {
			String qry = " SELECT SUM(IF(cs.clicks>0,1,0))  as uniqueClicks FROM  contact_specific_date_events e, sms_campaign_sent cs WHERE e.user_id ="+userId.longValue()+
			" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.sms_sent_id IS NOT NULL  AND e.sms_cr_id=cs.sms_cr_id " +
			" AND e.sms_sent_id = cs.sent_id AND e.sms_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' AND cs.status='Delivered'";
	
			logger.info("qry ::"+qry);
			
			List<Map<String, Object>> retList = jdbcTemplate.queryForList(qry);
			
			if(retList != null && retList.size() > 0) return retList;
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	}
	
	
	
	
	
	
/**
 * Update Sent Count for SMS Campaign
 * @param userId
 * @param eventTrigId
 * @param fromDate
 * @param endDate
 * @return
 */
 
	public long findAllSmsSentReport(Long userId, Long eventTrigId, String fromDate, String endDate) {
	
		String qry = " SELECT COUNT(e.eventId) FROM  ContactSpecificDateEvents e, SMSCampaignSent scs WHERE e.userId ="+userId.longValue()+
						" AND e.eventTriggerId="+eventTrigId.longValue()+" AND e.smsSentId IS NOT NULL  AND e.smsCrId=scs.smsCampaignReport.smsCrId " +
						" AND e.smsSentId = scs.sentId AND e.smsSentDate BETWEEN '"+fromDate+"' AND '"+endDate+"' ";
		
		List retList = executeQuery(qry);
		
		if(retList != null && retList.size() > 0) return ((Long)retList.get(0)).intValue();
		else return 0;
	}
	
	public long findAllEmailAndSmsSentReport(Long userId, Long eventTrigId, String fromDate, String endDate) {
		
		String qry = " SELECT COUNT(e.eventId) FROM  ContactSpecificDateEvents e, SMSCampaignSent scs WHERE e.userId ="+userId.longValue()+
						" AND e.eventTriggerId="+eventTrigId.longValue()+" AND e.smsSentId IS NOT NULL  AND e.smsCrId=scs.smsCampaignReport.smsCrId " +
						" AND e.smsSentId = scs.sentId AND e.smsSentDate BETWEEN '"+fromDate+"' AND '"+endDate+"' ";
		
		List retList = executeQuery(qry);
		
		if(retList != null && retList.size() > 0) return ((Long)retList.get(0)).intValue();
		else return 0;
	} 
	
/**
 * Update Bounced,Pending,Received Count For SMS Campaigns
 * @param userId
 * @param eventTrigId
 * @param fromDate
 * @param endDate
 * @param status
 * @return
 */
	
	public long findAllSmsReport(Long userId, Long eventTrigId, String fromDate, String endDate, String status) {
	
		String qry = " SELECT COUNT(e.eventId) FROM  ContactSpecificDateEvents e, SMSCampaignSent scs WHERE e.userId ="+userId.longValue()+
						" AND e.eventTriggerId="+eventTrigId.longValue()+" AND e.smsSentId IS NOT NULL  AND e.smsCrId=scs.smsCampaignReport.smsCrId " +
						" AND e.smsSentId = scs.sentId AND e.smsSentDate BETWEEN '"+fromDate+"' AND '"+endDate+"' AND scs.status='"+status+"'";
		
		List retList = executeQuery(qry);
		
		if(retList != null && retList.size() > 0) return ((Long)retList.get(0)).intValue();
		else return 0;
	}
 
 /**
  * Update Chart for Email Campaign
  * @param userId
  * @param eventTrigId
  * @param fromDate
  * @param endDate
  * @param status
  * @param type
  * @param isSuccess
  * @return
  */
	
	public List<Object[]> findTotEmailRepRate(Long userId, Long eventTrigId, String fromDate, 
			String endDate, String status, String type, boolean isSuccess){
			
			try {
				String grpQry = "" ;
				String appenQry = ""; 
				
				if(type.equals("Days")){
					
					appenQry = " DAY(e.campaignSentDate)" ;
					grpQry = " GROUP BY "+appenQry;
					
				}else{
	
					appenQry = " MONTH(e.campaignSentDate)  ";
					grpQry = " GROUP BY "+appenQry;
				}
				
				String subQry = "";
				if(!isSuccess && status != null) {
					
					
					subQry = " AND cs.status ='"+status+"'";
					
				}
	
				
				String qry = " SELECT COUNT(e.eventId),"+appenQry+" FROM  ContactSpecificDateEvents e, CampaignSent cs WHERE e.userId ="+userId.longValue()+
								" AND e.eventTriggerId="+eventTrigId.longValue()+" AND e.campSentId IS NOT NULL  AND e.campCrId=cs.campaignReport.crId " +
								" AND e.campSentId = cs.sentId AND e.campaignSentDate BETWEEN '"+fromDate+"' AND '"+endDate+"'" +
								subQry+grpQry;
				
				List<Object[]>  retList = executeQuery(qry);
				
				if(retList != null && retList.size() > 0) return retList;
				else return null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			}
	 
	}

 /**
  * Update Chart Open,Click Count For Email Campaigns
  * @param userId
  * @param eventTrigId
  * @param fromDate
  * @param endDate
  * @param status
  * @return
  */


 	public List<Map<String, Object>> findUniqueOpensAndClicks(Long userId, Long eventTrigId, String fromDate, String endDate, String type){
		
		try {
			String grpQry = "" ;
			String appenQry = ""; 
			
			if(type.equals("Days")){
				
				appenQry = " DAY(e.campaign_sent_date) as type" ;
				grpQry = " GROUP BY DAY(e.campaign_sent_date) ";
				
			}else{

				appenQry = " MONTH(e.campaign_sent_date) as type ";
				grpQry = " GROUP BY MONTH(e.campaign_sent_date)";
			}
			
			
			String qry = " SELECT SUM(IF(cs.opens>0,1,0)) as uniqueOpens ,SUM(IF(cs.clicks>0,1,0))  as uniqueClicks,"+appenQry+" FROM  contact_specific_date_events e, campaign_sent cs WHERE e.user_id ="+userId.longValue()+
			" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.camp_sent_id IS NOT NULL  AND e.camp_cr_id=cs.cr_id " +
			" AND e.camp_sent_id = cs.sent_id AND e.campaign_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' AND cs.status='Success' "+grpQry;

			logger.info("qry ::"+qry);
			
			List<Map<String, Object>> retList = jdbcTemplate.queryForList(qry);
			
			if(retList != null && retList.size() > 0) return retList;
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	}
 
 /**
  * Update Chart for SMS Campaign
  * @param userId
  * @param eventTrigId
  * @param fromDate
  * @param endDate
  * @param status
  * @param type
  * @param isSuccess
  * @return
  */
 	
 	public List<Object[]> findTotSmsRepRate(Long userId, Long eventTrigId, String fromDate, 
		String endDate, String status, String type, boolean isSuccess){
		
		try {
			String grpQry = "" ;
			String appenQry = ""; 
			
			if(type.equals("Days")){
				
				appenQry = " DAY(e.smsSentDate)" ;
				grpQry = " GROUP BY "+appenQry;
				
			}else{

				appenQry = " MONTH(e.smsSentDate)  ";
				grpQry = " GROUP BY "+appenQry;
			}
			
			String subQry = "";
			if(!isSuccess && status != null) {
				
				
				subQry = " AND scs.status ='"+status+"'";
				
			}

			
			String qry = " SELECT COUNT(e.eventId),"+appenQry+" FROM  ContactSpecificDateEvents e, SMSCampaignSent scs WHERE e.userId ="+userId.longValue()+
							" AND e.eventTriggerId="+eventTrigId.longValue()+" AND e.smsSentId IS NOT NULL  AND e.smsCrId=scs.smsCampaignReport.smsCrId " +
							" AND e.smsSentId = scs.sentId AND e.smsSentDate BETWEEN '"+fromDate+"' AND '"+endDate+"'" +
							subQry+grpQry;
			
			List<Object[]>  retList = executeQuery(qry);
			
			if(retList != null && retList.size() > 0) return retList;
			else return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}

 	}
 	
/**
 * Update Listbox fields for Email Campaigns
 * @param userId
 * @param eventTrigId
 * @param start
 * @param end
 * @param fromDate
 * @param toDate
 * @param key
 * @return
 */
 	
 	public List< ContactSpecificDateEvents> findEmailReports(Long userId, Long eventTrigId, int start,int end,String fromDate,String toDate,String key) {
		
		try {
			List<ContactSpecificDateEvents > list = null;
			String subQry="SELECT cs.email_id,MAX(e.campaign_sent_date) as lastSent, " +
							" cs.status as lastStatus, COUNT(e.event_id) as sentCount, SUM(IF(cs.opens >0, 1,0)) as uniopens," +
							"SUM(IF(cs.clicks >0, 1,0)) as uniclicks, e.event_id,  e.camp_sent_id FROM contact_specific_date_events e, campaign_sent cs WHERE e.user_id ="+userId.longValue()+
							" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.camp_sent_id IS NOT NULL  AND e.camp_cr_id=cs.cr_id " +
							" AND e.camp_sent_id = cs.sent_id AND e.campaign_sent_date BETWEEN '"+fromDate+"' AND '"+toDate+"' " +
							" AND cs.email_id IS NOT NULL  ";
			if(key != null){

				subQry += " AND cs.email_id LIKE '%"+key+"%'";
			}
			
			String query  = subQry +" GROUP BY cs.email_id  ORDER BY lastSent DESC  LIMIT "+" "+start+","+end ;
			/*String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)) as o,SUM(IF(clicks >0, 1,0)) as c,status,Count(id),id FROM DRSent WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";*/
			
			/*String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)),SUM(IF(clicks >0, 1,0)),status,Count(id),id FROM DRSent WHERE userId ="+userId.longValue()+" " +
							" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";*/
			
			
			
			list = jdbcTemplate.query(query, new RowMapper() {
				
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 
			            ContactSpecificDateEvents events = new ContactSpecificDateEvents();
			            events.setEventId(rs.getLong("event_id"));
			            events.setCampSentId(rs.getLong("camp_sent_id"));
			            events.setEmailId(rs.getString("email_id"));
			            
			            Calendar cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("lastSent"));
			            
		            	events.setLastSentDate(cal);
		            	events.setUniqueOpens( rs.getInt("uniopens"));
		            	events.setUniqueClicks( rs.getInt("uniclicks"));
		            	events.setLastStatus( rs.getString("lastStatus"));
		            	events.setSentCount( rs.getInt("sentCount"));
			
			            
			            return events;
			        }
			    });
			//list = getHibernateTemplate().find(query);
			//list = executeQuery(query, start, end);
			
			if(list!= null && list.size()>0) {
				return list;
			}
				
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	}
 	
 	
 	
public List< ContactSpecificDateEvents> findEmailReportsNew(Long userId, Long eventTrigId, int start,int end,String fromDate,String toDate,String key,String emailSatusTobeFetched) {
		
		try {
			List<ContactSpecificDateEvents > list = null;
			String openClicksQry="";
			String subQry="SELECT cs.email_id, e.campaign_sent_date as lastSent, " +
							" cs.status as lastStatus , cs.opens as uniopens,cs.clicks as uniclicks,e.event_id,  e.camp_sent_id" +
							" FROM contact_specific_date_events e, campaign_sent cs WHERE e.user_id ="+userId.longValue()+
							" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.camp_sent_id IS NOT NULL  AND e.camp_cr_id=cs.cr_id " +
							" AND e.camp_sent_id = cs.sent_id AND e.campaign_sent_date BETWEEN '"+fromDate+"' AND '"+toDate+"' " +
							" AND cs.email_id IS NOT NULL  " +
							" AND cs.status in ("+emailSatusTobeFetched+") ";
			
			
			
			
			if((emailSatusTobeFetched.contains("Bounced") && ! emailSatusTobeFetched.contains("special_condtion_for_all"))){ 
				
				if( !emailSatusTobeFetched.contains("Unsubscribed") && !emailSatusTobeFetched.contains("Spammed")  && !emailSatusTobeFetched.contains("Success")){
					subQry += "  AND cs.status='Bounced'  ";
				}else {
					subQry += "  AND cs.status='Bounced'  AND cs.status='Unsubscribed'  AND cs.status='Spammed' AND cs.status='Success'   ";
				}
				
			}
			
			
			
			
			
			
			
			if(emailSatusTobeFetched.contains("_fetch_clicks_also_") && !emailSatusTobeFetched.contains("_fetch_opens_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
				openClicksQry += " AND cs.clicks is not null AND cs.clicks !=0  ";
				
			}
			else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && !emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
				openClicksQry += " AND cs.opens is not null AND cs.opens !=0  ";
			}
			else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
				openClicksQry += " AND ((cs.opens is not null AND cs.opens !=0)   AND (cs.clicks is not null AND cs.clicks !=0))   ";
			}
			
			
			subQry += openClicksQry;
			
			
			
			
			if(key != null){

				subQry += " AND cs.email_id LIKE '%"+key+"%'";
			}
			
			String query  = subQry +" ORDER BY lastSent DESC  LIMIT "+" "+start+","+end ;
			/*String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)) as o,SUM(IF(clicks >0, 1,0)) as c,status,Count(id),id FROM DRSent WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";*/
			
			/*String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)),SUM(IF(clicks >0, 1,0)),status,Count(id),id FROM DRSent WHERE userId ="+userId.longValue()+" " +
							" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";*/
			
			
			logger.info("query >>> "+query);
			list = jdbcTemplate.query(query, new RowMapper() {
				
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 
			            ContactSpecificDateEvents events = new ContactSpecificDateEvents();
			            events.setEventId(rs.getLong("event_id"));
			            events.setCampSentId(rs.getLong("camp_sent_id"));
			            events.setEmailId(rs.getString("email_id"));
			            
			            Calendar cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("lastSent"));
			            
		            	events.setLastSentDate(cal);
		            	
		            	events.setLastStatus( rs.getString("lastStatus"));
		            	events.setUniqueOpens( rs.getInt("uniopens"));
		            	events.setUniqueClicks( rs.getInt("uniclicks"));
			            
			            return events;
			        }
			    });
			//list = getHibernateTemplate().find(query);
			//list = executeQuery(query, start, end);
			
			if(list!= null && list.size()>0) {
				return list;
			}
				
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
	}
public List< ContactSpecificDateEvents> findEmailStatusPendingReportsNew(Long userId, Long eventTrigId, int start,int end,String fromDate,String toDate,String key,String emailSatusTobeFetched) {
	
	try {
		List<ContactSpecificDateEvents > list = null;
		String subQry="SELECT cs.email_id, e.campaign_sent_date as lastSent,e.event_id,  e.camp_sent_id" +
						" FROM contact_specific_date_events e, campaign_sent cs WHERE e.user_id ="+userId.longValue()+
						" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.camp_sent_id IS NOT NULL  AND e.camp_cr_id=cs.cr_id " +
						" AND e.camp_sent_id = cs.sent_id AND e.campaign_sent_date BETWEEN '"+fromDate+"' AND '"+toDate+"' " +
						" AND cs.email_id IS NOT NULL  " +
						" AND cs.status in ('Submitted') ";
		
		
		if(key != null){

			subQry += " AND cs.email_id LIKE '%"+key+"%'";
		}
		
		String query  = subQry +" ORDER BY lastSent DESC  LIMIT "+" "+start+","+end ;
		
		
		logger.info("query >>> "+query);
		list = jdbcTemplate.query(query, new RowMapper() {
			
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 
		            ContactSpecificDateEvents events = new ContactSpecificDateEvents();
		            events.setEventId(rs.getLong("event_id"));
		            events.setCampSentId(rs.getLong("camp_sent_id"));
		            events.setEmailId(rs.getString("email_id"));
		            
		            Calendar cal = Calendar.getInstance();
	            	cal.setTime(rs.getTimestamp("lastSent"));
		            
	            	events.setLastSentDate(cal);
	            	
		            return events;
		        }
		    });
		//list = getHibernateTemplate().find(query);
		//list = executeQuery(query, start, end);
		
		if(list!= null && list.size()>0) {
			return list;
		}
			
		return null;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
		return null;
	}
}

 	
/**
  * Update Listbox fields for SMS Campaign
  * @param userId
  * @param eventTrigId
  * @param start
  * @param end
  * @param fromDate
  * @param toDate
  * @param key
  * @return
  */
 	public List< ContactSpecificDateEvents> findSmsReports(Long userId, Long eventTrigId, int start,int end,String fromDate,String toDate,String key) {
		
		try {
			List<ContactSpecificDateEvents > list = null;
			String subQry="SELECT scs.mobile_number,MAX(e.sms_sent_date) as lastSent, " +
							" scs.status as lastStatus, COUNT(e.event_id) as sentCount, " +
							" e.event_id, e.sms_sent_id FROM contact_specific_date_events e, sms_campaign_sent scs WHERE e.user_id ="+userId.longValue()+
							" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.sms_sent_id IS NOT NULL  AND e.sms_cr_id=scs.sms_cr_id " +
							" AND e.sms_sent_id = scs.sent_id AND e.sms_sent_date BETWEEN '"+fromDate+"' AND '"+toDate+"' " +
							" AND scs.mobile_number IS NOT NULL  ";
			if(key != null){

				subQry += " AND scs.mobile_number LIKE '%"+key+"%'";
			}
			
			String query  = subQry +" GROUP BY scs.mobile_number  ORDER BY lastSent DESC  LIMIT "+" "+start+","+end ;
			/*String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)) as o,SUM(IF(clicks >0, 1,0)) as c,status,Count(id),id FROM DRSent WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";*/
			
			/*String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)),SUM(IF(clicks >0, 1,0)),status,Count(id),id FROM DRSent WHERE userId ="+userId.longValue()+" " +
							" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";*/
			
			
			
			list = jdbcTemplate.query(query, new RowMapper() {
				
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 
			            ContactSpecificDateEvents events = new ContactSpecificDateEvents();
			            events.setEventId(rs.getLong("event_id"));
			            events.setSmsSentId(rs.getLong("sms_sent_id"));
			            events.setMobileNumber(rs.getString("mobile_number"));
			            
			            Calendar cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("lastSent"));
			            
		            	events.setLastSentDate(cal);
		            	/*events.setUniqueOpens( rs.getInt("uniopens"));
		            	events.setUniqueClicks( rs.getInt("uniclicks"));*/
		            	events.setLastStatus( rs.getString("lastStatus"));
		            	events.setSentCount( rs.getInt("sentCount"));
			
			
			            
			            return events;
			        }
			    });
			
			
			if(list!= null && list.size()>0) {
				return list;
			}
				
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
 	}
 	
 	
 	
  public List< ContactSpecificDateEvents> findSmsReportsNew(Long userId, Long eventTrigId, int start,int end,String fromDate,String toDate,String key, byte considerDeliveredStatus) {
		
		try {
			
			if(considerDeliveredStatus == -1){
				return null;
			}
			List<ContactSpecificDateEvents > list = null;
			String subQry="SELECT scs.mobile_number, e.sms_sent_date as lastSent, " +
							" scs.status as lastStatus, e.event_id, e.sms_sent_id" +
							" FROM contact_specific_date_events e, sms_campaign_sent scs WHERE e.user_id ="+userId.longValue()+
							" AND e.event_trigger_id="+eventTrigId.longValue()+" AND e.sms_sent_id IS NOT NULL  AND e.sms_cr_id=scs.sms_cr_id " +
							" AND e.sms_sent_id = scs.sent_id AND e.sms_sent_date BETWEEN '"+fromDate+"' AND '"+toDate+"' " +
							" AND scs.mobile_number IS NOT NULL  ";
							
			
			
			
			if(considerDeliveredStatus == 0){
				subQry += " AND scs.status not in ('Delivered','Success','Status Pending') ";
			}else if(considerDeliveredStatus == 1){
				subQry += " AND scs.status in ('Delivered','Success') ";
			}
			
			
			if(key != null){

				subQry += " AND scs.mobile_number LIKE '%"+key+"%'";
			}
			
			String query  = subQry +" ORDER BY lastSent DESC  LIMIT "+" "+start+","+end ;
			
			logger.info("query >>> "+query);
			/*String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)) as o,SUM(IF(clicks >0, 1,0)) as c,status,Count(id),id FROM DRSent WHERE userId ="+userId.longValue()+" " +
					" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";*/
			
			/*String query  = " SELECT emailId,MAX(sentDate), SUM(IF(opens >0, 1,0)),SUM(IF(clicks >0, 1,0)),status,Count(id),id FROM DRSent WHERE userId ="+userId.longValue()+" " +
							" AND sentDate >= '"+fromDate+"' AND sentDate <='"+toDate+"'  group by emailId ";*/
			
			
			
			list = jdbcTemplate.query(query, new RowMapper() {
				
				 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					 
			            ContactSpecificDateEvents events = new ContactSpecificDateEvents();
			            events.setEventId(rs.getLong("event_id"));
			            events.setSmsSentId(rs.getLong("sms_sent_id"));
			            events.setMobileNumber(rs.getString("mobile_number"));
			            
			            Calendar cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("lastSent"));
			            
		            	events.setLastSentDate(cal);
		            	/*events.setUniqueOpens( rs.getInt("uniopens"));
		            	events.setUniqueClicks( rs.getInt("uniclicks"));*/
		            	events.setLastStatus( rs.getString("lastStatus"));
			
			
			            
			            return events;
			        }
			    });
			
			
			if(list!= null && list.size()>0) {
				return list;
			}
				
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
 	}
	
 /**
  * Update Preview window
  * @param isEmail
  * @param eventId
  * @return
  */
 	public String findEtById(boolean isEmail, Long eventId){
 		String sentContent = null;
 		try {
 			String qry = null;
 			if(isEmail) {
 				
 				qry = "SELECT cr.content FROM ContactSpecificDateEvents e, CampaignReport cr WHERE e.eventId="+eventId.longValue()+
 						" AND e.campCrId IS NOT NULL AND e.campCrId=cr.crId";
 			}else{
 				
 				
 				qry = "SELECT scr.content FROM ContactSpecificDateEvents e, SMSCampaignReport scr WHERE e.eventId="+eventId.longValue()+
 						" AND e.smsCrId IS NOT NULL AND e.smsCrId=scr.smsCrId";
 			}
 			
 			List<String> list = executeQuery(qry);
 			if ( list != null && list.size() > 0) {
 				sentContent = list.get(0);
 			}
 			
 			return sentContent;
 		} catch (Exception e) {
 			logger.error(" ** Exception occured while fetching the "+(isEmail ?  "Email content" : "SMS content"), e ); 
 			return sentContent;
 		}
 		
 	}

 /**
  * Update View History Window
  * @param isEmail
  * @param contactEvent
  * @param etId
  * @param userId
  * @param fromDate
  * @param endDate
  * @return
  */
 	
 	public List<Object[]> findAllSentRep(boolean isEmail, ContactSpecificDateEvents contactEvent, Long etId, Long userId, String fromDate, String endDate  ) {
 		
 		List< Object[]> allreportsList = null;
 		try {
 			String qry = null;
 			if(isEmail) {
 				
 				qry = " SELECT cs.emailId, e.campaignSentDate, cs.status  FROM  ContactSpecificDateEvents e, CampaignSent cs WHERE e.userId ="+userId.longValue()+
 					" AND e.eventTriggerId="+etId.longValue()+" AND e.campSentId IS NOT NULL  AND e.campCrId=cs.campaignReport.crId " +
 					" AND e.campSentId = cs.sentId AND cs.emailId='"+contactEvent.getEmailId()+"' AND e.campaignSentDate BETWEEN '"+fromDate+"' AND '"+endDate+
 					"' ORDER BY e.campaignSentDate DESC";
 						
 				
 			}else{
 				
 				qry = " SELECT scs.mobileNumber, e.smsSentDate, scs.status  FROM  ContactSpecificDateEvents e, SMSCampaignSent scs WHERE e.userId ="+userId.longValue()+
 						" AND e.eventTriggerId="+etId.longValue()+" AND e.smsSentId IS NOT NULL  AND" +
 						" e.smsCrId=scs.smsCampaignReport.smsCrId  AND e.smsSentId = scs.sentId AND scs.mobileNumber='"+contactEvent.getMobileNumber()+"'" +
 						" AND e.smsSentDate BETWEEN '"+fromDate+"' AND '"+endDate+	"' ORDER BY e.smsSentDate DESC";
 						
 				
 				
 			}

 			allreportsList = executeQuery(qry);
 			if(allreportsList != null && allreportsList.size() > 0) return allreportsList;
 			
 			
 			return allreportsList;
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			logger.error("Exception ::" , e);
 			return allreportsList;
 		}
 		
 		
 		
 	}
	
}
