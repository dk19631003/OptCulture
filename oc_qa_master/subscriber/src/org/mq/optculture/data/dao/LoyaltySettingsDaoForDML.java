package org.mq.optculture.data.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.marketer.campaign.general.Constants;

public class LoyaltySettingsDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

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
		}else{
			return null;
		}
		logger.debug("<<<<<<<<<<<<< completed findByOrgId ");
		return loyaltySettings;
	}

	public List<LoyaltySettings> findByUrl(String url) {
		logger.debug(">>>>>>>>>>>>> entered in findByUrl");
		List<LoyaltySettings> list = null;

		String query = "FROM LoyaltySettings WHERE urlStr ='"+url+"'";

		list = getHibernateTemplate().find(query);
		return list;
	}*/
}
