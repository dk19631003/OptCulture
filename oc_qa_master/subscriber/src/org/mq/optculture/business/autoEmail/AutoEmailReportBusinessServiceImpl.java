package org.mq.optculture.business.autoEmail;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoEmailClicks;
import org.mq.marketer.campaign.controller.service.UpdateAutoEmailReports;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptUrlParameters;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.autoEmail.AutoEmailReportRequest;
import org.mq.optculture.model.autoEmail.AutoEmailReportResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class AutoEmailReportBusinessServiceImpl implements AutoEmailReportBusinessService{

	
	public static Queue<Long> opensQueue  =  new LinkedList<Long>();
	public static Queue<AutoEmailClicks> clicksQueue =  new LinkedList<AutoEmailClicks>();
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		AutoEmailReportRequest autoEmailReportRequest = null;
		AutoEmailReportResponse autoEmailReportResponse = null;
		try{
			AutoEmailReportBusinessService autoEmailReportBusinessService = (AutoEmailReportBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.AUTO_EMAIL_REPORT_BUSINESS_SERVICE);
			autoEmailReportRequest = (AutoEmailReportRequest) baseRequestObject;
			if(autoEmailReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_OPEN) == 0){
				autoEmailReportResponse = autoEmailReportBusinessService.processOpenUpdate(autoEmailReportRequest);
			}
			else if(autoEmailReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_CLICK) == 0) {
				autoEmailReportResponse = autoEmailReportBusinessService.processClickUpdate(autoEmailReportRequest);
			}
			if(opensQueue.size() >= 200 || clicksQueue.size() >= 200 ) {
				
				UpdateAutoEmailReports updateAutoEmailReports = (UpdateAutoEmailReports) ServiceLocator.getInstance().getBeanByName("updateAutoEmailReports");
				if(!updateAutoEmailReports.isRunning()) {
					logger.debug("processor is not running , try to ping it....");
					updateAutoEmailReports.run();
					return autoEmailReportResponse;
				}
			
			}
		}catch (Exception e) {
			throw new BaseServiceException("Exception occure while process auto email report..",e);
		}
		
		
		
		return autoEmailReportResponse;
	}

	
	@Override
	public AutoEmailReportResponse processOpenUpdate(
			AutoEmailReportRequest autoEmailReportRequest)
			throws BaseServiceException {
		AutoEmailReportResponse autoEmailReportResponse = new AutoEmailReportResponse();
		autoEmailReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_OPEN_IMAGE);
		Long sentId = null;
		try {
			
		sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(autoEmailReportRequest.getSentId()));
	} catch (Exception e) {
		logger.error("** Exception : Invalid sentId ::"+autoEmailReportRequest.getSentId() +" for the requested action - "+autoEmailReportRequest.getAction());
		throw new BaseServiceException("Exception occured while processing processOpenUpdate::::: ", e);
	} 	
	
	updateOpens(autoEmailReportRequest,sentId);
	
	return autoEmailReportResponse;
	}
	@Override
	public AutoEmailReportResponse processClickUpdate(
			AutoEmailReportRequest autoEmailReportRequest)
			throws BaseServiceException {
		AutoEmailReportResponse autoEmailReportResponse = new AutoEmailReportResponse();
		autoEmailReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_CLICK_REDIRECT);
		Long sentId = null;
		try {
			
				logger.info("Before decrypting SentId:"+autoEmailReportRequest.getSentId() );
				sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(autoEmailReportRequest.getSentId()));
				logger.info("After decrypting SentId:"+sentId);
			
		} catch (Exception e) {
			logger.error("** Exception : Invalid sentId ::"+autoEmailReportRequest.getSentId() );
			throw new BaseServiceException("Exception occured while processing processClickUpdate::::: ", e);
		} 	
		
		if(autoEmailReportRequest.getUrl()==null) {
			if(logger.isDebugEnabled()) logger.debug("URL is null");
			autoEmailReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
			return autoEmailReportResponse;
		} 
		
		if(autoEmailReportRequest.getUrl().contains("&amp;")) {

			autoEmailReportRequest.setUrl(autoEmailReportRequest.getUrl().replace("&amp;", "&"));
		}
		
		AutoEmailClicks autoEmailClicks = new AutoEmailClicks(sentId, autoEmailReportRequest.getUrl());
		
		synchronized (clicksQueue) {

			clicksQueue.add(autoEmailClicks);
		}
		
		if(autoEmailReportRequest.getUrl().indexOf("http://") == -1 && (autoEmailReportRequest.getUrl().indexOf("https://") == -1 )){
			autoEmailReportRequest.setUrl("http://" + autoEmailReportRequest.getUrl().trim());
		}
		autoEmailReportResponse.setUrlStr(autoEmailReportRequest.getUrl());
		return autoEmailReportResponse;
	}
	
	private void updateOpens(AutoEmailReportRequest autoEmailReportRequest,
			Long sentId) throws BaseServiceException {
		
		synchronized (opensQueue) {
			opensQueue.offer(sentId);

		}
		
	}
}
