package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Set;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.SMSCampaigns;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class SMSCampaignsDao extends AbstractSpringDao {
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	public SMSCampaignsDao(){}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	/*public void saveOrUpdate(SMSCampaigns smsCampaigns) {
        super.saveOrUpdate(smsCampaigns);
    }*/

    /*public void delete(SMSCampaigns smsCampaigns) {
        super.delete(smsCampaigns);
    }*/

    public List findAll() {
        return super.findAll(SMSCampaigns.class);
    }
	
    /**
     * this methos checks whether the given smsCampaign name is already exists or not.
     * @param smsCampName specifies the given smscampaign name.
     * @param userId specifies the logged in user id.
     * @return a boolean variable true if name already exists false if not.
     */
    public boolean checkName(String smsCampName, Long userId){
    	try{
    		 List list = getHibernateTemplate().find("From SMSCampaigns where smsCampaignName = '" + smsCampName + "' and users= " + userId);
    		
			if(list.size()>0){
				return true;
			}else{
				return false;
			}
    	}catch (Exception e) {
			// TODO: handle exception
    		logger.error("execption while verifying the sms campaign name",e);
    		return false;
		}
    }
    
   /* public List<SMSCampaigns> getSmsCampaignsByStatus(Long userId, String status){
		String query=null;
		String qry=null;
		List<SMSCampaigns> SmsCampList=null;
		try{
		if(status.equals("All")){
			logger.debug("just entered to fetch all the sms campaigns");
			query="from SMSCampaigns where users=" + userId+" order by createdDate desc";
		  }else {	
//			  "SELECT  new MailingList(m.listId,m.listName," +
//				"(select count(c.mailingList) from Contacts c where m.listId=c.mailingList)," +
//				"(SELECT COUNT(purged) FROM Contacts c1 WHERE m.listId=c1.mailingList and (purged=false))"+
//				",m.description,m.custField, m.lastModifiedDate,m.users) FROM MailingList m  " +
//				"where m.users=" + userId + " order by m.createdDate";
			//qry="select new Campaigns(c.campaignName,(select m.listName from MailingList m where m.listId in()))"
			query="from SMSCampaigns where users=" + userId+ " and status like '" + status + "' order by createdDate desc" ;
		  }
		SmsCampList=executeQuery(query);
		
		
	    }catch(Exception e) {
	    	  logger.error("exception while retrieving campaign list",(Throwable)e);
	    	  return null;
	      }
	
	return SmsCampList;
	
	}*/
    
    /**
     * Updates the  SMS Campaign's status to either 'Sent' or 'Running' based on 
	 * the no of active schedules for this SMS  campaign in sms_campaign_schedule.<BR><BR> 
	 * If for this smsCampaignId in sms_campaign_schedule table the number of 
	 * rows exists with status as '0' that means active schedules then the 
	 * SMS campaign will be updated as 'Running' SMS campaign if no rows exists with
	 * the status as '0' then the campaign will be updated as 'Sent'
	 * 
     * @param smsCampaignId -Id(Long) of the campaign. 
     */
    
    /*public void updateSmsCampaignStatus(Long smsCampaignId) {
    	
    	String qryStr = 
    		" UPDATE SMS_campaigns c SET c.status = ( SELECT IF(count(cs.status)>0,'Running','Sent')" +
    		" FROM SMS_campaign_schedule cs WHERE cs.sms_campaign_id="+smsCampaignId+" AND cs.status=0)" +
    		" WHERE c.sms_campaign_id="+smsCampaignId ;
    	int result = executeJdbcUpdateQuery(qryStr);
    	
    	if(result <= 0) {
    		if(logger.isWarnEnabled()) logger.warn("SMS Campaign status could not be updated for the sms campaign id :"+smsCampaignId);
    	}
    	
    }*/
    
    public SMSCampaigns findByCampaignId(Long smsCampaignId) {
    	
    	List<SMSCampaigns> campList =  getHibernateTemplate().find(
    			" FROM SMSCampaigns WHERE smsCampaignId = "+smsCampaignId);
    	
    	if(campList == null || campList.size() == 0) {
    		return null;
    	}
    	else {
    		return campList.get(0);
    	}
    }
    
   
 	
 	/**
 	 * added for EventTrigger sms changes
 	 */
 	/*public void deleteSmsTempContacts() {
 		try {
			jdbcTemplate.update("DELETE FROM sms_tempcontacts");
		} catch (DataAccessException e) {
			logger.error(" ** Exception : while deleting the contacts from sms_tempcontacts table", e);
		}
 	}*/
 	
	/**
	 * added for EventTrigger sms changes
	 * 
	 * @param qry
	 * @param startIndex
	 * @param size
	 * @return
	 * 
	 * Note: as we are fetching from sms_tempcontacts there wont be any complete MailingList object
	 * so we will just set the list id and create a dummy mailingList obj
	 */
public List<Contacts> getSegmentedSmsTempContacts(String qry, int startIndex, int size) {
		
		List<Contacts> list = null;

		try {
			
			list = jdbcTemplate.query(qry+" LIMIT "+startIndex+", "+size, new RowMapper() {
		
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		    		
		        	MailingList tempMailingList = new MailingList();
		    		tempMailingList.setListId(rs.getLong("list_id"));
		    		
		            Contacts contact = new Contacts();
		            contact.setContactId(rs.getLong("cid"));
		            
		            //Set<MailingList> mlset  = contact.getMlSet();
		            //mlset.add(tempMailingList);
		            //contact.setMlSet(mlset);
		            contact.setMlBits(rs.getLong("mlbits"));
		            //contact.setMailingList(tempMailingList);
		            contact.setEmailId(rs.getString("email_id"));
		            contact.setFirstName(rs.getString("first_name"));
		            contact.setLastName(rs.getString("last_name"));
		            //contact.setCreatedDate(rs.getDate("created_date"));
		            contact.setPurged(rs.getBoolean("purged"));
		            contact.setOptinStatus(rs.getBoolean("optin_status"));
		            contact.setEmailStatus(rs.getString("email_status"));
		            
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
		            contact.setAddressOne(rs.getString("address_one"));
		            contact.setAddressTwo(rs.getString("address_two"));
		            contact.setCity(rs.getString("city"));
		            contact.setState(rs.getString("state"));
		            contact.setCountry(rs.getString("country"));
		           /* contact.setPin(rs.getInt("pin"));
		            contact.setPhone(rs.getLong("phone"));*/
		            contact.setZip(rs.getString("zip"));
		            contact.setMobilePhone(rs.getString("mobile_phone"));
		            contact.setOptin(rs.getByte("optin"));
		            contact.setSubscriptionType(rs.getString("subscription_type"));
	
		            return contact;
		        }
		    });
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		return list;
	} // getSegmentedSmsTempContacts


    
}
