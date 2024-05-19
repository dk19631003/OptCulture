package org.mq.optculture.business.loyalty;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;

public interface ReIssuePerksBusinessService extends BaseService  {
	
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException;
	
	public void processReIssuePerksRequest(Long tierId, Long prgmId) throws BaseServiceException;

}
