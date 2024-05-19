package org.mq.optculture.business.mobileapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyMemberSessionID;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLogoutResponse;
import org.mq.optculture.model.mobileapp.Membership;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLoginRequest;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLoginResponse;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLogoutRequest;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLogoutResponse;
import org.mq.optculture.model.ocloyalty.AdditionalInfo;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.OTPRedeemLimit;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class OCLoyaltyMemberLogoutServiceImpl implements OCLoyaltyMemberLogoutService  {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		
		logger.info(" Login base oc service called ...");
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		Gson gson = new Gson();
		LoyaltyMemberLogoutResponse loyaltyMemberLogoutResponse = null;
		LoyaltyMemberLogoutRequest logoutRequest = null;
		BaseResponseObject responseObject = null;
		String responseJson = null;
		
		if(serviceRequest == null || !OCConstants.LOYALTY_SERVICE_ACTION_LOGOUT.equals(serviceRequest)){
			
			Status status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			loyaltyMemberLogoutResponse = new LoyaltyMemberLogoutResponse();
			loyaltyMemberLogoutResponse.setStatus(status);
			
			responseJson = gson.toJson(loyaltyMemberLogoutResponse);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}
		
		try{
			logoutRequest = gson.fromJson(requestJson, LoyaltyMemberLogoutRequest.class);
		}catch(Exception e){
			Status status = new Status("101001", ""+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			loyaltyMemberLogoutResponse = new LoyaltyMemberLogoutResponse();
			loyaltyMemberLogoutResponse.setStatus(status);
			responseJson = gson.toJson(loyaltyMemberLogoutResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid JSON ");
			return responseObject;
			
		}
		
		try{
			OCLoyaltyMemberLogoutService loyaltyMemberLoginService = (OCLoyaltyMemberLogoutService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_MEMBER_LOGOUT_BUSINESS_SERVICE);
			loyaltyMemberLogoutResponse = loyaltyMemberLoginService.processLogoutRequest(logoutRequest, baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate());
			responseJson = gson.toJson(loyaltyMemberLogoutResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
		}catch(Exception e){
			logger.error("Exception in loyalty inquiry base service.",e);
			throw new BaseServiceException("Server Error.");
		}
		return responseObject;
	}
	
	@Override
	public LoyaltyMemberLogoutResponse processLogoutRequest(LoyaltyMemberLogoutRequest loyaltyMemberLogoutRequest,
			String transactionId, String transactionDate) throws BaseServiceException {
		// TODO Auto-generated method stub
		
		
logger.info("processLoginRequest method called...");
		
		LoyaltyMemberLogoutResponse loyaltyMemberLogoutResponse = null;
		Status status = null;
		Users user = null;
		
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(loyaltyMemberLogoutRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(loyaltyMemberLogoutRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		responseHeader.setSourceType(loyaltyMemberLogoutRequest.getHeader().getSourceType() != null && 
				!loyaltyMemberLogoutRequest.getHeader().getSourceType().trim().isEmpty() ? loyaltyMemberLogoutRequest.getHeader().getSourceType().trim() : "");
		
		try{
			//validate mandatory fields userorgID, membershipnumber / phone number , password/OTP , incase OTP phone is must
			status = validateLogoutReqData(loyaltyMemberLogoutRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				loyaltyMemberLogoutResponse = prepareMemberLogoutResponse(responseHeader,  status);
				return loyaltyMemberLogoutResponse;
			}
			
			//validate user
			user = getUser(loyaltyMemberLogoutRequest.getUser().getUserName(), loyaltyMemberLogoutRequest.getUser().getOrganizationId());
			if(user == null){
				status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loyaltyMemberLogoutResponse = prepareMemberLogoutResponse(responseHeader,  status);
				return loyaltyMemberLogoutResponse;
			}
			if(!user.isEnabled()){
				status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loyaltyMemberLogoutResponse = prepareMemberLogoutResponse(responseHeader,  status);
				return loyaltyMemberLogoutResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loyaltyMemberLogoutResponse = prepareMemberLogoutResponse(responseHeader,  status);
				return loyaltyMemberLogoutResponse;
			}
			String sourceType = loyaltyMemberLogoutRequest.getHeader().getSourceType() != null ? loyaltyMemberLogoutRequest.getHeader().getSourceType().trim().toString() :"";
			//invalidate sessionID
			if(sourceType != null && !sourceType.isEmpty() && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) ){
				String sessionID = loyaltyMemberLogoutRequest.getUser().getSessionID();
				
				if(sessionID == null || sessionID.isEmpty()){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					loyaltyMemberLogoutResponse = prepareMemberLogoutResponse(responseHeader,  status);
					return loyaltyMemberLogoutResponse;
				}
				boolean invalidated = LoyaltyProgramHelper.invalidateSessionID(sessionID);
				if(!invalidated){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					loyaltyMemberLogoutResponse = prepareMemberLogoutResponse(responseHeader,  status);
					return loyaltyMemberLogoutResponse;
				}
				
				
			}
			
		
	}catch (Exception e) {
		// TODO: handle exception
		logger.error("exception ", e);
	}
		status = new Status("0", "Logout was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		loyaltyMemberLogoutResponse = prepareMemberLogoutResponse(responseHeader,  status);
		return loyaltyMemberLogoutResponse;
		
	}
	
private Users getUser(String userName, String orgId) throws Exception{
		
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findByUsername(completeUserName);
		return user;
	}	
	
	private Status validateLogoutReqData(LoyaltyMemberLogoutRequest logoutRequest) throws Exception{
		Status status = null;
		if(logoutRequest == null ){
			status = new Status(
					"101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		String reqSource = logoutRequest.getHeader().getSourceType();
		if(logoutRequest.getUser() == null){
			status = new Status(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		/*if(logoutRequest.getMembership() == null){
			status = new Status(
					"101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/
		
		if(reqSource!= null && reqSource.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)){
			
			if((logoutRequest.getUser().getUserName() == null || logoutRequest.getUser().getUserName().trim().isEmpty() ) &&
					(logoutRequest.getUser().getOrganizationId() == null || 
					logoutRequest.getUser().getOrganizationId().trim().length() <=0 ) ){
				status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
				
			}
			if(logoutRequest.getUser().getSessionID() == null || logoutRequest.getUser().getSessionID().trim().isEmpty()) {
				status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			/*Membership membership = logoutRequest.getMembership();
			
			if((membership.getEmailId() ==null || membership.getEmailId().isEmpty()) &&
					( membership.getPhoneNumber() == null || membership.getPhoneNumber().isEmpty()) ){
				status = new Status("800021", PropertyUtil.getErrorMessage(800021, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			if(membership.getDeviceId() == null || membership.getDeviceId().isEmpty() ){
				status = new Status("800023", PropertyUtil.getErrorMessage(800023, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
				
			}
			if((membership.getPassword() == null || membership.getPassword().trim().isEmpty()) && 
					(membership.getOTP() == null || membership.getOTP().trim().isEmpty()) ){
						
				status = new Status("800022", PropertyUtil.getErrorMessage(800022, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;	
			}*/
			//do we need a separate flag to specify pwd/otp based login???
			/*if( (membership.getOTP() != null && !membership.getOTP().trim().isEmpty()) && 
					(membership.getPhoneNumber() == null || membership.getPhoneNumber().trim().isEmpty()) ) {
				
				status = new Status("800023", PropertyUtil.getErrorMessage(800023, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;	
			}*/
			
			
		}
		
		
		/*if(logoutRequest.getHeader().getStoreNumber() == null || logoutRequest.getHeader().getStoreNumber().length() <= 0){
			status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/
		//removed the validation on 14th-Jan
		/*if(logoutRequest.getAmount()!=null && logoutRequest.getAmount().getReceiptAmount()!= null && !logoutRequest.getAmount().getReceiptAmount().trim().isEmpty()//APP-1131
				&& Double.parseDouble(logoutRequest.getAmount().getReceiptAmount())<=0){
			status = new Status("111592", PropertyUtil.getErrorMessage(111592, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/
		
		return status;
	}
	
	private LoyaltyMemberLogoutResponse prepareMemberLogoutResponse(ResponseHeader header, Status status) throws BaseServiceException {
		LoyaltyMemberLogoutResponse LoyaltyMemberLogoutResponse = new LoyaltyMemberLogoutResponse();
		LoyaltyMemberLogoutResponse.setHeader(header);
	
		
		LoyaltyMemberLogoutResponse.setStatus(status);
		return LoyaltyMemberLogoutResponse;
	}
	

}

