package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.mq.marketer.campaign.general.Constants;

public class Notification implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Long notificationId;
	private String notificationName;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private String status;
	private String draftStatus;
	private String notificationSenderId;
	private String header;
	private String logoImageUrl;
	private String bannerImageUrl;
	private String redirectUrl;
	private String notificationContent;
	private String scheduleType;
	private Long userId;
	private String listType;
	private Long category;
	private Set<MailingList> mailingLists = new HashSet<MailingList>(0);
	//Transient variable
	private String scheduledOccurrence;
	private String scheduledDates;
	
	public String getScheduledOccurrence() {
		return scheduledOccurrence;
	}
	public void setScheduledOccurrence(String scheduledOccurrence) {
		this.scheduledOccurrence = scheduledOccurrence;
	}
	
	public String getScheduledDates() {
		return scheduledDates;
	}
	public void setScheduledDates(String scheduledDates) {
		this.scheduledDates = scheduledDates;
	}
	public Long getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}
	public String getNotificationName() {
		return notificationName;
	}
	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public Calendar getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDraftStatus() {
		return draftStatus;
	}
	public void setDraftStatus(String draftStatus) {
		this.draftStatus = draftStatus;
	}
	public String getNotificationSenderId() {
		return notificationSenderId;
	}
	public void setNotificationSenderId(String notificationSenderId) {
		this.notificationSenderId = notificationSenderId;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getLogoImageUrl() {
		return logoImageUrl;
	}
	public void setLogoImageUrl(String logoImageUrl) {
		this.logoImageUrl = logoImageUrl;
	}
	public String getBannerImageUrl() {
		return bannerImageUrl;
	}
	public void setBannerImageUrl(String bannerImageUrl) {
		this.bannerImageUrl = bannerImageUrl;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public String getNotificationContent() {
		return notificationContent;
	}
	public void setNotificationContent(String notificationContent) {
		this.notificationContent = notificationContent;
	}
	public String getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getListType() {
		return listType;
	}
	public void setListType(String listType) {
		this.listType = listType;
	}
	public Long getCategory() {
		return category;
	}
	public void setCategory(Long category) {
		this.category = category;
	}
	public Set<MailingList> getMailingLists() {
		return mailingLists;
	}
	public void setMailingLists(Set<MailingList> mailingLists) {
		this.mailingLists = mailingLists;
	}
	
	private String listNames ;
	
	public String getListNames() {
		
		listNames = "";
		for(MailingList ml : mailingLists) {
			
			if(listNames.length() > 0) {
				listNames +=","+ml.getListName();
			}else {
				listNames += ml.getListName();
			}
		}
		return listNames;
	}
	
	public void setListNames(String listNames) {
		
		this.listNames = listNames;
		
	}
	
public Notification getCopy() {
		Notification newCampaign = new Notification();
		newCampaign.setCreatedDate(Calendar.getInstance());
		newCampaign.setModifiedDate(Calendar.getInstance());
		newCampaign.setStatus(Constants.CAMP_STATUS_DRAFT);
		newCampaign.setDraftStatus(this.getDraftStatus());
		newCampaign.setNotificationContent(this.getNotificationContent());
		newCampaign.setHeader(this.getHeader());
		newCampaign.setRedirectUrl(this.getRedirectUrl());
		newCampaign.setScheduleType(this.getScheduleType());
		newCampaign.setLogoImageUrl(this.getLogoImageUrl());
		newCampaign.setBannerImageUrl(this.getBannerImageUrl());
		newCampaign.setListType(this.getListType());
		newCampaign.setNotificationSenderId(this.getNotificationSenderId());
		newCampaign.setUserId(this.getUserId());
		newCampaign.setCategory(this.getCategory());
		Set<MailingList> newMlSet = new HashSet<MailingList>();
		for (MailingList ml : this.getMailingLists()) {
			newMlSet.add(ml);
		}
		newCampaign.setMailingLists(newMlSet);
		return newCampaign;
	
	}
	
		@Override
		public String toString() 
		{ 
		    return "pushNotification [header="
		        + header 
		        + ",description="
		        + notificationContent 
		        + ", logoImage="
		        + logoImageUrl 
		        + ", redirectUrl="
		        + redirectUrl 
		        + ", bannerImage=" 
		        + bannerImageUrl+"]"; 
		} 

}
