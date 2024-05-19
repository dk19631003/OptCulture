package org.mq.optculture.model.ocloyalty;

public class LoyaltyRedemptionRequest {

	private RequestHeader header;
	private MembershipRequest membership;
	private Amount amount;
	private String otpCode;
	private Discounts discounts;
	private Customer customer;
	private LoyaltyUser user;
	
	public LoyaltyRedemptionRequest() {
	}

	public Amount getAmount() {
		return amount;
	}

	public void setAmount(Amount amount) {
		this.amount = amount;
	}

	public String getOtpCode() {
		return otpCode;
	}

	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}

	public Discounts getDiscounts() {
		return discounts;
	}

	public void setDiscounts(Discounts discounts) {
		this.discounts = discounts;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LoyaltyUser getUser() {
		return user;
	}

	public void setUser(LoyaltyUser user) {
		this.user = user;
	}

	public RequestHeader getHeader() {
		return header;
	}

	public void setHeader(RequestHeader header) {
		this.header = header;
	}

	public MembershipRequest getMembership() {
		return membership;
	}

	public void setMembership(MembershipRequest membership) {
		this.membership = membership;
	}
	
	
}
