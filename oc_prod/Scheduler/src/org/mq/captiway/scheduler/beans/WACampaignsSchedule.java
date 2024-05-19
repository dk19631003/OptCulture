package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.TimeZone;

import org.mq.captiway.scheduler.utility.MyCalendar;


public class WACampaignsSchedule implements java.io.Serializable,Comparable {

	private Long waCsId;
	private Long waCampaignId;
	private Calendar scheduledDate;
	private Long crId;
	//private byte criteria;
	//private Long parentId;
	private byte status;
	//private byte resendLevel;
	private Long userId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public WACampaignsSchedule(){

	}

	public Long getWaCsId() {
 		return waCsId;
	}

	public void setWaCsId(Long waCsId) {
		this.waCsId = waCsId;
	}

	public Long getWaCampaignId() {
		return waCampaignId;
	}

	public void setWaCampaignId(Long waCampaignId) {
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
	/**this method retrieves the status of the wacampaignSchedule obj
	 * 
	 * @return string represeting status of the wacampaignschedule object
	 */
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
		
		if(obj == null) 
			return 1;
		
		if(obj instanceof WACampaignsSchedule) {
			
			WACampaignsSchedule tempCS = (WACampaignsSchedule)obj;
			return tempCS.scheduledDate.compareTo(this.scheduledDate);
		} 
		else {
			return 1;
		}
		
	}


}
