package org.mq.optculture.model.digitalReceipt;

import java.util.List;

public class Promotions {
	
	
	private String CouponCode;
	private String DiscountAmount;
	private String UsedLoyaltyReward;
	private String RewardValuecode;
	private List<Items> Items;
	public String getCouponCode() {
		return CouponCode;
	}
	public void setCouponCode(String couponCode) {
		CouponCode = couponCode;
	}
	public String getDiscountAmount() {
		return DiscountAmount;
	}
	public void setDiscountAmount(String discountAmount) {
		DiscountAmount = discountAmount;
	}
	public String getUsedLoyaltyReward() {
		return UsedLoyaltyReward;
	}
	public void setUsedLoyaltyReward(String usedLoyaltyReward) {
		UsedLoyaltyReward = usedLoyaltyReward;
	}
	public String getRewardValuecode() {
		return RewardValuecode;
	}
	public void setRewardValuecode(String rewardValuecode) {
		RewardValuecode = rewardValuecode;
	}
	public List<Items> getItems() {
		return Items;
	}
	public void setItems(List<Items> items) {
		Items = items;
	}
	

}
