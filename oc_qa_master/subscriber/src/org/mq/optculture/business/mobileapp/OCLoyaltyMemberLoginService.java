package org.mq.optculture.business.mobileapp;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLoginRequest;
import org.mq.optculture.model.mobileapp.LoyaltyMemberLoginResponse;

public interface OCLoyaltyMemberLoginService extends BaseService {

	public LoyaltyMemberLoginResponse processLoginRequest(LoyaltyMemberLoginRequest loyaltyMemberLoginRequest, String transactionId, String transactionDate)
			throws BaseServiceException;
}
