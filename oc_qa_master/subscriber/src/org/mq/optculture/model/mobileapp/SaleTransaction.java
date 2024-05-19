package org.mq.optculture.model.mobileapp;

import java.util.List;

public class SaleTransaction {

	private String date;
	private String storeNumber;
	private String receiptNumber;
	private String receiptAmount;
	private List<Item> items;
	private Payment payment;
	private List<LoyaltyTransaction> loyalty;
	
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
	public String getReceiptAmount() {
		return receiptAmount;
	}
	public void setReceiptAmount(String receiptAmount) {
		this.receiptAmount = receiptAmount;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	public Payment getPayment() {
		return payment;
	}
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	public List<LoyaltyTransaction> getLoyalty() {
		return loyalty;
	}
	public void setLoyalty(List<LoyaltyTransaction> loyalty) {
		this.loyalty = loyalty;
	}
}
