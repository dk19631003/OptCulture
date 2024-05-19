package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class LoyaltyCustomer {
	
	public LoyaltyCustomer(){}
	
	private String customerId;
	private String cardNumber;
	private String pin;
	
	@XmlAttribute(name="customerId")
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	@XmlElement(name="CARDNUMBER")
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	@XmlElement(name="PIN")
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	
}