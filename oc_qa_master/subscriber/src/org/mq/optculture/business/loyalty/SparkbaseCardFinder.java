package org.mq.optculture.business.loyalty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SparkBaseCard;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseCardDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class SparkbaseCardFinder {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public  SparkBaseCard findInventoryCardFromLocation(Long sbLocId, String cardType){
		logger.info("Entered findInventoryCardFromLocation >>>>>"+ Thread.currentThread().getName());
		SparkBaseCard sbcard = null;
		try{
			synchronized (this) {
				SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO);
				SparkBaseCardDaoForDML sparkBaseCardDaoForDML = (SparkBaseCardDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPARKBASECARD_DAO_FOR_DML);
				sbcard = sparkBaseCardDao.findAvailableCardByStore(sbLocId, cardType);
				if(sbcard != null){
					// sbcard.setStatus(Constants.SPARKBASE_CARD_STATUS_SELECTED);
					String updateQuery = "update SparkBaseCard set status='selected' where sparkBaseCard_id = " + sbcard.getSparkBaseCard_id() + " and status='inventory'";
					//int count = sparkBaseCardDao.executeUpdate(updateQuery);
					int count = sparkBaseCardDaoForDML.executeUpdate(updateQuery);
					if(count == 0){
						findInventoryCardFromLocation(sbLocId, cardType);
						logger.info("status of card is not updated calling again >>>>>"+sbcard.getCardId());
					}
					else 
						logger.info("status of card set selected >>>>>"+sbcard.getCardId());
					
					logger.info(" got card >>>>>"+sbcard.getCardId() + "   thread name  " + Thread.currentThread().getName());
				}
				else {
					logger.info("no card available of type  >>>>> "+cardType + "   thread name  " + Thread.currentThread().getName());
				}
			}
		}catch(Exception e){
			logger.error("Exception while getting inventory card...", e);
		}
		logger.info("Exited findInventoryCardFromLocation >>>>>   thread name  " + Thread.currentThread().getName());
		return sbcard;
	}
}
