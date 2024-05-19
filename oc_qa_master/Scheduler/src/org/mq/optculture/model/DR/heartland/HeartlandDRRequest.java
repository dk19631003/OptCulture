package org.mq.optculture.model.DR.heartland;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.DR.DRHead;
import org.mq.optculture.model.DR.DROptCultureDetails;


public class HeartlandDRRequest extends BaseRequestObject{
	private DRHead Head;
	private HeartlandDRBody Body;
	private DROptCultureDetails OptcultureDetails;
		
	
	public HeartlandDRBody getBody() {
		return Body;
	}
	
	public void setBody(HeartlandDRBody body) {
		Body = body;
	}

	

	public DROptCultureDetails getOptcultureDetails() {
		return OptcultureDetails;
	}

	public void setOptcultureDetails(DROptCultureDetails optcultureDetails) {
		OptcultureDetails = optcultureDetails;
	}

	public DRHead getHead() {
		return Head;
	}

	public void setHead(DRHead head) {
		Head = head;
	}


}