package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyTransferMembershipJsonRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyTransferMembershipJsonResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyTransferMembershipResponseObject;

public interface LoyaltyTransferMembershipService extends BaseService{

	
	public LoyaltyTransferMembershipJsonResponse processTransferRequest(LoyaltyTransferMembershipJsonRequest loyaltyTransferRequest, String mode, String transactionId, String transactionDate)
			throws BaseServiceException;
	
	
}
