package org.mq.optculture.business.helper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyCardSetDaoForDML;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

/**
 * This class generates loyalty card numbers for OC Loyalty program.
 * 
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyCardGenerator implements Runnable {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private long quantity;
	private String generationType;
	private long cardSetId;
	private String cardSetStatus;
	private long programId;
	private long orgId;
	private long userId;
	
	public LoyaltyCardGenerator(long quantity, String generationType,
			long cardSetId, String cardSetStatus, long programId, long orgId,long userId) {
		this.quantity = quantity;
		this.generationType = generationType;
		this.cardSetId = cardSetId;
		this.cardSetStatus = cardSetStatus;
		this.programId = programId;
		this.orgId = orgId;
		this.userId = userId;
	}
	
	@Override
	public void run() {
		logger.info("Loyalty Cards generation thread Starting::"+Thread.currentThread().getName()+"_"+System.currentTimeMillis());
		if(generationType.equalsIgnoreCase(OCConstants.LOYALTY_CARDSET_GEN_TYPE_SEQUENTIAL)) {
			generateSequentialCards();
			resetCardGenFlag(programId);
		}
		else if(generationType.equalsIgnoreCase(OCConstants.LOYALTY_CARDSET_GEN_TYPE_RANDOM)) {
			generateRandomCards();
			resetCardGenFlag(programId);
		}
		logger.info("Loyalty Cards generation thread Finished::"+Thread.currentThread().getName()+"_"+System.currentTimeMillis());
	}
	
	/**
	 * Generates sequential order type cards and saves them to database under the organisation.
	 */
	private void generateSequentialCards(){
		logger.info("Generating sequential order cards...");
		Set<String> cardSet = sequentialOrder((int)quantity, orgId);
		if(cardSet.size() >= 1) prepareAndSave(cardSet);
		return;
	}
	
	/**
	 * Generates random order type cards and saves them to database under the organisation.
	 */
	private void generateRandomCards(){
		logger.info("Generating random order cards...");
		Set<String> cardSet = randomOrder((int)quantity, orgId);
		//if(cardSet.size() > 1) prepareAndSave(cardSet);
		return;
	}
	
	/**
	 * Sequential order type logic
	 * @param size
	 * @param orgId
	 * @return
	 */
	private Set<String> sequentialOrder(int size, Long orgId) {
		logger.info("sequential order started...size = "+size+" orgId = "+orgId);
		
		Set<String> cardSet = null;
		StringBuffer cardNumberPrefix = new StringBuffer();
		StringBuffer cardNumber = new StringBuffer();
		try{
			UserOrganization userOrg = findUserOrg(orgId);
			
			if(userOrg.getCardSeqPrefix() != null && userOrg.getCardSeqPrefix() == 0) return Collections.emptySet();
			
			Long orgCardId = getOrgCardId(userOrg);
			logger.info("orgCardId = "+orgCardId);
			
			Long seqPrefixLong = userOrg.getCardSeqPrefix();
			logger.info("seqPrefix = "+seqPrefixLong);
			if(seqPrefixLong == null){
				userOrg.setCardSeqPrefix(8L);
				saveUserOrganization(userOrg);
			}
			
			cardNumberPrefix.append(userOrg.getCardSeqPrefix()).append(orgCardId);
			logger.info("cardnumber prefix = "+cardNumberPrefix);
			Long nextSeqNo = userOrg.getNextCardSeqNo();
			if(nextSeqNo == null){
				nextSeqNo = 0l;
			}
			
			cardSet = new LinkedHashSet<String>();
			int i = 1;
			Long nextNumber = nextSeqNo;
			while(i <= size){
				cardNumber = new StringBuffer();
				cardNumber.append(cardNumberPrefix.toString());
				nextNumber += 1;
				if(nextNumber == 99999999){
					userOrg.setCardSeqPrefix(userOrg.getCardSeqPrefix() - 2);
					saveUserOrganization(userOrg);
					sequentialOrder(size, orgId);
					return cardSet;
				}
				long numSize = String.valueOf(nextNumber.longValue()).length();
				while(8 - numSize > 0){
					cardNumber.append("0");
					numSize += 1;
				}
				cardNumber.append(""+nextNumber);
				cardSet.add(cardNumber.toString());
				//logger.info("cardnumber ==== "+cardNumber.toString());
				i++;
			}
			
			if(size == cardSet.size()){
				userOrg.setNextCardSeqNo(nextNumber+1);
				saveUserOrganization(userOrg);
				logger.info("sequential order completed...");
				return cardSet;
			}
			else{
				logger.info("sequential order failed...");
				return Collections.emptySet();
			}
		
		} catch (Exception e) {
			logger.info("sequential order failed...");
			logger.error("Exception in sequential order generation...", e);
			return Collections.emptySet();
		}
	}
	
	/**
	 * Random order type logic
	 * @param size
	 * @param orgId
	 * @return
	 */
	private Set<String> randomOrder(int size, Long orgId) {
		logger.info("random order started...size = "+size+" orgId = "+orgId);
		Set<String> cardSet = null;
		StringBuffer cardNumberPrefix = new StringBuffer();
		StringBuffer cardNumber = new StringBuffer();
		try{
			UserOrganization userOrg = findUserOrg(orgId);
			Long orgCardId = getOrgCardId(userOrg);
			logger.info("orgCardId ="+orgCardId);
			
			Long randPrefixLong = userOrg.getCardRandPrefix();
			logger.info("randPrefix = "+randPrefixLong);
			
			if(randPrefixLong == null){
				userOrg.setCardRandPrefix(7L);
				saveUserOrganization(userOrg);
			}
			cardNumberPrefix.append(userOrg.getCardRandPrefix()).append(orgCardId);
			logger.info("cardnumber prefix = "+cardNumberPrefix);
			cardSet = new LinkedHashSet<String>();
			int i = 1;
			Long nextLoyaltyId = findNextLoyaltyId();
			logger.info("nextLoyaltyId = "+nextLoyaltyId);
			Long nextNumber = null;
			StringBuffer sb = null;
			while(i <= size){
				nextLoyaltyId += 1;
				cardNumber = new StringBuffer();
				cardNumber.append(cardNumberPrefix.toString());
				sb = new StringBuffer();
				sb.append(nextLoyaltyId).append(System.currentTimeMillis());
				nextNumber = generateUniqueCode(sb.toString());
				
				int numSize  = String.valueOf(nextNumber.longValue()).length();
				while(8 - numSize > 0){
					cardNumber.append("0");
					numSize += 1;
				}
				cardNumber.append(nextNumber);
				
				//if(cardSet.contains(cardNumber.toString())) continue;
				if(!saveCard(cardNumber.toString(), userOrg.getUserOrgId())) continue;
				//cardSet.add(cardNumber.toString());
				//logger.info("sequence = "+i+"  cardnumber ==== "+cardNumber.toString());
				i++;
			}
			
			if(i == size+1){
				
				LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
				LoyaltyCardSetDaoForDML loyaltyCardSetDaoForDML = (LoyaltyCardSetDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARD_SET_DAO_FOR_DML);
				LoyaltyCardSet cardSetObj = loyaltyCardSetDao.findByCardSetId(cardSetId);
				cardSetObj.setStatus(cardSetStatus);
				//loyaltyCardSetDao.saveOrUpdate(cardSetObj);
				loyaltyCardSetDaoForDML.saveOrUpdate(cardSetObj);

				logger.info("random order completed...size = "+size+" and cardSet size = "+i);
				return cardSet;
			}
			else{
				logger.info("random order failed...size = "+size+" and cardSet size = "+i);
				return Collections.emptySet();
			}
		
		} catch (Exception e) {
			logger.info("random order failed...");
			logger.error("Exception in random order generation...", e);
			return Collections.emptySet();
		}
	}
	
	private boolean saveCard(String cardNumber, Long orgId){
		try{
			
			LoyaltyCards cardObj = new LoyaltyCards();
			cardObj.setProgramId(programId);
			cardObj.setCardSetId(cardSetId);
			cardObj.setCardNumber(cardNumber);
			cardObj.setCardPin(generateCardPin());
			cardObj.setOrgId(orgId);
			cardObj.setStatus("Inventory");
			cardObj.setUserId(userId);
			cardObj.setRegisteredFlag(OCConstants.FLAG_NO);
			
			LoyaltyCardsDao cardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			LoyaltyCardsDaoForDML cardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
			//LoyaltyCards card = cardsDao.findByCardNoAndOrgId(cardNumber, orgId);
			//cardsDao.save(cardObj);
			cardsDaoForDML.save(cardObj);
			return true;
		}catch(Exception e){
			logger.error("Exception in finding duplicate card...", e);
			return false;
		}
	}
	
	
	/**
	 * Saves cardSet under the organisation
	 * @param cardSet
	 */
	private void prepareAndSave(Set<String> cardSet){
		List<LoyaltyCards> cardsList = null;
		
		try{
			cardsList = new ArrayList<LoyaltyCards>();
			LoyaltyCards cardObj = null;
			
			for(String card : cardSet){
				cardObj = new LoyaltyCards();
				cardObj.setProgramId(programId);
				cardObj.setCardSetId(cardSetId);
				cardObj.setCardNumber(card);
				cardObj.setCardPin(generateCardPin());
				cardObj.setOrgId(orgId);
				cardObj.setStatus("Inventory");
				cardObj.setUserId(userId);
				cardObj.setRegisteredFlag(OCConstants.FLAG_NO);
				cardsList.add(cardObj);
			}
			
			LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);

			//loyaltyCardsDao.saveByCollection(cardsList);
			loyaltyCardsDaoForDML.saveByCollection(cardsList);
			
			LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			LoyaltyCardSetDaoForDML loyaltyCardSetDaoForDML = (LoyaltyCardSetDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARD_SET_DAO_FOR_DML);
			LoyaltyCardSet cardSetObj = loyaltyCardSetDao.findByCardSetId(cardSetId);
			cardSetObj.setStatus(cardSetStatus);
			//loyaltyCardSetDao.saveOrUpdate(cardSetObj);
			loyaltyCardSetDaoForDML.saveOrUpdate(cardSetObj);

		} catch (Exception e){
			logger.error("Exception while generating loyatly cards...", e);
		}
	}
	
	/**
	 * Fetches cardId for the organisation.
	 * @param userOrg
	 * @return
	 * @throws Exception
	 */
	private Long getOrgCardId(UserOrganization userOrg) throws Exception {
		logger.info("Getting organisation cardId...");
		
		Long orgCardId = null;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
		
		orgCardId = userOrg.getCardId();
		if (orgCardId != null && !orgCardId.toString().trim().isEmpty()) return orgCardId;
		orgCardId = generateCardId();
		logger.info("orgCardId = "+orgCardId);
		Long userOrgId = null;
		while(userOrgId == null) {
			userOrgId = usersDao.findByCardId(orgCardId);
			if(userOrgId != null){
				orgCardId = generateCardId();
				userOrgId = null;
			}
			break;
		}
		userOrg.setCardId(orgCardId);
		//usersDao.saveOrUpdate(userOrg);
		usersDaoForDML.saveOrUpdate(userOrg);
		return orgCardId;
	}
	
	/**
	 * Generates new cardId for the organisation
	 * @return
	 */
	private Long generateCardId() {
		logger.info("Generating new organisation cardId...");
		
		final int MAX_NUMBER = 9999999;
		final long MIN_NUMBER = 1000000L;
		Long randNo = 0L;
		String randNoStr = "";
		while(randNo < MIN_NUMBER){
			randNo = Long.valueOf(new Random().nextInt(MAX_NUMBER));
			if(randNo.toString().startsWith("-")) {
				randNoStr = randNo.toString().replace("-", "");
				randNo = Long.valueOf(randNoStr);
			}
			else {
				randNoStr = randNo.toString();
				randNo = Long.valueOf(randNoStr);
			}
		}
		return randNo;
	}
	
	/**
	 * Generates unique random number
	 * @param inputURL
	 * @return
	 */
	private Long generateUniqueCode(String inputURL) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(inputURL.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);
			while (hashtext.length() < 32) {
		       hashtext = "0" + hashtext;
			}
			String subHexStr = hashtext.substring(0, 8);
			Long uniqueCodeLong = Long.parseLong(subHexStr, 16);
			if(uniqueCodeLong.toString().length() < 8) return Long.valueOf(uniqueCodeLong.toString());
			else return Long.valueOf(uniqueCodeLong.toString().substring(0, 8));
		} catch (Exception e) {
			logger.error("Exception ::", e);
			return null;
		}
	} 
	
	/**
	 * Finds next primary key of the loyalty cards table
	 * @return
	 * @throws Exception
	 */
	private Long findNextLoyaltyId() throws Exception {
		LoyaltyCardsDao cardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return cardsDao.findNextPrimaryKey();
	}
	
	/**
	 * Saves user organisation object
	 * @param userOrg
	 * @throws Exception
	 */
	private void saveUserOrganization(UserOrganization userOrg) throws Exception {
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
		//usersDao.saveOrUpdate(userOrg);
		usersDaoForDML.saveOrUpdate(userOrg);
		
	}
	
	/**
	 * Finds user organisation by orgId
	 * @param orgId
	 * @return
	 * @throws Exception
	 */
	private UserOrganization findUserOrg(Long orgId) throws Exception {
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		return usersDao.findByOrgId(orgId);
	}
	
	/**
	 * Generates card pin
	 * @return
	 */
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
	
	/**
	 * Resets card generation flag (N) to the organisation after card generation is completed.
	 * @param programId
	 */
	private void resetCardGenFlag(Long programId) {
		logger.info("Setting card gen flag N to the organisation...");
		try{
			LoyaltyProgramDao programDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			LoyaltyProgram program = programDao.findById(programId);
			Long orgId = program.getOrgId();
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO); 
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
			UserOrganization userOrg = usersDao.findByOrgId(orgId);
			userOrg.setCardGenerateFlag(OCConstants.LOYALTY_CARD_GENERATION_FLAG_N);
			//usersDao.saveOrUpdate(userOrg);
			usersDaoForDML.saveOrUpdate(userOrg);
		} catch(Exception e) {
			logger.error("excetpion...", e);
			return;
		}
	}
	
}
