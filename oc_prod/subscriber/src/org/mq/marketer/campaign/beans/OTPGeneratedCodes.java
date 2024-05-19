package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class OTPGeneratedCodes {

	private Long otpCodeId;
	private String phoneNumber;
	private String otpCode;
	private Calendar createdDate;
	private Long userId;
	private String status;
	private Long sentCount;
	private String docsid;
	private String transactionType;
	private Double receiptAmount;
	private String email;
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Double getReceiptAmount() {
		return receiptAmount;
	}
	public void setReceiptAmount(Double receiptAmount) {
		this.receiptAmount = receiptAmount;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}	
	public String getDocsid() {
		return docsid;
	}
	public void setDocsid(String docsid) {
		this.docsid = docsid;
	}
	public Long getOtpCodeId() {
		return otpCodeId;
	}
	public void setOtpCodeId(Long otpCodeId) {
		this.otpCodeId = otpCodeId;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getOtpCode() {
		return otpCode;
	}
	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getSentCount() {
		return sentCount;
	}
	public void setSentCount(Long sentCount) {
		this.sentCount = sentCount;
	}
	
	
}
