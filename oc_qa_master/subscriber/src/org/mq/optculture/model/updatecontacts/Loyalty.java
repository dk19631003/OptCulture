package org.mq.optculture.model.updatecontacts;

public class Loyalty {
	private MobileAppPreferences mobileAppPreferences;
	private String password;
	private String fingerprintValidation;
	private String enrollCustomer;
	private String SuspendMembership;

	public String getSuspendMembership() {
		return SuspendMembership;
	}

	public void setSuspendMembership(String suspendMemberShip) {
		SuspendMembership = suspendMemberShip;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public MobileAppPreferences getMobileAppPreferences() {
		return mobileAppPreferences;
	}

	public void setMobileAppPreferences(MobileAppPreferences mobileAppPreferences) {
		this.mobileAppPreferences = mobileAppPreferences;
	}

	public String getFingerprintValidation() {
		return fingerprintValidation;
	}

	public void setFingerprintValidation(String fingerprintValidation) {
		this.fingerprintValidation = fingerprintValidation;
	}

	public String getEnrollCustomer() {
		return enrollCustomer;
	}

	public void setEnrollCustomer(String enrollCustomer) {
		this.enrollCustomer = enrollCustomer;
	}
	
	
}
