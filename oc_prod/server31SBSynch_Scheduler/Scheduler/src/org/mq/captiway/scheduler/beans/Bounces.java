package org.mq.captiway.scheduler.beans;
// Generated 30 Nov, 2009 6:24:52 PM by Hibernate Tools 3.2.0.CR1


import java.util.Date;

/**
 * Bounces generated by hbm2java
 */
@SuppressWarnings("serial")
public class Bounces  implements java.io.Serializable {


	private Long bounceId;
    private CampaignSent sentId;
    private String category;
    private String message;
    private Date bouncedDate;
    private Long crId;
    private String statusCode;

    public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

    public Bounces() {
    }
	
    public Bounces(CampaignSent sentId) {
        this.sentId = sentId;
    }
    
    public Bounces(CampaignSent sentId, String category, String message, Date date, Long crId) {
       this.sentId = sentId;
       this.category = category;
       this.message = message;
       this.bouncedDate = date;
       this.crId = crId;
    }
   
    public Long getBounceId() {
        return this.bounceId;
    }
    
    public void setBounceId(Long bounceId) {
        this.bounceId = bounceId;
    }
    public CampaignSent getSentId() {
        return this.sentId;
    }
    
    public void setSentId(CampaignSent sentId) {
        this.sentId = sentId;
    }

    public String getCategory() {
        return this.category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    public Date getBouncedDate() {
        return this.bouncedDate;
    }
    
    public void setBouncedDate(Date bouncedDate) {
        this.bouncedDate = bouncedDate;
    }

	public Long getCrId() {
		return crId;
	}

	public void setCrId(Long crId) {
		this.crId = crId;
	}

}

