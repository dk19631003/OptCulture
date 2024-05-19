package org.mq.optculture.model.DR;

import org.mq.optculture.model.BaseRequestObject;

public class DigitalReceipt extends BaseRequestObject{
	
private DRHead Head;
private DRBody Body;
private DROptCultureDetails OptcultureDetails;

public DRHead getHead() {
	return Head;
}
public void setHead(DRHead head) {
	Head = head;
}
public DRBody getBody() {
	return Body;
}
public void setBody(DRBody body) {
	Body = body;
}
public DROptCultureDetails getOptcultureDetails() {
	return OptcultureDetails;
}
public void setOptcultureDetails(DROptCultureDetails optcultureDetails) {
	OptcultureDetails = optcultureDetails;
}


	
	
}
