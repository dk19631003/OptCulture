package org.mq.marketer.campaign.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.ContactsLoyaltyStage;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ContactsLoyaltyStageDaoForDML extends AbstractSpringDaoForDML implements Serializable{

    public ContactsLoyaltyStageDaoForDML() {}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public int executeUpdateQuery(String qryStr) {
		return jdbcTemplate.update(qryStr);
	}

    /*public ContactsLoyaltyStage find(Long id) {
        return (ContactsLoyaltyStage) super.find(ContactsLoyaltyStage.class, id);
    }*/

    public void saveOrUpdate(ContactsLoyaltyStage contactsLoyaltyStage) {
        super.saveOrUpdate(contactsLoyaltyStage);
    }
    
    public void delete(ContactsLoyaltyStage contactsLoyaltyStage) {
    	String qry = "delete from contacts_loyalty_stage where loyalty_stage_id="+contactsLoyaltyStage.getLoyaltyStageId().longValue();
    	try {
    		logger.info("deleteing from staging table");
  		   executeUpdateQuery(qry);
  	   } catch(Exception e) {
  		   logger.error("Exception : Error occured while deleting from staging",e);
  	   }
    
    }
    	
	/*public ContactsLoyaltyStage findRequest(String custId, String email, 
			String phone, String card, String user, String requestType, String serviceType){
		
		String query = " FROM ContactsLoyaltyStage WHERE customerId = '"+custId +"' AND emailId = '"+email+"' AND phoneNumber = '"+phone
				+"' AND userName = '"+user+"' AND status ="
				+ " '"+Constants.LOYALTY_STAGE_PENDING+"' AND serviceType = '"+serviceType+"' AND  reqType='"+requestType+"'";
		
		List<ContactsLoyaltyStage> tempList = getHibernateTemplate().find(query);
		
		if(tempList != null && tempList.size() >0) {
			
			 return tempList.get(0);
		} 
		else 
			 return null;
	}
	
	
	public ContactsLoyaltyStage findTransferRequest(String sourceCard, String destCard, String user, String requestType, String serviceType){
		
		String query = " FROM ContactsLoyaltyStage WHERE cardNumber='source:"+sourceCard+"-dest:"+destCard+"' AND userName = '"+user+"' AND status ="
				+ " '"+Constants.LOYALTY_STAGE_PENDING+"' AND serviceType = '"+serviceType+"' AND  reqType='"+requestType+"'";
		
		List<ContactsLoyaltyStage> tempList = getHibernateTemplate().find(query);
		
		if(tempList != null && tempList.size() >0) {
			
			 return tempList.get(0);
		} 
		else 
			 return null;
	}
	
	
	public ContactsLoyaltyStage findByTid(Long tid){
		
		String query = " FROM ContactsLoyaltyStage WHERE trxId="+tid.longValue();
		
		List<ContactsLoyaltyStage> tempList = getHibernateTemplate().find(query);
		
		if(tempList != null && tempList.size() >0) {
			
			 return tempList.get(0);
		} 
		else 
			 return null;
	}
	
	
	public List<ContactsLoyaltyStage> findByPhone(String mobile) {
		
		String query = " FROM ContactsLoyaltyStage WHERE phoneNumber='"+mobile+"'";
		
		List<ContactsLoyaltyStage> tempList = getHibernateTemplate().find(query);
		
		if(tempList != null && tempList.size() >0) {
			
			 return tempList;
		} 
		else 
			 return null;
		
	}
*/}
