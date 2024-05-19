package org.mq.optculture.data.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyMemberSessionID;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyMemberSessionIDDao extends AbstractSpringDao implements Serializable{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public LoyaltyMemberSessionIDDao() {}


    public LoyaltyMemberSessionID find(Long id) {
        return (LoyaltyMemberSessionID) super.find(LoyaltyMemberSessionID.class, id);
    }
    
    public LoyaltyMemberSessionID findBy(Long userID, Long orgID, String cardNumber, String deviceID){
    	
    	try {
			String qry = "FROM LoyaltyMemberSessionID WHERE userId= "+userID+" AND orgID="+orgID+
					" AND cardNumber='"+cardNumber+"' AND deviceID='"+deviceID+"'";
			
			logger.debug("qry ==="+qry);
			List<LoyaltyMemberSessionID> retList= executeQuery(qry);
			if(retList != null && !retList.isEmpty()) return retList.get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception :", e);
		}
    	
    	return null;
    	
    	
    }

}
