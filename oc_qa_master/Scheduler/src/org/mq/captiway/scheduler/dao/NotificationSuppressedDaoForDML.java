package org.mq.captiway.scheduler.dao;

import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.NotificationSuppressedContacts;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationSuppressedDaoForDML extends AbstractSpringDaoForDML {
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;
	
	public NotificationSuppressedDaoForDML() {
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(NotificationSuppressedContacts notificationSuppressedContacts) {
		super.saveOrUpdate(notificationSuppressedContacts);
	}

	public void delete(NotificationSuppressedContacts notificationSuppressedContacts) {
		super.delete(notificationSuppressedContacts);
	}
	
}
