package org.mq.optculture.model.DR.tender;

import javax.xml.bind.annotation.XmlElement;

public class Check {

	private String Amount;
	private String Number;
	private String FirstName;
	private String LastName;
	private String WorkPhone;
	private String HomePhone;
	private String StateCode;
	private String Company;
	private String DriverLicense;
	private String DriverLicenseExp;
	private String DateOfBirth;
	private String Taken;
	private String Given;
	private String CurrencyName;
	
	public Check(){}
	
	public Check(String amount, String number, String firstName, String lastName, String workPhone, String homePhone,
			String stateCode, String company, String driverLicense, String driverLicenseExp, String dateOfBirth,
			String taken, String given, String currencyName) {
		Amount = amount;
		Number = number;
		FirstName = firstName;
		LastName = lastName;
		WorkPhone = workPhone;
		HomePhone = homePhone;
		StateCode = stateCode;
		Company = company;
		DriverLicense = driverLicense;
		DriverLicenseExp = driverLicenseExp;
		DateOfBirth = dateOfBirth;
		Taken = taken;
		Given = given;
		CurrencyName = currencyName;
	}
	public String getAmount() {
		return Amount;
	}
	@XmlElement(name = "Amount")
	public void setAmount(String amount) {
		Amount = amount;
	}
	public String getNumber() {
		return Number;
	}
	@XmlElement(name = "Number")
	public void setNumber(String number) {
		Number = number;
	}
	public String getFirstName() {
		return FirstName;
	}
	@XmlElement(name = "FirstName")
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	public String getLastName() {
		return LastName;
	}
	@XmlElement(name = "LastName")
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	public String getWorkPhone() {
		return WorkPhone;
	}
	@XmlElement(name = "WorkPhone")
	public void setWorkPhone(String workPhone) {
		WorkPhone = workPhone;
	}
	public String getHomePhone() {
		return HomePhone;
	}
	@XmlElement(name = "HomePhone")
	public void setHomePhone(String homePhone) {
		HomePhone = homePhone;
	}
	public String getStateCode() {
		return StateCode;
	}
	@XmlElement(name = "StateCode")
	public void setStateCode(String stateCode) {
		StateCode = stateCode;
	}
	public String getCompany() {
		return Company;
	}
	@XmlElement(name = "Company")
	public void setCompany(String company) {
		Company = company;
	}
	public String getDriverLicense() {
		return DriverLicense;
	}
	@XmlElement(name = "DriverLicense")
	public void setDriverLicense(String driverLicense) {
		DriverLicense = driverLicense;
	}
	public String getDriverLicenseExp() {
		return DriverLicenseExp;
	}
	@XmlElement(name = "DriverLicenseExp")
	public void setDriverLicenseExp(String driverLicenseExp) {
		DriverLicenseExp = driverLicenseExp;
	}
	public String getDateOfBirth() {
		return DateOfBirth;
	}
	@XmlElement(name = "DateOfBirth")
	public void setDateOfBirth(String dateOfBirth) {
		DateOfBirth = dateOfBirth;
	}
	public String getTaken() {
		return Taken;
	}
	@XmlElement(name = "Taken")
	public void setTaken(String taken) {
		Taken = taken;
	}
	public String getGiven() {
		return Given;
	}
	@XmlElement(name = "Given")
	public void setGiven(String given) {
		Given = given;
	}
	public String getCurrencyName() {
		return CurrencyName;
	}
	@XmlElement(name = "CurrencyName")
	public void setCurrencyName(String currencyName) {
		CurrencyName = currencyName;
	}

}
