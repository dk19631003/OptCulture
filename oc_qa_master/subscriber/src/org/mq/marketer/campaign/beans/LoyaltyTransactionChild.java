package org.mq.marketer.campaign.beans;

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
	private Long transactionId; //loyalty transaction parent id
	private String membershipNumber; 
	private String membershipType; //Card/Mobile
	private String transactionType;//Enrollment,Issuance,Bonus,Redemption,Adjustment,Inquiry,Expiry,OTP,Return
	private Long programId;
	private Long cardSetId;
	private Long tierId;
	private Long userId;
	private Long orgId;
	private Double enteredAmount;// entered amount json field(issuance:purchase amt / gift amount when it is purchase we wont consider value code value,
	//redem : it may be points or currency return : amount it may be storecredit / reversal)
	private Double excludedAmount;//product exclusion(only issuance)
	private String enteredAmountType; //Gift & Purchase(issuance), Bonus(bonus trx),
	// PointsRedeem(redeeming in points VC=points), AmountRedeem(redeeming amount VC=currency), 
	//PointsAdjustment and AmountAdjustment(adjustments),IssuanceReversal,RedemptionReversal,StoreCredit(for a single DocSID these 3 trx are multiple)
	private String pointsDifference;//redemption(-ve) & redemRevrsal(these values from actual redem trx will be taken n stored in desc)
	private String amountDifference;
	private String rewardDifference;//For special rewards
	private String giftDifference;
	private Double pointsBalance;//current balance including this trx
	private Double amountBalance;
	private Double rewardBalance;//For special rewards
	private Double giftBalance;
	private Calendar createdDate;
	private Date valueActivationDate;// activation date of the earned amount or points based on the tiers activate after setting.
	private String earnType;// Points / Currency(due to issuance / bonus /adjustments /return-redemption reversal(points/amount) & store credit)
	private Double earnedValue;//For storing  value-codes amount.
	private Double earnedPoints;//(due to issuance / bonus /adjustments /return-redemption reversal(earnType=points/amount) )
	private Double earnedAmount;//(due to issuance / bonus /adjustments /return-redemption reversal(earnType=points/amount) & store credit)
	private Double earnedReward; //Have to see where to use this value in the flow. Used for special rewards.
	private Double conversionAmt;// amount converted after points conversion to amt(redemption points conversion to amt ondemand, in issuance autoconversion)
	private String earnStatus;//New,Working,Processed,Suspended - used for activate after timer to pick new transactions
	private String storeNumber;// store number field in the request
	private String subsidiaryNumber; // subsidiary number field in the OC Json request(store_location_id in sbtooc json)
	private String receiptNumber;
	private Double receiptAmount;
	private String docSID;// docSID field in the request(not from original recpt)
	private String sourceType; //Store, Auto,Manual,Webform
	private String description;// conversion rule(iss & red)  , otp(redemption) , returned amount(iss reversal), returned amount and differences(redemption rev)
	private String description2;// used in reversal type return transactions to store the original receipt dosSID and in Specialreward it holds an itemSID n qty
//	private String eventTriggStatus;
	private Long contactId; 
	private String earnedRule;//For Special rewards,where will it come in case of redemption
//	private String triggerIds;
	private Long loyaltyId; // contacts_loyalty primary key
	private Double holdPoints;//(issuance activate after setting if there)
	private Double holdAmount;
	private Long redeemedOn;//used in redemption reversal. the redemption transaction primary key on which the redemption reversal is done.
	private Long transferedTo;
	private Calendar transferedOn;
	private String employeeId;
	private String terminalId;
	private String valueCode;
	private Long valueCodeId;//special rewards
	private Double excludedItemAmount;//special rewards
	private Long specialRewardId;
	private double totItemPrice;
	private Double issuanceAmount;//during issuance what was the amount to be considered.
	private String itemInfo;// item details like itemsid:qty:price,
	private String itemRewardsInfo;

    public double getTotItemPrice() {
        return totItemPrice;
    }
    public void setTotItemPrice(double totItemPrice) {
        this.totItemPrice = totItemPrice;
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
	
	public String getSubsidiaryNumber() {
		return subsidiaryNumber;
	}


	public void setSubsidiaryNumber(String subsidiaryNumber) {
		this.subsidiaryNumber = subsidiaryNumber;
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


	public Double getConversionAmt() {
		return conversionAmt;
	}


	public void setConversionAmt(Double conversionAmt) {
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
	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	public String getReceiptNumber() {
		return receiptNumber;
	}


	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}


	public Double getReceiptAmount() {
		return receiptAmount;
	}


	public void setReceiptAmount(Double receiptAmount) {
		this.receiptAmount = receiptAmount;
	}


	public Double getEarnedValue() {
		return earnedValue;
	}


	public void setEarnedValue(Double earnedValue) {
		this.earnedValue = earnedValue;
	}


	public String getEarnedRule() {
		return earnedRule;
	}


	public void setEarnedRule(String earnedRule) {
		this.earnedRule = earnedRule;
	}


	public Double getEarnedReward() {
		return earnedReward;
	}


	public void setEarnedReward(Double earnedReward) {
		this.earnedReward = earnedReward;
	}


	public Double getRewardBalance() {
		return rewardBalance;
	}


	public void setRewardBalance(Double rewardBalance) {
		this.rewardBalance = rewardBalance;
	}


	public String getRewardDifference() {
		return rewardDifference;
	}


	public void setRewardDifference(String rewardDifference) {
		this.rewardDifference = rewardDifference;
	}


	public String getValueCode() {
		return valueCode;
	}


	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}


	public Long getValueCodeId() {
		return valueCodeId;
	}


	public void setValueCodeId(Long valueCodeId) {
		this.valueCodeId = valueCodeId;
	}


	public Double getExcludedItemAmount() {
		return excludedItemAmount;
	}


	public void setExcludedItemAmount(Double excludedItemAmount) {
		this.excludedItemAmount = excludedItemAmount;
	}


	public Long getSpecialRewardId() {
		return specialRewardId;
	}


	public void setSpecialRewardId(Long specialRewardId) {
		this.specialRewardId = specialRewardId;
	}
	
	public String getItemInfo() {
		return itemInfo;
	}
	public void setItemInfo(String itemInfo) {
		this.itemInfo = itemInfo;
	}
	public Double getIssuanceAmount() {
		return issuanceAmount;
	}
	public void setIssuanceAmount(Double issuanceAmount) {
		this.issuanceAmount = issuanceAmount;
	}
	public String getItemRewardsInfo() {
		return itemRewardsInfo;
	}
	public void setItemRewardsInfo(String itemRewardsInfo) {
		this.itemRewardsInfo = itemRewardsInfo;
	}

	}
