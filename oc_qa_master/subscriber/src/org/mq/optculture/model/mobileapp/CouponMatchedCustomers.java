package org.mq.optculture.model.mobileapp;

import java.util.List;

public class CouponMatchedCustomers {

	private String customerId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String phone;
	private String membershipNumber;
	private List<IssuedCoupons> couponsNew;
	private List<IssuedCoupons> couponsDeactivated;
	private List<IssuedCoupons> couponsModified;
	
	/*public List<IssuedCoupons> getCoupons() {
		return coupons;
	}
	public void setCoupons(List<IssuedCoupons> coupons) {
		this.coupons = coupons;
	}*/
	
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
	public List<IssuedCoupons> getCouponsNew() {
		return couponsNew;
	}
	public void setCouponsNew(List<IssuedCoupons> couponsNew) {
		this.couponsNew = couponsNew;
	}
	public List<IssuedCoupons> getCouponsDeactivated() {
		return couponsDeactivated;
	}
	public void setCouponsDeactivated(List<IssuedCoupons> couponsDeactivated) {
		this.couponsDeactivated = couponsDeactivated;
	}
	public List<IssuedCoupons> getCouponsModified() {
		return couponsModified;
	}
	public void setCouponsModified(List<IssuedCoupons> couponsModified) {
		this.couponsModified = couponsModified;
	}
	
	

}
