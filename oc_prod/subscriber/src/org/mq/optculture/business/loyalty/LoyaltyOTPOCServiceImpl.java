package org.mq.optculture.business.loyalty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyProgram;

import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.OTPGeneratedCodes;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.ExternalSMTPDigiReceiptSender;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;

import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UserSMSSenderIdDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyProgramDao;

import org.mq.optculture.data.dao.OTPGeneratedCodesDao;
import org.mq.optculture.data.dao.OTPGeneratedCodesDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLoginRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyOTPRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyOTPResponse;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.model.updatecontacts.MobileAppPreferences;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

/**
 * === OptCulture Loyalty Program ===
 * 
 * Loyalty OTP service.
 * Input: otp request JSON 
 * Output: opt response JSON with status
 * 
 * @author Venkata Rathnam D
 *  
 */ 
public class LoyaltyOTPOCServiceImpl implements LoyaltyOTPOCService{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		
		logger.info("Started processRequest...");
		
		BaseResponseObject responseObject = null;
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		String responseJson = null;
		Gson gson = new Gson();
		LoyaltyOTPResponse otpResponse = null;
		LoyaltyOTPRequest otpRequest = null;
		
	
		if(serviceRequest == null || !serviceRequest.equals(OCConstants.LOYALTY_SERVICE_ACTION_OTP)){
			logger.info("servicerequest is null...");
			
			Status status = new Status("101001", ""+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			otpResponse = new LoyaltyOTPResponse();
			otpResponse.setStatus(status);
			responseJson = gson.toJson(otpResponse);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid service");
			return responseObject;
		}
		
		try{
			otpRequest = gson.fromJson(requestJson, LoyaltyOTPRequest.class);
		}catch(Exception e){
			
			Status status = new Status("101001", ""+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			otpResponse = new LoyaltyOTPResponse();
			otpResponse.setStatus(status);
			responseJson = gson.toJson(otpResponse);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid JSON ");
			return responseObject;
		}
		
		try{
			LoyaltyOTPOCService loyaltyotpService = (LoyaltyOTPOCService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_OTP_OC_BUSINESS_SERVICE);
			otpResponse = loyaltyotpService.processOTPRequest(otpRequest, OCConstants.LOYALTY_ONLINE_MODE, baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate());
			responseJson = gson.toJson(otpResponse);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
		}catch(Exception e){
			logger.error("Exception in loyalty enroll base service.",e);
			throw new BaseServiceException("Server Error.");
		}
		logger.info("Completed processing baserequest... ");
		return responseObject;
		
	}
	
	@Override
	public LoyaltyOTPResponse processOTPRequest(LoyaltyOTPRequest otpRequest,
			String mode, String transactionId, String transactionDate)
			throws BaseServiceException {
		logger.info("Started processing otp request...");
		LoyaltyOTPResponse otpResponse = null;
		Status status = null;
		Users user = null;
		
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(otpRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(otpRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		responseHeader.setSourceType(otpRequest.getHeader().getSourceType() != null && 
				!otpRequest.getHeader().getSourceType().trim().isEmpty() ? otpRequest.getHeader().getSourceType().trim() : "");
		try{
			
			status = validateOTPJsonData(otpRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
				return otpResponse;
			}
		
			user = getUser(otpRequest.getUser().getUserName(), otpRequest.getUser().getOrganizationId(),
					otpRequest.getUser().getToken());
			if(user == null){
				status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
				return otpResponse;
			}
			if(!user.isEnabled()){
				status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
				return otpResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
				return otpResponse;
			}
		    
			/*String phone = otpRequest.getCustomer().getPhone();
			String email= otpRequest.getCustomer().getEmailAddress();
			
			String mobile = otpRequest.getCustomer().getPhone();
			String emailID = otpRequest.getCustomer().getEmailAddress();
			
		
			
			List<ContactsLoyalty>enrollList = findEnrollListByEmailORPhone(emailID, phone, user.getUserId());
		
			logger.info("EnrollList is "+enrollList);
			Long programID = enrollList != null && !enrollList.isEmpty() ?  enrollList.get(0).getProgramId() : null;
		*/	
			//for mobile app & on reset-password

			String phone = otpRequest.getCustomer().getPhone();
			String email= otpRequest.getCustomer().getEmailAddress();
			
			boolean isEmailIDExists = (email != null && email.trim().length() != 0);
			boolean isEmailIdValid = Utility.validateEmail(email);
			boolean isMobileExists = phone != null && phone.trim().length() != 0;
			boolean isMobileValid = isMobileExists && Utility.phoneParse(phone, user.getUserOrganization()) != null;

			phone = !isMobileExists || !isMobileValid ? null : phone;
			 email = !isEmailIDExists || !isEmailIdValid ? null : email;
			
			 boolean isMobile=isMobileValid;
			 boolean isEmail=isEmailIdValid;
			
			
			
			Long mblNum = null;
			/*phone = phone!=null && !phone.isEmpty() ? phone.trim() : Constants.STRING_NILL;
			phone = Utility.phoneParse(phone, user!=null ? user.getUserOrganization() : null );*/
			/*if(phone != null &&phone.length()>0) {
				if(user.getUserOrganization().isRequireMobileValidation()){
				if(!phone.startsWith(user.getCountryCarrier().toString()) && 
						(phone.length() >= user.getUserOrganization().getMinNumberOfDigits()
						&& phone.length() <= user.getUserOrganization().getMaxNumberOfDigits())) {
					//phone = user.getCountryCarrier().toString()+phone;
					isMobile=true;
				}
				else isMobile=false;
				}
				try{
					mblNum = Long.parseLong(phone);
					isMobile=true;
				}catch (Exception e) {
					isMobile=false;
				}
			}
			if(email!=null && email.length()>0){
				isEmail=Utility.validateEmail(email);
			  }*/
			  if(!isMobileValid && !isEmailIdValid){
				status = new Status("111552", PropertyUtil.getErrorMessage(111552, OCConstants.ERROR_LOYALTY_FLAG)+" "+otpRequest.getCustomer().getPhone().trim()+" and email#:"+otpRequest.getCustomer().getEmailAddress().trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
				return otpResponse;
			    }
			String sourceType = otpRequest.getHeader().getSourceType();
			String issueOnAction = otpRequest.getIssueOnAction();
			ContactsLoyalty contactsLoyalty = null;
			Contacts contacts =null;
			
			if( sourceType != null && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) && 
					issueOnAction != null  && (issueOnAction.equals("resetPassword") || 
							issueOnAction.equals("login"))){
				
			if( (email != null && !email.trim().isEmpty()) || 
					(phone != null && !phone.trim().isEmpty()) ){
					
					//List<ContactsLoyalty> enrollList= new ArrayList<ContactsLoyalty>();
					List<ContactsLoyalty>enrollList = findEnrollListByEmailORPhone(email, phone, user);
				
					if(enrollList == null){
						if((email != null && !email.trim().isEmpty())){
							
							status = new Status("800024", PropertyUtil.getErrorMessage(800024, OCConstants.ERROR_MOBILEAPP_FLAG)+email.trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						}
						else if((phone != null && !phone.trim().isEmpty())) {
							status = new Status("800025", PropertyUtil.getErrorMessage(800025, OCConstants.ERROR_MOBILEAPP_FLAG)+phone.trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						}
						otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
						return otpResponse;
					}
					
					else if(enrollList.size() > 1){
						if((email != null && !email.trim().isEmpty())){
							
							status = new Status("800026", PropertyUtil.getErrorMessage(800026, OCConstants.ERROR_MOBILEAPP_FLAG)+"Email:"+email.trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						}
						else if((phone != null && !phone.trim().isEmpty())) {
							status = new Status("800026", PropertyUtil.getErrorMessage(800026, OCConstants.ERROR_MOBILEAPP_FLAG)+"Phone#:"+phone.trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						}
						List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList, otpRequest);
						otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
						return otpResponse;
					
					}
					else {
						
						
						contactsLoyalty = enrollList.get(0);
					}
					/*else {
						
						
						contactsLoyalty = enrollList.get(0);
					}
					
					String sessionID = LoyaltyProgramHelper.generateSessionID(Utility.getOnlyUserName(user.getUserName()), 
							Utility.getOnlyOrgId(user.getUserName()), otpRequest.getCustomer().getDeviceID(), contactsLoyalty.getCardNumber(), user);
					if(sessionID == null){
						
						logger.debug("===something wrong in generating sessionID====");
					}
					otpResponse.setSessionID(sessionID);	
				}
				return otpResponse;*/
			}
		  	

			}
			
			
			  
			  
			//APP-1976
			
			if(otpRequest.getHeader().getRequestType() != null && 
					!otpRequest.getHeader().getRequestType().isEmpty() && 
					(otpRequest.getHeader().getRequestType().equalsIgnoreCase(OCConstants.OTP_REQUSET_TYPE_ACKNOWLEDGE)
					|| otpRequest.getHeader().getRequestType().equalsIgnoreCase(OCConstants.OTP_GENERATED_CODE_STATUS_EXPIRED))) {//APP-2458
				
				//APP-3228
				OTPGeneratedCodes otpgeneratedcode = findOTPCodeByPhone(phone, user.getUserId(), OCConstants.OTP_GENERATED_CODE_STATUS_ACTIVE,email,null);
				
				if(otpRequest.getOtpCode()== null || otpRequest.getOtpCode().isEmpty()) {
					status = new Status("111601", PropertyUtil.getErrorMessage(111601, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
					return otpResponse;	
				}
				//OTP details were not found.
				OTPGeneratedCodes otpgeneratedcodeForOTP = findOTPCodeByPhone(phone, user.getUserId(), OCConstants.OTP_GENERATED_CODE_STATUS_ACTIVE,email,otpRequest.getOtpCode());	
				if(otpgeneratedcodeForOTP == null ) {
					status = new Status("111602", PropertyUtil.getErrorMessage(111602, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
					return otpResponse;
					
				}
				
				
				Calendar currCal = Calendar.getInstance();
				Calendar createdDate = otpgeneratedcode.getCreatedDate();
				long currtime = currCal.getTimeInMillis()/1000;
				long createdtime = createdDate.getTimeInMillis()/1000;
				long timeDiff = currtime - createdtime;
				long duration = 15*60; //all like this has to be changed redemption, login api, enroll api - update contact for password rest check al possible palces
				if(timeDiff > duration) {
					otpgeneratedcode.setStatus(OCConstants.OTP_GENERATED_CODE_STATUS_EXPIRED);
					saveOTPgeneratedcode(otpgeneratedcode);
					status = new Status("111617", PropertyUtil.getErrorMessage(111617, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					otpResponse = prepareOTPResponse(responseHeader, status,otpgeneratedcode.getOtpCode());
					return otpResponse;				
				}
				

				//OTP was not empty with Acknowledged state.
				boolean requestTypeExpired = otpRequest.getHeader().getRequestType().equalsIgnoreCase(OCConstants.OTP_GENERATED_CODE_STATUS_EXPIRED); 

				
				if(otpRequest.getOtpCode()== null || otpRequest.getOtpCode().isEmpty()) {
					status = new Status("111601", PropertyUtil.getErrorMessage(111601, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
					return otpResponse;	
				}
				//OTP details were not found.
				 otpgeneratedcodeForOTP = findOTPCodeByPhone(phone, user.getUserId(), OCConstants.OTP_GENERATED_CODE_STATUS_ACTIVE,email,otpRequest.getOtpCode());	
				if(otpgeneratedcodeForOTP == null ) {
					status = new Status("111602", PropertyUtil.getErrorMessage(111602, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
					return otpResponse;
					
				}
				 
				
				//OTP was found and marked used.
					otpgeneratedcodeForOTP.setDocsid(otpRequest.getHeader().getDocSID());
					otpgeneratedcodeForOTP.setStatus(requestTypeExpired?OCConstants.OTP_GENERATED_CODE_STATUS_EXPIRED:OCConstants.OTP_GENERATED_CODE_STATUS_USED);//APP-2458
					saveOTPgeneratedcode(otpgeneratedcodeForOTP);
					
					status = requestTypeExpired ? new Status("0", "OTP code got expired", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE) : new Status("0", "OTP was found and marked used.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
					
					
					if( sourceType != null && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) && 
							issueOnAction != null  && issueOnAction.equals("resetPassword")){
						if(otpRequest.getCustomer().getDeviceID() == null || otpRequest.getCustomer().getDeviceID().isEmpty()){
							
							status = new Status("800031", PropertyUtil.getErrorMessage(800031, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
							return otpResponse;
						}
					//	String mobile = otpRequest.getCustomer().getPhone();
					//	String emailID = otpRequest.getCustomer().getEmailAddress();
						
/*						
						if( (email != null && !email.trim().isEmpty()) || (phone != null && !phone.trim().isEmpty()) ){
							
							ContactsLoyalty contactsLoyalty = null;
							Contacts contacts =null;
							//List<ContactsLoyalty> enrollList= new ArrayList<ContactsLoyalty>();
								enrollList = findEnrollListByEmailORPhone(emailID, phone, user.getUserId());
							logger.info("replaced list 2");
							
			
							if(enrollList == null){
								if((emailID != null && !emailID.trim().isEmpty())){
									
									status = new Status("800024", PropertyUtil.getErrorMessage(800024, OCConstants.ERROR_MOBILEAPP_FLAG)+emailID.trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								}
								else if((phone != null && !phone.trim().isEmpty())) {
									status = new Status("800025", PropertyUtil.getErrorMessage(800025, OCConstants.ERROR_MOBILEAPP_FLAG)+phone.trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								}
								otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
								return otpResponse;
							}
							
							else if(enrollList.size() > 1){
								if((emailID != null && !emailID.trim().isEmpty())){
									
									status = new Status("800026", PropertyUtil.getErrorMessage(800026, OCConstants.ERROR_MOBILEAPP_FLAG)+"Email:"+emailID.trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								}
								else if((phone != null && !phone.trim().isEmpty())) {
									status = new Status("800026", PropertyUtil.getErrorMessage(800026, OCConstants.ERROR_MOBILEAPP_FLAG)+"Phone#:"+phone.trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								}
								List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList, otpRequest);
								otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
								return otpResponse;
							
							}
							else {
								
								
								contactsLoyalty = enrollList.get(0);
								
								logger.debug("===Enteringg into else====");

							}
						

								
						}*/
						String sessionID = LoyaltyProgramHelper.generateSessionID(Utility.getOnlyUserName(user.getUserName()), 
								Utility.getOnlyOrgId(user.getUserName()), otpRequest.getCustomer().getDeviceID(), contactsLoyalty.getCardNumber(), user);
						if(sessionID == null){
							
							logger.debug("===something wrong in generating sessionID====");
						}
						otpResponse.setSessionID(sessionID);
					}
				  	
					return otpResponse;

				}
			
			
			else if(otpRequest.getHeader().getRequestType() != null && 
					!otpRequest.getHeader().getRequestType().isEmpty() && 
					(otpRequest.getHeader().getRequestType().equalsIgnoreCase(OCConstants.OTP_REQUSET_TYPE_VALIDATE))){
					
				if(otpRequest.getOtpCode()== null || otpRequest.getOtpCode().isEmpty()) {
								status = new Status("111601", PropertyUtil.getErrorMessage(111601, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
								return otpResponse;	
					}	
			
				//OTP details were not found.where is the email 
				OTPGeneratedCodes otpgeneratedcodeForOTP = findOTPCodeByPhone(phone, user.getUserId(), OCConstants.OTP_GENERATED_CODE_STATUS_ACTIVE,email,otpRequest.getOtpCode());	
				if(otpgeneratedcodeForOTP == null ) {
					status = new Status("111602", PropertyUtil.getErrorMessage(111602, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
					return otpResponse;
					
				}
				if(otpgeneratedcodeForOTP != null){ //under validate its also imp that the given OTP is within expirity
					
					Calendar currCal = Calendar.getInstance();
					Calendar createdDate = otpgeneratedcodeForOTP.getCreatedDate();
					
					long currtime = currCal.getTimeInMillis()/1000;
					long createdtime = createdDate.getTimeInMillis()/1000;
					long timeDiff = currtime - createdtime;
					long duration = 15*60; //all like this has to be changed redemption, login api, enroll api - update contact for password rest check al possible palces
					boolean continueWithEmail = true;
					if(timeDiff > duration){
					otpgeneratedcodeForOTP.setStatus(OCConstants.OTP_GENERATED_CODE_STATUS_EXPIRED);
					saveOTPgeneratedcode(otpgeneratedcodeForOTP);
					status = new Status("111617", PropertyUtil.getErrorMessage(111617, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					otpResponse = prepareOTPResponse(responseHeader, status,otpgeneratedcodeForOTP.getOtpCode());
					return otpResponse;
					}
				}
				
				status =   new Status("0", "Given OTP is valid.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
				return otpResponse;
				
			}
		//it is reached mens only to issue - here we shudnt send any expirity - if the OTP is not used then expire the existing one and send a new.
			OTPGeneratedCodes otpgeneratedcode = findOTPCodeByPhone(phone, user.getUserId(), OCConstants.OTP_GENERATED_CODE_STATUS_ACTIVE,email,null);
			
			
			if(otpgeneratedcode != null){
				
				Calendar currCal = Calendar.getInstance();
				Calendar createdDate = otpgeneratedcode.getCreatedDate();
				
				long currtime = currCal.getTimeInMillis()/1000;
				long createdtime = createdDate.getTimeInMillis()/1000;
				long timeDiff = currtime - createdtime;
				long duration = 15*60; //all like this has to be changed redemption, login api, enroll api - update contact for password rest check al possible palces
				boolean continueWithEmail = true;
				if(timeDiff < duration){
					if(isMobile)
					status = sendOTPCode(user, phone, otpgeneratedcode.getOtpCode(),issueOnAction);
					if(status != null){
						if(isEmail){
							status=sendOTPCodeToEmail(user,email,otpgeneratedcode.getOtpCode());
							if(status != null){
								logger.info("Sending email failed..."+status.getMessage());
								status = new Status("111550", PropertyUtil.getErrorMessage(111550, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
								otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
								return otpResponse;
							 }
							continueWithEmail = false;
						}
					  }
					if(isEmail && continueWithEmail){
					status=sendOTPCodeToEmail(user,email,otpgeneratedcode.getOtpCode());
				    /* if(status != null){     //control comes here means tht mobile based otp is already sent shudnt send error message you shud combine error messages
						logger.info("Sending email failed..."+status.getMessage());
						otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
						return otpResponse;
					 }*/
					}
					otpgeneratedcode.setSentCount(otpgeneratedcode.getSentCount()+1);
					saveOTPgeneratedcode(otpgeneratedcode);
					status = new Status("0", PropertyUtil.getErrorMessage(111548, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					otpResponse = prepareOTPResponse(responseHeader, status,otpgeneratedcode.getOtpCode());
					return otpResponse;
				}
				else{
					logger.info(" otp code is expired and saved....");
					otpgeneratedcode.setStatus(OCConstants.OTP_GENERATED_CODE_STATUS_EXPIRED);
					saveOTPgeneratedcode(otpgeneratedcode);
					
					
					/*
					 * status = new Status("111617", PropertyUtil.getErrorMessage(111617,
					 * OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					 * otpResponse = prepareOTPResponse(responseHeader,
					 * status,otpgeneratedcode.getOtpCode()); return otpResponse;
					 */

				}
				
			}
			
			String activeOTPCode = generateOtpCode();
			
			otpgeneratedcode = new OTPGeneratedCodes();
			otpgeneratedcode.setCreatedDate(Calendar.getInstance());
			otpgeneratedcode.setOtpCode(activeOTPCode);
			if(isMobile)otpgeneratedcode.setPhoneNumber(phone);
			otpgeneratedcode.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_OTP);
			otpgeneratedcode.setStatus(OCConstants.OTP_GENERATED_CODE_STATUS_ACTIVE);
			otpgeneratedcode.setUserId(user.getUserId());
			otpgeneratedcode.setSentCount(1L);
			otpgeneratedcode.setDocsid(otpRequest.getHeader().getDocSID());
			

			if(isEmail)otpgeneratedcode.setEmail(email);
		
			boolean continueWithEmail = true;
			if(isMobile)
				status = sendOTPCode(user, phone, otpgeneratedcode.getOtpCode(),issueOnAction);
				if(status != null){
					if(isEmail){
						status=sendOTPCodeToEmail(user,email,otpgeneratedcode.getOtpCode());
						if(status != null){
							status = new Status("111550", PropertyUtil.getErrorMessage(111550, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
							otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
							return otpResponse;
						 }
						continueWithEmail = false;
					}
				  }
				if(isEmail && continueWithEmail){
				status=sendOTPCodeToEmail(user,email,otpgeneratedcode.getOtpCode());
				}
			saveOTPgeneratedcode(otpgeneratedcode);
			
			//status = new Status("0", PropertyUtil.getErrorMessage(111548, OCConstants.ERROR_LOYALTY_FLAG)+StringUtils.right(phone, 4), OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			status = new Status("0", PropertyUtil.getErrorMessage(111548, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			otpResponse = prepareOTPResponse(responseHeader, status,otpgeneratedcode.getOtpCode());
			return otpResponse;
			
		}catch(Exception e){
			logger.error("Exception occuring in loyalty otp service...", e);
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG)+" "+e.getMessage(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			otpResponse = prepareOTPResponse(responseHeader, status,Constants.STRING_NILL);
			return otpResponse;
		}
			
	}
		
	private Users getUser(String userName, String orgId, String userToken) throws Exception{
			
			String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.findUserByToken(completeUserName, userToken);
			return user;
	}
	private List<ContactsLoyalty> findEnrollListByEmailORPhone(String Email,String phone, Users user) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByEmailORPhone(Email, phone, user);
	
	}
	
	private LoyaltyAutoComm getLoyaltyAutoComm(Long programId) throws Exception {
		LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
		return autoCommDao.findById(programId);
	}
	
	
	
private List<MatchedCustomer> prepareMatchedCustomers(List<ContactsLoyalty> enrollList,LoyaltyOTPRequest loginRequest) throws Exception {
		
		Contacts contact = null;
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		List<MatchedCustomer> matchedCustList = new ArrayList<MatchedCustomer>();
		MatchedCustomer matchedCustomer = null;
		
		for(ContactsLoyalty loyalty : enrollList){
			if(loyalty.getContact() != null && loyalty.getContact().getContactId() != null){
				contact = contactsDao.findById(loyalty.getContact().getContactId());
				if(contact != null){
					matchedCustomer = new MatchedCustomer();
					if(loginRequest.getHeader().getSourceType() != null && 
							!loginRequest.getHeader().getSourceType().isEmpty() && 
							(loginRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
									loginRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){
						MobileAppPreferences mobileAppPreferences =new MobileAppPreferences(); 
						mobileAppPreferences.setLanguage(contact.getLanguage() == null ? "": contact.getLanguage().trim());
						mobileAppPreferences.setPushNotifications (contact.getPushNotification() == null ? "": contact.getPushNotification().toString());
					matchedCustomer.setMobileAppPreferences(mobileAppPreferences);
					matchedCustomer.setInstanceId(contact.getInstanceId() == null ? "": contact.getInstanceId().toString());//APP-1775
					}
					
						
					matchedCustomer.setMembershipNumber(""+loyalty.getCardNumber());
					matchedCustomer.setFirstName(contact.getFirstName() == null ? "" : contact.getFirstName().trim());
					matchedCustomer.setLastName(contact.getLastName() == null ? "" : contact.getLastName().trim());
					matchedCustomer.setCustomerId(contact.getExternalId() == null ? "" : contact.getExternalId());
					matchedCustomer.setPhone(contact.getMobilePhone() == null ? "" : contact.getMobilePhone());
					matchedCustomer.setEmailAddress(contact.getEmailId() == null ? "" : contact.getEmailId());
					matchedCustList.add(matchedCustomer);
				}
			}
		}
		
		return matchedCustList;
		
	}
	
	private LoyaltyOTPResponse prepareOTPResponse(ResponseHeader header, Status status,String otpCode) throws BaseServiceException {
			LoyaltyOTPResponse otpResponse = new LoyaltyOTPResponse();
			if(!header.getSourceType().equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP))otpResponse.setOtpCode(otpCode);
			otpResponse.setHeader(header);
			otpResponse.setStatus(status);
			
			return otpResponse;
	}
	
	private Status validateOTPJsonData(LoyaltyOTPRequest otpRequest) throws Exception{
		logger.info("Entered validate otp Json Data method >>>>");
		
		Status status = null;
		if(otpRequest == null ){
			status = new Status(
					"101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(otpRequest.getUser() == null){
			status = new Status(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(otpRequest.getCustomer() == null){
			status = new Status(
					"101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(otpRequest.getUser().getUserName() == null || otpRequest.getUser().getUserName().trim().length() <=0 || 
				otpRequest.getUser().getOrganizationId() == null || otpRequest.getUser().getOrganizationId().trim().length() <=0 || 
						otpRequest.getUser().getToken() == null || otpRequest.getUser().getToken().trim().length() <=0) {
			status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(otpRequest.getHeader().getStoreNumber() == null || otpRequest.getHeader().getStoreNumber().length() <= 0){
			status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(otpRequest.getHeader().getRequestType() != null && !otpRequest.getHeader().getRequestType().isEmpty()
				&& !otpRequest.getHeader().getRequestType().equalsIgnoreCase("Issue") && !otpRequest.getHeader().getRequestType().equalsIgnoreCase("Acknowledge")
				&& !otpRequest.getHeader().getRequestType().equalsIgnoreCase("Validate") 
				&& !otpRequest.getHeader().getRequestType().equalsIgnoreCase(OCConstants.OTP_GENERATED_CODE_STATUS_EXPIRED)){
			status = new Status("111600", PropertyUtil.getErrorMessage(111600, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		
		if((otpRequest.getCustomer().getPhone() == null || otpRequest.getCustomer().getPhone().trim().isEmpty())
				&& (otpRequest.getCustomer().getEmailAddress() == null || otpRequest.getCustomer().getEmailAddress().trim().isEmpty())){
			status = new Status("111552", PropertyUtil.getErrorMessage(111552, OCConstants.ERROR_LOYALTY_FLAG)+" "+otpRequest.getCustomer().getPhone().trim()+" and email#: "+otpRequest.getCustomer().getEmailAddress().trim()+".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		return status;
	}
	
	private OTPGeneratedCodes findOTPCodeByPhone(String phone, Long userId, String status,String email, String OTP) throws Exception {
		OTPGeneratedCodesDao otpGenCodesDao = (OTPGeneratedCodesDao)ServiceLocator.getInstance().getBeanByName(OCConstants.OTP_GENERATEDCODES_DAO);
		return otpGenCodesDao.findOTPCodeByPhone(phone, userId, status,email,OTP);
	}
	
	
	private void saveOTPgeneratedcode(OTPGeneratedCodes otpgenCode) throws Exception {
		OTPGeneratedCodesDao otpGenCodesDao = (OTPGeneratedCodesDao)ServiceLocator.getInstance().getBeanByName(OCConstants.OTP_GENERATEDCODES_DAO);
		OTPGeneratedCodesDaoForDML otpGenCodesDaoForDML = (OTPGeneratedCodesDaoForDML)ServiceLocator.getInstance().getBeanByName(OCConstants.OTP_GENERATEDCODES_DAO_FOR_DML);

		//otpGenCodesDao.saveOrUpdate(otpgenCode);
		otpGenCodesDaoForDML.saveOrUpdate(otpgenCode);
	}
	
	private String generateOtpCode() {
		
		final long MAX_NUMBER = 9999L;
		final long MIN_NUMBER = 1000L;
		String otpCode = "";
		String randNoStr = "";
		Long randNo = Long.valueOf(new Random().nextLong());
		
		if(randNo.toString().startsWith("-")) {
			randNoStr = randNo.toString().replace("-", "");
		}
		else {
			randNoStr = randNo.toString();
		}
		
		randNo = Long.valueOf((Long.parseLong(randNoStr) % (MAX_NUMBER-MIN_NUMBER)) + MIN_NUMBER);
		otpCode = randNo.toString();
		return otpCode;
	}
	

//	private Status sendOTPCode(Users user, String phone, String otpCode) throws Exception {
//		return sendOTPCode(user, phone, otpCode, "" );
//	}
//	
	private Status sendOTPCode(Users user, String phone, String otpCode, String issueOnAction ) throws Exception {
		

		
		Status status = null;
		
		try{
			//do not stop sending OTP for any mobile status
			/*ContactsDao contactdao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			Contacts contactobj=contactdao.findContactByPhone(phone, user.getUserId());
			if(contactobj!=null && !contactobj.getMobileStatus().equalsIgnoreCase(Constants.CON_MOBILE_STATUS_ACTIVE)) {
				status = new Status("111550", PropertyUtil.getErrorMessage(111550, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
*/			
			if(user.getUserOrganization().isRequireMobileValidation()){
				if(!phone.startsWith(user.getCountryCarrier().toString()) && 
						(phone.length() >= user.getUserOrganization().getMinNumberOfDigits()
						&& phone.length() <= user.getUserOrganization().getMaxNumberOfDigits())) {
					phone = user.getCountryCarrier().toString()+phone;
				}
				
			}
	
			if(!user.isEnableSMS()){
			status = new Status("111550", PropertyUtil.getErrorMessage(111550, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		     }
			
			OCSMSGateway ocGateway = GatewayRequestProcessHelper.getOcSMSGateway(user, 
					SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
			CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)ServiceLocator.getInstance().getBeanByName("captiwayToSMSApiGateway");
			if(!ocGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocGateway, 1)) {
				status = new Status("111551", PropertyUtil.getErrorMessage(111551, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			
			if(!(((user.getSmsCount() == null ? 0 : user.getSmsCount()) - (user.getUsedSmsCount() == null ? 0 : user.getUsedSmsCount())) >=  1)) {
				status = new Status("111551", PropertyUtil.getErrorMessage(111551, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			String senderId = ocGateway.getSenderId();
			UserSMSSenderIdDao userSMSSenderIdDao = (UserSMSSenderIdDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USER_SMS_SENDER_ID_DAO); 
			List<UserSMSSenderId> retList =  userSMSSenderIdDao.findSenderIdBySMSType(user.getUserId(), ocGateway.getAccountType());
			if(retList != null && !retList.isEmpty()) {
				senderId = retList.get(0).getSenderId();
			}
			String msgContent= "";
			String messageHeader = findMessageHeader(user);
			AutoSMS autoSms = null;
			
		

			AutoSMSDao autosmsdao=(AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			Long userid=user.getUserId();
			String Message="";
			
			logger.info("phone is "+phone);
			logger.info("entered into db flow");
			if(issueOnAction!=null && !issueOnAction.isEmpty()) {
			
				if(issueOnAction.equalsIgnoreCase("Redemption")){
				
				Message= PropertyUtil.getPropertyValueFromDB("RedemptionOTPSMSTemplates");

				}
			}
			
			if(Message.isEmpty()){
				
				Message = PropertyUtil.getPropertyValueFromDB("OTPSMSTemplate");

			}
			

			Message = Message.replace("[OTP]",otpCode);
			Message = Message.replace("[BrandName]",senderId);
	
			
			//Contacts contacts = contactsLoyalty.getContact();
			
			LoyaltyProgramService ltyprgrmservice=new LoyaltyProgramService();
			Long programId =null;
			if(issueOnAction!=null && !issueOnAction.isEmpty()) {

				if(issueOnAction.equalsIgnoreCase("Redemption")){
					programId =  ltyprgrmservice.getCustomizedOTPEnabledProgList(userid, true);//find the customized RedemptionOTP msg enabled program under user;

				}
			}	
			if(programId == null) {
				
				programId =  ltyprgrmservice.getCustomizedOTPEnabledProgList(userid, false);//find the customized OTP msg enabled program under user;
			}
			if(programId !=null) {
				LoyaltyAutoCommGenerator ltyautocom=new LoyaltyAutoCommGenerator();
				LoyaltyAutoCommDao loyaltyAutoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
				
				//LoyaltyAutoComm loyaltyAutoComm = loyaltyAutoCommDao.findById(contactsLoyalty.getProgramId());
				//LoyaltyAutoComm loyaltyAutoComm =new LoyaltyAutoComm();
				LoyaltyAutoComm autoComm=ltyprgrmservice.getAutoCommunicationObj(programId);
			
				
				logger.info("the msg "+autoComm);
				try {
				
				Long templateId=null;
				logger.info("issueOnAction value is :"+issueOnAction);
				if(issueOnAction!=null && !issueOnAction.isEmpty()) {

					if(issueOnAction.equalsIgnoreCase("Redemption")){
					 templateId=autoComm.getRedemptionOtpAutoSmsTmpltId();
					}
					
				}
				if(templateId == null) {
						
					templateId=autoComm.getOtpMessageAutoSmsTmpltId();
					
				}
				
				autoSms = autosmsdao.getAutoSmsTemplateById(templateId);

				
				logger.info("the templateId is "+templateId);

				if(templateId !=null) {
					
					
					logger.info("the templateId is "+templateId);
					logger.info("the autosms senderid is "+templateId);
					senderId=autoSms.getSenderId();
					Message= ltyautocom.sendOtpMessagesSMSTemplate(templateId, user,  phone, otpCode);
					Message = Message.replace("[OTP]",otpCode);
					Message=Message.replace("[Org Name]",user.getUserOrganization().getOrganizationName());
					
					
				}
				
				}catch(Exception e) {
					
					logger.info("Exception occured because of Autosms templtid is null "+e);

				}
			}

		
			
			logger.info("Final sms otp msg is"+Message);

			
			
			if(messageHeader.equals(Constants.STRING_NILL) ){
				msgContent= Message;
			}
			else{
				msgContent= messageHeader+ "\n" +Message;
			}
			
			logger.info("after adding header sms otp msg is"+msgContent);

			try {
				captiwayToSMSApiGateway.sendSingleSms(ocGateway, msgContent,
						phone, senderId,null);
				/**
				 * Update the Used SMS count
				 */
				try{
					//UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
					UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
					//usersDao.updateUsedSMSCount(user.getUserId(), 1);
					usersDaoForDML.updateUsedSMSCount(user.getUserId(), 1);
				}catch(Exception exception){
					logger.error("Exception while updating the Used SMS count",exception);
				}
				
				/**
				 * Update Sms Queue
				 */
				SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
				smsQueueHelper.updateSMSQueue(phone,msgContent,Constants.SMS_MSG_TYPE_OTPSMS, user, senderId);
			} catch (Exception e) {
				logger.error("Exception While sending OTP SMS ",e);
			}
			logger.debug(">>>>>>> Started LoyaltyOTPOCServiceImpl :: sendOTPCode <<<<<<< ");

			return null;
	
					}catch(Exception e){
			logger.error("Exception in sending otp code ...", e);
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG)+" "+e.getMessage(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		
			return status;
					}
		
	}
	
	// send opt to email
    
	private Status sendOTPCodeToEmail(Users user, String email, String otpCode) {
		
		Status status = null;
		EmailQueueDaoForDML emailQueueDaoForDML = null;
	//	CustomTemplatesDao  customtempDao=null;
	
         Long  userid=user.getUserId();
		try{
			
			if(!(((user.getEmailCount() == null ? 0 : user.getEmailCount()) - (user.getUsedEmailCount() == null ? 0 : user.getUsedEmailCount())) >=  1)) {
				status = new Status("111551", PropertyUtil.getErrorMessage(111551, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			CustomTemplatesDao	customtempDao= (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			MyTemplatesDao   mytempdao=(MyTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
			
			String messageHeader = Constants.STRING_NILL;
			String msgContent="";
			CustomTemplates	 msgobj=  customtempDao.emailotpmsg(userid);
			
			logger.info("entered into db flow");
			msgContent= PropertyUtil.getPropertyValueFromDB("OTPTemplate");
			msgContent = msgContent.replace("[OrganizationName]", user.getUserOrganization().getOrganizationName());
			msgContent = msgContent.replace("[OTPCode]",otpCode);

			
					//Contacts contacts = contactsLoyalty.getContact();
			LoyaltyProgramService ltyprgrmservice=new LoyaltyProgramService();
			Long programId =  ltyprgrmservice.getCustomizedOTPEnabledProgList(userid, false);//find the customized OTP msg enabled program under user;
				if(programId !=null) {
					LoyaltyAutoCommGenerator ltyautocom=new LoyaltyAutoCommGenerator();
					LoyaltyAutoCommDao loyaltyAutoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
					
					//LoyaltyAutoComm loyaltyAutoComm = loyaltyAutoCommDao.findById(contactsLoyalty.getProgramId());
					//LoyaltyAutoComm loyaltyAutoComm =new LoyaltyAutoComm();
					LoyaltyAutoComm autoComm=ltyprgrmservice.getAutoCommunicationObj(programId);
					logger.info("the msg "+autoComm);
					
					try {
						
					Long templateId=autoComm.getOtpMessageAutoEmailTmplId();
					
					logger.info("the templateId is "+templateId);

					if(templateId !=null) {
						
						logger.info("the templateId is "+templateId);
	
						msgContent= ltyautocom.sendOtpMessagesTemplate(templateId, user,  email,otpCode);
						msgContent =msgContent.replace("[OTP]",otpCode);
						msgContent=msgContent.replace("[Org Name]",user.getUserOrganization().getOrganizationName());
						
						
					}
					}catch(Exception e) {
					
					logger.info("Exception occured because of Autoemail templtid is null "+e);

					}
				}

			
			logger.info(" final emailotpmsg is"  +msgContent);
		
			  EmailQueue otpEmail = new EmailQueue("OTP for verification",msgContent,Constants.EQ_TYPE_OTP_MAIL,  "Sent",email,MyCalendar.getNewCalendar(),user);
			  emailQueueDaoForDML.saveOrUpdate(otpEmail);
				UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
				usersDaoForDML.updateUsedEmailCount(user.getUserId(), 1);
			
				
				 if(Constants.VMTA_SENDGRIDAPI.equalsIgnoreCase(user.getVmta().getVmtaName())){	
						try {
							 

							if(user != null) {
								/*messageHeader =  "{\"unique_args\": {\"userId\": \""+ user.getUserId() +"\" ,\"EmailType\" : \""+Constants.EQ_TYPE_DIGITALRECIEPT +"\",\"sentId\" : \""+drSent.getId()+"\" ,\"ServerName\": \""+ serverName +"\" }}";*/
								
								JSONObject messageHEaderObject = new JSONObject();
								JSONArray toEmailArray = new JSONArray();
								JSONObject uniqueArgsObject = new JSONObject();
								
								toEmailArray.add( email);
								
								uniqueArgsObject.put("ServerName", PropertyUtil.getPropertyValue("schedulerIp"));
								uniqueArgsObject.put("Email", Constants.EQ_TYPE_OTP_MAIL);
								uniqueArgsObject.put("EQID", ""+otpEmail.getId());
								uniqueArgsObject.put("userId", user.getUserId());
								messageHEaderObject.put("unique_args", uniqueArgsObject);
								messageHEaderObject.put("to", toEmailArray);
								
								messageHeader = messageHEaderObject.toString();
								
							}
							logger.debug("SENDING THROUGH sendGridAPI ...>>>>>>>>>>>>");
							ExternalSMTPDigiReceiptSender externalSMTPSender =  new ExternalSMTPDigiReceiptSender();
							String htmlContent = otpEmail.getMessage();
							String textContent = otpEmail.getMessage(); 
							String jobId = Constants.EQ_TYPE_OTP_MAIL;
							String from = user.getUserOrganization().getOrganizationName()+ "<" +user.getEmailId()+ ">" ;
							String replyTo = from;
							String subject = otpEmail.getSubject();
							//externalSMTPSender.submitDigitalreceipt(messageHeader, htmlContent, textContent, from, subject, toEmailId);
							externalSMTPSender.submitDigitalreceiptWithReplyTo(messageHeader, htmlContent, textContent, from, subject, email, replyTo);
						
						} catch (Exception e) {
							
							logger.debug("Exception while sending through sendGridAPI .. returning ",e);
							
						}
					}
			logger.debug(">>>>>>> Started LoyaltyOTPOCServiceImpl :: sendOTPCode <<<<<<< ");

			return null;
		}catch(Exception e){
			logger.error("Exception in sending otp code ...", e);
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG)+" "+e.getMessage(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
			
		}
	
		}
	
	private String findMessageHeader(Users user){
		
		String messageHeader = Constants.STRING_NILL;
		try{
			
			List<SMSSettings> smsSettings = null;
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName("smsSettingsDao");
			
			if(SMSStatusCodes.optOutFooterMap.get(user.getCountryType())) {
				
				smsSettings = smsSettingsDao.findByUser(user.getUserId());
				if(smsSettings != null) {
					SMSSettings optinSettings = null;
			  		SMSSettings optOutSettings = null;
			  		SMSSettings helpSettings = null;
			  		
			  		for (SMSSettings eachSMSSetting : smsSettings) {
			  			
			  			if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
			  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
			  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
			  			
			  		}
					if(optinSettings != null && messageHeader.isEmpty()) messageHeader = optinSettings.getMessageHeader();
					else if(optOutSettings != null && messageHeader.isEmpty()) messageHeader = optOutSettings.getMessageHeader();
					else if(helpSettings != null && messageHeader.isEmpty()) messageHeader = helpSettings.getMessageHeader();
				}
			
			}
		}catch(Exception e){
			logger.error("Exception in find message header...", e);
			return messageHeader;
		}
		return messageHeader;
		
	}
}
