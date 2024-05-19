package org.mq.optculture.business.gateway;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.WAHTTPDLRRequestObject;

public interface WABusinessService extends BaseService{

	public BaseResponseObject processDLRRequest(WAHTTPDLRRequestObject WAHTTPDLRRequestObj) throws BaseServiceException;
	
}