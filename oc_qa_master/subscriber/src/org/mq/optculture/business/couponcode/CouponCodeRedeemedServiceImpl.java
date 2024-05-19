package org.mq.optculture.business.couponcode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.ReferralProgram;
import org.mq.marketer.campaign.beans.ReferralcodesIssued;
import org.mq.marketer.campaign.beans.ReferralcodesRedeemed;
import org.mq.marketer.campaign.beans.RewardReferraltype;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponCodesDaoForDML;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.CouponsDaoForDML;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDao;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.ReferralcodesIssuedDao;
import org.mq.marketer.campaign.dao.ReferralcodesRedeemedDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.CouponCodeProcessHelper;
import org.mq.optculture.business.loyalty.LoyaltyIssuanceOCService;
import org.mq.optculture.business.loyalty.PerformRedemption;
import org.mq.optculture.business.updatecontacts.UpdateContactsBusinessService;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.data.dao.ReferralProgramDao;
import org.mq.optculture.data.dao.ReferralcodesRedeemedDao;
import org.mq.optculture.data.dao.RewardReferraltypeDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.couponcodes.CouponCodeInfo;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemReq;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemResponse;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemedObj;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemedResponse;
import org.mq.optculture.model.couponcodes.HeaderInfo;
import org.mq.optculture.model.couponcodes.PurchaseCouponInfo;
import org.mq.optculture.model.couponcodes.StatusInfo;
import org.mq.optculture.model.couponcodes.UserDetails;
import org.mq.optculture.model.ocloyalty.Amount;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.model.updatecontacts.ContactRequest;
import org.mq.optculture.model.updatecontacts.ContactResponse;
import org.mq.optculture.model.updatecontacts.Customer;
import org.mq.optculture.model.updatecontacts.Header;
import org.mq.optculture.model.updatecontacts.User;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;

import com.google.gson.Gson;

public class CouponCodeRedeemedServiceImpl implements CouponCodeRedeemedService {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	boolean refcodesredeem = false;

