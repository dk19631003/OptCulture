package org.mq.optculture.business.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.FarwardToFriendDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;

public class FarwardHelper {


	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public Contacts setFlagValue(Long cid)
	{

		try {

			/*ServletContext servletContext =this.getServletContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			ContactsDao contactsDao = (ContactsDao)wac.getBean("contactsDao");
			FarwardToFriendDao farwardToFriendDao = (FarwardToFriendDao)wac.getBean("farwardToFriendDao");*/

			ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName("contactsDao");
			FarwardToFriendDao farwardToFriendDao = (FarwardToFriendDao) ServiceLocator.getInstance().getDAOByName("farwardToFriendDao");
			
			//CampaignSentDao campaignSentDao = (CampaignSentDao)wac.getBean("campaignSentDao");
			//Contacts contacts = contactsDao.findById(sentId);
			Contacts contact = contactsDao.findById(cid);

			return contact;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);	
			return null;
		}
	}

}
