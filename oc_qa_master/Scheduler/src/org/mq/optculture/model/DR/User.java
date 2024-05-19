package org.mq.optculture.model.DR;

public class User {

	private String userName;
	private String token;
	private String organizationId;
	public User(){}
	public User(String userName, String token, String organizationId){
		
		this.organizationId = organizationId;
		this.token = token;
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	
}
