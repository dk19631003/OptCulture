package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class ShareSocialNetworkLinks implements java.io.Serializable{
	
	private Long id;
	private Long sentId;
	private Long crId;
	private String sourceType;
	private Calendar  sharedTime;
	
	
	public ShareSocialNetworkLinks(){}
	
	public ShareSocialNetworkLinks(Long sentId,Long crId,String sourceType, Calendar sharedTime) {
		this.sentId = sentId;
		this.crId = crId;
		this.sourceType  = sourceType;
		this.sharedTime = sharedTime;
	}
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSentId() {
		return sentId;
	}
	public void setSentId(Long sentId) {
		this.sentId = sentId;
	}
	public Long getCrId() {
		return crId;
	}
	public void setCrId(Long crId) {
		this.crId = crId;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public Calendar getSharedTime() {
		return sharedTime;
	}
	public void setSharedTime(Calendar sharedTime) {
		this.sharedTime = sharedTime;
	}

}
