package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.SuppressedContacts;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.beans.Users;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class SuppressedContactsDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
    public SuppressedContactsDaoForDML() {}
	
    private SessionFactory sessionFactory;
    
   private JdbcTemplate jdbcTemplate;
    
    

    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public void saveOrUpdate(SuppressedContacts suppressedContacts) {
        super.saveOrUpdate(suppressedContacts);
    }
    
    
    public void saveByCollection(Collection<SuppressedContacts> suppList) {
		super.saveOrUpdateAll(suppList);
	}
    public void delete(SuppressedContacts suppressedContacts) {
        super.delete(suppressedContacts);
    }
    public void deleteById(Long id) {
    	getHibernateTemplate().bulkUpdate("delete from SuppressedContacts where id="+ id);
    }
    public void deleteAllByUserId(Long userId) {
    	getHibernateTemplate().bulkUpdate("delete from SuppressedContacts where user.userId="+ userId);
    }
}

