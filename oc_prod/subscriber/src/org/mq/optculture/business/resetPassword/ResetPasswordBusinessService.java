package org.mq.optculture.business.resetPassword;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.resetPassword.ResetPasswordTokenRequest;
import org.mq.optculture.model.resetPassword.ResetPasswordTokenResponse;

public interface ResetPasswordBusinessService extends BaseService {
	public ResetPasswordTokenResponse processResetPasswordTokenRequest(ResetPasswordTokenRequest resetPasswordTokenRequest) throws BaseServiceException;
}
