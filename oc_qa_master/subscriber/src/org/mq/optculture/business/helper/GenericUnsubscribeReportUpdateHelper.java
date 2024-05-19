package org.mq.optculture.business.helper;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Unsubscribes;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.UnsubscribesDao;
import org.mq.marketer.campaign.dao.UnsubscribesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.campaign.GenericUnsubscribeRequest;
import org.mq.optculture.model.campaign.GenericUnsubscribeResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;

public class GenericUnsubscribeReportUpdateHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		GenericUnsubscribeRequest genericUnsubscribeRequest = (GenericUnsubscribeRequest) baseRequestObject;
		String action = baseRequestObject.getAction() ;
		if(action==null){
		return processUnsubscribeRequest(genericUnsubscribeRequest);
		}else if(action.equalsIgnoreCase(Constants.UNSUBSCRIBE_REQUEST_VALUE_RESUB_UPDATE)){
			return processResubscribe(genericUnsubscribeRequest);
		}else if(action.equalsIgnoreCase(Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB_UPDATE)){
		return	processUnsubscribeUpdate(genericUnsubscribeRequest);
		}
			return null;
	}
	
	
	public GenericUnsubscribeResponse processUnsubscribeRequest(
			GenericUnsubscribeRequest genericUnsubscribeRequest)
			throws BaseServiceException {
		GenericUnsubscribeResponse genericUnsubscribeResponse = new GenericUnsubscribeResponse();
		genericUnsubscribeResponse.setResponseType(OCConstants.RESPONSE_TYPE_UNSUBSCRIBE_REQ);
		Long userId = null;
		try {
				userId = genericUnsubscribeRequest.getUserId();
			
		} catch (Exception e) {
			logger.warn(" userId not found in the querystring");
			throw new BaseServiceException("Exception occured while processing processUnsubscribeRequest::::: ", e);
		}
		genericUnsubscribeResponse.setUserId(userId);
	//	logger.debug("returning campaignReportResponse"+campaignReportResponse);
		return genericUnsubscribeResponse;
	}
	
	public GenericUnsubscribeResponse processUnsubscribeUpdate(
			GenericUnsubscribeRequest genericUnsubscribeRequest)
			throws BaseServiceException {
		GenericUnsubscribeResponse genericUnsubscribeResponse = new GenericUnsubscribeResponse();
		genericUnsubscribeResponse.setResponseType(OCConstants.RESPONSE_TYPE_UNSUBSCRIBE_UPDATE);
		try {

			genericUnsubscribeResponse.setUserId(genericUnsubscribeRequest.getUserId());
			genericUnsubscribeResponse.setEmailId(genericUnsubscribeRequest.getEmailId());
		
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			contactsDaoForDML.updateEmailStatusByUserId(genericUnsubscribeRequest.getEmailId(), genericUnsubscribeRequest.getUserId(), "Unsubscribed");
			
			UnsubscribesDao unsubscribesDao = (UnsubscribesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.UNSUBSCRIBE_DAO);
			UnsubscribesDaoForDML unsubscribesDaoForDML = (UnsubscribesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UNSUBSCRIBE_DAO_FOR_DML);
			Unsubscribes unsubscribes = new Unsubscribes();
			unsubscribes.setEmailId(genericUnsubscribeRequest.getEmailId());
			unsubscribes.setUserId(genericUnsubscribeRequest.getUserId());
			unsubscribes.setReason(genericUnsubscribeRequest.getReason());
			unsubscribes.setUnsubcategoriesWeight((short)0);
			unsubscribes.setDate(Calendar.getInstance());
			unsubscribesDaoForDML.saveOrUpdate(unsubscribes);
			

		}catch (DataIntegrityViolationException e) {
			logger.error("Already unsubscribed email");
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processUnsubscribeUpdate::::: ", e);
		}
	//	logger.info("returning response campaignReportResponse...........");
		return genericUnsubscribeResponse;
	}
	
	public GenericUnsubscribeResponse processResubscribe(
			GenericUnsubscribeRequest genericUnsubscribeRequest)
			throws BaseServiceException {
		GenericUnsubscribeResponse genericUnsubscribeResponse = new GenericUnsubscribeResponse();
		genericUnsubscribeResponse.setResponseType(OCConstants.RESPONSE_TYPE_RESUBSCRIBE);
		
		String tempEmailId = null;
		Long tempUserId = null;
		try {
			
				tempEmailId = genericUnsubscribeRequest.getEmailId();
				
				tempUserId = genericUnsubscribeRequest.getUserId();
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
			throw new BaseServiceException("Exception occured while processing processResubscribe::::: ", e);
		}

		if(tempEmailId == null || tempEmailId.trim().length() == 0) {
			genericUnsubscribeResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
			return genericUnsubscribeResponse;
		}
		
		else if(tempUserId == null ) {
			genericUnsubscribeResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
			return genericUnsubscribeResponse;
		}

		//Find the User Object if exist

		//Update the Contact Status 
		try {
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			contactsDaoForDML.updateEmailStatusByUserId(tempEmailId, tempUserId, "Active");
			UnsubscribesDao unsubscribesDao = (UnsubscribesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.UNSUBSCRIBES_DAO);
			UnsubscribesDaoForDML unsubscribesDaoForDML = (UnsubscribesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UNSUBSCRIBES_DAO_FOR_DML);
			unsubscribesDaoForDML.deleteByEmailIdUserId(tempEmailId, tempUserId);
		} catch (Exception e) {
			logger.error("Exception ::", e);
			throw new BaseServiceException("Exception occured while processing processResubscribe::::: ", e);
		}
		
		return genericUnsubscribeResponse;
	}



}
