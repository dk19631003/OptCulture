package org.mq.optculture.model.importcontact;

import javax.xml.bind.annotation.XmlElement;

public class Customer {
	private String customerId;
	private String instanceId;
	private String phone;
	private String membershipNumber;
	private String emailAddress;
	private String firstName;
	private String lastName;
	private String city;
	private String state;
	private String postal;
	private String country;
	private String homeStore;
	private String EnrolledStore;
	private String SubsidiaryNumber;
	private String gender;
	private String creationDate;
	private String birthday;
	private String anniversary;
	private String anniversaryDateFormat;
	private String birthdayDateFormat;
	private String addressLine1;
	private String addressLine2;
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
	private Suppress suppress;// changes 2.5.3.0

	public Customer() {
	}

	public Customer(String customerId, String phone, String emailAddress, String firstName, String lastName,
			String city, String state, String postal, String country, String homeStore, String gender,
			String creationDate, String birthday, String anniversary, String addressLine1, String addressLine2,
			String uDF1, String uDF2, String uDF3, String uDF4, String uDF5, String uDF6, String uDF7, String uDF8,
			String uDF9, String uDF10, String uDF11, String uDF12, String uDF13, String uDF14, String uDF15) {
		super();
		this.customerId = customerId;
		this.phone = phone;
		this.emailAddress = emailAddress;
		this.firstName = firstName;
		this.lastName = lastName;
		this.city = city;
		this.state = state;
		this.postal = postal;
		this.country = country;
		this.homeStore = homeStore;
		this.gender = gender;
		this.creationDate = creationDate;
		this.birthday = birthday;
		this.anniversary = anniversary;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		UDF1 = uDF1;
		UDF2 = uDF2;
		UDF3 = uDF3;
		UDF4 = uDF4;
		UDF5 = uDF5;
		UDF6 = uDF6;
		UDF7 = uDF7;
		UDF8 = uDF8;
		UDF9 = uDF9;
		UDF10 = uDF10;
		UDF11 = uDF11;
		UDF12 = uDF12;
		UDF13 = uDF13;
		UDF14 = uDF14;
		UDF15 = uDF15;
	}

	public String getCustomerId() {
		return customerId;
	}

	@XmlElement(name = "customerId")
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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

	public String getHomeStore() {
		return homeStore;
	}

	@XmlElement(name = "homeStore")
	public void setHomeStore(String homeStore) {
		this.homeStore = homeStore;
	}

	public String getGender() {
		return gender;
	}

	@XmlElement(name = "gender")
	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCreationDate() {
		return creationDate;
	}

	@XmlElement(name = "creationDate")
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
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

	public String getUDF1() {
		return UDF1;
	}

	@XmlElement(name = "UDF1")
	public void setUDF1(String uDF1) {
		UDF1 = uDF1;
	}

	public String getUDF2() {
		return UDF2;
	}

	@XmlElement(name = "UDF2")
	public void setUDF2(String uDF2) {
		UDF2 = uDF2;
	}

	public String getUDF3() {
		return UDF3;
	}

	@XmlElement(name = "UDF3")
	public void setUDF3(String uDF3) {
		UDF3 = uDF3;
	}

	public String getUDF4() {
		return UDF4;
	}

	@XmlElement(name = "UDF4")
	public void setUDF4(String uDF4) {
		UDF4 = uDF4;
	}

	public String getUDF5() {
		return UDF5;
	}

	@XmlElement(name = "UDF5")
	public void setUDF5(String uDF5) {
		UDF5 = uDF5;
	}

	public String getUDF6() {
		return UDF6;
	}

	@XmlElement(name = "UDF6")
	public void setUDF6(String uDF6) {
		UDF6 = uDF6;
	}

	public String getUDF7() {
		return UDF7;
	}

	@XmlElement(name = "UDF7")
	public void setUDF7(String uDF7) {
		UDF7 = uDF7;
	}

	public String getUDF8() {
		return UDF8;
	}

	@XmlElement(name = "UDF8")
	public void setUDF8(String uDF8) {
		UDF8 = uDF8;
	}

	public String getUDF9() {
		return UDF9;
	}

	@XmlElement(name = "UDF9")
	public void setUDF9(String uDF9) {
		UDF9 = uDF9;
	}

	public String getUDF10() {
		return UDF10;
	}

	@XmlElement(name = "UDF10")
	public void setUDF10(String uDF10) {
		UDF10 = uDF10;
	}

	public String getUDF11() {
		return UDF11;
	}

	@XmlElement(name = "UDF11")
	public void setUDF11(String uDF11) {
		UDF11 = uDF11;
	}

	public String getUDF12() {
		return UDF12;
	}

	@XmlElement(name = "UDF12")
	public void setUDF12(String uDF12) {
		UDF12 = uDF12;
	}

	public String getUDF13() {
		return UDF13;
	}

	@XmlElement(name = "UDF13")
	public void setUDF13(String uDF13) {
		UDF13 = uDF13;
	}

	public String getUDF14() {
		return UDF14;
	}

	@XmlElement(name = "UDF14")
	public void setUDF14(String uDF14) {
		UDF14 = uDF14;
	}

	public String getUDF15() {
		return UDF15;
	}

	@XmlElement(name = "UDF15")
	public void setUDF15(String uDF15) {
		UDF15 = uDF15;
	}

	// changes 2.5.3.0 start
	public Suppress getSuppress() {
		return suppress;
	}

	public void setSuppress(Suppress suppress) {
		this.suppress = suppress;
	}
	// changes 2.5.3.0 end

	public String getMembershipNumber() {
		return membershipNumber;
	}

	public void setMembershipNumber(String membershipNumber) {
		this.membershipNumber = membershipNumber;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	
	public String getBirthdayDateFormat() {
		return birthdayDateFormat;
	}

	public void setBirthdayDateFormat(String birthdayDateFormat) {
		this.birthdayDateFormat = birthdayDateFormat;
	}

	public String getAnniversaryDateFormat() {
		return anniversaryDateFormat;
	}

	public void setAnniversaryDateFormat(String anniversaryDateFormat) {
		this.anniversaryDateFormat = anniversaryDateFormat;
	}

	public String getEnrolledStore() {
		return EnrolledStore;
	}

	public void setEnrolledStore(String enrolledStore) {
		EnrolledStore = enrolledStore;
	}

	public String getSubsidiaryNumber() {
		return SubsidiaryNumber;
	}

	public void setSubsidiaryNumber(String subsidiaryNumber) {
		SubsidiaryNumber = subsidiaryNumber;
	}

}
