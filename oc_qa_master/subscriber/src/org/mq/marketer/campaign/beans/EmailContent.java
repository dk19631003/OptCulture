package org.mq.marketer.campaign.beans;

public class EmailContent implements java.io.Serializable{
	
	private Long id;
	private String name;
	private String htmlContent;
	private String textContent;
	
	private Long campaignId;
	
	public EmailContent(){
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	
	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public Long getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
