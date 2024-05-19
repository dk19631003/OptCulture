package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings ({"serial"})
public class EventTrigger implements  java.io.Serializable, Comparable<Object> {

	private Long id;
	private String triggerName;
	private Long campaignId; 
	private String triggerType;
	private int minutesOffset;
	private String eventField;
	private String dataType;
	private String status; //possible values: ACTIVE/SENT/DRAFT/EXPIRED
	private Calendar triggerCreatedDate;
	private Calendar triggerModifiedDate;
	private Calendar lastSentDate;
	private String selectedCampaignFromName;
	private String selectedCampaignReplyEmail;
	private String selectedCampaignFromEmail;
	private int optionsFlag; //refer to Constants.java for flag values
	private Users users;
	private String smsQueryStr;//added for sms functionality  
	private Long smsId;//added for sms functionality  
    private Set<MailingList> mailingLists = new HashSet<MailingList>(0);
    private Set<MailingList> addTriggerContactsToMls;
    private MailingList addTriggerContactsToMl;
    
  

	//added for trigger new implementation
    private String triggerQuery;
	private Long trBits;
    private Integer trType;
    private Long trBitFlagOffTime;
    private String inputStr;//not required
    private Calendar lastFetchedTime;
    private String targetDaysFlag; 
    private Date targetTime;
    
    private Long campCategory;
	
  	public Long getCampCategory() {
  		return campCategory;
  	}

  	public void setCampCategory(Long campCategory) {
  		this.campCategory = campCategory;
  	}
  	

	public Calendar getLastFetchedTime() {
		return lastFetchedTime;
	}

	public void setLastFetchedTime(Calendar lastFetchedTime) {
		this.lastFetchedTime = lastFetchedTime;
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

    
    
	public EventTrigger () {
	}

	public EventTrigger(String triggerName, Long campaignId,
			String triggerType, int minutesOffset, String eventField, String dataType, 
			Users users, String status, Calendar 	triggerCreatedDate, Calendar triggerModifiedDate){
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

	public int getMinutesOffset() {
		return minutesOffset;
	}

	public void setMinutesOffset(int minutesOffset) {
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

	public Set<MailingList> getAddTriggerContactsToMls() {
		return addTriggerContactsToMls;
	}

	public void setAddTriggerContactsToMls(Set<MailingList> addTriggerContactsToMls) {
		this.addTriggerContactsToMls = addTriggerContactsToMls;
	}

	
	public MailingList getAddTriggerContactsToMl() {
		return addTriggerContactsToMl;
	}

	public void setAddTriggerContactsToMl(MailingList addTriggerContactsToMl) {
		this.addTriggerContactsToMl = addTriggerContactsToMl;
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

	public int getOptionsFlag() {
		return optionsFlag;
	}

	public void setOptionsFlag(int optionsFlag) {
		this.optionsFlag = optionsFlag;
	}

	public String getSmsQueryStr() {
		return smsQueryStr;
	}

	public void setSmsQueryStr(String smsQueryStr) {
		this.smsQueryStr = smsQueryStr;
	}
	public Long getSmsId() {
		return smsId;
	}

	public void setSmsId(Long smsId) {
		this.smsId = smsId;
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
	
	
	public int compareTo(Object obj){
		
		return 1;
	
	}
}
