package org.mq.optculture.business.account;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.mobileapp.OCLoyaltyMemberLoginService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.account.OCUser;
import org.mq.optculture.model.account.OCUserLoginRequest;
import org.mq.optculture.model.account.OCUserLoginResponse;
import org.mq.optculture.model.loyalty.StatusInfo;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLoginRequest;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLoginResponse;
import org.mq.optculture.model.mobileapp.Membership;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.google.gson.Gson;

public class OCUserLoginServiceImpl implements OCUserLoginService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {

		
		logger.info(" Login base oc service called ...");
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		Gson gson = new Gson();
		OCUserLoginResponse OCUserLoginResponse = null;
		OCUserLoginRequest loginRequest = null;
		BaseResponseObject responseObject = null;
		String responseJson = null;
		
		if(serviceRequest == null || !OCConstants.LOYALTY_SERVICE_ACTION_USER_LOGIN.equals(serviceRequest)){
			
			Status status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			OCUserLoginResponse = new OCUserLoginResponse();
			OCUserLoginResponse.setStatus(status);
			
			responseJson = gson.toJson(OCUserLoginResponse);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}
		
		try{
			loginRequest = gson.fromJson(requestJson, OCUserLoginRequest.class);
		}catch(Exception e){
			Status status = new Status("101001", ""+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			OCUserLoginResponse = new OCUserLoginResponse();
			OCUserLoginResponse.setStatus(status);
			responseJson = gson.toJson(OCUserLoginResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid JSON ");
			return responseObject;
			
		}
		
		try{
			OCUserLoginService OCUserLoginService = (OCUserLoginService) ServiceLocator.getInstance().getServiceByName(OCConstants.OC_USER_LOGIN_BUSINESS_SERVICE);
			OCUserLoginResponse = OCUserLoginService.processUserLoginRequest(loginRequest);
			responseJson = gson.toJson(OCUserLoginResponse);
			
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
	public OCUserLoginResponse processUserLoginRequest(OCUserLoginRequest OCUserLoginRequest) throws BaseServiceException {
		
		Status status = null;
		OCUserLoginResponse OCUserLoginResponse = null;
		try {
			Users user = null;
			
			
			status = validateLoginReqData(OCUserLoginRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				OCUserLoginResponse = prepareLoginResponse( status, null, null);
				return OCUserLoginResponse;
			}
			
			user = getUser(OCUserLoginRequest.getUser().getUserName(), OCUserLoginRequest.getUser().getOrganizationId());
			
			if(user == null){
				status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				OCUserLoginResponse = prepareLoginResponse(status, null,null);
				return OCUserLoginResponse;
			}
			if(user!=null){
				//validate the password
				OCUser loginUser = OCUserLoginRequest.getUser();
				if(loginUser.getPassword() != null && !loginUser.getPassword().isEmpty()){
					//OPOyXbL3plA=
					try {
						logger.debug("incoming =="+loginUser.getPassword()+" usr's=="+user.getPassword());
						//BCrypt.checkpw(plaintext, hashed)
						if(!LoyaltyProgramHelper.checkAuthentication(loginUser.getPassword(), user.getPassword() )){
							status = new Status("800027", PropertyUtil.getErrorMessage(800027, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							OCUserLoginResponse = prepareLoginResponse(status, null,null);
							return OCUserLoginResponse;
						}
					} catch (Exception e) {
						logger.error("Exception in bcrypt", e);
					}
					
				}
				
			}
			
			if(!user.isEnabled()){
				status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				OCUserLoginResponse = prepareLoginResponse(status, null,null);
				return OCUserLoginResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				OCUserLoginResponse = prepareLoginResponse(status, null,null);
				return OCUserLoginResponse;
			}
			status = new Status("0", "Login was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			OCUserLoginResponse = prepareLoginResponse(status, null,user.getUserOrganization().getOptSyncKey());
			return OCUserLoginResponse;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception while processing login request ", e);
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			OCUserLoginResponse = prepareLoginResponse(status, null,null);
			return OCUserLoginResponse;

		}
	}
	private Status validateLoginReqData(OCUserLoginRequest loginRequest) throws Exception{
		Status status = null;
		if(loginRequest == null ){
			status = new Status(
					"101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(loginRequest.getUser() == null || loginRequest.getUser().getUserName() == null || 
				loginRequest.getUser().getPassword() == null || loginRequest.getUser().getOrganizationId() == null || 
				loginRequest.getUser().getUserName().isEmpty() || loginRequest.getUser().getPassword().isEmpty() || 
				loginRequest.getUser().getOrganizationId().isEmpty()){
			
			status = new Status(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		return status;
		
		
		
	}
private Users getUser(String userName, String orgId) throws Exception{
		
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findByUsername(completeUserName);
		return user;
	}
private OCUserLoginResponse prepareLoginResponse( Status status, String sessionID, String APItoken) throws BaseServiceException {
		
		OCUserLoginResponse loginResponse = new OCUserLoginResponse();
		loginResponse.setStatus(status);
		loginResponse.setSessionID(sessionID);
		loginResponse.setAPIToken(APItoken);
		
		return loginResponse;
		
	}
		
}
