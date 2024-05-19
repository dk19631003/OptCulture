package org.mq.optculture.business.mobileapp;

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
import org.mq.marketer.campaign.beans.LoyaltyMemberSessionID;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyEnrollmentOCService;
import org.mq.optculture.business.updatesku.UpdateSkuBusinessService;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.HeaderInfo;
import org.mq.optculture.model.StatusInfo;
import org.mq.optculture.model.UserDetails;
import org.mq.optculture.model.ereceipt.Report;
import org.mq.optculture.model.importcontact.Customer;
import org.mq.optculture.model.importcontact.Email;
import org.mq.optculture.model.importcontact.Phone;
import org.mq.optculture.model.importcontact.Suppress;
import org.mq.optculture.model.mobileapp.Item;
import org.mq.optculture.model.mobileapp.Lookup;
import org.mq.optculture.model.mobileapp.LoyaltyTransaction;
import org.mq.optculture.model.mobileapp.MatchedCustomers;
import org.mq.optculture.model.mobileapp.Payment;
import org.mq.optculture.model.mobileapp.PurchaseHistoryRequest;
import org.mq.optculture.model.mobileapp.PurchaseHistoryResponse;
import org.mq.optculture.model.mobileapp.RequestReport;
import org.mq.optculture.model.mobileapp.ResponseHeader;
import org.mq.optculture.model.mobileapp.ResponseReport;
import org.mq.optculture.model.mobileapp.SaleTransaction;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.model.updatesku.SkuInfo;
import org.mq.optculture.model.updatesku.UpdateSkuRequestObject;
import org.mq.optculture.model.updatesku.UpdateSkuResponse;
import org.mq.optculture.model.updatesku.UpdateSkuResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class PurchaseHistoryServiceImpl implements PurchaseHistoryService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		BaseResponseObject baseResponseObject=new BaseResponseObject();
		PurchaseHistoryResponse purchaseHistoryResponse=null;
		try {
			
			logger.debug("-------entered processRequest---------");
			String serviceRequest = baseRequestObject.getAction();
			String requestJson = baseRequestObject.getJsonValue();
			String transactionID = baseRequestObject.getTransactionId();
			String transactionDate = baseRequestObject.getTransactionDate();
			String responseJson = null;
			Gson gson = new Gson();
			
			//json to object
			PurchaseHistoryRequest purchaseHistoryRequest = null;
			
			if(serviceRequest == null || !serviceRequest.equals(OCConstants.PURCHASE_HISTORY_SERVICE_REQUEST)){
				logger.info("servicerequest is null...");
				
				Status status = new Status("800000", ""+PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				
				purchaseHistoryResponse = new PurchaseHistoryResponse();
				purchaseHistoryResponse.setStatus(status);
				responseJson = gson.toJson(purchaseHistoryResponse);
				
				baseResponseObject.setAction(serviceRequest);
				baseResponseObject.setJsonValue(responseJson);
				logger.info("Exited baserequest due to invalid service");
				return baseResponseObject;
			}
			
			try {
				purchaseHistoryRequest = gson.fromJson(baseRequestObject.getJsonValue(), PurchaseHistoryRequest.class);
			} catch (Exception e) {
				logger.error("Exception ::",e);
				Status status = new Status("800000",PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				ResponseHeader header = new ResponseHeader(Constants.STRING_NILL,
						Constants.STRING_NILL, transactionDate, transactionID);
				
				purchaseHistoryResponse = prepareFinalResponse(header,status);
				String json=gson.toJson(purchaseHistoryResponse);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.PURCHASE_HISTORY_SERVICE_REQUEST);
				return baseResponseObject;
			}
			
			try{
				PurchaseHistoryService purchaseHistoryService = (PurchaseHistoryService) ServiceLocator.getInstance().getServiceByName(OCConstants.PURCHASE_HISTORY_SERVICE);
				purchaseHistoryResponse = purchaseHistoryService.processPurchaseHistoryRequest(purchaseHistoryRequest, 
						OCConstants.LOYALTY_ONLINE_MODE, transactionID, transactionDate);
				responseJson = gson.toJson(purchaseHistoryResponse);
				
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
	public PurchaseHistoryResponse processPurchaseHistoryRequest(PurchaseHistoryRequest purchaseHistoryRequest,
			String mode, String transactionId, String transactionDate) throws BaseServiceException {
		
		PurchaseHistoryResponse purchaseHistoryResponse=null;
		Status status = null;
		Users user = null;
		
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(purchaseHistoryRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(purchaseHistoryRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		try {
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.
					getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			
			status = validateJsonData(purchaseHistoryRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				purchaseHistoryResponse = prepareFinalResponse(responseHeader, status);
				return purchaseHistoryResponse;
			}
			user = getUser(purchaseHistoryRequest.getUser().getUserName(), purchaseHistoryRequest.getUser().getOrganizationId(),
					purchaseHistoryRequest.getUser().getToken());
			if(user == null){
				status = new Status("800002", PropertyUtil.getErrorMessage(800002, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				purchaseHistoryResponse = prepareFinalResponse(responseHeader, status);
				return purchaseHistoryResponse;
			}
			
			if(!user.isEnabled()){
				status = new Status("800003", PropertyUtil.getErrorMessage(800003, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				purchaseHistoryResponse = prepareFinalResponse(responseHeader, status);
				return purchaseHistoryResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("800004", PropertyUtil.getErrorMessage(800004, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				purchaseHistoryResponse = prepareFinalResponse(responseHeader, status);
				return purchaseHistoryResponse;
			}
			
			status = validateInnerObjects(purchaseHistoryRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				purchaseHistoryResponse = prepareFinalResponse(responseHeader, status);
				return purchaseHistoryResponse;
			}
			
			String sourceType = purchaseHistoryRequest.getHeader().getSourceType();
			if(sourceType != null && !sourceType.isEmpty() && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) ){
				String sessionID = purchaseHistoryRequest.getUser().getSessionID();
				
				if(sessionID == null || sessionID.isEmpty()){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					purchaseHistoryResponse = prepareFinalResponse(responseHeader, status);
					return purchaseHistoryResponse;
				}
				LoyaltyMemberSessionID loyaltyMemberSessionID = LoyaltyProgramHelper.validateSessionID(sessionID);
				if(loyaltyMemberSessionID == null){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					purchaseHistoryResponse = prepareFinalResponse(responseHeader, status);
					return purchaseHistoryResponse;
				}
				
				String cardNumber = LoyaltyProgramHelper.getCardFromSesstionID(sessionID);
				if(purchaseHistoryRequest.getLookup().getMembershipNumber() != null && 
						purchaseHistoryRequest.getLookup().getMembershipNumber().trim().length() > 0 && 
						!purchaseHistoryRequest.getLookup().getMembershipNumber().trim().equals(cardNumber)){
					status = new Status("800029", PropertyUtil.getErrorMessage(800029, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					purchaseHistoryResponse = prepareFinalResponse(responseHeader, status);
					return purchaseHistoryResponse;
					
				}
				
			}
			
			Lookup lookup = purchaseHistoryRequest.getLookup();
			String subsidiaryNumber = lookup.getSubsidiaryNumber();
			String storeNumber = lookup.getStoreNumber();
			String docSID = lookup.getDocSID();
			String receiptNumber = lookup.getReceiptNumber();
			String membershipNumber = lookup.getMembershipNumber();
			String phone = lookup.getPhone();
			String emailId = lookup.getEmailAddress();
			
			RequestReport report = purchaseHistoryRequest.getReport();
			String startDate = report.getStartDate();
			String endDate = report.getEndDate();
			String offSet = report.getOffset();
			int offSetInt = offSet != null && !offSet.isEmpty() ? (Integer.parseInt(offSet)+1) : 1;
			int offSetIncrements = 0;
			String maxRecords = report.getMaxRecords();
			int maxRecordsInt = maxRecords != null && !maxRecords.isEmpty() ? Integer.parseInt(maxRecords) : 0;
			String cid =  null;
			Calendar startDateCal = getDate(startDate);
			Calendar endCal = getDate(endDate);
			int iterCount = 0;
			
			if(docSID != null && !docSID.trim().isEmpty()){
				
				RetailProSalesDao retailProSalesDao = (RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
				ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
				
				List<SaleTransaction> transactions = new ArrayList<SaleTransaction>();
				List<Item> items = new ArrayList<Item>();
				String recptNumber = null;
				double Recptamount = 0.0;
				String currentDocSid = null;
				Double quantity = 0.0;
				Double salesPrise = 0.0;
				Double tax = 0.0;  
				Double discount = 0.0;
				Double currentrecordAmount = 0.0;
				Item item = null;
				Contacts contact = null;
				ResultSet retResultSetForSales = retailProSalesDao.executeQueryForPurchaseHistory( null, docSID,
						subsidiaryNumber,storeNumber,startDate, endDate, offSet, maxRecords, user.getUserId());
				int size= 0;
				if (retResultSetForSales != null)   
				{  
					retResultSetForSales.beforeFirst();  
					retResultSetForSales.last();  
					size = retResultSetForSales.getRow();  
				}  
					
				boolean isLastRecord = false;
				retResultSetForSales.beforeFirst();
				
				
			List<MatchedCustomers> matchedCustomers = new ArrayList<MatchedCustomers>();
			MatchedCustomers customer = new MatchedCustomers();
			if(size > 0) {
				
				
				//List<MatchedCustomers> matchedCustomers = prepareMatchedCustomers(retResultSetForContacts, conMembership.getContact());
				
				while(retResultSetForSales.next()){
					logger.debug("started at  row ==="+retResultSetForSales.getRow());
					isLastRecord = false;
					currentDocSid = retResultSetForSales.getString("sal.doc_sid");
					//if(iterCount > maxRecords)
					if(recptNumber ==  null ){
						recptNumber = currentDocSid;
						items = new ArrayList<Item>();
					}
					if(!recptNumber.equals(currentDocSid)) {
						//
						iterCount ++;
						isLastRecord = true;
						//Recptamount += currentrecordAmount;
						SaleTransaction trx = new SaleTransaction();
						trx.setDate(retResultSetForSales.getString("sal.sales_date") != null ? retResultSetForSales.getString("sal.sales_date")+OCConstants.APPEND_SERVER_TIMEZONE : "");
						trx.setReceiptNumber(retResultSetForSales.getString("sal.reciept_number"));
						trx.setReceiptAmount(Recptamount+Constants.STRING_NILL);
						trx.setItems(items);
						
						Payment payment = new Payment();
						payment.setAmount(Constants.STRING_NILL);
						payment.setMode(retResultSetForSales.getString("sal.tender_type"));
						
						trx.setPayment(payment);
						String contactID = retResultSetForSales.getString("sal.cid");
						if(contactID != null) {
							
							contact = contactsDao.findById(Long.parseLong(contactID));
							
							if(contact != null) {
								
								customer =new MatchedCustomers();
								//External ID mostly null.
								customer.setCustomerId(contact.getExternalId() == null ||  contact.getExternalId().isEmpty() ? Constants.STRING_NILL : contact.getExternalId());
								customer.setPhone(contact.getMobilePhone() == null ||  contact.getMobilePhone().isEmpty() ? Constants.STRING_NILL : contact.getMobilePhone());
								customer.setEmailAddress(contact.getEmailId() == null ||  contact.getEmailId().isEmpty() ? Constants.STRING_NILL : contact.getEmailId());
								customer.setFirstName(contact.getFirstName() == null ||  contact.getFirstName().isEmpty() ? Constants.STRING_NILL : contact.getFirstName());
								customer.setLastName(contact.getLastName() == null ||  contact.getLastName().isEmpty() ? Constants.STRING_NILL : contact.getLastName());
								customer.setMembershipNumber(membershipNumber);
								
								ContactsLoyalty conMembership = contactsLoyaltyDao.findByContactId(user.getUserId(), contact.getContactId());
								if(conMembership != null){
									
									List<LoyaltyTransactionChild> retList = loyaltyTransactionChildDao.findBy(contact.getContactId(),conMembership.getLoyaltyId(),
											startDate, endDate,currentDocSid, user.getUserId());
									List<LoyaltyTransaction> loyalty = new ArrayList<LoyaltyTransaction>();
									if(retList != null){
										for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
											
											LoyaltyTransaction ltytrx = new LoyaltyTransaction();
											ltytrx.setAmount(loyaltyTransactionChild.getEnteredAmount()+Constants.STRING_NILL);
											ltytrx.setType(loyaltyTransactionChild.getTransactionType()+Constants.STRING_NILL);
											ltytrx.setValueCode(loyaltyTransactionChild.getEarnType()+Constants.STRING_NILL);
											loyalty.add(ltytrx);
											
										}//for
										
									}//if
									
									trx.setLoyalty(loyalty);
									customer.setMembershipNumber(conMembership.getCardNumber());
								}//if
								
							}//if
							
						}//if
						
						transactions.add(trx);
						
						customer.setTransactions(transactions);
						Recptamount = 0.0;
						recptNumber = currentDocSid;
						items = new ArrayList<Item>();
					}
					
					quantity = retResultSetForSales.getDouble("sal.quantity");
					salesPrise = retResultSetForSales.getDouble("sal.sales_price");
					tax = retResultSetForSales.getDouble("sal.tax");
					discount = retResultSetForSales.getDouble("sal.discount");
					currentrecordAmount = (quantity*salesPrise)+tax-discount;
					Recptamount += currentrecordAmount;
					
					item = new Item();
					item.setItemCategory(retResultSetForSales.getString("sku.item_category"));
					item.setDepartmentCode(retResultSetForSales.getString("sku.department_code"));
					item.setItemClass(retResultSetForSales.getString("sku.class_code"));
					item.setItemSubClass(retResultSetForSales.getString("sku.subclass_code"));
					item.setDCS(retResultSetForSales.getString("sku.dcs"));
					item.setVendorCode(retResultSetForSales.getString("sku.vendor_code"));
					item.setSkuNumber(retResultSetForSales.getString("sku.sku"));
					item.setBilledUnitPrice(retResultSetForSales.getString("sku.list_price"));
					item.setQuantity(retResultSetForSales.getString("sal.quantity"));
					items.add(item);
					
					recptNumber = currentDocSid;
				}
			}
				logger.debug("getfetchsize ==="+retResultSetForSales.getFetchSize());
				//for last record
				//Recptamount += currentrecordAmount;
			if(size > 0 && isLastRecord == false) {
				retResultSetForSales.last();
				logger.debug("retResultSetForSales.last() "+retResultSetForSales.getRow());
				SaleTransaction trx = new SaleTransaction();
				trx.setDate(retResultSetForSales.getString("sal.sales_date") != null ? retResultSetForSales.getString("sal.sales_date")+OCConstants.APPEND_SERVER_TIMEZONE : Constants.STRING_NILL );
				trx.setReceiptNumber(retResultSetForSales.getString("sal.reciept_number"));
				trx.setReceiptAmount(Recptamount+Constants.STRING_NILL);
				trx.setItems(items);
				
				Payment payment = new Payment();
				payment.setAmount(Constants.STRING_NILL);
				payment.setMode(retResultSetForSales.getString("sal.tender_type"));
				
				trx.setPayment(payment);
				
				
					
					
				String contactID = retResultSetForSales.getString("sal.cid");
				if(contactID != null) {
					
					contact = contactsDao.findById(Long.parseLong(contactID));
					
					if(contact != null) {
						
						customer =new MatchedCustomers();
						//External ID mostly null.
						customer.setCustomerId(contact.getExternalId() == null ||  contact.getExternalId().isEmpty() ? Constants.STRING_NILL : contact.getExternalId());
						customer.setPhone(contact.getMobilePhone() == null ||  contact.getMobilePhone().isEmpty() ? Constants.STRING_NILL : contact.getMobilePhone());
						customer.setEmailAddress(contact.getEmailId() == null ||  contact.getEmailId().isEmpty() ? Constants.STRING_NILL : contact.getEmailId());
						customer.setFirstName(contact.getFirstName() == null ||  contact.getFirstName().isEmpty() ? Constants.STRING_NILL : contact.getFirstName());
						customer.setLastName(contact.getLastName() == null ||  contact.getLastName().isEmpty() ? Constants.STRING_NILL : contact.getLastName());
						customer.setMembershipNumber(membershipNumber);
						
						ContactsLoyalty conMembership = contactsLoyaltyDao.findByContactId(user.getUserId(), contact.getContactId());
						if(conMembership != null){
							
							List<LoyaltyTransactionChild> retList = loyaltyTransactionChildDao.findBy(contact.getContactId(),conMembership.getLoyaltyId(),
									startDate, endDate,currentDocSid, user.getUserId());
							List<LoyaltyTransaction> loyalty = new ArrayList<LoyaltyTransaction>();
							if(retList != null){
								for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
									
									LoyaltyTransaction ltytrx = new LoyaltyTransaction();
									ltytrx.setAmount(loyaltyTransactionChild.getEnteredAmount()+Constants.STRING_NILL);
									ltytrx.setType(loyaltyTransactionChild.getTransactionType()+Constants.STRING_NILL);
									ltytrx.setValueCode(loyaltyTransactionChild.getEarnType()+Constants.STRING_NILL);
									loyalty.add(ltytrx);
									
								}//for
								
							}//if
							
							trx.setLoyalty(loyalty);
							customer.setMembershipNumber(conMembership.getCardNumber());
						}//if
						
					}//if
					
				}//if
				
				transactions.add(trx);
				
				//transactions.add(trx);
				
			}
			customer.setTransactions(transactions);
			Recptamount = 0.0;
			recptNumber = currentDocSid;
			matchedCustomers.add(customer);
				
				
				status = new Status("0", "Contact lookup was successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				purchaseHistoryResponse = prepareFinalResponse(responseHeader, status, matchedCustomers, new ResponseReport());
				return purchaseHistoryResponse;
				
				
			}
				
			else if(receiptNumber != null && !receiptNumber.isEmpty()) {
				
				RetailProSalesDao retailProSalesDao = (RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
				ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
				
				List<SaleTransaction> transactions = new ArrayList<SaleTransaction>();
				List<Item> items = new ArrayList<Item>();
				String recptNumber = null;
				double Recptamount = 0.0;
				String currentDocSid = null;
				Double quantity = 0.0;
				Double salesPrise = 0.0;
				Double tax = 0.0;  
				Double discount = 0.0;
				Double currentrecordAmount = 0.0;
				Item item = null;
				Contacts contact = null;
				ResultSet retResultSetForSales = retailProSalesDao.executeQueryForPurchaseHistory( receiptNumber, null,
						subsidiaryNumber, storeNumber, startDate, endDate, offSet, maxRecords, user.getUserId());
				int size= 0;
				if (retResultSetForSales != null)   
				{  
					retResultSetForSales.beforeFirst();  
					retResultSetForSales.last();  
					size = retResultSetForSales.getRow();  
				}  
					
				boolean isLastRecord = false;
				retResultSetForSales.beforeFirst();
				
				
			List<MatchedCustomers> matchedCustomers = new ArrayList<MatchedCustomers>();
			MatchedCustomers customer = new MatchedCustomers();
			if(size > 0) {
				
				
				//List<MatchedCustomers> matchedCustomers = prepareMatchedCustomers(retResultSetForContacts, conMembership.getContact());
				
				while(retResultSetForSales.next()){
					logger.debug("started at  row ==="+retResultSetForSales.getRow());
					isLastRecord = false;
					currentDocSid = retResultSetForSales.getString("sal.doc_sid");
					if(recptNumber ==  null ){
						recptNumber = currentDocSid;
						items = new ArrayList<Item>();
					}
					if( !recptNumber.equals(currentDocSid)) {
						//
						isLastRecord = true;
						//Recptamount += currentrecordAmount;
						SaleTransaction trx = new SaleTransaction();
						trx.setDate(retResultSetForSales.getString("sal.sales_date") != null ? retResultSetForSales.getString("sal.sales_date")+OCConstants.APPEND_SERVER_TIMEZONE : "");
						trx.setReceiptNumber(retResultSetForSales.getString("sal.reciept_number"));
						trx.setReceiptAmount(Recptamount+Constants.STRING_NILL);
						trx.setItems(items);
						
						Payment payment = new Payment();
						payment.setAmount(Constants.STRING_NILL);
						payment.setMode(retResultSetForSales.getString("sal.tender_type"));
						
						trx.setPayment(payment);
						String contactID = retResultSetForSales.getString("sal.cid");
						if(contactID != null) {
							
							contact = contactsDao.findById(Long.parseLong(contactID));
							
							if(contact != null) {
								
								customer =new MatchedCustomers();
								//External ID mostly null.
								customer.setCustomerId(contact.getExternalId() == null ||  contact.getExternalId().isEmpty() ? Constants.STRING_NILL : contact.getExternalId());
								customer.setPhone(contact.getMobilePhone() == null ||  contact.getMobilePhone().isEmpty() ? Constants.STRING_NILL : contact.getMobilePhone());
								customer.setEmailAddress(contact.getEmailId() == null ||  contact.getEmailId().isEmpty() ? Constants.STRING_NILL : contact.getEmailId());
								customer.setFirstName(contact.getFirstName() == null ||  contact.getFirstName().isEmpty() ? Constants.STRING_NILL : contact.getFirstName());
								customer.setLastName(contact.getLastName() == null ||  contact.getLastName().isEmpty() ? Constants.STRING_NILL : contact.getLastName());
								customer.setMembershipNumber(membershipNumber);
								
								ContactsLoyalty conMembership = contactsLoyaltyDao.findByContactId(user.getUserId(), contact.getContactId());
								if(conMembership != null){
									
									List<LoyaltyTransactionChild> retList = loyaltyTransactionChildDao.findBy(contact.getContactId(),conMembership.getLoyaltyId(),
											startDate, endDate,currentDocSid, user.getUserId());
									List<LoyaltyTransaction> loyalty = new ArrayList<LoyaltyTransaction>();
									if(retList != null){
										for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
											
											LoyaltyTransaction ltytrx = new LoyaltyTransaction();
											ltytrx.setAmount(loyaltyTransactionChild.getEnteredAmount()+Constants.STRING_NILL);
											ltytrx.setType(loyaltyTransactionChild.getTransactionType()+Constants.STRING_NILL);
											ltytrx.setValueCode(loyaltyTransactionChild.getEarnType()+Constants.STRING_NILL);
											loyalty.add(ltytrx);
											
										}//for
										
									}//if
									
									trx.setLoyalty(loyalty);
									customer.setMembershipNumber(conMembership.getCardNumber());
								}//if
								
							}//if
							
						}//if
						
						transactions.add(trx);
						
						customer.setTransactions(transactions);
						Recptamount = 0.0;
						recptNumber = currentDocSid;
						items = new ArrayList<Item>();
					}
					
					quantity = retResultSetForSales.getDouble("sal.quantity");
					salesPrise = retResultSetForSales.getDouble("sal.sales_price");
					tax = retResultSetForSales.getDouble("sal.tax");
					discount = retResultSetForSales.getDouble("sal.discount");
					currentrecordAmount = (quantity*salesPrise)+tax-discount;
					Recptamount += currentrecordAmount;
					
					item = new Item();
					item.setItemCategory(retResultSetForSales.getString("sku.item_category"));
					item.setDepartmentCode(retResultSetForSales.getString("sku.department_code"));
					item.setItemClass(retResultSetForSales.getString("sku.class_code"));
					item.setItemSubClass(retResultSetForSales.getString("sku.subclass_code"));
					item.setDCS(retResultSetForSales.getString("sku.dcs"));
					item.setVendorCode(retResultSetForSales.getString("sku.vendor_code"));
					item.setSkuNumber(retResultSetForSales.getString("sku.sku"));
					item.setBilledUnitPrice(retResultSetForSales.getString("sku.list_price"));
					item.setQuantity(retResultSetForSales.getString("sal.quantity"));
					items.add(item);
					
					recptNumber = currentDocSid;
				}
			}
				logger.debug("getfetchsize ==="+retResultSetForSales.getFetchSize());
				//for last record
				//Recptamount += currentrecordAmount;
			if(size > 0 && isLastRecord == false) {
				retResultSetForSales.last();
				logger.debug("retResultSetForSales.last() "+retResultSetForSales.getRow());
				SaleTransaction trx = new SaleTransaction();
				trx.setDate(retResultSetForSales.getString("sal.sales_date") != null ? retResultSetForSales.getString("sal.sales_date")+OCConstants.APPEND_SERVER_TIMEZONE : Constants.STRING_NILL );
				trx.setReceiptNumber(retResultSetForSales.getString("sal.reciept_number"));
				trx.setReceiptAmount(Recptamount+Constants.STRING_NILL);
				trx.setItems(items);
				
				Payment payment = new Payment();
				payment.setAmount(Constants.STRING_NILL);
				payment.setMode(retResultSetForSales.getString("sal.tender_type"));
				
				trx.setPayment(payment);
				
				
					
					
				String contactID = retResultSetForSales.getString("sal.cid");
				if(contactID != null) {
					
					contact = contactsDao.findById(Long.parseLong(contactID));
					
					if(contact != null) {
						
						customer =new MatchedCustomers();
						//External ID mostly null.
						customer.setCustomerId(contact.getExternalId() == null ||  contact.getExternalId().isEmpty() ? Constants.STRING_NILL : contact.getExternalId());
						customer.setPhone(contact.getMobilePhone() == null ||  contact.getMobilePhone().isEmpty() ? Constants.STRING_NILL : contact.getMobilePhone());
						customer.setEmailAddress(contact.getEmailId() == null ||  contact.getEmailId().isEmpty() ? Constants.STRING_NILL : contact.getEmailId());
						customer.setFirstName(contact.getFirstName() == null ||  contact.getFirstName().isEmpty() ? Constants.STRING_NILL : contact.getFirstName());
						customer.setLastName(contact.getLastName() == null ||  contact.getLastName().isEmpty() ? Constants.STRING_NILL : contact.getLastName());
						customer.setMembershipNumber(membershipNumber);
						
						ContactsLoyalty conMembership = contactsLoyaltyDao.findByContactId(user.getUserId(), contact.getContactId());
						if(conMembership != null){
							
							List<LoyaltyTransactionChild> retList = loyaltyTransactionChildDao.findBy(contact.getContactId(),conMembership.getLoyaltyId(),
									startDate, endDate,currentDocSid, user.getUserId());
							List<LoyaltyTransaction> loyalty = new ArrayList<LoyaltyTransaction>();
							if(retList != null){
								for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
									
									LoyaltyTransaction ltytrx = new LoyaltyTransaction();
									ltytrx.setAmount(loyaltyTransactionChild.getEnteredAmount()+Constants.STRING_NILL);
									ltytrx.setType(loyaltyTransactionChild.getTransactionType()+Constants.STRING_NILL);
									ltytrx.setValueCode(loyaltyTransactionChild.getEarnType()+Constants.STRING_NILL);
									loyalty.add(ltytrx);
									
								}//for
								
							}//if
							
							trx.setLoyalty(loyalty);
							customer.setMembershipNumber(conMembership.getCardNumber());
						}//if
						
					}//if
					
				}//if
				
				//transactions.add(trx);
				
				transactions.add(trx);
				
			}
			customer.setTransactions(transactions);
			Recptamount = 0.0;
			recptNumber = currentDocSid;
			matchedCustomers.add(customer);
				
				status = new Status("0", "Contact lookup was successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				purchaseHistoryResponse = prepareFinalResponse(responseHeader, status, matchedCustomers, new ResponseReport());
				return purchaseHistoryResponse;
				
				
			}
			else if(membershipNumber != null && !membershipNumber.isEmpty()){
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
				RetailProSalesDao retailProSalesDao = (RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
				Contacts contact = conMembership.getContact();
				
				List<MatchedCustomers> matchedCustomers = new ArrayList<MatchedCustomers>();
				MatchedCustomers customer = new MatchedCustomers();
				
				
				//List<MatchedCustomers> matchedCustomers = prepareMatchedCustomers(retResultSetForContacts, conMembership.getContact());
				customer =new MatchedCustomers();
				//External ID mostly null.
				customer.setCustomerId(contact.getExternalId() == null ||  contact.getExternalId().isEmpty() ? Constants.STRING_NILL : contact.getExternalId());
				customer.setPhone(contact.getMobilePhone() == null ||  contact.getMobilePhone().isEmpty() ? Constants.STRING_NILL : contact.getMobilePhone());
				customer.setEmailAddress(contact.getEmailId() == null ||  contact.getEmailId().isEmpty() ? Constants.STRING_NILL : contact.getEmailId());
				customer.setFirstName(contact.getFirstName() == null ||  contact.getFirstName().isEmpty() ? Constants.STRING_NILL : contact.getFirstName());
				customer.setLastName(contact.getLastName() == null ||  contact.getLastName().isEmpty() ? Constants.STRING_NILL : contact.getLastName());
				customer.setMembershipNumber(membershipNumber);
				
				List<SaleTransaction> transactions = new ArrayList<SaleTransaction>();
				List<Item> items = new ArrayList<Item>();
				String recptNumber = null;
				double Recptamount = 0.0;
				String currentDocSid = null;
				Double quantity = 0.0;
				Double salesPrise = 0.0;
				Double tax = 0.0;  
				Double discount = 0.0;
				Double currentrecordAmount = 0.0;
				Item item = null;
				int docSidsCounter = 0;
				ResultSet retResultSetForSales = retailProSalesDao.executeQryForPurchaseHistory(membershipNumber, contact.getContactId().toString(), receiptNumber, storeNumber,
						startDate, endDate, offSet, maxRecords, user.getUserId());
				int size= 0;
				if (retResultSetForSales != null)   
				{  
					retResultSetForSales.beforeFirst();  
					retResultSetForSales.last();  
				  size = retResultSetForSales.getRow();  
				}  
					/*status = new Status("800012", PropertyUtil.getErrorMessage(80001, OCConstants.ERROR_MOBILEAPP_FLAG),
                            OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
                    return prepareFinalResponse(responseHeader, status);*/
				boolean isLastRecord = false;
				retResultSetForSales.beforeFirst();
				boolean isOFfSetMet =false;
				boolean isfirstCheck = false;
				String date = null;
				String receptNum = null;
				String paymode = null;
			if(size > 0) 
				while(retResultSetForSales.next()){
					logger.debug("started at  row ==="+retResultSetForSales.getRow());
					isLastRecord = false;
					//if(currentDocSid == null) {
						currentDocSid = retResultSetForSales.getString("sal.doc_sid");
					//}
						if(!isOFfSetMet && (recptNumber ==  null || !recptNumber.equals(currentDocSid))){
							docSidsCounter ++;
							if(offSetInt == docSidsCounter){
								isOFfSetMet = true;
								isfirstCheck = true;
							}
							recptNumber = currentDocSid;
						}
						if(!isOFfSetMet) continue;
						if(isOFfSetMet && isfirstCheck){
							
							recptNumber =  null;
							docSidsCounter = 0;
							isfirstCheck = false;
						}
					if(recptNumber ==  null ){
						items = new ArrayList<Item>();
						docSidsCounter ++;
						recptNumber = currentDocSid;
						
						date = retResultSetForSales.getString("sal.sales_date") != null ? retResultSetForSales.getString("sal.sales_date")+OCConstants.APPEND_SERVER_TIMEZONE : Constants.STRING_NILL ;
						receptNum = retResultSetForSales.getString("sal.reciept_number");
						paymode = retResultSetForSales.getString("sal.tender_type");
					}
					
					if( !recptNumber.equals(currentDocSid)) {
						
						if(docSidsCounter == maxRecordsInt) break;
						docSidsCounter ++;
						if(offSetInt == docSidsCounter) isOFfSetMet = true;
						isLastRecord = true;
						//Recptamount += currentrecordAmount;
						SaleTransaction trx = new SaleTransaction();
						trx.setDate(date);
						trx.setReceiptNumber(receptNum);
						trx.setReceiptAmount(Recptamount+Constants.STRING_NILL);
						trx.setItems(items);
						
						Payment payment = new Payment();
						payment.setAmount(Constants.STRING_NILL);
						payment.setMode(paymode);
						
						trx.setPayment(payment);
						
						
						
						//if(contact.getLoyaltyCustomer() != null && contact.getLoyaltyCustomer() == 1 ) {
							
						
							conMembership = contactsLoyaltyDao.findByContactId(user.getUserId(), contact.getContactId());
							if(conMembership != null){
								
								List<LoyaltyTransactionChild> retList = loyaltyTransactionChildDao.findBy(contact.getContactId(),conMembership.getLoyaltyId(),
										startDate, endDate,currentDocSid, user.getUserId());
								List<LoyaltyTransaction> loyalty = new ArrayList<LoyaltyTransaction>();
								if(retList != null){
									for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
										
										LoyaltyTransaction ltytrx = new LoyaltyTransaction();
										ltytrx.setAmount(loyaltyTransactionChild.getEnteredAmount()+Constants.STRING_NILL);
										ltytrx.setType(loyaltyTransactionChild.getTransactionType()+Constants.STRING_NILL);
										ltytrx.setValueCode(loyaltyTransactionChild.getEarnType()+Constants.STRING_NILL);
										loyalty.add(ltytrx);
										
									}//for
									
								}//if
								
								trx.setLoyalty(loyalty);
								customer.setMembershipNumber(conMembership.getCardNumber());
							}
						//}
						
						
						transactions.add(trx);
						
						customer.setTransactions(transactions);
						Recptamount = 0.0;
						recptNumber = currentDocSid;
						items = new ArrayList<Item>();
						
						date = retResultSetForSales.getString("sal.sales_date") != null ? retResultSetForSales.getString("sal.sales_date")+OCConstants.APPEND_SERVER_TIMEZONE : Constants.STRING_NILL ;
						receptNum = retResultSetForSales.getString("sal.reciept_number");
						paymode = retResultSetForSales.getString("sal.tender_type");
					}
					
					quantity = retResultSetForSales.getDouble("sal.quantity");
					salesPrise = retResultSetForSales.getDouble("sal.sales_price");
					tax = retResultSetForSales.getDouble("sal.tax");
					discount = retResultSetForSales.getDouble("sal.discount");
					currentrecordAmount = (quantity*salesPrise)+tax-discount;
					Recptamount += currentrecordAmount;
					
					item = new Item();
					item.setItemCategory(retResultSetForSales.getString("sku.item_category"));
					item.setDepartmentCode(retResultSetForSales.getString("sku.department_code"));
					item.setItemClass(retResultSetForSales.getString("sku.class_code"));
					item.setItemSubClass(retResultSetForSales.getString("sku.subclass_code"));
					item.setDCS(retResultSetForSales.getString("sku.dcs"));
					item.setVendorCode(retResultSetForSales.getString("sku.vendor_code"));
					item.setSkuNumber(retResultSetForSales.getString("sku.sku"));
					item.setBilledUnitPrice(retResultSetForSales.getString("sku.list_price"));
					item.setQuantity(retResultSetForSales.getString("sal.quantity"));
					items.add(item);
					
					//recptNumber = currentDocSid;
				}

				logger.debug("getfetchsize ==="+retResultSetForSales.getFetchSize());
				//for last record
				//Recptamount += currentrecordAmount;
			if(size > 0 && isLastRecord == false) {
				retResultSetForSales.last();
				logger.debug("retResultSetForSales.last() "+retResultSetForSales.getRow());
				SaleTransaction trx = new SaleTransaction();
				trx.setDate(date);
				trx.setReceiptNumber(receptNum);
				trx.setReceiptAmount(Recptamount+Constants.STRING_NILL);
				trx.setItems(items);
				
				Payment payment = new Payment();
				payment.setAmount(Constants.STRING_NILL);
				payment.setMode(paymode);//retResultSetForSales.getString("sal.tender_type"));
				
				trx.setPayment(payment);
				
				
				//if(contact.getLoyaltyCustomer() != null && contact.getLoyaltyCustomer() == 1 ) {
					
				
					 conMembership = contactsLoyaltyDao.findByContactId(user.getUserId(), contact.getContactId());
					if(conMembership != null){
						
						
						List<LoyaltyTransactionChild> retList = loyaltyTransactionChildDao.findBy(contact.getContactId(),conMembership.getLoyaltyId(),
								startDate, endDate,recptNumber, user.getUserId());
						List<LoyaltyTransaction> loyalty = new ArrayList<LoyaltyTransaction>();
						if(retList != null){
							for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
								
								LoyaltyTransaction ltytrx = new LoyaltyTransaction();
								ltytrx.setAmount(loyaltyTransactionChild.getEnteredAmount()+Constants.STRING_NILL);
								ltytrx.setType(loyaltyTransactionChild.getTransactionType()+Constants.STRING_NILL);
								ltytrx.setValueCode(loyaltyTransactionChild.getEarnType()+Constants.STRING_NILL);
								loyalty.add(ltytrx);
								
							}//for
							
						}//if
						
						trx.setLoyalty(loyalty);
					}
				
				//}
				transactions.add(trx);
				
			}
				
				customer.setTransactions(transactions);
				Recptamount = 0.0;
				recptNumber = currentDocSid;
				matchedCustomers.add(customer);
				
				
			
				status = new Status("0", "Contact lookup was successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				purchaseHistoryResponse = prepareFinalResponse(responseHeader, status, matchedCustomers, new ResponseReport());
				return purchaseHistoryResponse;
				
			}//if
			else if((emailId != null && !emailId.isEmpty()) || (phone != null && !phone.isEmpty())) {
				
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
				List<POSMapping> contactPOSMap = null;
				ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				RetailProSalesDao retailProSalesDao = (RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
				POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance()
												.getDAOByName(OCConstants.POSMAPPING_DAO);
				contactPOSMap = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_CONTACTS + "'",
						user.getUserId());
				
				status = validateContactPOSMap(contactPOSMap, user.getUserId());
				
				if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
					purchaseHistoryResponse = prepareFinalResponse(responseHeader, status);
					return purchaseHistoryResponse;
				}
				
				// validate emailId and mobile
				TreeMap<String, List<String>> prioMap = null;
				prioMap = Utility.getPriorityMap(user.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
				status = validatePriorityMap(prioMap, user);
				if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
					purchaseHistoryResponse = prepareFinalResponse(responseHeader, status);
					return purchaseHistoryResponse;
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
					List<MatchedCustomers> matchedCustomers = new ArrayList<MatchedCustomers>();
					
						for (Contacts contact : contactsList) {

							MatchedCustomers customer = new MatchedCustomers();
							
							//List<MatchedCustomers> matchedCustomers = prepareMatchedCustomers(retResultSetForContacts, conMembership.getContact());
							customer =new MatchedCustomers();
							//External ID mostly null.
							customer.setCustomerId(contact.getExternalId() == null ||  contact.getExternalId().isEmpty() ? Constants.STRING_NILL : contact.getExternalId());
							customer.setPhone(contact.getMobilePhone() == null ||  contact.getMobilePhone().isEmpty() ? Constants.STRING_NILL : contact.getMobilePhone());
							customer.setEmailAddress(contact.getEmailId() == null ||  contact.getEmailId().isEmpty() ? Constants.STRING_NILL : contact.getEmailId());
							customer.setFirstName(contact.getFirstName() == null ||  contact.getFirstName().isEmpty() ? Constants.STRING_NILL : contact.getFirstName());
							customer.setLastName(contact.getLastName() == null ||  contact.getLastName().isEmpty() ? Constants.STRING_NILL : contact.getLastName());
							//customer.setMembershipNumber(membershipNumber);
							
							List<SaleTransaction> transactions = new ArrayList<SaleTransaction>();
							List<Item> items = new ArrayList<Item>();
							String recptNumber = null;
							double Recptamount = 0.0;
							String currentDocSid = null;
							Double quantity = 0.0;
							Double salesPrise = 0.0;
							Double tax = 0.0;  
							Double discount = 0.0;
							Double currentrecordAmount = 0.0;
							Item item = null;
							int docSidsCounter = 0;
							String date = null;
							String receptNum = null;
							String paymode = null;
							boolean isOFfSetMet =false;
							boolean isfirstCheck = false;
							ResultSet retResultSetForSales = retailProSalesDao.executeQryForPurchaseHistory(membershipNumber, contact.getContactId().toString(), receiptNumber, storeNumber,
									startDate, endDate, offSet, maxRecords, user.getUserId());
							int size= 0;
							if (retResultSetForSales != null)   
							{  
								retResultSetForSales.beforeFirst();  
								retResultSetForSales.last();  
							  size = retResultSetForSales.getRow();  
							}  
							retResultSetForSales.beforeFirst();
								/*status = new Status("800012", PropertyUtil.getErrorMessage(80001, OCConstants.ERROR_MOBILEAPP_FLAG),
			                            OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			                    return prepareFinalResponse(responseHeader, status);*/
							boolean isLastRecord = false;
						if(size > 0) 
							while(retResultSetForSales.next()){
								logger.debug("started at  row ==="+retResultSetForSales.getRow());
								isLastRecord = false;
								//if(currentDocSid == null) {
									currentDocSid = retResultSetForSales.getString("sal.doc_sid");
								//}
									if(!isOFfSetMet && (recptNumber ==  null || !recptNumber.equals(currentDocSid))){
										docSidsCounter ++;
										if(offSetInt == docSidsCounter){
											isOFfSetMet = true;
											isfirstCheck = true;
										}
										recptNumber = currentDocSid;
									}
									if(!isOFfSetMet) continue;
									if(isOFfSetMet && isfirstCheck){
										
										recptNumber =  null;
										docSidsCounter = 0;
										isfirstCheck = false;
									}
								if(recptNumber ==  null ){
									items = new ArrayList<Item>();
									docSidsCounter ++;
									recptNumber = currentDocSid;
									
									date = retResultSetForSales.getString("sal.sales_date") != null ? retResultSetForSales.getString("sal.sales_date")+OCConstants.APPEND_SERVER_TIMEZONE : Constants.STRING_NILL ;
									receptNum = retResultSetForSales.getString("sal.reciept_number");
									paymode = retResultSetForSales.getString("sal.tender_type");
								}
								if( !recptNumber.equals(currentDocSid)) {
									//
									if(docSidsCounter == maxRecordsInt) break;
									docSidsCounter ++;
									if(offSetInt == docSidsCounter) isOFfSetMet = true;
									isLastRecord = true;
									//Recptamount += currentrecordAmount;
									SaleTransaction trx = new SaleTransaction();
									trx.setDate(date);
									trx.setReceiptNumber(receptNum);
									trx.setReceiptAmount(Recptamount+Constants.STRING_NILL);
									trx.setItems(items);
									
									Payment payment = new Payment();
									payment.setAmount(Constants.STRING_NILL);
									payment.setMode(paymode);
									
									trx.setPayment(payment);
									
									
									
									//if(contact.getLoyaltyCustomer() != null && contact.getLoyaltyCustomer() == 1 ) {
										
										ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
									
										ContactsLoyalty conMembership = contactsLoyaltyDao.findByContactId(user.getUserId(), contact.getContactId());
										if(conMembership != null){
											
											List<LoyaltyTransactionChild> retList = loyaltyTransactionChildDao.findBy(contact.getContactId(),conMembership.getLoyaltyId(),
													startDate, endDate,currentDocSid, user.getUserId());
											List<LoyaltyTransaction> loyalty = new ArrayList<LoyaltyTransaction>();
											if(retList != null){
												for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
													
													LoyaltyTransaction ltytrx = new LoyaltyTransaction();
													ltytrx.setAmount(loyaltyTransactionChild.getEnteredAmount()+Constants.STRING_NILL);
													ltytrx.setType(loyaltyTransactionChild.getTransactionType()+Constants.STRING_NILL);
													ltytrx.setValueCode(loyaltyTransactionChild.getEarnType()+Constants.STRING_NILL);
													loyalty.add(ltytrx);
													
												}//for
												
											}//if
											
											trx.setLoyalty(loyalty);
											customer.setMembershipNumber(conMembership.getCardNumber());
										}
									//}
									
									
									transactions.add(trx);
									
									customer.setTransactions(transactions);
									Recptamount = 0.0;
									recptNumber = currentDocSid;
									items = new ArrayList<Item>();
									
									date = retResultSetForSales.getString("sal.sales_date") != null ? retResultSetForSales.getString("sal.sales_date")+OCConstants.APPEND_SERVER_TIMEZONE : Constants.STRING_NILL ;
									receptNum = retResultSetForSales.getString("sal.reciept_number");
									paymode = retResultSetForSales.getString("sal.tender_type");
								}
								
								quantity = retResultSetForSales.getDouble("sal.quantity");
								salesPrise = retResultSetForSales.getDouble("sal.sales_price");
								tax = retResultSetForSales.getDouble("sal.tax");
								discount = retResultSetForSales.getDouble("sal.discount");
								currentrecordAmount = (quantity*salesPrise)+tax-discount;
								Recptamount += currentrecordAmount;
								
								item = new Item();
								item.setItemCategory(retResultSetForSales.getString("sku.item_category"));
								item.setDepartmentCode(retResultSetForSales.getString("sku.department_code"));
								item.setItemClass(retResultSetForSales.getString("sku.class_code"));
								item.setItemSubClass(retResultSetForSales.getString("sku.subclass_code"));
								item.setDCS(retResultSetForSales.getString("sku.dcs"));
								item.setVendorCode(retResultSetForSales.getString("sku.vendor_code"));
								item.setSkuNumber(retResultSetForSales.getString("sku.sku"));
								item.setBilledUnitPrice(retResultSetForSales.getString("sku.list_price"));
								item.setQuantity(retResultSetForSales.getString("sal.quantity"));
								items.add(item);
								
								recptNumber = currentDocSid;
							}

							logger.debug("getfetchsize ==="+retResultSetForSales.getFetchSize());
							//for last record
							//Recptamount += currentrecordAmount;
						if(size > 0 && isLastRecord == false) {
							retResultSetForSales.last();
							logger.debug("retResultSetForSales.last() "+retResultSetForSales.getRow());
							SaleTransaction trx = new SaleTransaction();
							
							trx.setDate(date);
							trx.setReceiptNumber(receptNum);
							trx.setReceiptAmount(Recptamount+Constants.STRING_NILL);
							trx.setItems(items);
							
							Payment payment = new Payment();
							payment.setAmount(Constants.STRING_NILL);
							payment.setMode(paymode);//retR
							trx.setPayment(payment);
							
							
							//if(contact.getLoyaltyCustomer() != null && contact.getLoyaltyCustomer() == 1 ) {
								
								ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
							
								ContactsLoyalty conMembership = contactsLoyaltyDao.findByContactId(user.getUserId(), contact.getContactId());
								if(conMembership != null){
									
									
									List<LoyaltyTransactionChild> retList = loyaltyTransactionChildDao.findBy(contact.getContactId(),conMembership.getLoyaltyId(),
											startDate, endDate,recptNumber, user.getUserId());
									List<LoyaltyTransaction> loyalty = new ArrayList<LoyaltyTransaction>();
									if(retList != null){
										for (LoyaltyTransactionChild loyaltyTransactionChild : retList) {
											
											LoyaltyTransaction ltytrx = new LoyaltyTransaction();
											ltytrx.setAmount(loyaltyTransactionChild.getEnteredAmount()+Constants.STRING_NILL);
											ltytrx.setType(loyaltyTransactionChild.getTransactionType()+Constants.STRING_NILL);
											ltytrx.setValueCode(loyaltyTransactionChild.getEarnType()+Constants.STRING_NILL);
											loyalty.add(ltytrx);
											
										}//for
										
									}//if
									
									trx.setLoyalty(loyalty);
								}
							
							//}
							transactions.add(trx);
							
						}
							
							customer.setTransactions(transactions);
							Recptamount = 0.0;
							recptNumber = currentDocSid;
							matchedCustomers.add(customer);
							
							
						}//for
					
				status = new Status("0", "Contact lookup was successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				purchaseHistoryResponse = prepareFinalResponse(responseHeader, status, matchedCustomers, new ResponseReport());
				return purchaseHistoryResponse;
			}
			else{
				
				status = new Status("800005", PropertyUtil.getErrorMessage(800005, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				purchaseHistoryResponse = prepareFinalResponse(responseHeader, status);
				return purchaseHistoryResponse;
			}
			
		} catch (Exception e) {
			logger.error("Exception ", e);
			status = new Status("800001", PropertyUtil.getErrorMessage(800001, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			purchaseHistoryResponse = prepareFinalResponse(responseHeader, status);
			return purchaseHistoryResponse;
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
	
	private PurchaseHistoryResponse prepareFinalResponse(
			ResponseHeader header, Status status)throws BaseServiceException {
		PurchaseHistoryResponse purchaseHistoryResponse = new PurchaseHistoryResponse();
		
		purchaseHistoryResponse.setHeader(header);
		List<MatchedCustomers> matchedCustomers = new ArrayList<MatchedCustomers>();
		MatchedCustomers matchedCustomer = new MatchedCustomers();
		matchedCustomers.add(matchedCustomer);
		purchaseHistoryResponse.setMatchedCustomers(matchedCustomers);
		//purchaseHistoryResponse.setReport(new ResponseReport());
		purchaseHistoryResponse.setStatus(status);
	
		return purchaseHistoryResponse;
	}//prepareFinalResponse
	
	private PurchaseHistoryResponse prepareFinalResponse(
			ResponseHeader header, Status status, List<MatchedCustomers> matchedCustomers, ResponseReport report)throws BaseServiceException {
		PurchaseHistoryResponse purchaseHistoryResponse = new PurchaseHistoryResponse();
		
		purchaseHistoryResponse.setHeader(header);
		purchaseHistoryResponse.setMatchedCustomers(matchedCustomers);
		//purchaseHistoryResponse.setReport(report);
		purchaseHistoryResponse.setStatus(status);
	
		return purchaseHistoryResponse;
	}//prepareFinalResponse
	private Status validateInnerObjects(PurchaseHistoryRequest purchaseHistoryRequest) throws Exception{
		
		Status status = null;
		Lookup lookup = purchaseHistoryRequest.getLookup();
		RequestReport report = purchaseHistoryRequest.getReport();
		
		/*if( (lookup.getStoreNumber() == null || lookup.getStoreNumber().trim().isEmpty()) ||
				(lookup.getDocSID() == null || lookup.getDocSID().trim().isEmpty())){
			
			status = new Status(
					"800005", PropertyUtil.getErrorMessage(800005, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
			
		}*/
		
		if((lookup.getEmailAddress() == null || lookup.getEmailAddress().isEmpty()) && 
				(lookup.getPhone() == null || lookup.getPhone().isEmpty()) && 
				(lookup.getMembershipNumber() == null || lookup.getMembershipNumber().isEmpty()) && 
				(lookup.getReceiptNumber() == null || lookup.getReceiptNumber().isEmpty()) &&
				(lookup.getDocSID() == null || lookup.getDocSID().trim().isEmpty())) {
			
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
	private Status validateJsonData(PurchaseHistoryRequest purchaseHistoryRequest) throws Exception{
		logger.info("Entered validateJsonData method >>>>");
		
		Status status = null;
		if(purchaseHistoryRequest == null ){
			status = new Status(
					"800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(purchaseHistoryRequest.getUser() == null){
			status = new Status(
					"800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(purchaseHistoryRequest.getLookup() == null){
			status = new Status(
					"800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(purchaseHistoryRequest.getUser().getUserName() == null || 
				purchaseHistoryRequest.getUser().getUserName().trim().length() <=0 || 
				purchaseHistoryRequest.getUser().getOrganizationId() == null || 
				purchaseHistoryRequest.getUser().getOrganizationId().trim().length() <=0 /*|| 
						purchaseHistoryRequest.getUser().getToken() == null || 
						purchaseHistoryRequest.getUser().getToken().trim().length() <=0*/) {
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
}
