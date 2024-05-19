package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ContactsLoyalty;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;
//import org.mq.marketer.campaign.beans.SparkBaseCard;
//import org.mq.marketer.campaign.controller.contacts.ContactListUploader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ContactsLoyaltyDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
    public ContactsLoyaltyDaoForDML() {}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public void saveOrUpdate(ContactsLoyalty contactsLoyalty) {
        super.saveOrUpdate(contactsLoyalty);
    }
    public void delete(ContactsLoyalty contactsLoyalty) {
    	logger.info("delete the objet");
        super.delete(contactsLoyalty);
    }
	

}
