package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
//import org.mq.marketer.campaign.beans.MyStoredProcedure;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings( { "unchecked", "serial", "unused", "deprecation" })
public class ContactsDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public ContactsDaoForDML() {
	}
	
	public int executeUpdateQuery(String qryStr) {
		return jdbcTemplate.update(qryStr);
	}
 
	public void saveOrUpdate(Contacts contact) {
		super.saveOrUpdate(contact);
	}
	public void merge(Contacts contact) {
		super.merge(contact);
	}
	public void delete(Contacts contact) {
		super.delete(contact);
	}
	/**
	 * @deprecated
	 * @param listIds
	 */
	public void deleteByListIds(String listIds) {
		//Deleting the custom fields for the list
		//getHibernateTemplate().bulkUpdate("delete from CustomFieldData where contact in (select contactId from Contacts where mailingList in (" + listIds + "))");
		//getHibernateTemplate().bulkUpdate("delete from Contacts where mailingList in (" + listIds + ")");
		String qry = "DELETE FROM contacts_mlists WHERE list_id in("+listIds+")";
    	getJdbcTemplate().update(qry);
	}
	/**
	 * @deprecated
	 * @param concacts
	 * @param isCFconfigured
	 * @param contactIds
	 * @param listId
	 */
	public void deleteByCollection(Collection<Contacts> concacts, boolean isCFconfigured, String contactIds, long listId) {
		try {
		
		if(isCFconfigured){
			getHibernateTemplate().bulkUpdate("delete from CustomFieldData where contact in (" + contactIds + ")");
		}	
		//getHibernateTemplate().deleteAll(concacts);
		
		String qry = "DELETE FROM contacts_mlists WHERE list_id = "+listId+" and cid in ("+contactIds+") ";
		getJdbcTemplate().update(qry);
		}catch (Exception e) {
			logger.error("** Exception:problem deleting the contacts",e);
		}
	}
	
	/**
	 * @deprecated
	 * @param cid
	 * @param listIds
	 */
	public void deleteByCidAndListId(long cid, String listIds){
		String qry = "DELETE FROM contacts_mlists WHERE list_id in("+listIds+")"+" and cid = "+cid;
    	getJdbcTemplate().update(qry);
	}
	
	
	public boolean changeStatus(Set emailIdSet, String status) {
		try {
			Iterator iterator = emailIdSet.iterator();
			while (iterator.hasNext()) {
				Contacts emailId = (Contacts) iterator.next();
				emailId.setEmailStatus(status);
				this.saveOrUpdate(emailId);
			}
			return true;
		} catch (Exception e) {
			logger.error("** Exception  " + e.getMessage() + " **");
			return false;
		}

	}
	
	/*For mobile status*/
	public boolean changeMobileStatus(Set mobileSet, String status) {
		try {
			Iterator iterator = mobileSet.iterator();
			while (iterator.hasNext()) {
				Contacts mobile = (Contacts) iterator.next();
				mobile.setMobileStatus(status);
				this.saveOrUpdate(mobile);
			}
			return true;
		} catch (Exception e) {
			logger.error("** Exception  " + e.getMessage() + " **");
			return false;
		}

	}
	
	
	
	
	
	public void copyList(MailingList oldMl, MailingList newMl) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			/*String sql = "insert into contacts_mlists(cid,list_id) select c.cid,"+newListId 
					+" from contacts c, contacts_mlists cm where c.cid=cm.cid and cm.list_id="+ oldListId;*/
			
			String sql = " UPDATE contacts SET mlbits = mlbits + "+newMl.getMlBit().longValue()+
				  " WHERE user_id = "+oldMl.getUsers().getUserId().longValue()+
				  " AND ( mlbits & "+oldMl.getMlBit()+")>0 ";
			
			logger.debug("Query : " + sql);
			jdbcTemplate.execute(sql);
		} catch (DataAccessException dae) {
			logger.error("** DataAccessException -  " + dae + " **");
		} catch (Exception e) {
			logger.error("** Exception -  " + e + " **");
		}
	}
 	
	public long updateContMlbits(Long userId, MailingList mlist){
		
		long mlComplement = ~mlist.getMlBit();
		
		String query = "UPDATE Contacts SET mlBits = bitwise_and(mlBits, "+mlComplement+") " +
				" WHERE users ="+userId+" AND bitwise_and(mlBits, "+mlist.getMlBit()+")>0 ";
		logger.debug(" update contactmlbits query :: "+query);
		long result = executeUpdate(query);
		return result;
		
	}
	
	public void setContactFieldsOnDeletion(long userId){
		 
		byte optinVal = 0;
		
		String query = " UPDATE Contacts " +
					   " SET purged = "+new Boolean(false)+", emailStatus = '"+Constants.CONT_STATUS_PURGE_PENDING+"', "+
					   " lastStatusChange = null , lastMailDate = null, "+
					   " optin = "+optinVal+", subscriptionType = null, "+
					   " optinMedium = null "+
					   " WHERE users ="+userId +" AND mlBits = 0";
		
		logger.debug(" set contact fields on deletion query :: "+query);
		long result = executeUpdate(query);
	}
	

	public int updatemobileStatus(String mobile, String status, Users user) {
 		if(mobile.startsWith(""+user.getCountryCarrier())) {
 			
 			mobile = mobile.substring((""+user.getCountryCarrier()).length());
 		}
		String subQry="";
		boolean isIndia = user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA);
 		
		if(isIndia) {
 			
 			subQry="AND mobilePhone = '"+mobile+"'";
 			
 		}else {
 			
 			subQry ="AND mobilePhone like '%"+mobile+"'";
 		}
 		
		String Qry = "UPDATE Contacts SET mobileStatus='"+status+"' WHERE   users="+user.getUserId().longValue() +subQry;

 		
 		logger.info("updatemobileStatus query is"+Qry);
 		int count = executeUpdate(Qry);
 		
 		return count;
 	}

	
	
 	/*
     *  Fetch contacts from all mailinglists for the given user.
     */
 	
 	public void updateEmailStatusByUserId(String emailIdStr,Long userId,String status) {
 		
		updateEmailStatusByUserId(emailIdStr, userId, status, null);

 	}
 	
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
    public void updateEmailStatusByUserId(String emailIdStr,Long userId,String targetStatus,String currectStatus) {
 	   try {
 		   
 		   
 		   Calendar cal = Calendar.getInstance();
 		   Date date = cal.getTime();
 		   
 		   String formattedDate = format.format(date);
 		   String appendQry = "";
 		   
 		   if(currectStatus != null && currectStatus.equalsIgnoreCase(Constants.CONT_STATUS_BOUNCED)) {
 			   
 			  appendQry = " and email_status='"+Constants.CONT_STATUS_BOUNCED+"'";
 		   }
 				   
 				   
 		   /*String query = "UPDATE contacts SET email_status='"+ status +"',last_status_change='"+formattedDate+"' where email_id='"+ emailIdStr +"' and list_id in " +
 		   		"(SELECT list_id FROM mailing_lists WHERE user_id =" + userId + ")";*/
 				   
 		  String query = "UPDATE contacts SET email_status='"+ targetStatus +"',last_status_change='"+formattedDate+"'" +
 		  		" where email_id='"+ emailIdStr +"' and user_id = " + userId + appendQry;
 		   int updateContContactsInt = executeUpdateQuery(query);
 		   logger.debug("Total unscribed contacts are :"+ updateContContactsInt);
 	   } catch(Exception e) {
 		   logger.error("Exception : Error occured while fetching contacts by email Id "+ emailIdStr);
 	   }
    } 
    public void updateEmailStatusByAdmin(String emailIdStr,Long userId,String targetStatus) {

   	   try {
   		   
   		   Calendar cal = Calendar.getInstance();
   		   Date date = cal.getTime();
   		   
   		   String formattedDate = format.format(date);
   				   
   		  String query = "UPDATE contacts SET email_status='"+ targetStatus +"',last_status_change='"+formattedDate+"'"+ 
   		  "where user_id = " + userId +" AND email_id IN("+ emailIdStr +")" + "and email_status IN "
   		  		+ "('"+Constants.CONT_STATUS_SUPPRESSED+"','"+Constants.CONT_STATUS_BOUNCED+"','"+Constants.CONT_STATUS_UNSUBSCRIBED+"')";
   		  logger.info(" query for changing status :: -----> "+query);
   		  
   		   int updatedContactsInt = executeUpdateQuery(query);
   		   logger.debug("Updated status for "+ updatedContactsInt + "contacts");
   		   
   	   } catch(Exception e) {
   		   logger.error("Exception : Error occured while fetching contacts by status "+ emailIdStr);
   		   logger.info("errorrrr++++",e);
   	   }
      
    }
    
    
    public void updateEmailStatusByStatus(String emailIdStr,Long userId,String targetStatus,String currentStatus) {
  	   try {
  		   
  		   Calendar cal = Calendar.getInstance();
  		   Date date = cal.getTime();
  		   
  		   String formattedDate = format.format(date);
  		   String appendQry = "";
  		   
  		   if(currentStatus != null ) {
  			   
  			  appendQry = " and email_status='"+currentStatus+"'";
  			  
  		   }
  				   
  		  String query = "UPDATE contacts SET email_status='"+ targetStatus +"',last_status_change='"+formattedDate+"'"+ "where user_id = " + userId +" AND email_id IN("+ emailIdStr +")" + appendQry;
  		  logger.info(" query for changing status :: -----> "+query);
  		  
  		   int updatedContactsInt = executeUpdateQuery(query);
  		   logger.debug("Updated status for "+ updatedContactsInt + "contacts");
  		   
  	   } catch(Exception e) {
  		   logger.error("Exception : Error occured while fetching contacts by status "+ emailIdStr);
  		   logger.info("errorrrr++++",e);
  	   }
     }
    
    
    //APP-1977
    public int updateMobileOptinStatus(String mobile, String status, Users user) {//?
 		if(mobile.startsWith(""+user.getCountryCarrier())) {
 			
 			mobile = mobile.substring((""+user.getCountryCarrier()).length());
 		}
 		
 		String Qry = "UPDATE Contacts SET mobileStatus='"+Constants.CON_MOBILE_STATUS_ACTIVE+"',"
 				+ " mobileOptin=true,mobileOptinDate='"+
 				MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR)+"'"+
 				" WHERE mobilePhone like '%"+mobile+"' AND users="+user.getUserId().longValue();
 		
 		int count = executeUpdate(Qry);
 		
 		return count;
 	}
 	
   /* public void updateEmailStatusById(Long Id, String email, String type,String targetStatus,String currentStatus) {
   	   try {
   		   
   		   
   		   Calendar cal = Calendar.getInstance();
   		   Date date = cal.getTime();
   		   
   		   String formattedDate = format.format(date);
   		   //String appendQry1 = "";
   		   
   		   
   		   if(currentStatus != null ) {
   			   
   			  //appendQry1 = " email_status='"+currentStatus+"";
   			   
   		   }	   
   		  String query = " UPDATE contacts SET email_status='"+ targetStatus +"',last_status_change='"+formattedDate+" where email_id =" +email+ "and email_status='"+currentStatus+"'";
   		  
   		   int updatedContactsInt = executeUpdateQuery(query);
   		   logger.debug("Total status changed contacts are :"+ updatedContactsInt);
   		   
   	   } catch(Exception e) {
   		   logger.error("Exception : Error occured while fetching contacts by Id "+ Id);
   		   logger.info("errorrrr++++",e);
   	   }
      }*/
    
    
    public void updateEmailStatus(Long userId, String targetStatus,String currentStatus) {
    	   try {
    		   
    		   
    		   Calendar cal = Calendar.getInstance();
    		   Date date = cal.getTime();
    		   
    		   String formattedDate = format.format(date);
    		   String appendQry = "";
    		   
    		   
    		   if(currentStatus != null ) {
    			   
    			  appendQry = " and email_status='"+currentStatus+"'";
  
    		   }	   
    		  String query = " UPDATE contacts SET email_status='"+ targetStatus +"',last_status_change='"+formattedDate+"' where user_id = " + userId + appendQry;
    		  
    		   int updatedContactsInt = executeUpdateQuery(query);
    		   logger.debug("Total status changed contacts are :"+ updatedContactsInt);
    		   
    	   } catch(Exception e) {
    		   logger.error("Exception : Error occured while fetching contacts by Id "+ userId);
    		   logger.info("errorrrr++++",e);
    	   }
    }
    public void updateCreatedDateForBulkEnrollment(Long userId, Long contactId, Calendar cal) throws Exception{
    	
    	try{
    	String qry = " UPDATE Contacts SET createdDate = '"+cal +"' WHERE  contactId = "+contactId;
		int updatedContact = executeUpdate(qry);
		logger.debug("Updated contacts are :"+ updatedContact);
    	}catch(Exception e){
 		   logger.info("Exception while updating createdDate ",e);
    	}
    	

	}
    
    public void updateInstanceID(String instanceID, Long cid){
    	
    	try {
			String updateQry = "UPDATE contacts SET instance_id='"+instanceID+"' WHERE cid="+cid.longValue();
			
			executeUpdateQuery(updateQry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("Exception while updating the instanc eID", e);
		}
    }
	
}
