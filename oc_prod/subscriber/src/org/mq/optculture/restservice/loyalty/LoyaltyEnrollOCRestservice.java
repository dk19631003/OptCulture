package org.mq.optculture.restservice.loyalty;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyaltyStage;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;
/**
 * === OptCulture Loyalty Program ===
 * Loyalty Enrolment RESTFul Service Controller.
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyEnrollOCRestservice extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/**
	 * Delegates request.
	 * Calls Loyalty Enrollment business service to process request.
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		logger.info("Started Loyalty Enroll Rest Service...");
		
		LoyaltyTransactionParent tranParent = createNewTransaction();
		Date date = tranParent.getCreatedDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);
		
		ContactsLoyaltyStage loyaltyStage = null;
		LoyaltyEnrollResponse enrollResponse = null;
		String userName = null;
		Status status = null;
		String responseJson = Constants.STRING_NILL;
		Gson gson = new Gson();
		response.setContentType("application/json");
		try{
			
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			logger.info("JSON Request: = "+requestJson);
			
			ResponseHeader responseHeader = new ResponseHeader();
			responseHeader.setRequestDate(Constants.STRING_NILL);
			responseHeader.setRequestId(Constants.STRING_NILL);
			responseHeader.setTransactionDate(transDate);
			responseHeader.setTransactionId(Constants.STRING_NILL+tranParent.getTransactionId());
			/*responseHeader.setSubsidiaryNumber(Constants.STRING_NILL);
			responseHeader.setReceiptNumber(Constants.STRING_NILL);
			responseHeader.setReceiptAmount(Constants.STRING_NILL);*/
			
			LoyaltyEnrollRequest enrollRequest = null;
			try{
				enrollRequest = gson.fromJson(requestJson, LoyaltyEnrollRequest.class);
			}catch(Exception e){
				status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(enrollResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, enrollResponse, null);
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			}
			
			if(enrollRequest == null){
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(enrollResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, enrollResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(enrollRequest.getHeader() == null){
				status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(enrollResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, enrollResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(enrollRequest.getHeader().getRequestId() == null || enrollRequest.getHeader().getRequestId().trim().isEmpty() ||
					enrollRequest.getHeader().getRequestDate() == null || enrollRequest.getHeader().getRequestDate().trim().isEmpty()){
				status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(enrollResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, enrollResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}

			if(enrollRequest.getUser() == null){
				status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(enrollResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, enrollResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(enrollRequest.getUser().getUserName() == null || enrollRequest.getUser().getOrganizationId().trim().length() <=0 || 
					enrollRequest.getUser().getOrganizationId() == null || enrollRequest.getUser().getOrganizationId().trim().length() <=0 || 
							enrollRequest.getUser().getToken() == null || enrollRequest.getUser().getToken().trim().length() <=0) {
				status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(enrollResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, enrollResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			userName = enrollRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					enrollRequest.getUser().getOrganizationId();
			
			//String pcFlag = enrollRequest.getHeader().getPcFlag();//?
			String requestId = enrollRequest.getHeader().getRequestId();
			LoyaltyTransaction transaction = null;
			/*if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
				transaction = findTransactionByRequestId(requestId);
				if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
					responseJson = transaction.getJsonResponse();
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					enrollResponse = gson.fromJson(responseJson, LoyaltyEnrollResponse.class);
					updateTransaction(tranParent, enrollResponse, userName);
					logger.info("Response = "+responseJson);
					return null;
				}
			}*/	
			
			//code to handle multiple enrolments for single customer due to timeout at pos or connection delay by sparkbase.
			loyaltyStage = findDuplicateRequest(enrollRequest);
			if(loyaltyStage != null){
				logger.info("Duplicate request....timed out request...");
				responseJson = "{\"STATUS\":{\"ERRORCODE\":\"101505\",\"MESSAGE\":\"Error 101505: Request is being processed.\",\"STATUS\":\"Failure\"}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				enrollResponse = gson.fromJson(responseJson, LoyaltyEnrollResponse.class);
				updateTransaction(tranParent, enrollResponse, userName);
				logger.info("Response = "+responseJson);
				return null;
			}
			else{
				loyaltyStage = saveRequestInStageTable(enrollRequest);
			}
			
			/*if(enrollRequest.getMembership() != null && enrollRequest.getMembership().getCardNumber() != null && !enrollRequest.getMembership().getCardNumber().trim().isEmpty() &&
					enrollRequest.getMembership().getIssueCardFlag().equalsIgnoreCase("Y")){*/
				logger.info("issue card flag Yes");
				
				LoyaltyTransaction trans = findRequestBycustSidAndReqId(enrollRequest.getUser().getUserName() + "__" +
						enrollRequest.getUser().getOrganizationId(),enrollRequest.getHeader().getRequestId().trim(),
						enrollRequest.getCustomer().getCustomerId().trim());
				if(trans != null){
					logger.info("duplicate transaction found...");
					responseHeader.setRequestId(enrollRequest.getHeader().getRequestId());
					responseHeader.setRequestDate(enrollRequest.getHeader().getRequestDate());
					status = new Status("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					responseJson = gson.toJson(enrollResponse);
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					updateTransaction(tranParent, enrollResponse, null);
					return null;
				}
			//}
			
			//log transaction
			if(transaction == null){
				transaction = logTransactionRequest(enrollRequest, requestJson, OCConstants.LOYALTY_ONLINE_MODE);
			}
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_ENROLMENT);
			requestObject.setTransactionId(Constants.STRING_NILL+tranParent.getTransactionId());
			requestObject.setTransactionDate(transDate);
			BaseService baseService = null;
			BaseResponseObject responseObject = null;
			
			baseService= ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_ENROLMENT_OC_BUSINESS_SERVICE);
			responseObject = baseService.processRequest(requestObject);
			
			logger.info("JSON Response: = "+responseObject.getJsonValue());
			LoyaltyEnrollResponse responseobject = gson.fromJson(responseObject.getJsonValue(), 
					LoyaltyEnrollResponse.class);
			
			updateTransactionStatus(transaction, responseObject.getJsonValue(), responseobject);
			
			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_ENROLMENT)){
				responseJson = responseObject.getJsonValue();
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				enrollResponse = gson.fromJson(responseJson, LoyaltyEnrollResponse.class);
				updateTransaction(tranParent, enrollResponse, userName);
				logger.info("Response = "+responseJson);
				return null;
			}
		}catch(Exception e){
			logger.error("Error in Enroll rest service", e);
			responseJson = "{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}";
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			enrollResponse = gson.fromJson(responseJson, LoyaltyEnrollResponse.class);
			updateTransaction(tranParent, enrollResponse, userName);
			logger.info("Response = "+responseJson);
			return null;
		}finally{
			if(loyaltyStage != null) deleteRequestFromStageTable(loyaltyStage);
			logger.info("Completed Loyalty Enroll Rest Service.");
		}
		return null;
	}
	
	public LoyaltyTransaction findTransactionByRequestId(String requestId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findByRequestId(requestId, OCConstants.LOYALTY_SERVICE_TYPE_OC);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
		
	public LoyaltyTransaction logTransactionRequest(LoyaltyEnrollRequest requestObject, String jsonRequest, String mode){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;

		LoyaltyTransaction transaction = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction = new LoyaltyTransaction();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(requestObject.getHeader().getRequestId());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHeader().getPcFlag()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ENROLMENT);
			transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
			transaction.setCustomerId(requestObject.getCustomer().getCustomerId().trim());
			transaction.setDocSID(requestObject.getHeader().getDocSID().trim());
			transaction.setStoreNumber(requestObject.getHeader().getStoreNumber().trim());
			transaction.setEmployeeId(requestObject.getHeader().getEmployeeId()!=null && !requestObject.getHeader().getEmployeeId().trim().isEmpty() ? requestObject.getHeader().getEmployeeId().trim():null);
			transaction.setTerminalId(requestObject.getHeader().getTerminalId()!=null && !requestObject.getHeader().getTerminalId().trim().isEmpty() ? requestObject.getHeader().getTerminalId().trim():null);
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	
	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyEnrollResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(response.getStatus().getStatus());
			transaction.setJsonResponse(responseJson);
			if (response.getMembership() != null && response.getMembership().getCardNumber() != null &&
					!response.getMembership().getCardNumber().trim().isEmpty()) {
				transaction.setCardNumber(response.getMembership().getCardNumber());
			} else {
				transaction.setCardNumber(response.getMembership().getPhoneNumber());
			}
			
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	
	private ContactsLoyaltyStage findDuplicateRequest(LoyaltyEnrollRequest requestObject) {
		//find the request in stage
		ContactsLoyaltyStage loyaltyStage = null;
		try{
			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			String custId = requestObject.getCustomer().getCustomerId() == null ? Constants.STRING_NILL : requestObject.getCustomer().getCustomerId().trim(); 
			String email = requestObject.getCustomer().getEmailAddress() == null ? Constants.STRING_NILL : requestObject.getCustomer().getEmailAddress().trim();
			String phone = requestObject.getCustomer().getPhone() == null ? Constants.STRING_NILL : requestObject.getCustomer().getPhone().trim(); 		
			String userName = requestObject.getUser().getUserName()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUser().getOrganizationId();  
			ContactsLoyaltyStage requestStage = contactsLoyaltyStageDao.findRequest(custId, email, phone, null,
					userName, OCConstants.LOYALTY_SERVICE_TYPE_OC, OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			if(requestStage != null){
				return loyaltyStage;
			}
			
		}catch(Exception e){
			logger.error("Exception in finding loyalty duplicate request...", e);
		}
		return loyaltyStage;
	}
	
	private ContactsLoyaltyStage saveRequestInStageTable(LoyaltyEnrollRequest requestObject){

		ContactsLoyaltyStage loyaltyStage = null;
		try{
			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
			String custId = requestObject.getCustomer().getCustomerId() == null ? Constants.STRING_NILL : requestObject.getCustomer().getCustomerId().trim(); 
			String email = requestObject.getCustomer().getEmailAddress() == null ? Constants.STRING_NILL : requestObject.getCustomer().getEmailAddress().trim();
			String phone = requestObject.getCustomer().getPhone() == null ? Constants.STRING_NILL : requestObject.getCustomer().getPhone().trim(); 		
			String userName = requestObject.getUser().getUserName()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUser().getOrganizationId();  
			logger.info("saving request in stage table...");
			loyaltyStage = new ContactsLoyaltyStage();
			loyaltyStage.setCustomerId(custId);
			loyaltyStage.setEmailId(email);
			loyaltyStage.setPhoneNumber(phone);
			loyaltyStage.setUserName(userName);
			loyaltyStage.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
			loyaltyStage.setReqType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			loyaltyStage.setStatus(Constants.LOYALTY_STAGE_PENDING);
			contactsLoyaltyStageDaoForDML.saveOrUpdate(loyaltyStage);
		}catch(Exception e){
			logger.error("Exception while saving loyalty request in stage table...", e);
		}
		return loyaltyStage;
	}
	
	private void deleteRequestFromStageTable(ContactsLoyaltyStage loyaltyStage) {
		
		try{
			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
			logger.info("deleting loyalty stage record...");
			//contactsLoyaltyStageDao.delete(loyaltyStage);
			contactsLoyaltyStageDaoForDML.delete(loyaltyStage);
		}catch(Exception e){
			logger.error("Exception in while deleting request record from staging table...", e);
		}
	}
	
	private LoyaltyTransactionParent createNewTransaction(){
		
		LoyaltyTransactionParent tranx  = null; 
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			tranx = new LoyaltyTransactionParent();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			tranx.setCreatedDate(cal);
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);
			
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}
	
	private LoyaltyEnrollResponse prepareEnrollmentResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyEnrollResponse enrollResponse = new LoyaltyEnrollResponse();
		enrollResponse.setHeader(header);
		if(membershipResponse == null){
			membershipResponse = new MembershipResponse();
			membershipResponse.setCardNumber(Constants.STRING_NILL);
			membershipResponse.setCardPin(Constants.STRING_NILL);
			membershipResponse.setExpiry(Constants.STRING_NILL);
			membershipResponse.setPhoneNumber(Constants.STRING_NILL);
			membershipResponse.setTierLevel(Constants.STRING_NILL);
			membershipResponse.setTierName(Constants.STRING_NILL);
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		enrollResponse.setMembership(membershipResponse);
		enrollResponse.setMatchedCustomers(matchedCustomers);
		enrollResponse.setStatus(status);
		return enrollResponse;
	}
	
	private void updateTransaction(LoyaltyTransactionParent trans, LoyaltyEnrollResponse enrollResponse, String userName) {
		
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			
			if(userName != null){
				trans.setUserName(userName);
			}
			if(enrollResponse.getStatus() != null) {
				trans.setStatus(enrollResponse.getStatus().getStatus());
				trans.setErrorMessage(enrollResponse.getStatus().getMessage());
			}
			if(enrollResponse.getHeader() != null){
				trans.setRequestId(enrollResponse.getHeader().getRequestId());
				trans.setRequestDate(enrollResponse.getHeader().getTransactionDate());
			}
			if(enrollResponse.getMembership() != null) {
					trans.setMembershipNumber(enrollResponse.getMembership().getCardNumber());
					trans.setMobilePhone(enrollResponse.getMembership().getPhoneNumber());
			}
			if(enrollResponse.getMatchedCustomers() != null) {
				//trans.setMobilePhone(enrollResponse.getMatchedCustomers().getPhone());
			}
			//parentDao.saveOrUpdate(trans);
			parentDaoForDML.saveOrUpdate(trans);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
	}
	
	private LoyaltyTransaction findRequestBycustSidAndReqId(String userName, String requestId, String custSID) throws Exception {
		LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		return loyaltyTransactionDao.findRequestBycustSidAndReqId(userName, custSID, requestId, OCConstants.LOYALTY_TRANSACTION_ENROLMENT, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
	}
}
