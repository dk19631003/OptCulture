package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.model.events.Events;
import org.springframework.jdbc.core.JdbcTemplate;

public class EventsDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	public EventsDaoForDML() {
	}
	
	
	public void saveOrUpdate(Events event) {
		super.saveOrUpdate(event);
	}
	
	 public void delete(Events event) {
         super.delete(event);
     }
	
	public void deleteByPrgmId(Long eventId) {
		String queryStr = " DELETE FROM Events WHERE eventId = "+eventId.longValue();
		
		getHibernateTemplate().bulkUpdate(queryStr);
		
	}
}
