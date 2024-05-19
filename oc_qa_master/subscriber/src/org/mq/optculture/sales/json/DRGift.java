package org.mq.optculture.sales.json;

import javax.xml.bind.annotation.XmlElement;

public class DRGift {
	private String Amount;
	private String GiftNum;
	private String Number;
	private String GiftCardNo;
	private String GiftPurchase;
	private String GiftPayment;
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
	public String getGiftNum() {
		return GiftNum;
	}
	@XmlElement(name = "GiftNum")
	public void setGiftNum(String giftNum) {
		GiftNum = giftNum;
	}
	public String getGiftPurchase() {
		return GiftPurchase;
	}
	@XmlElement(name = "GiftPurchase")
	public void setGiftPurchase(String giftPurchase) {
		GiftPurchase = giftPurchase;
	}
	public String getGiftPayment() {
		return GiftPayment;
	}
	@XmlElement(name = "GiftPayment")
	public void setGiftPayment(String giftPayment) {
		GiftPayment = giftPayment;
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
	public String getNumber() {
		return Number;
	}
	public void setNumber(String number) {
		Number = number;
	}
	public String getGiftCardNo() {
		return GiftCardNo;
	}
	public void setGiftCardNo(String giftCardNo) {
		GiftCardNo = giftCardNo;
	}
}
