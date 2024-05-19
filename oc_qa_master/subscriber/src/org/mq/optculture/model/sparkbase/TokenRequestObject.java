package org.mq.optculture.model.sparkbase;

import org.mq.optculture.model.BaseRequestObject;

public class TokenRequestObject extends BaseRequestObject{
	
	private String userName;
	//private String password;
	private String orgId;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/*public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}*/
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

}
