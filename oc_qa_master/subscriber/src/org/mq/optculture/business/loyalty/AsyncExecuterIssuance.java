package org.mq.optculture.business.loyalty;


import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyMemberItemQtyCounter;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDao;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyMemberItemQtyCounterDao;
import org.mq.marketer.campaign.dao.LoyaltyMemberItemQtyCounterDaoforDML;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.data.dao.SpecialRewardsDao;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.Items;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.SkuDetails;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;
import com.kenai.constantine.Constant;

public class AsyncExecuterIssuance extends Thread{

	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private LoyaltyProgram loyaltyProgram;
	private LoyaltyIssuanceRequest issuanceRequest;
	private Double itemExcludedAmount;
	private LoyaltyProgramTier tier;
	private ContactsLoyalty contactsLoyalty;
	private Users user;
	private ResponseHeader responseHeader;
	private LoyaltyCards loyaltyCard;
	private String flag="";

	public AsyncExecuterIssuance() {

	}

	public AsyncExecuterIssuance(LoyaltyProgram loyaltyProgram, LoyaltyIssuanceRequest issuanceRequest,
			Double itemExcludedAmount, LoyaltyProgramTier tier, ContactsLoyalty contactsLoyalty, Users user,
			ResponseHeader responseHeader, LoyaltyCards loyaltyCard,String flag) {
		this.loyaltyProgram = loyaltyProgram;
		this.issuanceRequest = issuanceRequest;
		this.itemExcludedAmount = itemExcludedAmount;
		this.tier = tier;
		this.contactsLoyalty = contactsLoyalty;
		this.user = user;
		this.responseHeader = responseHeader;
		this.loyaltyCard = loyaltyCard;
		this.flag=flag;
		
	}

	@Override
	public void run() {

		try {
			if (flag.equalsIgnoreCase(OCConstants.Issuance_DB_Operations)) {
				IssuanceDBOperations(loyaltyProgram, issuanceRequest, itemExcludedAmount, contactsLoyalty, user,
						responseHeader);
			} else if (flag.equalsIgnoreCase(OCConstants.Issuance_DB_PERK_Operations)) {
				IssuancePerkDBOperations(loyaltyProgram, issuanceRequest, contactsLoyalty, user,
						responseHeader);
			}  else if (flag.equalsIgnoreCase(OCConstants.Issuance_DB_Gift_Activated_Operations)) {
				IssuanceDBGiftActivatedOperations(contactsLoyalty, responseHeader, issuanceRequest);
			} else if (flag.equalsIgnoreCase(OCConstants.Issuance_DB_Gift_Inventory_Operations)){
				IssuanceDBGiftInventoryOperations(loyaltyProgram, issuanceRequest, contactsLoyalty, user,
						responseHeader, loyaltyCard);
			}
		} catch (Exception e) {
			logger.error("e===>"+e);
		}

	}

