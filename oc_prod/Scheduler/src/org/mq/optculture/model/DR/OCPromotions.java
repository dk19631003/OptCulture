package org.mq.optculture.model.DR;

public class OCPromotions {
	private String DiscountType;
	private String ItemCode;
	private String ItemDiscount;
	private String DiscountAmount;
	//private String Quantity;
	private String QuantityDiscounted;
	private String CouponCode;
	private String RewardRatio;
	public String getDiscountType() {
		return DiscountType;
	}
	public void setDiscountType(String discountType) {
		DiscountType = discountType;
	}
	public String getItemCode() {
		return ItemCode;
	}
	public void setItemCode(String itemCode) {
		ItemCode = itemCode;
	}
	public String getItemDiscount() {
		return ItemDiscount;
	}
	public void setItemDiscount(String itemDiscount) {
		ItemDiscount = itemDiscount;
	}
	public String getDiscountAmount() {
		return DiscountAmount;
	}
	public void setDiscountAmount(String discountAmount) {
		DiscountAmount = discountAmount;
	}
	/*public String getQuantity() {
		return Quantity;
	}
	public void setQuantity(String quantity) {
		Quantity = quantity;
	}*/
	public String getQuantityDiscounted() {
		return QuantityDiscounted;
	}
	public void setQuantityDiscounted(String quantityDiscounted) {
		QuantityDiscounted = quantityDiscounted;
	}
	public String getCouponCode() {
		return CouponCode;
	}
	public void setCouponCode(String couponCode) {
		CouponCode = couponCode;
	}
	public String getRewardRatio() {
		return RewardRatio;
	}
	public void setRewardRatio(String rewardRatio) {
		RewardRatio = rewardRatio;
	}
}
