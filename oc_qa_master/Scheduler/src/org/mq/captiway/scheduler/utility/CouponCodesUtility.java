package org.mq.captiway.scheduler.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CouponCodes;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.CouponCodesDaoForDML;
import org.mq.captiway.scheduler.dao.ReferralcodesIssuedDao;
import org.mq.captiway.scheduler.dao.ReferralcodesIssuedDaoForDML;
import org.mq.captiway.scheduler.beans.ReferralcodesIssued;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.zkoss.zkplus.spring.SpringUtil;

	public class CouponCodesUtility extends Thread implements ApplicationContextAware {
		
		private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
		private ApplicationContext context;
		
		CouponCodesDao couponCodesDao;
		ReferralcodesIssuedDao referralcodesDao;
		ReferralcodesIssuedDaoForDML referralcodesDaoForDML;
		CouponCodesDaoForDML couponCodesDaoForDML;
		Coupons coupon;
		
		@Override
		public void setApplicationContext(ApplicationContext context) throws BeansException {
			this.context=context;
		}
		
		
		public CouponCodesUtility(ApplicationContext context, Coupons coupon) {
			this.context = context;
			this.coupon = coupon;
			try {
				couponCodesDao = (CouponCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
				couponCodesDaoForDML = (CouponCodesDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPONCODES_DAOForDML);
				referralcodesDaoForDML = (ReferralcodesIssuedDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.REFERRALCODES_DAOFORDML);

		
			} catch (Exception e) {
				logger.error("Exception ", e);
			}
			if(logger.isDebugEnabled()) logger.debug("couponCodesDao=="+couponCodesDao);
		}

		public void run() {
			
			try {
				if(logger.isDebugEnabled()) logger.debug("Thread Starting::"+Thread.currentThread().getName()+"_"+System.currentTimeMillis());
				boolean refcode = coupon.getUseasReferralCode();
			
				logger.info("refcode value is"+refcode);	

				if(refcode) {
					
					logger.info("entered ref gen block");	

					generateReferralCodes();
					return;
				}
				generateCouponCodes();
				
				
				if(logger.isDebugEnabled()) logger.debug("Thread Finished::"+Thread.currentThread().getName()+"_"+System.currentTimeMillis());
			} catch (Exception e) {
				logger.error("Exception ::::", e);
			}
		}
		
		//Generates coupon codes for random or multiple generation type for each contact
		public synchronized boolean generateCouponCodes() {
			try {
				if(logger.isDebugEnabled()) logger.debug("coupon="+coupon);
				if(logger.isDebugEnabled()) logger.debug(" Promo-code id="+coupon.getCouponId());
				
				if(coupon.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_SINGLE) &&
						coupon.getAutoIncrCheck()==true) {
					if(logger.isDebugEnabled()) logger.debug(" Promo-code type single no need to generate.");
					return true;
				}
				
				long existCount = couponCodesDao.getCouponCodeCountByStatus(coupon.getCouponId().longValue(), "All");
				
				Long genQty = coupon.getTotalQty();
				
				if(genQty==null) {
					if(logger.isDebugEnabled()) logger.debug(" Promo-code Total qty. is null");
					return false;
				}
				
				if(genQty.longValue() <= existCount) {
					return false;
				}
				
				
				long codesToGenerate = genQty.longValue()-existCount;
				long organizationId = coupon.getOrgId().longValue();
				
				List<CouponCodes> ccList = new ArrayList<CouponCodes>();
				CouponCodes ccObj=null;
				
				for (int i = 0; i < codesToGenerate; i++) {
					
					String couponCode = null;
					 // Assign the CouponCode from Coupon Object 
					if(coupon.getCouponCode()!=null && coupon.getAutoIncrCheck()==false) {
						couponCode = coupon.getCouponCode();
					}
					if(coupon.getCoupCodeGenType() != null && coupon.getCoupCodeGenType().equals("Numerical")) {
						
						Random rnd = new Random();
					    int number = rnd.nextInt(999999);

					    // this will convert any number sequence into 6 character.
					    couponCode = String.format("%06d", number);
					}else {
						
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
					}
					
					ccObj = new CouponCodes();
					ccObj.setOrgId(organizationId);
					ccObj.setCouponId(coupon);
					ccObj.setStatus(Constants.COUP_CODE_STATUS_INVENTORY);
					ccObj.setCouponCode(couponCode);
					
					ccList.add(ccObj);
					
					if(ccList.size()>200) {
						//couponCodesDao.saveByCollection(ccList);
						couponCodesDaoForDML.saveByCollection(ccList);
						ccList.clear();
					}
				} // for i
				
				if(ccList.size() > 0) {
					//couponCodesDao.saveByCollection(ccList);
					couponCodesDaoForDML.saveByCollection(ccList);
					ccList.clear();
				}

				return true;
			} catch (Exception e) {
				logger.error("Exception in generating the  Promo-codes.", e);
				return false;
			}	
			
					
		} // End generateCouponCodes()
		
		
		
		
		public boolean generateReferralCodes() {
			try {
				if(logger.isDebugEnabled()) logger.debug("coupon="+coupon);
				if(logger.isDebugEnabled()) logger.debug(" Promo-code id="+coupon.getCouponId());
				logger.info("entering refgen method");	
				
				Long genQty = coupon.getTotalQty();
				logger.info("genQty value is"+genQty);	
				

				if(genQty==null) {
					
					logger.info("entered genqty");	

					if(logger.isDebugEnabled()) logger.debug(" Promo-code Total qty. is null");
					return false;
				}
				
				long codesToGenerate = genQty.longValue();
				
				long organizationId = coupon.getOrgId().longValue();
				
				long userid=coupon.getUserId().longValue();
				
				List<ReferralcodesIssued> refccList = new ArrayList<ReferralcodesIssued>();
				
				ReferralcodesIssued refccObj=null;
				for (int i = 0; i < codesToGenerate; i++) {
						
					String referralCode = null;
					 // Assign the CouponCode from Coupon Object 
					if(coupon.getCouponCode()!=null && coupon.getAutoIncrCheck()==false) {
						
						referralCode = coupon.getCouponCode();
					}
					if(coupon.getCoupCodeGenType() != null && coupon.getCoupCodeGenType().equals("Numerical")) {
						
						Random rnd = new Random();
					    int number = rnd.nextInt(999999);

					    // this will convert any number sequence into 6 character.
					    referralCode = String.format("%06d", number);
					}else {
					
					//Generate referral codes
					List<String> md5List = null;
					while(referralCode == null) {
						
						md5List = Utility.couponGenarationCode(organizationId + System.nanoTime()+"", 8);
						
						Iterator<String> iterator = md5List.iterator();
						while (iterator.hasNext()) {
							String code = iterator.next();
							if(couponCodesDao.testForCouponCodes(code, organizationId) == null) {
								referralCode = code;
								break;
							} 
						}//while 
					
					}//while 
				}
					refccObj = new ReferralcodesIssued();
					refccObj.setOrgId(organizationId);
					refccObj.setUserId(userid);
					refccObj.setCouponId(coupon);
					refccObj.setStatus(Constants.COUP_CODE_STATUS_INVENTORY);
					refccObj.setRefcode(referralCode);
					refccList.add(refccObj);
					
					if(refccList.size() > 0) {
						
						logger.info("refccList value is "+refccList.size());	
						referralcodesDaoForDML.saveByCollection(refccList);
						refccList.clear();
					}
				} // for i
				
			
				return true;
			} catch (Exception e) {
				logger.error("Exception in generating the  Promo-codes.", e);
				return false;
			}	
			
					
		} // End generateCouponCodes()
		
		
		
		
		
		
		
		
		
	}//End class
