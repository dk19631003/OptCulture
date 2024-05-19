package org.mq.captiway.scheduler.beans;

public class NotificationCampaignSent {
	private Long sentId;
    private String mobileNumber;
    private NotificationCampaignReport notificationCampaignReport;
    private String status;
    private int opens;
    private int clicks;
    private Long contactId;
    private String requestId;
    private String instanceId;
    private String contactPhValStr;
   
	
    public NotificationCampaignSent() {
    }
	
    public NotificationCampaignSent(Long sentId, String mobileNumber, NotificationCampaignReport smsCampaignReport, String status) {
       this.sentId = sentId;
       this.mobileNumber = mobileNumber;
       this.notificationCampaignReport = smsCampaignReport;
       this.status = status;
    }
	
    public NotificationCampaignSent(Long sentId, String mobileNumber, NotificationCampaignReport smsCampaignReport, String status,Long contactId, int opens, int clicks,String instanceId) {
	   this.sentId = sentId;
       this.mobileNumber = mobileNumber;
       this.notificationCampaignReport = smsCampaignReport;
       this.status = status;
       this.contactId = contactId;
       this.opens = opens;
       this.clicks = clicks;
       this.instanceId = instanceId;
    }

	public Long getSentId() {
		return sentId;
	}
	
	public void setSentId(Long sentId) {
		this.sentId = sentId;
	}
	
	public String getMobileNumber() {
		return mobileNumber;
	}
	
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	public NotificationCampaignReport getNotificationCampaignReport() {
		return notificationCampaignReport;
	}
	
	public void setNotificationCampaignReport(NotificationCampaignReport notificationCampaignReport) {
		this.notificationCampaignReport = notificationCampaignReport;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
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
	
	public Long getContactId() {
		return contactId;
	}
	
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	
	public String getRequestId() {
		return requestId;
	}
	
	public String getContactPhValStr() {
		return contactPhValStr;
	}

	public void setContactPhValStr(String contactPhValStr) {
		this.contactPhValStr = contactPhValStr;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public String getInstanceId() {
		return instanceId;
	}
	
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	
}
