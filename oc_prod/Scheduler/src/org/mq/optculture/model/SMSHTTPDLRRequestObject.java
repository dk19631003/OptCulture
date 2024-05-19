package org.mq.optculture.model;

import java.util.Calendar;

public class SMSHTTPDLRRequestObject extends BaseRequestObject{
	
	private String messageID;
	private String mobileNumber;
	private String deliveredTimeStr;
	private Calendar deliveredTime;
	private String status;
	private String reason;
	private String clientMsgId;
	private String userSMSTool;
	
	public String getUserSMSTool() {
		return userSMSTool;
	}
	public void setUserSMSTool(String userSMSTool) {
		this.userSMSTool = userSMSTool;
	}
	public String getClientMsgId() {
		return clientMsgId;
	}
	public void setClientMsgId(String clientMsgId) {
		this.clientMsgId = clientMsgId;
	}
	public String getMessageID() {
		return messageID;
	}
	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getDeliveredTimeStr() {
		return deliveredTimeStr;
	}
	public void setDeliveredTimeStr(String deliveredTimeStr) {
		this.deliveredTimeStr = deliveredTimeStr;
	}
	public Calendar getDeliveredTime() {
		return deliveredTime;
	}
	public void setDeliveredTime(Calendar deliveredTime) {
		this.deliveredTime = deliveredTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
