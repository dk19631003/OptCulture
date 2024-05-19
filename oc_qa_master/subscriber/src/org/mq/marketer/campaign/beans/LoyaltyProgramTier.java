package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class LoyaltyProgramTier {

	private Long tierId;
	private Long programId;
	private String tierType; // Tier level like Tier 1/ Tier 2 etc.
	private String tierName;
	private String earnType;
	private String earnValueType; // Value/Percentage
	private Double earnValue;
	private Double earnOnSpentAmount;
	private Double maxcap; // Issuance Value
	private String ptsActiveDateType;//Day
	private Long ptsActiveDateValue;
	private Double convertFromPoints;
	private Double convertToAmount;
	private String conversionType; //Auto/Ondemand
	//private char autoConvertFlag;
	private String tierUpgdConstraint;
	private Double tierUpgdConstraintValue;
	private String nextTierType; // not used as of now
	private Calendar createdDate;
	private String createdBy;
	private Calendar modifiedDate;
	private String modifiedBy;
	private String  rewardExpiryDateType; // Month/Year
	private Long  rewardExpiryDateValue;
	private String membershipExpiryDateType; // Month/Year
	private Long  membershipExpiryDateValue;
	private char activationFlag; // activate after flag
	private Long tierUpgradeCumulativeValue;
	private String roundingType; //Up/Down/Near
//	private String tierUpgradeCumulativeType;
	private Boolean issuanceChkEnable=false; 
private Boolean activateAfterDisableAllStore;//Name were used in opposite sense, it should be allowed a for acivate after
	private String disallowActivateAfterStores;//Name were used in opposite sense, it should be allowed a for acivate after

	//private String perkIssuanceType;
	//private Double perkIssuanceValue;
	//private boolean perkIssuanceExpiryCheck;
	private String perkLimitExpType;
	private Long perkLimitValue;
	private Double redemptionPercentageLimit;
	private Double redemptionValueLimit;
	//private String valueCode;
	private Double minReceiptValue;
	private Double minBalanceValue;
	private Double crossOverBonus;
	private char redemptionOTPFlag; // OTP check enable flag - Y or N
	private Double otpLimitAmt;
	private char considerRedeemedAmountFlag; // to include redeemed amt in issuance check enable flag - Y or N
	private char partialReversalFlag; // partial reversal check enable flag - Y or N
	private String multipleTierUpgrdRules;

	public String getMultipleTierUpgrdRules() {
		return multipleTierUpgrdRules;
	}

	public void setMultipleTierUpgrdRules(String multipleTierUpgrdRules) {
		this.multipleTierUpgrdRules = multipleTierUpgrdRules;
	}

	public char getPartialReversalFlag() {
		return partialReversalFlag;
	}

	public void setPartialReversalFlag(char partialReversalFlag) {
		this.partialReversalFlag = partialReversalFlag;
	}

	public char getConsiderRedeemedAmountFlag() {
		return considerRedeemedAmountFlag;
	}

	public void setConsiderRedeemedAmountFlag(char considerRedeemedAmountFlag) {
		this.considerRedeemedAmountFlag = considerRedeemedAmountFlag;
	}

	public Double getOtpLimitAmt() {
		return otpLimitAmt;
	}

	public void setOtpLimitAmt(Double otpLimitAmt) {
		this.otpLimitAmt = otpLimitAmt;
	}

	public char getRedemptionOTPFlag() {
		return redemptionOTPFlag;
	}

	public void setRedemptionOTPFlag(char redemptionOTPFlag) {
		this.redemptionOTPFlag = redemptionOTPFlag;
	}

	public Double getCrossOverBonus() {
		return crossOverBonus;
	}

	public void setCrossOverBonus(Double crossOverBonus) {
		this.crossOverBonus = crossOverBonus;
	}

	public Double getMinBalanceValue() {
		return minBalanceValue;
	}

	public void setMinBalanceValue(Double minBalanceValue) {
		this.minBalanceValue = minBalanceValue;
	}

	public Double getMinReceiptValue() {
		return minReceiptValue;
	}

	public void setMinReceiptValue(Double minReceiptValue) {
		this.minReceiptValue = minReceiptValue;
	}

	/*public String getValueCode() {
		return valueCode;
	}

	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}*/

	public Double getRedemptionPercentageLimit() {
		return redemptionPercentageLimit;
	}

	public void setRedemptionPercentageLimit(Double redemptionPercentageLimit) {
		this.redemptionPercentageLimit = redemptionPercentageLimit;
	}

	public Double getRedemptionValueLimit() {
		return redemptionValueLimit;
	}

	public void setRedemptionValueLimit(Double redemptionValueLimit) {
		this.redemptionValueLimit = redemptionValueLimit;
	}

	public Long getPerkLimitValue() {
		return perkLimitValue;
	}

	public void setPerkLimitValue(Long perkLimitValue) {
		this.perkLimitValue = perkLimitValue;
	}

	public String getPerkLimitExpType() {
		return perkLimitExpType;
	}

	public void setPerkLimitExpType(String perkLimitExpType) {
		this.perkLimitExpType = perkLimitExpType;
	}

	/*public Double getPerkIssuanceValue() {
		return perkIssuanceValue;
	}*/
	
	/*public boolean isPerkIssuanceExpiryCheck() {
		return perkIssuanceExpiryCheck;
	}

	public void setPerkIssuanceExpiryCheck(boolean perkIssuanceExpiryCheck) {
		this.perkIssuanceExpiryCheck = perkIssuanceExpiryCheck;
	}*/

	/*public void setPerkIssuanceValue(Double perkIssuanceValue) {
		this.perkIssuanceValue = perkIssuanceValue;
	}*/

	/*public String getPerkIssuanceType() {
		return perkIssuanceType;
	}

	public void setPerkIssuanceType(String perkIssuanceType) {
		this.perkIssuanceType = perkIssuanceType;
	}*/

	public Boolean getIssuanceChkEnable() {
		return issuanceChkEnable;
	}

	public void setIssuanceChkEnable(Boolean issuanceChkEnable) {
		this.issuanceChkEnable = issuanceChkEnable;
	}
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
	public Double getMaxcap() {
		return maxcap;
	}

	public void setMaxcap(Double maxcap) {
		this.maxcap = maxcap;
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
	public String getRoundingType() {
		return roundingType;
	}

	public void setRoundingType(String roundingType) {
		this.roundingType = roundingType;
	}


	public Boolean getActivateAfterDisableAllStore() {
		return activateAfterDisableAllStore;
	}

	public void setActivateAfterDisableAllStore(Boolean activateAfterDisableAllStore) {
		this.activateAfterDisableAllStore = activateAfterDisableAllStore;
	}

	public String getDisallowActivateAfterStores() {
		return disallowActivateAfterStores;
	}

	public void setDisallowActivateAfterStores(String disallowActivateAfterStores) {
		this.disallowActivateAfterStores = disallowActivateAfterStores;
	}
	
	
}
