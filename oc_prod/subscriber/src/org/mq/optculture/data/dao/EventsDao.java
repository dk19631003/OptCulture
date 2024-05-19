package org.mq.optculture.data.dao;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.model.events.EventInfo;
import org.mq.optculture.model.events.Events;
import org.mq.optculture.model.events.SortBy;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;

public class EventsDao extends AbstractSpringDao{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	public EventsDao() {
	
	}	
	
	public Events findById(Long eventId) {
		List<Events> list = executeQuery("FROM Events WHERE eventId="+ eventId.longValue());
    	if(list != null && list.size() == 1) {
    		return list.get(0);
    	}
    	return null;
	}
	public List<Events> getEventInfoByPageSize(Long userId,int offset,int limit){

		logger.info("query===>FROM Events where userId="+ userId );	
		List<Events> list = executeQuery("FROM Events where userId="+ userId ,offset,limit); 	
		return list;
	}
	
	public List<Events> getEventInfoByTextFields(Long userId,int offset,int limit,String textField,String searchType){

		String subQry = Constants.STRING_NILL;
		if(searchType.equals("Status") && !textField.equals("All")) {
			subQry += " AND eventStatus = '"+textField+"'";
		}
		else if(searchType.equals("EventName")) {
			subQry += " AND eventTitle like '%"+textField+"%'";
		}
		else if(searchType.equals("City")) {
			subQry += " AND city = '"+textField+"'";
		}
		logger.info("query===>FROM Events where userId="+ userId +subQry );	
		List<Events> list = executeQuery("FROM Events where userId="+ userId +subQry ,offset,limit); 	
		return list;
	}
	
	
	public List<Events> getEventInfoByCreatedDate(Long userId,int offset,int limit,String startDate,String endDate){

		logger.info("query===>FROM Events where userId="+ userId +" and eventCreateDate between '" +startDate+"' and '"+ endDate+"'");	
		List<Events> list = executeQuery("FROM Events where userId="+ userId +" and eventCreateDate between '" +startDate+"' and '"+ endDate+"'" ,offset,limit); 	
		return list;
	}
	public int getTotalEventSizeCreatedDate(Long userId,String startDate,String endDate){

		logger.info("query===>Select COUNT(*) FROM Events where userId="+ userId +" and eventCreateDate between '" +startDate+"' and '"+ endDate+"'");	
		String qry="Select COUNT(*) FROM Events where userId="+ userId +" and eventCreateDate between '" +startDate+"' and '"+ endDate+"'"; 	
		int size =((Long) executeQuery(qry).get(0)).intValue();
		return size;
	}
	
	
	public int getTotalEventSizeByTextFields(Long userId,String textField,String searchType) {
		
		String subQry = Constants.STRING_NILL;
		if(searchType.equals("Status") && !textField.equals("All")) {
			subQry += " AND eventStatus = '"+textField+"'";
		}
		else if(searchType.equals("EventName")) {
			subQry += " AND eventTitle like '%"+textField+"%'";
		}
		else if(searchType.equals("City")) {
			subQry += " AND city = '"+textField+"'";
		}
		logger.info("qry===>"+"SELECT  COUNT(*) FROM  Events  WHERE userId =" + userId+subQry);
		String qry = "SELECT  COUNT(*) FROM  Events  WHERE userId =" + userId+subQry;
		int size =((Long) executeQuery(qry).get(0)).intValue();
		return size;
	}
	
	
	public List<Events> getByEventInfo(EventInfo eventInfo,Long userId,SortBy sortBy) {
		
	
		
		String subQry = Constants.STRING_NILL;
	if(eventInfo != null) {
		//APP-3398
		if(eventInfo.getEventStatus() == null || eventInfo.getEventStatus().isEmpty()) {
			eventInfo.setEventStatus("Default");
		}
		if (eventInfo.getDate() != null  && !eventInfo.getDate().isEmpty()) {
			subQry += " AND  '" + eventInfo.getDate() +"' BETWEEN eventStartDate AND eventEndDate";
		}
		if(eventInfo.getEventStatus() != null && !eventInfo.getEventStatus().isEmpty()
				&& !eventInfo.getEventStatus().equalsIgnoreCase("All") && !eventInfo.getEventStatus().equalsIgnoreCase("Default")) {
			subQry += " AND eventStatus = '"+eventInfo.getEventStatus().trim()+"'";
		}
		//APP-3398
		if(eventInfo.getEventStatus().equalsIgnoreCase("Default")) {
			subQry += " AND eventStatus in ('"+Constants.EVENTS_STATUS_ACTIVE+"','"+ Constants.EVENTS_STATUS_RUNNING +"')";
		}
		if(eventInfo.getSearchEvent() != null && !eventInfo.getSearchEvent().isEmpty()) {
			subQry += " AND eventTitle like '%" + eventInfo.getSearchEvent().trim() + "%'";
		}
		if(eventInfo.getZipCode() != null && !eventInfo.getZipCode().isEmpty()) {
			subQry += " AND zipCode = '"+eventInfo.getZipCode().trim()+"'";
		}
		if(eventInfo.getCity() != null && !eventInfo.getCity().isEmpty()) {
			subQry += " AND city ='" + eventInfo.getCity().trim()+"'";
		}
		if(eventInfo.getState() != null && !eventInfo.getState().isEmpty()) {
			subQry += " AND state  ='" + eventInfo.getState().trim()+"'";
		}
		if(eventInfo.getStore() != null && !eventInfo.getStore().isEmpty()) {
			subQry += " AND store like '%" + StringEscapeUtils.escapeSql(eventInfo.getStore().trim())+"%'";
		}
		if(eventInfo.getIsOneDay()!= null && !eventInfo.getIsOneDay().isEmpty()) {
			subQry += " AND isOneDay="+eventInfo.getIsOneDay();
		}
	}
	
	if(sortBy != null) {
		String type = sortBy.getType();
		String order = sortBy.getOrder();
		   if(type!=null && !type.isEmpty()){//TODO changes on sorting fields
				 type= type.equalsIgnoreCase("Event") ?
						 "eventTitle" :type.equalsIgnoreCase("Date") ? "date" :type.equalsIgnoreCase("City") ?
								 "city" :type.equalsIgnoreCase("Store") ? "store" :type.equalsIgnoreCase("State") ? "state" :"";
			   }
			   

		   subQry +=(type!=null && !type.isEmpty())? " order by "+ type +" "+ order :"";  ////event, date, city, state
	}
	
	
		if(!subQry.contains("order by")) {//Chronological change
			subQry += " order by eventStartDate";
		}
			 
		logger.info("query===>FROM Events where userId="+ userId +subQry);	
		List<Events> list = executeQuery("FROM Events where userId="+ userId +subQry); 	
		return list;
	}

	public int getAllEventsCount() {
		// TODO Auto-generated method stub
		String qry ="SELECT COUNT(eventId) from Events";
		int size = ((Long) executeQuery(qry).get(0)).intValue();
		return size;
	}

	public List<Events> getAllEvents(int startIdx, int endIdx) {
		List<Events> eventList = null;
		
		String query = "FROM Events order by 1 ASC ";
		eventList = executeQuery(query, startIdx, endIdx);
		if(eventList!= null && eventList.size()>0) {
			return eventList;
		}
			
		return null;
		
	} // getAllCoupons


}
