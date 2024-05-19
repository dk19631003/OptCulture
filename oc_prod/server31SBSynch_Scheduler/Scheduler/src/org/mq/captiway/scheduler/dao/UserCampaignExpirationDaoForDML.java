package org.mq.captiway.scheduler.dao;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.UserCampaignExpiration;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.utils.OCConstants;

public class UserCampaignExpirationDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
    public UserCampaignExpirationDaoForDML() {}
    
/*    public UserCampaignExpiration find(Long id) {
        return (UserCampaignExpiration) super.find(UserCampaignExpiration.class, id);
    }*/

    public void saveOrUpdate(UserCampaignExpiration campaignSchedule) {
        super.saveOrUpdate(campaignSchedule);
    }

    public void delete(UserCampaignExpiration campaignSchedule) {
        super.delete(campaignSchedule);
    }

/*	public List findAll() {
        return super.findAll(UserCampaignExpiration.class);
    }
	*/
	public void saveByCollection(Collection collection){
    	getHibernateTemplate().saveOrUpdateAll(collection);
    }
	
	/*public List<Long> findUsers() throws BaseDAOException{
		
		String qry = "SELECT userId FROM UserCampaignExpiration WHERE status='A' AND userId IS NOT NULL GROUP BY userId order by userId";
		
		List<Long> userIDs = executeQuery(qry);
		
		return userIDs;
	}
	*/
	
	/*public List<UserCampaignExpiration> findBy(Long userId) {
		
		String qry = "FROM UserCampaignExpiration WHERE status='"+
					OCConstants.USER_ALERT_CAMPAIGN_EXPIRED_STATUS_ACTIVE+"' "
					+ "AND userId IS NOT NULL AND userId="+userId.longValue();
		
		List<UserCampaignExpiration> expiringCampaigns = executeQuery(qry);
		
		return expiringCampaigns;
		
	}*/
	
}
