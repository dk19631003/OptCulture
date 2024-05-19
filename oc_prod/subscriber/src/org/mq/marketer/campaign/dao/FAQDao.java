package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.FAQ;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class FAQDao extends AbstractSpringDao {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	  public FAQDao() {
		  
	  }
	  
	  private JdbcTemplate jdbcTemplate;			
		
		public JdbcTemplate getJdbcTemplate() {	
			return jdbcTemplate;
		}	
			
		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {	
			this.jdbcTemplate = jdbcTemplate;
		}	
		public int executeUpdateQuery(String qryStr) {	
			return jdbcTemplate.update(qryStr);
		}	
		
		public List<FAQ> findByUserId(Long userId) {

			String query = "FROM FAQ WHERE userId ="+userId;
			@SuppressWarnings("unchecked")
			List<FAQ> list = getHibernateTemplate().find(query);
			return list;
		}
}

