package org.mq.optculture.business.sparkbase;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.sparkbase.TokenRequestObject;

public interface SparkBaseTokenService extends BaseService{

	public BaseResponseObject processTokenRequest(TokenRequestObject tokenRequestObject) throws BaseServiceException;
}
