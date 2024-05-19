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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mq.captiway.scheduler.beans.Bounces;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.UserOrganization;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.captiway.scheduler.utility.ClickHouseDBConnection;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


@SuppressWarnings("unchecked")
public class ContactsDao extends AbstractSpringDao {


	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;
	
    public ContactsDao(){}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public Contacts find(Long id){
        return (Contacts) super.find(Contacts.class, id);
    }

    public List<Contacts> findAll(){
        return super.findAll(Contacts.class);
    }

    public Contacts findById(Long id) {
    	
    	List<Contacts> contactList = getHibernateTemplate().find("FROM Contacts WHERE contactId = "+ id);
    	if(contactList.size()>0){
    		return contactList.get(0);
    	}
        return null;
    }

    /**
     *  Returns the no of contacts in the given mailing lists
     * @param mlIds mlIds is a String of comma seperated mailing list id's
     * @param status Status is Contact's status
     * @return total count of contacts
     */
	public int getEmailCount(String mlIds, String status) {
		
		List<Long> contactList = getHibernateTemplate().find(
				" SELECT COUNT(DISTINCT c.emailId) FROM Contacts c join c.mlSet ml WHERE ml.list_id IN ("+ mlIds+")" +
				" AND c.emailStatus LIKE '"+status+"'" );
		
		if(contactList != null && contactList.size() >0) {
			return (contactList.get(0)).intValue();
		}
		return 0;
	}
	public int getAvailableContactsFromSmsTempContacts(String mlIds, String subquery) {
		try {
			/*List<Long> contactList = getHibernateTemplate().find(
					"select count(contactId) from Contacts where mailingList in("+mlIds+") and phone > 0" );
			*/
			/*if(contactList != null && contactList.size() >0) {
				return (contactList.get(0)).intValue();
			}
			return 0;*/
			//String countQry = "SELECT COUNT(st.cid) FROM sms_tempcontacts st, contacts_mlists cm where st.cid = cm.cid and cm.list_id in("+mlIds+") and st.mobile_phone is not null";
/*			Iterator<MailingList> mlIt = mlSet.iterator();
			
			String mlStr="";
			MailingList tempMl;
			
			while(mlIt.hasNext()) {
				tempMl = mlIt.next();
				if(mlStr.length() > 0) mlStr += ",";
				mlStr += tempMl.getListId().longValue();
				
			} // while
			
			Long userId = mlIt.next().getUsers().getUserId();
			long mlsbit = Utility.getMlsBit(mlSet);
*/			
			
			
			String countQry = "SELECT COUNT(st.cid) FROM sms_tempcontacts st, mailing_lists cm " +
				" WHERE cm.list_id  IN ("+ mlIds + ") AND st.user_id = cm.user_id "+
				" AND (st.mlbits & cm.mlbit )>0 and st.mobile_phone is not null AND mobile_status='Active' "+subquery;
			
			
			
			return jdbcTemplate.queryForInt(countQry);
		} catch (DataAccessException e) {
			logger.error(" Exception : ",(Throwable)e);
			return 0;
		}
		
	}
	public int getAvailableContactsFromWATempContacts(String mlIds, String subquery) {
		try {
			
			String countQry = "SELECT COUNT(wt.cid) FROM wa_tempcontacts wt, mailing_lists ml " +
				" WHERE ml.list_id  IN ("+ mlIds + ") AND wt.user_id = ml.user_id "+
				" AND (wt.mlbits & ml.mlbit )>0 and wt.mobile_phone is not null AND mobile_status='Active' "+subquery;
			
			return jdbcTemplate.queryForInt(countQry);
		} catch (DataAccessException e) {
			logger.error(" Exception : ",(Throwable)e);
			return 0;
		}
		
	}	
	public int getSegmentedContactsCount(String qry) {
		
		try {
			
			String countQry = "SELECT COUNT(*) FROM ("+qry +") AS tempCount";
			return jdbcTemplate.queryForInt(countQry);
		} 
		catch (DataAccessException e) {
			logger.error(" Exception : ",(Throwable)e);
			return 0;
		}
	}
	
