package org.mq.optculture.model.ocloyalty;

import javax.xml.bind.annotation.XmlElement;

/**
 * OC Loyalty program enrolment request JSON Object 
 * 
 * @author Venkata Rathnam D 
 *
 */
public class LoyaltyEnrollRequest {

	private RequestHeader header;
	private MembershipRequest membership;
	private Customer customer;
	private LoyaltyUser user;
	
	public LoyaltyEnrollRequest() {
	}

	public RequestHeader getHeader() {
		return header;
	}
	@XmlElement(name = "header")
	public void setHeader(RequestHeader header) {
		this.header = header;
	}
		
	public Customer getCustomer() {
		return customer;
	}
	@XmlElement(name = "customer")
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LoyaltyUser getUser() {
		return user;
	}
	@XmlElement(name = "user")
	public void setUser(LoyaltyUser user) {
		this.user = user;
	}

	public MembershipRequest getMembership() {
		return membership;
	}
	@XmlElement(name = "membership")
	public void setMembership(MembershipRequest membership) {
		this.membership = membership;
	}
	
}
