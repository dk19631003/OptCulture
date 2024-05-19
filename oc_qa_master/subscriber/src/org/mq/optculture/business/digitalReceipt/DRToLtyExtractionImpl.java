package org.mq.optculture.business.digitalReceipt;

import org.mq.optculture.restservice.digitalReceipt.DRToLtyExtractionService;
import org.mq.marketer.campaign.general.Constants;

import java.beans.PropertyDescriptor;
import java.security.KeyStore.Entry;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.ContactsLoyaltyStage;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.DigitalReceiptsJSON;
import org.mq.marketer.campaign.beans.GiftCardSkus;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.PromoTrxLog;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDaoForDML;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDaoForDML;
import org.mq.marketer.campaign.dao.GiftCardSkusDao;
import org.mq.marketer.campaign.dao.GiftProgramsDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.PromoTrxLogDao;
import org.mq.marketer.campaign.dao.PromoTrxLogDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.optculture.business.couponcode.CouponCodeRedeemedService;
import org.mq.optculture.business.helper.CouponCodeProcessHelper;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionAuth;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionCustomer;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionItems;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionParams;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionPromotions;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionReceipt;
import org.mq.optculture.business.helper.DRToLty.DRToLoyaltyExtractionRedemption;
import org.mq.optculture.business.helper.DRToLty.DRToLtyExtractionData;
import org.mq.optculture.business.loyalty.AsyncGiftIssuance;
import org.mq.optculture.business.loyalty.LoyaltyEnrollmentOCService;
import org.mq.optculture.business.loyalty.LoyaltyIssuanceOCService;
import org.mq.optculture.business.loyalty.LoyaltyRedemptionOCService;
import org.mq.optculture.business.loyalty.LoyaltyReturnTransactionOCService;
import org.mq.optculture.business.loyalty.PerformRedemption;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.DR.DRBody;
import org.mq.optculture.model.DR.DROptCultureDetails;
import org.mq.optculture.model.DR.DigitalReceipt;
import org.mq.optculture.model.DR.OCPromotions;
import org.mq.optculture.model.DR.Receipt;
import org.mq.optculture.model.DR.heartland.HeartlandDRRequest;
import org.mq.optculture.model.DR.magento.MagentoBasedDRRequest;
import org.mq.optculture.model.DR.orion.OrionDRRequest;
import org.mq.optculture.model.DR.prism.PrismBasedDRRequest;
import org.mq.optculture.model.DR.shopify.ShopifyBasedDRRequest;
import org.mq.optculture.model.DR.tender.COD;
import org.mq.optculture.model.DR.tender.CreditCard;
import org.mq.optculture.model.DR.tender.CustomTender;
import org.mq.optculture.model.DR.tender.GiftCard;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRBody;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRHead;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRRequest;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceOrderDetails;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceReturnDRBody;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceReturnDRRequest;
import org.mq.optculture.model.couponcodes.CouponCodeInfo;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemReq;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemResponse;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemedObj;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemedResponse;
import org.mq.optculture.model.couponcodes.HeaderInfo;
import org.mq.optculture.model.couponcodes.PurchaseCouponInfo;
import org.mq.optculture.model.couponcodes.StatusInfo;
import org.mq.optculture.model.couponcodes.UserDetails;
import org.mq.optculture.model.digitalReceipt.CreditCardDR;
import org.mq.optculture.model.digitalReceipt.DRCOD;
import org.mq.optculture.model.digitalReceipt.DigitalReceiptBody;
import org.mq.optculture.model.digitalReceipt.DRCreditCard;
import org.mq.optculture.model.digitalReceipt.DRHead;
import org.mq.optculture.model.digitalReceipt.DRHead.User;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionRequestObject;
import org.mq.optculture.model.digitalReceipt.DRItem;
import org.mq.optculture.model.digitalReceipt.DRJsonRequest;
import org.mq.optculture.model.digitalReceipt.DRReceipt;
import org.mq.optculture.model.digitalReceipt.Items;
import org.mq.optculture.model.digitalReceipt.Promotions;
import org.mq.optculture.model.ocloyalty.AdditionalInfo;
import org.mq.optculture.model.ocloyalty.Amount;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.Discounts;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyReturnTransactionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyReturnTransactionResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.OTPRedeemLimit;
import org.mq.optculture.model.ocloyalty.OriginalReceipt;
import org.mq.optculture.model.ocloyalty.Promotion;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.SkuDetails;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.restservice.digitalReceipt.DRToLtyExtractionService;
import org.mq.optculture.utils.HeartlandRequestTranslator;
import org.mq.optculture.utils.MagentoRequestTranslator;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.OrionRequestTranslator;
import org.mq.optculture.utils.PrismRequestTranslator;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.WooCommerceRequestTranslator;
import org.mq.optculture.utils.ShopifyRequestTranslator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class DRToLtyExtractionImpl implements DRToLtyExtractionService {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		private final String REDEEMTENDERCC = "Credit Card";
		private final String REDEEMTENDERCT = "CustomTender";
		
	@Override
	public String processRequest(String drJsonId, boolean mustExtract) throws BaseServiceException {

		// TODO Auto-generated method stub 
		
		try{
			DigitalReceiptsJSONDao digitalReceiptsJSONDao;
			UsersDao usersDao;
			ServiceLocator context = ServiceLocator.getInstance();
			Gson gson = new Gson();
		digitalReceiptsJSONDao = (DigitalReceiptsJSONDao)context.getDAOByName(OCConstants.DIGITAL_RECEIPTS_JSON_DAO);
		usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		
		DigitalReceiptsJSON digitalReceiptsJSON = digitalReceiptsJSONDao.getDigitalReceiptJson(drJsonId);
		Users user=usersDao.findByUserId(digitalReceiptsJSON.getUserId());
		logger.debug("mustextract ==="+mustExtract);
		if(digitalReceiptsJSON!=null ) {
			String sourceOfDR = digitalReceiptsJSON.getSource();
			DRToLtyExtractionData DRToLtyExtractionData = null;
			if(sourceOfDR == null || (!sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_PRISM) && 
					!sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_OPTDR) && !sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_Magento)
					&&!sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_WooCommerce)
					&&!sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_Shopify)&&!sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_HEARTLAND)
					&&!sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_ORION)) ) {
				
				DRJsonRequest dRJsonRequest =null;
				try {
					 dRJsonRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), DRJsonRequest.class);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("could not parse");
				}
				
				if(dRJsonRequest == null) {
					logger.info("dr request"+dRJsonRequest);
					//steps before returning?
					return null;
				}
				if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
						!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
						dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
					//dont process transactions when it is resend.
					return null;
					
				}
				DRToLtyExtractionData = convertDRFromOldPlugin(dRJsonRequest, user);
				
			}else if(sourceOfDR != null && (sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_PRISM) ||
					sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_OPTDR) || sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_Magento)||
					sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_WooCommerce)||
					sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_Shopify) || sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_HEARTLAND)||
					sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_ORION))) {
				DigitalReceipt dRJsonRequest =null;
				if(sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_PRISM)) {
					try {
						gson = new Gson();
						PrismRequestTranslator PrismRequestTranslator = new PrismRequestTranslator();
						
						PrismBasedDRRequest prismRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), PrismBasedDRRequest.class);
						dRJsonRequest = PrismRequestTranslator.convertPrismRequest(prismRequest);
						if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
								!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
								dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend") && !mustExtract){
							//dont process transactions when it is resend.
							logger.debug("extraction returned====");
							return null;
							
						}
						DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest,user);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug("could not convert the json", e);
					}
					
				}else if(sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_Magento)) {
					try {
						gson = new Gson();
						MagentoRequestTranslator magentoRequestTranslator = new MagentoRequestTranslator();
						
						MagentoBasedDRRequest magentoRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), MagentoBasedDRRequest.class);
						dRJsonRequest = magentoRequestTranslator.convertMagentoRequest(magentoRequest,user);
						
						if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
								!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
								dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
							//dont process transactions when it is resend.
							return null;
							
						}

						DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug("could not convert the json", e);
					}
					
				}else if(sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_WooCommerce)) {
					try {
						gson = new Gson();
						WooCommerceRequestTranslator wooCommerceRequestTranslator = new WooCommerceRequestTranslator();
						
						JSONObject jsonMainObj = (JSONObject)JSONValue.parse(digitalReceiptsJSON.getJsonStr());
						WooCommerceDRHead headJson = gson.fromJson(jsonMainObj.get("Head").toString(), WooCommerceDRHead.class);
						String receiptStatus="";
						try {
						WooCommerceDRBody receipt = gson.fromJson(jsonMainObj.get("Body").toString(), WooCommerceDRBody.class);
						receiptStatus = receipt.getOrderdetails().getStatus();
						}catch(Exception e) {
						WooCommerceReturnDRBody refundReceipt = gson.fromJson(jsonMainObj.get("Body").toString(), WooCommerceReturnDRBody.class);
						receiptStatus = refundReceipt.getOrderdetails().getStatus();
						}
						if(headJson.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_SALE) ||
								receiptStatus.equalsIgnoreCase("cancelled")){
						WooCommerceDRRequest wooCommerceRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), WooCommerceDRRequest.class);
						dRJsonRequest = wooCommerceRequestTranslator.convertWooCommerceRequest(wooCommerceRequest,user);
						}else {
							WooCommerceReturnDRRequest wooCommerceReturnRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), WooCommerceReturnDRRequest.class);
							dRJsonRequest = wooCommerceRequestTranslator.convertWooCommerceRefundRequest(wooCommerceReturnRequest,user);
						}
						
						if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
								!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
								dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
							//dont process transactions when it is resend.
							return null;
							
						}

						DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug("could not convert the json", e);
					}
					
				}else if(sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_Shopify)) {
						try {
							gson = new Gson();
							ShopifyRequestTranslator shopifyRequestTranslator = new ShopifyRequestTranslator();
							ShopifyBasedDRRequest   shopifyRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), ShopifyBasedDRRequest.class);
							dRJsonRequest = shopifyRequestTranslator.convertShopifyRequest(shopifyRequest,user);
							
							if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
									!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
									dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
								//dont process transactions when it is resend.
								return null;
								
							}

							
							DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.debug("could not convert the json", e);
						}
					
				}
				else if(sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_HEARTLAND)) {  //APP-3389
					try {
						gson = new Gson();
						HeartlandRequestTranslator heartlandRequestTranslator = new HeartlandRequestTranslator();
						HeartlandDRRequest   heartlandRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), HeartlandDRRequest.class);
						dRJsonRequest = heartlandRequestTranslator.convertHeartlandRequest(heartlandRequest,user);

						if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
								!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
								dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
							//dont process transactions when it is resend.
							return null;

						}


						DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug("could not convert the json", e);
					}
				}
				//APP-4773
				else if(sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_ORION)) {
					try {
						gson = new Gson();
						OrionRequestTranslator orionRequestTranslator = new OrionRequestTranslator();
						OrionDRRequest orionRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), OrionDRRequest.class);
						dRJsonRequest = orionRequestTranslator.convertOrionRequest(orionRequest,user);

						if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
								!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
								dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
							//dont process transactions when it is resend.
							return null;

						}


						DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug("could not convert the json", e);
					}
				}
				else{
					
					try {
						logger.debug("checking ===");
						dRJsonRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), DigitalReceipt.class);
						if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
								!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
								dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend") && !mustExtract){
							//dont process transactions when it is resend.
							return null;
							
						}
						
						if(dRJsonRequest.getOptcultureDetails() != null ) {//APP-4623
							DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user);
							
						}else {
							
							DRToLtyExtractionData = convertDRFromNewPlugin(dRJsonRequest, user);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug("could not convert the json", e);
					}
					
				}
				if(dRJsonRequest == null) {
					logger.info("dr request"+dRJsonRequest);
					//steps before returning?
					return null;
				}
				
				//DRToLtyExtractionData = convertDR(dRJsonRequest, user);
				
			}
			if(user.isGiftCardProgram()) {//Gift card flow - APP-4603
				processGiftIssuance(user,DRToLtyExtractionData);
			}
			String cardNumber = "";
			if(user.isEnableLoyaltyExtraction()) {
				cardNumber =  extractLoyaltyFromDR(DRToLtyExtractionData,user, digitalReceiptsJSON);
			}
			if(user.isEnablePromoRedemption()) {
				promoRedeemFromDR(DRToLtyExtractionData,user);
			}
			return cardNumber;
			
		}
		}catch(Exception e){
			logger.error("Exception ",e);
		}
		return null;
	
	}
	
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseResponseObject) throws BaseServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponseObject processRequest() throws BaseServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String processRequest(String drJsonId) throws BaseServiceException {
		// TODO Auto-generated method stub
		
		try{
			DigitalReceiptsJSONDao digitalReceiptsJSONDao;
			UsersDao usersDao;
			ServiceLocator context = ServiceLocator.getInstance();
			Gson gson = new Gson();
		digitalReceiptsJSONDao = (DigitalReceiptsJSONDao)context.getDAOByName(OCConstants.DIGITAL_RECEIPTS_JSON_DAO);
		usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		
		DigitalReceiptsJSON digitalReceiptsJSON = digitalReceiptsJSONDao.getDigitalReceiptJson(drJsonId);
		Users user=usersDao.findByUserId(digitalReceiptsJSON.getUserId());
		
		if(digitalReceiptsJSON!=null ) {
			String sourceOfDR = digitalReceiptsJSON.getSource();
			DRToLtyExtractionData DRToLtyExtractionData = null;
			if(sourceOfDR == null || (!sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_PRISM) && 
					!sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_OPTDR) && !sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_Magento)
					&&!sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_WooCommerce)
					&&!sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_Shopify)&&!sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_HEARTLAND)
					&&!sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_ORION)) ) {
				
				DRJsonRequest dRJsonRequest =null;
				try {
					 dRJsonRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), DRJsonRequest.class);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("could not parse");
				}
				
				if(dRJsonRequest == null) {
					logger.info("dr request"+dRJsonRequest);
					//steps before returning?
					return null;
				}
				if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
						!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
						dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
					//dont process transactions when it is resend.
					return null;
					
				}
				DRToLtyExtractionData = convertDRFromOldPlugin(dRJsonRequest, user);
				
			}else if(sourceOfDR != null && (sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_PRISM) ||
					sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_OPTDR) || sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_Magento)||
					sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_WooCommerce)||
					sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_Shopify)||sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_HEARTLAND)||
					sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_ORION))) {
				DigitalReceipt dRJsonRequest =null;
				if(sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_PRISM)) {
					try {
						gson = new Gson();
						PrismRequestTranslator PrismRequestTranslator = new PrismRequestTranslator();
						
						PrismBasedDRRequest prismRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), PrismBasedDRRequest.class);
						dRJsonRequest = PrismRequestTranslator.convertPrismRequest(prismRequest);
						if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
								!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
								dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
							//dont process transactions when it is resend.
							return null;
							
						}
						DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest,user);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug("could not convert the json", e);
					}
					
				}else if(sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_Magento)) {
					try {
						gson = new Gson();
						MagentoRequestTranslator magentoRequestTranslator = new MagentoRequestTranslator();
						
						MagentoBasedDRRequest magentoRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), MagentoBasedDRRequest.class);
						dRJsonRequest = magentoRequestTranslator.convertMagentoRequest(magentoRequest,user);
						
						if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
								!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
								dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
							//dont process transactions when it is resend.
							return null;
							
						}

						DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug("could not convert the json", e);
					}
					
				}else if(sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_WooCommerce)) {
					try {
						gson = new Gson();
						WooCommerceRequestTranslator wooCommerceRequestTranslator = new WooCommerceRequestTranslator();
						
						JSONObject jsonMainObj = (JSONObject)JSONValue.parse(digitalReceiptsJSON.getJsonStr());
						WooCommerceDRHead headJson = gson.fromJson(jsonMainObj.get("Head").toString(), WooCommerceDRHead.class);
						String receiptStatus="";
						try {
						WooCommerceDRBody receipt = gson.fromJson(jsonMainObj.get("Body").toString(), WooCommerceDRBody.class);
						receiptStatus = receipt.getOrderdetails().getStatus();
						}catch(Exception e) {
						WooCommerceReturnDRBody refundReceipt = gson.fromJson(jsonMainObj.get("Body").toString(), WooCommerceReturnDRBody.class);
						receiptStatus = refundReceipt.getOrderdetails().getStatus();
						}
						if(headJson.getReceiptType().equalsIgnoreCase(OCConstants.DR_RECEIPT_TYPE_SALE) ||
								receiptStatus.equalsIgnoreCase("cancelled")){
						WooCommerceDRRequest wooCommerceRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), WooCommerceDRRequest.class);
						dRJsonRequest = wooCommerceRequestTranslator.convertWooCommerceRequest(wooCommerceRequest,user);
						}else {
							WooCommerceReturnDRRequest wooCommerceReturnRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), WooCommerceReturnDRRequest.class);
							dRJsonRequest = wooCommerceRequestTranslator.convertWooCommerceRefundRequest(wooCommerceReturnRequest,user);
						}
						
						if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
								!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
								dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
							//dont process transactions when it is resend.
							return null;
							
						}

						DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug("could not convert the json", e);
					}
					
				}else if(sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_Shopify)) {
						try {
							gson = new Gson();
							ShopifyRequestTranslator shopifyRequestTranslator = new ShopifyRequestTranslator();
							ShopifyBasedDRRequest   shopifyRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), ShopifyBasedDRRequest.class);
							dRJsonRequest = shopifyRequestTranslator.convertShopifyRequest(shopifyRequest,user);
							
							if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
									!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
									dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
								//dont process transactions when it is resend.
								return null;
								
							}

							
							DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.debug("could not convert the json", e);
						}
					
				}
				
				else if(sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_HEARTLAND)) {
					try {
						gson = new Gson();
						HeartlandRequestTranslator heartlandRequestTranslator = new HeartlandRequestTranslator();
						HeartlandDRRequest   heartlandRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), HeartlandDRRequest.class);
						dRJsonRequest = heartlandRequestTranslator.convertHeartlandRequest(heartlandRequest,user);

						if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
								!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
								dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
							//dont process transactions when it is resend.
							return null;

						}


						DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug("could not convert the json", e);
					}
				}
				//APP-4773
				else if(sourceOfDR.equals(OCConstants.DR_SOURCE_TYPE_ORION)) {
					try {
						gson = new Gson();
						OrionRequestTranslator orionRequestTranslator = new OrionRequestTranslator();
						OrionDRRequest orionRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), OrionDRRequest.class);
						dRJsonRequest = orionRequestTranslator.convertOrionRequest(orionRequest,user);

						if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
								!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
								dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
							//dont process transactions when it is resend.
							return null;

						}


						DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug("could not convert the json", e);
					}
				}
				else{
					
					try {
						
						dRJsonRequest = gson.fromJson(digitalReceiptsJSON.getJsonStr(), DigitalReceipt.class);
						if(dRJsonRequest.getHead() != null && dRJsonRequest.getHead().getRequestType() != null && 
								!dRJsonRequest.getHead().getRequestType().trim().isEmpty() && 
								dRJsonRequest.getHead().getRequestType().trim().equalsIgnoreCase("Resend")){
							//dont process transactions when it is resend.
							return null;
							
						}
						if(dRJsonRequest.getOptcultureDetails() != null ) {//APP-4623
							DRToLtyExtractionData = convertDRFromOtherSources(dRJsonRequest, user);
							
						}else {
							
							DRToLtyExtractionData = convertDRFromNewPlugin(dRJsonRequest, user);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug("could not convert the json", e);
					}
					
				}
				if(dRJsonRequest == null) {
					logger.info("dr request"+dRJsonRequest);
					//steps before returning?
					return null;
				}
				
				//DRToLtyExtractionData = convertDR(dRJsonRequest, user);
				
			}
			if(user.isGiftCardProgram()) {//Gift card flow - APP-4603
				processGiftIssuance(user,DRToLtyExtractionData);
			}
			String cardNumber = "";
			if(user.isEnableLoyaltyExtraction()) {
				cardNumber = extractLoyaltyFromDR(DRToLtyExtractionData,user, digitalReceiptsJSON);
			}
			if(user.isEnablePromoRedemption()) {
				promoRedeemFromDR(DRToLtyExtractionData,user);
			}
			return cardNumber;
			
		}
		}catch(Exception e){
			logger.error("Exception ",e);
		}
		return null;
	}
	public DRToLtyExtractionData convertDRFromOldPlugin(DRJsonRequest DRFromOldPlugin, Users user){
		
		
		try {
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			
			
			DRReceipt receipt = DRFromOldPlugin.getBody().getReceipt();
			List<DRCreditCard> cc = DRFromOldPlugin.getBody().getCreditCard();
			List<CreditCard> DRCC = null;
			COD DRCOD = null;
			Gson gson = new Gson();
			if(cc != null && !cc.isEmpty()){
				DRCC = new ArrayList<CreditCard>();
				for (DRCreditCard drCreditCard : cc) {
					
					String json = gson.toJson(drCreditCard);
					CreditCard drCC = gson.fromJson(json, CreditCard.class);
					DRCC.add(drCC);
				}
			}
			
			DRCOD cod = DRFromOldPlugin.getBody().getCOD();
			if(cod != null){
				
				String json = gson.toJson(cod);
				DRCOD = gson.fromJson(json, COD.class);
			}
			if((receipt.getBillToPhone1() != null && !receipt.getBillToPhone1().trim().isEmpty())) {
				logger.info("no card number find with mobile based prog");
				String customerphoneParse = Utility.phoneParse(receipt.getBillToPhone1().trim(),
						user != null ? user.getUserOrganization() : null);
				if(customerphoneParse != null) {
					
					
					receipt.setBillToPhone1(customerphoneParse);
				}
			}
			
			DRToLoyaltyExtractionReceipt DRReceipt = new DRToLoyaltyExtractionReceipt(receipt.getDocDate(), receipt.getInvcNum(),
					receipt.getDocTime(), receipt.getDocSID(), receipt.getStore(), receipt.getSubsidiaryNumber(), receipt.getTotal(), 
					receipt.getSubtotal(), DRCC, DRCOD,receipt.getInvcHdrNotes(),receipt.getInvcComment1(),receipt.getInvcComment2(),null,receipt.getRefDocSID(),null, null,null,null);
			
			PropertyDescriptor cdi = null;
			String cardInfo = user.getCardInfo();
			String cardInfoObjStr=Constants.STRING_NILL;
			if(cardInfo!=null) {
				cdi = new PropertyDescriptor(cardInfo, receipt.getClass());
				Object cardInfoObj = cdi.getReadMethod().invoke(receipt);
				if(cardInfoObj != null && !cardInfoObj.toString().isEmpty() ) {
					cardInfoObjStr = cardInfoObj.toString();
				}
			}
			
			ContactsLoyalty contactsLoyalty = null;
			boolean mobileBasedEnroll=false; 
			if(cardInfoObjStr == null || cardInfoObjStr.isEmpty()) {
				LoyaltyProgram defaultLoyaltyProgram = findDefaultProgram(user.getUserId());
				if(defaultLoyaltyProgram !=null) {
					if(defaultLoyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
						if((receipt.getBillToPhone1() != null && !receipt.getBillToPhone1().trim().isEmpty())) {
							logger.info("no card number find with mobile based prog");
							contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),receipt.getBillToPhone1());
							if(contactsLoyalty==null);mobileBasedEnroll=true;
						}else{
							mobileBasedEnroll=true;
						}
					}
				}
			}else {
				logger.info("find with card number ");
				//look up with card number
			    contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),cardInfoObjStr);
			}
			if(contactsLoyalty==null) {
				String custID = receipt.getBillToCustSID();
				if(custID!=null && !custID.isEmpty()){
					contactsLoyalty = findLoyaltyCardInOCByCustId(custID, user.getUserId());
				}
			}
			DRToLoyaltyExtractionCustomer customer = new DRToLoyaltyExtractionCustomer(receipt.getBillToCustSID(), null, receipt.getBillToFName(), 
					receipt.getBillToLName(), receipt.getBillToPhone1(), receipt.getBillToEMail(), receipt.getBillToAddr1(), 
					null, receipt.getBillToAddr2(), receipt.getBillToAddr3(), receipt.getBillToZip(), null, receipt.getBillToUDF1(), 
					receipt.getBillToUDF2(), null, null, cardInfoObjStr, receipt.getBillToInfo2(), contactsLoyalty);
			
			List<DRItem> DRitems = DRFromOldPlugin.getBody().getItems();
			List<DRToLoyaltyExtractionItems> items = new ArrayList<DRToLoyaltyExtractionItems>();
			PropertyDescriptor pdi = null;
			String itemNoteUsed = user.getItemNoteUsed();
			for (DRItem item : DRitems) {
				String itemNoteObjStr=Constants.STRING_NILL;
				//double itemQty=(Double.valueOf(item.getQty()));
				//String qty=item.getQty();
				if(itemNoteUsed!=null) {
				pdi = new PropertyDescriptor(itemNoteUsed, item.getClass());
				Object itemNoteObj = pdi.getReadMethod().invoke(item);
				if(itemNoteObj != null && !itemNoteObj.toString().isEmpty() ) {
				itemNoteObjStr = itemNoteObj.toString();
				/*//Exclude qty in issuance items, based on item note qty (cc:qty,disc|--)
				String[] promoInfosArr =null;
				if(itemNoteObjStr.contains(""+Constants.DELIMITER_PIPE)){
					promoInfosArr = itemNoteObjStr.split("\\"+Constants.DELIMITER_PIPE);
					for (String promoInfo : promoInfosArr) {
						String[] promoInfoArr = promoInfo.split(""+Constants.DELIMETER_COLON);
						String[] qtyInfoArr = promoInfoArr[1].split(""+Constants.DELIMETER_COMMA);
						itemQty = itemQty>0 ? (qtyInfoArr[0]!=null && !qtyInfoArr[0].isEmpty() ? itemQty-Double.valueOf(qtyInfoArr[0]):itemQty):itemQty;
					}
				}else {
						String[] promoInfoArr = itemNoteObjStr.split(""+Constants.DELIMETER_COLON);
						String[] qtyInfoArr = promoInfoArr[1].split(""+Constants.DELIMETER_COMMA);
						itemQty = itemQty>0 ? (qtyInfoArr[0]!=null && !qtyInfoArr[0].isEmpty() ? itemQty-Double.valueOf(qtyInfoArr[0]):itemQty):itemQty;
				}
				qty=String.valueOf(itemQty);*/
				}
				}
				DRToLoyaltyExtractionItems drItem = new DRToLoyaltyExtractionItems(item.getItemCategory() != null && !item.getItemCategory().isEmpty() ? item.getItemCategory() : item.getDCSName(), 
								(item.getDepartmentCode()!=null && !item.getDepartmentCode().isEmpty()) ? item.getDepartmentCode() :"", 
								(item.getItemClass()!=null && !item.getItemClass().isEmpty()) ? item.getItemClass() : "", 
								(item.getItemSubClass()!=null && !item.getItemSubClass().isEmpty()) ? item.getItemSubClass() : "", 
								(item.getDCS()!=null && !item.getDCS().isEmpty()) ? item.getDCS() :"", 
								(item.getVendorCode()!=null && !item.getVendorCode().isEmpty()) ? item.getVendorCode() : "",
								(item.getUPC()!=null && !item.getUPC().isEmpty()) ? item.getUPC() : "", 
								item.getDocItemPrc()==null ? item.getInvcItemPrc() : item.getDocItemPrc(), 
								item.getQty(), (item.getTax()!=null && !item.getTax().isEmpty()) ? item.getTax() : "", 
								(item.getDocItemDiscAmt()!=null && !item.getDocItemDiscAmt().isEmpty()) ? item.getDocItemDiscAmt() :"", 
								(item.getDepartmentName()!=null && !item.getDepartmentName().isEmpty()) ? item.getDepartmentName() :"", 
								(item.getItemClassName()!=null && !item.getItemClassName().isEmpty()) ? item.getItemClassName() : "", 
								(item.getItemSubClassName()!=null && !item.getItemSubClassName().isEmpty()) ? item.getItemSubClassName() : "", 
								(item.getVendorName()!=null && !item.getVendorName().isEmpty()) ? item.getVendorName() :"", 
								(item.getItemSID()!=null && !item.getItemSID().isEmpty()) ? item.getItemSID() :"",
								(item.getRefStoreNum()!=null && !item.getRefStoreNum().isEmpty()) ? item.getRefStoreNum() :"", 
								(item.getRefSubsidiaryNumber()!=null && !item.getRefSubsidiaryNumber().isEmpty()) ? item.getRefSubsidiaryNumber() :"",
								(item.getRefReceiptNum()!=null && !item.getRefReceiptNum().isEmpty()) ? item.getRefReceiptNum() :"",
								(item.getRefDocSid()!=null && !item.getRefDocSid().isEmpty()) ? item.getRefDocSid() :"",
								(item.getDiscountReason()!=null && !item.getDiscountReason().isEmpty() ? item.getDiscountReason():""));
				drItem.setDocItemOrigPrc(item.getDocItemOrigPrc()!=null && !item.getDocItemOrigPrc().isEmpty() ? item.getDocItemOrigPrc() :"");
				drItem.setInvcItemPrc(item.getInvcItemPrc()!=null && !item.getInvcItemPrc().isEmpty() ? item.getInvcItemPrc() :"");
				drItem.setDiscount(item.getDocItemDiscAmt() !=null && !item.getDocItemDiscAmt().isEmpty() ? item.getDocItemDiscAmt() :
					   (item.getDocItemDisc()!=null && !item.getDocItemDisc().isEmpty()?item.getDocItemDisc():""));
				drItem.setItemNote(itemNoteObjStr!=null ? itemNoteObjStr : "");
				//drItem.setQuantity(qty!=null && !qty.isEmpty()?qty:item.getQty());
				items.add(drItem);
				
			}
			
			
			
			
			
			//DRToLoyaltyExtractionParams headerFileds = new DRToLoyaltyExtractionParams(null, null);
			DRToLoyaltyExtractionParams headerFileds = new DRToLoyaltyExtractionParams(DRFromOldPlugin.getHead().getEnrollCustomer(),
					DRFromOldPlugin.getHead().getIsLoyaltyCustomer(),DRFromOldPlugin.getHead().getRequestSource(), DRFromOldPlugin.getHead().getRequestType());
			
			
			String userName=Constants.STRING_NILL;
			String token =Constants.STRING_NILL;
			String orgId =Constants.STRING_NILL;
			
			if(DRFromOldPlugin.getHead()==null){
				userName=(DRFromOldPlugin.getBody().getUserDetails().getUserName()!=null && 
						!DRFromOldPlugin.getBody().getUserDetails().getUserName().isEmpty() ? DRFromOldPlugin.getBody().getUserDetails().getUserName() 
								: "");
				token=(DRFromOldPlugin.getBody().getUserDetails().getToken()!=null && 
						!DRFromOldPlugin.getBody().getUserDetails().getToken().isEmpty() ? DRFromOldPlugin.getBody().getUserDetails().getToken() :"");
				orgId=(DRFromOldPlugin.getBody().getUserDetails().getOrganisation()!=null && 
						!DRFromOldPlugin.getBody().getUserDetails().getOrganisation().isEmpty() ? DRFromOldPlugin.getBody().getUserDetails().getOrganisation() :"");
			}else {
				if(DRFromOldPlugin.getHead().getUser() == null) {
					DRHead head = new DRHead();
					User druser = head.new User();
					druser.setOrganizationId(user.getUserOrganization().getOrgExternalId());
					druser.setToken(user.getUserOrganization().getOptSyncKey()); 
					druser.setUserName(Utility.getOnlyUserName(user.getUserName()));
					head.setUser(druser);
					DRFromOldPlugin.setHead(head);
				}else {
					
					userName=DRFromOldPlugin.getHead().getUser().getUserName();
					token=DRFromOldPlugin.getHead().getUser().getToken();
					orgId=DRFromOldPlugin.getHead().getUser().getOrganizationId();
				}
			}
			if(userName.isEmpty() || token.isEmpty() || orgId.isEmpty()) {
				orgId = user.getUserOrganization().getOrgExternalId();
				userName = Utility.getOnlyUserName(user.getUserName());
				token = user.getUserOrganization().getOptSyncKey();
				
			}
			DRToLoyaltyExtractionAuth authUser = new DRToLoyaltyExtractionAuth(userName,orgId, token);
			DRToLtyExtractionData DRToLtyExtractionData = new DRToLtyExtractionData(customer, items, authUser, headerFileds, DRReceipt);
			DRToLtyExtractionData.setOTPCode("");
			
			DRToLtyExtractionData.setMobileBasedEnroll(mobileBasedEnroll);
			
			//Prepare promotions
			//ItemNote syntax cc:qty:disc:1.0-2 (ratio)
			if(user.isEnablePromoRedemption() && user.getItemNoteUsed()!=null){
			Map<String, String> distinctPromos = new HashMap<String, String>();
				String usedItemNote = user.getItemNoteUsed();
				String usedItemInfo = user.getItemInfo();
				PropertyDescriptor pdItemNote  = null;
				PropertyDescriptor pdItemInfo  = null;
				List<DRItem> promoItems = DRFromOldPlugin.getBody().getItems();
				//promo applied item level
				for (DRItem drItem : promoItems) {
					pdItemNote = new PropertyDescriptor(usedItemNote, drItem.getClass());
					if(pdItemNote == null ) continue;
					Object itemNoteObj = pdItemNote.getReadMethod().invoke(drItem);
					if(itemNoteObj == null || itemNoteObj.toString().isEmpty() ) continue;
					String itemNoteObjStr = itemNoteObj.toString();
					Object itemInfoObj = null;
					if(usedItemInfo!=null) {
						pdItemInfo = new PropertyDescriptor(usedItemInfo, drItem.getClass());
						itemInfoObj = pdItemInfo.getReadMethod().invoke(drItem);
					}
					String itemInfoObjStr = itemInfoObj!=null?itemInfoObj.toString():"";
					String[] promoInfosArr =null;
					// this may be a config field drItem.getDCS()
					if(itemNoteObjStr.contains(""+Constants.DELIMITER_PIPE)){
						promoInfosArr = itemNoteObjStr.split("\\"+Constants.DELIMITER_PIPE);
						for (String promoInfo : promoInfosArr) {
							String[] promoInfoArr = promoInfo.split(""+Constants.DELIMETER_COLON);
							//String[] qtyInfoArr = promoInfoArr[1].split(""+Constants.DELIMETER_COMMA);
								String itemInfo = distinctPromos.get(promoInfoArr[0]);
								if(itemInfo != null && !itemInfo.isEmpty()) itemInfo += Constants.DELIMITER_PIPE;
								
								itemInfo = (itemInfo == null ? 
										(itemInfoObjStr!=null && !itemInfoObjStr.isEmpty() ? itemInfoObjStr+""+Constants.DELIMETER_COLON+promoInfoArr[1]
												+""+Constants.DELIMETER_COLON+""+promoInfoArr[2]+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"") :
											drItem.getItemLookup()+""+Constants.DELIMETER_COLON+promoInfoArr[1]+""+Constants.DELIMETER_COLON+promoInfoArr[2]
													+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"")) : 
										(itemInfoObjStr!=null && !itemInfoObjStr.isEmpty() ? itemInfo+itemInfoObjStr+Constants.DELIMETER_COLON+promoInfoArr[1]
												+""+Constants.DELIMETER_COLON+promoInfoArr[2]+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"") :
										itemInfo+drItem.getItemLookup()+Constants.DELIMETER_COLON+promoInfoArr[1]+""+Constants.DELIMETER_COLON+promoInfoArr[2]
												+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"")));
								distinctPromos.put(promoInfoArr[0], itemInfo);
							}
						}else{
							String[] promoInfoArr = itemNoteObjStr.split(""+Constants.DELIMETER_COLON);
							//String[] qtyInfoArr = promoInfoArr[1].split(""+Constants.DELIMETER_COMMA);
							String itemInfo = distinctPromos.get(promoInfoArr[0]);
							if(itemInfo != null && !itemInfo.isEmpty()) itemInfo += Constants.DELIMITER_PIPE;
							
							itemInfo = (itemInfo == null ? 
									(itemInfoObjStr!=null && !itemInfoObjStr.isEmpty() ? itemInfoObjStr+""+Constants.DELIMETER_COLON+promoInfoArr[1]
											+""+Constants.DELIMETER_COLON+""+promoInfoArr[2]+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"") :
										drItem.getItemLookup()+""+Constants.DELIMETER_COLON+promoInfoArr[1]+""+Constants.DELIMETER_COLON+promoInfoArr[2]
												+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"")) : 
									(itemInfoObjStr!=null && !itemInfoObjStr.isEmpty() ? itemInfo+itemInfoObjStr+Constants.DELIMETER_COLON+promoInfoArr[1]
											+""+Constants.DELIMETER_COLON+promoInfoArr[2]+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"") :
									itemInfo+drItem.getItemLookup()+Constants.DELIMETER_COLON+promoInfoArr[1]+""+Constants.DELIMETER_COLON+promoInfoArr[2]
											+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"")));
							distinctPromos.put(promoInfoArr[0], itemInfo);
						}
					}
				//promo applied receipt level
				if(user.getReceiptNoteUsed()!=null) {
				String usedReceiptNote = user.getReceiptNoteUsed();
				PropertyDescriptor pdReceiptNote = null;
				pdReceiptNote = new PropertyDescriptor(usedReceiptNote, receipt.getClass());
				Object receiptNoteObj = pdReceiptNote.getReadMethod().invoke(receipt);
				if(receiptNoteObj != null && !receiptNoteObj.toString().isEmpty() ) {
				String receiptNoteObjStr = receiptNoteObj.toString();
				if(receiptNoteObjStr.contains(""+Constants.DELIMITER_PIPE)){
					String[] promoInfosArr = receiptNoteObjStr.split("\\"+Constants.DELIMITER_PIPE);
					for (String promoInfo : promoInfosArr) {
						String[] promoInfoArr = promoInfo.split(""+Constants.DELIMETER_COLON);
						distinctPromos.put(promoInfoArr[0],receiptNoteObjStr);
					}
				}else{
						String[] promoInfoArr = receiptNoteObjStr.split(""+Constants.DELIMETER_COLON);
						//distinctPromos.put(promoInfoArr[0],""+Constants.DELIMETER_COLON+promoInfoArr[1]);
						distinctPromos.put(promoInfoArr[0],receiptNoteObjStr);
				}
				}
				}
				if(distinctPromos.size() > 0){
					List<DRToLoyaltyExtractionPromotions> promotionsLst = new ArrayList<DRToLoyaltyExtractionPromotions>();
					for (String promo : distinctPromos.keySet()) {
						
						
						Double discountAmount = 0.0;
						
						String itemINfo = distinctPromos.get(promo);
						if(itemINfo.contains(""+Constants.DELIMITER_PIPE)){
							String[] itemInfoArr = itemINfo.split("\\"+Constants.DELIMITER_PIPE);
							for (String itemInfo : itemInfoArr) {
								String[] discArr = itemInfo.split(""+Constants.DELIMETER_COLON);
								discountAmount += (discArr.length>2?(Double.parseDouble(discArr[2]) * Double.parseDouble(discArr[1])):(Double.parseDouble(discArr[1])));
							}
							
						}else{
								if(itemINfo.contains(""+Constants.DELIMETER_COLON)) {
									String[] discArr = itemINfo.split(""+Constants.DELIMETER_COLON);
									discountAmount += (discArr.length>2?(Double.parseDouble(discArr[2]) * Double.parseDouble(discArr[1])):(Double.parseDouble(discArr[1])));
								}
						}
						logger.info("discountAmount "+discountAmount);
						DRToLoyaltyExtractionPromotions promotion = new DRToLoyaltyExtractionPromotions();
						promotion.setCouponCode(promo);
						promotion.setDiscountAmount(Utility.truncateUptoTwoDecimal(discountAmount));
						promotion.setItemCodeInfo(itemINfo);
						
						promotionsLst.add(promotion);						
					}
					DRToLtyExtractionData.setPromotions(promotionsLst);
					
				}
				
				}
			boolean isTenderExists = false;
			String tenderTaken = Constants.STRING_NILL;
			String tenderGiven = Constants.STRING_NILL;
			String userRedeemTender = user.getRedeemTender();
			if(userRedeemTender!=null && !userRedeemTender.isEmpty()) {
				if(userRedeemTender.contains(""+Constants.DELIMETER_COLON)) {
					String tenderType=user.getRedeemTender().split(""+Constants.DELIMETER_COLON)[1];
				List<CreditCard> listOfCards =DRCC;
				
				if(listOfCards != null && listOfCards.size() != 0){
					for(CreditCard creditCard:listOfCards){
						if(creditCard.getType()!=null && 
								!creditCard.getType().isEmpty() && 
								creditCard.getType().equalsIgnoreCase(tenderType) ){
							tenderTaken=(creditCard.getTaken()!=null && !creditCard.getTaken().isEmpty() ? creditCard.getTaken() : creditCard.getAmount());
							tenderGiven=(creditCard.getGiven()!=null && !creditCard.getGiven().isEmpty()? creditCard.getGiven() : creditCard.getAmount());
							if((tenderTaken!=null && !tenderTaken.isEmpty())||(tenderGiven!=null && !tenderGiven.isEmpty())) isTenderExists =true;
						}
					}
				}
			}else {
				COD COD = DRCOD;
				if(COD!=null)tenderTaken=(COD.getTaken()!=null && !COD.getTaken().isEmpty()? COD.getTaken() : COD.getAmount());
				if(COD!=null)tenderGiven=(COD.getGiven()!=null && !COD.getGiven().isEmpty() ? COD.getGiven() : COD.getAmount());
				if((tenderTaken!=null && !tenderTaken.isEmpty())||(tenderGiven!=null && !tenderGiven.isEmpty())) isTenderExists =true;
			}
				
			}
			DRToLoyaltyExtractionRedemption redemptionDetails = new DRToLoyaltyExtractionRedemption();
			if(isTenderExists) {
				redemptionDetails.setRedemptionAmount(tenderTaken);
				redemptionDetails.setLoyaltyRedeemReversal(tenderGiven);
			}
			DRToLtyExtractionData.setRedemptionDetails(redemptionDetails);
			
			return DRToLtyExtractionData;
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		}
		
		return null;
	}
	public DRToLtyExtractionData convertDRFromNewPlugin(DigitalReceipt DRFromV9ORPrism,Users user){
		
		
		try {
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			String source = DRFromV9ORPrism.getHead().getRequestSource();
			
			Receipt receipt = DRFromV9ORPrism.getBody().getReceipt();
			List<CreditCard> DRCC = DRFromV9ORPrism.getBody().getCreditCard();
			COD DRCOD = DRFromV9ORPrism.getBody().getCOD();
			
			PropertyDescriptor cdi = null;
			String cardInfo = user.getCardInfo();
			String cardInfoObjStr=Constants.STRING_NILL;
			if(cardInfo!=null) {
				cdi = new PropertyDescriptor(cardInfo, receipt.getClass());
				Object cardInfoObj = cdi.getReadMethod().invoke(receipt);
				if(cardInfoObj != null && !cardInfoObj.toString().isEmpty() ) {
					cardInfoObjStr = cardInfoObj.toString();
				}
			}
			if((receipt.getBillToPhone1() != null && !receipt.getBillToPhone1().trim().isEmpty())) {
				logger.info("no card number find with mobile based prog");
				String customerphoneParse = Utility.phoneParse(receipt.getBillToPhone1().trim(),
						user != null ? user.getUserOrganization() : null);
				if(customerphoneParse != null) {
					
					
					receipt.setBillToPhone1(customerphoneParse);
				}
			}
			
			DRToLoyaltyExtractionReceipt DRReceipt = new DRToLoyaltyExtractionReceipt(receipt.getDocDate(), receipt.getInvcNum(),
					receipt.getDocTime(), receipt.getDocSID(), receipt.getStore(), receipt.getSubsidiaryNumber(), receipt.getTotal(), 
					receipt.getSubtotal(), DRCC, DRCOD,receipt.getInvcHdrNotes(),receipt.getInvcComment1(),receipt.getInvcComment2(),
					receipt.getECOMOrderNo(),receipt.getRefDocSID(),receipt.getRefReceipt(), receipt.getRefStoreCode(), receipt.getRefSubsidiaryNumber(),receipt.getDiscount());
			DRReceipt.setBillToInfo1(receipt.getBillToInfo1());
			DRReceipt.setCustomTender(DRFromV9ORPrism.getBody().getCustomTender());
			
			ContactsLoyalty contactsLoyalty = null;
			boolean mobileBasedEnroll=false; 
			/*if(cardInfoObjStr == null || cardInfoObjStr.isEmpty()) {
				LoyaltyProgram defaultLoyaltyProgram = findDefaultProgram(user.getUserId());
				if(defaultLoyaltyProgram !=null) {
					if(defaultLoyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
						if((receipt.getBillToPhone1() != null && !receipt.getBillToPhone1().trim().isEmpty())) {
							logger.info("no card number find with mobile based prog");
							contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),receipt.getBillToPhone1());
							if(contactsLoyalty==null);mobileBasedEnroll=true;
						}
					}
				}
			}else {
				logger.info("find with card number ");
				//look up with card number
			    contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),cardInfoObjStr);
			}*/
			boolean perkBased = false;
            LoyaltyProgramDao prgmDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			
			List<LoyaltyProgram> listOfPrgms = prgmDao.getAllProgramsListByUserId(user.getUserId());
			LoyaltyProgram defaultLoyaltyProgram = null;
			LoyaltyProgram perkProg = null;
			if(listOfPrgms!=  null) {
				
				for(LoyaltyProgram prgm : listOfPrgms) {
					
					if(prgm.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED)) continue;
					if(prgm.getDefaultFlag()== OCConstants.FLAG_YES ) {
						defaultLoyaltyProgram = prgm;
						
					}
					if(prgm.getRewardType() != null && prgm.getRewardType().equals(OCConstants.REWARD_TYPE_PERK) )
						perkProg = prgm;
					
				}
			}
			if(cardInfoObjStr == null || cardInfoObjStr.isEmpty()) {
				//contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),receipt.getBillToPhone1());
				if(perkProg != null) {
					String phone =  receipt.getBillToPhone1();
					if(phone != null && !phone.isEmpty() && perkProg.getUniqueMobileFlag()==OCConstants.FLAG_YES) {
						
						String phoneParse = Utility.phoneParse(phone.trim(),
								user != null ? user.getUserOrganization() : null);
						contactsLoyalty = contactsLoyaltyDao.getLoyaltyByPrgmAndPhone(user,perkProg.getProgramId(), phoneParse);
						if(contactsLoyalty != null ) {
							perkBased = true;
						}
						
					}
					if(contactsLoyalty == null && perkProg.getUniqueEmailFlag()==OCConstants.FLAG_YES && 
							receipt.getBillToEMail()!= null && !receipt.getBillToEMail().isEmpty() ) {
						
						contactsLoyalty = contactsLoyaltyDao.findMembershipByEmailInCl( receipt.getBillToEMail(), user.getUserId(),perkProg.getProgramId());
						if(contactsLoyalty != null ) {
							perkBased = true;
						}
					}
					
				}
				if(contactsLoyalty == null && defaultLoyaltyProgram !=null) {
					if(defaultLoyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
						
						if((receipt.getBillToPhone1() != null && !receipt.getBillToPhone1().trim().isEmpty())) {
							logger.info("no card number find with mobile based prog");
							contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),receipt.getBillToPhone1());
							if(contactsLoyalty==null);mobileBasedEnroll=true;
						}else{
							mobileBasedEnroll=true;
						}
					}
				}
			}else {
					if(cardInfoObjStr != null && !cardInfoObjStr.isEmpty()) {
						contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),cardInfoObjStr);
						if(contactsLoyalty!=null && perkProg!=null) {
					    	logger.info("contatcs Loyalty not null && perk not null");
					    	if(contactsLoyalty.getProgramId()==perkProg.getProgramId()) {
					    		perkBased = true;
					    		logger.info("perk basd program ?>>>>"+perkBased);
					    	}
					    }
					}
				}
			if(contactsLoyalty==null) {
				String custID = receipt.getBillToCustSID();
				if(custID!=null && !custID.isEmpty()){
					contactsLoyalty = findLoyaltyCardInOCByCustId(custID, user.getUserId());
				}
			}
			
			DRToLoyaltyExtractionCustomer customer = new DRToLoyaltyExtractionCustomer(receipt.getBillToCustSID(), null, receipt.getBillToFName(), 
					receipt.getBillToLName(), receipt.getBillToPhone1(), receipt.getBillToEMail(), receipt.getBillToAddr1(), 
					null, receipt.getBillToAddr2(), receipt.getBillToAddr3(), receipt.getBillToZip(), null, receipt.getBillToUDF1(), 
					receipt.getBillToUDF2(), null, null, cardInfoObjStr, receipt.getBillToInfo2(), contactsLoyalty);
			
			List<org.mq.optculture.model.DR.DRItem> DRitems = DRFromV9ORPrism.getBody().getItems();
			List<DRToLoyaltyExtractionItems> items = new ArrayList<DRToLoyaltyExtractionItems>();
			PropertyDescriptor pdi = null;
			String itemNoteUsed = user.getItemNoteUsed();
			String ltyDiscountAmt = Constants.STRING_NILL;
			boolean loyaltyAsDiscount = false;
			
			String nonInventoryItem = user.getNonInventoryItem();
			
			
			for (org.mq.optculture.model.DR.DRItem item : DRitems) {
				
				//Prism Non-INvn item
				if(source !=null && source.equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_PRISM) ){
					
				
					if(nonInventoryItem != null && !nonInventoryItem.isEmpty()){
				
						String[] nonInventoryItemValue = nonInventoryItem.split(""+Constants.DELIMETER_COLON);
						String nonInventoryItemField = nonInventoryItemValue[0];
						String nonInventoryItemFieldValue=nonInventoryItemValue[1];
						PropertyDescriptor nonDiscItemNote  = null;
						nonDiscItemNote = new PropertyDescriptor(nonInventoryItemField, item.getClass());
						Object nonDiscItemObj = nonDiscItemNote.getReadMethod().invoke(item);
						//if(nonDiscItemObj == null || nonDiscItemObj.toString().isEmpty() ) continue;
						if(nonDiscItemObj != null && !nonDiscItemObj.toString().isEmpty() && 
								nonInventoryItemFieldValue.equalsIgnoreCase(nonDiscItemObj.toString())) {
							continue;
							
						}
				
					}
					
					
				}//prism 
				String itemNoteObjStr=Constants.STRING_NILL;
				double itemQty=(Double.valueOf(item.getQty()));
				if(itemNoteUsed!=null) {
					pdi = new PropertyDescriptor(itemNoteUsed, item.getClass());
					Object itemNoteObj = pdi.getReadMethod().invoke(item);
					if(itemNoteObj != null && !itemNoteObj.toString().isEmpty() ) {
						itemNoteObjStr = itemNoteObj.toString();
					}
				}
				
				DRToLoyaltyExtractionItems drItem = new DRToLoyaltyExtractionItems(item.getItemCategory() != null && 
						!item.getItemCategory().isEmpty() ? item.getItemCategory() : item.getDCSName(), 
						(item.getDepartmentCode()!=null && !item.getDepartmentCode().isEmpty()) ? item.getDepartmentCode() :"", 
						(item.getItemClass()!=null && !item.getItemClass().isEmpty()) ? item.getItemClass() : "", 
						(item.getItemSubClass()!=null && !item.getItemSubClass().isEmpty()) ? item.getItemSubClass() : "", 
						(item.getDCS()!=null && !item.getDCS().isEmpty()) ? item.getDCS() :"", 
						(item.getVendorCode()!=null && !item.getVendorCode().isEmpty()) ? item.getVendorCode() : "",
						(item.getUPC()!=null && !item.getUPC().isEmpty()) ? item.getUPC() : "", 
						item.getDocItemPrc()==null ? item.getInvcItemPrc() : item.getDocItemPrc(), 
						item.getQty(), (item.getTax()!=null && !item.getTax().isEmpty()) ? item.getTax() : "", 
						(item.getDCSName() != null && item.getDCSName().equalsIgnoreCase("COUPONS") && 
						item.getDocItemDisc() != null && !item.getDocItemDisc().isEmpty() ? item.getDocItemDisc() : (item.getDocItemDiscAmt()!=null && !item.getDocItemDiscAmt().isEmpty() ? item.getDocItemDiscAmt() :"")), 
						(item.getDepartmentName()!=null && !item.getDepartmentName().isEmpty()) ? item.getDepartmentName() :"", 
						(item.getItemClassName()!=null && !item.getItemClassName().isEmpty()) ? item.getItemClassName() : "", 
						(item.getItemSubClassName()!=null && !item.getItemSubClassName().isEmpty()) ? item.getItemSubClassName() : "", 
						(item.getVendorName()!=null && !item.getVendorName().isEmpty()) ? item.getVendorName() :"", 
						(item.getItemSID()!=null && !item.getItemSID().isEmpty()) ? item.getItemSID() :"",
						(item.getRefStoreCode()!=null && !item.getRefStoreCode().isEmpty()) ? item.getRefStoreCode() :"", 
						(item.getRefSubsidiaryNumber()!=null && !item.getRefSubsidiaryNumber().isEmpty()) ? item.getRefSubsidiaryNumber() :"",
						(item.getRefReceipt()!=null && !item.getRefReceipt().isEmpty()) ? item.getRefReceipt() :"",
						(item.getRefDocSID()!=null && !item.getRefDocSID().isEmpty()) ? item.getRefDocSID() :"",
						(item.getDiscountReason()!=null && !item.getDiscountReason().isEmpty() ? item.getDiscountReason():""));
				
				drItem.setDocItemOrigPrc(item.getDocItemOrigPrc()!=null && !item.getDocItemOrigPrc().isEmpty() ? item.getDocItemOrigPrc() :"");
				drItem.setInvcItemPrc(item.getInvcItemPrc()!=null && !item.getInvcItemPrc().isEmpty() ? item.getInvcItemPrc() :"");
				drItem.setDiscount(item.getDocItemDiscAmt() !=null && !item.getDocItemDiscAmt().isEmpty() ? item.getDocItemDiscAmt() :
					(item.getDocItemDisc()!=null && !item.getDocItemDisc().isEmpty()?item.getDocItemDisc():""));
				drItem.setItemNote(itemNoteObjStr!=null ? itemNoteObjStr : "");
				drItem.setDiscount(drItem.getDiscount().replace("&#045;",Constants.DELIMETER_HIPHEN).replace("\u0026#045;",Constants.DELIMETER_HIPHEN));
				drItem.setItemPromoDiscount(item.getItemPromoDiscount() != null && !item.getItemPromoDiscount().isEmpty() ? item.getItemPromoDiscount() : null);
				//set qty negative if ReceiptType=2 (return receipt)
				if(receipt.getReceiptType()!=null && !receipt.getReceiptType().isEmpty() && receipt.getReceiptType().equalsIgnoreCase("2")&& itemQty>0)
				drItem.setQuantity(String.valueOf((itemQty*=-1)));
				drItem.setTax(item.getDocItemOrigTax()!=null?item.getDocItemOrigTax():"");
				if(item.getItemType() != null && (item.getItemType().equalsIgnoreCase("NonInvnPromo") || (item.getItemType().equalsIgnoreCase("NonInvnRedeemReversal")))){
					drItem.setNonInventory(true);
					
				}
				items.add(drItem);
				
			}
			
			
			DRToLoyaltyExtractionParams headerFileds = new DRToLoyaltyExtractionParams(DRFromV9ORPrism.getHead().getEnrollCustomer(),
					DRFromV9ORPrism.getHead().getIsLoyaltyCustomer(),DRFromV9ORPrism.getHead().getRequestSource(), DRFromV9ORPrism.getHead().getRequestType());
			
			
			String userName=DRFromV9ORPrism.getHead().getUser().getUserName();
			String token =DRFromV9ORPrism.getHead().getUser().getToken();
			String orgId =DRFromV9ORPrism.getHead().getUser().getOrganizationId();
			
			DROptCultureDetails ocDetails = DRFromV9ORPrism.getOptcultureDetails();
			DRToLoyaltyExtractionAuth authUser = new DRToLoyaltyExtractionAuth(userName,orgId, token);
			DRToLtyExtractionData DRToLtyExtractionData = new DRToLtyExtractionData(customer, null, authUser, headerFileds, DRReceipt);
			DRToLtyExtractionData.setOTPCode(ocDetails != null && ocDetails.getLoyaltyRedeem() != null && 
					ocDetails.getLoyaltyRedeem().getOTPCode() != null ? ocDetails.getLoyaltyRedeem().getOTPCode() : Constants.STRING_NILL);

			DRToLtyExtractionData.setMobileBasedEnroll(mobileBasedEnroll);
			DRToLtyExtractionData.setPerkBased(perkBased);
			
			//Prepare promotions
			//ItemNote syntax cc:qty:disc:1.0-2 (ratio)
			Map<String, String> distinctPromos = new HashMap<String, String>();
			if(user.isEnablePromoRedemption() && user.getItemNoteUsed()!=null){
				String usedItemNote = user.getItemNoteUsed();
				String usedItemInfo = user.getItemInfo();
				PropertyDescriptor pdItemNote  = null;
				PropertyDescriptor pdItemInfo  = null;
				List<org.mq.optculture.model.DR.DRItem> promoItems = DRFromV9ORPrism.getBody().getItems();
				//promo applied item level
				for (org.mq.optculture.model.DR.DRItem drItem : promoItems) {
					pdItemNote = new PropertyDescriptor(usedItemNote, drItem.getClass());
					Object itemNoteObj = pdItemNote.getReadMethod().invoke(drItem);
					if(itemNoteObj == null || itemNoteObj.toString().isEmpty() ) continue;
					String itemNoteObjStr = itemNoteObj.toString();
					Object itemInfoObj = null;
					if(usedItemInfo!=null) {
						pdItemInfo = new PropertyDescriptor(usedItemInfo, drItem.getClass());
						itemInfoObj = pdItemInfo.getReadMethod().invoke(drItem);
					}
					String itemInfoObjStr = itemInfoObj!=null?itemInfoObj.toString():"";
					String[] promoInfosArr =null;
					if(itemNoteObjStr.contains(""+Constants.DELIMITER_PIPE)){
						promoInfosArr = itemNoteObjStr.split("\\"+Constants.DELIMITER_PIPE);
						for (String promoInfo : promoInfosArr) {
							String[] promoInfoArr = promoInfo.split(""+Constants.DELIMETER_COLON);
							//String[] qtyInfoArr = promoInfoArr[1].split(""+Constants.DELIMETER_COMMA);
							if(promoInfoArr.length>2) {
								String itemInfo = distinctPromos.get(promoInfoArr[0]);
								if(itemInfo != null && !itemInfo.isEmpty()) itemInfo += Constants.DELIMITER_PIPE;
								
								itemInfo = (itemInfo == null ? 
										(itemInfoObjStr!=null && !itemInfoObjStr.isEmpty() ? itemInfoObjStr+""+Constants.DELIMETER_COLON+promoInfoArr[1]
												+""+Constants.DELIMETER_COLON+""+promoInfoArr[2]+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"") :
											drItem.getItemLookup()+""+Constants.DELIMETER_COLON+promoInfoArr[1]+""+Constants.DELIMETER_COLON+promoInfoArr[2]
													+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"")) : 
										(itemInfoObjStr!=null && !itemInfoObjStr.isEmpty() ? itemInfo+itemInfoObjStr+Constants.DELIMETER_COLON+promoInfoArr[1]
												+""+Constants.DELIMETER_COLON+promoInfoArr[2]+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"") :
										itemInfo+drItem.getItemLookup()+Constants.DELIMETER_COLON+promoInfoArr[1]+""+Constants.DELIMETER_COLON+promoInfoArr[2]
												+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"")));
								distinctPromos.put(promoInfoArr[0], itemInfo);
							}
							}
						}else{
							String[] promoInfoArr = itemNoteObjStr.split(""+Constants.DELIMETER_COLON);
							String itemInfo = distinctPromos.get(promoInfoArr[0]);
							if(itemInfo != null && !itemInfo.isEmpty()) itemInfo += Constants.DELIMITER_PIPE;
							if(promoInfoArr.length>2) {
							itemInfo = (itemInfo == null ? 
									(itemInfoObjStr!=null && !itemInfoObjStr.isEmpty() ? itemInfoObjStr+""+Constants.DELIMETER_COLON+promoInfoArr[1]
											+""+Constants.DELIMETER_COLON+""+promoInfoArr[2]+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"") :
										drItem.getItemLookup()+""+Constants.DELIMETER_COLON+promoInfoArr[1]+""+Constants.DELIMETER_COLON+promoInfoArr[2]
												+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"")) : 
									(itemInfoObjStr!=null && !itemInfoObjStr.isEmpty() ? itemInfo+itemInfoObjStr+Constants.DELIMETER_COLON+promoInfoArr[1]
											+""+Constants.DELIMETER_COLON+promoInfoArr[2]+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"") :
									itemInfo+drItem.getItemLookup()+Constants.DELIMETER_COLON+promoInfoArr[1]+""+Constants.DELIMETER_COLON+promoInfoArr[2]
											+""+(promoInfoArr.length>=4?Constants.DELIMETER_COLON+promoInfoArr[3]:"")));
							distinctPromos.put(promoInfoArr[0], itemInfo);
							}
						}
					}
			}
				//promo applied receipt level
				boolean ltyRedeemExists = false;
				DRToLoyaltyExtractionRedemption redemptionDetails = new DRToLoyaltyExtractionRedemption();
				if(user.getReceiptNoteUsed()!=null){
					String usedReceiptNote = user.getReceiptNoteUsed();
					PropertyDescriptor pdReceiptNote = null;
					pdReceiptNote = new PropertyDescriptor(usedReceiptNote, receipt.getClass());
					Object receiptNoteObj = pdReceiptNote.getReadMethod().invoke(receipt);
					if(receiptNoteObj != null && !receiptNoteObj.toString().isEmpty() ) {
					String receiptNoteObjStr = receiptNoteObj.toString();
					//P:orL:--new syntax,L: then redemption amount
					if(receiptNoteObjStr.startsWith("P:")||receiptNoteObjStr.startsWith("L:")) {
						if(receiptNoteObjStr.contains(""+Constants.DELIMITER_PIPE)){
							String[] promoInfosArr = receiptNoteObjStr.split("\\"+Constants.DELIMITER_PIPE);
							for (String promoInfo : promoInfosArr) {
								if(promoInfo.startsWith("L:")) {
									String[] promoInfoArr = promoInfo.split(""+Constants.DELIMETER_COLON);
									ltyDiscountAmt=promoInfoArr[1];
									loyaltyAsDiscount = true;
									redemptionDetails.setDiscountSpreaded(true);
								}else {
								String[] promoInfoArr = promoInfo.split(""+Constants.DELIMETER_COMMA);
								String[] pArr = promoInfoArr[0].split(""+Constants.DELIMETER_COLON);
								if(!distinctPromos.containsKey(pArr[1]))distinctPromos.put(pArr[1],promoInfo);
								}
							}
						}else{
							if(receiptNoteObjStr.startsWith("L:")) {
								String[] promoInfoArr = receiptNoteObjStr.split(""+Constants.DELIMETER_COLON);
								ltyDiscountAmt=promoInfoArr[1];
								loyaltyAsDiscount = true;
								logger.debug("DiscountSpreaded ==="+true);
								redemptionDetails.setDiscountSpreaded(true);
							}else {
								String[] promoInfoArr = receiptNoteObjStr.split(""+Constants.DELIMETER_COMMA);
								String[] pArr = promoInfoArr[0].split(""+Constants.DELIMETER_COLON);
								if(!distinctPromos.containsKey(pArr[1]))distinctPromos.put(pArr[1],receiptNoteObjStr);
							}
						}
						
					}else {
						if(receiptNoteObjStr.contains(""+Constants.DELIMITER_PIPE)){
							String[] promoInfosArr = receiptNoteObjStr.split("\\"+Constants.DELIMITER_PIPE);
							for (String promoInfo : promoInfosArr) {
								String[] promoInfoArr = promoInfo.split(""+Constants.DELIMETER_COLON);
								distinctPromos.put(promoInfoArr[0],receiptNoteObjStr);
							}
						}else{
								String[] promoInfoArr = receiptNoteObjStr.split(""+Constants.DELIMETER_COLON);
								//distinctPromos.put(promoInfoArr[0],""+Constants.DELIMETER_COLON+promoInfoArr[1]);
								distinctPromos.put(promoInfoArr[0],receiptNoteObjStr);
						}
					}
					}
					}
				logger.info("ltyDiscountAmt "+ltyDiscountAmt);
				if(ltyDiscountAmt!=null && !ltyDiscountAmt.isEmpty()){
					ltyRedeemExists =true;
					loyaltyAsDiscount = true;
				}
				
				if(distinctPromos.size() > 0){
					List<DRToLoyaltyExtractionPromotions> promotionsLst = new ArrayList<DRToLoyaltyExtractionPromotions>();
					for (String promo : distinctPromos.keySet()) {
						
						
						Double discountAmount = 0.0;
						String itemINfo = distinctPromos.get(promo);
						if(itemINfo.contains(""+Constants.DELIMITER_PIPE)){
							String[] itemInfoArr = itemINfo.split("\\"+Constants.DELIMITER_PIPE);
							for (String itemInfo : itemInfoArr) {
									if(itemInfo.startsWith("P:") && itemInfo.contains(""+Constants.DELIMETER_COMMA)) {
										String[] discArr = itemInfo.split(""+Constants.DELIMETER_COMMA);
										discountAmount += (discArr.length>2?(Double.parseDouble(discArr[2]) * Double.parseDouble(discArr[1])):(Double.parseDouble(discArr[1])));
									}else {
										String[] discArr = itemInfo.split(""+Constants.DELIMETER_COLON);
										discountAmount += (discArr.length>2?(Double.parseDouble(discArr[2]) * Double.parseDouble(discArr[1])):(Double.parseDouble(discArr[1])));
									}
									}
						}else{
								if(itemINfo.startsWith("P:") && itemINfo.contains(""+Constants.DELIMETER_COMMA)) {
									String[] discArr = itemINfo.split(""+Constants.DELIMETER_COMMA);
									discountAmount += (discArr.length>2?(Double.parseDouble(discArr[2]) * Double.parseDouble(discArr[1])):(Double.parseDouble(discArr[1])));
								}else if(itemINfo.contains(""+Constants.DELIMETER_COLON)) {
									String[] discArr = itemINfo.split(""+Constants.DELIMETER_COLON);
									discountAmount += (discArr.length>2?(Double.parseDouble(discArr[2]) * Double.parseDouble(discArr[1])):(Double.parseDouble(discArr[1])));
								}
						}
						logger.info("discountAmount "+discountAmount);
						DRToLoyaltyExtractionPromotions promotion = new DRToLoyaltyExtractionPromotions();
						promotion.setCouponCode(promo);
						promotion.setDiscountAmount(Utility.truncateUptoTwoDecimal(discountAmount));
						promotion.setItemCodeInfo(itemINfo);
						
						promotionsLst.add(promotion);						
					
					DRToLtyExtractionData.setPromotions(promotionsLst);
					
				}
			}
				String tenderTaken = Constants.STRING_NILL;
				String tenderGiven = Constants.STRING_NILL;
				if(ltyDiscountAmt.isEmpty()) {
				String userRedeemTender = user.getRedeemTender();
				//String nonInventoryItem = user.getNonInventoryItem();
				if(userRedeemTender!=null && !userRedeemTender.isEmpty()) {
					if(userRedeemTender.contains(""+Constants.DELIMETER_COLON)) {
					String tenderType=user.getRedeemTender().split(""+Constants.DELIMETER_COLON)[1];
					if(user.getRedeemTender().startsWith(REDEEMTENDERCC)) {
						
						List<CreditCard> listOfCards =DRCC;
						
						if(listOfCards != null && listOfCards.size() != 0){
							for(CreditCard creditCard:listOfCards){
								if(creditCard.getType()!=null && 
										!creditCard.getType().isEmpty() && 
										creditCard.getType().equalsIgnoreCase(tenderType) ){
									//tenderAmount=creditCard.getAmount();
									tenderTaken=(creditCard.getTaken()!=null && !creditCard.getTaken().isEmpty() ? creditCard.getTaken() : creditCard.getAmount());
									tenderGiven=(creditCard.getGiven()!=null && !creditCard.getGiven().isEmpty()? creditCard.getGiven() : creditCard.getAmount());
									if((tenderTaken!=null && !tenderTaken.isEmpty())||(tenderGiven!=null && !tenderGiven.isEmpty())) ltyRedeemExists =true;
								}
							}
						}
					}else if(user.getRedeemTender().startsWith(REDEEMTENDERCT)) {
						
						List<CustomTender> listOfCT =DRFromV9ORPrism.getBody().getCustomTender();
						
						if(listOfCT != null && listOfCT.size() != 0){
							for(CustomTender customTender:listOfCT){
								if(customTender.getType()!=null && 
										!customTender.getType().isEmpty() && 
										customTender.getType().equalsIgnoreCase(tenderType) ){
									//tenderAmount=creditCard.getAmount();
									tenderTaken=(customTender.getTaken()!=null && !customTender.getTaken().isEmpty() ? customTender.getTaken() : customTender.getAmount());
									tenderGiven=(customTender.getGiven()!=null && !customTender.getGiven().isEmpty()? customTender.getGiven() : customTender.getAmount());
									if((tenderTaken!=null && !tenderTaken.isEmpty())||(tenderGiven!=null && !tenderGiven.isEmpty())) ltyRedeemExists =true;
								}
							}
						}
						
					}
				}else {
					COD COD = DRCOD;
					if(COD!=null)tenderTaken=(COD.getTaken()!=null && !COD.getTaken().isEmpty()? COD.getTaken() : COD.getAmount());
					if(COD!=null)tenderGiven=(COD.getGiven()!=null && !COD.getGiven().isEmpty() ? COD.getGiven() : COD.getAmount());
					if((tenderTaken!=null && !tenderTaken.isEmpty())||(tenderGiven!=null && !tenderGiven.isEmpty())) ltyRedeemExists =true;
				}
					
				}else if(nonInventoryItem!=null && !nonInventoryItem.isEmpty()) {
					String[] nonInventoryItemValue = nonInventoryItem.split(""+Constants.DELIMETER_COLON);
						String nonInventoryItemField = nonInventoryItemValue[0];
						String nonInventoryItemFieldValue=nonInventoryItemValue[1];
						PropertyDescriptor nonDiscItemNote  = null;
						for (org.mq.optculture.model.DR.DRItem drItem : DRFromV9ORPrism.getBody().getItems()){
						nonDiscItemNote = new PropertyDescriptor(nonInventoryItemField, drItem.getClass());
						Object nonDiscItemObj = nonDiscItemNote.getReadMethod().invoke(drItem);
						if(nonDiscItemObj == null || nonDiscItemObj.toString().isEmpty() ) continue;
						if(nonInventoryItemFieldValue.equalsIgnoreCase(nonDiscItemObj.toString())) {
							for (DRToLoyaltyExtractionItems DRItem : items) {
								
								if(DRItem.getItemSID().equals(drItem.getItemSID()))
									
									DRItem.setNonInventory(true);
							}
							if(receipt.getReceiptType()!=null && !receipt.getReceiptType().isEmpty() && 
									receipt.getReceiptType().equalsIgnoreCase("2")){
								
								tenderGiven= drItem.getDocItemPrc();
								ltyRedeemExists=true;
								
								/*if((Double.valueOf(drItem.getExtPrc()))<0) {
									ltyDiscountAmt = (String.valueOf(Math.abs(Double.valueOf(drItem.getDocItemPrc()))));
									ltyRedeemExists=true;
								}else if((Double.valueOf(drItem.getExtPrc()))>0) {
									tenderGiven= drItem.getDocItemPrc();
									ltyRedeemExists=true;
								}*/
								
								
							}else if(receipt.getReceiptType()!=null && !receipt.getReceiptType().isEmpty() && 
									!receipt.getReceiptType().equalsIgnoreCase("2"))  {
								if((Double.valueOf(drItem.getExtPrc()))<0) {
									ltyDiscountAmt = (String.valueOf(Math.abs(Double.valueOf(drItem.getDocItemPrc()))));
									ltyRedeemExists=true;
								}else if((Double.valueOf(drItem.getExtPrc()))>0) {
									tenderGiven= drItem.getDocItemPrc();
									ltyRedeemExists=true;
								}
								
							}
						}
						}
				}
				}
				DRToLtyExtractionData.setItems(items);
				logger.info("tenderAmounts "+headerFileds.getRequestType()+" "+ltyDiscountAmt+" "+tenderGiven+" "+tenderTaken+" "+loyaltyAsDiscount+" "+ltyDiscountAmt);
				
				if(ltyRedeemExists) {
					redemptionDetails.setRedemptionAmount(!loyaltyAsDiscount && ltyDiscountAmt!=null && 
							!ltyDiscountAmt.isEmpty()?ltyDiscountAmt:tenderTaken);
					if(loyaltyAsDiscount)redemptionDetails.setRedemptionAsDiscount(ltyDiscountAmt!=null && 
							!ltyDiscountAmt.isEmpty()?ltyDiscountAmt:"");
					redemptionDetails.setLoyaltyRedeemReversal(tenderGiven);
					/*if(headerFileds.getRequestType() != null && headerFileds.getRequestType().equalsIgnoreCase("Reversal") && 
							loyaltyAsDiscount && ltyDiscountAmt!=null && 
									!ltyDiscountAmt.isEmpty()){
						logger.info("ltyDiscountAmt =="+ltyDiscountAmt);
						redemptionDetails.setLoyaltyRedeemReversal(ltyDiscountAmt);
					}*/
				}else {
					if(ocDetails != null && ocDetails.getLoyaltyRedeem()!= null &&
							ocDetails.getLoyaltyRedeem().getDiscountAmount() != null && 
							!ocDetails.getLoyaltyRedeem().getDiscountAmount().isEmpty()) {
						redemptionDetails.setRedemptionAmount(ocDetails.getLoyaltyRedeem().getDiscountAmount());
						DRReceipt.setDiscountReasonName(receipt.getDiscountReasonName());
					}
				}
				DRToLtyExtractionData.setRedemptionDetails(redemptionDetails);
			return DRToLtyExtractionData;
		} catch (JsonSyntaxException e) {
			logger.error("Exception ::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
		return null;
	}
	public String extractLoyaltyFromDR(DRToLtyExtractionData DRToLtyExtractionData, Users user,DigitalReceiptsJSON digitalReceiptsJSON){

		String cardNUmber = Constants.STRING_NILL;
			try{
				logger.info("Started Loyalty extraction");
				
				String userName=Constants.STRING_NILL;
				String token =Constants.STRING_NILL;
				String orgId =Constants.STRING_NILL;
				//if(DRToLtyExtractionData.isPerkBased()) return DRToLtyExtractionData.getCustomer().getConLoyalty().getCardNumber();
				DRToLoyaltyExtractionAuth authUser = DRToLtyExtractionData.getUser();
				DRToLoyaltyExtractionReceipt receipt = DRToLtyExtractionData.getReceipt();
				DRToLoyaltyExtractionCustomer customer = DRToLtyExtractionData.getCustomer();
				DRToLoyaltyExtractionParams headerParams = DRToLtyExtractionData.getHeaderFileds();
				List<DRToLoyaltyExtractionItems> items = DRToLtyExtractionData.getItems();
				
				userName=authUser.getUserName();//(dRJsonRequest.getBody().getUserDetails().getUserName()!=null &&	!dRJsonRequest.getBody().getUserDetails().getUserName().isEmpty() ? dRJsonRequest.getBody().getUserDetails().getUserName()		: "");
				token=authUser.getToken();
				orgId=authUser.getOrgID();
				
				ContactsLoyaltyDao contactsLoyaltyDao;
				contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);

				boolean enrollRequest = user.isEnrollFromDR();
				boolean newlyEnrolled = false;
				//boolean mobileBasedEnroll=false; 
				//ContactsLoyalty contactsLoyalty = null;
				boolean mobileBasedEnroll=DRToLtyExtractionData.isMobileBasedEnroll();
				ContactsLoyalty contactsLoyalty = customer.getConLoyalty();
				String enrolledCard =Constants.STRING_NILL;
				//String billToInfo1 =Constants.STRING_NILL;
				LoyaltyEnrollResponse enrolResponseObject =null;
				
				//determine contactsLoyalty
				String isLoyaltyCustomer = headerParams.getIsLoyaltyCustomer();
				String enRollCustomer = headerParams.getEnrollCustomer();
				//String card=customer.getCardNumber();
				//String mobile=customer.getPhone();
				
				
				/*if(card == null || card.trim().isEmpty()) {
					LoyaltyProgram defaultLoyaltyProgram = findDefaultProgram(user.getUserId());
					if(defaultLoyaltyProgram !=null) {
						if(defaultLoyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
							if((mobile != null && !mobile.trim().isEmpty())) {
								logger.info("no card number lookup with mobile based prog");
								contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),mobile);
								if(contactsLoyalty==null)mobileBasedEnroll = true;
							}
						}
					}
				}else {
					logger.info("contains card number ");
					//look up with card number
				    contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),card);
				}*/
				
				
					
					/*if(contactsLoyalty==null) {
						String custID = customer.getCustomerId();//receipt.getBillToCustSID();
						if(custID!=null && !custID.isEmpty()){
							contactsLoyalty = findLoyaltyCardInOCByCustId(custID, user.getUserId());
						}
					}*/
					if(contactsLoyalty!=null) {
						if(contactsLoyalty.getMembershipType().equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)) 
							customer.setCardNumber(contactsLoyalty.getCardNumber());
						else if(contactsLoyalty.getMembershipType().equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE))
							customer.setPhone(contactsLoyalty.getCardNumber());
						if(isLoyaltyCustomer == null || isLoyaltyCustomer.isEmpty()) {
							
							isLoyaltyCustomer ="Y";
							headerParams.setIsLoyaltyCustomer("Y");
						}
						customer.setConLoyalty(contactsLoyalty);
						cardNUmber = contactsLoyalty.getCardNumber();
					}
			//	}
					logger.info("enroll request:"+isLoyaltyCustomer);
				//user level flag is just to take the trx or not - drflag is to enroll or not
				if(user.isEnrollFromDR() || !user.isEnrollFromDR()){
					enrollRequest= enRollCustomer!=null && !enRollCustomer.isEmpty();
					if(enRollCustomer!=null && !enRollCustomer.isEmpty()){
						if( (enRollCustomer.equals("Y")||enRollCustomer.equals("True")) ||
								(( enRollCustomer.equals("N")||enRollCustomer.equals("False") ) && 
										isLoyaltyCustomer!= null && isLoyaltyCustomer.equals("Y")) || 
								(user.isEnrollFromDR() &&  ( enRollCustomer.equals("N")||enRollCustomer.equals("False") ) && (isLoyaltyCustomer == null || isLoyaltyCustomer.isEmpty()))){
							enrollRequest=true;
						}else{
							enrollRequest=false;
						}
					}
					logger.info("enroll request:"+enrollRequest);
					/*
					 * if(DRToLtyExtractionData.isPerkBased()) { cardNUmber =
					 * DRToLtyExtractionData.getCustomer().getConLoyalty().getCardNumber();
					 */	//return DRToLtyExtractionData.getCustomer().getConLoyalty().getCardNumber();
					if(enrollRequest){//y is this coming as true?
					
					if(contactsLoyalty==null){
						//for a perk program it shudnt be executed
						//enrollment
						logger.info("performing enrollment");
						enrolResponseObject = prepareEnrollFromDRRequest(user,mobileBasedEnroll, receipt, authUser, customer,headerParams.getRequestSource());
						String responseJson = new Gson().toJson(enrolResponseObject, LoyaltyEnrollResponse.class);
						if(mobileBasedEnroll) {
							enrolledCard=enrolResponseObject.getMembership().getPhoneNumber();
						}else {
							enrolledCard=enrolResponseObject.getMembership().getCardNumber();
						}
						logger.info("Enrollment response : "+responseJson);
						logger.info("enrolled card : "+enrolledCard);
						newlyEnrolled = true;
						contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),enrolledCard);
						cardNUmber = contactsLoyalty.getCardNumber();
					}/*else {
						if(contactsLoyalty.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) 
					    mobileBasedEnroll=true;
					}*/
				 }
				}
				//separating 
				List<DRToLoyaltyExtractionItems> issuanceItemsList = new ArrayList<DRToLoyaltyExtractionItems>();
				List<DRToLoyaltyExtractionItems> returnItemsList = new ArrayList<DRToLoyaltyExtractionItems>();
				//List<DRToLoyaltyExtractionItems> withRefDocSidReturnItemsList = new ArrayList<DRToLoyaltyExtractionItems>();
				//List<DRToLoyaltyExtractionItems> noRefDocSidReturnItemsList = new ArrayList<DRToLoyaltyExtractionItems>();
				
				
				//List<DRItem> itemsList = dRJsonRequest.getBody().getItems();
				Iterator<DRToLoyaltyExtractionItems> iterItems = items.iterator();
				DRToLoyaltyExtractionItems item= null;
				
					while (iterItems.hasNext()) {
						item = iterItems.next();
						if(item.isNonInventory()) continue;
						if(item.getQuantity() !=null && !item.getQuantity().trim().isEmpty() && (Double.valueOf(item.getQuantity())<0)){
							returnItemsList.add(item);
						}else if(item.getQuantity() !=null && !item.getQuantity().trim().isEmpty() && (Double.valueOf(item.getQuantity())>0)){
							issuanceItemsList.add(item);
						}
					}
					if(!issuanceItemsList.isEmpty() && !returnItemsList.isEmpty()){
						
						//exchange receipt do not consider the exchanged item for bonus.
						Double returnedAmount = 0.0;
						for(DRToLoyaltyExtractionItems skuDetails : returnItemsList){
							
							if(skuDetails.getBilledUnitPrice() != null && !skuDetails.getBilledUnitPrice().trim().isEmpty() &&
									skuDetails.getQuantity() != null && !skuDetails.getQuantity().trim().isEmpty()){
								returnedAmount = returnedAmount + Math.abs((Double.valueOf(skuDetails.getQuantity())) * Double.valueOf(skuDetails.getBilledUnitPrice()));
									
							  }
							}
						DRToLtyExtractionData.setReturnedAmount(returnedAmount);
						
					}
				logger.info("issuanceItemsList size "+issuanceItemsList.size());
				logger.info("returnItemsList size "+returnItemsList.size());
				
				//added APP-3143
				if(headerParams.getRequestType() != null && 
						headerParams.getRequestType().equalsIgnoreCase("Reversal")){
					//String tenderGiven = DRToLtyExtractionData.getRedemptionDetails()!=null?DRToLtyExtractionData.getRedemptionDetails().getLoyaltyRedeemReversal():"";
					String tenderGiven = "";
					if(DRToLtyExtractionData.getRedemptionDetails()!=null) {
						if(DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount() != null && 
								!DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount().isEmpty()){
							tenderGiven = DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount();
						}else if(DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount()!= null && 
								!DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount().isEmpty()) {
							tenderGiven = DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount();
						}
					}
					logger.info("tenderGiven "+tenderGiven);
					if(user.isReturnFromDR() && digitalReceiptsJSON.getRetryForLtyExtraction()==0 
							&&	isLoyaltyCustomer != null && (isLoyaltyCustomer.trim().equals("Y")||isLoyaltyCustomer.equalsIgnoreCase("True")) && contactsLoyalty!=null && !newlyEnrolled){
						if(issuanceItemsList!=null && !issuanceItemsList.isEmpty()){//the items of +ve qty only comes here
							logger.info("performing return");
							
							LoyaltyReturnTransactionResponse returnResponse=
									prepareReturnFromDRRequest( DRToLtyExtractionData, user, issuanceItemsList,mobileBasedEnroll,tenderGiven);
							String returnResponseJson = new Gson().toJson(returnResponse, LoyaltyReturnTransactionResponse.class);
							logger.info("Return response : "+returnResponseJson);
						}
					}
					return cardNUmber;
				}
				// only issuance in case of new enrollment.
				if(user.isIssuanceFromDR() && newlyEnrolled){
					logger.info("perfroming issuance for a newly enrolled card");
				if(enrolResponseObject.getStatus().getErrorCode().equalsIgnoreCase("0") && contactsLoyalty != null){
					if(issuanceItemsList!=null && !issuanceItemsList.isEmpty()){
						LoyaltyIssuanceResponse issuanceResponseObject = 
						prepareIssuanceFromDRRequest(DRToLtyExtractionData, user,issuanceItemsList,contactsLoyalty, digitalReceiptsJSON, false);
					String responseJson = new Gson().toJson(issuanceResponseObject, LoyaltyIssuanceResponse.class);
					logger.info("Issuance response : "+responseJson);
					}
				}else{
					logger.info("Did not take up issuance as enrolmnet did not return error code 0 ");
				}
				return cardNUmber;
				}
				// existing membership, first redemption then issuance.
				
				//receipt type is null for sale in magento
				String tenderTaken = "";
				if(DRToLtyExtractionData.getRedemptionDetails()!=null && 
						(
								(DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount()!=null && !DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount().isEmpty()) 
								|| (DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount() != null && 
						!DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount().isEmpty())
								)) {
					if(DRToLtyExtractionData.getReceipt().getReceiptType()!=null) {
						if(!(DRToLtyExtractionData.getReceipt().getReceiptType().equalsIgnoreCase("Return")|| DRToLtyExtractionData.getReceipt().getReceiptType().equalsIgnoreCase("2"))) {
							if(DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount() != null && !DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount().isEmpty() )
								tenderTaken = DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount();
								else if(DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount() != null && !DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount().isEmpty())
									tenderTaken = DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount();	
					}
					}else {
						if(DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount() != null && !DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount().isEmpty() )
							tenderTaken = DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount();
							else if(DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount() != null && !DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount().isEmpty())
								tenderTaken = DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount();	
						
					}
				}
				logger.info("TenderExists :"+tenderTaken);
				boolean ignoreissuanceOnRedemption = user.isIgnoreissuanceOnRedemption();
				boolean isRedeemed = false;
				if( user.isRedemptionFromDR() && (tenderTaken!=null && !tenderTaken.isEmpty()) && (issuanceItemsList!=null && !issuanceItemsList.isEmpty()) && 
						Double.parseDouble(tenderTaken) >0 &&
						digitalReceiptsJSON.getRetryForLtyExtraction()==0 
							&&	isLoyaltyCustomer != null && (isLoyaltyCustomer.trim().equals("Y")||isLoyaltyCustomer.equalsIgnoreCase("True")) && contactsLoyalty!=null && !newlyEnrolled){
					logger.info("performing redemption");
					LoyaltyRedemptionResponse loyaltyRedemptionResponse =
							prepareRedemptionFromDRRequest(DRToLtyExtractionData, user,tenderTaken,mobileBasedEnroll,OCConstants.LOYALTY_TYPE_CURRENCY,false);
					String responseJson = new Gson().toJson(loyaltyRedemptionResponse, LoyaltyRedemptionResponse.class);
					logger.info("Redemption response : "+responseJson);
					isRedeemed = true;
				}
				ignoreissuanceOnRedemption = (ignoreissuanceOnRedemption && isRedeemed) || (DRToLtyExtractionData.isPerkBased());
				
				if(user.isIssuanceFromDR() && contactsLoyalty!=null &&	 
						isLoyaltyCustomer != null && (isLoyaltyCustomer.trim().equals("Y")||isLoyaltyCustomer.equalsIgnoreCase("True"))){
					logger.info("Entered issuance");
					if(issuanceItemsList!=null && !issuanceItemsList.isEmpty()){
						logger.info("perfroming issuance");
					LoyaltyIssuanceResponse issuanceResponseObject = 
							prepareIssuanceFromDRRequest(DRToLtyExtractionData,user,
									issuanceItemsList,contactsLoyalty, digitalReceiptsJSON, ignoreissuanceOnRedemption);
					String responseJson = new Gson().toJson(issuanceResponseObject, LoyaltyIssuanceResponse.class);
					logger.info("Issuance response : "+responseJson);
					}
				}
				String tenderGiven = DRToLtyExtractionData.getRedemptionDetails()!=null?DRToLtyExtractionData.getRedemptionDetails().getLoyaltyRedeemReversal():"";
				if(user.isReturnFromDR() && digitalReceiptsJSON.getRetryForLtyExtraction()==0 
						&&	isLoyaltyCustomer != null && (isLoyaltyCustomer.trim().equals("Y")||isLoyaltyCustomer.equalsIgnoreCase("True")) && contactsLoyalty!=null && !newlyEnrolled){
					if(returnItemsList!=null && !returnItemsList.isEmpty()){
						logger.info("performing return");
						
						LoyaltyReturnTransactionResponse returnResponse=
								prepareReturnFromDRRequest( DRToLtyExtractionData, user, returnItemsList,mobileBasedEnroll,tenderGiven);
						String returnResponseJson = new Gson().toJson(returnResponse, LoyaltyReturnTransactionResponse.class);
						logger.info("Return response : "+returnResponseJson);
						
					/*Map<String,String> uniqueRefDocsidMap = new HashMap<String,String>();
					for (DRToLoyaltyExtractionItems drItem : returnItemsList) {
						
						if(drItem.getRefDocSID() !=null && drItem.getRefDocSID().isEmpty()){
							
							noRefDocSidReturnItemsList.add(drItem);
								
						}else if(drItem.getRefDocSID()!=null && !drItem.getRefDocSID().isEmpty()){
						 		
				 			if(uniqueRefDocsidMap.get(drItem.getRefDocSID())==null){
				 				uniqueRefDocsidMap.put(drItem.getRefDocSID(),drItem.getItemSID());
				 				withRefDocSidReturnItemsList.add(drItem);
				 			}else {
				 				if(!uniqueRefDocsidMap.get(drItem.getRefDocSID()).equalsIgnoreCase(drItem.getItemSID())){
				 					uniqueRefDocsidMap.put(drItem.getRefDocSID(),drItem.getItemSID());
					 				withRefDocSidReturnItemsList.add(drItem);
				 				}
				 			}
					 	}
					}
					if(noRefDocSidReturnItemsList.size()>0){
						logger.info("return for items without ref docsid");
						LoyaltyReturnTransactionResponse returnResponse=
								prepareReturnFromDRRequest( DRToLtyExtractionData , user, noRefDocSidReturnItemsList,mobileBasedEnroll,tenderAmount);
						String returnResponseJson = new Gson().toJson(returnResponse, LoyaltyReturnTransactionResponse.class);
						logger.info("Return response for items with no ref doc sid : "+returnResponseJson);
					}
					if(withRefDocSidReturnItemsList.size()>0){
						logger.info("return for items with ref docsid");
						LoyaltyReturnTransactionResponse returnResponse=
								prepareReturnFromDRRequest( DRToLtyExtractionData, user, withRefDocSidReturnItemsList,mobileBasedEnroll,tenderAmount);
						String returnResponseJson = new Gson().toJson(returnResponse, LoyaltyReturnTransactionResponse.class);
						logger.info("Return response for items with ref doc sid : "+returnResponseJson);
					}*/
				}
				}
				logger.info("completed dr to lty extraction");
			} catch(Exception e) {
				
				logger.error("Exception ::::" , e);
			}
			return cardNUmber;
	}
	
	public LoyaltyEnrollResponse prepareEnrollFromDRRequest(Users user,boolean mobileBasedEnroll, 
			DRToLoyaltyExtractionReceipt receipt,
			DRToLoyaltyExtractionAuth authUser, DRToLoyaltyExtractionCustomer customerData,String requestSource){
		
		logger.info("started enrol from DR");
		
		LoyaltyEnrollRequest loyaltyEnrollRequest= new LoyaltyEnrollRequest();
		RequestHeader requestHeader =new RequestHeader();
		MembershipRequest membershipRequest = new MembershipRequest();
		Customer customer = new Customer();
		LoyaltyUser loyaltyUser = new LoyaltyUser();
		
		String requestId = OCConstants.DR_LTY_EXTRACTION_REQUEST_ID+authUser.getToken()+"_"+System.currentTimeMillis();
		requestHeader.setRequestId(requestId);
		
		//matching doc date with his pos mapping date format.
		Calendar syscal = new MyCalendar(Calendar.getInstance(), null,
				MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
		requestHeader.setRequestDate(syscal.toString());
		logger.info("syscal.toString() : "+syscal.toString());
		if(receipt.getDocDate()!=null && !receipt.getDocDate().isEmpty()) {
		try {
			String fieldValue = (Constants.STRING_NILL+receipt.getDocDate()+" "+receipt.getDocTime()).replace("T", Constants.STRING_NILL);
			DateFormat formatter;
			Date date;
			formatter = new SimpleDateFormat(MyCalendar.FORMAT_mm_DATETIME_STYEAR);
			date = (Date) formatter.parse(fieldValue);
			Calendar cal = new MyCalendar(Calendar.getInstance(), null,
					MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
			cal.setTime(date);
			requestHeader.setRequestDate(cal.toString());
			logger.info("cal.toString() : "+cal.toString());
			
			} catch (Exception e) {
				logger.info("date format not matched with data, setting system date",e);
				requestHeader.setRequestDate(syscal.toString());
			}
		}
		requestHeader.setStoreNumber(receipt.getStore());
		requestHeader.setDocSID(receipt.getDocSID());
		requestHeader.setSubsidiaryNumber(receipt.getSubsidiaryNumber());
		

		requestHeader.setReceiptNumber(receipt.getInvcNum());
		if(requestSource!=null && (requestSource.equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_Magento)||requestSource.equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_WooCommerce))) 
			requestHeader.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_E_COMM);
		//requestHeader.setRequestDate(receipt.getDocDate());
		//requestHeader.setPcFlag(pcFlag);
		//requestHeader.setSubsidiaryNumber(subsidiaryNumber);
		//requestHeader.setEmployeeId(employeeId);
		//requestHeader.setTerminalId(terminalId);
		LoyaltyEnrollResponse responseObject = null;
		
		if(mobileBasedEnroll ){
			if((customerData.getPhone() == null || customerData.getPhone().isEmpty())){
				try {
					Status status = new Status("111509", PropertyUtil.getErrorMessage(111509, OCConstants.ERROR_LOYALTY_FLAG)
							+ " " + "Phone" + ".", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					responseObject = prepareEnrollmentResponse(null, null, null, status);
					return null;
				} catch (BaseServiceException e) {
					// TODO Auto-generated catch block
				}
				
			}
			membershipRequest.setPhoneNumber(customerData.getPhone());
			membershipRequest.setCardNumber("");
		}else{
			membershipRequest.setCardNumber(customerData.getCardNumber());
			if(membershipRequest.getCardNumber() == null || membershipRequest.getCardNumber().trim().length() == 0) {
					
				membershipRequest.setIssueCardFlag("Y");
			}
			membershipRequest.setPhoneNumber("");
		}
		membershipRequest.setCardPin(customerData.getCardPin());
		//membershipRequest.setFingerprintValidation(fingerprintValidation);
		//membershipRequest.setPhoneNumber();
		//membershipRequest.setCreatedDate(createdDate);
		
		customer.setCustomerId(customerData.getCustomerId().trim());
		customer.setFirstName(customerData.getFirstName());
		customer.setLastName(customerData.getLastName());
		customer.setPhone(customerData.getPhone());
		customer.setEmailAddress(customerData.getEmailAddress());
		customer.setBirthday(customerData.getBirthday());
		customer.setAddressLine1(customerData.getAddressLine1()); //addressLine2 ? 
		customer.setCity(customerData.getCity()); 
		customer.setState(customerData.getState());
		customer.setPostal(customerData.getPostal());
		customer.setAnniversary(customerData.getAnniversary());
		//customer.setCountry(country);
		//customer.setGender(gender);
		//customer.setCreatedDate(receipt.billtocreated);
		
		loyaltyUser.setUserName(authUser.getUserName());
		loyaltyUser.setOrganizationId(authUser.getOrgID());
		loyaltyUser.setToken(authUser.getToken());
		
		loyaltyEnrollRequest.setHeader(requestHeader);
		loyaltyEnrollRequest.setMembership(membershipRequest);
		loyaltyEnrollRequest.setCustomer(customer);
		loyaltyEnrollRequest.setUser(loyaltyUser);
		
		//find duplicate request
		ContactsLoyaltyStage loyaltyStage = null;
			
			LoyaltyTransactionParent tranParent = createNewTransaction(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			Date date = tranParent.getCreatedDate().getTime();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
			String transDate = df.format(date);
			
			LoyaltyEnrollResponse enrollResponse = null;
			String userName = null;
			Status status = null;
			String responseJson = Constants.STRING_NILL;
			String reqJson="";
			Gson gson = new Gson();
			try{
				
				ResponseHeader responseHeader = new ResponseHeader();
				responseHeader.setRequestDate(Constants.STRING_NILL);
				responseHeader.setRequestId(Constants.STRING_NILL);
				responseHeader.setTransactionDate(transDate);
				responseHeader.setTransactionId(Constants.STRING_NILL+tranParent.getTransactionId());
				/*responseHeader.setSubsidiaryNumber(Constants.STRING_NILL);
				responseHeader.setReceiptNumber(Constants.STRING_NILL);
				responseHeader.setReceiptAmount(Constants.STRING_NILL);*/
				
				try{
					reqJson = gson.toJson(loyaltyEnrollRequest, LoyaltyEnrollRequest.class);
					logger.info("reqJson : "+reqJson);
				}catch(Exception e){
					status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					responseJson = gson.toJson(enrollResponse);
					updateEnrollmentTransaction(tranParent, enrollResponse, null);
					logger.info("Response = "+responseJson);
					logger.error("Exception in parsing request json ...",e);
					return null;
				}
				
				
				if(loyaltyEnrollRequest.getHeader() == null){
					status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					responseJson = gson.toJson(enrollResponse);
					updateEnrollmentTransaction(tranParent, enrollResponse, null);
					logger.info("Response = "+responseJson);
					return null;
				}
				
				if(loyaltyEnrollRequest.getHeader().getRequestId() == null || loyaltyEnrollRequest.getHeader().getRequestId().trim().isEmpty() ||
						loyaltyEnrollRequest.getHeader().getRequestDate() == null || loyaltyEnrollRequest.getHeader().getRequestDate().trim().isEmpty()){
					status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					responseJson = gson.toJson(enrollResponse);
					updateEnrollmentTransaction(tranParent, enrollResponse, null);
					logger.info("Response = "+responseJson);
					return null;
				}

				if(loyaltyEnrollRequest.getUser() == null){
					status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					responseJson = gson.toJson(enrollResponse);
					updateEnrollmentTransaction(tranParent, enrollResponse, null);
					logger.info("Response = "+responseJson);
					return null;
				}
				
				if(loyaltyEnrollRequest.getUser().getUserName() == null || loyaltyEnrollRequest.getUser().getOrganizationId().trim().length() <=0 || 
						loyaltyEnrollRequest.getUser().getOrganizationId() == null || loyaltyEnrollRequest.getUser().getOrganizationId().trim().length() <=0 || 
								loyaltyEnrollRequest.getUser().getToken() == null || loyaltyEnrollRequest.getUser().getToken().trim().length() <=0) {
					status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
					responseJson = gson.toJson(enrollResponse);
					updateEnrollmentTransaction(tranParent, enrollResponse, null);
					logger.info("Response = "+responseJson);
					return null;
				}
				
				userName = loyaltyEnrollRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
						loyaltyEnrollRequest.getUser().getOrganizationId();
				
				
				loyaltyStage = findDuplicateRequest(loyaltyEnrollRequest);
				if(loyaltyStage != null){
					logger.info("Duplicate request......");
					responseJson = "{\"STATUS\":{\"ERRORCODE\":\"101505\",\"MESSAGE\":\"Error 101505: Request is being processed.\",\"STATUS\":\"Failure\"}}";
					enrollResponse = gson.fromJson(responseJson, LoyaltyEnrollResponse.class);
					updateEnrollmentTransaction(tranParent, enrollResponse, userName);
					logger.info("Response = "+responseJson);
					return null;
				}
				else{
					loyaltyStage = saveRequestInStageTable(loyaltyEnrollRequest);
				}
		
		LoyaltyTransaction trans = findRequestBycustSidAndReqId(loyaltyEnrollRequest.getUser().getUserName() + "__" +
				loyaltyEnrollRequest.getUser().getOrganizationId(),loyaltyEnrollRequest.getHeader().getRequestId().trim(),
				loyaltyEnrollRequest.getCustomer().getCustomerId().trim());
		if(trans != null){
			logger.info("duplicate transaction found...");
			responseHeader.setRequestId(loyaltyEnrollRequest.getHeader().getRequestId());
			responseHeader.setRequestDate(loyaltyEnrollRequest.getHeader().getRequestDate());
			status = new Status("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
			logger.info("Response = "+responseJson);
			updateEnrollmentTransaction(tranParent, enrollResponse, null);
			return null;
		}
	
	//log transaction
	if(trans == null){
		//trans = logEnrollmentTransactionRequest(loyaltyEnrollRequest, reqJson, OCConstants.LOYALTY_ONLINE_MODE);
		trans = logEnrollmentTransactionRequest(loyaltyEnrollRequest, reqJson, OCConstants.LOYALTY_OFFLINE_MODE);
	}
		
		LoyaltyEnrollmentOCService enrollService = (LoyaltyEnrollmentOCService)ServiceLocator.getInstance().getServiceById(OCConstants.LOYALTY_ENROLMENT_OC_BUSINESS_SERVICE);
		responseObject = enrollService.processEnrollmentRequest(loyaltyEnrollRequest, 
				OCConstants.LOYALTY_OFFLINE_MODE, ""+tranParent.getTransactionId(), transDate);
		responseJson = new Gson().toJson(responseObject, LoyaltyEnrollResponse.class);	
		logger.info("Response = "+responseJson);
		updateTransactionStatus(trans, responseJson, responseObject);
		
		}catch(Exception e){
			logger.error("Error in DR to lty impl", e);
			responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}}";
			logger.info("Response = "+responseJson);
			/*if(loyaltyStage != null)
				deleteRequestFromStageTable(loyaltyStage);*/
		}finally{
			if(loyaltyStage != null) deleteRequestFromStageTable(loyaltyStage);
			//send alert mail for failures
			if(!responseObject.getStatus().getErrorCode().equalsIgnoreCase("0")) {
				Utility.sendDRToLtyFailureMail(receipt.getDocSID(),receipt.getDocDate(), receipt.getDocTime(), user,OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT,responseObject.getStatus().getMessage());
			}
			logger.info("Completed prepareEnrolFromDRRequest");
		}
			logger.info("enrol from DR ended");
		return responseObject;
	}
	
	public LoyaltyIssuanceResponse prepareIssuanceFromDRRequest(DRToLtyExtractionData DRToLtyExtractionData, Users user,
			List<DRToLoyaltyExtractionItems> issuanceItemsList,ContactsLoyalty contactsLoyalty, 
			DigitalReceiptsJSON digitalReceiptsJSON, boolean ignoreissuanceOnRedemption) throws Exception{
		
		logger.info("started issuance from DR");
		//ContactsLoyalty contactsLoyalty = null;
		DigitalReceiptsJSONDao digitalReceiptsJSONDao;
		DigitalReceiptsJSONDaoForDML digitalReceiptsJSONDaoForDML;
		POSMappingDao posMappingDao;
		ServiceLocator context = ServiceLocator.getInstance();
		digitalReceiptsJSONDao = (DigitalReceiptsJSONDao)context.getDAOByName(OCConstants.DIGITAL_RECEIPTS_JSON_DAO);
		digitalReceiptsJSONDaoForDML = (DigitalReceiptsJSONDaoForDML)context.getDAOForDMLByName(OCConstants.DIGITAL_RECEIPTS_JSON_DAOForDML);
		posMappingDao = (POSMappingDao)context.getDAOByName(OCConstants.POSMAPPING_DAO);
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		boolean excludeDiscountedItem = user.isExcludeDiscountedItem();
		Double ignoreDiscountPerc = user.getExcludeDiscPerc();
		boolean excludegiftRedemption = user.isExcludeGiftRedemption();
		boolean allowIssuanceOnOfferItem = user.isAllowIssuanceOnOfferItem();
		try{
		/*//check if enrollment already happened or not
		if(drJson.getBody().getReceipt().getBillToInfo1()!=null && !drJson.getBody().getReceipt().getBillToInfo1().isEmpty()) {
			contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),drJson.getBody().getReceipt().getBillToInfo1());
		}else if(mobileBasedEnroll && drJson.getBody().getReceipt().getBillToPhone1()!=null && 
				!drJson.getBody().getReceipt().getBillToPhone1().trim().isEmpty()) {
			contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),drJson.getBody().getReceipt().getBillToPhone1());
		}
		if(contactsLoyalty==null && drJson.getBody().getReceipt().getBillToCustSID()!=null &&
				!drJson.getBody().getReceipt().getBillToCustSID().isEmpty()) {
			String custID = drJson.getBody().getReceipt().getBillToCustSID();
			contactsLoyalty = findLoyaltyCardInOCByCustId(custID, user.getUserId());
		}	*/
		if(contactsLoyalty==null){
			logger.info("contactsLoyalty "+contactsLoyalty);
			if(digitalReceiptsJSON.getRetryForLtyExtraction() <= 5){
				logger.info("dr retry "+digitalReceiptsJSON.getRetryForLtyExtraction());
				int retryForExtraction= digitalReceiptsJSON.getRetryForLtyExtraction();
				retryForExtraction = retryForExtraction+1;
				digitalReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_NEW);
				digitalReceiptsJSON.setRetryForLtyExtraction(retryForExtraction);
			}else{
				digitalReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_UNPROCESSED);
			}
			
			logger.info("dr retry increment "+digitalReceiptsJSON.getRetryForLtyExtraction());
			digitalReceiptsJSONDaoForDML.saveOrUpdate(digitalReceiptsJSON);
			return null;
		}else if(digitalReceiptsJSON.getStatus().equalsIgnoreCase(Constants.DR_JSON_PROCESS_STATUS_NEW) &&
				digitalReceiptsJSON.getRetryForLtyExtraction() >0){
			logger.info("setting status to processed");
			digitalReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_PROCESSED);
			digitalReceiptsJSONDaoForDML.saveOrUpdate(digitalReceiptsJSON);
		}
		}catch(Exception e){
			logger.error("Exception ",e);
		}
		
		
		DRToLoyaltyExtractionAuth authUser = DRToLtyExtractionData.getUser();
		DRToLoyaltyExtractionReceipt receipt = DRToLtyExtractionData.getReceipt();
		DRToLoyaltyExtractionCustomer customerData = DRToLtyExtractionData.getCustomer();
		DRToLoyaltyExtractionParams headerParams = DRToLtyExtractionData.getHeaderFileds();
		List<DRToLoyaltyExtractionItems> items = DRToLtyExtractionData.getItems();
		
		//DRHead head = new DRHead();
		//DRExtrahead = (drJson.getHead()!=null ? drJson.getHead() : null);
		//DigitalReceiptBody body =new DigitalReceiptBody();
		//body = drJson.getBody();
		//D receipt = new DRReceipt();
		//receipt = drJson.getBody().getReceipt();
		
		LoyaltyIssuanceRequest issuanceRequest = new LoyaltyIssuanceRequest();
		LoyaltyIssuanceResponse issuanceResponse = new LoyaltyIssuanceResponse();
		
		RequestHeader header= new RequestHeader();
		MembershipRequest membershipRequest =new MembershipRequest();
		Amount amount = new Amount();
		LoyaltyUser loyaltyUser =new LoyaltyUser();
		List<SkuDetails> issuanceRequestItems = new ArrayList<SkuDetails>();
		Discounts discounts=new Discounts();
		List<Promotion> promotionList = new ArrayList<Promotion>();
		//Promotion promotion = new Promotion();
		Customer customer = new Customer();
		String requestId = OCConstants.DR_LTY_EXTRACTION_REQUEST_ID+"_"+authUser.getToken()+"_"+System.currentTimeMillis();
		
		header.setRequestId(requestId);
		
		try{
		/* matching doc date with his pos mapping date format. */
		
			Calendar syscal = new MyCalendar(Calendar.getInstance(), null,
					MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
			header.setRequestDate(syscal.toString());
			logger.info("syscal.toString() : "+syscal.toString());
			if(receipt.getDocDate()!=null && !receipt.getDocDate().isEmpty()) {
			try {
				String fieldValue = (Constants.STRING_NILL+receipt.getDocDate()+" "+receipt.getDocTime()).replace("T", Constants.STRING_NILL);
				DateFormat formatter;
				Date date;
				formatter = new SimpleDateFormat(MyCalendar.FORMAT_mm_DATETIME_STYEAR);
				date = (Date) formatter.parse(fieldValue.trim());
				
				Calendar cal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
				cal.setTime(date);
				//For PM case APP-3417
				if(fieldValue.contains("PM")) {
					cal.add(Calendar.HOUR, 12);
				}
				header.setRequestDate(cal.toString());
				logger.info("cal.toString() : "+cal.toString());
				} catch (Exception e) {
					logger.info("date format not matched with data, setting system date",e);
					header.setRequestDate(syscal.toString());
				}
			}
			//if(header.getRequestDate()==null)header.setRequestDate(syscal.toString());
		  //pcFlag
		  //subsidiaryNumber
		  //employeeId
		  //terminalId
		  header.setStoreNumber(receipt.getStore());
	      header.setDocSID(receipt.getDocSID());
	      header.setReceiptNumber(receipt.getInvcNum());
	      header.setSubsidiaryNumber(receipt.getSubsidiaryNumber());

		  if(contactsLoyalty.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
	    	  membershipRequest.setPhoneNumber(customerData.getPhone());
	    	  membershipRequest.setCardNumber("");
	      }else {
	    	membershipRequest.setCardNumber(contactsLoyalty.getCardNumber());
			membershipRequest.setCardPin(contactsLoyalty.getCardPin());
			membershipRequest.setPhoneNumber("");
	      }
		  	double enteredValue=0.0;
		  	double actualPurchaseValue = 0.0;
			amount.setType(OCConstants.LOYALTY_TYPE_PURCHASE);
			amount.setReturnedAmount(DRToLtyExtractionData.getReturnedAmount() == null ? "0.0" :DRToLtyExtractionData.getReturnedAmount()+"");
			String tenderAmount=(DRToLtyExtractionData.getRedemptionDetails()!=null && 
					DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount()!=null && !DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount().isEmpty())?
					DRToLtyExtractionData.getRedemptionDetails().getRedemptionAmount():"";
			logger.info("tenderAmount "+tenderAmount);
			
			/*String tenderAmount="";
			
			String userRedeemTender = user.getRedeemTender();
			if(userRedeemTender!=null && !userRedeemTender.isEmpty()) {
				if(userRedeemTender.contains(""+Constants.DELIMETER_COLON)) {
					String tenderType=user.getRedeemTender().split(""+Constants.DELIMETER_COLON)[1];
				List<CreditCard> listOfCards = receipt.getListOfCreditCards();
				
				if(listOfCards != null && listOfCards.size() != 0){
					for(CreditCard creditCard:listOfCards){
						if(creditCard.getType()!=null && 
								!creditCard.getType().isEmpty() && 
								creditCard.getType().equalsIgnoreCase(tenderType) ){
							tenderAmount=(creditCard.getTaken()!=null ? creditCard.getTaken() : creditCard.getAmount());
						}
					}
				}
			}else {
				COD COD = receipt.getCOD();
				if(COD!=null)tenderAmount=(COD.getTaken()!=null ? COD.getTaken() : COD.getAmount());
						//(drJson.getBody().getCOD().getTaken()!=null ? drJson.getBody().getCOD().getTaken() : drJson.getBody().getCOD().getAmount());
			}
				
			}*/
			
			//deduct receipt level promo discount
			double receiptPromoDisc = 0.0;
			
			if(DRToLtyExtractionData.getPromotions()!=null) {
				List<DRToLoyaltyExtractionPromotions> promotionsList = DRToLtyExtractionData.getPromotions();
				Iterator<DRToLoyaltyExtractionPromotions> iterPromosList = promotionsList.iterator();
				while (iterPromosList.hasNext()) {
					DRToLoyaltyExtractionPromotions ocpromotion=iterPromosList.next();
					if(ocpromotion!=null && ocpromotion.getCouponCode()!=null && !ocpromotion.getCouponCode().isEmpty()) {//APP-4874
						
						Promotion promotion = new Promotion();
						promotion.setName(ocpromotion.getCouponCode());
						promotionList.add(promotion);
					}
					
					if(ocpromotion!=null && ocpromotion.getDiscountType()!=null && ocpromotion.getDiscountType().equalsIgnoreCase(OCConstants.DR_PROMO_DISCOUNT_TYPE_RECEIPT)) {
						receiptPromoDisc+=Double.parseDouble(ocpromotion.getDiscountAmount());
					}
				}
			}
			if(receiptPromoDisc==0){
			if(user.getReceiptNoteUsed()!=null) {
			String usedReceiptNote = user.getReceiptNoteUsed();
			PropertyDescriptor pdReceiptNote = null;
			pdReceiptNote = new PropertyDescriptor(usedReceiptNote,receipt.getClass());
			Object receiptNoteObj = pdReceiptNote.getReadMethod().invoke(receipt);
			if(receiptNoteObj != null && !receiptNoteObj.toString().isEmpty() ) {
			String receiptNoteObjStr = receiptNoteObj.toString();
			if(receiptNoteObjStr.startsWith("P:")) {
				if(receiptNoteObjStr.contains(""+Constants.DELIMITER_PIPE)){
					String[] receiptDiscArr = receiptNoteObjStr.split("\\"+Constants.DELIMITER_PIPE);
					for (String info : receiptDiscArr) {
						String[] infoArr = info.split(""+Constants.DELIMETER_COMMA);
						//String[] pArr = infoArr[0].split(""+Constants.DELIMETER_COLON);
						if(infoArr.length>1) receiptPromoDisc += Double.parseDouble(infoArr[1]);
					}
				}else{
						String[] infoArr = receiptNoteObjStr.split(""+Constants.DELIMETER_COMMA);
						//String[] pArr = infoArr[0].split(""+Constants.DELIMETER_COLON);
						if(infoArr.length>1) receiptPromoDisc += Double.parseDouble(infoArr[1]);
				}
			}else if(!receiptNoteObjStr.startsWith("L:")){
				if(receiptNoteObjStr.contains(""+Constants.DELIMITER_PIPE)){
				String[] receiptDiscArr = receiptNoteObjStr.split("\\"+Constants.DELIMITER_PIPE);
				for (String info : receiptDiscArr) {
					String[] infoArr = info.split(""+Constants.DELIMETER_COLON);
					if(infoArr.length>1) receiptPromoDisc += Double.parseDouble(infoArr[1]);
				}
			}else{
					String[] infoArr = receiptNoteObjStr.split(""+Constants.DELIMETER_COLON);
					if(infoArr.length>1) receiptPromoDisc += Double.parseDouble(infoArr[1]);
			}
			}
			}
			}
			}
			
			//Double subTotal = receipt.getSubtotal() !=null && !receipt.getSubtotal().isEmpty() ? Double.parseDouble(receipt.getSubtotal()) :0.0; 
			amount.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
			//amount.setReceiptAmount(receipt.getTotal()); -- setting calculated enteredValue before deducting redemtpion tender.
			
			if(promotionList!=null && promotionList.size()>0) {//APP-4874
				discounts.setAppliedPromotion("Y");
			}else {
				Promotion promotion = new Promotion();
				promotion.setName("");
				promotionList.add(promotion);
				discounts.setAppliedPromotion("NA");
			}
			discounts.setPromotions(promotionList);
			
			customer.setCustomerId(customerData.getCustomerId().trim());
			customer.setFirstName(customerData.getFirstName());
			customer.setLastName(customerData.getLastName());
			customer.setPhone(customerData.getPhone());
			customer.setEmailAddress(customerData.getEmailAddress());
			customer.setBirthday(customerData.getBirthday());
			customer.setAddressLine1(customerData.getAddressLine1()); //addressLine2 ? 
			customer.setCity(customerData.getCity()); 
			customer.setState(customerData.getState());
			customer.setPostal(customerData.getPostal());
			customer.setAnniversary(customerData.getAnniversary());
			//customer.setCountry(country);
			//customer.setGender(gender);
			//customer.setCreatedDate();
			
			loyaltyUser.setUserName(authUser.getUserName());
			loyaltyUser.setOrganizationId(authUser.getOrgID());
			loyaltyUser.setToken(authUser.getToken());
			
			/* separating items based on quantity */
			
			Iterator<DRToLoyaltyExtractionItems> iterItems = issuanceItemsList.iterator();
			DRToLoyaltyExtractionItems item= null;
			
			double totalItemDisc=0;
		//	double promoDiscount = 0;
			//Calculating itemPrc instead of subtotal, due to exchange receipt case.
				while (iterItems.hasNext()) {
					item = iterItems.next();
					logger.info("nonInventory "+item.isNonInventory());
					
					
					//if(item.getQty()!=null && !item.getQty().trim().isEmpty() && (Double.valueOf(item.getQty())>0)){
						SkuDetails issuanceItems = new SkuDetails();
						issuanceItems.setItemCategory(item.getItemCategory() == null || item.getItemCategory().isEmpty() ? item.getItemCategory() : item.getItemCategory());
						issuanceItems.setDepartmentCode((item.getDepartmentCode()!=null && !item.getDepartmentCode().isEmpty()) ? item.getDepartmentCode() :"");
						issuanceItems.setDepartmentName((item.getDepartmentName()!=null && !item.getDepartmentName().isEmpty()) ? item.getDepartmentName() :"");
						issuanceItems.setItemClass((item.getItemClass()!=null && !item.getItemClass().isEmpty()) ? item.getItemClass() : "");
						issuanceItems.setItemClassName((item.getItemClassName()!=null && !item.getItemClassName().isEmpty()) ? item.getItemClassName() : "");
						issuanceItems.setItemSubClass((item.getItemSubClass()!=null && !item.getItemSubClass().isEmpty()) ? item.getItemSubClass() : "");
						issuanceItems.setItemSubClassName((item.getItemSubClassName()!=null && !item.getItemSubClassName().isEmpty()) ? item.getItemSubClassName() : "");
						issuanceItems.setDCS((item.getDCS()!=null && !item.getDCS().isEmpty()) ? item.getDCS() :"");
						issuanceItems.setVendorCode((item.getVendorCode()!=null && !item.getVendorCode().isEmpty()) ? item.getVendorCode() : "");
						issuanceItems.setVendorName((item.getVendorName()!=null && !item.getVendorName().isEmpty()) ? item.getVendorName() :"");
						issuanceItems.setSkuNumber((item.getSkuNumber()!=null && !item.getSkuNumber().isEmpty()) ? item.getSkuNumber() : "");
						issuanceItems.setBilledUnitPrice(item.getBilledUnitPrice()==null ? item.getBilledUnitPrice() : item.getBilledUnitPrice());
						issuanceItems.setQuantity(item.getQuantity());
						issuanceItems.setTax((item.getTax()!=null && !item.getTax().isEmpty()) ? item.getTax() : "");
						issuanceItems.setDiscount((item.getDiscount()!=null && !item.getDiscount().isEmpty()) ? item.getDiscount() :"");
						issuanceItems.setItemSID((item.getItemSID()!=null && !item.getItemSID().isEmpty()) ? item.getItemSID() :"");
						issuanceItems.setItemNote(item.getItemNote()!=null && !item.getItemNote().isEmpty()?item.getItemNote() : "");
						
						String Discount = item.getDiscount();
						//Double DiscDbl = Discount!= null && !Discount.isEmpty() ? Double.parseDouble(Discount):0.0;
						double DiscDbl = Discount!= null && !Discount.isEmpty() ? 
								(Double.valueOf(item.getDiscount()))*(Double.valueOf(item.getQuantity())):0.0;
						totalItemDisc+=DiscDbl;
						
						//APP-3925
						String itemPrcForLPV = item.getDocItemOrigPrc()!=null && !item.getDocItemOrigPrc().isEmpty() ? item.getDocItemOrigPrc() : item.getInvcItemPrc();
						if(itemPrcForLPV!=null && !itemPrcForLPV.isEmpty()) actualPurchaseValue += (Double.valueOf(itemPrcForLPV)*(Double.valueOf(item.getQuantity())))-(DiscDbl);
						
						if(excludeDiscountedItem ){
							logger.debug("item.getItemPromoDiscount()=="+item.getItemPromoDiscount());
							//for ginesys ignore only when there is store+promo discount
							boolean ignoreOnDiscPerc = false;
							if(ignoreDiscountPerc != null && ignoreDiscountPerc>0) {
								String itemPrc = item.getDocItemOrigPrc()!=null && !item.getDocItemOrigPrc().isEmpty() ? item.getDocItemOrigPrc() : item.getInvcItemPrc();
								double Disc = Discount!= null && !Discount.isEmpty() ? 
										(Double.valueOf(item.getDiscount())) :0;
								Double discperc = (Disc/(Double.valueOf(itemPrc)))*100;
								if( discperc > ignoreDiscountPerc) continue;
								
							}else {
								
								if(item.getItemPromoDiscount() != null ){
									if(!item.getItemPromoDiscount().isEmpty() && (Double.valueOf(item.getItemPromoDiscount())) > 0) {
										if((Double.valueOf(item.getItemPromoDiscount())) > 1) continue;
										else if((Double.valueOf(item.getItemPromoDiscount())) == 1 && !allowIssuanceOnOfferItem) continue;//APP-4657
									}
								}else{
									
									if(!ignoreOnDiscPerc && DiscDbl > 0) continue;
								}
							}
							
						}
						String itemPrc = item.getDocItemOrigPrc()!=null && !item.getDocItemOrigPrc().isEmpty() ? item.getDocItemOrigPrc() : item.getInvcItemPrc();
						if(itemPrc!=null && !itemPrc.isEmpty()) enteredValue += (Double.valueOf(itemPrc)*(Double.valueOf(item.getQuantity())))-(DiscDbl);
						//enteredValue=Math.abs(enteredValue);
						
						issuanceRequestItems.add(issuanceItems);
					//}
				}
				logger.debug("enteredValue===1=="+ enteredValue);
				if(enteredValue > 0 && DRToLtyExtractionData.getNonInvnPromoItemsDiscount() != null ){
					enteredValue = enteredValue-DRToLtyExtractionData.getNonInvnPromoItemsDiscount();
				}
				amount.setReceiptAmount(String.valueOf(enteredValue));
				
				/*if(enteredValue > 0 && digitalReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_Shopify) && 
						receiptPromoDisc>0 && digitalReceiptsJSON.getSource()!=null){ // APP-4592
					amount.setReceiptAmount(String.valueOf(enteredValue-receiptPromoDisc));
				}*/
				double tenderValue=(tenderAmount!=null && !tenderAmount.isEmpty()) ? Double.parseDouble(tenderAmount) : 0.0;
				if(enteredValue > 0 && tenderValue>0 && digitalReceiptsJSON.getSource()!=null && 
						!digitalReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_WooCommerce) &&
						!digitalReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_Shopify) ) {// APP-4592
						//!digitalReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_OPTDR)){
					
					enteredValue = enteredValue-tenderValue;
				}
				logger.debug("enteredValue===2=="+ enteredValue);
				if(receipt.getDiscount()!=null && !receipt.getDiscount().isEmpty()) { //for prism
					receiptPromoDisc = Double.valueOf(receipt.getDiscount());
				}
				
				/*
				 * logger.info("receiptPromoDisc in issuance "+receiptPromoDisc +" "+
				 * DRToLtyExtractionData.getRedemptionDetails().isDiscountSpreaded() +
				 * " enterd value=="+enteredValue);
				 */if(enteredValue > 0 && receipt.getCustomTender()!=null) {
					//Ginesys
					double discountToBeDeducted = receiptPromoDisc-totalItemDisc;
					enteredValue = enteredValue-discountToBeDeducted;
					logger.debug("enteredValue===3=="+ enteredValue);
				}else if(enteredValue > 0 && receiptPromoDisc>0 && digitalReceiptsJSON.getSource()!=null && 
						!digitalReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_WooCommerce) &&
						!digitalReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_OPTDR) && 
						!digitalReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_PRISM) &&
						!digitalReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_HEARTLAND) &&
						!digitalReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_Shopify) &&
						!digitalReceiptsJSON.getSource().equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_ORION)) {
					enteredValue = enteredValue-receiptPromoDisc;
					logger.debug("enteredValue===4=="+ enteredValue);
				}
				if(enteredValue > 0 && DRToLtyExtractionData.getRedemptionDetails() != null && DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount() != null && 
						!DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount().isEmpty()) {
					enteredValue = enteredValue-(! DRToLtyExtractionData.getRedemptionDetails().isDiscountSpreaded() ? 
							(Double.parseDouble(DRToLtyExtractionData.getRedemptionDetails().getRedemptionAsDiscount())) : 0);
				}
				
				if(excludegiftRedemption) {//APP-4163
					Double giftredemptionAmt = 0.0;
					Gson gson = new Gson ();
					
					DigitalReceipt DRJSON = gson.fromJson(digitalReceiptsJSON.getJsonStr(), DigitalReceipt.class);//digitalReceiptsJSON.getJsonStr();
					if(DRJSON != null && DRJSON.getBody().getGiftCard() != null && 
							!DRJSON.getBody().getGiftCard().isEmpty()) {
						List<GiftCard> giftredemptions = DRJSON.getBody().getGiftCard();
						for (GiftCard eachGiftCard : giftredemptions) {
							String redeemedAmount = eachGiftCard.getTaken();
							giftredemptionAmt += redeemedAmount != null && !redeemedAmount.isEmpty() ? Double.parseDouble(redeemedAmount) :0.0;
							
						}
						
						
					}
					enteredValue = enteredValue-giftredemptionAmt;
				}
				logger.info("enteredValue in issuance and LPV"+enteredValue+" "+actualPurchaseValue);
				amount.setPurchaseValue(String.valueOf(Math.abs(actualPurchaseValue)));//APP-3925
				amount.setEnteredValue(String.valueOf(Math.abs(enteredValue)));
				amount.setIgnoreIssuance(ignoreissuanceOnRedemption ? "Y" : null);
			issuanceRequest.setHeader(header);
			issuanceRequest.setMembership(membershipRequest);
			issuanceRequest.setAmount(amount);
			issuanceRequest.setItems(issuanceRequestItems);
			issuanceRequest.setCustomer(customer);
			issuanceRequest.setUser(loyaltyUser);
			discounts.setPromotions(promotionList);
			issuanceRequest.setDiscounts(discounts);
			LoyaltyTransaction transaction = null;
			transaction = findTransactionByRequestId(requestId);
			LoyaltyTransactionParent tranParent = createNewTransaction(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE); 
			Date date = tranParent.getCreatedDate().getTime();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
			String transDate = df.format(date);
			String responseJson ="";
			String reqJson="";
			Status status = null;
			Gson gson = new Gson();
			String userName = null;
			try{
			ResponseHeader responseHeader = new ResponseHeader();
			responseHeader.setRequestDate("");
			responseHeader.setRequestId("");
			responseHeader.setTransactionDate(transDate);
			responseHeader.setTransactionId(""+tranParent.getTransactionId());
			try{
				reqJson = gson.toJson(issuanceRequest, LoyaltyIssuanceRequest.class);
				logger.info("reqJson "+reqJson);
			}catch(Exception e){
				status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(issuanceResponse);
				updateIssuanceTransaction(tranParent, issuanceResponse, null);
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return null;
			}
			/*if(issuanceRequest == null){
				status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(issuanceResponse);
				logger.info("Response = "+responseJson);
				updateIssuanceTransaction(tranParent, issuanceResponse, null);
				return null;
			}*/
			
			if(issuanceRequest.getHeader() == null){
				status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(issuanceResponse);
				logger.info("Response = "+responseJson);
				updateIssuanceTransaction(tranParent, issuanceResponse, null);
				return null;
			}
			
			if(issuanceRequest.getHeader().getRequestId() == null || issuanceRequest.getHeader().getRequestId().trim().isEmpty() ||
					issuanceRequest.getHeader().getRequestDate() == null || issuanceRequest.getHeader().getRequestDate().trim().isEmpty()){
				status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(issuanceResponse);
				updateIssuanceTransaction(tranParent, issuanceResponse, null);
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(issuanceRequest.getMembership() == null){
				responseHeader.setRequestId(issuanceRequest.getHeader().getRequestId());
				responseHeader.setRequestDate(issuanceRequest.getHeader().getRequestDate());
				status = new Status("101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(issuanceResponse);
				logger.info("Response = "+responseJson);
				updateIssuanceTransaction(tranParent, issuanceResponse, null);
				return null;
			}
			
			
			if(issuanceRequest.getUser() == null){
				responseHeader.setRequestId(issuanceRequest.getHeader().getRequestId());
				responseHeader.setRequestDate(issuanceRequest.getHeader().getRequestDate());
				status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(issuanceResponse);
				logger.info("Response = "+responseJson);
				updateIssuanceTransaction(tranParent, issuanceResponse, null);
				return null;
			}
			
			if(issuanceRequest.getUser().getUserName() == null || issuanceRequest.getUser().getUserName().trim().length() <=0 || 
					issuanceRequest.getUser().getOrganizationId() == null || issuanceRequest.getUser().getOrganizationId().trim().length() <=0 || 
							issuanceRequest.getUser().getToken() == null || issuanceRequest.getUser().getToken().trim().length() <=0) {
				responseHeader.setRequestId(issuanceRequest.getHeader().getRequestId());
				responseHeader.setRequestDate(issuanceRequest.getHeader().getRequestDate());
				status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(issuanceResponse);
				logger.info("Response = "+responseJson);
				updateIssuanceTransaction(tranParent, issuanceResponse, null);
				return null;
			}
			userName = issuanceRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					issuanceRequest.getUser().getOrganizationId();
			
			LoyaltyTransaction trans = findRequestBydocSid(issuanceRequest.getUser().getUserName() + "__" +
					issuanceRequest.getUser().getOrganizationId(), issuanceRequest.getHeader().getDocSID().trim());
			if(trans != null){
				logger.info("Duplicate = "+responseJson);
				responseHeader.setRequestId(issuanceRequest.getHeader().getRequestId());
				responseHeader.setRequestDate(issuanceRequest.getHeader().getRequestDate());
				status = new Status("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				logger.info("Response = "+responseJson);
				updateIssuanceTransaction(tranParent, issuanceResponse, null);
				return null;
			}
			
			/*String pcFlag = issuanceRequest.getHeader().getPcFlag();
			String requestId = issuanceRequest.getHeader().getRequestId();
			LoyaltyTransaction transaction = null;
			if(pcFlag != null && pcFlag.equalsIgnoreCase("true")){
				transaction = findTransactionByRequestId(requestId);
				if(transaction != null && transaction.getStatus().equals(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED)){
					responseJson = transaction.getJsonResponse();
					updateIssuanceTransaction(tranParent, issuanceResponse, null);
					logger.info("Response = "+responseJson);
					return null;
				}
			}*/
			if(transaction == null){
				transaction = logIssuanceTransactionRequest(issuanceRequest, reqJson, OCConstants.LOYALTY_OFFLINE_MODE);
			}
			issuanceRequest.setJsonValue(reqJson);
			logger.info("request json "+reqJson);
			//send issuance request
			LoyaltyIssuanceOCService loyaltyIssuanceOCService = (LoyaltyIssuanceOCService) ServiceLocator.getInstance()
					.getServiceByName(OCConstants.LOYALTY_ISSUANCE_OC_BUSINESS_SERVICE);
			issuanceResponse = loyaltyIssuanceOCService.processIssuanceRequest(issuanceRequest,
					OCConstants.LOYALTY_OFFLINE_MODE,transaction.getId().toString(),transDate,
					OCConstants.DR_TO_LTY_EXTRACTION);
			responseJson = new Gson().toJson(issuanceResponse, LoyaltyIssuanceResponse.class);	
			logger.info("Response = "+responseJson);
			if(responseJson!=null){
				updateIssuanceTransactionStatus(transaction,responseJson,issuanceResponse);
			}
			
			}catch(Exception e){
				logger.error("Error in issuance restservice", e);
				ResponseHeader responseHeader = null;
				if(issuanceResponse == null){
					responseHeader = new ResponseHeader();
					responseHeader.setRequestDate("");
					responseHeader.setRequestId("");
					responseHeader.setTransactionDate(transDate);
					responseHeader.setTransactionId(""+tranParent.getTransactionId());
				}
				else{
					responseHeader = issuanceResponse.getHeader();
				}
				
				status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				issuanceResponse = prepareIssuanceResponse(responseHeader, null, null, null, null, status);
				responseJson = gson.toJson(issuanceResponse);
				updateIssuanceTransaction(tranParent, issuanceResponse, userName);
				logger.info("Response = "+responseJson);
				logger.info("Ended Loyalty Issuance Rest Service... at "+System.currentTimeMillis());
				return null;
			}finally{
				logger.info("Response = "+responseJson);
				//send alert mail for falures
				if(!issuanceResponse.getStatus().getErrorCode().equalsIgnoreCase("0")) {
					Utility.sendDRToLtyFailureMail(receipt.getDocSID(),receipt.getDocDate(), receipt.getDocTime(),user,OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,issuanceResponse.getStatus().getMessage());
				}
				logger.info("Completed Loyalty Issuance Rest Service.");
			}
			logger.info("issuance from DR ended");
		}catch(Exception e){
			logger.error("Exception ",e);
		}
		return issuanceResponse;
	}
	
	public LoyaltyRedemptionResponse prepareRedemptionFromDRRequest(DRToLtyExtractionData DRToLtyExtractionData, 
			Users user, String tenderAmount,
			boolean mobileBasedEnroll,String valueCode,boolean redeemFromPromo) {
		
		logger.info("started redemption from DR");
		ServiceLocator context = ServiceLocator.getInstance();
		
		DRToLoyaltyExtractionAuth authUser = DRToLtyExtractionData.getUser();
		DRToLoyaltyExtractionReceipt receipt = DRToLtyExtractionData.getReceipt();
		DRToLoyaltyExtractionCustomer customerData = DRToLtyExtractionData.getCustomer();
		DRToLoyaltyExtractionParams headerParams = DRToLtyExtractionData.getHeaderFileds();
		List<DRToLoyaltyExtractionItems> items = DRToLtyExtractionData.getItems();
		
		/*DRHead head = new DRHead();
		head = (drJson.getHead()!=null ? drJson.getHead() : null);
		DigitalReceiptBody body =new DigitalReceiptBody();
		body = drJson.getBody();
		DRReceipt receipt = new DRReceipt();
		receipt = drJson.getBody().getReceipt();*/
		
		LoyaltyRedemptionRequest loyaltyRedemptionRequest= new LoyaltyRedemptionRequest();
		LoyaltyRedemptionResponse redemptionResponse = null;
		
		 RequestHeader header =new RequestHeader();
		 MembershipRequest membership = new MembershipRequest();
		 Amount amount = new Amount();
		 Customer customer =new Customer();
		 LoyaltyUser loyaltyUser = new LoyaltyUser();
		 Discounts discounts=new Discounts();
		 List<Promotion> promotionList = new ArrayList<Promotion>();
		 //Promotion promotion = new Promotion();
		 String requestId=Constants.STRING_NILL;
		 String OTPCode = DRToLtyExtractionData.getOTPCode() != null ? DRToLtyExtractionData.getOTPCode() : "";
		 logger.debug("OTPCode ---"+OTPCode );
		 /*if(redeemFromPromo){
			 requestId = OCConstants.DR_LTY_PROMO_REDEMPTION_REQUEST_ID+user.getToken()+"_"+System.currentTimeMillis();
		 }else {
		 	requestId = OCConstants.DR_LTY_EXTRACTION_REQUEST_ID+user.getToken()+"_"+System.currentTimeMillis();
		 }*/
		 requestId = OCConstants.DR_LTY_EXTRACTION_REQUEST_ID+authUser.getToken()+"_"+System.currentTimeMillis();
			
			header.setRequestId(requestId);
			POSMappingDao posMappingDao=null;
			
			//matching doc date with his pos mapping date format.
			Calendar syscal = new MyCalendar(Calendar.getInstance(), null,
					MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
			header.setRequestDate(syscal.toString());
			logger.info("syscal.toString() : "+syscal.toString());
			if(receipt.getDocDate()!=null && !receipt.getDocDate().isEmpty()) {
			try {
				String fieldValue = (Constants.STRING_NILL+receipt.getDocDate()+" "+receipt.getDocTime()).replace("T", Constants.STRING_NILL);
				DateFormat formatter;
				Date date;
				formatter = new SimpleDateFormat(MyCalendar.FORMAT_mm_DATETIME_STYEAR);
				date = (Date) formatter.parse(fieldValue);
				Calendar cal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
				cal.setTime(date);
				header.setRequestDate(cal.toString());
				logger.info("cal.toString() : "+cal.toString());
				
				} catch (Exception e) {
					logger.info("date format not matched with data, setting system date",e);
					header.setRequestDate(syscal.toString());
				}
			}
			
			  header.setStoreNumber(receipt.getStore());
		      header.setDocSID(receipt.getDocSID());
		      header.setReceiptNumber(receipt.getInvcNum());
		      header.setSubsidiaryNumber(receipt.getSubsidiaryNumber());
		      //pcFlag
		      //subsidiaryNumber
		      //employeeId
		      //terminalId
		      
		      if(mobileBasedEnroll || (customerData.getConLoyalty() !=null && 
		    		  customerData.getConLoyalty().getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE))) {
		    	  membership.setPhoneNumber(customerData.getPhone());
		    	  membership.setCardNumber("");
		      }else {
		    	  membership.setCardNumber(customerData.getConLoyalty().getCardNumber());
		    	  membership.setPhoneNumber("");
		      }
		      //membership.setCardNumber(receipt.getBillToInfo1());
		     // membership.setPhoneNumber("");
		      membership.setCardPin(customerData.getCardPin());
		      
		      amount.setType(OCConstants.LOYALTY_TYPE_PURCHASE);
		      
			  amount.setEnteredValue(String.valueOf(Math.abs(Double.valueOf(tenderAmount))));
			  if(valueCode.isEmpty()) {
				  amount.setValueCode(OCConstants.LOYALTY_TYPE_POINTS);
			  }
			  else if(valueCode.equals(OCConstants.LOYALTY_USD)) {
				  amount.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
			  }else {
				  amount.setValueCode(valueCode);
			  }
			  amount.setReceiptAmount(receipt.getTotal());
			  
			  //APP-4874
			  if(DRToLtyExtractionData.getPromotions()!=null) {
					List<DRToLoyaltyExtractionPromotions> promotionsList = DRToLtyExtractionData.getPromotions();
					Iterator<DRToLoyaltyExtractionPromotions> iterPromosList = promotionsList.iterator();
					while (iterPromosList.hasNext()) {
						DRToLoyaltyExtractionPromotions ocpromotion=iterPromosList.next();
						if(ocpromotion!=null && ocpromotion.getCouponCode()!=null && !ocpromotion.getCouponCode().isEmpty()) {//APP-4874
							
							Promotion promotion = new Promotion();
							promotion.setName(ocpromotion.getCouponCode());
							promotionList.add(promotion);
						}
					}
				}
				
			  if(promotionList!=null && promotionList.size()>0) {//APP-4874
					discounts.setAppliedPromotion("Y");
				}else {
					Promotion promotion = new Promotion();
					promotion.setName("");
					promotionList.add(promotion);
					discounts.setAppliedPromotion("NA");
				}
				discounts.setPromotions(promotionList);
				
				customer.setCustomerId(customerData.getCustomerId().trim());
				customer.setFirstName(customerData.getFirstName());
				customer.setLastName(customerData.getLastName());
				customer.setPhone(customerData.getPhone());
				customer.setEmailAddress(customerData.getEmailAddress());
				customer.setBirthday(customerData.getBirthday());
				customer.setAddressLine1(customerData.getAddressLine1()); //addressLine2 ? 
				customer.setCity(customerData.getCity()); 
				customer.setState(customerData.getState());
				customer.setPostal(customerData.getPostal());
				customer.setAnniversary(customerData.getAnniversary());
				//customer.setGender(gender);
				//customer.setCreatedDate(receipt.billtocreated);
				
				loyaltyUser.setUserName(authUser.getUserName());
				loyaltyUser.setOrganizationId(authUser.getOrgID());
				loyaltyUser.setToken(authUser.getToken());
				//loyaltyRedemptionRequest.setOtpCode(otpCode);
				
				loyaltyRedemptionRequest.setHeader(header);
				loyaltyRedemptionRequest.setMembership(membership);
				loyaltyRedemptionRequest.setAmount(amount);
				loyaltyRedemptionRequest.setCustomer(customer);
				loyaltyRedemptionRequest.setUser(loyaltyUser);
				discounts.setPromotions(promotionList);
				loyaltyRedemptionRequest.setDiscounts(discounts);
				loyaltyRedemptionRequest.setOtpCode(OTPCode);
				
				LoyaltyTransactionParent tranParent = createNewTransaction(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION); 
				Date date = tranParent.getCreatedDate().getTime();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
				String transDate = df.format(date);
				
				Status status = null;
				String responseJson = "";
				String requestJson="";
				Gson gson = new Gson();
				String userName = null;
				
				
				try{
					ResponseHeader responseHeader = new ResponseHeader();
					responseHeader.setRequestDate("");
					responseHeader.setRequestId("");
					responseHeader.setTransactionDate(transDate);
					responseHeader.setTransactionId(""+tranParent.getTransactionId());
					
					try{
						requestJson = gson.toJson(loyaltyRedemptionRequest, LoyaltyRedemptionRequest.class);
					}catch(Exception e){
						status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						responseJson = gson.toJson(redemptionResponse);
						updateRedemptionTransaction(tranParent, redemptionResponse, null);
						logger.info("Response = "+responseJson);
						logger.error("Exception in parsing request json ...",e);
						return null;
					}
					
					if(loyaltyRedemptionRequest.getHeader() == null){
						status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						responseJson = gson.toJson(redemptionResponse);
						updateRedemptionTransaction(tranParent, redemptionResponse, null);
						logger.info("Response = "+responseJson);
						return null;
					}
					if(loyaltyRedemptionRequest.getHeader().getRequestId() == null || loyaltyRedemptionRequest.getHeader().getRequestId().trim().isEmpty() ||
							loyaltyRedemptionRequest.getHeader().getRequestDate() == null || loyaltyRedemptionRequest.getHeader().getRequestDate().trim().isEmpty()){
						status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						responseJson = gson.toJson(redemptionResponse);
						updateRedemptionTransaction(tranParent, redemptionResponse, null);
						logger.info("Response = "+responseJson);
						return null;
					}
					
					if(loyaltyRedemptionRequest.getMembership() == null){
						responseHeader.setRequestId(loyaltyRedemptionRequest.getHeader().getRequestId());
						responseHeader.setRequestDate(loyaltyRedemptionRequest.getHeader().getRequestDate());
						status = new Status("101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						responseJson = gson.toJson(redemptionResponse);
						updateRedemptionTransaction(tranParent, redemptionResponse, null);
						logger.info("Response = "+responseJson);
						return null;
					}
					if(loyaltyRedemptionRequest.getUser() == null){
						responseHeader.setRequestId(loyaltyRedemptionRequest.getHeader().getRequestId());
						responseHeader.setRequestDate(loyaltyRedemptionRequest.getHeader().getRequestDate());
						status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						responseJson = gson.toJson(redemptionResponse);
						updateRedemptionTransaction(tranParent, redemptionResponse, null);
						logger.info("Response = "+responseJson);
						return null;
					}
					if(loyaltyRedemptionRequest.getUser().getUserName() == null || loyaltyRedemptionRequest.getUser().getUserName().trim().length() <=0 || 
							loyaltyRedemptionRequest.getUser().getOrganizationId() == null || loyaltyRedemptionRequest.getUser().getOrganizationId().trim().length() <=0 || 
									loyaltyRedemptionRequest.getUser().getToken() == null || loyaltyRedemptionRequest.getUser().getToken().trim().length() <=0) {
						responseHeader.setRequestId(loyaltyRedemptionRequest.getHeader().getRequestId());
						responseHeader.setRequestDate(loyaltyRedemptionRequest.getHeader().getRequestDate());
						status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						responseJson = gson.toJson(redemptionResponse);//APP-1206
						logger.info("Response = "+responseJson);
						updateRedemptionTransaction(tranParent, redemptionResponse, null);
						return null;
					}
					userName = loyaltyRedemptionRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
							          loyaltyRedemptionRequest.getUser().getOrganizationId();
					
					LoyaltyTransaction trans =  null;
					if(amount.getValueCode().equals(OCConstants.LOYALTY_TYPE_CURRENCY)){
						
						trans = findRequestByReqIdAndDocSid(loyaltyRedemptionRequest.getUser().getUserName() + "__" +
								loyaltyRedemptionRequest.getUser().getOrganizationId(), loyaltyRedemptionRequest.getHeader().getRequestId().trim(), 
								loyaltyRedemptionRequest.getHeader().getDocSID().trim());
						if(trans != null){
							logger.info("Duplicate = "+responseJson);
							responseHeader.setRequestId(loyaltyRedemptionRequest.getHeader().getRequestId());
							responseHeader.setRequestDate(loyaltyRedemptionRequest.getHeader().getRequestDate());
							status = new Status("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
							responseJson = gson.toJson(redemptionResponse);
							logger.info("Response = "+responseJson);
							updateRedemptionTransaction(tranParent, redemptionResponse, null);
							return null;
						}
					}
					String reqJson = gson.toJson(loyaltyRedemptionRequest, LoyaltyRedemptionRequest.class);
					if(trans==null){
						trans = logRedemptionTransactionRequest(loyaltyRedemptionRequest, reqJson,"online");
					}
				
				LoyaltyRedemptionOCService loyaltyRedemptionService = (LoyaltyRedemptionOCService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_REDEMPTION_OC_BUSINESS_SERVICE);
				redemptionResponse = loyaltyRedemptionService.processRedemptionRequest(loyaltyRedemptionRequest, OCConstants.LOYALTY_ONLINE_MODE, 
									 trans.getId()+"", transDate,OCConstants.DR_TO_LTY_EXTRACTION);
				responseJson = gson.toJson(redemptionResponse);
				logger.info("Redemption response : "+responseJson);
				updateRedemptionTransactionStatus(trans, responseJson, loyaltyRedemptionRequest,user);
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
					try {
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						logger.error("Exception ",e);
					}
					responseJson = gson.toJson(redemptionResponse);
					updateRedemptionTransaction(tranParent, redemptionResponse, userName);
					logger.info("Response = "+responseJson);
					return null;
				}
				//send alert mail for falures
				if(!redemptionResponse.getStatus().getErrorCode().equalsIgnoreCase("0")) {
					Utility.sendDRToLtyFailureMail(receipt.getDocSID(),receipt.getDocDate(), receipt.getDocTime(),user,OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,redemptionResponse.getStatus().getMessage());
				}
				logger.info("redemption from DR ended");
		return redemptionResponse;
	}
	
	public LoyaltyReturnTransactionResponse prepareReturnFromDRRequest(DRToLtyExtractionData  DRToLtyExtractionData, Users user,
			List<DRToLoyaltyExtractionItems> returnItemsList,boolean mobileBasedEnroll,String tenderGiven){
		
		logger.info("entered return from DR");
		
		DRToLoyaltyExtractionAuth authUser = DRToLtyExtractionData.getUser();
		DRToLoyaltyExtractionReceipt receipt = DRToLtyExtractionData.getReceipt();
		DRToLoyaltyExtractionCustomer customerData = DRToLtyExtractionData.getCustomer();
		DRToLoyaltyExtractionParams headerParams = DRToLtyExtractionData.getHeaderFileds();
		List<DRToLoyaltyExtractionItems> items = DRToLtyExtractionData.getItems();
		String requestType = DRToLtyExtractionData.getHeaderFileds().getRequestType();
		/*DRHead head = new DRHead();
		head = (drJson.getHead()!=null ? drJson.getHead() : null);
		DigitalReceiptBody body =new DigitalReceiptBody();
		body = drJson.getBody();
		DRReceipt receipt = new DRReceipt();
		receipt = drJson.getBody().getReceipt();*/
		
		LoyaltyReturnTransactionRequest returnRequest = new LoyaltyReturnTransactionRequest();
		LoyaltyReturnTransactionResponse returnResponse = new LoyaltyReturnTransactionResponse();
		
		RequestHeader requestHeader = new RequestHeader();
		OriginalReceipt receiptOriginalReceipt = new OriginalReceipt();
		MembershipRequest membership = new MembershipRequest();
		Amount amount=new Amount();
		Customer customer = new Customer();
		LoyaltyUser loyaltyUser = new LoyaltyUser();
		
		String requestId = OCConstants.DR_LTY_EXTRACTION_REQUEST_ID+authUser.getToken()+"_"+System.currentTimeMillis();
		requestHeader.setRequestId(requestId);
		
		ServiceLocator context = ServiceLocator.getInstance();
		POSMappingDao posMappingDao;
		//matching doc date with his pos mapping date format.
		Calendar syscal = new MyCalendar(Calendar.getInstance(), null,
				MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
		requestHeader.setRequestDate(syscal.toString());
		logger.info("syscal.toString() : "+syscal.toString());
		if(receipt.getDocDate()!=null && !receipt.getDocDate().isEmpty()) {
		try {
			String fieldValue = (Constants.STRING_NILL+receipt.getDocDate()+" "+receipt.getDocTime()).replace("T", Constants.STRING_NILL);
			DateFormat formatter;
			Date date;
			formatter = new SimpleDateFormat(MyCalendar.FORMAT_mm_DATETIME_STYEAR);
			date = (Date) formatter.parse(fieldValue);
			Calendar cal = new MyCalendar(Calendar.getInstance(), null,
					MyCalendar.dateFormatMap.get(MyCalendar.FORMAT_DATETIME_STYEAR));
			cal.setTime(date);
			requestHeader.setRequestDate(cal.toString());
			logger.info("cal.toString() : "+cal.toString());
			
			} catch (Exception e) {
				logger.info("date format not matched with data, setting system date",e);
				requestHeader.setRequestDate(syscal.toString());
			}
		}
		//requestHeader.setPcFlag(pcFlag);
		requestHeader.setSubsidiaryNumber(receipt.getSubsidiaryNumber());
		//requestHeader.setEmployeeId(employeeId);
		//requestHeader.setTerminalId(terminalId);
		requestHeader.setStoreNumber(receipt.getStore());
		requestHeader.setDocSID(receipt.getDocSID());
		requestHeader.setReceiptNumber(receipt.getInvcNum());
		
		//Items items = body.getItems();
		//List<DRItem> itemsList = items.getItems();
		List<SkuDetails> returnRequestItems = new ArrayList<SkuDetails>();
		if(returnItemsList!=null && !returnItemsList.isEmpty()) {
		Iterator<DRToLoyaltyExtractionItems> iterItems = returnItemsList.iterator();
		DRToLoyaltyExtractionItems item= null;
		double enteredValue=0.0;
		String receiptOriginalDocSid=receipt.getRefDocSID() == null ? Constants.STRING_NILL : receipt.getRefDocSID();
		String receiptOriginalReceiptNum=receipt.getRefReceipt() == null ? Constants.STRING_NILL : receipt.getRefReceipt();
		String refStore = receipt.getRefStore() == null? Constants.STRING_NILL : receipt.getRefStore();
		String refSbs = receipt.getRefSbs()== null? Constants.STRING_NILL : receipt.getRefSbs();
		boolean doNotAddOR = false;
		List<SkuDetails> returnWithNoORRequestItems = new ArrayList<SkuDetails>();
		boolean noOrinalRcpt = false;
			while (iterItems.hasNext()) {
				item = iterItems.next();
				OriginalReceipt originalReceipt = new OriginalReceipt();
				//if(item.getQty()!=null && !item.getQty().trim().isEmpty() && (Double.valueOf(item.getQty())<0)){
					SkuDetails returnItems = new SkuDetails();
					returnItems.setItemCategory(item.getItemCategory() == null || item.getItemCategory().isEmpty() ? item.getItemCategory() : item.getItemCategory());
					returnItems.setDepartmentCode((item.getDepartmentCode()!=null && !item.getDepartmentCode().isEmpty()) ? item.getDepartmentCode() :"");
					returnItems.setDepartmentName((item.getDepartmentName()!=null && !item.getDepartmentName().isEmpty()) ? item.getDepartmentName() :"");
					returnItems.setItemClass((item.getItemClass()!=null && !item.getItemClass().isEmpty()) ? item.getItemClass() : "");
					returnItems.setItemClassName((item.getItemClassName()!=null && !item.getItemClassName().isEmpty()) ? item.getItemClassName() : "");
					returnItems.setItemSubClass((item.getItemSubClass()!=null && !item.getItemSubClass().isEmpty()) ? item.getItemSubClass() : "");
					returnItems.setItemSubClassName((item.getItemSubClassName()!=null && !item.getItemSubClassName().isEmpty()) ? item.getItemSubClassName() : "");
					returnItems.setDCS((item.getDCS()!=null && !item.getDCS().isEmpty()) ? item.getDCS() :"");
					returnItems.setVendorCode((item.getVendorCode()!=null && !item.getVendorCode().isEmpty()) ? item.getVendorCode() : "");
					returnItems.setVendorName((item.getVendorName()!=null && !item.getVendorName().isEmpty()) ? item.getVendorName() :"");
					returnItems.setSkuNumber((item.getSkuNumber()!=null && !item.getSkuNumber().isEmpty()) ? item.getSkuNumber() : "");
					returnItems.setBilledUnitPrice(item.getBilledUnitPrice()==null ? item.getBilledUnitPrice() : item.getBilledUnitPrice());
					//returnItems.setQuantity(item.getQuantity());
					//returnItems.setQuantity(item.getQty());
					returnItems.setItemSID((item.getItemSID()!=null && !item.getItemSID().isEmpty()) ? item.getItemSID() :"");
					returnItems.setQuantity(String.valueOf(Math.abs(Double.valueOf(item.getQuantity()))));
					returnItems.setTax((item.getTax()!=null && !item.getTax().isEmpty()) ? item.getTax() : "");
					returnItems.setDiscount((item.getDiscount()!=null && !item.getDiscount().isEmpty()) ? item.getDiscount() :"");
					
					originalReceipt.setReceiptNumber(item.getRefReceipt()!=null && !item.getRefReceipt().isEmpty() ? item.getRefReceipt() :"");
					originalReceipt.setDocSID(item.getRefDocSID()!=null && !item.getRefDocSID().isEmpty() ? item.getRefDocSID() :"");
					originalReceipt.setStoreNumber(item.getRefStoreCode() != null && !item.getRefStoreCode().isEmpty() ? item.getRefStoreCode():"");
					originalReceipt.setSubsidiaryNumber(item.getRefSubsidiaryNumber() != null && !item.getRefSubsidiaryNumber().isEmpty() ? item.getRefSubsidiaryNumber():"");
					returnItems.setOriginalReceipt(originalReceipt);
					
					if(!originalReceipt.getDocSID().isEmpty() ||
							(!originalReceipt.getReceiptNumber().isEmpty() && 
									!originalReceipt.getStoreNumber().isEmpty())){
						if(!receiptOriginalDocSid.equalsIgnoreCase(originalReceipt.getDocSID()) &&
								!receiptOriginalReceiptNum.equalsIgnoreCase(originalReceipt.getReceiptNumber())){
							doNotAddOR = true;
						}
					}
					if(!doNotAddOR && originalReceipt.getDocSID().isEmpty() && 
							originalReceipt.getReceiptNumber().isEmpty() &&
							originalReceipt.getStoreNumber().isEmpty()) {

						returnWithNoORRequestItems.add(returnItems);
					}
					
					//(prc*qty)-disc -- removed +tax
					String Discount = item.getDiscount();
					//Double DiscDbl = Discount!= null && !Discount.isEmpty() ? Double.parseDouble(Discount):0.0;
					double DiscDbl = Discount!= null && !Discount.isEmpty() ? 
							(Double.valueOf(item.getDiscount()))*(Double.valueOf(item.getQuantity())):0.0;
					String itemPrc = item.getDocItemOrigPrc()!=null && !item.getDocItemOrigPrc().isEmpty() ? item.getDocItemOrigPrc() : item.getInvcItemPrc();
					if(itemPrc!=null && !itemPrc.isEmpty()) enteredValue += (Double.valueOf(itemPrc)*(Math.abs(Double.valueOf(item.getQuantity()))))-DiscDbl;
					enteredValue=Math.abs(enteredValue);
					
					returnRequestItems.add(returnItems);
					
				//}
			}
			if(!doNotAddOR && returnWithNoORRequestItems.size() ==  returnRequestItems.size()) {
				
				if(receiptOriginalReceiptNum!=null && !receiptOriginalReceiptNum.isEmpty()){
					receiptOriginalReceipt.setReceiptNumber(receiptOriginalReceiptNum);
				}
				if(receiptOriginalDocSid!=null && !receiptOriginalDocSid.isEmpty()){
					//returnRequest.setCreditRedeemedAmount("Y");
					receiptOriginalReceipt.setDocSID(receiptOriginalDocSid);
				}
				if(refStore != null && !refStore.isEmpty()){
					receiptOriginalReceipt.setStoreNumber(refStore);
					
				}
				if(refSbs != null && !refSbs.isEmpty()){
					receiptOriginalReceipt.setSubsidiaryNumber(refSbs);
				}
			}
			if(headerParams.getRequestType() != null && 
					headerParams.getRequestType().equalsIgnoreCase("Reversal")){
				if(receipt.getInvcNum()!=null && !receipt.getInvcNum().isEmpty()){
					receiptOriginalReceipt.setReceiptNumber(receipt.getInvcNum());
				}
				if(receipt.getDocSID()!=null && !receipt.getDocSID().isEmpty()){
					//returnRequest.setCreditRedeemedAmount("Y");
					receiptOriginalReceipt.setDocSID(receipt.getDocSID());
				}
				if(receipt.getStore() != null && !receipt.getStore().isEmpty()){
					receiptOriginalReceipt.setStoreNumber(receipt.getStore());
					
				}
				if(receipt.getSubsidiaryNumber() != null && !receipt.getSubsidiaryNumber().isEmpty()){
					receiptOriginalReceipt.setSubsidiaryNumber(receipt.getSubsidiaryNumber());
				}

			 
		 }
			//returnRequest.setCreditRedeemedAmount(user.isPerformRedeemedAmountReversal() && tenderValue<0 ? "Y" : "N");
			//double tenderValue=(tenderAmount!=null && !tenderAmount.isEmpty()) ? Double.parseDouble(tenderAmount) : 0.0;
		}
		boolean tenderGivenExists = tenderGiven!=null && !tenderGiven.isEmpty();
		/*	String tenderGiven = Constants.STRING_NILL;
			String userRedeemTender = user.getRedeemTender();
			if(userRedeemTender!=null && !userRedeemTender.isEmpty()) {
				if(userRedeemTender.contains(""+Constants.DELIMETER_COLON)) {
					String tenderType=user.getRedeemTender().split(""+Constants.DELIMETER_COLON)[1];
				List<CreditCard> listOfCards =receipt.getListOfCreditCards();
				
				if(listOfCards != null && listOfCards.size() != 0){
					for(CreditCard creditCard:listOfCards){
						if(creditCard.getType()!=null && 
								!creditCard.getType().isEmpty() && 
								creditCard.getType().equalsIgnoreCase(tenderType) ){
							if(creditCard.getGiven()!=null && !creditCard.getGiven().isEmpty()) tenderGivenExists =true;
							tenderGiven=(tenderGivenExists ? creditCard.getGiven() : creditCard.getAmount());
						}
					}
				}
			}else {
				COD COD = receipt.getCOD();
				if(COD.getGiven()!=null && !COD.getGiven().isEmpty()) tenderGivenExists =true;
				tenderGiven=(tenderGivenExists ? COD.getGiven() : COD.getAmount());
			}
			}*/
			double tenderGivenAmount=(tenderGiven!=null && !tenderGiven.isEmpty()) ? Double.parseDouble(tenderGiven) : 0.0;
			if(tenderGivenExists) {
				returnRequest.setCreditRedeemedAmount(user.isPerformRedeemedAmountReversal() ? "Y" : "N");
			}else {
				returnRequest.setCreditRedeemedAmount(user.isPerformRedeemedAmountReversal() && tenderGivenAmount<0 ? "Y" : "N");
			}
			
			//originalReceipt.setReceiptAmount();
			if(mobileBasedEnroll || (customerData.getConLoyalty() !=null && 
					customerData.getConLoyalty().getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE))) {
		    	  membership.setPhoneNumber(customerData.getPhone());
		    	  membership.setCardNumber("");
		      }else {
		    	  membership.setCardNumber(customerData.getConLoyalty().getCardNumber());
		  		  membership.setPhoneNumber("");
		      }
		//membership.setCardNumber(receipt.getBillToInfo1());
		//membership.setPhoneNumber("");
		membership.setCardPin(customerData.getCardPin());
		
		amount.setType(OCConstants.LOYALTY_TYPE_REVERSAL);
		amount.setValueCode(OCConstants.LOYALTY_TYPE_CURRENCY);
		amount.setEnteredValue(tenderGivenAmount<0 ? String.valueOf(Math.abs(tenderGivenAmount)) : String.valueOf(tenderGivenAmount));
		//amount.setReceiptAmount();
		
		customer.setCustomerId(customerData.getCustomerId().trim());
		customer.setFirstName(customerData.getFirstName());
		customer.setLastName(customerData.getLastName());
		customer.setPhone(customerData.getPhone());
		customer.setEmailAddress(customerData.getEmailAddress());
		//customer.setBirthday(birthday);
		
		loyaltyUser.setUserName(authUser.getUserName());
		loyaltyUser.setOrganizationId(authUser.getOrgID());
		loyaltyUser.setToken(authUser.getToken());
		
		returnRequest.setHeader(requestHeader);
		returnRequest.setOriginalReceipt(receiptOriginalReceipt);
		returnRequest.setMembership(membership);
		returnRequest.setItems(returnRequestItems);
		returnRequest.setAmount(amount);
		returnRequest.setCustomer(customer);
		returnRequest.setUser(loyaltyUser);
		
		LoyaltyTransactionParent tranParent = createNewTransaction(OCConstants.LOYALTY_TRANS_TYPE_RETURN); 
		Date date = tranParent.getCreatedDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String transDate = df.format(date);

		Status status = null;
		String responseJson = "";
		Gson gson = new Gson(); 
		String userName = null;
		String reqJson="";

		try{
			
				ResponseHeader responseHeader = new ResponseHeader();
				responseHeader.setRequestDate("");
				responseHeader.setRequestId("");
				responseHeader.setTransactionDate(transDate);
				responseHeader.setTransactionId(""+tranParent.getTransactionId());

				try{
					reqJson = gson.toJson(returnRequest, LoyaltyReturnTransactionRequest.class);
					logger.info("reqJson "+reqJson);
				}catch(Exception e){
					status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnResponse = prepareReturnResponse(responseHeader, null, null, null, null, status);
					responseJson = gson.toJson(returnResponse);
					updateReturnTransaction(tranParent, returnResponse, null);
					logger.info("Response = "+responseJson);
					logger.error("Exception in parsing request json ...",e);
					return null;
				}

				/*if(returnRequest == null){
					status = new Status("101002", PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnResponse = prepareReturnResponse(responseHeader, null, null, null, null, status);
					responseJson = gson.toJson(returnResponse);
					logger.info("Response = "+responseJson);
					updateReturnTransaction(tranParent, returnResponse, null);
					return null;
				}
*/
				if(returnRequest.getHeader() == null){
					status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnResponse = prepareReturnResponse(responseHeader, null, null, null, null, status);
					responseJson = gson.toJson(returnResponse);
					logger.info("Response = "+responseJson);
					updateReturnTransaction(tranParent, returnResponse, null);
					return null;
				}

				if(returnRequest.getHeader().getRequestId() == null || returnRequest.getHeader().getRequestId().trim().isEmpty() ||
						returnRequest.getHeader().getRequestDate() == null || returnRequest.getHeader().getRequestDate().trim().isEmpty() || 
								returnRequest.getHeader().getDocSID() == null || returnRequest.getHeader().getDocSID().trim().isEmpty()){
					status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnResponse = prepareReturnResponse(responseHeader, null, null, null, null, status);
					responseJson = gson.toJson(returnResponse);
					updateReturnTransaction(tranParent, returnResponse, null);
					logger.info("Response = "+responseJson);
					return null;
				}

				
				if(returnRequest.getMembership() == null){
					responseHeader.setRequestId(returnRequest.getHeader().getRequestId());
					responseHeader.setRequestDate(returnRequest.getHeader().getRequestDate());
					status = new Status("101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnResponse = prepareReturnResponse(responseHeader, null, null, null, null, status);
					responseJson = gson.toJson(returnResponse);
					logger.info("Response = "+responseJson);
					updateReturnTransaction(tranParent, returnResponse, null);
					return null;
				}
				if(returnRequest.getUser() == null){
					responseHeader.setRequestId(returnRequest.getHeader().getRequestId());
					responseHeader.setRequestDate(returnRequest.getHeader().getRequestDate());
					status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnResponse = prepareReturnResponse(responseHeader, null, null, null, null, status);
					responseJson = gson.toJson(returnResponse);
					logger.info("Response = "+responseJson);
					updateReturnTransaction(tranParent, returnResponse, null);
					return null;
				}

				if(returnRequest.getUser().getUserName() == null || returnRequest.getUser().getUserName().trim().length() <=0 || 
						returnRequest.getUser().getOrganizationId() == null || returnRequest.getUser().getOrganizationId().trim().length() <=0 || 
								returnRequest.getUser().getToken() == null || returnRequest.getUser().getToken().trim().length() <=0) {
					responseHeader.setRequestId(returnRequest.getHeader().getRequestId());
					responseHeader.setRequestDate(returnRequest.getHeader().getRequestDate());
					status = new Status("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					returnResponse = prepareReturnResponse(responseHeader, null, null, null, null, status);
					responseJson = gson.toJson(returnResponse);
					logger.info("Response = "+responseJson);
					updateReturnTransaction(tranParent, returnResponse, null);
					return null;
				}

			userName = returnRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					returnRequest.getUser().getOrganizationId();

			
			String userDetails = returnRequest.getUser().getUserName()+"__"+returnRequest.getUser().getOrganizationId();

			LoyaltyTransaction trans = null;
			requestType = OCConstants.LOYALTY_TYPE_REVERSAL;
			//find duplicity only when it is not reversal type
			if(requestType != null && !requestType.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_REVERSAL)){
				
				trans = findReturnRequestBydocSid(userDetails,receipt.getDocSID());
				responseHeader.setRequestDate("");
				responseHeader.setRequestId("");
				responseHeader.setTransactionDate(transDate);
				responseHeader.setTransactionId(""+tranParent.getTransactionId());
				if(trans != null){
					returnResponse = prepareReturnResponse(responseHeader, null, null, null, null, null);
					updateReturnTransaction(tranParent, returnResponse, null);
					return null;
				}
				else {
				}
			}else{
				trans = logReturnTransactionRequest(returnRequest, reqJson,"online");
				
			}
			
			LoyaltyReturnTransactionOCService loyaltyReturnTransactionOCService = (LoyaltyReturnTransactionOCService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_RETURN_TRANSACTION_OC_BUSINESS_SERVICE);
			returnResponse = loyaltyReturnTransactionOCService.processReturnTransactionRequest(returnRequest, tranParent.getTransactionId().toString(), transDate,reqJson, OCConstants.LOYALTY_ONLINE_MODE,OCConstants.DR_TO_LTY_EXTRACTION);
			responseJson = gson.toJson(returnResponse);
			logger.info("Return response : "+responseJson);
			if(returnResponse != null){
				updateReturnTransactionStatus(trans, responseJson, returnResponse);
			}
		}catch(Exception e){
			ResponseHeader responseHeader = null;
			if(returnResponse == null){
				responseHeader = new ResponseHeader();
				responseHeader.setRequestDate("");
				responseHeader.setRequestId("");
				responseHeader.setTransactionDate(transDate);
				responseHeader.setTransactionId(""+tranParent.getTransactionId());
			}
			else{
				responseHeader = returnResponse.getHeader();
			}
			status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			try {
				returnResponse = prepareReturnResponse(responseHeader, null, null, null, null, status);
			} catch (BaseServiceException e1) {
				// TODO Auto-generated catch block
				logger.error("Exception ",e);
			}
			responseJson = gson.toJson(returnResponse);
			logger.info("Response = "+responseJson, e);
			updateReturnTransaction(tranParent, returnResponse, userName);
			return null;
		}finally{
			logger.info("Response = "+responseJson);
			//send alert mail for failures
			if(!returnResponse.getStatus().getErrorCode().equalsIgnoreCase("0")) {
				Utility.sendDRToLtyFailureMail(receipt.getDocSID(),receipt.getDocDate(), receipt.getDocTime(),user,OCConstants.LOYALTY_TRANS_TYPE_RETURN,returnResponse.getStatus().getMessage());
			}
			logger.info("Completed Loyalty Return Transaction Rest Service.");
		}
		logger.info("return from DR ended");
		return returnResponse;
		
	}
	private ContactsLoyalty findLoyaltyCardInOCByCustId(String customerId, Long userId) throws Exception {

		ContactsLoyalty contactLoyalty = null;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		List<ContactsLoyalty> loyaltyList = contactsLoyaltyDao.getLoyaltyListByCustId(customerId, userId);

		if (loyaltyList != null && loyaltyList.size() > 0) {
			Iterator<ContactsLoyalty> iterList = loyaltyList.iterator();
			ContactsLoyalty latestLoyalty = null;
			ContactsLoyalty iterLoyalty = null;
			while (iterList.hasNext()) {
				iterLoyalty = iterList.next();
				if (latestLoyalty != null && latestLoyalty.getCreatedDate().after(iterLoyalty.getCreatedDate())) {
					continue;
				}
				latestLoyalty = iterLoyalty;
			}

			contactLoyalty = latestLoyalty;
		}

		return contactLoyalty;
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
	
	public LoyaltyTransaction logEnrollmentTransactionRequest(LoyaltyEnrollRequest requestObject, String jsonRequest, String mode){
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
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ENROLMENT);
			transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
			transaction.setCustomerId(requestObject.getCustomer().getCustomerId().trim());
			//transaction.setDocSID(requestObject.getHeader().getDocSID().trim());
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
	
	private static ContactsLoyaltyStage findDuplicateRequest(LoyaltyEnrollRequest requestObject) {
		//find the request in stage
		ContactsLoyaltyStage loyaltyStage = null;

		try{

			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			String custId = requestObject.getCustomer().getCustomerId() == null ? "" : requestObject.getCustomer().getCustomerId().trim(); 
			String email = requestObject.getCustomer().getEmailAddress() == null ? "" : requestObject.getCustomer().getEmailAddress().trim();
			String phone = requestObject.getCustomer().getPhone() == null ? "" : requestObject.getCustomer().getPhone().trim(); 		
			String card = requestObject.getMembership().getCardNumber() == null ? "" : requestObject.getMembership().getCardNumber().trim(); 
			String userName = requestObject.getUser().getUserName()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUser().getOrganizationId();  

			ContactsLoyaltyStage requestStage = contactsLoyaltyStageDao.findRequest(custId, email, phone, card, userName,
					OCConstants.LOYALTY_SERVICE_TYPE_OC, OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			if(requestStage != null){
				return loyaltyStage;
			}

		}catch(Exception e){
			logger.error("Exception in finding loyalty staging duplicate request...");
		}
		return loyaltyStage;
	}
	
	private static ContactsLoyaltyStage saveRequestInStageTable(LoyaltyEnrollRequest requestObject){

		ContactsLoyaltyStage loyaltyStage = null;
		try{

			ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
			ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
			String custId = requestObject.getCustomer().getCustomerId() == null ? "" : requestObject.getCustomer().getCustomerId().trim(); 
			String email = requestObject.getCustomer().getEmailAddress() == null ? "" : requestObject.getCustomer().getEmailAddress().trim();
			String phone = requestObject.getCustomer().getPhone() == null ? "" : requestObject.getCustomer().getPhone().trim(); 		
			String card = requestObject.getMembership().getCardNumber() == null ? "" : requestObject.getMembership().getCardNumber().trim(); 
			String userName = requestObject.getUser().getUserName()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUser().getOrganizationId();  

			logger.info("saving request in stage table...");
			loyaltyStage = new ContactsLoyaltyStage();
			loyaltyStage.setCustomerId(custId.trim());
			loyaltyStage.setEmailId(email);
			loyaltyStage.setPhoneNumber(phone);
			loyaltyStage.setUserName(userName);
			loyaltyStage.setReqType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			loyaltyStage.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
			loyaltyStage.setStatus(Constants.LOYALTY_STAGE_PENDING);

			contactsLoyaltyStageDaoForDML.saveOrUpdate(loyaltyStage);
		}catch(Exception e){
			logger.error("Exception while saving loyalty request in stage table...", e);
		}
		return loyaltyStage;

	}

	private static void deleteRequestFromStageTable(ContactsLoyaltyStage loyaltyStage) {

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
	
	private LoyaltyTransactionParent createNewTransaction(String type){
		
		LoyaltyTransactionParent tranx  = null; 
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			tranx = new LoyaltyTransactionParent();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			tranx.setCreatedDate(cal);
			tranx.setTransactionType(type);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);

		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}
	
	public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyEnrollResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setJsonResponse(responseJson);
			if (response.getMembership() != null && response.getMembership().getCardNumber() != null &&
					!response.getMembership().getCardNumber().trim().isEmpty()) {
				transaction.setCardNumber(response.getMembership().getCardNumber());
			} else {
				transaction.setCardNumber(response.getMembership() == null ? "" : response.getMembership().getPhoneNumber());
			}
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	
	private void updateReturnTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyReturnTransactionResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_DAO);
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_DAO_FOR_DML);
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
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
	private boolean checkItemsEmpty(List<SkuDetails> itemsList) {

		boolean isEmpty = false;

		if (itemsList == null) {
			isEmpty = true;
			return isEmpty;
		}

		Iterator<SkuDetails> iterItems = itemsList.iterator();
		SkuDetails item = null;
		while (iterItems.hasNext()) {
			item = iterItems.next();
			if (item != null && (item.getItemCategory() == null || item.getItemCategory().trim().isEmpty())
					&& (item.getDepartmentCode() == null || item.getDepartmentCode().trim().isEmpty())
					&& (item.getItemClass() == null || item.getItemClass().trim().isEmpty())
					&& (item.getItemSubClass() == null || item.getItemSubClass().trim().isEmpty())
					&& (item.getDCS() == null || item.getDCS().trim().isEmpty())
					&& (item.getVendorCode() == null || item.getVendorCode().trim().isEmpty())
					&& (item.getSkuNumber() == null || item.getSkuNumber().trim().isEmpty())
					&& (item.getBilledUnitPrice() == null || item.getBilledUnitPrice().trim().isEmpty())
					&& (item.getQuantity() == null || item.getQuantity().trim().isEmpty())) {
				isEmpty =true;
			}
		}

		return isEmpty;
	}
	
	private LoyaltyTransaction findRequestBydocSid(String userName, String docSid) throws Exception {
		LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		return loyaltyTransactionDao.findRequestByDocSid(userName, docSid, OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
	}
	
	private LoyaltyTransaction findReturnRequestBydocSid(String userName, String docSid) throws Exception {
		LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		return loyaltyTransactionDao.findRequestByDocSid(userName, docSid, OCConstants.LOYALTY_TRANS_TYPE_RETURN, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
	}
	
private void updateEnrollmentTransaction(LoyaltyTransactionParent trans, LoyaltyEnrollResponse enrollResponse, String userName) {
		
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			
			if(userName != null){
				trans.setUserName(userName);
			}
			if(enrollResponse.getStatus() != null) {
				trans.setStatus(enrollResponse.getStatus().getStatus());
				trans.setErrorMessage(enrollResponse.getStatus().getMessage());
			}
			if(enrollResponse.getHeader() != null){
				trans.setRequestId(enrollResponse.getHeader().getRequestId());
				trans.setRequestDate(enrollResponse.getHeader().getTransactionDate());
			}
			if(enrollResponse.getMembership() != null) {
					trans.setMembershipNumber(enrollResponse.getMembership().getCardNumber());
					trans.setMobilePhone(enrollResponse.getMembership().getPhoneNumber());
			}
			if(enrollResponse.getMatchedCustomers() != null) {
				//trans.setMobilePhone(enrollResponse.getMatchedCustomers().getPhone());
			}
			//parentDao.saveOrUpdate(trans);
			parentDaoForDML.saveOrUpdate(trans);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
	}
	private void updateIssuanceTransaction(LoyaltyTransactionParent trans, LoyaltyIssuanceResponse responseObject, String userName) {
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
	private void updateIssuanceTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyIssuanceResponse response){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			transaction.setRequestStatus(response.getStatus().getStatus());
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
	
	public void updateRedemptionTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyRedemptionRequest request,Users user){
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
			transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
			 /*Users user= getUser(request.getUSERDETAILS().getUSERNAME(), request.getUSERDETAILS().getORGANISATION(),
					 request.getUSERDETAILS().getTOKEN());*/
			 if(Utility.countryCurrencyMap.get(user.getCountryType()) != null) {
				 
				 responseJson = responseJson.replace(Utility.countryCurrencyMap.get(user.getCountryType()), "");
			 }
			transaction.setJsonResponse(responseJson);
			if(request.getMembership() != null && request.getMembership().getCardNumber() != null 
					&& !request.getMembership().getCardNumber().trim().isEmpty()){
				transaction.setCardNumber(request.getMembership().getCardNumber());
			}
			else{
				transaction.setCardNumber(request.getMembership() == null ? "" : request.getMembership().getPhoneNumber());
			}
			//loyaltyTransactionDao.saveOrUpdate(transaction);
			loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception in updating transaction", e);
		}
	}
	private void updateReturnTransaction(LoyaltyTransactionParent trans, LoyaltyReturnTransactionResponse responseObject, String userName) {
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
	
	private void updateRedemptionTransaction(LoyaltyTransactionParent trans, LoyaltyRedemptionResponse responseObject, String userName) {
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
			logger.error("Exception while updating transaction...", e);
		}
	}
	
	private LoyaltyEnrollResponse prepareEnrollmentResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyEnrollResponse enrollResponse = new LoyaltyEnrollResponse();
		enrollResponse.setHeader(header);
		if(membershipResponse == null){
			membershipResponse = new MembershipResponse();
			membershipResponse.setCardNumber(Constants.STRING_NILL);
			membershipResponse.setCardPin(Constants.STRING_NILL);
			membershipResponse.setExpiry(Constants.STRING_NILL);
			membershipResponse.setPhoneNumber(Constants.STRING_NILL);
			membershipResponse.setTierLevel(Constants.STRING_NILL);
			membershipResponse.setTierName(Constants.STRING_NILL);
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		enrollResponse.setMembership(membershipResponse);
		enrollResponse.setMatchedCustomers(matchedCustomers);
		enrollResponse.setStatus(status);
		return enrollResponse;
	}
	private LoyaltyIssuanceResponse prepareIssuanceResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance, List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyIssuanceResponse issuanceResponse = new LoyaltyIssuanceResponse();
		issuanceResponse.setHeader(header);
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
		issuanceResponse.setMembership(membershipResponse);
		issuanceResponse.setBalances(balances);
		issuanceResponse.setHoldBalance(holdBalance);
		issuanceResponse.setMatchedCustomers(matchedCustomers);
		issuanceResponse.setStatus(status);
		return issuanceResponse;
	}
	private LoyaltyTransaction logIssuanceTransactionRequest(LoyaltyIssuanceRequest requestObject, String jsonRequest, String mode){
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
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ISSUANCE);
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
	
	private LoyaltyTransaction logRedemptionTransactionRequest(LoyaltyRedemptionRequest requestObject, String jsonRequest, String mode){
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
	
	private LoyaltyTransaction logReturnTransactionRequest(LoyaltyReturnTransactionRequest requestObject, String jsonRequest, String mode){
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
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_RETURN);
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
	
	public LoyaltyTransaction findTransactionBy(String userDetail, String docsid, String requestId){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findByDocSIdRequestIdAndType(userDetail, requestId, docsid, OCConstants.LOYALTY_TRANSACTION_REDEMPTION);
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
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
	
	private LoyaltyTransaction findRequestByReqIdAndDocSid(String userName, String requestId, String docSid) throws Exception {
		LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		return loyaltyTransactionDao.findRequestByDocSid(userName, docSid, OCConstants.LOYALTY_TRANSACTION_REDEMPTION, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		//return loyaltyTransactionDao.findByDocSIdRequestIdAndType(userName, null, docSid, OCConstants.LOYALTY_TRANSACTION_REDEMPTION);
	}
	
	private LoyaltyTransaction findRequestBycustSidAndReqId(String userName, String requestId, String custSID) throws Exception {
		LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		return loyaltyTransactionDao.findRequestBycustSidAndReqId(userName, custSID, requestId, OCConstants.LOYALTY_TRANSACTION_ENROLMENT, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
	}
	private LoyaltyReturnTransactionResponse prepareReturnResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<Balance> balances, HoldBalance holdBalance, List<MatchedCustomer> matchedCustomers, Status status) throws BaseServiceException {
		LoyaltyReturnTransactionResponse returnResponse = new LoyaltyReturnTransactionResponse();
		returnResponse.setHeader(header);
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
		returnResponse.setMembership(membershipResponse);
		returnResponse.setBalances(balances);
		returnResponse.setHoldBalance(holdBalance);
		returnResponse.setMatchedCustomers(matchedCustomers);
		returnResponse.setStatus(status);
		return returnResponse;
	}
	private LoyaltyProgram findDefaultMobileBasedProgram(Long userId) throws Exception {
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		LoyaltyProgram loyaltyProgram = loyaltyProgramDao.findMobileBasedProgram(userId);
		if(loyaltyProgram!=null){
			if(loyaltyProgram.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) && 
				loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)){
				return loyaltyProgram;
			}
		}
		return null;
	}
	private LoyaltyProgram findDefaultProgram(Long userId) throws Exception {
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		LoyaltyProgram loyaltyProgram = loyaltyProgramDao.findDefaultProgramByUserId(userId);
		
		return loyaltyProgram;
	}
	public void promoRedeemFromDR(DRToLtyExtractionData DRToLtyExtractionData,Users user) throws Exception{
		
		try {
			
			logger.info("started promo redemption from DR");
			String source =DRToLtyExtractionData.getHeaderFileds().getRequestSource();
			if(source != null && source.equalsIgnoreCase(OCConstants.DR_SOURCE_TYPE_OPTDR)) {
			if(DRToLtyExtractionData.getHeaderFileds().getRequestType() != null && 
					DRToLtyExtractionData.getHeaderFileds().getRequestType().equalsIgnoreCase("Reversal")){
				logger.debug("==reversal receipt === do not perform promo redemption trx");
				return;
			}
				
				List<DRToLoyaltyExtractionItems> items = DRToLtyExtractionData.getItems();
				if(items!= null && !items.isEmpty()) {
					
					int totItems = items.size();
					int totReturnedItems = 0;
					for (DRToLoyaltyExtractionItems drToLoyaltyExtractionItems : items) {
						if(drToLoyaltyExtractionItems.getQuantity() != null && 
								!drToLoyaltyExtractionItems.getQuantity().isEmpty() && 
								Double.parseDouble(drToLoyaltyExtractionItems.getQuantity())<0) totReturnedItems +=1;
					}
					if(totItems == totReturnedItems) return;
				}
			}
			
			
		CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
		ContactsLoyaltyDao contactsLoyaltyDao;
		contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		
		String userName=Constants.STRING_NILL;
		String token =Constants.STRING_NILL;
		String orgId =Constants.STRING_NILL;
		DRToLoyaltyExtractionAuth authUser = DRToLtyExtractionData.getUser();
		DRToLoyaltyExtractionReceipt receipt = DRToLtyExtractionData.getReceipt();
		List<DRToLoyaltyExtractionPromotions> promotionsList = DRToLtyExtractionData.getPromotions();
		DRToLoyaltyExtractionCustomer customer = DRToLtyExtractionData.getCustomer();
		DRToLoyaltyExtractionParams headerParams = DRToLtyExtractionData.getHeaderFileds();
		
		userName=authUser.getUserName();//(dRJsonRequest.getBody().getUserDetails().getUserName()!=null &&	!dRJsonRequest.getBody().getUserDetails().getUserName().isEmpty() ? dRJsonRequest.getBody().getUserDetails().getUserName()		: "");
		token=authUser.getToken();
		orgId=authUser.getOrgID();
		//String isLoyaltyCustomer = headerParams.getIsLoyaltyCustomer();
		//Prepare ItemsInfo
		//ContactsLoyalty contactsLoyalty = customer.getConLoyalty();
		if(promotionsList==null)logger.info("DRToLtyExtractionData.getPromotions() is empty ");
		if(promotionsList!=null && !promotionsList.isEmpty()) {
		Iterator<DRToLoyaltyExtractionPromotions> iterPromosList = promotionsList.iterator();
		while (iterPromosList.hasNext()) {
			try {
				
				DRToLoyaltyExtractionPromotions promotion=iterPromosList.next();
				CouponCodes couponCodeDB = couponCodesDao.findByDocSid(receipt.getDocSID(), promotion.getCouponCode(), user.getUserOrganization().getUserOrgId());
				if(couponCodeDB != null) {
					
					logger.debug("it is already redeemed =="+promotion.getCouponCode());
					continue;
				}
			CouponCodeRedeemReq couponCodeRedeemReq=new CouponCodeRedeemReq();
			CouponCodeRedeemedResponse couponCodeRedeemedResponse = new CouponCodeRedeemedResponse();
			String requestId = OCConstants.DR_LTY_PROMO_REDEMPTION_REQUEST_ID+authUser.getToken()+"_"+System.currentTimeMillis();
			int redeemRewards=0; 
			double totalAmt = 0.0;
			HeaderInfo HEADERINFO = new HeaderInfo();
			CouponCodeInfo COUPONCODEINFO = new CouponCodeInfo();
			PurchaseCouponInfo PURCHASECOUPONINFO = new PurchaseCouponInfo();
			UserDetails USERDETAILS = new UserDetails();
			
			//DRToLoyaltyExtractionPromotions promotion=iterPromosList.next();
			logger.info("itemInfo "+promotion.getItemCodeInfo());
			String itemInfoStr=promotion.getItemCodeInfo();
			itemInfoStr=itemInfoStr.replace("&#045;",Constants.DELIMETER_HIPHEN);
			itemInfoStr=itemInfoStr.replace("\u0026#045;",Constants.DELIMETER_HIPHEN);
			logger.info("itemInfo "+itemInfoStr);
			String[] promoInfo =null;
			if(itemInfoStr.contains(""+Constants.DELIMITER_PIPE)){
				promoInfo = itemInfoStr.split("\\"+Constants.DELIMITER_PIPE);
				for (String info : promoInfo) {
					if(info.contains(Constants.DELIMETER_COMMA)) {
						String[] promoInfoArr = info.split(""+Constants.DELIMETER_COMMA);
						Double rewards =0.0;
						redeemRewards += rewards.intValue();
						if(promoInfoArr.length>=2) totalAmt+=((Double.parseDouble(receipt.getSubtotal())-
								(receipt.getDiscount()!=null && !receipt.getDiscount().isEmpty()?Double.parseDouble(receipt.getDiscount()):Double.parseDouble(promoInfoArr[1]))));
					}else {
					String[] promoInfoArr = info.split(""+Constants.DELIMETER_COLON);
					Double rewards =0.0;
					if(promoInfoArr.length>=4) {
						String[] ratioArr = promoInfoArr[3].split(""+Constants.DELIMETER_HIPHEN);
						Double qtyBought=Double.parseDouble(promoInfoArr[1]);
						Double ratioQty=Double.parseDouble(ratioArr[0]);
						Double ratioReward = Double.parseDouble(ratioArr[1]);
						if(qtyBought<=ratioQty) {
							rewards = ratioReward;
						}else {
							Double div = qtyBought/ratioQty;
							int divNum = div.intValue();
							if(qtyBought%ratioQty==0){
								rewards =(divNum)*ratioReward;
							}else {
							rewards = ratioReward+(divNum)*ratioReward;
							}
						}
						//Double rewards = Double.parseDouble(promoInfoArr[1])*Integer.parseInt(ratioArr[1])/Double.parseDouble(ratioArr[0]);
					}
					redeemRewards += rewards.intValue();
					if(promoInfoArr.length==2) totalAmt+=((Double.parseDouble(receipt.getSubtotal())-
							(receipt.getDiscount()!=null && !receipt.getDiscount().isEmpty()?Double.parseDouble(receipt.getDiscount()):Double.parseDouble(promoInfoArr[1]))));
					}
				}
			}else {
				if(itemInfoStr.contains(Constants.DELIMETER_COMMA)) {
					String[] promoInfoArr = itemInfoStr.split(""+Constants.DELIMETER_COMMA);
					if(promoInfoArr.length>=2) totalAmt+=((Double.parseDouble(receipt.getSubtotal())-
							(receipt.getDiscount()!=null && !receipt.getDiscount().isEmpty()?Double.parseDouble(receipt.getDiscount()):Double.parseDouble(promoInfoArr[1]))));
				}else {
				String[] promoInfoArr = itemInfoStr.split(""+Constants.DELIMETER_COLON);
				Double rewards =0.0;
				if(promoInfoArr.length>=4) {
					String[] ratioArr = promoInfoArr[3].split(""+Constants.DELIMETER_HIPHEN);
					Double qtyBought=Double.parseDouble(promoInfoArr[1]);
					Double ratioQty=Double.parseDouble(ratioArr[0]);
					Double ratioReward = Double.parseDouble(ratioArr[1]);
					if(qtyBought<=ratioQty) {
						rewards = ratioReward;
					}else {
						Double div = qtyBought/ratioQty;
						int divNum = div.intValue();
						if(qtyBought%ratioQty==0){
							rewards =(divNum)*ratioReward;
						}else {
						rewards = ratioReward+(divNum)*ratioReward;
						}
					}
					//Double rewards = Double.parseDouble(promoInfoArr[1])*Integer.parseInt(ratioArr[1])/Double.parseDouble(ratioArr[0]);
				}
				redeemRewards += rewards.intValue();
				if(promoInfoArr.length==2) totalAmt+=((Double.parseDouble(receipt.getSubtotal())-
						(receipt.getDiscount()!=null && !receipt.getDiscount().isEmpty()?Double.parseDouble(receipt.getDiscount()):Double.parseDouble(promoInfoArr[1]))));
				}
			}
			logger.info("redeemRewards "+redeemRewards);
				//HEADERINFO.setENTEREDAMOUNT(); -- for inquiry
				HEADERINFO.setPCFLAG("");
				//HEADERINFO.setRECEIPTAMOUNT("");
				//HEADERINFO.setRECEIPTNUMBER("");
				HEADERINFO.setREQUESTID(requestId);
				HEADERINFO.setSOURCETYPE("");
				HEADERINFO.setSTORENUMBER(receipt.getStore());
				HEADERINFO.setSUBSIDIARYNUMBER(receipt.getSubsidiaryNumber());
				
				USERDETAILS.setUSERNAME(userName);
				USERDETAILS.setORGID(orgId);
				USERDETAILS.setTOKEN(token);
				
				COUPONCODEINFO.setCOUPONNAME("");
				String couponCode=promotion.getCouponCode();
				couponCode=couponCode.replace("&#045;",Constants.DELIMETER_HIPHEN);
				couponCode=couponCode.replace("\u0026#045;",Constants.DELIMETER_HIPHEN);
				COUPONCODEINFO.setCOUPONCODE(couponCode);
				COUPONCODEINFO.setSTORENUMBER(receipt.getStore());
				COUPONCODEINFO.setSUBSIDIARYNUMBER(receipt.getSubsidiaryNumber());
				COUPONCODEINFO.setRECEIPTNUMBER(receipt.getInvcNum());
				//COUPONCODEINFO.setENTEREDAMOUNT(eNTEREDAMOUNT);//for inquiry
				COUPONCODEINFO.setRECEIPTAMOUNT(String.valueOf((Double.parseDouble(receipt.getSubtotal())-
						(receipt.getDiscount()!=null && !receipt.getDiscount().isEmpty()?Double.parseDouble(receipt.getDiscount()):Double.parseDouble("0.0")))));
				COUPONCODEINFO.setCUSTOMERID(customer.getCustomerId().trim());
				COUPONCODEINFO.setPHONE(customer.getPhone());
				COUPONCODEINFO.setEMAIL(customer.getEmailAddress());
				COUPONCODEINFO.setDOCSID(receipt.getDocSID());
				COUPONCODEINFO.setCARDNUMBER(customer.getCardNumber());
				//COUPONCODEINFO.setDISCOUNTAMOUNT(promotion.getDiscountAmount());
				
				//PURCHASECOUPONINFO.setTOTALAMOUNT(receipt.getSubtotal());
				PURCHASECOUPONINFO.setTOTALAMOUNT(totalAmt>0? String.valueOf(totalAmt):receipt.getSubtotal());
				PURCHASECOUPONINFO.setTOTALDISCOUNT(promotion.getDiscountAmount());
				PURCHASECOUPONINFO.setUSEDLOYALTYPOINTS(promotion.getUsedLoyaltyReward());
				couponCodeRedeemReq.setCOUPONCODEINFO(COUPONCODEINFO);
				couponCodeRedeemReq.setHEADERINFO(HEADERINFO);
				couponCodeRedeemReq.setPURCHASECOUPONINFO(PURCHASECOUPONINFO);
				couponCodeRedeemReq.setUSERDETAILS(USERDETAILS);
				Gson gson =new Gson();
				String reqJson="";
				reqJson = gson.toJson(couponCodeRedeemReq, CouponCodeRedeemReq.class);
				logger.info("reqJson "+reqJson);
		
				CouponCodeRedeemedObj couponCodeRedeemedObj = new CouponCodeRedeemedObj();
				couponCodeRedeemedObj.setCOUPONCODEREDEEMREQ(couponCodeRedeemReq);
				couponCodeRedeemedObj.setJsonValue(reqJson);
				CouponCodeRedeemedService couponCodeRedeemedService=(CouponCodeRedeemedService) ServiceLocator.getInstance().getServiceByName(OCConstants.COUPON_CODE_REDEEMED_BUSINESS_SERVICE);
				logTransactionRequest(couponCodeRedeemReq, reqJson, OCConstants.LOYALTY_OFFLINE_MODE);
				couponCodeRedeemedResponse=(CouponCodeRedeemedResponse)couponCodeRedeemedService.processRedeemedRequest(couponCodeRedeemedObj,itemInfoStr,customer.getCardNumber(),redeemRewards);
				String responseJson = gson.toJson(couponCodeRedeemedResponse);
				logger.info("couponCodeRedeemedResponse "+responseJson);
		
				/*if(promotion.getRewardValuecode()!=null && promotion.getUsedLoyaltyReward()!=null && !promotion.getUsedLoyaltyReward().isEmpty()) {
					logger.info("sending normal redemption req");
					LoyaltyRedemptionResponse loyaltyRedemptionResponse =
							prepareRedemptionFromDRRequest( DRToLtyExtractionData, user, promotion.getUsedLoyaltyReward(),promotion.getRewardValuecode(),true);
					String redeemResponseJson = new Gson().toJson(loyaltyRedemptionResponse, LoyaltyRedemptionResponse.class);
					logger.info("promo redemption response : "+redeemResponseJson);
				}*/
				//send alert mail for failures
				if(!couponCodeRedeemedResponse.getCOUPONCODEREDEEMRESPONSE().getSTATUSINFO().getERRORCODE().equalsIgnoreCase("0")) {
					Utility.sendDRToPromoRedeemFailureMail(receipt.getDocSID(),receipt.getDocDate(), receipt.getDocTime(),user,COUPONCODEINFO.getCOUPONCODE(),
							couponCodeRedeemedResponse.getCOUPONCODEREDEEMRESPONSE().getSTATUSINFO().getMESSAGE());
				}

			}catch (NumberFormatException e) {
				logger.error("Exception ",e);
				continue;
			}catch(Exception e) {
				logger.error("Exception ",e);
				continue;
			}
		}
		}
		} catch (BaseServiceException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ",e);
		}
		
		
		
	}
	public PromoTrxLog logTransactionRequest(CouponCodeRedeemReq requestObject, String jsonRequest, String mode){
		PromoTrxLogDao PromoTrxLogDao = null;
		PromoTrxLogDaoForDML PromoTrxLogDaoForDML = null;

		PromoTrxLog transaction = null;
		try {
			PromoTrxLogDao = (PromoTrxLogDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROMOTRXLOG_DAO);
			PromoTrxLogDaoForDML = (PromoTrxLogDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROMOTRXLOG_DAO_FOR_DML);
			transaction = new PromoTrxLog();
			transaction.setJsonRequest(jsonRequest);
			transaction.setRequestId(requestObject.getHEADERINFO().getREQUESTID());
			transaction.setPcFlag(Boolean.valueOf(requestObject.getHEADERINFO().getPCFLAG()));
			transaction.setMode(mode);//online or offline
			transaction.setRequestDate(Calendar.getInstance());
			transaction.setType(OCConstants.LOYALTY_TRANSACTION_ENROLMENT);
			transaction.setUserDetail(requestObject.getUSERDETAILS().getUSERNAME()+"__"+requestObject.getUSERDETAILS().getORGID());
			transaction.setCustomerId(requestObject.getCOUPONCODEINFO().getCUSTOMERID() != null && !requestObject.getCOUPONCODEINFO().getCUSTOMERID().isEmpty()? requestObject.getCOUPONCODEINFO().getCUSTOMERID().trim() : null);
			transaction.setDocSID(requestObject.getCOUPONCODEINFO().getDOCSID() != null && !requestObject.getCOUPONCODEINFO().getDOCSID().isEmpty()? requestObject.getCOUPONCODEINFO().getDOCSID().trim() : null);
			transaction.setStoreNumber(requestObject.getCOUPONCODEINFO().getSTORENUMBER() != null && !requestObject.getCOUPONCODEINFO().getSTORENUMBER().isEmpty()? requestObject.getCOUPONCODEINFO().getSTORENUMBER().trim() : null);
			//PromoTrxLogDao.saveOrUpdate(transaction);
			PromoTrxLogDaoForDML.saveOrUpdate(transaction);
			
		} catch (Exception e) {
			logger.error("Exception in logging transaction", e);
		}
		return transaction;
	}
	public DRToLtyExtractionData convertDRFromOtherSources(DigitalReceipt digitalReceipt, Users user){

		
		
		try {
			Receipt receipt = digitalReceipt.getBody().getReceipt();
			DROptCultureDetails ocDetails = digitalReceipt.getOptcultureDetails();
			
			if((receipt.getBillToPhone1() != null && !receipt.getBillToPhone1().trim().isEmpty())) {
				logger.info("no card number find with mobile based prog");
				String customerphoneParse = Utility.phoneParse(receipt.getBillToPhone1().trim(),
						user != null ? user.getUserOrganization() : null);
				if(customerphoneParse != null) {
					
					
					receipt.setBillToPhone1(customerphoneParse);
				}
			}
			
			DRToLoyaltyExtractionReceipt DRReceipt = new DRToLoyaltyExtractionReceipt(receipt.getDocDate(), receipt.getInvcNum(),
					receipt.getDocTime(), receipt.getDocSID(), receipt.getStore(), receipt.getSubsidiaryNumber(), receipt.getTotal(), 
					receipt.getSubtotal(), null, null,null,null,null,null,receipt.getRefDocSID(),receipt.getRefReceipt(), receipt.getRefStoreCode(), receipt.getSubsidiaryNumber(),receipt.getDiscount());
			DRReceipt.setBillToInfo1(receipt.getBillToInfo1());
			
			DRToLoyaltyExtractionCustomer customer = new DRToLoyaltyExtractionCustomer(receipt.getBillToCustSID(), null, receipt.getBillToFName(), 
					receipt.getBillToLName(), receipt.getBillToPhone1(), receipt.getBillToEMail(), receipt.getBillToAddr1(), 
					null, receipt.getBillToAddr2(), receipt.getBillToAddr3(), receipt.getBillToZip(), null, receipt.getBillToUDF1(), 
					receipt.getBillToUDF2(), null, null, null, receipt.getBillToInfo2(), null);
			DRToLoyaltyExtractionParams headerFileds = new DRToLoyaltyExtractionParams(digitalReceipt.getHead().getEnrollCustomer(),digitalReceipt
					.getHead().getIsLoyaltyCustomer(),digitalReceipt.getHead().getRequestSource(), digitalReceipt.getHead().getRequestType());
			
			
			String userName=digitalReceipt.getHead().getUser().getUserName();
			String token =digitalReceipt.getHead().getUser().getToken();
			String orgId =digitalReceipt.getHead().getUser().getOrganizationId();
			
			
			DRToLoyaltyExtractionAuth authUser = new DRToLoyaltyExtractionAuth(userName,orgId, token);
			
			List<org.mq.optculture.model.DR.DRItem> DRitems = digitalReceipt.getBody().getItems();
			List<DRToLoyaltyExtractionItems> items = new ArrayList<DRToLoyaltyExtractionItems>();
			Map<String, DRToLoyaltyExtractionItems> distinctskus = new HashMap<String, DRToLoyaltyExtractionItems>();
			
			for (org.mq.optculture.model.DR.DRItem item : DRitems) {
				double itemQty=(Double.valueOf(item.getQty()));
				DRToLoyaltyExtractionItems drItem = new DRToLoyaltyExtractionItems(item.getItemCategory() != null && 
						!item.getItemCategory().isEmpty() ? item.getItemCategory() : item.getDCSName(), 
						(item.getDepartmentCode()!=null && !item.getDepartmentCode().isEmpty()) ? item.getDepartmentCode() :"", 
						(item.getItemClass()!=null && !item.getItemClass().isEmpty()) ? item.getItemClass() : "", 
						(item.getItemSubClass()!=null && !item.getItemSubClass().isEmpty()) ? item.getItemSubClass() : "", 
						(item.getDCS()!=null && !item.getDCS().isEmpty()) ? item.getDCS() :"", 
						(item.getVendorCode()!=null && !item.getVendorCode().isEmpty()) ? item.getVendorCode() : "",
						(item.getUPC()!=null && !item.getUPC().isEmpty()) ? item.getUPC() : "", 
						item.getDocItemPrc()==null || item.getDocItemPrc().isEmpty() ? item.getInvcItemPrc() : item.getDocItemPrc(), 
						item.getQty(), (item.getTax()!=null && !item.getTax().isEmpty()) ? item.getTax() : "", 
						(item.getDocItemDiscAmt()!=null && !item.getDocItemDiscAmt().isEmpty()) ? item.getDocItemDiscAmt() :"", 
						(item.getDepartmentName()!=null && !item.getDepartmentName().isEmpty()) ? item.getDepartmentName() :"", 
						(item.getItemClassName()!=null && !item.getItemClassName().isEmpty()) ? item.getItemClassName() : "", 
						(item.getItemSubClassName()!=null && !item.getItemSubClassName().isEmpty()) ? item.getItemSubClassName() : "", 
						(item.getVendorName()!=null && !item.getVendorName().isEmpty()) ? item.getVendorName() :"", 
						(item.getItemSID()!=null && !item.getItemSID().isEmpty()) ? item.getItemSID() :"",
						(item.getRefStoreCode()!=null && !item.getRefStoreCode().isEmpty()) ? item.getRefStoreCode() :"", 
						(item.getRefSubsidiaryNumber()!=null && !item.getRefSubsidiaryNumber().isEmpty()) ? item.getRefSubsidiaryNumber() :"",
						(item.getRefReceipt()!=null && !item.getRefReceipt().isEmpty()) ? item.getRefReceipt() :"",
						(item.getRefDocSID()!=null && !item.getRefDocSID().isEmpty()) ? item.getRefDocSID() :"",
						(item.getDiscountReason()!=null && !item.getDiscountReason().isEmpty() ? item.getDiscountReason():""));
				
				drItem.setItemType(item.getItemType() != null && !item.getItemType().isEmpty() ? item.getItemType() :"");
				
				drItem.setDocItemOrigPrc(item.getDocItemOrigPrc()!=null && !item.getDocItemOrigPrc().isEmpty() ? item.getDocItemOrigPrc() :"");
				drItem.setInvcItemPrc(item.getInvcItemPrc()!=null && !item.getInvcItemPrc().isEmpty() ? item.getInvcItemPrc() :"");
				drItem.setItemPromoDiscount(item.getItemPromoDiscount() != null && !item.getItemPromoDiscount().isEmpty() ? item.getItemPromoDiscount() : null);
				drItem.setDiscount(item.getDocItemDiscAmt() !=null && !item.getDocItemDiscAmt().isEmpty() ? item.getDocItemDiscAmt() :
					   (item.getDocItemDisc()!=null && !item.getDocItemDisc().isEmpty()?item.getDocItemDisc():""));
				if(receipt.getReceiptType()!=null && !receipt.getReceiptType().isEmpty() && receipt.getReceiptType().equalsIgnoreCase("2")&& itemQty>0)
				drItem.setQuantity(String.valueOf((itemQty*=-1)));
				if(item.getItemType() != null && (item.getItemType().equalsIgnoreCase("NonInvnPromo") || (item.getItemType().equalsIgnoreCase("NonInvnRedeemReversal")))){
					drItem.setNonInventory(true);
					
				}
				items.add(drItem);
				distinctskus.put(item.getItemSID(),drItem);
			}
			
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			
			PropertyDescriptor cdi = null;
			String cardInfo = user.getCardInfo();
			String cardInfoObjStr=Constants.STRING_NILL;
			if(cardInfo!=null) {
				cdi = new PropertyDescriptor(cardInfo, receipt.getClass());
				Object cardInfoObj = cdi.getReadMethod().invoke(receipt);
				if(cardInfoObj != null && !cardInfoObj.toString().isEmpty() ) {
					cardInfoObjStr = cardInfoObj.toString();
				}
			}
			logger.info("cardInfoObjStr "+cardInfoObjStr);
			ContactsLoyalty contactsLoyalty = null;
			boolean mobileBasedEnroll=false; 
			boolean perkBased = false;
			LoyaltyProgramDao prgmDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			
			List<LoyaltyProgram> listOfPrgms = prgmDao.getAllProgramsListByUserId(user.getUserId());
			LoyaltyProgram defaultLoyaltyProgram = null;
			LoyaltyProgram perkProg = null;
			if(listOfPrgms!=  null) {
				
				for(LoyaltyProgram prgm : listOfPrgms) {
					
					if(prgm.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED)) continue;
					if(prgm.getDefaultFlag()== OCConstants.FLAG_YES ) {
						defaultLoyaltyProgram = prgm;
						
					}
					if(prgm.getRewardType() != null && prgm.getRewardType().equals(OCConstants.REWARD_TYPE_PERK) )
						perkProg = prgm;
					
				}
			}
			if(cardInfoObjStr == null || cardInfoObjStr.isEmpty()) {
				//contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),receipt.getBillToPhone1());
				if(perkProg != null) {
					String phone =  receipt.getBillToPhone1();
					if(phone != null && !phone.isEmpty() && perkProg.getUniqueMobileFlag()==OCConstants.FLAG_YES) {
						
						String phoneParse = Utility.phoneParse(phone.trim(),
								user != null ? user.getUserOrganization() : null);
						contactsLoyalty = contactsLoyaltyDao.getLoyaltyByPrgmAndPhone(user,perkProg.getProgramId(), phoneParse);
						if(contactsLoyalty != null ) {
							perkBased = true;
						}
						
					}
					if(contactsLoyalty == null && perkProg.getUniqueEmailFlag()==OCConstants.FLAG_YES && 
							receipt.getBillToEMail()!= null && !receipt.getBillToEMail().isEmpty() ) {
						
						contactsLoyalty = contactsLoyaltyDao.findMembershipByEmailInCl( receipt.getBillToEMail(), user.getUserId(),perkProg.getProgramId());
						if(contactsLoyalty != null ) {
							perkBased = true;
						}
					}
				}
				if(contactsLoyalty == null && defaultLoyaltyProgram !=null) {
					if(defaultLoyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
						
						if((receipt.getBillToPhone1() != null && !receipt.getBillToPhone1().trim().isEmpty())) {
							logger.info("no card number find with mobile based prog");
							contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),receipt.getBillToPhone1());
							if(contactsLoyalty==null);mobileBasedEnroll=true;
						}else{
							mobileBasedEnroll=true;
						}
					}
				}
			}else {
				logger.info("find with card number in else");
				//look up with card number
			    contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),cardInfoObjStr);
			    if(contactsLoyalty!=null && perkProg!=null) {
			    	logger.info("contatcs Loyalty not null && perk not null");
			    	if(contactsLoyalty.getProgramId()==perkProg.getProgramId()) {
			    		perkBased = true;
			    		logger.info("perk basd program ?>>>>"+perkBased);
			    	}
			    }
			    
			}
			if(contactsLoyalty==null) {
				String custID = receipt.getBillToCustSID();
				if(custID!=null && !custID.isEmpty()){
					contactsLoyalty = findLoyaltyCardInOCByCustId(custID, user.getUserId());
				}
			}
			customer.setCardNumber(cardInfoObjStr);
			customer.setConLoyalty(contactsLoyalty);
			logger.info("contactsLoyalty "+contactsLoyalty);
			DRReceipt.setReceiptType(receipt.getReceiptType());
			DRToLtyExtractionData DRToLtyExtractionData = new DRToLtyExtractionData(customer, null, authUser, headerFileds, DRReceipt);
			DRToLtyExtractionData.setPerkBased(perkBased);
			
			if(contactsLoyalty!=null)DRToLtyExtractionData.getHeaderFileds().setIsLoyaltyCustomer("Y");
			
			//cc:qty:disc:1.0-2 (ratio)
			double promoDiscount = 0.0;
			if(ocDetails!=null) {
			List<OCPromotions> promotions = ocDetails.getPromotions();
			Map<String, String> distinctPromos = new HashMap<String, String>();
			if(promotions!=null) {
			for(OCPromotions ocpromotion : promotions) {
				String itemInfo = distinctPromos.get(ocpromotion.getCouponCode());
				String rewardRatio="";
				if(ocpromotion.getRewardRatio()!=null && !ocpromotion.getRewardRatio().isEmpty()) {
					rewardRatio = ocpromotion.getRewardRatio();
				}
				if(itemInfo != null && !itemInfo.isEmpty()) itemInfo += Constants.DELIMITER_PIPE;
				if(ocpromotion.getDiscountType().equalsIgnoreCase(OCConstants.DR_PROMO_DISCOUNT_TYPE_ITEM)) {
					
				itemInfo = (itemInfo == null ?ocpromotion.getCouponCode()+""+Constants.DELIMETER_COLON+ocpromotion.getQuantityDiscounted()+
						""+Constants.DELIMETER_COLON+ocpromotion.getItemDiscount()+Constants.DELIMETER_COLON+rewardRatio:
					    itemInfo+ocpromotion.getCouponCode()+""+Constants.DELIMETER_COLON+ocpromotion.getQuantityDiscounted()+
					    ""+Constants.DELIMETER_COLON+ocpromotion.getItemDiscount()+Constants.DELIMETER_COLON+rewardRatio);
				for(DRToLoyaltyExtractionItems drItem : items) {
					
					if(drItem.getItemSID().equalsIgnoreCase(ocpromotion.getItemCode())){
						 
						if(drItem.isNonInventory() && drItem.getItemType() != null && 
								drItem.getItemType().equalsIgnoreCase("NonInvnPromo")){
							
						}
						else {
							if(drItem.getDiscount_reason()!=null && drItem.getDiscount_reason().contains(ocpromotion.getCouponCode())) {
								drItem.setItemNote(itemInfo!=null && !itemInfo.isEmpty()?itemInfo:"");
							}
						}
					}
				}
				}else {
					if(ocpromotion.getItemCode() != null && !ocpromotion.getItemCode().isEmpty()){
						
						for(DRToLoyaltyExtractionItems drItem : items) {
							
							if(drItem.getItemSID().equalsIgnoreCase(ocpromotion.getItemCode())){
								
								if(drItem.isNonInventory() && drItem.getItemType() != null && 
										drItem.getItemType().equalsIgnoreCase("NonInvnPromo")){
									
									promoDiscount += Double.parseDouble(ocpromotion.getDiscountAmount());
								}
							}
						}
					}
					itemInfo = (itemInfo == null ?ocpromotion.getCouponCode()+""+Constants.DELIMETER_COLON+ocpromotion.getDiscountAmount():
					    itemInfo+ocpromotion.getCouponCode()+""+Constants.DELIMETER_COLON+ocpromotion.getDiscountAmount());
				}
				distinctPromos.put(ocpromotion.getCouponCode(), itemInfo);
			}
			}
			if(distinctPromos.size() > 0 && (receipt.getReceiptType()!=null?!(receipt.getReceiptType().equalsIgnoreCase("Return")
					||receipt.getReceiptType().equalsIgnoreCase("2")):true)){
				List<DRToLoyaltyExtractionPromotions> promotionsLst = new ArrayList<DRToLoyaltyExtractionPromotions>();
				for (String promo : distinctPromos.keySet()) {
					
					
					Double discountAmount = 0.0;
					String discountType = Constants.STRING_NILL;
					
					String itemINfo = distinctPromos.get(promo);
					if(itemINfo.contains(""+Constants.DELIMITER_PIPE)){
						String[] itemInfoStr = itemINfo.split("\\"+Constants.DELIMITER_PIPE);
						for (String itemInfo : itemInfoStr) {
							String[] itemInfoArr = itemInfo.split(""+Constants.DELIMETER_COLON);
							discountAmount += (itemInfoArr.length>2?(Double.parseDouble(itemInfoArr[2]) * Double.parseDouble(itemInfoArr[1])):(Double.parseDouble(itemInfoArr[1])));
							discountType=itemInfoArr.length>2?OCConstants.DR_PROMO_DISCOUNT_TYPE_ITEM:OCConstants.DR_PROMO_DISCOUNT_TYPE_RECEIPT;
						}
						
					}else{
							if(itemINfo.contains(""+Constants.DELIMETER_COLON)) {
								String[] itemInfoArr = itemINfo.split(""+Constants.DELIMETER_COLON);
								discountAmount += (itemInfoArr.length>2?(Double.parseDouble(itemInfoArr[2]) * Double.parseDouble(itemInfoArr[1])):(Double.parseDouble(itemInfoArr[1])));
								discountType=itemInfoArr.length>2?OCConstants.DR_PROMO_DISCOUNT_TYPE_ITEM:OCConstants.DR_PROMO_DISCOUNT_TYPE_RECEIPT;
							}
					}
					logger.info("discountAmount "+discountAmount);
					DRToLoyaltyExtractionPromotions promotion = new DRToLoyaltyExtractionPromotions();
					//promotion.setNonInvnPromoItemsDiscount(promotion+);
					promotion.setCouponCode(promo);
					promotion.setDiscountAmount(Utility.truncateUptoTwoDecimal(discountAmount));
					promotion.setItemCodeInfo(itemINfo);
					promotion.setDiscountType(discountType);
					
					
					promotionsLst.add(promotion);						
				}
				DRToLtyExtractionData.setPromotions(promotionsLst);
			}				
				DRToLoyaltyExtractionRedemption redemptionDetails = new DRToLoyaltyExtractionRedemption();
				DRToLtyExtractionData.setOTPCode(ocDetails != null && ocDetails.getLoyaltyRedeem() != null && 
						ocDetails.getLoyaltyRedeem().getOTPCode() != null ? ocDetails.getLoyaltyRedeem().getOTPCode() : Constants.STRING_NILL);

				if(ocDetails.getLoyaltyRedeem()!=null && (receipt.getReceiptType()!=null?!(receipt.getReceiptType().equalsIgnoreCase("Return")
						||receipt.getReceiptType().equalsIgnoreCase("2")):true)) {
					String redemptionAmount = ocDetails.getLoyaltyRedeem().getDiscountAmount();
					if(receipt.getDiscountReasonName() != null && 
							(receipt.getDiscountReasonName().equalsIgnoreCase("OC-R,OC Loyalty Balance") || 
									receipt.getDiscountReasonName().equalsIgnoreCase("Discount") )){
						logger.debug("==in disc block==");
						if(receipt.getDiscount() != null && !receipt.getDiscount().isEmpty() && 
								Double.parseDouble(receipt.getDiscount()) <=0 )redemptionDetails.setDiscountSpreaded(true);
						
						redemptionDetails.setRedemptionAsDiscount(redemptionAmount);
					}else{
						
						redemptionDetails.setRedemptionAmount(redemptionAmount);
					}
				}
				if(ocDetails.getLoyaltyRedeemReversal()!=null) {
					
				String creditRedeemedAmount = ocDetails.getLoyaltyRedeemReversal();
				redemptionDetails.setLoyaltyRedeemReversal(creditRedeemedAmount);
				}else if(ocDetails.getLoyaltyRedeem()!=null && ocDetails.getLoyaltyRedeem().getDiscountAmount()!=null) {
					String creditRedeemedAmount = ocDetails.getLoyaltyRedeem().getDiscountAmount();
					redemptionDetails.setLoyaltyRedeemReversal(creditRedeemedAmount);
				}
				DRToLtyExtractionData.setRedemptionDetails(redemptionDetails);
			}
			DRToLtyExtractionData.setItems(items);
			DRToLtyExtractionData.setMobileBasedEnroll(mobileBasedEnroll);
			DRToLtyExtractionData.setNonInvnPromoItemsDiscount(promoDiscount);
			return DRToLtyExtractionData;
			
			}catch(Exception e) {
				logger.error("Exception ",e);
				return null;
			}
	}
	public void processGiftIssuance(Users user,DRToLtyExtractionData DRToLtyExtractionData) {//Gift card flow - APP-4603
		
		List<DRToLoyaltyExtractionItems> giftCardItemsList = new ArrayList<DRToLoyaltyExtractionItems>();
		DRToLoyaltyExtractionReceipt receipt = DRToLtyExtractionData.getReceipt();
		DRToLoyaltyExtractionCustomer customer = DRToLtyExtractionData.getCustomer();
		List<GiftCardSkus> mappedSkusList = null;
		try {
			GiftCardSkusDao giftSkusDao = (GiftCardSkusDao) ServiceLocator.getInstance()
					.getDAOByName("giftCardSkusDao");
			mappedSkusList = giftSkusDao.findListByUserId(user.getUserId());
			logger.info("mappedSkus List : "+mappedSkusList.size());
		}catch (Exception e) {}
		
		List<DRToLoyaltyExtractionItems> items = DRToLtyExtractionData.getItems();
		Iterator<DRToLoyaltyExtractionItems> iterItems = items.iterator();
		DRToLoyaltyExtractionItems item= null;
		
		if(mappedSkusList!=null && mappedSkusList.size()>0) {
			while (iterItems.hasNext()) {
				item = iterItems.next();
				if(item.isNonInventory()) continue;
				if(item.getQuantity() !=null && !item.getQuantity().trim().isEmpty() && (Double.valueOf(item.getQuantity())>0)){
					for(GiftCardSkus giftCardSku : mappedSkusList) {
						
						if(item.getItemSID()!=null && !item.getItemSID().isEmpty() 
								&& giftCardSku.getSkuCode()!=null && !giftCardSku.getSkuCode().isEmpty()) {
							
							if(giftCardSku.getSkuCode().equals(item.getItemSID())) {
								giftCardItemsList.add(item);
								logger.info("gift skus code : "+item.getItemSID());
								break;
							}
							
						}
						
					}
					
				}
			}
		}
			
		logger.info("giftCardItemsList : "+giftCardItemsList.size());
		if(giftCardItemsList!=null && giftCardItemsList.size()>0) {
			AsyncGiftIssuance asyncGiftIssuanceThraed = new AsyncGiftIssuance(giftCardItemsList, user,customer,receipt);
			asyncGiftIssuanceThraed.run();
		}	
	}
}
