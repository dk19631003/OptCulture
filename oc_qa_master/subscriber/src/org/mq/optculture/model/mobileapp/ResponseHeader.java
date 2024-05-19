package org.mq.optculture.model.mobileapp;

public class ResponseHeader {

	private String requestId;
	private String requestDate;
	private String transactionDate;
	private String transactionId;
	
	
	public ResponseHeader(){}
	
	public ResponseHeader( String requestId, String requestDate, String transactionDate, String transactionId){
		
		this.requestId = requestId;
		this.requestDate = requestDate;
		this.transactionId = transactionId;
		this.transactionDate = transactionDate;
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
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
}
