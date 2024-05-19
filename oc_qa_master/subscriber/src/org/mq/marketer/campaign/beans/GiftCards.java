package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class GiftCards {
	
	private Long giftCardId;
	private Long userId;
    private Long giftProgramId;
    private String giftCardNumber;
    private String giftCardPin;
    private String giftCardStatus;
	private Double giftBalance;
    private Double totalLoaded;
    private Double totalRedeemed;
    private Double totalExpired;
    private Long purchasedMobile;
    private String purchasedEmail;
    private Calendar purchasedDate;
    private String purchasedItemSid;
    private Long purchasedStoreId;
    private Long giftedToMobile;
    private String giftedToEmail;
    private Calendar giftedDate;
    private Calendar expiryDate;
	
    public Long getGiftCardId() {
		return giftCardId;
	}
	public void setGiftCardId(Long giftCardId) {
		this.giftCardId = giftCardId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getGiftProgramId() {
		return giftProgramId;
	}
	public void setGiftProgramId(Long giftProgramId) {
		this.giftProgramId = giftProgramId;
	}
	public String getGiftCardNumber() {
		return giftCardNumber;
	}
	public void setGiftCardNumber(String giftCardNumber) {
		this.giftCardNumber = giftCardNumber;
	}
	public String getGiftCardPin() {
		return giftCardPin;
	}
	public void setGiftCardPin(String giftCardPin) {
		this.giftCardPin = giftCardPin;
	}
	public String getGiftCardStatus() {
		return giftCardStatus;
	}
	public void setGiftCardStatus(String giftCardStatus) {
		this.giftCardStatus = giftCardStatus;
	}
	public Double getGiftBalance() {
		return giftBalance;
	}
	public void setGiftBalance(Double giftBalance) {
		this.giftBalance = giftBalance;
	}
	public Double getTotalLoaded() {
		return totalLoaded;
	}
	public void setTotalLoaded(Double totalLoaded) {
		this.totalLoaded = totalLoaded;
	}
	public Double getTotalRedeemed() {
		return totalRedeemed;
	}
	public void setTotalRedeemed(Double totalRedeemed) {
		this.totalRedeemed = totalRedeemed;
	}
	public Double getTotalExpired() {
		return totalExpired;
	}
	public void setTotalExpired(Double totalExpired) {
		this.totalExpired = totalExpired;
	}
	public Long getPurchasedMobile() {
		return purchasedMobile;
	}
	public void setPurchasedMobile(Long purchasedMobile) {
		this.purchasedMobile = purchasedMobile;
	}
	public String getPurchasedEmail() {
		return purchasedEmail;
	}
	public void setPurchasedEmail(String purchasedEmail) {
		this.purchasedEmail = purchasedEmail;
	}
	public Calendar getPurchasedDate() {
		return purchasedDate;
	}
	public void setPurchasedDate(Calendar purchasedDate) {
		this.purchasedDate = purchasedDate;
	}
	public String getPurchasedItemSid() {
		return purchasedItemSid;
	}
	public void setPurchasedItemSid(String purchasedItemSid) {
		this.purchasedItemSid = purchasedItemSid;
	}
	public Long getPurchasedStoreId() {
		return purchasedStoreId;
	}
	public void setPurchasedStoreId(Long purchasedStoreId) {
		this.purchasedStoreId = purchasedStoreId;
	}
	public Long getGiftedToMobile() {
		return giftedToMobile;
	}
	public void setGiftedToMobile(Long giftedToMobile) {
		this.giftedToMobile = giftedToMobile;
	}
	public String getGiftedToEmail() {
		return giftedToEmail;
	}
	public void setGiftedToEmail(String giftedToEmail) {
		this.giftedToEmail = giftedToEmail;
	}
	public Calendar getGiftedDate() {
		return giftedDate;
	}
	public void setGiftedDate(Calendar giftedDate) {
		this.giftedDate = giftedDate;
	}
	public Calendar getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Calendar expiryDate) {
		this.expiryDate = expiryDate;
	}

}
