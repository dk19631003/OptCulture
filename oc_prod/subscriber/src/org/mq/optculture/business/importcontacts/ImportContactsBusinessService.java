package org.mq.optculture.business.importcontacts;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.importcontact.ImportRequest;


public interface ImportContactsBusinessService extends BaseService{
	public BaseResponseObject processImportContactRequest(ImportRequest importRequest) throws BaseServiceException;
	  

}
