package org.mq.optculture.sales.json;

import javax.xml.bind.annotation.XmlElement;

public class DRPayments {
	private String Amount;
	private String PaymentDate;
	private String Remark;
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
	public String getPaymentDate() {
		return PaymentDate;
	}
	@XmlElement(name = "PaymentDate")
	public void setPaymentDate(String paymentDate) {
		PaymentDate = paymentDate;
	}
	public String getRemark() {
		return Remark;
	}
	@XmlElement(name = "Remark")
	public void setRemark(String remark) {
		Remark = remark;
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
