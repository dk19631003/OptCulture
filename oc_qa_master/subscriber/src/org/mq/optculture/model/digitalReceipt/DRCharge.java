package org.mq.optculture.model.digitalReceipt;

import javax.xml.bind.annotation.XmlElement;

public class DRCharge {
	private String Amount;
	private String NetDays;
	private String DiscDays;
	private String DiscPercent;
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

}
