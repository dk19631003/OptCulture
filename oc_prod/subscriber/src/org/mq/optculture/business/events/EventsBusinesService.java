package org.mq.optculture.business.events;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.events.EventRequest;

public interface EventsBusinesService extends BaseService {
	public BaseResponseObject processEventsRequest(EventRequest eventRequest) throws BaseServiceException;

}
