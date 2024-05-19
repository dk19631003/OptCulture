

package org.mq.optculture.business.gateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.SMSHTTPDLRRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;


public class SMSBusinessServiceImpl implements SMSBusinessService{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		
		String action = baseRequestObject.getAction();
		logger.debug("=======calling the service ======"+action);
		
		if(action == null){
			throw new BaseServiceException("No action/type found");
		}
		
		SMSBusinessService smsBusinessService = (SMSBusinessService)ServiceLocator.getInstance().getServiceByName(OCConstants.SMSMB_BUSINESS_SERVICE);
		BaseResponseObject baseResponseObject = null;//response to return 
				
		if(action.equals(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR)) {
			
			SMSHTTPDLRRequestObject smsHTTPDLRRequestObj = (SMSHTTPDLRRequestObject)baseRequestObject;

			baseResponseObject = smsBusinessService.processDLRRequest(smsHTTPDLRRequestObj);
		}
		
		return baseResponseObject;
	}
	
	@Override
	public BaseResponseObject processDLRRequest(SMSHTTPDLRRequestObject SMSHTTPDLRRequestObj) throws BaseServiceException {
		BaseResponseObject baseResponseObject = new BaseResponseObject();//TODO set response
		
		
		String msgID = SMSHTTPDLRRequestObj.getMessageID();
		String mobileNumber = SMSHTTPDLRRequestObj.getMobileNumber();
		//String deliveredTimeStr = WAHTTPDLRRequestObj.getDeliveredTimeStr();
		String status = SMSHTTPDLRRequestObj.getStatus();
		
		if(status == null ) {
			
			throw new BaseServiceException("one of the required parameters are not received");
		}
		else if(msgID!=null) { //msgID != null && sentId!=null
			processCampaignDLR(SMSHTTPDLRRequestObj);
		}
		
		return baseResponseObject;
	}
	
	public void processCampaignDLR(SMSHTTPDLRRequestObject SMSHTTPDLRRequestObj) throws  BaseServiceException {
		
		
		String msgID = SMSHTTPDLRRequestObj.getMessageID();
		String status = SMSHTTPDLRRequestObj.getStatus();
		
		String mobile = SMSHTTPDLRRequestObj.getMobileNumber();
		
		SMSCampaignSentDao smsCampaignSentDao = null;
		SMSCampaignSentDaoForDML smsCampaignSentDaoForDML  = null;
		SMSCampaignReportDaoForDML smsCampaignReportDaoForDML=null;
		try {
			smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNSENT_DAO);
			smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGNSENT_DAO_ForDML);
			smsCampaignReportDaoForDML =(SMSCampaignReportDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGNREPORT_DAO_ForDML);
			
		} catch (Exception e) {
			throw new BaseServiceException("Exception while fetching waCampaignSentDao");
		}
			
		SMSCampaignSent smsCampaignSent = smsCampaignSentDao.findByApiMsgIds(msgID);
		if(smsCampaignSent == null ) throw new BaseServiceException("no waCampaignSent "
				+ "found with Msgid="+msgID+ "");
		
		String currentStatus = smsCampaignSent.getStatus();
		
		String sentStatus = "";
		int updateCount=0 ;
		int bounceCount = 0;
		
		if(SMSStatusCodes.BouncedSet.contains(status) ) { //&& !currentStatus.equalsIgnoreCase(WAStatusCodes.WA_STATUS_DELIVERED)) {
			
			//if failed, then set status (Submitted > Bounced)
			updateCount = smsCampaignSentDaoForDML.updateStatus(SMSStatusCodes.MB_DLR_STATUS_BOUNCED, msgID, mobile);
			logger.debug("updateCount========"+updateCount);
			
			//update the bounce reports
			if(smsCampaignSent.getSmsCampaignReport()!=null)
				bounceCount = smsCampaignReportDaoForDML.updateBounceReport(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
			logger.debug("bounceCount========"+bounceCount);

			
		}else if(SMSStatusCodes.DeliveredSet.contains(status)) {
			
			//if delivered successfully, then set status (Bounced > Delivered)
			updateCount = smsCampaignSentDaoForDML.updateStatus(SMSStatusCodes.MB_DLR_STATUS_DELIVERED, msgID, mobile);
			logger.debug("updateCount========"+updateCount);
			
			//update the bounce reports
			if(smsCampaignSent.getSmsCampaignReport()!=null)
				bounceCount = smsCampaignReportDaoForDML.updateBounceReport(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
			logger.debug("bounceCount========"+bounceCount);

			
		}
		
	}
}
