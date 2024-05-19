package org.mq.optculture.business.loyalty;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyIssuanceCPVThread implements Runnable{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private List<LoyaltyProgramTier> tiersList;
	private Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap;
	private Users user;
	private Long contactId;
	private Long loyaltyId;
	private LoyaltyProgram loyaltyProgram;
	private LoyaltyIssuanceRequest issuanceRequest;
	private Double itemExcludedAmount;
	String transactionId;

	public LoyaltyIssuanceCPVThread(Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap, Users user, Long contactId, List<LoyaltyProgramTier> tierList, Long loyaltyId, 
			LoyaltyProgram loyaltyProgram, LoyaltyIssuanceRequest issuanceRequest, Double itemExcludedAmount, String transactionId) {
		this.eligibleMap = eligibleMap;
		this.user = user;
		this.contactId = contactId;
		this.tiersList = tierList;
		this.loyaltyId = loyaltyId;
		this.loyaltyProgram = loyaltyProgram;
		this.issuanceRequest = issuanceRequest;
		this.itemExcludedAmount = itemExcludedAmount;
		this.transactionId = transactionId;
	}

	@Override
	public void run() {

		LoyaltyProgramTier tier =  findTier();
		saveProperties(tier);
		performIssuanceOperation(tier);
	}

	private void performIssuanceOperation(LoyaltyProgramTier tier) {
		try{
			ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyalty contactsLoyalty = loyaltyDao.findAllByLoyaltyId(loyaltyId);

			LoyaltyAutoComm autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());

			//Double purchaseAmountdbl = Double.valueOf(issuanceRequest.getAmount().getEnteredValue());
			Double purchaseAmountdbl = Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.valueOf(issuanceRequest.getAmount().getEnteredValue())));
			Double netPurchaseAmountdbl = purchaseAmountdbl - itemExcludedAmount;

			//long netPurchaseAmount = Math.round(netPurchaseAmountdbl);
			double netPurchaseAmount = netPurchaseAmountdbl;

			logger.info("netPurchaseAmount = "+netPurchaseAmount);

			double earnedValue = 0.0;
			String earntype = tier.getEarnType();
			long pointsDifference = 0;
			double amountDifference = 0.0;

			//		List<Balance> balances = null;
			//		HoldBalance holdBalance = null;
			List<String[]> bonusArrList = null;


			if(tier.getActivationFlag() == 'N'){

				Double fromLtyBalance = contactsLoyalty.getTotalLoyaltyEarned();
				Double fromAmtBalance = contactsLoyalty.getTotalGiftcardAmount();
				if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)){

					Double multipleFactordbl = netPurchaseAmount/tier.getEarnOnSpentAmount();
					long multipleFactor = multipleFactordbl.intValue();
					//earnedValue = (long)Math.floor(tier.getEarnValue() * multipleFactor);
					earnedValue = tier.getEarnValue() * multipleFactor;
				}
				else if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)){

					//earnedValue = (long)Math.floor((tier.getEarnValue() * netPurchaseAmount)/100);
					earnedValue = (tier.getEarnValue() * netPurchaseAmount)/100;
				}
				long earnedPoints = 0;
				double earnedAmount = 0.0;
				if(OCConstants.LOYALTY_TYPE_POINTS.equals(earntype)){
					earnedPoints = (long)Math.floor(earnedValue);
				}
				else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(earntype)){
					//earnedAmount = (new BigDecimal(earnedValue).setScale(2, BigDecimal.ROUND_HALF_DOWN)).doubleValue();
					String result = Utility.truncateUptoTwoDecimal(earnedValue);
					if(result != null)
						earnedAmount = Double.parseDouble(result);
				}
				logger.info("earnedValue = "+earnedValue);


			//	updateContactLoyaltyBalances((double)earnedValue, tier.getEarnType(), contactsLoyalty);
				updateContactLoyaltyBalances((double)earnedAmount, (double)earnedPoints, tier.getEarnType(), contactsLoyalty);

				logger.info("balances After earnedValue updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());

				LoyaltyTransactionChild childTx = createPurchaseTransaction(issuanceRequest, purchaseAmountdbl, contactsLoyalty, earnedAmount, earnedPoints,
						earntype, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE, user.getUserOrganization().getUserOrgId(), 
						""+pointsDifference, ""+amountDifference, transactionId, 
						OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED, null, itemExcludedAmount, 0, null);

				// Expiry transaction
				createExpiryTransaction(contactsLoyalty, earnedPoints, (double)earnedAmount, 
						childTx.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);

				String[] diffArr = applyConversionRules(contactsLoyalty, tier); //0 - amountdiff, 1 - pointsdiff
				logger.info("balances After conversion rules updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());

				String conversionRate = null;
				long convertPoints = 0;
				double convertAmount = 0;
				if(diffArr != null){
					convertAmount = Double.valueOf(diffArr[0].trim());
					convertPoints = Double.valueOf(diffArr[1].trim()).longValue();
					conversionRate = diffArr[2];
				}

				pointsDifference = earnedPoints - convertPoints;
				amountDifference = earnedAmount + convertAmount;

				tier = applyTierUpgradeRule(contactsLoyalty, loyaltyProgram, tier, autoComm, user, issuanceRequest);
				logger.info("balances After tier upgrade --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
				updatePurchaseTransaction(childTx, contactsLoyalty, ""+pointsDifference, ""+amountDifference, conversionRate, convertAmount, tier);
				logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());


				//Event Trigger sending part
				EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
				EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
				eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
				EventTriggerDao eventTriggerDao  = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
				List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(childTx.getUserId(),Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
				if(etList != null) {
					eventTriggerEventsObservable.notifyToObserver(etList, childTx.getTransChildId(), childTx.getTransChildId(), 
							childTx.getUserId(), OCConstants.LOYALTY_ISSUANCE,Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
				}
				
				bonusArrList = calculateThresholdBonus(contactsLoyalty, loyaltyProgram, fromLtyBalance, fromAmtBalance);

				if(bonusArrList != null && bonusArrList.size() > 0){
					LoyaltyProgramTier bonusTxTier = null;
					if(tier != null){
						bonusTxTier = tier;
					}
					LoyaltyTransactionChild transaction=null;
					boolean bonusflag =false;
					for (String[] bonusArr : bonusArrList) {
						double earnedBonus = 0.0;
						long bonusPts = 0;
						double bonusAmt = 0.0;
						String bonustype =null;
						if(OCConstants.LOYALTY_TYPE_POINTS.equals(bonusArr[0].trim())){
							earnedBonus = Double.valueOf(bonusArr[1].trim()).doubleValue();
							bonusPts = (long)earnedBonus;
							bonustype = OCConstants.LOYALTY_TYPE_POINTS;
							 bonusflag =true;
						}
						else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(bonusArr[0].trim())){
							earnedBonus = Double.valueOf(bonusArr[1].trim()).doubleValue();
							//bonusAmt = earnedBonus;
							bonusAmt = (new BigDecimal(earnedBonus).setScale(2, BigDecimal.ROUND_DOWN)).doubleValue();
							bonustype = OCConstants.LOYALTY_TYPE_AMOUNT;
							 bonusflag =true;
						}
						else{
							logger.info("THIS BONUS IS INVALID.... LOOK INTO THIS...");
						}

						//LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, earnedBonus, bonustype, bonusArr[2]);
						LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, bonusAmt, bonusPts, bonustype, bonusArr[2]);
						transaction=childTxbonus;
						createExpiryTransaction(contactsLoyalty, (long)bonusPts, (double)bonusAmt, 
								childTxbonus.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
						
						if(bonusTxTier != null){
							String[] diffBonArr = applyConversionRules(contactsLoyalty, bonusTxTier); //0 - amountdiff, 1 - pointsdiff
							logger.info("balances After conversion rules updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
							
							String conversionBonRate = null;
							long convertBonPoints = 0;
							double convertBonAmount = 0;
							if(diffBonArr != null){
								convertBonAmount = Double.valueOf(diffBonArr[0].trim());
								convertBonPoints = Double.valueOf(diffBonArr[1].trim()).longValue();
								conversionBonRate = diffBonArr[2];
							}
							pointsDifference = bonusPts - convertBonPoints;
							//amountDifference = (long)bonusAmt + convertBonAmount;
							amountDifference = (double)bonusAmt + convertBonAmount;
							
							bonusTxTier = applyTierUpgradeRule(contactsLoyalty, loyaltyProgram, bonusTxTier, autoComm, user, issuanceRequest);
							updatePurchaseTransaction(childTxbonus, contactsLoyalty, ""+pointsDifference, ""+amountDifference, conversionBonRate, convertBonAmount, bonusTxTier);
						}
					}
					if(transaction!=null){
						updateTransactionStatus(transaction, OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED);
					LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
					if(bonusflag && autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
							contactsLoyalty.getContact().getContactId() != null){
						Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
						if(contact != null && contact.getEmailId() != null){
							autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
									contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
									contact.getContactId(), contactsLoyalty.getLoyaltyId(),transaction.getTransChildId(), null);
						}
					}
					if (user.isEnableSMS() && bonusflag && autoComm != null && autoComm.getThreshBonusSmsTmpltId() != null){
						Long contactId = null;
						if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
							contactId = contactsLoyalty.getContact().getContactId();
						}
						autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, contactId,
									contactsLoyalty.getLoyaltyId(),contactsLoyalty.getMobilePhone()!=null?contactsLoyalty.getMobilePhone():null,transaction.getTransChildId(), null);
					   }
				     }
				   }
				}
			else{			
				boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(issuanceRequest.getHeader().getStoreNumber(),tier);
				//APP-3284
				if(isStoreActiveForActivateAfter) {
				
				if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)){

					Double multipleFactordbl = netPurchaseAmount/tier.getEarnOnSpentAmount();
					long multipleFactor = multipleFactordbl.intValue();
					//earnedValue = (long)Math.floor(tier.getEarnValue() * multipleFactor);
					earnedValue = tier.getEarnValue() * multipleFactor;

				}
				else if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)){

					//earnedValue = (long)Math.floor((tier.getEarnValue() * netPurchaseAmount)/100);
					earnedValue = (tier.getEarnValue() * netPurchaseAmount)/100;

				}

				long earnedPoints = 0;
				double earnedAmount = 0.0;
				if(OCConstants.LOYALTY_TYPE_POINTS.equals(earntype)){
					earnedPoints = (long)Math.floor(earnedValue);
				}
				else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(earntype)){
					//earnedAmount = (new BigDecimal(earnedValue).setScale(2, BigDecimal.ROUND_DOWN)).doubleValue();
					String result = Utility.truncateUptoTwoDecimal(earnedValue);
					if(result != null)
						earnedAmount = Double.parseDouble(result);
				}
				logger.info("earnedValue = "+earnedValue);
				//addEarnValueToHoldBalances(contactsLoyalty, tier.getEarnType(), earnedValue);
				addEarnValueToHoldBalances(contactsLoyalty, tier.getEarnType(), earnedAmount, earnedPoints);
				String activationDate = null;
				if(OCConstants.LOYALTY_TYPE_DAY.equals(tier.getPtsActiveDateType().trim())){

					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DAY_OF_MONTH, tier.getPtsActiveDateValue().intValue());
					activationDate = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_YEARTODATE);

				}
				//LoyaltyTransactionChild childTx = createPurchaseTransaction(issuanceRequest, purchaseAmountdbl, contactsLoyalty, earnedValue, 
				LoyaltyTransactionChild childTx = createPurchaseTransaction(issuanceRequest, purchaseAmountdbl, contactsLoyalty, earnedAmount, earnedPoints, 
						earntype, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE, user.getUserOrganization().getUserOrgId(), 
						""+pointsDifference, ""+amountDifference, transactionId, 
						OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_NEW, null, itemExcludedAmount, 0, activationDate);
			}
				else {


					Double fromLtyBalance = contactsLoyalty.getTotalLoyaltyEarned();
					Double fromAmtBalance = contactsLoyalty.getTotalGiftcardAmount();
					if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)){

						Double multipleFactordbl = netPurchaseAmount/tier.getEarnOnSpentAmount();
						long multipleFactor = multipleFactordbl.intValue();
						//earnedValue = (long)Math.floor(tier.getEarnValue() * multipleFactor);
						earnedValue = tier.getEarnValue() * multipleFactor;
					}
					else if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)){

						//earnedValue = (long)Math.floor((tier.getEarnValue() * netPurchaseAmount)/100);
						earnedValue = (tier.getEarnValue() * netPurchaseAmount)/100;
					}
					long earnedPoints = 0;
					double earnedAmount = 0.0;
					if(OCConstants.LOYALTY_TYPE_POINTS.equals(earntype)){
						earnedPoints = (long)Math.floor(earnedValue);
					}
					else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(earntype)){
						//earnedAmount = (new BigDecimal(earnedValue).setScale(2, BigDecimal.ROUND_HALF_DOWN)).doubleValue();
						String result = Utility.truncateUptoTwoDecimal(earnedValue);
						if(result != null)
							earnedAmount = Double.parseDouble(result);
					}
					logger.info("earnedValue = "+earnedValue);


				//	updateContactLoyaltyBalances((double)earnedValue, tier.getEarnType(), contactsLoyalty);
					updateContactLoyaltyBalances((double)earnedAmount, (double)earnedPoints, tier.getEarnType(), contactsLoyalty);

					logger.info("balances After earnedValue updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());

					LoyaltyTransactionChild childTx = createPurchaseTransaction(issuanceRequest, purchaseAmountdbl, contactsLoyalty, earnedAmount, earnedPoints,
							earntype, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE, user.getUserOrganization().getUserOrgId(), 
							""+pointsDifference, ""+amountDifference, transactionId, 
							OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED, null, itemExcludedAmount, 0, null);

					// Expiry transaction
					createExpiryTransaction(contactsLoyalty, earnedPoints, (double)earnedAmount, 
							childTx.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);

					String[] diffArr = applyConversionRules(contactsLoyalty, tier); //0 - amountdiff, 1 - pointsdiff
					logger.info("balances After conversion rules updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());

					String conversionRate = null;
					long convertPoints = 0;
					double convertAmount = 0;
					if(diffArr != null){
						convertAmount = Double.valueOf(diffArr[0].trim());
						convertPoints = Double.valueOf(diffArr[1].trim()).longValue();
						conversionRate = diffArr[2];
					}

					pointsDifference = earnedPoints - convertPoints;
					amountDifference = earnedAmount + convertAmount;

					tier = applyTierUpgradeRule(contactsLoyalty, loyaltyProgram, tier, autoComm, user, issuanceRequest);
					logger.info("balances After tier upgrade --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
					updatePurchaseTransaction(childTx, contactsLoyalty, ""+pointsDifference, ""+amountDifference, conversionRate, convertAmount, tier);
					logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());


					//Event Trigger sending part
					EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
					EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
					eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
					EventTriggerDao eventTriggerDao  = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
					List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(childTx.getUserId(),Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
					if(etList != null) {
						eventTriggerEventsObservable.notifyToObserver(etList, childTx.getTransChildId(), childTx.getTransChildId(), 
								childTx.getUserId(), OCConstants.LOYALTY_ISSUANCE,Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
					}
					
					bonusArrList = calculateThresholdBonus(contactsLoyalty, loyaltyProgram, fromLtyBalance, fromAmtBalance);

					if(bonusArrList != null && bonusArrList.size() > 0){
						LoyaltyProgramTier bonusTxTier = null;
						if(tier != null){
							bonusTxTier = tier;
						}
						LoyaltyTransactionChild transaction=null;
						boolean bonusflag =false;
						for (String[] bonusArr : bonusArrList) {
							double earnedBonus = 0.0;
							long bonusPts = 0;
							double bonusAmt = 0.0;
							String bonustype =null;
							if(OCConstants.LOYALTY_TYPE_POINTS.equals(bonusArr[0].trim())){
								earnedBonus = Double.valueOf(bonusArr[1].trim()).doubleValue();
								bonusPts = (long)earnedBonus;
								bonustype = OCConstants.LOYALTY_TYPE_POINTS;
								 bonusflag =true;
							}
							else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(bonusArr[0].trim())){
								earnedBonus = Double.valueOf(bonusArr[1].trim()).doubleValue();
								//bonusAmt = earnedBonus;
								bonusAmt = (new BigDecimal(earnedBonus).setScale(2, BigDecimal.ROUND_DOWN)).doubleValue();
								bonustype = OCConstants.LOYALTY_TYPE_AMOUNT;
								 bonusflag =true;
							}
							else{
								logger.info("THIS BONUS IS INVALID.... LOOK INTO THIS...");
							}

							//LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, earnedBonus, bonustype, bonusArr[2]);
							LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, bonusAmt, bonusPts, bonustype, bonusArr[2]);
							transaction=childTxbonus;
							createExpiryTransaction(contactsLoyalty, (long)bonusPts, (double)bonusAmt, 
									childTxbonus.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
							
							if(bonusTxTier != null){
								String[] diffBonArr = applyConversionRules(contactsLoyalty, bonusTxTier); //0 - amountdiff, 1 - pointsdiff
								logger.info("balances After conversion rules updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
								
								String conversionBonRate = null;
								long convertBonPoints = 0;
								double convertBonAmount = 0;
								if(diffBonArr != null){
									convertBonAmount = Double.valueOf(diffBonArr[0].trim());
									convertBonPoints = Double.valueOf(diffBonArr[1].trim()).longValue();
									conversionBonRate = diffBonArr[2];
								}
								pointsDifference = bonusPts - convertBonPoints;
								//amountDifference = (long)bonusAmt + convertBonAmount;
								amountDifference = (double)bonusAmt + convertBonAmount;
								
								bonusTxTier = applyTierUpgradeRule(contactsLoyalty, loyaltyProgram, bonusTxTier, autoComm, user, issuanceRequest);
								updatePurchaseTransaction(childTxbonus, contactsLoyalty, ""+pointsDifference, ""+amountDifference, conversionBonRate, convertBonAmount, bonusTxTier);
							}
						}
						if(transaction!=null){
							updateTransactionStatus(transaction, OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED);
						LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
						if(bonusflag && autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
								contactsLoyalty.getContact().getContactId() != null){
							Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
							if(contact != null && contact.getEmailId() != null){
								autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
										contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
										contact.getContactId(), contactsLoyalty.getLoyaltyId(),transaction.getTransChildId(), null);
							}
						}
						if (user.isEnableSMS() && bonusflag && autoComm != null && autoComm.getThreshBonusSmsTmpltId() != null){
							Long contactId = null;
							if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
								contactId = contactsLoyalty.getContact().getContactId();
							}
							autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, contactId,
										contactsLoyalty.getLoyaltyId(),contactsLoyalty.getMobilePhone()!=null?contactsLoyalty.getMobilePhone():null,transaction.getTransChildId(), null);
						   }
					     }
					   }
					
				}
			}	
			saveContactLoyalty(contactsLoyalty);
		}catch(Exception e){

		}
	}
	 private void updateTransactionStatus(LoyaltyTransactionChild transaction, String status) throws Exception {
			LoyaltyTransactionChildDaoForDML LoyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			transaction.setEarnStatus(status);
			LoyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
		}
	private void addEarnValueToHoldBalances(ContactsLoyalty contactsLoyalty, String earnType, double earnedAmount, double earnedPoints){

		if(earnType != null && earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){

			if(contactsLoyalty.getHoldPointsBalance() == null ) {
				//contactsLoyalty.setHoldPointsBalance(earnValue);
				contactsLoyalty.setHoldPointsBalance(earnedPoints);
			}
			else{
				//contactsLoyalty.setHoldPointsBalance(contactsLoyalty.getHoldPointsBalance()+earnValue);
				contactsLoyalty.setHoldPointsBalance(contactsLoyalty.getHoldPointsBalance()+earnedPoints);
			}

		}
		else if (earnType != null && earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
			if(contactsLoyalty.getHoldAmountBalance() == null ) {
				//contactsLoyalty.setHoldAmountBalance(earnValue);
				contactsLoyalty.setHoldAmountBalance(earnedAmount);
			}
			else{
				//contactsLoyalty.setHoldAmountBalance(contactsLoyalty.getHoldAmountBalance()+earnValue);
				//contactsLoyalty.setHoldAmountBalance((new BigDecimal(contactsLoyalty.getHoldAmountBalance()+earnedAmount)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
				contactsLoyalty.setHoldAmountBalance((new BigDecimal(contactsLoyalty.getHoldAmountBalance()+earnedAmount)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
		}

	}



	private LoyaltyTransactionChild createBonusTransaction(ContactsLoyalty loyalty,
			double bonusAmt, double bonusPts, String earnType, String bonusRate){

		LoyaltyTransactionChild transaction = null;
		try{

			transaction = new LoyaltyTransactionChild();
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());

			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setEarnType(earnType);
			if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
				//transaction.setEarnedPoints((double)earnedValue);
				transaction.setEarnedPoints((double)bonusPts);
				transaction.setEnteredAmount((double)bonusPts);
			}
			else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
				//transaction.setEarnedAmount((double)earnedValue);
				transaction.setEarnedAmount((double)bonusAmt);
				transaction.setEnteredAmount((double)bonusAmt);
			}
			//transaction.setEnteredAmount((double)earnedValue);
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format=new SimpleDateFormat(MyCalendar.FORMAT_YEARTODATE);
			Date date = cal.getTime();             
			String Strdate = format.format(date);
			transaction.setValueActivationDate(format.parse(Strdate));
			transaction.setEarnStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_BONUS);
			transaction.setDescription("Threshold Bonus : "+bonusRate);
			//transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_AUTO);
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

	private void updatePurchaseTransaction(LoyaltyTransactionChild transaction, ContactsLoyalty loyalty,
			String ptsDiff, String amtDiff, String conversionRate, double convertAmt,LoyaltyProgramTier tier){

		try{

			transaction.setAmountDifference(amtDiff);
			transaction.setPointsDifference(ptsDiff);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setDescription(conversionRate);
			transaction.setConversionAmt(convertAmt);
			transaction.setTierId(tier.getTierId());

			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);

		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
	}

	private LoyaltyProgramTier applyTierUpgradeRule(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, LoyaltyProgramTier currTier, 
			LoyaltyAutoComm autoComm, Users user, LoyaltyIssuanceRequest issuanceRequest){

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
			}
			
			
			// Send auto communication email
			// LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(program.getProgramId());
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			if(tierUpgd && autoComm != null && autoComm.getTierUpgdEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
					contactsLoyalty.getContact().getContactId() != null){
				Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
				if(contact != null && contact.getEmailId() != null){
					autoCommGen.sendTierUpgdTemplate(autoComm.getTierUpgdEmailTmpltId(), ""+contactsLoyalty.getCardNumber(), 
							contactsLoyalty.getCardPin(), user, contact.getEmailId(), contact.getFirstName(),
							contact.getContactId(), contactsLoyalty.getLoyaltyId());
				}
			}
			if(user.isEnableSMS() && tierUpgd && autoComm != null && autoComm.getTierUpgdSmsTmpltId() != null && contactsLoyalty.getMobilePhone() != null){
				Long contactId = null;
				if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
					contactId = contactsLoyalty.getContact().getContactId();
				}
				autoCommGen.sendTierUpgdSMSTemplate(autoComm.getTierUpgdSmsTmpltId(), user, 
						contactId, contactsLoyalty.getLoyaltyId(), contactsLoyalty.getMobilePhone());
			}


			
		}catch(Exception e){
			logger.error("Exception while upgrading tier...", e);
		}
		return currTier;
	}

	private Contacts findContactById(Long cid) throws Exception {

		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactsDao.findById(cid);
	}

	private String[] applyConversionRules(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier){

		String[] differenceArr = null;

		try{

			if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)){

				if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
						&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0 
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints()){

					differenceArr = new String[3];

					double multipledouble = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
					int multiple = (int)multipledouble;
					//double convertedAmount = tier.getConvertToAmount() * multiple;
					double convertedAmount = 0.0;
					String result = Utility.truncateUptoTwoDecimal(tier.getConvertToAmount() * multiple);
					if(result != null)
						convertedAmount  = Double.parseDouble(result);
					//double convertedAmount = new BigDecimal(tier.getConvertToAmount() * multiple).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
					double subPoints = multiple * tier.getConvertFromPoints();

					differenceArr[0] = ""+convertedAmount;
					differenceArr[1] = ""+subPoints;
					//differenceArr[2] = tier.getConvertFromPoints().intValue()+" Points -> "+tier.getConvertToAmount().intValue();
					differenceArr[2] = tier.getConvertFromPoints().intValue()+" Points -> "+tier.getConvertToAmount().doubleValue();

					logger.info("multiple factor = "+multiple);
					logger.info("Conversion amount ="+convertedAmount);
					logger.info("subtract points = "+subPoints);


					//update giftcard balance
					if(contactsLoyalty.getGiftcardBalance() == null ) {
						contactsLoyalty.setGiftcardBalance(convertedAmount);
					}
					else{
						//contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + convertedAmount);
						contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					if(contactsLoyalty.getTotalGiftcardAmount() == null){
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					}
					else{
						//contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount);
						contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}

					//deduct loyalty points
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - subPoints);
					contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? subPoints :
						contactsLoyalty.getTotalLoyaltyRedemption() + subPoints);

					logger.info("contactsLoyalty.getGiftcardBalance() = "+contactsLoyalty.getGiftcardBalance());

					deductPointsFromExpiryTable(contactsLoyalty, subPoints, convertedAmount);
				}
			}

		}catch(Exception e){
			logger.error("Exception while applying auto conversion rules...", e);
			return null;
		}
		return differenceArr;
	}

	private void deductPointsFromExpiryTable(ContactsLoyalty contactsLoyalty, double subPoints, double earnedAmt) throws Exception{

		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; //expiryDao.fetchExpPointsTrans(""+membershipNumber, 100, userId);
		Iterator<LoyaltyTransactionExpiry> iterList = null; //expiryList.iterator();
		LoyaltyTransactionExpiry expiry = null;
		long remainingPoints = (long)subPoints;

		do{

			expiryList = expiryDao.fetchExpLoyaltyPtsTrans(contactsLoyalty.getLoyaltyId(), 100, contactsLoyalty.getUserId());
			//logger.info("expiryList size = "+expiryList.size());
			if(expiryList == null) break;
			iterList = expiryList.iterator();

			while(iterList.hasNext()){

				logger.info("remainingPoints = "+remainingPoints +" earnedAmt = "+earnedAmt);
				expiry = iterList.next();

				//logger.info("expiry points= "+expiry.getExpiryPoints()+" expiry amount = "+expiry.getExpiryAmount());

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


	}


	private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty,
			Long expiryPoints, Double expiryAmount, Long transChildId, String rewardFlag){

		LoyaltyTransactionExpiry transaction = null;
		try{

			transaction = new LoyaltyTransactionExpiry();
			transaction.setTransChildId(transChildId);
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setExpiryPoints(expiryPoints);
			transaction.setExpiryAmount(expiryAmount);
			//transaction.setExpiryStatus(OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_NEW);
			transaction.setRewardFlag(rewardFlag);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());

			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			//loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
			loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);

		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		return transaction;
	}


	private LoyaltyTransactionChild createPurchaseTransaction(LoyaltyIssuanceRequest issuanceRequest, Double purchaseAmount, ContactsLoyalty loyalty,
			double earnedAmount, double earnedPoints, String earnType,	 String entAmountType, Long orgId, String ptsDiff, String amtDiff, 
			String transactionId, String earnStatus, String conversionRate, double itemExcludedAmt, double convertAmt, String activationDate){

		LoyaltyTransactionChild transaction = null;
		try{

			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(transactionId));
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());

			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setAmountDifference(amtDiff);
			transaction.setPointsDifference(ptsDiff);
			transaction.setEarnType(earnType);
			if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
				//transaction.setEarnedPoints((double)earnedValue);
				transaction.setEarnedPoints((double)earnedPoints);
			}
			else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
				//transaction.setEarnedAmount((double)earnedValue);
				transaction.setEarnedAmount((double)earnedAmount);
			}

			transaction.setEarnStatus(earnStatus);
			transaction.setEnteredAmount((double)purchaseAmount);
			transaction.setExcludedAmount(itemExcludedAmt);
			transaction.setEnteredAmountType(entAmountType);
			transaction.setOrgId(orgId);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);
			transaction.setStoreNumber(issuanceRequest.getHeader().getStoreNumber());
			transaction.setSubsidiaryNumber(issuanceRequest.getHeader().getSubsidiaryNumber() != null && !issuanceRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? issuanceRequest.getHeader().getSubsidiaryNumber().trim() : null);
			transaction.setReceiptNumber(issuanceRequest.getHeader().getReceiptNumber() != null && !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty() ? issuanceRequest.getHeader().getReceiptNumber() : null);
			
			transaction.setEmployeeId(issuanceRequest.getHeader().getEmployeeId()!=null && !issuanceRequest.getHeader().getEmployeeId().trim().isEmpty() ? issuanceRequest.getHeader().getEmployeeId().trim():null);
			transaction.setTerminalId(issuanceRequest.getHeader().getTerminalId()!=null && !issuanceRequest.getHeader().getTerminalId().trim().isEmpty() ? issuanceRequest.getHeader().getTerminalId().trim():null);
			transaction.setDocSID(issuanceRequest.getHeader().getDocSID());
			transaction.setDescription(conversionRate);
			transaction.setConversionAmt(convertAmt);
			if(activationDate != null){
				if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
					//transaction.setHoldPoints((double)earnedValue);
					transaction.setHoldPoints((double)earnedPoints);
				}
				else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
					//transaction.setHoldAmount((double)earnedValue);
					transaction.setHoldAmount((double)earnedAmount);
				}
				transaction.setValueActivationDate(new SimpleDateFormat("yyyy-MM-dd").parse(activationDate));
			}
			//transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE);
			transaction.setSourceType(issuanceRequest.getHeader().getSourceType());
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
//			transaction.setEventTriggStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
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

	private List<String[]> calculateThresholdBonus(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, Double fromLtyBalance, Double fromAmtBalance){
		try{

			LoyaltyThresholdBonusDao loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			List<LoyaltyThresholdBonus> threshBonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(program.getProgramId(), 'N' );
			List<LoyaltyThresholdBonus> pointsBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> amountBonusList = new ArrayList<LoyaltyThresholdBonus>();
			fromAmtBalance = fromAmtBalance == null ? 0.0 : fromAmtBalance;
			fromLtyBalance = fromLtyBalance == null ? 0.0 : fromLtyBalance;

			List<String[]> bonusArrList = null; //new String[2];
			String[] bonusArr = null; //new String[2];

			if(threshBonusList == null) return bonusArrList;

			for(LoyaltyThresholdBonus bonus : threshBonusList){
				if(bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_POINTS)){
					pointsBonusList.add(bonus);
				}
				else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
					amountBonusList.add(bonus);
				}
			}

			List<LoyaltyThresholdBonus> matchedBonusList = new ArrayList<LoyaltyThresholdBonus>();

			if(pointsBonusList.isEmpty() && amountBonusList.isEmpty()) return bonusArrList;

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

			if(matchedBonusList != null && matchedBonusList.size() > 0){
				bonusArrList = new ArrayList<String[]>();
				for (LoyaltyThresholdBonus matchedBonus : matchedBonusList) {
					bonusArr = new String[3];
					bonusArr[0] = matchedBonus.getExtraBonusType();
					bonusArr[1] = ""+matchedBonus.getExtraBonusValue();
					bonusArr[2] = ""+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
							" --> "+matchedBonus.getExtraBonusValue()+" "+matchedBonus.getExtraBonusType();
					if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getExtraBonusType())){
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

					}
					else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getExtraBonusType())){
						
						//double bonusAmount = (new BigDecimal(matchedBonus.getExtraBonusValue()).setScale(2, BigDecimal.ROUND_DOWN)).doubleValue();
						double bonusAmount = 0.0;
						String result = Utility.truncateUptoTwoDecimal(matchedBonus.getExtraBonusValue());
						if(result != null)
							bonusAmount = Double.parseDouble(result);
						if(contactsLoyalty.getGiftcardBalance() == null ) {
							//contactsLoyalty.setGiftcardBalance(matchedBonus.getExtraBonusValue());
							contactsLoyalty.setGiftcardBalance(bonusAmount);
						}
						else{
							//contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + matchedBonus.getExtraBonusValue());
							contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + bonusAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						}
						if(contactsLoyalty.getTotalGiftcardAmount() == null){
							//contactsLoyalty.setTotalGiftcardAmount(matchedBonus.getExtraBonusValue());
							contactsLoyalty.setTotalGiftcardAmount(bonusAmount);
						}
						else{
							//contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + matchedBonus.getExtraBonusValue());
							contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + bonusAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						}
					}
					bonusArrList.add(bonusArr);
				}
			}

			/*if(matchedPointsBonus != null && OCConstants.LOYALTY_TYPE_POINTS.equals(matchedPointsBonus.getExtraBonusType())){

				bonusArr = new String[3];
				bonusArr[0] = OCConstants.LOYALTY_TYPE_POINTS;
				bonusArr[1] = ""+matchedPointsBonus.getExtraBonusValue();
				bonusArr[2] = ""+matchedPointsBonus.getEarnedLevelValue()+" "+matchedPointsBonus.getEarnedLevelType()+
						" --> "+matchedPointsBonus.getExtraBonusValue()+" "+OCConstants.LOYALTY_TYPE_POINTS;

					if(contactsLoyalty.getLoyaltyBalance() == null ) {
						contactsLoyalty.setLoyaltyBalance(matchedPointsBonus.getExtraBonusValue());
					}
					else{
						contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() + matchedPointsBonus.getExtraBonusValue());
					}
					if(contactsLoyalty.getTotalLoyaltyEarned() == null){
						contactsLoyalty.setTotalLoyaltyEarned(matchedPointsBonus.getExtraBonusValue());
					}
					else{
						contactsLoyalty.setTotalLoyaltyEarned(contactsLoyalty.getTotalLoyaltyEarned() + matchedPointsBonus.getExtraBonusValue());
					}
			}
			else if(matchedAmountBonus != null && matchedAmountBonus.getExtraBonusType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){

				bonusArr = new String[3];
				bonusArr[0] = OCConstants.LOYALTY_TYPE_AMOUNT;
				bonusArr[1] = ""+matchedAmountBonus.getExtraBonusValue();
				bonusArr[2] = ""+matchedAmountBonus.getEarnedLevelValue()+" "+matchedAmountBonus.getEarnedLevelType()+
						" --> "+matchedAmountBonus.getExtraBonusValue()+" "+OCConstants.LOYALTY_TYPE_AMOUNT;

				if(contactsLoyalty.getGiftcardBalance() == null ) {
					contactsLoyalty.setGiftcardBalance(matchedPointsBonus.getExtraBonusValue());
				}
				else{
					contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + matchedAmountBonus.getExtraBonusValue());
				}
				if(contactsLoyalty.getTotalGiftcardAmount() == null){
					contactsLoyalty.setTotalGiftcardAmount(matchedAmountBonus.getExtraBonusValue());
				}
				else{
					contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + matchedAmountBonus.getExtraBonusValue());
				}
			}*/

			return bonusArrList;
		}catch(Exception e){
			logger.error("Exception in update threshold bonus...", e);
			return null;
		}

	}

	private LoyaltyAutoComm getLoyaltyAutoComm(Long programId) throws Exception {
		LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
		return autoCommDao.findById(programId);
	}

	//private void updateContactLoyaltyBalances(Double earned, String valueCode, ContactsLoyalty contactsLoyalty) throws Exception {
	private void updateContactLoyaltyBalances(Double earnedAmount, Double earnedPoints, String valueCode, ContactsLoyalty contactsLoyalty) throws Exception {

		if(valueCode != null && valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)){

			if(contactsLoyalty.getLoyaltyBalance() == null ) {
				//contactsLoyalty.setLoyaltyBalance(earned);
				contactsLoyalty.setLoyaltyBalance(earnedPoints);
			}
			else{
				//contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() + earned);
				contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() + earnedPoints);
			}
			if(contactsLoyalty.getTotalLoyaltyEarned() == null){
				//contactsLoyalty.setTotalLoyaltyEarned(earned);
				contactsLoyalty.setTotalLoyaltyEarned(earnedPoints);
			}
			else{
				//contactsLoyalty.setTotalLoyaltyEarned(contactsLoyalty.getTotalLoyaltyEarned() + earned);
				contactsLoyalty.setTotalLoyaltyEarned(contactsLoyalty.getTotalLoyaltyEarned() + earnedPoints);
			}

		}
		else if(valueCode != null && valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)){
			if(contactsLoyalty.getGiftcardBalance() == null ) {
				//contactsLoyalty.setGiftcardBalance(earned);
				contactsLoyalty.setGiftcardBalance(earnedAmount);
			}
			else{
				//contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + earned);
				contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + earnedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			if(contactsLoyalty.getTotalGiftcardAmount() == null){
				//contactsLoyalty.setTotalGiftcardAmount(earned);
				contactsLoyalty.setTotalGiftcardAmount(earnedAmount);
			}
			else{
				//contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + earned);
				contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + earnedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}

		}		

	}

	private void saveProperties(LoyaltyProgramTier tier){

		try{
			ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyalty loyalty = loyaltyDao.findAllByLoyaltyId(loyaltyId);

			loyalty.setProgramTierId(tier.getTierId());
			saveContactLoyalty(loyalty);

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
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyalty contactsLoyalty = null;
			contactsLoyalty = contactsLoyaltyDao.findByContactId(user.getUserId(), contactId);
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
				logger.info("contactId = "+contactId+" startDate = "+startDate+" endDate = "+endDate);

				/*Object[] cumulativeAmountArr = getCumulativeValue(startDate, endDate);

				cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());*/
				
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				cumulativeAmount = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));

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
		Object[] cumulativeAmountArr = salesDao.getCumulativePurchase(user.getUserId(), contactId, startDate, endDate);
		return cumulativeAmountArr;
	}
}

