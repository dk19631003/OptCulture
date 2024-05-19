package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.TimeZone;

import org.mq.captiway.scheduler.utility.MyCalendar;

@SuppressWarnings("serial")
public class NotificationSchedule implements java.io.Serializable,Comparable<Object> {
	
	 private Long notificationCsId;
     private Long notificationId;
     private Long notificationCrId;
     private Calendar scheduledDate;
     private byte criteria;
     private Long parentId;
     private byte status;
     private byte resendLevel;
     private Long userId;
     
     public Long getNotificationCsId() {
		return notificationCsId;
	}


	public void setNotificationCsId(Long notificationCsId) {
		this.notificationCsId = notificationCsId;
	}


	public Long getNotificationId() {
		return notificationId;
	}


	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public NotificationSchedule(){
    	 
     }
     
     
     public NotificationSchedule(Calendar scheduledDate,byte criteria, Long userId){
    	 this.scheduledDate = scheduledDate;
    	 this.criteria = criteria;
    	 this.userId = userId;
    	 
    	 
     }
     
     public NotificationSchedule( Calendar scheduledDate, Long notificationId,
     		byte status, byte criteria, Long userId) {
     	
     	
     	this.scheduledDate = scheduledDate;
     	this.notificationId = notificationId;
     	this.status = status;
     	this.criteria = criteria;
     	this.userId = userId;
     	
     }
     
	public Calendar getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Calendar scheduledDate) {
		this.scheduledDate = scheduledDate;
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
	
	public Long getNotificationCrId() {
		return notificationCrId;
	}


	public void setNotificationCrId(Long notificationCrId) {
		this.notificationCrId = notificationCrId;
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
		
		if(obj instanceof NotificationSchedule){
			NotificationSchedule tmpSch = (NotificationSchedule)obj;
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

		NotificationSchedule tempCS = (NotificationSchedule)obj;
		
//		logger.info("~~~~~~~~~~~~~~~~~ csId :"+csId);
//		logger.info("~~~~~~~~~~~~~~~~~ this Parent :"+this.parentId);
//		logger.info("~~~~~~~~~~~~~~~~~ passed object's csId :"+tempCS.getCsId());
//		logger.info("~~~~~~~~~~~~~~~~~ passed object's Parent :"+tempCS.getParentId());
		
		if(this.scheduledDate.compareTo(tempCS.getScheduledDate()) == 0) {
			
//			logger.info("~~~~~~~~~~~~~~~~~ Dates are same-------");
			
			if((this.parentId == tempCS.getNotificationCsId())){
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
