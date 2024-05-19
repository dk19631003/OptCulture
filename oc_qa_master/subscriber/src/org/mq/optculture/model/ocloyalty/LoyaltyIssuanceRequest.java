package org.mq.optculture.model.ocloyalty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.mq.optculture.model.BaseRequestObject;

public class LoyaltyIssuanceRequest extends BaseRequestObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6831961210923846896L;
	private RequestHeader header;
	private MembershipRequest membership;
	private Amount amount;
	private Discounts discounts;
	private List<SkuDetails> items;
	private Customer customer;
	private LoyaltyUser user;
	
	public LoyaltyIssuanceRequest(){
		//Default Constructor
	}

	public RequestHeader getHeader() {
		return header;
	}
	@XmlElement(name = "header")
	public void setHeader(RequestHeader header) {
		this.header = header;
	}

	public LoyaltyUser getUser() {
		return user;
	}
	@XmlElement(name = "user")
	public void setUser(LoyaltyUser user) {
		this.user = user;
	}

	public List<SkuDetails> getItems() {
		return items;
	}
	@XmlElement(name = "items")
	public void setItems(List<SkuDetails> items) {
		this.items = items;
	}

	public Customer getCustomer() {
		return customer;
	}
	@XmlElement(name = "customer")
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Amount getAmount() {
		return amount;
	}
	@XmlElement(name = "amount")
	public void setAmount(Amount amount) {
		this.amount = amount;
	}

	public Discounts getDiscounts() {
		return discounts;
	}
	@XmlElement(name = "discounts")
	public void setDiscounts(Discounts discounts) {
		this.discounts = discounts;
	}

	public MembershipRequest getMembership() {
		return membership;
	}
	@XmlElement(name = "membership")
	public void setMembership(MembershipRequest membership) {
		this.membership = membership;
	}

}
