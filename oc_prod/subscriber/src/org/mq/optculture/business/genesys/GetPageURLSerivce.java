package org.mq.optculture.business.genesys;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.genesys.GetPageURLRequest;
import org.mq.optculture.model.genesys.GetPageURLResponse;

public interface GetPageURLSerivce extends BaseService {

	public GetPageURLResponse processGetPageUrlRequest(GetPageURLRequest getPageURLRequest, String transactionId, String transactionDate);
}
