package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
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
	private int  noOfTiers;
	private String status;
	private char defaultFlag;
	private char uniqueMobileFlag;
	private String regRequisites;
//	private String bonusRewardType;
//	private double bonusRewardValue;
	//private char redemptionOTPFlag;
	private Long userId;
	private Long orgId;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private String createdBy;
	private String modifiedBy;
//	private char rewardExpiryOnLevelUpgd;
	private char mbrshipExpiryOnLevelUpgdFlag;
	private String draftStatus;
	public  char rewardExpiryFlag;
	public  char membershipExpiryFlag;
	public  String membershipType;
	public  char giftAmountExpiryFlag;
	private String  giftAmountExpiryDateType;
	private Long   giftAmountExpiryDateValue;
	public  char giftMembrshpExpiryFlag;
	private String giftMembrshpExpiryDateType;
	private Long  giftMembrshpExpiryDateValue;
	//private Double otpLimitAmt;
	public  String rewardType;

	public String getRewardType() {
		return rewardType;
	}

	public void setRewardType(String rewardType) {
		this.rewardType = rewardType;
	}
	
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
}
