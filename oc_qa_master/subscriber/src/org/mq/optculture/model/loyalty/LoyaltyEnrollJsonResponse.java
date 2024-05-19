package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseResponseObject;

public class LoyaltyEnrollJsonResponse extends BaseResponseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8862497093266456187L;
	private LoyaltyEnrollResponseObject ENROLLMENTRESPONSE;

	public LoyaltyEnrollResponseObject getENROLLMENTRESPONSE() {
		return ENROLLMENTRESPONSE;
	}

	public void setENROLLMENTRESPONSE(LoyaltyEnrollResponseObject eNROLLMENTRESPONSE) {
		ENROLLMENTRESPONSE = eNROLLMENTRESPONSE;
	}

}
