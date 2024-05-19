package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyDataRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyDataResponseObject;

public interface LoyaltyDataBusinessService extends BaseService {
	LoyaltyDataResponseObject processLoyaltyDataRequest(LoyaltyDataRequestObject loyaltyDataRequestObject) throws BaseServiceException;
}
