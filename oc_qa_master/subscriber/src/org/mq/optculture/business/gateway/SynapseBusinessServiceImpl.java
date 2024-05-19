package org.mq.optculture.business.gateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.SynapseDLRRequestObject;
import org.mq.optculture.model.SynapseDLRResponseObject;
import org.mq.optculture.utils.OCConstants;

import com.google.gson.Gson;

public class SynapseBusinessServiceImpl implements SynapseBusinessService{
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
	public BaseResponseObject processRequest(SynapseDLRRequestObject synDLRRequestObj) throws BaseServiceException {
		// TODO Auto-generated method stub
		
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		//DLRRequests dlr= synDLRRequestObj.new DLRRequests();
		String referenceId = synDLRRequestObj.getReferenceId();
		String mobile = synDLRRequestObj.getMobile();
		String deliveryTime = synDLRRequestObj.getDeliveryTime();
		String status = synDLRRequestObj.getStatus();
		String msgId = synDLRRequestObj.getMsgId();
		String responseJson = null;
		Gson gson = new Gson();
		SynapseDLRResponseObject dlrResp = new SynapseDLRResponseObject();
		//DLRResponse dlrResp= retResponse.new DLRResponse();
		dlrResp.setReferenceId(referenceId);
		dlrResp.setMobile(mobile);
		dlrResp.setDeliveryTime(deliveryTime);
		dlrResp.setStatus(status);
		dlrResp.setMsgId(msgId);
		responseJson = gson.toJson(dlrResp);
		logger.info("responseJson "+responseJson);
		/*try{
		SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(("smsCampaignSentDaoForDML"));
		smsCampaignSentDaoForDML.updateStatus(mrId, mobileNumber,status);
		}catch(Exception e){
			logger.error("Exception",e);
		}*/
		baseResponseObject.setAction(synDLRRequestObj.getAction());
		baseResponseObject.setJsonValue(responseJson);
		return baseResponseObject;
		//return null;
	}
}
