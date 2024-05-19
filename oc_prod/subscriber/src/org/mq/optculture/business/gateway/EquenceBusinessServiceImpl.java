package org.mq.optculture.business.gateway;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.EquenceDLRRequestObject;
import org.mq.optculture.model.EquenceDLRResponseObject;
import org.mq.optculture.model.ocloyalty.LoyaltyIssuanceResponse;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;

import com.google.gson.Gson;

import org.mq.marketer.campaign.controller.service.EquenceSMSGateway;
import org.mq.marketer.campaign.controller.service.EquenceSMSGateway.PrepareEquenceJsonResponse;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.SMSCampaignSentDaoForDML;
import org.mq.marketer.campaign.general.*;

public class EquenceBusinessServiceImpl implements EquenceBusinessService{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/*public EquenceBusinessServiceImpl(ApplicationContext applicationContext){
	this.applicationContext = applicationContext;
	smsCampaignSentDaoForDML=(SMSCampaignSentDaoForDML)applicationContext.getBean("smsCampaignSentDaoForDML");
	}*/
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponseObject processRequest(EquenceDLRRequestObject eqDLRRequestObj) throws BaseServiceException {
		// TODO Auto-generated method stub
		
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		String msgID = eqDLRRequestObj.getMessageID();
		String mobileNumber = eqDLRRequestObj.getMobileNumber();
		String deliveredTime = eqDLRRequestObj.getDeliveredTime();
		String status = eqDLRRequestObj.getStatus();
		String reason = eqDLRRequestObj.getReason();
		String mrId = eqDLRRequestObj.getMrId();
		String responseJson = null;
		EquenceDLRResponseObject resp = new EquenceDLRResponseObject();
		Gson gson = new Gson();
		resp.setMessageID(msgID);
		resp.setMobileNumber(mobileNumber);
		resp.setDeliveredTime(deliveredTime);
		resp.setStatus(status);
		resp.setReason(reason);
		resp.setMrId(mrId);
		responseJson = gson.toJson(resp);
		/*try{
		SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(("smsCampaignSentDaoForDML"));
		smsCampaignSentDaoForDML.updateStatus(mrId, mobileNumber,status);
		}catch(Exception e){
			logger.error("Exception",e);
		}*/
		baseResponseObject.setAction(eqDLRRequestObj.getAction());
		baseResponseObject.setJsonValue(responseJson);
		return baseResponseObject;
		//return null;
	}
	
	

}
