package org.mq.optculture.model.ocloyalty;

import javax.xml.bind.annotation.XmlElement;

public class MembershipRequest {

	private String issueCardFlag;
	private String cardNumber;
	private String cardPin;
	private String fingerprintValidation;
	private String phoneNumber;
	private String createdDate; 
	private String transactions;//  is created for setting created_date while implementing bulk transaction utility
	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public MembershipRequest() {
	}

	public String getIssueCardFlag() {
		return issueCardFlag;
	}
	@XmlElement(name = "issueCardFlag")	
	public void setIssueCardFlag(String issueCardFlag) {
		this.issueCardFlag = issueCardFlag;
	}

	public String getCardNumber() {
		return cardNumber;
	}
	@XmlElement(name = "cardNumber")
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardPin() {
		return cardPin;
	}
	@XmlElement(name = "cardPin")
	public void setCardPin(String cardPin) {
		this.cardPin = cardPin;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	@XmlElement(name = "phoneNumber")
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFingerprintValidation() {
		return fingerprintValidation;
	}

	public void setFingerprintValidation(String fingerprintValidation) {
		this.fingerprintValidation = fingerprintValidation;
	}

	public String getTransactions() {
		return transactions;
	}

	public void setTransactions(String transactions) {
		this.transactions = transactions;
	}
	
}
