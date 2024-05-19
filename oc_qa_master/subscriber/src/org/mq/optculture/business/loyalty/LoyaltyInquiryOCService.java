package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyInquiryResponse;
/**
 * loyalty inquiry business service interface.
 * 
 * @author Venkata Rathnam D
 * 
 */
public interface LoyaltyInquiryOCService extends BaseService{
	/**
	 * Handles complete process of Loyalty inquiry request
	 * 
	 * @param inquiryRequest 
	 * @return inquiryResponse
	 * @throws BaseServiceException
	 */
	public LoyaltyInquiryResponse processInquiryRequest(LoyaltyInquiryRequest inquiryRequest, String transactionId, String transactionDate)
			throws BaseServiceException;
}
