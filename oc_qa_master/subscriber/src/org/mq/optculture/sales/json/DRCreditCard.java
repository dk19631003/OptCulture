package org.mq.optculture.sales.json;

import javax.xml.bind.annotation.XmlElement;

public class DRCreditCard {

	private String Amount;
	private String Number;
	private String ExpMonth;
	private String ExpYear;
	private String Type;
	private String Authorization;
	private String ReferenceNumber;
	private String ControlNum;
	private String CardPresent;
	private String AVSCode;
	private String Taken;
	private String Given;
	private String CurrencyName;
	
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
	public String getType() {
		return Type;
	}
	@XmlElement(name = "Type")
	public void setType(String type) {
		Type = type;
	}
	public String getAuthorization() {
		return Authorization;
	}
	@XmlElement(name = "Authorization")
	public void setAuthorization(String authorization) {
		Authorization = authorization;
	}
	public String getReferenceNumber() {
		return ReferenceNumber;
	}
	@XmlElement(name = "ReferenceNumber")
	public void setReferenceNumber(String referenceNumber) {
		ReferenceNumber = referenceNumber;
	}
	public String getControlNum() {
		return ControlNum;
	}
	@XmlElement(name = "ControlNum")
	public void setControlNum(String controlNum) {
		ControlNum = controlNum;
	}
	public String getCardPresent() {
		return CardPresent;
	}
	@XmlElement(name = "CardPresent")
	public void setCardPresent(String cardPresent) {
		CardPresent = cardPresent;
	}
	public String getAVSCode() {
		return AVSCode;
	}
	@XmlElement(name = "AVSCode")
	public void setAVSCode(String aVSCode) {
		AVSCode = aVSCode;
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
