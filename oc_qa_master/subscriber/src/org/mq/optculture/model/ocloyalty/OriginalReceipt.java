package org.mq.optculture.model.ocloyalty;

import javax.xml.bind.annotation.XmlElement;

public class OriginalReceipt {

	private String docSID;
	private String receiptNumber;
	private String receiptAmount;
	private String subsidiaryNumber;//APP-2084
	private String storeNumber;
	
	public OriginalReceipt() {
	}
	
	public String getDocSID() {
		return docSID;
	}
	@XmlElement(name = "docSID")
	public void setDocSID(String docSID) {
		this.docSID = docSID;
	}
	public String getReceiptNumber() {
		return receiptNumber;
	}
	@XmlElement(name = "receiptNumber")
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	public String getReceiptAmount() {
		return receiptAmount;
	}
	@XmlElement(name = "receiptAmount")
	public void setReceiptAmount(String receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getSubsidiaryNumber() {
		return subsidiaryNumber;
	}

	public void setSubsidiaryNumber(String subsidiaryNumber) {
		this.subsidiaryNumber = subsidiaryNumber;
	}

	public String getStoreNumber() {
		return storeNumber;
	}

	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}
	
}
