package org.mq.optculture.model.PushNotification;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.magento.HeaderInfo;
import org.mq.optculture.model.mobileapp.User;


public class PushNotificationRequest extends BaseRequestObject{
	
	private String mobileNumber;
	private String instanceId;
	private int offSet;
	private HeaderInfo headerInfo;
	private User user;
	private String sentId;
	private Boolean notificationRead;
	
	
	public PushNotificationRequest(String mobileNumber,String instanceId,int offSet) {
		this.mobileNumber = mobileNumber;
		this.instanceId = instanceId;
		this.offSet = offSet;
	}


	public String getMobileNumber() {
		return mobileNumber;
	}


	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}


	public String getInstanceId() {
		return instanceId;
	}


	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}


	public int getOffSet() {
		return offSet;
	}


	public void setOffSet(int offSet) {
		this.offSet = offSet;
	}


	public HeaderInfo getHeaderInfo() {
		return headerInfo;
	}


	public void setHeaderInfo(HeaderInfo headerInfo) {
		this.headerInfo = headerInfo;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public String getSentId() {
		return sentId;
	}


	public void setSentId(String sentId) {
		this.sentId = sentId;
	}


	public Boolean getNotificationRead() {
		return notificationRead;
	}


	public void setNotificationRead(Boolean notificationRead) {
		this.notificationRead = notificationRead;
	}
	
}
