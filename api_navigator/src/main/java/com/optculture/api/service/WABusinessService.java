package com.optculture.api.service;

import com.optculture.api.exception.BaseServiceException;
import com.optculture.api.model.BaseResponseObject;
import com.optculture.api.model.WAHTTPDLRRequestObject;

public interface WABusinessService extends BaseService {
	
	public BaseResponseObject processDLRRequest(WAHTTPDLRRequestObject WAHTTPDLRRequestObj) throws BaseServiceException;

}
