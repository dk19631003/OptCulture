package org.mq.optculture.business.contactSubscription;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ContactSubscriptionRequestObject;

public interface ContactEnrollmentService extends BaseService{

	public BaseResponseObject processContactSubscriptionRequest(ContactSubscriptionRequestObject contactSubscriptionRequestObject) throws BaseServiceException;
}
