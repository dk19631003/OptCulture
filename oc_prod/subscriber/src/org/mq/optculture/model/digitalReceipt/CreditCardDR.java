package org.mq.optculture.model.digitalReceipt;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class CreditCardDR {

	private List<DRCreditCard> creditCards;
	
	public List<DRCreditCard> getCreditCards() {
		return creditCards;
	}
	@XmlElement(name = "CreditCards")
	public void setCreditCards(List<DRCreditCard> creditCards) {
		this.creditCards = creditCards;
	}
	
	
	
}
