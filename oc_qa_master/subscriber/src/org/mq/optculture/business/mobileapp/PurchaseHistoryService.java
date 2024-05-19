package org.mq.optculture.business.mobileapp;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.mobileapp.PurchaseHistoryRequest;
import org.mq.optculture.model.mobileapp.PurchaseHistoryResponse;

public interface PurchaseHistoryService extends BaseService{

	public PurchaseHistoryResponse processPurchaseHistoryRequest(PurchaseHistoryRequest purchaseHistoryRequest, String mode, String transactionId, String transactionDate)
			throws BaseServiceException;
	
	
	
	
}
