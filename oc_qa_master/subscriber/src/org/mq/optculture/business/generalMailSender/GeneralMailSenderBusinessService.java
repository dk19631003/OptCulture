package org.mq.optculture.business.generalMailSender;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.generalMailSender.MailSenderRequestObject;

public interface GeneralMailSenderBusinessService extends BaseService{
	public BaseResponseObject processMailSending(MailSenderRequestObject mailSenderRequestObject) throws BaseServiceException;
}
