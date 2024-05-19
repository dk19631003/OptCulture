package org.mq.captiway.scheduler.dao;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.*;
import org.mq.captiway.scheduler.beans.Opens;
import org.mq.captiway.scheduler.beans.TemplateCategory;
import org.mq.captiway.scheduler.beans.Unsubscribes;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class UnsubscribesDaoForDML extends AbstractSpringDaoForDML {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
    
	public UnsubscribesDaoForDML() {}
    private JdbcTemplate jdbcTemplate;
    
    

    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*public Unsubscribes find(Long id) {
        return (Unsubscribes) super.find(Unsubscribes.class, id);
    }*/

    public void saveOrUpdate(Unsubscribes unsubscribe) {
        super.saveOrUpdate(unsubscribe);
    }

    public void delete(Unsubscribes unsubscribe) {
        super.delete(unsubscribe);
    }

   /* public List findAll() {
        return super.findAll(Unsubscribes.class);
    }*/
    
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
   /* public int updateReason(Long unsubId, String reasonStr) {
    	return getHibernateTemplate().bulkUpdate("UPDATE Unsubscribes SET reason='" + 
    			reasonStr + "' WHERE unsubscribeId ="+unsubId);
    }*/
    
    public void deleteByEmailIdUserId(String emailId, Long userId) {
    	String queryStr = 
    		" DELETE FROM Unsubscribes WHERE emailId = '"+emailId+"' AND userId ="+userId;
    	executeUpdate(queryStr);
    }
    
    /**
     * Returns the List of categories with weight > 0 if recipient(emailId) of the user  <BR>
     * has unsubscribed for this category or = 0<BR>
     * @param emailId
     * @param userId
     * @return List of Categories
     */
    /*public List<TemplateCategory> getUnsubscribedCategories(String emailId, Long userId) {
    	
    	String query = 
    		" SELECT  category_name AS name, weightage AS weightage, ( weightage & " +
    		" IFNULL((SELECT unsub_categories_weight FROM  unsubscribes WHERE " +
    		" email_id = '"+emailId +"' AND user_id="+userId+"),0)) AS unsubWeight" +
    		" FROM template_category WHERE category_name NOT IN('My Templates','plainEditor')";
    	
    	return jdbcTemplate.query(query, new RowMapper() {

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				TemplateCategory tc = new TemplateCategory();
				tc.setCategoryName(rs.getString("name"));
				tc.setWeightage(rs.getShort("weightage"));
				
				*//**
				 *if user already unsubscribed for this category the result will be
				 *greater than 0 else 0.
				 **//*
				tc.setSelected(rs.getShort("unsubWeight") == 0 ? false:true); 
				return tc;
			}
    		
    	});
    	
    }*/
    
    
    public int updateUnsubscribe(String emailId, Long userId, String reasonStr, 
    		short unsubCategoriesWeight, Date date) {
    	
    	String query = 
    		" UPDATE unsubscribes set unsub_categories_weight="+unsubCategoriesWeight+", " +
    		" date = '" + format.format(date) + "', reason='" + reasonStr + "' WHERE " +
    		" email_id='"+emailId+"' AND user_id="+userId;
    	
    	try {
			return jdbcTemplate.update(query);
		} 
    	catch (DataAccessException e) {
			logger.error("** Exception: while updating the unsubscribe for("+
					emailId+","+userId+")", e);
			return -1;
		}
    }

/*	public List<Unsubscribes> isAlreadyUnsubscribedContact(String emailId, Long userId) {
		
		List<Unsubscribes> unsubscribedContactList = null;
		String qry = "select * from unsubscribes where user_id="+ userId + " and email_id REGEXP '" + emailId + "'";
		
		try {
			unsubscribedContactList = jdbcTemplate.query(qry, new RowMapper() {
	
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		        	Unsubscribes unsubscribedContacts = new Unsubscribes();
		            
		        	unsubscribedContacts.setEmailId(rs.getString("email_id"));
		        	unsubscribedContacts.setReason(rs.getString("reason"));
		        	unsubscribedContacts.setUnsubscribeId(rs.getLong("id"));
		        	unsubscribedContacts.setUserId(rs.getLong("user_id"));
		        	unsubscribedContacts.setUnsubcategoriesWeight(rs.getShort("unsub_categories_Weight"));
		    		
		        	if(rs.getDate("date") != null) {
		            	
		            	unsubscribedContacts.setUnsubscribedDate(rs.getTimestamp("date"));
		            }
		        	
		            return unsubscribedContacts;
		        }
		    });
		
		} catch (Exception e) {
			logger.error("Exception ***", e);
		}
		
		
		return unsubscribedContactList;
		
		
	}
*/
	/*public String isAlreadyUnsubscribedContact(Long userId, String emailId) {
		
		String status = null;
		
		try {
    		List<Unsubscribes> unsubList = null;
    		
    		unsubList =  getHibernateTemplate().find("from Unsubscribes where userId="+ userId+ " and emailId ='"+emailId+"'" );
    		if(unsubList != null && unsubList.size() > 0) {
    			
    			status = Constants.CONT_STATUS_UNSUBSCRIBED;
    			
    		}
    		
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
		}
		
		return status;
		
	}*/

}
