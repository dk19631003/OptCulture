package org.mq.optculture.business.loyalty;

import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyRedemptionResponse;
import org.mq.optculture.business.common.BaseService;

/**
 * Loyalty Redemption business service interface.
 * 
 * @author Venkata Rathnam D
 *
 */
public interface LoyaltyRedemptionOCService extends BaseService{
	/**
	 * Handles complete process of Loyalty Redemption request
	 * 
	 * @param redemptionRequest
	 * @return redemptionResponse
	 * @throws BaseServiceException
	 */
	public LoyaltyRedemptionResponse processRedemptionRequest(LoyaltyRedemptionRequest redemptionRequest, String mode, 
			String transactionId, String transactionDate,String loyaltyExtraction)
			throws BaseServiceException;	
}
