package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.UrlShortCodeMapping;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings({ "unchecked" })
public class MailingListDaoForDML extends AbstractSpringDaoForDML {

    private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;
	
	public MailingListDaoForDML() {}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public void saveOrUpdate(MailingList mailingList) {
        super.saveOrUpdate(mailingList);
    }

    /***Purging***********/
    public void setMlStatusById(Long mlId, String status) {
		try {
			String query = "UPDATE MailingList m SET m.status='" + status + "' where  m.listId  = " + mlId;
			executeUpdate(query); 
		} catch(DataAccessException e) {
			if(logger.isErrorEnabled()) logger.error("*** Exception : Error occured while setting mailing list status .",e);
		}	
	}//setMlStatusById
    
    public void delete(MailingList mailingList) {
        super.delete(mailingList);
    }
}

