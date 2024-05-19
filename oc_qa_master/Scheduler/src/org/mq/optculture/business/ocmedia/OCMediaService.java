package org.mq.optculture.business.ocmedia;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.OCMediaRequestObject;

public interface OCMediaService extends BaseService{

	public BaseResponseObject processURLShortCodeRequest(OCMediaRequestObject OCMediaRequestObj) throws BaseServiceException;
	
	
}

