package org.mq.optculture.business.loyalty;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
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
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

@Deprecated
public class LoyaltyReturnTransactionOCServiceImpl implements LoyaltyReturnTransactionOCService{

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

	/**
	 * Handles the complete process of Loyalty return transaction.
	 * 
	 * @param returnTransactionRequest
	 * @return returnTransactionResponse
	 * @throws BaseServiceException
	 */
	@Override
	public LoyaltyReturnTransactionResponse processReturnTransactionRequest(LoyaltyReturnTransactionRequest returnTransactionRequest,
			String transactionId, String transactionDate, String requestJson, String mode,String loyaltyExtraction) throws BaseServiceException {

		logger.info("processReturnTransactionRequest method called...");

		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;
		Users user = null;

		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(returnTransactionRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(returnTransactionRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		try{

			status = validateReturnTransactionJsonData(returnTransactionRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}

			status = validateEnteredValue(returnTransactionRequest.getAmount());
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

			if(returnTransactionRequest.getAmount().getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)) {

				if(returnTransactionRequest.getOriginalReceipt() == null) {
					status = new Status("111565", PropertyUtil.getErrorMessage(111565, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}

				if(returnTransactionRequest.getOriginalReceipt().getDocSID() == null || returnTransactionRequest.getOriginalReceipt().getDocSID().trim().isEmpty()){
					status = new Status("111564", PropertyUtil.getErrorMessage(111564, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					return returnTransactionResponse;
				}
				
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				List<LoyaltyTransactionChild> issTransList = loyaltyTransactionChildDao.findByDocSID(returnTransactionRequest.getOriginalReceipt().getDocSID(), 
																							      user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,null,null,null);
				if(issTransList != null) {
					List<LoyaltyTransactionChild> returnList = loyaltyTransactionChildDao.getTotReversalAmt(user.getUserId(), 
							returnTransactionRequest.getOriginalReceipt().getDocSID(), OCConstants.LOYALTY_TRANS_TYPE_RETURN,null);
					double totReturnAmt = 0.0; 
					if(returnList != null && returnList.size() >0) {
						for (LoyaltyTransactionChild returnObj : returnList) {
							totReturnAmt += returnObj.getDescription().contains(";=;") ? Double.parseDouble(returnObj.getDescription().split(";=;")[0]): Double.parseDouble(returnObj.getDescription());
						}
					}
					double totIssuedAmt = loyaltyTransactionChildDao.getTotPurchaseAmt(user.getUserId(), 
							returnTransactionRequest.getOriginalReceipt().getDocSID(), OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,null ,null,null);
					logger.info("Issued Amount::"+totIssuedAmt);
					double diffAmt = totIssuedAmt - totReturnAmt;
					logger.info("diffAmt ::"+diffAmt);
					if(diffAmt == 0) {
						status = new Status();
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}
					else if(Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue()) > diffAmt) {
						status = new Status("111567", PropertyUtil.getErrorMessage(111567, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}
					LoyaltyTransactionChild loyaltyTransactionChild = issTransList.get(0);
					returnTransactionResponse = performIssuanceBasedReversal(loyaltyTransactionChild, returnTransactionRequest,
							responseHeader, requestJson, user);
					return returnTransactionResponse;
				}
				
				List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(returnTransactionRequest.getOriginalReceipt().getDocSID(), 
																							user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,null,null,null);
				if(redempTransList != null) {
					/*List<LoyaltyTransactionChild> returnList = loyaltyTransactionChildDao.getTotReversalAmt(user.getUserId(), 
										  returnTransactionRequest.getOriginalReceipt().getDocSID(), OCConstants.LOYALTY_TRANS_TYPE_RETURN);
					double totReturnAmt = 0.0; 
					if(returnList != null && returnList.size() >0) {
						for (LoyaltyTransactionChild returnObj : returnList) {
							totReturnAmt += returnObj.getDescription().contains(";=;") ? Double.parseDouble(returnObj.getDescription().split(";=;")[0]): Double.parseDouble(returnObj.getDescription());
						}
					}
					double totRedeemedAmt = loyaltyTransactionChildDao.getTotRedeemedAmt(user.getUserId(), 
											returnTransactionRequest.getOriginalReceipt().getDocSID(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION);
					double diffAmt = totRedeemedAmt - totReturnAmt;
					if(diffAmt == 0) {
						status = new Status();
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}
					else  if(Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue()) > diffAmt) {
						status = new Status("111568", PropertyUtil.getErrorMessage(111568, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}*/
					returnTransactionResponse = performRedemptnBasedReversal(redempTransList, returnTransactionRequest,	responseHeader, requestJson, 
																			 user, null, null, null, null, null, true,
																			 PropertyUtil.getErrorMessage(111569, OCConstants.ERROR_LOYALTY_FLAG), 111569 );
					return returnTransactionResponse;
				}
				else {
					status = new Status("111566", PropertyUtil.getErrorMessage(111566, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				}
				
			}
			else if(returnTransactionRequest.getAmount().getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_STORE_CREDIT)){

				ContactsLoyalty contactsLoyalty = null;

				if(returnTransactionRequest.getMembership().getCardNumber() != null 
						&& returnTransactionRequest.getMembership().getCardNumber().trim().length() > 0){

					logger.info("Return transaction by card number >>>");

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

					logger.info("Card Number after parsing... "+cardNumber);

					return cardBasedReturnTransaction(returnTransactionRequest, cardNumber, responseHeader, user, requestJson);
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

					contactsLoyalty = findContactLoyaltyByMobile(returnTransactionRequest.getMembership().getPhoneNumber().trim(), user.getUserId());
					if(contactsLoyalty == null){
						status = new Status("111519", PropertyUtil.getErrorMessage(111519, OCConstants.ERROR_LOYALTY_FLAG)+" "+returnTransactionRequest.getMembership().getPhoneNumber().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
						return returnTransactionResponse;
					}

					return mobileBasedReturnTransaction(contactsLoyalty, responseHeader, returnTransactionRequest, user, requestJson);

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
						return mobileBasedReturnTransaction(contactsLoyalty, responseHeader, returnTransactionRequest, user, requestJson);
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
						return cardBasedReturnTransaction(returnTransactionRequest, ""+contactsLoyalty.getCardNumber(), responseHeader, user, requestJson);
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
		return returnTransactionResponse;
	}//processReturnTransactionRequest
	
	
	@Override
	public LoyaltyReturnTransactionResponse processOfflineReturnTransactionRequest(LoyaltyOfflineReturnTransactionRequest returnOfflineTransactionRequest, String transactionId, 
			String transactionDate, String requestJson, String mode) {
		
		return null;
		
	}


	private LoyaltyReturnTransactionResponse performRedemptnBasedReversal(List<LoyaltyTransactionChild> redempTransList,
															LoyaltyReturnTransactionRequest returnTransactionRequest,
															ResponseHeader responseHeader, String requestJson, Users user, 
															MembershipResponse response, List<Balance> balances, HoldBalance holdBalance,
															BalancesAdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers,
															boolean isIssuanceFailed, String msg, int errorCode) throws Exception{

		logger.info("performRedemptnBasedReversal method called...");
		LoyaltyReturnTransactionResponse returnTransactionResponse =  null;
		Status status = null;
		double balToCredit = Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue());
		if(!returnTransactionRequest.getCreditRedeemedAmount().equalsIgnoreCase("N")) {
			
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
					LoyaltyProgram loyaltyProgram = (LoyaltyProgram) contactLtyMap.get("program");

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
							responseMap = processRedemptionCredits(redemptionTrans, returnTransactionRequest,	responseHeader, requestJson, user, 
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
							responseMap = processRedemptionCredits(redemptionTrans, returnTransactionRequest,	responseHeader, requestJson, user, 
									response, balances, holdBalance, additionalInfo, matchedCustomers,  isIssuanceFailed,  
									msg, contactsLoyalty, loyaltyProgram, balToCredit, ltyPts, ltyAmt, ltyGft, credList);
							if(responseMap.get("returnResponse") != null) return (LoyaltyReturnTransactionResponse) responseMap.get("returnResponse");
							else if(responseMap.get("creditList") != null) credList = (List<Credit>) responseMap.get("creditList");
							balToCredit = (Double) responseMap.get("balToCredit");
						}
					}
					else {
						responseMap = processRedemptionCredits(redemptionTrans, returnTransactionRequest,	responseHeader, requestJson, user, 
								response, balances, holdBalance, additionalInfo, matchedCustomers,  isIssuanceFailed,  
								msg, contactsLoyalty, loyaltyProgram, balToCredit, pointsDiff, amountDiff, giftDiff, credList);
						if(responseMap.get("returnResponse") != null) return (LoyaltyReturnTransactionResponse) responseMap.get("returnResponse");
						else if(responseMap.get("creditList") != null) credList = (List<Credit>) responseMap.get("creditList");
						balToCredit = (Double) responseMap.get("balToCredit");
					}
					if(balToCredit == 0) break;
				}//for	
			}// if
			
			if(balToCredit ==  Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue()) && isIssuanceFailed) {
				if(isRedemptionExists) {
					status = new Status("111570", PropertyUtil.getErrorMessage(111570, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				else {
					status = new Status(errorCode+"", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
				return returnTransactionResponse;
			}
			else if(balToCredit ==  Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue()) && !isIssuanceFailed) {
				status = new Status("0", msg , OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
				return returnTransactionResponse;
			}
			else if(balToCredit !=  Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue()) && isIssuanceFailed) {
				msg += " Return was successful on redemption.";
				status = new Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				additionalInfo = new BalancesAdditionalInfo();
				additionalInfo.setDebit(new Debit());
				additionalInfo.setCredit(credList);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, additionalInfo, matchedCustomers, status);
				return returnTransactionResponse;
			}
			else if(balToCredit !=  Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue()) && !isIssuanceFailed){
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

	private Map<String, Object> processRedemptionCredits(LoyaltyTransactionChild redemptionTrans, 
			LoyaltyReturnTransactionRequest returnTransactionRequest, ResponseHeader responseHeader, 
			String requestJson, Users user, MembershipResponse response, List<Balance> balances,
			HoldBalance holdBalance, BalancesAdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers, 
			boolean isIssuanceFailed, String msg, ContactsLoyalty contactsLoyalty, LoyaltyProgram loyaltyProgram, 
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
					contactsLoyalty.setGiftcardBalance(ltyCurrBal + balToCredit);
					contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
					creditedReward = balToCredit+"";
					description = balToCredit+";=;"+balToCredit+";=;"+0+";=;"+0;
					createTransctnAndExpiry(returnTransactionRequest,contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, 
							balToCredit, earnedPts, OCConstants.LOYALTY_TYPE_AMOUNT, description,redemptionTrans.getTransChildId()); 
					returnTransactionResponse = prepareRedmptnSuccessResponse(returnTransactionRequest, responseHeader, requestJson, 
							user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
							isIssuanceFailed, msg, loyaltyProgram, contactsLoyalty, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM
							,creditedPoints, creditedReward, creditedGift, credList);
					responseMap.put("returnResponse", returnTransactionResponse);
					return responseMap;
				}
				else {
					if(amountDiff != 0) {
						balToCredit = balToCredit - amountDiff;
						contactsLoyalty.setGiftcardBalance(ltyCurrBal + amountDiff);
						contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
						creditedReward = amountDiff+"";
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
						contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
						creditedPoints = (long)autoCnvrtPts+"";
						description = amountDiff+balToCredit+";=;"+amountDiff+";=;"+autoCnvrtPts+";=;"+0;
						createTransctnAndExpiry(returnTransactionRequest,contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,
								amountDiff, autoCnvrtPts, OCConstants.LOYALTY_TYPE_POINTS+"/"+OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId()); 
						returnTransactionResponse = prepareRedmptnSuccessResponse(returnTransactionRequest, responseHeader, requestJson, 
								user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
								isIssuanceFailed, msg, loyaltyProgram, contactsLoyalty,
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM, creditedPoints, creditedReward, creditedGift, credList);
						responseMap.put("returnResponse", returnTransactionResponse);
						return responseMap;
					}
					else {
						if(pointsDiff != 0 || amountDiff != 0) {
							balToCredit = balToCredit - autoCnvrtPtsCredit;
							autoCnvrtPts = Math.round(autoCnvrtPtsCredit * ptsFactor);
							contactsLoyalty.setLoyaltyBalance(pointsBal + autoCnvrtPts);;
							contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
							creditedPoints = (long)autoCnvrtPts+"";
							description = amountDiff+autoCnvrtPtsCredit+";=;"+amountDiff+";=;"+autoCnvrtPts+";=;"+0;
							createTransctnAndExpiry(returnTransactionRequest,contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, 
									amountDiff, autoCnvrtPts, OCConstants.LOYALTY_TYPE_POINTS+"/"+OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId());
						}
						//prepare credit object
						Credit credit = prepareCreditObject(contactsLoyalty, creditedPoints, creditedReward, creditedGift);
						credList.add(credit);
						responseMap.put("creditList",credList);
						responseMap.put("balToCredit",balToCredit);
					}
				}
			}//Loyalty card
			else if(contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL)) {

				logger.info("--- In giftLoyalty card redemption ---");
				if(amountDiff != 0 && amountDiff >= balToCredit){
					contactsLoyalty.setGiftcardBalance(ltyCurrBal + balToCredit);
					contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
					creditedReward = balToCredit+"";
					description = balToCredit+";=;"+balToCredit+";=;"+0+";=;"+0;
					createTransctnAndExpiry(returnTransactionRequest,contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, balToCredit,
							earnedPts, OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId()); 
					returnTransactionResponse = prepareRedmptnSuccessResponse(returnTransactionRequest, responseHeader, requestJson, 
							user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
							isIssuanceFailed, msg, loyaltyProgram, contactsLoyalty, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM,
							creditedPoints, creditedReward, creditedGift, credList);
					responseMap.put("returnResponse", returnTransactionResponse);
					return responseMap;
				}
				else {
					if(amountDiff != 0) {
						balToCredit = balToCredit - amountDiff;
						creditedReward = amountDiff+"";
						contactsLoyalty.setGiftcardBalance(ltyCurrBal + amountDiff);
						contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
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
						contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
						creditedPoints = (long)autoCnvrtPts+"";
						description = amountDiff+balToCredit+";=;"+amountDiff+";=;"+autoCnvrtPts+";=;"+0;
						createTransctnAndExpiry(returnTransactionRequest,contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,
								amountDiff, autoCnvrtPts, OCConstants.LOYALTY_TYPE_POINTS+"/"+OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId()); 
						returnTransactionResponse = prepareRedmptnSuccessResponse(returnTransactionRequest, responseHeader, requestJson, 
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
							contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
							creditedPoints = (long)autoCnvrtPts+"";
						}
						if(giftDiff != 0 && giftDiff >= balToCredit){
							contactsLoyalty.setGiftBalance(giftBal + balToCredit);
							contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
							creditedGift = balToCredit+"";
							description = amountDiff+autoCnvrtPtsCredit+balToCredit+";=;"+amountDiff+";=;"+autoCnvrtPts+";=;"+balToCredit;
							createTransctnAndExpiry(returnTransactionRequest,contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, amountDiff+balToCredit,
									autoCnvrtPts, OCConstants.LOYALTY_TYPE_POINTS+"/"+OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId()); 
							returnTransactionResponse = prepareRedmptnSuccessResponse(returnTransactionRequest, responseHeader, requestJson, 
									user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
									isIssuanceFailed, msg, loyaltyProgram, contactsLoyalty, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM,
									creditedPoints, creditedReward, creditedGift, credList);
							responseMap.put("returnResponse", returnTransactionResponse);
							return responseMap;
						}
						else {
							if(giftDiff != 0 || pointsDiff != 0 || amountDiff != 0) {
								balToCredit = balToCredit - giftDiff;
								contactsLoyalty.setGiftBalance(giftBal + giftDiff);
								contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
								creditedGift = giftDiff+"";
								description = amountDiff+autoCnvrtPtsCredit+giftDiff+";=;"+amountDiff+";=;"+autoCnvrtPts+";=;"+giftDiff;
								createTransctnAndExpiry(returnTransactionRequest,contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L, 
										amountDiff+giftDiff, autoCnvrtPts, OCConstants.LOYALTY_TYPE_POINTS+"/"+OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId());
							}
						}
						//prepare credit object
						Credit credit = prepareCreditObject(contactsLoyalty,  creditedPoints, creditedReward, creditedGift);
						credList.add(credit);
						responseMap.put("creditList",credList);
						responseMap.put("balToCredit",balToCredit);
					}
				}
			}//GiftLoyalty-card
			else if(contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
				logger.info("--- In gift card redemption ---");
				if(giftDiff >= balToCredit){
					contactsLoyalty.setGiftBalance(giftBal + balToCredit);
					contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
					creditedGift = balToCredit+"";
					description = balToCredit+";=;"+0+";=;"+0+";=;"+balToCredit;
					createTransctnAndExpiry(returnTransactionRequest,contactsLoyalty,user,responseHeader,
										OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, balToCredit, earnedPts, OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId()); 
					returnTransactionResponse = prepareRedmptnSuccessResponse(returnTransactionRequest, responseHeader, requestJson, 
							user, response, balances, holdBalance, additionalInfo, matchedCustomers, 
							isIssuanceFailed, msg, loyaltyProgram, contactsLoyalty, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM
							,creditedPoints, creditedReward, creditedGift, credList);
					responseMap.put("returnResponse", returnTransactionResponse);
					return responseMap;
				}
				else {
					balToCredit = balToCredit - giftDiff;
					contactsLoyalty.setGiftBalance(giftBal + giftDiff);
					contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyalty);
					description = giftDiff+";=;"+0+";=;"+0+";=;"+giftDiff;
					createTransctnAndExpiry(returnTransactionRequest,contactsLoyalty,user,responseHeader,OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G, giftDiff, 
							earnedPts,  OCConstants.LOYALTY_TYPE_AMOUNT, description, redemptionTrans.getTransChildId());

					//prepare credit object
					creditedGift = giftDiff+"";
					Credit credit = prepareCreditObject(contactsLoyalty, creditedPoints, creditedReward, creditedGift);
					credList.add(credit);
					responseMap.put("creditList",credList);
					responseMap.put("balToCredit",balToCredit);
				}
			}//Gift-card
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

	private void createTransctnAndExpiry(LoyaltyReturnTransactionRequest returnTransactionRequest,ContactsLoyalty contactsLoyalty,
											Users user, ResponseHeader responseHeader, String rewardFlag, 
											double earnedAmount, double earnedPoints, String earnType, String description, Long redeemedOn) {
		
		logger.info("createTransctnAndExpiry method called...");
		long pointsDifference = 0;
		long amountDifference = 0;
		//create transaction 
		LoyaltyTransactionChild childTx = createReturnTransaction(returnTransactionRequest,contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
				""+pointsDifference,""+amountDifference,earnedAmount,earnedPoints, earnType,responseHeader.getTransactionId(),
				Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue()),
				OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL, description, redeemedOn);

		//Expiry transaction
		createExpiryTransaction(contactsLoyalty, earnedAmount, (long)earnedPoints, childTx.getTransChildId(), rewardFlag);
		logger.info("createTransctnAndExpiry method exit...");
	}//createTransctnAndExpiry

	private LoyaltyReturnTransactionResponse prepareRedmptnSuccessResponse(LoyaltyReturnTransactionRequest returnTransactionRequest,
													ResponseHeader responseHeader, String requestJson, Users user,
													MembershipResponse response, List<Balance> balances, HoldBalance holdBalance, BalancesAdditionalInfo additionalInfo, 
													List<MatchedCustomer> matchedCustomers, boolean isIssuanceFailed, String msg, LoyaltyProgram loyaltyProgram, 
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

	private LoyaltyReturnTransactionResponse performIssuanceBasedReversal(LoyaltyTransactionChild loyaltyTransactionChild, 
				LoyaltyReturnTransactionRequest returnTransactionRequest, ResponseHeader responseHeader, 
				String requestJson, Users user) throws Exception {
		
		logger.info("performIssuanceBasedReversal method called...");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		if(Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue()) > loyaltyTransactionChild.getEnteredAmount()) {
			Status status = new Status("111567", PropertyUtil.getErrorMessage(111567, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;
		}
		
		ContactsLoyalty contactsLoyalty = findLoyaltyById(loyaltyTransactionChild.getLoyaltyId(), loyaltyTransactionChild.getProgramId(),
				user.getUserId());
		//check if program is active and card-set
		LoyaltyProgram loyaltyProgram = null;
		loyaltyProgram = findLoyaltyProgramByProgramId(loyaltyTransactionChild.getProgramId(), user.getUserId());
		if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus())){
			/*status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;*/
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(returnTransactionRequest.getOriginalReceipt().getDocSID(), 
					user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,null,null,null);
			returnTransactionResponse = performRedemptnBasedReversal(redempTransList, returnTransactionRequest,	responseHeader, requestJson, user, 
					null, null, null, null, null,  true, PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".",111505);
			return returnTransactionResponse;
		}

		if(loyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)) {
			LoyaltyCardSet loyaltyCardSet = null;
			loyaltyCardSet = findLoyaltyCardSetByCardsetId(loyaltyTransactionChild.getCardSetId(), user.getUserId());
			if(loyaltyCardSet == null || !OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE.equals(loyaltyCardSet.getStatus())){
				/*status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;*/
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(returnTransactionRequest.getOriginalReceipt().getDocSID(), 
						user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,null,null,null);
				returnTransactionResponse = performRedemptnBasedReversal(redempTransList, returnTransactionRequest,	responseHeader, requestJson, user, 
						null, null, null, null, null,  true, PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".",111505);
				return returnTransactionResponse;
			}
		}
		
		if(contactsLoyalty == null){
			/*status = new Status("1000", PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null,status);
			return returnTransactionResponse;*/
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(returnTransactionRequest.getOriginalReceipt().getDocSID(), 
					user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,null,null,null);
			returnTransactionResponse = performRedemptnBasedReversal(redempTransList, returnTransactionRequest,	responseHeader, requestJson, user, 
					null, null, null, null, null,  true, PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), 1000);
			return returnTransactionResponse;
		}

		if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
			LoyaltyProgramTier tier = null;
			
			
			List<Balance> balances = null;
			List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
			
			/*String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG);
			status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, null, null, matchedCustomers, status);
			return returnTransactionResponse;*/
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(returnTransactionRequest.getOriginalReceipt().getDocSID(), 
					user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,null,null,null);
			String message = "";
			int errorCode = 0;
			if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
				balances = prepareBalancesObject(contactsLoyalty, "", "", "");
				contactLoyaltyList.add(contactsLoyalty);
				if(contactsLoyalty.getProgramTierId() != null)	tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				errorCode = 111517;
			}
			else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
				contactLoyaltyList.add(contactsLoyalty);
				balances = prepareBalancesObject(contactsLoyalty, "", "", "");
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
					balances = prepareBalancesObject(destLoyalty, "", "", "");
					maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
					
				}
				 message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+ maskedNum+".";
				 errorCode = 111578;
			}
			List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
			MembershipResponse response = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
			returnTransactionResponse = performRedemptnBasedReversal(redempTransList, returnTransactionRequest,	responseHeader, requestJson, user, 
					response, balances, null, null, matchedCustomers,  true, message, errorCode);
			return returnTransactionResponse;
		}

		/*LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
		if(loyaltyExclusion != null){
			status = validateStoreNumberExclusion(returnTransactionRequest, loyaltyProgram, loyaltyExclusion);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}
		}*/
		returnTransactionResponse = performReversalOperation(loyaltyTransactionChild, returnTransactionRequest, responseHeader, loyaltyProgram,
															contactsLoyalty, user, requestJson);
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

	private LoyaltyReturnTransactionResponse cardBasedReturnTransaction(LoyaltyReturnTransactionRequest returnTransactionRequest,
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

			return performLoyaltySCReturn(returnTransactionRequest, responseHeader, loyaltyProgram, cardNumber, user, requestJson);

		}
		else if(OCConstants.LOYALTY_CARD_STATUS_ACTIVATED.equalsIgnoreCase(loyaltyCard.getStatus())){

			return performGiftSCReturn(returnTransactionRequest, cardNumber, responseHeader, loyaltyProgram,user, requestJson);
		}
		else{
			status = new Status("111537", PropertyUtil.getErrorMessage(111537, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;
		}
	}//cardBasedReturnTransaction

	private LoyaltyReturnTransactionResponse performGiftSCReturn(LoyaltyReturnTransactionRequest returnTransactionRequest,
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
			MembershipResponse response = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
			List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, null, null, matchedCustomers, status);
			return returnTransactionResponse;
		}

		if(returnTransactionRequest.getAmount().getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_STORE_CREDIT)) {
			returnTransactionResponse = performStoreCreditOperation(returnTransactionRequest, responseHeader, loyaltyProgram, contactsLoyalty, user, requestJson);
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
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		if(contactLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED) && contactLoyalty.getTransferedTo() != null) {
			return loyaltyDao.findAllByLoyaltyId(contactLoyalty.getTransferedTo());
			
		}
		
		return null;
	}
	private LoyaltyReturnTransactionResponse performLoyaltySCReturn(LoyaltyReturnTransactionRequest returnTransactionRequest,
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
				balances = prepareBalancesObject(contactsLoyalty, "", "", "");
				contactLoyaltyList.add(contactsLoyalty);
				if(contactsLoyalty.getProgramTierId() != null) tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				
				String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
				balances = prepareBalancesObject(contactsLoyalty, "", "", "");
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
					balances = prepareBalancesObject(destLoyalty, "", "", "");
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
			status = validateStoreNumberExclusion(returnTransactionRequest, loyaltyProgram, loyaltyExclusion);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				return returnTransactionResponse;
			}
		}

		if(returnTransactionRequest.getAmount().getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_STORE_CREDIT)) {
			returnTransactionResponse = performStoreCreditOperation(returnTransactionRequest, responseHeader, loyaltyProgram, contactsLoyalty, user, requestJson);
			return returnTransactionResponse;
		}
		else {
			status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null,status);
			return returnTransactionResponse;
		}
	}//performLoyaltySCReturn

	private LoyaltyReturnTransactionResponse performStoreCreditOperation(LoyaltyReturnTransactionRequest returnTransactionRequest, ResponseHeader responseHeader,
			LoyaltyProgram loyaltyProgram, ContactsLoyalty contactsLoyalty, Users user, String requestJson) throws Exception{

		logger.info("performStoreCreditOperation method called...");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		
		ContactsLoyaltyDaoForDML contactsLoyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		String rewardFlag = "";
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		
		if(contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L) ||
				contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL)) {
			rewardFlag = OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L;
			Double ltyCurrBal = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
			Double balToAdd = ltyCurrBal + Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue());
			contactsLoyalty.setGiftcardBalance(new Double(decimalFormat.format(balToAdd)));
		}else if(contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
			rewardFlag = OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G;
			Double giftBal = contactsLoyalty.getGiftBalance() == null ? 0.0 : contactsLoyalty.getGiftBalance();
			Double balToAdd = giftBal + Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue());
			contactsLoyalty.setGiftBalance(new Double(decimalFormat.format(balToAdd)));
		}
		contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);

		long pointsDifference = 0;
		long amountDifference = 0;
		double earnedPts = 0;
		Double earnedAmt = Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue());
		//create transaction 
		LoyaltyTransactionChild childTx = createReturnTransaction(returnTransactionRequest,contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
				""+pointsDifference,""+amountDifference, earnedAmt, earnedPts, 
				OCConstants.LOYALTY_TYPE_AMOUNT,responseHeader.getTransactionId(),
				Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue()),OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT, "", null);

		//Expiry transaction
		createExpiryTransaction(contactsLoyalty, earnedAmt,	(long)earnedPts, childTx.getTransChildId(), rewardFlag);

		LoyaltyProgramTier loyaltyProgramTier = null;
		if(contactsLoyalty.getProgramTierId() != null)
			loyaltyProgramTier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
		MembershipResponse response = prepareMembershipResponse(contactsLoyalty, loyaltyProgramTier, loyaltyProgram);
		String expiryPeriod = "";
		if(loyaltyProgramTier != null && loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES	&& ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
				(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){
			expiryPeriod = loyaltyProgramTier.getPtsActiveDateValue()+" "+loyaltyProgramTier.getPtsActiveDateType();
		}
		HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
		List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
		contactLoyaltyList.add(contactsLoyalty);
		List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
		List<Balance> balances = prepareBalancesObject(contactsLoyalty, "","", "");
		/*BalancesAdditionalInfo additionalInfo = prepareCreditsAdditionalInfo(OCConstants.LOYALTY_TYPE_STORE_CREDIT, contactsLoyalty.getRewardFlag(),
																	returnTransactionRequest.getAmount().getEnteredValue(), contactsLoyalty.getCardNumber(),"");*/
		Status status = new Status("0", "Return was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, null, matchedCustomers, status);
		logger.info("performStoreCreditOperation method exit...");
		return returnTransactionResponse;
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

	
	private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty,
			Double expiryAmount, long expiryPoints, Long transChildId, String rewardFlag){

		logger.info("-- Entered createExpiryTransaction --");
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

	private LoyaltyReturnTransactionResponse performReversalOperation(LoyaltyTransactionChild loyaltyTransactionChild, LoyaltyReturnTransactionRequest returnTransactionRequest, ResponseHeader responseHeader, LoyaltyProgram loyaltyProgram,
			ContactsLoyalty contactsLoyalty, Users user, String requestJson) throws Exception {

		logger.info("-- Entered performReversalOperation --");
		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;
		
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		double earnedValue = 0;
		String pointsDifference = "";
		String amountDifference = "";
		double autoCnvrtPtsCredit = 0;
		String earnType = "";
		
		double enteredValue = Math.round(Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue().trim()));
		
		if(loyaltyTransactionChild.getConversionAmt() != null && loyaltyTransactionChild.getConversionAmt() != 0) {
			
			Double earnedMultipleFactordbl = loyaltyTransactionChild.getEarnedPoints()/loyaltyTransactionChild.getEnteredAmount();
			long earnedPointsValue = Math.round(enteredValue * earnedMultipleFactordbl);
			
			String[] convrsnRuleArray = loyaltyTransactionChild.getDescription().split(" Points -> ");
			Double multipleFactordbl = Double.parseDouble(convrsnRuleArray[1])/Double.parseDouble(convrsnRuleArray[0]);
			earnedValue = Double.parseDouble(decimalFormat.format(earnedPointsValue * multipleFactordbl));
			amountDifference = "-"+earnedValue;
			earnType = OCConstants.LOYALTY_TYPE_AMOUNT;
			autoCnvrtPtsCredit = (long)earnedPointsValue;
		}
		else {
			if(loyaltyTransactionChild.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
				Double multipleFactordbl = loyaltyTransactionChild.getEarnedPoints()/loyaltyTransactionChild.getEnteredAmount();
				earnedValue = Math.round(enteredValue * multipleFactordbl);
				pointsDifference = "-"+earnedValue;
				earnType = OCConstants.LOYALTY_TYPE_POINTS;
			}
			else {
				Double multipleFactordbl = loyaltyTransactionChild.getEarnedAmount()/loyaltyTransactionChild.getEnteredAmount();
				earnedValue = Math.round(enteredValue * multipleFactordbl);
				amountDifference = "-"+earnedValue;
				earnType = OCConstants.LOYALTY_TYPE_AMOUNT;
			}
		}
		
		//Prepare membership and matched customers objects
		LoyaltyProgramTier loyaltyProgramTier = null;
		if(contactsLoyalty.getProgramTierId() != null)
			loyaltyProgramTier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
		MembershipResponse response = prepareMembershipResponse(contactsLoyalty, loyaltyProgramTier, loyaltyProgram);
		List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
		contactLoyaltyList.add(contactsLoyalty);
		List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);

		//Update balances
		Map<String, Object> balMap = new HashMap<String, Object>();
		balMap = updateContactLoyaltyBalances(earnedValue, earnType, contactsLoyalty, loyaltyTransactionChild.getTransChildId());
		status = (Status) balMap.get("status");
		if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
			
			List<Balance> balances = prepareBalancesObject(contactsLoyalty, "", "", "");
			String expiryPeriod = "";
			if(loyaltyProgramTier != null && loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES	&& ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
					(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){
				expiryPeriod = loyaltyProgramTier.getPtsActiveDateValue()+" "+loyaltyProgramTier.getPtsActiveDateType();
			}
			HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
			BalancesAdditionalInfo additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
			
			//check for redemption reversal
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(returnTransactionRequest.getOriginalReceipt().getDocSID(), 
					user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,null,null,null);
			returnTransactionResponse = performRedemptnBasedReversal(redempTransList, returnTransactionRequest,	responseHeader, requestJson, user, 
																	response, balances, holdBalance, additionalInfo, matchedCustomers,  true,  PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 111562);
			return returnTransactionResponse;
			
		/*	List<Balance> balances = prepareBalancesObject(contactsLoyalty, "", "", "");
			String expiryPeriod = "";
			if(loyaltyProgramTier != null && loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES	&& ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
					(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){
				expiryPeriod = loyaltyProgramTier.getPtsActiveDateValue()+" "+loyaltyProgramTier.getPtsActiveDateType();
			}
			HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
			//BalancesAdditionalInfo additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, response, balances, holdBalance, null, matchedCustomers, status);
			return returnTransactionResponse;*/
		}

		//Create return transaction
		createReturnTransaction(returnTransactionRequest,contactsLoyalty,user.getUserOrganization().getUserOrgId(), 
								pointsDifference, amountDifference, null , null, null, 
								responseHeader.getTransactionId(), Double.parseDouble(returnTransactionRequest.getAmount().getEnteredValue()),OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL, 
								returnTransactionRequest.getAmount().getEnteredValue(), null);
		List<Balance> balances = prepareBalancesObject(contactsLoyalty, ""+pointsDifference, ""+amountDifference, "");
		String expiryPeriod = "";
		if(loyaltyProgramTier != null && loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES	&& ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
				(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){

			expiryPeriod = loyaltyProgramTier.getPtsActiveDateValue()+" "+loyaltyProgramTier.getPtsActiveDateType();
		}
		
		//check for redemption reversal
		
		HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
		BalancesAdditionalInfo additionalInfo = prepareDebitAddtionalInfo(balMap,contactsLoyalty);
		
		LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		List<LoyaltyTransactionChild> redempTransList = loyaltyTransactionChildDao.findByDocSID(returnTransactionRequest.getOriginalReceipt().getDocSID(), 
				user.getUserId(), OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,null,null,null);
		returnTransactionResponse = performRedemptnBasedReversal(redempTransList, returnTransactionRequest,	responseHeader, requestJson, user, 
																	response, balances, holdBalance, additionalInfo, matchedCustomers, false, "Return was successful on issuance.", 0);
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
			double value = new BigDecimal(contactsLoyalty.getHoldAmountBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			holdBalance.setCurrency(""+value);
		}
		holdBalance.setPoints(contactsLoyalty.getHoldPointsBalance() == null ? "" : ""+contactsLoyalty.getHoldPointsBalance().intValue());
		return holdBalance;
	}

	private LoyaltyTransactionChild createReturnTransaction(LoyaltyReturnTransactionRequest returnTransactionRequest,ContactsLoyalty loyalty,
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
			transaction.setEnteredAmount(enteredAmount);
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
			
			transaction.setStoreNumber(returnTransactionRequest.getHeader().getStoreNumber());
			transaction.setSubsidiaryNumber(returnTransactionRequest.getHeader().getSubsidiaryNumber() != null && !returnTransactionRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? returnTransactionRequest.getHeader().getSubsidiaryNumber().trim() : null);
			transaction.setReceiptNumber(returnTransactionRequest.getHeader().getReceiptNumber() != null && !returnTransactionRequest.getHeader().getReceiptNumber().trim().isEmpty() ? returnTransactionRequest.getHeader().getReceiptNumber() : null);
			
			transaction.setDocSID(returnTransactionRequest.getHeader().getDocSID());
			//transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE);
			transaction.setSourceType(returnTransactionRequest.getHeader().getSourceType());
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setDescription(description);
			transaction.setRedeemedOn(redeemedOn);
			if(!returnTransactionRequest.getAmount().getType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT)) {
				transaction.setDescription2(returnTransactionRequest.getOriginalReceipt().getDocSID()+"");
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

	private Map<String, Object> updateContactLoyaltyBalances(double earnedVal, String earnType, ContactsLoyalty contactsLoyalty, 
											 Long transChildId) throws Exception {

		try {
		Status status = null;
		double loyaltyPoints = contactsLoyalty.getLoyaltyBalance() == null ? 0 : contactsLoyalty.getLoyaltyBalance();
		double holdPoints = contactsLoyalty.getHoldPointsBalance() == null ? 0 : contactsLoyalty.getHoldPointsBalance();
		double loyaltyAmount = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
		double holdAmount = contactsLoyalty.getHoldAmountBalance() == null ? 0 : contactsLoyalty.getHoldAmountBalance();
		double returnBal = earnedVal;


		logger.info("loyaltyPoints="+loyaltyPoints);
		logger.info("loyaltyAmount="+loyaltyAmount);
		logger.info("holdPoints="+holdPoints);
		logger.info("holdAmount="+holdAmount);
		logger.info("returnBal="+returnBal);
		
		Map<String, Object> balMap = new HashMap<String, Object>();
		String debitedRewardPoints = "";
		String debitedHoldPoints = "";
		String debitedRewardCurrency = "";
		String debitedHoldCurrency = "";
		
		if(earnType != null && earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)){
			
			long deductPoints = 0;
			long deductHoldPoints = 0;
			if((loyaltyPoints+holdPoints) >= earnedVal){
				logger.info("Earned points is less than available points bal...");
				if(returnBal <= holdPoints){
					deductHoldPoints = (long) returnBal;
					contactsLoyalty.setHoldPointsBalance(holdPoints - returnBal);
					debitedHoldPoints = (long) returnBal+"";
					returnBal = 0;
				}
				else{
					returnBal = returnBal - holdPoints;
					deductHoldPoints = (long)holdPoints;
					deductPoints = (long) returnBal;
					contactsLoyalty.setHoldPointsBalance(0.0);
					contactsLoyalty.setLoyaltyBalance(loyaltyPoints - returnBal);
					debitedHoldPoints = (long) holdPoints+"";
					debitedRewardPoints = deductPoints+"";
				}
				saveContactsLoyalty(contactsLoyalty);
				deductHoldPoints(contactsLoyalty, deductHoldPoints);
				deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductPoints);
			}
			else{
				status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				//return status;
			}
		}
		else if(earnType != null && earnType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)){
			double deductAmount = 0.0;
			double deductHoldAmount = 0.0;
			if((loyaltyAmount+holdAmount) >= earnedVal){
				logger.info("Earned amount is less than available amount bal...");
				if(returnBal <= holdAmount){
					deductHoldAmount = returnBal;
					contactsLoyalty.setHoldAmountBalance(holdAmount - returnBal);
					debitedHoldCurrency = returnBal+"";
					returnBal = 0;
				}
				else{
					returnBal = returnBal - holdAmount;
					deductHoldAmount = holdAmount;
					deductAmount = returnBal;
					contactsLoyalty.setHoldAmountBalance(0.0);
					contactsLoyalty.setGiftcardBalance(loyaltyAmount - returnBal);
					debitedHoldCurrency = holdAmount+"";
					debitedRewardCurrency = deductAmount+"";
				}
//				contactsLoyalty.setLoyaltyBalance(loyaltyPoints + autoCnvrtPtsCredit); //TODO confirm once
				saveContactsLoyalty(contactsLoyalty);
//				createExpiryTransaction(contactsLoyalty, 0.0, (long)autoCnvrtPtsCredit, transChildId, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
				deductHoldAmount(contactsLoyalty, deductHoldAmount);
				deductLoyaltyAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductAmount);
			}
			else{
				status = new Status("111562", PropertyUtil.getErrorMessage(111562, OCConstants.ERROR_LOYALTY_FLAG), 
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				//return status;
			}
		}
//		balMap.put("autoCnvrtPtsCredit", autoCnvrtPtsCredit);
		balMap.put("debitedHoldPoints", debitedHoldPoints);
		balMap.put("debitedRewardPoints", debitedRewardPoints);
		balMap.put("debitedHoldCurrency", debitedHoldCurrency);
		balMap.put("debitedRewardCurrency", debitedRewardCurrency);
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

	private LoyaltyReturnTransactionResponse mobileBasedReturnTransaction(ContactsLoyalty contactsLoyalty, ResponseHeader responseHeader,
			LoyaltyReturnTransactionRequest returnTransactionRequest,Users user, String requestJson) throws Exception{

		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		Status status = null;

		LoyaltyProgram loyaltyProgram = findActiveMobileProgram(contactsLoyalty.getProgramId());

		if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equals(loyaltyProgram.getStatus())){
			status = new Status("111522", PropertyUtil.getErrorMessage(111522, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			return returnTransactionResponse;
		}
		return performLoyaltySCReturn(returnTransactionRequest, responseHeader, loyaltyProgram, contactsLoyalty.getCardNumber(), user, requestJson);
	}

	private LoyaltyProgram findActiveMobileProgram(Long programId) throws Exception {

		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findById(programId);
	}

	private Status validateStoreNumberExclusion(LoyaltyReturnTransactionRequest returnTransactionRequest, LoyaltyProgram program, 
			LoyaltyProgramExclusion loyaltyExclusion) throws Exception {

		Status status = null;
		if(loyaltyExclusion.getStoreNumberStr() != null && !loyaltyExclusion.getStoreNumberStr().trim().isEmpty()){
			String[] storeNumberArr = loyaltyExclusion.getStoreNumberStr().split(";=;");
			for(String storeNo : storeNumberArr){
				if(returnTransactionRequest.getHeader().getStoreNumber().trim().equals(storeNo.trim())){
					status = new Status("111532", PropertyUtil.getErrorMessage(111532, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
			}
		}
		return status;
	}

	private MembershipResponse prepareMembershipResponse(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier, 
			LoyaltyProgram program) throws Exception {

		MembershipResponse membershipResponse = new MembershipResponse();

		if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
			membershipResponse.setCardNumber(""+contactsLoyalty.getCardNumber());
			membershipResponse.setCardPin(contactsLoyalty.getCardPin());
			membershipResponse.setPhoneNumber("");
		}
		else{
			membershipResponse.setCardNumber("");
			membershipResponse.setCardPin("");
			membershipResponse.setPhoneNumber(""+contactsLoyalty.getCardNumber());
		}
		if(program.getTierEnableFlag() == OCConstants.FLAG_YES && tier != null){
			membershipResponse.setTierLevel(tier.getTierType());
			membershipResponse.setTierName(tier.getTierName());
		}
		else{
			membershipResponse.setTierLevel("");
			membershipResponse.setTierName("");
		}

		if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
			if(program.getGiftMembrshpExpiryFlag() == 'Y'){
				membershipResponse.setExpiry(LoyaltyProgramHelper.getGiftMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), 
						program.getGiftMembrshpExpiryDateType(), program.getGiftMembrshpExpiryDateValue()));
			}
			else{
				membershipResponse.setExpiry("");
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
				membershipResponse.setExpiry("");
			}
		}

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

	private LoyaltyCardSet findLoyaltyCardSetByCardsetId(Long cardSetId, Long userId) throws Exception {
		LoyaltyCardSetDao cardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		return cardSetDao.findByCardSetId(cardSetId);

	}
	private LoyaltyProgram findLoyaltyProgramByProgramId(Long programId, Long userId) throws Exception {

		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findByIdAndUserId(programId, userId);
	}

	private LoyaltyCards findLoyaltyCardByUserId(String cardNumber, Long userId) throws Exception {

		LoyaltyCardsDao loyaltyCardDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return loyaltyCardDao.findByCardNoAnduserId(cardNumber, userId);

	}

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
	private List<Contacts> findOCContact(Contacts jsonContact, Long userId,Users user) throws Exception {
		//logger.info("Entered findOCContact method >>>>");
		POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(userId, Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
		List<Contacts> dbContactList = contactsDao.findMatchedContactListByUniqPriority(priorMap, jsonContact, userId,user);
		//logger.info("Exited findOCContact method >>>>");
		return dbContactList;
	}

	private List<ContactsLoyalty> findEnrollListByMobile(String mobile, Long userId) throws Exception {

		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByMobile(mobile, userId);
	}

	private ContactsLoyalty findContactLoyaltyByMobile(String mobile, Long userId) throws Exception {

		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByPhone(Long.valueOf(mobile), OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE, userId);
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

		}catch(Exception e){
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
	private Status validateReturnTransactionJsonData(LoyaltyReturnTransactionRequest returnTransactionRequest) throws Exception{
		
		logger.info("-- Enetred validateReturnTransactionJsonData --");
		Status status = null;
		if(returnTransactionRequest == null ){
			status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}

		if(returnTransactionRequest.getHeader().getDocSID() == null || returnTransactionRequest.getHeader().getDocSID().trim().isEmpty()){
			status = new Status("111510", PropertyUtil.getErrorMessage(111510, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(returnTransactionRequest.getHeader().getStoreNumber() == null || returnTransactionRequest.getHeader().getStoreNumber().length() <= 0){
			status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if(returnTransactionRequest.getAmount() == null || returnTransactionRequest.getAmount().getType() == null || returnTransactionRequest.getAmount().getType().trim().isEmpty()
				|| returnTransactionRequest.getAmount().getEnteredValue() == null || returnTransactionRequest.getAmount().getEnteredValue().trim().isEmpty()) {
			status = new Status("111534", PropertyUtil.getErrorMessage(111534, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}

		if(returnTransactionRequest.getAmount().getType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_STORE_CREDIT) && 
				!returnTransactionRequest.getAmount().getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY)) {
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
	
}
