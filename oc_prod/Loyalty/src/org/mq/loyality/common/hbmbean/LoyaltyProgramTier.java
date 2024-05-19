package org.mq.loyality.common.hbmbean;


import java.util.Calendar;

public class LoyaltyProgramTier {

	private Long tierId;
	private Long programId;
	private String tierType;
	private String tierName;
	private String earnType;
	private String earnValueType;
	private Double earnValue;
	private Double earnOnSpentAmount;
	private String ptsActiveDateType;
	private Long ptsActiveDateValue;
	private Double convertFromPoints;
	private Double convertToAmount;
	private String conversionType; //Auto/Ondemand
	//private char autoConvertFlag;
	private String tierUpgdConstraint;
	private Double tierUpgdConstraintValue;
	private String nextTierType;
	private Calendar createdDate;
	private String createdBy;
	private Calendar modifiedDate;
	private String modifiedBy;
	private String  rewardExpiryDateType;
	private Long  rewardExpiryDateValue;
	private String membershipExpiryDateType;
	private Long  membershipExpiryDateValue;
	private char activationFlag;
	private Long tierUpgradeCumulativeValue;
//	private String tierUpgradeCumulativeType;
	public LoyaltyProgramTier() {
	}

	public Long getTierId() {
		return tierId;
	}

	public void setTierId(Long tierId) {
		this.tierId = tierId;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public String getTierType() {
		return tierType;
	}

	public void setTierType(String tierType) {
		this.tierType = tierType;
	}

	public String getTierName() {
		return tierName;
	}

	public void setTierName(String tierName) {
		this.tierName = tierName;
	}

	public String getEarnType() {
		return earnType;
	}

	public void setEarnType(String earnType) {
		this.earnType = earnType;
	}

	public String getEarnValueType() {
		return earnValueType;
	}

	public void setEarnValueType(String earnValueType) {
		this.earnValueType = earnValueType;
	}

	public Double getEarnValue() {
		return earnValue;
	}

	public void setEarnValue(Double earnValue) {
		this.earnValue = earnValue;
	}

	public Double getEarnOnSpentAmount() {
		return earnOnSpentAmount;
	}

	public void setEarnOnSpentAmount(Double earnOnSpentAmount) {
		this.earnOnSpentAmount = earnOnSpentAmount;
	}

	public String getPtsActiveDateType() {
		return ptsActiveDateType;
	}

	public void setPtsActiveDateType(String ptsActiveDateType) {
		this.ptsActiveDateType = ptsActiveDateType;
	}

	public Long getPtsActiveDateValue() {
		return ptsActiveDateValue;
	}

	public void setPtsActiveDateValue(Long ptsActiveDateValue) {
		this.ptsActiveDateValue = ptsActiveDateValue;
	}

	public Double getConvertFromPoints() {
		return convertFromPoints;
	}

	public void setConvertFromPoints(Double convertFromPoints) {
		this.convertFromPoints = convertFromPoints;
	}

	public Double getConvertToAmount() {
		return convertToAmount;
	}

	public void setConvertToAmount(Double convertToAmount) {
		this.convertToAmount = convertToAmount;
	}

	/*public char getAutoConvertFlag() {
		return autoConvertFlag;
	}

	public void setAutoConvertFlag(char autoConvertFlag) {
		this.autoConvertFlag = autoConvertFlag;
	}*/

	public String getTierUpgdConstraint() {
		return tierUpgdConstraint;
	}

	public void setTierUpgdConstraint(String tierUpgdConstraint) {
		this.tierUpgdConstraint = tierUpgdConstraint;
	}

	public Double getTierUpgdConstraintValue() {
		return tierUpgdConstraintValue;
	}

	public void setTierUpgdConstraintValue(Double tierUpgdConstraintValue) {
		this.tierUpgdConstraintValue = tierUpgdConstraintValue;
	}

	public String getNextTierType() {
		return nextTierType;
	}

	public void setNextTierType(String nextTierType) {
		this.nextTierType = nextTierType;
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

	public String getRewardExpiryDateType() {
		return rewardExpiryDateType;
	}

	public void setRewardExpiryDateType(String rewardExpiryDateType) {
		this.rewardExpiryDateType = rewardExpiryDateType;
	}

	public Long getRewardExpiryDateValue() {
		return rewardExpiryDateValue;
	}

	public void setRewardExpiryDateValue(Long rewardExpiryDateValue) {
		this.rewardExpiryDateValue = rewardExpiryDateValue;
	}

	public String getMembershipExpiryDateType() {
		return membershipExpiryDateType;
	}

	public void setMembershipExpiryDateType(String membershipExpiryDateType) {
		this.membershipExpiryDateType = membershipExpiryDateType;
	}

	public Long getMembershipExpiryDateValue() {
		return membershipExpiryDateValue;
	}

	public void setMembershipExpiryDateValue(Long membershipExpiryDateValue) {
		this.membershipExpiryDateValue = membershipExpiryDateValue;
	}

	public char getActivationFlag() {
		return activationFlag;
	}

	public void setActivationFlag(char activationFlag) {
		this.activationFlag = activationFlag;
	}

	public Long getTierUpgradeCumulativeValue() {
		return tierUpgradeCumulativeValue;
	}

	public void setTierUpgradeCumulativeValue(Long tierUpgradeCumulativeValue) {
		this.tierUpgradeCumulativeValue = tierUpgradeCumulativeValue;
	}

//	public String getTierUpgradeCumulativeType() {
//		return tierUpgradeCumulativeType;
//	}
//
//	public void setTierUpgradeCumulativeType(String tierUpgradeCumulativeType) {
//		this.tierUpgradeCumulativeType = tierUpgradeCumulativeType;
//	}

	public String getConversionType() {
		return conversionType;
	}

	public void setConversionType(String conversionType) {
		this.conversionType = conversionType;
	}
	
	
}
