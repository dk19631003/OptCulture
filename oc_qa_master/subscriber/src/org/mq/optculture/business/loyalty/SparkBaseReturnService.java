package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.ocloyalty.LoyaltyOfflineReturnTransactionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyReturnTransactionRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyReturnTransactionResponse;

public interface SparkBaseReturnService extends BaseService{
	/**
	 * Handles complete process of Loyalty return transaction request
	 * 
	 * @param returnTransactionRequest 
	 * @param requestJson 
	 * @return returnTransactionResponse
	 * @throws BaseServiceException
	 */
	public LoyaltyReturnTransactionResponse processTempReturnTransactionRequest(LoyaltyReturnTransactionRequest returnTransactionRequest, String transactionId, 
																			String transactionDate, String requestJson, String mode)
			throws BaseServiceException;
	
	/*public LoyaltyReturnTransactionResponse processOfflineReturnTransactionRequest(LoyaltyOfflineReturnTransactionRequest returnOfflineTransactionRequest, String transactionId, 
			String transactionDate, String requestJson, String mode)
throws BaseServiceException;*/

	
	public LoyaltyReturnTransactionResponse processReturnTransactionRequest(LoyaltyReturnTransactionRequest returnTransactionRequest, String transactionId, 
			String transactionDate, String requestJson, String mode)
throws BaseServiceException;

}
