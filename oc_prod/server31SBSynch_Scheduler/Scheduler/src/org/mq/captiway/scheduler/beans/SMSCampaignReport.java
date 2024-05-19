package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.Set;

public class SMSCampaignReport implements java.io.Serializable{

	private Long smsCrId;
    private String smsCampaignName;
    //private String subject;
    private String content;
    private Calendar sentDate;
    private long configured;
   

	public long getConfigured() {
		return configured;
	}

	public void setConfigured(long configured) {
		this.configured = configured;
	}

	private long sent;
    private int opens;
    private int clicks;
    private int unsubscribes;
    private int bounces;
    //private Set<String> urls;
    //private Set<String> domains;
    private Users user;
	private String status;
	private String sourceType;
	private long suppressedCount;
	
	private int preferenceCount;
	
	
	public int getPreferenceCount() {
		return preferenceCount;
	}

	public void setPreferenceCount(int preferenceCount) {
		this.preferenceCount = preferenceCount;
	}
	
	public SMSCampaignReport() {
	}

	public SMSCampaignReport(String smsCampaignName,Calendar sentDate, long sent, int opens, int clicks, int unsubscribes, int bounces,String status){
	    this.smsCampaignName = smsCampaignName;
	    
	    this.sent = sent;
	    this.sentDate = sentDate;
	    this.opens = opens;
	    this.clicks = clicks;
	    this.unsubscribes = unsubscribes;
	    this.bounces = bounces;
	    
	    this.status = status;
	}

	public SMSCampaignReport(Users user,String smsCampaignName,String content, 
			Calendar sentDate, long sent, int opens, int clicks, int unsubscribes, 
			int bounces,String status) {
		
	    this.user = user;
		this.smsCampaignName = smsCampaignName;
	    
	    this.content = content;
	    this.sent = sent;
	    this.sentDate = sentDate;
	    this.opens = opens;
	    this.clicks = clicks;
	    this.unsubscribes = unsubscribes;
	    this.bounces = bounces;
	    this.status = status;
	}
	
	public SMSCampaignReport(Users user,String smsCampaignName,String content, 
			Calendar sentDate, long sent, int opens, int clicks, int unsubscribes, 
			int bounces,String status, String sourceType) {
		
	    this.user = user;
		this.smsCampaignName = smsCampaignName;
	    
	    this.content = content;
	    this.sent = sent;
	    this.sentDate = sentDate;
	    this.opens = opens;
	    this.clicks = clicks;
	    this.unsubscribes = unsubscribes;
	    this.bounces = bounces;
	    this.status = status;
	    this.sourceType = sourceType;
	}
	
	
	public Long getSmsCrId() {
		return smsCrId;
	}

	public void setSmsCrId(Long smsCrId) {
		this.smsCrId = smsCrId;
	}

	public long getSent() {
		return sent;
	}

	public void setSent(long sent) {
		this.sent = sent;
	}

	public String getSmsCampaignName() {
		return smsCampaignName;
	}

	public void setSmsCampaignName(String smsCampaignName) {
		this.smsCampaignName = smsCampaignName;
	}

	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Calendar getSentDate() {
		return sentDate;
	}

	public void setSentDate(Calendar sentDate) {
		this.sentDate = sentDate;
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

	public int getUnsubscribes() {
		return unsubscribes;
	}

	public void setUnsubscribes(int unsubscribes) {
		this.unsubscribes = unsubscribes;
	}

	public int getBounces() {
		return bounces;
	}

	public void setBounces(int bounces) {
		this.bounces = bounces;
	}

	
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	    
    public long getSuppressedCount() {
		return suppressedCount;
	}

	public void setSuppressedCount(long suppressedCount) {
		this.suppressedCount = suppressedCount;
	}
	
}
