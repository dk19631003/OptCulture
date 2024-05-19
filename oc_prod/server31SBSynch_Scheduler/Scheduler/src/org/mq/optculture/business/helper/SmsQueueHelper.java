/**
 * 
 */
package org.mq.optculture.business.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SMSCampaigns;
import org.mq.captiway.scheduler.beans.SmsQueue;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.SmsQueueDao;
import org.mq.captiway.scheduler.dao.SmsQueueDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

/**
 * @author vinod.bokare
 * This helper Object helps to insert InMemory smsQueue Object to Persistent Storage.
 */
public class SmsQueueHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	/**
	 * This method performs task to insert InMemory smsQueue Object to Persistent Storage.
	 * @param mblNum
	 * @param sendingMsg
	 * @param messageType
	 * @param smsCampaign
	 * @param user
	 * @param senderId
	 */
	public void updateSMSQueue(String mblNum,String msgContent , String messageType , Users currUser,String senderId) {
		logger.debug(">>>>>>> Started SmsQueueHelper :: updateSMSQueue <<<<<<< ");
		if(mblNum == null){
			logger.error("Error While Updating SMS Queue as the no Mobile Number exist's");
			return;
		}

		List<SmsQueue> smsQueues = new ArrayList<SmsQueue>();
		String []mobArray = mblNum.split(",");

		SmsQueueDao smsQueueDao = null;
		SmsQueueDaoForDML smsQueueDaoForDML = null;
		try{
			smsQueueDao = (SmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_QUEUE_DAO);
			smsQueueDaoForDML = (SmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_QUEUE_DAO_For_DML);
			}

		catch(Exception exception){
			logger.error("Error While creating SmsQueueDao Object....:",exception);
		}

		for(String mobileNumber : mobArray){

			if(smsQueueDao != null){
				SmsQueue smsQueue = new SmsQueue();
				smsQueue.setMessage(msgContent);
				smsQueue.setMsgType(messageType);
				smsQueue.setToMobilePhone(mobileNumber);
				smsQueue.setUser(currUser);
				smsQueue.setSentDate(MyCalendar.getNewCalendar());
				smsQueue.setStatus(Constants.CAMP_STATUS_ACTIVE);
				//smsQueue.setSmsCampaigns(smsCampaign);
				smsQueue.setSenderId(senderId);
				//Add to list
				smsQueues.add(smsQueue);
				//Future Implementation
				//smsQueue.setMsgId(msgId);
				//smsQueue.setDlrStatus(dlrStatus);
				//Not Required smsQueue.setSmsCampaigns(smsCampaign);
			}
		}//for each
		/**
		 * Storing to DB 
		 */

		if(smsQueueDao != null){
			//smsQueueDao.saveOrUpdate(smsQueues);
			smsQueueDaoForDML.saveOrUpdate(smsQueues);
		}
		logger.debug(">>>>>>> Completed SmsQueueHelper :: updateSMSQueue <<<<<<< ");
	}//updateSMSQueue

}
