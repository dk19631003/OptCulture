package org.mq.optculture.model.couponcodes;

import javax.xml.bind.annotation.XmlElement;

public class PromoCustomerInfo {
	
	public PromoCustomerInfo(){}
	
	private String redeemCount;
	private String customerId;
	private String emailId;
	private String mobilePhone;
	private String status;
	
	@XmlElement(name="RETAIL_PRO_ID")
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	@XmlElement(name="EMAIL_ID")
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	@XmlElement(name="PHONE")
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	
	@XmlElement(name="STATUS")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	@XmlElement(name="REDEEM_COUNT")
	public String getRedeemCount() {
		return redeemCount;
	}
	public void setRedeemCount(String redeemCount) {
		this.redeemCount = redeemCount;
	}
	
	

}
