package org.mq.optculture.model.PushNotification;

import java.util.List;

import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.magento.HeaderInfo;
import org.mq.optculture.model.magento.StatusInfo;

public class PushNotificationResponse extends BaseResponseObject{

	private List<PushNotificationInfo> pushNotificationInfo;
	private StatusInfo statusInfo;
	private HeaderInfo headerInfo;
	private String unreadCount;
	
	public PushNotificationResponse() {
		
	}
	
	public List<PushNotificationInfo> getPushNotificationInfo() {
		return pushNotificationInfo;
	}


	public void setPushNotificationInfo(List<PushNotificationInfo> pushNotificationInfo) {
		this.pushNotificationInfo = pushNotificationInfo;
	}


	public StatusInfo getStatusInfo() {
		return statusInfo;
	}


	public void setStatusInfo(StatusInfo statusInfo) {
		this.statusInfo = statusInfo;
	}


	public HeaderInfo getHeaderInfo() {
		return headerInfo;
	}


	public void setHeaderInfo(HeaderInfo headerInfo) {
		this.headerInfo = headerInfo;
	}

	public String getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(String unreadCount) {
		this.unreadCount = unreadCount;
	}
	
}
