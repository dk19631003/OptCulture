package org.mq.optculture.business.importcontacts;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.LoyaltyMemberSessionID;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.SuppressedContacts;
import org.mq.marketer.campaign.beans.Unsubscribes;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.contacts.CustomFieldValidator;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.SuppressedContactsDao;
import org.mq.marketer.campaign.dao.UnsubscribesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.importcontact.ImportResponse;
import org.mq.optculture.model.importcontact.Lookup;
import org.mq.optculture.model.importcontact.Phone;
import org.mq.optculture.model.importcontact.Report;
import org.mq.optculture.model.importcontact.Status;
import org.mq.optculture.model.importcontact.Suppress;
import org.mq.optculture.model.importcontact.User;
import org.mq.optculture.model.loyalty.Customers;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.importcontact.Customer;
import org.mq.optculture.model.importcontact.Email;
import org.mq.optculture.model.importcontact.Header;
import org.mq.optculture.model.importcontact.ImportRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
// import com.newrelic.api.agent.Response;

public class ImportContactsBusinessServiceImpl implements ImportContactsBusinessService {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	long reportCount=0l; 
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		// TODO Auto-generated method stub
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		ImportResponse importResponse = null;

		try {
			logger.debug("-------entered processRequest---------");
			// json to object
			Gson gson = new Gson();
			ImportRequest importRequest = null;
			try {
				importRequest = gson.fromJson(baseRequestObject.getJsonValue(), ImportRequest.class);
			} catch (JsonSyntaxException e) {
				logger.error("Exception ::", e);
				Status status = new Status("700000",
						PropertyUtil.getErrorMessage(700000, OCConstants.ERROR_IMPORT_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				importResponse = prepareFinalResponse(new Header(), status, importRequest,null,null);
				String json = gson.toJson(importResponse);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.IMPORT_CONTACTS_SERVICE_REQUEST);
				return baseResponseObject;
			}
			ImportContactsBusinessService importContactsBusinessService = (ImportContactsBusinessService) ServiceLocator
					.getInstance().getServiceByName(OCConstants.IMPORT_CONTACTS_BUSINESS_SERVICE);
			importResponse = (ImportResponse) importContactsBusinessService
					.processImportContactRequest(importRequest);

			// object to json
			String json = gson.toJson(importResponse);
			baseResponseObject.setJsonValue(json);
			baseResponseObject.setAction(OCConstants.IMPORT_CONTACTS_SERVICE_REQUEST);
			return baseResponseObject;
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("-------exit  processRequest---------");
		return baseResponseObject;

	}

	@Override
	public ImportResponse processImportContactRequest(ImportRequest importRequest) throws BaseServiceException {
		// TODO Auto-generated method stub
		List <Customer> getCustomerInfoList =null;
		ImportResponse importResponse = new ImportResponse(); 
		Header header = importRequest.getHeader();
		String importType = importRequest.getImportType();
		Lookup lookup = importRequest.getLookup();
		Report report = importRequest.getReport();
		User user =importRequest.getUser();
		Customer customer =new Customer();
		String contactType = report.getContactType() ;		
		String reportType =report.getReportType();
		Status status =null;
		try {
		logger.info("Header==>"+header.getRequestId()+ " ImportType==>"+importType+" lookup"+lookup+" report"+report+" user"+user);
			

			UsersDao usersDao = null;
			try {
				usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
			String userName = user.getUserName();
			String orgId = user.getOrganizationId();
			String token = user.getToken();

			Users userObj = null;
			if(token != null && !token.isEmpty()) userObj = usersDao.findByToken(userName + Constants.USER_AND_ORG_SEPARATOR + orgId, token);
			else userObj = usersDao.findByUsername(userName + Constants.USER_AND_ORG_SEPARATOR + orgId);
			status = validateRootObjects(importRequest);
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equalsIgnoreCase(status.getStatus())) {
				logger.info("Entered If root objects");
				importResponse = prepareFinalResponse(header, status, importRequest,null,null);
				return importResponse;
			}
			status = validateInnerObjects(header, user,importType);
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
				logger.info("Entered If inner objects"+status.getErrorCode());
				importResponse = prepareFinalResponse(header, status, importRequest,null,null);
				return importResponse;
			}		
			String sourceType = header.getSourceType();
			if(sourceType != null && !sourceType.isEmpty() && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) ){
				String sessionID = importRequest.getUser().getSessionID();
				if(sessionID == null || sessionID.isEmpty()){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					importResponse = prepareFinalResponse(header, status, importRequest,null,null);
					return importResponse;
				}
				LoyaltyMemberSessionID loyaltyMemberSessionID = LoyaltyProgramHelper.validateSessionID(sessionID);
				if(loyaltyMemberSessionID == null){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					importResponse = prepareFinalResponse(header, status, importRequest,null,null);
					return importResponse;
				}
				
				String cardNumber = LoyaltyProgramHelper.getCardFromSesstionID(sessionID);
				if(importRequest.getLookup().getMembershipNumber() != null && 
						importRequest.getLookup().getMembershipNumber().trim().length() > 0 && 
						!importRequest.getLookup().getMembershipNumber().trim().equals(cardNumber)){
					status = new Status("800029", PropertyUtil.getErrorMessage(800029, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					importResponse = prepareFinalResponse(header, status, importRequest,null,null);
					return importResponse;
					
				}
				
			}
			if(importType.equalsIgnoreCase(OCConstants.IMPORT_TYPE_BULK)) {
				status=validateReport(report);
				if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equalsIgnoreCase(status.getStatus())) {
					importResponse = prepareFinalResponse(header, status, importRequest,null,null);
					return importResponse;
				}
				getCustomerInfoList = getCustomerInfoList(report, userObj,header, importType);
			}
				
			else if(importType.equalsIgnoreCase(OCConstants.IMPORT_TYPE_LOOKUP)) {
				List<Customers> lookupCustomers = importRequest.getLookup().getCustomer();
				if(lookupCustomers == null || lookupCustomers.isEmpty() ) {
					
					status=validateLookup(lookup);
					if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equalsIgnoreCase(status.getStatus())) {
						importResponse = prepareFinalResponse(header, status, importRequest,null,null);
						return importResponse;
					}
					getCustomerInfoList = getSingleLookupInfo(lookup,userObj,header); 
					
				}else{
					//APP-2236
					//APP-3622
					boolean CustomerValidation=false;
					if(lookupCustomers != null)
					for(Customers lookupCustomer:lookupCustomers) {
						if((lookupCustomer.getCustomerId() 	 != null && !lookupCustomer.getCustomerId().isEmpty()) ||
						   (lookupCustomer.getEmailAddress() != null && !lookupCustomer.getEmailAddress().isEmpty()) ||
						   (lookupCustomer.getPhone() != null && !lookupCustomer.getPhone().isEmpty()) ||
						   (lookupCustomer.getFirstName() !=null && !lookupCustomer.getFirstName().isEmpty()) ||
						   (lookupCustomer.getLastName()!=null && !lookupCustomer.getLastName().isEmpty())) {
							CustomerValidation=true;
							break;
						}
					} 
					if(!CustomerValidation) {
						status = new Status("700015", PropertyUtil.getErrorMessage(700015, OCConstants.ERROR_IMPORT_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						importResponse = prepareFinalResponse(header, status, importRequest,null,null);
						return importResponse;
					}
					
					getCustomerInfoList = getCustomerLookupInfo(lookup,userObj,header);	
				}
			}
			
			
			status=new Status("0","Number of contacts imported successfully ::"+reportCount,OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			importResponse = prepareFinalResponse(header, status, importRequest, report, getCustomerInfoList);
			reportCount=0;
			return importResponse;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
			 status = new Status("800001", PropertyUtil.getErrorMessage(800001, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			importResponse = prepareFinalResponse(header, status, importRequest,null,null);
			return importResponse;
		}
	}
	
	private ImportResponse prepareFinalResponse(Header header,Status status,ImportRequest importRequest,Report report,List<Customer> matchedCustomers) throws BaseServiceException {
		ImportResponse importResponse=new ImportResponse();
		Customer customer =new Customer();
		
		if(report!=null) {
		report.setContactSource(null);
		report.setContactType(null);
		report.setContactList(null);
		report.setReportType(null);
		report.setStartDate(null);
		report.setEndDate(null);
		report.setOffset(null);
		report.setMaxRecords(null);
		report.setStore(null);
		report.setTotalCount(reportCount+"");
		}
	  
	    if(reportCount == 0 && status.getErrorCode().equals("0")) {
	    	status = new Status("700014",PropertyUtil.getErrorMessage(700014,OCConstants.ERROR_IMPORT_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);	
	    }

		if(report == null) {
			report=new Report();
			report.setTotalCount("0");
			}
		if(matchedCustomers == null) {
			matchedCustomers= new ArrayList<Customer>(); 
			matchedCustomers.add(customer);
			}
			header.setPcFlag(null);
			header.setStoreNumber(null);
		    header.setEmployeeId(null);
		    header.setTerminalId(null);
		    header.setTransactionId("");
		    header.setTransactionDate("");
		importResponse.setHeader(header);
		importResponse.setStatus(status);
		importResponse.setMatchedCustomers(matchedCustomers);
		importResponse.setReport(report);
		
		
		return importResponse;
	
	}
	
	
	private List<Customer> getCustomerLookupInfo(Lookup lookup,Users user,Header header){
		
		String sourceType = header.getSourceType();
		Status status =null;
		SuppressedContactsDao suppressedContactsDao= null;
		ContactsDao contactsDao = null;  
		Customer customer =new Customer();
		Suppress suppress =new Suppress();
		Email email = new Email();
		Phone phone =new Phone();
		ResultSet contactsList = null;
		List<Customer> matchedCustomerList = new ArrayList<Customer>();
		try {
			suppressedContactsDao = (SuppressedContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO);
			contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
			// TODO Condition for when all the lookup condition values namely email,membership, phone,firstName and lastName
		List<Customers> customersList = lookup.getCustomer();
		String customerSIDStr = Constants.STRING_NILL;
		String emailIDStr = Constants.STRING_NILL;
		String mobileIDStr = Constants.STRING_NILL;
		//APP-3622
		String firstName = Constants.STRING_NILL;
		String lastName =Constants.STRING_NILL;
		
		for (Customers customers : customersList) {
			
			if(customers.getCustomerId() != null && !customers.getCustomerId().isEmpty()){
				
				if(!customerSIDStr.isEmpty()) customerSIDStr += Constants.DELIMETER_COMMA;
				
				customerSIDStr += "'"+customers.getCustomerId()+"'";
			}
			
			if(customers.getEmailAddress() != null && !customers.getEmailAddress().isEmpty()){
				
				if(!emailIDStr.isEmpty()) emailIDStr += Constants.DELIMETER_COMMA;
				
				emailIDStr += "'"+customers.getEmailAddress()+"'";
			}
			
			if(customers.getPhone() != null && !customers.getPhone().isEmpty()){
				
				if(!mobileIDStr.isEmpty()) mobileIDStr += "|";
				
				mobileIDStr += customers.getPhone();
			}
			//APP-3622
			if(customers.getFirstName() != null && !customers.getFirstName().isEmpty()){
				
				if(!firstName.isEmpty()) firstName += "|";
				
				firstName += customers.getFirstName();
			}
			
			if(customers.getLastName()!= null && !customers.getLastName().isEmpty()){
				
				if(!lastName.isEmpty()) lastName += "|";
				
				lastName += customers.getLastName();
			}
			
		}
	
		contactsList = contactsDao.findContactsInfoBy(user, customerSIDStr, emailIDStr, mobileIDStr,firstName,lastName);
		
		try {
			if(contactsList!=null) {
			while(contactsList.next())
			{
				try {
					//logger.info("PHONE=====>" + contactsListItem.getPhone().toString());		
					customer =new Customer();
					/*email =new Email();
					phone =new Phone();
					suppress =new Suppress();*/
					//External ID mostly null.
					customer.setCustomerId(contactsList.getString("external_id") == null ? "" : contactsList.getString("external_id"));
					customer.setInstanceId(contactsList.getString("instance_id") == null ? "" : contactsList.getString("instance_id"));
					customer.setPhone(contactsList.getString("mobile_phone") == null ? "" : contactsList.getString("mobile_phone"));
					customer.setEmailAddress(contactsList.getString("email_id") == null ? "" : contactsList.getString("email_id"));
					customer.setFirstName(contactsList.getString("first_name") == null ? "" : contactsList.getString("first_name"));
					customer.setLastName(contactsList.getString("last_name") == null ? "" : contactsList.getString("last_name"));
					customer.setMembershipNumber(contactsList.getString("card_number") == null ? "" : contactsList.getString("card_number"));
					customer.setCity(contactsList.getString("city") == null ? "" : contactsList.getString("city"));
					customer.setState(contactsList.getString("state") == null ? "" : contactsList.getString("state"));
					customer.setPostal(contactsList.getString("zip") == null ? "" : contactsList.getString("zip")+"");
					customer.setCountry(contactsList.getString("country") == null ? "" : contactsList.getString("country"));
					customer.setHomeStore(contactsList.getString("home_store") == null ? "" : contactsList.getString("home_store"));
					
					String enrolledStore = contactsList.getString("pos_location_id") == null ? "" : contactsList.getString("pos_location_id");
					String SBS = contactsList.getString("sbs_num") == null ? "" : contactsList.getString("sbs_num");
					if(enrolledStore != null && enrolledStore.equalsIgnoreCase("L-APP")) {
						enrolledStore = Constants.STRING_NILL;
						SBS = Constants.STRING_NILL;
					}
					customer.setEnrolledStore(enrolledStore);
					customer.setSubsidiaryNumber(SBS);
					customer.setGender(contactsList.getString("gender") == null ? "" : contactsList.getString("gender"));
					customer.setCreationDate(contactsList.getString("created_date") == null ? "" : contactsList.getString("created_date"));
					
					if(sourceType!=null && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)) {
						String birthAniversaryArray[] = dateConvert(contactsList,user);
						customer.setBirthday(birthAniversaryArray[0]==null?"":birthAniversaryArray[0]); 
						customer.setAnniversary(birthAniversaryArray[1]==null?"":birthAniversaryArray[1]); 			
					}
					else {
					customer.setBirthday(contactsList.getString("birth_day") == null ? "" : contactsList.getString("birth_day")); 
					customer.setAnniversary(contactsList.getString("anniversary_day") == null ? "" : contactsList.getString("anniversary_day")); 			
					}
					
					customer.setBirthdayDateFormat(LoyaltyProgramHelper.getDateFormatFor(POSFieldsEnum.birthDay.getOcAttr(), Constants.POS_MAPPING_TYPE_CONTACTS, user.getUserId()));
					customer.setAnniversaryDateFormat(LoyaltyProgramHelper.getDateFormatFor(POSFieldsEnum.anniversary.getOcAttr(),Constants.POS_MAPPING_TYPE_CONTACTS, user.getUserId() ));
					
					customer.setAddressLine1(contactsList.getString("address_one") == null ? "" : contactsList.getString("address_one"));
					customer.setAddressLine2(contactsList.getString("address_two") == null ? "" : contactsList.getString("address_two"));
					
					//if(reportType.equalsIgnoreCase(OCConstants.IMPORT_REPORT_TYPE_COMPLETE)) {
					customer.setUDF1(contactsList.getString("udf1") == null ? "" : contactsList.getString("udf1"));
					customer.setUDF2(contactsList.getString("udf2") == null ? "" : contactsList.getString("udf2"));
					customer.setUDF3(contactsList.getString("udf3") == null ? "" : contactsList.getString("udf3"));
					customer.setUDF4(contactsList.getString("udf4") == null ? "" : contactsList.getString("udf4"));
					customer.setUDF5(contactsList.getString("udf5") == null ? "" : contactsList.getString("udf5"));
					customer.setUDF6(contactsList.getString("udf6") == null ? "" : contactsList.getString("udf6"));
					customer.setUDF7(contactsList.getString("udf7") == null ? "" : contactsList.getString("udf7"));
					customer.setUDF8(contactsList.getString("udf8") == null ? "" : contactsList.getString("udf8"));
					customer.setUDF9(contactsList.getString("udf9") == null ? "" : contactsList.getString("udf9"));
					customer.setUDF10(contactsList.getString("udf10") == null ? "" : contactsList.getString("udf10"));
					customer.setUDF11(contactsList.getString("udf11") == null ? "" : contactsList.getString("udf11"));
					customer.setUDF12(contactsList.getString("udf12") == null ? "" : contactsList.getString("udf12"));
					customer.setUDF13(contactsList.getString("udf13") == null ? "" : contactsList.getString("udf13"));
					customer.setUDF14(contactsList.getString("udf14") == null ? "" : contactsList.getString("udf14"));
					customer.setUDF15(contactsList.getString("udf15") == null ? "" : contactsList.getString("udf15"));
					/*email.setIsTrue(contactsList.getString("email_id") != null?(contactsList.getString("emailSuppressedTime") != null || contactsList.getString("unsubscribeSuppressedTime") != null?"Y":"N"):"");
					if(contactsList.getString("unsubscribeSuppressedTime") != null && !contactsList.getString("unsubscribeSuppressedTime").isEmpty()) {
						email.setReason("Unsubscribe");
						email.setTimestamp(contactsList.getString("unsubscribeSuppressedTime") == null ? "" : contactsList.getString("unsubscribeSuppressedTime"));						
					}
					else{
						email.setReason(contactsList.getString("emailReason") == null ? "" : contactsList.getString("emailReason"));
						email.setTimestamp(contactsList.getString("emailSuppressedTime") == null ? "" : contactsList.getString("emailSuppressedTime"));
					}
					logger.info("MYLOG===="+contactsList.getString("suppressed_time"));
					phone.setIsTrue(contactsList.getString("mobile_phone") != null?(contactsList.getString("suppressed_time") == null?"N":"Y"):"");
					phone.setReason(contactsList.getString("type") == null ? "" : contactsList.getString("type"));
					phone.setTimestamp(contactsList.getString("suppressed_time") == null ? "" : contactsList.getString("suppressed_time"));
					suppress.setEmail(email);
					suppress.setPhone(phone);
					customer.setSuppress(suppress);
					*/
					
					matchedCustomerList.add(customer);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ", e);
				}

			}
			contactsList.last();
			reportCount=contactsList.getRow();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}		
		return matchedCustomerList;

		
	}
	
	
	private List<Customer> getSingleLookupInfo(Lookup lookup,Users user, Header header)
	{
		Status status =null;
		SuppressedContactsDao suppressedContactsDao= null;
		ContactsDao contactsDao = null;  
		Customer customer =new Customer();
		Suppress suppress =new Suppress();
		Email email = new Email();
		Phone phone =new Phone();
		ResultSet contactsList = null;
		List<Customer> matchedCustomerList = new ArrayList<Customer>();
		try {
			suppressedContactsDao = (SuppressedContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO);
			contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
			// TODO Condition for when all the lookup condition values namely email,membership and phone 
		
		contactsList = contactsDao.findContactsByLookup(lookup, user); 
		
		try {
			if(contactsList!=null)
			while(contactsList.next())
			{
				//logger.info("PHONE=====>" + contactsListItem.getPhone().toString());
				String sourceType = header.getSourceType();
				customer =new Customer();
				email =new Email();
				phone =new Phone();
				suppress =new Suppress();
				//External ID mostly null.
				customer.setCustomerId(contactsList.getString("external_id") == null ? "" : contactsList.getString("external_id"));
				customer.setInstanceId(contactsList.getString("instance_id") == null ? "" : contactsList.getString("instance_id"));
				customer.setPhone(contactsList.getString("mobile_phone") == null ? "" : contactsList.getString("mobile_phone"));
				customer.setEmailAddress(contactsList.getString("email_id") == null ? "" : contactsList.getString("email_id"));
				customer.setFirstName(contactsList.getString("first_name") == null ? "" : contactsList.getString("first_name"));
				customer.setLastName(contactsList.getString("last_name") == null ? "" : contactsList.getString("last_name"));
				customer.setMembershipNumber(contactsList.getString("card_number") == null ? "" : contactsList.getString("card_number"));
				customer.setCity(contactsList.getString("city") == null ? "" : contactsList.getString("city"));
				customer.setState(contactsList.getString("state") == null ? "" : contactsList.getString("state"));
				customer.setPostal(contactsList.getString("zip") == null ? "" : contactsList.getString("zip")+"");//Changes
				customer.setCountry(contactsList.getString("country") == null ? "" : contactsList.getString("country"));
				customer.setHomeStore(contactsList.getString("home_store") == null ? "" : contactsList.getString("home_store"));
				
				//customer.setEnrolledStore(contactsList.getString("pos_location_id") == null ? "" : contactsList.getString("pos_location_id"));
				//customer.setSubsidiaryNumber(contactsList.getString("sbs_num") == null ? "" : contactsList.getString("sbs_num"));
				
				//when its a mobile registration, the store comes as L-APP and Rpro can not understand this henc eleave it as blank for OptUpdate
				String enrolledStore = contactsList.getString("pos_location_id") == null ? "" : contactsList.getString("pos_location_id");
				String SBS = contactsList.getString("sbs_num") == null ? "" : contactsList.getString("sbs_num");
				if(enrolledStore != null && enrolledStore.equalsIgnoreCase("L-APP")) {
					enrolledStore = Constants.STRING_NILL;
					SBS = Constants.STRING_NILL;
				}
				customer.setEnrolledStore(enrolledStore);
				customer.setSubsidiaryNumber(SBS);
				
				
				customer.setGender(contactsList.getString("gender") == null ? "" : contactsList.getString("gender"));
				customer.setCreationDate(contactsList.getString("created_date") == null ? "" : contactsList.getString("created_date"));
				if(sourceType!=null && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)) {
					String birthAniversaryArray[] = dateConvert(contactsList,user);
					customer.setBirthday(birthAniversaryArray[0]==null?"":birthAniversaryArray[0]); 
					customer.setAnniversary(birthAniversaryArray[1]==null?"":birthAniversaryArray[1]); 			
				}
				else {
				customer.setBirthday(contactsList.getString("birth_day") == null ? "" : contactsList.getString("birth_day")); 
				customer.setAnniversary(contactsList.getString("anniversary_day") == null ? "" : contactsList.getString("anniversary_day")); 			
				}	
				customer.setBirthdayDateFormat(LoyaltyProgramHelper.getDateFormatFor(POSFieldsEnum.birthDay.getOcAttr(), Constants.POS_MAPPING_TYPE_CONTACTS, user.getUserId()));
				customer.setAnniversaryDateFormat(LoyaltyProgramHelper.getDateFormatFor(POSFieldsEnum.anniversary.getOcAttr(),Constants.POS_MAPPING_TYPE_CONTACTS, user.getUserId() ));
				
				customer.setAddressLine1(contactsList.getString("address_one") == null ? "" : contactsList.getString("address_one"));
				customer.setAddressLine2(contactsList.getString("address_two") == null ? "" : contactsList.getString("address_two"));
				
				//if(reportType.equalsIgnoreCase(OCConstants.IMPORT_REPORT_TYPE_COMPLETE)) {
				customer.setUDF1(contactsList.getString("udf1") == null ? "" : contactsList.getString("udf1"));
				customer.setUDF2(contactsList.getString("udf2") == null ? "" : contactsList.getString("udf2"));
				customer.setUDF3(contactsList.getString("udf3") == null ? "" : contactsList.getString("udf3"));
				customer.setUDF4(contactsList.getString("udf4") == null ? "" : contactsList.getString("udf4"));
				customer.setUDF5(contactsList.getString("udf5") == null ? "" : contactsList.getString("udf5"));
				customer.setUDF6(contactsList.getString("udf6") == null ? "" : contactsList.getString("udf6"));
				customer.setUDF7(contactsList.getString("udf7") == null ? "" : contactsList.getString("udf7"));
				customer.setUDF8(contactsList.getString("udf8") == null ? "" : contactsList.getString("udf8"));
				customer.setUDF9(contactsList.getString("udf9") == null ? "" : contactsList.getString("udf9"));
				customer.setUDF10(contactsList.getString("udf10") == null ? "" : contactsList.getString("udf10"));
				customer.setUDF11(contactsList.getString("udf11") == null ? "" : contactsList.getString("udf11"));
				customer.setUDF12(contactsList.getString("udf12") == null ? "" : contactsList.getString("udf12"));
				customer.setUDF13(contactsList.getString("udf13") == null ? "" : contactsList.getString("udf13"));
				customer.setUDF14(contactsList.getString("udf14") == null ? "" : contactsList.getString("udf14"));
				customer.setUDF15(contactsList.getString("udf15") == null ? "" : contactsList.getString("udf15"));
				email.setIsTrue(contactsList.getString("email_id") != null?(contactsList.getString("emailSuppressedTime") != null || contactsList.getString("unsubscribeSuppressedTime") != null?"Y":"N"):"");
				if(contactsList.getString("unsubscribeSuppressedTime") != null && !contactsList.getString("unsubscribeSuppressedTime").isEmpty()) {
					email.setReason("Unsubscribe");
					email.setTimestamp(contactsList.getString("unsubscribeSuppressedTime") == null ? "" : contactsList.getString("unsubscribeSuppressedTime"));						
				}
				else{
					email.setReason(contactsList.getString("emailReason") == null ? "" : contactsList.getString("emailReason"));
					email.setTimestamp(contactsList.getString("emailSuppressedTime") == null ? "" : contactsList.getString("emailSuppressedTime"));
				}
				logger.info("MYLOG===="+contactsList.getString("suppressed_time"));
				phone.setIsTrue(contactsList.getString("mobile_phone") != null?(contactsList.getString("suppressed_time") == null?"N":"Y"):"");
				phone.setReason(contactsList.getString("type") == null ? "" : contactsList.getString("type"));
				phone.setTimestamp(contactsList.getString("suppressed_time") == null ? "" : contactsList.getString("suppressed_time"));
				suppress.setEmail(email);
				suppress.setPhone(phone);
				customer.setSuppress(suppress);
				
				
				matchedCustomerList.add(customer);

			}
			contactsList.last();
			reportCount=contactsList.getRow();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}		
		return matchedCustomerList;

		}
		private List<Customer> getSuppressedEmailList(Report report, Users user){
		logger.info("---entered getSuppressedEmailList---");
	
		Customer customer =new Customer();
		Suppress suppress =new Suppress();
		Email email = new Email();
		
		
		int Offset=Integer.parseInt(report.getOffset()), MaxResult = Integer.parseInt(report.getMaxRecords());
		int Limit=MaxResult+Offset;
		int	SuppressedListSize = 0, unsubContTypeListSize = 0; 
		int CompleteListSize=0;
		List<SuppressedContacts> SuppressedList = null;
		List<Unsubscribes> unsubContTypeList = null;
		SuppressedContactsDao suppressedContactsDao= null;
		UnsubscribesDao unsubscribesDao = null;
		List<Customer> matchedCustomerList = new ArrayList<Customer>();
//		List<Customer> matchedCustomerSubList = new ArrayList<Customer>();
		
		try {
			suppressedContactsDao = (SuppressedContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO);
			unsubscribesDao = (UnsubscribesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.UNSUBSCRIBE_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
		 SuppressedList = suppressedContactsDao.findSuppressedContactsByReport(user,report) ;
//		 CompleteListSize = SuppressedList.size() + unsubContTypeList.size();
		 SuppressedListSize = SuppressedList.size();
//		 unsubContTypeListSize = unsubContTypeList.size();

	if(Offset<SuppressedListSize) {	
		 if(SuppressedList!=null)
				for(SuppressedContacts suppressedListItem : SuppressedList )
				{
					email =new Email();	
					suppress =new Suppress();
					customer =new Customer();
					
					email.setReason(suppressedListItem.getType() == null ? "" : suppressedListItem.getType());
					email.setTimestamp(suppressedListItem.getSuppressedtime() != null ? MyCalendar.calendarToString(suppressedListItem.getSuppressedtime(), MyCalendar.FORMAT_DATETIME_STYEAR) : "");
					customer.setEmailAddress(suppressedListItem.getEmail() == null ? "" : suppressedListItem.getEmail());
					suppress.setEmail(email);
					customer.setSuppress(suppress);
					matchedCustomerList.add(customer);		
				}
		}
		//Setting the offset so to fetch from Unsubscribe list
		Offset = Offset-SuppressedListSize;
		MaxResult = MaxResult-SuppressedListSize;

		if(unsubContTypeList!=null && Offset >= 0)
			report.setOffset(Offset+"");
			report.setMaxRecords(MaxResult+"");
			 unsubContTypeList = unsubscribesDao.findUnsubscribesByReport(user, report);
	
				for(Unsubscribes unsubContTypeListItem: unsubContTypeList )
				{
					email =new Email();	
					suppress =new Suppress();
					customer =new Customer();
					
					email.setReason("Unsubscribe");
					email.setTimestamp(unsubContTypeListItem.getDate() != null ? MyCalendar.calendarToString(unsubContTypeListItem.getDate(), MyCalendar.FORMAT_DATETIME_STYEAR) : "");
					customer.setEmailAddress(unsubContTypeListItem.getEmailId() == null ? "" : unsubContTypeListItem.getEmailId());
					suppress.setEmail(email);
					customer.setSuppress(suppress);
					matchedCustomerList.add(customer);
					
				}
				
		reportCount=matchedCustomerList.size();
		logger.info("---exit getSuppressedEmailList---");
		return matchedCustomerList;
		
	}
	
	private List<Customer> getSuppressedPhoneList(Report report, Users user)
	{
		Customer customer =new Customer();
		Suppress suppress =new Suppress();
		Phone phone = new Phone();
		SMSSuppressedContactsDao smsSuppressedContactsDao= null;
		List<Customer> matchedCustomerList = new ArrayList<Customer>();
		try {
			smsSuppressedContactsDao = (SMSSuppressedContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
		List<SMSSuppressedContacts> SuppressedList = smsSuppressedContactsDao.findSuppressedContactsByReport(user,report);
		
		for(SMSSuppressedContacts suppressedListItem : SuppressedList )
		{
		phone =new Phone();	
		suppress =new Suppress();
		customer =new Customer();
		
		phone.setReason(suppressedListItem.getType() == null ? "" : suppressedListItem.getType());
		phone.setTimestamp(suppressedListItem.getSuppressedtime() != null ? MyCalendar.calendarToString(suppressedListItem.getSuppressedtime(), MyCalendar.FORMAT_DATETIME_STYEAR) : "");
		customer.setPhone(suppressedListItem.getMobile() == null ? "" : suppressedListItem.getMobile());
		suppress.setPhone(phone);
		customer.setSuppress(suppress);
		matchedCustomerList.add(customer);
		
		}	
		
		reportCount=matchedCustomerList.size();
		return matchedCustomerList;
		
	}
	
	
	
	
	private List<Customer> getCustomerInfoList(Report report, Users user,Header header, String importType) throws BaseServiceException {

		
		if(report.getContactType().equalsIgnoreCase("SuppressedEmail")) return getSuppressedEmailList(report, user);
		if(report.getContactType().equalsIgnoreCase("SuppressedPhone")) return getSuppressedPhoneList(report, user);
		
		String sourceType = header.getSourceType();
		ContactsDao contactsDao = null;  
		Customer customer =null;
		/*Suppress suppress =new Suppress();
		Email email = new Email();
		Phone phone =new Phone();*/
		ResultSet contactsList = null;
		List<Customer> matchedCustomerList = new ArrayList<Customer>();
		try {
			contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
		
		logger.info("StartDate===>"+report.getStartDate()+" ,"+"EndDate===>"+report.getEndDate() +" ,UserID===>"+user.getUserId());
		
		

//		String qryPhone = " FROM SMSSuppressedContacts WHERE id=" +user.getUserId()+" AND suppressedtime BETWEEN '"
//				+startDate+"' AND '"+endDate+"'";
//		
		contactsList = contactsDao.findContactsByReport(report,user, importType); 
		
		try {
			if(contactsList!=null)
			while(contactsList.next())
			{
				try {
					//logger.info("PHONE=====>" + contactsListItem.getPhone().toString());		
					customer =new Customer();
					/*email =new Email();
					phone =new Phone();
					suppress =new Suppress();*/
					//External ID mostly null.
					customer.setCustomerId(contactsList.getString("external_id") == null ? "" : contactsList.getString("external_id"));
					customer.setInstanceId(contactsList.getString("instance_id") == null ? "" : contactsList.getString("instance_id"));
					customer.setPhone(contactsList.getString("mobile_phone") == null ? "" : contactsList.getString("mobile_phone"));
					customer.setEmailAddress(contactsList.getString("email_id") == null ? "" : contactsList.getString("email_id"));
					customer.setFirstName(contactsList.getString("first_name") == null ? "" : contactsList.getString("first_name"));
					customer.setLastName(contactsList.getString("last_name") == null ? "" : contactsList.getString("last_name"));
					customer.setMembershipNumber(contactsList.getString("card_number") == null ? "" : contactsList.getString("card_number"));
					customer.setCity(contactsList.getString("city") == null ? "" : contactsList.getString("city"));
					customer.setState(contactsList.getString("state") == null ? "" : contactsList.getString("state"));
					customer.setPostal(contactsList.getString("zip") == null ? "" : contactsList.getString("zip"));
					customer.setCountry(contactsList.getString("country") == null ? "" : contactsList.getString("country"));
					customer.setHomeStore(contactsList.getString("home_store") == null ? "" : contactsList.getString("home_store"));
					
					//when its a mobile registration, the store comes as L-APP and Rpro can not understand this henc eleave it as blank for OptUpdate
					String enrolledStore = contactsList.getString("pos_location_id") == null ? "" : contactsList.getString("pos_location_id");
					String SBS = contactsList.getString("sbs_num") == null ? "" : contactsList.getString("sbs_num");
					if(enrolledStore != null && enrolledStore.equalsIgnoreCase("L-APP")) {
						enrolledStore = Constants.STRING_NILL;
						SBS = Constants.STRING_NILL;
					}
					customer.setEnrolledStore(enrolledStore);
					customer.setSubsidiaryNumber(SBS);
					
					//customer.setEnrolledStore(contactsList.getString("pos_location_id") == null ? "" : contactsList.getString("pos_location_id"));
					//customer.setSubsidiaryNumber(contactsList.getString("sbs_num") == null ? "" : contactsList.getString("sbs_num"));
					customer.setGender(contactsList.getString("gender") == null ? "" : contactsList.getString("gender"));
					customer.setCreationDate(contactsList.getString("created_date") == null ? "" : contactsList.getString("created_date"));
					if(sourceType!=null && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)) {
						String birthAniversaryArray[] = dateConvert(contactsList,user);
						customer.setBirthday(birthAniversaryArray[0]==null?"":birthAniversaryArray[0]); 
						customer.setAnniversary(birthAniversaryArray[1]==null?"":birthAniversaryArray[1]); 			
					}
					else {
					customer.setBirthday(contactsList.getString("birth_day") == null ? "" : contactsList.getString("birth_day")); 
					customer.setAnniversary(contactsList.getString("anniversary_day") == null ? "" : contactsList.getString("anniversary_day")); 			
					}customer.setBirthdayDateFormat(LoyaltyProgramHelper.getDateFormatFor(POSFieldsEnum.birthDay.getOcAttr(), Constants.POS_MAPPING_TYPE_CONTACTS, user.getUserId()));
					customer.setAnniversaryDateFormat(LoyaltyProgramHelper.getDateFormatFor(POSFieldsEnum.anniversary.getOcAttr(),Constants.POS_MAPPING_TYPE_CONTACTS, user.getUserId() ));
					customer.setAddressLine1(contactsList.getString("address_one") == null ? "" : contactsList.getString("address_one"));
					customer.setAddressLine2(contactsList.getString("address_two") == null ? "" : contactsList.getString("address_two"));
					
					if(report.getReportType().equalsIgnoreCase(OCConstants.IMPORT_REPORT_TYPE_COMPLETE)) {
					customer.setUDF1(contactsList.getString("udf1") == null ? "" : contactsList.getString("udf1"));
					customer.setUDF2(contactsList.getString("udf2") == null ? "" : contactsList.getString("udf2"));
					customer.setUDF3(contactsList.getString("udf3") == null ? "" : contactsList.getString("udf3"));
					customer.setUDF4(contactsList.getString("udf4") == null ? "" : contactsList.getString("udf4"));
					customer.setUDF5(contactsList.getString("udf5") == null ? "" : contactsList.getString("udf5"));
					customer.setUDF6(contactsList.getString("udf6") == null ? "" : contactsList.getString("udf6"));
					customer.setUDF7(contactsList.getString("udf7") == null ? "" : contactsList.getString("udf7"));
					customer.setUDF8(contactsList.getString("udf8") == null ? "" : contactsList.getString("udf8"));
					customer.setUDF9(contactsList.getString("udf9") == null ? "" : contactsList.getString("udf9"));
					customer.setUDF10(contactsList.getString("udf10") == null ? "" : contactsList.getString("udf10"));
					customer.setUDF11(contactsList.getString("udf11") == null ? "" : contactsList.getString("udf11"));
					customer.setUDF12(contactsList.getString("udf12") == null ? "" : contactsList.getString("udf12"));
					customer.setUDF13(contactsList.getString("udf13") == null ? "" : contactsList.getString("udf13"));
					customer.setUDF14(contactsList.getString("udf14") == null ? "" : contactsList.getString("udf14"));
					customer.setUDF15(contactsList.getString("udf15") == null ? "" : contactsList.getString("udf15"));
					/*email.setIsTrue(contactsList.getString("email_id") != null?(contactsList.getString("emailSuppressedTime") != null || contactsList.getString("unsubscribeSuppressedTime") != null?"Y":"N"):"");
					if(contactsList.getString("unsubscribeSuppressedTime") != null && !contactsList.getString("unsubscribeSuppressedTime").isEmpty()) {
						email.setReason("Unsubscribe");
						email.setTimestamp(contactsList.getString("unsubscribeSuppressedTime") == null ? "" : contactsList.getString("unsubscribeSuppressedTime"));	
						}
					else{
					    email.setReason(contactsList.getString("emailReason") == null ? "" : contactsList.getString("emailReason"));
						email.setTimestamp(contactsList.getString("emailSuppressedTime") == null ? "" : contactsList.getString("emailSuppressedTime"));
					
					}
					
					phone.setIsTrue(contactsList.getString("mobile_phone") != null?(contactsList.getString("suppressed_time") == null?"N":"Y"):"");
					phone.setReason(contactsList.getString("type") == null ? "" : contactsList.getString("type"));
					phone.setTimestamp(contactsList.getString("suppressed_time") == null ? "" : contactsList.getString("suppressed_time"));
					suppress.setEmail(email);
					suppress.setPhone(phone);
					customer.setSuppress(suppress);*/
					}
					matchedCustomerList.add(customer);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ", e);
				}
			}
			contactsList.last();
			reportCount=contactsList.getRow();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
		
		return matchedCustomerList;
	}	

	//Validations here on
	private Status validateReport(Report report)	{
		logger.debug("-------entered  validateReport---------");
		String contactType = report.getContactType();
		String contactSource =report.getContactSource();
		Status status = null;
		if(report.getStartDate()!=null && report.getStartDate() !=null && !report.getStartDate().isEmpty() && !report.getStartDate().isEmpty()) {
		if(!CustomFieldValidator.validateDate(report.getStartDate(), "date", MyCalendar.FORMAT_DATETIME_STYEAR)) {
			logger.info("Error : unable to fetch the requested data,got wrong start date in JSON ****");
			status = new Status("700024",PropertyUtil.getErrorMessage(700024,OCConstants.ERROR_IMPORT_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(!CustomFieldValidator.validateDate(report.getEndDate(), "Date", MyCalendar.FORMAT_DATETIME_STYEAR)) {
			logger.info("Error : unable to fetch the requested data,got wrong end date in JSON ****");
			status = new Status("700025",PropertyUtil.getErrorMessage(700025,OCConstants.ERROR_IMPORT_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		}
		if(!contactType.equalsIgnoreCase("All") && !contactType.equalsIgnoreCase("SuppressedEmail") &&!contactType.equalsIgnoreCase("SuppressedPhone") && !contactType.equalsIgnoreCase("PushNotificationsTrue")) {
			status = new Status("700016",PropertyUtil.getErrorMessage(700016,OCConstants.ERROR_IMPORT_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(!contactSource.equalsIgnoreCase("All") && !contactSource.equalsIgnoreCase("Webform") && !contactSource.equalsIgnoreCase("eComm") && !contactSource.equalsIgnoreCase("Store") && 
				!contactSource.equalsIgnoreCase("LoyaltyApp") && !contactSource.equalsIgnoreCase("Mobile_App")) {
			status = new Status("700017",PropertyUtil.getErrorMessage(700017,OCConstants.ERROR_IMPORT_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(report.getOffset()!=null && report.getOffset().isEmpty()){
			
			status = new Status("700018",PropertyUtil.getErrorMessage(700018,OCConstants.ERROR_IMPORT_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;			
		}
		if(report.getOffset()!=null && Integer.parseInt(report.getOffset())<0){
			
			status = new Status("700022",PropertyUtil.getErrorMessage(700022,OCConstants.ERROR_IMPORT_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;			
		}
		
		if(report.getMaxRecords()!=null && report.getMaxRecords().isEmpty()){
			status = new Status("700019",PropertyUtil.getErrorMessage(700019,OCConstants.ERROR_IMPORT_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(report.getMaxRecords()!=null && Integer.parseInt(report.getMaxRecords())<1){
			status = new Status("700023",PropertyUtil.getErrorMessage(700023,OCConstants.ERROR_IMPORT_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		
		if(report.getMaxRecords()!=null && Integer.parseInt(report.getMaxRecords()) > 1000){
			status = new Status("700020",PropertyUtil.getErrorMessage(700020,OCConstants.ERROR_IMPORT_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		logger.debug("-------exit  validateReport---------");
	return status;		
	}
	
	private Status validateLookup(Lookup lookup)	{
		logger.debug("-------entered validateLookup---------");
		
		
		Status status= null;
		
		//App-3622
		
		if(lookup.getFirstName()!=null && lookup.getFirstName().length() > 0 && !Utility.validateName(lookup.getFirstName())){
		    status = new Status("700026", PropertyUtil.getErrorMessage(700026, OCConstants.ERROR_IMPORT_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
				
		}
		if(lookup.getLastName()!=null && lookup.getLastName().length() > 0 && !Utility.validateName(lookup.getLastName())){
			status = new Status("700027", PropertyUtil.getErrorMessage(700027, OCConstants.ERROR_IMPORT_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(lookup.getEmailAddress()!=null && lookup.getEmailAddress().length() > 0 &&!Utility.validateEmail(lookup.getEmailAddress())){
			status = new Status("700011", PropertyUtil.getErrorMessage(700011, OCConstants.ERROR_IMPORT_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		
		}
		if(lookup.getPhone()!=null && lookup.getPhone().length() > 0 && !Utility.validateUserPhoneNum(lookup.getPhone())){
			status = new Status("700012", PropertyUtil.getErrorMessage(700012, OCConstants.ERROR_IMPORT_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if((lookup.getPhone()==null || (lookup.getPhone()!=null && lookup.getPhone().isEmpty())) &&
		  (lookup.getEmailAddress()==null || (lookup.getEmailAddress()!=null && lookup.getEmailAddress().isEmpty())) &&
		  (lookup.getFirstName()==null || (lookup.getFirstName()!=null && lookup.getFirstName().isEmpty())) && //APP-3622
		  (lookup.getLastName()==null  || (lookup.getLastName()!=null && lookup.getLastName().isEmpty()))   &&
		  (lookup.getMembershipNumber()==null ||( lookup.getMembershipNumber()!=null && lookup.getMembershipNumber().isEmpty())) && 
		  (lookup.getCustomer() ==null || (lookup.getCustomer() != null && lookup.getCustomer().size() > 0))){//APP-2230
			status = new Status("700015", PropertyUtil.getErrorMessage(700015, OCConstants.ERROR_IMPORT_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		return status;
	}
	
	private Status validateRootObjects(ImportRequest importRequest) throws BaseServiceException {
		Status status= null;
		try {
			logger.debug("-------entered validateRootObject---------");

			if (importRequest == null) {
				status = new Status("700001", PropertyUtil.getErrorMessage(700001, OCConstants.ERROR_IMPORT_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing validateRootObject::::: ", e);
		}
		logger.info("STATUS==="+status);
		logger.debug("-------exit  validateRootObject---------");
		return status;
	}
		


	private Status validateInnerObjects(Header header, User user,String importType)  {
		Status status= null;
		logger.info("------Entered validateInnerObjects-----");
		if (header == null) {
			status = new Status("700002", PropertyUtil.getErrorMessage(700002, OCConstants.ERROR_IMPORT_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if (header.getRequestId() == null || header.getRequestId().isEmpty()) {
			status = new Status("700003", PropertyUtil.getErrorMessage(700003, OCConstants.ERROR_IMPORT_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}

		if (header.getRequestDate() == null || header.getRequestDate().isEmpty()) {
			status = new Status("700004", PropertyUtil.getErrorMessage(700004, OCConstants.ERROR_IMPORT_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if (user == null) {
			status = new Status("700005", PropertyUtil.getErrorMessage(700005, OCConstants.ERROR_IMPORT_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}

		String userNameStr = user.getUserName();
		if (userNameStr == null || userNameStr.trim().length() == 0) {
			status = new Status("700006", PropertyUtil.getErrorMessage(700006, OCConstants.ERROR_IMPORT_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		String orgId = user.getOrganizationId();
		if (orgId == null || orgId.trim().length() == 0) {
			status = new Status("700007", PropertyUtil.getErrorMessage(700007, OCConstants.ERROR_IMPORT_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		//removed

		/*String tokenStr = user.getToken();
		if ( tokenStr == null || tokenStr.trim().length() == 0) {
			
			status = new Status("700008", PropertyUtil.getErrorMessage(700008, OCConstants.ERROR_IMPORT_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/
		UsersDao usersDao = null;
		try {
			usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}	
		Users users = usersDao.findByToken(user.getUserName()+ Constants.USER_AND_ORG_SEPARATOR + user.getOrganizationId(), user.getToken());
		if (users == null) {
			status = new Status("700009", PropertyUtil.getErrorMessage(700009, OCConstants.ERROR_IMPORT_FLAG)
					,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(importType==null){
			status = new Status("700010", PropertyUtil.getErrorMessage(700010, OCConstants.ERROR_IMPORT_FLAG)
					,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(importType.isEmpty()){
			status = new Status("700010", PropertyUtil.getErrorMessage(700010, OCConstants.ERROR_IMPORT_FLAG)
					,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(!importType.equalsIgnoreCase("Bulk") && !importType.equalsIgnoreCase("Lookup")){
			status = new Status("700021", PropertyUtil.getErrorMessage(700021, OCConstants.ERROR_IMPORT_FLAG)
					,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.info("------Exit validateInnerObjects-----");			
		return status;
	}

	//APP-3282
	private String[] dateConvert(ResultSet contactsList, Users user) {
			// TODO Auto-generated method stub
			try {
				String birthAniversaryArray[]= {"",""};
				String birthDay = contactsList.getString("birth_day") == null ? "" : contactsList.getString("birth_day");
				String anniversary = contactsList.getString("anniversary_day") == null ? "" : contactsList.getString("anniversary_day");
			
				if(birthDay!=null && !birthDay.isEmpty() ) {
					Date date = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR).parse(birthDay);
					birthAniversaryArray[0] = new SimpleDateFormat(MyCalendar.FORMAT_YEARMONTH_HYPHEN).format(date);
				}
				if(anniversary!=null && !anniversary.isEmpty() ) {
					Date date = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR).parse(anniversary);
				birthAniversaryArray[1] = new SimpleDateFormat(MyCalendar.FORMAT_YEARMONTH_HYPHEN).format(date);
				}
				return birthAniversaryArray;
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.info("e==>"+e);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				logger.info("e==>"+e);
			}
			return null;
		}

	
	}
