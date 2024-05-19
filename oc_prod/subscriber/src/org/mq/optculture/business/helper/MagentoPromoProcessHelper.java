package org.mq.optculture.business.helper;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.magento.MagentoPromoRequest;
import org.mq.optculture.model.magento.MagentoPromoResponse;
import org.mq.optculture.model.magento.CouponCodeInfo;
import org.mq.optculture.model.magento.StatusInfo;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class MagentoPromoProcessHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public  StatusInfo validateContactObj(MagentoPromoRequest magentoPromoRequest,Coupons coupObj,CouponCodes coupCodeObj,Users user) throws BaseServiceException {
		StatusInfo statusInfo =null;
		try {
			statusInfo = checkIfPromoRedeemed(coupObj,coupCodeObj);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
				return statusInfo;
			}
			else if(coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_SINGLE) ) {
				statusInfo = checkSinglePromocode( magentoPromoRequest,coupObj,user);
			}

		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing validateContactObj::::: ", e);
		}
		return statusInfo;
	}//validateContactObj

	private StatusInfo checkIfPromoRedeemed(Coupons coupObj,CouponCodes coupCodeObj) throws BaseServiceException{
		StatusInfo statusInfo=null;
		if(coupObj.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_MULTIPLE) && 
				coupCodeObj.getStatus().trim().equals(Constants.COUP_CODE_STATUS_REDEEMED)) {

			statusInfo = new StatusInfo("100014",PropertyUtil.getErrorMessage(100014,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		return statusInfo;
	}//checkIfPromoRedeemed

	private StatusInfo checkSinglePromocode(MagentoPromoRequest magentoPromoRequest,Coupons coupObj,Users user)throws BaseServiceException {
		StatusInfo statusInfo = null;
		try {
			CouponCodesDao couponCodesDao = (CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Long orgIdLong = user.getUserOrganization().getUserOrgId();
			long redeemdCount = couponCodesDao.findRedeemdCoupCodeByCoup(coupObj.getCouponId(), orgIdLong, Constants.COUP_CODE_STATUS_REDEEMED);

			statusInfo = checkRedemptionLimit(coupObj, redeemdCount);
			if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
				return statusInfo;
			}

			if(coupObj.getSingPromoContUnlimitedRedmptChk() != null && !coupObj.getSingPromoContUnlimitedRedmptChk()) {
				CouponCodeInfo promoCodeDetailsObj=magentoPromoRequest.getCouponCodeInfo();
				String coupCodeStr = promoCodeDetailsObj.getCouponCode();
				String customerId =  promoCodeDetailsObj.getCustomerId();
				String phoneStr =  promoCodeDetailsObj.getPhone();
				String emailIdStr =  promoCodeDetailsObj.getEmail();
				Contacts contObj = null;
				CouponCodes couponCodes =null;
				contObj = getContactObj(emailIdStr,phoneStr,customerId,contObj, user);
				if(contObj!=null) {
					contObj.setUsers(user);
					List<Contacts> contactList=getOrgContactsList(contObj,orgIdLong);
					statusInfo = validateContactList(contactList,coupObj);
					if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
						return statusInfo;
					}
					couponCodes  = getCouponCodes(contactList,orgIdLong,coupCodeStr);
					statusInfo = validateCouponCodes(couponCodes,coupObj);
					if(statusInfo!=null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(statusInfo.getStatus())){
						return statusInfo;
					}
				}else if(couponCodes == null && !coupObj.getRedemedAutoChk()) {
					statusInfo = new StatusInfo("100013",PropertyUtil.getErrorMessage(100013,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return statusInfo;
				}
			}
		}catch(Exception  e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing checkSinglePromocode::::: ", e);
		}
		return statusInfo;
	}//checkSinglePromocode

	private StatusInfo checkRedemptionLimit(Coupons coupObj,Long redeemdCount)throws BaseServiceException {
		StatusInfo statusInfo=null;
		if(coupObj.getRedemedAutoChk()== false &&
				(coupObj.getRedeemdCount() != null &&  coupObj.getRedeemdCount() <= redeemdCount )) {
			statusInfo = new StatusInfo("100921",PropertyUtil.getErrorMessage(100921,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		return statusInfo;
	}//checkRedemptionLimit

	private CouponCodes getCouponCodes(List<Contacts> contactList,Long orgIdLong,String coupCodeStr)throws BaseServiceException {
		CouponCodes couponCodes=null;
		try {
			couponCodes = null;//(CouponCodes) ServiceLocator.getInstance().getServiceByName("couponCodes");
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
		return couponCodes;
	}//getCouponCodes

	private StatusInfo validateCouponCodes(CouponCodes couponCodes,Coupons coupObj)throws BaseServiceException{
		StatusInfo statusInfo=null;
		if(couponCodes == null && !coupObj.getRedemedAutoChk()) {
			statusInfo = new StatusInfo("100013",PropertyUtil.getErrorMessage(100013,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		return statusInfo;
	}//validateCouponCodes

	private List<Contacts> getOrgContactsList(Contacts contObj,Long orgIdLong) throws BaseServiceException{
		List<Contacts> contactList = getAllContactsByOrg(orgIdLong, contObj);
		return contactList;
	} //getOrgContactsList

	private StatusInfo validateContactList(List<Contacts> contactList,Coupons coupObj)throws BaseServiceException {
		StatusInfo statusInfo = null;
		if(contactList == null || contactList.size() == 0 ) {
			if (!coupObj.getRedemedAutoChk()) {
				statusInfo = new StatusInfo("100013",PropertyUtil.getErrorMessage(100013,OCConstants.ERROR_PROMO_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
		}
		return statusInfo;
	}//validateContactList

	public Contacts getContactObj(String emailIdStr,String phoneStr,String customerId,Contacts contObj,Users user)throws BaseServiceException{

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
		if(phoneStr != null && phoneStr.trim().length() >0 && 	Utility.phoneParse(phoneStr, user!=null ? user.getUserOrganization() : null ) != null) {
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
		return contObj;
	}//getContactObj

	private List<Contacts> getAllContactsByOrg(Long orgIdLong, Contacts contObj)throws BaseServiceException {
		List<Contacts> contactList = null;
		try {
			UsersDao usersDao =(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			POSMappingDao posMappingDao=(POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
			ContactsDao contactsDao=(ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			List<Users> orgUserList = usersDao.getUsersListByOrg(orgIdLong);
			Map<Long, TreeMap<String, List<String>>> usersTreeMap = Utility.getPriorityMapByUsersList(orgUserList,Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
			return  contactList = contactsDao.findContactsByUserList(contObj, orgUserList, usersTreeMap);
		}catch(Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing getAllContactsByOrg::::: ", e);
		}
	}
}//getAllContactsByOrg	
