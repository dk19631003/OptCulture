package org.mq.optculture.business.mobileapp;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.beans.LoyaltyMemberSessionID;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.DRSMSSentDao;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptUrlParameters;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.mobileapp.Amount;
import org.mq.optculture.model.mobileapp.Item;
import org.mq.optculture.model.mobileapp.Lookup;
import org.mq.optculture.model.mobileapp.LoyaltyMatchedCustomers;
import org.mq.optculture.model.mobileapp.LoyaltyMemberTransaction;
import org.mq.optculture.model.mobileapp.LoyaltyTransaction;
import org.mq.optculture.model.mobileapp.LoyaltyTransactionHistoryRequest;
import org.mq.optculture.model.mobileapp.LoyaltyTransactionHistoryResponse;
import org.mq.optculture.model.mobileapp.MatchedCustomers;
import org.mq.optculture.model.mobileapp.Payment;
import org.mq.optculture.model.mobileapp.LoyaltyTransactionHistoryResponse;
import org.mq.optculture.model.mobileapp.RequestReport;
import org.mq.optculture.model.mobileapp.ResponseHeader;
import org.mq.optculture.model.mobileapp.ResponseReport;
import org.mq.optculture.model.mobileapp.SaleTransaction;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class LoyaltyTransactionHistoryServiceImpl implements LoyaltyTransactionHistoryService{


	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		BaseResponseObject baseResponseObject=new BaseResponseObject();
		LoyaltyTransactionHistoryResponse LoyaltyTransactionHistoryResponse=null;
		try {
			
			logger.debug("-------entered processRequest---------");
			String serviceRequest = baseRequestObject.getAction();
			String requestJson = baseRequestObject.getJsonValue();
			String transactionID = baseRequestObject.getTransactionId();
			String transactionDate = baseRequestObject.getTransactionDate();
			String responseJson = null;
			Gson gson = new Gson();
			
			//json to object
			LoyaltyTransactionHistoryRequest LoyaltyTransactionHistoryRequest = null;
			
			if(serviceRequest == null || !serviceRequest.equals(OCConstants.LTYTRX_HISTORY_SERVICE_REQUEST)){
				logger.info("servicerequest is null...");
				
				Status status = new Status("800000", ""+PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				
				LoyaltyTransactionHistoryResponse = new LoyaltyTransactionHistoryResponse();
				LoyaltyTransactionHistoryResponse.setStatus(status);
				responseJson = gson.toJson(LoyaltyTransactionHistoryResponse);
				
				baseResponseObject.setAction(serviceRequest);
				baseResponseObject.setJsonValue(responseJson);
				logger.info("Exited baserequest due to invalid service");
				return baseResponseObject;
			}
			
			try {
				LoyaltyTransactionHistoryRequest = gson.fromJson(baseRequestObject.getJsonValue(), LoyaltyTransactionHistoryRequest.class);
			} catch (Exception e) {
				logger.error("Exception ::",e);
				Status status = new Status("800000",PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				ResponseHeader header = new ResponseHeader(Constants.STRING_NILL,
						Constants.STRING_NILL, transactionDate, transactionID);
				
				LoyaltyTransactionHistoryResponse = prepareFinalResponse(header,status);
				String json=gson.toJson(LoyaltyTransactionHistoryResponse);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.LTYTRX_HISTORY_SERVICE_REQUEST);
				return baseResponseObject;
			}
			
			try{
				LoyaltyTransactionHistoryService loyaltyTransactionHistoryService = (LoyaltyTransactionHistoryService) ServiceLocator.getInstance().getServiceByName(OCConstants.LTYTRX_HISTORY_SERVICE);
				LoyaltyTransactionHistoryResponse = loyaltyTransactionHistoryService.processLtyTrxHistoryRequest(LoyaltyTransactionHistoryRequest, 
						OCConstants.LOYALTY_ONLINE_MODE, transactionID, transactionDate);
				responseJson = gson.toJson(LoyaltyTransactionHistoryResponse);
				
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
	public LoyaltyTransactionHistoryResponse processLtyTrxHistoryRequest(LoyaltyTransactionHistoryRequest LoyaltyTransactionHistoryRequest,
			String mode, String transactionId, String transactionDate) throws BaseServiceException {
		
		LoyaltyTransactionHistoryResponse loyaltyTransactionHistoryResponse=null;
		Status status = null;
		Users user = null;
		
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(LoyaltyTransactionHistoryRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(LoyaltyTransactionHistoryRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		try {
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.
					getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			DRSentDao drSentDao = (DRSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SENT_DAO);
			DRSMSSentDao drsmsSentDao = (DRSMSSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_SENT_DAO);
			status = validateJsonData(LoyaltyTransactionHistoryRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status);
				return loyaltyTransactionHistoryResponse;
			}
			user = getUser(LoyaltyTransactionHistoryRequest.getUser().getUserName(), LoyaltyTransactionHistoryRequest.getUser().getOrganizationId(),
					LoyaltyTransactionHistoryRequest.getUser().getToken());
			if(user == null){
				status = new Status("800002", PropertyUtil.getErrorMessage(800002, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status);
				return loyaltyTransactionHistoryResponse;
			}
			
			if(!user.isEnabled()){
				status = new Status("800003", PropertyUtil.getErrorMessage(800003, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status);
				return loyaltyTransactionHistoryResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("800004", PropertyUtil.getErrorMessage(800004, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status);
				return loyaltyTransactionHistoryResponse;
			}
			
			status = validateInnerObjects(LoyaltyTransactionHistoryRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status);
				return loyaltyTransactionHistoryResponse;
			}
			
			String sourceType = LoyaltyTransactionHistoryRequest.getHeader().getSourceType();
			if(sourceType != null && !sourceType.isEmpty() && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) ){
				String sessionID = LoyaltyTransactionHistoryRequest.getUser().getSessionID();
				
				if(sessionID == null || sessionID.isEmpty()){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status);
					return loyaltyTransactionHistoryResponse;
				}
				LoyaltyMemberSessionID loyaltyMemberSessionID = LoyaltyProgramHelper.validateSessionID(sessionID);
				if(loyaltyMemberSessionID == null){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status);
					return loyaltyTransactionHistoryResponse;
				}
				
				String cardNumber = LoyaltyProgramHelper.getCardFromSesstionID(sessionID);
				if(LoyaltyTransactionHistoryRequest.getLookup().getMembershipNumber() != null && 
						LoyaltyTransactionHistoryRequest.getLookup().getMembershipNumber().trim().length() > 0 && 
						!LoyaltyTransactionHistoryRequest.getLookup().getMembershipNumber().trim().equals(cardNumber)){
					status = new Status("800029", PropertyUtil.getErrorMessage(800029, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status);
					return loyaltyTransactionHistoryResponse;
					
				}
				
			}
			Lookup lookup = LoyaltyTransactionHistoryRequest.getLookup();
			//String receiptNumber = lookup.getReceiptNumber();
			String membershipNumber = lookup.getMembershipNumber();
			String phone = lookup.getPhone();
			String emailId = lookup.getEmailAddress();
			
			RequestReport report = LoyaltyTransactionHistoryRequest.getReport();
			String loyaltyEnrolledSource  =report.getSource();
			String modeOfTrx = report.getMode();
			String transactionType = report.getTransactionType() != null && !report.getTransactionType().isEmpty() ? report.getTransactionType() : "All";
			String serviceType = report.getServiceType();
			String store = report.getStore();
			//loyaltyEnrolledSource = loyaltyEnrolledSource == null || loyaltyEnrolledSource.isEmpty() ? "All" : loyaltyEnrolledSource;//need to confirm about default val here 
			String startDate = report.getStartDate();
			String endDate = report.getEndDate();
			String offSet = report.getOffset();
			String maxRecords = report.getMaxRecords();
			String cid =  null;
			Calendar startDateCal = getDate(startDate);
			Calendar endCal = getDate(endDate);
			String transactionTypes = "";
			if(report.getTransactions() != null && !report.getTransactions().isEmpty()){
				List<String> rettransactionTypes = report.getTransactions();
				for (String type : rettransactionTypes) {
					
					if(!transactionTypes.isEmpty()) transactionTypes+= ",";
					transactionTypes+= "'"+type+"'";
					
				}
				
			}
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
				 
				Contacts contact = conMembership.getContact();
				

				List<LoyaltyMatchedCustomers> matchedCustomers = new ArrayList<LoyaltyMatchedCustomers>();
				LoyaltyMatchedCustomers customer = new LoyaltyMatchedCustomers();
				
				//List<LoyaltyMatchedCustomers> matchedCustomers = prepareMatchedCustomers(retResultSetForContacts, conMembership.getContact());
				customer =new LoyaltyMatchedCustomers();
				//External ID mostly null.
				customer.setCustomerId(contact.getExternalId() == null ||  contact.getExternalId().isEmpty() ? Constants.STRING_NILL : contact.getExternalId());
				customer.setPhone(contact.getMobilePhone() == null ||  contact.getMobilePhone().isEmpty() ? Constants.STRING_NILL : contact.getMobilePhone());
				customer.setEmailAddress(contact.getEmailId() == null ||  contact.getEmailId().isEmpty() ? Constants.STRING_NILL : contact.getEmailId());
				customer.setFirstName(contact.getFirstName() == null ||  contact.getFirstName().isEmpty() ? Constants.STRING_NILL : contact.getFirstName());
				customer.setLastName(contact.getLastName() == null ||  contact.getLastName().isEmpty() ? Constants.STRING_NILL : contact.getLastName());
				customer.setMembershipNumber(conMembership.getCardNumber());
				logger.debug("conMembership == is null ???"+conMembership ==null);
				
				List<LoyaltyTransactionChild> retList = loyaltyTransactionChildDao.findBy(contact.getContactId(),conMembership.getLoyaltyId(),
						startDate, endDate, user.getUserId(), Integer.parseInt(offSet), Integer.parseInt(maxRecords), (!transactionTypes.isEmpty()? 
								transactionTypes : !transactionType.equals("All") ? ("'"+transactionType+"'") : transactionType), 
						store, loyaltyEnrolledSource);
				logger.debug("retList == is null ???"+retList ==null);
				List<LoyaltyMemberTransaction> loyalty = new ArrayList<LoyaltyMemberTransaction>();
				if(retList != null){
					logger.debug("retList == size ???"+retList.size());
					for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
						if(!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)&&
								!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)&&
								!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)&&
								!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_BONUS)&&
								!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT)&&
								!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_RETURN) && 
								!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_EXPIRY)) continue;
								
						LoyaltyMemberTransaction ltytrx = new LoyaltyMemberTransaction();
						
						ltytrx.setDate(MyCalendar.calendarToString(loyaltyTransactionChild.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR)+OCConstants.APPEND_SERVER_TIMEZONE);
						ltytrx.setDocSID(loyaltyTransactionChild.getDocSID()!= null ? loyaltyTransactionChild.getDocSID() : Constants.STRING_NILL);
						ltytrx.setSubsidiaryNumber(loyaltyTransactionChild.getSubsidiaryNumber()!= null ? loyaltyTransactionChild.getSubsidiaryNumber() : Constants.STRING_NILL);
						if(loyaltyTransactionChild.getDocSID()!= null ){
							ltytrx.seteReceiptURL("");
							DRSent drSent = drSentDao.findBy(user.getUserId(), loyaltyTransactionChild.getDocSID());
							if(drSent != null){
								String sentId = null;
								try {
									sentId = EncryptDecryptUrlParameters.encrypt(drSent.getId()+Constants.STRING_NILL);
								} catch (Exception e) {
									logger.error("Exception ::::", e);
								}
								String eReceiptURL = PropertyUtil.getPropertyValue("DRWebLinkUrl").replace("|^sentId^|", sentId); //app-3769
								if(!loyaltyTransactionChild.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT))ltytrx.seteReceiptURL(eReceiptURL);
							}else {
								DRSMSSent DRSMSSent = drsmsSentDao.findBy(user.getUserId(), loyaltyTransactionChild.getDocSID());	//app-3785
								if(DRSMSSent != null) {
									String sentId = null;
									try {
										sentId = EncryptDecryptUrlParameters.encrypt(DRSMSSent.getId()+Constants.STRING_NILL);
									} catch (Exception e) {
										logger.error("Exception ::::", e);
									}
									String eReceiptURL = PropertyUtil.getPropertyValue("DRSMSLinkUrl").replace("|^sentId^|", sentId);	//app-3769
									if(!loyaltyTransactionChild.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT))ltytrx.seteReceiptURL(eReceiptURL);	}
							}
						}
						ltytrx.setReceiptNumber(loyaltyTransactionChild.getReceiptNumber() != null ? loyaltyTransactionChild.getReceiptNumber() : Constants.STRING_NILL);
						ltytrx.setStoreNumber(loyaltyTransactionChild.getStoreNumber() != null ? loyaltyTransactionChild.getStoreNumber() : Constants.STRING_NILL);
						Amount amount = new Amount();
						amount.setEnteredValue(loyaltyTransactionChild.getEnteredAmount()+Constants.STRING_NILL);
						amount.setReceiptAmount(loyaltyTransactionChild.getReceiptAmount() != null ? 
								loyaltyTransactionChild.getReceiptAmount()+Constants.STRING_NILL : Constants.STRING_NILL);
						String transType = loyaltyTransactionChild.getTransactionType();
						 if(transType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE)){
					            if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PURCHASE)) {
					                transType= OCConstants.LOYALTY_ISSUANCE;
					            }else if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_GIFT)){
					                transType= OCConstants.LOYALTY_GIFT_ISSUANCE;
					            }else if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD)){
					                transType= OCConstants.LOYALTY_TYPE_REWARD;
					            }
					        }
						 else if(transType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN)){//APP-2081
					        	if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL)) {
					        		transType= OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL_UI;
					        	}
					        	else if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL)) {
					        		transType= OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL_UI;
					        	}
					        	
					        }
						amount.setType(transType+ (transType.contentEquals(OCConstants.LOYALTY_TRANSACTION_ADJUSTMENT) ? "-"+loyaltyTransactionChild.getEnteredAmountType() :"")  );
						amount.setValueCode((transType.contentEquals(OCConstants.LOYALTY_TRANSACTION_ADJUSTMENT) || transType.contentEquals(OCConstants.LOYALTY_TRANS_TYPE_BONUS) ? loyaltyTransactionChild.getEarnType() :loyaltyTransactionChild.getEnteredAmountType()));
						List<Balance> balences = prepareBalancesObject(loyaltyTransactionChild, loyaltyTransactionChild.getPointsDifference(), 
								loyaltyTransactionChild.getAmountDifference(), loyaltyTransactionChild.getGiftDifference());
						ltytrx.setAmount(amount);
						ltytrx.setBalances(balences);
						loyalty.add(ltytrx);
						
					}//for
					
				}//if
						
				customer.setTransactions(loyalty);	
				matchedCustomers.add(customer);
				
				status = new Status("0", "Contact lookup was successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status, matchedCustomers, new ResponseReport());
				return loyaltyTransactionHistoryResponse;
		
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
					loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status);
					return loyaltyTransactionHistoryResponse;
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
					loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status);
					return loyaltyTransactionHistoryResponse;
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
						
					List<LoyaltyMatchedCustomers> matchedCustomers = new ArrayList<LoyaltyMatchedCustomers>();
						for (Contacts contact : contactsList) {
						
							
							LoyaltyMatchedCustomers customer = new LoyaltyMatchedCustomers();
							
							//List<LoyaltyMatchedCustomers> matchedCustomers = prepareMatchedCustomers(retResultSetForContacts, conMembership.getContact());
							//External ID mostly null.
							customer.setCustomerId(contact.getExternalId() == null ||  contact.getExternalId().isEmpty() ? Constants.STRING_NILL : contact.getExternalId());
							customer.setPhone(contact.getMobilePhone() == null ||  contact.getMobilePhone().isEmpty() ? Constants.STRING_NILL : contact.getMobilePhone());
							customer.setEmailAddress(contact.getEmailId() == null ||  contact.getEmailId().isEmpty() ? Constants.STRING_NILL : contact.getEmailId());
							customer.setFirstName(contact.getFirstName() == null ||  contact.getFirstName().isEmpty() ? Constants.STRING_NILL : contact.getFirstName());
							customer.setLastName(contact.getLastName() == null ||  contact.getLastName().isEmpty() ? Constants.STRING_NILL : contact.getLastName());
							//customer.setMembershipNumber(membershipNumber);
							
							
							//if(contact.getLoyaltyCustomer() == 1 ) {
								
								ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
							
								ContactsLoyalty conMembership = contactsLoyaltyDao.findByContactId(user.getUserId(), contact.getContactId());
								
								logger.debug("conMembership == is null ???"+conMembership ==null);
								if(conMembership != null){
									
									try {
										List<LoyaltyTransactionChild> retList = loyaltyTransactionChildDao.findBy(contact.getContactId(),conMembership.getLoyaltyId(),
												startDate, endDate, user.getUserId(), Integer.parseInt(offSet), Integer.parseInt(maxRecords), (!transactionTypes.isEmpty()? 
														transactionTypes : !transactionType.equals("All") ? ("'"+transactionType+"'") : transactionType), 
												store, loyaltyEnrolledSource);
										
										logger.debug("retList == is null ???"+retList ==null);
										List<LoyaltyMemberTransaction> loyalty = new ArrayList<LoyaltyMemberTransaction>();
										if(retList != null){
											logger.debug("retList == size ???"+retList.size());
											for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
												if(!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)&&
														!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)&&
														!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)&&
														!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_BONUS)&&
														!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT)&&
														!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_RETURN) && 
												!loyaltyTransactionChild.getTransactionType().equals(OCConstants.LOYALTY_TRANS_TYPE_EXPIRY)) continue;
														
												LoyaltyMemberTransaction ltytrx = new LoyaltyMemberTransaction();
												
												ltytrx.setDate(MyCalendar.calendarToString(loyaltyTransactionChild.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR)+OCConstants.APPEND_SERVER_TIMEZONE);
												ltytrx.setDocSID(loyaltyTransactionChild.getDocSID()!= null ? loyaltyTransactionChild.getDocSID() : Constants.STRING_NILL);
												if(loyaltyTransactionChild.getDocSID()!= null ){
													ltytrx.seteReceiptURL("");
													DRSent drSent = drSentDao.findBy(user.getUserId(), loyaltyTransactionChild.getDocSID());
													if(drSent != null){
														String sentId = null;
														try {
															sentId = EncryptDecryptUrlParameters.encrypt(drSent.getId()+Constants.STRING_NILL);
														} catch (Exception e) {
															logger.error("Exception ::::", e);
														}
														String eReceiptURL = PropertyUtil.getPropertyValue("DRWebLinkUrl").replace("|^sentId^|", sentId);	//app-3769
														if(!loyaltyTransactionChild.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT))ltytrx.seteReceiptURL(eReceiptURL);
													}else {
														DRSMSSent DRSMSSent = drsmsSentDao.findBy(user.getUserId(), loyaltyTransactionChild.getDocSID());	//app-3785
														if(DRSMSSent != null) {
															String sentId = null;
															try {
																sentId = EncryptDecryptUrlParameters.encrypt(DRSMSSent.getId()+Constants.STRING_NILL);
															} catch (Exception e) {
																logger.error("Exception ::::", e);
															}
															String eReceiptURL = PropertyUtil.getPropertyValue("DRSMSLinkUrl").replace("|^sentId^|", sentId);
															if(!loyaltyTransactionChild.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT))ltytrx.seteReceiptURL(eReceiptURL);	}
													}
												}
												ltytrx.setSubsidiaryNumber(loyaltyTransactionChild.getSubsidiaryNumber()!= null ? loyaltyTransactionChild.getSubsidiaryNumber() : Constants.STRING_NILL);
												ltytrx.setReceiptNumber(loyaltyTransactionChild.getReceiptNumber() != null ? loyaltyTransactionChild.getReceiptNumber() : Constants.STRING_NILL);
												ltytrx.setStoreNumber(loyaltyTransactionChild.getStoreNumber() != null ? loyaltyTransactionChild.getStoreNumber() : Constants.STRING_NILL);
												Amount amount = new Amount();
												amount.setEnteredValue(loyaltyTransactionChild.getEnteredAmount()+Constants.STRING_NILL);
												amount.setReceiptAmount(loyaltyTransactionChild.getReceiptAmount() != null ? loyaltyTransactionChild.getReceiptAmount()+Constants.STRING_NILL : Constants.STRING_NILL);
												
												String transType = loyaltyTransactionChild.getTransactionType();
												 if(transType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_ISSUANCE)){
											            if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PURCHASE)) {
											                transType= OCConstants.LOYALTY_ISSUANCE;
											            }else if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_GIFT)){
											                transType= OCConstants.LOYALTY_GIFT_ISSUANCE;
											            }else if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD)){
											                transType= OCConstants.LOYALTY_TYPE_REWARD;
											            }
											        }
												 else if(transType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN)){//APP-2081
											        	if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL)) {
											        		transType= OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL_UI;
											        	}
											        	else if(loyaltyTransactionChild.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL)) {
											        		transType= OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL_UI;
											        	}
											        	
											        }
													/*
													 * amount.setType(transType);
													 * amount.setValueCode(loyaltyTransactionChild.getEnteredAmountType(
													 * ));
													 */
												 amount.setType(transType+ (transType.contentEquals(OCConstants.LOYALTY_TRANSACTION_ADJUSTMENT) ? "-"+loyaltyTransactionChild.getEnteredAmountType() :"")  );
													//amount.setValueCode((transType.contentEquals(OCConstants.LOYALTY_TRANSACTION_ADJUSTMENT) ? loyaltyTransactionChild.getEarnType() :loyaltyTransactionChild.getEnteredAmountType()));
												 amount.setValueCode((transType.contentEquals(OCConstants.LOYALTY_TRANSACTION_ADJUSTMENT) || transType.contentEquals(OCConstants.LOYALTY_TRANS_TYPE_BONUS) ? loyaltyTransactionChild.getEarnType() :loyaltyTransactionChild.getEnteredAmountType()));
												List<Balance> balences = prepareBalancesObject(loyaltyTransactionChild, loyaltyTransactionChild.getPointsDifference(), 
														loyaltyTransactionChild.getAmountDifference(), loyaltyTransactionChild.getGiftDifference());
												ltytrx.setAmount(amount);
												ltytrx.setBalances(balences);
												loyalty.add(ltytrx);
												
											}//for
											
											customer.setTransactions(loyalty);	
										}//if
									} catch (Exception e) {
										// TODO Auto-generated catch block
										logger.error("Exception ", e);
									}
									customer.setMembershipNumber(conMembership.getCardNumber());
								}
							//}
									
							matchedCustomers.add(customer);
						}//for
					
						
						status = new Status("0", "Contact lookup was successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status, matchedCustomers, new ResponseReport());
						return loyaltyTransactionHistoryResponse;
							
			}
			else{
				
				status = new Status("800005", PropertyUtil.getErrorMessage(800005, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status);
				return loyaltyTransactionHistoryResponse;
			}
			
		} catch (Exception e) {
			logger.error("Exception ", e);
			status = new Status("800001", PropertyUtil.getErrorMessage(800001, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			loyaltyTransactionHistoryResponse = prepareFinalResponse(responseHeader, status);
			return loyaltyTransactionHistoryResponse;
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
	
	private LoyaltyTransactionHistoryResponse prepareFinalResponse(
			ResponseHeader header, Status status)throws BaseServiceException {
		LoyaltyTransactionHistoryResponse LoyaltyTransactionHistoryResponse = new LoyaltyTransactionHistoryResponse();
		
		LoyaltyTransactionHistoryResponse.setHeader(header);
		List<LoyaltyMatchedCustomers> matchedCustomers = new ArrayList<LoyaltyMatchedCustomers>();
		LoyaltyMatchedCustomers matchedCustomer = new LoyaltyMatchedCustomers();
		matchedCustomers.add(matchedCustomer);
		LoyaltyTransactionHistoryResponse.setMatchedCustomers(matchedCustomers);
		//LoyaltyTransactionHistoryResponse.setReport(new ResponseReport());
		LoyaltyTransactionHistoryResponse.setStatus(status);
	
		return LoyaltyTransactionHistoryResponse;
	}//prepareFinalResponse
	
	private LoyaltyTransactionHistoryResponse prepareFinalResponse(
			ResponseHeader header, Status status, List<LoyaltyMatchedCustomers> matchedCustomers, 
			ResponseReport report)throws BaseServiceException {
		LoyaltyTransactionHistoryResponse loyaltyTransactionHistoryResponse = new LoyaltyTransactionHistoryResponse();
		
		loyaltyTransactionHistoryResponse.setHeader(header);
		loyaltyTransactionHistoryResponse.setMatchedCustomers(matchedCustomers);
		//loyaltyTransactionHistoryResponse.setReport(report);
		loyaltyTransactionHistoryResponse.setStatus(status);
	
		return loyaltyTransactionHistoryResponse;
	}//prepareFinalResponse
	private Status validateInnerObjects(LoyaltyTransactionHistoryRequest LoyaltyTransactionHistoryRequest) throws Exception{
		
		Status status = null;
		Lookup lookup = LoyaltyTransactionHistoryRequest.getLookup();
		RequestReport report = LoyaltyTransactionHistoryRequest.getReport();
		if((lookup.getEmailAddress() == null || lookup.getEmailAddress().isEmpty()) && 
				(lookup.getPhone() == null || lookup.getPhone().isEmpty()) && 
				(lookup.getMembershipNumber() == null || lookup.getMembershipNumber().isEmpty()) && 
				(lookup.getReceiptNumber() == null || lookup.getReceiptNumber().isEmpty())) {
			status = new Status(
					"800005", PropertyUtil.getErrorMessage(800005, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
			
		}
		if((report.getOffset() == null || report.getOffset().isEmpty()) || 
				(report.getMaxRecords() == null && report.getMaxRecords().isEmpty())){
			
			status = new Status(
					"800005", PropertyUtil.getErrorMessage(800005, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
			
		}
		
		try{
			Long offsetLong = Long.parseLong(report.getOffset());
			Long maxrecords = Long.parseLong(report.getMaxRecords());
		}catch(NumberFormatException ne){
			
			status = new Status(
					"800006", PropertyUtil.getErrorMessage(800006, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		
		String startFrom = report.getStartDate();
		String endDate = report.getEndDate();
		
		if((startFrom != null && !startFrom.isEmpty() && !Utility.validateDate(startFrom, MyCalendar.FORMAT_DATETIME_STYEAR)) || 
				(endDate != null && !endDate.isEmpty() && !Utility.validateDate(endDate, MyCalendar.FORMAT_DATETIME_STYEAR))){
			String msg = PropertyUtil.getErrorMessage(800007, OCConstants.ERROR_MOBILEAPP_FLAG)+" "+MyCalendar.FORMAT_DATETIME_MIN_STYEAR+".";
			status = new Status("800007", msg, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		
		return status;
		
		
	}
	private Status validateJsonData(LoyaltyTransactionHistoryRequest LoyaltyTransactionHistoryRequest) throws Exception{
		logger.info("Entered validateJsonData method >>>>");
		
		Status status = null;
		if(LoyaltyTransactionHistoryRequest == null ){
			status = new Status(
					"800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(LoyaltyTransactionHistoryRequest.getUser() == null){
			status = new Status(
					"800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(LoyaltyTransactionHistoryRequest.getLookup() == null){
			status = new Status(
					"800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(LoyaltyTransactionHistoryRequest.getUser().getUserName() == null || 
				LoyaltyTransactionHistoryRequest.getUser().getUserName().trim().length() <=0 || 
				LoyaltyTransactionHistoryRequest.getUser().getOrganizationId() == null || 
				LoyaltyTransactionHistoryRequest.getUser().getOrganizationId().trim().length() <=0 
						/*LoyaltyTransactionHistoryRequest.getUser().getToken() == null || 
						LoyaltyTransactionHistoryRequest.getUser().getToken().trim().length() <=0*/) {
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
	private List<Balance> prepareBalancesObject(LoyaltyTransactionChild loyaltyTransactionChild, String pointsDiff, String amountDiff, String giftDiff) throws Exception{
		List<Balance> balancesList = null;
		Balance pointBalances = null;
		Balance amountBalances = null;
		Balance giftBalances = null;
		balancesList = new ArrayList<Balance>();
		
		pointBalances = new Balance();
		pointBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		pointBalances.setValueCode(OCConstants.LOYALTY_TYPE_POINTS);
		pointBalances.setAmount(loyaltyTransactionChild.getPointsBalance() == null ? Constants.STRING_NILL : Constants.STRING_NILL+loyaltyTransactionChild.getPointsBalance().intValue());
		pointBalances.setDifference(pointsDiff);
			
		
		amountBalances = new Balance();
		amountBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		amountBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		if(loyaltyTransactionChild.getAmountBalance() == null){
			amountBalances.setAmount(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(loyaltyTransactionChild.getAmountBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			amountBalances.setAmount(Constants.STRING_NILL+value);
		}
		if(amountDiff == null || amountDiff.isEmpty() ){
			amountBalances.setDifference(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(Double.valueOf(amountDiff)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			amountBalances.setDifference(Constants.STRING_NILL+value);
		}
		
		giftBalances = new Balance();
		giftBalances.setType(OCConstants.LOYALTY_TYPE_GIFT);
		giftBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		if(loyaltyTransactionChild.getGiftBalance() == null){
			giftBalances.setAmount(Constants.STRING_NILL);
		}
		else{
			double value = new BigDecimal(loyaltyTransactionChild.getGiftBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			giftBalances.setAmount(Constants.STRING_NILL+value);
		}
		if(giftDiff == null || giftDiff.isEmpty()){
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
	
	
}
