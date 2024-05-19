package org.mq.captiway.scheduler.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.apache.commons.lang.StringUtils;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.captiway.scheduler.dao.CouponCodesDaoForDML;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.beans.CouponCodes;

import java.util.List;
import java.util.Iterator;

public class CouponCodesGeneration {
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private ApplicationContext context;
	
	CouponsDao couponsdao;
	CouponCodesDao couponCodesDao;
	CouponCodesDaoForDML couponCodesDaoForDML;
	
	public CouponCodesGeneration(ApplicationContext context) {
		this.context = context;
		couponsdao = (CouponsDao)context.getBean("couponsDao");
		couponCodesDao = (CouponCodesDao)context.getBean("couponCodesDao");
		couponCodesDaoForDML = (CouponCodesDaoForDML)context.getBean("couponCodesDaoForDML");
	}

	//Generates coupon codes for random or multiple generation type for each contact
	public String generateCouponCodes(String couponPlaceHolder, long sentId, String campaignType, String issuedTo, String campName ){
		
		try {
			//Check whether place holder is of type coupon		
			if(couponPlaceHolder==null ||  !couponPlaceHolder.startsWith("CC_") || campaignType==null) {
				return null;
			}
				
			String coupon_ph = couponPlaceHolder.trim();
			String couponsPh = coupon_ph;
			
			Coupons coupons=null;
			
/*			couponsPh = StringUtils.stripStart(coupon_ph, "|^");
			couponsPh = StringUtils.stripEnd(couponsPh, "^|");
*/			 
		//	couponsPh = couponsPh.replace("|^", "").replace("^|", "");
			
			String[] cph = couponsPh.split("_");
			
			if(cph.length != 3) {
				if(logger.isDebugEnabled()) logger.debug("Invalid format :"+couponPlaceHolder);
				return null;
			}
			
			long couponId = Long.parseLong(cph[1]);

			//Get coupon object from coupon table by passing couponId
		
			coupons = couponsdao.findById(couponId);
			if(coupons == null) {
				if(logger.isDebugEnabled()) logger.debug(" Promo-code not found for the placeholder :"+couponPlaceHolder);
				return null;
			}
			
			
			String couponCode = null;
			long organizationId = 0;
			String status = Constants.COUP_GENT_CODE_STATUS_ACTIVE;
			
			//Coupon generation type is single
			if(coupons.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_SINGLE)) {
				couponCode = coupons.getCouponCode();
				organizationId = coupons.getOrgId();
				//status = coupons.getStatus();
			
			}
			else if(coupons.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_MULTIPLE)) {  
				//Coupon generation type is random or multiple
				organizationId = coupons.getOrgId();
				//status = coupons.getStatus();
				
				//Generate coupon codes
				List<String> md5List = null;
				while(couponCode == null) {
					
					md5List = Utility.couponGenarationCode(organizationId + System.nanoTime()+"", 8);
					
					Iterator<String> iterator = md5List.iterator();
					while (iterator.hasNext()) {
						String code = iterator.next();
						if(couponCodesDao.testForCouponCodes(code, organizationId) == null) {
							couponCode = code;
							break;
						} 
					}//while 
				
				}//while 
				
			}//else 

			
			//prepare coupon code record
			CouponCodes couponCodesObject = new CouponCodes();
			couponCodesObject.setCouponId(coupons);
			couponCodesObject.setCouponCode(couponCode);
			couponCodesObject.setSentId(sentId);
			couponCodesObject.setCampaignType(campaignType);
			couponCodesObject.setOrgId(organizationId);
			couponCodesObject.setStatus(status);
			couponCodesObject.setIssuedTo(issuedTo);
			couponCodesObject.setCampaignName(campName);
			

			//insert coupon code
			//couponCodesDao.saveOrUpdate(couponCodesObject);
			couponCodesDaoForDML.saveOrUpdate(couponCodesObject);
			return couponCode;
		} catch (Exception e) {
			logger.error("Exception in generating the Promo-code.", e);
			return null;
		}	
		
				
	} // End generateCouponCodes()
	
	
}//End class
