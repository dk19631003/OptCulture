package org.mq.optculture.model.DR.tender;

import javax.xml.bind.annotation.XmlElement;

public class StoreCredit {


	private String Amount;
	private String NewStoreCredit;
	private String Taken;
	private String Given;
	private String CurrencyName;
	
	public StoreCredit(){}
	public StoreCredit(String amount, String newStoreCredit, String taken, String given, String currencyName) {
		Amount = amount;
		NewStoreCredit = newStoreCredit;
		Taken = taken;
		Given = given;
		CurrencyName = currencyName;
	}
	public String getAmount() {
		return Amount;
	}
	@XmlElement(name = "Amount")
	public void setAmount(String amount) {
		Amount = amount;
	}
	public String getNewStoreCredit() {
		return NewStoreCredit;
	}
	@XmlElement(name = "NewStoreCredit")
	public void setNewStoreCredit(String newStoreCredit) {
		NewStoreCredit = newStoreCredit;
	}
	public String getTaken() {
		return Taken;
	}
	@XmlElement(name = "Taken")
	public void setTaken(String taken) {
		Taken = taken;
	}
	public String getGiven() {
		return Given;
	}
	@XmlElement(name = "Given")
	public void setGiven(String given) {
		Given = given;
	}
	public String getCurrencyName() {
		return CurrencyName;
	}
	@XmlElement(name = "CurrencyName")
	public void setCurrencyName(String currencyName) {
		CurrencyName = currencyName;
	}

}
