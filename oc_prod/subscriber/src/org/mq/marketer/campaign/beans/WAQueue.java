
package org.mq.marketer.campaign.beans;

import java.util.Calendar;

@SuppressWarnings("serial")
public class WAQueue implements java.io.Serializable {

	/**
	 * Generated Serial ID
	 */
	private static final long serialVersionUID = 520722215748770278L;

	/**
	 * Default Constructor
	 */
	public WAQueue() {
		// TODO Auto-generated constructor stub
	}
    private long id; 
    private String msgType;
    private String status;
    private String toMobilePhone;
    private String message;
    private Calendar sentDate;
    private Users user;
    
    private Long contactId;
    private String senderId;
    private String msgId;
    private String dlrStatus;
    
   
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	
	public String getMsgType() {
		return msgType;
	}
	
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getToMobilePhone() {
		return toMobilePhone;
	}
	
	public void setToMobilePhone(String toMobilePhone) {
		this.toMobilePhone = toMobilePhone;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	

	public Calendar getSentDate() {
		return sentDate;
	}

	public void setSentDate(Calendar sentDate) {
		this.sentDate = sentDate;
	}


	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getDlrStatus() {
		return dlrStatus;
	}
	public void setDlrStatus(String dlrStatus) {
		this.dlrStatus = dlrStatus;
	} 

	public static void main(String[] args) {
		String str ="Court48";
		
		String [] arr = str.split("4");
		for(int i=0;i<arr.length;i++)
			System.out.println(arr[i]);
	}
}//EOF
