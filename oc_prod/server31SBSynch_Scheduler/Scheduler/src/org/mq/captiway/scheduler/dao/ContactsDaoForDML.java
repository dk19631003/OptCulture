package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.UserOrganization;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


@SuppressWarnings("unchecked")
public class ContactsDaoForDML extends AbstractSpringDaoForDML {


	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;
	
    public ContactsDaoForDML(){}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public void saveOrUpdate(Contacts email){
        super.saveOrUpdate(email);
    }
    
    public void merge(Contacts email){
        super.merge(email);
    }
    
    /**
	 * @deprecated
	 * @param cfList
	 * @param listId
	 */
	public void updateTempContactsWithCF(String cfList, long listId) {
		String query = "UPDATE tempcontacts t SET t.cf_value = " +
				"(SELECT CONCAT(" + cfList + " ) FROM customfield_data WHERE contact_id = t.cid)" +
				" WHERE t.list_id=" + listId;
		jdbcTemplate.update(query);
	}
	
	/**
	 * Updates the Contact's Status
	 * @param email  EmailId of Contact 
	 * @param userId User Id for which the contact belongs to.
	 * @param status Status to be changed as for the contact.
	 */
 	public void updateContactStatus(String email, long userId, String status) {
		
 		try {
 			
			/*String queryStr = 
				" UPDATE contacts SET email_status='"	+ status + "' " +
				" WHERE email_id = '" + email + "' AND  list_id IN " +
				" (select list_id FROM mailing_lists WHERE user_id =" + userId + ")";*/
			String queryStr = 
					" UPDATE contacts SET email_status='"	+ status + "' " +
					" WHERE email_id = '" + email + "' AND  user_id = " + userId ;
			jdbcTemplate.update(queryStr);
		} 
 		catch (Exception e) {
			logger.error(" ** Exception :" + e + " ** ");
		}
		
 	}
 	
 	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 	/**
 	 * Updates the last mail date of the contacts 
 	 * @param cIdsStr
 	 * @param lastMailDate
 	 * @throws Exception
 	 */
	public void updateLastMailDate(String cIdsStr, Date lastMailDate) throws Exception {
 		
		String queryStr = 
			" UPDATE contacts SET last_mail_date='"+ format.format(lastMailDate) + "'" +
			" WHERE  cid IN('"+ cIdsStr+"')";
		jdbcTemplate.update(queryStr);
		
 	}
	
	public int executeUpdateQuery(String qryStr) {
		return jdbcTemplate.update(qryStr);
	}
 	
	
 	
 	// Added for preference count
 	
