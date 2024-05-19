package org.mq.optculture.business.mobileapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.OTPGeneratedCodes;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyInquiryOCService;
import org.mq.optculture.data.dao.LoyaltySettingsDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.OTPGeneratedCodesDao;
import org.mq.optculture.data.dao.OTPGeneratedCodesDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLoginRequest;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLoginResponse;
import org.mq.optculture.model.mobileapp.MatchedCustomers;
import org.mq.optculture.model.mobileapp.Membership;
import org.mq.optculture.model.ocloyalty.AdditionalInfo;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryResponse;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.OTPRedeemLimit;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.model.updatecontacts.MobileAppPreferences;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.google.gson.Gson;

public class OCLoyaltyMemberLoginServiceImpl implements OCLoyaltyMemberLoginService  {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private String ltyImgUrl = PropertyUtil.getPropertyValue("LoyaltyImageServerUrl");
	private String ltyParentDirectory = PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim();
	private String imgUrl = PropertyUtil.getPropertyValue("ImageServerUrl")+"UserData";
	private String userParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory").trim();
	
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		
		logger.info(" Login base oc service called ...");
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		Gson gson = new Gson();
		LoyaltyMemberLoginResponse loyaltyMemberLoginResponse = null;
		LoyaltyMemberLoginRequest loginRequest = null;
		BaseResponseObject responseObject = null;
		String responseJson = null;
		
		if(serviceRequest == null || !OCConstants.LOYALTY_SERVICE_ACTION_LOGIN.equals(serviceRequest)){
			
			Status status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			loyaltyMemberLoginResponse = new LoyaltyMemberLoginResponse();
			loyaltyMemberLoginResponse.setStatus(status);
			
			responseJson = gson.toJson(loyaltyMemberLoginResponse);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			return responseObject;
		}
		
