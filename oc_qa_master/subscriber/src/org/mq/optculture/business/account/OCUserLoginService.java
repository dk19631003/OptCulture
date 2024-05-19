package org.mq.optculture.business.account;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.account.OCUserLoginRequest;
import org.mq.optculture.model.account.OCUserLoginResponse;

public interface OCUserLoginService extends BaseService{

	
	public OCUserLoginResponse processUserLoginRequest(OCUserLoginRequest OCUserLoginRequest)
			throws BaseServiceException; 
}
