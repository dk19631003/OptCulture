package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.Set;


public class CampaignReport {
	
	private Long crId;
    private String campaignName;
    private String subject;
    private String content;
    private Calendar sentDate;
    private long sent;
    private int opens;
    private int clicks;
    private int unsubscribes;
    private int bounces;
    private Set<String> urls;
    private Set<String> domains;
    private Users user;
	private String status;
	private String placeHoldersStr;
	private String sourceType;
	private int spams;
	private int suppressed;

	public int getSuppressed() {
	  return suppressed;
	 }

	 public void setSuppressed(int supressed) {
	  this.suppressed = supressed;
	 }
	
	private int preferenceCount;
	
	public int getPreferenceCount() {
		return preferenceCount;
	}

	public void setPreferenceCount(int preferenceCount) {
		this.preferenceCount = preferenceCount;
	}

	
	public int getSpams() {
		return spams;
	}

	public void setSpams(int spams) {
		this.spams = spams;
	}

	 private long configured;
	   

		public long getConfigured() {
			return configured;
		}

		public void setConfigured(long configured) {
			this.configured = configured;
		}
	
	
	
	public CampaignReport() {
	}

	public CampaignReport(String campaignName,String subject, Calendar sentDate, long sent, 
			int opens, int clicks, int unsubscribes, int bounces,
			Set<String> urls, Set<String> domains, String status){
	    this.campaignName = campaignName;
	    this.subject = subject;
	    this.sent = sent;
	    this.sentDate = sentDate;
	    this.opens = opens;
	    this.clicks = clicks;
	    this.unsubscribes = unsubscribes;
	    this.bounces = bounces;
	    this.urls = urls;
	    this.domains = domains;
	    this.status = status;
	}

	public CampaignReport(Users user,String campaignName, String subject,String content, 
			Calendar sentDate, long sent, int opens, int clicks, int unsubscribes, 
			int bounces,String status) {
		
	    this.user = user;
		this.campaignName = campaignName;
	    this.subject = subject;
	    this.content = content;
	    this.sent = sent;
	    this.sentDate = sentDate;
	    this.opens = opens;
	    this.clicks = clicks;
	    this.unsubscribes = unsubscribes;
	    this.bounces = bounces;
	    this.status = status;
	}
	public CampaignReport(Users user,String campaignOrTriggerName, String subject,String content, 
			Calendar sentDate, long sent, int opens, int clicks, int unsubscribes, 
			int bounces,String status,String sourceType) { //constructor overwritten for EventTrigger 
		
	    this.user = user;
		this.campaignName = campaignOrTriggerName;
	    this.subject = subject;
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
	public Long getCrId() {
		return crId;
	}

	public void setCrId(Long crId) {
		this.crId = crId;
	}

	public long getSent() {
		return sent;
	}

	public void setSent(long sent) {
		this.sent = sent;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
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

	public Set<String> getUrls() {
		return urls;
	}
	public void setUrls(Set<String> urls) {
		this.urls = urls;
	}
	public Set<String> getDomains() {
		return domains;
	}
	public void setDomains(Set<String> domains) {
		this.domains = domains;
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

	public String getPlaceHoldersStr() {
		return placeHoldersStr;
	}

	public void setPlaceHoldersStr(String placeHoldersStr) {
		this.placeHoldersStr = placeHoldersStr;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

}
