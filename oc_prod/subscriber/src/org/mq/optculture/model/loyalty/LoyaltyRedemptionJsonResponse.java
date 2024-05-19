package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseResponseObject;

public class LoyaltyRedemptionJsonResponse extends BaseResponseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5992894027904757427L;
	private LoyaltyRedemptionResponseObject LOYALTYREDEMPTIONRESPONSE;

	public LoyaltyRedemptionResponseObject getLOYALTYREDEMPTIONRESPONSE() {
		return LOYALTYREDEMPTIONRESPONSE;
	}

	public void setLOYALTYREDEMPTIONRESPONSE(
			LoyaltyRedemptionResponseObject lOYALTYREDEMPTIONRESPONSE) {
		LOYALTYREDEMPTIONRESPONSE = lOYALTYREDEMPTIONRESPONSE;
	}
	
}
