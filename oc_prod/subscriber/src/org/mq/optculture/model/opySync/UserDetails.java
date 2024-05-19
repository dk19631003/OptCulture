package org.mq.optculture.model.opySync;

import javax.xml.bind.annotation.XmlElement;

public class UserDetails {
	
	private String USERNAME;
	private String ORGID;
	private String TOKEN;
	
	public UserDetails() {
	}
	
	public UserDetails(String uSERNAME, String oRGID, String tOKEN) {
		USERNAME = uSERNAME;
		ORGID = oRGID;
		TOKEN = tOKEN;
	}
	public String getUSERNAME() {
		return USERNAME;
	}
	@XmlElement(name = "USERNAME")
	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}
	public String getORGID() {
		return ORGID;
	}
	@XmlElement(name = "ORGID")
	public void setORGID(String oRGID) {
		ORGID = oRGID;
	}
	public String getTOKEN() {
		return TOKEN;
	}
	@XmlElement(name = "TOKEN")
	public void setTOKEN(String tOKEN) {
		TOKEN = tOKEN;
	}


}
