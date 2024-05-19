package org.mq.optculture.business.loyalty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyCardSetDaoForDML;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyCardsGeneration implements Runnable {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private long quantity;
	private String generationType;
	private long cardSetId;
	private String cardSetStatus;
	private long programId;
	private long orgId;
	private long userId;
	
	LoyaltyCardSetDao loyaltyCardSetDao;
	LoyaltyCardSetDaoForDML loyaltyCardSetDaoForDML;
	LoyaltyCardsDao loyaltyCardsDao;
	LoyaltyCardsDaoForDML loyaltyCardsDaoForDML;
	SparkBaseCardDao sparkBaseCardDao;
	SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao;
	
	public LoyaltyCardsGeneration(long quantity, String generationType,
			long cardSetId, String cardSetStatus, long programId, long orgId,long userId) {
		this.quantity = quantity;
		this.generationType = generationType;
		this.cardSetId = cardSetId;
		this.cardSetStatus = cardSetStatus;
		this.programId = programId;
		this.orgId = orgId;
		this.userId = userId;
	}
	
	public void run() {
		logger.info("Thread Starting::"+Thread.currentThread().getName()+"_"+System.currentTimeMillis());
		if(generationType.equalsIgnoreCase(OCConstants.LOYALTY_CARDSET_GEN_TYPE_SEQUENTIAL)) {
			generateSequentialCards();
		}
		else if(generationType.equalsIgnoreCase(OCConstants.LOYALTY_CARDSET_GEN_TYPE_RANDOM)) {
			generateRandomCards();
		}
		logger.info("Thread Finished::"+Thread.currentThread().getName()+"_"+System.currentTimeMillis());
	}
	
	private String generateCard() {
		final long MAX_NUMBER = 9999999999999999L;
		final long MIN_NUMBER = 1000000000000000L;
		String cardNo = "";
		String randNoStr = "";
		
		Long randNo = Long.valueOf(new Random().nextLong());
		
		if(randNo.toString().startsWith("-")) {
			randNoStr = randNo.toString().replace("-", "");
		}
		else {
			randNoStr = randNo.toString();
		}
		
		randNo = Long.valueOf((Long.parseLong(randNoStr) % (MAX_NUMBER-MIN_NUMBER)) + MIN_NUMBER);
		
		cardNo = randNo.toString();
		
		return cardNo;
	}

	private String generateCardPin() {
		final long MAX_NUMBER = 999999L;
		final long MIN_NUMBER = 100000L;
		String cardPin = "";
		String randNoStr = "";
		
		Long randNo = Long.valueOf(new Random().nextLong());
		
		if(randNo.toString().startsWith("-")) {
			randNoStr = randNo.toString().replace("-", "");
		}
		else {
			randNoStr = randNo.toString();
		}
		
		randNo = Long.valueOf((Long.parseLong(randNoStr) % (MAX_NUMBER-MIN_NUMBER)) + MIN_NUMBER);
		
		cardPin = randNo.toString();
		
		return cardPin;
		
	}
	
	private void generateSequentialCards() {
		try {
			loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
			sparkBaseCardDao = (SparkBaseCardDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_CARD_DAO);
			sparkBaseLocationDetailsDao = (SparkBaseLocationDetailsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_LOC_DAO);
			SparkBaseLocationDetails sparkBaseLocationDetails =  sparkBaseLocationDetailsDao.findByOrgId(orgId);
			
			List<LoyaltyCards> cardsList = null;
			do{
				cardsList = new ArrayList<LoyaltyCards>();
				String cardNo = "";
				LoyaltyCards cardObj = null;
				
				if(sparkBaseLocationDetails != null) { 
					do {
						cardNo = generateCard();
					}while(loyaltyCardsDao.findByCardNoAndOrgId(cardNo, orgId) != null || 
						sparkBaseCardDao.getCardByCardId(Long.parseLong(sparkBaseLocationDetails.getLocationId()), Long.parseLong(cardNo)) != null);
				}
				else {
					do {
						cardNo = generateCard();
					}while(loyaltyCardsDao.findByCardNoAndOrgId(cardNo, orgId) != null);
				}
				cardObj = new LoyaltyCards();
				cardObj.setProgramId(programId);
				cardObj.setCardSetId(cardSetId);
				cardObj.setCardNumber(cardNo);
				cardObj.setCardPin(generateCardPin());
				cardObj.setOrgId(orgId);
				cardObj.setStatus("Inventory");
				cardObj.setUserId(userId);
//				cardObj.setCreatedDate(Calendar.getInstance());
//				cardObj.setCreatedBy(userId+"");
				
				cardsList.add(cardObj);
				Long card = Long.parseLong(cardNo);
				boolean isExists = false;
				for(int i=1 ; i < quantity ; i++) {
					logger.info("--------entered i value"+i);
					cardObj = new LoyaltyCards();
					
					card = card +1;
					cardNo = card.toString();
					
					
					boolean condition = false; 
					if(sparkBaseLocationDetails != null) {
						condition = loyaltyCardsDao.findByCardNoAndOrgId(cardNo, orgId) == null && 
								sparkBaseCardDao.getCardByCardId(Long.parseLong(sparkBaseLocationDetails.getLocationId()), Long.parseLong(cardNo)) == null;
					}
					else {
						condition = loyaltyCardsDao.findByCardNoAndOrgId(cardNo, orgId) == null;
					}
					
					
					if(condition) {
						isExists = false;
						
						cardObj.setProgramId(programId);
						cardObj.setCardSetId(cardSetId);
						cardObj.setCardNumber(cardNo);
						cardObj.setCardPin(generateCardPin());
						cardObj.setOrgId(orgId);
						cardObj.setStatus("Inventory");
						cardObj.setUserId(userId);
//						cardObj.setCreatedDate(Calendar.getInstance());
//						cardObj.setCreatedBy(userId+"");
						
						cardsList.add(cardObj);
					}
					else {
						isExists = true;
						break;
					}
					
					
				}
				
				if(!isExists) {
					break;
				}
			}while(true);
			
			//loyaltyCardsDao.saveByCollection(cardsList);
			loyaltyCardsDaoForDML.saveByCollection(cardsList);

			
			LoyaltyCardSet cardSetObj = loyaltyCardSetDao.findByCardSetId(cardSetId);
			
			cardSetObj.setStatus(cardSetStatus);
			//loyaltyCardSetDao.saveOrUpdate(cardSetObj);
			loyaltyCardSetDaoForDML.saveOrUpdate(cardSetObj);

		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		
	}

	private void generateRandomCards() {
		try {
			loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
			sparkBaseCardDao = (SparkBaseCardDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_CARD_DAO);
			sparkBaseLocationDetailsDao = (SparkBaseLocationDetailsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_LOC_DAO);
			SparkBaseLocationDetails sparkBaseLocationDetails =  sparkBaseLocationDetailsDao.findByOrgId(orgId);
			
			List<LoyaltyCards> cardsList = new ArrayList<LoyaltyCards>();
			String cardNo = "";
			LoyaltyCards cardObj = null;
			
			for(int i=0 ; i < quantity ; i++) {
				logger.info("--------entered i value"+i);
				cardObj = new LoyaltyCards();
				if(sparkBaseLocationDetails != null){
					
					do {
						cardNo = generateCard();
					}while(loyaltyCardsDao.findByCardNoAndOrgId(cardNo, orgId) != null || 
						sparkBaseCardDao.getCardByCardId(Long.parseLong(sparkBaseLocationDetails.getLocationId()), Long.parseLong(cardNo)) != null);
				}
				else {
					do {
						cardNo = generateCard();
					}while(loyaltyCardsDao.findByCardNoAndOrgId(cardNo, orgId) != null);
				}
				
				
				cardObj.setProgramId(programId);
				cardObj.setCardSetId(cardSetId);
				cardObj.setCardNumber(cardNo);
				cardObj.setCardPin(generateCardPin());
				cardObj.setOrgId(orgId);
				cardObj.setStatus("Inventory");
				cardObj.setUserId(userId);
//				cardObj.setCreatedDate(Calendar.getInstance());
//				cardObj.setCreatedBy(userId+"");
				
				cardsList.add(cardObj);
				
			}
			
			//loyaltyCardsDao.saveByCollection(cardsList);
			loyaltyCardsDaoForDML.saveByCollection(cardsList);

			LoyaltyCardSet cardSetObj = loyaltyCardSetDao.findByCardSetId(cardSetId);
			cardSetObj.setStatus(cardSetStatus);
			//loyaltyCardSetDao.saveOrUpdate(cardSetObj);
			loyaltyCardSetDaoForDML.saveOrUpdate(cardSetObj);
			
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		
		
		
		
	}

}