	//APP-4127 ClickHouse
	public int getSegmentedContactsCountFromCH(String qry) {
		try {
			String countQry = "SELECT COUNT(*) as count FROM (" + qry + ") AS tempCount";
			ClickHouseDBConnection clickHouseConn = new ClickHouseDBConnection();
			ResultSet resultSet = clickHouseConn.getClickHouseConnectionAndRunQuery(countQry);
			int count = 0;
			
			try {
				while(resultSet.next()) {					
					count = resultSet.getInt("count");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("count of segmeneted contatcs "+count );
			return count;
			//return jdbcTemplate.queryForInt(countQry);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error(" Exception : ", (Throwable) e);
			return 0;
		}
	}

	public List<Contacts> getSegmentedContacts(String qry, int startIndex, int size) {
		
		List<Contacts> list = null;
		try {
			
			list = jdbcTemplate.query(qry+" LIMIT "+startIndex+", "+size, new RowMapper() {

		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		            Contacts contact = new Contacts();
		            
		            //MailingList tempMailingList = new MailingList();
		    		//tempMailingList.setListId(rs.getLong("list_id"));
		    		//contact.setMailingList(tempMailingList);
		    		//Set<MailingList> mlset = new HashSet<MailingList>();
		    		//mlset.add(tempMailingList);
		    		//contact.setMlSet(mlset);
		    		
		            contact.setContactId(rs.getLong("cid"));
		            contact.setEmailId(rs.getString("email_id"));
		            contact.setFirstName(rs.getString("first_name"));
		            contact.setLastName(rs.getString("last_name"));
		            contact.setAddressOne(rs.getString("address_one"));
		            contact.setAddressTwo(rs.getString("address_two"));
		            contact.setCity(rs.getString("city"));
		            contact.setState(rs.getString("state"));
		            contact.setCountry(rs.getString("country"));
//		            contact.setPin(rs.getInt("pin"));
		            contact.setPurged(rs.getBoolean("purged"));
		            contact.setOptin(rs.getByte("optin"));
//		            contact.setPhone(rs.getLong("phone"));
		            contact.setZip(rs.getString("zip"));
		            contact.setMobilePhone(rs.getString("mobile_phone"));
		            contact.setMobileStatus(rs.getString("mobile_status"));
		            contact.setGender(rs.getString("gender"));
		            contact.setHomeStore(rs.getString("home_store"));
		            contact.setExternalId(rs.getString("external_id"));
		            contact.setInstanceId(rs.getString("instance_id"));
		            contact.setPushNotification(rs.getBoolean("push_notification"));
		            contact.setDeviceType(rs.getString("device_Type"));
		            contact.setCategories(rs.getString("categories"));
		            contact.setLastMailSpan(rs.getInt("last_mail_span"));
		            contact.setLastSmsSpan(rs.getInt("last_sms_span"));
		          
		            
		           /* if(rs.getDate("birth_day") != null) {
		            	Calendar cal = Calendar.getInstance();
		            	cal.setTime(rs.getDate("birth_day"));
		            	contact.setBirthDay(cal);
		            } else {
		            	contact.setBirthDay(null);
		            }
		            
		            if(rs.getDate("anniversary_day") != null) {
		            	Calendar cal = Calendar.getInstance();
		            	cal.setTime(rs.getDate("anniversary_day"));
		            	contact.setAnniversary(cal);
		            } else {
		            	contact.setAnniversary(null);
		            }
		            
		            contact.setCreatedDate(rs.getDate("created_date"));
		            contact.setOptinStatus(rs.getBoolean("optin_status"));
		            contact.setLastStatusChange(rs.getDate("last_status_change"));
		            contact.setLastMailDate(rs.getDate("last_mail_date"));*/
		            
		            
		            
		            Calendar cal = null;
		            if(rs.getDate("birth_day") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("birth_day"));
		            	contact.setBirthDay(cal);
		            } else {
		            	contact.setBirthDay(null);
		            }
		            
		            if(rs.getDate("anniversary_day") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("anniversary_day"));
		            	contact.setAnniversary(cal);
		            } else {
		            	contact.setAnniversary(null);
		            }
		            if(rs.getDate("created_date") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("created_date"));
		            	contact.setCreatedDate(cal);
		            } else {
		            	contact.setCreatedDate(null);
		            }
		           
		            contact.setLastStatusChange(rs.getDate("last_status_change"));
		            contact.setLastMailDate(rs.getDate("last_mail_date"));
		            
		            contact.setEmailStatus(rs.getString("email_status"));
					contact.setOptin(rs.getByte("optin"));
					contact.setSubscriptionType(rs.getString("subscription_type"));
					contact.setPhValueStr(rs.getString("cf_value"));
					contact.setLoyaltyCustomer(rs.getByte("loyalty_customer"));
					Users user  = new Users();
					user.setUserId(rs.getLong("user_id"));
					contact.setUsers(user);
					contact.setMlBits(rs.getLong("mlbits"));
					contact.setUdf1(rs.getString("udf1"));
					contact.setUdf2(rs.getString("udf2"));
					contact.setUdf3(rs.getString("udf3"));
					contact.setUdf4(rs.getString("udf4"));
					contact.setUdf5(rs.getString("udf5"));
					contact.setUdf6(rs.getString("udf6"));
					contact.setUdf7(rs.getString("udf7"));
					contact.setUdf8(rs.getString("udf8"));
					contact.setUdf9(rs.getString("udf9"));
					contact.setUdf10(rs.getString("udf10"));
					contact.setUdf11(rs.getString("udf11"));
					contact.setUdf12(rs.getString("udf12"));
					contact.setUdf13(rs.getString("udf13"));
					contact.setUdf14(rs.getString("udf14"));
					contact.setUdf15(rs.getString("udf15"));
					
					
		            return contact;
		        }
		    });
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		return list;
	}
	
	/**
	 * added for eventTrigger
	 * 
	 * @param qry
	 * @param startIndex
	 * @param size
	 * @return
	 * 
	 * Note: as we are fetching from tempcontacts there wont be any complete MailingList object
	 * so we will just set the list id and create a dummy mailingList obj
	 */
	public List<Contacts> getSegmentedTempContacts(String qry, int startIndex, int size) {
		
		List<Contacts> list = null;

		try {
			
			list = jdbcTemplate.query(qry+" LIMIT "+startIndex+", "+size, new RowMapper() {
		
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		    		
		        	//MailingList tempMailingList = new MailingList();
		    		//tempMailingList.setListId(rs.getLong("list_id"));
		    		
		            Contacts contact = new Contacts();
		            contact.setContactId(rs.getLong("cid"));
		            //Set<MailingList> mlset = new HashSet<MailingList>();
		            //mlset.add(tempMailingList);
		            //contact.setMlSet(mlset);
		            //contact.setMailingList(tempMailingList);
		           
		            contact.setEmailId(rs.getString("email_id"));
		            contact.setFirstName(rs.getString("first_name"));
		            contact.setLastName(rs.getString("last_name"));
		            
		            //contact.setCreatedDate(rs.getDate("created_date"));
		            
		            
		            contact.setPurged(rs.getBoolean("purged"));
		            contact.setOptinStatus(rs.getBoolean("optin_status"));
		            contact.setEmailStatus(rs.getString("email_status"));
		            contact.setAddressOne(rs.getString("address_one"));
		            contact.setAddressTwo(rs.getString("address_two"));
		            contact.setCity(rs.getString("city"));
		            contact.setState(rs.getString("state"));
		            contact.setCountry(rs.getString("country"));
		            /*contact.setPin(rs.getInt("pin"));
		            contact.setPhone(rs.getLong("phone"));*/
		            contact.setZip(rs.getString("zip"));
		            contact.setMobilePhone(rs.getString("mobile_phone"));
					contact.setHomeStore(rs.getString("home_store"));
		            contact.setExternalId(rs.getString("external_id"));
		            
		            contact.setGender(rs.getString("gender"));
		            
		            
		            contact.setCategories(rs.getString("categories"));
		            contact.setLastMailSpan(rs.getInt("last_mail_span"));
		            contact.setLastSmsSpan(rs.getInt("last_sms_span"));
			           
		           /* if(rs.getDate("birth_day") != null) {
		            	Calendar cal = Calendar.getInstance();
		            	cal.setTime(rs.getDate("birth_day"));
		            	contact.setBirthDay(cal);
		            } else {
		            	contact.setBirthDay(null);
		            }
		            
		            if(rs.getDate("anniversary_day") != null) {
		            	Calendar cal = Calendar.getInstance();
		            	cal.setTime(rs.getDate("anniversary_day"));
		            	contact.setAnniversary(cal);
		            } else {
		            	contact.setAnniversary(null);
		            }*/
		            
		            Calendar cal = null;
		            if(rs.getDate("birth_day") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("birth_day"));
		            	contact.setBirthDay(cal);
		            } else {
		            	contact.setBirthDay(null);
		            }
		            
		            if(rs.getDate("anniversary_day") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("anniversary_day"));
		            	contact.setAnniversary(cal);
		            } else {
		            	contact.setAnniversary(null);
		            }
		            if(rs.getDate("created_date") != null) {
		            	cal = Calendar.getInstance();
		            	cal.setTime(rs.getTimestamp("created_date"));
		            	contact.setCreatedDate(cal);
		            } else {
		            	contact.setCreatedDate(null);
		            }
		           
		            contact.setLastStatusChange(rs.getDate("last_status_change"));
		            contact.setLastMailDate(rs.getDate("last_mail_date"));
		            
		            
		            contact.setLastStatusChange(rs.getDate("last_status_change"));
		            contact.setLastMailDate(rs.getDate("last_mail_date"));
		            
		            contact.setOptin(rs.getByte("optin"));
		            contact.setSubscriptionType(rs.getString("subscription_type"));
		            contact.setLoyaltyCustomer(rs.getByte("loyalty_customer"));
		            Users user = new Users();
		            user.setUserId(rs.getLong("user_id"));
					contact.setUsers(user);
					contact.setMlBits(rs.getLong("mlbits"));
					contact.setUdf1(rs.getString("udf1"));
					contact.setUdf2(rs.getString("udf2"));
					contact.setUdf3(rs.getString("udf3"));
					contact.setUdf4(rs.getString("udf4"));
					contact.setUdf5(rs.getString("udf5"));
					contact.setUdf6(rs.getString("udf6"));
					contact.setUdf7(rs.getString("udf7"));
					contact.setUdf8(rs.getString("udf8"));
					contact.setUdf9(rs.getString("udf9"));
					contact.setUdf10(rs.getString("udf10"));
					contact.setUdf11(rs.getString("udf11"));
					contact.setUdf12(rs.getString("udf12"));
					contact.setUdf13(rs.getString("udf13"));
					contact.setUdf14(rs.getString("udf14"));
					contact.setUdf15(rs.getString("udf15"));
					
		            return contact;
		        }
		    });
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		return list;
	} // getSegmentedTempContacts
