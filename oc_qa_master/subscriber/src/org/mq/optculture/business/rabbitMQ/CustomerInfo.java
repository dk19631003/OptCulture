package org.mq.optculture.business.rabbitMQ;

public class CustomerInfo {
	
	private String customerId;
	private String firstName;
	private String lastName;
	private String mobileNumber;
	private String emailAddress;
	private String birthday;
	private String anniversary;
	private String gender;
	private String createdDate;
	private String optinMedium;
	private String homeStore;
	
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
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getAnniversary() {
		return anniversary;
	}
	public void setAnniversary(String anniversary) {
		this.anniversary = anniversary;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getOptinMedium() {
		return optinMedium;
	}
	public void setOptinMedium(String optinMedium) {
		this.optinMedium = optinMedium;
	}
	public String getHomeStore() {
		return homeStore;
	}
	public void setHomeStore(String homeStore) {
		this.homeStore = homeStore;
	}

}
