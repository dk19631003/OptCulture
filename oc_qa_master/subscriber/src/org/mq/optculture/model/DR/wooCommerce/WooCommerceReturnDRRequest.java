package org.mq.optculture.model.DR.wooCommerce;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.DR.DRHead;
import org.mq.optculture.model.DR.DROptCultureDetails;

public class WooCommerceReturnDRRequest extends BaseRequestObject {
	private DRHead Head;
	private WooCommerceReturnDRBody Body;
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
	public WooCommerceReturnDRBody getBody() {
		return Body;
	}
	public void setBody(WooCommerceReturnDRBody body) {
		Body = body;
	}
}
