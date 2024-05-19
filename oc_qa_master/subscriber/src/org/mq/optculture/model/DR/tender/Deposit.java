package org.mq.optculture.model.DR.tender;

import javax.xml.bind.annotation.XmlElement;

public class Deposit {

	private String Amount;
	private String Taken;
	private String Given;
	private String CurrencyName;
	
	
	
	public Deposit(){}
	
	public Deposit(String amount, String taken, String given, String currencyName) {
		Amount = amount;
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
