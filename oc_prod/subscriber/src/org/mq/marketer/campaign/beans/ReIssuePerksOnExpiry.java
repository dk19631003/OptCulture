package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class ReIssuePerksOnExpiry {
	
	private Long reIssuePerkId;
	private Long programId;
	private Long tierId;
	private Calendar createdDate;
	private Long loyaltyId;
	private String valueCode;
	private Long userId;
	private Long transactionId;
	private String status;
	
	public ReIssuePerksOnExpiry() {
		
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getValueCode() {
		return valueCode;
	}

	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}

	public Long getLoyaltyId() {
		return loyaltyId;
	}

	public void setLoyaltyId(Long loyaltyId) {
		this.loyaltyId = loyaltyId;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public Long getTierId() {
		return tierId;
	}

	public void setTierId(Long tierId) {
		this.tierId = tierId;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public Long getReIssuePerkId() {
		return reIssuePerkId;
	}

	public void setReIssuePerkId(Long reIssuePerkId) {
		this.reIssuePerkId = reIssuePerkId;
	}

}
