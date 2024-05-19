package org.mq.optculture.business.nexon;

import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.business.common.BaseService;


public interface NexonStoreEnquiryBusinessService extends BaseService {
	public NexonStoreEnquiryResponse processNexonRequest(NexonStoreEnquiryRequest nexonRequest) throws BaseServiceException;
}
