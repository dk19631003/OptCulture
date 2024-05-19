package org.mq.optculture.model.ocloyalty;

public class Debit {

	private String membershipNumber;
	private String rewardPoints;
	private String rewardCurrency;
	private String holdPoints;
	private String holdCurrency;
	private String remainderToDebit;
	

	public Debit() {
	}
	
	public String getMembershipNumber() {
		return membershipNumber;
	}
	public void setMembershipNumber(String membershipNumber) {
		this.membershipNumber = membershipNumber;
	}
	public String getRewardPoints() {
		return rewardPoints;
	}
	public void setRewardPoints(String rewardPoints) {
		this.rewardPoints = rewardPoints;
	}
	public String getRewardCurrency() {
		return rewardCurrency;
	}
	public void setRewardCurrency(String rewardCurrency) {
		this.rewardCurrency = rewardCurrency;
	}
	public String getHoldPoints() {
		return holdPoints;
	}
	public void setHoldPoints(String holdPoints) {
		this.holdPoints = holdPoints;
	}
	public String getHoldCurrency() {
		return holdCurrency;
	}
	public void setHoldCurrency(String holdCurrency) {
		this.holdCurrency = holdCurrency;
	}

	public String getRemainderToDebit() {
		return remainderToDebit;
	}

	public void setRemainderToDebit(String remainderToDebit) {
		this.remainderToDebit = remainderToDebit;
	}
	
}
