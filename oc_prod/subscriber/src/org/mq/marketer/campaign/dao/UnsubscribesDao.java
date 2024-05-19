package org.mq.marketer.campaign.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.SuppressedContacts;
import org.mq.marketer.campaign.beans.TemplateCategory;
import org.mq.marketer.campaign.beans.Unsubscribes;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.model.importcontact.Report;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class UnsubscribesDao extends AbstractSpringDao implements Serializable {

    public UnsubscribesDao() {}
 private JdbcTemplate jdbcTemplate;
    
    

    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
    public Unsubscribes find(Long id) {
        return (Unsubscribes) super.find(Unsubscribes.class, id);
    }

    /*public void saveOrUpdate(Unsubscribes unsubscribes) {
        super.saveOrUpdate(unsubscribes);
    }

    public void delete(Unsubscribes unsubscribes) {
        super.delete(unsubscribes);
    }*/

    public List<Unsubscribes> findAll() {
        return super.findAll(Unsubscribes.class);
    }
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public Unsubscribes findByUserId(long userId, String emailId) {
    	try {
    		List<Unsubscribes> unsubList = null;
    		
    		unsubList =  getHibernateTemplate().find("from Unsubscribes where userId="+ userId+ " and emailId ='"+emailId+"'" );
    		if(unsubList != null && unsubList.size() > 0) {
    			return unsubList.get(0);
    		}else return null;
    		
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }
    /**
     * Returns the List of categories with weight > 0 if recipient(emailId) of the user  <BR>
     * has unsubscribed for this category or = 0<BR>
     * @param emailId
     * @param userId
     * @return List of Categories
     */
    public List<TemplateCategory> getUnsubscribedCategories(String emailId, Long userId) {
    	
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
				
				/**
				 *if user already unsubscribed for this category the result will be
				 *greater than 0 else 0.
				 **/
				tc.setSelected(rs.getShort("unsubWeight") == 0 ? false:true); 
				return tc;
			}
    		
    	});
    	
    }
    /*public void deleteByEmailIdUserId(String emailId, Long userId) {
    	String queryStr = 
    		" DELETE FROM Unsubscribes WHERE emailId = '"+emailId+"' AND userId ="+userId;
    	executeUpdate(queryStr);
    }
    
    public int updateUnsubscribe(String emailId, Long userId, String reasonStr, 
    		short unsubCategoriesWeight, Calendar date) {
    	
    	String query = 
    		" UPDATE unsubscribes set unsub_categories_weight="+unsubCategoriesWeight+", " +
    		" date = '" + format.format(date.getTime()) + "', reason='" + reasonStr + "' WHERE " +
    		" email_id='"+emailId+"' AND user_id="+userId;
    	
    	try {
			return jdbcTemplate.update(query);
		} 
    	catch (DataAccessException e) {
			logger.error("** Exception: while updating the unsubscribe for("+
					emailId+","+userId+")", e);
			return -1;
		}
    }*/
	public int getTotCountByUserId(Long userId, String searchStr) {
	    	String appendQry="";
	    	if(searchStr.trim().length() > 0) {
	    		
	    		appendQry = " AND emailId like '%"+ searchStr +"%'";
	    	}
	    	
	    	String query = "select count(*) from Unsubscribes where userId="+ userId + appendQry;
	    	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
	    }
	
	 public List<Unsubscribes> findAllByUsrId(Long userId, String searchStr,int firstResult, int size) {
	    	//logger.info("type :"+ type);
	    	String appendQry="";
	    	if(searchStr.trim().length() > 0) {
	    		
	    		appendQry = " AND emailId like '%"+ searchStr +"%'";
	    	}
	    	
	    	
	    	String qryStr = "from Unsubscribes where userId="+ userId + appendQry + "ORDER BY date DESC";
	    	return executeQuery(qryStr, firstResult, size);
	    }

	public List<Unsubscribes> isAlreadyUnsubscribedContact(String contactEmailId, Long userId) {
		// TODO Auto-generated method stub
		List<Unsubscribes> unsubscribedContactList = null;
		String qry = "select * from unsubscribes where user_id="+ userId + " and email_id REGEXP '" + contactEmailId + "'";
		
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
		            	Calendar cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("date"));
		            	unsubscribedContacts.setDate(cal);
		            }
		        	
		            return unsubscribedContacts;
		        }
		    });
		
		} catch (Exception e) {
			logger.error("Exception ***", e);
		}
		
		
		
		return unsubscribedContactList;
	}
	
	public String isAlreadyUnsubscribedContact(Long userId, String contactEmailId) {
		
		String status = null;
		
		try {
    		List<Unsubscribes> unsubList = null;
    		
    		unsubList =  getHibernateTemplate().find("from Unsubscribes where userId="+ userId+ " and emailId ='"+contactEmailId+"'" );
    		if(unsubList != null && unsubList.size() > 0) {
    			
    			status = Constants.CONT_STATUS_UNSUBSCRIBED;
    			
    		}
    		
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
		}
		
		return status;
		
	}
	    
	//Changes 2.5.3.0 start

    public List<Unsubscribes> findUnsubscribesByReport(Users user,Report report) {
  	  
    	int strtIndex =0;
    	int size =1000;
		logger.info("Enter findUnsubscribesByReport");

		String qry = " FROM Unsubscribes WHERE userId=" +user.getUserId();
		
		String subQry ="";
			
		if(report.getStartDate()!=null && report.getEndDate()!=null&&!report.getStartDate().isEmpty()&&!report.getEndDate().isEmpty()){
			subQry+=" AND date BETWEEN '"+report.getStartDate()+"' AND '"+report.getEndDate()+"'";
		}
		if(report.getOffset()!=null && !report.getOffset().isEmpty()) {
			strtIndex =Integer.parseInt(report.getOffset());
		}
		if(report.getMaxRecords()!=null && !report.getMaxRecords().isEmpty()) {
			size =Integer.parseInt(report.getMaxRecords())>1000?1000:Integer.parseInt(report.getMaxRecords());
		}
	
		logger.info("QUERY FOR Unsubscribe=====>"+qry+subQry);
    	
		List<Unsubscribes> UnsubscribedContactList = executeQuery(qry+subQry,strtIndex,size);
  		
		
  		logger.info("Exit findSuppressedContactsByDates");
  		return UnsubscribedContactList;
  	  
    }  	  
  	  

	//Changes 2.5.3.0 end
	
}

