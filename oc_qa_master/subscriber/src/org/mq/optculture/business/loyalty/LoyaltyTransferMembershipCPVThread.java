package org.mq.optculture.business.loyalty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.model.ocloyalty.LoyaltyTransferMembershipJsonRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyTransferMembershipCPVThread implements Runnable{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private List<LoyaltyProgramTier> tiersList;
	private Map<LoyaltyProgramTier,LoyaltyProgramTier> eligibleMap;
	private Users user;
	private ContactsLoyalty sourceMembership, destMembership;
	private boolean isForSource, isDestNew;
	//private LoyaltyTransactionChild transChild;
	private LoyaltyProgram program;
	private LoyaltyTransferMembershipJsonRequest transferRequest;
	
	
	public LoyaltyTransferMembershipCPVThread(Map<LoyaltyProgramTier,LoyaltyProgramTier> eligibleMap, Users user, 
			ContactsLoyalty sourceMembership, ContactsLoyalty destMembership, boolean isForSource, boolean isDestNew, List<LoyaltyProgramTier> tierList, LoyaltyProgram program, LoyaltyTransferMembershipJsonRequest transferRequest) {
		this.eligibleMap = eligibleMap;
		this.user = user;
		this.sourceMembership = sourceMembership;
		this.destMembership = destMembership;
		this.tiersList = tierList;
		//this.transChild = transChild;
		this.program = program;
		this.transferRequest = transferRequest;
		this.isForSource = isForSource;
		this.isDestNew = isDestNew;
	}

	@Override
	public void run() {

		try {
			logger.debug("isForSource "+isForSource);
			LoyaltyProgramTier tier =  (isForSource ? findTier(sourceMembership) : findTier(destMembership));
			logger.debug("tier "+tier == null ? tier : tier.getTierId());
			if(isForSource && tier != null) {
				logger.debug("====isForSource case 1=====");
				//check the dest tier and update the
				if(isDestNew) {//for a inventory card source tier == dest tier
					logger.debug("====isForSource case 1====="+isDestNew);
					//destMembership.setProgramTierId(tier.getTierId());
					//destMembership.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
					//condition added for cross program card transfer
					if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
						LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
						LoyaltyProgramTier destinationTier = null;
						destinationTier = loyaltyProgramTierDao.getTierByPrgmAndType(destMembership.getProgramId(), OCConstants.LOYALTY_PROGRAM_TIER1);
						saveContactLoyalty(destMembership, destinationTier.getTierId());
					}else{
						saveContactLoyalty(destMembership, tier.getTierId());
					}
				}
				else{//determine the tier because we skipped dest tier determination & finding highest tier in tansfer wen source tier== null
					if(destMembership.getProgramTierId() != null) {//find just highest tier among these 2 n set it to dest
						logger.debug("====isForSource case 1 destMembership.getProgramTierId() != null=====");
						LoyaltyProgramTier destTier = getLoyaltyTier(destMembership.getProgramTierId());
						//LoyaltyProgramTier highestTier = findHighestTier(tier, destTier);
						//condition added for cross program card transfer
						if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
							destTier = findHighestTier(tier, destTier);
						}
						logger.debug("====isForSource case 1  highestTier====="+ tier.getTierType()+" "+destTier.getTierType());
						//destMembership.setProgramTierId(highestTier.getTierId());
						//destMembership.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
						saveContactLoyalty(destMembership, destTier.getTierId());
						
					}else{
						logger.debug("====isForSource case 1 destMembership.getProgramTierId() == null=====");
						ContactsLoyalty membership = destMembership;
						Long contactId = membership.getContact().getContactId();
						Long userID = user.getUserId();
						List<LoyaltyProgramTier> tierList = getSortedTierList(program.getProgramId(), userID);
						if(tierList == null || tierList.size() == 0 || !OCConstants.LOYALTY_PROGRAM_TIER1.equals(tierList.get(0).getTierType())){
							
							
							
						}
						
						//Prepare eligible tiers map
						Iterator<LoyaltyProgramTier> iterTier = tierList.iterator();
						
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
						LoyaltyProgramTier destTier = findTier(contactId, userID, membership, tierList, eligibleMap);
						//LoyaltyProgramTier highestTier = findHighestTier(tier, destTier);
						if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
							destTier = findHighestTier(tier, destTier);
						}
						logger.debug("====isForSource case 1 destMembership.getProgramTierId() == null=====highestTier"+tier.getTierType()+ " "+destTier.getTierType());
						//destMembership.setProgramTierId(highestTier.getTierId());
						//destMembership.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
						saveContactLoyalty(destMembership, destTier.getTierId());
					}
				}
				
			}else if(!isForSource && tier != null) {
				LoyaltyProgramTier destTier = getLoyaltyTier(destMembership.getProgramTierId());
				//LoyaltyProgramTier highestTier = findHighestTier(tier, destTier);
				//condition added for cross program card transfer
				if(sourceMembership.getProgramId().longValue() == destMembership.getProgramId().longValue()){
					destTier = findHighestTier(tier, destTier);
				}
				logger.debug("====isForSource case 2 destMembership.getProgramTierId() == null=====highestTier"+tier.getTierType()+ " "+destTier.getTierType());
				//destMembership.setProgramTierId(highestTier.getTierId());
				//destMembership.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
				saveContactLoyalty(destMembership, destTier.getTierId());
				
				
			}
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
	}
	private LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getTierById(tierId);
		
	}
	
	private LoyaltyProgramTier findHighestTier(LoyaltyProgramTier sourceTier, LoyaltyProgramTier destTier) {
		
		List<LoyaltyProgramTier> TierList = new ArrayList<LoyaltyProgramTier>();
		TierList.add(sourceTier);
		TierList.add(destTier);
		
		Collections.sort(TierList, new Comparator<LoyaltyProgramTier>() {
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
		
		return TierList.get(tiersList.size()-1);
	}
	
	
	private LoyaltyProgramTier findTier(Long contactId, Long userId, ContactsLoyalty contactsLoyalty,
			List<LoyaltyProgramTier> tiersList, Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap) throws Exception {

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
		else if(contactId == null){
			logger.info("contactId is null and selected tier..."+tiersList.get(0).getTierType());
			return tiersList.get(0);
		}
		else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);

			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);				

			//List<Map<String, Object>> contactPurcahseList = contactsDao.findContactPurchaseDetails(userId, contactId);
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
		}else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			
			return findTier(destMembership);		
		}
		else{
			return null;
		}
	}
	
	private List<LoyaltyProgramTier> getSortedTierList(Long programId, Long contactId) throws Exception {
		try{
			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

			List<LoyaltyProgramTier> tiersList = loyaltyProgramTierDao.fetchTiersByProgramId(programId);
			if (tiersList == null || tiersList.size() <= 0) {
				logger.info("Tiers list is empty...");
				return null;
			}
			else if (tiersList.size() >= 1) {//sort tiers by tiertype i.e Tier 1, Tier 2, and so on.
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
			return tiersList;
		}catch(Exception e){
			logger.error("Exception in validating tiersList::", e);
			return null;
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
	
	private Contacts findContactById(Long cid) throws Exception {
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactsDao.findById(cid);
	}
	
	
	private void saveContactLoyalty(ContactsLoyalty loyalty, Long tierID) {
		try {
			logger.info("------------saveContactLoyalty::"+loyalty.getLoyaltyId()+ " "+tierID);
			//check the latest values are updating or not
			ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			loyaltyDao.updateTier(loyalty.getLoyaltyId(), tierID);//saveOrUpdate(loyalty);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
	}

	private LoyaltyProgramTier findTier(ContactsLoyalty membership){
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
				logger.info("contactId = "+membership.getContact().getContactId()+" startDate = "+startDate+" endDate = "+endDate);

				/*Object[] cumulativeAmountArr = getCumulativeValue(startDate, endDate, membership);

				cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());*/
				
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				cumulativeAmount = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(membership.getUserId(), membership.getProgramId(), membership.getLoyaltyId(), startDate, endDate));

				if(cumulativeAmount == null || cumulativeAmount <= 0){
					logger.info("cumulative purchase value is empty...");
					continue;
				}
				
				if(cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
					return nextKeyTier;
				}
				
			}
			
			return tiersList.get(0);
		}catch(Exception e){
			logger.error("Excepion in cpv thread ", e);
			return tiersList.get(0);
		}
	}
	
	private Object[] getCumulativeValue(String startDate, String endDate, ContactsLoyalty membership) throws Exception {

		RetailProSalesDao salesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
		Object[] cumulativeAmountArr = salesDao.getCumulativePurchase(user.getUserId(), membership.getContact().getContactId(), startDate, endDate);
		return cumulativeAmountArr;
	}
}
