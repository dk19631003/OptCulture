package org.mq.optculture.model.resetPassword;

import org.mq.optculture.model.BaseRequestObject;

public class ResetPasswordTokenRequest extends BaseRequestObject {
	String token;
	Long userId;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
