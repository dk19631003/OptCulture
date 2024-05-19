package org.mq.optculture.model.DR.shopify;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.DR.DRHead;
import org.mq.optculture.model.DR.DROptCultureDetails;

public class ShopifyBasedDRRequest extends BaseRequestObject {
	
	private DRHead Head;
	private ShopifyDRBody Body;
	private DROptCultureDetails OptcultureDetails;
	
	public ShopifyDRBody getBody() {
		return Body;
	}
	public void setBody(ShopifyDRBody body) {
		Body = body;
	}
	public DROptCultureDetails getOptcultureDetails() {
		return OptcultureDetails;
	}
	public void setOptcultureDetails(DROptCultureDetails optcultureDetails) {
		this.OptcultureDetails = optcultureDetails;
	}
	public DRHead getHead() {
		return Head;
	}
	public void setHead(DRHead head) {
		Head = head;
	}

}
