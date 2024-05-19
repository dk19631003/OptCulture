package org.mq.captiway.scheduler.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PushNotificationAndroid {
	
	private String[] registration_ids;
	private PushNotificationData notification;
	public String[] getRegistration_ids() {
		return registration_ids;
	}
	public void setRegistration_ids(String[] registration_ids) {
		this.registration_ids = registration_ids;
	}
	public PushNotificationData getNotification() {
		return notification;
	}
	public void setNotification(PushNotificationData notification) {
		this.notification = notification;
	}
	
	
}
