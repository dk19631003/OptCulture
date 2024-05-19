package org.mq.captiway.scheduler.services;

import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.CouponCodes;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.CouponCodesDaoForDML;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;

public class UpdateCouponsTimer extends TimerTask {
	
private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
		
	@Override
	public void run() {
		try{
			
			CouponsDao couponsDao = null;
			couponsDao = (CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			int totCouponCount = couponsDao.getDynamicCoupons();
			logger.info("totalDynamicCouponsCount is :: "+totCouponCount);
			if( totCouponCount == 0){
				logger.info(">>> No  Promo-codes exists for Upadting the Status");
				return;
			}
			
			int i = 0;
			while(i <= totCouponCount) {
				int k =100;
				UpdateCoupStatus(i, k);
				
				if(i == 0) i = k;	else  i= i+k;
				
				
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::" , e);
		}
	}

	private void UpdateCoupStatus(int j, int k) {
		
		CouponsDao couponsDao = null;
		CouponCodesDao couponCodesDao = null;
		CouponCodesDaoForDML couponCodesDaoForDML= null;
		ContactsDao contactsDao = null;
		try {
			couponsDao = (CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			couponCodesDao = (CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			couponCodesDaoForDML = (CouponCodesDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPONCODES_DAOForDML);
			contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		} catch (Exception e) {
			logger.error("Exception while creating couponCodesDao ..:",e);
		}
		
		List<Coupons> list =  couponsDao.getAllDynamicCoupons(j,k);
		if(list == null || list.size() == 0 ) {
			logger.info(">>> No  Promo-codes exists for Upadting the Status");
			return;
		}
		
		
		for (Coupons coupObj : list){
			
			try{

			String expiryDetails = coupObj.getExpiryDetails();
			if(expiryDetails == null) continue;
			
			String [] strArr = expiryDetails.split(Constants.ADDR_COL_DELIMETER);//
			
			
			if(Constants.COUP_DYN_ISSUANCE.equals(strArr[0])){

				//couponCodesDao.updatePromoCodeStatus(coupObj, strArr[1]);
				couponCodesDaoForDML.updatePromoCodeStatus(coupObj, strArr[1]);
			}
			else if(Constants.COUP_DYN_BDAY.equals(strArr[0])){
				//couponCodesDao.updatePromoCodeStatusByBDay(coupObj, strArr[1]);
				couponCodesDaoForDML.updatePromoCodeStatusByBDay(coupObj, strArr[1]);
				//couponCodesDao.updatePromoCodeStatus(coupObj, strArr[1]);
				couponCodesDaoForDML.updatePromoCodeStatus(coupObj, strArr[1]);
			}
			else if(Constants.COUP_DYN_ANNV.equals(strArr[0])){
				
				
				//couponCodesDao.updatePromoCodeStatusByAnniversay(coupObj, strArr[1]);
				couponCodesDaoForDML.updatePromoCodeStatusByAnniversay(coupObj, strArr[1]);
				//couponCodesDao.updatePromoCodeStatus(coupObj, strArr[1]);
				couponCodesDaoForDML.updatePromoCodeStatus(coupObj, strArr[1]);
			}
			
			

		/*//	CouponCodes couponCodes = couponCodesDao.find(coupObj.getCouponId());

			if(couponCodes != null && couponCodes.getContactId() != null){

			//	Contacts contacts = contactsDao.find(couponCodes.getContactId());

				List<CouponCodes> codeList = couponCodesDao.getAllActiveCon(couponCodes);


				if("I".equals(strArr[0])){

					couponCodesDao.updatePromoStatus(coupObj, numOfDays);
				}
				else if("B".equals(strArr[0])){
					
					for (CouponCodes couponCodes2 : codeList) {
						
						Contacts contacts = contactsDao.findById(couponCodes2.getContactId());
						Calendar bday = contacts.getBirthDay();
						couponCodesDao.updateCouponByBday(couponCodes2, bday, numOfDays);
					}

				}
				else if("A".equals(strArr[0])){
					
					for (CouponCodes couponCodes2 : codeList) {
						
						Contacts contacts = contactsDao.findById(couponCodes2.getContactId());
						Calendar annv = contacts.getAnniversary();
						couponCodesDao.updateCouponByAnnv(couponCodes2, annv, numOfDays);
					}

				}

			}//if couponCodes != null && couponCodes.getContactId() != null
*/		} catch (Exception e){
			logger.error("Exception ::::" , e);
		}
		}//for-loop
		
	}

}
