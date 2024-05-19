package org.mq.optculture.business.gateway;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.EquenceDLRRequestObject;

public interface EquenceBusinessService extends BaseService{
	
	public BaseResponseObject processRequest(EquenceDLRRequestObject request) throws BaseServiceException;

}
