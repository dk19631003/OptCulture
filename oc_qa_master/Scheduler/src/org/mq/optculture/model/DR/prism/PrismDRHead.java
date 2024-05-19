package org.mq.optculture.model.DR.prism;
public class PrismDRHead {

	private PrismDRUser user;
	private String enrollCustomer;
	private String isLoyaltyCustomer;
	private String emailReceipt;
	private String printReceipt;
	private String requestSource;
	private String requestFormat;
	private String requestEndPoint;

	public String getRequestEndPoint() {
		return requestEndPoint;
	}
	public void setRequestEndPoint(String requestEndPoint) {
		this.requestEndPoint = requestEndPoint;
	}
	public PrismDRUser getUser() {
		return user;
	}

	public void setUser(PrismDRUser user) {
		this.user = user;
	}
	
	public String getEnrollCustomer() {
		return enrollCustomer;
	}
	public void setEnrollCustomer(String enrollCustomer) {
		this.enrollCustomer = enrollCustomer;
	}
	public String getEmailReceipt() {
		return emailReceipt;
	}
	public void setEmailReceipt(String emailReceipt) {
		this.emailReceipt = emailReceipt;
	}
	public String getPrintReceipt() {
		return printReceipt;
	}
	public void setPrintReceipt(String printReceipt) {
		this.printReceipt = printReceipt;
	}
	public String getRequestSource() {
		return requestSource;
	}
	public void setRequestSource(String requestSource) {
		this.requestSource = requestSource;
	}
	public String getRequestFormat() {
		return requestFormat;
	}
	public void setRequestFormat(String requestFormat) {
		this.requestFormat = requestFormat;
	}
	public String getIsLoyaltyCustomer() {
		return isLoyaltyCustomer;
	}
	public void setIsLoyaltyCustomer(String isLoyaltyCustomer) {
		this.isLoyaltyCustomer = isLoyaltyCustomer;
	}
}
