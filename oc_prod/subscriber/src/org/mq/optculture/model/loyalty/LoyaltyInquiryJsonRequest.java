package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseRequestObject;

public class LoyaltyInquiryJsonRequest extends BaseRequestObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6018593841524439346L;
	private LoyaltyInquiryRequestObject LOYALTYINQUIRYREQ;

	public LoyaltyInquiryRequestObject getLOYALTYINQUIRYREQ() {
		return LOYALTYINQUIRYREQ;
	}

	public void setLOYALTYINQUIRYREQ(LoyaltyInquiryRequestObject lOYALTYINQUIRYREQ) {
		LOYALTYINQUIRYREQ = lOYALTYINQUIRYREQ;
	}
	
}
