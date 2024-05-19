package org.mq.marketer.campaign.beans;

import java.util.Calendar;

/**
 * 
 * @author Venkata Rathnam D
 * It handles data of loyalty transactions such as enrolment, issuance, redemption, inquiry etc.
 */
public class LoyaltyProgramTrans {

	private Long transactionId;
	private String requestId;
	private String cardNumber;
	private String cardPin;
	private Long contactId;
	private String customerId;
	//private String emailId;
	//private Long mobilePhone;
	private String transactionType;
	private Long programId;
	private Long cardSetId;
	private Long userId;
	private Long orgId;
	private Double enteredAmount;
	private String valueCode;
	private String difference;
	private Double pointsBalance;
	private Double amountBalance;
	private Calendar createdDate;
	private Calendar valueActivationDate;
	private String earnType;
	private Double earnedPoints;
	private Double excludedPoints;
	private Double netEarnedPoints;
	private Double earnedAmount;
	private Double excludedAmount;
	private Double netEarnedAmount;
	private String exclusionReason;
	private Calendar valueExpirationDate;
	private String netEarnedValueStatus;//active/inactive/expired
	private Long tierId;
	private String earnStatus;
	private String expiryStatus;
	private Double availableValue;
	private String loyaltyOptInMedium;

	public LoyaltyProgramTrans() {
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
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

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/*public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Long getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(Long mobilePhone) {
		this.mobilePhone = mobilePhone;
	}*/

	public Long getProgramId() {
		return programId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public Long getCardSetId() {
		return cardSetId;
	}

	public void setCardSetId(Long cardSetId) {
		this.cardSetId = cardSetId;
	}

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

	public String getValueCode() {
		return valueCode;
	}

	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public Calendar getValueActivationDate() {
		return valueActivationDate;
	}

	public void setValueActivationDate(Calendar valueActivationDate) {
		this.valueActivationDate = valueActivationDate;
	}

	public String getExclusionReason() {
		return exclusionReason;
	}

	public void setExclusionReason(String exclusionReason) {
		this.exclusionReason = exclusionReason;
	}

	public Calendar getValueExpirationDate() {
		return valueExpirationDate;
	}

	public void setValueExpirationDate(Calendar valueExpirationDate) {
		this.valueExpirationDate = valueExpirationDate;
	}

	public String getNetEarnedValueStatus() {
		return netEarnedValueStatus;
	}

	public void setNetEarnedValueStatus(String netEarnedValueStatus) {
		this.netEarnedValueStatus = netEarnedValueStatus;
	}

	public String getDifference() {
		return difference;
	}

	public void setDifference(String difference) {
		this.difference = difference;
	}

	public Long getTierId() {
		return tierId;
	}

	public void setTierId(Long tierId) {
		this.tierId = tierId;
	}

	public Double getEnteredAmount() {
		return enteredAmount;
	}

	public void setEnteredAmount(Double enteredAmount) {
		this.enteredAmount = enteredAmount;
	}

	public Double getPointsBalance() {
		return pointsBalance;
	}

	public void setPointsBalance(Double pointsBalance) {
		this.pointsBalance = pointsBalance;
	}

	public Double getAmountBalance() {
		return amountBalance;
	}

	public void setAmountBalance(Double amountBalance) {
		this.amountBalance = amountBalance;
	}

	public Double getEarnedPoints() {
		return earnedPoints;
	}

	public void setEarnedPoints(Double earnedPoints) {
		this.earnedPoints = earnedPoints;
	}

	public Double getExcludedPoints() {
		return excludedPoints;
	}

	public void setExcludedPoints(Double excludedPoints) {
		this.excludedPoints = excludedPoints;
	}

	public Double getNetEarnedPoints() {
		return netEarnedPoints;
	}

	public void setNetEarnedPoints(Double netEarnedPoints) {
		this.netEarnedPoints = netEarnedPoints;
	}

	public String getEarnType() {
		return earnType;
	}

	public void setEarnType(String earnType) {
		this.earnType = earnType;
	}

	public Double getEarnedAmount() {
		return earnedAmount;
	}

	public void setEarnedAmount(Double earnedAmount) {
		this.earnedAmount = earnedAmount;
	}

	public Double getExcludedAmount() {
		return excludedAmount;
	}

	public void setExcludedAmount(Double excludedAmount) {
		this.excludedAmount = excludedAmount;
	}

	public Double getNetEarnedAmount() {
		return netEarnedAmount;
	}

	public void setNetEarnedAmount(Double netEarnedAmount) {
		this.netEarnedAmount = netEarnedAmount;
	}

	public String getEarnStatus() {
		return earnStatus;
	}

	public void setEarnStatus(String earnStatus) {
		this.earnStatus = earnStatus;
	}

	public String getExpiryStatus() {
		return expiryStatus;
	}

	public void setExpiryStatus(String expiryStatus) {
		this.expiryStatus = expiryStatus;
	}

	public Double getAvailableValue() {
		return availableValue;
	}

	public void setAvailableValue(Double availableValue) {
		this.availableValue = availableValue;
	}

	public String getLoyaltyOptInMedium() {
		return loyaltyOptInMedium;
	}

	public void setLoyaltyOptInMedium(String loyaltyOptInMedium) {
		this.loyaltyOptInMedium = loyaltyOptInMedium;
	}
	
	
}
