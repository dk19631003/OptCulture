package org.mq.optculture.business.loyalty;

import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionResponseObject;
import org.mq.optculture.business.common.BaseService;

/**
 * Loyalty Redemption business service interface.
 * 
 * @author Venkata Rathnam D
 *
 */
public interface LoyaltyRedemptionService extends BaseService{
	/**
	 * Handles complete process of Loyalty Redemption request
	 * 
	 * @param redemptionRequest
	 * @return redemptionResponse
	 * @throws BaseServiceException
	 */
	public LoyaltyRedemptionResponseObject processRedemptionRequest(LoyaltyRedemptionRequestObject redemptionRequest, String mode, String trxID, String trxDate,String loyaltyExtraction)
			throws BaseServiceException;	
}
