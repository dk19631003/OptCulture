package org.mq.optculture.model.updatecontacts;
import javax.xml.bind.annotation.XmlElement;

import org.mq.optculture.model.BaseRequestObject;

public class ContactRequest extends BaseRequestObject{
	private Header header;
	private User user;
	private Customer customer;

	public ContactRequest() {
	}
	public ContactRequest(Header header, User user,Customer customer) {
		this.header = header;
		this.user = user;
		this.customer = customer;
	}
	
	public Header getHeader() {
		return header;
	}
	
	@XmlElement(name = "header")
	public void setHeader(Header header) {
		this.header = header;
	}
	public User getUser() {
		return user;
	}
	
	@XmlElement(name = "user")
	public void setUser(User user) {
		this.user = user;
	}
	public Customer getCustomer() {
		return customer;
	}
	
	@XmlElement(name = "customer")
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
}
