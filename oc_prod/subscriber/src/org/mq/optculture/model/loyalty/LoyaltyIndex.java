package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class LoyaltyIndex {

	private String minCardNumber;
	private String maxCardNumber;
	private String fileName;
	
	public LoyaltyIndex() {
		// TODO Auto-generated constructor stub
	}

	public String getMinCardNumber() {
		return minCardNumber;
	}
	@XmlAttribute(name="minCardNumber")
	public void setMinCardNumber(String minCardNumber) {
		this.minCardNumber = minCardNumber;
	}

	public String getMaxCardNumber() {
		return maxCardNumber;
	}
	@XmlAttribute(name="maxCardNumber")
	public void setMaxCardNumber(String maxCardNumber) {
		this.maxCardNumber = maxCardNumber;
	}

	public String getFileName() {
		return fileName;
	}
	@XmlElement(name="FILENAME")
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
