package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.ocloyalty.LoyaltyOTPRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyOTPResponse;

/**
 * === OptCulture Loyalty Program ===
 * OTP business service interface.
 * 
 * @author Venkata Rathnam D
 *
 */
public interface LoyaltyOTPOCService extends BaseService{

	public LoyaltyOTPResponse processOTPRequest(LoyaltyOTPRequest otpRequest, String mode, 
			String transactionId, String transactionDate) throws BaseServiceException;
}
