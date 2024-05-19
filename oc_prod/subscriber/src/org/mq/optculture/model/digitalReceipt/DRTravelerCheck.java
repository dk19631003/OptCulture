package org.mq.optculture.model.digitalReceipt;

import javax.xml.bind.annotation.XmlElement;

public class DRTravelerCheck {
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


}
