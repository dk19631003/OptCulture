package org.mq.optculture.timer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimerTask;

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
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyAutoCommGenerator;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

/**
 * 
 * @author Venkata Rathnam D
 * This timer activates earned points/amount and updates tier .
 */
public class EarnAndRedeemRules extends TimerTask{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public void run(){
		logger.info(">>> Started earn rules timer now >>>");
		
		try{
			List<LoyaltyTransactionChild> listOfTrans = fetchActiveTrans();
			if(listOfTrans == null || listOfTrans.isEmpty()){
				logger.info("Transactions list is empty...");
				return;
			}
			
			Map<Long, LoyaltyProgram> listOfPrograms = fetchProgramsList(listOfTrans); 
//			Map<Long, LoyaltyProgramTier> listOfTiers = fetchTiersList(listOfTrans);
			
			ContactsLoyalty contactsLoyalty = null;
			for(LoyaltyTransactionChild transaction : listOfTrans){
				logger.info("Processing points activation..."+transaction.getMembershipNumber());
				updateTransactionStatus(transaction, OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_WORKING);
				contactsLoyalty = getContactsLoyalty(transaction);
				if(contactsLoyalty == null){
					logger.info("loyalty not found...");
					updateTransactionStatus(transaction, OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_SUSPENDED);
					continue;
				}
				if((transaction.getHoldPoints() == null || transaction.getHoldPoints() <= 0) && 
						(transaction.getHoldAmount() == null || transaction.getHoldAmount() <= 0)){
					logger.info(" hold points/amount is less than zero...");
					updateTransactionStatus(transaction, OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED);
					continue;
				}
				Long netEarnedPoints = transaction.getHoldPoints() != null ? transaction.getHoldPoints().longValue() : 0l;;
				double netEarnedAmount = transaction.getHoldAmount() != null ? transaction.getHoldAmount() : 0.0;
				
				Double fromLtyBalance = contactsLoyalty.getTotalLoyaltyEarned();
				Double fromAmtBalance = contactsLoyalty.getTotalGiftcardAmount();
				updateActivePoints(contactsLoyalty, transaction);
				
				LoyaltyProgramTier tier = null;
				if(contactsLoyalty.getProgramTierId() == null) {
					tier = findTier(contactsLoyalty);
					if (tier == null) {
						updateThresholdBonus(contactsLoyalty, listOfPrograms.get(transaction.getProgramId()), fromLtyBalance, fromAmtBalance, null);
						updateTransactionStatus(transaction, OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED);
						saveContactsLoyalty(contactsLoyalty);
						continue;
					}
					else {
						contactsLoyalty.setProgramTierId(tier.getTierId());
					}
				}
				else{
					tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				}
				// Expiry transaction
				createExpiryTransaction(contactsLoyalty, netEarnedPoints, (double) netEarnedAmount,transaction.getOrgId(), transaction.getTransChildId(),
						 null);

				
				applyConversionRules(contactsLoyalty, transaction, listOfPrograms.get(transaction.getProgramId()), tier);
				tier = applyTierUpgradeRule(contactsLoyalty, transaction, listOfPrograms.get(transaction.getProgramId()), tier);
				
				updateThresholdBonus(contactsLoyalty, listOfPrograms.get(transaction.getProgramId()), fromLtyBalance, fromAmtBalance, tier);

				updateTransactionStatus(transaction, OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED);
				saveContactsLoyalty(contactsLoyalty);
			}
		}catch(Exception e){
			logger.error("Exception in :: ", e.getMessage());
		}
		logger.info(">>> Completed earn rules timer >>>");
	}
	
