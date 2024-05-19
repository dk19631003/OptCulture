package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Venkata Rathnam D
 * It is a child table of LoyaltyTransactionParent. It logs all successful loyalty transactions 
 * such as enrolment, issuance, redemption, inquiry etc.
 */
public class LoyaltyTransactionChild {

	private Long transChildId;
	private Long transactionId;
	private String membershipNumber;
	private String membershipType;
	private String transactionType;
	private Long programId;
	private Long cardSetId;
	private Long tierId;
	private Long userId;
	private Long orgId;
	private Double enteredAmount;
	private Double excludedAmount;
	private String enteredAmountType; //Gift, Purchase, Bonus, PointsRedeem, AmountRedeem, PointsAdjustment and AmountAdjustment
	private String pointsDifference;
	private String amountDifference;
	private String giftDifference;
	private Double pointsBalance;
	private Double amountBalance;
	private Double giftBalance;
	private Calendar createdDate;
	private Date valueActivationDate;
	private String earnType;
	private Double earnedPoints;
	private Double earnedAmount;
	private Long conversionAmt;
	private String earnStatus;
	private String storeNumber;
	private String docSID;
	private String sourceType; //Store, Auto
	private String description;
	private String description2;
//	private String eventTriggStatus;
	private Long contactId;
//	private String triggerIds;
	private Long loyaltyId;
	private Double holdPoints;
	private Double holdAmount;
	private Long redeemedOn;
	private Long transferedTo;
	private Calendar transferedOn;
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

	public LoyaltyTransactionChild() {
	}

	
	public Long getTransChildId() {
		return transChildId;
	}


	public void setTransChildId(Long transChildId) {
		this.transChildId = transChildId;
	}


	/*public String getOptInMedium() {
		return optInMedium;
	}


	public void setOptInMedium(String optInMedium) {
		this.optInMedium = optInMedium;
	}*/


	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	/*public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
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
	}*/

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

	/*public String getValueCode() {
		return valueCode;
	}

	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}*/

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public Date getValueActivationDate() {
		return valueActivationDate;
	}

	public void setValueActivationDate(Date valueActivationDate) {
		this.valueActivationDate = valueActivationDate;
	}

	/*public String getExclusionReason() {
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
	}*/


	public String getPointsDifference() {
		return pointsDifference;
	}


	public void setPointsDifference(String pointsDifference) {
		this.pointsDifference = pointsDifference;
	}


	public String getAmountDifference() {
		return amountDifference;
	}


	public void setAmountDifference(String amountDifference) {
		this.amountDifference = amountDifference;
	}


	public Long getTierId() {
		return tierId;
	}

	public void setTierId(Long tierId) {
		this.tierId = tierId;
	}

	/*public Double getEnteredAmount() {
		return enteredAmount;
	}

	public void setEnteredAmount(Double enteredAmount) {
		this.enteredAmount = enteredAmount;
	}*/

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

	/*public Double getExcludedPoints() {
		return excludedPoints;
	}

	public void setExcludedPoints(Double excludedPoints) {
		this.excludedPoints = excludedPoints;
	}*/

	/*public Double getNetEarnedPoints() {
		return netEarnedPoints;
	}

	public void setNetEarnedPoints(Double netEarnedPoints) {
		this.netEarnedPoints = netEarnedPoints;
	}*/

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

	/*public Double getNetEarnedAmount() {
		return netEarnedAmount;
	}

	public void setNetEarnedAmount(Double netEarnedAmount) {
		this.netEarnedAmount = netEarnedAmount;
	}*/

	public String getEarnStatus() {
		return earnStatus;
	}

	public void setEarnStatus(String earnStatus) {
		this.earnStatus = earnStatus;
	}

	/*public String getExpiryStatus() {
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
	}*/


	public String getStoreNumber() {
		return storeNumber;
	}


	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}

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


	/*public Long getContactId() {
		return contactId;
	}


	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}*/


	public Double getEnteredAmount() {
		return enteredAmount;
	}


	public void setEnteredAmount(Double enteredAmount) {
		this.enteredAmount = enteredAmount;
	}


	public String getEnteredAmountType() {
		return enteredAmountType;
	}


	public void setEnteredAmountType(String enteredAmountType) {
		this.enteredAmountType = enteredAmountType;
	}


	public String getDocSID() {
		return docSID;
	}


	public void setDocSID(String docSID) {
		this.docSID = docSID;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getDescription2() {
		return description2;
	}


	public void setDescription2(String description2) {
		this.description2 = description2;
	}


	public Long getConversionAmt() {
		return conversionAmt;
	}


	public void setConversionAmt(Long conversionAmt) {
		this.conversionAmt = conversionAmt;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}


	public String getGiftDifference() {
		return giftDifference;
	}


	public void setGiftDifference(String giftDifference) {
		this.giftDifference = giftDifference;
	}


	public Double getGiftBalance() {
		return giftBalance;
	}


	public void setGiftBalance(Double giftBalance) {
		this.giftBalance = giftBalance;
	}


	/*public String getEventTriggStatus() {
		return eventTriggStatus;
	}


	public void setEventTriggStatus(String eventTriggStatus) {
		this.eventTriggStatus = eventTriggStatus;
	}*/


	public Long getContactId() {
		return contactId;
	}


	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}


	/*public String getTriggerIds() {
		return triggerIds;
	}


	public void setTriggerIds(String triggerIds) {
		this.triggerIds = triggerIds;
	}*/
	
	public Long getLoyaltyId() {
		return loyaltyId;
	}

	public void setLoyaltyId(Long loyaltyId) {
		this.loyaltyId = loyaltyId;
	}

	public Double getHoldPoints() {
		return holdPoints;
	}


	public void setHoldPoints(Double holdPoints) {
		this.holdPoints = holdPoints;
	}


	public Double getHoldAmount() {
		return holdAmount;
	}


	public void setHoldAmount(Double holdAmount) {
		this.holdAmount = holdAmount;
	}

	public Long getRedeemedOn() {
		return redeemedOn;
	}


	public void setRedeemedOn(Long redeemedOn) {
		this.redeemedOn = redeemedOn;
	}
	
}
