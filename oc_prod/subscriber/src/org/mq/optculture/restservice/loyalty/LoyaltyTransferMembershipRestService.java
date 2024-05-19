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
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyTransferMembershipJsonRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyTransferMembershipJsonResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyTransferMembershipRequestObject;
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
 * 
 * @author proumyaa
 *Trnasfer API call begin here
 */
public class LoyaltyTransferMembershipRestService extends AbstractController{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/**
	 * Delegates request.
	 * Calls Loyalty Trnasfer business service to process request.
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		logger.info("Started Loyalty Enroll Rest Service...");
		
		LoyaltyTransactionParent tranParent = createNewTransaction();
		Date date = tranParent.getCreatedDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);
		
		LoyaltyTransferMembershipJsonResponse transferMembershipJsonResponse = null;
		ContactsLoyaltyStage loyaltyStage = null;
		//LoyaltyEnrollResponse enrollResponse = null;
		String userName = null;
		Status status = null;
		String responseJson = "";
		Gson gson = new Gson();
		response.setContentType("application/json");
		try{
			
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			logger.info("JSON Request: = "+requestJson);
			
			ResponseHeader responseHeader = new ResponseHeader();
			responseHeader.setRequestDate("");
			responseHeader.setRequestId("");
			responseHeader.setTransactionDate(transDate);
			responseHeader.setTransactionId(""+tranParent.getTransactionId());
			
			LoyaltyTransferMembershipJsonRequest transferRequest = null;
			try{
				transferRequest = gson.fromJson(requestJson, LoyaltyTransferMembershipJsonRequest.class);
			}catch(Exception e){
				status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			}
			
			if(transferRequest == null){
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(transferRequest.getHeader() == null){
				status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(transferRequest.getHeader().getRequestId() == null || transferRequest.getHeader().getRequestId().trim().isEmpty() ||
					transferRequest.getHeader().getRequestDate() == null || transferRequest.getHeader().getRequestDate().trim().isEmpty()){
				status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			if(transferRequest.getMembership() == null || transferRequest.getMembership().getCardNumber() == null){
				
				
				status = new Status("101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return null;
				
			}
			if(transferRequest.getTransferSource() == null || transferRequest.getTransferSource().getCardNumber() == null){
				status = new Status("101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return null;
				
			}
			
			if(transferRequest.getUser() == null){
				status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(transferRequest.getUser().getUserName() == null || transferRequest.getUser().getOrganizationId() == null || transferRequest.getUser().getOrganizationId().trim().length() <=0 || 
							transferRequest.getUser().getToken() == null || transferRequest.getUser().getToken().trim().length() <=0) {
				status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(transferMembershipJsonResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, transferMembershipJsonResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			userName = transferRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					transferRequest.getUser().getOrganizationId();
			
			String pcFlag = transferRequest.getHeader().getPcFlag();
			String requestId = transferRequest.getHeader().getRequestId();
			LoyaltyTransaction transaction = null;
			if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
				transaction = findTransactionByRequestId(requestId);
				if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
					responseJson = transaction.getJsonResponse();
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					transferMembershipJsonResponse = gson.fromJson(responseJson, LoyaltyTransferMembershipJsonResponse.class);
					updateTransaction(tranParent, transferMembershipJsonResponse, userName);
					logger.info("Response = "+responseJson);
					return null;
				}
			}	
			
			//code to handle multiple transfer for single customer due to timeout at pos or connection delay.
			loyaltyStage = findDuplicateRequest(transferRequest);
			if(loyaltyStage != null){
				logger.info("Duplicate request....timed out request...");
				responseJson = "{\"STATUS\":{\"ERRORCODE\":\"101505\",\"MESSAGE\":\"Error 101505: Request is being processed.\",\"STATUS\":\"Failure\"}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				transferMembershipJsonResponse = gson.fromJson(responseJson, LoyaltyTransferMembershipJsonResponse.class);
				updateTransaction(tranParent, transferMembershipJsonResponse, userName);
				logger.info("Response = "+responseJson);
				return null;
			}
			else{
				loyaltyStage = saveRequestInStageTable(transferRequest);
			}
			
			//TODO need to think
			if(transferRequest.getMembership() != null && transferRequest.getMembership().getCardNumber() != null
					&& transferRequest.getMembership().getCardNumber().trim().isEmpty() ){
				
				LoyaltyTransaction duplicateTrx = findRequestByCardAndReqId(transferRequest.getHeader().getRequestId().trim(),
						transferRequest.getTransferSource().getCardNumber().trim());
				if(duplicateTrx != null){
					logger.info("duplicate transaction found...");
					responseHeader.setRequestId(transferRequest.getHeader().getRequestId());
					responseHeader.setRequestDate(transferRequest.getHeader().getRequestDate());
					status = new Status("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					transferMembershipJsonResponse = prepareTransferResponse(responseHeader, null, null, null, null, status);
					responseJson = gson.toJson(transferMembershipJsonResponse);
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					updateTransaction(tranParent, transferMembershipJsonResponse, null);
					return null;
				}
			}
			
			//log transaction
			if(transaction == null){
				transaction = logTransactionRequest(transferRequest, requestJson, OCConstants.LOYALTY_ONLINE_MODE);
			}
			
			
			//update dest card if it is inventory card to avoid any concurrent req between enrollment & transfer
			updateDestCardStatus(transferRequest.getMembership().getCardNumber(), userName);
			
			
			
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_TRANSFER);
			requestObject.setTransactionId(""+tranParent.getTransactionId());
			requestObject.setTransactionDate(transDate);
			BaseService baseService = null;
			BaseResponseObject responseObject = null;
			
			
			baseService= ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_TRANSFER_MEMBERSHIP_BUSINESS_SERVICE);
			responseObject = baseService.processRequest(requestObject);
			
			logger.info("JSON Response: = "+responseObject.getJsonValue());
			LoyaltyTransferMembershipJsonResponse responseobject = gson.fromJson(responseObject.getJsonValue(), 
					LoyaltyTransferMembershipJsonResponse.class);
			
			updateTransactionStatus(transaction, responseObject.getJsonValue(), responseobject);
			revertCardStatus(responseobject, transferRequest.getMembership().getCardNumber(), userName);
			
			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_TRANSFER)){
				responseJson = responseObject.getJsonValue();
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				transferMembershipJsonResponse = gson.fromJson(responseJson, LoyaltyTransferMembershipJsonResponse.class);
				updateTransaction(tranParent, transferMembershipJsonResponse, userName);
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
			transferMembershipJsonResponse = gson.fromJson(responseJson, LoyaltyTransferMembershipJsonResponse.class);
			updateTransaction(tranParent, transferMembershipJsonResponse, userName);
			logger.info("Response = "+responseJson);
			return null;
		}finally{
			if(loyaltyStage != null) deleteRequestFromStageTable(loyaltyStage);
			logger.info("Completed Loyalty Enroll Rest Service.");
		}
		return null;
	}
	
	private void revertCardStatus(LoyaltyTransferMembershipJsonResponse responseobject, String cardNumber, String userName){
		
		if(responseobject.getStatus().getErrorCode()!= null  && !responseobject.getStatus().getErrorCode().equalsIgnoreCase("0")){
			
			revertDestCardStatus(cardNumber, userName);
		}
		
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
	private void updateDestCardStatus(String cardNumber, String userName) {
		
		try {
			LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
			UsersDao usersdao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersdao.findByUsername(userName);
			//loyaltyCardsDao.updateDestCardStatus(cardNumber, user.getUserId());
			loyaltyCardsDaoForDML.updateDestCardStatus(cardNumber, user.getUserId());
		} catch (Exception e) {
			
		}
		
		
	}
	
	private void revertDestCardStatus(String cardNumber, String userName) {
		
		try {
			LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
			UsersDao usersdao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersdao.findByUsername(userName);
			//loyaltyCardsDao.revertDestCardStatus(cardNumber, user.getUserId());
			loyaltyCardsDaoForDML.revertDestCardStatus(cardNumber, user.getUserId());
		} catch (Exception e) {
			
		}
		
		
	}
	private LoyaltyTransferMembershipJsonResponse prepareTransferResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance, List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyTransferMembershipJsonResponse transferResponse = new LoyaltyTransferMembershipJsonResponse();
		transferResponse.setHeader(header);
		
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
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		transferResponse.setMembership(membershipResponse);
		transferResponse.setBalances(balances);
		transferResponse.setHoldBalance(holdBalance);
		transferResponse.setMatchedCustomers(matchedCustomers);
		transferResponse.setStatus(status);
		return transferResponse;
	}
	
	private void updateTransaction(LoyaltyTransactionParent trans, LoyaltyTransferMembershipJsonResponse responseObject, String userName) {
		
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			if(userName != null){
				trans.setUserName(userName);
			}
			if(responseObject == null) return;
			if(responseObject.getStatus() != null) {
				trans.setStatus(responseObject.getStatus().getStatus());
				trans.setErrorMessage(responseObject.getStatus().getMessage());
			}
			if(responseObject.getHeader() != null){
				trans.setRequestId(responseObject.getHeader().getRequestId());
				trans.setRequestDate(responseObject.getHeader().getRequestDate());
			}
			if(responseObject.getMembership() != null) {
				if(responseObject.getMembership().getCardNumber() != null && !responseObject.getMembership().getCardNumber().trim().isEmpty()){
					trans.setMembershipNumber(responseObject.getMembership().getCardNumber());
				}
				else{
					trans.setMembershipNumber(responseObject.getMembership().getPhoneNumber());
				}
			}
			if(responseObject.getMatchedCustomers() != null && responseObject.getMatchedCustomers().size() >= 1 && responseObject.getMatchedCustomers().get(0) != null) {
				trans.setMobilePhone(responseObject.getMatchedCustomers().get(0).getPhone());
			}
			//parentDao.saveOrUpdate(trans);
			parentDaoForDML.saveOrUpdate(trans);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
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
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);
			
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
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
	
	private LoyaltyTransaction findRequestByCardAndReqId(String requestId, String cardNumber) throws Exception {
		LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		return loyaltyTransactionDao.findRequestByCardAndReqId(cardNumber, requestId, OCConstants.LOYALTY_TRANS_TYPE_TRANSFER, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
	}
	
	public LoyaltyTransaction logTransactionRequest(LoyaltyTransferMembershipJsonRequest requestObject, String jsonRequest, String mode){
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
			transaction.setCardNumber(requestObject.getMembership().getCardNumber().trim());
			transaction.setType(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER);
			transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
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
	
	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyTransferMembershipJsonResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(response.getStatus().getStatus());
			transaction.setJsonResponse(responseJson);
			
			
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	
	private ContactsLoyaltyStage findDuplicateRequest(LoyaltyTransferMembershipJsonRequest requestObject) {
		//find the request in stage
		ContactsLoyaltyStage loyaltyStage = null;
		try{
			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			String userName = requestObject.getUser().getUserName()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUser().getOrganizationId();  
			ContactsLoyaltyStage requestStage = contactsLoyaltyStageDao.findTransferRequest(requestObject.getTransferSource().getCardNumber(), 
					requestObject.getMembership().getCardNumber(), userName, OCConstants.LOYALTY_SERVICE_TYPE_OC, OCConstants.LOYALTY_TRANS_TYPE_TRANSFER);
			if(requestStage != null){
				return loyaltyStage;
			}
			
		}catch(Exception e){
			logger.error("Exception in finding loyalty duplicate request...", e);
		}
		return loyaltyStage;
	}
	
	
	private ContactsLoyaltyStage saveRequestInStageTable(LoyaltyTransferMembershipJsonRequest requestObject){

		ContactsLoyaltyStage loyaltyStage = null;
		try{
			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
			String userName = requestObject.getUser().getUserName()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUser().getOrganizationId();  
			logger.info("saving request in stage table...");
			loyaltyStage = new ContactsLoyaltyStage();
			loyaltyStage.setCardNumber("source:"+requestObject.getTransferSource().getCardNumber()+"-dest:"+ 
					requestObject.getMembership().getCardNumber());
			loyaltyStage.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
			loyaltyStage.setReqType(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER);
			loyaltyStage.setStatus(Constants.LOYALTY_STAGE_PENDING);
			loyaltyStage.setUserName(userName);
			contactsLoyaltyStageDaoForDML.saveOrUpdate(loyaltyStage);
		}catch(Exception e){
			logger.error("Exception while saving loyalty request in stage table...", e);
		}
		return loyaltyStage;
	}

}
