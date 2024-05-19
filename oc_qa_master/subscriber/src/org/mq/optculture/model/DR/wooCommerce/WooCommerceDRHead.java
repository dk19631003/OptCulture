package org.mq.optculture.model.DR.wooCommerce;


public class WooCommerceDRHead {
	private WooCommerceDRUser user;
	private String enrollCustomer;
	private String emailReceipt;
	private String printReceipt;
	private String requestSource;
	private String requestFormat;
	private String requestEndPoint;
	private String requestId;
    private String requestDate;
    private String isLoyaltyCustomer;
    private String requestType;
    private String receiptType;
	public WooCommerceDRUser getUser() {
		return user;
	}
	public void setUser(WooCommerceDRUser user) {
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
	public String getRequestEndPoint() {
		return requestEndPoint;
	}
	public void setRequestEndPoint(String requestEndPoint) {
		this.requestEndPoint = requestEndPoint;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}
	public String getIsLoyaltyCustomer() {
		return isLoyaltyCustomer;
	}
	public void setIsLoyaltyCustomer(String isLoyaltyCustomer) {
		this.isLoyaltyCustomer = isLoyaltyCustomer;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getReceiptType() {
		return receiptType;
	}
	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}
}
