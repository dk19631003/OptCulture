package org.mq.optculture.business.mobileapp;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.mobileapp.StoreInquiryRequest;
import org.mq.optculture.model.mobileapp.StoreInquiryResponse;

public interface StoreInquiryService extends BaseService{
	public StoreInquiryResponse processStoreInquiryRequest(StoreInquiryRequest storeInquiryRequest, String transactionId, String transactionDate)
			throws BaseServiceException;

}