		try{
			loginRequest = gson.fromJson(requestJson, LoyaltyMemberLoginRequest.class);
		}catch(Exception e){
			Status status = new Status("101001", ""+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
			loyaltyMemberLoginResponse = new LoyaltyMemberLoginResponse();
			loyaltyMemberLoginResponse.setStatus(status);
			responseJson = gson.toJson(loyaltyMemberLoginResponse);
			
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Exited baserequest due to invalid JSON ");
			return responseObject;
			
		}
		
		try{
			OCLoyaltyMemberLoginService loyaltyMemberLoginService = (OCLoyaltyMemberLoginService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_MEMBER_LOGIN_BUSINESS_SERVICE);
			loyaltyMemberLoginResponse = loyaltyMemberLoginService.processLoginRequest(loginRequest, baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate());
			responseJson = gson.toJson(loyaltyMemberLoginResponse);
			
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
	public LoyaltyMemberLoginResponse processLoginRequest(LoyaltyMemberLoginRequest loyaltyMemberLoginRequest,
			String transactionId, String transactionDate) throws BaseServiceException {
		// TODO Auto-generated method stub
		
		
		logger.info("processLoginRequest method called...");
		
		LoyaltyMemberLoginResponse loyaltyMemberLoginResponse = null;
		Status status = null;
		Users user = null;
		
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(loyaltyMemberLoginRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(loyaltyMemberLoginRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		responseHeader.setSourceType(loyaltyMemberLoginRequest.getHeader().getSourceType() != null && 
				!loyaltyMemberLoginRequest.getHeader().getSourceType().trim().isEmpty() ? loyaltyMemberLoginRequest.getHeader().getSourceType().trim() : "");
		
		try{
			//validate mandatory fields userorgID, membershipnumber / phone number , password/OTP , incase OTP phone is must
			status = validateLoginReqData(loyaltyMemberLoginRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, null, status);
				return loyaltyMemberLoginResponse;
			}
			
			//validate user
			user = getUser(loyaltyMemberLoginRequest.getUser().getUserName(), loyaltyMemberLoginRequest.getUser().getOrganizationId());
			if(user == null){
				status = new Status("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, null, status);
				return loyaltyMemberLoginResponse;
			}
			if(!user.isEnabled()){
				status = new Status("111558", PropertyUtil.getErrorMessage(111558, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, null, status);
				return loyaltyMemberLoginResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("111559", PropertyUtil.getErrorMessage(111559, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, null, status);
				return loyaltyMemberLoginResponse;
			}
			Membership membership = loyaltyMemberLoginRequest.getMembership();
			String phone = membership.getPhoneNumber();
			String emailID = membership.getEmailId();
			String membershipNumber = membership.getMembershipNumber();
			ContactsLoyalty contactsLoyalty = null;
			if(membershipNumber != null && !membershipNumber.trim().isEmpty()){
				contactsLoyalty = findbyMembershipNumber(membershipNumber.trim(), user.getUserId());
				if(contactsLoyalty == null){
					status = new Status("800034", PropertyUtil.getErrorMessage(800034, OCConstants.ERROR_MOBILEAPP_FLAG)+membershipNumber.trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, null, status);
					return loyaltyMemberLoginResponse;
				}
				
			}
			//find membership by email / phone
			else if( (emailID != null && !emailID.trim().isEmpty()) || (phone != null && !phone.trim().isEmpty()) ){
					
				Contacts contacts =null;
				//List<ContactsLoyalty> enrollList= new ArrayList<ContactsLoyalty>();
				List<ContactsLoyalty> enrollList = findEnrollListByEmailORPhone(emailID, phone, user);
				
				if(enrollList == null){
					if((emailID != null && !emailID.trim().isEmpty())){
						
						status = new Status("800024", PropertyUtil.getErrorMessage(800024, OCConstants.ERROR_MOBILEAPP_FLAG)+emailID.trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					}
					else if((phone != null && !phone.trim().isEmpty())) {
						status = new Status("800025", PropertyUtil.getErrorMessage(800025, OCConstants.ERROR_MOBILEAPP_FLAG)+phone.trim(), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					}
					loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, null, status);
					return loyaltyMemberLoginResponse;
				}
				
				else if(enrollList.size() > 1){
					if((emailID != null && !emailID.trim().isEmpty())){
						
						status = new Status("800026", PropertyUtil.getErrorMessage(800026, OCConstants.ERROR_MOBILEAPP_FLAG)+"Email:"+emailID.trim()+". Please resend request with more info like Membership#.", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					}
					else if((phone != null && !phone.trim().isEmpty())) {
						status = new Status("800026", PropertyUtil.getErrorMessage(800026, OCConstants.ERROR_MOBILEAPP_FLAG)+"Phone#:"+phone.trim()+". Please resend request with more info like Membership#.", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					}
					List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(enrollList, loyaltyMemberLoginRequest);
					loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, matchedCustomers, status);
					return loyaltyMemberLoginResponse;
				
				}
				else {
					
					
					contactsLoyalty = enrollList.get(0);
				}
			}
			if(contactsLoyalty != null){
				
				//authenticate it first 
				String password = membership.getPassword();
				String OTP = membership.getOTP();
				if(password != null && !password.isEmpty()){
					//OPOyXbL3plA=
					try {
						if(!LoyaltyProgramHelper.checkAuthentication(password.trim(), contactsLoyalty.getMembershipPwd() )){
							status = new Status("800027", PropertyUtil.getErrorMessage(800027, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, null, status);
							return loyaltyMemberLoginResponse;
						}
					} catch (Exception e) {
						logger.error("Exception in bcrypt", e);
					}
					
				}else if(OTP != null && !OTP.trim().isEmpty()){
					
					OTP = OTP.trim();
					OTPGeneratedCodes otpgeneratedcode = findOTPCodeByPhone(phone, 
							user.getUserId(), OCConstants.OTP_GENERATED_CODE_STATUS_ACTIVE,emailID);
					
					if(otpgeneratedcode != null){
						Calendar currCal = Calendar.getInstance();
						Calendar createdDate = otpgeneratedcode.getCreatedDate();
						long currtime = currCal.getTimeInMillis()/1000;
						long createdtime = createdDate.getTimeInMillis()/1000;
						long timeDiff = currtime - createdtime;
						long duration = 15*60; 
						
						if(otpgeneratedcode.getOtpCode() != null && !otpgeneratedcode.getOtpCode().trim().isEmpty() &&
								!otpgeneratedcode.getOtpCode().equals(OTP)) {
							logger.info("OTP code invalid : "+OTP.trim());
							
							status = new Status("111549", PropertyUtil.getErrorMessage(111549, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, null, status);
							return loyaltyMemberLoginResponse;
						}
						else if(timeDiff > duration ){
							logger.info("OTP code expired : "+otpgeneratedcode.getOtpCode());
							otpgeneratedcode.setStatus(OCConstants.OTP_GENERATED_CODE_STATUS_EXPIRED);
							saveOTPgeneratedcode(otpgeneratedcode);
							status = new Status("111617", PropertyUtil.getErrorMessage(111617, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, null, status);
							return loyaltyMemberLoginResponse;
						}else if(otpgeneratedcode.getOtpCode() != null && !otpgeneratedcode.getOtpCode().trim().isEmpty() &&
								otpgeneratedcode.getOtpCode().equals(OTP)) {
							
							otpgeneratedcode.setStatus(OCConstants.OTP_GENERATED_CODE_STATUS_USED);
							saveOTPgeneratedcode(otpgeneratedcode);
						}
					}else{
						status = new Status("111549", PropertyUtil.getErrorMessage(111549, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, null, status);
						return loyaltyMemberLoginResponse;
					}
					
				}
				//successful login
				LoyaltyProgram loyaltyProgram = null;
				loyaltyProgram = LoyaltyProgramHelper.findLoyaltyProgramByProgramId(contactsLoyalty.getProgramId(), user.getUserId());
				if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equals(loyaltyProgram.getStatus())){
					status = new Status("111610", PropertyUtil.getErrorMessage(111610, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, null, status);
					return loyaltyMemberLoginResponse;
				}
				
				
				if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED) ||
						contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED) || 
						contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
					
					if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_EXPIRED)){
						
						String message = PropertyUtil.getErrorMessage(111517, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
						status = new Status("111517", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					}
					else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED)){
						
						//String message = PropertyUtil.getErrorMessage(111539, OCConstants.ERROR_LOYALTY_FLAG)+" "+contactsLoyalty.getCardNumber()+".";
						status = new Status("111539", "Member does not exist!", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					}else if(contactsLoyalty.getMembershipStatus().equals(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED)){
						
						String message = PropertyUtil.getErrorMessage(111578, OCConstants.ERROR_LOYALTY_FLAG)+contactsLoyalty.getCardNumber()+".";
						status = new Status("111578", message, OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					}
					MembershipResponse membershipResponse = new MembershipResponse();
					membershipResponse.setCardNumber(contactsLoyalty.getCardNumber()+"");
					membershipResponse.setCardPin(contactsLoyalty.getCardPin());
					//membershipResponse.setPassword(contactsLoyalty.getMembershipPwd() != null ? contactsLoyalty.getMembershipPwd() : Constants.STRING_NILL );
					membershipResponse.setPhoneNumber("");
					membershipResponse.setTierLevel("");
					membershipResponse.setTierName("");
					membershipResponse.setExpiry("");
					
					AdditionalInfo additionalInfo = new AdditionalInfo();
					/*UserOrganization userOrg=null;
					UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
					userOrg = usersDao.findByOrgId(user.getUserOrganization().getUserOrgId());
					String bannerImg = "";
					if(userOrg!=null && userOrg.getBannerPath()!=null && !userOrg.getBannerPath().isEmpty()) {
						bannerImg=userOrg.getBannerPath().replace(userParentDirectory,imgUrl);
					}
					additionalInfo.setBannerImage((userOrg!=null && userOrg.getBannerPath()!=null) ? bannerImg : "");
					String companyLogo = "";
					if(loyaltySettings!=null && loyaltySettings.getPath()!=null && !loyaltySettings.getPath().isEmpty()) {
						if(loyaltySettings!=null && loyaltySettings.getPath()!=null && !loyaltySettings.getPath().isEmpty()) {
							companyLogo = loyaltySettings.getPath().replace(PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim(),ltyImgUrl);
							if(loyaltySettings.getPath().contains("RewardApp"))
								companyLogo = loyaltySettings.getPath().replace(PropertyUtil.getPropertyValue("usersParentDirectory").trim(),imgUrl);
							//companyLogo=loyaltySettings.getPath().replace(ltyParentDirectory,ltyImgUrl);
						}
					}
					additionalInfo.setCompanyLogo((loyaltySettings!=null && loyaltySettings.getPath()!=null) ? companyLogo : "");*/
					//inquiryResponse = prepareInquiryResponse(responseHeader, membershipResponse, balances, null, additionalInfo, matchedCustomers, status);
					loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, null, null, null, null, null, status);
					return loyaltyMemberLoginResponse;
				
				}
				
				return performInquiryOperation(loyaltyMemberLoginRequest, responseHeader, contactsLoyalty, loyaltyProgram, user);
						
			}
			
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("exception :", e);
		}
			
			
		
		
		
		return null;
	}
	
	private List<Balance> prepareBalancesObject(ContactsLoyalty loyalty) throws Exception{
		LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);		List<Balance> balancesList = null;
		Balance pointBalances = null;
		Balance amountBalances = null;
		Balance giftBalances = null;
		balancesList = new ArrayList<Balance>();
		
		pointBalances = new Balance();
		pointBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		pointBalances.setValueCode(OCConstants.LOYALTY_TYPE_POINTS);
		//Special reward changes
		Long loyaltyBalance = loyalty.getLoyaltyBalance() == null ? 0l : loyalty.getLoyaltyBalance().intValue();
		Long valueCodeBalance = (long) loyaltyBalanceDao.findLoyaltyBalanceByLoyaltyId(loyalty.getLoyaltyId());
		valueCodeBalance = valueCodeBalance==null?0l:valueCodeBalance;
		loyaltyBalance = loyaltyBalance+valueCodeBalance; 
		pointBalances.setAmount(loyaltyBalance+"");
		pointBalances.setDifference("");
		
		amountBalances = new Balance();
		amountBalances.setType(OCConstants.LOYALTY_TYPE_REWARD);
		amountBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		//amountBalances.setAmount(loyalty.getGiftcardBalance() == null ? "" : ""+loyalty.getGiftcardBalance());
		amountBalances.setDifference("");
		if(loyalty.getGiftcardBalance() == null){
			amountBalances.setAmount("");
		}
		else{
			//double value = new BigDecimal(loyalty.getGiftcardBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			//double value = new BigDecimal(loyalty.getGiftcardBalance()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
			double value = Double.parseDouble(Utility.truncateUptoTwoDecimal(loyalty.getGiftcardBalance()));
			amountBalances.setAmount(""+value);
		}
		
		giftBalances = new Balance();
		giftBalances.setType(OCConstants.LOYALTY_TYPE_GIFT);
		giftBalances.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		//giftBalances.setAmount(loyalty.getGiftBalance() == null ? "" : ""+loyalty.getGiftBalance());
		if(loyalty.getGiftBalance() == null){
			giftBalances.setAmount("");
		}
		else{
			//double value = new BigDecimal(loyalty.getGiftBalance()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			//double value = new BigDecimal(loyalty.getGiftBalance()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
			double value = Double.parseDouble(Utility.truncateUptoTwoDecimal(loyalty.getGiftBalance()));
			giftBalances.setAmount(""+value);
		}
		giftBalances.setDifference("");
		
		balancesList.add(pointBalances);
		balancesList.add(amountBalances);
		balancesList.add(giftBalances);
		
		return balancesList;
	}
	
	private LoyaltyMemberLoginResponse performInquiryOperation(LoyaltyMemberLoginRequest loginRequest, ResponseHeader responseHeader,
			ContactsLoyalty contactsLoyalty, LoyaltyProgram loyaltyProgram, Users user) throws Exception{
		
		logger.info("calling inquiry operation...");
		
		LoyaltyMemberLoginResponse loyaltyMemberLoginResponse = null;
		Status status = null;
		LoyaltyProgramTier tier = null;
		
		LoyaltySettings loyaltySettings=null;
		LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
		loyaltySettings = loyaltySettingsDao.findByUserId(contactsLoyalty.getUserId());
		
		if(contactsLoyalty.getProgramTierId() != null)
			tier = LoyaltyProgramHelper.getLoyaltyTier(contactsLoyalty.getProgramTierId());//if the tier is not found no need to find the tier here
		String sourceType =loginRequest.getHeader().getSourceType();
		String instanceID = loginRequest.getMembership().getInstanceId();
		List<Balance> balances = prepareBalancesObject(contactsLoyalty);
		MembershipResponse membershipResponse = new MembershipResponse();
		if(	sourceType != null && !sourceType.isEmpty() && 
				sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)){
			membershipResponse.setFingerprintValidation(contactsLoyalty.getFpRecognitionFlag() != null ? contactsLoyalty.getFpRecognitionFlag().toString() : Constants.STRING_NILL );
			if(contactsLoyalty.getContact().getInstanceId() == null && instanceID != null && !instanceID.isEmpty()){
				ContactsDaoForDML contactsDaoDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
				contactsDaoDML.updateInstanceID(instanceID.trim(), contactsLoyalty.getContact().getContactId());
			}
				
			membershipResponse.setCardNumber(""+contactsLoyalty.getCardNumber());
			membershipResponse.setCardPin(contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin() : Constants.STRING_NILL);
			membershipResponse.setPhoneNumber(contactsLoyalty.getMobilePhone() != null ? contactsLoyalty.getMobilePhone() : Constants.STRING_NILL);;
				
			
		}
		
		boolean upgdResetFlag = false;
		if(loyaltyProgram.getMbrshipExpiryOnLevelUpgdFlag() == 'Y'){
			upgdResetFlag = true;
		}
		
		if(tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
			if(loyaltyProgram.getTierEnableFlag() == OCConstants.FLAG_YES  ) {
				membershipResponse.setTierLevel(tier.getTierType());
				membershipResponse.setTierName(tier.getTierName());
			}
			else {
				membershipResponse.setTierLevel("");
				membershipResponse.setTierName("");
			}
			
			if(loyaltyProgram.getMembershipExpiryFlag() == 'Y' && tier.getMembershipExpiryDateType() != null 
					&& tier.getMembershipExpiryDateValue() != null){
				membershipResponse.setExpiry(LoyaltyProgramHelper.getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), contactsLoyalty.getTierUpgradedDate(), 
					upgdResetFlag, tier.getMembershipExpiryDateType(), tier.getMembershipExpiryDateValue()));
			}
			else{
				membershipResponse.setExpiry("");
			}
			
		}
		else{
			
			membershipResponse.setExpiry("");
		}
		//APP - 1855
		String tierUpgdConstraint=tier == null || tier.getTierUpgdConstraint() == null ?"":(tier.getTierUpgdConstraint() != null ? tier.getTierUpgdConstraint() : "");
		Double tierUpgdConstraintValue=tier == null || tier.getTierUpgdConstraintValue()==null ?0.00:(tier.getTierUpgdConstraintValue() != null ? tier.getTierUpgdConstraintValue() : 0.00);
		Double requiredDiff=0.0;
		String nextTierName = tier != null ? LoyaltyProgramHelper.getNextTierName(contactsLoyalty.getProgramId(),tier.getTierId()) : "";
		membershipResponse.setTierUpgradeCriteria(tierUpgdConstraint);
		membershipResponse.setNextTierName(nextTierName == null ? "" : nextTierName);
		//Written in the assumption that tier.getTierUpgdConstraintValue() will always be null in the case of last tier. 
		if(loyaltyProgram.getTierEnableFlag()=='Y' && tier != null && tier.getTierUpgdConstraintValue() != null ) { 
		
		if(OCConstants.LOYALTY_LIFETIME_POINTS.equals(tierUpgdConstraint)){
			Double totLoyaltyPointsValue = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.00 : contactsLoyalty.getTotalLoyaltyEarned();
			requiredDiff=tierUpgdConstraintValue-totLoyaltyPointsValue;
			membershipResponse.setCurrentTierValue(totLoyaltyPointsValue+"");
		}
		else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tierUpgdConstraint)){
			Double totPurchaseValue = null;
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);//contactsLoyalty.getLifeTimePurchaseValue() == null ? 0.0 : contactsLoyalty.getLifeTimePurchaseValue();//loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId());//Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
			requiredDiff=tierUpgdConstraintValue-totPurchaseValue;
			membershipResponse.setCurrentTierValue(totPurchaseValue+"");
		}
		else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(tierUpgdConstraint)){
				Double cumulativeAmount = 0.0;
				Long months = tier.getTierUpgradeCumulativeValue();
				int days=(int) (-30*months);
				
				
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.DATE, days);
				
				
				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);


			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			cumulativeAmount = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));
			requiredDiff=tierUpgdConstraintValue-cumulativeAmount;
			membershipResponse.setCurrentTierValue(cumulativeAmount+"");	
			}
		
		if(requiredDiff<0) requiredDiff=0.0; 
		
		membershipResponse.setNextTierMilestone(requiredDiff+"");;
		
		}
		else {
			
			membershipResponse.setCurrentTierValue("");
			membershipResponse.setNextTierMilestone("");
			membershipResponse.setTierUpgradeCriteria("");
			
		}
		
		
		HoldBalance holdBalance = new HoldBalance();
		holdBalance.setPoints(contactsLoyalty.getHoldPointsBalance()== null ? "" : ""+contactsLoyalty.getHoldPointsBalance().intValue());
		holdBalance.setCurrency(contactsLoyalty.getHoldAmountBalance() == null ? "" : ""+Math.round(contactsLoyalty.getHoldAmountBalance()));
		
		boolean isStoreActiveForActivateAfter = LoyaltyProgramHelper.isActivateAfterAllowed(loginRequest.getHeader().getStoreNumber(),tier);
		
		if(tier != null && tier.getActivationFlag() == 'Y' && isStoreActiveForActivateAfter && !"Pending".equalsIgnoreCase(tier.getTierType())
				&& ((contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldAmountBalance() > 0) ||
						(contactsLoyalty.getHoldPointsBalance() != null && contactsLoyalty.getHoldPointsBalance() > 0))){
			holdBalance.setActivationPeriod(tier.getPtsActiveDateValue()+" "+tier.getPtsActiveDateType());
		}
		else{
			holdBalance.setActivationPeriod("");
		}
		
		List<ContactsLoyalty> contactLoyaltyList = new ArrayList<ContactsLoyalty>();
		contactLoyaltyList.add(contactsLoyalty);
		
		List<MatchedCustomer> matchedCustomers = prepareMatchedCustomers(contactLoyaltyList,loginRequest);

