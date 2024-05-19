package org.mq.optculture.model.ocloyalty;

import java.util.List;

public class LoyaltyTransferMembershipJsonResponse {



	private ResponseHeader header;
	private MembershipResponse membership;
	private List<Balance> balances;
	private HoldBalance holdBalance;
	private List<MatchedCustomer> matchedCustomers;
	private Status status;
	
	public LoyaltyTransferMembershipJsonResponse() {
	}

	public ResponseHeader getHeader() {
		return header;
	}

	public void setHeader(ResponseHeader header) {
		this.header = header;
	}

	/*public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}*/

	public List<MatchedCustomer> getMatchedCustomers() {
		return matchedCustomers;
	}

	public void setMatchedCustomers(List<MatchedCustomer> matchedCustomers) {
		this.matchedCustomers = matchedCustomers;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public MembershipResponse getMembership() {
		return membership;
	}

	public void setMembership(MembershipResponse membership) {
		this.membership = membership;
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

	
}
