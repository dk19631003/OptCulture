package org.mq.optculture.model.loyalty;

import javax.xml.bind.annotation.XmlElement;

public class UserDetails {
	private String USERNAME;
	private String ORGANISATION;
	private String TOKEN;
	public String getUSERNAME() {
		return USERNAME;
	}
	@XmlElement(name = "USERNAME")
	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}
	public String getORGANISATION() {
		return ORGANISATION;
	}
	@XmlElement(name = "ORGANISATION")
	public void setORGANISATION(String oRGANISATION) {
		ORGANISATION = oRGANISATION;
	}
	public String getTOKEN() {
		return TOKEN;
	}
	@XmlElement(name = "TOKEN")
	public void setTOKEN(String tOKEN) {
		TOKEN = tOKEN;
	}
	
}
