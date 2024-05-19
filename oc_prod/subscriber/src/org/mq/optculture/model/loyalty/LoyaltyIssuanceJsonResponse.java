package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseResponseObject;

public class LoyaltyIssuanceJsonResponse extends BaseResponseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7076500827349133842L;
	private LoyaltyIssuanceResponseObject LOYALTYISSUANCERESPONSE;

	public LoyaltyIssuanceResponseObject getLOYALTYISSUANCERESPONSE() {
		return LOYALTYISSUANCERESPONSE;
	}

	public void setLOYALTYISSUANCERESPONSE(
			LoyaltyIssuanceResponseObject lOYALTYISSUANCERESPONSE) {
		LOYALTYISSUANCERESPONSE = lOYALTYISSUANCERESPONSE;
	}
	
}
