package org.mq.optculture.model.ocloyalty;

import java.util.List;

import org.mq.optculture.model.BaseRequestObject;

public class LoyaltyReturnTransactionResponse extends BaseRequestObject{

	private ResponseHeader header;
	private MembershipResponse membership;
	private List<Balance> balances;
	private HoldBalance holdBalance;
	private BalancesAdditionalInfo additionalInfo;
	private List<MatchedCustomer> matchedCustomers;
	private Status status;
	
	public LoyaltyReturnTransactionResponse(){
		//Default Constructor
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

	public HoldBalance getHoldBalance() {
		return holdBalance;
	}

	public void setHoldBalance(HoldBalance holdBalance) {
		this.holdBalance = holdBalance;
	}

	public BalancesAdditionalInfo getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(BalancesAdditionalInfo additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
}
