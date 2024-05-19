package org.mq.optculture.model.ocloyalty;


import org.mq.optculture.model.BaseRequestObject;

public class LoyaltyInquiryRequest extends BaseRequestObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6831961210923846896L;
	//private InquiryHeadRequest header;
	private RequestHeader header;
	private MembershipRequest membership;
	private Customer customer;
	private LoyaltyUser user;
	private Amount amount;
	
	public LoyaltyInquiryRequest(){
		//Default Constructor
	}

	public RequestHeader getHeader() {
		return header;
	}

	public void setHeader(RequestHeader header) {
		this.header = header;
	}

	public LoyaltyUser getUser() {
		return user;
	}

	public void setUser(LoyaltyUser user) {
		this.user = user;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public MembershipRequest getMembership() {
		return membership;
	}

	public void setMembership(MembershipRequest membership) {
		this.membership = membership;
	}

	public Amount getAmount() {
		return amount;
	}

	public void setAmount(Amount amount) {
		this.amount = amount;
	}


}
