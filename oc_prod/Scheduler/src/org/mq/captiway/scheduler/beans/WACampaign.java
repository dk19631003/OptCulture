package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class WACampaign implements java.io.Serializable {

	private Long waCampaignId;
	private String waCampaignName;


	private Users users;
	private Set<MailingList> mailingLists = new HashSet<MailingList>(0);


	private String messageContent;

	private Calendar createdDate;
	private Calendar modifiedDate;

	private String status;
	private String draftStatus;


	private String listType;
	private String waTemplateId;

	private String scheduledOccurrence;
	private String scheduledDates;


	public Long getWaCampaignId() {
		return waCampaignId;
	}


	public void setWaCampaignId(Long waCampaignId) {
		this.waCampaignId = waCampaignId;
	}


	public String getWaCampaignName() {
		return waCampaignName;
	}


	public void setWaCampaignName(String waCampaignName) {
		this.waCampaignName = waCampaignName;
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


	public String getListType() {
		return listType;
	}


	public void setListType(String listType) {
		this.listType = listType;
	}


	public String getWaTemplateId() {
		return waTemplateId;
	}


	public void setWaTemplateId(String waTemplateId) {
		this.waTemplateId = waTemplateId;
	}


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


	public void setListNames(String listNames) {
		this.listNames = listNames;
	}


	public WACampaign() {}


	public WACampaign(Calendar createdDate,Users users) {
		this.createdDate = createdDate;
		this.users = users;
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



}
