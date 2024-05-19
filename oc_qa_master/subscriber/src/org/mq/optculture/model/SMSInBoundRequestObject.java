package org.mq.optculture.model;

import java.util.Calendar;

public class SMSInBoundRequestObject extends BaseRequestObject{

	//private String requestType;
	private String source;
	private String destination;
	private String receivedTimeStr;
	
	private Calendar receivedTimeCal;
	//private String content;
	
	/*public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}*/
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public Calendar getReceivedTimeCal() {
		return receivedTimeCal;
	}
	public void setReceivedTimeCal(Calendar receivedTime) {
		this.receivedTimeCal = receivedTime;
	}
	/*public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}*/
	
	
	public String getReceivedTimeStr() {
		return receivedTimeStr;
	}
	public void setReceivedTimeStr(String receivedTimeStr) {
		this.receivedTimeStr = receivedTimeStr;
	}
	
}