/**
 * @deprecated
 * @return
 * added for eventTrigger
 * For OPENS and CLICKS  we can't rely on list_ids defined on the campaign 
 * (since campaign list_ids can be modified). So we take list_ids associated at the contacts level
 * from final tempcontacts table 
 */
public String getMlsForOpensClicksFromTempContacts(){
	try{

		if(logger.isDebugEnabled()) logger.debug("inside getMlsForOpensClicksFromTempContacts ");

		List<Long> mlistIds;
		String listIdsStr = "",qryStr = "";
		qryStr += " SELECT DISTINCT list_id FROM tempcontacts ";

		if(logger.isInfoEnabled()) logger.info("Executing query "+qryStr);
		
		mlistIds = jdbcTemplate.queryForList(qryStr, Long.class);
		
		for(Long listId : mlistIds) {
			
			if(listIdsStr.length() == 0) {
				listIdsStr += listId;
			}
			else {
				listIdsStr += ","+listId;
			}
		}
		return listIdsStr;
	}
	catch(Exception e){
		
		logger.error("**Exception",e);
		return null;
	}
} // getMlsForOpensClicksFromTempContacts
	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 	
	
	public int getTotalCountOfAvailableContacts(String qry) {
		
		
		return ((Long)getHibernateTemplate().find(qry).get(0)).intValue();
	}
	
	
	
 	
  	
 	
