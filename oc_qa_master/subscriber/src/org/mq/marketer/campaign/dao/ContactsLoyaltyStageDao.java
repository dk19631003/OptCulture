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

public class ContactsLoyaltyStageDao extends AbstractSpringDao implements Serializable{

    public ContactsLoyaltyStageDao() {}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    public ContactsLoyaltyStage find(Long id) {
        return (ContactsLoyaltyStage) super.find(ContactsLoyaltyStage.class, id);
    }

   /* public void saveOrUpdate(ContactsLoyaltyStage contactsLoyaltyStage) {
        super.saveOrUpdate(contactsLoyaltyStage);
    }
    
    public void delete(ContactsLoyaltyStage contactsLoyaltyStage) {
		super.delete(contactsLoyaltyStage);
	}*/
    	
	public ContactsLoyaltyStage findRequest(String custId, String email, 
			String phone, String card, String user, String serviceType, String requestType){
		
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
	
	
	public ContactsLoyaltyStage findTransferRequest(String sourceCard, String destCard, String user, String serviceType, String requestType){
		
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
	public List<ContactsLoyaltyStage> findByPhoneAndUser(String mobile,String user) {
		
		String query = " FROM ContactsLoyaltyStage WHERE userName = '"+user+"' AND phoneNumber='"+mobile+"' ";
		
		List<ContactsLoyaltyStage> tempList = getHibernateTemplate().find(query);
		
		if(tempList != null && tempList.size() >0) {
			
			 return tempList;
		} 
		else 
			 return null;
		
	}
}
