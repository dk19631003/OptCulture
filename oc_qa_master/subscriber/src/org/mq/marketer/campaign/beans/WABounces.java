package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class WABounces implements java.io.Serializable{

	
	
	private Long bounceId;
    private WACampaignSent sentId;
    
    private String category;
    private String message;
    private Calendar bouncedDate;
    private Long crId;
private String mobile;
    
    
    
    public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

    
    
    public WABounces() {
    }
	
    public WABounces(WACampaignSent sentId) {
        this.sentId = sentId;
    }
    
    public WABounces(WACampaignSent sentId, String category, String message, Calendar date, Long crId) {
       this.sentId = sentId;
       this.category = category;
       this.message = message;
       this.bouncedDate = date;
       this.crId = crId;
    }
    
    
    
	public Long getBounceId() {
		return bounceId;
	}
	public void setBounceId(Long bounceId) {
		this.bounceId = bounceId;
	}
	public WACampaignSent getSentId() {
		return sentId;
	}
	public void setSentId(WACampaignSent sentId) {
		this.sentId = sentId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Calendar getBouncedDate() {
		return bouncedDate;
	}
	public void setBouncedDate(Calendar bouncedDate) {
		this.bouncedDate = bouncedDate;
	}
	public Long getCrId() {
		return crId;
	}
	public void setCrId(Long crId) {
		this.crId = crId;
	}
    
    
    
    
    

	
}
