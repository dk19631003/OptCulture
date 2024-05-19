package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class WACampaignSent {
	private Long sentId;
	private String mobileNumber;
	private WACampaignReport waCampaignReport;
	private String status;
	private int opens;
	private int clicks;
	private Calendar bounceTime;
	private String bounceInfo;
	private Long contactId;
	private String requestId;
	private String apiMsgId;


	public String getApiMsgId() {
		return apiMsgId;
	}
	public void setApiMsgId(String apiMsgId) {
		this.apiMsgId = apiMsgId;
	}


	public WACampaignSent() {
	}
	public WACampaignSent(Long sentId, String mobileNumber, WACampaignReport waCampaignReport, String status) {
		this.sentId = sentId;
		this.mobileNumber = mobileNumber;
		this.waCampaignReport = waCampaignReport;
		this.status = status;
	}

	public WACampaignSent(Long sentId, String mobileNumber, WACampaignReport waCampaignReport, String status,Long contactId) {
		this.sentId = sentId;
		this.mobileNumber = mobileNumber;
		this.waCampaignReport = waCampaignReport;
		this.status = status;
		this.contactId = contactId;
	}

	public WACampaignSent(Long sentId,String mobileNumber, int opens) {
		this.sentId = sentId;
		this.mobileNumber = mobileNumber;
		this.opens = opens;
	}

	public WACampaignSent(String mobileNumber, WACampaignReport waCampaignReport, 
			String status, int opens, int clicks, Calendar bounceTime, String bounceInfo) {

		this.mobileNumber = mobileNumber;
		this.waCampaignReport = waCampaignReport;
		this.status = status;
		this.opens = opens;
		this.clicks = clicks;
		this.bounceTime = bounceTime;
		this.bounceInfo = bounceInfo;
	}

	public Long getSentId() {
		return this.sentId;
	}

	public void setSentId(Long sentId) {
		this.sentId = sentId;
	}
	public String getMobileNumber() {
		return this.mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public WACampaignReport getWaCampaignReport() {
		return waCampaignReport;
	}
	public void setWaCampaignReport(WACampaignReport waCampaignReport) {
		this.waCampaignReport = waCampaignReport;
	}
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getOpens() {
		return this.opens;
	}

	public void setOpens(int opens) {
		this.opens = opens;
	}
	public int getClicks() {
		return this.clicks;
	}

	public void setClicks(int clicks) {
		this.clicks = clicks;
	}
	public Calendar getBounceTime() {
		return this.bounceTime;
	}

	public void setBounceTime(Calendar bounceTime) {
		this.bounceTime = bounceTime;
	}
	public String getBounceInfo() {
		return this.bounceInfo;
	}

	public void setBounceInfo(String bounceInfo) {
		this.bounceInfo = bounceInfo;
	}
	public Long getContactId() {
		return contactId;
	}
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

}
