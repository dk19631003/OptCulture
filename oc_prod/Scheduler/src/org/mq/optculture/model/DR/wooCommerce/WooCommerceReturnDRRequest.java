package org.mq.optculture.model.DR.wooCommerce;

import org.mq.optculture.model.DR.DRHead;
import org.mq.optculture.model.DR.DROptCultureDetails;

public class WooCommerceReturnDRRequest {
	private DRHead Head;
	private WooCommerceReturnDRBody Body;
	private DROptCultureDetails OptcultureDetails;
	public synchronized DRHead getHead() {
		return Head;
	}
	public synchronized void setHead(DRHead head) {
		Head = head;
	}
	public synchronized WooCommerceReturnDRBody getBody() {
		return Body;
	}
	public synchronized void setBody(WooCommerceReturnDRBody body) {
		Body = body;
	}
	public synchronized DROptCultureDetails getOptcultureDetails() {
		return OptcultureDetails;
	}
	public synchronized void setOptcultureDetails(DROptCultureDetails optcultureDetails) {
		OptcultureDetails = optcultureDetails;
	}
}
