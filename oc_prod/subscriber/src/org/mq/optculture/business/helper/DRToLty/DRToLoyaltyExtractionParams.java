package org.mq.optculture.business.helper.DRToLty;

public class DRToLoyaltyExtractionParams {
	private String enrollCustomer;
	private String isLoyaltyCustomer;
	private String requestSource;
	private String requestType;
	private String receiptType;
	public String getReceiptType() {
		return receiptType;
	}
	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public DRToLoyaltyExtractionParams(String enrollCustomer, String isLoyaltyCustomer,String requestSource, String requestType) {
		super();
		this.enrollCustomer = enrollCustomer;
		this.isLoyaltyCustomer = isLoyaltyCustomer;
		this.requestSource = requestSource;
		this.requestType = requestType;
	}
	public String getEnrollCustomer() {
		return enrollCustomer;
	}
	public void setEnrollCustomer(String enrollCustomer) {
		this.enrollCustomer = enrollCustomer;
	}
	public String getIsLoyaltyCustomer() {
		return isLoyaltyCustomer;
	}
	public void setIsLoyaltyCustomer(String isLoyaltyCustomer) {
		this.isLoyaltyCustomer = isLoyaltyCustomer;
	}
	public String getRequestSource() {
		return requestSource;
	}
	public void setRequestSource(String requestSource) {
		this.requestSource = requestSource;
	}
}
