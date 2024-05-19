package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class GiftCardsHistory {
	
	private Long historyId;
    private Long userId;
    private Long giftCardId;
    private Long giftProgramId;
    private String giftCardNumber;
    private String transactionType;
    private String amountType;
    private Double enteredAmount;
    private Double giftDifference;
    private Double giftBalance;
    private Calendar transactionDate;
    private String itemInfo;
    private Long giftedToMobile;
    private String giftedToEmail;
    private String receiptNumber;
    private Long salesId;
    private String storeNumber;
    
	public Long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getGiftCardId() {
		return giftCardId;
	}
	public void setGiftCardId(Long giftCardId) {
		this.giftCardId = giftCardId;
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
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getAmountType() {
		return amountType;
	}
	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}
	public Double getEnteredAmount() {
		return enteredAmount;
	}
	public void setEnteredAmount(Double enteredAmount) {
		this.enteredAmount = enteredAmount;
	}
	public Double getGiftDifference() {
		return giftDifference;
	}
	public void setGiftDifference(Double giftDifference) {
		this.giftDifference = giftDifference;
	}
	public Double getGiftBalance() {
		return giftBalance;
	}
	public void setGiftBalance(Double giftBalance) {
		this.giftBalance = giftBalance;
	}
	public Calendar getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Calendar transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getItemInfo() {
		return itemInfo;
	}
	public void setItemInfo(String itemInfo) {
		this.itemInfo = itemInfo;
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
	public String getReceiptNumber() {
		return receiptNumber;
	}
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	public Long getSalesId() {
		return salesId;
	}
	public void setSalesId(Long salesId) {
		this.salesId = salesId;
	}
	public String getStoreNumber() {
		return storeNumber;
	}
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}


}
