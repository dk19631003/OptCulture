package org.mq.marketer.campaign.beans;

public class AutosmsSenturlShortcode implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long shortCodeId;
	private String autoSmsQueueSentId;
	private String generatedShortCode;
	private String originalShortCode;
	
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
	public String getAutoSmsQueueSentId() {
		return autoSmsQueueSentId;
	}
	public void setAutoSmsQueueSentId(String autoSmsQueueSentId) {
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
	
	
	
}
