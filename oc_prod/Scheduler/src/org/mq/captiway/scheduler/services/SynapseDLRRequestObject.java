package org.mq.captiway.scheduler.services;

import org.mq.optculture.model.BaseRequestObject;

public class SynapseDLRRequestObject extends BaseRequestObject{
	
	private String referenceId;
	private String msgId;
	private String mobile;
	private String status;
	private String deliveryTime;
	private String userSMSTool;
	
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	
	public String getUserSMSTool() {
		return userSMSTool;
	}
	public void setUserSMSTool(String userSMSTool) {
		this.userSMSTool = userSMSTool;
	}

}
