package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.loyalty.LoyaltyEnrollRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyEnrollResponseObject;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;

/**
 * === OptCulture Loyalty Program ===
 * Enrolment business service interface.
 * 
 * @author Venkata Rathnam D
 *
 */
public interface LoyaltyEnrollmentOCService extends BaseService{

	public LoyaltyEnrollResponse processEnrollmentRequest(LoyaltyEnrollRequest enrollRequest, String mode, String transactionId, String transactionDate)
			throws BaseServiceException;
}
