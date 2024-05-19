package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseResponseObject;

public class LoyaltyDataResponseObject extends BaseResponseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5244271853400515308L;
	private LoyaltyDataResponse LOYALTYDATARESPONSE;

	public LoyaltyDataResponse getLOYALTYDATARESPONSE() {
		return LOYALTYDATARESPONSE;
	}

	public void setLOYALTYDATARESPONSE(LoyaltyDataResponse lOYALTYDATARESPONSE) {
		LOYALTYDATARESPONSE = lOYALTYDATARESPONSE;
	}
}
