package org.mq.optculture.timer;

import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgramTrans;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.LoyaltyProgramTransDao;
import org.mq.optculture.data.dao.LoyaltyProgramTransDaoForDML;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyRewardExpiryAutoComm extends TimerTask{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public void run() {
		logger.info(">>> Started loyalty Rewards Expiry timer >>>");
		
		Calendar startTime = Calendar.getInstance();
		
		List<LoyaltyProgramTrans> listOfTrans;
		try {
			listOfTrans = fetchCurrentActiveTransForExpiry(startTime);
			logger.info(">>>>>>>>>>> list size::"+listOfTrans.size());
			if(listOfTrans == null || listOfTrans.isEmpty()){
				return;
			}
			for (LoyaltyProgramTrans transaction : listOfTrans) {
				logger.info(">>>>>>>>>>> entered for::");
				LoyaltyProgramTransDao loyaltyProgramTransDao = (LoyaltyProgramTransDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TRANS_DAO);
				LoyaltyProgramTransDaoForDML loyaltyProgramTransDaoForDML = (LoyaltyProgramTransDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_TRANS_DAO_FOR_DML);
				transaction.setExpiryStatus(OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_WORKING);
				//loyaltyProgramTransDao.saveOrUpdate(transaction);
				loyaltyProgramTransDaoForDML.saveOrUpdate(transaction);
				
				updateExpiry(transaction);
				
				transaction.setNetEarnedValueStatus("Expired");
				transaction.setExpiryStatus(OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED);
				//loyaltyProgramTransDao.saveOrUpdate(transaction);
				loyaltyProgramTransDaoForDML.saveOrUpdate(transaction);
			}
		
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		
		logger.info(">>> Completed loyalty Rewards Expiry timer >>>");
	}
	
	private List<LoyaltyProgramTrans> fetchCurrentActiveTransForExpiry(Calendar startTime) throws LoyaltyProgramException {
		
		List<LoyaltyProgramTrans> listOfTrans =null;
		try{
		
			LoyaltyProgramTransDao loyaltyProgramTransDao = (LoyaltyProgramTransDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TRANS_DAO);
			listOfTrans = loyaltyProgramTransDao.fetchCurrentActiveTransForExpiry(startTime);
			logger.info("list  ::::::::::"+listOfTrans);
			if(listOfTrans != null && listOfTrans.size() > 0){
				logger.info("list size ::::::::::"+listOfTrans.size());
				return listOfTrans;
			}
		
		}catch(Exception e){
			logger.error("Exception in dao service...");
			throw new LoyaltyProgramException("fetch current active trans failed.");
		}
		
		return listOfTrans;
			
	}
	
	private void updateExpiry(LoyaltyProgramTrans transaction) {
		try {
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(transaction.getUserId(), transaction.getCardNumber());
			
			String valueCode = transaction.getEarnType();
//			Double netEarnedPoints = transaction.getNetEarnedPoints();
//			Double netEarnedAmount = transaction.getNetEarnedAmount();
			Double availableValue = transaction.getAvailableValue();
			
			if(valueCode != null && valueCode.equalsIgnoreCase(OCConstants.LOYALTY_POINTS) && availableValue != null){
				
				contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - availableValue);
				
			}
			else if(valueCode != null && valueCode.equalsIgnoreCase(OCConstants.LOYALTY_USD) && availableValue != null){
			
				contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() - availableValue);
				
			}				

			contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
			
		
		} catch (Exception e) {
			logger.error("Exception in update expiry points...", e);
		} 
		
	}

}