/**
 	 * Added for EventTrigger
 	 * 
 	 * @param mailingList
 	 * @param emailId
 	 * @return
 	 * 
 	 * checking if a given contact is present in the ML.
 	 * Separated the logic into two queries since 
 	 */
 	public Long getContactIdByEmailIdAndMlist(MailingList mailingList,String emailId ){

 		try {

 			String queryStr = " FROM Contacts c " +
 			" WHERE c.users = mailingList.users and bitwise_and(c.mlBits,"+mailingList.getMlBit().longValue()+" ) AND c.emailId LIKE '"+emailId+"' " +
 			" AND ml.listId = "+mailingList.getListId()+" ";

 			if(logger.isInfoEnabled()) logger.info("query is "+queryStr);
 			List<Contacts> contactList = getHibernateTemplate().find(queryStr);

 			if(contactList != null && contactList.size() >0 ) {

 				//logger.debug("not null returning contact id"+contactList.get(0).getContactId());
 				return contactList.get(0).getContactId();
 			}

 			//logger.debug("null obj returning zero");
 			return 0l;

 		}
 		catch(Exception e) {

 			logger.info("**Exception ",e);
 			return null;
 		}

 	} // getIdByEmailIdAndMailingList
 	
 	/**
 	 * Added for EventTrigger
 	 * 
 	 * @param emailIdsListStrBuf
 	 * @param copyToMlListStr
 	 * @return
 	 * 
 	 * This method checks if the emailIds are present in destination mailingLists.
 	 */
 	public List<Contacts> getContactsByEmailIdsListAndMlist(StringBuffer emailIdsListStrBuf , String copyToMlListStr){
 		
 		//logger.debug("entered getContactIdByEmailId...");

 		List<Contacts> list = null;
 		try {
 			String queryStr = " SELECT DISTINCT c FROM Contacts c join c.mlSet ml" +
 			" WHERE c.emailId IN ("+emailIdsListStrBuf+") " +
 			" AND ml.list_id IN ("+copyToMlListStr+") ";
 			
 			if(logger.isDebugEnabled()) logger.debug(" query "+queryStr);
 			list =  getHibernateTemplate().find(queryStr);
 			
 			} catch (Exception e) {
 			
 			if(logger.isInfoEnabled()) logger.info("**Exception ");
 			logger.error("Exception ::::" , e);
 		}
 		return list; 			

 	} // getContactsByEmailIdsAndMlList 
 	
 	
	/**
 	 * Returns the list of Contacts to send 'Double optin mails'  
 	 * @param listIdsStr String of mailing list Id's for which double optin is enabled
 	 * @param contactId  Last fetched Contact's Id 
 	 * @param size Size of Contacts to be returned
 	 * @return List of Contact objects
 	 */
 	public List<Contacts> getDoubleOptinContacts(String listIdsStr, Long contactId, int size) {
 		
		try{
			String queryStr = 
				" select distinct c FROM Contacts c join c.mlSet ml WHERE ml.list_id IN ("+ listIdsStr +") AND  c.contactId >"+contactId+
				" AND (c.lastMailDate is null OR (DATEDIFF(now(), c.lastMailDate) >7))" +
				" AND (c.optin IS null OR c.optin < 3) AND (c.emailStatus ='Optin pending') ORDER BY c.contactId ";
			
			return executeQuery(queryStr, 0, size);
			
		}
		catch (DataAccessException e) {
			logger.error("** Exception : while getting double optin contacts size by size", (Throwable)e);
			return null;
		}
		
	}
 	
 	/**
 	 * this method retrieves all the contacts of given mailinglist(called in AutoProgramPublisher)
 	 * @param mlIds
 	 * @return
 	 */
 	
 	
 	public List<Contacts> findAllByMlId(String mlIds) {
 		
 		List<Contacts> contactList = null;
 		
 		//String qry = "SELECT DISTINCT c FROM Contacts c join c.mlSet ml WHERE ml.list_id in ("+mlIds+")";
 		
 		String qry = " SELECT DISTINCT c FROM Contacts c, MailingList ml WHERE ml.listId IN ("+mlIds+") "+
 				" AND c.userId = ml.userId AND bitwise_and(c.mlBits, ml.mlBit)>0 ";

 		contactList = getHibernateTemplate().find(qry);
 		return contactList;
 		
 		
 	}
 	
 	/**
 	 * This method is added for Auto Responder and calls when we need to update the components_contacts table 
 	 * @return contactIds which are not there in 
 	 */
 	
 	public List<Long> getContactIdsFromTempContacts(Long programId, int startIndex, int size) {
 		
 		List<Long> contactIdsList = null;
 		
 		String qry = "SELECT cid FROM tempcontacts WHERE cid NOT IN(SELECT " +
 					 "contact_id FROM components_contacts WHERE program_id="+programId+") limit "+ startIndex+" , "+size;
 		
 		contactIdsList = jdbcTemplate.queryForList(qry, Long.class);
 		
 		return contactIdsList;
 		
 	}
 	
 	 /*
     *  Fetch contacts from all mailinglists for the given user.
     */
 	
 	/**
 	 * this method returns the contact ids in temp_contacts and called from the 
 	 * pmtamailmergesubmitter while processing the email sending activity component's processing  
 	 * @param qry
 	 * @param startIndex
 	 * @param size
 	 * @return
 	 */
 	public List<Long> getContactsForProgram(String qry, int startIndex, int size) { 
 		
 		List<Long> contactIdList = null;
 		
 		contactIdList = jdbcTemplate.queryForList(qry+" limit "+ startIndex+" , "+size,Long.class);
 		return contactIdList;
 	}
 	
 	
 	 //emailId,phone
 	public Contacts findContactByValues(String emaiId , String phoneNum,String externalID, Long userId) {
 		
 		try {
 			if(logger.isDebugEnabled()) logger.debug("email ID : " + emaiId + " phone no : "+ externalID + " listID : "+ userId);
			List<Contacts> contactIdList = null;
			
/*			String query = "FROM Contacts WHERE mailingList="+listId+" AND ( externalId ='"+externalID+"' OR " +
					" emailId = '"+emaiId+"' OR phone ="+phoneNum + ")";
*/			String query ="";

			if(externalID!=null && externalID.trim().length()>0) {
				query = "FROM Contacts WHERE users="+userId+" AND externalId ='"+externalID+"' ";
			}
			else if(emaiId!=null && emaiId.trim().length()>0) {
				query = "FROM Contacts WHERE users="+userId+" AND emailId ='"+emaiId+"' ";
			}
			else if(phoneNum!=null) {
				query = "FROM Contacts WHERE users="+userId+" AND mobile_phone ='"+phoneNum+"' ";
			}
			
			if(logger.isDebugEnabled()) logger.debug("query ::"+query);
			
			if(query.trim().equals("")){
				return null;
			}
			contactIdList  = getHibernateTemplate().find(query);
			
			
			
			//if(logger.isDebugEnabled()) logger.debug("query ::"+query);
			
			
			if(contactIdList.size() == 1){
				return contactIdList.get(0);
			}else{
				
				return null;
			}
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
 		
 	}

 	
	public Contacts findContactByPriority_Org(TreeMap<String, List<String>> prioMap , String[] lineStr,  long userId) {
 		
 		try {
 			String query = " FROM Contacts  WHERE users="+userId+" ";
 			
 			//if(logger.isDebugEnabled()) logger.debug("prioMap="+prioMap);
 			Set<String> keySet = prioMap.keySet();
 			List<Contacts> contactIdList = null;
 			
 			List<String> tempList=null;
 			
 			outer:for (String eachKey : keySet) {
 				
 				query = " FROM Contacts  WHERE users="+userId+" ";
				
 				if(logger.isDebugEnabled()) logger.debug("Key ="+eachKey);
 				tempList = prioMap.get(eachKey);
 				
 				for (String eachStr : tempList) {
					int index = Integer.parseInt(eachStr.substring(0,eachStr.indexOf('|')));
					
					
					//query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
					String[] tempStr = eachStr.split("\\|");
					String valueStr = lineStr[index].trim();
					
					//if(logger.isDebugEnabled()) logger.debug("valueStr=="+valueStr);
					
					if(valueStr==null || valueStr.trim().isEmpty()) {
						
						
						continue outer;
					}
					
					String posMappingDateFormatStr = tempStr[2].trim();
					
					if(posMappingDateFormatStr.toLowerCase().startsWith("date") && !tempStr[1].trim().toLowerCase().startsWith("udf")) {
						try {
							posMappingDateFormatStr =  posMappingDateFormatStr.substring(posMappingDateFormatStr.indexOf("(")+1, posMappingDateFormatStr.indexOf(")"));
							valueStr = format.format((new SimpleDateFormat(posMappingDateFormatStr).parse(valueStr)));
							query += " AND "+tempStr[1] + " = '"+ valueStr +"' ";
						} catch (ParseException e) {
							logger.error("Exception ::::" , e);
							return null;
						}
					}
					else if(posMappingDateFormatStr.toLowerCase().startsWith("string") || posMappingDateFormatStr.toLowerCase().startsWith("udf")) {
//						query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
						query += " AND "+tempStr[1] + " = '"+ valueStr +"' ";
					}else {
						query += " AND "+tempStr[1] + " = "+ lineStr[index] +" ";
					}
					
				} // for 
 				
 				//if(logger.isDebugEnabled()) logger.debug("QUERY=="+query);
 				
 				contactIdList  = getHibernateTemplate().find(query);
 				
 				if(contactIdList.size() == 1) {
 					return contactIdList.get(0);
 				}
 				else if(contactIdList.size() > 1) {
 					if(logger.isDebugEnabled()) logger.debug("more than 1 object found :"+contactIdList.size());
 					return contactIdList.get(0);
 				}
 				
 				
			} // outer for
 			
 			return null;
 			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
 		
 	}
 	
 	
	public Contacts findContactByPriority(TreeMap<String, List<String>> prioMap , String[] lineStr,  Users user) {

		try {
			String query = null;
			String tempQry=null;
			String notQry="";

			// 			if(logger.isDebugEnabled()) logger.debug("prioMap="+prioMap);
			Set<String> keySet = prioMap.keySet();
			List<Contacts> contactIdList = null;

			List<String> tempList=null;

			outer:for (String eachKey : keySet) {

				query = " FROM Contacts  WHERE users="+user.getUserId()+" ";
				tempQry =  notQry;

				//if(logger.isDebugEnabled()) logger.debug("Key ="+eachKey);
				tempList = prioMap.get(eachKey);
				int size = tempList.size();
				for (String eachStr : tempList) {
					int index = Integer.parseInt(eachStr.substring(0,eachStr.indexOf('|')));


					//query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
					String[] tempStr = eachStr.split("\\|");
					String valueStr = lineStr[index].trim();

					//if(logger.isDebugEnabled()) logger.debug("valueStr=="+valueStr);
					size = size-1;
					if(valueStr==null || valueStr.trim().isEmpty()) {
						if(size != 0)continue;

						continue outer;
					}

					String posMappingDateFormatStr = tempStr[2].trim();

					if(posMappingDateFormatStr.toLowerCase().startsWith("date") && !tempStr[1].trim().toLowerCase().startsWith("udf")) {
						try {
							posMappingDateFormatStr =  posMappingDateFormatStr.substring(posMappingDateFormatStr.indexOf("(")+1, posMappingDateFormatStr.indexOf(")"));
							valueStr = format.format((new SimpleDateFormat(posMappingDateFormatStr).parse(valueStr)));
							tempQry += " AND "+tempStr[1] + " = '"+ valueStr +"' ";
							if(tempStr[1] != null && tempStr[1].trim().length() >0 ){
								notQry +=  " AND "+tempStr[1] + " is null ";
							}

							else {
								notQry += "";
							}
						} catch (ParseException e) {
							logger.error("Exception ::::" , e);
							return null;
						}
					}
					else if(posMappingDateFormatStr.toLowerCase().startsWith("string") || tempStr[1].trim().toLowerCase().startsWith("udf")) {
						//						query += " AND "+ eachStr.substring(eachStr.indexOf('|')+1) + " = '"+ lineStr[index] +"' ";
						boolean isIndia = user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA);
						if( tempStr[1].equalsIgnoreCase("Mobile") || tempStr[1].equalsIgnoreCase("mobilePhone")) {
								//valueStr ="918143958243";
							if(valueStr.startsWith("1") && valueStr.trim().length() > 10){
								valueStr = valueStr.substring(1);
							}else if(valueStr.startsWith("91") && valueStr.trim().length() > 10){
								valueStr = valueStr.substring(valueStr.trim().length()-10);
							}else if(valueStr.startsWith("92") && valueStr.trim().length() > 10){
								valueStr = valueStr.substring(valueStr.trim().length()-10);
							}else if(!isIndia && valueStr.startsWith("971") && valueStr.trim().length() > 7 ){
								valueStr = valueStr.substring(valueStr.trim().length()-(valueStr.trim().length() - 3)); // for uae
							}
							logger.info("isIndia ====="+isIndia);
							valueStr = !isIndia ? "%"+valueStr : valueStr;
						}
						//tempQry += " AND "+tempStr[1] + " LIKE '"+ valueStr +"' ";
						/**
						 * To Optimize the performance of Query, Email &
						 * Customer Id Will be Searched based on '=' & Mobile
						 * Number to be Check based on 'LIKE'
						 */
						if(valueStr.startsWith("%")){
							tempQry += " AND "+tempStr[1] + " LIKE '"+ valueStr +"' ";
							//logger.info("Temp Query Mobile ......"+tempQry);
						}
						else{
							tempQry += " AND "+tempStr[1] + " = '"+ valueStr +"' ";
							//logger.info("Temp Query Cust_id and Email id ......"+tempQry);
						}
						if(tempStr[1] != null && tempStr[1].trim().length() >0 ){
							notQry +=  " AND "+tempStr[1] + " is null ";
						}
						else{
							notQry += "";
						}

					}else {
						tempQry += " AND "+tempStr[1] + " = "+ lineStr[index] +" ";
						if(tempStr[1] != null && tempStr[1].trim().length() >0 ){
							notQry +=  " AND "+tempStr[1] + " is null ";
						}
						else{
							notQry += "";
						}

					}

				} // for 

				query += tempQry;

				logger.debug("QUERY=="+query);

				contactIdList  = getHibernateTemplate().find(query);

				if(contactIdList.size() == 1) {
					return contactIdList.get(0);
				}
				else if(contactIdList.size() > 1) {
					// 					if(logger.isDebugEnabled()) logger.debug("more than 1 object found :"+contactIdList.size());
					return contactIdList.get(0);
				}


			} // outer for

			return null;

		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}

	}//findContactByPriority
 	
 	
 	
 	/****Purging ****/
	public Long getUnPurgedSize(MailingList ml) {
			
			String queryString = "SELECT COUNT(DISTINCT c.contactId) FROM Contacts c " +
					"WHERE c.users ="+ ml.getUsers().getUserId()+" AND bitwise_and(c.mlBits, "+ml.getMlBit()+")>0 AND c.purged = false";
			return (Long) (getHibernateTemplate().find(queryString)).get(0);
	}
	
	public List<Contacts> getUnpurgedContactsByListId(MailingList ml, Long contactId, int size) {
		try{
			String queryStr = "select distinct c FROM Contacts c" +
					" WHERE c.users = " + ml.getUsers().getUserId() +
					" AND bitwise_and(c.mlBits, " + ml.getMlBit() + ") > 0"+
					" AND c.purged=false AND c.contactId >"+contactId+" ORDER BY c.contactId, c.mlBits"; // HS: Added c.mlBits to nudge mysql to use the right index (USER_MLBIT)
			return executeQuery(queryStr, 0, size);
			
//			return getHibernateTemplate().find("(FROM Contacts c WHERE c.mailingList ="+ listId+" AND c.purged=false) LIMIT "+startIndex+","+size);
			
		}catch (DataAccessException e) {
			// TODO: handle exception
			if(logger.isErrorEnabled()) logger.error("** Exception : while getting unpurged contacts size by size", (Throwable)e);
			return null;
		}
	}
 	
 	public Contacts findMobileContactByUser(String mobilePhone, Users user){
 		/*if(mobilePhone.startsWith("1")) {
 			
 			mobilePhone = mobilePhone.substring(1);
 		}*/
 		if(mobilePhone.startsWith(""+user.getCountryCarrier())) {
 			
 			mobilePhone = mobilePhone.substring((""+user.getCountryCarrier()).length());
 		}
 		
 		String qry = "FROM Contacts WHERE users="+user.getUserId()+" AND mobilePhone like '%"+mobilePhone+"'";
 		
 		List<Contacts> contactsList = getHibernateTemplate().find(qry);
 		
 		if(contactsList != null && contactsList.size() > 0) {
 			
 			return contactsList.get(0);
 			
 		}//if
 		else{
 			
 			return null;
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
 	
 	
	
 	
 	// added for DR extraction
 	
	public List<Map<String, Object>> getConatctIds(String qry ) {
 		
 		try {
			
			
			return jdbcTemplate.queryForList(qry);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			return null;
		}
 	}
 	
 	
 	public List<Contacts> getContactsByCids(String cidStr) {
 		
 		try {
 			//logger.info("cidStr = "+cidStr);
			String qry = "FROM Contacts WHERE contactId IN("+cidStr+")";
			
			List<Contacts> contactsList = executeQuery(qry);
			
			if(contactsList != null && contactsList.size() > 0) {
				
				return contactsList;
			}
			else return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
 		
 	}
 	
 	
 	// ADDED for digital receipt extarction
 	
 	public static Map<String, String> contPojoFieldMap = new HashMap<String, String>();
	
	static{
		contPojoFieldMap.put("Email" , "emailId");
		contPojoFieldMap.put("First Name" , "firstName");
		contPojoFieldMap.put("Last Name" , "lastName");
		contPojoFieldMap.put("Street" , "addressOne");
		contPojoFieldMap.put("Address Two" , "addressTwo");
		contPojoFieldMap.put("City" , "city");
		contPojoFieldMap.put("State" , "state");
		contPojoFieldMap.put("Country" , "country");
		contPojoFieldMap.put("ZIP" , "zip");
		contPojoFieldMap.put("Mobile" , "mobilePhone");
		contPojoFieldMap.put("Customer ID" , "externalId" );
		contPojoFieldMap.put("Addressunit ID" , "hpId" );
		contPojoFieldMap.put("Gender" , "gender");
		contPojoFieldMap.put("Home Store" , "homeStore");
		contPojoFieldMap.put("BirthDay" , "birthDay");
		contPojoFieldMap.put("Anniversary" , "anniversary");
	}
 	
	public Contacts findContactByUniqPriority(TreeMap<String, List<String>> prioMap , Contacts contactObj, Users user) {
 		
 		try {
 			String query = null;
 			String tempQry=null;
 			String notQry="";
 			
 			
 			//logger.info("prioMap="+prioMap);
 			Set<String> keySet = prioMap.keySet();
 			List<Contacts> contactIdList = null;
 			
 			List<String> tempList=null;
 			
 			
 			outer:for (String eachKey : keySet) {
 				query = " FROM Contacts  WHERE users="+user.getUserId()+" ";
 				
 				tempQry =  notQry;
				
 				//logger.info("Key ="+eachKey);
 				tempList = prioMap.get(eachKey);
 				int size = tempList.size();
 				for (String eachStr : tempList) {
 					//logger.info(">>> eachStr >>"+eachStr);
 					String[] tempStr = eachStr.split("\\|");
 					
// 					logger.info("tempStr[0]"+tempStr[0]+" :::tempStr[1]"+tempStr[1]);
 					
 					String valueStr = getContactFiled(contactObj,tempStr[0]);
 					
// 					logger.info(">>>tempStr[0]>>>"+tempStr[0]+ "Conatct Value is ::"+valueStr);
 					size = size-1;
 					if(valueStr == null || valueStr.trim().length() == 0){
 						if(size != 0)continue;
 						continue outer;
 					}
 						
 					valueStr = valueStr.trim();
 					
 					if(tempStr[0].startsWith("UDF")){
 						
 						tempQry += " AND "+tempStr[0] + " = '"+ valueStr +"' ";
 						
 						if(tempStr[0] != null && tempStr[0].trim().length() >0 ){
 							notQry +=  " AND "+tempStr[0] + " is null ";
 						}
 						else {
							notQry += "";
						}
 					}else {
 						
 						if(tempStr[1].toLowerCase().equals("string")) {
 							boolean isIndia = user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA);
 							if( tempStr[0].equalsIgnoreCase("Mobile") || tempStr[0].equalsIgnoreCase("mobilePhone" )) {
 								if(valueStr.startsWith("1") && valueStr.trim().length() > 10){
 									valueStr = valueStr.substring(1);
 								}else if(valueStr.startsWith("91") && valueStr.trim().length() > 10){
 									valueStr = valueStr.substring(valueStr.trim().length()-10);
 								}else if(valueStr.startsWith("92") && valueStr.trim().length() > 10){
 									valueStr = valueStr.substring(valueStr.trim().length()-10);
 								}else if(!isIndia && valueStr.startsWith("971") && valueStr.trim().length() > 7 ){
 									valueStr = valueStr.substring(valueStr.trim().length()-(valueStr.trim().length() - 3)); // for uae
 								}
 								logger.info("isIndia======="+isIndia);
 								valueStr = !isIndia ? "%"+valueStr : valueStr;
 							}
 							
 							tempQry += " AND  "+contPojoFieldMap.get(tempStr[0])+ (!isIndia ? " LIKE " : "=")+"'"+valueStr+"'";
 							//logger.info("tempQry here ::"+tempQry);
// 							notQry +=  " AND "+contPojoFieldMap.get(tempStr[0]) + " is null ";
 							
 						}else {
 							tempQry += " AND  "+contPojoFieldMap.get(tempStr[0])+"="+valueStr+"";
 							
 						}
 						if(tempStr[0] != null && tempStr[0].trim().length() >0 ){
 							notQry +=  " AND "+contPojoFieldMap.get(tempStr[0]) + " is null ";
 						}
 						else {
							notQry += "";
						}
 						
 					}
 					
 				
 					
				}
 				query += tempQry;
 				
 				logger.info("QUERY=="+query);
 				
 				contactIdList  = getHibernateTemplate().find(query);
 				//logger.info("contactIdList size is ::"+contactIdList.size());
 				if(contactIdList.size() == 1) {
 					return contactIdList.get(0);
 				}
 				else if(contactIdList.size() > 1) {
 					logger.info("more than 1 object found :"+contactIdList.size());
 					return contactIdList.get(0);
 				}
 				
 				
 				
			} // outer for
 			
 			return null;
 			
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
			return null;
		}
 		
 	} // findContactByPriority

 	private String getContactFiled(Contacts contObj,String contFiledStr) {
		
		
		if(contFiledStr.equals("Email")) return contObj.getEmailId();
		else if(contFiledStr.equals("First Name")) return contObj.getFirstName();
		else if(contFiledStr.equals("Last Name")) return contObj.getLastName();
		else if(contFiledStr.equals("Street")) return contObj.getAddressOne();
		else if(contFiledStr.equals("Address Two")) return contObj.getAddressTwo();
		else if(contFiledStr.equals("City")) return contObj.getCity();
		else if(contFiledStr.equals("State")) return contObj.getState();
		else if(contFiledStr.equals("Country")) return contObj.getCountry();
		else if(contFiledStr.equals("ZIP")) return contObj.getZip();
		else if(contFiledStr.equals("Mobile")) return contObj.getMobilePhone();
		else if(contFiledStr.equals("Customer ID")) return contObj.getExternalId();
//		else if(contFiledStr.equals("Addressunit ID")) return contObj.getHpId();
		else if(contFiledStr.equals("Gender")) return contObj.getGender();
		else if(contFiledStr.equals("BirthDay")) return contObj.getBirthDay() == null ?"":MyCalendar.calendarToString(contObj.getBirthDay(), MyCalendar.FORMAT_DATETIME_STYEAR);
		else if(contFiledStr.equals("Anniversary")) return contObj.getAnniversary() == null ?"":MyCalendar.calendarToString(contObj.getAnniversary(), MyCalendar.FORMAT_DATETIME_STYEAR);
		else if(contFiledStr.equals("UDF1")) return contObj.getUdf1();
		else if(contFiledStr.equals("UDF2")) return contObj.getUdf2();
		else if(contFiledStr.equals("UDF3")) return contObj.getUdf3();
		else if(contFiledStr.equals("UDF4")) return contObj.getUdf4();
		else if(contFiledStr.equals("UDF5")) return contObj.getUdf5();
		else if(contFiledStr.equals("UDF6")) return contObj.getUdf6();
		else if(contFiledStr.equals("UDF7")) return contObj.getUdf7();
		else if(contFiledStr.equals("UDF8")) return contObj.getUdf8();
		else if(contFiledStr.equals("UDF9")) return contObj.getUdf9();
		else if(contFiledStr.equals("UDF10")) return contObj.getUdf10();
		else if(contFiledStr.equals("UDF11")) return contObj.getUdf11();
		else if(contFiledStr.equals("UDF12")) return contObj.getUdf12();
		else if(contFiledStr.equals("UDF13")) return contObj.getUdf13();
		else if(contFiledStr.equals("UDF14")) return contObj.getUdf14();
		else if(contFiledStr.equals("UDF15")) return contObj.getUdf15();
		
		
		else return null;		
		
		
		
	}
	
 	public List<Contacts> findContactBycidStr(String cIdStr) {
		
		String query = " FROM Contacts WHERE contactId in ("+cIdStr+")";
		List<Contacts> cList = executeQuery(query);
		return cList;
	}
 	
 	public List<Contacts> findByQuery(String qry){
		/*if(mobilePhone.startsWith("1")) {
			
			mobilePhone = mobilePhone.substring(1);
		}*/
		
		
		//String qry = "FROM Contacts WHERE users="+user.getUserId()+" AND mobilePhone like '%"+mobilePhone+"'";
		
		List<Contacts> contactsList = getHibernateTemplate().find(qry);
		
		if(contactsList != null && contactsList.size() > 0) {
			
			return contactsList;
			
		}//if
		else{
			
			return null;
		}
		
		
		
	}
 	
 	public int getAvailableContactsFromNotificationTempContacts(String mlIds, String subquery) {
		try {
			String countQry = "SELECT COUNT(st.cid) FROM notification_tempcontacts st, mailing_lists cm " +
				" WHERE cm.list_id  IN ("+ mlIds + ") AND st.user_id = cm.user_id "+
				" AND (st.mlbits & cm.mlbit )>0 AND st.instance_id IS NOT NULL AND instance_id !='' AND st.push_notification = 1 "
				+ " AND device_Type IS NOT NULL AND device_Type !='' "+subquery;
			return jdbcTemplate.queryForInt(countQry);
		} catch (DataAccessException e) {
			logger.error(" Exception : ",(Throwable)e);
			return 0;
		}
		
	}
 	
}
