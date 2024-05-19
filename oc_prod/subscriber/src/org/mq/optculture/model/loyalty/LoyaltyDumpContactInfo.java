package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlElement;

public class LoyaltyDumpContactInfo {

	private String customerId;
	private String emailId;
	private String phone;
	
	public LoyaltyDumpContactInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getCustomerId() {
		return customerId;
	}
	@XmlElement(name="CUSTOMERID")
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getEmailId() {
		return emailId;
	}
	@XmlElement(name="EMAILID")
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPhone() {
		return phone;
	}
	@XmlElement(name="PHONE")
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
