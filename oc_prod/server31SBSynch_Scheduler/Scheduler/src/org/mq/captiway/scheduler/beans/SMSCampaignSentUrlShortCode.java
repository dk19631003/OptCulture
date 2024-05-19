package org.mq.captiway.scheduler.beans;

public class SMSCampaignSentUrlShortCode {

	private Long id;
	private Long smsCampaignUrlId;
	private Long sentId;
	private String originalShortCode;
	private String generatedShortCode;
	private String Clickedurl;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSmsCampaignUrlId() {
		return smsCampaignUrlId;
	}
	public void setSmsCampaignUrlId(Long smsCampaignUrlId) {
		this.smsCampaignUrlId = smsCampaignUrlId;
	}
	public Long getSentId() {
		return sentId;
	}
	public void setSentId(Long sentId) {
		this.sentId = sentId;
	}
	public String getOriginalShortCode() {
		return originalShortCode;
	}
	public void setOriginalShortCode(String originalShortCode) {
		this.originalShortCode = originalShortCode;
	}
	public String getGeneratedShortCode() {
		return generatedShortCode;
	}
	public void setGeneratedShortCode(String generatedShortCode) {
		this.generatedShortCode = generatedShortCode;
	}
	
	public String getClickedurl() {
		return Clickedurl;
	}
	public void setClickedurl(String clickedurl) {
		Clickedurl = clickedurl;
	}
}
