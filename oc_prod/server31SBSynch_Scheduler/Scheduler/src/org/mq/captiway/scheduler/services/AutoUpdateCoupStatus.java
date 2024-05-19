package org.mq.captiway.scheduler.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.dao.CouponsDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AutoUpdateCoupStatus  extends TimerTask implements ApplicationContextAware {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private ApplicationContext context;
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
	
	CouponsDao couponsDao;
	CouponsDaoForDML couponsDaoForDML;
	
	public void run() {
		
		try{
			logger.info("********Calling here ********* ");
			
			couponsDao = (CouponsDao)context.getBean("couponsDao");
			couponsDaoForDML = (CouponsDaoForDML)context.getBean("couponsDaoForDML");
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
			logger.error("Exception ::::" , e);
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
			String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
			Calendar currntDateCal = null;
			for (Coupons coupObj : list) {
				currntDateCal = userTZCalander(coupObj.getUserId(),serverTimeZoneVal);
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
				
				
			} // for
			
			
			if(modifyCoupStausLst.size() > 0) {
				logger.info(" finally :: "+modifyCoupStausLst.size()+" ::   Promo-code Obj status updated");
				//couponsDao.saveByCollection(modifyCoupStausLst);
				couponsDaoForDML.saveByCollection(modifyCoupStausLst);
				modifyCoupStausLst.clear();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::" , e);
		}
		
	}  // autoUpdateCoupStatus
	
	
	private Calendar userTZCalander(Long userId,String serverTimeZoneVal){
		Calendar cal = Calendar.getInstance();
		try {
			if(userId == null) return cal;
			UsersDao userDao =(UsersDao) ServiceLocator.getInstance().getDAOByName("usersDao");
			Users user = userDao.findByUserId(userId);
			
			
			int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
			String timezoneDiffrenceMinutes = user.getClientTimeZone();
			int timezoneDiffrenceMinutesInt = 0;
			
			if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
			timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
			
			cal.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cal;
	}

}
