/**
 * 
 */
package org.mq.optculture.business.magento;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.helper.MagentoPromoProcessHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.magento.PurchasedItems;
import org.mq.optculture.model.magento.UserDetails;
import org.mq.optculture.model.magento.HeaderInfo;
import org.mq.optculture.model.magento.MagentoPromoRequest;
import org.mq.optculture.model.magento.MagentoPromoResponse;
import org.mq.optculture.model.magento.PromoCodeDiscountInfo;
import org.mq.optculture.model.magento.CouponCodeInfo;
import org.mq.optculture.model.magento.PromoCodeResponse;
import org.mq.optculture.model.magento.PromoDiscountInfo;
import org.mq.optculture.model.magento.StatusInfo;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

/**
 * @author abheeshna.nalla
 *
 */
public class MagentoBusinessServiceImpl implements MagentoBusinessService{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		BaseResponseObject baseResponseObject=new BaseResponseObject();
		MagentoPromoResponse magentoPromoResponse=null;
		if(baseRequestObject.getAction() == null){
			throw new BaseServiceException("Please provide action");
		}
		try {
			if(baseRequestObject.getAction().equals(OCConstants.MAGENTO_SERVICE_ACTION_PROMO_ENQUIRY_REQUEST)){
				
				//json to object
				Gson gson = new Gson();
				MagentoPromoRequest magentoPromoRequest = gson.fromJson(baseRequestObject.getJsonValue(), MagentoPromoRequest.class);
				MagentoBusinessService magentoBusinessService=(MagentoBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.MAGENTO_BUSINESS_SERVICE);
				magentoPromoResponse =(MagentoPromoResponse) magentoBusinessService.processPromoRequest(magentoPromoRequest);

				//object to json
				String jsonValue=gson.toJson(magentoPromoResponse);
				//logger.debug("jsonValue-------------"+jsonValue);
				baseResponseObject.setJsonValue(jsonValue);
				baseResponseObject.setAction(OCConstants.MAGENTO_SERVICE_ACTION_PROMO_ENQUIRY_REQUEST);
			}
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}
		return baseResponseObject;
	}//processRequest

	@Override
	public MagentoPromoResponse processPromoRequest(
			MagentoPromoRequest magentoPromoRequest)
					throws BaseServiceException {
		MagentoPromoResponse magentoPromoResponse = null;
		StatusInfo statusInfo=null;
		List<PromoCodeDiscountInfo> promoCodeDiscountInfoList=null ;
		try {
			statusInfo = validateJsonUser(magentoPromoRequest);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
				magentoPromoResponse = finalJsonResponse(magentoPromoRequest.getHeaderInfo().getRequestId(),promoCodeDiscountInfoList,statusInfo);
				return magentoPromoResponse;
			}
			UserDetails userDetailsObj=magentoPromoRequest.getUserDetails();
			CouponCodeInfo promoCodeDetailsObj=magentoPromoRequest.getCouponCodeInfo();
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			String userNameStr  = userDetailsObj.getUserName();
			String orgId = userDetailsObj.getOrgId();
			String coupCodeStr = promoCodeDetailsObj.getCouponCode();
			String tokenStr = userDetailsObj.getToken();
			Users user = usersDao.findByToken(userNameStr+Constants.USER_AND_ORG_SEPARATOR+orgId , tokenStr);
			if(!coupCodeStr.equals("OC-LOYALTY")) {
				CouponCodes  coupCodeObj = couponCodesDao.testForCouponCodes(coupCodeStr, user.getUserOrganization().getUserOrgId());
				Coupons coupObj = getCouponObj(user,coupCodeStr);
				statusInfo = validateCoupon(coupObj);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
					magentoPromoResponse = finalJsonResponse(magentoPromoRequest.getHeaderInfo().getRequestId(),promoCodeDiscountInfoList,statusInfo);
					return magentoPromoResponse;
				}
				statusInfo = validateStoreNum(magentoPromoRequest,coupObj);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
					magentoPromoResponse = finalJsonResponse(magentoPromoRequest.getHeaderInfo().getRequestId(),promoCodeDiscountInfoList,statusInfo);
					return magentoPromoResponse;
				}
				MagentoPromoProcessHelper magentopromoProcessHelper = new MagentoPromoProcessHelper();
				statusInfo=magentopromoProcessHelper.validateContactObj(magentoPromoRequest,coupObj,coupCodeObj,user);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
					magentoPromoResponse = finalJsonResponse(magentoPromoRequest.getHeaderInfo().getRequestId(),promoCodeDiscountInfoList,statusInfo);
					return magentoPromoResponse;
				}
				if(coupObj.getDiscountCriteria().equals("SKU")) {
					List<PurchasedItems> purItemList=checkIfSkuType(magentoPromoRequest,coupObj);
					statusInfo =  validatePurItemList(purItemList);
				}
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
					magentoPromoResponse = finalJsonResponse(magentoPromoRequest.getHeaderInfo().getRequestId(),promoCodeDiscountInfoList,statusInfo);
					return magentoPromoResponse;
				}
				promoCodeDiscountInfoList= setCoupDiscInfoObj(magentoPromoRequest, coupObj);
				if(promoCodeDiscountInfoList==null) {
					statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					magentoPromoResponse = finalJsonResponse(magentoPromoRequest.getHeaderInfo().getRequestId(),promoCodeDiscountInfoList,statusInfo);
					return magentoPromoResponse;
				}/*else {
					statusInfo = new StatusInfo( "0","Promo-code enquiry is successful.",OCConstants.MAGENTO_SERVICE_RESPONSE_STATUS_SUCCESS);
					return magentoPromoResponse;
				}*/
			}
			else {
				statusInfo = promoReedemptionWithLoyaltyPts(magentoPromoRequest,user);
				if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
					magentoPromoResponse = finalJsonResponse(magentoPromoRequest.getHeaderInfo().getRequestId(),promoCodeDiscountInfoList,statusInfo);
					return magentoPromoResponse;
				}
			}
			statusInfo = new StatusInfo( "0","Promo-code enquiry is successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			magentoPromoResponse = finalJsonResponse(magentoPromoRequest.getHeaderInfo().getRequestId(),promoCodeDiscountInfoList,statusInfo);
			return magentoPromoResponse;
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processPromoRequest::::: ", e);
		}
		/*finally {
			magentoPromoResponse = finalJsonResponse(magentoPromoRequest.getHeaderInfo().getRequestId(),promoCodeDiscountInfoList,statusInfo);
			return magentoPromoResponse;
		}*/
	}//processPromoRequest

	private StatusInfo validatePurItemList(List<PurchasedItems> purItemList) {
		StatusInfo statusInfo=null;
		if(purItemList == null) {
			logger.debug("Error : unable to find the Promo-code purchase Info in JSON ****");
			statusInfo = new StatusInfo("100009",PropertyUtil.getErrorMessage(100009,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo ;
		}
		if(purItemList.size() == 0) {
			logger.debug("Error : No Item Code Info from JSON ****");
			statusInfo = new StatusInfo("100010",PropertyUtil.getErrorMessage(100010,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo ;
		}
		return statusInfo;
	}//validatePurItemList

	private StatusInfo validateCoupon(Coupons coupObj) {
		StatusInfo statusInfo =null;
		if(coupObj == null){
			logger.debug("Error :Promo-code not exists in DB ****");
			statusInfo = new StatusInfo("100012",PropertyUtil.getErrorMessage(100012,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo ;
		}
		if(Calendar.getInstance().after(coupObj.getCouponExpiryDate())) {
			statusInfo = new StatusInfo("100015",PropertyUtil.getErrorMessage(100015,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		return statusInfo;
	}//validateCoupon

	private StatusInfo validateJsonUser(MagentoPromoRequest magentoPromoRequest) throws BaseServiceException {
		StatusInfo statusInfo =null;
		try {
			CouponCodeInfo promoCodeDetailsObj=magentoPromoRequest.getCouponCodeInfo();
			UserDetails userDetailsObj=magentoPromoRequest.getUserDetails();
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			if(userDetailsObj == null) {
				logger.debug("Error : unable to find the User details in JSON ****");
				statusInfo = new StatusInfo("100003",PropertyUtil.getErrorMessage(100003,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			String userNameStr  = userDetailsObj.getUserName();
			if(userNameStr == null || userNameStr.trim().length() == 0) {
				logger.debug("Error : unable to find the User Name  in JSON ****");
				statusInfo = new StatusInfo("100004",PropertyUtil.getErrorMessage(100004,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			String orgId = userDetailsObj.getOrgId();
			if(orgId == null || orgId.trim().length() == 0) {
				logger.debug("Error : unable to find the User Organization  in JSON ****");
				statusInfo = new StatusInfo("100005",PropertyUtil.getErrorMessage(100005,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			String tokenStr = userDetailsObj.getToken();
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
			if(promoCodeDetailsObj==null) {
				logger.debug("Error : unable to find the Promo-code Info  in JSON ****");
				statusInfo = new StatusInfo("100007",PropertyUtil.getErrorMessage(100007,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
			String coupCodeStr = promoCodeDetailsObj.getCouponCode();
			if(coupCodeStr==null || coupCodeStr.trim().length() == 0 ) {
				logger.debug("Error : unable to find thePromo-code Info  in JSON ****");
				statusInfo = new StatusInfo("100008",PropertyUtil.getErrorMessage(100008,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing validateJsonUser::::: ", e);
		}
		return statusInfo;
	}//validateJsonUser

	private Coupons getCouponObj(Users user,String coupCodeStr) throws BaseServiceException{
		Coupons coupObj = null;
		try {
			CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			CouponsDao couponsDao=(CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			CouponCodes  coupCodeObj = couponCodesDao.testForCouponCodes(coupCodeStr, user.getUserOrganization().getUserOrgId());
			if(coupCodeObj == null) {
				coupObj = couponsDao.findCoupon(coupCodeStr,user.getUserOrganization().getUserOrgId());

			}else{
				coupObj  = coupCodeObj.getCouponId();
			}

		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getCouponObj::::: ", e);
			
		}
		return coupObj;
	}//getCouponObj

	private StatusInfo promoReedemptionWithLoyaltyPts(MagentoPromoRequest magentoPromoRequest,Users user) throws BaseServiceException{
		StatusInfo statusInfo=null;
		try {
			List<PromoCodeDiscountInfo> promoCodeDiscountInfoList=null ;
			CouponsDao couponsDao=(CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			CouponDiscountGenerateDao coupDiscGenDao=(CouponDiscountGenerateDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
			List<Coupons> listPromoCoupons = couponsDao.listOfSinglePromoCoupons(user.getUserOrganization().getUserOrgId());
			statusInfo = validatePromoList(listPromoCoupons);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
				return statusInfo;
			}
			String coupIdStr = "";
			for (Coupons coupons : listPromoCoupons) {
				if(coupIdStr.trim().length()  ==  0) coupIdStr = ""+coupons.getCouponId();
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

			Set<Long> coupObjIdSet = setOfCoupDisMap.keySet();
			for (Long eachCoupId : coupObjIdSet) {
				tempCoupDisList = setOfCoupDisMap.get(eachCoupId);
				Coupons eachCouponObj = ((CouponDiscountGeneration)tempCoupDisList.get(0)).getCoupons();
				promoCodeDiscountInfoList = setCoupDiscInfoObj(magentoPromoRequest,eachCouponObj);
				if(promoCodeDiscountInfoList==null) {
					statusInfo = new StatusInfo("100016",PropertyUtil.getErrorMessage(100016,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return statusInfo;
				}else {
					statusInfo = new StatusInfo( "0","Promo-code enquiry is successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					return statusInfo;
				}
			}

		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing promoReedemptionWithLoyaltyPts::::: ", e);
		}
		return statusInfo;
	}//promoReedemptionWithLoyaltyPts

	private StatusInfo validatePromoList(List<Coupons> listPromoCoupons) throws BaseServiceException{
		StatusInfo statusInfo =null;
		if(listPromoCoupons == null || listPromoCoupons.size() == 0 ) {
			statusInfo = new StatusInfo("100019",PropertyUtil.getErrorMessage(100019,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		return statusInfo;
	}//validatePromoList

	private List<PromoCodeDiscountInfo> setCoupDiscInfoObj(MagentoPromoRequest magentoPromoRequest,Coupons eachCouponObj) throws BaseServiceException {

		List<PromoCodeDiscountInfo> promoCodeDiscountInfoList=new ArrayList<PromoCodeDiscountInfo>() ;
		PromoCodeDiscountInfo promoCodeDiscountInfo=new PromoCodeDiscountInfo();
		promoCodeDiscountInfo.setCouponName(eachCouponObj.getCouponName());
		promoCodeDiscountInfo.setCouponCode(eachCouponObj.getCouponCode());
		String loyaltyPointsStr = eachCouponObj.getRequiredLoyltyPoits() == null ? "" : ""+eachCouponObj.getRequiredLoyltyPoits();
		promoCodeDiscountInfo.setLoyaltyPoints(loyaltyPointsStr);

		if(eachCouponObj.getRequiredLoyltyPoits() != null && eachCouponObj.getLoyaltyPoints() != null) {
			promoCodeDiscountInfo.setCouponType("ONLY-LOYALTY"); 

		}else {

			promoCodeDiscountInfo.setCouponType("REGULAR");
		}

		promoCodeDiscountInfo.setValidFrom(MyCalendar.calendarToString(
				eachCouponObj.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR));

		promoCodeDiscountInfo.setValidTo(MyCalendar.calendarToString(eachCouponObj.
				getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STYEAR));


		boolean isItemCode = false;
		String disCountType ="";
		if(eachCouponObj.getDiscountType().equals("Percentage") && eachCouponObj.getDiscountCriteria().equals("Total Purchase Amount")) {

			promoCodeDiscountInfo.setDiscountCriteria("PERCENTAGE-MVP");
			disCountType = "P";
		}
		else if(eachCouponObj.getDiscountType().equals("Percentage") && eachCouponObj.getDiscountCriteria().equals("SKU")) {
			promoCodeDiscountInfo.setDiscountCriteria("PERCENTAGE-IC");
			isItemCode = true;
			disCountType = "P";
		}
		else if(eachCouponObj.getDiscountType().equals("Value") && eachCouponObj.getDiscountCriteria().equals("Total Purchase Amount")) {
			promoCodeDiscountInfo.setDiscountCriteria("VALUE-MVP");
			disCountType = "V";
		}
		else {
			promoCodeDiscountInfo.setDiscountCriteria("VALUE-I");
			isItemCode = true;
			disCountType = "V";
		}
		promoCodeDiscountInfo = discountAmount(magentoPromoRequest, eachCouponObj, isItemCode, promoCodeDiscountInfo, promoCodeDiscountInfoList);
		promoCodeDiscountInfoList.add(promoCodeDiscountInfo);
		return promoCodeDiscountInfoList;
	}//setCoupDiscInfoObj

	private PromoCodeDiscountInfo discountAmount(MagentoPromoRequest magentoPromoRequest,Coupons coupObj,boolean isItemCode,PromoCodeDiscountInfo promoCodeDiscountInfo,List<PromoCodeDiscountInfo> promoCodeDiscountInfoList) throws BaseServiceException {
		try {

			PromoDiscountInfo promoDiscountInfo = new PromoDiscountInfo();
			CouponDiscountGenerateDao coupDiscGenDao=(CouponDiscountGenerateDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
			List<PurchasedItems> purItemList = magentoPromoRequest.getPurchasedItems();
			Map<String, PurchasedItems> isItemMap=new HashMap<String, PurchasedItems>();
			Set<String> itemCodeSet = new HashSet<String>();

			PurchasedItems tempObj = null;
			if(purItemList != null && purItemList.size() > 0) {

				for (Object object : purItemList) {
					if(object instanceof PurchasedItems){
						tempObj = (PurchasedItems)object;

						if(isItemCode) {
							itemCodeSet.add(tempObj.getItemCode());
						}
						isItemMap.put(tempObj.getItemCode(), tempObj);
					}
				} //for
			}

			String itemCodeStr = "";
			if(itemCodeSet != null && itemCodeSet.size() > 0){
				for (String string : itemCodeSet) {
					if(itemCodeStr.trim().length() > 0) {
						itemCodeStr = itemCodeStr+", '"+string+"'";
					}else {
						itemCodeStr = itemCodeStr +"'"+string+"'";
					}
				}

			}

			List<CouponDiscountGeneration>  coupDisList = coupDiscGenDao.findCoupCodeByFlag(coupObj ,itemCodeStr);
			if(coupDisList != null && coupDisList.size() > 0 ) {
				Double itemsTotalDiscountAmount=0.0;
				Double tpaTotalDiscountAmount =0.0;
				for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
					Double discount = null;
					if(isItemCode) {
						if(coupObj.getDiscountType().equals("Value") && coupObj.getDiscountCriteria().equals("SKU")) {
							discount = coupDisGenObj.getDiscount();
							itemsTotalDiscountAmount+=discount;
						}
						else if(coupObj.getDiscountType().equals("Percentage") && coupObj.getDiscountCriteria().equals("SKU")) {
							discount = coupDisGenObj.getDiscount();
							
							tempObj = isItemMap.get(coupDisGenObj.getItemCategory());
							Double price = Double.parseDouble(tempObj.getPrice());
							itemsTotalDiscountAmount+=(price*discount)/100;
						}
					}
					else {
						if(coupObj.getDiscountType().equals("Value") && coupObj.getDiscountCriteria().equals("Total Purchase Amount")) {
							discount = coupDisGenObj.getDiscount();
							tpaTotalDiscountAmount+=discount;
						}
						else {
							discount = coupDisGenObj.getDiscount();
							Long totalpurchaseAmount =coupDisGenObj.getTotPurchaseAmount();
							tpaTotalDiscountAmount+=(totalpurchaseAmount*discount)/100;
						}
					}
					promoDiscountInfo.setItemsTotalDiscountAmount(itemsTotalDiscountAmount.toString());
					promoDiscountInfo.setTpaTotalDiscountAmount(tpaTotalDiscountAmount.toString());
				}

			}
			promoCodeDiscountInfo.setPromoDiscountInfo(promoDiscountInfo);

		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing discountAmount::::: ", e);
		}
		return promoCodeDiscountInfo;
	}//discountAmount

	private MagentoPromoResponse finalJsonResponse(String requestId ,List<PromoCodeDiscountInfo> promoCodeDiscountInfoList,StatusInfo statusInfo) throws BaseServiceException {
		MagentoPromoResponse magentoPromoResponse = new MagentoPromoResponse();
		PromoCodeResponse promoCodeResponse=new PromoCodeResponse(promoCodeDiscountInfoList);
		magentoPromoResponse.setPromoCodeResponse(promoCodeResponse);
		magentoPromoResponse.setStatusInfo(statusInfo);
		HeaderInfo headerInfo = new HeaderInfo(requestId,null);
		magentoPromoResponse.setHeaderInfo(headerInfo);
		return magentoPromoResponse;
	}//finalJsonResponse

	private StatusInfo validateStoreNum(MagentoPromoRequest magentoPromoRequest,Coupons coupObj) throws BaseServiceException{
		StatusInfo statusInfo=null;
		CouponCodeInfo promoCodeDetailsObj=magentoPromoRequest.getCouponCodeInfo();
		String strorNumberStr = "";
		if((coupObj.getAllStoreChk() != null && coupObj.getAllStoreChk() == false) ||
				(coupObj.getSelectedStores() != null && coupObj.getSelectedStores().trim().length() > 0)) {
			if(promoCodeDetailsObj.getStoreNumber()!=null) {
				strorNumberStr = promoCodeDetailsObj.getStoreNumber();
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
			for (String eachStoe : storeListArr) {
				if(eachStoe.equals(strorNumberStr.trim())) {
					storeExisted = true ;
					break;
				}
			}
			if(!storeExisted) {
				statusInfo = new StatusInfo("100021",PropertyUtil.getErrorMessage(100021,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
		}
		return statusInfo;
	}//validateStoreNum()

	private List<PurchasedItems> checkIfSkuType(MagentoPromoRequest magentoPromoRequest,Coupons coupObj) throws BaseServiceException{
		List<PurchasedItems>  purItemList= null;
		purItemList = magentoPromoRequest.getPurchasedItems();
		return purItemList;
	}//checkIfSkuType

}
