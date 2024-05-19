package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class SMSOpens implements java.io.Serializable {

	private Long openId;
    private SMSCampaignSent sentId;
    private Calendar openDate;

    
    public SMSOpens() {
    }

	
    public SMSOpens(SMSCampaignSent sentId) {
        this.sentId = sentId;
    }
    public SMSOpens(SMSCampaignSent sentId, Calendar openDate) {
       this.sentId = sentId;
       this.openDate = openDate;
    }
    
	
    public Long getOpenId() {
		return openId;
	}


	public void setOpenId(Long openId) {
		this.openId = openId;
	}


	public SMSCampaignSent getSentId() {
		return sentId;
	}


	public void setSentId(SMSCampaignSent sentId) {
		this.sentId = sentId;
	}


	public Calendar getOpenDate() {
		return openDate;
	}


	public void setOpenDate(Calendar openDate) {
		this.openDate = openDate;
	}
	
   
	
}
