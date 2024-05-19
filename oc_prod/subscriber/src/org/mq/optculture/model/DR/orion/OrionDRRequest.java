package org.mq.optculture.model.DR.orion;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.DR.DRHead;
import org.mq.optculture.model.DR.DROptCultureDetails;


public class OrionDRRequest extends BaseRequestObject{

	private DRHead Head;
	private OrionDRBody Body;
	private DROptCultureDetails OptcultureDetails;
	
	public DRHead getHead() {
		return Head;
	}
	public void setHead(DRHead head) {
		Head = head;
	}
	public OrionDRBody getBody() {
		return Body;
	}
	public void setBody(OrionDRBody body) {
		Body = body;
	}
	public DROptCultureDetails getOptcultureDetails() {
		return OptcultureDetails;
	}
	public void setOptcultureDetails(DROptCultureDetails optcultureDetails) {
		OptcultureDetails = optcultureDetails;
	}


}
