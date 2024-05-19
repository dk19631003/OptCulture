package org.mq.captiway.scheduler.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.CouponCodes;
import org.mq.captiway.scheduler.beans.ExternalSMTPEvents;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.springframework.jdbc.core.JdbcTemplate;

public class ExternalSMTPEventsDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public ExternalSMTPEvents find(Long id) {
		return (ExternalSMTPEvents) super.find(ExternalSMTPEvents.class, id);
	}

/*	public void saveOrUpdate(ExternalSMTPEvents externalSMTPEvents) {
		super.saveOrUpdate(externalSMTPEvents);
	}

   public void saveByCollection(Collection<ExternalSMTPEvents> ExternalSMTPEventsCollection){
    	super.saveOrUpdateAll(ExternalSMTPEventsCollection);
    }

	public void delete(ExternalSMTPEvents externalSMTPEvents) {
		super.delete(externalSMTPEvents);
	}
	
	public void deleteByCollection(Collection<ExternalSMTPEvents> ExternalSMTPEventsCollection) {
		
		getHibernateTemplate().deleteAll(ExternalSMTPEventsCollection);
		
	}*/
	
	private SimpleDateFormat formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
	public List<ExternalSMTPEvents> findAllEvents(Calendar reqTime, int index, int size) {
		
		Date reqDate = reqTime.getTime();
		 String reqDateStr = formatter.format(reqDate);
		
		
		String qry = " FROM ExternalSMTPEvents WHERE requestTime <= '"+reqDateStr+"' ORDER BY crId"; 
		
		List<ExternalSMTPEvents> eventsList = executeQuery(qry, index, size);
		
		if(eventsList != null && eventsList.size() > 0) {
			
			return eventsList;
		}else{
			return null;
		}
		
		
	}
	
	
	
	
}
