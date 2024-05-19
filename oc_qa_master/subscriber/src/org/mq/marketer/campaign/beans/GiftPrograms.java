package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class GiftPrograms {
	
	private Long giftProgramId;
    private String giftProgramName;
    private String programStatus;
    private Long userId;
    private Calendar createdDate;
    private Calendar modifiedDate;
    private Long expiryInMonths;
    private String redemptionType;
    private Long pinLength;
    private String cardType;
    
	public Long getGiftProgramId() {
		return giftProgramId;
	}
	public void setGiftProgramId(Long giftProgramId) {
		this.giftProgramId = giftProgramId;
	}
	public String getGiftProgramName() {
		return giftProgramName;
	}
	public void setGiftProgramName(String giftProgramName) {
		this.giftProgramName = giftProgramName;
	}
	public String getProgramStatus() {
		return programStatus;
	}
	public void setProgramStatus(String programStatus) {
		this.programStatus = programStatus;
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
	public Calendar getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public Long getExpiryInMonths() {
		return expiryInMonths;
	}
	public void setExpiryInMonths(Long expiryInMonths) {
		this.expiryInMonths = expiryInMonths;
	}
	public String getRedemptionType() {
		return redemptionType;
	}
	public void setRedemptionType(String redemptionType) {
		this.redemptionType = redemptionType;
	}
	public Long getPinLength() {
		return pinLength;
	}
	public void setPinLength(Long pinLength) {
		this.pinLength = pinLength;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
    

}
