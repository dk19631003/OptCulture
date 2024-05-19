package com.optculture.app.dto.coupons;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IssueCouponRequest {
    String contactId;
    Long couponId;
    String mobile;
   String emailId;
   String expiryDetails;
}
