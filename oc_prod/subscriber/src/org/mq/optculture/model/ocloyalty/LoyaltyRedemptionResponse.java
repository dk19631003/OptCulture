package org.mq.optculture.model.ocloyalty;

import java.util.List;

public class LoyaltyRedemptionResponse {

	private ResponseHeader header;
	private MembershipResponse membership;
	private List<Balance> balances;
	private HoldBalance holdBalance;
	private AdditionalInfo additionalInfo;
	private List<MatchedCustomer> matchedCustomers;
	private Status status;
	
	public LoyaltyRedemptionResponse() {
	}

	public ResponseHeader getHeader() {
		return header;
	}

	public void setHeader(ResponseHeader header) {
		this.header = header;
	}

	public List<Balance> getBalances() {
		return balances;
	}

	public void setBalances(List<Balance> balances) {
		this.balances = balances;
	}

	public HoldBalance getHoldBalance() {
		return holdBalance;
	}

	public void setHoldBalance(HoldBalance holdBalance) {
		this.holdBalance = holdBalance;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<MatchedCustomer> getMatchedCustomers() {
		return matchedCustomers;
	}

	public void setMatchedCustomers(List<MatchedCustomer> matchedCustomers) {
		this.matchedCustomers = matchedCustomers;
	}

	public MembershipResponse getMembership() {
		return membership;
	}

	public void setMembership(MembershipResponse membership) {
		this.membership = membership;
	}

	public AdditionalInfo getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(AdditionalInfo additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
	
}
