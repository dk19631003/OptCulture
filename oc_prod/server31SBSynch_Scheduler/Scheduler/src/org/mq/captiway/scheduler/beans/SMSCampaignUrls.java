package org.mq.captiway.scheduler.beans;

public class SMSCampaignUrls {
	
	private Long id;
	private Long crId;
	private String originalUrl;
	private String shortUrl;
	private String shortCode;
	
	private String typeOfUrl;
	
	public SMSCampaignUrls() {}
	
	public SMSCampaignUrls( Long crId,	 String originalUrl, String shortUrl, String shortCode, String typeOfUrl	) {
		
		this.crId = crId;
		this.originalUrl = originalUrl;
		this.shortUrl = shortUrl;
		this.typeOfUrl = typeOfUrl;
		this.shortCode = shortCode;
		
	}
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCrId() {
		return crId;
	}
	public void setCrId(Long crId) {
		this.crId = crId;
	}
	public String getOriginalUrl() {
		return originalUrl;
	}
	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}
	public String getShortUrl() {
		return shortUrl;
	}
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	public String getTypeOfUrl() {
		return typeOfUrl;
	}
	public void setTypeOfUrl(String typeOfUrl) {
		this.typeOfUrl = typeOfUrl;
	}
	
	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	
	
	
}
