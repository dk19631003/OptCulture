package org.mq.captiway.scheduler.beans;

public class AutosmsSenturlShortcode implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long shortCodeId;
	private Long autoSmsQueueSentId;
	private String generatedShortCode;
	private String originalShortCode;
	private String clickedUrl;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getShortCodeId() {
		return shortCodeId;
	}
	public void setShortCodeId(Long shortCodeId) {
		this.shortCodeId = shortCodeId;
	}
	public Long getAutoSmsQueueSentId() {
		return autoSmsQueueSentId;
	}
	public void setAutoSmsQueueSentId(Long autoSmsQueueSentId) {
		this.autoSmsQueueSentId = autoSmsQueueSentId;
	}
	public String getGeneratedShortCode() {
		return generatedShortCode;
	}
	public void setGeneratedShortCode(String generatedShortCode) {
		this.generatedShortCode = generatedShortCode;
	}
	public String getOriginalShortCode() {
		return originalShortCode;
	}
	public void setOriginalShortCode(String originalShortCode) {
		this.originalShortCode = originalShortCode;
	}
	public String getClickedUrl() {
		return clickedUrl;
	}
	public void setClickedUrl(String clickedUrl) {
		this.clickedUrl = clickedUrl;
	}
	
}
