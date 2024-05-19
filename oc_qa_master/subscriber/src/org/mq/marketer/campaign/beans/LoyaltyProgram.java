package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.List;
/**
 * 
 * @author Venkata Rathnam D
 * It handles data of loyalty program settings.
 */
public class LoyaltyProgram {

	private Long programId;
	private String programName;
	private String description;
	private char tierEnableFlag;
	private int  noOfTiers; // min value 1 and max value 10
	private String status;
	private char defaultFlag; // default program flag value - Y or N
	private char uniqueMobileFlag; // single membership with mobile no. flag value - Y or N
	private char uniqueEmailFlag;
	private String regRequisites;
//	private String bonusRewardType;
//	private double bonusRewardValue;
	//private char redemptionOTPFlag; // OTP check enable flag - Y or N
	//private Double redemptionPercentageLimit;
	//private Double redemptionValueLimit;
	//private char partialReversalFlag; // partial reversal check enable flag - Y or N
	private Long userId;
	private Long orgId;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private String createdBy;
	private String modifiedBy;
//	private char rewardExpiryOnLevelUpgd;
	private char mbrshipExpiryOnLevelUpgdFlag; // reset validity on level upgrade - Y or N
	private String draftStatus;
	public  char rewardExpiryFlag;
	public  char membershipExpiryFlag;
	public  String membershipType;
	public  char giftAmountExpiryFlag;
	private String  giftAmountExpiryDateType; // Month/Year
	private Long   giftAmountExpiryDateValue;
	public  char giftMembrshpExpiryFlag;
	private String giftMembrshpExpiryDateType; // Month/Year
	private Long  giftMembrshpExpiryDateValue;
	//private Double otpLimitAmt;
	private String validationRule;//Dcardbased program should have validation rule
	private String programType;
	private List<LoyaltyThresholdBonus> expireBonusList;
	//private char considerRedeemedAmountFlag; // to include redeemed amt in issuance check enable flag - Y or N 
	//private Boolean IssuanceDisable;//Special rewards related
	//private Double minReceiptAmtValue;
	//private Double minBalanceRedeemValue;
	//private String minBalanceType; //points/currency
	public  String rewardType;

	public String getRewardType() {
		return rewardType;
	}

	public void setRewardType(String rewardType) {
		this.rewardType = rewardType;
	}

	/*public String getMinBalanceType() {
		return minBalanceType;
	}

	public void setMinBalanceType(String minBalanceType) {
		this.minBalanceType = minBalanceType;
	}

	public Double getMinBalanceRedeemValue() {
		return minBalanceRedeemValue;
	}

	public void setMinBalanceRedeemValue(Double minBalanceRedeemValue) {
		this.minBalanceRedeemValue = minBalanceRedeemValue;
	}
	
	public Double getMinReceiptAmtValue() {
		return minReceiptAmtValue;
	}

	public void setMinReceiptAmtValue(Double minReceiptAmtValue) {
		this.minReceiptAmtValue = minReceiptAmtValue;
	}

	public char getConsiderRedeemedAmountFlag() {
		return considerRedeemedAmountFlag;
	}

	public void setConsiderRedeemedAmountFlag(char considerRedeemedAmountFlag) {
		this.considerRedeemedAmountFlag = considerRedeemedAmountFlag;
	}*/

