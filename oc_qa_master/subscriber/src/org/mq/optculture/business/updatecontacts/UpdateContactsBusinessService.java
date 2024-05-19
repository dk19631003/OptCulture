package org.mq.optculture.business.updatecontacts;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.updatecontacts.ContactRequest;

public interface UpdateContactsBusinessService extends BaseService{
	public BaseResponseObject processUpdateContactRequest(ContactRequest contactRequest) throws BaseServiceException;
	public BaseResponseObject processUpdateContactRequest(ContactRequest contactRequest,boolean ignoreMobileValidation) throws BaseServiceException;  

}
