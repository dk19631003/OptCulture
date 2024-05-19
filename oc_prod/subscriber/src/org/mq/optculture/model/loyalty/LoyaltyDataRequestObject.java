package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseRequestObject;

public class LoyaltyDataRequestObject extends BaseRequestObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5190524497983077963L;
	private LoyaltyDataReq LOYALTYDATAREQ;

	public LoyaltyDataReq getLOYALTYDATAREQ() {
		return LOYALTYDATAREQ;
	}

	public void setLOYALTYDATAREQ(LoyaltyDataReq lOYALTYDATAREQ) {
		LOYALTYDATAREQ = lOYALTYDATAREQ;
	}
	
}
