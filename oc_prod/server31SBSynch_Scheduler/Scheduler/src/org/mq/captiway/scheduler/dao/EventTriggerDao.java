package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.mapping.Array;
import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.TemplateCategory;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.services.EventTriggerEventsObserver;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.beans.BeansException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class EventTriggerDao extends AbstractSpringDao  {
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public EventTriggerDao(){
	}
	
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@SuppressWarnings("unchecked")
	public List<EventTrigger> getActiveTriggerList(){ //pick all the triggers that are active and those who were sent more than a day ago
		try {

			List<EventTrigger> eventTriggerList=null;
			List <Long> idsList=null;
			String idsListStr = "";

			 String queryStr= " SELECT id FROM event_trigger " +
				" WHERE ( (options_flag & "+Constants.ET_TRIGGER_IS_ACTIVE_FLAG+") = "+Constants.ET_TRIGGER_IS_ACTIVE_FLAG +") ";
				// AND (last_sent_date IS NULL OR DATEDIFF(now(),last_sent_date) >= 1)";

			//if(logger.isInfoEnabled()) logger.info(" Executing query "+queryStr);
			//if(logger.isDebugEnabled()) logger.debug(" DEBUG ,,,,,Executing query "+queryStr);
			
			idsList = jdbcTemplate.queryForList(queryStr,Long.class);

			if(idsList.isEmpty()) {

				if(logger.isDebugEnabled()) logger.debug("No Active Trigger FOund....Exiting");
				return eventTriggerList;
			}

			for(Iterator<Long> iterator=idsList.iterator();iterator.hasNext();){
				if(idsListStr.length() <= 0) {
					idsListStr += iterator.next();
				}
				else {
					idsListStr += ","+iterator.next();
				}

			} //idsListStr has all the active trigger ids list

			queryStr = " FROM EventTrigger " +
			"WHERE id IN ("+idsListStr+") ";

			//if(logger.isInfoEnabled()) logger.info("inside EventTriggerDao.EventTrigger() executing the query "+queryStr);
			eventTriggerList = getHibernateTemplate().find(queryStr);
			return eventTriggerList;
		}
		catch (Exception e){

			if(logger.isDebugEnabled()) logger.debug("** Exception ",e);
			logger.error("Exception ::::" , e);
			return null; 
		}

	}
	
	public List<EventTrigger> findAllUserAutoRespondTriggers(Long userId, String trTypeStr) {
		
		try {
			
			//TODO can pass type to this query and condition query will be prepared accordingly
			String hql = "FROM EventTrigger WHERE users="+userId.longValue()+" AND trType in("+trTypeStr+") AND bitwise_and(optionsFlag,"
						+Constants.ET_TRIGGER_IS_ACTIVE_FLAG+")="+Constants.ET_TRIGGER_IS_ACTIVE_FLAG ;
			
			List<EventTrigger> retList = getHibernateTemplate().find(hql);
			
			if(retList != null && retList.size() > 0) {
				
				return retList;
			}
			else return null;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			if(logger.isErrorEnabled())logger.error("Exception while getting Store triggers for user ::"+userId.longValue());
			return null;
		}
		
		
	}
	
	
	public List<EventTrigger> findAllActiveTriggerList(String currentTime, boolean isSpecificDate){
		
		String subQry = Constants.STRING_NILL;
		
		if(!isSpecificDate) {
			
			subQry = " AND et.trType NOT IN("+Constants.ET_TYPE_ON_CONTACT_DATE+")";
			
		}else {
			
			subQry = " AND et.trType IN("+Constants.ET_TYPE_ON_CONTACT_DATE+")";
			
		}
		
		//bitwise will give 0/1 as its value is '1'
		/*String queryStr= " FROM EventTrigger WHERE bitwise_and(optionsFlag,"
						+Constants.ET_TRIGGER_IS_ACTIVE_FLAG+")="+Constants.ET_TRIGGER_IS_ACTIVE_FLAG + subQry+" ORDER BY users";*/
		
		String queryStr=
				" SELECT DISTINCT et " +
				" FROM EventTrigger et, Users usr " +
				" WHERE et.users = usr.userId " +
				" AND bitwise_and(et.optionsFlag," +
						Constants.ET_TRIGGER_IS_ACTIVE_FLAG + ")=" + Constants.ET_TRIGGER_IS_ACTIVE_FLAG  + subQry +
				" AND DATE(now()) <= DATE(usr.packageExpiryDate) AND usr.enabled = 1 " +
				" ORDER BY et.users ";
		
		
		/*String qry = "SELECT Distinct ete.event_id FROM event_trigger_events ete, event_trigger et WHERE ete.user_id="+eventTrigger.getUsers().getUserId().longValue()+"" +
		 " AND et.user_id="+eventTrigger.getUsers().getUserId().longValue()+"" AND event_trigger_id="+eventTrigger.getId().longValue()+
		TriggerQueryGenerator.getTriggerTimeDiffCond(eventTrigger.getMinutesOffset(), "event_time")+" AND status=0";

		
		String queryStr= " SELECT DISTINCT e, COUNT(eventId) as cnt FROM EventTrigger e, EventTriggerEvents et " +
						" WHERE et.eventTriggerId= e.id AND et.userId=e.users.userId AND bitwise_and(e.optionsFlag,"
						+Constants.ET_TRIGGER_IS_ACTIVE_FLAG+")="+Constants.ET_TRIGGER_IS_ACTIVE_FLAG+" AND et.status=0 AND GROUP BY et.eventTriggerId ORDER BY et.eventTriggerId HAVING cnt>0" ;
*/
		
		
		/*if(logger.isInfoEnabled()) {
			
			logger.info("active tr qry ::"+queryStr);
		}*/
		
		
		
		List<EventTrigger> retList = getHibernateTemplate().find(queryStr);
		if(retList != null && retList.size() > 0) {
			
			return retList;
		}else return null;
		
	}//findAllActiveTriggerList
	
	
	/**
	 * added for event trigger sms feature
	 * 
	 * @param id
	 * @return
	 */
	public EventTrigger getEventTriggerById(Long id){
		
		try{
			return (EventTrigger)getHibernateTemplate().find(" FROM EventTrigger WHERE id = "+id).get(0);
		} 
		catch(Exception E){
			if(logger.isDebugEnabled()) logger.debug("**Exception while getting event Trigger object");
			return null;
		}
	}
	
    /*public void saveOrUpdate(EventTrigger eventTrigger) {
        super.saveOrUpdate(eventTrigger);
    }*/

    /*public void update(EventTrigger eventTrigger) {
        super.update(eventTrigger);
    }*/

    /*public void delete(EventTrigger eventTrigger) {
        super.delete(eventTrigger);
    }*/

    /*public int insertEvents(String sql) {
		
		try {
			
			
			int result = jdbcTemplate.update(sql);
			//if(logger.isInfoEnabled()) logger.error(" query to save events::"+sql+" result ::"+result);
			
			
			return result;
		
		
		
		} 
		catch (BeansException e) {
				if(logger.isErrorEnabled()) logger.error(" ** Exception while getting the bean jdbcTemplate from the context", e);
				return -1;
		}
		catch (DataAccessException e) {
			if(logger.isErrorEnabled()) logger.error("** Exception while executing the query:"+sql, e);
			return -1;
		}
		
	}*/
    
   
    
    //updates the events with the status 'Sent' after trigger associated email sent
    /*public int updateEmailStatusFromEvents(Long userId, Long etId, Long crLong, String fromTable ) {
    	
    	try{
    		Date sentDate = new Date();
    		
    		//long etComplement = ~(Constants.ET_TRIGGER_EVENTS_SEND_EMAIL_CAMPAIGN_FLAG);
    		 
    		String sql = " UPDATE "+fromTable+" et, tempcontacts tc SET et.email_status="+Constants.ET_EMAIL_STATUS_SENT+"," +
    						" et.campaign_sent_date='"+format.format(sentDate)+"',et.camp_cr_id="+crLong.longValue()+" WHERE " +
    					"	et.user_id="+userId.longValue()+" AND event_trigger_id="+etId.longValue()+" AND et.event_id=tc.event_source_id";
    		
    		//logger.info("sql ::"+sql);
    		int updateCount = executeJdbcUpdateQuery(sql);
    		return updateCount;
    		
    	}catch (Exception e) {
			// TODO: handle exception
    		
    		return -1;
		}
    	
    	
    	
    	
    }*///
    
    
    //updates the events with the status 'Sent' after trigger associated email sent
    /*public int updateEventsSentId(Long userId, Long etId, Long crLong, String fromTable ) {
    	
    	try{
    		Date sentDate = new Date();
    		
    		 
    		String sql = " UPDATE "+fromTable+" et, campaign_sent cs SET et.camp_sent_id=cs.sent_id " +
    						" WHERE et.user_id="+userId.longValue()+" AND event_trigger_id="+etId.longValue()+
    						" AND cs.cr_id="+crLong.longValue()+" AND et.camp_cr_id=cs.cr_id AND et.contact_id=cs.contact_id ";
    		

    		int updateCount = executeJdbcUpdateQuery(sql);
    		return updateCount;
    		
    	}catch (Exception e) {
			// TODO: handle exception
    		
    		return -1;
		}
    	
    	
    	
    	
    }*/
    
    
    
    //updates the events with the status 'Sent' after trigger associated SMS sent
  /*public int updateSMSStatusFromEvents(Long userId, Long etId, Long smsCrLong, String fromTable ) {
    	
    	try{
    		Date sentDate = new Date();
    		//long etComplement = ~(Constants.ET_TRIGGER_EVENTS_SEND_SMS_CAMPAIGN_FLAG);
    		
    		String sql = " UPDATE "+fromTable+" et, sms_tempcontacts tc SET et.sms_status="+Constants.ET_SMS_STATUS_SENT+", " +
    				"et.sms_sent_date='"+format.format(sentDate)+"',et.sms_cr_id="+smsCrLong.longValue()+" WHERE " +
    					"	et.user_id="+userId.longValue()+" AND event_trigger_id="+etId.longValue()+" AND et.event_id=tc.event_source_id";
    		

    		int updateCount = executeJdbcUpdateQuery(sql);
    		return updateCount;
    		
    	}catch (Exception e) {
			// TODO: handle exception
    		
    		return -1;
		}
    	
    	
    	
    	
    }*/
    
  //updates the events with the status 'Sent' after trigger associated email sent
  /*public int updateEventsSMSSentId(Long userId, Long etId, Long smsCrLong, String fromTable) {
  	
  	try{
  		Date sentDate = new Date();
  		
  		 
  		String sql = " UPDATE "+fromTable+" et, sms_campaign_sent cs SET et.sms_sent_id=cs.sent_id " +
  						" WHERE et.user_id="+userId.longValue()+" AND event_trigger_id="+etId.longValue()+
  						" AND cs.sms_cr_id="+smsCrLong.longValue()+" AND et.sms_cr_id=cs.sms_cr_id AND et.contact_id=cs.contact_id ";
  		

  		int updateCount = executeJdbcUpdateQuery(sql);
  		return updateCount;
  		
  	}catch (Exception e) {
			// TODO: handle exception
  		
  		return -1;
		}
  	
  	
  	
  	
  }*/
  
  
  public List<EventTrigger> findAllETByUserAndType(Long userId,int trType){
		String hql = "FROM EventTrigger WHERE users="+userId.longValue()+" AND  bitwise_and(trType,"+trType+") > 0"
				+ " AND  bitwise_and(optionsFlag,"
				+Constants.ET_TRIGGER_IS_ACTIVE_FLAG+")="+Constants.ET_TRIGGER_IS_ACTIVE_FLAG ;
		
		logger.debug(" Trnsaction qry is  :"+hql);
		List<EventTrigger> retList = getHibernateTemplate().find(hql);
		logger.debug(" retList   :"+retList.size());
		return retList;
	}
  
  
    
}