 	public int updatePhoneByConcatCarrier(short mobileCarrier, Users user) {
 		
 		try {
			//update sms_tempcontacts set phone=concat('1',phone) where phone IS NOT LIKE '1%'
//			String qry = "UPDATE IGNORE sms_tempcontacts SET phone=concat('1',phone) WHERE phone  NOT LIKE '1%'";
 			//String qry = "UPDATE IGNORE sms_tempcontacts SET mobile_phone=concat('1',mobile_phone) WHERE mobile_phone  NOT LIKE '1%'";
 		//	String careerDigits = mobileCarrier+Constants.STRING_NILL;
 			UserOrganization userOrganization = user.getUserOrganization();
 			String qry = "UPDATE IGNORE sms_tempcontacts SET mobile_phone=concat('"+mobileCarrier+"',mobile_phone) "+
 			" WHERE ((LENGTH(mobile_phone)>= "+userOrganization.getMinNumberOfDigits()+") &&(LENGTH(mobile_phone)<= "+userOrganization.getMaxNumberOfDigits()+") )";

 			// 			+ " WHERE LENGTH(mobile_phone) = 10 ";//"AND mobile_phone  NOT LIKE '"+mobileCarrier+"%' ) OR ())";//	+ " WHERE LENGTH(mobile_phone) != LENGTH(mobile_phone) ";//"AND mobile_phone  NOT LIKE '"+mobileCarrier+"%' ) OR ())";
 			
			int retCount  = jdbcTemplate.update(qry);
			
			return retCount;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return 0;
		}
 		
 	}
 	
 	

public synchronized int updateSmsPreferenceTempContacts( Long categoryId) {
	
	try {
		int delCount = 0;
		if(categoryId != null  && !categoryId.equals(Constants.CAMP_CATEGORY_TRANSACTIONAL_ID)){
			
			delCount =jdbcTemplate.update(
					" DELETE FROM sms_tempcontacts WHERE (categories IS NOT NULL AND ( categories = '"+Constants.CATEGORY_NO_CATEGORY+"' OR ( categories NOT LIKE" +
					" '%,"+categoryId.toString()+"'  AND categories NOT LIKE '"+categoryId.toString()+",%'" +
						"  AND categories !='"+categoryId.toString()+"' AND categories NOT LIKE '%,"+categoryId.toString()+",%')))" +
								" OR (last_sms_date IS NOT NULL AND  DATEDIFF(DATE(NOW()),DATE(last_sms_date) ) < last_sms_span)");
			
		}
		
		return delCount;
		
		
	} catch (DataAccessException e) {
		// TODO Auto-generated catch block
		logger.error(" ** Exception : while inserting the contacts into sms_tempcontacts table", e);
		return 0;
	}catch (Exception e) {
		// TODO: handle exception
		logger.error(" ** Exception : while inserting the contacts into sms_tempcontacts table", e);
		return 0;
	}
}
	
 	
 	
 	
 	public void saveByCollection(Collection contactsCollection){ //added for EventTrigger
    	
		if(logger.isInfoEnabled()) logger.info("entered savebycollection contactsDao ");
    	try{

    		super.saveOrUpdateAll(contactsCollection);
    		
    	}
    	catch(Exception e) {

    		logger.info(" exception ");
    		logger.error("Exception ::::" , e);
    		logger.error("**Exception while saveOrUpdate ",e);
    	}
    } // saveByCollection
 	
 	/**
 	 * Updates the No of times mail is sent (double optin) for the given Contacts of mailing list.
 	 * @param listId MailingList Id for which updates should be made.
 	 * @param lastCId Last Contact Id which is selected for which Double optin mail is sent
 	 * @return
 	 */
 	public int updateOptinCount(MailingList mailingList, Long lastCId) {

 		
 		int count = -1;
		try{
			Date lastMailDate = new Date();
			/*String queryStr = 
				" UPDATE Contacts SET optin=optin+1 , lastMailDate='"+format.format(lastMailDate)+"'" +
				" WHERE mailingList.listId ="+listId+
				" AND  contactId <="+lastCId+
				" AND (lastMailDate is null OR (DATEDIFF(now(), lastMailDate) >7))" +
				" AND (optin IS null OR optin < 3) AND (emailStatus ='Optin pending')";*/
			/*String queryStr = 
					" UPDATE Contacts SET optin=optin+1 , lastMailDate='"+format.format(lastMailDate)+"'" +
					" WHERE "+listId.longValue()+"in (select distinct list_id from contacts_mlists where cid = "+lastCId+") "+
					" AND  contactId <="+lastCId+
					" AND (lastMailDate is null OR (DATEDIFF(now(), lastMailDate) >7))" +
					" AND (optin IS null OR optin < 3) AND (emailStatus ='Optin pending')";*/
			String queryStr = 
			" UPDATE Contacts SET optin=optin+1 , lastMailDate='"+format.format(lastMailDate)+"'" +
			" WHERE users = "+mailingList.getUsers().getUserId()+ " AND bitwise_and(mlBits,"+mailingList.getMlBit()+")>0 "+
			" AND  contactId <="+lastCId+
			" AND (lastMailDate is null OR (DATEDIFF(now(), lastMailDate) >7))" +
			" AND (optin IS null OR optin < 3) AND (emailStatus ='Optin pending')";
			
			
			count =  executeUpdate(queryStr);
		}catch (DataAccessException e) {
			
			logger.error("** Exception : while getting double optin contacts size by size", (Throwable)e);
		}
		return count;
 	}
 	
