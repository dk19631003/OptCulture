package org.mq.optculture.model.account;

import javax.xml.bind.annotation.XmlElement;

public class OCUser {



	private String userName;
	private String organizationId;
	private String password;
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String token;
	private String sessionID;
	
	public OCUser() {
	}

	public String getUserName() {
		return userName;
	}
	@XmlElement(name = "userName")
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrganizationId() {
		return organizationId;
	}
	@XmlElement(name = "organizationId")
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getToken() {
		return token;
	}
	@XmlElement(name = "token")
	public void setToken(String token) {
		this.token = token;
	}

	
	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	

}
