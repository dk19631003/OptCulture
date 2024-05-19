package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class FarwardToFriend implements java.io.Serializable{
	private Long farwardFriendId;
	private Long contactId;
	private long userId;
	private Long crId;
	private String email;
	private String referer;
	private Long sentId;
	private String custMsg;
	private String toEmailId;
	private String toFullName;
	
	private Calendar sentDate;
	private int opens;
	private int clicks;
	
	private String captcha;
	private String status;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public FarwardToFriend(){
		
	}

	public Long getFarwardFriendId() {
		return farwardFriendId;
	}

	public void setFarwardFriendId(Long farwardFriendId) {
		this.farwardFriendId = farwardFriendId;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Long getCrId() {
		return crId;
	}

	public void setCrId(Long crId) {
		this.crId = crId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public Long getSentId() {
		return sentId;
	}

	public void setSentId(Long sentId) {
		this.sentId = sentId;
	}

	public String getCustMsg() {
		return custMsg;
	}

	public void setCustMsg(String custMsg) {
		this.custMsg = custMsg;
	}

	public String getToEmailId() {
		return toEmailId;
	}

	public void setToEmailId(String toEmailId) {
		this.toEmailId = toEmailId;
	}

	public String getToFullName() {
		return toFullName;
	}

	public void setToFullName(String toFullName) {
		this.toFullName = toFullName;
	}

	public Calendar getSentDate() {
		return sentDate;
	}

	public void setSentDate(Calendar sentDate) {
		this.sentDate = sentDate;
	}

	public int getOpens() {
		return opens;
	}

	public void setOpens(int opens) {
		this.opens = opens;
	}

	public int getClicks() {
		return clicks;
	}

	public void setClicks(int clicks) {
		this.clicks = clicks;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	

}
