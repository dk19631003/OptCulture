package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class SocialCampaignReport implements java.io.Serializable{

	Long reportId;
	Long campaignId;
	Long scheduleId;
	String campaignName;
	String providerType;
	Calendar sentDate;
	String campaignStatus;
	String providerToken;
	Long userId;
	
	public SocialCampaignReport() {
		// TODO Auto-generated constructor stub
	}
	
	public SocialCampaignReport(Long campaignId, Long scheduleId, String campaignName,String providerType, Calendar sentDate,
			String providerToken,Long userId,String campaignStatus) {
		this.campaignId = campaignId;
		this.scheduleId = scheduleId;
		this.campaignName = campaignName;
		this.providerType = providerType;
		this.sentDate = sentDate;
		this.userId = userId;
		this.providerToken = providerToken;
		this.campaignStatus = campaignStatus;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getProviderType() {
		return providerType;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public Calendar getSentDate() {
		return sentDate;
	}

	public void setSentDate(Calendar sentDate) {
		this.sentDate = sentDate;
	}

	public String getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(String campaignStatus) {
		this.campaignStatus = campaignStatus;
	}

	public String getProviderToken() {
		return providerToken;
	}

	public void setProviderToken(String providerToken) {
		this.providerToken = providerToken;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}
	
	
	
}
