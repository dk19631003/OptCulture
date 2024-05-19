package org.mq.optculture.business.resetPassword;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ResetPasswordToken;
import org.mq.marketer.campaign.dao.ResetPasswordTokenDao;
import org.mq.marketer.campaign.dao.ResetPasswordTokenDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.resetPassword.ResetPasswordTokenRequest;
import org.mq.optculture.model.resetPassword.ResetPasswordTokenResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class ResetPasswordBusinessServiceImpl implements ResetPasswordBusinessService {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		ResetPasswordTokenResponse resetPasswordTokenResponse = null;
		try {
			ResetPasswordTokenRequest resetPasswordTokenRequest = (ResetPasswordTokenRequest) baseRequestObject;

			ResetPasswordBusinessService resetPasswordBusinessService = (ResetPasswordBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.RESET_PASSWORD_BUSINESS_SERVICE);
			resetPasswordTokenResponse = (ResetPasswordTokenResponse) resetPasswordBusinessService.processResetPasswordTokenRequest(resetPasswordTokenRequest);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}
		return resetPasswordTokenResponse;
	}

	@Override
	public ResetPasswordTokenResponse processResetPasswordTokenRequest(
			ResetPasswordTokenRequest resetPasswordTokenRequest)
					throws BaseServiceException {
		ResetPasswordTokenResponse resetPasswordTokenResponse = null;
		try {
			String token = resetPasswordTokenRequest.getToken();
			Long userId = resetPasswordTokenRequest.getUserId();
			resetPasswordTokenResponse = validateToken(token,userId);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processResetPasswordTokenRequest::::: ", e);
		}
		return resetPasswordTokenResponse;
	}

	private ResetPasswordTokenResponse validateToken(String token, Long userId) throws BaseServiceException {
		ResetPasswordTokenResponse resetPasswordTokenResponse = new ResetPasswordTokenResponse();
		try {
			ResetPasswordTokenDao resetPasswordTokenDao = (ResetPasswordTokenDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RESET_PASSWORD_TOKEN_DAO);
			ResetPasswordTokenDaoForDML resetPasswordTokenDaoForDML = (ResetPasswordTokenDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.RESET_PASSWORD_TOKEN_DAO_FOR_DML);
			ResetPasswordToken tokenObj = resetPasswordTokenDao.findByTokenValue(token);

			Calendar createdDate = tokenObj.getCreatedDate();
			Calendar currentDate = Calendar.getInstance();
			int dateDiff = 0;
			if(createdDate != null){
				dateDiff = (int)((currentDate.getTimeInMillis() - createdDate.getTimeInMillis())/(1000*60));
			}
			if (tokenObj != null && tokenObj.getStatus().equals(Constants.TOKEN_STATUS_ACTIVE) &&
					tokenObj.getUserId().toString().equals(userId.toString()) && dateDiff <= (24*60)) {
				resetPasswordTokenResponse.setStatus(OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			}
			else {
				if (tokenObj != null && tokenObj.getStatus().equals(Constants.TOKEN_STATUS_ACTIVE) && 
						tokenObj.getUserId().toString().equals(userId.toString())) {
					tokenObj.setStatus(Constants.TOKEN_STATUS_EXPIRED);
					resetPasswordTokenDaoForDML.saveOrUpdate(tokenObj);
				}
				resetPasswordTokenResponse.setStatus(OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing validateToken::::: ", e);
		}
		return resetPasswordTokenResponse;
	}

}
