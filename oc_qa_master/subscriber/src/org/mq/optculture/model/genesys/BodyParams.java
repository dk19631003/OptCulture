package org.mq.optculture.model.genesys;

public class BodyParams {
	private String eventType;//pointRedemtion & CouponRedemption
	private String billGUID;
	private String ISDcode;
	private String mobile;
	private String billValue;
	private String isEmployee;
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getBillGUID() {
		return billGUID;
	}
	public void setBillGUID(String billGUID) {
		this.billGUID = billGUID;
	}
	public String getISDcode() {
		return ISDcode;
	}
	public void setISDcode(String iSDcode) {
		ISDcode = iSDcode;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getBillValue() {
		return billValue;
	}
	public void setBillValue(String billValue) {
		this.billValue = billValue;
	}
	public String getIsEmployee() {
		return isEmployee;
	}
	public void setIsEmployee(String isEmployee) {
		this.isEmployee = isEmployee;
	}
	
}
