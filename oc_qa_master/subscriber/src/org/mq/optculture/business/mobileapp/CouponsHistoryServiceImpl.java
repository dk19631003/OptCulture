package org.mq.optculture.business.mobileapp;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.*;
import java.util.*;

import org.apache.catalina.User;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ApplicationProperties;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.LoyaltyMemberSessionID;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.OrganizationZone;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ApplicationPropertiesDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.ZoneDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.mobileapp.Item;
import org.mq.optculture.model.mobileapp.Lookup;
import org.mq.optculture.model.mobileapp.CouponMatchedCustomers;
import org.mq.optculture.model.mobileapp.LoyaltyTransaction;
import org.mq.optculture.model.mobileapp.MatchedCustomers;
import org.mq.optculture.model.mobileapp.Payment;
import org.mq.optculture.model.mobileapp.CouponDiscount;
import org.mq.optculture.model.mobileapp.CouponMatchedCustomers;
import org.mq.optculture.model.mobileapp.CouponsHistoryRequest;
import org.mq.optculture.model.mobileapp.CouponsHistoryResponse;
import org.mq.optculture.model.mobileapp.IssuedCoupons;
import org.mq.optculture.model.mobileapp.RequestReport;
import org.mq.optculture.model.mobileapp.ResponseHeader;
import org.mq.optculture.model.mobileapp.ResponseReport;
import org.mq.optculture.model.mobileapp.SaleTransaction;
import org.mq.optculture.model.ocloyalty.Discounts;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;


import com.google.gson.Gson;

