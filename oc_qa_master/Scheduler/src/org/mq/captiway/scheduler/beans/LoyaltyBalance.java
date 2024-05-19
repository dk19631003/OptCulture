package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class LoyaltyBalance {

	 private Long lbId;
	 private Long  orgId;
	 private Long userId;
	 private Calendar createdDate;
	 private String valueCode;
	 private Long balance;
	 private Double totalEarnedBalance;
	 private Double totalRedeemedBalance;
	 private Double totalExpiredBalance;
	 private Long loyaltyId; 
	 private String memberShipNumber;
	 private Long programId;
	 
	public Long getLbId() {
		return lbId;
	}
	public void setLbId(Long lbId) {
		this.lbId = lbId;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public String getValueCode() {
		return valueCode;
	}
	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}
	public Long getBalance() {
		return balance;
	}
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	public Long getLoyaltyId() {
		return loyaltyId;
	}
	public void setLoyaltyId(Long loyaltyId) {
		this.loyaltyId = loyaltyId;
	}
	public String getMemberShipNumber() {
		return memberShipNumber;
	}
	public void setMemberShipNumber(String memberShipNumber) {
		this.memberShipNumber = memberShipNumber;
	}
	public Double getTotalEarnedBalance() {
		return totalEarnedBalance;
	}
	public void setTotalEarnedBalance(Double totalEarnedBalance) {
		this.totalEarnedBalance = totalEarnedBalance;
	}
	public Double getTotalRedeemedBalance() {
		return totalRedeemedBalance;
	}
	public void setTotalRedeemedBalance(Double totalRedeemedBalance) {
		this.totalRedeemedBalance = totalRedeemedBalance;
	}
	public Double getTotalExpiredBalance() {
		return totalExpiredBalance;
	}
	public void setTotalExpiredBalance(Double totalExpiredBalance) {
		this.totalExpiredBalance = totalExpiredBalance;
	}
	public Long getProgramId() {
		return programId;
	}
	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	 
	 
	 
	 
}
