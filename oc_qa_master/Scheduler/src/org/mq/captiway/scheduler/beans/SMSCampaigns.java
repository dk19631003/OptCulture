package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class SMSCampaigns implements java.io.Serializable {

	private Long smsCampaignId;
	private String smsCampaignName;
	private String senderId;
	
	
	private Users users;
	private Set<MailingList> mailingLists = new HashSet<MailingList>(0);
	
	
	private String messageContent;
	private String messageType;
	private Byte messageSizeOption;
	
	private Calendar createdDate;
    private Calendar modifiedDate;
    
    private String status;
    private String draftStatus;
    private String scheduleType;
    
    
    private String listType;
    
    private Long category;
    private String templateRegisteredId; //added for Equence
    
    
    private String scheduledOccurrence;
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

	private String scheduledDates;
    
	
   	public Long getCategory() {
   		return category;
   	}


   	public void setCategory(Long category) {
   		this.category = category;
   	}
    
	public String getListType() {
		return listType;
	}


	public void setListType(String listType) {
		this.listType = listType;
	}

	 private boolean enableEntireList;
	    
		
	public boolean isEnableEntireList() {
		return enableEntireList;
	}


	public void setEnableEntireList(boolean enableEntireList) {
		this.enableEntireList = enableEntireList;
	}
	
	/*
	    private Long campaignId;
     private String campaignName;
     private String label;
     private String subject;
     private String fromName;
     private String fromEmail;
     private String replyEmail;
     private String companyName;
     private Calendar createdDate;
     private Calendar modifiedDate;
     

     
     private String status;
     private String draftStatus;
     private String editorType;
     private String contentType;
     private String listsType;
     private String placeHoldersType;
     
     private String htmlText;
     private String finalHtmlText;
     private boolean prepared;
     private String textMessage;
     private boolean couponFlag;
     
     private String scheduleType;
     
     private boolean webLinkFlag;
     private String webLinkText;
     private String webLinkUrlText;
     private boolean permissionRemainderFlag;
     private String permissionRemainderText;
     
     
     private boolean addressFlag;
     
     private Address address;
     private String addressStr;
     
     private short categoryWeight;
     
     private Set<MailingList> mailingLists = new HashSet<MailingList>(0);
     private Users users;
     private SystemTemplates template;
     private boolean personalizeTo;
     private String toName;
     
	 */
    public SMSCampaigns() {}
    
    
    public SMSCampaigns(Calendar createdDate,Users users) {
    	this.createdDate = createdDate;
    	this.users = users;
    }
	public Long getSmsCampaignId() {
		return smsCampaignId;
	}
	public void setSmsCampaignId(Long smsCampaignId) {
		this.smsCampaignId = smsCampaignId;
	}
	
	
	public String getSmsCampaignName() {
		return smsCampaignName;
	}
	public void setSmsCampaignName(String smsCampaignName) {
		this.smsCampaignName = smsCampaignName;
	}
	
	
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	
	
	public Users getUsers() {
		return users;
	}
	public void setUsers(Users users) {
		this.users = users;
	}
	
	
	public Set<MailingList> getMailingLists() {
		return mailingLists;
	}
	public void setMailingLists(Set<MailingList> mailingLists) {
		this.mailingLists = mailingLists;
	}
	
	
	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	
	
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	
	public Byte getMessageSizeOption() {
		return messageSizeOption;
	}
	public void setMessageSizeOption(Byte messageSizeOption) {
		this.messageSizeOption = messageSizeOption;
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
	
	
	public String getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
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


	public String getTemplateRegisteredId() {
		return templateRegisteredId;
	}


	public void setTemplateRegisteredId(String templateRegisteredId) {
		this.templateRegisteredId = templateRegisteredId;
	}
}
