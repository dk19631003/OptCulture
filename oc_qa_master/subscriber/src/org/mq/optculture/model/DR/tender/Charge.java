package org.mq.optculture.model.DR.tender;

import javax.xml.bind.annotation.XmlElement;

public class Charge {


	private String Amount;
	private String NetDays;
	private String DiscDays;
	private String DiscPercent;
	private String Taken;
	private String Given;
	private String CurrencyName;
	
	
	public Charge(){}
	public Charge(String amount, String netDays, String discDays, String discPercent, String taken, String given,
			String currencyName) {
		super();
		Amount = amount;
		NetDays = netDays;
		DiscDays = discDays;
		DiscPercent = discPercent;
		Taken = taken;
		Given = given;
		CurrencyName = currencyName;
	}
	public String getAmount() {
		return Amount;
	}
	@XmlElement(name = "Amount")
	public void setAmount(String amount) {
		Amount = amount;
	}
	public String getNetDays() {
		return NetDays;
	}
	@XmlElement(name = "NetDays")
	public void setNetDays(String netDays) {
		NetDays = netDays;
	}
	public String getDiscDays() {
		return DiscDays;
	}
	@XmlElement(name = "DiscDays")
	public void setDiscDays(String discDays) {
		DiscDays = discDays;
	}
	public String getDiscPercent() {
		return DiscPercent;
	}
	@XmlElement(name = "DiscPercent")
	public void setDiscPercent(String discPercent) {
		DiscPercent = discPercent;
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


	
	
}
