package org.mq.optculture.model.campaign;

import org.mq.optculture.model.BaseResponseObject;

public class GenericUnsubscribeResponse  extends BaseResponseObject{
	private String responseType;
	private String emailId;
	private Long userId;
	private String status;
	
	
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
