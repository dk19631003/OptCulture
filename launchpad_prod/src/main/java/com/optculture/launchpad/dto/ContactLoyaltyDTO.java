package com.optculture.launchpad.dto;

import com.optculture.shared.entities.loyalty.LoyaltyProgramTier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ContactLoyaltyDTO {
	//=========================================================================================
	//                            DIRECT VALUES
	//=========================================================================================

	private String membershipNumber;
	private String mobilePhone;// loyalty registered mobile no.
	private String membershipType;// Card/Mobile
	private String enrollmentDate;
	private String membershipPin;
	private String enrollmentSource;
	private Double lifetimePoints; //total loyalty points earned(Lifetime points earned)\
	private String membershipPwd;
//	private String contactLoyaltyType;// loyalty_enrol_source :: optin medium - POS/Webform
	private String enrollmentStore;// loyalty-enrol store :: enroll store no in the enrollment request
//	private String enrollmentStore;
	private Double loyaltyPointBalance; // loyalty points balance
	private Double loyaltyCurrencyBalance; // loyalty amount balance :: loyalty membership currency balance
	private Double giftBalance; //gift balance  : loyaltygift balance.
	private Double lifeTimePurchaseValue;
	private Double cummulativePurchaseValue;
	private Double cummulativeReturnValue;
	private Double holdAmountBalance;
	private Double holdPointsBalance;
	private LoyaltyProgramTierDTO tier; // to access loyalty.tier
	
	

	
	
	private String membershipStatus; //loyalty membership status :: active/expired/suspended

	
	//==========================================================================================
	//						NEED TO COMPUTE
	//=========================================================================================
	
	/* Loyalty Last Threshold Level::|^GEN_loyaltyLastThresholdLevel / DEFAULT=Not Available^|
	 * Loyalty Member Tier::|^GEN_loyaltyMemberTier / DEFAULT=Not Available^|
	 * Loyalty Redeemable Currency Balance::|^GEN_loyaltyRC / DEFAULT=Not Available^|
	 * Loyalty Hold Balance::|^GEN_loyaltyHoldBalance / DEFAULT=Not Available^|
	 * Loyalty Reward Activation Period::|^GEN_loyaltyRewardActivationPeriod / DEFAULT=Not Available^|
	 * Loyalty Last Earned Value::|^GEN_loyaltyLastEarnedValue / DEFAULT=Not Available^|
	 * Loyalty Last Redeemed Value::|^GEN_loyaltyLastRedeemedValue / DEFAULT=Not Available^|
	 * Loyalty Membership Password::|^GEN_loyaltyMembershipPassword / DEFAULT=Not Available^|
	 * Loyalty Reward Expiration Period::|^GEN_loyaltyRewardExpirationPeriod / DEFAULT=Not Available^|
	 * Loyalty Gift Amount Expiration Period::|^GEN_loyaltyGiftAmountExpirationPeriod / DEFAULT=Not Available^|
	 * Loyalty Membership Expiration Date::|^GEN_loyaltyMembershipExpirationDate / DEFAULT=Not Available^|
	 * Loyalty Gift-card Expiration Date::|^GEN_loyaltyGiftCardExpirationDate / DEFAULT=Not Available^| 
	 * Loyalty Last Bonus Value::|^GEN_loyaltyLastBonusValue / DEFAULT=Not Available^| : involves loyalty_child
	 * Loyalty Reward Expiring Value::|^GEN_loyaltyRewardExpiringValue / DEFAULT=Not Available^|
	 * Loyalty Gift Amount Expiring Value::|^GEN_loyaltyGiftAmountExpiringValue / DEFAULT=Not Available^|
	 */

	private String holdBalance;
	private	Double redeemedCurrency;
	private String lastEarnedValue;
	private String lastRedeemedValue;
	private String membershipPassword;
	private String giftAmountExpirationPeriod;
	private String giftCardExpriationDate;
	private String lastBonusValue;
	private String lastThresholdLevel;
	private String giftAmountExpiringValue;
	
	}
	
