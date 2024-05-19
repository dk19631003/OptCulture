package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class SMSClicks implements java.io.Serializable{

	
	private Long clickId;
	private SMSCampaignSent sentId;
    private String clickUrl;
    private Calendar clickDate;
    private Long smsCampUrlId;

    
    
    public SMSClicks() {
    	
    }
	
    public SMSClicks(SMSCampaignSent sentId) {
    	
        this.sentId = sentId;
    }
    
    public SMSClicks(SMSCampaignSent sentId, String clickUrl, Calendar clickDate) {
    	
       this.sentId = sentId;
       this.clickUrl = clickUrl;
       this.clickDate = clickDate;
    }
    
    
    
    public Long getClickId() {
		return clickId;
	}
	public void setClickId(Long clickId) {
		this.clickId = clickId;
	}
	public SMSCampaignSent getSentId() {
		return sentId;
	}
	public void setSentId(SMSCampaignSent sentId) {
		this.sentId = sentId;
	}
	public String getClickUrl() {
		return clickUrl;
	}
	public void setClickUrl(String clickUrl) {
		this.clickUrl = clickUrl;
	}
	public Calendar getClickDate() {
		return clickDate;
	}
	public void setClickDate(Calendar clickDate) {
		this.clickDate = clickDate;
	}

	public Long getSmsCampUrlId() {
		return smsCampUrlId;
	}

	public void setSmsCampUrlId(Long smsCampUrlId) {
		this.smsCampUrlId = smsCampUrlId;
	}
    
    
    
    
    
	
}
