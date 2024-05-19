package org.mq.optculture.business.gateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.WACampaignSent;
import org.mq.captiway.scheduler.dao.WACampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.WACampaignSentDao;
import org.mq.captiway.scheduler.dao.WACampaignSentDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.WAStatusCodes;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.WAHTTPDLRRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;


public class WABusinessServiceImpl implements WABusinessService{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		
		String action = baseRequestObject.getAction();
		//logger.debug("=======calling the service ======"+action);
		
		if(action == null){
			throw new BaseServiceException("No action/type found");
		}
		
		WABusinessService waBusinessService = (WABusinessService)ServiceLocator.getInstance().getServiceByName(OCConstants.WA_BUSINESS_SERVICE);
		BaseResponseObject baseResponseObject = null;//response to return 
				
		if(action.equals(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR)) {
			
			WAHTTPDLRRequestObject WAHTTPDLRRequestObj = (WAHTTPDLRRequestObject)baseRequestObject;

			baseResponseObject = waBusinessService.processDLRRequest(WAHTTPDLRRequestObj);
		}
		
		return baseResponseObject;
	}
	
	@Override
	public BaseResponseObject processDLRRequest(WAHTTPDLRRequestObject WAHTTPDLRRequestObj) throws BaseServiceException {
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		
		
		String msgID = WAHTTPDLRRequestObj.getMessageID();
		String mobileNumber = WAHTTPDLRRequestObj.getMobileNumber();
		//String deliveredTimeStr = WAHTTPDLRRequestObj.getDeliveredTimeStr();
		String status = WAHTTPDLRRequestObj.getStatus();
		Long sentId = WAHTTPDLRRequestObj.getSentId();
		
		if(status == null || sentId==null) {
			
			throw new BaseServiceException("one of the required parameters are not received");
		}
		else if(sentId!=null) { //msgID != null && sentId!=null
			processCampaignDLR(WAHTTPDLRRequestObj);
		}
		
		return baseResponseObject;
	}
	
	public void processCampaignDLR(WAHTTPDLRRequestObject WAHTTPDLRRequestObj) throws  BaseServiceException {
		
		String msgID = WAHTTPDLRRequestObj.getMessageID();
		long sentId = WAHTTPDLRRequestObj.getSentId();
		String status = WAHTTPDLRRequestObj.getStatus();
		String mobileNo = WAHTTPDLRRequestObj.getMobileNumber();
		
		WACampaignSentDao waCampaignSentDao = null;
		WACampaignSentDaoForDML waCampaignSentDaoForDML  = null;
		WACampaignReportDaoForDML waCampaignReportDaoForDML = null;

		try {
			waCampaignSentDao = (WACampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.WA_CAMPAIGNSENT_DAO);
			waCampaignSentDaoForDML = (WACampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.WA_CAMPAIGNSENT_DAO_ForDML);
			waCampaignReportDaoForDML =  (WACampaignReportDaoForDML )ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.WA_CAMPAIGNREPORT_DAO_FOR_DML);

		} catch (Exception e) {
			throw new BaseServiceException("Exception while fetching waCampaignSentDao");
		}
			
		WACampaignSent waCampaignSent = waCampaignSentDao.findByAPIMsgIdAndSentId(msgID, sentId);
		if(waCampaignSent == null ) throw new BaseServiceException("no waCampaignSent "
				+ "found with Msgid="+msgID+ " and sentId="+sentId);
		
		int updateCount=0 ;
		int bounceCount = 0;
		
		if(WAStatusCodes.BouncedSet.contains(status) ) {
			
			//if failed, then set status (Submitted > Bounced)
			updateCount = waCampaignSentDaoForDML.updateStatusByMsgIdAndSentId(WAStatusCodes.WA_STATUS_BOUNCED, msgID, sentId);
			logger.debug("updateCount========"+updateCount);
			
			//update the bounce reports
			if(waCampaignSent.getWaCampaignReport() !=null)
				bounceCount = waCampaignReportDaoForDML.updateBounceReport(waCampaignSent.getWaCampaignReport().getWaCrId());
			logger.debug("bounceCount========"+bounceCount);

			
		}else if(WAStatusCodes.DeliveredSet.contains(status)) {
			
			//if delivered successfully, then set status (Bounced > Delivered)
			updateCount = waCampaignSentDaoForDML.updateStatusByMsgIdAndSentId(WAStatusCodes.WA_STATUS_DELIVERED, msgID, sentId);
			logger.debug("updateCount========"+updateCount);
			
		}else if(WAStatusCodes.ReadSet.contains(status)) {
			//if msg read then no status change, only update opens=1
			updateCount=0 ;
			updateCount = waCampaignSentDaoForDML.updateOpens(msgID, sentId);
			logger.debug("updateCount========"+updateCount);
		}
		
	}
}
