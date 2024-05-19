package com.optculture.api.service;

import com.optculture.api.exception.BaseServiceException;
import com.optculture.api.model.BaseRequestObject;
import com.optculture.api.model.BaseResponseObject;

public interface BaseService {
	
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException;

}