//		if(tier != null && OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND.equalsIgnoreCase(tier.getConversionType().trim()) ||
//				loyaltyProgram.getRedemptionOTPFlag() == 'Y'){
			
			AdditionalInfo additionalInfo = new AdditionalInfo();
			UserOrganization userOrg=null;
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			userOrg = usersDao.findByOrgId(contactsLoyalty.getOrgId());
			additionalInfo.setBannerImage((userOrg!=null && userOrg.getBannerPath()!=null) ? userOrg.getBannerPath() : "");
			//additionalInfo.setCompanyLogo((loyaltySettings!=null && loyaltySettings.getPath()!=null) ? loyaltySettings.getPath() : "");
			String bannerImg = "";
			if(userOrg!=null && userOrg.getBannerPath()!=null && !userOrg.getBannerPath().isEmpty()) {
				bannerImg=userOrg.getBannerPath().replace(userParentDirectory,imgUrl);
			}
			additionalInfo.setBannerImage((userOrg!=null && userOrg.getBannerPath()!=null) ? bannerImg : "");
			String companyLogo = "";
			if(loyaltySettings!=null && loyaltySettings.getPath()!=null && !loyaltySettings.getPath().isEmpty()) {
				if(loyaltySettings!=null && loyaltySettings.getPath()!=null && !loyaltySettings.getPath().isEmpty()) {
					companyLogo = loyaltySettings.getPath().replace(PropertyUtil.getPropertyValue("loyaltyPortalParentDirectory").trim(),ltyImgUrl);
					if(loyaltySettings.getPath().contains("RewardApp"))
						companyLogo = loyaltySettings.getPath().replace(PropertyUtil.getPropertyValue("usersParentDirectory").trim(),imgUrl);
					//companyLogo=loyaltySettings.getPath().replace(ltyParentDirectory,ltyImgUrl);
				}
			}
			//APP-3666
			additionalInfo.setCompanyLogo((loyaltySettings!=null && loyaltySettings.getPath()!=null) ? companyLogo : "");
			if(tier!=null && tier.getRedemptionOTPFlag() == 'Y'){
				additionalInfo.setOtpEnabled("True");
			}
			else{
				additionalInfo.setOtpEnabled("False");
			}
			if(tier!=null && tier.getRedemptionOTPFlag() == OCConstants.FLAG_YES && tier.getOtpLimitAmt()!=null){
			OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
			otpRedeemLimit.setAmount(""+tier.getOtpLimitAmt());
			otpRedeemLimit.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
			List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
			otpRedeemLimitlist.add(otpRedeemLimit);
			additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
			}
			else{
			List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
			additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
			}
		    if(tier != null && !"Pending".equalsIgnoreCase(tier.getTierType())){
				double loyaltyAmount = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
				double giftAmount = contactsLoyalty.getGiftBalance() == null ? 0.0 : contactsLoyalty.getGiftBalance();
				double pointsAmount = 0.0;
				double totalReedmCurr = 0.0;
				if(OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND.equalsIgnoreCase(tier.getConversionType().trim())) {
					pointsAmount = LoyaltyProgramHelper.calculatePointsAmount(contactsLoyalty, tier);
					additionalInfo.setPointsEquivalentCurrency(pointsAmount+"");
				}
				else{
					additionalInfo.setPointsEquivalentCurrency("");
				}
				totalReedmCurr = loyaltyAmount + pointsAmount + giftAmount;
				//To provide totalRedeemableCurrency  depending upon whichever is the least value among 
				//member's balance and redeem limit value (% of Billed Amount or flat value)//Changes 2.5.5.0
				additionalInfo.setTotalRedeemableCurrency(Utility.truncateUptoTwoDecimal(totalReedmCurr+""));
				
			}
			else {
				additionalInfo.setPointsEquivalentCurrency("");
				additionalInfo.setTotalRedeemableCurrency("");
			}
			
			String sessionID = LoyaltyProgramHelper.generateSessionID(Utility.getOnlyUserName(user.getUserName()), 
					Utility.getOnlyOrgId(user.getUserName()), loginRequest.getMembership().getDeviceId(), contactsLoyalty.getCardNumber(), user);
			if(sessionID == null){
				
				logger.debug("===something wrong in generating sessionID====");
			}
			membershipResponse.setSessionID(sessionID);
			/*if(tier != null && OCConstants.LOYALTY_CONVERSION_TYPE_ONDEMAND.equalsIgnoreCase(tier.getConversionType().trim())
					&& contactsLoyalty.getLoyaltyBalance() != null && !(contactsLoyalty.getLoyaltyBalance() == 0.0)
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints() 
						&& !"Pending".equalsIgnoreCase(tier.getTierType())){
					
					int factor = contactsLoyalty.getLoyaltyBalance().intValue()/tier.getConvertFromPoints().intValue();
					double pointsToCurrency = Math.floor(factor*tier.getConvertToAmount());
					int pointsCurrency = (int)pointsToCurrency;
					additionalInfo.setPointsEquivalentCurrency(""+pointsCurrency);
					
					if(contactsLoyalty.getGiftcardBalance() != null){
						double totalReedmCurr = contactsLoyalty.getGiftcardBalance() + pointsCurrency;
						additionalInfo.setTotalRedeemableCurrency(""+totalReedmCurr);
					}
					else{
						additionalInfo.setTotalRedeemableCurrency(""+pointsCurrency);
					}
				}
			else{
				additionalInfo.setPointsEquivalentCurrency("");
				additionalInfo.setTotalRedeemableCurrency("");
			}*/
			
			status = new Status("0", "Login was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			loyaltyMemberLoginResponse = prepareMemberLoginResponse(responseHeader, membershipResponse, balances, holdBalance, additionalInfo, matchedCustomers, status);
			return loyaltyMemberLoginResponse;
		/*}
		else{
			
			createSuccessfulTransaction(contactsLoyalty, inquiryRequest, Long.valueOf(responseHeader.getTransactionId()));
			
			status = new Status("0", "Inquiry was successful.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			inquiryResponse = prepareInquiryResponse(responseHeader, membershipResponse, balances, holdBalance, null, matchedCustomers, status);
			return inquiryResponse;
			
		}*/
		
	}
