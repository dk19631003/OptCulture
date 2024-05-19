package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.mq.captiway.scheduler.beans.CustomTemplates;
import org.mq.captiway.scheduler.utility.Constants;

@SuppressWarnings( { "unchecked", "serial", "unused", "deprecation" })
public class CustomTemplatesDaoForDML extends AbstractSpringDaoForDML{
	
	private SessionFactory sessionFactory;
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(CustomTemplates customTemplates) {
		super.saveOrUpdate(customTemplates);
	}
	
	/*public int getCustomTemplateCount(Long userId) {
		 List<Long> countList = getHibernateTemplate().find("select count(templateId) from CustomTemplates where userId = " + userId);
	        if(countList.size()>0) {
	        	return countList.get(0).intValue();
	        }else {
	        	return 0;
	        }
	}
	
	
	
public CustomTemplates findCustTemplateById(Long id) {
		
		
		String hql = "FROM CustomTemplates WHERE templateId=" + id;
		return (CustomTemplates)getHibernateTemplate().find(hql).get(0);
		
		
	}
	
	public String getTemplateHTMLById(Long templateId) {
		try {
			return getHibernateTemplate().find("select htmlText from CustomTemplates where templateId = " + templateId).get(0).toString();
		} catch (Exception e) {
			logger.error("** Exception : No template exists with the given Id.  . Template No : " + templateId  + "**");
			return null;
		}
	}*/
	
}
