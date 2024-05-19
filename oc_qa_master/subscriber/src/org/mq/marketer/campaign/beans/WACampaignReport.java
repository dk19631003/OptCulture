package org.mq.marketer.campaign.beans;

import java.util.Calendar;


public class WACampaignReport implements java.io.Serializable{

	private Long waCrId;
	private String waCampaignName;
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
	private Users user;
	private String status;
	private String sourceType;
	private long suppressedCount;

	private int preferenceCount;
	private int creditsCount;

	public int getPreferenceCount() {
		return preferenceCount;
	}

	public void setPreferenceCount(int preferenceCount) {
		this.preferenceCount = preferenceCount;
	}

	public int getCreditsCount() {
		return creditsCount;
	}

	public void setCreditsCount(int creditsCount) {
		this.creditsCount = creditsCount;
	}

	public WACampaignReport() {
	}

	public WACampaignReport(String waCampaignName,Calendar sentDate, long sent, int opens, int clicks, int unsubscribes, int bounces,String status){
		this.waCampaignName = waCampaignName;

		this.sent = sent;
		this.sentDate = sentDate;
		this.opens = opens;
		this.clicks = clicks;
		this.unsubscribes = unsubscribes;
		this.bounces = bounces;

		this.status = status;
	}

	public WACampaignReport(Users user,String waCampaignName,String content, 
			Calendar sentDate, long sent, int opens, int clicks, int unsubscribes, 
			int bounces,String status) {

		this.user = user;
		this.waCampaignName = waCampaignName;

		this.content = content;
		this.sent = sent;
		this.sentDate = sentDate;
		this.opens = opens;
		this.clicks = clicks;
		this.unsubscribes = unsubscribes;
		this.bounces = bounces;
		this.status = status;
	}

	public WACampaignReport(Users user,String waCampaignName,String content, 
			Calendar sentDate, long sent, int opens, int clicks, int unsubscribes, 
			int bounces,String status, String sourceType) {

		this.user = user;
		this.waCampaignName = waCampaignName;

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

	public long getSent() {
		return sent;
	}

	public void setSent(long sent) {
		this.sent = sent;
	}

	public Long getWaCrId() {
		return waCrId;
	}

	public void setWaCrId(Long waCrId) {
		this.waCrId = waCrId;
	}

	public String getWaCampaignName() {
		return waCampaignName;
	}

	public void setWaCampaignName(String waCampaignName) {
		this.waCampaignName = waCampaignName;
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

