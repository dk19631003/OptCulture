package org.mq.marketer.campaign.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PushNotificationIos {
	
	private String[] registration_ids;
	private PushNotificationData data;
	private IosNotification notification;
	public String[] getRegistration_ids() {
		return registration_ids;
	}
	
	
	public void setRegistration_ids(String[] registration_ids) {
		this.registration_ids = registration_ids;
	}
	public PushNotificationData getData() {
		return data;
	}
	public void setData(PushNotificationData data) {
		this.data = data;
	}


	public IosNotification getNotification() {
		return notification;
	}


	public void setNotification(IosNotification notification) {
		this.notification = notification;
	}
	
	
	
}
