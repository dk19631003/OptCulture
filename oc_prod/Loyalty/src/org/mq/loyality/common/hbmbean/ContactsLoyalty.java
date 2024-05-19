package org.mq.loyality.common.hbmbean;


import java.util.Calendar;

public class ContactsLoyalty implements java.io.Serializable {
	
	private Long loyaltyId;
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
	//private String programTierName;
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
	private Contacts contact;
	private String color;
	private String path;
	private Long transferedTo;
	private Calendar transferedOn;
	
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Contacts getContact() {
		return contact;
	}
	public void setContact(Contacts contact) {
		this.contact = contact;
	}
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
	public Long getTransferedTo() {
		return transferedTo;
	}
	public void setTransferedTo(Long transferedTo) {
		this.transferedTo = transferedTo;
	}
	public Calendar getTransferedOn() {
		return transferedOn;
	}
	public void setTransferedOn(Calendar transferedOn) {
		this.transferedOn = transferedOn;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accountId == null) ? 0 : accountId.hashCode());
		result = prime * result
				+ ((cardNumber == null) ? 0 : cardNumber.hashCode());
		result = prime * result + ((cardPin == null) ? 0 : cardPin.hashCode());
		result = prime * result
				+ ((cardSetId == null) ? 0 : cardSetId.hashCode());
		result = prime * result
				+ ((cardType == null) ? 0 : cardType.hashCode());
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		result = prime
				* result
				+ ((contactLoyaltyType == null) ? 0 : contactLoyaltyType
						.hashCode());
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((empId == null) ? 0 : empId.hashCode());
		result = prime
				* result
				+ ((expiredGiftAmount == null) ? 0 : expiredGiftAmount
						.hashCode());
		result = prime * result
				+ ((expiredPoints == null) ? 0 : expiredPoints.hashCode());
		result = prime
				* result
				+ ((expiredRewardAmount == null) ? 0 : expiredRewardAmount
						.hashCode());
		result = prime * result
				+ ((giftBalance == null) ? 0 : giftBalance.hashCode());
		result = prime * result
				+ ((giftcardBalance == null) ? 0 : giftcardBalance.hashCode());
		result = prime
				* result
				+ ((holdAmountBalance == null) ? 0 : holdAmountBalance
						.hashCode());
		result = prime
				* result
				+ ((holdPointsBalance == null) ? 0 : holdPointsBalance
						.hashCode());
		result = prime * result + isRegistered;
		result = prime * result
				+ ((lastFechedDate == null) ? 0 : lastFechedDate.hashCode());
		result = prime
				* result
				+ ((lastLoggedInTime == null) ? 0 : lastLoggedInTime.hashCode());
		result = prime
				* result
				+ ((lastRedumptionDate == null) ? 0 : lastRedumptionDate
						.hashCode());
		result = prime * result
				+ ((locationId == null) ? 0 : locationId.hashCode());
		result = prime * result
				+ ((loyaltyBalance == null) ? 0 : loyaltyBalance.hashCode());
		result = prime * result
				+ ((loyaltyId == null) ? 0 : loyaltyId.hashCode());
		result = prime * result
				+ ((loyaltyType == null) ? 0 : loyaltyType.hashCode());
		result = prime * result
				+ ((membershipPwd == null) ? 0 : membershipPwd.hashCode());
		result = prime
				* result
				+ ((membershipStatus == null) ? 0 : membershipStatus.hashCode());
		result = prime * result
				+ ((membershipType == null) ? 0 : membershipType.hashCode());
		result = prime * result
				+ ((mobilePhone == null) ? 0 : mobilePhone.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result
				+ ((optinDate == null) ? 0 : optinDate.hashCode());
		result = prime * result + ((orgId == null) ? 0 : orgId.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime
				* result
				+ ((posStoreLocationId == null) ? 0 : posStoreLocationId
						.hashCode());
		result = prime * result
				+ ((programId == null) ? 0 : programId.hashCode());
		result = prime * result
				+ ((programTierId == null) ? 0 : programTierId.hashCode());
		result = prime * result
				+ ((rewardFlag == null) ? 0 : rewardFlag.hashCode());
		result = prime * result
				+ ((serviceType == null) ? 0 : serviceType.hashCode());
		result = prime
				* result
				+ ((tierUpgradeReason == null) ? 0 : tierUpgradeReason
						.hashCode());
		result = prime
				* result
				+ ((tierUpgradedDate == null) ? 0 : tierUpgradedDate.hashCode());
		result = prime * result
				+ ((totalGiftAmount == null) ? 0 : totalGiftAmount.hashCode());
		result = prime
				* result
				+ ((totalGiftRedemption == null) ? 0 : totalGiftRedemption
						.hashCode());
		result = prime
				* result
				+ ((totalGiftcardAmount == null) ? 0 : totalGiftcardAmount
						.hashCode());
		result = prime
				* result
				+ ((totalGiftcardRedemption == null) ? 0
						: totalGiftcardRedemption.hashCode());
		result = prime
				* result
				+ ((totalLoyaltyEarned == null) ? 0 : totalLoyaltyEarned
						.hashCode());
		result = prime
				* result
				+ ((totalLoyaltyRedemption == null) ? 0
						: totalLoyaltyRedemption.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result
				+ ((valueCode == null) ? 0 : valueCode.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContactsLoyalty other = (ContactsLoyalty) obj;
		if (accountId == null) {
			if (other.accountId != null)
				return false;
		} else if (!accountId.equals(other.accountId))
			return false;
		if (cardNumber == null) {
			if (other.cardNumber != null)
				return false;
		} else if (!cardNumber.equals(other.cardNumber))
			return false;
		if (cardPin == null) {
			if (other.cardPin != null)
				return false;
		} else if (!cardPin.equals(other.cardPin))
			return false;
		if (cardSetId == null) {
			if (other.cardSetId != null)
				return false;
		} else if (!cardSetId.equals(other.cardSetId))
			return false;
		if (cardType == null) {
			if (other.cardType != null)
				return false;
		} else if (!cardType.equals(other.cardType))
			return false;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (contact == null) {
			if (other.contact != null)
				return false;
		} else if (!contact.equals(other.contact))
			return false;
		if (contactLoyaltyType == null) {
			if (other.contactLoyaltyType != null)
				return false;
		} else if (!contactLoyaltyType.equals(other.contactLoyaltyType))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		if (empId == null) {
			if (other.empId != null)
				return false;
		} else if (!empId.equals(other.empId))
			return false;
		if (expiredGiftAmount == null) {
			if (other.expiredGiftAmount != null)
				return false;
		} else if (!expiredGiftAmount.equals(other.expiredGiftAmount))
			return false;
		if (expiredPoints == null) {
			if (other.expiredPoints != null)
				return false;
		} else if (!expiredPoints.equals(other.expiredPoints))
			return false;
		if (expiredRewardAmount == null) {
			if (other.expiredRewardAmount != null)
				return false;
		} else if (!expiredRewardAmount.equals(other.expiredRewardAmount))
			return false;
		if (giftBalance == null) {
			if (other.giftBalance != null)
				return false;
		} else if (!giftBalance.equals(other.giftBalance))
			return false;
		if (giftcardBalance == null) {
			if (other.giftcardBalance != null)
				return false;
		} else if (!giftcardBalance.equals(other.giftcardBalance))
			return false;
		if (holdAmountBalance == null) {
			if (other.holdAmountBalance != null)
				return false;
		} else if (!holdAmountBalance.equals(other.holdAmountBalance))
			return false;
		if (holdPointsBalance == null) {
			if (other.holdPointsBalance != null)
				return false;
		} else if (!holdPointsBalance.equals(other.holdPointsBalance))
			return false;
		if (isRegistered != other.isRegistered)
			return false;
		if (lastFechedDate == null) {
			if (other.lastFechedDate != null)
				return false;
		} else if (!lastFechedDate.equals(other.lastFechedDate))
			return false;
		if (lastLoggedInTime == null) {
			if (other.lastLoggedInTime != null)
				return false;
		} else if (!lastLoggedInTime.equals(other.lastLoggedInTime))
			return false;
		if (lastRedumptionDate == null) {
			if (other.lastRedumptionDate != null)
				return false;
		} else if (!lastRedumptionDate.equals(other.lastRedumptionDate))
			return false;
		if (locationId == null) {
			if (other.locationId != null)
				return false;
		} else if (!locationId.equals(other.locationId))
			return false;
		if (loyaltyBalance == null) {
			if (other.loyaltyBalance != null)
				return false;
		} else if (!loyaltyBalance.equals(other.loyaltyBalance))
			return false;
		if (loyaltyId == null) {
			if (other.loyaltyId != null)
				return false;
		} else if (!loyaltyId.equals(other.loyaltyId))
			return false;
		if (loyaltyType == null) {
			if (other.loyaltyType != null)
				return false;
		} else if (!loyaltyType.equals(other.loyaltyType))
			return false;
		if (membershipPwd == null) {
			if (other.membershipPwd != null)
				return false;
		} else if (!membershipPwd.equals(other.membershipPwd))
			return false;
		if (membershipStatus == null) {
			if (other.membershipStatus != null)
				return false;
		} else if (!membershipStatus.equals(other.membershipStatus))
			return false;
		if (membershipType == null) {
			if (other.membershipType != null)
				return false;
		} else if (!membershipType.equals(other.membershipType))
			return false;
		if (mobilePhone == null) {
			if (other.mobilePhone != null)
				return false;
		} else if (!mobilePhone.equals(other.mobilePhone))
			return false;
		if (mode == null) {
			if (other.mode != null)
				return false;
		} else if (!mode.equals(other.mode))
			return false;
		if (optinDate == null) {
			if (other.optinDate != null)
				return false;
		} else if (!optinDate.equals(other.optinDate))
			return false;
		if (orgId == null) {
			if (other.orgId != null)
				return false;
		} else if (!orgId.equals(other.orgId))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (posStoreLocationId == null) {
			if (other.posStoreLocationId != null)
				return false;
		} else if (!posStoreLocationId.equals(other.posStoreLocationId))
			return false;
		if (programId == null) {
			if (other.programId != null)
				return false;
		} else if (!programId.equals(other.programId))
			return false;
		if (programTierId == null) {
			if (other.programTierId != null)
				return false;
		} else if (!programTierId.equals(other.programTierId))
			return false;
		if (rewardFlag == null) {
			if (other.rewardFlag != null)
				return false;
		} else if (!rewardFlag.equals(other.rewardFlag))
			return false;
		if (serviceType == null) {
			if (other.serviceType != null)
				return false;
		} else if (!serviceType.equals(other.serviceType))
			return false;
		if (tierUpgradeReason == null) {
			if (other.tierUpgradeReason != null)
				return false;
		} else if (!tierUpgradeReason.equals(other.tierUpgradeReason))
			return false;
		if (tierUpgradedDate == null) {
			if (other.tierUpgradedDate != null)
				return false;
		} else if (!tierUpgradedDate.equals(other.tierUpgradedDate))
			return false;
		if (totalGiftAmount == null) {
			if (other.totalGiftAmount != null)
				return false;
		} else if (!totalGiftAmount.equals(other.totalGiftAmount))
			return false;
		if (totalGiftRedemption == null) {
			if (other.totalGiftRedemption != null)
				return false;
		} else if (!totalGiftRedemption.equals(other.totalGiftRedemption))
			return false;
		if (totalGiftcardAmount == null) {
			if (other.totalGiftcardAmount != null)
				return false;
		} else if (!totalGiftcardAmount.equals(other.totalGiftcardAmount))
			return false;
		if (totalGiftcardRedemption == null) {
			if (other.totalGiftcardRedemption != null)
				return false;
		} else if (!totalGiftcardRedemption
				.equals(other.totalGiftcardRedemption))
			return false;
		if (totalLoyaltyEarned == null) {
			if (other.totalLoyaltyEarned != null)
				return false;
		} else if (!totalLoyaltyEarned.equals(other.totalLoyaltyEarned))
			return false;
		if (totalLoyaltyRedemption == null) {
			if (other.totalLoyaltyRedemption != null)
				return false;
		} else if (!totalLoyaltyRedemption.equals(other.totalLoyaltyRedemption))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (valueCode == null) {
			if (other.valueCode != null)
				return false;
		} else if (!valueCode.equals(other.valueCode))
			return false;
		return true;
	}
	
	
	
	
	
	
}
