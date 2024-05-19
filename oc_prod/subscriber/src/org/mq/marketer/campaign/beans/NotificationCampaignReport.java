package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class NotificationCampaignReport implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private Long notificationCrId;
    private String notificationCampaignName;
    private String notificationContent;
    private String notificationHeaderContent;
    private String notificationUrl;
    private String notificationLogoImage;
    private String notificationBannerImage;
    private Calendar sentDate;
    private long configured;
    private long suppressedCount;
    private long sent;
    private int opens;
    private int clicks;
    private int unsubscribes;
    private int bounces;
    private long userId;
	private String status;
	private String sourceType;
	private int preferenceCount;
	private int creditsCount;
	
	public int getPreferenceCount() {
		return preferenceCount;
	}

	public void setPreferenceCount(int preferenceCount) {
		this.preferenceCount = preferenceCount;
	}

	public int getCreditsCount() {
		return creditsCount;
	}

	public void setCreditsCount(int creditsCount) {
		this.creditsCount = creditsCount;
	}
	public NotificationCampaignReport() {
	}

	public NotificationCampaignReport(String notificationCampaignName,Calendar sentDate, long sent, int opens, int clicks, int unsubscribes, int bounces,String status){
	    this.notificationCampaignName = notificationCampaignName;
	    
	    this.sent = sent;
	    this.sentDate = sentDate;
	    this.opens = opens;
	    this.clicks = clicks;
	    this.unsubscribes = unsubscribes;
	    this.bounces = bounces;
	    
	    this.status = status;
	}

	public NotificationCampaignReport(long userId,String notificationCampaignName,String notificationContent, 
			Calendar sentDate, long sent, int opens, int clicks, int unsubscribes, 
			int bounces,String status) {
		
	    this.userId = userId;
		this.notificationCampaignName = notificationCampaignName;
	    
	    this.notificationContent = notificationContent;
	    this.sent = sent;
	    this.sentDate = sentDate;
	    this.opens = opens;
	    this.clicks = clicks;
	    this.unsubscribes = unsubscribes;
	    this.bounces = bounces;
	    this.status = status;
	}
	
	
	public NotificationCampaignReport(Users user,String notificationCampaignName,String notificationContent, 
			Calendar sentDate, long sent, int opens, int clicks, int unsubscribes, 
			int bounces,String status, String sourceType) {
		
	    this.userId = userId;
		this.notificationCampaignName = notificationCampaignName;
	    
	    this.notificationContent = notificationContent;
	    this.sent = sent;
	    this.sentDate = sentDate;
	    this.opens = opens;
	    this.clicks = clicks;
	    this.unsubscribes = unsubscribes;
	    this.bounces = bounces;
	    this.status = status;
	    this.sourceType = sourceType;
	}

	public Long getNotificationCrId() {
		return notificationCrId;
	}

	public void setNotificationCrId(Long notificationCrId) {
		this.notificationCrId = notificationCrId;
	}

	public String getNotificationCampaignName() {
		return notificationCampaignName;
	}

	public void setNotificationCampaignName(String notificationCampaignName) {
		this.notificationCampaignName = notificationCampaignName;
	}

	public String getNotificationContent() {
		return notificationContent;
	}

	public void setNotificationContent(String notificationContent) {
		this.notificationContent = notificationContent;
	}

	public Calendar getSentDate() {
		return sentDate;
	}

	public void setSentDate(Calendar sentDate) {
		this.sentDate = sentDate;
	}

	public long getConfigured() {
		return configured;
	}

	public void setConfigured(long configured) {
		this.configured = configured;
	}

	public long getSuppressedCount() {
		return suppressedCount;
	}

	public void setSuppressedCount(long suppressedCount) {
		this.suppressedCount = suppressedCount;
	}

	public long getSent() {
		return sent;
	}

	public void setSent(long sent) {
		this.sent = sent;
	}

	public int getOpens() {
		return opens;
	}

	public void setOpens(int opens) {
		this.opens = opens;
	}

	public int getClicks() {
		return clicks;
	}

	public void setClicks(int clicks) {
		this.clicks = clicks;
	}

	public int getUnsubscribes() {
		return unsubscribes;
	}

	public void setUnsubscribes(int unsubscribes) {
		this.unsubscribes = unsubscribes;
	}

	public int getBounces() {
		return bounces;
	}

	public void setBounces(int bounces) {
		this.bounces = bounces;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getNotificationHeaderContent() {
		return notificationHeaderContent;
	}

	public void setNotificationHeaderContent(String notificationHeaderContent) {
		this.notificationHeaderContent = notificationHeaderContent;
	}

	public String getNotificationUrl() {
		return notificationUrl;
	}

	public void setNotificationUrl(String notificationUrl) {
		this.notificationUrl = notificationUrl;
	}

	public String getNotificationLogoImage() {
		return notificationLogoImage;
	}

	public void setNotificationLogoImage(String notificationLogoImage) {
		this.notificationLogoImage = notificationLogoImage;
	}

	public String getNotificationBannerImage() {
		return notificationBannerImage;
	}

	public void setNotificationBannerImage(String notificationBannerImage) {
		this.notificationBannerImage = notificationBannerImage;
	}
	
}
