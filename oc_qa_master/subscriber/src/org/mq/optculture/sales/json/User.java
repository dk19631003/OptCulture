package org.mq.optculture.sales.json;

public class User{
	
	private String userName;

	private String organizationId;
	private String token;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	

	public String getToken() {
		return token;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public void setToken(String token) {
		this.token = token;
	}
}