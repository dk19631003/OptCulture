package org.mq.optculture.model.digitalReceipt;

import javax.xml.bind.annotation.XmlElement;

public class DRGift {
	private String Amount;
	private String GiftNum;
	private String GiftPurchase;
	private String GiftPayment;
	
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

}
