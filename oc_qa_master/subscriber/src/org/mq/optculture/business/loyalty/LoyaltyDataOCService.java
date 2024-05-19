package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.ocloyalty.LoyaltyDataRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyDataResponse;

public interface LoyaltyDataOCService extends BaseService {
	LoyaltyDataResponse processLoyaltyDataRequest(LoyaltyDataRequest loyaltyDataRequest, String transactionId, String transactionDate) throws BaseServiceException;
}
