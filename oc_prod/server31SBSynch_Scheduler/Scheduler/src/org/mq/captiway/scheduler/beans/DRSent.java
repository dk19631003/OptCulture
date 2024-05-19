package org.mq.captiway.scheduler.beans;

import java.util.Calendar;


public class DRSent {
	
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
    private long drJsonObjId;
    private String templateName;
    
    private long uniqueOpens;
    private long uniqueClicks;
    
    // Added after DR ReSend 
    
    private String htmlStr;
    private String docSid;
    private int sentCount;
   
    public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public long getDrJsonObjId() {
		return drJsonObjId;
	}

	public void setDrJsonObjId(long drJsonObjId) {
		this.drJsonObjId = drJsonObjId;
	}

	public DRSent(){
    	
    }
    
    public DRSent(String subject,String message, String status,String emailId,Calendar sentDate,long userId,long drJsonObjId,String templateName) {
    	this.subject = subject;
    	this.message = message;
        this.status = status;
        this.emailId = emailId;
        this.sentDate = sentDate;
        this.userId = userId;
        this.drJsonObjId= drJsonObjId;
        this.templateName = templateName;
    }
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
	

}
