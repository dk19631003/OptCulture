package org.mq.optculture.model.importcontact;

import java.util.List;

import org.mq.optculture.model.loyalty.Customers;

public class Lookup {

	private String emailAddress;
	private String phone;
	private String membershipNumber;
	//APP-3622
	private String firstName,lastName;
	private List<Customers> customer;

	
	public List<Customers> getCustomer() {
		return customer;
	}

	public void setCustomer(List<Customers> customer) {
		this.customer = customer;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMembershipNumber() {
		return membershipNumber;
	}

	public void setMembershipNumber(String membershipNumber) {
		this.membershipNumber = membershipNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
