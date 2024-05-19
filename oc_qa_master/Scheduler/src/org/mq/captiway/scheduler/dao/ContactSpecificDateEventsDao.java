package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ContactSpecificDateEvents;
import org.mq.captiway.scheduler.beans.EventTriggerEvents;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ContactSpecificDateEventsDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public ContactSpecificDateEventsDao() {
		
		
	}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
public List<ContactSpecificDateEvents> findAllActiveEvents(String qry, int startIndex, int size) {
		
		
		

		
		List<ContactSpecificDateEvents> list = null;
		try {
			
			list = jdbcTemplate.query(qry+" LIMIT "+startIndex+", "+size, new RowMapper() {

		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	ContactSpecificDateEvents contactDateEvents = new ContactSpecificDateEvents();
		            
		           
		        	 /*event_trigger_id,trigger_type, created_time, event_time, user_id, email_status, sms_status, " +
						" event_category, source_id, contact_id, tr_condition 
*/		        	contactDateEvents.setEventId(rs.getLong("event_id"));
		        	 contactDateEvents.setEventTriggerId(rs.getLong("event_trigger_id"));
		        	 contactDateEvents.setTriggerType(rs.getInt("trigger_type"));
		        	 Calendar cal = null;
		        	 
		        	 if(rs.getDate("created_time") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("created_time"));
		            	contactDateEvents.setCreatedTime(cal);
		            } else {
		            	contactDateEvents.setCreatedTime(null);
		            }
		        	 if(rs.getDate("event_time") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("event_time"));
		            	contactDateEvents.setEventTime(cal);
		            } else {
		            	contactDateEvents.setEventTime(null);
		            }
		        	 
		        	 contactDateEvents.setUserId(rs.getLong("user_id"));
		        	 contactDateEvents.setEmailStatus(rs.getByte("email_status"));
		        	 contactDateEvents.setSmsStatus(rs.getByte("sms_status"));
		        	 
		        	 contactDateEvents.setEventCategory(rs.getString("event_category"));
		        	 contactDateEvents.setSourceId(rs.getLong("source_id"));
		        	 contactDateEvents.setContactId(rs.getLong("contact_id"));
		        	 contactDateEvents.setTriggerCondition(rs.getString("tr_condition"));
		        	 contactDateEvents.setSmsCrId(rs.getLong("sms_cr_id"));
		        	 contactDateEvents.setCampCrId(rs.getLong("camp_cr_id"));
		        	 contactDateEvents.setSmsSentId(rs.getLong("sms_sent_id"));
		        	 contactDateEvents.setCampSentId(rs.getLong("camp_sent_id"));
		        	 if(rs.getDate("campaign_sent_date") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("campaign_sent_date"));
		            	contactDateEvents.setCampaignSentDate(cal);
		            } else {
		            	contactDateEvents.setCampaignSentDate(null);
		            }
		        	 if(rs.getDate("sms_sent_date") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("sms_sent_date"));
		            	contactDateEvents.setSmsSentDate(cal);
		            } else {
		            	contactDateEvents.setSmsSentDate(null);
		            }
					
					
		            return contactDateEvents;
		        }
		    });
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		//logger.info("qry ::"+qry+" LIMIT "+startIndex+", "+size+" list size ::"+list.size());
		return list;
	
		
		
	}
	
	/*public void saveByCollection(List<ContactSpecificDateEvents> etEventsList){
		
		  super.saveOrUpdateAll(etEventsList);
	  }*/

	public int findEventsCount(String qry) {
		
		
		try {
			
			
			String countQry = "SELECT COUNT(*) FROM ("+qry +") AS tempCount";
			if(logger.isInfoEnabled()) logger.error(" query to fetch events::"+countQry);
			return jdbcTemplate.queryForInt(countQry);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error(" Exception : ",(Throwable)e);
			return 0;
		}
		
		
		
	}

	
	
}
