package com.optculture.launchpad.utility;

import com.optculture.shared.entities.promotion.CouponCodes;
import com.optculture.shared.entities.promotion.Coupons;
import lombok.Data;

import java.util.concurrent.BlockingQueue;
@Data
public class CustomCouponObj {
    BlockingQueue<CouponCodes> couponCodesQue;
    Coupons coupon;

    public CustomCouponObj(BlockingQueue<CouponCodes> couponCodesQue, Coupons coupon) {
        this.couponCodesQue = couponCodesQue;
        this.coupon = coupon;
    }
}
