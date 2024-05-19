package org.mq.optculture.model.DR.wooCommerce;

import org.mq.optculture.model.DR.DRHead;
import org.mq.optculture.model.DR.DROptCultureDetails;

public class WooCommerceDRRequest {
	
	private DRHead Head;
	private WooCommerceDRBody Body;
	private DROptCultureDetails OptcultureDetails;
	
	public WooCommerceDRBody getBody() {
		return Body;
	}
	public void setBody(WooCommerceDRBody body) {
		Body = body;
	}
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
	
}
