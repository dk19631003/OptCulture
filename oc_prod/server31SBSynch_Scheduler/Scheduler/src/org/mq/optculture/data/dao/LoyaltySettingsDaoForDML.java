package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.LoyaltySettings;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.dao.AbstractSpringDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;


public class LoyaltySettingsDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	
	public LoyaltySettingsDaoForDML() {
	}
	
	public void saveOrUpdate(LoyaltySettings loyaltySettings) {
		super.saveOrUpdate(loyaltySettings);
	}

	/*public LoyaltySettings findByOrgId(Long orgId) {
		logger.debug(">>>>>>>>>>>>> entered in findByOrgId");
		LoyaltySettings loyaltySettings = null;

		String query = "FROM LoyaltySettings WHERE userOrgId ="+orgId;

		List<LoyaltySettings> list = getHibernateTemplate().find(query);

		if(list != null && list.size()>0){
			loyaltySettings = list.get(0);
		}
		logger.debug("<<<<<<<<<<<<< completed findByOrgId ");
		return loyaltySettings;
	}//findByUserId
*/}
