package org.mq.optculture.model.digitalReceipt;

import javax.xml.bind.annotation.XmlElement;

public class DRCOD {
	private String Amount;
	private String Given;
	private String Taken;
	
	public String getAmount() {
		return Amount;
	}
	@XmlElement(name = "Amount")
	public void setAmount(String amount) {
		Amount = amount;
	}
	public String getGiven() {
		return Given;
	}
	public void setGiven(String given) {
		Given = given;
	}
	public String getTaken() {
		return Taken;
	}
	public void setTaken(String taken) {
		Taken = taken;
	}

}
