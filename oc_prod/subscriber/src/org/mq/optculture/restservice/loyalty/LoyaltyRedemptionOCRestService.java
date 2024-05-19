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
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.AdditionalInfo;
import org.mq.optculture.model.ocloyalty.OTPRedeemLimit;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionResponse;
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
 * Loyalty Redemption(Points, USD) RESTFul Service Controller.
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyRedemptionOCRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/**
	 * Delegates request.
	 * Calls Loyalty Redemption business service to process request.
	 */
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("Started Loyalty Redemption Rest Service... at "+System.currentTimeMillis()+" for "+request.getRemoteHost()+" ");

		response.setContentType("application/json");
		LoyaltyTransactionParent tranParent = createNewTransaction(); 
		Date date = tranParent.getCreatedDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);
		
		LoyaltyRedemptionResponse redemptionResponse = null;
		LoyaltyRedemptionRequest redemptionRequest = null;
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
				redemptionRequest = gson.fromJson(requestJson, LoyaltyRedemptionRequest.class);
			}catch(Exception e){
				status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(redemptionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, redemptionResponse, null);
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			}
			
			if(redemptionRequest == null){
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(redemptionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, redemptionResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(redemptionRequest.getHeader() == null){
				status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(redemptionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, redemptionResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			if(redemptionRequest.getHeader().getRequestId() == null || redemptionRequest.getHeader().getRequestId().trim().isEmpty() ||
					redemptionRequest.getHeader().getRequestDate() == null || redemptionRequest.getHeader().getRequestDate().trim().isEmpty()){
				status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(redemptionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, redemptionResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(redemptionRequest.getMembership() == null){
				responseHeader.setRequestId(redemptionRequest.getHeader().getRequestId());
				responseHeader.setRequestDate(redemptionRequest.getHeader().getRequestDate());
				status = new Status("101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(redemptionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, redemptionResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			if(redemptionRequest.getUser() == null){
				responseHeader.setRequestId(redemptionRequest.getHeader().getRequestId());
				responseHeader.setRequestDate(redemptionRequest.getHeader().getRequestDate());
				status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(redemptionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, redemptionResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			if(redemptionRequest.getUser().getUserName() == null || redemptionRequest.getUser().getUserName().trim().length() <=0 || 
					redemptionRequest.getUser().getOrganizationId() == null || redemptionRequest.getUser().getOrganizationId().trim().length() <=0 || 
							redemptionRequest.getUser().getToken() == null || redemptionRequest.getUser().getToken().trim().length() <=0) {
				responseHeader.setRequestId(redemptionRequest.getHeader().getRequestId());
				responseHeader.setRequestDate(redemptionRequest.getHeader().getRequestDate());
				status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(redemptionResponse);//APP-1206
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				updateTransaction(tranParent, redemptionResponse, null);
				return null;
			}
			
			userName = redemptionRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					redemptionRequest.getUser().getOrganizationId();
			
			LoyaltyTransaction trans = findRequestByReqIdAndDocSid(redemptionRequest.getUser().getUserName() + "__" +
					redemptionRequest.getUser().getOrganizationId(), redemptionRequest.getHeader().getRequestId().trim(), 
					redemptionRequest.getHeader().getDocSID().trim());
			if(trans != null){
				responseHeader.setRequestId(redemptionRequest.getHeader().getRequestId());
				responseHeader.setRequestDate(redemptionRequest.getHeader().getRequestDate());
				status = new Status("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
				responseJson = gson.toJson(redemptionResponse);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				updateTransaction(tranParent, redemptionResponse, null);
				return null;
			}
			else{
				trans = logTransactionRequest(redemptionRequest, requestJson);
			}
			
			
			
			BaseRequestObject requestObject = new BaseRequestObject();
			requestObject.setJsonValue(requestJson);
			requestObject.setAction(OCConstants.LOYALTY_SERVICE_ACTION_REDEMPTION);
			requestObject.setTransactionId(""+tranParent.getTransactionId());
			requestObject.setTransactionDate(transDate);
			BaseService baseService = null;
			BaseResponseObject responseObject = null;
			baseService= ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_REDEMPTION_OC_BUSINESS_SERVICE);
			responseObject = baseService.processRequest(requestObject);
			redemptionResponse = gson.fromJson(responseObject.getJsonValue(), 
					LoyaltyRedemptionResponse.class);
			updateTransactionStatus(trans, responseObject.getJsonValue(), redemptionResponse,redemptionRequest);
			logger.info("JSON Response: = "+responseObject.getJsonValue());
			if(responseObject.getAction().equals(OCConstants.LOYALTY_SERVICE_ACTION_REDEMPTION)){
				String responseJson1 = responseObject.getJsonValue();
				responseJson1.replace(OCConstants.JSON_RESPONSE_IGNORED_MESSAGE, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson1);
				printWriter.flush();
				printWriter.close();
				updateTransaction(tranParent, redemptionResponse, userName);
			}
		}catch(Exception e){
			logger.error("Error in redemption restservice", e);
			ResponseHeader responseHeader = null;
			if(redemptionResponse == null){
				responseHeader = new ResponseHeader();
				responseHeader.setRequestDate("");
				responseHeader.setRequestId("");
				responseHeader.setTransactionDate(transDate);
				responseHeader.setTransactionId(""+tranParent.getTransactionId());
			}
			else{
				responseHeader = redemptionResponse.getHeader();
			}
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
			responseJson = gson.toJson(redemptionResponse);
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			updateTransaction(tranParent, redemptionResponse, userName);
			logger.info("Response = "+responseJson);
			return null;
		}
		logger.info("Completed Loyalty Redemption Rest Service... at "+System.currentTimeMillis()+" for "+request.getRemoteHost()+" ");
		return null;
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
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);
			
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}
	
	private LoyaltyRedemptionResponse prepareRedemptionResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance, AdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyRedemptionResponse redemptionResponse = new LoyaltyRedemptionResponse();
		redemptionResponse.setHeader(header);
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
			//OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
			/*otpRedeemLimit.setAmount("");
			otpRedeemLimit.setValueCode("");*/
			List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
			//otpRedeemLimitlist.add(otpRedeemLimit);
			additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
			additionalInfo.setPointsEquivalentCurrency("");
			additionalInfo.setTotalRedeemableCurrency("");
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		
		redemptionResponse.setMembership(membershipResponse);
		redemptionResponse.setBalances(balances);
		redemptionResponse.setHoldBalance(holdBalance);
		redemptionResponse.setAdditionalInfo(additionalInfo);
		redemptionResponse.setMatchedCustomers(matchedCustomers);
		redemptionResponse.setStatus(status);
		return redemptionResponse;
	}
	
	private void updateTransaction(LoyaltyTransactionParent trans, LoyaltyRedemptionResponse responseObject, String userName) {
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			if(userName != null){
				trans.setUserName(userName);
			}
			if(responseObject == null) return;
			if(responseObject.getStatus() != null) {
				trans.setStatus(responseObject.getStatus().getStatus());
				trans.setErrorMessage(responseObject.getStatus().getMessage().toString());
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
	
	private LoyaltyTransaction findRequestByReqIdAndDocSid(String userName, String requestId, String docSid) throws Exception {
		LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		return loyaltyTransactionDao.findRequestByReqIdAndDocSid(userName, requestId, docSid, OCConstants.LOYALTY_TRANSACTION_REDEMPTION, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
	}
	
	private LoyaltyTransaction logTransactionRequest(LoyaltyRedemptionRequest requestObject, String jsonRequest){
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
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_REDEMPTION);
			transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
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
	
	private void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyRedemptionResponse response,
			LoyaltyRedemptionRequest request){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			if(response.getStatus().getStatus().equals((OCConstants.JSON_RESPONSE_IGNORED_MESSAGE))) {
				transaction.setStatus(OCConstants.JSON_RESPONSE_IGNORED_MESSAGE);
			}
			else {
				transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			}
			transaction.setRequestStatus(response.getStatus().getStatus());
			Users user= getUser(request.getUser().getUserName(), request.getUser().getOrganizationId(),
					 request.getUser().getToken());
			if(Utility.countryCurrencyMap.get(user.getCountryType()) != null) {
				 
				 responseJson = responseJson.replace(Utility.countryCurrencyMap.get(user.getCountryType()), "");
			 }
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
	private Users getUser(String userName, String orgId, String userToken) throws Exception{
		
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findByToken(completeUserName, userToken);
		return user;
	}
}
