package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.general.Constants;

public class LoyaltyThresholdAlertsDao extends AbstractSpringDao {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public LoyaltyThresholdAlertsDao() {
	}
	
	/*public void saveOrUpdate(LoyaltyThresholdAlerts loyaltyThresholdAlerts) {
		super.saveOrUpdate(loyaltyThresholdAlerts);
	}*/

	public LoyaltyThresholdAlerts findByUserId(Long userId) {
		logger.debug(">>>>>>>>>>>>> entered in findByUserId");
		LoyaltyThresholdAlerts loyaltyThresholdAlerts = null;

		String query = "FROM LoyaltyThresholdAlerts WHERE userId = " + userId;

		List<LoyaltyThresholdAlerts> list = getHibernateTemplate().find(query);

		if(list != null && list.size() > 0){
			loyaltyThresholdAlerts = list.get(0);
		}
		logger.debug("<<<<<<<<<<<<< completed findByUserId ");
		return loyaltyThresholdAlerts;
	}
}
