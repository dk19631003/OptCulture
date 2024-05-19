package org.mq.optculture.business.helper.DRToLty;

import java.util.List;

public class DRToLtyExtractionData {
	
	private DRToLoyaltyExtractionCustomer customer;
	private List<DRToLoyaltyExtractionItems> Items;
	private List<DRToLoyaltyExtractionPromotions> promotions;
	private DRToLoyaltyExtractionAuth user;
	private DRToLoyaltyExtractionParams headerFileds;
	private DRToLoyaltyExtractionReceipt receipt;
	private DRToLoyaltyExtractionRedemption redemptionDetails;
	private boolean perkBased;
	
    public boolean isPerkBased() {
		return perkBased;
	}
	public void setPerkBased(boolean perkBased) {
		this.perkBased = perkBased;
	}
private String OTPCode;
	
	public String getOTPCode() {
		return OTPCode;
	}
	public void setOTPCode(String oTPCode) {
		OTPCode = oTPCode;
	}
	private Double returnedAmount = 0.0;
	private Double nonInvnPromoItemsDiscount;
	public Double getNonInvnPromoItemsDiscount() {
		return nonInvnPromoItemsDiscount;
	}
	public void setNonInvnPromoItemsDiscount(Double nonInvnPromoItemsDiscount) {
		this.nonInvnPromoItemsDiscount = nonInvnPromoItemsDiscount;
	}
	public Double getReturnedAmount() {
		return returnedAmount;
	}
	public void setReturnedAmount(Double returnedAmount) {
		this.returnedAmount = returnedAmount;
	}
	private boolean mobileBasedEnroll = false;
	public DRToLoyaltyExtractionCustomer getCustomer() {
		return customer;
	}
	public void setCustomer(DRToLoyaltyExtractionCustomer customer) {
		this.customer = customer;
	}
	public List<DRToLoyaltyExtractionItems> getItems() {
		return Items;
	}
	public void setItems(List<DRToLoyaltyExtractionItems> items) {
		Items = items;
	}
	public DRToLoyaltyExtractionAuth getUser() {
		return user;
	}
	public void setUser(DRToLoyaltyExtractionAuth user) {
		this.user = user;
	}
	public DRToLoyaltyExtractionParams getHeaderFileds() {
		return headerFileds;
	}
	public void setHeaderFileds(DRToLoyaltyExtractionParams headerFileds) {
		this.headerFileds = headerFileds;
	}
	public DRToLoyaltyExtractionReceipt getReceipt() {
		return receipt;
	}
	public void setReceipt(DRToLoyaltyExtractionReceipt receipt) {
		this.receipt = receipt;
	}
	public DRToLtyExtractionData(DRToLoyaltyExtractionCustomer customer, List<DRToLoyaltyExtractionItems> items,
			DRToLoyaltyExtractionAuth user, DRToLoyaltyExtractionParams headerFileds,
			DRToLoyaltyExtractionReceipt receipt) {
		super();
		this.customer = customer;
		Items = items;
		this.user = user;
		this.headerFileds = headerFileds;
		this.receipt = receipt;
	}
	public List<DRToLoyaltyExtractionPromotions> getPromotions() {
		return promotions;
	}
	public void setPromotions(List<DRToLoyaltyExtractionPromotions> promotions) {
		this.promotions = promotions;
	}
	public boolean isMobileBasedEnroll() {
		return mobileBasedEnroll;
	}
	public void setMobileBasedEnroll(boolean mobileBasedEnroll) {
		this.mobileBasedEnroll = mobileBasedEnroll;
	}
	public DRToLoyaltyExtractionRedemption getRedemptionDetails() {
		return redemptionDetails;
	}
	public void setRedemptionDetails(DRToLoyaltyExtractionRedemption redemptionDetails) {
		this.redemptionDetails = redemptionDetails;
	}
	
	
}
