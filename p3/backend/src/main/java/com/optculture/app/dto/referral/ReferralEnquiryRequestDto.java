package com.optculture.app.dto.referral;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferralEnquiryRequestDto {
    @JsonProperty
    private ReferralCodeInfoDto REFERRALCODEINFO;
    @JsonProperty
    private UserDetailsDto USERDETAILS;
}
