package com.optculture.api.service;

import com.optculture.api.exception.BaseServiceException;
import com.optculture.api.model.BaseResponseObject;
import com.optculture.api.model.EquenceDLRRequestObject;

public interface EquenceBusinessService extends BaseService {
	
	public BaseResponseObject processDLRRequest(EquenceDLRRequestObject EquenceDLRRequestObj) throws BaseServiceException;

}
