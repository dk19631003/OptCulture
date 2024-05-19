package com.optculture.app.dto.coupons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferralCodesDto {
    String referralCode;
    Long pointsEarned;
    Long redeemedCount;
    Boolean refProgram;
}
