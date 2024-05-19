package org.mq.optculture.business.mobileapp;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.mobileapp.LoyaltyTransactionHistoryRequest;
import org.mq.optculture.model.mobileapp.LoyaltyTransactionHistoryResponse;

public interface LoyaltyTransactionHistoryService extends BaseService{
	public LoyaltyTransactionHistoryResponse processLtyTrxHistoryRequest(LoyaltyTransactionHistoryRequest purchaseHistoryRequest, String mode, String transactionId, String transactionDate)
			throws BaseServiceException;
	
}
