package org.mq.captiway.scheduler.beans;

import java.io.Serializable;
import java.util.Calendar;


public class SMSSettings implements Serializable{
	
	
	
	private Long setupId;
	private Long orgId;
	private Calendar validUpto;
	private Calendar createdDate;
	
	private Calendar startFrom;
	private String keyword;
	private String type;
	private Users userId;
	private String autoResponse;
	public String getAutoResponse() {
		return autoResponse;
	}
	public void setAutoResponse(String autoResponse) {
		this.autoResponse = autoResponse;
	}
	//private MailingList listId;
	private Long listId;
	private String shortCode;
	private String optinMissedCalNumber;
	public String getOptinMissedCalNumber() {
		return optinMissedCalNumber;
	}
	public void setOptinMissedCalNumber(String optinMissedCalNumber) {
		this.optinMissedCalNumber = optinMissedCalNumber;
	}
	private boolean enable;
	private boolean enableWelcomeMessage;
	private String welcomeMessage;
	//private boolean enableOptOutMsg;
	
	
	public String getWelcomeMessage() {
		return welcomeMessage;
	}
	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}
	public boolean isEnableWelcomeMessage() {
		return enableWelcomeMessage;
	}
	public void setEnableWelcomeMessage(boolean enableWelcomeMessage) {
		this.enableWelcomeMessage = enableWelcomeMessage;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	private String messageHeader;
	
	private Byte optInMedium;
	private String senderId;
	
	public String getShortCode() {
		return shortCode;
	}
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	
	public String getMessageHeader() {
		return messageHeader;
	}
	public void setMessageHeader(String messageHeader) {
		this.messageHeader = messageHeader;
	}
	
	/*public MailingList getListId() {
		return listId;
	}
	public void setListId(MailingList listId) {
		this.listId = listId;
	}*/
	
	
	public void setListId(Long listId) {
		this.listId = listId;
	}
	public Long getListId() {
		return listId;
	}
	public Long getSetupId() {
		return setupId;
	}
	public void setSetupId(Long setupId) {
		this.setupId = setupId;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public Calendar getValidUpto() {
		return validUpto;
	}
	public void setValidUpto(Calendar validUpto) {
		this.validUpto = validUpto;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public Calendar getStartFrom() {
		return startFrom;
	}
	public void setStartFrom(Calendar startFrom) {
		this.startFrom = startFrom;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Users getUserId() {
		return userId;
	}
	public void setUserId(Users userId) {
		this.userId = userId;
	}
	
	public Byte getOptInMedium() {
		return optInMedium;
	}
	public void setOptInMedium(Byte optInMedium) {
		this.optInMedium = optInMedium;
	}
	
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

}
