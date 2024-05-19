package org.mq.optculture.business.gateway;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.SynapseDLRRequestObject;

public interface SynapseBusinessService extends BaseService{
	public BaseResponseObject processRequest(SynapseDLRRequestObject request) throws BaseServiceException;
}
