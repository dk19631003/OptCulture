package org.mq.optculture.business.nexon;

public class NexonStoreEnquiryRequest {
	
	private String userName;
	private String token;
	private String organizationId;

	public String getorganizationId() {
		return organizationId;
	}

	public void setorganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getuserName() {
		return userName;
	}

	public void setuserName(String userName) {
		this.userName = userName;
	}


}
