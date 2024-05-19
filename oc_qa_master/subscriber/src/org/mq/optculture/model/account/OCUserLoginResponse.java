package org.mq.optculture.model.account;

import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.Status;

public class OCUserLoginResponse extends BaseResponseObject {

	private Status status;
	private String sessionID;
	private String APIToken;
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public String getAPIToken() {
		return APIToken;
	}
	public void setAPIToken(String aPIToken) {
		APIToken = aPIToken;
	}
}
