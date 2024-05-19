package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.mq.marketer.campaign.general.Constants;

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
    private boolean enableEntireList;
    
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


	public boolean isEnableEntireList() {
		return enableEntireList;
	}


	public void setEnableEntireList(boolean enableEntireList) {
		this.enableEntireList = enableEntireList;
	}


	public String getListType() {
		return listType;
	}


	public void setListType(String listType) {
		this.listType = listType;
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
	
	
public void setListNames(String listNames) {
		
		this.listNames = listNames;
		
	}
	
	public SMSCampaigns getCopy() {
		
		SMSCampaigns newCampaign = new SMSCampaigns();
		//newCampaign.setSmsCampaignName(this.smsCampaignName);
		newCampaign.setCreatedDate(Calendar.getInstance());
		newCampaign.setModifiedDate(Calendar.getInstance());
		newCampaign.setStatus(Constants.CAMP_STATUS_DRAFT);
		newCampaign.setDraftStatus(this.getDraftStatus());
		newCampaign.setMessageContent(this.getMessageContent());
		newCampaign.setMessageType(this.getMessageType());
		newCampaign.setEnableEntireList(this.isEnableEntireList());
		newCampaign.setListType(this.getListType());
		newCampaign.setMessageSizeOption(this.getMessageSizeOption());
		newCampaign.setSenderId(this.getSenderId());
		newCampaign.setUsers(this.getUsers());
		newCampaign.setCategory(this.getCategory());
		newCampaign.setTemplateRegisteredId(this.getTemplateRegisteredId());
		Set<MailingList> newMlSet = new HashSet();
		for (MailingList ml : this.getMailingLists()) {
			newMlSet.add(ml);
		}
		newCampaign.setMailingLists(newMlSet);
		return newCampaign;
	
/*
		SMSCampaigns newCampaign = new SMSCampaigns();
		newCampaign.setSmsCampaignName(this.smsCampaignName);
		newCampaign.setCreatedDate(Calendar.getInstance());
		newCampaign.setModifiedDate(Calendar.getInstance());
		newCampaign.setStatus(Constants.CAMP_STATUS_DRAFT);
		newCampaign.setDraftStatus(this.draftStatus);
		newCampaign.setMessageContent(this.messageContent);
		newCampaign.setMessageType(this.messageType);
		newCampaign.setMessageSizeOption(this.messageSizeOption);
		newCampaign.setSenderId(this.senderId);
		newCampaign.setUsers(this.users);
		Set<MailingList> newMlSet = new HashSet();
		for (MailingList ml : this.mailingLists) {
			newMlSet.add(ml);
		}
		newCampaign.setMailingLists(newMlSet);
		return newCampaign;*/
	}

	@Override
	public String toString() {
		return this.smsCampaignName;
	}


	public String getTemplateRegisteredId() {
		return templateRegisteredId;
	}


	public void setTemplateRegisteredId(String templateRegisteredId) {
		this.templateRegisteredId = templateRegisteredId;
	}
	
}
