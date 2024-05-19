package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.loyalty.LoyaltyDataRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyDataResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyHistoryRequest;
import org.mq.optculture.model.loyalty.LoyaltyHistoryRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyHistoryResponse;
import org.mq.optculture.model.loyalty.LoyaltyHistoryResponseObject;

public interface LoyaltyEnrollmentHistoryService extends BaseService{
	
	LoyaltyHistoryResponseObject processLoyaltyDataRequest(LoyaltyHistoryRequestObject loyaltyHistoryRequestObject) throws BaseServiceException;

}
