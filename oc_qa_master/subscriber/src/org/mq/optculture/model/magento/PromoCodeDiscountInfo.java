package org.mq.optculture.model.magento;

import java.util.List;

public class PromoCodeDiscountInfo {

	private String couponName;
	private String couponCode;
	private String couponType;
	private String validFrom;
	private String validTo;
	private String discountCriteria;
	private String loyaltyPoints;
	private PromoDiscountInfo promoDiscountInfo;

	public PromoCodeDiscountInfo() {
		
	}

	public PromoCodeDiscountInfo(String couponName, String couponCode,
			String couponType, String validFrom, String validTo,
			String discountCriteria, String loyaltyPoints,
			PromoDiscountInfo promoDiscountInfo) {
		super();
		this.couponName = couponName;
		this.couponCode = couponCode;
		this.couponType = couponType;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.discountCriteria = discountCriteria;
		this.loyaltyPoints = loyaltyPoints;
		this.promoDiscountInfo = promoDiscountInfo;
	}
	
	public String getCouponName() {
		return couponName;
	}
	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	public String getCouponType() {
		return couponType;
	}
	public void setCouponType(String couponType) {
		this.couponType = couponType;
	}
	public String getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}
	public String getValidTo() {
		return validTo;
	}
	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}
	public String getDiscountCriteria() {
		return discountCriteria;
	}
	public void setDiscountCriteria(String discountCriteria) {
		this.discountCriteria = discountCriteria;
	}
	public String getLoyaltyPoints() {
		return loyaltyPoints;
	}
	public void setLoyaltyPoints(String loyaltyPoints) {
		this.loyaltyPoints = loyaltyPoints;
	}

	public PromoDiscountInfo getPromoDiscountInfo() {
		return promoDiscountInfo;
	}

	public void setPromoDiscountInfo(PromoDiscountInfo promoDiscountInfo) {
		this.promoDiscountInfo = promoDiscountInfo;
	}

	
}
