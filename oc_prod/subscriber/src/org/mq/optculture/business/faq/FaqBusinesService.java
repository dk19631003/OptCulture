package org.mq.optculture.business.faq;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.events.EventRequest;
import org.mq.optculture.model.mobileapp.FaqRequest;
import org.mq.optculture.model.mobileapp.FaqResponse;

public interface FaqBusinesService extends BaseService {
	public FaqResponse processFaqRequest(FaqRequest faqRequest) throws BaseServiceException;

}