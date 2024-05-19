package org.mq.optculture.sales.json;

import javax.xml.bind.annotation.XmlElement;

public class DRDebitCard {
	private String Amount;
	private String Number;
	private String ExpMonth;
	private String ExpYear;
	private String Authorization;
	private String ReferenceNumber;
	private String Tracenum;
	private String Fee;
	private String AmtPlusFee;
	private String CashBackAmount;
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
	public String getTracenum() {
		return Tracenum;
	}
	@XmlElement(name = "Tracenum")
	public void setTracenum(String tracenum) {
		Tracenum = tracenum;
	}
	public String getFee() {
		return Fee;
	}
	@XmlElement(name = "Fee")
	public void setFee(String fee) {
		Fee = fee;
	}
	public String getAmtPlusFee() {
		return AmtPlusFee;
	}
	@XmlElement(name = "AmtPlusFee")
	public void setAmtPlusFee(String amtPlusFee) {
		AmtPlusFee = amtPlusFee;
	}
	
	public String getCashBackAmount() {
		return CashBackAmount;
	}
	@XmlElement(name = "CashBackAmount")
	public void setCashBackAmount(String cashBackAmount) {
		CashBackAmount = cashBackAmount;
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
