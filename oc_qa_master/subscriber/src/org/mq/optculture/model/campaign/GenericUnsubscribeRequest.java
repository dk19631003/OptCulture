package org.mq.optculture.model.campaign;

import org.mq.optculture.model.BaseRequestObject;

public class GenericUnsubscribeRequest extends BaseRequestObject {
	
	private String emailId;
	private String reason;;
	private String action;
	private Long userId;
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
