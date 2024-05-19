package org.mq.optculture.business.helper;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyMemberSessionID;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyMemberSessionIDDao;
import org.mq.optculture.data.dao.LoyaltyMemberSessionIDDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdAlertsDao;
import org.mq.optculture.data.dao.LoyaltyThresholdAlertsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class LoyaltyProgramHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public static String getMbrshipExpiryDate(Calendar createdDate, Calendar upgradedDate, boolean upgdResetFlag,
			String dateType, Long dateValue){ 
		
		Calendar expiryCal = null;
		
		if(upgdResetFlag == true){
			
			if(upgradedDate != null){
				expiryCal = (Calendar)upgradedDate.clone();
			}
			else{
				expiryCal = (Calendar)createdDate.clone();
			}
			
		}
		else{
			expiryCal = (Calendar)createdDate.clone();
		}
		
		if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(dateType)){
			expiryCal.add(Calendar.MONTH, dateValue.intValue());
		}
		else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(dateType)){
			expiryCal.add(Calendar.YEAR, dateValue.intValue());
		}
		
		return MyCalendar.calendarToString(expiryCal, MyCalendar.FORMAT_YEARTODATE);
		
	}
	
	public static String getGiftMbrshipExpiryDate(Calendar createdDate, String dateType, Long dateValue){
		
		Calendar expiryCal = (Calendar)createdDate.clone();
		
		if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(dateType)){
			expiryCal.add(Calendar.MONTH, dateValue.intValue());
		}
		else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(dateType)){
			expiryCal.add(Calendar.YEAR, dateValue.intValue());
		}
		
		return MyCalendar.calendarToString(expiryCal, MyCalendar.FORMAT_YEARTODATE);
		
	}
	
	public static String validateMembershipMobile(String phone){
		try{
			Long phoneNum = Long.parseLong(phone);
//			if(phone.trim().length() != 10) return OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID;
		}catch(Exception e){
			return OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID;
		}
		return OCConstants.LOYALTY_MEMBERSHIP_MOBILE_VALID;
	}
	
	/**
	 * This method verifies for concurrent card generation under organisation by multiple users.
	 * @param programId
	 * @return
	 * @throws Exception
	 */
	public static boolean anotherCardGeneration(Long programId){
		try{
			logger.info("Checking another user generating cards...");
			LoyaltyProgramDao programDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			LoyaltyProgram program = programDao.findById(programId);
			Long orgId = program.getOrgId();
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO); 
			UserOrganization userOrg = usersDao.findByOrgId(orgId);
			if(OCConstants.LOYALTY_CARD_GENERATION_FLAG_Y.equals(userOrg.getCardGenerateFlag())){
				return true;
			}
			return false;
		} catch(Exception e){
			logger.error("Exception in...", e);
			return false;
		}
	}
	
	public static LoyaltyProgramTier applyTierUpgdRules(Long contactId, ContactsLoyalty contactsLoyalty, LoyaltyProgramTier currTier) throws Exception{

		LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

		List<LoyaltyProgramTier> tempTiersList = loyaltyProgramTierDao.fetchTiersByProgramId(contactsLoyalty.getProgramId());
		if (tempTiersList == null || tempTiersList.size() <= 0) {
			logger.info("Tiers list is empty...");
			return currTier;
		}
		else if (tempTiersList.size() >= 1) {
			Collections.sort(tempTiersList, new Comparator<LoyaltyProgramTier>() {
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
		
		List<LoyaltyProgramTier> tiersList = new ArrayList<LoyaltyProgramTier>();
		boolean flag = false;
		for(LoyaltyProgramTier tier : tempTiersList) {
			logger.info("tier level : "+tier.getTierType());
			if(currTier.getTierType().equalsIgnoreCase(tier.getTierType())){
				flag = true;
			}
			if(flag){
				tiersList.add(tier);
			}
		}

		//Prepare eligible tiers map
		Iterator<LoyaltyProgramTier> iterTier = tiersList.iterator();
		Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
		LoyaltyProgramTier prevtier = null;
		LoyaltyProgramTier nexttier = null;

		while(iterTier.hasNext()){
			nexttier = iterTier.next();
			if(currTier.getTierType().equals(nexttier.getTierType())){
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

			Double totLoyaltyPointsValue = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.00 : contactsLoyalty.getTotalLoyaltyEarned();
			logger.info("totLoyaltyPointsValue value = "+totLoyaltyPointsValue);

			if(totLoyaltyPointsValue == null || totLoyaltyPointsValue <= 0){
				logger.info("totLoyaltyPointsValue value is empty...");
				return currTier;
			}
			else{
				Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
				LoyaltyProgramTier prevKeyTier = null;
				LoyaltyProgramTier nextKeyTier = null;
				while(it.hasNext()){
					nextKeyTier = it.next();
					logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
					logger.info("-------------currTier::"+currTier.getTierType());
					if(currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())){
						prevKeyTier = nextKeyTier;
						continue;
					}
					if(totLoyaltyPointsValue > 0 && totLoyaltyPointsValue < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
						if(prevKeyTier == null){
							logger.info("selected tier is currTier..."+currTier.getTierType());
							return currTier;
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
				return currTier;
			}//else
		}
		else if(contactId == null){
			logger.info("contactId is null and selected tier..."+tiersList.get(0).getTierType());
			return currTier;
		}
		else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);
			
			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);				

			//List<Map<String, Object>> contactPurcahseList = contactsDao.findContactPurchaseDetails(contactsLoyalty.getUserId(), contactId);
			Double totPurchaseValue = null;
			/*if(contactPurcahseList != null && contactPurcahseList.size() == 1) {
				for (Map<String, Object> eachMap : contactPurcahseList) {
					if(eachMap.containsKey("tot_purchase_amt")){
						totPurchaseValue = Double.valueOf(eachMap.get("tot_purchase_amt") != null ? eachMap.get("tot_purchase_amt").toString() : "0.00");
						logger.info("purchase value = "+totPurchaseValue);
					}
				}
			}*/
			//LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			//totPurchaseValue = contactsLoyalty.getLifeTimePurchaseValue() == null ? 0.00 : contactsLoyalty.getLifeTimePurchaseValue();//Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
			totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);
			logger.info("purchase value = "+totPurchaseValue);

			//if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){
			if(totPurchaseValue == null || totPurchaseValue <= 0){
				logger.info("purchase value is empty...");
				return currTier;
			}
			else{
				Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
				LoyaltyProgramTier prevKeyTier = null;
				LoyaltyProgramTier nextKeyTier = null;
				while(it.hasNext()){
					nextKeyTier = it.next();
					logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
					logger.info("-------------currTier::"+currTier.getTierType());
					if(currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())){
						prevKeyTier = nextKeyTier;
						continue;
					}
					if(totPurchaseValue > 0 && totPurchaseValue < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
						if(prevKeyTier == null){
							logger.info("selected tier is currTier..."+currTier.getTierType());
							return currTier;
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
				return currTier;
			}//else
		}
		else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			
			Double cumulativeAmount = 0.0;
//			Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
			ListIterator<LoyaltyProgramTier> it = new ArrayList(eligibleMap.keySet()).listIterator(eligibleMap.size());
//			LoyaltyProgramTier prevKeyTier = null;
			LoyaltyProgramTier nextKeyTier = null;
			while(it.hasPrevious()){
				nextKeyTier = it.previous();
				logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
				logger.info("-------------currTier::"+currTier.getTierType());
				if(currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())){
//					prevKeyTier = nextKeyTier;
					return currTier;
				}
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				logger.info("contactId = "+contactId+" startDate = "+startDate+" endDate = "+endDate);

				/*RetailProSalesDao salesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
				Object[] cumulativeAmountArr = salesDao.getCumulativePurchase(contactsLoyalty.getUserId(), contactId, startDate, endDate);
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
				logger.info("-------------currTier::"+currTier.getTierType());
				if(currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())){
					prevKeyTier = nextKeyTier;
					continue;
				}
				
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				logger.info("contactId = "+contactId+" startDate = "+startDate+" endDate = "+endDate);

				RetailProSalesDao salesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
				Object[] cumulativeAmountArr = salesDao.getCumulativePurchase(contactsLoyalty.getUserId(), contactId, startDate, endDate);

				cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());

				if(cumulativeAmount == null || cumulativeAmount <= 0){
					logger.info("cumulative purchase value is empty...");
					continue;
				}
				if(cumulativeAmount > 0 && cumulativeAmount < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
					if(prevKeyTier == null){
						logger.info("selected tier is currTier..."+currTier.getTierType());
						return currTier;
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
			return currTier;
		}
		else{
			return currTier;
		}
	
	}
        
    public static LoyaltyProgramTier checkElelegibilityOfMultipleTierUpgrade(ContactsLoyalty contactsLoyalty,
			LoyaltyProgramTier currTier,Double enteredValue,Double LPV) throws Exception {
    	
    	LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

		List<LoyaltyProgramTier> tempTiersList = loyaltyProgramTierDao
				.fetchTiersByProgramId(contactsLoyalty.getProgramId());
		if (tempTiersList == null || tempTiersList.size() <= 0) {
			logger.info("Tiers list is empty...");
			return currTier;
		} else if (tempTiersList.size() >= 1) {
			Collections.sort(tempTiersList, new Comparator<LoyaltyProgramTier>() {
				@Override
				public int compare(LoyaltyProgramTier o1, LoyaltyProgramTier o2) {

					int num1 = Integer.valueOf(o1.getTierType().substring(5)).intValue();
					int num2 = Integer.valueOf(o2.getTierType().substring(5)).intValue();
					if (num1 < num2) {
						return -1;
					} else if (num1 == num2) {
						return 0;
					} else {
						return 1;
					}
				}
			});
		}

		List<LoyaltyProgramTier> tiersList = new ArrayList<LoyaltyProgramTier>();
		boolean flag = false;
		for (LoyaltyProgramTier tier : tempTiersList) {
			logger.info("tier level : " + tier.getTierType());
			if (currTier.getTierType().equalsIgnoreCase(tier.getTierType())) {
				flag = true;
			}
			if (flag) {
				tiersList.add(tier);
			}
		}

		// Prepare eligible tiers map
		Iterator<LoyaltyProgramTier> iterTier = tiersList.iterator();
		Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
		LoyaltyProgramTier prevtier = null;
		LoyaltyProgramTier nexttier = null;

		while (iterTier.hasNext()) {
			nexttier = iterTier.next();
			
				
				if (currTier.getTierType().equals(nexttier.getTierType())) {
					eligibleMap.put(nexttier, null);
				} else {
					if(prevtier.getMultipleTierUpgrdRules()==null || prevtier.getMultipleTierUpgrdRules().isEmpty()) continue;//APP-3946
					String[] listOfRules = prevtier.getMultipleTierUpgrdRules().split("\\|\\|");
					
					for(String rule : listOfRules) {
						
						String[] listOfAttributes = rule.split(":");
						
					if ((Integer.valueOf(prevtier.getTierType().substring(5)) + 1) == Integer
							.valueOf(nexttier.getTierType().substring(5))
							&& listOfAttributes[1] != null) {
						eligibleMap.put(nexttier, prevtier);
						logger.info("eligible tier =" + nexttier.getTierType() + " upgdconstrant value = "
								+ listOfAttributes[1]);
					}
				}
			}
			prevtier = nexttier;
		}
		
		Iterator<LoyaltyProgramTier> it = tiersList.iterator();
		LoyaltyProgramTier prevKeyTier = null;
		LoyaltyProgramTier nextKeyTier = null;
		LoyaltyProgramTier finalTier = null;
		boolean tierChngd = false;
		LoyaltyProgramTier updTier = null;
		while (it.hasNext()) {
			nextKeyTier = it.next();
			
			if (currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())) {
				prevKeyTier = nextKeyTier;
				continue;
			}
			if(eligibleMap.get(nextKeyTier)==null) continue;//APP-3946
			String[] listOfRules = eligibleMap.get(nextKeyTier).getMultipleTierUpgrdRules().split("\\|\\|");
			
			for(String rule : listOfRules) {
				
				String[] listOfAttributes = rule.split(":");
				
				String type = listOfAttributes[0];
				String value = listOfAttributes[1];
				
				if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() && 
						type.equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE)) {
					
					if (LPV == null || LPV <= 0) {
						logger.info("purchase value is empty...");
						if(tierChngd) finalTier= updTier;//APP-4500
						else
						finalTier = currTier;
						//return currTier;
					} else {
						
							
							if (LPV > 0
									&& LPV < Double.parseDouble(value)) {
								
								finalTier = currTier;
								if(tierChngd) finalTier=updTier;
								
							} else if (LPV > 0
									&& LPV >= Double.parseDouble(value)) {
									//&& !it.hasNext()) {
								
								logger.info("selected tier..." + nextKeyTier.getTierType());
								tierChngd = true;
								finalTier= nextKeyTier;
								updTier= finalTier;
								//return nextKeyTier;
							}
							//prevKeyTier = nextKeyTier;
						
						//return currTier;
					}
					
				} else if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() && type.equalsIgnoreCase(OCConstants.LOYALTY_SINGLE_PURCHASE_VALUE)) {
					
					Double singleShotPurchaseValue = null;
					
	                LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
							.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
	                
	                singleShotPurchaseValue = loyaltyTransactionChildDao.findPurchaseAmountInLast24Hours(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId());
	                
					logger.info("purchase value before totalpurchasevalue= " + singleShotPurchaseValue);
					
					singleShotPurchaseValue = singleShotPurchaseValue + enteredValue;
					
					logger.info("purchase value after totalpurchasevalue= " + singleShotPurchaseValue);

					
					if (singleShotPurchaseValue == null || singleShotPurchaseValue <= 0) {
						logger.info("purchase value is empty...");
						if(tierChngd) finalTier= updTier;//APP-4500
						else
						finalTier = currTier;
						//return currTier;
					} else {
						
							
							if (singleShotPurchaseValue > 0
									&& singleShotPurchaseValue < Double.parseDouble(value)) {
								
								finalTier = currTier;
								if(tierChngd) finalTier=updTier;
								
							} else if (singleShotPurchaseValue > 0
									&& singleShotPurchaseValue >= Double.parseDouble(value)) {
									//&& !it.hasNext()) {
								
								logger.info("selected tier..." + nextKeyTier.getTierType());
								tierChngd = true;
								finalTier= nextKeyTier;
								updTier= finalTier;
								//return nextKeyTier;
							}
							//prevKeyTier = nextKeyTier;
						}
					
				}
			}
			prevKeyTier = nextKeyTier;
		}

    	
    	return finalTier;
    }

	public static void updateLoyaltyMembrshpPhone(Contacts contact, String newPhone) {
		try {
			logger.info("========= entered updateLoyaltyMembrshpPhone =========");
			if(contact.getContactId() == null) return; 
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			List<ContactsLoyalty> contactLoyaltyList = contactsLoyaltyDao.findOCLoyaltyListByContactId(contact.getUsers().getUserId(), contact.getContactId());
			if(contactLoyaltyList == null || contactLoyaltyList.size() == 0) return;

			for (ContactsLoyalty loyalty : contactLoyaltyList) {

				boolean isDifferentMobile = false;
				boolean sendUpdateAlert = false;
				String conMobile = loyalty.getMobilePhone();
				//to identify whether entered one is same as previous mobile
				if(newPhone != null && !newPhone.isEmpty()) {
					if(conMobile != null && !conMobile.isEmpty() && !conMobile.equals(newPhone)) {
						if( (conMobile.length() < newPhone.length() && !newPhone.endsWith(conMobile) ) ||
								(newPhone.length() < conMobile.length() && !conMobile.endsWith(newPhone)) || conMobile.length() == newPhone.length()) {
							isDifferentMobile = true;
							sendUpdateAlert = true;
						}
					}
					else if(conMobile == null || conMobile.isEmpty()){
						isDifferentMobile = true;
					}
				}

				if(isDifferentMobile) {
					if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equalsIgnoreCase(loyalty.getMembershipType())) {
						LoyaltyProgramDao loyaltyProgramDao =  (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
						LoyaltyProgram prgmObj = loyaltyProgramDao.findById(loyalty.getProgramId());
						if(prgmObj != null && prgmObj.getUniqueMobileFlag() == OCConstants.FLAG_YES) {
							ContactsLoyalty tempLtyContact = contactsLoyaltyDao.getLoyaltyByPrgmAndPhone(contact.getUsers(),loyalty.getProgramId(),newPhone);
							if(tempLtyContact != null) continue;
							loyalty.setMobilePhone(newPhone);
							contactsLoyaltyDaoForDML.saveOrUpdate(loyalty);
							logger.info("updated loyalty registered phone from::"+conMobile+" to::"+newPhone);
							if(sendUpdateAlert){
								sendUpdateAlert(loyalty, conMobile, newPhone, contact.getEmailId(), contact.getContactId());
							}
							else{
								sendAddAlert(loyalty, newPhone, contact.getEmailId(), contact.getContactId());
							}
						}
						else {
							loyalty.setMobilePhone(newPhone);
							contactsLoyaltyDaoForDML.saveOrUpdate(loyalty);
							logger.info("updated loyalty registered phone from::"+conMobile+" to::"+newPhone);
							if(sendUpdateAlert){
								sendUpdateAlert(loyalty, conMobile, newPhone, contact.getEmailId(), contact.getContactId());
							}
							else{
								sendAddAlert(loyalty, newPhone, contact.getEmailId(), contact.getContactId());
							}
						}
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equalsIgnoreCase(loyalty.getMembershipType())) {
						ContactsLoyalty tempContact = contactsLoyaltyDao.getLoyaltyByPrgmAndMembrshp(loyalty.getUserId(), loyalty.getProgramId(),newPhone); 
						if (tempContact != null) continue;
						loyalty.setMobilePhone(newPhone);
						loyalty.setCardNumber(newPhone);
						contactsLoyaltyDaoForDML.saveOrUpdate(loyalty);
						logger.info("updated loyalty registered phone from::"+conMobile+" to::"+newPhone);
						if(sendUpdateAlert){
							sendUpdateAlert(loyalty, conMobile, newPhone, contact.getEmailId(), contact.getContactId());
						}
						else{
							sendAddAlert(loyalty, newPhone, contact.getEmailId(), contact.getContactId());
						}
					}
				}
				else continue;
			}
			logger.info("========= exited updateLoyaltyMembrshpPhone =========");
		}catch(Exception e){
			logger.error("Exception::",e);
		}

	}
	public static void updateLoyaltyEmailId(Contacts contact, String newEmailId){
		try{
			logger.info("========= entered updateLoyaltyMembrshpPhone =========");
			if(contact.getContactId() == null || newEmailId ==null || newEmailId.isEmpty()) return; 
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			List<ContactsLoyalty> contactLoyaltyList = contactsLoyaltyDao.findOCLoyaltyListByContactId(contact.getUsers().getUserId(), contact.getContactId());//here if its null or empty u dont need to fetch from DB 
			if(contactLoyaltyList == null || contactLoyaltyList.size() == 0) return;

			for (ContactsLoyalty loyalty : contactLoyaltyList) {

				boolean isDifferentEmail = false;
				boolean sendUpdateAlert = false;
				String conEmail = loyalty.getEmailId();
				//to identify whether entered one is same as previous mobile
				if(newEmailId != null && !newEmailId.isEmpty()) {
					if(conEmail != null && !conEmail.isEmpty() && !conEmail.equals(newEmailId)) {
						
							isDifferentEmail = true;
							sendUpdateAlert = true;
						
					}
					else if(conEmail == null || conEmail.isEmpty()){
						isDifferentEmail = true;
					}
				}

				if(isDifferentEmail) {
						LoyaltyProgramDao loyaltyProgramDao =  (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
						LoyaltyProgram prgmObj = loyaltyProgramDao.findById(loyalty.getProgramId());
						if(prgmObj != null && prgmObj.getUniqueEmailFlag() == OCConstants.FLAG_YES) {
							ContactsLoyalty tempLtyContact = contactsLoyaltyDao.getLoyaltyByPrgmAndPhone(contact.getUsers(),loyalty.getProgramId(),newEmailId);
							if(tempLtyContact != null) continue;
							loyalty.setEmailId(newEmailId);
							contactsLoyaltyDaoForDML.saveOrUpdate(loyalty);
							logger.info("updated loyalty registered phone from::"+conEmail+" to::"+newEmailId);
							if(sendUpdateAlert){
								sendUpdateAlertEmail(loyalty, conEmail, newEmailId, contact.getPhone()+Constants.STRING_NILL, contact.getContactId());
							}
							else{
								sendAddAlertEmail(loyalty, newEmailId, contact.getPhone()+Constants.STRING_NILL, contact.getContactId());
							}
						}
						else {
							loyalty.setEmailId(newEmailId);
							contactsLoyaltyDaoForDML.saveOrUpdate(loyalty);
							logger.info("updated loyalty registered phone from::"+conEmail+" to::"+newEmailId);
							if(sendUpdateAlert){
								sendUpdateAlertEmail(loyalty, conEmail, newEmailId, contact.getPhone()+Constants.STRING_NILL, contact.getContactId());
							}
							else{
								sendAddAlertEmail(loyalty, newEmailId, contact.getPhone()+Constants.STRING_NILL, contact.getContactId());
							}
						}
				}
				else continue;
			}
			logger.info("========= exited updateLoyaltyMembrshpPhone =========");
		}catch(Exception e){
			logger.error("Exception::",e);
		}

	}
	
	
	
	
	private static void sendAddAlert(ContactsLoyalty loyalty, String newPhone,	String emailId, Long contactId) {
		try{
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.find(loyalty.getUserId());
			String phoneSubStr = StringUtils.right(newPhone, 4);
			String orgName = user.getUserOrganization().getOrganizationName();
			if(emailId != null && !emailId.isEmpty()){
				String messageStr = PropertyUtil.getPropertyValueFromDB("ltyPhoneAddTemplate");
				messageStr = messageStr.replace("[toStr]", phoneSubStr);

				EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
				EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
				EmailQueue email = new EmailQueue(null, Constants.EQ_TYPE_LOYALTY_OC_ALERTS  , messageStr, Constants.EQ_STATUS_ACTIVE, emailId, user, MyCalendar.getNewCalendar(), orgName+": Your Phone Number Updated Successfully!", contactId, null);
				//emailQueueDao.saveOrUpdate(email);
				emailQueueDaoForDML.saveOrUpdate(email);
			}

			if(!user.isEnableSMS()) return;

			String message = PropertyUtil.getPropertyValueFromDB("ltyPhoneAddSmsMsgTemplate");
			message = message.replace("[orgName]", orgName);
			message = message.replace("[toStr]", phoneSubStr);
			
			//validate loyalty phone no and new phone no
			Map<String, Object> resultMap = null;

			resultMap = validateMobile(user,newPhone);
			newPhone = (String) resultMap.get("phone"); 
			boolean phoneIsValid = (Boolean) resultMap.get("isValid");

			if(phoneIsValid){
				sendSmsAlert(user, newPhone, message);
			}
		}catch(Exception e){
			logger.error("Exception in sending alerts", e);
		}
	}

	private static void sendUpdateAlert(ContactsLoyalty loyalty, String conMobile, String newPhone, String emailId, Long contactId) {
		try{
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.find(loyalty.getUserId());
			String conMobileSubStr = StringUtils.right(conMobile, 4);
			String phoneSubStr = StringUtils.right(newPhone, 4);
			String orgName = user.getUserOrganization().getOrganizationName();
			if(emailId != null && !emailId.isEmpty()){
				String messageStr = PropertyUtil.getPropertyValueFromDB("ltyPhoneUpdateTemplate");
				messageStr = messageStr.replace("[fromStr]", conMobileSubStr);
				messageStr = messageStr.replace("[toStr]", phoneSubStr);

				EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
				EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
				EmailQueue email = new EmailQueue(null, Constants.EQ_TYPE_LOYALTY_OC_ALERTS  , messageStr, Constants.EQ_STATUS_ACTIVE, emailId, user, MyCalendar.getNewCalendar(), orgName+": Your Phone Number Updated Successfully!", contactId, null);
				//emailQueueDao.saveOrUpdate(email);
				emailQueueDaoForDML.saveOrUpdate(email);
			}

			if(!user.isEnableSMS()) return;

			String message = PropertyUtil.getPropertyValueFromDB("ltyPhoneUpdateSmsMsgTemplate");
			message = message.replace("[orgName]", orgName);
			message = message.replace("[fromStr]", conMobileSubStr);
			message = message.replace("[toStr]", phoneSubStr);
			
			//validate loyalty phone no and new phone no
			Map<String, Object> resultMap = null;

			resultMap = validateMobile(user, conMobile);
			conMobile = (String) resultMap.get("phone");
			boolean conMobileIsValid = (Boolean) resultMap.get("isValid");

			resultMap = validateMobile(user,newPhone);
			newPhone = (String) resultMap.get("phone"); 
			boolean phoneIsValid = (Boolean) resultMap.get("isValid");

			if(conMobileIsValid){
				sendSmsAlert(user, conMobile, message);
			}
			if(phoneIsValid){
				sendSmsAlert(user, newPhone, message);
			}
		}catch(Exception e){
			logger.error("Exception in sending alerts", e);
		}
	}

	private static void sendUpdateAlertEmail(ContactsLoyalty loyalty, String conEmail, String newEmailId, String Phone, Long contactId) {
		try{
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.find(loyalty.getUserId());
			
			String orgName = user.getUserOrganization().getOrganizationName();
			if(validateEmail(conEmail) && validateEmail(newEmailId) ){
				String messageStr = PropertyUtil.getPropertyValueFromDB("ltyEmailUpdateTemplate");
				if(messageStr== null || messageStr.isEmpty()) return;
				messageStr = messageStr.replace("[fromStr]", conEmail);
				messageStr = messageStr.replace("[toStr]", newEmailId);

				EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
				EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
				EmailQueue email = new EmailQueue(null, Constants.EQ_TYPE_LOYALTY_OC_ALERTS  , messageStr, Constants.EQ_STATUS_ACTIVE, conEmail, user, MyCalendar.getNewCalendar(), orgName+": Your Email Id Updated Successfully!", contactId, null);
				EmailQueue emailNew = new EmailQueue(null, Constants.EQ_TYPE_LOYALTY_OC_ALERTS  , messageStr, Constants.EQ_STATUS_ACTIVE, newEmailId, user, MyCalendar.getNewCalendar(), orgName+": Your Email Id Updated Successfully!", contactId, null);
				//emailQueueDao.saveOrUpdate(email);
				emailQueueDaoForDML.saveOrUpdate(email);
				emailQueueDaoForDML.saveOrUpdate(emailNew);
			}
			else return;
			
			if(!user.isEnableSMS()) return;

			String message = PropertyUtil.getPropertyValueFromDB("ltyEmailUpdateSmsMsgTemplate");
			if(message== null || message.isEmpty()) return;
			message = message.replace("[orgName]", orgName);
			message = message.replace("[fromStr]", conEmail);
			message = message.replace("[toStr]", newEmailId);
			
			Map<String, Object> resultMap = null;
			resultMap = validateMobile(user, Phone);
			String conMobile = (String) resultMap.get("phone");
			boolean conMobileIsValid = (Boolean) resultMap.get("isValid");
			
			if(conMobileIsValid){
				sendSmsAlert(user, conMobile, message);
			}
			
			
		}catch(Exception e){
			logger.error("Exception in sending alerts", e);
		}
	}
	private static void sendAddAlertEmail(ContactsLoyalty loyalty, String newEmailId,String phone, Long contactId) {
		try{
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.find(loyalty.getUserId());
			String orgName = user.getUserOrganization().getOrganizationName();
			if(validateEmail(newEmailId)){
				String messageStr = PropertyUtil.getPropertyValueFromDB("ltyEmailAddTemplate");
				if(messageStr== null || messageStr.isEmpty()) return;
				messageStr = messageStr.replace("[toStr]", newEmailId);

				EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
				EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
				EmailQueue email = new EmailQueue(null, Constants.EQ_TYPE_LOYALTY_OC_ALERTS  , messageStr, Constants.EQ_STATUS_ACTIVE, newEmailId, user, MyCalendar.getNewCalendar(), orgName+": Your Phone Number Updated Successfully!", contactId, null);
				//emailQueueDao.saveOrUpdate(email);
				emailQueueDaoForDML.saveOrUpdate(email);
			}
			else return;
			if(!user.isEnableSMS()) return;

			String message = PropertyUtil.getPropertyValueFromDB("ltyEmailAddSmsMsgTemplate");
			if(message== null || message.isEmpty()) return;
			message = message.replace("[orgName]", orgName);
			message = message.replace("[toStr]", newEmailId);
			
			//validate loyalty phone no and new phone no
			Map<String, Object> resultMap = null;

			resultMap = validateMobile(user,phone);
			String newPhone = (String) resultMap.get("phone"); 
			boolean phoneIsValid = (Boolean) resultMap.get("isValid");

			if(phoneIsValid){
				sendSmsAlert(user, newPhone, message);
			}
		}catch(Exception e){
			logger.error("Exception in sending alerts", e);
		}
	}

	
	
	
	private static boolean validateEmail(String email) {
		if (email ==null || email.trim().isEmpty()) {
			return false;
			
		} else if (!Utility.validateEmail(email.trim())) {
			return false;
		}
		return true;
	}
	
	
	
	public static Map<String,Object> validateMobile(Users user, String phone) {
		Map<String, Object> resultMap =  new HashMap<String, Object>();
		phone = phone.trim();
		UserOrganization organization=  user!=null ? user.getUserOrganization() : null ;
		phone = Utility.phoneParse(phone,organization);
		if(organization.isRequireMobileValidation()){
		if(phone != null &&	!phone.startsWith(user.getCountryCarrier().toString()) && phone.length() >= user.getUserOrganization().getMinNumberOfDigits() && phone.length() <=user.getUserOrganization().getMaxNumberOfDigits()) {
				phone = user.getCountryCarrier().toString()+phone;
			}
		}
			try{
				Long.parseLong(phone);
				resultMap.put("phone", phone);
				resultMap.put("isValid", true);
			}catch (Exception e) {
				resultMap.put("phone", phone);
				resultMap.put("isValid", false);
				return resultMap;
		}
		return resultMap;
	}

	public static void sendSmsAlert(Users user, String phone, String message) {

		try{

			OCSMSGateway ocGateway = GatewayRequestProcessHelper.getOcSMSGateway(user, 
					SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
			CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)ServiceLocator.getInstance().getBeanByName("captiwayToSMSApiGateway");
			if(!ocGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocGateway, 1)) {
				return;
			}

			if(!(((user.getSmsCount() == null ? 0 : user.getSmsCount()) - (user.getUsedSmsCount() == null ? 0 : user.getUsedSmsCount())) >=  1)) {
				return;
			}

			String msgContent= "";
			String messageHeader = findMessageHeader(user);

			if(messageHeader == ""){
				msgContent= message;
			}
			else{
				msgContent= messageHeader+message;
			}

			String senderId = ocGateway.getSenderId();
			
			try {
				captiwayToSMSApiGateway.sendSingleSms(ocGateway, msgContent,
						phone, senderId,null);
				/**
				 * Update the Used SMS count
				 */
				try{
					//UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
					UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
					//usersDao.updateUsedSMSCount(user.getUserId(), 1);
					usersDaoForDML.updateUsedSMSCount(user.getUserId(), 1);
				}catch(Exception exception){
					logger.error("Exception while updating the Used SMS count",exception);
				}
				
				/**
				 * Update Sms Queue
				 */
				SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
				smsQueueHelper.updateSMSQueue(phone,msgContent,Constants.SMS_MSG_TYPE_ALERTSMS , user, senderId);
				
				
			} catch (Exception e) {
				logger.error("Exception While sending OTP SMS ",e);
			}

			return;
		}catch(Exception e){
			logger.error("Exception in sending sms alert ...", e);
			return;
		}
	}

	private static String findMessageHeader(Users user){

		String messageHeader = Constants.STRING_NILL;
		try{

			List<SMSSettings> smsSettings = null;
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName("smsSettingsDao");

			if(SMSStatusCodes.optOutFooterMap.get(user.getCountryType())) {

				smsSettings = smsSettingsDao.findByUser(user.getUserId());
				if(smsSettings != null) {
					SMSSettings optinSettings = null;
					SMSSettings optOutSettings = null;
					SMSSettings helpSettings = null;

					for (SMSSettings eachSMSSetting : smsSettings) {

						if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
						else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
						else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;

					}
					if(optinSettings != null && messageHeader.isEmpty()) messageHeader = optinSettings.getMessageHeader();
					else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
					else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
				}

			}
		}catch(Exception e){
			logger.error("Exception in find message header...", e);
			return messageHeader;
		}
		return messageHeader;
	}

	public static void sendLowCardsThresholdAlerts(Users user, LoyaltyProgram prgmObj, boolean checkVirtual){
		try{
			logger.debug("Entered sendLowCardsThresholdAlerts ");
			
			if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)||prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC)) return;
			
			LoyaltyThresholdAlertsDao loyaltyThresholdAlertsDao = (LoyaltyThresholdAlertsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO);
			LoyaltyThresholdAlertsDaoForDML loyaltyThresholdAlertsDaoForDML = (LoyaltyThresholdAlertsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO_FOR_DML);
			LoyaltyThresholdAlerts alertsObj = loyaltyThresholdAlertsDao.findByUserId(user.getUserId());

			if(alertsObj != null){
				if(alertsObj.getEnableAlerts() == OCConstants.FLAG_NO) return;

				if(alertsObj.getCountType() == null || alertsObj.getCountType().isEmpty() || alertsObj.getCountValue() == null || alertsObj.getCountValue().isEmpty()) return;

				String emailId = alertsObj.getAlertEmailId() == null ? "" : alertsObj.getAlertEmailId().trim();
				String phone = alertsObj.getAlertMobilePhn() == null ? "" : alertsObj.getAlertMobilePhn().trim();
				Calendar currentDate = Calendar.getInstance();
				Calendar lastSentDate = alertsObj.getEnrollAlertLastSentDate();

				logger.debug("dbCurrentDate "+lastSentDate);

				int dateDiff = 0;
				if(lastSentDate != null){
					logger.debug("currentDate.getTimeInMillis()"+currentDate.getTimeInMillis());
					logger.debug(" dbCurrentDate.getTimeInMillis())"+ lastSentDate.getTimeInMillis());
					dateDiff = (int)((currentDate.getTimeInMillis() - lastSentDate.getTimeInMillis())/(1000*60));
				}

				logger.debug("dateDiff  "+dateDiff);

				if(dateDiff > 24*60 || lastSentDate == null) {
					LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
					List<LoyaltyCardSet> activeCardSets = null;
					if(checkVirtual){
						activeCardSets = loyaltyCardSetDao.findActiveByProgramId(prgmObj.getProgramId());
					}
					else{
						activeCardSets = loyaltyCardSetDao.findActiveSetByProgramId(prgmObj.getProgramId());
					}
					if(activeCardSets == null) return;
					String cardSetIdStr = null;
					for(LoyaltyCardSet cardSet : activeCardSets){
						if(cardSetIdStr == null){
							cardSetIdStr = ""+cardSet.getCardSetId();
						}
						else{
							cardSetIdStr += ","+cardSet.getCardSetId();
						}
					}

					LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
					Long totalInvCards = loyaltyCardsDao.getCardsCountByCardSetId(prgmObj.getProgramId(),cardSetIdStr, true);
					Long tresholdCount = null;

					if(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE.equalsIgnoreCase(alertsObj.getCountType())){
						Long totalCards = loyaltyCardsDao.getCardsCountByCardSetId(prgmObj.getProgramId(),cardSetIdStr, false);
						Long percentValue = Long.parseLong(alertsObj.getCountValue());
						tresholdCount = (percentValue * totalCards)/100;
					}
					else if(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_VALUE.equalsIgnoreCase(alertsObj.getCountType())){
						tresholdCount = Long.parseLong(alertsObj.getCountValue());
					}

					if(totalInvCards <= tresholdCount){
						if(emailId != null && !emailId.isEmpty()) {
							String messageStr = PropertyUtil.getPropertyValueFromDB("lowCardThresholdEnrollmentTemplate");
							messageStr = messageStr.replace("[fname]", user.getFirstName() == null ? "" : user.getFirstName());
							messageStr = messageStr.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_USERNAME, Utility.getOnlyUserName(user.getUserName()));
							messageStr = messageStr.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_ORGID, Utility.getOnlyOrgId(user.getUserName()));
							messageStr = messageStr.replace("[programName]", prgmObj.getProgramName());
							messageStr = messageStr.replace("[noofcards]", totalInvCards+"");

							EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
							EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
							EmailQueue email = new EmailQueue("OptCulture Alert: Running Low On Loyalty Cards", messageStr, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, Constants.EQ_STATUS_ACTIVE, emailId, MyCalendar.getNewCalendar(), user);
							//emailQueueDao.saveOrUpdate(email);
							emailQueueDaoForDML.saveOrUpdate(email);
							alertsObj.setEnrollAlertLastSentDate(Calendar.getInstance());
							//loyaltyThresholdAlertsDao.saveOrUpdate(alertsObj);
							loyaltyThresholdAlertsDaoForDML.saveOrUpdate(alertsObj);
							logger.debug("--------loyalty alert date-------------"+alertsObj.getEnrollAlertLastSentDate());
						}
						if(phone != null && !phone.isEmpty()){
							if(!user.isEnableSMS()) return;

							String message = PropertyUtil.getPropertyValueFromDB("lowCardThresholdEnrollmentSmsTemplate");
							message = message.replace("[programName]", prgmObj.getProgramName());
							message = message.replace("[noofcards]", totalInvCards+"");

							//validate loyalty phone no and new phone no
							Map<String, Object> resultMap = null;

							resultMap = validateMobile(user, phone);
							phone = (String) resultMap.get("phone");
							boolean phoneIsValid = (Boolean) resultMap.get("isValid");

							if(phoneIsValid){
								sendSmsAlert(user, phone, message);
							}
							alertsObj.setEnrollAlertLastSentDate(Calendar.getInstance());
							//loyaltyThresholdAlertsDao.saveOrUpdate(alertsObj);
							loyaltyThresholdAlertsDaoForDML.saveOrUpdate(alertsObj);

							logger.debug("--------loyalty alert date-------------"+alertsObj.getEnrollAlertLastSentDate());
						}
					}
				}
			}
			logger.debug("Exited sendLowCardsThresholdAlerts ");
		}catch(Exception e){
			logger.error("Exception in sending loyalty threshold alerts:::",e);
		}
		

	}//sendLoyaltyThresholdAlerts()
	public static String preparePhoneNumber(Users user,String phoneNumber) {//APP-1208
		//TODO ReplaceAll
		String countryCode=user.getCountryCarrier()+"";
		int countryCodeLength=countryCode.length();
		if(phoneNumber.startsWith(countryCode) && phoneNumber.length()>= (user.getUserOrganization().getMaxNumberOfDigits()+countryCodeLength) ){
			
				
			phoneNumber=phoneNumber.substring(countryCodeLength);
		}
		return phoneNumber;
		
	}
	
	public static Double getLPV(ContactsLoyalty contactsLoyalty) throws Exception{
		Double totPurchaseValue = null;
		Double cummulativePurchaseValue = contactsLoyalty.getCummulativePurchaseValue() == null ? 0.0 : contactsLoyalty.getCummulativePurchaseValue();
		Double cummulativeReturnValue = contactsLoyalty.getCummulativeReturnValue() == null ? 0.0 : contactsLoyalty.getCummulativeReturnValue();
		totPurchaseValue = (new BigDecimal(cummulativePurchaseValue-cummulativeReturnValue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		//totPurchaseValue = cummulativePurchaseValue-cummulativeReturnValue;
		logger.debug("totPurchaseValue =="+totPurchaseValue);
		return totPurchaseValue;
	}
	
	//make a central logic for all common operations
	public void updateContactLoyaltyBalances(Double purchaseamount, Double earnedAmount, Double earnedPoints, String valueCode,
			ContactsLoyalty contactsLoyalty) throws Exception {
		
		if(purchaseamount!=null){
		if(contactsLoyalty.getCummulativePurchaseValue() == null) {
			contactsLoyalty.setCummulativePurchaseValue(purchaseamount);
		}
		else {
			contactsLoyalty.setCummulativePurchaseValue(contactsLoyalty.getCummulativePurchaseValue() + purchaseamount);
		}
		}
		
		/*if(contactsLoyalty.getLifeTimePurchaseValue() == null) {
			
			contactsLoyalty.setLifeTimePurchaseValue(purchaseamount);
			
		}else {
			contactsLoyalty.setLifeTimePurchaseValue(contactsLoyalty.getLifeTimePurchaseValue() + purchaseamount);
		}*/
		
		//for special reward
		if (valueCode != null && valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {

			if (contactsLoyalty.getLoyaltyBalance() == null) {
				// contactsLoyalty.setLoyaltyBalance(earned);
				contactsLoyalty.setLoyaltyBalance(earnedPoints);
			} else {
				// contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() +
				// earned);
				contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() + earnedPoints);
			}
			if (contactsLoyalty.getTotalLoyaltyEarned() == null) {
				// contactsLoyalty.setTotalLoyaltyEarned(earned);
				contactsLoyalty.setTotalLoyaltyEarned(earnedPoints);
			} else {
				// contactsLoyalty.setTotalLoyaltyEarned(contactsLoyalty.getTotalLoyaltyEarned()
				// + earned);
				contactsLoyalty.setTotalLoyaltyEarned(contactsLoyalty.getTotalLoyaltyEarned() + earnedPoints);
			}

		} else if (valueCode != null && valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT) || valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY)) {
			if (contactsLoyalty.getGiftcardBalance() == null) {
				// contactsLoyalty.setGiftcardBalance(earned);
				contactsLoyalty.setGiftcardBalance(earnedAmount);
			} else {
				// contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() +
				// earned);
				/*
				 * String result =
				 * Utility.truncateUptoTwoDecimal(contactsLoyalty.getGiftcardBalance() +
				 * earnedAmount); logger.info("GiftCard balance:::"+result);
				 * contactsLoyalty.setGiftcardBalance(Double.parseDouble(result));
				 */
				contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + earnedAmount)
						.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			if (contactsLoyalty.getTotalGiftcardAmount() == null) {
				// contactsLoyalty.setTotalGiftcardAmount(earned);
				contactsLoyalty.setTotalGiftcardAmount(earnedAmount);
			} else {
				// contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount()
				// + earned);y
				/*
				 * String res =
				 * Utility.truncateUptoTwoDecimal(contactsLoyalty.getTotalGiftcardAmount() +
				 * earnedAmount);
				 * contactsLoyalty.setTotalGiftcardAmount(Double.parseDouble(res));
				 */
				contactsLoyalty
						.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + earnedAmount)
								.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}

		}

	
		
	}
	
	/**
	 * 
	 * @param contactsLoyalty
	 * @param beforeLPV
	 * @param afterLPV
	 * @param earnedlevel
	 * @param bonus
	 * @return multiplier -1= don't apply on current reaching level; 0=apply on current reaching level; >0 = apply multiplier on recurring level 
	 */
	public static int doIssueBonus(ContactsLoyalty contactsLoyalty,Double beforeLPV, Double afterLPV, Double earnedlevel, LoyaltyThresholdBonus bonus	){

		int multiplier = -1;//to make sure that in every match the value is returning 
		try {
			
			Double lastThresholdReachlevelDbl=0.0;
			String lastThreshold = contactsLoyalty.getLastThreshold();
			String lastThresholdToUpdate = lastThreshold; //on every bonus object modify and update to conlty
			
			if(lastThreshold == null || lastThreshold.isEmpty()){
				logger.info("Last threshold is null or empty");
				if(bonus.isRecurring()){
					logger.info("recurring");
					if((bonus.getThresholdLimit() == null || 
							(bonus.getThresholdLimit() !=null && 
							(((beforeLPV< bonus.getThresholdLimit() && afterLPV>=bonus.getThresholdLimit()) ||afterLPV<=bonus.getThresholdLimit() ))))){
						int rem=beforeLPV.intValue() % earnedlevel.intValue();
						Double addedPV=afterLPV-beforeLPV;
						multiplier=(rem+addedPV.intValue())/earnedlevel.intValue();
						if(bonus.getThresholdLimit()!=null){
							if(afterLPV>bonus.getThresholdLimit()){
								logger.info("limit not null and LPV crossed limit");
								multiplier=(int) ((rem+addedPV.intValue())-(afterLPV-bonus.getThresholdLimit()))/earnedlevel.intValue();
							}
						}
						if(multiplier>0){
							logger.info("setting last threshold");
							lastThresholdToUpdate = earnedlevel+Constants.DELIMETER_DOUBLECOLON+multiplier;
							contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
							return multiplier;
						}else return -1;
					}
					return multiplier;
					
				}else{
					logger.info("Not recurring");
					boolean doApply = (beforeLPV.doubleValue() < earnedlevel &&  afterLPV >= earnedlevel);
					if(doApply) {
						logger.info("setting last threshold");
						lastThresholdToUpdate = earnedlevel+Constants.STRING_NILL;
						contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
						return 0;
					}
					
					return  -1;
				}
				
			}
			String[] thresholdArr = null;
			String lastThresholdStrToken = null;
			String lastThresholdReachlevelStr = null;
			boolean didFindReaching = false;

			//String lastThreshold=contactsLoyalty.getLastThreshold();
			if(lastThreshold.contains(Constants.DELIMITER_DOUBLE_PIPE)){
				logger.info("---------contains pipe");
				thresholdArr = lastThreshold.split("\\|\\|");
				lastThresholdStrToken = thresholdArr[0];//first
				if(lastThresholdStrToken.contains(Constants.DELIMETER_DOUBLECOLON) || thresholdArr[1].contains(Constants.DELIMETER_DOUBLECOLON)){ //this is multiplier
					logger.info("---------contains colon");
					if(bonus.isRecurring()){
						logger.info("----------recurring");
						boolean didNotMatchAny = false;
						String lastThresholdToUpdateInner = null;
						String[] lastThresholdRepeatOfArr = null;
						for (String repeatOfThreshold : thresholdArr) {
							if(!repeatOfThreshold.contains(Constants.DELIMETER_DOUBLECOLON)) continue;
							
							lastThresholdRepeatOfArr = repeatOfThreshold.split(Constants.DELIMETER_DOUBLECOLON);
							
							lastThresholdReachlevelDbl = Double.parseDouble(lastThresholdRepeatOfArr[0]);
							int limit = Integer.parseInt(lastThresholdRepeatOfArr[1]);
							
							if(!lastThresholdReachlevelDbl.equals(earnedlevel)){
								didNotMatchAny = true;
								continue;
							}
							didNotMatchAny = false;
							int afterFactor = ((Double)(afterLPV/earnedlevel)).intValue();
							int beforeFactor = ((Double)(beforeLPV/earnedlevel)).intValue();
							//multiplier = afterFactor.intValue()-limit;				
							if((bonus.getThresholdLimit() == null || 
									(bonus.getThresholdLimit() !=null && (((beforeLPV< bonus.getThresholdLimit() 
									&& afterLPV>=bonus.getThresholdLimit()) ||afterLPV<=bonus.getThresholdLimit() ))))){
								int rem=beforeLPV.intValue() % earnedlevel.intValue();
								Double addedPV=afterLPV-beforeLPV;
								multiplier=(rem+addedPV.intValue())/earnedlevel.intValue();
								if(bonus.getThresholdLimit()!=null){
									if(afterLPV>bonus.getThresholdLimit()){
										logger.info("limit not null and LPV crossed limit");
										multiplier=(int) ((rem+addedPV.intValue())-(afterLPV-bonus.getThresholdLimit()))/earnedlevel.intValue();
									}
								}
								if(multiplier > 0 && afterFactor>limit && ((beforeFactor+multiplier)>limit)){
									logger.info("setting last threshold");
									if(((beforeFactor+multiplier)>limit)){
										multiplier=beforeFactor+multiplier-limit;
									}
									lastThresholdToUpdateInner = earnedlevel+Constants.DELIMETER_DOUBLECOLON+(limit+multiplier);
									lastThresholdToUpdate = lastThreshold.replace(repeatOfThreshold, lastThresholdToUpdateInner);
									contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
									return multiplier;
								
							}else return -1;
							}else return -1;
						}
						
						if(didNotMatchAny) {
							//to fix repeat of issue
							//Double afterFactor = afterLPV/earnedlevel;
							//multiplier = afterFactor.intValue();
							
							if((bonus.getThresholdLimit() == null || 
									(bonus.getThresholdLimit() !=null &&
									(((beforeLPV< bonus.getThresholdLimit() && afterLPV>=bonus.getThresholdLimit()) 
											||afterLPV<=bonus.getThresholdLimit() ))))){
								int rem=beforeLPV.intValue() % earnedlevel.intValue();
								Double addedPV=afterLPV-beforeLPV;
								multiplier=(rem+addedPV.intValue())/earnedlevel.intValue();
								if(bonus.getThresholdLimit()!=null){
									if(afterLPV>bonus.getThresholdLimit()){
										logger.info("limit not null and LPV crossed limit");
										multiplier=(int) ((rem+addedPV.intValue())-(afterLPV-bonus.getThresholdLimit()))/earnedlevel.intValue();
									}
								}
								if(multiplier>0){
									logger.info("setting last threshold");
									lastThresholdToUpdate = lastThreshold+Constants.DELIMITER_DOUBLE_PIPE+earnedlevel+Constants.DELIMETER_DOUBLECOLON+multiplier;
									contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
									return multiplier;
								}else return -1;
							}else return -1;
							
						}
						
					}else{
						if(!lastThresholdStrToken.contains(Constants.DELIMETER_DOUBLECOLON)){
							
							lastThresholdReachlevelDbl = Double.parseDouble(lastThresholdStrToken);
							didFindReaching = true;
						}
						if(!bonus.isRecurring() ){
							logger.info("Not recurring");
							if(didFindReaching ){
								if(afterLPV> lastThresholdReachlevelDbl && 
									lastThresholdReachlevelDbl<earnedlevel &&
									afterLPV >= earnedlevel &&  
									beforeLPV.doubleValue() < earnedlevel ){
								
									//changing as the last threshold is updating only reaching value but not multipier
									//lastThresholdToUpdate = (earnedlevel+Constants.STRING_NILL);//(lastThreshold.replace(lastThreshold.substring(0,lastThreshold.indexOf(Constants.DELIMITER_DOUBLE_PIPE)), Constants.STRING_NILL));
									lastThresholdToUpdate = (earnedlevel+Constants.STRING_NILL)+(lastThreshold.replace(lastThreshold.substring(0,lastThreshold.indexOf(Constants.DELIMITER_DOUBLE_PIPE)), Constants.STRING_NILL));
									logger.info("setting last threshold");
									contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
									
									return 0;
								}
							}else{
								if(afterLPV >= earnedlevel &&  
										beforeLPV.doubleValue() < earnedlevel ){
									lastThresholdToUpdate = (earnedlevel+Constants.STRING_NILL)+Constants.DELIMITER_DOUBLE_PIPE+lastThreshold;//(lastThreshold.replace(lastThreshold.substring(0,lastThreshold.indexOf(Constants.DELIMITER_DOUBLE_PIPE)), Constants.STRING_NILL));
									logger.info("setting last threshold");
									contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
									
									return 0;
								}
							
							}
								
						}
						
					}
				}else{
					lastThresholdReachlevelStr = lastThresholdStrToken;
					lastThresholdReachlevelDbl = Double.parseDouble(lastThresholdReachlevelStr);
					if(!bonus.isRecurring() && 
							afterLPV> lastThresholdReachlevelDbl && 
							lastThresholdReachlevelDbl<earnedlevel &&
							afterLPV >= earnedlevel &&  
							beforeLPV.doubleValue() < earnedlevel ){
						logger.info("setting last threshold");
						lastThresholdToUpdate = (earnedlevel+Constants.STRING_NILL)+(lastThreshold.replace(lastThreshold.substring(0,lastThreshold.indexOf(Constants.DELIMITER_DOUBLE_PIPE)), Constants.STRING_NILL));
						contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
						
						
						return 0;
					}
				
					return -1;
				}
				
				
			}else{
				 didFindReaching = false;
				boolean didFindRepeatOf = false;
				int limit = 0;
				if(!lastThreshold.contains(Constants.DELIMETER_DOUBLECOLON)){
					lastThresholdReachlevelStr = lastThreshold;
					lastThresholdReachlevelDbl = Double.parseDouble(lastThresholdReachlevelStr);
					didFindReaching = true;
				}else{
					
					String[] lastThresholdRepeatOfArr = lastThreshold.split(Constants.DELIMETER_DOUBLECOLON);
					
					lastThresholdReachlevelDbl = Double.parseDouble(lastThresholdRepeatOfArr[0]);
					 limit = Integer.parseInt(lastThresholdRepeatOfArr[1]);
					didFindRepeatOf = true;
				}
				//**************************
				if(!bonus.isRecurring() ){
					
					if(didFindReaching ){
						if(afterLPV> lastThresholdReachlevelDbl && 
							lastThresholdReachlevelDbl<earnedlevel &&
							afterLPV >= earnedlevel &&  
							beforeLPV.doubleValue() < earnedlevel ){
							logger.info("setting last threshold");
							lastThresholdToUpdate = (earnedlevel+Constants.STRING_NILL);//(lastThreshold.replace(lastThreshold.substring(0,lastThreshold.indexOf(Constants.DELIMITER_DOUBLE_PIPE)), Constants.STRING_NILL));
							contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
							
							return 0;
						}
					}else{
						if(afterLPV >= earnedlevel &&  
								beforeLPV.doubleValue() < earnedlevel ){
							logger.info("setting last threshold");
							lastThresholdToUpdate = (earnedlevel+Constants.STRING_NILL)+Constants.DELIMITER_DOUBLE_PIPE+lastThreshold;//(lastThreshold.replace(lastThreshold.substring(0,lastThreshold.indexOf(Constants.DELIMITER_DOUBLE_PIPE)), Constants.STRING_NILL));
							contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
							
							return 0;
						}
					
					}
						
				}else{
					if(didFindRepeatOf ){
						if(!lastThresholdReachlevelDbl.equals(earnedlevel)){
						//	Double afterFactor = afterLPV/earnedlevel;
						//	multiplier = afterFactor.intValue();
							
							if((bonus.getThresholdLimit() == null || (bonus.getThresholdLimit() !=null && (((beforeLPV< bonus.getThresholdLimit() && afterLPV>=bonus.getThresholdLimit()) ||afterLPV<=bonus.getThresholdLimit() ))))){
								int rem=beforeLPV.intValue() % earnedlevel.intValue();
								Double addedPV=afterLPV-beforeLPV;
								multiplier=(rem+addedPV.intValue())/earnedlevel.intValue();
								if(bonus.getThresholdLimit()!=null){
									if(afterLPV>bonus.getThresholdLimit()){
										logger.info("limit not null and LPV crossed limit");
										multiplier=(int) ((rem+addedPV.intValue())-(afterLPV-bonus.getThresholdLimit()))/earnedlevel.intValue();
									}
								}
								if(multiplier>0){
								lastThresholdToUpdate = lastThreshold+Constants.DELIMITER_DOUBLE_PIPE+earnedlevel+Constants.DELIMETER_DOUBLECOLON+multiplier;
								logger.info("setting last threshold");
								contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
								return multiplier;
								}else return -1;
							}else return -1;
						}else{
							int afterFactor = ((Double)(afterLPV/earnedlevel)).intValue();
							int beforeFactor = ((Double)(beforeLPV/earnedlevel)).intValue();
							//multiplier = afterFactor.intValue()-limit;
							if( (bonus.getThresholdLimit() == null || (bonus.getThresholdLimit() !=null && (((beforeLPV< bonus.getThresholdLimit() && afterLPV>=bonus.getThresholdLimit()) || afterLPV<=bonus.getThresholdLimit() ))))){
								int rem=beforeLPV.intValue() % earnedlevel.intValue();
								Double addedPV=afterLPV-beforeLPV;
								multiplier=(rem+addedPV.intValue())/earnedlevel.intValue();
								//int limitFactor = ((Double)(bonus.getThresholdLimit()/earnedlevel)).intValue();
								//if(afterFactor > limitFactor) afterFactor = limitFactor;
								if(bonus.getThresholdLimit()!=null){
									if(afterLPV>bonus.getThresholdLimit()){
										logger.info("limit not null and LPV crossed limit");
										multiplier=(int) ((rem+addedPV.intValue())-(afterLPV.intValue()-bonus.getThresholdLimit()))/earnedlevel.intValue();
									}
								}
								if(afterFactor>limit && multiplier > 0 && ((beforeFactor+multiplier)>limit)){
									logger.info("setting last threshold");
									if(((beforeFactor+multiplier)>limit)){
										multiplier=beforeFactor+multiplier-limit;
									}
									lastThresholdToUpdate = earnedlevel+Constants.DELIMETER_DOUBLECOLON+(limit+multiplier);
									contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
									return multiplier;
								}else return -1;
								
							}else return -1;
						}
						
					}else{
						
						/*Double afterFactor = afterLPV/earnedlevel;
						multiplier = afterFactor.intValue();*/
						if( (bonus.getThresholdLimit() == null || (bonus.getThresholdLimit() !=null && (((beforeLPV< bonus.getThresholdLimit() && afterLPV>=bonus.getThresholdLimit()) || afterLPV<=bonus.getThresholdLimit() ))))){
							int rem=beforeLPV.intValue() % earnedlevel.intValue();
							Double addedPV=afterLPV-beforeLPV;
							multiplier=(rem+addedPV.intValue())/earnedlevel.intValue();
							if(bonus.getThresholdLimit()!=null){
								if(afterLPV>bonus.getThresholdLimit()){
									logger.info("limit not null and LPV crossed limit");
									multiplier=(int) ((rem+addedPV.intValue())-(afterLPV.intValue()-bonus.getThresholdLimit()))/earnedlevel.intValue();
								}
							}
							if(multiplier > 0){
								logger.info("setting last threshold");
								lastThresholdToUpdate = lastThreshold+Constants.DELIMITER_DOUBLE_PIPE+earnedlevel+Constants.DELIMETER_DOUBLECOLON+(multiplier);
								contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
								return multiplier;
							}else return -1;
							
						}else return -1;
					}
					
					
				}
					/*return -1;
					if(bonus.isRecurring()){
						
						
						String[] lastThresholdRepeatOfArr = lastThreshold.split(Constants.DELIMETER_DOUBLECOLON);
						
						lastThresholdReachlevelDbl = Double.parseDouble(lastThresholdRepeatOfArr[0]);
						int limit = Integer.parseInt(lastThresholdRepeatOfArr[1]);
						
						if(!lastThresholdReachlevelDbl.equals(earnedlevel)){
							Double afterFactor = afterLPV/earnedlevel;
							multiplier = afterFactor.intValue();
							
							if(multiplier>0){
								lastThresholdToUpdate = earnedlevel+Constants.DELIMETER_DOUBLECOLON+multiplier;
								contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
							}
							return multiplier;
						}
						
						Double afterFactor = afterLPV/earnedlevel;
						multiplier = afterFactor.intValue()-limit;
						if(multiplier > 0){
						
							lastThresholdToUpdate = earnedlevel+Constants.DELIMETER_DOUBLECOLON+multiplier;
							contactsLoyalty.setLastThreshold(lastThresholdToUpdate);
							
						}
						return multiplier;
					
					}
					
				*/
			}
								
			logger.info("last threshold after bonus in helper----"+contactsLoyalty.getLastThreshold()==null?0:contactsLoyalty.getLastThreshold());
	
		} catch (Exception e) {
			logger.error("Exception in...", e);
		}
		
		return multiplier;
	}
	public static long calculateMultiplier(ContactsLoyalty contactsLoyalty, Double beforeCPV, Double afterCPV, Double earnedlevel){
	//	Double rem = beforeCPV % earnedlevel;
		Double CRV = contactsLoyalty.getCummulativeReturnValue()==null ? 0 :contactsLoyalty.getCummulativeReturnValue();
		Double beforeLPV = beforeCPV - CRV;
		Double addedPV = afterCPV-beforeCPV;
		Double amountToIgnore = contactsLoyalty.getAmountToIgnore()==null? 0 :contactsLoyalty.getAmountToIgnore();
		Double rem = (amountToIgnore==0)?(beforeLPV % earnedlevel):(beforeCPV%earnedlevel);
		logger.info("remainder ----- "+rem);


		if(amountToIgnore!= null && amountToIgnore>0){
			double valueLeft =rem+addedPV-contactsLoyalty.getAmountToIgnore();
			if(valueLeft >0){
				long multiplier	=(long)(valueLeft/earnedlevel);
				/*if(multiplier>0){
					//contactsLoyalty.setAmountToIgnore(0.0);
				}*/
				return multiplier;
			}else{
				//contactsLoyalty.setAmountToIgnore(contactsLoyalty.getAmountToIgnore()-addedPV);
				return 0;
			}
			
			
		}
		return (long)((addedPV+rem)/earnedlevel) ;
		
		
		
	}
	public static  LoyaltyProgram findLoyaltyProgramByProgramId(Long programId, Long userId) throws Exception {
		
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findByIdAndUserId(programId, userId);
	}
	
	public static  LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getTierById(tierId);
		
	}
	public static String getNextTierName(Long programId,Long tierId) {
		LoyaltyProgramTierDao loyaltyProgramTierDao;
		try {
			loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			List<LoyaltyProgramTier> tierList= null;
			tierList = loyaltyProgramTierDao.getTierListByPrgmIdAsc(programId);
			if(tierList==null) return null;
	        ListIterator tierIterator = tierList.listIterator(); 

			while(tierIterator.hasNext()) {
				if(tierId.equals(((LoyaltyProgramTier) tierIterator.next()).getTierId()))
					if(tierIterator.hasNext())		
						return ((LoyaltyProgramTier) tierIterator.next()).getTierName();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		
		
		
		return null;
	}
	
	public static double calculatePointsAmount(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier) throws Exception {
		
		if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
				&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0){
		
			double factor = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
			int intFactor = (int)factor;
			return tier.getConvertToAmount() * intFactor;
			
		}
		else return 0.0;
	}
	public static String pointsEarnedInLastTrx(ContactsLoyalty contactsLoyalty) {
		
		try {
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			
			LoyaltyTransactionChild issuance = loyaltyTransactionChildDao.findLastPurchaseTransaction(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId());
			
			if(issuance != null){
				Calendar createDate = issuance.getCreatedDate();
				Calendar now = Calendar.getInstance();
				long hours = ChronoUnit.HOURS.between(now.toInstant(), createDate.toInstant());
				if(hours <= 3){
					return issuance.getEarnedPoints() != null ? issuance.getEarnedPoints().longValue()+"" : "0";
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return "";
	}
// escapade
	public static boolean checkRewardGainedAlready(ContactsLoyalty contactsLoyalty, String docSid) {

		try {
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);

			return loyaltyTransactionChildDao.findARewardInLast12Hours(contactsLoyalty.getUserId(),
					OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE, contactsLoyalty.getProgramId(),
					contactsLoyalty.getLoyaltyId(), docSid); // (contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(),
														// contactsLoyalty.getLoyaltyId());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);

		}
		return false;
	}

	public static String[] pointsEarnedIncurrent(ContactsLoyalty contactsLoyalty, String docsid) {
		String[] balArr = new String[3];
		balArr[0] = "0";
		balArr[1] = "0.0";
		balArr[2] = "0";
		try {
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);

			List<LoyaltyTransactionChild> trxList = loyaltyTransactionChildDao.findByDocSIDAndUserId(
					contactsLoyalty.getUserId(), docsid, OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,
					contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()); // (contactsLoyalty.getUserId(),
																						// contactsLoyalty.getProgramId(),
																						// contactsLoyalty.getLoyaltyId());
			int pointsEarnedToday = 0;
			double currEarnedToday = 0;
			int pointsRedeemedToday = 0;
			if (trxList != null && !trxList.isEmpty()) {
				for (LoyaltyTransactionChild loyaltyTransactionChild : trxList) {

					if (loyaltyTransactionChild.getTransactionType()
							.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)) {
						
						pointsRedeemedToday += (loyaltyTransactionChild.getPointsDifference() == null
								|| loyaltyTransactionChild.getPointsDifference().isEmpty()) ? 0
										: (int)(Double.parseDouble(loyaltyTransactionChild.getPointsDifference()));
						continue;
					}

					pointsEarnedToday += (loyaltyTransactionChild.getPointsDifference() == null
							|| loyaltyTransactionChild.getPointsDifference().isEmpty()) ? 0
									: (int)(Double.parseDouble(loyaltyTransactionChild.getPointsDifference()));
					currEarnedToday += (loyaltyTransactionChild.getAmountDifference() == null
							|| loyaltyTransactionChild.getAmountDifference().isEmpty()) ? 0
									: Double.parseDouble(loyaltyTransactionChild.getAmountDifference());
				}
				balArr[0] = pointsEarnedToday < 0 ? "0" : pointsEarnedToday + Constants.STRING_NILL;
				balArr[1] = currEarnedToday < 0 ? "0" : Utility.truncateUptoTwoDecimal(currEarnedToday);
				balArr[2] = Math.abs(pointsRedeemedToday) +  Constants.STRING_NILL;
				return balArr;
				/*
				 * Calendar createDate = issuance.getCreatedDate(); Calendar now =
				 * Calendar.getInstance(); long hours =
				 * ChronoUnit.HOURS.between(now.toInstant(), createDate.toInstant()); if(hours
				 * <= 3){ return issuance.getEarnedPoints() != null ?
				 * issuance.getEarnedPoints().longValue()+"" : "0"; }
				 */
			}
		} catch (Exception e) {
			logger.error("Exception ", e);
			// TODO Auto-generated catch block
		}
		return balArr;
	}

	public static List<LoyaltyBalance> getOtherBalances(Long userID, Long loyaltyID) throws Exception{
		LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
		return loyaltyBalanceDao.findBy(userID, loyaltyID);
	
	}
	
	public static List<LoyaltyBalance> getOnlyRewards(Long userID, Long loyaltyID, Long orgID) throws Exception{
		LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
		return loyaltyBalanceDao.findOnlyRewardsBy(userID, loyaltyID, orgID);
		
	}
	public static void main(String args[]){
		String lastTh="2000.0||1000::1";
		String[] thresholdArr = null;
		thresholdArr=lastTh.split("\\|\\|");
		logger.info("1st element --"+thresholdArr[0]);
		logger.info("2nd element --"+thresholdArr[1]);
		
	}
	
	 public static LoyaltyProgramTier checkElelegibilityOfTierUpgrade(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier currTier, Double totPurchaseValue) throws Exception{

			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

			List<LoyaltyProgramTier> tempTiersList = loyaltyProgramTierDao.fetchTiersByProgramId(contactsLoyalty.getProgramId());
			if (tempTiersList == null || tempTiersList.size() <= 0) {
				return currTier;
			}
			else if (tempTiersList.size() >= 1) {
				Collections.sort(tempTiersList, new Comparator<LoyaltyProgramTier>() {
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
			
			List<LoyaltyProgramTier> tiersList = new ArrayList<LoyaltyProgramTier>();
			boolean flag = false;
			for(LoyaltyProgramTier tier : tempTiersList) {
				if(currTier.getTierType().equalsIgnoreCase(tier.getTierType())){
					flag = true;
				}
				if(flag){
					tiersList.add(tier);
				}
			}

			Iterator<LoyaltyProgramTier> iterTier = tiersList.iterator();
			Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
			LoyaltyProgramTier prevtier = null;
			LoyaltyProgramTier nexttier = null;

			while(iterTier.hasNext()){
				nexttier = iterTier.next();
				if(currTier.getTierType().equals(nexttier.getTierType())){
					eligibleMap.put(nexttier, null);
				}
				else{
					if((Integer.valueOf(prevtier.getTierType().substring(5))+1) 
							== Integer.valueOf(nexttier.getTierType().substring(5)) && prevtier.getTierUpgdConstraintValue() != null){
						eligibleMap.put(nexttier, prevtier);
					}
				}
				prevtier = nexttier;
			}
			
			if(totPurchaseValue == null || totPurchaseValue <= 0){
				return currTier;
			}
			else{
				Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
				LoyaltyProgramTier prevKeyTier = null;
				LoyaltyProgramTier nextKeyTier = null;
				while(it.hasNext()){
					nextKeyTier = it.next();
					if(currTier.getTierType().equalsIgnoreCase(nextKeyTier.getTierType())){
						prevKeyTier = nextKeyTier;
						continue;
					}
					if(totPurchaseValue > 0 && totPurchaseValue < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
						if(prevKeyTier == null){
							return currTier;
						}
						return prevKeyTier;
					}
					else if (totPurchaseValue > 0 && totPurchaseValue >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
						return nextKeyTier;
					}
					prevKeyTier = nextKeyTier;
				}
				return currTier;
				
			}
		
		}
	public static Double getRoundedPurchaseAmount(String roundingType, double netPurchaseAmount){
		

		
		Double purchaseAmount = Math.floor(netPurchaseAmount);
		logger.info("value of the total reward before ceil/floor===="+netPurchaseAmount);
		
		if(roundingType!=null  && roundingType.toString().equalsIgnoreCase("Up")){
			
			logger.info("value of the total reward in ceil===="+netPurchaseAmount);

			
			purchaseAmount =  Math.ceil(netPurchaseAmount);

		}else if(roundingType!=null  && roundingType.toString().equalsIgnoreCase("Down")) {
			
			logger.info("value of the total reward in floor===="+netPurchaseAmount);

			
			purchaseAmount =  Math.floor(netPurchaseAmount);
		}else if(roundingType!=null  && roundingType.toString().equalsIgnoreCase("Near")){
			
			purchaseAmount =  (double)Math.round(netPurchaseAmount);
		}
		return purchaseAmount;
	
	}
	
	public static long getRoundedPoints(String roundingType, double earnedPoints){
		
		if(earnedPoints < 1) return 0;
		
		long points =  (long) Math.floor(earnedPoints);
		logger.info("value of the total reward before ceil/floor===="+earnedPoints);
		
		if(roundingType!=null  && roundingType.toString().equalsIgnoreCase("Up")){
			
			logger.info("value of the total reward in ceil===="+earnedPoints);

			
			points = (long) Math.ceil(earnedPoints);

		}else if(roundingType!=null  && roundingType.toString().equalsIgnoreCase("Down")) {
			
			logger.info("value of the total reward in floor===="+earnedPoints);

			
			points = (long) Math.floor(earnedPoints);
		}else if(roundingType!=null  && roundingType.toString().equalsIgnoreCase("Near")){
			
			points = (long) Math.round(earnedPoints);
		}
		return points;
	}
	
	public static String generateSessionID(String userName, String orgID, String deviceID, String cardNumber, Users user){
		
		try {
			String sessionID = userName + Constants.ADDR_COL_DELIMETER +
					orgID + Constants.ADDR_COL_DELIMETER + deviceID + Constants.ADDR_COL_DELIMETER + cardNumber+Constants.ADDR_COL_DELIMETER+ System.nanoTime();

			/*List<String> md5List = Utility.couponGenarationCode(sessionID + System.nanoTime(), 8);
			Iterator<String> iterator = md5List.iterator();
			String encryptedSessionID = null;
			while (iterator.hasNext()) {

				encryptedSessionID = iterator.next();
				break;

			}*/
			logger.debug("sessionID ==="+sessionID);
			String encryptedSessionID = EncryptDecryptLtyMembshpPwd.encryptSessionID(sessionID);
			logger.debug("encryptedSessionID ==="+encryptedSessionID);
			LoyaltyMemberSessionID loyaltyMemberSessionID = new LoyaltyMemberSessionID(encryptedSessionID, 
					Calendar.getInstance(),  OCConstants.LOYALTY_MEMBER_SESSION_STATUS_ACTIVE, user.getUserOrganization().getUserOrgId(),
					deviceID, user.getUserId(),cardNumber );
			
			//find and save to DB
			LoyaltyMemberSessionIDDao loyaltyMemberSessionIDDao = (LoyaltyMemberSessionIDDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTYMEMBERSESSIONID_DAO);
			
			LoyaltyMemberSessionIDDaoForDML loyaltyMemberSessionIDDaoForDML = (LoyaltyMemberSessionIDDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTYMEMBERSESSIONID_DAO_FOR_DML);
			loyaltyMemberSessionIDDaoForDML.deleteBy(loyaltyMemberSessionID);
			loyaltyMemberSessionIDDaoForDML.saveOrUpdate(loyaltyMemberSessionID);
			
			return encryptedSessionID;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return null;
	}
	
	public static boolean checkAuthentication(String memPwd, String encryptedPwd) throws Exception {  
		if(!BCrypt.checkpw(memPwd, encryptedPwd)){
			return false;
		}
		return true;
	}
	
	public static String getCardFromSesstionID(String sessionID) throws Exception{
		String sessionToekns =  EncryptDecryptLtyMembshpPwd.decryptSessionID(sessionID);
		
		String[] sessionTokensArr = sessionToekns.split(Constants.ADDR_COL_DELIMETER);
		String username = sessionTokensArr[0];
		String orgID = sessionTokensArr[1];
		String cardNumber = sessionTokensArr[3];
		String deviceID = sessionTokensArr[2];
		
		return cardNumber;
		
	}
	public static boolean invalidateSessionID(String sessionID) throws Exception {
		
		LoyaltyMemberSessionIDDaoForDML loyaltyMemberSessionIDDaoForDML = (LoyaltyMemberSessionIDDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTYMEMBERSESSIONID_DAO_FOR_DML);
		String sessionToekns =  EncryptDecryptLtyMembshpPwd.decryptSessionID(sessionID);
		
		String[] sessionTokensArr = sessionToekns.split(Constants.ADDR_COL_DELIMETER);
		String username = sessionTokensArr[0];
		String orgID = sessionTokensArr[1];
		String cardNumber = sessionTokensArr[3];
		String deviceID = sessionTokensArr[2];
		
		String userName = username+Constants.USER_AND_ORG_SEPARATOR+orgID;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		
		Users user = usersDao.findByUsername(userName);
		
		int deleted = loyaltyMemberSessionIDDaoForDML.deleteBy(user.getUserId(),user.getUserOrganization().getUserOrgId(), cardNumber, deviceID, sessionID);
		logger.debug("deleted==="+deleted);
		return deleted>0 ;
	}
	public static LoyaltyMemberSessionID validateSessionID(String sessionID) throws Exception{
		
		String sessionToekns =  EncryptDecryptLtyMembshpPwd.decryptSessionID(sessionID);
		
		String[] sessionTokensArr = sessionToekns.split(Constants.ADDR_COL_DELIMETER);
		String username = sessionTokensArr[0];
		String orgID = sessionTokensArr[1];
		String cardNumber = sessionTokensArr[3];
		String deviceID = sessionTokensArr[2];
		
		String userName = username+Constants.USER_AND_ORG_SEPARATOR+orgID;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		
		Users user = usersDao.findByUsername(userName);
		
		LoyaltyMemberSessionIDDao loyaltyMemberSessionIDDao = (LoyaltyMemberSessionIDDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTYMEMBERSESSIONID_DAO);
		return loyaltyMemberSessionIDDao.findBy(user.getUserId(),user.getUserOrganization().getUserOrgId(), cardNumber, deviceID );	
	}
	
	public static String getDateFormatFor(String OCAttribute, String mappingType, Long userId){
		String dateFormat = MyCalendar.FORMAT_DATETIME_STYEAR;
		 List<POSMapping> DateFieldsMappings = getDateFormatFor(mappingType, userId);
		 if(DateFieldsMappings ==null || DateFieldsMappings.isEmpty()) return dateFormat;
		 for (POSMapping posMapping : DateFieldsMappings) {
			if(posMapping.getCustomFieldName().equals(OCAttribute)){
				String dateformat = posMapping.getDataType().substring(posMapping.getDataType().indexOf("(") + 1,
						posMapping.getDataType().indexOf(")"));
				return dateformat;
			}
		}
		 return dateFormat;
		
	}
	public static List<POSMapping> getDateFormatFor( String mappingType, Long userId){
		
		try {
			POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
			return posMappingDao.findDateMappings(mappingType, userId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static boolean isActivateAfterAllowed(String homeStoreId, LoyaltyProgramTier loyaltyProgramTier) {
	
		if(homeStoreId == null || homeStoreId.isEmpty()) {
			return true;
		}
		logger.info("tier"+loyaltyProgramTier);
		logger.info("loyalty program tier"+loyaltyProgramTier.getDisallowActivateAfterStores());
		if(loyaltyProgramTier.getDisallowActivateAfterStores()==null || 
				loyaltyProgramTier.getDisallowActivateAfterStores().isEmpty()) {
			return true;//is this means for all stores its activated?
		}
	
		//Name were used in opposite sense, it should be allowed a for activate after
		String disallowActivateAfterStores = loyaltyProgramTier.getDisallowActivateAfterStores(); 
		String disallowActivateAfterStoresArray[] = disallowActivateAfterStores.split(OCConstants.FORM_MAPPING_SPLIT_DELIMETER);
		List<String> disallowActivateAfterStoresArrayList = Arrays.asList(disallowActivateAfterStoresArray);
		Set<String> StoresAllowedSet = new HashSet<String>();
		StoresAllowedSet.addAll(disallowActivateAfterStoresArrayList);
		if(disallowActivateAfterStoresArrayList.contains(homeStoreId) || loyaltyProgramTier.getActivateAfterDisableAllStore()) {
			return true; //so the selected stores are online
		}
		return false;//Yes
		
	}
		
		
}
