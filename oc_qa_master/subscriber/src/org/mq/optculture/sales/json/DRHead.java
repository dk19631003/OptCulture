package org.mq.optculture.sales.json;

public class DRHead {

	private User user;
	private String enrollCustomer;
	private String emailReceipt;
	private String printReceipt;
	private String requestSource;
	private String requestFormat;
	private String requestEndPoint;
	private String isLoyaltyCustomer;
	private String requestTimestamp;

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

	public String getRequestEndPoint() {
		return requestEndPoint;
	}

	public void setRequestEndPoint(String requestEndPoint) {
		this.requestEndPoint = requestEndPoint;
	}

	public String getRequestTimestamp() {
		return requestTimestamp;
	}

	public void setRequestTimestamp(String requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}

	public String getIsLoyaltyCustomer() {
		return isLoyaltyCustomer;
	}

	public void setIsLoyaltyCustomer(String isLoyaltyCustomer) {
		this.isLoyaltyCustomer = isLoyaltyCustomer;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
