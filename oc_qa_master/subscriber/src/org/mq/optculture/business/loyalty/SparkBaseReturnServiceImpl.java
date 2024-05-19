package org.mq.optculture.business.loyalty;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.sparkbase.SparkBaseServiceAsync;
import org.mq.marketer.sparkbase.transactionWsdl.AdjustmentResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ErrorMessageComponent;
import org.mq.marketer.sparkbase.transactionWsdl.GiftIssuanceResponse;
import org.mq.marketer.sparkbase.transactionWsdl.GiftRedemptionResponse;
import org.mq.marketer.sparkbase.transactionWsdl.LoyaltyIssuanceResponse;
import org.mq.marketer.sparkbase.transactionWsdl.LoyaltyRedemptionResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ResponseStandardHeaderComponent;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramExclusionDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.StatusInfo;
import org.mq.optculture.model.ocloyalty.Amount;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.BalancesAdditionalInfo;
import org.mq.optculture.model.ocloyalty.Credit;
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.Debit;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyOfflineReturnTransactionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyReturnTransactionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyReturnTransactionResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.OriginalReceipt;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.SkuDetails;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class SparkBaseReturnServiceImpl implements SparkBaseReturnService{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	
	/**
	 * BaseService Request called by rest service controller.
	 * @return BaseResponseObject
	 */
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {

		logger.info(" Return transaction base oc service called ...");
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		Gson gson = new Gson();
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		LoyaltyReturnTransactionRequest returnTransactionRequest = null;
		BaseResponseObject responseObject = null;
		String responseJson = null;

		if(serviceRequest == null || !OCConstants.LOYALTY_SERVICE_ACTION_RETURN.equals(serviceRequest)){

			Status status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);

			returnTransactionResponse = new LoyaltyReturnTransactionResponse();
			returnTransactionResponse.setStatus(status);

			responseJson = gson.toJson(returnTransactionResponse);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}

		try{
			returnTransactionRequest = gson.fromJson(requestJson, LoyaltyReturnTransactionRequest.class);
		}catch(Exception e){
			Status status = new Status("101001", ""+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);

			returnTransactionResponse = new LoyaltyReturnTransactionResponse();
			returnTransactionResponse.setStatus(status);
			responseJson = gson.toJson(returnTransactionResponse);

			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid JSON ");
			return responseObject;

		}

		try{
			LoyaltyReturnTransactionOCService loyaltyReturnTransactionOCService = (LoyaltyReturnTransactionOCService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_RETURN_TRANSACTION_OC_BUSINESS_SERVICE);
			returnTransactionResponse = loyaltyReturnTransactionOCService.processReturnTransactionRequest(returnTransactionRequest, baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate(),requestJson, OCConstants.LOYALTY_ONLINE_MODE,null);
			responseJson = gson.toJson(returnTransactionResponse);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
		}catch(Exception e){
			logger.error("Exception in loyalty return transaction base service.",e);
			throw new BaseServiceException("Server Error.");
		}
		return responseObject;
	}

	
	@Override
	public LoyaltyReturnTransactionResponse processTempReturnTransactionRequest(LoyaltyReturnTransactionRequest returnTransactionRequest,
			String transactionId, String transactionDate, String requestJson, String mode) throws BaseServiceException {
		
		return null;
		
	}

	/**
	 * Finds sparkbase location settings object in OptCulture
	 * 
	 * @param userOrgId
	 * @return SparkBaseLocation
	 * @throws Exception
	 */
	private SparkBaseLocationDetails getSparkbaseLocation(Long userOrgId) throws Exception{
		SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao = (SparkBaseLocationDetailsDao)
				ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_LOCATIONDETAILS_DAO);
		List<SparkBaseLocationDetails> sbDetailsList = sparkBaseLocationDetailsDao.findActiveSBLocByOrgId(userOrgId);
		SparkBaseLocationDetails sparkbaseLocation = (sbDetailsList == null) ? null : sbDetailsList.get(0);
		return sparkbaseLocation;

	}
	
	/**
	 * Handles the complete process of Loyalty return transaction.
	 * 
	 * @param returnTransactionRequest
	 * @return returnTransactionResponse
	 * @throws BaseServiceException
	 */
	@Override
	public LoyaltyReturnTransactionResponse processReturnTransactionRequest(LoyaltyReturnTransactionRequest returnTransactionRequest,
			String transactionId, String transactionDate, String requestJson, String mode) throws BaseServiceException {

		logger.info("processReturnTransactionRequest method called...");

		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;
		Users user = null;

		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(returnTransactionRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(returnTransactionRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		responseHeader.setSourceType(returnTransactionRequest.getHeader().getSourceType() != null && 
				!returnTransactionRequest.getHeader().getSourceType().trim().isEmpty() ? returnTransactionRequest.getHeader().getSourceType().trim() : "");
		/*responseHeader.setSubsidiaryNumber(returnTransactionRequest.getHeader().getSubsidiaryNumber() != null && !returnTransactionRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? returnTransactionRequest.getHeader().getSubsidiaryNumber().trim() : Constants.STRING_NILL);
		responseHeader.setReceiptNumber(returnTransactionRequest.getHeader().getReceiptNumber() != null && !returnTransactionRequest.getHeader().getReceiptNumber().trim().isEmpty() ? returnTransactionRequest.getHeader().getReceiptNumber().trim() : Constants.STRING_NILL);
		responseHeader.setReceiptAmount(Constants.STRING_NILL);*/
		
		
		try{

//basic validations are already passed.
			user = getUser(returnTransactionRequest.getUser().getUserName(), returnTransactionRequest.getUser().getOrganizationId(),
					returnTransactionRequest.getUser().getToken());
			
			SparkBaseLocationDetails sparkbaseLocation = getSparkbaseLocation(user.getUserOrganization().getUserOrgId());
			
			Amount amount = returnTransactionRequest.getAmount();
			RequestHeader header = returnTransactionRequest.getHeader();
			OriginalReceipt originalRecpt = returnTransactionRequest.getOriginalReceipt();
			String creditRedeemedAmount = returnTransactionRequest.getCreditRedeemedAmount();
			List<SkuDetails> items = returnTransactionRequest.getItems();
			
			
			if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)) {
				//logger.debug("mode ==="+mode+" && returnTransactionRequest.getReturnItems()==="+returnTransactionRequest.getReturnItems());
				status = checkItemsEmpty(items );
				if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
				
				//original receipt structure should not check 
				if(originalRecpt == null || 
						originalRecpt.getDocSID() == null || 
								originalRecpt.getDocSID().trim().isEmpty()) {
					
					if((returnTransactionRequest.getMembership() == null || 
							returnTransactionRequest.getMembership().getCardNumber() == null 
							|| returnTransactionRequest.getMembership().getCardNumber().trim().isEmpty()) && 
							(returnTransactionRequest.getCustomer() == null || returnTransactionRequest.getCustomer().getPhone() == null ||
							returnTransactionRequest.getCustomer().getPhone().isEmpty()) ){
						
						//when original receipt & membership details not found need a error code
						status = new Status("111564", PropertyUtil.getErrorMessage(111564, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}
					
				}
				
				if(originalRecpt != null && 
						originalRecpt.getDocSID() != null && 
						!originalRecpt.getDocSID().trim().isEmpty()) {
					
					LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
					List<LoyaltyTransactionChild> issTransList = loyaltyTransactionChildDao.findByDocSID(originalRecpt.getDocSID(), 
																								      user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,null,null,null);
					if(issTransList != null) {
						//check for the sufficient balance
						String docSID = "OR-"+originalRecpt.getDocSID();
						List<LoyaltyTransactionChild> returnList = loyaltyTransactionChildDao.getTotReversalAmt(user.getUserId(), 
								docSID, OCConstants.LOYALTY_TRANS_TYPE_RETURN,null);
					    double totReturnAmt = 0.0; 
						if(returnList != null && returnList.size() >0) {
							for (LoyaltyTransactionChild returnObj : returnList) {
								totReturnAmt += returnObj.getDescription().contains(";=;") ? Double.parseDouble(returnObj.getDescription().split(";=;")[0]): Double.parseDouble(returnObj.getDescription());
							}
						}
						logger.info("retuenedAmt ::"+totReturnAmt);
						double totIssuedAmt = issTransList.get(0).getEnteredAmount()==null?0:issTransList.get(0).getEnteredAmount();
						logger.info("Issued Amount::"+totIssuedAmt);
						double diffAmt = totIssuedAmt - totReturnAmt;
						logger.info("diffAmt ::"+diffAmt);
						
						if(diffAmt == 0 || 0 > diffAmt) {
							status = new Status("111567", PropertyUtil.getErrorMessage(111567, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
							return returnTransactionResponse;
						}
						
						LoyaltyTransactionChild loyaltyTransactionChild = issTransList.get(0);
						returnTransactionResponse = performIssuanceBasedReversal(sparkbaseLocation, loyaltyTransactionChild, header,
								amount, originalRecpt, creditRedeemedAmount, items, responseHeader, requestJson, user,diffAmt, mode);
						return returnTransactionResponse;
					}
					List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(originalRecpt.getDocSID(), 
							user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,null,null,null);
					if(redempTransList != null) {
					
					returnTransactionResponse = performRedemptnBasedReversal(sparkbaseLocation, redempTransList, header, amount, originalRecpt, creditRedeemedAmount, responseHeader, requestJson, 
								 user, null, null, null, null, null, true,
								 PropertyUtil.getErrorMessage(111569, OCConstants.ERROR_LOYALTY_FLAG), 111569 );
					return returnTransactionResponse;
					}
					else {
					status = new Status("111566", PropertyUtil.getErrorMessage(111566, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					}
					
				}else {
					
					ContactsLoyalty contactsLoyalty = null;
					
					if( (returnTransactionRequest.getMembership().getCardNumber() != null 
								&& returnTransactionRequest.getMembership().getCardNumber().trim().length() > 0) ) {
						String cardNumber = "";
						String cardLong = null;
	
						cardLong = OptCultureUtils.validateOCLtyCardNumber(returnTransactionRequest.getMembership().getCardNumber().trim());
						if(cardLong == null){
							String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getMembership().getCardNumber().trim()+".";
							status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
							return returnTransactionResponse;
						}
						cardNumber = ""+cardLong;
						returnTransactionRequest.getMembership().setCardNumber(cardNumber);
						

						contactsLoyalty = findLoyaltyCardInOC(cardNumber, user.getUserId());
						
						if(contactsLoyalty == null){
							status = new Status("1000", PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
							return returnTransactionResponse;
						}
					
					}//if card
					
					if(contactsLoyalty != null ){
						
						if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
							
							
							String cardNumber = contactsLoyalty.getCardNumber()+Constants.STRING_NILL;
							
							if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equals(contactsLoyalty.getRewardFlag()) ){
								
								status = new Status("111581", PropertyUtil.getErrorMessage(111581, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
								return returnTransactionResponse;
								
							}
							
						}//if card based loyalty
						

                        Double returnedAmountdbl = calculateReturnAmount(items );
		                logger.info("Return value "+returnedAmountdbl);
						
						
						Double netReturnedAmountdbl = returnedAmountdbl;// - itemExcludedAmount;
						
						double netReturnedAmount = Math.round(netReturnedAmountdbl);
						//calculate the rollback based on current earn rules 
						
						
						//long earnedValue = 0;
						double earnedValue = 0;
						String earntype = sparkbaseLocation.getEarnType();
						String pointsDifference = "";
						String amountDifference = "";
						
						if(sparkbaseLocation.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)){
							
							Double multipleFactordbl = netReturnedAmount/sparkbaseLocation.getEarnOnSpentAmount();
							//double multipleFactor = multipleFactordbl.intValue();
							//earnedValue = (long)Math.floor(tier.getEarnValue() * multipleFactor);
							earnedValue = sparkbaseLocation.getEarnValue() * multipleFactordbl;
						}
						else if(sparkbaseLocation.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)){
							
							//earnedValue = (long)Math.floor((tier.getEarnValue() * netReturnedAmount)/100);
							earnedValue = (sparkbaseLocation.getEarnValue() * netReturnedAmount)/100;
						}
						
						if(OCConstants.LOYALTY_TYPE_POINTS.equals(earntype)){
							earnedValue = (long)Math.floor(earnedValue);
							pointsDifference = "-"+earnedValue;
						}
						else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(earntype)){
							String res = Utility.truncateUptoTwoDecimal(earnedValue);
							if(res != null)
								earnedValue = Double.parseDouble(res);
							amountDifference = "-"+earnedValue;
						}
						
						Map<String, Object> balMap = new HashMap<String, Object>();
						balMap = updateContactLoyaltyBalances(sparkbaseLocation, earnedValue, earntype, contactsLoyalty,netReturnedAmount);
						status = (Status) balMap.get("status");
						MembershipResponse response = prepareMembershipResponse(contactsLoyalty);
						List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
						contactLoyaltyList.add(contactsLoyalty);
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
						if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
							
							List<Balance> balances = prepareBalancesObject(contactsLoyalty, "", "", "");
							String expiryPeriod = "";
							
							HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
							BalancesAdditionalInfo additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
							
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances,
									holdBalance, additionalInfo, null, status);
							return returnTransactionResponse;
						}
						String description = returnedAmountdbl+"";
						/*createReturnTransaction(header, amount, 
								originalRecpt, contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
								pointsDifference, amountDifference, null , null, null, 
								responseHeader.getTransactionId(), Double.parseDouble(amount.getEnteredValue()),OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL, 
								amount.getEnteredValue(), null);*/
						
						if(balMap.get("debitedRewardPoints") != null && !balMap.get("debitedRewardPoints").toString().isEmpty()) {
							pointsDifference = "-" + balMap.get("debitedRewardPoints") + Constants.STRING_NILL;
						}else{
							pointsDifference = Constants.STRING_NILL;
						}
						if(balMap.get("debitedRewardCurrency") != null && !balMap.get("debitedRewardCurrency").toString().isEmpty()){
							amountDifference = "-" + balMap.get("debitedRewardCurrency") + Constants.STRING_NILL;
						}else{
							amountDifference = Constants.STRING_NILL;
						}
						createReturnTransaction(header, amount, 
								originalRecpt, contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
								pointsDifference, amountDifference, null , null, null, 
								responseHeader.getTransactionId(), Double.parseDouble(amount.getEnteredValue()),OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL, 
								description, null);
						List<Balance> balances = prepareBalancesObject(contactsLoyalty, ""+pointsDifference, ""+amountDifference, "");
						String expiryPeriod = "";
						
						
						HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
						BalancesAdditionalInfo additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
						String appendRemainderRes = Constants.STRING_NILL;
						String remainderDebit = (String)balMap.get("remainderDebit");
						if(remainderDebit != null && !remainderDebit.isEmpty() && !remainderDebit.equals("0.0")){
							appendRemainderRes = " Remainder Debit:$"+remainderDebit+".";
						}
						status = new Status("0", "Return was successful."+appendRemainderRes, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances,
								holdBalance, additionalInfo, matchedCustomers, status);
						return returnTransactionResponse;
										
					}
					
					
					
				}
				
			}
			else {
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null,status);
				return returnTransactionResponse;
			}
			
		}catch(Exception e){
			logger.error("Exception in loyalty return transaction service", e);
			throw new BaseServiceException("Loyalty return transaction Request Failed");
		}
		return returnTransactionResponse;
	}//processReturnTransactionRequest
	
	
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
	
	/*private LoyaltyProgramTier findTier(Long contactId, Long userId, ContactsLoyalty contactsLoyalty,
			List<LoyaltyProgramTier> tiersList, Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap) throws Exception {

		LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

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
		}

		//Prepare eligible tiers map
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
		else if(contactId == null){
			logger.info("contactId is null and selected tier..."+tiersList.get(0).getTierType());
			return tiersList.get(0);
		}
		else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);

			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);				

			//List<Map<String, Object>> contactPurcahseList = contactsDao.findContactPurchaseDetails(userId, contactId);
			Double totPurchaseValue = null;
			if(contactPurcahseList != null && contactPurcahseList.size() == 1) {
				for (Map<String, Object> eachMap : contactPurcahseList) {
					if(eachMap.containsKey("tot_purchase_amt")){
						totPurchaseValue = Double.valueOf(eachMap.get("tot_purchase_amt") != null ? eachMap.get("tot_purchase_amt").toString() : "0.00");
						logger.info("purchase value = "+totPurchaseValue);
					}
				}
			}

			if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			totPurchaseValue = contactsLoyalty.getLifeTimePurchaseValue(); //Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
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
					logger.info("contactId = "+contactId+" startDate = "+startDate+" endDate = "+endDate);

					RetailProSalesDao salesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
					Object[] cumulativeAmountArr = salesDao.getCumulativePurchase(userId, contactId, startDate, endDate);

					cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());
					
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
				while(it.hasNext()){
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
				}
				return tiersList.get(0);
			}catch(Exception e){
				logger.error("Excepion in cpv thread ", e);
				return tiersList.get(0);
			}
		}
		else{
			return null;
		}

	}*/
	
	private Status checkItemsEmpty(List<SkuDetails> itemsList) {
		
		Status status = null;
		
		if(itemsList == null){
			status = new Status("111528", PropertyUtil.getErrorMessage(111528, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
				
		Iterator<SkuDetails> iterItems = itemsList.iterator();
		SkuDetails item = null;
		while(iterItems.hasNext()){
			item = iterItems.next();
			if(item != null && (item.getItemCategory() == null || item.getItemCategory().trim().isEmpty()) && 
					(item.getDepartmentCode() == null || item.getDepartmentCode().trim().isEmpty()) &&
					(item.getItemClass() == null || item.getItemClass().trim().isEmpty()) &&
					(item.getItemSubClass() == null || item.getItemSubClass().trim().isEmpty()) &&
					(item.getDCS() == null || item.getDCS().trim().isEmpty()) &&
					(item.getVendorCode() == null || item.getVendorCode().trim().isEmpty()) &&
					(item.getSkuNumber() == null || item.getSkuNumber().trim().isEmpty()) &&
					(item.getBilledUnitPrice() == null || item.getBilledUnitPrice().trim().isEmpty()) && 
					(item.getQuantity() == null || item.getQuantity().trim().isEmpty())){
				status = new Status("111528", PropertyUtil.getErrorMessage(111528, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
		}
		
		return status;
	}
	
private Double calculateReturnAmount(List<SkuDetails> itemList) throws Exception {
		
		Double returnAmount = 0.0;
		
		for(SkuDetails skuDetails : itemList){
			
			if(skuDetails.getBilledUnitPrice() != null && !skuDetails.getBilledUnitPrice().trim().isEmpty() &&
					skuDetails.getQuantity() != null && !skuDetails.getQuantity().trim().isEmpty()){
				returnAmount = returnAmount + (Double.valueOf(skuDetails.getQuantity()) * Double.valueOf(skuDetails.getBilledUnitPrice()));
					
			  }
			}
		
		return returnAmount;
	}
	
private Double calculateItemDiscount(List<SkuDetails> itemList, LoyaltyProgramExclusion loyaltyExclusion) throws Exception {
		
		Double excludedAmount = 0.0;
		List<String> itemClassList = null;
		List<String> itemDcsList = null;
		List<String> itemdeptCodeList = null;
		List<String> itemCatList = null;
		List<String> skuNumberList = null;
		List<String> itemSubClassList = null;
		List<String> itemVendorCodeList = null;
		
		if(loyaltyExclusion.getClassStr() != null && !loyaltyExclusion.getClassStr().trim().isEmpty()){
			itemClassList = Arrays.asList(loyaltyExclusion.getClassStr().split(";=;"));
		}
		if(loyaltyExclusion.getDcsStr() != null && !loyaltyExclusion.getDcsStr().trim().isEmpty()){
			itemDcsList = Arrays.asList(loyaltyExclusion.getDcsStr().split(";=;"));
		}
		if(loyaltyExclusion.getDeptCodeStr() != null && !loyaltyExclusion.getDeptCodeStr().trim().isEmpty()){
			itemdeptCodeList = Arrays.asList(loyaltyExclusion.getDeptCodeStr().split(";=;"));
		}
		if(loyaltyExclusion.getItemCatStr() != null && !loyaltyExclusion.getItemCatStr().trim().isEmpty()){
			itemCatList = Arrays.asList(loyaltyExclusion.getItemCatStr().split(";=;"));
		}
		if(loyaltyExclusion.getSkuNumStr() != null && !loyaltyExclusion.getSkuNumStr().trim().isEmpty()){
			skuNumberList = Arrays.asList(loyaltyExclusion.getSkuNumStr().split(";=;"));
		}
		if(loyaltyExclusion.getSubClassStr() != null && !loyaltyExclusion.getSubClassStr().trim().isEmpty()){
			itemSubClassList = Arrays.asList(loyaltyExclusion.getSubClassStr().split(";=;"));
		}
		if(loyaltyExclusion.getVendorStr() != null && !loyaltyExclusion.getVendorStr().trim().isEmpty()){
			itemVendorCodeList = Arrays.asList(loyaltyExclusion.getVendorStr().split(";=;"));
		}
		
		for(SkuDetails skuDetails : itemList){
			
			if(skuDetails.getBilledUnitPrice() != null && !skuDetails.getBilledUnitPrice().trim().isEmpty() &&
					skuDetails.getQuantity() != null && !skuDetails.getQuantity().trim().isEmpty()){
				if(itemCatList != null && itemCatList.contains(skuDetails.getItemCategory())){
					excludedAmount = excludedAmount + (Double.valueOf(skuDetails.getQuantity()) * Double.valueOf(skuDetails.getBilledUnitPrice()));
					continue;
				}
				if(itemdeptCodeList != null && itemdeptCodeList.contains(skuDetails.getDepartmentCode())){
					excludedAmount = excludedAmount + (Double.valueOf(skuDetails.getQuantity()) * Double.valueOf(skuDetails.getBilledUnitPrice()));
					continue;
				}
				if(itemClassList != null && itemClassList.contains(skuDetails.getItemClass())){
					excludedAmount = excludedAmount + (Double.valueOf(skuDetails.getQuantity()) * Double.valueOf(skuDetails.getBilledUnitPrice()));
					continue;
				}
				if(itemDcsList != null && itemDcsList.contains(skuDetails.getDCS())){
					excludedAmount = excludedAmount + (Double.valueOf(skuDetails.getQuantity()) * Double.valueOf(skuDetails.getBilledUnitPrice()));
					continue;
				}
				if(itemVendorCodeList != null && itemVendorCodeList.contains(skuDetails.getVendorCode())){
					excludedAmount = excludedAmount + (Double.valueOf(skuDetails.getQuantity()) * Double.valueOf(skuDetails.getBilledUnitPrice()));
					continue;
				}
				if(skuNumberList != null && skuNumberList.contains(skuDetails.getSkuNumber())){
					excludedAmount = excludedAmount + (Double.valueOf(skuDetails.getQuantity()) * Double.valueOf(skuDetails.getBilledUnitPrice()));
					continue;
				}
				if(itemSubClassList != null && itemSubClassList.contains(skuDetails.getItemSubClass())){
					excludedAmount = excludedAmount + (Double.valueOf(skuDetails.getQuantity()) * Double.valueOf(skuDetails.getBilledUnitPrice()));
					continue;
				}
			}
		}
		
		return excludedAmount;
	}
	private LoyaltyReturnTransactionResponse performRedemptnBasedReversal(SparkBaseLocationDetails sbDetails, List<LoyaltyTransactionChild> redempTransList, RequestHeader header,
															Amount amount, OriginalReceipt originalRecpt, String creditRedeemedAmount, 
															ResponseHeader responseHeader, String requestJson, Users user, 
															MembershipResponse response, List<Balance> balances, HoldBalance holdBalance,
															BalancesAdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers,
															boolean isIssuanceFailed, String msg, int errorCode) throws Exception{

		logger.info("performRedemptnBasedReversal method called...");
		LoyaltyReturnTransactionResponse returnTransactionResponse =  null;
		Status status = null;
		//double balToCredit = Double.parseDouble(amount.getEnteredValue());
		double balToCredit = 0.0;
		String res = Utility.truncateUptoTwoDecimal(Double.parseDouble(amount.getEnteredValue()));
		if(res != null)
			balToCredit = Double.parseDouble(res);
		if(!creditRedeemedAmount.equalsIgnoreCase("N")) {
			
			List<Credit> credList = new ArrayList<Credit>();
			String redeemedOnIdStr = "";
			
			if(redempTransList != null) {
				logger.info("---No. of redemptions found for the DocSID in original receipt are ::"+redempTransList.size());
				for(LoyaltyTransactionChild redemptionTrans : redempTransList) {
					Map<String, Object> contactLtyMap = new HashMap<String, Object>();
					contactLtyMap = getContactLtyObj(redemptionTrans, user);
					if(contactLtyMap.get("flag") != null && contactLtyMap.get("flag").toString().equalsIgnoreCase("true")) continue;
					if(redeemedOnIdStr != null && !redeemedOnIdStr.isEmpty())redeemedOnIdStr += ","+redemptionTrans.getTransChildId();
					else redeemedOnIdStr = redemptionTrans.getTransChildId()+"";
				}
			}
			
			logger.info("redeemedOnIdStr"+redeemedOnIdStr);
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			List<LoyaltyTransactionChild> returnList = redeemedOnIdStr.isEmpty() ? null : loyaltyTransactionChildDao.getReturnList(user.getUserId(), redeemedOnIdStr);
			boolean isRedemptionExists = false;
			if(redempTransList != null) {
				isRedemptionExists = true;
				for (LoyaltyTransactionChild redemptionTrans : redempTransList) {

					Map<String, Object> contactLtyMap = new HashMap<String, Object>();
					Map<String, Object> responseMap = new HashMap<String, Object>();
					contactLtyMap = getContactLtyObj(redemptionTrans, user);
					if(contactLtyMap.get("flag") != null && contactLtyMap.get("flag").toString().equalsIgnoreCase("true")) continue;
					ContactsLoyalty contactsLoyalty = (ContactsLoyalty) contactLtyMap.get("loyaltyObj");

					double pointsDiff = (redemptionTrans.getPointsDifference() == null || redemptionTrans.getPointsDifference().isEmpty()) ? 0 :
						Double.parseDouble(redemptionTrans.getPointsDifference().replace("-", ""));
					double amountDiff = (redemptionTrans.getAmountDifference() == null || redemptionTrans.getAmountDifference().isEmpty()) ? 0 : 
						Double.parseDouble(redemptionTrans.getAmountDifference().replace("-", ""));
					double giftDiff = (redemptionTrans.getGiftDifference() == null || redemptionTrans.getGiftDifference().isEmpty()) ? 0 : 
						Double.parseDouble(redemptionTrans.getGiftDifference().replace("-", ""));

					if(returnList != null && returnList.size() > 0) {
						double returnedAmt = 0.0;
						double ltyPts = 0.0;
						double ltyAmt = 0.0;
						double ltyGft = 0.0;

						//LoyaltyTransactionChild matchedReturnsTrans = null; 
						for (LoyaltyTransactionChild returnTrans : returnList) {
							if(returnTrans.getRedeemedOn().longValue() == redemptionTrans.getTransChildId().longValue()) {
								//matchedReturnsTrans =  returnTrans;
								returnedAmt += Double.parseDouble(returnTrans.getDescription().split(";=;")[0]);
								ltyAmt  += Double.parseDouble(returnTrans.getDescription().split(";=;")[1]);
								ltyPts  += Double.parseDouble(returnTrans.getDescription().split(";=;")[2]);
								ltyGft  += Double.parseDouble(returnTrans.getDescription().split(";=;")[3]);
							}
						}
						logger.info("returnedAmt ::"+returnedAmt);
						if(redemptionTrans.getEnteredAmount().doubleValue() == returnedAmt) {
							continue;
						}
						else if(returnedAmt == 0) {
							responseMap = processRedemptionCredits(sbDetails, redemptionTrans, header, amount, originalRecpt, responseHeader, requestJson,  user, 
									response, balances, holdBalance, additionalInfo, matchedCustomers,  isIssuanceFailed,  
									msg, contactsLoyalty, balToCredit, pointsDiff, amountDiff, giftDiff, credList);
							if(responseMap.get("returnResponse") != null) return (LoyaltyReturnTransactionResponse) responseMap.get("returnResponse");
							if(responseMap.get("creditList") != null) credList = (List<Credit>) responseMap.get("creditList");
							if(responseMap.get("balToCredit") != null) balToCredit = (Double) responseMap.get("balToCredit");
							//balToCredit = (Double) responseMap.get("balToCredit");
						}
						else {
							ltyAmt  = amountDiff - ltyAmt;
							ltyPts  = pointsDiff - ltyPts;
							ltyGft  = giftDiff - ltyGft;
							responseMap = processRedemptionCredits(sbDetails, redemptionTrans, header, amount, originalRecpt,	responseHeader, requestJson, user, 
									response, balances, holdBalance, additionalInfo, matchedCustomers,  isIssuanceFailed,  
									msg, contactsLoyalty,  balToCredit, ltyPts, ltyAmt, ltyGft, credList);
							if(responseMap.get("returnResponse") != null) return (LoyaltyReturnTransactionResponse) responseMap.get("returnResponse");
							if(responseMap.get("creditList") != null) credList = (List<Credit>) responseMap.get("creditList");
							if(responseMap.get("balToCredit") != null) balToCredit = (Double) responseMap.get("balToCredit");
							//balToCredit = (Double) responseMap.get("balToCredit");
						}
					}
					else {
						responseMap = processRedemptionCredits(sbDetails, redemptionTrans, header, amount, originalRecpt,	responseHeader, requestJson, user, 
								response, balances, holdBalance, additionalInfo, matchedCustomers,  isIssuanceFailed,  
								msg, contactsLoyalty,  balToCredit, pointsDiff, amountDiff, giftDiff, credList);
						if(responseMap.get("returnResponse") != null) return (LoyaltyReturnTransactionResponse) responseMap.get("returnResponse");
						 if(responseMap.get("creditList") != null) credList = (List<Credit>) responseMap.get("creditList");
						 if(responseMap.get("balToCredit") != null) balToCredit = (Double) responseMap.get("balToCredit");
						//balToCredit = (Double) responseMap.get("balToCredit");
					}
					if(balToCredit == 0) break;
				}//for	
			}// if
			logger.info("Error Code"+errorCode);
			logger.info("Balance"+balToCredit);
			if(balToCredit ==  Double.parseDouble(amount.getEnteredValue()) && isIssuanceFailed) {
				if(isRedemptionExists) {
					status = new Status("111570", PropertyUtil.getErrorMessage(111570, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				else {
					status = new Status(errorCode+"", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
				return returnTransactionResponse;
			}
			else if(balToCredit ==  Double.parseDouble(amount.getEnteredValue()) && !isIssuanceFailed) {
				status = new Status("0", msg , OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
				return returnTransactionResponse;
			}
			else if(balToCredit !=  Double.parseDouble(amount.getEnteredValue()) && isIssuanceFailed) {
				msg += " Return was successful on redemption.";
				status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				additionalInfo = new BalancesAdditionalInfo();
				additionalInfo.setDebit(new Debit());
				additionalInfo.setCredit(credList);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
				return returnTransactionResponse;
			}
			else if(balToCredit !=  Double.parseDouble(amount.getEnteredValue()) && !isIssuanceFailed){
				status = new Status("0", "Return was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				additionalInfo.setCredit(credList);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
				return returnTransactionResponse;
			}
		}
		else {
			logger.info("---No redemptions found for the DocSID in original receipt.");
			if(isIssuanceFailed) {
				status = new Status(errorCode+"", msg , OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
			}
			else {
				status = new Status("0", msg , OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
			}
		}
		logger.info("performRedemptnBasedReversal method called...");
		return returnTransactionResponse;
	}//performRedemptnBasedReversal
	private StatusInfo callToSparkbaseApiForIssuance(String valueCode, String enteredAmount, 
			SparkBaseLocationDetails sparkbaseLocation, ContactsLoyalty contactLoyalty) throws Exception {

		logger.info(">>>Started callToSparkbaseApi method>>>");
		
		Object sbIssuanceResponse = null;
		String[] amount = new String[2];
		amount[0] = valueCode;
		amount[1] = enteredAmount;

		if(valueCode.equals(OCConstants.LOYALTY_POINTS)){
			sbIssuanceResponse = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.ADJUSTMENT, sparkbaseLocation, contactLoyalty, null, amount, true);
		}
		else if(valueCode.equals(OCConstants.LOYALTY_USD)){
			sbIssuanceResponse = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.ADJUSTMENT, sparkbaseLocation, contactLoyalty, null, amount, true);
		}
		org.mq.marketer.sparkbase.transactionWsdl.LoyaltyIssuanceResponse loyaltyIssuanceResponse = null;
		GiftIssuanceResponse giftIssuanceResponse = null;
		ErrorMessageComponent errorMsg = null;
		StatusInfo statusInfo = null;

		if(sbIssuanceResponse instanceof ErrorMessageComponent) {
			errorMsg = (ErrorMessageComponent)sbIssuanceResponse;
		} else if (sbIssuanceResponse instanceof LoyaltyIssuanceResponse){
			loyaltyIssuanceResponse = (org.mq.marketer.sparkbase.transactionWsdl.LoyaltyIssuanceResponse)sbIssuanceResponse;
			ResponseStandardHeaderComponent standardHeader = loyaltyIssuanceResponse.getStandardHeader();
			if (standardHeader.getStatus().equals("E")) {
				logger.info("Printing Error...");
				errorMsg = loyaltyIssuanceResponse.getErrorMessage();
			}
		}
		else if (sbIssuanceResponse instanceof GiftIssuanceResponse){
			giftIssuanceResponse = (GiftIssuanceResponse)sbIssuanceResponse;
			ResponseStandardHeaderComponent standardHeader = giftIssuanceResponse.getStandardHeader();
			if (standardHeader.getStatus().equals("E")) {
				logger.info("Printing Error...");
				errorMsg = giftIssuanceResponse.getErrorMessage();
			}
		}

		if(errorMsg != null){
			statusInfo = new StatusInfo("200012", PropertyUtil.getErrorMessage(200012, OCConstants.ERROR_LOYALTY_FLAG)+", "+errorMsg.getBriefMessage(),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		else{
			
			return statusInfo;
		}
	}
	private Map<String, Object> processRedemptionCredits(SparkBaseLocationDetails sbDetails, LoyaltyTransactionChild redemptionTrans, 
			RequestHeader header, Amount amount, OriginalReceipt originalRecpt, ResponseHeader responseHeader, 
			String requestJson, Users user, MembershipResponse response, List<Balance> balances,
			HoldBalance holdBalance, BalancesAdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers, 
			boolean isIssuanceFailed, String msg, ContactsLoyalty contactsLoyalty, 
			double balToCredit, double pointsDiff, double amountDiff, double giftDiff, List<Credit> credList) throws Exception {

		LoyaltyReturnTransactionResponse returnTransactionResponse =  null;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("contactsLoyaltyDaoForDML");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		
		double earnedPts = 0;
		String creditedPoints = "";
		String creditedReward = "";
		String creditedGift = "";
		
		String description = "";

		Double pointsBal = contactsLoyalty.getLoyaltyBalance() == null ? 0 : contactsLoyalty.getLoyaltyBalance();
		Double ltyCurrBal = contactsLoyalty.getGiftcardBalance() == null ? 0 : contactsLoyalty.getGiftcardBalance();
		Double giftBal = contactsLoyalty.getGiftBalance() ==  null ? 0 : contactsLoyalty.getGiftBalance();

		if(redemptionTrans.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM)) {

			if(contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L)) {
				logger.info("--- In loyaty amount redemption ---");
				if(amountDiff != 0 && amountDiff >= balToCredit){
					StatusInfo statusInfo = callToSparkbaseApiForIssuance(OCConstants.LOYALTY_USD, balToCredit+Constants.STRING_NILL, sbDetails, contactsLoyalty);
					if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
						saveContactsLoyalty(contactsLoyalty);
					}
					
					creditedReward = balToCredit+"";
					description = balToCredit+";=;"+balToCredit+";=;"+0+";=;"+0;
					createTransctnAndExpiry(header, amount, originalRecpt, contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, 
							balToCredit, earnedPts, OCConstants.LOYALTY_TYPE_AMOUNT, description,redemptionTrans.getTransChildId()); 

					
					returnTransactionResponse = prepareRedmptnSuccessResponse(responseHeader, requestJson, 
							user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
							isIssuanceFailed, msg, contactsLoyalty, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM
							,creditedPoints, creditedReward, creditedGift, credList);
					responseMap.put("returnResponse", returnTransactionResponse);
					return responseMap;
				}
				else {
					if(amountDiff != 0) {
						balToCredit = balToCredit - amountDiff;
						StatusInfo statusInfo = callToSparkbaseApiForIssuance(OCConstants.LOYALTY_USD, balToCredit+Constants.STRING_NILL, sbDetails, contactsLoyalty);
						if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
							saveContactsLoyalty(contactsLoyalty);
						}
						
						creditedReward = amountDiff+"";
					}
				}
			}//Loyalty card
			else if(contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL)) {

				logger.info("--- In giftLoyalty card redemption ---");
				if(amountDiff != 0 && amountDiff >= balToCredit){
					
					StatusInfo statusInfo = callToSparkbaseApiForIssuance(OCConstants.LOYALTY_USD, balToCredit+Constants.STRING_NILL, sbDetails, contactsLoyalty);
					if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
						saveContactsLoyalty(contactsLoyalty);
					}
					
					creditedReward = balToCredit+"";
					description = balToCredit+";=;"+balToCredit+";=;"+0+";=;"+0;
					createTransctnAndExpiry(header, amount, originalRecpt, contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, balToCredit,
							earnedPts, OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId()); 
					
					returnTransactionResponse = prepareRedmptnSuccessResponse( responseHeader, requestJson, 
							user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
							isIssuanceFailed, msg, contactsLoyalty, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM,
							creditedPoints, creditedReward, creditedGift, credList);
					responseMap.put("returnResponse", returnTransactionResponse);
					return responseMap;
				}
				else {
					if(amountDiff != 0) {
						balToCredit = balToCredit - amountDiff;
						creditedReward = amountDiff+"";
						
						StatusInfo statusInfo = callToSparkbaseApiForIssuance(OCConstants.LOYALTY_USD, amountDiff+Constants.STRING_NILL, sbDetails, contactsLoyalty);
						if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
							saveContactsLoyalty(contactsLoyalty);
						}
						
					}
						
					
					//prepare credit object
					Credit credit = prepareCreditObject(contactsLoyalty,  creditedPoints, creditedReward, creditedGift);
					credList.add(credit);
					responseMap.put("creditList",credList);
					responseMap.put("balToCredit",balToCredit);
				}
			}//GiftLoyalty-card
		}
		return responseMap;
	}

	
	
	private Credit prepareCreditObject(ContactsLoyalty contactsLoyalty, String creditedPoints, 
			String creditedReward, String creditedGift) {

		logger.info("prepareCreditObject method called...");
		Credit credit = new Credit();
		credit.setMembershipNumber(contactsLoyalty.getCardNumber()+"");
		//if(earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
		credit.setRewardPoints(creditedPoints);
		credit.setRewardCurrency(creditedReward);
		credit.setGift(creditedGift);
		/*}
		else if(earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT) && 
				 contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L)){
			credit.setRewardPoints(creditedPoints);
			credit.setRewardCurrency(creditedReward);
			credit.setGift(creditedGift);
		}else if(earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT) && 
						contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL)){
			credit.setRewardPoints(creditedPoints);
			credit.setRewardCurrency(creditedReward);
			credit.setGift(creditedGift);
		}
		else {
			credit.setRewardPoints(creditedPoints);
			credit.setRewardCurrency(creditedReward);
			credit.setGift(creditedGift);
		}*/
		logger.info("prepareCreditObject method exit...");
		return credit;
	}//prepareCreditObject

	private void createTransctnAndExpiry(RequestHeader header, Amount amount, OriginalReceipt originalRecpt, ContactsLoyalty contactsLoyalty,
											Users user, ResponseHeader responseHeader, String rewardFlag, 
											double earnedAmount, double earnedPoints, String earnType, String description, Long redeemedOn) {
		
		logger.info("createTransctnAndExpiry method called...");
		long pointsDifference = 0;
		double amountDifference = 0;
		if(earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT))
			amountDifference = Double.parseDouble(Utility.truncateUptoTwoDecimal(earnedAmount));
		else if(earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS))
			pointsDifference = (long)earnedPoints;
		else{
			amountDifference = Double.parseDouble(Utility.truncateUptoTwoDecimal(earnedAmount));
			pointsDifference = (long)earnedPoints;
		}
		//create transaction 
		LoyaltyTransactionChild childTx = createReturnTransaction(header, amount, originalRecpt, contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
				""+pointsDifference,""+amountDifference,earnedAmount,earnedPoints, earnType,responseHeader.getTransactionId(),
				Double.parseDouble(amount.getEnteredValue()),
				OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL, description, redeemedOn);

		//Expiry transaction
		//createExpiryTransaction(contactsLoyalty, earnedAmount, (long)earnedPoints, childTx.getTransChildId(), rewardFlag);
		logger.info("createTransctnAndExpiry method exit...");
	}//createTransctnAndExpiry

	private LoyaltyReturnTransactionResponse prepareRedmptnSuccessResponse(ResponseHeader responseHeader, String requestJson, Users user,
													MembershipResponse response, List<Balance> balances, HoldBalance holdBalance, BalancesAdditionalInfo additionalInfo, 
													List<MatchedCustomer> matchedCustomers, boolean isIssuanceFailed, String msg, 
													ContactsLoyalty contactsLoyalty, String enteredAmountType, 
													String creditedPoints, String creditedReward, String creditedGift, List<Credit> credList) throws Exception{
		
		logger.info("prepareRedmptnSuccessResponse method called...");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;
		
		if(isIssuanceFailed) {
			msg += " Return was successful on redemption.";
		}
		else {
			msg = "Return was successful.";
		}
		additionalInfo = prepareCreditsAdditionalInfo(additionalInfo, OCConstants.LOYALTY_TYPE_REVERSAL, contactsLoyalty.getRewardFlag(),
													creditedPoints, creditedReward, creditedGift, contactsLoyalty.getCardNumber(), enteredAmountType, credList);
		status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
		logger.info("prepareRedmptnSuccessResponse method exit...");
		return returnTransactionResponse;
	}//prepareRedmptnSuccessResponse

	private Map<String, Object> getContactLtyObj(LoyaltyTransactionChild redemptionTrans, Users user) throws Exception {
		
		logger.info("getContactLtyObj method called...");
		Map<String, Object> conMap = new HashMap<String, Object>();
		//check if program is active and card-set
		/*LoyaltyProgram loyaltyProgram = null;
		loyaltyProgram = findLoyaltyProgramByProgramId(redemptionTrans.getProgramId(), user.getUserId());
		if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus())){
			conMap.put("flag", "true");
		}
		if(loyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)) {
			LoyaltyCardSet loyaltyCardSet = null;
			loyaltyCardSet = findLoyaltyCardSetByCardsetId(redemptionTrans.getCardSetId(), user.getUserId());
			if(loyaltyCardSet == null || !OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE.equals(loyaltyCardSet.getStatus())){
				conMap.put("flag", "true");
			}
		}*/
		
		ContactsLoyalty contactsLoyalty = findLoyaltyById(redemptionTrans.getLoyaltyId(), redemptionTrans.getProgramId(),
																			user.getUserId());
		if(contactsLoyalty == null){
			conMap.put("flag", "true");
		}
		if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED) || 
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
			conMap.put("flag", "true");
		}
		conMap.put("loyaltyObj", contactsLoyalty);
		logger.info("getContactLtyObj method exit...");
		return conMap;
	}//getContactLtyObj

	private LoyaltyReturnTransactionResponse performIssuanceBasedReversal(SparkBaseLocationDetails sbDetails, LoyaltyTransactionChild loyaltyTransactionChild, RequestHeader header, Amount amount,
				OriginalReceipt originalRecpt, String creditReddemedAmount,List<SkuDetails> itemList, ResponseHeader responseHeader, 
				String requestJson, Users user,double diffAmt, String mode) throws Exception {
		
		logger.info("performIssuanceBasedReversal method called...");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		/*if(Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue()) > loyaltyTransactionChild.getEnteredAmount()) {
		   Status status = new Status("111567", PropertyUtil.getErrorMessage(111567, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;
		}*/
		
		ContactsLoyalty contactsLoyalty = findLoyaltyById(loyaltyTransactionChild.getLoyaltyId(), loyaltyTransactionChild.getProgramId(),
				user.getUserId());
		
		if(contactsLoyalty == null){
			/*status = new Status("1000", PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null,status);
			return returnTransactionResponse;*/
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(originalRecpt.getDocSID(), 
					user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,null,null,null);
			returnTransactionResponse = performRedemptnBasedReversal(sbDetails, redempTransList, header, amount, originalRecpt, creditReddemedAmount,	responseHeader, requestJson, user, 
					null, null, null, null, null,  true, PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), 1000);
			return returnTransactionResponse;
		}

		
		//Double returnedAmountdbl = calculateReturnAmount(itemList );
		Double returnedAmountdbl = 0.0;
		Double returnedAmount = calculateReturnAmount(itemList );
		String res = Utility.truncateUptoTwoDecimal(returnedAmount);
		if(res != null)
			returnedAmountdbl = Double.parseDouble(res);
		logger.info("Return value "+returnedAmountdbl);
		if(returnedAmountdbl >= diffAmt){
			returnedAmountdbl = diffAmt;
		}
		
		
		Double netReturnedAmountdbl = returnedAmountdbl;
		
		//double netReturnedAmount = Math.round(netReturnedAmountdbl);
		double netReturnedAmount = netReturnedAmountdbl;
		
		
		
		returnTransactionResponse = performReversalOperation(sbDetails, loyaltyTransactionChild, header, amount, originalRecpt, creditReddemedAmount, responseHeader, 
															contactsLoyalty, user, requestJson, netReturnedAmount, returnedAmountdbl);
		logger.info("performIssuanceBasedReversal method exit...");
		return returnTransactionResponse;
	}//performIssuanceBasedReversal

	private ContactsLoyalty findLoyaltyById(Long loyaltyId,Long programId, Long userId) throws Exception {

		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findLoyaltyById(loyaltyId, programId, userId);
	}//findLoyaltyById

	private List<MatchedCustomer> prepareMatchedCustomers(List<ContactsLoyalty> enrollList) throws Exception {

		logger.info("prepareMatchedCustomers method called...");
		Contacts contact = null;
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		List<MatchedCustomer> matchedCustList = new ArrayList<MatchedCustomer>();
		MatchedCustomer matchedCustomer = null;

		for(ContactsLoyalty loyalty : enrollList){
			if(loyalty.getContact() != null && loyalty.getContact().getContactId() != null){
				contact = contactsDao.findById(loyalty.getContact().getContactId());
				if(contact != null){
					matchedCustomer = new MatchedCustomer();
					matchedCustomer.setMembershipNumber(""+loyalty.getCardNumber());
					matchedCustomer.setFirstName(contact.getFirstName() == null ? "" : contact.getFirstName().trim());
					matchedCustomer.setLastName(contact.getLastName() == null ? "" : contact.getLastName().trim());
					matchedCustomer.setCustomerId(contact.getExternalId() == null ? "" : contact.getExternalId());
					matchedCustomer.setEmailAddress(contact.getEmailId() == null ? "" : contact.getEmailId());
					matchedCustomer.setPhone(contact.getMobilePhone() == null ? "" : contact.getMobilePhone());
					matchedCustList.add(matchedCustomer);
				}
			}
		}
		logger.info("prepareMatchedCustomers method exit...");
		return matchedCustList;
	}//prepareMatchedCustomers

	
	private LoyaltyReturnTransactionResponse performGiftSCReturn(RequestHeader header, Amount amount, OriginalReceipt originalRecpt,
			String membershipNo, ResponseHeader responseHeader,LoyaltyProgram loyaltyProgram,
			Users user, String requestJson) throws Exception {

		logger.info("-- Enetred performGiftSCReturn --");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;

		ContactsLoyalty contactsLoyalty = findContactLoyalty(membershipNo, loyaltyProgram.getProgramId(), user.getUserId());

		if(contactsLoyalty == null){
			status = new Status("1000", PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;
		}

		if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED) || 
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
			LoyaltyProgramTier tier = null;
			
			
			List<Balance> balances = null ; 
			List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
			
			
			if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
				contactLoyaltyList.add(contactsLoyalty);
				if(contactsLoyalty.getProgramTierId() != null) tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				
				balances = prepareBalancesObject(contactsLoyalty, "", "", "");
				String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
				contactLoyaltyList.add(contactsLoyalty);
				
				if(contactsLoyalty.getProgramTierId() != null)	tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				balances = prepareBalancesObject(contactsLoyalty, "", "", "");
				String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}else if( contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
				ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactsLoyalty);
				String maskedNum = Constants.STRING_NILL;
				if(destLoyalty != null) {
					contactLoyaltyList.add(destLoyalty);
					contactsLoyalty = destLoyalty;
					tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
					balances = prepareBalancesObject(destLoyalty, "", "", "");
					maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
					
				}
				String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+maskedNum+".";
				status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			MembershipResponse response = prepareMembershipResponse(contactsLoyalty);
			List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, null, null, matchedCustomers, status);
			return returnTransactionResponse;
		}
		else {
			status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;
		}
	}//performGiftSCReturn
	private ContactsLoyalty getDestMembershipIfAny(ContactsLoyalty contactLoyalty) throws Exception{
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		if(contactLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED) && contactLoyalty.getTransferedTo() != null) {
			return loyaltyDao.findAllByLoyaltyId(contactLoyalty.getTransferedTo());
			
		}
		
		return null;
	}

	private BalancesAdditionalInfo prepareCreditsAdditionalInfo(BalancesAdditionalInfo additionalInfo, String type, 
																String flag, String creditedPoints, String creditedReward, String creditedGift, 
																String membershipNumber, String enteredAmountType, List<Credit> creditList) {
		logger.info("-- Entered prepareCreditsAdditionalInfo --");
		if(additionalInfo == null) {
			additionalInfo = new BalancesAdditionalInfo();
			Debit debit = new Debit();
			additionalInfo.setDebit(debit);
		}

		//List<Credit> creditList = new ArrayList<Credit>();
		Credit credit = new Credit();
		credit.setMembershipNumber(membershipNumber+"");
		credit.setRewardPoints(creditedPoints);
		credit.setRewardCurrency(creditedReward);
		credit.setGift(creditedGift);
		creditList.add(credit);
		additionalInfo.setCredit(creditList);
		logger.info("-- Exit prepareCreditsAdditionalInfo --");
		return additionalInfo;
	}

	
	
	private StatusInfo callToSparkbaseApiForRedemption(String valueCode, String enteredAmount, 
			SparkBaseLocationDetails sparkbaseLocation, ContactsLoyalty contactLoyalty) throws Exception {
		 logger.info("callToSparkbaseApiForRedemption==="+valueCode+"==="+enteredAmount);
		Object sbRedemptionResponse = null;
		String[] amount = new String[2];
		amount[0] = valueCode;
		amount[1] = "-"+enteredAmount;
		
		if(valueCode.equals(OCConstants.LOYALTY_TYPE_POINTS)){
			sbRedemptionResponse = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.ADJUSTMENT, sparkbaseLocation, contactLoyalty, null, amount, true);
		}
		else if(valueCode.equals(OCConstants.LOYALTY_USD)){
			sbRedemptionResponse = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.ADJUSTMENT, sparkbaseLocation, contactLoyalty, null, amount, true);
		}


		AdjustmentResponse loyaltyRedemptionResponse = null;
		GiftRedemptionResponse giftRedemptionResponse = null;
		ErrorMessageComponent errorMsg = null;
		StatusInfo statusInfo = null;
		
		if(sbRedemptionResponse instanceof ErrorMessageComponent) {
			errorMsg = (ErrorMessageComponent)sbRedemptionResponse;
		} else if (sbRedemptionResponse instanceof AdjustmentResponse){
			loyaltyRedemptionResponse = (AdjustmentResponse)sbRedemptionResponse;
			ResponseStandardHeaderComponent standardHeader = loyaltyRedemptionResponse.getStandardHeader();
			if (standardHeader.getStatus().equals("E")) {
				  logger.info("Printing Error...");
		          errorMsg = loyaltyRedemptionResponse.getErrorMessage();
		    }
		}
		/*else if (sbRedemptionResponse instanceof GiftRedemptionResponse){
			giftRedemptionResponse = (GiftRedemptionResponse)sbRedemptionResponse;
			ResponseStandardHeaderComponent standardHeader = giftRedemptionResponse.getStandardHeader();
			if (standardHeader.getStatus().equals("E")) {
				  logger.info("Printing Error...");
		          errorMsg = giftRedemptionResponse.getErrorMessage();
		    }
		}*/
		logger.info("callToSparkbaseApiForRedemption==="+errorMsg);
		if(errorMsg != null){
			statusInfo = new StatusInfo("200102", PropertyUtil.getErrorMessage(200102, OCConstants.ERROR_LOYALTY_FLAG)+", "+errorMsg.getBriefMessage(),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
		else{
			//if(valueCode.equals("Points")){
				statusInfo = new StatusInfo("0", "Successfully redeemed.",
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			//}
			/*else if(valueCode.equals("USD")){
				statusInfo = new StatusInfo("0", "Gift Redemption Successful.",
						OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			}*/
		}
		
		return statusInfo;
	}
	private LoyaltyReturnTransactionResponse performReversalOperation(SparkBaseLocationDetails sbDetails, LoyaltyTransactionChild loyaltyTransactionChild, 
			RequestHeader header, Amount amount,
			OriginalReceipt originalRecpt, String creditReddemedAmount, ResponseHeader responseHeader,
			ContactsLoyalty contactsLoyalty, Users user, String requestJson, double netReturnedAmt, Double returnedAmountdbl) throws Exception {

		logger.info("-- Entered performReversalOperation --");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;
		StatusInfo statusInfo = null;
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		double earnedValue = 0;
		String pointsDifference = "";
		String amountDifference = "";
		double autoCnvrtPtsCredit = 0;
		double remainderDebit = 0.0;
		
		String debitedRewardPoints = "";
		String debitedHoldPoints = "";
		String debitedRewardCurrency = "";
		String debitedHoldCurrency = "";
		
		String remainderBal = Constants.STRING_NILL;
		String earnType = "";
		double loyaltyPoints = contactsLoyalty.getLoyaltyBalance() == null ? 0 : contactsLoyalty.getLoyaltyBalance();
		Double ltyCurrBal = contactsLoyalty.getGiftcardBalance() == null ? 0 : contactsLoyalty.getGiftcardBalance();
		if(loyaltyTransactionChild.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
			logger.info("-- Entered performReversalOperation --1"+earnedValue +" "+loyaltyPoints);
			Double multipleFactordbl = loyaltyTransactionChild.getEarnedPoints()/(loyaltyTransactionChild.getEnteredAmount());
			earnedValue = Math.round(netReturnedAmt * multipleFactordbl);
			if(loyaltyPoints >= earnedValue){
				logger.info("-- Entered performReversalOperation --2");
				pointsDifference = "-"+(long)earnedValue;//amountDifference = "-"+earnedValue;
				earnType = OCConstants.LOYALTY_TYPE_POINTS;
				
				debitedRewardPoints = (long)earnedValue+"";
				 statusInfo = callToSparkbaseApiForRedemption(earnType, (long)earnedValue+Constants.STRING_NILL, sbDetails, contactsLoyalty);
				logger.info("-- Entered performReversalOperation --4"+statusInfo.getERRORCODE());
				
			}
			else if((loyaltyPoints) < earnedValue ){
				remainderDebit = earnedValue-loyaltyPoints;
				earnType = OCConstants.LOYALTY_TYPE_POINTS;
				logger.info("-- Entered performReversalOperation --3"+remainderDebit);
				if(loyaltyPoints > 0) {
					
					pointsDifference = "-"+(long)loyaltyPoints;
					statusInfo = callToSparkbaseApiForRedemption(earnType, (long)loyaltyPoints+Constants.STRING_NILL, sbDetails, contactsLoyalty);
					logger.info("-- Entered performReversalOperation --4"+statusInfo.getERRORCODE());
					
					debitedRewardPoints = (long)loyaltyPoints+"";
				}
				if(sbDetails.getConversionType() != null && 
						sbDetails.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) && 
						sbDetails.getConvertFromPoints() != null && sbDetails.getConvertFromPoints() > 0 &&
						sbDetails.getConvertToAmount() != null && ltyCurrBal != null ){
					
					if(   ltyCurrBal > 0){//auto conversion reversal
						earnType = OCConstants.LOYALTY_USD;
						
						double multipledouble = (double)remainderDebit/(double)sbDetails.getConvertFromPoints();
						logger.info("-- Entered performReversalOperation --4"+multipledouble);
						String res = Utility.truncateUptoTwoDecimal((sbDetails.getConvertToAmount() * multipledouble));//(for earnedPointsValue pts ? amt)
						if(res != null) {
							logger.info("-- Entered performReversalOperation --4"+res);
							earnedValue = Double.parseDouble(res);
							if(ltyCurrBal < earnedValue) {//added for partial reversal
								remainderDebit = earnedValue-ltyCurrBal;
								remainderBal = Utility.truncateUptoTwoDecimal(remainderDebit)+Constants.STRING_NILL;
								earnedValue = ltyCurrBal;//available total balance should be deducted
								
							}
							if(earnedValue > 0 ) {
								debitedRewardCurrency = earnedValue+Constants.STRING_NILL;
								statusInfo = callToSparkbaseApiForRedemption(earnType, earnedValue+Constants.STRING_NILL, sbDetails, contactsLoyalty);
								
							}
							
						}
						amountDifference = "-"+earnedValue;
					
					}else{
						
						double multipledouble = (double)remainderDebit/(double)sbDetails.getConvertFromPoints();
						String res = Utility.truncateUptoTwoDecimal((sbDetails.getConvertToAmount() * multipledouble));//(for earnedPointsValue pts ? amt)
						if(res != null)
							remainderDebit = Double.parseDouble(res);
						remainderBal = Utility.truncateUptoTwoDecimal(remainderDebit)+Constants.STRING_NILL;
						
					}
				}else{
					if(loyaltyPoints == 0) {
						
						statusInfo = new StatusInfo("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					}
					//return status;
				}
				
			}
			
		}
		else {
			if(ltyCurrBal != null && ltyCurrBal>0) {
				
				Double multipleFactordbl = loyaltyTransactionChild.getEarnedAmount()/(loyaltyTransactionChild.getEnteredAmount());
				//earnedValue = Math.round(netReturnedAmt * multipleFactordbl);
				String res = Utility.truncateUptoTwoDecimal(netReturnedAmt * multipleFactordbl);
				logger.debug("res ==="+res);
				earnType = OCConstants.LOYALTY_USD;
				if(res != null) {
					
					earnedValue = Double.parseDouble(res);
					if(ltyCurrBal < earnedValue) {//added for partial reversal
						remainderDebit = earnedValue-ltyCurrBal;
						remainderBal = Utility.truncateUptoTwoDecimal(remainderDebit)+Constants.STRING_NILL;
						earnedValue = ltyCurrBal;//available total balance should be deducted
						
					}if(earnedValue > 0){
						debitedRewardCurrency = earnedValue+"";
						statusInfo = callToSparkbaseApiForRedemption(earnType, earnedValue+Constants.STRING_NILL, sbDetails, contactsLoyalty);
						
						
						amountDifference = "-"+earnedValue;
					}
				}
			}else{
				
				statusInfo = new StatusInfo("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
		}
		
		//Prepare membership and matched customers objects
		MembershipResponse response = prepareMembershipResponse(contactsLoyalty);
		List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
		contactLoyaltyList.add(contactsLoyalty);
		List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);

		//Update balances
		Map<String, Object> balMap = new HashMap<String, Object>();
		balMap.put("debitedHoldPoints", debitedHoldPoints);
		balMap.put("debitedRewardPoints", debitedRewardPoints);
		balMap.put("debitedHoldCurrency", debitedHoldCurrency);
		balMap.put("debitedRewardCurrency", debitedRewardCurrency);
		//balMap.put("remainderDebit", remainderBal+Constants.STRING_NILL);
		logger.debug("earnedValue ==="+earnedValue);
		//perform SB redemption
		
		
		if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
			saveContactsLoyalty(contactsLoyalty);
		}else {
			List<Balance> balances = prepareBalancesObject(contactsLoyalty, "", "", "");
			String expiryPeriod = "";
			
			HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
			BalancesAdditionalInfo additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
			
			//check for redemption reversal
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(originalRecpt.getDocSID(), 
					user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,null,null,null);
			returnTransactionResponse = performRedemptnBasedReversal(sbDetails, redempTransList, header, amount, originalRecpt, creditReddemedAmount, responseHeader, requestJson, user, 
																	response, balances, holdBalance, additionalInfo, matchedCustomers,  true,  PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 111562);
			return returnTransactionResponse;
			
		}

		//Create return transaction
		String description = returnedAmountdbl+"";
	    createReturnTransaction(header, amount, originalRecpt, 
	    		contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
				pointsDifference, amountDifference, null , null, null, 
				responseHeader.getTransactionId(), Double.parseDouble(amount.getEnteredValue()),OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL, 
				description, null);
		
		List<Balance> balances = prepareBalancesObject(contactsLoyalty, ""+pointsDifference, ""+amountDifference, "");
		String expiryPeriod = "";
		
		
		//check for redemption reversal
		
		HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
		BalancesAdditionalInfo additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
		
		LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(originalRecpt.getDocSID(), 
				user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,null,null,null);
		returnTransactionResponse = performRedemptnBasedReversal(sbDetails, redempTransList, header, amount, originalRecpt, creditReddemedAmount, responseHeader, requestJson, user, 
																	response, balances, holdBalance, additionalInfo, matchedCustomers, false, "Return was successful on issuance.", 0);
		 if(!remainderBal.isEmpty()) {
			 Status retStatus = returnTransactionResponse.getStatus();
			 retStatus.setMessage(retStatus.getMessage()+" Remainder Debit:$"+remainderBal+".");
			 returnTransactionResponse.setStatus(retStatus);
		 }
		
		return returnTransactionResponse;
		
		/*status = new Status("0", "Return was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
		return returnTransactionResponse;*/
	}//performReversalOperation()

	private BalancesAdditionalInfo prepareDebitAddtionalInfo(Map<String, Object> balMap, ContactsLoyalty contactsLoyalty) {
		BalancesAdditionalInfo additionalInfo = new BalancesAdditionalInfo();
		
		Debit debit = new Debit();
		debit.setMembershipNumber(contactsLoyalty.getCardNumber()+"");
		debit.setRewardPoints((String)(balMap.get("debitedRewardPoints")));
		debit.setRewardCurrency((String)(balMap.get("debitedRewardCurrency")));
		debit.setHoldPoints((String)(balMap.get("debitedHoldPoints")));
		debit.setHoldCurrency((String)(balMap.get("debitedHoldCurrency")));
		additionalInfo.setDebit(debit);
		
		List<Credit> credList = new ArrayList<Credit>();
		additionalInfo.setCredit(credList);
		
		return additionalInfo;
	}

	private HoldBalance prepareHoldBalances(ContactsLoyalty contactsLoyalty, String activationPeriod) {
		HoldBalance holdBalance = new HoldBalance();
		holdBalance.setActivationPeriod(activationPeriod);
		if(contactsLoyalty.getHoldAmountBalance() == null){
			holdBalance.setCurrency("");
		}
		else{
			//double value = new BigDecimal(contactsLoyalty.getHoldAmountBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			String res = Utility.truncateUptoTwoDecimal(contactsLoyalty.getHoldAmountBalance());
			double value = Double.parseDouble(res);
			holdBalance.setCurrency(""+value);
		}
		holdBalance.setPoints(contactsLoyalty.getHoldPointsBalance() == null ? "" : ""+contactsLoyalty.getHoldPointsBalance().intValue());
		return holdBalance;
	}

	private LoyaltyTransactionChild createReturnTransaction(RequestHeader header, Amount amount, OriginalReceipt originalRecipt, ContactsLoyalty loyalty,
			Long orgId, String ptsDiff, String amtDiff,Double earnedAmt, Double earnedPts, String earnType,
			String transactionId,Double enteredAmount, String entAmountType, String description, Long redeemedOn) {

		LoyaltyTransactionChild transaction = null;
		try{

			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(transactionId));
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());

			transaction.setCreatedDate(Calendar.getInstance());
			//transaction.setEnteredAmount(enteredAmount);
			String res = Utility.truncateUptoTwoDecimal(enteredAmount);
			if(res != null)
				transaction.setEnteredAmount(Double.parseDouble(res));
			transaction.setEnteredAmountType(entAmountType);
			transaction.setEarnType(earnType);
			transaction.setEarnedAmount(earnedAmt);
			transaction.setEarnedPoints(earnedPts);
			transaction.setAmountDifference(amtDiff);
			transaction.setPointsDifference(ptsDiff);
			transaction.setOrgId(orgId);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_RETURN);
			transaction.setStoreNumber(header.getStoreNumber());
			
			transaction.setSubsidiaryNumber(header.getSubsidiaryNumber() != null && !header.getSubsidiaryNumber().trim().isEmpty() ? header.getSubsidiaryNumber().trim() : null);
			transaction.setReceiptNumber(header.getReceiptNumber() != null && !header.getReceiptNumber().trim().isEmpty() ? header.getReceiptNumber() : null);
			
			
			
			transaction.setDocSID(header.getDocSID());
			//transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE);
			transaction.setSourceType(header.getSourceType());
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setDescription(description);
			transaction.setEmployeeId(header.getEmployeeId() != null &&
					!header.getEmployeeId().trim().isEmpty() ? header.getEmployeeId().trim() : null);
			transaction.setTerminalId(header.getTerminalId() != null && 
					!header.getTerminalId().trim().isEmpty() ? header.getTerminalId().trim() : null);
			transaction.setRedeemedOn(redeemedOn);
			if(!amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT)) {
				transaction.setDescription2(originalRecipt != null && 
						originalRecipt.getDocSID() != null && !originalRecipt.getDocSID().isEmpty() ?
								"OR-"+originalRecipt.getDocSID()+"" :"Membership-"+loyalty.getCardNumber());
			}
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		return transaction;
	}

	private Map<String, Object> updateContactLoyaltyBalances(SparkBaseLocationDetails sbDetails, double earnedVal, String earnType, 
			ContactsLoyalty contactsLoyalty,double netReturnedAmt) throws Exception {

		try {
		Status status = null;
		double loyaltyPoints = contactsLoyalty.getLoyaltyBalance() == null ? 0 : contactsLoyalty.getLoyaltyBalance();
		double totalLtyPoints = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0 : contactsLoyalty.getTotalLoyaltyEarned();
		//double holdPoints = contactsLoyalty.getHoldPointsBalance() == null ? 0 : contactsLoyalty.getHoldPointsBalance();
		double loyaltyAmount = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
		double totalLoyaltyAmount = contactsLoyalty.getTotalGiftcardAmount() == null ? 0.0 : contactsLoyalty.getTotalGiftcardAmount();
		//double holdAmount = contactsLoyalty.getHoldAmountBalance() == null ? 0 : contactsLoyalty.getHoldAmountBalance();
		double returnBal = earnedVal;
		double CRV = contactsLoyalty.getCummulativeReturnValue() == null  ? 0 : contactsLoyalty.getCummulativeReturnValue();
		double amountToIgnore = contactsLoyalty.getAmountToIgnore() == null  ? 0 : contactsLoyalty.getAmountToIgnore();

		logger.info("loyaltyPoints="+loyaltyPoints);
		logger.info("loyaltyAmount="+loyaltyAmount);
		logger.info("returnBal="+returnBal);
		
		
		Map<String, Object> balMap = new HashMap<String, Object>();
		String debitedRewardPoints = "";
		String debitedHoldPoints = "";
		String debitedRewardCurrency = "";
		String debitedHoldCurrency = "";
		Double remainderDebit=0.0;
		String remainderBal = Constants.STRING_NILL;
		Double currBal = contactsLoyalty.getGiftcardBalance();
		//if(LPV != 0 && LPV>returnBal)contactsLoyalty.setLifeTimePurchaseValue(LPV-returnBal);
		contactsLoyalty.setCummulativeReturnValue(CRV+netReturnedAmt);//Changes LPV
		//contactsLoyalty.setAmountToIgnore(amountToIgnore+netReturnedAmt);

		if(earnType != null && earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)){
			
			long deductPoints = 0;
			if((loyaltyPoints) >= earnedVal){
				logger.info("Earned points is less than available points bal...");
				
				deductPoints = (long) returnBal;
				
				StatusInfo statusInfo = callToSparkbaseApiForRedemption(earnType, returnBal+Constants.STRING_NILL, sbDetails, contactsLoyalty);
				
				if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
					saveContactsLoyalty(contactsLoyalty);
				}
				
				debitedRewardPoints = deductPoints+"";
			}else {
				//deduct those many available first for remaining check the currency bal 
				//and based on conversion rule reverse it and get equallent currency and deduct 
				remainderDebit = earnedVal-loyaltyPoints;
				if(loyaltyPoints > 0){
					debitedRewardPoints = loyaltyPoints+"";
					StatusInfo statusInfo = callToSparkbaseApiForRedemption(earnType, loyaltyPoints+Constants.STRING_NILL, sbDetails, contactsLoyalty);
					
					if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
						saveContactsLoyalty(contactsLoyalty);
					}
				}
				 if(sbDetails.getConversionType() != null && sbDetails.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) 
						 &&  sbDetails.getConvertFromPoints() != null && 
						 sbDetails.getConvertFromPoints() > 0  ) {
					 
					 if(contactsLoyalty.getGiftcardBalance() > 0) {
						 long remainderDebitLong = remainderDebit.longValue();
						 Double returnpointsEquallentAmt = applyConversionRules(remainderDebitLong, sbDetails);
						 logger.debug("returnpointsEquallentAmt ==="+returnpointsEquallentAmt);
							
							deductPoints = (long) returnBal;
							if(currBal != null && currBal >0 && returnpointsEquallentAmt > 0 && currBal< returnpointsEquallentAmt){
								remainderDebit = returnpointsEquallentAmt-currBal;
								remainderBal = Utility.truncateUptoTwoDecimal(remainderDebit)+Constants.STRING_NILL;
								
								returnpointsEquallentAmt = currBal;
							}
							if(returnpointsEquallentAmt > 0 ) {
								debitedRewardCurrency = returnpointsEquallentAmt+Constants.STRING_NILL;
								StatusInfo statusInfo = callToSparkbaseApiForRedemption(OCConstants.LOYALTY_USD, returnpointsEquallentAmt+Constants.STRING_NILL, sbDetails, contactsLoyalty);
								if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
									saveContactsLoyalty(contactsLoyalty);
								}
							}
						
					}else{
						 long remainderDebitLong = remainderDebit.longValue();
						 remainderDebit = applyConversionRules(remainderDebitLong, sbDetails);
						remainderBal = Utility.truncateUptoTwoDecimal(remainderDebit)+Constants.STRING_NILL;
					}
						 
						 
				 }else{
					 if(loyaltyPoints == 0) {
						 
						 status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
								 OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					 }
						//return status;
				}
				
			}
			
			
		}
		else if(earnType != null && earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)){
			double deductAmount = 0.0;
			double deductHoldAmount = 0.0;
			StatusInfo statusInfo = null;
			if(loyaltyAmount > 0) {
				
				if((loyaltyAmount) >= earnedVal){
					logger.info("Earned amount is less than available amount bal...");
					
					deductAmount = returnBal;
					
					debitedRewardCurrency = deductAmount+Constants.STRING_NILL;
				}
				else{
					if(loyaltyAmount < earnedVal) {//added for partial reversal
						remainderDebit = returnBal-loyaltyAmount;
						remainderBal = Utility.truncateUptoTwoDecimal(remainderDebit)+Constants.STRING_NILL;
						returnBal = loyaltyAmount;//available total balance should be deducted
						debitedRewardCurrency = returnBal+Constants.STRING_NILL;
					}
					
					/*status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);*/
					//return status;
				}
				
				 statusInfo = callToSparkbaseApiForRedemption(OCConstants.LOYALTY_USD, returnBal+Constants.STRING_NILL, sbDetails, contactsLoyalty);
				
			}else{
				
				statusInfo = new StatusInfo("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			if(statusInfo != null && statusInfo.getERRORCODE().equals("0")){
				saveContactsLoyalty(contactsLoyalty);
			}
		}
//		balMap.put("autoCnvrtPtsCredit", autoCnvrtPtsCredit);
		balMap.put("debitedHoldPoints", debitedHoldPoints);
		balMap.put("debitedRewardPoints", debitedRewardPoints);
		balMap.put("debitedHoldCurrency", debitedHoldCurrency);
		balMap.put("debitedRewardCurrency", debitedRewardCurrency);
		balMap.put("remainderDebit", remainderBal+Constants.STRING_NILL);
		balMap.put("status", status);
		return balMap;
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
			return null;
		}
	}
	
	
	private void deductPointsFromExpiryTable(ContactsLoyalty loyalty, Long userId, long subPoints) throws Exception{
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; 
		Iterator<LoyaltyTransactionExpiry> iterList = null;
		LoyaltyTransactionExpiry expiry = null;
		long remPoints = subPoints;
		
		do{
			expiryList = expiryDao.fetchExpLoyaltyPtsTrans(loyalty.getLoyaltyId(), 100, userId);
			if(expiryList == null || remPoints <= 0) break;
			iterList = expiryList.iterator();
			
			while(iterList.hasNext()){
				expiry = iterList.next();
				
				if(expiry.getExpiryPoints() == null || expiry.getExpiryPoints() <= 0){ 
					logger.info("WRONG EXPIRY TRANSACTION FETCHED...");
					continue;
				}
				else if(expiry.getExpiryPoints() < remPoints){
					logger.info("subtracted loyalty points = "+expiry.getExpiryPoints());
					remPoints = remPoints - expiry.getExpiryPoints().longValue();
					expiry.setExpiryPoints(0l);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					continue;
					
				}
				else if(expiry.getExpiryPoints() >= remPoints){
					logger.info("subtracted loyalty points = "+expiry.getExpiryPoints());
					expiry.setExpiryPoints(expiry.getExpiryPoints() - remPoints);
					remPoints = 0; 
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					break;
				}
				
			}
			expiryList = null;
		
		}while(remPoints > 0);
		
		//createTransactionForExpiry(loyalty, subPoints-remPoints, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTS_EXP);
	}

	private void deductLoyaltyAmtFromExpiryTable(ContactsLoyalty loyalty, Long userId, double subAmt) throws Exception{

		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; 
		Iterator<LoyaltyTransactionExpiry> iterList = null;
		LoyaltyTransactionExpiry expiry = null;
		double remAmount = subAmt;

		do{
			expiryList = expiryDao.fetchExpLoyaltyAmtTrans(loyalty.getLoyaltyId(), 100, userId);
			if(expiryList == null || remAmount <= 0) break;
			iterList = expiryList.iterator();

			while(iterList.hasNext()){
				expiry = iterList.next();

				if(expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0){ 
					logger.info("WRONG EXPIRY TRANSACTION FETCHED...");
					continue;
				}
				else if(expiry.getExpiryAmount() < remAmount){
					logger.info("subtracted loyalty amount = "+expiry.getExpiryAmount());
					remAmount = remAmount - expiry.getExpiryAmount().doubleValue();
					expiry.setExpiryAmount(0.0);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					logger.info("Expiry Amount deducted..."+expiry.getExpiryAmount().doubleValue());
					continue;

				}
				else if(expiry.getExpiryAmount() >= remAmount){
					logger.info("subtracted loyalty amount = "+expiry.getExpiryAmount());
					expiry.setExpiryAmount(expiry.getExpiryAmount() - remAmount);
					remAmount = 0; 
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					logger.info("Expiry Amount deducted..."+remAmount);
					break;
				}

			}
			expiryList = null;

		}while(remAmount > 0);

	}

	private void deductHoldAmount(ContactsLoyalty loyalty, double balanceToSub) {

		try {
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			List<LoyaltyTransactionChild> transList = null; 
			Iterator<LoyaltyTransactionChild> iterList = null;
			LoyaltyTransactionChild loyaltyTransactionChild = null;
			double remAmount = balanceToSub;

			do{
				transList = loyaltyTransactionChildDao.fetchHoldAmtTrans(loyalty.getLoyaltyId(), 100, loyalty.getUserId());
				if(transList == null || remAmount <= 0) break;
				iterList = transList.iterator();

				while(iterList.hasNext()){
					loyaltyTransactionChild = iterList.next();

					if(loyaltyTransactionChild.getHoldAmount() == null || loyaltyTransactionChild.getHoldAmount() <= 0){ 
						logger.info("WRONG TRANSACTION FETCHED...");
						continue;
					}
					else if(loyaltyTransactionChild.getHoldAmount() < remAmount){
						logger.info("subtracted hold amount = "+loyaltyTransactionChild.getHoldAmount());
						remAmount = remAmount - loyaltyTransactionChild.getHoldAmount().doubleValue();
						if(loyaltyTransactionChild.getDescription2() != null){
							loyaltyTransactionChild.setDescription2(loyaltyTransactionChild.getDescription2()+Constants.ADDR_COL_DELIMETER+OCConstants.LOYALTY_TRANS_TYPE_RETURN+":"+loyaltyTransactionChild.getHoldAmount());
						}else{
							loyaltyTransactionChild.setDescription2(OCConstants.LOYALTY_TRANS_TYPE_RETURN+":"+loyaltyTransactionChild.getHoldAmount());
						}
						loyaltyTransactionChild.setHoldAmount(0.0);
						//loyaltyTransactionChildDao.saveOrUpdate(loyaltyTransactionChild);
						loyaltyTransactionChildDaoForDML.saveOrUpdate(loyaltyTransactionChild);
						logger.info("Hold Amount deducted..."+loyaltyTransactionChild.getHoldAmount().doubleValue());
						continue;

					}
					else if(loyaltyTransactionChild.getHoldAmount() >= remAmount){
						logger.info("subtracted loyalty amount = "+loyaltyTransactionChild.getHoldAmount());
						loyaltyTransactionChild.setHoldAmount(loyaltyTransactionChild.getHoldAmount() - remAmount);
						remAmount = 0; 
						if(loyaltyTransactionChild.getDescription2() != null){
							loyaltyTransactionChild.setDescription2(loyaltyTransactionChild.getDescription2()+Constants.ADDR_COL_DELIMETER+OCConstants.LOYALTY_TRANS_TYPE_RETURN+":"+remAmount);
						}else{
							loyaltyTransactionChild.setDescription2(OCConstants.LOYALTY_TRANS_TYPE_RETURN+":"+remAmount);
						}
						//loyaltyTransactionChildDao.saveOrUpdate(loyaltyTransactionChild);
						loyaltyTransactionChildDaoForDML.saveOrUpdate(loyaltyTransactionChild);
						logger.info("Hold Amount deducted..."+remAmount);
						break;
					}

				}
				transList = null;

			}while(remAmount > 0);
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}
	
	private void deductHoldPoints(ContactsLoyalty loyalty, long balanceToSub) {
		
		try {
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			List<LoyaltyTransactionChild> transList = null; 
			Iterator<LoyaltyTransactionChild> iterList = null;
			LoyaltyTransactionChild loyaltyTransactionChild = null;
			long remPoints = balanceToSub;

			do{
				transList = loyaltyTransactionChildDao.fetchHoldPtsTrans(loyalty.getLoyaltyId(), 100, loyalty.getUserId());
				if(transList == null || remPoints <= 0) break;
				iterList = transList.iterator();

				while(iterList.hasNext()){
					loyaltyTransactionChild = iterList.next();

					if(loyaltyTransactionChild.getHoldPoints() == null || loyaltyTransactionChild.getHoldPoints() <= 0){ 
						logger.info("WRONG  TRANSACTION FETCHED...");
						continue;
					}
					else if(loyaltyTransactionChild.getHoldPoints() < remPoints){
						logger.info("subtracted hold points = "+loyaltyTransactionChild.getHoldPoints());
						remPoints = remPoints - loyaltyTransactionChild.getHoldPoints().longValue();
						if(loyaltyTransactionChild.getDescription2() != null){
							loyaltyTransactionChild.setDescription2(loyaltyTransactionChild.getDescription2()+Constants.ADDR_COL_DELIMETER+OCConstants.LOYALTY_TRANS_TYPE_RETURN+":"+loyaltyTransactionChild.getHoldPoints());
						}else{
							loyaltyTransactionChild.setDescription2(OCConstants.LOYALTY_TRANS_TYPE_RETURN+":"+loyaltyTransactionChild.getHoldPoints());
						}
						loyaltyTransactionChild.setHoldPoints(0.0);
						//loyaltyTransactionChildDao.saveOrUpdate(loyaltyTransactionChild);
						loyaltyTransactionChildDaoForDML.saveOrUpdate(loyaltyTransactionChild);
						continue;

					}
					else if(loyaltyTransactionChild.getHoldPoints() >= remPoints){
						logger.info("subtracted hold points = "+loyaltyTransactionChild.getHoldPoints());
						loyaltyTransactionChild.setHoldPoints(loyaltyTransactionChild.getHoldPoints() - remPoints);
						if(loyaltyTransactionChild.getDescription2() != null){
							loyaltyTransactionChild.setDescription2(loyaltyTransactionChild.getDescription2()+Constants.ADDR_COL_DELIMETER+OCConstants.LOYALTY_TRANS_TYPE_RETURN+":"+remPoints);
						}else{
							loyaltyTransactionChild.setDescription2(OCConstants.LOYALTY_TRANS_TYPE_RETURN+":"+remPoints);
						}
						remPoints = 0; 
						//loyaltyTransactionChildDao.saveOrUpdate(loyaltyTransactionChild);
						loyaltyTransactionChildDaoForDML.saveOrUpdate(loyaltyTransactionChild);
						break;
					}

				}
				transList = null;
			}while(remPoints > 0);
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}
	
	private void saveContactsLoyalty(ContactsLoyalty contactsLoyalty) throws Exception {
		ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		loyaltyDao.saveOrUpdate(contactsLoyalty);
	}

	

	private LoyaltyProgram findActiveMobileProgram(Long programId) throws Exception {

		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findById(programId);
	}

	private Status validateStoreNumberExclusion(RequestHeader header, LoyaltyProgram program, 
			LoyaltyProgramExclusion loyaltyExclusion) throws Exception {

		Status status = null;
		if(loyaltyExclusion.getStoreNumberStr() != null && !loyaltyExclusion.getStoreNumberStr().trim().isEmpty()){
			String[] storeNumberArr = loyaltyExclusion.getStoreNumberStr().split(";=;");
			for(String storeNo : storeNumberArr){
				if(header.getStoreNumber().trim().equals(storeNo.trim())){
					status = new Status("111532", PropertyUtil.getErrorMessage(111532, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
			}
		}
		return status;
	}

	private MembershipResponse prepareMembershipResponse(ContactsLoyalty contactsLoyalty) throws Exception {

		MembershipResponse membershipResponse = new MembershipResponse();

		if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
			membershipResponse.setCardNumber(""+contactsLoyalty.getCardNumber());
			membershipResponse.setCardPin(contactsLoyalty.getCardPin());
			membershipResponse.setPhoneNumber("");
		}
		
			membershipResponse.setTierLevel("");
			membershipResponse.setTierName("");
			membershipResponse.setExpiry("");

		

		return membershipResponse;
	}

	private LoyaltyProgramExclusion getLoyaltyExclusion(Long programId) throws Exception {
		try{
			LoyaltyProgramExclusionDao exclusionDao = (LoyaltyProgramExclusionDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO);
			return exclusionDao.getExlusionByProgId(programId);
		}catch(Exception e){
			logger.error("Exception in getting loyalty exclusion ..", e);
		}
		return null;
	}

	private LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{

		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getTierById(tierId);

	}

	private ContactsLoyalty findContactLoyalty(String cardNumber, Long programId, Long userId) throws Exception {

		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findByProgram(cardNumber, programId, userId);
	}

	
	private ContactsLoyalty findLoyaltyCardInOC(String cardNumber, Long userId) throws Exception {
		ContactsLoyalty contactLoyalty = null;
		String parsedCardNumber = OptCultureUtils.parseCardNumber(cardNumber);
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		contactLoyalty = contactsLoyaltyDao.getContactsLoyaltyByCardId(parsedCardNumber, userId);
		return contactLoyalty;
	}
	/*private LoyaltyCards findLoyaltyCardByUserId(String cardNumber, Long userId) throws Exception {

		LoyaltyCardsDao loyaltyCardDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return loyaltyCardDao.findByCardNoAnduserId(cardNumber, userId);

	}*/

	private List<Balance> prepareBalancesObject(ContactsLoyalty loyalty, String pointsDiff, String amountDiff, String giftDiff) throws Exception{
		List<Balance> balancesList = null;
		Balance pointBalances = null;
		Balance amountBalances = null;
		Balance giftBalances = null;
		balancesList = new ArrayList<Balance>();

		pointBalances = new Balance();
		pointBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		pointBalances.setValueCode(OCConstants.LOYALTY_TYPE_POINTS);
		pointBalances.setAmount(loyalty.getLoyaltyBalance() == null ? "" : ""+loyalty.getLoyaltyBalance().intValue());
		pointBalances.setDifference(pointsDiff);

		amountBalances = new Balance();
		amountBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		amountBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		amountBalances.setDifference(amountDiff);
		if(loyalty.getGiftcardBalance() == null){
			amountBalances.setAmount("");
		}
		else{
			//double value = new BigDecimal(loyalty.getGiftcardBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			amountBalances.setAmount(""+loyalty.getGiftcardBalance());
		}

		giftBalances = new Balance();
		giftBalances.setType(OCConstants.LOYALTY_TYPE_GIFT);
		giftBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		if(loyalty.getGiftBalance() == null){
			giftBalances.setAmount("");
		}
		else{
			//double value = new BigDecimal(loyalty.getGiftBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			giftBalances.setAmount(""+loyalty.getGiftBalance());
		}
		giftBalances.setDifference(giftDiff);

		balancesList.add(pointBalances);
		balancesList.add(amountBalances);
		balancesList.add(giftBalances);

		return balancesList;
	}

	private Contacts prepareContactFromJsonData(Customer customerInfo, Long userId) throws Exception {

		logger.info("Entered prepareContactFromJsonData method >>>>>");
		Contacts inputContact = new Contacts();
		if(customerInfo.getCustomerId() != null && customerInfo.getCustomerId().trim().length() > 0) {
			inputContact.setExternalId(customerInfo.getCustomerId().trim());
			logger.info("customer id: "+customerInfo.getCustomerId());
		}
		if(customerInfo.getEmailAddress() != null && customerInfo.getEmailAddress().trim().length() > 0) {
			inputContact.setEmailId(customerInfo.getEmailAddress().trim());
			logger.info("email id: "+customerInfo.getEmailAddress());
		}
		if(customerInfo.getFirstName() != null && customerInfo.getFirstName().trim().length() > 0) {
			inputContact.setFirstName(customerInfo.getFirstName().trim());
		}
		if(customerInfo.getLastName() != null && customerInfo.getLastName().trim().length() > 0) {
			inputContact.setLastName(customerInfo.getLastName().trim());
		}
		if(customerInfo.getAddressLine1() != null && customerInfo.getAddressLine1().trim().length() > 0) {
			inputContact.setAddressOne(customerInfo.getAddressLine1().trim());
		}
		if(customerInfo.getAddressLine2() != null && customerInfo.getAddressLine2().trim().length() > 0) {
			inputContact.setAddressTwo(customerInfo.getAddressLine2().trim());
		}
		if(customerInfo.getCity() != null && customerInfo.getCity().trim().length() > 0) {
			inputContact.setCity(customerInfo.getCity().trim());
		}
		if(customerInfo.getState() != null && customerInfo.getState().trim().length() > 0) {
			inputContact.setState(customerInfo.getState().trim());
		}
		if(customerInfo.getCountry() != null && customerInfo.getCountry().trim().length() > 0) {
			inputContact.setCountry(customerInfo.getCountry().trim());
		}
		if(customerInfo.getPostal() != null && customerInfo.getPostal().trim().length() > 0) {
			inputContact.setZip(customerInfo.getPostal().trim());
		}
		if(customerInfo.getBirthday() != null && customerInfo.getBirthday().trim().length() > 0) {
			Calendar cal = MyCalendar.dateString2Calendar(customerInfo.getBirthday().trim());
			inputContact.setBirthDay(cal);
		}
		if(customerInfo.getAnniversary() != null && customerInfo.getAnniversary().trim().length() > 0) {
			Calendar cal = MyCalendar.dateString2Calendar(customerInfo.getAnniversary().trim());
			inputContact.setAnniversary(cal);
		}
		if(customerInfo.getGender() != null && customerInfo.getGender().trim().length() > 0) {
			inputContact.setGender(customerInfo.getGender().trim());
		}	
		if( customerInfo.getPhone() != null && customerInfo.getPhone().trim().length() > 0) {
			inputContact.setMobilePhone(customerInfo.getPhone());
			logger.info("phone= "+customerInfo.getPhone());
		}
		logger.info("Exited prepareContactFromJsonData method >>>>>");
		return inputContact;
	}

	/**
	 * Checks whether given contact is exist in oc. It searches by external id, email id and mobile phone.
	 * If given contact is found in db, it returns db contact object.
	 * 
	 * @param jsonContact
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private List<Contacts> findOCContact(Contacts jsonContact, Long userId) throws Exception {
		//logger.info("Entered findOCContact method >>>>");
		POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(userId, Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
		List<Contacts> dbContactList = contactsDao.findMatchedContactListByUniqPriority(priorMap, jsonContact, userId);
		//logger.info("Exited findOCContact method >>>>");
		return dbContactList;
	}

	private List<ContactsLoyalty> findEnrollListByMobile(String mobile, Long userId) throws Exception {

		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByMobile(mobile, userId);
	}

	private ContactsLoyalty findContactLoyaltyByMobile(String mobile, Users user) throws Exception {
		Long userId=user.getUserId();
		String phoneNumber=LoyaltyProgramHelper.preparePhoneNumber(user,mobile);//APP-1208
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByPhone(Long.valueOf(phoneNumber), OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE, userId);
	}

	private Status validateEnteredValue(Amount amount){
		logger.info(" Entered into validateEnteredValue method >>>");
		Status status = null;
		try{
			double enteredValue = Double.valueOf(amount.getEnteredValue().trim());
			logger.info("enteredvalue = "+enteredValue);
			if(enteredValue <= 0){
				logger.info("enteredvalue less than 1");
				status = new Status("111557", PropertyUtil.getErrorMessage(111557, OCConstants.ERROR_LOYALTY_FLAG), 
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			if(!validFormat(amount.getEnteredValue().trim())) {
				status = new Status("111526", PropertyUtil.getErrorMessage(111526, OCConstants.ERROR_LOYALTY_FLAG), 
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}

		}catch(NumberFormatException ne){
			status = new Status("111526", PropertyUtil.getErrorMessage(111526, OCConstants.ERROR_LOYALTY_FLAG), 
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		catch(Exception e){
			logger.info("Entered value validation failed...");
			logger.error("Exception ::",e);
		}
		logger.info("Completed validateEnteredValue method <<<");
		return status;
	}

	
	private boolean validFormat(String s) {

		String[] value = s.split("\\.");
		if(value.length > 1) {
			if(value[1].length() <= 2) {
				return true;
			}
		}else {
			return true;
		}
		return false;
	}

	/**
	 * Fetches Users object from OC database
	 * 
	 * @param userName
	 * @param orgId
	 * @param userToken
	 * @return Users
	 * @throws Exception
	 */
	private Users getUser(String userName, String orgId, String userToken) throws Exception{

		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findUserByToken(completeUserName, userToken);
		return user;
	}

	/**
	 * Validates JSON objects in the request. 
	 * 
	 * @param returnTransactionRequest
	 * @return StatusInfo
	 * @throws Exception
	 */
	private Status validateReturnTransactionJsonData(RequestHeader header, Amount amount, String mode) throws Exception{
		
		logger.info("-- Enetred validateReturnTransactionJsonData --");
		Status status = null;
		
		//DocSID is not mandatory
		if(header.getDocSID() == null || header.getDocSID().trim().isEmpty()){
			status = new Status("111510", PropertyUtil.getErrorMessage(111510, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(header.getStoreNumber() == null || header.getStoreNumber().length() <= 0){
			status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if(amount == null || amount == null || amount.getType().trim().isEmpty()
				|| amount.getEnteredValue() == null || amount.getEnteredValue().trim().isEmpty()) {
			status = new Status("111534", PropertyUtil.getErrorMessage(111534, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(!amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_STORE_CREDIT) && 
				!amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)) {
			status = new Status("111580", PropertyUtil.getErrorMessage(111580, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		/*if(OCConstants.LOYALTY_OFFLINE_MODE.equals(mode) && amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL) && 
				returnTransactionRequest.getReturnItems() != null && returnTransactionRequest.getReturnItems().getOCLoyaltyItem() != null && returnTransactionRequest.getReturnItems().getOCLoyaltyItem().isEmpty()) {
			status = new Status("111510", PropertyUtil.getErrorMessage(111510, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/
		
		/*if(OCConstants.LOYALTY_ONLINE_MODE.equals(mode) && returnTransactionRequest.getAmount().getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL) && 
				returnTransactionRequest.getItems() != null  && returnTransactionRequest.getItems().isEmpty()) {
			status = new Status("111510", PropertyUtil.getErrorMessage(111510, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/
		if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_STORE_CREDIT) && 
				!amount.getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY)) {
			status = new Status("111526", PropertyUtil.getErrorMessage(111526, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.info("-- Exit validateReturnTransactionJsonData --");
		return status;
	}

	private LoyaltyReturnTransactionResponse prepareReturnTransactionResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance,BalancesAdditionalInfo additionalInfo,List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyReturnTransactionResponse returnTransactionResponse = new LoyaltyReturnTransactionResponse();
		returnTransactionResponse.setHeader(header);

		logger.info("-- Enetred prepareReturnTransactionResponse --");
		if(membershipResponse == null){
			membershipResponse = new MembershipResponse();
			membershipResponse.setCardNumber("");
			membershipResponse.setCardPin("");
			membershipResponse.setExpiry("");
			membershipResponse.setPhoneNumber("");
			membershipResponse.setTierLevel("");
			membershipResponse.setTierName("");
		}
		if(balances == null){
			balances = new ArrayList<Balance>();
		}
		if(holdBalance == null){
			holdBalance = new HoldBalance();
			holdBalance.setActivationPeriod("");
			holdBalance.setCurrency("");
			holdBalance.setPoints("");
		}
		
		if(additionalInfo == null){
			
			additionalInfo = new BalancesAdditionalInfo();
			Debit debit = new Debit();
			additionalInfo.setDebit(debit);
			
			List<Credit> credList = new ArrayList<Credit>();
			additionalInfo.setCredit(credList);
		}
		
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}

		returnTransactionResponse.setMembership(membershipResponse);
		returnTransactionResponse.setBalances(balances);
		returnTransactionResponse.setHoldBalance(holdBalance);
		returnTransactionResponse.setAdditionalInfo(additionalInfo);
		returnTransactionResponse.setMatchedCustomers(matchedCustomers);
		returnTransactionResponse.setStatus(status);
		logger.info("-- Exit prepareReturnTransactionResponse --");
		return returnTransactionResponse;
	}

	/*	private LoyaltyTransactionParent findTransactionByRequestId(String requestId) {
		LoyaltyTransactionParent loyaltyTransactionParent = null;
		LoyaltyTransactionParentDao loyaltyTransactionParentDao = null;
		try {
			loyaltyTransactionParentDao = (LoyaltyTransactionParentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			loyaltyTransactionParent = loyaltyTransactionParentDao.findByRequestId(requestId);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return loyaltyTransactionParent;
	}*/

	/*private LoyaltyProgramTier findTier(Long programId, Long contactId, Long userId, ContactsLoyalty contactsLoyalty) throws Exception {

		LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

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
		else if(contactId == null){
			logger.info("contactId is null and selected tier..."+tiersList.get(0).getTierType());
			return tiersList.get(0);
		}
		else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);

			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);				

			List<Map<String, Object>> contactPurcahseList = contactsDao.findContactPurchaseDetails(userId, contactId);
			Double totPurchaseValue = null;
			if(contactPurcahseList != null && contactPurcahseList.size() == 1) {
				for (Map<String, Object> eachMap : contactPurcahseList) {
					if(eachMap.containsKey("tot_purchase_amt")){
						totPurchaseValue = Double.valueOf(eachMap.get("tot_purchase_amt") != null ? eachMap.get("tot_purchase_amt").toString() : "0.00");
						logger.info("purchase value = "+totPurchaseValue);
					}
				}
			}

			if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){
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
					Calendar startCal = Calendar.getInstance();
					Calendar endCal = Calendar.getInstance();
					endCal.add(Calendar.MONTH, -((LoyaltyProgramTier) eligibleMap.get(nextKeyTier)).getTierUpgradeCumulativeValue().intValue());

					String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					logger.info("contactId = "+contactId+" startDate = "+startDate+" endDate = "+endDate);

					RetailProSalesDao salesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
					Object[] cumulativeAmountArr = salesDao.getCumulativePurchase(userId, contactId, startDate, endDate);

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
				}
				return tiersList.get(0);
			}catch(Exception e){
				logger.error("Excepion in cpv thread ", e);
				return tiersList.get(0);
			}
		}
		else{
			return null;
		}
	}*/
	
	private double getAutoConvertionReversalVal(ContactsLoyalty contactsLoyalty, SparkBaseLocationDetails sbDetails) {
		/* Amith Lulla: e.g. rule 100 points to $1
[12:53:27] Amith Lulla: sorry, you understood na?
[12:53:41] Amith Lulla: anyways...
[12:53:58] Amith Lulla: 550 points earned. converts to $5 and 50 points.
[12:54:11] Amith Lulla: if product worth returned and 350 points return
[12:54:25] proumya Acharya: autoconvertion pura rollback karna hein na?
[12:54:34] proumya Acharya: plz continue
[12:54:35] Amith Lulla: The calculator will convert $5 x 100 points + 50- points = 550 points
[12:54:45] Amith Lulla: -350 points= 250 points
[12:54:59] Amith Lulla: now bal =250 points = $2 and 50p */

		double unitAmtFactor = sbDetails.getConvertFromPoints()/sbDetails.getConvertToAmount();
		int multiple = (int)unitAmtFactor;
		double totConvertedPts = contactsLoyalty.getGiftcardBalance() * multiple;
		
		return totConvertedPts;
		//double subPoints = multiple * tier.getConvertFromPoints();
		
		
	}

private Double applyConversionRules(long returnPoints, SparkBaseLocationDetails sbDetails){
		
		//String[] differenceArr = null;

	Double convertedAmount = 0.0;
		try{
			
			if(sbDetails.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)){
				
				if(sbDetails.getConvertFromPoints() != null && sbDetails.getConvertFromPoints() > 0  ){
				
				//	differenceArr = new String[3];
					
					double multipledouble = (double)returnPoints/(double)sbDetails.getConvertFromPoints();
					String res = Utility.truncateUptoTwoDecimal((sbDetails.getConvertToAmount() * multipledouble));//(for earnedPointsValue pts ? amt)
					if(res != null)
					convertedAmount = Double.parseDouble(res);
					logger.debug("convertedAmount===="+convertedAmount);
					return convertedAmount;
					
					
					
				}
			}
		
		}catch(Exception e){
			logger.error("Exception while applying auto conversion rules...", e);
			return null;
		}
		return null;
	}
}
