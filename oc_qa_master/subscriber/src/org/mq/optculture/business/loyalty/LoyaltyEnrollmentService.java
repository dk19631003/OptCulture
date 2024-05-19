package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.loyalty.LoyaltyEnrollRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyEnrollResponseObject;

/**
 * Loyalty Enrollment business service interface.
 * 
 * @author Venkata Rathnam D
 *
 */
public interface LoyaltyEnrollmentService extends BaseService{

	public LoyaltyEnrollResponseObject processEnrollmentRequest(LoyaltyEnrollRequestObject enrollRequest, String mode, String transactionId, String trxDate)
			throws BaseServiceException;
}
