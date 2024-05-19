package org.mq.marketer.campaign.beans;

import java.util.Calendar;

/**
 * 
 * @author Venkata Rathnam D
 * It handles program threshold bonus data.
 */
public class LoyaltyThresholdBonus {
	private Long thresholdBonusId;
	private Long programId;
	private String extraBonusType;
	private Double extraBonusValue;
	private String earnedLevelType;//Points,Amount, LPV
	private Double earnedLevelValue;
	private boolean recurring;//fixed reaching=0,every increment=1
	private Calendar createdDate;
	private String createdBy;
	private Calendar modifiedDate;
	private String modifiedBy;
	private char registrationFlag; // to differentiate enrollment bonus with threshold bonus
	private Double thresholdLimit;
	private Long emailTempId;
	private Long smsTempId;
	private Long emailExpiryTempId;
	private Long smsExpiryTempId;
	private String bonusExpiryDateType;
	private Long bonusExpiryDateValue;
	
	public Double getThresholdLimit() {
		return thresholdLimit;
	}

	public void setThresholdLimit(Double thresholdLimit) {
		this.thresholdLimit = thresholdLimit;
	}

	public char getRegistrationFlag() {
		return registrationFlag;
	}

	public void setRegistrationFlag(char registrationFlag) {
		this.registrationFlag = registrationFlag;
	}

	public LoyaltyThresholdBonus() {
	}

	public Long getThresholdBonusId() {
		return thresholdBonusId;
	}

	public void setThresholdBonusId(Long thresholdBonusId) {
		this.thresholdBonusId = thresholdBonusId;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public String getExtraBonusType() {
		return extraBonusType;
	}

	public void setExtraBonusType(String extraBonusType) {
		this.extraBonusType = extraBonusType;
	}

	public Double getExtraBonusValue() {
		return extraBonusValue;
	}

	public void setExtraBonusValue(Double extraBonusValue) {
		this.extraBonusValue = extraBonusValue;
	}

	public String getEarnedLevelType() {
		return earnedLevelType;
	}

	public void setEarnedLevelType(String earnedLevelType) {
		this.earnedLevelType = earnedLevelType;
	}

	public Double getEarnedLevelValue() {
		return earnedLevelValue;
	}

	public void setEarnedLevelValue(Double earnedLevelValue) {
		this.earnedLevelValue = earnedLevelValue;
	}
	
	 public boolean isRecurring() {
	        return recurring;
	    }
	    public void setRecurring(boolean recurring) {
	        this.recurring = recurring;
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

	public Long getEmailTempId() {
		return emailTempId;
	}

	public void setEmailTempId(Long emailTempId) {
		this.emailTempId = emailTempId;
	}

	public Long getSmsTempId() {
		return smsTempId;
	}

	public void setSmsTempId(Long smsTempId) {
		this.smsTempId = smsTempId;
	}

	public Long getEmailExpiryTempId() {
		return emailExpiryTempId;
	}

	public void setEmailExpiryTempId(Long emailExpiryTempId) {
		this.emailExpiryTempId = emailExpiryTempId;
	}

	public Long getSmsExpiryTempId() {
		return smsExpiryTempId;
	}

	public void setSmsExpiryTempId(Long smsExpiryTempId) {
		this.smsExpiryTempId = smsExpiryTempId;
	}

	public String getBonusExpiryDateType() {
		return bonusExpiryDateType;
	}

	public void setBonusExpiryDateType(String bonusExpiryDateType) {
		this.bonusExpiryDateType = bonusExpiryDateType;
	}

	public Long getBonusExpiryDateValue() {
		return bonusExpiryDateValue;
	}

	public void setBonusExpiryDateValue(Long bonusExpiryDateValue) {
		this.bonusExpiryDateValue = bonusExpiryDateValue;
	}
	
	
}
