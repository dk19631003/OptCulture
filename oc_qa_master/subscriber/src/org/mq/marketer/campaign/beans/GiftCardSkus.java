package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class GiftCardSkus {
	
	private Long skuId;
    private GiftPrograms giftProgram;
    private String skuCode;
    private String skuName;
    private Long giftProgramId;
    private String category;
    private Double fixedAmount;
    private Long userId;
    private Calendar createdDate;
    
	public Long getSkuId() {
		return skuId;
	}
	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}
	public GiftPrograms getGiftProgram() {
		return giftProgram;
	}
	public void setGiftProgram(GiftPrograms giftProgram) {
		this.giftProgram = giftProgram;
	}
	public String getSkuCode() {
		return skuCode;
	}
	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}
	public String getSkuName() {
		return skuName;
	}
	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}
	public Long getGiftProgramId() {
		return giftProgramId;
	}
	public void setGiftProgramId(Long giftProgramId) {
		this.giftProgramId = giftProgramId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Double getFixedAmount() {
		return fixedAmount;
	}
	public void setFixedAmount(Double fixedAmount) {
		this.fixedAmount = fixedAmount;
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

}
