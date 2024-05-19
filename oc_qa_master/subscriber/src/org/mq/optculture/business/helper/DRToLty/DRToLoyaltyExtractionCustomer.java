package org.mq.optculture.business.helper.DRToLty;

import org.mq.marketer.campaign.beans.ContactsLoyalty;

public class DRToLoyaltyExtractionCustomer {

	private String customerId;
	private String instanceId;
	private String firstName;
	private String lastName;
	private String phone;
	private String emailAddress;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String postal;
	private String country;
	private String birthday;
	private String anniversary;
	private String gender;
	private String createdDate;
	//  is created for setting created_date while implementing bulk enrollment utility
	private String cardNumber;
	private String cardPin;
	private ContactsLoyalty conLoyalty;

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	
	public DRToLoyaltyExtractionCustomer(String customerId, String instanceId, String firstName, String lastName,
			String phone, String emailAddress, String addressLine1, String addressLine2, String city, String state,
			String postal, String country, String birthday, String anniversary, String gender, String createdDate,
			String cardNumber, String cardPin, ContactsLoyalty conLoyalty) {
		super();
		this.customerId = customerId;
		this.instanceId = instanceId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.emailAddress = emailAddress;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.state = state;
		this.postal = postal;
		this.country = country;
		this.birthday = birthday;
		this.anniversary = anniversary;
		this.gender = gender;
		this.createdDate = createdDate;
		this.cardNumber = cardNumber;
		this.cardPin = cardPin;
		this.conLoyalty = conLoyalty;
	}

	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
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
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPostal() {
		return postal;
	}
	public void setPostal(String postal) {
		this.postal = postal;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
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
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCardPin() {
		return cardPin;
	}

	public void setCardPin(String cardPin) {
		this.cardPin = cardPin;
	}

	public ContactsLoyalty getConLoyalty() {
		return conLoyalty;
	}

	public void setConLoyalty(ContactsLoyalty conLoyalty) {
		this.conLoyalty = conLoyalty;
	}
	
}
