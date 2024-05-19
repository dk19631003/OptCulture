package org.mq.captiway.scheduler.services;

public class Item {

	private String recipient;
	private String status;
	private String statusDatetime;
	private String messagePartCount;
	
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusDatetime() {
		return statusDatetime;
	}
	public void setStatusDatetime(String statusDatetime) {
		this.statusDatetime = statusDatetime;
	}
	public String getMessagePartCount() {
		return messagePartCount;
	}
	public void setMessagePartCount(String messagePartCount) {
		this.messagePartCount = messagePartCount;
	}
	
}
