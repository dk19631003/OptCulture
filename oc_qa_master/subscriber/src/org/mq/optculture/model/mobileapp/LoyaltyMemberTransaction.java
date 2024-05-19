package org.mq.optculture.model.mobileapp;

import java.util.List;

import org.mq.optculture.model.ocloyalty.Balance;

public class LoyaltyMemberTransaction {

	private String date;
	private String storeNumber;
	private String subsidiaryNumber;
	public String getSubsidiaryNumber() {
		return subsidiaryNumber;
	}
	public void setSubsidiaryNumber(String subsidiaryNumber) {
		this.subsidiaryNumber = subsidiaryNumber;
	}
	public String getDocSID() {
		return docSID;
	}
	public void setDocSID(String docSID) {
		this.docSID = docSID;
	}
	private String docSID;
	private String receiptNumber;
	private String eReceiptURL;
    private Amount amount;
	private List<Balance> balances;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStoreNumber() {
		return storeNumber;
	}
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}
	public String getReceiptNumber() {
		return receiptNumber;
	}
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	public Amount getAmount() {
		return amount;
	}
	public void setAmount(Amount amount) {
		this.amount = amount;
	}
	public List<Balance> getBalances() {
		return balances;
	}
	public void setBalances(List<Balance> balances) {
		this.balances = balances;
	}
	public String geteReceiptURL() {
		return eReceiptURL;
	}
	public void seteReceiptURL(String eReceiptURL) {
		this.eReceiptURL = eReceiptURL;
	}
	
}