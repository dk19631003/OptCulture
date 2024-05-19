package org.mq.optculture.model.ocloyalty;

import javax.xml.bind.annotation.XmlElement;

public class TransferSource {

	private String cardNumber;
	private String cardPin;
	
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardPin() {
		return cardPin;
	}
	public void setCardPin(String cardPin) {
		this.cardPin = cardPin;
	}
	
}
