package org.mq.optculture.model.DR.tender;

import javax.xml.bind.annotation.XmlElement;

public class CustomTender {
	private String Name;
	private String Type;
	private String Amount;
	private String Taken;
	private String Given;
	private String Number;//APP-4195
	public String getNumber() {
		return Number;
	}

	public void setNumber(String number) {
		Number = number;
	}

	public CustomTender(){}
	
	public CustomTender(String name, String amount) {
		super();
		Name = name;
		Amount = amount;
	}
	public String getName() {
		return Name;
	}

	@XmlElement(name = "Amount")
	public void setName(String name) {
		Name = name;
	}

	public String getAmount() {
		return Amount;
	}
	@XmlElement(name = "Name")
	public void setAmount(String amount) {
		Amount = amount;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getTaken() {
		return Taken;
	}

	public void setTaken(String taken) {
		Taken = taken;
	}

	public String getGiven() {
		return Given;
	}

	public void setGiven(String given) {
		Given = given;
	}
}
