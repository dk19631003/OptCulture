package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class LtyBalanceType {

	private String valueCode;
	private String value;
	
	public LtyBalanceType() {
		// TODO Auto-generated constructor stub
	}

	public String getValueCode() {
		return valueCode;
	}
	@XmlAttribute(name="valueCode")
	public void setValueCode(String valueCode) {
		this.valueCode = valueCode;
	}

	public String getValue() {
		return value;
	}
	@XmlElement(name="VALUE")
	public void setValue(String value) {
		this.value = value;
	}
	
}
