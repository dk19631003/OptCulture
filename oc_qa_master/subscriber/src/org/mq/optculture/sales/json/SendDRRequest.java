package org.mq.optculture.sales.json;

import org.mq.optculture.model.BaseRequestObject;

public class SendDRRequest extends BaseRequestObject {
	
	private String userName;
	private String userOrg;
	private String jsonValue;
	private String token;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserOrg() {
		return userOrg;
	}
	public void setUserOrg(String userOrg) {
		this.userOrg = userOrg;
	}
	public String getJsonValue() {
		return jsonValue;
	}
	public void setJsonValue(String jsonValue) {
		this.jsonValue = jsonValue;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