private List<MatchedCustomer> prepareMatchedCustomers(List<ContactsLoyalty> enrollList,LoyaltyMemberLoginRequest loginRequest) throws Exception {
		
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
									loginRequest.getHeader().getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_E_COMM)	)){
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
	
	private void saveOTPgeneratedcode(OTPGeneratedCodes otpgenCode) throws Exception {
		OTPGeneratedCodesDaoForDML otpGenCodesDaoForDML = (OTPGeneratedCodesDaoForDML)ServiceLocator.getInstance().getBeanByName(OCConstants.OTP_GENERATEDCODES_DAO_FOR_DML);
		otpGenCodesDaoForDML.saveOrUpdate(otpgenCode);
	}
	private ContactsLoyalty findbyMembershipNumber(String membershipNumber, Long userId) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findBy(membershipNumber, userId);//(Email, phone, userId);
	}
	
	private List<ContactsLoyalty> findEnrollListByEmailORPhone(String Email,String phone, Users user) throws Exception {
		
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		return loyaltyDao.findMembershipByEmailORPhone(Email, phone, user);
	}
	
	private Users getUser(String userName, String orgId) throws Exception{
		
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findByUsername(completeUserName);
		return user;
	}	
	
	private LoyaltyMemberLoginResponse prepareMemberLoginResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance, AdditionalInfo additionalInfo, List<MatchedCustomer> matchedCustomers,
			Status status) throws BaseServiceException {
		LoyaltyMemberLoginResponse loyaltyMemberLoginResponse = new LoyaltyMemberLoginResponse();
		loyaltyMemberLoginResponse.setHeader(header);
		
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
			/*OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
			otpRedeemLimit.setAmount("");
			otpRedeemLimit.setValueCode("");*/
			List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
			//otpRedeemLimitlist.add(otpRedeemLimit);
			additionalInfo.setOtpRedeemLimit(otpRedeemLimitlist);
			additionalInfo.setPointsEquivalentCurrency("");
			additionalInfo.setTotalRedeemableCurrency("");
			additionalInfo.setCompanyLogo("");
			additionalInfo.setBannerImage("");
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		
		loyaltyMemberLoginResponse.setMembership(membershipResponse);
		loyaltyMemberLoginResponse.setBalances(balances);
		loyaltyMemberLoginResponse.setHoldBalance(holdBalance);
		loyaltyMemberLoginResponse.setAdditionalInfo(additionalInfo);
		loyaltyMemberLoginResponse.setMatchedCustomers(matchedCustomers);
		loyaltyMemberLoginResponse.setStatus(status);
		return loyaltyMemberLoginResponse;
	}
	
	
	
	private Status validateLoginReqData(LoyaltyMemberLoginRequest loginRequest) throws Exception{
		Status status = null;
		if(loginRequest == null ){
			status = new Status(
					"101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		String reqSource = loginRequest.getHeader().getSourceType();
		if(loginRequest.getUser() == null){
			status = new Status(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(loginRequest.getMembership() == null){
			status = new Status(
					"101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if(reqSource!= null && reqSource.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)){
			
			if((loginRequest.getUser().getUserName() == null || loginRequest.getUser().getUserName().trim().isEmpty() ) &&
					(loginRequest.getUser().getOrganizationId() == null || loginRequest.getUser().getOrganizationId().trim().length() <=0 ) ){
				status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
				
			}
			Membership membership = loginRequest.getMembership();
			
			if((membership.getMembershipNumber() == null || membership.getMembershipNumber().isEmpty() ) && 
					(membership.getEmailId() ==null || membership.getEmailId().isEmpty()) &&
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
			}
			//do we need a separate flag to specify pwd/otp based login???
			/*if( (membership.getOTP() != null && !membership.getOTP().trim().isEmpty()) && 
					(membership.getPhoneNumber() == null || membership.getPhoneNumber().trim().isEmpty()) ) {
				
				status = new Status("800023", PropertyUtil.getErrorMessage(800023, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;	
			}*/
			
			
		}
		
		
		/*if(loginRequest.getHeader().getStoreNumber() == null || loginRequest.getHeader().getStoreNumber().length() <= 0){
			status = new Status("111501", PropertyUtil.getErrorMessage(111501, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/
		//removed the validation on 14th-Jan
		/*if(loginRequest.getAmount()!=null && loginRequest.getAmount().getReceiptAmount()!= null && !loginRequest.getAmount().getReceiptAmount().trim().isEmpty()//APP-1131
				&& Double.parseDouble(loginRequest.getAmount().getReceiptAmount())<=0){
			status = new Status("111592", PropertyUtil.getErrorMessage(111592, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/
		
		return status;
	}
	
	private OTPGeneratedCodes findOTPCodeByPhone(String phone, Long userId, String status,String email) throws Exception {
		OTPGeneratedCodesDao otpGenCodesDao = (OTPGeneratedCodesDao)ServiceLocator.getInstance().getBeanByName(OCConstants.OTP_GENERATEDCODES_DAO);
		return otpGenCodesDao.findOTPCodeByPhone(phone, userId, status,email, null);
	}
	
}
