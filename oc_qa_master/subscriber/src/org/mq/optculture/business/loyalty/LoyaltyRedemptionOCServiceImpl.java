package org.mq.optculture.business.loyalty;

import java.math.BigDecimal;
import java.util.Date;
import java.text.DateFormat;
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
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.OTPGeneratedCodes;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.controller.service.ExternalSMTPDigiReceiptSender;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDao;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDaoForDML;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramExclusionDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.data.dao.OTPGeneratedCodesDao;
import org.mq.optculture.data.dao.OTPGeneratedCodesDaoForDML;
import org.mq.optculture.data.dao.ValueCodesDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.AdditionalInfo;
import org.mq.optculture.model.ocloyalty.OTPRedeemLimit;
import org.mq.optculture.model.ocloyalty.Amount;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.Discounts;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionResponse;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.Promotion;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

/**
 * == OC Loyalty Program == Loyalty redemption handler  
 *
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyRedemptionOCServiceImpl implements LoyaltyRedemptionOCService{
	
	

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private String loyaltyExtraction;
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
		LoyaltyRedemptionResponse redemptionResponse = null;
		LoyaltyRedemptionRequest redemptionRequest = null;
		BaseResponseObject responseObject = null;
		String responseJson = null;
		
		if(requestJson == null || serviceRequest == null || !serviceRequest.equals(OCConstants.LOYALTY_SERVICE_ACTION_REDEMPTION)){
			redemptionResponse = new LoyaltyRedemptionResponse();
			
			Status status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			redemptionResponse.setStatus(status);
			
			//Convert Object to JSON string
			responseJson = gson.toJson(redemptionResponse);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}
		
		try{
			
			redemptionRequest = gson.fromJson(requestJson, LoyaltyRedemptionRequest.class);
			
		}catch(Exception e){
			
			Status status = new Status("101001", Constants.STRING_NILL+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			redemptionResponse = new LoyaltyRedemptionResponse();
			redemptionResponse.setStatus(status);
			responseJson = gson.toJson(redemptionResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid JSON ");
			return responseObject;
		}
		
		try{
			logger.info("POS issuance request...online mode ...");
			LoyaltyRedemptionOCService loyaltyRedemptionService = (LoyaltyRedemptionOCService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_REDEMPTION_OC_BUSINESS_SERVICE);
			redemptionResponse = loyaltyRedemptionService.processRedemptionRequest(redemptionRequest, OCConstants.LOYALTY_ONLINE_MODE, 
					baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate(),null);
				responseJson = gson.toJson(redemptionResponse);
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
	 * Handles the complete process of Loyalty Redemption for either points or amount(USD).
	 * @return LoyaltyRedemptionResponse
	 */
	@Override
	public LoyaltyRedemptionResponse processRedemptionRequest(LoyaltyRedemptionRequest redemptionRequest, String mode, 
			String transactionId, String transactionDate,String loyaltyExtraction)
			throws BaseServiceException {
		
		LoyaltyRedemptionResponse redemptionResponse = null;
		Status status = null;
		Users user = null;
		this.loyaltyExtraction=loyaltyExtraction;
		
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(redemptionRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(redemptionRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		responseHeader.setSourceType(redemptionRequest.getHeader().getSourceType() != null && !redemptionRequest.getHeader().getSourceType().trim().isEmpty() ? redemptionRequest.getHeader().getSourceType().trim() : Constants.STRING_NILL);
		try{
			status = validateRedemptionJsonData(redemptionRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				return redemptionResponse;
			}
			String type = redemptionRequest.getAmount().getType()!=null ? redemptionRequest.getAmount().getType() : "";//APP-4766
			status = validateEnteredValue(redemptionRequest.getAmount(),type);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				return redemptionResponse;
			}
			
			user = getUser(redemptionRequest.getUser().getUserName(), redemptionRequest.getUser().getOrganizationId(),
					redemptionRequest.getUser().getToken());
			if(user == null){
				status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				return redemptionResponse;
			}
			if(!user.isEnabled()){
				status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				return redemptionResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				return redemptionResponse;
			}
			if(user.isEnableLoyaltyExtraction() && user.isRedemptionFromDR() && loyaltyExtraction == null && user.isIgnoretrxUpOnExtraction()) {
				status = new Status("0", "Redemption will be done shortly.", OCConstants.JSON_RESPONSE_IGNORED_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null,null, status);
				return redemptionResponse;
			}
			//if the user has loyalty type oc(changed during migrationsbtooc)
			if(OCConstants.LOYALTY_SERVICE_TYPE_OC.equals(user.getloyaltyServicetype())) {
				
				
				if(redemptionRequest.getHeader().getDocSID() == null || redemptionRequest.getHeader().getDocSID().trim().isEmpty()){
					status = new Status("111510", PropertyUtil.getErrorMessage(111510, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				}
				if(redemptionRequest.getHeader().getStoreNumber() == null || redemptionRequest.getHeader().getStoreNumber().length() <= 0){
					status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				}
				//updating subsidiary to request	
				if((redemptionRequest.getHeader().getSubsidiaryNumber() == null || 
						(redemptionRequest.getHeader().getSubsidiaryNumber().isEmpty()) ) && redemptionRequest.getHeader().getStoreNumber() != null && !redemptionRequest.getHeader().getStoreNumber().isEmpty()){
					OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
					UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
					Long domainId = userDao.findDomainByUserId(user.getUserId());
					if(domainId!=null){
					OrganizationStores orgStores = organizationStoresDao.findOrgByDomain(user.getUserOrganization().getUserOrgId(), domainId, redemptionRequest.getHeader().getStoreNumber());
					redemptionRequest.getHeader().setSubsidiaryNumber(orgStores!=null ? orgStores.getSubsidiaryId() : null);
				 }
				}
				
			}
			
			
			LoyaltyCards loyaltyCard = null;
			
			if(redemptionRequest.getMembership().getCardNumber() != null && redemptionRequest.getMembership().getCardNumber().length() > 0){
				
				logger.info("redemption by card number >>>");
				String cardNumber = Constants.STRING_NILL;
				String cardLong = null;
				
				cardLong = OptCultureUtils.validateOCLtyCardNumber(redemptionRequest.getMembership().getCardNumber().trim());
				if(cardLong == null){
					String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+redemptionRequest.getMembership().getCardNumber().trim()+".";
					status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				}
				cardNumber = Constants.STRING_NILL+cardLong;
				redemptionRequest.getMembership().setCardNumber(cardNumber);
				
				loyaltyCard = findLoyaltyCardByUserId(cardNumber, user.getUserId());
				
				if(loyaltyCard == null){
					status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				}
				
				if(OCConstants.LOYALTY_CARD_STATUS_INVENTORY.equalsIgnoreCase(loyaltyCard.getStatus())){
					status = new Status("111537", PropertyUtil.getErrorMessage(111537, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				}
				
				return cardBasedRedemption(redemptionRequest, loyaltyCard, responseHeader, user);
				
			}
			else if(redemptionRequest.getMembership().getPhoneNumber() != null 
					&& redemptionRequest.getMembership().getPhoneNumber().trim().length() > 0){
				
				String validStatus = LoyaltyProgramHelper.validateMembershipMobile(redemptionRequest.getMembership().getPhoneNumber().trim());
				if(OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID.equals(validStatus)){
					String msg = PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+redemptionRequest.getMembership().getPhoneNumber().trim();
					status = new Status("111554", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				}
				
				String phoneNumber=redemptionRequest.getMembership().getPhoneNumber().trim();
				ContactsLoyalty contactsLoyalty = findContactLoyaltyByMobile(phoneNumber, user);
				
				if(contactsLoyalty == null){
					String msg = PropertyUtil.getErrorMessage(111519, OCConstants.ERROR_LOYALTY_FLAG)+" "+redemptionRequest.getMembership().getPhoneNumber().trim();
					status = new Status("111519", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				}
				
				return mobileBasedRedemption(redemptionRequest, contactsLoyalty, user, responseHeader, loyaltyCard);
				
			}
			else if(redemptionRequest.getCustomer().getPhone() != null 
					&& !redemptionRequest.getCustomer().getPhone().trim().isEmpty()){
				
				List<ContactsLoyalty> enrollList = findEnrollListByMobile(redemptionRequest.getCustomer().getPhone(), user);
				
				if(enrollList == null){
					status = new Status("111524", PropertyUtil.getErrorMessage(111524, OCConstants.ERROR_LOYALTY_FLAG)+" "+redemptionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				}
				
				ContactsLoyalty contactsLoyalty = null;
				List<Contacts> dbContactList = null;
				Contacts dbContact = null;
				
				if(enrollList.size() > 1){
					logger.info("Found more than 1 enrollments");
					Contacts jsonContact = prepareContactFromJsonData(redemptionRequest.getCustomer(), user.getUserId());
					jsonContact.setUsers(user);
					dbContactList = findOCContact(jsonContact, user.getUserId(), user);
					
					if(dbContactList == null){
						logger.info(" request contact not found in OC");
						
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);
						
						status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+redemptionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, matchedCustomers, status);
						return redemptionResponse;
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
							
							status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+redemptionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, matchedCustomers, status);
							return redemptionResponse;
						}
					}
					else{
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList);

						status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+redemptionRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, matchedCustomers, status);
						return redemptionResponse;
					}
					
				}
				else{
					logger.info("loyalty found in else case....");
					contactsLoyalty = enrollList.get(0);
				}
				logger.info("contactsLoyalty = "+contactsLoyalty);
				
				if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(contactsLoyalty.getMembershipType())){
					
					return mobileBasedRedemption(redemptionRequest, contactsLoyalty, user, responseHeader, loyaltyCard);
				}
				else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
					
					loyaltyCard = findLoyaltyCardByUserId(Constants.STRING_NILL+contactsLoyalty.getCardNumber(), user.getUserId());
					return cardBasedRedemption(redemptionRequest, loyaltyCard, responseHeader, user);
				}
				else{
					logger.info("INVALID LOYALTY MEMBERSHIP CARD TYPE .... LOOKINTO THIS...");
				}
				
			}
			else{
				status = new Status("111523", PropertyUtil.getErrorMessage(111523, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				return redemptionResponse;
			}

			
		}catch(Exception e){
			logger.error("Exception in loyalty Redemption service", e);
			throw new BaseServiceException("Loyalty Redemption Request Failed");
		}
		return redemptionResponse;
	}
	/**
	 * Validates all JSON Request parameters
	 * @param LoyaltyIssuanceRequestObject
	 * @return StatusInfo
	 * @throws Exception
	 */
	private Status validateRedemptionJsonData(LoyaltyRedemptionRequest redemptionRequest) throws Exception{
		
		Status status = null;
		
		if(redemptionRequest == null ){
			status = new Status(
					"101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(redemptionRequest.getUser() == null){
			status = new Status(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(redemptionRequest.getMembership() == null){
			status = new Status(
					"101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(redemptionRequest.getAmount() == null){
			status = new Status(
					"111534", PropertyUtil.getErrorMessage(111534, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(redemptionRequest.getUser().getUserName() == null || redemptionRequest.getUser().getUserName().trim().length() <=0 || 
				redemptionRequest.getUser().getOrganizationId() == null || redemptionRequest.getUser().getOrganizationId().trim().length() <=0 || 
				redemptionRequest.getUser().getToken() == null || redemptionRequest.getUser().getToken().trim().length() <=0) {
			status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if(redemptionRequest.getAmount().getEnteredValue() == null || redemptionRequest.getAmount().getEnteredValue().trim().isEmpty() ||
				redemptionRequest.getAmount().getValueCode() == null || redemptionRequest.getAmount().getValueCode().trim().isEmpty()) {
			status = new Status("111526", PropertyUtil.getErrorMessage(111526, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(redemptionRequest.getAmount().getValueCode().equals(OCConstants.LOYALTY_TYPE_POINTS) && 
				redemptionRequest.getAmount().getEnteredValue() != null && 
				!redemptionRequest.getAmount().getEnteredValue().trim().isEmpty()){   try{
				    int enteredValue = 0;
				    Double enteredValueDouble = Double.parseDouble(redemptionRequest.getAmount().getEnteredValue());
				    if(enteredValueDouble != null) {
				     
				     enteredValue = enteredValueDouble.intValue();
				    }
				   }catch(Exception e){
				    status = new Status("111526", PropertyUtil.getErrorMessage(111526, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				    return status;
				   }}
		Double enteredValue;
		if(redemptionRequest.getAmount().getValueCode().equals(OCConstants.LOYALTY_TYPE_CURRENCY) && 
				redemptionRequest.getAmount().getEnteredValue() != null && 
				!redemptionRequest.getAmount().getEnteredValue().trim().isEmpty()){
			try{
				enteredValue = Double.valueOf(redemptionRequest.getAmount().getEnteredValue());
			}catch(Exception e){
				logger.info("error===>"+e);
				status = new Status("111526", PropertyUtil.getErrorMessage(111526, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			
		}
		
		return status;
	}
		
	/**
	 * Saves contacts loyalty object in OptCulture database.
	 * 
	 * @param contactsLoyalty
	 * @param redemptionStatus
	 * @throws Exception
	 */
	private void saveContactsLoyalty(ContactsLoyalty contactsLoyalty) throws Exception {
		ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		loyaltyDao.saveOrUpdate(contactsLoyalty);
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
	
	
	private LoyaltyRedemptionResponse prepareRedemptionResponse(ResponseHeader header, MembershipResponse accountResponse,
			List<Balance> balances, HoldBalance holdBalance, AdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyRedemptionResponse redemptionResponse = new LoyaltyRedemptionResponse();
		redemptionResponse.setHeader(header);
		
		if(accountResponse == null){
			accountResponse = new MembershipResponse();
			accountResponse.setCardNumber(Constants.STRING_NILL);
			accountResponse.setCardPin(Constants.STRING_NILL);
			accountResponse.setExpiry(Constants.STRING_NILL);
			accountResponse.setPhoneNumber(Constants.STRING_NILL);
			accountResponse.setTierLevel(Constants.STRING_NILL);
			accountResponse.setTierName(Constants.STRING_NILL);
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
			additionalInfo = new AdditionalInfo();
			additionalInfo.setOtpEnabled(Constants.STRING_NILL);
			List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
			additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
			additionalInfo.setPointsEquivalentCurrency(Constants.STRING_NILL);
			additionalInfo.setTotalRedeemableCurrency(Constants.STRING_NILL);
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		
		redemptionResponse.setMembership(accountResponse);
		redemptionResponse.setBalances(balances);
		redemptionResponse.setHoldBalance(holdBalance);
		redemptionResponse.setAdditionalInfo(additionalInfo);
		redemptionResponse.setMatchedCustomers(matchedCustomers);
		redemptionResponse.setStatus(status);
		return redemptionResponse;
	}
	
	
	private LoyaltyCards findLoyaltyCardByUserId(String cardNumber, Long userId) throws Exception {
		
		LoyaltyCardsDao loyaltyCardDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return loyaltyCardDao.findByCardNoAnduserId(cardNumber, userId);
		
	}
	
	private List<LoyaltyProgram> findActiveProgramList(Long userId) throws Exception {
		
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findProgramsBy(userId, OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE, OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD);
	}
	
	private List<LoyaltyCardSet> findActiveCardSets(String programIdStr) throws Exception {
		
		LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		return loyaltyCardSetDao.findByProgramIdStr(programIdStr, OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE);
	}
	
	private ContactsLoyalty findContactLoyalty(String cardNumber, Long programId, Long userId) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findByProgram(cardNumber, programId, userId);
	}
	
	private LoyaltyCards findCardByCardNumber(String programIdStr, String cardSetIdStr, String cardNumber, Long userId) throws Exception {
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return loyaltyCardsDao.findCardByProgram(programIdStr, cardSetIdStr, cardNumber, userId);
	}
	
	private LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getTierById(tierId);
		
	}
	
	private ContactsLoyalty findContactLoyaltyByMobile(String mobile, Users user) throws Exception {
		
		Long userId=user.getUserId();
		String phoneNumber=LoyaltyProgramHelper.preparePhoneNumber(user,mobile);//APP-1208
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByPhone(Long.valueOf(phoneNumber), OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE, userId);
	}
	
	private LoyaltyProgram findMobileBasedProgram(Long userId) throws Exception {
		
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findMobileBasedProgram(userId);
	}
	
	private Contacts findCustomerByCid(Long cid, Long userId) throws Exception {
		
		ContactsDao contactDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactDao.findById(cid);
	}
	
	private Customer prepareCustomer(Contacts contact) throws Exception {
		
		Customer customer = new Customer();
		
		customer.setAddressLine1(contact.getAddressOne() == null ? Constants.STRING_NILL : contact.getAddressOne());
		customer.setAddressLine2(contact.getAddressTwo() == null ? Constants.STRING_NILL : contact.getAddressTwo());
		customer.setAnniversary(contact.getAnniversary() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contact.getAnniversary());
		customer.setBirthday(contact.getBirthDay() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contact.getBirthDay());
		customer.setCity(contact.getCity() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contact.getCity());
		customer.setCountry(contact.getCountry() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contact.getCountry());
		customer.setCustomerId(contact.getExternalId() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contact.getExternalId());
		customer.setEmailAddress(contact.getEmailId() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contact.getEmailId());
		customer.setFirstName(contact.getFirstName() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contact.getFirstName());
		customer.setGender(contact.getGender() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contact.getGender());
		customer.setLastName(contact.getLastName() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contact.getLastName());
		customer.setPhone(contact.getMobilePhone() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contact.getMobilePhone());
		customer.setPostal(contact.getZip() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contact.getZip());
		customer.setState(contact.getState() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contact.getState());
		
		return customer;
		
	}
	
	private LoyaltyProgram findActiveMobileProgram(Long programId) throws Exception {
		
	 LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
	 return loyaltyProgramDao.findById(programId);
	}
	
	private List<ContactsLoyalty> findEnrollListByMobile(String mobile, Users user) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByEmailORPhone(null,mobile, user);
	}
private List<ContactsLoyalty> findEnrollListByEmailORPhone(String Email,String phone, Users user) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByEmailORPhone(Email, phone, user);
	
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
	
	private List<Contacts> findOCContact(Contacts jsonContact, Long userId, Users user) throws Exception {
		POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(userId, Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
		List<Contacts> dbContact = contactsDao.findMatchedContactListByUniqPriority(priorMap, jsonContact, userId, user);
		return dbContact;
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
					matchedCustList.add(matchedCustomer);
				}
			}
		}
		
		return matchedCustList;
		
	}
	
	private List<MatchedCustomer> prepareSuccessMatchedCustomers(List<ContactsLoyalty> enrollList) throws Exception {
		
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
					matchedCustomer.setCustomerId(contact.getExternalId() == null ? Constants.STRING_NILL : contact.getExternalId().trim());
					matchedCustomer.setEmailAddress(contact.getEmailId() == null ? Constants.STRING_NILL : contact.getEmailId().trim());
					matchedCustomer.setPhone(contact.getMobilePhone() == null ? Constants.STRING_NILL : contact.getMobilePhone().trim());
					matchedCustList.add(matchedCustomer);
				}
			}
		}
		
		return matchedCustList;
		
	}
	
	private List<Balance> prepareBalancesObject(ContactsLoyalty loyalty, String pointsDifference, 
			String amountDifference, String giftDifference) throws Exception{
		
		List<Balance> balancesList = null;
		Balance pointBalances = null;
		Balance amountBalances = null;
		Balance giftBalances = null;
		
		balancesList = new ArrayList<Balance>();
		
		pointBalances = new Balance();
		pointBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		pointBalances.setValueCode(OCConstants.LOYALTY_TYPE_POINTS);
		pointBalances.setAmount(loyalty.getLoyaltyBalance() == null ? Constants.STRING_NILL : Constants.STRING_NILL+loyalty.getLoyaltyBalance().intValue());
		pointBalances.setDifference(pointsDifference);
		
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
		if(amountDifference == null || amountDifference == Constants.STRING_NILL){
			amountBalances.setDifference(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(Double.valueOf(amountDifference)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
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
		if(giftDifference == null || giftDifference == Constants.STRING_NILL){
			giftBalances.setDifference(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(Double.valueOf(giftDifference)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			giftBalances.setDifference(Constants.STRING_NILL+value);
		}		
		balancesList.add(pointBalances);
		balancesList.add(amountBalances);
		balancesList.add(giftBalances);
		
		return balancesList;
	}
	
	private Status validatePromocodeExclusion(LoyaltyRedemptionRequest redemptionRequest, 
			LoyaltyProgramExclusion loyaltyExclusion, Long orgId) throws Exception {
		
		
		Status status = null;
		
		if(redemptionRequest.getDiscounts().getAppliedPromotion().equalsIgnoreCase("Y") && loyaltyExclusion.getRedemptionWithPromoFlag() == 'Y'){
			
			List<Promotion> promoList = redemptionRequest.getDiscounts().getPromotions();
			List<String> promoNames = null;
			
				if(loyaltyExclusion.getRedemptionPromoIdStr() != null){
					
					String[] promoIdArr = loyaltyExclusion.getRedemptionPromoIdStr().split(";=;");
					if(promoIdArr.length == 1 && promoIdArr[0].equalsIgnoreCase(OCConstants.LOYALTY_PROMO_EXCLUSION_ALL)){
						promoNames = findPromoNames(orgId, null);
					}
					else{
						String promoIdStr = Constants.STRING_NILL;
						for(String promoId : promoIdArr){
								promoIdStr += (promoIdStr == Constants.STRING_NILL) ? "'"+promoId+"'" : ",'"+promoId+"'";
								logger.info("promoIdStr = "+promoIdStr);
						}
						promoNames = findPromoNames(orgId, promoIdStr);
					}
					
					if(promoNames != null){
						for(Promotion promotion : promoList){
							if(promoNames.contains(promotion.getName())){
								status = new Status("101406", PropertyUtil.getErrorMessage(101406, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								return status;
							}
						}//for
					}//if
				}
		}
		return status;
	}
	
    private Status validateSpecialDateExclusion(LoyaltyRedemptionRequest redemptionRequest, LoyaltyProgramExclusion exclusion) throws Exception {
		
		Status status = null;
		
		if(exclusion.getExclRedemDateStr() != null){
			
			String requestDate = redemptionRequest.getHeader().getRequestDate();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(sdf1.parse(requestDate));
			
			//SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
			String[] dateArr = null;
			dateArr = exclusion.getExclRedemDateStr().split(";=;");
			
				for(String dateStr : dateArr){
					if(!dateStr.contains(",")) {
						if(dateStr.length()==3) {
							SimpleDateFormat sdf = new SimpleDateFormat("MMM");
							Calendar cal = Calendar.getInstance();
							cal.setTime(sdf.parse(dateStr));
						
							if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH))){
							status = new Status("111620", PropertyUtil.getErrorMessage(111620, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							return status;
							}
						} else if(dateStr.length()==4) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
							Calendar cal = Calendar.getInstance();
							cal.setTime(sdf.parse(dateStr));
						
							if(	(cal.get(Calendar.YEAR) == cal1.get(Calendar.YEAR))){
							status = new Status("111621", PropertyUtil.getErrorMessage(111621, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							return status;
							}
						} else if(dateStr.contains("-")) {
							SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
							Calendar cal = Calendar.getInstance();
							cal.setTime(sdf.parse(dateStr));
						
							if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH)) &&
								(cal.get(Calendar.YEAR) == cal1.get(Calendar.YEAR))){
							status = new Status("111620", PropertyUtil.getErrorMessage(111620, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							return status;
							}
						} else {
							SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
							Calendar cal = Calendar.getInstance();
							cal.setTime(sdf.parse(dateStr));
						
							if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH)) &&
								(cal.get(Calendar.DAY_OF_MONTH) == cal1.get(Calendar.DAY_OF_MONTH))){
							status = new Status("111619", PropertyUtil.getErrorMessage(111619, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							return status;
							}
						}
					} else {
						SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
						Calendar cal = Calendar.getInstance();
						cal.setTime(sdf.parse(dateStr));
					
						if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH)) &&
							(cal.get(Calendar.DAY_OF_MONTH) == cal1.get(Calendar.DAY_OF_MONTH)) && (cal.get(Calendar.YEAR) == cal1.get(Calendar.YEAR))){
						status = new Status("111619", PropertyUtil.getErrorMessage(111619, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
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
		
		if(discount.getAppliedPromotion().equalsIgnoreCase("Y")){
			
		}
		else if(discount.getAppliedPromotion().equalsIgnoreCase("N")){
			
		}
		else if(discount.getAppliedPromotion().equalsIgnoreCase("NA")){
			
		}
		else{
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
	
	private Status validateStoreNumberRedumptionExclusion(LoyaltyRedemptionRequest redemptionRequest, 
			LoyaltyProgramExclusion loyaltyExclusion) throws Exception {
		
		Status status = null;
		if(loyaltyExclusion.getStrRedempChk()!=null && loyaltyExclusion.getStrRedempChk()) {
			if(loyaltyExclusion.getSelectedStoreStr() != null && !loyaltyExclusion.getSelectedStoreStr().trim().isEmpty()){
				String[] storeNumberArr = loyaltyExclusion.getSelectedStoreStr().split(";=;");
				for(String storeNo : storeNumberArr){
					if(redemptionRequest.getHeader().getStoreNumber().trim().equals(storeNo.trim())){
						status = new Status("111533", PropertyUtil.getErrorMessage(111533, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						return status;
					}
				}
			} else if(loyaltyExclusion.getAllStrChk()!=null && loyaltyExclusion.getAllStrChk()==true) {
				status = new Status("111618", PropertyUtil.getErrorMessage(111618, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
		}
		
		return status;
	}
	
	private Status validateStoreNumberExclusion(LoyaltyRedemptionRequest redemptionRequest, 
			LoyaltyProgramExclusion loyaltyExclusion) throws Exception {
		
		Status status = null;
		if(loyaltyExclusion.getStoreNumberStr() != null && !loyaltyExclusion.getStoreNumberStr().trim().isEmpty()){
			String[] storeNumberArr = loyaltyExclusion.getStoreNumberStr().split(";=;");
			for(String storeNo : storeNumberArr){
				if(redemptionRequest.getHeader().getStoreNumber().trim().equals(storeNo.trim())){
					status = new Status("111532", PropertyUtil.getErrorMessage(111532, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
			}
		}
		
		return status;
	}
	
	private Status applyLoyaltyExclusions(LoyaltyRedemptionRequest redemptionRequest, 
			LoyaltyProgramExclusion loyaltyExclusion, Long orgId) throws Exception {
		
		Status status = null;
		// handle redemption at stores
		status = validateStoreNumberRedumptionExclusion(redemptionRequest, loyaltyExclusion);
		if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
			return status;
		}
				
		// handle store number
		status = validateStoreNumberExclusion(redemptionRequest, loyaltyExclusion);
		if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
			return status;
		}
		
		// handle promo codes
		status = validatePromocodeExclusion(redemptionRequest, loyaltyExclusion, orgId);
		if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
			return status;
		}
		
		//handle exclude special days
		status = validateSpecialDateExclusion(redemptionRequest, loyaltyExclusion);
		if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
			return status;
		}
		return status;
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
	
	
	private LoyaltyTransactionChild createSuccessfulTransaction(LoyaltyRedemptionRequest redemptionRequest, ContactsLoyalty loyalty,
			Long orgId, Long transactionId, String amountType, String pointsDiff, String amountDiff, String giftDiff,
			boolean withOtpCode, double conversionAmt, String description,LoyaltyBalance loyaltyBalance,Long rewardDiff, String valueCode, String earnType ){
		
		LoyaltyTransactionChild transaction = null;
		try{
			
			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(transactionId);
			
			transaction.setMembershipNumber(Constants.STRING_NILL+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());

			if(redemptionRequest.getMembership().getCreatedDate() != null && !redemptionRequest.getMembership().getCreatedDate().trim().isEmpty()){
				String requestDate = redemptionRequest.getMembership().getCreatedDate();  
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
			transaction.setPointsDifference(pointsDiff);
			transaction.setAmountDifference(amountDiff);
			transaction.setGiftDifference(giftDiff);
			
			if(redemptionRequest.getAmount().getEnteredValue() != null && !redemptionRequest.getAmount().getEnteredValue().trim().isEmpty()){
				String enteredAmount = Utility.truncateUptoTwoDecimal(Double.valueOf(redemptionRequest.getAmount().getEnteredValue()));
				transaction.setEnteredAmount(Double.parseDouble(enteredAmount));
				transaction.setEnteredAmountType(amountType);
			}
			
			if(redemptionRequest.getAmount().getReceiptAmount() != null && !redemptionRequest.getAmount().getReceiptAmount().trim().isEmpty()){
				String receiptAmount = Utility.truncateUptoTwoDecimal((redemptionRequest.getAmount().getReceiptAmount()));
				
				
				transaction.setReceiptAmount(Double.parseDouble(receiptAmount));
			}
			
			transaction.setUserId(loyalty.getUserId());
			transaction.setOrgId(orgId);
			
			transaction.setEarnType(earnType);
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setCardSetId(loyalty.getCardSetId());
			
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION);
			transaction.setSubsidiaryNumber(redemptionRequest.getHeader().getSubsidiaryNumber() != null && !redemptionRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? redemptionRequest.getHeader().getSubsidiaryNumber().trim() : null);
			logger.debug("storeNumber is===>"+redemptionRequest.getHeader().getStoreNumber());           
			transaction.setStoreNumber(redemptionRequest.getHeader().getStoreNumber() != null && !redemptionRequest.getHeader().getStoreNumber().trim().isEmpty() ? redemptionRequest.getHeader().getStoreNumber() : null);
			transaction.setReceiptNumber(redemptionRequest.getHeader().getReceiptNumber() != null && !redemptionRequest.getHeader().getReceiptNumber().trim().isEmpty() ? redemptionRequest.getHeader().getReceiptNumber().trim() : null);
			
			//	transaction.setStoreNumber(redemptionRequest.getHeader().getStoreNumber());
			transaction.setEmployeeId(redemptionRequest.getHeader().getEmployeeId()!=null && !redemptionRequest.getHeader().getEmployeeId().trim().isEmpty() ? redemptionRequest.getHeader().getEmployeeId().trim():null);
			transaction.setTerminalId(redemptionRequest.getHeader().getTerminalId()!=null && !redemptionRequest.getHeader().getTerminalId().trim().isEmpty() ? redemptionRequest.getHeader().getTerminalId().trim():null);
			transaction.setDocSID(redemptionRequest.getHeader().getDocSID());
			transaction.setSourceType(redemptionRequest.getHeader().getSourceType());
			if(withOtpCode){
				transaction.setDescription2("Redeemed with OTP code : "+redemptionRequest.getOtpCode());
			}
			transaction.setDescription(description.isEmpty() ? null : description);
			transaction.setConversionAmt(conversionAmt);
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			//SpecilaRewards
			
			transaction.setRewardDifference(rewardDiff+"");
        		transaction.setRewardBalance(loyaltyBalance!=null &&loyaltyBalance.getBalance()!=null? loyaltyBalance.getBalance().doubleValue():0.0);
           		transaction.setEarnedReward(rewardDiff!=null ?rewardDiff.doubleValue():0.0);
			transaction.setValueCode(valueCode);
			
			LoyaltyTransactionChildDao loyaltyTransChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			loyaltyTransChildDaoForDML.saveOrUpdate(transaction);
			
			//Event Trigger sending part
			EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			EventTriggerDao eventTriggerDao  = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
			List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(transaction.getUserId(),Constants.ET_TYPE_ON_LOYALTY_REDEMPTION);
			List<EventTrigger> giftEtList = eventTriggerDao.findAllETByUserAndType(transaction.getUserId(),Constants.ET_TYPE_ON_GIFT_REDEMPTION);
			if(etList != null) {
				eventTriggerEventsObservable.notifyToObserver(etList, transaction.getTransChildId(), transaction.getTransChildId(), 
																transaction.getUserId(), OCConstants.LOYALTY_REDEMPTION,Constants.ET_TYPE_ON_LOYALTY_REDEMPTION);
			}
			if(giftEtList != null) {
				eventTriggerEventsObservable.notifyToObserver(giftEtList, transaction.getTransChildId(), transaction.getTransChildId(), 
																transaction.getUserId(), OCConstants.LOYALTY_GIFT_REDEMPTION,Constants.ET_TYPE_ON_GIFT_REDEMPTION);
			}
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		return transaction;
	}
	
	
	private LoyaltyRedemptionResponse performRedemption(LoyaltyRedemptionRequest redemptionRequest, ContactsLoyalty contactsLoyalty, 
			ResponseHeader responseHeader, LoyaltyCards loyaltyCard, Users user, LoyaltyProgram loyaltyProgram) throws Exception {
		
		logger.info(" Entered perform Redemption method...");
		LoyaltyRedemptionResponse redemptionResponse = null;
		Status status = null;
		ValueCodesDao valueCodeDao = (ValueCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.VALUE_CODES_DAO);
		LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
		LoyaltyBalanceDaoForDML loyaltyBalanceDaoForDML = (LoyaltyBalanceDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_BALANCE_DAO_FOR_DML);
		Boolean isValuecodeRedemptionTrue = false;
		String finalRewardAmount=null;
		String type = redemptionRequest.getAmount().getType()!=null ? redemptionRequest.getAmount().getType() : "";//APP-4766
		if(redemptionRequest.getAmount().getEnteredValue() == null || redemptionRequest.getAmount().getEnteredValue().trim().isEmpty()
				|| (Double.valueOf(redemptionRequest.getAmount().getEnteredValue().trim()).doubleValue() <= 0 && !type.equals("Void"))){
			status = new Status("111529", PropertyUtil.getErrorMessage(111529, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
			return redemptionResponse;
		}
		
		boolean withOtpCode = false;
		
		//TODO additional check for otp min limit...
		boolean checkOtp = false;
		/*List<ContactsLoyalty> EnrollList = findEnrollListByEmailORPhone(redemptionRequest.getCustomer().getEmailAddress(), redemptionRequest.getCustomer().getPhone(), user.getUserId());
		
		logger.info("EnrollList is "+EnrollList);
		Long programID = EnrollList != null && !EnrollList.isEmpty() ?  EnrollList.get(0).getProgramId() : null;
	
		logger.info("Program Id is "+programID);*/

		OTPGeneratedCodes otpgeneratedcode = null;
		
		//APP-3666
		LoyaltyProgramTier tier = null;
		if(contactsLoyalty.getProgramTierId() != null)
			tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
		
		if(tier == null){
			Long contactId = null;
			if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
				contactId = contactsLoyalty.getContact().getContactId();
				tier = findTier(contactsLoyalty.getProgramId(), contactId, contactsLoyalty.getUserId(), contactsLoyalty);
				if(tier != null){
					contactsLoyalty.setProgramTierId(tier.getTierId());
					saveContactsLoyalty(contactsLoyalty);
				}
				else{
					status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				}
			}
		}
		
		//APP-4766 Void Redemption
		if(redemptionRequest.getAmount().getType()!=null && !redemptionRequest.getAmount().getType().isEmpty() && 
				redemptionRequest.getAmount().getType().equals("Void")) {
			
			logger.info("Inside void redemption");
			LoyaltyTransactionChildDao loyaltyTransChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			List<LoyaltyTransactionChild> redemptionTrxList = loyaltyTransChildDao.findByDocSID(redemptionRequest.getHeader().getDocSID(),user.getUserId(),OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,
					redemptionRequest.getHeader().getReceiptNumber(),redemptionRequest.getHeader().getStoreNumber(),redemptionRequest.getHeader().getSubsidiaryNumber());
			
			if(redemptionTrxList == null) {
				
				status = new Status("0", "Successfully Reverted ", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				MembershipResponse membershipResponse = prepareMembershipResponse(contactsLoyalty,tier,loyaltyProgram);
				redemptionResponse = prepareRedemptionResponse(responseHeader, membershipResponse, null, null, null, null, status);
				return redemptionResponse;
				/*String msg = PropertyUtil.getErrorMessage(111566, OCConstants.ERROR_LOYALTY_FLAG)+" "+redemptionRequest.getMembership().getCardNumber().trim()+".";
				status = new Status("111566", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				return redemptionResponse;*/
			}
			
			logger.info("redemptionTrxList size "+redemptionTrxList.size());
			double actualRedeemedAmount = 0.0;
			double actualRedeemedPoints = 0.0;
			
			LoyaltyTransactionChild LoyaltyTransactionChild = redemptionTrxList.get(0);
			double enteredAmount = LoyaltyTransactionChild.getEnteredAmount();
			logger.info("enteredAmount in void "+enteredAmount);
			
			if(LoyaltyTransactionChild.getEnteredAmountType() != null && 
					LoyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM)){
				
				if(LoyaltyTransactionChild.getPointsDifference()!=null && !LoyaltyTransactionChild.getPointsDifference().isEmpty())
					actualRedeemedPoints = Double.parseDouble(LoyaltyTransactionChild.getPointsDifference().replace("-",""));
				else if(LoyaltyTransactionChild.getAmountDifference()!=null && !LoyaltyTransactionChild.getAmountDifference().isEmpty())
					actualRedeemedAmount =Double.parseDouble(LoyaltyTransactionChild.getAmountDifference().replace("-",""));
			}
			LoyaltyTransactionChild.setTransactionType("VoidRedemption");
			LoyaltyTransactionChild.setEnteredAmountType("VoidAmountRedeem");
			
			logger.info("actualRedeemedAmount "+actualRedeemedAmount);
			logger.info("actualRedeemedPoints "+actualRedeemedPoints);
			
			if(actualRedeemedAmount>0) {
				double totAmountRedeem = contactsLoyalty.getTotalGiftcardRedemption() == null ? actualRedeemedAmount 
						: contactsLoyalty.getTotalGiftcardRedemption().doubleValue() - actualRedeemedAmount;
				logger.info("totAmountRedeem=="+totAmountRedeem);
				contactsLoyalty.setTotalGiftcardRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance().doubleValue() + actualRedeemedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			
			if(actualRedeemedPoints>0) {
				double totPointsRedeem = contactsLoyalty.getTotalLoyaltyRedemption() == null ? actualRedeemedPoints 
						: contactsLoyalty.getTotalLoyaltyRedemption().doubleValue() - actualRedeemedPoints;
				logger.info("totPointsRedeem=="+totPointsRedeem);
				contactsLoyalty.setTotalLoyaltyRedemption(new BigDecimal(totPointsRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				contactsLoyalty.setLoyaltyBalance(new BigDecimal(contactsLoyalty.getLoyaltyBalance().doubleValue() + actualRedeemedPoints).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			saveContactsLoyalty(contactsLoyalty);//update balances
			
			//updating redemption transaction
			LoyaltyTransactionChildDaoForDML loyaltyTransChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			loyaltyTransChildDaoForDML.saveOrUpdate(LoyaltyTransactionChild);
			
			//insert void poinst/amount in expiry table
			createExpiryTransaction(contactsLoyalty,(long)actualRedeemedPoints,actualRedeemedAmount);
			
			status = new Status("0", "Successfully Reverted "+enteredAmount +" ", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			MembershipResponse membershipResponse = prepareMembershipResponse(contactsLoyalty,tier,loyaltyProgram);
			redemptionResponse = prepareRedemptionResponse(responseHeader, membershipResponse, null, null, null, null, status);
			return redemptionResponse;
			
		}// Void Redemption
		
		if(redemptionRequest.getAmount().getValueCode().equals(OCConstants.LOYALTY_TYPE_CURRENCY) && tier.getRedemptionOTPFlag() == 'Y'){
			checkOtp = true;
			Double enteredValue = Double.valueOf(redemptionRequest.getAmount().getEnteredValue().trim());
			if(tier.getOtpLimitAmt() != null && tier.getOtpLimitAmt().doubleValue() > 0 
					&& enteredValue < tier.getOtpLimitAmt().doubleValue()){//TODO condition check
				checkOtp = false;
			}
		}
		//by this the otp is already validated here we have to just mark the OTP as Used
		if(checkOtp){
			logger.info("OTP flag is enabled...");
			logger.info("RedemptionRequestId from DR"+redemptionRequest.getHeader().getRequestId());
			
			if(redemptionRequest.getOtpCode() == null || redemptionRequest.getOtpCode().trim().isEmpty()){
				
				if(redemptionRequest.getHeader().getRequestId()!=null && !redemptionRequest.getHeader().getRequestId().startsWith("DRToLty")) {

				String message = "Error 111582: Authentication by OTP required to complete redemption of amount "
					+redemptionRequest.getAmount().getEnteredValue()+ " on this purchase. "
					+ "Please try again with OTP code sent to customer's mobile# or email.";
				
				// added to send OTP code to customer and membership-holder's mobile# when redemption request without OTP
				
				/*String phone = redemptionRequest.getCustomer().getPhone();
				String email=redemptionRequest.getCustomer().getEmailAddress();
				boolean isMobile =false;
				boolean isEmail=false;
				Long mblNum = null;
				phone = phone.trim();
				phone = Utility.phoneParse(phone, user!=null ? user.getUserOrganization() : null );
				if(phone != null &&phone.length()>0) {
					if(user.getUserOrganization().isRequireMobileValidation()){
					if(!phone.startsWith(user.getCountryCarrier().toString()) && 
							(phone.length() >= user.getUserOrganization().getMinNumberOfDigits()
							&& phone.length() <= user.getUserOrganization().getMaxNumberOfDigits())) {
						//phone = user.getCountryCarrier().toString()+phone;
						isMobile=true;
					}
					else isMobile=false;
					}
					try{
						mblNum = Long.parseLong(phone);
						isMobile=true;
					}catch (Exception e) {
						isMobile=false;
					}
				}
				if(email!=null && email.length()>0){
						isEmail=Utility.validateEmail(email);
					  }*/
				
				String phone = redemptionRequest.getCustomer().getPhone();
				String email= redemptionRequest.getCustomer().getEmailAddress();
				
				boolean isEmailIDExists = (email != null && email.trim().length() != 0);
				boolean isEmailIdValid = Utility.validateEmail(email);
				boolean isMobileExists = phone != null && phone.trim().length() != 0;
				boolean isMobileValid = isMobileExists && Utility.phoneParse(phone, user.getUserOrganization()) != null;

				phone = !isMobileExists || !isMobileValid ? null : phone;
				 email = !isEmailIDExists || !isEmailIdValid ? null : email;
				
				 boolean isMobile=isMobileValid;
				 boolean isEmail=isEmailIdValid;
				
				if(!isMobile && !isEmail){
					status = new Status("111589", PropertyUtil.getErrorMessage(111589, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				   }
				
				
				otpgeneratedcode = findOTPCodeByPhone(phone, user, OCConstants.OTP_GENERATED_CODE_STATUS_ACTIVE,email);
				if(otpgeneratedcode != null){
					
					Calendar currCal = Calendar.getInstance();
					Calendar createdDate = otpgeneratedcode.getCreatedDate();
					
					long currtime = currCal.getTimeInMillis()/1000;
					long createdtime = createdDate.getTimeInMillis()/1000;
					long timeDiff = currtime - createdtime;
					long duration = 15*60; 
					
						boolean continueWithEmail = true;
						if(timeDiff < duration){
							if(isMobile)
							status = sendOTPCode(user, phone, otpgeneratedcode.getOtpCode());
							if(status != null){
								if(isEmail){
									status=sendOTPCodeToEmail(user,email,otpgeneratedcode.getOtpCode());
									if(status != null){
										logger.info("Sending email failed..."+status.getMessage());
										status = new Status("111588", PropertyUtil.getErrorMessage(111588, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
										redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
										return redemptionResponse;
									 }
									continueWithEmail = false;
								}
							  }
							if(isEmail && continueWithEmail){
							status=sendOTPCodeToEmail(user,email,otpgeneratedcode.getOtpCode());
							}
						otpgeneratedcode.setSentCount(otpgeneratedcode.getSentCount()+1);
						saveOTPgeneratedcode(otpgeneratedcode);					
					}
					else{
						logger.info(" otp code is expired and saved....");
						otpgeneratedcode.setStatus(OCConstants.OTP_GENERATED_CODE_STATUS_EXPIRED);
						saveOTPgeneratedcode(otpgeneratedcode);
					}					
				}else{
					String activeOTPCode = generateOtpCode();
					
					otpgeneratedcode = new OTPGeneratedCodes();
					otpgeneratedcode.setCreatedDate(Calendar.getInstance());
					otpgeneratedcode.setOtpCode(activeOTPCode);
					otpgeneratedcode.setPhoneNumber(phone);
					otpgeneratedcode.setStatus(OCConstants.OTP_GENERATED_CODE_STATUS_ACTIVE);
					otpgeneratedcode.setUserId(user.getUserId());
					otpgeneratedcode.setSentCount(1L);
					otpgeneratedcode.setDocsid(redemptionRequest.getHeader().getDocSID());
					otpgeneratedcode.setEmail(email);
				
				
					boolean continueWithEmail = true;
					if(isMobile)
						status = sendOTPCode(user, phone, otpgeneratedcode.getOtpCode());
						if(status != null){
							if(isEmail){
								status=sendOTPCodeToEmail(user,email,otpgeneratedcode.getOtpCode());
								if(status != null){
									status = new Status("111600", PropertyUtil.getErrorMessage(111600, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
									redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
									return redemptionResponse;
								 }
								continueWithEmail = false;
							}
						  }
						if(isEmail && continueWithEmail){
						status=sendOTPCodeToEmail(user,email,otpgeneratedcode.getOtpCode());
						}
					saveOTPgeneratedcode(otpgeneratedcode);
				}
				
				status = new Status("111582", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				return redemptionResponse;
			
			}
			}
			else{
				
				String phone = redemptionRequest.getCustomer().getPhone();
				String email=redemptionRequest.getCustomer().getEmailAddress();
				Long mblNum = null;
				boolean isMobile =false;
				boolean isEmail=false;
				phone = phone.trim();
				phone = Utility.phoneParse(phone, user!=null ? user.getUserOrganization() : null );
				if(phone != null &&phone.length()>0) {
					if(user.getUserOrganization().isRequireMobileValidation()){
					if(!phone.startsWith(user.getCountryCarrier().toString()) && 
							(phone.length() >= user.getUserOrganization().getMinNumberOfDigits()
							&& phone.length() <= user.getUserOrganization().getMaxNumberOfDigits())) {
						//phone = user.getCountryCarrier().toString()+phone;
						isMobile=true;
					}
					else isMobile=false;
					}
					try{
						mblNum = Long.parseLong(phone);
						isMobile=true;
					}catch (Exception e) {
						isMobile=false;
					}
				}
				if(email!=null && email.length()>0){
						isEmail=Utility.validateEmail(email);
					  }
				if(!isMobile && !isEmail){
					status = new Status("111589", PropertyUtil.getErrorMessage(111589, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				}
				
				otpgeneratedcode = findOTPCodeByPhone(phone, 
						user, OCConstants.OTP_GENERATED_CODE_STATUS_ACTIVE,email);
				
				if(otpgeneratedcode != null){
					Calendar currCal = Calendar.getInstance();
					Calendar createdDate = otpgeneratedcode.getCreatedDate();
					long currtime = currCal.getTimeInMillis()/1000;
					long createdtime = createdDate.getTimeInMillis()/1000;
					long timeDiff = currtime - createdtime;
					long duration = 15*60; 
					
					if(otpgeneratedcode.getOtpCode() != null && !otpgeneratedcode.getOtpCode().trim().isEmpty() &&
							!otpgeneratedcode.getOtpCode().equals(redemptionRequest.getOtpCode().trim())) {
						logger.info("OTP code invalid : "+redemptionRequest.getOtpCode().trim());
						
						status = new Status("111549", PropertyUtil.getErrorMessage(111549, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						return redemptionResponse;
					}
					else if(timeDiff > duration ){
						logger.info("OTP code expired : "+otpgeneratedcode.getOtpCode());
						otpgeneratedcode.setStatus(OCConstants.OTP_GENERATED_CODE_STATUS_EXPIRED);
						saveOTPgeneratedcode(otpgeneratedcode);
						
						status = new Status("111549", PropertyUtil.getErrorMessage(111549, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						return redemptionResponse;
					}
					withOtpCode = true;
				}
				else{
					status = new Status("111549", PropertyUtil.getErrorMessage(111549, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				}
				
			}
			
		}
		//APP-3666
		/*LoyaltyProgramTier tier = null;
		if(contactsLoyalty.getProgramTierId() != null)
			tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());*/
		
		//Changes 2.5.6.0 APP-928 		
		
		/*if(tier == null){
			Long contactId = null;
			if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
				contactId = contactsLoyalty.getContact().getContactId();
				tier = findTier(contactsLoyalty.getProgramId(), contactId, contactsLoyalty.getUserId(), contactsLoyalty);
				if(tier != null){
					contactsLoyalty.setProgramTierId(tier.getTierId());
					saveContactsLoyalty(contactsLoyalty);
				}
				else{
					status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					return redemptionResponse;
				}
			}
		}*/

		
		LoyaltyProgram program = findLoyaltyProgramByProgramId(contactsLoyalty.getProgramId(), contactsLoyalty.getUserId());
		
		String pointsDiff = Constants.STRING_NILL;
		String amountDiff = Constants.STRING_NILL;
		String giftDiff = Constants.STRING_NILL;
		String entAmountType = Constants.STRING_NILL;
		String description = Constants.STRING_NILL;
		double conversionAmt = 0;
		
		
		
		if(redemptionRequest.getAmount().getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)){
			
			LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
			if(loyaltyExclusion != null){
				status = applyLoyaltyExclusions(redemptionRequest,loyaltyExclusion, user.getUserOrganization().getUserOrgId());
				if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
					redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null,status);
					return redemptionResponse;
				}
			}

			Double enteredValue = Double.valueOf(redemptionRequest.getAmount().getEnteredValue());
			int enteredPoints = enteredValue != null ? enteredValue.intValue():0;
			
			if(contactsLoyalty.getLoyaltyBalance() == null 
					|| !(contactsLoyalty.getLoyaltyBalance().intValue() >= enteredPoints)){
				
				if(contactsLoyalty.getLoyaltyBalance() == null || contactsLoyalty.getLoyaltyBalance().intValue() <= 0){
					status = new Status("111530", PropertyUtil.getErrorMessage(111530, OCConstants.ERROR_LOYALTY_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				else{
					status = new Status("111530", PropertyUtil.getErrorMessage(111530, OCConstants.ERROR_LOYALTY_FLAG)+
							" "+contactsLoyalty.getLoyaltyBalance(), 
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				
				List<Balance> balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				MembershipResponse membershipResponse = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
				
				
				HoldBalance holdBalance = new HoldBalance();
				holdBalance.setPoints(contactsLoyalty.getHoldPointsBalance()== null ? Constants.STRING_NILL : Constants.STRING_NILL+contactsLoyalty.getHoldPointsBalance().intValue());
				if(contactsLoyalty.getHoldAmountBalance() == null){
					holdBalance.setCurrency(Constants.STRING_NILL);
				}
				else{
					double value = new BigDecimal(contactsLoyalty.getHoldAmountBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					holdBalance.setCurrency(Constants.STRING_NILL+value);
				}
				
				boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(redemptionRequest.getHeader().getStoreNumber(),tier);
				
				if(tier != null &&  tier.getActivationFlag() == 'Y' && isStoreActiveForActivateAfter &&
						((contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0) ||
						(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0))){
					holdBalance.setActivationPeriod(tier.getPtsActiveDateValue()+" "+tier.getPtsActiveDateType());
				}
				else{
					holdBalance.setActivationPeriod(Constants.STRING_NILL);
				}
				
				//APP-3666
				AdditionalInfo addinfo = new AdditionalInfo();
				if(tier.getRedemptionOTPFlag() == OCConstants.FLAG_YES){
					addinfo.setOtpEnabled("True");
				}
				else{
					addinfo.setOtpEnabled("False");
				}
				if(tier.getRedemptionOTPFlag() == OCConstants.FLAG_YES && tier.getOtpLimitAmt()!=null){
				OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
				otpRedeemLimit.setAmount(Constants.STRING_NILL+tier.getOtpLimitAmt());
				otpRedeemLimit.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
				List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
				otpRedeemLimitlist.add(otpRedeemLimit);
				addinfo.setOtpRedeemLimit(otpRedeemLimitlist);
				}else{
					List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
					addinfo.setOtpRedeemLimit(otpRedeemLimitlist);
					}
				addinfo.setPointsEquivalentCurrency(Constants.STRING_NILL);
				addinfo.setTotalRedeemableCurrency(Constants.STRING_NILL);
			
				redemptionResponse = prepareRedemptionResponse(responseHeader, membershipResponse, balances, holdBalance, addinfo, null, status);
				return redemptionResponse;
			}
			double loyaltyBal = contactsLoyalty.getLoyaltyBalance() - enteredPoints;
			contactsLoyalty.setLoyaltyBalance(loyaltyBal);
			double totLoyaltyRedeem = contactsLoyalty.getTotalLoyaltyRedemption() == null ? enteredPoints 
					: contactsLoyalty.getTotalLoyaltyRedemption()+enteredPoints;
			contactsLoyalty.setTotalLoyaltyRedemption(totLoyaltyRedeem);
			pointsDiff = "-"+enteredPoints;
			amountDiff = Constants.STRING_NILL;
			giftDiff = Constants.STRING_NILL;
			entAmountType = OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM;
			deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), enteredPoints);
		}
		else if(redemptionRequest.getAmount().getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY)){
			
			logger.info("Currency Redemption ....");
			Double enteredValue = Double.valueOf(redemptionRequest.getAmount().getEnteredValue().trim());
			String result = Utility.truncateUptoTwoDecimal(Double.valueOf(redemptionRequest.getAmount().getEnteredValue().trim()));
			if(result != null)
				enteredValue = Double.parseDouble(result);
			//To provide totalRedeemableCurrency  depending upon whichever is the least value among 
			//member's balance and redeem limit value (% of Billed Amount or flat value)
			double pointsAmount = 0.0;
			double loyaltyAmount = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
			double giftAmount = contactsLoyalty.getGiftBalance() == null ? 0.0 : contactsLoyalty.getGiftBalance();
			double perkAmount = 0.0;
			LoyaltyBalance ltyBalance = null;
			if(program!=null && program.getRewardType()!=null && program.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK)) {
				
				ltyBalance = loyaltyBalanceDao.findBy(user.getUserId(), loyaltyProgram.getProgramId(),
						contactsLoyalty.getLoyaltyId(),tier.getEarnType());
				
				if(ltyBalance!=null && tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
						&& ltyBalance.getBalance() != null && ltyBalance.getBalance() > 0){
				
					double factor = ltyBalance.getBalance()/tier.getConvertFromPoints();
					int intFactor = (int)factor;
					perkAmount = tier.getConvertToAmount() * intFactor;
					
					
			}
				
			}
		
			//Changes 2.5.6.0 Discussion required for tier !- 
			if(!OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equals(contactsLoyalty.getRewardFlag()) 
					&& OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND.equalsIgnoreCase(tier.getConversionType()))
				pointsAmount = calculatePointsAmount(contactsLoyalty, tier);
			
			logger.info("loyaltyAmount="+loyaltyAmount);
			logger.info("pointsAmount="+pointsAmount);
			logger.info("giftAmount="+giftAmount);
			
			double totalRedeemableAmount = giftAmount + loyaltyAmount + pointsAmount+perkAmount;

			
			
			//APP-1967
			if(redemptionRequest.getAmount().getReceiptAmount() != null && !redemptionRequest.getAmount().getReceiptAmount().trim().isEmpty() && loyaltyExtraction==null){//APP-2022
			double enteredValOG = enteredValue; 
			double enteredVal = Double.parseDouble(Utility.truncateUptoTwoDecimal((redemptionRequest.getAmount().getReceiptAmount().trim())));	
			
			if(enteredValue > totalRedeemableAmount ) {
				status = new Status("111603", PropertyUtil.getErrorMessage(111603, OCConstants.ERROR_LOYALTY_FLAG), 
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				return redemptionResponse;	
			}
				

				//Changes 2.5.6.0
				//Comparison
			//APP-3666
				if(tier.getRedemptionPercentageLimit() != null && tier.getRedemptionPercentageLimit() > 0 ){
					
					enteredVal = tier.getRedemptionPercentageLimit().doubleValue()/100 * enteredVal;
					enteredValue = enteredVal;
				}
				
				
				if(tier.getRedemptionValueLimit() != null && tier.getRedemptionValueLimit() > 0//APP-1164
						&& enteredValue > tier.getRedemptionValueLimit()){
					enteredValue = tier.getRedemptionValueLimit();
				}
				if(totalRedeemableAmount > 0 && enteredValue > totalRedeemableAmount){
					enteredValue = totalRedeemableAmount;
				}
			
				
			if(enteredValOG > enteredValue ) {
				status = new Status("111603", PropertyUtil.getErrorMessage(111603, OCConstants.ERROR_LOYALTY_FLAG), 
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				return redemptionResponse;	
			}
			enteredValue = enteredValOG;
			
			
			}
			double enteredAmt = enteredValue.doubleValue();
			entAmountType = OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM;
			
			double remBal = enteredAmt;
			logger.info("remBal::"+remBal);
			
			if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equals(contactsLoyalty.getRewardFlag())){
				
				LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
				if(loyaltyExclusion != null){
					status = applyLoyaltyExclusions(redemptionRequest,loyaltyExclusion, user.getUserOrganization().getUserOrgId());
					if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null,status);
						return redemptionResponse;
					}
				}

				logger.info("Loyalty Reward flag ...."+contactsLoyalty.getRewardFlag());
				if(loyaltyAmount >= enteredAmt || (loyaltyAmount < enteredAmt && perkAmount >= enteredAmt)) {
					
					if((loyaltyAmount < enteredAmt && perkAmount >= enteredAmt) && program!=null && program.getRewardType()!=null && program.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK)) {
						
						Long rewardDiff=0l;
						String valueCode = tier.getEarnType();
						enteredAmt = LoyaltyProgramHelper.getRoundedPurchaseAmount(tier.getRoundingType(),enteredAmt);
						rewardDiff = -(long)enteredAmt;
						
						Long totRewardRedeem = (long) (ltyBalance.getTotalRedeemedBalance() == null ? enteredAmt 
								: ltyBalance.getTotalRedeemedBalance().doubleValue() + enteredAmt);
						logger.info("totAmountRedeem=="+totRewardRedeem);
						ltyBalance.setTotalRedeemedBalance(new BigDecimal(totRewardRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						ltyBalance.setBalance(new BigDecimal(ltyBalance.getBalance().longValue() - enteredAmt).setScale(2, BigDecimal.ROUND_HALF_UP).longValue());
						deductRewardAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), enteredAmt,ltyBalance.getValueCode());
						finalRewardAmount = enteredAmt+"";
						
						loyaltyBalanceDaoForDML.saveOrUpdate(ltyBalance);
						createSuccessfulTransaction(redemptionRequest, contactsLoyalty, user.getUserOrganization().getUserOrgId(), 
								Long.valueOf(responseHeader.getTransactionId()), entAmountType, pointsDiff, amountDiff, giftDiff, withOtpCode, conversionAmt, description,ltyBalance,rewardDiff,valueCode,valueCode);
						
						isValuecodeRedemptionTrue=true;
						
					} else {
						amountDiff = "-"+enteredAmt;
						double totAmountRedeem = contactsLoyalty.getTotalGiftcardRedemption() == null ? enteredAmt 
								: contactsLoyalty.getTotalGiftcardRedemption().doubleValue() + enteredAmt;
						logger.info("totAmountRedeem=="+totAmountRedeem);
						contactsLoyalty.setTotalGiftcardRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance().doubleValue() - enteredAmt).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						deductLoyaltyAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), enteredAmt);
					}
					
				}
				else{
					if(tier == null){
						Long contactId = null;
						if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
							contactId = contactsLoyalty.getContact().getContactId();
							tier = findTier(contactsLoyalty.getProgramId(), contactId, contactsLoyalty.getUserId(), contactsLoyalty);
							if(tier != null){
								contactsLoyalty.setProgramTierId(tier.getTierId());
								saveContactsLoyalty(contactsLoyalty);
							}
							else{
								status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
										OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
								return redemptionResponse;
							}
						}
					}

					if(OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND.equals(tier.getConversionType())){
						pointsAmount = calculatePointsAmount(contactsLoyalty, tier);

						if((loyaltyAmount+pointsAmount) >= enteredAmt){
							logger.info("Entered amount is less than available amount...");

							if(remBal > 0 && loyaltyAmount > 0){

								double deductAmt = 0.0;
								if(remBal <= loyaltyAmount){
									deductAmt = remBal;
									contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance().doubleValue() - remBal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
									remBal = 0;
								}
								else{
									remBal = remBal - loyaltyAmount;
									deductAmt = loyaltyAmount;
									contactsLoyalty.setGiftcardBalance(0.0);
								}
								amountDiff = "-"+deductAmt;
								double totAmountRedeem = contactsLoyalty.getTotalGiftcardRedemption() == null ? deductAmt 
										: contactsLoyalty.getTotalGiftcardRedemption().doubleValue() + deductAmt;
								contactsLoyalty.setTotalGiftcardRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								deductLoyaltyAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), deductAmt);
							}
							if(remBal > 0){
								double pointsFactor = tier.getConvertFromPoints()/tier.getConvertToAmount();
								double pointsToBeDeducted = Math.floor(pointsFactor * remBal);
								if(pointsToBeDeducted < contactsLoyalty.getLoyaltyBalance().longValue()){
									logger.info("INVALID CONDITION --- REDEMPTION --- POINTS TO BE DEDUCTED...");
								}
								contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance().longValue() - pointsToBeDeducted);
								contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? pointsToBeDeducted 
										: contactsLoyalty.getTotalLoyaltyRedemption().longValue() + pointsToBeDeducted);
								deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), (long)pointsToBeDeducted);

								pointsDiff = "-"+pointsToBeDeducted;
								description = tier.getConvertFromPoints()+" Points -> "+tier.getConvertToAmount();
								conversionAmt =  remBal;
							}

						}
						else{
							return prepareFailureResponse(contactsLoyalty, tier, program, pointsAmount, responseHeader,redemptionRequest);
						}
					}
					else{
						return prepareFailureResponse(contactsLoyalty, tier, program, pointsAmount, responseHeader,redemptionRequest);
					}
				}
			}
			else if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equals(contactsLoyalty.getRewardFlag())){
				logger.info("Loyalty Reward flag ...."+contactsLoyalty.getRewardFlag());
				boolean isExcluded = false;
				LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
				if(loyaltyExclusion != null){
					status = applyLoyaltyExclusions(redemptionRequest,loyaltyExclusion, user.getUserOrganization().getUserOrgId());
					if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
						isExcluded = true;
					}
				}
				
				if(OCConstants.LOYALTY_TYPE_REWARD.equalsIgnoreCase(redemptionRequest.getAmount().getType())){
					if(isExcluded){
						if(giftAmount >= enteredAmt){
							status = null ;
							giftDiff = "-"+enteredAmt;
							double totAmountRedeem = contactsLoyalty.getTotalGiftRedemption() == null ? enteredAmt 
									: contactsLoyalty.getTotalGiftRedemption().doubleValue() + enteredAmt;
							contactsLoyalty.setTotalGiftRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							contactsLoyalty.setGiftBalance(new BigDecimal(contactsLoyalty.getGiftBalance().doubleValue() - enteredAmt).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							deductGiftAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), enteredAmt);
						}
						else{
							redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null,status);
							return redemptionResponse;
						}
					}
					else if(loyaltyAmount >= enteredAmt){
						amountDiff = "-"+enteredAmt;
						double totAmountRedeem = contactsLoyalty.getTotalGiftcardRedemption() == null ? enteredAmt 
								: contactsLoyalty.getTotalGiftcardRedemption().doubleValue() + enteredAmt;
						contactsLoyalty.setTotalGiftcardRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance().doubleValue() - enteredAmt).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						deductLoyaltyAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), enteredAmt);
					}
					else{
						if(tier == null){
							Long contactId = null;
							if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
								contactId = contactsLoyalty.getContact().getContactId();
								tier = findTier(contactsLoyalty.getProgramId(), contactId, contactsLoyalty.getUserId(), contactsLoyalty);
								if(tier != null){
									contactsLoyalty.setProgramTierId(tier.getTierId());
									saveContactsLoyalty(contactsLoyalty);
								}
								else{
									status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
											OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
									redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
									return redemptionResponse;
								}
							}
						}
						if(OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND.equals(tier.getConversionType())){
							logger.info("On Demand... pointsAmount before conv...."+pointsAmount);
							pointsAmount = calculatePointsAmount(contactsLoyalty, tier);
							logger.info("On Demand... pointsAmount after conv...."+pointsAmount);
						}
						if(loyaltyAmount + pointsAmount >= enteredAmt){
							if(remBal > 0 && loyaltyAmount > 0){
								//Deduct from loyalty bal
								remBal = remBal - loyaltyAmount;
								amountDiff = "-"+loyaltyAmount;
								double totAmountRedeem = contactsLoyalty.getTotalGiftcardRedemption() == null ? loyaltyAmount 
										: contactsLoyalty.getTotalGiftcardRedemption().doubleValue() + loyaltyAmount;
								contactsLoyalty.setTotalGiftcardRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance().doubleValue() - loyaltyAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								deductLoyaltyAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), loyaltyAmount);
							}
							if(remBal > 0 && pointsAmount > 0){
								double pointsFactor = tier.getConvertFromPoints()/tier.getConvertToAmount();
								double pointsToBeDeducted = Math.floor(pointsFactor * remBal);
								if(pointsToBeDeducted < contactsLoyalty.getLoyaltyBalance().longValue()){
									logger.info("INVALID CONDITION --- REDEMPTION --- POINTS TO BE DEDUCTED...");
								}
								pointsDiff = "-"+pointsToBeDeducted;
								contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance().longValue() - pointsToBeDeducted);
								contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? pointsToBeDeducted 
										: contactsLoyalty.getTotalLoyaltyRedemption().longValue() + pointsToBeDeducted);
								deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), (long)pointsToBeDeducted);
								description = tier.getConvertFromPoints()+" Points -> "+tier.getConvertToAmount();
								conversionAmt =  remBal;
							}
						}
						else if(loyaltyAmount + pointsAmount + giftAmount >= enteredAmt){
							if(remBal > 0 && loyaltyAmount > 0){
								remBal = remBal - loyaltyAmount;
								amountDiff = "-"+loyaltyAmount;
								double totAmountRedeem = contactsLoyalty.getTotalGiftcardRedemption() == null ? loyaltyAmount 
										: contactsLoyalty.getTotalGiftcardRedemption().doubleValue() + loyaltyAmount;
								contactsLoyalty.setTotalGiftcardRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance().doubleValue() - loyaltyAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								deductLoyaltyAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), loyaltyAmount);
							}
							if(remBal > 0 && pointsAmount > 0){
								remBal = remBal - pointsAmount;
								double pointsFactor = tier.getConvertFromPoints()/tier.getConvertToAmount();
								double pointsToBeDeducted = Math.floor(pointsFactor * pointsAmount);
								if(pointsToBeDeducted < contactsLoyalty.getLoyaltyBalance().longValue()){
									logger.info("INVALID CONDITION --- REDEMPTION --- POINTS TO BE DEDUCTED...");
								}
								pointsDiff = "-"+pointsToBeDeducted;
								contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance().longValue() - pointsToBeDeducted);
								contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? pointsToBeDeducted 
										: contactsLoyalty.getTotalLoyaltyRedemption().longValue() + pointsToBeDeducted);
								deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), (long)pointsToBeDeducted);
								description = tier.getConvertFromPoints()+" Points -> "+tier.getConvertToAmount();
								conversionAmt = remBal;
							}
							if(remBal > 0 && giftAmount > 0){
								//Deduct from gift amount
								giftDiff = "-"+remBal;
								double totAmountRedeem = contactsLoyalty.getTotalGiftRedemption() == null ? remBal 
										: contactsLoyalty.getTotalGiftRedemption().doubleValue() + remBal;
								contactsLoyalty.setTotalGiftRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								contactsLoyalty.setGiftBalance(new BigDecimal(contactsLoyalty.getGiftBalance().doubleValue() - remBal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								deductGiftAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), remBal);
							}
						}
						else{
							return prepareFailureResponse(contactsLoyalty, tier, program, pointsAmount, responseHeader,redemptionRequest);
						}
					}
				}
				else if(OCConstants.LOYALTY_TYPE_GIFT.equalsIgnoreCase(redemptionRequest.getAmount().getType())){
					if(giftAmount >= enteredAmt){
						status = null ;
						giftDiff = "-"+enteredAmt;
						double totAmountRedeem = contactsLoyalty.getTotalGiftRedemption() == null ? enteredAmt 
								: contactsLoyalty.getTotalGiftRedemption().doubleValue() + enteredAmt;
						contactsLoyalty.setTotalGiftRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						contactsLoyalty.setGiftBalance(new BigDecimal(contactsLoyalty.getGiftBalance().doubleValue() - enteredAmt).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						deductGiftAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), enteredAmt);
					}
					else if(isExcluded){
						status = null;
						return prepareFailureResponse(contactsLoyalty, tier, program, pointsAmount, responseHeader,redemptionRequest);
					}
					else if(giftAmount + loyaltyAmount >= enteredAmt){
						if(remBal > 0 && giftAmount > 0){
							//Deduct from gift bal
							remBal = remBal - giftAmount;
							giftDiff = "-"+giftAmount;
							double totAmountRedeem = contactsLoyalty.getTotalGiftRedemption() == null ? giftAmount 
									: contactsLoyalty.getTotalGiftRedemption().doubleValue() + giftAmount;
							contactsLoyalty.setTotalGiftRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							contactsLoyalty.setGiftBalance(0.0);
							deductGiftAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), giftAmount);
						}
						if(remBal > 0 && loyaltyAmount > 0){
							//Deduct from loyalty bal
							amountDiff = "-"+remBal;
							double totAmountRedeem = contactsLoyalty.getTotalGiftcardRedemption() == null ? remBal 
									: contactsLoyalty.getTotalGiftcardRedemption().doubleValue() + remBal;
							contactsLoyalty.setTotalGiftcardRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance().doubleValue() - remBal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							deductLoyaltyAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), remBal);
						}
					}
					else{
						if(tier == null){
							Long contactId = null;
							if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
								contactId = contactsLoyalty.getContact().getContactId();
								tier = findTier(contactsLoyalty.getProgramId(), contactId, contactsLoyalty.getUserId(), contactsLoyalty);
								if(tier != null){
									contactsLoyalty.setProgramTierId(tier.getTierId());
									saveContactsLoyalty(contactsLoyalty);
								}
								else{
									status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
											OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
									redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
									return redemptionResponse;
								}
							}
						}
						if(OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND.equals(tier.getConversionType())){
							logger.info("On Demand... pointsAmount before conv...."+pointsAmount);
							pointsAmount = calculatePointsAmount(contactsLoyalty, tier);
							logger.info("On Demand... pointsAmount after conv...."+pointsAmount);
						}
						if(giftAmount + loyaltyAmount + pointsAmount >= enteredAmt){
							if(remBal > 0 && giftAmount > 0){
								//Deduct from gift bal
								remBal = remBal - giftAmount;
								giftDiff = "-"+giftAmount;
								double totAmountRedeem = contactsLoyalty.getTotalGiftRedemption() == null ? giftAmount 
										: contactsLoyalty.getTotalGiftRedemption().doubleValue() + giftAmount;
								contactsLoyalty.setTotalGiftRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								contactsLoyalty.setGiftBalance(0.0);
								deductGiftAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), giftAmount);
							}
							if(remBal > 0 && loyaltyAmount > 0){
								//Deduct from loyalty bal
								remBal = remBal - loyaltyAmount;
								amountDiff = "-"+loyaltyAmount;
								double totAmountRedeem = contactsLoyalty.getTotalGiftcardRedemption() == null ? loyaltyAmount 
										: contactsLoyalty.getTotalGiftcardRedemption().doubleValue() + loyaltyAmount;
								contactsLoyalty.setTotalGiftcardRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance().doubleValue() - loyaltyAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								deductLoyaltyAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), loyaltyAmount);
							}
							if(remBal > 0 && pointsAmount > 0){
								double pointsFactor = (tier.getConvertFromPoints()/tier.getConvertToAmount());
								double pointsToBeDeducted = Math.floor(pointsFactor * remBal);
								if(pointsToBeDeducted < contactsLoyalty.getLoyaltyBalance().longValue()){
									logger.info("INVALID CONDITION --- REDEMPTION --- POINTS TO BE DEDUCTED...");
								}
								pointsDiff = "-"+pointsToBeDeducted;
								contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - pointsToBeDeducted);
								contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? pointsToBeDeducted 
										: contactsLoyalty.getTotalLoyaltyRedemption().longValue() + pointsToBeDeducted);
								deductPointsFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), (long)pointsToBeDeducted);
								description = tier.getConvertFromPoints()+" Points -> "+tier.getConvertToAmount();
								conversionAmt = remBal;
							}
						}
						else{
							return prepareFailureResponse(contactsLoyalty, tier, program, pointsAmount, responseHeader,redemptionRequest);
						}
					}
				}
				else{
					MembershipResponse membershipResponse = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
					List<Balance> balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
					List<ContactsLoyalty> loyaltyList = new ArrayList<ContactsLoyalty>();
					loyaltyList.add(contactsLoyalty);
					List<MatchedCustomer> custList = prepareSuccessMatchedCustomers(loyaltyList);
					
					HoldBalance holdBalance = new HoldBalance();
					holdBalance.setPoints(contactsLoyalty.getHoldPointsBalance()== null ? Constants.STRING_NILL : Constants.STRING_NILL+contactsLoyalty.getHoldPointsBalance().intValue());
					if(contactsLoyalty.getHoldAmountBalance() == null){
						holdBalance.setCurrency(Constants.STRING_NILL);
					}
					else{
						double value = new BigDecimal(contactsLoyalty.getHoldAmountBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						holdBalance.setCurrency(Constants.STRING_NILL+value);
					}
					
					boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(redemptionRequest.getHeader().getStoreNumber(),tier);

					if(tier != null &&  tier.getActivationFlag() == 'Y' && isStoreActiveForActivateAfter &&
							((contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0) ||
							(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0))){
						holdBalance.setActivationPeriod(tier.getPtsActiveDateValue()+" "+tier.getPtsActiveDateType());
					}
					else{
						holdBalance.setActivationPeriod(Constants.STRING_NILL);
					}
					
					status = new Status("111560", PropertyUtil.getErrorMessage(111560, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					redemptionResponse = prepareRedemptionResponse(responseHeader, membershipResponse, balances, holdBalance, null, custList, status);
					return redemptionResponse;
				}
				
			} // GL - else if
			else if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equals(contactsLoyalty.getRewardFlag())){
				if(giftAmount >= enteredAmt) {
					giftDiff = "-"+enteredAmt;
					double totAmountRedeem = contactsLoyalty.getTotalGiftRedemption() == null ? enteredAmt 
							: contactsLoyalty.getTotalGiftRedemption() + enteredAmt;
					contactsLoyalty.setTotalGiftRedemption(new BigDecimal(totAmountRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					contactsLoyalty.setGiftBalance(new BigDecimal(contactsLoyalty.getGiftBalance() - enteredAmt).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					deductGiftAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), enteredAmt);
				}
				else{
			
					return prepareFailureResponse(contactsLoyalty, null, program, pointsAmount, responseHeader,redemptionRequest);
					
				}
				
			}
		}
		else if(!redemptionRequest.getAmount().getValueCode().isEmpty()) {
			logger.info("ValueCode Redemption ....");
			String valueCode = redemptionRequest.getAmount().getValueCode().toString();
				LoyaltyBalance loyaltyBalance = loyaltyBalanceDao.findBy(user.getUserId(), loyaltyProgram.getProgramId(),contactsLoyalty.getLoyaltyId(),valueCode);
				if(loyaltyBalance!=null) {
				Long enteredValue = Long.parseLong(redemptionRequest.getAmount().getEnteredValue().trim());
				Long rewardAmount =loyaltyBalance ==null  ?0 : (long) loyaltyBalance.getBalance();
				Long totalRedeemableAmount =rewardAmount;
				Long rewardDiff=0l;
				if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equals(contactsLoyalty.getRewardFlag())){
					
					LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
					if(loyaltyExclusion != null){
						status = applyLoyaltyExclusions(redemptionRequest,loyaltyExclusion, user.getUserOrganization().getUserOrgId());
						if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
							redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null,status);
							return redemptionResponse;
						}
					}
					if(rewardAmount >= enteredValue) {
						rewardDiff = -enteredValue;
						Long totRewardRedeem = (long) (loyaltyBalance.getTotalRedeemedBalance() == null ? enteredValue 
								: loyaltyBalance.getTotalRedeemedBalance().doubleValue() + enteredValue);
						logger.info("totAmountRedeem=="+totRewardRedeem);
						loyaltyBalance.setTotalRedeemedBalance(new BigDecimal(totRewardRedeem).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						loyaltyBalance.setBalance(new BigDecimal(loyaltyBalance.getBalance().longValue() - enteredValue).setScale(2, BigDecimal.ROUND_HALF_UP).longValue());
						deductRewardAmtFromExpiryTable(contactsLoyalty, contactsLoyalty.getUserId(), enteredValue,loyaltyBalance.getValueCode());
						finalRewardAmount = enteredValue+"";
					}
					else {//Not correct
					return prepareFailureResponse(contactsLoyalty, tier, program, rewardAmount, responseHeader,redemptionRequest);
					}
					loyaltyBalanceDaoForDML.saveOrUpdate(loyaltyBalance);
					createSuccessfulTransaction(redemptionRequest, contactsLoyalty, user.getUserOrganization().getUserOrgId(), 
							Long.valueOf(responseHeader.getTransactionId()), entAmountType, pointsDiff, amountDiff, giftDiff, withOtpCode, conversionAmt, description,loyaltyBalance,rewardDiff,valueCode,valueCode);
					
					isValuecodeRedemptionTrue=true;
			}
			else {
				return prepareFailureResponse(contactsLoyalty, tier, program, rewardAmount, responseHeader,redemptionRequest);
			}
		}
	else {
		
		status = new Status("111599", PropertyUtil.getErrorMessage(111599, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
		return redemptionResponse;
	}
				
	  		
			
			
			
		}
		
		
		saveContactsLoyalty(contactsLoyalty);
		
		List<Balance> balances = prepareBalancesObject(contactsLoyalty, pointsDiff, amountDiff, giftDiff);
		
		MembershipResponse membershipResponse = prepareMembershipResponse(contactsLoyalty, tier, program);
		
		HoldBalance holdBalance = new HoldBalance();
		holdBalance.setPoints(contactsLoyalty.getHoldPointsBalance()== null ? Constants.STRING_NILL : Constants.STRING_NILL+contactsLoyalty.getHoldPointsBalance().intValue());
		holdBalance.setCurrency(contactsLoyalty.getHoldAmountBalance() == null ? Constants.STRING_NILL : Constants.STRING_NILL+contactsLoyalty.getHoldAmountBalance());
		
		boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(redemptionRequest.getHeader().getStoreNumber(),tier);

		if(tier != null && tier.getActivationFlag() == 'Y' && isStoreActiveForActivateAfter &&
				((contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0) ||
				(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0))){
			holdBalance.setActivationPeriod(tier.getPtsActiveDateValue()+" "+tier.getPtsActiveDateType());
		}
		else {
			holdBalance.setActivationPeriod(Constants.STRING_NILL);
		}
		if(!isValuecodeRedemptionTrue)
		createSuccessfulTransaction(redemptionRequest, contactsLoyalty, user.getUserOrganization().getUserOrgId(), 
				Long.valueOf(responseHeader.getTransactionId()), entAmountType, pointsDiff, amountDiff, giftDiff, withOtpCode, conversionAmt, description,null,null,null,Constants.STRING_NILL);
		
		List<ContactsLoyalty> loyaltyList = new ArrayList<ContactsLoyalty>();
		loyaltyList.add(contactsLoyalty);
		List<MatchedCustomer> custList = prepareSuccessMatchedCustomers(loyaltyList);
		
		// to update docsid,transaction_type,receipt_amount,status(used) in OTPGeneratedCodes 
		if(checkOtp && otpgeneratedcode != null){
			otpgeneratedcode.setDocsid(redemptionRequest.getHeader().getDocSID());
			otpgeneratedcode.setStatus(OCConstants.OTP_GENERATED_CODE_STATUS_USED);
			otpgeneratedcode.setTransactionType(OCConstants.LOYALTY_TRANSACTION_REDEMPTION);
			otpgeneratedcode.setReceiptAmount(redemptionRequest.getAmount().getReceiptAmount() != null && 
					!redemptionRequest.getAmount().getReceiptAmount().isEmpty()? 
							Double.parseDouble(Utility.truncateUptoTwoDecimal(redemptionRequest.getAmount().getReceiptAmount())) : 0);
			saveOTPgeneratedcode(otpgeneratedcode);
		}
		//Chnages 2.5.5 start
		double redeemAmount = !amountDiff.equals(Constants.STRING_NILL)?Math.abs(Double.parseDouble(amountDiff)):0.0;
		double redeemGift 	= !giftDiff.equals(Constants.STRING_NILL)?Math.abs(Double.parseDouble(giftDiff)):0.0;
		int redeemPoints 	= (int)(!pointsDiff.equals(Constants.STRING_NILL)?Math.abs(Double.parseDouble(pointsDiff)):0);
		String valueCode = redemptionRequest.getAmount().getValueCode();
		String currency = Utility.countryCurrencyMap.get(user.getCountryType());
	
		if(redemptionRequest.getAmount().getValueCode().trim().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY) ){
			if( redeemPoints <=0) {//Changes APP-737
				status = new Status("0", "Successfully redeemed "+currency+(redeemAmount+redeemGift)+".", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			}else if(redeemAmount == 0 && redeemGift==0 && redeemPoints >0) {
				
				status = new Status("0", "Successfully redeemed "+redeemPoints +" points.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			}else if((redeemAmount > 0 || redeemGift >0) && redeemPoints>0 ){
				status = new Status("0", "Successfully redeemed "+currency+(redeemAmount+redeemGift)+" and "+redeemPoints +" points.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			}else{
				status = new Status("0", "Redemption is successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			}
		}
		else if(isValuecodeRedemptionTrue){
			status = new Status("0", "Successfully redeemed "+(finalRewardAmount+" "+valueCode)+".", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		}else if(redemptionRequest.getAmount().getValueCode().trim().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)){
			status = new Status("0", "Successfully redeemed "+redeemPoints +" points.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		}else{
			status = new Status("0", "Redemption is successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		}
		
		logger.info("Successfully==>"+redeemAmount+" === "+redeemGift +" === "+redeemPoints);
		
		LoyaltyAutoComm autoComm = getLoyaltyAutoComm(loyaltyProgram.getProgramId());
		LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
		if (autoComm != null && autoComm.getRedemptionAutoEmailTmplId() != null
				&& contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
			
			logger.info("Inside LoyaltyRedemption AutoComm 1");
			Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
			if (contact != null && contact.getEmailId() != null) {
				autoCommGen.sendLoyaltyRedemptionTemplate(autoComm.getRedemptionAutoEmailTmplId(),
						Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), user,
						contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
						contactsLoyalty.getLoyaltyId());
				logger.info("Inside LoyaltyRedemption AutoComm");
			}
		}
		if (user.isEnableSMS() && autoComm != null && autoComm.getRedemptionAutoSmsTmplId() != null
				&& contactsLoyalty.getMobilePhone() != null) {
			// Contacts contact =
			// findContactById(contactsLoyalty.getContact().getContactId());
			Long contactId = null;
			if (contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
				autoCommGen.sendLoyaltyRedemptionSMSTemplate(autoComm.getRedemptionAutoSmsTmplId(), user,
						contactsLoyalty.getContact().getContactId(), contactsLoyalty.getLoyaltyId(),
						contactsLoyalty.getMobilePhone());
			}
		}
		
		//Chnages 2.5.5 end
		redemptionResponse = prepareRedemptionResponse(responseHeader, membershipResponse, balances, holdBalance, null, custList, status);
		return redemptionResponse;
		
		
		
	}
	
	//APP-4766
	private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty, Long expiryPoints,
			Double expiryAmount) {

		LoyaltyTransactionExpiry transaction = null;
		try {

			transaction = new LoyaltyTransactionExpiry();
			transaction.setMembershipNumber(Constants.STRING_NILL + loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setExpiryPoints(expiryPoints);
			transaction.setExpiryAmount(expiryAmount);
			transaction.setRewardFlag(loyalty.getRewardFlag());
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			
			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao) ServiceLocator
					.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			
			loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);

		} catch (Exception e) {
			logger.error("Exception while logging enroll transaction...", e);
		}
		return transaction;
	}
	
	private Contacts findContactById(Long cid) throws Exception {

		ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactsDao.findById(cid);
	}
	
	private LoyaltyAutoComm getLoyaltyAutoComm(Long programId) throws Exception {
		LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
		return autoCommDao.findById(programId);
	}
	
	
	private LoyaltyRedemptionResponse cardBasedRedemption(LoyaltyRedemptionRequest redemptionRequest, LoyaltyCards loyaltyCard, 
			ResponseHeader responseHeader, Users user) throws Exception {
		
		LoyaltyRedemptionResponse redemptionResponse = null;
		Status status = null;
		
		LoyaltyProgram loyaltyProgram = null;
		
		loyaltyProgram = findLoyaltyProgramByProgramId(loyaltyCard.getProgramId(), user.getUserId());
		if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equals(loyaltyProgram.getStatus())){
			status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
			return redemptionResponse;
		}
		
		LoyaltyCardSet loyaltyCardSet = null;
		loyaltyCardSet = findLoyaltyCardSetByCardsetId(loyaltyCard.getCardSetId(), user.getUserId());
		if(loyaltyCardSet == null || !OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE.equals(loyaltyCardSet.getStatus())){
			status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
			return redemptionResponse;
		}
		
		ContactsLoyalty contactsLoyalty = findContactLoyalty(loyaltyCard.getCardNumber(), loyaltyProgram.getProgramId(), user.getUserId());
	
		if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
			LoyaltyProgramTier tier = null;
			
			List<Balance> balances = null;
			
			List<ContactsLoyalty> loyaltyList = new ArrayList<ContactsLoyalty>();
			
			
			if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
				loyaltyList.add(contactsLoyalty);
				if(contactsLoyalty.getProgramTierId() != null)	tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				
				balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
				loyaltyList.add(contactsLoyalty);
				if(contactsLoyalty.getProgramTierId() != null) tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				
				balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}else if( contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
				ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactsLoyalty);
				String maskedNum = Constants.STRING_NILL;
				if(destLoyalty != null) {
					loyaltyList.add(destLoyalty);
					contactsLoyalty = destLoyalty;
					tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
					balances = prepareBalancesObject(destLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
					maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
					
				}
				String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+maskedNum+".";
				
				status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			
			List<MatchedCustomer> custList = prepareSuccessMatchedCustomers(loyaltyList);
			MembershipResponse membershipResponse = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
			redemptionResponse = prepareRedemptionResponse(responseHeader, membershipResponse, balances, null, null, custList, status);
			return redemptionResponse;
		}
		
		if(OCConstants.LOYALTY_CARD_STATUS_ENROLLED.equalsIgnoreCase(loyaltyCard.getStatus())){
			
			status = checkPromoEmpty(redemptionRequest.getDiscounts());
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				return redemptionResponse;
			}
			return performRedemption(redemptionRequest, contactsLoyalty, responseHeader, loyaltyCard, user, loyaltyProgram);
			
		}
		else if(OCConstants.LOYALTY_CARD_STATUS_ACTIVATED.equalsIgnoreCase(loyaltyCard.getStatus())){
			return performRedemption(redemptionRequest, contactsLoyalty, responseHeader, loyaltyCard, user, loyaltyProgram);
		}
		else{
								
			status = new Status("111537", PropertyUtil.getErrorMessage(111537, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
			return redemptionResponse;
		}
		
	}
	private ContactsLoyalty getDestMembershipIfAny(ContactsLoyalty contactLoyalty) throws Exception{
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		if(contactLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED) && contactLoyalty.getTransferedTo() != null) {
			return loyaltyDao.findAllByLoyaltyId(contactLoyalty.getTransferedTo());
			
		}
		
		return null;
	}
	private LoyaltyRedemptionResponse mobileBasedRedemption(LoyaltyRedemptionRequest redemptionRequest, ContactsLoyalty contactsLoyalty, Users user, 
			ResponseHeader responseHeader, LoyaltyCards loyaltyCard) throws Exception {
		
		Status status = null;
		LoyaltyRedemptionResponse redemptionResponse = null;
		
		LoyaltyProgram loyaltyProgram = findActiveMobileProgram(contactsLoyalty.getProgramId());
		
		if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equals(loyaltyProgram.getStatus())){
			status = new Status("111522", PropertyUtil.getErrorMessage(111522, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
			return redemptionResponse;
		}
		
		if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED) ){
			LoyaltyProgramTier tier = null;
			List<Balance> balances = null;
			
			List<ContactsLoyalty> loyaltyList = new ArrayList<ContactsLoyalty>();
			
			
			if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
				loyaltyList.add(contactsLoyalty);
				if(contactsLoyalty.getProgramTierId() != null)
					tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				 balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
				loyaltyList.add(contactsLoyalty);
				if(contactsLoyalty.getProgramTierId() != null)
					tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
				String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}	 
			List<MatchedCustomer> custList = prepareSuccessMatchedCustomers(loyaltyList);
			MembershipResponse membershipResponse = prepareMembershipResponse(contactsLoyalty, tier, loyaltyProgram);
			redemptionResponse = prepareRedemptionResponse(responseHeader, membershipResponse, balances, null, null, custList, status);
			return redemptionResponse;
		}
		
		status = checkPromoEmpty(redemptionRequest.getDiscounts());
		if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
			redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
			return redemptionResponse;
		}
		
		return performRedemption(redemptionRequest, contactsLoyalty, responseHeader, loyaltyCard, user, loyaltyProgram);
		
	}
	
	private MembershipResponse prepareMembershipResponse(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier, 
			LoyaltyProgram program) throws Exception {
	
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
		
		return membershipResponse;
	}
	
	private LoyaltyProgram findLoyaltyProgramByProgramId(Long programId, Long userId) throws Exception {
		
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findByIdAndUserId(programId, userId);
	}
			
	private LoyaltyCardSet findLoyaltyCardSetByCardsetId(Long cardSetId, Long userId) throws Exception {
		LoyaltyCardSetDao cardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		return cardSetDao.findByCardSetId(cardSetId);
		
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
					expiryDaoForDML.saveOrUpdate(expiry);
					continue;
					
				}
				else if(expiry.getExpiryPoints() >= remPoints){
					logger.info("subtracted loyalty points = "+expiry.getExpiryPoints());
					expiry.setExpiryPoints(expiry.getExpiryPoints() - remPoints);
					remPoints = 0; 
					expiryDaoForDML.saveOrUpdate(expiry);
					break;
				}
				
			}
			expiryList = null;
		
		}while(remPoints > 0);
		
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
	
	
private void deductRewardAmtFromExpiryTable(ContactsLoyalty loyalty, Long userId, double subAmt,String valueCode) throws Exception{
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; 
		Iterator<LoyaltyTransactionExpiry> iterList = null;
		LoyaltyTransactionExpiry expiry = null;
		Long remAmount =(long) subAmt;
		
		do{
			expiryList = expiryDao.fetchExpRewardAmtTrans(loyalty.getLoyaltyId(), 100, userId,valueCode);
			if(expiryList == null || remAmount <= 0) break;
			iterList = expiryList.iterator();
			
			while(iterList.hasNext()){
				expiry = iterList.next();
				
				if(expiry.getExpiryReward() == null || expiry.getExpiryReward() <= 0){ 
					logger.info("WRONG EXPIRY TRANSACTION FETCHED...");
					continue;
				}
				else if(expiry.getExpiryReward() < remAmount){
					logger.info("subtracted loyalty amount = "+expiry.getExpiryAmount());
					remAmount = remAmount - expiry.getExpiryReward();
					expiry.setExpiryReward(0l);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					logger.info("Expiry Reward deducted..."+expiry.getExpiryReward().doubleValue());
					continue;
					
				}
				else if(expiry.getExpiryReward() >= remAmount){
					logger.info("subtracted loyalty amount = "+expiry.getExpiryAmount());
					expiry.setExpiryReward(expiry.getExpiryReward() - remAmount);
					remAmount = 0l; 
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					logger.info("Expiry Reward deducted..."+remAmount);
					break;
				}
				
			}
			expiryList = null;
		
		}while(remAmount > 0);
		
	}
	
	private void deductGiftAmtFromExpiryTable(ContactsLoyalty loyalty, Long userId, double subAmt) throws Exception{
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; 
		Iterator<LoyaltyTransactionExpiry> iterList = null;
		LoyaltyTransactionExpiry expiry = null;
		double remAmt = subAmt;
		
		do{
			expiryList = expiryDao.fetchExpGiftAmtTrans(loyalty.getLoyaltyId(), 100, userId);
			if(expiryList == null || remAmt <= 0) break;
			iterList = expiryList.iterator();
			
			while(iterList.hasNext()){
				expiry = iterList.next();
				
				if(expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0){ 
					logger.info("WRONG EXPIRY TRANSACTION FETCHED...");
					continue;
				}
				else if(expiry.getExpiryAmount() < remAmt){
					logger.info("subtracted gift amount = "+expiry.getExpiryAmount());
					remAmt = remAmt - expiry.getExpiryAmount().doubleValue();
					expiry.setExpiryAmount(0.0);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					continue;
					
				}
				else if(expiry.getExpiryAmount() >= remAmt){
					logger.info("subtracted gift amount = "+expiry.getExpiryAmount());
					expiry.setExpiryAmount(expiry.getExpiryAmount() - remAmt);
					remAmt = 0; 
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					break;
				}
				
			}
			expiryList = null;
		
		}while(remAmt > 0);
		
	}
	
	private double calculatePointsAmount(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier) throws Exception {
		
		if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
				&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0){
		
			double factor = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
			int intFactor = (int)factor;
			return tier.getConvertToAmount() * intFactor;
			
		}
		else return 0.0;
	}
		
	private LoyaltyRedemptionResponse prepareFailureResponse(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier,
			LoyaltyProgram program, double pointsAmount, ResponseHeader responseHeader, LoyaltyRedemptionRequest loyaltyRedemptionRequest) throws Exception {
		
		Status status = new Status("111530", PropertyUtil.getErrorMessage(111530, OCConstants.ERROR_LOYALTY_FLAG), 
				OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		List<Balance> balances = prepareBalancesObject(contactsLoyalty, Constants.STRING_NILL, Constants.STRING_NILL, Constants.STRING_NILL);
		
		MembershipResponse membershipResponse = prepareMembershipResponse(contactsLoyalty, tier, program);
		
		HoldBalance holdBalance = new HoldBalance();
		holdBalance.setPoints(contactsLoyalty.getHoldPointsBalance()== null ? Constants.STRING_NILL : Constants.STRING_NILL+contactsLoyalty.getHoldPointsBalance().intValue());
		if(contactsLoyalty.getHoldAmountBalance() == null){
			holdBalance.setCurrency(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(contactsLoyalty.getHoldAmountBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			holdBalance.setCurrency(Constants.STRING_NILL+value);
		}
		
		boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(loyaltyRedemptionRequest.getHeader().getStoreNumber(),tier);
		
		if(tier != null &&  tier.getActivationFlag() == 'Y' && isStoreActiveForActivateAfter &&
				((contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0) ||
				(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0))){
			holdBalance.setActivationPeriod(tier.getPtsActiveDateValue()+" "+tier.getPtsActiveDateType());
		}
		else{
			holdBalance.setActivationPeriod(Constants.STRING_NILL);
		}
		
		//APP-3666
		AdditionalInfo addinfo = new AdditionalInfo();
		if(tier.getRedemptionOTPFlag() == OCConstants.FLAG_YES){
			addinfo.setOtpEnabled("True");
		}
		else{
			addinfo.setOtpEnabled("False");
		}
		if(tier.getRedemptionOTPFlag() == OCConstants.FLAG_YES && tier.getOtpLimitAmt()!=null){
			OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
			otpRedeemLimit.setAmount(Constants.STRING_NILL+tier.getOtpLimitAmt());
			otpRedeemLimit.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
			List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
			otpRedeemLimitlist.add(otpRedeemLimit);
			addinfo.setOtpRedeemLimit(otpRedeemLimitlist);
			}
		else{
			List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
		    addinfo.setOtpRedeemLimit(otpRedeemLimitlist);
			}
		if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
			addinfo.setPointsEquivalentCurrency(Constants.STRING_NILL);
			if(contactsLoyalty.getGiftBalance() != null){
				double value = new BigDecimal(contactsLoyalty.getGiftBalance().doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				addinfo.setTotalRedeemableCurrency(value+Constants.STRING_NILL);
			}
			else{
				addinfo.setTotalRedeemableCurrency(Constants.STRING_NILL);
			}
		}
		else{
			addinfo.setPointsEquivalentCurrency(Constants.STRING_NILL+pointsAmount);
			double totalredeemable = pointsAmount;
			if(contactsLoyalty.getGiftcardBalance() != null){
				totalredeemable += contactsLoyalty.getGiftcardBalance().doubleValue();
			}
			if(contactsLoyalty.getGiftBalance() != null){
				totalredeemable += contactsLoyalty.getGiftBalance().doubleValue();
			}
			
			double value = new BigDecimal(totalredeemable).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			
			addinfo.setTotalRedeemableCurrency(Constants.STRING_NILL+value);
		}
		
		LoyaltyRedemptionResponse redemptionResponse = prepareRedemptionResponse(responseHeader, membershipResponse, balances, holdBalance, addinfo, null, status);
		return redemptionResponse;
	}
	
	private OTPGeneratedCodes findOTPCodeByPhone(String phone, Users user, String status,String email) throws Exception {
		OTPGeneratedCodesDao otpGenCodesDao = (OTPGeneratedCodesDao)ServiceLocator.getInstance().getBeanByName(OCConstants.OTP_GENERATEDCODES_DAO);
		return otpGenCodesDao.findOTPCodeByPhonenew(phone, user, status,email,null);
	}
	private void saveOTPgeneratedcode(OTPGeneratedCodes otpgenCode) throws Exception {
		OTPGeneratedCodesDao otpGenCodesDao = (OTPGeneratedCodesDao)ServiceLocator.getInstance().getBeanByName(OCConstants.OTP_GENERATEDCODES_DAO);
		OTPGeneratedCodesDaoForDML otpGenCodesDaoForDML = (OTPGeneratedCodesDaoForDML)ServiceLocator.getInstance().getBeanByName(OCConstants.OTP_GENERATEDCODES_DAO_FOR_DML);
		otpGenCodesDaoForDML.saveOrUpdate(otpgenCode);
	}
	
	private Status validateEnteredValue(Amount amount,String type){
		logger.info(" Entered into validateEnteredValue method >>>");
		Status status = null;
		try{
			double enteredValue = Double.valueOf(amount.getEnteredValue().trim());
			logger.info("enteredvalue = "+enteredValue+" type "+type);
			if(enteredValue <= 0 && !type.equals("Void")){//APP-4766
				logger.info("enteredvalue less than 1");
				status = new Status("111557", PropertyUtil.getErrorMessage(111557, OCConstants.ERROR_LOYALTY_FLAG), 
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			
		}catch(Exception e){
			logger.info("Entered value validation failed...");
		}
		logger.info("Completed validateEnteredValue method <<<");
		return status;
		
	}
	
	private LoyaltyProgramTier findTier(Long programId, Long contactId, Long userId, ContactsLoyalty contactsLoyalty) throws Exception {

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

			Double totPurchaseValue = null;

			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);
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
	private String generateOtpCode() {
		
		final long MAX_NUMBER = 9999L;
		final long MIN_NUMBER = 1000L;
		String otpCode = "";
		String randNoStr = "";
		Long randNo = Long.valueOf(new Random().nextLong());
		
		if(randNo.toString().startsWith("-")) {
			randNoStr = randNo.toString().replace("-", "");
		}
		else {
			randNoStr = randNo.toString();
		}
		
		randNo = Long.valueOf((Long.parseLong(randNoStr) % (MAX_NUMBER-MIN_NUMBER)) + MIN_NUMBER);
		otpCode = randNo.toString();
		return otpCode;
	}
	

private Status sendOTPCode(Users user, String phone, String otpCode) {
		
		Status status = null;
		
		try{
			
			//APP-3997
			//get the OTP route if its India
			OCSMSGateway ocGateway = user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA) ? GatewayRequestProcessHelper.getOcSMSGateway(user, 
					SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()), true)
					:GatewayRequestProcessHelper.getOcSMSGateway(user, 
					SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
			
			CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)ServiceLocator.getInstance().getBeanByName("captiwayToSMSApiGateway");
			if(!ocGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocGateway, 1)) {
				status = new Status("111551", PropertyUtil.getErrorMessage(111551, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			
			if(!(((user.getSmsCount() == null ? 0 : user.getSmsCount()) - (user.getUsedSmsCount() == null ? 0 : user.getUsedSmsCount())) >=  1)) {
				status = new Status("111551", PropertyUtil.getErrorMessage(111551, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			String senderId = ocGateway.getSenderId();
			UserSMSSenderIdDao userSMSSenderIdDao = (UserSMSSenderIdDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USER_SMS_SENDER_ID_DAO); 
			List<UserSMSSenderId> retList =  userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(), ocGateway.getAccountType());
			if(retList != null && !retList.isEmpty()) {
				senderId = retList.get(0).getSenderId();
			}
			String msgContent= "";
			String messageHeader = findMessageHeader(user);
			AutoSMS autoSms = null;

		

			AutoSMSDao autosmsdao=(AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			Long userid=user.getUserId();
			String Message="";
			
			logger.info("phone is "+phone);
		
			logger.info("entered into db flow");
			Message= PropertyUtil.getPropertyValueFromDB("RedemptionOTPSMSTemplates");
			Message = Message.replace("[OTP]",otpCode);
			Message = Message.replace("[BrandName]",senderId);
	
			
			//Contacts contacts = contactsLoyalty.getContact();
			LoyaltyProgramService ltyprgrmservice=new LoyaltyProgramService();
			Long programID =  ltyprgrmservice.getCustomizedOTPEnabledProgList(user.getUserId(), true);
			if(programID!=null) {
				LoyaltyAutoCommGenerator ltyautocom=new LoyaltyAutoCommGenerator();
				LoyaltyAutoCommDao loyaltyAutoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
				
				//LoyaltyAutoComm loyaltyAutoComm = loyaltyAutoCommDao.findById(contactsLoyalty.getProgramId());
				//LoyaltyAutoComm loyaltyAutoComm =new LoyaltyAutoComm();
				LoyaltyAutoComm autoComm=ltyprgrmservice.getAutoCommunicationObj(programID);
				logger.info("the msg "+autoComm);
				
				Long templateId=autoComm.getRedemptionOtpAutoSmsTmpltId();
				autoSms = autosmsdao.getAutoSmsTemplateById(templateId);

				if(templateId !=null) {
					
					logger.info("the templateId is "+templateId);
					logger.info("the autosms senderid is "+templateId);
					senderId=autoSms.getSenderId();
					Message= ltyautocom.sendRedemptionOtpSMSTemplate(templateId, user,  phone, otpCode);
					Message = Message.replace("[OTP]",otpCode);
					Message=Message.replace("[Org Name]",user.getUserOrganization().getOrganizationName());
					
					
				}
			}

		
			
			logger.info("Final sms otp msg is"+Message);

			
			
			if(messageHeader.equals(Constants.STRING_NILL) ){
				msgContent= Message;
			}
			else{
				msgContent= messageHeader+Message;
			}
					
		/*if(user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA)) {
			
			if(messageHeader == "" ){
					
			
					msgContent= PropertyUtil.getPropertyValueFromDB("RedemptionOTPSMSTemplates");
					msgContent = msgContent.replace("[OTP]",otpCode);
					msgContent = msgContent.replace("[BrandName]",ocGateway.getSenderId());
					logger.info("entering the msgcontent "+msgContent);
			
				
				}
				
			else{
				
				
					msgContent= messageHeader+PropertyUtil.getPropertyValueFromDB("RedemptionOTPSMSTemplates");
					msgContent = msgContent.replace("[OTP]",otpCode);
					msgContent = msgContent.replace("[BrandName]",ocGateway.getSenderId());
				
		
			
		}
	}	else {
				if(messageHeader == ""){
					
					
					
						msgContent= " Hi! Please use "+otpCode+" as your OTP code for redemption. Happy shopping!";
				
					
				
				//	Hi! Please use [OTP] as your OTP code for redemption. Happy shopping!
				
						//	[BrandName]
				
				}else {
				
						msgContent= messageHeader+" Hi! Please use "+otpCode+" as your OTP code for redemption. Happy shopping!";
				}
	}	*/	
			
			
			try {
				captiwayToSMSApiGateway.sendSingleSms(ocGateway, msgContent,
						phone, senderId,null);
				/**
				 * Update the Used SMS count
				 */
				try{
					UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
					usersDaoForDML.updateUsedSMSCount(user.getUserId(), 1);
				}catch(Exception exception){
					logger.error("Exception while updating the Used SMS count",exception);
				}
				
				/**
				 * Update Sms Queue
				 */
				SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
				smsQueueHelper.updateSMSQueue(phone,msgContent,Constants.SMS_MSG_TYPE_OTPSMS, user, senderId);
			} catch (Exception e) {
				logger.error("Exception While sending OTP SMS ",e);
			}
			logger.debug(">>>>>>> Started LoyaltyOTPOCServiceImpl :: sendOTPCode <<<<<<< ");

			return null;
		}catch(Exception e){
			logger.error("Exception in sending otp code ...", e);
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG)+" "+e.getMessage(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		}
	// send opt to email
    private Status sendOTPCodeToEmail(Users user, String email, String otpCode ){
		
		Status status = null;
		EmailQueueDaoForDML emailQueueDaoForDML = null;
		try{
			
			if(!(((user.getEmailCount() == null ? 0 : user.getEmailCount()) - (user.getUsedEmailCount() == null ? 0 : user.getUsedEmailCount())) >=  1)) {
				status = new Status("111551", PropertyUtil.getErrorMessage(111551, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			String messageHeader = Constants.STRING_NILL;
		
			String msgContent="";
		
			logger.info("entered into db flow");
			msgContent= PropertyUtil.getPropertyValueFromDB("OTPTemplate");
			msgContent = msgContent.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			msgContent = msgContent.replace("[OTPCode]",otpCode);

		
				//Contacts contacts = contactsLoyalty.getContact();
			LoyaltyProgramService ltyprgrmservice=new LoyaltyProgramService();
			Long programID =  ltyprgrmservice.getCustomizedOTPEnabledProgList(user.getUserId(), true);
			if(programID !=null) {
				LoyaltyAutoCommGenerator ltyautocom=new LoyaltyAutoCommGenerator();
			//	LoyaltyAutoCommDao loyaltyAutoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
				
				//LoyaltyAutoComm loyaltyAutoComm = loyaltyAutoCommDao.findById(contactsLoyalty.getProgramId());
				//LoyaltyAutoComm loyaltyAutoComm =new LoyaltyAutoComm();
				LoyaltyAutoComm autoComm=ltyprgrmservice.getAutoCommunicationObj(programID);
				logger.info("the msg "+autoComm);
				
				Long templateId=autoComm.getRedemptionOtpAutoEmailTmplId();
				if(templateId !=null) {
					
					logger.info("the templateId is "+templateId);

					msgContent= ltyautocom.sendRedemptionOtpTemplate(templateId, user, email, otpCode);
					msgContent =msgContent.replace("[OTP]",otpCode);
					msgContent=msgContent.replace("[Org Name]",user.getUserOrganization().getOrganizationName());
					
					
				}
			}

		
		logger.info(" final emailotpmsg is"  +msgContent);
		
		
	
			/*if(messageHeader!=null)
				msgContent=messageHeader+msgContent;*/
		
			EmailQueue otpEmail = new EmailQueue("OTP for verification",msgContent,Constants.EQ_TYPE_OTP_MAIL,  "Sent",email,MyCalendar.getNewCalendar(),user);
			  emailQueueDaoForDML.saveOrUpdate(otpEmail);
				UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
				usersDaoForDML.updateUsedEmailCount(user.getUserId(), 1);
			
				
				 if(Constants.VMTA_SENDGRIDAPI.equalsIgnoreCase(user.getVmta().getVmtaName())){	
						try {
							 

							if(user != null) {
								/*messageHeader =  "{\"unique_args\": {\"userId\": \""+ user.getUserId() +"\" ,\"EmailType\" : \""+Constants.EQ_TYPE_DIGITALRECIEPT +"\",\"sentId\" : \""+drSent.getId()+"\" ,\"ServerName\": \""+ serverName +"\" }}";*/
								
								JSONObject messageHEaderObject = new JSONObject();
								JSONArray toEmailArray = new JSONArray();
								JSONObject uniqueArgsObject = new JSONObject();
								
								toEmailArray.add( email);
								
								uniqueArgsObject.put("ServerName", PropertyUtil.getPropertyValue("schedulerIp"));
								uniqueArgsObject.put("Email", Constants.EQ_TYPE_OTP_MAIL);
								uniqueArgsObject.put("EQID", ""+otpEmail.getId());
								uniqueArgsObject.put("userId", user.getUserId());
								messageHEaderObject.put("unique_args", uniqueArgsObject);
								messageHEaderObject.put("to", toEmailArray);
								
								messageHeader = messageHEaderObject.toString();
								
							}
							logger.debug("SENDING THROUGH sendGridAPI ...>>>>>>>>>>>>");
							ExternalSMTPDigiReceiptSender externalSMTPSender =  new ExternalSMTPDigiReceiptSender();
							String htmlContent = otpEmail.getMessage();
							String textContent = otpEmail.getMessage(); 
							String jobId = Constants.EQ_TYPE_OTP_MAIL;
							String from = user.getUserOrganization().getOrganizationName()+ "<" +user.getEmailId()+ ">" ;
							String replyTo = from;
							String subject = otpEmail.getSubject();
							//externalSMTPSender.submitDigitalreceipt(messageHeader, htmlContent, textContent, from, subject, toEmailId);
							externalSMTPSender.submitDigitalreceiptWithReplyTo(messageHeader, htmlContent, textContent, from, subject, email, replyTo);
						
						} catch (Exception e) {
							
							logger.debug("Exception while sending through sendGridAPI .. returning ",e);
							
						}
					}
			logger.debug(">>>>>>> Started LoyaltyOTPOCServiceImpl :: sendOTPCode <<<<<<< ");

			return null;
		}catch(Exception e){
			logger.error("Exception in sending otp code ...", e);
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG)+" "+e.getMessage(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
	}
	private String findMessageHeader(Users user){
		
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
} 
