package org.mq.optculture.model.ocloyalty;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;

public class Customer {

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
	private String subsidiaryNumber;
	private String createdDate; //  is created for setting created_date while implementing bulk enrollment utility
	private String deviceType;
	private String deviceID;
	private String password;//membership password
	private String homeStore;
	private String UDF1;
	private String UDF2;
	private String UDF3;
	private String UDF4;
	private String UDF5;
	private String UDF6;
	private String UDF7;
	private String UDF8;
	private String UDF9;
	private String UDF10;
	private String UDF11;
	private String UDF12;
	private String UDF13;
	private String UDF14;
	private String UDF15;

	
	public String getHomeStore() {
		return homeStore;
	}

	public void setHomeStore(String homeStore) {
		this.homeStore = homeStore;
	}

	public String getUDF1() {
		return UDF1;
	}

	public void setUDF1(String uDF1) {
		UDF1 = uDF1;
	}

	public String getUDF2() {
		return UDF2;
	}

	public void setUDF2(String uDF2) {
		UDF2 = uDF2;
	}

	public String getUDF3() {
		return UDF3;
	}

	public void setUDF3(String uDF3) {
		UDF3 = uDF3;
	}

	public String getUDF4() {
		return UDF4;
	}

	public void setUDF4(String uDF4) {
		UDF4 = uDF4;
	}

	public String getUDF5() {
		return UDF5;
	}

	public void setUDF5(String uDF5) {
		UDF5 = uDF5;
	}

	public String getUDF6() {
		return UDF6;
	}

	public void setUDF6(String uDF6) {
		UDF6 = uDF6;
	}

	public String getUDF7() {
		return UDF7;
	}

	public void setUDF7(String uDF7) {
		UDF7 = uDF7;
	}

	public String getUDF8() {
		return UDF8;
	}

	public void setUDF8(String uDF8) {
		UDF8 = uDF8;
	}

	public String getUDF9() {
		return UDF9;
	}

	public void setUDF9(String uDF9) {
		UDF9 = uDF9;
	}

	public String getUDF10() {
		return UDF10;
	}

	public void setUDF10(String uDF10) {
		UDF10 = uDF10;
	}

	public String getUDF11() {
		return UDF11;
	}

	public void setUDF11(String uDF11) {
		UDF11 = uDF11;
	}

	public String getUDF12() {
		return UDF12;
	}

	public void setUDF12(String uDF12) {
		UDF12 = uDF12;
	}

	public String getUDF13() {
		return UDF13;
	}

	public void setUDF13(String uDF13) {
		UDF13 = uDF13;
	}

	public String getUDF14() {
		return UDF14;
	}

	public void setUDF14(String uDF14) {
		UDF14 = uDF14;
	}

	public String getUDF15() {
		return UDF15;
	}

	public void setUDF15(String uDF15) {
		UDF15 = uDF15;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public Customer() {
	}

	public String getCustomerId() {
		return customerId;
	}
	@XmlElement(name = "customerId")
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getFirstName() {
		return firstName;
	}
	@XmlElement(name = "firstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}
	@XmlElement(name = "lastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}
	@XmlElement(name = "phone")
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmailAddress() {
		return emailAddress;
	}
	@XmlElement(name = "emailAddress")
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getAddressLine1() {
		return addressLine1;
	}
	@XmlElement(name = "addressLine1")
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}
	@XmlElement(name = "addressLine2")
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}
	@XmlElement(name = "city")
	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}
	@XmlElement(name = "state")
	public void setState(String state) {
		this.state = state;
	}

	public String getPostal() {
		return postal;
	}
	@XmlElement(name = "postal")
	public void setPostal(String postal) {
		this.postal = postal;
	}

	public String getCountry() {
		return country;
	}
	@XmlElement(name = "country")
	public void setCountry(String country) {
		this.country = country;
	}

	public String getBirthday() {
		return birthday;
	}
	@XmlElement(name = "birthday")
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAnniversary() {
		return anniversary;
	}
	@XmlElement(name = "anniversary")
	public void setAnniversary(String anniversary) {
		this.anniversary = anniversary;
	}

	public String getGender() {
		return gender;
	}
	@XmlElement(name = "gender")
	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getSubsidiaryNumber() {
		return subsidiaryNumber;
	}

	public void setSubsidiaryNumber(String subsidiaryNumber) {
		this.subsidiaryNumber = subsidiaryNumber;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
