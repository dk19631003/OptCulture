package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.TimeZone;

import org.mq.marketer.campaign.custom.MyCalendar;

public class WACampaignsSchedule implements java.io.Serializable,Comparable {
	
	
	
	 private Long waCsId;
     private Long waCampaignId;
     private Calendar scheduledDate;
     private Long crId;
     private byte status;
     private Long userId;
     
     public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public WACampaignsSchedule(){
    	 
     }
     
     
     public WACampaignsSchedule(Calendar scheduledDate, Long userId){
    	 this.scheduledDate = scheduledDate;
    	 this.userId = userId;
    	 
    	 
     }
     
     public WACampaignsSchedule( Calendar scheduledDate, Long waCampaignId,
     		byte status, Long userId) {
     	
     	
     	this.scheduledDate = scheduledDate;
     	this.waCampaignId = waCampaignId;
     	this.status = status;
     	this.userId = userId;
     	
     }
     
	public Long getwaCsId() {
		return waCsId;
	}

	public void setwaCsId(Long waCsId) {
		this.waCsId = waCsId;
	}

	
	public Long getwaCampaignId() {
		return waCampaignId;
	}

	public void setwaCampaignId(Long waCampaignId) {
		this.waCampaignId = waCampaignId;
	}

	
	
	public Calendar getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Calendar scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	
	
	public Long getCrId() {
		return crId;
	}

	public void setCrId(Long crId) {
		this.crId = crId;
	}
	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}
	
	/**
	 * Returns the string representation of Calender in the specified timezone
	 * @param timeZone
	 * @return String value of Calender.
	 */
	public String getDateStrByTimeZone(String format, TimeZone timeZone) {
		
		return MyCalendar.calendarToString(this.scheduledDate, format, timeZone);
	}
	public String getStatusStr() {
		switch(this.status) {
		case 0:
			return "Scheduled";
		case 1:
			return "Sent";
		case 2:
			return "Draft";
		case 3:
			return "Insufficient Credits / Subscription Expired";
		case 4:
			return "Insufficient Promo-codes";
		case 5:
			return "Promotion Expired / Paused";
		case 6:
			return "Expired";
		case 8:
			return "Promotion Deleted";
		case 9:
			return "Schedule Failure";
		case 10:
			return "Partial Failure";
		default:
			return "Draft";
		}
	}


	@Override
	public int compareTo(Object obj) {

		if(obj == null){
			return 1;
		}
		
		if(obj instanceof WACampaignsSchedule){
			WACampaignsSchedule tmpSch = (WACampaignsSchedule)obj;
			return this.scheduledDate.compareTo(tmpSch.scheduledDate);
		}
		else
		return 1;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null) 
			return false;

		if(obj.getClass() != getClass()){
			return false;
		}

		WACampaignsSchedule tempCS = (WACampaignsSchedule)obj;		
		if(this.scheduledDate.compareTo(tempCS.getScheduledDate()) == 0) {
			
		}
		
		return false;

	}
	
	@Override
	public int hashCode() {
		
		return scheduledDate.get(1) + scheduledDate.get(2) + scheduledDate.get(5) +
		scheduledDate.get(10) + scheduledDate.get(12)+  scheduledDate.get(13);		
	}
}