 	/**
 	 * Updates the Contact when recipient clicks on optin confirmation. <BR>
 	 * And also updates the last mail date as null.
 	 * @param contactId
 	 * @return
 	 */
 	public int updateEmailStatusByContactId(Long contactId) {
 		
 		try {
			String queryStr = 
				" UPDATE contacts SET email_status='Active',optin=5, last_mail_date = NULL " +
				" WHERE cid=" + contactId;
			int status = jdbcTemplate.update(queryStr);
			if(status == 1)
				return status;
			else 
				return -1;
		} catch (Exception e) {
			logger.error(" ** Exception :" + e + " ** ");
			return -1;
		}
 	}

 	
 	
 	 /*
     *  Fetch contacts from all mailinglists for the given user.
     */
 	
 	public void updateEmailStatusByUserId(String emailIdStr,Long userId,String status) {
 		
		updateEmailStatusByUserId(emailIdStr, userId, status, null);

 	}
 	
    public void updateEmailStatusByUserId(String emailIdStr,Long userId,String targetStatus,String currectStatus) {
 	   try {
 		   
 		   
 		   Calendar cal = Calendar.getInstance();
 		   Date date = cal.getTime();
 		   
 		   String formattedDate = format.format(date);
 		   
 		  String appendQry = "";
		   
		   if(currectStatus != null && currectStatus.equalsIgnoreCase(Constants.CONT_STATUS_ACTIVE)) {
			   
			  appendQry = " and email_status='"+Constants.CONT_STATUS_ACTIVE+"'";
		   }
				
 		   
 		   /*String query = "UPDATE contacts SET email_status='"+ status +"',last_status_change='"+formattedDate+"' where email_id='"+ emailIdStr +"' and list_id in " +
 		   		"(SELECT list_id FROM mailing_lists WHERE user_id =" + userId + ")";*/
 		  String query = "UPDATE contacts SET email_status='"+ targetStatus +"',last_status_change='"+formattedDate+"'" +
 		  		" where email_id='"+ emailIdStr +"' and user_id = " + userId + appendQry;
 		   int updateContContactsInt = executeUpdateQuery(query);
 		   if(logger.isDebugEnabled()) logger.debug("Total unscribed contacts are :"+ updateContContactsInt);
 	   } catch(Exception e) {
 		   logger.error("Exception : Error occured while fetching contacts by email Id "+ emailIdStr);
 	   }
    }
    
    
 	
 	/*public int updatemobileStatus(String mobile, String status, Long userId) {
 		if(mobile.startsWith("1")) {
 			
 			mobile = mobile.substring(1);
 		}
 		
 		String Qry = "UPDATE Contacts SET mobileStatus='"+status+"' WHERE mobilePhone like '%"+mobile+"' AND users="+userId.longValue();
 		
 		int count = executeUpdate(Qry);
 		
 		return count;
 	}*/
 	
 	public int updateMobileOptinStatus(String mobile, String status, Users user, String source) {
 		if(mobile.startsWith(""+user.getCountryCarrier())) {
 			
 			mobile = mobile.substring((""+user.getCountryCarrier()).length());
 		}
 		
 		String Qry = "UPDATE Contacts SET mobileStatus='"+Constants.CON_MOBILE_STATUS_ACTIVE+"',"
 				+ " mobileOptin=true, mobileOptinSource='"+source+"',mobileOptinDate='"+
 				MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR)+"'"+
 				" WHERE mobilePhone like '%"+mobile+"' AND users="+user.getUserId().longValue();
 		
 		int count = executeUpdate(Qry);
 		
