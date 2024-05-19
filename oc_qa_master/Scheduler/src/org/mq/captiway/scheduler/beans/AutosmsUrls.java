package org.mq.captiway.scheduler.beans;

public class AutosmsUrls implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long autoSmsQueueSentId;
	private String originalUrl;
	private String shortUrl;
	private String typeOfUrl;
	private String shortCode;
	
	public AutosmsUrls() {}
	
	public AutosmsUrls(Long autoSmsQueueSentId, String originalUrl, String shortUrl, String shortCode, String typeOfUrl) {
		this.autoSmsQueueSentId = autoSmsQueueSentId;
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
	public Long getAutoSmsQueueSentId() {
		return autoSmsQueueSentId;
	}
	public void setAutoSmsQueueSentId(Long autoSmsQueueSentId) {
		this.autoSmsQueueSentId = autoSmsQueueSentId;
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
