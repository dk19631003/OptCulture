package org.mq.optculture.model.digitalReceipt;

import javax.xml.bind.annotation.XmlElement;

public class DRDeposit {
	private String Amount;

	public String getAmount() {
		return Amount;
	}
	@XmlElement(name = "Amount")
	public void setAmount(String amount) {
		Amount = amount;
	}

}
