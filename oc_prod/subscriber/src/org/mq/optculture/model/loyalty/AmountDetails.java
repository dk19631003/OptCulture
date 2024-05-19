package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlElement;

public class AmountDetails {
	private String ENTEREDAMOUNT;
	private String VALUECODE;
	public String getENTEREDAMOUNT() {
		return ENTEREDAMOUNT;
	}
	@XmlElement(name = "ENTEREDAMOUNT")
	public void setENTEREDAMOUNT(String eNTEREDAMOUNT) {
		ENTEREDAMOUNT = eNTEREDAMOUNT;
	}
	public String getVALUECODE() {
		return VALUECODE;
	}
	@XmlElement(name = "VALUECODE")
	public void setVALUECODE(String vALUECODE) {
		VALUECODE = vALUECODE;
	}
}
