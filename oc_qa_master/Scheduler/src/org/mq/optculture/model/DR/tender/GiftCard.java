package org.mq.optculture.model.DR.tender;

import javax.xml.bind.annotation.XmlElement;

public class GiftCard {

	private String Amount;
	private String Number;
	private String ExpMonth;
	private String ExpYear;
	private String Authorization;
	private String Balance;
	private String Taken;
	private String Given;
	private String CurrencyName;
	
	public GiftCard(){}
	
	public GiftCard(String amount, String number, String expMonth, String expYear, String authorization, String balance,
			String taken, String given, String currencyName) {
		Amount = amount;
		Number = number;
		ExpMonth = expMonth;
		ExpYear = expYear;
		Authorization = authorization;
		Balance = balance;
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
	public String getNumber() {
		return Number;
	}
	@XmlElement(name = "Number")
	public void setNumber(String number) {
		Number = number;
	}
	public String getExpMonth() {
		return ExpMonth;
	}
	@XmlElement(name = "ExpMonth")
	public void setExpMonth(String expMonth) {
		ExpMonth = expMonth;
	}
	public String getExpYear() {
		return ExpYear;
	}
	@XmlElement(name = "ExpYear")
	public void setExpYear(String expYear) {
		ExpYear = expYear;
	}
	public String getAuthorization() {
		return Authorization;
	}
	@XmlElement(name = "Authorization")
	public void setAuthorization(String authorization) {
		Authorization = authorization;
	}
	public String getBalance() {
		return Balance;
	}
	@XmlElement(name = "Balance")
	public void setBalance(String balance) {
		Balance = balance;
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
