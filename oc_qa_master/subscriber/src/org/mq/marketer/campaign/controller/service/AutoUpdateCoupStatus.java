package org.mq.marketer.campaign.controller.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.ReferralProgram;
import org.mq.marketer.campaign.beans.ReferralcodesIssued;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.CouponsDaoForDML;
import org.mq.marketer.campaign.dao.ReferralcodesIssuedDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.ReferralProgramDao;
import org.mq.marketer.campaign.dao.ReferralcodesIssuedDao;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AutoUpdateCoupStatus  extends TimerTask implements ApplicationContextAware {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private ApplicationContext context;
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
	
	CouponsDao couponsDao;
	ReferralProgramDao refprgmDao;
	
	ReferralcodesIssuedDao refcodesDao;
	
	CouponsDaoForDML couponsDaoForDML;
	
	ReferralcodesIssuedDaoForDML refcodesDaoForDML;
	
	public void run() {
		
		try{
			logger.info("********Calling here ********* ");
			
			couponsDao = (CouponsDao)context.getBean("couponsDao");
			couponsDaoForDML = (CouponsDaoForDML)context.getBean("couponsDaoForDML");
		
			refprgmDao = (ReferralProgramDao)context.getBean("referralProgramDao");

			
			int totalCouponsCount = couponsDao.getAllCouponsCount();
			logger.info("totalCouponsCount is :: "+totalCouponsCount);
			if( totalCouponsCount == 0){
				logger.info(">>> No  Promo-codes exists for Upadting the Status");
				return;
			}
			int i = 0;
			while(i <= totalCouponsCount) {
				int k =100;
				autoUpdateCoupStatus(i, k);
				
				if(i == 0) i = k;	else  i= i+k;
				
				
			}
			
			
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception ::" , e);
		}		
		
	} // run
	
	
	
	private void autoUpdateCoupStatus(int j, int k) {
		
		try {
			
			
			
			
			List<Coupons> list =  couponsDao.getAllCoupons(j,k);
		
			
			
			if(list == null || list.size() == 0 ) {
				logger.info(">>> No  Promo-codes exists for Upadting the Status");
				return;
			}
			
			List<Coupons> modifyCoupStausLst = new ArrayList<Coupons>();
			
			List<ReferralcodesIssued> modifyRefcodeStausLst = new ArrayList<ReferralcodesIssued>();

			
			
			Calendar currntDateCal = Calendar.getInstance();
			for (Coupons coupObj : list) {
				
			boolean useasrefcode = 	coupObj.isUseasReferralCode();
			
			if(!useasrefcode) {
				
				if(coupObj.getStatus().equals(Constants.COUP_STATUS_ACTIVE)) {			// CheckIf Coupon Status Active
					
					if(currntDateCal.after(coupObj.getCouponCreatedDate()) && currntDateCal.before(coupObj.getCouponExpiryDate())) {
						
						coupObj.setStatus(Constants.COUP_STATUS_RUNNING);
						modifyCoupStausLst.add(coupObj);
					}
					else if(currntDateCal.after(coupObj.getCouponExpiryDate())) {
						
						coupObj.setStatus(Constants.COUP_STATUS_EXPIRED);
						modifyCoupStausLst.add(coupObj);
					}
					
					
				} else  if(coupObj.getStatus().equals(Constants.COUP_STATUS_RUNNING)) {	// CheckIf Coupon Status Running
					
					if(currntDateCal.before(coupObj.getCouponCreatedDate())) {
						
						coupObj.setStatus(Constants.COUP_STATUS_ACTIVE);
						modifyCoupStausLst.add(coupObj);
					}
					else if(currntDateCal.after(coupObj.getCouponExpiryDate())) {
						
						coupObj.setStatus(Constants.COUP_STATUS_EXPIRED);
						modifyCoupStausLst.add(coupObj);
					}
					
				} else  if(coupObj.getStatus().equals(Constants.COUP_STATUS_PAUSED)) {	// CheckIf Coupon Status Paused
					
					if(currntDateCal.after(coupObj.getCouponExpiryDate())) {

						coupObj.setStatus(Constants.COUP_STATUS_EXPIRED);
						modifyCoupStausLst.add(coupObj);
					}
					
					
				} else if(coupObj.getStatus().equals(Constants.COUP_STATUS_EXPIRED)) {	// CheckIf Coupon Status Expired
					
					if(currntDateCal.before(coupObj.getCouponCreatedDate()) ) {
						
						coupObj.setStatus(Constants.COUP_STATUS_ACTIVE);
						modifyCoupStausLst.add(coupObj);
					}
					else if(currntDateCal.before(coupObj.getCouponExpiryDate())) {
						
						coupObj.setStatus(Constants.COUP_STATUS_RUNNING);
						modifyCoupStausLst.add(coupObj);
					}
					
				}
				
				
				if(modifyCoupStausLst.size() == 100) {
					logger.info("for Every 100 coup Obj status updated");
					//couponsDao.saveByCollection(modifyCoupStausLst);
					couponsDaoForDML.saveByCollection(modifyCoupStausLst);

					modifyCoupStausLst.clear();
				}
			
			}else {
				
				 
					ReferralProgram refprgmobj=refprgmDao.findReferalprogramByUserIdnew(coupObj.getUserId(),coupObj.getCouponId());
					
					ReferralcodesIssued refcodesobj=refcodesDao.findReferalprogramByUserIdnew(coupObj.getUserId(),coupObj.getCouponId());
					
					logger.info("refprgmobj value"+refprgmobj);
					logger.info("refcodesobj value"+refcodesobj);

					
					
				if(coupObj.getStatus().equals(Constants.COUP_STATUS_ACTIVE)) {			// CheckIf Coupon Status Active
					
				
						logger.info("refprgmobj start date value"+refprgmobj.getStartDate());	
						logger.info("refprgmobj end date value"+refprgmobj.getEndDate());	

						
						if(currntDateCal.after(refprgmobj.getStartDate()) && currntDateCal.before(refprgmobj.getEndDate())) {
						
						coupObj.setStatus(Constants.COUP_STATUS_RUNNING);
						
						coupObj.setUserCreatedDate(refprgmobj.getCreatedDate());
						coupObj.setCouponCreatedDate(refprgmobj.getStartDate());
						coupObj.setCouponExpiryDate(refprgmobj.getEndDate());
						modifyCoupStausLst.add(coupObj);
						
						refcodesobj.setStatus(Constants.COUP_STATUS_RUNNING);
						modifyRefcodeStausLst.add(refcodesobj);
						
						}
						else if(currntDateCal.after(refprgmobj.getEndDate())) {
						
						coupObj.setStatus(Constants.COUP_STATUS_EXPIRED);

						coupObj.setUserCreatedDate(refprgmobj.getCreatedDate());
						coupObj.setCouponCreatedDate(refprgmobj.getStartDate());
						coupObj.setCouponExpiryDate(refprgmobj.getEndDate());
						modifyCoupStausLst.add(coupObj);
						
						refcodesobj.setStatus(Constants.COUP_STATUS_EXPIRED);
						modifyRefcodeStausLst.add(refcodesobj);

						}
					
					
				} else  if(coupObj.getStatus().equals(Constants.COUP_STATUS_RUNNING)) {	// CheckIf Coupon Status Running
					
					
					
					if(currntDateCal.before(refprgmobj.getStartDate())) {
						
						coupObj.setStatus(Constants.COUP_STATUS_ACTIVE);
						coupObj.setUserCreatedDate(refprgmobj.getCreatedDate());
						coupObj.setCouponCreatedDate(refprgmobj.getStartDate());
						coupObj.setCouponExpiryDate(refprgmobj.getEndDate());
						modifyCoupStausLst.add(coupObj);
					
						refcodesobj.setStatus(Constants.COUP_STATUS_ACTIVE);
						modifyRefcodeStausLst.add(refcodesobj);

					}
					else if(currntDateCal.after(refprgmobj.getEndDate())) {
						
						coupObj.setStatus(Constants.COUP_STATUS_EXPIRED);
						coupObj.setUserCreatedDate(refprgmobj.getCreatedDate());
						coupObj.setCouponCreatedDate(refprgmobj.getStartDate());
						coupObj.setCouponExpiryDate(refprgmobj.getEndDate());
						modifyCoupStausLst.add(coupObj);
						
						refcodesobj.setStatus(Constants.COUP_STATUS_EXPIRED);
						modifyRefcodeStausLst.add(refcodesobj);

						//all the referal codes those r generated - under this discount codes must be expired.
						
					}
					
				} else  if(coupObj.getStatus().equals(Constants.COUP_STATUS_PAUSED)) {	// CheckIf Coupon Status Paused
					
					if(currntDateCal.after(refprgmobj.getEndDate())) {

						coupObj.setStatus(Constants.COUP_STATUS_EXPIRED);
						coupObj.setUserCreatedDate(refprgmobj.getCreatedDate());
						coupObj.setCouponCreatedDate(refprgmobj.getStartDate());
						coupObj.setCouponExpiryDate(refprgmobj.getEndDate());
						modifyCoupStausLst.add(coupObj);
					
						refcodesobj.setStatus(Constants.COUP_STATUS_EXPIRED);
						modifyRefcodeStausLst.add(refcodesobj);

					}
					
					
				} else if(coupObj.getStatus().equals(Constants.COUP_STATUS_EXPIRED)) {	// CheckIf Coupon Status Expired
					
					if(currntDateCal.before(refprgmobj.getStartDate()) ) {
						
						coupObj.setStatus(Constants.COUP_STATUS_ACTIVE);
						coupObj.setUserCreatedDate(refprgmobj.getCreatedDate());
						coupObj.setCouponCreatedDate(refprgmobj.getStartDate());
						coupObj.setCouponExpiryDate(refprgmobj.getEndDate());
						modifyCoupStausLst.add(coupObj);
					
						refcodesobj.setStatus(Constants.COUP_STATUS_ACTIVE);
						modifyRefcodeStausLst.add(refcodesobj);

					
					}
					else if(currntDateCal.before(refprgmobj.getEndDate())) {
						
						coupObj.setStatus(Constants.COUP_STATUS_RUNNING);
						coupObj.setUserCreatedDate(refprgmobj.getCreatedDate());
						coupObj.setCouponCreatedDate(refprgmobj.getStartDate());
						coupObj.setCouponExpiryDate(refprgmobj.getEndDate());
						modifyCoupStausLst.add(coupObj);
					
						refcodesobj.setStatus(Constants.COUP_STATUS_RUNNING);
						modifyRefcodeStausLst.add(refcodesobj);

					
					}
					
				}
				
				
				if(modifyCoupStausLst.size() == 100) {
					logger.info("for Every 100 coup Obj status updated");
					//couponsDao.saveByCollection(modifyCoupStausLst);
					couponsDaoForDML.saveByCollection(modifyCoupStausLst);

					modifyCoupStausLst.clear();
				}
				
				if(modifyRefcodeStausLst.size() == 100) {                    //need to do changes
					logger.info("for Every 100 coup Obj status updated");
					refcodesDaoForDML.saveByCollection(modifyRefcodeStausLst);
					modifyRefcodeStausLst.clear();
				}
				
			}
				
		} // for
			
			
			if(modifyCoupStausLst.size() > 0) {
				logger.info(" finally :: "+modifyCoupStausLst.size()+" ::   Promo-code Obj status updated");
				//couponsDao.saveByCollection(modifyCoupStausLst);
				couponsDaoForDML.saveByCollection(modifyCoupStausLst);
				modifyCoupStausLst.clear();
			}
			
			if(modifyRefcodeStausLst.size() > 0) {
				logger.info(" finally :: "+modifyRefcodeStausLst.size()+" ::   Ref-code Obj status updated");
				//couponsDao.saveByCollection(modifyCoupStausLst);
				refcodesDaoForDML.saveByCollection(modifyRefcodeStausLst);
				modifyRefcodeStausLst.clear();
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}
		
	}  // autoUpdateCoupStatus
	
}
