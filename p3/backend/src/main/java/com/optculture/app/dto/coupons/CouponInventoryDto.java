package com.optculture.app.dto.coupons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
@Data
@RequiredArgsConstructor
public class CouponInventoryDto {
    String couponName;
    String couponCode;
    LocalDateTime expiryDate;
    String expiryDetails;
    String description;
    Long couponCodeId;
    Long couponId;
    String couponType;
}
