package org.mq.optculture.business.loyalty;

import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceResponseObject;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceResponse;
import org.mq.optculture.business.common.BaseService;

/**
 * Loyalty Issuance business service interface.
 * 
 * @author Venkata Rathnam D
 *
 */
public interface LoyaltyIssuanceOCService extends BaseService{
	/**
	 * Handles complete process of Loyalty Issuance request
	 * 
	 * @param issuanceRequest
	 * @return issuanceResponse
	 * @throws BaseServiceException
	 */
	public LoyaltyIssuanceResponse processIssuanceRequest(LoyaltyIssuanceRequest issuanceRequest, String mode,
			String transactionId, String transactionDate,String loyaltyExtraction) throws BaseServiceException;
}
