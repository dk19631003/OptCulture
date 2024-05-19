package org.mq.optculture.business.digitalReceipt;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.digitalReceipt.PosDRRequest;
import org.mq.optculture.model.digitalReceipt.PosDRResponse;

public interface PosDRBusinessService extends BaseService {
	public PosDRResponse processPosDRRequest(PosDRRequest posDRRequest) throws BaseServiceException;

}
