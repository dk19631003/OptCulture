package org.mq.optculture.model.ereceipt;

public class EReceipt {
	
	private String sentDate;
	//private String storeNumber;
	private String docSID;
	private String emailAddress;
	private String status;
	private EReceiptMetrics metrics;
	private Store store;
	
	public String getSentDate() {
		return sentDate;
	}
	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}
	/*public String getStoreNumber() {
		return storeNumber;
	}
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}*/
	public String getDocSID() {
		return docSID;
	}
	public void setDocSID(String docSID) {
		this.docSID = docSID;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public EReceiptMetrics getMetrics() {
		return metrics;
	}
	public void setMetrics(EReceiptMetrics metrics) {
		this.metrics = metrics;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	
	
}