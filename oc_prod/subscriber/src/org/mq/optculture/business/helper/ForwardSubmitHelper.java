package org.mq.optculture.business.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.FarwardToFriend;
import org.mq.marketer.campaign.dao.FarwardToFriendDao;
import org.mq.marketer.campaign.dao.FarwardToFriendDaoForDML;
import org.mq.marketer.campaign.dao.ResetPasswordTokenDao;
import org.mq.marketer.campaign.dao.ResetPasswordTokenDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;

public class ForwardSubmitHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public void setFlagValue(String custMessage,String userIdStr,Long userId,String refererName,String refererEmailStr,String campRepIdStr,Long crId,String sentIdStr,Long sentId,String cIdStr,Long cId,String [] email,String [] name)	
	{

		try {
			FarwardToFriendDao farwardToFriendDao = (FarwardToFriendDao)ServiceLocator.getInstance().getDAOByName("farwardToFriendDao");
			FarwardToFriendDaoForDML farwardToFriendDaoForDML = (FarwardToFriendDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("farwardToFriendDaoForDML");

			List<FarwardToFriend> farwardToFriendList = new ArrayList<FarwardToFriend>();


			for (int z=0; z < email.length; z++)
			{


				if(email[z].length() == 0 ||  email[z].isEmpty() || email[z].equals("Your friend's email ID") ) {
					continue;
				}

				FarwardToFriend farwardToFriend = new FarwardToFriend();
				farwardToFriend.setReferer(refererName);
				farwardToFriend.setEmail(refererEmailStr);
				farwardToFriend.setToEmailId(email[z]);
				farwardToFriend.setSentDate(Calendar.getInstance());

				farwardToFriend.setCustMsg(custMessage);
				farwardToFriend.setContactId(cId);
				farwardToFriend.setSentId(sentId);
				farwardToFriend.setUserId(userId);
				farwardToFriend.setCrId(crId);
				farwardToFriend.setStatus(Constants.CAMP_STATUS_ACTIVE);
				if(name[z].length() == 0 ||  name[z].isEmpty() || name[z].equals("Your friend's name") ) {

					farwardToFriend.setToFullName("");

				}else{
					farwardToFriend.setToFullName(name[z]);
				}

				farwardToFriendList.add(farwardToFriend);



			}

			farwardToFriendDaoForDML.saveByCollection(farwardToFriendList);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
	}


}
