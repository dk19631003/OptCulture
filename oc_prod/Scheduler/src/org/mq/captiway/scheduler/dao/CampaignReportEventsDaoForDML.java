package org.mq.captiway.scheduler.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.CampaignReportEvents;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.springframework.jdbc.core.JdbcTemplate;

public class CampaignReportEventsDaoForDML extends AbstractSpringDaoForDML{
	


	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	/*public CampaignReportEvents find(Long id) {
		return (CampaignReportEvents) super.find(CampaignReportEvents.class, id);
	}*/

	public void saveOrUpdate(CampaignReportEvents campaignReportEvents) {
		super.saveOrUpdate(campaignReportEvents);
	}

   public void saveByCollection(Collection<CampaignReportEvents> campaignReportEventsCollection){
    	super.saveOrUpdateAll(campaignReportEventsCollection);
    }

	public void delete(CampaignReportEvents campaignReportEvents) {
		super.delete(campaignReportEvents);
	}
	
	public void deleteByCollection(Collection<CampaignReportEvents> campaignReportEventsCollection) {
		
		getHibernateTemplate().deleteAll(campaignReportEventsCollection);
		
	}
	
	/*private SimpleDateFormat formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
	public List<CampaignReportEvents> findAllEvents(Calendar reqTime,  int index, int size) {
		
		Date reqDate = reqTime.getTime();
		 String reqDateStr = formatter.format(reqDate);
		
		
		String qry = " FROM CampaignReportEvents WHERE requestTime <= '"+reqDateStr+"' ORDER BY crId"; 
		
		List<CampaignReportEvents> eventsList = executeQuery(qry, index, size);
		
		if(eventsList != null && eventsList.size() > 0) {
			
			return eventsList;
		}else{
			return null;
		}
		
		
	}*/
	
	
	
	

	
	

}

