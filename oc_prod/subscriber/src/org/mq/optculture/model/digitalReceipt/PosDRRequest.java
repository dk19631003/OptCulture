package org.mq.optculture.model.digitalReceipt;

import org.mq.optculture.model.BaseRequestObject;

public class PosDRRequest extends BaseRequestObject {
	private String userName;
	private String userOrg;
	private String jsonValue;
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
}
