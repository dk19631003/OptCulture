package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.CustomTemplates;
import org.mq.captiway.scheduler.beans.OptInReport;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class OptInReportDaoForDML extends AbstractSpringDaoForDML{

	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private SessionFactory sessionFactory;
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(OptInReport optInReport) {
		super.saveOrUpdate(optInReport);
	}
	
	/* public OptInReport findById(Long id) {
	    	
	    	List<OptInReport> contactList = getHibernateTemplate().find("FROM OptInReport WHERE optRepId = "+ id);
	    	if(contactList.size()>0){
	    		return contactList.get(0);
	    	}
	        return null;
	    }
	*/
	   public void delete(OptInReport optInReport) {
           super.delete(optInReport);
       }
}

