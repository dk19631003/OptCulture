package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

public class SMSDeliveryReport implements java.io.Serializable,Comparable {

	private Long smsDlrId;
	private String status;
	private Long smsCampRepId;
	private Calendar reqGeneratedDate;
	private String requestId;
	private Long userSMSTool;
	/*private String isTransactional;
	

	private String userSMSTool;*/
	
	public SMSDeliveryReport() {
		
	}
	
	public SMSDeliveryReport(String requestId, Long smsCampRepId, String status, Calendar  reqGeneratedDate) {
		
		this.requestId = requestId;
		this.smsCampRepId = smsCampRepId;
		this.status = status;
		this.reqGeneratedDate = reqGeneratedDate;
		
	}
	
	
	
	public Long getSmsDlrId() {
		return smsDlrId;
	}
	public void setSmsDlrId(Long smsDlrId) {
		this.smsDlrId = smsDlrId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getSmsCampRepId() {
		return smsCampRepId;
	}
	public void setSmsCampRepId(Long smsCampRepId) {
		this.smsCampRepId = smsCampRepId;
	}
	public Calendar getReqGeneratedDate() {
		return reqGeneratedDate;
	}
	public void setReqGeneratedDate(Calendar reqGeneratedDate) {
		this.reqGeneratedDate = reqGeneratedDate;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	/*public String getIsTransactional() {
		return isTransactional;
	}

	public void setIsTransactional(String isTransactional) {
		this.isTransactional = isTransactional;
	}*/

	/*public String getUserSMSTool() {
		return userSMSTool;
	}

	public void setUserSMSTool(String userSMSTool) {
		this.userSMSTool = userSMSTool;
	}*/
	
	public Long getUserSMSTool() {
		return userSMSTool;
	}

	public void setUserSMSTool(Long userSMSTool) {
		this.userSMSTool = userSMSTool;
	}
	
	@Override
	public int compareTo(Object obj) {
		
		if(obj == null) 
			return 1;
		
		if(obj instanceof SMSDeliveryReport) {
			
			SMSDeliveryReport tempDlr = (SMSDeliveryReport)obj;
			return tempDlr.reqGeneratedDate.compareTo(this.reqGeneratedDate);
		} 
		else {
			return 1;
		}
		
	}
	
	
}
