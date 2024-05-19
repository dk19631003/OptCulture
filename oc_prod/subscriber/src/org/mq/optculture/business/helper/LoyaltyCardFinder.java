package org.mq.optculture.business.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;


import java.util.HashMap;
import java.util.Map;


public class LoyaltyCardFinder {
	 

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private static LoyaltyCardFinder loyaltyCardFinder;
	public static LoyaltyCards findInventoryCard(String programIdStr, String cardSetIdStr, String cardNumber, Long userId){
		logger.info("Entered findInventoryCard >>>>>");
		
		LoyaltyCards loyaltyCard = null;
		try{
			LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
			
			if(programIdStr != null && cardSetIdStr != null && cardNumber != null && userId != null){
				loyaltyCard = loyaltyCardsDao.getInventoryCard(programIdStr, cardSetIdStr, cardNumber, userId);
				
				if(loyaltyCard == null || loyaltyCard.getStatus().equals(OCConstants.LOYALTY_CARD_STATUS_SELECTED)){
					logger.info("loyalty card is null or with selected status");
					return null;
				}
				else if(loyaltyCard.getStatus().equals(OCConstants.LOYALTY_CARD_STATUS_INVENTORY)){ //inventory
					logger.info("loyaltycard with inventory status");
					loyaltyCard.setStatus(OCConstants.LOYALTY_CARD_STATUS_SELECTED);
					//loyaltyCardsDao.saveOrUpdate(loyaltyCard);
					loyaltyCardsDaoForDML.saveOrUpdate(loyaltyCard);
					loyaltyCard.setStatus(OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
					//loyaltyCard.setStatus(OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
					return loyaltyCard;
				}
				logger.info("loyaltycard with active/enrolled/ or other than selected and inventory status");
				return loyaltyCard; // not inventory
			}
			else if(programIdStr != null && cardSetIdStr != null && cardNumber == null && userId != null){
					loyaltyCard = loyaltyCardsDao.getInventoryCard(programIdStr, cardSetIdStr, cardNumber, userId);
					
					if(loyaltyCard == null || loyaltyCard.getStatus().equals(OCConstants.LOYALTY_CARD_STATUS_SELECTED)){
						return null;
					}
					else if(loyaltyCard.getStatus().equals(OCConstants.LOYALTY_CARD_STATUS_INVENTORY)){ //inventory
						loyaltyCard.setStatus(OCConstants.LOYALTY_CARD_STATUS_SELECTED);
						//loyaltyCardsDao.saveOrUpdate(loyaltyCard);
						loyaltyCardsDaoForDML.saveOrUpdate(loyaltyCard);
						
						//loyaltyCard.setStatus(OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
						return loyaltyCard;
					}
					return loyaltyCard; // not inventory
			}
			
		}catch(Exception e){
			logger.error("Exception while getting inventory card...", e);
		}
		logger.info("Exited findInventoryCardFromLocation >>>>>");
		return loyaltyCard;
	}
	private static Map<Long, Boolean> loyaltyUsersMap = new HashMap<Long, Boolean>();
	public Map<Long, Boolean> getLoyaltyUsersMap() {
		return loyaltyUsersMap;
	}
	
	public static boolean letMeIn(Long userID){
		logger.info("Just getting a chance===="+Thread.currentThread().getName());
		synchronized (loyaltyUsersMap) {
			
			if(loyaltyUsersMap.get(userID) == null){
				loyaltyUsersMap.put(userID, true);
				return true;
				
			}
			
			if(loyaltyUsersMap.get(userID)== true) {
				
				return false;
			}
			else{
				loyaltyUsersMap.put(userID, true);
				return true;
			}
			
		}
	}
	
	public static void MarkIamDone(Long userID){
		
		synchronized (loyaltyUsersMap) {
			
			loyaltyUsersMap.put(userID, false);
		}
	}

	
		
}
