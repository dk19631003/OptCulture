package org.mq.optculture.business.optSyncData;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.OptSyncDataRequestObject;

public interface OptSyncDataService extends BaseService {
	
	public BaseResponseObject processOptSynsDataRequest(OptSyncDataRequestObject optSyncDataRequestObject) throws BaseServiceException;

}
