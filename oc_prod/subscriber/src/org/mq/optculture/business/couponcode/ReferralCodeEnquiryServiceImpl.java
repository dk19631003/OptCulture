package org.mq.optculture.business.couponcode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.security.auth.message.callback.PrivateKeyCallback.IssuerSerialNumRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.FAQ;
import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.marketer.campaign.beans.LoyaltyMemberItemQtyCounter;
import org.mq.marketer.campaign.beans.LoyaltyMemberSessionID;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.PromoTrxLog;
import org.mq.marketer.campaign.beans.ReferralProgram;
import org.mq.marketer.campaign.beans.ReferralcodesIssued;
import org.mq.marketer.campaign.beans.ReferralcodesRedeemed;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.FAQDao;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDao;
import org.mq.marketer.campaign.dao.LoyaltyMemberItemQtyCounterDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.PromoTrxLogDao;
import org.mq.marketer.campaign.dao.PromoTrxLogDaoForDML;
import org.mq.marketer.campaign.dao.ReferralcodesIssuedDao;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.CouponDescriptionAlgorithm;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.faq.FaqBusinesService;
import org.mq.optculture.business.helper.CouponCodeProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyProgramExclusionDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.data.dao.ReferralProgramDao;
import org.mq.optculture.data.dao.ReferralcodesRedeemedDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.couponcodes.CoupnCodeEnqResponse;
import org.mq.optculture.model.couponcodes.Coupon;
import org.mq.optculture.model.couponcodes.CouponCodeEnqReq;
import org.mq.optculture.model.couponcodes.CouponCodeEnquObj;
import org.mq.optculture.model.couponcodes.CouponCodeInfo;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemReq;
import org.mq.optculture.model.couponcodes.CouponCodeResponse;
import org.mq.optculture.model.couponcodes.CouponDiscountInfo;
import org.mq.optculture.model.couponcodes.DiscountInfo;
import org.mq.optculture.model.couponcodes.HeaderInfo;
import org.mq.optculture.model.couponcodes.ItemAttribute;
import org.mq.optculture.model.couponcodes.ItemCodeInfo;
import org.mq.optculture.model.couponcodes.LoyaltyInfo;
import org.mq.optculture.model.couponcodes.OptSyncPromoDiscountInfo;
import org.mq.optculture.model.couponcodes.PromoItemCodeInfo;
import org.mq.optculture.model.couponcodes.PurchasedItems;
import org.mq.optculture.model.couponcodes.ReferralCodeEnqRequest;
import org.mq.optculture.model.couponcodes.ReferralCodeEnqResponse;
import org.mq.optculture.business.couponcode.ReferralcodeProvider;
import org.mq.optculture.model.couponcodes.ReferralCodeInfo;
import org.mq.optculture.model.couponcodes.ReferralInfo;
import org.mq.optculture.model.couponcodes.StatusInfo;
import org.mq.optculture.model.couponcodes.UserDetails;
import org.mq.optculture.model.importcontact.Status;
import org.mq.optculture.model.loyalty.Balances;
import org.mq.optculture.model.loyalty.OTPRedeemLimit;
import org.mq.optculture.model.mobileapp.FaqRequest;
import org.mq.optculture.model.mobileapp.FaqResponse;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ReferralCodeEnquiryServiceImpl implements ReferralCodeEnquiryService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private ReferralcodeProvider referralcodeProvider;
	private ReferralcodesIssuedDao referralcodesIssuedDao;

	public ReferralcodesIssuedDao getReferralcodesIssuedDao() {
		return referralcodesIssuedDao;
	}

	public void setReferralcodesIssuedDao(ReferralcodesIssuedDao referralcodesIssuedDao) {
		this.referralcodesIssuedDao = referralcodesIssuedDao;
	}
	
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		ReferralCodeEnqResponse refcodeResponse = null;

		try {
			logger.debug("-------entered processRequest---------");
			//String serviceRequest = baseRequestObject.getAction();
			// json to object
			Gson gson = new Gson();
			ReferralCodeEnqRequest referralCodeEnqRequest = null;
			try {
				referralCodeEnqRequest = gson.fromJson(baseRequestObject.getJsonValue(), ReferralCodeEnqRequest.class);
			} catch (JsonSyntaxException e) {
				logger.error("Exception ::", e);
				StatusInfo statusInfo = new StatusInfo("100900",PropertyUtil.getErrorMessage(100900, OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);

				refcodeResponse = finalJsonResponse(new ReferralInfo(), statusInfo);
				String json = gson.toJson(refcodeResponse);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.REFERRAL_SERVICE_REQUEST);
				return baseResponseObject;
			}
			ReferralCodeEnquiryService referralCodeEnquiryService = (ReferralCodeEnquiryService) ServiceLocator.getInstance().getServiceByName(OCConstants.REFERRAL_CODE_ENQUIRY_BUSINESS_SERVICE);
			refcodeResponse = referralCodeEnquiryService.processReferralRequest(referralCodeEnqRequest);

			// object to json
			String json = gson.toJson(refcodeResponse);
			baseResponseObject.setJsonValue(json);
			baseResponseObject.setAction(OCConstants.FAQ_SERVICE_REQUEST);
			return baseResponseObject;
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("-------exit  processRequest---------");
		return baseResponseObject;
		
	
	
	}


	

	
	@Override
	public ReferralCodeEnqResponse processReferralRequest(ReferralCodeEnqRequest referralCodeEnqRequest)
			throws BaseServiceException {
		// TODO Auto-generated method stub
		
		ReferralCodeEnqResponse referralCodeEnqResponse = null;
		ReferralInfo referralInfo = new ReferralInfo();
		StatusInfo statusInfo=null;
		ReferralcodesRedeemedDao referralcodesRedeemedDao=null;
		LoyaltyTransactionChildDao loyaltyTransactionDao = null;
		ContactsLoyaltyDao contactsLoyaltyDao = null;
		UsersDao usersDao = null;
		ContactsDao contactsDao=null;
		ReferralcodesIssuedDao refcodesissued=null;
		List<ReferralcodesIssued> refissuedobj=null;
		ReferralProgramDao refProgramDao=null;	
		CouponsDao couponsDao=null;


		try {
			
			
			try {
				
				referralcodesRedeemedDao= ( ReferralcodesRedeemedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_CODES_REDEEMED_DAO);
				usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				loyaltyTransactionDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName("contactsLoyaltyDao");
				contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
				refcodesissued = (ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
				refProgramDao = (ReferralProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_PROGRAM_DAO);
				couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
				
			} catch(Exception e) {
				
			}
			
			String userNameStr  = referralCodeEnqRequest.getUSERDETAILS().getUSERNAME();
			String orgId =  referralCodeEnqRequest.getUSERDETAILS().getORGID();
			String tokenStr =  referralCodeEnqRequest.getUSERDETAILS().getTOKEN();

			Users user = usersDao.findByToken(userNameStr+Constants.USER_AND_ORG_SEPARATOR+orgId , tokenStr);
			if(user == null) {
				logger.debug("Error : User not exists in DB ****");
				statusInfo = new StatusInfo("100011",PropertyUtil.getErrorMessage(100011,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				referralCodeEnqResponse = finalJsonResponse(referralInfo,statusInfo);	
				return referralCodeEnqResponse;
			}
			
			statusInfo = validateJsonUser(referralCodeEnqRequest);
			if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				referralCodeEnqResponse = finalJsonResponse(referralInfo,statusInfo);	
				return referralCodeEnqResponse;
			}
			String refcode = "";
			String cfStr = "";
			ReferralProgram refProgramObj=refProgramDao.findReferalprogramByUserId(user.getUserId());
			cfStr = "REF_CC_"+refProgramObj.getCouponId();
			logger.info("cfStr :"+cfStr);
			
    		logger.info("referralcodeProvider :"+referralcodeProvider);
			if(referralcodeProvider == null) { //get coupon provider
				logger.info("entered as referral provider is null");
				referralcodeProvider = ReferralcodeProvider.getReferralcodeProviderInstance(null, referralcodesIssuedDao);
				if(referralcodeProvider == null) {
					if(logger.isInfoEnabled()) logger.info("No Referral provider found....");{
						return null;

					}
				}//if

				logger.info("Couponset value before adding"+referralcodeProvider.couponSet);

				if(referralcodeProvider.couponSet != null ) {	
					logger.info("Coupon Set starting");
				if(!referralcodeProvider.couponSet.contains(cfStr)) {
					
					logger.info("CouponId"+cfStr);
					referralcodeProvider.couponSet.add(cfStr);
				}
				}
				logger.info("CouponSet :"+referralcodeProvider.couponSet);
			}else {
				logger.info("entered as referralProvider not null");
				referralcodeProvider.couponSet.add(cfStr);
			}
			
			
			Contacts contact=contactsDao.findContactByValues(null,referralCodeEnqRequest.getREFERRALCODEINFO().getPHONE(), null,user.getUserId());
			logger.info("contactObj"+contact);
			
			refissuedobj = refcodesissued.getRefcodebycontactid(contact.getContactId());
			logger.info("Ref Object :"+refissuedobj);
			
			if(refissuedobj != null && refissuedobj.size()>0) {	
				logger.info("Ref code exists");
				refcode = refissuedobj.get(0).getRefcode();
			}
			else {
				logger.info("Ref code not exist");
				refcode = referralcodeProvider.getNextCouponCode(cfStr,"",
						"Webapp", referralCodeEnqRequest.getREFERRALCODEINFO().getPHONE(),null, null,contact.getContactId(),true,user.getUserId());;
			}
			
			Double referralReward=0.0;
		  	
			referralReward=getReferralRewardEarned(user.getUserId(),contact.getContactId());

			
			long count=referralcodesRedeemedDao.findRedeemdCountByCode(refcode);
			logger.info("count of redeemed is "+count);
			
			Coupons couponsObj = refissuedobj.get(0).getCouponId();
			
//			String refcode = referralCodeEnqRequest.getREFERRALCODEINFO().getREFERRALCODE();
			if(count > 0 && referralReward > 0.0) {
				
				logger.info("entering count if block");

				referralInfo.setREDEEMEDCOUNT(String.valueOf(count));
				referralInfo.setREFERRALCODE(refcode);
				referralInfo.setREWARDEARNED(referralReward.toString());
				referralInfo.setOFFERHEADING(couponsObj.getOfferHeading());
				referralInfo.setOFFERDESCRIPTION(couponsObj.getOfferDescription());
				referralInfo.setBANNERIMAGE(couponsObj.getBannerImage());
				statusInfo = new StatusInfo("0","Referral-code enquiry is successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				referralCodeEnqResponse = finalJsonResponse(referralInfo,statusInfo);
				
			} else {
				
				logger.info("entering else block ");
				
				referralInfo.setREDEEMEDCOUNT("0");
				referralInfo.setREFERRALCODE(refcode);
				referralInfo.setREWARDEARNED("0");
				referralInfo.setOFFERHEADING(couponsObj.getOfferHeading());
				referralInfo.setOFFERDESCRIPTION(couponsObj.getOfferDescription());
				referralInfo.setBANNERIMAGE(couponsObj.getBannerImage());
				statusInfo = new StatusInfo("0","Referral-code enquiry is successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				referralCodeEnqResponse = finalJsonResponse(referralInfo,statusInfo);
			}
		
			
			logger.debug("-------exit  processCouponRequest---------");
			return referralCodeEnqResponse;
		} catch (Exception e) {
			statusInfo = new StatusInfo("100901",PropertyUtil.getErrorMessage(100901,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			referralCodeEnqResponse = finalJsonResponse(referralInfo,statusInfo);
			logger.error("Exception ::" , e);
			return referralCodeEnqResponse;
		}
		
	}

	private  Double getReferralRewardEarned(Long userId,Long contactId){

		ContactsLoyaltyDao contactsLoyaltyDao = null;
		LoyaltyTransactionChildDao loyaltyTransactionDao = null;
		try {
		contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName("contactsLoyaltyDao");
		loyaltyTransactionDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		}catch(Exception e) {
			logger.error("Exception in loading Daos",e);
		}
		ContactsLoyalty contactsLoyalty= contactsLoyaltyDao.findByContactId(userId,contactId);
		logger.info("contact loyaltyObj in getReferralRewardEarned:"+contactsLoyalty);
		if(contactsLoyalty==null) return 0.0;

		List<Object[]> totalRewards=loyaltyTransactionDao.findTotalRewardsEarnByMembershipNumber(contactsLoyalty.getCardNumber(),userId,"Reward");
		logger.info("totalRewards :"+totalRewards);
		if(totalRewards==null || totalRewards.size()==0) {
			return 0.0;
		}
		else {
			Double amount = 0.0;
			amount = (Double) totalRewards.get(0)[0];
			Double points = 0.0;
			points = (Double) totalRewards.get(0)[1];
			if(points!=null && points>0.0) {
				return points;
			}
			else if(amount!=null && amount>0){
					return amount;
				}
			return 0.0;
		}
	}


private StatusInfo validateJsonUser(ReferralCodeEnqRequest referralCodeEnqRequest) throws BaseServiceException {
	StatusInfo statusInfo=null;
	try {
		logger.debug("-------entered validateJsonUser---------");
		ReferralCodeInfo referralCodeInfoObj=referralCodeEnqRequest.getREFERRALCODEINFO();
		UserDetails userDetailsObj=referralCodeEnqRequest.getUSERDETAILS();
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);

		
		if(userDetailsObj == null) {
			logger.debug("Error : unable to find the User details in JSON ****");
			statusInfo = new StatusInfo("100003",PropertyUtil.getErrorMessage(100003,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		String userNameStr  = userDetailsObj.getUSERNAME();
		if(userNameStr == null || userNameStr.trim().length() == 0) {
			logger.debug("Error : unable to find the User Name  in JSON ****");
			statusInfo = new StatusInfo("100004",PropertyUtil.getErrorMessage(100004,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;

		}
		String orgId = userDetailsObj.getORGID();
		if(orgId == null || orgId.trim().length() == 0) {
			logger.debug("Error : unable to find the User Organization  in JSON ****");
			statusInfo = new StatusInfo("100005",PropertyUtil.getErrorMessage(100005,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		String tokenStr = userDetailsObj.getTOKEN();
		if(tokenStr == null || tokenStr.trim().length() == 0) {
			logger.debug("Error : unable to find the User Token  in JSON ****");
			statusInfo = new StatusInfo("100006",PropertyUtil.getErrorMessage(100006,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		//check the User obj from DB
		Users user = usersDao.findByToken(userNameStr+Constants.USER_AND_ORG_SEPARATOR+orgId , tokenStr);
		if(user == null) {
			logger.debug("Error : User not exists in DB ****");
			statusInfo = new StatusInfo("100011",PropertyUtil.getErrorMessage(100011,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(referralCodeInfoObj==null) {
			logger.debug("Error : unable to find the Promo-code Info  in JSON ****");
			statusInfo = new StatusInfo("100007",PropertyUtil.getErrorMessage(100007,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		/*String coupCodeStr = couponCodeInfoObj.getCOUPONCODE(); //commented w.r.t revised API under 2.7.4
		if(coupCodeStr==null || coupCodeStr.trim().length() == 0 ) {
			logger.debug("Error : unable to find thePromo-code Info  in JSON ****");
			statusInfo = new StatusInfo("100008",PropertyUtil.getErrorMessage(100008,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}*/
	}catch(Exception e) {
		logger.error("Exception ::" , e);
		throw new BaseServiceException("Exception occured while processing validateJsonUser::::: ", e);
	}
	logger.debug("-------exit  validateJsonUser---------");
	return statusInfo;
}

private ReferralCodeEnqResponse finalJsonResponse(ReferralInfo referralInfo,StatusInfo statusInfo) throws BaseServiceException{
	logger.debug("-------entered finalJsonResponse---------");
	
	ReferralCodeEnqResponse referralCodeEnqResponse=new ReferralCodeEnqResponse(referralInfo,statusInfo);
	
	/*couponcodeEnqResponse.setHEADERINFO(headerInfo);
	couponcodeEnqResponse.setSTATUSINFO(statusInfo);*/
	logger.debug("-------exit  finalJsonResponse---------");
	return referralCodeEnqResponse;
}//finalJsonResponse











}
