package org.mq.optculture.business.rabbitMQ;

public class SaleInfo {
	
	private String docSid;
	private String receiptNumber;
	private String storeNumber;
	
	public String getDocSid() {
		return docSid;
	}
	public void setDocSid(String docSid) {
		this.docSid = docSid;
	}
	public String getReceiptNumber() {
		return receiptNumber;
	}
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	public String getStoreNumber() {
		return storeNumber;
	}
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}

}
