package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class AutoSmsQueue implements java.io.Serializable {
	
 	private long id; 
 	private String message;
    private String type;
    private String status;
	private String toMobileNo;
    private String accountType;
    private String senderId;
    private long userId;
    private Calendar sentDate;
    private Long contactId;
    private String messageId;
    private String dlrStatus;
	private Long loyaltyId;
	private Long templateId;
	private int clicks;
	private String templateRegisteredId;
    
    public AutoSmsQueue(){
    }
    
    public AutoSmsQueue(String message, String type, String status,String toMobileNo,String accountType,
    		String senderId,Calendar sentDate,long userId, Long contactId, Long loyaltyId) {
    	this.message = message;
    	this.type = type;
        this.status = status;
        this.toMobileNo = toMobileNo;
        this.accountType = accountType;
        this.senderId = senderId;
        this.sentDate = sentDate;
        this.userId = userId;
        this.contactId = contactId;
        this.loyaltyId = loyaltyId;
    }
    
    public AutoSmsQueue(String message, String type, String toMobileNo, Long userId, Long cid, Long loyaltyId) {
    	this.message = message;
    	this.type = type;
        this.toMobileNo = toMobileNo;
        this.userId = userId;
        this.contactId = cid;
        this.loyaltyId = loyaltyId;
    }
    
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getToMobileNo() {
		return toMobileNo;
	}
	public void setToMobileNo(String toMobileNo) {
		this.toMobileNo = toMobileNo;
	}
	
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public Calendar getSentDate() {
		return sentDate;
	}
	public void setSentDate(Calendar sentDate) {
		this.sentDate = sentDate;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getDlrStatus() {
		return dlrStatus;
	}

	public void setDlrStatus(String dlrStatus) {
		this.dlrStatus = dlrStatus;
	}
    public Long getLoyaltyId() {
		return loyaltyId;
	}
	public void setLoyaltyId(Long loyaltyId) {
		this.loyaltyId = loyaltyId;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public int getClicks() {
		return clicks;
	}

	public void setClicks(int clicks) {
		this.clicks = clicks;
	}

	public String getTemplateRegisteredId() {
		return templateRegisteredId;
	}

	public void setTemplateRegisteredId(String templateRegisteredId) {
		this.templateRegisteredId = templateRegisteredId;
	}
	
    
}
