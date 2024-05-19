package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

/**
 * 
 * @author Venkata Rathnam D
 * It handles configured template(email/sms) names of loyalty program. 
 */
public class LoyaltyAutoComm {
	private Long autoCommId;
	private Long programId;
	private Long regEmailTmpltId;
	private Long regSmsTmpltId;
	private Long tierUpgdEmailTmpltId;
	private Long tierUpgdSmsTmpltId;
	private Long threshBonusEmailTmpltId;
	private Long threshBonusSmsTmpltId;
	private Long rewardExpiryEmailTmpltId;
	private Long rewardExpirySmsTmpltId;
	private Long mbrshipExpiryEmailTmpltId;
	private Long mbrshipExpirySmsTmpltId;
	private Calendar createdDate;
	private String createdBy;
	private Calendar modifiedDate;
	private String modifiedBy;
	private Long giftAmtExpiryEmailTmpltId;
	private Long giftAmtExpirySmsTmpltId;
	private Long giftMembrshpExpiryEmailTmpltId;
	private Long giftMembrshpExpirySmsTmpltId;
	private Long giftCardIssuanceEmailTmpltId;
	private Long giftCardIssuanceSmsTmpltId;
	
	public LoyaltyAutoComm() {
	}
	public Long getAutoCommId() {
		return autoCommId;
	}
	public void setAutoCommId(Long autoCommId) {
		this.autoCommId = autoCommId;
	}
	public Long getProgramId() {
		return programId;
	}
	public void setProgramId(Long programId) {
		this.programId = programId;
	}
	public Long getRegEmailTmpltId() {
		return regEmailTmpltId;
	}
	public void setRegEmailTmpltId(Long regEmailTmpltId) {
		this.regEmailTmpltId = regEmailTmpltId;
	}
	
	public Long getRegSmsTmpltId() {
		return regSmsTmpltId;
	}
	public void setRegSmsTmpltId(Long regSmsTmpltId) {
		this.regSmsTmpltId = regSmsTmpltId;
	}
	public Long getTierUpgdEmailTmpltId() {
		return tierUpgdEmailTmpltId;
	}
	public void setTierUpgdEmailTmpltId(Long tierUpgdEmailTmpltId) {
		this.tierUpgdEmailTmpltId = tierUpgdEmailTmpltId;
	}
	public Long getTierUpgdSmsTmpltId() {
		return tierUpgdSmsTmpltId;
	}
	public void setTierUpgdSmsTmpltId(Long tierUpgdSmsTmpltId) {
		this.tierUpgdSmsTmpltId = tierUpgdSmsTmpltId;
	}
	public Long getThreshBonusEmailTmpltId() {
		return threshBonusEmailTmpltId;
	}
	public void setThreshBonusEmailTmpltId(Long threshBonusEmailTmpltId) {
		this.threshBonusEmailTmpltId = threshBonusEmailTmpltId;
	}
	public Long getThreshBonusSmsTmpltId() {
		return threshBonusSmsTmpltId;
	}
	public void setThreshBonusSmsTmpltId(Long threshBonusSmsTmpltId) {
		this.threshBonusSmsTmpltId = threshBonusSmsTmpltId;
	}
	public Long getRewardExpiryEmailTmpltId() {
		return rewardExpiryEmailTmpltId;
	}
	public void setRewardExpiryEmailTmpltId(Long rewardExpiryEmailTmpltId) {
		this.rewardExpiryEmailTmpltId = rewardExpiryEmailTmpltId;
	}
	public Long getRewardExpirySmsTmpltId() {
		return rewardExpirySmsTmpltId;
	}
	public void setRewardExpirySmsTmpltId(Long rewardExpirySmsTmpltId) {
		this.rewardExpirySmsTmpltId = rewardExpirySmsTmpltId;
	}
	public Long getMbrshipExpiryEmailTmpltId() {
		return mbrshipExpiryEmailTmpltId;
	}
	public void setMbrshipExpiryEmailTmpltId(Long mbrshipExpiryEmailTmpltId) {
		this.mbrshipExpiryEmailTmpltId = mbrshipExpiryEmailTmpltId;
	}
	public Long getMbrshipExpirySmsTmpltId() {
		return mbrshipExpirySmsTmpltId;
	}
	public void setMbrshipExpirySmsTmpltId(Long mbrshipExpirySmsTmpltId) {
		this.mbrshipExpirySmsTmpltId = mbrshipExpirySmsTmpltId;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Calendar getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	public Long getGiftAmtExpiryEmailTmpltId() {
		return giftAmtExpiryEmailTmpltId;
	}
	public void setGiftAmtExpiryEmailTmpltId(Long giftAmtExpiryEmailTmpltId) {
		this.giftAmtExpiryEmailTmpltId = giftAmtExpiryEmailTmpltId;
	}
	public Long getGiftAmtExpirySmsTmpltId() {
		return giftAmtExpirySmsTmpltId;
	}
	public void setGiftAmtExpirySmsTmpltId(Long giftAmtExpirySmsTmpltId) {
		this.giftAmtExpirySmsTmpltId = giftAmtExpirySmsTmpltId;
	}
	public Long getGiftMembrshpExpiryEmailTmpltId() {
		return giftMembrshpExpiryEmailTmpltId;
	}
	public void setGiftMembrshpExpiryEmailTmpltId(
			Long giftMembrshpExpiryEmailTmpltId) {
		this.giftMembrshpExpiryEmailTmpltId = giftMembrshpExpiryEmailTmpltId;
	}
	public Long getGiftMembrshpExpirySmsTmpltId() {
		return giftMembrshpExpirySmsTmpltId;
	}
	public void setGiftMembrshpExpirySmsTmpltId(Long giftMembrshpExpirySmsTmpltId) {
		this.giftMembrshpExpirySmsTmpltId = giftMembrshpExpirySmsTmpltId;
	}
	public Long getGiftCardIssuanceEmailTmpltId() {
		return giftCardIssuanceEmailTmpltId;
	}
	public void setGiftCardIssuanceEmailTmpltId(Long giftCardIssuanceEmailTmpltId) {
		this.giftCardIssuanceEmailTmpltId = giftCardIssuanceEmailTmpltId;
	}
	public Long getGiftCardIssuanceSmsTmpltId() {
		return giftCardIssuanceSmsTmpltId;
	}
	public void setGiftCardIssuanceSmsTmpltId(Long giftCardIssuanceSmsTmpltId) {
		this.giftCardIssuanceSmsTmpltId = giftCardIssuanceSmsTmpltId;
	}
	
}
