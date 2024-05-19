package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.util.List;

import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyMemberSessionID;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyMemberSessionIDDaoForDML extends AbstractSpringDaoForDML implements Serializable{

    public LoyaltyMemberSessionIDDaoForDML() {}

	

    /*public ContactsLoyalty find(Long id) {
        return (ContactsLoyalty) super.find(ContactsLoyalty.class, id);
    }*/
    public void deleteBy(LoyaltyMemberSessionID LoyaltyMemberSessionID) throws Exception{
    	
		String query = "DELETE FROM LoyaltyMemberSessionID WHERE orgID="+LoyaltyMemberSessionID.getOrgID()+" "
				+ " AND userId ="+LoyaltyMemberSessionID.getUserId()+" AND deviceID='"+LoyaltyMemberSessionID.getDeviceID()+
				"' AND cardNumber='"+LoyaltyMemberSessionID.getCardNumber()+"'";
		
		executeUpdate(query);
		
    }
    public int deleteBy(Long userID, Long orgID, String cardNumber, String deviceID, String sessionID){
    	
    	try {
			String qry = "DELETE FROM LoyaltyMemberSessionID WHERE userId= "+userID+" AND orgID="+orgID+
					" AND cardNumber='"+cardNumber+"' AND deviceID='"+deviceID+"' AND sessionID='"+sessionID+"'";
			
			logger.debug("qry ==="+qry);
			
			return executeUpdate(qry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :", e);
		}
    	return 0;
    }
    
    public void saveOrUpdate(LoyaltyMemberSessionID LoyaltyMemberSessionID) {
    	
        super.saveOrUpdate(LoyaltyMemberSessionID);
    }

}
