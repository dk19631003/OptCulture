package org.mq.marketer.campaign.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SMSCampaignUrls {
	
	private Long id;
	private Long crId;
	private String originalUrl;
	private String shortUrl;
	private String typeOfUrl;
	
	
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
	
	
	
}
