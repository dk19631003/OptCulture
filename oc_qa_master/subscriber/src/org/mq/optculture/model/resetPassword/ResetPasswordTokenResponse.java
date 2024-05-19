package org.mq.optculture.model.resetPassword;

import org.mq.optculture.model.BaseResponseObject;

public class ResetPasswordTokenResponse extends BaseResponseObject{
	String Status;

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	} 
}
