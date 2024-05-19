package org.mq.optculture.business.loyalty;

import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceResponseObject;
import org.mq.optculture.business.common.BaseService;

/**
 * Loyalty Issuance business service interface.
 * 
 * @author Venkata Rathnam D
 *
 */
public interface LoyaltyIssuanceService extends BaseService{
	/**
	 * Handles complete process of Loyalty Issuance request
	 * 
	 * @param issuanceRequest
	 * @return issuanceResponse
	 * @throws BaseServiceException
	 */
	public LoyaltyIssuanceResponseObject processIssuanceRequest(LoyaltyIssuanceRequestObject issuanceRequest, String mode, String trxId, String trxDate)
			throws BaseServiceException;
}
