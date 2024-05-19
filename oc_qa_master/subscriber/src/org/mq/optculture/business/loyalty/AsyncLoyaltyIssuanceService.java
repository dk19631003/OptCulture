package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceResponseObject;

public interface AsyncLoyaltyIssuanceService extends BaseService{
	/**
	 * Handles complete process of Loyalty Issuance request
	 * 
	 * @param issuanceRequest
	 * @return issuanceResponse
	 * @throws BaseServiceException
	 */
	public LoyaltyIssuanceResponseObject processIssuanceRequest(LoyaltyIssuanceRequestObject issuanceRequest, String mode)
			throws BaseServiceException;
}
