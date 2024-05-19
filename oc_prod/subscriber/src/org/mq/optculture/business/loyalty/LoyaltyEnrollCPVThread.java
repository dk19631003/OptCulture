package org.mq.optculture.business.loyalty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyEnrollCPVThread implements Runnable{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private List<LoyaltyProgramTier> tiersList;
	private Map<LoyaltyProgramTier,LoyaltyProgramTier> eligibleMap;
	private Users user;
	private ContactsLoyalty loyalty;
	private LoyaltyTransactionChild transChild;
	private LoyaltyProgram program;
	private LoyaltyEnrollRequest enrollRequest;
	
	public LoyaltyEnrollCPVThread(Map<LoyaltyProgramTier,LoyaltyProgramTier> eligibleMap, Users user, ContactsLoyalty loyalty, List<LoyaltyProgramTier> tierList, LoyaltyTransactionChild transChild, LoyaltyProgram program, LoyaltyEnrollRequest enrollRequest) {
		this.eligibleMap = eligibleMap;
		this.user = user;
		this.loyalty = loyalty;
		this.tiersList = tierList;
		this.transChild = transChild;
		this.program = program;
		this.enrollRequest = enrollRequest;
	}

	@Override
	public void run() {

		LoyaltyProgramTier tier =  findTier();
		saveProperties(tier);
		updateEnrollmentTransaction(tier);
	}

	private void updateEnrollmentTransaction(LoyaltyProgramTier tier) {
		try{
			LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(program.getProgramId());

			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			//Send Loyalty Registration Email
			if(loyalty.getContact().getEmailId() != null && loyaltyAutoComm != null && loyaltyAutoComm.getRegEmailTmpltId() != null){
				//email queue
				autoCommGen.sendEnrollTemplate(loyaltyAutoComm.getRegEmailTmpltId(), ""+loyalty.getCardNumber(), 
						loyalty.getCardPin(), user, loyalty.getContact().getEmailId(), loyalty.getContact().getFirstName(),
						loyalty.getContact().getContactId(), loyalty.getLoyaltyId(),"");
			}
			if (user.isEnableSMS() && loyaltyAutoComm != null && loyaltyAutoComm.getRegSmsTmpltId() != null && loyalty.getMobilePhone() != null) {
				//sms queue
				Long cid = null;
				if (loyalty.getContact() != null && loyalty.getContact().getContactId() != null) {
					cid = loyalty.getContact().getContactId();
				}
				autoCommGen.sendEnrollSMSTemplate(loyaltyAutoComm.getRegSmsTmpltId(), user, cid, loyalty.getLoyaltyId(),
						loyalty.getMobilePhone(), loyalty.getMemPwd() != null ?  loyalty.getMemPwd() : "");
			}

			updateMembershipBalances(enrollRequest, loyalty, program, loyaltyAutoComm, user, tier);

			saveContactLoyalty(loyalty);
		}catch(Exception e){
			logger.error("Exception ::",e);
		}
	}
	
	private LoyaltyAutoComm getLoyaltyAutoComm(Long programId){
		try{
			LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
			return autoCommDao.findById(programId);
		}catch(Exception e){
			logger.error("Exception in getting auto comm object...", e);
			return null;
		}
	}
	
	private void updateMembershipBalances(LoyaltyEnrollRequest enrollRequest, ContactsLoyalty loyalty, LoyaltyProgram program, 
			LoyaltyAutoComm autoComm, Users user, LoyaltyProgramTier tier) throws Exception {
		Double fromLtyBalance = loyalty.getTotalLoyaltyEarned();
		Double fromAmtBalance = loyalty.getTotalGiftcardAmount();
		LoyaltyThresholdBonusDao bonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
		List<LoyaltyThresholdBonus> bonusList = bonusDao.getBonusListByPrgmId(program.getProgramId());
		if(bonusList == null || bonusList.size() == 0) return;
		for(LoyaltyThresholdBonus bonus : bonusList){
			if(bonus.getRegistrationFlag() == 'Y'){
				String earnType = null;
				double earnedValue = 0;
				boolean bonusPointsFlag = false;

				if(bonus.getExtraBonusType().equals(OCConstants.LOYALTY_TYPE_POINTS)){
					bonusPointsFlag = true;
					earnType = OCConstants.LOYALTY_TYPE_POINTS;
					earnedValue = bonus.getExtraBonusValue();

					if(loyalty.getTotalLoyaltyEarned() == null){
						loyalty.setTotalLoyaltyEarned(bonus.getExtraBonusValue());
					}
					else{
						loyalty.setTotalLoyaltyEarned(loyalty.getTotalLoyaltyEarned()+bonus.getExtraBonusValue());
					}

					if(loyalty.getLoyaltyBalance() == null){
						loyalty.setLoyaltyBalance(bonus.getExtraBonusValue());
					}
					else{
						loyalty.setLoyaltyBalance(loyalty.getLoyaltyBalance()+bonus.getExtraBonusValue());
					}

				}
				else if(bonus.getExtraBonusType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){

					earnType = OCConstants.LOYALTY_TYPE_AMOUNT;
					earnedValue = bonus.getExtraBonusValue();

					if(loyalty.getTotalGiftcardAmount() == null){
						loyalty.setTotalGiftcardAmount(bonus.getExtraBonusValue());
					}
					else{
						loyalty.setTotalGiftcardAmount(loyalty.getTotalGiftcardAmount()+bonus.getExtraBonusValue());
					}

					if(loyalty.getGiftcardBalance() == null){
						loyalty.setGiftcardBalance(bonus.getExtraBonusValue());
					}
					else{
						loyalty.setGiftcardBalance(loyalty.getGiftcardBalance()+bonus.getExtraBonusValue());
					}
				}

				LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
				// Send auto communication email
				if(autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null && loyalty.getContact().getEmailId() != null){
					/*autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+loyalty.getCardNumber(),
								loyalty.getCardPin(), user, emailId, firstName, contactId);*/
					autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+loyalty.getCardNumber(), 
							loyalty.getCardPin(), user, loyalty.getContact().getEmailId(), loyalty.getContact().getFirstName(), loyalty.getContact().getContactId(), loyalty.getLoyaltyId());
				}
				if(autoComm != null && autoComm.getThreshBonusSmsTmpltId() != null && loyalty.getMobilePhone() != null){
					autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, loyalty.getContact().getContactId(), 
							loyalty.getLoyaltyId(), loyalty.getMobilePhone());
				}

				// create a child transaction
				LoyaltyTransactionChild childTxbonus = createBonusTransaction(loyalty, earnedValue, earnType, "Registration Bonus");

				double pointsbonus = 0.0;
				double amountbonus = 0.0;
				if(bonusPointsFlag){
					pointsbonus = bonus.getExtraBonusValue();
				}
				else{
					amountbonus = bonus.getExtraBonusValue();
				}

				createExpiryTransaction(loyalty, (long)pointsbonus, amountbonus, loyalty.getOrgId(), 
						childTxbonus.getTransChildId(),bonus.getThresholdBonusId());

				applyConversionRules(loyalty, childTxbonus, program, tier);
				tier = applyTierUpgradeRule(loyalty, program, childTxbonus, tier);

				break;
			}//if bonus flag
		}
		updateThresholdBonus(loyalty, program, fromLtyBalance, fromAmtBalance, tier);

	}
	
	private void updateThresholdBonus(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, Double fromLtyBalance, Double fromAmtBalance, LoyaltyProgramTier tier) throws Exception {
		logger.info(" Entered into updateThresholdBonus method >>>");
		
		try{
			LoyaltyThresholdBonusDao loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			List<LoyaltyThresholdBonus> threshBonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(program.getProgramId(), 'N' );
			List<LoyaltyThresholdBonus> pointsBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> amountBonusList = new ArrayList<LoyaltyThresholdBonus>();
			fromAmtBalance = fromAmtBalance == null ? 0.0 : fromAmtBalance;
			fromLtyBalance = fromLtyBalance == null ? 0.0 : fromLtyBalance;
			
			if(threshBonusList == null) return;
			
			for(LoyaltyThresholdBonus bonus : threshBonusList){
				if(bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_POINTS)){
					pointsBonusList.add(bonus);
				}
				else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
					amountBonusList.add(bonus);
				}
			}
			
			if(pointsBonusList.isEmpty() && amountBonusList.isEmpty()) return;
			
			List<LoyaltyThresholdBonus> matchedBonusList = new ArrayList<LoyaltyThresholdBonus>();
			
			if(pointsBonusList.size() > 0){
				Collections.sort(pointsBonusList, new Comparator<LoyaltyThresholdBonus>(){
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			if(amountBonusList.size() > 0){
				Collections.sort(amountBonusList, new Comparator<LoyaltyThresholdBonus>(){
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			if(contactsLoyalty.getTotalLoyaltyEarned() != null && contactsLoyalty.getTotalLoyaltyEarned() > 0){
				for(LoyaltyThresholdBonus bonus : pointsBonusList){
					if(contactsLoyalty.getTotalLoyaltyEarned() >= bonus.getEarnedLevelValue() && 
							(fromLtyBalance == null || fromLtyBalance.doubleValue() < bonus.getEarnedLevelValue())){
						matchedBonusList.add(bonus);
					}
				}
			}
			if(contactsLoyalty.getTotalGiftcardAmount() != null && contactsLoyalty.getTotalGiftcardAmount() > 0){
				for(LoyaltyThresholdBonus bonus : amountBonusList){
					if(contactsLoyalty.getTotalGiftcardAmount() >= bonus.getEarnedLevelValue() && 
							(fromAmtBalance == null || fromAmtBalance.doubleValue() < bonus.getEarnedLevelValue())){
						matchedBonusList.add(bonus);
					}
				}
			}
			 
			long bonusPoints = 0;
			double bonusAmount = 0.0;
			String bonusRate = null;
			boolean bonusflag =false;
			
			if(matchedBonusList != null && matchedBonusList.size() > 0){
				for (LoyaltyThresholdBonus matchedBonus : matchedBonusList) {
					if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getExtraBonusType())){
						bonusflag = true;
						logger.info("loyalty bonus type :Points:");
						bonusPoints = matchedBonus.getExtraBonusValue().longValue();
						bonusRate = ""+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
								" --> "+matchedBonus.getExtraBonusValue()+" "+OCConstants.LOYALTY_TYPE_POINTS;
						if(contactsLoyalty.getLoyaltyBalance() == null ) {
							contactsLoyalty.setLoyaltyBalance(matchedBonus.getExtraBonusValue());
						}
						else{
							contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() + matchedBonus.getExtraBonusValue());
						}
						if(contactsLoyalty.getTotalLoyaltyEarned() == null){
							contactsLoyalty.setTotalLoyaltyEarned(matchedBonus.getExtraBonusValue());
						}
						else{
							contactsLoyalty.setTotalLoyaltyEarned(contactsLoyalty.getTotalLoyaltyEarned() + matchedBonus.getExtraBonusValue());
						}
						LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, Double.valueOf(matchedBonus.getExtraBonusValue()), OCConstants.LOYALTY_TYPE_POINTS, bonusRate);
								
						logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
						createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
								childTxbonus.getTransChildId(),matchedBonus.getThresholdBonusId());
						
						applyConversionRules(contactsLoyalty, childTxbonus, program, tier);
						tier = applyTierUpgradeRule(contactsLoyalty, program, childTxbonus, tier);
						
					}
					else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getExtraBonusType())){
						bonusflag = true;
						logger.info("loyalty bonus type :Amount:");
						bonusAmount = matchedBonus.getExtraBonusValue();
						bonusRate = ""+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
								" --> "+matchedBonus.getExtraBonusValue()+" "+OCConstants.LOYALTY_TYPE_AMOUNT;
						if(contactsLoyalty.getGiftcardBalance() == null ) {
							contactsLoyalty.setGiftcardBalance(matchedBonus.getExtraBonusValue());
						}
						else{
							contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + matchedBonus.getExtraBonusValue());
						}
						if(contactsLoyalty.getTotalGiftcardAmount() == null){
							contactsLoyalty.setTotalGiftcardAmount(matchedBonus.getExtraBonusValue());
						}
						else{
							contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + matchedBonus.getExtraBonusValue());
						}
						LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, Double.valueOf(matchedBonus.getExtraBonusValue()), OCConstants.LOYALTY_TYPE_AMOUNT, bonusRate);
								
						logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
						createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
								childTxbonus.getTransChildId(),matchedBonus.getThresholdBonusId());
						
						applyConversionRules(contactsLoyalty, childTxbonus, program, tier);
						tier = applyTierUpgradeRule(contactsLoyalty, program, childTxbonus, tier);
					}
				}
			}
			
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			LoyaltyAutoComm autoComm = getLoyaltyAutoComm(program.getProgramId());
			if(bonusflag && autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
					contactsLoyalty.getContact().getContactId() != null){
				Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
				if(contact != null && contact.getEmailId() != null){
					autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
							contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
							contact.getContactId(), contactsLoyalty.getLoyaltyId());
				}
			}
			UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = userDao.findByUserId(contactsLoyalty.getUserId());
			if (user.isEnableSMS() && bonusflag && autoComm != null && autoComm.getThreshBonusSmsTmpltId() != null){
				Long contactId = null;
				if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
					contactId = contactsLoyalty.getContact().getContactId();
				}
				autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, contactId,
							contactsLoyalty.getLoyaltyId(), null);
			}
			logger.info("bonus ...points = "+bonusPoints+" amount = "+bonusAmount);
			logger.info("Completed updateThresholdBonus method <<<");
			return;
		}catch(Exception e){
			logger.error("Exception in update threshold bonus...", e);
			throw new LoyaltyProgramException("Exception in threshold bonus...");
		}
	}
	

	private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty,
			Long expiryPoints, Double expiryAmount, Long orgId, Long transChildId,Long bonusId ){
		
		LoyaltyTransactionExpiry transaction = null;
		try{
			
			transaction = new LoyaltyTransactionExpiry();
			transaction.setTransChildId(transChildId);
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setOrgId(orgId);
			transaction.setUserId(loyalty.getUserId());
			transaction.setExpiryPoints(expiryPoints);
			transaction.setExpiryAmount(expiryAmount);
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setBonusId(bonusId);
			
			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			//loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
			loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);
			
		}catch(Exception e){
			logger.error("Exception while logging enroll bonus expiry transaction...",e);
		}
		return transaction;
	}
	
	private LoyaltyTransactionChild createBonusTransaction(ContactsLoyalty loyalty,	double earnedValue, String earnType, String bonusRate){
		
		LoyaltyTransactionChild transaction = null;
		try{
			
			transaction = new LoyaltyTransactionChild();
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setEarnType(earnType);
			if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
				transaction.setEarnedPoints((double)earnedValue);
			}
			else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
				transaction.setEarnedAmount((double)earnedValue);
			}
			transaction.setEnteredAmount((double)earnedValue);
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_BONUS);
			transaction.setDescription(bonusRate);
			transaction.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_AUTO);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		return transaction;
	}
	
	
	private Contacts findContactById(Long cid) throws Exception {
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactsDao.findById(cid);
	}
	
	private LoyaltyProgramTier applyTierUpgradeRule(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, LoyaltyTransactionChild transactionChild, LoyaltyProgramTier currTier){
		logger.debug(">>>>>>>>>>>>> entered in applyTierUpgradeRule");
		try{
			boolean tierUpgd = false;

			LoyaltyProgramTier newTier = LoyaltyProgramHelper.applyTierUpgdRules(contactsLoyalty.getContact().getContactId(), contactsLoyalty, currTier);
			if(!newTier.getTierType().equalsIgnoreCase(currTier.getTierType())){
				currTier = newTier;
				tierUpgd = true;
			}
			
			if(tierUpgd){
				contactsLoyalty.setProgramTierId(currTier.getTierId());
				contactsLoyalty.setTierUpgradedDate(Calendar.getInstance());
				contactsLoyalty.setTierUpgradeReason(currTier.getTierUpgdConstraint());
				ContactsLoyaltyDaoForDML contactsLoyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
				contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);

				transactionChild.setTierId(currTier.getTierId());
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
				//loyaltyTransactionChildDao.saveOrUpdate(transactionChild);
				loyaltyTransactionChildDaoForDML.saveOrUpdate(transactionChild);
			}
			
			Contacts contact = null;
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			LoyaltyAutoComm autoComm = getLoyaltyAutoComm(program.getProgramId());
			if(tierUpgd && autoComm != null && autoComm.getTierUpgdEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
					contactsLoyalty.getContact().getContactId() != null){
				contact = findContactById(contactsLoyalty.getContact().getContactId());
				if(contact != null && contact.getEmailId() != null){
					autoCommGen.sendTierUpgdTemplate(autoComm.getTierUpgdEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
							contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(),
							contact.getFirstName(), contact.getContactId(), contactsLoyalty.getLoyaltyId());
				}

			}
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.findByUserId(contactsLoyalty.getUserId());
			if(user.isEnableSMS() && tierUpgd && autoComm != null && autoComm.getTierUpgdSmsTmpltId() != null) {
				Long contactId = null;
				if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
					contactId = contactsLoyalty.getContact().getContactId();
				}
				autoCommGen.sendTierUpgdSMSTemplate(autoComm.getTierUpgdSmsTmpltId(), user, contactId,
						contactsLoyalty.getLoyaltyId(), null);
			}

			//contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);
		}catch(Exception e){
			logger.error("Exception while upgrading tier...", e);
		}
		logger.debug("<<<<<<<<<<<<< completed applyTierUpgradeRule");
		return currTier;
	}//applyTierUpgradeRule
	
	private void applyConversionRules(ContactsLoyalty contactsLoyalty, LoyaltyTransactionChild transaction, LoyaltyProgram program, LoyaltyProgramTier tier){
		logger.info(" Entered into applyConversionRules method >>>");
		String[] differenceArr = null;

		try{
			if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)){
				if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
						&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0 
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints()){
				
					differenceArr = new String[3];
					
					double multipledouble = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
					int multiple = (int)multipledouble;
					double convertedAmount = tier.getConvertToAmount() * multiple;
					double subPoints = multiple * tier.getConvertFromPoints();
					
					differenceArr[0] = ""+convertedAmount;
					differenceArr[1] = ""+subPoints;
					differenceArr[2] = tier.getConvertFromPoints()+" Points -> "+tier.getConvertToAmount();
					
					logger.info("multiple factor = "+multiple);
					logger.info("Conversion amount ="+convertedAmount);
					logger.info("subtract points = "+subPoints);
					
					//update giftcard balance
					if(contactsLoyalty.getGiftcardBalance() == null ) {
						contactsLoyalty.setGiftcardBalance(convertedAmount);
					}
					else{
						contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + convertedAmount);
					}
					if(contactsLoyalty.getTotalGiftcardAmount() == null){
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					}
					else{
						contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount);
					}
					
					transaction.setConversionAmt(convertedAmount);
					//deduct loyalty points
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - subPoints);
					contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? subPoints :
						contactsLoyalty.getTotalLoyaltyRedemption() + subPoints);
					
					logger.info("contactsLoyalty.getGiftcardBalance() = "+contactsLoyalty.getGiftcardBalance());
					transaction.setAmountBalance(contactsLoyalty.getLoyaltyBalance());
					transaction.setPointsBalance(contactsLoyalty.getGiftcardBalance());
					transaction.setGiftBalance(contactsLoyalty.getGiftBalance());
					
					// Deduct points or amount from expiry table
					deductPointsFromExpiryTable(contactsLoyalty.getLoyaltyId(), contactsLoyalty.getUserId(), subPoints, convertedAmount);
					logger.info("After conversion rules...subPoints = "+subPoints+" and convertedAmount = "+convertedAmount);
				}
			}
		
		}catch(Exception e){
			logger.error("Exception while applying auto conversion rules...", e);
		}
		logger.info("Completed applyConversionRules method <<<");
	}
	
	private void deductPointsFromExpiryTable(Long loyaltyId, Long userId, double subPoints, double earnedAmt) throws Exception{
		logger.info(" Entered into deductPointsFromExpiryTable method >>>");
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; //expiryDao.fetchExpPointsTrans(""+membershipNumber, 100, userId);
		Iterator<LoyaltyTransactionExpiry> iterList = null; //expiryList.iterator();
		LoyaltyTransactionExpiry expiry = null;
		long remainingPoints = (long)subPoints;
		
		do{
			expiryList = expiryDao.fetchExpLoyaltyPtsTrans(loyaltyId, 100, userId);
			if(expiryList == null) break;
			iterList = expiryList.iterator();
			
			while(iterList.hasNext()){
				
				logger.info("remainingPoints = "+remainingPoints +" earnedAmt = "+earnedAmt);
				expiry = iterList.next();
				
				if((expiry.getExpiryPoints() == null || expiry.getExpiryPoints() <= 0) && 
						(expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0)){
					logger.info("Wrong entry condition...");
				}
				else if(expiry.getExpiryPoints() < remainingPoints){
					logger.info("subtracted points = "+expiry.getExpiryPoints());
					remainingPoints = remainingPoints - expiry.getExpiryPoints().longValue();
					expiry.setExpiryPoints(0l);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					continue;
				}
				else if(expiry.getExpiryPoints() >= remainingPoints){
					logger.info("subtracted points = "+expiry.getExpiryPoints());
					expiry.setExpiryPoints(expiry.getExpiryPoints() - remainingPoints);
					remainingPoints = 0;
					if(expiry.getExpiryAmount() == null){
						expiry.setExpiryAmount(earnedAmt);
					}
					else{
						expiry.setExpiryAmount(expiry.getExpiryAmount() + earnedAmt);
					}
					//logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ " earnedAmt = "+earnedAmt);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					//logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ " earnedAmt = "+earnedAmt);
					break;
				}
			}
		
		}while(remainingPoints > 0 && expiryList != null);
		logger.info("Completed deductPointsFromExpiryTable method <<<");
	}
	
	private void saveProperties(LoyaltyProgramTier tier){

		try{
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);

			loyalty.setProgramTierId(tier.getTierId());
			saveContactLoyalty(loyalty);
			
			transChild.setTierId(tier.getTierId());
			//loyaltyTransactionChildDao.saveOrUpdate(transChild);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transChild);

		}catch(Exception e){
			logger.error("Exception while saving tier data...",e);
		}
	}

	private void saveContactLoyalty(ContactsLoyalty loyalty) throws Exception{

		ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		loyaltyDao.saveOrUpdate(loyalty);
	}

	private LoyaltyProgramTier findTier(){
		try{
			Double cumulativeAmount = 0.0;
//			Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
			ListIterator<LoyaltyProgramTier> it = new ArrayList(eligibleMap.keySet()).listIterator(eligibleMap.size());
//			LoyaltyProgramTier prevKeyTier = null;
			LoyaltyProgramTier nextKeyTier = null;
			while(it.hasPrevious()){
				nextKeyTier = it.previous();
				logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
				logger.info("-------------currTier::"+tiersList.get(0).getTierType());
				if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
//					prevKeyTier = nextKeyTier;
					return tiersList.get(0);
				}
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				logger.info("contactId = "+loyalty.getContact().getContactId()+" startDate = "+startDate+" endDate = "+endDate);

				/*Object[] cumulativeAmountArr = getCumulativeValue(startDate, endDate);

				cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());*/
				
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				cumulativeAmount = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(user.getUserId(), loyalty.getProgramId(), loyalty.getLoyaltyId(), startDate, endDate));


				if(cumulativeAmount == null || cumulativeAmount <= 0){
					logger.info("cumulative purchase value is empty...");
					continue;
				}
				
				if(cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
					return nextKeyTier;
				}
				
			}
			/*while(it.hasNext()){
				nextKeyTier = it.next();
				logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
				logger.info("-------------tiersList.get(0)::"+tiersList.get(0).getTierType());
				if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
					prevKeyTier = nextKeyTier;
					continue;
				}
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.MONTH, -((LoyaltyProgramTier) eligibleMap.get(nextKeyTier)).getTierUpgradeCumulativeValue().intValue());

				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				logger.info("contactId = "+contactId+" startDate = "+startDate+" endDate = "+endDate);

				Object[] cumulativeAmountArr = getCumulativeValue(startDate, endDate);

				cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());

				if(cumulativeAmount == null || cumulativeAmount <= 0){
					logger.info("cumulative purchase value is empty...");
					continue;
				}
				if(cumulativeAmount > 0 && cumulativeAmount < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
					if(prevKeyTier == null){
						logger.info("selected tier is currTier..."+tiersList.get(0).getTierType());
						return tiersList.get(0);
					}
					logger.info("selected tier..."+prevKeyTier.getTierType());
					return prevKeyTier;
				}
				else if (cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
					logger.info("selected tier..."+nextKeyTier.getTierType());
					return nextKeyTier;
				}
				prevKeyTier = nextKeyTier;
			}*/
			return tiersList.get(0);
		}catch(Exception e){
			logger.error("Excepion in cpv thread ", e);
			return tiersList.get(0);
		}
	}
	
	private Object[] getCumulativeValue(String startDate, String endDate) throws Exception {

		RetailProSalesDao salesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
		Object[] cumulativeAmountArr = salesDao.getCumulativePurchase(user.getUserId(), loyalty.getContact().getContactId(), startDate, endDate);
		return cumulativeAmountArr;
	}
}

