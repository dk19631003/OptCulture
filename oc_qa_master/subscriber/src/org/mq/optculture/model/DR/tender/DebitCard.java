package org.mq.optculture.model.DR.tender;

import javax.xml.bind.annotation.XmlElement;

public class DebitCard {

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
	
	
	public DebitCard(){}
	
	public DebitCard(String amount, String number, String expMonth, String expYear, String authorization,
			String referenceNumber, String tracenum, String fee, String amtPlusFee, String cashBackAmount, String taken,
			String given, String currencyName) {
		super();
		Amount = amount;
		Number = number;
		ExpMonth = expMonth;
		ExpYear = expYear;
		Authorization = authorization;
		ReferenceNumber = referenceNumber;
		Tracenum = tracenum;
		Fee = fee;
		AmtPlusFee = amtPlusFee;
		CashBackAmount = cashBackAmount;
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
