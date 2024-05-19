package org.mq.optculture.model.digitalReceipt;

public class ResponseInfo {
	
	private Status status;
	
	public ResponseInfo(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
