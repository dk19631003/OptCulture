package org.mq.optculture.model;

import javax.xml.bind.annotation.XmlElement;

public class HeaderInfo {
private String requestId;
private String requestDate;
private String source;

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
public String getSource() {
	return source;
}
@XmlElement(name = "source")
public void setSource(String source) {
	this.source = source;
}
//Added for new Fields, will be removed in future.

private String REQUESTID;

public String getREQUESTID() {
	return REQUESTID;
}
@XmlElement(name = "REQUESTID")
public void setREQUESTID(String rEQUESTID) {
	REQUESTID = rEQUESTID;
}


}
