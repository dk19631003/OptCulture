package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.beans.SMSBounces;
import org.mq.captiway.scheduler.beans.SMSSuppressedContacts;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SMSSuppressedContactsDaoForDML extends AbstractSpringDaoForDML{

	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

    public SMSSuppressedContactsDaoForDML() {}
	
    private SessionFactory sessionFactory;
    
    
    private JdbcTemplate jdbcTemplate;
	

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
    
    /*public SMSSuppressedContacts find(Long id) {
        return (SMSSuppressedContacts) super.find(SMSSuppressedContacts.class, id);
    }*/

    public void saveOrUpdate(SMSSuppressedContacts smsSuppressedContacts) {
    	//logger.debug("in save or update"+smsSuppressedContacts.getMobile());
        super.saveOrUpdate(smsSuppressedContacts);
    }

    public void saveByCollection(Collection<SMSSuppressedContacts> campList) {
		super.saveOrUpdateAll(campList);
	}
    
    public void delete(SMSSuppressedContacts SMSSuppressedContacts) {
        super.delete(SMSSuppressedContacts);
    }

    /*public List findAll() {
        return super.findAll(SMSSuppressedContacts.class);
    }

    public List findByUserName(String userName){
    	return getHibernateTemplate().find("from SMSSuppressedContacts where user.userName = '" + userName + "'");
    }
    
    public List<SMSSuppressedContacts> findAllByUserId(Long userId) {
    	try {
			return getHibernateTemplate().find("from SMSSuppressedContacts where user.userId="+userId);
		} catch (DataAccessException e) {
			logger.error("Exception ::::" , e);
			return null;
		}
    }*/
    
    public void deleteById(Long id) {
    	getHibernateTemplate().bulkUpdate("delete from SMSSuppressedContacts where id="+ id);
    }
    
    /*public List<SMSSuppressedContacts> searchContactsById(Long userId,String searchStr) {
    	
    	String substr = "";
    	if(userId != null) {
    		
    		substr = "  user.userId="+ userId + " AND ";
    	}
    	
    	
    	return getHibernateTemplate().find("from SMSSuppressedContacts WHERE "+substr+" mobile like '%"+ searchStr +"'");
    }
    
    
    
    public List<SMSSuppressedContacts> searchContactsByMultipleMobiles(Long userId,String searchStr) {
    	
    	String substr = "";
    	if(userId != null) {
    		
    		substr = "  user_id="+ userId + " AND ";
    	}
    	
    	String qry = "SELECT * FROM sms_suppressed_contacts WHERE "+substr+" mobile REGEXP '"+ searchStr +"'";
    	
    	List<SMSSuppressedContacts> list = null;
		try {
			
			list = jdbcTemplate.query(qry, new RowMapper() {

		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	SMSSuppressedContacts smsSuppressedContacts = new SMSSuppressedContacts();
		            
		        	smsSuppressedContacts.setId(rs.getLong("id"));
		        	smsSuppressedContacts.setMobile(rs.getString("mobile"));
		        	
		        	
		        	 if(rs.getDate("suppressed_time") != null) {
			            	Calendar cal = Calendar.getInstance();
			            	cal.setTime(rs.getTimestamp("suppressed_time"));
			            	smsSuppressedContacts.setSuppressedtime(cal);
			            	
			            } 
		        	smsSuppressedContacts.setType(rs.getString("type"));
		        	
		        	Users user  = new Users();
					user.setUserId(rs.getLong("user_id"));
		        	smsSuppressedContacts.setUser(user);
		    		
		            
		            return smsSuppressedContacts;
		        }
		    });
		} catch (Exception e) {
			logger.error("Exception ***", e);
		}
		return list;
    	
    	
    	
    	
    }
    
    
    public int getTotCountByUserId(Long userId) {
    	String query = "select count(*) from SMSSuppressedContacts where user.userId="+userId;
    	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
    }*/
    
    
    public void deleteAllByUserId(Long userId) {
    	getHibernateTemplate().bulkUpdate("delete from SMSSuppressedContacts where user.userId="+ userId);
    }

    
    public int deleteFromSuppressedContacts(Users user, String mobile) {
   	 if(mobile.startsWith(""+user.getCountryCarrier())) {
  			
  			mobile = mobile.substring((""+user.getCountryCarrier()).length());
	}
    
    	
   	return getHibernateTemplate().bulkUpdate("DELETE FROM SMSSuppressedContacts WHERE "
   			+ "(user="+ user.getUserId().longValue() +" OR user IS NULL) AND mobile like '%"+ mobile +"'");
   	
   }
	
	
}
