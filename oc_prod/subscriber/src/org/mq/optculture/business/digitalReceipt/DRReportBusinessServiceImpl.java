package org.mq.optculture.business.digitalReceipt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DRSMSChannelSent;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.beans.DRSent;
import org.mq.marketer.campaign.dao.DRSMSChannelSentDao;
import org.mq.marketer.campaign.dao.DRSMSChannelSentDaoForDML;
import org.mq.marketer.campaign.dao.DRSMSSentDao;
import org.mq.marketer.campaign.dao.DRSMSSentDaoForDML;
import org.mq.marketer.campaign.dao.DRSentDao;
import org.mq.marketer.campaign.dao.DRSentDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptUrlParameters;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.digitalReceipt.DRReportRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class DRReportBusinessServiceImpl implements DRReportBusinessService {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		BaseResponseObject baseResponseObject = null;
		try {
			DRReportRequest drReportRequest = (DRReportRequest) baseRequestObject;
			DRReportBusinessService drReportBusinessService = (DRReportBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.DR_REPORT_BUSINESS_SERVICE);
			baseResponseObject = drReportBusinessService.processDRReportRequest(drReportRequest);
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}
		return baseResponseObject;
	}

	@Override
	public BaseResponseObject processDRReportRequest(
			DRReportRequest drReportRequest) throws BaseServiceException {
		try {
			DRSentDao drSentDao = (DRSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SENT_DAO);
			DRSentDaoForDML drSentDaoForDML = (DRSentDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SENT_DAO_ForDML);
			DRSMSSentDao drSmsSentDao = (DRSMSSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_SENT_DAO);
			DRSMSSentDaoForDML drSmsSentDaoForDML = (DRSMSSentDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_SENT_DAO_ForDML);
			DRSMSChannelSentDao drSmsChannelSentDao = (DRSMSChannelSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_Channel_SENT_DAO);
			DRSMSChannelSentDaoForDML drSmsChannelSentDaoForDML = (DRSMSChannelSentDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_Channel_SENT_DAO_For_DML);
			//		DRSent drSent = drSentDao.findById(sentId);
			logger.debug("action==="+drReportRequest.getRequestedAction());
			Long sentId=null;
			if (drReportRequest.getSentId() != null) {
				try {
					sentId = Long.parseLong(drReportRequest.getSentId() + "");
				} catch (Exception e) {
					sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(drReportRequest.getSentId() + "")); //app-3769
					logger.info("Decrypted SentId ..................:" + sentId);
				} 
			}
			if (OCConstants.DR_REPORT_ACTION_TYPE_OPEN.equals(drReportRequest.getRequestedAction())) {
				//drSentDao.updateOpenCount(drReportRequest.getSentId());
				if(sentId != null)	drSentDaoForDML.updateOpenCount(sentId);
			}
			if (OCConstants.DR_REPORT_ACTION_TYPE_OPEN_SMS.equals(drReportRequest.getRequestedAction())) {
				if (sentId != null) {
					drSmsSentDaoForDML.updateOpenCount(sentId);
					DRSMSSent drSmsSent = drSmsSentDao.findById(sentId);
					if (drSmsSent != null && drSmsSent.getOpens() > 0
							&& drSmsSent.getStatus().equals(Constants.DR_STATUS_SUBMITTED)) {
						drSmsSentDaoForDML.updateDeliveryStatus(sentId);
					} 
				}
			}
			else if(OCConstants.DR_REPORT_ACTION_TYPE_CLICK.equals(drReportRequest.getRequestedAction())) {
				if (sentId != null) {
					DRSent drSent = drSentDao.findById(sentId); //(drReportRequest.getSentId());
					if (drSent != null && drSent.getOpens() == 0) {
						//drSentDao.updateOpenCount(drReportRequest.getSentId());
						drSentDaoForDML.updateOpenCount(sentId); //(drReportRequest.getSentId());
						//					drSentDao.updateClickCount(drReportRequest.getSentId());
					}
					//drSentDao.updateClickCount(drReportRequest.getSentId());
					drSentDaoForDML.updateClickCount(sentId); //(drReportRequest.getSentId());
					if (drReportRequest.getUrl().indexOf("http://") == -1
							&& (drReportRequest.getUrl().indexOf("https://") == -1)) {
						drReportRequest.setUrl("http://" + drReportRequest.getUrl().trim());
					} 
				}
			}else if(OCConstants.DR_REPORT_ACTION_TYPE_CLICK_SMS.equals(drReportRequest.getRequestedAction())) {
				logger.debug("clickSMS");
				if (sentId != null) {
					DRSMSSent drSmsSent = drSmsSentDao.findById(sentId);
					if (drSmsSent != null && drSmsSent.getOpens() == 0) {
						drSmsSentDaoForDML.updateOpenCount(sentId);
					}
					drSmsSentDaoForDML.updateClickCount(sentId);
					if (drReportRequest.getUrl().indexOf("http://") == -1
							&& (drReportRequest.getUrl().indexOf("https://") == -1)) {
						drReportRequest.setUrl("http://" + drReportRequest.getUrl().trim());
					} 
				}
			}else if(OCConstants.DR_REPORT_ACTION_TYPE_WEBPAGE.equals(drReportRequest.getRequestedAction())) {
				logger.debug("webpage link==="+sentId);
				if(sentId != null){
					
					DRSent drSent = drSentDao.findById(sentId);
					if(drSent != null && drSent.getHtmlStr() != null){
						logger.debug("webpage link===2");
						BaseResponseObject baseResponseObject = new BaseResponseObject();
						baseResponseObject.setResponseObject(drSent.getHtmlStr());
						return baseResponseObject;
					}
				}
			}else if(OCConstants.DR_REPORT_ACTION_TYPE_DR_SMS.equals(drReportRequest.getRequestedAction())) {
				logger.debug("DRsms link==="+sentId);
				if(sentId != null){
					
					DRSMSSent drSmsSent = drSmsSentDao.findById(sentId);
					/*if(drSmsSent != null && drSmsSent.getOpens() == 0) {
						drSmsSentDaoForDML.updateOpenCount(drReportRequest.getSentId());
					}*/
					if(drSmsSent != null && drSmsSent.getHtmlStr() != null){
						logger.debug("DRsms link===2");
						BaseResponseObject baseResponseObject = new BaseResponseObject();
						baseResponseObject.setResponseObject(drSmsSent.getHtmlStr());
						return baseResponseObject;
					}
				}
			}else if(OCConstants.DR_REPORT_ACTION_TYPE_WA.equals(drReportRequest.getRequestedAction())) {
				logger.debug("DR over WA link==="+sentId);
				if(sentId != null){
					
					DRSMSChannelSent drSmsChannelSent = drSmsChannelSentDao.findById(sentId);
					
					if(drSmsChannelSent != null && drSmsChannelSent.getHtmlContent() != null){
						logger.debug("DRsms link===2");
						BaseResponseObject baseResponseObject = new BaseResponseObject();
						baseResponseObject.setResponseObject(drSmsChannelSent.getHtmlContent());
						return baseResponseObject;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processDRReportRequest::::: ", e);
		}

		return null;
	}

}
