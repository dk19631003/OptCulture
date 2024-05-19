package org.mq.optculture.model.updatecontacts;

import javax.xml.bind.annotation.XmlElement;

public class Header {
private String requestId;
private String requestDate;
private String contactSource;//looks like there is a gap
private String contactList;
private String sourceType;

public String getRequestId() {
	return requestId;
}

@XmlElement(name = "requestId")
public void setRequestId(String requestId) {
	this.requestId = requestId;
}
public String getRequestDate() {
	return requestDate;
}
@XmlElement(name = "requestDate")
public void setRequestDate(String requestDate) {
	this.requestDate = requestDate;
}

public String getContactSource() {
	return contactSource;
}

@XmlElement(name = "contactSource")
public void setContactSource(String contactSource) {
	this.contactSource = contactSource;
}

public String getContactList() {
	return contactList;
}

@XmlElement(name = "contactList")
public void setContactList(String contactList) {
	this.contactList = contactList;
}

public String getSourceType() {
	return sourceType;
}

public void setSourceType(String sourceType) {
	this.sourceType = sourceType;
}

}
