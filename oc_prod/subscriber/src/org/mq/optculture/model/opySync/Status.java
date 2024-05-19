package org.mq.optculture.model.opySync;

public class Status {
	private String STATUS;
	private String MESSAGE;
	private String ERRORCODE;

	public Status(String errorCode, String message,
			String status) {
		this.ERRORCODE = errorCode;
		this.MESSAGE = message;
		this.STATUS = status;
	}
	public String getStatus() {
		return STATUS;
	}
	public void setStatus(String status) {
		this.STATUS = status;
	}
	public String getMessage() {
		return MESSAGE;
	}
	public void setMessage(String message) {
		this.MESSAGE = message;
	}
	public String getErrorCode() {
		return ERRORCODE;
	}
	public void setErrorCode(String errorCode) {
		this.ERRORCODE = errorCode;
	}


}
