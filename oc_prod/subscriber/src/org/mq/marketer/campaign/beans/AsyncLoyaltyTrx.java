package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class AsyncLoyaltyTrx {

	private Long id;
	private String trxType;
	private Calendar createdTime;
	private Calendar processedTime;
	private String status;
	private String statusCode;
	private LoyaltyTransaction loyaltyTransaction;
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public LoyaltyTransaction getLoyaltyTransaction() {
		return loyaltyTransaction;
	}
	public void setLoyaltyTransaction(LoyaltyTransaction loyaltyTransaction) {
		this.loyaltyTransaction = loyaltyTransaction;
	}
	public String getTrxType() {
		return trxType;
	}
	public void setTrxType(String trxType) {
		this.trxType = trxType;
	}
	public Calendar getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Calendar createdTime) {
		this.createdTime = createdTime;
	}
	public Calendar getProcessedTime() {
		return processedTime;
	}
	public void setProcessedTime(Calendar processedTime) {
		this.processedTime = processedTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
}

