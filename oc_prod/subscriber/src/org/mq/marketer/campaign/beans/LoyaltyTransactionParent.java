package org.mq.marketer.campaign.beans;

import java.util.Calendar;

/**
 * 
 * @author Venkata Rathnam D
 * It logs loyalty transactions such as enrolment, issuance, redemption, inquiry etc. Its transactionId is used
 * in JSON response and further processing in service layer.
 */
public class LoyaltyTransactionParent {

	private Long transactionId;
	private String requestId;
	private String requestDate;
	private String pcFlag;
	private String membershipNumber;
	private String mobilePhone;
	private String transactionType;
	private Calendar createdDate;
	private String userName;
	private String status; //Success,Failure
	private String errorMessage; //status message sent in the response
	
	public LoyaltyTransactionParent() {
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

	/*public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}*/

	public String getPcFlag() {
		return pcFlag;
	}

	public void setPcFlag(String pcFlag) {
		this.pcFlag = pcFlag;
	}

	/*public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}*/

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	/*public Double getEnteredAmount() {
		return enteredAmount;
	}

	public void setEnteredAmount(Double enteredAmount) {
		this.enteredAmount = enteredAmount;
	}

	public String getValueCode() {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public String getMembershipNumber() {
		return membershipNumber;
	}

	public void setMembershipNumber(String membershipNumber) {
		this.membershipNumber = membershipNumber;
	}
	
}
