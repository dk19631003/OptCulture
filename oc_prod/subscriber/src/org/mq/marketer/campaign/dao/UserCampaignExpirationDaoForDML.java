package org.mq.marketer.campaign.dao;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UserCampaignExpiration;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.utils.OCConstants;

public class UserCampaignExpirationDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public UserCampaignExpirationDaoForDML() {}
    
   /* public UserCampaignExpiration find(Long id) {
        return (UserCampaignExpiration) super.find(UserCampaignExpiration.class, id);
    }*/

    public void saveOrUpdate(UserCampaignExpiration userCampaignExpiration) {
        super.saveOrUpdate(userCampaignExpiration);
    }

    public void delete(UserCampaignExpiration userCampaignExpiration) {
        super.delete(userCampaignExpiration);
    }
/*
	public List findAll() {
        return super.findAll(UserCampaignExpiration.class);
    }
	
	 
	
	public List<UserCampaignExpiration> findBy(Long userId, Long campaignId) {
		
		String qry = " FROM UserCampaignExpiration WHERE "
				+ " userId="+userId.longValue()+" AND campaignId="+
				campaignId.longValue();//+" AND status = '"+OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_ACTIVE+"'";
		
		List<UserCampaignExpiration> retList = executeQuery(qry);
		
		return retList;
		
	}*/
	
	public int deleteBy(String campIds) throws BaseDAOException{
		
		Calendar now = Calendar.getInstance();
		String nowStr = MyCalendar.calendarToString(now, MyCalendar.FORMAT_DATETIME_STYEAR);
		
		String qry = "UPDATE UserCampaignExpiration set status='"+
		OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_CAMPAIGN_DELEATED+"', modifiedDate='"+nowStr+"' WHERE campaignId IN("+campIds+")";
		
		int retVal = executeUpdate(qry);
		
		return retVal;
	}
	
}
