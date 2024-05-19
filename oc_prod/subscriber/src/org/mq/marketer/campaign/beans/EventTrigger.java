package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mq.marketer.campaign.beans.MailingList;

@SuppressWarnings ({"serial"})
public class EventTrigger implements  java.io.Serializable, Comparable<Object> {

	private Long id;
	private String triggerName;
	private String triggerType;//long value
	private Users users;
	private Long minutesOffset;
	private String eventField;
	private String dataType;
	private String status; //possible values: ACTIVE/SENT/DRAFT/EXPIRED
	private Calendar triggerCreatedDate;
	private Calendar triggerModifiedDate;
	private Calendar lastSentDate;//
	private String selectedCampaignFromName;
	private String selectedCampaignReplyEmail;
	private String selectedCampaignFromEmail;
	private Long optionsFlag; //refer to Constants.java for flag values
    private Long campaignId; 
    private Long smsId;//added for sms functionality
    
    private Set<MailingList> mailingLists = new HashSet(0);//no need
   // private Set<MailingList> addTriggerContactsToMls = new HashSet(0);//to be deleted
    
    
    private MailingList addTriggerContactsToMl;
   

	//added for trigger new implementation
    private String triggerQuery;
	private Long trBits;
    private Integer trType;
    private Long trBitFlagOffTime;
    private String inputStr;//not required
    private String targetDaysFlag; 
    private Date targetTime;
    
    private Calendar lastFetchedTime;

    
    private Long campCategory;
	
	public Long getCampCategory() {
		return campCategory;
	}

	public void setCampCategory(Long campCategory) {
		this.campCategory = campCategory;
	}

	public EventTrigger () {
	}

	public EventTrigger(String triggerName, Long campaignId,
			String triggerType, Long minutesOffset, String eventField, String dataType, 
			Users users, String status, Calendar triggerCreatedDate, Calendar triggerModifiedDate){
		this.triggerName = triggerName;
		this.campaignId = campaignId;
		this.triggerType = triggerType;
		this.minutesOffset = minutesOffset;
		this.eventField = eventField;
		this.dataType = dataType;
		this.users = users;
		this.status=status;
		this.triggerCreatedDate = triggerCreatedDate;
		this.triggerModifiedDate = triggerModifiedDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public String getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	public Long getMinutesOffset() {
		return minutesOffset;
	}

	public void setMinutesOffset(Long minutesOffset) {
		this.minutesOffset = minutesOffset;
	}

	public String getEventField(){
		return this.eventField;
	}

	public void setEventField(String eventField){
		this.eventField = eventField;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getOptionsFlag() {
		return optionsFlag;
	}

	public void setOptionsFlag(Long optionsFlag) {
		this.optionsFlag = optionsFlag;
	}

	public Users getUsers() {
		return users;
	}

	public Calendar getTriggerCreatedDate() {
		return triggerCreatedDate;
	}

	public void setTriggerCreatedDate(Calendar triggerCreatedDate) {
		this.triggerCreatedDate = triggerCreatedDate;
	}

	public Calendar getTriggerModifiedDate() {
		return triggerModifiedDate;
	}

	public void setTriggerModifiedDate(Calendar triggerModifiedDate) {
		this.triggerModifiedDate = triggerModifiedDate;
	}

	public Calendar getLastSentDate() {
		return lastSentDate; 
	}

	public void setLastSentDate(Calendar lastSentDate) {
		this.lastSentDate = lastSentDate;
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


	public Long getSmsId() {
		return smsId;
	}

	public void setSmsId(Long smsId) {
		this.smsId = smsId;
	}

	public String getSelectedCampaignFromName() {
		return selectedCampaignFromName;
	}

	public void setSelectedCampaignFromName(String selectedCampaignFromName) {
		this.selectedCampaignFromName = selectedCampaignFromName;
	}

	public String getSelectedCampaignReplyEmail() {
		return selectedCampaignReplyEmail;
	}

	public void setSelectedCampaignReplyEmail(String selectedCampaignReplyEmail) {
		this.selectedCampaignReplyEmail = selectedCampaignReplyEmail;
	}

	public String getSelectedCampaignFromEmail() {
		return selectedCampaignFromEmail;
	}

	public void setSelectedCampaignFromEmail(String selectedCampaignFromEmail) {
		this.selectedCampaignFromEmail = selectedCampaignFromEmail;
	}
	
	/*public Set<MailingList> getAddTriggerContactsToMls() {
		return addTriggerContactsToMls;
	}

	public void setAddTriggerContactsToMls(Set<MailingList> addTriggerContactsToMls) {
		this.addTriggerContactsToMls = addTriggerContactsToMls;
	}*/

	public int compareTo(Object obj){
		
		return 1;
	/*	if(obj == null){
			return 1;
		}
		else if(obj instanceof EventTrigger) 
		{
			EventTrigger eventTrigger = (EventTrigger)obj;
			return eventTrigger.triggerModifiedDate.compareTo(this.triggerModifiedDate);
		}
		else {
			return 1;
		}*/

	}
	
	public String getTriggerQuery() {
		return triggerQuery;
	}

	public void setTriggerQuery(String triggerQuery) {
		this.triggerQuery = triggerQuery;
	}

	public Long getTrBits() {
		return trBits;
	}

	public void setTrBits(Long trBits) {
		this.trBits = trBits;
	}

	public Integer getTrType() {
		return trType;
	}

	public void setTrType(Integer trType) {
		this.trType = trType;
	}

	public Long getTrBitFlagOffTime() {
		return trBitFlagOffTime;
	}

	public void setTrBitFlagOffTime(Long trBitFlagOffTime) {
		this.trBitFlagOffTime = trBitFlagOffTime;
	}

	public String getInputStr() {
		return inputStr;
	}

	public void setInputStr(String inputStr) {
		this.inputStr = inputStr;
	}

	public Calendar getLastFetchedTime() {
		return lastFetchedTime;
	}

	public void setLastFetchedTime(Calendar lastFetchedTime) {
		this.lastFetchedTime = lastFetchedTime;
	}
	
	 public MailingList getAddTriggerContactsToMl() {
		return addTriggerContactsToMl;
	}

	public void setAddTriggerContactsToMl(MailingList addTriggerContactsToMl) {
		this.addTriggerContactsToMl = addTriggerContactsToMl;
	}

	public String getTargetDaysFlag() {
		return targetDaysFlag;
	}

	public void setTargetDaysFlag(String targetDaysFlag) {
		this.targetDaysFlag = targetDaysFlag;
	}

	

	public Date getTargetTime() {
		return targetTime;
	}

	public void setTargetTime(Date targetTime) {
		this.targetTime = targetTime;
	}
	
	
	
}
