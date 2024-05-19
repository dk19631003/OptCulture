package org.mq.optculture.business.mobileapp;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLogoutRequest;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLogoutResponse;

public interface OCLoyaltyMemberLogoutService  extends BaseService {

	public LoyaltyMemberLogoutResponse processLogoutRequest(LoyaltyMemberLogoutRequest loyaltyMemberLogoutRequest, String transactionId, String transactionDate)
			throws BaseServiceException;
	
	
	
	
}

