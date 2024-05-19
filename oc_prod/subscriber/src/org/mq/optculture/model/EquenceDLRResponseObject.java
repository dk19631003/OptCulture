package org.mq.optculture.model;

import java.util.Calendar;

public class EquenceDLRResponseObject extends BaseRequestObject{
	
	
	
	private String msg_id;
	private String MSGID;
	private String mobile_no;
	private String sms_delv_dttime;
	private String sms_delv_status;
	private String remarks;
	private String mr_id;
	private String userSMSTool;
	//private String timeFormat;
	
	public String getUserSMSTool() {
		return userSMSTool;
	}
	public void setUserSMSTool(String userSMSTool) {
		this.userSMSTool = userSMSTool;
	}
	public String getMessageID() {
		return msg_id;
	}
	public void setMessageID(String messageID) {
		this.msg_id = messageID;
	}
	public String getMSGID() {
		return MSGID;
	}
	public void setMSGID(String messageID) {
		this.MSGID = messageID;
	}
	public String getMobileNumber() {
		return mobile_no;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobile_no = mobileNumber;
	}
	public String getDeliveredTime() {
		return sms_delv_dttime;
	}
	public void setDeliveredTime(String deliveredTime) {
		this.sms_delv_dttime = deliveredTime;
	}
	public String getStatus() {
		return sms_delv_status;
	}
	public void setStatus(String status) {
		this.sms_delv_status = status;
	}
	public String getReason() {
		return remarks;
	}
	public void setReason(String reason) {
		this.remarks = reason;
	}
	public String getMrId() {
		return mr_id;
	}
	public void setMrId(String mrId) {
		this.mr_id = mrId;
	}
	/*public String getTimeFormat() {
		return timeFormat;
	}
	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}*/
	
}
