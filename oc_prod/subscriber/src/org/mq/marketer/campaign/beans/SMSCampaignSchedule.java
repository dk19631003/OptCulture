package org.mq.marketer.campaign.beans;

import java.util.Calendar;
import java.util.TimeZone;

import org.mq.marketer.campaign.custom.MyCalendar;

@SuppressWarnings("serial")
public class SMSCampaignSchedule implements java.io.Serializable,Comparable {
	
	
	
	 private Long smsCsId;
     private Long smsCampaignId;
     private Calendar scheduledDate;
     private Long crId;
     private byte criteria;
     private Long parentId;
     private byte status;
     private byte resendLevel;
     private Long userId;
     
     public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public SMSCampaignSchedule(){
    	 
     }
     
     
     public SMSCampaignSchedule(Calendar scheduledDate,byte criteria, Long userId){
    	 this.scheduledDate = scheduledDate;
    	 this.criteria = criteria;
    	 this.userId = userId;
    	 
    	 
     }
     
    /* public SMSCampaignSchedule(Long smsCsId,Calendar scheduledDate,byte criteria){
    	 this.smsCsId = smsCsId;
    	 this.scheduledDate = scheduledDate;
    	 this.criteria = criteria;
    	 
     }
     */
     
     public SMSCampaignSchedule( Calendar scheduledDate, Long smsCampaignId,
     		byte status, byte criteria, Long userId) {
     	
     	
     	this.scheduledDate = scheduledDate;
     	this.smsCampaignId = smsCampaignId;
     	this.status = status;
     	this.criteria = criteria;
     	this.userId = userId;
     	
     }
     
	public Long getSmsCsId() {
		return smsCsId;
	}

	public void setSmsCsId(Long smsCsId) {
		this.smsCsId = smsCsId;
	}

	
	public Long getSmsCampaignId() {
		return smsCampaignId;
	}

	public void setSmsCampaignId(Long smsCampaignId) {
		this.smsCampaignId = smsCampaignId;
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

	
	
	public byte getCriteria() {
		return criteria;
	}

	public void setCriteria(byte criteria) {
		this.criteria = criteria;
	}

	
	
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	
	
	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	
	public byte getResendLevel() {
		return resendLevel;
	}

	public void setResendLevel(byte resendLevel) {
		this.resendLevel = resendLevel;
	}
	
	/**
	 * Returns the string representation of Calender in the specified timezone
	 * @param timeZone
	 * @return String value of Calender.
	 */
	public String getDateStrByTimeZone(String format, TimeZone timeZone) {
		
		return MyCalendar.calendarToString(this.scheduledDate, format, timeZone);
	}
	/**this method retrieves the status of the smscampaignSchedule obj
	 * 
	 * @return string represeting status of the smscampaignschedule object
	 */
	public String getStatusStr() {
		switch(this.status) {
			case 0:
				return "Active";
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
				return "SMS Keyword Expired / Inactive Keyword";
			case 7:
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
		
		if(obj instanceof SMSCampaignSchedule){
			SMSCampaignSchedule tmpSch = (SMSCampaignSchedule)obj;
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

		SMSCampaignSchedule tempCS = (SMSCampaignSchedule)obj;
		
//		logger.info("~~~~~~~~~~~~~~~~~ csId :"+csId);
//		logger.info("~~~~~~~~~~~~~~~~~ this Parent :"+this.parentId);
//		logger.info("~~~~~~~~~~~~~~~~~ passed object's csId :"+tempCS.getCsId());
//		logger.info("~~~~~~~~~~~~~~~~~ passed object's Parent :"+tempCS.getParentId());
		
		if(this.scheduledDate.compareTo(tempCS.getScheduledDate()) == 0) {
			
//			logger.info("~~~~~~~~~~~~~~~~~ Dates are same-------");
			
			if((this.parentId == tempCS.getSmsCsId())){
				return true;
			}
			else if(((this.parentId == null && tempCS.getParentId() == null)) || 
				(this.parentId != null && tempCS.getParentId() != null && 
				this.parentId.intValue() == tempCS.parentId.intValue())) {
				
				return true;
			}
			
		} // date
		
		return false;

	}
	
	@Override
	public int hashCode() {
		
		return scheduledDate.get(1) + scheduledDate.get(2) + scheduledDate.get(5) +
		scheduledDate.get(10) + scheduledDate.get(12)+  scheduledDate.get(13)+
		scheduledDate.get(14)+( parentId != null ? parentId.intValue():0);
		
	}
}
