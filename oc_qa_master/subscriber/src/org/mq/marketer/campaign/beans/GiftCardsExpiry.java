package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class GiftCardsExpiry {
	
	private Long expiryId;
    private Long historyId;
    private Long giftCardId;
    private Long giftProgramId;
    private String giftCardNumber;
    private Long userId;
    private Calendar createdDate;
    private Double expiryAmount;
    private Calendar expiryDate;
    
	public Long getExpiryId() {
		return expiryId;
	}
	public void setExpiryId(Long expiryId) {
		this.expiryId = expiryId;
	}
	public Long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public Double getExpiryAmount() {
		return expiryAmount;
	}
	public void setExpiryAmount(Double expiryAmount) {
		this.expiryAmount = expiryAmount;
	}
	public Calendar getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Calendar expiryDate) {
		this.expiryDate = expiryDate;
	}

}
