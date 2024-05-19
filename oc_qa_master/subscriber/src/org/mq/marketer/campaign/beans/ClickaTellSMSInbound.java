package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class ClickaTellSMSInbound implements java.io.Serializable{

	private Long inboundMsgId;
	private String text;
	private Calendar timeStamp;
	private String usedKeyWords;
	private String moFrom;
	private String moTo;
	private Long orgId;
	private String autoResponse;
	
	private String deliveryStatus;
	private Calendar deliveredTime;
	private String msgID;
	
	public ClickaTellSMSInbound() { }
	
	
	
	public Long getInboundMsgId() {
		return inboundMsgId;
	}
	public void setInboundMsgId(Long inboundMsgId) {
		this.inboundMsgId = inboundMsgId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Calendar getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Calendar timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getUsedKeyWords() {
		return usedKeyWords;
	}
	public void setUsedKeyWords(String usedKeyWords) {
		this.usedKeyWords = usedKeyWords;
	}
	public String getMoFrom() {
		return moFrom;
	}
	public void setMoFrom(String moFrom) {
		this.moFrom = moFrom;
	}
	public String getMoTo() {
		return moTo;
	}
	public void setMoTo(String moTo) {
		this.moTo = moTo;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	
	public Calendar getDeliveredTime() {
		return deliveredTime;
	}


	public void setDeliveredTime(Calendar deliveredTime) {
		this.deliveredTime = deliveredTime;
	}
	
	public String getAutoResponse() {
		return autoResponse;
	}

	public void setAutoResponse(String autoResponse) {
		this.autoResponse = autoResponse;
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}
	public String getMsgID() {
		return msgID;
	}

	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}
	
}
