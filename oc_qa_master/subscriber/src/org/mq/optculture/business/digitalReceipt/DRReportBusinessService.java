package org.mq.optculture.business.digitalReceipt;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.digitalReceipt.DRReportRequest;

public interface DRReportBusinessService extends BaseService {
	public BaseResponseObject processDRReportRequest(DRReportRequest DRReportRequest) throws BaseServiceException;

}
