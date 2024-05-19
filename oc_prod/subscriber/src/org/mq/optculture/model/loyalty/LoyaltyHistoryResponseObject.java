package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseResponseObject;

public class LoyaltyHistoryResponseObject extends BaseResponseObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5244271853400515308L;
	private LoyaltyHistoryResponse LOYALTYDATARESPONSE;

	public LoyaltyHistoryResponse getLOYALTYDATARESPONSE() {
		return LOYALTYDATARESPONSE;
	}

	public void setLOYALTYDATARESPONSE(LoyaltyHistoryResponse lOYALTYDATARESPONSE) {
		LOYALTYDATARESPONSE = lOYALTYDATARESPONSE;
	}
}
