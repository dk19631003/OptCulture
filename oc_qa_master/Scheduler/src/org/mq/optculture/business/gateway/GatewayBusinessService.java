package org.mq.optculture.business.gateway;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.SMSHTTPDLRRequestObject;
import org.mq.optculture.model.SMSInBoundRequestObject;

public interface GatewayBusinessService extends BaseService{

	public BaseResponseObject processSMSInBoundRequest(SMSInBoundRequestObject smsInBoundRequestObject) throws BaseServiceException;
	
	public BaseResponseObject processSMSMissedCallOptinRequest(SMSInBoundRequestObject smsInBoundRequestObject) throws BaseServiceException;
	
	public BaseResponseObject processDLRRequest(SMSHTTPDLRRequestObject SMSHTTPDLRRequestObj) throws BaseServiceException;
	
}

