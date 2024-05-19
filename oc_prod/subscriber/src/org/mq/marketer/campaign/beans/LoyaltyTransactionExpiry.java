package org.mq.marketer.campaign.beans;

import java.util.Calendar;

/**
 * 
 * @author Venkata Rathnam D
 * It is a child table of LoyaltyTransactionChild. It logs all successful loyalty expiration transactions 
 * such as issuance, and bonus.
 */
public class LoyaltyTransactionExpiry {

	private Long transExpiryId;
	private Long transChildId; //loyalty transaction child id
	private String membershipNumber;
	private String membershipType;// Card/Mobile
//	private String transactionType;
	private String rewardFlag;// G or L
	private Calendar createdDate;
	private Long userId;
	private Long orgId;
	private Long programId;
	private Long tierId;
	private Long expiryPoints; //points to be expired
	private Double expiryAmount; //amount to be expired
	private Long expiryReward;//reward to be expired
	private Long loyaltyId;// contacts_loyalty primary key
	private Long transferedTo;
	private Calendar transferedOn;
	private String valueCode;
	private Long specialRewardId;
	private Long bonusId;

	
	
	public Calendar getTransferedOn() {
		return transferedOn;
	}


	public void setTransferedOn(Calendar transferedOn) {
		this.transferedOn = transferedOn;
	}
	public Long getTransferedTo() {
		return transferedTo;
	}


	public void setTransferedTo(Long transferedTo) {
		this.transferedTo = transferedTo;
	}
	public LoyaltyTransactionExpiry() {
	}

	
	public Long getTransChildId() {
		return transChildId;
	}


	public void setTransChildId(Long transChildId) {
		this.transChildId = transChildId;
	}


	/*public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
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

	/*public Calendar getValueExpirationDate() {
		return valueExpirationDate;
	}

	public void setValueExpirationDate(Calendar valueExpirationDate) {
		this.valueExpirationDate = valueExpirationDate;
	}*/

	/*public String getExpiryStatus() {
		return expiryStatus;
	}

	public void setExpiryStatus(String expiryStatus) {
		this.expiryStatus = expiryStatus;
	}*/

	public String getMembershipNumber() {
		return membershipNumber;
	}

	public void setMembershipNumber(String membershipNumber) {
		this.membershipNumber = membershipNumber;
	}

	public String getMembershipType() {
		return membershipType;
	}

	public void setMembershipType(String membershipType) {
		this.membershipType = membershipType;
	}

	public Long getTransExpiryId() {
		return transExpiryId;
	}

	public void setTransExpiryId(Long transExpiryId) {
		this.transExpiryId = transExpiryId;
	}

	public Long getExpiryPoints() {
		return expiryPoints;
	}

	public void setExpiryPoints(Long expiryPoints) {
		this.expiryPoints = expiryPoints;
	}

	public Double getExpiryAmount() {
		return expiryAmount;
	}

	public void setExpiryAmount(Double expiryAmount) {
		this.expiryAmount = expiryAmount;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public Long getTierId() {
		return tierId;
	}

	public void setTierId(Long tierId) {
		this.tierId = tierId;
	}

	public String getRewardFlag() {
		return rewardFlag;
	}

	public void setRewardFlag(String rewardFlag) {
		this.rewardFlag = rewardFlag;
	}


	public Long getLoyaltyId() {
		return loyaltyId;
	}


	public void setLoyaltyId(Long loyaltyId) {
		this.loyaltyId = loyaltyId;
	}


	public String getValueCode() {
		return valueCode;
	}


	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}


	public Long getExpiryReward() {
		return expiryReward;
	}


	public void setExpiryReward(Long expiryReward) {
		this.expiryReward = expiryReward;
	}


	public Long getSpecialRewardId() {
		return specialRewardId;
	}


	public void setSpecialRewardId(Long specialRewardId) {
		this.specialRewardId = specialRewardId;
	}


	public Long getBonusId() {
		return bonusId;
	}


	public void setBonusId(Long bonusId) {
		this.bonusId = bonusId;
	}
	
}
