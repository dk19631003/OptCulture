package org.mq.optculture.model.digitalReceipt;

import javax.xml.bind.annotation.XmlElement;

public class DRStoreCredit {

	private String Amount;
	private String NewStoreCredit;
	
	public String getAmount() {
		return Amount;
	}
	@XmlElement(name = "Amount")
	public void setAmount(String amount) {
		Amount = amount;
	}
	public String getNewStoreCredit() {
		return NewStoreCredit;
	}
	@XmlElement(name = "NewStoreCredit")
	public void setNewStoreCredit(String newStoreCredit) {
		NewStoreCredit = newStoreCredit;
	}
}
