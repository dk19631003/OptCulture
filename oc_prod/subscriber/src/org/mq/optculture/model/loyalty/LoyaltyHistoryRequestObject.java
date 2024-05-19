package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseRequestObject;

public class LoyaltyHistoryRequestObject extends BaseRequestObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5190524497983077963L;
	private LoyaltyHistoryRequest LOYALTYDATAREQ;

	public LoyaltyHistoryRequest getLOYALTYDATAREQ() {
		return LOYALTYDATAREQ;
	}

	public void setLOYALTYDATAREQ(LoyaltyHistoryRequest lOYALTYDATAREQ) {
		LOYALTYDATAREQ = lOYALTYDATAREQ;
	}
	
}
