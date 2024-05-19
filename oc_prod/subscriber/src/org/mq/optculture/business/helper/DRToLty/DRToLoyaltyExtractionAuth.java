package org.mq.optculture.business.helper.DRToLty;

public class DRToLoyaltyExtractionAuth {

	private String userName;
	private String orgID;
	private String token;
	
	public DRToLoyaltyExtractionAuth(String userName, String orgID, String token) {
		super();
		this.userName = userName;
		this.orgID = orgID;
		this.token = token;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOrgID() {
		return orgID;
	}
	public void setOrgID(String orgID) {
		this.orgID = orgID;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
