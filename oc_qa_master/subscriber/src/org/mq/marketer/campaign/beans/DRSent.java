package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class DRSent implements java.io.Serializable{
	
	private long id;
	private long userId;
	//private long templateId;
	private Long contactId;
	private String emailId;
	private String status;
	private Calendar sentDate;
	private String phValStr;
	private int opens;
    private int clicks;
    private int bounced;
    private int spam;
    private String subject;
    private String message;
    private Long drJsonObjId;
    private String templateName;
    
     private Calendar maxSentDate;
     
     
    // Added after DR ReSend 
     
    private String htmlStr;
    private String docSid;
    private int sentCount;
    private String storeNumber;
private String sbsNumber;
    
    public String getSbsNumber() {
		return sbsNumber;
	}

	public void setSbsNumber(String sbsNumber) {
		this.sbsNumber = sbsNumber;
	}

    
    private Long myTemplateId;
    
    
    private Long zoneId;
    
    public Long getZoneId() {
		return zoneId;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	public Long getMyTemplateId() {
		return myTemplateId;
	}

	public void setMyTemplateId(Long myTemplateId) {
		this.myTemplateId = myTemplateId;
	}

	public Calendar getMaxSentDate() {
		return maxSentDate;
	}

	public void setMaxSentDate(Calendar maxSentDate) {
		this.maxSentDate = maxSentDate;
	}

	 private Long totalSentCount;
    public Long getTotalSentCount() {
		return totalSentCount;
	}

	public void setTotalSentCount(Long totalSentCount) {
		this.totalSentCount = totalSentCount;
	}

	private long uniqueOpens;
    private long uniqueClicks;
/*    
   public long getSentCount() {
		return sentCount;
	}

	public void setSentCount(long sentCount) {
		this.sentCount = sentCount;
	}*/

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

   
    public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Long getDrJsonObjId() {
		return drJsonObjId;
	}

	public void setDrJsonObjId(Long drJsonObjId) {
		this.drJsonObjId = drJsonObjId;
	}

	public DRSent(){
    	
    }
    
    public DRSent(String subject,String phValStr, String status,String emailId,Calendar sentDate,long userId,Long drJsonObjId,String templateName) {
    	this.subject = subject;
    	this.phValStr = phValStr;
        this.status = status;
        this.emailId = emailId;
        this.sentDate = sentDate;
        this.userId = userId;
        this.drJsonObjId= drJsonObjId;
        this.templateName = templateName;
    }
    
    
    public DRSent(String status,String emailId,Calendar sentDate,long userId,Long drJsonObjId) {
        this.status = status;
        this.emailId = emailId;
        this.sentDate = sentDate;
        this.userId = userId;
        this.drJsonObjId= drJsonObjId;
    }
    
    
    
    
   /* public DRSent(Long id, String status, String emailId, Calendar sentDate, long uniqueOpens, long uniqueClicks, long sentCount) {
    	
    	this.id = id;
    	this.uniqueClicks = uniqueClicks;
    	this.uniqueOpens = uniqueOpens;
    	this.sentCount = sentCount;
    	this.status = status;
        this.emailId = emailId;
        this.sentDate = sentDate;
    }*/
    
    
   /* public DRSent(String subject,long templateId, String status,String emailId,Calendar sentDate,long userId) {
    	this.subject = subject;
    	this.templateId = templateId;
        this.status = status;
        this.emailId = emailId;
        this.sentDate = sentDate;
        this.userId = userId;
    }*/
	
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
	/*public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}*/
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
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
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
	public String getPhValStr() {
		return phValStr;
	}
	public void setPhValStr(String phValStr) {
		this.phValStr = phValStr;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getBounced() {
		return bounced;
	}

	public void setBounced(int bounced) {
		this.bounced = bounced;
	}

	public int getSpam() {
		return spam;
	}

	public void setSpam(int spam) {
		this.spam = spam;
	}

	public String getHtmlStr() {
		return htmlStr;
	}

	public void setHtmlStr(String htmlStr) {
		this.htmlStr = htmlStr;
	}

	public String getDocSid() {
		return docSid;
	}

	public void setDocSid(String docSid) {
		this.docSid = docSid;
	}

	public int getSentCount() {
		return sentCount;
	}

	public void setSentCount(int sentCount) {
		this.sentCount = sentCount;
	}

	public String getStoreNumber() {
		return storeNumber;
	}

	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}
	

}
