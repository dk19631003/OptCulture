package org.mq.captiway.scheduler.beans;

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
	private String earnedLevelType;
	private Double earnedLevelValue;
	private Calendar createdDate;
	private String createdBy;
	private Calendar modifiedDate;
	private String modifiedBy;
	private char registrationFlag;

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
	
	
}
