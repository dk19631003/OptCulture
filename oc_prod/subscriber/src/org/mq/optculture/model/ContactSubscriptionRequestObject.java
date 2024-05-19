package org.mq.optculture.model;

import java.util.HashMap;

import org.mq.marketer.campaign.beans.Contacts;

public class ContactSubscriptionRequestObject extends BaseRequestObject{ 
	
	private String formId;
	private Contacts contact;
	private HashMap<String, String> formValuesMap;
	
	
	public String getFormId() {
		return formId;
	}
	
	public void setFormId(String formId) {
		this.formId = formId;
	}
	
	
	public Contacts getContact() {
		return contact;
	}
	
	public void setContact(Contacts contact) {
		this.contact = contact;
	}

	public HashMap<String, String> getFormValuesMap() {
		return formValuesMap;
	}

	public void setFormValuesMap(HashMap<String, String> formValuesMap) {
		this.formValuesMap = formValuesMap;
	}
}
