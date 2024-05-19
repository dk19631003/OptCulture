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
import org.mq.marketer.campaign.beans.ReferralcodesIssued;
import org.mq.marketer.campaign.beans.ReferralcodesRedeemed;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDaoForDML;
import org.mq.marketer.campaign.dao.CouponsDao;
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
import org.mq.optculture.business.helper.CouponCodeProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyProgramExclusionDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
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
import org.mq.optculture.model.couponcodes.StatusInfo;
import org.mq.optculture.model.couponcodes.UserDetails;
import org.mq.optculture.model.importcontact.Status;
import org.mq.optculture.model.loyalty.Balances;
import org.mq.optculture.model.loyalty.OTPRedeemLimit;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class CouponCodeEnquiryServiceImpl implements CouponCodeEnquiryService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private final String FREE_ITEM_NUDGE="Add Item to Avail Discount.";
	private final String RECEIPT_NUDGE="Buy More to Get Discount.";
	private final String ITEMS = " item(s)";
	private final String ONESPACE = " ";
	private TimeZone clientTimeZone;

	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		BaseResponseObject baseResponseObject=new BaseResponseObject();
		CoupnCodeEnqResponse coupnCodeEnqResponse=null;
		try {
			if(baseRequestObject.getAction().equals(OCConstants.COUPON_CODE_ENQUIRY_REQUEST)){

				//json to object 
				String json =baseRequestObject.getJsonValue();
				Gson gson = new Gson();
				CouponCodeEnquObj couponCodeEnquObj;
				try {
					couponCodeEnquObj = gson.fromJson(json, CouponCodeEnquObj.class);
					
				} catch (Exception e) {
					logger.error("Exception ::",e);
					StatusInfo statusInfo = new StatusInfo("100900",PropertyUtil.getErrorMessage(100900, OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					coupnCodeEnqResponse = finalJsonResponse(new HeaderInfo(),new ArrayList<CouponDiscountInfo>(),statusInfo);	
					String jsonString=gson.toJson(coupnCodeEnqResponse);
					baseResponseObject.setJsonValue(jsonString);
					baseResponseObject.setAction(OCConstants.COUPON_CODE_ENQUIRY_REQUEST);
					return baseResponseObject;
				}
				
				CouponCodeEnquiryService couponCodeEnquiryService = (CouponCodeEnquiryService) ServiceLocator.getInstance().getServiceByName(OCConstants.COUPON_CODE_ENQUIRY_BUSINESS_SERVICE);
				coupnCodeEnqResponse=(CoupnCodeEnqResponse) couponCodeEnquiryService.processCouponRequest(couponCodeEnquObj);

				//object to json
				String jsonString=gson.toJson(coupnCodeEnqResponse);
				baseResponseObject.setJsonValue(jsonString);
				baseResponseObject.setAction(OCConstants.COUPON_CODE_ENQUIRY_REQUEST);
				
				logTransactionRequestResponse(coupnCodeEnqResponse, couponCodeEnquObj, json, jsonString, OCConstants.LOYALTY_ONLINE_MODE);

				
				return baseResponseObject;
			}
		}catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}
		return baseResponseObject;
	}//processRequest

	@Override
	public CoupnCodeEnqResponse processCouponRequest(
			CouponCodeEnquObj couponCodeEnquObj) throws BaseServiceException {
		CoupnCodeEnqResponse coupnCodeEnqResponse = null;
		StatusInfo statusInfo=null;
		HeaderInfo headerInfo=null;
		List<CouponDiscountInfo> coupDiscInfoList=new ArrayList<CouponDiscountInfo>() ;
		LoyaltyInfo loyaltyInfo = null;
		
		try {
			logger.debug("-------entered processCouponRequest---------");
			
			statusInfo=validateRootObj(couponCodeEnquObj);
			if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
				return coupnCodeEnqResponse;
			}
			headerInfo=couponCodeEnquObj.getCOUPONCODEENQREQ().getHEADERINFO();
			UserDetails userDetailsObj=couponCodeEnquObj.getCOUPONCODEENQREQ().getUSERDETAILS();
			CouponCodeInfo couponCodeInfoObj=couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO();
			statusInfo = validateJsonUser(couponCodeEnquObj);
			if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
				return coupnCodeEnqResponse;
			}
			
			String sourceType = headerInfo.getSOURCETYPE();
			if(sourceType != null && !sourceType.isEmpty() && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) ){
				String sessionID = userDetailsObj.getSESSIONID();
				if(sessionID == null || sessionID.isEmpty()){
					statusInfo=new StatusInfo("800028", PropertyUtil.getErrorMessage(800028,OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
					return coupnCodeEnqResponse;
				}
				LoyaltyMemberSessionID loyaltyMemberSessionID = LoyaltyProgramHelper.validateSessionID(sessionID);
				if(loyaltyMemberSessionID == null){
					statusInfo=new StatusInfo("800028", PropertyUtil.getErrorMessage(800028,OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
					return coupnCodeEnqResponse;
					
				}
				
				String cardNumber = LoyaltyProgramHelper.getCardFromSesstionID(sessionID);
				if(couponCodeInfoObj.getCARDNUMBER() != null && 
						couponCodeInfoObj.getCARDNUMBER().trim().length() > 0 && 
						!couponCodeInfoObj.getCARDNUMBER().trim().equals(cardNumber)){
					statusInfo=new StatusInfo("800029", PropertyUtil.getErrorMessage(800029,OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
					return coupnCodeEnqResponse;
					
					
					
				}
				
			}
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
			
			CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
		
			ReferralcodesIssuedDao referralCodesDao=(ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);

			ReferralcodesRedeemedDao refredeemedCodesDao=(ReferralcodesRedeemedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_CODES_REDEEMED_DAO);

			ContactsDao contactdao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);

			CouponsDao couponsDao=(CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
			LoyaltyMemberItemQtyCounterDao loyaltyMemberItemQtyCounterDao = (LoyaltyMemberItemQtyCounterDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO);
			String userNameStr  = userDetailsObj.getUSERNAME();
			String orgId = userDetailsObj.getORGID();
			String coupCodeStr = couponCodeInfoObj.getCOUPONCODE();
			String tokenStr = userDetailsObj.getTOKEN();
			Users user = usersDao.findByToken(userNameStr+Constants.USER_AND_ORG_SEPARATOR+orgId , tokenStr);
			boolean requestingFromNewPlugin = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT() != null &&
					couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCARDNUMBER() != null && 
					couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getDISCOUNTAMOUNT() != null;
			boolean noReceiptData = false;
			if(requestingFromNewPlugin){
				
				noReceiptData =( couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT().trim().isEmpty() ||
						Double.parseDouble(couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT().trim()) == 0) &&
								couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS().isEmpty();
				
			}
			List<POSMapping> posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_SKU+"'", user.getUserId());
			Map<String , String> posMappingMap = new HashMap<String , String>();
			if(posMappingsList !=null && !posMappingsList.isEmpty()){
				
				for (POSMapping posMapping : posMappingsList) {
					if(posMapping.getCustomFieldName().toLowerCase().startsWith("udf")){
						posMappingMap.put(posMapping.getCustomFieldName().toLowerCase(), posMapping.getDisplayLabel());

					}else{
						
						posMappingMap.put(posMapping.getCustomFieldName(), posMapping.getDisplayLabel());
					}
				}
			}
			
			if(coupCodeStr != null && !coupCodeStr.trim().isEmpty() && 
					!coupCodeStr.equals("OC-LOYALTY") && !coupCodeStr.equalsIgnoreCase("ALL") 
					&& !coupCodeStr.equalsIgnoreCase("BALANCES") && 
					!coupCodeStr.equalsIgnoreCase("SPECIFIC") && 
					!coupCodeStr.equalsIgnoreCase("DISCOUNTS") && !coupCodeStr.equalsIgnoreCase("ALLCODES")) { //changed w.r.t APP-2181 i.e to combine recommendation +unlock promotions 
				
				
				ReferralcodesIssued  refCodeObj=null;
				Contacts contactsobj=null;
				ContactsLoyalty contactsLoyaltyobj=null;
				ReferralcodesRedeemed redeemedrefobj=null;
				Coupons coupObj = null;
				
				String ph=couponCodeInfoObj.getPHONE();
				String phonePar = Utility.phoneParse(ph.trim(),
						user != null ? user.getUserOrganization() : null);
				if(phonePar != null && !phonePar.isEmpty()) phonePar = LoyaltyProgramHelper.preparePhoneNumber(user, phonePar); //APP-4380
				logger.info("emailid is "+couponCodeInfoObj.getEMAIL());
				logger.info("phone is "+phonePar);
				CouponCodes  coupCodeObj = couponCodesDao.testForCouponCodes(coupCodeStr, user.getUserOrganization().getUserOrgId());
				contactsobj=contactdao.findContactByValues(couponCodeInfoObj.getEMAIL(),phonePar,null, user.getUserId());

				if(coupCodeObj==null) {
				try {
					refCodeObj = referralCodesDao.testForrefCodes(coupCodeStr, user.getUserOrganization().getUserOrgId());
					logger.info("value refCodeObj is"+refCodeObj);
					}catch(Exception e) {
						logger.info("Exception in contacts object"+e);
					}
				}
			if(refCodeObj!=null) {
					
					
					logger.info("entering referral code flow");
						
						coupObj = getCouponObj(user,coupCodeStr);
						statusInfo = validateCoupon(coupObj,user);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
						
							logger.info("entering  validateCoupon method");

							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
							return coupnCodeEnqResponse;
						}
						logger.info("coupObj value is"+coupObj);
			
					
						if(refCodeObj != null && !refCodeObj.getRefcode().equals(coupCodeStr)) {
							
							logger.info("entering refcodeobj value method");

							statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
							return coupnCodeEnqResponse;
						}
			
						try {
						redeemedrefobj=refredeemedCodesDao.testIfOfferAvailed( user.getUserOrganization().getUserOrgId(),contactsobj.getContactId());
						logger.info("value redeemedrefobj is"+redeemedrefobj);
						}catch(Exception e) {
							logger.info("Exception in redeemedref object"+e);
						}
						
						statusInfo = checkredeemedCoupon(redeemedrefobj,user);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							
							logger.info("entering  checkredeemedCoupon method");

							
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
							return coupnCodeEnqResponse;
						}
					
						try {
							contactsLoyaltyobj = contactsLoyaltyDao.findBy(user, couponCodeInfoObj.getEMAIL(), phonePar);
							logger.info("value contactsLoyaltyobj is"+contactsLoyaltyobj);
							}catch(Exception e) {
								logger.info("Exception in contactsLoyalty object"+e);
							}
							
						if(contactsLoyaltyobj!=null) {
							statusInfo = checkloyaltycontact(contactsLoyaltyobj,user);
							if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
								
								logger.info("entering  checkloyaltycontact method");

								coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
								return coupnCodeEnqResponse;
							}
						}
				
			} //old coupon flow
				
				if(refCodeObj== null && coupCodeObj != null && !coupCodeObj.getCouponCode().equals(coupCodeStr)) {
					statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
					return coupnCodeEnqResponse;
				}
				if(coupObj == null)
					
					coupObj = getCouponObjcouponcode(coupCodeObj,coupCodeStr,user.getUserOrganization().getUserOrgId());

					//coupObj = getCouponObj(user,coupCodeStr);

				if(coupObj != null && coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_SINGLE) && 
						!coupObj.getCouponCode().equals(coupCodeStr)) {
					statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
					return coupnCodeEnqResponse;
				}

				statusInfo = validateCoupon(coupObj,user);
				if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
					return coupnCodeEnqResponse;
				}
			
				
				logger.info("useasrefcode flag :"+coupObj.isUseasReferralCode());
				logger.info("coupon generation type is :"+coupObj.getCouponGeneratedType());
				logger.info("phonePar from request:"+phonePar);

				// Validate that the coupon (not promo) is used by the person/number it is issued to
				// Skip the checks for referral codes since they will be used by someone else by definition
				if(!coupObj.isUseasReferralCode() 
						&& coupObj.getCouponGeneratedType().equalsIgnoreCase(Constants.COUP_GENT_TYPE_MULTIPLE) 
						&& phonePar != null && !phonePar.isEmpty()) {

					logger.info("entered into couponissue checking block");
		
					try {
					boolean numcheck=Long.parseLong(coupCodeObj.getIssuedTo()) != Long.parseLong(phonePar);
					boolean statuscheck=coupCodeObj.getStatus().equals("Active");
					logger.info(" phone number check "+numcheck);
					logger.info(" status check "+statuscheck);
					}catch(NumberFormatException e) {
						logger.error("Exception while checking phonenumber not present in couponocodes table"+e);
					}
					
					
					String couponIssuednum=coupCodeObj.getIssuedTo(); 
					if(couponIssuednum!=null  && 
							(coupCodeObj.getCampaignType().equalsIgnoreCase(OCConstants.COUP_GENT_CAMPAIGN_TYPE_SINGLE_SMS) || 
							coupCodeObj.getCampaignType().equalsIgnoreCase(OCConstants.AUTO_COMM_TYPE_SMS) || 
							coupCodeObj.getCampaignType().equalsIgnoreCase(OCConstants.COUP_GENT_CAMPAIGN_TYPE_PUSH_NOTIFICATION) ||  
							coupCodeObj.getCampaignType().equalsIgnoreCase(OCConstants.COUP_GENT_CAMPAIGN_TYPE_WHATSAPP  ))){
						
						couponIssuednum = LoyaltyProgramHelper.preparePhoneNumber(user, couponIssuednum); //APP-4380
						logger.info("after removing countrycarrier no is:"+couponIssuednum);

						if( coupCodeObj.getStatus().equals("Active") && Long.parseLong(couponIssuednum) != Long.parseLong(phonePar)) {
						
						logger.info("entered into issuedphone not equal block");
						statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);

						logger.info("Coupon issued to " + coupCodeObj.getIssuedTo() + " but used by " + phonePar);
						coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
						return coupnCodeEnqResponse;
					}
				 
					}else {
						
						logger.info("entering email issued block");
				
						if( coupCodeObj.getStatus().equals("Active") && !couponIssuednum.equalsIgnoreCase(contactsobj.getEmailId())) {
							
							logger.info("entered into issuedemail not equal block");
							statusInfo=new StatusInfo("100918", PropertyUtil.getErrorMessage(100918,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);

							logger.info("Coupon issued to " + coupCodeObj.getIssuedTo() + " but used by " + couponCodeInfoObj.getEMAIL());
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
							return coupnCodeEnqResponse;
						}
					}
					
				}
				
				
				if(coupCodeObj != null )statusInfo = validateDynamicCoupon(coupObj,coupCodeObj);
				if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
					return coupnCodeEnqResponse;
				}
				statusInfo = validateStoreNum(couponCodeInfoObj,coupObj);
				if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
					return coupnCodeEnqResponse;
				}
				
				//TODO validate SubsidiaryNumber, CardNumber

				CouponCodeProcessHelper couponCodeProcessHelper = new CouponCodeProcessHelper();
				
				logger.info("refCodeObj value before processhelper"+refCodeObj);
				logger.info("coupCodeObj value before processhelper"+coupCodeObj);

				if(coupCodeObj!=null &&refCodeObj==null) {
				
			
				statusInfo=couponCodeProcessHelper.validateContactObj(couponCodeInfoObj,coupObj,coupCodeObj,user);
				if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
					return coupnCodeEnqResponse;
				}
				}
				if(coupObj.getDiscountCriteria().equals("SKU")) {
					List<PurchasedItems> purchaseList= couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS();//checkIfSkuType(couponCodeEnquObj,coupObj);
					statusInfo =  validatePurItemList(purchaseList);
				}
				if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
					return coupnCodeEnqResponse;
				}
				
				//Split new and existing flow
				//if(user.isNewPlugin()) {
					String cardNumber = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCARDNUMBER();
					String customerId = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCUSTOMERID();
					String email =  couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getEMAIL();
					String phone =  couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getPHONE();
					
					if(phone != null && !phone.isEmpty()) phone = LoyaltyProgramHelper.preparePhoneNumber(user, phone); //APP-4380
					
					statusInfo= validatePromoListSize(coupObj, customerId, email, phone, coupCodeStr, 
							user.getUserOrganization().getUserOrgId(),cardNumber);
					if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
						coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
						return coupnCodeEnqResponse;
					}
					
					//ContactsLoyalty contactsLoyaltyObj =  null;
					if((cardNumber != null && !cardNumber.trim().isEmpty()) || 
							(customerId != null && !customerId.trim().isEmpty())) {
						contactsLoyaltyobj = contactsLoyaltyDao.getContactsLoyaltyByCustId(cardNumber,customerId,user.getUserId());
						
					}//if
					
					if(contactsLoyaltyobj==null){
						if((email != null && !email.isEmpty()) || (phone != null && !phone.isEmpty()) ){
							
							String phoneParse = Utility.phoneParse(phone.trim(),
									user != null ? user.getUserOrganization() : null);
							
							contactsLoyaltyobj = contactsLoyaltyDao.findBy(user, email, phoneParse);
						}
					}
					
					
					
						
						
					boolean isLOyaltyPromo = false;
					boolean isEligible = false;
					boolean isReward = false;
					LoyaltyBalance balance =  null;
					if(coupObj.getLoyaltyPoints() != null && 
							(coupObj.getRequiredLoyltyPoits() != null || coupObj.getMultiplierValue()!=null)) { //changed w.r.t APP-2181 i.e to
						//changed when a loyalty promo has requested thru a promo manager(as a regular promo) 
						//with a CODE then also check against the balance 
						isLOyaltyPromo = true;
						
						if((cardNumber==null || cardNumber.isEmpty() ) && 
								(customerId == null || customerId.isEmpty()) && 
								(email == null || email.isEmpty() ) && (phone == null || phone.isEmpty()))

						{
							// add new error code
							statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
								coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
								return coupnCodeEnqResponse;
							}
						}
						if(contactsLoyaltyobj==null){
							if((email != null && !email.isEmpty()) || (phone != null && !phone.isEmpty()) ){
								
								String phoneParse = Utility.phoneParse(phone.trim(),
										user != null ? user.getUserOrganization() : null);
								
								contactsLoyaltyobj = contactsLoyaltyDao.findBy(user, email, phoneParse);
							}
						}
						if(contactsLoyaltyobj==null)	{
							// add new error code
							statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
								coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
								return coupnCodeEnqResponse;
							}

						}
						if(coupObj.getValueCode() != null && 
								!coupObj.getValueCode().equals(OCConstants.LOYALTY_POINTS) && coupObj.getRequiredLoyltyPoits() != null) {
							balance = loyaltyBalanceDao.findBy(user.getUserId(), contactsLoyaltyobj.getProgramId(),
									contactsLoyaltyobj.getLoyaltyId(),  coupObj.getValueCode());
							isReward = true;
							if(balance != null && balance.getBalance()>=coupObj.getRequiredLoyltyPoits())  isEligible = true;
							
								
						}else if((coupObj.getValueCode() == null || coupObj.getValueCode().equals(OCConstants.LOYALTY_POINTS)) && coupObj.getRequiredLoyltyPoits() != null &&
								contactsLoyaltyobj.getLoyaltyBalance() != null && contactsLoyaltyobj.getLoyaltyBalance() >= coupObj.getRequiredLoyltyPoits()) {
							
							isEligible = true;
						}
						
						if(coupObj.getRequiredLoyltyPoits() != null && !isReward && !isEligible) { //change the error code and message
							statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
								coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
								return coupnCodeEnqResponse;
							}
							
						}
						
					}
					// if accumulation is disable if receiptdiscount or item discount  is there then return error
					/*if(coupObj.isAccumulateOtherPromotion()!= null && ) {
						boolean isStorePromoExists = false;
						String reciptdDiscount = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getDISCOUNTAMOUNT();

						isStorePromoExists = reciptdDiscount != null && !reciptdDiscount.isEmpty() && Double.parseDouble(reciptdDiscount) > 0;

						if(!isStorePromoExists) {
							List<PurchasedItems> purchaseList=couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS();
							if(purchaseList != null && purchaseList.size() > 0) {

								for (PurchasedItems tempObj : purchaseList) {

									String ItemDiscount = tempObj.getITEMDISCOUNT()!= null && !tempObj.getITEMDISCOUNT().isEmpty() ? tempObj.getITEMDISCOUNT() : Constants.STRING_NILL;

									isStorePromoExists = ItemDiscount != null && !ItemDiscount.isEmpty() && Double.parseDouble(ItemDiscount) > 0;
									if(isStorePromoExists) break;	
								} //for
							}


						}

						if(isStorePromoExists) { // add new error code
							statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
								coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
								return coupnCodeEnqResponse;
							}
						}
					}*/
					if(isLOyaltyPromo) {
						List<Coupons> finalList = new ArrayList<Coupons>();
						finalList.add(coupObj);
						Double pointBalance = contactsLoyaltyobj.getLoyaltyBalance(); 
						coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, coupObj,false,coupCodeStr, user, coupCodeObj, 
								requestingFromNewPlugin, posMappingMap, isReward, isEligible, balance,
								contactsLoyaltyobj.getLoyaltyId(), null, pointBalance, contactsLoyaltyobj);
						//check if its a req from old plugin and none of the items are matched then return the error response else send the loyalty info
						
						if(coupObj.getDiscountCriteria().equals("SKU") && !requestingFromNewPlugin && 
								coupDiscInfoList != null && coupDiscInfoList.size()>0){
							boolean itemMatched = false;
							List<PurchasedItems> purchaseList=couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS();
							if(purchaseList != null && purchaseList.size() > 0) {
								for (PurchasedItems tempObj : purchaseList) {
									for (CouponDiscountInfo couponDiscountInfo : coupDiscInfoList) {
										List<DiscountInfo> items = couponDiscountInfo.getDISCOUNTINFO();
										for (DiscountInfo discountInfo : items) {
											List<ItemCodeInfo> itemCodeInfos = discountInfo.getITEMCODEINFO();
											if(itemCodeInfos != null && itemCodeInfos.size()>0){
												
												for (ItemCodeInfo itemCodeInfo : itemCodeInfos) {
													if(itemCodeInfo == null || itemCodeInfo.getITEMCODE()==null) continue;
													if(itemCodeInfo.getITEMCODE().equals(tempObj.getITEMCODE())) {
														
														itemMatched = true;
														break;
													}
												}
											}
											if(itemMatched ) break;
										}
									}//for
									
									if(itemMatched ) break;
								}
							}
						
							if(!itemMatched) {
								coupDiscInfoList = new ArrayList<CouponDiscountInfo>();
							}
						}
						
						
						
					}else{
						
						coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, coupObj,false,coupCodeStr, user, coupCodeObj, 
								requestingFromNewPlugin, posMappingMap, isReward, isEligible, balance, 
								contactsLoyaltyobj != null ? contactsLoyaltyobj.getLoyaltyId() : null, null,null, contactsLoyaltyobj);
					}
					if(requestingFromNewPlugin && contactsLoyaltyobj != null) {
						
						loyaltyInfo = prepareLoyaltyInfo(contactsLoyaltyobj, couponCodeEnquObj, user, true);
					}
				/*}else {
					coupDiscInfoList = setCoupDiscInfoObj(coupDiscInfoList,couponCodeEnquObj, coupObj,false,coupCodeStr, user);
				}*/

				boolean isDiscountNotExist = false;			
				logger.info("coupDiscInfoList is  :: "+coupDiscInfoList.size());

				if((coupDiscInfoList == null || coupDiscInfoList.size() == 0)) {
					isDiscountNotExist = true;
					if(isReward){
						statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
							return coupnCodeEnqResponse;
						}
						
					}
				}else {
					CouponDiscountInfo coupoDisInfoObj = coupDiscInfoList.get(0);
					if(coupoDisInfoObj != null && ((!isReward) && 
							(coupoDisInfoObj.getNUDGEPROMOCODE() != null && coupoDisInfoObj.getNUDGEPROMOCODE().equals("NO")) 
							)  ) {
						List<DiscountInfo> discountList = coupoDisInfoObj.getDISCOUNTINFO();
						if(discountList == null || discountList.size() == 0) {
							isDiscountNotExist = true;
						}/*else if(discountList != null && !discountList.isEmpty()){
						
							
						}*/
					}
				}

				if(isDiscountNotExist) {
					statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
						coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
						return coupnCodeEnqResponse;
					}
				}/*else {
					statusInfo = new StatusInfo("0","Promo-code enquiry is successful.",OCConstants.COUPONCODE_SERVICE_RESPONSE_STATUS_SUCCESS);
					if(statusInfo!=null && OCConstants.COUPONCODE_SERVICE_RESPONSE_STATUS_FAILURE.equals(statusInfo.getSTATUS())){
						coupnCodeEnqResponse = finalJsonResponse(couponCodeEnquObj.getCOUPONCODEENQREQ().getHEADERINFO(),coupDiscInfoList,statusInfo);	
						return coupnCodeEnqResponse;
					}
				}*/
					//ending old coupon flow
			}
			else if(coupCodeStr.equals("OC-LOYALTY")) {

				
				List<Coupons> listPromoCoupons = couponsDao.listOfSinglePromoCoupons(user.getUserOrganization().getUserOrgId());

				statusInfo = validatePromoList(listPromoCoupons);
				if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
					return coupnCodeEnqResponse;
				}
				
					String cardNumber = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCARDNUMBER();
					String customerId = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCUSTOMERID();
					String email =  couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getEMAIL();
					String phone =  couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getPHONE();
					
					if(phone != null && !phone.isEmpty()) phone = LoyaltyProgramHelper.preparePhoneNumber(user, phone); //APP-4380
					
					if((cardNumber==null || cardNumber.isEmpty() ) && 
							(customerId == null || customerId.isEmpty()) && 
							(email == null || email.isEmpty() ) && (phone == null || phone.isEmpty()))
					{
						// add new error code
						statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
							return coupnCodeEnqResponse;
						}
					}
					
					
					ContactsLoyalty contactsLoyaltyObj = null;
					if((cardNumber != null && !cardNumber.isEmpty()) || (customerId != null && !customerId.isEmpty())){
						contactsLoyaltyObj = contactsLoyaltyDao.getContactsLoyaltyByCustId(cardNumber,customerId,user.getUserId());
						
					}else if((email != null && !email.isEmpty()) || (phone != null && !phone.isEmpty()) ){
						
						String phoneParse = Utility.phoneParse(phone.trim(),
								user != null ? user.getUserOrganization() : null);
						
						contactsLoyaltyObj = contactsLoyaltyDao.findBy(user, email, phoneParse);
					}

					//ContactsLoyalty contactsLoyaltyObj = contactsLoyaltyDao.getContactsLoyaltyByCustId(cardNumber,customerId,user.getUserId());

					// cardnum = loyaltpoints(1111) or cardnumber in loyaltyalance (valuecode)

					if(contactsLoyaltyObj==null)	{
						// add new error code
						statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
							return coupnCodeEnqResponse;
						}

					}
					
					
					
					CouponCodeProcessHelper helper = new CouponCodeProcessHelper();
					List<Coupons> filteredList = new ArrayList<Coupons>();
					//check for the redemption limit 
					for (Coupons coupObj : listPromoCoupons) {
						
						
						statusInfo = validateCoupon(coupObj,user);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							logger.debug("validity failed =="+coupObj.getCouponCode());
							continue;
						}
						
						statusInfo = validateStoreNum(couponCodeInfoObj,coupObj);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							logger.debug("store check failed =="+coupObj.getCouponCode());
							continue;
						}
						statusInfo = validatePromoListSize(coupObj, customerId, email, phone, 
								coupObj.getCouponCode(), user.getUserOrganization().getUserOrgId(), cardNumber);
						
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							logger.debug("redemption per sub limit exceed for this coupon =="+coupObj.getCouponCode());
							continue;
						}
						//check if any persublimit exceeds && check for the redemption limit 
						statusInfo = helper.checkSinglePromocode(couponCodeInfoObj,coupObj,user);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							logger.debug("redemption limit exceed for this coupon =="+coupObj.getCouponCode());
							continue;
						}
						
						filteredList.add(coupObj);
					}
					
					if(filteredList.isEmpty()){
						statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
							return coupnCodeEnqResponse;
						}
					}
					
						String rewardValueCodes = Constants.STRING_NILL;
						Map<String, LoyaltyBalance> balancesMap = new HashMap<String, LoyaltyBalance>();
						Map<Long, LoyaltyMemberItemQtyCounter> memberQtyMap = new HashMap<Long, LoyaltyMemberItemQtyCounter>();
						String rewardIds = Constants.STRING_NILL;
						for (Coupons rewards : filteredList) {
							
							//if(isStorePromoExists && !coupons.isAccumulateOtherPromotion()) continue;
							if(rewards.getValueCode() != null && 
									!rewards.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
								if(!rewardValueCodes.isEmpty()) rewardValueCodes += ",";
								rewardValueCodes += "'"+rewards.getValueCode()+"'";
								
								if(rewards.getSpecialRewadId() != null){
									if(!rewardIds.isEmpty()) rewardIds += ",";
									rewardIds += rewards.getSpecialRewadId().longValue();
								}
							}
						}
						if(!rewardIds.isEmpty()) {
							List<LoyaltyMemberItemQtyCounter> retList = null;
							retList= loyaltyMemberItemQtyCounterDao.findItemsCounter(rewardIds, contactsLoyaltyObj.getLoyaltyId());
							if(retList != null && !retList.isEmpty()) {
								for (LoyaltyMemberItemQtyCounter loyaltyMemberItemQtyCounter : retList) {
									
									memberQtyMap.put(loyaltyMemberItemQtyCounter.getSPRuleID(), loyaltyMemberItemQtyCounter);
								}
							}
						}
						if(!rewardValueCodes.isEmpty()){
							List<LoyaltyBalance> balances = loyaltyBalanceDao.findAllBy(user.getUserId(), contactsLoyaltyObj.getProgramId(),
									contactsLoyaltyObj.getLoyaltyId(),  rewardValueCodes);
							if(balances != null && !balances.isEmpty()){
								for (LoyaltyBalance loyaltyBalance : balances) {
									
									balancesMap.put(loyaltyBalance.getValueCode(), loyaltyBalance);
								}
							}
						}
					for (Coupons coupons : filteredList) {
						
						//if(isStorePromoExists && !coupons.isAccumulateOtherPromotion()) continue;
						boolean iselegeble = false;
						if(coupons.getValueCode() != null && 
								!coupons.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
							
							LoyaltyBalance balance = balancesMap.get(coupons.getValueCode());//loyaltyBalanceDao.findBy(user.getUserId(), contactsLoyaltyObj.getProgramId(),			contactsLoyaltyObj.getLoyaltyId(),  coupons.getValueCode());
							if((balance != null && coupons.getRequiredLoyltyPoits()!=null && balance.getBalance()>=coupons.getRequiredLoyltyPoits()) || (coupons.getRequiredLoyltyPoits()==null && coupons.getMultiplierValue()!=null)) iselegeble = true;
							
							coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
									coupons,false,coupons.getCouponCode(), user, null, requestingFromNewPlugin,
									posMappingMap, true, iselegeble, balance, contactsLoyaltyObj.getLoyaltyId(),memberQtyMap, null, contactsLoyaltyObj );
							//balanceList.add(balance);
							//finalList.add(coupons);
						}
						
					}
					for (Coupons coupons : filteredList) {
						if(coupons.getValueCode() == null || coupons.getValueCode().equals(OCConstants.LOYALTY_POINTS)){
							
							if(contactsLoyaltyObj.getLoyaltyBalance() == null || (contactsLoyaltyObj.getLoyaltyBalance() != null && coupons.getRequiredLoyltyPoits()!=null &&  contactsLoyaltyObj.getLoyaltyBalance()< coupons.getRequiredLoyltyPoits())) continue;
							coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
									coupons,false,coupons.getCouponCode(), user, null, requestingFromNewPlugin, 
									posMappingMap, false, false, null, null, null, contactsLoyaltyObj.getLoyaltyBalance(), contactsLoyaltyObj);
						}
						
						
					}
					
					
					boolean isDiscountNotExist = false;			
					//logger.info("coupDiscInfoList is  :: "+coupDiscInfoList.size());

					if((coupDiscInfoList == null || coupDiscInfoList.size() == 0)) {
						isDiscountNotExist = true;

					}else {
						if(!requestingFromNewPlugin) {
							boolean allNotAvailable = false;
							//get all not availableCount 
							int allcount = 0;
							for (CouponDiscountInfo couponDiscountInfo : coupDiscInfoList) {
								List<DiscountInfo> eachDiscInfo = couponDiscountInfo.getDISCOUNTINFO();
								
								if(eachDiscInfo == null || eachDiscInfo.isEmpty()) allcount += 1;
							}
							if(allcount == coupDiscInfoList.size()) isDiscountNotExist =true;
							
						}
					}

					if(isDiscountNotExist) {
						statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
							return coupnCodeEnqResponse;
						}
					}
					
					
					
					if(requestingFromNewPlugin){
						coupDiscInfoList = reArrangeForNudge(coupDiscInfoList);
						
						loyaltyInfo = prepareLoyaltyInfo(contactsLoyaltyObj, couponCodeEnquObj, user, true);
					}

				//}
				/*if((coupDiscInfoList == null || coupDiscInfoList.size() == 0)) {
						isDiscountNotExist = true;

					}else {
						CouponDiscountInfo coupoDisInfoObj = coupDiscInfoList.get(0);
						if(coupoDisInfoObj != null) {
							List<DiscountInfo> discountList = coupoDisInfoObj.getDISCOUNTINFO();
							if(discountList == null || discountList.size() == 0) {
								isDiscountNotExist = true;
							}
						}
					}

					if(isDiscountNotExist) {
						statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
							return coupnCodeEnqResponse;
						}
					}
				 */
			}else if(coupCodeStr.equalsIgnoreCase("ALL") || 
					coupCodeStr.trim().isEmpty()){
				
				//send all single(nonloyalty)+loyalty
				//if(user.isNewPlugin()) {
					
					List<Coupons> multipleRandom = null;
					List<Coupons> singlePromos = null;
					List<Coupons> loyaltyPromos = null;
					
					CouponCodeInfo couponCodeInfo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO();
					String custSID = couponCodeInfo.getCUSTOMERID();
					String email = couponCodeInfo.getEMAIL();
					String phone = couponCodeInfo.getPHONE();
					
					if(phone != null && !phone.isEmpty()) phone = LoyaltyProgramHelper.preparePhoneNumber(user, phone); //APP-4380
					
					singlePromos = couponsDao.getAllSinglePromos(user.getUserOrganization().getUserOrgId()); 
					
					String cardNumber = couponCodeInfo.getCARDNUMBER();
					ContactsLoyalty contactsLoyaltyObj = null;
					//if(cardNumber!=null && !cardNumber.trim().isEmpty() ) {
					
					if((cardNumber != null && !cardNumber.isEmpty()) || (custSID != null && !custSID.isEmpty())){
						contactsLoyaltyObj = contactsLoyaltyDao.getContactsLoyaltyByCustId(cardNumber,custSID,user.getUserId());
						
					}else if((email != null && !email.isEmpty()) || (phone != null && !phone.isEmpty()) ){
						String phoneParse =   phone != null && !phone.isEmpty() ? Utility.phoneParse(phone.trim(),
								user != null ? user.getUserOrganization() : null) : null;
						
						contactsLoyaltyObj = contactsLoyaltyDao.findBy(user, email, phoneParse);
					}
					singlePromos = couponsDao.getAllSinglePromos(user.getUserOrganization().getUserOrgId()); 
										
					if((custSID != null && !custSID.trim().isEmpty()) || 
							(email != null && !email.isEmpty())||
							(phone != null && !phone.isEmpty()) || 
							(contactsLoyaltyObj != null)){
						
						multipleRandom = couponsDao.getAllMultiplePromos(user.getUserOrganization().getUserOrgId());
						
						String multipleCodes ="";
						if(multipleRandom != null && !multipleRandom.isEmpty()){
							for (Coupons coupons : multipleRandom) {
								if(!multipleCodes.isEmpty()) multipleCodes += ",";
								multipleCodes += coupons.getCouponId().longValue();
							}
							List<CouponCodes> multipleRandomCodes =  null;
							
					
							if((custSID == null || custSID.trim().isEmpty()) && 
									(email == null || email.isEmpty())&&
									(phone == null || phone.isEmpty()) && 
									(contactsLoyaltyObj != null) ){
								if(contactsLoyaltyObj.getMobilePhone() != null && 
										!contactsLoyaltyObj.getMobilePhone().isEmpty()){
									
									multipleRandomCodes = couponCodesDao.getAllMultipleCodesIssuedIfAny(user.getUserOrganization().getUserOrgId(),
											multipleCodes,custSID, email, contactsLoyaltyObj.getMobilePhone());
									
								}
								if(multipleRandomCodes == null || multipleRandomCodes.isEmpty()){
									
									if(contactsLoyaltyObj.getEmailId() != null && 
											!contactsLoyaltyObj.getEmailId().isEmpty()){
										
										multipleRandomCodes = couponCodesDao.getAllMultipleCodesIssuedIfAny(user.getUserOrganization().getUserOrgId(),
												multipleCodes,custSID, contactsLoyaltyObj.getEmailId(), phone);
									}
								}
							
								
							}else if((custSID == null || custSID.trim().isEmpty()) && 
									(email == null || email.isEmpty())&&
									(phone != null && !phone.isEmpty()) && 
									(contactsLoyaltyObj != null) ){
								
								multipleRandomCodes = couponCodesDao.getAllMultipleCodesIssuedIfAny(user.getUserOrganization().getUserOrgId(),
										multipleCodes,custSID, email, phone);
								
								if(multipleRandomCodes == null || multipleRandomCodes.isEmpty()){
									
									if(contactsLoyaltyObj.getEmailId() != null && 
											!contactsLoyaltyObj.getEmailId().isEmpty()){
										
										multipleRandomCodes = couponCodesDao.getAllMultipleCodesIssuedIfAny(user.getUserOrganization().getUserOrgId(),
												multipleCodes,custSID, contactsLoyaltyObj.getEmailId(), phone);
									}
								}
							}else if((custSID == null || custSID.trim().isEmpty()) && 
									(email != null && !email.isEmpty())&&
									(phone == null || phone.isEmpty()) && 
									(contactsLoyaltyObj != null) ){
								
								multipleRandomCodes = couponCodesDao.getAllMultipleCodesIssuedIfAny(user.getUserOrganization().getUserOrgId(),
										multipleCodes,custSID, email, phone);
								
								if(multipleRandomCodes == null || multipleRandomCodes.isEmpty()){
									
									if(contactsLoyaltyObj.getEmailId() != null && 
											!contactsLoyaltyObj.getEmailId().isEmpty()){
										
										multipleRandomCodes = couponCodesDao.getAllMultipleCodesIssuedIfAny(user.getUserOrganization().getUserOrgId(),
												multipleCodes,custSID, email, contactsLoyaltyObj.getMobilePhone());
									}
								}
							}
							if(multipleRandomCodes == null || multipleRandomCodes.isEmpty()){
								
								multipleRandomCodes = couponCodesDao.getAllMultipleCodesIssuedIfAny(user.getUserOrganization().getUserOrgId(),
										multipleCodes,custSID, email, phone);
							}
							if(multipleRandomCodes != null && !multipleRandomCodes.isEmpty()){
								
								multipleRandom = new ArrayList<Coupons>();
								for (CouponCodes codes : multipleRandomCodes) {
									Coupons coupon = codes.getCouponId();
									if(!coupon.getStatus().equals("Running")) continue;
									
									statusInfo = validateDynamicCoupon(coupon, codes);
									if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
										continue;
									}
									
									statusInfo = validateCoupon(coupon,user);
									if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
										logger.debug("validity failed =="+codes);
										continue;
									}
									
									statusInfo = validateStoreNum(couponCodeInfoObj,coupon);
									if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
										logger.debug("store check failed =="+codes);
										continue;
									}
									
									//check if any persublimit exceeds && check for the redemption limit 
									/*statusInfo = helper.checkSinglePromocode(couponCodeInfoObj,coupObj,user);
									if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
										logger.debug("redemption limit exceed for this coupon =="+coupObj.getCouponCode());
										continue;
									}*/
									coupon.setCouponCode(codes.getCouponCode());
									coupon.setCouponCodeObj(codes);
									
									
									multipleRandom.add(coupon);
								}
								
							}else{
								multipleRandom=null;
							}
						}
						
					
					}
					
					
					
						//contactsLoyaltyObj = contactsLoyaltyDao.getContactsLoyaltyByCustId(cardNumber,custSID,user.getUserId());
						if(contactsLoyaltyObj != null) {
							
							loyaltyPromos = couponsDao.getAllLoyaltyPromos(user.getUserOrganization().getUserOrgId());
						}
						
					//}

					if((loyaltyPromos ==null || loyaltyPromos.isEmpty()) &&
							(singlePromos == null || singlePromos.isEmpty()) && 
							(multipleRandom == null || multipleRandom.isEmpty())) { // needed a new error code
						if(requestingFromNewPlugin && contactsLoyaltyObj == null){
							statusInfo = new StatusInfo("100022",PropertyUtil.getErrorMessage(100022,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);
							return coupnCodeEnqResponse;
						}
						statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							if(requestingFromNewPlugin && contactsLoyaltyObj != null){
								statusInfo = new StatusInfo("0","Promo-code enquiry is successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
								loyaltyInfo = prepareLoyaltyInfo(contactsLoyaltyObj, couponCodeEnquObj, user, true);
							}
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo, loyaltyInfo);	
							return coupnCodeEnqResponse;
						}
						
					}
					/*boolean isStorePromoExists = false;
					String reciptdDiscount = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getDISCOUNTAMOUNT();

					isStorePromoExists = reciptdDiscount != null && !reciptdDiscount.isEmpty() && Double.parseDouble(reciptdDiscount) > 0;

					if(!isStorePromoExists) {
						List<PurchasedItems> purchaseList=couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS();
						if(purchaseList != null && purchaseList.size() > 0) {

							for (PurchasedItems tempObj : purchaseList) {

								String ItemDiscount = tempObj.getITEMDISCOUNT();

								isStorePromoExists = ItemDiscount != null && !ItemDiscount.isEmpty() && Double.parseDouble(ItemDiscount) > 0;
								if(isStorePromoExists) break;	
							} //for
						}


					}
*/
						//List<Coupons> finalList = new ArrayList<Coupons>();
						//List<LoyaltyBalance> balanceList = new ArrayList<LoyaltyBalance>();
						
					CouponCodeProcessHelper helper = new CouponCodeProcessHelper();
						if(loyaltyPromos != null && !loyaltyPromos.isEmpty()) {
							String rewardValueCodes = Constants.STRING_NILL;
							Map<String, LoyaltyBalance> balancesMap = new HashMap<String, LoyaltyBalance>();
							Map<Long, LoyaltyMemberItemQtyCounter> memberQtyMap = new HashMap<Long, LoyaltyMemberItemQtyCounter>();
							String rewardIds = Constants.STRING_NILL;
							List<Coupons> filteredList = new ArrayList<Coupons>();
							//check for the redemption limit 
							for (Coupons coupObj : loyaltyPromos) {
								
								
								statusInfo = validateCoupon(coupObj,user);
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("validity failed =="+coupObj.getCouponCode());
									continue;
								}
								
								statusInfo = validateStoreNum(couponCodeInfoObj,coupObj);
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("store check failed =="+coupObj.getCouponCode());
									continue;
								}
								statusInfo = validatePromoListSize(coupObj, custSID, email, phone, 
										coupObj.getCouponCode(), user.getUserOrganization().getUserOrgId(), cardNumber);
								
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("redemption per sub limit exceed for this coupon =="+coupObj.getCouponCode());
									continue;
								}
								
								//check if any persublimit exceeds && check for the redemption limit 
								statusInfo = helper.checkSinglePromocode(couponCodeInfoObj,coupObj,user);
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("redemption limit exceed for this coupon =="+coupObj.getCouponCode());
									continue;
								}
								
								filteredList.add(coupObj);
							}
							
							
							
							for (Coupons rewards : filteredList) {
								
								//if(isStorePromoExists && !coupons.isAccumulateOtherPromotion()) continue;
								if(rewards.getValueCode() != null && 
										!rewards.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
									if(!rewardValueCodes.isEmpty()) rewardValueCodes += ",";
									rewardValueCodes += "'"+rewards.getValueCode()+"'";
									
									if(rewards.getSpecialRewadId() != null){
										if(!rewardIds.isEmpty()) rewardIds += ",";
										rewardIds += rewards.getSpecialRewadId().longValue();
									}
								}
							}
							if(!rewardIds.isEmpty()) {
								List<LoyaltyMemberItemQtyCounter> retList = null;
								retList= loyaltyMemberItemQtyCounterDao.findItemsCounter(rewardIds, contactsLoyaltyObj.getLoyaltyId());
								if(retList != null && !retList.isEmpty()) {
									for (LoyaltyMemberItemQtyCounter loyaltyMemberItemQtyCounter : retList) {
										
										memberQtyMap.put(loyaltyMemberItemQtyCounter.getSPRuleID(), loyaltyMemberItemQtyCounter);
									}
								}
							}
							if(!rewardValueCodes.isEmpty()){
								List<LoyaltyBalance> balances = loyaltyBalanceDao.findAllBy(user.getUserId(), contactsLoyaltyObj.getProgramId(),
										contactsLoyaltyObj.getLoyaltyId(),  rewardValueCodes);
								if(balances != null && !balances.isEmpty()){
									for (LoyaltyBalance loyaltyBalance : balances) {
										
										balancesMap.put(loyaltyBalance.getValueCode(), loyaltyBalance);
									}
								}
							}
							for (Coupons coupons : filteredList) {
								
								//if(isStorePromoExists && !coupons.isAccumulateOtherPromotion()) continue;
								boolean iselegeble = false;
								if(coupons.getValueCode() != null && 
										!coupons.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
									//logger.debug("noReceiptData==="+noReceiptData+
										//	" coupons.getSpecialRewadId() ==="+coupons.getSpecialRewadId()+" memberQtyMap.containsKey(coupons.getSpecialRewadId())=="+memberQtyMap.containsKey(coupons.getSpecialRewadId())+ONESPACE+memberQtyMap.get(coupons.getSpecialRewadId()));
									if(noReceiptData && coupons.getSpecialRewadId() != null && !memberQtyMap.containsKey(coupons.getSpecialRewadId())){
										//logger.debug("continue===no need to check");
										continue;
									}
									LoyaltyBalance balance = balancesMap.get(coupons.getValueCode());//loyaltyBalanceDao.findBy(user.getUserId(), contactsLoyaltyObj.getProgramId(),			contactsLoyaltyObj.getLoyaltyId(),  coupons.getValueCode());
									if((balance != null && coupons.getRequiredLoyltyPoits()!=null && balance.getBalance()>=coupons.getRequiredLoyltyPoits()) || (coupons.getRequiredLoyltyPoits()==null && coupons.getMultiplierValue()!=null)) iselegeble = true; 
									coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
											coupons,false,coupons.getCouponCode(), user, null, requestingFromNewPlugin, posMappingMap, 
											true, iselegeble, balance, contactsLoyaltyObj.getLoyaltyId(), memberQtyMap,null, contactsLoyaltyObj);
									//balanceList.add(balance);
									//finalList.add(coupons);
								}
								
							}
							for (Coupons coupons : filteredList) {
								if(coupons.getValueCode() == null || coupons.getValueCode().equals(OCConstants.LOYALTY_POINTS)){
									if(contactsLoyaltyObj.getLoyaltyBalance() == null || (contactsLoyaltyObj.getLoyaltyBalance() != null && coupons.getRequiredLoyltyPoits()!=null && contactsLoyaltyObj.getLoyaltyBalance()< coupons.getRequiredLoyltyPoits())) continue;
									if(!noReceiptData)coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
											coupons,false,coupons.getCouponCode(), user, null, requestingFromNewPlugin, posMappingMap, 
											false, false, null, contactsLoyaltyObj.getLoyaltyId(), null, contactsLoyaltyObj.getLoyaltyBalance(), contactsLoyaltyObj);
								}
								
								
							}
							
						}

						if(!noReceiptData && multipleRandom != null && !multipleRandom.isEmpty()) {
							
							for (Coupons coupObj : multipleRandom) {
								logger.debug("coupObj n status ==="+coupObj.getCouponName()+ONESPACE+coupObj.getStatus());
								
								
								//if(isStorePromoExists && !coupObj.isAccumulateOtherPromotion()) continue;
								coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
										coupObj,false,coupObj.getCouponCode(), user, coupObj.getCouponCodeObj(), 
										requestingFromNewPlugin, posMappingMap, false, false, null, null, null, null, contactsLoyaltyObj);
							}
						}
						if(!noReceiptData && singlePromos != null && !singlePromos.isEmpty()) {
							List<Coupons> filteredList = new ArrayList<Coupons>();
							for (Coupons coupObj : singlePromos) {
								
								//check for the redemption limit 
									
									
								statusInfo = validateCoupon(coupObj,user);
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("validity failed =="+coupObj.getCouponCode());
									continue;
								}
								
								statusInfo = validateStoreNum(couponCodeInfoObj,coupObj);
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("store check failed =="+coupObj.getCouponCode());
									continue;
								}
								
								statusInfo = validatePromoListSize(coupObj, custSID, email, phone, 
										coupObj.getCouponCode(), user.getUserOrganization().getUserOrgId(), cardNumber);
								
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("redemption per sub limit exceed for this coupon =="+coupObj.getCouponCode());
									continue;
								}
								//check if any persublimit exceeds && check for the redemption limit 
								statusInfo = helper.checkSinglePromocode(couponCodeInfoObj,coupObj,user);
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("redemption limit exceed for this coupon =="+coupObj.getCouponCode());
									continue;
								}
								filteredList.add(coupObj);	
								//if(isStorePromoExists && !coupObj.isAccumulateOtherPromotion()) continue;
							}
							if(!filteredList.isEmpty()) {
								for (Coupons coupObj : filteredList) {
									
									logger.debug("coupObj n status ==="+coupObj.getCouponName()+ONESPACE+coupObj.getStatus());
								coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
										coupObj,false,coupObj.getCouponCode(), user, null, requestingFromNewPlugin,
										posMappingMap, false, false, null, null, null, null, contactsLoyaltyObj);
								
								}
							}
							
						}
						
						
						boolean isDiscountNotExist = false;			
						//logger.info("coupDiscInfoList is  :: "+coupDiscInfoList.size());

						if((coupDiscInfoList == null || coupDiscInfoList.size() == 0)) {
							isDiscountNotExist = true;

						}/*else {
							CouponDiscountInfo coupoDisInfoObj = coupDiscInfoList.get(0);
							if(coupoDisInfoObj != null ) {
								List<DiscountInfo> discountList = coupoDisInfoObj.getDISCOUNTINFO();
								if(discountList == null || discountList.size() == 0) {
									isDiscountNotExist = true;
								}
							}
						}*/
						

						if(isDiscountNotExist) {
							if(requestingFromNewPlugin && contactsLoyaltyObj == null){
								statusInfo = new StatusInfo("100022",PropertyUtil.getErrorMessage(100022,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);
								return coupnCodeEnqResponse;
							}
							statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
								if(requestingFromNewPlugin && contactsLoyaltyObj != null){
									statusInfo = new StatusInfo("0","Promo-code enquiry is successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
									//coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo, loyaltyInfo);
									loyaltyInfo = prepareLoyaltyInfo(contactsLoyaltyObj, couponCodeEnquObj, user, true);
								}
								coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo, loyaltyInfo);	
								return coupnCodeEnqResponse;
							}
						}
						if(requestingFromNewPlugin) {
							List<CouponDiscountInfo> finalDiscounts = new ArrayList<CouponDiscountInfo>();
							List<CouponDiscountInfo> receiptTypeCoupons = new ArrayList<CouponDiscountInfo>();
							List<CouponDiscountInfo> appliedReceiptLevelSet = new ArrayList<CouponDiscountInfo>();
							List<CouponDiscountInfo> appliedProductLevelSet = new ArrayList<CouponDiscountInfo>();
							List<CouponDiscountInfo> receiptTypeLtyCoupons = new ArrayList<CouponDiscountInfo>();
							
							List<CouponDiscountInfo> ItemBasedCoupons = new ArrayList<CouponDiscountInfo>();
							for (CouponDiscountInfo couponDiscountInfo : coupDiscInfoList) {
								logger.debug("couponcode is ==="+couponDiscountInfo.getCOUPONCODE());
								if(couponDiscountInfo.getCOUPONTYPE().equals("REWARDS") || couponDiscountInfo.getCOUPONTYPE().equals("LOYALTY")){
									if(	(user.isShowOnlyHighestLtyDC() && couponDiscountInfo.getCOUPONTYPE().equals("LOYALTY"))){
										if(couponDiscountInfo.getDISCOUNTCRITERIA().equals("PERCENTAGE-MVP") || 
												couponDiscountInfo.getDISCOUNTCRITERIA().equals("VALUE-MVP")){
											receiptTypeLtyCoupons.add(couponDiscountInfo);
										}
									}else{
										
										finalDiscounts.add(couponDiscountInfo);
									}
								}else if(couponDiscountInfo.getCOUPONTYPE().equals("COUPONS") && couponDiscountInfo.getNUDGEPROMOCODE().equals("YES")){
									finalDiscounts.add(couponDiscountInfo);
								}
								
							}
							List<String>  appliedPromo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getAPPLIEDCOUPONS();
							Set<String> appliedSet = null;
							if( appliedPromo!= null && !appliedPromo.isEmpty()){
								appliedSet = new HashSet<String>();
								appliedSet.addAll(appliedPromo);
							}
							for (CouponDiscountInfo couponDiscountInfo : coupDiscInfoList) {
								logger.debug("coupon code + nudge + applied==="+couponDiscountInfo.getCOUPONCODE()+ONESPACE+couponDiscountInfo.getNUDGEPROMOCODE()+ONESPACE+couponDiscountInfo.getAPPLIEDCOUPONS());;
							}
							for (CouponDiscountInfo couponDiscountInfo : coupDiscInfoList) {
								if(couponDiscountInfo.getCOUPONTYPE().equals("PROMOTIONS") || 
										couponDiscountInfo.getCOUPONTYPE().equals("COUPONS")){//only regular promos give relevant
									
									if(couponDiscountInfo.getNUDGEPROMOCODE().equals("YES")) continue;
									if(couponDiscountInfo.getDISCOUNTCRITERIA().equals("PERCENTAGE-MVP") || 
											couponDiscountInfo.getDISCOUNTCRITERIA().equals("VALUE-MVP")){
										if(appliedSet != null && !appliedSet.isEmpty() && appliedSet.contains(couponDiscountInfo.getCOUPONCODE())){
											appliedReceiptLevelSet.add(couponDiscountInfo);
											continue;
										}
										receiptTypeCoupons.add(couponDiscountInfo);
									}else if(couponDiscountInfo.getDISCOUNTCRITERIA().equals("PERCENTAGE-IC") || 
											couponDiscountInfo.getDISCOUNTCRITERIA().equals("VALUE-I")  ){
										if(appliedSet != null && !appliedSet.isEmpty() && appliedSet.contains(couponDiscountInfo.getCOUPONCODE())){
											appliedProductLevelSet.add(couponDiscountInfo);
											continue;
										}
										ItemBasedCoupons.add(couponDiscountInfo);
									}
									
								}
								
								
							}
							if(receiptTypeLtyCoupons.size() > 0){//to show only hghest loyalty DC
								Double discount = 0.0;
								CouponDiscountInfo  finalReceiptCoupon = null;
								for (CouponDiscountInfo  receiptCoupon: receiptTypeLtyCoupons) {
									if(receiptCoupon.getNUDGEPROMOCODE().equals("YES")){
										finalDiscounts.add(receiptCoupon);
										continue;
									}
									DiscountInfo oneDiscInfo = receiptCoupon.getDISCOUNTINFO().get(0);
									Double disc = Double.parseDouble(oneDiscInfo.getMAXRECEIPTDISCOUNT());
									if(discount == 0 || discount<disc) {
										discount = disc;
										finalReceiptCoupon = receiptCoupon;
									}
								}
								if(finalReceiptCoupon != null) {
									//CouponDescriptionAlgorithm couponDesc = new CouponDescriptionAlgorithm();
									//finalReceiptCoupon = couponDesc.preparecouponDisc(finalReceiptCoupon, user);
									finalDiscounts.add(finalReceiptCoupon);
								}
							}
							if(receiptTypeCoupons.size() > 0){
								Double discount = 0.0;
								CouponDiscountInfo  finalReceiptCoupon = null;
								if( user.isShowOnlyHighestDiscReceiptDC()){
									for (CouponDiscountInfo  receiptCoupon: receiptTypeCoupons) {
										/*if(appliedSet != null && !appliedSet.isEmpty() && appliedSet.contains(receiptCoupon.getCOUPONCODE())){
											finalDiscounts.add(receiptCoupon);
											continue;
										}*/
										if(receiptCoupon.getNUDGEPROMOCODE().equals("YES")){
											finalDiscounts.add(receiptCoupon);
											continue;
										}
										DiscountInfo oneDiscInfo = receiptCoupon.getDISCOUNTINFO().get(0);
										Double disc = Double.parseDouble(oneDiscInfo.getMAXRECEIPTDISCOUNT());
										if(discount == 0 || discount<disc) {
											discount = disc;
											finalReceiptCoupon = receiptCoupon;
										}
									}
									if(finalReceiptCoupon != null) {
										//CouponDescriptionAlgorithm couponDesc = new CouponDescriptionAlgorithm();
										//finalReceiptCoupon = couponDesc.preparecouponDisc(finalReceiptCoupon, user);
										finalDiscounts.add(finalReceiptCoupon);
									}
									
								}else{
									finalDiscounts.addAll(receiptTypeCoupons);
								}
							}
							if(appliedReceiptLevelSet.size() > 0) finalDiscounts.addAll(appliedReceiptLevelSet);
							//finalDiscounts.addAll(ItemBasedCoupons);
							if(ItemBasedCoupons.size() > 0) {
								//finalDiscounts.addAll(ItemBasedCoupons);
								
								List<PurchasedItems> purchaseList=couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS();
								Map<String, CouponDiscountInfo> itemFinalDiscount = new HashMap<String, CouponDiscountInfo>();
								Map<String, Double> itemDisc = new HashMap<String, Double>();
								Map<String, Set<String>> itemDiscCodes = new HashMap<String, Set<String>>();
								for (PurchasedItems purchasedItems : purchaseList) {
									
									for (CouponDiscountInfo itemBasedCoupon : ItemBasedCoupons) {
										logger.debug("promotion =="+itemBasedCoupon.getCOUPONCODE());
										List<DiscountInfo> itemDiscounts = itemBasedCoupon.getDISCOUNTINFO();
										logger.debug("itemDiscounts "+itemDiscounts.size());
										for (DiscountInfo discountInfo : itemDiscounts) {
											
											List<ItemCodeInfo> itemCodeInfo = discountInfo.getITEMCODEINFO();
											for (ItemCodeInfo itemCode : itemCodeInfo) {
												if(itemCode.getMAXITEMDISCOUNT() == null || itemCode.getMAXITEMDISCOUNT().isEmpty()){
													
													logger.debug("No max discount is given ==");
													continue;
												}
												if(purchasedItems.getITEMCODE().equalsIgnoreCase(itemCode.getITEMCODE())){
													logger.debug("item satsfied=="+purchasedItems.getITEMCODE()+ "==on Disc =="+itemCode.getMAXITEMDISCOUNT() );
													if(itemFinalDiscount.get(itemCode.getITEMCODE()) == null){
														itemDisc.put(itemCode.getITEMCODE(), Double.parseDouble(itemCode.getMAXITEMDISCOUNT()));
														itemFinalDiscount.put(itemCode.getITEMCODE(), itemBasedCoupon);
														Set<String> itemcodeSet = new HashSet<String>();
														itemcodeSet.add(itemCode.getITEMCODE());
														itemDiscCodes.put(itemBasedCoupon.getCOUPONCODE(), itemcodeSet);
													}else{
														if(itemDisc.get(itemCode.getITEMCODE()) <  Double.parseDouble(itemCode.getMAXITEMDISCOUNT()) ){
															
															itemDisc.put(itemCode.getITEMCODE(), Double.parseDouble(itemCode.getMAXITEMDISCOUNT()));
															itemFinalDiscount.put(itemCode.getITEMCODE(), itemBasedCoupon);
															Set<String> itemcodeSet = itemDiscCodes.get(itemBasedCoupon.getCOUPONCODE());
															if(itemcodeSet == null) itemcodeSet =  new HashSet<String>();
															itemcodeSet.add(itemCode.getITEMCODE());
															itemDiscCodes.put(itemBasedCoupon.getCOUPONCODE(), itemcodeSet);
														}
														
													}
													
												}
												
											}
											
										}
										
									}//for each coupon
									
								}//for each item
								
								List<CouponDiscountInfo> itemsFinalDiscount = new ArrayList<CouponDiscountInfo>();
								
								logger.debug("size of the final promos==="+itemFinalDiscount.size());
								Set<String> couponcodes = new HashSet<String>();
								for (String itemCodes : itemFinalDiscount.keySet()) {
									CouponDiscountInfo itemDicountInfo = itemFinalDiscount.get(itemCodes);
									logger.debug("item==="+itemCodes+" ==promotion=="+itemDicountInfo.getCOUPONCODE());
									if(couponcodes.contains(itemCodes)) {
										logger.debug("==yes==");
										continue;
									}
									List<DiscountInfo> discountInfoLst = new ArrayList<DiscountInfo>();
									for (DiscountInfo itemDiscInfo : itemDicountInfo.getDISCOUNTINFO()) {
										List<ItemCodeInfo> items = itemDiscInfo.getITEMCODEINFO();
										Set<String> itemcodeSet = itemDiscCodes.get(itemDicountInfo.getCOUPONCODE());
										List<ItemCodeInfo> newItemInfo = new ArrayList<ItemCodeInfo>();
										//logger.debug("size of the final promos==="+items.size());

										for (ItemCodeInfo itemCodeInfo : items) {
											//logger.debug("size of the final promos==="+itemCodeInfo.getITEMCODE()+ONESPACE+itemcodeSet);
											logger.debug("itemCodeInfo==="+itemCodeInfo.getITEMCODE()+ONESPACE+ONESPACE+
											itemcodeSet.contains(itemCodeInfo.getITEMCODE())+itemCodeInfo.getMAXITEMDISCOUNT()+ONESPACE+itemDisc.get(itemCodeInfo.getITEMCODE()));

											if(itemDisc.containsKey(itemCodeInfo.getITEMCODE()) && !couponcodes.contains(itemCodeInfo.getITEMCODE()) &&
													itemDisc.get(itemCodeInfo.getITEMCODE()).doubleValue() ==Double.parseDouble(itemCodeInfo.getMAXITEMDISCOUNT())){
												logger.debug("==exists==");
												newItemInfo.add(itemCodeInfo);
												couponcodes.add(itemCodeInfo.getITEMCODE());
											}
											
										}
										if(newItemInfo.size() > 0){
											
											itemDiscInfo.setITEMCODEINFO(newItemInfo);
											discountInfoLst.add(itemDiscInfo);
										}
										//logger.debug("size of the final promos==="+couponcodes.size());
									}
									if(discountInfoLst.size()>0){
										itemDicountInfo.setDISCOUNTINFO(discountInfoLst);
										//CouponDescriptionAlgorithm couponDescriptionAlgorithm = new CouponDescriptionAlgorithm();
										//itemDicountInfo = couponDescriptionAlgorithm.preparecouponDisc(itemDicountInfo, user, true);
										itemsFinalDiscount.add(itemDicountInfo);
									}
									//logger.debug("size of the final promos==="+itemsFinalDiscount.size());
								}
								//logger.debug("size of the final promos==="+couponcodes.size()+itemsFinalDiscount.size());
								if(itemsFinalDiscount.size() > 0) finalDiscounts.addAll(itemsFinalDiscount);
								
							}//if on product based exists
							if(appliedProductLevelSet.size() > 0) finalDiscounts.addAll(appliedProductLevelSet);
							finalDiscounts = reArrangeForNudge(finalDiscounts);
							logger.debug("===return from reArrangeForNudge==="+finalDiscounts.size());
							coupDiscInfoList = finalDiscounts;
						}
						if(requestingFromNewPlugin && contactsLoyaltyObj != null){
							logger.debug("===prepareloyalty==");
							loyaltyInfo = prepareLoyaltyInfo(contactsLoyaltyObj, couponCodeEnquObj, user, true);
							logger.debug("===prepareloyalty==end");
						}
						if(contactsLoyaltyObj == null){
							logger.debug("===no loyalty==");
							statusInfo = new StatusInfo("100022",PropertyUtil.getErrorMessage(100022,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);
							return coupnCodeEnqResponse;
						}
				//}
				
				logger.debug("return coupons==="+coupDiscInfoList.size());
			}else if(coupCodeStr.equalsIgnoreCase("BALANCES") ){
				
				CouponCodeInfo couponCodeInfo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO();
				String custSID = couponCodeInfo.getCUSTOMERID();
				String email = couponCodeInfo.getEMAIL();
				String phone = couponCodeInfo.getPHONE();
				
				if(phone != null && !phone.isEmpty()) phone = LoyaltyProgramHelper.preparePhoneNumber(user, phone); //APP-4380

				String cardNumber = couponCodeInfo.getCARDNUMBER();
				ContactsLoyalty contactsLoyaltyObj = null;
				//if(cardNumber!=null && !cardNumber.trim().isEmpty() ) {
				if((cardNumber != null && !cardNumber.isEmpty()) || (custSID != null && !custSID.isEmpty())){
					contactsLoyaltyObj = contactsLoyaltyDao.getContactsLoyaltyByCustId(cardNumber,custSID,user.getUserId());
					
				}else if((email != null && !email.isEmpty()) || (phone != null && !phone.isEmpty()) ){
					
					String phoneParse = phone != null && !phone.isEmpty() ? Utility.phoneParse(phone.trim(),
							user != null ? user.getUserOrganization() : null) : null;
					
					contactsLoyaltyObj = contactsLoyaltyDao.findBy(user, email, phoneParse);
				}
				if(contactsLoyaltyObj == null){
					statusInfo = new StatusInfo("100022",PropertyUtil.getErrorMessage(100022,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);
					return coupnCodeEnqResponse;
				}
					
				loyaltyInfo = prepareLoyaltyInfo(contactsLoyaltyObj, couponCodeEnquObj, user, false);
				
			}else if(coupCodeStr.equalsIgnoreCase("SPECIFIC") ){
				
				List<String>  appliedPromo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getAPPLIEDCOUPONS();
				
				if(appliedPromo == null || appliedPromo.isEmpty()){
					statusInfo = new StatusInfo("100023",PropertyUtil.getErrorMessage(100023,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);
					return coupnCodeEnqResponse;
				}
				if(requestingFromNewPlugin && appliedPromo!= null && !appliedPromo.isEmpty()){
					
					ContactsLoyalty contactsLoyaltyObj = null;
					for (String appliedCode : appliedPromo) {
						if(appliedCode.trim().isEmpty()) continue;
						boolean isLOyaltyPromo = false;
						logger.debug("appliedCode ==="+appliedCode);
						Coupons coupObj = null;
						/*CouponCodes  coupCodeObj = couponCodesDao.testForCouponCodes(appliedCode, user.getUserOrganization().getUserOrgId());
						if(coupCodeObj == null) {
							coupObj = couponsDao.findCoupon(appliedCode,user.getUserOrganization().getUserOrgId());

						}else{
							coupObj  = coupCodeObj.getCouponId();
						}*/
						coupObj = getCouponObj(user, appliedCode);
						if(coupObj == null) continue;
						
						
						
						
						
						
						
						if(coupObj.getLoyaltyPoints() != null && 
								(coupObj.getRequiredLoyltyPoits() != null || coupObj.getMultiplierValue()!=null )) { //changed w.r.t APP-2181 i.e to
							//changed when a loyalty promo has requested thru a promo manager(as a regular promo) 
							//with a CODE then also check against the balance 
							isLOyaltyPromo = true;
						}
						
						if(isLOyaltyPromo){
							if(contactsLoyaltyObj == null){
								
								CouponCodeInfo couponCodeInfo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO();
								String custSID = couponCodeInfo.getCUSTOMERID();
								String email = couponCodeInfo.getEMAIL();
								String phone = couponCodeInfo.getPHONE();
								String cardNumber = couponCodeInfo.getCARDNUMBER();
								
								if(phone != null && !phone.isEmpty()) phone = LoyaltyProgramHelper.preparePhoneNumber(user, phone); //APP-4380
								//if(cardNumber!=null && !cardNumber.trim().isEmpty() ) {
								
								if((cardNumber != null && !cardNumber.isEmpty()) || (custSID != null && !custSID.isEmpty())){
									contactsLoyaltyObj = contactsLoyaltyDao.getContactsLoyaltyByCustId(cardNumber,custSID,user.getUserId());
									
								}else if((email != null && !email.isEmpty()) || (phone != null && !phone.isEmpty()) ){
									
									String phoneParse = phone != null && !phone.isEmpty() ? Utility.phoneParse(phone.trim(),
											user != null ? user.getUserOrganization() : null) : null;
									
									contactsLoyaltyObj = contactsLoyaltyDao.findBy(user, email, phoneParse);
								}
							}
							if(contactsLoyaltyObj != null){
								
								boolean iselegeble = false;
								if(coupObj.getValueCode() != null && 
										!coupObj.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
									
									LoyaltyBalance balance = loyaltyBalanceDao.findBy(user.getUserId(), contactsLoyaltyObj.getProgramId(),
											contactsLoyaltyObj.getLoyaltyId(),  coupObj.getValueCode());
									if((balance != null && coupObj.getRequiredLoyltyPoits()!=null && balance.getBalance()>=coupObj.getRequiredLoyltyPoits()) || (coupObj.getRequiredLoyltyPoits()==null && coupObj.getMultiplierValue()!=null)) iselegeble = true;
									coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
											coupObj,false,coupObj.getCouponCode(), user, null, requestingFromNewPlugin, 
											posMappingMap, true, iselegeble, balance, contactsLoyaltyObj.getLoyaltyId(), null, null, contactsLoyaltyObj);
									//balanceList.add(balance);
									//finalList.add(coupons);
								}else if(coupObj.getValueCode() == null || coupObj.getValueCode().equals(OCConstants.LOYALTY_POINTS)){
									
									if(contactsLoyaltyObj.getLoyaltyBalance() != null && coupObj.getRequiredLoyltyPoits()!=null && contactsLoyaltyObj.getLoyaltyBalance()< coupObj.getRequiredLoyltyPoits()) continue;
									coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
											coupObj,false,coupObj.getCouponCode(), user, null, requestingFromNewPlugin, 
											posMappingMap, false, false, null, contactsLoyaltyObj.getLoyaltyId(), null, contactsLoyaltyObj.getLoyaltyBalance(), contactsLoyaltyObj);
								}
								
								
							}else{
								
								logger.debug("===loyalty object didnt find==="+appliedCode);
							}
						}else{
							coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
									coupObj,false,coupObj.getCouponCode(), user, coupObj.getCouponCodeObj(), 
									requestingFromNewPlugin, posMappingMap, false, false, null, null, null, null, contactsLoyaltyObj);
						}
						
						
					}//for
					
					
				}
				
				
			}else if(coupCodeStr.equalsIgnoreCase("DISCOUNTS") ){
				

				
				//send all single(nonloyalty)+loyalty
				//if(user.isNewPlugin()) {
					
					List<Coupons> multipleRandom = null;
					List<Coupons> singlePromos = null;
					List<Coupons> loyaltyPromos = null;
					
					CouponCodeInfo couponCodeInfo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO();
					String custSID = couponCodeInfo.getCUSTOMERID();
					String email = couponCodeInfo.getEMAIL();
					String phone = couponCodeInfo.getPHONE();
					
					if(phone != null && !phone.isEmpty()) phone = LoyaltyProgramHelper.preparePhoneNumber(user, phone); //APP-4380
					
					singlePromos = couponsDao.getAllSinglePromos(user.getUserOrganization().getUserOrgId()); 
					
					if((custSID != null && !custSID.trim().isEmpty()) || 
							(email != null && !email.isEmpty())||
							(phone != null && !phone.isEmpty())){
						multipleRandom = couponsDao.getAllMultiplePromos(user.getUserOrganization().getUserOrgId());
						String multipleCodes ="";
						if(multipleRandom != null && !multipleRandom.isEmpty()){
							for (Coupons coupons : multipleRandom) {
								if(!multipleCodes.isEmpty()) multipleCodes += ",";
								multipleCodes += coupons.getCouponId().longValue();
							}
							List<CouponCodes> multipleRandomCodes = couponCodesDao.getAllMultipleCodesIssuedIfAny(user.getUserOrganization().getUserOrgId(),
																		multipleCodes,custSID, email, phone);
							if(multipleRandomCodes != null && !multipleRandomCodes.isEmpty()){
								
								multipleRandom = new ArrayList<Coupons>();
								for (CouponCodes codes : multipleRandomCodes) {
									
									Coupons coupon = codes.getCouponId();
									if(!coupon.getStatus().equals("Running")) continue;
									
									
									statusInfo = validateDynamicCoupon(coupon, codes);
									if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
										continue;
									}
									
									statusInfo = validateCoupon(coupon,user);
									if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
										logger.debug("validity failed =="+codes);
										continue;
									}
									
									statusInfo = validateStoreNum(couponCodeInfoObj,coupon);
									if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
										logger.debug("store check failed =="+codes);
										continue;
									}
									
									coupon.setCouponCode(codes.getCouponCode());
									coupon.setCouponCodeObj(codes);
									multipleRandom.add(coupon);
								}
								
							}else{
								multipleRandom=null;
							}
						}
						
					
					}
					
					
					String cardNumber = couponCodeInfo.getCARDNUMBER();
					ContactsLoyalty contactsLoyaltyObj = null;
					//if(cardNumber!=null && !cardNumber.trim().isEmpty() ) {
					
					if((cardNumber != null && !cardNumber.isEmpty()) || (custSID != null && !custSID.isEmpty())){
						contactsLoyaltyObj = contactsLoyaltyDao.getContactsLoyaltyByCustId(cardNumber,custSID,user.getUserId());
						
					}else if((email != null && !email.isEmpty()) || (phone != null && !phone.isEmpty()) ){
						
						String phoneParse = phone != null && !phone.isEmpty() ? Utility.phoneParse(phone.trim(),
								user != null ? user.getUserOrganization() : null) : null;
						
						contactsLoyaltyObj = contactsLoyaltyDao.findBy(user, email, phoneParse);
					}
						//contactsLoyaltyObj = contactsLoyaltyDao.getContactsLoyaltyByCustId(cardNumber,custSID,user.getUserId());
					if(contactsLoyaltyObj != null) {
						
						loyaltyPromos = couponsDao.getAllLoyaltyPromos(user.getUserOrganization().getUserOrgId());
					}
						
					//}

					if((loyaltyPromos ==null || loyaltyPromos.isEmpty()) &&
							(singlePromos == null || singlePromos.isEmpty()) && 
							(multipleRandom == null || multipleRandom.isEmpty())) { // needed a new error code
						if(requestingFromNewPlugin && contactsLoyaltyObj == null){
							statusInfo = new StatusInfo("100022",PropertyUtil.getErrorMessage(100022,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);
							return coupnCodeEnqResponse;
						}
						statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
							if(requestingFromNewPlugin && contactsLoyaltyObj != null){
								statusInfo = new StatusInfo("0","Promo-code enquiry is successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
								loyaltyInfo = prepareLoyaltyInfo(contactsLoyaltyObj, couponCodeEnquObj, user, true);
							}
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo, loyaltyInfo);	
							return coupnCodeEnqResponse;
						}
						
					}
					CouponCodeProcessHelper helper = new CouponCodeProcessHelper();
						
						if(loyaltyPromos != null && !loyaltyPromos.isEmpty()) {
							String rewardValueCodes = Constants.STRING_NILL;
							Map<String, LoyaltyBalance> balancesMap = new HashMap<String, LoyaltyBalance>();
							Map<Long, LoyaltyMemberItemQtyCounter> memberQtyMap = new HashMap<Long, LoyaltyMemberItemQtyCounter>();
							String rewardIds = Constants.STRING_NILL;
							
							List<Coupons> filteredList = new ArrayList<Coupons>();
							//check for the redemption limit 
							for (Coupons coupObj : loyaltyPromos) {
								
								
								statusInfo = validateCoupon(coupObj,user);
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("validity failed =="+coupObj.getCouponCode());
									continue;
								}
								
								statusInfo = validateStoreNum(couponCodeInfoObj,coupObj);
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("store check failed =="+coupObj.getCouponCode());
									continue;
								}
								
								statusInfo = validatePromoListSize(coupObj, custSID, email, phone, coupObj.getCouponCode(), 
										user.getUserOrganization().getUserOrgId(), cardNumber);
								
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("redemption per sub limit exceed for this coupon =="+coupObj.getCouponCode());
									continue;
								}
								//check if any persublimit exceeds && check for the redemption limit 
								statusInfo = helper.checkSinglePromocode(couponCodeInfoObj,coupObj,user);
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("redemption limit exceed for this coupon =="+coupObj.getCouponCode());
									continue;
								}
								
								filteredList.add(coupObj);
							}
							
							for (Coupons rewards : filteredList) {
								
								//if(isStorePromoExists && !coupons.isAccumulateOtherPromotion()) continue;
								if(rewards.getValueCode() != null && 
										!rewards.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
									if(!rewardValueCodes.isEmpty()) rewardValueCodes += ",";
									rewardValueCodes += "'"+rewards.getValueCode()+"'";
									
									if(rewards.getSpecialRewadId() != null){
										if(!rewardIds.isEmpty()) rewardIds += ",";
										rewardIds += rewards.getSpecialRewadId().longValue();
									}
								}
							}
							if(!rewardIds.isEmpty()) {
								List<LoyaltyMemberItemQtyCounter> retList = null;
								retList= loyaltyMemberItemQtyCounterDao.findItemsCounter(rewardIds, contactsLoyaltyObj.getLoyaltyId());
								if(retList != null && !retList.isEmpty()) {
									for (LoyaltyMemberItemQtyCounter loyaltyMemberItemQtyCounter : retList) {
										
										memberQtyMap.put(loyaltyMemberItemQtyCounter.getSPRuleID(), loyaltyMemberItemQtyCounter);
									}
								}
							}
							if(!rewardValueCodes.isEmpty()){
								List<LoyaltyBalance> balances = loyaltyBalanceDao.findAllBy(user.getUserId(), contactsLoyaltyObj.getProgramId(),
										contactsLoyaltyObj.getLoyaltyId(),  rewardValueCodes);
								if(balances != null && !balances.isEmpty()){
									for (LoyaltyBalance loyaltyBalance : balances) {
										
										balancesMap.put(loyaltyBalance.getValueCode(), loyaltyBalance);
									}
								}
							}
							for (Coupons coupons : filteredList) {
								
								//if(isStorePromoExists && !coupons.isAccumulateOtherPromotion()) continue;
								boolean iselegeble = false;
								if(coupons.getValueCode() != null && 
										!coupons.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
									//logger.debug("noReceiptData==="+noReceiptData+
										//	" coupons.getSpecialRewadId() ==="+coupons.getSpecialRewadId()+" memberQtyMap.containsKey(coupons.getSpecialRewadId())=="+memberQtyMap.containsKey(coupons.getSpecialRewadId())+ONESPACE+memberQtyMap.get(coupons.getSpecialRewadId()));
									if(noReceiptData && coupons.getSpecialRewadId() != null && !memberQtyMap.containsKey(coupons.getSpecialRewadId())){
										//logger.debug("continue===no need to check");
										continue;
									}
									LoyaltyBalance balance = balancesMap.get(coupons.getValueCode());//loyaltyBalanceDao.findBy(user.getUserId(), contactsLoyaltyObj.getProgramId(),			contactsLoyaltyObj.getLoyaltyId(),  coupons.getValueCode());
									if((balance != null && coupons.getRequiredLoyltyPoits()!=null && balance.getBalance()>=coupons.getRequiredLoyltyPoits()) || (coupons.getRequiredLoyltyPoits()==null && coupons.getMultiplierValue()!=null)) iselegeble = true;
									coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
											coupons,false,coupons.getCouponCode(), user, null, requestingFromNewPlugin, posMappingMap, 
											true, iselegeble, balance, contactsLoyaltyObj.getLoyaltyId(), memberQtyMap,null, contactsLoyaltyObj);
									//balanceList.add(balance);
									//finalList.add(coupons);
								}
								
							}
							for (Coupons coupons : filteredList) {
								if(coupons.getValueCode() == null || coupons.getValueCode().equals(OCConstants.LOYALTY_POINTS)){
									
									if(contactsLoyaltyObj.getLoyaltyBalance() == null || (contactsLoyaltyObj.getLoyaltyBalance() != null && coupons.getRequiredLoyltyPoits()!=null && contactsLoyaltyObj.getLoyaltyBalance()< coupons.getRequiredLoyltyPoits())) continue;
									if(!noReceiptData)coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
											coupons,false,coupons.getCouponCode(), user, null, requestingFromNewPlugin, posMappingMap, 
											false, false, null, contactsLoyaltyObj.getLoyaltyId(), null, contactsLoyaltyObj.getLoyaltyBalance(), contactsLoyaltyObj);
								}
								
								
							}
							
						}

						if(!noReceiptData && multipleRandom != null && !multipleRandom.isEmpty()) {
							
							for (Coupons coupObj : multipleRandom) {
								logger.debug("coupObj n status ==="+coupObj.getCouponName()+ONESPACE+coupObj.getStatus());
								//if(isStorePromoExists && !coupObj.isAccumulateOtherPromotion()) continue;
								coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
										coupObj,false,coupObj.getCouponCode(), user, coupObj.getCouponCodeObj(), 
										requestingFromNewPlugin, posMappingMap, false, false, null, null, null, null, contactsLoyaltyObj);
							}
						}
						if(!noReceiptData && singlePromos != null && !singlePromos.isEmpty()) {
							List<Coupons> filteredList = new ArrayList<Coupons>();
							for (Coupons coupObj : singlePromos) {
								
								//check for the redemption limit 
									
									
								statusInfo = validateCoupon(coupObj,user);
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("validity failed =="+coupObj.getCouponCode());
									continue;
								}
								
								statusInfo = validateStoreNum(couponCodeInfoObj,coupObj);
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("store check failed =="+coupObj.getCouponCode());
									continue;
								}
								
								statusInfo = validatePromoListSize(coupObj, custSID, email, phone, coupObj.getCouponCode(),
										user.getUserOrganization().getUserOrgId(), cardNumber);
								
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("redemption per sub limit exceed for this coupon =="+coupObj.getCouponCode());
									continue;
								}
								
								//check if any persublimit exceeds && check for the redemption limit 
								statusInfo = helper.checkSinglePromocode(couponCodeInfoObj,coupObj,user);
								if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
									logger.debug("redemption limit exceed for this coupon =="+coupObj.getCouponCode());
									continue;
								}
								filteredList.add(coupObj);	
								//if(isStorePromoExists && !coupObj.isAccumulateOtherPromotion()) continue;
							}
							if(!filteredList.isEmpty()) {
								for (Coupons coupObj : filteredList) {
								//if(isStorePromoExists && !coupObj.isAccumulateOtherPromotion()) continue;
								logger.debug("coupObj n status ==="+coupObj.getCouponName()+ONESPACE+coupObj.getStatus());
								coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
										coupObj,false,coupObj.getCouponCode(), user, null, requestingFromNewPlugin,
										posMappingMap, false, false, null, null, null, null, contactsLoyaltyObj);
							}
							
						}
						}
						
						boolean isDiscountNotExist = false;			
						//logger.info("coupDiscInfoList is  :: "+coupDiscInfoList.size());

						if((coupDiscInfoList == null || coupDiscInfoList.size() == 0)) {
							isDiscountNotExist = true;

						}/*else {
							CouponDiscountInfo coupoDisInfoObj = coupDiscInfoList.get(0);
							if(coupoDisInfoObj != null ) {
								List<DiscountInfo> discountList = coupoDisInfoObj.getDISCOUNTINFO();
								if(discountList == null || discountList.size() == 0) {
									isDiscountNotExist = true;
								}
							}
						}*/
						

						if(isDiscountNotExist) {
							if(requestingFromNewPlugin && contactsLoyaltyObj == null){
								statusInfo = new StatusInfo("100022",PropertyUtil.getErrorMessage(100022,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
								coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);
								return coupnCodeEnqResponse;
							}
							statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
								if(requestingFromNewPlugin && contactsLoyaltyObj != null){
									statusInfo = new StatusInfo("0","Promo-code enquiry is successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
									//coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo, loyaltyInfo);
									loyaltyInfo = prepareLoyaltyInfo(contactsLoyaltyObj, couponCodeEnquObj, user, true);
								}
								coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo, loyaltyInfo);	
								return coupnCodeEnqResponse;
							}
						}
						if(requestingFromNewPlugin) {
							List<CouponDiscountInfo> finalDiscounts = new ArrayList<CouponDiscountInfo>();
							List<CouponDiscountInfo> receiptTypeCoupons = new ArrayList<CouponDiscountInfo>();
							List<CouponDiscountInfo> appliedReceiptLevelSet = new ArrayList<CouponDiscountInfo>();
							List<CouponDiscountInfo> appliedProductLevelSet = new ArrayList<CouponDiscountInfo>();
							List<CouponDiscountInfo> receiptTypeLtyCoupons = new ArrayList<CouponDiscountInfo>();
							
							List<CouponDiscountInfo> ItemBasedCoupons = new ArrayList<CouponDiscountInfo>();
							for (CouponDiscountInfo couponDiscountInfo : coupDiscInfoList) {
								logger.debug("couponcode is ==="+couponDiscountInfo.getCOUPONCODE());
								if(couponDiscountInfo.getCOUPONTYPE().equals("REWARDS") || 
										couponDiscountInfo.getCOUPONTYPE().equals("LOYALTY")){
									if(	(user.isShowOnlyHighestLtyDC() && couponDiscountInfo.getCOUPONTYPE().equals("LOYALTY"))){
											if(couponDiscountInfo.getDISCOUNTCRITERIA().equals("PERCENTAGE-MVP") || 
													couponDiscountInfo.getDISCOUNTCRITERIA().equals("VALUE-MVP")){
												receiptTypeLtyCoupons.add(couponDiscountInfo);
											}
										}else{
											
											finalDiscounts.add(couponDiscountInfo);
										}
									}/*else if(couponDiscountInfo.getCOUPONTYPE().equals("COUPONS") && couponDiscountInfo.getNUDGEPROMOCODE().equals("YES")){
									finalDiscounts.add(couponDiscountInfo);
								}*/
								
							}
							List<String>  appliedPromo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getAPPLIEDCOUPONS();
							Set<String> appliedSet = null;
							if( appliedPromo!= null && !appliedPromo.isEmpty()){
								appliedSet = new HashSet<String>();
								appliedSet.addAll(appliedPromo);
							}
							for (CouponDiscountInfo couponDiscountInfo : coupDiscInfoList) {
								if(couponDiscountInfo.getCOUPONTYPE().equals("PROMOTIONS") || 
										couponDiscountInfo.getCOUPONTYPE().equals("COUPONS")){//only regular promos give relevant
									
									if(couponDiscountInfo.getNUDGEPROMOCODE().equals("YES")) continue;
									if(couponDiscountInfo.getDISCOUNTCRITERIA().equals("PERCENTAGE-MVP") || 
											couponDiscountInfo.getDISCOUNTCRITERIA().equals("VALUE-MVP")){
										if(appliedSet != null && !appliedSet.isEmpty() && appliedSet.contains(couponDiscountInfo.getCOUPONCODE())){
											appliedReceiptLevelSet.add(couponDiscountInfo);
											continue;
										}
										receiptTypeCoupons.add(couponDiscountInfo);
									}else if(couponDiscountInfo.getDISCOUNTCRITERIA().equals("PERCENTAGE-IC") || 
											couponDiscountInfo.getDISCOUNTCRITERIA().equals("VALUE-I")  ){
										if(appliedSet != null && !appliedSet.isEmpty() && appliedSet.contains(couponDiscountInfo.getCOUPONCODE())){
											appliedProductLevelSet.add(couponDiscountInfo);
											continue;
										}
										ItemBasedCoupons.add(couponDiscountInfo);
									}
									
								}
								
								
							}
							if(receiptTypeLtyCoupons.size() > 0){//to show only hghest loyalty DC
								Double discount = 0.0;
								CouponDiscountInfo  finalReceiptCoupon = null;
								for (CouponDiscountInfo  receiptCoupon: receiptTypeLtyCoupons) {
									if(receiptCoupon.getNUDGEPROMOCODE().equals("YES")){
										finalDiscounts.add(receiptCoupon);
										continue;
									}
									DiscountInfo oneDiscInfo = receiptCoupon.getDISCOUNTINFO().get(0);
									Double disc = Double.parseDouble(oneDiscInfo.getMAXRECEIPTDISCOUNT());
									if(discount == 0 || discount<disc) {
										discount = disc;
										finalReceiptCoupon = receiptCoupon;
									}
								}
								if(finalReceiptCoupon != null) {
									//CouponDescriptionAlgorithm couponDesc = new CouponDescriptionAlgorithm();
									//finalReceiptCoupon = couponDesc.preparecouponDisc(finalReceiptCoupon, user);
									finalDiscounts.add(finalReceiptCoupon);
								}
							}
							if(receiptTypeCoupons.size() > 0){
								Double discount = 0.0;
								CouponDiscountInfo  finalReceiptCoupon = null;
								if( user.isShowOnlyHighestDiscReceiptDC()){
									for (CouponDiscountInfo  receiptCoupon: receiptTypeCoupons) {
										/*if(appliedSet != null && !appliedSet.isEmpty() && appliedSet.contains(receiptCoupon.getCOUPONCODE())){
											finalDiscounts.add(receiptCoupon);
											continue;
										}*/
										if(receiptCoupon.getNUDGEPROMOCODE().equals("YES")){
											finalDiscounts.add(receiptCoupon);
											continue;
										}
										DiscountInfo oneDiscInfo = receiptCoupon.getDISCOUNTINFO().get(0);
										Double disc = Double.parseDouble(oneDiscInfo.getMAXRECEIPTDISCOUNT());
										if(discount == 0 || discount<disc) {
											discount = disc;
											finalReceiptCoupon = receiptCoupon;
										}
									}
									if(finalReceiptCoupon != null) {
										//CouponDescriptionAlgorithm couponDesc = new CouponDescriptionAlgorithm();
										//finalReceiptCoupon = couponDesc.preparecouponDisc(finalReceiptCoupon, user);
										finalDiscounts.add(finalReceiptCoupon);
									}
								}else{
									finalDiscounts.addAll(receiptTypeCoupons);
								}
							}
							if(appliedReceiptLevelSet.size() > 0) finalDiscounts.addAll(appliedReceiptLevelSet);
							//finalDiscounts.addAll(ItemBasedCoupons);
							if(ItemBasedCoupons.size() > 0) {
								//finalDiscounts.addAll(ItemBasedCoupons);
								
								List<PurchasedItems> purchaseList=couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS();
								Map<String, CouponDiscountInfo> itemFinalDiscount = new HashMap<String, CouponDiscountInfo>();
								Map<String, Double> itemDisc = new HashMap<String, Double>();
								Map<String, Set<String>> itemDiscCodes = new HashMap<String, Set<String>>();
								for (PurchasedItems purchasedItems : purchaseList) {
									
									for (CouponDiscountInfo itemBasedCoupon : ItemBasedCoupons) {
										logger.debug("promotion =="+itemBasedCoupon.getCOUPONCODE());
										List<DiscountInfo> itemDiscounts = itemBasedCoupon.getDISCOUNTINFO();
										logger.debug("itemDiscounts "+itemDiscounts.size());
										for (DiscountInfo discountInfo : itemDiscounts) {
											
											List<ItemCodeInfo> itemCodeInfo = discountInfo.getITEMCODEINFO();
											for (ItemCodeInfo itemCode : itemCodeInfo) {
												if(itemCode.getMAXITEMDISCOUNT() == null || itemCode.getMAXITEMDISCOUNT().isEmpty()){
													
													logger.debug("No max discount is given ==");
													continue;
												}
												if(purchasedItems.getITEMCODE().equalsIgnoreCase(itemCode.getITEMCODE())){
													logger.debug("item satsfied=="+purchasedItems.getITEMCODE()+ "==on Disc =="+itemCode.getMAXITEMDISCOUNT() );
													if(itemFinalDiscount.get(itemCode.getITEMCODE()) == null){
														itemDisc.put(itemCode.getITEMCODE(), Double.parseDouble(itemCode.getMAXITEMDISCOUNT()));
														itemFinalDiscount.put(itemCode.getITEMCODE(), itemBasedCoupon);
														Set<String> itemcodeSet = new HashSet<String>();
														itemcodeSet.add(itemCode.getITEMCODE());
														itemDiscCodes.put(itemBasedCoupon.getCOUPONCODE(), itemcodeSet);
													}else{
														if(itemDisc.get(itemCode.getITEMCODE()) <  Double.parseDouble(itemCode.getMAXITEMDISCOUNT()) ){
															
															itemDisc.put(itemCode.getITEMCODE(), Double.parseDouble(itemCode.getMAXITEMDISCOUNT()));
															itemFinalDiscount.put(itemCode.getITEMCODE(), itemBasedCoupon);
															Set<String> itemcodeSet = itemDiscCodes.get(itemBasedCoupon.getCOUPONCODE());
															if(itemcodeSet == null) itemcodeSet =  new HashSet<String>();
															itemcodeSet.add(itemCode.getITEMCODE());
															itemDiscCodes.put(itemBasedCoupon.getCOUPONCODE(), itemcodeSet);
														}
														
													}
													
												}
												
											}
											
										}
										
									}//for each coupon
									
								}//for each item
								
								List<CouponDiscountInfo> itemsFinalDiscount = new ArrayList<CouponDiscountInfo>();
								
								logger.debug("size of the final promos==="+itemFinalDiscount.size());
								Set<String> couponcodes = new HashSet<String>();
								for (String itemCodes : itemFinalDiscount.keySet()) {
									CouponDiscountInfo itemDicountInfo = itemFinalDiscount.get(itemCodes);
									logger.debug("item==="+itemCodes+" ==promotion=="+itemDicountInfo.getCOUPONCODE());
									if(couponcodes.contains(itemCodes)) {
										logger.debug("==yes==");
										continue;
									}
									List<DiscountInfo> discountInfoLst = new ArrayList<DiscountInfo>();
									for (DiscountInfo itemDiscInfo : itemDicountInfo.getDISCOUNTINFO()) {
										List<ItemCodeInfo> items = itemDiscInfo.getITEMCODEINFO();
										Set<String> itemcodeSet = itemDiscCodes.get(itemDicountInfo.getCOUPONCODE());
										List<ItemCodeInfo> newItemInfo = new ArrayList<ItemCodeInfo>();
										//logger.debug("size of the final promos==="+items.size());

										for (ItemCodeInfo itemCodeInfo : items) {
											//logger.debug("size of the final promos==="+itemCodeInfo.getITEMCODE()+ONESPACE+itemcodeSet);
											logger.debug("itemCodeInfo==="+itemCodeInfo.getITEMCODE()+ONESPACE+ONESPACE+
											itemcodeSet.contains(itemCodeInfo.getITEMCODE())+itemCodeInfo.getMAXITEMDISCOUNT()+ONESPACE+itemDisc.get(itemCodeInfo.getITEMCODE()));

											if(itemDisc.containsKey(itemCodeInfo.getITEMCODE()) && !couponcodes.contains(itemCodeInfo.getITEMCODE()) &&
													itemDisc.get(itemCodeInfo.getITEMCODE()).doubleValue() ==Double.parseDouble(itemCodeInfo.getMAXITEMDISCOUNT())){
												logger.debug("==exists==");
												newItemInfo.add(itemCodeInfo);
												couponcodes.add(itemCodeInfo.getITEMCODE());
											}
											
										}
										if(newItemInfo.size() > 0){
											
											itemDiscInfo.setITEMCODEINFO(newItemInfo);
											discountInfoLst.add(itemDiscInfo);
										}
										//logger.debug("size of the final promos==="+couponcodes.size());
									}
									if(discountInfoLst.size()>0){
										itemDicountInfo.setDISCOUNTINFO(discountInfoLst);
										//CouponDescriptionAlgorithm couponDescriptionAlgorithm = new CouponDescriptionAlgorithm();
										//itemDicountInfo = couponDescriptionAlgorithm.preparecouponDisc(itemDicountInfo, user, true);
										itemsFinalDiscount.add(itemDicountInfo);
									}
									//logger.debug("size of the final promos==="+itemsFinalDiscount.size());
								}
								//logger.debug("size of the final promos==="+couponcodes.size()+itemsFinalDiscount.size());
								if(itemsFinalDiscount.size() > 0) finalDiscounts.addAll(itemsFinalDiscount);
								
							}//if on product based exists
							if(appliedProductLevelSet.size() > 0) finalDiscounts.addAll(appliedProductLevelSet);
							finalDiscounts = reArrangeForNudge(finalDiscounts);
							
							coupDiscInfoList = finalDiscounts;
						}
						if(requestingFromNewPlugin && contactsLoyaltyObj != null) loyaltyInfo = prepareLoyaltyInfo(contactsLoyaltyObj, couponCodeEnquObj, user, true);
						if(contactsLoyaltyObj == null){
							statusInfo = new StatusInfo("100022",PropertyUtil.getErrorMessage(100022,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);
							return coupnCodeEnqResponse;
						}
				//}
				
				
			
			}else if(coupCodeStr.equalsIgnoreCase("ALLCODES") ){
				
				List<Coupons> listPromoCoupons = couponsDao.findCouponsByStatus(user.getUserOrganization().getUserOrgId());

				statusInfo = validatePromoList(listPromoCoupons);
				if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);	
					return coupnCodeEnqResponse;
				}
				for (Coupons coupObj : listPromoCoupons) {
					
					coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj, 
							coupObj,false,coupObj.getCouponCode(), user, null, requestingFromNewPlugin,
							posMappingMap, false, false, null, null, null, null, null, true);
					
					
				}
				
				
			}
			logger.debug("===came till here===");
			statusInfo = new StatusInfo("0","Promo-code enquiry is successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo, loyaltyInfo);
			logger.debug("-------exit  processCouponRequest---------");
			return coupnCodeEnqResponse;
		}catch(Exception e) {

			statusInfo = new StatusInfo("100901",PropertyUtil.getErrorMessage(100901,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			coupnCodeEnqResponse = finalJsonResponse(headerInfo,coupDiscInfoList,statusInfo);
			logger.error("Exception ::" , e);
			return coupnCodeEnqResponse;
			//			 throw new BaseServiceException("Exception occured while processing processCouponRequest::::: ", e);
		}
		/*finally{
			coupnCodeEnqResponse = finalJsonResponse(couponCodeEnquObj.getCOUPONCODEENQREQ().getHEADERINFO(),coupDiscInfoList,statusInfo);	
			return coupnCodeEnqResponse;
		}*/
	}//processCouponRequest
	
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
	private StatusInfo checkredeemedCoupon(ReferralcodesRedeemed redeemedrefobj, Users user) {
		
		StatusInfo statusInfo=null;
		logger.debug("-------entered validate redeemed Coupon---------");
	
			if(redeemedrefobj!= null){
			logger.debug("Error :Promo-code already redeemed ****");
			statusInfo = new StatusInfo("100014",PropertyUtil.getErrorMessage(100014,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
			}
			return statusInfo;
		
	}
	private StatusInfo checkloyaltycontact(ContactsLoyalty contactsloyaltyobj, Users user) throws ParseException {
		
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

	private StatusInfo validatePromoListSize(Coupons coupObj, String customerIdStr, String emailStr, 
			String mobileStr, String reqCoupcode, Long orgId, String membershipNumber) throws BaseServiceException {
		StatusInfo statusInfo=null;
		try {
			logger.debug("-------entered validatePromoListSize---------");
			CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			if(coupObj.getSingPromoContRedmptLimit() != null && 
					(customerIdStr != null && !customerIdStr.trim().isEmpty() ||
					emailStr != null && !emailStr.trim().isEmpty() || 
					mobileStr != null && !mobileStr.trim().isEmpty())) {
				List<CouponCodes> promoList = couponCodesDao.isPromoExistForRedeem(customerIdStr,emailStr,mobileStr,reqCoupcode,orgId, membershipNumber);
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
	
	
	private List<CouponDiscountInfo> reArrangeForNudge(List<CouponDiscountInfo> coupDiscInfoList){
		
		try {
			List<CouponDiscountInfo> discountsList = new ArrayList<CouponDiscountInfo>();
			List<CouponDiscountInfo> nudgeDiscountsList = new ArrayList<CouponDiscountInfo>();
			List<CouponDiscountInfo> appliedDiscounts = new ArrayList<CouponDiscountInfo>();
			Set<String> applied = new HashSet<String>();
			for (CouponDiscountInfo couponDiscountInfo : coupDiscInfoList) {
				if(couponDiscountInfo != null && couponDiscountInfo.getAPPLIEDCOUPONS() != null && couponDiscountInfo.getAPPLIEDCOUPONS().equals("YES")){
					applied.add(couponDiscountInfo.getCOUPONCODE());
					appliedDiscounts.add(couponDiscountInfo);
				}
				
			}
			for (CouponDiscountInfo couponDiscountInfo : coupDiscInfoList) {
				if(couponDiscountInfo.getNUDGEPROMOCODE() != null && couponDiscountInfo.getNUDGEPROMOCODE().equalsIgnoreCase("YES")){
					
					nudgeDiscountsList.add(couponDiscountInfo);
				}else{
					if(applied.contains(couponDiscountInfo.getCOUPONCODE())) continue;
					discountsList.add(couponDiscountInfo);
					
				}
				
			}
			if(!discountsList.isEmpty() || !nudgeDiscountsList.isEmpty() || !appliedDiscounts.isEmpty()){
				
				coupDiscInfoList = new ArrayList<CouponDiscountInfo>();
				if(!appliedDiscounts.isEmpty()) coupDiscInfoList.addAll(appliedDiscounts);
				if(!discountsList.isEmpty()) coupDiscInfoList.addAll(discountsList);
				if(!nudgeDiscountsList.isEmpty()) coupDiscInfoList.addAll(nudgeDiscountsList);
				
				for (CouponDiscountInfo couponDiscountInfo : coupDiscInfoList) {
					logger.debug("couponDiscountInfo === "+couponDiscountInfo.getCOUPONCODE());
				}
				return coupDiscInfoList;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return coupDiscInfoList;
		
		
	}

	private StatusInfo validateRootObj(
			CouponCodeEnquObj couponCodeEnquObj) throws BaseServiceException{
		logger.debug("-------entered validateRootObj---------");
		StatusInfo statusInfo=null;
		if(couponCodeEnquObj == null) {
			logger.debug("Error : Unable to parse the json.. Returning. ****");
			statusInfo = new StatusInfo("100002",PropertyUtil.getErrorMessage(100002,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(couponCodeEnquObj.getCOUPONCODEENQREQ() == null) {
			logger.debug("Error : Unable to parse the json.. Returning. ****");
			statusInfo = new StatusInfo("100000",PropertyUtil.getErrorMessage(100000,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateRootObj---------");
		return statusInfo;
	}//validateRootObj

	private StatusInfo validateJsonUser(CouponCodeEnquObj couponCodeEnquObj) throws BaseServiceException {
		StatusInfo statusInfo=null;
		try {
			logger.debug("-------entered validateJsonUser---------");
			CouponCodeInfo couponCodeInfoObj=couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO();
			UserDetails userDetailsObj=couponCodeEnquObj.getCOUPONCODEENQREQ().getUSERDETAILS();
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);

			if(couponCodeEnquObj.getCOUPONCODEENQREQ().getHEADERINFO()==null) {
				statusInfo = new StatusInfo("100001",PropertyUtil.getErrorMessage(100001,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
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
			if(couponCodeInfoObj==null) {
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
	}//validateJsonUser

	
	
	
	
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private Coupons getCouponObj(Users user,String coupCodeStr) throws BaseServiceException {
		Coupons coupObj = null;
		try {
			logger.debug("-------entered getCouponObj---------");
			CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			CouponsDao couponsDao=(CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			ReferralcodesIssuedDao referralCodesDao=(ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);

		//	CouponCodes  coupCodeObj = couponCodesDao.testForCouponCodes(coupCodeStr, user.getUserOrganization().getUserOrgId());
			
			ReferralcodesIssued	refCodeObj = referralCodesDao.testForrefCodes(coupCodeStr, user.getUserOrganization().getUserOrgId());

			logger.info("refCodeObj value is"+refCodeObj);

			
			if(refCodeObj == null) {
				
				logger.info("refCodeObj value in if"+refCodeObj);

				coupObj = couponsDao.findCoupon(coupCodeStr,user.getUserOrganization().getUserOrgId());

				logger.info("coupObj value in if is"+coupObj);

				
			}else{
				
				logger.info("refCodeObj value in else"+refCodeObj);

				coupObj  = refCodeObj.getCouponId();
				if(coupObj.getCouponCode() == null) coupObj.setCouponCode(refCodeObj.getRefcode());
			}
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getCouponObj::::: ", e);
		}
		logger.debug("-------exit  getCouponObj---------");
		
		logger.info("coupObj value is"+coupObj);
		return coupObj;
	}//getCouponObj

	private StatusInfo validateCoupon(Coupons coupObj, Users user) throws BaseServiceException{
		StatusInfo statusInfo=null;
		logger.debug("-------entered validateCoupon---------");
		if(coupObj == null){
			logger.debug("Error :Promo-code not exists in DB ****");
			statusInfo = new StatusInfo("100012",PropertyUtil.getErrorMessage(100012,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}

		String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
		int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
		String timezoneDiffrenceMinutes = user.getClientTimeZone();
		int timezoneDiffrenceMinutesInt = 0;

		if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
		timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;

		Calendar cal = Calendar.getInstance();

		if(coupObj.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_STATIC)){
			if(cal.after(coupObj.getCouponExpiryDate())) {
				statusInfo = new StatusInfo("100015",PropertyUtil.getErrorMessage(100015,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}

			if(coupObj.getStatus().equals(Constants.COUP_STATUS_ACTIVE) ) {
				statusInfo = new StatusInfo("100931",PropertyUtil.getErrorMessage(100931,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
		}

		if(!coupObj.getStatus().equals(Constants.COUP_STATUS_RUNNING) ){

			logger.debug("Error : Promo-code Object cannot be redeemed ****");
			String errorMsg = PropertyUtil.getErrorMessage(100919,OCConstants.ERROR_PROMO_FLAG);
			logger.debug(" :: errorMsg ::  is "+errorMsg);
			errorMsg += ONESPACE+coupObj.getStatus()+")";
			statusInfo = new StatusInfo("100919",errorMsg,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateCoupon---------");
		return statusInfo;
	}//validateCoupon

	private StatusInfo validateDynamicCoupon(Coupons coupObj,CouponCodes coupCodeObj){
		StatusInfo statusInfo=null;
		logger.debug("-------entered validateDynamicCoupon---------");
		if(coupCodeObj != null) {
			if(coupObj == null){
			logger.debug("Error :Promo-code not exists in DB ****");
			statusInfo = new StatusInfo("100012",PropertyUtil.getErrorMessage(100012,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
			}
			if(coupObj.getExpiryType().equals(Constants.COUP_VALIDITY_PERIOD_DYNAMIC)){
				if(coupCodeObj != null && coupCodeObj.getStatus().equals(Constants.COUP_STATUS_EXPIRED)){
				statusInfo = new StatusInfo("100015",PropertyUtil.getErrorMessage(100015,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
				}
			}
	}
		return statusInfo;
	}

	private StatusInfo validateStoreNum(CouponCodeInfo couponCodeInfoObj,Coupons coupObj) throws BaseServiceException{
		StatusInfo statusInfo=null;
		String strorNumberStr = Constants.STRING_NILL;
		String sBSNumberStr = Constants.STRING_NILL;
		logger.debug("-------entered validateStoreNum---------");
		if((coupObj.getAllStoreChk() != null && coupObj.getAllStoreChk() == false) ||
				(coupObj.getSelectedStores() != null && coupObj.getSelectedStores().trim().length() > 0)) {
			if(couponCodeInfoObj.getSTORENUMBER()!=null) {
				strorNumberStr = couponCodeInfoObj.getSTORENUMBER();
				sBSNumberStr = couponCodeInfoObj.getSUBSIDIARYNUMBER();
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
		logger.debug("-------exit  validateStoreNum---------");
		return statusInfo;
	}//validateStoreNum()

	/*private List<PurchasedItems> checkIfSkuType(CouponCodeEnquObj couponCodeEnquObj,Coupons coupObj) throws BaseServiceException{
		List<PurchasedItems>  purchaseList= null;
		purchaseList = couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS();
		return purchaseList;
	}//checkIfSkuType
	 */	
	private StatusInfo validatePurItemList(List<PurchasedItems> purchaseList) throws BaseServiceException{
		StatusInfo statusInfo=null;
		logger.debug("-------entered validatePurItemList---------");
		if(purchaseList == null) {
			logger.debug("Error : unable to find the Promo-code purchase Info in JSON ****");
			statusInfo = new StatusInfo("100009",PropertyUtil.getErrorMessage(100009,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo ;
		}
		if(purchaseList.size() == 0) {
			logger.debug("Error : No Item Code Info from JSON ****");
			statusInfo = new StatusInfo("100010",PropertyUtil.getErrorMessage(100010,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo ;
		}
		logger.debug("-------exit  validatePurItemList---------");
		return statusInfo;
	}//validatePurItemList

	private CoupnCodeEnqResponse finalJsonResponse(HeaderInfo headerInfo,List<CouponDiscountInfo> coupDiscInfoList,StatusInfo statusInfo) throws BaseServiceException{
		logger.debug("-------entered finalJsonResponse---------");
		CoupnCodeEnqResponse  couponcodeEnqResponse=new CoupnCodeEnqResponse();
		CouponCodeResponse couponCodeResponse=new CouponCodeResponse(coupDiscInfoList,statusInfo,headerInfo);
		
		couponcodeEnqResponse.setCOUPONCODERESPONSE(couponCodeResponse);
		/*couponcodeEnqResponse.setHEADERINFO(headerInfo);
		couponcodeEnqResponse.setSTATUSINFO(statusInfo);*/
		logger.debug("-------exit  finalJsonResponse---------");
		return couponcodeEnqResponse;
	}//finalJsonResponse

	private CoupnCodeEnqResponse finalJsonResponse(HeaderInfo headerInfo,List<CouponDiscountInfo> coupDiscInfoList,StatusInfo statusInfo, LoyaltyInfo loyaltyInfo) throws BaseServiceException{
		logger.debug("-------entered finalJsonResponse---------");
		CoupnCodeEnqResponse  couponcodeEnqResponse=new CoupnCodeEnqResponse();
		CouponCodeResponse couponCodeResponse=new CouponCodeResponse(coupDiscInfoList,statusInfo,headerInfo);
		if(loyaltyInfo != null){
			
			couponCodeResponse.setLOYALTYINFO(loyaltyInfo);
		}
		couponcodeEnqResponse.setCOUPONCODERESPONSE(couponCodeResponse);
		/*couponcodeEnqResponse.setHEADERINFO(headerInfo);
		couponcodeEnqResponse.setSTATUSINFO(statusInfo);*/
		logger.debug("-------exit  finalJsonResponse---------");
		return couponcodeEnqResponse;
	}//finalJsonResponse

	private LoyaltyInfo prepareLoyaltyInfo(ContactsLoyalty contactsLoyalty,  CouponCodeEnquObj couponCodeEnquObj, Users user, boolean includeFBP) {
		
		LoyaltyInfo loyaltyInfo = new LoyaltyInfo();
		try {
			Contacts contact = contactsLoyalty.getContact();
			String Email =  contact.getEmailId() != null ? contact.getEmailId() : "";
			String phone = contact.getMobilePhone() != null ? contact.getMobilePhone() :"";
			String name = contact.getFirstName() != null ? contact.getFirstName() : "";
			if(contact.getLastName() != null && !name.isEmpty()) name += ONESPACE+contact.getLastName();
			loyaltyInfo.setCUSTOMERNAME(name);
			loyaltyInfo.setCUSTOMERID(contactsLoyalty.getCustomerId());
			loyaltyInfo.setCARDNUMBER(contactsLoyalty.getCardNumber());
			loyaltyInfo.setCARDPIN(Constants.STRING_NILL);
			loyaltyInfo.setEMAIL(Email);
			loyaltyInfo.setPHONE(phone);
			loyaltyInfo.setALLOWREDEMPTION(user.isAllowBothDiscounts() ? "TRUE" :"");
			//set OTP details
			LoyaltyProgram loyaltyProgram = LoyaltyProgramHelper.findLoyaltyProgramByProgramId(contactsLoyalty.getProgramId(), contactsLoyalty.getUserId());
			if(loyaltyProgram == null || !OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equals(loyaltyProgram.getStatus())){
				return loyaltyInfo;
			}
			//APP-3666
			/*if(loyaltyProgram.getRedemptionOTPFlag() == OCConstants.FLAG_YES){
				loyaltyInfo.setOTPENABLED("True");
			}
			else{
				loyaltyInfo.setOTPENABLED("False");
			}
			if(loyaltyProgram.getRedemptionOTPFlag() == OCConstants.FLAG_YES && loyaltyProgram.getOtpLimitAmt()!=null){
				OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
				otpRedeemLimit.setAMOUNT(Constants.STRING_NILL+loyaltyProgram.getOtpLimitAmt());
				otpRedeemLimit.setVALUECODE(OCConstants.LOYALTY_TYPE_CURRENCY);
				List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
				otpRedeemLimitlist.add(otpRedeemLimit);
				loyaltyInfo.setOTPREDEEMLIMIT(otpRedeemLimitlist);
			}
			else{
				List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
				loyaltyInfo.setOTPREDEEMLIMIT(otpRedeemLimitlist);
			}*/
			
			//check the expirations exists..
			//calculate total redeemable currency
			if(contactsLoyalty.getProgramTierId() != null && loyaltyProgram.getRewardType() != null && 
					loyaltyProgram.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_LOYALTY)) {
				LoyaltyProgramTier tier = LoyaltyProgramHelper.getLoyaltyTier(contactsLoyalty.getProgramTierId());
				if(tier== null){
					return loyaltyInfo;
				}
				
				//set OTP details APP-3666
				if(tier.getRedemptionOTPFlag() == OCConstants.FLAG_YES){
					loyaltyInfo.setOTPENABLED("True");
				}
				else{
					loyaltyInfo.setOTPENABLED("False");
				}
				if(tier.getRedemptionOTPFlag() == OCConstants.FLAG_YES && tier.getOtpLimitAmt()!=null){
					OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
					otpRedeemLimit.setAMOUNT(Constants.STRING_NILL+tier.getOtpLimitAmt());
					otpRedeemLimit.setVALUECODE(OCConstants.LOYALTY_TYPE_CURRENCY);
					List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
					otpRedeemLimitlist.add(otpRedeemLimit);
					loyaltyInfo.setOTPREDEEMLIMIT(otpRedeemLimitlist);
				}
				else{
					List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
					loyaltyInfo.setOTPREDEEMLIMIT(otpRedeemLimitlist);
				}
				
				if(user.isSendExpiryInfo()){
					List<String> expiryInfo = new ArrayList<String>();
					if(user.getSelectedExpiryInfoType() != null && 
							user.getSelectedExpiryInfoType().equals(OCConstants.SELECTED_EXPIRY_INFO_TYPE_AMOUNT) )	{
						List<LoyaltyThresholdBonus> expireThreshBonusList = isBonusOnAmountSetOnexpiry(contactsLoyalty.getProgramId(), true);
						
						String earntype = tier.getEarnType();
						if( (((tier.getRewardExpiryDateType()) != null && tier.getRewardExpiryDateValue() != null ) && ((OCConstants.LOYALTY_TYPE_AMOUNT.equals(earntype) )|| 
								(earntype.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)&& 
										tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)
										&& tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0))) ||
								(expireThreshBonusList!= null && !expireThreshBonusList.isEmpty()) ){
							
							if(expireThreshBonusList != null && !expireThreshBonusList.isEmpty()){
								
								List<String> bonusExpiryInfo = getAmountExpiryList(user, 
										contactsLoyalty.getLoyaltyId(), expireThreshBonusList); 
								if(!bonusExpiryInfo.isEmpty()){
									expiryInfo.addAll(bonusExpiryInfo);
								}
							}
						}
						
						loyaltyInfo.setEXPIRYINFO(expiryInfo);
						
					}
				}
				//if(loyaltyProgram.getRewardExpiryFlag()== OCConstants.FLAG_YES || tier. )
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				String earn=ltyPrgmSevice.getEarn(tier);
				
				//give earn rule
				String userCurrencySymbol = "$";
                if(!earn.isEmpty() && earn.contains("$")){
					
					String currSymbol = Utility.countryCurrencyMap.get(user.getCountryType());
					if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
					    
					   earn = earn.contains("$")? earn.replace("$", userCurrencySymbol):earn; 
				}
                loyaltyInfo.setISSUANCEDESC(earn);
                
                //give burn rule
                String conversionRule=ltyPrgmSevice.getRule(tier);
				
				 if(!conversionRule.isEmpty() && conversionRule.contains("$")){
						
						String currSymbol = Utility.countryCurrencyMap.get(user.getCountryType());
						if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
						    
						conversionRule = conversionRule.contains("$")? conversionRule.replace("$", userCurrencySymbol):conversionRule; 
					}
				 loyaltyInfo.setREDEEMDESC(conversionRule);
				
				double loyaltyAmount = contactsLoyalty.getGiftcardBalance() == null ? 0.0 : contactsLoyalty.getGiftcardBalance();
				double giftAmount = contactsLoyalty.getGiftBalance() == null ? 0.0 : contactsLoyalty.getGiftBalance();
				double pointsAmount = 0.0;
				double totalReedmCurr = 0.0;
				String excludeRedemptionNote = "";
				
				pointsAmount = LoyaltyProgramHelper.calculatePointsAmount(contactsLoyalty, tier);
				
				totalReedmCurr = loyaltyAmount + pointsAmount + giftAmount;
				Double totalRedeemableAmount = 	loyaltyAmount + pointsAmount + giftAmount;	
				String givenRecptAmount = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT();	
				if(givenRecptAmount != null && !givenRecptAmount.trim().isEmpty() && Double.parseDouble(givenRecptAmount.trim())>0)	{

					if(tier.getRedemptionPercentageLimit() != null && tier.getRedemptionPercentageLimit() > 0 ){
						totalReedmCurr = (tier.getRedemptionPercentageLimit()/100) * Double.parseDouble(givenRecptAmount);
					}
					//APP-1967
					if(tier.getRedemptionValueLimit() != null && tier.getRedemptionValueLimit() > 0
							&& totalReedmCurr > tier.getRedemptionValueLimit()){
						totalReedmCurr = tier.getRedemptionValueLimit();
					}
					if(totalRedeemableAmount >= 0 && totalReedmCurr > totalRedeemableAmount){
						totalReedmCurr = totalRedeemableAmount;
					}
				}

				LoyaltyProgramExclusionDao loyaltyProgramExclusionDao=(LoyaltyProgramExclusionDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO);
				LoyaltyProgramExclusion loyaltyExclusion =loyaltyProgramExclusionDao.getExlusionByProgId(contactsLoyalty.getProgramId());
				
				// redeemable balance = 0.00 for excluded stores
				if(loyaltyExclusion != null && loyaltyExclusion.getStrRedempChk()!=null && loyaltyExclusion.getStrRedempChk()) {
					if(loyaltyExclusion.getSelectedStoreStr() != null && !loyaltyExclusion.getSelectedStoreStr().trim().isEmpty()){
						String[] storeNumberArr = loyaltyExclusion.getSelectedStoreStr().split(";=;");
						for(String storeNo : storeNumberArr){
							if(couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO()
									.getSTORENUMBER().trim().equals(storeNo.trim())){
								totalReedmCurr = 0;
								excludeRedemptionNote = "Redemption is not allowed for selected store";
								loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
							}
						}
					} 
				}
				if(loyaltyExclusion != null &&  loyaltyExclusion.getAllStrChk()!=null && loyaltyExclusion.getAllStrChk()) {
					totalReedmCurr = 0;
					excludeRedemptionNote = "Redemption is not allowed for all stores";
					loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
				}
				
				// redeemable balance = 0.00 for excluded days
				if(loyaltyExclusion != null &&  loyaltyExclusion.getExclRedemDateStr() != null){
					
					Date date = Calendar.getInstance().getTime();  
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
					Calendar cal1 = Calendar.getInstance();
					cal1.setTime(dateFormat.parse(dateFormat.format(date)));
					
					String[] dateArr = null;
					dateArr = loyaltyExclusion.getExclRedemDateStr().split(";=;");
					
						for(String dateStr : dateArr){
							if(!dateStr.contains(",")) {
								if(dateStr.length()==3) {
									SimpleDateFormat sdf = new SimpleDateFormat("MMM");
									Calendar cal = Calendar.getInstance();
									cal.setTime(sdf.parse(dateStr));
								
									if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH))){
										totalReedmCurr = 0;
										excludeRedemptionNote = "Redemption is not allowed in this month";
										loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
									}
								} else if(dateStr.length()==4) {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
									Calendar cal = Calendar.getInstance();
									cal.setTime(sdf.parse(dateStr));
								
									if(	(cal.get(Calendar.YEAR) == cal1.get(Calendar.YEAR))){
										totalReedmCurr = 0;
										excludeRedemptionNote = "Redemption is not allowed in this year";
										loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
									}
								} else if(dateStr.contains("-")) {
									SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
									Calendar cal = Calendar.getInstance();
									cal.setTime(sdf.parse(dateStr));
								
									if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH)) &&
										(cal.get(Calendar.YEAR) == cal1.get(Calendar.YEAR))){
										totalReedmCurr = 0;
										excludeRedemptionNote = "Redemption is not allowed in this month";
										loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
									}
								} else {
									SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
									Calendar cal = Calendar.getInstance();
									cal.setTime(sdf.parse(dateStr));
								
									if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH)) &&
										  (cal.get(Calendar.DAY_OF_MONTH) == cal1.get(Calendar.DAY_OF_MONTH))){
										totalReedmCurr = 0;
										excludeRedemptionNote = "Redemption is not allowed today";
										loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
									}
								}
							} else {
								SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
								Calendar cal = Calendar.getInstance();
								cal.setTime(sdf.parse(dateStr));
							
								if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH)) &&
									(cal.get(Calendar.DAY_OF_MONTH) == cal1.get(Calendar.DAY_OF_MONTH)) && (cal.get(Calendar.YEAR) == cal1.get(Calendar.YEAR))){
									totalReedmCurr = 0;
									excludeRedemptionNote = "Redemption is not allowed today";
									loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
								}
							}
							
						}
				}
				//APP-3666
				Double minReceiptAmt = tier.getMinReceiptValue();
				Double minBalanceValue = tier.getMinBalanceValue();
				Double pointsBalance = contactsLoyalty.getLoyaltyBalance();
				String receiptAmtStr = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT();
				Double receiptAmt = 0.0;
				receiptAmt = receiptAmtStr != null && !receiptAmtStr.isEmpty() ? 
						Double.parseDouble(receiptAmtStr) : receiptAmt;
				if(minReceiptAmt!=null && receiptAmt!=null && minReceiptAmt>0 && receiptAmt>0) {
					if(receiptAmt<minReceiptAmt) {
						totalReedmCurr = 0;
					}
				}
				if(tier.getEarnType()!=null && !tier.getEarnType().isEmpty()) {
					if(tier.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS) && pointsBalance!=null && minBalanceValue!=null) {
						if(pointsBalance<minBalanceValue) {
							totalReedmCurr = 0;
						}
					} else if(tier.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT) && totalReedmCurr>0 && minBalanceValue!=null) {
						if(totalReedmCurr<minBalanceValue) {
							totalReedmCurr = 0;
						}
					}
				}
				//APP-3666
				/*Double minReceiptAmt = loyaltyProgram.getMinReceiptAmtValue();
				Double minBalanceValue = loyaltyProgram.getMinBalanceRedeemValue();
				Double pointsBalance = contactsLoyalty.getLoyaltyBalance();
				String receiptAmtStr = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT();
				Double receiptAmt = 0.0;
				receiptAmt = receiptAmtStr != null && !receiptAmtStr.isEmpty() ? 
						Double.parseDouble(receiptAmtStr) : receiptAmt;
				if(minReceiptAmt!=null && receiptAmt!=null && minReceiptAmt>0 && receiptAmt>0) {
					if(receiptAmt<minReceiptAmt) {
						totalReedmCurr = 0;
					}
				}
				if(loyaltyProgram.getMinBalanceType()!=null && !loyaltyProgram.getMinBalanceType().isEmpty()) {
					if(loyaltyProgram.getMinBalanceType().equalsIgnoreCase("Points") && pointsBalance!=null && minBalanceValue!=null) {
						if(pointsBalance<minBalanceValue) {
							totalReedmCurr = 0;
						}
					} else if(loyaltyProgram.getMinBalanceType().equalsIgnoreCase("Amount") && totalReedmCurr>0 && minBalanceValue!=null) {
						if(totalReedmCurr<minBalanceValue) {
							totalReedmCurr = 0;
						}
					}
				}*/

				String docSID = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getDOCSID();
				String checkReward = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCHECKREWARDS();
				if(checkReward != null && !checkReward.isEmpty()){
					
					boolean rewardExists = LoyaltyProgramHelper.checkRewardGainedAlready(contactsLoyalty, docSID);
					loyaltyInfo.setCHECKREWARDS(rewardExists ? checkReward : "");
				}
				
				String[] balArr = new String[2];
				String pOINTSEARNEDTODAY = Constants.STRING_NILL;
				String CurrEARNEDTODAY = Constants.STRING_NILL;
				String pointsRedeemed = Constants.STRING_NILL;
				if(docSID != null && !docSID.isEmpty()){
					balArr =  LoyaltyProgramHelper.pointsEarnedIncurrent(contactsLoyalty, docSID) ;
					pOINTSEARNEDTODAY = balArr[0];
					CurrEARNEDTODAY = balArr[1];
					pointsRedeemed = balArr[2];
				}
				loyaltyInfo.setREDEEMABLEAMOUNT(BigDecimal.valueOf(totalReedmCurr).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
				loyaltyInfo.setPOINTSEARNED(pOINTSEARNEDTODAY != null ? pOINTSEARNEDTODAY : "0" );
				loyaltyInfo.setCURRENCYEARNED(CurrEARNEDTODAY != null ? CurrEARNEDTODAY : "0.0" );
				loyaltyInfo.setPOINTSREDEEMED(pointsRedeemed);
				loyaltyInfo.setLIFETIMEPOINTS(contactsLoyalty.getTotalLoyaltyEarned() != null ? contactsLoyalty.getTotalLoyaltyEarned().longValue()+Constants.STRING_NILL:"");
				List<LoyaltyBalance> retBalances = null;
				if(includeFBP) retBalances = LoyaltyProgramHelper.getOtherBalances(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId());
				else{
					retBalances = LoyaltyProgramHelper.getOnlyRewards(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(), contactsLoyalty.getOrgId());
				}
				List<Balances> balances = new ArrayList<Balances>();
				
				Balances amountBalances = new Balances();
						
				
				amountBalances.setAMOUNT(contactsLoyalty.getGiftcardBalance() != null ? 
						(BigDecimal.valueOf(contactsLoyalty.getGiftcardBalance()).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL): "0.00");
				String currSymbol = Utility.countryCurrencyMap.get(user.getCountryType());
				   if(currSymbol != null && !currSymbol.isEmpty()) {
					   if(currSymbol.equals("$")) {
						   currSymbol = "USD";
						   if(user.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_CANADA)) {
							   currSymbol = "CAD";
						   }
					   }
				   }else {
					   currSymbol = "USD";
				   }
				amountBalances.setVALUECODE(currSymbol );
				//amountBalances.setVALUECODE("USD");
				amountBalances.setEXCHANGERATE(Constants.STRING_NILL);
				amountBalances.setDIFFERENCE("0");
				balances.add(amountBalances);
				
				Balances pointBalances = new Balances();
				pointBalances.setAMOUNT(contactsLoyalty.getLoyaltyBalance() != null ? contactsLoyalty.getLoyaltyBalance().longValue()+Constants.STRING_NILL : "0" );
				pointBalances.setVALUECODE("Points");
				pointBalances.setEXCHANGERATE(Constants.STRING_NILL);
				pointBalances.setDIFFERENCE("0");
				balances.add(pointBalances);
				
				
				if(retBalances != null && !retBalances.isEmpty()) {
					
					for (LoyaltyBalance loyaltyBalance : retBalances) {
						
						Balances balance = new Balances();
						balance.setAMOUNT(loyaltyBalance.getBalance()+Constants.STRING_NILL);
						balance.setDIFFERENCE(Constants.STRING_NILL);
						balance.setEXCHANGERATE(Constants.STRING_NILL);
						balance.setVALUECODE(loyaltyBalance.getValueCode());
						
						balances.add(balance);
					}
					
					
				}
				
				loyaltyInfo.setBALANCES(balances);
				//String kjjk="Loyalty balance is <TAG:LOYALTYINFO.Balances.POINTS/> points and <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.Balances.CURRENCY/>.\nCumulative loyalty points earned to date is <TAG:LOYALTYINFO.LIFETIMEPOINTS/> points.\nRedemption currency available is <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.REDEEMABLEAMOUNT/>.\n\n<TAG:DISCOUNTSHEADER><b>The below discounts are eligible to redeem.</b></TAG:DISCOUNTSHEADER>\n<TAG:DISCOUNTS><TAG:COUPONDISCOUNTINFO.COUPONCODE/> expiry: <TAG:COUPONDISCOUNTINFO.VALIDTO/>\n<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n\n</TAG:DISCOUNTS><TAG:NUDGESHEADER><b>Below is the status of programs entered.</b></TAG:NUDGESHEADER>\n<TAG:NUDGES><TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>\n\n</TAG:NUDGES>";
				/*String DISPLAYTEMPLATE = "Loyalty balance is <TAG:LOYALTYINFO:Balances:AMOUNT/> points and <TAG:CURRENCYCODE/><TAG:LOYALTYINFO:Balances:AMOUNT/>.\n"
						+ "Cumulative loyalty points earned to date is <TAG:LOYALTYINFO:LIFETIMEPOINTS/> points.\n"
						+ "Redemption currency available is <TAG:CURRENCYCODE/><TAG:LOYALTYINFO:REDEEMABLEAMOUNT/>.\n"
						+ "<b>The below discounts are eligible to redeem.<b/>\n"
						+ "<TAG:BEGINDISCOUNTS/>"
						+ "<TAG:COUPONDISCOUNTINFO:DESCRIPTION/>\n"
						+ "<TAG:ENDDISCOUNTS/>"
						+ "<b>Below is the status of programs entered.<b/>\n"
						+ "<TAG:BEGINNUDGECODES/>"
						+ "<TAG:COUPONDISCOUNTINFO:COUPONCODE/>:<TAG:COUPONDISCOUNTINFO:DESCRIPTION/>\n"
						+ "<TAG:COUPONDISCOUNTINFO:NUDGEDESCRIPTION/>\n"
						+ "<TAG:ENDNUDGECODES/>";*/
				//String DISPLAYTEMPLATE = "Loyalty balance is <TAG:LOYALTYINFO.Balances.POINTS/> points and <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.Balances.CURRENCY/>.\nCumulative loyalty points earned to date is <TAG:LOYALTYINFO.LIFETIMEPOINTS/> points.\nRedemption currency available is <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.REDEEMABLEAMOUNT/>.\n\n<TAG:DISCOUNTSHEADER><b>The below discounts are eligible to redeem.</b></TAG:DISCOUNTSHEADER>\n<TAG:DISCOUNTS><TAG:COUPONDISCOUNTINFO.COUPONCODE/> expiry: <TAG:COUPONDISCOUNTINFO.VALIDTO/>>\n<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n\n</TAG:DISCOUNTS>\n\n<TAG:NUDGESHEADER><b>Below is the status of programs entered.</b></TAG:NUDGESHEADER>\n<TAG:NUDGES><TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>\n</TAG:NUDGES>";
				/*String DISPLAYTEMPLATE = "Loyalty balance is <TAG:LOYALTYINFO.Balances.POINTS/> points and <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.Balances.CURRENCY/>.\n"
						+ "Cumulative loyalty points earned to date is <TAG:LOYALTYINFO.LIFETIMEPOINTS/> points.\n"
						+ "Redemption currency available is <TAG:CURRENCYCODE/><TAG:LOYALTYINFO.REDEEMABLEAMOUNT/>.\n\n"
						+ "<b>The below discounts are eligible to redeem.</b>\n"
						+ "<TAG:BEGINDISCOUNTS/>"
						+ "<TAG:COUPONDISCOUNTINFO.COUPONCODE/> expiry: <TAG:COUPONDISCOUNTINFO.VALIDTO/>\n"
						+ "<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n\n"
						+ "<TAG:ENDDISCOUNTS/>\n\n"
						+ "<b>Below is the status of programs entered.</b>\n"
						+ "<TAG:BEGINNUDGECODES/>"
						+ "<TAG:COUPONDISCOUNTINFO.DESCRIPTION/>\n"
						+ "<TAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/>\n\n"
						+ "<TAG:ENDNUDGECODES/>";*/
				/*String DISPLAYTEMPLATE = "Loyalty balance is <TAG:TOTALPOINTS/> points and <TAG:CURRENCYCODE/><TAG:TOTALCURRENCY/>.\n"
						+ "Cumulative loyalty points earned to date is <TAG:AVAILPOINTS/> points."
						+ "Redemption currency available is <TAG:CURRENCYCODE/><TAG:AVAILCURRENCY/>."
						+ "<b>The below discounts are eligible to redeem.<b/>"
						+ "<TAG:BEGINDISCOUNTS/>"
						+ "<TAG:DESCRIPTION/>"
						+ "<TAG:ENDDISCOUNTS/>"
						+ "<b>Below is the status of programs entered.<b/>"
						+ "<TAG:BEGINPROGRAMS/>"
						+ "<TAG:DESCRIPTION/>"
						+ "<NUDGEDESCRIPTION/>"
						+ "<TAG:ENDPROGRAMS/>";*/
				String DISPLAYTEMPLATE = user.getUserOrganization().getLoyaltyDisplayTemplate();//"Loyalty balance is TAG:LOYALTYINFO.Balances.POINTS/ points and TAG:CURRENCYCODE/TAG:LOYALTYINFO.Balances.CURRENCY/.\nCumulative loyalty points earned to date is TAG:LOYALTYINFO.LIFETIMEPOINTS/ points.\nRedemption currency available is TAG:CURRENCYCODE/TAG:LOYALTYINFO.REDEEMABLEAMOUNT/.\n\nTAG:DISCOUNTSHEADER<b>The below discounts are eligible to redeem.</b></TAG:DISCOUNTSHEADER>\nTAG:DISCOUNTSTAG:COUPONDISCOUNTINFO.COUPONCODE/ expiry: TAG:COUPONDISCOUNTINFO.VALIDTO/\nTAG:COUPONDISCOUNTINFO.DESCRIPTION/\n\n</TAG:DISCOUNTS>\n\nTAG:NUDGESHEADER<b>Below is the status of programs entered.</b></TAG:NUDGESHEADER>\nTAG:NUDGESTAG:COUPONDISCOUNTINFO.DESCRIPTION/\nTAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/\n\n</TAG:NUDGES> \n\n";
				loyaltyInfo.setDISPLAYTEMPLATE(DISPLAYTEMPLATE != null && !DISPLAYTEMPLATE.isEmpty() ? DISPLAYTEMPLATE : PropertyUtil.getPropertyValueFromDB("LoyaltyDisplayTemplate"));
				//newly added
				loyaltyInfo.setTIERNAME(tier.getTierName());

			} else if(contactsLoyalty.getProgramTierId() != null && loyaltyProgram.getRewardType() != null &&  loyaltyProgram.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK)) {
				
				LoyaltyProgramTier tier = LoyaltyProgramHelper.getLoyaltyTier(contactsLoyalty.getProgramTierId());
				if(tier== null){
					return loyaltyInfo;
				}
				
				//set OTP details APP-3666
				if(tier.getRedemptionOTPFlag() == OCConstants.FLAG_YES){
					loyaltyInfo.setOTPENABLED("True");
				}
				else{
					loyaltyInfo.setOTPENABLED("False");
				}
				if(tier.getRedemptionOTPFlag() == OCConstants.FLAG_YES && tier.getOtpLimitAmt()!=null){
					OTPRedeemLimit otpRedeemLimit = new OTPRedeemLimit();
					otpRedeemLimit.setAMOUNT(Constants.STRING_NILL+tier.getOtpLimitAmt());
					otpRedeemLimit.setVALUECODE(OCConstants.LOYALTY_TYPE_CURRENCY);
					List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
					otpRedeemLimitlist.add(otpRedeemLimit);
					loyaltyInfo.setOTPREDEEMLIMIT(otpRedeemLimitlist);
				}
				else{
					List<OTPRedeemLimit> otpRedeemLimitlist = new ArrayList<OTPRedeemLimit>();
					loyaltyInfo.setOTPREDEEMLIMIT(otpRedeemLimitlist);
				}
				if(user.isSendExpiryInfo()){
					List<String> expiryInfo = new ArrayList<String>();
					if(user.getSelectedExpiryInfoType() != null && 
							user.getSelectedExpiryInfoType().equals(OCConstants.SELECTED_EXPIRY_INFO_TYPE_AMOUNT) )	{
						List<LoyaltyThresholdBonus> expireThreshBonusList = isBonusOnAmountSetOnexpiry(contactsLoyalty.getProgramId(), true);
						
						String earntype = tier.getEarnType();
						if( (((tier.getRewardExpiryDateType()) != null && tier.getRewardExpiryDateValue() != null ) && ((OCConstants.LOYALTY_TYPE_AMOUNT.equals(earntype) )|| 
								(earntype.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)&& 
										tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)
										&& tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0))) ||
								(expireThreshBonusList!= null && !expireThreshBonusList.isEmpty()) ){
							
							if(expireThreshBonusList != null && !expireThreshBonusList.isEmpty()){
								
								List<String> bonusExpiryInfo = getAmountExpiryList(user, 
										contactsLoyalty.getLoyaltyId(), expireThreshBonusList); 
								if(!bonusExpiryInfo.isEmpty()){
									expiryInfo.addAll(bonusExpiryInfo);
								}
							}
						}
						
						loyaltyInfo.setEXPIRYINFO(expiryInfo);
						
					}
				}
				
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				String conversionRule = ltyPrgmSevice.getConversionRule(tier);
				
				String userCurrencySymbol = "$";
				if(!conversionRule.isEmpty() && conversionRule.contains("$")){
					
					String currSymbol = Utility.countryCurrencyMap.get(user.getCountryType());
					if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol + " ";
					    
					conversionRule = conversionRule.contains("$")? conversionRule.replace("$", userCurrencySymbol):conversionRule; 
				}
			 loyaltyInfo.setREDEEMDESC(conversionRule);
			 
			 
			 //slect sum(entered_amount) from loyalty_trx_child where user_id programid loyalty_id trxtype =redemption, enterdamount_type / valuecode = perk and  month(created_date) = month(current date) 
			 
			 double totalReedmCurr = 0.0;
			 long covertedAmnt = 0;
			 long usageLimit = tier.getPerkLimitValue()!=null ? tier.getPerkLimitValue() : 0;
			 LoyaltyTransactionChildDao trxChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			 double lastRedemptions = trxChildDao.fetchLastPerkRedemption(user.getUserId(),loyaltyProgram.getProgramId(),contactsLoyalty.getLoyaltyId(),tier.getEarnType());
			 logger.info("last redemptions of perks"+lastRedemptions);
			 
			 LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
			 LoyaltyBalance ltyBalance = loyaltyBalanceDao.findBy(user.getUserId(), contactsLoyalty.getLoyaltyId(), tier.getEarnType());
			 
			 if(ltyBalance!=null && tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
						&& ltyBalance.getBalance() != null && ltyBalance.getBalance() > 0){
				
					double factor = ltyBalance.getBalance()/tier.getConvertFromPoints();
					int intFactor = (int)factor;
					covertedAmnt = (long)(tier.getConvertToAmount() * intFactor);
					totalReedmCurr = covertedAmnt;//-lastRedemptions;
					logger.info("total redem currency after converting perks-------"+totalReedmCurr);
			}
			 
			 
				Double totalRedeemableAmount = totalReedmCurr;
				logger.info("totalRedeemableAmount>>>>"+totalRedeemableAmount);
				String givenRecptAmount = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT();	
				if(givenRecptAmount != null && !givenRecptAmount.trim().isEmpty() && Double.parseDouble(givenRecptAmount.trim())>0)	{

					if(tier.getRedemptionPercentageLimit() != null && tier.getRedemptionPercentageLimit() > 0 ){
						totalRedeemableAmount = (tier.getRedemptionPercentageLimit()/100) * Double.parseDouble(givenRecptAmount);
					}
					//APP-1967
					if(tier.getRedemptionValueLimit() != null && tier.getRedemptionValueLimit() > 0
							&& totalRedeemableAmount > tier.getRedemptionValueLimit()){
						totalRedeemableAmount = tier.getRedemptionValueLimit();
					}
					if(totalRedeemableAmount >= 0 && totalReedmCurr > totalRedeemableAmount){
						totalReedmCurr = totalRedeemableAmount;
					}
				}
				
				logger.info("totalReedmCurr after redeemable amount calculation"+totalReedmCurr);
				if(totalReedmCurr>usageLimit-lastRedemptions) {
					
					totalReedmCurr = usageLimit-lastRedemptions <0?0: usageLimit-lastRedemptions;
				} /*
					 * else { totalReedmCurr = totalReedmCurr > lastRedemptions ?
					 * totalReedmCurr-lastRedemptions : lastRedemptions-totalReedmCurr; }
					 */
				
				logger.info("final totalReedmCurr"+totalReedmCurr);
				
				LoyaltyProgramExclusionDao loyaltyProgramExclusionDao=(LoyaltyProgramExclusionDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO);
				LoyaltyProgramExclusion loyaltyExclusion =loyaltyProgramExclusionDao.getExlusionByProgId(contactsLoyalty.getProgramId());
				String excludeRedemptionNote = "";
				
				// redeemable balance = 0.00 for excluded stores
				if(loyaltyExclusion != null && loyaltyExclusion.getStrRedempChk()!=null && loyaltyExclusion.getStrRedempChk()) {
					if(loyaltyExclusion.getSelectedStoreStr() != null && !loyaltyExclusion.getSelectedStoreStr().trim().isEmpty()){
						String[] storeNumberArr = loyaltyExclusion.getSelectedStoreStr().split(";=;");
						for(String storeNo : storeNumberArr){
							if(couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO()
									.getSTORENUMBER().trim().equals(storeNo.trim())){
								totalReedmCurr = 0;
								excludeRedemptionNote = "Redemption is not allowed for selected store";
								loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
							}
						}
					} 
				}
				if(loyaltyExclusion != null &&  loyaltyExclusion.getAllStrChk()!=null && loyaltyExclusion.getAllStrChk()) {
					totalReedmCurr = 0;
					excludeRedemptionNote = "Redemption is not allowed for all stores";
					loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
				}
				
				// redeemable balance = 0.00 for excluded days
				if(loyaltyExclusion != null &&  loyaltyExclusion.getExclRedemDateStr() != null){
					
					Date date = Calendar.getInstance().getTime();  
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
					Calendar cal1 = Calendar.getInstance();
					cal1.setTime(dateFormat.parse(dateFormat.format(date)));
					
					String[] dateArr = null;
					dateArr = loyaltyExclusion.getExclRedemDateStr().split(";=;");
					
						for(String dateStr : dateArr){
							if(!dateStr.contains(",")) {
								if(dateStr.length()==3) {
									SimpleDateFormat sdf = new SimpleDateFormat("MMM");
									Calendar cal = Calendar.getInstance();
									cal.setTime(sdf.parse(dateStr));
								
									if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH))){
										totalReedmCurr = 0;
										excludeRedemptionNote = "Redemption is not allowed in this month";
										loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
									}
								} else if(dateStr.length()==4) {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
									Calendar cal = Calendar.getInstance();
									cal.setTime(sdf.parse(dateStr));
								
									if(	(cal.get(Calendar.YEAR) == cal1.get(Calendar.YEAR))){
										totalReedmCurr = 0;
										excludeRedemptionNote = "Redemption is not allowed in this year";
										loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
									}
								} else if(dateStr.contains("-")) {
									SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
									Calendar cal = Calendar.getInstance();
									cal.setTime(sdf.parse(dateStr));
								
									if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH)) &&
										(cal.get(Calendar.YEAR) == cal1.get(Calendar.YEAR))){
										totalReedmCurr = 0;
										excludeRedemptionNote = "Redemption is not allowed in this month";
										loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
									}
								} else {
									SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
									Calendar cal = Calendar.getInstance();
									cal.setTime(sdf.parse(dateStr));
								
									if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH)) &&
										  (cal.get(Calendar.DAY_OF_MONTH) == cal1.get(Calendar.DAY_OF_MONTH))){
										totalReedmCurr = 0;
										excludeRedemptionNote = "Redemption is not allowed today";
										loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
									}
								}
							} else {
								SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
								Calendar cal = Calendar.getInstance();
								cal.setTime(sdf.parse(dateStr));
							
								if(	(cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH)) &&
									(cal.get(Calendar.DAY_OF_MONTH) == cal1.get(Calendar.DAY_OF_MONTH)) && (cal.get(Calendar.YEAR) == cal1.get(Calendar.YEAR))){
									totalReedmCurr = 0;
									excludeRedemptionNote = "Redemption is not allowed today";
									loyaltyInfo.setEXCLREDMPNOTE(excludeRedemptionNote);
								}
							}
							
						}
				}
				
				Double minReceiptAmt = tier.getMinReceiptValue();
				String receiptAmtStr = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT();
				Double receiptAmt = 0.0;
				receiptAmt = receiptAmtStr != null && !receiptAmtStr.isEmpty() ? 
						Double.parseDouble(receiptAmtStr) : receiptAmt;
				if(minReceiptAmt!=null && receiptAmt!=null && minReceiptAmt>0 && receiptAmt>0) {
					if(receiptAmt<minReceiptAmt) {
						totalReedmCurr = 0;
					}
				}
				
				
				loyaltyInfo.setREDEEMABLEAMOUNT(BigDecimal.valueOf(totalReedmCurr).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
				String docSID = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getDOCSID();
				String checkReward = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCHECKREWARDS();
				if(checkReward != null && !checkReward.isEmpty()){
					
					boolean rewardExists = LoyaltyProgramHelper.checkRewardGainedAlready(contactsLoyalty, docSID);
					loyaltyInfo.setCHECKREWARDS(rewardExists ? checkReward : "");
				}
				
				String[] balArr = new String[3];
				String pOINTSEARNEDTODAY = Constants.STRING_NILL;
				String CurrEARNEDTODAY = Constants.STRING_NILL;
				String pointsRedeemedToday = Constants.STRING_NILL;
				if(docSID != null && !docSID.isEmpty()){
					balArr =  LoyaltyProgramHelper.pointsEarnedIncurrent(contactsLoyalty, docSID) ;
					pOINTSEARNEDTODAY = balArr[0];
					CurrEARNEDTODAY = balArr[1];
					pointsRedeemedToday = balArr[2];
				}
				loyaltyInfo.setREDEEMABLEAMOUNT(BigDecimal.valueOf(totalReedmCurr).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL);
				loyaltyInfo.setPOINTSEARNED(pOINTSEARNEDTODAY != null ? pOINTSEARNEDTODAY : "0" );
				loyaltyInfo.setCURRENCYEARNED(CurrEARNEDTODAY != null ? CurrEARNEDTODAY : "0.0" );
				
				loyaltyInfo.setLIFETIMEPOINTS(contactsLoyalty.getTotalLoyaltyEarned() != null ? contactsLoyalty.getTotalLoyaltyEarned().longValue()+Constants.STRING_NILL:"");
				List<LoyaltyBalance> retBalances = null;
				if(includeFBP) retBalances = LoyaltyProgramHelper.getOtherBalances(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId());
				else{
					retBalances = LoyaltyProgramHelper.getOnlyRewards(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(), contactsLoyalty.getOrgId());
				}
				List<Balances> balances = new ArrayList<Balances>();
				
				Balances amountBalances = new Balances();
						
				
				amountBalances.setAMOUNT(contactsLoyalty.getGiftcardBalance() != null ? 
						(BigDecimal.valueOf(contactsLoyalty.getGiftcardBalance()).setScale(2, RoundingMode.HALF_UP) +Constants.STRING_NILL): "0.00");
				String currSymbol = Utility.countryCurrencyMap.get(user.getCountryType());
				   if(currSymbol != null && !currSymbol.isEmpty()) {
					   if(currSymbol.equals("$")) {
						   currSymbol = "USD";
						   if(user.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_CANADA)) {
							   currSymbol = "CAD";
						   }
					   }
				   }else {
					   currSymbol = "USD";
				   }
				amountBalances.setVALUECODE(currSymbol );
				//amountBalances.setVALUECODE("USD");
				amountBalances.setEXCHANGERATE(Constants.STRING_NILL);
				amountBalances.setDIFFERENCE("0");
				balances.add(amountBalances);
				
				Balances pointBalances = new Balances();
				pointBalances.setAMOUNT(contactsLoyalty.getLoyaltyBalance() != null ? contactsLoyalty.getLoyaltyBalance().longValue()+Constants.STRING_NILL : "0" );
				pointBalances.setVALUECODE("Points");
				pointBalances.setEXCHANGERATE(Constants.STRING_NILL);
				pointBalances.setDIFFERENCE("0");
				balances.add(pointBalances);
				
				
				if(retBalances != null && !retBalances.isEmpty()) {
					
					for (LoyaltyBalance loyaltyBalance : retBalances) {
						
						Balances balance = new Balances();
						balance.setAMOUNT(loyaltyBalance.getBalance()+Constants.STRING_NILL);
						balance.setDIFFERENCE(Constants.STRING_NILL);
						balance.setEXCHANGERATE(Constants.STRING_NILL);
						balance.setVALUECODE(loyaltyBalance.getValueCode());
						
						balances.add(balance);
					}
					
					
				}
				
				loyaltyInfo.setBALANCES(balances);
				
				String DISPLAYTEMPLATE = user.getUserOrganization().getLoyaltyDisplayTemplate();//"Loyalty balance is TAG:LOYALTYINFO.Balances.POINTS/ points and TAG:CURRENCYCODE/TAG:LOYALTYINFO.Balances.CURRENCY/.\nCumulative loyalty points earned to date is TAG:LOYALTYINFO.LIFETIMEPOINTS/ points.\nRedemption currency available is TAG:CURRENCYCODE/TAG:LOYALTYINFO.REDEEMABLEAMOUNT/.\n\nTAG:DISCOUNTSHEADER<b>The below discounts are eligible to redeem.</b></TAG:DISCOUNTSHEADER>\nTAG:DISCOUNTSTAG:COUPONDISCOUNTINFO.COUPONCODE/ expiry: TAG:COUPONDISCOUNTINFO.VALIDTO/\nTAG:COUPONDISCOUNTINFO.DESCRIPTION/\n\n</TAG:DISCOUNTS>\n\nTAG:NUDGESHEADER<b>Below is the status of programs entered.</b></TAG:NUDGESHEADER>\nTAG:NUDGESTAG:COUPONDISCOUNTINFO.DESCRIPTION/\nTAG:COUPONDISCOUNTINFO.NUDGEDESCRIPTION/\n\n</TAG:NUDGES> \n\n";
				loyaltyInfo.setDISPLAYTEMPLATE(DISPLAYTEMPLATE != null && !DISPLAYTEMPLATE.isEmpty() ? DISPLAYTEMPLATE : PropertyUtil.getPropertyValueFromDB("LoyaltyDisplayTemplate"));
				//newly added
				loyaltyInfo.setTIERNAME(tier.getTierName());
			}
			
			
			//loyaltyInfo.setBALANCES(bALANCES);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("exception", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("exception", e);
		}
		return loyaltyInfo;
		
	}


	/*private List<CouponDiscountInfo> promoReedemptionWithLoyaltyPts(CouponCodeEnquObj couponCodeEnquObj,Users user,List<Coupons> listPromoCoupons) throws BaseServiceException {


		List<CouponDiscountInfo> coupDiscInfoList=null ;
		try {
			boolean isOcLoyalty = true ;
			logger.debug("-------entered promoReedemptionWithLoyaltyPts---------");
			CouponDiscountGenerateDao coupDiscGenDao=(CouponDiscountGenerateDao)
					ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);

			String coupIdStr = Constants.STRING_NILL;
			for (Coupons coupons : listPromoCoupons) {
				if(coupIdStr.trim().length()  ==  0) coupIdStr = Constants.STRING_NILL+coupons.getCouponId();
				else coupIdStr += ","+coupons.getCouponId();
			}

			List<CouponDiscountGeneration>  coupDisList  = coupDiscGenDao.findCoupCodesByCouponObj(coupIdStr);
			Map<Long , List<CouponDiscountGeneration>> setOfCoupDisMap = new HashMap<Long, List<CouponDiscountGeneration>>();
			List<CouponDiscountGeneration> tempCoupDisList = null;

			for (CouponDiscountGeneration eachCoupDisGenObj : coupDisList) {

				if(setOfCoupDisMap.containsKey(eachCoupDisGenObj.getCoupons().getCouponId())){
					tempCoupDisList = setOfCoupDisMap.get(eachCoupDisGenObj.getCoupons().getCouponId());
					tempCoupDisList.add(eachCoupDisGenObj);
				}else{
					tempCoupDisList = new ArrayList<CouponDiscountGeneration>();
					tempCoupDisList.add(eachCoupDisGenObj);
				}
				setOfCoupDisMap.put(eachCoupDisGenObj.getCoupons().getCouponId(), tempCoupDisList);
			}
			logger.info("setDoscountInfo is :: "+setOfCoupDisMap);

			Set<Long> coupObjIdSet = setOfCoupDisMap.keySet();
			for (Long eachCoupId : coupObjIdSet) {
				tempCoupDisList = setOfCoupDisMap.get(eachCoupId);
				Coupons eachCouponObj = ((CouponDiscountGeneration)tempCoupDisList.get(0)).getCoupons();

				coupDiscInfoList = setCoupDiscInfoObj(coupDiscInfoList,couponCodeEnquObj,eachCouponObj,isOcLoyalty,eachCouponObj.getCouponCode(), user);

				if(coupDiscInfoList==null) {
					statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return statusInfo;
				}

			}

			//statusInfo = new StatusInfo("0","Promo-code enquiry is successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);


			for (Coupons eachCouponObj : listPromoCoupons) {

				coupDiscInfoList = setCoupDiscInfoObj(coupDiscInfoList,couponCodeEnquObj,eachCouponObj,isOcLoyalty,eachCouponObj.getCouponCode(), user);

			}

		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing promoReedemptionWithLoyaltyPts::::: ", e);
		}
		logger.debug("-------exit  promoReedemptionWithLoyaltyPts---------");
		return coupDiscInfoList;
	}//promoReedemptionWithLoyaltyPts
*/

	/*private List<CouponDiscountInfo> setCoupDiscInfoObj(List<CouponDiscountInfo> coupDiscInfoList ,
			CouponCodeEnquObj couponCodeEnquObj,Coupons eachCouponObj, boolean isOcLoyalty, 
			String couponCodeStr, Users user) throws BaseServiceException {

		logger.debug("-------entered setCoupDiscInfoObj---------");
		if(coupDiscInfoList== null) {
			coupDiscInfoList = new ArrayList<CouponDiscountInfo>() ;
		}
		CouponDiscountInfo couponDiscountInfo=new CouponDiscountInfo();
		couponDiscountInfo.setCOUPONNAME(eachCouponObj.getCouponName());
		couponDiscountInfo.setCOUPONCODE(couponCodeStr);
		
		String loyaltyPointsStr = eachCouponObj.getRequiredLoyltyPoits() == null ? Constants.STRING_NILL : Constants.STRING_NILL+eachCouponObj.getRequiredLoyltyPoits();
		couponDiscountInfo.setLOYALTYPOINTS(loyaltyPointsStr);

		if(eachCouponObj.getRequiredLoyltyPoits() != null && eachCouponObj.getLoyaltyPoints() != null) {
			couponDiscountInfo.setCOUPONTYPE("ONLY-LOYALTY"); 

		}else {

			couponDiscountInfo.setCOUPONTYPE("REGULAR");
		}

		if(eachCouponObj.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_STATIC)){
			couponDiscountInfo.setVALIDFROM(MyCalendar.calendarToString(
					eachCouponObj.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
			logger.info("static to date "+eachCouponObj.getCouponExpiryDate());
			couponDiscountInfo.setVALIDTO(MyCalendar.calendarToString(eachCouponObj.
					getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
		}
		else{
			couponDiscountInfo.setVALIDFROM(MyCalendar.calendarToString(
					eachCouponObj.getUserCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
			Calendar cal = Calendar.getInstance();
			cal.set(2020, 11, 31, 23, 59, 59);
			logger.info("dynamic to date "+cal.toString());
			couponDiscountInfo.setVALIDTO(MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR));
		}

		boolean isItemCode = false;
		String disCountType =Constants.STRING_NILL;
		if(eachCouponObj.getDiscountType().equals("Percentage") && eachCouponObj.getDiscountCriteria().equals("Total Purchase Amount")) {

			couponDiscountInfo.setDISCOUNTCRITERIA("PERCENTAGE-MVP");
			disCountType = "P";
		}
		else if(eachCouponObj.getDiscountType().equals("Percentage") && eachCouponObj.getDiscountCriteria().equals("SKU")) {
			couponDiscountInfo.setDISCOUNTCRITERIA("PERCENTAGE-IC");
			isItemCode = true;
			disCountType = "P";
		}
		else if(eachCouponObj.getDiscountType().equals("Value") && eachCouponObj.getDiscountCriteria().equals("Total Purchase Amount")) {
			couponDiscountInfo.setDISCOUNTCRITERIA("VALUE-MVP");
			disCountType = "V";
		}
		else {
			couponDiscountInfo.setDISCOUNTCRITERIA("VALUE-I");
			isItemCode = true;
			disCountType = "V";
		}
		couponDiscountInfo.setACCUMULATEDISCOUNT(Constants.STRING_NILL);
		couponDiscountInfo.setELIGIBILITY(Constants.STRING_NILL);
		couponDiscountInfo.setEXCLUDEDISCOUNTEDITEMS(Constants.STRING_NILL);
		
		couponDiscountInfo = getDiscountAmount(couponCodeEnquObj, eachCouponObj, isItemCode, 
				couponDiscountInfo, coupDiscInfoList,disCountType,isOcLoyalty, user);
		Gson temp = new Gson();

		String test = temp.toJson(couponDiscountInfo);
		logger.info(" discount json is "+test);
		coupDiscInfoList.add(couponDiscountInfo);
		//		logger.debug("-------exit  setCoupDiscInfoObj---------"+coupDiscInfoList);
		return coupDiscInfoList;
	}//setCoupDiscInfoObj
*/

	private CouponDiscountInfo getDiscountAmount(CouponCodeEnquObj couponCodeEnquObj,Coupons coupObj,
			boolean isItemCode,CouponDiscountInfo couponDiscountInfo,List<CouponDiscountInfo> coupDiscInfoList,
			String disCountType, boolean isOcLoyalty, Users user, ContactsLoyalty contactsLoyaltyObj) throws BaseServiceException {
		try {
			logger.debug("-------entered getDiscountAmount---------");
			List<PurchasedItems> purchaseList=couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS();
			CouponDiscountGenerateDao couponDiscountGenerateDao=(CouponDiscountGenerateDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
			//SkuFileDao skuFileDao = (SkuFileDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SKU_FILE_DAO);

			List<CouponDiscountGeneration>  coupDisList =null;
			//List<SkuFile> skuFilesList = null;
			if(!isOcLoyalty){
				if(isItemCode){

					Set<String> itemCodeSet = new HashSet<String>();
					if(purchaseList != null && purchaseList.size() > 0) {

						for (Object object : purchaseList) {
							if(object instanceof PurchasedItems){

								PurchasedItems tempObj = (PurchasedItems)object;

								if(isItemCode) {
									itemCodeSet.add(tempObj.getITEMCODE());

								}
							}
						} //for
					}

					String itemCodeStr = Constants.STRING_NILL;


					if(itemCodeSet != null && itemCodeSet.size() > 0){

						for (String string : itemCodeSet) {
							if(itemCodeStr.trim().length() > 0) {
								itemCodeStr = itemCodeStr+", '"+string+"'";
							}else {
								itemCodeStr = itemCodeStr +"'"+string+"'";
							}
						}
						/*skuFilesList = skuFileDao.findInventoryBy(user.getUserId(), itemCodeStr);

					Map<String, String> itemsMap = new HashMap<String, String>();

					if(skuFilesList != null && !skuFilesList.isEmpty()) {
						for (SkuFile inventory : skuFilesList) {

							if(inventory.get)

						}//for

					}//if */


					}

					coupDisList = couponDiscountGenerateDao.findAllDiscountsBy(coupObj.getCouponId()+Constants.STRING_NILL, itemCodeStr, user.getUserId());

				}else {

					coupDisList = couponDiscountGenerateDao.findByCoupon(coupObj);
				}

				logger.info(">>> coupDisList size  is  "+coupDisList.size());

				List<DiscountInfo> discountInfoList  = new ArrayList<DiscountInfo>();

				Map<Double, DiscountInfo> isSameDisMap = new HashMap<Double, DiscountInfo>();

				if(coupDisList != null && coupDisList.size() > 0 ) {
					DiscountInfo  tempObj1 = null;
					List<ItemCodeInfo>	tempArrObj = null;

					for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
						if(!(isSameDisMap.containsKey(coupDisGenObj.getDiscount()))) {
							tempObj1 = new DiscountInfo();
							tempObj1.setVALUE(coupDisGenObj.getDiscount().toString());
							tempObj1.setVALUECODE(disCountType);
							tempObj1.setSHIPPINGFEE(coupDisGenObj.getShippingFee() == null ? "" : coupDisGenObj.getShippingFee());
							////tempObj1.setSHIPPINGFEEFREE(coupDisGenObj.getShippingFeeFree() == null ? "" : coupDisGenObj.getShippingFeeFree());
							tempObj1.setSHIPPINGFEETYPE(coupDisGenObj.getShippingFeeType() == null ?  "" : coupDisGenObj.getShippingFeeType());
							tempArrObj = new ArrayList<ItemCodeInfo>();
							ItemCodeInfo tempObj2 = new ItemCodeInfo();


							if(isItemCode) {
								tempObj1.setMINPURCHASEVALUE(Constants.STRING_NILL);
								tempObj2.setITEMCODE(coupDisGenObj.getItemCategory());
								tempObj2.setITEMPRICE(coupDisGenObj.getItemListPrice() != null ? coupDisGenObj.getItemListPrice() : Constants.STRING_NILL);
								//tempObj2.setATTRIBUTECODE(Constants.STRING_NILL);
								//tempObj2.setITEMATTRIBUTE(Constants.STRING_NILL);
								tempObj2.setITEMDISCOUNT(Constants.STRING_NILL);
								tempObj2.setMAXITEMDISCOUNT(Constants.STRING_NILL);
								tempObj2.setQUANTITY(Constants.STRING_NILL);
								tempObj2.setQUANTITYCRITERIA(Constants.STRING_NILL);
								tempArrObj.add(tempObj2);

							}else {

								tempObj1.setMINPURCHASEVALUE(coupDisGenObj.getTotPurchaseAmount().toString());
							}
							tempObj1.setITEMCODEINFO(tempArrObj);
							tempObj1.setMAXRECEIPTDISCOUNT(Constants.STRING_NILL);
							tempObj1.setRECEIPTDISCOUNT(Constants.STRING_NILL);
							isSameDisMap.put(coupDisGenObj.getDiscount(), tempObj1);

						}else  {

							tempObj1 = isSameDisMap.get(coupDisGenObj.getDiscount());
							tempArrObj =(List<ItemCodeInfo>)tempObj1.getITEMCODEINFO();

							ItemCodeInfo tempObj2 = new ItemCodeInfo();
							tempObj2.setITEMCODE(coupDisGenObj.getItemCategory());
							tempObj2.setITEMPRICE(coupDisGenObj.getItemListPrice() != null ? coupDisGenObj.getItemListPrice() : Constants.STRING_NILL);
							//tempObj2.setATTRIBUTECODE(Constants.STRING_NILL);
							//tempObj2.setITEMATTRIBUTE(Constants.STRING_NILL);
							tempObj2.setITEMDISCOUNT(Constants.STRING_NILL);
							tempObj2.setMAXITEMDISCOUNT(Constants.STRING_NILL);
							tempObj2.setQUANTITY(Constants.STRING_NILL);
							tempObj2.setQUANTITYCRITERIA(Constants.STRING_NILL);
							tempArrObj.add(tempObj2);
							tempObj1.setITEMCODEINFO(tempArrObj);

						}


					} // for
					logger.info("isSameDisMap is  :: "+isSameDisMap);
					Set<Double> keySet = isSameDisMap.keySet();
					for (Double double1 : keySet) {
						discountInfoList.add(isSameDisMap.get(double1));
					}
					couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
				}
			}else{
				CouponDiscountGenerateDao coupDiscGenDao  = (CouponDiscountGenerateDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
				CouponDiscountGenerateDaoForDML couponDiscountGenerateDaoForDML  = (CouponDiscountGenerateDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO_FOR_DML);

				if(isItemCode){

					List<String> retList = coupDiscGenDao.findDistinctAttr(coupObj.getCouponId());

					//long deleted = couponDiscountGenerateDaoForDML.deleteTempPromoDumpBy(coupObj.getCouponId(), user.getUserId());
					coupDisList = new ArrayList<CouponDiscountGeneration>();
					for (String CDGAttr : retList) {

						List<CouponDiscountGeneration> tempcoupDisList = coupDiscGenDao.findBy(coupObj.getCouponId(), user.getUserId(), 
								Utility.CDGAttrToSKUMap.get(CDGAttr), CDGAttr);
						if(tempcoupDisList != null && !tempcoupDisList.isEmpty()) {

							coupDisList.addAll(tempcoupDisList);
						}

					}//for
					if(coupDisList == null || coupDisList.isEmpty()) {

						//List<Object[]> list = coupDiscGenDao.getDiscAndNumOfItems(orgOwner);
						coupDisList = couponDiscountGenerateDao.findByCoupon(coupObj);
					}
					if(coupDisList != null && !coupDisList.isEmpty()){
						List<DiscountInfo> discountInfoList  = new ArrayList<DiscountInfo>();

						Map<Double, DiscountInfo> isSameDisMap = new HashMap<Double, DiscountInfo>();

						DiscountInfo  tempObj1 = null;
						List<ItemCodeInfo>	tempArrObj = null;

						for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
							
							String programID = coupDisGenObj.getProgram() != null ? coupDisGenObj.getProgram().toString() : Constants.STRING_NILL;
							String tierID = coupDisGenObj.getTierNum() != null ? coupDisGenObj.getTierNum().toString() : Constants.STRING_NILL;
							String cardSetId = coupDisGenObj.getCardSetNum() != null ? coupDisGenObj.getCardSetNum().toString() : Constants.STRING_NILL;
							/*if((programID != null && !programID.isEmpty() &&( (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramId().toString().equals(programID)) || (programID != null && contactsLoyaltyObj == null)))
									|| (tierID !=null && !tierID.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramTierId().toString().equals(tierID))) ||
									(cardSetId != null && !cardSetId.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getCardSetId().toString().equals(cardSetId))) ){
								logger.debug("elegibility on tier + cardset are failed");
								continue;
							}*/
							if(!(isSameDisMap.containsKey(coupDisGenObj.getDiscount()))) {
								tempObj1 = new DiscountInfo();
								tempObj1.setVALUE(coupDisGenObj.getDiscount().toString());
								tempObj1.setVALUECODE(disCountType);
								tempObj1.setSHIPPINGFEE(coupDisGenObj.getShippingFee() == null ? "" : coupDisGenObj.getShippingFee());
								//tempObj1.setSHIPPINGFEEFREE(coupDisGenObj.getShippingFeeFree() == null ? "" : coupDisGenObj.getShippingFeeFree());
								tempObj1.setSHIPPINGFEETYPE(coupDisGenObj.getShippingFeeType() == null ?  "" : coupDisGenObj.getShippingFeeType());
								tempArrObj = new ArrayList<ItemCodeInfo>();
								ItemCodeInfo tempObj2 = new ItemCodeInfo();


								if(isItemCode) {
									tempObj1.setMINPURCHASEVALUE(Constants.STRING_NILL);
									tempObj2.setITEMCODE(coupDisGenObj.getItemCategory() != null ? coupDisGenObj.getItemCategory() : Constants.STRING_NILL);
									tempObj2.setITEMPRICE(coupDisGenObj.getItemListPrice() != null ? coupDisGenObj.getItemListPrice() : Constants.STRING_NILL);
									//tempObj2.setATTRIBUTECODE(Constants.STRING_NILL);
									//tempObj2.setITEMATTRIBUTE(Constants.STRING_NILL);
									tempObj2.setITEMDISCOUNT(Constants.STRING_NILL);
									tempObj2.setMAXITEMDISCOUNT(Constants.STRING_NILL);
									tempObj2.setQUANTITY(Constants.STRING_NILL);
									tempObj2.setQUANTITYCRITERIA(Constants.STRING_NILL);
									tempArrObj.add(tempObj2);

								}else {

									tempObj1.setMINPURCHASEVALUE(coupDisGenObj.getTotPurchaseAmount().toString());
								}
								tempObj1.setITEMCODEINFO(tempArrObj);
								tempObj1.setMAXRECEIPTDISCOUNT(Constants.STRING_NILL);
								tempObj1.setRECEIPTDISCOUNT(Constants.STRING_NILL);
								isSameDisMap.put(coupDisGenObj.getDiscount(), tempObj1);

							}else  {

								tempObj1 = isSameDisMap.get(coupDisGenObj.getDiscount());
								tempArrObj =(List<ItemCodeInfo>)tempObj1.getITEMCODEINFO();

								ItemCodeInfo tempObj2 = new ItemCodeInfo();
								tempObj2.setITEMCODE(coupDisGenObj.getItemCategory());
								tempObj2.setITEMPRICE(coupDisGenObj.getItemListPrice() != null ? coupDisGenObj.getItemListPrice() : Constants.STRING_NILL);
								//tempObj2.setATTRIBUTECODE(Constants.STRING_NILL);
								//tempObj2.setITEMATTRIBUTE(Constants.STRING_NILL);
								tempObj2.setITEMDISCOUNT(Constants.STRING_NILL);
								tempObj2.setMAXITEMDISCOUNT(Constants.STRING_NILL);
								tempObj2.setQUANTITY(Constants.STRING_NILL);
								tempObj2.setQUANTITYCRITERIA(Constants.STRING_NILL);
								tempArrObj.add(tempObj2);
								tempObj1.setITEMCODEINFO(tempArrObj);

							}


						} // for
						logger.info("isSameDisMap is  :: "+isSameDisMap);
						Set<Double> keySet = isSameDisMap.keySet();
						for (Double double1 : keySet) {
							discountInfoList.add(isSameDisMap.get(double1));
						}
						if(discountInfoList.isEmpty()) return null;
						couponDiscountInfo.setDISCOUNTINFO(discountInfoList);

					}

				}else{	

					List<DiscountInfo> discountInfoList  = new ArrayList<DiscountInfo>();

					Map<Double, DiscountInfo> isSameDisMap = new HashMap<Double, DiscountInfo>();
					coupDisList = couponDiscountGenerateDao.findByCoupon(coupObj);
					if(coupDisList != null && coupDisList.size() > 0 ) {
						DiscountInfo  tempObj1 = null;
						List<ItemCodeInfo>	tempArrObj = null;

						for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
							if(!(isSameDisMap.containsKey(coupDisGenObj.getDiscount()))) {
								tempObj1 = new DiscountInfo();
								tempObj1.setVALUE(coupDisGenObj.getDiscount().toString());
								tempObj1.setVALUECODE(disCountType);
								tempObj1.setSHIPPINGFEE(coupDisGenObj.getShippingFee() == null ? "" : coupDisGenObj.getShippingFee());
								//tempObj1.setSHIPPINGFEEFREE(coupDisGenObj.getShippingFeeFree() == null ? "" : coupDisGenObj.getShippingFeeFree());
								tempObj1.setSHIPPINGFEETYPE(coupDisGenObj.getShippingFeeType() == null ?  "" : coupDisGenObj.getShippingFeeType());
								tempArrObj = new ArrayList<ItemCodeInfo>();
								ItemCodeInfo tempObj2 = new ItemCodeInfo();


								if(isItemCode) {
									tempObj1.setMINPURCHASEVALUE(Constants.STRING_NILL);
									tempObj2.setITEMCODE(coupDisGenObj.getItemCategory());
									tempObj2.setITEMPRICE(coupDisGenObj.getItemListPrice() != null ? coupDisGenObj.getItemListPrice() : Constants.STRING_NILL);
									tempArrObj.add(tempObj2);

								}else {

									tempObj1.setMINPURCHASEVALUE(coupDisGenObj.getTotPurchaseAmount().toString());
								}
								tempObj1.setITEMCODEINFO(tempArrObj);

								isSameDisMap.put(coupDisGenObj.getDiscount(), tempObj1);

							}else  {

								tempObj1 = isSameDisMap.get(coupDisGenObj.getDiscount());
								tempArrObj =(List<ItemCodeInfo>)tempObj1.getITEMCODEINFO();

								ItemCodeInfo tempObj2 = new ItemCodeInfo();
								tempObj2.setITEMCODE(coupDisGenObj.getItemCategory());
								tempObj2.setITEMPRICE(coupDisGenObj.getItemListPrice() != null ? coupDisGenObj.getItemListPrice() : Constants.STRING_NILL);
								tempArrObj.add(tempObj2);
								tempObj1.setITEMCODEINFO(tempArrObj);

							}


						} // for
						logger.info("isSameDisMap is  :: "+isSameDisMap);
						Set<Double> keySet = isSameDisMap.keySet();
						for (Double double1 : keySet) {
							discountInfoList.add(isSameDisMap.get(double1));
						}
						couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
						//APP-3667 - multiplier in discount
						if(coupObj.getMultiplierValue()!=null) {
							
							String receiptAmount = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT();
							boolean isEligibleWithMP = false;
							int requiredPoints = 0;
							if(coupObj.getDiscountType()!=null && coupObj.getDiscountType().equals("Percentage")) {
								double discAmnt = (Double.parseDouble(receiptAmount)/100)*(Double.parseDouble(tempObj1.getVALUE()));
								requiredPoints = (int)(discAmnt*coupObj.getMultiplierValue());
							}else if(coupObj.getDiscountType()!=null && coupObj.getDiscountType().equals("Value")) {
								requiredPoints = (int)(Double.parseDouble(tempObj1.getVALUE())*coupObj.getMultiplierValue());
							}
							logger.info("requiredPoints 3>>>"+requiredPoints);
							
							LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
							if(coupObj.getValueCode() != null &&
									!coupObj.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
								LoyaltyBalance balance = loyaltyBalanceDao.findBy(user.getUserId(), contactsLoyaltyObj.getProgramId(),
										contactsLoyaltyObj.getLoyaltyId(),  coupObj.getValueCode());
								if(balance != null && balance.getBalance()>=requiredPoints)  isEligibleWithMP = true;
								
								
							}else if((coupObj.getValueCode() == null || coupObj.getValueCode().equals(OCConstants.LOYALTY_POINTS)) &&
									contactsLoyaltyObj.getLoyaltyBalance() != null && contactsLoyaltyObj.getLoyaltyBalance() >= requiredPoints) {
								
								isEligibleWithMP = true;
							}
							if(isEligibleWithMP) couponDiscountInfo.setLOYALTYPOINTS(requiredPoints>0?requiredPoints+"":"");
							else return null;
						}
					}
				}
			}

		}catch(Exception e){
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getDiscountAmount::::: ", e);
		}

		logger.debug("-------exit  getDiscountAmount---------"+couponDiscountInfo);
		return couponDiscountInfo;
	}//getDiscountAmount



	private StatusInfo validatePromoList(List<Coupons> listPromoCoupons) throws BaseServiceException{
		logger.debug("-------entered validatePromoList---------");
		StatusInfo statusInfo=null;
		if(listPromoCoupons == null || listPromoCoupons.size() == 0 ) {
			statusInfo = new StatusInfo("100019",PropertyUtil.getErrorMessage(100019,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validatePromoList---------");
		return statusInfo;
	}//validatePromoList



	private CouponDiscountInfo smartPromotion(CouponCodeEnquObj couponCodeEnquObj,Coupons coupObj,
			boolean isItemCode,CouponDiscountInfo couponDiscountInfo,List<CouponDiscountInfo> coupDiscInfoList,
			String disCountType, Users user, List<PurchasedItems> purchaseList, Map<String, String> posMappingMap, 
			boolean isRequestedFromNewPlugin, LoyaltyBalance balance, Double pointsBalance, ContactsLoyalty contactsLoyaltyObj) throws BaseServiceException{
		try{		
			logger.debug("couponcode=== "+couponDiscountInfo.getCOUPONCODE());
			CouponDiscountGenerateDao couponDiscountGenerateDao=(CouponDiscountGenerateDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
			String receiptAmount = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT();//!=null ? couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT().trim().isEmpty()?null:Double.parseDouble(couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT().trim()):null;

			if(isRequestedFromNewPlugin && (receiptAmount == null || receiptAmount.trim().isEmpty() || Double.parseDouble(receiptAmount.trim()) <= 0)) {
				logger.debug("==receiptAmount is null returning==");
				return null;
			}
			
			//Double finialReceiptAmount =   Double.parseDouble(receiptAmount);
			/*String reciptdDiscount = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getDISCOUNTAMOUNT();
			Double reciptDiscount = reciptdDiscount !=null && !reciptdDiscount.isEmpty() ? Double.parseDouble(reciptdDiscount) : 0;*/// couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getDISCOUNTAMOUNT().trim().isEmpty()?null:Double.parseDouble(couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getDISCOUNTAMOUNT().trim()):null;
			//On Receipt
			if(!isItemCode ) {
				//============================
				List<CouponDiscountGeneration> coupDisList = couponDiscountGenerateDao.findByCoupon(coupObj);
				if(coupDisList == null || coupDisList.isEmpty() ) return null;
				
				
				CouponDiscountGeneration MaxMPVcoupDisGenObj = null;
				Double finialReceiptAmount = 0.0;
				if(isRequestedFromNewPlugin){
					
					 finialReceiptAmount =   Double.parseDouble(receiptAmount);
					//String reciptdDiscount = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getDISCOUNTAMOUNT();
				//Double reciptDiscount = reciptdDiscount !=null && !reciptdDiscount.isEmpty() ? Double.parseDouble(reciptdDiscount) : 0;
					//finialReceiptAmount = finialReceiptAmount-reciptDiscount;
					//To get the highest priced Item without discount 
					//Map<String,Double> nonDiscountItemCodeSet_ItemPrice = new HashMap<>();
					if(purchaseList != null && purchaseList.size() > 0) {
						for (PurchasedItems tempObj : purchaseList) {
							
							
							Double totalItemPrice=null;
							
							String itemDisc = tempObj.getITEMDISCOUNT();
							
							//Check if exclude discounted items setting is on. If yes, then look for items with discounted value in request.
							if(itemDisc == null || itemDisc.isEmpty() || Double.parseDouble(itemDisc)<=0 ) continue;
							String itemQty =  tempObj.getQUANTITY() ;
							String itemPrice = tempObj.getITEMPRICE();
							
							Double itemDiscount =  Double.parseDouble(itemDisc);
							int itemQuantity = itemQty !=null && !itemQty.trim().isEmpty() ? Integer.parseInt(itemQty):0;
							Double itemPriceDbl = itemPrice !=null && !itemPrice.trim().isEmpty() ? Double.parseDouble(itemPrice):0;
							
							
							
							//totalItemPrice = (itemPriceDbl-itemDiscount)*itemQuantity;
							totalItemPrice = (itemPriceDbl)*itemQuantity;
							//Receipt final amount 
							if(coupObj.isExcludeItems() != null && coupObj.isExcludeItems())finialReceiptAmount = finialReceiptAmount - totalItemPrice;
							//else finialReceiptAmount = finialReceiptAmount-itemDiscount;
							
							
						}//for
						
					} //for
					
					Long MPV = 0l;
				
				for (CouponDiscountGeneration coupDisGenObj : coupDisList) {


					/** if Minimum Purchase Amount(MPV) is present and ReceiptAmount < Minimum Purchase Amount
					 *  then
					 *  Send Failure Response
					 */
					logger.debug("understanding the discount order"+" finialReceiptAmount "
							+ "="+finialReceiptAmount+" MPV==="+MPV+" ,TPA==="+coupDisGenObj.getTotPurchaseAmount()+",  ");
					String programID = coupDisGenObj.getProgram() != null ? coupDisGenObj.getProgram().toString() : Constants.STRING_NILL;
					String tierID = coupDisGenObj.getTierNum() != null ? coupDisGenObj.getTierNum().toString() : Constants.STRING_NILL;
					String cardSetId = coupDisGenObj.getCardSetNum() != null ? coupDisGenObj.getCardSetNum().toString() : Constants.STRING_NILL;
					if((programID != null && !programID.isEmpty() &&( (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramId().toString().equals(programID)) || (programID != null && contactsLoyaltyObj == null)))
							|| (tierID !=null && !tierID.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramTierId().toString().equals(tierID))) ||
							(cardSetId != null && !cardSetId.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getCardSetId().toString().equals(cardSetId))) ){
						logger.debug("elegibility on tier + cardset are failed");
						continue;
					}
					if(coupDisGenObj.getTotPurchaseAmount() == null || coupDisGenObj.getTotPurchaseAmount()<=0) continue;
					if(MPV == 0 ) {
						MPV = coupDisGenObj.getTotPurchaseAmount();
					}
					if(MaxMPVcoupDisGenObj == null) MaxMPVcoupDisGenObj = coupDisGenObj;

					//if( coupDisGenObj.getTotPurchaseAmount()>MPV) MPV = coupDisGenObj.getTotPurchaseAmount();

					if(coupDisGenObj.getTotPurchaseAmount() > MaxMPVcoupDisGenObj.getTotPurchaseAmount()   &&
							finialReceiptAmount>= coupDisGenObj.getTotPurchaseAmount()) {
						MaxMPVcoupDisGenObj = coupDisGenObj;
						MPV = coupDisGenObj.getTotPurchaseAmount();
					}



				}
				
				//logger.debug("MaxMPVcoupDisGenObj ===="+MaxMPVcoupDisGenObj.getDiscount() );

				if(MPV != 0 && MPV > finialReceiptAmount) return null;//what about new error code???

				
				}
				
				//============================
				
				
				List<DiscountInfo> discountInfoList  = new ArrayList<DiscountInfo>();
				DiscountInfo  receiptDiscountInfo = null;
				Map<Double, DiscountInfo> isSameDisMap = new HashMap<Double, DiscountInfo>();
				List<ItemCodeInfo>	itemCodeArrObj = null;
				if(MaxMPVcoupDisGenObj != null){
					
					logger.info("entering MaxMPVcoupDisGenObj notnull block");
					
					receiptDiscountInfo = new DiscountInfo();
					receiptDiscountInfo.setVALUE(MaxMPVcoupDisGenObj.getDiscount().toString());
					receiptDiscountInfo.setVALUECODE(disCountType);
					receiptDiscountInfo.setMINPURCHASEVALUE(MaxMPVcoupDisGenObj.getTotPurchaseAmount().toString());
					if(isRequestedFromNewPlugin) {
						receiptDiscountInfo.setSHIPPINGFEE(MaxMPVcoupDisGenObj.getShippingFee() != null ? MaxMPVcoupDisGenObj.getShippingFee() :"");
						//receiptDiscountInfo.setSHIPPINGFEEFREE(MaxMPVcoupDisGenObj.getShippingFeeFree() != null ? MaxMPVcoupDisGenObj.getShippingFeeFree() :"");
						receiptDiscountInfo.setSHIPPINGFEETYPE(MaxMPVcoupDisGenObj.getShippingFeeType()  != null ? MaxMPVcoupDisGenObj.getShippingFeeType() :"");
						
						receiptDiscountInfo.setRECEIPTDISCOUNT(MaxMPVcoupDisGenObj.getDiscount().toString());
						Double maxDisc = MaxMPVcoupDisGenObj.getDiscount();
						if(coupObj.getDiscountType().equals("Value") ) {

							receiptDiscountInfo.setMAXRECEIPTDISCOUNT(maxDisc+ Constants.STRING_NILL);

						}else if(coupObj.getDiscountType().equals("Percentage") ) {
							//remainderDiscount = maxDiscount - discountedAmountOfReceipt;
							logger.info("entering MAXRECEIPTDISCOUNT block");
							maxDisc = (finialReceiptAmount/100)*maxDisc;
							logger.info("maxDisc value is "+maxDisc);

							//max cap in dc
							if(MaxMPVcoupDisGenObj.getMaxDiscount()!=null ) {
								Double maxcap= MaxMPVcoupDisGenObj.getMaxDiscount()!=null && MaxMPVcoupDisGenObj.getMaxDiscount()>0 ? MaxMPVcoupDisGenObj.getMaxDiscount() : maxDisc ;
								maxDisc =  maxDisc>maxcap ? maxcap : maxDisc;
								logger.info("maxcap value is "+maxcap);
							}
							logger.info("final maxDisc value is"+maxDisc);
						}
						receiptDiscountInfo.setMAXRECEIPTDISCOUNT(maxDisc+ Constants.STRING_NILL);
						
					}
					itemCodeArrObj = new ArrayList<ItemCodeInfo>();
					
					receiptDiscountInfo.setITEMCODEINFO(itemCodeArrObj);
					discountInfoList.add(receiptDiscountInfo);

				}else{
					
					logger.info("entering MaxMPVcoupDisGenObj else block");

					for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
						String programID = coupDisGenObj.getProgram() != null ? coupDisGenObj.getProgram().toString() : Constants.STRING_NILL;
						String tierID = coupDisGenObj.getTierNum() != null ? coupDisGenObj.getTierNum().toString() : Constants.STRING_NILL;
						String cardSetId = coupDisGenObj.getCardSetNum() != null ? coupDisGenObj.getCardSetNum().toString() : Constants.STRING_NILL;
						if((programID != null && !programID.isEmpty() &&( (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramId().toString().equals(programID)) || (programID != null && contactsLoyaltyObj == null)))
								|| (tierID !=null && !tierID.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramTierId().toString().equals(tierID))) ||
								(cardSetId != null && !cardSetId.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getCardSetId().toString().equals(cardSetId))) ){
							logger.debug("elegibility on tier + cardset are failed");
							continue;
						}
						if(!(isSameDisMap.containsKey(coupDisGenObj.getDiscount()))) {
							receiptDiscountInfo = new DiscountInfo();
							receiptDiscountInfo.setVALUE(coupDisGenObj.getDiscount().toString());
							receiptDiscountInfo.setVALUECODE(disCountType);
							receiptDiscountInfo.setMINPURCHASEVALUE(coupDisGenObj.getTotPurchaseAmount().toString());
							if(isRequestedFromNewPlugin) {
								receiptDiscountInfo.setSHIPPINGFEE(coupDisGenObj.getShippingFee() != null ? MaxMPVcoupDisGenObj.getShippingFee() :"");
								//receiptDiscountInfo.setSHIPPINGFEEFREE(coupDisGenObj.getShippingFeeFree() != null ? MaxMPVcoupDisGenObj.getShippingFeeFree() :"");
								receiptDiscountInfo.setSHIPPINGFEETYPE(coupDisGenObj.getShippingFeeType()  != null ? MaxMPVcoupDisGenObj.getShippingFeeType() :"");
								
								
								receiptDiscountInfo.setRECEIPTDISCOUNT(coupDisGenObj.getDiscount().toString());

									
								Double maxDisc = coupDisGenObj.getDiscount();
								if(coupObj.getDiscountType().equals("Value") ) {

									receiptDiscountInfo.setMAXRECEIPTDISCOUNT(maxDisc+ Constants.STRING_NILL);

								}else if(coupObj.getDiscountType().equals("Percentage") ) {
									//remainderDiscount = maxDiscount - discountedAmountOfReceipt;
									logger.info("entering MAXRECEIPTDISCOUNT block");
									maxDisc = (finialReceiptAmount/100)*maxDisc;
									logger.info("maxDisc value is "+maxDisc);

									//max cap in dc
									if(coupDisGenObj.getMaxDiscount()!=null ) {
										Double maxcap= coupDisGenObj.getMaxDiscount()!=null && coupDisGenObj.getMaxDiscount()>0 ? coupDisGenObj.getMaxDiscount() : maxDisc ;
										maxDisc =  maxDisc>maxcap ? maxcap : maxDisc;
										logger.info("maxcap value is "+maxcap);
									}
									logger.info("final maxDisc value is"+maxDisc);
								}
								receiptDiscountInfo.setMAXRECEIPTDISCOUNT(maxDisc+ Constants.STRING_NILL);
									
							}
							itemCodeArrObj = new ArrayList<ItemCodeInfo>();
							receiptDiscountInfo.setITEMCODEINFO(itemCodeArrObj);
							isSameDisMap.put(coupDisGenObj.getDiscount(), receiptDiscountInfo);
							
						}else  {
							
							receiptDiscountInfo = isSameDisMap.get(coupDisGenObj.getDiscount());
						}
						
					}
					Set<Double> keySet = isSameDisMap.keySet();
					for (Double uniqDiscount : keySet) {
						discountInfoList.add(isSameDisMap.get(uniqDiscount));
					}
				}
				if(discountInfoList.isEmpty()) return null;
				couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
				//APP-3667 - multiplier in discount
				if(coupObj.getMultiplierValue()!=null) {
					
					boolean isEligibleWithMP = false;
					int requiredPoints = 0;
					if(coupObj.getDiscountType()!=null && coupObj.getDiscountType().equals("Percentage")) {
						double discAmnt = (finialReceiptAmount/100)*(Double.parseDouble(receiptDiscountInfo.getVALUE()));
						requiredPoints = (int)(discAmnt*coupObj.getMultiplierValue());
					}else if(coupObj.getDiscountType()!=null && coupObj.getDiscountType().equals("Value")) {
						requiredPoints = (int)(Double.parseDouble(receiptDiscountInfo.getVALUE())*coupObj.getMultiplierValue());
					}
					logger.info("requiredPoints 1>>>"+requiredPoints);
					
					if(coupObj.getValueCode() != null &&
							!coupObj.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
						if(balance != null && balance.getBalance()>=requiredPoints)  isEligibleWithMP = true;
						
						
					}else if((coupObj.getValueCode() == null || coupObj.getValueCode().equals(OCConstants.LOYALTY_POINTS)) &&
							contactsLoyaltyObj.getLoyaltyBalance() != null && contactsLoyaltyObj.getLoyaltyBalance() >= requiredPoints) {
						
						isEligibleWithMP = true;
					}
					if(isEligibleWithMP) couponDiscountInfo.setLOYALTYPOINTS(requiredPoints>0?requiredPoints+"":"");
					else return null;
				}
				
			}

			//SKU On Product
			else if(isItemCode ) {

				if(purchaseList == null || purchaseList.isEmpty()) return null;
				String itemCodeStr = Constants.STRING_NILL;
				List<PurchasedItems> ItemsList = new ArrayList<PurchasedItems>();
				//PurchasedItems highestPriced = null;
				for (PurchasedItems tempObj : purchaseList) {

					//Double totalItemPrice=null;

					Double itemDiscount = tempObj.getITEMDISCOUNT()!=null ? tempObj.getITEMDISCOUNT().trim().isEmpty()?null:Double.parseDouble(tempObj.getITEMDISCOUNT()):null;
					//int itemQuantity = tempObj.getQUANTITY()!=null ? tempObj.getQUANTITY().trim().isEmpty()?null:Integer.parseInt(tempObj.getQUANTITY()):null;
					//Double itemPrice = tempObj.getITEMPRICE()!=null &&  !tempObj.getITEMPRICE().trim().isEmpty()? Double.parseDouble(tempObj.getITEMPRICE()) : 0;

					//itemCode_PurchasedItemsMap.put(tempObj.getITEMCODE(),tempObj);

					if(coupObj.isExcludeItems() != null && coupObj.isExcludeItems()) {
						if(itemDiscount!=null && itemDiscount>0){
							//DiscountItem
							//discountItemCodeSet.add(tempObj.getITEMCODE());
							continue;
						}

					}
					ItemsList.add(tempObj);
					//if(itemCodeStr.trim().length() > 0) itemCodeStr += Constants.DELIMETER_COMMA;
					//itemCodeStr += tempObj.getITEMCODE();

					if(itemCodeStr.trim().length() > 0) itemCodeStr += Constants.DELIMETER_COMMA;
					itemCodeStr += "'"+tempObj.getITEMCODE()+"'";

					
				} //for
				
				logger.info("itemCodeStr :"+itemCodeStr);
				if(itemCodeStr.isEmpty()) return null;
				List<CouponDiscountGeneration> coupDisList = null;
				if(coupObj.isCombineItemAttributes()) { //change it to coupon
					List<Object[]> retList = couponDiscountGenerateDao.findDistinctAttrCombos(coupObj.getCouponId());
					if(retList == null || retList.isEmpty())return null; 
					if(retList != null && !retList.isEmpty()) {
						String comboQuery = Constants.STRING_NILL;
						Double discount = null;
						String shippingFee = null;
						//String shippingFeeFree = null;
						String shippingFeeType = null;
						Double maxDiscount = null;
						String quantity = null;
						String programID = null;
						String tierID = null;
						String cardSetId = null;
						String quantityCriteria = null;
						Double MPV = null;
						String noOfElegibleItems = null;
						Double ItemPrice = null;
						String ItemPriceCriteria = null;
						
						List<ItemAttribute> itemAttrLst = new ArrayList<ItemAttribute>();
						for (Object[] objects : retList) {
							
							String skuAttribute = (String)objects[0];
							String skuAttributeValue = (String)objects[1];
							
							if(discount == null)discount = objects[2] != null ? (Double)objects[2] : null;
							if(shippingFee == null) shippingFee = objects[10] != null ? objects[10].toString() : null;
							if(shippingFeeType == null) shippingFeeType = objects[11] != null ? objects[11].toString() : null;
							//if(shippingFeeFree == null) shippingFeeFree = objects[12] != null ? objects[12].toString() : null;
							if(maxDiscount == null)maxDiscount = objects[3] != null ? (Double)objects[3] : null;
							if(programID == null)  programID = objects[12] != null ? objects[12].toString() : null;
							if(tierID == null)  tierID = objects[13] != null ? objects[13].toString() : null;
							if(cardSetId == null)  cardSetId = objects[14] != null ? objects[14].toString() : null;
							
							
							if(quantity == null)quantity = (String)objects[4];
							if(quantityCriteria == null)quantityCriteria = (String)objects[5];
							if(noOfElegibleItems == null) noOfElegibleItems = (String)objects[6];
							if(ItemPrice == null) ItemPrice = objects[7] != null ? (Double)objects[7] : null;
							if(ItemPriceCriteria == null) ItemPriceCriteria = (String)objects[8] ;
							if(MPV == null ) MPV = objects[9] != null ? ((Long)objects[9]).doubleValue() : null;
							
								
							if(skuAttributeValue != null && !skuAttributeValue.isEmpty() ) {
								
								comboQuery += " AND "+( Utility.CDGAttrToSKUMap.containsKey(skuAttribute) ? Utility.CDGAttrToSKUMap.get(skuAttribute) : skuAttribute )+" = '"+skuAttributeValue+"'";
							}
							
							ItemAttribute itemAttr = new ItemAttribute();
							itemAttr.setITEMATTRIBUTE(posMappingMap.get(skuAttribute));
							itemAttr.setATTRIBUTECODE(skuAttributeValue);
							itemAttrLst.add(itemAttr);
						}
						
						if(MPV != null && MPV >=0 && receiptAmount !=null && !receiptAmount.isEmpty() && Double.parseDouble(receiptAmount) < MPV  ){
							logger.debug("MPV wasnt matchs");
							return null;
						}
						if((programID != null && !programID.isEmpty() &&( (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramId().toString().equals(programID)) || (programID != null && contactsLoyaltyObj == null)))
								|| (tierID !=null && !tierID.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramTierId().toString().equals(tierID))) ||
								(cardSetId != null && !cardSetId.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getCardSetId().toString().equals(cardSetId))) ){
							logger.debug("elegibility on tier + cardset are failed");
							return null;
						}
						List<SkuFile> itemsUnderDiscount = couponDiscountGenerateDao.findDiscountedItems(itemCodeStr, user.getUserId(), comboQuery);
						if(itemsUnderDiscount == null || itemsUnderDiscount.isEmpty()) {
							
						
							
							return null;
						}
						//coupDisList = couponDiscountGenerateDao.findDiscountsBy(coupObj.getCouponId()+Constants.STRING_NILL, itemCodeStr, user.getUserId());
						if(itemsUnderDiscount != null && !itemsUnderDiscount.isEmpty()){
							List<DiscountInfo> discountInfoList = new ArrayList<DiscountInfo>();
							List<ItemCodeInfo> itemsForThisDiscount = new ArrayList<ItemCodeInfo>();
							List<PurchasedItems> matchingItems = new ArrayList<PurchasedItems>();
							String itemQuantity = Constants.ITEM_QUANTITY_ALL;
							for (SkuFile skuFile : itemsUnderDiscount) {
								
								for (PurchasedItems purchasedItems : ItemsList) {
									if(skuFile.getItemSid().equalsIgnoreCase(purchasedItems.getITEMCODE())){
										if(purchasedItems.getITEMPRICE() != null && !purchasedItems.getITEMPRICE().isEmpty() && ItemPrice != null && ItemPrice > 0  && ItemPriceCriteria != null){
											
											Double receiptItemPrice = Double.parseDouble(purchasedItems.getITEMPRICE());
											boolean isItemPrice = false;
											if(ItemPriceCriteria.equals(">=")){
												isItemPrice = receiptItemPrice >= ItemPrice;
											}else if(ItemPriceCriteria.equals("<=")){
												isItemPrice = receiptItemPrice <= ItemPrice;
											}
											
											if(!isItemPrice) continue;
										}
										matchingItems.add(purchasedItems);
									}
								}
							}
							
							List<PurchasedItems> finalItemsList = new ArrayList<PurchasedItems>();
							PurchasedItems highestPriceItem = null;
							PurchasedItems highestPriceItemWithDiscount = null;
							PurchasedItems lowestPriceItem = null;
							PurchasedItems lowestPriceItemWithDiscount = null;
							double lowestPriceItemWithDiscountPrice = 0.0;
							double highestPriceItemWithDiscountPrice = 0.0;
							if(coupObj.getNoOfEligibleItems() != null && noOfElegibleItems == null) noOfElegibleItems=coupObj.getNoOfEligibleItems();
							if(isRequestedFromNewPlugin && noOfElegibleItems!=null) {

								if(noOfElegibleItems.equals(Constants.ALL_ELIGIBLE_ITEMS)) {
									finalItemsList = matchingItems;

								}else if( noOfElegibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT)) {
									for (PurchasedItems tempObj : matchingItems) {
										if(highestPriceItem == null) highestPriceItem = tempObj;

										Double highestPricedItem = Double.parseDouble(highestPriceItem.getITEMPRICE()); 
										Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

										if( presentItemPrice > highestPricedItem ) highestPriceItem = tempObj;
									}
									itemQuantity = Constants.ITEM_QUANTITY_ONE;
									finalItemsList.add(highestPriceItem);
								}else if(noOfElegibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT)){

									for (PurchasedItems tempObj : matchingItems) {
										if(highestPriceItemWithDiscount == null) {
											highestPriceItemWithDiscount = tempObj;
											
											String itemPr = highestPriceItemWithDiscount.getITEMPRICE();
											String itemDis = highestPriceItemWithDiscount.getITEMDISCOUNT();
											
											Double itemPrice = itemPr != null && !itemPr.trim().isEmpty() ? Double.parseDouble(itemPr):0.0;
											Double itemDiscountDbl = itemDis != null && !itemDis.trim().isEmpty() ? Double.parseDouble(itemDis):0.0;
											
											Double highestItemPrice = itemPrice; 
											highestPriceItemWithDiscountPrice = highestItemPrice + itemDiscountDbl;
											
											//Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
											//highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
										}

										
										String itemPr1 = tempObj.getITEMPRICE();
										String itemDis1 = tempObj.getITEMDISCOUNT();
										
										Double itemPrice1 = itemPr1 != null && !itemPr1.trim().isEmpty() ? Double.parseDouble(itemPr1):0.0;
										Double itemDiscountDbl1 = itemDis1 != null && !itemDis1.trim().isEmpty() ? Double.parseDouble(itemDis1):0.0;
										
										Double presentItemPrice =  itemPrice1; 

										// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
										Double presentItemdiscount =  itemDiscountDbl1; 

										
			/*							Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

										// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
										Double presentItemdiscount =  Double.parseDouble(tempObj.getITEMDISCOUNT()); 
			*/
										presentItemPrice += presentItemdiscount;

										if(presentItemPrice > highestPriceItemWithDiscountPrice ) {
											highestPriceItemWithDiscount = tempObj;
										
											String itemPr2 = highestPriceItemWithDiscount.getITEMPRICE();
											String itemDis2 = highestPriceItemWithDiscount.getITEMDISCOUNT();
											
											
											Double itemPrice2 = itemPr2 != null && !itemPr2.trim().isEmpty() ? Double.parseDouble(itemPr2):0.0;
											Double itemDiscountDbl2 = itemDis2 != null && !itemDis2.trim().isEmpty() ? Double.parseDouble(itemDis2):0.0;
											
											
											Double highestItemPrice =  itemPrice2;
											highestPriceItemWithDiscountPrice = highestItemPrice + itemDiscountDbl2;
											
											/*Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
											highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());*/
										}
									}
									itemQuantity = Constants.ITEM_QUANTITY_ONE;
									logger.info("highestPriceItemWithDiscount ::"+highestPriceItemWithDiscount!=null?highestPriceItemWithDiscount.getITEMCODE():Constants.STRING_NILL);
									finalItemsList.add(highestPriceItemWithDiscount);
								}else if( noOfElegibleItems.equals(Constants.LOWEST_PRICED_ITEMS_WITH_OUT_DISCOUNT)) {
									for (PurchasedItems tempObj : matchingItems) {
										if(lowestPriceItem == null) lowestPriceItem = tempObj;

										Double lowestPricedItem = Double.parseDouble(lowestPriceItem.getITEMPRICE()); 
										Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

										if( presentItemPrice < lowestPricedItem ) lowestPriceItem = tempObj;
									}
									itemQuantity = Constants.ITEM_QUANTITY_ONE;
									finalItemsList.add(lowestPriceItem);
								}else if( noOfElegibleItems.equals(Constants.LOWEST_PRICED_ITEMS_WITH_DISCOUNT)) {

									for (PurchasedItems tempObj : matchingItems) {
										if(lowestPriceItemWithDiscount == null) {
											lowestPriceItemWithDiscount = tempObj;
											
											String itemPr = lowestPriceItemWithDiscount.getITEMPRICE();
											String itemDis = lowestPriceItemWithDiscount.getITEMDISCOUNT();
											
											Double itemPrice = itemPr != null && !itemPr.trim().isEmpty() ? Double.parseDouble(itemPr):0.0;
											Double itemDiscountDbl = itemDis != null && !itemDis.trim().isEmpty() ? Double.parseDouble(itemDis):0.0;
											
											Double lowestItemPrice = itemPrice; 
											lowestPriceItemWithDiscountPrice = lowestItemPrice + itemDiscountDbl;
											
											//Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
											//highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
										}

										
										String itemPr1 = tempObj.getITEMPRICE();
										String itemDis1 = tempObj.getITEMDISCOUNT();
										
										Double itemPrice1 = itemPr1 != null && !itemPr1.trim().isEmpty() ? Double.parseDouble(itemPr1):0.0;
										Double itemDiscountDbl1 = itemDis1 != null && !itemDis1.trim().isEmpty() ? Double.parseDouble(itemDis1):0.0;
										
										Double presentItemPrice =  itemPrice1; 

										// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
										Double presentItemdiscount =  itemDiscountDbl1; 

										
			/*							Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

										// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
										Double presentItemdiscount =  Double.parseDouble(tempObj.getITEMDISCOUNT()); 
			*/
										presentItemPrice += presentItemdiscount;

										if(presentItemPrice < lowestPriceItemWithDiscountPrice ) {
											lowestPriceItemWithDiscount = tempObj;
										
											String itemPr2 = lowestPriceItemWithDiscount.getITEMPRICE();
											String itemDis2 = lowestPriceItemWithDiscount.getITEMDISCOUNT();
											
											
											Double itemPrice2 = itemPr2 != null && !itemPr2.trim().isEmpty() ? Double.parseDouble(itemPr2):0.0;
											Double itemDiscountDbl2 = itemDis2 != null && !itemDis2.trim().isEmpty() ? Double.parseDouble(itemDis2):0.0;
											
											
											Double lowestItemPrice =  itemPrice2;
											lowestPriceItemWithDiscountPrice = lowestItemPrice + itemDiscountDbl2;
											
											/*Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
											highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());*/
										}
									}
									itemQuantity = Constants.ITEM_QUANTITY_ONE;
									logger.info("highestPriceItemWithDiscount ::"+lowestPriceItemWithDiscount!=null?lowestPriceItemWithDiscount.getITEMCODE():Constants.STRING_NILL);
									finalItemsList.add(lowestPriceItemWithDiscount);
								}

							}else {

								finalItemsList = matchingItems;

							}
							if(finalItemsList == null || finalItemsList.isEmpty()){
								logger.debug("===nothing to match with it===");
								return null;
							}

							
							DiscountInfo discountInfo = new DiscountInfo();
							discountInfo.setVALUE(discount != null ? discount+ Constants.STRING_NILL : Constants.STRING_NILL);
							discountInfo.setVALUECODE(disCountType);
							discountInfo.setMINPURCHASEVALUE(MPV != null ? MPV+Constants.STRING_NILL : Constants.STRING_NILL);
							if(isRequestedFromNewPlugin) {
								discountInfo.setSHIPPINGFEE(shippingFee != null ? shippingFee :"");
								//discountInfo.setSHIPPINGFEEFREE(shippingFeeFree != null ? shippingFeeFree :"");
								discountInfo.setSHIPPINGFEETYPE(shippingFeeType != null ? shippingFeeType :"");
								
								discountInfo.setMAXRECEIPTDISCOUNT(Constants.STRING_NILL);
								discountInfo.setRECEIPTDISCOUNT(Constants.STRING_NILL);
								discountInfo.setITEMPRICE(ItemPrice != null ? ItemPrice+Constants.STRING_NILL : Constants.STRING_NILL);
								discountInfo.setELIGIBILITY(noOfElegibleItems  != null ? noOfElegibleItems:Constants.STRING_NILL );
							}
							//removed change in request
							/*if(isRequestedFromNewPlugin && quantity != null) {
								List<PurchasedItems> finalItemsQtyList = new ArrayList<PurchasedItems>();
								for (PurchasedItems purchasedItem : finalItemsList) {
									
									double Quantity = purchasedItem.getQUANTITY()!=null ? purchasedItem.getQUANTITY().trim().isEmpty()?null:Double.parseDouble(purchasedItem.getQUANTITY()):null;
									if(Quantity >= Double.parseDouble(quantity))finalItemsQtyList.add(purchasedItem);
									
								}
								finalItemsList = finalItemsQtyList;
								if(finalItemsList.isEmpty()) return null;
								
							}*/
							for (PurchasedItems purchasedItem : finalItemsList) {
								ItemCodeInfo itemcodeInfo = new ItemCodeInfo();
								itemcodeInfo.setITEMCODE(purchasedItem.getITEMCODE());
								if(isRequestedFromNewPlugin) {
									String itemPrice = purchasedItem.getITEMPRICE() ;
									

									Double itemPriceDbl = itemPrice != null && !itemPrice.trim().isEmpty() ? Double.parseDouble(itemPrice):0.0;

									//Double itemOrigPriceDbl = itemPriceDbl + itemDiscountDbl;
									
									
									
									if(coupObj.getDiscountType().equals("Value") ) {

										maxDiscount = discount;

									}else if(coupObj.getDiscountType().equals("Percentage") ) {

										maxDiscount = (itemPriceDbl/100)*discount;

									}
									if(quantity != null){
										
										String discountedQty = getDiscountQty(quantity, purchasedItem.getQUANTITY(), quantityCriteria, balance, coupObj.getRequiredLoyltyPoits(), pointsBalance);
										if(discountedQty.isEmpty() || discountedQty.equals("0")) continue;
										else {
											itemcodeInfo.setITEMDISCOUNT(discount == null ? Constants.STRING_NILL : discount+Constants.STRING_NILL);
											itemcodeInfo.setMAXITEMDISCOUNT(maxDiscount == null ? Constants.STRING_NILL : maxDiscount+ Constants.STRING_NILL);
											//if(quantity != null)itemcodeInfo.setQUANTITY(discountedQty);
											itemcodeInfo.setQUANTITY(discountedQty == null ? Constants.STRING_NILL : discountedQty );
											itemcodeInfo.setQUANTITYCRITERIA(quantityCriteria == null ? Constants.STRING_NILL : Utility.limitQuantityMap.get(quantityCriteria) );
											String rewardRatio = "";
											logger.debug("quantity=="+discountedQty);
											if(quantity != null && !quantity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
												rewardRatio = quantity+"-"+coupObj.getRequiredLoyltyPoits(); 
											}
											logger.debug("rewardRatio=="+rewardRatio);
											itemcodeInfo.setREWARDRATIO(rewardRatio);
											itemcodeInfo.setITEMATTRIBUTE(itemAttrLst);
										}
									}else{
										if(quantity == null) quantity =  purchasedItem.getQUANTITY();
										itemcodeInfo.setITEMCODE(purchasedItem.getITEMCODE());
										itemcodeInfo.setITEMDISCOUNT(discount == null ? Constants.STRING_NILL : discount+Constants.STRING_NILL);
										itemcodeInfo.setMAXITEMDISCOUNT(maxDiscount == null ? Constants.STRING_NILL : maxDiscount+ Constants.STRING_NILL);
										itemcodeInfo.setQUANTITY(quantity == null ? Constants.STRING_NILL : quantity );
										itemcodeInfo.setQUANTITYCRITERIA(quantityCriteria == null ? Constants.STRING_NILL : Utility.limitQuantityMap.get(quantityCriteria) );
										String rewardRatio = "";
										/*if(quantity != null && !quantity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
											rewardRatio = quantity+"-"+coupObj.getRequiredLoyltyPoits(); 
										}*/
										logger.debug("quantity=="+quantity);
										/*if(quantity != null && !quantity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
											rewardRatio = quantity+"-"+coupObj.getRequiredLoyltyPoits(); 
										}*/
										logger.debug("rewardRatio=="+rewardRatio);
										itemcodeInfo.setREWARDRATIO(rewardRatio);
										itemcodeInfo.setITEMATTRIBUTE(itemAttrLst);
									}
									
									
								}
								itemsForThisDiscount.add(itemcodeInfo);
								
							}
							logger.debug("itemsForThisDiscount===="+itemsForThisDiscount.size());
							if(!itemsForThisDiscount.isEmpty()) {
								
								discountInfo.setITEMCODEINFO(itemsForThisDiscount);
								discountInfoList.add(discountInfo);
							}else{
								return null;
							}
							if(discountInfoList.isEmpty()) return null;
							couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
						}
						
						
					}
				}else{
					
					 coupDisList = couponDiscountGenerateDao.findDiscountsBy(coupObj.getCouponId()+Constants.STRING_NILL, itemCodeStr, user.getUserId());
				
					//get udf based dicounts if any
						List<String> retUDFList = couponDiscountGenerateDao.findDistinctUDFAttr(coupObj.getCouponId());
						if(retUDFList != null && !retUDFList.isEmpty()) {
							
							for (String CDGAttr : retUDFList) {
								
								List<CouponDiscountGeneration> tempcoupDisList = couponDiscountGenerateDao.findBy(coupObj.getCouponId(), user.getUserId(), 
										CDGAttr, CDGAttr);
								if(tempcoupDisList != null && !tempcoupDisList.isEmpty()) {
									
									coupDisList.addAll(tempcoupDisList);
								}
								
							}//for
						}
						
						//List<CouponDiscountGeneration> coupDisList = couponDiscountGenerateDao.findDiscountsBy(coupObj.getCouponId()+Constants.STRING_NILL, itemCodeStr, user.getUserId());
						
						
						logger.info("coupDisList :"+coupDisList!=null?coupDisList.size():"null");
						if(coupDisList == null || coupDisList.isEmpty() ) return null;
						List<CouponDiscountGeneration> eligibleDiscList  = new ArrayList<CouponDiscountGeneration>();
						for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
							
							String programID = coupDisGenObj.getProgram() != null ? coupDisGenObj.getProgram().toString() : Constants.STRING_NILL;
							String tierID = coupDisGenObj.getTierNum() != null ? coupDisGenObj.getTierNum().toString() : Constants.STRING_NILL;
							String cardSetId = coupDisGenObj.getCardSetNum() != null ? coupDisGenObj.getCardSetNum().toString() : Constants.STRING_NILL;
							if((!programID.isEmpty() &&( (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramId().toString().equals(programID)) || (programID != null && contactsLoyaltyObj == null)))
									|| (!tierID.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramTierId().toString().equals(tierID))) ||
									(!cardSetId.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getCardSetId().toString().equals(cardSetId))) ){
								logger.debug("elegibility on tier + cardset are failed");
								continue;
							}
							eligibleDiscList.add(coupDisGenObj);
							
						}
						if(eligibleDiscList.size() == 0 ) return null;
						coupDisList = new ArrayList<CouponDiscountGeneration>(eligibleDiscList); 
						
						List<PurchasedItems> matchingItems = new ArrayList<PurchasedItems>();
						//remove the items from the discountlist
						List<CouponDiscountGeneration> finalDiscList = new ArrayList<CouponDiscountGeneration>();
						
						for (PurchasedItems purchasedItems : ItemsList) {
							boolean isMatched = false;
							for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
								if(!purchasedItems.getITEMCODE().equalsIgnoreCase(coupDisGenObj.getItemCategory())) {
									logger.debug("purchasedItems=="+purchasedItems.getITEMCODE()+" coupDisGenObj.getItemCategory()=="+coupDisGenObj.getItemCategory());
									continue;
								}
								isMatched = true;
								//finalDiscList.add(coupDisGenObj);
							}
							if(isMatched)matchingItems.add(purchasedItems);
						}
						if(matchingItems == null || matchingItems.isEmpty()) {
							
							logger.debug("nothing to match==");
							return null;
						}
						
						/**
						 * distinctDisc holds Discount value
						 */ 
						Set<Double> distinctDisc = new HashSet<Double>();
						for (CouponDiscountGeneration couponDiscountGeneration : coupDisList) {
							distinctDisc.add(couponDiscountGeneration.getDiscount());
						}
						List<DiscountInfo> discountInfoList = new ArrayList<DiscountInfo>();
						List<PurchasedItems> finalItemsList = new ArrayList<PurchasedItems>();
						PurchasedItems highestPriceItem = null;
						PurchasedItems highestPriceItemWithDiscount = null;
						double highestPriceItemWithDiscountPrice = 0.0;
						PurchasedItems lowestPriceItem = null;
						PurchasedItems lowestPriceItemWithDiscount = null;
						double lowestPriceItemWithDiscountPrice = 0.0;
						String itemQuantity = Constants.ITEM_QUANTITY_ALL;
						Map<String,PurchasedItems> elegibleItemsMap = new HashMap<String, PurchasedItems>();
						if(isRequestedFromNewPlugin ) {
						elegibleItemsMap = getTheElegibilitySet(matchingItems);

						/*if(coupObj.getNoOfEligibleItems().equals(Constants.ALL_ELIGIBLE_ITEMS)) {
								finalItemsList = matchingItems;

							}else if( coupObj.getNoOfEligibleItems().equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT)) {
								for (PurchasedItems tempObj : matchingItems) {
									if(highestPriceItem == null) highestPriceItem = tempObj;

									Double highestPricedItem = Double.parseDouble(highestPriceItem.getITEMPRICE()); 
									Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

									if( presentItemPrice > highestPricedItem ) highestPriceItem = tempObj;
								}
								itemQuantity = Constants.ITEM_QUANTITY_ONE;
								finalItemsList.add(highestPriceItem);
							}else if(coupObj.getNoOfEligibleItems().equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT)){

								for (PurchasedItems tempObj : matchingItems) {
									if(highestPriceItemWithDiscount == null) {
										highestPriceItemWithDiscount = tempObj;
										
										String itemPr = highestPriceItemWithDiscount.getITEMPRICE();
										String itemDis = highestPriceItemWithDiscount.getITEMDISCOUNT();
										
										Double itemPrice = itemPr != null && !itemPr.trim().isEmpty() ? Double.parseDouble(itemPr):0.0;
										Double itemDiscountDbl = itemDis != null && !itemDis.trim().isEmpty() ? Double.parseDouble(itemDis):0.0;
										
										Double highestItemPrice = itemPrice; 
										highestPriceItemWithDiscountPrice = highestItemPrice + itemDiscountDbl;
										
										//Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
										//highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
									}

									
									String itemPr1 = tempObj.getITEMPRICE();
									String itemDis1 = tempObj.getITEMDISCOUNT();
									
									Double itemPrice1 = itemPr1 != null && !itemPr1.trim().isEmpty() ? Double.parseDouble(itemPr1):0.0;
									Double itemDiscountDbl1 = itemDis1 != null && !itemDis1.trim().isEmpty() ? Double.parseDouble(itemDis1):0.0;
									
									Double presentItemPrice =  itemPrice1; 

									// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
									Double presentItemdiscount =  itemDiscountDbl1; 

									
									Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

									// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
									Double presentItemdiscount =  Double.parseDouble(tempObj.getITEMDISCOUNT()); 
		
									presentItemPrice += presentItemdiscount;

									if(presentItemPrice > highestPriceItemWithDiscountPrice ) {
										highestPriceItemWithDiscount = tempObj;
									
										String itemPr2 = highestPriceItemWithDiscount.getITEMPRICE();
										String itemDis2 = highestPriceItemWithDiscount.getITEMDISCOUNT();
										
										
										Double itemPrice2 = itemPr2 != null && !itemPr2.trim().isEmpty() ? Double.parseDouble(itemPr2):0.0;
										Double itemDiscountDbl2 = itemDis2 != null && !itemDis2.trim().isEmpty() ? Double.parseDouble(itemDis2):0.0;
										
										
										Double highestItemPrice =  itemPrice2;
										highestPriceItemWithDiscountPrice = highestItemPrice + itemDiscountDbl2;
										
										Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
										highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
									}
								}
								itemQuantity = Constants.ITEM_QUANTITY_ONE;
								logger.info("highestPriceItemWithDiscount ::"+highestPriceItemWithDiscount!=null?highestPriceItemWithDiscount.getITEMCODE():Constants.STRING_NILL);
								finalItemsList.add(highestPriceItemWithDiscount);
							}else if( coupObj.getNoOfEligibleItems().equals(Constants.LOWEST_PRICED_ITEMS_WITH_OUT_DISCOUNT)) {
								for (PurchasedItems tempObj : matchingItems) {
									if(lowestPriceItem == null) lowestPriceItem = tempObj;

									Double lowestPricedItem = Double.parseDouble(lowestPriceItem.getITEMPRICE()); 
									Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

									if( presentItemPrice < lowestPricedItem ) lowestPriceItem = tempObj;
								}
								itemQuantity = Constants.ITEM_QUANTITY_ONE;
								finalItemsList.add(lowestPriceItem);
							}else if( coupObj.getNoOfEligibleItems().equals(Constants.LOWEST_PRICED_ITEMS_WITH_DISCOUNT)) {

								for (PurchasedItems tempObj : matchingItems) {
									if(lowestPriceItemWithDiscount == null) {
										lowestPriceItemWithDiscount = tempObj;
										
										String itemPr = lowestPriceItemWithDiscount.getITEMPRICE();
										String itemDis = lowestPriceItemWithDiscount.getITEMDISCOUNT();
										
										Double itemPrice = itemPr != null && !itemPr.trim().isEmpty() ? Double.parseDouble(itemPr):0.0;
										Double itemDiscountDbl = itemDis != null && !itemDis.trim().isEmpty() ? Double.parseDouble(itemDis):0.0;
										
										Double lowestItemPrice = itemPrice; 
										lowestPriceItemWithDiscountPrice = lowestItemPrice + itemDiscountDbl;
										
										//Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
										//highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
									}

									
									String itemPr1 = tempObj.getITEMPRICE();
									String itemDis1 = tempObj.getITEMDISCOUNT();
									
									Double itemPrice1 = itemPr1 != null && !itemPr1.trim().isEmpty() ? Double.parseDouble(itemPr1):0.0;
									Double itemDiscountDbl1 = itemDis1 != null && !itemDis1.trim().isEmpty() ? Double.parseDouble(itemDis1):0.0;
									
									Double presentItemPrice =  itemPrice1; 

									// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
									Double presentItemdiscount =  itemDiscountDbl1; 

									
									Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

									// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
									Double presentItemdiscount =  Double.parseDouble(tempObj.getITEMDISCOUNT()); 
		
									presentItemPrice += presentItemdiscount;

									if(presentItemPrice < lowestPriceItemWithDiscountPrice ) {
										lowestPriceItemWithDiscount = tempObj;
									
										String itemPr2 = lowestPriceItemWithDiscount.getITEMPRICE();
										String itemDis2 = lowestPriceItemWithDiscount.getITEMDISCOUNT();
										
										
										Double itemPrice2 = itemPr2 != null && !itemPr2.trim().isEmpty() ? Double.parseDouble(itemPr2):0.0;
										Double itemDiscountDbl2 = itemDis2 != null && !itemDis2.trim().isEmpty() ? Double.parseDouble(itemDis2):0.0;
										
										
										Double lowestItemPrice =  itemPrice2;
										lowestPriceItemWithDiscountPrice = lowestItemPrice + itemDiscountDbl2;
										
										Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
										highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
									}
								}
								itemQuantity = Constants.ITEM_QUANTITY_ONE;
								logger.info("highestPriceItemWithDiscount ::"+lowestPriceItemWithDiscount!=null?lowestPriceItemWithDiscount.getITEMCODE():Constants.STRING_NILL);
								finalItemsList.add(lowestPriceItemWithDiscount);
							}


						*/}else {


						}
						finalItemsList = matchingItems;
						if(finalItemsList == null || finalItemsList.isEmpty()){
							logger.debug("===nothing to match with it===");
							return null;
						}
						for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
							for (PurchasedItems purchasedItems : finalItemsList) {
								if(!purchasedItems.getITEMCODE().equalsIgnoreCase(coupDisGenObj.getItemCategory())) {
									logger.debug("purchasedItems=="+purchasedItems.getITEMCODE()+" coupDisGenObj.getItemCategory()=="+coupDisGenObj.getItemCategory());
									continue;
								}
								finalDiscList.add(coupDisGenObj);
							}
						}
						
						if(finalDiscList.isEmpty()) return null;
						Set<Double> retDiscList = new HashSet<Double>();
						for (CouponDiscountGeneration coupDisGenObj : finalDiscList) {
							
							
							retDiscList.add(coupDisGenObj.getDiscount());
						}
							
/*						logger.debug("finalDiscList =="+finalDiscList.size());
						Map<Double, DiscountInfo> isSameDisMap = new HashMap<Double, DiscountInfo>();
						List<PurchasedItems> finalElegibleItemsList = new ArrayList<PurchasedItems>();
						String itemQuantityStr = Constants.ITEM_QUANTITY_ALL;
						List<ItemCodeInfo> itemsForThisDiscount = null;
							for (CouponDiscountGeneration coupDisGenObj : finalDiscList) {
								
								if(!isSameDisMap.containsKey(coupDisGenObj.getDiscount())){
									itemsForThisDiscount = new ArrayList<ItemCodeInfo>();
										if(!purchasedItems.getITEMCODE().equalsIgnoreCase(coupDisGenObj.getItemCategory())) {
											logger.debug("purchasedItems=="+purchasedItems.getITEMCODE()+" coupDisGenObj.getItemCategory()=="+coupDisGenObj.getItemCategory());
											continue;
										}
										
										ItemCodeInfo itemcodeInfo = new ItemCodeInfo();
										itemcodeInfo.setITEMCODE(coupDisGenObj.getItemCategory());
										if(isRequestedFromNewPlugin) {
											List<ItemAttribute> itemAttrList = new ArrayList<ItemAttribute>();
											ItemAttribute itemAttr = new ItemAttribute();
											itemAttr.setITEMATTRIBUTE(posMappingMap.get(coupDisGenObj.getSkuAttribute()) == null ? coupDisGenObj.getSkuAttribute() :  posMappingMap.get(coupDisGenObj.getSkuAttribute()));
											itemAttr.setATTRIBUTECODE(coupDisGenObj.getSkuValue());
											itemAttrList.add(itemAttr);
											itemcodeInfo.setITEMATTRIBUTE(itemAttrList);
											//itemcodeInfo.setITEMATTRIBUTE(posMappingMap.get(coupDisGenObj.getSkuAttribute()) == null ? coupDisGenObj.getSkuAttribute() :  posMappingMap.get(coupDisGenObj.getSkuAttribute()));
											//itemcodeInfo.setATTRIBUTECODE(coupDisGenObj.getSkuValue());
											itemcodeInfo.setITEMDISCOUNT(coupDisGenObj.getDiscount()+Constants.STRING_NILL );
											itemcodeInfo.setMAXITEMDISCOUNT(coupDisGenObj.getMaxDiscount() == null ?  Constants.STRING_NILL  : coupDisGenObj.getMaxDiscount()+Constants.STRING_NILL  );
											itemcodeInfo.setQUANTITY(coupDisGenObj.getQuantity() != null && !coupDisGenObj.getQuantity().isEmpty()? coupDisGenObj.getQuantity() : itemQuantityStr);
										}

										itemsForThisDiscount.add(itemcodeInfo);
										
										
										
										
									if(itemsForThisDiscount.size() > 0) {
										
										DiscountInfo discountInfo = new DiscountInfo();
										
										discountInfo.setVALUE(coupDisGenObj.getDiscount()+Constants.STRING_NILL);
										discountInfo.setVALUECODE(disCountType);
										discountInfo.setMINPURCHASEVALUE(Constants.STRING_NILL);
										if(isRequestedFromNewPlugin) {
											
											discountInfo.setMAXRECEIPTDISCOUNT(Constants.STRING_NILL);
											discountInfo.setRECEIPTDISCOUNT(Constants.STRING_NILL);
										}
										logger.debug("itemsForThisDiscount===="+itemsForThisDiscount.size());
										discountInfo.setITEMCODEINFO(itemsForThisDiscount);
										isSameDisMap.put(coupDisGenObj.getDiscount(), discountInfo);
									}
								}else{
									DiscountInfo discountInfo = isSameDisMap.get(coupDisGenObj.getDiscount());
									itemsForThisDiscount = discountInfo.getITEMCODEINFO();
									
									ItemCodeInfo itemcodeInfo = new ItemCodeInfo();
									itemcodeInfo.setITEMCODE(coupDisGenObj.getItemCategory());
									if(isRequestedFromNewPlugin) {
										List<ItemAttribute> itemAttrList = new ArrayList<ItemAttribute>();
										ItemAttribute itemAttr = new ItemAttribute();
										itemAttr.setITEMATTRIBUTE(posMappingMap.get(coupDisGenObj.getSkuAttribute()) == null ? coupDisGenObj.getSkuAttribute() :  posMappingMap.get(coupDisGenObj.getSkuAttribute()));
										itemAttr.setATTRIBUTECODE(coupDisGenObj.getSkuValue());
										itemAttrList.add(itemAttr);
										itemcodeInfo.setITEMATTRIBUTE(itemAttrList);
										
										//itemcodeInfo.setITEMATTRIBUTE(posMappingMap.get(coupDisGenObj.getSkuAttribute()) == null ? coupDisGenObj.getSkuAttribute() :  posMappingMap.get(coupDisGenObj.getSkuAttribute()));
										//itemcodeInfo.setATTRIBUTECODE(coupDisGenObj.getSkuValue());
										itemcodeInfo.setITEMDISCOUNT(coupDisGenObj.getDiscount()+Constants.STRING_NILL );
										itemcodeInfo.setMAXITEMDISCOUNT(coupDisGenObj.getMaxDiscount() == null ?  Constants.STRING_NILL  : coupDisGenObj.getMaxDiscount()+Constants.STRING_NILL  );
										itemcodeInfo.setQUANTITY(coupDisGenObj.getQuantity() != null && !coupDisGenObj.getQuantity().isEmpty()? coupDisGenObj.getQuantity() : itemQuantityStr);
										
									}
									itemsForThisDiscount.add(itemcodeInfo);
									discountInfo.setITEMCODEINFO(itemsForThisDiscount);
									//logger.debug("no need to do anything here");
									isSameDisMap.put(coupDisGenObj.getDiscount(), discountInfo);
								}
							}//foe each discount criteria
							Set<Double> keySet = isSameDisMap.keySet();
							for (Double uniqDiscount : keySet) {
								discountInfoList.add(isSameDisMap.get(uniqDiscount));
							}
							couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
					}
*/
						
						for (PurchasedItems purchasedItems : ItemsList) {
							
						}
						for (Double discount : retDiscList) {
							logger.info("discount ::"+discount);
							//logger.info("finalCoupDisList :"+finalCoupDisList!=null ? finalCoupDisList.toArray():"NULL");
							Long MPV = null;
							String shippingFee = Constants.STRING_NILL;;
							String shippingFeeFree = Constants.STRING_NILL;;
							String shippingFeeType = Constants.STRING_NILL;;
							List<ItemCodeInfo> itemCodeInfoList = new ArrayList<ItemCodeInfo>();
							for (PurchasedItems purchasedItems : ItemsList) {
								boolean itemMatched = false;
							
								List<ItemAttribute> itemAttrList = new ArrayList<ItemAttribute>();
								String MaxDiscount = Constants.STRING_NILL;
								String quanity = Constants.STRING_NILL;
								String quanityCriteria = Constants.STRING_NILL;
								String noOfElegibleItems = null;
								Double ItemPrice = null;
								String ItemPriceCriteria = null;
								for (CouponDiscountGeneration coupDisGenObj : finalDiscList) {
									logger.info("discount :: "+discount+" coupDisGenObj ::"+coupDisGenObj.getDiscount());
									if(discount.doubleValue() != coupDisGenObj.getDiscount().doubleValue()) continue;
									if(!purchasedItems.getITEMCODE().equalsIgnoreCase(coupDisGenObj.getItemCategory())) {
										continue;
									}
									MPV = coupDisGenObj.getTotPurchaseAmount() != null ? coupDisGenObj.getTotPurchaseAmount().longValue() : null;
									if(MPV != null && MPV >=0 && receiptAmount != null && !receiptAmount.isEmpty() && Double.parseDouble(receiptAmount) < MPV  ){
										logger.debug("didnt satify the MPV");
										continue;
									}
									logger.debug("purchasedItems=="+purchasedItems.getITEMCODE()+" coupDisGenObj.getItemCategory()=="+coupDisGenObj.getItemCategory());
									shippingFee = coupDisGenObj.getShippingFee() == null ? Constants.STRING_NILL : coupDisGenObj.getShippingFee();
									////shippingFeeFree = coupDisGenObj.getShippingFeeFree() == null ? Constants.STRING_NILL : coupDisGenObj.getShippingFeeFree();
									shippingFeeType = coupDisGenObj.getShippingFeeType() == null ? Constants.STRING_NILL : coupDisGenObj.getShippingFeeType();
									MaxDiscount = coupDisGenObj.getMaxDiscount() == null ? Constants.STRING_NILL : coupDisGenObj.getMaxDiscount()+Constants.STRING_NILL;
									quanity = coupDisGenObj.getQuantity() == null ? Constants.STRING_NILL : coupDisGenObj.getQuantity();
									quanityCriteria = coupDisGenObj.getLimitQuantity() == null ? Constants.STRING_NILL : coupDisGenObj.getLimitQuantity();
									noOfElegibleItems = coupDisGenObj.getNoOfEligibleItems() == null ? Constants.STRING_NILL : coupDisGenObj.getNoOfEligibleItems();
									ItemPrice = coupDisGenObj.getItemPrice() != null ? coupDisGenObj.getItemPrice() : null;
									ItemPriceCriteria =  coupDisGenObj.getItemPriceCriteria() != null ? coupDisGenObj.getItemPriceCriteria() : Constants.STRING_NILL;
									if(purchasedItems.getITEMPRICE() != null && !purchasedItems.getITEMPRICE().isEmpty() 
											&& ItemPrice != null && ItemPrice > 0 && ItemPriceCriteria != null && !ItemPriceCriteria.isEmpty()){
										
											
										Double receiptItemPrice = Double.parseDouble(purchasedItems.getITEMPRICE());
										boolean isItemPrice = false;
										if(ItemPriceCriteria.equals(">=")){
											isItemPrice = receiptItemPrice >= ItemPrice;
										}else if(ItemPriceCriteria.equals("<=")){
											isItemPrice = receiptItemPrice <= ItemPrice;
										}
										
										if(!isItemPrice) continue;
									}
									/*if(isRequestedFromNewPlugin && quanity != null && !quanity.isEmpty()) {
											
										double Quantity = purchasedItems.getQUANTITY()!=null ? purchasedItems.getQUANTITY().trim().isEmpty()?null:Double.parseDouble(purchasedItems.getQUANTITY()):null;
										if(Quantity < Double.parseDouble(quanity)) continue;
											
										
										
									}*/
									if(noOfElegibleItems != null && 
											elegibleItemsMap.containsKey(noOfElegibleItems) && 
											!noOfElegibleItems.equalsIgnoreCase(Constants.ALL_ELIGIBLE_ITEMS) ){
										PurchasedItems eligibleItem =  elegibleItemsMap.get(noOfElegibleItems);
										if(!eligibleItem.getITEMCODE().equalsIgnoreCase(purchasedItems.getITEMCODE())){
											logger.debug("===not matched==");
											continue;
										}
									}
									
									ItemAttribute itemAttr = new ItemAttribute();
									itemAttr.setITEMATTRIBUTE(posMappingMap.get(coupDisGenObj.getSkuAttribute()) == null ? coupDisGenObj.getSkuAttribute() :  posMappingMap.get(coupDisGenObj.getSkuAttribute()));
									itemAttr.setATTRIBUTECODE(coupDisGenObj.getSkuValue());
									itemAttrList.add(itemAttr);
									itemMatched = true;
								}
								
								if(itemMatched){
									ItemCodeInfo itemcodeInfo = new ItemCodeInfo();
									itemcodeInfo.setITEMCODE(purchasedItems.getITEMCODE());
									if(isRequestedFromNewPlugin) {
										itemcodeInfo.setITEMATTRIBUTE(itemAttrList);
										String itemPrice = purchasedItems.getITEMPRICE() ;
										

										Double itemPriceDbl = itemPrice != null && !itemPrice.trim().isEmpty() ? Double.parseDouble(itemPrice):0.0;

										//Double itemOrigPriceDbl = itemPriceDbl + itemDiscountDbl;
										
										
										Double maxDiscount = null;
										if(coupObj.getDiscountType().equals("Value") ) {

											maxDiscount = discount;

										}else if(coupObj.getDiscountType().equals("Percentage") ) {

											maxDiscount = (itemPriceDbl/100)*discount;

										}
										if(quanity != null && !quanity.isEmpty()){
											
											String discountedQty = getDiscountQty(quanity, purchasedItems.getQUANTITY(), quanityCriteria, balance, coupObj.getRequiredLoyltyPoits(), pointsBalance);
											logger.debug("==get discouned qty "+discountedQty);
											if(discountedQty.isEmpty() || discountedQty.equals("0")) continue;
											else {
												itemcodeInfo.setITEMDISCOUNT(discount == null ? Constants.STRING_NILL : discount+Constants.STRING_NILL);
												itemcodeInfo.setMAXITEMDISCOUNT(maxDiscount == null ? Constants.STRING_NILL : maxDiscount+ Constants.STRING_NILL);
												//if(quantity != null)itemcodeInfo.setQUANTITY(discountedQty);
												itemcodeInfo.setQUANTITY(discountedQty == null ? Constants.STRING_NILL : discountedQty );
												itemcodeInfo.setQUANTITYCRITERIA(quanityCriteria == null ? Constants.STRING_NILL : Utility.limitQuantityMap.get(quanityCriteria) );
												String rewardRatio = "";
												logger.debug("quantity=="+discountedQty);
												if(quanity != null && !quanity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
													rewardRatio = quanity+"-"+coupObj.getRequiredLoyltyPoits(); 
												}
												logger.debug("rewardRatio=="+rewardRatio);
												itemcodeInfo.setREWARDRATIO(rewardRatio);
											}
										}else{
											if(quanity == null) quanity = purchasedItems.getQUANTITY();
											itemcodeInfo.setITEMCODE(purchasedItems.getITEMCODE());
											itemcodeInfo.setITEMDISCOUNT(discount == null ? Constants.STRING_NILL : discount+Constants.STRING_NILL);
											itemcodeInfo.setMAXITEMDISCOUNT(maxDiscount == null ? Constants.STRING_NILL : maxDiscount+ Constants.STRING_NILL);
											itemcodeInfo.setQUANTITY(quanity == null ? Constants.STRING_NILL : quanity );
											itemcodeInfo.setQUANTITYCRITERIA(quanityCriteria == null ? Constants.STRING_NILL : Utility.limitQuantityMap.get(quanityCriteria) );
											String rewardRatio = "";
											/*if( quanity != null && !quanity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
												rewardRatio = quanity+"-"+coupObj.getRequiredLoyltyPoits(); 
											}*/
											logger.debug("quantity=="+quanity);
											/*if(quantity != null && !quantity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
												rewardRatio = quantity+"-"+coupObj.getRequiredLoyltyPoits(); 
											}*/
											logger.debug("rewardRatio=="+rewardRatio);
											itemcodeInfo.setREWARDRATIO(rewardRatio);
										}
										
										/*//itemcodeInfo.setITEMATTRIBUTE(posMappingMap.get(coupDisGenObj.getSkuAttribute()) == null ? coupDisGenObj.getSkuAttribute() :  posMappingMap.get(coupDisGenObj.getSkuAttribute()));//getPOSdiaplaylabel
										//itemcodeInfo.setATTRIBUTECODE(coupDisGenObj.getSkuValue());
										itemcodeInfo.setITEMDISCOUNT(discount+Constants.STRING_NILL);
										itemcodeInfo.setMAXITEMDISCOUNT(maxDiscount+Constants.STRING_NILL);
										itemcodeInfo.setQUANTITY(quanity);
										itemcodeInfo.setQUANTITYCRITERIA(Utility.limitQuantityMap.get(quanityCriteria));
										String rewardRatio = "";
										logger.debug("quantity=="+quanity);
										if(quanity != null && !quanity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
											rewardRatio = quanity+"-"+coupObj.getRequiredLoyltyPoits(); 
										}
										logger.debug("rewardRatio=="+rewardRatio);
										itemcodeInfo.setREWARDRATIO(rewardRatio);*/
										
									}

									itemCodeInfoList.add(itemcodeInfo);
								}
							}
							if(!itemCodeInfoList.isEmpty()) {


								DiscountInfo discountInfo = new DiscountInfo();
								discountInfo.setMINPURCHASEVALUE(MPV != null ? MPV +Constants.STRING_NILL: Constants.STRING_NILL);
								discountInfo.setVALUE(discount.toString());
								discountInfo.setVALUECODE(disCountType);
								if(isRequestedFromNewPlugin) {
									
									discountInfo.setSHIPPINGFEE(shippingFee);
									//discountInfo.setSHIPPINGFEEFREE(shippingFeeFree);
									discountInfo.setSHIPPINGFEETYPE(shippingFeeType);;
									discountInfo.setMAXRECEIPTDISCOUNT(Constants.STRING_NILL);
									discountInfo.setRECEIPTDISCOUNT(Constants.STRING_NILL);
									
								}

								//discountInfo.setEXCLUDEDISCOUNTEDITEMS(coupObj.isExcludeItems() ? "True" : "False");

								discountInfo.setITEMCODEINFO(itemCodeInfoList );
								discountInfoList.add(discountInfo);
								
								
							}else{
								return null;
							}
							
						}
						if(discountInfoList.isEmpty()) return null;
						couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
					}

				
				}
				

		}catch(Exception e){
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getDiscountAmount::::: ", e);
		}

		logger.debug("-------exit  smartPromotion method---------"+couponDiscountInfo);
		return couponDiscountInfo;
	}


	/*	private CouponDiscountInfo OCLoyaltySmartPromotion(CouponCodeEnquObj couponCodeEnquObj,Coupons coupObj,
			boolean isItemCode,CouponDiscountInfo couponDiscountInfo,List<CouponDiscountInfo> coupDiscInfoList,
			String disCountType, Users user,List<CouponDiscountGeneration>  coupDisList) throws BaseServiceException{
		try{

			CouponDiscountGenerateDao couponDiscountGenerateDao  = (CouponDiscountGenerateDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);

			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			LoyaltyBalanceDao loyaltyBalanceDao = (LoyaltyBalanceDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);

			if(isItemCode){

				List<String> retList = couponDiscountGenerateDao.findDistinctAttr(coupObj.getCouponId());

				//long deleted = couponDiscountGenerateDaoForDML.deleteTempPromoDumpBy(coupObj.getCouponId(), user.getUserId());
				coupDisList = new ArrayList<CouponDiscountGeneration>();
				for (String CDGAttr : retList) {

					List<CouponDiscountGeneration> tempcoupDisList = couponDiscountGenerateDao.findBy(coupObj.getCouponId(), user.getUserId(), 
							Utility.CDGAttrToSKUMap.get(CDGAttr), CDGAttr);
					if(tempcoupDisList != null && !tempcoupDisList.isEmpty()) {

						coupDisList.addAll(tempcoupDisList);
					}

				}//for
				if(coupDisList == null || coupDisList.isEmpty()) {

					//List<Object[]> list = coupDiscGenDao.getDiscAndNumOfItems(orgOwner);
					coupDisList = couponDiscountGenerateDao.findByCoupon(coupObj);
				}


				String customerId = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCUSTOMERID();
				long cardNumber= Long.parseLong(couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCARDNUMBER());


				ContactsLoyalty contactsLoyaltyObj = contactsLoyaltyDao.getContactsLoyaltyByCustId(customerId,user.getUserId());

				if(contactsLoyaltyObj!=null)				
					loyaltyBalanceDao.findByLoyaltyIdUserId(user.getUserId(), contactsLoyaltyObj.getLoyaltyId());





				if(coupDisList != null && !coupDisList.isEmpty()){
					 List<DiscountInfo> discountInfoList  = new ArrayList<DiscountInfo>();

					Map<Double, DiscountInfo> isSameDisMap = new HashMap<Double, DiscountInfo>();

					DiscountInfo  tempObj1 = null;
					List<ItemCodeInfo>	tempArrObj = null;

					for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
						if(!(isSameDisMap.containsKey(coupDisGenObj.getDiscount()))) {
							tempObj1 = new DiscountInfo();
							tempObj1.setVALUE(coupDisGenObj.getDiscount().toString());
							tempObj1.setVALUECODE(disCountType);
							tempArrObj = new ArrayList<ItemCodeInfo>();
							ItemCodeInfo tempObj2 = new ItemCodeInfo();


							if(isItemCode) {
								tempObj1.setMINPURCHASEVALUE(Constants.STRING_NILL);
								tempObj2.setITEMCODE(coupDisGenObj.getItemCategory() != null ? coupDisGenObj.getItemCategory() : Constants.STRING_NILL);
								tempArrObj.add(tempObj2);

							}else {

								tempObj1.setMINPURCHASEVALUE(coupDisGenObj.getTotPurchaseAmount().toString());
							}
							tempObj1.setITEMCODEINFO(tempArrObj);

							isSameDisMap.put(coupDisGenObj.getDiscount(), tempObj1);

						}else  {

							tempObj1 = isSameDisMap.get(coupDisGenObj.getDiscount());
							tempArrObj =(List<ItemCodeInfo>)tempObj1.getITEMCODEINFO();

							ItemCodeInfo tempObj2 = new ItemCodeInfo();
							tempObj2.setITEMCODE(coupDisGenObj.getItemCategory());
							tempArrObj.add(tempObj2);
							tempObj1.setITEMCODEINFO(tempArrObj);

						}


					} // for
					logger.info("isSameDisMap is  :: "+isSameDisMap);
					Set<Double> keySet = isSameDisMap.keySet();
					for (Double double1 : keySet) {
						discountInfoList.add(isSameDisMap.get(double1));
					}
					couponDiscountInfo.setDISCOUNTINFO(discountInfoList);

				}

			 }else{	

			List<DiscountInfo> discountInfoList  = new ArrayList<DiscountInfo>();

			Map<Double, DiscountInfo> isSameDisMap = new HashMap<Double, DiscountInfo>();
			coupDisList = couponDiscountGenerateDao.findByCoupon(coupObj);
			if(coupDisList != null && coupDisList.size() > 0 ) {
				DiscountInfo  tempObj1 = null;
				List<ItemCodeInfo>	tempArrObj = null;

				for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
					if(!(isSameDisMap.containsKey(coupDisGenObj.getDiscount()))) {
						tempObj1 = new DiscountInfo();
						tempObj1.setVALUE(coupDisGenObj.getDiscount().toString());
						tempObj1.setVALUECODE(disCountType);
						tempArrObj = new ArrayList<ItemCodeInfo>();
						ItemCodeInfo tempObj2 = new ItemCodeInfo();

						if(isItemCode) {
							tempObj1.setMINPURCHASEVALUE(Constants.STRING_NILL);
							tempObj2.setITEMCODE(coupDisGenObj.getItemCategory());
							tempArrObj.add(tempObj2);
						}else {

							tempObj1.setMINPURCHASEVALUE(coupDisGenObj.getTotPurchaseAmount().toString());
						}
						tempObj1.setITEMCODEINFO(tempArrObj);

						isSameDisMap.put(coupDisGenObj.getDiscount(), tempObj1);
					}else  {

						tempObj1 = isSameDisMap.get(coupDisGenObj.getDiscount());
						tempArrObj =(List<ItemCodeInfo>)tempObj1.getITEMCODEINFO();

						ItemCodeInfo tempObj2 = new ItemCodeInfo();
						tempObj2.setITEMCODE(coupDisGenObj.getItemCategory());
						tempArrObj.add(tempObj2);
						tempObj1.setITEMCODEINFO(tempArrObj);
					}
				} // for
				logger.info("isSameDisMap is  :: "+isSameDisMap);
				Set<Double> keySet = isSameDisMap.keySet();
				for (Double double1 : keySet) {
					discountInfoList.add(isSameDisMap.get(double1));
				}
				couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
				}
			 }

		}catch(Exception e){
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getDiscountAmount::::: ", e);
		}

		logger.debug("-------exit  OcLoyaltySmartPromotion---------"+couponDiscountInfo);
			return couponDiscountInfo;
		}*/
	private List<CouponDiscountInfo> setCoupDiscInfoObjNewPlugin(List<CouponDiscountInfo> coupDiscInfoList ,
			CouponCodeEnquObj couponCodeEnquObj,Coupons eachCouponObj, boolean isOcLoyalty, 
			String couponCodeStr, Users user, CouponCodes couponCode, boolean isRequestedFromNewPlugin,
			Map<String, String> posMappingMap, boolean isreward, boolean iselegible, LoyaltyBalance balance, Long loyaltyId,
			Map<Long, LoyaltyMemberItemQtyCounter> memberQtyMap, Double pointsBalance, ContactsLoyalty conLty) throws Exception{
		
		return setCoupDiscInfoObjNewPlugin(coupDiscInfoList, couponCodeEnquObj, eachCouponObj, isOcLoyalty, couponCodeStr, user, 
				couponCode, isRequestedFromNewPlugin, posMappingMap, isreward, iselegible, balance, loyaltyId, memberQtyMap, pointsBalance, conLty, false);
	}
			

	private List<CouponDiscountInfo> setCoupDiscInfoObjNewPlugin(List<CouponDiscountInfo> coupDiscInfoList ,
			CouponCodeEnquObj couponCodeEnquObj,Coupons eachCouponObj, boolean isOcLoyalty, 
			String couponCodeStr, Users user, CouponCodes couponCode, boolean isRequestedFromNewPlugin,
			Map<String, String> posMappingMap, boolean isreward, boolean iselegible, LoyaltyBalance balance, Long loyaltyId,
			Map<Long, LoyaltyMemberItemQtyCounter> memberQtyMap, Double pointsBalance, ContactsLoyalty conLty, boolean sendall) throws Exception {

		logger.debug("-------entered setCoupDiscInfoObj---------"+eachCouponObj.getCouponCode()+" "+isreward);
		if(coupDiscInfoList== null) {
			coupDiscInfoList = new ArrayList<CouponDiscountInfo>() ;
		}
		CouponDiscountInfo couponDiscountInfo=new CouponDiscountInfo();
		couponDiscountInfo.setCOUPONNAME(eachCouponObj.getCouponName());
		couponDiscountInfo.setCOUPONCODE(couponCodeStr);
		
		boolean noReceiptData = false;
		if(isRequestedFromNewPlugin){
			
			noReceiptData =( couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT().trim().isEmpty() ||
					Double.parseDouble(couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT().trim()) == 0) &&
							couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS().isEmpty();
			
		}
		
		LoyaltyMemberItemQtyCounterDao loyaltyMemberItemQtyCounterDao = (LoyaltyMemberItemQtyCounterDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_MMEMBER_ITEM_QTY_COUNTER_DAO);
		String couponType = Constants.STRING_NILL;
		String description = eachCouponObj.getCouponDescription(); 
		if(description != null ){
			String currSymbol = Utility.countryCurrencyMap.get(user.getCountryType());
			   if(currSymbol != null && !currSymbol.isEmpty()) {
				   if(currSymbol.equals("$")) {
					   currSymbol = "USD";
					   if(user.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_CANADA)) {
						   currSymbol = "CAD";
					   }
				   }
			   }else {
				   currSymbol = "USD";
			   }
			description = description.replace("[PHCurr]", currSymbol);
		}
		couponDiscountInfo.setDESCRIPTION(description != null && !description.isEmpty() ? description : Constants.STRING_NILL);
		if(isRequestedFromNewPlugin){
			//String loyaltyValueCode =  Constants.STRING_NILL;
			couponDiscountInfo.setLOYALTYVALUECODE(Constants.STRING_NILL);
			//couponDiscountInfo.setREWARDRATIO(eachCouponObj.getPurchaseQty() != null && eachCouponObj.getPurchaseQty() != 0 ? eachCouponObj.getPurchaseQty()+"-"+eachCouponObj.getRequiredLoyltyPoits() : "" );
			couponDiscountInfo.setNUDGEPROMOCODE("NO");
			couponDiscountInfo.setNUDGEDESCRIPTION(Constants.STRING_NILL);
			//loyaltyValueCode = eachCouponObj.getValueCode();
			couponDiscountInfo.setAPPLYATTRIBUTES(eachCouponObj.isCombineItemAttributes() ? "Combination":"Multiple");
			if((eachCouponObj.getRequiredLoyltyPoits() != null || eachCouponObj.getMultiplierValue()!=null) && eachCouponObj.getLoyaltyPoints() != null) {
				
				if(eachCouponObj.getValueCode() == null || 
						eachCouponObj.getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
					couponType = "LOYALTY";
					couponDiscountInfo.setLOYALTYVALUECODE(eachCouponObj.getValueCode() == null ? OCConstants.LOYALTY_TYPE_POINTS : eachCouponObj.getValueCode());		
				}else if(eachCouponObj.getValueCode() != null){
					
					couponType = "REWARDS";
					couponDiscountInfo.setLOYALTYVALUECODE(eachCouponObj.getValueCode());
				}
				
			}else{
				
				if(eachCouponObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_SINGLE)){
					
					couponType = "PROMOTIONS";
					
				}else if(eachCouponObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_MULTIPLE)){
					
					couponType = "COUPONS";
				}
			}
			
			couponDiscountInfo.setCOUPONTYPE(couponType); 
			
		}else{
			if((eachCouponObj.getRequiredLoyltyPoits() != null || eachCouponObj.getMultiplierValue()!=null) && eachCouponObj.getLoyaltyPoints() != null) {
				couponDiscountInfo.setCOUPONTYPE("ONLY-LOYALTY"); 
				
	
			}else {
	
				couponDiscountInfo.setCOUPONTYPE("REGULAR");
				//couponDiscountInfo.setLOYALTYPOINTS(Constants.STRING_NILL);
			}
			
		}
		String loyaltyPointsStr = eachCouponObj.getRequiredLoyltyPoits() == null ? Constants.STRING_NILL : Constants.STRING_NILL+eachCouponObj.getRequiredLoyltyPoits();
		if(eachCouponObj.getMultiplierValue()==null)
		couponDiscountInfo.setLOYALTYPOINTS(loyaltyPointsStr); //to set this value we can only give after the discount rule returned  
		/*if(isOcLoyalty && eachCouponObj.getValueCode()!=null && !eachCouponObj.getValueCode().isEmpty()) {
			couponDiscountInfo.setLOYALTYVALUECODE(eachCouponObj.getValueCode());
		}else if(isOcLoyalty && eachCouponObj.getValueCode()==null) {
			couponDiscountInfo.setLOYALTYVALUECODE(OCConstants.LOYALTY_POINTS);
		}*/
		if(eachCouponObj.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_STATIC)){
			couponDiscountInfo.setVALIDFROM(MyCalendar.calendarToString(
					eachCouponObj.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
			//logger.info("static to date "+eachCouponObj.getCouponExpiryDate());
			couponDiscountInfo.setVALIDTO(MyCalendar.calendarToString(eachCouponObj.
					getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
		}
		else{
			boolean isSet = false;
			if(eachCouponObj.getExpiryType().equalsIgnoreCase(Constants.COUP_VALIDITY_PERIOD_DYNAMIC) && couponCode != null) {
				
				String expiryType = eachCouponObj.getExpiryDetails();
				String [] strArr = expiryType.split(Constants.ADDR_COL_DELIMETER);//
				int numOfDays = Integer.parseInt(strArr[1]);
				if("I".equals(strArr[0])){
					couponDiscountInfo.setVALIDFROM(MyCalendar.calendarToString(
							couponCode.getIssuedOn(), MyCalendar.FORMAT_DATETIME_STYEAR));
					Calendar cal = couponCode.getIssuedOn();
					cal.add(Calendar.DAY_OF_MONTH, numOfDays);  
					couponDiscountInfo.setVALIDTO(MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR));
					isSet = true;
				}else{
					
					if(couponCode.getContactId() != null ){
						try {
							ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
							Contacts contacts = contactsDao.findById(couponCode.getContactId());
							if(contacts != null) {
								
								if("B".equals(strArr[0]) && contacts.getBirthDay() != null) {
									couponDiscountInfo.setVALIDFROM(MyCalendar.calendarToString(
											contacts.getBirthDay(), MyCalendar.FORMAT_DATETIME_STYEAR));
									Calendar cal = contacts.getBirthDay();
									cal.add(Calendar.DAY_OF_MONTH, numOfDays);  
									couponDiscountInfo.setVALIDTO(MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR));
									isSet = true;
								}else if("A".equals(strArr[0]) && contacts.getAnniversary() != null){
									
									couponDiscountInfo.setVALIDFROM(MyCalendar.calendarToString(
											contacts.getAnniversary(), MyCalendar.FORMAT_DATETIME_STYEAR));
									Calendar cal = contacts.getAnniversary();
									cal.add(Calendar.DAY_OF_MONTH, numOfDays);  
									couponDiscountInfo.setVALIDTO(MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR));
									isSet = true;
								}
							}
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
						
					}
					if(!isSet) {
						
						couponDiscountInfo.setVALIDFROM(MyCalendar.calendarToString(
								eachCouponObj.getUserCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
						Calendar cal = Calendar.getInstance();
						cal.set(2020, 11, 31, 23, 59, 59);
						//logger.info("dynamic to date "+cal.toString());
						couponDiscountInfo.setVALIDTO(MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR));
					}
				}
				
				
			}else{
				
				couponDiscountInfo.setVALIDFROM(MyCalendar.calendarToString(
						eachCouponObj.getUserCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
				Calendar cal = Calendar.getInstance();
				cal.set(2020, 11, 31, 23, 59, 59);
				//logger.info("dynamic to date "+cal.toString());
				couponDiscountInfo.setVALIDTO(MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STYEAR));
			}
		}

		boolean isItemCode = false;
		String disCountType =Constants.STRING_NILL;
		if(eachCouponObj.getDiscountType().equals("Percentage") && eachCouponObj.getDiscountCriteria().equals("Total Purchase Amount")) {

			couponDiscountInfo.setDISCOUNTCRITERIA("PERCENTAGE-MVP");
			disCountType = "P";
		}
		else if(eachCouponObj.getDiscountType().equals("Percentage") && eachCouponObj.getDiscountCriteria().equals("SKU")) {
			couponDiscountInfo.setDISCOUNTCRITERIA("PERCENTAGE-IC");
			isItemCode = true;
			disCountType = "P";
		}
		else if(eachCouponObj.getDiscountType().equals("Value") && eachCouponObj.getDiscountCriteria().equals("Total Purchase Amount")) {
			couponDiscountInfo.setDISCOUNTCRITERIA("VALUE-MVP");
			disCountType = "V";
		}
		else {
			couponDiscountInfo.setDISCOUNTCRITERIA("VALUE-I");
			isItemCode = true;
			disCountType = "V";
		}


		/**APP-721 Smart Promotions
		 * LOYALTYVALUECODE
		 *  */

		/*if(isOcLoyalty && eachCouponObj.getValueCode()!=null && !eachCouponObj.getValueCode().isEmpty()) {
			couponDiscountInfo.setLOYALTYVALUECODE(eachCouponObj.getValueCode());
		}else if(isOcLoyalty && eachCouponObj.getValueCode()==null) {
			couponDiscountInfo.setLOYALTYVALUECODE(OCConstants.LOYALTY_POINTS);
		}*/
		
		if(isRequestedFromNewPlugin) {
			
			couponDiscountInfo.setEXCLUDEDISCOUNTEDITEMS(eachCouponObj.isExcludeItems() != null && eachCouponObj.isExcludeItems() ? "True" : "False");
			couponDiscountInfo.setACCUMULATEDISCOUNT(eachCouponObj.isAccumulateOtherPromotion() != null && eachCouponObj.isAccumulateOtherPromotion() ? "TRUE" : "FALSE");
			if(eachCouponObj.isOtpAuthenCheck()) {
				couponDiscountInfo.setOTPENABLED("True");
			} else {
				couponDiscountInfo.setOTPENABLED("False");
			}
			couponDiscountInfo.setAPPLYDEFAULT( eachCouponObj.isApplyDefault() ? "TRUE" :"FALSE");
		//	couponDiscountInfo.setSHIPPINGFEE(eachCouponObj.getShippingFee() != null ? eachCouponObj.getShippingFee() : Constants.STRING_NILL );
			//couponDiscountInfo.setSHIPPINGFEETYPE(eachCouponObj.getShippingFeeType() != null ? eachCouponObj.getShippingFeeType() : Constants.STRING_NILL );
			
			
			String ItemsEligibility = Constants.STRING_NILL;
			if( eachCouponObj.getNoOfEligibleItems() != null && 
					!eachCouponObj.getNoOfEligibleItems().isEmpty()){
				if(eachCouponObj.getNoOfEligibleItems().equalsIgnoreCase("AllEligibleItems")) {
					
					
					ItemsEligibility = "ALL";
					
				}else if(eachCouponObj.getNoOfEligibleItems().equalsIgnoreCase(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT)) {
					
					ItemsEligibility = "HP";
					
				}else if(eachCouponObj.getNoOfEligibleItems().equalsIgnoreCase(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT)){
					
					ItemsEligibility = "HPND";
				}else if(eachCouponObj.getNoOfEligibleItems().equalsIgnoreCase(Constants.LOWEST_PRICED_ITEMS_WITH_DISCOUNT)){
					
					ItemsEligibility = "LP";
				}else if(eachCouponObj.getNoOfEligibleItems().equalsIgnoreCase(Constants.LOWEST_PRICED_ITEMS_WITH_OUT_DISCOUNT)){
					
					ItemsEligibility = "LPND";
				}
			}
			couponDiscountInfo.setELIGIBILITY(ItemsEligibility);
			
		}
		
		CouponDiscountInfo rewardDiscountInfo = null;
		CouponDiscountInfo LoyaltyOrMultipleDisc = null;
		if(couponDiscountInfo.getCOUPONTYPE().equals("COUPONS") || (couponDiscountInfo.getCOUPONTYPE().equals("LOYALTY"))){
			LoyaltyOrMultipleDisc = couponDiscountInfo;
			
		}
		if(isreward) rewardDiscountInfo = couponDiscountInfo;
		
		List<String>  appliedPromo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getAPPLIEDCOUPONS();
		boolean isApplied = false;
		logger.debug("appliedPromo == "+isRequestedFromNewPlugin +ONESPACE+appliedPromo!= null );
		if(isRequestedFromNewPlugin && appliedPromo!= null && !appliedPromo.isEmpty()){
			for (String appliedCode : appliedPromo) {
				logger.debug("appliedCode ==="+appliedCode+" == "+couponDiscountInfo.getCOUPONCODE());
				if(couponDiscountInfo.getCOUPONCODE().equalsIgnoreCase(appliedCode)) {
					//couponDiscountInfo.setAPPLIEDCOUPONS("YES");
					isApplied = true;
					break;
				}
			}//for
		}
		if(!sendall){
		couponDiscountInfo = getDiscountAmountNewPlugin(couponCodeEnquObj, eachCouponObj, isItemCode, 
				couponDiscountInfo, coupDiscInfoList,disCountType,isOcLoyalty, user, posMappingMap,
				isRequestedFromNewPlugin,balance,pointsBalance, conLty );
		
		logger.debug("==returned=="+couponDiscountInfo);
		if(couponDiscountInfo == null && LoyaltyOrMultipleDisc != null && eachCouponObj.getMultiplierValue()==null){
			 if(eachCouponObj.getDiscountCriteria().equals("SKU")){
				LoyaltyOrMultipleDisc.setNUDGEDESCRIPTION(FREE_ITEM_NUDGE);
			}else{
				LoyaltyOrMultipleDisc.setNUDGEDESCRIPTION(RECEIPT_NUDGE);
			}
			LoyaltyOrMultipleDisc.setNUDGEPROMOCODE("YES");
			couponDiscountInfo = LoyaltyOrMultipleDisc;
			
			logger.debug("==returned=="+couponDiscountInfo.getNUDGEDESCRIPTION());
		}
		LoyaltyMemberItemQtyCounter memberqtyProgressBar = null;
		if(isreward && eachCouponObj.getSpecialRewadId() != null && loyaltyId != null ){
			try {
				if( memberQtyMap != null){
					
					memberqtyProgressBar = memberQtyMap.get(eachCouponObj.getSpecialRewadId());
				}else{
					if(balance !=null){
						
						List<LoyaltyMemberItemQtyCounter> retList = loyaltyMemberItemQtyCounterDao.findItemsCounter(eachCouponObj.getSpecialRewadId()+"", balance.getLoyaltyId());
						if(retList != null && !retList.isEmpty()){
							
							logger.debug("==returned=="+couponDiscountInfo == null);
							memberqtyProgressBar = retList.get(0); 
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}
		}
		//Gson temp = new Gson();
		CouponDiscountInfo splitToNudge = null;
		if(  isreward &&  eachCouponObj.getMultiplierValue()==null){
			logger.debug("==step1==");
			
								
			if(eachCouponObj.getDiscountCriteria().equals("SKU")){//on product
				boolean isdone = false;
				if(couponDiscountInfo != null) {
					logger.debug("==step2==");
					List<DiscountInfo> discounts = couponDiscountInfo.getDISCOUNTINFO();
					if(discounts == null || discounts.size() == 0) {
						if(iselegible ){
							couponDiscountInfo.setNUDGEPROMOCODE("YES");
							couponDiscountInfo.setNUDGEDESCRIPTION(FREE_ITEM_NUDGE);
						}
						
					}else{
						logger.debug("==step3==");
						for (DiscountInfo discountInfo : discounts) {
							
							List<ItemCodeInfo> itemCodeInfos = discountInfo.getITEMCODEINFO();
							if(itemCodeInfos != null && itemCodeInfos.size()>0){
								
								for (ItemCodeInfo itemCodeInfo : itemCodeInfos) {
									if(itemCodeInfo == null || itemCodeInfo.getITEMCODE()==null) continue;
									if(iselegible && itemCodeInfo.getITEMCODE().isEmpty()) {
										//LoyaltyMemberItemQtyCounter memberqtyProgressBar = null;
										if( eachCouponObj.getPurchaseQty() != null && eachCouponObj.getPurchaseQty() != 0){
											

											if(eachCouponObj.getSpecialRewadId() != null){
												logger.debug("==step6=="+balance.getBalance());
											//	List<LoyaltyMemberItemQtyCounter> retList = loyaltyMemberItemQtyCounterDao.findItemsCounter(eachCouponObj.getSpecialRewadId()+"", loyaltyId);
												/*if(retList != null && !retList.isEmpty()){
													
													memberqtyProgressBar = retList.get(0); 
												}*/
												
											}
											double totalPurchases = balance.getBalance()*eachCouponObj.getPurchaseQty();
											if(memberqtyProgressBar != null && memberqtyProgressBar.getQty() != 0){
												
												totalPurchases += memberqtyProgressBar.getQty();
											}
											logger.debug("==step7=="+totalPurchases);
											Double currentqty = 0.0;
											double multipleFactor = totalPurchases%eachCouponObj.getPurchaseQty();
											double totalfreeItems = (totalPurchases/eachCouponObj.getPurchaseQty())/eachCouponObj.getRequiredLoyltyPoits();
											couponDiscountInfo.setNUDGEPROMOCODE("YES");
											couponDiscountInfo.setNUDGEDESCRIPTION("Add Item to Avail Discount");
											couponDiscountInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
											if(isApplied){
												couponDiscountInfo.setAPPLIEDCOUPONS("YES");
												couponDiscountInfo.setNUDGEPROMOCODE("NO");
												couponDiscountInfo.setNUDGEDESCRIPTION("");
												
											}
											if(multipleFactor > 0 ){// need to split the nudge
												currentqty += multipleFactor;
												/*CouponDiscountInfo actualDiscountInfo = couponDiscountInfo;
												CouponDiscountInfo splitToNudge = couponDiscountInfo;*/
												
												splitToNudge = new CouponDiscountInfo(couponDiscountInfo.getCOUPONNAME(), 
														couponDiscountInfo.getCOUPONCODE(), couponDiscountInfo.getCOUPONTYPE(), 
														couponDiscountInfo.getACCUMULATEDISCOUNT(), couponDiscountInfo.getVALIDFROM(), 
														couponDiscountInfo.getVALIDTO(), couponDiscountInfo.getDISCOUNTCRITERIA(), 
														couponDiscountInfo.getLOYALTYPOINTS(), new ArrayList<DiscountInfo>());
												splitToNudge.setEXCLUDEDISCOUNTEDITEMS(couponDiscountInfo.getEXCLUDEDISCOUNTEDITEMS());
												splitToNudge.setELIGIBILITY(couponDiscountInfo.getELIGIBILITY());
												splitToNudge.setDESCRIPTION(couponDiscountInfo.getDESCRIPTION());
												splitToNudge.setAPPLYATTRIBUTES(couponDiscountInfo.getAPPLYATTRIBUTES());
												splitToNudge.setLOYALTYVALUECODE(couponDiscountInfo.getLOYALTYVALUECODE());
												splitToNudge.setNUDGEPROMOCODE("YES");
											//	List<String>  appliedPromo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getAPPLIEDCOUPONS();
												logger.debug("appliedPromo == "+isRequestedFromNewPlugin +ONESPACE+appliedPromo!= null );
												if(isRequestedFromNewPlugin && appliedPromo!= null && !appliedPromo.isEmpty()){
													splitToNudge.setAPPLIEDCOUPONS("NO");
													/*for (String appliedCode : appliedPromo) {
														logger.debug("appliedCode ==="+appliedCode+" == "+splitToNudge.getCOUPONCODE());
														if(splitToNudge.getCOUPONCODE().equalsIgnoreCase(appliedCode)) {
															splitToNudge.setAPPLIEDCOUPONS("YES");
															break;
														}
													}//for
*/												}
												splitToNudge.setNUDGEDESCRIPTION(currentqty.intValue()+ " of "+eachCouponObj.getPurchaseQty().intValue() + ONESPACE+"Purchases"+ " Reached.");
												splitToNudge.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
												if(eachCouponObj.isCombineItemAttributes()){
													String desc = couponDiscountInfo.getDESCRIPTION();
													//if(desc.indexOf("against") != -1 && desc.indexOf(" reward") != -1) {
														
														Pattern p = Pattern.compile(Pattern.quote("up to") + "(.*?)" + Pattern.quote("item(s)"));
											            Matcher m = p.matcher(desc);
											            String setDesc = "";
											            String finalDesc = "";
											            boolean replaceQty = false;
											            /*while (m.find()) {
											            	String qty = m.group(1);
											            	replaceQty = true;
											            	Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
											            	setDesc = desc.replace(m.group(1), ONESPACE+getActualNumber(replaceQtyDbl)+ONESPACE);
											            	//setDesc = desc.replace(m.group(1), ONESPACE+((Double.parseDouble(qty))*balance.getBalance())/eachCouponObj.getRequiredLoyltyPoits()+ONESPACE);
											            }*/
											            String itemsQty = StringUtils.substringBetween(desc,"up to ", ITEMS);
											            if(itemsQty != null){
											            	replaceQty = true;
											            	logger.debug("itemsQty =="+itemsQty);
											            	String qty = itemsQty;
											            	Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
											            	setDesc = desc.replace("up to "+itemsQty+ITEMS, "up to "+getActualNumber(replaceQtyDbl)+ITEMS);
											            }else{
											            	//Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
											            	String[] returnarr = getReplacedQty(desc, balance, eachCouponObj.getRequiredLoyltyPoits(), null, null, null );
											            	if(returnarr != null){
											            		
											            		replaceQty = (returnarr != null);
											            		setDesc = returnarr[1];
											            	}
											            }
											            if(replaceQty ){
											            	
											            	/*p = Pattern.compile(Pattern.quote("against") + "(.*?)" + Pattern.quote("Reward"));
											            	m = p.matcher(desc);
											            	while (m.find()) {
											            		finalDesc = setDesc.replace(m.group(1), ONESPACE+balance.getBalance()+ONESPACE);
											            	}*/
											            	finalDesc = setDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+balance.getBalance()+ONESPACE);
											            	/*String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"").
																replace(desc.substring(desc.indexOf("against")+"against".length(), desc.indexOf(" reward")), balance.getBalance()+"");
														//rewardDiscountInfo.setDESCRIPTION(setDesc);
														 
		 */													logger.debug("desc==="+finalDesc);
											            	 couponDiscountInfo.setDESCRIPTION(finalDesc);
											            	 //}
											            }
												}
											
												//String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"")
													//	.replace(desc.substring(desc.indexOf("against")+8, desc.indexOf(" reward")), balance.getBalance()+"");
												
											}else if(eachCouponObj.getRequiredLoyltyPoits()!=null && totalfreeItems > eachCouponObj.getRequiredLoyltyPoits() && eachCouponObj.isCombineItemAttributes()){
												String desc = couponDiscountInfo.getDESCRIPTION();
												//if(desc.indexOf("against") != -1 && desc.indexOf(" reward") != -1) {
													Pattern p = Pattern.compile(Pattern.quote("up to") + "(.*?)" + Pattern.quote("item(s)"));
										            Matcher m = p.matcher(desc);
										            String setDesc = "";
										            String finalDesc = "";
										            boolean replaceQty = false;
										            /*while (m.find()) {
										            	replaceQty = true;
										            	setDesc = desc.replace(m.group(1), ONESPACE+getActualNumber(totalfreeItems)+ONESPACE);
										            }*/
										            String itemsQty = StringUtils.substringBetween(desc,"up to ", ITEMS);
										            if(itemsQty != null){
										            	replaceQty = true;
										            	logger.debug("itemsQty =="+itemsQty);
										            	setDesc = desc.replace("up to "+itemsQty+ITEMS, "up to "+getActualNumber(totalfreeItems)+ITEMS);
										            }else{
										            	//Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
										            	String[] returnarr = getReplacedQty(desc, null, null, totalfreeItems, null, null );
										            	if(returnarr != null){
										            		
										            		replaceQty = (returnarr != null);
										            		setDesc = returnarr[1];
										            	}
										            }
										            if(replaceQty){
										            	finalDesc = setDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+balance.getBalance()+ONESPACE);
										            	logger.debug("desc==="+finalDesc);
										            	 couponDiscountInfo.setDESCRIPTION(finalDesc);
										            }
												//}
												//String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"")
													//	.replace(desc.substring(desc.indexOf("against")+8, desc.indexOf(" reward")), balance.getBalance()+"");
												
											}
											
										}else{
											if(eachCouponObj.isCombineItemAttributes()){
											String desc = couponDiscountInfo.getDESCRIPTION();
												
									            String setDesc = "";
									            String finalDesc = "";
									            boolean replaceQty = false;
									           /* while (m.find()) {

									            	String qty = m.group(1);
									            	replaceQty = true;
									            	Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
									            	setDesc = desc.replace(m.group(1), ONESPACE+getActualNumber(replaceQtyDbl)+ONESPACE);
									            
									            }*/
									            String itemsQty = StringUtils.substringBetween(desc,"up to ", ITEMS);
									            if(itemsQty != null){
									            	replaceQty = true;
									            	logger.debug("itemsQty =="+itemsQty);
									            	String qty = itemsQty;
									            	Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
									            	setDesc = desc.replace("up to "+itemsQty+ITEMS, "up to "+getActualNumber(replaceQtyDbl)+ITEMS);
									            }else{
									            	//Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
									            	String[] returnarr = getReplacedQty(desc, balance, eachCouponObj.getRequiredLoyltyPoits(), null, null, null );
									            	if(returnarr != null){
									            		
									            		replaceQty = (returnarr != null);
									            		setDesc = returnarr[1];
									            	}
									            }
									            if(replaceQty){
									            	
									            	/*p = Pattern.compile(Pattern.quote("against") + "(.*?)" + Pattern.quote("Reward"));
									            	m = p.matcher(desc);
									            	while (m.find()) {
									            		finalDesc = setDesc.replace(m.group(1), ONESPACE+balance.getBalance()+ONESPACE);
									            	}*/
									            	finalDesc = setDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+balance.getBalance()+ONESPACE);
									            	/*String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"").
														replace(desc.substring(desc.indexOf("against")+"against".length(), desc.indexOf(" reward")), balance.getBalance()+"");
												//rewardDiscountInfo.setDESCRIPTION(setDesc);
												 
									            	 */												logger.debug("desc==="+finalDesc);
									            	 couponDiscountInfo.setDESCRIPTION(finalDesc);
									            }
											}

											//}
											couponDiscountInfo.setNUDGEPROMOCODE("YES");
											couponDiscountInfo.setNUDGEDESCRIPTION(FREE_ITEM_NUDGE);
											couponDiscountInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
										}
										isdone = true;
										break;
									}else if(!iselegible && !itemCodeInfo.getITEMCODE().isEmpty()){
										if( eachCouponObj.getPurchaseQty() != null && eachCouponObj.getPurchaseQty() != 0){
											Double totalPurchases = 0.0;
											//LoyaltyMemberItemQtyCounter memberqtyProgressBar = null;
											/*if(eachCouponObj.getSpecialRewadId() != null){
												//logger.debug("==step6=="+balance.getBalance());
												List<LoyaltyMemberItemQtyCounter> retList = loyaltyMemberItemQtyCounterDao.findItemsCounter(eachCouponObj.getSpecialRewadId()+"", loyaltyId);
												if(retList != null && !retList.isEmpty()){
													
													memberqtyProgressBar = retList.get(0); 
												}
												
											}*/
											//Double replaceQtyDbl = ((Double.parseDouble(qty))*balance.getBalance())/eachCouponObj.getRequiredLoyltyPoits();
							            	//setDesc = desc.replace(m.group(1), ONESPACE+replaceQtyDbl+ONESPACE);
											if(balance !=null && balance.getBalance() != null)totalPurchases = balance.getBalance()*eachCouponObj.getPurchaseQty();
											if(memberqtyProgressBar != null && memberqtyProgressBar.getQty() != 0){
												
												totalPurchases += memberqtyProgressBar.getQty();
											}
											couponDiscountInfo.setNUDGEPROMOCODE("YES");
											couponDiscountInfo.setNUDGEDESCRIPTION((totalPurchases.intValue()) + " of "+eachCouponObj.getPurchaseQty().intValue() + ONESPACE+"Purchases"+ " Reached.");
											couponDiscountInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
											
										}else{
											
											couponDiscountInfo.setNUDGEPROMOCODE("YES");
											couponDiscountInfo.setNUDGEDESCRIPTION((balance !=null ? balance.getBalance() : 0)+ " of "+eachCouponObj.getRequiredLoyltyPoits() + ONESPACE+eachCouponObj.getValueCode()+ " Reached.");
											couponDiscountInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
										}
										//List<CouponDiscountInfo> NudcoupDiscInfoList = new ArrayList<CouponDiscountInfo>();
										/*NudcoupDiscInfoList.add(couponDiscountInfo);
									coupDiscInfoList = NudcoupDiscInfoList;*/
										isdone = true;
										break;
									}
								}
							}
							if(isdone){
								logger.debug("==step4==");
								couponDiscountInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
								break;
							}
						}
						if(!isdone){
							logger.debug("==step5=="+iselegible+ONESPACE+ONESPACE+eachCouponObj.getPurchaseQty());
							//LoyaltyMemberItemQtyCounter memberqtyProgressBar = null;
							
							if( eachCouponObj.getPurchaseQty() != null && eachCouponObj.getPurchaseQty() != 0){
								if(iselegible ){
									/*if(eachCouponObj.getSpecialRewadId() != null){
										logger.debug("==step6=="+balance.getBalance());
										List<LoyaltyMemberItemQtyCounter> retList = loyaltyMemberItemQtyCounterDao.findItemsCounter(eachCouponObj.getSpecialRewadId()+"", loyaltyId);
										if(retList != null && !retList.isEmpty()){
											
											memberqtyProgressBar = retList.get(0); 
										}
										
									}*/
									double totalPurchases = balance.getBalance()*eachCouponObj.getPurchaseQty();
									if(memberqtyProgressBar != null && memberqtyProgressBar.getQty() != 0){
										
										totalPurchases += memberqtyProgressBar.getQty();
									}
									logger.debug("==step7=="+totalPurchases);
									Double currentqty = 0.0;
									double multipleFactor = totalPurchases%eachCouponObj.getPurchaseQty();
									double totalfreeItems = (totalPurchases/eachCouponObj.getPurchaseQty())/eachCouponObj.getRequiredLoyltyPoits();
									if(multipleFactor > 0 ){// need to split the nudge
										currentqty += multipleFactor;
										CouponDiscountInfo actualDiscountInfo = couponDiscountInfo;
										 splitToNudge = new CouponDiscountInfo(couponDiscountInfo.getCOUPONNAME(), 
												couponDiscountInfo.getCOUPONCODE(), couponDiscountInfo.getCOUPONTYPE(), 
												couponDiscountInfo.getACCUMULATEDISCOUNT(), couponDiscountInfo.getVALIDFROM(), 
												couponDiscountInfo.getVALIDTO(), couponDiscountInfo.getDISCOUNTCRITERIA(), 
												couponDiscountInfo.getLOYALTYPOINTS(), new ArrayList<DiscountInfo>());
										splitToNudge.setEXCLUDEDISCOUNTEDITEMS(couponDiscountInfo.getEXCLUDEDISCOUNTEDITEMS());
										splitToNudge.setELIGIBILITY(couponDiscountInfo.getELIGIBILITY());
										splitToNudge.setDESCRIPTION(couponDiscountInfo.getDESCRIPTION());
										splitToNudge.setAPPLYATTRIBUTES(couponDiscountInfo.getAPPLYATTRIBUTES());
										splitToNudge.setLOYALTYVALUECODE(couponDiscountInfo.getLOYALTYVALUECODE());
										//List<String>  appliedPromo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getAPPLIEDCOUPONS();
										logger.debug("appliedPromo == "+isRequestedFromNewPlugin +ONESPACE+appliedPromo!= null );
										if(isRequestedFromNewPlugin && appliedPromo!= null && !appliedPromo.isEmpty()){
											splitToNudge.setAPPLIEDCOUPONS("NO");
											/*for (String appliedCode : appliedPromo) {
												logger.debug("appliedCode ==="+appliedCode+" == "+splitToNudge.getCOUPONCODE());
												if(splitToNudge.getCOUPONCODE().equalsIgnoreCase(appliedCode)) {
													splitToNudge.setAPPLIEDCOUPONS("YES");
													break;
												}
											}//for
*/										}
										splitToNudge.setNUDGEPROMOCODE("YES");
										splitToNudge.setNUDGEDESCRIPTION(currentqty.intValue()+ " of "+eachCouponObj.getPurchaseQty().intValue() + ONESPACE+"Purchases"+ " Reached.");
										splitToNudge.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
										
										//coupDiscInfoList.add(splitToNudge);
										if(eachCouponObj.isCombineItemAttributes()){
											String desc = couponDiscountInfo.getDESCRIPTION();
											//if(desc.indexOf("against") != -1 && desc.indexOf(" reward") != -1) {
												Pattern p = Pattern.compile(Pattern.quote("up to") + "(.*?)" + Pattern.quote("item(s)"));
									            Matcher m = p.matcher(desc);
									            String setDesc = "";
									            String finalDesc = "";
									            String qty = "1.0";
									            String rewardsToCut = "";
									            boolean replaceQty = false;
									            /*while (m.find()) {

									            	 qty = m.group(1);
									            	 replaceQty = true;
									            	 
									            	setDesc = desc.replace(m.group(1), ONESPACE+getActualNumber((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()))+ONESPACE);
									            
									            }*/
									            String itemsQty = StringUtils.substringBetween(desc,"up to ", ITEMS);
									            /*if(itemsQty != null){
									            	replaceQty = true;
									            	logger.debug("itemsQty =="+itemsQty);
									            	qty = itemsQty;
									            	Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
									            	setDesc = desc.replace("up to "+itemsQty+ITEMS, "up to "+getActualNumber(replaceQtyDbl)+ITEMS);
									            }else{*/
									            	//Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
									            	String discountQty = "";
									            	List<DiscountInfo> discountInfoLst = couponDiscountInfo.getDISCOUNTINFO();
									            	 for (DiscountInfo discountInfo : discountInfoLst) {
									            		 
									            		 List<ItemCodeInfo> itemsLst = discountInfo.getITEMCODEINFO();
									            		 for (ItemCodeInfo itemCodeInfo : itemsLst) {
									            			 discountQty= itemCodeInfo.getQUANTITY();//*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits())+"");
									            			 
									            		 }
									            		 
									            	 }
									            	String[] returnarr = getReplacedQty(desc, balance, eachCouponObj.getRequiredLoyltyPoits(), null, discountQty, null );
									            	if(returnarr != null){
									            		
									            		replaceQty = (returnarr != null);
									            		setDesc = returnarr[1];
									            		qty=returnarr[0];
									            		rewardsToCut = returnarr[2];
									            		logger.debug("rewardsToCut ==="+rewardsToCut);
									            	}
									            	/*replaceQty = (returnarr != null);
									            	setDesc = returnarr[1];*/
									            //}
									            if(replaceQty){
									            	
									            	/*p = Pattern.compile(Pattern.quote("against") + "(.*?)" + Pattern.quote("Reward"));
									            	m = p.matcher(desc);
									            	while (m.find()) {
									            		finalDesc = setDesc.replace(m.group(1), ONESPACE+balance.getBalance()+ONESPACE);
									            	}*/
									            	finalDesc = setDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+(!rewardsToCut.isEmpty()? rewardsToCut :balance.getBalance())+ONESPACE);
									            	/*String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"").
														replace(desc.substring(desc.indexOf("against")+"against".length(), desc.indexOf(" reward")), balance.getBalance()+"");
												//rewardDiscountInfo.setDESCRIPTION(setDesc);
									            	 */	logger.debug("desc==="+finalDesc);
									            	 couponDiscountInfo.setDESCRIPTION(finalDesc);
									            	 //}
									            	 //String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"")
									            	 //	.replace(desc.substring(desc.indexOf("against")+8, desc.indexOf(" reward")), balance.getBalance()+"");
									            	// List<DiscountInfo> discountsInfoLst = couponDiscountInfo.getDISCOUNTINFO();
									            	 for (DiscountInfo discountInfo : discountInfoLst) {
									            		 
									            		 List<ItemCodeInfo> itemsLst = discountInfo.getITEMCODEINFO();
									            		 for (ItemCodeInfo itemCodeInfo : itemsLst) {
									            			 itemCodeInfo.setQUANTITY(Double.parseDouble(qty)+"");//*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits())+"");
									            			 
									            		 }
									            		 
									            	 }
									            }
										}

										couponDiscountInfo.setNUDGEPROMOCODE("NO");
										couponDiscountInfo.setNUDGEDESCRIPTION("");
									}else if(eachCouponObj.getRequiredLoyltyPoits()!=null && totalfreeItems > eachCouponObj.getRequiredLoyltyPoits()){
										if(eachCouponObj.isCombineItemAttributes()){
											String desc = couponDiscountInfo.getDESCRIPTION();
											//if(desc.indexOf("against") != -1 && desc.indexOf(" reward") != -1) {
												Pattern p = Pattern.compile(Pattern.quote("up to") + "(.*?)" + Pattern.quote("item(s)"));
									            Matcher m = p.matcher(desc);
									            String setDesc = "";
									            String finalDesc = "";
									            boolean replaceQty = false;
									            /*while (m.find()) {
									            	setDesc = desc.replace(m.group(1), ONESPACE+getActualNumber(totalfreeItems)+ONESPACE);
									            	replaceQty = true;
									            }*/
									            String itemsQty = StringUtils.substringBetween(desc,"up to ", ITEMS);
									            if(itemsQty != null){
									            	replaceQty = true;
									            	logger.debug("itemsQty =="+itemsQty);
									            	String qty = itemsQty;
									            	Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
									            	setDesc = desc.replace("up to "+itemsQty+ITEMS, "up to "+getActualNumber(replaceQtyDbl)+ITEMS);
									            }else{
									            	//Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
									            	String[] returnarr = getReplacedQty(desc, balance, eachCouponObj.getRequiredLoyltyPoits(), null , null, null);
									            	if(returnarr != null){
									            		
									            		replaceQty = (returnarr != null);
									            		setDesc = returnarr[1];
									            	}
									            }
									            if(replaceQty){
									            	
									            	/*p = Pattern.compile(Pattern.quote("against") + "(.*?)" + Pattern.quote("Reward"));
									            	m = p.matcher(desc);
									            	while (m.find()) {
									            		finalDesc = setDesc.replace(m.group(1), ONESPACE+balance.getBalance()+ONESPACE);
									            	}*/
									            	finalDesc = setDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+balance.getBalance()+ONESPACE);
									            	/*String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), totalfreeItems+"").
														replace(desc.substring(desc.indexOf("against")+"against".length(), desc.indexOf(" reward")), balance.getBalance()+"");
									            	 *///rewardDiscountInfo.setDESCRIPTION(setDesc);
									            	logger.debug("desc==="+finalDesc);
									            	couponDiscountInfo.setDESCRIPTION(finalDesc);
									            	//}
									            	//String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"")
									            	//	.replace(desc.substring(desc.indexOf("against")+8, desc.indexOf(" reward")), balance.getBalance()+"");
									            	List<DiscountInfo> discountInfoLst = couponDiscountInfo.getDISCOUNTINFO();
									            	for (DiscountInfo discountInfo : discountInfoLst) {
									            		
									            		List<ItemCodeInfo> itemsLst = discountInfo.getITEMCODEINFO();
									            		for (ItemCodeInfo itemCodeInfo : itemsLst) {
									            			itemCodeInfo.setQUANTITY(totalfreeItems+"");
									            			
									            		}
									            		
									            	}
									            }
										}

										couponDiscountInfo.setNUDGEPROMOCODE("NO");
										couponDiscountInfo.setNUDGEDESCRIPTION("");
									}
									
								}else{
									Double totalPurchases = 0.0;
									/*if(eachCouponObj.getSpecialRewadId() != null){
										logger.debug("==step6=="+balance.getBalance());
										List<LoyaltyMemberItemQtyCounter> retList = loyaltyMemberItemQtyCounterDao.findItemsCounter(eachCouponObj.getSpecialRewadId()+"", loyaltyId);
										if(retList != null && !retList.isEmpty()){
											
											memberqtyProgressBar = retList.get(0); 
										}
										
									}*/
									if(balance != null && balance.getBalance() != null)totalPurchases = balance.getBalance()*eachCouponObj.getPurchaseQty();
									if(memberqtyProgressBar != null && memberqtyProgressBar.getQty() != 0){
										
										totalPurchases += memberqtyProgressBar.getQty();
									}
									couponDiscountInfo.setNUDGEPROMOCODE("YES");
									couponDiscountInfo.setNUDGEDESCRIPTION((totalPurchases.intValue()) + " of "+eachCouponObj.getPurchaseQty().intValue() + ONESPACE+"Purchases"+ " Reached.");
									couponDiscountInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
								}
								/*double currentQty = 0;
								for (DiscountInfo discountInfo : discounts) {
									List<ItemCodeInfo> items = discountInfo.getITEMCODEINFO();
									for (ItemCodeInfo itemCodeInfo : items) {
										for (PurchasedItems purchasedItems : couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS()) {
											if(itemCodeInfo.getITEMCODE().equalsIgnoreCase(purchasedItems.getITEMCODE())){
												
												currentQty += Double.parseDouble(purchasedItems.getQUANTITY());
											}
										}
									}
								}*/
								
							}else{
								if(iselegible){
									if(eachCouponObj.isCombineItemAttributes()){
										
										String desc = couponDiscountInfo.getDESCRIPTION();
										//if(desc.indexOf("against") != -1 && desc.indexOf(" reward") != -1) {
										
										Pattern p = Pattern.compile(Pattern.quote("up to") + "(.*?)" + Pattern.quote("item(s)"));
										Matcher m = p.matcher(desc);
										String setDesc = "";
										String finalDesc = "";
										String qty="1.0";
										String rewardsToCut = "";//returnarr[2];
										boolean replaceQty = false;
										/* while (m.find()) {
							            	qty = m.group(1);
							            	replaceQty = true;
							            	setDesc = desc.replace(m.group(1), ONESPACE+getActualNumber((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()))+ONESPACE);
							            }*/
										String itemsQty = StringUtils.substringBetween(desc,"up to ", ITEMS);
										
										/*if(itemsQty != null){
											replaceQty = true;
											logger.debug("itemsQty =="+itemsQty);
											qty = itemsQty;
											Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
											setDesc = desc.replace("up to "+itemsQty+ITEMS, "up to "+getActualNumber(replaceQtyDbl)+ITEMS);
										}else{*/
											//Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
											String discountQty = "";
											List<DiscountInfo> discountInfoLst = couponDiscountInfo.getDISCOUNTINFO();
											for (DiscountInfo discountInfo : discountInfoLst) {
												
												List<ItemCodeInfo> itemsLst = discountInfo.getITEMCODEINFO();
												for (ItemCodeInfo itemCodeInfo : itemsLst) {
													discountQty= itemCodeInfo.getQUANTITY();//*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits())+"");
													
												}
												
											}
											String[] returnarr = getReplacedQty(desc, balance, eachCouponObj.getRequiredLoyltyPoits(), null, discountQty, null );
											if(returnarr != null){
												
												replaceQty = (returnarr != null);
												qty = returnarr[0];
												setDesc = returnarr[1];
												rewardsToCut = returnarr[2];
												logger.debug("rewardsToCut ==="+rewardsToCut);
											}
											/*replaceQty = (returnarr != null);
							            	setDesc = returnarr[1];*/
										//}
										if(replaceQty){
											
											/*p = Pattern.compile(Pattern.quote("against") + "(.*?)" + Pattern.quote("Reward"));
							            	m = p.matcher(desc);
							            	while (m.find()) {
							            	}*/
											finalDesc = setDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+(!rewardsToCut.isEmpty() ? rewardsToCut : balance.getBalance())+ONESPACE);
											
											//String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"").
											//	replace(desc.substring(desc.indexOf("against")+"against".length(), desc.indexOf(" reward")), balance.getBalance()+"");
											logger.debug("desc==="+finalDesc);
											couponDiscountInfo.setDESCRIPTION(finalDesc);
											 discountInfoLst = couponDiscountInfo.getDISCOUNTINFO();
											for (DiscountInfo discountInfo : discountInfoLst) {
												
												List<ItemCodeInfo> itemsLst = discountInfo.getITEMCODEINFO();
												for (ItemCodeInfo itemCodeInfo : itemsLst) {
													itemCodeInfo.setQUANTITY(Double.parseDouble(qty)+"");//*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits())+"");
													
												}
												
											}
										}
									}
										//String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"").
											//	replace(desc.substring(desc.indexOf("against")+"against".length(), desc.indexOf(" reward")), balance.getBalance()+"");
										//rewardDiscountInfo.setDESCRIPTION(setDesc);
									//}	
								}
							}
							
						}
					}
					
				}else{
						if( eachCouponObj.getPurchaseQty() != null && eachCouponObj.getPurchaseQty()!=0) {
							//LoyaltyMemberItemQtyCounter memberqtyProgressBar = null;
							if(iselegible) {
								/*if(eachCouponObj.getSpecialRewadId() != null){
									
									List<LoyaltyMemberItemQtyCounter> retList = loyaltyMemberItemQtyCounterDao.findItemsCounter(eachCouponObj.getSpecialRewadId()+"", balance.getLoyaltyId());
									if(retList != null && !retList.isEmpty()){
										
										memberqtyProgressBar = retList.get(0); 
									}
									
								}*/
								double totalPurchases = balance.getBalance()*eachCouponObj.getPurchaseQty();
								if(memberqtyProgressBar != null && memberqtyProgressBar.getQty() != 0){
									
									totalPurchases += memberqtyProgressBar.getQty();
								}
								Double currentqty = 0.0;
								double multipleFactor = totalPurchases%eachCouponObj.getPurchaseQty();
								double totalfreeItems = (totalPurchases/eachCouponObj.getPurchaseQty())/eachCouponObj.getRequiredLoyltyPoits();
								if(multipleFactor > 0){// need to split the nudge
									currentqty += multipleFactor;
									
									/*CouponDiscountInfo splitToNudge = rewardDiscountInfo;
									splitToNudge.setNUDGEPROMOCODE("YES");
									splitToNudge.setNUDGEDESCRIPTION(currentqty+ " of "+eachCouponObj.getPurchaseQty() + ONESPACE+"Purchases"+ " Reached.");
									splitToNudge.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
									coupDiscInfoList.add(splitToNudge);*/
									splitToNudge = new CouponDiscountInfo(rewardDiscountInfo.getCOUPONNAME(), 
											rewardDiscountInfo.getCOUPONCODE(), rewardDiscountInfo.getCOUPONTYPE(), 
											rewardDiscountInfo.getACCUMULATEDISCOUNT(), rewardDiscountInfo.getVALIDFROM(), 
											rewardDiscountInfo.getVALIDTO(), rewardDiscountInfo.getDISCOUNTCRITERIA(), 
											rewardDiscountInfo.getLOYALTYPOINTS(), new ArrayList<DiscountInfo>());
									splitToNudge.setEXCLUDEDISCOUNTEDITEMS(rewardDiscountInfo.getEXCLUDEDISCOUNTEDITEMS());
									splitToNudge.setELIGIBILITY(rewardDiscountInfo.getELIGIBILITY());
									String splitNudgeDesc = rewardDiscountInfo.getDESCRIPTION();
									splitToNudge.setDESCRIPTION(splitNudgeDesc);
									/*Pattern patt = Pattern.compile(Pattern.quote("against") + "(.*?)" + Pattern.quote("Reward"));
					            	Matcher mat = patt.matcher(splitNudgeDesc);*/
					            	String splitNudgeDescNew = splitNudgeDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+eachCouponObj.getPurchaseQty().intValue()+"").replace("Reward type: "+eachCouponObj.getValueCode(), "Purchases");
					            	splitToNudge.setDESCRIPTION(splitNudgeDescNew);
//					            	while (mat.find()) {
//					            				splitNudgeDesc.replace(mat.group(1), ONESPACE+eachCouponObj.getPurchaseQty().intValue()+ONESPACE).replace("Reward type: "+eachCouponObj.getValueCode(), "Purchases");
//					            		logger.debug("splitNudgeDescNew==="+splitNudgeDescNew);
//					            	}
									splitToNudge.setAPPLYATTRIBUTES(rewardDiscountInfo.getAPPLYATTRIBUTES());
									splitToNudge.setLOYALTYVALUECODE(rewardDiscountInfo.getLOYALTYVALUECODE());
									
									splitToNudge.setNUDGEPROMOCODE("YES");
									//List<String>  appliedPromo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getAPPLIEDCOUPONS();
									logger.debug("appliedPromo == "+isRequestedFromNewPlugin +ONESPACE+appliedPromo!= null );
									if(isRequestedFromNewPlugin && appliedPromo!= null && !appliedPromo.isEmpty()){
										splitToNudge.setAPPLIEDCOUPONS("NO");
										/*for (String appliedCode : appliedPromo) {
											logger.debug("appliedCode ==="+appliedCode+" == "+splitToNudge.getCOUPONCODE());
											if(splitToNudge.getCOUPONCODE().equalsIgnoreCase(appliedCode)) {
												splitToNudge.setAPPLIEDCOUPONS("YES");
												break;
											}
										}//for
*/									}
									splitToNudge.setNUDGEDESCRIPTION(currentqty.intValue()+ " of "+eachCouponObj.getPurchaseQty().intValue() + ONESPACE+"Purchases"+ " Reached.");
									splitToNudge.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
									if(eachCouponObj.isCombineItemAttributes()){
										//coupDiscInfoList.add(splitToNudge);
										String desc = rewardDiscountInfo.getDESCRIPTION();
										//if(desc.indexOf("against") != -1 && desc.indexOf(" reward") != -1) {
											
											
											Pattern p = Pattern.compile(Pattern.quote("up to") + "(.*?)" + Pattern.quote("item(s)"));
								            Matcher m = p.matcher(desc);
								            String setDesc = "";
								            String finalDesc = "";
								            boolean replaceQty = false;
								            /*while (m.find()) {
								            	String qty = m.group(1);
								            	replaceQty = true;
								            	Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
								            	setDesc = desc.replace(m.group(1), ONESPACE+getActualNumber(replaceQtyDbl)+ONESPACE);
								            }*/
								            String itemsQty = StringUtils.substringBetween(desc,"up to ", ITEMS);
								            
								            if(itemsQty != null){
								            	replaceQty = true;
								            	logger.debug("itemsQty =="+itemsQty);
								            	String qty = itemsQty;
								            	Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
								            	setDesc = desc.replace("up to "+itemsQty+ITEMS, "up to "+getActualNumber(replaceQtyDbl)+ITEMS);
								            }else{
								            	//Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
								            	String[] returnarr = getReplacedQty(desc, balance, eachCouponObj.getRequiredLoyltyPoits(), null, null, null );
								            	if(returnarr != null){
								            		
								            		replaceQty = (returnarr != null);
								            		setDesc = returnarr[1];
								            	}
								            }
								            if(replaceQty){
								            	/*p = Pattern.compile(Pattern.quote("against") + "(.*?)" + Pattern.quote("Reward"));
								            	m = p.matcher(desc);
								            	while (m.find()) {
								            		finalDesc = setDesc.replace(m.group(1), ONESPACE+balance.getBalance()+ONESPACE);
								            	}*/
								            	finalDesc = setDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+balance.getBalance()+ONESPACE);
								            	logger.debug("desc==="+finalDesc);
								            	//String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"").
								            	//	replace(desc.substring(desc.indexOf("against")+"against".length(), desc.indexOf(" reward")), balance.getBalance()+"");
								            	rewardDiscountInfo.setDESCRIPTION(finalDesc);
								            	
								            }
										
									}
									//}
									/*String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"").
											replace(desc.substring(desc.indexOf("against")+8, desc.indexOf(" reward")), balance.getBalance()+"");
									rewardDiscountInfo.setDESCRIPTION(setDesc);*/
									/*List<DiscountInfo> discountInfoLst = couponDiscountInfo.getDISCOUNTINFO();
									for (DiscountInfo discountInfo : discountInfoLst) {
										
										List<ItemCodeInfo> itemsLst = discountInfo.getITEMCODEINFO();
										for (ItemCodeInfo itemCodeInfo : itemsLst) {
											itemCodeInfo.setQUANTITY(balance.getBalance()+"");
											
										}
										
									}*/
								}else if(totalfreeItems > eachCouponObj.getRequiredLoyltyPoits()){
									if(eachCouponObj.isCombineItemAttributes()){
										String desc = rewardDiscountInfo.getDESCRIPTION();
										//if(desc.indexOf("against") != -1 && desc.indexOf(" reward") != -1) {
											
											Pattern p = Pattern.compile(Pattern.quote("up to") + "(.*?)" + Pattern.quote("item(s)"));
								            Matcher m = p.matcher(desc);
								            String setDesc = "";
								            String finalDesc = "";
								            boolean replaceQty = false;
								           /* while (m.find()) {
								            	replaceQty = true;
								            	setDesc = desc.replace(m.group(1), ONESPACE+getActualNumber(totalfreeItems)+ONESPACE);
								            }*/
								            String itemsQty = StringUtils.substringBetween(desc,"up to ", ITEMS);
								            if(itemsQty != null){
								            	replaceQty = true;
								            	logger.debug("itemsQty =="+itemsQty);
								            	String qty = itemsQty;
								            	setDesc = desc.replace("up to "+itemsQty+ITEMS, "up to "+getActualNumber(totalfreeItems)+ITEMS);
								            }
								            else{
								            	//Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
								            	String[] returnarr = getReplacedQty(desc, null, null, totalfreeItems, null, null );
								            	if(returnarr != null){
								            		
								            		replaceQty = (returnarr != null);
								            		setDesc = returnarr[1];
								            	}
								            }
								            if(replaceQty){
								            	
								            	/*p = Pattern.compile(Pattern.quote("against") + "(.*?)" + Pattern.quote("Reward"));
								            	m = p.matcher(desc);
								            	while (m.find()) {
								            		finalDesc = setDesc.replace(m.group(1), ONESPACE+balance.getBalance()+ONESPACE);
								            	}*/
								            	finalDesc = setDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+balance.getBalance()+ONESPACE);
								            	logger.debug("desc==="+finalDesc);
								            	
								            	rewardDiscountInfo.setDESCRIPTION(finalDesc);
								            	
								            }
								    
									}
									        
										//String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"").
											//	replace(desc.substring(desc.indexOf("against")+"against".length(), desc.indexOf(" reward")), balance.getBalance()+"");
										
										/*String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), totalfreeItems+"").
												replace(desc.substring(desc.indexOf("against")+"against".length(), desc.indexOf(" reward")), balance.getBalance()+"");
										//rewardDiscountInfo.setDESCRIPTION(setDesc);
										rewardDiscountInfo.setDESCRIPTION(setDesc);*/
									//}
									//String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"")
										//	.replace(desc.substring(desc.indexOf("against")+8, desc.indexOf(" reward")), balance.getBalance()+"");
									
								}
								rewardDiscountInfo.setNUDGEPROMOCODE("YES");
								rewardDiscountInfo.setNUDGEDESCRIPTION(FREE_ITEM_NUDGE);
								if(isRequestedFromNewPlugin && appliedPromo!= null && !appliedPromo.isEmpty())rewardDiscountInfo.setAPPLIEDCOUPONS("NO");
								if(isApplied){
									rewardDiscountInfo.setAPPLIEDCOUPONS("YES");
									rewardDiscountInfo.setNUDGEPROMOCODE("NO");
									rewardDiscountInfo.setNUDGEDESCRIPTION("");
								}
								rewardDiscountInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
								List<DiscountInfo> discounts = new ArrayList<DiscountInfo>();
								rewardDiscountInfo.setDISCOUNTINFO(discounts);
								//List<String>  appliedPromo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getAPPLIEDCOUPONS();
								logger.debug("appliedPromo == "+isRequestedFromNewPlugin +ONESPACE+appliedPromo!= null );
								if(isRequestedFromNewPlugin && appliedPromo!= null && !appliedPromo.isEmpty()){
									//rewardDiscountInfo.setAPPLIEDCOUPONS("NO");
									/*for (String appliedCode : appliedPromo) {
										logger.debug("appliedCode ==="+appliedCode+" == "+rewardDiscountInfo.getCOUPONCODE());
										if(rewardDiscountInfo.getCOUPONCODE().equalsIgnoreCase(appliedCode)) {
											rewardDiscountInfo.setAPPLIEDCOUPONS("YES");
											break;
										}
									}//for
*/								}
								//couponDiscountInfo = rewardDiscountInfo;
								logger.debug("discount info added===");
								coupDiscInfoList.add(rewardDiscountInfo);
								if(splitToNudge != null)coupDiscInfoList.add(splitToNudge);
							}else{/*
								rewardDiscountInfo.setNUDGEPROMOCODE("YES");
								rewardDiscountInfo.setNUDGEDESCRIPTION((balance !=null ? balance.getBalance() : 0)+ " of "+eachCouponObj.getRequiredLoyltyPoits() + ONESPACE+eachCouponObj.getValueCode()+ " Reached.");
								rewardDiscountInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
							*/
								Double totalPurchases = 0.0;
								if(balance !=null && balance.getBalance() != null)totalPurchases = balance.getBalance()*eachCouponObj.getPurchaseQty();
								if(memberqtyProgressBar != null && memberqtyProgressBar.getQty() != 0 ){
									
									totalPurchases += memberqtyProgressBar.getQty();
									if(totalPurchases > 0){
										
										rewardDiscountInfo.setNUDGEPROMOCODE("YES");
										rewardDiscountInfo.setNUDGEDESCRIPTION((totalPurchases.intValue()) + " of "+eachCouponObj.getPurchaseQty().intValue() + ONESPACE+"Purchases"+ " Reached.");
										if(isRequestedFromNewPlugin && appliedPromo!= null && !appliedPromo.isEmpty())rewardDiscountInfo.setAPPLIEDCOUPONS("NO");
										
										if(isApplied){
											rewardDiscountInfo.setAPPLIEDCOUPONS("YES");
											rewardDiscountInfo.setNUDGEPROMOCODE("NO");
											rewardDiscountInfo.setNUDGEDESCRIPTION("");
										}
										List<DiscountInfo> discounts = new ArrayList<DiscountInfo>();
										rewardDiscountInfo.setDISCOUNTINFO(discounts);
										//List<String>  appliedPromo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getAPPLIEDCOUPONS();
										logger.debug("appliedPromo == "+isRequestedFromNewPlugin +ONESPACE+appliedPromo!= null );
										if(isRequestedFromNewPlugin && appliedPromo!= null && !appliedPromo.isEmpty()){
											//rewardDiscountInfo.setAPPLIEDCOUPONS("NO");
											/*for (String appliedCode : appliedPromo) {
												logger.debug("appliedCode ==="+appliedCode+" == "+rewardDiscountInfo.getCOUPONCODE());
												if(rewardDiscountInfo.getCOUPONCODE().equalsIgnoreCase(appliedCode)) {
													rewardDiscountInfo.setAPPLIEDCOUPONS("YES");
													break;
												}
											}//for
*/										}
										//couponDiscountInfo = rewardDiscountInfo;
										logger.debug("discount info added===");
										coupDiscInfoList.add(rewardDiscountInfo);
									}
								}
							}
														
						}else{
							if(iselegible){
								if(eachCouponObj.isCombineItemAttributes()){
									String desc = rewardDiscountInfo.getDESCRIPTION();
									logger.debug("desc==="+desc);
									//if(desc.indexOf("against") != -1 && desc.indexOf(" reward") != -1) {
										logger.debug("desc==="+desc);
										Pattern p = Pattern.compile(Pattern.quote("up to") + "(.*?)" + Pattern.quote("item(s)"));
							            Matcher m = p.matcher(desc);
							            boolean replaceQty = false;
							            String setDesc = "";
							            String finalDesc = "";
							            String rewardsToCut = "";
										
										
										
										
							           /* while (m.find()) {
							            	String qty = m.group(1);
							            	replaceQty = true;
							            	Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
							            	setDesc = desc.replace(m.group(1), ONESPACE+getActualNumber(replaceQtyDbl)+ONESPACE);
							            	//setDesc = desc.replace(m.group(1), ONESPACE+((Double.parseDouble(qty))*balance.getBalance())/eachCouponObj.getRequiredLoyltyPoits()+ONESPACE);
							            }*/
							            String itemsQty = StringUtils.substringBetween(desc,"up to ", ITEMS);
							            if(itemsQty != null){
							            	replaceQty = true;
							            	logger.debug("itemsQty =="+itemsQty);
							            	String qty = itemsQty;
							            	Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
							            	setDesc = desc.replace("up to "+itemsQty+ITEMS, "up to "+getActualNumber(replaceQtyDbl)+ITEMS);
							            }else{
							            	//Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
							            	String[] returnarr = getReplacedQty(desc, balance, eachCouponObj.getRequiredLoyltyPoits(), null, null, null );
							            	if(returnarr != null){
							            		
							            		replaceQty = (returnarr != null);
							            		setDesc = returnarr[1];
							            		rewardsToCut = returnarr[2];
							            	}
							            }
							            if(replaceQty) {
							            	
							            	/*p = Pattern.compile(Pattern.quote("against") + "(.*?)" + Pattern.quote("Reward"));
							            	m = p.matcher(desc);
							            	while (m.find()) {
							            		finalDesc = setDesc.replace(m.group(1), ONESPACE+balance.getBalance()+ONESPACE);
							            	}*/
							            	finalDesc = setDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+(!rewardsToCut.isEmpty() ? rewardsToCut : balance.getBalance())+ONESPACE);
							            	//finalDesc = setDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+balance.getBalance()+ONESPACE);
							            	rewardDiscountInfo.setDESCRIPTION(finalDesc);
							            	logger.debug("desc==="+finalDesc);
							            }
							        
								}
								    
									//String setDesc = desc.replace(desc.substring(desc.indexOf("up to")+5, desc.indexOf("item(s)")), balance.getBalance()+"").
										//	replace(desc.substring(desc.indexOf("against")+"against".length(), desc.indexOf(" reward")), balance.getBalance()+"");
								//}
								/*List<DiscountInfo> discountInfoLst = couponDiscountInfo.getDISCOUNTINFO();
								for (DiscountInfo discountInfo : discountInfoLst) {
									
									List<ItemCodeInfo> itemsLst = discountInfo.getITEMCODEINFO();
									for (ItemCodeInfo itemCodeInfo : itemsLst) {
										itemCodeInfo.setQUANTITY(balance.getBalance()+"");
										
									}
									
								}*/
								rewardDiscountInfo.setNUDGEPROMOCODE("YES");
								rewardDiscountInfo.setNUDGEDESCRIPTION(FREE_ITEM_NUDGE);
								if(isRequestedFromNewPlugin && appliedPromo!= null && !appliedPromo.isEmpty())rewardDiscountInfo.setAPPLIEDCOUPONS("NO");
								if(isApplied){
									rewardDiscountInfo.setAPPLIEDCOUPONS("YES");
									rewardDiscountInfo.setNUDGEPROMOCODE("NO");
									rewardDiscountInfo.setNUDGEDESCRIPTION("");
								}
								rewardDiscountInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
								List<DiscountInfo> discounts = new ArrayList<DiscountInfo>();
								rewardDiscountInfo.setDISCOUNTINFO(discounts);
								//List<String>  appliedPromo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getAPPLIEDCOUPONS();
								logger.debug("appliedPromo == "+isRequestedFromNewPlugin +ONESPACE+appliedPromo!= null );
								if(isRequestedFromNewPlugin && appliedPromo!= null && !appliedPromo.isEmpty()){
									//rewardDiscountInfo.setAPPLIEDCOUPONS("NO");
									/*for (String appliedCode : appliedPromo) {
										logger.debug("appliedCode ==="+appliedCode+" == "+rewardDiscountInfo.getCOUPONCODE());
										if(rewardDiscountInfo.getCOUPONCODE().equalsIgnoreCase(appliedCode)) {
											rewardDiscountInfo.setAPPLIEDCOUPONS("YES");
											break;
										}
									}//for
*/								}
								//couponDiscountInfo = rewardDiscountInfo;
								logger.debug("discount info added===");
								coupDiscInfoList.add(rewardDiscountInfo);
							}else{/*
								
								rewardDiscountInfo.setNUDGEPROMOCODE("YES");
								rewardDiscountInfo.setNUDGEDESCRIPTION((balance !=null ? balance.getBalance() : 0)+ " of "+eachCouponObj.getRequiredLoyltyPoits() + ONESPACE+eachCouponObj.getValueCode()+ " Reached.");
								rewardDiscountInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
							*/}
							
						}
					}
					
				}
			else{//onrecpt
				if(couponDiscountInfo !=null) {
					List<DiscountInfo> discounts = couponDiscountInfo.getDISCOUNTINFO();
					if(iselegible && (discounts == null || discounts.size() == 0)) {
						couponDiscountInfo.setNUDGEPROMOCODE("YES");
						couponDiscountInfo.setNUDGEDESCRIPTION(RECEIPT_NUDGE );
						couponDiscountInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
					}else if(!iselegible && discounts != null && !discounts.isEmpty()){
						
						couponDiscountInfo.setNUDGEPROMOCODE("YES");
						couponDiscountInfo.setNUDGEDESCRIPTION((balance !=null ? balance.getBalance() : 0)+ " of "+eachCouponObj.getRequiredLoyltyPoits() + ONESPACE+eachCouponObj.getValueCode()+ " Reached.");
						couponDiscountInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
					}
					
				}else{
					if(iselegible ) {
						
						rewardDiscountInfo.setNUDGEPROMOCODE("YES");
						rewardDiscountInfo.setNUDGEDESCRIPTION(RECEIPT_NUDGE );
						List<DiscountInfo> discounts = new ArrayList<DiscountInfo>();
						rewardDiscountInfo.setDISCOUNTINFO(discounts);
						couponDiscountInfo = rewardDiscountInfo;
						
					}/*else{
						rewardDiscountInfo.setNUDGEPROMOCODE("YES");
						rewardDiscountInfo.setNUDGEDESCRIPTION((balance !=null ? balance.getBalance() : 0)+ " of "+eachCouponObj.getRequiredLoyltyPoits() + ONESPACE+eachCouponObj.getValueCode()+ " Reached.");
						
					}*/
				}
				
			}
				
			
		}// end if
		
		
		if(couponDiscountInfo != null) {
			
			//give dynamic description for loyalty type DCs
			List<DiscountInfo> discountInfoLst = couponDiscountInfo.getDISCOUNTINFO();
			if(isRequestedFromNewPlugin && eachCouponObj.isCombineItemAttributes() && couponDiscountInfo.getCOUPONTYPE().equals("LOYALTY")  
					){
				if(eachCouponObj.isCombineItemAttributes()){
					String desc = couponDiscountInfo.getDESCRIPTION();
					logger.debug("desc==="+desc);
			            boolean replaceQty = false;
			            String setDesc = "";
			            String finalDesc = "";
			            String rewardsToCut = "";
			            String itemsQty = StringUtils.substringBetween(desc,"up to ", ITEMS);
			            /*if(itemsQty != null){
			            	replaceQty = true;
			            	logger.debug("itemsQty =="+itemsQty);
			            	String qty = itemsQty;
			            	Double replaceQtyDbl = ((Double.parseDouble(qty))*(pointsBalance/eachCouponObj.getRequiredLoyltyPoits()));
			            	logger.debug("itemsQty =="+replaceQtyDbl);
			            	setDesc = desc.replace("up to "+itemsQty+ITEMS, "up to "+getActualNumber(replaceQtyDbl)+ITEMS);*/
			            //}else{
			            	if(discountInfoLst != null && !discountInfoLst.isEmpty()){
			            		
			            		String discountQty = "";
			            		
			            		for (DiscountInfo discountInfo : discountInfoLst) {
			            			
			            			List<ItemCodeInfo> itemsLst = discountInfo.getITEMCODEINFO();
			            			for (ItemCodeInfo itemCodeInfo : itemsLst) {
			            				discountQty= itemCodeInfo.getQUANTITY();//*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits())+"");
			            				
			            			}
			            			
			            		}
			            		//Double replaceQtyDbl = ((Double.parseDouble(qty))*(balance.getBalance()/eachCouponObj.getRequiredLoyltyPoits()));
			            		String[] returnarr = getReplacedQty(desc, balance, eachCouponObj.getRequiredLoyltyPoits(), null, discountQty, pointsBalance.longValue() );
			            		if(returnarr != null){
			            			
			            			replaceQty = (returnarr != null);
			            			setDesc = returnarr[1];
			            			rewardsToCut = returnarr[2];
			            			
			            			
			            		}
			            	}else{
			            		String[] returnarr = getReplacedQty(desc, balance, eachCouponObj.getRequiredLoyltyPoits(), null, null, pointsBalance.longValue()  );
				            	if(returnarr != null){
				            		
				            		replaceQty = (returnarr != null);
				            		setDesc = returnarr[1];
				            		rewardsToCut = returnarr[2];
				            	}
			            	}
			            //}
			            if(replaceQty) {
			            	
			            	/*p = Pattern.compile(Pattern.quote("against") + "(.*?)" + Pattern.quote("Reward"));
			            	m = p.matcher(desc);
			            	while (m.find()) {
			            		finalDesc = setDesc.replace(m.group(1), ONESPACE+balance.getBalance()+ONESPACE);
			            	}*/
			            	finalDesc = setDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+(!rewardsToCut.isEmpty() ? rewardsToCut :pointsBalance.longValue())+ONESPACE);
			            	//finalDesc = setDesc.replace("against "+eachCouponObj.getRequiredLoyltyPoits(), "against "+pointsBalance+ONESPACE);
			            	couponDiscountInfo.setDESCRIPTION(finalDesc);
			            	logger.debug("desc==="+finalDesc);
			            }
			        
				}
			}
			//List<String>  appliedPromo = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getAPPLIEDCOUPONS();
			logger.debug("logger to check==="+couponDiscountInfo.getCOUPONCODE()+ONESPACE+couponDiscountInfo.getNUDGEPROMOCODE()+ONESPACE+couponDiscountInfo.getAPPLIEDCOUPONS());
			logger.debug("appliedPromo == "+isRequestedFromNewPlugin +ONESPACE+appliedPromo!= null );
			boolean isapplied = false;
			if(isRequestedFromNewPlugin && appliedPromo!= null && !appliedPromo.isEmpty()){
				couponDiscountInfo.setAPPLIEDCOUPONS("NO");
				for (String appliedCode : appliedPromo) {
					logger.debug("appliedCode ==="+appliedCode+" == "+couponDiscountInfo.getCOUPONCODE());
					if(couponDiscountInfo.getCOUPONCODE().equalsIgnoreCase(appliedCode)) {
						couponDiscountInfo.setAPPLIEDCOUPONS("YES");
						if(couponDiscountInfo.getNUDGEPROMOCODE().equals("YES")){
							couponDiscountInfo.setNUDGEPROMOCODE("NO");
							couponDiscountInfo.setNUDGEDESCRIPTION("");
						}
						isapplied = true;
						break;
					}
				}//for
			}
			if(couponDiscountInfo!=null && couponDiscountInfo.getDISCOUNTINFO()!= null && couponDiscountInfo.getDISCOUNTINFO().isEmpty() ){
				/*if(couponDiscountInfo.getNUDGEPROMOCODE().equals("YES")){
					if(isapplied){
						logger.debug("for applied coupon =="+couponDiscountInfo.getCOUPONCODE()+ONESPACE);
						
						CouponDiscountInfo appliedDiscInfo = new CouponDiscountInfo(couponDiscountInfo.getCOUPONNAME(), 
								couponDiscountInfo.getCOUPONCODE(), couponDiscountInfo.getCOUPONTYPE(), 
								couponDiscountInfo.getACCUMULATEDISCOUNT(), couponDiscountInfo.getVALIDFROM(), 
								couponDiscountInfo.getVALIDTO(), couponDiscountInfo.getDISCOUNTCRITERIA(), 
								couponDiscountInfo.getLOYALTYPOINTS(), new ArrayList<DiscountInfo>());
						appliedDiscInfo.setEXCLUDEDISCOUNTEDITEMS(couponDiscountInfo.getEXCLUDEDISCOUNTEDITEMS());
						appliedDiscInfo.setELIGIBILITY(couponDiscountInfo.getELIGIBILITY());
						appliedDiscInfo.setDESCRIPTION(couponDiscountInfo.getDESCRIPTION());
						appliedDiscInfo.setAPPLYATTRIBUTES(couponDiscountInfo.getAPPLYATTRIBUTES());
						appliedDiscInfo.setLOYALTYVALUECODE(couponDiscountInfo.getLOYALTYVALUECODE());
						appliedDiscInfo.setNUDGEPROMOCODE("NO");
						appliedDiscInfo.setAPPLIEDCOUPONS("YES");
						
						appliedDiscInfo.setNUDGEDESCRIPTION("");
						appliedDiscInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
						
						coupDiscInfoList.add(appliedDiscInfo);
						couponDiscountInfo.setAPPLIEDCOUPONS("NO");
					}
					logger.debug("adding ==1");
					
				}*/
				coupDiscInfoList.add(couponDiscountInfo);
				return coupDiscInfoList;
			}
			if(isapplied){/*
				if(couponDiscountInfo.getNUDGEPROMOCODE().equals("YES")){
					
					CouponDiscountInfo appliedDiscInfo = new CouponDiscountInfo(couponDiscountInfo.getCOUPONNAME(), 
							couponDiscountInfo.getCOUPONCODE(), couponDiscountInfo.getCOUPONTYPE(), 
							couponDiscountInfo.getACCUMULATEDISCOUNT(), couponDiscountInfo.getVALIDFROM(), 
							couponDiscountInfo.getVALIDTO(), couponDiscountInfo.getDISCOUNTCRITERIA(), 
							couponDiscountInfo.getLOYALTYPOINTS(), new ArrayList<DiscountInfo>());
					appliedDiscInfo.setEXCLUDEDISCOUNTEDITEMS(couponDiscountInfo.getEXCLUDEDISCOUNTEDITEMS());
					appliedDiscInfo.setELIGIBILITY(couponDiscountInfo.getELIGIBILITY());
					appliedDiscInfo.setDESCRIPTION(couponDiscountInfo.getDESCRIPTION());
					appliedDiscInfo.setAPPLYATTRIBUTES(couponDiscountInfo.getAPPLYATTRIBUTES());
					appliedDiscInfo.setLOYALTYVALUECODE(couponDiscountInfo.getLOYALTYVALUECODE());
					appliedDiscInfo.setNUDGEPROMOCODE("NO");
					appliedDiscInfo.setAPPLIEDCOUPONS("YES");
					
					appliedDiscInfo.setNUDGEDESCRIPTION("");
					appliedDiscInfo.setDISCOUNTINFO(new ArrayList<DiscountInfo>());
					couponDiscountInfo.setAPPLIEDCOUPONS("NO");
					logger.debug("adding ==2");
					coupDiscInfoList.add(appliedDiscInfo);
				}
			*/}
			/*if(couponDiscountInfo.getDISCOUNTINFO()!= null &&couponDiscountInfo.getDISCOUNTINFO().isEmpty()
					&& ((isreward && couponDiscountInfo.getNUDGEPROMOCODE().equals("YES"))  )) {
				coupDiscInfoList.add(couponDiscountInfo);
				return coupDiscInfoList;
			}*/
			//String test = temp.toJson(couponDiscountInfo);
			//logger.info(" discount json is "+test);
			logger.debug("added prmo=="+eachCouponObj.getCouponCode());
			logger.debug("adding ==3");
			if(couponDiscountInfo!=null)
			coupDiscInfoList.add(couponDiscountInfo);
			
			if(splitToNudge != null) {
				//if(isapplied )splitToNudge.setAPPLIEDCOUPONS("NO");
				logger.debug("adding ==4");
				coupDiscInfoList.add(splitToNudge);
			}
		}
		}else{
			couponDiscountInfo = getDiscountAmount(couponCodeEnquObj, eachCouponObj, isItemCode, 
					couponDiscountInfo, coupDiscInfoList,disCountType,isOcLoyalty, user,  conLty );
			if(couponDiscountInfo != null ) coupDiscInfoList.add(couponDiscountInfo);
		}
		//		logger.debug("-------exit  setCoupDiscInfoObj---------"+coupDiscInfoList);
		
		return coupDiscInfoList;
	}//setCoupDiscInfoObjNewPlugin

	private CouponDiscountInfo getLoyaltyPromoInfo(CouponCodeEnquObj couponCodeEnquObj,Coupons coupObj,
			boolean isItemCode,CouponDiscountInfo couponDiscountInfo,List<CouponDiscountInfo> coupDiscInfoList,
			String disCountType, boolean isOcLoyalty, Users user, Map<String, String> posMappingMap, 
			boolean isRequestedFromNewPlugin, ContactsLoyalty contactsLoyaltyObj) throws BaseServiceException {
		
		try {
			
			CouponDiscountGenerateDao coupDiscGenDao  = (CouponDiscountGenerateDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
				if(coupObj.isCombineItemAttributes()) {
					List<Object[]> retList = coupDiscGenDao.findDistinctAttrCombos(coupObj.getCouponId());
					if(retList == null || retList.isEmpty())return null;
					if(retList != null && !retList.isEmpty()) {
						String comboQuery = Constants.STRING_NILL;
						Double discount = null;
						Double maxDiscount = null;
						String shippingFee = null;
						String cardSetId = null;
						String programID = null;
						String tierID =null;
						//String shippingFeeFree = null;
						String shippingFeeType = null;
						String quantity = null;
						String quantityCriteria = null;
						List<ItemAttribute> itemAttrLst = new ArrayList<ItemAttribute>();
						for (Object[] objects : retList) {
							
							String skuAttribute = (String)objects[0];
							String skuAttributeValue = (String)objects[1];
							
							if(discount == null)discount = objects[2] != null ? (Double)objects[2] : null;
							if(shippingFee == null) shippingFee = objects[10] != null ? objects[10].toString() : null;
							if(shippingFeeType == null) shippingFeeType = objects[11] != null ? objects[11].toString() : null;
							if(programID == null) programID = objects[12] != null ? objects[12].toString() : null;
							if(tierID == null) tierID = objects[13] != null ? objects[13].toString() : null;
							if(cardSetId == null) cardSetId = objects[14] != null ? objects[14].toString() : null;
							if((programID != null && !programID.isEmpty() &&( (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramId().toString().equals(programID)) || (programID != null && contactsLoyaltyObj == null)))
									|| (tierID !=null && !tierID.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramTierId().toString().equals(tierID))) ||
									(cardSetId != null && !cardSetId.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getCardSetId().toString().equals(cardSetId))) ){
								logger.debug("elegibility on tier + cardset are failed");
								return null;
							}
												
							
							//if(shippingFeeFree == null) shippingFeeFree = objects[12] != null ? objects[12].toString() : null;
							if(maxDiscount == null)maxDiscount = objects[3] != null ? (Double)objects[3] : null;
							
							if(quantity == null)quantity = (String)objects[4];
							if(quantityCriteria == null) quantityCriteria = (String)objects[5];
								
							if(skuAttributeValue != null && !skuAttributeValue.isEmpty() ) {
								
								comboQuery += " AND "+( Utility.CDGAttrToSKUMap.containsKey(skuAttribute) ? Utility.CDGAttrToSKUMap.get(skuAttribute) : skuAttribute )+" = '"+skuAttributeValue+"'";
							}
							
							ItemAttribute itemAttr = new ItemAttribute();
							itemAttr.setITEMATTRIBUTE(posMappingMap.get(skuAttribute));
							itemAttr.setATTRIBUTECODE(skuAttributeValue);
							itemAttrLst.add(itemAttr);
						}
						List<SkuFile> itemsUnderDiscount = coupDiscGenDao.findDiscountedItems(Constants.STRING_NILL, user.getUserId(), comboQuery);
						if(itemsUnderDiscount == null || itemsUnderDiscount.isEmpty()) return null;
						
						//since in combination(AND) rule there would be only one discount
						List<DiscountInfo> discountInfoList = new ArrayList<DiscountInfo>();
						List<ItemCodeInfo> itemsForThisDiscount = new ArrayList<ItemCodeInfo>();
						
						DiscountInfo discountInfo = new DiscountInfo();
						discountInfo.setVALUE(discount != null ? discount+ Constants.STRING_NILL : Constants.STRING_NILL);
						discountInfo.setVALUECODE(disCountType);
						discountInfo.setMINPURCHASEVALUE(Constants.STRING_NILL);
						if(isRequestedFromNewPlugin) {
							discountInfo.setSHIPPINGFEE(shippingFee != null ? shippingFee : "");
							//discountInfo.setSHIPPINGFEEFREE(shippingFeeFree != null ? shippingFeeFree : "");
							discountInfo.setSHIPPINGFEETYPE(shippingFeeType != null ? shippingFeeType :"");
							discountInfo.setMAXRECEIPTDISCOUNT(Constants.STRING_NILL);
							discountInfo.setRECEIPTDISCOUNT(Constants.STRING_NILL);
						}
						for (SkuFile sku : itemsUnderDiscount) {
							ItemCodeInfo itemcodeInfo = new ItemCodeInfo();
							itemcodeInfo.setITEMCODE(sku.getItemSid());
							if(isRequestedFromNewPlugin) {
								
								itemcodeInfo.setITEMDISCOUNT(discount == null ? Constants.STRING_NILL : discount+Constants.STRING_NILL);
								itemcodeInfo.setMAXITEMDISCOUNT(maxDiscount == null ? Constants.STRING_NILL : maxDiscount+ Constants.STRING_NILL);
								itemcodeInfo.setQUANTITY(quantity == null ? Constants.STRING_NILL : quantity );
								itemcodeInfo.setQUANTITYCRITERIA(quantityCriteria == null ? Constants.STRING_NILL : Utility.limitQuantityMap.get(quantityCriteria));
								itemcodeInfo.setITEMATTRIBUTE(itemAttrLst);
								String rewardRatio = "";
								logger.debug("quantity=="+quantity);
								if(quantity != null && !quantity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
									rewardRatio = quantity+"-"+coupObj.getRequiredLoyltyPoits(); 
								}
								logger.debug("rewardRatio=="+rewardRatio);
								itemcodeInfo.setREWARDRATIO(rewardRatio);
								
							}
							itemsForThisDiscount.add(itemcodeInfo);
							
						}
						logger.debug("itemsForThisDiscount===="+itemsForThisDiscount.size());
						discountInfo.setITEMCODEINFO(itemsForThisDiscount);
						discountInfoList.add(discountInfo);
						
						couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
						
					}
					
				}//AND - combination rule
				else{ // OR - multiple rule
					List<String> retList = coupDiscGenDao.findDistinctAttr(coupObj.getCouponId());
					List<Double> retDiscList = coupDiscGenDao.findDistinctCoupDiscount(coupObj.getCouponId());

					logger.info("coupObj ::"+coupObj.getCouponName());
					logger.info("retDiscList ::"+retDiscList!=null?retDiscList.size():"null");
					logger.info("retList ::"+retList!=null?retList.size():"null");
					//long deleted = couponDiscountGenerateDaoForDML.deleteTempPromoDumpBy(coupObj.getCouponId(), user.getUserId());
					List<CouponDiscountGeneration> coupDisList = new ArrayList<CouponDiscountGeneration>();
					for (String CDGAttr : retList) {

						List<CouponDiscountGeneration> tempcoupDisList = coupDiscGenDao.findBy(coupObj.getCouponId(), user.getUserId(), 
								Utility.CDGAttrToSKUMap.containsKey(CDGAttr) ? Utility.CDGAttrToSKUMap.get(CDGAttr) : CDGAttr, CDGAttr);
						if(tempcoupDisList != null && !tempcoupDisList.isEmpty()) {

							coupDisList.addAll(tempcoupDisList);
						}

					}//for
					List<CouponDiscountGeneration> finalDiscList = null;
					List<DiscountInfo> discountInfoList = new ArrayList<DiscountInfo>();
					for (Double discount : retDiscList) {
						logger.info("discount ::"+discount);
						String shippingFee = Constants.STRING_NILL;
						//String shippingFeeFree = Constants.STRING_NILL;
						String shippingFeeType = Constants.STRING_NILL;
						List<ItemCodeInfo> itemCodeInfoList = new ArrayList<ItemCodeInfo>();
						for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
							logger.info("discount :: "+discount+" coupDisGenObj ::"+coupDisGenObj.getDiscount());
							if(discount.doubleValue() != coupDisGenObj.getDiscount().doubleValue()) continue;
							String programID = coupDisGenObj.getProgram() != null ? coupDisGenObj.getProgram().toString() : Constants.STRING_NILL;
							String tierID = coupDisGenObj.getTierNum() != null ? coupDisGenObj.getTierNum().toString() : Constants.STRING_NILL;
							String cardSetId = coupDisGenObj.getCardSetNum() != null ? coupDisGenObj.getCardSetNum().toString() : Constants.STRING_NILL;
							if((programID != null && !programID.isEmpty() &&( (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramId().toString().equals(programID)) || (programID != null && contactsLoyaltyObj == null)))
									|| (tierID !=null && !tierID.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramTierId().toString().equals(tierID))) ||
									(cardSetId != null && !cardSetId.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getCardSetId().toString().equals(cardSetId))) ){
								logger.debug("elegibility on tier + cardset are failed");
								continue;
							}
							shippingFee = coupDisGenObj.getShippingFee() == null ? Constants.STRING_NILL : coupDisGenObj.getShippingFee();
							shippingFeeType = coupDisGenObj.getShippingFeeType() == null ? Constants.STRING_NILL : coupDisGenObj.getShippingFeeType();
							//shippingFeeFree = coupDisGenObj.getShippingFeeFree() == null ? Constants.STRING_NILL : coupDisGenObj.getShippingFeeFree();
							//if(!discount.equals(coupDisGenObj.getDiscount())) continue;
							
							logger.info("discount :: "+discount+" coupDisGenObj ::"+coupDisGenObj.getDiscount());
							
							ItemCodeInfo itemcodeInfo = new ItemCodeInfo();
							itemcodeInfo.setITEMCODE(coupDisGenObj.getItemCategory());
							

							itemCodeInfoList.add(itemcodeInfo);
						}

						logger.info("itemCodeInfoList :"+itemCodeInfoList!=null?itemCodeInfoList.size():"NULL");
						if(!itemCodeInfoList.isEmpty()) {

							DiscountInfo discountInfo = new DiscountInfo();
							//discountInfo.setMAXRECEIPTDISCOUNT("");

							discountInfo.setMINPURCHASEVALUE("");
							//discountInfo.setRECEIPTDISCOUNT("");
							discountInfo.setVALUE(discount.toString());
							discountInfo.setVALUECODE(disCountType);
							//discountInfo.setEXCLUDEDISCOUNTEDITEMS(coupObj.isExcludeItems() ? "True" : "False");
							discountInfo.setSHIPPINGFEE(shippingFee == null ? "" : shippingFee);
							//discountInfo.setSHIPPINGFEEFREE(shippingFeeFree == null ? "" : shippingFeeFree );
							discountInfo.setSHIPPINGFEETYPE(shippingFeeType == null ?  "" : shippingFeeType);
							discountInfo.setITEMCODEINFO(itemCodeInfoList );
							discountInfoList.add(discountInfo);

						}

					}
					if(discountInfoList.isEmpty()) return null;
					couponDiscountInfo.setDISCOUNTINFO(discountInfoList);


				}//end -OR
				
				
				
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
		}
		return couponDiscountInfo;
	}
	private CouponDiscountInfo getDiscountAmountNewPlugin(CouponCodeEnquObj couponCodeEnquObj,Coupons coupObj, 
			boolean isItemCode,CouponDiscountInfo couponDiscountInfo,List<CouponDiscountInfo> coupDiscInfoList,
			String disCountType, boolean isOcLoyalty, Users user, Map<String, String> posMappingMap, 
			boolean isRequestedFromNewPlugin, LoyaltyBalance balance, Double pointsBalance, ContactsLoyalty contactsLoyaltyObj) throws BaseServiceException {
		try {
			logger.debug("-------entered getDiscountAmount---------"+coupObj.getCouponName()+ONESPACE+couponDiscountInfo.getCOUPONCODE());
			List<PurchasedItems> purchaseList=couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS();
			CouponDiscountGenerateDao couponDiscountGenerateDao=(CouponDiscountGenerateDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
			//SkuFileDao skuFileDao = (SkuFileDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SKU_FILE_DAO);
			boolean isReward = coupObj.getValueCode() != null && !coupObj.getValueCode().equals(OCConstants.LOYALTY_POINTS);
			
			boolean noReceiptData = false;
			if(isRequestedFromNewPlugin){
				
				noReceiptData =( couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT().trim().isEmpty() ||
						Double.parseDouble(couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT().trim()) == 0) &&
								couponCodeEnquObj.getCOUPONCODEENQREQ().getPURCHASEDITEMS().isEmpty();
				
			}
			//List<SkuFile> skuFilesList = null;
			if(!isOcLoyalty){//Regular Promotion
				logger.debug("-------calling smartPromotion----");
				return smartPromotion( couponCodeEnquObj, coupObj, isItemCode, couponDiscountInfo, coupDiscInfoList,
						disCountType, user,  purchaseList, posMappingMap, isRequestedFromNewPlugin, balance, pointsBalance, contactsLoyaltyObj);
			}else{//OC-Loyalty
				List<CouponDiscountGeneration>  coupDisList =null;
				CouponDiscountGenerateDao coupDiscGenDao  = (CouponDiscountGenerateDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
				//CouponDiscountGenerateDaoForDML couponDiscountGenerateDaoForDML  = (CouponDiscountGenerateDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO_FOR_DML);
				String coupCodeStr = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCOUPONCODE();
				if(isItemCode){
					
					
					if(!isRequestedFromNewPlugin && coupCodeStr.equalsIgnoreCase("OC-LOYALTY")) {
					
						
						return getLoyaltyPromoInfo(couponCodeEnquObj, coupObj, isItemCode, couponDiscountInfo, 
								coupDiscInfoList, disCountType, isOcLoyalty, user, posMappingMap, isRequestedFromNewPlugin, contactsLoyaltyObj);
						
						//return couponDiscountInfo;
					}
					if((!isReward && noReceiptData) && (purchaseList == null || purchaseList.isEmpty())) return null;
					
					String receiptAmount = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT();//!=null ? couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT().trim().isEmpty()?null:Double.parseDouble(couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT().trim()):null;

					if(isRequestedFromNewPlugin && (receiptAmount == null || receiptAmount.trim().isEmpty() || Double.parseDouble(receiptAmount.trim()) <= 0)) {
						logger.debug("==receiptAmount is null returning==");
						return null;
					}
					
					String itemCodeStr = Constants.STRING_NILL;
					List<PurchasedItems> ItemsList = new ArrayList<PurchasedItems>();
					for (PurchasedItems tempObj : purchaseList) {

						//Double totalItemPrice=null;

						Double itemDiscount = tempObj.getITEMDISCOUNT()!=null ? tempObj.getITEMDISCOUNT().trim().isEmpty()?null:Double.parseDouble(tempObj.getITEMDISCOUNT()):null;
						//int itemQuantity = tempObj.getQUANTITY()!=null ? tempObj.getQUANTITY().trim().isEmpty()?null:Integer.parseInt(tempObj.getQUANTITY()):null;
						//Double itemPrice = tempObj.getITEMPRICE()!=null &&  !tempObj.getITEMPRICE().trim().isEmpty()? Double.parseDouble(tempObj.getITEMPRICE()) : 0;

						//itemCode_PurchasedItemsMap.put(tempObj.getITEMCODE(),tempObj);

						if(coupObj.isExcludeItems() != null && coupObj.isExcludeItems()) {
							if(itemDiscount!=null && itemDiscount>0){
								//DiscountItem
								//discountItemCodeSet.add(tempObj.getITEMCODE());
								continue;
							}

						}
						ItemsList.add(tempObj);
						//if(itemCodeStr.trim().length() > 0) itemCodeStr += Constants.DELIMETER_COMMA;
						//itemCodeStr += tempObj.getITEMCODE();

						if(itemCodeStr.trim().length() > 0) itemCodeStr += Constants.DELIMETER_COMMA;
						itemCodeStr += "'"+tempObj.getITEMCODE()+"'";
						

						/*else {
									//Item with out discount
									nonDiscountItemCodeSet_ItemPrice.put(tempObj.getITEMCODE(),Double.parseDouble(tempObj.getITEMPRICE()));
								}*/
					} //for
					
					logger.info("itemCodeStr :"+itemCodeStr);
					
					if((!isReward && noReceiptData) && itemCodeStr.isEmpty() ) return null;
					if(coupObj.isCombineItemAttributes()) {
						List<Object[]> retList = couponDiscountGenerateDao.findDistinctAttrCombos(coupObj.getCouponId());
						if(retList == null || retList.isEmpty())return null;
						if(retList != null && !retList.isEmpty()) {
							String comboQuery = Constants.STRING_NILL;
							Double discount = null;
							String shippingFee = null;
							//String shippingFeeFree = null;
							String shippingFeeType = null;
							String cardSetId = null;
							String programID = null;
							String tierID =null;
							Double maxDiscount = null;
							String quantity = null;
							String quantityCriteria = null;
							Double MPV = null;
							String noOfElegibleItems = null;
							Double ItemPrice = null;
							String ItemPriceCriteria = null;
							List<ItemAttribute> itemAttrLst = new ArrayList<ItemAttribute>();
							for (Object[] objects : retList) {
								
								String skuAttribute = (String)objects[0];
								String skuAttributeValue = (String)objects[1];
								
								if(discount == null)discount = objects[2] != null ? (Double)objects[2] : null;
								if(shippingFee == null) shippingFee = objects[10] != null ? objects[10].toString() : null;
								if(shippingFeeType == null) shippingFeeType = objects[11] != null ? objects[11].toString() : null;
								//if(shippingFeeFree == null) shippingFeeFree = objects[12] != null ? objects[12].toString() : null;
								if(programID == null) programID = objects[12] != null ? objects[12].toString() : null;
								if(tierID == null) tierID = objects[13] != null ? objects[13].toString() : null;
								if(cardSetId == null) cardSetId = objects[14] != null ? objects[14].toString() : null;
								if(maxDiscount == null)maxDiscount = objects[3] != null ? (Double)objects[3] : null;
								
								if(quantity == null)quantity = (String)objects[4];
								if(quantityCriteria == null) quantityCriteria = (String)objects[5];
								if(noOfElegibleItems == null) noOfElegibleItems = (String)objects[6];
								if(ItemPrice == null) ItemPrice = objects[7] != null ? (Double)objects[7] : null;
								if(ItemPriceCriteria == null) ItemPriceCriteria = (String)objects[8] ;
								if(MPV == null ) MPV = objects[9] != null ? ((Long)objects[9]).doubleValue() : null;
								
									
								if(skuAttributeValue != null && !skuAttributeValue.isEmpty() ) {
									
									comboQuery += " AND "+( Utility.CDGAttrToSKUMap.containsKey(skuAttribute) ? Utility.CDGAttrToSKUMap.get(skuAttribute) : skuAttribute )+" = '"+skuAttributeValue+"'";
								}
								
								ItemAttribute itemAttr = new ItemAttribute();
								itemAttr.setITEMATTRIBUTE(posMappingMap.get(skuAttribute));
								itemAttr.setATTRIBUTECODE(skuAttributeValue);
								itemAttrLst.add(itemAttr);
							}
							if(MPV != null && MPV >=0 && 
									receiptAmount!= null && !receiptAmount.isEmpty() && Double.parseDouble(receiptAmount) < MPV  ){
								logger.debug("MPV wasnt matchs");
								return null;
							}
							
							if((programID != null && !programID.isEmpty() &&( (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramId().toString().equals(programID)) || (programID != null && contactsLoyaltyObj == null)))
									|| (tierID !=null && !tierID.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramTierId().toString().equals(tierID))) ||
									(cardSetId != null && !cardSetId.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getCardSetId().toString().equals(cardSetId))) ){
								logger.debug("elegibility on tier + cardset are failed");
								return null;
							}
							
							List<SkuFile> itemsUnderDiscount = couponDiscountGenerateDao.findDiscountedItems(itemCodeStr, user.getUserId(), comboQuery);
							if(itemsUnderDiscount == null || itemsUnderDiscount.isEmpty()) {//when items in the receipt are not matching 
								//then leave the itemcode as empty and give all the attribute as in array
								
								if( ( (coupCodeStr.equalsIgnoreCase("ALL") || 
										coupCodeStr.equalsIgnoreCase(Constants.STRING_NILL) 
										) && (coupObj.getValueCode()!= null && 
										!coupObj.getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) ) || 
										coupCodeStr.equalsIgnoreCase("OC-LOYALTY")){ 
									
									List<DiscountInfo> discountInfoList = new ArrayList<DiscountInfo>();
									List<ItemCodeInfo> itemsForThisDiscount = new ArrayList<ItemCodeInfo>();
									
									DiscountInfo discountInfo = new DiscountInfo();
									discountInfo.setVALUE(discount != null ? discount+ Constants.STRING_NILL : Constants.STRING_NILL);
									discountInfo.setVALUECODE(disCountType);
									discountInfo.setMINPURCHASEVALUE(Constants.STRING_NILL);
									if(isRequestedFromNewPlugin) {
										discountInfo.setSHIPPINGFEE(shippingFee != null ? shippingFee : "");
										//discountInfo.setSHIPPINGFEEFREE(shippingFeeFree == null ? "" : shippingFeeFree );
										discountInfo.setSHIPPINGFEETYPE(shippingFeeType != null ? shippingFeeType :"");
										discountInfo.setMAXRECEIPTDISCOUNT(Constants.STRING_NILL);
										discountInfo.setRECEIPTDISCOUNT(Constants.STRING_NILL);
									}
									ItemCodeInfo itemcodeInfo = new ItemCodeInfo();
									itemcodeInfo.setITEMCODE(Constants.STRING_NILL);
									if(isRequestedFromNewPlugin) {
										
										itemcodeInfo.setITEMDISCOUNT(discount == null ? Constants.STRING_NILL : discount+Constants.STRING_NILL);
										itemcodeInfo.setMAXITEMDISCOUNT(maxDiscount == null ? Constants.STRING_NILL : maxDiscount+ Constants.STRING_NILL);
										itemcodeInfo.setQUANTITY(quantity == null ? Constants.STRING_NILL : quantity );
										itemcodeInfo.setQUANTITYCRITERIA(quantityCriteria == null ? Constants.STRING_NILL : Utility.limitQuantityMap.get(quantityCriteria));
										String rewardRatio = "";
										logger.debug("quantity=="+quantity);
										if(quantity != null && !quantity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
											rewardRatio = quantity+"-"+coupObj.getRequiredLoyltyPoits(); 
											logger.debug("rewardRatio=="+rewardRatio);
										}
										itemcodeInfo.setREWARDRATIO(rewardRatio);
										itemcodeInfo.setITEMATTRIBUTE(itemAttrLst);
										
									}
									itemsForThisDiscount.add(itemcodeInfo);
									
									logger.debug("itemsForThisDiscount===="+itemsForThisDiscount.size());
									discountInfo.setITEMCODEINFO(itemsForThisDiscount);
									discountInfoList.add(discountInfo);
									
									couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
								}else{
									
									return null;
								}
							}
							else if(itemsUnderDiscount != null && !itemsUnderDiscount.isEmpty()){
								
							
								List<DiscountInfo> discountInfoList = new ArrayList<DiscountInfo>();
								List<ItemCodeInfo> itemsForThisDiscount = new ArrayList<ItemCodeInfo>();
								List<PurchasedItems> matchingItems = new ArrayList<PurchasedItems>();
								List<SkuFile> finalItems = (coupCodeStr.equalsIgnoreCase("OC-LOYALTY")) ? itemsUnderDiscount : new ArrayList<SkuFile>();
								if(finalItems.isEmpty()) {
									
									
									if( coupCodeStr.equalsIgnoreCase("ALL") || 
											coupCodeStr.equalsIgnoreCase(Constants.STRING_NILL)  ){ 
										if(coupObj.getValueCode() != null && 
												!coupObj.getValueCode().equals(OCConstants.LOYALTY_TYPE_POINTS)) {
											
											finalItems = itemsUnderDiscount;
										}else if(coupObj.getValueCode() == null || 
												coupObj.getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
											
											for (SkuFile skuFile : itemsUnderDiscount) {
												
												for (PurchasedItems purchasedItems : ItemsList) {
													if(skuFile.getItemSid().equalsIgnoreCase(purchasedItems.getITEMCODE())){
														
														finalItems.add(skuFile);
														break;
													}
												}
											}
											
										}
										
									}else{
										
										for (SkuFile skuFile : itemsUnderDiscount) {
											
											for (PurchasedItems purchasedItems : ItemsList) {
												if(skuFile.getItemSid().equalsIgnoreCase(purchasedItems.getITEMCODE())){
													finalItems.add(skuFile);
													break;
												}
											}
										}
										
									}
								}//if not oc_loyalty case
								for (PurchasedItems item : ItemsList) {
									for (SkuFile skuFile : finalItems) {
										
										
										if(item.getITEMCODE().equalsIgnoreCase(skuFile.getItemSid())){
											if(item.getITEMPRICE() != null && !item.getITEMPRICE().isEmpty() && ItemPrice != null && ItemPrice > 0  && ItemPriceCriteria != null){
												
												Double receiptItemPrice = Double.parseDouble(item.getITEMPRICE());
												boolean isItemPrice = false;
												if(ItemPriceCriteria.equals(">=")){
													isItemPrice = receiptItemPrice >= ItemPrice;
												}else if(ItemPriceCriteria.equals("<=")){
													isItemPrice = receiptItemPrice <= ItemPrice;
												}
												
												if(!isItemPrice) continue;
											}
											matchingItems.add(item);
											break;
										}
									}
									
								}
								if(matchingItems.isEmpty()) return null;
								List<PurchasedItems> finalItemsList = new ArrayList<PurchasedItems>();
								PurchasedItems highestPriceItem = null;
								PurchasedItems highestPriceItemWithDiscount = null;
								double highestPriceItemWithDiscountPrice = 0.0;
								PurchasedItems lowestPriceItem = null;
								PurchasedItems lowestPriceItemWithDiscount = null;
								double lowestPriceItemWithDiscountPrice = 0.0;
								String itemQuantity = Constants.ITEM_QUANTITY_ALL;
								if(coupObj.getNoOfEligibleItems() != null && noOfElegibleItems == null) noOfElegibleItems=coupObj.getNoOfEligibleItems();
								if(isRequestedFromNewPlugin && noOfElegibleItems!=null) {

									if(coupObj.getNoOfEligibleItems().equals(Constants.ALL_ELIGIBLE_ITEMS)) {
										finalItemsList = matchingItems;

									}else if( noOfElegibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT)) {
										for (PurchasedItems tempObj : matchingItems) {
											if(highestPriceItem == null) highestPriceItem = tempObj;

											Double highestPricedItem = Double.parseDouble(highestPriceItem.getITEMPRICE()); 
											Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

											if( presentItemPrice > highestPricedItem ) highestPriceItem = tempObj;
										}
										itemQuantity = Constants.ITEM_QUANTITY_ONE;
										finalItemsList.add(highestPriceItem);
									}else if(noOfElegibleItems.equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT)){

										for (PurchasedItems tempObj : matchingItems) {
											if(highestPriceItemWithDiscount == null) {
												highestPriceItemWithDiscount = tempObj;
												
												String itemPr = highestPriceItemWithDiscount.getITEMPRICE();
												String itemDis = highestPriceItemWithDiscount.getITEMDISCOUNT();
												
												Double itemPrice = itemPr != null && !itemPr.trim().isEmpty() ? Double.parseDouble(itemPr):0.0;
												Double itemDiscountDbl = itemDis != null && !itemDis.trim().isEmpty() ? Double.parseDouble(itemDis):0.0;
												
												Double highestItemPrice = itemPrice; 
												highestPriceItemWithDiscountPrice = highestItemPrice + itemDiscountDbl;
												
												//Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
												//highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
											}

											
											String itemPr1 = tempObj.getITEMPRICE();
											String itemDis1 = tempObj.getITEMDISCOUNT();
											
											Double itemPrice1 = itemPr1 != null && !itemPr1.trim().isEmpty() ? Double.parseDouble(itemPr1):0.0;
											Double itemDiscountDbl1 = itemDis1 != null && !itemDis1.trim().isEmpty() ? Double.parseDouble(itemDis1):0.0;
											
											Double presentItemPrice =  itemPrice1; 

											// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
											Double presentItemdiscount =  itemDiscountDbl1; 

											
				/*							Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

											// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
											Double presentItemdiscount =  Double.parseDouble(tempObj.getITEMDISCOUNT()); 
				*/
											presentItemPrice += presentItemdiscount;

											if(presentItemPrice > highestPriceItemWithDiscountPrice ) {
												highestPriceItemWithDiscount = tempObj;
											
												String itemPr2 = highestPriceItemWithDiscount.getITEMPRICE();
												String itemDis2 = highestPriceItemWithDiscount.getITEMDISCOUNT();
												
												
												Double itemPrice2 = itemPr2 != null && !itemPr2.trim().isEmpty() ? Double.parseDouble(itemPr2):0.0;
												Double itemDiscountDbl2 = itemDis2 != null && !itemDis2.trim().isEmpty() ? Double.parseDouble(itemDis2):0.0;
												
												
												Double highestItemPrice =  itemPrice2;
												highestPriceItemWithDiscountPrice = highestItemPrice + itemDiscountDbl2;
												
												/*Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
												highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());*/
											}
										}
										itemQuantity = Constants.ITEM_QUANTITY_ONE;
										logger.info("highestPriceItemWithDiscount ::"+highestPriceItemWithDiscount!=null?highestPriceItemWithDiscount.getITEMCODE():Constants.STRING_NILL);
										finalItemsList.add(highestPriceItemWithDiscount);
									}else if( noOfElegibleItems.equals(Constants.LOWEST_PRICED_ITEMS_WITH_OUT_DISCOUNT)) {
										for (PurchasedItems tempObj : matchingItems) {
											if(lowestPriceItem == null) lowestPriceItem = tempObj;

											Double lowestPricedItem = Double.parseDouble(lowestPriceItem.getITEMPRICE()); 
											Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

											if( presentItemPrice < lowestPricedItem ) lowestPriceItem = tempObj;
										}
										itemQuantity = Constants.ITEM_QUANTITY_ONE;
										finalItemsList.add(lowestPriceItem);
									}else if( noOfElegibleItems.equals(Constants.LOWEST_PRICED_ITEMS_WITH_DISCOUNT)) {

										for (PurchasedItems tempObj : matchingItems) {
											if(lowestPriceItemWithDiscount == null) {
												lowestPriceItemWithDiscount = tempObj;
												
												String itemPr = lowestPriceItemWithDiscount.getITEMPRICE();
												String itemDis = lowestPriceItemWithDiscount.getITEMDISCOUNT();
												
												Double itemPrice = itemPr != null && !itemPr.trim().isEmpty() ? Double.parseDouble(itemPr):0.0;
												Double itemDiscountDbl = itemDis != null && !itemDis.trim().isEmpty() ? Double.parseDouble(itemDis):0.0;
												
												Double lowestItemPrice = itemPrice; 
												lowestPriceItemWithDiscountPrice = lowestItemPrice + itemDiscountDbl;
												
												//Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
												//highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
											}

											
											String itemPr1 = tempObj.getITEMPRICE();
											String itemDis1 = tempObj.getITEMDISCOUNT();
											
											Double itemPrice1 = itemPr1 != null && !itemPr1.trim().isEmpty() ? Double.parseDouble(itemPr1):0.0;
											Double itemDiscountDbl1 = itemDis1 != null && !itemDis1.trim().isEmpty() ? Double.parseDouble(itemDis1):0.0;
											
											Double presentItemPrice =  itemPrice1; 

											// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
											Double presentItemdiscount =  itemDiscountDbl1; 

											
				/*							Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

											// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
											Double presentItemdiscount =  Double.parseDouble(tempObj.getITEMDISCOUNT()); 
				*/
											presentItemPrice += presentItemdiscount;

											if(presentItemPrice < lowestPriceItemWithDiscountPrice ) {
												lowestPriceItemWithDiscount = tempObj;
											
												String itemPr2 = lowestPriceItemWithDiscount.getITEMPRICE();
												String itemDis2 = lowestPriceItemWithDiscount.getITEMDISCOUNT();
												
												
												Double itemPrice2 = itemPr2 != null && !itemPr2.trim().isEmpty() ? Double.parseDouble(itemPr2):0.0;
												Double itemDiscountDbl2 = itemDis2 != null && !itemDis2.trim().isEmpty() ? Double.parseDouble(itemDis2):0.0;
												
												
												Double lowestItemPrice =  itemPrice2;
												lowestPriceItemWithDiscountPrice = lowestItemPrice + itemDiscountDbl2;
												
												/*Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
												highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());*/
											}
										}
										itemQuantity = Constants.ITEM_QUANTITY_ONE;
										logger.info("highestPriceItemWithDiscount ::"+lowestPriceItemWithDiscount!=null?lowestPriceItemWithDiscount.getITEMCODE():Constants.STRING_NILL);
										finalItemsList.add(lowestPriceItemWithDiscount);
									}

								}else {

									finalItemsList = matchingItems;

								}
								
								DiscountInfo discountInfo = new DiscountInfo();
								discountInfo.setVALUE(discount != null ? discount+ Constants.STRING_NILL : Constants.STRING_NILL);
								discountInfo.setVALUECODE(disCountType);
								discountInfo.setMINPURCHASEVALUE(MPV != null ? MPV+Constants.STRING_NILL : Constants.STRING_NILL);
								if(isRequestedFromNewPlugin) {
									discountInfo.setSHIPPINGFEE(shippingFee != null ? shippingFee : "");
									//discountInfo.setSHIPPINGFEEFREE(shippingFeeFree == null ? "" : shippingFeeFree );
									discountInfo.setSHIPPINGFEETYPE(shippingFeeType != null ? shippingFeeType :"");
									discountInfo.setMAXRECEIPTDISCOUNT(Constants.STRING_NILL);
									discountInfo.setRECEIPTDISCOUNT(Constants.STRING_NILL);
									discountInfo.setITEMPRICE(ItemPrice != null ? ItemPrice+Constants.STRING_NILL : Constants.STRING_NILL);
									discountInfo.setELIGIBILITY(noOfElegibleItems  != null ? noOfElegibleItems:Constants.STRING_NILL );
								}
								for (PurchasedItems item : finalItemsList) {
									ItemCodeInfo itemcodeInfo = new ItemCodeInfo();
									itemcodeInfo.setITEMCODE(item.getITEMCODE());
									if(isRequestedFromNewPlugin) {
										
										if(quantity != null){
											
											String discountedQty = getDiscountQty(quantity, item.getQUANTITY(), quantityCriteria, balance, 
													coupObj.getRequiredLoyltyPoits(), pointsBalance);
											if(discountedQty.isEmpty() || discountedQty.equals("0")) continue;
											else {
												itemcodeInfo.setITEMDISCOUNT(discount == null ? Constants.STRING_NILL : discount+Constants.STRING_NILL);
												itemcodeInfo.setMAXITEMDISCOUNT(maxDiscount == null ? Constants.STRING_NILL : maxDiscount+ Constants.STRING_NILL);
												//if(quantity != null)itemcodeInfo.setQUANTITY(discountedQty);
												itemcodeInfo.setQUANTITY(discountedQty == null ? Constants.STRING_NILL : discountedQty );
												itemcodeInfo.setQUANTITYCRITERIA(quantityCriteria == null ? Constants.STRING_NILL : Utility.limitQuantityMap.get(quantityCriteria) );
												String rewardRatio = "";
												logger.debug("quantity=="+discountedQty);
												if( quantity !=null && !quantity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
													rewardRatio = quantity+"-"+coupObj.getRequiredLoyltyPoits(); 
												}
												logger.debug("rewardRatio=="+rewardRatio);
												itemcodeInfo.setREWARDRATIO(rewardRatio);
												itemcodeInfo.setITEMATTRIBUTE(itemAttrLst);

											}
										}else{
											if(quantity == null) quantity = item.getQUANTITY();
											itemcodeInfo.setITEMCODE(item.getITEMCODE());
											itemcodeInfo.setITEMDISCOUNT(discount == null ? Constants.STRING_NILL : discount+Constants.STRING_NILL);
											itemcodeInfo.setMAXITEMDISCOUNT(maxDiscount == null ? Constants.STRING_NILL : maxDiscount+ Constants.STRING_NILL);
											itemcodeInfo.setQUANTITY(quantity == null ? Constants.STRING_NILL : quantity );
											itemcodeInfo.setQUANTITYCRITERIA(quantityCriteria == null ? Constants.STRING_NILL : Utility.limitQuantityMap.get(quantityCriteria) );
											String rewardRatio = "";
											if( quantity != null && !quantity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
												rewardRatio = quantity+"-"+coupObj.getRequiredLoyltyPoits(); 
											}
											logger.debug("quantity=="+quantity);
											/*if(quantity != null && !quantity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
												rewardRatio = quantity+"-"+coupObj.getRequiredLoyltyPoits(); 
											}*/
											logger.debug("rewardRatio=="+rewardRatio);
											itemcodeInfo.setREWARDRATIO(rewardRatio);
											itemcodeInfo.setITEMATTRIBUTE(itemAttrLst);
										}
										
										
										
										
										//################
										/*itemcodeInfo.setITEMDISCOUNT(discount == null ? Constants.STRING_NILL : discount+Constants.STRING_NILL);
										itemcodeInfo.setMAXITEMDISCOUNT(maxDiscount == null ? Constants.STRING_NILL : maxDiscount+ Constants.STRING_NILL);
										itemcodeInfo.setQUANTITY(quantity == null ? Constants.STRING_NILL : quantity );
										itemcodeInfo.setQUANTITYCRITERIA(quantityCriteria == null ? Constants.STRING_NILL : Utility.limitQuantityMap.get(quantityCriteria));
										String rewardRatio = "";
										logger.debug("quantity=="+quantity);
										if(quantity != null && !quantity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
											rewardRatio = quantity+"-"+coupObj.getRequiredLoyltyPoits(); 
											logger.debug("rewardRatio=="+rewardRatio);
										}
										itemcodeInfo.setREWARDRATIO(rewardRatio);*/
										
									}//end if new plugin request
									itemsForThisDiscount.add(itemcodeInfo);
									
								}
								logger.debug("itemsForThisDiscount===="+itemsForThisDiscount.size());
								if(!itemsForThisDiscount.isEmpty()){
									
									discountInfo.setITEMCODEINFO(itemsForThisDiscount);
									discountInfoList.add(discountInfo);
									
									couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
								}else{
									return null;
								}
								
							}
						}
						
					}else{
						
						List<String> retList = coupDiscGenDao.findDistinctAttr(coupObj.getCouponId());
						List<Double> retDiscList = coupDiscGenDao.findDistinctCoupDiscount(coupObj.getCouponId());

						logger.info("coupObj ::"+coupObj.getCouponName());
						logger.info("retDiscList ::"+retDiscList!=null?retDiscList.size():"null");
						logger.info("retList ::"+retList!=null?retList.size():"null");
						//long deleted = couponDiscountGenerateDaoForDML.deleteTempPromoDumpBy(coupObj.getCouponId(), user.getUserId());
						coupDisList = new ArrayList<CouponDiscountGeneration>();
						for (String CDGAttr : retList) {

							List<CouponDiscountGeneration> tempcoupDisList = coupDiscGenDao.findBy(coupObj.getCouponId(), user.getUserId(), 
									Utility.CDGAttrToSKUMap.containsKey(CDGAttr) ? Utility.CDGAttrToSKUMap.get(CDGAttr) : CDGAttr, CDGAttr);
							if(tempcoupDisList != null && !tempcoupDisList.isEmpty()) {

								coupDisList.addAll(tempcoupDisList);
							}

						}//for
						List<CouponDiscountGeneration> finalDiscList = null;
						finalDiscList = new ArrayList<CouponDiscountGeneration>();
						for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
							for (PurchasedItems purchasedItems : ItemsList) {
								if(!purchasedItems.getITEMCODE().equalsIgnoreCase(coupDisGenObj.getItemCategory())) {
									logger.debug("purchasedItems=="+purchasedItems.getITEMCODE()+" coupDisGenObj.getItemCategory()=="+coupDisGenObj.getItemCategory());
									continue;
								}
								
								finalDiscList.add(coupDisGenObj);
							}
							
						}
						if(finalDiscList == null || finalDiscList.isEmpty()) {
							
							if( ( (coupCodeStr.equalsIgnoreCase("ALL") || 
									coupCodeStr.equalsIgnoreCase(Constants.STRING_NILL) 
									) && (coupObj.getValueCode()!= null && 
									!coupObj.getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) ) ||
									coupCodeStr.equalsIgnoreCase("OC-LOYALTY")){ 
								List<DiscountInfo> discountInfoList = new ArrayList<DiscountInfo>();
								for (Double discount : retDiscList) {
									Map<String, Set<String>> attributeMap = new HashMap<String, Set<String>>();
									Set<String> atributeValue = null;
									String MaxDiscount = Constants.STRING_NILL;
									String quanity = Constants.STRING_NILL;
									String quantityCriteria = Constants.STRING_NILL;
									String shippingFee = Constants.STRING_NILL;
									String shippingFeeFree = Constants.STRING_NILL;
									String shippingFeeType = Constants.STRING_NILL;
									String cardSetId = null;
									String programID = null;
									String tierID =null;
									List<ItemCodeInfo> itemCodeInfoList = new ArrayList<ItemCodeInfo>();
									for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
									

										logger.info("discount :: "+discount+" coupDisGenObj ::"+coupDisGenObj.getDiscount());
										if(discount.doubleValue() != coupDisGenObj.getDiscount().doubleValue()) continue;
										
										programID = coupDisGenObj.getProgram() != null ? coupDisGenObj.getProgram().toString() : Constants.STRING_NILL;
										tierID = coupDisGenObj.getTierNum() != null ? coupDisGenObj.getTierNum().toString() : Constants.STRING_NILL;
										cardSetId = coupDisGenObj.getCardSetNum() != null ? coupDisGenObj.getCardSetNum().toString() : Constants.STRING_NILL;
										if((programID != null && !programID.isEmpty() &&( (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramId().toString().equals(programID)) || (programID != null && contactsLoyaltyObj == null)))
												|| (tierID !=null && !tierID.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramTierId().toString().equals(tierID))) ||
												(cardSetId != null && !cardSetId.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getCardSetId().toString().equals(cardSetId))) ){
											logger.debug("elegibility on tier + cardset are failed");
											continue;
										}
										
										shippingFee = coupDisGenObj.getShippingFee() == null ? Constants.STRING_NILL : coupDisGenObj.getShippingFee();
										//shippingFeeFree = coupDisGenObj.getShippingFeeFree() == null ? Constants.STRING_NILL : coupDisGenObj.getShippingFeeFree();
										
										shippingFeeType = coupDisGenObj.getShippingFeeType() == null ? Constants.STRING_NILL : coupDisGenObj.getShippingFeeType();
										
										
										MaxDiscount = coupDisGenObj.getMaxDiscount() == null ? Constants.STRING_NILL : coupDisGenObj.getMaxDiscount()+Constants.STRING_NILL;
										
										quanity = coupDisGenObj.getQuantity() == null ? Constants.STRING_NILL : coupDisGenObj.getQuantity();
										quantityCriteria = coupDisGenObj.getLimitQuantity() == null ? Constants.STRING_NILL : coupDisGenObj.getLimitQuantity();
										//if(!discount.equals(coupDisGenObj.getDiscount())) continue;
										if(attributeMap.containsKey(coupDisGenObj.getSkuAttribute())){
											
											atributeValue = attributeMap.get(coupDisGenObj.getSkuAttribute());
											if(atributeValue == null) atributeValue = new HashSet<String>();
											
											atributeValue.add(coupDisGenObj.getSkuValue());
										}else{
											
											atributeValue = new HashSet<String>();
											atributeValue.add(coupDisGenObj.getSkuValue());
										}
										attributeMap.put(coupDisGenObj.getSkuAttribute(), atributeValue);
										
										
									}
									ItemCodeInfo itemcodeInfo = new ItemCodeInfo();
									itemcodeInfo.setITEMCODE(Constants.STRING_NILL);
									if(isRequestedFromNewPlugin) {
										List<ItemAttribute> itemAttrList = new ArrayList<ItemAttribute>();
										for (String attribute : attributeMap.keySet()) {
												Set<String> sttrValuesSet = attributeMap.get(attribute);
												for (String attrVal : sttrValuesSet) {
													ItemAttribute itemAttr = new ItemAttribute();
													itemAttr.setITEMATTRIBUTE(posMappingMap.get(attribute) == null ? attribute :  posMappingMap.get(attribute));
													itemAttr.setATTRIBUTECODE(attrVal);
													itemAttrList.add(itemAttr);
												}
											}
										itemcodeInfo.setITEMATTRIBUTE(itemAttrList);
										//itemcodeInfo.setITEMATTRIBUTE(posMappingMap.get(coupDisGenObj.getSkuAttribute()) == null ? coupDisGenObj.getSkuAttribute() :  posMappingMap.get(coupDisGenObj.getSkuAttribute()));//getPOSdiaplaylabel
										//itemcodeInfo.setATTRIBUTECODE(coupDisGenObj.getSkuValue());
										itemcodeInfo.setITEMDISCOUNT(discount+Constants.STRING_NILL);
										itemcodeInfo.setMAXITEMDISCOUNT(MaxDiscount);
										itemcodeInfo.setQUANTITY(quanity);
										itemcodeInfo.setQUANTITYCRITERIA(quantityCriteria == null ? Constants.STRING_NILL : Utility.limitQuantityMap.get(quantityCriteria));
										String rewardRatio = "";
										logger.debug("quantity=="+quanity);
										if(quanity != null && !quanity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
											rewardRatio = quanity+"-"+coupObj.getRequiredLoyltyPoits(); 
											logger.debug("rewardRatio=="+rewardRatio);
										}
										itemcodeInfo.setREWARDRATIO(rewardRatio);
									}

									itemCodeInfoList.add(itemcodeInfo);
									
									if(!itemCodeInfoList.isEmpty()) {

										DiscountInfo discountInfo = new DiscountInfo();
										discountInfo.setMINPURCHASEVALUE(Constants.STRING_NILL);
										discountInfo.setVALUE(discount.toString());
										discountInfo.setVALUECODE(disCountType);
										if(isRequestedFromNewPlugin) {
											discountInfo.setSHIPPINGFEE(shippingFee);
											discountInfo.setSHIPPINGFEEFREE(shippingFeeFree );
											discountInfo.setSHIPPINGFEETYPE(shippingFeeType);
											discountInfo.setMAXRECEIPTDISCOUNT(Constants.STRING_NILL);
											discountInfo.setRECEIPTDISCOUNT(Constants.STRING_NILL);
											
										}

										//discountInfo.setEXCLUDEDISCOUNTEDITEMS(coupObj.isExcludeItems() ? "True" : "False");

										discountInfo.setITEMCODEINFO(itemCodeInfoList );
										discountInfoList.add(discountInfo);

									}
									
								}
								if(discountInfoList.isEmpty()) return null;
								couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
							}else {
								
								return null;
							}
							
						}else if(finalDiscList !=null && !finalDiscList.isEmpty()){
							
							List<PurchasedItems> matchingItems = new ArrayList<PurchasedItems>();
							//remove the items from the discountlist
							for (PurchasedItems purchasedItems : ItemsList) {
								boolean isMatched = false;
								for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
									if(!purchasedItems.getITEMCODE().equalsIgnoreCase(coupDisGenObj.getItemCategory())) {
										logger.debug("purchasedItems=="+purchasedItems.getITEMCODE()+" coupDisGenObj.getItemCategory()=="+coupDisGenObj.getItemCategory());
										continue;
									}

									isMatched = true;
									//finalDiscList.add(coupDisGenObj);
								}
								if(isMatched)matchingItems.add(purchasedItems);
							}
							if(matchingItems == null || matchingItems.isEmpty()) {
								
								logger.debug("nothing to match==");
								return null;
							}
							
							/**
							 * distinctDisc holds Discount value
							 */
							Set<Double> distinctDisc = new HashSet<Double>();
							for (CouponDiscountGeneration couponDiscountGeneration : coupDisList) {
								distinctDisc.add(couponDiscountGeneration.getDiscount());
							}
							List<PurchasedItems> finalItemsList = new ArrayList<PurchasedItems>();
							PurchasedItems highestPriceItem = null;
							PurchasedItems highestPriceItemWithDiscount = null;
							double highestPriceItemWithDiscountPrice = 0.0;
							PurchasedItems lowestPriceItem = null;
							PurchasedItems lowestPriceItemWithDiscount = null;
							double lowestPriceItemWithDiscountPrice = 0.0;
							String itemQuantity = Constants.ITEM_QUANTITY_ALL;
							Map<String,PurchasedItems> elegibleItemsMap = new HashMap<String, PurchasedItems>();
							if(isRequestedFromNewPlugin ) {
								elegibleItemsMap = getTheElegibilitySet(matchingItems);
								
								/*

								if(coupObj.getNoOfEligibleItems().equals(Constants.ALL_ELIGIBLE_ITEMS)) {
									finalItemsList = matchingItems;

								}else if( coupObj.getNoOfEligibleItems().equals(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT)) {
									for (PurchasedItems tempObj : matchingItems) {
										if(highestPriceItem == null) highestPriceItem = tempObj;

										Double highestPricedItem = Double.parseDouble(highestPriceItem.getITEMPRICE()); 
										Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

										if( presentItemPrice > highestPricedItem ) highestPriceItem = tempObj;
									}
									itemQuantity = Constants.ITEM_QUANTITY_ONE;
									finalItemsList.add(highestPriceItem);
								}else if(coupObj.getNoOfEligibleItems().equals(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT)){

									for (PurchasedItems tempObj : matchingItems) {
										if(highestPriceItemWithDiscount == null) {
											highestPriceItemWithDiscount = tempObj;
											
											String itemPr = highestPriceItemWithDiscount.getITEMPRICE();
											String itemDis = highestPriceItemWithDiscount.getITEMDISCOUNT();
											
											Double itemPrice = itemPr != null && !itemPr.trim().isEmpty() ? Double.parseDouble(itemPr):0.0;
											Double itemDiscountDbl = itemDis != null && !itemDis.trim().isEmpty() ? Double.parseDouble(itemDis):0.0;
											
											Double highestItemPrice = itemPrice; 
											highestPriceItemWithDiscountPrice = highestItemPrice + itemDiscountDbl;
											
											//Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
											//highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
										}

										
										String itemPr1 = tempObj.getITEMPRICE();
										String itemDis1 = tempObj.getITEMDISCOUNT();
										
										Double itemPrice1 = itemPr1 != null && !itemPr1.trim().isEmpty() ? Double.parseDouble(itemPr1):0.0;
										Double itemDiscountDbl1 = itemDis1 != null && !itemDis1.trim().isEmpty() ? Double.parseDouble(itemDis1):0.0;
										
										Double presentItemPrice =  itemPrice1; 

										// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
										Double presentItemdiscount =  itemDiscountDbl1; 

										
										Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

										// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
										Double presentItemdiscount =  Double.parseDouble(tempObj.getITEMDISCOUNT()); 
			
										presentItemPrice += presentItemdiscount;

										if(presentItemPrice > highestPriceItemWithDiscountPrice ) {
											highestPriceItemWithDiscount = tempObj;
										
											String itemPr2 = highestPriceItemWithDiscount.getITEMPRICE();
											String itemDis2 = highestPriceItemWithDiscount.getITEMDISCOUNT();
											
											
											Double itemPrice2 = itemPr2 != null && !itemPr2.trim().isEmpty() ? Double.parseDouble(itemPr2):0.0;
											Double itemDiscountDbl2 = itemDis2 != null && !itemDis2.trim().isEmpty() ? Double.parseDouble(itemDis2):0.0;
											
											
											Double highestItemPrice =  itemPrice2;
											highestPriceItemWithDiscountPrice = highestItemPrice + itemDiscountDbl2;
											
											Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
											highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
										}
									}
									itemQuantity = Constants.ITEM_QUANTITY_ONE;
									logger.info("highestPriceItemWithDiscount ::"+highestPriceItemWithDiscount!=null?highestPriceItemWithDiscount.getITEMCODE():Constants.STRING_NILL);
									finalItemsList.add(highestPriceItemWithDiscount);
								}else if( coupObj.getNoOfEligibleItems().equals(Constants.LOWEST_PRICED_ITEMS_WITH_OUT_DISCOUNT)) {
									for (PurchasedItems tempObj : matchingItems) {
										if(lowestPriceItem == null) lowestPriceItem = tempObj;

										Double lowestPricedItem = Double.parseDouble(lowestPriceItem.getITEMPRICE()); 
										Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

										if( presentItemPrice < lowestPricedItem ) lowestPriceItem = tempObj;
									}
									itemQuantity = Constants.ITEM_QUANTITY_ONE;
									finalItemsList.add(lowestPriceItem);
								}else if( coupObj.getNoOfEligibleItems().equals(Constants.LOWEST_PRICED_ITEMS_WITH_DISCOUNT)) {

									for (PurchasedItems tempObj : matchingItems) {
										if(lowestPriceItemWithDiscount == null) {
											lowestPriceItemWithDiscount = tempObj;
											
											String itemPr = lowestPriceItemWithDiscount.getITEMPRICE();
											String itemDis = lowestPriceItemWithDiscount.getITEMDISCOUNT();
											
											Double itemPrice = itemPr != null && !itemPr.trim().isEmpty() ? Double.parseDouble(itemPr):0.0;
											Double itemDiscountDbl = itemDis != null && !itemDis.trim().isEmpty() ? Double.parseDouble(itemDis):0.0;
											
											Double lowestItemPrice = itemPrice; 
											lowestPriceItemWithDiscountPrice = lowestItemPrice + itemDiscountDbl;
											
											//Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
											//highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
										}

										
										String itemPr1 = tempObj.getITEMPRICE();
										String itemDis1 = tempObj.getITEMDISCOUNT();
										
										Double itemPrice1 = itemPr1 != null && !itemPr1.trim().isEmpty() ? Double.parseDouble(itemPr1):0.0;
										Double itemDiscountDbl1 = itemDis1 != null && !itemDis1.trim().isEmpty() ? Double.parseDouble(itemDis1):0.0;
										
										Double presentItemPrice =  itemPrice1; 

										// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
										Double presentItemdiscount =  itemDiscountDbl1; 

										
										Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

										// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
										Double presentItemdiscount =  Double.parseDouble(tempObj.getITEMDISCOUNT()); 
			
										presentItemPrice += presentItemdiscount;

										if(presentItemPrice < lowestPriceItemWithDiscountPrice ) {
											lowestPriceItemWithDiscount = tempObj;
										
											String itemPr2 = lowestPriceItemWithDiscount.getITEMPRICE();
											String itemDis2 = lowestPriceItemWithDiscount.getITEMDISCOUNT();
											
											
											Double itemPrice2 = itemPr2 != null && !itemPr2.trim().isEmpty() ? Double.parseDouble(itemPr2):0.0;
											Double itemDiscountDbl2 = itemDis2 != null && !itemDis2.trim().isEmpty() ? Double.parseDouble(itemDis2):0.0;
											
											
											Double lowestItemPrice =  itemPrice2;
											lowestPriceItemWithDiscountPrice = lowestItemPrice + itemDiscountDbl2;
											
											Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
											highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
										}
									}
									itemQuantity = Constants.ITEM_QUANTITY_ONE;
									logger.info("highestPriceItemWithDiscount ::"+lowestPriceItemWithDiscount!=null?lowestPriceItemWithDiscount.getITEMCODE():Constants.STRING_NILL);
									finalItemsList.add(lowestPriceItemWithDiscount);
								}


							*/}else {


							}
							finalItemsList = matchingItems;
							if(finalItemsList == null || finalItemsList.isEmpty()){
								logger.debug("===nothing to match with it===");
								return null;
							}
							for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
								for (PurchasedItems purchasedItems : finalItemsList) {
									if(!purchasedItems.getITEMCODE().equalsIgnoreCase(coupDisGenObj.getItemCategory())) {
										logger.debug("purchasedItems=="+purchasedItems.getITEMCODE()+" coupDisGenObj.getItemCategory()=="+coupDisGenObj.getItemCategory());
										continue;
									}
									finalDiscList.add(coupDisGenObj);
								}
							}
							if(finalDiscList.isEmpty()) return null;
							Set<Double> DiscList = new HashSet<Double>();
							for (CouponDiscountGeneration coupDisGenObj : finalDiscList) {
								
								
								DiscList.add(coupDisGenObj.getDiscount());
							}
							//DiscountInfo  DiscountInfo = null;
							List<DiscountInfo> discountInfoList = new ArrayList<DiscountInfo>();
							for (Double discount : DiscList) {
								logger.info("discount ::"+discount);
								//logger.info("finalCoupDisList :"+finalCoupDisList!=null ? finalCoupDisList.toArray():"NULL");
								List<ItemCodeInfo> itemCodeInfoList = new ArrayList<ItemCodeInfo>();
								String shippingFeeType = null;
								String shippingFee = null;
								String shippingFeeFree = null;
								for (PurchasedItems purchasedItems : ItemsList) {
									boolean itemMatched = false;
								
									List<ItemAttribute> itemAttrList = new ArrayList<ItemAttribute>();
									String MaxDiscount = Constants.STRING_NILL;
									String quanity = Constants.STRING_NILL;
									String quantityCriteria = Constants.STRING_NILL;
									//String noOfElegibleItems = null;
									
									Long MPV = null;
									String noOfElegibleItems = null;
									Double ItemPrice = null;
									String ItemPriceCriteria = null;
									for (CouponDiscountGeneration coupDisGenObj : finalDiscList) {
										logger.info("discount :: "+discount+" coupDisGenObj ::"+coupDisGenObj.getDiscount());
										if(discount.doubleValue() != coupDisGenObj.getDiscount().doubleValue()) continue;
										if(!purchasedItems.getITEMCODE().equalsIgnoreCase(coupDisGenObj.getItemCategory())) {
											logger.debug("purchasedItems=="+purchasedItems.getITEMCODE()+" coupDisGenObj.getItemCategory()=="+coupDisGenObj.getItemCategory());
											continue;
										}
										MaxDiscount = coupDisGenObj.getMaxDiscount() == null ? Constants.STRING_NILL : coupDisGenObj.getMaxDiscount()+Constants.STRING_NILL;
										quanity = coupDisGenObj.getQuantity() == null ? Constants.STRING_NILL : coupDisGenObj.getQuantity();
										quantityCriteria = coupDisGenObj.getLimitQuantity() == null ? Constants.STRING_NILL : coupDisGenObj.getLimitQuantity();
										shippingFee = coupDisGenObj.getShippingFee() == null ? Constants.STRING_NILL : coupDisGenObj.getShippingFee();
										shippingFeeType = coupDisGenObj.getShippingFeeType() == null ? Constants.STRING_NILL : coupDisGenObj.getShippingFeeType();
										
										noOfElegibleItems = coupDisGenObj.getNoOfEligibleItems() == null ? Constants.STRING_NILL : coupDisGenObj.getNoOfEligibleItems();
										ItemPrice = coupDisGenObj.getItemPrice() != null ? coupDisGenObj.getItemPrice() : null;
										MPV = coupDisGenObj.getTotPurchaseAmount() != null ? coupDisGenObj.getTotPurchaseAmount().longValue() : null;
										ItemPriceCriteria =  coupDisGenObj.getItemPriceCriteria() != null ? coupDisGenObj.getItemPriceCriteria() : Constants.STRING_NILL;
										if(purchasedItems.getITEMPRICE() != null && !purchasedItems.getITEMPRICE().isEmpty() && ItemPrice != null && ItemPrice > 0 && ItemPriceCriteria != null && !ItemPriceCriteria.isEmpty()){
											
												
											Double receiptItemPrice = Double.parseDouble(purchasedItems.getITEMPRICE());
											boolean isItemPrice = false;
											if(ItemPriceCriteria.equals(">=")){
												isItemPrice = receiptItemPrice >= ItemPrice;
											}else if(ItemPriceCriteria.equals("<=")){
												isItemPrice = receiptItemPrice <= ItemPrice;
											}
											
											if(!isItemPrice) continue;
										}
										
										if(noOfElegibleItems != null && 
												elegibleItemsMap.containsKey(noOfElegibleItems) && 
												!noOfElegibleItems.equalsIgnoreCase(Constants.ALL_ELIGIBLE_ITEMS) ){
											PurchasedItems eligibleItem =  elegibleItemsMap.get(noOfElegibleItems);
											if(!eligibleItem.getITEMCODE().equalsIgnoreCase(purchasedItems.getITEMCODE())) continue;
										}
										
										
										ItemAttribute itemAttr = new ItemAttribute();
										itemAttr.setITEMATTRIBUTE(posMappingMap.get(coupDisGenObj.getSkuAttribute()) == null ? coupDisGenObj.getSkuAttribute() :  posMappingMap.get(coupDisGenObj.getSkuAttribute()));
										itemAttr.setATTRIBUTECODE(coupDisGenObj.getSkuValue());
										itemAttrList.add(itemAttr);
										itemMatched = true;
									}
									
									if(itemMatched){
										ItemCodeInfo itemcodeInfo = new ItemCodeInfo();
										itemcodeInfo.setITEMCODE(purchasedItems.getITEMCODE());
										if(isRequestedFromNewPlugin) {
											
											
											if(quanity != null){
												
												String discountedQty = getDiscountQty(quanity, purchasedItems.getQUANTITY(), quantityCriteria, balance, coupObj.getRequiredLoyltyPoits(), pointsBalance);
												if(discountedQty.isEmpty() || discountedQty.equals("0")) continue;
												else {
													itemcodeInfo.setITEMDISCOUNT(discount == null ? Constants.STRING_NILL : discount+Constants.STRING_NILL);
													itemcodeInfo.setMAXITEMDISCOUNT(MaxDiscount == null ? Constants.STRING_NILL : MaxDiscount+ Constants.STRING_NILL);
													//if(quantity != null)itemcodeInfo.setQUANTITY(discountedQty);
													itemcodeInfo.setQUANTITY(discountedQty == null ? Constants.STRING_NILL : discountedQty );
													itemcodeInfo.setQUANTITYCRITERIA(quantityCriteria == null ? Constants.STRING_NILL : Utility.limitQuantityMap.get(quantityCriteria) );
													String rewardRatio = "";
													logger.debug("quantity=="+discountedQty);
													if( quanity != null && !quanity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
														rewardRatio = quanity+"-"+coupObj.getRequiredLoyltyPoits(); 
													}
													logger.debug("rewardRatio=="+rewardRatio);
													itemcodeInfo.setREWARDRATIO(rewardRatio);
													itemcodeInfo.setITEMATTRIBUTE(itemAttrList);

												}
											}else{
												if(quanity == null) quanity = purchasedItems.getQUANTITY();
												itemcodeInfo.setITEMCODE(purchasedItems.getITEMCODE());
												itemcodeInfo.setITEMDISCOUNT(discount == null ? Constants.STRING_NILL : discount+Constants.STRING_NILL);
												itemcodeInfo.setMAXITEMDISCOUNT(MaxDiscount == null ? Constants.STRING_NILL : MaxDiscount+ Constants.STRING_NILL);
												itemcodeInfo.setQUANTITY(quanity == null ? Constants.STRING_NILL : quanity );
												itemcodeInfo.setQUANTITYCRITERIA(quantityCriteria == null ? Constants.STRING_NILL : Utility.limitQuantityMap.get(quantityCriteria) );
												String rewardRatio = "";
												logger.debug("quantity=="+quanity);
												/*if(quantity != null && !quantity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
													rewardRatio = quantity+"-"+coupObj.getRequiredLoyltyPoits(); 
												}*/
												logger.debug("rewardRatio=="+rewardRatio);
												itemcodeInfo.setREWARDRATIO(rewardRatio);
												itemcodeInfo.setITEMATTRIBUTE(itemAttrList);
											}
											//#########################
											/*itemcodeInfo.setITEMATTRIBUTE(itemAttrList);
											//itemcodeInfo.setITEMATTRIBUTE(posMappingMap.get(coupDisGenObj.getSkuAttribute()) == null ? coupDisGenObj.getSkuAttribute() :  posMappingMap.get(coupDisGenObj.getSkuAttribute()));//getPOSdiaplaylabel
											//itemcodeInfo.setATTRIBUTECODE(coupDisGenObj.getSkuValue());
											itemcodeInfo.setITEMDISCOUNT(discount+Constants.STRING_NILL);
											itemcodeInfo.setMAXITEMDISCOUNT(MaxDiscount);
											itemcodeInfo.setQUANTITY(quanity);
											itemcodeInfo.setQUANTITYCRITERIA(Utility.limitQuantityMap.get(quantityCriteria));
											String rewardRatio = "";
											logger.debug("quantity=="+quanity);
											if(quanity != null && !quanity.isEmpty() && coupObj.getRequiredLoyltyPoits() != null ){
												rewardRatio = quanity+"-"+coupObj.getRequiredLoyltyPoits(); 
												logger.debug("rewardRatio=="+rewardRatio);
											}
											itemcodeInfo.setREWARDRATIO(rewardRatio);*/
											
										}

										itemCodeInfoList.add(itemcodeInfo);
									}
								}
								if(!itemCodeInfoList.isEmpty()) {


									DiscountInfo discountInfo = new DiscountInfo();
									discountInfo.setMINPURCHASEVALUE(Constants.STRING_NILL);
									discountInfo.setVALUE(discount.toString());
									discountInfo.setVALUECODE(disCountType);
									if(isRequestedFromNewPlugin) {
										discountInfo.setSHIPPINGFEE(shippingFee == null ? Constants.STRING_NILL : shippingFee);
										discountInfo.setSHIPPINGFEEFREE(shippingFeeFree == null ? Constants.STRING_NILL : shippingFeeFree);
										discountInfo.setSHIPPINGFEETYPE(shippingFeeType == null ? Constants.STRING_NILL : shippingFeeType);
										discountInfo.setMAXRECEIPTDISCOUNT(Constants.STRING_NILL);
										discountInfo.setRECEIPTDISCOUNT(Constants.STRING_NILL);
										
									}

									//discountInfo.setEXCLUDEDISCOUNTEDITEMS(coupObj.isExcludeItems() ? "True" : "False");

									discountInfo.setITEMCODEINFO(itemCodeInfoList );
									discountInfoList.add(discountInfo);
									
									
								}else{
									return null;
								}
								
							}
							couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
						}
											
						//if(finalDiscList.isEmpty()) return null;
						/*for (CouponDiscountGeneration coupDisGenObj : finalDiscList) {
							
							
							retDiscList.add(coupDisGenObj.getDiscount());
						}*/	
						/*if( coupCodeStr.equalsIgnoreCase("ALL") || 
								coupCodeStr.equalsIgnoreCase(Constants.STRING_NILL) ||
								coupCodeStr.equalsIgnoreCase("OC-LOYALTY") ){ 
							if(coupObj.getValueCode() != null && 
								!coupObj.getValueCode().equals(OCConstants.LOYALTY_TYPE_POINTS)) {
							
								finalDiscList = coupDisList;
							
							}else if(coupObj.getValueCode() == null || coupObj.getValueCode().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
								finalDiscList = new ArrayList<CouponDiscountGeneration>();
								for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
									for (PurchasedItems purchasedItems : ItemsList) {
										if(!purchasedItems.getITEMCODE().equalsIgnoreCase(coupDisGenObj.getItemCategory())) {
											logger.debug("purchasedItems=="+purchasedItems.getITEMCODE()+" coupDisGenObj.getItemCategory()=="+coupDisGenObj.getItemCategory());
											continue;
										}
										
										finalDiscList.add(coupDisGenObj);
									}
									
								}
								
							}
						}else{
							
							finalDiscList = new ArrayList<CouponDiscountGeneration>();
							for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
								for (PurchasedItems purchasedItems : ItemsList) {
									if(!purchasedItems.getITEMCODE().equalsIgnoreCase(coupDisGenObj.getItemCategory())) {
										logger.debug("purchasedItems=="+purchasedItems.getITEMCODE()+" coupDisGenObj.getItemCategory()=="+coupDisGenObj.getItemCategory());
										continue;
									}
									
									finalDiscList.add(coupDisGenObj);
								}
								
							}
							
						}*/
						
					}					
					
					
				
					
				}else{

					//need a through testing
					coupDisList = couponDiscountGenerateDao.findByCoupon(coupObj);
					if(coupDisList == null || coupDisList.isEmpty() ) return null;
					CouponDiscountGeneration MaxMPVcoupDisGenObj = null;
					String shippingFee = Constants.STRING_NILL;
					//String shippingFeeFree = Constants.STRING_NILL;
					String shippingFeeType = Constants.STRING_NILL;
					Double finialReceiptAmount = null;
					if(isRequestedFromNewPlugin) {
						
						
						String receiptAmount = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT();//!=null ? couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT().trim().isEmpty()?null:Double.parseDouble(couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getRECEIPTAMOUNT().trim()):null;
						if((!isReward && noReceiptData) && (receiptAmount == null || receiptAmount.trim().isEmpty() || Double.parseDouble(receiptAmount.trim()) <= 0)) return null;
						
						if(!noReceiptData) {
							finialReceiptAmount =   Double.parseDouble(receiptAmount);
							String reciptdDiscount = couponCodeEnquObj.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getDISCOUNTAMOUNT();
							Double reciptDiscount = reciptdDiscount !=null && !reciptdDiscount.isEmpty() ? Double.parseDouble(reciptdDiscount) : 0;
							if(purchaseList != null && purchaseList.size() > 0) {
								for (PurchasedItems tempObj : purchaseList) {
									
									
									Double totalItemPrice=null;
									
									String itemDisc = tempObj.getITEMDISCOUNT();
									
									//Check if exclude discounted items setting is on. If yes, then look for items with discounted value in request.
									if(itemDisc == null || itemDisc.isEmpty() || Double.parseDouble(itemDisc)<=0 ) continue;
									String itemQty =  tempObj.getQUANTITY() ;
									String itemPrice = tempObj.getITEMPRICE();
									
									Double itemDiscount =  Double.parseDouble(itemDisc);
									int itemQuantity = itemQty !=null && !itemQty.trim().isEmpty() ? Integer.parseInt(itemQty):0;
									Double itemPriceDbl = itemPrice !=null && !itemPrice.trim().isEmpty() ? Double.parseDouble(itemPrice):0;
									
									
									
									//totalItemPrice = (itemPriceDbl-itemDiscount)*itemQuantity;
									totalItemPrice = (itemPriceDbl)*itemQuantity;
									//Receipt final amount 
									if(coupObj.isExcludeItems() != null && coupObj.isExcludeItems())finialReceiptAmount = finialReceiptAmount - totalItemPrice;
									//else finialReceiptAmount = finialReceiptAmount-itemDiscount;
									
									
								}//for
								
							} //for
							Long MPV = 0l;
							
							for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
								
								/** if Minimum Purchase Amount(MPV) is present and ReceiptAmount < Minimum Purchase Amount
								 *  then
								 *  Send Failure Response
								 */
								logger.debug("understanding the discount order"+" finialReceiptAmount "
										+ "="+finialReceiptAmount+" MPV==="+MPV+" ,TPA==="+coupDisGenObj.getTotPurchaseAmount()+",  ");
								
								if(coupDisGenObj.getTotPurchaseAmount() == null || coupDisGenObj.getTotPurchaseAmount() <=0) continue;
								String programID = coupDisGenObj.getProgram() != null ? coupDisGenObj.getProgram().toString() : Constants.STRING_NILL;
								String tierID = coupDisGenObj.getTierNum() != null ? coupDisGenObj.getTierNum().toString() : Constants.STRING_NILL;
								String cardSetId = coupDisGenObj.getCardSetNum() != null ? coupDisGenObj.getCardSetNum().toString() : Constants.STRING_NILL;
								if((programID != null && !programID.isEmpty() &&( (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramId().toString().equals(programID)) || (programID != null && contactsLoyaltyObj == null)))
										|| (tierID !=null && !tierID.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramTierId().toString().equals(tierID))) ||
										(cardSetId != null && !cardSetId.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getCardSetId().toString().equals(cardSetId))) ){
									logger.debug("elegibility on tier + cardset are failed");
									continue;
								}
								if(MPV == 0 ) {
									MPV = coupDisGenObj.getTotPurchaseAmount();
								}
								if(MaxMPVcoupDisGenObj == null ) {
									
									MaxMPVcoupDisGenObj = coupDisGenObj;
									shippingFeeType = coupDisGenObj.getShippingFeeType();
									shippingFee = coupDisGenObj.getShippingFee();
									//shippingFeeFree =coupDisGenObj.getShippingFeeFree();
								}
								
								//if( coupDisGenObj.getTotPurchaseAmount()>MPV) MPV = coupDisGenObj.getTotPurchaseAmount();
								
								if(coupDisGenObj.getTotPurchaseAmount() >= MaxMPVcoupDisGenObj.getTotPurchaseAmount()   &&
										finialReceiptAmount>= coupDisGenObj.getTotPurchaseAmount()) {
									MaxMPVcoupDisGenObj = coupDisGenObj;
									MPV = coupDisGenObj.getTotPurchaseAmount();
									shippingFeeType = coupDisGenObj.getShippingFeeType();
									shippingFee = coupDisGenObj.getShippingFee();
									//shippingFeeFree =coupDisGenObj.getShippingFeeFree();
								}
								
								
								logger.debug("MPV==="+MPV+" finialReceiptAmount =="+finialReceiptAmount);
							}
							
							if(MPV != 0 && MPV > finialReceiptAmount) {
								logger.debug("MPV==="+MPV+" finialReceiptAmount =="+finialReceiptAmount);
								
								return null;//what about new error code???
							}
						}
						//finialReceiptAmount = finialReceiptAmount-reciptDiscount;
						//To get the highest priced Item without discount 
						//Map<String,Double> nonDiscountItemCodeSet_ItemPrice = new HashMap<>();
						
						
					}

				

				List<DiscountInfo> discountInfoList  = new ArrayList<DiscountInfo>();
				DiscountInfo  receiptDiscountInfo = null;
				List<ItemCodeInfo>	itemCodeArrObj = null;

				if(MaxMPVcoupDisGenObj != null) {
					receiptDiscountInfo = new DiscountInfo();
					receiptDiscountInfo.setVALUE(MaxMPVcoupDisGenObj.getDiscount().toString());
					receiptDiscountInfo.setVALUECODE(disCountType);
					receiptDiscountInfo.setMINPURCHASEVALUE(MaxMPVcoupDisGenObj.getTotPurchaseAmount().toString());
					if(isRequestedFromNewPlugin) {
						receiptDiscountInfo.setSHIPPINGFEE(shippingFee == null ? Constants.STRING_NILL : shippingFee);
						//receiptDiscountInfo.setSHIPPINGFEEFREE(shippingFeeFree == null ? Constants.STRING_NILL : shippingFeeFree);
						receiptDiscountInfo.setSHIPPINGFEETYPE(shippingFeeType == null ? Constants.STRING_NILL : shippingFeeType);
						receiptDiscountInfo.setRECEIPTDISCOUNT(MaxMPVcoupDisGenObj.getDiscount().toString());
						receiptDiscountInfo.setMAXRECEIPTDISCOUNT(MaxMPVcoupDisGenObj.getMaxDiscount() != null ? MaxMPVcoupDisGenObj.getMaxDiscount()+Constants.STRING_NILL : Constants.STRING_NILL);
						
					}
					itemCodeArrObj = new ArrayList<ItemCodeInfo>();
					receiptDiscountInfo.setITEMCODEINFO(itemCodeArrObj);
					discountInfoList.add(receiptDiscountInfo);
					
				}else{
					
					Map<Double, DiscountInfo> isSameDisMap = new HashMap<Double, DiscountInfo>();
					for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
						String programID = coupDisGenObj.getProgram() != null ? coupDisGenObj.getProgram().toString() : Constants.STRING_NILL;
						String tierID = coupDisGenObj.getTierNum() != null ? coupDisGenObj.getTierNum().toString() : Constants.STRING_NILL;
						String cardSetId = coupDisGenObj.getCardSetNum() != null ? coupDisGenObj.getCardSetNum().toString() : Constants.STRING_NILL;
						if((programID != null && !programID.isEmpty() &&( (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramId().toString().equals(programID)) || (programID != null && contactsLoyaltyObj == null)))
								|| (tierID !=null && !tierID.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getProgramTierId().toString().equals(tierID))) ||
								(cardSetId != null && !cardSetId.isEmpty() && (contactsLoyaltyObj != null && !contactsLoyaltyObj.getCardSetId().toString().equals(cardSetId))) ){
							logger.debug("elegibility on tier + cardset are failed");
							continue;
						}
						if(!(isSameDisMap.containsKey(coupDisGenObj.getDiscount()))) {
							receiptDiscountInfo = new DiscountInfo();
							receiptDiscountInfo.setVALUE(coupDisGenObj.getDiscount().toString());
							receiptDiscountInfo.setVALUECODE(disCountType);
							receiptDiscountInfo.setMINPURCHASEVALUE(coupDisGenObj.getTotPurchaseAmount().toString());
							if(isRequestedFromNewPlugin) {
								receiptDiscountInfo.setSHIPPINGFEE(coupDisGenObj.getShippingFee() == null ? "" : coupDisGenObj.getShippingFee());
								//receiptDiscountInfo.setSHIPPINGFEEFREE(coupDisGenObj.getShippingFeeFree() == null ? "" : coupDisGenObj.getShippingFeeFree());
								receiptDiscountInfo.setSHIPPINGFEETYPE(coupDisGenObj.getShippingFeeType() == null ? "" : coupDisGenObj.getShippingFeeType());
								receiptDiscountInfo.setRECEIPTDISCOUNT(coupDisGenObj.getDiscount().toString());
								receiptDiscountInfo.setMAXRECEIPTDISCOUNT(coupDisGenObj.getMaxDiscount() != null ? coupDisGenObj.getMaxDiscount()+Constants.STRING_NILL : Constants.STRING_NILL);
								
							}
							itemCodeArrObj = new ArrayList<ItemCodeInfo>();
							receiptDiscountInfo.setITEMCODEINFO(itemCodeArrObj);
							isSameDisMap.put(coupDisGenObj.getDiscount(), receiptDiscountInfo);
							
						}else {
							
							receiptDiscountInfo = isSameDisMap.get(coupDisGenObj.getDiscount());
						}
						
					}
					Set<Double> keySet = isSameDisMap.keySet();
					for (Double uniqDiscount : keySet) {
						discountInfoList.add(isSameDisMap.get(uniqDiscount));
					}
					
				}
					if(discountInfoList.isEmpty() ) return null;
					couponDiscountInfo.setDISCOUNTINFO(discountInfoList);
					//APP-3667 - multiplier in discount
					if(coupObj.getMultiplierValue()!=null) {
						
						boolean isEligibleWithMP = false;
						int requiredPoints = 0;
						if(coupObj.getDiscountType()!=null && coupObj.getDiscountType().equals("Percentage")) {
							double discAmnt = (finialReceiptAmount/100)*(Double.parseDouble(receiptDiscountInfo.getVALUE()));
							requiredPoints = (int)(discAmnt*coupObj.getMultiplierValue());
						}else if(coupObj.getDiscountType()!=null && coupObj.getDiscountType().equals("Value")) {
							requiredPoints = (int)(Double.parseDouble(receiptDiscountInfo.getVALUE())*coupObj.getMultiplierValue());
						}
						logger.info("requiredPoints 2>>>"+requiredPoints);
						
						if(coupObj.getValueCode() != null &&
								!coupObj.getValueCode().equals(OCConstants.LOYALTY_POINTS)) {
							if(balance != null && balance.getBalance()>=requiredPoints)  isEligibleWithMP = true;
							
							
						}else if((coupObj.getValueCode() == null || coupObj.getValueCode().equals(OCConstants.LOYALTY_POINTS)) &&
								contactsLoyaltyObj.getLoyaltyBalance() != null && contactsLoyaltyObj.getLoyaltyBalance() >= requiredPoints) {
							
							isEligibleWithMP = true;
						}
						if(isEligibleWithMP) couponDiscountInfo.setLOYALTYPOINTS(requiredPoints>0?requiredPoints+"":"");
						else {
							return null; 
						}
					}

				}
			}
		}catch(Exception e){
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getDiscountAmount::::: ", e);
		}

		logger.debug("-------exit  getDiscountAmount---------"+couponDiscountInfo);
		return couponDiscountInfo;
	}//getDiscountAmount


	/*private List<CouponDiscountInfo> promoReedemptionWithLoyaltyPtsNewPlugin(CouponCodeEnquObj couponCodeEnquObj,
			Users user,List<Coupons> listPromoCoupons,boolean isRequestFromNewPlugin, Map<String, String> posMappingMap) throws BaseServiceException {


		List<CouponDiscountInfo> coupDiscInfoList=null ;
		try {
			boolean isOcLoyalty = true ;
			logger.debug("-------entered promoReedemptionWithLoyaltyPts---------");
			CouponDiscountGenerateDao coupDiscGenDao=(CouponDiscountGenerateDao)
					ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);



			for (Coupons eachCouponObj : listPromoCoupons) {

				coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj,eachCouponObj,isOcLoyalty,eachCouponObj.getCouponCode(), 
						user, null, isRequestFromNewPlugin, posMappingMap);
				
				

			}

		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing promoReedemptionWithLoyaltyPts::::: ", e);
		}
		logger.debug("-------exit  promoReedemptionWithLoyaltyPts---------");
		return coupDiscInfoList;
	}//promoReedemptionWithLoyaltyPts
*/
	/*private List<CouponDiscountInfo> promoReedemptionWithLoyaltyPtsNewPlugin(CouponCodeEnquObj couponCodeEnquObj,Users user,
			List<Coupons> listPromoCoupons, List<CouponDiscountInfo> coupDiscInfoList, boolean isRequestFromNewPlugin, Map<String, String> posMappingMap) throws BaseServiceException {


		if(coupDiscInfoList== null) {
			coupDiscInfoList = new ArrayList<CouponDiscountInfo>() ;
		}
		try {
			boolean isOcLoyalty = true ;
			logger.debug("-------entered promoReedemptionWithLoyaltyPts---------");
			


			for (Coupons eachCouponObj : listPromoCoupons) {

				coupDiscInfoList = setCoupDiscInfoObjNewPlugin(coupDiscInfoList,couponCodeEnquObj,eachCouponObj,isOcLoyalty,eachCouponObj.getCouponCode(), user, null, isRequestFromNewPlugin, posMappingMap);

			}

		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing promoReedemptionWithLoyaltyPts::::: ", e);
		}
		logger.debug("-------exit  promoReedemptionWithLoyaltyPts---------");
		return coupDiscInfoList;
	}//promoReedemptionWithLoyaltyPts
*/

	
	public PromoTrxLog logTransactionRequestResponse(CoupnCodeEnqResponse responseObject, CouponCodeEnquObj requestObject,String jsonRequest ,String jsonResponse, String mode){
	

		PromoTrxLogDao PromoTrxLogDao = null;
		PromoTrxLogDaoForDML PromoTrxLogDaoForDML = null;

		PromoTrxLog transaction = null;
		try {
			PromoTrxLogDao = (PromoTrxLogDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROMOTRXLOG_DAO);
			PromoTrxLogDaoForDML = (PromoTrxLogDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROMOTRXLOG_DAO_FOR_DML);
			transaction = new PromoTrxLog();
			
			
			CouponCodeInfo couponCodeInfoObj=requestObject.getCOUPONCODEENQREQ().getCOUPONCODEINFO();
			String coupCodeStr = couponCodeInfoObj.getCOUPONCODE();
			if(coupCodeStr.equals("OC-LOYALTY") || coupCodeStr.equals("ALL") || coupCodeStr.isEmpty()) jsonResponse=Constants.STRING_NILL;
			
			transaction.setJsonRequest(jsonRequest);
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			UserDetails userDetailsObj=requestObject.getCOUPONCODEENQREQ().getUSERDETAILS();
			String userNameStr  = userDetailsObj.getUSERNAME();
			String orgId = userDetailsObj.getORGID();
			String tokenStr = userDetailsObj.getTOKEN();
			Users users = usersDao.findByToken(userNameStr+Constants.USER_AND_ORG_SEPARATOR+orgId , tokenStr);
			String currSymbol = Utility.countryCurrencyMap.get(users.getCountryType());
			   if(currSymbol != null && !currSymbol.isEmpty()) {
				   if(currSymbol.equals("$")) {
					   currSymbol = "USD";
					   if(users.getCountryType().equalsIgnoreCase(Constants.SMS_COUNTRY_CANADA)) {
						   currSymbol = "CAD";
					   }
				   }
			   }else {
				   currSymbol = "USD";
				   
			   }
			if(!jsonResponse.isEmpty() && !users.getCountryType().equals(Constants.SMS_COUNTRY_US) && 
					jsonResponse.contains(currSymbol)){
				jsonResponse = jsonResponse.replace(currSymbol, "[PHCurr]");
				
			}
			
			transaction.setJsonResponse(jsonResponse);
			transaction.setPcFlag(Boolean.valueOf(responseObject.getCOUPONCODERESPONSE().getHEADERINFO().getPCFLAG()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_INQUIRY);
			transaction.setUserDetail(requestObject.getCOUPONCODEENQREQ().getUSERDETAILS().getUSERNAME()+"__"+requestObject.getCOUPONCODEENQREQ().getUSERDETAILS().getORGID());
			transaction.setCustomerId(requestObject.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCUSTOMERID() != null && !requestObject.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCUSTOMERID().isEmpty()? requestObject.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getCUSTOMERID().trim() : null);
			//transaction.setDocSID();
			transaction.setStoreNumber(requestObject.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getSTORENUMBER() != null && !requestObject.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getSTORENUMBER().isEmpty()? requestObject.getCOUPONCODEENQREQ().getCOUPONCODEINFO().getSTORENUMBER().trim() : null);

			PromoTrxLogDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	
		
	}
	
	public static void main(String[] args) {
		Set<String> set = new HashSet<String>();
            String str = "Nom for 3 Oscar, dom for 234235 Oscars";
           // String desc =" Discount on KHAKI, SKY, SAND, WHITE, RED VEG, SILVER, L, GREEN, MED, NATURAL, NAVY, PINOT, GREY up to 1.0 item(s); 100.0% against 1 Reward types: Valuecode01.";
          String desc  = "Discount on ATLANTIC, 34 up to 12 item(s): 20%";
          System.out.println((50/8)*8 );
          System.out.println(5 * (12/10) );
            /* Pattern pattern = Pattern.compile("up to(.*?)item(s)");
            Matcher matcher = pattern.matcher(desc);
            while (matcher.find()) {
                System.out.println(matcher.group(1));
            }*/
            
            /*Pattern p = Pattern.compile(Pattern.quote("up to") + "(.*?)" + Pattern.quote("item(s)"));
            Matcher m = p.matcher(desc);
            String setDesc = "";
            while (m.find()) {
            	String qty = m.group(1);
            	BigDecimal bigDecimal = new BigDecimal(String.valueOf(((Double.parseDouble("12.0"))*1)/1));
        		int intValue = bigDecimal.intValue();
        		//bigDecimal.toPlainString());
        		BigDecimal decimalPart =  bigDecimal.subtract( new BigDecimal(intValue));
        		String val = decimalPart.doubleValue() != 0.0 ? bigDecimal.toPlainString() :  intValue+"";
        		if(decimalPart.doubleValue() != 0.0)
            	setDesc = desc.replace(m.group(1), ONESPACE+val+ONESPACE);
        		//setDesc = desc.replace(m.group(1), Double.parseDouble(qty)*5.0+"");
            }
            
            System.out.println(setDesc);*/
         /* String finalDesc = "";
            desc ="Discount on ATLANTIC, 34 up to 1 item(s): 20% against 1 Reward type: C0002.";
            if(desc.indexOf("against") != -1){
            	
            	Pattern p = Pattern.compile(Pattern.quote("against") + "(.*?)" + Pattern.quote("Reward"));
            	Matcher m = p.matcher(desc);
            	int i =1;
            	while (m.find()) {
            		System.out.println("find");
            		if(i==1){
            			i+=1;
            			continue;
            		}
            		finalDesc = desc.replace(m.group(1),"5.0");
            	}
            }
            System.out.println(finalDesc);
            System.out.println("01ae7b3a-dd60-42db-ac84-71d337c80cd5".length());*/
		/*set.add("string");
		String setDesc = desc.replace(desc.substring(desc.indexOf("up to"), desc.indexOf("item(s)")), 5+"").
				replace(desc.substring(desc.indexOf("against")+"against".length(), desc.indexOf(" reward")), 5+"");
		System.out.println(setDesc);*/

    		BigDecimal bigDecimal = new BigDecimal(String.valueOf("210.00000000000182"));
    		int intValue = bigDecimal.intValue();
    		//bigDecimal.toPlainString());
    		BigDecimal decimalPart =  bigDecimal.subtract( new BigDecimal(intValue));
    		if(decimalPart.doubleValue() != 0.0) System.out.println( bigDecimal.toPlainString());
    		else System.out.println(intValue+"");
	}

	public String getActualNumber(Double givenDecimalNum){
		
		BigDecimal bigDecimal = new BigDecimal(String.valueOf(givenDecimalNum));
		int intValue = bigDecimal.intValue();
		//bigDecimal.toPlainString());
		BigDecimal decimalPart =  bigDecimal.subtract( new BigDecimal(intValue));
		if(decimalPart.doubleValue() != 0.0) return bigDecimal.toPlainString();
		else return intValue+"";
	}
	/* limitQuantityMap.put("LTE","Maximum");
     limitQuantityMap.put("GTE", "Minimum");
     limitQuantityMap.put("ET", "Equals");
     limitQuantityMap.put("M", "Multiple Of");*/
	
	public String[] getReplacedQty(String description,  LoyaltyBalance balance, Integer requiredLtyPoints, Double totalfreeItems, String discountedQty, Long pointsBalance){
		Map<String, String> QtyCriteriaMap = Utility.limitQuantityMap;
		String itemsQty = null;
		String criteria = null;
		for (String key : QtyCriteriaMap.keySet()) {
			itemsQty = StringUtils.substringBetween(description.toLowerCase(),QtyCriteriaMap.get(key).toLowerCase()+ONESPACE, ITEMS);
			criteria = key;
			if(itemsQty != null ) break;
		}
		boolean calcOnBals = ((balance != null || pointsBalance!= null) && requiredLtyPoints != null);
		if(itemsQty != null){
			if(calcOnBals ) {
				Double replaceQtyDbl = balance != null ? ((Double.parseDouble(itemsQty))*(balance.getBalance()/requiredLtyPoints)) : ((Double.parseDouble(itemsQty))*(pointsBalance/requiredLtyPoints)) ;
				String rewardsToCut = "";
				if(discountedQty != null) {
					replaceQtyDbl = Double.parseDouble(discountedQty);
				}else{
					discountedQty = replaceQtyDbl+""; 
				}
				logger.debug("discountedQty =="+discountedQty+" "+requiredLtyPoints+" "+itemsQty);
				if(Double.parseDouble(discountedQty)<=Double.parseDouble(itemsQty)) {
					rewardsToCut = requiredLtyPoints+"";
				}else {
					Double div = (Double.parseDouble(discountedQty)/Double.parseDouble(itemsQty));
					int divNum = div.intValue();
					if(Double.parseDouble(discountedQty)%Double.parseDouble(itemsQty)==0){
						rewardsToCut =(divNum)*requiredLtyPoints+"";
					}else {
						rewardsToCut = requiredLtyPoints+(divNum)*requiredLtyPoints+"";
					}
				}
				
				//String discountQty = getDiscountQty(itemsQty, replaceQtyDbl+Constants.STRING_NILL, criteria);
				//if(!discountQty.isEmpty()) {
					
				String setDesc = description.replace(QtyCriteriaMap.get(criteria).toLowerCase()+ ONESPACE +itemsQty+ITEMS, QtyCriteriaMap.get(criteria).toLowerCase()+ ONESPACE+getActualNumber(replaceQtyDbl)+ITEMS);
				String[] retArr = new String[3];
				retArr[0] = replaceQtyDbl+Constants.STRING_NILL;
				retArr[1] = setDesc;
				retArr[2] = rewardsToCut;
				return retArr;
				//}
			}else{
				String discountQty = getDiscountQty(itemsQty, totalfreeItems+Constants.STRING_NILL, criteria, null, requiredLtyPoints, null);
				if(!discountQty.isEmpty()) {
					
					String setDesc = description.replace(QtyCriteriaMap.get(criteria).toLowerCase()+ ONESPACE +itemsQty+ITEMS, QtyCriteriaMap.get(criteria).toLowerCase()+ ONESPACE+getActualNumber(Double.parseDouble(discountQty))+ITEMS);
					String[] retArr = new String[2];
					retArr[0] = totalfreeItems+Constants.STRING_NILL;
					retArr[1] = setDesc;
					return retArr;
				}
				
			}
        
		}
		return null;
		
		
	}
	public String getDiscountQty(String definedQty, String purchasedQty, String qtyCriteria, LoyaltyBalance balance, Integer requiredLtyPoints, Double pointsbalance ){
		
		logger.debug("===entered getDiscountedQty==="+definedQty +" "+purchasedQty+" "+qtyCriteria+" ");
		if(purchasedQty == null || purchasedQty.isEmpty()) return "";
		if(definedQty != null && (qtyCriteria == null || qtyCriteria.isEmpty() || qtyCriteria.equals("up to"))) qtyCriteria = "LTE" ;
		else if(definedQty != null && qtyCriteria != null && qtyCriteria.equalsIgnoreCase("ALL") )return purchasedQty;// != null && !purchasedQty.isEmpty() ? purchasedQty : "";
		
		if(definedQty == null || definedQty.isEmpty()) return purchasedQty ;//!= null && !purchasedQty.isEmpty() ? purchasedQty : "";
		Double replaceQtyDbl = null;
		boolean calcOnBals = ( requiredLtyPoints != null && (balance != null || pointsbalance != null));
		if(calcOnBals) replaceQtyDbl = balance != null ? ((Double.parseDouble(definedQty))*(balance.getBalance()/requiredLtyPoints)) : ((Double.parseDouble(definedQty))*(pointsbalance.intValue()/requiredLtyPoints));
			Double difeinedQtyDbl = Double.parseDouble(definedQty);
			Double purchasedQtyDbl = Double.parseDouble(purchasedQty);
			switch(qtyCriteria) {
			  case "LTE":
			   if(purchasedQtyDbl <= difeinedQtyDbl || (replaceQtyDbl != null && purchasedQtyDbl<=replaceQtyDbl))
				   //if(replaceQtyDbl != null) return getActualNumber(purchasedQtyDbl*(balance.getBalance()/replaceQtyDbl))+"";
				   return purchasedQty;
			   else return replaceQtyDbl == null ? definedQty : replaceQtyDbl+"";
			   /*else 
				   if(replaceQtyDbl != null) return getActualNumber(replaceQtyDbl)+"";
				   return  definedQty;*/
			    
			  case "GTE":
				  if(purchasedQtyDbl >= difeinedQtyDbl) {
					  if(replaceQtyDbl != null ){
						  if(purchasedQtyDbl>=replaceQtyDbl) return replaceQtyDbl+"";
						  else return ((purchasedQtyDbl.intValue()/difeinedQtyDbl.intValue()))*(difeinedQtyDbl) +"";
					  }
					  else return purchasedQty;
				  }
				  else return  "0";
			  case "ET" :
				  if(purchasedQtyDbl >= difeinedQtyDbl){
					  if(replaceQtyDbl != null ){
						  if(purchasedQtyDbl>=replaceQtyDbl) return replaceQtyDbl+"";
						  else return ((purchasedQtyDbl.intValue()/difeinedQtyDbl.intValue()))*(difeinedQtyDbl) +"";
					  }
					  else return difeinedQtyDbl+"";
					  
				  }
				  else return  "0";
			  default:return "";
			    // code block
			}
			
		
	}
	public Map<String, PurchasedItems> getTheElegibilitySet(List<PurchasedItems> matchingItems){
		
		Map<String,PurchasedItems> elegibleItemsMap = new HashMap<String, PurchasedItems>();
		
		elegibleItemsMap.put(Constants.ALL_ELIGIBLE_ITEMS, null);
		PurchasedItems highestPriceItem = null;
		PurchasedItems highestPriceItemWithDiscount = null;
		Double highestPriceItemWithDiscountPrice = 0.0;
		for (PurchasedItems tempObj : matchingItems) {
			if(highestPriceItem == null) highestPriceItem = tempObj;

			Double highestPricedItem = Double.parseDouble(highestPriceItem.getITEMPRICE()); 
			Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

			if( presentItemPrice > highestPricedItem ) highestPriceItem = tempObj;
		}
		elegibleItemsMap.put(Constants.HIGHEST_PRICED_ITEM_WITH_OUT_DISCOUNT, highestPriceItem);

			for (PurchasedItems tempObj : matchingItems) {
				if(highestPriceItemWithDiscount == null) {
					highestPriceItemWithDiscount = tempObj;
					
					String itemPr = highestPriceItemWithDiscount.getITEMPRICE();
					String itemDis = highestPriceItemWithDiscount.getITEMDISCOUNT();
					
					Double itemPrice = itemPr != null && !itemPr.trim().isEmpty() ? Double.parseDouble(itemPr):0.0;
					Double itemDiscountDbl = itemDis != null && !itemDis.trim().isEmpty() ? Double.parseDouble(itemDis):0.0;
					
					Double highestItemPrice = itemPrice; 
					highestPriceItemWithDiscountPrice = highestItemPrice + itemDiscountDbl;
					
					//Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
					//highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
				}

				
				String itemPr1 = tempObj.getITEMPRICE();
				String itemDis1 = tempObj.getITEMDISCOUNT();
				
				Double itemPrice1 = itemPr1 != null && !itemPr1.trim().isEmpty() ? Double.parseDouble(itemPr1):0.0;
				Double itemDiscountDbl1 = itemDis1 != null && !itemDis1.trim().isEmpty() ? Double.parseDouble(itemDis1):0.0;
				
				Double presentItemPrice =  itemPrice1; 

				// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
				Double presentItemdiscount =  itemDiscountDbl1; 

				
/*							Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

				// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
				Double presentItemdiscount =  Double.parseDouble(tempObj.getITEMDISCOUNT()); 
*/
				presentItemPrice += presentItemdiscount;

				if(presentItemPrice > highestPriceItemWithDiscountPrice ) {
					highestPriceItemWithDiscount = tempObj;
				
					String itemPr2 = highestPriceItemWithDiscount.getITEMPRICE();
					String itemDis2 = highestPriceItemWithDiscount.getITEMDISCOUNT();
					
					
					Double itemPrice2 = itemPr2 != null && !itemPr2.trim().isEmpty() ? Double.parseDouble(itemPr2):0.0;
					Double itemDiscountDbl2 = itemDis2 != null && !itemDis2.trim().isEmpty() ? Double.parseDouble(itemDis2):0.0;
					
					
					Double highestItemPrice =  itemPrice2;
					highestPriceItemWithDiscountPrice = highestItemPrice + itemDiscountDbl2;
					
					/*Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
					highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());*/
				}
			}
			
			elegibleItemsMap.put(Constants.HIGHEST_PRICED_ITEM_WITH_DISCOUNT, highestPriceItemWithDiscount);
			 //if( coupObj.getNoOfEligibleItems().equals(Constants.LOWEST_PRICED_ITEMS_WITH_OUT_DISCOUNT)) {
			PurchasedItems lowestPriceItem = null;
			for (PurchasedItems tempObj : matchingItems) {
				if(lowestPriceItem == null) lowestPriceItem = tempObj;

				Double lowestPricedItem = Double.parseDouble(lowestPriceItem.getITEMPRICE()); 
				Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

				if( presentItemPrice < lowestPricedItem ) lowestPriceItem = tempObj;
			}
			elegibleItemsMap.put(Constants.LOWEST_PRICED_ITEMS_WITH_OUT_DISCOUNT, lowestPriceItem);
			
				//}else if( coupObj.getNoOfEligibleItems().equals(Constants.LOWEST_PRICED_ITEMS_WITH_DISCOUNT)) {
			PurchasedItems lowestPriceItemWithDiscount = null;
			Double lowestPriceItemWithDiscountPrice = null;
				for (PurchasedItems tempObj : matchingItems) {
					if(lowestPriceItemWithDiscount == null) {
						lowestPriceItemWithDiscount = tempObj;
						
						String itemPr = lowestPriceItemWithDiscount.getITEMPRICE();
						String itemDis = lowestPriceItemWithDiscount.getITEMDISCOUNT();
						
						Double itemPrice = itemPr != null && !itemPr.trim().isEmpty() ? Double.parseDouble(itemPr):0.0;
						Double itemDiscountDbl = itemDis != null && !itemDis.trim().isEmpty() ? Double.parseDouble(itemDis):0.0;
						
						Double lowestItemPrice = itemPrice; 
						lowestPriceItemWithDiscountPrice = lowestItemPrice + itemDiscountDbl;
						
						//Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
						//highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());
					}

					
					String itemPr1 = tempObj.getITEMPRICE();
					String itemDis1 = tempObj.getITEMDISCOUNT();
					
					Double itemPrice1 = itemPr1 != null && !itemPr1.trim().isEmpty() ? Double.parseDouble(itemPr1):0.0;
					Double itemDiscountDbl1 = itemDis1 != null && !itemDis1.trim().isEmpty() ? Double.parseDouble(itemDis1):0.0;
					
					Double presentItemPrice =  itemPrice1; 

					// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
					Double presentItemdiscount =  itemDiscountDbl1; 

					
/*							Double presentItemPrice =  Double.parseDouble(tempObj.getITEMPRICE()); 

						// Double highestPricedItemdiscount = Double.parseDouble(highestPriceItemWithDiscount.getITEMDISCOUNT()); 
						Double presentItemdiscount =  Double.parseDouble(tempObj.getITEMDISCOUNT()); 
*/
					presentItemPrice += presentItemdiscount;

					if(presentItemPrice < lowestPriceItemWithDiscountPrice ) {
						lowestPriceItemWithDiscount = tempObj;
					
						String itemPr2 = lowestPriceItemWithDiscount.getITEMPRICE();
						String itemDis2 = lowestPriceItemWithDiscount.getITEMDISCOUNT();
						
						
						Double itemPrice2 = itemPr2 != null && !itemPr2.trim().isEmpty() ? Double.parseDouble(itemPr2):0.0;
						Double itemDiscountDbl2 = itemDis2 != null && !itemDis2.trim().isEmpty() ? Double.parseDouble(itemDis2):0.0;
						
						
						Double lowestItemPrice =  itemPrice2;
						lowestPriceItemWithDiscountPrice = lowestItemPrice + itemDiscountDbl2;
						
						/*Double highestItemPrice = Double.parseDouble(highestPriceItemWithDiscount.getITEMPRICE()); 
						highestPriceItemWithDiscountPrice = highestItemPrice + Double.parseDouble( highestPriceItemWithDiscount.getITEMDISCOUNT());*/
					}
				}
			elegibleItemsMap.put(Constants.LOWEST_PRICED_ITEMS_WITH_DISCOUNT, lowestPriceItemWithDiscount);
			return elegibleItemsMap;
	}
	public List<LoyaltyThresholdBonus> isBonusOnAmountSetOnexpiry(Long programId, boolean fetchExpireBonuses) throws Exception{
		LoyaltyThresholdBonusDao loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
		List<LoyaltyThresholdBonus> threshBonusList = loyaltyThresholdBonusDao.getAllExpireBy(programId, fetchExpireBonuses);
		List<LoyaltyThresholdBonus> expireThreshBonusList = new ArrayList<LoyaltyThresholdBonus>() ;
		for (LoyaltyThresholdBonus loyaltyThresholdBonus : threshBonusList) {
			if(loyaltyThresholdBonus.getExtraBonusType() != null && 
					OCConstants.LOYALTY_TYPE_AMOUNT.equals(loyaltyThresholdBonus.getExtraBonusType())){
				expireThreshBonusList.add(loyaltyThresholdBonus);
			}
			
		}
		
		return expireThreshBonusList;
	}
	public List<String> getAmountExpiryList(Users userID,  Long loyaltyId, List<LoyaltyThresholdBonus> expiryBonusList){
		try {
			Map<String, Double> monthYearAmountMap = new HashMap<String, Double>();
			LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			for (LoyaltyThresholdBonus loyaltyThresholdBonus  : expiryBonusList) {
				
				String subquery ="";
				if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyThresholdBonus.getBonusExpiryDateType())){
					subquery = 	"MONTH"; 
				}
				else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyThresholdBonus.getBonusExpiryDateType())){
					subquery = 	"YEAR";
					
				}
				else{
					logger.info("INVALID LOYALTY REWARD EXPIRY TYPE MONTH/YEAR ...");
					continue;
				}
				
				List<Object[]> retList = expiryDao.getTheExpiryAmount(userID.getUserId(),  loyaltyId, loyaltyThresholdBonus.getThresholdBonusId(), 
						loyaltyThresholdBonus.getBonusExpiryDateValue().intValue(), subquery);
				
				if(retList == null) continue;
				
				for (Object[] expDetails : retList) {
					
					Double expAmount = expDetails[0] !=null && 
							!expDetails[0].toString().isEmpty()? Double.parseDouble(expDetails[0] .toString()):0.0; 
					String expirity = expDetails[1].toString();
					if(monthYearAmountMap.containsKey(expirity)) expAmount += monthYearAmountMap.get(expirity);
					
					monthYearAmountMap.put(expirity, expAmount);
					
				}
			}
			List<String> expirityDetails = new ArrayList<String>();
			String userCurrencySymbol = "$";
			SimpleDateFormat sdf1 = new SimpleDateFormat(MyCalendar.FORMAT_YEARTODATE);
			
			for (String expirity : monthYearAmountMap.keySet()) {
				String currSymbol = Utility.countryCurrencyMap.get(userID.getCountryType());
				if(currSymbol != null && !currSymbol.isEmpty()) userCurrencySymbol = currSymbol ;
				
				Date dateq = sdf1.parse(expirity);
				Calendar mycal = MyCalendar.getInstance();
				mycal.setTime(dateq);
				expirityDetails.add(userCurrencySymbol+monthYearAmountMap.get(expirity)+
						" Loyalty Reward - Expires "+MyCalendar.calendarToString(mycal, MyCalendar.FORMAT_MONTHDATE_ONLY));
			}
			return expirityDetails;
			
			
			
		} catch (Exception e) {

			logger.debug("Exception e", e);
		}
		return null;
	}
}