	public LoyaltyProgram() {
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public char getTierEnableFlag() {
		return tierEnableFlag;
	}

	public void setTierEnableFlag(char tierEnableFlag) {
		this.tierEnableFlag = tierEnableFlag;
	}

	public int getNoOfTiers() {
		return noOfTiers;
	}

	public void setNoOfTiers(int noOfTiers) {
		this.noOfTiers = noOfTiers;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public char getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(char defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public char getUniqueMobileFlag() {
		return uniqueMobileFlag;
	}

	public void setUniqueMobileFlag(char uniqueMobileFlag) {
		this.uniqueMobileFlag = uniqueMobileFlag;
	}

	public String getRegRequisites() {
		return regRequisites;
	}

	public void setRegRequisites(String regRequisites) {
		this.regRequisites = regRequisites;
	}

	/*public String getBonusRewardType() {
		return bonusRewardType;
	}

	public void setBonusRewardType(String bonusRewardType) {
		this.bonusRewardType = bonusRewardType;
	}

	public double getBonusRewardValue() {
		return bonusRewardValue;
	}

	public void setBonusRewardValue(double bonusRewardValue) {
		this.bonusRewardValue = bonusRewardValue;
	}*/

	/*public char getRedemptionOTPFlag() {
		return redemptionOTPFlag;
	}

	public void setRedemptionOTPFlag(char redemptionOTPFlag) {
		this.redemptionOTPFlag = redemptionOTPFlag;
	}*/
	
	/*public char getPartialReversalFlag() {
		return partialReversalFlag;
	}

	public void setPartialReversalFlag(char partialReversalFlag) {
		this.partialReversalFlag = partialReversalFlag;
	}*/

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/*public char getRewardExpiryOnLevelUpgd() {
		return rewardExpiryOnLevelUpgd;
	}

	public void setRewardExpiryOnLevelUpgd(char rewardExpiryOnLevelUpgd) {
		this.rewardExpiryOnLevelUpgd = rewardExpiryOnLevelUpgd;
	}*/

	public char getMbrshipExpiryOnLevelUpgdFlag() {
		return mbrshipExpiryOnLevelUpgdFlag;
	}

	public void setMbrshipExpiryOnLevelUpgdFlag(char mbrshipExpiryOnLevelUpgdFlag) {
		this.mbrshipExpiryOnLevelUpgdFlag = mbrshipExpiryOnLevelUpgdFlag;
	}

	public String getDraftStatus() {
		return draftStatus;
	}

	public void setDraftStatus(String draftStatus) {
		this.draftStatus = draftStatus;
	}

	public char getRewardExpiryFlag() {
		return rewardExpiryFlag;
	}

	public void setRewardExpiryFlag(char rewardExpiryFlag) {
		this.rewardExpiryFlag = rewardExpiryFlag;
	}

	public char getMembershipExpiryFlag() {
		return membershipExpiryFlag;
	}

	public void setMembershipExpiryFlag(char membershipExpiryFlag) {
		this.membershipExpiryFlag = membershipExpiryFlag;
	}

	public String getMembershipType() {
		return membershipType;
	}

	public void setMembershipType(String membershipType) {
		this.membershipType = membershipType;
	}

	public char getGiftAmountExpiryFlag() {
		return giftAmountExpiryFlag;
	}

	public void setGiftAmountExpiryFlag(char giftAmountExpiryFlag) {
		this.giftAmountExpiryFlag = giftAmountExpiryFlag;
	}

	public String getGiftAmountExpiryDateType() {
		return giftAmountExpiryDateType;
	}

	public void setGiftAmountExpiryDateType(String giftAmountExpiryDateType) {
		this.giftAmountExpiryDateType = giftAmountExpiryDateType;
	}

	public Long getGiftAmountExpiryDateValue() {
		return giftAmountExpiryDateValue;
	}

	public void setGiftAmountExpiryDateValue(Long giftAmountExpiryDateValue) {
		this.giftAmountExpiryDateValue = giftAmountExpiryDateValue;
	}

	public char getGiftMembrshpExpiryFlag() {
		return giftMembrshpExpiryFlag;
	}

	public void setGiftMembrshpExpiryFlag(char giftMembrshpExpiryFlag) {
		this.giftMembrshpExpiryFlag = giftMembrshpExpiryFlag;
	}

	public String getGiftMembrshpExpiryDateType() {
		return giftMembrshpExpiryDateType;
	}

	public void setGiftMembrshpExpiryDateType(String giftMembrshpExpiryDateType) {
		this.giftMembrshpExpiryDateType = giftMembrshpExpiryDateType;
	}

	public Long getGiftMembrshpExpiryDateValue() {
		return giftMembrshpExpiryDateValue;
	}

	public void setGiftMembrshpExpiryDateValue(Long giftMembrshpExpiryDateValue) {
		this.giftMembrshpExpiryDateValue = giftMembrshpExpiryDateValue;
	}

	/*public Double getOtpLimitAmt() {
		return otpLimitAmt;
	}

	public void setOtpLimitAmt(Double otpLimitAmt) {
		this.otpLimitAmt = otpLimitAmt;
	}*/
	public String getValidationRule() {
		return validationRule;
	}

	public void setValidationRule(String validationRule) {
		this.validationRule = validationRule;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	/*public Double getRedemptionPercentageLimit() {
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
	}*/

	public char getUniqueEmailFlag() {
		return uniqueEmailFlag;
	}

	public void setUniqueEmailFlag(char uniqueEmailFlag) {
		this.uniqueEmailFlag = uniqueEmailFlag;
	}

	public List<LoyaltyThresholdBonus> getExpireBonusList() {
		return expireBonusList;
	}

	public void setExpireBonusList(List<LoyaltyThresholdBonus> expireBonusList) {
		this.expireBonusList = expireBonusList;
	}

/*	public boolean isIssuanceDisable() {
		return IssuanceDisable;
	}

	public void setIssuanceDisable(boolean issuanceDisable) {
		IssuanceDisable = issuanceDisable;
	}
*/
}
