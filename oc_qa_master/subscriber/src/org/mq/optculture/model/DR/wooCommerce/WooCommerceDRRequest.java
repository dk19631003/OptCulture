package org.mq.optculture.model.DR.wooCommerce;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.DR.DRHead;
import org.mq.optculture.model.DR.DROptCultureDetails;

public class WooCommerceDRRequest extends BaseRequestObject {
	
	private DRHead Head;
	private WooCommerceDRBody Body;
	private DROptCultureDetails OptcultureDetails;
	
	public DRHead getHead() {
		return Head;
	}
	public void setHead(DRHead head) {
		Head = head;
	}
	public DROptCultureDetails getOptcultureDetails() {
		return OptcultureDetails;
	}
	public void setOptcultureDetails(DROptCultureDetails optcultureDetails) {
		OptcultureDetails = optcultureDetails;
	}
	public WooCommerceDRBody getBody() {
		return Body;
	}
	public void setBody(WooCommerceDRBody body) {
		Body = body;
	}
	
}
