package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.FAQ;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.ResultSet;
public class FAQDaoForDML extends AbstractSpringDaoForDML {
	  //public FAQDaoForDML() {}
		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

		private SessionFactory sessionFactory;
		
		public FAQDaoForDML() {
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

	    public void saveOrUpdate(FAQ faq) {
	        super.saveOrUpdate(faq);
	    }
	    
	    @SuppressWarnings("unchecked")
		public List<String> findFaqDataByType(String lang, Long userId) {
	     		
	    	List<String> list = null;
	     		String qry = "SELECT faqContent from FAQ WHERE language='"+lang+"' AND userId='"+userId+"'";
	     		
	     		list = getHibernateTemplate().find(qry);
	     		
	     		return list;
	    }
	    @SuppressWarnings("unchecked")
		public List<String> findTermsDataByType(String lang, Long userId) {
	     		
	    	List<String> list = null;
	     		String qry = "SELECT termsAndCondition from FAQ WHERE language='"+lang+"' AND userId='"+userId+"'";
	     		
	     		list = getHibernateTemplate().find(qry);
	     		
	     		return list;
	    }
	    public List<FAQ> findByUserId(Long userId) {

			String query = "FROM FAQ WHERE userId ="+userId;
			@SuppressWarnings("unchecked")
			List<FAQ> list = getHibernateTemplate().find(query);
			return list;
		}
}

