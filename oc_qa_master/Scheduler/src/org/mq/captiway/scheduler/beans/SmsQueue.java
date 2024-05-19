/**
 * 
 */
package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

/**
 * @author vinod.bokare
 *
 */
public class SmsQueue implements java.io.Serializable{

	/**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = 520722215748770278L;

	/**
	 * Default Constructor
	 */
	public SmsQueue() {
		// TODO Auto-generated constructor stub
	}

    private long id; 
    private String msgType;
    private String status;
    private String toMobilePhone;
    private String message;
    private Calendar sentDate;

    private Users user;
   // private SMSCampaigns smsCampaigns;
    private Long contactId;
    private String senderId;
    
    /**
     * For Future Purpose
     */
    private String msgId;
    private String dlrStatus;
    
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @return the msgType
	 */
	public String getMsgType() {
		return msgType;
	}
	/**
	 * @param msgType the msgType to set
	 */
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the toMobilePhone
	 */
	public String getToMobilePhone() {
		return toMobilePhone;
	}
	/**
	 * @param toMobilePhone the toMobilePhone to set
	 */
	public void setToMobilePhone(String toMobilePhone) {
		this.toMobilePhone = toMobilePhone;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * @return the sentDate
	 */
	public Calendar getSentDate() {
		return sentDate;
	}
	/**
	 * @param sentDate the sentDate to set
	 */
	public void setSentDate(Calendar sentDate) {
		this.sentDate = sentDate;
	}
	/**
	 * @return the user
	 */
	public Users getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(Users user) {
		this.user = user;
	}
	/**
	 * @return the contactId
	 */
	public Long getContactId() {
		return contactId;
	}
	/**
	 * @param contactId the contactId to set
	 */
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	/**
	 * @return the senderId
	 */
	public String getSenderId() {
		return senderId;
	}
	/**
	 * @param senderId the senderId to set
	 */
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	/**
	 * @return the smsCampaigns
	 *//*
	public SMSCampaigns getSmsCampaigns() {
		return smsCampaigns;
	}
	*//**
	 * @param smsCampaigns the smsCampaigns to set
	 *//*
	public void setSmsCampaigns(SMSCampaigns smsCampaigns) {
		this.smsCampaigns = smsCampaigns;
	}*/
	/**
	 * @return the msgId
	 */
	public String getMsgId() {
		return msgId;
	}
	/**
	 * @param msgId the msgId to set
	 */
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	/**
	 * @return the dlrStatus
	 */
	public String getDlrStatus() {
		return dlrStatus;
	}
	/**
	 * @param dlrStatus the dlrStatus to set
	 */
	public void setDlrStatus(String dlrStatus) {
		this.dlrStatus = dlrStatus;
	} 
	
}
