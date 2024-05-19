package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.loyalty.LoyaltyInquiryJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyInquiryJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyInquiryRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyInquiryResponseObject;
/**
 * loyalty inquiry business service interface.
 * 
 * @author Venkata Rathnam D
 * 
 */
public interface LoyaltyInquiryService extends BaseService{
	/**
	 * Handles complete process of Loyalty inquiry request
	 * 
	 * @param inquiryRequest 
	 * @return inquiryResponse
	 * @throws BaseServiceException
	 */
	public LoyaltyInquiryResponseObject processInquiryRequest(LoyaltyInquiryRequestObject inquiryRequest)
			throws BaseServiceException;
}
