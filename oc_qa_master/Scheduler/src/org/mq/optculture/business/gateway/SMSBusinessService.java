package org.mq.optculture.business.gateway;


import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.SMSHTTPDLRRequestObject;

public interface SMSBusinessService extends BaseService{

	public BaseResponseObject processDLRRequest(SMSHTTPDLRRequestObject SMSHTTPDLRRequestObj) throws BaseServiceException;
	
}
