package org.mq.optculture.model.digitalReceipt;

import javax.xml.bind.annotation.XmlElement;

public class DRGiftCard {
	private String Amount;
	private String Number;
	private String ExpMonth;
	private String ExpYear;
	private String Authorization;
	private String Balance;
	
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

}
