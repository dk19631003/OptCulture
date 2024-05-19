package org.mq.optculture.model.mobileapp;

import javax.xml.bind.annotation.XmlElement;

public class PrerequisiteInfo {
	
	
	private String firstName;
	private String lastName;
	private String mobilenumber;
	private String emailAddress;
	private String street;
	private String city;
	private String state;
	private String postalcode;
	private String country;
	private String birthday;
	private String anniversary;
	private String gender;
	
	
	
	
	
	
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
	public String getMobilenumber() {
		return mobilenumber;
	}
	@XmlElement(name = "mobilenumber")

	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	@XmlElement(name = "emailAddress")

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getStreet() {
		return street;
	}
	@XmlElement(name = "street")

	public void setStreet(String street) {
		this.street = street;
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
	public String getPostalcode() {
		return postalcode;
	}
	@XmlElement(name = "postalcode")

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
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
	

	
}

