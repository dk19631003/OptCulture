package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.EventTriggerEvents;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class EventTriggerEventsDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public EventTriggerEventsDao() {
		
		
	}
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public List<EventTriggerEvents> findAllActiveEvents(String qry, int startIndex, int size) {
		
		
		

		
		List<EventTriggerEvents> list = null;
		try {
			
			list = jdbcTemplate.query(qry+" LIMIT "+startIndex+", "+size, new RowMapper() {

		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	EventTriggerEvents events = new EventTriggerEvents();
		            
		           
		    		
		        	events.setEventId(rs.getLong("event_id"));
		        	 events.setEventTriggerId(rs.getLong("event_trigger_id"));
		        	 events.setTriggerType(rs.getInt("trigger_type"));
		        	 Calendar cal = null;
		        	 
		        	 if(rs.getDate("created_time") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("created_time"));
		            	events.setCreatedTime(cal);
		            } else {
		            	events.setCreatedTime(null);
		            }
		        	 if(rs.getDate("event_time") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("event_time"));
		            	events.setEventTime(cal);
		            } else {
		            	events.setEventTime(null);
		            }
		        	 
		        	 events.setUserId(rs.getLong("user_id"));
		        	 events.setEmailStatus(rs.getByte("email_status"));
		        	 events.setSmsStatus(rs.getByte("sms_status"));
		        	 
		        	 events.setEventCategory(rs.getString("event_category"));
		        	 events.setSourceId(rs.getLong("source_id"));
		        	 events.setContactId(rs.getLong("contact_id"));
		        	 events.setTriggerCondition(rs.getString("tr_condition"));
		        	 events.setSmsCrId(rs.getLong("sms_cr_id"));
		        	 events.setCampCrId(rs.getLong("camp_cr_id"));
		        	 events.setSmsSentId(rs.getLong("sms_sent_id"));
		        	 events.setCampSentId(rs.getLong("camp_sent_id"));
		        	 if(rs.getDate("campaign_sent_date") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("campaign_sent_date"));
		            	events.setCampaignSentDate(cal);
		            } else {
		            	events.setCampaignSentDate(null);
		            }
		        	 if(rs.getDate("sms_sent_date") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("sms_sent_date"));
		            	events.setSmsSentDate(cal);
		            } else {
		            	events.setSmsSentDate(null);
		            }
					
		        	 
		            return events;
		        }
		    });
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		return list;
	
		
		
	}
	
	 /* public void saveByCollection(List<EventTriggerEvents> etEventsList){
	    	
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
