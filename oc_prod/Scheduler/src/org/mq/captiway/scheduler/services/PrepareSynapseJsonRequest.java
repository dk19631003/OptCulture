package org.mq.captiway.scheduler.services;

import java.util.List;

public class PrepareSynapseJsonRequest {
	
	private String userName;
	private String priority;
	private String referenceId;
	private String dlrUrl;
	private String msgType;
	private String senderId;
	private String message;
	private SynapseSMSList mobileNumbers;
	private String password;
	
	
	public PrepareSynapseJsonRequest(){
	//Default Constructor
	}
	
	public String getUserName() {
	return userName;
	}
	
	public void setUserName(String username) {
	this.userName = username;
	}
	
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getPassword() {
	return password;
	}
	
	public void setPassword(String password) {
	this.password = password;
	}
	public String getReferenceId() {
		return referenceId;
	}
	
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public SynapseSMSList getMobileNumbers() {
		return mobileNumbers;
	}

	public void setMobileNumbers(SynapseSMSList mobileNumbers) {
		this.mobileNumbers = mobileNumbers;
	}

	public String getDlrUrl() {
		return dlrUrl;
	}

	public void setDlrUrl(String dlrUrl) {
		this.dlrUrl = dlrUrl;
	}

}
