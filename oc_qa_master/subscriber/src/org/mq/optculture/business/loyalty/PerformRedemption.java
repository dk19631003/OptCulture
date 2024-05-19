package org.mq.optculture.business.loyalty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CouponCodesDaoForDML;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.helper.CouponCodeProcessHelper;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemReq;
import org.mq.optculture.model.ocloyalty.AdditionalInfo;
import org.mq.optculture.model.ocloyalty.Amount;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.Customer;
import org.mq.optculture.model.ocloyalty.Discounts;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.OTPRedeemLimit;
import org.mq.optculture.model.ocloyalty.Promotion;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class PerformRedemption extends Thread{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Coupons coupon;
	private CouponCodes redeemedCoupon;
	private Users user;
	private CouponCodeRedeemReq couponCodeRedeemReq;
	private Integer redeemReward = null;
	private double totDiscount;
	
	public PerformRedemption(){
		
		
	}
	
	public PerformRedemption(Coupons coupon, CouponCodes redeemedCoupon, Users user, CouponCodeRedeemReq couponCodeRedeemReq, int redeemReward, double totDiscount){
		
		this.coupon = coupon;
		this.redeemedCoupon = redeemedCoupon;
		this.user = user;
		this.couponCodeRedeemReq = couponCodeRedeemReq;
		this.redeemReward = redeemReward;
		this.totDiscount = totDiscount;
	}
	public PerformRedemption(Coupons coupon, CouponCodes redeemedCoupon, Users user, CouponCodeRedeemReq couponCodeRedeemReq, double totDiscount){
		
		this.coupon = coupon;
		this.redeemedCoupon = redeemedCoupon;
		this.user = user;
		this.couponCodeRedeemReq = couponCodeRedeemReq;
		this.totDiscount = totDiscount; 
	}
	
	@Override
	public void run() {
		
		
		performLoyaltyRedemption();		
		
	}
	
	public void performLoyaltyRedemption() {
		try {
			logger.debug("====entered PerformRedemption===="+coupon+" redeemedCoupon==="+redeemedCoupon+" getRequiredLoyltyPoits ==="+coupon.getRequiredLoyltyPoits()+" getMultiplierValue ==="+totDiscount);
			// TODO Auto-generated method stub
			if(!user.isIgnorePointsRedemption()) {
				logger.debug("===nothing to do ===");
				return;
			}
			if(OCConstants.LOYALTY_SERVICE_TYPE_SB.equals(user.getloyaltyServicetype())) {
				
				logger.debug("===nothing to do as the request is of SB user ===");
				return;
			}
			/*if(coupon == null || 
					coupon.getRequiredLoyltyPoits() == null || 
					coupon.getRequiredLoyltyPoits()<=0 ){
				
				logger.debug("===nothing to do ===");
				return;
			}*/
			String cardNUmber = couponCodeRedeemReq.getCOUPONCODEINFO().getCARDNUMBER();
			String email = couponCodeRedeemReq.getCOUPONCODEINFO().getEMAIL();
			String phone = couponCodeRedeemReq.getCOUPONCODEINFO().getPHONE();
			ContactsLoyalty membership = null;
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			CouponDiscountGenerateDao coupDiscGenDao = (CouponDiscountGenerateDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);

			if(cardNUmber == null || cardNUmber.trim().isEmpty()){
				logger.debug("===no card# is given ===");
				String custSID = couponCodeRedeemReq.getCOUPONCODEINFO().getCUSTOMERID();
				if(custSID != null && !custSID.isEmpty()) {
					
					membership = contactsLoyaltyDao.getContactsLoyaltyByCustId(custSID, user.getUserId());
					if(membership == null){
						logger.debug("===nothing to do - no membershipfound for==="+custSID);
						return;
						
					}
					couponCodeRedeemReq.getCOUPONCODEINFO().setCARDNUMBER(membership.getCardNumber());
					
					
				}else if((email != null && !email.isEmpty()) || (phone != null && !phone.isEmpty()) ){
					
					membership = contactsLoyaltyDao.findBy(user, email, phone);
				}
				
			}else {
				
				membership = contactsLoyaltyDao.getContactsLoyaltyByCardId(cardNUmber, user.getUserId());//CustId(cardNUmber, user.getUserId());
			}
			
			Double reqMultiplierValue = null; //APP-3667
			if(coupon.getMultiplierValue()!=null) {

				if(totDiscount>0 && user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA)) {
					reqMultiplierValue = totDiscount * coupon.getMultiplierValue();
				
				}else if(totDiscount==0 && !user.getCountryType().equals(Constants.SMS_COUNTRY_INDIA)) {
					reqMultiplierValue = Double.parseDouble(couponCodeRedeemReq.getPURCHASECOUPONINFO().getTOTALDISCOUNT())*coupon.getMultiplierValue();
					
				}
				logger.info("reqMultiplierValue && totDiscount>>>>>>"+reqMultiplierValue+" "+totDiscount);
			}
			 
			LoyaltyRedemptionRequest loyaltyRedemptionRequest = prepareRedemptionRequest(couponCodeRedeemReq, membership, reqMultiplierValue);
			if(loyaltyRedemptionRequest == null){
				
				logger.debug("===request isn't prepared ===");
				return;
			}
			try {
					
					LoyaltyTransactionParent tranParent = createNewTransaction(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION); 
					Date date = tranParent.getCreatedDate().getTime();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
					String transDate = df.format(date);
					
					Status status = null;
					String responseJson = "";
					String requestJson="";
					Gson gson = new Gson();
					String userName = null;
					
					ResponseHeader responseHeader = new ResponseHeader();
					responseHeader.setRequestDate("");
					responseHeader.setRequestId("");
					responseHeader.setTransactionDate(transDate);
					responseHeader.setTransactionId(""+tranParent.getTransactionId());
					
					LoyaltyRedemptionResponse redemptionResponse = null;
					
					try{
						requestJson = gson.toJson(loyaltyRedemptionRequest, LoyaltyRedemptionRequest.class);
					}catch(Exception e){
						status = new Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						responseJson = gson.toJson(redemptionResponse);
						updateRedemptionTransaction(tranParent, redemptionResponse, null);
						logger.info("Response = "+responseJson);
						logger.error("Exception in parsing request json ...",e);
						return ;
					}
					
					if(loyaltyRedemptionRequest.getHeader() == null){
						status = new Status("1004", PropertyUtil.getErrorMessage(1004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						responseJson = gson.toJson(redemptionResponse);
						updateRedemptionTransaction(tranParent, redemptionResponse, null);
						logger.info("Response = "+responseJson);
						return ;
					}
					if(loyaltyRedemptionRequest.getHeader().getRequestId() == null || loyaltyRedemptionRequest.getHeader().getRequestId().trim().isEmpty() ||
							loyaltyRedemptionRequest.getHeader().getRequestDate() == null || loyaltyRedemptionRequest.getHeader().getRequestDate().trim().isEmpty()){
						status = new Status("111553", PropertyUtil.getErrorMessage(111553, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						responseJson = gson.toJson(redemptionResponse);
						updateRedemptionTransaction(tranParent, redemptionResponse, null);
						logger.info("Response = "+responseJson);
						return ;
					}
					
					if(loyaltyRedemptionRequest.getMembership() == null){
						responseHeader.setRequestId(loyaltyRedemptionRequest.getHeader().getRequestId());
						responseHeader.setRequestDate(loyaltyRedemptionRequest.getHeader().getRequestDate());
						status = new Status("101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						responseJson = gson.toJson(redemptionResponse);
						updateRedemptionTransaction(tranParent, redemptionResponse, null);
						logger.info("Response = "+responseJson);
						return ;
					}
					if(loyaltyRedemptionRequest.getUser() == null){
						responseHeader.setRequestId(loyaltyRedemptionRequest.getHeader().getRequestId());
						responseHeader.setRequestDate(loyaltyRedemptionRequest.getHeader().getRequestDate());
						status = new Status("101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						responseJson = gson.toJson(redemptionResponse);
						updateRedemptionTransaction(tranParent, redemptionResponse, null);
						logger.info("Response = "+responseJson);
						return ;
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
						return ;
					}
					userName = loyaltyRedemptionRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
							          loyaltyRedemptionRequest.getUser().getOrganizationId();
					
					LoyaltyTransaction trans = findRequestByReqIdAndDocSid(loyaltyRedemptionRequest.getUser().getUserName() + "__" +
							loyaltyRedemptionRequest.getUser().getOrganizationId(), loyaltyRedemptionRequest.getHeader().getRequestId().trim(), 
							loyaltyRedemptionRequest.getHeader().getDocSID().trim());
					String reqJson = gson.toJson(loyaltyRedemptionRequest, LoyaltyRedemptionRequest.class);
					if(trans != null){
						responseHeader.setRequestId(loyaltyRedemptionRequest.getHeader().getRequestId());
						responseHeader.setRequestDate(loyaltyRedemptionRequest.getHeader().getRequestDate());
						status = new Status("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						redemptionResponse = prepareRedemptionResponse(responseHeader, null, null, null, null, null, status);
						responseJson = gson.toJson(redemptionResponse);
						logger.info("Response = "+responseJson);
						updateRedemptionTransaction(tranParent, redemptionResponse, null);
						return ;
					}
					if(trans==null){
						trans = logRedemptionTransactionRequest(loyaltyRedemptionRequest, reqJson,"online");
					}
				
				LoyaltyRedemptionOCService loyaltyRedemptionService = (LoyaltyRedemptionOCService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_REDEMPTION_OC_BUSINESS_SERVICE);
				redemptionResponse = loyaltyRedemptionService.processRedemptionRequest(loyaltyRedemptionRequest, OCConstants.LOYALTY_ONLINE_MODE, 
									 trans.getId()+"", transDate,OCConstants.DR_TO_LTY_EXTRACTION);
				responseJson = gson.toJson(redemptionResponse);
				logger.info("Redemption response : "+responseJson);
				updateTransactionStatus(trans, responseJson, redemptionResponse);
				
			} catch (Exception e) {
				logger.error("==Exception===", e);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("==Exception===", e);
		}
		
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
	
	
	private LoyaltyTransaction findRequestByReqIdAndDocSid(String userName, String requestId, String docSid) throws Exception {
		LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		return loyaltyTransactionDao.findRequestByReqIdAndDocSid(userName, requestId, docSid, OCConstants.LOYALTY_TRANSACTION_REDEMPTION, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
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
	private void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyRedemptionResponse response){
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
	
	
	public LoyaltyRedemptionRequest prepareRedemptionRequest(CouponCodeRedeemReq couponCodeRedeemReq, ContactsLoyalty member, Double reqMultiplierValue){
		try {
			logger.debug("====entered prepareRedemptionRequest====");
			LoyaltyRedemptionRequest loyaltyRedemptionRequest= new LoyaltyRedemptionRequest();
			 
			
			 
			 String requestId=couponCodeRedeemReq.getCOUPONCODEINFO().getDOCSID()+couponCodeRedeemReq.getHEADERINFO().getREQUESTID() != null ? ("_"+couponCodeRedeemReq.getHEADERINFO().getREQUESTID()):
				 MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_SB_DATETIME);
			
			 RequestHeader header = new RequestHeader();
			 header.setRequestId(requestId);
			 header.setRequestDate(MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR));
			 header.setDocSID(couponCodeRedeemReq.getCOUPONCODEINFO().getDOCSID());
			 header.setReceiptNumber(couponCodeRedeemReq.getCOUPONCODEINFO().getRECEIPTNUMBER());
			 header.setStoreNumber(couponCodeRedeemReq.getCOUPONCODEINFO().getSTORENUMBER());
			 header.setSubsidiaryNumber(couponCodeRedeemReq.getCOUPONCODEINFO().getSUBSIDIARYNUMBER());
			 header.setSourceType(couponCodeRedeemReq.getHEADERINFO().getSOURCETYPE());
			 
			 MembershipRequest membership = new MembershipRequest();
			 membership.setCardNumber(couponCodeRedeemReq.getCOUPONCODEINFO().getCARDNUMBER());//need not be a card (consider mobile-based enrollments wont happen thru )
			  if(member  != null && member.getMembershipType().equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
				  membership.setPhoneNumber(member.getCardNumber());
				  membership.setCardNumber("");
			  }
			 Customer customer = new Customer();
			 customer.setCustomerId(couponCodeRedeemReq.getCOUPONCODEINFO().getCUSTOMERID());
			 customer.setEmailAddress(couponCodeRedeemReq.getCOUPONCODEINFO().getEMAIL());
			 customer.setPhone(couponCodeRedeemReq.getCOUPONCODEINFO().getPHONE());

			 Amount amount = new Amount();
			 amount.setEnteredValue((redeemReward == null || redeemReward <= 0 ) ? (coupon.getRequiredLoyltyPoits()!=null ? coupon.getRequiredLoyltyPoits()+Constants.STRING_NILL : reqMultiplierValue!=null && reqMultiplierValue>0 ? reqMultiplierValue.longValue()+Constants.STRING_NILL : Constants.STRING_NILL) : redeemReward+Constants.STRING_NILL);
			 amount.setValueCode(coupon.getValueCode() == null ? OCConstants.LOYALTY_POINTS : coupon.getValueCode());
			 amount.setType(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE);
			 
			 
			 
			 
			 Discounts discounts=new Discounts();
			 List<Promotion> promotionList = new ArrayList<Promotion>();
			 
			 Promotion promotion = new Promotion();
			  	promotion.setName("");
				promotionList.add(promotion);
				discounts.setAppliedPromotion("NA");
				discounts.setPromotions(promotionList);
			 
			 LoyaltyUser ltyUser = new LoyaltyUser();
			 ltyUser.setUserName(couponCodeRedeemReq.getUSERDETAILS().getUSERNAME());
			 ltyUser.setToken(couponCodeRedeemReq.getUSERDETAILS().getTOKEN());
			 ltyUser.setOrganizationId(couponCodeRedeemReq.getUSERDETAILS().getORGID());
			 
			 loyaltyRedemptionRequest.setHeader(header);
			 loyaltyRedemptionRequest.setMembership(membership);
			 loyaltyRedemptionRequest.setAmount(amount);
			 loyaltyRedemptionRequest.setDiscounts(discounts);
			 loyaltyRedemptionRequest.setCustomer(customer);
			 loyaltyRedemptionRequest.setUser(ltyUser);
			 
			 
			 return loyaltyRedemptionRequest;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("===Exception===", e);
		}
	     return null;
	}
	
	private String getDate(String dateStr){
		
		try {
			char[] datechars = dateStr.toCharArray();
			
			String yyyy = Constants.STRING_NILL;
			String mm = Constants.STRING_NILL;
			String dd = Constants.STRING_NILL;
			String hh = Constants.STRING_NILL;
			String MM = Constants.STRING_NILL;
			String ss = Constants.STRING_NILL;
			
			
			for (int i = 0; i < datechars.length; i++) {
				
				
				if(i>=14) break;
				if(i<=3) yyyy += datechars[i];
				if(i > 3 && i<6 ) mm += datechars[i];
				if(i > 5 && i<8 ) dd += datechars[i];
				if(i > 7 && i<10 ) hh += datechars[i];
				if(i > 9 && i<12 ) MM += datechars[i];
				if(i > 11 && i<14 ) ss += datechars[i];
				
			} 
			
			return yyyy+Constants.DELIMETER_HIPHEN+mm+Constants.DELIMETER_HIPHEN+dd+
					Constants.DELIMETER_SPACE+hh+Constants.DELIMETER_COLON+MM+Constants.DELIMETER_COLON+ss;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception====", e);
			return MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STYEAR);
		}
		
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
	public static void main(String[] args) {
		
char[] datechars = "20200216142213773".toCharArray();
		
		String yyyy = Constants.STRING_NILL;
		String mm = Constants.STRING_NILL;
		String dd = Constants.STRING_NILL;
		String hh = Constants.STRING_NILL;
		String MM = Constants.STRING_NILL;
		String ss = Constants.STRING_NILL;
		
		
		for (int i = 0; i < datechars.length; i++) {
			
			if(i>=14) break;
			if(i<=3) yyyy += datechars[i];
			if(i > 3 && i<6 ) mm += datechars[i];
			if(i > 5 && i<8 ) dd += datechars[i];
			if(i > 7 && i<10 ) hh += datechars[i];
			if(i > 9 && i<12 ) MM += datechars[i];
			if(i > 11 && i<14 ) ss += datechars[i];
			
		} 
		
		System.out.println(yyyy+Constants.DELIMETER_HIPHEN+mm+Constants.DELIMETER_HIPHEN+dd+
				Constants.DELIMETER_SPACE+hh+Constants.DELIMETER_COLON+MM+Constants.DELIMETER_COLON+ss);

		
	}
}
