package org.mq.optculture.business.mobileapp;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.mobileapp.CouponsHistoryRequest;
import org.mq.optculture.model.mobileapp.CouponsHistoryResponse;
import org.mq.optculture.model.mobileapp.LoyaltyTransactionHistoryRequest;
import org.mq.optculture.model.mobileapp.LoyaltyTransactionHistoryResponse;

public interface CouponsHistoryService extends BaseService{
	public CouponsHistoryResponse processCouponsHistoryRequest(CouponsHistoryRequest couponsHistoryRequest, String mode, String transactionId, String transactionDate)
			throws BaseServiceException;
}
