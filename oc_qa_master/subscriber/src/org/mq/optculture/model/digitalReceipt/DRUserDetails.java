package org.mq.optculture.model.digitalReceipt;

import javax.xml.bind.annotation.XmlElement;

public class DRUserDetails {
	private String UserName;
	private String Organisation;
	private String Token;
	
	public String getUserName() {
		return UserName;
	}
	@XmlElement(name = "UserName")
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getOrganisation() {
		return Organisation;
	}
	@XmlElement(name = "Organisation")
	public void setOrganisation(String organisation) {
		Organisation = organisation;
	}
	public String getToken() {
		return Token;
	}
	@XmlElement(name = "Token")
	public void setToken(String token) {
		Token = token;
	}
	
	
}
