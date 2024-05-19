package org.mq.optculture.business.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.ServiceLocator;

public class PintoRanch_UnsubscribeHelper {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public Users setFlagValue()
	{

		try {

			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName("usersDao");
			Users user = usersDao.getUserByGenericUnsubUrl("PintoRanch_Unsubscribe.jsp");

			return user;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);	
			return null;
		}
	}

}
