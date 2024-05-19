package org.mq.optculture.model.ocloyalty;

import org.mq.optculture.model.updatecontacts.MobileAppPreferences;

public class MatchedCustomer {

	private String membershipNumber;
	private MobileAppPreferences mobileAppPreferences;
	private String customerId;
	private String instanceId;
	private String phone;
	private String emailAddress;
	private String firstName;
	private String lastName;
	
	public MatchedCustomer() {
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

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public MobileAppPreferences getMobileAppPreferences() {
		return mobileAppPreferences;
	}

	public void setMobileAppPreferences(MobileAppPreferences mobileAppPreferences) {
		this.mobileAppPreferences = mobileAppPreferences;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	
}
