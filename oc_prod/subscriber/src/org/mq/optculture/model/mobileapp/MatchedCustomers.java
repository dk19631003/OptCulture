package org.mq.optculture.model.mobileapp;

import java.util.List;

public class MatchedCustomers {

	private String customerId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String phone;
	private String membershipNumber;
	private List<SaleTransaction> transactions;
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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
	public List<SaleTransaction> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<SaleTransaction> transactions) {
		this.transactions = transactions;
	}
	
}
