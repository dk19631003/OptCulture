/**
 * 
 */
package org.mq.optculture.business.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.WAQueue;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.WAQueueDao;
import org.mq.marketer.campaign.dao.WAQueueDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

/**
 * @author vinod.bokare
 * This helper Object helps to insert InMemory waQueue Object to Persistent Storage.
 */
public class WAQueueHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	/**
	 * This method performs task to insert InMemory waQueue Object to Persistent Storage.
	 * @param mblNum
	 * @param sendingMsg
	 * @param messageType
	 * @param waCampaign
	 * @param user
	 * @param senderId
	 */
	public void updateWAQueue(String mblNum,String msgContent , String messageType , Users currUser) {
		logger.debug(">>>>>>> Started WaQueueHelper :: updateWaQueue <<<<<<< ");
		if(mblNum == null){
			logger.error("Error While Updating WA Queue as the no Mobile Number exist's");
			return;
		}

		List<WAQueue> waQueues = new ArrayList<WAQueue>();
		String []mobArray = mblNum.split(",");

		WAQueueDao waQueueDao = null;
		WAQueueDaoForDML waQueueDaoForDML = null;
		try{
			waQueueDao = (WAQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.WA_QUEUE_DAO);
			waQueueDaoForDML = (WAQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.WA_QUEUE_DAO_ForDML);

		}
		catch(Exception exception){
			logger.error("Error While creating WAQueueDao Object....:",exception);
		}

		for(String mobileNumber : mobArray){

			if(waQueueDao != null){
				WAQueue waQueue = new WAQueue();
				waQueue.setMessage(msgContent);
				waQueue.setMsgType(messageType);
				waQueue.setToMobilePhone(mobileNumber);
				waQueue.setUser(currUser);
				waQueue.setSentDate(MyCalendar.getNewCalendar());
				waQueue.setStatus(Constants.CAMP_STATUS_ACTIVE);
				waQueues.add(waQueue);
				
							}
		}//for each
		/**
		 * Storing to DB 
		 */

		if(waQueueDao != null){
			waQueueDaoForDML.saveByCollection(waQueues);

		}
		logger.debug(">>>>>>> Completed WAQueueHelper :: updateWAQueue <<<<<<< ");
	}//updateWAQueue

}//EOF
