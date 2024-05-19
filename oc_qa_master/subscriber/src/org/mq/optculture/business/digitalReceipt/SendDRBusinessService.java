package org.mq.optculture.business.digitalReceipt;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.digitalReceipt.SendDRRequest;
import org.mq.optculture.model.digitalReceipt.SendDRResponse;

public interface SendDRBusinessService extends BaseService{
	public SendDRResponse processSendDRRequest(SendDRRequest sendDRRequest, String mode) throws BaseServiceException;
}
