package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseResponseObject;

public class LoyaltyInquiryJsonResponse extends BaseResponseObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 672362199454878539L;
	private LoyaltyInquiryResponseObject LOYALTYINQUIRYRESPONSE;

	public LoyaltyInquiryResponseObject getLOYALTYINQUIRYRESPONSE() {
		return LOYALTYINQUIRYRESPONSE;
	}

	public void setLOYALTYINQUIRYRESPONSE(
			LoyaltyInquiryResponseObject lOYALTYINQUIRYRESPONSE) {
		LOYALTYINQUIRYRESPONSE = lOYALTYINQUIRYRESPONSE;
	}
	
}
