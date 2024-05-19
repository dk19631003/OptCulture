package org.mq.optculture.model.mobileapp;

public class Amount {

	private  String type;
	private  String enteredValue;
	private  String valueCode;
	private  String receiptAmount; 
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getEnteredValue() {
		return enteredValue;
	}
	public void setEnteredValue(String enteredValue) {
		this.enteredValue = enteredValue;
	}
	public String getValueCode() {
		return valueCode;
	}
	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}
	public String getReceiptAmount() {
		return receiptAmount;
	}
	public void setReceiptAmount(String receiptAmount) {
		this.receiptAmount = receiptAmount;
	}
}
