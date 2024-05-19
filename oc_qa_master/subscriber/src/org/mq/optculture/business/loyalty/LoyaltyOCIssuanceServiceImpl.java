


package org.mq.optculture.business.loyalty;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.catalina.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyCardSetDaoForDML;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramExclusionDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
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
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.Discounts;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceResponse;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.Promotion;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.SkuDetails;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;
/**
 * === OptCulture Loyalty Program ===
 * 
 * Performs loyalty issuance on a given card number with the specified amount 
 * On successful, balances(points, amount) data are updated in OptCulture database for reference. 
 *
 * @author Venkata Rathnam D
 * @deprecated
 */
public class LoyaltyOCIssuanceServiceImpl implements LoyaltyIssuanceOCService{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/**
	 * BaseService Request called by rest service controller.
	 * @return BaseResponseObject
	 */
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {

		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		Gson gson = new Gson();
		LoyaltyIssuanceResponse issuanceResponse = null;
		LoyaltyIssuanceRequest issuanceRequest = null;
		BaseResponseObject responseObject = null;
		String responseJson = null;
		
		if(serviceRequest == null || !serviceRequest.equals(OCConstants.LOYALTY_SERVICE_ACTION_ISSUANCE)){
			issuanceResponse = new LoyaltyIssuanceResponse();

			Status status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse.setStatus(status);
			//Convert Object to JSON string
			responseJson = gson.toJson(issuanceResponse);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}

		try{
			issuanceRequest = gson.fromJson(requestJson, LoyaltyIssuanceRequest.class);
		}catch(Exception e){
			
			Status status = new Status("101001", ""+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			issuanceResponse = new LoyaltyIssuanceResponse();
			issuanceResponse.setStatus(status);
			responseJson = gson.toJson(issuanceResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid JSON ");
			return responseObject;
		}
		
		try{
		logger.info("POS issuance request...online mode ...");
			LoyaltyIssuanceOCService loyaltyIssuanceOCService = (LoyaltyIssuanceOCService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_ISSUANCE_OC_BUSINESS_SERVICE);
			issuanceResponse = loyaltyIssuanceOCService.processIssuanceRequest(issuanceRequest, OCConstants.LOYALTY_ONLINE_MODE, baseRequestObject.getTransactionId(),
					baseRequestObject.getTransactionDate(),null);
			responseJson = gson.toJson(issuanceResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
		}catch(Exception e){
			logger.error("Exception in loyalty issuance base service.",e);
			throw new BaseServiceException("Server Error.");
		}
		return responseObject;
	}
	/**
	 * Handles the complete process of Loyalty Issuance for either points or amount(USD).
	 * 
	 * @param issuanceRequest
	 * @return issuanceResponse
	 * @throws BaseServiceException
	 */

	@Override
	public LoyaltyIssuanceResponse processIssuanceRequest(LoyaltyIssuanceRequest issuanceRequest, String mode, 
			String transactionId, String transactionDate,String notRequired) throws BaseServiceException {//To avoid error introduced notRequired

		logger.info(">>> Loyalty Issuance started");
		
		LoyaltyIssuanceResponse issuanceResponse = null;
		Status status = null;
		Users user = null;
		
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(issuanceRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(issuanceRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		responseHeader.setSourceType(issuanceRequest.getHeader().getSourceType() != null && 
				!issuanceRequest.getHeader().getSourceType().trim().isEmpty() ? issuanceRequest.getHeader().getSourceType().trim() : Constants.STRING_NILL);
		
		/*responseHeader.setSubsidiaryNumber(issuanceRequest.getHeader().getSubsidiaryNumber() != null && !issuanceRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? issuanceRequest.getHeader().getSubsidiaryNumber().trim() : Constants.STRING_NILL);
		responseHeader.setReceiptNumber(issuanceRequest.getHeader().getReceiptNumber() != null && !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty() ? issuanceRequest.getHeader().getReceiptNumber().trim() : Constants.STRING_NILL);
		responseHeader.setReceiptAmount(Constants.STRING_NILL);*/
		
		
		try{
			
			status = validateIssuanceJsonData(issuanceRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				return issuanceResponse;
			}
			
			status = validateEnteredValue(issuanceRequest.getAmount());
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				return issuanceResponse;
			}
			
			user = getUser(issuanceRequest.getUser().getUserName(), issuanceRequest.getUser().getOrganizationId(),
					issuanceRequest.getUser().getToken());
			if(user == null){
				status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				return issuanceResponse;
			}
			if(!user.isEnabled()){
				status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				return issuanceResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				return issuanceResponse;
			}
			
			//if the user has loyalty type oc(changed during migrationsbtooc)
			if(OCConstants.LOYALTY_SERVICE_TYPE_OC.equals(user.getloyaltyServicetype())) {
				
				if(issuanceRequest.getHeader().getDocSID() == null || issuanceRequest.getHeader().getDocSID().trim().isEmpty()){
					status = new Status("111510", PropertyUtil.getErrorMessage(111510, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
					return issuanceResponse;
				}
				
				if(issuanceRequest.getHeader().getStoreNumber() == null || issuanceRequest.getHeader().getStoreNumber().length() <= 0){
					status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
					return issuanceResponse;
				}
				
			}
			
			ContactsLoyalty contactsLoyalty = null;
			
			//changes w,r,t migration.When the isuance of type SBToOC-offline comes in, 
			//cardnumber may not be given as if the enrollment goes offline
			//TODO 
			if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(user.getloyaltyServicetype()) && OCConstants.LOYALTY_OFFLINE_MODE.equals(mode) ){
				if(issuanceRequest.getMembership().getCardNumber() == null || issuanceRequest.getMembership().getCardNumber().length() == 0) {
					
					String customerId = issuanceRequest.getCustomer().getCustomerId();
					
					contactsLoyalty = findLoyaltyCardInOCByCustId(customerId, user.getUserId());
					if(contactsLoyalty == null ){
						
						logger.info("contactsloyalty not found in OC and card is not given in request...");
						
						status = new Status("200013", PropertyUtil.getErrorMessage(200013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
						return issuanceResponse;
					}
			}
					
				
				
			}//
			
			if(issuanceRequest.getMembership().getCardNumber() != null && issuanceRequest.getMembership().getCardNumber().length() > 0){
				logger.info("Issuance by card number >>>");
				
				/*String cardNumber = Constants.STRING_NILL;
				Long cardLong = null;
				//****if(issuanceRequest.getMembership().getCardNumber().trim().length() != 16) {
					status = new Status("100107", PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
					return issuanceResponse;
				}***already commented
				
				
				
				cardLong = OptCultureUtils.validateOCLtyCardNumber(issuanceRequest.getMembership().getCardNumber().trim());
				if(cardLong == null){
					String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+issuanceRequest.getMembership().getCardNumber().trim()+".";
					status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
					return issuanceResponse;
				}
				cardNumber = Constants.STRING_NILL+cardLong;
				issuanceRequest.getMembership().setCardNumber(cardNumber);
				*/
				return cardBasedIssuance(issuanceRequest, mode, issuanceRequest.getMembership().getCardNumber().trim(), responseHeader, user);
				
			}
			else if(issuanceRequest.getMembership().getPhoneNumber() != null && issuanceRequest.getMembership().getPhoneNumber().trim().length() > 0){
				logger.info("Issuance by phone number >>>");
				
				String validStatus = LoyaltyProgramHelper.validateMembershipMobile(issuanceRequest.getMembership().getPhoneNumber().trim());
				if(OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID.equals(validStatus)){
					status = new Status("111554", PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+issuanceRequest.getMembership().getPhoneNumber().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null,status);
					return issuanceResponse;
				}
				
				String phoneNumber=issuanceRequest.getMembership().getPhoneNumber().trim();
				contactsLoyalty = findContactLoyaltyByMobile(phoneNumber, user);
				
				if(contactsLoyalty == null){
					status = new Status("111519", PropertyUtil.getErrorMessage(111519, OCConstants.ERROR_LOYALTY_FLAG)+" "+issuanceRequest.getMembership().getPhoneNumber().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null,status);
					return issuanceResponse;
				}
				
				return mobileBasedIssuance(issuanceRequest, contactsLoyalty, responseHeader, 
						issuanceRequest.getMembership().getPhoneNumber(), user, mode);
			}
			else if (issuanceRequest.getCustomer() != null && issuanceRequest.getCustomer().getPhone().trim().length() > 0){
				
				List<ContactsLoyalty> enrollList = findEnrollListByMobile(issuanceRequest.getCustomer().getPhone().trim(), user.getUserId());
				
				if(enrollList == null){
					status = new Status("111524", PropertyUtil.getErrorMessage(111524, OCConstants.ERROR_LOYALTY_FLAG)+" "+issuanceRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null,status);
					return issuanceResponse;
				}
				
				List<Contacts> dbContactList = null;
				Contacts dbContact = null;
				
				if(enrollList.size() > 1){
					logger.info("Found more than 1 enrollments");
					Contacts jsonContact = prepareContactFromJsonData(issuanceRequest.getCustomer(), user.getUserId());
					jsonContact.setUsers(user);
					dbContactList = findOCContact(jsonContact, user.getUserId(),user);
					
					if(dbContactList == null || dbContactList.size() == 0){
						logger.info(" request contact not found in OC");
						
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);
						
						status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+issuanceRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, matchedCustomers, status);
						return issuanceResponse;
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
							
							status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+issuanceRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, matchedCustomers, status);
							return issuanceResponse;
						}
					}
					else{
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);
						
						status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+issuanceRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, matchedCustomers, status);
						return issuanceResponse;
					}
					
					
				}
				else{
					logger.info("loyalty found in else case....");
					contactsLoyalty = enrollList.get(0);
				}
				logger.info("contactsLoyalty = "+contactsLoyalty);
				
				if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(contactsLoyalty.getMembershipType())){
					
					return mobileBasedIssuance(issuanceRequest, contactsLoyalty, responseHeader, issuanceRequest.getCustomer().getPhone(), user, mode);
				}
				else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
					
					return cardBasedIssuance(issuanceRequest, mode, Constants.STRING_NILL+contactsLoyalty.getCardNumber(), responseHeader, user);
				}
				else{
					status = new Status("111511", PropertyUtil.getErrorMessage(111511, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
					return issuanceResponse;
				}
				
			}
			else{
				status = new Status("111523", PropertyUtil.getErrorMessage(111523, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null,status);
				return issuanceResponse;
			}
			
		}catch(Exception e){
			logger.error("Exception in loyalty issuance service", e);
			throw new BaseServiceException("Loyalty Issuance Request Failed");
		}
		
	}
	/**
	 * Validates all JSON Request parameters
	 * @param LoyaltyIssuanceRequest
	 * @return StatusInfo
	 * @throws Exception
	 */
	private Status validateIssuanceJsonData(LoyaltyIssuanceRequest issuanceRequest) throws Exception{

		Status status = null;

		if(issuanceRequest == null ){
			status = new Status(
					"101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(issuanceRequest.getUser() == null){
			status = new Status(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(issuanceRequest.getMembership() == null){
			status = new Status(
					"101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(issuanceRequest.getUser().getUserName() == null || issuanceRequest.getUser().getUserName().trim().length() <=0 || 
				issuanceRequest.getUser().getOrganizationId() == null || issuanceRequest.getUser().getOrganizationId().trim().length() <=0 || 
				issuanceRequest.getUser().getToken() == null || issuanceRequest.getUser().getToken().trim().length() <=0) {
			status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if(issuanceRequest.getAmount() == null || issuanceRequest.getAmount().getValueCode() == null ||
				!issuanceRequest.getAmount().getValueCode().equals(OCConstants.LOYALTY_TYPE_CURRENCY)){
			status = new Status("111534", PropertyUtil.getErrorMessage(111534, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if(issuanceRequest.getAmount().getEnteredValue() == null || issuanceRequest.getAmount().getEnteredValue().trim().isEmpty() ||
				issuanceRequest.getAmount().getValueCode() == null || issuanceRequest.getAmount().getValueCode().trim().isEmpty() ||
				!(issuanceRequest.getAmount().getValueCode().equals(OCConstants.LOYALTY_TYPE_CURRENCY) ||
						issuanceRequest.getAmount().getValueCode().equals(OCConstants.LOYALTY_TYPE_POINTS))){
			status = new Status("111526", PropertyUtil.getErrorMessage(111526, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		
		return status;
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
	
	private LoyaltyIssuanceResponse prepareIssuanceResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance, List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyIssuanceResponse issuanceResponse = new LoyaltyIssuanceResponse();
		issuanceResponse.setHeader(header);
		
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
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		
		issuanceResponse.setMembership(membershipResponse);
		issuanceResponse.setBalances(balances);
		issuanceResponse.setHoldBalance(holdBalance);
		issuanceResponse.setMatchedCustomers(matchedCustomers);
		issuanceResponse.setStatus(status);
		return issuanceResponse;
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
		pointBalances.setAmount(loyalty.getLoyaltyBalance() == null ? Constants.STRING_NILL : Constants.STRING_NILL+loyalty.getLoyaltyBalance().intValue());
		pointBalances.setDifference(pointsDiff);
			
		
		amountBalances = new Balance();
		amountBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		amountBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		if(loyalty.getGiftcardBalance() == null){
			amountBalances.setAmount(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(loyalty.getGiftcardBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			amountBalances.setAmount(Constants.STRING_NILL+value);
		}
		if(amountDiff == null || amountDiff == Constants.STRING_NILL){
			amountBalances.setDifference(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(Double.valueOf(amountDiff)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			amountBalances.setDifference(Constants.STRING_NILL+value);
		}
		
		giftBalances = new Balance();
		giftBalances.setType(OCConstants.LOYALTY_TYPE_GIFT);
		giftBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		if(loyalty.getGiftBalance() == null){
			giftBalances.setAmount(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(loyalty.getGiftBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			giftBalances.setAmount(Constants.STRING_NILL+value);
		}
		if(giftDiff == null || giftDiff == Constants.STRING_NILL){
			giftBalances.setDifference(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(Double.valueOf(giftDiff)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			giftBalances.setDifference(Constants.STRING_NILL+value);
		}
		
		balancesList.add(pointBalances);
		balancesList.add(amountBalances);
		balancesList.add(giftBalances);
		
		return balancesList;
	}
	
	
	private Status applyLoyaltyExclusions(LoyaltyIssuanceRequest issuanceRequest, LoyaltyProgram program, 
			LoyaltyProgramExclusion loyaltyExclusion, Long orgId) throws Exception {
		
		Status status = null;
		// handle store number
		status = validateStoreNumberExclusion(issuanceRequest, program, loyaltyExclusion);
		if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
			return status;
		}
		
		// handle promo codes
		status = validatePromocodeExclusion(issuanceRequest, program, loyaltyExclusion, orgId);
		if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
			return status;
		}
		// handle items
		//status = validateItemsExclusion();
		
		//handle special dates
		status = validateSpecialDateExclusion(issuanceRequest, program, loyaltyExclusion);
		if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
			return status;
		}
		
		return null;
	}
	
	private Status validateStoreNumberExclusion(LoyaltyIssuanceRequest issuanceRequest, LoyaltyProgram program, 
			LoyaltyProgramExclusion loyaltyExclusion) throws Exception {
		
		Status status = null;
		if(loyaltyExclusion.getStoreNumberStr() != null && !loyaltyExclusion.getStoreNumberStr().trim().isEmpty()){
			String[] storeNumberArr = loyaltyExclusion.getStoreNumberStr().split(";=;");
			for(String storeNo : storeNumberArr){
				if(issuanceRequest.getHeader().getStoreNumber().trim().equals(storeNo.trim())){
					status = new Status("111532", PropertyUtil.getErrorMessage(111532, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
			}
		}
		
		return status;
	}
	
	private Status validatePromocodeExclusion(LoyaltyIssuanceRequest issuanceRequest, LoyaltyProgram program, 
			LoyaltyProgramExclusion loyaltyExclusion, Long orgId) throws Exception {
		
		
		Status status = null;
		
		if(issuanceRequest.getDiscounts().getAppliedPromotion().equalsIgnoreCase("Y") && loyaltyExclusion.getIssuanceWithPromoFlag() == 'Y'){
			
			List<Promotion> promoList = issuanceRequest.getDiscounts().getPromotions();
			String promoCodeStr = Constants.STRING_NILL;
			List<String> promoNames = null;
			
				if(loyaltyExclusion.getIssuancePromoIdStr() != null){
					
					String[] promoIdArr = loyaltyExclusion.getIssuancePromoIdStr().split(";=;");
					if(promoIdArr.length == 1 && promoIdArr[0].equalsIgnoreCase(OCConstants.LOYALTY_PROMO_EXCLUSION_ALL)){
						promoNames = findPromoNames(orgId, null);
					}
					else{
						String promoIdStr = Constants.STRING_NILL;
						for(String promoId : promoIdArr){
								promoIdStr += (promoIdStr == Constants.STRING_NILL) ? "'"+promoId+"'" : ",'"+promoId+"'";
						}
						promoNames = findPromoNames(orgId, promoIdStr);
						logger.info("promoIdStr = "+promoIdStr);
						logger.info("promoNames from oc : "+promoNames);
					}
					
					if(promoNames == null) return status;
					for(Promotion promotion : promoList){
						if(promoNames.contains(promotion.getName().trim())){
							status = new Status("101405", PropertyUtil.getErrorMessage(101405, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							return status;
						}
					}
				}
		}
		return status;
	}
	
	private List<String> findPromoNames(Long orgId, String promoIdStr) throws Exception {
		
		try{
			
			CouponsDao couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			return couponsDao.findCouponNames(orgId, promoIdStr);
			
		}catch(Exception e){
			logger.error("Exception while getting coupons...", e);
		}
		return null;
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
	
	private LoyaltyCards findCardByCardNumber(String programIdStr, String cardSetIdStr, String cardNumber, Long userId) throws Exception {
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return loyaltyCardsDao.findCardByProgram(programIdStr, cardSetIdStr, cardNumber, userId);
	}
	
	/*private ContactsLoyalty prepareContactsLoyaltyObject(LoyaltyCards loyaltyCard, String cardIdType, 
			String optInMedium, String storeNumber, String mode, LoyaltyProgram program, 
			LoyaltyProgramTier tier,String empId,String termId, String sourceType) throws Exception {*/
	private ContactsLoyalty prepareContactsLoyaltyObject(LoyaltyCards loyaltyCard, String cardIdType, 
			String optInMedium,String subsidiaryNumber, String storeNumber, String mode, LoyaltyProgram program, 
			LoyaltyProgramTier tier,String empId,String termId, String sourceType) throws Exception {
		//logger.info("Entered prepareContactsLoyaltyObject >>>>>");
		ContactsLoyalty contactLoyalty = new ContactsLoyalty();
		contactLoyalty.setCardNumber(loyaltyCard.getCardNumber());
		contactLoyalty.setCardPin(loyaltyCard.getCardPin());
		contactLoyalty.setCreatedDate(Calendar.getInstance());
//		contactLoyalty.setOptinMedium(optInMedium);
		contactLoyalty.setContactLoyaltyType(optInMedium);
		contactLoyalty.setSourceType(sourceType);
		contactLoyalty.setSubsidiaryNumber(subsidiaryNumber != null && !subsidiaryNumber.trim().isEmpty() ? subsidiaryNumber.trim() : null);
		contactLoyalty.setPosStoreLocationId(storeNumber);
		contactLoyalty.setEmpId(empId!=null && !empId.trim().isEmpty() ? empId.trim():null);
		contactLoyalty.setTerminalId(termId!=null && !termId.trim().isEmpty() ? termId.trim():null);
		contactLoyalty.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
		contactLoyalty.setMembershipType(cardIdType);
		contactLoyalty.setMode(mode);
		contactLoyalty.setOptinDate(Calendar.getInstance());
		contactLoyalty.setProgramId(program.getProgramId());
		contactLoyalty.setCardSetId(loyaltyCard.getCardSetId());
		
		if(tier != null){
			contactLoyalty.setProgramTierId(tier.getTierId());
			//contactLoyalty.setProgramTierName(tier.getTierName());
		}
		
		return contactLoyalty;
	}
	
	/*private void saveContactLoyalty(ContactsLoyalty loyalty) throws Exception{
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		loyaltyDao.saveOrUpdate(loyalty);
	}*/
	
	private void updateCardStatus(String status, LoyaltyCards loyaltyCard) throws Exception {
		
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
		loyaltyCard.setStatus(status);
		if(status.equals(OCConstants.LOYALTY_CARD_STATUS_ACTIVATED) || status.equals(OCConstants.LOYALTY_CARD_STATUS_ENROLLED)){
			loyaltyCard.setActivationDate(Calendar.getInstance());
		}
		//loyaltyCardsDao.saveOrUpdate(loyaltyCard);
		loyaltyCardsDaoForDML.saveOrUpdate(loyaltyCard);
		
	}
	
	private List<LoyaltyCardSet> findCardSetBy(Long programId, String status, String cardGenerationType) throws Exception {
		
		LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		return loyaltyCardSetDao.findBy(programId, OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE, cardGenerationType);
	}
	
	
	private LoyaltyCards insertDCard(String cardNumber, String cardPin, Users user, 
			LoyaltyProgram dCardBasedProgram, LoyaltyCardSet dCardBasedCardSet) throws Exception {

		LoyaltyCards loyaltyCard = findCardByCardNumber(dCardBasedProgram.getProgramId().toString(), dCardBasedCardSet.getCardSetId().toString(), cardNumber, dCardBasedProgram.getUserId());
		
		
		if (loyaltyCard != null
				&& loyaltyCard.getStatus().equals(
						OCConstants.LOYALTY_CARD_STATUS_SELECTED)) {
			logger.info("Usergenerated card with selected status");
			return null;
		} else if (loyaltyCard != null) {
			return loyaltyCard;
		} else {
			logger.info("inserting user generated card");
			updateCardSetQuantity(dCardBasedCardSet);
			loyaltyCard = new LoyaltyCards();
			loyaltyCard.setProgramId(dCardBasedProgram.getProgramId());
			loyaltyCard.setCardSetId(dCardBasedCardSet.getCardSetId());
			loyaltyCard.setCardNumber(cardNumber);
			loyaltyCard.setCardPin(cardPin);
			loyaltyCard.setOrgId(user.getUserOrganization().getUserOrgId());
			loyaltyCard.setUserId(user.getUserId());
			loyaltyCard.setRegisteredFlag('N');
			//loyaltyCard.setStatus(OCConstants.LOYALTY_CARD_STATUS_SELECTED);
			loyaltyCard.setStatus(OCConstants.LOYALTY_CARD_STATUS_INVENTORY);//in gift issuance and when the dcard is to be inserted it no need to be in selected status.
			/*updateLoyaltyCardStatus(loyaltyCard,
					OCConstants.LOYALTY_CARD_STATUS_SELECTED);*/
			return loyaltyCard;
		}

	}
	private void updateCardSetQuantity(LoyaltyCardSet cardSet) throws Exception{
		LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		LoyaltyCardSetDaoForDML loyaltyCardSetDaoForDML = (LoyaltyCardSetDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARD_SET_DAO_FOR_DML);
		//loyaltyCardSetDao.updateCardSetQuantity(cardSet.getCardSetId(), 1l);
		loyaltyCardSetDaoForDML.updateCardSetQuantity(cardSet.getCardSetId(), 1l);
	}
	
	private void updateLoyaltyCardStatus(LoyaltyCards loyaltyCard, String status) throws Exception {
		
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
		
			
			loyaltyCard.setStatus(status);
			//loyaltyCardsDao.saveOrUpdate(loyaltyCard);
			loyaltyCardsDaoForDML.saveOrUpdate(loyaltyCard);
		
	}
	
	/*private List<LoyaltyCardSet> findActiveCardSets(String programIdStr) throws Exception {
		
		LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		return loyaltyCardSetDao.findByProgramIdStr(programIdStr, OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE);
	}*/
	
	private ContactsLoyalty findContactLoyalty(String cardNumber, Long programId, Long userId) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findByProgram(cardNumber, programId, userId);
	}
	
	private Status validateSpecialDateExclusion(LoyaltyIssuanceRequest issuanceRequest, LoyaltyProgram program, LoyaltyProgramExclusion exclusion) throws Exception {
		
		Status status = null;
		
		if(exclusion.getDateStr() != null){
		
			String requestDate = issuanceRequest.getHeader().getRequestDate();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(sdf1.parse(requestDate));
			
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
			String[] dateArr = null;
			if(exclusion.getDateStr().contains(";=;")){
				dateArr = exclusion.getDateStr().split(";=;");
			
				for(String dateStr : dateArr){
					Calendar cal = Calendar.getInstance();
					cal.setTime(sdf.parse(dateStr));
				
					//Calendar calToday = Calendar.getInstance();
					if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH)) &&
						(cal.get(Calendar.DAY_OF_MONTH) == cal1.get(Calendar.DAY_OF_MONTH))){
					status = new Status("111518", PropertyUtil.getErrorMessage(111518, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
					}
				}
			}
			else{
				Calendar cal = Calendar.getInstance();
				cal.setTime(sdf.parse(exclusion.getDateStr().trim()));
			
				//Calendar calToday = Calendar.getInstance();
				if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH)) &&
					(cal.get(Calendar.DAY_OF_MONTH) == cal1.get(Calendar.DAY_OF_MONTH))){
				status = new Status("111518", PropertyUtil.getErrorMessage(111518, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
				}
			}
		}
		return status;
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
	
	private LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getTierById(tierId);
		
	}
	
	/*private List<String[]> calculateThresholdBonus(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, Double fromLtyBalance, Double fromAmtBalance){
		try{
			
			LoyaltyThresholdBonusDao loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			List<LoyaltyThresholdBonus> threshBonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(program.getProgramId(), 'N' );
			List<LoyaltyThresholdBonus> pointsBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> amountBonusList = new ArrayList<LoyaltyThresholdBonus>();
			
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
					bonusArr[1] = Constants.STRING_NILL+matchedBonus.getExtraBonusValue();
					bonusArr[2] = Constants.STRING_NILL+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
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
					}
					bonusArrList.add(bonusArr);
				}
			}
			
			if(matchedPointsBonus != null && OCConstants.LOYALTY_TYPE_POINTS.equals(matchedPointsBonus.getExtraBonusType())){
				
				bonusArr = new String[3];
				bonusArr[0] = OCConstants.LOYALTY_TYPE_POINTS;
				bonusArr[1] = Constants.STRING_NILL+matchedPointsBonus.getExtraBonusValue();
				bonusArr[2] = Constants.STRING_NILL+matchedPointsBonus.getEarnedLevelValue()+" "+matchedPointsBonus.getEarnedLevelType()+
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
				bonusArr[1] = Constants.STRING_NILL+matchedAmountBonus.getExtraBonusValue();
				bonusArr[2] = Constants.STRING_NILL+matchedAmountBonus.getEarnedLevelValue()+" "+matchedAmountBonus.getEarnedLevelType()+
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
			}
			
			return bonusArrList;
		}catch(Exception e){
			logger.error("Exception in update threshold bonus...", e);
			return null;
		}
		
	}*/
	private void calculateThresholdBonus(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, Double fromLtyBalance, Double fromAmtBalance, LoyaltyProgramTier tier, 
			LoyaltyAutoComm autoComm, Users user, LoyaltyIssuanceRequest issuanceRequest, Long pointsDifference, double amountDifference){
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
			LoyaltyTransactionChild transaction=null;
			if(matchedBonusList != null && matchedBonusList.size() > 0){
				for (LoyaltyThresholdBonus matchedBonus : matchedBonusList) {
					if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getExtraBonusType())){
						bonusflag = true;
						logger.info("loyalty bonus type :Points:");
						bonusPoints = matchedBonus.getExtraBonusValue().longValue();
						bonusRate = Constants.STRING_NILL+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
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
						LoyaltyProgramTier bonusTxTier = null;
						if(tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
							bonusTxTier = tier;
						}
						//LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, bonusPoints, OCConstants.LOYALTY_TYPE_POINTS, bonusRate);
						LoyaltyTransactionChild childTxbonus = createBonusTransaction(issuanceRequest, contactsLoyalty, bonusPoints, OCConstants.LOYALTY_TYPE_POINTS, bonusRate);
						transaction=childTxbonus;
						createExpiryTransaction(contactsLoyalty, (long)bonusPoints, (double)bonusAmount, 
								childTxbonus.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
						
						if(bonusTxTier != null){
							String[] diffBonArr = applyConversionRules(contactsLoyalty, bonusTxTier); 
							logger.info("balances After conversion rules updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
							
							String conversionBonRate = null;
							long convertBonPoints = 0;
							double convertBonAmount = 0;
							//double convertBonAmount = 0.0;
							if(diffBonArr != null){
								logger.info("Arr[]"+diffBonArr);
								convertBonAmount = Double.valueOf(diffBonArr[0].trim());
								//convertBonAmount = Double.valueOf(diffBonArr[0].trim()).doubleValue();
								convertBonPoints = Double.valueOf(diffBonArr[1].trim()).longValue();
								conversionBonRate = diffBonArr[2];
							}
							pointsDifference = bonusPoints - convertBonPoints;
							//amountDifference = (long)bonusAmount + convertBonAmount;
							if(diffBonArr[0]!=null)
							amountDifference = Double.valueOf(diffBonArr[0].trim()).doubleValue();
							bonusTxTier = applyTierUpgradeRule(contactsLoyalty, program, bonusTxTier, autoComm, user, issuanceRequest);
							logger.info("Earn Type: Point =============== ");
							logger.info(" Point diff::"+pointsDifference);
							logger.info("amount diff :"+amountDifference);
							updatePurchaseTransaction(childTxbonus, contactsLoyalty, Constants.STRING_NILL+pointsDifference, Constants.STRING_NILL+amountDifference, conversionBonRate, convertBonAmount, bonusTxTier);
							//updatePurchaseTransaction(childTxbonus, contactsLoyalty, Constants.STRING_NILL+pointsDifference, Constants.STRING_NILL+amountDifference, conversionBonRate, Double.valueOf(diffBonArr[0].trim()).longValue(), bonusTxTier);
						}
					}
					else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getExtraBonusType())){
						bonusflag = true;
						logger.info("loyalty bonus type :Amount:");
						//bonusAmount = matchedBonus.getExtraBonusValue();
						//bonusAmount = new BigDecimal(matchedBonus.getExtraBonusValue()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
						String result = Utility.truncateUptoTwoDecimal(matchedBonus.getExtraBonusValue());
						if(result != null)
							bonusAmount = Double.parseDouble(result);
						bonusRate = Constants.STRING_NILL+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
								" --> "+matchedBonus.getExtraBonusValue()+" "+OCConstants.LOYALTY_TYPE_AMOUNT;
						if(contactsLoyalty.getGiftcardBalance() == null ) {
							//contactsLoyalty.setGiftcardBalance(matchedBonus.getExtraBonusValue());
							contactsLoyalty.setGiftcardBalance(bonusAmount);
						}
						else{
							//contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + matchedBonus.getExtraBonusValue());
							contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + bonusAmount).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
						}
						if(contactsLoyalty.getTotalGiftcardAmount() == null){
							//contactsLoyalty.setTotalGiftcardAmount(matchedBonus.getExtraBonusValue());
							contactsLoyalty.setTotalGiftcardAmount(bonusAmount);
						}
						else{
							//contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + matchedBonus.getExtraBonusValue());
							contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + bonusAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						}
						LoyaltyProgramTier bonusTxTier = null;
						if(tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
							bonusTxTier = tier;
						}
						//LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, bonusAmount, OCConstants.LOYALTY_TYPE_AMOUNT, bonusRate);
						LoyaltyTransactionChild childTxbonus = createBonusTransaction(issuanceRequest, contactsLoyalty, bonusAmount, OCConstants.LOYALTY_TYPE_AMOUNT, bonusRate);
						transaction=childTxbonus;
						createExpiryTransaction(contactsLoyalty, (long)bonusPoints, (double)bonusAmount, 
								childTxbonus.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
						
						if(bonusTxTier != null){
							String[] diffBonArr = applyConversionRules(contactsLoyalty, bonusTxTier); 
							logger.info("balances After conversion rules updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
							
							String conversionBonRate = null;
							long convertBonPoints = 0;
							double convertBonAmount = 0;
							//double convertBonAmount = 0.0;
							if(diffBonArr != null){
								convertBonAmount = Double.valueOf(diffBonArr[0].trim());
								//convertBonAmount = Double.valueOf(diffBonArr[0].trim()).doubleValue();
								convertBonPoints = Double.valueOf(diffBonArr[1].trim()).longValue();
								conversionBonRate = diffBonArr[2];
							}
							pointsDifference = bonusPoints - convertBonPoints;
							//amountDifference = (long)bonusAmount + convertBonAmount;
							amountDifference = (double)bonusAmount + convertBonAmount;
							bonusTxTier = applyTierUpgradeRule(contactsLoyalty, program, bonusTxTier, autoComm, user, issuanceRequest);
							logger.info(" Earn Type Amount::========");
							logger.info("Pointdiff::"+pointsDifference);
							logger.info("amount diff :"+amountDifference);
							updatePurchaseTransaction(childTxbonus, contactsLoyalty, Constants.STRING_NILL+pointsDifference, Constants.STRING_NILL+amountDifference, conversionBonRate, convertBonAmount, bonusTxTier);
							//updatePurchaseTransaction(childTxbonus, contactsLoyalty, Constants.STRING_NILL+pointsDifference, Constants.STRING_NILL+amountDifference, conversionBonRate, Double.valueOf(diffBonArr[0].trim()).longValue(), bonusTxTier);
						}
					}
				
				//by pravendra
				if(transaction!=null){
				updateTransactionStatus(transaction, OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_PROCESSED);
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			if(bonusflag && autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
					contactsLoyalty.getContact().getContactId() != null){
				Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
				if(contact != null && contact.getEmailId() != null){
					autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
							contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
							contact.getContactId(), contactsLoyalty.getLoyaltyId());
				}
			}
			if (user.isEnableSMS() && bonusflag && autoComm != null && autoComm.getThreshBonusSmsTmpltId() != null){
				Long contactId = null;
				if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
					contactId = contactsLoyalty.getContact().getContactId();
				}
				autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, contactId,
							contactsLoyalty.getLoyaltyId(),contactsLoyalty.getMobilePhone()!=null?contactsLoyalty.getMobilePhone():null);
			   }
		     }
			 }

			}
		}catch(Exception e){
				logger.error("Exception in update threshold bonus...", e);
				//return null;
			}
	}
   private void updateTransactionStatus(LoyaltyTransactionChild transaction, String status) throws Exception {
		LoyaltyTransactionChildDaoForDML LoyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
		transaction.setEarnStatus(status);
		LoyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
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
			 /*if(OCConstants.LOYALTY_LIFETIME_POINTS.equals(tier.getTierUpgdConstraint())){
				
				if(contactsLoyalty.getTotalLoyaltyEarned() != null && tier.getTierUpgdConstraintValue() != null &&
						contactsLoyalty.getTotalLoyaltyEarned() >= tier.getTierUpgdConstraintValue()){
					
					//get the next tier
					LoyaltyProgramTier nextTier = getNextTier(tier);
					if(nextTier != null){
						tier = nextTier;
						tierUpgd = true;
					}
					
					contactsLoyalty.setProgramTierId(tier.getTierId());
					//contactsLoyalty.setProgramTierName(tier.getTierName());
					contactsLoyalty.setTierUpgradedDate(Calendar.getInstance());
					contactsLoyalty.setTierUpgradeReason(OCConstants.LOYALTY_LIFETIME_POINTS);
					
				}
				
			}
			else if (contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null && 
					tier.getTierUpgdConstraint().equals(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE)){
				
				ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);				
				
				List<Map<String, Object>> contactPurcahseList = contactsDao.findContactPurchaseDetails(user.getUserId(), contactsLoyalty.getContact().getContactId());
				Double totPurchaseValue = null;
				if(contactPurcahseList != null && contactPurcahseList.size() == 1) {
					for (Map<String, Object> eachMap : contactPurcahseList) {
						if(eachMap.containsKey("tot_purchase_amt")){
							totPurchaseValue = Double.valueOf(eachMap.get("tot_purchase_amt") != null ? eachMap.get("tot_purchase_amt").toString() : "0.00");
						}
					}
				}
				
				if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){
					logger.info("purchase value is empty...");
					return;
				}
				
				if(totPurchaseValue >= tier.getTierUpgdConstraintValue()){
					
					//get the next tier
					LoyaltyProgramTier nextTier = getNextTier(tier);
					if(nextTier != null){
						tier = nextTier;
						tierUpgd = true;
					}
					
					contactsLoyalty.setProgramTierId(tier.getTierId());
					//contactsLoyalty.setProgramTierName(tier.getTierName());
					contactsLoyalty.setTierUpgradedDate(Calendar.getInstance());
					contactsLoyalty.setTierUpgradeReason(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);
					
				}
				
				
				
			}
			else if(tier.getTierUpgdConstraint().equals(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)){
				
				if(tier.getTierUpgradeCumulativeValue() == null) return;
				
				Double cumulativeAmount = 0.0;
				
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.MONTH, -tier.getTierUpgradeCumulativeValue().intValue());
				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
			
				RetailProSalesDao salesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
				cumulativeAmount = salesDao.findTotalRevenueByCustId(contactsLoyalty.getUserId(), contactsLoyalty.getCustomerId(), startDate, endDate);
				
				if(cumulativeAmount >= 0 && cumulativeAmount >= tier.getTierUpgradeCumulativeValue()){
					
					//get the next tier
					LoyaltyProgramTier nextTier = getNextTier(tier);
					if(nextTier != null){
						tier = nextTier;
						tierUpgd = true;
					}
					contactsLoyalty.setProgramTierId(tier.getTierId());
					//contactsLoyalty.setProgramTierName(tier.getTierName());
					contactsLoyalty.setTierUpgradedDate(Calendar.getInstance());
					contactsLoyalty.setTierUpgradeReason(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE);
					
				}
				
			}*/
			 
			// Send auto communication email
			// LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(program.getProgramId());
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			if(tierUpgd && autoComm != null && autoComm.getTierUpgdEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
					contactsLoyalty.getContact().getContactId() != null){
				Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
				if(contact != null && contact.getEmailId() != null){
					autoCommGen.sendTierUpgdTemplate(autoComm.getTierUpgdEmailTmpltId(), Constants.STRING_NILL+contactsLoyalty.getCardNumber(), 
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
					
			
		//contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);
		}catch(Exception e){
			logger.error("Exception while upgrading tier...", e);
		}
		return currTier;
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
					
					differenceArr[0] = Constants.STRING_NILL+convertedAmount;
					differenceArr[1] = Constants.STRING_NILL+subPoints;
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
	
	private ContactsLoyalty findContactLoyaltyByMobile(String mobile, Users user) throws Exception {
		
		Long userId=user.getUserId();
		String phoneNumber=LoyaltyProgramHelper.preparePhoneNumber(user,mobile);//APP-1208
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByPhone(Long.valueOf(phoneNumber), OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE, userId);
	}
	
	private MembershipResponse prepareAccountIssuanceResponse(ContactsLoyalty contactLoyalty, LoyaltyProgramTier tier,
			LoyaltyProgram program) throws Exception {
		
		MembershipResponse response = new MembershipResponse();
		if(contactLoyalty.getMembershipType().equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)){
			response.setCardNumber(Constants.STRING_NILL+contactLoyalty.getCardNumber());
			response.setCardPin(contactLoyalty.getCardPin());
			response.setPhoneNumber(Constants.STRING_NILL);
		}
		else{
			response.setCardNumber(Constants.STRING_NILL);
			response.setCardPin(Constants.STRING_NILL);
			response.setPhoneNumber(Constants.STRING_NILL+contactLoyalty.getCardNumber());
		}
		if(program.getTierEnableFlag() == OCConstants.FLAG_YES && tier != null){
			response.setTierLevel(tier.getTierType());
			response.setTierName(tier.getTierName());
		}
		else{
			response.setTierLevel(Constants.STRING_NILL);
			response.setTierName(Constants.STRING_NILL);
		}
		
		if(contactLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)){
			if(program.getGiftMembrshpExpiryFlag() == 'Y'){
				response.setExpiry(LoyaltyProgramHelper.getGiftMbrshipExpiryDate(contactLoyalty.getCreatedDate(), 
						program.getGiftMembrshpExpiryDateType(), program.getGiftMembrshpExpiryDateValue()));
			}
			else{
				response.setExpiry(Constants.STRING_NILL);
			}
		}
		else{
			boolean upgdFlag = false;
			if(program.getMbrshipExpiryOnLevelUpgdFlag() == 'Y'){
				upgdFlag = true;
			}
			if(program.getMembershipExpiryFlag() == 'Y' && tier.getMembershipExpiryDateType() != null 
					&& tier.getMembershipExpiryDateValue() != null){
				response.setExpiry(LoyaltyProgramHelper.getMbrshipExpiryDate(contactLoyalty.getCreatedDate(), contactLoyalty.getTierUpgradedDate(), 
						upgdFlag, tier.getMembershipExpiryDateType(), tier.getMembershipExpiryDateValue()));
			}
			else{
				response.setExpiry(Constants.STRING_NILL);
			}
		}
		
		
		return response;
		
	}
	
	private LoyaltyTransactionChild createGiftTransaction(String transId, LoyaltyIssuanceRequest issuanceRequest, ContactsLoyalty loyalty,
			String ptsDiff, String amtDiff){
		
		LoyaltyTransactionChild transaction = null;
		try{
			
			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(transId));
			transaction.setMembershipNumber(Constants.STRING_NILL+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			transaction.setCreatedDate(Calendar.getInstance());
			//transaction.setEnteredAmount(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()));
			transaction.setEnteredAmount(Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()))));
			transaction.setEnteredAmountType(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT);
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);
			
			//transaction.setAmountDifference(amtDiff);
			transaction.setAmountDifference(Utility.truncateUptoTwoDecimal(Double.valueOf(amtDiff)));
			transaction.setEarnType(OCConstants.LOYALTY_TYPE_AMOUNT);
			transaction.setEarnedAmount(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()));
			logger.debug("storeNumber is===>"+issuanceRequest.getHeader().getStoreNumber());
			//if()
			transaction.setStoreNumber(issuanceRequest.getHeader().getStoreNumber() != null && !issuanceRequest.getHeader().getStoreNumber().trim().isEmpty() ? issuanceRequest.getHeader().getStoreNumber() : null);
			transaction.setSubsidiaryNumber(issuanceRequest.getHeader().getSubsidiaryNumber() != null && !issuanceRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? issuanceRequest.getHeader().getSubsidiaryNumber().trim() : null);
			transaction.setReceiptNumber(issuanceRequest.getHeader().getReceiptNumber() != null && !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty() ? issuanceRequest.getHeader().getReceiptNumber() : null);
			
			transaction.setEmployeeId(issuanceRequest.getHeader().getEmployeeId()!=null && !issuanceRequest.getHeader().getEmployeeId().trim().isEmpty() ? issuanceRequest.getHeader().getEmployeeId().trim():null);
			transaction.setTerminalId(issuanceRequest.getHeader().getTerminalId()!=null && !issuanceRequest.getHeader().getTerminalId().trim().isEmpty() ? issuanceRequest.getHeader().getTerminalId().trim():null);
			transaction.setDocSID(issuanceRequest.getHeader().getDocSID());
			//transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE);
			transaction.setSourceType(issuanceRequest.getHeader().getSourceType());
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
//			transaction.setEventTriggStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			
			//Event Trigger sending part
			EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			EventTriggerDao eventTriggerDao  = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
			List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(transaction.getUserId(),Constants.ET_TYPE_ON_GIFT_ISSUANCE);
			
			if(etList != null) {
				eventTriggerEventsObservable.notifyToObserver(etList, transaction.getTransChildId(), transaction.getTransChildId(), 
																transaction.getUserId(), OCConstants.LOYALTY_GIFT_ISSUANCE,Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
			}
			List<EventTrigger> retList = eventTriggerDao.findAllETByUserAndType(transaction.getUserId(),Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
			logger.debug("etList ::"+retList);
			if(retList != null) {
				eventTriggerEventsObservable.notifyToObserver(retList, transaction.getTransChildId(), transaction.getTransChildId(), transaction.getUserId(),
																OCConstants.LOYALTY_ISSUANCE,Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
			}
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		return transaction;
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
	
	

	private String performMobileOptin(Contacts contact, Users currentUser) throws Exception {
		SMSSettingsDao smsSettingsDao  = null;
		UsersDao usersDao = null;
		UsersDaoForDML usersDaoForDML = null;
		ContactsDaoForDML contactsDaoForDML = null;
		try{
			smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
			contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		}catch(Exception e){
			logger.error("Exception in getting smssettingsdao or usersdao", e);
		}
			
			SMSSettings smsSettings = null;
			if(SMSStatusCodes.smsProgramlookupOverUserMap.get(currentUser.getCountryType())) smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
			else  smsSettings = smsSettingsDao.findByOrg(currentUser.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
		
			//SMSSettings smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN );
			
			if(smsSettings == null) {
				contact.setMobileOptin(false);
				return Constants.CON_MOBILE_STATUS_ACTIVE;
				
			}
			Users user = smsSettings.getUserId();
			OCSMSGateway ocsmsGateway = GatewayRequestProcessHelper.getOcSMSGateway(user, 
					SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
			if(ocsmsGateway == null) {
				
				return Constants.CON_MOBILE_STATUS_ACTIVE;
			}
			
			currentUser = smsSettings.getUserId();//to avoid lazy=false from contacts
			//do only when the existing phone number is not same with the entered
			byte optin = 0;
			String phone = contact.getMobilePhone();
			String mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;
			if(contact.getOptinMedium() != null) {
				
				if(contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY) ) {
					optin = 1;
				}
				else if(contact.getOptinMedium().startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM) ) {
					optin = 2;
				}
				else if(contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_POS) ) {
					optin = 4;
				}
			}
			
			Users contactOwner = contact.getUsers();
			Byte userOptinVal =	smsSettings.getOptInMedium();
			
			userOptinVal = ( SMSStatusCodes.userOptinMediumMap.get(contactOwner.getCountryType()) && contactOwner.getOptInMedium() != null) ? 
					contactOwner.getOptInMedium() : userOptinVal;
					CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)ServiceLocator.getInstance().getServiceById(OCConstants.CAPTIWAYTOSMSAPIGATEWAY);
					
			if(smsSettings.isEnable() && 
					userOptinVal != null && 
					(userOptinVal.byteValue() & optin ) > 0 ) {	
				//TODO after the above todo done consider only one among these two conditions on contact
				if(contact.getLastSMSDate() == null && contact.isMobileOptin() != true) {
					
					mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
					contact.setMobileStatus(mobileStatus);
					contact.setLastSMSDate(Calendar.getInstance());
					if(!ocsmsGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocsmsGateway, 1)) {
						
						logger.debug("low credits with clickatell");
						return mobileStatus;
					}
					
					if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() ) ) >=  1) {
						
						String msgContent = smsSettings.getAutoResponse();
						if(msgContent != null) {
							if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType())){
								
								msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
							}
							//msgContent = smsSettings.getMessageHeader() == null ? Constants.STRING_NILL : smsSettings.getMessageHeader() + " "+ msgContent;
						}
						
						mobileStatus = captiwayToSMSApiGateway.sendSingleMobileDoubleOptin(ocsmsGateway, 
								smsSettings.getSenderId(), phone, msgContent, smsSettings.getUserId());
	
						if(mobileStatus == null) {
							
							mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
						}
						
						if(!mobileStatus.equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)) {
							contactsDaoForDML.updatemobileStatus(phone, mobileStatus, currentUser);
						}
						
						/*currentUser.setUsedSmsCount( (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount()) +1);
						usersDao.saveOrUpdate(currentUser);*/
						//usersDao.updateUsedSMSCount(currentUser.getUserId(), 1);
						usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), 1);
						
						/**
						 * Update Sms Queue
						 */
						SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
						smsQueueHelper.updateSMSQueue(phone,msgContent,Constants.SMS_MSG_TYPE_OPTIN, user, smsSettings.getSenderId());
					}else {
						logger.debug("low credits with user...");
						return mobileStatus;
					}
				}//if
			}//if
			
			else{
				
				if(contact.getMobilePhone() != null) {
					mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;
					contact.setMobileStatus(mobileStatus);
				}
			}
			
			return mobileStatus;
	}
	
	
	private Contacts validateAndSavedbContact(Contacts jsonContact, MailingList mlList, Users user) throws Exception {
		
		List<Contacts> dbContactList = findOCContact(jsonContact, user.getUserId(),user);
		Contacts dbContact = null;
		if(dbContactList != null && dbContactList.size() > 0){
			dbContact = dbContactList.get(0);
		}
		PurgeList purgeList = (PurgeList)ServiceLocator.getInstance().getServiceById(OCConstants.PURGELIST);
		boolean updateMLFlag = false;
		boolean isEnableEvent = false;
		boolean isNewContact = false;
		
		if(dbContact == null) {
			logger.info("New Contact...");
			dbContact = jsonContact;
			logger.info("In Validate Contact method dbContact = "+dbContact);
			//dbContact.setEmailId(jsonContact.getEmailId());
			dbContact.setEmailId(validateEmailId(dbContact.getEmailId()));
			dbContact.setMlBits(mlList.getMlBit());
			dbContact.setUsers(user);
			dbContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
			dbContact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
			dbContact.setCreatedDate(Calendar.getInstance());
			
			isNewContact = true;
			updateMLFlag = true;
			isEnableEvent = true;
			
			dbContact.setPurged(false);
			dbContact.setMobilePhone(dbContact.getMobilePhone() == null ? null : Utility.phoneParse(dbContact.getMobilePhone(), user!=null ? user.getUserOrganization() : null ));
			purgeList.checkForValidDomainByEmailId(dbContact);
			validateMobilePhoneStatus(dbContact);
		}
		else {
			logger.info("Existing contact.");
			
			String emailStatus = dbContact.getEmailStatus();
			boolean emailFlag = dbContact.getPurged();
			boolean purgeFlag = false;
			long contactBit = dbContact.getMlBits().longValue();
			if((dbContact.getEmailId() != null && jsonContact.getEmailId() != null &&	! dbContact.getEmailId().equalsIgnoreCase(
					jsonContact.getEmailId())) || (dbContact.getEmailId() == null && jsonContact.getEmailId() != null) ) {
				emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
				emailFlag = false;
				purgeFlag = true;
			}
			if( contactBit == 0l ) {//deleted contact ,need to be triggered action
				dbContact.setMlBits(mlList.getMlBit());
				dbContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
				emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
				emailFlag = false;
				purgeFlag = true;
				isEnableEvent = true;
				updateMLFlag = true;
				dbContact.setUsers(user);
			}
			else{
				if(mlList != null && ( (contactBit & mlList.getMlBit() ) <= 0) ) { //add existing contact to POS if it is not there in it
					dbContact.setMlBits(contactBit | mlList.getMlBit());
					updateMLFlag = true;
				}
			}
			dbContact = Utility.mergeContacts(jsonContact, dbContact);
			//perform mobile optin
			validateMobilePhoneStatus(dbContact);

			dbContact.setEmailStatus(emailStatus);
			dbContact.setPurged(emailFlag);
			if(purgeFlag) {
				purgeList.checkForValidDomainByEmailId(dbContact);
			}
			
			/*if(dbContact.getMlBits().longValue() == 0l ) {//marked as deleted
				
				dbContact.setAddressOne(jsonContact.getAddressOne());
				dbContact.setAddressTwo(jsonContact.getAddressTwo());
				dbContact.setAnniversary(jsonContact.getAnniversary());
				dbContact.setBirthDay(jsonContact.getBirthDay());
				dbContact.setCity(jsonContact.getCity());
				dbContact.setCountry(jsonContact.getCountry());
				dbContact.setEmailId(validateEmailId(dbContact.getEmailId()));
				dbContact.setExternalId(jsonContact.getExternalId());
				dbContact.setFirstName(jsonContact.getFirstName());
				dbContact.setGender(jsonContact.getGender());
				dbContact.setLastName(jsonContact.getLastName());
				dbContact.setMobilePhone(jsonContact.getMobilePhone());
				validateMobilePhoneStatus(dbContact);
				
				isEnableEvent = true;
				updateMLFlag = true;
				
				dbContact.setPurged(false);
				dbContact.setMlBits(mlList.getMlBit());
				dbContact.setUsers(user);
				dbContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
				dbContact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
				purgeList.checkForValidDomainByEmailId(dbContact);
			}
			else{
				long conMlBits = dbContact.getMlBits();
				if(mlList != null && ( (conMlBits & mlList.getMlBit() ) <= 0) ) { //add existing contact to POS if it is not there in it
					dbContact.setMlBits(conMlBits | mlList.getMlBit());
					updateMLFlag = true;
				}
			}*/
		}
		
		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		contactsDaoForDML.saveOrUpdate(dbContact);
		
		MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
		MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
		if(updateMLFlag) {
			
			mlList.setListSize(mlList.getListSize() + 1);
			mlList.setLastModifiedDate(Calendar.getInstance());
			mailingListDaoForDML.saveOrUpdate(mlList);
		}
		
		if(isEnableEvent){
			EventTriggerEventsObservable eventTriggerEventsObservable=(EventTriggerEventsObservable) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver=(EventTriggerEventsObserver) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			eventTriggerEventsObservable.notifyForWebEvents(user.getUserId().longValue(),
					dbContact.getContactId().longValue(), dbContact.getContactId().longValue() );
			
		}
		
		if(isNewContact  && !mlList.getCheckDoubleOptin() && mlList.isCheckWelcomeMsg()) {
			sendWelcomeEmail(dbContact, mlList, user);
		}
		//logger.info("Exited validatedbContact method >>>>>");
		return dbContact;
	}
	
	private String validateEmailId(String emailId){
		//logger.info("Entered validateEmailId method >>>>");
		if(emailId != null) {
			if(Utility.validateEmail(emailId)) {
				//logger.info("Exited validateEmailId method >>>>");
				return emailId;
			}
		}
		//logger.info("Exited validateEmailId method >>>>");
		return null;
	}
	/**
	 * Validates mobilephone and sets appropriate message
	 * 
	 * @param dbContact
	 */
	private void validateMobilePhoneStatus(Contacts dbContact) throws Exception{
		//logger.info("Entered validateMobilePhoneStatus method >>>>");
		if(dbContact.getMobilePhone() != null && dbContact.getMobilePhone().trim().length() > 0) {
			//dbContact.setMobileStatus(performMobileOptin(dbContact, dbContact.getUsers()));
			try {
				Users user = dbContact.getUsers();
				String phoneStr = Utility.phoneParse(dbContact.getMobilePhone().toString().trim(), user!=null ? user.getUserOrganization() : null );
				if(phoneStr != null ) {
					dbContact.setMobilePhone(phoneStr);
					if(dbContact.getUsers().isEnableSMS() && dbContact.getUsers().isConsiderSMSSettings()){
						dbContact.setMobileStatus(performMobileOptin(dbContact, dbContact.getUsers()));
					}else{
						dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
					}
				}else {
					dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				}
			} catch(Exception e) {
				logger.error("Exception in phone parse", e);
			}
		}	
		else{
			dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
			dbContact.setMobileOptin(false);
		}
		//logger.info("Exited validateMobilePhoneStatus method >>>>");
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
	private List<Contacts> findOCContact(Contacts jsonContact, Long userId, Users user) throws Exception {
		//logger.info("Entered findOCContact method >>>>");
		POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(userId, Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
		List<Contacts> dbContactList = contactsDao.findMatchedContactListByUniqPriority(priorMap, jsonContact, userId,user);
		//logger.info("Exited findOCContact method >>>>");
		return dbContactList;
	}
	
	private void sendWelcomeEmail(Contacts contact, MailingList mailingList, Users user) {
		logger.info("Entered sendWelcomeEmail method >>>>>");
		//to send the loyalty related email
		EmailQueueDao emailQueueDao = null;
		EmailQueueDaoForDML emailQueueDaoForDML = null;
		CustomTemplatesDao customTemplatesDao = null;
		try{
		emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
		emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
		customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName("customTemplatesDao");
		}catch(Exception e){
			logger.error("Exception in sending welcome email", e);
			return ;
		}
		CustomTemplates custTemplate = null;
		  String message = PropertyUtil.getPropertyValueFromDB("welcomeMsgTemplate");
		  
		  if(mailingList.getWelcomeCustTempId() != null) {
			   
			  custTemplate = customTemplatesDao.findCustTemplateById(mailingList.getWelcomeCustTempId());
			  if(custTemplate != null) {
			  
				  message = custTemplate.getHtmlText();
			  }
		  }
		  
		  message = message.replace("[OrganisationName]", user.getUserOrganization().getOrganizationName())
				  .replace("[senderReplyToEmailID]", user.getEmailId());
		  
		  EmailQueue testEmailQueue = new EmailQueue(mailingList.getWelcomeCustTempId(),Constants.EQ_TYPE_WELCOME_MAIL, message, "Active",
				  				contact.getEmailId(), user, MyCalendar.getNewCalendar(), "Welcome Mail",
				  				null, contact.getFirstName(), null, contact.getContactId());
				
			//testEmailQueue.setChildEmail(childEmail);
			logger.info("testEmailQueue"+testEmailQueue.getChildEmail());
			
			//emailQueueDao.saveOrUpdate(testEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
		logger.info("Exited sendWelcomeEmail method >>>>>");
		
	}//sendWelcomeEmail
	
	
	private Status checkPromoEmpty(Discounts discount) throws Exception {
	
		Status status = null;
		
		if(discount == null){
			status = new Status("111527", PropertyUtil.getErrorMessage(111527, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(discount.getAppliedPromotion() == null ){
			status = new Status("111527", PropertyUtil.getErrorMessage(111527, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(discount.getAppliedPromotion().equalsIgnoreCase("NA") || discount.getAppliedPromotion().equalsIgnoreCase("N")){
			return null;
		}
		
		if(!discount.getAppliedPromotion().equalsIgnoreCase("Y")){
			status = new Status("111527", PropertyUtil.getErrorMessage(111527, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if(discount.getAppliedPromotion().equalsIgnoreCase("Y") && discount.getPromotions() == null){
			status = new Status("111527", PropertyUtil.getErrorMessage(111527, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		List<Promotion> promoList = discount.getPromotions();
		
		Iterator<Promotion> iterPromo = promoList.iterator();
		Promotion promo = null;
		while(iterPromo.hasNext()){
			promo = iterPromo.next();
			if(promo != null && (promo.getName() == null || promo.getName().trim().isEmpty())){
				status = new Status("111527", PropertyUtil.getErrorMessage(111527, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
		}
		
		return status;
	}
	
	//private LoyaltyTransactionChild createPurchaseTransaction(LoyaltyIssuanceRequest issuanceRequest, Double purchaseAmount, ContactsLoyalty loyalty, double earnedValue, 
	private LoyaltyTransactionChild createPurchaseTransaction(LoyaltyIssuanceRequest issuanceRequest, Double purchaseAmount, ContactsLoyalty loyalty, double earnedAmount, double earnedPoints, 
			String earnType,	 String entAmountType, Long orgId, String ptsDiff, String amtDiff, 
			String transactionId, String earnStatus, String conversionRate, double itemExcludedAmt, double convertAmt, String activationDate){
		
		LoyaltyTransactionChild transaction = null;
		try{
			
			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(Long.valueOf(transactionId));
			transaction.setMembershipNumber(Constants.STRING_NILL+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			
			//transaction.setCreatedDate(Calendar.getInstance());
			if(issuanceRequest.getMembership().getCreatedDate() != null && !issuanceRequest.getMembership().getCreatedDate().trim().isEmpty()){

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
				UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				Users user = usersDao.findMlUser(loyalty.getUserId());
				String timezoneDiffrenceMinutes = user.getClientTimeZone();
				logger.info(timezoneDiffrenceMinutes);
				int timezoneDiffrenceMinutesInt = 0;
				if(timezoneDiffrenceMinutes != null) 
					timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
				timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
				logger.info("Client time to Server Time.."+timezoneDiffrenceMinutesInt);
				cal.add(Calendar.MINUTE,timezoneDiffrenceMinutesInt);
				logger.info("Client time to Server Time Calendar.."+cal);
				transaction.setCreatedDate(cal);
			
			}
			else{
				transaction.setCreatedDate(Calendar.getInstance());
			}
			//transaction.setAmountDifference(amtDiff);
			transaction.setAmountDifference(Utility.truncateUptoTwoDecimal(Double.valueOf(amtDiff)));
			transaction.setPointsDifference(ptsDiff);
			transaction.setEarnType(earnType);
			if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
				//transaction.setEarnedPoints((double)earnedValue);
				transaction.setEarnedPoints((double)earnedPoints);
				//transaction.setNetEarnedPoints((double)earnedValue);
			}
			else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
				//transaction.setEarnedAmount((double)earnedValue);
				transaction.setEarnedAmount((double)earnedAmount);
				//transaction.setNetEarnedAmount((double)earnedValue);
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
			transaction.setSubsidiaryNumber(issuanceRequest.getHeader().getSubsidiaryNumber() != null && !issuanceRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? issuanceRequest.getHeader().getSubsidiaryNumber().trim() : null);
			logger.debug("storeNumber is===>"+issuanceRequest.getHeader().getStoreNumber());               
			transaction.setStoreNumber(issuanceRequest.getHeader().getStoreNumber() != null && !issuanceRequest.getHeader().getStoreNumber().trim().isEmpty() ? issuanceRequest.getHeader().getStoreNumber() : null);
			transaction.setReceiptNumber(issuanceRequest.getHeader().getReceiptNumber() != null && !issuanceRequest.getHeader().getReceiptNumber().trim().isEmpty() ? issuanceRequest.getHeader().getReceiptNumber() : null);
			
		//	transaction.setStoreNumber(issuanceRequest.getHeader().getStoreNumber());
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
			
			//transaction.setRuleType(ruleType);
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
		    //loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			logger.debug("Issuance Transaction Id:::"+transaction.getTransChildId());
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		return transaction;
	}
	
	
	private void updatePurchaseTransaction(LoyaltyTransactionChild transaction, ContactsLoyalty loyalty,
			String ptsDiff, String amtDiff, String conversionRate, double convertAmt, LoyaltyProgramTier tier){
		
		try{
			logger.info("***************inside update trasaction ***************");
			//transaction.setAmountDifference(amtDiff);
			transaction.setAmountDifference(Utility.truncateUptoTwoDecimal(Double.valueOf(amtDiff)));
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
	
	/*private LoyaltyTransactionChild createBonusTransaction(ContactsLoyalty loyalty,
			double earnedValue, String earnType, String bonusRate){*/
	private LoyaltyTransactionChild createBonusTransaction(LoyaltyIssuanceRequest issuanceRequest, ContactsLoyalty loyalty,
			double earnedValue, String earnType, String bonusRate){
		
		LoyaltyTransactionChild transaction = null;
		try{
			
			transaction = new LoyaltyTransactionChild();
			transaction.setMembershipNumber(Constants.STRING_NILL+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			
			//transaction.setCreatedDate(Calendar.getInstance());
			if(issuanceRequest.getMembership().getCreatedDate() != null && !issuanceRequest.getMembership().getCreatedDate().trim().isEmpty()){

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
				UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				Users user = usersDao.findMlUser(loyalty.getUserId());
				String timezoneDiffrenceMinutes = user.getClientTimeZone();
				logger.info(timezoneDiffrenceMinutes);
				int timezoneDiffrenceMinutesInt = 0;
				if(timezoneDiffrenceMinutes != null) 
					timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
				timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
				logger.info("Client time to Server Time.."+timezoneDiffrenceMinutesInt);
				cal.add(Calendar.MINUTE,timezoneDiffrenceMinutesInt);
				logger.info("Client time to Server Time Calendar.."+cal);
				transaction.setCreatedDate(cal);
			
			}
			else{
				transaction.setCreatedDate(Calendar.getInstance());
			}
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
	
	
	private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty,
			Long expiryPoints, Double expiryAmount, Long transChildId, String rewardFlag){
		
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
		return transaction;
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
				/*String result = Utility.truncateUptoTwoDecimal(contactsLoyalty.getGiftcardBalance() + earnedAmount);
				logger.info("GiftCard balance:::"+result);
				contactsLoyalty.setGiftcardBalance(Double.parseDouble(result));*/
				contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + earnedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			if(contactsLoyalty.getTotalGiftcardAmount() == null){
				//contactsLoyalty.setTotalGiftcardAmount(earned);
				contactsLoyalty.setTotalGiftcardAmount(earnedAmount);
			}
			else{
				//contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + earned);y
				/*String res = Utility.truncateUptoTwoDecimal(contactsLoyalty.getTotalGiftcardAmount() + earnedAmount);
				contactsLoyalty.setTotalGiftcardAmount(Double.parseDouble(res));*/
				contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + earnedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			
		}		
			
	}
	
	//private void addEarnValueToHoldBalances(ContactsLoyalty contactsLoyalty, String earnType, double earnValue){
	private void addEarnValueToHoldBalances(ContactsLoyalty contactsLoyalty, String earnType, double earnAmount, double earnedPoints){
		
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
				contactsLoyalty.setHoldAmountBalance(earnAmount);
			}
			else{
				//contactsLoyalty.setHoldAmountBalance(contactsLoyalty.getHoldAmountBalance()+earnValue);
				contactsLoyalty.setHoldAmountBalance((new BigDecimal(contactsLoyalty.getHoldAmountBalance()+earnAmount)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				
			}
		}
		
	}
	
	private HoldBalance prepareHoldBalances(ContactsLoyalty contactsLoyalty, String activationPeriod){
		
		HoldBalance holdBalance = new HoldBalance();
		holdBalance.setActivationPeriod(activationPeriod);
		//holdBalance.setCurrency(contactsLoyalty.getHoldAmountBalance() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contactsLoyalty.getHoldAmountBalance());
		if(contactsLoyalty.getHoldAmountBalance() == null){
			holdBalance.setCurrency(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(contactsLoyalty.getHoldAmountBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			holdBalance.setCurrency(Constants.STRING_NILL+value);
		}
		
		holdBalance.setPoints(contactsLoyalty.getHoldPointsBalance() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contactsLoyalty.getHoldPointsBalance().intValue());
		return holdBalance;
		
	}
	
	private List<ContactsLoyalty> findEnrollListByMobile(String mobile, Long userId) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByMobile(mobile, userId);
	}
	
	private List<MatchedCustomer> prepareMatchedCustomers(List<ContactsLoyalty> enrollList) throws Exception {
		
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
		
		return matchedCustList;
		
	}
	
	private LoyaltyProgram findActiveMobileProgram(Long programId) throws Exception {
		
		 LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		 return loyaltyProgramDao.findById(programId);
	}
	
	
	private LoyaltyIssuanceResponse performGiftCardIssuance(LoyaltyIssuanceRequest issuanceRequest, LoyaltyCards loyaltyCard, 
			ResponseHeader responseHeader, Users user, LoyaltyProgram loyaltyProgram, String mode) throws Exception {
		
		LoyaltyIssuanceResponse issuanceResponse = null;
		Status status = null;
		ContactsLoyalty contactsLoyalty = null;

		if(loyaltyCard.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_CARD_STATUS_ACTIVATED) ||
				loyaltyCard.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_CARD_STATUS_ENROLLED)){
			
			logger.info("Card status actived/enrolled...");
			
			contactsLoyalty = findContactLoyalty(loyaltyCard.getCardNumber(), loyaltyProgram.getProgramId(), user.getUserId());
			
			if(contactsLoyalty == null){
				status = new Status("1000", PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null,status);
				return issuanceResponse;
			}
			
			if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag())) {
				status = new Status("111512", PropertyUtil.getErrorMessage(111512, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				return issuanceResponse;
			}
			
			if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
					contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED) || 
					contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
				
				
				List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
				List<Balance> balances = null;
				
				if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
					contactLoyaltyList.add(contactsLoyalty);
					balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
					String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
					status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
					contactLoyaltyList.add(contactsLoyalty);
					balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
					String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
					status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}else if( contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
					ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactsLoyalty);
					String maskedNum = Constants.STRING_NILL;
					if(destLoyalty != null) {
						contactLoyaltyList.add(destLoyalty);
						contactsLoyalty = destLoyalty;
						balances = prepareBalancesObject(destLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
						maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
						
					}
					String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+maskedNum+".";
					status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				MembershipResponse response = prepareAccountIssuanceResponse(contactsLoyalty, null, loyaltyProgram);
				List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
				issuanceResponse = prepareIssuanceResponse(responseHeader, response, balances, null, matchedCustomers, status);
				return issuanceResponse;
			}
			
			
			if(contactsLoyalty.getGiftBalance() == null ) {
				//contactsLoyalty.setGiftBalance(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()));
				//contactsLoyalty.setGiftBalance((new BigDecimal(issuanceRequest.getAmount().getEnteredValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
				String result = Utility.truncateUptoTwoDecimal(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()));
				contactsLoyalty.setGiftBalance(Double.parseDouble(result));
			}
			else{
				double giftBal = contactsLoyalty.getGiftBalance() + Double.valueOf(issuanceRequest.getAmount().getEnteredValue());
				//contactsLoyalty.setGiftBalance(giftBal);
				/*String result = Utility.truncateUptoTwoDecimal(giftBal);
				contactsLoyalty.setGiftBalance(Double.parseDouble(result));*/
				contactsLoyalty.setGiftBalance(new BigDecimal(giftBal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

			}
			if(contactsLoyalty.getTotalGiftAmount() == null){
				//contactsLoyalty.setTotalGiftAmount(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()));
				contactsLoyalty.setTotalGiftAmount(Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()))));
			}
			else{
				double totgiftBal = contactsLoyalty.getTotalGiftAmount() + Double.valueOf(issuanceRequest.getAmount().getEnteredValue());
				contactsLoyalty.setTotalGiftAmount(new BigDecimal(totgiftBal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			saveContactsLoyalty(contactsLoyalty);
			LoyaltyProgramTier tier = contactsLoyalty.getProgramTierId() != null ? tier = getLoyaltyTier(contactsLoyalty.getProgramTierId()) : null;
			
			MembershipResponse response = prepareAccountIssuanceResponse(contactsLoyalty, tier, loyaltyProgram);
			
			LoyaltyTransactionChild tranx = createGiftTransaction(responseHeader.getTransactionId(), issuanceRequest, contactsLoyalty, "0", issuanceRequest.getAmount().getEnteredValue());
			
			createExpiryTransaction(contactsLoyalty, 0L, Double.valueOf(issuanceRequest.getAmount().getEnteredValue()), 
				tranx.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G);
			
			List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
			contactLoyaltyList.add(contactsLoyalty);
			List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
			
			status = new Status("0", "Issuance was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			List<Balance> balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, issuanceRequest.getAmount().getEnteredValue());
			String activationPeriod = Constants.STRING_NILL;
			if(tier != null && tier.getActivationFlag() == OCConstants.FLAG_YES					
					&& ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
							(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){

				activationPeriod = tier.getPtsActiveDateValue()+" "+tier.getPtsActiveDateType();
			}
			HoldBalance holdBalance = prepareHoldBalances(contactsLoyalty, activationPeriod);
			issuanceResponse = prepareIssuanceResponse(responseHeader, response, balances, holdBalance, matchedCustomers, status);
			return issuanceResponse;
			
		}
		else if(loyaltyCard.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_CARD_STATUS_INVENTORY)){
			
			logger.info("Card status inventory...");
			
			/*contactsLoyalty = prepareContactsLoyaltyObject(loyaltyCard, OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, 
					Constants.CONTACT_LOYALTY_TYPE_POS, issuanceRequest.getHeader().getStoreNumber(), mode, loyaltyProgram, null,
					issuanceRequest.getHeader().getEmployeeId(),issuanceRequest.getHeader().getTerminalId(), issuanceRequest.getHeader().getSourceType());*/
			contactsLoyalty = prepareContactsLoyaltyObject(loyaltyCard, OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, 
					Constants.CONTACT_LOYALTY_TYPE_POS, issuanceRequest.getHeader().getSubsidiaryNumber(), issuanceRequest.getHeader().getStoreNumber(), mode, loyaltyProgram, null,
					issuanceRequest.getHeader().getEmployeeId(),issuanceRequest.getHeader().getTerminalId(), issuanceRequest.getHeader().getSourceType());
			
			MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
			MailingList mlList = mailingListDao.findPOSMailingList(user);
			
			if(mlList != null){
				List<POSMapping> contactPOSMap = null;
				POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
				contactPOSMap = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", user.getUserId());
				
				Contacts jsonContact = new Contacts();
				jsonContact.setUsers(user);

				if(issuanceRequest.getCustomer().getPhone() != null && !issuanceRequest.getCustomer().getPhone().isEmpty()){
					String validStatus = LoyaltyProgramHelper.validateMembershipMobile(issuanceRequest.getCustomer().getPhone().trim());
					if(OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID.equals(validStatus)){
						status = new Status("111554", PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+issuanceRequest.getCustomer().getPhone()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null,status);
						return issuanceResponse;
					}
				}
				
				if(contactPOSMap != null){
					jsonContact = setContactFields(jsonContact, contactPOSMap, issuanceRequest);
				}
				
				if(issuanceRequest.getCustomer().getPhone() != null && !issuanceRequest.getCustomer().getPhone().isEmpty() &&
						(jsonContact.getMobilePhone() == null || jsonContact.getMobilePhone().trim().isEmpty())){
					status = new Status("111554", PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+issuanceRequest.getCustomer().getPhone()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null,status);
					return issuanceResponse;
				}
				issuanceRequest.getCustomer().setPhone(jsonContact.getMobilePhone());
				
//						prepareContactFromJsonData(enrollRequest, user.getUserId(), program);
//				Contacts jsonContact = prepareContactFromJsonData(issuanceRequest.getCustomer(), user.getUserId());
				Contacts dbContact = validateAndSavedbContact(jsonContact, mlList, user);
				
				ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
				dbContact.setLoyaltyCustomer((byte)1);
				contactsDaoForDML.saveOrUpdate(dbContact);
				contactsLoyalty.setContact(dbContact);
				contactsLoyalty.setCustomerId(dbContact.getExternalId());
				if(jsonContact.getMobilePhone() != null){
					contactsLoyalty.setMobilePhone(jsonContact.getMobilePhone());
				}
				else{
					contactsLoyalty.setMobilePhone(dbContact.getMobilePhone());
				}
			}
			
			contactsLoyalty.setUserId(user.getUserId());
			contactsLoyalty.setOrgId(user.getUserOrganization().getUserOrgId());
			//contactsLoyalty.setGiftBalance(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()));
			//contactsLoyalty.setGiftBalance((new BigDecimal(issuanceRequest.getAmount().getEnteredValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
			contactsLoyalty.setGiftBalance(Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()))));
			//contactsLoyalty.setTotalGiftAmount(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()));
			contactsLoyalty.setTotalGiftAmount(Double.parseDouble(Utility.truncateUptoTwoDecimal(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()))));
			contactsLoyalty.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G);
			contactsLoyalty.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
			saveContactsLoyalty(contactsLoyalty);
			
			//update loyalty card status
			updateCardStatus(OCConstants.LOYALTY_CARD_STATUS_ACTIVATED, loyaltyCard);
			
			MembershipResponse response = prepareAccountIssuanceResponse(contactsLoyalty, null, loyaltyProgram);
			
			LoyaltyTransactionChild tranx = createGiftTransaction(responseHeader.getTransactionId(), issuanceRequest, 
					contactsLoyalty, Constants.STRING_NILL, issuanceRequest.getAmount().getEnteredValue());
			
			createExpiryTransaction(contactsLoyalty, 0L, Double.valueOf(issuanceRequest.getAmount().getEnteredValue()), 
				tranx.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G);
			
			//send loyalty threshold alerts...
			LoyaltyProgramHelper.sendLowCardsThresholdAlerts(user, loyaltyProgram, false);

			status = new Status("0", "Issuance was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			
			LoyaltyAutoComm autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			if(autoComm != null && autoComm.getGiftCardIssuanceEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
					contactsLoyalty.getContact().getContactId() != null){
				Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
				if(contact != null && contact.getEmailId() != null){
					autoCommGen.sendGiftIssueTemplate(autoComm.getGiftCardIssuanceEmailTmpltId(), Constants.STRING_NILL+contactsLoyalty.getCardNumber(),
							contactsLoyalty.getCardPin(), user, contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
							contactsLoyalty.getLoyaltyId());
				}
			}
			if(user.isEnableSMS() && autoComm != null && autoComm.getGiftCardIssuanceSmsTmpltId() != null && contactsLoyalty.getMobilePhone() != null){
				//Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
				Long contactId = null;
				if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
					autoCommGen.sendGiftIssueSMSTemplate(autoComm.getGiftCardIssuanceSmsTmpltId(), user, contactsLoyalty.getContact().getContactId(), 
							contactsLoyalty.getLoyaltyId(), contactsLoyalty.getMobilePhone());
				}
			}
			
			List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
			contactLoyaltyList.add(contactsLoyalty);
			List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
			
			
			List<Balance> balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, issuanceRequest.getAmount().getEnteredValue());
			issuanceResponse = prepareIssuanceResponse(responseHeader, response, balances, null, matchedCustomers, status);
			return issuanceResponse;
			
		}
		else{
			status = new Status("111512", PropertyUtil.getErrorMessage(111512, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
			return issuanceResponse;
		}
		
	}
	private ContactsLoyalty getDestMembershipIfAny(ContactsLoyalty contactLoyalty) throws Exception{
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		if(contactLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED) && contactLoyalty.getTransferedTo() != null) {
			return loyaltyDao.findAllByLoyaltyId(contactLoyalty.getTransferedTo());
			
		}
		
		return null;
	}
	private LoyaltyIssuanceResponse cardBasedIssuance(LoyaltyIssuanceRequest issuanceRequest, String mode, 
			String cardNumber, ResponseHeader responseHeader, Users user) throws Exception {
		
		logger.info(">>> Entered Card based issuance");
		LoyaltyIssuanceResponse issuanceResponse = null;
		Status status = null;
		
		/*String programIdStr = null;
		List<LoyaltyProgram> activePrograms = findActiveProgramList(user.getUserId());
		if(activePrograms == null || activePrograms.size() <= 0){
			status = new Status("111503", PropertyUtil.getErrorMessage(111503, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
			return issuanceResponse;
		}
		for(LoyaltyProgram program : activePrograms){
			if(programIdStr == null){
				programIdStr = Constants.STRING_NILL+program.getProgramId();
			}
			else{
				programIdStr += ","+program.getProgramId();
			}
		}
		logger.info("Active program ids : "+programIdStr);
		
		//find all active card sets of the user of type physical and virtual
		String cardSetIdStr = null;
		List<LoyaltyCardSet> activeCardSets = findActiveCardSets(programIdStr);
		if(activeCardSets == null){
			status = new Status("111504", PropertyUtil.getErrorMessage(111504, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
			return issuanceResponse;
		}
		for(LoyaltyCardSet cardSet : activeCardSets){
			if(cardSetIdStr == null){
				cardSetIdStr = Constants.STRING_NILL+cardSet.getCardSetId();
			}
			else{
				cardSetIdStr += ","+cardSet.getCardSetId();
			}
		}
		logger.info("Active cardset ids : "+cardSetIdStr);*/
		
//		LoyaltyProgram loyaltyProgram = null;
		LoyaltyCards loyaltyCard = findLoyaltyCardByUserId(cardNumber, user.getUserId());
		LoyaltyProgram loyaltyProgram = null;
		LoyaltyCardSet loyaltyCardSet = null;
		boolean continueWithDCard = false;
		if(loyaltyCard == null){
			
			// check for DCard based program only when issuance is gift type.
			if(OCConstants.LOYALTY_TYPE_GIFT.equals(issuanceRequest.getAmount().getType().trim()) ) {
				
				LoyaltyProgram dCardBasedProgram = findProgramBy(user.getUserId(), 
						OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE, OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC);
				if(dCardBasedProgram == null){
					
					status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
					return issuanceResponse;
				}
				String validationRule = dCardBasedProgram.getValidationRule();
				if(validationRule != null) {
					 
					cardNumber = OptCultureUtils.validateOCLtyDCardNumber(cardNumber, validationRule);
					if(cardNumber == null){
						String val[] = validationRule.split(Constants.ADDR_COL_DELIMETER);
						String msg = PropertyUtil.getErrorMessage(100108, OCConstants.ERROR_LOYALTY_FLAG)+" "+(val[0].equalsIgnoreCase("N")?"Numeric":"Alphanumeric")+
								" and length should be "+val[1]+(val[0].equalsIgnoreCase("N")?" digits":" characters")+".";
						status = new Status("100108", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
						return issuanceResponse;
					}
					issuanceRequest.getMembership().setCardNumber(cardNumber);
				}
				
				List<LoyaltyCardSet> dCardBasedCardSetList = findCardSetBy(dCardBasedProgram.getProgramId(), OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE, OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC);
				if(dCardBasedCardSetList == null) {
					status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
					return issuanceResponse;
					
				}
				LoyaltyCardSet dCardBasedCardSet = dCardBasedCardSetList.get(0);
				loyaltyCard = insertDCard(cardNumber, null, user, dCardBasedProgram, dCardBasedCardSet);
				if(loyaltyCard == null) {
					String msg = PropertyUtil.getErrorMessage(111584, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".";
					status = new Status("111584", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
					return issuanceResponse;
					
					
				}
				loyaltyProgram = dCardBasedProgram;
				loyaltyCardSet = dCardBasedCardSet;
				continueWithDCard = true;
			}
			else{
				
				status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				return issuanceResponse;
			}
		}
		//do validate card if it is not gift+dcard
		if(!continueWithDCard) {
			String cardLong = null;
			/*if(issuanceRequest.getMembership().getCardNumber().trim().length() != 16) {
				status = new Status("100107", PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				return issuanceResponse;
			}*/
			
			cardLong = OptCultureUtils.validateOCLtyCardNumber(issuanceRequest.getMembership().getCardNumber().trim());
			if(cardLong == null){
				String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+issuanceRequest.getMembership().getCardNumber().trim()+".";
				status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				return issuanceResponse;
			}
			cardNumber = Constants.STRING_NILL+cardLong;
			issuanceRequest.getMembership().setCardNumber(cardNumber);
			
		}
		
		
		if(loyaltyProgram == null)loyaltyProgram = findLoyaltyProgramByProgramId(loyaltyCard.getProgramId(), user.getUserId());
		
		if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(loyaltyProgram.getStatus())){
			status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
			return issuanceResponse;
		}
		
		
		if(loyaltyCardSet == null)loyaltyCardSet = findLoyaltyCardSetByCardsetId(loyaltyCard.getCardSetId(), user.getUserId());
		
		if(loyaltyCardSet == null || !OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE.equals(loyaltyCardSet.getStatus())){
			status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
			return issuanceResponse;
		}
		/*for(LoyaltyProgram program : activePrograms){
			if(program.getProgramId().equals(loyaltyCard.getProgramId())){
				loyaltyProgram = program;
			}
		}*/
		
		if(OCConstants.LOYALTY_TYPE_PURCHASE.equals(issuanceRequest.getAmount().getType().trim()) && 
				issuanceRequest.getAmount().getEnteredValue() != null && 
				!issuanceRequest.getAmount().getEnteredValue().trim().isEmpty() &&
				issuanceRequest.getAmount().getValueCode() != null &&
				issuanceRequest.getAmount().getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY)){
			
			logger.info("Issuance by purchase amount >>>");
			
			if(!OCConstants.LOYALTY_CARD_STATUS_ENROLLED.equals(loyaltyCard.getStatus())){
				status = new Status("111513", PropertyUtil.getErrorMessage(111513, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				return issuanceResponse;
			}
			
			return performIssuanceOperation(issuanceRequest, responseHeader, loyaltyProgram, cardNumber, user,null);
			
		}
		else if (OCConstants.LOYALTY_TYPE_GIFT.equals(issuanceRequest.getAmount().getType().trim()) && 
				issuanceRequest.getAmount().getEnteredValue() != null &&
				!issuanceRequest.getAmount().getEnteredValue().trim().isEmpty() &&
				issuanceRequest.getAmount().getValueCode() != null &&
				issuanceRequest.getAmount().getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY)){
				
			logger.info("Issuance by gift amount >>>");
			
			return performGiftCardIssuance(issuanceRequest, loyaltyCard, responseHeader, user, loyaltyProgram, mode);
			
		}
		else{
			status = new Status("111511", PropertyUtil.getErrorMessage(111511, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
			return issuanceResponse;
		}
		
	}
	
	
	private LoyaltyProgram findProgramBy(Long userId, String status, String programType) throws Exception {
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findProgramByUserId(userId, status, programType);
		
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
	
	private LoyaltyIssuanceResponse mobileBasedIssuance(LoyaltyIssuanceRequest issuanceRequest, ContactsLoyalty contactsLoyalty,
			ResponseHeader responseHeader, String mobileNo, Users user, String mode) throws Exception {
		
		LoyaltyIssuanceResponse issuanceResponse = null;
		Status status = null;
		
		LoyaltyProgram loyaltyProgram = findActiveMobileProgram(contactsLoyalty.getProgramId());
		
		if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equals(loyaltyProgram.getStatus())){
			status = new Status("111522", PropertyUtil.getErrorMessage(111522, OCConstants.ERROR_LOYALTY_FLAG)+" "+mobileNo+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
			return issuanceResponse;
		}
		
		if(OCConstants.LOYALTY_TYPE_PURCHASE.equals(issuanceRequest.getAmount().getType().trim()) && 
				issuanceRequest.getAmount().getEnteredValue() != null && 
				!issuanceRequest.getAmount().getEnteredValue().trim().isEmpty() &&
				issuanceRequest.getAmount().getValueCode() != null &&
				issuanceRequest.getAmount().getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY)){
			
			logger.info("Issuance by purchase amount >>>");
			
			return performIssuanceOperation(issuanceRequest, responseHeader, loyaltyProgram, mobileNo, user,contactsLoyalty);
		}
		else if (OCConstants.LOYALTY_TYPE_GIFT.equals(issuanceRequest.getAmount().getType().trim()) && 
				issuanceRequest.getAmount().getEnteredValue() != null &&
				!issuanceRequest.getAmount().getEnteredValue().trim().isEmpty() &&
				issuanceRequest.getAmount().getValueCode() != null &&
				issuanceRequest.getAmount().getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY)){
			
			status = new Status("111520", PropertyUtil.getErrorMessage(111520, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null,status);
			return issuanceResponse;
		}
		else{
			status = new Status("111521", PropertyUtil.getErrorMessage(111521, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null,status);
			return issuanceResponse;
		}
		
	}
	
	
	private LoyaltyIssuanceResponse performIssuanceOperation(LoyaltyIssuanceRequest issuanceRequest, 
			ResponseHeader responseHeader, LoyaltyProgram loyaltyProgram, String membershipNo, Users user,ContactsLoyalty contactsLoyalty) throws Exception {
		
		
		LoyaltyIssuanceResponse issuanceResponse = null;
		Status status = null;
		
		if(contactsLoyalty==null)
		contactsLoyalty = findContactLoyalty(membershipNo, loyaltyProgram.getProgramId(), user.getUserId());//APP-1208
		
		
		if(contactsLoyalty == null){
			status = new Status("1000", PropertyUtil.getErrorMessage(1000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null,status);
			return issuanceResponse;
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
			MembershipResponse response = prepareAccountIssuanceResponse(contactsLoyalty, tier, loyaltyProgram);
			issuanceResponse = prepareIssuanceResponse(responseHeader, response, balances, null, matchedCustomers, status);
			return issuanceResponse;
		}
		
		status = checkPromoEmpty(issuanceRequest.getDiscounts());
		if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
			return issuanceResponse;
		}
		
		
		LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
		if(loyaltyExclusion != null){
			status = applyLoyaltyExclusions(issuanceRequest, loyaltyProgram, loyaltyExclusion, user.getUserOrganization().getUserOrgId());
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				return issuanceResponse;
			}
		}
		
		status = checkItemsEmpty(issuanceRequest.getItems());
		if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
			return issuanceResponse;
		}
		
		Double itemExcludedAmount = 0.0;
		
		if(loyaltyExclusion == null ||(loyaltyExclusion.getClassStr() == null && loyaltyExclusion.getDcsStr() == null &&
				loyaltyExclusion.getDeptCodeStr() == null && loyaltyExclusion.getItemCatStr() == null &&
				loyaltyExclusion.getSkuNumStr() == null && loyaltyExclusion.getSubClassStr() == null &&
				loyaltyExclusion.getVendorStr() == null)){
		}
		else{
			itemExcludedAmount = calculateItemDiscount(issuanceRequest.getItems(), loyaltyExclusion);
		}
		
		logger.info("itemExcludedAmount = "+itemExcludedAmount);
		
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
					issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
					return issuanceResponse;
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
				
				if(tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
					contactsLoyalty.setProgramTierId(tier.getTierId());
					saveContactsLoyalty(contactsLoyalty);
				}
				else if(tier != null && "Pending".equalsIgnoreCase(tier.getTierType())){
					LoyaltyIssuanceCPVThread cpvThread = new LoyaltyIssuanceCPVThread(eligibleMap, user, contactId,	tierList,
							contactsLoyalty.getLoyaltyId(), loyaltyProgram, issuanceRequest, itemExcludedAmount, responseHeader.getTransactionId());
					Thread th = new Thread(cpvThread);
					th.start();
					
					MembershipResponse response = prepareAccountIssuanceResponse(contactsLoyalty, null, loyaltyProgram);
					List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
					contactLoyaltyList.add(contactsLoyalty);
					List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList);
					//TODO check if balances need to be added
					status = new Status("0", "Issuance will be done shortly.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					issuanceResponse = prepareIssuanceResponse(responseHeader, response, null, null, matchedCustomers, status);
					return issuanceResponse;
				}
			}
		}
		
		LoyaltyAutoComm autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());
		
		//Double purchaseAmountdbl = Double.valueOf(issuanceRequest.getAmount().getEnteredValue());
		Double purchaseAmountdbl = 0.0;
		String purchaseAmountinDec = Utility.truncateUptoTwoDecimal(Double.valueOf(issuanceRequest.getAmount().getEnteredValue()));
		if(purchaseAmountinDec != null)
			purchaseAmountdbl = Double.parseDouble(purchaseAmountinDec);
		Double netPurchaseAmountdbl = purchaseAmountdbl - itemExcludedAmount;
		
		//long netPurchaseAmount = Math.round(netPurchaseAmountdbl);
		double netPurchaseAmount = netPurchaseAmountdbl;
		
		logger.info("netPurchaseAmount = "+netPurchaseAmount);
		
		//long earnedValue = 0;
		double earnedValue = 0.0;
		String earntype = tier.getEarnType();
		long pointsDifference = 0;
		double amountDifference = 0.0;
		
		List<Balance> balances = null;
		HoldBalance holdBalance = null;
		List<String[]> bonusArrList = null;
		
		
		if(tier.getActivationFlag() == 'N'){
			
			Double fromLtyBalance = contactsLoyalty.getTotalLoyaltyEarned();
			Double fromAmtBalance = contactsLoyalty.getTotalGiftcardAmount();
			if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)){
				
				Double multipleFactordbl = netPurchaseAmount/tier.getEarnOnSpentAmount();
				long multipleFactor = multipleFactordbl.intValue();
				//double multipleFactor = multipleFactordbl;
				earnedValue = tier.getEarnValue() * multipleFactor;
			}
			else if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)){
				
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
			
			
			//updateContactLoyaltyBalances((double)earnedValue, tier.getEarnType(), contactsLoyalty);
			updateContactLoyaltyBalances((double)earnedAmount, (double)earnedPoints,  tier.getEarnType(), contactsLoyalty);
			logger.info("balances After earnedValue updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
			
			//LoyaltyTransactionChild childTx = createPurchaseTransaction(issuanceRequest, purchaseAmountdbl, contactsLoyalty, earnedValue, 
			LoyaltyTransactionChild childTx = createPurchaseTransaction(issuanceRequest, purchaseAmountdbl, contactsLoyalty, earnedAmount, earnedPoints, 
					earntype, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE, user.getUserOrganization().getUserOrgId(), 
					Constants.STRING_NILL+pointsDifference, Constants.STRING_NILL+amountDifference, responseHeader.getTransactionId(), 
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
			//amountDifference = earnedAmount + convertAmount;
			amountDifference = earnedAmount + (diffArr != null ? Double.parseDouble(diffArr[0].trim()) : 0.0);
			tier = applyTierUpgradeRule(contactsLoyalty, loyaltyProgram, tier, autoComm, user, issuanceRequest);
			logger.info("balances After tier upgrade --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
			updatePurchaseTransaction(childTx, contactsLoyalty, Constants.STRING_NILL+pointsDifference, Constants.STRING_NILL+amountDifference, conversionRate, convertAmount,tier);
			logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
			
			//Event Trigger sending part
			EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			EventTriggerDao eventTriggerDao  = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
			List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(childTx.getUserId(),Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
			logger.debug("etList ::"+etList);
			if(etList != null) {
				eventTriggerEventsObservable.notifyToObserver(etList, childTx.getTransChildId(), childTx.getTransChildId(), childTx.getUserId(),
																OCConstants.LOYALTY_ISSUANCE,Constants.ET_TYPE_ON_LOYALTY_ISSUANCE);
			}
			List<EventTrigger> retList = eventTriggerDao.findAllETByUserAndType(childTx.getUserId(),Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
			logger.debug("etList ::"+retList);
			if(retList != null) {
				eventTriggerEventsObservable.notifyToObserver(retList, childTx.getTransChildId(), childTx.getTransChildId(), childTx.getUserId(),
																OCConstants.LOYALTY_ISSUANCE,Constants.ET_TYPE_ON_LOYALTY_DIFFERENCE_IN_ISSUANCE);
			}
			balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL+pointsDifference, Constants.STRING_NILL+amountDifference, Constants.STRING_NILL);
			holdBalance = prepareHoldBalances(contactsLoyalty, Constants.STRING_NILL);
			//bonusArrList = calculateThresholdBonus(contactsLoyalty, loyaltyProgram, fromLtyBalance, fromAmtBalance);
			calculateThresholdBonus(contactsLoyalty, loyaltyProgram, fromLtyBalance, fromAmtBalance, tier, autoComm, user, issuanceRequest, pointsDifference, amountDifference);

/*			if(bonusArrList != null && bonusArrList.size() > 0){
				LoyaltyProgramTier bonusTxTier = null;
				if(tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
					bonusTxTier = tier;
				}
				for (String[] bonusArr : bonusArrList) {
					double earnedBonus = 0.0;
					long bonusPts = 0;
					double bonusAmt = 0.0;
					String bonustype =null;
					if(OCConstants.LOYALTY_TYPE_POINTS.equals(bonusArr[0].trim())){
						earnedBonus = Double.valueOf(bonusArr[1].trim()).doubleValue();
						bonusPts = (long)earnedBonus;
						bonustype = OCConstants.LOYALTY_TYPE_POINTS;
					}
					else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(bonusArr[0].trim())){
						earnedBonus = Double.valueOf(bonusArr[1].trim()).doubleValue();
						bonusAmt = earnedBonus;
						bonustype = OCConstants.LOYALTY_TYPE_AMOUNT;
					}
					else{
						logger.info("THIS BONUS IS INVALID.... LOOK INTO THIS...");
					}
					
					LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, earnedBonus, bonustype, bonusArr[2]);
					
					createExpiryTransaction(contactsLoyalty, (long)bonusPts, (double)bonusAmt, 
							childTxbonus.getTransChildId(), OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
					
					if(bonusTxTier != null){
						String[] diffBonArr = applyConversionRules(contactsLoyalty, bonusTxTier); //0 - amountdiff, 1 - pointsdiff
						logger.info("balances After conversion rules updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
						
						String conversionBonRate = null;
						long convertBonPoints = 0;
						long convertBonAmount = 0;
						if(diffBonArr != null){
							convertBonAmount = Double.valueOf(diffBonArr[0].trim()).longValue();
							convertBonPoints = Double.valueOf(diffBonArr[1].trim()).longValue();
							conversionBonRate = diffBonArr[2];
						}
						pointsDifference = bonusPts - convertBonPoints;
						amountDifference = (long)bonusAmt + convertBonAmount;
						bonusTxTier = applyTierUpgradeRule(contactsLoyalty, loyaltyProgram, bonusTxTier, autoComm, user, issuanceRequest);
						updatePurchaseTransaction(childTxbonus, contactsLoyalty, Constants.STRING_NILL+pointsDifference, Constants.STRING_NILL+amountDifference, conversionBonRate, convertBonAmount, bonusTxTier);
					}
				}
				
			}*/
			
		}
		else{
			//create a transaction 
			balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
			
			if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_VALUE)){
				
				Double multipleFactordbl = netPurchaseAmount/tier.getEarnOnSpentAmount();
				//long multipleFactor = multipleFactordbl.intValue();
				double multipleFactor = multipleFactordbl;
				earnedValue = tier.getEarnValue() * multipleFactor;
			}
			else if(tier.getEarnValueType().equals(OCConstants.LOYALTY_TYPE_PERCENTAGE)){
				
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
			//addEarnValueToHoldBalances(contactsLoyalty, tier.getEarnType(), earnedAmount, earnedPoints);
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
					Constants.STRING_NILL+pointsDifference, Constants.STRING_NILL+amountDifference, responseHeader.getTransactionId(), 
					OCConstants.LOYALTY_PROGRAM_TRANS_STATUS_NEW, null, itemExcludedAmount, 0, activationDate);
			String expiryPeriod = Constants.STRING_NILL;
			if(tier != null && tier.getActivationFlag() == OCConstants.FLAG_YES					
				&& ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
					(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){
				
				expiryPeriod = tier.getPtsActiveDateValue()+" "+tier.getPtsActiveDateType();
			}
			holdBalance = prepareHoldBalances(contactsLoyalty, expiryPeriod);
			
		}
		
		List<ContactsLoyalty> loyaltyList  = new ArrayList<ContactsLoyalty>();
		loyaltyList.add(contactsLoyalty);
		
		MembershipResponse response = prepareAccountIssuanceResponse(contactsLoyalty, tier, loyaltyProgram);
		prepareMatchedCustomers(loyaltyList);
		
		//success -- create a transaction in child table
		
		if(issuanceRequest.getAmount().getType().equals(OCConstants.LOYALTY_TYPE_GIFT) && issuanceRequest.getAmount().getEnteredValue() != null 
				&& !issuanceRequest.getAmount().getEnteredValue().trim().isEmpty()){
			status = new Status("0", "Card balance will be updated for new purchase. Purchase and Gift amount "
					+ "can't be clubbed in single issuance request. Please send a separate Gift issuance Request.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		
			issuanceResponse = prepareIssuanceResponse(responseHeader, null, balances, holdBalance, null, status);
			saveContactsLoyalty(contactsLoyalty);
			return issuanceResponse;
		}
		else{
			status = new Status("0", "Issuance was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			
			issuanceResponse = prepareIssuanceResponse(responseHeader, response, balances, holdBalance, prepareMatchedCustomers(loyaltyList), status);
			saveContactsLoyalty(contactsLoyalty);
			return issuanceResponse;
		}
		
	}
	
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
	private LoyaltyProgramTier getNextTier(LoyaltyProgramTier tier) throws Exception {
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		
		String[] type = tier.getTierType().split("\\s+");
		int tierNo = Integer.valueOf(type[1].trim()).intValue();
		int nextTierNo = tierNo + 1;
		String nextTier = "Tier "+nextTierNo;
		
		return tierDao.findTierByType(tier.getProgramId(), nextTier);
		
	}
	
	
	private void deductPointsFromExpiryTable(ContactsLoyalty contactsLoyalty, double subPoints, double earnedAmt) throws Exception{
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; //expiryDao.fetchExpPointsTrans(Constants.STRING_NILL+membershipNumber, 100, userId);
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
	
	private LoyaltyAutoComm getLoyaltyAutoComm(Long programId) throws Exception {
			LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
			return autoCommDao.findById(programId);
	}
	
	private Contacts findContactById(Long cid) throws Exception {
		
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactsDao.findById(cid);
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
			
		}catch(NumberFormatException ne){
			status = new Status("111526", PropertyUtil.getErrorMessage(111526, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		catch(Exception e){
			logger.info("Entered value validation failed...");
		}
		logger.info("Completed validateEnteredValue method <<<");
		return status;
		
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
			totPurchaseValue =  LoyaltyProgramHelper.getLPV(contactsLoyalty);//contactsLoyalty.getLifeTimePurchaseValue() == null ? 0.00 : contactsLoyalty.getLifeTimePurchaseValue();//Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
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
			
			//create a temp object and return it to caller. caller should handle this temp object.
			
			LoyaltyProgramTier tempTier = new LoyaltyProgramTier();
			tempTier.setTierType("Pending");
			return tempTier;			
		}
		else{
			return null;
		}

	}
	
	private void saveContactsLoyalty(ContactsLoyalty contactsLoyalty) throws Exception {
		ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		loyaltyDao.saveOrUpdate(contactsLoyalty);
	}
	
	private Contacts setContactFields(Contacts inputContact,List<POSMapping> contactPOSMap, LoyaltyIssuanceRequest issuanceRequest)throws BaseServiceException {
		Class strArg[] = new Class[] { String.class };
		Class calArg[] = new Class[] { Calendar.class };

		logger.debug("-------entered setContactFields---------");
		for(POSMapping posMapping : contactPOSMap){

			String custFieldAttribute = posMapping.getCustomFieldName();
			String fieldValue=getFieldValue(issuanceRequest,posMapping);
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
					String phoneParse = Utility.phoneParse(fieldValue, user!=null ? user.getUserOrganization() : null );
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

	private String getFieldValue(LoyaltyIssuanceRequest issuanceRequest,POSMapping posMapping)throws BaseServiceException {
		String fieldValue=null;
		logger.debug("-------entered getFieldValue---------");
		if(posMapping.getCustomFieldName().equalsIgnoreCase("street")) {
			fieldValue = issuanceRequest.getCustomer().getAddressLine1();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("address two")) {
			fieldValue = issuanceRequest.getCustomer().getAddressLine2();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("email")) {
			fieldValue = issuanceRequest.getCustomer().getEmailAddress();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("mobile")) {
			fieldValue = issuanceRequest.getCustomer().getPhone();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("first name")) {
			fieldValue = issuanceRequest.getCustomer().getFirstName();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("last name")) {
			fieldValue = issuanceRequest.getCustomer().getLastName();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("city")) {
			fieldValue = issuanceRequest.getCustomer().getCity();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("state")) {
			fieldValue = issuanceRequest.getCustomer().getState();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("country")) {
			fieldValue = issuanceRequest.getCustomer().getCountry();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("zip")) {
			fieldValue = issuanceRequest.getCustomer().getPostal();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("gender")) {
			fieldValue = issuanceRequest.getCustomer().getGender();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("customer id")) {
			fieldValue = issuanceRequest.getCustomer().getCustomerId();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("birthday")) {
			fieldValue = issuanceRequest.getCustomer().getBirthday();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("anniversary")) {
			fieldValue = issuanceRequest.getCustomer().getAnniversary();
			return fieldValue;
		}
		logger.debug("-------exit  getFieldValue---------");
		return fieldValue;
	}//getFieldValue
	//w.r.t migration SBTOOC
private ContactsLoyalty findLoyaltyCardInOCByCustId(String customerId, Long userId) throws Exception {
		
		ContactsLoyalty contactLoyalty = null;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		List<ContactsLoyalty> loyaltyList = contactsLoyaltyDao.getLoyaltyListByCustId(customerId, userId);
		
		if(loyaltyList != null && loyaltyList.size() > 0){
			Iterator<ContactsLoyalty> iterList = loyaltyList.iterator();
			ContactsLoyalty latestLoyalty = null;
			ContactsLoyalty iterLoyalty = null;
			while(iterList.hasNext()){
				iterLoyalty = iterList.next();
				if(latestLoyalty != null && latestLoyalty.getCreatedDate().after(iterLoyalty.getCreatedDate())){
					continue;
				}
				latestLoyalty = iterLoyalty;
			}
			
			contactLoyalty = latestLoyalty;
		}
		
		return contactLoyalty;
	}
	
	
}









