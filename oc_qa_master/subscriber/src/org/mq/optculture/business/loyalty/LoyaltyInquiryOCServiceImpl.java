package org.mq.optculture.business.loyalty;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyMemberSessionID;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
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
import org.mq.optculture.data.dao.LoyaltySettingsDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.AdditionalInfo;
import org.mq.optculture.model.ocloyalty.OTPRedeemLimit;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryResponse;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.model.updatecontacts.MobileAppPreferences;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

/**
 * OptCulture Loyalty Program --- loyalty inquiry process handler.
 *
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyInquiryOCServiceImpl implements LoyaltyInquiryOCService {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private String ltyImgUrl = PropertyUtil.getPropertyValue("LoyaltyImageServerUrl");
	private String ltyParentDirectory = PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim();
	private String imgUrl = PropertyUtil.getPropertyValue("ImageServerUrl")+"UserData";
	private String userParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory").trim();
	/**
	 * BaseService Request called by rest service controller.
	 * @return BaseResponseObject
	 */
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		
		logger.info(" Inquiry base oc service called ...");
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		Gson gson = new Gson();
		LoyaltyInquiryResponse inquiryResponse = null;
		LoyaltyInquiryRequest inquiryRequest = null;
		BaseResponseObject responseObject = null;
		String responseJson = null;
		
		if(serviceRequest == null || !OCConstants.LOYALTY_SERVICE_ACTION_INQUIRY.equals(serviceRequest)){
			
			Status status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			inquiryResponse = new LoyaltyInquiryResponse();
			inquiryResponse.setStatus(status);
			
			responseJson = gson.toJson(inquiryResponse);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}
		
		try{
			inquiryRequest = gson.fromJson(requestJson, LoyaltyInquiryRequest.class);
		}catch(Exception e){
			Status status = new Status("101001", ""+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			inquiryResponse = new LoyaltyInquiryResponse();
			inquiryResponse.setStatus(status);
			responseJson = gson.toJson(inquiryResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid JSON ");
			return responseObject;
			
		}
		
		try{
			LoyaltyInquiryOCService loyaltyInquiryOCService = (LoyaltyInquiryOCService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_INQUIRY_OC_BUSINESS_SERVICE);
			inquiryResponse = loyaltyInquiryOCService.processInquiryRequest(inquiryRequest, baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate());
			responseJson = gson.toJson(inquiryResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
		}catch(Exception e){
			logger.error("Exception in loyalty inquiry base service.",e);
			throw new BaseServiceException("Server Error.");
		}
		return responseObject;
	}
	
	/**
	 * Handles the complete process of Loyalty Inquiry.
	 * 
	 * @param inquiryRequest
	 * @return inquiryResponse
	 * @throws BaseServiceException
	 */
	@Override
	public LoyaltyInquiryResponse processInquiryRequest(LoyaltyInquiryRequest inquiryRequest, String transactionId, 
			String transactionDate) throws BaseServiceException {
		
		logger.info("processInquiryRequest method called...");
		
		LoyaltyInquiryResponse inquiryResponse = null;
		Status status = null;
		Users user = null;
		
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(inquiryRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(inquiryRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		responseHeader.setSourceType(inquiryRequest.getHeader().getSourceType() != null && 
				!inquiryRequest.getHeader().getSourceType().trim().isEmpty() ? inquiryRequest.getHeader().getSourceType().trim() : "");
		/*responseHeader.setSubsidiaryNumber(inquiryRequest.getHeader().getSubsidiaryNumber() != null && !inquiryRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? inquiryRequest.getHeader().getSubsidiaryNumber().trim() : "");
		responseHeader.setReceiptNumber( "");
		responseHeader.setReceiptAmount("");*/
		
		
		try{
			
			//user = getUser(inquiryRequest.getUser().getUserName(), inquiryRequest.getUser().getOrganizationId(), inquiryRequest.getUser().getToken());
			status = validateInquiryJsonData(inquiryRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
				return inquiryResponse;
			}
			
			
			user = getUser(inquiryRequest.getUser().getUserName(), inquiryRequest.getUser().getOrganizationId(),
					inquiryRequest.getUser().getToken());
			if(user == null){
				status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
				return inquiryResponse;
			}
			if(!user.isEnabled()){
				status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
				return inquiryResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
				return inquiryResponse;
			}
			
			//validate sessionID for mobileapp
			String sourceType = inquiryRequest.getHeader().getSourceType();
			if(sourceType != null && !sourceType.isEmpty() && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) ){
				String sessionID = inquiryRequest.getUser().getSessionID();
				
				if(sessionID == null || sessionID.isEmpty()){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
				}
				LoyaltyMemberSessionID loyaltyMemberSessionID = LoyaltyProgramHelper.validateSessionID(sessionID);
				if(loyaltyMemberSessionID == null){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
				}
				
				String cardNumber = LoyaltyProgramHelper.getCardFromSesstionID(sessionID);
				if(inquiryRequest.getMembership().getCardNumber() != null && inquiryRequest.getMembership().getCardNumber().trim().length() > 0 && 
						!inquiryRequest.getMembership().getCardNumber().trim().equals(cardNumber)){
					status = new Status("800029", PropertyUtil.getErrorMessage(800029, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
					
				}
				
			}
			
			//by pravendra
			if(OCConstants.LOYALTY_SERVICE_TYPE_OC.equals(user.getloyaltyServicetype())){
				//updating subsidiary to request	
				if(inquiryRequest.getHeader().getStoreNumber() != null && !inquiryRequest.getHeader().getStoreNumber().isEmpty()){
					OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
					UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
					Long domainId = userDao.findDomainByUserId(user.getUserId());
					if(domainId!=null){
					OrganizationStores orgStores = organizationStoresDao.findOrgByDomain(user.getUserOrganization().getUserOrgId(), domainId, inquiryRequest.getHeader().getStoreNumber());
					inquiryRequest.getHeader().setSubsidiaryNumber(orgStores!=null ? orgStores.getSubsidiaryId() : null);
				 }
				}
			}
			LoyaltyCards loyaltyCard = null;
//			LoyaltyProgram loyaltyProgram = null;
//			LoyaltyProgramTier tier = null;
			
			if(inquiryRequest.getMembership().getCardNumber() != null 
					&& inquiryRequest.getMembership().getCardNumber().trim().length() > 0){
				
				logger.info("Inquiry by card number >>>");
				
				String cardNumber = "";
				String cardLong = null;
				
				/*if(inquiryRequest.getMembership().getCardNumber().trim().length() != 16){
					status = new Status("100107", PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
				}*/
				cardLong = OptCultureUtils.validateOCLtyCardNumber(inquiryRequest.getMembership().getCardNumber().trim());
				if(cardLong == null){
					String msg = PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getMembership().getCardNumber().trim()+".";
					status = new Status("100107", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
				}
				cardNumber = ""+cardLong;
				inquiryRequest.getMembership().setCardNumber(cardNumber);
				
				logger.info("Card Number after parsing... "+cardNumber);
				
				loyaltyCard = findLoyaltyCardByUserId(cardNumber, user.getUserId());
				
				if(loyaltyCard == null){//temporary hot fix
					String validStatus = LoyaltyProgramHelper.validateMembershipMobile(cardNumber);
					if(OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID.equals(validStatus)){
						String msg = PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getMembership().getPhoneNumber().trim();
						status = new Status("111554", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
						return inquiryResponse;
					}
					String phoneNumber=cardNumber;//inquiryRequest.getMembership().getPhoneNumber().trim();
					ContactsLoyalty contactsLoyalty = findContactLoyaltyByMobile(phoneNumber, user);
					if(contactsLoyalty == null){
						String msg = PropertyUtil.getErrorMessage(111519, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber.trim();
						status = new Status("111519", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
						return inquiryResponse;
					}
					
					return mobileBasedInquiry(contactsLoyalty, responseHeader, inquiryRequest, user);
					/*status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;*/
				}
				
				if(OCConstants.LOYALTY_CARD_STATUS_INVENTORY.equalsIgnoreCase(loyaltyCard.getStatus())){
					status = new Status("111537", PropertyUtil.getErrorMessage(111537, OCConstants.ERROR_LOYALTY_FLAG)+" "+cardNumber+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
				}
			
				return cardBasedInquiry(loyaltyCard, inquiryRequest, user, responseHeader);
				
			}
			else if(inquiryRequest.getMembership().getPhoneNumber() != null 
					&& inquiryRequest.getMembership().getPhoneNumber().trim().length() > 0){
				
				String validStatus = LoyaltyProgramHelper.validateMembershipMobile(inquiryRequest.getMembership().getPhoneNumber().trim());
				if(OCConstants.LOYALTY_MEMBERSHIP_MOBILE_INVALID.equals(validStatus)){
					String msg = PropertyUtil.getErrorMessage(111554, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getMembership().getPhoneNumber().trim();
					status = new Status("111554", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
				}
				String phoneNumber=inquiryRequest.getMembership().getPhoneNumber().trim();
				ContactsLoyalty contactsLoyalty = findContactLoyaltyByMobile(phoneNumber, user);
				if(contactsLoyalty == null){
					String msg = PropertyUtil.getErrorMessage(111519, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getMembership().getPhoneNumber().trim();
					status = new Status("111519", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
				}
				
				return mobileBasedInquiry(contactsLoyalty, responseHeader, inquiryRequest, user);
				
			}
			//Addition of CustomerId flow.
			else if(inquiryRequest.getCustomer().getCustomerId() != null 
					&& !inquiryRequest.getCustomer().getCustomerId().trim().isEmpty())
			{
				ContactsLoyalty contactsLoyalty = null;
				List<ContactsLoyalty> enrollList = findEnrollListByCustomerId(inquiryRequest.getCustomer().getCustomerId(), user.getUserId());
				
				if(enrollList == null){
					status = new Status("111615", PropertyUtil.getErrorMessage(111615, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getCustomer().getPhone().trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
				}
		
				logger.info("enrollList===>"+enrollList);
				
				
				if(enrollList.size() > 1){
					
					status = new Status("111597", PropertyUtil.getErrorMessage(111597, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getCustomer().getPhone().trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
					
				}
				else {
					
					
					contactsLoyalty = enrollList.get(0);
				}
					logger.info("contactsLoyalty = "+contactsLoyalty);
					
					if(contactsLoyalty.getMembershipType()==null)  {
						logger.info("INVALID LOYALTY MEMBERSHIP CARD TYPE .... LOOKINTO THIS...");
					}
					if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(contactsLoyalty.getMembershipType())){
						return mobileBasedInquiry(contactsLoyalty, responseHeader, inquiryRequest, user);
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
						loyaltyCard = findLoyaltyCardByUserId(""+contactsLoyalty.getCardNumber(), user.getUserId());
						return cardBasedInquiry(loyaltyCard, inquiryRequest, user, responseHeader);
					}
					else{
						logger.info("INVALID LOYALTY MEMBERSHIP CARD TYPE .... LOOKINTO THIS...");
					}			
			
				}
			
			
			else if(inquiryRequest.getCustomer().getPhone() != null 
					&& !inquiryRequest.getCustomer().getPhone().trim().isEmpty()){
				
				ContactsLoyalty contactsLoyalty = null;
				
				List<ContactsLoyalty> enrollList = findEnrollListByMobile(inquiryRequest.getCustomer().getPhone(), user.getUserId());
				
				if(enrollList == null){
					status = new Status("111524", PropertyUtil.getErrorMessage(111524, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
				}
				
				List<Contacts> dbContactList = null;
				Contacts dbContact = null;
				
				if(enrollList.size() > 1){
					logger.info("Found more than 1 enrollments");
					Contacts jsonContact = prepareContactFromJsonData(inquiryRequest.getCustomer(), user.getUserId());
					jsonContact.setUsers(user);
					dbContactList = findOCContact(jsonContact, user.getUserId(), user);
					
					if(dbContactList == null){
						logger.info(" request contact not found in OC");
						
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList,inquiryRequest);
						
						status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, matchedCustomers, status);
						return inquiryResponse;
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
							
							List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList,inquiryRequest);
							
							status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, matchedCustomers, status);
							return inquiryResponse;
						}
					}
					else{
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList,inquiryRequest);
						
						status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, matchedCustomers, status);
						return inquiryResponse;
					}
					
				}
				else{
					//logger.info("loyalty found in else case....");
					contactsLoyalty = enrollList.get(0);
				}
				logger.info("contactsLoyalty = "+contactsLoyalty);
				
				if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(contactsLoyalty.getMembershipType())){
					return mobileBasedInquiry(contactsLoyalty, responseHeader, inquiryRequest, user);
				}
				else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
					loyaltyCard = findLoyaltyCardByUserId(""+contactsLoyalty.getCardNumber(), user.getUserId());
					return cardBasedInquiry(loyaltyCard, inquiryRequest, user, responseHeader);
				}
				else{
					logger.info("INVALID LOYALTY MEMBERSHIP CARD TYPE .... LOOKINTO THIS...");
				}
				
			}
			else if(inquiryRequest.getCustomer().getEmailAddress() != null 
					&& !inquiryRequest.getCustomer().getEmailAddress().trim().isEmpty()){
				ContactsLoyalty contactsLoyalty = null;
				Contacts contacts =null;
				//List<ContactsLoyalty> enrollList= new ArrayList<ContactsLoyalty>();
				List<ContactsLoyalty> enrollList = findEnrollListByEmail(inquiryRequest.getCustomer().getEmailAddress(), user.getUserId());
				
				if(enrollList == null){
					status = new Status("111598", PropertyUtil.getErrorMessage(111598, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getCustomer().getPhone().trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
				}
			
			/*	while(enrollResult.next())
				{
					contacts=new Contacts();
					contacts.setContactId(enrollResult.getLong("contact_id") == 0l ? 0l : enrollResult.getLong("contact_id"));
					contactsLoyalty=new ContactsLoyalty();
					contactsLoyalty.setContact(contacts);
					contactsLoyalty.setCardNumber(enrollResult.getString("card_number") == null ? "" : enrollResult.getString("card_number"));
					contactsLoyalty.setMembershipType(enrollResult.getString("membership_type") == null ? "" : enrollResult.getString("membership_type"));
					enrollList.add(contactsLoyalty);
				
				}
				*/
				
				logger.info("enrollList===>"+enrollList);
				
				List<Contacts> dbContactList = null;
				Contacts dbContact = null;
				
				if(enrollList.size() > 1){
					
					status = new Status("111597", PropertyUtil.getErrorMessage(111597, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getCustomer().getPhone().trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
					
					
					/*
					logger.info("Found more than 1 enrollments");
					Contacts jsonContact = prepareContactFromJsonData(inquiryRequest.getCustomer(), user.getUserId());
					jsonContact.setUsers(user);
					dbContactList = findOCContact(jsonContact, user.getUserId());
					if(dbContactList == null){
						logger.info(" request contact not found in OC");
						
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList,inquiryRequest);
						
						status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, matchedCustomers, status);
						return inquiryResponse;
					}
					else if(dbContactList.size() == 1) {

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
							
							List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList,inquiryRequest);
							
							status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG)+" "+inquiryRequest.getCustomer().getPhone().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, matchedCustomers, status);
							return inquiryResponse;
							}
					
							}
					else{
						status = new Status("111525", PropertyUtil.getErrorMessage(111525, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
						return inquiryResponse;
					}*/
					
				}
				else {
					
					
					contactsLoyalty = enrollList.get(0);
				}
					logger.info("contactsLoyalty = "+contactsLoyalty);
					
					if(contactsLoyalty.getMembershipType()==null)  {
						logger.info("INVALID LOYALTY MEMBERSHIP CARD TYPE .... LOOKINTO THIS...");
					}
					if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(contactsLoyalty.getMembershipType())){
						return mobileBasedInquiry(contactsLoyalty, responseHeader, inquiryRequest, user);
					}
					else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
						loyaltyCard = findLoyaltyCardByUserId(""+contactsLoyalty.getCardNumber(), user.getUserId());
						return cardBasedInquiry(loyaltyCard, inquiryRequest, user, responseHeader);
					}
					else{
						logger.info("INVALID LOYALTY MEMBERSHIP CARD TYPE .... LOOKINTO THIS...");
					}			
			
				}
		
		
			
			
		
			
		}catch(Exception e){
			logger.error("Exception in loyalty inquiry service", e);
			throw new BaseServiceException("Loyalty Inquiry Request Failed");
		}
		return inquiryResponse;
	}
	
	
	
	/**
	 * Validates existence of JSON objects in the request, such as userdetails, inquiryinfo. 
	 * 
	 * @param inquiryRequest
	 * @return StatusInfo
	 * @throws Exception
	 */
	private Status validateInquiryJsonData(LoyaltyInquiryRequest inquiryRequest) throws Exception{
		Status status = null;
		if(inquiryRequest == null ){
			status = new Status(
					"101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		String reqSource = inquiryRequest.getHeader().getSourceType();
		if(inquiryRequest.getUser() == null  ){
			status = new Status(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(inquiryRequest.getMembership() == null){
			status = new Status(
					"101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		/*if(reqSource != null && reqSource.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)){
			status = new Status("", PropertyUtil.getErrorMessage(800002, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
			
		}*/
		
		
		if((reqSource!= null && !reqSource.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) && 
				!reqSource.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)) &&( inquiryRequest.getUser().getUserName() == null || inquiryRequest.getUser().getUserName().trim().length() <=0 || 
				inquiryRequest.getUser().getOrganizationId() == null || inquiryRequest.getUser().getOrganizationId().trim().length() <=0 || 
				inquiryRequest.getUser().getToken() == null || inquiryRequest.getUser().getToken().trim().length() <=0)) {
			status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(reqSource!= null && (reqSource.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || reqSource.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)) && 
				(inquiryRequest.getUser().getOrganizationId() == null || inquiryRequest.getUser().getOrganizationId().trim().length() <=0  || 
				inquiryRequest.getUser().getUserName() == null || inquiryRequest.getUser().getUserName().trim().length() <=0)){
			status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
			
		}
		
		if(reqSource!= null && !reqSource.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) && !reqSource.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP) &&
				(inquiryRequest.getHeader().getStoreNumber() == null || inquiryRequest.getHeader().getStoreNumber().length() <= 0)){
			status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		//removed the validation on 14th-Jan
		/*if(inquiryRequest.getAmount()!=null && inquiryRequest.getAmount().getReceiptAmount()!= null && !inquiryRequest.getAmount().getReceiptAmount().trim().isEmpty()//APP-1131
				&& Double.parseDouble(inquiryRequest.getAmount().getReceiptAmount())<=0){
			status = new Status("111592", PropertyUtil.getErrorMessage(111592, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/
		
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
		Users user = null;
		if(userToken != null && !userToken.isEmpty()) {
			user = usersDao.findUserByToken(completeUserName, userToken);
		}else{
			user = usersDao.findByUsername(completeUserName);
		}
		return user;
	}
	
	private List<Balance> prepareBalancesObject(ContactsLoyalty loyalty) throws Exception{
		LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);		List<Balance> balancesList = null;
		Balance pointBalances = null;
		Balance amountBalances = null;
		Balance giftBalances = null;
		balancesList = new ArrayList<Balance>();
		
		pointBalances = new Balance();
		pointBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		pointBalances.setValueCode(OCConstants.LOYALTY_TYPE_POINTS);
		pointBalances.setExpire("");
		//Special reward changes
		Long loyaltyBalance = loyalty.getLoyaltyBalance() == null ? 0l : loyalty.getLoyaltyBalance().intValue();
		Long valueCodeBalance = (long) loyaltyBalanceDao.findLoyaltyBalanceByLoyaltyId(loyalty.getLoyaltyId(),loyalty.getUserId());
		valueCodeBalance = valueCodeBalance==null?0l:valueCodeBalance;
		loyaltyBalance = loyaltyBalance+valueCodeBalance; 
		pointBalances.setAmount(loyaltyBalance+"");
		pointBalances.setDifference("");		
		amountBalances = new Balance();
		amountBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		amountBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		amountBalances.setExpire("");
		//amountBalances.setAmount(loyalty.getGiftcardBalance() == null ? "" : ""+loyalty.getGiftcardBalance());
		amountBalances.setDifference("");
		if(loyalty.getGiftcardBalance() == null){
			amountBalances.setAmount("");
		}
		else{
			//double value = new BigDecimal(loyalty.getGiftcardBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			//double value = new BigDecimal(loyalty.getGiftcardBalance()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
			double value = Double.parseDouble(Utility.truncateUptoTwoDecimal(loyalty.getGiftcardBalance()));
			amountBalances.setAmount(""+value);
		}
		
		giftBalances = new Balance();
		giftBalances.setType(OCConstants.LOYALTY_TYPE_GIFT);
		giftBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		//giftBalances.setAmount(loyalty.getGiftBalance() == null ? "" : ""+loyalty.getGiftBalance());
		if(loyalty.getGiftBalance() == null){
			giftBalances.setAmount("");
		}
		else{
			//double value = new BigDecimal(loyalty.getGiftBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			//double value = new BigDecimal(loyalty.getGiftBalance()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
			double value = Double.parseDouble(Utility.truncateUptoTwoDecimal(loyalty.getGiftBalance()));
			giftBalances.setAmount(""+value);
		}
		giftBalances.setDifference("");
		
		balancesList.add(pointBalances);
		balancesList.add(amountBalances);
		balancesList.add(giftBalances);
		
		return balancesList;
	}
	
	private LoyaltyInquiryResponse prepareInquiryResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance, AdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers,
			Status status) throws BaseServiceException {
		LoyaltyInquiryResponse inquiryResponse = new LoyaltyInquiryResponse();
		inquiryResponse.setHeader(header);
		
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
			additionalInfo = new AdditionalInfo();
			additionalInfo.setOtpEnabled("");
			/*OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
			otpRedeemLimit.setAmount("");
			otpRedeemLimit.setValueCode("");*/
			List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
			//otpRedeemLimitlist.add(otpRedeemLimit);
			additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
			additionalInfo.setPointsEquivalentCurrency("");
			additionalInfo.setTotalRedeemableCurrency("");
			additionalInfo.setLifeTimePurchaseValue("");
			additionalInfo.setCompanyLogo("");
			additionalInfo.setBannerImage("");
			additionalInfo.setFontName("");
			additionalInfo.setFontURL("");
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		
		inquiryResponse.setMembership(membershipResponse);
		inquiryResponse.setBalances(balances);
		inquiryResponse.setHoldBalance(holdBalance);
		inquiryResponse.setAdditionalInfo(additionalInfo);
		inquiryResponse.setMatchedCustomers(matchedCustomers);
		inquiryResponse.setStatus(status);
		return inquiryResponse;
	}
	
	
	private LoyaltyCards findLoyaltyCardByUserId(String cardNumber, Long userId) throws Exception {
		
		LoyaltyCardsDao loyaltyCardDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		return loyaltyCardDao.findByCardNoAnduserId(cardNumber, userId);
		
	}
	
	private ContactsLoyalty findContactLoyalty(String cardNumber, Long programId, Long userId) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findByProgram(cardNumber, programId, userId);
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
	
	private Contacts findCustomerByCid(Long cid, Long userId) throws Exception {
		
		ContactsDao contactDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactDao.findById(cid);
	}
	
	private ContactsLoyalty findContactsLoyaltyByCardNumberANDUser(Long userId,String cardNumber) {
		ContactsLoyaltyDao loyaltyDao = null;
		try {
			loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("findContactsLoyaltyByCardNumberANDUser===>"+e);
		}
		return loyaltyDao.findContLoyaltyByCardId(userId,cardNumber);	
	}
	
	
	private Customer prepareCustomer(Contacts contact) throws Exception {
		
		Customer customer = new Customer();
		
		customer.setAddressLine1(contact.getAddressOne() == null ? "" : contact.getAddressOne());
		customer.setAddressLine2(contact.getAddressTwo() == null ? "" : contact.getAddressTwo());
		customer.setAnniversary(contact.getAnniversary() == null ? "" : ""+contact.getAnniversary());
		customer.setBirthday(contact.getBirthDay() == null ? "" : ""+contact.getBirthDay());
		customer.setCity(contact.getCity() == null ? "" : ""+contact.getCity());
		customer.setCountry(contact.getCountry() == null ? "" : ""+contact.getCountry());
		customer.setCustomerId(contact.getExternalId() == null ? "" : ""+contact.getExternalId());
		customer.setEmailAddress(contact.getEmailId() == null ? "" : ""+contact.getEmailId());
		customer.setFirstName(contact.getFirstName() == null ? "" : ""+contact.getFirstName());
		customer.setGender(contact.getGender() == null ? "" : ""+contact.getGender());
		customer.setLastName(contact.getLastName() == null ? "" : ""+contact.getLastName());
		customer.setPhone(contact.getMobilePhone() == null ? "" : ""+contact.getMobilePhone());
		customer.setPostal(contact.getZip() == null ? "" : ""+contact.getZip());
		customer.setState(contact.getState() == null ? "" : ""+contact.getState());
		
		return customer;
		
	}
	
	private LoyaltyProgram findActiveMobileProgram(Long programId) throws Exception {
		
	 LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
	 return loyaltyProgramDao.findById(programId);
	}
	
	private List<ContactsLoyalty> findEnrollListByMobile(String mobile, Long userId) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByMobile(mobile, userId);
	}
	
	private List<ContactsLoyalty> findEnrollListByEmail(String Email, Long userId) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByEmail(Email, userId);
	}
	
	private List<ContactsLoyalty> findEnrollListByCustomerId(String customerId, Long userId) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.getLoyaltyListByCustId(customerId.trim(), userId);
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
		//logger.info("Entered findOCContact method >>>>");
		POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(userId, Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
		List<Contacts> dbContactList = contactsDao.findMatchedContactListByUniqPriority(priorMap, jsonContact, userId, user);
		//logger.info("Exited findOCContact method >>>>");
		return dbContactList;
	}
	
	private List<MatchedCustomer> prepareMatchedCustomers(List<ContactsLoyalty> enrollList,LoyaltyInquiryRequest inquiryRequest) throws Exception {
		
		Contacts contact = null;
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		List<MatchedCustomer> matchedCustList = new ArrayList<MatchedCustomer>();
		MatchedCustomer matchedCustomer = null;
		
		for(ContactsLoyalty loyalty : enrollList){
			if(loyalty.getContact() != null && loyalty.getContact().getContactId() != null){
				contact = contactsDao.findById(loyalty.getContact().getContactId());
				if(contact != null){
					matchedCustomer = new MatchedCustomer();
					if(inquiryRequest.getHeader().getSourceType() != null && 
							!inquiryRequest.getHeader().getSourceType().isEmpty() && 
							(inquiryRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) ||
									inquiryRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){
						MobileAppPreferences mobileAppPreferences =new MobileAppPreferences(); 
						mobileAppPreferences.setLanguage(contact.getLanguage() == null ? "": contact.getLanguage().trim());
						mobileAppPreferences.setPushNotifications (contact.getPushNotification() == null ? "": contact.getPushNotification().toString());
					matchedCustomer.setMobileAppPreferences(mobileAppPreferences);
					matchedCustomer.setInstanceId(contact.getInstanceId() == null ? "": contact.getInstanceId().toString());//APP-1775
					}
					
						
					matchedCustomer.setMembershipNumber(""+loyalty.getCardNumber());
					matchedCustomer.setFirstName(contact.getFirstName() == null ? "" : contact.getFirstName().trim());
					matchedCustomer.setLastName(contact.getLastName() == null ? "" : contact.getLastName().trim());
					matchedCustomer.setCustomerId(contact.getExternalId() == null ? "" : contact.getExternalId());
					matchedCustomer.setPhone(contact.getMobilePhone() == null ? "" : contact.getMobilePhone());
					matchedCustomer.setEmailAddress(contact.getEmailId() == null ? "" : contact.getEmailId());
					matchedCustList.add(matchedCustomer);
				}
			}
		}
		
		return matchedCustList;
		
	}
	
	private LoyaltyTransactionChild createSuccessfulTransaction(ContactsLoyalty loyalty, LoyaltyInquiryRequest inquiryRequest, 
			Long transactionId){
		
		LoyaltyTransactionChild transaction = null;
		try{
			
			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(transactionId);
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setUserId(loyalty.getUserId());
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setSubsidiaryNumber(inquiryRequest.getHeader().getSubsidiaryNumber() != null && !inquiryRequest.getHeader().getSubsidiaryNumber().trim().isEmpty() ? inquiryRequest.getHeader().getSubsidiaryNumber().trim() : null);
			
			transaction.setReceiptNumber(inquiryRequest.getHeader().getReceiptNumber() != null && !inquiryRequest.getHeader().getReceiptNumber().trim().isEmpty() ? inquiryRequest.getHeader().getReceiptNumber().trim() : null);
			transaction.setStoreNumber(inquiryRequest.getHeader().getStoreNumber());
			transaction.setEmployeeId(inquiryRequest.getHeader().getEmployeeId()!=null && !inquiryRequest.getHeader().getEmployeeId().trim().isEmpty() ? inquiryRequest.getHeader().getEmployeeId().trim():null);
			transaction.setTerminalId(inquiryRequest.getHeader().getTerminalId()!=null && !inquiryRequest.getHeader().getTerminalId().trim().isEmpty() ? inquiryRequest.getHeader().getTerminalId().trim():null);
			if(inquiryRequest.getHeader().getDocSID() != null && !inquiryRequest.getHeader().getDocSID().isEmpty())
				transaction.setDocSID(inquiryRequest.getHeader().getDocSID());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY);
			transaction.setSourceType(inquiryRequest.getHeader().getSourceType());
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			
			LoyaltyTransactionChildDao loyaltyTransChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransChildDao.saveOrUpdate(transaction);
			loyaltyTransChildDaoForDML.saveOrUpdate(transaction);
			
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		return transaction;
	}
	
	private Status validateStoreNumberExclusion(LoyaltyInquiryRequest inquiryRequest, 
			LoyaltyProgramExclusion loyaltyExclusion) throws Exception {
		
		Status status = null;
		if(loyaltyExclusion.getStoreNumberStr() != null && !loyaltyExclusion.getStoreNumberStr().trim().isEmpty()){
			String[] storeNumberArr = loyaltyExclusion.getStoreNumberStr().split(";=;");
			for(String storeNo : storeNumberArr){
				if(inquiryRequest.getHeader().getStoreNumber().trim().equals(storeNo.trim())){
					status = new Status("111532", PropertyUtil.getErrorMessage(111532, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
			}
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
	
	private List<MatchedCustomer> prepareMatchedCustomers(Customer customer, String membershipNumber){
		
		List<MatchedCustomer> matchedCustomers = new ArrayList<MatchedCustomer>();
		MatchedCustomer matchedCustomer = new MatchedCustomer();
		matchedCustomer.setCustomerId(customer.getCustomerId() == null ? "" : customer.getCustomerId());
		matchedCustomer.setEmailAddress(customer.getEmailAddress() == null ? "" : customer.getEmailAddress());
		matchedCustomer.setFirstName(customer.getFirstName() == null ? "" : customer.getFirstName());
		matchedCustomer.setLastName(customer.getLastName() == null ? "" : customer.getLastName());
		matchedCustomer.setMembershipNumber(membershipNumber == null ? "" : membershipNumber);
		matchedCustomer.setPhone(customer.getPhone() == null ? "" : customer.getPhone());
		
		matchedCustomers.add(matchedCustomer);
		return matchedCustomers;
	}
	
	
	private LoyaltyInquiryResponse mobileBasedInquiry(ContactsLoyalty contactsLoyalty, ResponseHeader responseHeader, 
			LoyaltyInquiryRequest inquiryRequest, Users userObj) throws Exception {
		
		LoyaltyInquiryResponse inquiryResponse = null;
		Status status = null;
		//Contacts contact = null;
		//Customer customer = null;
		
		/*if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
			contact = findCustomerByCid(contactsLoyalty.getContact().getContactId(), userId);
		}
		
		customer = prepareCustomer(contact);*/
		
		LoyaltyProgram loyaltyProgram = findActiveMobileProgram(contactsLoyalty.getProgramId());
		
		if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equals(loyaltyProgram.getStatus())){
			status = new Status("111522", PropertyUtil.getErrorMessage(111522, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
			return inquiryResponse;
		}

		LoyaltySettings loyaltySettings=null;
		UserOrganization userOrg=null;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		userOrg = usersDao.findByOrgId(userObj.getUserOrganization().getUserOrgId());
		LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
		loyaltySettings = loyaltySettingsDao.findByUserId(userObj.getUserId());
		AdditionalInfo additionalInfo = new AdditionalInfo();

		Users user = getUser(inquiryRequest.getUser().getUserName(), inquiryRequest.getUser().getOrganizationId(),
				inquiryRequest.getUser().getToken());
		String bannerImg = "";
		if(userOrg!=null && userOrg.getBannerPath()!=null && !userOrg.getBannerPath().isEmpty()) {
			bannerImg=userOrg.getBannerPath().replace(userParentDirectory,imgUrl);
		}
		additionalInfo.setBannerImage((userOrg!=null && userOrg.getBannerPath()!=null) ? bannerImg : "");
		/*additionalInfo.setCompanyLogo(
				(loyaltySettings!=null && loyaltySettings.getPath()!=null && !loyaltySettings.getPath().isEmpty())?
						loyaltySettings.getPath() : "");*/
		String companyLogo = "";
		if(loyaltySettings!=null && loyaltySettings.getPath()!=null && !loyaltySettings.getPath().isEmpty()) {
			companyLogo = loyaltySettings.getPath().replace(PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim(),ltyImgUrl);
			if(loyaltySettings.getPath().contains("RewardApp"))
				companyLogo = loyaltySettings.getPath().replace(PropertyUtil.getPropertyValue("usersParentDirectory").trim(),imgUrl);
			//companyLogo=loyaltySettings.getPath().replace(ltyParentDirectory,ltyImgUrl);
		}
		additionalInfo.setCompanyLogo(companyLogo);
		additionalInfo.setFontName(loyaltySettings!=null && loyaltySettings.getFontName()!=null?loyaltySettings.getFontName():"");
		additionalInfo.setFontURL(loyaltySettings!=null && loyaltySettings.getFontURL()!=null?loyaltySettings.getFontURL():"");
		//no need to handle for closed card here
		if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
			
			List<Balance> balances = null;
			LoyaltyProgramTier tier = contactsLoyalty.getProgramTierId() == null ? null : getLoyaltyTier(contactsLoyalty.getProgramTierId());
			MembershipResponse membershipResponse = new MembershipResponse();
			membershipResponse.setCardNumber("");
			membershipResponse.setCardPin("");
			membershipResponse.setPhoneNumber(contactsLoyalty.getCardNumber()+"");
			if(inquiryRequest.getMembership().getTransactions() != null && 
					!inquiryRequest.getMembership().getTransactions().isEmpty() && 
					inquiryRequest.getMembership().getTransactions().equals("Y")){
				LoyaltyTransactionChildDao loyaltyTransactionDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				List<String> retList = loyaltyTransactionDao.findAllTransactionsToShow(contactsLoyalty.getLoyaltyId(),contactsLoyalty.getUserId() );
				if(retList == null || retList.isEmpty()) retList = new ArrayList<String>();
				membershipResponse.setTransactions(retList);
				
			}
			if(tier != null){
				if(loyaltyProgram.getTierEnableFlag() == OCConstants.FLAG_YES  ) {
					membershipResponse.setTierLevel(tier.getTierType());
					membershipResponse.setTierName(tier.getTierName());
				}
				else {
					membershipResponse.setTierLevel("");
					membershipResponse.setTierName("");
				}
				if(loyaltyProgram.getMembershipExpiryFlag() == OCConstants.FLAG_YES && tier.getMembershipExpiryDateType() != null 
						&& tier.getMembershipExpiryDateValue() != null){
					membershipResponse.setExpiry(LoyaltyProgramHelper.getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(),
						contactsLoyalty.getTierUpgradedDate(), loyaltyProgram.getMbrshipExpiryOnLevelUpgdFlag() == OCConstants.FLAG_YES ? true : false,
							tier.getMembershipExpiryDateType(), tier.getMembershipExpiryDateValue()));
				}
				else{
					membershipResponse.setExpiry("");
				}
			}
			else {
				membershipResponse.setTierLevel("");
				membershipResponse.setTierName("");
				membershipResponse.setExpiry("");
			}
			List<ContactsLoyalty> loyaltyList = new ArrayList<ContactsLoyalty>();
			
			
			//String additionalMessage = " "+contactsLoyalty.getCardNumber()+" issued to <"+customer.getFirstName()+", "+customer.getLastName()+">";
			if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
				loyaltyList.add(contactsLoyalty);
				balances = prepareBalancesObject(contactsLoyalty);
				String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
				loyaltyList.add(contactsLoyalty);
				balances = prepareBalancesObject(contactsLoyalty);
				String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}/*else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
				ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactsLoyalty);
				loyaltyList.add(destLoyalty);
				
				balances = prepareBalancesObject(destLoyalty);
				String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+ Utility.maskNumber(contactsLoyalty.getCardNumber()+"")+".";
				status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}*/
			List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(loyaltyList,inquiryRequest);
			inquiryResponse = prepareInquiryResponse(responseHeader, membershipResponse, balances, null, additionalInfo, matchedCustomers, status);
			return inquiryResponse;
		}
		
		return performInquiryOperation(inquiryRequest, responseHeader, contactsLoyalty, loyaltyProgram);
	}
	
	
	private LoyaltyInquiryResponse cardBasedInquiry(LoyaltyCards loyaltyCard, LoyaltyInquiryRequest inquiryRequest, 
			Users userObj, ResponseHeader responseHeader) throws Exception {
		
		logger.info(" calling card based inquiry....");
		
		LoyaltyInquiryResponse inquiryResponse = null;
		Status status = null;
		
		LoyaltyProgram loyaltyProgram = null;
		
		loyaltyProgram = findLoyaltyProgramByProgramId(loyaltyCard.getProgramId(), userObj.getUserId());
		if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equals(loyaltyProgram.getStatus())){
			status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
			return inquiryResponse;
		}
		
		LoyaltyCardSet loyaltyCardSet = null;
		loyaltyCardSet = findLoyaltyCardSetByCardsetId(loyaltyCard.getCardSetId(), userObj.getUserId());
		if(loyaltyCardSet == null || !OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE.equals(loyaltyCardSet.getStatus())){
			status = new Status("111505", PropertyUtil.getErrorMessage(111505, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
			return inquiryResponse;
		}
		LoyaltySettings loyaltySettings=null;
		LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
		loyaltySettings = loyaltySettingsDao.findByUserId(userObj.getUserId());
		ContactsLoyalty contactsLoyalty = findContactLoyalty(loyaltyCard.getCardNumber(), loyaltyProgram.getProgramId(), userObj.getUserId());
	
		if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED) || 
				contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
			List<Balance> balances = null;
			LoyaltyProgramTier tier = null ; 
			
			List<ContactsLoyalty> loyaltyList = new ArrayList<ContactsLoyalty>();
			
			
			if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
				loyaltyList.add(contactsLoyalty);
				if(contactsLoyalty.getProgramTierId() != null)	tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				
				balances = prepareBalancesObject(contactsLoyalty);
				String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
				loyaltyList.add(contactsLoyalty);
				if(contactsLoyalty.getProgramTierId() != null)	tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
				
				balances = prepareBalancesObject(contactsLoyalty);
				String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
				status = new Status("111539", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
				ContactsLoyalty destLoyalty = getDestMembershipIfAny(contactsLoyalty);
				String maskedNum = Constants.STRING_NILL;
				if(destLoyalty != null) {
					loyaltyList.add(destLoyalty);
					contactsLoyalty = destLoyalty;
					tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
					balances = prepareBalancesObject(destLoyalty);
					maskedNum = Utility.maskNumber(destLoyalty.getCardNumber()+Constants.STRING_NILL);
					
				}
				String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+maskedNum+".";
				status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
			MembershipResponse membershipResponse = new MembershipResponse();
			if(inquiryRequest.getMembership().getTransactions() != null && 
					!inquiryRequest.getMembership().getTransactions().isEmpty() && 
					inquiryRequest.getMembership().getTransactions().equals("Y")){
				LoyaltyTransactionChildDao loyaltyTransactionDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				List<String> retList = loyaltyTransactionDao.findAllTransactionsToShow(contactsLoyalty.getLoyaltyId(),contactsLoyalty.getUserId() );
				if(retList == null || retList.isEmpty()) retList = new ArrayList<String>();
				membershipResponse.setTransactions(retList);
				
			}
			membershipResponse.setCardNumber(contactsLoyalty.getCardNumber()+"");
			membershipResponse.setCardPin(contactsLoyalty.getCardPin());
			//membershipResponse.setPassword(contactsLoyalty.getMembershipPwd() != null ? contactsLoyalty.getMembershipPwd() : Constants.STRING_NILL );
			membershipResponse.setPhoneNumber("");
			if(tier != null){
				if(loyaltyProgram.getTierEnableFlag() == OCConstants.FLAG_YES ) {
					membershipResponse.setTierLevel(tier.getTierType());
					membershipResponse.setTierName(tier.getTierName());
				}
				else {
					membershipResponse.setTierLevel("");
					membershipResponse.setTierName("");
				}
				if(loyaltyProgram.getMembershipExpiryFlag() == OCConstants.FLAG_YES && tier.getMembershipExpiryDateType() != null 
						&& tier.getMembershipExpiryDateValue() != null){
					membershipResponse.setExpiry(LoyaltyProgramHelper.getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(),
						contactsLoyalty.getTierUpgradedDate(), loyaltyProgram.getMbrshipExpiryOnLevelUpgdFlag() == OCConstants.FLAG_YES ? true : false,
							tier.getMembershipExpiryDateType(), tier.getMembershipExpiryDateValue()));
				}
				else{
					membershipResponse.setExpiry("");
				}
			}
			else {
				membershipResponse.setTierLevel("");
				membershipResponse.setTierName("");
				membershipResponse.setExpiry("");
			}
			List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(loyaltyList,inquiryRequest);
			AdditionalInfo additionalInfo = new AdditionalInfo();
			UserOrganization userOrg=null;
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			userOrg = usersDao.findByOrgId(userObj.getUserOrganization().getUserOrgId());
			String bannerImg = "";
			if(userOrg!=null && userOrg.getBannerPath()!=null && !userOrg.getBannerPath().isEmpty()) {
				bannerImg=userOrg.getBannerPath().replace(userParentDirectory,imgUrl);
			}
			additionalInfo.setBannerImage((userOrg!=null && userOrg.getBannerPath()!=null) ? bannerImg : "");
			String companyLogo = "";
			if(loyaltySettings!=null && loyaltySettings.getPath()!=null && !loyaltySettings.getPath().isEmpty()) {
				if(loyaltySettings!=null && loyaltySettings.getPath()!=null && !loyaltySettings.getPath().isEmpty()) {
					companyLogo = loyaltySettings.getPath().replace(PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim(),ltyImgUrl);
					if(loyaltySettings.getPath().contains("RewardApp"))
						companyLogo = loyaltySettings.getPath().replace(PropertyUtil.getPropertyValue("usersParentDirectory").trim(),imgUrl);
					//companyLogo=loyaltySettings.getPath().replace(ltyParentDirectory,ltyImgUrl);
				}
			}
			additionalInfo.setCompanyLogo((loyaltySettings!=null && loyaltySettings.getPath()!=null) ? companyLogo : "");
			additionalInfo.setFontName(loyaltySettings!=null && loyaltySettings.getFontName()!=null?loyaltySettings.getFontName():"");
			additionalInfo.setFontURL(loyaltySettings!=null && loyaltySettings.getFontURL()!=null?loyaltySettings.getFontURL():"");
			inquiryResponse = prepareInquiryResponse(responseHeader, membershipResponse, balances, null, additionalInfo, matchedCustomers, status);
			return inquiryResponse;
		}
	
		if(OCConstants.LOYALTY_CARD_STATUS_ENROLLED.equalsIgnoreCase(loyaltyCard.getStatus())){
			
			return performInquiryOperation(inquiryRequest, responseHeader, contactsLoyalty, loyaltyProgram);
			
		}
		else if(OCConstants.LOYALTY_CARD_STATUS_ACTIVATED.equalsIgnoreCase(loyaltyCard.getStatus())){
			
			return performGiftCardInquiry(inquiryRequest, contactsLoyalty, responseHeader, loyaltyProgram);
		}
		else{
			status = new Status("111537", PropertyUtil.getErrorMessage(111537, OCConstants.ERROR_LOYALTY_FLAG)+" "+loyaltyCard.getCardNumber()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
			return inquiryResponse;
		}
		
	}
	
	private ContactsLoyalty getDestMembershipIfAny(ContactsLoyalty contactLoyalty) throws Exception{
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		if(contactLoyalty.getMembershipStatus().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED) && contactLoyalty.getTransferedTo() != null) {
			return loyaltyDao.findAllByLoyaltyId(contactLoyalty.getTransferedTo());
			
		}
		
		return null;
	}
	private LoyaltyInquiryResponse performGiftCardInquiry(LoyaltyInquiryRequest inquiryRequest, ContactsLoyalty contactsLoyalty, ResponseHeader responseHeader, LoyaltyProgram program) throws Exception {
		
		logger.info("Calling Gift card based inquiry...");
		
		LoyaltyInquiryResponse inquiryResponse = null;
		Status status = null;
		double totalReedmCurr = 0.0;
		
		List<Balance> balances = prepareBalancesObject(contactsLoyalty);
		
		MembershipResponse membershipResponse = new MembershipResponse();
		membershipResponse.setCardNumber(contactsLoyalty.getCardNumber()+"");
		if(inquiryRequest.getHeader().getSourceType() != null && 
				!inquiryRequest.getHeader().getSourceType().isEmpty() && 
				(inquiryRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
						inquiryRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){
			//membershipResponse.setPassword(contactsLoyalty.getMembershipPwd() != null ? contactsLoyalty.getMembershipPwd() : Constants.STRING_NILL );
			membershipResponse.setFingerprintValidation(contactsLoyalty.getFpRecognitionFlag() != null ? contactsLoyalty.getFpRecognitionFlag().toString() : Constants.STRING_NILL );
			
		}	
		if(inquiryRequest.getMembership().getTransactions() != null && 
				!inquiryRequest.getMembership().getTransactions().isEmpty() && 
				inquiryRequest.getMembership().getTransactions().equals("Y")){
			LoyaltyTransactionChildDao loyaltyTransactionDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			List<String> retList = loyaltyTransactionDao.findAllTransactionsToShow(contactsLoyalty.getLoyaltyId(),contactsLoyalty.getUserId() );
			if(retList == null || retList.isEmpty()) retList = new ArrayList<String>();
			membershipResponse.setTransactions(retList);
			
		}
		membershipResponse.setCardPin(contactsLoyalty.getCardPin());
		//membershipResponse.setPassword(contactsLoyalty.getMembershipPwd() != null ? contactsLoyalty.getMembershipPwd() : Constants.STRING_NILL );
		membershipResponse.setPhoneNumber("");
		membershipResponse.setTierLevel("");
		membershipResponse.setTierName("");
		
		if(program.getGiftMembrshpExpiryFlag() == 'Y'){
			membershipResponse.setExpiry(LoyaltyProgramHelper.getGiftMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), 
					program.getGiftMembrshpExpiryDateType(), program.getGiftMembrshpExpiryDateValue()));
		}
		else{
			membershipResponse.setExpiry("");
		}
		
		AdditionalInfo additionalInfo = new AdditionalInfo();

		LoyaltySettings loyaltySettings=null;
		LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
		loyaltySettings = loyaltySettingsDao.findByUserId(contactsLoyalty.getUserId());
		UserOrganization userOrg=null;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		userOrg = usersDao.findByOrgId(contactsLoyalty.getOrgId());
		additionalInfo.setBannerImage((userOrg!=null && userOrg.getBannerPath()!=null) ? userOrg.getBannerPath() : "");
		//additionalInfo.setCompanyLogo((loyaltySettings!=null && loyaltySettings.getPath()!=null) ? loyaltySettings.getPath() : "");
		String bannerImg = "";
		if(userOrg!=null && userOrg.getBannerPath()!=null && !userOrg.getBannerPath().isEmpty()) {
			bannerImg=userOrg.getBannerPath().replace(userParentDirectory,imgUrl);
		}
		additionalInfo.setBannerImage((userOrg!=null && userOrg.getBannerPath()!=null) ? bannerImg : "");
		String companyLogo = "";
		if(loyaltySettings!=null && loyaltySettings.getPath()!=null && !loyaltySettings.getPath().isEmpty()) {
			if(loyaltySettings!=null && loyaltySettings.getPath()!=null && !loyaltySettings.getPath().isEmpty()) {
				companyLogo = loyaltySettings.getPath().replace(PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim(),ltyImgUrl);
				if(loyaltySettings.getPath().contains("RewardApp"))
					companyLogo = loyaltySettings.getPath().replace(PropertyUtil.getPropertyValue("usersParentDirectory").trim(),imgUrl);
				//companyLogo=loyaltySettings.getPath().replace(ltyParentDirectory,ltyImgUrl);
			}
		}
		additionalInfo.setCompanyLogo((loyaltySettings!=null && loyaltySettings.getPath()!=null) ? companyLogo : "");
		additionalInfo.setFontName(loyaltySettings!=null && loyaltySettings.getFontName()!=null?loyaltySettings.getFontName():"");
		additionalInfo.setFontURL(loyaltySettings!=null && loyaltySettings.getFontURL()!=null?loyaltySettings.getFontURL():"");
		//APP-3666
		/*if(program.getRedemptionOTPFlag() == 'Y'){
			additionalInfo.setOtpEnabled("True");
		}
		else{
			additionalInfo.setOtpEnabled("False");
		}
		
		if(program.getRedemptionOTPFlag() == OCConstants.FLAG_YES && program.getOtpLimitAmt() !=null){
		OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
		otpRedeemLimit.setAmount(""+program.getOtpLimitAmt());
		otpRedeemLimit.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
		otpRedeemLimitlist.add(otpRedeemLimit);
		additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
		}
		else{
		List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
		additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
		}*/
		additionalInfo.setPointsEquivalentCurrency("");
		double giftAmount = contactsLoyalty.getGiftBalance() == null ? 0.0 : contactsLoyalty.getGiftBalance();
		
		totalReedmCurr = giftAmount;
		LoyaltyProgramTier tier = null;
		if(contactsLoyalty.getProgramTierId() != null)	tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
		
		//added at tier level from program level APP-3666
		if(tier!=null && tier.getRedemptionOTPFlag() == 'Y'){
			additionalInfo.setOtpEnabled("True");
		}
		else{
			additionalInfo.setOtpEnabled("False");
		}
		
		if(tier!=null && tier.getRedemptionOTPFlag() == OCConstants.FLAG_YES && tier.getOtpLimitAmt() !=null){
		OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
		otpRedeemLimit.setAmount(""+tier.getOtpLimitAmt());
		otpRedeemLimit.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
		otpRedeemLimitlist.add(otpRedeemLimit);
		additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
		}
		else{
		List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
		additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
		}
		//To provide totalRedeemableCurrency  depending upon whichever is the least value among 
		//member's balance and redeem limit value (% of Billed Amount or flat value)//Changes 2.5.5.0
		if(inquiryRequest.getAmount()!=null) {//APP-1132
		Double totalRedeemableAmount = 	 giftAmount;	
		//if(inquiryRequest.getAmount().getReceiptAmount() != null && !inquiryRequest.getAmount().getReceiptAmount().trim().isEmpty()){
				
			if(inquiryRequest.getAmount().getReceiptAmount() != null 
					&& !inquiryRequest.getAmount().getReceiptAmount().trim().isEmpty())
				{

				if(tier!=null && tier.getRedemptionPercentageLimit() != null && tier.getRedemptionPercentageLimit() > 0 ){
				totalReedmCurr = tier.getRedemptionPercentageLimit()/100 * Double.parseDouble(inquiryRequest.getAmount().getReceiptAmount());
				}
				//APP-1967
				if(tier!=null && tier.getRedemptionValueLimit() != null && tier.getRedemptionValueLimit() > 0
						&& totalReedmCurr > tier.getRedemptionValueLimit()){
					totalReedmCurr = tier.getRedemptionValueLimit();
				}
				if(totalRedeemableAmount >= 0 && totalReedmCurr > totalRedeemableAmount){
					totalReedmCurr = totalRedeemableAmount;
				}
			}}
		
		
		//additionalInfo.setTotalRedeemableCurrency(Utility.truncateUptoTwoDecimal(""+giftAmount));
		additionalInfo.setTotalRedeemableCurrency(Utility.truncateUptoTwoDecimal(""+totalReedmCurr));//APP-1132
		additionalInfo.setLifeTimePurchaseValue(LoyaltyProgramHelper.getLPV(contactsLoyalty)+"");
		List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
		contactLoyaltyList.add(contactsLoyalty);
		List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList,inquiryRequest);
		
		createSuccessfulTransaction(contactsLoyalty, inquiryRequest, Long.valueOf(responseHeader.getTransactionId()));
		
		status = new Status("0", "Inquiry was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		
		inquiryResponse = prepareInquiryResponse(responseHeader, membershipResponse, balances, null, additionalInfo, matchedCustomers, status);
		return inquiryResponse;
	
	}
	
	private LoyaltyInquiryResponse performInquiryOperation(LoyaltyInquiryRequest inquiryRequest, ResponseHeader responseHeader,
			ContactsLoyalty contactsLoyalty, LoyaltyProgram loyaltyProgram) throws Exception{
		
		logger.info("calling inquiry operation...");
		
		LoyaltyInquiryResponse inquiryResponse = null;
		Status status = null;
		LoyaltyProgramTier tier = null;
		
		LoyaltyProgramExclusion loyaltyExclusion = getLoyaltyExclusion(loyaltyProgram.getProgramId());
		if(loyaltyExclusion != null){
			status = validateStoreNumberExclusion(inquiryRequest, loyaltyExclusion);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
				return inquiryResponse;
			}
		}
		
		LoyaltySettings loyaltySettings=null;
		LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
		loyaltySettings = loyaltySettingsDao.findByUserId(contactsLoyalty.getUserId());
		
		if(contactsLoyalty.getProgramTierId() != null)
			tier = getLoyaltyTier(contactsLoyalty.getProgramTierId());
		else{
			Long contactId = null;
			if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
				contactId = contactsLoyalty.getContact().getContactId();
				tier = findTier(contactsLoyalty.getProgramId(), contactId, contactsLoyalty.getUserId(), contactsLoyalty);
				if(tier == null){
					status = new Status("111555", PropertyUtil.getErrorMessage(111555, OCConstants.ERROR_LOYALTY_FLAG), 
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					inquiryResponse = prepareInquiryResponse(responseHeader, null, null, null, null, null, status);
					return inquiryResponse;
				}
				else if(tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
					contactsLoyalty.setProgramTierId(tier.getTierId());
					saveContactsLoyalty(contactsLoyalty);
				}
			}
		}
		//String sourceType =inquiryRequest.getHeader().getSourceType();
		/*if(contactsLoyalty.getContact().getInstanceId() == null && 
				sourceType != null && !sourceType.isEmpty() && 
				sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP) && 
				instanceID != null && !instanceID.isEmpty()){
			ContactsDaoForDML contactsDaoDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			contactsDaoDML.updateInstanceID(instanceID.trim(), contactsLoyalty.getContact().getContactId());
		}*/
		List<Balance> balances = prepareBalancesObject(contactsLoyalty);
		MembershipResponse membershipResponse = new MembershipResponse();
		if(inquiryRequest.getMembership().getTransactions() != null && 
				!inquiryRequest.getMembership().getTransactions().isEmpty() && 
				inquiryRequest.getMembership().getTransactions().equals("Y")){
			LoyaltyTransactionChildDao loyaltyTransactionDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			List<String> retList = loyaltyTransactionDao.findAllTransactionsToShow(contactsLoyalty.getLoyaltyId(),contactsLoyalty.getUserId() );
			if(retList == null || retList.isEmpty()) retList = new ArrayList<String>();
			membershipResponse.setTransactions(retList);
			
		}
		if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equals(contactsLoyalty.getMembershipType())){
			membershipResponse.setCardNumber(""+contactsLoyalty.getCardNumber());
			if(inquiryRequest.getHeader().getSourceType() != null && 
					!inquiryRequest.getHeader().getSourceType().isEmpty() && 
					(inquiryRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
							inquiryRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){
				//membershipResponse.setPassword(contactsLoyalty.getMembershipPwd() != null ? contactsLoyalty.getMembershipPwd() : Constants.STRING_NILL );
				membershipResponse.setFingerprintValidation(contactsLoyalty.getFpRecognitionFlag() != null ? contactsLoyalty.getFpRecognitionFlag().toString() : Constants.STRING_NILL );
			}	
			membershipResponse.setCardPin(contactsLoyalty.getCardPin());
			membershipResponse.setPhoneNumber("");
		}
		else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equals(contactsLoyalty.getMembershipType())){
			membershipResponse.setCardNumber("");
			if(inquiryRequest.getHeader().getSourceType() != null && 
					!inquiryRequest.getHeader().getSourceType().isEmpty() && 
					(inquiryRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
							inquiryRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){
				//me
				//membershipResponse.setPassword(contactsLoyalty.getMembershipPwd() != null ? contactsLoyalty.getMembershipPwd() : Constants.STRING_NILL );
				membershipResponse.setFingerprintValidation(contactsLoyalty.getFpRecognitionFlag() != null ? contactsLoyalty.getFpRecognitionFlag().toString() : Constants.STRING_NILL );
			}	
			membershipResponse.setCardPin("");
			membershipResponse.setPhoneNumber(""+contactsLoyalty.getCardNumber());
		}
		
		boolean upgdResetFlag = false;
		if(loyaltyProgram.getMbrshipExpiryOnLevelUpgdFlag() == 'Y'){
			upgdResetFlag = true;
		}
		
		if(tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
			if(loyaltyProgram.getTierEnableFlag() == OCConstants.FLAG_YES  ) {
				membershipResponse.setTierLevel(tier.getTierType());
				membershipResponse.setTierName(tier.getTierName());
			}
			else {
				membershipResponse.setTierLevel("");
				membershipResponse.setTierName("");
			}
			
			if(loyaltyProgram.getMembershipExpiryFlag() == 'Y' && tier.getMembershipExpiryDateType() != null 
					&& tier.getMembershipExpiryDateValue() != null){
				membershipResponse.setExpiry(LoyaltyProgramHelper.getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), contactsLoyalty.getTierUpgradedDate(), 
					upgdResetFlag, tier.getMembershipExpiryDateType(), tier.getMembershipExpiryDateValue()));
			}
			else{
				membershipResponse.setExpiry("");
			}
		}
		else{
			
			membershipResponse.setExpiry("");
		}
		//APP - 1855
		String tierUpgdConstraint=tier.getTierUpgdConstraint() == null ?"":tier.getTierUpgdConstraint();
		Double tierUpgdConstraintValue=tier.getTierUpgdConstraintValue()==null ?0.00:tier.getTierUpgdConstraintValue();
		Double requiredDiff=0.0;
		String nextTierName = getNextTierName(contactsLoyalty.getProgramId(),tier.getTierId());
		membershipResponse.setTierUpgradeCriteria(tierUpgdConstraint);
		membershipResponse.setNextTierName(nextTierName == null ? "" : nextTierName);
		//Written in the assumption that tier.getTierUpgdConstraintValue() will always be null in the case of last tier. 
		if(loyaltyProgram.getTierEnableFlag()=='Y' && tier.getTierUpgdConstraintValue() != null ) { 
		
		if(OCConstants.LOYALTY_LIFETIME_POINTS.equals(tierUpgdConstraint)){
			Double totLoyaltyPointsValue = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.00 : contactsLoyalty.getTotalLoyaltyEarned();
			requiredDiff=tierUpgdConstraintValue-totLoyaltyPointsValue;
			membershipResponse.setCurrentTierValue(totLoyaltyPointsValue+"");
		}
		else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tierUpgdConstraint)){
			Double totPurchaseValue = null;
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);//contactsLoyalty.getLifeTimePurchaseValue() == null ? 0.0 : contactsLoyalty.getLifeTimePurchaseValue();//loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId());//Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
			requiredDiff=tierUpgdConstraintValue-totPurchaseValue;
			membershipResponse.setCurrentTierValue(totPurchaseValue+"");
		}
		else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(tierUpgdConstraint)){
				Double cumulativeAmount = 0.0;
				Long months = tier.getTierUpgradeCumulativeValue();
				int days=(int) (-30*months);
				
				
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.DATE, days);
				
				
				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);


			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			cumulativeAmount = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));
			requiredDiff=tierUpgdConstraintValue-cumulativeAmount;
			membershipResponse.setCurrentTierValue(cumulativeAmount+"");	
			}
			else if (OCConstants.LOYALTY_CUMULATIVE_POINTS.equals(tierUpgdConstraint)) {//APP-4559
				
				logger.info("inside cumulative points "+tierUpgdConstraint);
				Double cumulativePoints = 0.0;
				Long months = tier.getTierUpgradeCumulativeValue();
				
				String startDate = MyCalendar.calendarToString(contactsLoyalty.getTierUpgradedDate()!=null?
						contactsLoyalty.getTierUpgradedDate():contactsLoyalty.getCreatedDate(), 
						MyCalendar.FORMAT_DATETIME_STYEAR);
				String endCal1 = MyCalendar.calendarToString(contactsLoyalty.getTierUpgradedDate()!=null?
						contactsLoyalty.getTierUpgradedDate():contactsLoyalty.getCreatedDate(), 
						MyCalendar.FORMAT_DATETIME_STYEAR);
				
				Calendar endCal = MyCalendar.string2Calendar(endCal1);
				endCal.add(Calendar.MONTH, Integer.parseInt(months+""));
				logger.info("date to string "+startDate+" "+endCal1);

				//String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				logger.info(" startDate = " + startDate + " endDate = " + endDate);


			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			cumulativePoints = Double.valueOf(loyaltyTransactionChildDao.getCumulativePoints(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));
			logger.info("cumulativePoints >>> "+cumulativePoints);
			requiredDiff=tierUpgdConstraintValue-cumulativePoints;
			membershipResponse.setCurrentTierValue(cumulativePoints+"");
				
			}
		
		if(requiredDiff<0) requiredDiff=0.0; 
		
		membershipResponse.setNextTierMilestone(requiredDiff+"");;
		
		}
		else {
			
			membershipResponse.setCurrentTierValue("");
			membershipResponse.setNextTierMilestone("");
			membershipResponse.setTierUpgradeCriteria("");
			
		}
		
		
		HoldBalance holdBalance = new HoldBalance();
		holdBalance.setPoints(contactsLoyalty.getHoldPointsBalance()== null ? "" : ""+contactsLoyalty.getHoldPointsBalance().intValue());
		holdBalance.setCurrency(contactsLoyalty.getHoldAmountBalance() == null ? "" : ""+Math.round(contactsLoyalty.getHoldAmountBalance()));
		
		//APP-3284
		boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(inquiryRequest.getHeader().getStoreNumber(),tier);

		
		if(tier != null && tier.getActivationFlag() == 'Y' && isStoreActiveForActivateAfter && !"Pending".equalsIgnoreCase(tier.getTierType())
				&& ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
						(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){
			holdBalance.setActivationPeriod(tier.getPtsActiveDateValue()+" "+tier.getPtsActiveDateType());
		}
		else{
			holdBalance.setActivationPeriod("");
		}
		
		List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
		contactLoyaltyList.add(contactsLoyalty);
		
		List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList,inquiryRequest);

//		if(tier != null && OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND.equalsIgnoreCase(tier.getConversionType().trim()) ||
//				loyaltyProgram.getRedemptionOTPFlag() == 'Y'){
			
			AdditionalInfo additionalInfo = new AdditionalInfo();
			UserOrganization userOrg=null;
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			userOrg = usersDao.findByOrgId(contactsLoyalty.getOrgId());
			additionalInfo.setBannerImage((userOrg!=null && userOrg.getBannerPath()!=null) ? userOrg.getBannerPath() : "");
			//additionalInfo.setCompanyLogo((loyaltySettings!=null && loyaltySettings.getPath()!=null) ? loyaltySettings.getPath() : "");
			String bannerImg = "";
			if(userOrg!=null && userOrg.getBannerPath()!=null && !userOrg.getBannerPath().isEmpty()) {
				bannerImg=userOrg.getBannerPath().replace(userParentDirectory,imgUrl);
			}
			additionalInfo.setBannerImage((userOrg!=null && userOrg.getBannerPath()!=null) ? bannerImg : "");
			String companyLogo = "";
			if(loyaltySettings!=null && loyaltySettings.getPath()!=null && !loyaltySettings.getPath().isEmpty()) {
				if(loyaltySettings!=null && loyaltySettings.getPath()!=null && !loyaltySettings.getPath().isEmpty()) {
					companyLogo = loyaltySettings.getPath().replace(PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim(),ltyImgUrl);
					if(loyaltySettings.getPath().contains("RewardApp"))
						companyLogo = loyaltySettings.getPath().replace(PropertyUtil.getPropertyValue("usersParentDirectory").trim(),imgUrl);
					//companyLogo=loyaltySettings.getPath().replace(ltyParentDirectory,ltyImgUrl);
				}
			}
			additionalInfo.setCompanyLogo((loyaltySettings!=null && loyaltySettings.getPath()!=null) ? companyLogo : "");
			additionalInfo.setFontName(loyaltySettings!=null && loyaltySettings.getFontName()!=null?loyaltySettings.getFontName():"");
			additionalInfo.setFontURL(loyaltySettings!=null && loyaltySettings.getFontURL()!=null?loyaltySettings.getFontURL():"");
			//APP-3666
			if(tier!=null && tier.getRedemptionOTPFlag() == 'Y'){
				additionalInfo.setOtpEnabled("True");
			}
			else{
				additionalInfo.setOtpEnabled("False");
			}
			if(tier!=null && tier.getRedemptionOTPFlag() == OCConstants.FLAG_YES && tier.getOtpLimitAmt()!=null){
			OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
			otpRedeemLimit.setAmount(""+tier.getOtpLimitAmt());
			otpRedeemLimit.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
			List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
			otpRedeemLimitlist.add(otpRedeemLimit);
			additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
			}
			else{
			List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
			additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
			}
		    if(tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
				double loyaltyAmount = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
				double giftAmount = contactsLoyalty.getGiftBalance() == null ? 0.0 : contactsLoyalty.getGiftBalance();
				double pointsAmount = 0.0;
				double totalReedmCurr = 0.0;
				if(tier.getConversionType()!=null && OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND.equalsIgnoreCase(tier.getConversionType().trim())) {
					pointsAmount = calculatePointsAmount(contactsLoyalty, tier);
					additionalInfo.setPointsEquivalentCurrency(pointsAmount+"");
				}
				else{
					additionalInfo.setPointsEquivalentCurrency("");
				}
				totalReedmCurr = loyaltyAmount + pointsAmount + giftAmount;
				//To provide totalRedeemableCurrency  depending upon whichever is the least value among 
				//member's balance and redeem limit value (% of Billed Amount or flat value)//Changes 2.5.5.0
				if(inquiryRequest.getAmount()!=null) {
				Double totalRedeemableAmount = 	loyaltyAmount + pointsAmount + giftAmount;	
			//	if(inquiryRequest.getAmount().getReceiptAmount() != null && !inquiryRequest.getAmount().getReceiptAmount().trim().isEmpty()){
				
					if(inquiryRequest.getAmount().getReceiptAmount() != null 
							&& !inquiryRequest.getAmount().getReceiptAmount().trim().isEmpty())
						{

						if(tier.getRedemptionPercentageLimit() != null && tier.getRedemptionPercentageLimit() > 0 ){
						totalReedmCurr = tier.getRedemptionPercentageLimit()/100 * Double.parseDouble(inquiryRequest.getAmount().getReceiptAmount());
						}
						//APP-1967
						if(tier.getRedemptionValueLimit() != null && tier.getRedemptionValueLimit() > 0
								&& totalReedmCurr > tier.getRedemptionValueLimit()){
							totalReedmCurr = tier.getRedemptionValueLimit();
						}
						if(totalRedeemableAmount >= 0 && totalReedmCurr > totalRedeemableAmount){
							totalReedmCurr = totalRedeemableAmount;
						}
					}
					
					 if(loyaltyProgram!=null && loyaltyProgram.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK)) {
						 long covertedAmnt = 0;
							long usageLimit = tier.getPerkLimitValue()!=null ? tier.getPerkLimitValue() : 0;
							LoyaltyTransactionChildDao trxChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
							double lastRedemptions = trxChildDao.fetchLastPerkRedemption(contactsLoyalty.getUserId(),loyaltyProgram.getProgramId(),contactsLoyalty.getLoyaltyId(),tier.getEarnType());
							logger.info("last redemption in inquery"+lastRedemptions);
							LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
							 LoyaltyBalance ltyBalance = loyaltyBalanceDao.findBy(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(), tier.getEarnType());
							 
						 if(ltyBalance!=null && tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
									&& ltyBalance.getBalance() != null && ltyBalance.getBalance() > 0){
							
								double factor = ltyBalance.getBalance()/tier.getConvertFromPoints();
								int intFactor = (int)factor;
								covertedAmnt = (long)(tier.getConvertToAmount() * intFactor);
								totalReedmCurr = covertedAmnt;//-lastRedemptions;
								logger.info("total redem currency after converting perks in inquery-------"+totalReedmCurr);
						}
						  totalRedeemableAmount = totalReedmCurr;
						  logger.info("totalRedeemableAmount in inqury>>>>"+totalRedeemableAmount);
							if(inquiryRequest.getAmount().getReceiptAmount() != null 
									&& !inquiryRequest.getAmount().getReceiptAmount().trim().isEmpty())
								{

								if(tier.getRedemptionPercentageLimit() != null && tier.getRedemptionPercentageLimit() > 0 ){
								totalReedmCurr = tier.getRedemptionPercentageLimit()/100 * Double.parseDouble(inquiryRequest.getAmount().getReceiptAmount());
								}
								//APP-1967
								if(tier.getRedemptionValueLimit() != null && tier.getRedemptionValueLimit() > 0
										&& totalReedmCurr > tier.getRedemptionValueLimit()){
									totalReedmCurr = tier.getRedemptionValueLimit();
								}
								if(totalRedeemableAmount >= 0 && totalReedmCurr > totalRedeemableAmount){
									totalReedmCurr = totalRedeemableAmount;
								}
							}
							logger.info("totalReedmCurr after redeemable amount calculation in inqury"+totalReedmCurr);
						 
							if(totalReedmCurr>usageLimit-lastRedemptions) {
								
								totalReedmCurr = usageLimit-lastRedemptions <0?0: usageLimit-lastRedemptions;
							} /*
								 * else { totalReedmCurr = totalReedmCurr > lastRedemptions ?
								 * totalReedmCurr-lastRedemptions : lastRedemptions-totalReedmCurr; }
								 */
						 logger.info("final totalReedmCurr in inquery"+totalReedmCurr);
						 
					 }
					
					
				//}else{
					//totalReedmCurr = totalRedeemableAmount;
			//	}
				}
				additionalInfo.setTotalRedeemableCurrency(Utility.truncateUptoTwoDecimal(totalReedmCurr+""));
				additionalInfo.setLifeTimePurchaseValue(LoyaltyProgramHelper.getLPV(contactsLoyalty)+"");
			}
			else {
				additionalInfo.setPointsEquivalentCurrency("");
				additionalInfo.setTotalRedeemableCurrency("");
				additionalInfo.setLifeTimePurchaseValue("");
			}
			
			
			/*if(tier != null && OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND.equalsIgnoreCase(tier.getConversionType().trim())
					&& contactsLoyalty.getLoyaltyBalance() != null && !(contactsLoyalty.getLoyaltyBalance() == 0.0)
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints() 
						&& !"Pending".equalsIgnoreCase(tier.getTierType())){
					
					int factor = contactsLoyalty.getLoyaltyBalance().intValue()/tier.getConvertFromPoints().intValue();
					double pointsToCurrency = Math.floor(factor*tier.getConvertToAmount());
					int pointsCurrency = (int)pointsToCurrency;
					additionalInfo.setPointsEquivalentCurrency(""+pointsCurrency);
					
					if(contactsLoyalty.getGiftcardBalance() != null){
						double totalReedmCurr = contactsLoyalty.getGiftcardBalance() + pointsCurrency;
						additionalInfo.setTotalRedeemableCurrency(""+totalReedmCurr);
					}
					else{
						additionalInfo.setTotalRedeemableCurrency(""+pointsCurrency);
					}
				}
			else{
				additionalInfo.setPointsEquivalentCurrency("");
				additionalInfo.setTotalRedeemableCurrency("");
			}*/
			
			createSuccessfulTransaction(contactsLoyalty, inquiryRequest, Long.valueOf(responseHeader.getTransactionId()));
			
			status = new Status("0", "Inquiry was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			inquiryResponse = prepareInquiryResponse(responseHeader, membershipResponse, balances, holdBalance, additionalInfo, matchedCustomers, status);
			return inquiryResponse;
		/*}
		else{
			
			createSuccessfulTransaction(contactsLoyalty, inquiryRequest, Long.valueOf(responseHeader.getTransactionId()));
			
			status = new Status("0", "Inquiry was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			inquiryResponse = prepareInquiryResponse(responseHeader, membershipResponse, balances, holdBalance, null, matchedCustomers, status);
			return inquiryResponse;
			
		}*/
		
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
	
	
	private LoyaltyProgram findLoyaltyProgramByProgramId(Long programId, Long userId) throws Exception {
		
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		return loyaltyProgramDao.findByIdAndUserId(programId, userId);
	}
	
	private LoyaltyCardSet findLoyaltyCardSetByCardsetId(Long cardSetId, Long userId) throws Exception {
		LoyaltyCardSetDao cardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
		return cardSetDao.findByCardSetId(cardSetId);
		
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
			totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);//contactsLoyalty.getLifeTimePurchaseValue();//Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
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
			
			LoyaltyInquiryCPVThread cpvThread = new LoyaltyInquiryCPVThread(eligibleMap, userId, contactId, tiersList, contactsLoyalty.getLoyaltyId());
			Thread th = new Thread(cpvThread);
			th.start();
			
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
	private String getNextTierName(Long programId,Long tierId) {
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
	
}
