package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class LoyaltyDumpDataRecord {

	private String cardNumber;
	private String pin;
	private LoyaltyDumpBalances balanceInfo;
	private LoyaltyDumpContactInfo contactInfo;
	
	public LoyaltyDumpDataRecord() {
		// TODO Auto-generated constructor stub
	}

	public String getCardNumber() {
		return cardNumber;
	}
	@XmlAttribute(name="cardNumber")
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getPin() {
		return pin;
	}
	@XmlAttribute(name="pin")
	public void setPin(String pin) {
		this.pin = pin;
	}

	public LoyaltyDumpBalances getBalanceInfo() {
		return balanceInfo;
	}
	@XmlElement(name="BALANCEINFO")
	public void setBalanceInfo(LoyaltyDumpBalances balanceInfo) {
		this.balanceInfo = balanceInfo;
	}

	public LoyaltyDumpContactInfo getContactInfo() {
		return contactInfo;
	}
	@XmlElement(name="CONTACTINFO")
	public void setContactInfo(LoyaltyDumpContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}
	
}
