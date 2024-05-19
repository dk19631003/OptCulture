package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.loyalty.LoyaltyInquiryRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyInquiryResponseObject;

public interface AsyncLoyaltyInquiryService extends BaseService{
	/**
	 * Handles complete process of Loyalty inquiry request
	 * 
	 * @param inquiryRequest 
	 * @return inquiryResponse
	 * @throws BaseServiceException
	 */
	/*public LoyaltyInquiryResponseObject processInquiryRequest(LoyaltyInquiryRequestObject inquiryRequest)
			throws BaseServiceException;*/
	
	public LoyaltyInquiryResponseObject processInquiryRequest(LoyaltyInquiryRequestObject inquiryRequest, String trxID, String trxDate)
			throws BaseServiceException;
}