public class CouponsHistoryServiceImpl implements CouponsHistoryService{
	
 
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		BaseResponseObject baseResponseObject=new BaseResponseObject();
		CouponsHistoryResponse couponsHistoryResponse=null;
		try {
			
			logger.debug("-------entered processRequest---------");
			String serviceRequest = baseRequestObject.getAction();
			String requestJson = baseRequestObject.getJsonValue();
			String transactionID = baseRequestObject.getTransactionId();
			String transactionDate = baseRequestObject.getTransactionDate();
			
			String responseJson = null;
			Gson gson = new Gson();
			
			//json to object
			CouponsHistoryRequest couponsHistoryRequest = null;
			
			if(serviceRequest == null || !serviceRequest.equals(OCConstants.COUPONS_HISTORY_SERVICE_REQUEST)){
				logger.info("servicerequest is null...");
				
				Status status = new Status("800000", ""+PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				
				couponsHistoryResponse = new CouponsHistoryResponse();
				couponsHistoryResponse.setStatus(status);
				responseJson = gson.toJson(couponsHistoryResponse);
				
				baseResponseObject.setAction(serviceRequest);
				baseResponseObject.setJsonValue(responseJson);
				logger.info("Exited baserequest due to invalid service");
				return baseResponseObject;
			}
			
			try {
				couponsHistoryRequest = gson.fromJson(baseRequestObject.getJsonValue(), CouponsHistoryRequest.class);
			} catch (Exception e) {
				logger.error("Exception ::",e);
				Status status = new Status("800000",PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				ResponseHeader header = new ResponseHeader(Constants.STRING_NILL,
						Constants.STRING_NILL, transactionDate, transactionID);
				
				couponsHistoryResponse = prepareFinalResponse(header,status);
				String json=gson.toJson(couponsHistoryResponse);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.COUPONS_HISTORY_SERVICE_REQUEST);
				return baseResponseObject;
			}
			
			try{
				CouponsHistoryService couponsHistoryService = (CouponsHistoryService) ServiceLocator.getInstance().getServiceByName(OCConstants.COUPONS_HISTORY_SERVICE);
				couponsHistoryResponse = couponsHistoryService.processCouponsHistoryRequest(couponsHistoryRequest, 
						OCConstants.LOYALTY_ONLINE_MODE, transactionID, transactionDate);
				responseJson = gson.toJson(couponsHistoryResponse);
				
				baseResponseObject = new BaseResponseObject();
				baseResponseObject.setAction(serviceRequest);
				baseResponseObject.setJsonValue(responseJson);
			}catch(Exception e){
				logger.error("Exception in loyalty enroll base service.",e);
				throw new BaseServiceException("Server Error.");
			}
			logger.info("Completed processing baserequest... ");
			return baseResponseObject;
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return baseResponseObject;
	}
	
	@Override
	public CouponsHistoryResponse processCouponsHistoryRequest(CouponsHistoryRequest couponsHistoryRequest,
			String mode, String transactionId, String transactionDate) throws BaseServiceException {
		
		CouponsHistoryResponse couponsHistoryResponse=null;
		Status status = null;
		Users user = null;
		
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(couponsHistoryRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(couponsHistoryRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		try {
		
			String sourceType = couponsHistoryRequest.getHeader().getSourceType() != null ? couponsHistoryRequest.getHeader().getSourceType().trim().toString() :"";
			CouponsDao couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			ApplicationPropertiesDao applicationPropertiesDao = (ApplicationPropertiesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.APPLICATION_PROPERTIES_DAO);
			status = validateJsonData(couponsHistoryRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
				return couponsHistoryResponse;
			}
			user = getUser(couponsHistoryRequest.getUser().getUserName(), couponsHistoryRequest.getUser().getOrganizationId(),
					couponsHistoryRequest.getUser().getToken());
			if(user == null){
				status = new Status("800002", PropertyUtil.getErrorMessage(800002, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
				return couponsHistoryResponse;
			}
			
			if(!user.isEnabled()){
				status = new Status("800003", PropertyUtil.getErrorMessage(800003, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
				return couponsHistoryResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("800004", PropertyUtil.getErrorMessage(800004, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
				return couponsHistoryResponse;
			}
			
			status = validateInnerObjects(couponsHistoryRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
				return couponsHistoryResponse;
			}
			if(sourceType != null && !sourceType.isEmpty() && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) ){
				String sessionID = couponsHistoryRequest.getUser().getSessionID();
				
				if(sessionID == null || sessionID.isEmpty()){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
					return couponsHistoryResponse;
				}
				LoyaltyMemberSessionID loyaltyMemberSessionID = LoyaltyProgramHelper.validateSessionID(sessionID);
				if(loyaltyMemberSessionID == null){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
					return couponsHistoryResponse;
				}
				
				String cardNumber = LoyaltyProgramHelper.getCardFromSesstionID(sessionID);
				if(couponsHistoryRequest.getLookup().getMembershipNumber() != null && 
						couponsHistoryRequest.getLookup().getMembershipNumber().trim().length() > 0 && 
						!couponsHistoryRequest.getLookup().getMembershipNumber().trim().equals(cardNumber)){
					status = new Status("800029", PropertyUtil.getErrorMessage(800029, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
					return couponsHistoryResponse;
					
				}
				
			}
			Lookup lookup = couponsHistoryRequest.getLookup();
			String membershipNumber = lookup.getMembershipNumber();
			String phone = lookup.getPhone();
			String emailId = lookup.getEmailAddress();
			String lastFetchedTime = lookup.getLastFetchedTime()!=null?lookup.getLastFetchedTime():"";
			logger.info("lastFetchedTime==>"+lastFetchedTime);
			lastFetchedTime =getStartDate(lastFetchedTime,applicationPropertiesDao.findByPropertyKey(OCConstants.DB_SERVER_TIMEZONE));
			logger.info("lastFetchedTime==>"+lastFetchedTime);
			
			String cid =  null;
			
			
			String locality= (lookup.getLocality()!=null && !lookup.getLocality().isEmpty()) ? lookup.getLocality() : Constants.STRING_NILL;
			String city =(lookup.getCityName()!=null && !lookup.getCityName().isEmpty()) ? lookup.getCityName() : Constants.STRING_NILL; 
			String country =(lookup.getCountry()!=null && !lookup.getCountry().isEmpty()) ? lookup.getCountry() : Constants.STRING_NILL;
			String brandName =(lookup.getBrandName()!=null && !lookup.getBrandName().isEmpty())  ? lookup.getBrandName() : Constants.STRING_NILL;   
			String storeName =(lookup.getStoreName()!=null && !lookup.getStoreName().isEmpty() && lookup.getSubsidiaryNumber()!=null && !lookup.getSubsidiaryNumber().isEmpty()) ? lookup.getStoreName() : Constants.STRING_NILL;
			String SBSNo =(lookup.getSubsidiaryNumber()!=null && !lookup.getSubsidiaryNumber().isEmpty())?lookup.getSubsidiaryNumber():Constants.STRING_NILL;
			String sortOrder = (lookup.getSortOrder()!=null && !lookup.getSortOrder().isEmpty())?lookup.getSortOrder():Constants.STRING_NILL;
			String sortType = (lookup.getSortType()!=null && !lookup.getSortType().isEmpty())?lookup.getSortType():Constants.STRING_NILL;
			
			String order="desc";
			if(sortOrder!=null && !sortOrder.isEmpty()){
			order=((sortOrder.equalsIgnoreCase("asc"))?(sortOrder.toLowerCase()):order);
			}
		
			
		 	List<Coupons> couponsNew = null;
		 	List<Coupons> couponsDeactivated = null;
		 	List<Coupons> couponsModified = null;
		
		
			
			
			if(membershipNumber != null && !membershipNumber.isEmpty()){
				ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
				ContactsLoyalty conMembership= contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(), membershipNumber);
				if(conMembership == null){
					status = new Status("800008", PropertyUtil.getErrorMessage(800008, OCConstants.ERROR_MOBILEAPP_FLAG),
                            OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
                    return prepareFinalResponse(responseHeader, status);
				}
				 if(conMembership.getContact() == null) {
	                    status = new Status("800009", PropertyUtil.getErrorMessage(800009, OCConstants.ERROR_MOBILEAPP_FLAG),
	                            OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
	                    return prepareFinalResponse(responseHeader, status);
                } //error
				
				cid = conMembership.getContact().getContactId().longValue()+Constants.STRING_NILL;
				ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				 
				Contacts contact = conMembership.getContact();
				   

				List<CouponMatchedCustomers> matchedCustomers = new ArrayList<CouponMatchedCustomers>();
				CouponMatchedCustomers customer = null; 
				customer =new CouponMatchedCustomers();
					
				couponsNew = couponsDao.findCouponsNew(user.getUserId(),lastFetchedTime,sourceType);
				//couponsDeactivated = couponsDao.findCouponsDeactivated(user.getUserId(),lastFetchedTime,sourceType,lookup);
				couponsModified = couponsDao.findCouponsModified(user.getUserId(),lastFetchedTime,sourceType,lookup);	
					
				if((locality!=null && !locality.isEmpty())  || (city!=null && !city.isEmpty()) || (country!=null && !country.isEmpty())||
				(brandName!=null && !brandName.isEmpty())|| (storeName!=null && !storeName.isEmpty()) || (SBSNo!=null && !SBSNo.isEmpty())
				|| (sortOrder!=null && !sortOrder.isEmpty()) || (sortType!=null && !sortType.isEmpty())) {
					
	  						List<Coupons>[] coupons = zoneFilter(lookup, contact, user, sourceType, lastFetchedTime,couponsNew,couponsDeactivated,couponsModified);		
					if(coupons == null) {
				  		status = new Status("800018", PropertyUtil.getErrorMessage(800018, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				  		return prepareFinalResponse(responseHeader, status);
				  	}
	  				couponsNew = coupons[0];
					//couponsDeactivated = coupons[1];
					couponsModified = coupons[2];
				}	
				//External ID mostly null.
				//Setting Objects
				customer.setCustomerId(contact.getExternalId() == null ||  contact.getExternalId().isEmpty() ? Constants.STRING_NILL : contact.getExternalId());
				customer.setPhone(contact.getMobilePhone() == null ||  contact.getMobilePhone().isEmpty() ? Constants.STRING_NILL : contact.getMobilePhone());
				customer.setEmailAddress(contact.getEmailId() == null ||  contact.getEmailId().isEmpty() ? Constants.STRING_NILL : contact.getEmailId());
				customer.setFirstName(contact.getFirstName() == null ||  contact.getFirstName().isEmpty() ? Constants.STRING_NILL : contact.getFirstName());
				customer.setLastName(contact.getLastName() == null ||  contact.getLastName().isEmpty() ? Constants.STRING_NILL : contact.getLastName());
				customer.setMembershipNumber(conMembership.getCardNumber());
				customer.setCouponsNew(matchedCoupons(couponsNew,sourceType,contact,user,conMembership,couponsHistoryRequest));
				customer.setCouponsModified(matchedCoupons(couponsModified,sourceType,contact,user,conMembership,couponsHistoryRequest));
				customer.setCouponsDeactivated(matchedCoupons(couponsDeactivated,sourceType,contact,user,conMembership,couponsHistoryRequest));
				matchedCustomers.add(customer);
				
				
				status = new Status("0", "Contact lookup was successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				couponsHistoryResponse = prepareFinalResponse(responseHeader, status, matchedCustomers, new ResponseReport());
				return couponsHistoryResponse;
				
			}//if
			else if((emailId != null && !emailId.isEmpty()) || (phone != null && !phone.isEmpty())) {
				
				List<POSMapping> contactPOSMap = null;
				ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance()
												.getDAOByName(OCConstants.POSMAPPING_DAO);
				contactPOSMap = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_CONTACTS + "'",
						user.getUserId());
				
				status = validateContactPOSMap(contactPOSMap, user.getUserId());
				
				if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
					couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
					return couponsHistoryResponse;
				}
				Contacts inputContact = new Contacts();
				inputContact.setUsers(user); 
				
				if(emailId != null && !emailId.isEmpty() ){
					if(!Utility.validateEmail(emailId.trim())){
					
						status = new Status("800013", PropertyUtil.getErrorMessage(800013, OCConstants.ERROR_MOBILEAPP_FLAG),
	                            OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
	                    return prepareFinalResponse(responseHeader, status);
					}else{
						inputContact.setEmailId(emailId);
					}
				}
				if((phone != null && !phone.isEmpty())) {
					String mobilePhone = Utility.phoneParse(phone.trim(), user.getUserOrganization());
					if(mobilePhone == null ){
						status = new Status("800014", PropertyUtil.getErrorMessage(800014, OCConstants.ERROR_MOBILEAPP_FLAG),
	                            OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
	                    return prepareFinalResponse(responseHeader, status);
						
						
					}else{
						inputContact.setMobilePhone(mobilePhone);;
					}
				}
				// validate emailId and mobile
				TreeMap<String, List<String>> prioMap = null;
				prioMap = Utility.getPriorityMap(user.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
				status = validatePriorityMap(prioMap, user);
				if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
					couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
					return couponsHistoryResponse;
				}
					List<Contacts> contactsList = contactsDao.findContactBy(prioMap, inputContact, user);
					if(contactsList == null || contactsList.isEmpty()){
						
						 status = new Status("800012", PropertyUtil.getErrorMessage(800012, OCConstants.ERROR_MOBILEAPP_FLAG),
		                            OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		                    return prepareFinalResponse(responseHeader, status);
					}
					if(contactsList.size() >1) {
						status = new Status("800016", PropertyUtil.getErrorMessage(800016, OCConstants.ERROR_MOBILEAPP_FLAG),
	                            OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
	                    return prepareFinalResponse(responseHeader, status);
						
					}
						
					List<CouponMatchedCustomers> matchedCustomers = new ArrayList<CouponMatchedCustomers>();
						for (Contacts contact : contactsList) {
							//if(contact.getLoyaltyCustomer() == 1 ) {
								ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
								
								ContactsLoyalty conMembership = contactsLoyaltyDao.findByContactId(user.getUserId(), contact.getContactId());
								if(conMembership != null){
									
									CouponMatchedCustomers customer = null;
									
									//List<MatchedCustomers> matchedCustomers = prepareMatchedCustomers(retResultSetForContacts, conMembership.getContact());
									customer =new CouponMatchedCustomers();
									//External ID mostly null.
									
									couponsNew = couponsDao.findCouponsNew(user.getUserId(),lastFetchedTime,sourceType);
//									couponsDeactivated = couponsDao.findCouponsDeactivated(user.getUserId(),lastFetchedTime,sourceType,lookup);
									couponsModified = couponsDao.findCouponsModified(user.getUserId(),lastFetchedTime,sourceType,lookup);
									
									if((locality!=null && !locality.isEmpty())  || (city!=null && !city.isEmpty()) || (country!=null && !country.isEmpty())||
									  (brandName!=null && !brandName.isEmpty())|| (storeName!=null && !storeName.isEmpty()) || (SBSNo!=null && !SBSNo.isEmpty())
									  || (sortOrder!=null && !sortOrder.isEmpty()) || (sortType!=null && !sortType.isEmpty())) {
								  	List<Coupons>[] coupons = zoneFilter(lookup, contact, user, sourceType, lastFetchedTime,couponsNew,couponsDeactivated,couponsModified);
								  	if(coupons == null) {
								  		status = new Status("800018", PropertyUtil.getErrorMessage(800018, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								  		return prepareFinalResponse(responseHeader, status);
								  	}
								  	couponsNew = coupons[0];
//			  						couponsDeactivated = coupons[1];
			  						couponsModified = coupons[2];
									}
									customer.setCustomerId(contact.getExternalId() == null ||  contact.getExternalId().isEmpty() ? Constants.STRING_NILL : contact.getExternalId());
									customer.setPhone(contact.getMobilePhone() == null ||  contact.getMobilePhone().isEmpty() ? Constants.STRING_NILL : contact.getMobilePhone());
									customer.setEmailAddress(contact.getEmailId() == null ||  contact.getEmailId().isEmpty() ? Constants.STRING_NILL : contact.getEmailId());
									customer.setFirstName(contact.getFirstName() == null ||  contact.getFirstName().isEmpty() ? Constants.STRING_NILL : contact.getFirstName());
									customer.setLastName(contact.getLastName() == null ||  contact.getLastName().isEmpty() ? Constants.STRING_NILL : contact.getLastName());
									customer.setMembershipNumber(conMembership.getCardNumber());
									//Setting Objects
									customer.setCouponsNew(matchedCoupons(couponsNew,sourceType,contact,user,null,couponsHistoryRequest));
									customer.setCouponsModified(matchedCoupons(couponsModified,sourceType,contact,user,null,couponsHistoryRequest));
									customer.setCouponsDeactivated(matchedCoupons(couponsDeactivated,sourceType,contact,user,null,couponsHistoryRequest));
									
									matchedCustomers.add(customer);
									
								}
								
								
							//}
							
							
						}
						status = new Status("0", "Contact lookup was successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						couponsHistoryResponse = prepareFinalResponse(responseHeader, status, matchedCustomers, new ResponseReport());
						return couponsHistoryResponse;
							
			}
			else{
				
				status = new Status("800005", PropertyUtil.getErrorMessage(800005, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
				return couponsHistoryResponse;
			}	
			
		} catch (Exception e) {
			logger.info("e===>"+e);
			status = new Status("800001", PropertyUtil.getErrorMessage(800001, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			couponsHistoryResponse = prepareFinalResponse(responseHeader, status);
			return couponsHistoryResponse;
		}
	}
	
	private Status validatePriorityMap(TreeMap<String, List<String>> prioMap, Users user) throws BaseServiceException {
		Status status = null;
		logger.debug("-------entered validatePriorityMap---------");
		if (prioMap == null || prioMap.size() == 0) {
			logger.info("Unique Priority Map NOT configured to the user: " + user.getUserName());
			status = new Status("800011", PropertyUtil.getErrorMessage(800011, OCConstants.ERROR_MOBILEAPP_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.debug("-------exit  validatePriorityMap---------");
		return status;
	}
	
	private Status validateContactPOSMap(List<POSMapping> contactPOSMap, Long userId) throws BaseServiceException {
		Status status = null;
		if (contactPOSMap == null || contactPOSMap.size() == 0) {
			status = new Status("800010", PropertyUtil.getErrorMessage(800010, OCConstants.ERROR_MOBILEAPP_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.debug("-------exit  validateContactPOSMap---------");
		return status;
	}// validateContactPOSMap
	private Calendar getDate(String date){
		try {
			DateFormat formatter;
			Date udfdDate;
			formatter = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
			udfdDate = (Date) formatter.parse(date);
			Calendar udfCal = new MyCalendar(Calendar.getInstance(), null,
					MyCalendar.FORMAT_DATETIME_STYEAR);
			udfCal.setTime(udfdDate);
			//udfDataStr = MyCalendar.calendarToString(udfCal, MyCalendar.dateFormatMap.get(dateFormat));
			return udfCal;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
	}
	
	private CouponsHistoryResponse prepareFinalResponse(
			ResponseHeader header, Status status)throws BaseServiceException {
		CouponsHistoryResponse couponsHistoryResponse = new CouponsHistoryResponse();
		
		couponsHistoryResponse.setHeader(header);
		List<CouponMatchedCustomers> matchedCustomers = new ArrayList<CouponMatchedCustomers>();
		CouponMatchedCustomers matchedCustomer = new CouponMatchedCustomers();
		matchedCustomers.add(matchedCustomer);
		couponsHistoryResponse.setMatchedCustomers(matchedCustomers);
		couponsHistoryResponse.setStatus(status);
	
		return couponsHistoryResponse;
	}//prepareFinalResponse
	
	private CouponsHistoryResponse prepareFinalResponse(
			ResponseHeader header, Status status, List<CouponMatchedCustomers> matchedCustomers, ResponseReport report)throws BaseServiceException {
		CouponsHistoryResponse couponsHistoryResponse = new CouponsHistoryResponse();
		
		couponsHistoryResponse.setHeader(header);
		couponsHistoryResponse.setMatchedCustomers(matchedCustomers);
		couponsHistoryResponse.setStatus(status);
	
		return couponsHistoryResponse;
	}//prepareFinalResponse
	private Status validateInnerObjects(CouponsHistoryRequest couponsHistoryRequest) throws Exception{
		
		Status status = null;
		Lookup lookup = couponsHistoryRequest.getLookup();
		if((lookup.getEmailAddress() == null || lookup.getEmailAddress().isEmpty()) && 
				(lookup.getPhone() == null || lookup.getPhone().isEmpty()) && 
				(lookup.getMembershipNumber() == null || lookup.getMembershipNumber().isEmpty()) && 
				(lookup.getReceiptNumber() == null || lookup.getReceiptNumber().isEmpty())) {
			status = new Status(
					"800005", PropertyUtil.getErrorMessage(800005, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
		}
		
		return status;
		
		
	}
	private Status validateJsonData(CouponsHistoryRequest couponsHistoryRequest) throws Exception{
		logger.info("Entered validateJsonData method >>>>");
		Status status = null;
		if(couponsHistoryRequest == null ){
			status = new Status(
					"800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(couponsHistoryRequest.getUser() == null){
			status = new Status(
					"800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(couponsHistoryRequest.getLookup() == null){
			status = new Status(
					"800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(couponsHistoryRequest.getLookup().getLastFetchedTime()==null ||(couponsHistoryRequest.getLookup().getLastFetchedTime()!=null && couponsHistoryRequest.getLookup().getLastFetchedTime().isEmpty() )) {
			status = new Status(
					"800017", PropertyUtil.getErrorMessage(800017, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(couponsHistoryRequest.getUser().getUserName() == null || 
				couponsHistoryRequest.getUser().getUserName().trim().length() <=0 || 
				couponsHistoryRequest.getUser().getOrganizationId() == null || 
				couponsHistoryRequest.getUser().getOrganizationId().trim().length() <=0 /*|| 
						couponsHistoryRequest.getUser().getToken() == null || 
						couponsHistoryRequest.getUser().getToken().trim().length() <=0*/) {
			status = new Status("800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
				
		return status;
	}
	
private Users getUser(String userName, String orgId, String userToken) throws Exception{
		
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findByUsername(completeUserName);
		return user;
	}

	public String getStartDate(String dateStr,ApplicationProperties serveTime ){
		
		int serveTimeStr = Integer.parseInt(serveTime.getValue());
		int timeDiff=0;
		
		String arrDate[]=dateStr.split(" ");
		if(arrDate[2]!=null) {
			String timeZone[]=arrDate[2].substring(3).split(":");
			if(Integer.parseInt(timeZone[0])<0)
				timeDiff=Integer.parseInt(timeZone[0])*60-Integer.parseInt(timeZone[1]);
			else
				timeDiff=Integer.parseInt(timeZone[0])*60+Integer.parseInt(timeZone[1]);
		}
			timeDiff=serveTimeStr-timeDiff;	
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal=MyCalendar.string2Calendar(arrDate[0]+" "+arrDate[1]);  
		cal.add(Calendar.MINUTE, timeDiff);
	
		return formatter.format(cal.getTime());
	}
	
	public List<IssuedCoupons> matchedCoupons(List<Coupons> CouponsList,String sourceType,Contacts contact,Users user,ContactsLoyalty contactsLoyalty,CouponsHistoryRequest couponHistoryRequest)
	{
		try {
		CouponDiscountGenerateDao couponDiscountGenerateDao = (CouponDiscountGenerateDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
		CouponCodesDao couponsCodesDao = (CouponCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
		List<IssuedCoupons> couponsList = new ArrayList<IssuedCoupons>();
		IssuedCoupons coupons = null;
		boolean highlightedOffers = false;
		if(couponHistoryRequest.getLookup().getHighlightedOffers() != null &&
				!couponHistoryRequest.getLookup().getHighlightedOffers().isEmpty() &&
				couponHistoryRequest.getLookup().getHighlightedOffers().equals("1"))
			highlightedOffers = true;
			
		if(CouponsList != null ) 	
		for (Coupons couponObj : CouponsList) {
			
		if(((!sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) && !sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)) || 
				(couponObj.isEnableOffer() && (sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)||sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)))) &&
		   (!highlightedOffers || (couponObj.isHighlightedOffer() && highlightedOffers))) {
		if(couponObj.getCouponGeneratedType() != null && 
				Constants.COUP_GENT_TYPE_MULTIPLE.equals(couponObj.getCouponGeneratedType())){
			
			List<CouponCodes> retList = couponsCodesDao.findIssuedCouponsByModified(user.getUserOrganization().getUserOrgId(),
					contact.getContactId(),couponObj.getCouponId() );
			
			if(retList == null || retList.isEmpty()) continue;
			
			for (CouponCodes couponCodes : retList) {

				coupons = new IssuedCoupons();
				coupons.setName(couponObj.getCouponName());
				coupons.setCouponCode(couponCodes.getCouponCode());
				coupons.setDescription(couponObj.getCouponDescription());
				
				coupons.setLoyaltyPoints(couponObj.getRequiredLoyltyPoits() != null ? couponObj.getRequiredLoyltyPoits() +Constants.STRING_NILL : Constants.STRING_NILL);
				boolean isSet = false;
				if(couponObj.getExpiryType().equals(OCConstants.COUPON_EXPIRY_TYPE_DYNAMIC)) {
					
					String expiryType = couponObj.getExpiryDetails();
					String [] strArr = expiryType.split(Constants.ADDR_COL_DELIMETER);//
					int numOfDays = Integer.parseInt(strArr[1]);
					if("I".equals(strArr[0])){
						coupons.setValidFrom((MyCalendar.calendarToString(
								couponCodes.getIssuedOn(), MyCalendar.FORMAT_DATETIME_STYEAR)));
						Calendar cal = couponCodes.getIssuedOn();
						cal.add(Calendar.DAY_OF_MONTH, numOfDays);  
						coupons.setValidTo(MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR));
						isSet = true;
					}else{
						
						if(couponCodes.getContactId() != null ){
							try {
								ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
								Contacts contacts = contactsDao.findById(couponCodes.getContactId());
								if(contacts != null) {
									
									if("B".equals(strArr[0]) && contacts.getBirthDay() != null) {
										coupons.setValidFrom(MyCalendar.calendarToString(
												contacts.getBirthDay(), MyCalendar.FORMAT_DATETIME_STYEAR));
										Calendar cal = contacts.getBirthDay();
										cal.add(Calendar.DAY_OF_MONTH, numOfDays);  
										coupons.setValidTo(MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR));
										isSet = true;
									}else if("A".equals(strArr[0]) && contacts.getAnniversary() != null){
										
										coupons.setValidFrom(MyCalendar.calendarToString(
												contacts.getAnniversary(), MyCalendar.FORMAT_DATETIME_STYEAR));
										Calendar cal = contacts.getAnniversary();
										cal.add(Calendar.DAY_OF_MONTH, numOfDays);  
										coupons.setValidTo(MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR));
										isSet = true;
									}
								}
								
							} catch (Exception e) {
								logger.info("e==>"+e);
								}
							
						}
						if(!isSet) {
							
							coupons.setValidFrom(MyCalendar.calendarToString(
									couponObj.getUserCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
							Calendar cal = Calendar.getInstance();
							cal.set(2020, 11, 31, 23, 59, 59);
							logger.info("dynamic to date "+cal.toString());
							coupons.setValidTo(MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR));
						}
					}
					
					
				}
				else {
					coupons.setValidFrom(MyCalendar.calendarToString(couponObj.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR) +" EST");
					coupons.setValidTo(MyCalendar.calendarToString(couponObj.getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STYEAR)+ " EST");
				}
				//New Mobile-APP offers field	
					coupons.setEnableOffer(couponObj.isEnableOffer());
					coupons.setBannerImage(couponObj.getBannerImage() !=null ? PropertyUtil.getPropertyValue("ImageServerUrl").trim()+"UserData/"+user.getUserName()+"/Coupon/offerBanner/"+ couponObj.getBannerImage() : "");
					coupons.setBannerUrlRedirect(couponObj.getBannerUrlRedirect() !=null ? couponObj.getBannerUrlRedirect() : "");
					String offerHeading = replacePlaceHolder(couponObj.getOfferHeading(),contact,contactsLoyalty);
					coupons.setOfferHeading(offerHeading !=null ? offerHeading : "");
					String offerDescription = replacePlaceHolder(couponObj.getOfferDescription(),contact,contactsLoyalty);
					coupons.setOfferDescription(offerDescription !=null ? offerDescription : "");
					coupons.setHighlightedOffers(couponObj.isHighlightedOffer());
					coupons.setCreatedDate(couponObj.getUserCreatedDate()!=null?MyCalendar.calendarToString(couponObj.getUserCreatedDate(),MyCalendar.FORMAT_DATETIME_STYEAR):"");
					coupons.setUpdatedDate(couponObj.getUserLastModifiedDate()!=null?MyCalendar.calendarToString(couponObj.getUserLastModifiedDate(),MyCalendar.FORMAT_DATETIME_STYEAR):"");
				
				
				
				String disCountType ="";
				String discountCriteriaStr = "";
				if(couponObj.getDiscountType().equals("Percentage") && couponObj.getDiscountCriteria().equals("Total Purchase Amount")) {
					discountCriteriaStr = "PERCENTAGE-MVP";
					disCountType = "P";
				}
				else if(couponObj.getDiscountType().equals("Percentage") && couponObj.getDiscountCriteria().equals("SKU")) {
					discountCriteriaStr = "PERCENTAGE-IC";
//					isItemCode = true;
					disCountType = "P";
				}
				else if(couponObj.getDiscountType().equals("Value") && couponObj.getDiscountCriteria().equals("Total Purchase Amount")) {
					discountCriteriaStr = "VALUE-MVP";
					disCountType = "V";
				}
				else {
					discountCriteriaStr = "VALUE-I";
//					isItemCode = true;
					disCountType = "V";
				}
				coupons.setDiscountCriteria(discountCriteriaStr);
				List<Object[]> retDiscounts = couponDiscountGenerateDao.findDistinctDisc(couponObj.getCouponId(), user.getUserId());
				List<CouponDiscount> discounts = new ArrayList<CouponDiscount>();
				for (Object[] objects : retDiscounts) {
					
					CouponDiscount discount =  new CouponDiscount();
					discount.setValue(objects[0] != null ? objects[0].toString() : Constants.STRING_NILL);
					discount.setValueCode(disCountType);
					discount.setMinimumPurchaseAmount(objects[1] != null ? objects[1].toString() : Constants.STRING_NILL);
					discounts.add(discount);
				}
				coupons.setDiscount(discounts);
				couponsList.add(coupons);
			
				
			}
			
			
		}//if
		else {
			
			coupons = new IssuedCoupons();
			coupons.setName(couponObj.getCouponName());
			coupons.setCouponCode(couponObj.getCouponCode());
			coupons.setDescription(couponObj.getCouponDescription());
			
			coupons.setLoyaltyPoints(couponObj.getRequiredLoyltyPoits() != null ? couponObj.getRequiredLoyltyPoits() +Constants.STRING_NILL : Constants.STRING_NILL);
			coupons.setValidFrom(MyCalendar.calendarToString(couponObj.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR) +" EST");
			//validTo
			coupons.setValidTo(MyCalendar.calendarToString(couponObj.getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STYEAR)+ " EST");
			//New Mobile-APP offers field changes	
				coupons.setEnableOffer(couponObj.isEnableOffer());
				coupons.setBannerImage(couponObj.getBannerImage() !=null ? PropertyUtil.getPropertyValue("ImageServerUrl").trim()+"UserData/"+user.getUserName()+"/Coupon/offerBanner/"+ couponObj.getBannerImage() : "");
				coupons.setBannerUrlRedirect(couponObj.getBannerUrlRedirect() !=null ? couponObj.getBannerUrlRedirect() : "");
				String offerHeading = replacePlaceHolder(couponObj.getOfferHeading(),contact,contactsLoyalty);
				coupons.setOfferHeading(offerHeading !=null ? offerHeading : "");
				String offerDescription = replacePlaceHolder(couponObj.getOfferDescription(),contact,contactsLoyalty);
				coupons.setOfferDescription(offerDescription !=null ? offerDescription : "");
				coupons.setHighlightedOffers(couponObj.isHighlightedOffer());
				coupons.setCreatedDate(couponObj.getUserCreatedDate()!=null?MyCalendar.calendarToString(couponObj.getUserCreatedDate(),MyCalendar.FORMAT_DATETIME_STYEAR):"");
				coupons.setUpdatedDate(couponObj.getUserLastModifiedDate()!=null?MyCalendar.calendarToString(couponObj.getUserLastModifiedDate(),MyCalendar.FORMAT_DATETIME_STYEAR):"");
			
			String disCountType ="";
			String discountCriteriaStr = "";
			if(couponObj.getDiscountType().equals("Percentage") && couponObj.getDiscountCriteria().equals("Total Purchase Amount")) {
				discountCriteriaStr = "PERCENTAGE-MVP";
				disCountType = "P";
			}
			else if(couponObj.getDiscountType().equals("Percentage") && couponObj.getDiscountCriteria().equals("SKU")) {
				discountCriteriaStr = "PERCENTAGE-IC";
//					isItemCode = true;
				disCountType = "P";
			}
			else if(couponObj.getDiscountType().equals("Value") && couponObj.getDiscountCriteria().equals("Total Purchase Amount")) {
				discountCriteriaStr = "VALUE-MVP";
				disCountType = "V";
			}
			else {
				discountCriteriaStr = "VALUE-I";
//					isItemCode = true;
				disCountType = "V";
			}
			coupons.setDiscountCriteria(discountCriteriaStr);
			List<Object[]> retDiscounts = couponDiscountGenerateDao.findDistinctDisc(couponObj.getCouponId(), user.getUserId());
			List<CouponDiscount> discounts = new ArrayList<CouponDiscount>();
			for (Object[] objects : retDiscounts) {
				
				CouponDiscount discount =  new CouponDiscount();
				discount.setValue(objects[0] != null ? objects[0].toString() : Constants.STRING_NILL);
				discount.setValueCode(disCountType);
				discount.setMinimumPurchaseAmount(objects[1] != null ? objects[1].toString() : Constants.STRING_NILL);
				discounts.add(discount);
			}
			coupons.setDiscount(discounts);
			couponsList.add(coupons);
			
			
		}
	}
	
}//for
		return couponsList;
		}
		catch(Exception e) {
			
			logger.info("e===>"+e);
			return new ArrayList<IssuedCoupons>();
			
		}
		
	}
	
	public List<Coupons>[] zoneFilter(Lookup lookup,Contacts contact,Users user,String sourceType,
			String lastFetchedTime,List<Coupons> couponsNew,
			List<Coupons> couponsDeactivated,List<Coupons> couponsModified)
	{
		//Zone related filters
		ArrayList<Coupons> coupons[] = new ArrayList[3];
		coupons[0] = new ArrayList<Coupons>();
		coupons[1] = new ArrayList<Coupons>();
		coupons[2] = new ArrayList<Coupons>();


		try {		
						
			
			
						CouponsDao couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
						
						String locality= (lookup.getLocality()!=null && !lookup.getLocality().isEmpty()) ? lookup.getLocality() : Constants.STRING_NILL;
						String city =(lookup.getCityName()!=null && !lookup.getCityName().isEmpty()) ? lookup.getCityName() : Constants.STRING_NILL; 
						String country =(lookup.getCountry()!=null && !lookup.getCountry().isEmpty()) ? lookup.getCountry() : Constants.STRING_NILL;
						String brandName =(lookup.getBrandName()!=null && !lookup.getBrandName().isEmpty())  ? lookup.getBrandName() : Constants.STRING_NILL;   
						String storeName =(lookup.getStoreName()!=null && !lookup.getStoreName().isEmpty() && lookup.getSubsidiaryNumber()!=null && !lookup.getSubsidiaryNumber().isEmpty()) ? lookup.getStoreName() : Constants.STRING_NILL;
						String SBSNo =(lookup.getSubsidiaryNumber()!=null && !lookup.getSubsidiaryNumber().isEmpty())?lookup.getSubsidiaryNumber():Constants.STRING_NILL;
						String sortOrder = (lookup.getSortOrder()!=null && !lookup.getSortOrder().isEmpty())?lookup.getSortOrder():Constants.STRING_NILL;
						String sortType = (lookup.getSortType()!=null && !lookup.getSortType().isEmpty())?lookup.getSortType():Constants.STRING_NILL;
						
						String order="desc";
						if(sortOrder!=null && !sortOrder.isEmpty()){
						order=((sortOrder.equalsIgnoreCase("asc"))?(sortOrder.toLowerCase()):order);
						}
					
						
						if((locality!=null && !locality.isEmpty())  || (city!=null && !city.isEmpty()) || (country!=null && !country.isEmpty())||
						   (brandName!=null && !brandName.isEmpty())|| (storeName!=null && !storeName.isEmpty()) || (SBSNo!=null && !SBSNo.isEmpty())) {
							
						
						UsersDao usersDao  = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
						
						List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());//finding this power user's domain
						
						if(domainsList == null) return null;
						
											
						OrganizationStoresDao orgStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
						List<OrganizationStores> orgStore = orgStoresDao.findOrgStoreByFilters(user.getUserOrganization().getUserOrgId(),domainsList.get(0).getDomainId()+Constants.STRING_NILL
								,SBSNo,brandName,locality,city,country,storeName,sortType, order);//find the Org_store record to find the which zone this belongs to
				
						if(orgStore == null || orgStore.isEmpty()) return null;
						
						//Zone mapping code
						ZoneDao zoneDao  = (ZoneDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_ZONE_DAO);
						List<OrganizationZone> orgZoneList = zoneDao.findByOrgID(orgStore.get(0).getStoreId());
						
						StringBuilder sbOrgZoneIds = new  StringBuilder();
						for(OrganizationZone orgZone:orgZoneList) {
							if(sbOrgZoneIds.length()>0)
								sbOrgZoneIds.append(Constants.DELIMETER_COMMA);
							sbOrgZoneIds.append(orgZone.getZoneId());
						}	
						if(orgZoneList == null) return null;
						//Only Coupon mapped with zone would be taken.
						coupons[0] = (ArrayList<Coupons>) couponsDao.findCouponsNewByOrgZone(user.getUserId(),lastFetchedTime,sourceType,sbOrgZoneIds.toString());
						coupons[1] = (ArrayList<Coupons>) couponsDao.findCouponsDeactivatedByOrgZone(user.getUserId(),lastFetchedTime,sourceType,sbOrgZoneIds.toString());
						coupons[2] = (ArrayList<Coupons>) couponsDao.findCouponsModifiedByOrgZone(user.getUserId(),lastFetchedTime,sourceType,sbOrgZoneIds.toString());
						
			

						coupons[0].addAll(couponListAddition(couponsNew,orgStore));
						coupons[1].addAll(couponListAddition(couponsDeactivated,orgStore));
						coupons[2].addAll(couponListAddition(couponsModified,orgStore));
				}
						//Sort- By
						else {
							OrganizationStoresDao orgStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
							List<OrganizationStores> orgStore = orgStoresDao.findOrgStoreByFilters(user.getUserOrganization().getUserOrgId(),Constants.STRING_NILL
									,SBSNo,brandName,locality,city,country,storeName,sortType, order);//find the Org_store record to find the which zone this belongs to

							coupons[0].addAll(couponListAddition(couponsNew,orgStore));
							coupons[1].addAll(couponListAddition(couponsDeactivated,orgStore));
							coupons[2].addAll(couponListAddition(couponsModified,orgStore));
						}
						
			}
		catch(Exception e) {
			logger.info("e===>"+e);
		}
		return coupons;
	}	
	
	public String replacePlaceHolder(String description,Contacts contacts,ContactsLoyalty contactsLoyalty) {
		try {
		if(description ==null || (description!=null && description.isEmpty())) return description;
		
		ContactsLoyaltyDao contactsLoyaltyDao;
		
		if(contactsLoyalty == null) {
			contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contactsLoyalty = contactsLoyaltyDao.findByContactId(contacts.getUsers().getUserId(), contacts.getContactId());
		}
		description = description.replace("|^", "[").replace("^|", "]");
		logger.info("description value is "+description);
		Set<String> totalPhSet = getCustomFields(description);
		logger.info("totalPhSet value is "+totalPhSet);

		if(totalPhSet.size()>0) {
			
		String arr[]=getContactPhValue(contacts,description,totalPhSet,contacts.getUsers(),contactsLoyalty.getLoyaltyId());
		logger.info("arr value is after getconatctphvalue  "+arr[0]);

		if(arr!=null && arr.length!=0) {
		description=arr[0];
		}
		
		}
		
		}catch(Exception e) {
			logger.info("e===>",e);
			logger.info(e);
			e.printStackTrace();
		}
		logger.info("description==>"+description);
	return description;
	
}
	
	public  String[] getContactPhValue(Contacts contact,
			String description,  Set<String> totalPhSet, Users user ,Long loyaltyId ) {
		String[] contentsStrArr = new String[4];
		
		logger.info("Entered in getContactPhValue method");

		if(totalPhSet != null && totalPhSet.size() >0) {
			StringBuffer phKeyValue = new StringBuffer();
//			contentsStrArr = new String[4];


			try {

				String value=Constants.STRING_NILL;

				String preStr = Constants.STRING_NILL; 

				for (String cfStr : totalPhSet) {
					//	logger.debug("<<<<   cfStr : "+ cfStr);
					preStr = cfStr;
					if(cfStr.startsWith("GEN_")) {
						cfStr = cfStr.substring(4);
						String defVal="";
						int defIndex = cfStr.indexOf('=');

						if(defIndex != -1) {

							defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
							cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
						} // if



						if(cfStr.equalsIgnoreCase("emailId") || cfStr.equalsIgnoreCase("email") ) {
							value = contact.getEmailId();
						}

						else if(cfStr.equalsIgnoreCase("firstName")) {
							value = contact.getFirstName();
						}
						else if(cfStr.equalsIgnoreCase("lastName"))	{
							value = contact.getLastName();
						}
						else if(cfStr.equalsIgnoreCase("addressOne")) {
							value = contact.getAddressOne();
						}
						else if(cfStr.equalsIgnoreCase("addressTwo")) {
							value = contact.getAddressTwo();
						}
						else if(cfStr.equalsIgnoreCase("city"))	{ 
							value = contact.getCity();
						}
						else if(cfStr.equalsIgnoreCase("state")) {
							value = contact.getState();
						}
						else if(cfStr.equalsIgnoreCase("country")) {
							value = contact.getCountry();
						}
						else if(cfStr.equalsIgnoreCase("pin")) {
							value = contact.getZip();
						}
						else if(cfStr.equalsIgnoreCase("phone")) {
							value = contact.getMobilePhone() != null && contact.getMobilePhone().length() != 0 ? contact.getMobilePhone() : Constants.STRING_NILL;
						}
						else if(cfStr.equalsIgnoreCase("gender")) {
							value = contact.getGender();
						}

						else if(cfStr.equalsIgnoreCase("birthday") ) {

							value = MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATEONLY_GENERAL);



						}
						else if(cfStr.equalsIgnoreCase("anniversary") ) {


							value = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATEONLY_GENERAL);


						}
						else if(cfStr.equalsIgnoreCase("createdDate") ) {


							value = MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);


						}
						
						else if(cfStr.equalsIgnoreCase("organizationName") ) {


							value = getUserOrganization(user, defVal);


						}

						else if(cfStr.toLowerCase().startsWith(OCConstants.CAMPAIGN_PH_STARTSWITH_STORE)) {



							value = getStorePlaceholders(contact,cfStr, defVal,user.getUserOrganization().getUserOrgId());

						}

						else if(cfStr.toLowerCase().startsWith(OCConstants.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {

							value = getLoyaltyPlaceholders(user.getUserId(), contact,cfStr, defVal, loyaltyId, false);

						}

						else if(cfStr.toLowerCase().startsWith(OCConstants.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS) || cfStr.toLowerCase().startsWith(OCConstants.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE)) {
							defVal = getDefaultUserAddress(contact.getUsers());
							value = getLastPurchaseStorePlaceholders(contact,cfStr, defVal,user.getUserOrganization().getUserOrgId());
							logger.info("CAMPAIGN_PH_STARTSWITH_LASETPURCHASE"+value);


						}
						else if(cfStr.toLowerCase().startsWith(OCConstants.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE) || cfStr.toLowerCase().startsWith(OCConstants.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE_ADDRESS)) {
							defVal = getDefaultUserAddress(contact.getUsers());
                            value = getstoreaddress(contact,cfStr, defVal,user.getUserOrganization().getUserOrgId());
                            logger.info("CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE"+value);
                        }

						else {
							value = Constants.STRING_NILL;
						}
							if(logger.isInfoEnabled()) logger.info(">>>>>>>>> Gen token <<<<<<<<<<< :" + cfStr + " - Value :" + value);
							try {
								
								if(value != null && !value.trim().isEmpty()) {
									
									value = ( value.equals("--") &&  defVal != null) ? defVal : value;
									//cfStr = cfStr.toLowerCase();
									description = description.replace("["+preStr+"]", value);
									
																						
								} else {
									
									value = defVal;
									description = description.replace("["+preStr+"]", value);
									
									
								}
									
							} catch (Exception e) {
								logger.error("Exception while adding the General Fields as place holders ", e);
							}
						} 
						//Changes to add mapped UDF fields as placeholders
						else if(cfStr.startsWith(Constants.UDF_TOKEN) ) {
							 	
								cfStr = cfStr.substring(4);
								String defVal="";
								int defIndex = cfStr.indexOf('=');
								
								if(defIndex != -1) {
									/*defVal = cfStr.substring(defIndex+1);
									cfStr = cfStr.substring(0,defIndex);*/
									defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
									cfStr =cfStr.substring(0,cfStr.indexOf("/")).trim();
								} // if
								
								int UDFIdx = Integer.parseInt(cfStr.substring("UDF".length()));
								try {
								//skuFile = setSKUCustFielddata(skuFile, UDfIdx, udfDataStr);
								value = getConatctCustFields(contact, UDFIdx, defVal);
								
								if(value==null || value.isEmpty()) value=defVal;
								
							} catch (Exception e) {
								logger.error("Exception ::::", e);
								logger.info("Exception error getting while setting the Udf value due to wrong values existed from the sku csv file .. so we ignore the udf data.. ");
								value = Constants.STRING_NILL;
							}
							
							if(value != null && !value.trim().isEmpty()) {
								description = description.replace("["+preStr+"]", value);
																												
							} else {
								value =defVal ;
								description = description.replace("["+preStr+"]", value);
							}
						}
						else {
							if(logger.isDebugEnabled()) logger.debug("<<<< --2-->>>");
							cfStr = cfStr.substring(3);
							// Removing if the PH if key is not found..
							description = description.replace("["+"CF_" + cfStr+"]", Constants.STRING_NILL);
						}
						
						//Placeholders key value pair preparation for weblink url replacement
						
						
						if(phKeyValue.length() > 0) phKeyValue.append(Constants.ADDR_COL_DELIMETER)  ;
						
						phKeyValue.append("[" + preStr + "]" + Constants.DELIMETER_DOUBLECOLON + value);
						
						
						
					}	
					
					contentsStrArr[0] = description;
					if(phKeyValue.toString().trim().length() > 0) {
						
						contentsStrArr[3] = phKeyValue.toString();
						
					}else{
						
						contentsStrArr[3] = null;
					}
					
			} catch (Exception e) {
						logger.error("Exception while adding the Custom Fields as place holders ", e);
			}
		} // If PH exist
		
		logger.info("contentsStrArr value is "+contentsStrArr);

		return contentsStrArr;

	}
	private String getUserOrganization(Users user, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getUserOrganization");
		String organizationName = defVal;
		try{
			organizationName = user.getUserOrganization().getOrganizationName();
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed getUserOrganization");
		return organizationName;
	}//getUserOrganization
	private String getStorePlaceholders(Contacts contact, String placeholder, String defVal,Long orgId){

		String storePlaceholder = defVal;//Constants.STRING_NILL;
		OrganizationStoresDao organizationStoresDao = null;
		try {
			organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("e==>"+e);
		}
		
		if(contact.getHomeStore() != null && orgId != null) {
			//logger.info("-----------1----------------");
			OrganizationStores organizationStores = organizationStoresDao.findByStoreLocationId(orgId, contact.getHomeStore());

			if(organizationStores != null){

				if(placeholder.equals(OCConstants.CAMPAIGN_PH_STORENAME)){ 
					storePlaceholder = organizationStores.getStoreName() ;
				}
				else if(placeholder.equals(OCConstants.CAMPAIGN_PH_STOREMANAGER)){
					storePlaceholder = organizationStores.getStoreManagerName();
				}
				else if(placeholder.equals(OCConstants.CAMPAIGN_PH_STOREEMAIL)){
					storePlaceholder = organizationStores.getEmailId();
				}
				else if(placeholder.equals(OCConstants.CAMPAIGN_PH_STOREPHONE)){
					storePlaceholder = organizationStores.getAddress().getPhone();
				}
				else if(placeholder.equals(OCConstants.CAMPAIGN_PH_STORESTREET)){
					storePlaceholder = organizationStores.getAddress().getAddressOne();
				}
				else if(placeholder.equals(OCConstants.CAMPAIGN_PH_STORECITY)){
					storePlaceholder = organizationStores.getAddress().getCity() ;
				}
				else if(placeholder.equals(OCConstants.CAMPAIGN_PH_STORESTATE)){
					storePlaceholder = organizationStores.getAddress().getState();
				}
				else if(placeholder.equals(OCConstants.CAMPAIGN_PH_STOREZIP)){
					storePlaceholder = organizationStores.getAddress().getPin() != null ? organizationStores.getAddress().getPin()+Constants.STRING_NILL:Constants.STRING_NILL;
				}



			}	
		}		
		return storePlaceholder == null || storePlaceholder.trim().isEmpty() ? defVal : storePlaceholder;
	}//getStorePlaceholders()

	private  String getConatctCustFields(Contacts contact, int index, String defVal)  {

		String retVal = defVal;

		try {
			switch(index){
			case 1: retVal = contact.getUdf1()!= null ? contact.getUdf1() : defVal; 
			break; 
			case 2: retVal = contact.getUdf2()!= null ? contact.getUdf2() : defVal; 
			break;  
			case 3: retVal = contact.getUdf3()!= null ? contact.getUdf3() : defVal;
			break;  
			case 4: retVal = contact.getUdf4()!= null ? contact.getUdf4() : defVal;
			break;  
			case 5: retVal = contact.getUdf5()!= null ? contact.getUdf5() : defVal; 
			break;  
			case 6: retVal = contact.getUdf6()!= null ? contact.getUdf6() : defVal; 
			break;  
			case 7: retVal = contact.getUdf7()!= null ? contact.getUdf7() : defVal; 
			break;  
			case 8: retVal = contact.getUdf8()!= null ? contact.getUdf8() : defVal; 
			break;  
			case 9: retVal = contact.getUdf9()!= null ? contact.getUdf9() : defVal; 
			break;  
			case 10: retVal = contact.getUdf10()!= null ? contact.getUdf10() : defVal; 
			break;  
			case 11: retVal = contact.getUdf11()!= null ? contact.getUdf11() : defVal; 
			break;  
			case 12: retVal = contact.getUdf12()!= null ? contact.getUdf12() : defVal; 
			break;  
			case 13: retVal = contact.getUdf13()!= null ? contact.getUdf13() : defVal; 
			break;  
			case 14: retVal = contact.getUdf14()!= null ? contact.getUdf14() : defVal; 
			break;  
			case 15: retVal = contact.getUdf15()!= null ? contact.getUdf15() : defVal; 
			break;  
			}
			
		} catch (Exception e) {
			return defVal;
		}
		if (retVal == null) retVal = defVal; 	

		return retVal;



	} // setConatctCustFields


	private String getLoyaltyPlaceholders(Long userId, Contacts contact, String placeholder, String defVal, Long loyaltyId, boolean isSms) {


		try {

			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			String loyaltyPlaceholder =defVal;
			DecimalFormat f=new DecimalFormat("#0.00"); 
			ContactsLoyalty contactsLoyalty = null;

			if(loyaltyId != null){
				logger.info("contactsLoyaltyDao ="+contactsLoyaltyDao);

				contactsLoyalty = contactsLoyaltyDao.findAllByLoyaltyId(loyaltyId.longValue());
				logger.info("contactsLoyalty ="+contactsLoyaltyDao);
			}
			else{
				contactsLoyalty = contactsLoyaltyDao.findByContactId(userId, contact.getContactId());
			}
			if(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED.equalsIgnoreCase(contactsLoyalty.getMembershipStatus()) && contactsLoyalty.getTransferedTo() != null){
				
				contactsLoyalty = contactsLoyaltyDao.findAllByLoyaltyId(contactsLoyalty.getTransferedTo());
			}
			//if(contact.getContactId() != null && contact.getLoyaltyCustomer() != null ){ 
			//ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findByContactId(campaign.getUsers().getUserOrganization().getUserOrgId(), contact.getContactId());


			if(logger.isInfoEnabled()) logger.info("gotloyalty obj ==="+contactsLoyalty+" "+placeholder);
			if(contactsLoyalty != null){

				//TODO new Place holders to be added
				loyaltyPlaceholder = replaceLoyaltyPlaceHolders(placeholder,contactsLoyalty,defVal, isSms);
				logger.info("**************************** loyaltyPlaceholder Id ************* "+loyaltyPlaceholder);
			}
			return loyaltyPlaceholder != null && !loyaltyPlaceholder.trim().isEmpty() ? loyaltyPlaceholder : defVal;


		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Contacts loyalty place holders: "+e);
			//logger.error("Exception ::::", e);
			return null;
		}
	}


	
	
	private String replaceLoyaltyPlaceHolders(String placeholder,ContactsLoyalty contactsLoyalty,String defVal, boolean isSms) {

		if(contactsLoyalty == null){
			logger.error("contactsLoyalty is null :: "+contactsLoyalty);
			logger.error("defVal is null :: "+defVal);
			return defVal;
		}
		if(placeholder == null  && defVal == null){
			logger.error("Value is null placeholder :: "+placeholder+"\t contactsLoyalty "+contactsLoyalty+"\t defVal"+defVal);
			return defVal;
		}

		logger.info("In replaceLoyaltyPlaceHolders :: " +  placeholder);
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		String loyaltyPlaceholder="";
		//OC LOYALTYCARDPIN
		if(OCConstants.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PIN.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin(): defVal;
			logger.info("OC Membership Pin ::"+contactsLoyalty.getCardPin());
		}
		//SB LOYALTYCARDPIN
		else if(OCConstants.CAMPAIGN_PH_LOYALTYCARDPIN.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin(): defVal;
			logger.info("SB Membership Pin ::"+contactsLoyalty.getCardPin());
		}
		//REFRESHEDON
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_REFRESHEDON.equalsIgnoreCase(placeholder) ){
			loyaltyPlaceholder = contactsLoyalty.getLastFechedDate() ==  null ? defVal :  MyCalendar.calendarToString(contactsLoyalty.getLastFechedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);
		}
		//OC MEMBERSHIP_NUMBER
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_NUMBER.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber()+"":defVal;
			logger.info("OC Membership Number ::"+contactsLoyalty.getCardNumber());
		}
		//SB MEMBERSHIP_NUMBER
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_CARDNUMBER.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber()+"":defVal;
			logger.info("SB Membership Number ::"+contactsLoyalty.getCardNumber());
		}
		//MEMBER_TIER
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_MEMBER_TIER.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getProgramTierId() != null ? getMemberTier(contactsLoyalty.getProgramTierId() , defVal) : defVal; 
			logger.info("Member Tier ::"+loyaltyPlaceholder);
		}
		//MEMBER_STATUS
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_MEMBER_STATUS.equalsIgnoreCase(placeholder)){
			logger.info("MEMBER_STATUS contactsLoyalty.getMembershipStatus() ::"+contactsLoyalty.getMembershipStatus());
			loyaltyPlaceholder = contactsLoyalty.getMembershipStatus() != null ? contactsLoyalty.getMembershipStatus() : defVal;
			logger.info("MEMBER_STATUS ::"+loyaltyPlaceholder);
		}
		//LOYALTY_ENROLLMENT_DATE
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_ENROLLMENT_DATE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCreatedDate() != null ?  MyCalendar.calendarToString(contactsLoyalty.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR) : defVal ;
			logger.info("LOYALTY_ENROLLMENT_DATE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_ENROLLMENT_SOURCE
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_ENROLLMENT_SOURCE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getContactLoyaltyType() != null ? getEnrollmentSource(contactsLoyalty.getContactLoyaltyType() , defVal) : defVal;
			logger.info("LOYALTY_ENROLLMENT_SOURCE ::"+loyaltyPlaceholder);
		}//LOYALTY_ENROLLMENT_STORE
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_ENROLLMENT_STORE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getPosStoreLocationId() != null ? contactsLoyalty.getPosStoreLocationId()+"":defVal;
			logger.info("LOYALTY_ENROLLMENT_STORE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_REGISTERED_PHONE
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_REGISTERED_PHONE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getMobilePhone() != null ? contactsLoyalty.getMobilePhone():defVal;
			logger.info("LOYALTY_REGISTERED_PHONE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_POINTS_BALANCE
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_POINTS_BALANCE.equalsIgnoreCase(placeholder)) {
			loyaltyPlaceholder = contactsLoyalty.getLoyaltyBalance() != null ? contactsLoyalty.getLoyaltyBalance().longValue()+" Points" : defVal;
		//OC LOYALTY_MEMBERSHIP_CURRENCY_BALANCE
		}else if(OCConstants.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_CURRENCY_BALANCE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getGiftcardBalance() != null ?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) : defVal;
			logger.info("LOYALTY_MEMBERSHIP_CURRENCY_BALANCE ::"+loyaltyPlaceholder);
		}
		//SB GIFTCARD_BALANCE
		else if(OCConstants.CAMPAIGN_PH_GIFTCARD_BALANCE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getGiftcardBalance() != null ?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) : defVal;
			logger.info("GIFTCARD_BALANCE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_GIFT_BALANCE
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_GIFT_BALANCE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getGiftBalance() != null ? decimalFormat.format(contactsLoyalty.getGiftBalance()) : defVal;
			logger.info("LOYALTY_GIFT_BALANCE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_GIFT_CARD_EXPIRATION
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_GIFT_CARD_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getGiftCardExpirationDate(contactsLoyalty ,defVal);
			logger.info("LOYALTY_GIFT_CARD_EXPIRATION ::"+loyaltyPlaceholder);
		}
		//LOYALTY_HOLD_BALANCE
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_HOLD_BALANCE.equalsIgnoreCase(placeholder)) {
			loyaltyPlaceholder = getHoldBalance(contactsLoyalty,defVal);
			logger.info("LOYALTY_HOLD_BALANCE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_REWARD_ACTIVATION_PERIOD
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_REWARD_ACTIVATION_PERIOD.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getProgramTierId() != null ? getRewardActivationPeriod(contactsLoyalty.getProgramTierId(),defVal) : defVal ;
			logger.info("LOYALTY_REWARD_ACTIVATION_PERIOD::"+loyaltyPlaceholder);
		}
		//LOYALTY_LAST_EARNED_VALUE(changes for transfer)
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_LAST_EARNED_VALUE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastEarnedValue(contactsLoyalty.getLoyaltyId(), OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,defVal,contactsLoyalty.getUserId()) : defVal;
			logger.info("LOYALTY_LAST_EARNED_VALUE::"+loyaltyPlaceholder);
		}
		//LOYALTY_LAST_REDEEMED_VALUE(changes for transfer)
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_LAST_REDEEMED_VALUE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastRedeemedValue(contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,defVal,contactsLoyalty.getUserId()) : defVal;
			logger.info("LOYALTY_LAST_REDEEMED_VALUE::"+loyaltyPlaceholder);
		}
		else if(OCConstants. CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PASSWORD.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getMembershipPassword(contactsLoyalty,defVal);
			logger.info("LOYALTY_MEMBERSHIP_PASSWORD::"+loyaltyPlaceholder);
		} 
		//LOYALTY_LOGIN_URL
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_LOGIN_URL.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getLoyaltyURL(contactsLoyalty,defVal,isSms);
			logger.info("LOYALTY_LOGIN_URL::"+loyaltyPlaceholder);
		}
		/*//ORGANIZATION_NAME
		else if(OCConstants.CAMPAIGN_PH_ORGANIZATION_NAME.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getUserOrganization(contactsLoyalty,defVal);
			logger.info("PH_ORGANIZATION_NAME::"+loyaltyPlaceholder);
		} */
		//REWARD_EXPIRATION_PERIOD
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_REWARD_EXPIRATION_PERIOD.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getRewardExpirationPeriod(contactsLoyalty ,defVal);
			logger.info("LOYALTY_REWARD_EXPIRATION_Period ::"+loyaltyPlaceholder);
		}
		//MEMBERSHIP_EXPIRATION_DATE
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getLoyaltyMembershipExpirationDate(contactsLoyalty, defVal);	
			logger.info("MEMBERSHIP_EXPIRATION_DATE ::"+loyaltyPlaceholder);
		}
		//LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getGiftAmountExpirationPeriod(contactsLoyalty,defVal);
			logger.info("LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD :: "+loyaltyPlaceholder);
		}
		//LOYALTY_LAST_BONUS_VALUE(changes for transfer)
		else if(OCConstants.CAMPAIGN_PH_LOYALTY_LAST_BONUS_VALUE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastBonusValue(contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_BONUS,defVal,contactsLoyalty.getUserId()) : defVal;
			logger.info("LOYALTY_LAST_BONUS_VALUE :: "+loyaltyPlaceholder);
		}
		//REWARD_EXPIRING_VALUE(changes for transfer)
		else if(OCConstants.CAMPAIGN_PH_REWARD_EXPIRING_VALUE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getRewardExpiringValue(contactsLoyalty,defVal);
			logger.info("REWARD_EXPIRING_VALUE :: "+loyaltyPlaceholder);
		}
		//GIFT_AMOUNT_EXPIRING_VALUE(changes for transfer)
		else if(OCConstants.CAMPAIGN_PH_GIFT_AMOUNT_EXPIRING_VALUE.equalsIgnoreCase(placeholder)){
			loyaltyPlaceholder = getGiftAmountExpiringValue(contactsLoyalty,defVal);
			logger.info("GIFT_AMOUNT_EXPIRING_VALUE :: "+loyaltyPlaceholder);
		}

		logger.info("Completed replace holder method");
		return loyaltyPlaceholder;
	}//replaceLoyaltyOCConstants
	private String getMemberTier(Long programTierId,String defValue) {

		LoyaltyProgramTier loyaltyProgramTier = null;
		//helper class obj
		LoyaltyHelper loyaltyHelper= new LoyaltyHelper();
		String tier = "" ,level ="",loyaltyPlaceholder="";

		loyaltyProgramTier =    loyaltyHelper.getTierObj(programTierId);
		if(loyaltyProgramTier != null){
			tier = loyaltyProgramTier.getTierName() ;
			level = " ( Level : "+(loyaltyProgramTier.getTierType() == null ? "" : loyaltyProgramTier.getTierType())+" )";
			loyaltyPlaceholder = tier + level ; //it will tier name + level
		}
		else{
			loyaltyPlaceholder = defValue; //default value to be replaced
		}
		return loyaltyPlaceholder;
	}//getMemberTier

	private String getGiftCardExpirationDate(ContactsLoyalty contactsLoyalty, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getGiftCardExpirationDate");
		String giftCardExpriationDate = defVal;

		LoyaltyHelper loyaltyHelper= new LoyaltyHelper();
		LoyaltyProgram loyaltyProgram =  null;

		if(contactsLoyalty.getProgramId() != null){
			loyaltyProgram = loyaltyHelper.getProgmObj(contactsLoyalty.getProgramId());
			if(loyaltyProgram != null && contactsLoyalty.getRewardFlag() != null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
				if(loyaltyProgram.getGiftMembrshpExpiryFlag() == 'Y' && loyaltyProgram.getGiftMembrshpExpiryDateType() != null 
						&& loyaltyProgram.getGiftMembrshpExpiryDateValue() != null){
					giftCardExpriationDate = LoyaltyProgramHelper.getGiftMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), 
							loyaltyProgram.getGiftMembrshpExpiryDateType(), loyaltyProgram.getGiftMembrshpExpiryDateValue());
				}//if 
			}
		}
		logger.debug("<<<<<<<<<<<<< completed getGiftCardExpirationDate ");
		return giftCardExpriationDate;
	}//getGiftCardExpirationDate

	private String getEnrollmentSource(String loyaltyType, String defVal) {
		String loyaltyPH = "";
		if(Constants.CONTACT_LOYALTY_TYPE_POS.equalsIgnoreCase(loyaltyType)) {
			loyaltyPH = Constants.CONTACT_LOYALTY_TYPE_STORE;
		}
		else {
			loyaltyPH = loyaltyType;
		}
		return loyaltyPH;
	}//getEnrollmentSource	
	private String getHoldBalance(ContactsLoyalty contactsLoyalty,String defVal) {
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		String loyaltyPlaceholder ="";
		if(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldPointsBalance() != null){
			loyaltyPlaceholder = decimalFormat.format(contactsLoyalty.getHoldAmountBalance()) +" & "+contactsLoyalty.getHoldPointsBalance().intValue()+ " Points";;
		}
		else if(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldPointsBalance() == null){
			loyaltyPlaceholder = decimalFormat.format(contactsLoyalty.getHoldAmountBalance());
		}
		else if(contactsLoyalty.getHoldAmountBalance() == null && contactsLoyalty.getHoldPointsBalance() != null){
			loyaltyPlaceholder = contactsLoyalty.getHoldPointsBalance().intValue() + " Points";
		}
		else{
			loyaltyPlaceholder =  defVal;
		}
		return loyaltyPlaceholder;
	}//getHoldBalance
	private String getMembershipPassword(ContactsLoyalty contactsLoyalty,String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getMembershipPassword");
		String password = defVal;
		/*try {
			if(!contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
				password = contactsLoyalty.getMembershipPwd() != null ? EncryptDecryptLtyMembshpPwd.decrypt(contactsLoyalty.getMembershipPwd()) : defVal;
			}
		} catch (Exception e) {
			logger.error("Expection while replacing place holder :: ",e);
		}*/
		logger.debug("<<<<<<<<<<<<< completed getMembershipPassword ");
		return password;
	}//getMembershipPassword

	private String getRewardActivationPeriod(Long programTierId,String defValue) {
		LoyaltyProgramTier loyaltyProgramTier = null;
		String loyaltyPlaceholder = defValue;
		//helper class obj
		LoyaltyHelper loyaltyHelper= new LoyaltyHelper();
		loyaltyProgramTier = loyaltyHelper.getTierObj(programTierId);
		if(loyaltyProgramTier != null && loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES){
			loyaltyPlaceholder = loyaltyProgramTier.getPtsActiveDateValue() +" "+ loyaltyProgramTier.getPtsActiveDateType()+OCConstants.MORETHANONEOCCURENCE;
		}
		else{
			loyaltyPlaceholder = defValue ;
		}
		return loyaltyPlaceholder;
	}//getRewardActivationPeriod
	private String getLoyaltyURL(ContactsLoyalty contactsLoyalty,String defVal, boolean isSms) {
		
		logger.debug(">>>>>>>>>>>>> entered in getLoyaltyURL");
		String loyaltyUrl = defVal;
		try {
		LoyaltyHelper loyaltyHelper= new LoyaltyHelper();
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.find(contactsLoyalty.getUserId());
		LoyaltySettings loyaltySettings = loyaltyHelper.findLoyaltySettingsByOrgId(user.getUserOrganization().getUserOrgId());

		if(loyaltySettings != null){
			loyaltyUrl = loyaltySettings.getUrlStr();
			if(!isSms){
				loyaltyUrl = "<a href="+loyaltyUrl+">"+loyaltyUrl+"</a>";
			}
		}
		logger.debug("<<<<<<<<<<<<< completed getLoyaltyURL ");
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return loyaltyUrl;
	}//getLoyaltyURL
	private String getLastEarnedValue(Long loyaltyId,String loyaltyTransTypeIssuance,String defValue,Long userId) {
		String loyaltyPlaceholder = "";
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		//helper class obj
		LoyaltyHelper loyaltyHelper =new LoyaltyHelper();
		LoyaltyTransactionChild child = null;
		child = loyaltyHelper.getTransByMembershipNoAndTransType(loyaltyId, loyaltyTransTypeIssuance,userId);
		if(child != null){
			if(child.getEarnedAmount() != null && child.getEarnedPoints() != null){
				loyaltyPlaceholder = decimalFormat.format(child.getEarnedAmount())+" & "+child.getEarnedPoints().intValue()+" Points";
			}
			else if(child.getEarnedAmount() != null && child.getEarnedPoints() == null){
				loyaltyPlaceholder = decimalFormat.format(child.getEarnedAmount());
			}
			else if(child.getEarnedAmount() == null && child.getEarnedPoints() != null){
				loyaltyPlaceholder = child.getEarnedPoints().intValue()+" Points";
			}
			else{
				loyaltyPlaceholder = defValue;
			}
		}
		else{
			loyaltyPlaceholder = defValue;
		}
		return loyaltyPlaceholder;
	}//getLastEarnedValue

	private String getRewardExpirationPeriod(ContactsLoyalty contactsLoyalty, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getRewardExpirationPeriod");
		String rewardExpirationPeriod = defVal;

		Long tierId =  contactsLoyalty.getProgramTierId();

		if(tierId != null){
			LoyaltyHelper loyaltyHelper =new LoyaltyHelper();
			LoyaltyProgramTier loyaltyProgramTier = null;
			LoyaltyProgram loyaltyProgram =  null;

			if(contactsLoyalty.getProgramId() != null  && contactsLoyalty.getRewardFlag() != null){
				loyaltyProgram = loyaltyHelper.getProgmObj(contactsLoyalty.getProgramId());
				loyaltyProgramTier = loyaltyHelper.getTierObj(contactsLoyalty.getProgramTierId());

				if(loyaltyProgram != null && loyaltyProgramTier != null && loyaltyProgram.getRewardExpiryFlag()==OCConstants.FLAG_YES){

					if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) || OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

						if(loyaltyProgramTier != null && loyaltyProgramTier.getRewardExpiryDateValue() != null 
								&& loyaltyProgramTier.getRewardExpiryDateValue() != 0 
								&& loyaltyProgramTier.getRewardExpiryDateType() != null 
								&& !loyaltyProgramTier.getRewardExpiryDateType().isEmpty())
						{
							rewardExpirationPeriod = loyaltyProgramTier.getRewardExpiryDateValue()+" "
									+loyaltyProgramTier.getRewardExpiryDateType()+OCConstants.MORETHANONEOCCURENCE;
						}//if

					}//if oc 
				}//if lty !=null
			}//if cont
		}//tier id
		logger.debug("<<<<<<<<<<<<< completed getRewardExpirationPeriod ");
		return rewardExpirationPeriod;
	}//getRewardExpirationPeriod
	private String getLoyaltyMembershipExpirationDate(ContactsLoyalty contactsLoyalty, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getLoyaltyMembershipExpriationDate");
		String membershipExpriationDate = defVal;

		LoyaltyProgramTier loyaltyProgramTier = null;
		LoyaltyProgram loyaltyProgram =  null;
		LoyaltyHelper loyaltyHelper = new LoyaltyHelper();
		if(contactsLoyalty.getProgramId() != null && contactsLoyalty.getProgramTierId() != null && contactsLoyalty.getRewardFlag() != null){
			loyaltyProgram = loyaltyHelper.getProgmObj(contactsLoyalty.getProgramId());
			loyaltyProgramTier = loyaltyHelper.getTierObj(contactsLoyalty.getProgramTierId());

			if(loyaltyProgram != null && loyaltyProgramTier != null){

				if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) || OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
					////if flag L or GL
					if(loyaltyProgram.getMembershipExpiryFlag() == 'Y' && loyaltyProgramTier.getMembershipExpiryDateType() != null 
							&& loyaltyProgramTier.getMembershipExpiryDateValue() != null){
						
						boolean upgdReset = loyaltyProgram.getMbrshipExpiryOnLevelUpgdFlag() == 'Y' ? true : false;

						membershipExpriationDate = LoyaltyProgramHelper.getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), contactsLoyalty.getTierUpgradedDate(), 
								upgdReset, loyaltyProgramTier.getMembershipExpiryDateType(), loyaltyProgramTier.getMembershipExpiryDateValue());
					}
				}//if

			}//loyaltyProgram && loyaltyProgramTier 
		}
		logger.debug("<<<<<<<<<<<<< completed getLoyaltyMembershipExpriationDate ");
		return membershipExpriationDate;
	}//getLoyaltyMembershipExpriationDate
	
	private String getGiftAmountExpirationPeriod(ContactsLoyalty contactsLoyalty, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getGiftAmountExpirationPeriod");
		String giftAmountExpirationPeriod = defVal;

		LoyaltyHelper loyaltyHelper =new LoyaltyHelper();
		LoyaltyProgram loyaltyProgram =  null;

		if(contactsLoyalty.getProgramId() != null  && contactsLoyalty.getRewardFlag() != null){
			loyaltyProgram = loyaltyHelper.getProgmObj(contactsLoyalty.getProgramId());

			if(loyaltyProgram != null && loyaltyProgram.getGiftMembrshpExpiryFlag() == OCConstants.FLAG_YES){

				if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) ||
						OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

					if(loyaltyProgram.getGiftAmountExpiryDateValue() != null  && loyaltyProgram.getGiftAmountExpiryDateValue() != 0 
							&& loyaltyProgram.getGiftAmountExpiryDateType() != null && !loyaltyProgram.getGiftAmountExpiryDateType().isEmpty())
					{
						giftAmountExpirationPeriod = loyaltyProgram.getGiftAmountExpiryDateValue()+" "+loyaltyProgram.getGiftAmountExpiryDateType()+OCConstants.MORETHANONEOCCURENCE;
					}//if

				}//if oc 
			}//if lty !=null
		}//if cont
		logger.debug("<<<<<<<<<<<<< completed getGiftAmountExpirationPeriod ");
		return giftAmountExpirationPeriod;
	}//getGiftAmountExpirationPeriod
	
	private String getRewardExpiringValue(ContactsLoyalty contactsLoyalty,String defVal) {
		logger.info("--Start of getRewardExpiringValue--");
		String rewardExpVal = defVal ;
		try {
		if(contactsLoyalty.getProgramId()== null) return rewardExpVal;
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		LoyaltyProgram program = loyaltyProgramDao.findById(contactsLoyalty.getProgramId());
		
		if(contactsLoyalty.getProgramTierId()== null) return rewardExpVal;
		LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		LoyaltyProgramTier loyaltyProgramTier = loyaltyProgramTierDao.findByTierId(contactsLoyalty.getProgramTierId());
		
		if(OCConstants.FLAG_YES == program.getRewardExpiryFlag() && loyaltyProgramTier.getRewardExpiryDateType() != null 
				&& loyaltyProgramTier.getRewardExpiryDateValue() != null){

			Calendar cal = Calendar.getInstance();
			if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyProgramTier.getRewardExpiryDateType())){
				cal.add(Calendar.MONTH, -(loyaltyProgramTier.getRewardExpiryDateValue().intValue()));
			}
			else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyProgramTier.getRewardExpiryDateType())){
				cal.add(Calendar.YEAR, -(loyaltyProgramTier.getRewardExpiryDateValue().intValue()));
			}
			
			String expDate = "";
			if(cal.get(Calendar.MONTH) == 11) {
				expDate = cal.get(Calendar.YEAR)+"-12";
			} 
			else {
				cal.add(Calendar.MONTH, 1);
				expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
			}
			logger.info("expDate = "+expDate);
			Object[] expiryValueArr = fetchExpiryValues(contactsLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);

			if(expiryValueArr != null ) { 
				DecimalFormat decimalFormat = new DecimalFormat("#0.00");
				if(expiryValueArr[1] != null && Long.valueOf(expiryValueArr[1].toString()) > 0 && expiryValueArr[2] != null
						&& Double.valueOf(expiryValueArr[2].toString()) >  0.0){
					rewardExpVal = Long.valueOf(expiryValueArr[1].toString())+" Points"+
												" & "+decimalFormat.format(Double.valueOf(expiryValueArr[2].toString()));
				}
				else if(expiryValueArr[1] != null && Long.valueOf(expiryValueArr[1].toString()) > 0 && (expiryValueArr[2] == null ||
						Double.valueOf(expiryValueArr[2].toString()) == 0.0)) {
					rewardExpVal = Long.valueOf(expiryValueArr[1].toString())+" Points";
				}
				else if(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) >  0.0
						&& (expiryValueArr[1] == null || Long.valueOf(expiryValueArr[1].toString()) == 0)){
					rewardExpVal = decimalFormat.format(Double.valueOf(expiryValueArr[2].toString()));
				}
				else {
					rewardExpVal = defVal;
				}
			}
		}
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		logger.info("--Exit of getRewardExpiringValue--");
		return rewardExpVal;
	}//getRewardExpiringValue
	
	private String getGiftAmountExpiringValue(ContactsLoyalty contactsLoyalty,String defVal) {
		logger.info("--Start of getGiftAmountExpiringValue--");
		String giftExpValue = defVal;
		try {
			if(contactsLoyalty.getProgramId()== null) return giftExpValue;
			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			LoyaltyProgram program = loyaltyProgramDao.findById(contactsLoyalty.getProgramId());
		
		if(OCConstants.FLAG_YES == program.getGiftAmountExpiryFlag() && program.getGiftAmountExpiryDateType() != null 
				&& program.getGiftAmountExpiryDateValue() != null){
			
			Calendar cal = Calendar.getInstance();
			if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(program.getGiftAmountExpiryDateType())){
				cal.add(Calendar.MONTH, -(program.getGiftAmountExpiryDateValue().intValue()));
			}
			else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(program.getGiftAmountExpiryDateType())){
				cal.add(Calendar.YEAR, -(program.getGiftAmountExpiryDateValue().intValue()));
			}
			String expDate = "";
			if(cal.get(Calendar.MONTH) == 11) {
				expDate = cal.get(Calendar.YEAR)+"-12";
			} 
			else {
				cal.add(Calendar.MONTH, 1);
				expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
			}
			logger.info("expDate = "+expDate);
			
			Object[] expiryValueArr = fetchExpiryValues(contactsLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G);
			
			if(expiryValueArr != null && expiryValueArr[2] != null){
				DecimalFormat decimalFormat = new DecimalFormat("#0.00");
				double expGift = Double.valueOf(expiryValueArr[2].toString());
				if(expGift > 0){
					giftExpValue = decimalFormat.format(expGift);  
				}
			}
		}
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		logger.info("--Exit of getGiftAmountExpiringValue--");
		return giftExpValue;
	}//getGiftAmountExpiringValue

	private String getLastRedeemedValue(Long loyaltyId,String loyaltyTransTypeRedemption,String defValue,Long userId) {
		String loyaltyPlaceholder = "";
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		//helper class obj
		LoyaltyHelper loyaltyHelper =new LoyaltyHelper();
		LoyaltyTransactionChild child = null;
		child = loyaltyHelper.getTransByMembershipNoAndTransType(loyaltyId, loyaltyTransTypeRedemption,userId);
		if(child != null){
			if(child.getEnteredAmount() != null && child.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM)){
				loyaltyPlaceholder = child.getEnteredAmount().intValue()+" Points";
			}
			else if(child.getEnteredAmount() != null && child.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM)){
				loyaltyPlaceholder = decimalFormat.format(child.getEnteredAmount());
			}
			else{
				loyaltyPlaceholder = defValue;
			}
		}
		else{
			loyaltyPlaceholder = defValue;
		}
		return loyaltyPlaceholder;

	}//getLastRedeemedValue
	private String getLastBonusValue(Long loyaltyId,String transactionType, String defVal,Long userId) {
		logger.info("--Start of getLastBonusValue--");
		String loyaltyPlaceholder = "";
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		LoyaltyHelper loyaltyHelper =new LoyaltyHelper();
		LoyaltyTransactionChild loyaltyTransactionChild = null;
		loyaltyTransactionChild = loyaltyHelper.getTransByMembershipNoAndTransType(loyaltyId, transactionType,userId);
		if(loyaltyTransactionChild != null){
			if(loyaltyTransactionChild.getEarnedAmount() != null && loyaltyTransactionChild.getEarnedPoints() != null){
				loyaltyPlaceholder = decimalFormat.format(loyaltyTransactionChild.getEarnedAmount())+" & "+loyaltyTransactionChild.getEarnedPoints().intValue()+" Points";
			}
			else if(loyaltyTransactionChild.getEarnedAmount() != null && loyaltyTransactionChild.getEarnedPoints() == null){
				loyaltyPlaceholder = decimalFormat.format(loyaltyTransactionChild.getEarnedAmount());
			}
			else if(loyaltyTransactionChild.getEarnedAmount() == null && loyaltyTransactionChild.getEarnedPoints() != null){
				loyaltyPlaceholder = loyaltyTransactionChild.getEarnedPoints().intValue()+" Points";
			}
			else{
				loyaltyPlaceholder = defVal;
			}
		}
		else{
			loyaltyPlaceholder = defVal;
		}
		logger.info("--Exit of getLastBonusValue--");
		return loyaltyPlaceholder;
	}//getLastBonusValue

	private Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag) throws Exception {
		logger.info("--Start of fetchExpiryValues--");
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		logger.info("--Exit of fetchExpiryValues--");
		return expiryDao.fetchExpiryValues(loyaltyId, expDate, rewardFlag,false);
	}//fetchExpiryValues
	private String getDefaultUserAddress(Users user) {
		String[] usraddr = {""};
		if(user != null) {
			if(user.getAddressOne()!=null && !user.getAddressOne().trim().equals("")){
				usraddr[0] = usraddr[0]  + user.getAddressOne(); 
			}
			if(user.getAddressTwo()!=null && !user.getAddressTwo().trim().equals("")){
				usraddr[0] = usraddr[0] + ", " + user.getAddressTwo(); 
			}
			if(user.getCity()!=null && !user.getCity().trim().equals("")){
				usraddr[0] = usraddr[0] + ", " + user.getCity(); 
			}
			if(user.getState()!=null && !user.getState().trim().equals("")){
				usraddr[0] = usraddr[0] + ", " + user.getState(); 
			}
			if(user.getCountry()!=null && !user.getCountry().trim().equals("")){
				usraddr[0] = usraddr[0] + ", " + user.getCountry(); 
			}
			if(user.getPinCode()!=null && !user.getPinCode().trim().equals("")){
				usraddr[0] = usraddr[0] + ", " + user.getPinCode(); 
			}
			usraddr[0] = usraddr[0];
		}
		return usraddr[0];
	}
	private String getLastPurchaseStorePlaceholders(Contacts contact, String placeholder, String defVal,Long orgId){
		try {
			
			
			RetailProSalesDao retailProSalesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);

			RetailProSalesCSV lastSaleRecord = retailProSalesDao.findLastpurchaseRecord(contact.getContactId(), contact.getUsers().getUserId());//(contact.getExternalId(),contact.getUsers().getUserId());
			if(lastSaleRecord == null) return defVal;

			OrganizationStores organizationStores = null;
			String value = Constants.STRING_NILL;
			String storeNum = lastSaleRecord.getStoreNumber();

			if(placeholder.toLowerCase().endsWith(OCConstants.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS.toLowerCase()) || placeholder.toLowerCase().endsWith(OCConstants.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE.toLowerCase())){
				if(orgId != null && storeNum != null) {

					organizationStores = organizationStoresDao.findByStoreLocationId(orgId, storeNum );
					value = getLastPurchaseStoreAddr( organizationStores, defVal);
				}
			}
			else if(placeholder.toLowerCase().endsWith(OCConstants.CAMPAIGN_PH_LASTPURCHASE_DATE.toLowerCase())) {

				//MailingList mailingList = contact.getMailingListByType(Constants.CONTACT_OPTIN_MEDIUM_POS);

				Calendar Mycalender = lastSaleRecord.getSalesDate();//retailProSalesDao.findLastpurchasedDate(contact.getExternalId(),contact.getUsers().getUserId());
				if(Mycalender != null){
					value = MyCalendar.calendarToString(Mycalender, MyCalendar.FORMAT_DATEONLY_GENERAL);
				}
				else {
					value=defVal;
				}

			}
		else if(placeholder.toLowerCase().endsWith(OCConstants.CAMPAIGN_PH_LASTPURCHASE_AMOUNT.toLowerCase())) {
		
						Double lastPurchaseDetails = lastSaleRecord.getSalesPrice();
						if(lastPurchaseDetails != null){
								value = lastPurchaseDetails.toString();
						}
						else {
							value=defVal;
						}
					
				}
			else {
				if(storeNum != null  ) {
					if( organizationStores == null) organizationStores = organizationStoresDao.findByStoreLocationId(orgId, storeNum);

					if(organizationStores == null) {
						value = defVal;
					}
					else {
						if(placeholder.toLowerCase().endsWith(OCConstants.CAMPAIGN_PH_STORENAME.toLowerCase())){ 
							value = organizationStores.getStoreName() ;
						}
						else if(placeholder.toLowerCase().endsWith(OCConstants.CAMPAIGN_PH_STOREMANAGER.toLowerCase())){
							value = organizationStores.getStoreManagerName();
						}
						else if(placeholder.toLowerCase().endsWith(OCConstants.CAMPAIGN_PH_STOREEMAIL.toLowerCase())){
							value = organizationStores.getEmailId();
						}
						else if(placeholder.toLowerCase().endsWith(OCConstants.CAMPAIGN_PH_STOREPHONE.toLowerCase())){
							value = organizationStores.getAddress().getPhone();
						}
						else if(placeholder.toLowerCase().endsWith(OCConstants.CAMPAIGN_PH_STORESTREET.toLowerCase())){
							value = organizationStores.getAddress().getAddressOne();
						}
						else if(placeholder.toLowerCase().endsWith(OCConstants.CAMPAIGN_PH_STORECITY.toLowerCase())){
							value = organizationStores.getAddress().getCity() ;
						}
						else if(placeholder.toLowerCase().endsWith(OCConstants.CAMPAIGN_PH_STORESTATE.toLowerCase())){
							value = organizationStores.getAddress().getState();
						}
						else if(placeholder.toLowerCase().endsWith(OCConstants.CAMPAIGN_PH_STOREZIP.toLowerCase())){
							value = organizationStores.getAddress().getPin() != null ? organizationStores.getAddress().getPin()+Constants.STRING_NILL:Constants.STRING_NILL;
						}
					}	
				}else{

					value = defVal;
				}
			}

			if(value == null || value.trim().isEmpty()) {

				return defVal;
			}
			return value;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception while giving the ph value ",e);
			return defVal;
		}
	}//getLastPurchaseStorePlaceholders()

	private String getLastPurchaseStoreAddr(OrganizationStores organizationStores, String defVal) {
		//need to get the contact last puchased store address
		//RetailProSalesDao retailProSalesDao = (RetailProSalesDao)context.getBean("retailProSalesDao");
		//OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)context.getBean("organizationStoresDao");

		String storeAddress = null;

		//MailingList mailingList = contact.getMailingListByType(Constants.CONTACT_OPTIN_MEDIUM_POS);

		if(organizationStores != null) {

			//String pattern = "[;=;]+";
			//logger.debug("entered into organization stores=====================");
			if(storeAddress == null) storeAddress = Constants.STRING_NILL;

			String strAddr[] = organizationStores.getAddressStr().split(Constants.ADDR_COL_DELIMETER);
			int count = 0;
			for(String str : strAddr){
				count++;

				if(count == 7 && storeAddress.length()>0 && str.trim().length()>0){
					storeAddress = storeAddress+" | Phone: "+str;
				}
				else if(storeAddress.length()==0 && str.trim().length()>0){
					storeAddress = storeAddress+str;
				}
				else if(storeAddress.length()>0 && str.trim().length()>0){
					storeAddress = storeAddress+", "+str;
				}
			}

			//storeAddress = organizationStores.getAddressStr().replace(Constants.ADDR_COL_DELIMETER, " | ");
			//storeAddress = organizationStores.getAddressStr().replaceAll(pattern, " | ");
		}
		else {
			storeAddress = defVal;
		}
		return storeAddress != null && !storeAddress.trim().isEmpty() ? storeAddress : defVal;
	}//getLastPurchaseStoreAddr();
	private String getstoreaddress(Contacts contact, String placeholder, String defVal,Long orgId){
	    String storeNum = contact.getHomeStore();
	    OrganizationStoresDao organizationStoresDao = null;
		try {
			organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(orgId != null && storeNum != null) {
	        OrganizationStores organizationStores = organizationStoresDao.findByStoreLocationId(orgId, storeNum );
	        return  getLastPurchaseStoreAddr( organizationStores, defVal);
	    }
	    return defVal;
	}
	private Set<String> getPhSet(String content) {
		//logger.debug("+++++++ Just Entered +++++"+ content);
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String> totalPhSet = new HashSet<String>();

		try {
			while(m.find()) {

				ph = m.group(1); //.toUpperCase()
				if(logger.isInfoEnabled()) logger.info("Ph holder :" + ph);

				if(ph.startsWith("GEN_")) {
					totalPhSet.add(ph);
				}
				if(ph.startsWith(Constants.UDF_TOKEN)) {
					totalPhSet.add(ph);
				}
				else if(ph.startsWith("CC_")) {
					totalPhSet.add(ph);
				}
			} // while
			
			if(logger.isDebugEnabled()) logger.debug("+++ Exiting : "+ totalPhSet);
		} catch (Exception e) {
			logger.error("Exception while getting the place holders ", e);
		}

		return totalPhSet;
	}
	
	private Set<String> getCustomFields(String content) {
		//logger.debug("+++++++ Just Entered +++++"+ content);

		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String> totalPhSet = new HashSet<String>();
	
		try {
			while(m.find()) {

				ph = m.group(1); //.toUpperCase()
				logger.info("Ph holder :" + ph);

				if(ph.startsWith("CC_")) {
					totalPhSet.add(ph);
				}
				else if(ph.startsWith("GEN_")) {
					totalPhSet.add(ph);
				}
				else if(ph.startsWith(Constants.UDF_TOKEN)) {
					totalPhSet.add(ph);
				}
				else if(ph.startsWith("CF_")) {
					totalPhSet.add(ph);
				}
				else if(ph.startsWith("MLS_")){
					totalPhSet.add(ph);

				}
				else if(ph.startsWith(Constants.DATE_PH_DATE_)){
					totalPhSet.add(ph);
				}
				
			} // while
			
			if(logger.isDebugEnabled()) logger.debug("+++ Exiting : "+ totalPhSet);
		} catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("Exception while getting the place holders ", e);
		}

		
		return totalPhSet;
	}
	public List<Coupons> couponListAddition (List<Coupons> coupons,List<OrganizationStores> orgStore){
		
		List<Coupons> couponsListAll = new ArrayList<Coupons>(); 
		List<Coupons> couponsListStoreSpecific = new ArrayList<Coupons>(); 
		List<Coupons> couponsCombined = new ArrayList<Coupons>();
		for(Coupons coupon : coupons) {
			 if(!coupon.isMappedOnZone()) {	
					if(coupon.getAllStoreChk()) {
						couponsListAll.add(coupon);
					}
					else {
						for(OrganizationStores store : orgStore) {
							String storeSplit[] = coupon.getSelectedStores().split(";=;");
							for(String stores : storeSplit ) {
							if(stores.equals(store.getHomeStoreId()+"")) {
								couponsListStoreSpecific.add(coupon);
							break;
								}
							}
						}
					}
			 }	
		}
		Set<Coupons> hSetCouponsAll = new HashSet<Coupons>(couponsListAll);
		Set<Coupons> hSetStoreSpecific = new HashSet<Coupons>(couponsListStoreSpecific);
		
		//To handle repeating coupon from all case.
		couponsCombined.addAll(hSetCouponsAll.stream().collect(Collectors.toList()));
		couponsCombined.addAll(hSetStoreSpecific);
		
		return couponsCombined;
	}
	
	
}
