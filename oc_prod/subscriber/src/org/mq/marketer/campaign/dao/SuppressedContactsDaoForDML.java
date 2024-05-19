package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.SuppressedContacts;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.mq.marketer.campaign.beans.Users;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class SuppressedContactsDaoForDML extends AbstractSpringDaoForDML {

    public SuppressedContactsDaoForDML() {}
	
    private SessionFactory sessionFactory;
    

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private JdbcTemplate jdbcTemplate;
    
    public void saveOrUpdate(SuppressedContacts suppressedContacts) throws Exception{
        super.saveOrUpdate(suppressedContacts);
    }
    public void deleteById(Long id) {
    	getHibernateTemplate().bulkUpdate("delete from SuppressedContacts where id IN("+ id+")");
    	logger.info("id of deleted contact is :::"+id);
    }
    public int deleteAllByUserId(Long userId, String type) {
    	return getHibernateTemplate().bulkUpdate("delete from SuppressedContacts where user.userId="+ userId+" AND type='"+type+"'");
    }
    public int deleteBy(Long userId, String emailIds) throws BaseDAOException{
    	try {
			//logger.debug("emailIds ::"+emailIds);
			String query = "DELETE FROM suppressed_contacts WHERE "
					+ "user_id="+userId.longValue()+" AND type='bouncedcontact' AND email REGEXP "+emailIds;
			
			int count = executeJdbcUpdateQuery(query);
			
			return count;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseDAOException("messages ::", e);
		}
    	
    }
    public int deleteByEmailId(Long userId,String emailId) throws BaseDAOException{
    	try {
			//logger.debug("emailIds ::"+emailIds);
			String query = "DELETE FROM suppressed_contacts WHERE "+"user_id="+userId.longValue()+" and email='"+emailId+"'";
			
			int count = executeJdbcUpdateQuery(query);
			
			return count;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BaseDAOException("messages ::", e);
		}
    	
    }
}

