package org.mq.marketer.campaign.beans;

import java.io.Serializable;
import java.util.Calendar;

public class SocialCampaignSchedule implements Serializable{

	Long scheduleId;
	Long campaignId;
	String campaignContent;
	String postType;
	String urlLinks;
	Calendar createdDate;
	Calendar lastModifiedDate;
	Calendar scheduleDate;
	String scheduleStatus;
	String failureStatus;

	public SocialCampaignSchedule() {
		// TODO Auto-generated constructor stub
	}
	
	public SocialCampaignSchedule(Long socialCampaignId, String campaignContent,String postType,String urlLinks,
			String scheduleStatus, Calendar scheduleDate) {
		
		this.campaignId = socialCampaignId;
		this.campaignContent = campaignContent;
		this.postType = postType;
		this.urlLinks = urlLinks;
		this.scheduleDate = scheduleDate;
		this.scheduleStatus = scheduleStatus;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public String getCampaignContent() {
		return campaignContent;
	}

	public void setCampaignContent(String campaignContent) {
		this.campaignContent = campaignContent;
	}

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public String getUrlLinks() {
		return urlLinks;
	}

	public void setUrlLinks(String urlLinks) {
		this.urlLinks = urlLinks;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public Calendar getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Calendar lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Calendar getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Calendar scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public String getScheduleStatus() {
		return scheduleStatus;
	}

	public void setScheduleStatus(String scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}

	public String getFailureStatus() {
		return failureStatus;
	}

	public void setFailureStatus(String failureStatus) {
		this.failureStatus = failureStatus;
	}
	
}
