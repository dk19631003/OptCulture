package org.mq.optculture.business.helper;


import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.CouponDiscountGeneration;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.DigitalReceiptsJSON;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponDiscountGenerateDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.DR.DRItem;
import org.mq.optculture.model.DR.DigitalReceipt;
import org.mq.optculture.model.couponcodes.CouponCodeInfo;
import org.mq.optculture.model.couponcodes.CouponCodeRedeemReq;
import org.mq.optculture.model.couponcodes.StatusInfo;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class CouponCodeProcessHelper {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public  StatusInfo validateContactObj(CouponCodeInfo couponCodeInfoObj,Coupons coupObj,CouponCodes coupCodeObj,Users user) throws BaseServiceException {
		StatusInfo statusInfo = null;
		try {
			logger.debug("-------entered validateContactObj---------");
			statusInfo = checkIfPromoRedeemed(coupObj,coupCodeObj);
			if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				return statusInfo;
			}
			if(coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_SINGLE) ) {
				statusInfo = checkSinglePromocode(couponCodeInfoObj,coupObj,user);
				if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					return statusInfo;
				}
			}
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing validateContactObj::::: ", e);
		}
		logger.debug("-------exit  validateContactObj---------");
		return statusInfo;
	}//validateContactObj

	private StatusInfo checkIfPromoRedeemed(Coupons coupObj,CouponCodes coupCodeObj) throws BaseServiceException{
		logger.debug("-------entered checkIfPromoRedeemed---------");
		StatusInfo statusInfo=null;
		if(coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_MULTIPLE) && 
				coupCodeObj.getStatus().trim().equals(Constants.COUP_CODE_STATUS_REDEEMED)) {
			statusInfo = new StatusInfo("100014",PropertyUtil.getErrorMessage(100014,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  checkIfPromoRedeemed---------");
		return statusInfo;
	}//checkIfPromoRedeemed

	public StatusInfo checkSinglePromocode(CouponCodeInfo couponCodeInfoObj,Coupons coupObj,Users user) throws BaseServiceException {
		StatusInfo statusInfo=null;
		try {
			logger.debug("-------entered checkSinglePromocode---------");
			CouponCodesDao couponCodesDao = (CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			Long orgIdLong = user.getUserOrganization().getUserOrgId();
			long redeemdCount = couponCodesDao.findRedeemdCoupCodeByCoup(coupObj.getCouponId(), orgIdLong, Constants.COUP_CODE_STATUS_REDEEMED);

			statusInfo = checkRedemptionLimit(coupObj, redeemdCount);
			if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				return statusInfo;
			}

			if(coupObj.getSingPromoContUnlimitedRedmptChk() != null && !coupObj.getSingPromoContUnlimitedRedmptChk()) {
				String coupCodeStr = couponCodeInfoObj.getCOUPONCODE();
				String customerId =  couponCodeInfoObj.getCUSTOMERID();
				String phoneStr =  couponCodeInfoObj.getPHONE();
				String emailIdStr =  couponCodeInfoObj.getEMAIL();
				Contacts contObj = null;
				CouponCodes couponCodes =null;

				contObj = getContactObj(emailIdStr,phoneStr,customerId,contObj, user);
				if(contObj!=null) {

					contObj.setUsers(user);

					List<Contacts> contactList=getOrgContactsList(contObj,orgIdLong);
					
					if(contactList == null || contactList.size() == 0 ) {
						if (!coupObj.getRedemedAutoChk()) {
							statusInfo = new StatusInfo("100013",PropertyUtil.getErrorMessage(100013,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							return statusInfo;
						}
					}
					/*statusInfo = validateContactList(contactList,coupObj,redeemdCount);
//					logger.info("Contact List Object is ::::::: " + contactList + "    Status info is:::::::  " + statusInfo.getSTATUS());
					if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
						return statusInfo;
					}*/
					else {
					couponCodes  = getCouponCodes(contactList,orgIdLong,coupCodeStr);
					statusInfo = validateCouponCodes(couponCodes,coupObj,redeemdCount);
					if(statusInfo!=null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
						return statusInfo;
					}
					}

				}else if(couponCodes == null && !coupObj.getRedemedAutoChk() &&
						coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() <= redeemdCount) {
					statusInfo = new StatusInfo("100013",PropertyUtil.getErrorMessage(100013,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return statusInfo;
				}
			}
		}catch(Exception  e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing checkSinglePromocode::::: ", e);
		}
		logger.debug("-------exit  checkSinglePromocode---------");
		return statusInfo;
	}//checkSinglePromocode

	private StatusInfo checkRedemptionLimit(Coupons coupObj,Long redeemdCount) throws BaseServiceException{
		logger.debug("-------entered checkRedemptionLimit---------");
		StatusInfo statusInfo=null;
		if(coupObj.getRedemedAutoChk()== false &&
				(coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() <= redeemdCount )) {
			statusInfo = new StatusInfo("100921",PropertyUtil.getErrorMessage(100921,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  checkRedemptionLimit---------");
		return statusInfo;
	}//checkRedemptionLimit

	private CouponCodes getCouponCodes(List<Contacts> contactList,Long orgIdLong,String coupCodeStr) throws BaseServiceException {

		logger.debug("-------entered getCouponCodes---------");
		CouponCodes couponCodes = null; //(CouponCodes) ServiceLocator.getInstance().getServiceByName("couponCodes");
		try {
			CouponCodesDao couponCodesDao=(CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);

			String contactIdStr = "";
			for (Contacts contacts : contactList) {
				contactIdStr += contactIdStr.trim().length() == 0 ? ""+contacts.getContactId() :","+contacts.getContactId();
			}
			couponCodes = couponCodesDao.findCoupCodeByContactId(contactIdStr , orgIdLong ,coupCodeStr );
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getCouponCodes::::: ", e);
		}
		logger.debug("-------exit  getCouponCodes---------");
		return couponCodes;
	}//getCouponCodes

	private StatusInfo validateCouponCodes(CouponCodes couponCodes,Coupons coupObj,long redeemdCount)throws BaseServiceException {
		logger.debug("-------entered validateCouponCodes---------");
		StatusInfo statusInfo=null;
		if(couponCodes == null && !coupObj.getRedemedAutoChk() &&
				coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() <= redeemdCount) {
			statusInfo = new StatusInfo("100013",PropertyUtil.getErrorMessage(100013,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		logger.debug("-------exit  validateCouponCodes---------");
		return statusInfo;
	}//validateCouponCodes

	private List<Contacts> getOrgContactsList(Contacts contObj,Long orgIdLong) throws BaseServiceException {
		logger.debug("-------entered getOrgContactsList---------");
		List<Contacts> contactList = getAllContactsByOrg(orgIdLong, contObj);
		logger.debug("-------exit  getOrgContactsList---------"+contactList);
		return contactList;
	} //getOrgContactsList

	/*private StatusInfo validateContactList(List<Contacts> contactList,Coupons coupObj, long redeemdCount)throws BaseServiceException {
		logger.debug("-------entered validateContactList---------");
		StatusInfo statusInfo=null;
		if(contactList == null || contactList.size() == 0 ) {
			if (!coupObj.getRedemedAutoChk()) {
				statusInfo = new StatusInfo("100013",PropertyUtil.getErrorMessage(100013,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
		}
		logger.debug("-------exit  validateContactList---------");
		return statusInfo;
	}//validateContactList
*/
	public Contacts getContactObj(String emailIdStr,String phoneStr,String customerId,Contacts contObj,Users user)throws BaseServiceException{
		
		logger.debug("-------entered getContactObj---------");
		UserOrganization organization = user != null ?  user.getUserOrganization() : null;
		if(emailIdStr != null && emailIdStr.trim().length() > 0  && Utility.validateEmail(emailIdStr)) {
			if(contObj == null){
				contObj = new Contacts();
				contObj.setEmailId(emailIdStr.trim());
				return contObj;
			}
			else {
				contObj.setEmailId(emailIdStr.trim());
				return contObj;
			}
		}
		if(phoneStr != null && phoneStr.trim().length() >0 && 	Utility.phoneParse(phoneStr,organization) != null) {
			if(contObj==null) {
				contObj = new Contacts();
				contObj.setMobilePhone(phoneStr.trim());
				return contObj;
			}
			else {
				contObj.setMobilePhone(phoneStr.trim());
				return contObj;
			}
		}
		if(customerId != null && !customerId.isEmpty())  {
			if(contObj==null) {
				contObj = new Contacts();
				contObj.setExternalId(customerId);
				return contObj;
			}
			else {
				contObj.setExternalId(customerId);
				return contObj;
			}

		}
		logger.debug("-------exit  getContactObj---------");
		return contObj;
	}//getContactObj

	
	public Contacts findContactByUserId(Long userId, Contacts contObj, Users user) throws BaseServiceException{
		try {
			POSMappingDao posMappingDao=(POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
			TreeMap<String, List<String>> prioMap =  Utility.getPriorityMap(userId, Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
			if(prioMap == null) return null;
			ContactsDao contactsDao=(ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			return contObj = contactsDao.findContactByUniqPriority(prioMap,contObj, userId, user);
		} catch (Exception e) {

			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getAllContactsByOrg::::: ", e);
	
		}
       
    }
	private List<Contacts> getAllContactsByOrg(Long orgIdLong, Contacts contObj) throws BaseServiceException {
		List<Contacts> contactList = null;
		try {
			logger.debug("-------entered getAllContactsByOrg---------");
			UsersDao usersDao =(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			POSMappingDao posMappingDao=(POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
			ContactsDao contactsDao=(ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			List<Users> orgUserList = usersDao.getUsersListByOrg(orgIdLong);
			Map<Long, TreeMap<String, List<String>>> usersTreeMap = Utility.getPriorityMapByUsersList(orgUserList,Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
			logger.debug("-------exit  getAllContactsByOrg---------");
			return  contactList = contactsDao.findContactsByUserList(contObj, orgUserList, usersTreeMap);
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getAllContactsByOrg::::: ", e);
		}
	}//getAllContactsByOrg	
	//APP-3667
		public double getTotalDiscValue(Users user, Coupons coupObj, CouponCodeRedeemReq couponRedeemReq) {
			
			double totDiscount = 0;
			try {
			CouponDiscountGenerateDao couponDiscountGenerateDao=(CouponDiscountGenerateDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPON_DICOUNT_GENERATE_DAO);
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			ContactsLoyalty contactLty = contactsLoyaltyDao.findBy(user, couponRedeemReq.getCOUPONCODEINFO().getEMAIL(), couponRedeemReq.getCOUPONCODEINFO().getPHONE());
			List<CouponDiscountGeneration> coupDisList = couponDiscountGenerateDao.findByCoupon(coupObj);
			if(coupDisList == null || coupDisList.isEmpty() ) return totDiscount;
			
			CouponDiscountGeneration MaxMPVcoupDisGenObj = null;
			Double finialReceiptAmount = Double.parseDouble(couponRedeemReq.getPURCHASECOUPONINFO().getTOTALAMOUNT());
			
			Long MPV = 0l;
			
			for (CouponDiscountGeneration coupDisGenObj : coupDisList) {

				logger.debug("understanding the discount order"+" finialReceiptAmount "
						+ "="+finialReceiptAmount+" MPV==="+MPV+" ,TPA==="+coupDisGenObj.getTotPurchaseAmount()+",  ");
				String programID = coupDisGenObj.getProgram() != null ? coupDisGenObj.getProgram().toString() : Constants.STRING_NILL;
				String tierID = coupDisGenObj.getTierNum() != null ? coupDisGenObj.getTierNum().toString() : Constants.STRING_NILL;
				String cardSetId = coupDisGenObj.getCardSetNum() != null ? coupDisGenObj.getCardSetNum().toString() : Constants.STRING_NILL;
				if((programID != null && !programID.isEmpty() &&( (contactLty != null && !contactLty.getProgramId().toString().equals(programID)) || (programID != null && contactLty == null)))
						|| (tierID !=null && !tierID.isEmpty() && (contactLty != null && !contactLty.getProgramTierId().toString().equals(tierID))) ||
						(cardSetId != null && !cardSetId.isEmpty() && (contactLty != null && !contactLty.getCardSetId().toString().equals(cardSetId))) ){
					logger.debug("elegibility on tier + cardset are failed");
					continue;
				}
				if(coupDisGenObj.getTotPurchaseAmount() == null || coupDisGenObj.getTotPurchaseAmount()<=0) continue;
				if(MPV == 0 ) {
					MPV = (long)(coupDisGenObj.getTotPurchaseAmount()*(1-(coupDisGenObj.getDiscount()/100)));
				}
				if(MaxMPVcoupDisGenObj == null) MaxMPVcoupDisGenObj = coupDisGenObj;

				if(coupDisGenObj.getTotPurchaseAmount()*(1-(coupDisGenObj.getDiscount()/100)) > MaxMPVcoupDisGenObj.getTotPurchaseAmount()*(1-(MaxMPVcoupDisGenObj.getDiscount()/100))   &&
						finialReceiptAmount>= coupDisGenObj.getTotPurchaseAmount()*(1-(coupDisGenObj.getDiscount()/100))) {
					MaxMPVcoupDisGenObj = coupDisGenObj;
					MPV = (long)(coupDisGenObj.getTotPurchaseAmount()*(1-(coupDisGenObj.getDiscount()/100)));
				}



			}
			logger.info("MPV 1 && finialReceiptAmount 1 >>>> "+MPV+" "+finialReceiptAmount);

			if(MPV != 0 && MPV > finialReceiptAmount) return totDiscount;
			
			if(MaxMPVcoupDisGenObj != null){
				
				totDiscount = MaxMPVcoupDisGenObj.getDiscount();
			}else {
				
				for (CouponDiscountGeneration coupDisGenObj : coupDisList) {
					
					totDiscount = coupDisGenObj.getDiscount();
				}
			}
			
			
			if(totDiscount>0 && coupObj.getDiscountType().equals("Percentage")) {
				
				double discDecimal = 1-(totDiscount/100);
				finialReceiptAmount = finialReceiptAmount/discDecimal;
				totDiscount = (finialReceiptAmount*totDiscount)/100;
			}
			logger.info("totDiscount>>>>"+totDiscount);
			
			} catch(Exception e) {}
			
			return totDiscount; 
		}
}


