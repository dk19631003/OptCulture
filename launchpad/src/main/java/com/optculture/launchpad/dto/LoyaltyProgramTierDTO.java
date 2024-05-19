package com.optculture.launchpad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoyaltyProgramTierDTO {

	private String tierName;
	private String programTierName;
	private String tierType;
	private String rewardActivationPeriod;
	private String membershipExpirationDate;
	private String rewardExpirationPeriod;
	private String rewardExpiringValue;
	private String giftAmountExpiringValue;

}
