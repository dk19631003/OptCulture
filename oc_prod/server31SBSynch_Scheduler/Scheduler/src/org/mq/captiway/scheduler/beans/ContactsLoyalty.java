package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class ContactsLoyalty implements java.io.Serializable {
	
	private Long loyaltyId;
	private Contacts contact;
	private String customerId;
	private Long accountId;
	private String cardNumber;
	private String mobilePhone;
	private String membershipType;
	private String serviceType;
	private String cardPin;
	private String locationId;
	private String empId;
	private String posStoreLocationId;
	private Double totalLoyaltyEarned;
	private Double totalLoyaltyRedemption;
	private Double loyaltyBalance;
	private String valueCode;
	private Double totalGiftcardAmount;
	private Double totalGiftcardRedemption;
	private Double giftcardBalance;
	private Double totalGiftAmount;
	private Double totalGiftRedemption;
	private Double giftBalance;
	private Double holdPointsBalance;
	private Double holdAmountBalance;
	private Calendar optinDate;
//	private String optinMedium;
	private Calendar lastRedumptionDate;
	private String cardType;
	private String loyaltyType;
	private Calendar lastFechedDate;
	private Calendar createdDate;
	private String contactLoyaltyType;
	private Long userId;
	private Long orgId;
	private byte isRegistered;
	private String mode;
	private Long programTierId;
//	private String programTierName;
	private Calendar tierUpgradedDate;
	private String tierUpgradeReason;
	private Long programId;
	//private Calendar membershipExpirationDate;
	private String membershipStatus; //active/expired/suspended
//	private String storeNumber;
	private Long cardSetId;
	//private String cardNumberType;
	private Long expiredPoints;
	private Double expiredRewardAmount;
	private Double expiredGiftAmount;
	private String rewardFlag; // G -gift, L-loyalty, GL - Gift to Loyalty
	private String membershipPwd;
	private Calendar lastLoggedInTime;
	private Long transferedTo;
	private Calendar transferedOn;
	
	public Long getProgramTierId() {
		return programTierId;
	}
	public void setProgramTierId(Long programTierId) {
		this.programTierId = programTierId;
	}
	/*public String getProgramTierName() {
		return programTierName;
	}
	public void setProgramTierName(String programTierName) {
		this.programTierName = programTierName;
	}*/
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public byte getIsRegistered() {
		return isRegistered;
	}
	public void setIsRegistered(byte isRegistered) {
		this.isRegistered = isRegistered;
	}
	
	public String getPosStoreLocationId() {
		return posStoreLocationId;
	}
	public void setPosStoreLocationId(String posStoreLocationId) {
		this.posStoreLocationId = posStoreLocationId;
	}
	
	public Long getLoyaltyId() {
		return loyaltyId;
	}
	public void setLoyaltyId(Long loyaltyId) {
		this.loyaltyId = loyaltyId;
	}
	public Contacts getContact() {
		return contact;
	}
	public void setContact(Contacts contact) {
		this.contact = contact;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getCardPin() {
		return cardPin;
	}
	public void setCardPin(String cardPin) {
		this.cardPin = cardPin;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public Double getTotalLoyaltyEarned() {
		return totalLoyaltyEarned;
	}
	public void setTotalLoyaltyEarned(Double totalLoyaltyEarned) {
		this.totalLoyaltyEarned = totalLoyaltyEarned;
	}
	public Double getTotalLoyaltyRedemption() {
		return totalLoyaltyRedemption;
	}
	public void setTotalLoyaltyRedemption(Double totalLoyaltyRedemption) {
		this.totalLoyaltyRedemption = totalLoyaltyRedemption;
	}
	public Double getLoyaltyBalance() {
		return loyaltyBalance;
	}
	public void setLoyaltyBalance(Double loyaltyBalance) {
		this.loyaltyBalance = loyaltyBalance;
	}
	public String getValueCode() {
		return valueCode;
	}
	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}
	public Double getTotalGiftcardAmount() {
		return totalGiftcardAmount;
	}
	public void setTotalGiftcardAmount(Double totalGiftcardAmount) {
		this.totalGiftcardAmount = totalGiftcardAmount;
	}
	public Double getTotalGiftcardRedemption() {
		return totalGiftcardRedemption;
	}
	public void setTotalGiftcardRedemption(Double totalGiftcardRedemption) {
		this.totalGiftcardRedemption = totalGiftcardRedemption;
	}
	public Double getGiftcardBalance() {
		return giftcardBalance;
	}
	public void setGiftcardBalance(Double giftcardBalance) {
		this.giftcardBalance = giftcardBalance;
	}
	public Calendar getOptinDate() {
		return optinDate;
	}
	public void setOptinDate(Calendar optinDate) {
		this.optinDate = optinDate;
	}
	/*public String getOptinMedium() {
		return optinMedium;
	}
	public void setOptinMedium(String optinMedium) {
		this.optinMedium = optinMedium;
	}*/
	public Calendar getLastRedumptionDate() {
		return lastRedumptionDate;
	}
	public void setLastRedumptionDate(Calendar lastRedumptionDate) {
		this.lastRedumptionDate = lastRedumptionDate;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getLoyaltyType() {
		return loyaltyType;
	}
	public void setLoyaltyType(String loyaltyType) {
		this.loyaltyType = loyaltyType;
	}
	public Calendar getLastFechedDate() {
		return lastFechedDate;
	}
	public void setLastFechedDate(Calendar lastFechedDate) {
		this.lastFechedDate = lastFechedDate;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	
	public String getContactLoyaltyType() {
		return contactLoyaltyType;
	}
	public void setContactLoyaltyType(String contactLoyaltyType) {
		this.contactLoyaltyType = contactLoyaltyType;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Calendar getTierUpgradedDate() {
		return tierUpgradedDate;
	}
	public void setTierUpgradedDate(Calendar tierUpgradedDate) {
		this.tierUpgradedDate = tierUpgradedDate;
	}
	public String getTierUpgradeReason() {
		return tierUpgradeReason;
	}
	public void setTierUpgradeReason(String tierUpgradeReason) {
		this.tierUpgradeReason = tierUpgradeReason;
	}
	public Long getProgramId() {
		return programId;
	}
	public void setProgramId(Long programId) {
		this.programId = programId;
	}
	/*public Calendar getMembershipExpirationDate() {
		return membershipExpirationDate;
	}
	public void setMembershipExpirationDate(Calendar membershipExpirationDate) {
		this.membershipExpirationDate = membershipExpirationDate;
	}*/
	
	public String getMembershipStatus() {
		return membershipStatus;
	}
	public void setMembershipStatus(String membershipStatus) {
		this.membershipStatus = membershipStatus;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	/*public String getStoreNumber() {
		return storeNumber;
	}
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}*/
	/*public String getCardNumberType() {
		return cardNumberType;
	}
	public void setCardNumberType(String cardNumberType) {
		this.cardNumberType = cardNumberType;
	}*/
	public String getMembershipType() {
		return membershipType;
	}
	public void setMembershipType(String membershipType) {
		this.membershipType = membershipType;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public Double getHoldPointsBalance() {
		return holdPointsBalance;
	}
	public void setHoldPointsBalance(Double holdPointsBalance) {
		this.holdPointsBalance = holdPointsBalance;
	}
	public Double getHoldAmountBalance() {
		return holdAmountBalance;
	}
	public void setHoldAmountBalance(Double holdAmountBalance) {
		this.holdAmountBalance = holdAmountBalance;
	}
	public Long getCardSetId() {
		return cardSetId;
	}
	public void setCardSetId(Long cardSetId) {
		this.cardSetId = cardSetId;
	}
	public Long getExpiredPoints() {
		return expiredPoints;
	}
	public void setExpiredPoints(Long expiredPoints) {
		this.expiredPoints = expiredPoints;
	}
	public Double getExpiredRewardAmount() {
		return expiredRewardAmount;
	}
	public void setExpiredRewardAmount(Double expiredRewardAmount) {
		this.expiredRewardAmount = expiredRewardAmount;
	}
	public Double getExpiredGiftAmount() {
		return expiredGiftAmount;
	}
	public void setExpiredGiftAmount(Double expiredGiftAmount) {
		this.expiredGiftAmount = expiredGiftAmount;
	}
	
	public Double getTotalGiftAmount() {
		return totalGiftAmount;
	}
	public void setTotalGiftAmount(Double totalGiftAmount) {
		this.totalGiftAmount = totalGiftAmount;
	}
	public Double getTotalGiftRedemption() {
		return totalGiftRedemption;
	}
	public void setTotalGiftRedemption(Double totalGiftRedemption) {
		this.totalGiftRedemption = totalGiftRedemption;
	}
	public Double getGiftBalance() {
		return giftBalance;
	}
	public void setGiftBalance(Double giftBalance) {
		this.giftBalance = giftBalance;
	}
	public String getRewardFlag() {
		return rewardFlag;
	}
	public void setRewardFlag(String rewardFlag) {
		this.rewardFlag = rewardFlag;
	}
	public String getMembershipPwd() {
		return membershipPwd;
	}
	public void setMembershipPwd(String membershipPwd) {
		this.membershipPwd = membershipPwd;
	}
	public Calendar getLastLoggedInTime() {
		return lastLoggedInTime;
	}
	public void setLastLoggedInTime(Calendar lastLoggedInTime) {
		this.lastLoggedInTime = lastLoggedInTime;
	}
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
	
	
}
