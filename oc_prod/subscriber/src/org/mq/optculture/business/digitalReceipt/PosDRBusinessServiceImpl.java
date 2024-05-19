package org.mq.optculture.business.digitalReceipt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.digitalReceipt.PosDRRequest;
import org.mq.optculture.model.digitalReceipt.PosDRResponse;
import org.mq.optculture.model.digitalReceipt.ResponseInfo;
import org.mq.optculture.model.digitalReceipt.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class PosDRBusinessServiceImpl implements PosDRBusinessService {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		//		String action = baseRequestObject.getAction();
		PosDRResponse posDRResponse=null;
		try {
			PosDRRequest posDRRequest = (PosDRRequest) baseRequestObject;
			

			PosDRBusinessService posDRBusinessService = (PosDRBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.POS_DR_BUSINESS_SERVICE);
			posDRResponse = (PosDRResponse) posDRBusinessService.processPosDRRequest(posDRRequest);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}
		return posDRResponse;
	}
	@Override
	public PosDRResponse processPosDRRequest(PosDRRequest posDRRequest) throws BaseServiceException {

		PosDRResponse posDRResponse=null;
		Status status = null;
		try {
			String userFullDetails = null;

			if( (posDRRequest.getUserName() != null && posDRRequest.getUserName().length() > 1) &&
					(posDRRequest.getUserOrg() != null &&  posDRRequest.getUserOrg().length() > 1) ) {	
				userFullDetails =  posDRRequest.getUserName() + Constants.USER_AND_ORG_SEPARATOR + posDRRequest.getUserOrg();
			} 

			status = validateUser(userFullDetails);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())){
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			JSONObject jsonMainObj = null;
			JSONObject jsonHeadObj= null;
			if(posDRRequest.getJsonValue() != null && posDRRequest.getJsonValue().length() > 1) {
				jsonMainObj = (JSONObject)JSONValue.parse(posDRRequest.getJsonValue());
			} else {
				return null;
			}

			jsonHeadObj = (JSONObject)jsonMainObj.get("Head");
			status = validateHead(jsonHeadObj);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())){
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}

			String token = jsonHeadObj.get("token") == null ? null : jsonHeadObj.get("token").toString().trim();
			status = validateToken(token);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())){
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}

			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.findByToken(userFullDetails, token);
			status = validateUserWithToken(user);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())){
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			status = new Status("0", "", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			posDRResponse = prepareResponseObject(status);
			return posDRResponse;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processPosDRRequest::::: ", e);
		}
	}

	private Status validateUser(String userFullDetails)  throws BaseServiceException  {
		Status status = null;
		if(userFullDetails == null || userFullDetails.isEmpty()) {
			logger.debug("Required user name and Organization fields are not valid ... returning ");
			status = new Status("300001","Given User name or Organization details not found .",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status ;
		}
		return status ;
	}

	private Status validateHead(JSONObject head)  throws BaseServiceException {
		Status status = null;
		if(head == null) {
			logger.info("Error : unable to find the User Details with given token****");
			status = new Status("101011","Error : unable to find the User Token in JSON ",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status ;
		}
		return status ;
	}

	private Status validateToken(String token)  throws BaseServiceException {
		Status status = null;
		if(token == null || token.isEmpty() ) {
			logger.info("Error : User Token,UserName,Organisation cannot be empty.");
			status = new Status("101012","Error : User Token cannot be empty.",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status ;
		}
		return status ;
	}

	private Status validateUserWithToken(Users user) throws BaseServiceException  {
		Status status = null;
		if(user == null) {
			logger.debug("******** User Object not Found ... returing ");
			status = new Status("100011","Given User name or Organization details with token not found .",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status ;
		}
		return status ;
	}
	
	private PosDRResponse prepareResponseObject(Status status) {
		PosDRResponse posDRResponse = new PosDRResponse();
		ResponseInfo responseInfo = new ResponseInfo(status);
		posDRResponse.setResponseInfo(responseInfo);
		return posDRResponse;
	}

}
