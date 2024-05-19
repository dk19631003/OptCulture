package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.DefaultCategories;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class DefaultCategoriesDaoForDML extends AbstractSpringDaoForDML {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	/*public DefaultCategories find(Long id) {
		return (DefaultCategories) super.find(DefaultCategories.class, id);
	}*/

	public void saveOrUpdate(DefaultCategories defaultCategories) {
		super.saveOrUpdate(defaultCategories);
	}

	public void delete(DefaultCategories defaultCategories) {
		super.delete(defaultCategories);
	}
	
	/* public List findAll() {
	        return super.findAll(DefaultCategories.class);
	    }
	*/

}