	@Override
	
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		BaseResponseObject baseResponseObject=new BaseResponseObject();
		CouponCodeRedeemedResponse couponCodeRedeemedResponse=null;
		try {
			logger.debug("-------entered processRequest---------");
			if(baseRequestObject.getAction().equals(OCConstants.COUPON_CODE_REDEEMED_REQUEST)) {

				//json to object
				Gson gson =new Gson();
				CouponCodeRedeemedObj couponCodeRedeemedObj;
				try {
					couponCodeRedeemedObj = gson.fromJson(baseRequestObject.getJsonValue(), CouponCodeRedeemedObj.class);
				} catch (Exception e) {
					logger.error("Exception e ::",e);
					StatusInfo statusInfo = new StatusInfo("100900",PropertyUtil.getErrorMessage(100900, OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					couponCodeRedeemedResponse = finalResponse(new HeaderInfo(),new CouponCodeInfo(), new PurchaseCouponInfo(),new UserDetails(), statusInfo);
					String jsonString=gson.toJson(couponCodeRedeemedResponse);
					baseResponseObject.setJsonValue(jsonString);
					baseResponseObject.setAction(OCConstants.COUPON_CODE_REDEEMED_REQUEST);
					return baseResponseObject;
				}
				CouponCodeRedeemedService couponCodeRedeemedService=(CouponCodeRedeemedService) ServiceLocator.getInstance().getServiceByName(OCConstants.COUPON_CODE_REDEEMED_BUSINESS_SERVICE);
				couponCodeRedeemedResponse=(CouponCodeRedeemedResponse)couponCodeRedeemedService.processRedeemedRequest(couponCodeRedeemedObj);

				//object to json
				String json = gson.toJson(couponCodeRedeemedResponse);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.COUPON_CODE_REDEEMED_REQUEST);
				return baseResponseObject;
			}
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}
		 logger.debug("-------exit  processRequest---------");
		return baseResponseObject;
	}
@Override
public BaseResponseObject processRedeemedRequest(CouponCodeRedeemedObj couponCodeRedeemedObj, String itemCodeInfo, String cardNumber, int redeemReward)
		throws BaseServiceException {

	CouponCodeRedeemedResponse couponCodeRedeemedResponse=null;
	StatusInfo statusInfo=null;
	HeaderInfo headerInfo=null;
	UserDetails userDetails=null;
	CouponCodeInfo couponCodeInfo=null;
	PurchaseCouponInfo purchaseCouponInfo=null;
	try {
		logger.debug("-------entered processRedeemedRequest1---------");
		UsersDao usersDao=(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
		ReferralcodesIssuedDao referralCodesDao=(ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
		ContactsDao contactdao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);

		ReferralcodesRedeemedDao refredeemedCodesDao=(ReferralcodesRedeemedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_CODES_REDEEMED_DAO);

		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);

		statusInfo=validateRootObj(couponCodeRedeemedObj);
		if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
			couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			return couponCodeRedeemedResponse;
		}
		headerInfo=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getHEADERINFO();
		userDetails=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getUSERDETAILS();
		couponCodeInfo=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO();
		purchaseCouponInfo=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getPURCHASECOUPONINFO();
		//validate headerinfo requestId
		statusInfo=validateRequestId(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getHEADERINFO().getREQUESTID());
		if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
			couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			return couponCodeRedeemedResponse;
		}
		//validate userDetails 
		String token=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getUSERDETAILS().getTOKEN();
		String userName=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getUSERDETAILS().getUSERNAME();
		String userOrg=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getUSERDETAILS().getORGID();
		statusInfo=validateUserDetails(token,userName,userOrg);
		if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
			couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			return couponCodeRedeemedResponse;
		}
		Users user = usersDao.findByToken(userName+ Constants.USER_AND_ORG_SEPARATOR +userOrg, token );
		statusInfo=validateUser(user);
		if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
			couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			return couponCodeRedeemedResponse;
		}
		
		Long orgId = user.getUserOrganization().getUserOrgId();
		//validate coupon code
		String reqCoupcode=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO().getCOUPONCODE();
		String redeemStr = null;
		redeemStr=getRedeemStr(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO().getCUSTOMERID(),couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO().getEMAIL(),couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO().getPHONE(),redeemStr);
		statusInfo=validateCouponCode(reqCoupcode);
		if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
			couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			return couponCodeRedeemedResponse;
		}
		//validate purchasecouponinfo
		double totalAmount=0;
		double totalDiscount=0;
		try {
			totalAmount=Double.parseDouble( couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getPURCHASECOUPONINFO().getTOTALAMOUNT());
		} catch (NumberFormatException e) {
			statusInfo=validateTotAmount();
		}
		try{
			totalDiscount=Double.parseDouble(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getPURCHASECOUPONINFO().getTOTALDISCOUNT());
			
		}catch(NumberFormatException e){
			statusInfo=validateTotDiscount();
		}
		statusInfo=validatePurchaseCouponInfoDetails(totalAmount,totalDiscount,purchaseCouponInfo);
		if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
			couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			return couponCodeRedeemedResponse;
		}
		
		Coupons coupObj = null;
		Contacts contactsobj=null;
		MailingListDao mailingListDao=null;
		ReferralcodesRedeemed redeemedrefobj=null;
		ContactsLoyalty contactsLoyaltyobjfind=null;
		ReferralcodesRedeemed redeemedobj=null;
		ReferralcodesIssued  refCodeObj=null;
		
		String phone=couponCodeInfo.getPHONE();
		String phoneParse = Utility.phoneParse(phone.trim(),
				user != null ? user.getUserOrganization() : null);
		
		CouponCodes couponCodes = couponCodesDao.testForCouponCodes(reqCoupcode, orgId);
		logger.info("couponCodes value is"+couponCodes);
		if(couponCodes==null) {
		try {
			
		refCodeObj = referralCodesDao.testForrefCodes(reqCoupcode, orgId);

		}catch(Exception e) {
			logger.info("Exception in refCodeObj object"+e);
		}
		}
		
		if(refCodeObj!=null) {
			
				logger.info("entering into referral flow");
			
			try {
				contactsobj=contactdao.findContactByValues(couponCodeInfo.getEMAIL(),phoneParse,null, user.getUserId());
				logger.info("contactsobj value is"+contactsobj);
			}catch(Exception e) {
				logger.info("Exception in contactsobj object"+e);
			}

			coupObj = getCouponObj(refCodeObj, reqCoupcode, orgId);
			logger.info("coupObj value is"+coupObj);
			
			statusInfo = validateCoupObj(coupObj);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
				}
		
			try {
			redeemedrefobj=refredeemedCodesDao.testIfOfferAvailed( user.getUserOrganization().getUserOrgId(),contactsobj.getContactId());
			logger.info("value(redeemed Coupon) is"+redeemedrefobj);
			}catch(Exception e) {
				logger.info("Exception in redeemedrefobj object"+e);
			}
			
			statusInfo = checkredeemedCoupon(redeemedrefobj,user);
			if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				logger.info("entering check redeemed coupon"+statusInfo);
				couponCodeRedeemedResponse = finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);	
				return couponCodeRedeemedResponse;
			}
			
			try {
				contactsLoyaltyobjfind = contactsLoyaltyDao.findBy(user, couponCodeInfo.getEMAIL(), phoneParse);
				logger.info("value(Loyaltycontact Date) is"+contactsLoyaltyobjfind);				
				}catch(Exception e) {
					logger.info("Exception in Loyaltycontact object"+e);
				}
				
		
			if(contactsLoyaltyobjfind!=null) {
		
			statusInfo = checkloyaltycontact(contactsLoyaltyobjfind,user);
			if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse = finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);	
				return couponCodeRedeemedResponse;
			}
			}
	
			
			if(contactsobj==null && !headerInfo.getSOURCETYPE().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_OPTDR)) {
				
				logger.info("entering contact creation");

				ContactResponse contactResponse = null;
				MailingList posML=null;
				ContactRequest contactRequest = new ContactRequest();
				Header header = new Header();
				User userh = new User();
				Gson gson = new Gson();
				String finialjsonbeforegivingtoUpdate = gson.toJson(contactRequest);
				Calendar syscal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
				String requestId = OCConstants.DR_LTY_EXTRACTION_REQUEST_ID+user.getToken()+"_"+System.currentTimeMillis();
				mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
				posML = mailingListDao.findPOSMailingList(user);
			
				header.setRequestDate(syscal.toString());
				header.setRequestId(requestId);
				header.setSourceType("Referral");
				header.setContactSource("LoyaltyApp");
				header.setContactList(posML.getListName());
				
				Customer customerobj=new Customer();
				
				customerobj.setCreationDate(syscal.toString());
				customerobj.setEmailAddress(couponCodeInfo.getEMAIL());
				customerobj.setPhone(phoneParse);
				
				userh.setUserName(userName);
				userh.setOrganizationId(userOrg);
				userh.setToken(token);

				contactRequest.setHeader(header);
				contactRequest.setUser(userh);
				contactRequest.setCustomer(customerobj);
					
				UpdateContactsBusinessService updateContactsBusinessService=(UpdateContactsBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.UPDATE_CONTACTS_BUSINESS_SERVICE);

				ContactResponse baseService = (ContactResponse)updateContactsBusinessService.processUpdateContactRequest(contactRequest);
  
				String finialjson = gson.toJson(baseService);
				logger.info("final json for ocsurvey from updateContactsBusinessService.processUpdateContactRequest..:"+finialjson);
				
				contactResponse = baseService;
					
					
				}
				
			}
		
		
			if(coupObj == null)coupObj = getCouponObjcouponcode(couponCodes, reqCoupcode, orgId);

				statusInfo = validateCoupObj(coupObj);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
				}

		if(refCodeObj== null && couponCodes != null) {
        	
				logger.info("refCodeObj value is"+refCodeObj);
				logger.info("couponCodes value is"+couponCodes);

			
			if(coupObj.getCouponGeneratedType().equalsIgnoreCase(Constants.COUP_GENT_TYPE_SINGLE) && 
        			!couponCodes.getCouponCode().equalsIgnoreCase(reqCoupcode)) {
				logger.info("entering if in single");
				statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
        	}
        	else if(coupObj.getCouponGeneratedType().equalsIgnoreCase(Constants.COUP_GENT_TYPE_MULTIPLE) && 
        			couponCodes.getCouponCode().length() == 8 && !couponCodes.getCouponCode().equalsIgnoreCase(reqCoupcode)) {
        		
        	
				logger.info("entering if in multiple");

        		statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
        	}
        	else if(coupObj.getCouponGeneratedType().equalsIgnoreCase(Constants.COUP_GENT_TYPE_MULTIPLE) && 
        			couponCodes.getCouponCode().length() != 8 && !couponCodes.getCouponCode().equals(reqCoupcode)) {
        		
				logger.info("entering if in last");

        		
        		statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
        	}
		}
		
		if(coupObj != null && coupObj.getCouponName() != null && !coupObj.getCouponName().isEmpty()) {
			
			couponCodeInfo.setCOUPONNAME(coupObj.getCouponName());
			
		}
		if(coupObj != null && coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_SINGLE) && 
				!coupObj.getCouponCode().equalsIgnoreCase(reqCoupcode)) {
			statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			return couponCodeRedeemedResponse;
			
		}
		
		statusInfo=checkCouponStatus(couponCodes,coupObj);
		if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
			couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			return couponCodeRedeemedResponse;
		}
		
		statusInfo = validateDynamicCoupon(coupObj,couponCodes);
		if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
			couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			return couponCodeRedeemedResponse;
		}
	
		statusInfo=validateStore(coupObj,couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO());
		if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
			couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			return couponCodeRedeemedResponse;
		}
		
		
		logger.info("refCodeObj value before savecoup"+refCodeObj);
	//	if(refCodeObj==null && couponCodes!=null) {
		
		double totalDisForIndia = 0;
		if(totalDiscount==1 && user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA)) {//APP-3667
			CouponCodeProcessHelper couponHelper = new CouponCodeProcessHelper();
			totalDisForIndia = couponHelper.getTotalDiscValue(user, coupObj, couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ());
			//totalDiscount = totalDisForIndia>0 ? totalDisForIndia : totalDiscount;
			logger.info("totalDisForIndia 1>>>"+totalDisForIndia+" & totalDiscount 1>>"+totalDiscount);
			
		}
			
		statusInfo=saveCouponCodesObj(coupObj,couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ(),user,orgId,totalAmount,totalDiscount,redeemStr,couponCodes,
				itemCodeInfo,cardNumber,redeemReward);
		if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
			couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			return couponCodeRedeemedResponse;
		}
		//}
		couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
		 logger.debug("-------exit  processRedeemedRequest---------"+coupObj.getLoyaltyPoints()+"======");
		 
		 
		 if(coupObj.getLoyaltyPoints() != null &&  coupObj.getLoyaltyPoints() == 1 &&
				 (coupObj.getRequiredLoyltyPoits() != null || coupObj.getMultiplierValue()!=null)) {
			 
			 //couponCodes.setItemInfo(itemCodeInfo);
			 //couponCodes.setValueCode(coupObj.getValueCode() == null ? OCConstants.LOYALTY_POINTS : coupObj.getValueCode());
			 //couponCodes.setMembership(cardNumber);
			 //couponCodes.setUsedLoyaltyPoints(Double.valueOf(coupObj.getRequiredLoyltyPoits()));
			 //CouponCodesDaoForDML couponCodesDaoForDML=(CouponCodesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPONCODES_DAOForDML);
			 //couponCodesDaoForDML.saveOrUpdate(couponCodes);
			 logger.debug("===about to call===");
			 PerformRedemption executer = new PerformRedemption(coupObj, couponCodes, user, couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ(), redeemReward, totalDisForIndia);
			 executer.performLoyaltyRedemption();
			 //executer.start();
		 }
		return couponCodeRedeemedResponse;
	}catch (Exception e) {
		statusInfo = new StatusInfo("100901",PropertyUtil.getErrorMessage(100901,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		couponCodeRedeemedResponse = finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
		logger.error("Exception ::" , e);
		return couponCodeRedeemedResponse;
		/*logger.error("Exception ::" , e);
		throw new BaseServiceException("Exception occured while processing processRedeemedRequest::::: ", e);*/
	}
	/*finally{
		couponCodeRedeemedResponse=finalResponse(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getHEADERINFO(),couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO(),
				couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getPURCHASECOUPONINFO(),couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getUSERDETAILS(),statusInfo);
		return couponCodeRedeemedResponse;
	}*/

}
private StatusInfo checkredeemedCoupon(ReferralcodesRedeemed redeemedrefobj, Users user) {
	
	StatusInfo statusInfo=null;
	logger.debug("-------entered checkredeemedCoupon---------");
	
	logger.info("redeemedrefobj value is"+redeemedrefobj);

		if(redeemedrefobj!= null){
		logger.debug("Error :Promo-code already redeemed ****");
		statusInfo = new StatusInfo("100014",PropertyUtil.getErrorMessage(100014,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		return statusInfo;
		}
		return statusInfo;
	
}
private StatusInfo checkusedRefcode(ReferralcodesRedeemed redeemedrefobj, Users user) {
	
	StatusInfo statusInfo=null;
	logger.debug("-------entered refcoe---------");

		if(redeemedrefobj!= null){
		logger.debug("Error :Refcode already redeemed with email  ****");
		statusInfo = new StatusInfo("100025",PropertyUtil.getErrorMessage(100025,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		return statusInfo;
		}
		return statusInfo;
	
}

private StatusInfo checkloyaltycontact(ContactsLoyalty contactsloyaltyobj, Users user) {
	
	StatusInfo statusInfo=null;
	logger.debug("-------entered validateloyalty contact---------");

		Calendar cal2 = Calendar.getInstance();
		Calendar createddate=contactsloyaltyobj.getCreatedDate();
	
		String currentDate = MyCalendar.calendarToString(cal2,
				MyCalendar.FORMAT_YEARMONTH_HYPHEN);

		String createdtDate = MyCalendar.calendarToString(createddate,
				MyCalendar.FORMAT_YEARMONTH_HYPHEN);

		logger.info("currentDate is"+currentDate);
		logger.info("createdtDate is"+createdtDate);

		if(contactsloyaltyobj!=null ){
		
			if(!(createdtDate.compareTo(currentDate)==0)) {
		
			logger.info("created date is not equal to current date");
			statusInfo = new StatusInfo("100024:",PropertyUtil.getErrorMessage(100024,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
	}
		return statusInfo;
	
}









	@Override
	public CouponCodeRedeemedResponse processRedeemedRequest(
			CouponCodeRedeemedObj couponCodeRedeemedObj)
					throws BaseServiceException {
		CouponCodeRedeemedResponse couponCodeRedeemedResponse=null;
		StatusInfo statusInfo=null;
		HeaderInfo headerInfo=null;
		UserDetails userDetails=null;
		CouponCodeInfo couponCodeInfo=null;
		PurchaseCouponInfo purchaseCouponInfo=null;
		try {
			logger.debug("-------entered processRedeemedRequest2---------");
			UsersDao usersDao=(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			ReferralcodesIssuedDao referralCodesDao=(ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);

			
			statusInfo=validateRootObj(couponCodeRedeemedObj);
			
			logger.debug("-------entered processRedeemedRequest2 validateRootObj---------");

			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
			}
			headerInfo=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getHEADERINFO();
			userDetails=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getUSERDETAILS();
			couponCodeInfo=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO();
			purchaseCouponInfo=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getPURCHASECOUPONINFO();
			//validate headerinfo requestId
			statusInfo=validateRequestId(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getHEADERINFO().getREQUESTID());
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
			}
			//validate userDetails 
			String token=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getUSERDETAILS().getTOKEN();
			String userName=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getUSERDETAILS().getUSERNAME();
			String userOrg=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getUSERDETAILS().getORGID();
		
			statusInfo=validateUserDetails(token,userName,userOrg);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
			}
			Users user = usersDao.findByToken(userName+ Constants.USER_AND_ORG_SEPARATOR +userOrg, token );
			//ignore direct req if promo redeem from DR is enabled
			
			logger.info("user value is "+user);

			
			logger.info("EnablePromoRedemptio value is "+user.isEnablePromoRedemption());

		
			
			if(user.isEnablePromoRedemption() && user.isIgnoretrxUpOnExtraction()) {
				statusInfo=new StatusInfo("0","Promo will be redeemed shortly", OCConstants.JSON_RESPONSE_IGNORED_MESSAGE);
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
			}
			
			
			statusInfo=validateUser(user);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
			}
			
			Long orgId = user.getUserOrganization().getUserOrgId();
			
			logger.info("orgId value is "+orgId);

			//validate coupon code
			String reqCoupcode=couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO().getCOUPONCODE();
			String redeemStr = null;
			redeemStr=getRedeemStr(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO().getCUSTOMERID(),couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO().getEMAIL(),couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO().getPHONE(),redeemStr);
			statusInfo=validateCouponCode(reqCoupcode);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
			}
			//validate purchasecouponinfo
			double totalAmount=0;
			double totalDiscount=0;
			try {
				totalAmount=Double.parseDouble( couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getPURCHASECOUPONINFO().getTOTALAMOUNT());
			} catch (NumberFormatException e) {
				statusInfo=validateTotAmount();
			}
			try{
				totalDiscount=Double.parseDouble(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getPURCHASECOUPONINFO().getTOTALDISCOUNT());
				
			}catch(NumberFormatException e){
				statusInfo=validateTotDiscount();
			}
			statusInfo=validatePurchaseCouponInfoDetails(totalAmount,totalDiscount,purchaseCouponInfo);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
			}
			
			
			//CouponCodes couponCodes = couponCodesDao.testForCouponCodes(reqCoupcode, orgId);
			//Coupons coupObj = getCouponObj(couponCodes, reqCoupcode, orgId);
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);

			ContactsDao contactdao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);

			logger.info("contactdao value is "+contactdao);

			
			ReferralcodesRedeemedDao refredeemedCodesDao=(ReferralcodesRedeemedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_CODES_REDEEMED_DAO);

			ReferralcodesIssued  refCodeObj=null;
			ReferralcodesRedeemed redeemedrefobj=null;
			ContactsLoyalty contactsLoyaltyobjfind=null;
			Contacts contactsobj=null;
			MailingListDao mailingListDao=null;
			Coupons coupObj = null;
			CouponCodes couponCodes=null;

			String phone=couponCodeInfo.getPHONE();
			String email=couponCodeInfo.getEMAIL();
			String phoneParse = Utility.phoneParse(phone.trim(),
					user != null ? user.getUserOrganization() : null);
			 
			couponCodes = couponCodesDao.testForCouponCodes(reqCoupcode, orgId);
			if(couponCodes == null) {
			try {
				refCodeObj = referralCodesDao.testForrefCodes(reqCoupcode, orgId);
				logger.info("couponCodes value is"+couponCodes);

			}catch(Exception e) {
				logger.info("Exception due to null object"+e);
			}
			}
			
			logger.info("refCodeObj value is "+refCodeObj);

		if( refCodeObj!=null ) {
				
			logger.info("entering into referral flow");
			
				if(purchaseCouponInfo.getTYPE()!=null && !purchaseCouponInfo.getTYPE().isEmpty() && purchaseCouponInfo.getTYPE().equals("Void")) {
					logger.info("No Void on reffereal code");
					statusInfo=new StatusInfo("100026", PropertyUtil.getErrorMessage(100026,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
					return couponCodeRedeemedResponse;
				}
				
				try {
				contactsobj=contactdao.findContactByValues(couponCodeInfo.getEMAIL(),phoneParse,null, user.getUserId());
				logger.info("contactsobj value is"+contactsobj);
			
				}catch(Exception e) {
					logger.info("Exception due to null contactsobj  object"+e);
				}
			
				coupObj = getCouponObj(refCodeObj, reqCoupcode, orgId);

				statusInfo = validateCoupObj(coupObj);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
					couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
					logger.info("couponCodeRedeemedResponse value is"+couponCodeRedeemedResponse);
					return couponCodeRedeemedResponse;
				}
			
				try {
				redeemedrefobj=refredeemedCodesDao.testIfOfferAvailed( user.getUserOrganization().getUserOrgId(),contactsobj.getContactId());
				logger.info("redeemedrefobj value is"+redeemedrefobj);
				}catch(Exception e) {
					logger.info("Exception in redeemedrefobj object"+e);
				}
				
				
				statusInfo = checkredeemedCoupon(redeemedrefobj,user);
				if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					logger.info("entering check redeemed coupon"+statusInfo);
					couponCodeRedeemedResponse = finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);	
					return couponCodeRedeemedResponse;
				}
				
				try {
				contactsLoyaltyobjfind = contactsLoyaltyDao.findBy(user, couponCodeInfo.getEMAIL(), phoneParse);
				logger.info("value(Loyaltycontact Date) is"+contactsLoyaltyobjfind);
				}catch(Exception e) {
					logger.info("Exception in contactsLoyaltyobjfind object"+e);
				}
				
				if(contactsLoyaltyobjfind!=null) {
			
				statusInfo = checkloyaltycontact(contactsLoyaltyobjfind,user);
				if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					couponCodeRedeemedResponse = finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);	
					return couponCodeRedeemedResponse;
				}
				}
				
				if(contactsobj==null) {
					
					logger.info("entering contact creation");

					ContactResponse contactResponse = null;
					MailingList posML=null;
					ContactRequest contactRequest = new ContactRequest();
					Header header = new Header();
					User userh = new User();
					Gson gson = new Gson();
					String finialjsonbeforegivingtoUpdate = gson.toJson(contactRequest);
					Calendar syscal = new MyCalendar(Calendar.getInstance(), null,
							MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
					String requestId = OCConstants.DR_LTY_EXTRACTION_REQUEST_ID+user.getToken()+"_"+System.currentTimeMillis();
					mailingListDao = (MailingListDao)ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
					posML = mailingListDao.findPOSMailingList(user);
					
					header.setRequestDate(syscal.toString());
					header.setRequestId(requestId);
					header.setSourceType("Referral");
					header.setContactSource("LoyaltyApp");
					header.setContactList(posML.getListName());
					
					Customer customerobj=new Customer();
					
					customerobj.setCreationDate(syscal.toString());
					customerobj.setEmailAddress(couponCodeInfo.getEMAIL());
					customerobj.setPhone(phoneParse);
					
					userh.setUserName(userName);
					userh.setOrganizationId(userOrg);
					userh.setToken(token);

					contactRequest.setHeader(header);
					contactRequest.setUser(userh);
					contactRequest.setCustomer(customerobj);
						
					UpdateContactsBusinessService updateContactsBusinessService=(UpdateContactsBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.UPDATE_CONTACTS_BUSINESS_SERVICE);

					ContactResponse baseService = (ContactResponse)updateContactsBusinessService.processUpdateContactRequest(contactRequest);
	  
					String finialjson = gson.toJson(baseService);
					logger.info("final json from updateContactsBusinessService.processUpdateContactRequest..:"+finialjson);
					
					contactResponse = baseService;
						
					}
			}
			
			
			
			
			
			if(coupObj == null)coupObj = getCouponObjcouponcode(couponCodes, reqCoupcode, orgId);
			
			statusInfo = validateCoupObj(coupObj);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
			}

			if(couponCodes != null) {
            	if(coupObj.getCouponGeneratedType().equalsIgnoreCase(Constants.COUP_GENT_TYPE_SINGLE) && 
            			!couponCodes.getCouponCode().equalsIgnoreCase(reqCoupcode)) {
            		statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
    				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
    				return couponCodeRedeemedResponse;
            	}
            	else if(coupObj.getCouponGeneratedType().equalsIgnoreCase(Constants.COUP_GENT_TYPE_MULTIPLE) && 
            			couponCodes.getCouponCode().length() == 8 && !couponCodes.getCouponCode().equalsIgnoreCase(reqCoupcode)) {
            		statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
    				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
    				return couponCodeRedeemedResponse;
            	}
            	else if(coupObj.getCouponGeneratedType().equalsIgnoreCase(Constants.COUP_GENT_TYPE_MULTIPLE) && 
            			couponCodes.getCouponCode().length() != 8 && !couponCodes.getCouponCode().equals(reqCoupcode)) {
            		statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
    				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
    				return couponCodeRedeemedResponse;
            	}
			}
			
			if(coupObj != null && coupObj.getCouponName() != null && !coupObj.getCouponName().isEmpty()) {
				
				couponCodeInfo.setCOUPONNAME(coupObj.getCouponName());
				
			}
			if(coupObj != null && coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_SINGLE) && 
					!coupObj.getCouponCode().equalsIgnoreCase(reqCoupcode)) {
				statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
				
			}
			
			statusInfo=checkCouponStatus(couponCodes,coupObj);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
			}
			
			statusInfo = validateDynamicCoupon(coupObj,couponCodes);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
			}
		
			statusInfo=validateStore(coupObj,couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO());
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
			}
			
			//APP-4766 Void coupon code
			String type = purchaseCouponInfo.getTYPE()!=null ? purchaseCouponInfo.getTYPE() : "";
			String docSID = couponCodeInfo.getDOCSID() !=null ? couponCodeInfo.getDOCSID() : "";
			
			// void coupon code when there is TYPE='Void'
			if(type!=null && !type.isEmpty() && type.equalsIgnoreCase("Void")) {
				
				logger.info("inside void promo");
				if(docSID!=null && !docSID.isEmpty()) {
					
					CouponCodes redeemedCouponCode = couponCodesDao.findByDocSid(docSID,reqCoupcode,orgId);
					if(redeemedCouponCode == null) {
						
						statusInfo=new StatusInfo("0","Promo reverted sucessfully", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
						couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
						logger.debug("------- No Redemption found to roll-back ---------");
						 return couponCodeRedeemedResponse;
						/*statusInfo=new StatusInfo("100027", PropertyUtil.getErrorMessage(100027,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
						return couponCodeRedeemedResponse;*/
					}
					logger.info("redeemedCouponCode status before update ==== "+redeemedCouponCode.getStatus());
					//CouponCodes redeemedCouponCodeDup = redeemedCouponCode;
					
					//Loyalty points/reward reversals
					if(coupObj.getLoyaltyPoints()!=null && coupObj.getLoyaltyPoints().byteValue() == 1 && 
							coupObj.getRequiredLoyltyPoits()!=null && coupObj.getRequiredLoyltyPoits()>0) {
						
						ContactsLoyalty membership = null;
						String cardNUmber = couponCodeInfo.getCARDNUMBER();
						if(redeemedCouponCode.getValueCode()!=null) {
							
							if(redeemedCouponCode.getContactId()!=null) {
								membership = contactsLoyaltyDao.findByContactId(user.getUserId(),redeemedCouponCode.getContactId());
							}else {
								
								if(cardNUmber == null || cardNUmber.trim().isEmpty()){
									logger.debug("===no card# is given ===");
									String custSID = couponCodeInfo.getCUSTOMERID();
									if(custSID != null && !custSID.isEmpty()) {
										
										membership = contactsLoyaltyDao.getContactsLoyaltyByCustId(custSID, user.getUserId());
										if(membership == null){
											logger.debug("===nothing to do - no membershipfound for==="+custSID);
											statusInfo=new StatusInfo("111616", PropertyUtil.getErrorMessage(111616,OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
											couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
											return couponCodeRedeemedResponse;
											
										}
										
										
									}else if((email != null && !email.isEmpty()) || (phone != null && !phone.isEmpty()) ){
										
										membership = contactsLoyaltyDao.findBy(user, email, phone);
										if(membership == null){
											logger.debug("===nothing to do - no membershipfound for==="+phone+" and "+email);
											statusInfo=new StatusInfo("200009", PropertyUtil.getErrorMessage(200009,OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
											couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
											return couponCodeRedeemedResponse;
											
										}
									}
									
								}else {
									
									membership = contactsLoyaltyDao.getContactsLoyaltyByCardId(cardNUmber, user.getUserId());
								}
							}
							if(membership!=null) {	
								
								logger.info("membership cardnumber "+membership.getCardNumber());
								List<LoyaltyTransactionChild> childTrx = null;
								ContactsLoyaltyDaoForDML ltyDML = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
								LoyaltyTransactionChildDao childDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
								LoyaltyTransactionChild trxChildObj= null;
								
								childTrx = childDao.getOriginalRedemptionTrx(user.getUserId(),membership.getLoyaltyId(),docSID);
								
								if(childTrx!=null) {
									
									long pointsToAdd = 0;
									double rewardToAdd = 0;
									for(LoyaltyTransactionChild trx : childTrx) {
										logger.info("entered amount type "+trx.getEnteredAmountType());
										if(trx.getEnteredAmountType()!=null && !trx.getEnteredAmountType().isEmpty() && 
												trx.getEnteredAmountType().equals(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM)) {
											
											logger.info("inside points redemption ");
											pointsToAdd += Long.parseLong(trx.getPointsDifference().replace("-",""));
											trxChildObj = trx;
											trxChildObj.setTransactionType("VoidRedemption");
											trxChildObj.setEnteredAmountType("VoidPointRedeem");
											
										}else if(trx.getValueCode()!=null && !trx.getValueCode().isEmpty() && 
												trx.getValueCode().equalsIgnoreCase(redeemedCouponCode.getValueCode())) {
											
											logger.info("inside value code redemption ");
											rewardToAdd += Double.parseDouble(trx.getRewardDifference().replace("-",""));
											trxChildObj = trx;
											trxChildObj.setTransactionType("VoidRedemption");
											trxChildObj.setEnteredAmountType("VoidRewardRedeem");
											
										}
									}
									if(pointsToAdd>0) {
										membership.setLoyaltyBalance(membership.getLoyaltyBalance()+pointsToAdd);
										membership.setTotalLoyaltyRedemption(membership.getTotalLoyaltyRedemption()-pointsToAdd);
										ltyDML.saveOrUpdate(membership);
									}
									
									if(rewardToAdd>0) {
										LoyaltyBalanceDaoForDML ltyBlncDML = (LoyaltyBalanceDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_BALANCE_DAO_FOR_DML);
										LoyaltyBalanceDao ltyBlncDao = (LoyaltyBalanceDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
										LoyaltyBalance loyaltyBalance = ltyBlncDao.findBy(user.getUserId(),membership.getLoyaltyId(),redeemedCouponCode.getValueCode());
										 
										if(loyaltyBalance!=null) {
											loyaltyBalance.setBalance(loyaltyBalance.getBalance()+pointsToAdd);
											loyaltyBalance.setTotalRedeemedBalance(loyaltyBalance.getTotalRedeemedBalance()-pointsToAdd);
											ltyBlncDML.saveOrUpdate(loyaltyBalance);
										}
									}
									
									//insert expiry trx
									createExpiryTransaction(membership,pointsToAdd,rewardToAdd);
									
									//updating redemption transaction
									LoyaltyTransactionChildDaoForDML loyaltyTransChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
									if(trxChildObj!=null)
										loyaltyTransChildDaoForDML.saveOrUpdate(trxChildObj);
									
									
								}
								
							}
						}
						
					}
					try {
						CouponCodesDaoForDML couponCodesDaoForDML=(CouponCodesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPONCODES_DAOForDML);
						//Making coupon code to redeem i.e changing status from 'Redeemed' to'Active'
						redeemedCouponCode.setStatus("Active");
						redeemedCouponCode.setRedeemedOn(null);
						redeemedCouponCode.setTotDiscount(null);
						redeemedCouponCode.setTotRevenue(null);
						redeemedCouponCode.setUsedLoyaltyPoints(null);
						redeemedCouponCode.setDocSid(null);
						redeemedCouponCode.setRedeemedTo(null);
						redeemedCouponCode.setRedeemCustId(null);
						redeemedCouponCode.setRedeemEmailId(null);
						redeemedCouponCode.setRedeemPhnId(null);
						redeemedCouponCode.setStoreNumber(null);
						redeemedCouponCode.setReceiptAmount(null);
						redeemedCouponCode.setReceiptNumber(null);
						redeemedCouponCode.setValueCode(null);
						
						couponCodesDaoForDML.saveOrUpdate(redeemedCouponCode);
						
					}catch(Exception e) {
						
					}
					logger.info("redeemedCouponCode status after update ==== "+redeemedCouponCode.getStatus());
					//logger.info("redeemedCouponCodeDup status ==== "+redeemedCouponCode.getStatus());
					
					//Success response
					statusInfo=new StatusInfo("0","Promo reverted sucessfully", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
					 logger.debug("-------exit void  processRedeemedRequest---------"+coupObj.getLoyaltyPoints()+"======");
					 return couponCodeRedeemedResponse;
					
				}else {//docs id is null
					logger.info("docsId is null in void promo redemption");
					statusInfo=new StatusInfo("100000", PropertyUtil.getErrorMessage(100000,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
					return couponCodeRedeemedResponse;
				}
				
			}
			
			double totalDisForIndia = 0;
			if(totalDiscount==1 && user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA)) {//APP-3667
				CouponCodeProcessHelper couponHelper = new CouponCodeProcessHelper();
				totalDisForIndia = couponHelper.getTotalDiscValue(user, coupObj, couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ());
				//totalDiscount = totalDisForIndia>0 ? totalDisForIndia : totalDiscount;
				logger.info("totalDisForIndia 1>>>"+totalDisForIndia+" & totalDiscount 1>>"+totalDiscount);
				
			}
			
			statusInfo=saveCouponCodesObj(coupObj,couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ(),user,orgId,totalAmount,totalDiscount,redeemStr,couponCodes,null,null,null);
			
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
				couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
				return couponCodeRedeemedResponse;
			}
			
			couponCodeRedeemedResponse=finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			 logger.debug("-------exit  processRedeemedRequest---------"+coupObj.getLoyaltyPoints()+"======");
			 
			 if(coupObj.getLoyaltyPoints() != null &&  coupObj.getLoyaltyPoints() == 1 && 
					 (coupObj.getRequiredLoyltyPoits() != null || coupObj.getMultiplierValue()!=null)) {
				 
				 logger.debug("===about to call===");
				 PerformRedemption executer = new PerformRedemption(coupObj, couponCodes, user, 
						 couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ(), totalDisForIndia);
				 executer.start();
			 }
			return couponCodeRedeemedResponse;
		}catch (Exception e) {
			statusInfo = new StatusInfo("100901",PropertyUtil.getErrorMessage(100901,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			couponCodeRedeemedResponse = finalResponse(headerInfo,couponCodeInfo,purchaseCouponInfo,userDetails,statusInfo);
			logger.error("Exception ::" , e);
			return couponCodeRedeemedResponse;
			/*logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processRedeemedRequest::::: ", e);*/
		}
		/*finally{
			couponCodeRedeemedResponse=finalResponse(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getHEADERINFO(),couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO(),
					couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getPURCHASECOUPONINFO(),couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getUSERDETAILS(),statusInfo);
			return couponCodeRedeemedResponse;
		}*/
	}//processRedeemedRequest
	
	//APP-4766
		private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty, Long expiryPoints,
				Double expiryAmount) {

			LoyaltyTransactionExpiry transaction = null;
			try {

				transaction = new LoyaltyTransactionExpiry();
				transaction.setMembershipNumber(Constants.STRING_NILL + loyalty.getCardNumber());
				transaction.setMembershipType(loyalty.getMembershipType());
				transaction.setCreatedDate(Calendar.getInstance());
				transaction.setOrgId(loyalty.getOrgId());
				transaction.setUserId(loyalty.getUserId());
				transaction.setExpiryPoints(expiryPoints);
				transaction.setExpiryAmount(expiryAmount);
				transaction.setRewardFlag(loyalty.getRewardFlag());
				transaction.setLoyaltyId(loyalty.getLoyaltyId());
				transaction.setProgramId(loyalty.getProgramId());
				transaction.setTierId(loyalty.getProgramTierId());
				
				LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao) ServiceLocator
						.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
				LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML) ServiceLocator
						.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
				
				loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);

			} catch (Exception e) {
				logger.error("Exception while logging enroll transaction...", e);
			}
			return transaction;
		}

	
/*	private StatusInfo validateCouponByDocsid(String docSidStr, Coupons coupObj, String reqCoupcode) {
		logger.debug("-------entered validateCouponByDocsid---------");
		StatusInfo statusInfo = null;
		try {
			CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			CouponCodes couponCode = null;
        	if(coupObj.getCouponGeneratedType().equalsIgnoreCase(Constants.COUP_GENT_TYPE_SINGLE)) {
        		couponCode = couponCodesDao.findByDocSid(docSidStr,Constants.COUP_GENT_TYPE_SINGLE, reqCoupcode);
        	}
        	else {
        		couponCode = couponCodesDao.findByDocSid(docSidStr,Constants.COUP_GENT_TYPE_MULTIPLE, reqCoupcode);
        	}
			if(couponCode != null) {
				statusInfo = new StatusInfo("100022", PropertyUtil.getErrorMessage(100022,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		 logger.debug("-------exit  validateCouponByDocsid---------");
		return statusInfo;
	}*/

	private CouponCodes setCouponCodes(CouponCodes couponCodes,Coupons coupObj,long orgId,String reqCoupcode,String redeemStr, Long contactId) throws BaseServiceException {
		logger.debug("-------entered setCouponCodes---------");
		couponCodes = new CouponCodes();
		couponCodes.setCouponId(coupObj);
		couponCodes.setOrgId(orgId);
		couponCodes.setCouponCode(reqCoupcode);
		couponCodes.setContactId(contactId);
		 logger.debug("-------exit  setCouponCodes---------");
		return couponCodes;
	}//setCouponCodes

	private StatusInfo validatePromoListSize(Coupons coupObj,String customerIdStr,String emailStr,String mobileStr,String reqCoupcode,long orgId) throws BaseServiceException {
		StatusInfo statusInfo=null;
		try {
			logger.debug("-------entered validatePromoListSize---------");
			CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			if(coupObj.getSingPromoContRedmptLimit() != null && 
					(customerIdStr != null && !customerIdStr.trim().isEmpty() ||
					emailStr != null && !emailStr.trim().isEmpty() || 
					mobileStr != null && !mobileStr.trim().isEmpty())) {
				List<CouponCodes> promoList = couponCodesDao.isPromoExistForRedeem(customerIdStr,emailStr,mobileStr,reqCoupcode,orgId);
				if(promoList != null && promoList.size() >= coupObj.getSingPromoContRedmptLimit()){
					statusInfo=new StatusInfo("100020", PropertyUtil.getErrorMessage(100020,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return statusInfo;
				}
			}
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing validatePromoListSize::::: ", e);
		}
		 logger.debug("-------exit  validatePromoListSize---------");
		return statusInfo;
	}//validatePromoListSize

	private StatusInfo validatePromo(CouponCodes couponCodes,Coupons coupObj)
				throws BaseServiceException  {
			logger.debug("-------entered validatePromo---------");
			StatusInfo statusInfo=null;
				statusInfo=new StatusInfo("100930", PropertyUtil.getErrorMessage(100930,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				logger.debug("-------exit  validatePromo---------");
				return statusInfo;
				/*if(couponCodes == null && !coupObj.getRedemedAutoChk()) {
			}
			return statusInfo;*/
		}//validatePromo

	/*private boolean getNewCC(Contacts contObj,Users user,Coupons coupObj,long orgId,String reqCoupcode,CouponCodes couponCodes) throws BaseServiceException {
		boolean newCC=false;
		if(contObj!=null) {
			contObj.setUsers(user);
			List<Contacts> contactList = getAllContactsByOrg(orgId, contObj);
			newCC = validateContactList(contactList,coupObj);
			couponCodes  = getCouponCodes(contactList,orgId,reqCoupcode);
			newCC = validateCouponCodes(couponCodes,coupObj);
		}
		return newCC;
	}//getNewCC
*/
	
	
	private Coupons getCouponObjcouponcode(
			CouponCodes couponCodes,String reqCoupcode,long orgId) throws BaseServiceException {
		Coupons coupObj =null;
		try {
			logger.debug("-------entered getCouponObj---------");
			CouponsDao couponsDao=(CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			if(couponCodes == null){
				coupObj = couponsDao.findCoupon(reqCoupcode,orgId);

			}else{
				coupObj =couponCodes.getCouponId();
			}
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getCouponObj::::: ", e);
		}
		logger.debug("-------exit  getCouponObj---------");
		return coupObj;
	}//getCouponObj
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private Coupons getCouponObj(
			ReferralcodesIssued refecodesissued,String reqCoupcode,long orgId) throws BaseServiceException {
		refcodesredeem=true;
		Coupons coupObj =null;
		ReferralcodesIssuedDao referralCodesDao=null;
		try {
			referralCodesDao = (ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//	ReferralcodesIssued	refCodeObj = referralCodesDao.testForrefCodes(reqCoupcode, orgId);
		try {
			logger.debug("-------entered getCouponObj---------");
			CouponsDao couponsDao=(CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			if(refecodesissued == null){
				
				coupObj = couponsDao.findCoupon(reqCoupcode,orgId);
				logger.info("coupObj value in if"+coupObj);
		}else{
				coupObj =refecodesissued.getCouponId();
				logger.info("entering else flow"+coupObj);
			}
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getCouponObj::::: ", e);
		}
		logger.debug("-------exit  getCouponObj---------");
		logger.info("coupObj value is"+coupObj);

		return coupObj;
		
	}//getCouponObj

	private CouponCodeRedeemedResponse finalResponse(HeaderInfo headerInfo,CouponCodeInfo couponCodeInfo,PurchaseCouponInfo purchaseCouponInfo,UserDetails userDetails,StatusInfo statusInfo) throws BaseServiceException {
		logger.debug("-------entered finalResponse---------");
		CouponCodeRedeemedResponse couponCodeRedeemedResponse=new CouponCodeRedeemedResponse();
		CouponCodeRedeemResponse couponCodeRedeemResponse=new CouponCodeRedeemResponse();
		couponCodeRedeemResponse.setCOUPONCODEINFO(couponCodeInfo);
		couponCodeRedeemResponse.setHEADERINFO(headerInfo);
		couponCodeRedeemResponse.setUSERDETAILS(userDetails);
		if(statusInfo.getSTATUS().equals((OCConstants.JSON_RESPONSE_IGNORED_MESSAGE))) {
			statusInfo=new StatusInfo("0","Promo redeem sucessfully", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		}
		couponCodeRedeemResponse.setSTATUSINFO(statusInfo);
		couponCodeRedeemResponse.setPURCHASECOUPONINFO(purchaseCouponInfo);
		couponCodeRedeemedResponse.setCOUPONCODEREDEEMRESPONSE(couponCodeRedeemResponse);
		logger.debug("-------exit  finalResponse---------");
		return couponCodeRedeemedResponse;
	}//finalResponse

	private StatusInfo validateUsedLoyalty() throws BaseServiceException {
		logger.debug("Error :  Value for Used Loyalty Points  shoulb be a Number in JSON ****");
		StatusInfo statusInfo=new StatusInfo("100929", PropertyUtil.getErrorMessage(100929,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		return statusInfo;
	}//validateUsedLoyalty

	private StatusInfo validateTotDiscount() throws BaseServiceException  {
		logger.debug("Error : Value for Total Discount  shoulb be a Number in JSON ****");
		StatusInfo statusInfo=new StatusInfo("100917",PropertyUtil.getErrorMessage(100917,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		return statusInfo;
	}//validateTotDiscount

	private StatusInfo validateTotAmount() throws BaseServiceException  {
		logger.debug("Error :  Value for Total Ammount  shoulb be a Number in JSON ****");
		StatusInfo statusInfo=new StatusInfo("100916",PropertyUtil.getErrorMessage(100916,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		return statusInfo;
	}//validateTotAmount

	private boolean validateCouponCodes(
			CouponCodes couponCodes, Coupons coupObj) throws BaseServiceException  {
		logger.debug("-------entered validateCouponCodes---------");
		boolean newCC=false;
		if(couponCodes == null ) {
			newCC = true;
		}
		logger.debug("-------exit  validateCouponCodes---------");
		return newCC;
	}//validateCouponCodes

	private CouponCodes getCouponCodes(List<Contacts> contactList, Long orgId,
			String reqCoupcode) throws BaseServiceException {
		CouponCodes couponCodes = null;
		try {
			logger.debug("-------entered getCouponCodes---------");
			CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			String contactIdStr = "";
			for (Contacts contacts : contactList) {
				contactIdStr += contactIdStr.trim().length() == 0 ? ""+contacts.getContactId() :","+contacts.getContactId();
			}
			if(contactIdStr!=null && !contactIdStr.trim().isEmpty()) {
			couponCodes = couponCodesDao.findCoupCodeByContactId(contactIdStr , orgId ,reqCoupcode );
			}
			logger.debug("-------exit  getCouponCodes---------");
			return couponCodes;
		}catch (Exception e) {
		
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getCouponCodes::::: ", e);
		}
	}//getCouponCodes

	private boolean validateContactList(
			List<Contacts> contactList, Coupons coupObj) throws BaseServiceException  {
		logger.debug("-------entered validateContactList---------");
		boolean newCC=false;
		if(contactList == null || contactList.size() == 0){
			newCC = true;
		}
		logger.debug("-------exit  validateContactList---------");
		return newCC;
	}//validateContactList

	private StatusInfo validateRedeemCount(Coupons coupObj,Long redeemdCount) throws BaseServiceException {
		logger.debug("-------entered validateRedeemCount---------");
		StatusInfo statusInfo=null;
		if(coupObj.getRedemedAutoChk()== false &&
				(coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() <= redeemdCount )) {
			statusInfo=new StatusInfo("100921", PropertyUtil.getErrorMessage(100921,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateRedeemCount---------");
		return statusInfo;
	}//validateRedeemCount

	private StatusInfo validateDynamicCoupon(Coupons coupObj,CouponCodes coupCodeObj){
		StatusInfo statusInfo=null;
		logger.debug("-------entered validateDynamicCoupon---------");
	
	if(coupCodeObj!=null) {
		
		if(coupObj == null){
			logger.debug("Error :Promo-code not exists in DB ****");
			statusInfo = new StatusInfo("100012",PropertyUtil.getErrorMessage(100012,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(coupObj.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
			if(coupCodeObj.getStatus().equals(Constants.COUP_STATUS_EXPIRED)){
				statusInfo = new StatusInfo("100015",PropertyUtil.getErrorMessage(100015,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
		}
	}	
		return statusInfo;
	
	
	}
	private StatusInfo validateStore(Coupons coupObj,CouponCodeInfo couponCodeInfo) throws BaseServiceException  {
		logger.debug("-------entered validateStore---------");
		StatusInfo statusInfo=null;
		//Check with store number
		String strorNumberStr = Constants.STRING_NILL;
		String sBSNumberStr = Constants.STRING_NILL;
		logger.debug("-------entered validateStoreNum---------");
		if(coupObj!=null) {
		
	
		if((coupObj.getAllStoreChk() != null && coupObj.getAllStoreChk() == false) ||
				(coupObj.getSelectedStores() != null && coupObj.getSelectedStores().trim().length() > 0)) {
			if(couponCodeInfo.getSTORENUMBER()!=null) {
				strorNumberStr = couponCodeInfo.getSTORENUMBER();
				sBSNumberStr = couponCodeInfo.getSUBSIDIARYNUMBER();
				if(strorNumberStr == null || strorNumberStr.length() == 0) {
					statusInfo = new StatusInfo("100017",PropertyUtil.getErrorMessage(100017,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return statusInfo;
				}
			} else {
				statusInfo = new StatusInfo("100018",PropertyUtil.getErrorMessage(100018,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			//Check with Store
			String seledStoreStr =  coupObj.getSelectedStores();
			String[] storeListArr  = seledStoreStr.split(";=;");
			boolean storeExisted = false;

			/*
			 * Long jsonStoreLong = null; try { jsonStoreLong =
			 * Long.parseLong(strorNumberStr); } catch (Exception e) {
			 * logger.error("Exception ::" , e); //throw new
			 * BaseServiceException("Exception occured while processing validateStoreNum::::: "
			 * , e); }
			 */
			//Long eachStoreLong = null;

			for (String eachstoreToken : storeListArr) {
				
				if(eachstoreToken.contains(Constants.DELIMETER_DOUBLECOLON)) {
					
					String SBS = eachstoreToken.split(Constants.DELIMETER_DOUBLECOLON)[0];
					String store = eachstoreToken.split(Constants.DELIMETER_DOUBLECOLON)[1];
					if(sBSNumberStr == null || sBSNumberStr.isEmpty()) {
						if(store.trim().equals(strorNumberStr.trim())) {
							storeExisted = true ;
							break;
						}
					}else {
						if(store.trim().equals(strorNumberStr.trim()) && SBS.trim().equals(sBSNumberStr.trim())) {
							storeExisted = true ;
							break;
						}
					}
					
				}else {
					if(eachstoreToken.trim().equals(strorNumberStr.trim())) {
						storeExisted = true ;
						break;
					}
					//throw new BaseServiceException("Exception occured while processing validateStoreNum::::: ", e);
				}
			}
			if(!storeExisted) {
				statusInfo = new StatusInfo("100021",PropertyUtil.getErrorMessage(100021,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
		}
		logger.debug("-------exit  validateStore---------");
		}
		
		return statusInfo;
	}//validateStore

	private StatusInfo checkCouponStatus(CouponCodes couponCodes,Coupons coupObj) throws BaseServiceException  {
		logger.debug("-------entered checkCouponStatus---------");
		StatusInfo statusInfo=null;
		if(coupObj.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_STATIC)){
		if(coupObj.getStatus().equals(Constants.COUP_STATUS_ACTIVE) ) {
			statusInfo=new StatusInfo("100931", PropertyUtil.getErrorMessage(100931,OCConstants.ERROR_PROMO_FLAG)+coupObj.getStatus()+")", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
        }
		}
		if(!coupObj.getStatus().equals(Constants.COUP_STATUS_RUNNING) ){
			logger.debug("Error : Promo-code Object cannot be redeemed ****");
			statusInfo=new StatusInfo("100919", PropertyUtil.getErrorMessage(100919,OCConstants.ERROR_PROMO_FLAG)+coupObj.getStatus()+")", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  checkCouponStatus---------");
		return statusInfo;
	}//checkCouponStatus

	private StatusInfo validateCoupObj(Coupons coupObj) throws BaseServiceException  {
		logger.debug("-------entered validateCoupObj---------");
		StatusInfo statusInfo=null;
		if(coupObj == null){
			logger.debug("Error : Promo-code not exist in DB ****");
			statusInfo = new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateCoupObj---------");
	
		logger.info("statusInfo value is"+statusInfo);

		return statusInfo;
		
		
	}//validateCoupObj

	private StatusInfo validatePurchaseCouponInfoDetails(double totalAmount,double totalDiscount,PurchaseCouponInfo purchaseCouponInfo) throws BaseServiceException  {
		logger.debug("-------entered validatePurchaseCouponInfoDetails---------");
		StatusInfo statusInfo=null;
		//commented to allow 100% for Zohaibzaman
		/*if(totalAmount == 0){
			logger.debug("Error : unable to find the  required total amount  details in JSON ****");
			statusInfo=new StatusInfo("100914",PropertyUtil.getErrorMessage(100914,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}*/
		if(totalDiscount == 0 && (purchaseCouponInfo.getTYPE() == null || !purchaseCouponInfo.getTYPE().equalsIgnoreCase("Void"))) {//APP-4766
			logger.debug("Error : unable to find the  required total discount  details in JSON ****");
			statusInfo=new StatusInfo("100915", PropertyUtil.getErrorMessage(100915,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validatePurchaseCouponInfoDetails---------");
		return statusInfo;
	}//validatePurchaseCouponInfoDetails

	private StatusInfo validateCouponCode(String reqCoupcode) throws BaseServiceException {
		logger.debug("-------entered validateCouponCode---------");
		StatusInfo statusInfo=null;
		if(reqCoupcode == null){
			logger.debug("Unable to find the Promo-code  ");
			statusInfo=new StatusInfo("100912", PropertyUtil.getErrorMessage(100912,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateCouponCode---------");
		return statusInfo;
	}//validateCouponCode

	private String getRedeemStr(String custId,String email,String phone,String redeemStr) throws BaseServiceException  {
		logger.debug("-------entered getRedeemStr---------");
		if(custId != null && custId.trim().length() >0){
			redeemStr = "Customer Id:"+custId;
		}
		if(email != null && email.trim().length() >0){
			if(redeemStr != null && redeemStr.trim().length() >0) redeemStr += ";Email:"+email;
			else redeemStr = "Email:"+email;
		}
		if(phone != null && phone.trim().length() >0){
			if(redeemStr != null && redeemStr.trim().length() >0) redeemStr += ";Phone:"+phone;
			else redeemStr = "Phone:"+phone;
		}
		logger.debug("-------exit  getRedeemStr---------");
		return redeemStr;
	}//getRedeemStr

	private StatusInfo validateUser(Users user) throws BaseServiceException  {
		logger.debug("-------entered validateUser---------");
		StatusInfo statusInfo=null;
		if(user == null){
			logger.debug("Unable to find the user Obj");
			statusInfo=new StatusInfo("100910", PropertyUtil.getErrorMessage(100910,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateUser---------");
		return statusInfo;
	}//validateUser

	private StatusInfo validateUserDetails(String token,String userName,String userOrg) throws BaseServiceException  {
		logger.debug("-------entered validateUserDetails---------");
		StatusInfo statusInfo=null;
	
		if(token == null || token.isEmpty()){
			logger.debug("Error : User Token cannot be empty.");
			statusInfo=new StatusInfo("100908", PropertyUtil.getErrorMessage(100908,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(userName == null ||userName.isEmpty()|| userOrg.isEmpty() || userOrg == null){
			logger.debug("Error : Username or organisation cannot be empty.");
			statusInfo=new StatusInfo("100909", PropertyUtil.getErrorMessage(100909,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateUserDetails---------");
		return statusInfo;
	}//validateUserDetails

	private StatusInfo validateRequestId(String requestId) throws BaseServiceException {
		logger.debug("-------entered validateRequestId---------");
		StatusInfo statusInfo=null;
		if(requestId == null || requestId.trim().length() == 0){
			logger.debug("Error : Request ID of HearderInfo is empty in JSON ****");
			statusInfo=new StatusInfo( "100906", PropertyUtil.getErrorMessage(100906,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateRequestId---------");
		return statusInfo;
	}//validateRequestId

	private StatusInfo validateRootObj(
			CouponCodeRedeemedObj couponCodeRedeemedObj) throws BaseServiceException {
		StatusInfo statusInfo=null;
		try {
			logger.debug("-------entered validateRootObj---------");
			if(couponCodeRedeemedObj==null){
				logger.debug("Error : Unable to parse the json.. Returning. ****");
				statusInfo=new StatusInfo("100903",PropertyUtil.getErrorMessage(100903,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ()==null){
				logger.debug("Error : unable to find the Promo-code redeemed info in JSON ****");
				statusInfo=new StatusInfo("100904", PropertyUtil.getErrorMessage(100904,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getHEADERINFO()==null){
				logger.debug("Error : unable to find the HearderInfo in JSON ****");
				statusInfo=new StatusInfo("100905", PropertyUtil.getErrorMessage(100905,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}

			if(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getUSERDETAILS()== null ){
				logger.debug("Error : unable to find the User Details in JSON ****");
				statusInfo=new StatusInfo("100907", PropertyUtil.getErrorMessage(100907,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getCOUPONCODEINFO() == null){
				logger.debug("Unable to find the Promo-code Details ");
				statusInfo=new StatusInfo("100911", PropertyUtil.getErrorMessage(100911,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			if(couponCodeRedeemedObj.getCOUPONCODEREDEEMREQ().getPURCHASECOUPONINFO()== null){
				logger.debug("Error : unable to find the  required purchase details in JSON ****");
				statusInfo=new StatusInfo("100913",  PropertyUtil.getErrorMessage(100913,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing validateRootObj::::: ", e);
		}
		logger.debug("-------exit  validateRootObj---------");
		return statusInfo;
	}//validateRootObj
	
	private List<Contacts> getAllContactsByOrg(Long orgId, Contacts contObj) throws BaseServiceException {
		List<Contacts> contactList = null;
		try {
			logger.debug("-------entered getAllContactsByOrg---------");
			UsersDao usersDao =(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			POSMappingDao posMappingDao=(POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
			ContactsDao contactsDao=(ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			//check the list of Contacts from Organization
			List<Users> orgUserList = usersDao.getUsersListByOrg(orgId);
			Map<Long, TreeMap<String, List<String>>> usersTreeMap = Utility.getPriorityMapByUsersList(orgUserList,Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
			logger.debug("-------exit  getAllContactsByOrg---------");
			return  contactList = contactsDao.findContactsByUserList(contObj, orgUserList, usersTreeMap);
		}catch(Exception e){
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getAllContactsByOrg::::: ", e);
		}
	} // getAllContactsByOrg
	
	private StatusInfo saveCouponCodesObj(Coupons coupObj,CouponCodeRedeemReq couponCodeRedeemReq,Users user,
										long orgId,double totalAmount,double totalDiscount,String redeemStr,CouponCodes couponCodes,
						
										String itemInfo,String membership, Integer redeemReward)throws Exception  {
		
		
		logger.info("entering savcouponcodeobj method ");

		
		StatusInfo statusInfo=null;
		//CouponCodes couponCodes=null;
		ReferralcodesRedeemedDaoForDML refcodesredeemedDaoFoDML= (ReferralcodesRedeemedDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.REFERRAL_CODES_REDEEMED_DAO_FOR_DML);
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);

		ContactsDao contactdao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);

		ReferralcodesRedeemedDao refcoderedDao=	(ReferralcodesRedeemedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_CODES_REDEEMED_DAO);
	
		RewardReferraltypeDao rewardreftypeDao=(RewardReferraltypeDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REWARD_REFERRAL_TYPE_DAO);
	
		ReferralProgramDao refprgmDao=(ReferralProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_PROGRAM_DAO);
		
		List<RewardReferraltype> Rewardrefobj=null;
		
		ReferralProgram Refprgrmobj=null;
		
		ReferralcodesIssuedDao referralCodesDao=(ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
		String docSidStr = couponCodeRedeemReq.getCOUPONCODEINFO().getDOCSID();
		String custId = couponCodeRedeemReq.getCOUPONCODEINFO().getCUSTOMERID();
		String emailId = couponCodeRedeemReq.getCOUPONCODEINFO().getEMAIL();
		String phone = couponCodeRedeemReq.getCOUPONCODEINFO().getPHONE();
		String cardNumber = couponCodeRedeemReq.getCOUPONCODEINFO().getCARDNUMBER();
		String docsid=couponCodeRedeemReq.getCOUPONCODEINFO().getDOCSID();
		
		ReferralcodesIssued  refCodeObj=null;
		
		String reqCoupcode= couponCodeRedeemReq.getCOUPONCODEINFO().getCOUPONCODE();

		try{
		refCodeObj = referralCodesDao.testForrefCodes(reqCoupcode, orgId);
		}catch(Exception e) {
			
			logger.info(" Exception value "+e);

		}
		logger.info(" refcodesredeem value "+refcodesredeem);

		if(coupObj!=null && !refcodesredeem) {
			
			logger.info("entering coupon flow");

			try {
			logger.debug("-------entered saveCouponCodesObj---------");
			CouponCodeProcessHelper couponCodeProcessHelper=new CouponCodeProcessHelper();
			CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			CouponCodesDaoForDML couponCodesDaoForDML=(CouponCodesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPONCODES_DAOForDML);
			CouponsDao couponsDao=(CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			CouponsDaoForDML couponsDaoForDML=(CouponsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPONS_DAOForDML);
		
			
			
			
			
			double usedLoyalty = ((redeemReward == null || redeemReward == 0) ? (coupObj.getRequiredLoyltyPoits() != null ? coupObj.getRequiredLoyltyPoits() : 0):
									redeemReward);
			Long contactId = null;
			if(coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_SINGLE)) {
				couponCodes  =  null;
				/*if(coupObj.getLoyaltyPoints() != null && 
						couponCodeRedeemReq.getPURCHASECOUPONINFO().getUSEDLOYALTYPOINTS().trim().length() > 0){
					try {
						usedLoyalty = Double.parseDouble(couponCodeRedeemReq.getPURCHASECOUPONINFO().getUSEDLOYALTYPOINTS());
					} catch (NumberFormatException e) {
						statusInfo=validateUsedLoyalty(); 
						if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
							return statusInfo;
						}
					}
				}*/
				
                //Check for same docsid
				boolean isStakableDiscount = false;
            /*    if(docSidStr != null && !docSidStr.isEmpty()) {
                	couponCodes = couponCodesDao.findByDocSid(docSidStr, couponCodeRedeemReq.getCOUPONCODEINFO().getCOUPONCODE(), orgId);
                	if(couponCodes != null) {
                		if(coupObj.isStackable()) {
                			isStakableDiscount = true;
                			
                		}
                		
                		if(!isStakableDiscount){
                			coupObj.setTotRevenue(coupObj.getTotRevenue() != null ? coupObj.getTotRevenue().longValue()-couponCodes.getTotRevenue().doubleValue()+totalAmount : totalAmount);
                			couponCodes.setTotDiscount(totalDiscount);
                			couponCodes.setTotRevenue(totalAmount);
                			couponCodes.setUsedLoyaltyPoints(usedLoyalty);
                			if(docSidStr != null && docSidStr.trim().length() > 0) couponCodes.setDocSid(docSidStr); 

                			if(redeemStr != null && redeemStr.trim().length() > 0)
                				couponCodes.setRedeemedTo(redeemStr);
                			if(custId != null && custId.trim().length() > 0)
                				couponCodes.setRedeemCustId(custId);
                			if(emailId != null && emailId.trim().length() > 0)
                				couponCodes.setRedeemEmailId(emailId);
                			if(phone != null && phone.trim().length() > 0)
                				couponCodes.setRedeemPhnId(phone);

                			logger.debug("CouponCode  Obj is ::"+couponCodes);
                			//couponCodesDao.saveOrUpdate(couponCodes);
                			couponCodesDaoForDML.saveOrUpdate(couponCodes);
                			//couponsDao.saveOrUpdate(coupObj);
                			couponsDaoForDML.saveOrUpdate(coupObj);
                			if(coupObj.getLoyaltyPoints()!=null)
              	               couponsDaoForDML.updateUsedLoyaltyPoint(coupObj.getCouponId());
                			statusInfo=new StatusInfo("0", "Promo updated successfully", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
                			return statusInfo;
                		}
                	}
                }*/
				
                if(coupObj.isStackable()) {
        			isStakableDiscount = true;
        		}
				long redeemdCount = couponCodesDao.findRedeemdCoupCodeByCoup(coupObj.getCouponId(), orgId, Constants.COUP_CODE_STATUS_REDEEMED);
				statusInfo=validateRedeemCount(coupObj,redeemdCount);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
					return statusInfo;
				}
				boolean newCC=false;
				Contacts inputcontObj = null;
				Contacts dbContactObj = null;
				inputcontObj=couponCodeProcessHelper.getContactObj(couponCodeRedeemReq.getCOUPONCODEINFO().getEMAIL(), 
						couponCodeRedeemReq.getCOUPONCODEINFO().getPHONE(), couponCodeRedeemReq.getCOUPONCODEINFO().getCUSTOMERID(), inputcontObj, user);
				//boolean newCC=getNewCC(contObj,user,coupObj,orgId,couponCodeRedeemReq.getCOUPONCODEINFO().getCOUPONCODE(),couponCodes);
				if(inputcontObj!=null) {
					inputcontObj.setUsers(user);
					dbContactObj = couponCodeProcessHelper.findContactByUserId(user.getUserId(), inputcontObj, user);
	                   
                    if(dbContactObj != null) { 
                    	
                    	contactId = dbContactObj.getContactId();
                    }else{
                    	String custSID = couponCodeRedeemReq.getCOUPONCODEINFO().getCUSTOMERID();
                    	if(custSID != null && !custSID.isEmpty() ) {
                    		
                    		PurgeList purgeList = (PurgeList)ServiceLocator.getInstance().getServiceById(OCConstants.PURGELIST);
                    		MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance()
                    				.getDAOByName(OCConstants.MAILINGLIST_DAO);
                    		MailingList mlList = mailingListDao.findPOSMailingList(user);
                    		
                    		dbContactObj = new Contacts();
                    		logger.info("In Validate Contact method dbContact = "+inputcontObj);
                    		//dbContact.setEmailId(jsonContact.getEmailId());
                    		dbContactObj.setMlBits(mlList.getMlBit());
                    		dbContactObj.setUsers(user);
                    		dbContactObj.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
                    		dbContactObj.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
                    		dbContactObj.setCreatedDate(Calendar.getInstance());
                    		
                    		dbContactObj.setExternalId(couponCodeRedeemReq.getCOUPONCODEINFO().getCUSTOMERID());
                    		dbContactObj.setPurged(false);
                    		purgeList.checkForValidDomainByEmailId(dbContactObj);
                    		//dbContactObj.setMobilePhone(dbContact.getMobilePhone() == null ? null : Utility.phoneParse(dbContact.getMobilePhone(),user!=null ? user.getUserOrganization() : null ));
                    		dbContactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
                    		dbContactObj.setMobileOptin(false);
                    		
                    		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
                    		contactsDaoForDML.saveOrUpdate(dbContactObj);
                    		
                    		contactId = dbContactObj.getContactId();
                    		
                    		MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
                    			
                    			mlList.setListSize(mlList.getListSize() + 1);
                    			mlList.setLastModifiedDate(Calendar.getInstance());
                    			mailingListDaoForDML.saveOrUpdate(mlList);
                    	}
                    	
                    }
					
					
					List<Contacts> contactList = getAllContactsByOrg(orgId, inputcontObj);
					newCC = validateContactList(contactList,coupObj);
					couponCodes  = getCouponCodes(contactList,orgId,couponCodeRedeemReq.getCOUPONCODEINFO().getCOUPONCODE());
					newCC = validateCouponCodes(couponCodes,coupObj);
				}else if(couponCodes == null && !coupObj.getRedemedAutoChk()) {
                    //TODO send error message couponcode not avaialable
	            	if(coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() > redeemdCount){
	            		newCC = true; 
	            	}else {
	            		statusInfo=validatePromo(couponCodes,coupObj);
	    				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
	    					return statusInfo;
	    				}
	            	}
	            	
	             }else if(couponCodes == null && coupObj.getRedemedAutoChk()){
                	 logger.info("newcc value true setting here  ... ");
                	 newCC = true; 
                 }
				logger.info("newCC--------------"+newCC);
				if(newCC || isStakableDiscount) {
					logger.info(">>>>>>>>>new couponcode>>>>>>>>>");
					statusInfo=validatePromoListSize(coupObj,custId,emailId,phone,couponCodeRedeemReq.getCOUPONCODEINFO().getCOUPONCODE(),orgId);
					if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getSTATUS())){
						return statusInfo;
					}
					couponCodes=setCouponCodes(couponCodes,coupObj,orgId,couponCodeRedeemReq.getCOUPONCODEINFO().getCOUPONCODE(),redeemStr, contactId);
					int succecount = couponsDaoForDML.updateTotalCountForNewCouponCodeRedumtion(coupObj.getCouponId(), 1);
					if(succecount > 0) {
						coupObj.setTotalQty(coupObj.getTotalQty()+1);
					}
				}
				String storeNO = couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER()!=null ? couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER().trim().isEmpty()?null:couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER().trim():null;
				
				if(storeNO == null) {
					
					storeNO = couponCodeRedeemReq.getHEADERINFO().getSTORENUMBER()!=null ? couponCodeRedeemReq.getHEADERINFO().getSTORENUMBER().trim().isEmpty()?null:couponCodeRedeemReq.getHEADERINFO().getSTORENUMBER().trim():null;
				}
				couponCodes.setStoreNumber(storeNO);
				couponCodes.setSubsidiaryNumber(couponCodeRedeemReq.getCOUPONCODEINFO().getSUBSIDIARYNUMBER()!=null ? couponCodeRedeemReq.getCOUPONCODEINFO().getSUBSIDIARYNUMBER().trim().isEmpty()?null:couponCodeRedeemReq.getCOUPONCODEINFO().getSUBSIDIARYNUMBER().trim():null);
				couponCodes.setReceiptNumber(couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTNUMBER()!=null ? couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTNUMBER().trim().isEmpty()?null:couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTNUMBER().trim():null);
				couponCodes.setReceiptAmount(couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTAMOUNT()!=null ? couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTAMOUNT().trim().isEmpty()?null:Double.parseDouble(couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTAMOUNT().trim()):null);
				
				//couponCodes.setStoreNumber(couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER()!=null ? couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER().trim().isEmpty()?null:couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER().trim():null);
				couponCodes.setSourceType(couponCodeRedeemReq.getHEADERINFO().getSOURCETYPE()!=null ? couponCodeRedeemReq.getHEADERINFO().getSOURCETYPE().trim().isEmpty()?null:couponCodeRedeemReq.getHEADERINFO().getSOURCETYPE().trim():null);
				couponCodes.setStatus(Constants.COUP_CODE_STATUS_REDEEMED);
				couponCodes.setRedeemedOn(Calendar.getInstance());
				if(custId!=null && custId.trim().length() > 0 ) {
					couponCodes.setRedeemCustId(custId);
				}
				if(emailId!=null && emailId.trim().length() > 0 ) {
					couponCodes.setRedeemEmailId(emailId);
				}
				if(phone!=null && phone.trim().length() > 0 ) {
					couponCodes.setRedeemPhnId(phone);
				}
				couponCodes.setTotDiscount(totalDiscount);
				couponCodes.setTotRevenue(totalAmount);
				Double totRevOnThisRecpt = 0.0;
				if(isStakableDiscount) {
					
					totRevOnThisRecpt = couponCodesDao.getTotRevOnThisRecpt(coupObj.getCouponId(), docSidStr);
					//couponCodesDao.updateRevenueOnStackableTrx(coupObj.getCouponId(), docSidStr);
					couponCodesDaoForDML.updateRevenueOnStackableTrx(coupObj.getCouponId(), docSidStr);
				}
				
				couponCodes.setUsedLoyaltyPoints(usedLoyalty);
				if(couponCodeRedeemReq.getCOUPONCODEINFO().getDOCSID() != null && couponCodeRedeemReq.getCOUPONCODEINFO().getDOCSID().trim().length() > 0) couponCodes.setDocSid(couponCodeRedeemReq.getCOUPONCODEINFO().getDOCSID()); 
				couponCodes.setRedeemedTo(redeemStr);
				if(itemInfo!=null) couponCodes.setItemInfo(itemInfo);
				if(membership!=null) couponCodes.setMembership(membership);
				couponCodes.setValueCode(coupObj.getValueCode() == null ? OCConstants.LOYALTY_POINTS : coupObj.getValueCode());
				//couponCodes.setUsedLoyaltyPoints(Double.valueOf(coupObj.getRequiredLoyltyPoits()));
				//couponCodesDao.saveOrUpdate(couponCodes);
				couponCodesDaoForDML.saveOrUpdate(couponCodes);
				long allCount = couponCodesDao.findIssuedCoupCodeByCoup(coupObj.getCouponId());
				coupObj.setRedeemed(coupObj.getRedeemed()!= null ? coupObj.getRedeemed().longValue()+1 : 1);
				if(!isStakableDiscount){
					coupObj.setTotRevenue(coupObj.getTotRevenue() != null ? coupObj.getTotRevenue().longValue()+totalAmount : totalAmount);
				}else{
					
					coupObj.setTotRevenue(coupObj.getTotRevenue() != null ? coupObj.getTotRevenue().longValue()-totRevOnThisRecpt+totalAmount: totalAmount);
				}
				if(coupObj.getTotalQty() != null) {
					long availCount = coupObj.getTotalQty().longValue()-allCount;
					if(availCount < 0) availCount = 0;
					coupObj.setAvailable(availCount);
				}
			//	logger.info(">>>>>>discount & revenue>>>>>>"+coupObj.getTotDiscount()+"-----"+coupObj.getTotRevenue());
				//couponsDao.saveOrUpdate(coupObj);
				couponsDaoForDML.saveOrUpdate(coupObj);
				logger.info(" Coupon Used Loyalty used :"+coupObj.getLoyaltyPoints());
				if(coupObj.getLoyaltyPoints()!=null)
				couponsDaoForDML.updateUsedLoyaltyPoint(coupObj.getCouponId());
				statusInfo=new StatusInfo("0","Promo redeem sucessfully", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				sendEmailForReferralProgram(couponCodes,user);
			}
			else {
				//Check for same docsid
				
				Contacts inputcontObj = null;
				Contacts dbContactObj = null;
				inputcontObj=couponCodeProcessHelper.getContactObj(couponCodeRedeemReq.getCOUPONCODEINFO().getEMAIL(), 
						couponCodeRedeemReq.getCOUPONCODEINFO().getPHONE(), couponCodeRedeemReq.getCOUPONCODEINFO().getCUSTOMERID(), inputcontObj, user);
				//boolean newCC=getNewCC(contObj,user,coupObj,orgId,couponCodeRedeemReq.getCOUPONCODEINFO().getCOUPONCODE(),couponCodes);
				if(inputcontObj!=null) {
					inputcontObj.setUsers(user);
					dbContactObj = couponCodeProcessHelper.findContactByUserId(user.getUserId(), inputcontObj, user);
	                   
                    if(dbContactObj != null) { 
                    	
                    	contactId = dbContactObj.getContactId();
                    }else{
                    	String custSID = couponCodeRedeemReq.getCOUPONCODEINFO().getCUSTOMERID();
                    	if(custSID != null && !custSID.isEmpty() ) {
                    		
                    		PurgeList purgeList = (PurgeList)ServiceLocator.getInstance().getServiceById(OCConstants.PURGELIST);
                    		MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance()
                    				.getDAOByName(OCConstants.MAILINGLIST_DAO);
                    		MailingList mlList = mailingListDao.findPOSMailingList(user);
                    		
                    		dbContactObj = new Contacts();
                    		logger.info("In Validate Contact method dbContact = "+inputcontObj);
                    		//dbContact.setEmailId(jsonContact.getEmailId());
                    		dbContactObj.setMlBits(mlList.getMlBit());
                    		dbContactObj.setUsers(user);
                    		dbContactObj.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
                    		dbContactObj.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
                    		dbContactObj.setCreatedDate(Calendar.getInstance());
                    		
                    		dbContactObj.setExternalId(couponCodeRedeemReq.getCOUPONCODEINFO().getCUSTOMERID());
                    		dbContactObj.setPurged(false);
                    		purgeList.checkForValidDomainByEmailId(dbContactObj);
                    		//dbContactObj.setMobilePhone(dbContact.getMobilePhone() == null ? null : Utility.phoneParse(dbContact.getMobilePhone(),user!=null ? user.getUserOrganization() : null ));
                    		dbContactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
                    		dbContactObj.setMobileOptin(false);
                    		
                    		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
                    		contactsDaoForDML.saveOrUpdate(dbContactObj);
                    		
                    		contactId = dbContactObj.getContactId();
                    		
                    		MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
                    			
                    			mlList.setListSize(mlList.getListSize() + 1);
                    			mlList.setLastModifiedDate(Calendar.getInstance());
                    			mailingListDaoForDML.saveOrUpdate(mlList);
                    	}
                    	
                    }
					
					
				}
				
				
				
				
                if(docSidStr != null && !docSidStr.isEmpty()) {
                	if(docSidStr.equalsIgnoreCase(couponCodes.getDocSid()) && 
                			couponCodes.getStatus().equalsIgnoreCase(Constants.COUP_CODE_STATUS_REDEEMED)) {
                		coupObj.setTotRevenue(coupObj.getTotRevenue() != null ? 
     	                		coupObj.getTotRevenue().longValue()-couponCodes.getTotRevenue().doubleValue()+totalAmount : totalAmount);
     	                couponCodes.setTotDiscount(totalDiscount);
     	                couponCodes.setTotRevenue(totalAmount);
     	                couponCodes.setUsedLoyaltyPoints(usedLoyalty);
     	                if(docSidStr != null && docSidStr.trim().length() > 0) couponCodes.setDocSid(docSidStr); 
     	                
     	                if(redeemStr != null && redeemStr.trim().length() > 0)
     	                		couponCodes.setRedeemedTo(redeemStr);
     	                if(custId != null && custId.trim().length() > 0)
     	                		couponCodes.setRedeemCustId(custId);
     	                if(emailId != null && emailId.trim().length() > 0)
     	                		couponCodes.setRedeemEmailId(emailId);
     	                if(phone != null && phone.trim().length() > 0)
     	                		couponCodes.setRedeemPhnId(phone);
     	                
     	                logger.debug("CouponCode  Obj is ::"+couponCodes);
     	                /*couponCodes.setRedeemedTo(redeemStr);
     					if(itemInfo!=null) couponCodes.setItemInfo(itemInfo);
					if(membership!=null) couponCodes.setMembership(membership);
     					couponCodes.setValueCode(coupObj.getValueCode() == null ? OCConstants.LOYALTY_POINTS : coupObj.getValueCode());
     					couponCodes.setUsedLoyaltyPoints(Double.valueOf(coupObj.getRequiredLoyltyPoits()));*/
     	                //couponCodesDao.saveOrUpdate(couponCodes);
     	                couponCodesDaoForDML.saveOrUpdate(couponCodes);
     	                //couponsDao.saveOrUpdate(coupObj);
     	                couponsDaoForDML.saveOrUpdate(coupObj);
     	               if(coupObj.getLoyaltyPoints()!=null)
     	               couponsDaoForDML.updateUsedLoyaltyPoint(coupObj.getCouponId());
     	                statusInfo=new StatusInfo("0", "Promo updated successfully", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
     	                return statusInfo;
                	}
                }
				
				if(couponCodes != null && couponCodes.getStatus()!= null && 
						couponCodes.getStatus().equals(Constants.COUP_CODE_STATUS_REDEEMED) &&
						coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_MULTIPLE)){
					logger.debug("Promo-code already redeemed in ");
					statusInfo=new StatusInfo("100920", PropertyUtil.getErrorMessage(100920,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return statusInfo;
				}
				String storeNO = couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER()!=null ? couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER().trim().isEmpty()?null:couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER().trim():null;
				
				if(storeNO == null) {
					
					storeNO = couponCodeRedeemReq.getHEADERINFO().getSTORENUMBER()!=null ? couponCodeRedeemReq.getHEADERINFO().getSTORENUMBER().trim().isEmpty()?null:couponCodeRedeemReq.getHEADERINFO().getSTORENUMBER().trim():null;
				}
				couponCodes.setStoreNumber(storeNO);
				couponCodes.setSubsidiaryNumber(couponCodeRedeemReq.getCOUPONCODEINFO().getSUBSIDIARYNUMBER()!=null ? couponCodeRedeemReq.getCOUPONCODEINFO().getSUBSIDIARYNUMBER().trim().isEmpty()?null:couponCodeRedeemReq.getCOUPONCODEINFO().getSUBSIDIARYNUMBER().trim():null);
				couponCodes.setReceiptNumber(couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTNUMBER()!=null ? couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTNUMBER().trim().isEmpty()?null:couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTNUMBER().trim():null);
				couponCodes.setReceiptAmount(couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTAMOUNT()!=null ? couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTAMOUNT().trim().isEmpty()?null:Double.parseDouble(couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTAMOUNT().trim()):null);
				couponCodes.setSourceType(couponCodeRedeemReq.getHEADERINFO().getSOURCETYPE()!=null ? couponCodeRedeemReq.getHEADERINFO().getSOURCETYPE().trim().isEmpty()?null:couponCodeRedeemReq.getHEADERINFO().getSOURCETYPE().trim():null);
				couponCodes.setStatus(Constants.COUP_CODE_STATUS_REDEEMED);
				couponCodes.setRedeemedOn(Calendar.getInstance());
				if(couponCodes.getContactId() == null) couponCodes.setContactId(contactId);
				if(custId!=null && custId.trim().length() > 0 ) {
					couponCodes.setRedeemCustId(custId);
				}
				if(emailId!=null && emailId.trim().length() > 0 ) {
					couponCodes.setRedeemEmailId(emailId);
				}
				if(phone!=null && phone.trim().length() > 0 ) {
					couponCodes.setRedeemPhnId(phone);
				}
				couponCodes.setTotDiscount(totalDiscount);
				couponCodes.setTotRevenue(totalAmount);
				couponCodes.setUsedLoyaltyPoints(usedLoyalty);
				if(couponCodeRedeemReq.getCOUPONCODEINFO().getDOCSID() != null && couponCodeRedeemReq.getCOUPONCODEINFO().getDOCSID().trim().length() > 0) couponCodes.setDocSid(couponCodeRedeemReq.getCOUPONCODEINFO().getDOCSID()); 
				couponCodes.setRedeemedTo(redeemStr);
				couponCodes.setRedeemedTo(redeemStr);
				if(itemInfo!=null) couponCodes.setItemInfo(itemInfo);
				if(membership!=null) couponCodes.setMembership(membership);
				couponCodes.setValueCode(coupObj.getValueCode() == null ? OCConstants.LOYALTY_POINTS : coupObj.getValueCode());
				//couponCodes.setUsedLoyaltyPoints(Double.valueOf(coupObj.getRequiredLoyltyPoits()));
				//couponCodesDao.saveOrUpdate(couponCodes);
				couponCodesDaoForDML.saveOrUpdate(couponCodes);
				long allCount = couponCodesDao.findIssuedCoupCodeByCoup(coupObj.getCouponId());
				coupObj.setRedeemed(coupObj.getRedeemed()!= null ? coupObj.getRedeemed().longValue()+1 : 1);
				coupObj.setTotRevenue(coupObj.getTotRevenue() != null ? coupObj.getTotRevenue().longValue()+totalAmount : totalAmount);
				if(coupObj.getTotalQty() != null) {
					long availCount = coupObj.getTotalQty().longValue()-allCount;
					if(availCount < 0) availCount = 0;
					coupObj.setAvailable(availCount);
				}
				//couponsDao.saveOrUpdate(coupObj);
				 couponsDaoForDML.saveOrUpdate(coupObj);
				 if(coupObj.getLoyaltyPoints()!=null)
				 couponsDaoForDML.updateUsedLoyaltyPoint(coupObj.getCouponId());
				statusInfo=new StatusInfo("0","Promo redeem sucessfully", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				sendEmailForReferralProgram(couponCodes,user);
			}
			
		
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing saveCouponCodesObj::::: ", e);
		}
	
	
		}else {
		
			
			refcodesredeem=false;
			logger.info("entering Referralcoupon flow");

			ContactsLoyalty contactsLoyaltyObj =  null;
			if((cardNumber != null && !cardNumber.trim().isEmpty()) || 
					(custId != null && !custId.trim().isEmpty())) {
			
				contactsLoyaltyObj = contactsLoyaltyDao.getContactsLoyaltyByCustId(cardNumber,custId,user.getUserId());
				
			}//if
			String phoneParse="";
			if(contactsLoyaltyObj==null){
				if((emailId != null && !emailId.isEmpty()) || (phone != null && !phone.isEmpty()) ){
					
					logger.info("emailid value"+emailId);

					 phoneParse = Utility.phoneParse(phone.trim(),
							user != null ? user.getUserOrganization() : null);
				
					 logger.info(" phoneParse value"+phoneParse);

					contactsLoyaltyObj = contactsLoyaltyDao.findBy(user, emailId, phoneParse);
				
					 logger.info(" contactsLoyaltyObj value"+contactsLoyaltyObj);

				}
			}
			List<ReferralcodesRedeemed> refredeemedList = new ArrayList<ReferralcodesRedeemed>();

			Contacts contactsobj=null;
			Contacts contactcidobj=null;
			
			
			logger.info("emailid value"+emailId);
			logger.info("phoneParse value"+phoneParse);

			try {
			
			contactsobj=contactdao.findContactByValues(emailId,phoneParse,null, user.getUserId());
			contactcidobj=contactdao.findContactByCid(refCodeObj.getReferredCId(), user.getUserId());
			
			}catch(Exception e) {
				
				logger.info(" contactcidobj and contactsobj are null caught in block "+e);

			}
			
			logger.info(" contactcidobj value in "+contactcidobj);
			logger.info("contactsobj value in savecouponcodesobj"+contactsobj);

			
			ReferralcodesRedeemed refredobj=new ReferralcodesRedeemed();
	
			if(contactsobj!=null) {
				
				logger.info("entering into if contactobj");
				logger.info(" contactobj cid "+contactsobj.getContactId());

				
				refredobj.setRefereecid(contactsobj.getContactId());
			
			}else if(contactsLoyaltyObj!=null) {
			
				logger.info("entering into else if contactsLoyaltyObj"+contactsLoyaltyObj);

				refredobj.setRefereecid(contactsLoyaltyObj.getContact().getContactId());

			}
			/*else {
			
				logger.info("entering into else contactobj");
				logger.info(" contactobj cid in else"+refCodeObj.getReferredCId());

				refredobj.setReferredcid(refCodeObj.getReferredCId());
				
			}*/
			
			refredobj.setReferredcid(refCodeObj.getReferredCId());
			refredobj.setReferredName(contactcidobj.getFirstName());
			refredobj.setRefcode(refCodeObj.getRefcode());
			refredobj.setUserId(user.getUserId());
			refredobj.setOrgId(orgId);
			
			//refredobj.setReferrecid(contactsLoyaltyObj.getContact().getContactId());
		//	refredobj.setReferralId(refCodeObj.getReferralCodeId());
			
			refredobj.setDocSID(docsid);
			refredobj.setReferralcodeid(refCodeObj.getReferralCodeId());
			refredobj.setRedeemedDate(Calendar.getInstance());
			refredobj.setRefcodestatus(Constants.COUP_CODE_STATUS_REDEEMED);
			refredobj.setTotRevenue(totalAmount);
			
			logger.info("Referredcid value"+refCodeObj.getReferredCId());
			logger.info("Refcode value"+refCodeObj.getRefcode());
			logger.info("orgid value"+refCodeObj.getOrgId());
			logger.info("totalAmount value"+refredobj.getTotRevenue());


			refcodesredeemedDaoFoDML.saveOrUpdate(refredobj);
			
			statusInfo=new StatusInfo("0","Promo redeem sucessfully", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);

			Long count=refcoderedDao.findRedeemdCountByRefId(refCodeObj.getReferralCodeId(),refCodeObj.getReferredCId());
		
			logger.info("count value is"+count);

			String rewardreftype="";
			try {
			
			Refprgrmobj=refprgmDao.findReferalprogramByActive(user.getUserId(),coupObj.getCouponId());

			Rewardrefobj=rewardreftypeDao.getMilestonesListByRefId(Refprgrmobj.getReferralId());
		
			logger.info("Rewardrefobj value is"+Rewardrefobj);
		
			rewardreftype=Refprgrmobj.getRewardonReferraltype();

			
			}catch(Exception e) {}
			
		
			RewardReferraltype cosideredlevel=null;
			
			boolean rulesatisfied=false;
			
			//int levelcount = Rewardrefobj.size();
			
			int refrepeats = 0;
			
			if(rewardreftype.equalsIgnoreCase("Milestones")) {
		
				
				logger.info("entering milestones");
				
				logger.info("Rewardrefobj size is"+Rewardrefobj.size());

				if (Rewardrefobj.size() >= 1) {
					
					Collections.sort(Rewardrefobj, new Comparator<RewardReferraltype>() {
				
						public int compare(RewardReferraltype o1, RewardReferraltype o2) {
						
							logger.info("entering compare refrewobjs");

							int num1 = Integer.valueOf(o1.getMilestoneLevel().intValue());
							int num2 = Integer.valueOf(o2.getMilestoneLevel().intValue());
						
							logger.info("num1 value is"+num1);
							logger.info("num2 value is"+num2);

							
							if (num1 < num2) {
								return -1;
							} else if (num1 == num2) {
								return 0;
							} else {
								return 1;
							}
						}
						});
						}
				
							logger.info("Rewardrefobj vallue before forloop is"+Rewardrefobj);

				
							for(RewardReferraltype refobj:Rewardrefobj) {          
				
							
									refrepeats+=Integer.valueOf(refobj.getRewardonReferralRepeats());
								
									logger.info("refobj value is"+refobj);
									logger.info("refrepeats value is"+refrepeats);
									
									//refrepeats=Integer.valueOf(refobj.getRewardonReferralRepeats());
									//	all condition check (need to be added)
									
									if(count<=refrepeats) {
										
										
											logger.info("entering inner if");
											logger.info("count value is"+count);
											logger.info("refrepeats value is"+refrepeats);


											rulesatisfied  = true;
											cosideredlevel = refobj;
											logger.info("cosideredlevel value is"+cosideredlevel);
											break;
										
									
									}
									
								
							}
							if(cosideredlevel!=null) {
								
								logger.info("entering if milestone to call "+cosideredlevel);

								preparerewardrequest(couponCodeRedeemReq,user,Refprgrmobj,cosideredlevel);
							}
			}
			
			else {
				
				logger.info("entering flat");

			
				preparerewardrequest(couponCodeRedeemReq,user,Refprgrmobj,cosideredlevel);
			}
			refcodesredeem=false;
	}
	
		logger.debug("-------exit  saveCouponCodesObj---------");
		return statusInfo;
	}//saveCouponCodesObj
	
	
	
	private void  preparerewardrequest(CouponCodeRedeemReq couponCodeRedeemReq,Users user,ReferralProgram Refprgrmobj,RewardReferraltype cosideredlevel) throws BaseServiceException {
		
		 
		logger.info("entering prepare reward request");

		
	
		
		String token=couponCodeRedeemReq.getUSERDETAILS().getTOKEN();
		String userName=couponCodeRedeemReq.getUSERDETAILS().getUSERNAME();
		String userOrg=couponCodeRedeemReq.getUSERDETAILS().getORGID();
		
	//	String phone = couponCodeRedeemReq.getCOUPONCODEINFO().getPHONE();
		//String cardNumber = couponCodeRedeemReq.getCOUPONCODEINFO().getCARDNUMBER();
		String docSidStr = couponCodeRedeemReq.getCOUPONCODEINFO().getDOCSID();

		String storeNO = couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER()!=null ? couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER().trim().isEmpty()?null:couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER().trim():null;

		ContactsDao contactdao=null;
		Contacts contactobj=null;
		ReferralcodesIssuedDao referralCodesDao=null;
		ReferralcodesIssued  refCodeObj=null;
		ContactsLoyalty ltycontactobj=null;
	
		String reqCoupcode= couponCodeRedeemReq.getCOUPONCODEINFO().getCOUPONCODE();
		Long orgid=user.getUserOrganization().getUserOrgId();
		
			
			
		try {
			
			referralCodesDao=(ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
			contactdao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);

		
			refCodeObj = referralCodesDao.testForrefCodes(reqCoupcode,orgid);
			
			contactobj=contactdao.findById(refCodeObj.getReferredCId());

			ltycontactobj=contactsLoyaltyDao.findByContactId(user.getUserId(),refCodeObj.getReferredCId());
		
		} catch (Exception e) {
			
			logger.info("refCodeObj value"+refCodeObj);
			
			logger.info("contactobj value"+contactobj);

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("refCodeObj value is "+refCodeObj);
		logger.info("refCodeObj cid is"+refCodeObj.getReferredCId());
		logger.info("contactobj value is"+contactobj);
		
		//String phone=ltycontactobj.getMobilePhone();
		//String cardnumber=ltycontactobj.getCardNumber();
		
		//logger.info("value of phone is "+phone);
		//logger.info("value of cardnumber is "+cardnumber);

		
		LoyaltyIssuanceRequest loyaltyIssuanceRequest = new LoyaltyIssuanceRequest();
		MembershipRequest membershipRequest = new MembershipRequest(); 
		RequestHeader header =  new RequestHeader();
		Amount amount =  new Amount();
		LoyaltyUser loyaltyUser = new LoyaltyUser();
		Gson gson = new Gson();

		
		if(Refprgrmobj.getRewardonReferraltype().equalsIgnoreCase("Milestones")) {
		
			amount.setEnteredValue(cosideredlevel.getRewardonReferralValue());
			
			amount.setValueCode(cosideredlevel.getRewardonReferralVC());
			
		}else {
			
			
			logger.info("entering flat else");
			logger.info("entering flat amountvalue"+Refprgrmobj.getRewardonReferralValue());
			logger.info("entering flat amountvc"+Refprgrmobj.getRewardonReferralVC());

			amount.setEnteredValue(Refprgrmobj.getRewardonReferralValue());
			amount.setValueCode(Refprgrmobj.getRewardonReferralVC());
		}	
		
		
		amount.setType(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD);
		
		
		
		Calendar cal = new MyCalendar(Calendar.getInstance(), null,MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
	
		//	header.setRequestId(""+requestId);
		//	header.setSourceType(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMapping.getFormMappingName());

		header.setRequestDate(""+cal);
		header.setDocSID(docSidStr);
		header.setStoreNumber(storeNO);
		
		
		   if(ltycontactobj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
		    	  membershipRequest.setPhoneNumber(ltycontactobj.getCardNumber());
		    	  membershipRequest.setCardNumber("");
		    	  logger.info("inside membership type mobile>>>"+ltycontactobj.getMembershipType());
		      }else {
		    	membershipRequest.setCardNumber(ltycontactobj.getCardNumber());
		    	//membershipRequest.setCardPin(ltycontactobj.getCardPin());
				membershipRequest.setPhoneNumber("");
				logger.info("inside membership type card>>>"+ltycontactobj.getMembershipType()+" card number>>>"+ltycontactobj.getCardNumber());
		      }
		
	//	membershipRequest.setCardNumber(cardnumber);
	//	membershipRequest.setPhoneNumber(phone);
	
		loyaltyUser.setUserName(userName);
		loyaltyUser.setOrganizationId(userOrg);
		loyaltyUser.setToken(token);
		
		loyaltyIssuanceRequest.setHeader(header);
		loyaltyIssuanceRequest.setAmount(amount);
		loyaltyIssuanceRequest.setUser(loyaltyUser);
		loyaltyIssuanceRequest.setMembership(membershipRequest);
		
		logger.info(":: created object for loyaltyIssuanceRequest ::");
		
		LoyaltyTransactionParent tranParent = createNewTransaction(); 
		Date date = tranParent.getCreatedDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);
		
		LoyaltyIssuanceOCService loyaltyIssuanceOCService = (LoyaltyIssuanceOCService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_ISSUANCE_OC_BUSINESS_SERVICE);
		LoyaltyIssuanceResponse baseservice=(LoyaltyIssuanceResponse)loyaltyIssuanceOCService.processIssuanceRequest(loyaltyIssuanceRequest,OCConstants.LOYALTY_ONLINE_MODE, ""+tranParent.getTransactionId(),transDate,OCConstants.DR_TO_LTY_EXTRACTION);
		
		String finaljson = gson.toJson(baseservice,LoyaltyIssuanceResponse.class);
		
		logger.info(":: final json response after issuance request completed ::"+finaljson);

		logger.info(":: returned form processIssuanceRequest ::");

	}
	
	
		 private LoyaltyTransactionParent createNewTransaction(){
		LoyaltyTransactionParent tranx  = null; 
		try{
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);			tranx = new LoyaltyTransactionParent();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			tranx.setCreatedDate(cal);
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);
			parentDaoForDML.saveOrUpdate(tranx);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}

	private void sendEmailForReferralProgram(CouponCodes couponCodes,Users user) throws BaseServiceException {
		try {
			logger.debug("-------entered sendEmailForReferralProgram---------");
		ContactsDao contactsDao=(ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		CustomTemplatesDao customTemplatesDao=(CustomTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
		EmailQueueDao emailQueueDao=(EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
		EmailQueueDaoForDML emailQueueDaoForDML=(EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
		if(couponCodes.getContactId() != null) {
			Contacts contactObj = contactsDao.findById(couponCodes.getContactId());
			String refererEmailId = contactObj.getUdf12();
			if(refererEmailId != null 
					&& !refererEmailId.trim().isEmpty() 
					&& Utility.validateEmail(refererEmailId)) {
				
				CustomTemplates customTemplates = customTemplatesDao.findByUserRefereeEmail(user.getUserId());
				if(customTemplates != null) {
						String message=null;

						if(customTemplates != null && customTemplates.getHtmlText()!= null) {
							  message = customTemplates.getHtmlText();
						  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(customTemplates.getEditorType()) && customTemplates.getMyTemplateId()!=null) {
							  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
							  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
							  if(myTemplates != null)message = myTemplates.getContent();
						  }
						message = message.replace("[OrganisationName]",
								user.getUserOrganization().getOrganizationName()).replace(
										"[senderReplyToEmailID]", user.getEmailId());
						
						EmailQueue testEmailQueue = new EmailQueue(customTemplates.getTemplateId(),
								Constants.EQ_TYPE_WELCOME_MAIL, message, "Active",
								refererEmailId, user, MyCalendar.getNewCalendar(),
								" Welcome Mail.", null, null, null,
								contactObj.getContactId());
					
						// testEmailQueue.setChildEmail(childEmail);
						//emailQueueDao.saveOrUpdate(testEmailQueue);
						emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
				}//if
			}	
		}
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing sendEmailForReferralProgram::::: ", e);
		}
	}//sendEmailForReferralProgram
}
