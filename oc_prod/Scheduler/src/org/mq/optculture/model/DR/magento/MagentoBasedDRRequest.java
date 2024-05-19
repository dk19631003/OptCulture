package org.mq.optculture.model.DR.magento;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.DR.DRHead;
import org.mq.optculture.model.DR.DROptCultureDetails;

public class MagentoBasedDRRequest extends BaseRequestObject {
	
	private DRHead Head;
	private MagentoDRBody Body;
	private DROptCultureDetails OptcultureDetails;
	public DRHead getHead() {
		return Head;
	}
	public void setHead(DRHead head) {
		Head = head;
	}
	public MagentoDRBody getBody() {
		return Body;
	}
	public void setBody(MagentoDRBody body) {
		Body = body;
	}
	public DROptCultureDetails getOptcultureDetails() {
		return OptcultureDetails;
	}
	public void setOptcultureDetails(DROptCultureDetails optcultureDetails) {
		this.OptcultureDetails = optcultureDetails;
	}

}
