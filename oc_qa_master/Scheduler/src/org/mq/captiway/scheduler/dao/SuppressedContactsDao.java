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
public class SuppressedContactsDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
    public SuppressedContactsDao() {}
	
    private SessionFactory sessionFactory;
    
   private JdbcTemplate jdbcTemplate;
    
    

    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
    
    public SuppressedContacts find(Long id) {
        return (SuppressedContacts) super.find(SuppressedContacts.class, id);
    }

   
    public List findAll() {
        return super.findAll(SuppressedContacts.class);
    }

    public List findByUserName(String userName){
    	return getHibernateTemplate().find("from SuppressedContacts where users.userName = '" + userName + "'");
    }
    
    public List<SuppressedContacts> findAllByUserId(Long userId) {
    	try {
			return getHibernateTemplate().find("from SuppressedContacts where user.userId="+userId);
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
    }
    
    
    
    public List<SuppressedContacts> searchContactsById(Long userId,String searchStr) {
    	return getHibernateTemplate().find("from SuppressedContacts where user.userId="+ userId + " and email like '%"+ searchStr +"%'");
    }
    
    public int getTotCountByUserId(Long userId) {
    	String query = "select count(*) from SuppressedContacts where user.userId="+userId;
    	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
    }
    
    
    
	public List<SuppressedContacts> isAlreadySuppressedContact(String emailAddress, Long userId) {
		
		Map<String,Boolean> existingMap = new HashMap<String, Boolean>();
		
		List<SuppressedContacts> suppsedContactList =null;
		String qry= "select * from  suppressed_contacts where user_id="+ userId + " and email REGEXP '" + emailAddress + "' and type in('" +Constants.SUPP_TYPE_BOUNCED +"','" +Constants.CS_STATUS_SPAMMED+ "')";
		
		try {
			suppsedContactList = jdbcTemplate.query(qry, new RowMapper() {
	
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	SuppressedContacts suppressedContacts = new SuppressedContacts();
		            
		        	suppressedContacts.setId(rs.getLong("id"));
		        	suppressedContacts.setEmail(rs.getString("email"));
		        	
		        	
		        	 if(rs.getDate("suppressed_time") != null) {
			            	Calendar cal = Calendar.getInstance();
			            	cal.setTime(rs.getTimestamp("suppressed_time"));
			            	suppressedContacts.setSuppressedtime(cal);
			            	
			            } 
		        	 
		        	 suppressedContacts.setType(rs.getString("type"));
		        	
		        	Users user  = new Users();
					user.setUserId(rs.getLong("user_id"));
					suppressedContacts.setUser(user);
					
					suppressedContacts.setReason(rs.getString("reason"));
		    		
		            
		            return suppressedContacts;
		        }
		    });
		
		} catch (Exception e) {
			logger.error("Exception ***", e);
		}
		return suppsedContactList;
		
	}

	public String isAlreadySuppressedContact(Long userId, String emailId) {
		
		try {
			String status = null;
			
			String query = "FROM SuppressedContacts where user.userId="+userId +" and  email like '"+ emailId +"' and type in('" +Constants.SUPP_TYPE_BOUNCED +"','" +Constants.CS_STATUS_SPAMMED+ "')";
			List<SuppressedContacts>  suppContList =  getHibernateTemplate().find(query);
			if(suppContList != null && suppContList.size() > 0) {
				
				status = suppContList.get(0).getType();
			}
			
			return status;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
	}

}

