package org.mq.marketer.campaign.beans;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class SocialCampaign implements Serializable {

	Long campaignId;
	String campaignName;
	String description;
	Byte providers;
	String fbPageIds;
	String twitterContent;
	Long userId;
	Calendar createdDate;
	Calendar lastModifiedDate;
	Calendar scheduleDate;
	String campaignStatus;
	Set<SocialCampaignSchedule> socialCampSchedules = new HashSet<SocialCampaignSchedule>();

	public SocialCampaign() {
		// TODO Auto-generated constructor stub
	}

	public SocialCampaign(Long userId,String campaignName,String description,Byte providers,
			String campaignStatus,String twitterContent,String fbPageIds) {
		
		this.campaignName = campaignName;
		this.description = description;
		this.providers = providers;		
		this.createdDate = Calendar.getInstance();
		this.userId = userId;
		this.campaignStatus = campaignStatus;
		this.twitterContent = twitterContent;
		this.fbPageIds = fbPageIds;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Byte getProviders() {
		return providers;
	}

	public void setProviders(Byte providers) {
		this.providers = providers;
	}
	
	public String getFbPageIds() {
		return fbPageIds;
	}

	public void setFbPageIds(String fbPageIds) {
		this.fbPageIds = fbPageIds;
	}

	public String getTwitterContent() {
		return twitterContent;
	}

	public void setTwitterContent(String twitterContent) {
		this.twitterContent = twitterContent;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public String getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(String campaignStatus) {
		this.campaignStatus = campaignStatus;
	}

	public Set<SocialCampaignSchedule> getSocialCampSchedules() {
		return socialCampSchedules;
	}

	public void setSocialCampSchedules(
			Set<SocialCampaignSchedule> socialCampSchedules) {
		this.socialCampSchedules = socialCampSchedules;
	}

}
