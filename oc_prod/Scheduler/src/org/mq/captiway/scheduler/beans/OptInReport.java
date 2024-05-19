package org.mq.captiway.scheduler.beans;

public class OptInReport {

	private Long optRepId;
	private String htmlContent;
	private long confirmCount;
	
	
	public OptInReport(){}
	
	public OptInReport(String htmlContent) {
		
		this.htmlContent = htmlContent;
	}
	
	
	public Long getOptRepId() {
		return optRepId;
	}
	public void setOptRepId(Long optRepId) {
		this.optRepId = optRepId;
	}
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	public long getConfirmCount() {
		return confirmCount;
	}
	public void setConfirmCount(long confirmCount) {
		this.confirmCount = confirmCount;
	}
	
}
