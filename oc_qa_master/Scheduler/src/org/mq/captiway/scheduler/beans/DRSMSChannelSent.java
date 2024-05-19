package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class DRSMSChannelSent implements java.io.Serializable{
	
	private long id;
	private long userId;
	private Long contactId;
	private String mobile;
	private String email;
	private String name;
	private String status;
	private Calendar sentDate;
	private int sentCount;
	private int opens;
    private int clicks;
    private Long drJsonObjId;
    private String docSid;
    private String storeNumber;
    private String sbsNumber;
    private String ackId;
    private String dlrStatus;
	private String orgId;
    private Long zoneId;
    private String htmlContent;
    
    private String originalShortCode;
    private String shortUrl;
    private String originalUrl;
    private String generatedShortCode;
    
    private long uniqueOpens;
    private long uniqueClicks;
    
    private String receiptNo;
    private Calendar transactionTime;
    
    private String channel;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public Long getContactId() {
		return contactId;
	}
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Calendar getSentDate() {
		return sentDate;
	}
	public void setSentDate(Calendar sentDate) {
		this.sentDate = sentDate;
	}
	public int getSentCount() {
		return sentCount;
	}
	public void setSentCount(int sentCount) {
		this.sentCount = sentCount;
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
	public Long getDrJsonObjId() {
		return drJsonObjId;
	}
	public void setDrJsonObjId(Long drJsonObjId) {
		this.drJsonObjId = drJsonObjId;
	}
	public String getDocSid() {
		return docSid;
	}
	public void setDocSid(String docSid) {
		this.docSid = docSid;
	}
	public String getStoreNumber() {
		return storeNumber;
	}
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}
	public String getSbsNumber() {
		return sbsNumber;
	}
	public void setSbsNumber(String sbsNumber) {
		this.sbsNumber = sbsNumber;
	}
	public String getAckId() {
		return ackId;
	}
	public void setAckId(String ackId) {
		this.ackId = ackId;
	}
	public String getDlrStatus() {
		return dlrStatus;
	}
	public void setDlrStatus(String dlrStatus) {
		this.dlrStatus = dlrStatus;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public Long getZoneId() {
		return zoneId;
	}
	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	public String getOriginalShortCode() {
		return originalShortCode;
	}
	public void setOriginalShortCode(String originalShortCode) {
		this.originalShortCode = originalShortCode;
	}
	public String getShortUrl() {
		return shortUrl;
	}
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	public String getOriginalUrl() {
		return originalUrl;
	}
	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}
	public String getGeneratedShortCode() {
		return generatedShortCode;
	}
	public void setGeneratedShortCode(String generatedShortCode) {
		this.generatedShortCode = generatedShortCode;
	}
	public long getUniqueOpens() {
		return uniqueOpens;
	}
	public void setUniqueOpens(long uniqueOpens) {
		this.uniqueOpens = uniqueOpens;
	}
	public long getUniqueClicks() {
		return uniqueClicks;
	}
	public void setUniqueClicks(long uniqueClicks) {
		this.uniqueClicks = uniqueClicks;
	}
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public Calendar getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(Calendar transactionTime) {
		this.transactionTime = transactionTime;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public DRSMSChannelSent(){
    	
    }
    
    public DRSMSChannelSent(String status,String mobile,Calendar sentDate,long userId,Long drJsonObjId) {
        this.status = status;
        this.mobile = mobile;
        this.sentDate = sentDate;
        this.userId = userId;
        this.drJsonObjId= drJsonObjId;
    }
    

}


