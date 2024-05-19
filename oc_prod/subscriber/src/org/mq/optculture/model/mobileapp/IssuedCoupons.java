package org.mq.optculture.model.mobileapp;

import java.util.List;

public class IssuedCoupons {

	private String name;
	private String  description;
	private String  couponCode;
	private String loyaltyPoints;
	private String validFrom;
	private String validTo;
	private String discountCriteria;
	private boolean enableOffer;
	private String bannerImage;
	private String bannerUrlRedirect;
	private String offerHeading;
	private String offerDescription;
	private boolean highlightedOffers;
	private String createdDate;
	private String updatedDate;

	
	private List<CouponDiscount> discount;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	public String getLoyaltyPoints() {
		return loyaltyPoints;
	}
	public void setLoyaltyPoints(String loyaltyPoints) {
		this.loyaltyPoints = loyaltyPoints;
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
	public List<CouponDiscount> getDiscount() {
		return discount;
	}
	public void setDiscount(List<CouponDiscount> discount) {
		this.discount = discount;
	}
	
	public boolean isEnableOffer() {
		return enableOffer;
	}
	public void setEnableOffer(boolean enableOffer) {
		this.enableOffer = enableOffer;
	}
	public String getBannerImage() {
		return bannerImage;
	}
	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}
	public String getBannerUrlRedirect() {
		return bannerUrlRedirect;
	}
	public void setBannerUrlRedirect(String bannerUrlRedirect) {
		this.bannerUrlRedirect = bannerUrlRedirect;
	}
	public String getOfferHeading() {
		return offerHeading;
	}
	public void setOfferHeading(String offerHeading) {
		this.offerHeading = offerHeading;
	}
	public String getOfferDescription() {
		return offerDescription;
	}
	public void setOfferDescription(String offerDescription) {
		this.offerDescription = offerDescription;
	}
	public boolean isHighlightedOffers() {
		return highlightedOffers;
	}
	public void setHighlightedOffers(boolean highlightedOffers) {
		this.highlightedOffers = highlightedOffers;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}
	
}
