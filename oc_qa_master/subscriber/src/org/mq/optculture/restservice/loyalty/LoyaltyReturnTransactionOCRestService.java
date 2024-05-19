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
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.BalancesAdditionalInfo;
import org.mq.optculture.model.ocloyalty.Credit;
import org.mq.optculture.model.ocloyalty.Debit;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyReturnTransactionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyReturnTransactionResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyTransferMembershipJsonResponse;
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

public class LoyaltyReturnTransactionOCRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/**
	 * Delegates request.
	 * Calls Loyalty Return Transaction business service to process request.
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,HttpServletResponse response) throws Exception {
		logger.info(">>>Started Loyalty Return Transaction Rest Service.");

		response.setContentType("application/json");
		LoyaltyTransactionParent tranParent = createNewTransaction(); 
		Date date = tranParent.getCreatedDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);

		LoyaltyReturnTransactionResponse returnTransactionResponse = null;
		LoyaltyReturnTransactionRequest returnTransactionRequest = null;
		Status status = null;
		String responseJson = "";
		Gson gson = new Gson(); 
		String userName = null;

		try{
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			logger.info("JSON Request: = "+requestJson);
			ResponseHeader responseHeader = new ResponseHeader();
			responseHeader.setRequestDate("");
			responseHeader.setRequestId("");
			responseHeader.setTransactionDate(transDate);
			responseHeader.setTransactionId(""+tranParent.getTransactionId());

			try{
				returnTransactionRequest = gson.fromJson(requestJson, LoyaltyReturnTransactionRequest.class);
			}catch(Exception e){
				status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(returnTransactionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, returnTransactionResponse, null);
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			}

			if(returnTransactionRequest == null){
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(returnTransactionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				updateTransaction(tranParent, returnTransactionResponse, null);
				return null;
			}

			if(returnTransactionRequest.getHeader() == null){
				status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(returnTransactionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				updateTransaction(tranParent, returnTransactionResponse, null);
				return null;
			}

			if(returnTransactionRequest.getHeader().getRequestId() == null || returnTransactionRequest.getHeader().getRequestId().trim().isEmpty() ||
					returnTransactionRequest.getHeader().getRequestDate() == null || returnTransactionRequest.getHeader().getRequestDate().trim().isEmpty() || 
					returnTransactionRequest.getHeader().getDocSID() == null || returnTransactionRequest.getHeader().getDocSID().trim().isEmpty()){
				status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(returnTransactionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, returnTransactionResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}

			
			if(returnTransactionRequest.getMembership() == null){
				responseHeader.setRequestId(returnTransactionRequest.getHeader().getRequestId());
				responseHeader.setRequestDate(returnTransactionRequest.getHeader().getRequestDate());
				status = new Status("101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(returnTransactionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				updateTransaction(tranParent, returnTransactionResponse, null);
				return null;
			}
			if(returnTransactionRequest.getUser() == null){
				responseHeader.setRequestId(returnTransactionRequest.getHeader().getRequestId());
				responseHeader.setRequestDate(returnTransactionRequest.getHeader().getRequestDate());
				status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(returnTransactionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				updateTransaction(tranParent, returnTransactionResponse, null);
				return null;
			}

			if(returnTransactionRequest.getUser().getUserName() == null || returnTransactionRequest.getUser().getUserName().trim().length() <=0 || 
					returnTransactionRequest.getUser().getOrganizationId() == null || returnTransactionRequest.getUser().getOrganizationId().trim().length() <=0 || 
					returnTransactionRequest.getUser().getToken() == null || returnTransactionRequest.getUser().getToken().trim().length() <=0) {
				responseHeader.setRequestId(returnTransactionRequest.getHeader().getRequestId());
				responseHeader.setRequestDate(returnTransactionRequest.getHeader().getRequestDate());
				status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(returnTransactionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				updateTransaction(tranParent, returnTransactionResponse, null);
				return null;
			}

			userName = returnTransactionRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					returnTransactionRequest.getUser().getOrganizationId();

			
			String userDetails = returnTransactionRequest.getUser().getUserName()+"__"+returnTransactionRequest.getUser().getOrganizationId();
			String pcFlag = returnTransactionRequest.getHeader().getPcFlag();
			String requestId = returnTransactionRequest.getHeader().getRequestId();
			LoyaltyTransaction transaction = null;
			
			
			if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
				transaction = findRequest(requestId, userDetails);
				if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
					responseJson = transaction.getJsonResponse();
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					returnTransactionResponse = gson.fromJson(responseJson, LoyaltyReturnTransactionResponse.class);
					updateTransaction(tranParent, returnTransactionResponse, userName);
					logger.info("Response = "+responseJson);
					return null;
				}
			}	
			String docsid = returnTransactionRequest.getHeader().getDocSID();
			String userFullName = returnTransactionRequest.getUser().getUserName()+Constants.USER_AND_ORG_SEPARATOR+returnTransactionRequest.getUser().getOrganizationId();
			//if DOCSID not there then split the request id(the second token is meant for DOCSID).This is given for v8 plugin
			boolean isInFormat = (requestId != null && !requestId.isEmpty() && requestId.split(OCConstants.TOKEN_UNDERSCORE).length==3);
			
			String CustSID = returnTransactionRequest.getCustomer().getCustomerId();
			
			boolean isCustSID = (isInFormat && CustSID != null && !CustSID.trim().isEmpty() && CustSID.equalsIgnoreCase(requestId.split(OCConstants.TOKEN_UNDERSCORE)[1]));
			
			if(docsid == null ||( docsid != null && docsid.trim().isEmpty()) && isInFormat && !isCustSID) {
				String POSVersion = PropertyUtil.getPOSVersion(userFullName);
				if(POSVersion != null && !POSVersion.equalsIgnoreCase(OCConstants.POSVERSION_V8)){
					docsid = requestId.split(OCConstants.TOKEN_UNDERSCORE)[1];
					
				}
				
			}
			LoyaltyTransaction trans = null;
			String amountType = returnTransactionRequest.getAmount().getType();
			if(amountType !=null && !amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY))  {
				
				trans = findDuplicateRequest(docsid, userDetails);
				
				if(trans != null){
					responseHeader.setRequestId(returnTransactionRequest.getHeader().getRequestId());
					responseHeader.setRequestDate(returnTransactionRequest.getHeader().getRequestDate());
					status = new Status("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
					responseJson = gson.toJson(returnTransactionResponse);
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					updateTransaction(tranParent, returnTransactionResponse, null);
					return null;
				}
				else {
					trans = logTransactionRequest(returnTransactionRequest, requestJson, amountType);
				}
			}else{
				trans  = logTransactionRequest(returnTransactionRequest, requestJson, amountType);
				
			}
			
			
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_RETURN);
			requestObject.setTransactionId(""+tranParent.getTransactionId());
			requestObject.setTransactionDate(transDate);
			BaseService baseService = null;
			BaseResponseObject responseObject = null;
			baseService= ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_RETURN_TRANSACTION_OC_BUSINESS_SERVICE);
			responseObject = baseService.processRequest(requestObject);
			
			
			returnTransactionResponse = gson.fromJson(responseObject.getJsonValue(), LoyaltyReturnTransactionResponse.class);
			if(returnTransactionResponse != null){
				updateTransactionStatus(trans, responseObject.getJsonValue(), returnTransactionResponse);
			}

			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_RETURN)){
				String responseJson1 = responseObject.getJsonValue();
				responseJson1.replace(OCConstants.JSON_RESPONSE_IGNORED_MESSAGE, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				logger.info("Response json = "+responseJson1);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson1);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, returnTransactionResponse, userName);
			}
		}catch(Exception e){
			ResponseHeader responseHeader = null;
			if(returnTransactionResponse == null){
				responseHeader = new ResponseHeader();
				responseHeader.setRequestDate("");
				responseHeader.setRequestId("");
				responseHeader.setTransactionDate(transDate);
				responseHeader.setTransactionId(""+tranParent.getTransactionId());
			}
			else{
				responseHeader = returnTransactionResponse.getHeader();
			}
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			returnTransactionResponse = prepareReturnTransactionResponse(responseHeader, null, null, null, null, null, status);
			responseJson = gson.toJson(returnTransactionResponse);
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = "+responseJson, e);
			updateTransaction(tranParent, returnTransactionResponse, userName);
			return null;
		}finally{
			logger.info("Response = "+responseJson);
			logger.info("Completed Loyalty Return Transaction Rest Service.");
		}
		return null;
	}	

	private LoyaltyTransaction findRequest(String requestId, String userDetails) {
		LoyaltyTransaction loyaltyTransaction = null;
		try {
			LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransaction =  loyaltyTransactionDao.findRequestBy(requestId, userDetails, OCConstants.LOYALTY_TRANSACTION_RETURN, OCConstants.LOYALTY_SERVICE_TYPE_OC);
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return loyaltyTransaction;
	}

	
	private LoyaltyTransaction findDuplicateRequest(String docSID, String userDetails) {
		LoyaltyTransaction loyaltyTransaction = null;
		try {
			LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransaction =  loyaltyTransactionDao.findDuplicateRequestBy(docSID, userDetails, OCConstants.LOYALTY_TRANSACTION_RETURN, OCConstants.LOYALTY_SERVICE_TYPE_OC);
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return loyaltyTransaction;
	}

	
	private void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyReturnTransactionResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_DAO);
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_DAO_FOR_DML);
			
			if(response.getStatus().getStatus().equals((OCConstants.JSON_RESPONSE_IGNORED_MESSAGE))) {
				transaction.setStatus(OCConstants.JSON_RESPONSE_IGNORED_MESSAGE);
			}
			else {
				transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			}
			//transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(response.getStatus().getStatus());
			responseJson = responseJson.replace("\n", "");
			transaction.setJsonResponse(responseJson);
			if(response.getMembership() != null && response.getMembership().getCardNumber() != null 
					&& !response.getMembership().getCardNumber().trim().isEmpty()){
				transaction.setCardNumber(response.getMembership().getCardNumber());
			}
			else{
				transaction.setCardNumber(response.getMembership() == null ? "" : response.getMembership().getPhoneNumber());
			}
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}

	private LoyaltyReturnTransactionResponse prepareReturnTransactionResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance,BalancesAdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyReturnTransactionResponse returnTransactionResponse = new LoyaltyReturnTransactionResponse();
		returnTransactionResponse.setHeader(header);
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
		return returnTransactionResponse;
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
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_RETURN);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);
		}catch(Exception e){
			logger.error("Exception while creating new transaction...", e);
		}
		return tranx;
	}

	private void updateTransaction(LoyaltyTransactionParent trans, LoyaltyReturnTransactionResponse responseObject, String userName) {
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			if(userName != null){
				trans.setUserName(userName);
			}
			if(responseObject == null){
				return;
			}
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
			if(responseObject.getMatchedCustomers() != null && responseObject.getMatchedCustomers().size() >=1 
					&& responseObject.getMatchedCustomers().get(0) != null) {
				trans.setMobilePhone(responseObject.getMatchedCustomers().get(0).getPhone());
			}
			//parentDao.saveOrUpdate(trans);
			parentDaoForDML.saveOrUpdate(trans);

		}catch(Exception e){
			logger.error("Exception while creating new transaction...", e);
		}
	}
	
	private LoyaltyTransaction logTransactionRequest(LoyaltyReturnTransactionRequest requestObject, String jsonRequest, String amountType){
		
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
			transaction.setMode("online");//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_RETURN);
			if(amountType != null && amountType.equalsIgnoreCase(OCConstants.LOYALTY_RETURN_REQUEST_TYPE_INQUIRY)){
				transaction.setType(OCConstants.LOYALTY_TRANSACTION_RETURN_INQUIRY);
			}
			transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
			transaction.setDocSID(requestObject.getHeader().getDocSID().trim());
			transaction.setStoreNumber(requestObject.getHeader().getStoreNumber().trim());
			transaction.setEmployeeId(requestObject.getHeader().getEmployeeId() != null && 
					!requestObject.getHeader().getEmployeeId().trim().isEmpty() ? requestObject.getHeader().getEmployeeId().trim() : null);
			transaction.setTerminalId(requestObject.getHeader().getTerminalId() != null &&
					!requestObject.getHeader().getTerminalId().trim().isEmpty() ?  requestObject.getHeader().getTerminalId().trim() : null);
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}


}
