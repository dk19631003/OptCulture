package org.mq.optculture.model.ocloyalty;

public class Status {

	private String errorCode;
	private String message;
	private String status;
	
	public Status() {
	}
	
	public Status(String errorCode, String message, String status) {
		this.errorCode = errorCode;
		this.message = message;
		this.status = status;
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
