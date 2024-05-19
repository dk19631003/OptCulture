package org.mq.captiway.scheduler.beans;

public class NotSentToContacts {

	private Long notSentId;
	private Long crId;
	private Long sentId;
	
	public NotSentToContacts() {}
	
	public NotSentToContacts(Long crId, Long sentId) {
		
		this.crId = crId;
		this.sentId = sentId;
		
	}//NotSentToContacts
	
	public Long getNotSentId() {
		return notSentId;
	}
	public void setNotSentId(Long notSentId) {
		this.notSentId = notSentId;
	}
	public Long getCrId() {
		return crId;
	}
	public void setCrId(Long crId) {
		this.crId = crId;
	}
	public Long getSentId() {
		return sentId;
	}
	public void setSentId(Long sentId) {
		this.sentId = sentId;
	}
	
	
	
	
	
	
}//NotSentToContacts
