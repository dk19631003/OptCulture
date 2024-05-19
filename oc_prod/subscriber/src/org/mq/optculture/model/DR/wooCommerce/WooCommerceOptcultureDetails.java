package org.mq.optculture.model.DR.wooCommerce;

import java.util.List;


public class WooCommerceOptcultureDetails {
	
	private WooCommerceLoyaltyRedeem LoyaltyRedeem;
	private List<WooCommercePromotions> Promotions;
	public WooCommerceLoyaltyRedeem getLoyaltyRedeem() {
		return LoyaltyRedeem;
	}
	public void setLoyaltyRedeem(WooCommerceLoyaltyRedeem loyaltyRedeem) {
		LoyaltyRedeem = loyaltyRedeem;
	}
	public List<WooCommercePromotions> getPromotions() {
		return Promotions;
	}
	public void setPromotions(List<WooCommercePromotions> promotions) {
		Promotions = promotions;
	}
}
