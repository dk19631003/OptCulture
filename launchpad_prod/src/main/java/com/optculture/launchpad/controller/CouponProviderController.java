package com.optculture.launchpad.controller;


import com.optculture.launchpad.repositories.CouponsRepository;
import com.optculture.launchpad.services.CouponsProvider;
import com.optculture.shared.entities.promotion.Coupons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Optional;

//@RestController
//@RequestMapping("/couponProvider.mqrm")
@Controller
public class CouponProviderController {

    private final Logger logger = LoggerFactory.getLogger(CouponProviderController.class);
    @Autowired
    private CouponsProvider couponsProvider;
    @Autowired
    CouponsRepository couponsRepository;

   // @PostMapping
    public  String generateCoupon(Long couponId,String issuedTo,String channelType,Long cid) {

        if (couponId == null) {
            if (logger.isInfoEnabled()) logger.info("required param is null...");
            return null;
        }

        if (couponsProvider == null) {
            couponsProvider=CouponsProvider.getCouponsProvider();
            if (couponsProvider == null) {
                if (logger.isInfoEnabled()) logger.info("No Coup provider found....");
                return null;
            }
        }

		String campaignType = "SMS";
		if(channelType != null ){
			campaignType = channelType;
		}
		String value ="";

        Optional<Coupons> couponOpt = couponsRepository.findById(couponId);
        if (couponOpt.isEmpty()) {
            return null;
        }
		
			logger.info("cid is"+cid);
        if(couponOpt.get().getCouponGeneratedType().equalsIgnoreCase("single")){
            return couponOpt.get().getCouponCode();
        }
			if(cid != null) {
				
				logger.info("cid not null block"+cid);

				value = couponsProvider.getAlreadyIssuedCoupon(couponOpt.get(), cid);
				logger.info("value from issuedone is"+value);
			
			} //TODO camp name
			if(value == null || value.isEmpty()) {
                logger.info("coupon code not already issued for "+cid);

                value = couponsProvider.getNextCouponCode(couponOpt.get(), null, campaignType, issuedTo, null, null, cid, true);

                logger.info("coupon code issued for "+cid +" is "+value);
            }
			logger.info("issued to  from Scheduler is::::"+issuedTo);
		
			if(value == null) value ="";

		 return value;
    }


}
