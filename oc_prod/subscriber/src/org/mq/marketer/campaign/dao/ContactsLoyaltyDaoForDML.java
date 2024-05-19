package org.mq.marketer.campaign.dao;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgramTrans;
import org.mq.marketer.campaign.beans.SparkBaseCard;
import org.mq.marketer.campaign.controller.contacts.ContactListUploader;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.optculture.model.ocloyalty.MatchedCustomerReport;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ContactsLoyaltyDaoForDML extends AbstractSpringDaoForDML implements Serializable{

    public ContactsLoyaltyDaoForDML() {}

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    /*public ContactsLoyalty find(Long id) {
        return (ContactsLoyalty) super.find(ContactsLoyalty.class, id);
    }*/

    public void saveOrUpdate(ContactsLoyalty contactsLoyalty) {
        super.saveOrUpdate(contactsLoyalty);
    }
    
	public void updateAllChildMembership(Long sourceLoyaltyId, Long destLoyaltyId, Long userId, Long programId, String transferedON) throws Exception {
		
		String qry = "UPDATE ContactsLoyalty SET transferedTo="+destLoyaltyId+", transferedOn='"+transferedON+"'"
				+ " WHERE  userId = "+userId+" AND programId="+programId+" AND transferedTo="+sourceLoyaltyId;
		executeUpdate(qry);
		
	}
	
public long updateGiftCustomersToExpire(Long userId, Long programId, String currentDateStr) {
		
		
		try {
			String query = "UPDATE contacts_loyalty SET membership_status='" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED+"'"+
					" WHERE user_id="+userId+" AND program_id = " + programId.longValue() +
					" AND reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G + "'"	+
					" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " +
					" AND CONCAT(YEAR(created_date),'-', MONTH(created_date)) <= '"+currentDateStr+"' ";
			logger.info("query ==="+query);	
			return executeJdbcUpdateQuery(query);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			return 0;
		}catch(Exception e){
			return 0;
		}
		
		
	}

public long updateLtyCustomersToExpireByCreatedDate(Long userId, Long programID, Long tierId, String currentDateStr, char resetFlag) {
	try{
		
		String appQry = "";
		if(resetFlag == OCConstants.FLAG_YES){
			appQry = " AND tier_upgraded_date IS NULL " ;
		}
		
		String query = "UPDATE contacts_loyalty SET membership_status='" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED+"'"+
				" WHERE user_id="+userId+" AND program_id="+programID.longValue()+" AND program_tier_id = " + tierId.longValue() +
				" AND (reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L + "' OR reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+ "') " +
				" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " + appQry +
				" AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)), '%Y-%m') <= STR_TO_DATE('"+currentDateStr+"', '%Y-%m') ";
		
		logger.info("Exp Date query ::"+query);
		
		return executeJdbcUpdateQuery(query);
	} catch (DataAccessException e) {
		return 0;
	}catch(Exception e){
		return 0;
	}
}
public long updateLtyCustomersToExpireByUpgradedDate(Long userId,Long programID, Long tierId, String currentDateStr) {
	
	try {
		String query = "UPDATE contacts_loyalty SET membership_status='" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED+"'"+
				" WHERE user_id="+userId+" AND program_id="+programID+" AND program_tier_id = " + tierId.longValue() +
				" AND (reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L + "' OR reward_flag = '" + OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL+ "') " +
				" AND membership_status = '" +OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE+ "' " +
				" AND if(tier_upgraded_date IS NOT NULL , STR_TO_DATE(CONCAT(YEAR(tier_upgraded_date),'-', MONTH(tier_upgraded_date)), '%Y-%m') "
						+ " <= STR_TO_DATE('"+currentDateStr+"', '%Y-%m'),STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)), '%Y-%m') "
								+ "<= STR_TO_DATE('"+currentDateStr+"', '%Y-%m') )" ;
				logger.info("query ==="+query);		
				return executeJdbcUpdateQuery(query);
	} catch (DataAccessException e) {
		return 0;
	}catch(Exception e){
		return 0;
	}
}
	public void updateTier(Long loyaltyId, Long tierID) throws Exception{
		
		String qry = "UPDATE ContactsLoyalty SET programTierId="+tierID +" WHERE loyaltyId="+loyaltyId;
		
		executeUpdate(qry);


	}

	public void updateOptInDateAndModeForBulkEnrollment(Long userId, Long loyaltyId, Calendar cal) throws Exception{
		
		try{
			String qry = "UPDATE ContactsLoyalty SET optinDate = '"+ cal +"', createdDate = '"+ cal +"', mode = '" +OCConstants.LOYALTY_MODE_MANUAL_BULK_ENROLLMENT+ "' WHERE userId = "+ userId +" AND loyaltyId = "+loyaltyId;
			int updatedContact = executeUpdate(qry);
			logger.debug("Updated count are :"+ updatedContact);
		}catch(Exception e){
			logger.info("Exception while updating createdDate ",e);
		}

	}
	
	public void updateMembershipPasswordByLoyaltyId(Long loyaltyId,String password) {
		try {
			String qry="UPDATE ContactsLoyalty SET membershipPwd ='"+password+"' where loyaltyId= '"+loyaltyId+"'";
			int updatedContact = executeUpdate(qry);
			logger.debug("Updated count are :"+ updatedContact);
		}catch(Exception e){
			logger.info("Exception while updating Password ",e);
		}
	}
	public void updateMembershipFingerPrintByLoyaltyId(Long loyaltyId,Boolean fpRecognitionFlag) {
		try {
			String qry="UPDATE ContactsLoyalty SET fpRecognitionFlag ="+fpRecognitionFlag+" where loyaltyId= '"+loyaltyId+"'";
			int updatedContact = executeUpdate(qry);
			logger.debug("Updated count are :"+ updatedContact);
		}catch(Exception e){
			logger.info("Exception while updating Password ",e);
		}
	}
	

}//EOF
