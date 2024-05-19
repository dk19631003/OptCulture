package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.SuppressedContacts;
import org.mq.marketer.campaign.beans.Users;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SMSSuppressedContactsDaoForDML extends AbstractSpringDaoForDML{

	


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

    public void saveOrUpdate(SMSSuppressedContacts SMSSuppressedContacts) {
        super.saveOrUpdate(SMSSuppressedContacts);
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
			logger.error("Exception ::" , e);
			return null;
		}
    }
    
    public List<String> findAllTypeByUserId(Long userId) {
    	try {
			return getHibernateTemplate().find("select distinct type from SMSSuppressedContacts where user.userId=" + userId + "group by type order by count(type) desc");
		} catch (DataAccessException e) {
			logger.error("Exception ::" , e);
			return null;
		}
    }*/
    
    public void deleteById(Long id) {
    	getHibernateTemplate().bulkUpdate("delete from SMSSuppressedContacts where id="+ id);
    }
    
    /*public List<SMSSuppressedContacts> searchContactsById(Long userId,String searchStr) {
    	return getHibernateTemplate().find("from SMSSuppressedContacts where user.userId="+ userId + " and mobile like '%"+ searchStr +"'");
    }
    
    public int getTotCountByUserId(Long userId) {
    	String query = "select count(*) from SMSSuppressedContacts where user.userId="+userId;
    	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
    }*/
    
    
    public void deleteAllByUserId(Long userId, String type) {
    	getHibernateTemplate().bulkUpdate("delete from SMSSuppressedContacts where user.userId="+ userId+" AND type='"+type+"'");
    }
    
     public void deleteMblByUserId(Users user, String mobile) {
    	 
    	 if(mobile.startsWith(""+user.getCountryCarrier())) {
  			
  			mobile = mobile.substring((""+user.getCountryCarrier()).length());
  		}
    	 
    	String queryStr = 
        		" DELETE FROM SMSSuppressedContacts WHERE mobile like '%"+ mobile+"' AND (user ="+user.getUserId()+" OR user IS NULL) ";
        	executeUpdate(queryStr);
    	
    }
     //APP-1977
     public int deleteFromSuppressedContacts(Users user, String mobile) {
       	 if(mobile.startsWith(""+user.getCountryCarrier())) {
      			
      			mobile = mobile.substring((""+user.getCountryCarrier()).length());
    	}
        
        	
       	return getHibernateTemplate().bulkUpdate("DELETE FROM SMSSuppressedContacts WHERE "
       			+ "(user="+ user.getUserId().longValue() +" OR user IS NULL) AND mobile like '%"+ mobile +"'");
       	
       }
     
     
     
     
     
 /*public int getCountByReport(Long userId, String campRepLists, Long repId) {
    	
    	
    	String queryStr = "SELECT COUNT(s.id) FROM SuppressedContacts s,Contacts c where s.user="+userId+"" +
						" AND s.email=c.emailId  AND c.mailingList in(SELECT m.listId FROM MailingList m where m.users="+userId+")";

    	String queryStr = "SELECT COUNT(s.id) FROM SuppressedContacts s,CampaignSent cs where s.user="+userId+"" +
						  " AND s.email=cs.emailId  AND cs.campaignReport="+campRepId; 
    	
    	
    	String queryStr = "SELECT COUNT(DISTINCT s.mobile) FROM SMSSuppressedContacts s,Contacts c WHERE s.user="+userId+"" +
		  "  AND c.mailingList  IN (SELECT listId FROM MailingList WHERE listName IN("+campRepLists+")) AND s.mobile=c.phone"; 
    	
	 
	 
    	String queryStr = "SELECT COUNT(DISTINCT s.mobile) FROM SMSSuppressedContacts s,Contacts c, MailingList m WHERE s.user="+userId+" AND c.users="+userId+" AND m.users="+userId+
		  "  AND m.listName IN ("+campRepLists+") AND bitwise_and(c.mlBits, m.mlBit)>0 AND c.mobilePhone IS NOT NULL AND s.mobile LIKE CONCAT('%', c.mobilePhone, '%') ";

    	
    	int count = ((Long)getHibernateTemplate().find(queryStr).get(0)).intValue();
    	
    	return count;
    	
    }
    
 public List<Object[]> getSuppressedContactsByReport(Long userId, String ListNames,Long repId, String suppressedTime,
		 int startFrom, int count) {
 	
 	
 	try {
			List<Object[]> catList = new ArrayList<Object[]>();
			
			String queryStr = "SELECT s.email,s.type FROM SuppressedContacts s,Contacts c where s.user="+userId+"" +
							" AND s.email=c.emailId AND c.mailingList in(SELECT m.listId FROM MailingList m where m.users="+userId+")";
			
			
			
			String queryStr = "SELECT DISTINCT s.mobile,s.type FROM SMSSuppressedContacts s,Contacts c WHERE s.user="+userId+"" +
		  "  AND c.mailingList  IN (SELECT listId FROM MailingList WHERE listName IN("+ListNames+")) AND s.mobile=c.phone";

			
			String queryStr = "SELECT DISTINCT s.mobile,s.type FROM SMSSuppressedContacts s,SMSCampaignSent c, MailingList m WHERE c.smsCampaignReport="+repId+" AND s.user="+userId+"" +
			  "  AND m.listName IN ("+ListNames+") AND s.mobile LIKE CONCAT('%', c.mobileNumber, '%')" ;


			
			String queryStr = " SELECT DISTINCT s.email, s.type FROM SuppressedContacts s,Contacts c, MailingList m WHERE s.user="+userId+" AND c.users="+userId+" AND m.users="+userId+
			" AND m.listName IN ("+ListNames+") AND bitwise_and(c.mlBits, m.mlBit)>0 AND c.emailId IS NOT NULL AND c.emailId !='' AND  s.email=c.emailId AND s.suppressedtime < '"+suppressedTime+"'" ;
			
			
			
			String queryStr = "SELECT DISTINCT s.mobile,s.type FROM SMSSuppressedContacts s,Contacts c, MailingList m  WHERE " +
							" (s.user="+userId+" OR s.user IS NULL ) AND c.users="+userId+" AND m.users="+userId+ " AND m.listName IN ("+ListNames+") " +
							" AND bitwise_and(c.mlBits, m.mlBit)>0  AND c.mobilePhone IS NOT NULL AND s.mobile LIKE CONCAT('%', c.mobilePhone, '%') AND s.suppressedtime < '"+suppressedTime+"'" ;


			
			catList = executeQuery(queryStr, startFrom, count);
			
			//logger.info("list size :: "+catList.size());
			return catList;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
 	
 }
 
 public int getTotCountByUserId(Long userId, String type, String searchStr) {
 	
 	String appendQry="";
 	if(searchStr.trim().length() > 0) {
 		
 		appendQry = " AND mobile like '%"+ searchStr +"%'";
 	}
 	
 	String query = "select count(*) from SMSSuppressedContacts where user.userId=" + userId + " AND type='" + type + "'"+appendQry;
 	if(type.equalsIgnoreCase("all")) {
 		query = "select count(*) from SMSSuppressedContacts where user.userId=" + userId + " AND type like '%%'"+appendQry;
 	}
 	return ((Long)((getHibernateTemplate().find(query)).get(0))).intValue();
 }
 
 public List<SMSSuppressedContacts> findAllByUsrId(Long userId, String type, String searchStr,int firstResult, int size) {
 	logger.info("type :"+ type);
 	
 	String appendQry="";
 	if(searchStr.trim().length() > 0) {
 		
 		appendQry = " AND mobile like '%"+ searchStr +"%'";
 	}
 	
 	String qryStr = "from SMSSuppressedContacts where user.userId="+ userId +" and type='" + type + "'"+appendQry;
 	if(type.equalsIgnoreCase("all")){
 		qryStr = "from SMSSuppressedContacts where user.userId="+ userId +" and type like '%%'"+appendQry;
 	}
 	return executeQuery(qryStr, firstResult, size);
 }
 
 public boolean isMobileOptedOut(Long userId, String mobile){
	 String query = "from SMSSuppressedContacts where user.userId="+ userId +" and mobile='" + mobile + "'";
	 
	 List<SMSSuppressedContacts> list = executeQuery(query);
	 if(list.size() > 0) return true;
	 else return false;
 }
 
 
 public List<SMSSuppressedContacts> searchContactsByMultipleMobiles(Long userId,String searchStr) {
 	
 	String substr = "";
 	if(userId != null) {
 		
 		substr = "  user_id="+ userId + " AND ";
 	}
 	
 	String qry = "SELECT * FROM sms_suppressed_contacts WHERE "+substr+" mobile REGEXP '"+ searchStr +"'";
 	logger.info("jdbcTemplate>>>>>>>"+jdbcTemplate);
 	logger.info("qry>>>>>>>"+qry);
 	
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
 	
 	
 	
 	
 }*/
     
}