	private LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getTierById(tierId);
		
	}
	
	private LoyaltyProgramTier findTier(ContactsLoyalty contactsLoyalty) throws Exception {

		LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

		List<LoyaltyProgramTier> tiersList = loyaltyProgramTierDao.fetchTiersByProgramId(contactsLoyalty.getProgramId());
		if (tiersList == null || tiersList.size() <= 0) {
			logger.info("Tiers list is empty...");
			return null;
		}
		else if (tiersList.size() >= 1) {
			Collections.sort(tiersList, new Comparator<LoyaltyProgramTier>() {
				@Override
				public int compare(LoyaltyProgramTier o1, LoyaltyProgramTier o2) {

					int num1 = Integer.valueOf(o1.getTierType().substring(5)).intValue();
					int num2 = Integer.valueOf(o2.getTierType().substring(5)).intValue();
					if(num1 < num2){
						return -1;
					}
					else if(num1 == num2){
						return 0;
					}
					else{
						return 1;
					}
				}
			});
		}

		for(LoyaltyProgramTier tier : tiersList) {//testing purpose
			logger.info("tier level : "+tier.getTierType());
		}

		if(!OCConstants.LOYALTY_PROGRAM_TIER1.equals(tiersList.get(0).getTierType())){// if tier 1 not exist return null
			logger.info("selected tier...null...tier1 not found");
			return null;
		}

		//Prepare eligible tiers map
		Iterator<LoyaltyProgramTier> iterTier = tiersList.iterator();
		Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
		LoyaltyProgramTier prevtier = null;
		LoyaltyProgramTier nexttier = null;

		while(iterTier.hasNext()){
			nexttier = iterTier.next();
			if(OCConstants.LOYALTY_PROGRAM_TIER1.equals(nexttier.getTierType())){
				eligibleMap.put(nexttier, null);
			}
			else{
				if((Integer.valueOf(prevtier.getTierType().substring(5))+1) 
						== Integer.valueOf(nexttier.getTierType().substring(5)) && prevtier.getTierUpgdConstraintValue() != null){
					eligibleMap.put(nexttier, prevtier);
					logger.info("eligible tier ="+nexttier.getTierType()+" upgdconstrant value = "+prevtier.getTierUpgdConstraintValue());
				}
			}
			prevtier = nexttier;
		}

		if(OCConstants.LOYALTY_LIFETIME_POINTS.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_POINTS);
			if(contactsLoyalty == null) {
				return tiersList.get(0);
			}
			else {

				Double totLoyaltyPointsValue = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.00 : contactsLoyalty.getTotalLoyaltyEarned();
				logger.info("totLoyaltyPointsValue value = "+totLoyaltyPointsValue);

				if(totLoyaltyPointsValue == null || totLoyaltyPointsValue <= 0){
					logger.info("totLoyaltyPointsValue value is empty...");
					return tiersList.get(0);
				}
				else{
					Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
					LoyaltyProgramTier prevKeyTier = null;
					LoyaltyProgramTier nextKeyTier = null;
					while(it.hasNext()){
						nextKeyTier = it.next();
						logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
						logger.info("-------------currTier::"+tiersList.get(0).getTierType());
						if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
							prevKeyTier = nextKeyTier;
							continue;
						}
						if(totLoyaltyPointsValue > 0 && totLoyaltyPointsValue < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
							if(prevKeyTier == null){
								logger.info("selected tier is currTier..."+tiersList.get(0).getTierType());
								return tiersList.get(0);
							}
							logger.info("selected tier..."+prevKeyTier.getTierType());
							return prevKeyTier;
						}
						else if (totLoyaltyPointsValue > 0 && totLoyaltyPointsValue >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
							logger.info("selected tier..."+nextKeyTier.getTierType());
							return nextKeyTier;
						}
						prevKeyTier = nextKeyTier;
					}
					return tiersList.get(0);
				}//else
			}
		}
		else if(contactsLoyalty.getContact() == null || contactsLoyalty.getContact().getContactId() == null){
			logger.info("contactId is null and selected tier..."+tiersList.get(0).getTierType());
			return tiersList.get(0);
		}
		else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);
			
			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);				

			//List<Map<String, Object>> contactPurcahseList = contactsDao.findContactPurchaseDetails(contactsLoyalty.getUserId(), contactsLoyalty.getContact().getContactId());
			Double totPurchaseValue = null;
			/*if(contactPurcahseList != null && contactPurcahseList.size() == 1) {
				for (Map<String, Object> eachMap : contactPurcahseList) {
					if(eachMap.containsKey("tot_purchase_amt")){
						totPurchaseValue = Double.valueOf(eachMap.get("tot_purchase_amt") != null ? eachMap.get("tot_purchase_amt").toString() : "0.00");
						logger.info("purchase value = "+totPurchaseValue);
					}
				}
			}

			if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){*/
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);//contactsLoyalty.getLifeTimePurchaseValue();//Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
			logger.info("purchase value = "+totPurchaseValue);

			if(totPurchaseValue == null || totPurchaseValue <= 0){
				logger.info("purchase value is empty...");
				return tiersList.get(0);
			}
			else{

				Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
				LoyaltyProgramTier prevKeyTier = null;
				LoyaltyProgramTier nextKeyTier = null;
				while(it.hasNext()){
					nextKeyTier = it.next();
					logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
					logger.info("-------------tiersList.get(0)::"+tiersList.get(0).getTierType());
					if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
						prevKeyTier = nextKeyTier;
						continue;
					}
					if(totPurchaseValue > 0 && totPurchaseValue < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
						if(prevKeyTier == null){
							logger.info("selected tier is currTier..."+tiersList.get(0).getTierType());
							return tiersList.get(0);
						}
						logger.info("selected tier..."+prevKeyTier.getTierType());
						return prevKeyTier;
					}
					else if (totPurchaseValue > 0 && totPurchaseValue >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
						logger.info("selected tier..."+nextKeyTier.getTierType());
						return nextKeyTier;
					}
					prevKeyTier = nextKeyTier;
				}
				return tiersList.get(0);
			}//else
		}
		else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			try{
				Double cumulativeAmount = 0.0;
//				Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
				ListIterator<LoyaltyProgramTier> it = new ArrayList(eligibleMap.keySet()).listIterator(eligibleMap.size());
//				LoyaltyProgramTier prevKeyTier = null;
				LoyaltyProgramTier nextKeyTier = null;
				while(it.hasPrevious()){
					nextKeyTier = it.previous();
					logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
					logger.info("-------------currTier::"+tiersList.get(0).getTierType());
					if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
//						prevKeyTier = nextKeyTier;
						return tiersList.get(0);
					}
					Calendar startCal = Calendar.getInstance();
					Calendar endCal = Calendar.getInstance();
					endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

					String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					logger.info("contactId = "+contactsLoyalty.getContact().getContactId()+" startDate = "+startDate+" endDate = "+endDate);

					/*RetailProSalesDao salesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
					Object[] cumulativeAmountArr = salesDao.getCumulativePurchase(contactsLoyalty.getUserId(), contactsLoyalty.getContact().getContactId(), startDate, endDate);

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
		else{
			return null;
		}
	}
	
	private List<LoyaltyTransactionChild> fetchActiveTrans() throws LoyaltyProgramException {
		
		List<LoyaltyTransactionChild> listOfTrans =null;
		try{
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			//String transType = OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE;
			Calendar cal = Calendar.getInstance();
			String date = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_YEARTODATE);
			String earnStatus = OCConstants.LOYALTY_TRANSACTION_STATUS_NEW;
			listOfTrans = loyaltyTransactionChildDao.fetchCurrentInActiveTrans(earnStatus,date, null);
			if(listOfTrans != null && listOfTrans.size() > 0){
				return listOfTrans;
			}
		}catch(Exception e){
			logger.error("Exception in dao service...");
			//throw new LoyaltyProgramException("fetch current active trans failed.");
		}
		return listOfTrans;
	}
	
	private Map<Long, LoyaltyProgram> fetchProgramsList(List<LoyaltyTransactionChild> transList) {
		Map<Long, LoyaltyProgram> programMap = null;
		String programIdStr = null;
		
		for(LoyaltyTransactionChild programTrans : transList){
			if(programIdStr == null){
				programIdStr = ""+programTrans.getProgramId();
			}
			else{
				programIdStr += ","+programTrans.getProgramId();
			}
		}
		
		try{
			
			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			List<LoyaltyProgram> listOfPrograms = loyaltyProgramDao.fetchProgramsByProgramIdStr(programIdStr);
			
			if(listOfPrograms != null && listOfPrograms.size() > 0){
				programMap = new HashMap<Long, LoyaltyProgram>();
				
				for(LoyaltyProgram program : listOfPrograms){
					programMap.put(program.getProgramId(), program);
				}
			}
			
		}catch(Exception e){
			logger.error("Exception in fetching programs...", e);
		}
		return programMap;
	}
	
	private Map<Long, LoyaltyProgramTier> fetchTiersList(List<LoyaltyTransactionChild> transList) {
		
		Map<Long, LoyaltyProgramTier> tierMap = null;
		String tierIdStr = null;
		
		for(LoyaltyTransactionChild programTrans : transList){
			if(tierIdStr == null){
				tierIdStr = ""+programTrans.getTierId();
			}
			else{
				tierIdStr += ","+programTrans.getTierId();
			}
		}
		
		try{
			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			List<LoyaltyProgramTier> listOfTiers = loyaltyProgramTierDao.fetchTiersBytierIdStr(tierIdStr);
			if(listOfTiers != null && listOfTiers.size() > 0){
				tierMap = new HashMap<Long, LoyaltyProgramTier>();
				
				for(LoyaltyProgramTier tier : listOfTiers){
					tierMap.put(tier.getTierId(), tier);
				}
			}
		}catch(Exception e){
			logger.error("Exception in fetching tiers...",e);
		}
		return tierMap;
	}
	
	private ContactsLoyalty getContactsLoyalty(LoyaltyTransactionChild transaction){
		
		ContactsLoyalty contactsLoyalty = null;
		try{
			//changes done after transfer API
			Long contactLoyaltyID = transaction.getTransferedTo() == null ? transaction.getLoyaltyId() : transaction.getTransferedTo();
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO); 
			contactsLoyalty = contactsLoyaltyDao.findAllByLoyaltyId(contactLoyaltyID);
			return contactsLoyalty;
		}catch(Exception e){
			logger.error("Exception while getting loyalty object in active points timer....", e);
		}
		return contactsLoyalty;
	}
	
	private void updateActivePoints(ContactsLoyalty contactsLoyalty, LoyaltyTransactionChild transaction){
		logger.info(" Entered into updateActivePoints method >>>");
		try{
			String valueCode = transaction.getEarnType();
			Double netEarnedPoints = transaction.getHoldPoints();
			Double netEarnedAmount = transaction.getHoldAmount();
			
			if(valueCode != null && valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS) && transaction.getHoldPoints() != null){
				if(contactsLoyalty.getLoyaltyBalance() == null ) {
					contactsLoyalty.setLoyaltyBalance(netEarnedPoints);
				}
				else{
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() + netEarnedPoints);
				}
				if(contactsLoyalty.getTotalLoyaltyEarned() == null){
					contactsLoyalty.setTotalLoyaltyEarned(netEarnedPoints);
				}
				else{
					contactsLoyalty.setTotalLoyaltyEarned(contactsLoyalty.getTotalLoyaltyEarned() + netEarnedPoints);
				}
				if(transaction.getDescription2() != null){
					transaction.setDescription2(transaction.getDescription2()+Constants.ADDR_COL_DELIMETER+"Activated:"+netEarnedPoints);
				}else{
					transaction.setDescription2("Activated:"+netEarnedPoints);
				}
				
				contactsLoyalty.setHoldPointsBalance(contactsLoyalty.getHoldPointsBalance() - netEarnedPoints);
				transaction.setHoldPoints(0.0);
			}
			else if(valueCode != null && valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT) && transaction.getHoldAmount() != null){
				if(contactsLoyalty.getGiftcardBalance() == null ) {
					contactsLoyalty.setGiftcardBalance(netEarnedAmount);
				}
				else{
					contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + netEarnedAmount);
				}
				if(contactsLoyalty.getTotalGiftcardAmount() == null){
					contactsLoyalty.setTotalGiftcardAmount(netEarnedAmount);
				}
				else{
					contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + netEarnedAmount);
				}
				if(transaction.getDescription2() != null){
					transaction.setDescription2(transaction.getDescription2()+Constants.ADDR_COL_DELIMETER+"Activated:"+netEarnedAmount);
				}else{
					transaction.setDescription2("Activated:"+netEarnedAmount);
				}
				contactsLoyalty.setHoldAmountBalance(contactsLoyalty.getHoldAmountBalance() - netEarnedAmount);
				transaction.setHoldAmount(0.0);
			}
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			
			logger.info("activated...points="+netEarnedPoints+" amount = "+netEarnedAmount);
		}catch(Exception e){
			logger.error("Exception in update active points...", e);
		}
		logger.info("Completed updateActivePoints method <<<");
	}
	
	private void updateThresholdBonus(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, Double 
			fromLtyBalance, Double fromAmtBalance, LoyaltyProgramTier loyaltyProgramTier) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in updateThresholdBonus");
		try{
			LoyaltyThresholdBonusDao loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			List<LoyaltyThresholdBonus> threshBonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(program.getProgramId(), 'N' );
			List<LoyaltyThresholdBonus> pointsBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> amountBonusList = new ArrayList<LoyaltyThresholdBonus>();
			fromAmtBalance = fromAmtBalance == null ? 0.0 : fromAmtBalance;
			fromLtyBalance = fromLtyBalance == null ? 0.0 : fromLtyBalance;

			//String[] bonusArr = null; //new String[2];
			if(threshBonusList != null && threshBonusList.size()>0){
				for(LoyaltyThresholdBonus bonus : threshBonusList){
					if(bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_POINTS)){
						pointsBonusList.add(bonus);
					}
					else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
						amountBonusList.add(bonus);
					}
				}

				List<LoyaltyThresholdBonus> matchedBonusList = new ArrayList<LoyaltyThresholdBonus>();

				Collections.sort(pointsBonusList, new Comparator<LoyaltyThresholdBonus>(){
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});


				Collections.sort(amountBonusList, new Comparator<LoyaltyThresholdBonus>(){
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});

				matchedBonusList.addAll(pointsBonusList);
				matchedBonusList.addAll(amountBonusList);

				/*if(contactsLoyalty.getTotalLoyaltyEarned() != null && contactsLoyalty.getTotalLoyaltyEarned() > 0){
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
*/
				long bonusPoints = 0;
				double bonusAmount = 0.0;
				String bonusRate = null;
				boolean bonusflag =false;
				
				if(matchedBonusList != null && matchedBonusList.size() > 0){
					for (LoyaltyThresholdBonus matchedBonus : matchedBonusList) {
						
						bonusflag = false;
						long multiplier = 1;
						
						
						
						
						if (OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getEarnedLevelType())) {
							logger.info("---------POINTS-----------");
							logger.info("previous points balance (fromLtyBalance)"+fromLtyBalance);
							logger.info("after points balance (getEarnedLevelValue())"+matchedBonus.getEarnedLevelValue());
							
							//This code is for recurring bonus
							if(matchedBonus.isRecurring()){
								
								Double beforeFactor = fromLtyBalance.doubleValue()/matchedBonus.getEarnedLevelValue();
								Double afterFactor = contactsLoyalty.getTotalLoyaltyEarned()/matchedBonus.getEarnedLevelValue();
								if(beforeFactor.intValue() < afterFactor.intValue()) {
									bonusflag = true;
									multiplier = afterFactor.intValue()-beforeFactor.intValue();
								}
								logger.info("before factor===="+beforeFactor);
								logger.info("after factor===="+afterFactor);
								logger.info("multiplier===="+multiplier);
							}
							else if (! matchedBonus.isRecurring() && contactsLoyalty.getTotalLoyaltyEarned() >= matchedBonus.getEarnedLevelValue()
									&& (fromLtyBalance == null || fromLtyBalance.doubleValue() < matchedBonus.getEarnedLevelValue())) {
								bonusflag = true;
							}
						}else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getEarnedLevelType())) {
							
							logger.info("---------AMOUNT-----------");
							logger.info("previous points balance (fromAmtBalance)"+fromAmtBalance);
							logger.info("after points balance (getEarnedLevelValue())"+matchedBonus.getEarnedLevelValue());
							
							if(matchedBonus.isRecurring()){
								
								Double beforeFactor = fromAmtBalance.doubleValue()/matchedBonus.getEarnedLevelValue();
								Double afterFactor = contactsLoyalty.getTotalGiftcardAmount()/matchedBonus.getEarnedLevelValue();
								if(beforeFactor.intValue() < afterFactor.intValue()){
									bonusflag = true;
									multiplier = afterFactor.intValue()-beforeFactor.intValue();
								}
								logger.info("before factor===="+beforeFactor);
								logger.info("after factor===="+afterFactor);
								logger.info("multiplier===="+multiplier);
							
							}else if (! matchedBonus.isRecurring() && contactsLoyalty.getTotalGiftcardAmount() >= matchedBonus.getEarnedLevelValue()
									&& (fromAmtBalance == null || fromAmtBalance.doubleValue() < matchedBonus.getEarnedLevelValue())) {
								
								bonusflag = true;
							}
							
						}
						
						if(!bonusflag) continue;
						if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getExtraBonusType())){
							

							if (contactsLoyalty.getLoyaltyBalance() == null) {
								contactsLoyalty.setLoyaltyBalance(multiplier*matchedBonus.getExtraBonusValue());
							} else {
								contactsLoyalty.setLoyaltyBalance(
										contactsLoyalty.getLoyaltyBalance() + (multiplier*matchedBonus.getExtraBonusValue()));
							}
							if (contactsLoyalty.getTotalLoyaltyEarned() == null) {
								contactsLoyalty.setTotalLoyaltyEarned(multiplier*matchedBonus.getExtraBonusValue());
							} else {
								contactsLoyalty.setTotalLoyaltyEarned(
										contactsLoyalty.getTotalLoyaltyEarned() +(multiplier* matchedBonus.getExtraBonusValue()));
							}
							bonusPoints = multiplier*matchedBonus.getExtraBonusValue().longValue();
							
						
							bonusRate = Constants.STRING_NILL + matchedBonus.getEarnedLevelValue() + " "
									+ matchedBonus.getEarnedLevelType() + " --> " + matchedBonus.getExtraBonusValue() + " "
									+ matchedBonus.getExtraBonusType();
							
							LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, 
									bonusPoints, OCConstants.LOYALTY_TYPE_POINTS, bonusRate);

							logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
							createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
									childTxbonus.getTransChildId(),matchedBonus.getThresholdBonusId());
							if(loyaltyProgramTier != null){
								applyConversionRules(contactsLoyalty, childTxbonus, program, loyaltyProgramTier);	
								loyaltyProgramTier = applyTierUpgradeRule(contactsLoyalty, childTxbonus, program, loyaltyProgramTier);
							}
						}
						else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getExtraBonusType())){
							

							
							String result = Utility.truncateUptoTwoDecimal(multiplier*matchedBonus.getExtraBonusValue());
							if (result != null)
								bonusAmount = Double.parseDouble(result);
							bonusRate = Constants.STRING_NILL + matchedBonus.getEarnedLevelValue() + " "
									+ matchedBonus.getEarnedLevelType() + " --> " + matchedBonus.getExtraBonusValue() + " "
									+ matchedBonus.getExtraBonusType();
							if (contactsLoyalty.getGiftcardBalance() == null) {
								// contactsLoyalty.setGiftcardBalance(matchedBonus.getExtraBonusValue());
								contactsLoyalty.setGiftcardBalance(bonusAmount);
							} else {
								// contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() +
								// matchedBonus.getExtraBonusValue());
								contactsLoyalty.setGiftcardBalance(
										new BigDecimal(contactsLoyalty.getGiftcardBalance() + bonusAmount)
												.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							}
							if (contactsLoyalty.getTotalGiftcardAmount() == null) {
								// contactsLoyalty.setTotalGiftcardAmount(matchedBonus.getExtraBonusValue());
								contactsLoyalty.setTotalGiftcardAmount(bonusAmount);
							} else {
								// contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount()
								// + matchedBonus.getExtraBonusValue());
								contactsLoyalty.setTotalGiftcardAmount(
										new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + bonusAmount)
												.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							}
							
							
							//bonusAmount = matchedBonus.getExtraBonusValue();
							bonusRate = ""+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
									" --> "+matchedBonus.getExtraBonusValue()+" "+OCConstants.LOYALTY_TYPE_AMOUNT;

							
							LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, 
									bonusAmount, OCConstants.LOYALTY_TYPE_AMOUNT, bonusRate);


							logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
							createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
									childTxbonus.getTransChildId(),matchedBonus.getThresholdBonusId());
							/*if(loyaltyProgramTier != null){
								// CALL CONVERSION
								applyConversionRules(contactsLoyalty, childTxbonus, program, loyaltyProgramTier);
								// CALL TIER UPGD
								loyaltyProgramTier = applyTierUpgradeRule(contactsLoyalty, program, childTxbonus, loyaltyProgramTier);
							}*/
						}
						
						LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
						Contacts contact = null;
						LoyaltyAutoComm autoComm = getLoyaltyAutoComm(program.getProgramId());
						if(bonusflag && autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
								contactsLoyalty.getContact().getContactId() != null){
							contact = findContactById(contactsLoyalty.getContact().getContactId());
							if(contact != null && contact.getEmailId() != null){
								autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
										contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
										contact.getContactId(), contactsLoyalty.getLoyaltyId());
							}
						}
						UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
						Users user = userDao.findByUserId(contactsLoyalty.getUserId());
						if(user.isEnableSMS() && bonusflag && autoComm != null && autoComm.getThreshBonusSmsTmpltId() != null) { 
							Long contactId = null;	
							if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
								contactId = contactsLoyalty.getContact().getContactId();
							}
							autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, contactId,
									contactsLoyalty.getLoyaltyId(), null);
						}
						
						
						
						
						
					}
				}

				

			}
			else{
				logger.error("Thershold bonus is Null");
			}
			logger.debug("<<<<<<<<<<<<< completed updateThresholdBonus");
			//return bonusArr;
		}catch(Exception e){
			logger.error("Exception in update threshold bonus...", e);
			throw new LoyaltyProgramException("Exception in threshold bonus...");
		}
	}
	
	private LoyaltyProgramTier applyTierUpgradeRule(ContactsLoyalty contactsLoyalty, LoyaltyTransactionChild transaction, LoyaltyProgram program, LoyaltyProgramTier currTier){
		logger.info(" Entered into applyTierUpgradeRule method >>>");
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

				transaction.setTierId(currTier.getTierId());
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
				//loyaltyTransactionChildDao.saveOrUpdate(transaction);
				loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			}
			
			 Contacts contact = null;
			 LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			 LoyaltyAutoComm autoComm = getLoyaltyAutoComm(program.getProgramId());
				if (tierUpgd && autoComm != null && autoComm.getTierUpgdEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
						contactsLoyalty.getContact().getContactId() != null) {
					contact = contactsLoyalty.getContact();
					if (contact != null && contact.getEmailId() != null) {
						autoCommGen.sendTierUpgdTemplate(autoComm.getTierUpgdEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
								contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
								contact.getContactId(), contactsLoyalty.getLoyaltyId());
					}
				}
				UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				Users user = usersDao.findByUserId(contactsLoyalty.getUserId());
				if (user.isEnableSMS() && tierUpgd && autoComm != null && autoComm.getTierUpgdSmsTmpltId() != null) {
					Long contactId = null;
					if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
						contactId = contactsLoyalty.getContact().getContactId();
					}
					autoCommGen.sendTierUpgdSMSTemplate(autoComm.getTierUpgdSmsTmpltId(), user, contactId,
							contactsLoyalty.getLoyaltyId(), null);
				}
			logger.info("tier upgraded flag = "+tierUpgd);
		}catch(Exception e){
			logger.error("Exception while upgrading tier...", e);
		}
		logger.info("Completed applyTierUpgradeRule method <<<");
		return currTier;
	}
	
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
  				//	double convertedAmount = tier.getConvertToAmount() * multiple;
					double convertedAmount = 0.0;
					String result = Utility.truncateUptoTwoDecimal(tier.getConvertToAmount() * multiple);
					if(result != null)
						convertedAmount = Double.parseDouble(result);
				
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
                   /* transaction.setAmountBalance(contactsLoyalty.getLoyaltyBalance());
					transaction.setPointsBalance(contactsLoyalty.getGiftcardBalance()); 
*/
					transaction.setAmountBalance(contactsLoyalty.getGiftcardBalance());
					transaction.setPointsBalance(contactsLoyalty.getLoyaltyBalance());
					transaction.setGiftBalance(contactsLoyalty.getGiftBalance());
					transaction.setAmountDifference(""+convertedAmount);
					transaction.setPointsDifference(""+subPoints);
					
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
	
	private void updateTransactionStatus(LoyaltyTransactionChild transaction, String status) throws Exception {
		
		LoyaltyTransactionChildDao LoyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		LoyaltyTransactionChildDaoForDML LoyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
		transaction.setEarnStatus(status);
		//LoyaltyTransactionChildDao.saveOrUpdate(transaction);
		LoyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
	}
	
	private void saveContactsLoyalty(ContactsLoyalty loyalty) throws Exception {
		
		ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		loyaltyDao.saveOrUpdate(loyalty);
	}
	
	private LoyaltyTransactionChild createBonusTransaction(ContactsLoyalty loyalty,
			double earnedValue, String earnType, String bonusRate){
		logger.info(" Entered into createBonusTransaction method >>>");
		LoyaltyTransactionChild bonusTransaction = null;
		try{
			
			bonusTransaction = new LoyaltyTransactionChild();
			bonusTransaction.setMembershipNumber(loyalty.getCardNumber()+"");
			bonusTransaction.setMembershipType(loyalty.getMembershipType());
			bonusTransaction.setCardSetId(loyalty.getCardSetId());
			
			bonusTransaction.setCreatedDate(Calendar.getInstance());
			bonusTransaction.setEarnType(earnType);
			if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
				bonusTransaction.setEarnedPoints(earnedValue);
			}
			else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
				bonusTransaction.setEarnedAmount(earnedValue);
			}
			bonusTransaction.setEnteredAmount(earnedValue);
			bonusTransaction.setOrgId(loyalty.getOrgId());
			bonusTransaction.setPointsBalance(loyalty.getLoyaltyBalance());
			bonusTransaction.setAmountBalance(loyalty.getGiftcardBalance());
			bonusTransaction.setGiftBalance(loyalty.getGiftBalance());
			bonusTransaction.setProgramId(loyalty.getProgramId());
			bonusTransaction.setTierId(loyalty.getProgramTierId());
			bonusTransaction.setUserId(loyalty.getUserId());
			bonusTransaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_BONUS);
			bonusTransaction.setDescription("Threshold bonus: "+bonusRate);
			bonusTransaction.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_AUTO);
			bonusTransaction.setLoyaltyId(loyalty.getLoyaltyId());
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransactionChildDao.saveOrUpdate(bonusTransaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(bonusTransaction);
			
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		logger.info("Completed createBonusTransaction method <<<");
		return bonusTransaction;
	}
	
	
	private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty,
			Long expiryPoints, Double expiryAmount, Long orgId, Long transChildId,Long bonusId){
		logger.info(" Entered into createExpiryTransaction method >>>");
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
			transaction.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setBonusId(bonusId);

			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			//loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
			loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);
			
			
			
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		logger.info("Completed createExpiryTransaction method <<<");
		return transaction;
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
	
	private LoyaltyProgramTier getNextTier(LoyaltyProgramTier tier) throws Exception {
		logger.info(" Entered into getNextTier method >>>");
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		
		String[] type = tier.getTierType().split("\\s+");
		int tierNo = Integer.valueOf(type[1].trim()).intValue();
		int nextTierNo = tierNo + 1;
		String nextTier = "Tier "+nextTierNo;
		LoyaltyProgramTier programTier = tierDao.findTierByType(tier.getProgramId(), nextTier);
		logger.info("Completed getNextTier method <<<");
		return programTier;
	}
	
	private LoyaltyAutoComm getLoyaltyAutoComm(Long programId) throws Exception {
		LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
		return autoCommDao.findById(programId);
	}

	private Contacts findContactById(Long cid) throws Exception {
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactsDao.findById(cid);
	}
	
	
	
	
	
	
}
