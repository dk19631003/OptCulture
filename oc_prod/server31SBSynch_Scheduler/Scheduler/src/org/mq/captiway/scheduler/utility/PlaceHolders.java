package org.mq.captiway.scheduler.utility;

public interface PlaceHolders {
	String PH_SENT_ID = "|^sentId^|";
	String PH_CAMPAIGN_ID = "|^campaignId^|";
	String PH_CONTACT_ID = "|^contactId^|";
	String PH_USER_ID = "|^userId^|";
	String PH_UNSUBSCRIBE_URL = "|^unsubUrl^|";
	String PH_OPEN_TRACK_URL = "|^openTracKUrl^|";
	String PH_SENDER_ADDR = "|^senderAddress^|";
	String PH_SENDER_EMAIL = "|^senderEmail^|";
	String PH_ORGANIZATION_UNITANDNAME = "|^orgUnitAndName^|";
	String PH_CAMPAIGN_REPORT_ID = "|^crId^|";
	String PH_CLICK_URL = "|^clickUrl^|";
	String DIV_CONTENT = "|^divContent^|";
	String PH_OPT_IN_REP_ID = "|^optRepId^|";
	String PH_CAMPAIGN_ADDRESS_CONTACT_HOMESTORE = "|^GEN_ContactHomeStore^|";
	String PH_CAMPAIGN_ADDRESS_CONTACT_LAST_PURCHASED_STORE = "|^GEN_ContactLastPurchasedStore^|";
	String PH_PROCESSED_CAMPAIGN_ADDRESS_CONTACT_HOMESTORE = "[GEN_ContactHomeStore]";
	String PH_PROCESSED_CAMPAIGN_ADDRESS_CONTACT_LAST_PURCHASED_STORE = "[GEN_ContactLastPurchasedStore]";
	String PH_UPDATE_SUBSCIRIPTION_URL = "|^updateSubSUrl^|";
	String PH_UPDATE_SUBSCIRIPTION_TEXT = "|^UPDATESUBTEXT^|";
	
	
	 /**
	  * Campaign Place holders Type
	  */
	 String CAMPAIGN_PH_STORENAME = "storeName";
	 String CAMPAIGN_PH_STOREMANAGER = "storeManager";
	 String CAMPAIGN_PH_STOREEMAIL = "storeEmail";
	 String CAMPAIGN_PH_STOREPHONE = "storePhone";
	 String CAMPAIGN_PH_STORESTREET = "storeStreet";
	 String CAMPAIGN_PH_STORECITY = "storeCity";
	 String CAMPAIGN_PH_STORESTATE = "storeState";
	 String CAMPAIGN_PH_STOREZIP = "storeZip";
	 
	 String CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS = "lastPurchaseStoreAddress";
	 String CAMPAIGN_PH_LASTPURCHASE_DATE = "lastPurchaseDate";
	 String CAMPAIGN_PH_LASTPURCHASE_AMOUNT = "lastPurchaseAmount";
	 String CAMPAIGN_PH_UNSUBSCRIBE_LINK = "unsubscribeLink";
	 String CAMPAIGN_PH_WEBPAGE_VERSION_LINK = "webPageVersionLink";
	 
	 String CAMPAIGN_PH_STARTSWITH_STORE = "store";
	 String CAMPAIGN_PH_STARTSWITH_LOYALTY = "loyalty";
	 String CAMPAIGN_PH_STARTSWITH_LASETPURCHASE = "lastPurchase";
	 String CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE = "contactlastpurchasedstore";
	 String CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE="contacthomestore";
	

	 /**
	  * Loyalty Place Holders Type
	  */
	 //Old Loyalty Place Holders.
	 String CAMPAIGN_PH_GIFTCARD_BALANCE = "loyaltygiftcardBalance";
	 String CAMPAIGN_PH_LOYALTY_CARDNUMBER = "loyaltyCardNumber";
	 String CAMPAIGN_PH_LOYALTYCARDPIN = "loyaltyCardPin";				//same as SparkBase
	 String CAMPAIGN_PH_LOYALTY_REFRESHEDON ="loyaltyRefreshedOn";			//same as SparkBase

	 //New loyalty place holders
	 //TODO String CAMPAIGN_PH_LOYALTY_URL="loyaltyUrl";								
	 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_NUMBER="loyaltyMembershipNumber";
	 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PIN="loyaltyMembershipPin";
	 String CAMPAIGN_PH_LOYALTY_MEMBER_TIER="loyaltyMemberTier";
	 String CAMPAIGN_PH_LOYALTY_MEMBER_STATUS="loyaltyMemberStatus";
	 String CAMPAIGN_PH_LOYALTY_ENROLLMENT_DATE ="loyaltyEnrollmentDate";
	 String CAMPAIGN_PH_LOYALTY_ENROLLMENT_SOURCE ="loyaltyEnrollmentSource";
	 String CAMPAIGN_PH_LOYALTY_ENROLLMENT_STORE ="loyaltyEnrollmentStore";
	 String CAMPAIGN_PH_LOYALTY_REGISTERED_PHONE ="loyaltyRegisteredPhone";
	 String CAMPAIGN_PH_LOYALTY_POINTS_BALANCE="loyaltyPointsBalance";
	 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_CURRENCY_BALANCE="loyaltyMembershipCurrencyBalance";
	 String CAMPAIGN_PH_LOYALTY_GIFT_BALANCE ="loyaltyGiftBalance";
	 String CAMPAIGN_PH_LOYALTY_HOLD_BALANCE  ="loyaltyHoldBalance";
	 String CAMPAIGN_PH_LOYALTY_REWARD_ACTIVATION_PERIOD ="loyaltyRewardActivationPeriod";
	 String CAMPAIGN_PH_LOYALTY_LAST_EARNED_VALUE ="loyaltyLastEarnedValue";
	 String CAMPAIGN_PH_LOYALTY_LAST_REDEEMED_VALUE ="loyaltyLastRedeemedValue";
	 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PASSWORD = "loyaltyMembershipPassword";
	 String CAMPAIGN_PH_LOYALTY_LOGIN_URL ="loyaltyLoginUrl";
	// String CAMPAIGN_PH_ORGANIZATION_NAME ="organizationName";
	 String CAMPAIGN_PH_LOYALTY_REWARD_EXPIRATION_PERIOD ="loyaltyRewardExpirationPeriod";
	 String CAMPAIGN_PH_LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD ="loyaltyGiftAmountExpirationPeriod";
	 String CAMPAIGN_PH_LOYALTY_MEMBERSHIP_EXPIRATION_DATE ="loyaltyMembershipExpirationDate";
	 String CAMPAIGN_PH_LOYALTY_GIFT_CARD_EXPIRATION_DATE ="loyaltyGiftCardExpirationDate";
	 String CAMPAIGN_PH_LOYALTY_LAST_BONUS_VALUE ="loyaltyLastBonusValue";
	 String CAMPAIGN_PH_REWARD_EXPIRING_VALUE ="loyaltyRewardExpiringValue";
	 String CAMPAIGN_PH_GIFT_AMOUNT_EXPIRING_VALUE ="loyaltyGiftAmountExpiringValue";
	 
	 //Place holder for Social 
	 String CAMPAIGN_PH_TWEET_URL = "shareOnTwitter";
	 String CAMPAIGN_PH_FACEBOOK_URL = "shareOnFacebook";
 	 String CAMPAIGN_PH_FORWRADFRIEND_LINK = "forwardToFriend";
 	 
 	String CAMPAIGN_PH__UPDATE_PREFERENCE_LINK = "updatePreferenceLink";
	
	
}
