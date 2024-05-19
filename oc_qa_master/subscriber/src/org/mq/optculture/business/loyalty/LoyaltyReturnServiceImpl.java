package org.mq.optculture.business.loyalty;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyMemberItemQtyCounter;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDao;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyMemberItemQtyCounterDao;
import org.mq.marketer.campaign.dao.LoyaltyMemberItemQtyCounterDaoforDML;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.common.BaseService;
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
import org.mq.optculture.data.dao.SpecialRewardsDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.Amount;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.BalancesAdditionalInfo;
import org.mq.optculture.model.ocloyalty.Credit;
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.Debit;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyOfflineReturnTransactionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionRequest;
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
import org.mq.optculture.model.updatecontacts.Header;
import org.mq.optculture.model.updatecontacts.User;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;


import com.google.gson.Gson;

public class LoyaltyReturnServiceImpl implements LoyaltyReturnTransactionOCService{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private static final String TOKEN_ITEM_WITH_OR="IWOR";
	private static final String TOKEN_ITEM_WITH_OUT_OR="IWOOR";
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
			Status status = new Status("101001", Constants.STRING_NILL+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);

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
	/**
	 * Handles the complete process of Loyalty return transaction.
	 * 
	 * @param returnTransactionRequest
	 * @return returnTransactionResponse
	 * @throws BaseServiceException
	 */
	@Override
	public LoyaltyReturnTransactionResponse processReturnTransactionRequest(LoyaltyReturnTransactionRequest returnTransactionRequest,
			String transactionId, String transactionDate, String requestJson, String mode, String loyaltyExtraction) throws BaseServiceException {
		


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
				!returnTransactionRequest.getHeader().getSourceType().trim().isEmpty() ? returnTransactionRequest.getHeader().getSourceType().trim() : Constants.STRING_NILL);
		/*responseHeader.setSubsidiaryNumber(returnTransactionRequest.getHeader().getSubsidiaryNumber() != null && !returnTransactionRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? returnTransactionRequest.getHeader().getSubsidiaryNumber().trim() : Constants.STRING_NILL);
		responseHeader.setReceiptNumber(returnTransactionRequest.getHeader().getReceiptNumber() != null && !returnTransactionRequest.getHeader().getReceiptNumber().trim().isEmpty() ? returnTransactionRequest.getHeader().getReceiptNumber().trim() : Constants.STRING_NILL);
		responseHeader.setReceiptAmount(Constants.STRING_NILL);*/
		
