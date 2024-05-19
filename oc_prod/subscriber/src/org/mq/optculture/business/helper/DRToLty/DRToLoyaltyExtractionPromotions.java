package org.mq.optculture.business.helper.DRToLty;

import java.util.List;

import org.mq.optculture.model.digitalReceipt.Items;

public class DRToLoyaltyExtractionPromotions {

	private String CouponCode;
	private String DiscountAmount;
	private String itemCodeInfo;
	private String UsedLoyaltyReward;
	private String RewardValuecode;
	private List<Items> Items;
	private String DiscountType; //Item or Receipt
	
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
	public DRToLoyaltyExtractionPromotions(){}
	public DRToLoyaltyExtractionPromotions(String couponCode, String discountAmount, String usedLoyaltyReward,
			String rewardValuecode) {
		super();
		CouponCode = couponCode;
		DiscountAmount = discountAmount;
		UsedLoyaltyReward = usedLoyaltyReward;
		RewardValuecode = rewardValuecode;
	}
	public DRToLoyaltyExtractionPromotions(String couponCode, String discountAmount, String usedLoyaltyReward,
			String rewardValuecode,List<Items> items) {
		super();
		CouponCode = couponCode;
		DiscountAmount = discountAmount;
		UsedLoyaltyReward = usedLoyaltyReward;
		RewardValuecode = rewardValuecode;
		Items = items;
	}
	public String getItemCodeInfo() {
		return itemCodeInfo;
	}
	public void setItemCodeInfo(String itemCodeInfo) {
		this.itemCodeInfo = itemCodeInfo;
	}
	public String getDiscountType() {
		return DiscountType;
	}
	public void setDiscountType(String discountType) {
		DiscountType = discountType;
	}

}
