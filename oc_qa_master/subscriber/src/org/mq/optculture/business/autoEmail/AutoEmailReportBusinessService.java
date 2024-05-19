package org.mq.optculture.business.autoEmail;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.autoEmail.AutoEmailReportRequest;
import org.mq.optculture.model.autoEmail.AutoEmailReportResponse;

public interface AutoEmailReportBusinessService extends BaseService {
	public AutoEmailReportResponse  processOpenUpdate(AutoEmailReportRequest autoEmailReportRequest) throws BaseServiceException;
	public AutoEmailReportResponse  processClickUpdate(AutoEmailReportRequest autoEmailReportRequest) throws BaseServiceException;
}
