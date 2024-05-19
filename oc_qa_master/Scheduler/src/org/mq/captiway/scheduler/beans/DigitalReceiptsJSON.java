package org.mq.captiway.scheduler.beans;

import java.io.Serializable;
import java.util.Calendar;

public class DigitalReceiptsJSON implements Serializable{
	
	private Long drjsonId;
	private String jsonStr;
	private String status;
	private Long userId;
	private Calendar createdDate;
	private String mode;
	private int retryForLtyExtraction;
	private String source;
	
	public int getRetryForLtyExtraction() {
		return retryForLtyExtraction;
	}


	public void setRetryForLtyExtraction(int retryForLtyExtraction) {
		this.retryForLtyExtraction = retryForLtyExtraction;
	}


	public DigitalReceiptsJSON(){}
	
	
	public Long getDrjsonId() {
		return drjsonId;
	}
	public void setDrjsonId(Long drjsonId) {
		this.drjsonId = drjsonId;
	}
	public String getJsonStr() {
		return jsonStr;
	}
	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public Calendar getCreatedDate() {
		return createdDate;
	}


	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	
	public String getMode() {
		return mode;
	}


	public void setMode(String mode) {
		this.mode = mode;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}
	

}
