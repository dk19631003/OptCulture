package org.mq.optculture.model;

public class WAHTTPDLRRequestObject extends BaseRequestObject{

	private String messageID;
	private String mobileNumber;
	//private String deliveredTimeStr;
	//private Calendar deliveredTime;
	private String status;
	private Long sentId;


	public String getMessageID() {
		return messageID;
	}
	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getSentId() {
		return sentId;
	}
	public void setSentId(Long sentId) {
		this.sentId = sentId;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	


}
