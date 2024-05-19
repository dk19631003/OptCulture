package org.mq.optculture.business.helper.DRToLty;

public class DRToLoyaltyExtractionRedemption {
	private String redemptionAmount;
	private String loyaltyRedeemReversal;
	private String redemptionAsDiscount;
	private boolean discountSpreaded;
	public boolean isDiscountSpreaded() {
		return discountSpreaded;
	}

	public void setDiscountSpreaded(boolean discountSpreaded) {
		this.discountSpreaded = discountSpreaded;
	}
	public String getRedemptionAsDiscount() {
		return redemptionAsDiscount;
	}

	public void setRedemptionAsDiscount(String redemptionAsDiscount) {
		this.redemptionAsDiscount = redemptionAsDiscount;
	}
	public String getRedemptionAmount() {
		return redemptionAmount;
	}

	public void setRedemptionAmount(String redemptionAmount) {
		this.redemptionAmount = redemptionAmount;
	}

	public String getLoyaltyRedeemReversal() {
		return loyaltyRedeemReversal;
	}

	public void setLoyaltyRedeemReversal(String loyaltyRedeemReversal) {
		this.loyaltyRedeemReversal = loyaltyRedeemReversal;
	}
}