	public void performIssuance(){
		
		try {
			if (flag.equalsIgnoreCase(OCConstants.Issuance_DB_Operations)) {
				IssuanceDBOperations(loyaltyProgram, issuanceRequest, itemExcludedAmount, contactsLoyalty, user,
						responseHeader);
			} else if (flag.equalsIgnoreCase(OCConstants.Issuance_DB_PERK_Operations)) {
				IssuancePerkDBOperations(loyaltyProgram, issuanceRequest, contactsLoyalty, user,
						responseHeader);
			} else if (flag.equalsIgnoreCase(OCConstants.Issuance_DB_Gift_Activated_Operations)) {
				IssuanceDBGiftActivatedOperations(contactsLoyalty, responseHeader, issuanceRequest);
			} else if (flag.equalsIgnoreCase(OCConstants.Issuance_DB_Gift_Inventory_Operations)){
				IssuanceDBGiftInventoryOperations(loyaltyProgram, issuanceRequest, contactsLoyalty, user,
						responseHeader, loyaltyCard);
			}
		} catch (Exception e) {
			logger.error("e===>",e);
		}

	}
	void IssuanceDBGiftActivatedOperations(ContactsLoyalty contactsLoyalty,ResponseHeader responseHeader,LoyaltyIssuanceRequest issuanceRequest)throws Exception{
		saveContactsLoyalty(contactsLoyalty);
		LoyaltyTransactionChild tranx = createGiftTransaction(responseHeader.getTransactionId(), issuanceRequest, contactsLoyalty, "0", issuanceRequest.getAmount().getEnteredValue());
		
		createExpiryTransaction(contactsLoyalty, 0L, Double.valueOf(issuanceRequest.getAmount().getEnteredValue()), 
			tranx.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, null,null);
	
	}
	void IssuanceDBOperations(LoyaltyProgram loyaltyProgram, LoyaltyIssuanceRequest issuanceRequest,
			Double itemExcludedAmount, ContactsLoyalty contactsLoyalty, Users user,
			ResponseHeader responseHeader) throws Exception {
		
		//Changes ks
		try {
		Boolean isSpecialRewardExist = false;
		boolean ignoreIssuance = false;
		SpecialRewardsDao specialRewardsDao = (SpecialRewardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPECIAL_REWARDS_DAO);
		LoyaltyMemberItemQtyCounterDao loyaltyMemberItemQtyCounterDao =(LoyaltyMemberItemQtyCounterDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO);
		LoyaltyMemberItemQtyCounterDaoforDML loyaltyMemberItemQtyCounterDaoForDml =(LoyaltyMemberItemQtyCounterDaoforDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO_FOR_DML);
		LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
		LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
		
		Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
		List<SpecialReward>   itemBasedAutoComm= new ArrayList<SpecialReward>();
		List<SpecialReward>   VisistsBasedAutoComm= new ArrayList<SpecialReward>();
		List<SpecialReward>   OthersBasedAutoComm= new ArrayList<SpecialReward>();
		
		Double fromLtyBalance = contactsLoyalty.getTotalLoyaltyEarned();
		Double fromAmtBalance = contactsLoyalty.getTotalGiftcardAmount();
		Double fromCPVBalance = contactsLoyalty.getCummulativePurchaseValue();
		
		Double fromLPVBalance = LoyaltyProgramHelper.getLPV(contactsLoyalty);
		
		List<SpecialReward> spList=null;
		SpecialReward highestMultiplierReward = null;
		List<String> rewardRule=null;
		double multiplier=1l; 
		
		//APP-3925
		String cummulativeprchaseStr = issuanceRequest.getAmount().getPurchaseValue();
		Double actualPurchaseValue = 0.0;
		if(cummulativeprchaseStr!=null && !cummulativeprchaseStr.isEmpty() && 
				Double.valueOf(cummulativeprchaseStr)>0 && !loyaltyProgram.getRewardType().equals(OCConstants.REWARD_TYPE_PERK))
			actualPurchaseValue = Double.parseDouble(Utility
					.truncateUptoTwoDecimal(Double.valueOf(cummulativeprchaseStr)));
		
		Double purchaseAmountdbl = 0.0;
		String purchaseAmountinDec = Utility
				.truncateUptoTwoDecimal(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()));
		if (purchaseAmountinDec != null)
			purchaseAmountdbl = Double.parseDouble(purchaseAmountinDec);
		if(user.isIgnoreissuanceOnRedemption() && 
				issuanceRequest.getAmount().getIgnoreIssuance() != null && 
				issuanceRequest.getAmount().getIgnoreIssuance().equals("Y")) {
			purchaseAmountdbl =0.0;
		}
		if(loyaltyProgram.getRewardType().equals(OCConstants.REWARD_TYPE_PERK)) {
			purchaseAmountdbl =0.0;
		}
		Double netPurchaseAmountdbl = purchaseAmountdbl;
		

		// long netPurchaseAmount = Math.round(netPurchaseAmountdbl);
		double netPurchaseAmount = netPurchaseAmountdbl;
		logger.info("netPurchaseAmount from enteredamount = " + netPurchaseAmount);
		/*if(loyaltyProgram.getConsiderRedeemedAmountFlag()== OCConstants.FLAG_YES && 
				issuanceRequest.getAmount().getReceiptAmount() != null){
			
			String receiptAmountinDec = Utility
					.truncateUptoTwoDecimal(Double.valueOf(issuanceRequest.getAmount().getReceiptAmount()));
			
			if (receiptAmountinDec != null){
				double receiptAmountdbl = Double.parseDouble(receiptAmountinDec);
				netPurchaseAmount = receiptAmountdbl;
			}
			
		}

		logger.info("netPurchaseAmount from receipt amount= " + netPurchaseAmount);*/
		
		LoyaltyProgramTier tier = null; //getLoyaltyTier(contactsLoyalty.getProgramTierId());
		LoyaltyProgramTier currtier = null; 
		if(contactsLoyalty.getProgramTierId() != null) {
			tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());	
		}
		else{
			Long contactId = null;
			if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
				contactId = contactsLoyalty.getContact().getContactId();
				List<LoyaltyProgramTier> tierList = validateTierList(contactsLoyalty.getProgramId(), contactsLoyalty.getUserId());
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
				// TODO in pending case w.r.t SP. 
				tier = findTier(contactId, contactsLoyalty.getUserId(),contactsLoyalty, tierList,eligibleMap);
				
				if(tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
					contactsLoyalty.setProgramTierId(tier.getTierId());
					saveContactsLoyalty(contactsLoyalty);
				}
				else if(tier != null && "Pending".equalsIgnoreCase(tier.getTierType())){
					LoyaltyIssuanceCPVThread cpvThread = new LoyaltyIssuanceCPVThread(eligibleMap, user, contactId,	tierList,
							contactsLoyalty.getLoyaltyId(), loyaltyProgram, issuanceRequest, itemExcludedAmount, responseHeader.getTransactionId());
					Thread th = new Thread(cpvThread);
					th.start();
					
					//MembershipResponse response = prepareAccountIssuanceResponse(contactsLoyalty, null, loyaltyProgram);
					//List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
					//contactLoyaltyList.add(contactsLoyalty);
					//List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
					//TODO check if balances need to be added
				}
			}
		}
		currtier = tier;
		//check the elegibility for tier upgrade if its LPV
		if(tier != null && (tier.getMultipleTierUpgrdRules()==null || tier.getMultipleTierUpgrdRules().isEmpty()) && tier.getTierUpgdConstraint() != null && 
				OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tier.getTierUpgdConstraint())){
			Double totPurchaseValue = fromLPVBalance;
			totPurchaseValue+= netPurchaseAmount;
			LoyaltyProgramTier newTier = LoyaltyProgramHelper
					.checkElelegibilityOfTierUpgrade(contactsLoyalty, tier, totPurchaseValue);
			if (!newTier.getTierType().equalsIgnoreCase(tier.getTierType())) {
				tier = newTier;
			}
			
		} else if(tier.getMultipleTierUpgrdRules()!=null && !tier.getMultipleTierUpgrdRules().isEmpty()) {
			
			//checking if multiple tier upgrd has life time purchase value (LPV) and Single purchase value
			Double LPV = fromLPVBalance;
			LPV+= netPurchaseAmount;
			Double enteredValue = 0.0;
			enteredValue+= netPurchaseAmount;
			LoyaltyProgramTier newTier = LoyaltyProgramHelper
					.checkElelegibilityOfMultipleTierUpgrade(contactsLoyalty, tier, enteredValue,LPV);
			if (newTier!=null && !newTier.getTierType().equalsIgnoreCase(tier.getTierType())) {
				tier = newTier;
			}
			
			/*String[] listOfRules = tier.getMultipleTierUpgrdRules().split("\\|\\|");
			boolean isLPV = false;
			
			for(String rule : listOfRules) {
				
				String[] listOfAttributes = rule.split(":");
				
				String type = listOfAttributes[0];
				String value = listOfAttributes[1];
				
				if(type!=null && !type.isEmpty() && value!=null && !value.isEmpty() && 
					       type.equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE)) {
					
					isLPV = true;
					
				}
				
			}
			
			if(isLPV) {
				
				Double totPurchaseValue = fromLPVBalance;
				totPurchaseValue+= netPurchaseAmount;
				LoyaltyProgramTier newTier = LoyaltyProgramHelper
						.checkElelegibilityOfMultipleTierUpgrade(contactsLoyalty, tier, totPurchaseValue);
				if (!newTier.getTierType().equalsIgnoreCase(tier.getTierType())) {
					tier = newTier;
				}
				
			}*/
			
		}
		//APP-3666
		if(tier.getConsiderRedeemedAmountFlag()== OCConstants.FLAG_YES && 
				issuanceRequest.getAmount().getReceiptAmount() != null){
			
			String receiptAmountinDec = Utility
					.truncateUptoTwoDecimal(Double.valueOf(issuanceRequest.getAmount().getReceiptAmount()));
			
			if (receiptAmountinDec != null){
				double receiptAmountdbl = Double.parseDouble(receiptAmountinDec);
				netPurchaseAmount = receiptAmountdbl;
			}
			
		}

		logger.info("netPurchaseAmount from receipt amount= " + netPurchaseAmount);
		//Find Special reward in program
		spList = specialRewardsDao.findSpecialRewardInProgram(loyaltyProgram.getProgramId(), loyaltyProgram.getOrgId());
		Set<String> ExcludeItems =  new HashSet<String>();
		//Map<String, Double> ExcludeItemsMap =  new HashMap<String, Double>();
		String itemRewardInfo = null;
		if(spList!=null && !spList.isEmpty()) {
			//get those rewards which r satisfying with the current trx
			// [#ItemPurchse#];=;items:vendorCode;=;ACC;=;single;=;10<OR>||
			SkuFileDao skuFileDao = (SkuFileDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SKU_FILE_DAO);
			List<SkuDetails> itemsList = issuanceRequest.getItems();
			String itemSids = Constants.STRING_NILL;
			Map<String , SkuFile> itemsMap = new HashMap<String, SkuFile>();
			for (SkuDetails skuDetails : itemsList) {
				if(!itemSids.isEmpty()) itemSids += ",";
				if(skuDetails.getItemSID() != null && !skuDetails.getItemSID().isEmpty()) itemSids += "'"+skuDetails.getItemSID()+"'";
			}
			if(!itemSids.isEmpty()) {
				
				List<SkuFile> items = skuFileDao.findSkuByOptField(user.getUserId(), itemSids);
				if(items != null && !items.isEmpty()) {
					
					for (SkuFile skuFile : items) {
						
						itemsMap.put(skuFile.getItemSid(), skuFile);
					}
				}
				
			}
			String spStr = Constants.STRING_NILL;
			
			for (SpecialReward spReward : spList) {
			
				if(!spStr.isEmpty()) spStr += ",";
				
				spStr += spReward.getRewardId().longValue();
			}
			List<LoyaltyMemberItemQtyCounter> retList = null;
			retList= loyaltyMemberItemQtyCounterDao.findItemsCounter(spStr, contactsLoyalty.getLoyaltyId());
			Map<Long, LoyaltyMemberItemQtyCounter > itemCountSet = new HashMap<Long, LoyaltyMemberItemQtyCounter>();
			logger.debug("retList ===", retList);
			if(retList != null && !retList.isEmpty()) {
				for (LoyaltyMemberItemQtyCounter loyaltyMemberItemQtyCounter : retList) {
					
					itemCountSet.put(loyaltyMemberItemQtyCounter.getSPRuleID(), loyaltyMemberItemQtyCounter);
				}
			}
			Gson gson = new Gson();
			String issuanceJsonStr = gson.toJson(issuanceRequest);
			JSONObject issuanceJson = (JSONObject)JSONValue.parse(issuanceJsonStr);
			String spRule = null;
     		boolean isPurchaseDate = false; //APP-3492
			List<SpecialReward> itemsBasedSP = new ArrayList<SpecialReward>();
			List<SpecialReward> visitsBasedSP = new ArrayList<SpecialReward>();
			List<SpecialReward> otherSP = new ArrayList<SpecialReward>();
			Map<String , Double> matchedItemsMap = new java.util.HashMap<String, Double>();
			Map<String, Double>DiscountedItemsMap = new HashMap<String, Double>();
			Map<String, Integer>DiscountedItemsQtyMap = new HashMap<String, Integer>();
			for (SpecialReward specialReward : spList) {
				Map<String , Integer> sameitemsmap = new java.util.HashMap<String, Integer>();
				Map<String , Boolean> considerItemsMap = new java.util.HashMap<String, Boolean>();
				Map<String , Integer> itemsQtyMap = new java.util.HashMap<String, Integer>();
				Map<String , Double> itemsPriceMap = new java.util.HashMap<String, Double>();
				boolean considerthisSP = false;
				boolean isItemsBased = false;
				boolean isVisitsBased = false;
				boolean isOthers = true;
				boolean isMPV = false;
				double minPurchaseVal = 0;
				//continue if this reward is already gained within issuancewindow
				if(specialReward.getIssuanceWindow() != null && !specialReward.getIssuanceWindow().isEmpty()) {
					//find loyaltytrx child for this reward
					boolean isgainedAlready = loyaltyTransactionChildDao.findBySpecialReward(specialReward.getRewardId(), user.getUserId(), specialReward.getIssuanceWindow(),contactsLoyalty.getLoyaltyId());
						
						if(isgainedAlready) continue;
				}
				String ProductRuleType = getSPTypeOnProduct(specialReward);	//APP-4064
				
				
				
				spRule = specialReward.getRewardRule();
				boolean itemCriteriaExists = false; //APP-4064
				double totItemPrice = 0.0;
				String purchaseType = Constants.STRING_NILL;
				double qty = 0;
				boolean includeDiscount = true;
				
				
				LoyaltyMemberItemQtyCounter loyaltyMemberItemQtyCounter = null;
				int spmultiplier = 1;
				loyaltyMemberItemQtyCounter =  itemCountSet.get(specialReward.getRewardId());
				//Map<String, String> itemCounterSet = getThecounterSet(loyaltyMemberItemQtyCounter);
			//	Map<String, String> itemCounterSetToSave = new java.util.HashMap<String, String>();
				//Set<String> itemsPriceTaken = new HashSet<String>(); 
				boolean currANDFlag = true;
				//logger.info("spRule===>"+spRule);
				String[] ruleArr = spRule.split("\\|\\|");
				for (int i = 0; i < ruleArr.length; i++) {
					//logger.info("ruleArr===>"+ruleArr[i]);
					// currANDFlag = true;
					//considerthisSP = considerthisSP && considerthisSP;
					//Changes APP-2000
					boolean currFlag = false;
					String rule=ruleArr[i].trim();
					String[] subRuleTokenArr = rule.split("<OR>");
					
					for (int tokenIndex = 0;tokenIndex<subRuleTokenArr.length ;tokenIndex++) {
						//logger.info("subRuleTokenArr===>"+subRuleTokenArr);	
						
						String subRule = subRuleTokenArr[tokenIndex];
						
						boolean currORFlag = false;
						String[] ruleTokensArr = subRule.split(Constants.ADDR_COL_DELIMETER);
						String ruleHashTag = ruleTokensArr[0];
						if(ruleHashTag.equalsIgnoreCase("[#ItemFactor#]")){ //take out the qty+single/bulk + discount include / exclude
							qty = Double.parseDouble(ruleTokensArr[4]);
							purchaseType = ruleTokensArr[5];
							includeDiscount = ruleTokensArr[3].equalsIgnoreCase("I");
							String promoUsed = specialReward.getPromoCode(); //check whether the qty has free item  - APP-4064
							currFlag=true;//Change APP-2000
							if(ProductRuleType.equals("IF")) {//validate the ITem qty criteria here itself - APP-4064
								
								for (SkuDetails item : issuanceRequest.getItems()) {
									
									double itemQty = (double)Double.parseDouble(item.getQuantity());
									if(!purchaseType.equals("M") && itemQty < qty) continue; //tODO to understand what is the real case in qty accross multiple purchases
									double itemBilledPrice = (double)Double.parseDouble(item.getBilledUnitPrice());
									
									if(promoUsed != null && !promoUsed.isEmpty() && 
											specialReward.getExcludeQty() != null && 
													item.getItemNote() != null && !item.getItemNote().toString().isEmpty() && 
															item.getItemNote().toString().toLowerCase().contains(promoUsed.toLowerCase()+Constants.DELIMETER_COLON)){
										String[] itemNoteTokenArr = item.getItemNote().toString().toLowerCase().split(Constants.DELIMETER_COLON+"");
										String  qtyToken = itemNoteTokenArr[1];//.split(Constants.DELIMETER_COMMA)[0];
										Double ignoreQty = Double.parseDouble(qtyToken);
										itemQty -= ignoreQty ;//specialReward.getExcludeQty();
										considerItemsMap.put(item.getItemSID(), true);
										if(!includeDiscount) {
											DiscountedItemsMap.put(item.getItemSID().toString(), itemBilledPrice*ignoreQty);
										}
									}
									
								}
								currFlag = !considerItemsMap.isEmpty();
								isItemsBased = true;
							}
							itemCriteriaExists = true;
							continue;
						}
						
						String ruleTemplate = Utility.specialRuleHashTag.get(ruleHashTag);
						if(ruleHashTag.equalsIgnoreCase("[#PurchasedItem#]")) {
							String jsonelement = ruleTokensArr[1];
							String jsonValue = ruleTokensArr[3];
							
							isItemsBased = true;
							isVisitsBased = false;
							isOthers = false;
							String[] jsoneleArr = jsonelement.split(Constants.DELIMETER_COLON+Constants.STRING_NILL);
							String jsonParent = jsoneleArr[0];
							String jsonele = jsoneleArr[1];
							Object jsonObj = issuanceJson.get(jsonParent);
							if(jsonObj instanceof JSONArray){
								JSONArray items = (JSONArray)jsonObj;
								for (Object object : items) {
									try {
										JSONObject itemObj = (JSONObject)object;
										Object discount = itemObj.get("discount");
										Object quantity  = itemObj.get("quantity");
										Object tax = itemObj.get("tax");
										Object unitPrice = itemObj.get("billedUnitPrice");
										Object itemSID = itemObj.get("itemSID");
										Object itemNote=itemObj.get("itemNote");
										
										if((unitPrice == null || unitPrice.toString().isEmpty()) || tax == null ||
												(quantity == null || quantity.toString().isEmpty() ) ||
												discount ==null || (itemSID == null || itemSID.toString().isEmpty())) continue;
										double itemQty = (double)Double.parseDouble(quantity.toString());
										double itemBilledPrice = (double)Double.parseDouble(unitPrice.toString());//APP-4064
										String promoUsed = specialReward.getPromoCode(); //check whether the qty has free item  
										if(promoUsed != null && !promoUsed.isEmpty() && 
												specialReward.getExcludeQty() != null && 
												itemNote != null && !itemNote.toString().isEmpty() && 
												itemNote.toString().toLowerCase().contains(promoUsed.toLowerCase()+Constants.DELIMETER_COLON)){
											String[] itemNoteTokenArr = itemNote.toString().toLowerCase().split(Constants.DELIMETER_COLON+"");
											String  qtyToken = itemNoteTokenArr[1];//.split(Constants.DELIMETER_COMMA)[0];
											Double ignoreQty = Double.parseDouble(qtyToken);
											itemQty -= ignoreQty ;//specialReward.getExcludeQty();
											considerItemsMap.put(itemSID.toString(), true);
											if(ProductRuleType.equals("B") && !includeDiscount) {//APP-4064
												DiscountedItemsMap.put(itemSID.toString(), itemBilledPrice*ignoreQty);
											}
										}
										if(itemQty <= 0 ) {
											
											continue;
										}
										
										
										double discountDbl = !discount.toString().isEmpty() ? Double.parseDouble(discount.toString()) : 0.0;
										//int itemQty = Integer.parseInt(quantity.toString());
										//double taxDbl = !tax.toString().isEmpty() ? Double.parseDouble(tax.toString()) : 0.0;
										double itemUnitPrice =  Double.parseDouble(unitPrice.toString()) ;
										double itemPrice = (itemQty*itemUnitPrice);
										
										/*if(!includeDiscount) {
											if(discount != null && !discount.toString().isEmpty() && Double.parseDouble(discount.toString()) >0){
												continue; //excluded discounted items
											}
										}*/
										if(!includeDiscount) {
											if(discount != null && !discount.toString().isEmpty() && Double.parseDouble(discount.toString()) >0){
												if(DiscountedItemsMap.get(itemSID.toString()) == null){
													DiscountedItemsQtyMap.put(itemSID.toString(), (int)itemQty);
													
												}
												continue; //excluded discounted items
											}
										}
										
										itemsPriceMap.put(itemSID.toString(), itemPrice);
										String attribute = itemObj.get(jsonele)!=null ? (itemObj.get(jsonele)).toString().trim() : Constants.STRING_NILL;
										String anotherAttribute = Constants.STRING_NILL;
										String anotherJsonEle = Constants.STRING_NILL;
										//logger.debug("jsonele ==="+jsonele);
										if(Utility.ItemsAnotherFactor.containsKey(jsonele)){
											
											anotherJsonEle = Utility.ItemsAnotherFactor.get(jsonele);
											anotherAttribute = itemObj.get(anotherJsonEle) != null &&  
													!itemObj.get(anotherJsonEle).toString().isEmpty() ? itemObj.get(anotherJsonEle).toString().trim() : Constants.STRING_NILL;;
										
											//logger.debug("anotherAttribute at first level ==="+anotherAttribute);
										}	
										if(anotherAttribute.isEmpty()){
											
											
											
											SkuFile item = itemsMap.get(itemSID.toString());//skuFileDao.findRecordBy(itemSID.toString(), user.getUserId());
											if(item != null) {
												PropertyDescriptor pd  = null;
												if(Utility.ItemsFeilds.containsKey(jsonele)){
													 pd = new PropertyDescriptor(Utility.ItemsFeilds.get(jsonele), item.getClass());
																										
												}else{
													
													if(item != null && jsonele.startsWith(OCConstants.POS_MAPPING_POS_ATTRIBUTE_UDF ) ||
															 jsonele.equals(OCConstants.POS_MAPPING_POS_ATTRIBUTE_SUBSIDIARY_NUMBER) ||
															 jsonele.equals(OCConstants.POS_MAPPING_POS_ATTRIBUTE_DESCRIPTION)) {
														//TODO work for udfs with ItemSid
														
														 pd = new PropertyDescriptor(jsonele, item.getClass());
														/*Object retValue =  pd.getReadMethod().invoke(item);
														
														anotherAttribute = retValue != null && 
																! retValue.toString().trim().isEmpty() ? 
																		retValue.toString().trim() :  Constants.STRING_NILL ;*/
														
														//logger.debug("anotherAttribute at 3rd level ==="+anotherAttribute);
													} 
												}
												
												//logger.debug("anotherAttribute at 2nd level ==="+anotherAttribute);
												if(pd != null){
													
													Object retValue =  pd.getReadMethod().invoke(item);
													
													anotherAttribute = retValue != null && ! retValue.toString().trim().isEmpty() ? 
															retValue.toString().trim() :  Constants.STRING_NILL ;
												}
														
											}
										}
										logger.debug("anotherAttribute  ==="+anotherAttribute);
										currORFlag =  ((attribute.trim().equalsIgnoreCase(jsonValue) || (anotherAttribute != null && !anotherAttribute.isEmpty() ? anotherAttribute.trim().equalsIgnoreCase(jsonValue) : false) ) );
										logger.debug("anotherAttribute  ==="+anotherAttribute +" currORFlag==="+currORFlag);
										/*if(currORFlag && !itemsPriceTaken.contains(itemSID.toString())){
											itemsPriceTaken.add(itemSID.toString());
											totItemPrice = totItemPrice + itemPrice;
											if(specialReward.isDeductItemPrice())
											netPurchaseAmount = netPurchaseAmount-itemPrice;
											logger.info("totItemPrice===>"+totItemPrice);
										}
										*/
										
											
										if(considerItemsMap.containsKey(itemSID.toString())){
											considerItemsMap.put(itemSID.toString(), considerItemsMap.get(itemSID.toString()) && (currORFlag));
											
										}else{
											
											considerItemsMap.put(itemSID.toString(), currORFlag );
										}
										
										/*if(itemsQtyMap.containsKey(itemSID.toString())){
											
											itemsQtyMap.put(itemSID.toString(), itemsQtyMap.get(itemSID.toString())+itemQty);	
										}else{
											itemsQtyMap.put(itemSID.toString(),itemQty);
										}*/
										if(currORFlag && itemQty > 0){
											
											//if(purchaseType.equals("M")){}else{}
											
										}//
										
									} catch (Exception e) {
										// TODO Auto-generated catch block
										logger.error("unexpected error ===", e);
										
									}
									currFlag = currORFlag || currFlag;
								}//for each item
								
								
							}//if item array 
							//logger.debug("currORFlag ==="+currORFlag);
							currFlag = currORFlag || currFlag;
							logger.debug("currFlag ==="+currFlag);
						}//if item based sp
					
					else if(ruleHashTag.equalsIgnoreCase("[#Minimum Receipt Total#]"))
						// according to APP-4051 this should not be  on receipt total rather on the satified items total 
						
						{
							Object value	= issuanceJson.get("amount");
							JSONObject itemObj = (JSONObject)value;
				         	Object receiptAmt	= itemObj.get("receiptAmount");
							double receiptVal=  Double.parseDouble(receiptAmt.toString());
							logger.debug(receiptVal);
						//	double value=	Double.parseDouble(issuanceRequest.getAmount().getReceiptAmount());
							minPurchaseVal=Double.parseDouble(ruleTokensArr[3]);
							currORFlag  = receiptVal>=minPurchaseVal; //for item SP this must be the total of the items worth
							isMPV = true;
						}	
						
					else if(ruleHashTag.equalsIgnoreCase("[#Visit#]")) {
							int numOfVisits = loyaltyTransactionChildDao.getCountOfPurchases(user.getUserId(),loyaltyProgram.getProgramId(), 
									contactsLoyalty.getLoyaltyId(), OCConstants.LOYALTY_TYPE_PURCHASE ); 
							
							String visitType = ruleTokensArr[3];
							int number = Integer.parseInt(ruleTokensArr[4]);
							if(visitType.equals("F")) {
								
								currORFlag = ((numOfVisits+1) == number) ; 
							}else if(visitType.equals("R")) {
								
								currORFlag = ((numOfVisits+1) % number == 0); 
								
							}
							if(currORFlag) isVisitsBased = true;
							
						}else{ //others based
							try {
							if(ruleHashTag.equalsIgnoreCase("[#PuchaseDate#]")) {
								
								//time , dow, dom, date, between dates
								isItemsBased = true;
								isPurchaseDate = true;
								String dateTimeToken = ruleTokensArr[3];
								String requestDate = issuanceRequest.getHeader().getRequestDate();//2014-06-16 12:12:12
								Calendar requestCal = MyCalendar.string2Calendar(requestDate);
								
								if(dateTimeToken.equalsIgnoreCase("TOD")) {
									//APP-1990
									int startHour = (ruleTokensArr[4]!=null && !ruleTokensArr[4].isEmpty()) ? Integer.parseInt(ruleTokensArr[4]) : -1  ;
									int endHour = (ruleTokensArr[5]!=null && !ruleTokensArr[5].isEmpty()) ? Integer.parseInt(ruleTokensArr[5]) : -1  ;
									int hour = requestCal.get(Calendar.HOUR_OF_DAY);
									
									/**APP-3417
									 * Previous logic, if 16:54 time came, it will treat as 16>=15 && 16<=16, which is wrong as 
									 * clearly 16:54 is not between 3PM and 4PM.
									 * New logic if we are flooring the time anyway, then 3PM to 3:59:59PM will become 15, which is equal to 3. 
									 * So the condition should be less than endHour not less than equals 
									**/
									//currORFlag = (hour >= startHour && hour <= endHour);
									currORFlag = (hour >= startHour && hour < endHour);
									
									
								}else if(dateTimeToken.equalsIgnoreCase("DOW")) {
									int dow = requestCal.get(Calendar.DAY_OF_WEEK);
									currORFlag = (dow+Constants.STRING_NILL).equals(ruleTokensArr[4]); 
								}else if(dateTimeToken.equalsIgnoreCase("DOM")) {
									int dom = requestCal.get(Calendar.DAY_OF_MONTH);
									currORFlag = (dom+Constants.STRING_NILL).equals(ruleTokensArr[4]); 
									
								}else if(dateTimeToken.equalsIgnoreCase("Day")) {
									//APP-1990
									String date = requestDate.substring(0,requestDate.indexOf(' ')).trim();
									currORFlag = (date).equals(ruleTokensArr[4].trim());
								}else if(dateTimeToken.equalsIgnoreCase("Dates")) {
									Calendar date = MyCalendar.dateString2Calendar(requestDate.substring(0,requestDate.indexOf(' ')).trim());
									Calendar date1 = MyCalendar.dateString2Calendar(ruleTokensArr[4].trim());
									Calendar date2 = MyCalendar.dateString2Calendar(ruleTokensArr[5].trim());
									currORFlag = ( date1.before(date) && date2.after(date));
									currORFlag = currORFlag || (date1.equals(date) || date2.equals(date) );
								}else if(dateTimeToken.equalsIgnoreCase("BirthDay") || 
										dateTimeToken.equalsIgnoreCase("Anniversary") ||  
										dateTimeToken.startsWith("udf")) {
									Calendar birthday = contact.getBirthDay();
									Calendar anniversary = contact.getAnniversary();
									Calendar now = Calendar.getInstance() ;
									if((dateTimeToken.equalsIgnoreCase("BirthDay") && (now.get(Calendar.MONTH) == birthday.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) == birthday.get(Calendar.DAY_OF_MONTH)) ) || 
											(dateTimeToken.equalsIgnoreCase("Anniversary") && now.get(Calendar.MONTH) == anniversary.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) == anniversary.get(Calendar.DAY_OF_MONTH))) {
										currORFlag = true;
									}
									//try to parse the date value contact's udf to standard format n then compare with the trx date
									
									
								}
								
							}else if(ruleHashTag.equalsIgnoreCase("[#PurchaseTier#]")) {
						//APP-3674		
								currORFlag = (contactsLoyalty.getProgramId().toString().equals(ruleTokensArr[2]) && 
										tier.getTierId().toString().equalsIgnoreCase(ruleTokensArr[3])  );
							//contactsLoyalty.getProgramTierId().toString().equalsIgnoreCase(ruleTokensArr[3]
								
							}else if(ruleHashTag.equalsIgnoreCase("[#PurchaseCardSet#]")) {
								
								currORFlag = (contactsLoyalty.getProgramId().toString().equals(ruleTokensArr[2]) && 
										contactsLoyalty.getCardSetId().toString().equalsIgnoreCase(ruleTokensArr[3])  );
							}else if(ruleHashTag.equalsIgnoreCase("[#PurchaseStore#]")) {
								
								String storeNumber = issuanceRequest.getHeader().getStoreNumber();
								currORFlag = (storeNumber.equalsIgnoreCase(ruleTokensArr[3])  ); 
								
							}
							
						  }	
						catch(Exception e) {
							logger.info("Exception in others special reward block ",e);
						}
						}
						//changes APP-2000
						//if(currFlag) break;
						//logger.debug("currORFlag=="+currORFlag+" currFlag ==="+currFlag);
						currFlag = currORFlag || currFlag;
					}
					//logger.debug("currANDFlag=="+currANDFlag+" currFlag ==="+currFlag);
					currANDFlag = currANDFlag && currFlag;
					
					//if(!currANDFlag) break;//check this
				}//and rule
				boolean considerItemsMapEmpty = false;
				if(!considerItemsMap.isEmpty()){
					
					for (String key : considerItemsMap.keySet()) {
						
						if(considerItemsMap.get(key) != null && considerItemsMap.get(key)) {
							considerItemsMapEmpty = true;
							break;
						}
					}
				}
				//considerthisSP = currANDFlag && considerItemsMapEmpty;
				considerthisSP = !considerItemsMap.isEmpty() ? currANDFlag && considerItemsMapEmpty : currANDFlag;
				if(considerthisSP && isItemsBased && isMPV && considerItemsMapEmpty) { //check again on satidfied items total amount APP-4051
					int totalQty = 0;
					List<SkuDetails> items = issuanceRequest.getItems();
					String itemStr = Constants.STRING_NILL;
					String itemSIDs = Constants.STRING_NILL;
					//logger.debug("considerItemsMap ===>"+considerItemsMap.size());
					for (String itemSID : considerItemsMap.keySet()) {
						//logger.debug("itemSID ==="+itemSID);
						if(considerItemsMap.get(itemSID)){
					
							for (SkuDetails skuDetails : items) {
								if(!skuDetails.getItemSID().equals(itemSID) || 
										(DiscountedItemsQtyMap.get(skuDetails.getItemSID()) != null && 
										skuDetails.getDiscount() != null && 
										!skuDetails.getDiscount().isEmpty() && Double.parseDouble(skuDetails.getDiscount())>0)) {
									
									continue;
								}
								double itemUnitPrice =  Double.parseDouble(skuDetails.getBilledUnitPrice()) ;
								double eachItemPrice = (Double.parseDouble(skuDetails.getQuantity())*itemUnitPrice);
								totItemPrice += eachItemPrice;
							}
						}
					}
					considerthisSP = totItemPrice>=minPurchaseVal;
					totItemPrice = 0;
				}
				double itemPrice = 0.0;
				if(considerthisSP && isItemsBased && considerItemsMapEmpty){
					logger.debug("considerthisSP  ==="+considerthisSP );
					int totalQty = 0;
					List<SkuDetails> items = issuanceRequest.getItems();
					String itemStr = Constants.STRING_NILL;
					String itemSIDs = Constants.STRING_NILL;
					//logger.debug("considerItemsMap ===>"+considerItemsMap.size());
					for (String itemSID : considerItemsMap.keySet()) {
						//logger.debug("itemSID ==="+itemSID);
						if(considerItemsMap.get(itemSID)){
					
							for (SkuDetails skuDetails : items) {
								if(!skuDetails.getItemSID().equals(itemSID) || 
										(DiscountedItemsQtyMap.get(skuDetails.getItemSID()) != null && 
										skuDetails.getDiscount() != null && 
										!skuDetails.getDiscount().isEmpty() && Double.parseDouble(skuDetails.getDiscount())>0)) {
									
									continue;
								}
								
								
								double itemQty = Double.parseDouble(skuDetails.getQuantity());
								String promoUsed = specialReward.getPromoCode(); //check whether the qty has free item  
								String itemNote = skuDetails.getItemNote();
								if(promoUsed != null && !promoUsed.isEmpty() && 
										specialReward.getExcludeQty() != null && 
										itemNote != null && !itemNote.toString().isEmpty() && 
										itemNote.toString().toLowerCase().contains(promoUsed.toLowerCase()+Constants.DELIMETER_COLON)){
									String[] itemNoteTokenArr = itemNote.toString().toLowerCase().split(Constants.DELIMETER_COLON+"");
									String  qtyToken = itemNoteTokenArr[1];//.split(Constants.DELIMETER_COMMA)[0];
									Double ignoreQty = Double.parseDouble(qtyToken);
									itemQty -= ignoreQty ;//specialReward.getExcludeQty();
								}
								
								
							//	logger.debug("itemSID ==="+skuDetails.getItemSID()+" itemQty==>"+itemQty);
								//double taxDbl = !tax.toString().isEmpty() ? Double.parseDouble(tax.toString()) : 0.0;
								double itemUnitPrice =  Double.parseDouble(skuDetails.getBilledUnitPrice()) ;
								double eachItemPrice = (Double.parseDouble(skuDetails.getQuantity())*itemUnitPrice);
								if(specialReward.isDeductItemPrice())matchedItemsMap.put(itemSID, eachItemPrice);								
								/*if(specialReward.isDeductItemPrice()){
									if(DiscountedItemsMap.containsKey(itemSID)){
										eachItemPrice -= DiscountedItemsMap.get(itemSID);
									}
									matchedItemsMap.put(itemSID, eachItemPrice);
								}*/

								totalQty += itemQty;
								totItemPrice += eachItemPrice;
								if(!itemStr.isEmpty()) itemStr += ",";
								itemStr += itemSID+Constants.DELIMETER_COLON+itemQty+Constants.DELIMETER_COLON+itemUnitPrice;
								
								if(!itemSIDs.isEmpty()) itemSIDs += ",";
								itemSIDs += itemSID;//+Constants.DELIMETER_COLON+itemQty;
								if(specialReward.isDeductItemPrice()) ExcludeItems.add(itemSID);
								/*if(specialReward.isDeductItemPrice()) {
									
									if(ExcludeItemsMap.containsKey(itemSID)){
										double existingItemPrice = ExcludeItemsMap.get(itemSID)<=totItemPrice ? ExcludeItemsMap.get(itemSID) : totItemPrice;
										ExcludeItemsMap.put(itemSID, existingItemPrice);
									}else{
										ExcludeItemsMap.put(itemSID, totItemPrice);
									}
									ExcludeItems.add(itemSID);
								}*/
							}
							
							
						}
					}
					
					/*for (String itemSID : considerItemsMap.keySet()) {
						if(considerItemsMap.get(itemSID)){
							//itemPrice += 
							totalQty += itemsQtyMap.get(itemSID);
							totItemPrice = totItemPrice + itemsPriceMap.get(itemSID);
							
						}
						
					}//for
*/					
					if(purchaseType.equals("M")){
						
						if(loyaltyMemberItemQtyCounter == null){
							loyaltyMemberItemQtyCounter = new LoyaltyMemberItemQtyCounter();
							loyaltyMemberItemQtyCounter.setLoyaltyID(contactsLoyalty.getLoyaltyId());
							loyaltyMemberItemQtyCounter.setSPRuleID(specialReward.getRewardId());
							loyaltyMemberItemQtyCounter.setUserId(user.getUserId());
							loyaltyMemberItemQtyCounter.setOrgId(user.getUserOrganization().getUserOrgId());
							
						}else{
							logger.debug("qty ==="+totalQty +"  "+loyaltyMemberItemQtyCounter.getQty());
							totalQty += loyaltyMemberItemQtyCounter.getQty();
							
						}
						double resetQty = 0;
						if( totalQty>= qty) {//APP-2041
							
							 resetQty = (totalQty)%qty;
							//spmultiplier = ((itemCounterCurrVal + itemQty)/qty)>spmultiplier ? ((itemCounterCurrVal + itemQty)/qty) : spmultiplier;//this says how many times the special reward must be given
							//logger.debug("spmultiplier===="+spmultiplier);
						}else{
							resetQty = totalQty;
							//loyaltyMemberItemQtyCounter.setQty(loyaltyMemberItemQtyCounter.getQty()+resetQty);
						}
						loyaltyMemberItemQtyCounter.setQty(resetQty);
						spmultiplier = (int)(totalQty/qty) ;
						logger.debug("resetQty ==="+resetQty +" spmultiplier=== "+spmultiplier);
						loyaltyMemberItemQtyCounterDaoForDml.saveOrUpdate(loyaltyMemberItemQtyCounter);
					}else{
						
						spmultiplier = (int)(totalQty/qty) ;
					}
					considerthisSP = spmultiplier > 0;
					specialReward.setItemsSatisfied(itemStr+Constants.ADDR_COL_DELIMETER+qty);
					if(!considerthisSP && purchaseType.equals("M")){
					//if(!considerthisSP){
						if(itemSIDs.length() > 0) {
							if(itemRewardInfo == null) itemRewardInfo="";
							
							if(itemRewardInfo.length() > 0 ) itemRewardInfo+=Constants.DELIMITER_PIPE;
							
							itemRewardInfo += specialReward.getRewardId().longValue()+Constants.ADDR_COL_DELIMETER+qty+
									Constants.DELIMETER_COLON+specialReward.getRewardValue()+Constants.DELIMETER_COLON+specialReward.getRewardValueCode()+Constants.ADDR_COL_DELIMETER+itemSIDs;
						}
					}
				}
				logger.info("considerthisSP===>"+considerthisSP+" totItemPrice===>"+totItemPrice + " netPurchaseAmount ===>"+netPurchaseAmount+" spmultiplier===>"+spmultiplier);
				/*if(loyaltyMemberItemQtyCounter != null) {
					//loyaltyMemberItemQtyCounter.setItemStr(itemCounterStrToSave);
					logger.debug("itemCounterStrToSave==="+loyaltyMemberItemQtyCounter.getItemStr());
					loyaltyMemberItemQtyCounter.setUserId(user.getUserId());
					loyaltyMemberItemQtyCounter.setOrgId(user.getUserOrganization().getUserOrgId());
					loyaltyMemberItemQtyCounterDaoForDml.saveOrUpdate(loyaltyMemberItemQtyCounter);
					
				}
				*/
				if(considerthisSP){
					//becoz among items n visits only one exists but for others case , it may exists alone and also in combination with items OR visits
					if(isItemsBased || isVisitsBased) isOthers = false; 
					if(isItemsBased  ) {
						
						specialReward.setTotItemPrice(totItemPrice);
						
						specialReward.setCountMultiplier(spmultiplier);
						itemsBasedSP.add(specialReward);
					}
					if(isVisitsBased) visitsBasedSP.add(specialReward);
				//	if(isOthers) otherSP.add(specialReward);
					if(!isItemsBased && !isVisitsBased && isOthers) {
						
						if(specialReward.getRewardType().equalsIgnoreCase("F") || specialReward.getRewardType().equalsIgnoreCase("T") ) {//APP-4642
							visitsBasedSP.add(specialReward);
						}else if(specialReward.getRewardType().equalsIgnoreCase("M")){
							otherSP.add(specialReward);
						}
					}
				}
			}
			if(!matchedItemsMap.isEmpty()){
				double totItemPrice = 0.0;
				for (String itemSID : matchedItemsMap.keySet()) {
					totItemPrice += matchedItemsMap.get(itemSID);
				}
				String totItemPriceStr = Utility
						.truncateUptoTwoDecimal(totItemPrice);
				if (totItemPriceStr != null)
					totItemPrice = Double.parseDouble(totItemPriceStr);
				if(netPurchaseAmount >= totItemPrice)netPurchaseAmount = netPurchaseAmount-(totItemPrice);
				else netPurchaseAmount = 0;
			}
			
			if(itemsBasedSP.size() > 0) {//And purchase date
				isSpecialRewardExist=true;
				for (SpecialReward itemsBased : itemsBasedSP) { // multiplier + flat
					String earnType = null;
					String earnrule = null;
					double totalReward = 0.0;
						if(tier != null && itemsBased.getRewardType().equalsIgnoreCase("M")){
							//for (int i=0 ; i<itemsBased.getCountMultiplier() ; i++) {//remove in case if its multiplier
								double earnedReward = 0.0;
							multiplier = Double.parseDouble(itemsBased.getRewardValue());
							earnType = tier.getEarnType();
														//APP-3492
													if(isPurchaseDate) { 
															if(issuanceRequest.getAmount()!=null && issuanceRequest.getAmount().getEnteredValue()!=null && !issuanceRequest.getAmount().getEnteredValue().isEmpty() ) {
																String earnReward = issuanceRequest.getAmount().getEnteredValue();
																itemsBased.setTotItemPrice(Double.parseDouble(earnReward));
															}
														}
							if (tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)) {

								//Double multipleFactordbl = LoyaltyProgramHelper.getRoundedPurchaseAmount(tier.getRoundingType(),itemsBased.getTotItemPrice()) / tier.getEarnOnSpentAmount();
								Double multipleFactordbl = itemsBased.getTotItemPrice() / tier.getEarnOnSpentAmount();
								//double multipleFactor = multipleFactordbl.intValue();
								// double multipleFactor = multipleFactordbl;
								
								//earnedReward = tier.getEarnValue() * multipleFactor;
								if(tier.getRoundingType()!=null && tier.getRoundingType().equals("Up"))
									earnedReward = tier.getEarnValue() * multipleFactordbl;//APP-4027
								else
									earnedReward = tier.getEarnValue() * (multipleFactordbl+0.001);//APP-4027
								
							} else if (tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)) {
								//APP-1997
								//earnedReward = (tier.getEarnValue() * (LoyaltyProgramHelper.getRoundedPurchaseAmount(tier.getRoundingType(),itemsBased.getTotItemPrice()) )) / 100;
								earnedReward = (tier.getEarnValue() * (itemsBased.getTotItemPrice()) ) / 100;
							}
							earnedReward = multiplier * earnedReward;
							totalReward += earnedReward;
							//}
							
							long earnedPoints = 0;
							double earnedAmount = 0.0;
							long pointsDifference = 0;
							double amountDifference = 0.0;
							String roundingType= tier.getRoundingType();
							logger.info("Rounding type of the tier is ==="+tier.getRoundingType());
							if (OCConstants.LOYALTY_TYPE_POINTS.equals(earnType)) {
								
								earnedPoints = LoyaltyProgramHelper.getRoundedPoints(roundingType, totalReward);
								
								
								/*logger.info("value of the total reward before ceil/floor===="+totalReward);
								
								if(roundingType!=null  && roundingType.toString().equalsIgnoreCase("Up")){
									
									logger.info("value of the total reward in ceil===="+totalReward);

									
									earnedPoints = (long) Math.ceil(totalReward);

								}else {
									
									logger.info("value of the total reward in floor===="+totalReward);

									
									earnedPoints = (long) Math.floor(totalReward);
								}
								*/
								//earnedPoints = (long) Math.floor(totalReward);
							} else if (OCConstants.LOYALTY_TYPE_AMOUNT.equals(earnType)) {
								// earnedAmount = (new BigDecimal(earnedValue).setScale(2,
								// BigDecimal.ROUND_DOWN)).doubleValue();
								String result = Utility.truncateUptoTwoDecimal(totalReward);
								if (result != null)
									earnedAmount = Double.parseDouble(result);
							}
							logger.info("earnedReward = " + totalReward);
							
							LoyaltyProgramHelper helper = new LoyaltyProgramHelper();
							helper.updateContactLoyaltyBalances(null, earnedAmount, (double)earnedPoints, tier.getEarnType(), contactsLoyalty);
							
							
							/*updateContactLoyaltyBalances((double) earnedAmount, (double) earnedPoints, tier.getEarnType(),
									contactsLoyalty);*/
													
							LoyaltyTransactionChild childTx = createMultiplierRewardTransaction(issuanceRequest, itemsBased.isDeductItemPrice()?itemsBased.getTotItemPrice() : 0.0, 
									contactsLoyalty, earnedAmount, earnedPoints, earnType, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD,
									user.getUserOrganization().getUserOrgId(), earnedPoints+"", earnedAmount+"", 
									responseHeader.getTransactionId(), OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED, null,
									itemExcludedAmount, 0, null, itemsBased.getRewardId(),itemsBased.getRewardRule(),itemsBased,totalReward, itemsBased.getTotItemPrice());
							
							createExpiryTransaction(contactsLoyalty, earnedPoints, (double) earnedAmount,  childTx.getTransChildId(), 
									OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,itemsBased.getRewardId(),null);
						
							
						}else if(itemsBased.getRewardType().equalsIgnoreCase("F")){
							//commented under APP-4051
							/*for (int i=0 ; i<itemsBased.getCountMultiplier() ; i++) {
								double earnedReward = 0.0;
							earnType = itemsBased.getRewardValueCode();
							earnedReward = Double.parseDouble(itemsBased.getRewardValue());
							totalReward += earnedReward;
							}*/
							double earnedReward = 0.0;
							earnType = itemsBased.getRewardValueCode();
							earnedReward = Double.parseDouble(itemsBased.getRewardValue());
							totalReward += earnedReward;
							long earnedPoints = 0;
							String roundingType= tier.getRoundingType();
							if(earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS) ||
									earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY) || earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
								logger.info("Rounding type of the tier is ==="+tier.getRoundingType());

								if (OCConstants.LOYALTY_TYPE_POINTS.equals(earnType)) {
									
									earnedPoints = LoyaltyProgramHelper.getRoundedPoints(roundingType, totalReward);

									
									/*if(roundingType!=null && roundingType.toString().equalsIgnoreCase("Up")){
										
										earnedPoints = (long) Math.ceil(totalReward);

									}else {
										
										earnedPoints = (long) Math.floor(totalReward);
									}
									*/
									
									//earnedPoints = (long) Math.floor(totalReward);
								} else if(OCConstants.LOYALTY_TYPE_CURRENCY.equals(earnType) || earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
									// earnedAmount = (new BigDecimal(earnedValue).setScale(2,
									// BigDecimal.ROUND_DOWN)).doubleValue();
									earnType = OCConstants.LOYALTY_TYPE_AMOUNT;
									
									String result = Utility.truncateUptoTwoDecimal(totalReward);
									if (result != null)
										totalReward = Double.parseDouble(result);
								}
								logger.info("earnedReward = " + totalReward);
								
								LoyaltyProgramHelper helper = new LoyaltyProgramHelper();
								//changed by shailika
								helper.updateContactLoyaltyBalances(null, totalReward, (double)earnedPoints, earnType, contactsLoyalty);
								
								LoyaltyTransactionChild childTx =  createRewardTransaction(issuanceRequest,  0.0, contactsLoyalty, totalReward, earnType, 
										OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD, user.getUserOrganization().getUserOrgId(), earnedPoints, totalReward,""+totalReward,
										responseHeader.getTransactionId(), OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED,
										"", itemExcludedAmount, 0, null, itemsBased.getRewardId(),itemsBased.getRewardRule(),itemsBased, itemsBased.getTotItemPrice());
								
								createRewardExpiryTransaction(contactsLoyalty, earnedPoints, (double) totalReward,  childTx.getTransChildId(), 
										OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,totalReward,itemsBased.getRewardValueCode(),itemsBased.getRewardId(),null,null);
							
							
								
						}		
						else{
							//double enteredAmount =  (itemsBased.isDeductItemPrice() || (!itemsBased.isDeductItemPrice() && purchaseAmountdbl==itemsBased.getTotItemPrice()))?
									//itemsBased.getTotItemPrice() : 0.0;
							String rewardEarned = Utility.truncateUptoTwoDecimal(totalReward);
							updateRewardBalances(user.getUserId(),user.getUserOrganization().getUserOrgId(), contactsLoyalty.getProgramId(),contactsLoyalty.getLoyaltyId(),
									itemsBased.getRewardValueCode(), rewardEarned,contactsLoyalty.getCardNumber());
							LoyaltyTransactionChild childTx =  createRewardTransaction(issuanceRequest, 0.0, contactsLoyalty, totalReward, earnType, 
									OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD, user.getUserOrganization().getUserOrgId(),0,0, rewardEarned,
									responseHeader.getTransactionId(), OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED,
									"", itemExcludedAmount, 0, null, itemsBased.getRewardId(),itemsBased.getRewardRule(),itemsBased, itemsBased.getTotItemPrice());
							
							createRewardExpiryTransaction(contactsLoyalty,0l, 0.0,  childTx.getTransChildId(), 
									OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,totalReward,itemsBased.getRewardValueCode(),itemsBased.getRewardId(),null,null);
						}
							
					}else if(itemsBased.getRewardType().equalsIgnoreCase("P")){
//						for (int i=0 ; i<itemsBased.getCountMultiplier() ; i++) {
//							double earnedReward = 0.0;
//						earnType = itemsBased.getRewardValueCode();
//						earnedReward = Double.parseDouble(itemsBased.getRewardValue());
//						totalReward += earnedReward;
//						}
						earnType = itemsBased.getRewardValueCode();
						long percentage = Long.parseLong(itemsBased.getRewardValue());
						long earnedPoints = 0;
						String roundingType= tier.getRoundingType();
						if(earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS) ||
								earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY) || earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
							logger.info("Rounding type of the tier is ==="+tier.getRoundingType());

							if (OCConstants.LOYALTY_TYPE_POINTS.equals(earnType)) {
								
								double mulfactor = (percentage * (itemsBased.getTotItemPrice()) ) / 100;
								earnedPoints = LoyaltyProgramHelper.getRoundedPoints(roundingType, mulfactor);
								
							} else if(OCConstants.LOYALTY_TYPE_CURRENCY.equals(earnType) || earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
								earnType = OCConstants.LOYALTY_TYPE_AMOUNT;
								
								Double mulfactor = (percentage * (itemsBased.getTotItemPrice()) ) / 100;
								String result = Utility.truncateUptoTwoDecimal(mulfactor);
								if (result != null)
									totalReward = Double.parseDouble(result);
							}
							logger.info("earnedReward = " + totalReward);
							
							LoyaltyProgramHelper helper = new LoyaltyProgramHelper();
							helper.updateContactLoyaltyBalances(null, totalReward, (double)earnedPoints, earnType, contactsLoyalty);
							
							LoyaltyTransactionChild childTx =  createRewardTransaction(issuanceRequest,  0.0, contactsLoyalty, totalReward, earnType, 
									OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD, user.getUserOrganization().getUserOrgId(), earnedPoints, totalReward,""+totalReward,
									responseHeader.getTransactionId(), OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED,
									"", itemExcludedAmount, 0, null, itemsBased.getRewardId(),itemsBased.getRewardRule(),itemsBased, itemsBased.getTotItemPrice());
							
							createRewardExpiryTransaction(contactsLoyalty, earnedPoints, (double) totalReward,  childTx.getTransChildId(), 
									OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,totalReward,itemsBased.getRewardValueCode(),itemsBased.getRewardId(),null,null);
						
						
							
					}		
					else{
						
						Double mulfactor = (percentage * (itemsBased.getTotItemPrice()) ) / 100;
						String result = Utility.truncateUptoTwoDecimal(mulfactor);
						if (result != null)
							totalReward = Double.parseDouble(result);
						//totalReward = Double.parseDouble(itemsBased.getRewardValue());
						
						String rewardEarned = Utility.truncateUptoTwoDecimal(totalReward);
						updateRewardBalances(user.getUserId(),user.getUserOrganization().getUserOrgId(), contactsLoyalty.getProgramId(),contactsLoyalty.getLoyaltyId(),
								itemsBased.getRewardValueCode(), rewardEarned,contactsLoyalty.getCardNumber());
						LoyaltyTransactionChild childTx =  createRewardTransaction(issuanceRequest, 0.0, contactsLoyalty, totalReward, earnType, 
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD, user.getUserOrganization().getUserOrgId(),0,0, rewardEarned,
								responseHeader.getTransactionId(), OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED,
								"", itemExcludedAmount, 0, null, itemsBased.getRewardId(),itemsBased.getRewardRule(),itemsBased, itemsBased.getTotItemPrice());
						
						createRewardExpiryTransaction(contactsLoyalty,0l, 0.0,  childTx.getTransChildId(), 
								OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,totalReward,itemsBased.getRewardValueCode(),itemsBased.getRewardId(),null,null);
					}
						
				}else if (tier != null && itemsBased.getRewardType()!=null && itemsBased.getRewardType().equalsIgnoreCase("T")) {// APP-4642 tier upgrade
					
					logger.info("inside tier upgrade through reward , current tier id : "+tier.getTierId());
					LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
					List<LoyaltyProgramTier> tiersList = loyaltyProgramTierDao.fetchTiersByProgramId(tier.getProgramId());
					
					if(itemsBased.getRewardValueCode()!=null && itemsBased.getRewardValue()!=null) {
						
						LoyaltyProgramTier rewardTier = loyaltyProgramTierDao.getTierById(Long.parseLong(itemsBased.getRewardValue()));
						logger.info("rewardTier Name : "+rewardTier.getTierName()+"rewardTierId : "+rewardTier.getTierId());
						
						if(rewardTier!=null && (Integer.valueOf(rewardTier.getTierType().substring(5)) > Integer.valueOf(tier.getTierType().substring(5))) 
									&& (rewardTier.getProgramId().equals(tier.getProgramId()))){
							
							logger.info("tiersList size :"+tiersList.size());
							for(LoyaltyProgramTier ltyTier : tiersList) {
								
								if(ltyTier.getTierId()==Long.parseLong(itemsBased.getRewardValue())) {
									
									tier = ltyTier;
									logger.info("upgrd tier id :"+tier.getTierId());
									if(contactsLoyalty!=null) {
										contactsLoyalty.setTierUpgradeReason(OCConstants.LOYALTY_REWARD_TIER_UPGRADE);
										contactsLoyalty.setProgramTierId(tier.getTierId());
										contactsLoyalty.setTierUpgradedDate(Calendar.getInstance());
										createUpgrdaeTierTransaction(contactsLoyalty,itemsBased,issuanceRequest);
									}
									break;
								}
								
							}
						}
					}
				}

						//LoyaltyAutoComm autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());
						LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();

						if(itemsBased!=null && itemsBased.getAutoCommEmail()!=null && !itemsBased.getAutoCommEmail().isEmpty()
									&& contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
								
								if (contact != null && contact.getEmailId() != null) {
									itemBasedAutoComm.add(itemsBased);
									/*autoCommGen.sendSpecialRewardTemplate(Long.parseLong(itemsBased.getAutoCommEmail()),
											Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
											contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
											contactsLoyalty.getLoyaltyId(), itemsBased);*/
								}
							}
							
						
							if (itemsBased!=null && itemsBased.getAutoCommSMS()!=null && !itemsBased.getAutoCommSMS().isEmpty() && user.isEnableSMS() 
									&& contactsLoyalty.getMobilePhone() != null) {
								if (contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
									logger.info("sending issuance auto email place 1:"+contactsLoyalty.getLoyaltyId());
									autoCommGen.sendSpecialRewardIssueSMSTemplate(Long.parseLong(itemsBased.getAutoCommSMS()), user,
											contactsLoyalty.getContact().getContactId(), contactsLoyalty.getLoyaltyId(),
											contactsLoyalty.getMobilePhone());
								}
							
						}
						
					
				}
			}
			if(visitsBasedSP.size() > 0 && netPurchaseAmount > 0) { // flat and tier
				long earnedPoints = 0;
				double earnedAmount = 0.0;
				
				for (SpecialReward visitBased : visitsBasedSP) {
					
					if(visitBased.getRewardType().equalsIgnoreCase("F")) {
						
						double earnedReward = 0.0;
						String earnType = null;
						//if(ignoreIssuance == false && visitBased.isDeductItemPrice()) ignoreIssuance = true;
						earnType = visitBased.getRewardValueCode();
						earnedReward = Double.parseDouble(visitBased.getRewardValue());
						String roundingType = tier.getRoundingType();
						
						if (earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS) || 
								earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY) || 
								earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
							if (OCConstants.LOYALTY_TYPE_POINTS.equals(earnType)) {
								
								logger.info("Rounding type of the tier is ==="+tier.getRoundingType());
								
								earnedPoints = LoyaltyProgramHelper.getRoundedPoints(roundingType, earnedReward);
								
								
								/*
						if(roundingType!=null  && roundingType.toString().equalsIgnoreCase("Up")){
							
							earnedPoints = (long) Math.ceil(earnedReward);

						}else {
							
							earnedPoints = (long) Math.floor(earnedReward);
						}*/
								//earnedPoints = (long) Math.floor(earnedReward);
							} else if (OCConstants.LOYALTY_TYPE_CURRENCY.equals(earnType) || 
									earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
								// earnedAmount = (new BigDecimal(earnedValue).setScale(2,
								// BigDecimal.ROUND_DOWN)).doubleValue();
								earnType = OCConstants.LOYALTY_TYPE_AMOUNT;
								String result = Utility.truncateUptoTwoDecimal(earnedReward);
								if (result != null)
									earnedAmount = Double.parseDouble(result);
							}
							logger.info("earnedReward = " + earnedReward);
							
							LoyaltyProgramHelper helper = new LoyaltyProgramHelper();
							helper.updateContactLoyaltyBalances(null, earnedAmount, (double)earnedPoints, earnType, contactsLoyalty);
							
							/*updateContactLoyaltyBalances((double) earnedAmount, (double) earnedPoints, tier.getEarnType(),
							contactsLoyalty);
							 */
							
							LoyaltyTransactionChild childTx =  createRewardTransaction(issuanceRequest, 0.0, contactsLoyalty, earnedReward, earnType, 
									OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD, user.getUserOrganization().getUserOrgId(), earnedPoints, earnedAmount,"",
									responseHeader.getTransactionId(), OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED,
									"", itemExcludedAmount, 0, null, visitBased.getRewardId(),visitBased.getRewardRule(),visitBased, 0.0);
							
							createRewardExpiryTransaction(contactsLoyalty, earnedPoints, (double) earnedAmount,  childTx.getTransChildId(), 
									OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,earnedReward,visitBased.getRewardValueCode(),visitBased.getRewardId(),null,null);
							
							
							
						}
						
						else {
							
							
							String rewardEarned = Utility.truncateUptoTwoDecimal(earnedReward);
							
							updateRewardBalances(user.getUserId(),user.getUserOrganization().getUserOrgId(), contactsLoyalty.getProgramId(),contactsLoyalty.getLoyaltyId(),
									visitBased.getRewardValueCode(), rewardEarned,contactsLoyalty.getCardNumber());
							LoyaltyTransactionChild childTx =  createRewardTransaction(issuanceRequest, 0.0, contactsLoyalty, earnedReward, earnType, 
									OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD, user.getUserOrganization().getUserOrgId(),0,0, rewardEarned,
									responseHeader.getTransactionId(), OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED,
									"", itemExcludedAmount, 0, null, visitBased.getRewardId(),visitBased.getRewardRule(),visitBased, 0.0);
							
							createRewardExpiryTransaction(contactsLoyalty,0l, 0.0,  childTx.getTransChildId(), 
									OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,earnedReward,visitBased.getRewardValueCode(),visitBased.getRewardId(),null,null);
							
						}
					}else if (tier != null && visitBased.getRewardType()!=null && visitBased.getRewardType().equalsIgnoreCase("T")) {// APP-4642 tier upgrade
						
						logger.info("inside tier upgrade through reward , current tier id : "+tier.getTierId());
						LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
						List<LoyaltyProgramTier> tiersList = loyaltyProgramTierDao.fetchTiersByProgramId(tier.getProgramId());
						
						if(visitBased.getRewardValueCode()!=null && visitBased.getRewardValue()!=null) {
							
							LoyaltyProgramTier rewardTier = loyaltyProgramTierDao.getTierById(Long.parseLong(visitBased.getRewardValue()));
							logger.info("rewardTier Name : "+rewardTier.getTierName()+"rewardTierId : "+rewardTier.getTierId());
							
							if(rewardTier!=null && (Integer.valueOf(rewardTier.getTierType().substring(5)) > Integer.valueOf(tier.getTierType().substring(5))) 
										&& (rewardTier.getProgramId().equals(tier.getProgramId()))){
								
								logger.info("tiersList size :"+tiersList.size());
								for(LoyaltyProgramTier ltyTier : tiersList) {
									
									if(ltyTier.getTierId()==Long.parseLong(visitBased.getRewardValue())) {
										
										tier = ltyTier;
										logger.info("upgrd tier id :"+tier.getTierId());
										if(contactsLoyalty!=null) {
											contactsLoyalty.setTierUpgradeReason(OCConstants.LOYALTY_REWARD_TIER_UPGRADE);
											contactsLoyalty.setProgramTierId(tier.getTierId());
											contactsLoyalty.setTierUpgradedDate(Calendar.getInstance());
											createUpgrdaeTierTransaction(contactsLoyalty,visitBased,issuanceRequest);
										}
										break;
									}
									
								}
							}
						}
					}
			    
				//LoyaltyAutoComm autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());
				LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();

				if(visitBased!=null && visitBased.getAutoCommEmail()!=null && !visitBased.getAutoCommEmail().isEmpty()
							&& contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
						//Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
						if (contact != null && contact.getEmailId() != null) {
							autoCommGen.sendSpecialRewardTemplate(Long.parseLong(visitBased.getAutoCommEmail()),
									Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
									contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
									contactsLoyalty.getLoyaltyId(), null, null);
						}
					}
					
				
					if (visitBased!=null && visitBased.getAutoCommSMS()!=null && !visitBased.getAutoCommSMS().isEmpty() && user.isEnableSMS() 
							&& contactsLoyalty.getMobilePhone() != null) {
						if (contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
							logger.info("add here for loyalty Issuance place 2:"+contactsLoyalty.getLoyaltyId());
							autoCommGen.sendSpecialRewardIssueSMSTemplate(Long.parseLong(visitBased.getAutoCommSMS()), user,
									contactsLoyalty.getContact().getContactId(), contactsLoyalty.getLoyaltyId(),
									contactsLoyalty.getMobilePhone());
						}
					
				}
			    
			    
				}
				
				isSpecialRewardExist=true;	
			}
			if(otherSP.size() > 0 && tier != null && netPurchaseAmount > 0){ //only multipliers
				try {
				SpecialReward highestMultiplier = findHighestMultiplier(otherSP);
				//if(ignoreIssuance == false && highestMultiplier.isDeductItemPrice()) ignoreIssuance = true;
				double earnedReward = 0.0;
				String earnType = null;
				String earnrule = null;
				multiplier = Double.parseDouble(highestMultiplier.getRewardValue());
				earnType = tier.getEarnType();
				if (tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)) {
					//Double multipleFactordbl = LoyaltyProgramHelper.getRoundedPurchaseAmount(tier.getRoundingType(), netPurchaseAmount) / tier.getEarnOnSpentAmount();
					Double multipleFactordbl = netPurchaseAmount / tier.getEarnOnSpentAmount();
					//long multipleFactor = multipleFactordbl.intValue();
					//earnedReward = tier.getEarnValue() * multipleFactor;
					if(tier.getRoundingType()!=null && tier.getRoundingType().equals("Up"))
						earnedReward = tier.getEarnValue() * multipleFactordbl;//APP-4027
					else
						earnedReward = tier.getEarnValue() * (multipleFactordbl+0.001);//APP-4027
					/*Double multipleFactordbl = netPurchaseAmount / tier.getEarnOnSpentAmount();
					double multipleFactor = multipleFactordbl.intValue();
					// double multipleFactor = multipleFactordbl;
					
					earnedReward = tier.getEarnValue() * multipleFactor;*/
					
				} else if (tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)) {
						//earnedReward = (tier.getEarnValue() * (LoyaltyProgramHelper.getRoundedPurchaseAmount(tier.getRoundingType(), netPurchaseAmount))) / 100;
					earnedReward = (tier.getEarnValue() * (netPurchaseAmount)) / 100;
				}
				earnedReward = multiplier * earnedReward;				
				long earnedPoints = 0;
				double earnedAmount = 0.0;
				long pointsDifference = 0;
				double amountDifference = 0.0;
				String roundingType = tier.getRoundingType();
				if (OCConstants.LOYALTY_TYPE_POINTS.equals(earnType)) {
					
					logger.info("Rounding type of the tier is ==="+tier.getRoundingType());
					
					earnedPoints = LoyaltyProgramHelper.getRoundedPoints(roundingType, earnedReward);
/*

					if(roundingType!=null  && roundingType.toString().equalsIgnoreCase("Up")){
						
						earnedPoints = (long) Math.ceil(earnedReward);

					}else {
						
						earnedPoints = (long) Math.floor(earnedReward);
					}*/
					//earnedPoints = (long) Math.floor(earnedReward);
				} else if (OCConstants.LOYALTY_TYPE_AMOUNT.equals(earnType)) {
					// earnedAmount = (new BigDecimal(earnedValue).setScale(2,
					// BigDecimal.ROUND_DOWN)).doubleValue();
					String result = Utility.truncateUptoTwoDecimal(earnedReward);
					if (result != null)
						earnedAmount = Double.parseDouble(result);
				}
				logger.info("earnedReward = " + earnedReward);
				
				LoyaltyProgramHelper helper = new LoyaltyProgramHelper();
			//jhjhjhjh	1 time
			helper.updateContactLoyaltyBalances(null, earnedAmount, (double)earnedPoints, tier.getEarnType(), contactsLoyalty);
				
				
				/*updateContactLoyaltyBalances((double) earnedAmount, (double) earnedPoints, tier.getEarnType(),
						contactsLoyalty);*/
										
				LoyaltyTransactionChild childTx = createMultiplierRewardTransaction(issuanceRequest, 0.0, 
						contactsLoyalty, earnedAmount, earnedPoints, earnType, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD,
						user.getUserOrganization().getUserOrgId(), earnedPoints+"", earnedAmount+"", 
						responseHeader.getTransactionId(), OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED, null,
						itemExcludedAmount, 0, null, highestMultiplier.getRewardId(),highestMultiplier.getRewardRule(),highestMultiplier,
						earnedReward, netPurchaseAmount);
				
				createExpiryTransaction(contactsLoyalty, earnedPoints, (double) earnedAmount, childTx.getTransChildId(),
						OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, highestMultiplier.getRewardId(),null);
				
				
				//LoyaltyAutoComm autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());
				LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();

				if(highestMultiplier!=null && highestMultiplier.getAutoCommEmail()!=null && !highestMultiplier.getAutoCommEmail().isEmpty()
							&& contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
						//Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
						if (contact != null && contact.getEmailId() != null) {
							autoCommGen.sendSpecialRewardTemplate(Long.parseLong(highestMultiplier.getAutoCommEmail()),
									Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
									contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
									contactsLoyalty.getLoyaltyId(), null, null);
						}
					}
					
				
					if (highestMultiplier!=null && highestMultiplier.getAutoCommSMS()!=null && !highestMultiplier.getAutoCommSMS().isEmpty() && user.isEnableSMS() 
							&& contactsLoyalty.getMobilePhone() != null) {
						if (contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
							logger.info("added here for loyalty issuance place 3"+contactsLoyalty.getLoyaltyId());
							autoCommGen.sendSpecialRewardIssueSMSTemplate(Long.parseLong(highestMultiplier.getAutoCommSMS()), user,
									contactsLoyalty.getContact().getContactId(), contactsLoyalty.getLoyaltyId(),
									contactsLoyalty.getMobilePhone());
						}
					
				}
				
				isSpecialRewardExist=true;
				}
				catch(Exception e) {
					logger.error("otherSP==>", e);
				}
				
			}
		//	if(isSpecialRewardExist && loyaltyProgram.isIssuanceDisable()) return;
			

		}
		String itemInfo = Constants.STRING_NILL;
		List<SkuDetails> items = issuanceRequest.getItems();
		Double isuanceAmount =0.0;
		for (SkuDetails item : items) {
			if(item.getItemSID() != null && !ExcludeItems.isEmpty() && ExcludeItems.contains(item.getItemSID())){
				
isuanceAmount +=  (Double.valueOf(item.getQuantity())
						* Double.valueOf(item.getBilledUnitPrice()));
				
//isuanceAmount +=  ExcludeItemsMap.get(item.getItemSID());/*(Double.valueOf(item.getQuantity())
						//* Double.valueOf(item.getBilledUnitPrice()));*/
				continue;
			}
			if(itemInfo.length() > 0) itemInfo += Constants.DELIMETER_COMMA;
			itemInfo += item.getItemSID()+Constants.DELIMETER_COLON+item.getQuantity()+Constants.DELIMETER_COLON+item.getBilledUnitPrice();
		}
			
		isuanceAmount = purchaseAmountdbl-itemExcludedAmount-isuanceAmount;
		
		LoyaltyAutoComm autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());

		
		netPurchaseAmount = netPurchaseAmount - itemExcludedAmount;
		netPurchaseAmount = netPurchaseAmount>0? netPurchaseAmount : 0.00;
		//error found
		Double returndAmount  = issuanceRequest.getAmount().getReturnedAmount() != null &&
				!issuanceRequest.getAmount().getReturnedAmount().isEmpty() ? Double.parseDouble(issuanceRequest.getAmount().getReturnedAmount()) : 0.0;
		//Double returndAmount  = Double.parseDouble(issuanceRequest.getAmount().getReturnedAmount());
		boolean isExchange = returndAmount != 0;
		Double BonusAmt = netPurchaseAmount-returndAmount;
		double earnedValue = 0.0;
		double earnedValueForBonus = 0.0;
		String earntype = tier.getEarnType();
		String roundingType= tier.getRoundingType();
		long pointsDifference = 0;
		double amountDifference = 0.0;
		long pointsDifferenceForBonus = 0;
		double amountDifferenceForBonus = 0.0;
		
		Double AfterLtyBalanceForBonus = contactsLoyalty.getTotalLoyaltyEarned() == null? 0:contactsLoyalty.getTotalLoyaltyEarned();
		Double AfterAmtBalanceForBonus = contactsLoyalty.getTotalGiftcardAmount() == null ? 0 :contactsLoyalty.getTotalGiftcardAmount();

		List<Balance> balances = null;
		HoldBalance holdBalance = null;
		List<String[]> bonusArrList = null;
		
		logger.info("1.contactsLoyalty==>"+contactsLoyalty+ "loyaltyProgram==>"+ loyaltyProgram+"tier===>"+ tier+"autocomm===>"+autoComm+"user===>"+user +"issuanceRequest==>"+issuanceRequest);
		
		if (tier.getActivationFlag() == 'N') {

			
			
			//Double AfterCPVBalanceBonus = contactsLoyalty.getCummulativePurchaseValue() == null? 0+BonusAmt : contactsLoyalty.getCummulativePurchaseValue()+BonusAmt;
			
			//Double AfterLPVBalanceForBonus = LoyaltyProgramHelper.getLPV(contactsLoyalty);
			logger.info("netPurchaseAmount and earnonSpentamt>>>>"+netPurchaseAmount+" "+tier.getEarnOnSpentAmount());

			if (tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)) {

				//Double multipleFactordbl = LoyaltyProgramHelper.getRoundedPurchaseAmount(roundingType, netPurchaseAmount) / tier.getEarnOnSpentAmount();
				Double multipleFactordbl = netPurchaseAmount / tier.getEarnOnSpentAmount();
				//long multipleFactor = multipleFactordbl.intValue();
				//earnedValue = tier.getEarnValue() * multipleFactor;
				if(tier.getRoundingType()!=null && tier.getRoundingType().equals("Up"))
					earnedValue = tier.getEarnValue() * multipleFactordbl;//APP-4027
				else
					earnedValue = tier.getEarnValue() * (multipleFactordbl+0.001);//APP-4027
				logger.info("earned value and multipleFactordbl>>>>"+earnedValue+" "+multipleFactordbl);
				
				
				
				if(BonusAmt > 0){
					//Double multipleFactordblForBonus = LoyaltyProgramHelper.getRoundedPurchaseAmount(roundingType, BonusAmt) / tier.getEarnOnSpentAmount();
					Double multipleFactordblForBonus = BonusAmt / tier.getEarnOnSpentAmount();
					//long multipleFactorForBonus = multipleFactordblForBonus.intValue();
					//earnedValueForBonus = tier.getEarnValue() * multipleFactorForBonus;
					if(tier.getRoundingType()!=null && tier.getRoundingType().equals("Up"))
						earnedValueForBonus = tier.getEarnValue() * multipleFactordblForBonus;//APP-4027
					else
						earnedValueForBonus = tier.getEarnValue() * (multipleFactordblForBonus+0.001);//APP-4027
					
					
					
				}
			} else if (tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)) {

				//earnedValue = (tier.getEarnValue() * (LoyaltyProgramHelper.getRoundedPurchaseAmount(tier.getRoundingType(), netPurchaseAmount))) / 100;
				earnedValue = (tier.getEarnValue() * netPurchaseAmount) / 100;
				
				if(BonusAmt > 0){
					//earnedValueForBonus = (tier.getEarnValue() * (LoyaltyProgramHelper.getRoundedPurchaseAmount(tier.getRoundingType(), BonusAmt))) / 100;
					earnedValueForBonus = (tier.getEarnValue() * BonusAmt) / 100;
				}
			}
			
			//tier's max limit wise homany points to be issued.
			Double maxcap= tier.getIssuanceChkEnable()!=null && tier.getIssuanceChkEnable() && tier.getMaxcap()!=null && tier.getMaxcap()>0 ? tier.getMaxcap() : earnedValue ;
			earnedValue =  earnedValue>maxcap ? maxcap : earnedValue;
			
			long earnedPoints = 0;
			double earnedAmount = 0.0;
			
			long earnedPointsForBonus = 0;
			double earnedAmountForBonus = 0.0;
			if (OCConstants.LOYALTY_TYPE_POINTS.equals(earntype)) {
				
				logger.info("Rounding type of the tier is ==="+tier.getRoundingType());
				
				earnedPoints = LoyaltyProgramHelper.getRoundedPoints(roundingType, earnedValue);
				if(earnedValueForBonus > 0){
					
					earnedPointsForBonus = LoyaltyProgramHelper.getRoundedPoints(roundingType, earnedValueForBonus);
					
				}
				
				AfterLtyBalanceForBonus = AfterLtyBalanceForBonus==null ? 0+earnedPointsForBonus : AfterLtyBalanceForBonus+earnedPointsForBonus;
/*
				if(roundingType!=null && roundingType.toString().equalsIgnoreCase("Up")){
					
					earnedPoints = (long) Math.ceil(earnedValue);

				}else {
					
					earnedPoints = (long) Math.floor(earnedValue);
				}*/
				
				
				
				
			} else if (OCConstants.LOYALTY_TYPE_AMOUNT.equals(earntype)) {
				String result = Utility.truncateUptoTwoDecimal(earnedValue);
				if (result != null && !result.equalsIgnoreCase("NaN"))
					earnedAmount = Double.parseDouble(result);
				
				if(earnedValueForBonus > 0){
					String resultForBonus = Utility.truncateUptoTwoDecimal(earnedValueForBonus);
					if (resultForBonus != null)
						earnedAmountForBonus = Double.parseDouble(resultForBonus);
				}
				
				AfterAmtBalanceForBonus = AfterAmtBalanceForBonus==null ? 0+earnedAmountForBonus : AfterAmtBalanceForBonus+earnedAmountForBonus;
			}
			logger.info("earnedValue = " + earnedValue);

			
			LoyaltyProgramHelper helper = new LoyaltyProgramHelper();
			String[] diffArrForBonus = applyConversionRules(contactsLoyalty, tier, earnedValueForBonus, earntype);
			//APP-3925
			actualPurchaseValue = actualPurchaseValue!=null && actualPurchaseValue>0 ? actualPurchaseValue : purchaseAmountdbl;
			helper.updateContactLoyaltyBalances(actualPurchaseValue, earnedAmount, (double)earnedPoints, tier.getEarnType(), contactsLoyalty);
			
			//updateContactLoyaltyBalances((double) earnedAmount, (double) earnedPoints, tier.getEarnType(),
					//contactsLoyalty);
			logger.info("balances After earnedValue updatation --  points = " + contactsLoyalty.getLoyaltyBalance()
					+ " currency = " + contactsLoyalty.getGiftcardBalance());
			String tierEarnRule =tier.getEarnValueType()+"->"+tier.getEarnValue()+"->"+tier.getEarnOnSpentAmount(); 

			LoyaltyTransactionChild childTx = createPurchaseTransaction(issuanceRequest, purchaseAmountdbl,
					contactsLoyalty, earnedAmount, earnedPoints, earntype,
					OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE, user.getUserOrganization().getUserOrgId(),
					Constants.STRING_NILL + pointsDifference, Constants.STRING_NILL + amountDifference,
					responseHeader.getTransactionId(), OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED, null,
					itemExcludedAmount, 0, null,tierEarnRule, itemInfo, isuanceAmount, itemRewardInfo);
				if(!itemBasedAutoComm.isEmpty()){
					LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
					for (SpecialReward autoCommToSend : itemBasedAutoComm) {
						autoCommGen.sendSpecialRewardTemplate(Long.parseLong(autoCommToSend.getAutoCommEmail()),
						Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
						contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
						contactsLoyalty.getLoyaltyId(), autoCommToSend, earnedPoints+"");
					}
				}
			// Expiry transaction
			createExpiryTransaction(contactsLoyalty, earnedPoints, (double) earnedAmount, childTx.getTransChildId(),
					OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, null,null);
			String[] diffArr = applyConversionRules(contactsLoyalty, tier);
			// 0 - amountdiff, 1 - pointsdiff
			logger.info("balances After conversion rules updatation --  points = " + contactsLoyalty.getLoyaltyBalance()
					+ " currency = " + contactsLoyalty.getGiftcardBalance());
			long convertPointsForBonus = 0;
			long convertAmountForBonus = 0;
			if (diffArrForBonus != null) {
				convertAmountForBonus = Double.valueOf(diffArrForBonus[0].trim()).longValue();
				convertPointsForBonus = Double.valueOf(diffArrForBonus[1].trim()).longValue();
			}
			pointsDifferenceForBonus = earnedPointsForBonus - convertPointsForBonus;
			// amountDifference = earnedAmount + convertAmount;
			amountDifferenceForBonus = earnedAmountForBonus + (diffArrForBonus != null ? Double.parseDouble(diffArrForBonus[0].trim()) : 0.0);
			
			String conversionRate = null;
			long convertPoints = 0;
			double convertAmount = 0;
			if (diffArr != null) {
				convertAmount = Double.valueOf(diffArr[0].trim());
				convertPoints = Double.valueOf(diffArr[1].trim()).longValue();
				conversionRate = diffArr[2];
			}

			pointsDifference = earnedPoints - convertPoints;
			// amountDifference = earnedAmount + convertAmount;
			amountDifference = earnedAmount + (diffArr != null ? Double.parseDouble(diffArr[0].trim()) : 0.0);
			logger.info("2.contactsLoyalty==>"+contactsLoyalty+ "loyaltyProgram==>"+ loyaltyProgram+"tier===>"+ tier+"autocomm===>"+autoComm+"user===>"+user +"issuanceRequest==>"+issuanceRequest);
			//tier = applyTierUpgradeRule(contactsLoyalty, loyaltyProgram, tier, autoComm, user, issuanceRequest); //APP-1769
			
			updatePurchaseTransaction(childTx, contactsLoyalty, Constants.STRING_NILL + pointsDifference,
					Constants.STRING_NILL + amountDifference, conversionRate, convertAmount, tier);
			
			// Event Trigger sending part
			EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator
					.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator
					.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			EventTriggerDao eventTriggerDao = (EventTriggerDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
			List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(childTx.getUserId(),
					Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
			logger.debug("etList ::" + etList);
			if (etList != null) {
				eventTriggerEventsObservable.notifyToObserver(etList, childTx.getTransChildId(),
						childTx.getTransChildId(), childTx.getUserId(), OCConstants.LOYALTY_ISSUANCE,
						Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
			}
			List<EventTrigger> retList = eventTriggerDao.findAllETByUserAndType(childTx.getUserId(),
					Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
			
			if (retList != null) {
				eventTriggerEventsObservable.notifyToObserver(retList, childTx.getTransChildId(),
						childTx.getTransChildId(), childTx.getUserId(), OCConstants.LOYALTY_ISSUANCE,
						Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
			}
			balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL + pointsDifference,
					Constants.STRING_NILL + amountDifference, Constants.STRING_NILL);
			holdBalance = prepareHoldBalances(contactsLoyalty, Constants.STRING_NILL);
			// bonusArrList = calculateThresholdBonus(contactsLoyalty, loyaltyProgram,
			// fromLtyBalance, fromAmtBalance);
			//calculateThresholdBonus(contactsLoyalty, loyaltyProgram, fromLtyBalance, fromAmtBalance, fromCPVBalance, fromLPVBalance , tier, autoComm,
				//	user, issuanceRequest, pointsDifference, amountDifference,AfterLtyBalanceForBonus, AfterAmtBalanceForBonus, isExchange, returndAmount );

		} else {
			//APP-3284
			boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(issuanceRequest.getHeader().getStoreNumber(),tier);
				
				if(isStoreActiveForActivateAfter) {
			// create a transaction
			balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL,
					Constants.STRING_NILL);

			if (tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)) {
				//Double multipleFactordbl = LoyaltyProgramHelper.getRoundedPurchaseAmount(roundingType, netPurchaseAmount) / tier.getEarnOnSpentAmount();
				Double multipleFactordbl = netPurchaseAmount / tier.getEarnOnSpentAmount();
				//long multipleFactor = multipleFactordbl.intValue();
				//earnedValue = tier.getEarnValue() * multipleFactor;
				if(tier.getRoundingType()!=null && tier.getRoundingType().equals("Up"))
					earnedValue = tier.getEarnValue() * multipleFactordbl;//APP-4027
				else
					earnedValue = tier.getEarnValue() * (multipleFactordbl+0.001);//APP-4027
				/*Double multipleFactordbl = netPurchaseAmount / tier.getEarnOnSpentAmount();
				// long multipleFactor = multipleFactordbl.intValue();
				double multipleFactor = multipleFactordbl;
				earnedValue = tier.getEarnValue() * multipleFactor;*/
			} else if (tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)) {

				//earnedValue = (tier.getEarnValue() * (LoyaltyProgramHelper.getRoundedPurchaseAmount(roundingType, netPurchaseAmount))) / 100;
				earnedValue = (tier.getEarnValue() * netPurchaseAmount) / 100;
			}
			
			//tier's max limit wise homany points to be issued.
			Double maxcap= tier.getIssuanceChkEnable()!=null && tier.getIssuanceChkEnable() && tier.getMaxcap()!=null && tier.getMaxcap()>0 ? tier.getMaxcap() : earnedValue ;
			earnedValue =  earnedValue>maxcap ? maxcap : earnedValue;
			
			long earnedPoints = 0;
			double earnedAmount = 0.0;
			if (OCConstants.LOYALTY_TYPE_POINTS.equals(earntype)) {
				
				logger.info("Rounding type of the tier is ==="+tier.getRoundingType());
				
				earnedPoints = LoyaltyProgramHelper.getRoundedPoints(roundingType, earnedValue);

/*
				if(roundingType!=null && roundingType.toString().equalsIgnoreCase("Up")){
					
					earnedPoints = (long) Math.ceil(earnedValue);

				}else {
					
					earnedPoints = (long) Math.floor(earnedValue);
				}*/
				//earnedPoints = (long) Math.floor(earnedValue);
			} else if (OCConstants.LOYALTY_TYPE_AMOUNT.equals(earntype)) {
				// earnedAmount = (new BigDecimal(earnedValue).setScale(2,
				// BigDecimal.ROUND_DOWN)).doubleValue();
				String result = Utility.truncateUptoTwoDecimal(earnedValue);
				if (result != null)
					earnedAmount = Double.parseDouble(result);
			}
			logger.info("earnedValue = " + earnedValue);

			// addEarnValueToHoldBalances(contactsLoyalty, tier.getEarnType(), earnedValue);
			// addEarnValueToHoldBalances(contactsLoyalty, tier.getEarnType(), earnedAmount,
			// earnedPoints);
			//APP-3925
			actualPurchaseValue = actualPurchaseValue!=null && actualPurchaseValue>0 ? actualPurchaseValue : purchaseAmountdbl;
			addEarnValueToHoldBalances(actualPurchaseValue,contactsLoyalty, tier.getEarnType(), earnedAmount, earnedPoints);
			String activationDate = null;
			if (tier.getPtsActiveDateType()!=null && (OCConstants.LOYALTY_TYPE_DAY.equals(tier.getPtsActiveDateType().trim()))) {

				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, tier.getPtsActiveDateValue().intValue());
				activationDate = MyCalendar.calendarToString(cal, MyCalendar.FORMAT_YEARTODATE);

			}

			String tierEarnRule =tier.getEarnValueType()+"->"+tier.getEarnValue()+"->"+tier.getEarnOnSpentAmount(); 
logger.debug("purchaseAmountdbl=="+purchaseAmountdbl);
			LoyaltyTransactionChild childTx = createPurchaseTransaction(issuanceRequest, purchaseAmountdbl,
					contactsLoyalty, earnedAmount, earnedPoints, earntype,
					OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE, user.getUserOrganization().getUserOrgId(),
					Constants.STRING_NILL + pointsDifference, Constants.STRING_NILL + amountDifference,
					responseHeader.getTransactionId(), OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_NEW, null,
					itemExcludedAmount, 0, activationDate,tierEarnRule, itemInfo, isuanceAmount, itemRewardInfo);
			String expiryPeriod = Constants.STRING_NILL;
			if (tier != null && tier.getActivationFlag() == OCConstants.FLAG_YES
					&& ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0)
							|| (contactsLoyalty.getHoldPointsBalance() != null
									&& contactsLoyalty.getHoldPointsBalance() > 0))
					&& isStoreActiveForActivateAfter) {

				expiryPeriod = tier.getPtsActiveDateValue() + " " + tier.getPtsActiveDateType();
			}
			holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
			//for FBB-trigger even on activate after
			// Event Trigger sending part
			EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator
					.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator
					.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			EventTriggerDao eventTriggerDao = (EventTriggerDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
			List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(childTx.getUserId(),
					Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
			logger.debug("etList ::" + etList);
			if (etList != null) {
				eventTriggerEventsObservable.notifyToObserver(etList, childTx.getTransChildId(),
						childTx.getTransChildId(), childTx.getUserId(), OCConstants.LOYALTY_ISSUANCE,
						Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
			}
			List<EventTrigger> retList = eventTriggerDao.findAllETByUserAndType(childTx.getUserId(),
					Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
			
			if (retList != null) {
				eventTriggerEventsObservable.notifyToObserver(retList, childTx.getTransChildId(),
						childTx.getTransChildId(), childTx.getUserId(), OCConstants.LOYALTY_ISSUANCE,
						Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
			}
			//end
				}else {


					
					
					//Double AfterLtyBalanceForBonus = contactsLoyalty.getTotalLoyaltyEarned() == null? 0:contactsLoyalty.getTotalLoyaltyEarned();
					//Double AfterAmtBalanceForBonus = contactsLoyalty.getTotalGiftcardAmount() == null ? 0 :contactsLoyalty.getTotalGiftcardAmount();
					//Double AfterCPVBalanceBonus = contactsLoyalty.getCummulativePurchaseValue() == null? 0+BonusAmt : contactsLoyalty.getCummulativePurchaseValue()+BonusAmt;
					
					//Double AfterLPVBalanceForBonus = LoyaltyProgramHelper.getLPV(contactsLoyalty);
					

					if (tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)) {

						//Double multipleFactordbl = LoyaltyProgramHelper.getRoundedPurchaseAmount(roundingType, netPurchaseAmount) / tier.getEarnOnSpentAmount();
						Double multipleFactordbl = netPurchaseAmount / tier.getEarnOnSpentAmount();
						//long multipleFactor = multipleFactordbl.intValue();
						//earnedValue = tier.getEarnValue() * multipleFactor;
						if(tier.getRoundingType()!=null && tier.getRoundingType().equals("Up"))
							earnedValue = tier.getEarnValue() * multipleFactordbl;//APP-4027
						else
							earnedValue = tier.getEarnValue() * (multipleFactordbl+0.001);//APP-4027
						
						
						
						if(BonusAmt > 0){
							//Double multipleFactordblForBonus = LoyaltyProgramHelper.getRoundedPurchaseAmount(roundingType, BonusAmt) / tier.getEarnOnSpentAmount();
							Double multipleFactordblForBonus = BonusAmt / tier.getEarnOnSpentAmount();
							//long multipleFactorForBonus = multipleFactordblForBonus.intValue();
							//earnedValueForBonus = tier.getEarnValue() * multipleFactorForBonus;
							if(tier.getRoundingType()!=null && tier.getRoundingType().equals("Up"))
								earnedValueForBonus = tier.getEarnValue() * multipleFactordblForBonus;//APP-4027
							else
								earnedValueForBonus = tier.getEarnValue() * (multipleFactordblForBonus+0.001);//APP-4027
							
							
							
						}
					} else if (tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)) {

						//earnedValue = (tier.getEarnValue() * (LoyaltyProgramHelper.getRoundedPurchaseAmount(tier.getRoundingType(), netPurchaseAmount))) / 100;
						earnedValue = (tier.getEarnValue() * netPurchaseAmount) / 100;
						
						if(BonusAmt > 0){
							//earnedValueForBonus = (tier.getEarnValue() * (LoyaltyProgramHelper.getRoundedPurchaseAmount(tier.getRoundingType(), BonusAmt))) / 100;
							earnedValueForBonus = (tier.getEarnValue() * BonusAmt) / 100;
						}
					}
					long earnedPoints = 0;
					double earnedAmount = 0.0;
					
					long earnedPointsForBonus = 0;
					double earnedAmountForBonus = 0.0;
					if (OCConstants.LOYALTY_TYPE_POINTS.equals(earntype)) {
						
						logger.info("Rounding type of the tier is ==="+tier.getRoundingType());
						
						earnedPoints = LoyaltyProgramHelper.getRoundedPoints(roundingType, earnedValue);
						if(earnedValueForBonus > 0){
							
							earnedPointsForBonus = LoyaltyProgramHelper.getRoundedPoints(roundingType, earnedValueForBonus);
							
						}
						
						AfterLtyBalanceForBonus = AfterLtyBalanceForBonus==null ? 0+earnedPointsForBonus : AfterLtyBalanceForBonus+earnedPointsForBonus;
		/*
						if(roundingType!=null && roundingType.toString().equalsIgnoreCase("Up")){
							
							earnedPoints = (long) Math.ceil(earnedValue);

						}else {
							
							earnedPoints = (long) Math.floor(earnedValue);
						}*/
						
						
						
						
					} else if (OCConstants.LOYALTY_TYPE_AMOUNT.equals(earntype)) {
						String result = Utility.truncateUptoTwoDecimal(earnedValue);
						if (result != null)
							earnedAmount = Double.parseDouble(result);
						
						if(earnedValueForBonus > 0){
							String resultForBonus = Utility.truncateUptoTwoDecimal(earnedValueForBonus);
							if (resultForBonus != null)
								earnedAmountForBonus = Double.parseDouble(resultForBonus);
						}
						
						AfterAmtBalanceForBonus = AfterAmtBalanceForBonus==null ? 0+earnedAmountForBonus : AfterAmtBalanceForBonus+earnedAmountForBonus;
					}
					logger.info("earnedValue = " + earnedValue);

					
					LoyaltyProgramHelper helper = new LoyaltyProgramHelper();
					String[] diffArrForBonus = applyConversionRules(contactsLoyalty, tier, earnedValueForBonus, earntype);
					//APP-3925
					actualPurchaseValue = actualPurchaseValue!=null && actualPurchaseValue>0 ? actualPurchaseValue : purchaseAmountdbl;
					helper.updateContactLoyaltyBalances(actualPurchaseValue, earnedAmount, (double)earnedPoints, tier.getEarnType(), contactsLoyalty);
					
					//updateContactLoyaltyBalances((double) earnedAmount, (double) earnedPoints, tier.getEarnType(),
							//contactsLoyalty);
					logger.info("balances After earnedValue updatation --  points = " + contactsLoyalty.getLoyaltyBalance()
							+ " currency = " + contactsLoyalty.getGiftcardBalance());
					String tierEarnRule =tier.getEarnValueType()+"->"+tier.getEarnValue()+"->"+tier.getEarnOnSpentAmount(); 

					LoyaltyTransactionChild childTx = createPurchaseTransaction(issuanceRequest, purchaseAmountdbl,
							contactsLoyalty, earnedAmount, earnedPoints, earntype,
							OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE, user.getUserOrganization().getUserOrgId(),
							Constants.STRING_NILL + pointsDifference, Constants.STRING_NILL + amountDifference,
							responseHeader.getTransactionId(), OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED, null,
							itemExcludedAmount, 0, null,tierEarnRule, itemInfo, isuanceAmount, itemRewardInfo);
						if(!itemBasedAutoComm.isEmpty()){
							LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
							for (SpecialReward autoCommToSend : itemBasedAutoComm) {
								autoCommGen.sendSpecialRewardTemplate(Long.parseLong(autoCommToSend.getAutoCommEmail()),
								Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
								contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
								contactsLoyalty.getLoyaltyId(), autoCommToSend, earnedPoints+"");
							}
						}
					// Expiry transaction
					createExpiryTransaction(contactsLoyalty, earnedPoints, (double) earnedAmount, childTx.getTransChildId(),
							OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, null,null);
					String[] diffArr = applyConversionRules(contactsLoyalty, tier);
					// 0 - amountdiff, 1 - pointsdiff
					logger.info("balances After conversion rules updatation --  points = " + contactsLoyalty.getLoyaltyBalance()
							+ " currency = " + contactsLoyalty.getGiftcardBalance());
					long convertPointsForBonus = 0;
					long convertAmountForBonus = 0;
					if (diffArrForBonus != null) {
						convertAmountForBonus = Double.valueOf(diffArrForBonus[0].trim()).longValue();
						convertPointsForBonus = Double.valueOf(diffArrForBonus[1].trim()).longValue();
					}
					pointsDifferenceForBonus = earnedPointsForBonus - convertPointsForBonus;
					// amountDifference = earnedAmount + convertAmount;
					amountDifferenceForBonus = earnedAmountForBonus + (diffArrForBonus != null ? Double.parseDouble(diffArrForBonus[0].trim()) : 0.0);
					
					String conversionRate = null;
					long convertPoints = 0;
					double convertAmount = 0;
					if (diffArr != null) {
						convertAmount = Double.valueOf(diffArr[0].trim());
						convertPoints = Double.valueOf(diffArr[1].trim()).longValue();
						conversionRate = diffArr[2];
					}

					pointsDifference = earnedPoints - convertPoints;
					// amountDifference = earnedAmount + convertAmount;
					amountDifference = earnedAmount + (diffArr != null ? Double.parseDouble(diffArr[0].trim()) : 0.0);
					logger.info("2.contactsLoyalty==>"+contactsLoyalty+ "loyaltyProgram==>"+ loyaltyProgram+"tier===>"+ tier+"autocomm===>"+autoComm+"user===>"+user +"issuanceRequest==>"+issuanceRequest);
					//tier = applyTierUpgradeRule(contactsLoyalty, loyaltyProgram, tier, autoComm, user, issuanceRequest); //APP-1769
					
					updatePurchaseTransaction(childTx, contactsLoyalty, Constants.STRING_NILL + pointsDifference,
							Constants.STRING_NILL + amountDifference, conversionRate, convertAmount, tier);
					
					// Event Trigger sending part
					EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator
							.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
					EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator
							.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
					eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
					EventTriggerDao eventTriggerDao = (EventTriggerDao) ServiceLocator.getInstance()
							.getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
					List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(childTx.getUserId(),
							Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
					logger.debug("etList ::" + etList);
					if (etList != null) {
						eventTriggerEventsObservable.notifyToObserver(etList, childTx.getTransChildId(),
								childTx.getTransChildId(), childTx.getUserId(), OCConstants.LOYALTY_ISSUANCE,
								Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
					}
					List<EventTrigger> retList = eventTriggerDao.findAllETByUserAndType(childTx.getUserId(),
							Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
					
					if (retList != null) {
						eventTriggerEventsObservable.notifyToObserver(retList, childTx.getTransChildId(),
								childTx.getTransChildId(), childTx.getUserId(), OCConstants.LOYALTY_ISSUANCE,
								Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
					}
					balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL + pointsDifference,
							Constants.STRING_NILL + amountDifference, Constants.STRING_NILL);
					holdBalance = prepareHoldBalances(contactsLoyalty, Constants.STRING_NILL);
					// bonusArrList = calculateThresholdBonus(contactsLoyalty, loyaltyProgram,
					// fromLtyBalance, fromAmtBalance);
					//calculateThresholdBonus(contactsLoyalty, loyaltyProgram, fromLtyBalance, fromAmtBalance, fromCPVBalance, fromLPVBalance , tier, autoComm,
						//	user, issuanceRequest, pointsDifference, amountDifference,AfterLtyBalanceForBonus, AfterAmtBalanceForBonus, isExchange, returndAmount );

				
					//copy paste the entire code from the above if(activate ==N) block
					
				}
		}
		//applyMultipleTierUpgradeRule
		if(currtier!=null && currtier.getMultipleTierUpgrdRules()!=null && !currtier.getMultipleTierUpgrdRules().isEmpty())
			tier = applyMultipleTierUpgradeRule(contactsLoyalty, currtier, autoComm, user);
		else
		    tier = applyTierUpgradeRule(contactsLoyalty, loyaltyProgram, currtier, autoComm, user, issuanceRequest);//APP-1769
		
		//calling bonus after tier upgrade - APP-4508
		boolean tierUpgd = false;
		if (!currtier.getTierType().equalsIgnoreCase(tier.getTierType())) {
			tierUpgd = true;
		}
		calculateThresholdBonus(contactsLoyalty, loyaltyProgram, fromLtyBalance, fromAmtBalance, fromCPVBalance, fromLPVBalance , tier, autoComm,
				user, issuanceRequest, pointsDifference, amountDifference,AfterLtyBalanceForBonus, AfterAmtBalanceForBonus, isExchange, returndAmount,tierUpgd);
		
		List<ContactsLoyalty> loyaltyList = new ArrayList<ContactsLoyalty>();
		loyaltyList.add(contactsLoyalty);

		saveContactsLoyalty(contactsLoyalty);// If-else not required as the flow is different now.
		
		autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());
		LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
		if (autoComm != null && autoComm.getIssuanceAutoEmailTmplId() != null
				&& contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
			
			logger.info("Inside LoyaltyIssuance AutoComm 1");
			contact = findContactById(contactsLoyalty.getContact().getContactId());
			if (contact != null && contact.getEmailId() != null) {
				autoCommGen.sendLoyaltyIssueTemplate(autoComm.getIssuanceAutoEmailTmplId(),
						Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
						contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
						contactsLoyalty.getLoyaltyId());
				logger.info("Inside LoyaltyIssuance AutoComm");
			}
		}
		if (user.isEnableSMS() && autoComm != null && autoComm.getIssuanceAutoSmsTmplId() != null
				&& contactsLoyalty.getMobilePhone() != null) {
			// Contacts contact =
			// findContactById(contactsLoyalty.getContact().getContactId());
			Long contactId = null;
			if (contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
				String receiptAmount = issuanceRequest.getAmount().getReceiptAmount();
				autoCommGen.sendLoyaltyIssueSMSTemplate(autoComm.getIssuanceAutoSmsTmplId(), user,
						contactsLoyalty.getContact().getContactId(), contactsLoyalty.getLoyaltyId(),
						contactsLoyalty.getMobilePhone(), (issuanceRequest.getHeader().getReceiptNumber() != null
						&& !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty()
						? issuanceRequest.getHeader().getReceiptNumber()
						: "NA"), (receiptAmount != null && 
						!receiptAmount.isEmpty() ? receiptAmount : Constants.STRING_NILL) );
			}
		}
		/*
		 * if(issuanceRequest.getAmount().getType().equals(OCConstants.
		 * LOYALTY_TYPE_GIFT) && issuanceRequest.getAmount().getEnteredValue() != null
		 * && !issuanceRequest.getAmount().getEnteredValue().trim().isEmpty()){
		 * saveContactsLoyalty(contactsLoyalty);
		 * 
		 * } else{ saveContactsLoyalty(contactsLoyalty);
		 * 
		 * }
		 */
		}catch(Exception e) {
			logger.error("Exception ",e);
		}
	}
	
	void IssuancePerkDBOperations(LoyaltyProgram loyaltyProgram, LoyaltyIssuanceRequest issuanceRequest,
			ContactsLoyalty contactsLoyalty, Users user,
			ResponseHeader responseHeader) throws Exception {
		
		
		
		Double isuanceAmount =0.0;
		Double earnedValue = 0.0;
		
		
		LoyaltyProgramTier tier = null; //getLoyaltyTier(contactsLoyalty.getProgramTierId());
		if(contactsLoyalty.getProgramTierId() != null) {
			tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());	
		} else {
			
			if(contactsLoyalty.getProgramId() != null && contactsLoyalty.getUserId() != null){
				List<LoyaltyProgramTier> tierList = validateTierList(contactsLoyalty.getProgramId(), contactsLoyalty.getUserId());
				tier = tierList.get(0);
			}
		}
		
		
		String earnedValueStr = issuanceRequest.getAmount().getEnteredValue();
		earnedValue = earnedValueStr != null && !earnedValueStr.isEmpty() ? 
				Double.parseDouble(earnedValueStr) : earnedValue;
		
		String earntype = tier.getEarnType(); 
		String result = Utility.truncateUptoTwoDecimal(earnedValue);
		if (result != null)
			earnedValue = Double.parseDouble(result);
		
		logger.info("earnedValue = " + earnedValue);
		isuanceAmount = earnedValue;
		
		updateRewardBalances(user.getUserId(),user.getUserOrganization().getUserOrgId(), contactsLoyalty.getProgramId(),contactsLoyalty.getLoyaltyId(),
				earntype, earnedValueStr,contactsLoyalty.getCardNumber());
		
		LoyaltyTransactionChild childTx = createPerkTransaction(issuanceRequest,isuanceAmount, contactsLoyalty, 0.0, 0.0, earntype, earnedValue,
				OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD, user.getUserOrganization().getUserOrgId(),
				responseHeader.getTransactionId(), OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED, isuanceAmount);
		
		createRewardExpiryTransaction(contactsLoyalty,0l, 0.0,  childTx.getTransChildId(), 
				OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,earnedValue,earntype,null,tier.getTierId(),loyaltyProgram.getProgramId());
		
		//calling auto comm on perk issuance
		LoyaltyAutoComm autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());
		Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
		
		autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());
		LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
		if (autoComm != null && autoComm.getIssuanceAutoEmailTmplId() != null
				&& contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
			
			logger.info("Inside LoyaltyPerkIssuance AutoComm 1");
			contact = findContactById(contactsLoyalty.getContact().getContactId());
			if (contact != null && contact.getEmailId() != null) {
				autoCommGen.sendLoyaltyIssueTemplate(autoComm.getIssuanceAutoEmailTmplId(),
						Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
						contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
						contactsLoyalty.getLoyaltyId());
				logger.info("Inside LoyaltyPerkIssuance AutoComm");
			}
		}
		if (user.isEnableSMS() && autoComm != null && autoComm.getIssuanceAutoSmsTmplId() != null
				&& contactsLoyalty.getMobilePhone() != null) {
			// Contacts contact =
			// findContactById(contactsLoyalty.getContact().getContactId());
			Long contactId = null;
			if (contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
				String enteredAmount = issuanceRequest.getAmount().getEnteredValue();
				autoCommGen.sendLoyaltyIssueSMSTemplate(autoComm.getIssuanceAutoSmsTmplId(), user,
						contactsLoyalty.getContact().getContactId(), contactsLoyalty.getLoyaltyId(),
						contactsLoyalty.getMobilePhone(), (issuanceRequest.getHeader().getReceiptNumber() != null
						&& !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty()
						? issuanceRequest.getHeader().getReceiptNumber()
						: "NA"), (enteredAmount != null && 
						!enteredAmount.isEmpty() ? enteredAmount : Constants.STRING_NILL) );
			}
		}
		
	}

	void IssuanceDBGiftInventoryOperations(LoyaltyProgram loyaltyProgram, LoyaltyIssuanceRequest issuanceRequest,
			ContactsLoyalty contactsLoyalty, Users user, ResponseHeader responseHeader, LoyaltyCards loyaltyCard)
			throws Exception {

		saveContactsLoyalty(contactsLoyalty);

		// update loyalty card status
		updateCardStatus(OCConstants.LOYALTY_CARD_STATUS_ACTIVATED, loyaltyCard);

		// MembershipResponse response = prepareAccountIssuanceResponse(contactsLoyalty,
		// null, loyaltyProgram);

		LoyaltyTransactionChild tranx = createGiftTransaction(responseHeader.getTransactionId(), issuanceRequest,
				contactsLoyalty, Constants.STRING_NILL, issuanceRequest.getAmount().getEnteredValue());

		createExpiryTransaction(contactsLoyalty, 0L, Double.valueOf(issuanceRequest.getAmount().getEnteredValue()),
				tranx.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, null,null);

		// send loyalty threshold alerts...
		LoyaltyProgramHelper.sendLowCardsThresholdAlerts(user, loyaltyProgram, false);

		LoyaltyAutoComm autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());
		LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
		if (autoComm != null && autoComm.getGiftCardIssuanceEmailTmpltId() != null
				&& contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
			Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
			if (contact != null && contact.getEmailId() != null) {
				autoCommGen.sendGiftIssueTemplate(autoComm.getGiftCardIssuanceEmailTmpltId(),
						Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
						contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
						contactsLoyalty.getLoyaltyId());
			}
		}
		if (user.isEnableSMS() && autoComm != null && autoComm.getGiftCardIssuanceSmsTmpltId() != null
				&& contactsLoyalty.getMobilePhone() != null) {
			// Contacts contact =
			// findContactById(contactsLoyalty.getContact().getContactId());
			Long contactId = null;
			if (contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
				autoCommGen.sendGiftIssueSMSTemplate(autoComm.getGiftCardIssuanceSmsTmpltId(), user,
						contactsLoyalty.getContact().getContactId(), contactsLoyalty.getLoyaltyId(),
						contactsLoyalty.getMobilePhone());
			}
		}

		List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
		contactLoyaltyList.add(contactsLoyalty);
		// List<MatchedCustomer> matchedCustomers =
		// prepareMatchedCustomers(contactLoyaltyList);

	}

	private void updateCardStatus(String status, LoyaltyCards loyaltyCard) throws Exception {

		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML) ServiceLocator.getInstance()
				.getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
		loyaltyCard.setStatus(status);
		if (status.equals(OCConstants.LOYALTY_CARD_STATUS_ACTIVATED)
				|| status.equals(OCConstants.LOYALTY_CARD_STATUS_ENROLLED)) {
			loyaltyCard.setActivationDate(Calendar.getInstance());
		}
		// loyaltyCardsDao.saveOrUpdate(loyaltyCard);
		loyaltyCardsDaoForDML.saveOrUpdate(loyaltyCard);

	}
	//APP-4064
	private String getSPTypeOnProduct(SpecialReward specialReward ) {
		
		String spRule = specialReward.getRewardRule();
		String[] ruleArr = spRule.split("\\|\\|");
		String ProductRuleType = "";
		for (int i = 0; i < ruleArr.length; i++) {
			
			String rule=ruleArr[i].trim();
			String[] subRuleTokenArr = rule.split("<OR>");
			
			for (int tokenIndex = 0;tokenIndex<subRuleTokenArr.length ;tokenIndex++) {
				//logger.info("subRuleTokenArr===>"+subRuleTokenArr);	
				
				String subRule = subRuleTokenArr[tokenIndex];
				String[] ruleTokensArr = subRule.split(Constants.ADDR_COL_DELIMETER);
				String ruleHashTag = ruleTokensArr[0];
				if(ruleHashTag.equalsIgnoreCase("[#ItemFactor#]")){ 
					//take out the qty+single/bulk + discount include / exclude
					ProductRuleType = ProductRuleType != null ? "B" : "IF";
				}else if(ruleHashTag.equalsIgnoreCase("[#PurchasedItem#]")) {
					ProductRuleType = ProductRuleType != null ? "B" : "PI";
				}
				if(ProductRuleType.equals("B")) return ProductRuleType;
				
			}
			
		}
		
		return ProductRuleType;
	}
	private LoyaltyAutoComm getLoyaltyAutoComm(Long programId) throws Exception {
		LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
		return autoCommDao.findById(programId);
	}
	
	private SpecialReward findHighestMultiplier(List<SpecialReward> multiplierSet) {
		SpecialReward higestMultiplier = null;
		int multiplier = 1;
		int highestmultiplier = 1;
		for (SpecialReward specialReward : multiplierSet) {
			
			if(higestMultiplier == null ) higestMultiplier = specialReward;
			
			multiplier =(int) Double.parseDouble(specialReward.getRewardValue());
			if(multiplier > highestmultiplier) {
				higestMultiplier = specialReward;
				highestmultiplier = multiplier;
			}
		}
		return higestMultiplier;
	}

	private void addEarnValueToHoldBalances(Double purchaseAmountdbl,ContactsLoyalty contactsLoyalty, String earnType, double earnAmount,
			double earnedPoints) {
		
		if(contactsLoyalty.getCummulativePurchaseValue() == null) {
			
			contactsLoyalty.setCummulativePurchaseValue(purchaseAmountdbl);
			
		}else {
			contactsLoyalty.setCummulativePurchaseValue(contactsLoyalty.getCummulativePurchaseValue() + purchaseAmountdbl);
		}
		
		if (earnType != null && earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)) {

			if (contactsLoyalty.getHoldPointsBalance() == null) {
				// contactsLoyalty.setHoldPointsBalance(earnValue);
				contactsLoyalty.setHoldPointsBalance(earnedPoints);
			} else {
				// contactsLoyalty.setHoldPointsBalance(contactsLoyalty.getHoldPointsBalance()+earnValue);
				contactsLoyalty.setHoldPointsBalance(contactsLoyalty.getHoldPointsBalance() + earnedPoints);
			}

		} else if (earnType != null && earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)) {
			if (contactsLoyalty.getHoldAmountBalance() == null) {
				// contactsLoyalty.setHoldAmountBalance(earnValue);
				contactsLoyalty.setHoldAmountBalance(earnAmount);
			} else {
				// contactsLoyalty.setHoldAmountBalance(contactsLoyalty.getHoldAmountBalance()+earnValue);
				contactsLoyalty
						.setHoldAmountBalance((new BigDecimal(contactsLoyalty.getHoldAmountBalance() + earnAmount))
								.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

			}
		}

	}
	private void updateRewardBalances(Long userId,Long orgId, Long programId, Long loyaltyId, String valueCode, String earnedReward,String cardNumber) {
		LoyaltyBalanceDao loyaltyBalanceDao = null;
		LoyaltyBalanceDaoForDML loyaltyBalanceDaoforDML = null;
		Double earnedRewardDouble = Double.parseDouble(earnedReward);
		Long earnedRewardLong = (new Double(earnedRewardDouble)).longValue();
		try {
			loyaltyBalanceDao = (LoyaltyBalanceDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
			loyaltyBalanceDaoforDML = (LoyaltyBalanceDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName (OCConstants.LOYALTY_BALANCE_DAO_FOR_DML);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LoyaltyBalance rewardBalance = loyaltyBalanceDao.findBy(userId, programId, loyaltyId, valueCode);
		
		if(rewardBalance == null){
			rewardBalance =  new LoyaltyBalance();
			rewardBalance.setTotalEarnedBalance(Double.parseDouble(earnedReward)); 
			rewardBalance.setBalance(earnedRewardLong);
			rewardBalance.setCreatedDate(Calendar.getInstance());
			rewardBalance.setValueCode(valueCode);
			rewardBalance.setProgramId(programId);
			rewardBalance.setOrgId(orgId);
			rewardBalance.setUserId(userId);
			rewardBalance.setLoyaltyId(loyaltyId);
			rewardBalance.setMemberShipNumber(cardNumber);//
			
		
		}else{
			
			rewardBalance.setTotalEarnedBalance(rewardBalance.getTotalEarnedBalance()+Double.parseDouble(earnedReward)); 
			rewardBalance.setBalance(earnedRewardLong+rewardBalance.getBalance());
			rewardBalance.setCreatedDate(Calendar.getInstance());
			rewardBalance.setValueCode(valueCode);
			rewardBalance.setProgramId(programId);
			rewardBalance.setOrgId(orgId);
			rewardBalance.setUserId(userId);
			rewardBalance.setLoyaltyId(loyaltyId);
			rewardBalance.setMemberShipNumber(cardNumber);//
			
		}
		logger.info("Loyalty_balance after updation for valCode:"+ rewardBalance.getBalance());

		loyaltyBalanceDaoforDML.saveOrUpdate(rewardBalance);
	}
	/*private void updateContactLoyaltyBalances(Double earnedAmount, Double earnedPoints, String valueCode,
			ContactsLoyalty contactsLoyalty) throws Exception {
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

		} else if (valueCode != null && valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
			if (contactsLoyalty.getGiftcardBalance() == null) {
				// contactsLoyalty.setGiftcardBalance(earned);
				contactsLoyalty.setGiftcardBalance(earnedAmount);
			} else {
				// contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() +
				// earned);
				
				 * String result =
				 * Utility.truncateUptoTwoDecimal(contactsLoyalty.getGiftcardBalance() +
				 * earnedAmount); logger.info("GiftCard balance:::"+result);
				 * contactsLoyalty.setGiftcardBalance(Double.parseDouble(result));
				 
				contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + earnedAmount)
						.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			if (contactsLoyalty.getTotalGiftcardAmount() == null) {
				// contactsLoyalty.setTotalGiftcardAmount(earned);
				contactsLoyalty.setTotalGiftcardAmount(earnedAmount);
			} else {
				// contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount()
				// + earned);y
				
				 * String res =
				 * Utility.truncateUptoTwoDecimal(contactsLoyalty.getTotalGiftcardAmount() +
				 * earnedAmount);
				 * contactsLoyalty.setTotalGiftcardAmount(Double.parseDouble(res));
				 
				contactsLoyalty
						.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + earnedAmount)
								.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}

		}

	}*/
	private LoyaltyTransactionChild createRewardTransaction(LoyaltyIssuanceRequest issuanceRequest,
			Double purchaseAmount, ContactsLoyalty loyalty, double earnedReward, String earnType,
			String entAmountType, Long orgId,long earnedPoints,double earnedAmount, String rewardDiff, String transactionId, String earnStatus,String some,
			double itemExcludedAmt, double excludedDiscountedAmt, String enterdAmount,Long RewardId,
			String rewardRule,SpecialReward reward, double issuanceAmount) {//I have to add item excluded amount with each call?
		



		LoyaltyTransactionChild transaction = null;
		try {

			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(transactionId));
			transaction.setMembershipNumber(Constants.STRING_NILL + loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			transaction.setDescription2(reward.getItemsSatisfied() != null ? reward.getItemsSatisfied()+Constants.ADDR_COL_DELIMETER+reward.getRewardValue() : null);
			
			transaction.setItemInfo(reward.getItemsSatisfied() != null ? reward.getItemsSatisfied()+Constants.ADDR_COL_DELIMETER+reward.getRewardValue() : null);
			// transaction.setCreatedDate(Calendar.getInstance());
			if (issuanceRequest.getMembership().getCreatedDate() != null
					&& !issuanceRequest.getMembership().getCreatedDate().trim().isEmpty()) {

				String requestDate = issuanceRequest.getMembership().getCreatedDate();
				DateFormat formatter;
				Date date;
				formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
				date = (Date) formatter.parse(requestDate);
				Calendar cal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
				cal.setTime(date);

				String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
				int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
				UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				Users user = usersDao.findMlUser(loyalty.getUserId());
				String timezoneDiffrenceMinutes = user.getClientTimeZone();
				logger.info(timezoneDiffrenceMinutes);
				int timezoneDiffrenceMinutesInt = 0;
				if (timezoneDiffrenceMinutes != null)
					timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
				timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
				logger.info("Client time to Server Time.." + timezoneDiffrenceMinutesInt);
				cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
				logger.info("Client time to Server Time Calendar.." + cal);
				transaction.setCreatedDate(cal);

			} else {
				transaction.setCreatedDate(Calendar.getInstance());
			}
			// transaction.setAmountDifference(amtDiff);
				transaction.setEarnType(earnType);
				
				// transaction.setNetEarnedAmount((double)earnedValue);
				if (earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)) {
					// transaction.setEarnedPoints((double)earnedValue);
					transaction.setEarnedPoints((double) earnedPoints);
					// transaction.setNetEarnedPoints((double)earnedValue);
				} else if (earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)) {
					// transaction.setEarnedAmount((double)earnedValue);
					transaction.setEarnedAmount((double) earnedAmount);
					// transaction.setNetEarnedAmount((double)earnedValue);
				}else{
					
					transaction.setEarnedReward((double) earnedReward);
				}
			transaction.setEarnStatus(earnStatus);
			transaction.setEnteredAmount((double) purchaseAmount);
			transaction.setExcludedAmount(itemExcludedAmt);
			transaction.setEnteredAmountType(entAmountType);
			transaction.setOrgId(orgId);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setEarnedRule(rewardRule);
			//Special rewards
			transaction.setRewardDifference((rewardDiff!=null && !rewardDiff.isEmpty()) 
					? Utility.truncateUptoTwoDecimal(Double.valueOf(rewardDiff)) : Constants.STRING_NILL);
			transaction.setRewardBalance(earnedReward);
			transaction.setValueCode(reward.getRewardValueCode());
			transaction.setExcludedItemAmount(itemExcludedAmt);
			transaction.setSpecialRewardId(reward.getRewardId());
			
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);
			transaction.setSubsidiaryNumber(issuanceRequest.getHeader().getSubsidiaryNumber() != null
					&& !issuanceRequest.getHeader().getSubsidiaryNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getSubsidiaryNumber().trim()
							: null);
			logger.debug("storeNumber is===>" + issuanceRequest.getHeader().getStoreNumber());
			transaction.setStoreNumber(issuanceRequest.getHeader().getStoreNumber() != null
					&& !issuanceRequest.getHeader().getStoreNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getStoreNumber()
							: null);
			transaction.setReceiptNumber(issuanceRequest.getHeader().getReceiptNumber() != null
					&& !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getReceiptNumber()
							: null);

			// transaction.setStoreNumber(issuanceRequest.getHeader().getStoreNumber());
			transaction.setEmployeeId(issuanceRequest.getHeader().getEmployeeId() != null
					&& !issuanceRequest.getHeader().getEmployeeId().trim().isEmpty()
							? issuanceRequest.getHeader().getEmployeeId().trim()
							: null);
			transaction.setTerminalId(issuanceRequest.getHeader().getTerminalId() != null
					&& !issuanceRequest.getHeader().getTerminalId().trim().isEmpty()
							? issuanceRequest.getHeader().getTerminalId().trim()
							: null);
			transaction.setDocSID(issuanceRequest.getHeader().getDocSID());
			transaction.setDescription(enterdAmount);
			// transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE);
			transaction.setSourceType(issuanceRequest.getHeader().getSourceType());
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
			// transaction.setEventTriggStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setIssuanceAmount(issuanceAmount);
			// transaction.setRuleType(ruleType);
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			// loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			logger.debug("Issuance Transaction Id:::" + transaction.getTransChildId());
		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
		return transaction;
	
		
		
		
		
	
		
		
	}

	private LoyaltyTransactionChild createMultiplierRewardTransaction(LoyaltyIssuanceRequest issuanceRequest,
			Double purchaseAmount, ContactsLoyalty loyalty, double earnedAmount, long earnedPoints, String earnType,
			String entAmountType, Long orgId, String ptsDiff, String amtDiff, String transactionId, String earnStatus,
			String conversionRate, double itemExcludedAmt, double convertAmt, String activationDate, Long SpID,String earnedRule,
			SpecialReward reward,Double earnedReward, double issuanceAmount) {


		LoyaltyTransactionChild transaction = null;
		try {

			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(transactionId));
			transaction.setMembershipNumber(Constants.STRING_NILL + loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			transaction.setSpecialRewardId(SpID);
			transaction.setDescription2(reward.getItemsSatisfied());
			transaction.setItemInfo(reward.getItemsSatisfied());
			transaction.setIssuanceAmount(issuanceAmount);
			// transaction.setCreatedDate(Calendar.getInstance());
			if (issuanceRequest.getMembership().getCreatedDate() != null
					&& !issuanceRequest.getMembership().getCreatedDate().trim().isEmpty()) {

				String requestDate = issuanceRequest.getMembership().getCreatedDate();
				DateFormat formatter;
				Date date;
				formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
				date = (Date) formatter.parse(requestDate);
				Calendar cal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
				cal.setTime(date);

				String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
				int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
				UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				Users user = usersDao.findMlUser(loyalty.getUserId());
				String timezoneDiffrenceMinutes = user.getClientTimeZone();
				logger.info(timezoneDiffrenceMinutes);
				int timezoneDiffrenceMinutesInt = 0;
				if (timezoneDiffrenceMinutes != null)
					timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
				timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
				logger.info("Client time to Server Time.." + timezoneDiffrenceMinutesInt);
				cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
				logger.info("Client time to Server Time Calendar.." + cal);
				transaction.setCreatedDate(cal);

			} else {
				transaction.setCreatedDate(Calendar.getInstance());
			}
			// transaction.setAmountDifference(amtDiff);
			transaction.setAmountDifference(Utility.truncateUptoTwoDecimal(Double.valueOf(amtDiff)));
			transaction.setPointsDifference(ptsDiff);
			transaction.setEarnType(earnType);
			if (earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)) {
				// transaction.setEarnedPoints((double)earnedValue);
				transaction.setEarnedPoints((double) earnedPoints);
				// transaction.setNetEarnedPoints((double)earnedValue);
			} else if (earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)) {
				// transaction.setEarnedAmount((double)earnedValue);
				transaction.setEarnedAmount((double) earnedAmount);
				// transaction.setNetEarnedAmount((double)earnedValue);
			}

			
			
			transaction.setEarnStatus(earnStatus);
			transaction.setEnteredAmount((double) purchaseAmount);
			transaction.setExcludedAmount(itemExcludedAmt);
			transaction.setEnteredAmountType(entAmountType);
			transaction.setOrgId(orgId);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setEarnedRule(earnedRule);
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);
			transaction.setSubsidiaryNumber(issuanceRequest.getHeader().getSubsidiaryNumber() != null
					&& !issuanceRequest.getHeader().getSubsidiaryNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getSubsidiaryNumber().trim()
							: null);
			logger.debug("storeNumber is===>" + issuanceRequest.getHeader().getStoreNumber());
			transaction.setStoreNumber(issuanceRequest.getHeader().getStoreNumber() != null
					&& !issuanceRequest.getHeader().getStoreNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getStoreNumber()
							: null);
			transaction.setReceiptNumber(issuanceRequest.getHeader().getReceiptNumber() != null
					&& !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getReceiptNumber()
							: null);

			// transaction.setStoreNumber(issuanceRequest.getHeader().getStoreNumber());
			transaction.setEmployeeId(issuanceRequest.getHeader().getEmployeeId() != null
					&& !issuanceRequest.getHeader().getEmployeeId().trim().isEmpty()
							? issuanceRequest.getHeader().getEmployeeId().trim()
							: null);
			transaction.setTerminalId(issuanceRequest.getHeader().getTerminalId() != null
					&& !issuanceRequest.getHeader().getTerminalId().trim().isEmpty()
							? issuanceRequest.getHeader().getTerminalId().trim()
							: null);
			transaction.setDocSID(issuanceRequest.getHeader().getDocSID());
			transaction.setDescription(conversionRate);
			transaction.setConversionAmt(convertAmt);
			if (activationDate != null) {
				if (earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)) {
					// transaction.setHoldPoints((double)earnedValue);
					transaction.setHoldPoints((double) earnedPoints);
				} else if (earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)) {
					// transaction.setHoldAmount((double)earnedValue);
					transaction.setHoldAmount((double) earnedAmount);
				}
				transaction.setValueActivationDate(new SimpleDateFormat("yyyy-MM-dd").parse(activationDate));
			}
			// transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE);
			transaction.setSourceType(issuanceRequest.getHeader().getSourceType());
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
			// transaction.setEventTriggStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			//Specail Rewards
			//transaction.setRewardDifference(earnedReward+"");
			//transaction.setEarnedReward(earnedReward);
			//transaction.setValueCode(reward.getRewardValueCode());
			transaction.setExcludedItemAmount(itemExcludedAmt);
			
			
			
			// transaction.setRuleType(ruleType);
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			// loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			logger.debug("Issuance Transaction Id:::" + transaction.getTransChildId());
		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
		return transaction;
	
		
		
		
		
	}


	private LoyaltyTransactionChild createPurchaseTransaction(LoyaltyIssuanceRequest issuanceRequest,
			Double purchaseAmount, ContactsLoyalty loyalty, double earnedAmount, double earnedPoints, String earnType,
			String entAmountType, Long orgId, String ptsDiff, String amtDiff, String transactionId, String earnStatus,
			String conversionRate, double itemExcludedAmt, double convertAmt, String activationDate, String tierEarnRule, 
			String itemInfo, double issuanceAmount, String itemRewardInfo) {

		LoyaltyTransactionChild transaction = null;
		try {

			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(transactionId));
			transaction.setMembershipNumber(Constants.STRING_NILL + loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());

			// transaction.setCreatedDate(Calendar.getInstance());
			if (issuanceRequest.getMembership().getCreatedDate() != null
					&& !issuanceRequest.getMembership().getCreatedDate().trim().isEmpty()) {

				String requestDate = issuanceRequest.getMembership().getCreatedDate();
				DateFormat formatter;
				Date date;
				formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
				date = (Date) formatter.parse(requestDate);
				Calendar cal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
				cal.setTime(date);

				String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
				int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
				UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				Users user = usersDao.findMlUser(loyalty.getUserId());
				String timezoneDiffrenceMinutes = user.getClientTimeZone();
				logger.info(timezoneDiffrenceMinutes);
				int timezoneDiffrenceMinutesInt = 0;
				if (timezoneDiffrenceMinutes != null)
					timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
				timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
				logger.info("Client time to Server Time.." + timezoneDiffrenceMinutesInt);
				cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
				logger.info("Client time to Server Time Calendar.." + cal);
				transaction.setCreatedDate(cal);

			} else {
				transaction.setCreatedDate(Calendar.getInstance());
			}
			transaction.setIssuanceAmount(issuanceAmount);
			transaction.setAmountDifference(Utility.truncateUptoTwoDecimal(Double.valueOf(amtDiff)));
			transaction.setPointsDifference(ptsDiff);
			transaction.setEarnType(earnType);
			if (earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)) {
				transaction.setEarnedPoints((double) earnedPoints);
			} else if (earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)) {
				transaction.setEarnedAmount((double) earnedAmount);
			}

			transaction.setEarnStatus(earnStatus);
			logger.debug("purchaseAmount ==="+purchaseAmount);
			transaction.setEnteredAmount((double) purchaseAmount);
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
			transaction.setSubsidiaryNumber(issuanceRequest.getHeader().getSubsidiaryNumber() != null
					&& !issuanceRequest.getHeader().getSubsidiaryNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getSubsidiaryNumber().trim()
							: null);
			logger.debug("storeNumber is===>" + issuanceRequest.getHeader().getStoreNumber());
			transaction.setStoreNumber(issuanceRequest.getHeader().getStoreNumber() != null
					&& !issuanceRequest.getHeader().getStoreNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getStoreNumber()
							: null);
			transaction.setReceiptNumber(issuanceRequest.getHeader().getReceiptNumber() != null
					&& !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getReceiptNumber()
							: null);
			transaction.setEmployeeId(issuanceRequest.getHeader().getEmployeeId() != null
					&& !issuanceRequest.getHeader().getEmployeeId().trim().isEmpty()
							? issuanceRequest.getHeader().getEmployeeId().trim()
							: null);
			transaction.setTerminalId(issuanceRequest.getHeader().getTerminalId() != null
					&& !issuanceRequest.getHeader().getTerminalId().trim().isEmpty()
							? issuanceRequest.getHeader().getTerminalId().trim()
							: null);
			transaction.setDocSID(issuanceRequest.getHeader().getDocSID());
			transaction.setDescription(conversionRate);
			transaction.setDescription2(tierEarnRule);
			transaction.setConversionAmt(convertAmt);
			if (activationDate != null) {
				if (earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)) {
					transaction.setHoldPoints((double) earnedPoints);
				} else if (earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)) {
					transaction.setHoldAmount((double) earnedAmount);
				}
				transaction.setValueActivationDate(new SimpleDateFormat("yyyy-MM-dd").parse(activationDate));
			}
			transaction.setSourceType(issuanceRequest.getHeader().getSourceType());
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setItemInfo(itemInfo);
			transaction.setItemRewardsInfo(itemRewardInfo);
			// transaction.setRuleType(ruleType);
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			// loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			logger.debug("Issuance Transaction Id:::" + transaction.getTransChildId());
		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
		return transaction;
	}
	
	private LoyaltyTransactionChild createPerkTransaction(LoyaltyIssuanceRequest issuanceRequest,Double enteredAmount,
			ContactsLoyalty loyalty, double earnedAmount, double earnedPoints, String earnType, Double earnedReward,
			String entAmountType, Long orgId, String transactionId, String earnStatus, 
			double issuanceAmount) {

		LoyaltyTransactionChild transaction = null;
		try {

			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(transactionId));
			transaction.setMembershipNumber(Constants.STRING_NILL + loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			//transaction.setDescription2(reward.getItemsSatisfied() != null ? reward.getItemsSatisfied()+Constants.ADDR_COL_DELIMETER+reward.getRewardValue() : null);
			
			//transaction.setItemInfo(reward.getItemsSatisfied() != null ? reward.getItemsSatisfied()+Constants.ADDR_COL_DELIMETER+reward.getRewardValue() : null);
			// transaction.setCreatedDate(Calendar.getInstance());
			if (issuanceRequest.getMembership().getCreatedDate() != null
					&& !issuanceRequest.getMembership().getCreatedDate().trim().isEmpty()) {

				String requestDate = issuanceRequest.getMembership().getCreatedDate();
				DateFormat formatter;
				Date date;
				formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
				date = (Date) formatter.parse(requestDate);
				Calendar cal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
				cal.setTime(date);

				String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
				int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
				UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				Users user = usersDao.findMlUser(loyalty.getUserId());
				String timezoneDiffrenceMinutes = user.getClientTimeZone();
				logger.info(timezoneDiffrenceMinutes);
				int timezoneDiffrenceMinutesInt = 0;
				if (timezoneDiffrenceMinutes != null)
					timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
				timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
				logger.info("Client time to Server Time.." + timezoneDiffrenceMinutesInt);
				cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
				logger.info("Client time to Server Time Calendar.." + cal);
				transaction.setCreatedDate(cal);

			} else {
				transaction.setCreatedDate(Calendar.getInstance());
			}
			// transaction.setAmountDifference(amtDiff);
				transaction.setEarnType(earnType);
				
				// transaction.setNetEarnedAmount((double)earnedValue);
				/*if (earnType.equals(OCConstants.LOYALTY_TYPE_PERKS)) {
					// transaction.setEarnedPoints((double)earnedValue);
					transaction.setEarnedPoints((double) earnedPoints);
					// transaction.setNetEarnedPoints((double)earnedValue);
				} else if (earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)) {
					// transaction.setEarnedAmount((double)earnedValue);
					transaction.setEarnedAmount((double) earnedAmount);
					// transaction.setNetEarnedAmount((double)earnedValue);
				}else{
					
					transaction.setEarnedReward((double) earnedReward);
				}*/
			//transaction.setEarnedPoints(earnedPoints);
			//transaction.setEarnedAmount(earnedAmount);
			transaction.setEarnedReward(earnedReward);	
			transaction.setEarnStatus(earnStatus);
			transaction.setEnteredAmount(enteredAmount);
			//transaction.setExcludedAmount(itemExcludedAmt);
			transaction.setEnteredAmountType(entAmountType);
			transaction.setOrgId(orgId);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			//transaction.setEarnedRule(rewardRule);
			//Special rewards
			//transaction.setRewardDifference((rewardDiff!=null && !rewardDiff.isEmpty()) 
				//	? Utility.truncateUptoTwoDecimal(Double.valueOf(rewardDiff)) : Constants.STRING_NILL);
			transaction.setRewardBalance(earnedReward);
			transaction.setValueCode(earnType);
			//transaction.setExcludedItemAmount(itemExcludedAmt);
			//transaction.setSpecialRewardId(reward.getRewardId());
			
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);
			transaction.setSubsidiaryNumber(issuanceRequest.getHeader().getSubsidiaryNumber() != null
					&& !issuanceRequest.getHeader().getSubsidiaryNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getSubsidiaryNumber().trim()
							: null);
			logger.debug("storeNumber is===>" + issuanceRequest.getHeader().getStoreNumber());
			transaction.setStoreNumber(issuanceRequest.getHeader().getStoreNumber() != null
					&& !issuanceRequest.getHeader().getStoreNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getStoreNumber()
							: null);
			transaction.setReceiptNumber(issuanceRequest.getHeader().getReceiptNumber() != null
					&& !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getReceiptNumber()
							: null);

			// transaction.setStoreNumber(issuanceRequest.getHeader().getStoreNumber());
			transaction.setEmployeeId(issuanceRequest.getHeader().getEmployeeId() != null
					&& !issuanceRequest.getHeader().getEmployeeId().trim().isEmpty()
							? issuanceRequest.getHeader().getEmployeeId().trim()
							: null);
			transaction.setTerminalId(issuanceRequest.getHeader().getTerminalId() != null
					&& !issuanceRequest.getHeader().getTerminalId().trim().isEmpty()
							? issuanceRequest.getHeader().getTerminalId().trim()
							: null);
			transaction.setDocSID(issuanceRequest.getHeader().getDocSID());
			//transaction.setDescription(enterdAmount);
			// transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE);
			transaction.setSourceType(issuanceRequest.getHeader().getSourceType());
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
			// transaction.setEventTriggStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setIssuanceAmount(issuanceAmount);
			// transaction.setRuleType(ruleType);
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			// loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			logger.debug("Issuance Transaction Id:::" + transaction.getTransChildId());
		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
		return transaction;
	}

	private void saveContactsLoyalty(ContactsLoyalty contactsLoyalty) throws Exception {
		ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance()
				.getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		loyaltyDao.saveOrUpdate(contactsLoyalty);
	}

	private HoldBalance prepareHoldBalances(ContactsLoyalty contactsLoyalty, String activationPeriod) {

		HoldBalance holdBalance = new HoldBalance();
		holdBalance.setActivationPeriod(activationPeriod);
		// holdBalance.setCurrency(contactsLoyalty.getHoldAmountBalance() == null ?
		// Constants.STRING_NILL :
		// Constants.STRING_NILL+contactsLoyalty.getHoldAmountBalance());
		if (contactsLoyalty.getHoldAmountBalance() == null) {
			holdBalance.setCurrency(Constants.STRING_NILL);
		} else {
			double value = new BigDecimal(contactsLoyalty.getHoldAmountBalance()).setScale(2, BigDecimal.ROUND_HALF_UP)
					.doubleValue();
			holdBalance.setCurrency(Constants.STRING_NILL + value);
		}

		holdBalance.setPoints(contactsLoyalty.getHoldPointsBalance() == null ? Constants.STRING_NILL
				: Constants.STRING_NILL + contactsLoyalty.getHoldPointsBalance().intValue());
		return holdBalance;

	}

	private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty, Long expiryPoints,
			Double expiryAmount, Long transChildId, String rewardFlag, Long specialRewardId,Long bonusId) {

		LoyaltyTransactionExpiry transaction = null;
		try {

			transaction = new LoyaltyTransactionExpiry();
			transaction.setTransChildId(transChildId);
			transaction.setMembershipNumber(Constants.STRING_NILL + loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setExpiryPoints(expiryPoints);
			transaction.setExpiryAmount(expiryAmount);
			transaction.setRewardFlag(rewardFlag);
			transaction.setSpecialRewardId(specialRewardId);//neded in case of multipliers
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setBonusId(bonusId);
			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			// loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
			loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);

		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
		return transaction;
	}
	private LoyaltyTransactionExpiry createRewardExpiryTransaction(ContactsLoyalty loyalty, 
			 Long expiryPoints,Double expiryAmount, Long transChildId, String rewardFlag,Double earnedReward,
			 String valueCode,Long specialRewardId, Long tierId,Long prgmId) {

		LoyaltyTransactionExpiry transaction = null;
		try {

			transaction = new LoyaltyTransactionExpiry();
			transaction.setTransChildId(transChildId);
			transaction.setMembershipNumber(Constants.STRING_NILL + loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setExpiryPoints(expiryPoints);
			transaction.setExpiryAmount(expiryAmount);
			transaction.setRewardFlag(rewardFlag);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setExpiryReward(earnedReward==null?0l:earnedReward.longValue());
			transaction.setValueCode(valueCode);
			transaction.setSpecialRewardId(specialRewardId);
			transaction.setTierId(tierId); //for perk expiry trx
			transaction.setProgramId(prgmId); //for perk expiry trx
			
			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			// loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
			loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);

		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
		return transaction;
	}
	private String[] applyConversionRules(ContactsLoyalty contactsLoyalty,
			LoyaltyProgramTier tier, Double EarnedValue, String earnType) {
		


		String[] differenceArr = null;

		try {

			if (tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)) {

				if (tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 &&
						
						(contactsLoyalty.getLoyaltyBalance() == null ? 0+EarnedValue:contactsLoyalty.getLoyaltyBalance()+EarnedValue) >= tier.getConvertFromPoints()){
					

					differenceArr = new String[3];

					double multipledouble = (contactsLoyalty.getLoyaltyBalance() == null ? 0+EarnedValue:contactsLoyalty.getLoyaltyBalance()+EarnedValue) / tier.getConvertFromPoints();
					int multiple = (int) multipledouble;
					// double convertedAmount = tier.getConvertToAmount() * multiple;
					double convertedAmount = 0.0;
					String result = Utility.truncateUptoTwoDecimal(tier.getConvertToAmount() * multiple);
					if (result != null)
						convertedAmount = Double.parseDouble(result);
					// double convertedAmount = new BigDecimal(tier.getConvertToAmount() *
					// multiple).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
					double subPoints = multiple * tier.getConvertFromPoints();

					differenceArr[0] = Constants.STRING_NILL + convertedAmount;
					differenceArr[1] = Constants.STRING_NILL + subPoints;
					// differenceArr[2] = tier.getConvertFromPoints().intValue()+" Points ->
					// "+tier.getConvertToAmount().intValue();
					differenceArr[2] = tier.getConvertFromPoints().intValue() + " Points -> "
							+ tier.getConvertToAmount().doubleValue();

					logger.info("multiple factor = " + multiple);
					logger.info("Conversion amount =" + convertedAmount);
					logger.info("subtract points = " + subPoints);

					// update giftcard balance
					/*if (contactsLoyalty.getGiftcardBalance() == null) {
						contactsLoyalty.setGiftcardBalance(convertedAmount);
					} else {
						contactsLoyalty.setGiftcardBalance(
								new BigDecimal(contactsLoyalty.getGiftcardBalance() + convertedAmount)
										.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

					}
					if (contactsLoyalty.getTotalGiftcardAmount() == null) {
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					} else {
						contactsLoyalty.setTotalGiftcardAmount(
								new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount)
										.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}

					// deduct loyalty points
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - subPoints);
					contactsLoyalty
							.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? subPoints
									: contactsLoyalty.getTotalLoyaltyRedemption() + subPoints);

					logger.info("contactsLoyalty.getGiftcardBalance() = " + contactsLoyalty.getGiftcardBalance());

					deductPointsFromExpiryTable(contactsLoyalty, subPoints, convertedAmount);*/
				}
			}

		} catch (Exception e) {
			logger.error("Exception while applying auto conversion rules...", e);
			return null;
		}
		return differenceArr;
	
		
		
	}
	private String[] applyConversionRules(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier) {

		String[] differenceArr = null;

		try {

			if (tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)) {

				if (tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0
						&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints()) {

					differenceArr = new String[3];

					double multipledouble = contactsLoyalty.getLoyaltyBalance() / tier.getConvertFromPoints();
					int multiple = (int) multipledouble;
					// double convertedAmount = tier.getConvertToAmount() * multiple;
					double convertedAmount = 0.0;
					String result = Utility.truncateUptoTwoDecimal(tier.getConvertToAmount() * multiple);
					if (result != null)
						convertedAmount = Double.parseDouble(result);
					// double convertedAmount = new BigDecimal(tier.getConvertToAmount() *
					// multiple).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
					double subPoints = multiple * tier.getConvertFromPoints();

					differenceArr[0] = Constants.STRING_NILL + convertedAmount;
					differenceArr[1] = Constants.STRING_NILL + subPoints;
					// differenceArr[2] = tier.getConvertFromPoints().intValue()+" Points ->
					// "+tier.getConvertToAmount().intValue();
					differenceArr[2] = tier.getConvertFromPoints().intValue() + " Points -> "
							+ tier.getConvertToAmount().doubleValue();

					logger.info("multiple factor = " + multiple);
					logger.info("Conversion amount =" + convertedAmount);
					logger.info("subtract points = " + subPoints);

					// update giftcard balance
					if (contactsLoyalty.getGiftcardBalance() == null) {
						contactsLoyalty.setGiftcardBalance(convertedAmount);
					} else {
						contactsLoyalty.setGiftcardBalance(
								new BigDecimal(contactsLoyalty.getGiftcardBalance() + convertedAmount)
										.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

					}
					if (contactsLoyalty.getTotalGiftcardAmount() == null) {
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					} else {
						contactsLoyalty.setTotalGiftcardAmount(
								new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount)
										.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}

					// deduct loyalty points
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - subPoints);
					contactsLoyalty
							.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? subPoints
									: contactsLoyalty.getTotalLoyaltyRedemption() + subPoints);

					logger.info("contactsLoyalty.getGiftcardBalance() = " + contactsLoyalty.getGiftcardBalance());

					deductPointsFromExpiryTable(contactsLoyalty, subPoints, convertedAmount);
				}
			}

		} catch (Exception e) {
			logger.error("Exception while applying auto conversion rules...", e);
			return null;
		}
		return differenceArr;
	}

	private void deductPointsFromExpiryTable(ContactsLoyalty contactsLoyalty, double subPoints, double earnedAmt)
			throws Exception {

		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML) ServiceLocator
				.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; // expiryDao.fetchExpPointsTrans(Constants.STRING_NILL+membershipNumber,
															// 100, userId);
		Iterator<LoyaltyTransactionExpiry> iterList = null; // expiryList.iterator();
		LoyaltyTransactionExpiry expiry = null;
		long remainingPoints = (long) subPoints;

		do {

			expiryList = expiryDao.fetchExpLoyaltyPtsTrans(contactsLoyalty.getLoyaltyId(), 100,
					contactsLoyalty.getUserId());
			// logger.info("expiryList size = "+expiryList.size());
			if (expiryList == null)
				break;
			iterList = expiryList.iterator();

			while (iterList.hasNext()) {

				logger.info("remainingPoints = " + remainingPoints + " earnedAmt = " + earnedAmt);
				expiry = iterList.next();

				// logger.info("expiry points= "+expiry.getExpiryPoints()+" expiry amount =
				// "+expiry.getExpiryAmount());

				if ((expiry.getExpiryPoints() == null || expiry.getExpiryPoints() <= 0)
						&& (expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0)) {
					logger.info("Wrong entry condition...");
				} else if (expiry.getExpiryPoints() < remainingPoints) {
					logger.info("subtracted points = " + expiry.getExpiryPoints());
					remainingPoints = remainingPoints - expiry.getExpiryPoints().longValue();
					expiry.setExpiryPoints(0l);
					// expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					continue;

				} else if (expiry.getExpiryPoints() >= remainingPoints) {
					logger.info("subtracted points = " + expiry.getExpiryPoints());
					expiry.setExpiryPoints(expiry.getExpiryPoints() - remainingPoints);
					remainingPoints = 0;
					if (expiry.getExpiryAmount() == null) {
						expiry.setExpiryAmount(earnedAmt);
					} else {
						expiry.setExpiryAmount(expiry.getExpiryAmount() + earnedAmt);
					}
					// logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ "
					// earnedAmt = "+earnedAmt);
					// expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					// logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ "
					// earnedAmt = "+earnedAmt);
					break;
				}

			}

		} while (remainingPoints > 0 && expiryList != null);

	}

	private void calculateThresholdBonus(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, Double fromLtyBalance,
			Double fromAmtBalance,Double fromCPVBalance,Double fromLPVBalance , LoyaltyProgramTier tier, LoyaltyAutoComm autoComm, Users user,
			LoyaltyIssuanceRequest issuanceRequest, 
			Long pointsDifference, double amountDifference, 
			double afterpointsForBonus, double AfterAMountForBonus, boolean isExchange, double returnedAmt,boolean tierUpgd) {
		try {
			
			
			fromAmtBalance = fromAmtBalance == null ? 0.0 : fromAmtBalance;
			fromLPVBalance = fromLPVBalance == null ? 0.0 : fromLPVBalance;
			fromLtyBalance = fromLtyBalance == null ? 0.0 : fromLtyBalance;
			fromCPVBalance = fromCPVBalance == null ? 0.0 : fromCPVBalance;
			LoyaltyThresholdBonusDao loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			List<LoyaltyThresholdBonus> threshBonusList = loyaltyThresholdBonusDao
					.getBonusListByPrgmId(program.getProgramId(), 'N');
			List<LoyaltyThresholdBonus> pointsBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> amountBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> LPVBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> TierBonusList = new ArrayList<LoyaltyThresholdBonus>();
			
			Double bonusPointsThreshold = (contactsLoyalty.getBonusPointsThreshold()==null) ? 0.0 : contactsLoyalty.getBonusPointsThreshold();
			Double bonusCurrencyThreshold = (contactsLoyalty.getBonusCurrencyThreshold()==null) ? 0.0 : contactsLoyalty.getBonusCurrencyThreshold();
			Double bonusPointsIterator = (contactsLoyalty.getBonusPointsIterator()==null)? 0.0 : contactsLoyalty.getBonusPointsIterator();
			Double bonusCurrencyIterator = contactsLoyalty.getBonusCurrencyIterator();

			if (threshBonusList == null)
				return;

			for (LoyaltyThresholdBonus bonus : threshBonusList) {
				if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_POINTS)) {
					pointsBonusList.add(bonus);
				} else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)) {
					amountBonusList.add(bonus);
				} else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_LPV)){
					LPVBonusList.add(bonus);
				} else if (bonus.getEarnedLevelType().equals(OCConstants.THRESHOLD_TYPE_TIER)) {
					TierBonusList.add(bonus);
				}
			}

			if (pointsBonusList.isEmpty() && amountBonusList.isEmpty() && LPVBonusList.isEmpty() && TierBonusList.isEmpty())
				return;

			List<LoyaltyThresholdBonus> matchedBonusList = new ArrayList<LoyaltyThresholdBonus>();

			if (pointsBonusList.size() > 0) {
				Collections.sort(pointsBonusList, new Comparator<LoyaltyThresholdBonus>() {
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			if (amountBonusList.size() > 0) {
				Collections.sort(amountBonusList, new Comparator<LoyaltyThresholdBonus>() {
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			if (LPVBonusList.size() > 0) {
				Collections.sort(LPVBonusList, new Comparator<LoyaltyThresholdBonus>() {
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			if (TierBonusList.size() > 0) {
				Collections.sort(TierBonusList, new Comparator<LoyaltyThresholdBonus>() {
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			
			matchedBonusList.addAll(LPVBonusList);
			matchedBonusList.addAll(pointsBonusList);
			matchedBonusList.addAll(amountBonusList);
			matchedBonusList.addAll(TierBonusList);
			
			
			/*if (contactsLoyalty.getTotalLoyaltyEarned() != null && contactsLoyalty.getTotalLoyaltyEarned() > 0) {
				for (LoyaltyThresholdBonus bonus : pointsBonusList) {
					if (contactsLoyalty.getTotalLoyaltyEarned() >= bonus.getEarnedLevelValue()
							&& (fromLtyBalance == null || fromLtyBalance.doubleValue() < bonus.getEarnedLevelValue())) {
						matchedBonusList.add(bonus);
					}
				}
			}
			if (contactsLoyalty.getTotalGiftcardAmount() != null && contactsLoyalty.getTotalGiftcardAmount() > 0) {
				for (LoyaltyThresholdBonus bonus : amountBonusList) {
					if (contactsLoyalty.getTotalGiftcardAmount() >= bonus.getEarnedLevelValue()
							&& (fromAmtBalance == null || fromAmtBalance.doubleValue() < bonus.getEarnedLevelValue())) {
						matchedBonusList.add(bonus);
					}
				}
			}*/
			long bonusPoints = 0;
			double bonusAmount = 0.0;
			String bonusRate = null;
			boolean bonusflag = false;
			LoyaltyTransactionChild transaction = null;
			if (matchedBonusList != null && matchedBonusList.size() > 0) {
				for (LoyaltyThresholdBonus matchedBonus : matchedBonusList) {
					bonusflag = false;
					long multiplier = -1;
					double afterBalLoyaltyEarned= contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.0 : contactsLoyalty.getTotalLoyaltyEarned();
					double afterBalGiftCardAmt= contactsLoyalty.getTotalGiftcardAmount() == null ? 0.0 : contactsLoyalty.getTotalGiftcardAmount();
					double afterBalCPV= contactsLoyalty.getCummulativePurchaseValue() == null ? 0.0 : contactsLoyalty.getCummulativePurchaseValue();
					
					double afterBalLPV= LoyaltyProgramHelper.getLPV(contactsLoyalty);
					afterBalLPV = isExchange ? (afterBalLPV-returnedAmt):afterBalLPV;
					if (OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getEarnedLevelType())) {
						logger.info("---------POINTS-----------");
						logger.info("previous points balance (fromLtyBalance)"+fromLtyBalance);
						logger.info("after points balance (getEarnedLevelValue())"+matchedBonus.getEarnedLevelValue());
						
						//This code is for recurring bonus
						if(matchedBonus.isRecurring()){
							if(!isExchange){
								
								Double beforeFactor = fromLtyBalance.doubleValue()/matchedBonus.getEarnedLevelValue();
								Double afterFactor = afterBalLoyaltyEarned/matchedBonus.getEarnedLevelValue();
								if(beforeFactor.intValue() < afterFactor.intValue()) {
									bonusflag = true;
									multiplier = afterFactor.intValue()-beforeFactor.intValue();
								}
								logger.info("before factor===="+beforeFactor);
								logger.info("after factor===="+afterFactor);
								logger.info("multiplier===="+multiplier);
							}else{
								Double beforeFactor = fromLtyBalance.doubleValue()/matchedBonus.getEarnedLevelValue();
								Double afterFactor = afterpointsForBonus/matchedBonus.getEarnedLevelValue();
								if(beforeFactor.intValue() < afterFactor.intValue()) {
									bonusflag = true;
									multiplier = afterFactor.intValue()-beforeFactor.intValue();
								}
								logger.info("before factor===="+beforeFactor);
								logger.info("after factor===="+afterFactor);
								logger.info("multiplier===="+multiplier);
							}
						}
						else if (! matchedBonus.isRecurring() ){
							
							if(!isExchange){
								if( afterBalLoyaltyEarned >= matchedBonus.getEarnedLevelValue() && 
										(fromLtyBalance == null || fromLtyBalance.doubleValue() < matchedBonus.getEarnedLevelValue()) && 
										bonusPointsThreshold < matchedBonus.getEarnedLevelValue() ){
									multiplier = 1;
									bonusflag = true;
								}
										
							}else{
								if( afterpointsForBonus >= matchedBonus.getEarnedLevelValue() && 
										(fromLtyBalance == null || fromLtyBalance.doubleValue() < matchedBonus.getEarnedLevelValue()) && 
										bonusPointsThreshold < matchedBonus.getEarnedLevelValue() ){
									multiplier = 1;
									bonusflag = true;
								}
								
							}
						}
							
							
					}else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getEarnedLevelType())) {
						
						logger.info("---------AMOUNT-----------");
						logger.info("previous points balance (fromAmtBalance)"+fromAmtBalance);
						logger.info("after points balance (getEarnedLevelValue())"+matchedBonus.getEarnedLevelValue());
						
						if(matchedBonus.isRecurring()){
							if(!isExchange){
								Double beforeFactor = fromAmtBalance.doubleValue()/matchedBonus.getEarnedLevelValue();
								Double afterFactor = afterBalGiftCardAmt/matchedBonus.getEarnedLevelValue();
								if(beforeFactor.intValue() < afterFactor.intValue()){
									bonusflag = true;
									multiplier = afterFactor.intValue()-beforeFactor.intValue();
								}
								logger.info("before factor===="+beforeFactor);
								logger.info("after factor===="+afterFactor);
								logger.info("multiplier===="+multiplier);
								
							}else{
								Double beforeFactor = fromAmtBalance.doubleValue()/matchedBonus.getEarnedLevelValue();
								Double afterFactor = AfterAMountForBonus/matchedBonus.getEarnedLevelValue();
								if(beforeFactor.intValue() < afterFactor.intValue()){
									bonusflag = true;
									multiplier = afterFactor.intValue()-beforeFactor.intValue();
								}
								logger.info("before factor===="+beforeFactor);
								logger.info("after factor===="+afterFactor);
								logger.info("multiplier===="+multiplier);
							}
						
						}else if (! matchedBonus.isRecurring()){
							
							if(!isExchange){
								if(afterBalGiftCardAmt >= matchedBonus.getEarnedLevelValue() && (fromAmtBalance == null || fromAmtBalance.doubleValue() < matchedBonus.getEarnedLevelValue() && bonusCurrencyThreshold < matchedBonus.getEarnedLevelValue())) {
									multiplier = 1;
									bonusflag = true;
								}
							}else{
								if(AfterAMountForBonus >= matchedBonus.getEarnedLevelValue() && (fromAmtBalance == null || fromAmtBalance.doubleValue() < matchedBonus.getEarnedLevelValue() && bonusCurrencyThreshold < matchedBonus.getEarnedLevelValue())) {
									multiplier = 1;
									bonusflag = true;
								}
							}
						}
								
						
					}else if(OCConstants.LOYALTY_TYPE_LPV.equals(matchedBonus.getEarnedLevelType())) {
						
						logger.info("---------LPV-----------");
						logger.info("previous points balance (fromLPVBalance)"+fromLPVBalance);
						logger.info("after points balance (getEarnedLevelValue())"+matchedBonus.getEarnedLevelValue());
						
						/*if(matchedBonus.isRecurring()){
							afterBalLPV= (afterBalLPV==afterBalCPV)  ? afterBalCPV : afterBalLPV;
							Double beforeFactor = fromCPVBalance.doubleValue()/matchedBonus.getEarnedLevelValue();
							Double afterFactor = afterBalLPV/matchedBonus.getEarnedLevelValue();
							if(beforeFactor.intValue() < afterFactor.intValue()){
								bonusflag = true;
								multiplier = afterFactor.intValue()-beforeFactor.intValue();
							}
							logger.info("before factor===="+beforeFactor);
							logger.info("after factor===="+afterFactor);
							logger.info("multiplier===="+multiplier);
						
						}*/
												
						multiplier = LoyaltyProgramHelper.doIssueBonus(contactsLoyalty, fromLPVBalance, afterBalLPV, 
								matchedBonus.getEarnedLevelValue(), matchedBonus);
						if(matchedBonus.isRecurring() && multiplier > 0){
							
							bonusflag = true;
							logger.info("multiplier===="+multiplier);
						
						}else if (! matchedBonus.isRecurring() && multiplier ==0 ) {
							multiplier = 1;
							bonusflag = true;
						}
					}else if(tierUpgd && OCConstants.THRESHOLD_TYPE_TIER.equals(matchedBonus.getEarnedLevelType())) {//APP-4508
						
						if(Integer.valueOf(tier.getTierType().substring(5))==matchedBonus.getEarnedLevelValue().intValue()) {
							multiplier = 1;
							bonusflag = true;
						}
						
					}
					if(!bonusflag) continue;
						logger.info("loyalty bonus type :Points:");
						if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getExtraBonusType()) ){
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
						}else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getExtraBonusType())){
							
							String result = Utility.truncateUptoTwoDecimal(multiplier*matchedBonus.getExtraBonusValue());
							if (result != null)
								bonusAmount = Double.parseDouble(result);
							bonusRate = Constants.STRING_NILL + matchedBonus.getEarnedLevelValue() + " "
									+ matchedBonus.getEarnedLevelType() + " --> " + multiplier*matchedBonus.getExtraBonusValue() + " "
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
						}
						if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getEarnedLevelType())){
							logger.info("*****setting bonus points threshold*********");
							contactsLoyalty.setBonusPointsThreshold(matchedBonus.getEarnedLevelValue());
						}else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getEarnedLevelType())){
							logger.info("*****setting bonus currency threshold*********");
							contactsLoyalty.setBonusCurrencyThreshold(matchedBonus.getEarnedLevelValue());

						}
						LoyaltyProgramTier bonusTxTier = null;
						if (tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())) {
							bonusTxTier = tier;
						}
						//fromLtyBalance += matchedBonus.getExtraBonusValue();
						// LoyaltyTransactionChild childTxbonus =
						// createBonusTransaction(contactsLoyalty, bonusPoints,
						// OCConstants.LOYALTY_TYPE_POINTS, bonusRate);
						
						//bonusPoints =multiplier* matchedBonus.getExtraBonusValue().longValue();
						bonusRate = Constants.STRING_NILL + matchedBonus.getEarnedLevelValue() + " "
								+ matchedBonus.getEarnedLevelType() + " --> " + matchedBonus.getExtraBonusValue() + " "
								+ matchedBonus.getExtraBonusType();
						
						LoyaltyTransactionChild childTxbonus = createBonusTransaction(issuanceRequest, contactsLoyalty,
								(bonusPoints != 0 ?bonusPoints : bonusAmount ), matchedBonus.getExtraBonusType(), bonusRate);
						transaction = childTxbonus;
						createExpiryTransaction(contactsLoyalty, (long) bonusPoints, (double) bonusAmount,
								childTxbonus.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, null,matchedBonus.getThresholdBonusId());

						if (bonusTxTier != null && OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getExtraBonusType())) {
							String[] diffBonArr = applyConversionRules(contactsLoyalty, bonusTxTier);
							logger.info("balances After conversion rules updatation --  points = "
									+ contactsLoyalty.getLoyaltyBalance() + " currency = "
									+ contactsLoyalty.getGiftcardBalance());
							
							String conversionBonRate = null;
							long convertBonPoints = 0;
							double convertBonAmount = 0;
							// double convertBonAmount = 0.0;
							if (diffBonArr != null) {
								logger.info("Arr[]" + diffBonArr);
								convertBonAmount = Double.valueOf(diffBonArr[0].trim());
								// convertBonAmount = Double.valueOf(diffBonArr[0].trim()).doubleValue();
								convertBonPoints = Double.valueOf(diffBonArr[1].trim()).longValue();
								conversionBonRate = diffBonArr[2];
							//}
							pointsDifference = bonusPoints - convertBonPoints;
							// amountDifference = (long)bonusAmount + convertBonAmount;
							if (diffBonArr[0] != null)
								amountDifference = Double.valueOf(diffBonArr[0].trim()).doubleValue();
							/*if(bonusTxTier.getMultipleTierUpgrdRules()!=null && !bonusTxTier.getMultipleTierUpgrdRules().isEmpty())
								bonusTxTier = applyMultipleTierUpgradeRule(contactsLoyalty, bonusTxTier, autoComm, user);
							else
							    bonusTxTier = applyTierUpgradeRule(contactsLoyalty, program, bonusTxTier, autoComm, user,
									   issuanceRequest);*/
							logger.info("Earn Type: Point =============== ");
							logger.info(" Point diff::" + pointsDifference);
							logger.info("amount diff :" + amountDifference);
							updatePurchaseTransaction(childTxbonus, contactsLoyalty,
									Constants.STRING_NILL + pointsDifference, Constants.STRING_NILL + amountDifference,
									conversionBonRate, convertBonAmount, bonusTxTier);
							// updatePurchaseTransaction(childTxbonus, contactsLoyalty,
							// Constants.STRING_NILL+pointsDifference,
							// Constants.STRING_NILL+amountDifference, conversionBonRate,
							// Double.valueOf(diffBonArr[0].trim()).longValue(), bonusTxTier);
							}
						
						}
											

					// by pravendra
						if (transaction != null) {
							updateTransactionStatus(transaction, OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED);
							LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
							if (bonusflag &&  contactsLoyalty.getContact() != null
									&& contactsLoyalty.getContact().getContactId() != null) {
								Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
								if (contact != null && contact.getEmailId() != null) {
									autoCommGen.sendEarnBonusTemplate(autoComm != null ? autoComm.getThreshBonusEmailTmpltId() :null,
											"" + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(),
											contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
											contact.getContactId(), contactsLoyalty.getLoyaltyId(),
											transaction.getTransChildId(),matchedBonus);
								}
							}
							if (user.isEnableSMS() && bonusflag ) {
								Long contactId = null;
								if (contactsLoyalty.getContact() != null
										&& contactsLoyalty.getContact().getContactId() != null) {
									contactId = contactsLoyalty.getContact().getContactId();
								}
								autoCommGen.sendEarnBonusSMSTemplate(autoComm!=null ? autoComm.getThreshBonusSmsTmpltId() : null, user, contactId,
										contactsLoyalty.getLoyaltyId(),
										contactsLoyalty.getMobilePhone() != null ? contactsLoyalty.getMobilePhone() : null,
										transaction.getTransChildId(),matchedBonus);
							}
						}
						
				}

			}
		} catch (Exception e) {
			logger.error("Exception in update threshold bonus...", e);
			// return null;
		}
	}

	private LoyaltyProgramTier applyTierUpgradeRule(ContactsLoyalty contactsLoyalty, LoyaltyProgram program,
			LoyaltyProgramTier currTier, LoyaltyAutoComm autoComm, Users user, LoyaltyIssuanceRequest issuanceRequest) {

		try {
			boolean tierUpgd = false;
			LoyaltyProgramTier newTier = LoyaltyProgramHelper
					.applyTierUpgdRules(contactsLoyalty.getContact().getContactId(), contactsLoyalty, currTier);
			if (!newTier.getTierType().equalsIgnoreCase(currTier.getTierType())) {
				currTier = newTier;
				tierUpgd = true;
			}

			if (tierUpgd) {
				contactsLoyalty.setProgramTierId(currTier.getTierId());
				contactsLoyalty.setTierUpgradedDate(Calendar.getInstance());
				contactsLoyalty.setTierUpgradeReason(currTier.getTierUpgdConstraint());
				ContactsLoyaltyDaoForDML contactsLoyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance()
						.getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
				contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);
			}

			// Send auto communication email
			// LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(program.getProgramId());
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			if (tierUpgd && autoComm != null && autoComm.getTierUpgdEmailTmpltId() != null
					&& contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
				Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
				if (contact != null && contact.getEmailId() != null) {
					autoCommGen.sendTierUpgdTemplate(autoComm.getTierUpgdEmailTmpltId(),
							Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
							contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
							contactsLoyalty.getLoyaltyId());
				}
			}
			if (user.isEnableSMS() && tierUpgd && autoComm != null && autoComm.getTierUpgdSmsTmpltId() != null
					&& contactsLoyalty.getMobilePhone() != null) {
				Long contactId = null;
				if (contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
					contactId = contactsLoyalty.getContact().getContactId();
				}
				autoCommGen.sendTierUpgdSMSTemplate(autoComm.getTierUpgdSmsTmpltId(), user, contactId,
						contactsLoyalty.getLoyaltyId(), contactsLoyalty.getMobilePhone());
			}

			// contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);
		} catch (Exception e) {
			logger.error("Exception while upgrading tier...", e);
		}
		return currTier;
	}
	
	private LoyaltyProgramTier applyMultipleTierUpgradeRule(ContactsLoyalty contactsLoyalty,
			LoyaltyProgramTier currTier, LoyaltyAutoComm autoComm, Users user) {

		try {
			boolean tierUpgd = false;
			LoyaltyMultipleTierUpgradeRules mulUpgrdRuls = new LoyaltyMultipleTierUpgradeRules();
			LoyaltyProgramTier newTier = mulUpgrdRuls
					.applyMultipleTierUpgdRules(contactsLoyalty.getContact().getContactId(), contactsLoyalty, currTier);
			if (!newTier.getTierType().equalsIgnoreCase(currTier.getTierType())) {
				currTier = newTier;
				tierUpgd = true;
			}

			if (tierUpgd) {
				contactsLoyalty.setProgramTierId(currTier.getTierId());
				contactsLoyalty.setTierUpgradedDate(Calendar.getInstance());
				//contactsLoyalty.setTierUpgradeReason(currTier.getTierUpgdConstraint());
				ContactsLoyaltyDaoForDML contactsLoyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance()
						.getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
				contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);
			}

			// Send auto communication email
			// LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(program.getProgramId());
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			if (tierUpgd && autoComm != null && autoComm.getTierUpgdEmailTmpltId() != null
					&& contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
				Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
				if (contact != null && contact.getEmailId() != null) {
					autoCommGen.sendTierUpgdTemplate(autoComm.getTierUpgdEmailTmpltId(),
							Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
							contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
							contactsLoyalty.getLoyaltyId());
				}
			}
			if (user.isEnableSMS() && tierUpgd && autoComm != null && autoComm.getTierUpgdSmsTmpltId() != null
					&& contactsLoyalty.getMobilePhone() != null) {
				Long contactId = null;
				if (contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
					contactId = contactsLoyalty.getContact().getContactId();
				}
				autoCommGen.sendTierUpgdSMSTemplate(autoComm.getTierUpgdSmsTmpltId(), user, contactId,
						contactsLoyalty.getLoyaltyId(), contactsLoyalty.getMobilePhone());
			}

			// contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);
		} catch (Exception e) {
			logger.error("Exception while upgrading tier...", e);
		}
		return currTier;
	}

	private void updatePurchaseTransaction(LoyaltyTransactionChild transaction, ContactsLoyalty loyalty, String ptsDiff,
			String amtDiff, String conversionRate, double convertAmt, LoyaltyProgramTier tier) {

		try {
			logger.info("***************inside update trasaction ***************");
			// transaction.setAmountDifference(amtDiff);
			transaction.setAmountDifference(Utility.truncateUptoTwoDecimal(Double.valueOf(amtDiff)));
			transaction.setPointsDifference(ptsDiff);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setDescription(conversionRate);
			transaction.setConversionAmt(convertAmt);
			transaction.setTierId(tier.getTierId());

			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			// loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);

		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
	}

	private List<Balance> prepareBalancesObject(ContactsLoyalty loyalty, String pointsDiff, String amountDiff,
			String giftDiff) throws Exception {
		List<Balance> balancesList = null;
		Balance pointBalances = null;
		Balance amountBalances = null;
		Balance giftBalances = null;
		balancesList = new ArrayList<Balance>();

		pointBalances = new Balance();
		pointBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		pointBalances.setValueCode(OCConstants.LOYALTY_TYPE_POINTS);
		pointBalances.setAmount(loyalty.getLoyaltyBalance() == null ? Constants.STRING_NILL
				: Constants.STRING_NILL + loyalty.getLoyaltyBalance().intValue());
		pointBalances.setDifference(pointsDiff);

		amountBalances = new Balance();
		amountBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		amountBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		if (loyalty.getGiftcardBalance() == null) {
			amountBalances.setAmount(Constants.STRING_NILL);
		} else {
			double value = new BigDecimal(loyalty.getGiftcardBalance()).setScale(2, BigDecimal.ROUND_HALF_UP)
					.doubleValue();
			amountBalances.setAmount(Constants.STRING_NILL + value);
		}
		if (amountDiff == null || amountDiff == Constants.STRING_NILL) {
			amountBalances.setDifference(Constants.STRING_NILL);
		} else {
			double value = new BigDecimal(Double.valueOf(amountDiff)).setScale(2, BigDecimal.ROUND_HALF_UP)
					.doubleValue();
			amountBalances.setDifference(Constants.STRING_NILL + value);
		}

		giftBalances = new Balance();
		giftBalances.setType(OCConstants.LOYALTY_TYPE_GIFT);
		giftBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		if (loyalty.getGiftBalance() == null) {
			giftBalances.setAmount(Constants.STRING_NILL);
		} else {
			double value = new BigDecimal(loyalty.getGiftBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			giftBalances.setAmount(Constants.STRING_NILL + value);
		}
		if (giftDiff == null || giftDiff == Constants.STRING_NILL) {
			giftBalances.setDifference(Constants.STRING_NILL);
		} else {
			double value = new BigDecimal(Double.valueOf(giftDiff)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			giftBalances.setDifference(Constants.STRING_NILL + value);
		}

		balancesList.add(pointBalances);
		balancesList.add(amountBalances);
		balancesList.add(giftBalances);

		return balancesList;
	}

	private LoyaltyTransactionChild createBonusTransaction(LoyaltyIssuanceRequest issuanceRequest,
			ContactsLoyalty loyalty, double earnedValue, String earnType, String bonusRate) {

		LoyaltyTransactionChild transaction = null;
		try {

			transaction = new LoyaltyTransactionChild();
			transaction.setMembershipNumber(Constants.STRING_NILL + loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());

			// transaction.setCreatedDate(Calendar.getInstance());
			if (issuanceRequest.getMembership().getCreatedDate() != null
					&& !issuanceRequest.getMembership().getCreatedDate().trim().isEmpty()) {

				String requestDate = issuanceRequest.getMembership().getCreatedDate();
				DateFormat formatter;
				Date date;
				formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
				date = (Date) formatter.parse(requestDate);
				Calendar cal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
				cal.setTime(date);

				String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
				int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
				UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				Users user = usersDao.findMlUser(loyalty.getUserId());
				String timezoneDiffrenceMinutes = user.getClientTimeZone();
				logger.info(timezoneDiffrenceMinutes);
				int timezoneDiffrenceMinutesInt = 0;
				if (timezoneDiffrenceMinutes != null)
					timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
				timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
				logger.info("Client time to Server Time.." + timezoneDiffrenceMinutesInt);
				cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
				logger.info("Client time to Server Time Calendar.." + cal);
				transaction.setCreatedDate(cal);

			} else {
				transaction.setCreatedDate(Calendar.getInstance());
			}
			transaction.setEarnType(earnType);
			if (earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)) {
				transaction.setEarnedPoints((double) earnedValue);
			} else if (earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)) {
				transaction.setEarnedAmount((double) earnedValue);
			}
			transaction.setEnteredAmount((double) earnedValue);
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(MyCalendar.FORMAT_YEARTODATE);
			Date date = cal.getTime();
			String Strdate = format.format(date);
			transaction.setValueActivationDate(format.parse(Strdate));
			transaction.setEarnStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_BONUS);
			transaction.setDescription("Threshold Bonus : " + bonusRate);
			// transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_AUTO);
			transaction.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_AUTO);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			//adding Receipt number
			transaction.setReceiptNumber(issuanceRequest.getHeader().getReceiptNumber() != null
					&& !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getReceiptNumber()
							: null);
			
			//Setting discription2 with receipt number
			transaction.setDescription2( issuanceRequest.getHeader().getReceiptNumber() != null
					&& !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty()
					? issuanceRequest.getHeader().getReceiptNumber()
					: null);
			//setting docsid 
			transaction.setDocSID(issuanceRequest.getHeader().getDocSID() != null
					&& !issuanceRequest.getHeader().getDocSID().trim().isEmpty()
					? issuanceRequest.getHeader().getDocSID()
					: null);
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			// loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);

		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
		return transaction;
	}

	private void updateTransactionStatus(LoyaltyTransactionChild transaction, String status) throws Exception {
		LoyaltyTransactionChildDaoForDML LoyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator
				.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
		transaction.setEarnStatus(status);
		LoyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
	}

	private Contacts findContactById(Long cid) throws Exception {

		ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactsDao.findById(cid);
	}

	private LoyaltyTransactionChild createGiftTransaction(String transId, LoyaltyIssuanceRequest issuanceRequest,
			ContactsLoyalty loyalty, String ptsDiff, String amtDiff) {

		LoyaltyTransactionChild transaction = null;
		try {

			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(transId));
			transaction.setMembershipNumber(Constants.STRING_NILL + loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			transaction.setCreatedDate(Calendar.getInstance());
			// transaction.setEnteredAmount(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()));
			transaction.setEnteredAmount(Double.parseDouble(
					Utility.truncateUptoTwoDecimal(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()))));
			transaction.setEnteredAmountType(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT);
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);

			// transaction.setAmountDifference(amtDiff);
			transaction.setAmountDifference(Utility.truncateUptoTwoDecimal(Double.valueOf(amtDiff)));
			transaction.setEarnType(OCConstants.LOYALTY_TYPE_AMOUNT);
			transaction.setEarnedAmount(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()));
			logger.debug("storeNumber is===>" + issuanceRequest.getHeader().getStoreNumber());
			// if()
			transaction.setStoreNumber(issuanceRequest.getHeader().getStoreNumber() != null
					&& !issuanceRequest.getHeader().getStoreNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getStoreNumber()
							: null);
			transaction.setSubsidiaryNumber(issuanceRequest.getHeader().getSubsidiaryNumber() != null
					&& !issuanceRequest.getHeader().getSubsidiaryNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getSubsidiaryNumber().trim()
							: null);
			transaction.setReceiptNumber(issuanceRequest.getHeader().getReceiptNumber() != null
					&& !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty()
							? issuanceRequest.getHeader().getReceiptNumber()
							: null);

			transaction.setEmployeeId(issuanceRequest.getHeader().getEmployeeId() != null
					&& !issuanceRequest.getHeader().getEmployeeId().trim().isEmpty()
							? issuanceRequest.getHeader().getEmployeeId().trim()
							: null);
			transaction.setTerminalId(issuanceRequest.getHeader().getTerminalId() != null
					&& !issuanceRequest.getHeader().getTerminalId().trim().isEmpty()
							? issuanceRequest.getHeader().getTerminalId().trim()
							: null);
			transaction.setDocSID(issuanceRequest.getHeader().getDocSID());
			// transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE);
			transaction.setSourceType(issuanceRequest.getHeader().getSourceType());
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
			// transaction.setEventTriggStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());

			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			// loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);

			// Event Trigger sending part
			EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator
					.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator
					.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			EventTriggerDao eventTriggerDao = (EventTriggerDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
			List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(transaction.getUserId(),
					Constants.ET_TYPE_ON_GIFT_ISSUANCE);

			if (etList != null) {
				eventTriggerEventsObservable.notifyToObserver(etList, transaction.getTransChildId(),
						transaction.getTransChildId(), transaction.getUserId(), OCConstants.LOYALTY_GIFT_ISSUANCE,
						Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
			}
			List<EventTrigger> retList = eventTriggerDao.findAllETByUserAndType(transaction.getUserId(),
					Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
			logger.debug("etList ::" + retList);
			if (retList != null) {
				eventTriggerEventsObservable.notifyToObserver(retList, transaction.getTransChildId(),
						transaction.getTransChildId(), transaction.getUserId(), OCConstants.LOYALTY_ISSUANCE,
						Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
			}
		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
		return transaction;
	}
	private LoyaltyProgramTier findTier(Long contactId, Long userId, ContactsLoyalty contactsLoyalty,
			List<LoyaltyProgramTier> tiersList, Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap) throws Exception {

		/*LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

		List<LoyaltyProgramTier> tiersList = loyaltyProgramTierDao.fetchTiersByProgramId(programId);
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
		}*/

		/*//Prepare eligible tiers map
		Iterator<LoyaltyProgramTier> iterTier = tiersList.iterator();
		Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = new HashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
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
		}*/

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
			}*/
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			//totPurchaseValue = contactsLoyalty.getLifeTimePurchaseValue();//Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
			totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);
			logger.info("purchase value = "+totPurchaseValue);

			//if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){
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
			Double cumulativeAmount = 0.0;
			//Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
			ListIterator<LoyaltyProgramTier> it = new ArrayList(eligibleMap.keySet()).listIterator(eligibleMap.size());
			//LoyaltyProgramTier prevKeyTier = null;
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
				//logger.info("contactId = "+loyalty.getContact().getContactId()+" startDate = "+startDate+" endDate = "+endDate);

				/*Object[] cumulativeAmountArr = getCumulativeValue(startDate, endDate);

				cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());*/
				
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				cumulativeAmount = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(user.getUserId(),contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));


				if(cumulativeAmount == null || cumulativeAmount <= 0){
					logger.info("cumulative purchase value is empty...");
					continue;
				}
				
				if(cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
					return nextKeyTier;
				}
				
			}
			return tiersList.get(0);
			}
		
		
		else{
			return null;
		}

	}
	private List<LoyaltyProgramTier> validateTierList(Long programId, Long contactId) throws Exception {
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
	
	private LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getTierById(tierId);
		
	} 
	private Map<String, String> getThecounterSet(LoyaltyMemberItemQtyCounter loyaltyMemberItemQtyCounter) {
		
		if(loyaltyMemberItemQtyCounter == null) return new java.util.HashMap<String, String>();
		
		String itemStr = loyaltyMemberItemQtyCounter.getItemStr();
		if(itemStr == null || itemStr.isEmpty()) return new java.util.HashMap<String, String>();
		String[] itemArr = itemStr.split(Constants.ADDR_COL_DELIMETER);
		String itemCounterStrToSave = Constants.STRING_NILL;
		boolean itemStrFound = false;
		Map<String, String> retSet = new java.util.HashMap<String, String>();
		for (String itemAttr : itemArr) {
			
			String[] itemTokenArr = itemAttr.split(Constants.FORM_MAPPING_DELIMETER);
			
			String itemAttribute = itemTokenArr[0];
			String itemAttrValue = itemTokenArr[1];
			retSet.put(itemAttribute+Constants.FORM_MAPPING_DELIMETER+itemAttrValue,itemAttr);
		}
		return retSet;
	
	}
	public static void main(String[] args) {
		
		int requiredQty = 10;
		int qty = 12;
		System.out.println(10/12 +" "+12%10);
		String totItemPriceStr = Utility
				.truncateUptoTwoDecimal(110.859999999);
		if (totItemPriceStr != null)
			System.out.println( Double.parseDouble("1.000")-Double.parseDouble("1"));
	}
	
	private void createUpgrdaeTierTransaction(ContactsLoyalty cl,SpecialReward reward,LoyaltyIssuanceRequest issuanceRequest){
		try {
		LoyaltyTransactionChild loyaltyTransactionChild = new LoyaltyTransactionChild();
		loyaltyTransactionChild.setMembershipNumber(cl.getCardNumber());
		loyaltyTransactionChild.setMembershipType(cl.getMembershipType());
		loyaltyTransactionChild.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_UPGRADE_TIER);
		loyaltyTransactionChild.setEnteredAmountType(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD);
		loyaltyTransactionChild.setDescription2(reward.getItemsSatisfied() != null ? reward.getItemsSatisfied()+Constants.ADDR_COL_DELIMETER+reward.getRewardValue() : null);
		loyaltyTransactionChild.setItemInfo(reward.getItemsSatisfied() != null ? reward.getItemsSatisfied()+Constants.ADDR_COL_DELIMETER+reward.getRewardValue() : null);
		loyaltyTransactionChild.setProgramId(cl.getProgramId());
		loyaltyTransactionChild.setCardSetId(cl.getCardSetId());
		loyaltyTransactionChild.setTierId(cl.getProgramTierId());
		loyaltyTransactionChild.setUserId(cl.getUserId());
		loyaltyTransactionChild.setOrgId(cl.getOrgId());
		loyaltyTransactionChild.setAmountBalance(cl.getGiftcardBalance());
		loyaltyTransactionChild.setPointsBalance(cl.getLoyaltyBalance());
		loyaltyTransactionChild.setGiftBalance(cl.getGiftBalance());
		loyaltyTransactionChild.setHoldAmount(cl.getHoldAmountBalance());
		loyaltyTransactionChild.setHoldPoints(cl.getHoldPointsBalance());
		loyaltyTransactionChild.setCreatedDate(Calendar.getInstance());
		loyaltyTransactionChild.setSourceType(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD);
		loyaltyTransactionChild.setContactId(cl.getContact() == null ? null : cl.getContact().getContactId());
		loyaltyTransactionChild.setLoyaltyId(cl.getLoyaltyId());
		loyaltyTransactionChild.setSpecialRewardId(reward.getRewardId());
		loyaltyTransactionChild.setValueCode(reward.getRewardValueCode());
		loyaltyTransactionChild.setSubsidiaryNumber(issuanceRequest.getHeader().getSubsidiaryNumber() != null
				&& !issuanceRequest.getHeader().getSubsidiaryNumber().trim().isEmpty()
						? issuanceRequest.getHeader().getSubsidiaryNumber().trim()
						: null);
		logger.debug("storeNumber is===>" + issuanceRequest.getHeader().getStoreNumber());
		loyaltyTransactionChild.setStoreNumber(issuanceRequest.getHeader().getStoreNumber() != null
				&& !issuanceRequest.getHeader().getStoreNumber().trim().isEmpty()
						? issuanceRequest.getHeader().getStoreNumber()
						: null);
		loyaltyTransactionChild.setReceiptNumber(issuanceRequest.getHeader().getReceiptNumber() != null
				&& !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty()
						? issuanceRequest.getHeader().getReceiptNumber()
						: null);
		loyaltyTransactionChild.setDocSID(issuanceRequest.getHeader().getDocSID());
		
		LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML;
		
			loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(loyaltyTransactionChild);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	


}
