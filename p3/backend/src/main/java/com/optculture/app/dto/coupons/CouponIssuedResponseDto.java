package com.optculture.app.dto.coupons;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CouponIssuedResponseDto {
    String couponCode;
    String couponStatus;
    String coupCodeStatus;
    LocalDateTime couponExpiryDate;
    String description;
    LocalDateTime coupCodeExpiryDate;
    LocalDateTime issuedOn;
    String expiryDetails;
}