 		return count;
 	}
 	
	public int updateMobileOptoutStatus(String mobile, Users user) {
 		if(mobile.startsWith(""+user.getCountryCarrier())) {
 			
 			mobile = mobile.substring((""+user.getCountryCarrier()).length());
 		}
 		
 		String Qry = "UPDATE Contacts SET mobileStatus='"+Constants.CON_MOBILE_STATUS_OPTED_OUT+"',"
 				+ " mobileOptin=false "+
 				" WHERE mobilePhone like '%"+mobile+"' AND users="+user.getUserId().longValue();
 		
 		int count = executeUpdate(Qry);
 		
 		return count;
 	}
	
 	
 	// added for DR extraction
 	
	public int updatemobileStatus(String mobile, String status, Users user) {
 		if(mobile.startsWith(""+user.getCountryCarrier())) {
 			
 			mobile = mobile.substring((""+user.getCountryCarrier()).length());
 		}
 		
 		String Qry = "UPDATE Contacts SET mobileStatus='"+status+"' WHERE mobilePhone like '%"+mobile+"' AND users="+user.getUserId().longValue();
 		
 		int count = executeUpdate(Qry);
 		
 		return count;
 	}
 	
 	public int updatemobileStatusForMultipleContacts(String mobilesStr, String status, Long userId) {
 		
 		
 		String Qry = "UPDATE contacts SET mobile_status='"+status+"' WHERE  user_id="+userId.longValue()+
 					" AND mobile_phone REGEXP '"+mobilesStr+"'" ;
 		
 		int count = executeJdbcUpdateQuery(Qry);
 		
 		return count;
 	}
 	
 	public int updatelastSMSDate(Long smsCrId, String smsDate) {
 		
 		
 		try {
			String qry = "UPDATE contacts c, sms_campaign_sent cs SET c.last_sms_date='"+smsDate+"'  WHERE cs.sms_cr_id="+smsCrId.longValue()+" AND cs.contact_id=c.cid ";
			return executeJdbcUpdateQuery(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return 0;
		}
 		
 	}
 	
 // Added for preference count
 	
  	public synchronized int createPreferenceTempContacts( Long category){
  		
  		try {
 			int count =0;
 				if(category != null && !category.equals(Constants.CAMP_CATEGORY_TRANSACTIONAL_ID)){
 					
 					String subPreQry = " DELETE FROM tempcontacts WHERE (categories IS NOT NULL AND ( categories = '"+Constants.CATEGORY_NO_CATEGORY+"' OR ( categories NOT LIKE '%,"+category.toString()+"'" +
 							"  AND categories NOT LIKE '"+category.toString()+",%'  AND categories !='"+category.toString()+"' AND categories NOT LIKE '%,"+category+",%')))"   +
 							" OR (last_mail_date IS NOT NULL AND  DATEDIFF(DATE(NOW()),DATE(last_mail_date) ) < last_mail_span )";
 					
 					logger.debug(" qry for delete :: "+subPreQry);
 					
 					count = jdbcTemplate.update(subPreQry);
 					
 				}
 			
 			
 			
 			return count;
 		} catch (DataAccessException e) {
 			// TODO Auto-generated catch block
 			logger.error(" ** Exception : while inserting the contacts into tempcontacts table", e);
 			return 0;
 		}catch (Exception e) {
 			// TODO Auto-generated catch block
 			logger.error(" ** Exception : while inserting the contacts into tempcontacts table", e);
 			return 0;
 		}
  		
  	}
 	public synchronized int createTempContacts(String qryStr, Short categoryWeight, Long userId){
		return createTempContacts(qryStr, categoryWeight, userId, true);
	}
	
 	/**
 	 * Inserts the contacts into the tempcontacts table by executing the given query string <BR>
 	 * @param qryStr query string to be executed
 	 * @param userId If userId is passed, based on this suppressed contacts will be considered and will be ignored<BR>
 	 * 		  to insert into the tempcontacts table
 	 */
 	public synchronized int createTempContacts(Short categoryWeight, Long userId, int count){
 		
 		try {
			
			if(userId != null) {
				// remove suppressed contacts
				count -= jdbcTemplate.update(
						" DELETE FROM tempcontacts WHERE email_id IN(SELECT email FROM " +
						" suppressed_contacts WHERE user_id="+userId+")");
				// remove unsubscribed contacts for this category
				count -= jdbcTemplate.update(
						" DELETE FROM tempcontacts WHERE email_id IN(SELECT email_id FROM  unsubscribes" +
						" WHERE user_id="+userId+" AND (unsub_categories_weight & "+categoryWeight+")>0)");
				
				/*//added after subscriber preference center
				if(category != null){
					String subPreQry = " DELETE FROM tempcontacts WHERE (categories IS NOT NULL AND  categories NOT LIKE '%,"+category.toString()+"'" +
							"  AND categories NOT LIKE '"+category.toString()+",%'  AND categories !='"+category.toString()+"' AND categories NOT LIKE '%,"+category+",%')"   +
							" OR DATEDIFF(DATE(NOW()),DATE(last_mail_date) ) < last_mail_span";
					
					logger.debug(" qry for delete :: "+subPreQry);
					
					count -= jdbcTemplate.update(subPreQry);
					
				}*/
			}
			return count;
			
		} catch (DataAccessException e) {
			logger.error(" ** Exception : while inserting the contacts into tempcontacts table", e);
			return 0;
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(" ** Exception : while inserting the contacts into tempcontacts table", e);
			return 0;
		}
 		
	}
 	
/**
 	 * Inserts the contacts into the tempcontacts table by executing the given query string <BR>
 	 * @param qryStr query string to be executed
 	 * @param userId If userId is passed, based on this suppressed contacts will be considered and will be ignored<BR>
 	 * 		  to insert into the tempcontacts table
 	 */
 	public synchronized int createTempContacts(String qryStr, Short categoryWeight, Long userId, boolean deleteFlag){
 		
 		try {
 			
 			if(deleteFlag) deleteTempContacts();
 			
			int count = jdbcTemplate.update(qryStr);
			
			if(userId != null) {
				// remove suppressed contacts
				count -= jdbcTemplate.update(
						" DELETE FROM tempcontacts WHERE email_id IN(SELECT email FROM " +
						" suppressed_contacts WHERE user_id="+userId+")");
				// remove unsubscribed contacts for this category
				count -= jdbcTemplate.update(
						" DELETE FROM tempcontacts WHERE email_id IN(SELECT email_id FROM  unsubscribes" +
						" WHERE user_id="+userId+" AND (unsub_categories_weight & "+categoryWeight+")>0)");
			}
			return count;
			
		} catch (DataAccessException e) {
			logger.error(" ** Exception : while inserting the contacts into tempcontacts table", e);
			return 0;
		}
 		
	}
 	
 	public void deleteTempContacts() {
 		try {
			jdbcTemplate.update("DELETE FROM tempcontacts");
		} catch (DataAccessException e) {
			logger.error(" ** Exception : while deleting the contacts from tempcontacts table", e);
		}
 	}
 	
 	  /** added for EventTrigger sms changes
 	   * Inserts the contacts into the sms_tempcontacts table by executing the given query string <BR>
 	   * @param qryStr query string to be executed
 	   * @param userId If userId is passed, based on this suppressed contacts will be considered and will be ignored<BR>
 	   *     to insert into the sms_tempcontacts table
 	   */
 	  public synchronized int createSmsTempContacts(String qryStr, Short categoryWeight, Users user, boolean deleteFlag, short mobileCarrier) {
 	   
 	   try {
 	    
 	    if(deleteFlag) deleteSmsTempContacts();
 	    
 	    if(logger.isDebugEnabled()) logger.debug("the query to be executed is====>"+qryStr);
 	   int count = jdbcTemplate.update(qryStr);
 	   
 	    //updatePhoneByConcatOne();
 	   updatePhoneByConcatCarrier(mobileCarrier,user);
 	   
 	   /*int phoneUpdatecount = jdbcTemplate.update(
 	   " DELETE FROM sms_tempcontacts WHERE  phone  NOT LIKE '1%'");*/
 	    
 	    int phoneUpdatecount = jdbcTemplate.update(
 	      " DELETE FROM sms_tempcontacts WHERE  mobile_phone  NOT LIKE '"+mobileCarrier+"%'");
 	    
 	   if(phoneUpdatecount > 0){
 	    
 	    count -= phoneUpdatecount;
 	   }
 	   /*if(userId != null) {
 	    
 	    // remove suppressed contacts
 	    count -= jdbcTemplate.update(
 	      " DELETE FROM sms_tempcontacts WHERE phone IN(SELECT mobile FROM " +
 	      " sms_suppressed_contacts WHERE user_id="+userId+" OR user_id IS NULL)");
 	    
 	    // remove unsubscribed contacts for this category
 	    count -= jdbcTemplate.update(
 	      " DELETE FROM sms_tempcontacts WHERE email_id IN(SELECT email_id FROM  unsubscribes" +
 	      " WHERE user_id="+userId+" AND (unsub_categories_weight & "+categoryWeight+")>0)");
 	   }*/
 	   
 	   return count;
 	   
 	  } catch (DataAccessException e) {
 	   logger.error(" ** Exception : while inserting the contacts into sms_tempcontacts table", e);
 	   return 0;
 	  }
 	  } 
 	  
 	 public void deleteSmsTempContacts() {
 		   
 		   try{
 		    jdbcTemplate.update("DELETE FROM sms_tempcontacts");
 		    
 		   }catch (Exception e) {
 		    logger.error(" ** Exception : while deleting the contacts from smstempcontacts table", e);
 		  }

 	 }
 	/**
  	 * 
  	 * @param emailIdsStr : emailIds of the contacts that satisfy current trigger conditions
  	 * @param listIdsStr : mailingListIds of the contacts that satisfy current trigger conditions
  	 * @return
  	 * 
  	 * This method is added for EventTrigger and is called when user
  	 * selects ET_REMOVE_FROM_CURRENT_ML_FLAG. 
  	 * It first removes all the custom fields and then the contacts from DB
  	 *       
  	 */
  	public int removeContactsFromMlUsingTempContacts() { // added for eventTrigger
 		
  		try { 
 			
  			int deleteCfdCount = 0;
  		
  			/*
  			 * First delete from customfield_data table
  			 */
  			String queryStr = 
 				" DELETE FROM customfield_data " +
 				" WHERE contact_id IN (SELECT cid " +
 				" FROM tempcontacts ) ";
 			
 			deleteCfdCount = jdbcTemplate.update(queryStr);
 			if(logger.isDebugEnabled()) logger.debug("Deleted cfds entries for "+deleteCfdCount+" contacts");

 		
 			/*
  			 * Now delete from contacts table
  			 */
 			queryStr = " DELETE FROM contacts " +
 					" WHERE cid IN ( SELECT cid " +
 					" FROM tempcontacts )";
 			
 			return jdbcTemplate.update(queryStr);
 			
 		} catch (Exception e) {
 			
 			if(logger.isErrorEnabled()) logger.error(" ** Exception while removing contacts from current Mailing List:" + e + " ** ");
 			return -1;
 		}
  	}
 	 
public synchronized int updateSmsTempContacts(Long userId,  boolean isTransactional) {
		
		try {
			
			
		//int phoneUpdateCount = updatePhoneByConcatOne();
		int delCount = 0;
		//if(userId != null) {
			
			// remove suppressed contacts
			/*delCount = jdbcTemplate.update(
					" DELETE FROM sms_tempcontacts WHERE phone IN(SELECT mobile FROM " +
					" sms_suppressed_contacts WHERE user_id="+userId+" OR user_id IS NULL)");*///Suman
		
		delCount = jdbcTemplate.update(
				" DELETE FROM sms_tempcontacts WHERE mobile_phone IN(SELECT mobile FROM " +
				" sms_suppressed_contacts WHERE user_id="+userId+" OR user_id IS NULL "+(isTransactional ? " AND type !='"+Constants.SMS_SUPP_TYPE_DND+"'" : "")+")");

			//added after subscriber preference center
			/*if(categoryId != null){
				
				delCount -=jdbcTemplate.update(
						" DELETE FROM tempcontacts WHERE (categories IS NOT NULL AND  categories NOT LIKE" +
						" '%,"+categoryId.toString()+"'  AND categories NOT LIKE '"+categoryId.toString()+",%'" +
							"  AND categories !='"+categoryId.toString()+"' AND categories NOT LIKE '%,"+categoryId.toString()+",%') OR" +
							" DATEDIFF(DATE(NOW()),DATE(last_sms_date) )< last_sms_span");
				
			}
			*/
			
			
			// remove unsubscribed contacts for this category
			/*count -= jdbcTemplate.update(
					" DELETE FROM sms_tempcontacts WHERE email_id IN(SELECT email_id FROM  unsubscribes" +
					" WHERE user_id="+userId+" AND (unsub_categories_weight & "+categoryWeight+")>0)");*/
		//}
		
		return delCount;
		
	} catch (DataAccessException e) {
		logger.error(" ** Exception : while inserting the contacts into sms_tempcontacts table", e);
		return 0;
	}catch (Exception e) {
		// TODO: handle exception
		logger.error(" ** Exception : while inserting the contacts into sms_tempcontacts table", e);
		return 0;
	}
		
}
 	 
}
