package com.optculture.launchpad.services;

import com.optculture.launchpad.repositories.CouponCodesRepository;
import com.optculture.launchpad.repositories.CouponsRepository;
import com.optculture.launchpad.utility.CustomCouponObj;
import com.optculture.shared.entities.promotion.CouponCodes;
import com.optculture.shared.entities.promotion.Coupons;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//@NoArgsConstructor
@Service
public class CouponsProvider {

	public static Hashtable<Long, CustomCouponObj> couponCodesMap = new Hashtable<Long, CustomCouponObj>();

	public static BlockingQueue<CouponCodes> usedCouponCodesQue = new LinkedBlockingQueue<>();

	private static CouponsProvider couponsProvider;
	@Autowired
	private CouponCodegenerator couponCodegenerator;
	private static String THREAD_LOCK="Thread Lock";

	public static CouponsProvider getCouponsProvider(){
		if(couponsProvider==null){
			couponsProvider=new CouponsProvider();
		}
		return couponsProvider;
	}
	public CouponsProvider(){}


	Logger logger = LoggerFactory.getLogger(CouponsProvider.class);

	@Autowired
	CouponsRepository couponsRepository;

	@Autowired
	CouponCodesRepository couponCodesRepository;

//	public CouponsProvider(CouponCodesRepository couponCodesRepository) {
//		this.couponCodesRepository = couponCodesRepository;
//	}


	public String getAlreadyIssuedCoupon(Coupons coupon, Long contactId) {
		int numOfDays=0;
		String expiryType=coupon.getExpiryType();
		List<CouponCodes> couponCodesList = couponCodesRepository.findByCouponIdCouponIdAndContactIdAndStatus(coupon.getCouponId(), contactId, "Active");
		if(expiryType.equalsIgnoreCase("D")){
			String expiryDetails = coupon.getExpiryDetails();
			if (expiryDetails != null) {
				String arr[] = expiryDetails.split(";=;");
				try {
					numOfDays = Integer.parseInt(arr[1]);
					LocalDateTime validPeriodTime=LocalDateTime.now().minusDays(numOfDays);
					for(CouponCodes couponCode:couponCodesList){
						if(couponCode.getIssuedOn().isAfter(validPeriodTime)){
							return couponCode.getCouponCode();
						}
					}
					return  null;
				} catch (Exception e) {
					logger.info("Exception while extracting dynamic expiry ", e);
				}
			}
			return  null;
		}
		else if(!couponCodesList.isEmpty()){
			return couponCodesList.get(0).getCouponCode();
		}
		return null;
	}

	public  String getNextCouponCode(Coupons coupon, String campaignName, String campaignType,
												 String issuedTo, Long sentId, String smsContent, Long contactId, boolean flushToDB) {
			Long couponId=coupon.getCouponId();
		CustomCouponObj customCouponObj = couponCodesMap.get(couponId); // custobj has coupon and queue of couponcodes
		BlockingQueue<CouponCodes> availbleQue=null;

		if (customCouponObj != null) {
		 	availbleQue= customCouponObj.getCouponCodesQue(); // generated couponcodes stored que
		}
		else { //not intialized queue
			logger.info(" coupon value is" + coupon);

			logger.info(" coupon genet type is" + coupon.getCouponGeneratedType());

			logger.info(" coupon  autoincr check is" + coupon.getAutoIncrCheck());


			synchronized (THREAD_LOCK) {
				customCouponObj = couponCodesMap.get(couponId);
				//double check before queue intialization
				if(customCouponObj==null) {
					//queue creation and coupon generation
					couponCodegenerator.intializeQueue(coupon, couponId);
				}
			}

			customCouponObj=couponCodesMap.get(couponId);
			availbleQue=customCouponObj.getCouponCodesQue();
		}
		try {
			CouponCodes tempCC = availbleQue.poll(2L, TimeUnit.MINUTES);
			if(tempCC==null){
				logger.info("No couponCode found skipping this Coupon");
				return null;
			}
			else {
				logger.info("couponCode :"+tempCC.getCouponCode());
				tempCC.setCampaignName(campaignName);
				tempCC.setCampaignType(campaignType);
				tempCC.setIssuedTo(issuedTo);
				tempCC.setStatus("Active");
				tempCC.setSentId(sentId);
				tempCC.setIssuedOn(LocalDateTime.now());
				tempCC.setContactId(contactId);
				usedCouponCodesQue.put(tempCC);
				return tempCC.getCouponCode();
			}

		}catch (Exception e){
			logger.info("Exception while getting couponCode",e);
		}
		return null;
	}

	@Scheduled(cron = "*/20 * * * * *")
	public synchronized void saveCouponCodes(){
		logger.info("writing issued coupon codes to DB");
		BlockingQueue<CouponCodes> couponCodesQue=new LinkedBlockingQueue<>();
		try {
			logger.info("used couponsQueue size"+usedCouponCodesQue.size());
			if(usedCouponCodesQue.size()>0){
				usedCouponCodesQue.drainTo(couponCodesQue);
				if (!couponCodesQue.isEmpty()) {
					couponCodesRepository.saveAll(couponCodesQue);
				}
			}
		}catch (Exception e){
			logger.debug("Exception while saving issued couponCodes",e);
		}
	}
}
