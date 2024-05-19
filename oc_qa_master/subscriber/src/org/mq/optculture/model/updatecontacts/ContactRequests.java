package org.mq.optculture.model.updatecontacts;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="contactRequests")
public class ContactRequests {

	private List<ContactRequest> contactRequest;

	public ContactRequests() {
	}

	public List<ContactRequest> getContactRequest() {
		return contactRequest;
	}

	@XmlElement(name="contactRequest")
	public void setContactRequest(List<ContactRequest> contactRequest) {
		this.contactRequest = contactRequest;
	}

}