		try{

			/*if(returnTransactionRequest == null ){
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}*/
			status = validateReturnTransactionJsonData(returnTransactionRequest.getHeader(), returnTransactionRequest.getAmount(), mode);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}

			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}

			user = getUser(returnTransactionRequest.getUser().getUserName(), returnTransactionRequest.getUser().getOrganizationId(),
					returnTransactionRequest.getUser().getToken());
			if(user == null){
				status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}
			if(!user.isEnabled()){
				status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}
			Amount amount = returnTransactionRequest.getAmount();
			if(user.isEnableLoyaltyExtraction() && user.isReturnFromDR() && loyaltyExtraction == null && 
					!amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VOID) && !amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) ) {
				status = new Status("0", "Return will be done shortly.", OCConstants.JSON_RESPONSE_IGNORED_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;//Special Reward changes
			}
			/*if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VOID)){
				amount.setType(OCConstants.LOYALTY_TYPE_REVERSAL);
				returnTransactionRequest.setAmount(amount);
			}*/
			//by pravendra
			if(OCConstants.LOYALTY_SERVICE_TYPE_OC.equals(user.getloyaltyServicetype())){
				//updating subsidiary to request	
				if((returnTransactionRequest.getHeader().getSubsidiaryNumber() == null || 
						(returnTransactionRequest.getHeader().getSubsidiaryNumber().isEmpty()) ) && returnTransactionRequest.getHeader().getStoreNumber() != null && 
						!returnTransactionRequest.getHeader().getStoreNumber().isEmpty()){
					OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
					UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
					Long domainId = userDao.findDomainByUserId(user.getUserId());
					if(domainId!=null){
					OrganizationStores orgStores = organizationStoresDao.findOrgByDomain(user.getUserOrganization().getUserOrgId(), domainId, returnTransactionRequest.getHeader().getStoreNumber());
					returnTransactionRequest.getHeader().setSubsidiaryNumber(orgStores!=null ? orgStores.getSubsidiaryId() : null);
				  }
				}
			}
			
			//by proumya for SB return
			if(OCConstants.LOYALTY_SERVICE_TYPE_SB.equals(user.getloyaltyServicetype())){
				
				SparkBaseReturnService sbReturnService= (SparkBaseReturnService)ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_RETURN_TRANSACTION_SB_BUSINESS_SERVICE);
				returnTransactionResponse = sbReturnService.processReturnTransactionRequest(returnTransactionRequest, transactionId, transactionDate, requestJson, mode);
				return returnTransactionResponse;
			}
			//APP-4624
			List<SkuDetails> allReturnItems  = returnTransactionRequest.getItems();
			double actualReturnAmnt = 0.0;
			for(SkuDetails returnItem : allReturnItems) {
				actualReturnAmnt+= (Double.parseDouble(returnItem.getBilledUnitPrice()))*(Double.parseDouble(returnItem.getQuantity()));
			}
			logger.info("actualReturnAmnt : "+actualReturnAmnt);
			
			OriginalReceipt originalrec = returnTransactionRequest.getOriginalReceipt();
			//Return void reversal
			if(originalrec != null && ((originalrec.getDocSID() != null && !originalrec.getDocSID().trim().isEmpty()) 
					&& amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VOID))) {
				
				Gson gson = new Gson();
				LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_DAO);
				//LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				LoyaltyTransaction loyaltyTransactionIssuance = loyaltyTransactionDao.findRequestByDocSid(returnTransactionRequest.getUser().getUserName() + "__" +
						returnTransactionRequest.getUser().getOrganizationId(), originalrec.getDocSID().trim(),OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				LoyaltyTransaction loyaltyRedemptionTransaction = loyaltyTransactionDao.findRequestByDocSid(returnTransactionRequest.getUser().getUserName() + "__" +
						returnTransactionRequest.getUser().getOrganizationId(), originalrec.getDocSID().trim(),OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				
				
				//LoyaltyTransactionChild loyaltyRedemptionTransaction = loyaltyTransactionChildDao.findByDocSIDAndUserId(user.getUserId(), originalrec.getDocSID().trim(),OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);
				LoyaltyIssuanceRequest issuanceRequest = null; 
				LoyaltyRedemptionRequest redemptionRequest = null;
				
				if( loyaltyTransactionIssuance !=null) {
				issuanceRequest = gson.fromJson(loyaltyTransactionIssuance.getJsonRequest(), LoyaltyIssuanceRequest.class);	
				}
				if(loyaltyRedemptionTransaction !=null) {
				redemptionRequest = gson.fromJson(loyaltyRedemptionTransaction.getJsonRequest(), LoyaltyRedemptionRequest.class);
				}
				if(issuanceRequest!=null && issuanceRequest.getItems() != null) {
				returnTransactionRequest.setItems(issuanceRequest.getItems());
				}
				
				
				amount.setType(OCConstants.LOYALTY_TYPE_REVERSAL);
				amount.setRequestedType(OCConstants.LOYALTY_TYPE_VOID);
				if(redemptionRequest !=null && redemptionRequest.getAmount() != null && redemptionRequest.getAmount().getEnteredValue() != null) {
				amount.setEnteredValue(redemptionRequest.getAmount().getEnteredValue());
				returnTransactionRequest.setAmount(amount);
				returnTransactionRequest.setCreditRedeemedAmount(OCConstants.DECISION_FLAG_YES);
				
				}
			}
			Gson gson = new Gson();
			logger.info("returnTransactionRequest==>"+gson.toJson(returnTransactionRequest));
			
			 amount = returnTransactionRequest.getAmount();
			RequestHeader header = returnTransactionRequest.getHeader();
			//OriginalReceipt originalRecpt = returnTransactionRequest.getOriginalReceipt();
			//String creditRedeemedAmount = returnTransactionRequest.getCreditRedeemedAmount();
			List<SkuDetails> items = returnTransactionRequest.getItems();
			//ContactsLoyalty contactsLoyalty = null;
			String inquiry = amount.getType()!=null && amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) ? OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY : Constants.STRING_NILL;
			if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL) || 
					amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)) {
				//logger.debug("mode ==="+mode+" && returnTransactionRequest.getReturnItems()==="+returnTransactionRequest.getReturnItems());
				
				status = checkItemsEmpty(items );
				if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
				boolean isWithOR = findOR(returnTransactionRequest)	;//#TESTING
				logger.debug("isWithOR ===? "+isWithOR);
				if(!isWithOR) {
					
					return processReturnWithOutOR(returnTransactionRequest, responseHeader, user, items, true,actualReturnAmnt);
				}else{
					LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);					
					SpecialRewardsDao specialRewardsDao = (SpecialRewardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPECIAL_REWARDS_DAO);
					
					OriginalReceipt originalRecpt = findFullReversalBasedRecptOR(returnTransactionRequest);
					
					ContactsLoyalty contactsLoyalty = null;
					Contacts contact = null;
					//if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)){//check for the customer
					String cardNumber = returnTransactionRequest.getMembership().getCardNumber();
					if(cardNumber != null && !cardNumber.isEmpty()){ //card / mobile number
						
						contactsLoyalty = findContactLoyalty(cardNumber, user.getUserId());
						if(contactsLoyalty != null){
							contact = contactsLoyalty.getContact();
						}
					}else{
						
						List<POSMapping> contactPOSMap = null;
						POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
						contactPOSMap = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
						
						Contacts jsonContact = new Contacts();
						jsonContact.setUsers(user);				
						if(contactPOSMap != null){
							jsonContact = setContactFields(jsonContact, contactPOSMap, returnTransactionRequest,OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD);//#Card flow
						
							Contacts dbContact = findSingleOCContact(jsonContact, user.getUserId(),user);
							if(dbContact != null) contact = dbContact;
						}
						
						
					}
						
					//}//if
					if(contact == null){
						status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
						
					}
					
					if(originalRecpt != null) {//full recpt
						logger.debug("==found OR-lvel==");
						List<Credit> credList=null;//Flow change related, redemption reversal.
						String key=null;
						Double creditRewardCurrency = 0.0;
						List<SkuDetails> singleItemLst = returnTransactionRequest.getItems();
						if(user.isValidateItemsInReturnTrx()) {
							List<RetailProSalesCSV> retList = getItemsOfOR(originalRecpt , singleItemLst, user.getUserId() );
							 
							 String itemsStr = Constants.STRING_NILL;
							 String customMsg = Constants.STRING_NILL;
							 for (SkuDetails item : singleItemLst) {
								if(item.getItemSID() == null || item.getItemSID().isEmpty()) continue;
								 if(!itemsStr.isEmpty()) itemsStr+= Constants.DELIMETER_COMMA;
								 
								 itemsStr += item.getItemSID();
							}
							 
							 if( (retList == null || retList.isEmpty())) {
								 
								 customMsg += "could not find the Original trx with DOCSID-"+originalRecpt.getDocSID()+
										 (!itemsStr.isEmpty() ? (" and for the items : "+itemsStr+" \n") : Constants.STRING_NILL);
							 }
							 
							 if(! customMsg.isEmpty()) {
								 
								String msg = PropertyUtil.getErrorMessage(111608, OCConstants.ERROR_LOYALTY_FLAG)+" "+customMsg+".";
								status = new Status("111608", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
	 							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
	 							return returnTransactionResponse;
	 							        							        							
							}
							 List<SkuDetails> nonMatchingItemsWithOR = new ArrayList<SkuDetails>();
							 for (SkuDetails item : singleItemLst) {
								 if(item.getItemSID() == null || item.getItemSID().isEmpty()) continue;
								 boolean notFound = true;
								 for (RetailProSalesCSV retailProSalesCSV : retList) {
								 
									 if(retailProSalesCSV.getItemSid() != null && 
											 !retailProSalesCSV.getItemSid().equals(item.getItemSID())) {
										 
										 notFound =false;
										 break;
									 }
								 
								 }
								 if(notFound) nonMatchingItemsWithOR.add(item);
							 }
							 
							 if(!nonMatchingItemsWithOR.isEmpty()){
								 
								 String NonitemsStr = Constants.STRING_NILL;
								 for (SkuDetails item : nonMatchingItemsWithOR) {
									
									 if(!NonitemsStr.isEmpty()) NonitemsStr+= Constants.DELIMETER_COMMA;
									 
									 NonitemsStr += item.getItemSID();
								}
								 
								 customMsg += "The items : "+itemsStr+" are not matching with Original "
								 		+ "transaction with DOCSID-"+originalRecpt.getDocSID()+" \n" ;
							 }
							 
							 if(! customMsg.isEmpty()) {
								 
								 
								String msg = PropertyUtil.getErrorMessage(111606, OCConstants.ERROR_LOYALTY_FLAG)+" "+customMsg+".";
								status = new Status("111606", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
								return returnTransactionResponse;
								        							        							
							 }
						}
						 
						 boolean doRedemptionReversal = amount.getType().equals(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) ? true :
							 (amount.getType().equals(OCConstants.LOYALTY_TYPE_REVERSAL) && amount.getEnteredValue()!= null && 
							 !amount.getEnteredValue().trim().isEmpty() ? true : false);
						 List<LoyaltyTransactionChild> redempTransList  = null;
						 boolean stillGiveSuccessResponse = doRedemptionReversal && Double.parseDouble(amount.getEnteredValue().trim())<=0;
						 
						//if(amount.getType().equals(OCConstants.LOYALTY_TYPE_REVERSAL)) doRedemptionReversal = doRedemptionReversal && Double.parseDouble(amount.getEnteredValue().trim()) > 0;//APP-4592
						List<LoyaltyTransactionChild> issTransList = loyaltyTransactionChildDao.findByDocSID(originalRecpt.getDocSID(),//Time 
								user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE, originalRecpt.getReceiptNumber(),originalRecpt.getStoreNumber(),originalRecpt.getSubsidiaryNumber());
					
					List<LoyaltyTransactionChild> rewardIssuList = loyaltyTransactionChildDao.findSpecialRewardByDocSID(originalRecpt.getDocSID(),//Time 
						      user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE, originalRecpt.getReceiptNumber(),originalRecpt.getStoreNumber(),originalRecpt.getSubsidiaryNumber());
					
						if(doRedemptionReversal && user.isPerformRedeemedAmountReversal()) {//APP-4792
							String DocSID = issTransList != null && !issTransList.isEmpty() ? issTransList.get(0).getDocSID() : (rewardIssuList != null && !issTransList.isEmpty() ? rewardIssuList.get(0).getDocSID() : originalRecpt.getDocSID());
							
							 redempTransList = loyaltyTransactionChildDao.findByDocSID(DocSID,
									 user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION, originalRecpt.getReceiptNumber(),originalRecpt.getStoreNumber(),originalRecpt.getSubsidiaryNumber());
							
						}
						if(redempTransList != null) {
							logger.debug("==redempTransList=="+redempTransList.size());
							
							String customMsg = Constants.STRING_NILL;
							boolean customerMisMatch = false;
							String docSID = Constants.STRING_NILL;
							String customerSID = returnTransactionRequest.getCustomer().getCustomerId();
							for (LoyaltyTransactionChild loyaltyTransactionChild : redempTransList) {
								if(customerSID == null || customerSID.isEmpty()) continue;
								docSID = loyaltyTransactionChild.getDocSID();
								ContactsLoyalty childcontactsLoyalty = getMembership(loyaltyTransactionChild);
								
								if(childcontactsLoyalty != null && 
										(childcontactsLoyalty.getContact().getContactId().longValue() != contact.getContactId().longValue())
										//!contactsLoyalty.getCardNumber().equals(returnTransactionRequest.getMembership().getCardNumber()) ||
										) {
									customerMisMatch = true;
									
									
								}//if
							}//for
							if(customerMisMatch){
								
								customMsg = " Customer mismatch for the Receipt with DOCSID-"+docSID;//+" for the returned items-"+itemsStr;
								String msg = PropertyUtil.getErrorMessage(111607, OCConstants.ERROR_LOYALTY_FLAG)+" "+customMsg+".";
								status = new Status("111607", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
    							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
    							return returnTransactionResponse;
							}
							
							returnTransactionResponse = performRedemptnBasedReversal(issTransList, redempTransList, amount, header, amount.getType(),returnTransactionRequest.getItems(), originalRecpt, OCConstants.DECISION_FLAG_YES, responseHeader, requestJson, 
										 user, null, null, null, null, null, false,
										 PropertyUtil.getErrorMessage(0, OCConstants.ERROR_LOYALTY_FLAG),0);
							credList=returnTransactionResponse.getAdditionalInfo().getCredit();//Null condition check is not required because of prepareReturnTransactionResponse.
							creditRewardCurrency += getCreditRewardCurrency(credList);
						
						}
						
						
						boolean isFullRcpt = isFullRcpt(issTransList,rewardIssuList, returnTransactionRequest.getItems() );
						logger.info("isFullRcpt==="+isFullRcpt);
						if(issTransList != null || rewardIssuList != null) {
							String customMsg = Constants.STRING_NILL;
							boolean customerMisMatch = false;
							String originaldocSID = Constants.STRING_NILL;
							LoyaltyTransactionChild loyaltyTransactionChild = issTransList != null ? issTransList.get(0) : rewardIssuList.get(0);
							originaldocSID = loyaltyTransactionChild.getDocSID();
							ContactsLoyalty childcontactsLoyalty = getMembership(loyaltyTransactionChild);
							if(childcontactsLoyalty != null && 
									(childcontactsLoyalty.getContact().getContactId().longValue() != contact.getContactId().longValue())
									//!contactsLoyalty.getCardNumber().equals(returnTransactionRequest.getMembership().getCardNumber()) ||
									) {
								customerMisMatch = true;
								logger.debug(childcontactsLoyalty.getContact().getContactId().longValue()+" != "+contact.getContactId().longValue());
								
							}//if
							if(customerMisMatch){
								
								customMsg = " Customer mismatch for the Receipt with DOCSID-"+originaldocSID;//+" for the returned items-"+itemsStr;
								String msg = PropertyUtil.getErrorMessage(111607, OCConstants.ERROR_LOYALTY_FLAG)+" "+customMsg+".";
								status = new Status("111607", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
    							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
    							return returnTransactionResponse;
							}
							
							
							if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)){
								
								if(contactsLoyalty != null && 
										contactsLoyalty.getLoyaltyId().longValue() == childcontactsLoyalty.getLoyaltyId().longValue()) {
									
									returnTransactionResponse = performIssuanceBasedReversal(issTransList != null ? issTransList.get(0) : null, header,
											amount.getType(), originalRecpt, singleItemLst, responseHeader, requestJson, user,
											mode,key, contactsLoyalty, amount, rewardIssuList, isFullRcpt, creditRewardCurrency,actualReturnAmnt);
								}else{
									returnTransactionResponse = performIssuanceBasedReversal(issTransList != null ? issTransList.get(0) : null, header,
											amount.getType(), originalRecpt, singleItemLst, responseHeader, requestJson, user, mode,key, 
											childcontactsLoyalty,amount, rewardIssuList, isFullRcpt, creditRewardCurrency,actualReturnAmnt);
								}
								
							}else{
								returnTransactionResponse = performIssuanceBasedReversal(loyaltyTransactionChild, header,
										amount.getType(), originalRecpt, singleItemLst, responseHeader, 
										requestJson, user, mode,key, childcontactsLoyalty,amount, rewardIssuList, isFullRcpt, creditRewardCurrency,actualReturnAmnt);
							}
							BalancesAdditionalInfo additionalInfo=returnTransactionResponse.getAdditionalInfo();
							additionalInfo.setCredit(credList);
							returnTransactionResponse.setAdditionalInfo(additionalInfo);//As credList was the only thing missed, prepared the object here instead of the changing the flow upside down.
							//return returnTransactionResponse;
						}else{
							if((issTransList == null || issTransList.isEmpty()) && (rewardIssuList == null || rewardIssuList.isEmpty()))
							returnTransactionResponse = processReturnWithOutOR(returnTransactionRequest, responseHeader, user, singleItemLst, true,actualReturnAmnt);
						}
						
						if(returnTransactionResponse != null){
							return returnTransactionResponse;
						}else {
							if(stillGiveSuccessResponse) {
								status = new Status("0", "Return "+inquiry+" was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
								return returnTransactionResponse;
							}else{
								
								status = new Status("111566", PropertyUtil.getErrorMessage(111566, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
								return returnTransactionResponse;
							}
						}
					}else{
						logger.debug("===found Item-Level OR===");
						Map<String,List<SkuDetails>> itemLevelORMap = findItemLevelOR(returnTransactionRequest);
						List<SkuDetails> itemsWithOR = itemLevelORMap.get(TOKEN_ITEM_WITH_OR);
						List<SkuDetails> itemsWithOutOR = itemLevelORMap.get(TOKEN_ITEM_WITH_OUT_OR);
						logger.debug("===found Item-Level OR==="+itemsWithOR.size());
						logger.debug("===found Item-Level OR==="+itemsWithOutOR.size());
						Double creditRewardCurrency = 0.0;
						Double debitRewardCurrency = 0.0;
						Double remainderToDebit = 0.0;
						//List<Credit> CreditList = new ArrayList<Credit>();
						//ContactsLoyalty contactsLoyalty = null;
						if(!itemsWithOR.isEmpty()){
							String customMsg = Constants.STRING_NILL;
							
							
							Set<List<SkuDetails>> sameORSet = findSameORItemSet(itemsWithOR);// 
							
							logger.debug("same OR items size ===>"+sameORSet.size());
							for (List<SkuDetails> singleItemLst : sameORSet) {
								
								List<Credit> credList=null;//Flow change related, redemption reversal.
								String key=null;
								originalRecpt = singleItemLst.get(0).getOriginalReceipt();//itemLevelORMap.get(skuDetails);
								if(user.isValidateItemsInReturnTrx()) {
									
									 List<RetailProSalesCSV> retList = getItemsOfOR(originalRecpt , singleItemLst, user.getUserId() );
									 
									 String itemsStr = Constants.STRING_NILL;
									 for (SkuDetails item : singleItemLst) {
										 
										if(item.getItemSID() == null || item.getItemSID().isEmpty() ) continue;
										 if(!itemsStr.isEmpty()) itemsStr+= Constants.DELIMETER_COMMA;
										 
										 itemsStr += item.getItemSID();
									}
									 
									 if( (retList == null || retList.isEmpty())) {
										 
										 customMsg += "could not find the Original trx with DOCSID-"+originalRecpt.getDocSID()+
												 (!itemsStr.isEmpty() ? (" and for the items : "+itemsStr+" \n") : Constants.STRING_NILL);
									 }
									 
									 if(! customMsg.isEmpty()) {
										 
										 
											String msg = PropertyUtil.getErrorMessage(111608, OCConstants.ERROR_LOYALTY_FLAG)+" "+customMsg+".";
											status = new Status("111608", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		        							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
		        							return returnTransactionResponse;
		        							        							        							
									}
									 List<SkuDetails> nonMatchingItemsWithOR = new ArrayList<SkuDetails>();
									 for (SkuDetails item : singleItemLst) {
										 if(item.getItemSID() == null || item.getItemSID().isEmpty() ) continue;
										 boolean notFound = true;
										 for (RetailProSalesCSV retailProSalesCSV : retList) {
										 
											 if(retailProSalesCSV.getItemSid() != null && 
													 !retailProSalesCSV.getItemSid().equals(item.getItemSID())) {
												 
												 notFound =false;
												 break;
											 }
										 
										 }
										 if(notFound) nonMatchingItemsWithOR.add(item);
									 }
									 
									 if(!nonMatchingItemsWithOR.isEmpty()){
										 
										 String NonitemsStr = Constants.STRING_NILL;
										 for (SkuDetails item : nonMatchingItemsWithOR) {
											
											 if(!NonitemsStr.isEmpty()) NonitemsStr+= Constants.DELIMETER_COMMA;
											 
											 NonitemsStr += item.getItemSID();
										}
										 
										 customMsg += "The items : "+itemsStr+" are not matching with Original "
										 		+ "transaction with DOCSID-"+originalRecpt.getDocSID()+" \n" ;
									 }
									 
									 if(! customMsg.isEmpty()) {
										 
										 
										String msg = PropertyUtil.getErrorMessage(111606, OCConstants.ERROR_LOYALTY_FLAG)+" "+customMsg+".";
										status = new Status("111606", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
	        							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
	        							return returnTransactionResponse;
	        							        							        							
									 }
								}
								 
								
								 boolean doRedemptionReversal = amount.getType().equals(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) ? true :
									 (amount.getType().equals(OCConstants.LOYALTY_TYPE_REVERSAL) && amount.getEnteredValue()!= null && 
									 !amount.getEnteredValue().trim().isEmpty() ? true : false);
								 List<LoyaltyTransactionChild> redempTransList  = null;
								 
								 List<LoyaltyTransactionChild> issTransList = loyaltyTransactionChildDao.findByDocSID(originalRecpt.getDocSID(),//Time 
									      user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE, originalRecpt.getReceiptNumber(),originalRecpt.getStoreNumber(),originalRecpt.getSubsidiaryNumber());

								List<LoyaltyTransactionChild> rewardIssuList = loyaltyTransactionChildDao.findSpecialRewardBy(originalRecpt.getDocSID(),//Time 
								user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE, originalRecpt.getReceiptNumber(),originalRecpt.getStoreNumber(),originalRecpt.getSubsidiaryNumber());

								 if(doRedemptionReversal && user.isPerformRedeemedAmountReversal()) {//APP-4792
									 String DocSID = issTransList != null && !issTransList.isEmpty() ? issTransList.get(0).getDocSID() : (rewardIssuList != null && !rewardIssuList.isEmpty() ? rewardIssuList.get(0).getDocSID() : originalRecpt.getDocSID());
										
									 redempTransList = loyaltyTransactionChildDao.findByDocSID(DocSID,
											 user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION, originalRecpt.getReceiptNumber(),originalRecpt.getStoreNumber(),originalRecpt.getSubsidiaryNumber());
								 }
								if(redempTransList != null) {
									customMsg = Constants.STRING_NILL;
									boolean customerMisMatch = false;
									String docSID = Constants.STRING_NILL;
									String customerSID = returnTransactionRequest.getCustomer().getCustomerId();
									for (LoyaltyTransactionChild loyaltyTransactionChild : redempTransList) {
										if(customerSID == null || customerSID.isEmpty()) continue;
										docSID = loyaltyTransactionChild.getDocSID();
										//if(contactsLoyalty == null) contactsLoyalty = getMembership(loyaltyTransactionChild);
										ContactsLoyalty childcontactsLoyalty = getMembership(loyaltyTransactionChild);
										if(childcontactsLoyalty != null && 
											(childcontactsLoyalty.getContact().getContactId().longValue() 
													!= contact.getContactId().longValue())
												) {
											customerMisMatch = true;
											logger.debug(childcontactsLoyalty.getContact().getContactId().longValue()+" != "+contact.getContactId().longValue());
										}//if
									}//for
									if(customerMisMatch){
										
										customMsg = " Customer mismatch for the Receipt with DOCSID-"+docSID;
										String msg = PropertyUtil.getErrorMessage(111607, OCConstants.ERROR_LOYALTY_FLAG)+" "+customMsg+".";
										status = new Status("111607", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
	        							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
	        							return returnTransactionResponse;
									}
									
									returnTransactionResponse = performRedemptnBasedReversal(issTransList,redempTransList, amount, header, amount.getType(),singleItemLst, originalRecpt, OCConstants.DECISION_FLAG_YES, responseHeader, requestJson, 
												 user, null, null, null, null, null, false,
												 PropertyUtil.getErrorMessage(0, OCConstants.ERROR_LOYALTY_FLAG),0);
									credList=returnTransactionResponse.getAdditionalInfo().getCredit();//Null condition check is not required because of prepareReturnTransactionResponse.
									creditRewardCurrency += getCreditRewardCurrency(credList);
								}
								
								
								if(issTransList != null || rewardIssuList !=null) {
									 
									customMsg = Constants.STRING_NILL;
									boolean customerMisMatch = false;
									String originaldocSID = Constants.STRING_NILL;
									LoyaltyTransactionChild loyaltyTransactionChild = issTransList != null ? issTransList.get(0): rewardIssuList.get(0);
									originaldocSID = loyaltyTransactionChild.getDocSID();
									//if(contactsLoyalty == null) contactsLoyalty = getMembership(loyaltyTransactionChild);
									ContactsLoyalty childcontactsLoyalty = getMembership(loyaltyTransactionChild);
									if(childcontactsLoyalty != null && 
											(childcontactsLoyalty.getContact().getContactId().longValue() != contact.getContactId().longValue())
											//!contactsLoyalty.getCardNumber().equals(returnTransactionRequest.getMembership().getCardNumber()) ||
											) {
										customerMisMatch = true;
										
										
									}//if
									if(customerMisMatch){
										logger.debug("Fromm here====");
										customMsg = " Customer mismatch for the Receipt with DOCSID-"+originaldocSID;//+" for the returned items-"+itemsStr;
										String msg = PropertyUtil.getErrorMessage(111607, OCConstants.ERROR_LOYALTY_FLAG)+" "+customMsg+".";
										status = new Status("111607", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
	        							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
	        							return returnTransactionResponse;
									}
									/*key=genKey(issTransList.get(0).getDocSID(),issTransList.get(0).getReceiptNumber(),issTransList.get(0).getStoreNumber(), issTransList.get(0).getSubsidiaryNumber());
									logger.info("issTransList.get(0).getLoyaltyId()==>"+issTransList.get(0).getLoyaltyId());
									String docSID = "OR-"+originalRecpt.getDocSID();
									logger.info("key==>"+key);
									List<LoyaltyTransactionChild> returnList = loyaltyTransactionChildDao.getTotReversalAmt(user.getUserId(), 
											docSID, OCConstants.LOYALTY_TRANS_TYPE_RETURN,key);
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
	        						}*/
									if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)){
										
										if(contactsLoyalty != null && 
												contactsLoyalty.getLoyaltyId().longValue() == childcontactsLoyalty.getLoyaltyId().longValue()) {
											
											returnTransactionResponse = performIssuanceBasedReversal(loyaltyTransactionChild, header,
													amount.getType(), originalRecpt, singleItemLst, responseHeader, requestJson, user,
													mode,key, contactsLoyalty, amount,rewardIssuList, false, creditRewardCurrency,actualReturnAmnt);
										}else{
											returnTransactionResponse = performIssuanceBasedReversal(loyaltyTransactionChild, header,
													amount.getType(), originalRecpt, singleItemLst, responseHeader, requestJson, user,
													mode,key, childcontactsLoyalty, amount,
													rewardIssuList, false, creditRewardCurrency,actualReturnAmnt);
										}
										
									}else{
										returnTransactionResponse = performIssuanceBasedReversal(loyaltyTransactionChild, header,
												amount.getType(), originalRecpt, singleItemLst, responseHeader, requestJson, user,
												 mode,key, childcontactsLoyalty, amount,rewardIssuList, false, creditRewardCurrency,actualReturnAmnt);
									}
									BalancesAdditionalInfo additionalInfo=returnTransactionResponse.getAdditionalInfo();
									additionalInfo.setCredit(credList);
									returnTransactionResponse.setAdditionalInfo(additionalInfo);//As credList was the only thing missed, prepared the object here instead of the changing the flow upside down.
									String debitCurrency = additionalInfo.getDebit().getRewardCurrency();
									String remainderTodebit = additionalInfo.getDebit().getRemainderToDebit();
									
									debitRewardCurrency += debitCurrency!=null && !debitCurrency.isEmpty() ? Double.parseDouble(debitCurrency): 0.0;
									remainderToDebit += remainderTodebit != null && !remainderTodebit.isEmpty() ? Double.parseDouble(remainderTodebit) : 0.0;
									//return returnTransactionResponse;
								}else{
									if((issTransList == null || issTransList.isEmpty()) && (rewardIssuList == null || rewardIssuList.isEmpty()))
									returnTransactionResponse = processReturnWithOutOR(returnTransactionRequest, responseHeader, user, singleItemLst, true,actualReturnAmnt);
								}
								
							}//for
							
							
						}//if
						if(!itemsWithOutOR.isEmpty()) {
							
							returnTransactionResponse =  processReturnWithOutOR(returnTransactionRequest, responseHeader, user, itemsWithOutOR, false,actualReturnAmnt);
							//responseList.add(returnTransactionResponse);
							String debitCurrency = returnTransactionResponse.getAdditionalInfo().getDebit().getRewardCurrency();
							String remainderTodebit = returnTransactionResponse.getAdditionalInfo().getDebit().getRemainderToDebit();
							debitRewardCurrency += debitCurrency!=null && !debitCurrency.isEmpty() ? Double.parseDouble(debitCurrency): 0.0;
							remainderToDebit += remainderTodebit != null && !remainderTodebit.isEmpty() ? Double.parseDouble(remainderTodebit) : 0.0;
						}
						if(returnTransactionResponse!=null ) {
							
							List<Credit> creditList = returnTransactionResponse.getAdditionalInfo().getCredit();
							if(creditList != null && !creditList.isEmpty()){
								Credit creditReward = creditList.get(0);
								creditReward.setRewardCurrency(Utility.truncateUptoTwoDecimal(creditRewardCurrency));//creditRewardCurrency+Constants.STRING_NILL);
								creditList.clear();
								creditList.add(creditReward);
								BalancesAdditionalInfo additionalInfo = returnTransactionResponse.getAdditionalInfo();
								additionalInfo.setCredit(creditList);
								returnTransactionResponse.setAdditionalInfo(additionalInfo);
							}
							Debit finalDebit = returnTransactionResponse.getAdditionalInfo().getDebit();
							if(finalDebit != null){
								logger.debug("finalDebit ==="+remainderToDebit);
								finalDebit.setRemainderToDebit(Utility.truncateUptoTwoDecimal(remainderToDebit));
								finalDebit.setRewardCurrency(Utility.truncateUptoTwoDecimal(debitRewardCurrency));
								returnTransactionResponse.getAdditionalInfo().setDebit(finalDebit);
							}
							
							return returnTransactionResponse; //As we are not immediately returning after redemption reversal.
						}else {
							status = new Status("111566", PropertyUtil.getErrorMessage(111566, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						}
					
						
						
					
					}//else item-level
					
					
					
					
					
					
				}//else (item-level OR)
			}//end reversal	
			else if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_STORE_CREDIT)){

				ContactsLoyalty contactsLoyalty = null;
				OriginalReceipt originalRecpt = returnTransactionRequest.getOriginalReceipt();
				if(returnTransactionRequest.getMembership().getCardNumber() != null 
						&& returnTransactionRequest.getMembership().getCardNumber().trim().length() > 0){

					logger.info("Return transaction by card number >>>");

					String cardNumber = Constants.STRING_NILL;
					String cardLong = null;

					cardLong = OptCultureUtils.validateOCLtyCardNumber(returnTransactionRequest.getMembership().getCardNumber().trim());
					if(cardLong == null){
						String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getMembership().getCardNumber().trim()+".";
						status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}
					cardNumber = Constants.STRING_NILL+cardLong;
					returnTransactionRequest.getMembership().setCardNumber(cardNumber);

					logger.info("Card Number after parsing... "+cardNumber);

					return cardBasedReturnTransaction(returnTransactionRequest, header, amount, originalRecpt, cardNumber, responseHeader, user, requestJson);
				}
				else if(returnTransactionRequest.getMembership().getPhoneNumber() != null 
						&& returnTransactionRequest.getMembership().getPhoneNumber().trim().length() > 0){

					String validStatus = LoyaltyProgramHelper.validateMembershipMobile(returnTransactionRequest.getMembership().getPhoneNumber().trim());
					if(OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID.equals(validStatus)){
						String msg = PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getMembership().getPhoneNumber().trim();
						status = new Status("111554", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}

					contactsLoyalty = findContactLoyaltyByMobile(returnTransactionRequest.getMembership().getPhoneNumber().trim(), user);
					if(contactsLoyalty == null){
						status = new Status("111519", PropertyUtil.getErrorMessage(111519, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getMembership().getPhoneNumber().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}

					return mobileBasedReturnTransaction(returnTransactionRequest, contactsLoyalty, responseHeader, header, amount, originalRecpt,  user, requestJson);

				}
				else if(returnTransactionRequest.getCustomer().getPhone() != null 
						&& !returnTransactionRequest.getCustomer().getPhone().trim().isEmpty()){

					List<ContactsLoyalty> enrollList = findEnrollListByMobile(returnTransactionRequest.getCustomer().getPhone(), user.getUserId());

					if(enrollList == null){
						status = new Status("111524", PropertyUtil.getErrorMessage(111524, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}

					List<Contacts> dbContactList = null;
					Contacts dbContact = null;

					if(enrollList.size() > 1){
						logger.info("Found more than 1 enrollments");
						Contacts jsonContact = prepareContactFromJsonData(returnTransactionRequest.getCustomer(), user.getUserId());
						jsonContact.setUsers(user);
						dbContactList = findOCContact(jsonContact, user.getUserId(),user);

						if(dbContactList == null){
							logger.info(" request contact not found in OC");

							List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);

							status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null,null, matchedCustomers, status);
							return returnTransactionResponse;
						}
						else if(dbContactList.size() == 1){
							logger.info("else case..enrollList ..."+enrollList.size());
							dbContact = dbContactList.get(0);
							logger.info("dbcontact cid == "+dbContact.getContactId());
							Iterator<ContactsLoyalty> iterList = enrollList.iterator();
							ContactsLoyalty loyalty = null;
							int count = 0;
							while(iterList.hasNext()){
								loyalty = iterList.next();
								logger.info(" enrollist cid.."+loyalty.getContact().getContactId());
								if(loyalty.getContact() != null && loyalty.getContact().getContactId() != null 
										&& loyalty.getContact().getContactId().longValue() == dbContact.getContactId().longValue()){
									if(contactsLoyalty == null)	contactsLoyalty = loyalty;
									count++;
									logger.info("loyalty found in more than one enrollment case...");
								}
							}
							if(count > 1){
								contactsLoyalty = null;
							}
							if(contactsLoyalty == null){

								List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);

								status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, matchedCustomers, status);
								return returnTransactionResponse;
							}
						}
						else{
							List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);

							status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null,null, matchedCustomers, status);
							return returnTransactionResponse;
						}

					}
					else{
						contactsLoyalty = enrollList.get(0);
					}
					logger.info("contactsLoyalty = "+contactsLoyalty);

					if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(contactsLoyalty.getMembershipType())){
						return mobileBasedReturnTransaction(returnTransactionRequest, contactsLoyalty, responseHeader, header, amount, originalRecpt, user, requestJson);
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
						return cardBasedReturnTransaction(returnTransactionRequest, header, amount, originalRecpt, Constants.STRING_NILL+contactsLoyalty.getCardNumber(), responseHeader, user, requestJson);
					}
					else{
						logger.info("INVALID LOYALTY MEMBERSHIP CARD TYPE .... LOOKINTO THIS...");
					}

				}
				else{
					status = new Status("111523", PropertyUtil.getErrorMessage(111523, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null,null, null, status);
					return returnTransactionResponse;
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
		logger.info("processReturnTransactionRequest ends");
		return returnTransactionResponse;
		
		
		
	
		
	}

		
	
	
	
	
	
	
	/**
	 * Handles the complete process of Loyalty return transaction.
	 * 
	 * @param returnTransactionRequest
	 * @return returnTransactionResponse
	 * @throws BaseServiceException
	 */
	@Override
	public LoyaltyReturnTransactionResponse processOfflineReturnTransactionRequest(LoyaltyOfflineReturnTransactionRequest returnOfflineTransactionRequest,
			String transactionId, String transactionDate, String requestJson, String mode) throws BaseServiceException {

		return null;
		/*
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;
		Users user = null;

		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(returnOfflineTransactionRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(returnOfflineTransactionRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		responseHeader.setSourceType(returnOfflineTransactionRequest.getHeader().getSourceType() != null && 
				!returnOfflineTransactionRequest.getHeader().getSourceType().trim().isEmpty() ? returnOfflineTransactionRequest.getHeader().getSourceType().trim() : Constants.STRING_NILL);
		responseHeader.setSubsidiaryNumber(returnOfflineTransactionRequest.getHeader().getSubsidiaryNumber() != null && !returnOfflineTransactionRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? returnOfflineTransactionRequest.getHeader().getSubsidiaryNumber().trim() : Constants.STRING_NILL);
		responseHeader.setReceiptNumber(returnOfflineTransactionRequest.getHeader().getReceiptNumber() != null && !returnOfflineTransactionRequest.getHeader().getReceiptNumber().trim().isEmpty() ? returnOfflineTransactionRequest.getHeader().getReceiptNumber().trim() : Constants.STRING_NILL);
		responseHeader.setReceiptAmount(Constants.STRING_NILL);
		
		
		try{

			if(returnOfflineTransactionRequest == null ){
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}
			status = validateReturnTransactionJsonData(returnOfflineTransactionRequest.getHeader(), returnOfflineTransactionRequest.getAmount(), mode);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}

			//status = validateEnteredValue(returnOfflineTransactionRequest.getAmount());
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}
			LoyaltyUser ltyUser = returnOfflineTransactionRequest.getUser();
			user = getUser(ltyUser.getUserName(), ltyUser.getOrganizationId(),
					ltyUser.getToken());
			if(user == null){
				status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}
			if(!user.isEnabled()){
				status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}

			RequestHeader header = returnOfflineTransactionRequest.getHeader();
			Amount amount = returnOfflineTransactionRequest.getAmount();
			OriginalReceipt originalRecept = returnOfflineTransactionRequest.getOriginalReceipt();
			String creditRedeemedAmount = returnOfflineTransactionRequest.getCreditRedeemedAmount();
			List<SkuDetails> items = returnOfflineTransactionRequest.getItems().getOCLoyaltyItem();
			
			
			if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)) {
				//logger.debug("mode ==="+mode+" && returnTransactionRequest.getReturnItems()==="+returnTransactionRequest.getReturnItems());
				status = checkItemsEmpty(returnOfflineTransactionRequest.getItems().getOCLoyaltyItem() );
				if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
				
				//original receipt structure should not check 
				if(originalRecept == null || 
						originalRecept.getDocSID() == null || 
								originalRecept.getDocSID().trim().isEmpty()) {
					
					if((returnOfflineTransactionRequest.getMembership() == null || 
							returnOfflineTransactionRequest.getMembership().getCardNumber() == null 
							|| returnOfflineTransactionRequest.getMembership().getCardNumber().trim().isEmpty()) && 
							(returnOfflineTransactionRequest.getCustomer() == null || returnOfflineTransactionRequest.getCustomer().getPhone() == null ||
									returnOfflineTransactionRequest.getCustomer().getPhone().isEmpty()) ){
						
						//when original receipt & membership details not found need a error code
						status = new Status("111564", PropertyUtil.getErrorMessage(111564, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}
					
				}
				
				if(originalRecept != null && 
						originalRecept.getDocSID() != null && 
						!originalRecept.getDocSID().trim().isEmpty()) {
					List<Credit> credList=null;//Flow change related, redemption reversal.
					
					LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
					List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(originalRecept.getDocSID(), 
							user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,null,null,null);
					if(redempTransList != null) {
					
						returnTransactionResponse = performRedemptnBasedReversal(redempTransList, header, amount.getType(), originalRecept, creditRedeemedAmount,	responseHeader, requestJson, 
									 user, null, null, null, null, null, false,
									 PropertyUtil.getErrorMessage(0, OCConstants.ERROR_LOYALTY_FLAG), 0);//Changed from true to false for issuanceFailed as RedemptionReversal is happening before IssuanceReversal now.
						credList=returnTransactionResponse.getAdditionalInfo().getCredit();//Null condition check is not required because of prepareReturnTransactionResponse.
						//return returnTransactionResponse;
					}
					
					
					List<LoyaltyTransactionChild> issTransList = loyaltyTransactionChildDao.findByDocSID(originalRecept.getDocSID(), 
																								      user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,null,null,null);
					if(issTransList != null) {
						//check for the sufficient balance
						String docSID = "OR-"+originalRecept.getDocSID();
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
						returnTransactionResponse = performIssuanceBasedReversal(loyaltyTransactionChild, header, amount, originalRecept, creditRedeemedAmount,items,
								responseHeader, requestJson, user,diffAmt, mode,null);
						BalancesAdditionalInfo additionalInfo=returnTransactionResponse.getAdditionalInfo();
						additionalInfo.setCredit(credList);
						returnTransactionResponse.setAdditionalInfo(additionalInfo);//As credList was the only thing missed, prepared the object here instead of the changing the flow upside down.
						return returnTransactionResponse;
					}
					if(returnTransactionResponse!=null)
						return returnTransactionResponse; //As we are not immediately returning after redemption reversal.

					
					
					else {
					status = new Status("111566", PropertyUtil.getErrorMessage(111566, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					}
					
				}else {
					
					LoyaltyProgram loyaltyProgram = null;
					ContactsLoyalty contactsLoyalty = null;
					LoyaltyCards loyaltyCard = null;
					
					if( (returnOfflineTransactionRequest.getMembership().getCardNumber() != null 
								&& returnOfflineTransactionRequest.getMembership().getCardNumber().trim().length() > 0) ) {
						String cardNumber = Constants.STRING_NILL;
						String cardLong = null;
	
						cardLong = OptCultureUtils.validateOCLtyCardNumber(returnOfflineTransactionRequest.getMembership().getCardNumber().trim());
						if(cardLong == null){
							String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getMembership().getCardNumber().trim()+".";
							status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
							return returnTransactionResponse;
						}
						cardNumber = Constants.STRING_NILL+cardLong;
						returnOfflineTransactionRequest.getMembership().setCardNumber(cardNumber);
						
						loyaltyCard = findLoyaltyCardByUserId(cardNumber, user.getUserId());
						
						if(loyaltyCard == null){
							status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
							return returnTransactionResponse;
						}
						

						loyaltyProgram = findLoyaltyProgramByProgramId(loyaltyCard.getProgramId(), user.getUserId());
						if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus())){
							status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
							return returnTransactionResponse;
						}
						contactsLoyalty = findContactLoyalty(cardNumber, loyaltyProgram.getProgramId(), user.getUserId());
						
						if(contactsLoyalty == null){
							status = new Status("1000", PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
							return returnTransactionResponse;
						}
					
					}//if card
					else if(returnOfflineTransactionRequest.getMembership().getPhoneNumber() != null 
							&& returnOfflineTransactionRequest.getMembership().getPhoneNumber().trim().length() > 0){//look up loyalty based on mobile number
						
						String validStatus = LoyaltyProgramHelper.validateMembershipMobile(returnOfflineTransactionRequest.getMembership().getPhoneNumber().trim());
						if(OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID.equals(validStatus)){
							status = new Status("111554", PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getMembership().getPhoneNumber().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
							return returnTransactionResponse;
						}
						
						contactsLoyalty = findContactLoyaltyByMobile(returnOfflineTransactionRequest.getMembership().getPhoneNumber().trim(), user);
						
						if(contactsLoyalty == null){
							status = new Status("111519", PropertyUtil.getErrorMessage(111519, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getMembership().getPhoneNumber().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
							return returnTransactionResponse;
						}
						
						
					}else if(returnOfflineTransactionRequest.getCustomer() != null && returnOfflineTransactionRequest.getCustomer().getPhone() != null 
							&& !returnOfflineTransactionRequest.getCustomer().getPhone().trim().isEmpty()){//cutomer's phone
						

						
						List<ContactsLoyalty> enrollList = findEnrollListByMobile(returnOfflineTransactionRequest.getCustomer().getPhone().trim(), user.getUserId());
						
						if(enrollList == null){
							status = new Status("111524", PropertyUtil.getErrorMessage(111524, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
							return returnTransactionResponse;
						}
						
						List<Contacts> dbContactList = null;
						Contacts dbContact = null;
						
						if(enrollList.size() > 1){
							logger.info("Found more than 1 enrollments");
							Contacts jsonContact = prepareContactFromJsonData(returnOfflineTransactionRequest.getCustomer(), user.getUserId());
							jsonContact.setUsers(user);
							dbContactList = findOCContact(jsonContact, user.getUserId());
							
							if(dbContactList == null || dbContactList.size() == 0){
								logger.info(" request contact not found in OC");
								
								List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);
								
								status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, matchedCustomers, status);
								return returnTransactionResponse;
							}
							else if(dbContactList.size() == 1){
								logger.info("else case..enrollList ..."+enrollList.size());
								dbContact = dbContactList.get(0);
								logger.info("dbcontact cid == "+dbContact.getContactId());
								Iterator<ContactsLoyalty> iterList = enrollList.iterator();
								ContactsLoyalty loyalty = null;
								int count = 0;
								while(iterList.hasNext()){
									loyalty = iterList.next();
									logger.info(" enrollist cid.."+loyalty.getContact().getContactId());
									if(loyalty.getContact() != null && loyalty.getContact().getContactId() != null 
											&& loyalty.getContact().getContactId().equals(dbContact.getContactId())){
										if(contactsLoyalty == null)	contactsLoyalty = loyalty;
										count++;
										logger.info("loyalty found in more than one enrollment case...");
									}
								}
								if(count > 1){
									contactsLoyalty = null;
								}
								if(contactsLoyalty == null){
									
									List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);
									
									status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
									returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, matchedCustomers, status);
									return returnTransactionResponse;
								}
							}
							else{
								List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);
								status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, matchedCustomers, status);
								return returnTransactionResponse;
							}
							
							
						}
						else{
							logger.info("loyalty found in else case....");
							contactsLoyalty = enrollList.get(0);
						}
					
						
					}// else if customer's phone
					
					if(contactsLoyalty != null ){
						
						if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
							
							
							String cardNumber = contactsLoyalty.getCardNumber()+Constants.STRING_NILL;
							
							if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equals(contactsLoyalty.getRewardFlag()) ){
								
								status = new Status("111581", PropertyUtil.getErrorMessage(111581, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
								return returnTransactionResponse;
								
								
							}
							
							
							if(loyaltyCard == null) loyaltyCard = findLoyaltyCardByUserId(cardNumber, user.getUserId());
							
							if(loyaltyCard == null){
								status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
								return returnTransactionResponse;
							}
							
							if(loyaltyProgram == null) {
							
								loyaltyProgram = findLoyaltyProgramByProgramId(loyaltyCard.getProgramId(), user.getUserId());
								if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus())){
									status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
									returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
									return returnTransactionResponse;
								}
							}
							
							LoyaltyCardSet loyaltyCardSet = null;
							loyaltyCardSet = findLoyaltyCardSetByCardsetId(loyaltyCard.getCardSetId(), user.getUserId());
							if(loyaltyCardSet == null || !OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE.equals(loyaltyCardSet.getStatus())){
								status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
								return returnTransactionResponse;
							}
							if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
									contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED) || 
									contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
								

								LoyaltyProgramTier tier = null;
								
								
								List<Balance> balances = null;
								
								List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
								
								if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
									contactLoyaltyList.add(contactsLoyalty);
									if(contactsLoyalty.getProgramTierId() != null) tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
									
									balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
									String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
									status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								}
								else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
									contactLoyaltyList.add(contactsLoyalty);
									if(contactsLoyalty.getProgramTierId() != null) tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
									
									balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
									String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
									status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								}else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
									ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactsLoyalty);
									String maskedNum = Constants.STRING_NILL;
									if(destLoyalty != null) {
										contactLoyaltyList.add(destLoyalty);
										contactsLoyalty = destLoyalty;
										tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
										balances = prepareBalancesObject(destLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
										maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
										
									}
									String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+maskedNum+".";
									status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								}
								
								List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
								MembershipResponse response = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
								
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances,
														null, null, matchedCustomers, status);
								return returnTransactionResponse;
								
								
								
							}//if contact loyalty is active or not
							
							
						}//if card based loyalty
						else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(contactsLoyalty.getMembershipType())){
							
							 loyaltyProgram = findActiveMobileProgram(contactsLoyalty.getProgramId());
							
							if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equals(loyaltyProgram.getStatus())){
								status = new Status("111522", PropertyUtil.getErrorMessage(111522, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null,
										null, null, null, status);
								return returnTransactionResponse;
							}
							
						}//else if mobile based
						
						LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());

                        Double returnedAmountdbl = calculateReturnAmount(returnOfflineTransactionRequest.getItems().getOCLoyaltyItem() );
		                logger.info("Return value "+returnedAmountdbl);
						Double itemExcludedAmount = 0.0;
						if(loyaltyExclusion != null && ((loyaltyExclusion.getClassStr() != null  && !loyaltyExclusion.getClassStr().isEmpty()) || loyaltyExclusion.getDcsStr() != null ||
								(loyaltyExclusion.getDeptCodeStr() != null && !loyaltyExclusion.getDeptCodeStr().isEmpty())|| 
								(loyaltyExclusion.getItemCatStr() != null && !loyaltyExclusion.getItemCatStr().isEmpty()) ||
								(loyaltyExclusion.getSkuNumStr() != null  && !loyaltyExclusion.getSkuNumStr().isEmpty())||
								(loyaltyExclusion.getSubClassStr() != null && !loyaltyExclusion.getSubClassStr().isEmpty()) ||
								(loyaltyExclusion.getVendorStr() != null && !loyaltyExclusion.getVendorStr().isEmpty()))){
							itemExcludedAmount = calculateItemDiscount(returnOfflineTransactionRequest.getItems().getOCLoyaltyItem() , loyaltyExclusion);
						}
						
						
						Double netReturnedAmountdbl = returnedAmountdbl - itemExcludedAmount;
						
						//double netReturnedAmount = Math.round(netReturnedAmountdbl);
						double netReturnedAmount = netReturnedAmountdbl;
						//calculate the rollback based on current earn rules 
						
						if(netReturnedAmount <= 0) {//APP-2114
							status = new Status("111604", PropertyUtil.getErrorMessage(111604, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
							return returnTransactionResponse;
							}
						LoyaltyProgramTier tier = null;//getLoyaltyTier(contactsLoyalty.getProgramTierId());
						
						if(contactsLoyalty.getProgramTierId() != null)
							tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
						else{
							Long contactId = null;
							if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
								contactId = contactsLoyalty.getContact().getContactId();
								List<LoyaltyProgramTier> tierList = validateTierList(contactsLoyalty.getProgramId(), contactsLoyalty.getUserId());
								if(tierList == null || tierList.size() == 0 || !OCConstants.LOYALTY_PROGRAM_TIER1.equals(tierList.get(0).getTierType())){
									status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
											OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
									returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null,
											null, null, null, status);
									return returnTransactionResponse;
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
								
								tier = findTier(contactId, contactsLoyalty.getUserId(),contactsLoyalty, tierList,eligibleMap);
								if(tier != null){
									contactsLoyalty.setProgramTierId(tier.getTierId());
									saveContactsLoyalty(contactsLoyalty);
								}
								else{
									status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
											OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
									returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null,
											null, null, null, status);
									return returnTransactionResponse;
								}
							}
						}//determine tier
						//long earnedValue = 0;
						double earnedValue = 0;
						String earntype = tier.getEarnType();
						String pointsDifference = Constants.STRING_NILL;
						String amountDifference = Constants.STRING_NILL;
						
						if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)){
							
							Double multipleFactordbl = netReturnedAmount/tier.getEarnOnSpentAmount();
							long multipleFactor = multipleFactordbl.intValue();
							//earnedValue = (long)Math.floor(tier.getEarnValue() * multipleFactor);
							earnedValue = tier.getEarnValue() * multipleFactor;
						}
						else if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)){
							
							earnedValue = (tier.getEarnValue() * netReturnedAmount)/100;
						}
						
						if(OCConstants.LOYALTY_TYPE_POINTS.equals(earntype)){
							earnedValue = (long)Math.floor(earnedValue);
							pointsDifference = "-"+(long)earnedValue;
						}
						else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(earntype)){
							String result = Utility.truncateUptoTwoDecimal(earnedValue);
							if(result != null)
								earnedValue = Double.parseDouble(result);
							amountDifference = "-"+earnedValue;
						}
						
						Map<String, Object> balMap = new HashMap<String, Object>();
						balMap = updateContactLoyaltyBalances(earnedValue, earntype, contactsLoyalty, tier,netReturnedAmount);//Changes LPV
						
						status = (Status) balMap.get("status");
						MembershipResponse response = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
						List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
						contactLoyaltyList.add(contactsLoyalty);
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
						if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
							
							List<Balance> balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
							String expiryPeriod = Constants.STRING_NILL;
							if(tier != null && tier.getActivationFlag() == OCConstants.FLAG_YES	&& ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
									(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){
								expiryPeriod = tier.getPtsActiveDateValue()+" "+tier.getPtsActiveDateType();
							}
							HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
							BalancesAdditionalInfo additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
							
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances,
									holdBalance, additionalInfo, null, status);
							return returnTransactionResponse;
						}
						if(loyaltyProgram.getPartialReversalFlag() == OCConstants.FLAG_YES){
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
						}
						String description = returnedAmountdbl+Constants.STRING_NILL;
						amountDifference = balMap.get("AmountDifference") != null && !balMap.get("AmountDifference").toString().isEmpty() ? (String)balMap.get("AmountDifference") : Constants.STRING_NILL;
						pointsDifference = balMap.get("PointsDifference") != null && !balMap.get("PointsDifference").toString().isEmpty() ? (String)balMap.get("PointsDifference") : Constants.STRING_NILL;
						createReturnTransaction(returnOfflineTransactionRequest.getHeader(), returnOfflineTransactionRequest.getAmount(), originalRecept, contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
								pointsDifference, amountDifference, null , null, null, 
								responseHeader.getTransactionId(), Double.parseDouble(returnOfflineTransactionRequest.getAmount().getEnteredValue()),OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL, 
								returnOfflineTransactionRequest.getAmount().getEnteredValue(), null);
						createReturnTransaction(returnOfflineTransactionRequest.getHeader(), returnOfflineTransactionRequest.getAmount(), originalRecept, contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
								pointsDifference, amountDifference, null , null, null, 
								responseHeader.getTransactionId(), Double.parseDouble(returnOfflineTransactionRequest.getAmount().getEnteredValue()),OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL, 
								description, null,null);
						List<Balance> balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL+pointsDifference, Constants.STRING_NILL+amountDifference, Constants.STRING_NILL);
						String expiryPeriod = Constants.STRING_NILL;
						if(tier != null && tier.getActivationFlag() == OCConstants.FLAG_YES	&& ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
								(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){
				
							expiryPeriod = tier.getPtsActiveDateValue()+" "+tier.getPtsActiveDateType();
						}
						
						
						HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
						BalancesAdditionalInfo additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
						//status = new Status("0", "Return was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						if(loyaltyProgram.getPartialReversalFlag() == OCConstants.FLAG_YES){
							if((String)(balMap.get("remainingRewardCurrency")) != null && !((String)(balMap.get("remainingRewardCurrency")).toString()).isEmpty())
								status = new Status("0", "Return was successful and Remainder Debit : $ " + Utility.truncateUptoTwoDecimal((String)(balMap.get("remainingRewardCurrency"))) + ".", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
							else
								status = new Status("0", "Return was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						}else{
							status = new Status("0", "Return was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						}
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances,
								holdBalance, additionalInfo, matchedCustomers, status);
						return returnTransactionResponse;
										
					}
					
					
					
				}
				
			}
			else if(returnOfflineTransactionRequest.getAmount().getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_STORE_CREDIT)){

				ContactsLoyalty contactsLoyalty = null;

				if(returnOfflineTransactionRequest.getMembership().getCardNumber() != null 
						&& returnOfflineTransactionRequest.getMembership().getCardNumber().trim().length() > 0){

					logger.info("Return transaction by card number >>>");

					String cardNumber = Constants.STRING_NILL;
					String cardLong = null;

					cardLong = OptCultureUtils.validateOCLtyCardNumber(returnOfflineTransactionRequest.getMembership().getCardNumber().trim());
					if(cardLong == null){
						String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getMembership().getCardNumber().trim()+".";
						status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}
					cardNumber = Constants.STRING_NILL+cardLong;
					returnOfflineTransactionRequest.getMembership().setCardNumber(cardNumber);

					logger.info("Card Number after parsing... "+cardNumber);

					return cardBasedReturnTransaction(header, amount, originalRecept,  cardNumber, responseHeader, user, requestJson);
				}
				else if(returnOfflineTransactionRequest.getMembership().getPhoneNumber() != null 
						&& returnOfflineTransactionRequest.getMembership().getPhoneNumber().trim().length() > 0){

					String validStatus = LoyaltyProgramHelper.validateMembershipMobile(returnOfflineTransactionRequest.getMembership().getPhoneNumber().trim());
					if(OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID.equals(validStatus)){
						String msg = PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getMembership().getPhoneNumber().trim();
						status = new Status("111554", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}

					contactsLoyalty = findContactLoyaltyByMobile(returnOfflineTransactionRequest.getMembership().getPhoneNumber().trim(), user);
					if(contactsLoyalty == null){
						status = new Status("111519", PropertyUtil.getErrorMessage(111519, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getMembership().getPhoneNumber().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}

					return mobileBasedReturnTransaction(contactsLoyalty, responseHeader, header, amount, originalRecept,  user, requestJson);

				}
				else if(returnOfflineTransactionRequest.getCustomer().getPhone() != null 
						&& !returnOfflineTransactionRequest.getCustomer().getPhone().trim().isEmpty()){

					List<ContactsLoyalty> enrollList = findEnrollListByMobile(returnOfflineTransactionRequest.getCustomer().getPhone(), user.getUserId());

					if(enrollList == null){
						status = new Status("111524", PropertyUtil.getErrorMessage(111524, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}

					List<Contacts> dbContactList = null;
					Contacts dbContact = null;

					if(enrollList.size() > 1){
						logger.info("Found more than 1 enrollments");
						Contacts jsonContact = prepareContactFromJsonData(returnOfflineTransactionRequest.getCustomer(), user.getUserId());
						jsonContact.setUsers(user);
						dbContactList = findOCContact(jsonContact, user.getUserId());

						if(dbContactList == null){
							logger.info(" request contact not found in OC");

							List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);

							status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null,null, matchedCustomers, status);
							return returnTransactionResponse;
						}
						else if(dbContactList.size() == 1){
							logger.info("else case..enrollList ..."+enrollList.size());
							dbContact = dbContactList.get(0);
							logger.info("dbcontact cid == "+dbContact.getContactId());
							Iterator<ContactsLoyalty> iterList = enrollList.iterator();
							ContactsLoyalty loyalty = null;
							int count = 0;
							while(iterList.hasNext()){
								loyalty = iterList.next();
								logger.info(" enrollist cid.."+loyalty.getContact().getContactId());
								if(loyalty.getContact() != null && loyalty.getContact().getContactId() != null 
										&& loyalty.getContact().getContactId().longValue() == dbContact.getContactId().longValue()){
									if(contactsLoyalty == null)	contactsLoyalty = loyalty;
									count++;
									logger.info("loyalty found in more than one enrollment case...");
								}
							}
							if(count > 1){
								contactsLoyalty = null;
							}
							if(contactsLoyalty == null){

								List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);

								status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, matchedCustomers, status);
								return returnTransactionResponse;
							}
						}
						else{
							List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);

							status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnOfflineTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null,null, matchedCustomers, status);
							return returnTransactionResponse;
						}

					}
					else{
						contactsLoyalty = enrollList.get(0);
					}
					logger.info("contactsLoyalty = "+contactsLoyalty);

					if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(contactsLoyalty.getMembershipType())){
						return mobileBasedReturnTransaction(contactsLoyalty, responseHeader, header, amount, originalRecept, user, requestJson);
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
						return cardBasedReturnTransaction(header, amount, originalRecept, Constants.STRING_NILL+contactsLoyalty.getCardNumber(), responseHeader, user, requestJson);
					}
					else{
						logger.info("INVALID LOYALTY MEMBERSHIP CARD TYPE .... LOOKINTO THIS...");
					}

				}
				else{
					status = new Status("111523", PropertyUtil.getErrorMessage(111523, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null,null, null, status);
					return returnTransactionResponse;
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
		logger.info("processReturnTransactionRequest end");
		return returnTransactionResponse;
	
		return null;
	*/}//processReturnTransactionRequest
	
	
	private ContactsLoyalty getMembership(LoyaltyTransactionChild loyaltyTransactionChild) throws Exception {
		
		ContactsLoyalty contactsLoyalty = findLoyaltyById(loyaltyTransactionChild.getLoyaltyId(), loyaltyTransactionChild.getProgramId(),
				loyaltyTransactionChild.getUserId());
		
		return contactsLoyalty;
	}
	
	private List<LoyaltyProgramTier> validateTierList(Long programId, Long contactId) throws Exception {
		logger.info("validateTierList start");
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
			
			
			logger.info("validateTierList end");
			return tiersList;
		}catch(Exception e){
			logger.error("Exception in validating tiersList::", e);
			return null;
		}

	}
	
	private LoyaltyProgramTier findTier(Long contactId, Long userId, ContactsLoyalty contactsLoyalty,
			List<LoyaltyProgramTier> tiersList, Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap) throws Exception {
		logger.info("findTier start");

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

					/*RetailProSalesDao salesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
					Object[] cumulativeAmountArr = salesDao.getCumulativePurchase(userId, contactId, startDate, endDate);

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
				logger.info("findTier end");
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
	
	private Status checkItemsEmpty(List<SkuDetails> itemsList) {
		logger.info("checkItemsEmpty start now");
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
					(item.getQuantity() == null || item.getQuantity().trim().isEmpty()) && 
					(item.getItemSID() == null || (item.getItemSID().trim().isEmpty()))){
				status = new Status("111528", PropertyUtil.getErrorMessage(111528, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		
			}
			 /* if(!item.getBilledUnitPrice().trim().isEmpty() && Double.parseDouble(item.getBilledUnitPrice())<=0) {//APP-2114
				status = new Status("111528", PropertyUtil.getErrorMessage(111528, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);//APP-2114
				return status;
			}*/
			
		}
		
		logger.info("checkItemsEmpty end");
		return status;
	}
	
	
	private Map<String ,List<SkuDetails> > findItemLevelOR(LoyaltyReturnTransactionRequest returnRequest){
		
		Map<String ,List<SkuDetails> > itemLevelORMap = new HashMap<String ,List<SkuDetails> >();;
		List<SkuDetails> itemsWithOR = new ArrayList<SkuDetails>();
		List<SkuDetails> itemsWithOutOR = new ArrayList<SkuDetails>();
		List<SkuDetails> iterItems = returnRequest.getItems();
		for (SkuDetails skuDetails : iterItems) {
			
			OriginalReceipt itemLevelOR = skuDetails.getOriginalReceipt();
			/*if(itemLevelOR != null &&
					((itemLevelOR.getDocSID() != null && 
					!itemLevelOR.getDocSID().trim().isEmpty())||
					(itemLevelOR.getReceiptNumber()!=null && itemLevelOR.getReceiptNumber().trim().isEmpty() && 
					((itemLevelOR.getStoreNumber()!= null && 	!itemLevelOR.getStoreNumber().isEmpty()) || 
							(itemLevelOR.getSubsidiaryNumber() != null && !itemLevelOR.getSubsidiaryNumber().isEmpty()))))){
				*/
			boolean isOR = (itemLevelOR != null && 
					((itemLevelOR.getDocSID() != null && 
					!itemLevelOR.getDocSID().trim().isEmpty()) ||
					((itemLevelOR.getReceiptNumber()!=null && !itemLevelOR.getReceiptNumber().trim().isEmpty()) && 
							((itemLevelOR.getStoreNumber()!= null && !itemLevelOR.getStoreNumber().isEmpty()) ||
									(itemLevelOR.getSubsidiaryNumber() != null && !itemLevelOR.getSubsidiaryNumber().isEmpty())))));
			logger.debug("isOR==="+isOR);
			if(isOR) {
				itemsWithOR.add(skuDetails);
			
			}else{
				itemsWithOutOR.add(skuDetails);
				
			}
			
		}
		itemLevelORMap.put(TOKEN_ITEM_WITH_OR, itemsWithOR);
		itemLevelORMap.put(TOKEN_ITEM_WITH_OUT_OR, itemsWithOutOR);
		
		return itemLevelORMap;
	}
	
	private Set<List<SkuDetails>> findSameORItemSet(List<SkuDetails> items ){

		
		Set<List<SkuDetails>> sameORSet = new HashSet<List<SkuDetails>>();
		OriginalReceipt currOriginalReceipt = null;
		
		List<SkuDetails> sameORItems = null;
		Set<Integer> itemsSet = new HashSet<Integer>();
		for (int i=0; i<items.size(); i++) {
			SkuDetails skuItem = items.get(i);
			if(itemsSet.contains(i) ) {
				logger.debug("i==="+i+"   itemsSet.contains(j)==="+itemsSet.contains(i));
				continue;
			}
			
			itemsSet.add(i);
			OriginalReceipt currRecept = skuItem.getOriginalReceipt();
			sameORItems = new ArrayList<SkuDetails>();
			sameORItems.add(skuItem);
			for (int j=0; j<items.size(); j++) {
				SkuDetails item = items.get(j);
				if(i==j || itemsSet.contains(j) ){
					logger.debug("i==="+i+"  j ==="+j+" itemsSet.contains(j)==="+itemsSet.contains(j));
					continue;
				}
				currOriginalReceipt = item.getOriginalReceipt();
				if(currOriginalReceipt != null && currRecept != null && ((currOriginalReceipt.getDocSID() != null && !currOriginalReceipt.getDocSID().isEmpty() && 
						currRecept.getDocSID() != null && !currRecept.getDocSID().isEmpty() && 
						currOriginalReceipt.getDocSID().equals(currRecept.getDocSID())) ||
						(currOriginalReceipt.getReceiptNumber() != null && !currOriginalReceipt.getReceiptNumber().isEmpty() &&
						currRecept.getReceiptNumber() != null && !currRecept.getReceiptNumber().isEmpty() && 
						currOriginalReceipt.getReceiptNumber().equals(currRecept.getReceiptNumber()) && 
						((currOriginalReceipt.getStoreNumber()!= null && 	!currOriginalReceipt.getStoreNumber().isEmpty() && 
						currRecept.getStoreNumber() != null && !currRecept.getStoreNumber().isEmpty() && currOriginalReceipt.getStoreNumber().equals(currRecept.getStoreNumber()) ) || 
								(currOriginalReceipt.getSubsidiaryNumber() != null && !currOriginalReceipt.getSubsidiaryNumber().isEmpty() &&
										currRecept.getSubsidiaryNumber() != null && !currRecept.getSubsidiaryNumber().isEmpty() && 
										currRecept.getSubsidiaryNumber().equals(currOriginalReceipt.getSubsidiaryNumber())) )))){
					
					sameORItems.add(item);
					itemsSet.add(j);				
					//itemsSet.add(skuItem.getItemSID());
					
				}
			
			}
			sameORSet.add(sameORItems);
			
		}
		
		return sameORSet;
		
	
		
	}
	
	private Set<List<SkuDetails>> findSameORItemSetDup(List<SkuDetails> items ){
		
		Set<List<SkuDetails>> sameORSet = new HashSet<List<SkuDetails>>();
		OriginalReceipt currOriginalReceipt = null;
		
		List<SkuDetails> sameORItems = null;
		Set<String> itemsSet = new HashSet<String>();
		for (int i=0; i<items.size(); i++) {
			SkuDetails skuItem = items.get(i);
			if(skuItem.getItemSID() != null && !skuItem.getItemSID().isEmpty() && itemsSet.contains(skuItem.getItemSID())) continue;
			
			itemsSet.add(skuItem.getItemSID());
			OriginalReceipt currRecept = skuItem.getOriginalReceipt();
			sameORItems = new ArrayList<SkuDetails>();
			sameORItems.add(skuItem);
			for (int j=0; j<items.size(); j++) {
				SkuDetails item = items.get(j);
				if(i==j ||(skuItem.getItemSID() != null && !skuItem.getItemSID().isEmpty() && itemsSet.contains(skuItem.getItemSID())) ) continue;
				currOriginalReceipt = item.getOriginalReceipt();
				if(currOriginalReceipt != null && currRecept != null && ((currOriginalReceipt.getDocSID() != null && !currOriginalReceipt.getDocSID().isEmpty() && 
						currRecept.getDocSID() != null && !currRecept.getDocSID().isEmpty() && 
						currOriginalReceipt.getDocSID().equals(currRecept.getDocSID())) ||
						(currOriginalReceipt.getReceiptNumber() != null && !currOriginalReceipt.getReceiptNumber().isEmpty() &&
						currRecept.getReceiptNumber() != null && !currRecept.getReceiptNumber().isEmpty() && 
						currOriginalReceipt.getReceiptNumber().equals(currRecept.getReceiptNumber()) && 
						((currOriginalReceipt.getStoreNumber()!= null && 	!currOriginalReceipt.getStoreNumber().isEmpty() && 
						currRecept.getStoreNumber() != null && !currRecept.getStoreNumber().isEmpty() && currOriginalReceipt.getStoreNumber().equals(currRecept.getStoreNumber()) ) || 
								(currOriginalReceipt.getSubsidiaryNumber() != null && !currOriginalReceipt.getSubsidiaryNumber().isEmpty() &&
										currRecept.getSubsidiaryNumber() != null && !currRecept.getSubsidiaryNumber().isEmpty() && 
										currRecept.getSubsidiaryNumber().equals(currOriginalReceipt.getSubsidiaryNumber())) )))){
					
					sameORItems.add(item);
					itemsSet.add(skuItem.getItemSID());
					
				}
			
			}
			sameORSet.add(sameORItems);
			
		}
		
		return sameORSet;
		
	}
	private boolean isFullRcpt(List<LoyaltyTransactionChild> issuanceList, List<LoyaltyTransactionChild> rewardList, List<SkuDetails> returnedItems){
		logger.debug("===entered to decide isfullreversal===");
		Map<String, Double> itemsMap = new HashMap<String, Double>();
		Map<String, Double> totalItemsMap = new HashMap<String, Double>();
		if(issuanceList != null && !issuanceList.isEmpty()){
			
			for (LoyaltyTransactionChild issuance : issuanceList) {
				
				if(issuance.getItemInfo() != null && !issuance.getItemInfo().isEmpty()){
					String itemInfo = issuance.getItemInfo();
					logger.debug("===itemInfo==="+itemInfo);
					String[] itemsArr = itemInfo.split(Constants.ADDR_COL_DELIMETER)[0].split(",");
					//String requiredQty = itemInfo.split(Constants.ADDR_COL_DELIMETER)[1];
					
					for (String itemStr : itemsArr) {
						String[] itemToken = itemStr.split(Constants.DELIMETER_COLON+"");
						Double qty = Double.parseDouble(itemToken[1]);
						String itemSID = itemToken[0];
						if(itemsMap.containsKey(itemSID)){
							itemsMap.put(itemSID, qty+itemsMap.get(itemSID));
							
						}else{
							
							itemsMap.put(itemSID, qty);
						}
						
					}//for-inner
				}
				
			}
		}
		if(itemsMap.size() > 0)totalItemsMap.putAll(itemsMap);
		Map<String, Double> rewardsitemsMap = new HashMap<String, Double>();
		if(rewardList != null && !rewardList.isEmpty()){
			for (LoyaltyTransactionChild reward : issuanceList) {		
				if(reward.getItemInfo() != null && !reward.getItemInfo().isEmpty()){
					String itemInfo = reward.getItemInfo();
					logger.debug("===itemInfo==="+itemInfo);
					String[] itemsArr = itemInfo.split(Constants.ADDR_COL_DELIMETER)[0].split(",");
					//String requiredQty = itemInfo.split(Constants.ADDR_COL_DELIMETER)[1];
						
					for (String itemStr : itemsArr) {
						String[] itemToken = itemStr.split(Constants.DELIMETER_COLON+"");
						Double qty = Double.parseDouble(itemToken[1]);
						String itemSID = itemToken[0];
						if(rewardsitemsMap.containsKey(itemSID)){
							rewardsitemsMap.put(itemSID, qty+rewardsitemsMap.get(itemSID));
							
						}else{
							
							rewardsitemsMap.put(itemSID, qty);
						}
						
					}//for-inner
				}
				
			}
			if(rewardsitemsMap.size() > 0) {
				
				for (String itemSID : rewardsitemsMap.keySet()) {
					
					if(totalItemsMap.containsKey(itemSID)) continue;
					totalItemsMap.put(itemSID, rewardsitemsMap.get(itemSID));
					
				}
			}//if
			
		}
		
		for (SkuDetails skuDetails : returnedItems) {
			
			if(totalItemsMap.containsKey(skuDetails.getItemSID())){
				if(totalItemsMap.get(skuDetails.getItemSID()).doubleValue() != Double.parseDouble(skuDetails.getQuantity())) return false;
				
			}else return false;
		}
		return true;
	}//
	private OriginalReceipt findFullReversalBasedRecptOR(LoyaltyReturnTransactionRequest returnRequest){
		OriginalReceipt originalRecpt = returnRequest.getOriginalReceipt();
		/*boolean isOR = (originalRecpt != null &&
				((originalRecpt.getDocSID() != null && 
				!originalRecpt.getDocSID().trim().isEmpty())||
				(originalRecpt.getReceiptNumber()!=null && !originalRecpt.getReceiptNumber().trim().isEmpty() && 
				((originalRecpt.getStoreNumber()!= null && 	!originalRecpt.getStoreNumber().isEmpty()) || 
						(originalRecpt.getSubsidiaryNumber() != null && !originalRecpt.getSubsidiaryNumber().isEmpty())))));*/
		boolean isOR = (originalRecpt != null && 
				((originalRecpt.getDocSID() != null && 
				!originalRecpt.getDocSID().trim().isEmpty())||
				(originalRecpt.getReceiptNumber()!=null && !originalRecpt.getReceiptNumber().trim().isEmpty() && Long.parseLong(originalRecpt.getReceiptNumber())>0)));
		if(isOR){
			return originalRecpt;
		}
		else return null;
	}
	
	
	private Object getItemBasedSpecilaRewards(LoyaltyProgram loyaltyProgram, List<SkuDetails> itemsList, Users user,
			LoyaltyReturnTransactionRequest returnRequest, ContactsLoyalty contactsLoyalty, ResponseHeader responseHeader) throws Exception{
		
		SpecialRewardsDao specialRewardsDao = (SpecialRewardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPECIAL_REWARDS_DAO);
		SkuFileDao skuFileDao = (SkuFileDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SKU_FILE_DAO);
		LoyaltyMemberItemQtyCounterDao loyaltyMemberItemQtyCounterDao =(LoyaltyMemberItemQtyCounterDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO);
		List<SpecialReward> considerSpecialRewards = new ArrayList<SpecialReward>();
		List<SpecialReward> spList=null;
		spList = specialRewardsDao.findItemBasedSpecialRewardInProgram(loyaltyProgram.getProgramId(), loyaltyProgram.getOrgId());
		Set<String> excludeItems = new HashSet<String>();
		if(spList != null && !spList.isEmpty()) {
			
			
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
			/*List<LoyaltyMemberItemQtyCounter> retList = null;
			retList= loyaltyMemberItemQtyCounterDao.findItemsCounter(spStr, contactsLoyalty.getLoyaltyId());
			Map<Long, LoyaltyMemberItemQtyCounter > itemCountSet = new HashMap<Long, LoyaltyMemberItemQtyCounter>();
			logger.debug("retList ===", retList);
			if(retList != null && !retList.isEmpty()) {
				for (LoyaltyMemberItemQtyCounter loyaltyMemberItemQtyCounter : retList) {
					
					itemCountSet.put(loyaltyMemberItemQtyCounter.getSPRuleID(), loyaltyMemberItemQtyCounter);
				}
			}*/
			Gson gson = new Gson();
			String returnJsonStr = gson.toJson(itemsList);
			Object issuanceJsonItems = JSONValue.parse(returnJsonStr);
			String spRule = null;
			List<SpecialReward> itemsBasedSP = new ArrayList<SpecialReward>();
			List<SpecialReward> visitsBasedSP = new ArrayList<SpecialReward>();
			List<SpecialReward> otherSP = new ArrayList<SpecialReward>();
			Map<String , Double> matchedItemsMap = new java.util.HashMap<String, Double>();
			for (SpecialReward specialReward : spList) {
				

				Map<String , Integer> sameitemsmap = new java.util.HashMap<String, Integer>();
				Map<String , Boolean> considerItemsMap = new java.util.HashMap<String, Boolean>();
				Map<String , Integer> itemsQtyMap = new java.util.HashMap<String, Integer>();
				Map<String , Double> itemsPriceMap = new java.util.HashMap<String, Double>();
				boolean considerthisSP = false;
				boolean isItemsBased = false;
				//boolean isVisitsBased = false;
				//boolean isOthers = true;
				spRule = specialReward.getRewardRule();
				double totItemPrice = 0.0;
				String purchaseType = Constants.STRING_NILL;
				double qty = 0;
				boolean includeDiscount = true;
				
				
				//LoyaltyMemberItemQtyCounter loyaltyMemberItemQtyCounter = null;
				//int spmultiplier = 1;
				//loyaltyMemberItemQtyCounter =  itemCountSet.get(specialReward.getRewardId());
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
							qty =  Double.parseDouble(ruleTokensArr[4]);
							purchaseType = ruleTokensArr[5];
							includeDiscount = ruleTokensArr[3].equalsIgnoreCase("I");
							currFlag=true;//Change APP-2000
							continue;
						}
						
						String ruleTemplate = Utility.specialRuleHashTag.get(ruleHashTag);
						if(ruleHashTag.equalsIgnoreCase("[#PurchasedItem#]")) {
							String jsonelement = ruleTokensArr[1];
							String jsonValue = ruleTokensArr[3];
							
							isItemsBased = true;
							String[] jsoneleArr = jsonelement.split(Constants.DELIMETER_COLON+Constants.STRING_NILL);
							String jsonParent = jsoneleArr[0];
							String jsonele = jsoneleArr[1];
							Object jsonObj = issuanceJsonItems;
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
										
										String promoUsed = specialReward.getPromoCode(); //check whether the qty has free item  
										if(promoUsed != null && !promoUsed.isEmpty() && 
												specialReward.getExcludeQty() != null && 
												itemNote != null && !itemNote.toString().isEmpty() && 
												itemNote.toString().toLowerCase().contains(promoUsed.toLowerCase()+Constants.DELIMETER_COLON)){
											
											
											itemQty -= specialReward.getExcludeQty();
										}
										if(itemQty <= 0 ) continue;
										
										double discountDbl = !discount.toString().isEmpty() ? Double.parseDouble(discount.toString()) : 0.0;
										//int itemQty = Integer.parseInt(quantity.toString());
										//double taxDbl = !tax.toString().isEmpty() ? Double.parseDouble(tax.toString()) : 0.0;
										double itemUnitPrice =  Double.parseDouble(unitPrice.toString()) ;
										/*logger.debug("itemUnitPrice ==="+itemUnitPrice);
										if(itemUnitPrice <= 0 ){
											logger.debug("==its 0 ==");
											continue;
										}*/
										
										double itemPrice = (itemQty*itemUnitPrice);
										
										if(!includeDiscount) {
											if(discount != null && !discount.toString().isEmpty() && Double.parseDouble(discount.toString()) >0){
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
							//logger.debug("currFlag ==="+currFlag);
						}//if item based sp
						
						currFlag = currORFlag || currFlag;
					}
					//logger.debug("currANDFlag=="+currANDFlag+" currFlag ==="+currFlag);
					currANDFlag = currANDFlag && currFlag;
					
					//if(!currANDFlag) break;//check this
				}//and rule
				considerthisSP = currANDFlag || !considerItemsMap.isEmpty();
				double itemPrice = 0.0;
				if(considerthisSP && isItemsBased && !considerItemsMap.isEmpty()){
					
					int totalQty = 0;
					List<SkuDetails> items =itemsList ;
					String itemStr = Constants.STRING_NILL;
					String itemSIDs = Constants.STRING_NILL;
					//logger.debug("considerItemsMap ===>"+considerItemsMap.size());
					for (String itemSID : considerItemsMap.keySet()) {
						//logger.debug("itemSID ==="+itemSID);
						if(considerItemsMap.get(itemSID)){
					
							for (SkuDetails skuDetails : items) {
								if(!skuDetails.getItemSID().equals(itemSID)) continue;
								double itemQty = Double.parseDouble(skuDetails.getQuantity());
							//	logger.debug("itemSID ==="+skuDetails.getItemSID()+" itemQty==>"+itemQty);
								//double taxDbl = !tax.toString().isEmpty() ? Double.parseDouble(tax.toString()) : 0.0;
								double itemUnitPrice =  Double.parseDouble(skuDetails.getBilledUnitPrice()) ;
								double eachItemPrice = (itemQty*itemUnitPrice);
								if(specialReward.isDeductItemPrice())matchedItemsMap.put(itemSID, eachItemPrice);
								totalQty += itemQty;
								totItemPrice += eachItemPrice;
								if(!itemStr.isEmpty()) itemStr += ",";
								itemStr += itemSID+Constants.DELIMETER_COLON+itemQty+Constants.DELIMETER_COLON+itemUnitPrice;
								
								if(!itemSIDs.isEmpty()) itemSIDs += ",";
								itemSIDs += itemSID+Constants.DELIMETER_COLON+itemQty;
								if(specialReward.isDeductItemPrice()) excludeItems.add(itemSID);
								
							}
							
							
						}
					}
					if(totalQty > 0){
						boolean deductReward = false; ;
						LoyaltyMemberItemQtyCounterDao LoyaltyMemberItemQtyCounterDao = (LoyaltyMemberItemQtyCounterDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO);
						List<LoyaltyMemberItemQtyCounter> memberQty = LoyaltyMemberItemQtyCounterDao.findItemsCounter(specialReward.getRewardId().longValue()+"", contactsLoyalty.getLoyaltyId());
						LoyaltyMemberItemQtyCounter existingQty = null; //memberQty.get(0);
						LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
						LoyaltyBalanceDaoForDML loyaltyBalanceDaoForDML = (LoyaltyBalanceDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_BALANCE_DAO_FOR_DML);
						LoyaltyBalance loyaltyBalances = loyaltyBalanceDao.findBy(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(), specialReward.getRewardValueCode());
						if(memberQty != null && !memberQty.isEmpty()){
							existingQty =  memberQty.get(0);
						}
						if(existingQty != null) { //accross multiple
							double existingqty = existingQty.getQty();
							long reward =Long.parseLong(specialReward.getRewardValue());
							double duductRewardDbl = 0;
							boolean issufficient = false;
							if(existingqty == 0  ){//deduct the reward 
								duductRewardDbl = reward;
								deductReward = true;
								issufficient = loyaltyBalances != null && loyaltyBalances.getBalance() >= Long.parseLong(specialReward.getRewardValue());
								existingQty.setQty(qty-totalQty);
							}else if(existingQty.getQty() >= totalQty){ // dont deduct the reward
								existingQty.setQty(existingQty.getQty()-totalQty);
							}else if(existingQty.getQty() < totalQty ) {//deduct
								deductReward = true;
								issufficient = loyaltyBalances != null && loyaltyBalances.getBalance() >= Long.parseLong(specialReward.getRewardValue());
								duductRewardDbl = reward;
								existingQty.setQty((qty)-totalQty+existingQty.getQty());
							}
							logger.debug("existingQty==="+existingQty.getQty() +" duductRewardDbl==="+duductRewardDbl);
							if(deductReward && !issufficient){
								 //MembershipResponse memresponse = prepareMembershipResponse(contactsLoyalty, loyaltyProgramTier, loyaltyProgram);
								Status status = new Status("111562", "On returned items, earned rewards of "+duductRewardDbl+" "+specialReward.getRewardValueCode()+" cannot be reversed." , OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
								LoyaltyReturnTransactionResponse returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
								return returnTransactionResponse;
							}
							if(!returnRequest.getAmount().getType().equals(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)){
								
								LoyaltyMemberItemQtyCounterDaoforDML loyaltyMemberItemQtyCounterDaoForDml =(LoyaltyMemberItemQtyCounterDaoforDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO_FOR_DML);
								loyaltyMemberItemQtyCounterDaoForDml.saveOrUpdate(existingQty);
								if(deductReward){
									String rewardDiff = Constants.STRING_NILL;
									Long rewardBal = loyaltyBalances.getBalance();
									logger.debug("curr bal before==="+reward);
									Double totalReward = loyaltyBalances.getTotalEarnedBalance();
									rewardDiff = (rewardBal <=  duductRewardDbl) ? ("-"+rewardBal): "-"+duductRewardDbl ;
									loyaltyBalances.setBalance(loyaltyBalances.getBalance()-(long)duductRewardDbl);
									loyaltyBalances.setTotalEarnedBalance(loyaltyBalances.getTotalEarnedBalance()-(long)duductRewardDbl);
									if(loyaltyBalances.getBalance() < 0) loyaltyBalances.setBalance(0l);
									if(loyaltyBalances.getTotalEarnedBalance() < 0) loyaltyBalances.setTotalEarnedBalance(0.0);	
									loyaltyBalanceDaoForDML.saveOrUpdate(loyaltyBalances);
									logger.debug("rewardDiff==="+rewardDiff+ " earnedReward=="+duductRewardDbl +" curr bal ==="+loyaltyBalances.getBalance());
									
									
									createReturnTransaction(returnRequest.getHeader(), returnRequest.getAmount().getType(), null, 
											contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
											Constants.STRING_NILL, Constants.STRING_NILL, null , null, loyaltyBalances.getValueCode(), 
											responseHeader.getTransactionId(), 0.0,OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD_REVERSAL, 
											Constants.STRING_NILL, null,null, specialReward.getRewardId(),rewardDiff, null );
									//additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
								}
							}
						}
						
						
						/*LoyaltyMemberItemQtyCounterDaoforDML loyaltyMemberItemQtyCounterDaoForDml =(LoyaltyMemberItemQtyCounterDaoforDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO_FOR_DML);
						loyaltyMemberItemQtyCounterDaoForDml.updateCurrQty(contactsLoyalty.getLoyaltyId(), rewardTrx.getSpecialRewardId(), rollBackQty,(int)Double.parseDouble(requiredQty));*/
					}
					
				}
				
			
			}
		}
		
		if(!excludeItems.isEmpty() ) return excludeItems;
		return null;
	}
	private boolean findOR(LoyaltyReturnTransactionRequest returnRequest){
		
		boolean isWithOR = false;
		List<OriginalReceipt> foundORs = new ArrayList<OriginalReceipt>();
		OriginalReceipt originalRecpt = returnRequest.getOriginalReceipt();
		boolean isOR = (originalRecpt != null && 
				((originalRecpt.getDocSID() != null && 
				!originalRecpt.getDocSID().trim().isEmpty())||
				(originalRecpt.getReceiptNumber()!=null && !originalRecpt.getReceiptNumber().trim().isEmpty() && Long.parseLong(originalRecpt.getReceiptNumber())>0 &&
				((originalRecpt.getStoreNumber()!= null && 	!originalRecpt.getStoreNumber().isEmpty()) || 
						(originalRecpt.getSubsidiaryNumber() != null && !originalRecpt.getSubsidiaryNumber().isEmpty())))));
		
		logger.debug("IS OR ==="+isOR);
		
		
		isOR = (originalRecpt != null && 
				((originalRecpt.getDocSID() != null && 
				!originalRecpt.getDocSID().trim().isEmpty())||
				(originalRecpt.getReceiptNumber()!=null && !originalRecpt.getReceiptNumber().trim().isEmpty() && Long.parseLong(originalRecpt.getReceiptNumber())>0)));
		if(isOR){
			foundORs.add(originalRecpt);
		}
		logger.debug("foundORs.size=="+foundORs.size());
		List<SkuDetails> iterItems = returnRequest.getItems();
		for (SkuDetails skuDetails : iterItems) {
			
			OriginalReceipt itemLevelOR = skuDetails.getOriginalReceipt();
			if(itemLevelOR == null) continue;
			isOR = (itemLevelOR != null && 
					((itemLevelOR.getDocSID() != null && 
					!itemLevelOR.getDocSID().trim().isEmpty())||
					(itemLevelOR.getReceiptNumber()!=null && !itemLevelOR.getReceiptNumber().trim().isEmpty())));
			if(isOR) {
			/*if(itemLevelOR != null &&
					((itemLevelOR.getDocSID() != null && 
					!itemLevelOR.getDocSID().trim().isEmpty())||
					(itemLevelOR.getReceiptNumber()!=null && !itemLevelOR.getReceiptNumber().trim().isEmpty() && 
					((itemLevelOR.getStoreNumber()!= null && 	!itemLevelOR.getStoreNumber().isEmpty()) || 
							(itemLevelOR.getSubsidiaryNumber() != null && !itemLevelOR.getSubsidiaryNumber().isEmpty()))))){*/
				foundORs.add(itemLevelOR);
			}
			
		}
		logger.debug("foundORs.size=="+foundORs.size());
		isWithOR = foundORs.size()>0;
		return isWithOR;
	}
	
private Double calculateReturnAmount(List<SkuDetails> itemList) throws Exception {
	logger.info("calculateReturnAmount start");
		Double returnAmount = 0.0;
		
		for(SkuDetails skuDetails : itemList){
			
			if(skuDetails.getBilledUnitPrice() != null && !skuDetails.getBilledUnitPrice().trim().isEmpty() &&
					skuDetails.getQuantity() != null && !skuDetails.getQuantity().trim().isEmpty()){
				returnAmount = returnAmount + (Double.valueOf(skuDetails.getQuantity()) * Double.valueOf(skuDetails.getBilledUnitPrice()));
					
			  }
			}
		logger.info("calculateReturnAmount end");
		return returnAmount;
	}

private Double calculateCreditableAmount(List<SkuDetails> skuDetailsLst) throws Exception {
	logger.info("calculateReturnAmount start");
		Double returnAmount = 0.0;
		for (SkuDetails skuDetails : skuDetailsLst) {
			
			if(skuDetails.getBilledUnitPrice() != null && !skuDetails.getBilledUnitPrice().trim().isEmpty() &&
					skuDetails.getQuantity() != null && !skuDetails.getQuantity().trim().isEmpty()){
				returnAmount = returnAmount + (Double.valueOf(skuDetails.getQuantity()) * Double.valueOf(skuDetails.getBilledUnitPrice()));
				
			}
		}
			
		logger.info("calculateReturnAmount end");
		return returnAmount;
	}




private List<SkuDetails> getFinalItems (List<SkuDetails> itemList, LoyaltyProgramExclusion loyaltyExclusion)
		throws Exception {

	List<String> itemClassList = null;
	List<String> itemDcsList = null;
	List<String> itemdeptCodeList = null;
	List<String> itemCatList = null;
	List<String> skuNumberList = null;
	List<String> itemSubClassList = null;
	List<String> itemVendorCodeList = null;

	if (loyaltyExclusion != null && loyaltyExclusion.getClassStr() != null && !loyaltyExclusion.getClassStr().trim().isEmpty()) {
		itemClassList = Arrays.asList(loyaltyExclusion.getClassStr().split(";=;"));
	}
	if (loyaltyExclusion != null && loyaltyExclusion.getDcsStr() != null && !loyaltyExclusion.getDcsStr().trim().isEmpty()) {
		itemDcsList = Arrays.asList(loyaltyExclusion.getDcsStr().split(";=;"));
	}
	if (loyaltyExclusion != null && loyaltyExclusion.getDeptCodeStr() != null && !loyaltyExclusion.getDeptCodeStr().trim().isEmpty()) {
		itemdeptCodeList = Arrays.asList(loyaltyExclusion.getDeptCodeStr().split(";=;"));
	}
	if (loyaltyExclusion != null && loyaltyExclusion.getItemCatStr() != null && !loyaltyExclusion.getItemCatStr().trim().isEmpty()) {
		itemCatList = Arrays.asList(loyaltyExclusion.getItemCatStr().split(";=;"));
	}
	if (loyaltyExclusion != null && loyaltyExclusion.getSkuNumStr() != null && !loyaltyExclusion.getSkuNumStr().trim().isEmpty()) {
		skuNumberList = Arrays.asList(loyaltyExclusion.getSkuNumStr().split(";=;"));
	}
	if (loyaltyExclusion != null && loyaltyExclusion.getSubClassStr() != null && !loyaltyExclusion.getSubClassStr().trim().isEmpty()) {
		itemSubClassList = Arrays.asList(loyaltyExclusion.getSubClassStr().split(";=;"));
	}
	if (loyaltyExclusion != null && loyaltyExclusion.getVendorStr() != null && !loyaltyExclusion.getVendorStr().trim().isEmpty()) {
		itemVendorCodeList = Arrays.asList(loyaltyExclusion.getVendorStr().split(";=;"));
	}
	List<SkuDetails> finalItems = new ArrayList<SkuDetails>();
	for (SkuDetails skuDetails : itemList) {
		
		if (itemCatList != null && itemCatList.contains(skuDetails.getItemCategory())) {
			
			continue;
		}
		if (itemdeptCodeList != null && itemdeptCodeList.contains(skuDetails.getDepartmentCode())) {
			
			continue;
		}
		if (itemClassList != null && itemClassList.contains(skuDetails.getItemClass())) {
			
			continue;
		}
		if (itemDcsList != null && itemDcsList.contains(skuDetails.getDCS())) {
			
			continue;
		}
		if (itemVendorCodeList != null && itemVendorCodeList.contains(skuDetails.getVendorCode())) {
			
			continue;
		}
		if (skuNumberList != null && skuNumberList.contains(skuDetails.getSkuNumber())) {
			
			continue;
		}
		if (itemSubClassList != null && itemSubClassList.contains(skuDetails.getItemSubClass())) {
			
			continue;
		}
		finalItems.add(skuDetails);
		
	}

	return finalItems;
}

private Set<String> getExcludeItems (List<SkuDetails> itemList, LoyaltyProgramExclusion loyaltyExclusion)
		throws Exception {

	List<String> itemClassList = null;
	List<String> itemDcsList = null;
	List<String> itemdeptCodeList = null;
	List<String> itemCatList = null;
	List<String> skuNumberList = null;
	List<String> itemSubClassList = null;
	List<String> itemVendorCodeList = null;

	if (loyaltyExclusion != null && loyaltyExclusion.getClassStr() != null && !loyaltyExclusion.getClassStr().trim().isEmpty()) {
		itemClassList = Arrays.asList(loyaltyExclusion.getClassStr().split(";=;"));
	}
	if (loyaltyExclusion != null && loyaltyExclusion.getDcsStr() != null && !loyaltyExclusion.getDcsStr().trim().isEmpty()) {
		itemDcsList = Arrays.asList(loyaltyExclusion.getDcsStr().split(";=;"));
	}
	if (loyaltyExclusion != null && loyaltyExclusion.getDeptCodeStr() != null && !loyaltyExclusion.getDeptCodeStr().trim().isEmpty()) {
		itemdeptCodeList = Arrays.asList(loyaltyExclusion.getDeptCodeStr().split(";=;"));
	}
	if (loyaltyExclusion != null && loyaltyExclusion.getItemCatStr() != null && !loyaltyExclusion.getItemCatStr().trim().isEmpty()) {
		itemCatList = Arrays.asList(loyaltyExclusion.getItemCatStr().split(";=;"));
	}
	if (loyaltyExclusion != null && loyaltyExclusion.getSkuNumStr() != null && !loyaltyExclusion.getSkuNumStr().trim().isEmpty()) {
		skuNumberList = Arrays.asList(loyaltyExclusion.getSkuNumStr().split(";=;"));
	}
	if (loyaltyExclusion != null && loyaltyExclusion.getSubClassStr() != null && !loyaltyExclusion.getSubClassStr().trim().isEmpty()) {
		itemSubClassList = Arrays.asList(loyaltyExclusion.getSubClassStr().split(";=;"));
	}
	if (loyaltyExclusion != null && loyaltyExclusion.getVendorStr() != null && !loyaltyExclusion.getVendorStr().trim().isEmpty()) {
		itemVendorCodeList = Arrays.asList(loyaltyExclusion.getVendorStr().split(";=;"));
	}
	Set<String> finalItems = new HashSet<String>();
	for (SkuDetails skuDetails : itemList) {
		
		if (itemCatList != null && itemCatList.contains(skuDetails.getItemCategory())) {
			finalItems.add(skuDetails.getItemSID());
			continue;
		}
		if (itemdeptCodeList != null && itemdeptCodeList.contains(skuDetails.getDepartmentCode())) {
			finalItems.add(skuDetails.getItemSID());
			continue;
		}
		if (itemClassList != null && itemClassList.contains(skuDetails.getItemClass())) {
			finalItems.add(skuDetails.getItemSID());
			continue;
		}
		if (itemDcsList != null && itemDcsList.contains(skuDetails.getDCS())) {
			finalItems.add(skuDetails.getItemSID());
			continue;
		}
		if (itemVendorCodeList != null && itemVendorCodeList.contains(skuDetails.getVendorCode())) {
			finalItems.add(skuDetails.getItemSID());
			continue;
		}
		if (skuNumberList != null && skuNumberList.contains(skuDetails.getSkuNumber())) {
			finalItems.add(skuDetails.getItemSID());
			continue;
		}
		if (itemSubClassList != null && itemSubClassList.contains(skuDetails.getItemSubClass())) {
			finalItems.add(skuDetails.getItemSID());
			continue;
		}
		//finalItems.add(skuDetails.getItemSID());
		
	}

	return finalItems;
}


private Double calculateItemDiscount(List<SkuDetails> itemList, LoyaltyProgramExclusion loyaltyExclusion) throws Exception {
	logger.info("calculateItemDiscount start");
		Double excludedAmount = 0.0;
		List<String> itemClassList = null;
		List<String> itemDcsList = null;
		List<String> itemdeptCodeList = null;
		List<String> itemCatList = null;
		List<String> skuNumberList = null;
		List<String> itemSubClassList = null;
		List<String> itemVendorCodeList = null;
		
		if(loyaltyExclusion != null && loyaltyExclusion.getClassStr() != null && !loyaltyExclusion.getClassStr().trim().isEmpty()){
			itemClassList = Arrays.asList(loyaltyExclusion.getClassStr().split(";=;"));
		}
		if(loyaltyExclusion != null && loyaltyExclusion.getDcsStr() != null && !loyaltyExclusion.getDcsStr().trim().isEmpty()){
			itemDcsList = Arrays.asList(loyaltyExclusion.getDcsStr().split(";=;"));
		}
		if(loyaltyExclusion != null && loyaltyExclusion.getDeptCodeStr() != null && !loyaltyExclusion.getDeptCodeStr().trim().isEmpty()){
			itemdeptCodeList = Arrays.asList(loyaltyExclusion.getDeptCodeStr().split(";=;"));
		}
		if(loyaltyExclusion != null && loyaltyExclusion.getItemCatStr() != null && !loyaltyExclusion.getItemCatStr().trim().isEmpty()){
			itemCatList = Arrays.asList(loyaltyExclusion.getItemCatStr().split(";=;"));
		}
		if(loyaltyExclusion != null && loyaltyExclusion.getSkuNumStr() != null && !loyaltyExclusion.getSkuNumStr().trim().isEmpty()){
			skuNumberList = Arrays.asList(loyaltyExclusion.getSkuNumStr().split(";=;"));
		}
		if(loyaltyExclusion != null && loyaltyExclusion.getSubClassStr() != null && !loyaltyExclusion.getSubClassStr().trim().isEmpty()){
			itemSubClassList = Arrays.asList(loyaltyExclusion.getSubClassStr().split(";=;"));
		}
		if(loyaltyExclusion != null && loyaltyExclusion.getVendorStr() != null && !loyaltyExclusion.getVendorStr().trim().isEmpty()){
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
		logger.info("calculateItemDiscount end");

		return excludedAmount;
	}
	
private double getCreditRewardCurrency(List<Credit> creditList) {
	double rewardCurr = 0.0;
	for (Credit credit : creditList) {
		
		
		rewardCurr += credit.getRewardCurrency() != null && !credit.getRewardCurrency().isEmpty() ? 
				Double.parseDouble(credit.getRewardCurrency()) : 0.0;
		
		
	}
	return rewardCurr;
	
}

private LoyaltyReturnTransactionResponse performRedemptnBasedReversal(List<LoyaltyTransactionChild> issTransList, List<LoyaltyTransactionChild> redempTransList, Amount amount, RequestHeader header, String amountType,
															List<SkuDetails> itemDetails, OriginalReceipt originalRecpt, String creditRedeemedAmount, 
															ResponseHeader responseHeader, String requestJson, Users user, 
															MembershipResponse response, List<Balance> balances, HoldBalance holdBalance,
															BalancesAdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers,
															boolean isIssuanceFailed, String msg, int errorCode) throws Exception{

		logger.info("performRedemptnBasedReversal method called...");
		LoyaltyReturnTransactionResponse returnTransactionResponse =  null;
		String inquiry = amount.getType()!=null && amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) ? OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY : Constants.STRING_NILL;
		Status status = null;
		//double balToCredit = Double.parseDouble(amount.getEnteredValue());
		double balToCredit = 0.0;
		Double creditRedeemedAmt = 0.0;
		if(amountType.equals(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)){
			
			creditRedeemedAmt = calculateCreditableAmount(itemDetails);
		}else if(amountType.equals(OCConstants.LOYALTY_TYPE_REVERSAL)) {
			
			if(amount.getEnteredValue() != null && !amount.getEnteredValue().trim().isEmpty() && Double.parseDouble(amount.getEnteredValue().trim()) >0){
				creditRedeemedAmt =  Double.parseDouble(amount.getEnteredValue().trim());
				
			}else{
				//changes made for //App-3440
				if(user.isRedemptionAsDiscount()) {
					creditRedeemedAmt = calculateCreditableAmount(itemDetails);
					
					//check if its a full reversal and matching to the full redemption amount
					double actualRedeemedAmount = 0.0;
					for (LoyaltyTransactionChild LoyaltyTransactionChild : redempTransList) {
						
						if(LoyaltyTransactionChild.getEnteredAmountType() != null && 
								LoyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM)){
							actualRedeemedAmount += LoyaltyTransactionChild.getEnteredAmount();
						}
					}
					//if(actualRedeemedAmount > creditRedeemedAmt) {
						String itemsInfo = Constants.STRING_NILL;
						double netPurchasedAmountdbl =0.0;
						boolean isFullReceipt = false;//APP-4592
						Double issuanceQty = 0.0;
						Double returnQty = 0.0;
						for (SkuDetails skuDetails : itemDetails) {
							for (LoyaltyTransactionChild LoyaltyTransactionChild : issTransList) {
								
								if(!LoyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE)) continue;
								itemsInfo = LoyaltyTransactionChild.getItemInfo();//.split(":");
								String[] itemsInfoArr = itemsInfo.split(Constants.DELIMETER_COMMA);
								for (String itemInfo : itemsInfoArr) {
									
									String[] item = itemInfo.split(Constants.DELIMETER_COLON+"");
									String itemSID = item[0];
									if(skuDetails.getItemSID().equals(itemSID)) {
										issuanceQty += Double.parseDouble(item[1]);
										
										/*if(Double.parseDouble(skuDetails.getQuantity())!=Double.parseDouble((qty))) {
											isFullReceipt = false;
										}*/
									}
									//if(isFullReceipt)continue;
								}
								//if(!isFullReceipt) break;
							}
							returnQty +=Double.parseDouble(skuDetails.getQuantity());
							
						}
						if(issuanceQty==returnQty) isFullReceipt=true;
						
						for (LoyaltyTransactionChild LoyaltyTransactionChild : issTransList) {
							
							if(!LoyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE)) continue;
							itemsInfo = LoyaltyTransactionChild.getItemInfo();//.split(":");
							String[] itemsInfoArr = itemsInfo.split(Constants.DELIMETER_COMMA);
							for (String itemInfo : itemsInfoArr) {
								
								String[] item = itemInfo.split(Constants.DELIMETER_COLON+"");
								String itemSID = item[0];
								String qty = item[1];
								String price = item[2];
								netPurchasedAmountdbl += Double.parseDouble(price)*(Double.parseDouble(qty));
								
							}
							
						}
						
						creditRedeemedAmt = isFullReceipt ? actualRedeemedAmount : actualRedeemedAmount*(creditRedeemedAmt/netPurchasedAmountdbl);
						//creditRedeemedAmt = actualRedeemedAmount*(creditRedeemedAmt/netPurchasedAmountdbl);
						logger.debug("creditRedeemedAmt==="+creditRedeemedAmt);
					
				}
				//}//if
				
				
			}
		}
		
		
		
		
		//Double creditRedeemedAmt = amountType.equals(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) ? calculateCreditableAmount(itemDetails) : Double.parseDouble(amount.getEnteredValue());//#TESTING
		String res = Utility.truncateUptoTwoDecimal(creditRedeemedAmt);
		if(res != null)
			balToCredit = Double.parseDouble(res);
		
		if(!creditRedeemedAmount.equalsIgnoreCase("N")) {//Changes Return flow, introduce a new check as performRedemptnBasedReversal is the last function to be called even in case for Issuance.
			
			List<Credit> credList = new ArrayList<Credit>();
			String redeemedOnIdStr = Constants.STRING_NILL;
			/*status = validateProgramAndMembership(redemptionTrans, user);
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}
			*/
			if(redempTransList != null) {
				logger.info("---No. of redemptions found for the DocSID in original receipt are ::"+redempTransList.size());
				for(LoyaltyTransactionChild redemptionTrans : redempTransList) {
					Map<String, Object> contactLtyMap = new HashMap<String, Object>();
					contactLtyMap = getContactLtyObj(redemptionTrans, user);
					if(contactLtyMap.get("flag") != null && contactLtyMap.get("flag").toString().equalsIgnoreCase("true")) continue;
					if(redeemedOnIdStr != null && !redeemedOnIdStr.isEmpty())redeemedOnIdStr += ","+redemptionTrans.getTransChildId();
					else redeemedOnIdStr = redemptionTrans.getTransChildId()+Constants.STRING_NILL;
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
					ContactsLoyalty contactsLoyalty = findLoyaltyById(redemptionTrans.getLoyaltyId(), redemptionTrans.getProgramId(),
							user.getUserId());
					LoyaltyProgram loyaltyProgram = findLoyaltyProgramByProgramId(redemptionTrans.getProgramId(), user.getUserId());

					double pointsDiff = (redemptionTrans.getPointsDifference() == null || redemptionTrans.getPointsDifference().isEmpty()) ? 0 :
						Double.parseDouble(redemptionTrans.getPointsDifference().replace("-", Constants.STRING_NILL));
					double amountDiff = (redemptionTrans.getAmountDifference() == null || redemptionTrans.getAmountDifference().isEmpty()) ? 0 : 
						Double.parseDouble(redemptionTrans.getAmountDifference().replace("-", Constants.STRING_NILL));
					double giftDiff = (redemptionTrans.getGiftDifference() == null || redemptionTrans.getGiftDifference().isEmpty()) ? 0 : 
						Double.parseDouble(redemptionTrans.getGiftDifference().replace("-", Constants.STRING_NILL));

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
							responseMap = processRedemptionCredits(redemptionTrans, header, amountType, creditRedeemedAmt, originalRecpt, responseHeader, requestJson,  user, 
									response, balances, holdBalance, additionalInfo, matchedCustomers,  isIssuanceFailed,  
									msg, contactsLoyalty, loyaltyProgram, balToCredit, pointsDiff, amountDiff, giftDiff, credList);
							if(responseMap.get("returnResponse") != null) return (LoyaltyReturnTransactionResponse) responseMap.get("returnResponse");
							else if(responseMap.get("creditList") != null) credList = (List<Credit>) responseMap.get("creditList");
							balToCredit = (Double) responseMap.get("balToCredit");
						}
						else {
							ltyAmt  = amountDiff - ltyAmt;
							ltyPts  = pointsDiff - ltyPts;
							ltyGft  = giftDiff - ltyGft;
							responseMap = processRedemptionCredits(redemptionTrans, header, amountType,creditRedeemedAmt, originalRecpt,	responseHeader, requestJson, user, 
									response, balances, holdBalance, additionalInfo, matchedCustomers,  isIssuanceFailed,  
									msg, contactsLoyalty, loyaltyProgram, balToCredit, ltyPts, ltyAmt, ltyGft, credList);
							if(responseMap.get("returnResponse") != null) return (LoyaltyReturnTransactionResponse) responseMap.get("returnResponse");
							else if(responseMap.get("creditList") != null) credList = (List<Credit>) responseMap.get("creditList");
							balToCredit = (Double) responseMap.get("balToCredit");
						}
					}
					else {
						responseMap = processRedemptionCredits(redemptionTrans, header, amountType, creditRedeemedAmt, originalRecpt,	responseHeader, requestJson, user, 
								response, balances, holdBalance, additionalInfo, matchedCustomers,  isIssuanceFailed,  
								msg, contactsLoyalty, loyaltyProgram, balToCredit, pointsDiff, amountDiff, giftDiff, credList);
						if(responseMap.get("returnResponse") != null) return (LoyaltyReturnTransactionResponse) responseMap.get("returnResponse");
						else if(responseMap.get("creditList") != null) credList = (List<Credit>) responseMap.get("creditList");
						logger.info(" after processRedemptionCredits credList"+credList.get(0).getRewardPoints()+credList.get(0).getRewardCurrency()+credList.get(0).getGift());
						balToCredit = (Double) responseMap.get("balToCredit");
					}
					if(balToCredit == 0) break;
				}//for	
			}// if
			logger.info("Error Code"+errorCode);
			logger.info("Balance"+balToCredit);
			if(balToCredit ==  creditRedeemedAmt && isIssuanceFailed) {
				logger.info("=========1==========");
				if(isRedemptionExists) {
					status = new Status("111570", PropertyUtil.getErrorMessage(111570, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				else {
					status = new Status(errorCode+Constants.STRING_NILL, msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
				return returnTransactionResponse;
			}
			else if(balToCredit ==  creditRedeemedAmt && !isIssuanceFailed) {
				logger.info("=========2==========");
				status = new Status("0", msg , OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
				return returnTransactionResponse;
			}
			else if(balToCredit !=  creditRedeemedAmt && isIssuanceFailed) {
				logger.info("=========3==========");
				msg += " Return "+inquiry+" was successful on redemption.";
				status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				additionalInfo = new BalancesAdditionalInfo();
				additionalInfo.setDebit(new Debit());
				additionalInfo.setCredit(credList);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
				return returnTransactionResponse;
			}
			else if(balToCredit !=  creditRedeemedAmt && !isIssuanceFailed){
				logger.info("=========4==========");
				status = new Status("0", "Return "+inquiry+" was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				additionalInfo = new BalancesAdditionalInfo();
				additionalInfo.setCredit(credList);//?
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
				return returnTransactionResponse;
			}
		}
		else {
			logger.info("---No redemptions found for the DocSID in original receipt.");
			if(isIssuanceFailed) {
				status = new Status(errorCode+Constants.STRING_NILL, msg , OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
			}
			else {
				status = new Status("0", msg , OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
			}
		}
		logger.info("performRedemptnBasedReversal method ended");
		return returnTransactionResponse;
	}//performRedemptnBasedReversal

	private Map<String, Object> processRedemptionCredits(LoyaltyTransactionChild redemptionTrans, 
			RequestHeader header, String amountType,Double creditableAmount, OriginalReceipt originalRecpt, ResponseHeader responseHeader, 
			String requestJson, Users user, MembershipResponse response, List<Balance> balances,
			HoldBalance holdBalance, BalancesAdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers, 
			boolean isIssuanceFailed, String msg, ContactsLoyalty contactsLoyalty, LoyaltyProgram loyaltyProgram, 
			double balToCredit, double pointsDiff, double amountDiff, double giftDiff, List<Credit> credList) throws Exception {

		logger.info("processRedemptionCredits method start==="+balToCredit);
		LoyaltyReturnTransactionResponse returnTransactionResponse =  null;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("contactsLoyaltyDaoForDML");
		Map<String, Object> responseMap = new HashMap<String, Object>();
		
		double earnedPts = 0;
		String creditedPoints = Constants.STRING_NILL;
		String creditedReward = Constants.STRING_NILL;
		String creditedGift = Constants.STRING_NILL;
		
		String description = Constants.STRING_NILL;

		Double pointsBal = contactsLoyalty.getLoyaltyBalance() == null ? 0 : contactsLoyalty.getLoyaltyBalance();
		Double ltyCurrBal = contactsLoyalty.getGiftcardBalance() == null ? 0 : contactsLoyalty.getGiftcardBalance();
		Double giftBal = contactsLoyalty.getGiftBalance() ==  null ? 0 : contactsLoyalty.getGiftBalance();

		if(redemptionTrans.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM)) {

			if(contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L)) {
				logger.info("--- In loyaty amount redemption ---");
				if(amountDiff != 0 && amountDiff >= balToCredit){
					//contactsLoyalty.setGiftcardBalance(ltyCurrBal + balToCredit);
					contactsLoyalty.setGiftcardBalance(new BigDecimal(ltyCurrBal + balToCredit).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
					creditedReward = balToCredit+Constants.STRING_NILL;
					logger.info("creditedReward and balToCredit in ====1==="+creditedReward+" "+balToCredit);
					description = balToCredit+";=;"+balToCredit+";=;"+0+";=;"+0;
					createTransctnAndExpiry(header, amountType, creditableAmount, originalRecpt, contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, 
							balToCredit, earnedPts, OCConstants.LOYALTY_TYPE_AMOUNT, description,redemptionTrans.getTransChildId()); 
					returnTransactionResponse = prepareRedmptnSuccessResponse(responseHeader, requestJson, 
							user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
							isIssuanceFailed, msg, loyaltyProgram, contactsLoyalty, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM
							,creditedPoints, creditedReward, creditedGift, credList);
					responseMap.put("returnResponse", returnTransactionResponse);
					return responseMap;
				}
				else {
					if(amountDiff != 0) {
						balToCredit = balToCredit - amountDiff;
						//contactsLoyalty.setGiftcardBalance(ltyCurrBal + amountDiff);
						contactsLoyalty.setGiftcardBalance(new BigDecimal(ltyCurrBal + amountDiff).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
						creditedReward = amountDiff+Constants.STRING_NILL;
						logger.info("creditedReward and amount diff in ====2==="+creditedReward+" "+amountDiff);
					}
					double autoCnvrtPtsCredit = 0;
					double autoCnvrtPts = 0;
					Double amtFactor = 0.0;
					Double ptsFactor = 0.0; 
					if(pointsDiff !=0 && redemptionTrans.getConversionAmt() != null && redemptionTrans.getConversionAmt() > 0) {
						String[] convrsnRuleArray = null;
						convrsnRuleArray = redemptionTrans.getDescription().split(" Points -> ");
						amtFactor = Double.parseDouble(convrsnRuleArray[1])/Double.parseDouble(convrsnRuleArray[0]);
						ptsFactor = Double.parseDouble(convrsnRuleArray[0])/Double.parseDouble(convrsnRuleArray[1]);
						autoCnvrtPtsCredit = pointsDiff * amtFactor;
					}
					if(autoCnvrtPtsCredit >= balToCredit) {
						autoCnvrtPts = Math.round(balToCredit * ptsFactor);
						contactsLoyalty.setLoyaltyBalance(pointsBal + autoCnvrtPts);
						if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
						creditedPoints = (long)autoCnvrtPts+Constants.STRING_NILL;
						logger.info("creditedPoints and autoCnvrtPts ====3==="+creditedReward+" "+autoCnvrtPts);
						creditedReward = autoCnvrtPtsCredit+"";
						logger.info("creditedReward and autoCnvrtPtsCredit ====3==="+creditedReward+" "+autoCnvrtPtsCredit);
						description = amountDiff+balToCredit+";=;"+amountDiff+";=;"+autoCnvrtPts+";=;"+0;
						createTransctnAndExpiry(header, amountType, creditableAmount,originalRecpt,contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,
								amountDiff, autoCnvrtPts, OCConstants.LOYALTY_TYPE_POINTS+"/"+OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId()); 
						returnTransactionResponse = prepareRedmptnSuccessResponse(responseHeader, requestJson, 
								user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
								isIssuanceFailed, msg, loyaltyProgram, contactsLoyalty,
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM, creditedPoints, creditedReward, creditedGift, credList);
						responseMap.put("returnResponse", returnTransactionResponse);
						return responseMap;
					}
					else {
						if(pointsDiff != 0 || amountDiff != 0) {
							logger.info("points diff and amount diff "+pointsDiff+" "+amountDiff);
							balToCredit = balToCredit - autoCnvrtPtsCredit;
							autoCnvrtPts = Math.round(autoCnvrtPtsCredit * ptsFactor);
							contactsLoyalty.setLoyaltyBalance(pointsBal + autoCnvrtPts);;
							if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
							creditedPoints = (long)autoCnvrtPts+Constants.STRING_NILL;
							logger.info("creditedPoints and autoCnvrtPts ====3==="+creditedReward+" "+autoCnvrtPts);
							//creditedReward = autoCnvrtPtsCredit+"";
							if(pointsDiff!=0) creditedReward = autoCnvrtPtsCredit+"";
							logger.info("creditedReward and autoCnvrtPtsCredit ====3==="+creditedReward+" "+autoCnvrtPtsCredit);
							description = amountDiff+autoCnvrtPtsCredit+";=;"+amountDiff+";=;"+autoCnvrtPts+";=;"+0;
							createTransctnAndExpiry(header, amountType,creditableAmount, originalRecpt,contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, 
									amountDiff, autoCnvrtPts, OCConstants.LOYALTY_TYPE_POINTS+"/"+OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId());
						}
						//prepare credit object
						logger.info("before prepareCreditObject calling>>>>>"+creditedPoints+creditedReward+creditedGift);
						Credit credit = prepareCreditObject(contactsLoyalty, creditedPoints, creditedReward, creditedGift, user);
						credList.add(credit);
						logger.info("after prepareCreditObject calling>>>>>"+credit.getRewardPoints()+credit.getRewardCurrency()+credit.getGift()+" and credit list size"+credList.size());
						responseMap.put("creditList",credList);
						responseMap.put("balToCredit",balToCredit);
					}
				}
			}//Loyalty card
			else if(contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL)) {

				logger.info("--- In giftLoyalty card redemption ---");
				if(amountDiff != 0 && amountDiff >= balToCredit){
					//contactsLoyalty.setGiftcardBalance(ltyCurrBal + balToCredit);
					contactsLoyalty.setGiftcardBalance(new BigDecimal(ltyCurrBal + balToCredit).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
					creditedReward = balToCredit+Constants.STRING_NILL;
					description = balToCredit+";=;"+balToCredit+";=;"+0+";=;"+0;
					createTransctnAndExpiry(header, amountType, creditableAmount, originalRecpt, contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, balToCredit,
							earnedPts, OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId()); 
					returnTransactionResponse = prepareRedmptnSuccessResponse( responseHeader, requestJson, 
							user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
							isIssuanceFailed, msg, loyaltyProgram, contactsLoyalty, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM,
							creditedPoints, creditedReward, creditedGift, credList);
					responseMap.put("returnResponse", returnTransactionResponse);
					return responseMap;
				}
				else {
					if(amountDiff != 0) {
						balToCredit = balToCredit - amountDiff;
						creditedReward = amountDiff+Constants.STRING_NILL;
						//contactsLoyalty.setGiftcardBalance(ltyCurrBal + amountDiff);
						contactsLoyalty.setGiftcardBalance(new BigDecimal(ltyCurrBal + amountDiff).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
					}
					double autoCnvrtPtsCredit = 0;
					double autoCnvrtPts = 0;
					Double amtFactor = 0.0;
					Double ptsFactor = 0.0; 
					if(pointsDiff !=0 && redemptionTrans.getConversionAmt() != null && redemptionTrans.getConversionAmt() != 0) {
						String[] convrsnRuleArray = null;
						convrsnRuleArray = redemptionTrans.getDescription().split(" Points -> ");
						ptsFactor = Double.parseDouble(convrsnRuleArray[0])/Double.parseDouble(convrsnRuleArray[1]);
						amtFactor = Double.parseDouble(convrsnRuleArray[1])/Double.parseDouble(convrsnRuleArray[0]);
						autoCnvrtPtsCredit = pointsDiff * amtFactor;
					}
					if(autoCnvrtPtsCredit >= balToCredit) {
						autoCnvrtPts = Math.round(balToCredit * ptsFactor);
						contactsLoyalty.setLoyaltyBalance(pointsBal + autoCnvrtPts);;
						if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
						creditedPoints = (long)autoCnvrtPts+Constants.STRING_NILL;
						creditedReward = autoCnvrtPtsCredit+"" ;
						description = amountDiff+balToCredit+";=;"+amountDiff+";=;"+autoCnvrtPts+";=;"+0;
						createTransctnAndExpiry(header, amountType, creditableAmount, originalRecpt,contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,
								amountDiff, autoCnvrtPts, OCConstants.LOYALTY_TYPE_POINTS+"/"+OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId()); 
						returnTransactionResponse = prepareRedmptnSuccessResponse( responseHeader, requestJson, 
								user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
								isIssuanceFailed, msg, loyaltyProgram, contactsLoyalty,
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM, creditedPoints, creditedReward, creditedGift, credList);
						responseMap.put("returnResponse", returnTransactionResponse);
						return responseMap;
					}
					else {
						if(pointsDiff != 0) {
							balToCredit = balToCredit - autoCnvrtPtsCredit;
							autoCnvrtPts = Math.round(autoCnvrtPtsCredit * ptsFactor);
							contactsLoyalty.setLoyaltyBalance(pointsBal + autoCnvrtPts);;
							if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))	contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
							creditedPoints = (long)autoCnvrtPts+Constants.STRING_NILL;
							creditedReward = autoCnvrtPtsCredit+"" ;
							
						}
						if(giftDiff != 0 && giftDiff >= balToCredit){
							//contactsLoyalty.setGiftBalance(giftBal + balToCredit);
							contactsLoyalty.setGiftBalance(new BigDecimal(giftBal + balToCredit).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
							creditedGift = balToCredit+Constants.STRING_NILL;
							description = amountDiff+autoCnvrtPtsCredit+balToCredit+";=;"+amountDiff+";=;"+autoCnvrtPts+";=;"+balToCredit;
							createTransctnAndExpiry(header, amountType,creditableAmount, originalRecpt, contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, amountDiff+balToCredit,
									autoCnvrtPts, OCConstants.LOYALTY_TYPE_POINTS+"/"+OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId()); 
							returnTransactionResponse = prepareRedmptnSuccessResponse( responseHeader, requestJson, 
									user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
									isIssuanceFailed, msg, loyaltyProgram, contactsLoyalty, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM,
									creditedPoints, creditedReward, creditedGift, credList);
							responseMap.put("returnResponse", returnTransactionResponse);
							return responseMap;
						}
						else {
							if(giftDiff != 0 || pointsDiff != 0 || amountDiff != 0) {
								balToCredit = balToCredit - giftDiff;
								//contactsLoyalty.setGiftBalance(giftBal + giftDiff);
								contactsLoyalty.setGiftBalance(new BigDecimal(giftBal + giftDiff).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
								creditedGift = giftDiff+Constants.STRING_NILL;
								description = amountDiff+autoCnvrtPtsCredit+giftDiff+";=;"+amountDiff+";=;"+autoCnvrtPts+";=;"+giftDiff;
								createTransctnAndExpiry(header, amountType, creditableAmount,originalRecpt, contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, 
										amountDiff+giftDiff, autoCnvrtPts, OCConstants.LOYALTY_TYPE_POINTS+"/"+OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId());
							}
						}
						//prepare credit object
						Credit credit = prepareCreditObject(contactsLoyalty,  creditedPoints, creditedReward, creditedGift, user);
						credList.add(credit);
						responseMap.put("creditList",credList);
						responseMap.put("balToCredit",balToCredit);
					}
				}
			}//GiftLoyalty-card
			else if(contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
				logger.info("--- In gift card redemption ---");
				if(giftDiff >= balToCredit){
					//contactsLoyalty.setGiftBalance(giftBal + balToCredit);
					contactsLoyalty.setGiftBalance(new BigDecimal(giftBal + balToCredit).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
					creditedGift = balToCredit+Constants.STRING_NILL;
					description = balToCredit+";=;"+0+";=;"+0+";=;"+balToCredit;
					createTransctnAndExpiry(header, amountType,creditableAmount, originalRecpt, contactsLoyalty,user,responseHeader,
										OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, balToCredit, earnedPts, OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId()); 
					returnTransactionResponse = prepareRedmptnSuccessResponse( responseHeader, requestJson, 
							user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
							isIssuanceFailed, msg, loyaltyProgram, contactsLoyalty, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM
							,creditedPoints, creditedReward, creditedGift, credList);
					responseMap.put("returnResponse", returnTransactionResponse);
					return responseMap;
				}
				else {
					balToCredit = balToCredit - giftDiff;
					//contactsLoyalty.setGiftBalance(giftBal + giftDiff);
					contactsLoyalty.setGiftBalance(new BigDecimal(giftBal + giftDiff).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
					description = giftDiff+";=;"+0+";=;"+0+";=;"+giftDiff;
					createTransctnAndExpiry(header, amountType,creditableAmount, originalRecpt, contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, giftDiff, 
							earnedPts,  OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId());

					//prepare credit object
					creditedGift = giftDiff+Constants.STRING_NILL;
					Credit credit = prepareCreditObject(contactsLoyalty, creditedPoints, creditedReward, creditedGift, user);
					credList.add(credit);
					responseMap.put("creditList",credList);
					responseMap.put("balToCredit",balToCredit);
				}
			}//Gift-card
		}
		logger.info("processRedemptionCredits method start");
		return responseMap;
	}

	
	
	private Credit prepareCreditObject(ContactsLoyalty contactsLoyalty, String creditedPoints, 
			String creditedReward, String creditedGift, Users user) {
		logger.info("prepareCreditObject method called...");
		Credit credit = new Credit();
		credit.setMembershipNumber(contactsLoyalty.getCardNumber()+Constants.STRING_NILL);
		credit.setRewardPoints(user.isRedemptionAsDiscount() ? "0" : creditedPoints);
		credit.setRewardCurrency(user.isRedemptionAsDiscount() ? "0" : creditedReward);
		credit.setGift(user.isRedemptionAsDiscount() ? "0" :creditedGift);
		logger.info("prepareCreditObject method exit...");
		return credit;
	}//prepareCreditObject

	private void createTransctnAndExpiry(RequestHeader header, String amountType ,Double creditableAmountGiven, OriginalReceipt originalRecpt, ContactsLoyalty contactsLoyalty,
											Users user, ResponseHeader responseHeader, String rewardFlag, 
											double earnedAmount, double earnedPoints, String earnType, String description, Long redeemedOn) {
		
		logger.info("createTransctnAndExpiry method called...");
		if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)) return;
		long pointsDifference = 0;
		//long amountDifference = 0;
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
		if(amountType !=null && !amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)) {
			
			LoyaltyTransactionChild childTx = createReturnTransaction(header, amountType, originalRecpt, contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
					Constants.STRING_NILL+pointsDifference,Constants.STRING_NILL+amountDifference,earnedAmount,earnedPoints, earnType,responseHeader.getTransactionId(),
					creditableAmountGiven,
					OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL, description, redeemedOn,null, null,Constants.STRING_NILL, null );
			
			//Expiry transaction
			createExpiryTransaction(contactsLoyalty, earnedAmount, (long)earnedPoints, childTx.getTransChildId(), rewardFlag, amountType);
		}
		logger.info("createTransctnAndExpiry method exit....");
	}//createTransctnAndExpiry

	private LoyaltyReturnTransactionResponse prepareRedmptnSuccessResponse(ResponseHeader responseHeader, String requestJson, Users user,
													MembershipResponse response, List<Balance> balances, HoldBalance holdBalance, BalancesAdditionalInfo additionalInfo, 
													List<MatchedCustomer> matchedCustomers, boolean isIssuanceFailed, String msg, LoyaltyProgram loyaltyProgram, 
													ContactsLoyalty contactsLoyalty, String enteredAmountType, 
													String creditedPoints, String creditedReward, String creditedGift, List<Credit> credList) throws Exception{
		
		logger.info("prepareRedmptnSuccessResponse method called...");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;
		String inquiry = enteredAmountType!=null && enteredAmountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) ? OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY: Constants.STRING_NILL; 
		if(isIssuanceFailed) {
			msg += " Return "+inquiry+" was successful on redemption.";
		}
		else {
			msg = "Return "+inquiry+" was successful.";
		}
		additionalInfo = prepareCreditsAdditionalInfo(additionalInfo, OCConstants.LOYALTY_TYPE_REVERSAL, contactsLoyalty.getRewardFlag(),
													creditedPoints, creditedReward, creditedGift, contactsLoyalty.getCardNumber(), enteredAmountType, credList, user);
		status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		logger.info("msg==>"+msg);
		returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
		logger.info("prepareRedmptnSuccessResponse method exit...");
		return returnTransactionResponse;
	}//prepareRedmptnSuccessResponse

	private Map<String, Object> getContactLtyObj(LoyaltyTransactionChild redemptionTrans, Users user) throws Exception {
		
		logger.info("getContactLtyObj method called...");
		Map<String, Object> conMap = new HashMap<String, Object>();
		//check if program is active and card-set
		LoyaltyProgram loyaltyProgram = null;
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
		}
		
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
		conMap.put("program", loyaltyProgram);
		logger.info("getContactLtyObj method exit...");
		return conMap;
	}//getContactLtyObj

	//Changes start 2.5.5.0
	private Status validateProgramAndMembership(LoyaltyTransactionChild redemptionTrans, Users user) throws Exception {
		logger.info("validateProgramAndMembership method start...");
		Status status =null; 
		//check if program is active and card-set
				LoyaltyProgram loyaltyProgram = null;
				loyaltyProgram = findLoyaltyProgramByProgramId(redemptionTrans.getProgramId(), user.getUserId());
				if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus())){
					status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);				
					}
				if(loyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)) {
					LoyaltyCardSet loyaltyCardSet = null;
					loyaltyCardSet = findLoyaltyCardSetByCardsetId(redemptionTrans.getCardSetId(), user.getUserId());
					if(loyaltyCardSet == null || !OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE.equals(loyaltyCardSet.getStatus())){
						status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);				
					}
				}
				
				ContactsLoyalty contactsLoyalty = findLoyaltyById(redemptionTrans.getLoyaltyId(), redemptionTrans.getProgramId(),
																					user.getUserId());
				if(contactsLoyalty == null){
					status = new Status("1000", PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
					status = new Status("111517", PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
					status = new Status("111539", PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				} 
			    if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
					status = new Status("111540", PropertyUtil.getErrorMessage(111570, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			    }				
				logger.info("validateProgramAndMembership method start...");
	
		return status;
	}
	//Changes end 2.5.5.0
	
	private LoyaltyReturnTransactionResponse performIssuanceBasedReversal(LoyaltyTransactionChild loyaltyIssuanceTransactionChild, 
			RequestHeader header, String amountType,
				OriginalReceipt originalRecpt,List<SkuDetails> itemList, ResponseHeader responseHeader, 
				String requestJson, Users user, String mode,String key, ContactsLoyalty contactsLoyalty, Amount amount,
				List<LoyaltyTransactionChild> rewardIssuanceList, boolean isFullRcpt, double creditRewardCurrency,double actualReturnAmnt) throws Exception {
		
		Status status=null;
		logger.info("performIssuanceBasedReversal method called...");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		
		/*ContactsLoyalty contactsLoyalty = findLoyaltyById(loyaltyTransactionChild.getLoyaltyId(), loyaltyTransactionChild.getProgramId(),
				user.getUserId());*/
		//check if program is active and card-set
		LoyaltyProgram loyaltyProgram = null;
		loyaltyProgram = findLoyaltyProgramByProgramId(contactsLoyalty.getProgramId(), user.getUserId());
		if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus())){
			
			status = new Status("111585", PropertyUtil.getErrorMessage(111585, OCConstants.ERROR_LOYALTY_FLAG) , OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
		
			/*LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(originalRecpt.getDocSID(), 
					user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION);
			returnTransactionResponse = performRedemptnBasedReversal(redempTransList, header, amount, originalRecpt, creditReddemedAmount, responseHeader, requestJson, user, 
					null, null, null, null, null, true, PropertyUtil.getErrorMessage(111585, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".",111585,true);*/
			return returnTransactionResponse;
		}

		if(loyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)) {
			LoyaltyCardSet loyaltyCardSet = null;
			loyaltyCardSet = findLoyaltyCardSetByCardsetId(contactsLoyalty.getCardSetId(), user.getUserId());
			if(loyaltyCardSet == null || !OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE.equals(loyaltyCardSet.getStatus())){
	
				status = new Status("111591", PropertyUtil.getErrorMessage(111591, OCConstants.ERROR_LOYALTY_FLAG) , OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);	
				
				return returnTransactionResponse;
			}
		}

		if(contactsLoyalty != null &&( contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED))){
			LoyaltyProgramTier tier = null;
			
			
			List<Balance> balances = null;
			List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
			
			String message = Constants.STRING_NILL;
			int errorCode = 0;
			if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
				balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				contactLoyaltyList.add(contactsLoyalty);
				if(contactsLoyalty.getProgramTierId() != null)	tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				errorCode = 111517;
			}
			else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
				contactLoyaltyList.add(contactsLoyalty);
				balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				if(contactsLoyalty.getProgramTierId() != null)	tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				 message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				 errorCode = 111539;
			}else if( contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
				ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactsLoyalty);
				String maskedNum = Constants.STRING_NILL;
				if(destLoyalty != null) {
					contactLoyaltyList.add(destLoyalty);
					contactsLoyalty = destLoyalty;
					tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
					balances = prepareBalancesObject(destLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
					maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
					
				}
				 message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+ maskedNum+".";
				 errorCode = 111578;
			}
			List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
			MembershipResponse response = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
			
			status = new Status(errorCode+Constants.STRING_NILL, message , OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, null, null, matchedCustomers, status);
			
			return returnTransactionResponse;
		}
		
		
		Double itemExcludedAmount = 0.0;
		Double returnedAmount = calculateReturnAmount(itemList );
		
		double netReturnedAmountdbl = 0.0;//this is to rollback
		Double returnedAmountdbl = 0.0;//this is for CRV
		String res = Utility.truncateUptoTwoDecimal(returnedAmount- ((amount.getRequestedType()!= null && amount.getRequestedType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VOID) || user.isRedemptionAsDiscount())? 0 : creditRewardCurrency));
		LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
		if(res != null){
			
			 returnedAmountdbl = Double.parseDouble(res);
			logger.info("Return value "+returnedAmountdbl);
			if(loyaltyExclusion != null && (loyaltyExclusion.getClassStr() != null || loyaltyExclusion.getDcsStr() != null ||
					loyaltyExclusion.getDeptCodeStr() != null || loyaltyExclusion.getItemCatStr() != null ||
					loyaltyExclusion.getSkuNumStr() != null || loyaltyExclusion.getSubClassStr() != null ||
					loyaltyExclusion.getVendorStr() != null)){
				itemExcludedAmount = calculateItemDiscount(itemList , loyaltyExclusion);
			}
			if(loyaltyIssuanceTransactionChild.getEnteredAmount() < returnedAmountdbl ) {
				
				returnedAmountdbl = loyaltyIssuanceTransactionChild.getEnteredAmount();// != null ? loyaltyIssuanceTransactionChild.getEnteredAmount()-loyaltyIssuanceTransactionChild.getExcludedAmount();//-itemExcludedAmount;
				/*status = new Status("111567", PropertyUtil.getErrorMessage(111567, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;*/
				
			}
			
		}
		List<LoyaltyTransactionChild> returnList = null;
		if(loyaltyIssuanceTransactionChild != null){
			
			key=genKey(loyaltyIssuanceTransactionChild.getDocSID(),loyaltyIssuanceTransactionChild.getReceiptNumber(),
					loyaltyIssuanceTransactionChild.getStoreNumber(), loyaltyIssuanceTransactionChild.getSubsidiaryNumber());
			logger.info("issTransList.get(0).getLoyaltyId()==>"+loyaltyIssuanceTransactionChild.getLoyaltyId());
			String docSID = "OR-"+originalRecpt.getDocSID();
			logger.info("key==>"+key);
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			 returnList = loyaltyTransactionChildDao.getTotReversalAmt(user.getUserId(), 
					docSID, OCConstants.LOYALTY_TRANS_TYPE_RETURN,key);
			double totReturnAmt = 0.0; 
			if(returnList != null && returnList.size() >0) {
				for (LoyaltyTransactionChild returnObj : returnList) {
					totReturnAmt += returnObj.getDescription().contains(";=;") ? Double.parseDouble(returnObj.getDescription().split(";=;")[0]): Double.parseDouble(returnObj.getDescription());
				}
			}
			logger.debug("loyaltyIssuanceTransactionChild ID==="+loyaltyIssuanceTransactionChild.getTransChildId().longValue());
			double totIssuedAmt = loyaltyIssuanceTransactionChild.getEnteredAmount();//this is the total transacted amount
			logger.info("retuenedAmt ::"+returnedAmount);
			//logger.info("creditRewardCurrency ::"+creditRewardCurrency);
			logger.info("retuenedAmt ::"+totReturnAmt);
			logger.info("Issued Amount::"+totIssuedAmt);
			//if(returnedAmount >= totIssuedAmt) creditRewardCurrency = 0;
			double diffAmt = totIssuedAmt - totReturnAmt;
			logger.info("diffAmt ::"+diffAmt);
			
			
			if(diffAmt == 0 ) {//since enteredamount is the total trx amount n tht it is already reached including SP
				//if(rewardIssuanceList == null) {
				if( creditRewardCurrency > 0){//redemption reversal was successful
					
					String msg = "";
					String inquiry = amountType!=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) ? OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY: Constants.STRING_NILL; 
					
					msg += " Return "+inquiry+" was successful on redemption.";
					status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;

				}else{
					
					if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)) {//APP-4624
						double CRV = contactsLoyalty.getCummulativeReturnValue() == null  ? 0 : contactsLoyalty.getCummulativeReturnValue();
						contactsLoyalty.setCummulativeReturnValue(CRV+actualReturnAmnt);
						saveContactsLoyalty(contactsLoyalty); 
					}
					
					status = new Status("111604", PropertyUtil.getErrorMessage(111604, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
				//}
			}
			if( returnedAmountdbl > diffAmt){
				returnedAmountdbl = diffAmt;
				//logger.debug("already reached ==="+);
			}
			logger.debug("actual returned amount ==="+returnedAmountdbl);
			//calculate return loyalty amount for the regular issuance trx. 
			
			//Double returnedAmountdbl = calculateReturnAmount(itemList );
			netReturnedAmountdbl = returnedAmountdbl - itemExcludedAmount;
			String itemsInfo = loyaltyIssuanceTransactionChild.getItemInfo();
			Set<String> excludeItems = getExcludeItems(itemList, loyaltyExclusion);
			logger.debug("excludeItems ==="+excludeItems.size());
			StringBuilder ItemInfo = new StringBuilder(Constants.STRING_NILL);
			if(itemsInfo != null && !itemsInfo.isEmpty()){
				double itemExcludeAmount= 0.0;
				netReturnedAmountdbl =0.0;
				String[] itemsInfoArr = itemsInfo.split(Constants.DELIMETER_COMMA);
				for (String itemInfo : itemsInfoArr) {
					
					String[] item = itemInfo.split(Constants.DELIMETER_COLON+"");
					String itemSID = item[0];
					String qty = item[1];
					String price = item[2];
					for (SkuDetails returnedItem : itemList) {
						
						if(returnedItem.getItemSID() != null && returnedItem.getItemSID().equals(itemSID)) {
							if(ItemInfo.length() > 0) ItemInfo.append(Constants.DELIMETER_COMMA);
							ItemInfo.append(returnedItem.getItemSID()+Constants.DELIMETER_COLON+returnedItem.getQuantity()+Constants.DELIMETER_COLON+returnedItem.getBilledUnitPrice()) ;
							netReturnedAmountdbl += (Double.parseDouble(returnedItem.getBilledUnitPrice()))*(Double.parseDouble(returnedItem.getQuantity()));
							break;
						}
						
					}
					
				}
				for (String itemInfo : itemsInfoArr) {
					
					String[] item = itemInfo.split(Constants.DELIMETER_COLON+"");
					String itemSID = item[0];
					String qty = item[1];
					String price = item[2];
					for (SkuDetails returnedItem : itemList) {
						
						if(returnedItem.getItemSID() != null && returnedItem.getItemSID().equals(itemSID) && !excludeItems.isEmpty() && excludeItems.contains(returnedItem.getItemSID())) {
							
							itemExcludeAmount += (Double.parseDouble(returnedItem.getBilledUnitPrice()))*(Double.parseDouble(returnedItem.getQuantity()));
							break;
						}
						
					}
					
				}
				logger.debug("returnedAmountdbl ==="+netReturnedAmountdbl+" itemExcludeAmount=="+itemExcludeAmount);
				if(netReturnedAmountdbl > 0) netReturnedAmountdbl = netReturnedAmountdbl-itemExcludeAmount;
				if(netReturnedAmountdbl > returnedAmountdbl ) netReturnedAmountdbl = returnedAmountdbl;
				logger.debug("netReturnedAmountdbl ==="+netReturnedAmountdbl);
				
			}else{
				//netReturnedAmountdbl = returnedAmountdbl-itemExcludeAmount;
				/*if(rewardIssuanceList ==null) {
					
					Double itemExcludedAmount = 0.0;
					Double returnedAmount = calculateReturnAmount(itemList );
					String res = Utility.truncateUptoTwoDecimal(returnedAmount);
					if(res != null)
						returnedAmountdbl = Double.parseDouble(res);
					logger.info("Return value "+returnedAmountdbl);
					if(returnedAmountdbl >= diffAmt){
						returnedAmountdbl = diffAmt;
					}
					itemExcludedAmount = calculateItemDiscount(itemList, loyaltyExclusion);
					netReturnedAmountdbl = returnedAmountdbl - itemExcludedAmount;
				}*/
				
				
			}
			List<SkuDetails> finalItems = itemList;

			if (loyaltyExclusion == null || (loyaltyExclusion.getClassStr() == null && loyaltyExclusion.getDcsStr() == null
					&& loyaltyExclusion.getDeptCodeStr() == null && loyaltyExclusion.getItemCatStr() == null
					&& loyaltyExclusion.getSkuNumStr() == null && loyaltyExclusion.getSubClassStr() == null
					&& loyaltyExclusion.getVendorStr() == null)) {
			} else {
				finalItems = getFinalItems(itemList, loyaltyExclusion);
			}
			itemList = finalItems;//issuanceRequest.setItems(finalItems);//exclude the items which are in exclusions
			
			
			
			//double netReturnedAmount = Math.round(netReturnedAmountdbl);
			double netReturnedAmount = Double.parseDouble(Utility.truncateUptoTwoDecimal(netReturnedAmountdbl));
			//deduct the qty for that 
			String itemRewardsInfo = loyaltyIssuanceTransactionChild.getItemRewardsInfo();
						
			if(netReturnedAmount <= 0 && rewardIssuanceList == null && 
					(itemRewardsInfo== null || itemRewardsInfo.isEmpty())) {//APP-2114
				
				if( creditRewardCurrency > 0){//redemption reversal was successful
					
					String msg = "";
					String inquiry = amountType!=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) ? OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY: Constants.STRING_NILL; 
					
					msg += " Return "+inquiry+" was successful on redemption.";
					status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;

				}else{
					
					if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)) {//APP-4624
						double CRV = contactsLoyalty.getCummulativeReturnValue() == null  ? 0 : contactsLoyalty.getCummulativeReturnValue();
						contactsLoyalty.setCummulativeReturnValue(CRV+actualReturnAmnt);
						saveContactsLoyalty(contactsLoyalty); 
					}
					
					status = new Status("111604", PropertyUtil.getErrorMessage(111604, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
			}
			
			/*if(loyaltyIssuanceTransactionChild.getEnteredAmount() < netReturnedAmount ) {
				status = new Status("111567", PropertyUtil.getErrorMessage(111567, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
				
			}*/
			returnTransactionResponse = performReversalOperation(loyaltyIssuanceTransactionChild, header, amountType, originalRecpt,  responseHeader, loyaltyProgram,
					contactsLoyalty, user, itemList, (netReturnedAmount), returnedAmountdbl,key, rewardIssuanceList, isFullRcpt, returnList, ItemInfo.toString(),actualReturnAmnt);
			
		}else{
			//this will never be executed
			if(rewardIssuanceList != null){
				returnTransactionResponse = performReversalOperation(null, header, amountType, originalRecpt,  responseHeader, loyaltyProgram,
						contactsLoyalty, user, itemList, 0.0, 0.0,key, rewardIssuanceList , isFullRcpt, returnList,  "",actualReturnAmnt);
				
			}
			else{
				status = new Status("111566", PropertyUtil.getErrorMessage(111566, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}
			
		}
		
		logger.info("performIssuanceBasedReversal method exit...");
		return returnTransactionResponse;
	}//performIssuanceBasedReversal

	private ContactsLoyalty findLoyaltyById(Long loyaltyId,Long programId, Long userId) throws Exception {
		logger.info("findLoyaltyById start");
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		logger.info("findLoyaltyById end");
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
					matchedCustomer.setMembershipNumber(Constants.STRING_NILL+loyalty.getCardNumber());
					matchedCustomer.setFirstName(contact.getFirstName() == null ? Constants.STRING_NILL : contact.getFirstName().trim());
					matchedCustomer.setLastName(contact.getLastName() == null ? Constants.STRING_NILL : contact.getLastName().trim());
					matchedCustomer.setCustomerId(contact.getExternalId() == null ? Constants.STRING_NILL : contact.getExternalId());
					matchedCustomer.setEmailAddress(contact.getEmailId() == null ? Constants.STRING_NILL : contact.getEmailId());
					matchedCustomer.setPhone(contact.getMobilePhone() == null ? Constants.STRING_NILL : contact.getMobilePhone());
					matchedCustList.add(matchedCustomer);
				}
			}
		}
		logger.info("prepareMatchedCustomers method exit...");
		return matchedCustList;
	}//prepareMatchedCustomers

	private LoyaltyReturnTransactionResponse cardBasedReturnTransaction(LoyaltyReturnTransactionRequest request,RequestHeader header, Amount amount, OriginalReceipt originalRecpt,
			String cardNumber, ResponseHeader responseHeader, Users user, String requestJson) throws Exception{

		logger.info(">>> Entered Card based return transaction");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;

		LoyaltyCards loyaltyCard = findLoyaltyCardByUserId(cardNumber, user.getUserId());

		if(loyaltyCard == null){
			status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;
		}

		LoyaltyProgram loyaltyProgram = null;

		loyaltyProgram = findLoyaltyProgramByProgramId(loyaltyCard.getProgramId(), user.getUserId());
		if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus())){
			status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;
		}

		LoyaltyCardSet loyaltyCardSet = null;
		loyaltyCardSet = findLoyaltyCardSetByCardsetId(loyaltyCard.getCardSetId(), user.getUserId());
		if(loyaltyCardSet == null || !OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE.equals(loyaltyCardSet.getStatus())){
			status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;
		}

		if(OCConstants.LOYALTY_CARD_STATUS_ENROLLED.equalsIgnoreCase(loyaltyCard.getStatus())){

			return performLoyaltySCReturn(request,header, amount, originalRecpt, responseHeader, loyaltyProgram, cardNumber, user, requestJson);

		}
		else if(OCConstants.LOYALTY_CARD_STATUS_ACTIVATED.equalsIgnoreCase(loyaltyCard.getStatus())){

			return performGiftSCReturn(request, header, amount, originalRecpt, cardNumber, responseHeader, loyaltyProgram,user, requestJson);
		}
		else{
			status = new Status("111537", PropertyUtil.getErrorMessage(111537, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;
		}
	}//cardBasedReturnTransaction

	private LoyaltyReturnTransactionResponse performGiftSCReturn(LoyaltyReturnTransactionRequest returnRequest, RequestHeader header, Amount amount, OriginalReceipt originalRecpt,
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
				
				balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
				contactLoyaltyList.add(contactsLoyalty);
				
				if(contactsLoyalty.getProgramTierId() != null)	tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}else if( contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
				ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactsLoyalty);
				String maskedNum = Constants.STRING_NILL;
				if(destLoyalty != null) {
					contactLoyaltyList.add(destLoyalty);
					contactsLoyalty = destLoyalty;
					tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
					balances = prepareBalancesObject(destLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
					maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
					
				}
				String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+maskedNum+".";
				status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			MembershipResponse response = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
			List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, null, null, matchedCustomers, status);
			return returnTransactionResponse;
		}

		if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_STORE_CREDIT)) {
			returnTransactionResponse = performStoreCreditOperation(returnRequest, header, returnRequest.getItems(), amount.getType(), originalRecpt, responseHeader, loyaltyProgram, contactsLoyalty, user, requestJson);
		}
		else {
			status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;
		}
		logger.info("-- Exit performGiftSCReturn --");
		return returnTransactionResponse;
	}//performGiftSCReturn
	private ContactsLoyalty getDestMembershipIfAny(ContactsLoyalty contactLoyalty) throws Exception{
		logger.info("getDestMembershipIfAny start");
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		if(contactLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED) && contactLoyalty.getTransferedTo() != null) {
			return loyaltyDao.findAllByLoyaltyId(contactLoyalty.getTransferedTo());
			
		}
		logger.info("getDestMembershipIfAny end");
		return null;
	}
	private LoyaltyReturnTransactionResponse performLoyaltySCReturn(LoyaltyReturnTransactionRequest loyaltyReturnRequest, RequestHeader header, Amount amount, OriginalReceipt originalRecpt,
			ResponseHeader responseHeader, LoyaltyProgram loyaltyProgram, String membershipNo,
			Users user, String requestJson) throws Exception{

		logger.info("-- Enetred performLoyaltySCReturn --");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;

		ContactsLoyalty contactsLoyalty = findContactLoyalty(membershipNo, loyaltyProgram.getProgramId(), user.getUserId());

		if(contactsLoyalty == null){
			status = new Status("1000", PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null,status);
			return returnTransactionResponse;
		}

		if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
			LoyaltyProgramTier tier = null;
		
			
			List<Balance> balances = null;
			List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
			
			if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
				balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				contactLoyaltyList.add(contactsLoyalty);
				if(contactsLoyalty.getProgramTierId() != null) tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				
				String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
				balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				contactLoyaltyList.add(contactsLoyalty);
				if(contactsLoyalty.getProgramTierId() != null) tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
				
				ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactsLoyalty);
				String maskedNum = Constants.STRING_NILL;
				if(destLoyalty != null) {
					contactLoyaltyList.add(destLoyalty);
					contactsLoyalty = destLoyalty;
					tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
					balances = prepareBalancesObject(destLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
					maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
					
				}
				String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+maskedNum+".";
					status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			MembershipResponse response = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
			List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, null, null, matchedCustomers, status);
			return returnTransactionResponse;
		}

		LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
		if(loyaltyExclusion != null){
			status = validateStoreNumberExclusion(header, loyaltyProgram, loyaltyExclusion);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}
		}
		List<SkuDetails> items = loyaltyReturnRequest.getItems();
		if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_STORE_CREDIT)) {
			returnTransactionResponse = performStoreCreditOperation(loyaltyReturnRequest, header, items,
					amount.getType(), originalRecpt, responseHeader, loyaltyProgram, contactsLoyalty, user, requestJson);
			return returnTransactionResponse;
		}
		else {
			status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null,status);
			return returnTransactionResponse;
		}
	}//performLoyaltySCReturn

	private LoyaltyReturnTransactionResponse performStoreCreditOperation(LoyaltyReturnTransactionRequest returnRequest, RequestHeader header, List<SkuDetails> itemList, String amountType, OriginalReceipt originalRecpt, ResponseHeader responseHeader,
			LoyaltyProgram loyaltyProgram, ContactsLoyalty contactsLoyalty, Users user, String requestJson) throws Exception{

		logger.info("performStoreCreditOperation method called...");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		String inquiry = returnRequest.getAmount().getType()!=null && returnRequest.getAmount().getType().equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) ? OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY : Constants.STRING_NILL;
		
		ContactsLoyaltyDaoForDML contactsLoyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		String rewardFlag = Constants.STRING_NILL;
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		
		double enteredAmount = 0.0;
		String res = Utility.truncateUptoTwoDecimal(calculateReturnAmount(itemList));//Utility.truncateUptoTwoDecimal(Double.parseDouble(amount.getEnteredValue()));
		if(res != null)
			enteredAmount = Double.parseDouble(res);
		
		if(contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L) ||
				contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL)) {
			rewardFlag = OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L;
			Double ltyCurrBal = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
			//Double balToAdd = ltyCurrBal + Double.parseDouble(amount.getEnteredValue());
			Double balToAdd = ltyCurrBal + enteredAmount;
			//contactsLoyalty.setGiftcardBalance(new Double(decimalFormat.format(balToAdd)));
			contactsLoyalty.setGiftcardBalance(new BigDecimal(balToAdd).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		}else if(contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
			rewardFlag = OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G;
			Double giftBal = contactsLoyalty.getGiftBalance() == null ? 0.0 : contactsLoyalty.getGiftBalance();
			//Double balToAdd = giftBal + Double.parseDouble(amount.getEnteredValue());
			Double balToAdd = giftBal + enteredAmount;
			//contactsLoyalty.setGiftBalance(new Double(decimalFormat.format(balToAdd)));
			contactsLoyalty.setGiftBalance(new BigDecimal(balToAdd).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		}
		contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);

		long pointsDifference = 0;
		//long amountDifference = 0;
		double amountDifference = 0;
		double earnedPts = 0;
		amountDifference = enteredAmount;//Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.parseDouble(amount.getEnteredValue())));

		//create transaction 
		if(amountType !=null && !amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)) {
			
			LoyaltyTransactionChild childTx = createReturnTransaction(header, amountType, originalRecpt,
					contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
					Constants.STRING_NILL+pointsDifference,Constants.STRING_NILL+amountDifference, enteredAmount, earnedPts, 
					OCConstants.LOYALTY_TYPE_AMOUNT,responseHeader.getTransactionId(),
					enteredAmount,OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT, Constants.STRING_NILL, 
					null,null, null, Constants.STRING_NILL, null);
			
			//Expiry transaction
			createExpiryTransaction(contactsLoyalty, enteredAmount,	(long)earnedPts, childTx.getTransChildId(), rewardFlag, amountType);
		}

		LoyaltyProgramTier loyaltyProgramTier = null;
		if(contactsLoyalty.getProgramTierId() != null)
			loyaltyProgramTier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
		MembershipResponse response = prepareMembershipResponse(contactsLoyalty, loyaltyProgramTier, loyaltyProgram);
		String expiryPeriod = Constants.STRING_NILL;

		//APP-3284
		boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(returnRequest.getHeader().getStoreNumber(),loyaltyProgramTier);

		if(loyaltyProgramTier != null && loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES	&& isStoreActiveForActivateAfter && ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
				(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){
			expiryPeriod = loyaltyProgramTier.getPtsActiveDateValue()+" "+loyaltyProgramTier.getPtsActiveDateType();
		}
		HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
		List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
		contactLoyaltyList.add(contactsLoyalty);
		List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
		List<Balance> balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL,Constants.STRING_NILL+amountDifference, Constants.STRING_NILL);
		/*BalancesAdditionalInfo additionalInfo = prepareCreditsAdditionalInfo(OCConstants.LOYALTY_TYPE_STORE_CREDIT, contactsLoyalty.getRewardFlag(),
																	returnTransactionRequest.getAmount().getEnteredValue(), contactsLoyalty.getCardNumber(),Constants.STRING_NILL);*/
		Status status = new Status("0", "Return "+inquiry+" was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, null, matchedCustomers, status);
		logger.info("performStoreCreditOperation method exit...");
		return returnTransactionResponse;
	}

	private BalancesAdditionalInfo prepareCreditsAdditionalInfo(BalancesAdditionalInfo additionalInfo, String type, 
																String flag, String creditedPoints, String creditedReward, String creditedGift, 
																String membershipNumber, String enteredAmountType, List<Credit> creditList, Users user) {
		logger.info("-- Entered prepareCreditsAdditionalInfo --");
		if(additionalInfo == null) {
			additionalInfo = new BalancesAdditionalInfo();
			Debit debit = new Debit();
			additionalInfo.setDebit(debit);
		}

		//List<Credit> creditList = new ArrayList<Credit>();
		Credit credit = new Credit();
		credit.setMembershipNumber(membershipNumber+Constants.STRING_NILL);
		credit.setRewardPoints(user.isRedemptionAsDiscount() ? "0" : creditedPoints);
		credit.setRewardCurrency(user.isRedemptionAsDiscount() ? "0" : creditedReward);
		credit.setGift(user.isRedemptionAsDiscount() ? "0" :creditedGift);
		creditList.add(credit);
		additionalInfo.setCredit(creditList);
		logger.info("-- Exit prepareCreditsAdditionalInfo --");
		return additionalInfo;
	}

	
	private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty,
			Double expiryAmount, long expiryPoints, Long transChildId, String rewardFlag, String amountType){

		logger.info("-- Entered createExpiryTransaction --");
		if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)) return null;
		LoyaltyTransactionExpiry transaction = null;
		try{
			transaction = new LoyaltyTransactionExpiry();
			transaction.setTransChildId(transChildId);
			transaction.setMembershipNumber(Constants.STRING_NILL+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setExpiryPoints(expiryPoints);
			transaction.setExpiryAmount(expiryAmount);
			transaction.setRewardFlag(rewardFlag);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());

			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			//loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
			loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);

		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		logger.info("-- Exit createExpiryTransaction --");
		return transaction;
	}//createExpiryTransaction

	private LoyaltyReturnTransactionResponse performReversalOperation(LoyaltyTransactionChild loyaltyTransactionChild, 
			RequestHeader header, String amountType,
			OriginalReceipt originalRecpt,  ResponseHeader responseHeader, LoyaltyProgram loyaltyProgram,
			ContactsLoyalty contactsLoyalty, Users user, List<SkuDetails> itemList, double netReturnedAmt, 
			Double returnedAmountdbl,String key, List<LoyaltyTransactionChild> rewardIssuList, boolean isFullRcpt, 
			List<LoyaltyTransactionChild> returnedTrs, String issuanceTrxtemInfo,double actualReturnAmnt) throws Exception {

		logger.info("-- Entered performReversalOperation --");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;
		
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		double earnedValue = 0;
		String pointsDifference = Constants.STRING_NILL;
		String amountDifference = Constants.STRING_NILL;
		double autoCnvrtPtsCredit = 0;
		String earnType = Constants.STRING_NILL;
		BalancesAdditionalInfo additionalInfo = null;
		LoyaltyProgramTier loyaltyProgramTier = null;
		if(contactsLoyalty.getProgramTierId() != null)
			loyaltyProgramTier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
		//Prepare membership and matched customers objects
		
		
		List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
		contactLoyaltyList.add(contactsLoyalty);
		List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);

		MembershipResponse response = prepareMembershipResponse(contactsLoyalty, loyaltyProgramTier, loyaltyProgram);
		double excludedAmt = 0.0;
		double fullreturnedAMount =0.0;
		String roundingType= loyaltyProgramTier.getRoundingType();

		/*if(loyaltyTransactionChild != null && 
				loyaltyTransactionChild.getEnteredAmount() != null && 
				loyaltyTransactionChild.getEnteredAmount() != 0 && 
				((loyaltyTransactionChild.getEarnedAmount() != null && loyaltyTransactionChild.getEarnedAmount() !=0)|| 
				(loyaltyTransactionChild.getEarnedPoints() != null && loyaltyTransactionChild.getEarnedPoints() !=0 ))) {*/
			
			excludedAmt = loyaltyTransactionChild.getExcludedAmount() != null ? loyaltyTransactionChild.getExcludedAmount() : 0;
			
			//only for auto-conversion
			if(loyaltyTransactionChild.getConversionAmt() != null && loyaltyTransactionChild.getConversionAmt() != 0) {
				
				Double earnedMultipleFactordbl = 0.0;
				if(loyaltyTransactionChild.getIssuanceAmount() != null ) {
					earnedMultipleFactordbl= loyaltyTransactionChild.getEarnedPoints()/loyaltyTransactionChild.getIssuanceAmount();
					fullreturnedAMount = loyaltyTransactionChild.getIssuanceAmount();
				}
				if(rewardIssuList==null && (loyaltyTransactionChild.getItemInfo() == null || loyaltyTransactionChild.getItemInfo().isEmpty()) ){//for older type of issuances
					fullreturnedAMount = loyaltyTransactionChild.getEnteredAmount()-excludedAmt;
					earnedMultipleFactordbl = loyaltyTransactionChild.getEarnedPoints()/(loyaltyTransactionChild.getEnteredAmount()-excludedAmt);
				}else if(loyaltyTransactionChild.getItemInfo() != null && 
						!loyaltyTransactionChild.getItemInfo().isEmpty()  ){//take from issuance amount
					earnedMultipleFactordbl = loyaltyTransactionChild.getEarnedPoints()/loyaltyTransactionChild.getIssuanceAmount();
					fullreturnedAMount = loyaltyTransactionChild.getIssuanceAmount();
				}
				logger.info("loyaltyTransactionChild.getEarnedPoints()==>"+loyaltyTransactionChild.getEarnedPoints());
				logger.info("loyaltyTransactionChild.getEnteredAmount()"+loyaltyTransactionChild.getEnteredAmount());
				double earnedPointsValue = LoyaltyProgramHelper.getRoundedPoints("Near", netReturnedAmt * earnedMultipleFactordbl); //Changed in accordance with APP-976,904 from round to floor then removed altogether.
				earnedValue = earnedPointsValue; //APP-4171 changes to rounding near instead of tier rounding type
				amountDifference = "-"+earnedValue;
				logger.info("earned value and earnedPointsValue>>>"+earnedValue+" "+earnedPointsValue);
				earnType = loyaltyTransactionChild.getEarnType()==null?OCConstants.LOYALTY_TYPE_POINTS:loyaltyTransactionChild.getEarnType();//Changes corresponding to APP-906,Release 2.5.6.0
				logger.info("loyaltyTransactionChild.getEarnType()==>"+loyaltyTransactionChild.getEarnType());
			}
			else {
				if(loyaltyTransactionChild.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
					
					Double multipleFactordbl = 0.0;
				//	String roundingType = loyaltyProgramTier.getRoundingType();
					
					if(rewardIssuList==null && (loyaltyTransactionChild.getItemInfo() == null || loyaltyTransactionChild.getItemInfo().isEmpty()) ){//for older type of issuances
						fullreturnedAMount = loyaltyTransactionChild.getEnteredAmount()-excludedAmt;
						multipleFactordbl = loyaltyTransactionChild.getEarnedPoints()/(loyaltyTransactionChild.getEnteredAmount()-excludedAmt);
					}else if(loyaltyTransactionChild.getItemInfo() != null && 
							!loyaltyTransactionChild.getItemInfo().isEmpty()  ){//take from issuance amount
						multipleFactordbl = loyaltyTransactionChild.getEarnedPoints()/loyaltyTransactionChild.getIssuanceAmount();
						fullreturnedAMount = loyaltyTransactionChild.getIssuanceAmount();
					}
					//earnedValue = Math.floor(netReturnedAmt * multipleFactordbl); //Changed in accordance with APP-962
					logger.info("Rounding type of the tier is ==="+loyaltyProgramTier.getRoundingType());
					
					earnedValue = LoyaltyProgramHelper.getRoundedPoints("Near", netReturnedAmt * multipleFactordbl);//APP-4171 changes to rounding near instead of tier rounding type

/*
					if(roundingType!=null && roundingType.toString().equalsIgnoreCase("Up")){
						
						earnedValue = (long) Math.ceil(netReturnedAmt * multipleFactordbl);

					}else {
						
						earnedValue = (long) Math.floor(netReturnedAmt * multipleFactordbl);
					}
					*/
					
				//	earnedValue = Math.floor(netReturnedAmt * multipleFactordbl); //Changed in accordance with APP-962

					pointsDifference = "-"+(long)earnedValue;//amountDifference = "-"+earnedValue;
					earnType = OCConstants.LOYALTY_TYPE_POINTS;
				}
				else {
					Double multipleFactordbl = 0.0;
					if(rewardIssuList==null && (loyaltyTransactionChild.getItemInfo() == null || loyaltyTransactionChild.getItemInfo().isEmpty()) ){//for older type of issuances
						fullreturnedAMount = loyaltyTransactionChild.getEnteredAmount()-excludedAmt;
						multipleFactordbl = loyaltyTransactionChild.getEarnedAmount()/(loyaltyTransactionChild.getEnteredAmount()-excludedAmt);
					}else if(loyaltyTransactionChild.getItemInfo() != null && 
							!loyaltyTransactionChild.getItemInfo().isEmpty()  ){//take from issuance amount
						multipleFactordbl = loyaltyTransactionChild.getEarnedAmount()/loyaltyTransactionChild.getIssuanceAmount();
						fullreturnedAMount = loyaltyTransactionChild.getIssuanceAmount();
						
					}
					logger.debug("multipleFactordbl ::"+multipleFactordbl+" netReturnedAmt :: "+netReturnedAmt);
					String res = Utility.truncateUptoTwoDecimal(netReturnedAmt * multipleFactordbl);
					logger.debug("res ::"+res);
					if(res != null)
						earnedValue = Double.parseDouble(res);
					amountDifference = "-"+earnedValue;
					earnType = OCConstants.LOYALTY_TYPE_AMOUNT;
				}
			}
			
			
			//Update balances
			Map<String, Object> balMap = new HashMap<String, Object>();
			balMap = updateContactLoyaltyBalances(earnedValue, earnType, contactsLoyalty, loyaltyProgramTier,returnedAmountdbl, amountType,actualReturnAmnt); //Changes LPV
			
			
			status = (Status) balMap.get("status");
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				
				List<Balance> balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				String expiryPeriod = Constants.STRING_NILL;

				boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(header.getStoreNumber(),loyaltyProgramTier);

				if(loyaltyProgramTier != null && loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES	&& isStoreActiveForActivateAfter && ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
						(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){
					expiryPeriod = loyaltyProgramTier.getPtsActiveDateValue()+" "+loyaltyProgramTier.getPtsActiveDateType();
				}
				HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
				 additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
				//check for redemption reversal
				/*LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(originalRecpt.getDocSID(), 
						user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION);*/
				
				logger.info("performRedemptnBasedReversal");
				status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG) , OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
				
				/*returnTransactionResponse = performRedemptnBasedReversal(redempTransList, header, amount, originalRecpt, creditReddemedAmount, responseHeader, requestJson, user, 
																		response, balances, holdBalance, additionalInfo, matchedCustomers,  true,  PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 111562,true);
				*/return returnTransactionResponse;
				
		
			}
			String description = netReturnedAmt+Constants.STRING_NILL;
			//if(isFullRcpt) description = fullreturnedAMount+Constants.STRING_NILL;
			amountDifference = balMap.get("AmountDifference") != null && !balMap.get("AmountDifference").toString().isEmpty() ? "-"+(String)balMap.get("AmountDifference") : Constants.STRING_NILL;
			pointsDifference = balMap.get("PointsDifference") != null && !balMap.get("PointsDifference").toString().isEmpty() ? "-"+(String)balMap.get("PointsDifference") : Constants.STRING_NILL;
			createReturnTransaction(header, amountType, originalRecpt, 
		    		contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
		    		pointsDifference, amountDifference, null , null, null, 
					responseHeader.getTransactionId(), netReturnedAmt,OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL, 
					description, null,key, null, Constants.STRING_NILL, issuanceTrxtemInfo);
			 additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
			 String itemRewardsInfo = loyaltyTransactionChild.getItemRewardsInfo();
		 if( itemRewardsInfo != null && !itemRewardsInfo.isEmpty()){
			 logger.debug("itemRewardsInfo=="+itemRewardsInfo);
				String[] itemsAndRewards = itemRewardsInfo.indexOf(Constants.DELIMITER_PIPE) != -1 ? 
						itemRewardsInfo.split("\\"+Constants.DELIMITER_PIPE) : null;
						if(itemsAndRewards == null){
							itemsAndRewards = new String[1];
							itemsAndRewards[0] = itemRewardsInfo;
						}
				Double rollBackQty =0.0;
				for (String itemRewards : itemsAndRewards) {
					String[] itemRewardsArr = itemRewards.split(Constants.ADDR_COL_DELIMETER);//5182;=;12:1:camo;=;7038103734733705212
					String spId = itemRewardsArr[0];
					String[] spDetailsArr = itemRewardsArr[1].split(Constants.DELIMETER_COLON+"");
					String RequiredQty = spDetailsArr[0];
					double reward = Double.parseDouble(spDetailsArr[1]);
					
					
					String valuecode = spDetailsArr[2];
					double requiredQty = Double.parseDouble(RequiredQty);
					String itemSids = itemRewardsArr[2];
					String[] itemSidsArr = itemSids.split(",");
					for (SkuDetails item : itemList) {
						
						if(itemSids.contains(item.getItemSID())){
							for (String itemStr : itemSidsArr) {
								if(item.getItemSID().equals(itemStr)){
									
									rollBackQty += Double.parseDouble(item.getQuantity());
								}
							}//for-inner
							
						}//if
						
					}//for-outer
					//=============
					if(rollBackQty > 0){
						boolean deductReward = false; ;
						LoyaltyMemberItemQtyCounterDao LoyaltyMemberItemQtyCounterDao = (LoyaltyMemberItemQtyCounterDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO);
						List<LoyaltyMemberItemQtyCounter> memberQty = LoyaltyMemberItemQtyCounterDao.findItemsCounter(spId+"", contactsLoyalty.getLoyaltyId());
						//LoyaltyMemberItemQtyCounter existingQty = memberQty.get(0);
						LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
						LoyaltyBalanceDaoForDML loyaltyBalanceDaoForDML = (LoyaltyBalanceDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_BALANCE_DAO_FOR_DML);
						LoyaltyBalance loyaltyBalances = loyaltyBalanceDao.findBy(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(), valuecode);
						LoyaltyMemberItemQtyCounter existingQty = null;
						
						double duductRewardDbl = 0;
						boolean issufficient = false;
						if(memberQty != null && !memberQty.isEmpty() ) {
							
							existingQty = memberQty.get(0);
							if(existingQty != null) { //accross multiple
								double existingqty = existingQty.getQty();
								if(existingqty == 0  ){//deduct the reward 
									duductRewardDbl = reward;
									deductReward = true;
									issufficient =  loyaltyBalances != null && loyaltyBalances.getBalance() >= reward;
									if(issufficient)existingQty.setQty(requiredQty-rollBackQty);
								}else if(existingQty.getQty() >= rollBackQty){ // dont deduct the reward
									existingQty.setQty(existingQty.getQty()-rollBackQty);
								}else if(existingQty.getQty() < rollBackQty ) {//deduct
									deductReward = true;
									duductRewardDbl = reward;
									existingQty.setQty((requiredQty)-rollBackQty+existingQty.getQty());
									issufficient =   loyaltyBalances != null && loyaltyBalances.getBalance() >= reward;
								}
								if( !amountType.equals(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)){
									
									LoyaltyMemberItemQtyCounterDaoforDML loyaltyMemberItemQtyCounterDaoForDml =(LoyaltyMemberItemQtyCounterDaoforDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO_FOR_DML);
									loyaltyMemberItemQtyCounterDaoForDml.saveOrUpdate(existingQty);
								}
							}
						}else{
							if(loyaltyBalances != null && loyaltyBalances.getBalance() >= reward){
								duductRewardDbl = reward;
								issufficient = true;
							}
							
						}
						
						
						/*if(existingQty != null) { //accross multiple
							double existingqty = existingQty.getQty();
							if(existingqty == 0  && loyaltyBalances != null && loyaltyBalances.getBalance() >= reward){//deduct the reward 
								duductRewardDbl = reward;
								deductReward = true;
								issufficient = true;
								existingQty.setQty(requiredQty-rollBackQty);
							}else if(existingQty.getQty() >= rollBackQty){ // dont deduct the reward
								existingQty.setQty(existingQty.getQty()-rollBackQty);
							}else if(existingQty.getQty() < rollBackQty &&  loyaltyBalances != null && loyaltyBalances.getBalance() >= reward) {//deduct
								deductReward = true;
								duductRewardDbl = reward;
								existingQty.setQty((requiredQty)-rollBackQty+existingQty.getQty());
								issufficient = true;
							}
						}else{
							
							
						}*/
						if(deductReward && !issufficient){
							
							status = new Status("111562", "On returned items, earned rewards of "+duductRewardDbl+" "+earnType+" cannot be reversed." , OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, null, null, additionalInfo, matchedCustomers, status);
							return returnTransactionResponse;
						}
						if(!amountType.equals(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)){
							
							/*LoyaltyMemberItemQtyCounterDaoforDML loyaltyMemberItemQtyCounterDaoForDml =(LoyaltyMemberItemQtyCounterDaoforDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO_FOR_DML);
							loyaltyMemberItemQtyCounterDaoForDml.saveOrUpdate(existingQty);*/
							if(deductReward && issufficient){
								String rewardDiff = Constants.STRING_NILL;
								Long rewardBal = loyaltyBalances.getBalance();
								logger.debug("curr bal before==="+reward);
								Double totalReward = loyaltyBalances.getTotalEarnedBalance();
								rewardDiff = (rewardBal <=  duductRewardDbl) ? ("-"+rewardBal): "-"+duductRewardDbl ;
								loyaltyBalances.setBalance(loyaltyBalances.getBalance()-(long)duductRewardDbl);
								loyaltyBalances.setTotalEarnedBalance(loyaltyBalances.getTotalEarnedBalance()-(long)duductRewardDbl);
								if(loyaltyBalances.getBalance() < 0) loyaltyBalances.setBalance(0l);
								if(loyaltyBalances.getTotalEarnedBalance() < 0) loyaltyBalances.setTotalEarnedBalance(0.0);	
								loyaltyBalanceDaoForDML.saveOrUpdate(loyaltyBalances);
								logger.debug("rewardDiff==="+rewardDiff+ " earnedReward=="+duductRewardDbl +" curr bal ==="+loyaltyBalances.getBalance());
								
								
								createReturnTransaction(header, amountType, null, 
										contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
										Constants.STRING_NILL, Constants.STRING_NILL, null , null, loyaltyBalances.getValueCode(), 
										responseHeader.getTransactionId(), 0.0,OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD_REVERSAL, 
										Constants.STRING_NILL, null,null, Long.parseLong(spId),rewardDiff, null );
								//additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
							}
						}
	
					}
										
				}
		}

		if(rewardIssuList != null && !rewardIssuList.isEmpty()) {
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		if(isFullRcpt){
				key=genKey(loyaltyTransactionChild.getDocSID(),loyaltyTransactionChild.getReceiptNumber(),loyaltyTransactionChild.getStoreNumber(), loyaltyTransactionChild.getSubsidiaryNumber());
				String docSID = "OR-"+originalRecpt.getDocSID();
				logger.info("key==>"+key);
				for (LoyaltyTransactionChild rewardTrx : rewardIssuList) {
					Map<String, Object> innerbalMap = null;
					
					List<LoyaltyTransactionChild> returnList = loyaltyTransactionChildDao.getTotRewarReversalAmt(user.getUserId(), 
							docSID, OCConstants.LOYALTY_TRANS_TYPE_RETURN,key, rewardTrx.getSpecialRewardId());
					if(returnList == null || returnList.size() ==0) {
						earnType = rewardTrx.getEarnType();
						if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT) || earnType.equals(OCConstants.LOYALTY_TYPE_CURRENCY)){
							
							Double earnedAmount = rewardTrx.getEarnedAmount();
							innerbalMap = updateContactLoyaltyBalances(earnedAmount, earnType, contactsLoyalty, loyaltyProgramTier,0, amountType,0);
							
						}else if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
							
							Double earnedPoints = rewardTrx.getEarnedPoints();
							
							innerbalMap = updateContactLoyaltyBalances(earnedPoints, earnType, contactsLoyalty, loyaltyProgramTier,0, amountType,0);
						}else{

							if(amountType !=null ) {
								
								Double earnedReward = rewardTrx.getEarnedReward();
								LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
								LoyaltyBalanceDaoForDML loyaltyBalanceDaoForDML = (LoyaltyBalanceDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_BALANCE_DAO_FOR_DML);
								LoyaltyBalance loyaltyBalances = loyaltyBalanceDao.findBy(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(), earnType);
								if(loyaltyBalances == null || loyaltyBalances.getBalance() == null || loyaltyBalances.getBalance()<earnedReward){
									
									status = new Status("111562", "On returned items, earned rewards of "+earnedReward+" "+earnType+" cannot be reversed." , OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
									returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, null, null, additionalInfo, matchedCustomers, status);
									return returnTransactionResponse;
								}
								
								if(loyaltyBalances != null && !amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)){
									String rewardDiff = Constants.STRING_NILL;
									Long reward = loyaltyBalances.getBalance();
									logger.debug("curr bal before==="+reward);
									Double totalReward = loyaltyBalances.getTotalEarnedBalance();
									rewardDiff = (reward <=  earnedReward) ? ("-"+reward): "-"+earnedReward ;
									loyaltyBalances.setBalance(loyaltyBalances.getBalance()-earnedReward.longValue());
									loyaltyBalances.setTotalEarnedBalance(loyaltyBalances.getTotalEarnedBalance()-earnedReward);
									if(loyaltyBalances.getBalance() < 0) loyaltyBalances.setBalance(0l);
									if(loyaltyBalances.getTotalEarnedBalance() < 0) loyaltyBalances.setTotalEarnedBalance(0.0);	
									loyaltyBalanceDaoForDML.saveOrUpdate(loyaltyBalances);
									logger.debug("rewardDiff==="+rewardDiff+ " earnedReward=="+earnedReward +" curr bal ==="+loyaltyBalances.getBalance());
									createReturnTransaction(header, amountType, originalRecpt, 
											contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
											Constants.STRING_NILL, Constants.STRING_NILL, null , null, loyaltyBalances.getValueCode(), 
											responseHeader.getTransactionId(), netReturnedAmt,OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD_REVERSAL, 
											Constants.STRING_NILL, null,key, rewardTrx.getSpecialRewardId(),rewardDiff,Constants.STRING_NILL );
									//additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);	
								}
							}
							
						}
						if(innerbalMap != null ) {
							
							status = (Status) innerbalMap.get("status");
							if(status == null || OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())){
								//description = rewardTrx.getEnteredAmount()+Constants.STRING_NILL;
								amountDifference = innerbalMap.get("AmountDifference") != null && !innerbalMap.get("AmountDifference").toString().isEmpty() ? "-"+(String)innerbalMap.get("AmountDifference") : Constants.STRING_NILL;
								pointsDifference = innerbalMap.get("PointsDifference") != null && !innerbalMap.get("PointsDifference").toString().isEmpty() ? "-"+(String)innerbalMap.get("PointsDifference") : Constants.STRING_NILL;
								createReturnTransaction(header, amountType, originalRecpt, 
							    		contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
							    		pointsDifference, amountDifference, null , null, null, 
										responseHeader.getTransactionId(), netReturnedAmt,OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD_REVERSAL, 
										description, null,key, rewardTrx.getSpecialRewardId(),Constants.STRING_NILL, Constants.STRING_NILL);
								additionalInfo = prepareDebitAddtionalInfo(innerbalMap,contactsLoyalty);
								
							}//if
							
						}
						if(amountType !=null && !amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)) {
							int rollBackQty = 0;
							if(rewardTrx.getItemInfo()!=null && !rewardTrx.getItemInfo().isEmpty()){
								String itemInfo = rewardTrx.getItemInfo();
								String[] itemsArr = itemInfo.split(Constants.ADDR_COL_DELIMETER)[0].split(",");
								String requiredQty = itemInfo.split(Constants.ADDR_COL_DELIMETER)[1];
								for (SkuDetails item : itemList) {
									
									if(itemInfo.contains(item.getItemSID())){
										for (String itemStr : itemsArr) {
											String[] itemToken = itemStr.split(Constants.DELIMETER_COLON+"");
											if(item.getItemSID().equals(itemToken[0])){
												
												rollBackQty += Double.parseDouble(item.getQuantity());
											}
										}//for-inner
										
									}//if
									
								}//for-outer
								if(rollBackQty > 0 && !amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)){
									
									LoyaltyMemberItemQtyCounterDaoforDML loyaltyMemberItemQtyCounterDaoForDml =(LoyaltyMemberItemQtyCounterDaoforDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO_FOR_DML);
									loyaltyMemberItemQtyCounterDaoForDml.updateCurrQty(contactsLoyalty.getLoyaltyId(), rewardTrx.getSpecialRewardId(), rollBackQty,(int)Double.parseDouble(requiredQty));
								}
							}//if
						}//if only for reversal	
					}//no previous returns exists
					
				}//for each reward trx
			}else{//partial return only for itembased
				Map<String, Object> innerbalMap = null;
				for (LoyaltyTransactionChild rewardTrx : rewardIssuList) {
					key=genKey(rewardTrx.getDocSID(),rewardTrx.getReceiptNumber(),rewardTrx.getStoreNumber(), rewardTrx.getSubsidiaryNumber());
					String docSID = "OR-"+originalRecpt.getDocSID();
					logger.info("key==>"+key);
					List<LoyaltyTransactionChild> returnList = loyaltyTransactionChildDao.getTotRewarReversalAmt(user.getUserId(), 
							docSID, OCConstants.LOYALTY_TRANS_TYPE_RETURN,key, rewardTrx.getSpecialRewardId());
					long returnedReward = 0l;
					double returnedAmount = 0l;
					double returnedPoints = 0l;
					double returnedQty = 0;
					if(returnList != null && !returnList.isEmpty()){
						
						for (LoyaltyTransactionChild returnedTrx : returnList) {
							if(returnedTrx.getAmountDifference() != null && !returnedTrx.getAmountDifference().isEmpty() ) returnedAmount+= Double.parseDouble(returnedTrx.getAmountDifference());
							 if(returnedTrx.getPointsDifference() != null && !returnedTrx.getPointsDifference().isEmpty() ) returnedPoints += Double.parseDouble(returnedTrx.getPointsDifference());
							if (returnedTrx.getRewardDifference() != null && !returnedTrx.getRewardDifference().isEmpty() ) returnedReward += Double.parseDouble(returnedTrx.getRewardDifference());
							//returnedReward += Double.parseDouble(returnedTrx.getRewardDifference());
							
							if(returnedTrx.getItemInfo() != null && !returnedTrx.getItemInfo().isEmpty()){
								String[] itemsArr = returnedTrx.getItemInfo().split(Constants.DELIMETER_COMMA);
								for (String items : itemsArr) {
									for (SkuDetails item : itemList) {
										
										if(items.contains(item.getItemSID())){
											String[] itemToken = items.split(Constants.DELIMETER_COLON+"");
											if(item.getItemSID().equals(itemToken[0])){
												returnedQty += Double.parseDouble(item.getQuantity());
											}
											
										}//if
										
									}//for-outer
									//	}
									
								}
							}
						}
					}
					String returnedItemInfo = "";
					earnType = rewardTrx.getEarnType();
					Double enteredAmount = 0.0;
					String itemInfo = rewardTrx.getItemInfo();
					int rollBackQty = 0;
					boolean deductReward = false;
					if(amountType !=null) {
						if(rewardTrx.getItemInfo()!=null && !rewardTrx.getItemInfo().isEmpty()){
							String[] itemsArr = itemInfo.split(Constants.ADDR_COL_DELIMETER)[0].split(",");
							String requiredQty = itemInfo.split(Constants.ADDR_COL_DELIMETER)[1];
							for (SkuDetails item : itemList) {
								
								if(itemInfo.contains(item.getItemSID())){
									for (String itemStr : itemsArr) {
										String[] itemToken = itemStr.split(Constants.DELIMETER_COLON+"");
										if(item.getItemSID().equals(itemToken[0])){
											if(returnedItemInfo.length()>0) returnedItemInfo += Constants.DELIMETER_COMMA;
											returnedItemInfo += item.getItemSID()+Constants.DELIMETER_COLON+item.getQuantity()+Constants.DELIMETER_COLON+item.getBilledUnitPrice();
											enteredAmount += Double.parseDouble(item.getQuantity())*Double.parseDouble(item.getBilledUnitPrice());
											rollBackQty += Double.parseDouble(item.getQuantity());
										}
									}//for-inner
									
								}//if
								
							}//for-outer
							if(rollBackQty > 0){
								LoyaltyMemberItemQtyCounterDao LoyaltyMemberItemQtyCounterDao = (LoyaltyMemberItemQtyCounterDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO);
								List<LoyaltyMemberItemQtyCounter> memberQty = LoyaltyMemberItemQtyCounterDao.findItemsCounter(rewardTrx.getSpecialRewardId().longValue()+"", rewardTrx.getLoyaltyId());
								long deductRewardDbl = 0l;
								boolean issufficient =false;
								if(memberQty != null && !memberQty.isEmpty() ) {
									
									LoyaltyMemberItemQtyCounter existingQty = memberQty.get(0);
									if(existingQty != null) { //accross multiple
										if(existingQty.getQty() == 0 ){//deduct the reward 
											deductReward = true;
											existingQty.setQty((int)Double.parseDouble(requiredQty)-rollBackQty);
										}else if(existingQty.getQty() >= rollBackQty){ // dont deduct the reward
											existingQty.setQty(existingQty.getQty()-rollBackQty);
										}else if(existingQty.getQty() < rollBackQty) {//deduct
											deductReward = true;
											existingQty.setQty((int)Double.parseDouble(requiredQty)-rollBackQty+existingQty.getQty());
										}
										if(!amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)) {
											
											LoyaltyMemberItemQtyCounterDaoforDML loyaltyMemberItemQtyCounterDaoForDml =(LoyaltyMemberItemQtyCounterDaoforDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO_FOR_DML);
											loyaltyMemberItemQtyCounterDaoForDml.saveOrUpdate(existingQty);
										}
									}
								}
								else{//accross single
									
									deductReward = true;
								}
								
								/*LoyaltyMemberItemQtyCounterDaoforDML loyaltyMemberItemQtyCounterDaoForDml =(LoyaltyMemberItemQtyCounterDaoforDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO_FOR_DML);
								loyaltyMemberItemQtyCounterDaoForDml.updateCurrQty(contactsLoyalty.getLoyaltyId(), rewardTrx.getSpecialRewardId(), rollBackQty,(int)Double.parseDouble(requiredQty));*/
							}
						}//if
					}
					if(deductReward){
						
						
						if(rewardTrx.getItemInfo()!=null && !rewardTrx.getItemInfo().isEmpty()){
							String[] itemsArr = itemInfo.split(Constants.ADDR_COL_DELIMETER)[0].split(",");
							String requiredQty = itemInfo.split(Constants.ADDR_COL_DELIMETER)[1];
							
							if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT) || 
									earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
								if(rewardTrx.getIssuanceAmount() != null && rewardTrx.getIssuanceAmount()!=0){//its multiplier case
									Double multipleFactordbl  = 0.0;
									if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
										 multipleFactordbl = rewardTrx.getEarnedAmount()/(rewardTrx.getIssuanceAmount());
									}else{
										 multipleFactordbl = rewardTrx.getEarnedPoints()/(rewardTrx.getIssuanceAmount());	
									}
									logger.debug("multipleFactordbl ::"+multipleFactordbl+" netReturnedAmt :: "+enteredAmount);
									String res = Utility.truncateUptoTwoDecimal(enteredAmount * multipleFactordbl);
									logger.debug("res ::"+res);
									if(res != null)
										earnedValue = Double.parseDouble(res);
								}else{
									
									if(itemInfo.split(Constants.ADDR_COL_DELIMETER).length >2) {
										
										String VCs = itemInfo.split(Constants.ADDR_COL_DELIMETER)[2];
										double requiredQtyDbl =  Double.parseDouble(requiredQty);
										earnedValue = Double.parseDouble(VCs);
										
										double totQty= requiredQtyDbl*rewardTrx.getEarnedReward();
										if(requiredQtyDbl == earnedValue){
											int multipleFactor = (int)(Double.parseDouble(requiredQty)/earnedValue);
											
											earnedValue = (double)(rollBackQty*multipleFactor);
										}else{
											double remainingqty = (rollBackQty+returnedQty) / Double.parseDouble(requiredQty);
											Double multipleFactor = (rollBackQty+returnedQty) % Double.parseDouble(requiredQty);
											double subtractReward = 0l;
											
											if(remainingqty <= 1 && returnedReward==0 ){
												
												subtractReward = earnedValue;
											}else if(remainingqty > 1 && multipleFactor < requiredQtyDbl ){
												
												subtractReward = (remainingqty-returnedReward);//+(earnedValue);
											}
											earnedValue =subtractReward;
										}
										
									}
								}
								Double earnedAmount = earnedValue;
								innerbalMap = updateContactLoyaltyBalances(earnedAmount, earnType, contactsLoyalty, loyaltyProgramTier,0, amountType,0);
								
							}else{
								
								if(amountType !=null ) {
									if(itemInfo.split(Constants.ADDR_COL_DELIMETER).length >2) {
										

										
										String VCs = itemInfo.split(Constants.ADDR_COL_DELIMETER)[2];
										double requiredQtyDbl =  Double.parseDouble(requiredQty);
										earnedValue = Double.parseDouble(VCs);
										
										double totQty= requiredQtyDbl*rewardTrx.getEarnedReward();
										if(requiredQtyDbl == earnedValue){
											int multipleFactor = (int)(Double.parseDouble(requiredQty)/earnedValue);
											if(earnedValue>rollBackQty*multipleFactor)
												earnedValue = (double)(rollBackQty*multipleFactor);
											logger.info("earnedValue====1"+earnedValue);
										}else{
											logger.debug("(rollBackQty+returnedQty) =="+(rollBackQty+returnedQty) + " "+requiredQty);
											double remainingqty = (rollBackQty+returnedQty) / Double.parseDouble(requiredQty);
											Double multipleFactor = (rollBackQty+returnedQty) % Double.parseDouble(requiredQty);
											double subtractReward = 0l;
											logger.debug("remainingqty =="+(remainingqty) + " "+requiredQty+" "+multipleFactor+" "+requiredQtyDbl+" "+earnedValue);
											if(((int)remainingqty <= 1 ) && returnedReward==0 ){
												logger.debug("remainingqty in if=");
												subtractReward = earnedValue;
											}else if(remainingqty > 1 && multipleFactor < requiredQtyDbl ){
												
												subtractReward = (remainingqty-returnedReward);//+(earnedValue);
												logger.debug("remainingqty in else=");
											}
											earnedValue =subtractReward;
										}
										
									
										logger.debug("earnedReward ==="+earnedValue);
										LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
										LoyaltyBalanceDaoForDML loyaltyBalanceDaoForDML = (LoyaltyBalanceDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_BALANCE_DAO_FOR_DML);
										LoyaltyBalance loyaltyBalances = loyaltyBalanceDao.findBy(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(), earnType);
										if(loyaltyBalances == null || loyaltyBalances.getBalance() == null || loyaltyBalances.getBalance()<earnedValue){
											
											status = new Status("111562", "On returned items, earned rewards of "+VCs+" "+earnType+" cannot be reversed." , OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
											returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, null, null, additionalInfo, matchedCustomers, status);
											return returnTransactionResponse;
										}
										if(loyaltyBalances != null && !amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)){
											String rewardDiff = Constants.STRING_NILL;
											Long reward = loyaltyBalances.getBalance();
											logger.debug("curr bal before==="+reward);
											Double totalReward = loyaltyBalances.getTotalEarnedBalance();
											rewardDiff = (reward <=  earnedValue) ? ("-"+reward): "-"+earnedValue ;
											loyaltyBalances.setBalance(loyaltyBalances.getBalance()-(long)earnedValue);
											loyaltyBalances.setTotalEarnedBalance(loyaltyBalances.getTotalEarnedBalance()-(long)earnedValue);
											if(loyaltyBalances.getBalance() < 0) loyaltyBalances.setBalance(0l);
											if(loyaltyBalances.getTotalEarnedBalance() < 0) loyaltyBalances.setTotalEarnedBalance(0.0);	
											loyaltyBalanceDaoForDML.saveOrUpdate(loyaltyBalances);
											logger.debug("rewardDiff==="+rewardDiff+ " earnedReward=="+earnedValue +" curr bal ==="+loyaltyBalances.getBalance());
											createReturnTransaction(header, amountType, originalRecpt, 
													contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
													Constants.STRING_NILL, Constants.STRING_NILL, null , null, loyaltyBalances.getValueCode(), 
													responseHeader.getTransactionId(), netReturnedAmt,OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD_REVERSAL, 
													enteredAmount+Constants.STRING_NILL, null,key, rewardTrx.getSpecialRewardId(),rewardDiff, returnedItemInfo );
											//additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);	
										}
										
										/*if(loyaltyBalances != null){
											
											
											String rewardDiff = Constants.STRING_NILL;
											Long reward = loyaltyBalances.getBalance();
											Double totalReward = loyaltyBalances.getTotalEarnedBalance();
											rewardDiff = (reward <=  earnedReward) ? ("-"+reward): "-"+earnedReward ;
											loyaltyBalances.setBalance(loyaltyBalances.getBalance()-earnedReward.longValue());
											loyaltyBalances.setTotalEarnedBalance(loyaltyBalances.getTotalEarnedBalance()-earnedReward);
											if(loyaltyBalances.getBalance() < 0) loyaltyBalances.setBalance(0l);
											if(loyaltyBalances.getTotalEarnedBalance() < 0) loyaltyBalances.setTotalEarnedBalance(0.0);	
											loyaltyBalanceDaoForDML.saveOrUpdate(loyaltyBalances);
											createReturnTransaction(header, amountType, originalRecpt, 
													contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
													Constants.STRING_NILL, Constants.STRING_NILL, null , null, loyaltyBalances.getValueCode(), 
													responseHeader.getTransactionId(), netReturnedAmt,OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD_REVERSAL, 
													reward+Constants.STRING_NILL, null,key, rewardTrx.getSpecialRewardId(),rewardDiff );
											//additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);	
											
											//additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);	
										}*/
									}
								}
								
							}
							if(innerbalMap != null ) {
								
								status = (Status) innerbalMap.get("status");
								if(status == null || OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())){
									description = rewardTrx.getEnteredAmount()+Constants.STRING_NILL;
									amountDifference = innerbalMap.get("AmountDifference") != null && !innerbalMap.get("AmountDifference").toString().isEmpty() ? "-"+(String)innerbalMap.get("AmountDifference") : Constants.STRING_NILL;
									pointsDifference = innerbalMap.get("PointsDifference") != null && !innerbalMap.get("PointsDifference").toString().isEmpty() ? "-"+(String)innerbalMap.get("PointsDifference") : Constants.STRING_NILL;
									createReturnTransaction(header, amountType, originalRecpt, 
											contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
											pointsDifference, amountDifference, null , null, null, 
											responseHeader.getTransactionId(), netReturnedAmt,OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD_REVERSAL, 
											enteredAmount+Constants.STRING_NILL, null,key, rewardTrx.getSpecialRewardId(), Constants.STRING_NILL, returnedItemInfo);
									additionalInfo = prepareDebitAddtionalInfo(innerbalMap,contactsLoyalty);
									
								}//if
								
							}
							
						}//if
					}
										
					
					
					
				}
			}
		}
		//Create return transaction
		//commented because - remaindertodebit is the value which we didnt deduct at all and there is no change in the balances.APP-1982
		/*if(loyaltyProgram.getPartialReversalFlag() == OCConstants.FLAG_YES){
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
			
			
		}*/
		
		//amountDifference = balMap.get("AmountDifference") != null && !balMap.get("AmountDifference").toString().isEmpty() ? "-"+(String)balMap.get("AmountDifference") : Constants.STRING_NILL;
		//pointsDifference = balMap.get("PointsDifference") != null && !balMap.get("PointsDifference").toString().isEmpty() ? "-"+(String)balMap.get("PointsDifference") : Constants.STRING_NILL;
		List<Balance> balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL+contactsLoyalty.getPointsDifference(), Constants.STRING_NILL+contactsLoyalty.getAmountDifference(), Constants.STRING_NILL);
		String expiryPeriod = Constants.STRING_NILL;

		boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(header.getStoreNumber(),loyaltyProgramTier);

		if(loyaltyProgramTier != null && loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES	&& isStoreActiveForActivateAfter && ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
				(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){

			expiryPeriod = loyaltyProgramTier.getPtsActiveDateValue()+" "+loyaltyProgramTier.getPtsActiveDateType();
		}
		
		//check for redemption reversal
		
		HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
		
		String inquiry = amountType!=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) ? OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY : Constants.STRING_NILL;
		/*LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(originalRecpt.getDocSID(), 
				user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION);
		*/String msg = Constants.STRING_NILL;
		if(loyaltyProgramTier.getPartialReversalFlag() == OCConstants.FLAG_YES){
			if((contactsLoyalty.getRemainingRewardCurrency() != null && contactsLoyalty.getRemainingRewardCurrency() != 0))
				msg = "Return "+inquiry+" was successful and Remainder Debit : $ " + Utility.truncateUptoTwoDecimal(contactsLoyalty.getRemainingRewardCurrency()) + ".";
			else
				msg = "Return "+inquiry+" was successful on issuance.";
		}else{
			msg = "Return "+inquiry+" was successful on issuance.";
		}
		
		status = new Status("0", msg , OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
	
	/*	returnTransactionResponse = performRedemptnBasedReversal(redempTransList, header, amount, originalRecpt, creditReddemedAmount, responseHeader, requestJson, user, 
				response, balances, holdBalance, additionalInfo, matchedCustomers, false, msg, 0,true);
	*/	logger.info("-- Exited performReversalOperation --");
		return returnTransactionResponse;
		
		/*status = new Status("0", "Return was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
		return returnTransactionResponse;*/
	}//performReversalOperation()

		
	
		
	private BalancesAdditionalInfo prepareDebitAddtionalInfo(Map<String, Object> balMap, ContactsLoyalty contactsLoyalty) {
		BalancesAdditionalInfo additionalInfo = new BalancesAdditionalInfo();
		logger.info("-- Entered prepareDebitAddtionalInfo --");
		String debitedRewardPoints = (String)balMap.get("debitedRewardPoints");
		String debitedRewardCurrency= (String)balMap.get("debitedRewardCurrency");
		String debitedHoldPoints = (String)(balMap.get("debitedHoldPoints")==null?"0":balMap.get("debitedHoldPoints"));
		String debitedHoldCurrency = (String)(balMap.get("debitedHoldCurrency"));
		String remainingRewardCurrency = (String)(balMap.get("remainingRewardCurrency"));
		String amountDifference = (String)(balMap.get("AmountDifference"));
		String pointsDifference = (String)(balMap.get("PointsDifference"));
		double debitedRewardPointsDbl = debitedRewardPoints.isEmpty() ? 0 : Double.parseDouble(debitedRewardPoints);
		double debitedRewardCurrencyDbl = debitedRewardCurrency.isEmpty() ? 0 : Double.parseDouble(debitedRewardCurrency);
		double debitedHoldPointsDbl = debitedHoldPoints.isEmpty() ? 0 : Double.parseDouble(debitedHoldPoints);
		double debitedHoldCurrencyDbl = debitedHoldCurrency.isEmpty() ? 0 : Double.parseDouble(debitedHoldCurrency);
		double remainingRewardCurrencyDbl = remainingRewardCurrency.isEmpty() ? 0 : Double.parseDouble(remainingRewardCurrency);
		double amountDifferenceDbl = amountDifference.isEmpty() ? 0 : Double.parseDouble(amountDifference);
		double pointsDifferenceDbl = pointsDifference.isEmpty() ? 0 : Double.parseDouble(pointsDifference);
		
		contactsLoyalty.setDebitedRewardCurrency(contactsLoyalty.getDebitedRewardCurrency()+debitedRewardCurrencyDbl);
		contactsLoyalty.setDebitedRewardPoints(contactsLoyalty.getDebitedRewardPoints()+debitedRewardPointsDbl);
		contactsLoyalty.setDebitedHoldCurrency(contactsLoyalty.getDebitedHoldCurrency()+debitedHoldCurrencyDbl);
		contactsLoyalty.setDebitedHoldPoints(contactsLoyalty.getDebitedHoldPoints()+debitedHoldPointsDbl);
		contactsLoyalty.setRemainingRewardCurrency(contactsLoyalty.getRemainingRewardCurrency()+remainingRewardCurrencyDbl);
		contactsLoyalty.setAmountDifference(contactsLoyalty.getAmountDifference()+amountDifferenceDbl);
		contactsLoyalty.setPointsDifference(contactsLoyalty.getPointsDifference()+pointsDifferenceDbl);
		
		Debit debit = new Debit();
		debit.setMembershipNumber(contactsLoyalty.getCardNumber()+Constants.STRING_NILL);
		debit.setRewardPoints(contactsLoyalty.getDebitedRewardPoints()+Constants.STRING_NILL);
		debit.setRewardCurrency(Utility.truncateUptoTwoDecimal(contactsLoyalty.getDebitedRewardCurrency()));
		debit.setHoldPoints(contactsLoyalty.getDebitedHoldPoints().longValue()+Constants.STRING_NILL);//release 2.5.6.0,APP - 944 "Introduced null condition."
		debit.setHoldCurrency(Utility.truncateUptoTwoDecimal(contactsLoyalty.getDebitedHoldCurrency()));
		logger.info("remainingRewardCurrencyDbl====>"+contactsLoyalty.getRemainingRewardCurrency());
		//RemainderToDebit rd = new RemainderToDebit();
		//rd.setRewardPoints((String)(balMap.get("remainingRewardPoints"))); // remaining points on partial reversal
		debit.setRemainderToDebit(Utility.truncateUptoTwoDecimal(contactsLoyalty.getRemainingRewardCurrency())); // remaining currency on partial reversal
		//debit.setRemainderToDebit(rd);
		additionalInfo.setDebit(debit);
		
		List<Credit> credList = new ArrayList<Credit>();
		additionalInfo.setCredit(credList);
		logger.info("-- Exited prepareDebitAddtionalInfo --");
		return additionalInfo;
	}

	private HoldBalance prepareHoldBalances(ContactsLoyalty contactsLoyalty, String activationPeriod) {
		logger.info("-- Entered prepareHoldBalances --");
		HoldBalance holdBalance = new HoldBalance();
		holdBalance.setActivationPeriod(activationPeriod);
		if(contactsLoyalty.getHoldAmountBalance() == null){
			holdBalance.setCurrency(Constants.STRING_NILL);
		}
		else{
			//double value = new BigDecimal(contactsLoyalty.getHoldAmountBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			String res = Utility.truncateUptoTwoDecimal(contactsLoyalty.getHoldAmountBalance());
			double value = Double.parseDouble(res);
			holdBalance.setCurrency(Constants.STRING_NILL+value);
		}
		holdBalance.setPoints(contactsLoyalty.getHoldPointsBalance() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contactsLoyalty.getHoldPointsBalance().intValue());
		logger.info("-- Exited prepareHoldBalances --");
		return holdBalance;
	}

	private LoyaltyTransactionChild createReturnTransaction(RequestHeader header, String amountType, OriginalReceipt originalRecipt, ContactsLoyalty loyalty,
			Long orgId, String ptsDiff, String amtDiff,Double earnedAmt, Double earnedPts, String earnType,
			String transactionId,Double enteredAmount, String entAmountType, String description, 
			Long redeemedOn,String key, Long specialRewardID, String rewardDiff, String itemInfo) {
		logger.info("-- Entered createReturnTransaction --");
		
		if(amountType !=null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)) return null;
		LoyaltyTransactionChild transaction = null;
		try{

			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(transactionId));
			transaction.setMembershipNumber(Constants.STRING_NILL+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			transaction.setRewardDifference(rewardDiff);
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setItemInfo(itemInfo);
			//transaction.setEnteredAmount(enteredAmount);
			String res = Utility.truncateUptoTwoDecimal(enteredAmount);
			if(res != null)
			transaction.setEnteredAmount(Double.parseDouble(res));
			transaction.setEnteredAmountType(entAmountType);
			transaction.setEarnType(earnType);
			transaction.setEarnedAmount(earnedAmt);
			transaction.setEarnedPoints(earnedPts);
			transaction.setAmountDifference(amtDiff);
			//transaction.setAmountDifference(Utility.truncateUptoTwoDecimal(Double.parseDouble(amtDiff)));
			transaction.setPointsDifference(ptsDiff);
			transaction.setOrgId(orgId);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_RETURN);
			transaction.setSubsidiaryNumber(header.getSubsidiaryNumber() != null && !header.getSubsidiaryNumber().trim().isEmpty() ? header.getSubsidiaryNumber().trim() : null);
			transaction.setStoreNumber(header.getStoreNumber());
			
			transaction.setReceiptNumber(header.getReceiptNumber() != null && !header.getReceiptNumber().trim().isEmpty() ? header.getReceiptNumber() : null);
			transaction.setSpecialRewardId(specialRewardID);
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
			if(!amountType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT)) {
				
				
				if(originalRecipt!=null && (originalRecipt.getDocSID()==null || originalRecipt.getDocSID().isEmpty()) 
						&& originalRecipt.getReceiptNumber()!=null && !originalRecipt.getReceiptNumber().isEmpty() && key!=null) {
					transaction.setDescription2(key);
				}
				else {
					transaction.setDescription2(originalRecipt != null && 
							originalRecipt.getDocSID() != null && !originalRecipt.getDocSID().isEmpty() ?
									"OR-"+originalRecipt.getDocSID()+Constants.STRING_NILL :"Membership-"+loyalty.getCardNumber());
				
				}
				
			}
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		logger.info("-- Exit createReturnTransaction --");
		return transaction;
		
	}

	
	private void updateContactLoyaltyBalances(double earnedVal, String earnType, 
			ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier) throws Exception {
		
		//Changes LPV
				logger.info("-- Entered updateContactLoyaltyBalances --");
				
				Boolean notAutoConvert=true;
				try {
				Status status = null;
				double loyaltyPoints = contactsLoyalty.getLoyaltyBalance() == null ? 0 : contactsLoyalty.getLoyaltyBalance();
				double totalLtyPoints = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0 : contactsLoyalty.getTotalLoyaltyEarned();
				double holdPoints = contactsLoyalty.getHoldPointsBalance() == null ? 0 : contactsLoyalty.getHoldPointsBalance();
				double loyaltyAmount = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
				double totalLoyaltyAmount = contactsLoyalty.getTotalGiftcardAmount() == null ? 0.0 : contactsLoyalty.getTotalGiftcardAmount();
				double holdAmount = contactsLoyalty.getHoldAmountBalance() == null ? 0 : contactsLoyalty.getHoldAmountBalance();
				double returnBal = earnedVal;
				
				//to find partial reversal in return
				LoyaltyProgram ltyProgram = findLoyaltyProgramByProgramId(contactsLoyalty.getProgramId().longValue(), contactsLoyalty.getUserId().longValue());		
				
				double amountDifferenceDbl = 0.0;
				long pointsDifference = 0;
				
				if(earnType != null && earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)){
					// partial reversal in return			
					if(tier.getPartialReversalFlag() == OCConstants.FLAG_YES){	
						double bal = 0;
						double finalDifference =0; //App 910
						long deductPoints = 0;
						long deductHoldPoints = 0;
						double totalConvertedPoints =0.0;
						if(holdPoints == 0 && loyaltyPoints == 0 && loyaltyAmount == 0){ //When the card balance is 0 App - 902
							logger.info("LoyaltPointsReversal");
							status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						}
						
						/*
						 * This case arrives when the currency balance exists but the points balance is zero.
						 * Another case need not be handled is when the program is auto conversion. 
						 * Both if blocks are modified for APP - 90.2	
						 * */
						else if(holdPoints == 0 && loyaltyPoints == 0 && !tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)) {
							logger.info("LoyaltPointsReversal2");
							status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);//When the card balance is 0 
						}
						logger.info("tier.getConversionType()===>"+tier.getConversionType());
						
						if(holdPoints > 0 && returnBal > 0){					
							bal = returnBal - holdPoints;
							if(bal >= 0){
								deductHoldPoints = (long) holdPoints;
								contactsLoyalty.setHoldPointsBalance(0.0);
								if(loyaltyPoints-bal<0) {//APP-945
								bal=bal-loyaltyPoints; //To calculate remainderToDebit as customer cannot pay in points.
								}
								returnBal = bal;
							}else{
								deductHoldPoints = (long) returnBal;
								contactsLoyalty.setHoldPointsBalance(holdPoints - (long)returnBal);
								returnBal = 0;
							}
							pointsDifference += deductHoldPoints;
							saveContactsLoyalty(contactsLoyalty);
							deductHoldPoints(contactsLoyalty, deductHoldPoints);
						}
						//changes done - for null check and when return bal is still >0 then only try this
						if(returnBal > 0 && tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) &&   
								tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 && 
								contactsLoyalty.getGiftcardBalance() != null && contactsLoyalty.getGiftcardBalance() > 0  ){
							returnBal =(long) returnBal;
							notAutoConvert=false;
							if(returnBal > loyaltyPoints) {
							totalConvertedPoints = getAutoConvertionReversalVal(contactsLoyalty, tier);//convert all currency to points
							contactsLoyalty.setGiftcardBalance(0.0);
							}
							totalConvertedPoints = totalConvertedPoints+ loyaltyPoints;
							bal = returnBal - totalConvertedPoints;
							if(bal >= 0){
								deductPoints = (long) totalConvertedPoints;
								contactsLoyalty.setLoyaltyBalance(0.0);
								//contactsLoyalty.setGiftcardBalance(0.0);APP-2143
							}else{
								deductPoints = (long) returnBal;
								contactsLoyalty.setLoyaltyBalance(totalConvertedPoints - (long)returnBal);
							}
							pointsDifference += deductPoints;
							if(contactsLoyalty.getLoyaltyBalance() > 0)
								contactsLoyalty = applyConversionRules(contactsLoyalty, tier);
							contactsLoyalty.setTotalLoyaltyEarned(totalLtyPoints-deductPoints);
							logger.info("contactsLoyalty.getLoyaltyBalance()===>"+contactsLoyalty.getLoyaltyBalance());
							saveContactsLoyalty(contactsLoyalty);
							deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductPoints);
							
							
						}
						/*
						 * Chnages App - 910 2.5.5.0
						 * Added totalConvertedPoints with loyalty points for the case of auto conversion.
						 * 
						 *  */
						if(loyaltyPoints > 0 && returnBal > 0 && notAutoConvert ){
							bal = returnBal - (loyaltyPoints+totalConvertedPoints);
							if(bal >= 0){
								deductPoints = (long) loyaltyPoints;
								contactsLoyalty.setLoyaltyBalance(0.0);
								returnBal = bal;
							}else{
							// If Difference<0 then we make it 0 and the value for deduction of points is already getting handled in auto-conversion.	App-910 
								deductPoints = (long) returnBal;
								finalDifference=(loyaltyPoints - deductPoints)<0?0.0:(loyaltyPoints - deductPoints);
								contactsLoyalty.setLoyaltyBalance(finalDifference);
								/*deductPoints = (long) returnBal; Changes 2.5.5.0
								contactsLoyalty.setLoyaltyBalance(loyaltyPoints - (long)returnBal);
								returnBal = 0;
								debitedRewardPoints = (long) returnBal + Constants.STRING_NILL;*/
							}
							pointsDifference += deductPoints;
							contactsLoyalty.setTotalLoyaltyEarned(totalLtyPoints-deductPoints);
							saveContactsLoyalty(contactsLoyalty);
							deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductPoints);
						}
						
					}else{
							
						long deductPoints = 0;
						long deductHoldPoints = 0;
						if((loyaltyPoints+holdPoints) >= earnedVal){
							logger.info("Earned points is less than available points bal...");
							if(returnBal <= holdPoints){
								deductHoldPoints = (long) returnBal;
								contactsLoyalty.setHoldPointsBalance(holdPoints - returnBal);
								returnBal = 0;
							}
							else{
								returnBal = returnBal - holdPoints;
								deductHoldPoints = (long)holdPoints;
								deductPoints = (long) returnBal;
								contactsLoyalty.setHoldPointsBalance(0.0);
								contactsLoyalty.setLoyaltyBalance(loyaltyPoints - (long)returnBal);
								contactsLoyalty.setTotalLoyaltyEarned(totalLtyPoints-returnBal);
								
							}
							pointsDifference += (deductPoints+deductHoldPoints);
							saveContactsLoyalty(contactsLoyalty);
							deductHoldPoints(contactsLoyalty, deductHoldPoints);
							deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductPoints);
						}else if(returnBal >0 && tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) && 
								tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 &&
										contactsLoyalty.getGiftcardBalance() != null && contactsLoyalty.getGiftcardBalance() > 0) {//changes done - for null check and when return bal is still >0 then only try this
							

							double totalConvertedPoitns = getAutoConvertionReversalVal(contactsLoyalty, tier);
							double totalIntoPoints = totalConvertedPoitns+ loyaltyPoints;
							/*double loyaltyBal = totalIntoPoints - (long)returnBal;
							contactsLoyalty.setGiftcardBalance(0.0);
							contactsLoyalty.setLoyaltyBalance(loyaltyBal);
							contactsLoyalty = applyConversionRules(contactsLoyalty, tier);
							contactsLoyalty.setTotalLoyaltyEarned(totalLtyPoints-(long)returnBal);
							saveContactsLoyalty(contactsLoyalty);
							deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductPoints);*/
							// fix for bug 845
							if(totalIntoPoints >= returnBal){
								deductPoints = (long)returnBal;
								double loyaltyBal = totalIntoPoints - (long)returnBal;
								contactsLoyalty.setGiftcardBalance(0.0);
								contactsLoyalty.setLoyaltyBalance(loyaltyBal);
								contactsLoyalty = applyConversionRules(contactsLoyalty, tier);
								contactsLoyalty.setTotalLoyaltyEarned(totalLtyPoints-(long)returnBal);
								saveContactsLoyalty(contactsLoyalty);
								deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductPoints);
								pointsDifference += deductPoints;
							}else{
								logger.info("Loyalty Auto Conversion");
								status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
										OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							}
						}
						//this is the place to make changes when points are insufficient then reverse the conversion n deduct
						else{
							logger.info("Loyalty Auto Conversion 2");
							status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							//return status;
						}
					
					}
					
				}
				else if(earnType != null && earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)){
					logger.debug("===entered into Amount flow===="+tier.getPartialReversalFlag());
					// partial reversal in returns
					if(tier.getPartialReversalFlag() == OCConstants.FLAG_YES){
						double deductAmount = 0.0;
						double deductHoldAmount = 0.0;
						double bal = 0;		
						if(holdAmount == 0 && loyaltyAmount == 0){ //When the card balance is 0 App - 902
							logger.info("Loyalty Type Amout");
							status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						}
							//remainingRewardCurrency = (long) earnedVal + Constants.STRING_NILL;
						if(holdAmount > 0 && returnBal > 0){	
							
							
							bal = returnBal - holdAmount;
							if(bal >= 0){
								deductHoldAmount = (long) holdAmount;
								contactsLoyalty.setHoldAmountBalance(0.0);
								returnBal = bal;
							}else{
								//deductHoldAmount = (long) returnBal; APP -944,release 2.5.6.0 Took out implicit conversion
								deductHoldAmount = returnBal;
								contactsLoyalty.setHoldAmountBalance(holdAmount - returnBal);
								returnBal = 0;
							}
							amountDifferenceDbl += deductHoldAmount;
							logger.debug("===deduct from hold first===="+deductHoldAmount + "returnBal =="+returnBal);
							saveContactsLoyalty(contactsLoyalty);
							deductHoldAmount(contactsLoyalty, deductHoldAmount);
						}
						if(loyaltyAmount > 0 && returnBal > 0){
							bal = returnBal - loyaltyAmount;
							if(bal >= 0){
								deductAmount = (long) loyaltyAmount;
								contactsLoyalty.setGiftcardBalance(0.0);
								returnBal = bal;
							}else{
								deductAmount = (long) returnBal;
								//logger.info("LPV===="+LPV+"SETTER==="+contactsLoyalty.getLifeTimePurchaseValue()+"card number=="+contactsLoyalty.getCardNumber());
								contactsLoyalty.setGiftcardBalance(new BigDecimal(loyaltyAmount - returnBal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

							}
							amountDifferenceDbl += returnBal;
							contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(totalLoyaltyAmount-deductAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							saveContactsLoyalty(contactsLoyalty);
							deductLoyaltyAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductAmount);
							
						}
					}else{

						double deductAmount = 0.0;
						double deductHoldAmount = 0.0;
						if((loyaltyAmount+holdAmount) >= earnedVal){
							logger.info("Earned amount is less than available amount bal...");
							if(returnBal <= holdAmount){
								deductHoldAmount = returnBal;
								contactsLoyalty.setHoldAmountBalance(holdAmount - returnBal);
								returnBal = 0;
							}
							else{
								returnBal = returnBal - holdAmount;
								deductHoldAmount = holdAmount;
								deductAmount = returnBal;
								contactsLoyalty.setHoldAmountBalance(0.0);
								//contactsLoyalty.setGiftcardBalance(loyaltyAmount - returnBal);
								contactsLoyalty.setGiftcardBalance(new BigDecimal(loyaltyAmount - returnBal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								//contactsLoyalty.setTotalGiftcardAmount(totalLoyaltyAmount-returnBal);
								contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(totalLoyaltyAmount-returnBal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							}
							amountDifferenceDbl += (deductHoldAmount+deductAmount);
				//		    contactsLoyalty.setLoyaltyBalance(loyaltyPoints + autoCnvrtPtsCredit); //TODO confirm once
							saveContactsLoyalty(contactsLoyalty);
//							createExpiryTransaction(contactsLoyalty, 0.0, (long)autoCnvrtPtsCredit, transChildId, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
							deductHoldAmount(contactsLoyalty, deductHoldAmount);
							deductLoyaltyAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductAmount);
						}
						else{
							logger.info("Loyalty Type Amout 2");
							status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							//return status;
						}
					
					}
				}
				logger.info("-- Exit updateContactLoyaltyBalances --");
				}
				catch(Exception e) {
					logger.error("Exception ::",e);
				}
			
	}
	
	private Map<String, Object> updateContactLoyaltyBalances(double earnedVal, String earnType, 
			ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier,double netReturnedAmt, String requestType,double actualReturnAmnt) throws Exception {//Changes LPV
		logger.info("-- Entered updateContactLoyaltyBalances --");
		
		Boolean notAutoConvert=true;
		try {
		Status status = null;
		double loyaltyPoints = contactsLoyalty.getLoyaltyBalance() == null ? 0 : contactsLoyalty.getLoyaltyBalance();
		double totalLtyPoints = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0 : contactsLoyalty.getTotalLoyaltyEarned();
		double holdPoints = contactsLoyalty.getHoldPointsBalance() == null ? 0 : contactsLoyalty.getHoldPointsBalance();
		double loyaltyAmount = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
		double totalLoyaltyAmount = contactsLoyalty.getTotalGiftcardAmount() == null ? 0.0 : contactsLoyalty.getTotalGiftcardAmount();
		double holdAmount = contactsLoyalty.getHoldAmountBalance() == null ? 0 : contactsLoyalty.getHoldAmountBalance();
		double returnBal = earnedVal;
		
		
		double CRV = contactsLoyalty.getCummulativeReturnValue() == null  ? 0 : contactsLoyalty.getCummulativeReturnValue();
		double amountToIgnore = contactsLoyalty.getAmountToIgnore() == null  ? 0 : contactsLoyalty.getAmountToIgnore();

		//double LPV = contactsLoyalty.getLifeTimePurchaseValue() == null  ? 0 : contactsLoyalty.getLifeTimePurchaseValue();
		
		//to find partial reversal in return
		LoyaltyProgram ltyProgram = findLoyaltyProgramByProgramId(contactsLoyalty.getProgramId().longValue(), contactsLoyalty.getUserId().longValue());		


		logger.info("loyaltyPoints="+loyaltyPoints);
		logger.info("loyaltyAmount="+loyaltyAmount);
		logger.info("holdPoints="+holdPoints);
		logger.info("holdAmount="+holdAmount);
		logger.info("returnBal="+returnBal);
		logger.info("earnedVal="+earnedVal);
		
		
		Map<String, Object> balMap = new HashMap<String, Object>();
		String debitedRewardPoints = Constants.STRING_NILL;
		String debitedHoldPoints = Constants.STRING_NILL;
		String debitedRewardCurrency = Constants.STRING_NILL;
		String debitedHoldCurrency = Constants.STRING_NILL;
		String remainingRewardPoints = Constants.STRING_NILL;
		String remainingRewardCurrency = "0.0";
		
		String amountDifference = Constants.STRING_NILL;
		String pointDifference = Constants.STRING_NILL;
		double amountDifferenceDbl = 0.0;
		long pointsDifference = 0;
		//if(LPV != 0 && LPV>earnedVal)contactsLoyalty.setLifeTimePurchaseValue(LPV-earnedVal);
	//	if(CRV != 0 && CRV>=netReturnedAmt)
			contactsLoyalty.setCummulativeReturnValue(CRV+actualReturnAmnt);//Changes LPV
			//contactsLoyalty.setAmountToIgnore(amountToIgnore+netReturnedAmt);
		logger.info("actualReturnAmnt ======= "+actualReturnAmnt);	
		logger.info("netReturnedAmt==========="+netReturnedAmt);
		logger.info("amountToIgnore==========="+(amountToIgnore+netReturnedAmt));
		logger.info("LPV===="+CRV+"SETTER==="+contactsLoyalty.getCummulativeReturnValue()+"card number=="+contactsLoyalty.getCardNumber());
		if(requestType !=null && requestType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))saveContactsLoyalty(contactsLoyalty);
		
		if(earnType != null && earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)){
			// partial reversal in return			
			if(tier.getPartialReversalFlag() == OCConstants.FLAG_YES){	
				double bal = 0;
				double finalDifference =0; //App 910
				long deductPoints = 0;
				long deductHoldPoints = 0;
				double totalConvertedPoints =0.0;
				if(holdPoints == 0 && loyaltyPoints == 0 && loyaltyAmount == 0){ //When the card balance is 0 App - 902
					logger.info("LoyaltPointsReversal");
					/*status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);*/
					remainingRewardCurrency = getConversionVal(returnBal, tier) != null ? getConversionVal(returnBal, tier) + Constants.STRING_NILL : Constants.STRING_NILL;
					
				}
				
				
				/*
				 * This case arrives when the currency balance exists but the points balance is zero.
				 * Another case need not be handled is when the program is auto conversion. 
				 * Both if blocks are modified for APP - 90.2	
				 * */
				else if(holdPoints == 0 && loyaltyPoints == 0 && 
						!tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)) {
					logger.info("LoyaltPointsReversal2");
					/*status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);//When the card balance is 0 
*/				
					remainingRewardCurrency = getConversionVal(returnBal, tier) != null ? getConversionVal(returnBal, tier) + Constants.STRING_NILL : Constants.STRING_NILL;	
				}
				logger.info("tier.getConversionType()===>"+tier.getConversionType());
				
				if(holdPoints > 0 && returnBal > 0){
					bal = returnBal - holdPoints;
					logger.info("holdPoints > 0 && returnBal > 0 && bal && loyalty points==="+holdPoints+" "+returnBal+" "+bal+" "+loyaltyPoints);
					if(bal >= 0){
						deductHoldPoints = (long) holdPoints;
						//if(requestType !=null && requestType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))
						contactsLoyalty.setHoldPointsBalance(0.0);
						if(loyaltyPoints-bal<0) {//APP-945
						bal=bal-loyaltyPoints; //To calculate remainderToDebit as customer cannot pay in points.
						remainingRewardCurrency = getConversionVal(bal, tier) != null ? getConversionVal(bal, tier) + Constants.STRING_NILL : Constants.STRING_NILL;
						}
						returnBal = bal;
						debitedHoldPoints = (long) holdPoints + Constants.STRING_NILL;
					}else{
						deductHoldPoints = (long) returnBal;
						//if(requestType !=null && requestType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL))
						contactsLoyalty.setHoldPointsBalance(holdPoints - (long)returnBal);
						returnBal = 0;
						debitedHoldPoints = (long) deductHoldPoints + Constants.STRING_NILL;//Changed to deductHoldPoints instead of returnBal. APP-944 ,Release-2.5.6.0 
					}
					pointsDifference += deductHoldPoints;
					logger.info("CRV===="+CRV+"SETTER==="+contactsLoyalty.getCummulativePurchaseValue()+"card number=="+contactsLoyalty.getCardNumber());
					if(requestType !=null && requestType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)){
						saveContactsLoyalty(contactsLoyalty);
						deductHoldPoints(contactsLoyalty, deductHoldPoints);
					}
				}
				//changes done - for null check and when return bal is still >0 then only try this
				if(returnBal > 0 && tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) &&   
						tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 && 
						contactsLoyalty.getGiftcardBalance() != null && contactsLoyalty.getGiftcardBalance() > 0  ){
					returnBal =(long) returnBal;
					notAutoConvert=false;
					if(returnBal > loyaltyPoints) {
					totalConvertedPoints = getAutoConvertionReversalVal(contactsLoyalty, tier);//convert all currency to points
					contactsLoyalty.setGiftcardBalance(0.0);
					}
					totalConvertedPoints = totalConvertedPoints+ loyaltyPoints;
					bal = returnBal - totalConvertedPoints;
					if(bal >= 0){
						deductPoints = (long) totalConvertedPoints;
						contactsLoyalty.setLoyaltyBalance(0.0);
						//contactsLoyalty.setGiftcardBalance(0.0);APP-2143
						debitedRewardPoints = (long) totalConvertedPoints + Constants.STRING_NILL;
						remainingRewardCurrency = getConversionVal(bal, tier) != null ? getConversionVal(bal, tier) + Constants.STRING_NILL : Constants.STRING_NILL;
					}else{
						deductPoints = (long) returnBal;
						contactsLoyalty.setLoyaltyBalance(totalConvertedPoints - (long)returnBal);
						debitedRewardPoints = (long) returnBal + Constants.STRING_NILL;
					}
					pointsDifference += deductPoints;
					if(contactsLoyalty.getLoyaltyBalance() > 0)
						contactsLoyalty = applyConversionRules(contactsLoyalty, tier);
					contactsLoyalty.setTotalLoyaltyEarned(totalLtyPoints-deductPoints);
					logger.info("contactsLoyalty.getLoyaltyBalance()===>"+contactsLoyalty.getLoyaltyBalance());
					logger.info("CRV===="+CRV+"SETTER==="+contactsLoyalty.getCummulativeReturnValue()+"card number=="+contactsLoyalty.getCardNumber());
					if(requestType !=null && requestType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)){
						saveContactsLoyalty(contactsLoyalty);
						deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductPoints);
					}
					
					
				}
				/*
				 * Chnages App - 910 2.5.5.0
				 * Added totalConvertedPoints with loyalty points for the case of auto conversion.
				 * 
				 *  */
				if(loyaltyPoints > 0 && returnBal > 0 && notAutoConvert ){
					bal = returnBal - (loyaltyPoints+totalConvertedPoints);
					logger.info("bal && totalConvertedPoints====2===="+bal+" "+totalConvertedPoints);
					if(bal >= 0){
						deductPoints = (long) loyaltyPoints;
						contactsLoyalty.setLoyaltyBalance(0.0);
					    remainingRewardPoints = bal + Constants.STRING_NILL; //APP - 940 Introduce to display remainingRewardPoints in return response. 
						remainingRewardCurrency = getConversionVal(bal, tier) != null ? getConversionVal(bal, tier) + Constants.STRING_NILL : Constants.STRING_NILL;
						returnBal = bal;
						debitedRewardPoints = (long) loyaltyPoints + Constants.STRING_NILL;
					}else{
					// If Difference<0 then we make it 0 and the value for deduction of points is already getting handled in auto-conversion.	App-910 
						deductPoints = (long) returnBal;
						finalDifference=(loyaltyPoints - deductPoints)<0?0.0:(loyaltyPoints - deductPoints);
						contactsLoyalty.setLoyaltyBalance(finalDifference);
						debitedRewardPoints = deductPoints + Constants.STRING_NILL;
						/*deductPoints = (long) returnBal; Changes 2.5.5.0
						contactsLoyalty.setLoyaltyBalance(loyaltyPoints - (long)returnBal);
						returnBal = 0;
						debitedRewardPoints = (long) returnBal + Constants.STRING_NILL;*/
					}
					pointsDifference += deductPoints;
					contactsLoyalty.setTotalLoyaltyEarned(totalLtyPoints-deductPoints);
					logger.info("CRV===="+CRV+"SETTER==="+contactsLoyalty.getCummulativeReturnValue()+"card number=="+contactsLoyalty.getCardNumber());
					if(requestType !=null && requestType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)){
						saveContactsLoyalty(contactsLoyalty);
						deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductPoints);
					}
				}
				
			}else{
					
				long deductPoints = 0;
				long deductHoldPoints = 0;
				if((loyaltyPoints+holdPoints) >= earnedVal){
					logger.info("Earned points is less than available points bal...");
					if(returnBal <= holdPoints){
						deductHoldPoints = (long) returnBal;
						contactsLoyalty.setHoldPointsBalance(holdPoints - returnBal);
						debitedHoldPoints = (long) returnBal+Constants.STRING_NILL;
						returnBal = 0;
					}
					else{
						returnBal = returnBal - holdPoints;
						deductHoldPoints = (long)holdPoints;
						deductPoints = (long) returnBal;
							
							contactsLoyalty.setHoldPointsBalance(0.0);
							contactsLoyalty.setLoyaltyBalance(loyaltyPoints - (long)returnBal);
							contactsLoyalty.setTotalLoyaltyEarned(totalLtyPoints-returnBal);
						
						debitedHoldPoints = (long) holdPoints+Constants.STRING_NILL;
						debitedRewardPoints = deductPoints+Constants.STRING_NILL;
					}
					pointsDifference += (deductPoints+deductHoldPoints);
					logger.info("CRV===="+CRV+"SETTER==="+contactsLoyalty.getCummulativeReturnValue()+"card number=="+contactsLoyalty.getCardNumber());
					if(requestType !=null && requestType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)){
						
						saveContactsLoyalty(contactsLoyalty);
						deductHoldPoints(contactsLoyalty, deductHoldPoints);
						deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductPoints);
						
					}
				}else if(returnBal >0 && tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) && 
						tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 &&
								contactsLoyalty.getGiftcardBalance() != null && contactsLoyalty.getGiftcardBalance() > 0) {//changes done - for null check and when return bal is still >0 then only try this
					

					double totalConvertedPoitns = getAutoConvertionReversalVal(contactsLoyalty, tier);
					double totalIntoPoints = totalConvertedPoitns+ loyaltyPoints;
					/*double loyaltyBal = totalIntoPoints - (long)returnBal;
					contactsLoyalty.setGiftcardBalance(0.0);
					contactsLoyalty.setLoyaltyBalance(loyaltyBal);
					contactsLoyalty = applyConversionRules(contactsLoyalty, tier);
					contactsLoyalty.setTotalLoyaltyEarned(totalLtyPoints-(long)returnBal);
					saveContactsLoyalty(contactsLoyalty);
					deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductPoints);*/
					// fix for bug 845
					if(totalIntoPoints >= returnBal){
						deductPoints = (long)returnBal;
						double loyaltyBal = totalIntoPoints - (long)returnBal;
						contactsLoyalty.setGiftcardBalance(0.0);
						contactsLoyalty.setLoyaltyBalance(loyaltyBal);
						contactsLoyalty = applyConversionRules(contactsLoyalty, tier);
						contactsLoyalty.setTotalLoyaltyEarned(totalLtyPoints-(long)returnBal);
						logger.info("CRV===="+CRV+"SETTER==="+contactsLoyalty.getCummulativeReturnValue()+"card number=="+contactsLoyalty.getCardNumber());
						if(requestType !=null && requestType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)){
							saveContactsLoyalty(contactsLoyalty);
							deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductPoints);
						}
						pointsDifference += deductPoints;
					}else{
						logger.info("Loyalty Auto Conversion");
						status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					}
				}
				//this is the place to make changes when points are insufficient then reverse the conversion n deduct
				else{
					logger.info("Loyalty Auto Conversion 2");
					status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					//return status;
				}
			
			}
			
		}
		else if(earnType != null && earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)){
			logger.debug("===entered into Amount flow===="+tier.getPartialReversalFlag());
			// partial reversal in returns
			if(tier.getPartialReversalFlag() == OCConstants.FLAG_YES){
				double deductAmount = 0.0;
				double deductHoldAmount = 0.0;
				double bal = 0;		
				if(holdAmount == 0 && loyaltyAmount == 0){ //When the card balance is 0 App - 902
					logger.info("Loyalty Type Amout");
					/*status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);*/
					
					remainingRewardCurrency = Utility.truncateUptoTwoDecimal(returnBal);
				}
					//remainingRewardCurrency = (long) earnedVal + Constants.STRING_NILL;
				if(holdAmount > 0 && returnBal > 0){	
					
					logger.info("holdAmount > 0 && returnBal > 0"+holdAmount+" "+returnBal);
					bal = returnBal - holdAmount;
					if(bal >= 0){
						deductHoldAmount = (long) holdAmount;
						contactsLoyalty.setHoldAmountBalance(0.0);
						remainingRewardCurrency = bal + Constants.STRING_NILL;//?
						returnBal = bal;
						logger.info("retun bal and deduct hold amnt====1====="+returnBal+" "+deductHoldAmount);
						debitedHoldCurrency = Utility.truncateUptoTwoDecimal( holdAmount) + Constants.STRING_NILL;
					}else{
						//deductHoldAmount = (long) returnBal; APP -944,release 2.5.6.0 Took out implicit conversion
						deductHoldAmount = returnBal;
						contactsLoyalty.setHoldAmountBalance(holdAmount - returnBal);
						returnBal = 0;
						logger.info("retun bal and deduct hold amnt====1====="+returnBal+" "+deductHoldAmount);
						debitedHoldCurrency =  Utility.truncateUptoTwoDecimal(deductHoldAmount) + Constants.STRING_NILL;//Changed to deductHoldAmount,APP - 944
					}
					amountDifferenceDbl += deductHoldAmount;
					logger.debug("===deduct from hold first===="+deductHoldAmount + "returnBal =="+returnBal);
					if(requestType !=null && requestType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)){
						saveContactsLoyalty(contactsLoyalty);
						deductHoldAmount(contactsLoyalty, deductHoldAmount);
					}
				}
				if(loyaltyAmount > 0 && returnBal > 0){
					bal = returnBal - loyaltyAmount;
					if(bal >= 0){
						deductAmount = (long) loyaltyAmount;
						contactsLoyalty.setGiftcardBalance(0.0);
						remainingRewardCurrency = bal + Constants.STRING_NILL;
						returnBal = bal;
						logger.info("retunbal and deduct amnt====2====="+returnBal+" "+deductAmount);
						debitedRewardCurrency =  Utility.truncateUptoTwoDecimal( loyaltyAmount) + Constants.STRING_NILL;
					}else{
						deductAmount = (long) returnBal;
						//logger.info("LPV===="+LPV+"SETTER==="+contactsLoyalty.getLifeTimePurchaseValue()+"card number=="+contactsLoyalty.getCardNumber());
						contactsLoyalty.setGiftcardBalance(new BigDecimal(loyaltyAmount - returnBal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						debitedRewardCurrency =  Utility.truncateUptoTwoDecimal( returnBal) + Constants.STRING_NILL;
						remainingRewardCurrency = "0";//APP - 945,Release 2.5.6.0 Changes
						logger.info("else retunbal and deduct amnt====2====="+returnBal+" "+deductAmount);

					}
					amountDifferenceDbl += Double.parseDouble(debitedRewardCurrency);
					contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(totalLoyaltyAmount-deductAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					logger.info("CRV===="+CRV+"SETTER==="+contactsLoyalty.getCummulativeReturnValue()+"card number=="+contactsLoyalty.getCardNumber());
					if(requestType !=null && requestType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)){
						saveContactsLoyalty(contactsLoyalty);
						deductLoyaltyAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductAmount);
					}
					
					logger.debug("===deduct from active bal===="+deductAmount + "debitedRewardCurrency =="+debitedRewardCurrency);
				}
			}else{

				double deductAmount = 0.0;
				double deductHoldAmount = 0.0;
				if((loyaltyAmount+holdAmount) >= earnedVal){
					logger.info("Earned amount is less than available amount bal...");
					if(returnBal <= holdAmount){
						deductHoldAmount = returnBal;
						contactsLoyalty.setHoldAmountBalance(holdAmount - returnBal);
						debitedHoldCurrency = returnBal+Constants.STRING_NILL;
						returnBal = 0;
					}
					else{
						returnBal = returnBal - holdAmount;
						deductHoldAmount = holdAmount;
						deductAmount = returnBal;
						contactsLoyalty.setHoldAmountBalance(0.0);
						//contactsLoyalty.setGiftcardBalance(loyaltyAmount - returnBal);
						contactsLoyalty.setGiftcardBalance(new BigDecimal(loyaltyAmount - returnBal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						//contactsLoyalty.setTotalGiftcardAmount(totalLoyaltyAmount-returnBal);
						contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(totalLoyaltyAmount-returnBal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						debitedHoldCurrency = holdAmount+Constants.STRING_NILL;
						debitedRewardCurrency = deductAmount+Constants.STRING_NILL;
					}
					amountDifferenceDbl += (deductHoldAmount+deductAmount);
		//		    contactsLoyalty.setLoyaltyBalance(loyaltyPoints + autoCnvrtPtsCredit); //TODO confirm once
					logger.info("CRV===="+CRV+"SETTER==="+contactsLoyalty.getCummulativeReturnValue()+"card number=="+contactsLoyalty.getCardNumber());
					if(requestType !=null && requestType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)){
						
						saveContactsLoyalty(contactsLoyalty);
	//					createExpiryTransaction(contactsLoyalty, 0.0, (long)autoCnvrtPtsCredit, transChildId, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
						deductHoldAmount(contactsLoyalty, deductHoldAmount);
						deductLoyaltyAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductAmount);
						
					}
				}
				else{
					logger.info("Loyalty Type Amout 2");
					status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					//return status;
				}
			
			}
		}
		if(amountDifferenceDbl >0) amountDifference = Constants.STRING_NILL+amountDifferenceDbl;
		if(pointsDifference > 0) pointDifference = Constants.STRING_NILL+pointsDifference;
//		balMap.put("autoCnvrtPtsCredit", autoCnvrtPtsCredit);
		balMap.put("debitedHoldPoints", debitedHoldPoints);
		balMap.put("debitedRewardPoints", debitedRewardPoints);
		balMap.put("debitedHoldCurrency", debitedHoldCurrency);
		balMap.put("debitedRewardCurrency", debitedRewardCurrency);
		balMap.put("remainingRewardPoints", remainingRewardPoints);
		balMap.put("remainingRewardCurrency", remainingRewardCurrency);
		balMap.put("AmountDifference", amountDifference);
		balMap.put("PointsDifference", pointDifference);
		balMap.put("status", status);
		logger.info("debitedRewardCurrency===>"+debitedRewardCurrency);
		logger.info("remainingRewardCurrency===>"+remainingRewardCurrency);
		logger.info("-- Exit updateContactLoyaltyBalances --");
		return balMap;
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
			return null;
		}
	}
	
	
	private void deductPointsFromExpiryTable(ContactsLoyalty loyalty, Long userId, long subPoints) throws Exception{
		logger.info("-- Entered deductPointsFromExpiryTable --");
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
		logger.info("-- Exit deductPointsFromExpiryTable --");	
		//createTransactionForExpiry(loyalty, subPoints-remPoints, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTS_EXP);
	}

	private void deductLoyaltyAmtFromExpiryTable(ContactsLoyalty loyalty, Long userId, double subAmt) throws Exception{
		logger.info("-- Entered deductLoyaltyAmtFromExpiryTable --");
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
		logger.info("-- Exit deductLoyaltyAmtFromExpiryTable --");
	}

	private void deductHoldAmount(ContactsLoyalty loyalty, double balanceToSub) {
		logger.info("-- Entered deductHoldAmount --");
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
			logger.info("-- Entered deductHoldAmount --");
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}
	
	private void deductHoldPoints(ContactsLoyalty loyalty, long balanceToSub) {
		logger.info("-- Entered deductHoldAmount --");

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
			logger.info("-- Exit deductHoldAmount --");

		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}
	
	private void saveContactsLoyalty(ContactsLoyalty contactsLoyalty) throws Exception {
		logger.info("-- Entered saveContactsLoyalty --");
		ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		loyaltyDao.saveOrUpdate(contactsLoyalty);
		logger.info("-- Exit saveContactsLoyalty --");
	}

	private LoyaltyReturnTransactionResponse mobileBasedReturnTransaction(LoyaltyReturnTransactionRequest returnRequest, ContactsLoyalty contactsLoyalty, ResponseHeader responseHeader,
			RequestHeader header, Amount amount, OriginalReceipt originalRecpt, Users user, String requestJson) throws Exception{
		logger.info("-- Entered mobileBasedReturnTransaction --");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;

		LoyaltyProgram loyaltyProgram = findActiveMobileProgram(contactsLoyalty.getProgramId());

		if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equals(loyaltyProgram.getStatus())){
			status = new Status("111522", PropertyUtil.getErrorMessage(111522, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;
		}
		logger.info("-- Exit mobileBasedReturnTransaction --");
		return performLoyaltySCReturn(returnRequest,header, amount, originalRecpt, responseHeader, loyaltyProgram, contactsLoyalty.getCardNumber(), user, requestJson);
	}

	private LoyaltyProgram findActiveMobileProgram(Long programId) throws Exception {
		logger.info("-- Entered findActiveMobileProgram --");
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		logger.info("-- Exit findActiveMobileProgram --");
		return loyaltyProgramDao.findById(programId);
	}

	private Status validateStoreNumberExclusion(RequestHeader header, LoyaltyProgram program, 
			LoyaltyProgramExclusion loyaltyExclusion) throws Exception {
		logger.info("-- Entered validateStoreNumberExclusion --");
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
		logger.info("-- Exit validateStoreNumberExclusion --");
		return status;
	}

	private MembershipResponse prepareMembershipResponse(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier, 
			LoyaltyProgram program) throws Exception {
		logger.info("-- Entered prepareMembershipResponse --");
		MembershipResponse membershipResponse = new MembershipResponse();

		if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
			membershipResponse.setCardNumber(Constants.STRING_NILL+contactsLoyalty.getCardNumber());
			membershipResponse.setCardPin(contactsLoyalty.getCardPin());
			membershipResponse.setPhoneNumber(Constants.STRING_NILL);
		}
		else{
			membershipResponse.setCardNumber(Constants.STRING_NILL);
			membershipResponse.setCardPin(Constants.STRING_NILL);
			membershipResponse.setPhoneNumber(Constants.STRING_NILL+contactsLoyalty.getCardNumber());
		}
		if(program.getTierEnableFlag() == OCConstants.FLAG_YES && tier != null){
			membershipResponse.setTierLevel(tier.getTierType());
			membershipResponse.setTierName(tier.getTierName());
		}
		else{
			membershipResponse.setTierLevel(Constants.STRING_NILL);
			membershipResponse.setTierName(Constants.STRING_NILL);
		}

		if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
			if(program.getGiftMembrshpExpiryFlag() == 'Y'){
				membershipResponse.setExpiry(LoyaltyProgramHelper.getGiftMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), 
						program.getGiftMembrshpExpiryDateType(), program.getGiftMembrshpExpiryDateValue()));
			}
			else{
				membershipResponse.setExpiry(Constants.STRING_NILL);
			}
		}
		else{
			boolean upgdFlag = false;
			if(program.getMbrshipExpiryOnLevelUpgdFlag() == 'Y'){
				upgdFlag = true;
			}
			if(program.getMembershipExpiryFlag() == 'Y' && tier != null && tier.getMembershipExpiryDateType() != null 
					&& tier.getMembershipExpiryDateValue() != null){
				membershipResponse.setExpiry(LoyaltyProgramHelper.getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), contactsLoyalty.getTierUpgradedDate(), 
						upgdFlag, tier.getMembershipExpiryDateType(), tier.getMembershipExpiryDateValue()));
			}
			else{
				membershipResponse.setExpiry(Constants.STRING_NILL);
			}
		}
		logger.info("-- Exit prepareMembershipResponse --");
		return membershipResponse;
	}

	private LoyaltyProgramExclusion getLoyaltyExclusion(Long programId) throws Exception {
		try{
			logger.info("-- Entered getLoyaltyExclusion --");
			LoyaltyProgramExclusionDao exclusionDao = (LoyaltyProgramExclusionDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO);
			logger.info("-- Exit getLoyaltyExclusion --");
			return exclusionDao.getExlusionByProgId(programId);
		}catch(Exception e){
			logger.error("Exception in getting loyalty exclusion ..", e);
		}
		return null;
	}

	private LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{
		logger.info("-- Entered getLoyaltyTier --");
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		logger.info("-- Exit getLoyaltyTier --");
		return tierDao.getTierById(tierId);

	}

	private ContactsLoyalty findContactLoyalty(String cardNumber, Long programId, Long userId) throws Exception {
		logger.info("-- Entered findContactLoyalty --");
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		logger.info("-- Exit findContactLoyalty --");
		return loyaltyDao.findByProgram(cardNumber, programId, userId);
	}
	private ContactsLoyalty findContactLoyalty(String cardNumber, Long userId) throws Exception {
		logger.info("-- Entered findContactLoyalty --");
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		logger.info("-- Exit findContactLoyalty --");
		return loyaltyDao.findContLoyaltyByCardId(userId, cardNumber);
	}

	private LoyaltyCardSet findLoyaltyCardSetByCardsetId(Long cardSetId, Long userId) throws Exception {
		logger.info("-- Entered findLoyaltyCardSetByCardsetId --");
		LoyaltyCardSetDao cardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		logger.info("-- Exit findLoyaltyCardSetByCardsetId --");
		return cardSetDao.findByCardSetId(cardSetId);

	}
	private LoyaltyProgram findLoyaltyProgramByProgramId(Long programId, Long userId) throws Exception {
		logger.info("-- Entered findLoyaltyProgramByProgramId --");
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		logger.info("-- Exit findLoyaltyProgramByProgramId --");
		return loyaltyProgramDao.findByIdAndUserId(programId, userId);
	}

	private LoyaltyCards findLoyaltyCardByUserId(String cardNumber, Long userId) throws Exception {
		logger.info("-- Entered findLoyaltyCardByUserId --");
		LoyaltyCardsDao loyaltyCardDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		logger.info("-- Exit findLoyaltyCardByUserId --");
		return loyaltyCardDao.findByCardNoAnduserId(cardNumber, userId);

	}

	private List<Balance> prepareBalancesObject(ContactsLoyalty loyalty, String pointsDiff, String amountDiff, String giftDiff) throws Exception{
		logger.info("-- Entered prepareBalancesObject --");
		List<Balance> balancesList = null;
		Balance pointBalances = null;
		Balance amountBalances = null;
		Balance giftBalances = null;
		balancesList = new ArrayList<Balance>();

		pointBalances = new Balance();
		pointBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		pointBalances.setValueCode(OCConstants.LOYALTY_TYPE_POINTS);
		pointBalances.setAmount(loyalty.getLoyaltyBalance() == null ? Constants.STRING_NILL : Constants.STRING_NILL+loyalty.getLoyaltyBalance().intValue());
		pointBalances.setDifference(pointsDiff);

		amountBalances = new Balance();
		amountBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		amountBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		amountBalances.setDifference(amountDiff);
		if(loyalty.getGiftcardBalance() == null){
			amountBalances.setAmount(Constants.STRING_NILL);
		}
		else{
			//double value = new BigDecimal(loyalty.getGiftcardBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			amountBalances.setAmount(Utility.truncateUptoTwoDecimal(Constants.STRING_NILL+loyalty.getGiftcardBalance()));//APP - 991
		}

		giftBalances = new Balance();
		giftBalances.setType(OCConstants.LOYALTY_TYPE_GIFT);
		giftBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		if(loyalty.getGiftBalance() == null){
			giftBalances.setAmount(Constants.STRING_NILL);
		}
		else{
			//double value = new BigDecimal(loyalty.getGiftBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			giftBalances.setAmount(Constants.STRING_NILL+loyalty.getGiftBalance());
		}
		giftBalances.setDifference(giftDiff);

		balancesList.add(pointBalances);
		balancesList.add(amountBalances);
		balancesList.add(giftBalances);
		logger.info("-- Exit prepareBalancesObject --");
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
	private List<Contacts> findOCContact(Contacts jsonContact, Long userId,Users user) throws Exception {
		logger.info("Entered findOCContact method >>>>");
		POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(userId, Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
		List<Contacts> dbContactList = contactsDao.findMatchedContactListByUniqPriority(priorMap, jsonContact, userId,user);
		logger.info("Exited findOCContact method >>>>");
		return dbContactList;
	}
	private Contacts findSingleOCContact(Contacts jsonContact, Long userId,Users user) throws Exception {
		logger.info("Entered findOCContact method >>>>");
		POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(userId, Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
		List<Contacts> dbContactList = contactsDao.findMatchedContactListByUniqPriority(priorMap, jsonContact, userId,user);
		logger.info("Exited findOCContact method >>>>");
		return dbContactList != null && !dbContactList.isEmpty() ? dbContactList.get(0) : null;
	}
	private List<ContactsLoyalty> findEnrollListByMobile(String mobile, Long userId) throws Exception {
		logger.info("Entered findEnrollListByMobile >>>>");
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		logger.info("Exit findEnrollListByMobile >>>>");
		return loyaltyDao.findMembershipByMobile(mobile, userId);
	}

	private ContactsLoyalty findContactLoyaltyByMobile(String mobile, Users user) throws Exception {
		logger.info("Entered findContactLoyaltyByMobile >>>>");
		Long userId=user.getUserId();
		String phoneNumber=LoyaltyProgramHelper.preparePhoneNumber(user,mobile);//APP-1208
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		logger.info("Exit findContactLoyaltyByMobile >>>>");
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
		logger.info("Entered getUser >>>>");
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findUserByToken(completeUserName, userToken);
		logger.info("Exit getUser >>>>");
		return user;
	}

	
	private List<RetailProSalesCSV> getItemsOfOR(OriginalReceipt originalRcpt, List<SkuDetails> itemsList , Long userID) throws Exception{
		
		String itemSIDs = Constants.STRING_NILL;
		logger.info("inside getItemsOfOR=== docsId=="+originalRcpt.getDocSID()+" receiptNumber=="+ 
				originalRcpt.getReceiptNumber()+" subsidary num==="+ originalRcpt.getSubsidiaryNumber()+" storeNumber=="+ originalRcpt.getStoreNumber());
		
		RetailProSalesDao retailProSalesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
		List<RetailProSalesCSV> retList = retailProSalesDao.findBy(userID, originalRcpt.getDocSID(), 
				originalRcpt.getReceiptNumber(),  originalRcpt.getSubsidiaryNumber(), originalRcpt.getStoreNumber() );
		return retList; 
		
		
	}//validateItemsOfOR
	
	private String getFieldValue(LoyaltyReturnTransactionRequest enrollRequest,POSMapping posMapping,String memType)throws BaseServiceException {
		String fieldValue=null;
		logger.debug("-------entered getFieldValue---------");
		if(posMapping.getCustomFieldName().equalsIgnoreCase("street")) {
			fieldValue = enrollRequest.getCustomer().getAddressLine1();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("address two")) {
			fieldValue = enrollRequest.getCustomer().getAddressLine2();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("email")) {
			fieldValue = enrollRequest.getCustomer().getEmailAddress();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("mobile")) {
			if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(memType) && 
					enrollRequest.getMembership().getPhoneNumber() != null && enrollRequest.getMembership().getPhoneNumber().trim().length() > 0) {
				fieldValue = enrollRequest.getMembership().getPhoneNumber().trim();
			}
			else {
				fieldValue = enrollRequest.getCustomer().getPhone();
			}
//			fieldValue = enrollRequest.getCustomer().getPhone();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("first name")) {
			fieldValue = enrollRequest.getCustomer().getFirstName();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("last name")) {
			fieldValue = enrollRequest.getCustomer().getLastName();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("city")) {
			fieldValue = enrollRequest.getCustomer().getCity();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("state")) {
			fieldValue = enrollRequest.getCustomer().getState();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("country")) {
			fieldValue = enrollRequest.getCustomer().getCountry();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("zip")) {
			fieldValue = enrollRequest.getCustomer().getPostal();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("gender")) {
			fieldValue = enrollRequest.getCustomer().getGender();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("customer id")) {
			fieldValue = enrollRequest.getCustomer().getCustomerId();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("birthday")) {
			fieldValue = enrollRequest.getCustomer().getBirthday();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("anniversary")) {
			fieldValue = enrollRequest.getCustomer().getAnniversary();
			return fieldValue;
		}
		logger.debug("-------exit  getFieldValue---------");
		return fieldValue;
	}
		private String getDateFormattedData(POSMapping posMapping, String fieldValue) throws BaseServiceException{
			String dataTypeStr = posMapping.getDataType();
			String dateFieldValue = null;
			logger.debug("-------entered getDateFormattedData---------");
			//String custfieldName = posMapping.getCustomFieldName();
			if(posMapping.getDataType().trim().startsWith("Date")) {
				try {
					String dateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
					if(!Utility.validateDate(fieldValue, dateFormat)) {
						return null;
					}
					DateFormat formatter ; 
					Date date ; 
					formatter = new SimpleDateFormat(dateFormat);
					date = (Date)formatter.parse(fieldValue); 
					Calendar cal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
					cal.setTime(date);
					dateFieldValue= MyCalendar.calendarToString(cal, MyCalendar.dateFormatMap.get(dateFormat));
				} catch (Exception e) {
					logger.error("Exception  ::", e);
					throw new BaseServiceException("Exception occured while processing getDateFormattedData::::: ", e);
				}
			}
			logger.debug("-------exit  getDateFormattedData---------");
			return dateFieldValue;
			}//getDateFormattedData
	
		
		private Contacts setContactFields(Contacts inputContact,List<POSMapping> contactPOSMap, LoyaltyReturnTransactionRequest returnRequest, 
			String memType)throws BaseServiceException {
		Class strArg[] = new Class[] { String.class };
		Class calArg[] = new Class[] { Calendar.class };

		logger.debug("-------entered setContactFields---------");
		for(POSMapping posMapping : contactPOSMap){

			String custFieldAttribute = posMapping.getCustomFieldName();
			String fieldValue=getFieldValue(returnRequest,posMapping,memType);
			if(fieldValue == null || fieldValue.trim().length() <= 0){
				logger.info("custom field value is empty ...for field : "+custFieldAttribute);
				continue;
			}
			fieldValue = fieldValue.trim();
			String dateTypeStr = null;
			dateTypeStr = posMapping.getDataType();
			if(dateTypeStr == null || dateTypeStr.trim().length() <=0){
				continue;
			}
			if(dateTypeStr.startsWith("Date")){
				String dateValue = getDateFormattedData(posMapping, fieldValue);
				if(dateValue == null) continue;
			}

			Object[] params = null;
			Method method = null;
			try {

				if (custFieldAttribute.equals(POSFieldsEnum.emailId.getOcAttr()) && fieldValue.length() > 0 &&
						Utility.validateEmail(fieldValue)) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.emailId.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.firstName.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.firstName.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.lastName.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.lastName.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.addressOne.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.addressOne.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.addressTwo.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.addressTwo.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.city.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.city.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.state.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.state.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.country.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.country.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.zip.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.zip.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.mobilePhone.getOcAttr()) && fieldValue.length() > 0) {
					Users user = inputContact.getUsers();
					String phoneParse = Utility.phoneParse(fieldValue,user!=null ? user.getUserOrganization() : null );
					if(phoneParse != null){
						//logger.info("after phone parse: "+phoneParse);
						method = Contacts.class.getMethod("set" + POSFieldsEnum.mobilePhone.getPojoField(), strArg);
						params = new Object[] { phoneParse };
					}
				}

				else if (custFieldAttribute.equals(POSFieldsEnum.externalId.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.externalId.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}

				else if (custFieldAttribute.equals(POSFieldsEnum.gender.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.gender.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}

				else if (custFieldAttribute.equals(POSFieldsEnum.birthDay.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.birthDay.getPojoField(), calArg);
					try {
						String dateformat = dateTypeStr.substring(dateTypeStr.indexOf("(")+1, dateTypeStr.indexOf(")"));
						DateFormat formatter ; 
						Date date ; 
						formatter = new SimpleDateFormat(dateformat);
						date = (Date)formatter.parse(fieldValue); 
						Calendar dobCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateformat));
						dobCal.setTime(date);
						params = new Object[] { dobCal };
						//contact.setBirthDay(dobCal);
					} catch (Exception e) {
						logger.info("BirthDay date format not matched with data",e);
					}

				}

				else if (custFieldAttribute.equals(POSFieldsEnum.anniversary.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.anniversary.getPojoField(), calArg);
					try {
						String dateformat = dateTypeStr.substring(dateTypeStr.indexOf("(")+1, dateTypeStr.indexOf(")"));
						DateFormat formatter ; 
						Date date ; 
						formatter = new SimpleDateFormat(dateformat);
						date = (Date)formatter.parse(fieldValue); 
						Calendar dobCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateformat));
						dobCal.setTime(date);
						params = new Object[] { dobCal };
						//contact.setBirthDay(dobCal);
					} catch (Exception e) {
						logger.info("Anniversary date format not matched with data",e);
					}
				}
				if (method != null) {
					try {
						method.invoke(inputContact, params);
						//logger.info("method name:  "+method.getName()+" field value: "+fieldValue);
					} catch (Exception e) {
						logger.error("Exception ::" , e);
					} 
				}
			} catch (Exception e) {
				//logger.info("securityexception");
				logger.error("Exception ::" , e);
			} 
			logger.debug("-------exit  setContactFields---------");
		}
		//logger.info("set contact data input contact: mobile: "+inputContact.getMobilePhone());
		return inputContact;
	}//setContactFields
	
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
		
		if(amount == null || amount == null || amount.getType().trim().isEmpty()){
				//|| amount.getEnteredValue() == null || amount.getEnteredValue().trim().isEmpty()) {
			status = new Status("111534", PropertyUtil.getErrorMessage(111534, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		
		if(!amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_STORE_CREDIT) && 
				!amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL) && 
				!amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) &&
				!amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VOID)) {
			status = new Status("111580", PropertyUtil.getErrorMessage(111580, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		 
		if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_STORE_CREDIT) && 
				!amount.getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY)) {
			status = new Status("111526", PropertyUtil.getErrorMessage(111526, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		/*if(amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL) ){
				//(amount.getEnteredValue() == null || amount.getEnteredValue().trim().isEmpty())){
			
			status = new Status("111534", PropertyUtil.getErrorMessage(111534, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/
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
			membershipResponse.setCardNumber(Constants.STRING_NILL);
			membershipResponse.setCardPin(Constants.STRING_NILL);
			membershipResponse.setExpiry(Constants.STRING_NILL);
			membershipResponse.setPhoneNumber(Constants.STRING_NILL);
			membershipResponse.setTierLevel(Constants.STRING_NILL);
			membershipResponse.setTierName(Constants.STRING_NILL);
		}
		if(balances == null){
			balances = new ArrayList<Balance>();
		}
		if(holdBalance == null){
			holdBalance = new HoldBalance();
			holdBalance.setActivationPeriod(Constants.STRING_NILL);
			holdBalance.setCurrency(Constants.STRING_NILL);
			holdBalance.setPoints(Constants.STRING_NILL);
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

	
	private double getAutoConvertionReversalVal(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier) {
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

		double unitAmtFactor = (double)tier.getConvertFromPoints()/tier.getConvertToAmount();
		//int multiple = (int)unitAmtFactor;
		double multiple = (double)unitAmtFactor;
		double totConvertedPts = contactsLoyalty.getGiftcardBalance() * multiple;

		return totConvertedPts;//changes long conversion was removed APP-1072
		//double subPoints = multiple * tier.getConvertFromPoints();
		
		
	}
	
	private Double getConversionVal(Double remainingPoints, LoyaltyProgramTier tier){
		try{
			if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) && 
					tier.getConvertFromPoints() != null && tier.getConvertFromPoints()>0){
				double multipledouble = tier.getConvertToAmount()/tier.getConvertFromPoints();
				double multiple = (double)multipledouble;
				double convertedAmount = Double.parseDouble(Utility.truncateUptoTwoDecimal(remainingPoints.intValue() * multiple));
				return convertedAmount;
			}else{
				return null;
			}

		}catch(Exception e){
				logger.error("Exception while applying auto conversion rules...", e);
				return null;
		}

	}

private ContactsLoyalty applyConversionRules(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier){//APP-2226
		//This calculation only works if points balance is less  than 0. 
		logger.info("-- Entered applyConversionRules --");
		String[] differenceArr = null;

		try{
			
			if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)){
				
				if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
						&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0 
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints()){
				
					differenceArr = new String[3];
					
					/*double multipledouble = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
					double multiple = multipledouble;
					double convertedAmount = tier.getConvertToAmount() * multiple;
					double subPoints = multiple * tier.getConvertFromPoints();*/
					
					//APP-3262
					double multipledouble = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
					int multiple = (int)multipledouble;
					double convertedAmount = 0.0;
					String result = Utility.truncateUptoTwoDecimal(tier.getConvertToAmount() * multiple);
					
					if(result != null)
						convertedAmount  = Double.parseDouble(result);
					
					double subPoints = multiple * tier.getConvertFromPoints();
					
					differenceArr[0] = Constants.STRING_NILL+convertedAmount;
					differenceArr[1] = Constants.STRING_NILL+subPoints;
					differenceArr[2] = tier.getConvertFromPoints().intValue()+" Points -> "+tier.getConvertToAmount().intValue();
					
					logger.info("multiple factor = "+multiple);
					logger.info("Conversion amount ="+convertedAmount);
					logger.info("subtract points = "+subPoints);
					
					
					//update giftcard balance
					if(contactsLoyalty.getGiftcardBalance() == null ) {
						contactsLoyalty.setGiftcardBalance(convertedAmount);
					}
					else{
						contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					if(contactsLoyalty.getTotalGiftcardAmount() == null){
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					}
					else{
						contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					
					//deduct loyalty points
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - subPoints);
					
					logger.info("contactsLoyalty.getGiftcardBalance() = "+contactsLoyalty.getGiftcardBalance());
					
					return contactsLoyalty;
					
				}
			}
		
		}catch(Exception e){
			logger.error("Exception while applying auto conversion rules...", e);
			return null;
		}
		logger.info("-- Exit applyConversionRules --");
		//return null;
		return contactsLoyalty;
	}
	private String genKey(String docSID, String recieptNumber,String storeNumber, String subsNumber){
		
		String key= (docSID==null?Constants.STRING_NILL:"OR-"+docSID)+";=;"+(recieptNumber==null?Constants.STRING_NILL:recieptNumber)+";=;"+(storeNumber==null?Constants.STRING_NILL:storeNumber)+";=;"+(subsNumber==null?Constants.STRING_NILL:subsNumber);

	return key;
	}
	
	private LoyaltyReturnTransactionResponse processReturnWithOutOR(LoyaltyReturnTransactionRequest returnTransactionRequest, 
			ResponseHeader responseHeader, Users user, List<SkuDetails> items , boolean isfullRcpt,double actualReturnAmnt) {
		logger.debug("===process withoutOR===");
		try {
			Status status = null;
			LoyaltyReturnTransactionResponse returnTransactionResponse = null;
			
			RequestHeader header = returnTransactionRequest.getHeader();
			Amount amount = returnTransactionRequest.getAmount();
			SpecialRewardsDao specialRewardsDao = (SpecialRewardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPECIAL_REWARDS_DAO);
			//APP-2084
			if((returnTransactionRequest.getMembership() == null || 
					returnTransactionRequest.getMembership().getCardNumber() == null 
					|| returnTransactionRequest.getMembership().getCardNumber().trim().isEmpty()) && 
					(returnTransactionRequest.getCustomer() == null || returnTransactionRequest.getCustomer().getPhone() == null ||
					returnTransactionRequest.getCustomer().getPhone().isEmpty())){//#TESTING
				
				//when original receipt & membership details not found need a error code
				if(isfullRcpt){
					status = new Status("111564", PropertyUtil.getErrorMessage(111564, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}else{
					
					status = new Status("111604", PropertyUtil.getErrorMessage(111604, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
			}
			String inquiry = amount.getType()!=null && amount.getType().equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY) ? OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY : Constants.STRING_NILL;

			LoyaltyProgram loyaltyProgram = null;
			ContactsLoyalty contactsLoyalty = null;
			LoyaltyCards loyaltyCard = null;
			
			if( (returnTransactionRequest.getMembership().getCardNumber() != null 
						&& returnTransactionRequest.getMembership().getCardNumber().trim().length() > 0) ) {
				String cardNumber = Constants.STRING_NILL;
				String cardLong = null;

				cardLong = OptCultureUtils.validateOCLtyCardNumber(returnTransactionRequest.getMembership().getCardNumber().trim());
				if(cardLong == null){
					String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getMembership().getCardNumber().trim()+".";
					status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
				cardNumber = Constants.STRING_NILL+cardLong;
				returnTransactionRequest.getMembership().setCardNumber(cardNumber);
				
				loyaltyCard = findLoyaltyCardByUserId(cardNumber, user.getUserId());
				
				if(loyaltyCard == null){
					status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
				

				loyaltyProgram = findLoyaltyProgramByProgramId(loyaltyCard.getProgramId(), user.getUserId());
				if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus())){
					status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
				contactsLoyalty = findContactLoyalty(cardNumber, loyaltyProgram.getProgramId(), user.getUserId());
				
				if(contactsLoyalty == null){
					status = new Status("1000", PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
				
			
			}//if card
			else if(returnTransactionRequest.getMembership().getPhoneNumber() != null 
					&& returnTransactionRequest.getMembership().getPhoneNumber().trim().length() > 0){//look up loyalty based on mobile number
				
				String validStatus = LoyaltyProgramHelper.validateMembershipMobile(returnTransactionRequest.getMembership().getPhoneNumber().trim());
				if(OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID.equals(validStatus)){
					status = new Status("111554", PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getMembership().getPhoneNumber().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
				
				contactsLoyalty = findContactLoyaltyByMobile(returnTransactionRequest.getMembership().getPhoneNumber().trim(), user);
				
				if(contactsLoyalty == null){
					status = new Status("111519", PropertyUtil.getErrorMessage(111519, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getMembership().getPhoneNumber().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
				
				
			}else if(returnTransactionRequest.getCustomer() != null && returnTransactionRequest.getCustomer().getPhone() != null 
					&& !returnTransactionRequest.getCustomer().getPhone().trim().isEmpty()){//cutomer's phone
				

				
				List<ContactsLoyalty> enrollList = findEnrollListByMobile(returnTransactionRequest.getCustomer().getPhone().trim(), user.getUserId());
				
				if(enrollList == null){
					status = new Status("111524", PropertyUtil.getErrorMessage(111524, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
				
				List<Contacts> dbContactList = null;
				Contacts dbContact = null;
				
				if(enrollList.size() > 1){
					logger.info("Found more than 1 enrollments");
					Contacts jsonContact = prepareContactFromJsonData(returnTransactionRequest.getCustomer(), user.getUserId());
					jsonContact.setUsers(user);
					dbContactList = findOCContact(jsonContact, user.getUserId(),user);
					
					if(dbContactList == null || dbContactList.size() == 0){
						logger.info(" request contact not found in OC");
						
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);
						
						status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, matchedCustomers, status);
						return returnTransactionResponse;
					}
					else if(dbContactList.size() == 1){
						logger.info("else case..enrollList ..."+enrollList.size());
						dbContact = dbContactList.get(0);
						logger.info("dbcontact cid == "+dbContact.getContactId());
						Iterator<ContactsLoyalty> iterList = enrollList.iterator();
						ContactsLoyalty loyalty = null;
						int count = 0;
						while(iterList.hasNext()){
							loyalty = iterList.next();
							logger.info(" enrollist cid.."+loyalty.getContact().getContactId());
							if(loyalty.getContact() != null && loyalty.getContact().getContactId() != null 
									&& loyalty.getContact().getContactId().equals(dbContact.getContactId())){
								if(contactsLoyalty == null)	contactsLoyalty = loyalty;
								count++;
								logger.info("loyalty found in more than one enrollment case...");
							}
						}
						if(count > 1){
							contactsLoyalty = null;
						}
						if(contactsLoyalty == null){
							
							List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);
							
							status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, matchedCustomers, status);
							return returnTransactionResponse;
						}
					}
					else{
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);
						status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, matchedCustomers, status);
						return returnTransactionResponse;
					}
					
					
				}
				else{
					logger.info("loyalty found in else case....");
					contactsLoyalty = enrollList.get(0);
				}
			
				
			}// else if customer's phone
			
			if(contactsLoyalty != null ){
				
				if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
					
					
					String cardNumber = contactsLoyalty.getCardNumber()+Constants.STRING_NILL;
					
					if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equals(contactsLoyalty.getRewardFlag()) ){
						
						status = new Status("111581", PropertyUtil.getErrorMessage(111581, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
						
						
					}
					
					
					if(loyaltyCard == null) loyaltyCard = findLoyaltyCardByUserId(cardNumber, user.getUserId());
					
					if(loyaltyCard == null){
						status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}
					
					if(loyaltyProgram == null) {
					
						loyaltyProgram = findLoyaltyProgramByProgramId(loyaltyCard.getProgramId(), user.getUserId());
						if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus())){
							status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
							return returnTransactionResponse;
						}
					}
					
					LoyaltyCardSet loyaltyCardSet = null;
					loyaltyCardSet = findLoyaltyCardSetByCardsetId(loyaltyCard.getCardSetId(), user.getUserId());
					if(loyaltyCardSet == null || !OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE.equals(loyaltyCardSet.getStatus())){
						status = new Status("111574", PropertyUtil.getErrorMessage(111574, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}
					if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
							contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED) || 
							contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
						

						LoyaltyProgramTier tier = null;
						
						
						List<Balance> balances = null;
						
						List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
						
						if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
							contactLoyaltyList.add(contactsLoyalty);
							if(contactsLoyalty.getProgramTierId() != null) tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
							
							balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
							String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
							status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						}
						else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
							contactLoyaltyList.add(contactsLoyalty);
							if(contactsLoyalty.getProgramTierId() != null) tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
							
							balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
							String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
							status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						}else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
							ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactsLoyalty);
							String maskedNum = Constants.STRING_NILL;
							if(destLoyalty != null) {
								contactLoyaltyList.add(destLoyalty);
								contactsLoyalty = destLoyalty;
								tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
								balances = prepareBalancesObject(destLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
								maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
								
							}
							String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+maskedNum+".";
							status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						}
						
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
						MembershipResponse response = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
						
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances,
												null, null, matchedCustomers, status);
						return returnTransactionResponse;
						
						
						
					}//if contact loyalty is active or not
					
					
				}//if card based loyalty
				else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(contactsLoyalty.getMembershipType())){
					
					 loyaltyProgram = findActiveMobileProgram(contactsLoyalty.getProgramId());
					
					if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equals(loyaltyProgram.getStatus())){
						status = new Status("111522", PropertyUtil.getErrorMessage(111522, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null,
								null, null, null, status);
						return returnTransactionResponse;
					}
					
				}//else if mobile based
				
				LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
				List<SkuDetails> finalItems = items;

				if (loyaltyExclusion == null || (loyaltyExclusion.getClassStr() == null && loyaltyExclusion.getDcsStr() == null
						&& loyaltyExclusion.getDeptCodeStr() == null && loyaltyExclusion.getItemCatStr() == null
						&& loyaltyExclusion.getSkuNumStr() == null && loyaltyExclusion.getSubClassStr() == null
						&& loyaltyExclusion.getVendorStr() == null)) {
				} else {
					finalItems = getFinalItems(returnTransactionRequest.getItems(), loyaltyExclusion);
				}
				Object retResponse = getItemBasedSpecilaRewards(loyaltyProgram, finalItems, user, returnTransactionRequest, 
						contactsLoyalty, responseHeader);
				
				if(retResponse != null && retResponse instanceof LoyaltyReturnTransactionResponse) return (LoyaltyReturnTransactionResponse)retResponse;
				
				Set<String>  excludeItems = retResponse != null && retResponse instanceof Set ? (Set<String>)retResponse : null;
				
				Double excludedPrice = 0.0; 
				if(excludeItems != null && !excludeItems.isEmpty()){
					
					for (SkuDetails item : items) {
						if(item.getItemSID() != null && !excludeItems.isEmpty() && excludeItems.contains(item.getItemSID())){
							excludedPrice +=  (Double.valueOf(item.getQuantity())
									* Double.valueOf(item.getBilledUnitPrice()));
							continue;
						}
					}
				}

			    Double returnedAmountdbl = calculateReturnAmount(items );
			    logger.info("Return value "+returnedAmountdbl);
				Double itemExcludedAmount = 0.0;
				if(loyaltyExclusion != null && (loyaltyExclusion.getClassStr() != null || loyaltyExclusion.getDcsStr() != null ||
						loyaltyExclusion.getDeptCodeStr() != null || loyaltyExclusion.getItemCatStr() != null ||
						loyaltyExclusion.getSkuNumStr() != null || loyaltyExclusion.getSubClassStr() != null ||
						loyaltyExclusion.getVendorStr() != null)){
					itemExcludedAmount = calculateItemDiscount(items , loyaltyExclusion);
				}
				double netReturnedAmount = returnedAmountdbl;
				//if(excludedPrice < returnedAmountdbl){
					
					Double netReturnedAmountdbl = returnedAmountdbl - itemExcludedAmount- excludedPrice;
					logger.debug("netReturnedAmountdbl "+netReturnedAmountdbl);
					netReturnedAmount = netReturnedAmountdbl;   
					//calculate the rollback based on current earn rules 
					if(netReturnedAmount <= 0 && excludedPrice<=0) {//APP-2114
						status = new Status("111604", PropertyUtil.getErrorMessage(111604, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}
					
				/*}if(excludedPrice == returnedAmountdbl) {
					
					
				}*/
				LoyaltyProgramTier tier = null;//getLoyaltyTier(contactsLoyalty.getProgramTierId());
				
				if(contactsLoyalty.getProgramTierId() != null)
					tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				else{
					Long contactId = null;
					if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
						contactId = contactsLoyalty.getContact().getContactId();
						List<LoyaltyProgramTier> tierList = validateTierList(contactsLoyalty.getProgramId(), contactsLoyalty.getUserId());
						if(tierList == null || tierList.size() == 0 || !OCConstants.LOYALTY_PROGRAM_TIER1.equals(tierList.get(0).getTierType())){
							status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null,
									null, null, null, status);
							return returnTransactionResponse;
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
						
						tier = findTier(contactId, contactsLoyalty.getUserId(),contactsLoyalty, tierList,eligibleMap);
						if(tier != null){
							contactsLoyalty.setProgramTierId(tier.getTierId());
							saveContactsLoyalty(contactsLoyalty);
						}
						else{
							status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null,
									null, null, null, status);
							return returnTransactionResponse;
						}
					}
				}//determine tier
				//long earnedValue = 0;
				double earnedValue = 0;
				String earntype = tier.getEarnType();
				String pointsDifference = Constants.STRING_NILL;
				String amountDifference = Constants.STRING_NILL;
				String roundingType = tier.getRoundingType();
				if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)){

						//Double multipleFactordbl = LoyaltyProgramHelper.getRoundedPurchaseAmount(roundingType, netReturnedAmount) / tier.getEarnOnSpentAmount();
					Double multipleFactordbl = netReturnedAmount / tier.getEarnOnSpentAmount();
						//long multipleFactor = multipleFactordbl.intValue();
					if(roundingType!=null && roundingType.equals("Up"))
						earnedValue = tier.getEarnValue() * multipleFactordbl;//APP-4027
					else
						earnedValue = tier.getEarnValue() * (multipleFactordbl+0.001);//APP-4027
					//Double multipleFactordbl = netReturnedAmount/tier.getEarnOnSpentAmount();
					//long multipleFactor = multipleFactordbl.intValue();//Changes APP-2133
					//long multipleFactor = multipleFactordbl.intValue();
					//earnedValue = (long)Math.floor(tier.getEarnValue() * multipleFactor);
					//earnedValue = tier.getEarnValue() * multipleFactordbl;
				}
				else if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)){
					//earnedValue = (tier.getEarnValue() * (LoyaltyProgramHelper.getRoundedPurchaseAmount(tier.getRoundingType(), netReturnedAmount))) / 100;
					earnedValue = (tier.getEarnValue() * netReturnedAmount) / 100;
					//earnedValue = (long)Math.floor((tier.getEarnValue() * netReturnedAmount)/100);
					//earnedValue = (tier.getEarnValue() * netReturnedAmount)/100;
				}
				
				if(OCConstants.LOYALTY_TYPE_POINTS.equals(earntype)){
					
					//logger.info("Rounding type of the tier is ==="+tier.getRoundingType());
					
					earnedValue = LoyaltyProgramHelper.getRoundedPoints("Near", earnedValue);//APP-4171 changes to rounding near instead of tier rounding type


					/*if(roundingType!=null && roundingType.toString().equalsIgnoreCase("Up")){
						
						earnedValue = (long) Math.ceil(earnedValue);

					}else {
						
						earnedValue = (long) Math.floor(earnedValue);
					}*/
					//earnedValue = Math.floor(earnedValue); // Changed it again to floor in accordance with APP-962  
					pointsDifference = "-"+(long)earnedValue;
				}
				else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(earntype)){
					String res = Utility.truncateUptoTwoDecimal(earnedValue);
					if(res != null)
						earnedValue = Double.parseDouble(res);
					amountDifference = "-"+earnedValue;
				}
				
				Double maxcap= tier.getIssuanceChkEnable()!=null && tier.getIssuanceChkEnable() && tier.getMaxcap()!=null && tier.getMaxcap()>0 ? tier.getMaxcap() : earnedValue ;
				earnedValue =  earnedValue>maxcap ? maxcap : earnedValue;
				
				Map<String, Object> balMap = new HashMap<String, Object>();
				balMap = updateContactLoyaltyBalances(earnedValue, earntype, contactsLoyalty, tier,
						netReturnedAmount,returnTransactionRequest.getAmount().getType(),actualReturnAmnt);//Changes LPV
				status = (Status) balMap.get("status");
				MembershipResponse response = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
				List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
				contactLoyaltyList.add(contactsLoyalty);
				List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
				if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
					
					List<Balance> balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
					String expiryPeriod = Constants.STRING_NILL;
					boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(header.getStoreNumber(),tier);

					if(tier != null && tier.getActivationFlag() == OCConstants.FLAG_YES	&& isStoreActiveForActivateAfter && ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
							(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){
						expiryPeriod = tier.getPtsActiveDateValue()+" "+tier.getPtsActiveDateType();
					}
					HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
					BalancesAdditionalInfo additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
					
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances,
							holdBalance, additionalInfo, null, status);
					return returnTransactionResponse;
				}
				/*if(loyaltyProgram.getPartialReversalFlag() == OCConstants.FLAG_YES){
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
				}*/
				String description = returnedAmountdbl+Constants.STRING_NILL;
				amountDifference = balMap.get("AmountDifference") != null && !balMap.get("AmountDifference").toString().isEmpty() ? (String)balMap.get("AmountDifference") : Constants.STRING_NILL;
				pointsDifference = balMap.get("PointsDifference") != null && !balMap.get("PointsDifference").toString().isEmpty() ? (String)balMap.get("PointsDifference") : Constants.STRING_NILL;
				/*createReturnTransaction(header, amount, 
						originalRecpt, contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
						pointsDifference, amountDifference, null , null, null, 
						responseHeader.getTransactionId(), Double.parseDouble(amount.getEnteredValue()),OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL, 
						amount.getEnteredValue(), null);*/
				createReturnTransaction(header, amount.getType(),	null, contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
						pointsDifference.isEmpty()?pointsDifference:"-"+pointsDifference, amountDifference.isEmpty()?amountDifference:"-"+amountDifference, null , null, null, 
						responseHeader.getTransactionId(), netReturnedAmount,OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL, 
						description, null,null, null, Constants.STRING_NILL,Constants.STRING_NILL);
				List<Balance> balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL+pointsDifference, Constants.STRING_NILL+amountDifference, Constants.STRING_NILL);
				String expiryPeriod = Constants.STRING_NILL;
				boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(header.getStoreNumber(),tier);

				if(tier != null && tier.getActivationFlag() == OCConstants.FLAG_YES	&& isStoreActiveForActivateAfter && ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
						(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){

					expiryPeriod = tier.getPtsActiveDateValue()+" "+tier.getPtsActiveDateType();
				}
				
				
				HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
				BalancesAdditionalInfo additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
				//status = new Status("0", "Return was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);

				if(tier.getPartialReversalFlag() == OCConstants.FLAG_YES){
					logger.info("remainingRewardCurrency====>"+
							(balMap.get("remainingRewardCurrency")).toString());
					if((String)(balMap.get("remainingRewardCurrency")) !=  null 
								&& !((String)(balMap.get("remainingRewardCurrency")).toString()).equalsIgnoreCase("0.0") && tier.getConversionType() != null
								&& tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) ) {
							status = new Status("0", "Return "+inquiry+" has been processed. Due to insufficient balance, roll back failed for:" + 
									(String)(balMap.get("remainingRewardPoints")) + " points worth $ " + Utility.truncateUptoTwoDecimal((String)(balMap.get("remainingRewardCurrency"))) + ".", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						}
					else if((String)(balMap.get("remainingRewardCurrency")) !=  null && !((String)(balMap.get("remainingRewardCurrency")).toString()).isEmpty())
						status = new Status("0", "Return "+inquiry+" was successful and Remainder Debit : $ " + Utility.truncateUptoTwoDecimal((String)(balMap.get("remainingRewardCurrency"))) + ".", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					else
						status = new Status("0", "Return "+inquiry+" was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				}else{
					status = new Status("0", "Return "+inquiry+" was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				}
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances,
						holdBalance, additionalInfo, matchedCustomers, status);
				return returnTransactionResponse;
								
			}else {
				
				//List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);
				status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
				
			}
		} catch (NumberFormatException e) {
			logger.error("Exception ::", e);
			logger.error("Exception ::", e);
		} catch (BaseServiceException e) {
			logger.error("Exception ::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		return null;
		
	}
	
	private void rollbackSpecialReward( LoyaltyTransactionChild sprewardChild,	ContactsLoyalty contactsLoyalty, String reqType ,double actualReturnAmnt) throws Exception{
		
		String valueCode = sprewardChild.getEarnType();
		LoyaltyProgramTier tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
		if(valueCode.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
			
			Double earnedAmount = sprewardChild.getEarnedAmount();
			updateContactLoyaltyBalances(earnedAmount, valueCode, contactsLoyalty, tier);
			
		}else if(valueCode.equals(OCConstants.LOYALTY_TYPE_POINTS)){
			
			Double earnedPoints = sprewardChild.getEarnedPoints();
			
			updateContactLoyaltyBalances(earnedPoints, valueCode, contactsLoyalty, tier, sprewardChild.getEnteredAmount(), reqType,actualReturnAmnt);
		}else{
			Double earnedReward = sprewardChild.getEarnedReward();
			LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
			LoyaltyBalanceDaoForDML loyaltyBalanceDaoForDML = (LoyaltyBalanceDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_BALANCE_DAO_FOR_DML);
			LoyaltyBalance loyaltyBalances = loyaltyBalanceDao.findBy(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(), valueCode);
			if(loyaltyBalances != null){
				
				Long reward = loyaltyBalances.getBalance();
				Double totalReward = loyaltyBalances.getTotalEarnedBalance();
				if(reward <=  earnedReward) reward = earnedReward.longValue();
				loyaltyBalances.setBalance(loyaltyBalances.getBalance()-reward);
				loyaltyBalances.setTotalEarnedBalance(loyaltyBalances.getTotalEarnedBalance()-reward);
				loyaltyBalanceDaoForDML.saveOrUpdate(loyaltyBalances);
			}
			
		}
		//String earntype = specialReward.getRewardType();
		
		
		
	} 
	private List<LoyaltyTransactionChild> getRewardReturnedTrx(List<LoyaltyTransactionChild> returnedTrx){
		List<LoyaltyTransactionChild> retReturnedTrx = new ArrayList<LoyaltyTransactionChild>();
		
		for (LoyaltyTransactionChild loyaltyTransactionChild : returnedTrx) {
			if(loyaltyTransactionChild.getSpecialRewardId() != null )
				retReturnedTrx.add(loyaltyTransactionChild);
		}
		return retReturnedTrx;
	}
	/*private boolean checkAMatchWithSP(SpecialReward SPReward){
		
		
		
		
	}*/
	
	public static void main(String[] args) {
		
		Double retunredamount = Double.parseDouble(Utility.truncateUptoTwoDecimal(166.35000000000002));
		String itemRewardsInfo = "5188;=;12.0:1:Platinum;=;7049139156276416508|6540;=;12.0:1:C0025;=;7049139156276416508";
		
		String[] itemsAndRewards = itemRewardsInfo.indexOf(Constants.DELIMITER_PIPE) != -1 ? 
				itemRewardsInfo.split("\\"+Constants.DELIMITER_PIPE) : null;
				System.out.println(itemsAndRewards);
				if(itemsAndRewards == null){
					System.out.println(itemRewardsInfo);
					itemsAndRewards = new String[1];
					itemsAndRewards[0] = itemRewardsInfo;
				}
		Double rollBackQty =0.0;
		for (String itemRewards : itemsAndRewards) {
			
			System.out.println(itemRewards);
			String[] itemRewardsArr = itemRewards.split(Constants.ADDR_COL_DELIMETER);//5182;=;12:1:camo;=;7038103734733705212
			String spId = itemRewardsArr[0];
			String[] spDetailsArr = itemRewardsArr[1].split(Constants.DELIMETER_COLON+"");
		}
		
		System.out.println("5182;=;12:1:camo;=;7038103734733705212".split(Constants.DELIMITER_PIPE)[0].split(Constants.ADDR_COL_DELIMETER)[1]);
		/*Double a= 6.85;
		Double b =9.34;
		Double c= */
	}
}
