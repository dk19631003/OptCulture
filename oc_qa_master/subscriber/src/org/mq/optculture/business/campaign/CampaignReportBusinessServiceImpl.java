package org.mq.optculture.business.campaign;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.CampaignSent;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Clicks;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.NotificationClicks;
import org.mq.marketer.campaign.beans.Opens;
import org.mq.marketer.campaign.beans.ShareSocialNetworkLinks;
import org.mq.marketer.campaign.beans.TemplateCategory;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.service.UpdateEmailReports;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.ShareSocialNetworkLinksDao;
import org.mq.marketer.campaign.dao.ShareSocialNetworkLinksDaoForDML;
import org.mq.marketer.campaign.dao.UnsubscribesDao;
import org.mq.marketer.campaign.dao.UnsubscribesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptUrlParameters;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.business.helper.CampaignReportUpdateHelper;
import org.mq.optculture.business.helper.UpdateResubscribeHelper;
import org.mq.optculture.business.helper.UpdateUnsubscribeHelper;
import org.mq.optculture.business.rabbitMQ.PublishQueue;
import org.mq.marketer.campaign.beans.CommunicationEvent;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.campaign.CampaignReportRequest;
import org.mq.optculture.model.campaign.CampaignReportResponse;
import org.mq.optculture.model.campaign.EmailPdfResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;

public class CampaignReportBusinessServiceImpl implements CampaignReportBusinessService{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	static final String DIV_TEMPLATE = PropertyUtil.getPropertyValue("divTemplate");

	public static Queue<Opens> opensQueue  =  new LinkedList<Opens>();
	public static Queue<Clicks> clicksQueue =  new LinkedList<Clicks>();
	public static Queue<NotificationClicks> notificationClicksQueue =  new LinkedList<NotificationClicks>();
	private static final String ERROR_RESPONSE = 
			"<div style='font-size:15px;color:blue;font-family:verdena;" +
					"font-weight:bold;margin-top:50px'>The Web Page is expired</div>";
	
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		try {
			CampaignReportRequest campaignReportRequest = (CampaignReportRequest) baseRequestObject;
			CampaignReportBusinessService campaignReportBusinessService = (CampaignReportBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.CAMPAIGN_REPORT_BUSINESS_SERVICE);
			
			
			if(campaignReportRequest != null){
				
				logger.info("User Id "+campaignReportRequest.getUserId()+" \t Sent Id "+campaignReportRequest.getSentId()+"\t Cid "+campaignReportRequest.getcId());
			}
			
			if(campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_OPEN) == 0){
				campaignReportResponse = campaignReportBusinessService.processOpenUpdate(campaignReportRequest);
			}
			else if(campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_CLICK) == 0) {
				campaignReportResponse = campaignReportBusinessService.processClickUpdate(campaignReportRequest);
			}
			else if(campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_WEBPAGE) == 0) {
				campaignReportResponse = campaignReportBusinessService.processWeblink(campaignReportRequest);
			}
			else if(campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_RESUBSCRIBE) == 0) {
				campaignReportResponse = campaignReportBusinessService.processResubscribe(campaignReportRequest);
			}
			else if(campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_UNSUBSCRIBE) == 0) {
				campaignReportResponse = campaignReportBusinessService.processUnsubscribeRequest(campaignReportRequest);
			}
			else if (campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_UNSUB_REASON) == 0) {
				campaignReportResponse = campaignReportBusinessService.processUnsubscribeUpdate(campaignReportRequest);
			} 
			else if(campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_FARWARD) == 0){
				campaignReportResponse = campaignReportBusinessService.processFarwardUpdate(campaignReportRequest);
			}
			else if (campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_UPDATE_SUBSCRPTION) == 0) {
				campaignReportResponse = campaignReportBusinessService.processUpdateSubscriptionlink(campaignReportRequest);
			}
			else if(campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_TWEET_ON_TWITTER) == 0) {
				campaignReportResponse = campaignReportBusinessService.processShareOnTwitter(campaignReportRequest);
			}
			else if(campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_SHARED_ON_FB) == 0) {
				campaignReportResponse = campaignReportBusinessService.processShareOnFacebook(campaignReportRequest);
			}
			else if(campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_SHARE_LINK) == 0){
				campaignReportResponse = campaignReportBusinessService.processShareLink(campaignReportRequest);
			}
			else if(campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_COUPONCODE) == 0) {
				campaignReportResponse = campaignReportBusinessService.processCouponBarCode(campaignReportRequest);
			}
			else if(campaignReportRequest.getAction().compareTo(OCConstants.REPORT_ACTION_TYPE_PDF) == 0) {
				campaignReportResponse = campaignReportBusinessService.processEmailTopdf(campaignReportRequest);
			}
			else if(campaignReportRequest.getAction().compareTo(OCConstants.UPDATE_REPORT_ACTION_NOTIFICATIONCLICK) == 0) {
				campaignReportResponse = campaignReportBusinessService.processNotificationClickUpdate(campaignReportRequest);
			}
			if(opensQueue.size() >= 200 || clicksQueue.size() >= 200 ||notificationClicksQueue.size() >= 200 ) {

//				UpdateEmailReports updateEmailReports = (UpdateEmailReports) ServiceLocator.getInstance().getServiceByName(OCConstants.UPDATE_EMAIL_REPORTS);
				UpdateEmailReports updateEmailReports = (UpdateEmailReports) ServiceLocator.getInstance().getBeanByName(OCConstants.UPDATE_EMAIL_REPORTS);
				if(!updateEmailReports.isRunning()) {
					logger.debug("processor is not running , try to ping it....");
					updateEmailReports.run();
					return campaignReportResponse;
				}


			}
		} catch (Exception e) {
			
			logger.error("Exception ::" , e);
			
			throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
		}
		return campaignReportResponse;
	}
	
	@Override
	public CampaignReportResponse processOpenUpdate(
			CampaignReportRequest campaignReportRequest)
					throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_OPEN_IMAGE);
		Long sentId = null;
		try {
			if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_OLD_REQUESTED_ACTION) == 0) {
				
				logger.info("Processing Old Action Type ::");
				
				sentId = Long.parseLong(campaignReportRequest.getSentId());
				logger.info("Decrypted SentId ..................:"+sentId);
		
			}
			else if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_NEW_REQUESTED_ACTION) == 0) {
				logger.info("Processing new Action Type ::");
				logger.info("Before decrypting SentId:"+campaignReportRequest.getSentId() +"CId ::"+campaignReportRequest.getcId());
				sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getSentId()));
				logger.info("After decrypting Type SentId :"+sentId);
			}
		} catch (Exception e) {
			logger.error("CrId ::"+campaignReportRequest.getCrId() +"CId ::"+campaignReportRequest.getcId() +"UserId ::"+campaignReportRequest.getUserId());
			logger.error("** Exception : Invalid sentId ::"+campaignReportRequest.getSentId() +" for the requested action - "+campaignReportRequest.getAction());
			throw new BaseServiceException("Exception occured while processing processOpenUpdate::::: ", e);
		} 	
		
		updateOpens(campaignReportRequest,sentId);
		
		return campaignReportResponse;
	}
	
	private void updateOpens(CampaignReportRequest campaignReportRequest,
			Long sentId) throws BaseServiceException {
		
		synchronized (opensQueue) {

			Opens opens = new Opens(sentId, Calendar.getInstance(), campaignReportRequest.getUserAgent());
			opensQueue.offer(opens);

		}
		
	}

	@Override
	public CampaignReportResponse processClickUpdate(
			CampaignReportRequest campaignReportRequest)
					throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_CLICK_REDIRECT);
		Long sentId = null;
		try {
			if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_OLD_REQUESTED_ACTION) == 0) {
				logger.info("Processing Old Action Type ::");
				logger.info("Old Type sent Id** ::"+campaignReportRequest.getSentId());
				sentId = Long.parseLong(campaignReportRequest.getSentId());
			}
			else if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_NEW_REQUESTED_ACTION) == 0) {
				logger.info("Processing new Action Type ::");
				logger.info("Before decrypting SentId:"+campaignReportRequest.getSentId() +"CId ::"+campaignReportRequest.getcId());
				sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getSentId()));
				logger.info("After decrypting SentId:"+sentId);
			}
		} catch (Exception e) {
			logger.error("CrId ::"+campaignReportRequest.getCrId() +"CId ::"+campaignReportRequest.getcId() +"UserId ::"+campaignReportRequest.getUserId());
			logger.error("** Exception : Invalid sentId ::"+campaignReportRequest.getSentId() +" for the requested action - "+campaignReportRequest.getAction());
			throw new BaseServiceException("Exception occured while processing processClickUpdate::::: ", e);
		} 	
		
		if(campaignReportRequest.getUrl()==null) {
			if(logger.isDebugEnabled()) logger.debug("URL is null");
			campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
			return campaignReportResponse;
		} 
		
		if(campaignReportRequest.getUrl().contains("&amp;")) {

			campaignReportRequest.setUrl(campaignReportRequest.getUrl().replace("&amp;", "&"));
		}
		
		Clicks clicks = new Clicks(sentId, campaignReportRequest.getUrl(), Calendar.getInstance(), campaignReportRequest.getUserAgent());
		
		synchronized (clicksQueue) {

			clicksQueue.add(clicks);
		}
		
		if(campaignReportRequest.getUrl().indexOf("http://") == -1 && (campaignReportRequest.getUrl().indexOf("https://") == -1 )){
			campaignReportRequest.setUrl("http://" + campaignReportRequest.getUrl().trim());
		}
		campaignReportResponse.setUrlStr(campaignReportRequest.getUrl());
		return campaignReportResponse;
	}
	
	@Override
	public CampaignReportResponse processWeblink(
			CampaignReportRequest campaignReportRequest)
					throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_WEB_LINK);
		Long sentId = null;
		Long cId = null;
		try {
			if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_OLD_REQUESTED_ACTION) == 0) {
				logger.info("Processing Old Action Type ::");
				sentId = Long.parseLong(campaignReportRequest.getSentId());
				cId = Long.parseLong(campaignReportRequest.getcId());
			}
			else if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_NEW_REQUESTED_ACTION) == 0) {
				logger.info("Processing new Action Type ::");
				logger.info("Before decrypting SentId:"+campaignReportRequest.getSentId() +"CId ::"+campaignReportRequest.getcId());
				sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getSentId()));
				cId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getcId()));
				logger.info("After decrypting SentId:"+sentId);
				logger.info("After decrypting Cid:"+cId);
			}
		} catch (Exception e) {
			logger.error("CrId ::"+campaignReportRequest.getCrId() +"CId ::"+campaignReportRequest.getcId() +"UserId ::"+campaignReportRequest.getUserId());
			logger.error("** Exception : Invalid sentId ::"+campaignReportRequest.getSentId() +" for the requested action - "+campaignReportRequest.getAction());
			throw new BaseServiceException("Exception occured while processing processWeblink::::: ", e);
		} 	
		
		try {
			CampaignSentDao campaignSentDao = (CampaignSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CAMPAIGN_SENT_DAO);
			CampaignsDao campaignDao = (CampaignsDao) ServiceLocator.getInstance().getDAOByName("campaignsDao");
			CampaignSent campaignSent = campaignSentDao.findById(sentId);
			
			if(campaignSent == null) {
				logger.warn(" CampaignSent is not found for the sentId :"+sentId);
				campaignReportResponse.setErrorResponse(ERROR_RESPONSE);
				campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
				return campaignReportResponse;
			}
			CampaignReport campaignReport = campaignSent.getCampaignReport();
			
			if(campaignReport == null) {
				logger.warn(" Campaign Report object not found for sentId :"+sentId);
				campaignReportResponse.setErrorResponse(ERROR_RESPONSE);
				campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
				return campaignReportResponse;
			}
			
			updateOpens(campaignReportRequest, sentId);
			
			String htmlContent = campaignReport.getContent();
			
			if(htmlContent == null) {
				campaignReportResponse.setErrorResponse(ERROR_RESPONSE);
				campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
				return campaignReportResponse;
			}
			
			
			htmlContent = htmlContent.replaceFirst("<font (.*?)<a[^>]*>(.*?)</a></font>", "");
			String openTrackURl = PropertyUtil.getPropertyValue("OpenTrackUrl").replace("|^", "[").replace("^|", "]");
			htmlContent = htmlContent.replace(openTrackURl, PropertyUtil.getPropertyValue("subscriberUrl") + "img/transparent.gif");

			Long userId = campaignReport.getUser().getUserId();
			htmlContent = htmlContent.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(sentId.toString()));
			htmlContent = htmlContent.replace("[cId]", EncryptDecryptUrlParameters.encrypt(cId.toString()));
			htmlContent = htmlContent.replace("[crId]", campaignReport.getCrId().toString());
			htmlContent = htmlContent.replace("[email]", campaignSent.getEmailId());
			
			String placeHoldersStr = campaignReport.getPlaceHoldersStr();
			
			/********replace contact's placeholders***************************/
			if(placeHoldersStr != null && !placeHoldersStr.trim().isEmpty()) {
				String contactPhValStr = campaignSent.getContactPhValStr();
				logger.info("contactPlhValStr is "+contactPhValStr);
				CampaignReportUpdateHelper campaignReportUpdateHelper = new CampaignReportUpdateHelper();
				htmlContent = campaignReportUpdateHelper.replacePlaceHolders(placeHoldersStr,contactPhValStr,htmlContent);

			}
			
			Campaigns campaign = campaignDao.findByCampaignId(campaignSent.getCampaignId());
			String webLinkText = campaign.getWebLinkText();
			String webLinkUrlText = campaign.getWebLinkUrlText();

			String weblinkUrl =  PropertyUtil.getPropertyValue("weblinkUrl");
			weblinkUrl =weblinkUrl.replace("|^", "[").replace("^|", "]");
			String webUrl = null;
			if(webLinkText!=null && !webLinkText.isEmpty() && webLinkUrlText!=null && !webLinkUrlText.isEmpty()) {
				webUrl =  DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, webLinkText + " <a href='" + weblinkUrl + "'>"+ webLinkUrlText + "</a>");
			} 
		
			webUrl = webUrl.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(sentId.toString()));
			webUrl = webUrl.replace("[cId]", EncryptDecryptUrlParameters.encrypt(cId.toString()));
			htmlContent = htmlContent.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(sentId.toString()));
			htmlContent = htmlContent.replace("[cId]", EncryptDecryptUrlParameters.encrypt(cId.toString()));
			htmlContent = htmlContent.replace("[userId]", EncryptDecryptUrlParameters.encrypt(userId.longValue()+"" ));
			htmlContent = htmlContent.replace("[email]", campaignSent.getEmailId());
			htmlContent = htmlContent.replace("[crId]", campaignReport.getCrId().toString());

			/**********************************************************************************************/
			//replaced the oldTrackUrl with the new
			if(htmlContent.contains(PropertyUtil.getPropertyValue(Constants.OLD_TRACK_URL))) {
				htmlContent = htmlContent.replace(PropertyUtil.getPropertyValue(Constants.OLD_TRACK_URL), PropertyUtil.getPropertyValue(Constants.NEW_TRACK_URL));
			}
			htmlContent = htmlContent.replace(webUrl, "");
			campaignReportResponse.setHtmlContent(htmlContent);
		} catch (Exception e) {
			logger.error("Exception :: ",e);
			throw new BaseServiceException("Exception occured while processing processWeblink::::: ", e);
		}
		
		return campaignReportResponse;
		
	}

	@Override
	public CampaignReportResponse processResubscribe(
			CampaignReportRequest campaignReportRequest)
			throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_RESUBSCRIBE);
		
		String tempEmailId = null;
		Long tempUserId = null;
		try {
			if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_OLD_REQUESTED_ACTION) == 0) {
				logger.info("Processing Old Action Type ::");
				logger.info("Before decrypting EmailId:"+campaignReportRequest.getEmailId());
				logger.info("Before decrypting Userid:"+campaignReportRequest.getUserId());
				tempEmailId = campaignReportRequest.getEmailId();
				tempUserId = Long.parseLong(campaignReportRequest.getUserId());
			}
			else if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_NEW_REQUESTED_ACTION) == 0) {
				logger.info("Processing new Action Type ::");
				logger.info("Before decrypting EmailId:"+campaignReportRequest.getEmailId());
				logger.info("Before decrypting Userid:"+campaignReportRequest.getUserId());
				tempEmailId = EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getEmailId());
				tempUserId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getUserId()));
				logger.info("After decrypting EmailId:"+tempEmailId);
				logger.info("After decrypting Userid:"+tempUserId);
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
			throw new BaseServiceException("Exception occured while processing processResubscribe::::: ", e);
		}

		if(tempEmailId == null || tempEmailId.trim().length() == 0) {
			campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
			return campaignReportResponse;
		}
		
		else if(tempUserId == null ) {
			campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
			return campaignReportResponse;
		}

		//Find the User Object if exist

		//Update the Contact Status 
		try {
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			contactsDaoForDML.updateEmailStatusByUserId(tempEmailId, tempUserId, "Active");

			//Remove Contact From Unsubscribe
			UnsubscribesDao unsubscribesDao = (UnsubscribesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.UNSUBSCRIBES_DAO);
			UnsubscribesDaoForDML unsubscribesDaoForDML = (UnsubscribesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UNSUBSCRIBES_DAO_FOR_DML);
			unsubscribesDaoForDML.deleteByEmailIdUserId(tempEmailId, tempUserId);
		} catch (Exception e) {
			logger.error("Exception ::", e);
			throw new BaseServiceException("Exception occured while processing processResubscribe::::: ", e);
		}
		
		return campaignReportResponse;
	}

	@Override
	public CampaignReportResponse processUnsubscribeRequest(
			CampaignReportRequest campaignReportRequest)
			throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_UNSUBSCRIBE_REQ);
		Long sentId = null;
		Long userId = null;
//		String emailId = "";
//		Long crId = 0L;
//		Long cId = 0L;
		try {
			
			logger.info("entered processUnsubscribeRequest  flow ");

			
			if(campaignReportRequest.getSentId() == null && campaignReportRequest.getActionType().equalsIgnoreCase("updatedAction")) {
					
					logger.info("Entered in 3.0 flow ");
					campaignReportResponse.setResponseType("PlatformUnsubscribe");


					campaignReportResponse.setEmailId(campaignReportRequest.getEmailId());
					campaignReportResponse.setCrId(Long.parseLong(campaignReportRequest.getCrId()));
					campaignReportResponse.setUserId(Long.parseLong(campaignReportRequest.getUserId()));
					campaignReportResponse.setCampaignId(campaignReportRequest.getCampaignId());
					campaignReportResponse.setcId(Long.parseLong(campaignReportRequest.getcId()));
					
					logger.info("UserId : "+campaignReportResponse.getUserId()+", crID : "+campaignReportResponse.getCrId()+" , emailId : "+campaignReportResponse.getEmailId());

					return campaignReportResponse;
						

			}
			if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_OLD_REQUESTED_ACTION) == 0) {
				userId = Long.parseLong(campaignReportRequest.getUserId());
				sentId = Long.parseLong(campaignReportRequest.getSentId());
				
			}
			else if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_NEW_REQUESTED_ACTION) == 0) {
				sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getSentId()));
				userId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getUserId()));
			//	logger.debug("Processed SentId is .........:"+sentId);
			}
//			else if( campaignReportRequest.getActionType().compareTo("updatedAction")==0 && campaignReportRequest.getQueryString().equals("hash")) {
//				
//				//decoded as they will be encoded.
//		logger.info("Entered in 3.0 flow , userId : "+userId+", crID : "+crId+" , emailId : "+emailId);
//
//			 userId = Long.parseLong(String.valueOf(Base64.decodeBase64(String.valueOf(userId))));
//			 crId = Long.parseLong(String.valueOf(Base64.decodeBase64(campaignReportRequest.getCrId())));
//			 emailId = String.valueOf(Base64.decodeBase64(campaignReportRequest.getEmailId()));
//			 cId = Long.parseLong(String.valueOf(Base64.decodeBase64(campaignReportRequest.getcId())));
//			 
//			 logger.info("UserId : "+userId+", crID : "+crId+" , emailId : "+emailId);
//			 
//			//emailId
//			}
		} catch (Exception e) {
			logger.warn(" userId not found in the querystring");
			throw new BaseServiceException("Exception occured while processing processUnsubscribeRequest::::: ", e);
		}
		CampaignSent campaignSent =null;
		try {
			CampaignSentDao campaignSentDao = (CampaignSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CAMPAIGN_SENT_DAO);
			campaignSent = campaignSentDao .findById(sentId);
			if(campaignSent == null) {
				
				campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
				return campaignReportResponse;
			}
			

		} catch (Exception e) {
			logger.error("Exception : Problem while updating the opens \n", e);
			throw new BaseServiceException("Exception occured while processing processUnsubscribeRequest::::: ", e);
		}


		campaignReportResponse.setEmailId(campaignSent.getEmailId());
		campaignReportResponse.setUserId(userId);
		campaignReportResponse.setSentId(sentId);
	//	logger.debug("returning campaignReportResponse"+campaignReportResponse);
		return campaignReportResponse;
	}

	@Override
	public CampaignReportResponse processUnsubscribeUpdate(
			CampaignReportRequest campaignReportRequest)
			throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_UNSUBSCRIBE_UPDATE);
		try {
			
			logger.info("In side processUnsubscribeUpdate");
			
			logger.info("UserId : "+campaignReportRequest.getUserId()+", crID : "+campaignReportRequest.getCrId()+" , emailId : "+campaignReportRequest.getEmailId()+ 
					"campaignId : "+campaignReportRequest.getCampaignId()+ " cId : "+campaignReportRequest.getcId());

			Long userId = Long.parseLong(campaignReportRequest.getUserId().trim());
		//	String reasonStr = campaignReportRequest.getReason();
			String emailId = campaignReportRequest.getEmailId();
			String crId = campaignReportRequest.getCrId();
			Long cId = Long.parseLong(campaignReportRequest.getcId());

		//	CampaignReportUpdateHelper campaignReportUpdateHelper = new CampaignReportUpdateHelper();
			logger.info("campaignId : "+campaignReportRequest.getCampaignId());

			campaignReportResponse.setUserId(userId);
			campaignReportResponse.setEmailId(emailId);
			
			
			
			if(campaignReportRequest.getSource().equalsIgnoreCase("Platform")) {
			
			campaignReportResponse.setCampaignId(campaignReportRequest.getCampaignId());
			campaignReportResponse.setCrId(Long.parseLong(crId));
			campaignReportResponse.setcId(cId);
			
			CommunicationEvent event = new CommunicationEvent(Long.parseLong(crId),Long.parseLong(campaignReportRequest.getCampaignId()), emailId, "Unsubscribed", LocalDateTime.now(), "", "Email", userId, cId);

			logger.info("Communication event object created successfully"+event.getCampaignId()+" cId"+event.getContactId()+" email : "+event.getRecipient());
			
			String responseJson = new Gson().toJson(event, CommunicationEvent.class);
			
			Map<String, Object> bindingHeaders = new HashMap<String,Object>();
		//	bindingHeaders.put("routingKey", "communication_event_key");
			
			logger.info("pushing event object ");

			PublishQueue.publish("communication_event_queue","communication_event_exchange",bindingHeaders,responseJson);//queue name,excname,key and message
			
			logger.info("consumed event object ");
			
			
			return campaignReportResponse;
			//TODO send 3.0.	
			
			}
			
			Long sentId = Long.parseLong(campaignReportRequest.getSentId().trim());
			campaignReportResponse.setSentId(sentId);

			//Call Thread.
			if(Constants.UNSUBSCRIBE_REQUEST_VALUE_UNSUB_UPDATE.equals(campaignReportRequest.getUnSubscribeReqType())){
				UpdateUnsubscribeHelper updateUnsubscribeHelper =  new UpdateUnsubscribeHelper(campaignReportRequest);
				updateUnsubscribeHelper.start();
			}
			else if(Constants.UNSUBSCRIBE_REQUEST_VALUE_RESUB.equals(campaignReportRequest.getUnSubscribeReqType())){
				UpdateResubscribeHelper updateResubscribeHelper = new UpdateResubscribeHelper(campaignReportRequest);
				updateResubscribeHelper.start();
			}

		} catch (Exception e) {
			logger.error("Exception ::" , e);
			throw new BaseServiceException("Exception occured while processing processUnsubscribeUpdate::::: ", e);
		}
	//	logger.info("returning response campaignReportResponse...........");
		return campaignReportResponse;
	}

	
	@Override
	public CampaignReportResponse processFarwardUpdate(
			CampaignReportRequest campaignReportRequest)
			throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_FARWARD_UPDATE);
		Long sentId = null;
		try {
			if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_OLD_REQUESTED_ACTION) == 0) {
				logger.info("Processing Old Action Type ::");
				logger.info("Old SentId ** :"+campaignReportRequest.getSentId());
				sentId = Long.parseLong(campaignReportRequest.getSentId());
			}
			else if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_NEW_REQUESTED_ACTION) == 0) {
				logger.info("Processing new Action Type ::");
				logger.info("Before Decrypting  SentId ## :"+campaignReportRequest.getSentId());
				sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getSentId()));
				logger.info("After Decrypting  SentId ## :"+sentId);
			}
		} catch (Exception e) {
			logger.error("** Exception : Invalid sentId ::"+campaignReportRequest.getSentId()+"  for the requested action - "+campaignReportRequest.getAction());
			throw new BaseServiceException("Exception occured while processing processFarwardUpdate::::: ", e);
		} 	
		
		try {
			CampaignSentDao campaignSentDao = (CampaignSentDao) ServiceLocator.getInstance().getDAOByName("campaignSentDao");
			CampaignSent campaignSent = campaignSentDao .findById(sentId);
			if(campaignSent == null) {
				campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
				return campaignReportResponse;
			}
			campaignReportResponse.setCampaignSent(campaignSent);
		} catch (Exception e) {
			logger.error("Exception ::", e);
			throw new BaseServiceException("Exception occured while processing processFarwardUpdate::::: ", e);
		}
		return campaignReportResponse;
	}

	@Override
	public CampaignReportResponse processUpdateSubscriptionlink(
			CampaignReportRequest campaignReportRequest)
			throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_UPDATE_SUBSCRPTION);
		Long sentId = null;
		Long cId = null;
		try {
			if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_OLD_REQUESTED_ACTION) == 0) {
				logger.info("Processing Old Action Type ::");
				logger.info("Old SentId ** :"+campaignReportRequest.getSentId());
				sentId = Long.parseLong(campaignReportRequest.getSentId());
				cId = Long.parseLong(campaignReportRequest.getcId());
			}
			else if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_NEW_REQUESTED_ACTION) == 0) {
				logger.info("Processing new Action Type ::");
				logger.info("Before Decrypting  SentId ## :"+campaignReportRequest.getSentId());
				sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getSentId()));
				cId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getcId()));
				logger.info("After Decrypting  SentId ## :"+sentId);
			}
		} catch (Exception e) {
			logger.error("** Exception : Invalid sentId "+campaignReportRequest.getSentId()+" for the requested action - "+campaignReportRequest.getAction());
			throw new BaseServiceException("Exception occured while processing processUpdateSubscriptionlink::::: ", e);
		} 	
		try {
			ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			Contacts contact = contactsDao.findById(cId);
			if(contact == null) {
				campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
				return campaignReportResponse;
			}
			
			Users user =contact.getUsers();
			campaignReportResponse.setUser(user);
			campaignReportResponse.setcId(cId);
			campaignReportResponse.setSentId(sentId);
			
		} catch (Exception e) {
			logger.error("Exception :: ",e);
			throw new BaseServiceException("Exception occured while processing processUpdateSubscriptionlink::::: ", e);
		}
		return campaignReportResponse;
	}

	
	@Override
	public CampaignReportResponse processShareOnTwitter(
			CampaignReportRequest campaignReportRequest)
			throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_REDIRECT_SHARE);
		Long sentId = null;
		try {
			if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_OLD_REQUESTED_ACTION) == 0) {
				logger.info("Processing Old Action Type ::");
				logger.info("Old SentId ** :"+campaignReportRequest.getSentId());
				sentId = Long.parseLong(campaignReportRequest.getSentId());
			}
			else if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_NEW_REQUESTED_ACTION) == 0) {
				logger.info("Processing new Action Type ::");
				logger.info("Before Decrypting  SentId ## :"+campaignReportRequest.getSentId());
				sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getSentId()));
				logger.info("After Decrypting  SentId ## :"+sentId);
				
			}
		} catch (Exception e) {
			logger.error("** Exception : Invalid sentId :: "+campaignReportRequest.getSentId()+" for the requested action - "+campaignReportRequest.getAction());
			throw new BaseServiceException("Exception occured while processing processShareOnTwitter::::: ", e);
		} 	
		try {
			String requestCmpelteUrl = campaignReportRequest.getRequestUrl().toString()+"?"+campaignReportRequest.getQueryString();
			requestCmpelteUrl = requestCmpelteUrl.replace("tweetOnTwitter", "shareLink");
			logger.info("afetr replceing  complete url is ::"+requestCmpelteUrl);

			String urlEncodedData = URLEncoder.encode(requestCmpelteUrl,"UTF-8");

			String shareUrl =  "https://twitter.com/share?url="+urlEncodedData;
			logger.info("afetr replceing  complete url is ::"+shareUrl);


			Long crId = Long.parseLong(campaignReportRequest.getCrId());

			ShareSocialNetworkLinksDao shareSocialNetworkLinksDao = (ShareSocialNetworkLinksDao) ServiceLocator.getInstance().getDAOByName("shareSocialNetworkLinksDao");
			ShareSocialNetworkLinksDaoForDML shareSocialNetworkLinksDaoForDML  = (ShareSocialNetworkLinksDaoForDML ) ServiceLocator.getInstance().getDAOForDMLByName("shareSocialNetworkLinksDaoForDML");

			ShareSocialNetworkLinks shareSocilaNetLinkObj = new ShareSocialNetworkLinks(sentId, crId, "Twitter", Calendar.getInstance());
			//shareSocialNetworkLinksDao.saveOrUpdate(shareSocilaNetLinkObj);
			shareSocialNetworkLinksDaoForDML.saveOrUpdate(shareSocilaNetLinkObj);

			campaignReportResponse.setUrlStr(shareUrl);

		} catch (Exception e) {
			logger.error("Exception ::", e);
			throw new BaseServiceException("Exception occured while processing processShareOnTwitter::::: ", e);
		}
		return campaignReportResponse;
	}

	
	@Override
	public CampaignReportResponse processShareOnFacebook(
			CampaignReportRequest campaignReportRequest)
			throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_REDIRECT_SHARE);
		Long sentId = null;
		try {
			if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_OLD_REQUESTED_ACTION) == 0) {
				logger.info("Processing Old Action Type ::");
				logger.info("Old SentId ** :"+campaignReportRequest.getSentId());
				sentId = Long.parseLong(campaignReportRequest.getSentId());
			}
			else if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_NEW_REQUESTED_ACTION) == 0) {
				logger.info("Processing new Action Type ::");
				logger.info("Before Decrypting  SentId ## :"+campaignReportRequest.getSentId());
				sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getSentId()));
				logger.info("After Decrypting  SentId ## :"+sentId);
			}
		} catch (Exception e) {
			logger.error("** Exception : Invalid sentId :: "+campaignReportRequest.getSentId()+" for the requested action - "+campaignReportRequest.getAction());
			throw new BaseServiceException("Exception occured while processing processShareOnFacebook::::: ", e);
		} 	
		try {
			String requestCmpelteUrl = campaignReportRequest.getRequestUrl().toString()+"?"+campaignReportRequest.getQueryString();
			requestCmpelteUrl = requestCmpelteUrl.replace("sharedOnFb", "shareLink");
			logger.info("afetr replceing  complete url is ::"+requestCmpelteUrl);

			String urlEncodedData = URLEncoder.encode(requestCmpelteUrl,"UTF-8");

			String shareUrl =  "http://www.facebook.com/sharer/sharer.php?u="+urlEncodedData;
			logger.info("afetr replceing  complete url is ::"+shareUrl);


			Long crId = Long.parseLong(campaignReportRequest.getCrId());

			ShareSocialNetworkLinksDao shareSocialNetworkLinksDao = (ShareSocialNetworkLinksDao) ServiceLocator.getInstance().getDAOByName("shareSocialNetworkLinksDao");
			ShareSocialNetworkLinksDaoForDML shareSocialNetworkLinksDaoForDML = (ShareSocialNetworkLinksDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName("shareSocialNetworkLinksDaoForDML");
			ShareSocialNetworkLinks shareSocilaNetLinkObj = new ShareSocialNetworkLinks(sentId, crId, "Facebook", Calendar.getInstance());
	
			//shareSocialNetworkLinksDao.saveOrUpdate(shareSocilaNetLinkObj);
			shareSocialNetworkLinksDaoForDML.saveOrUpdate(shareSocilaNetLinkObj);

			campaignReportResponse.setUrlStr(shareUrl);
		} catch (Exception e) {
			logger.error("Exception ::", e);
			throw new BaseServiceException("Exception occured while processing processShareOnFacebook::::: ", e);
		}
		return campaignReportResponse;
	}

	
	@Override
	public CampaignReportResponse processShareLink(
			CampaignReportRequest campaignReportRequest)
			throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_SHARE_LINK);
		Long sentId = null;
		try {
			if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_OLD_REQUESTED_ACTION) == 0) {
				logger.info("Processing Old Action Type ::");
				logger.info("Old SentId ** :"+campaignReportRequest.getSentId());
				sentId = Long.parseLong(campaignReportRequest.getSentId());
			}
			else if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_NEW_REQUESTED_ACTION) == 0) {
				logger.info("Processing new Action Type ::");
				logger.info("Before Decrypting  SentId ## :"+campaignReportRequest.getSentId());
				sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getSentId()));
				logger.info("After Decrypting  SentId ## :"+sentId);
			}
		} catch (Exception e) {
			logger.error("** Exception : Invalid sentId :: "+campaignReportRequest.getSentId()+" for the requested action - "+campaignReportRequest.getAction());
			throw new BaseServiceException("Exception occured while processing processShareLink::::: ", e);
		} 	
		
		try {
			CampaignSentDao campaignSentDao = (CampaignSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CAMPAIGN_SENT_DAO);
			CampaignSent campaignSent = campaignSentDao.findById(sentId);
			
			if(campaignSent == null) {
				logger.warn(" CampaignSent is not found for the sentId :"+sentId);
				campaignReportResponse.setErrorResponse(ERROR_RESPONSE);
				campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
				return campaignReportResponse;
			}
			else {
				CampaignReport campaignReport = campaignSent.getCampaignReport();
				String htmlContent = campaignReport.getContent();

				Long campainId =  campaignSent.getCampaignId();
				CampaignsDao campaignsDao =(CampaignsDao) ServiceLocator.getInstance().getDAOByName("campaignsDao");
				Campaigns campaign = campaignsDao.findByCampaignId(campainId);

				boolean oldCampSentFlag = false;
				if(htmlContent.contains(PropertyUtil.getPropertyValue(Constants.OLD_TRACK_URL))) {
					htmlContent = htmlContent.replace(PropertyUtil.getPropertyValue(Constants.OLD_TRACK_URL), PropertyUtil.getPropertyValue(Constants.NEW_TRACK_URL));
					oldCampSentFlag = true;
				}
				
				String webLinkText = campaign.getWebLinkText();
				String webLinkUrlText = campaign.getWebLinkUrlText();

				String weblinkUrl =  PropertyUtil.getPropertyValue("weblinkUrl");
				//String optInWebLinktext =  PropertyUtil.getPropertyValue("optInWebLinktext");
				//String optInWebLinkUrltext =  PropertyUtil.getPropertyValue("optInWebLinkUrltext");
				weblinkUrl =weblinkUrl.replace("|^", "[").replace("^|", "]");
				String webUrl = null;
				if(webLinkText!=null && !webLinkText.isEmpty() && webLinkUrlText!=null && !webLinkUrlText.isEmpty()) {
					webUrl =  DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, webLinkText + " <a href='" + weblinkUrl + "'>"+ webLinkUrlText + "</a>");
				} 

				htmlContent = htmlContent.replace(webUrl, "");
				
				//delete the content of Unsubscribe link div from the html content
				String unSubUrl = PropertyUtil.getPropertyValue("unSubscribeUrl");
				unSubUrl =unSubUrl.replace("|^", "[").replace("^|", "]");
				if(oldCampSentFlag) {
					unSubUrl = unSubUrl.replace("[userId]", campaignReport.getUser().getUserId().toString());
				}
				else {
					unSubUrl = unSubUrl.replace("[userId]", EncryptDecryptUrlParameters.encrypt(campaignReport.getUser().getUserId().toString()));
				}
				
				String unsubDiv = PropertyUtil.getPropertyValue("unsubFooterText");
				unsubDiv = unsubDiv.replace("|^unsubUrl^|", unSubUrl);

				htmlContent =htmlContent.replace(unsubDiv, "");
				
				
				// remove update subscription link  and unsubscribe
				String upadteUnsubDiv = PropertyUtil.getPropertyValue("updateUnsubFooterText");
				upadteUnsubDiv = upadteUnsubDiv.replace("|^unsubUrl^|", unSubUrl);
				
				htmlContent = htmlContent.replace(upadteUnsubDiv, "");
										
				String updateSubsLink =  PropertyUtil.getPropertyValue("updateSubscriptionLink");
				updateSubsLink =updateSubsLink.replace("|^", "[").replace("^|", "]");
									
				String updateUnsubDiv = PropertyUtil.getPropertyValue("updateSubHTMLTxt");
				updateUnsubDiv = updateUnsubDiv.replace("|^updateSubSUrl^|", updateSubsLink);
				
				htmlContent = htmlContent.replace(updateUnsubDiv, "");

				if(htmlContent.contains("href='")){
					htmlContent = htmlContent.replaceAll("href='([^\"]+)'", "href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");
					
				}
				if(htmlContent.contains("href=\"")){
					htmlContent = htmlContent.replaceAll("href=\"([^\"]+)\"", "href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");
				}

				//contact home store and last purchase place holders replacement logic
				String placeHoldersStr = campaignReport.getPlaceHoldersStr();
				if(placeHoldersStr != null && !placeHoldersStr.trim().isEmpty()) {
					String contactPhValStr = campaignSent.getContactPhValStr();
					if(contactPhValStr != null) {

						String[] phTokenArr = contactPhValStr.split(Constants.ADDR_COL_DELIMETER);
						String keyStr = "";
						String ValStr = "";
						for (String phToken : phTokenArr) {
							keyStr = phToken.substring(0, phToken.indexOf(Constants.DELIMETER_DOUBLECOLON));
							if(!( keyStr.equalsIgnoreCase("[GEN_ContactHomeStore]") ) && ! (keyStr.equalsIgnoreCase("[GEN_ContactLastPurchasedStore]")) &&  !( keyStr.startsWith("[CC_"))  ){
								continue;
							}
							ValStr = phToken.substring(phToken.indexOf(Constants.DELIMETER_DOUBLECOLON)+Constants.DELIMETER_DOUBLECOLON.length());

							htmlContent = htmlContent.replace(keyStr, ValStr);

						}	

					}
				}
				htmlContent = htmlContent.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(sentId.toString()));
				
				campaignReportResponse.setHtmlContent(htmlContent);
			}
		} catch (Exception e) {
			logger.error("Exception :: ",e);
			throw new BaseServiceException("Exception occured while processing processShareLink::::: ", e);
		}
		
		return campaignReportResponse;
	}

	@Override
	public CampaignReportResponse processCouponBarCode(
			CampaignReportRequest campaignReportRequest)
			throws BaseServiceException {
		CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
		campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_BARCODE);
		BitMatrix bitMatrix = null;

		try {
			String couponcode = campaignReportRequest.getCode();
			String widthStr = campaignReportRequest.getWidth().trim();
			int width = Integer.parseInt(widthStr);
			String heightStr =campaignReportRequest.getHeight().trim();
			int height = Integer.parseInt(heightStr);

			String type = campaignReportRequest.getType();

			if(!Constants.barcodeTypes.contains(type)){
				campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
				return campaignReportResponse;
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();


			if(type.equals(Constants.COUP_BARCODE_QR)){
				bitMatrix = new QRCodeWriter().encode(couponcode, BarcodeFormat.QR_CODE, width, height,null);
			}
			else if(type.equals(Constants.COUP_BARCODE_AZTEC)){
				bitMatrix = new AztecWriter().encode(couponcode, BarcodeFormat.AZTEC, width, height);
			}
			else if(type.equals(Constants.COUP_BARCODE_LINEAR)){
				bitMatrix = new Code128Writer().encode(couponcode, BarcodeFormat.CODE_128, width, height,null);
			}
			else if(type.equals(Constants.COUP_BARCODE_DATAMATRIX)){
				bitMatrix = new DataMatrixWriter().encode(couponcode, BarcodeFormat.DATA_MATRIX, width, height,null);
			}

			if(bitMatrix == null){
				campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
				return campaignReportResponse;
			}
			campaignReportResponse.setBitMatrix(bitMatrix);
			
		} catch (Exception e) {
			logger.error("Exception :: ",e);
			throw new BaseServiceException("Exception occured while processing processCouponBarCode::::: ", e);
		}
		
		return campaignReportResponse;
	}
	
	@Override
	public CampaignReportResponse processEmailTopdf(CampaignReportRequest campaignReportRequest)throws BaseServiceException {
		CampaignReportResponse response = new CampaignReportResponse();
		EmailPdfResponseObject emailPdf = new EmailPdfResponseObject();
		final String USER_PARENT_DIR=PropertyUtil.getPropertyValue("usersParentDirectory");
        String PDF_CMD= PropertyUtil.getPropertyValueFromDB("pathToPhantomjs");
        File htmlFile = null;
         try {
        	String sId=campaignReportRequest.getSentId();
        	String type = campaignReportRequest.getType();
        	String htmlContentArray = null;
        	String htmlPath = null;
        	String pdfPath = null;
        	String pdfFileName = null;
        	String htfileName = null;
        	Calendar cal=Calendar.getInstance();
        	if(type.equals("autoEmail")) {
        		 EmailQueueDao emailQueue = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
        		 EmailQueue emailQueueData = emailQueue.findEqById(Long.parseLong(sId));
        		 htmlContentArray = emailQueueData.getMessage();
        		 pdfFileName="PDF_"+cal.getTimeInMillis()+"_"+sId+".pdf";
                 htfileName="HTML_"+cal.getTimeInMillis()+"_"+sId+".html";
                 GetUser.checkUserFolders(emailQueueData.getUser().getUserName());
        		 htmlPath= USER_PARENT_DIR+File.separator+emailQueueData.getUser().getUserName()+File.separator+OCConstants.campaign+File.separator+OCConstants.HTML+File.separator+htfileName;
        		 pdfPath= USER_PARENT_DIR+File.separator+emailQueueData.getUser().getUserName()+File.separator+OCConstants.campaign+File.separator+OCConstants.PDF+File.separator+pdfFileName;
        		 File myHtmlTemp = new File(htmlPath);
                 File parentDir = myHtmlTemp.getParentFile();
                     if(!parentDir.exists()) {
                      parentDir.mkdir();
                     }
                 File myPdfTemp = new File(pdfPath);
                 File pdfParentDir = myPdfTemp.getParentFile();
                     if(!pdfParentDir.exists()) {
                        pdfParentDir.mkdir();
                     }
        	}else if(type.equals("campaign")) {
        		 sId = EncryptDecryptUrlParameters.decrypt(sId.toString());
        		 CampaignSentDao campSentDao = (CampaignSentDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CAMPAIGN_SENT_DAO);
                 CampaignSent campSent = campSentDao.findById(Long.parseLong(sId));
                 if(campSent == null) {
                   response.setResponseType(OCConstants.REPORT_ACTION_TYPE_PDF);
                   return response;
                 }
                 pdfFileName ="PDF_"+cal.getTimeInMillis()+"_"+sId+".pdf";
                 htfileName="HTML_"+cal.getTimeInMillis()+"_"+sId+".html";
                 GetUser.checkUserFolders(campSent.getCampaignReport().getUser().getUserName());
                 String campPath = USER_PARENT_DIR+File.separator+campSent.getCampaignReport().getUser().getUserName()+File.separator+OCConstants.campaign;
                 File myHtmlTempCamp = new File(campPath);
                 if(!myHtmlTempCamp.exists() && !myHtmlTempCamp.isDirectory()) {
                	 myHtmlTempCamp.mkdir();
                 }
                 htmlPath= campPath+File.separator+OCConstants.HTML+File.separator+htfileName;
                 pdfPath= campPath+File.separator+OCConstants.PDF+File.separator+pdfFileName;
                 File myHtmlTemp = new File(htmlPath);
                 File parentDir = myHtmlTemp.getParentFile();
                     if(!parentDir.exists()) {
                      parentDir.mkdir();
                     }
                 File myPdfTemp = new File(pdfPath);
                 File pdfParentDir = myPdfTemp.getParentFile();
                     if(!pdfParentDir.exists()) {
                        pdfParentDir.mkdir();
                     }
                     htmlContentArray = prepareHtmlContent(campSent);
        	}
           
           String htmlContent =htmlContentArray;
                    if(htmlContent!=null){
                        String openurl = PropertyUtil.getPropertyValue("OpenTrackUrl");
                        String autoemailOpenurl = PropertyUtil.getPropertyValue("AutoEmailOpenTrackUrl");
                        String AUTO_EMAIL_FOOTER_TEXT = PropertyUtil.getPropertyValue("autoEMailFooterText");
                        openurl = openurl.replace("|^", "[").replace("^|", "]");
                        AUTO_EMAIL_FOOTER_TEXT = AUTO_EMAIL_FOOTER_TEXT.replace("|^", "[").replace("^|", "]");
                        autoemailOpenurl = autoemailOpenurl.replace("|^", "[").replace("^|", "]");
                        if(htmlContent.contains("&amp;"))
                            openurl = StringEscapeUtils.escapeHtml(openurl);
                        	htmlContent = htmlContent.replace(openurl,"");
				
                        if(htmlContent.contains("&amp;")) autoemailOpenurl =  StringEscapeUtils.escapeHtml(autoemailOpenurl); 
				  		AUTO_EMAIL_FOOTER_TEXT =AUTO_EMAIL_FOOTER_TEXT.replace("[AutoEmailOpenTrackUrl]", autoemailOpenurl);
                        htmlContent = htmlContent.replace(AUTO_EMAIL_FOOTER_TEXT," ");
                        
                        String emailtopdfUrl = PropertyUtil.getPropertyValue("PdfGenerationUrl");
                        emailtopdfUrl = emailtopdfUrl.replace("|^", "[").replace("^|", "]");
                        String PDFdivTemplate = PropertyUtil.getPropertyValue("DRPDFdivTemplate");
                        String pdfUrl = null;
                        
                        if(type.equals("campaign"))
                        	pdfUrl = PDFdivTemplate.replace(PlaceHolders.DIV_CONTENT,"To Download as a PDF <a href='" + emailtopdfUrl.concat("&type=campaign") + "'> click here</a>");
                        else if(type.equals("autoEmail"))
                        	pdfUrl = PDFdivTemplate.replace(PlaceHolders.DIV_CONTENT,"To Download as a PDF <a href='" + emailtopdfUrl.concat("&type=autoEmail") + "'> click here</a>");
                        
                        htmlContent = htmlContent.replace(pdfUrl, " ");
                        
                        String newHtmlContent=  htmlContent.replace("To Download as a PDF", " ").replace("click here", "");
                        
                        try {
    						Pattern  r = Pattern.compile("<\\w*[^>]*>(.[&nbsp;]+)<\\w*[^>]*>", Pattern.CASE_INSENSITIVE);
    						Matcher idm = r.matcher(newHtmlContent);
    					    while(idm.find()){
    					    	String matchedSpace = idm.group(0);
    					    	newHtmlContent = newHtmlContent.replace(matchedSpace, "<p></p>");
    						}
    					}catch(Exception e) {
    						logger.error("empty space "+e);
    					}
                        
                        htmlFile =new File(htmlPath);
                        BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile));
                        bw.write(newHtmlContent.trim());
                        bw.close(); 
                        try {
                          ProcessBuilder pb = new ProcessBuilder(PDF_CMD+"bin/phantomjs",PDF_CMD+"htmltoImage/htmltoPdf.js", htmlPath, pdfPath);
                          Process p = pb.start();
                          int exitVal = p.waitFor(); // do a wait here to prevent it running for ever
                          if (exitVal != 0) {
                            logger.error("EXIT-STATUS while processing pdf " + p.toString());
                          }
                          p.destroy();
                        }catch (Exception e) {
                          logger.error("Exception ::"+e);
                        }
                        emailPdf.setPdfName(pdfFileName);
                        emailPdf.setPdfFilePath(pdfPath);
                        emailPdf.setHtmlFilePath(htmlPath);
                   response.setResponseObject(emailPdf);
                   response.setResponseType(OCConstants.REPORT_ACTION_TYPE_PDF);
                   logger.info(" generated html and pdf files are deleted ");
                }else {
                	response.setResponseType(OCConstants.REPORT_ACTION_TYPE_PDF);
                	return response;
                }
        } catch ( Exception  e) {
          logger.error("Exception" ,e);
        }
		return response;
	}
	
private String prepareHtmlContent(CampaignSent campSent){
		
		try {
			CampaignsDao campaignsDao = (CampaignsDao) ServiceLocator.getInstance().getDAOByName("campaignsDao");
			
			Long sentId = campSent.getSentId().longValue(); 
			CampaignReport camprep= campSent.getCampaignReport();
			if(camprep == null) {
				if(logger.isWarnEnabled()) logger.warn(" Campaign Report object not found for sentId :"+sentId);
				logger.debug(ERROR_RESPONSE);
				return null;
			}
			
			String htmlStr = camprep.getContent();
			if(htmlStr == null) {
				logger.debug(ERROR_RESPONSE);
				return null;
			}
			
			Long userId = campSent.getCampaignReport().getUser().getUserId();
			Long campainId =  campSent.getCampaignId();
			Campaigns campaign = campaignsDao.findByCampaignId(campainId);
			
			// TODO for the campaigns already sent
			boolean oldCampSentFlag = false;
			if(htmlStr.contains(PropertyUtil.getPropertyValue(Constants.OLD_TRACK_URL))) {
				htmlStr = htmlStr.replace(PropertyUtil.getPropertyValue(Constants.OLD_TRACK_URL), PropertyUtil.getPropertyValue(Constants.NEW_TRACK_URL));
				oldCampSentFlag = true;
			}
			String webLinkText = campaign.getWebLinkText();
			String webLinkUrlText = campaign.getWebLinkUrlText();
			
			
			String weblinkUrl =  PropertyUtil.getPropertyValue("weblinkUrl");
			//String optInWebLinktext =  PropertyUtil.getPropertyValue("optInWebLinktext");
			//String optInWebLinkUrltext =  PropertyUtil.getPropertyValue("optInWebLinkUrltext");
			
			weblinkUrl =weblinkUrl.replace("|^", "[").replace("^|", "]");
			String webUrl = null;
			if(webLinkText!=null && !webLinkText.isEmpty() && webLinkUrlText!=null && !webLinkUrlText.isEmpty()) {
				webUrl =  DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT, webLinkText + " <a href='" + weblinkUrl + "'>"+ webLinkUrlText + "</a>");
			}
			htmlStr = htmlStr.replace(webUrl, " ");
			String unSubUrl = PropertyUtil.getPropertyValue("unSubscribeUrl");
			unSubUrl =unSubUrl.replace("|^", "[").replace("^|", "]");
			if(oldCampSentFlag) {
				unSubUrl = unSubUrl.replace("[userId]", userId.toString());
			}
			else {
				unSubUrl = unSubUrl.replace("[userId]", EncryptDecryptUrlParameters.encrypt(userId.toString()));
			}
			
			String unsubDiv = PropertyUtil.getPropertyValue("unsubFooterText");
			unsubDiv = unsubDiv.replace("|^unsubUrl^|", unSubUrl);
			
			
			htmlStr =htmlStr.replace(unsubDiv, "");
			
			
			// remove update subscription link  and unsubscribe
			
			String upadteUnsubDiv = PropertyUtil.getPropertyValue("updateUnsubFooterText");
			upadteUnsubDiv = upadteUnsubDiv.replace("|^unsubUrl^|", unSubUrl);
			
			htmlStr = htmlStr.replace(upadteUnsubDiv, "");
			
			String updateSubsLink =  PropertyUtil.getPropertyValue("updateSubscriptionLink");
			updateSubsLink =updateSubsLink.replace("|^", "[").replace("^|", "]");
		
			String updateUnsubDiv = PropertyUtil.getPropertyValue("updateSubHTMLTxt");
			updateUnsubDiv = updateUnsubDiv.replace("|^updateSubSUrl^|", updateSubsLink);
			
			htmlStr = htmlStr.replace(updateUnsubDiv, "");

			
			if(htmlStr.contains("href='")){
				htmlStr = htmlStr.replaceAll("href='([^\"]+)'", "href=\"#\" target=\"_self\"");
				
			}
			if(htmlStr.contains("href=\"")){
				htmlStr = htmlStr.replaceAll("href=\"([^\"]+)\"", "href=\"#\" target=\"_self\"");
			}
			
			
			//replacement logic for subject symbol place holders
			
			Set<String> symbolSet = getSubjectSymbolFields(htmlStr);
			if(symbolSet != null && symbolSet.size()>0){
				
				//logger.debug("symbolSet ==========>"+symbolSet);
				for (String symbol : symbolSet) {
						if(symbol.startsWith(Constants.DATE_PH_DATE_)) {
							if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_today)){
								Calendar cal = MyCalendar.getNewCalendar();
							}
							else if(symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)){
								Calendar cal = MyCalendar.getNewCalendar();
								cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
							}
							else if(symbol.endsWith(Constants.DATE_PH_DAYS)){
								
								try {
									String[] days = symbol.split("_");
									Calendar cal = MyCalendar.getNewCalendar();
									cal.set(Calendar.DATE, cal.get(Calendar.DATE)+Integer.parseInt(days[1].trim()));
								} catch (Exception e) {
									logger.debug("exception in parsing date placeholder");
								}
						}
						}
				}
			}
			//contact home store and last purchase place holders replacement logic
			String placeHoldersStr = camprep.getPlaceHoldersStr();
			if(placeHoldersStr != null && !placeHoldersStr.trim().isEmpty()) {
				String contactPhValStr = campSent.getContactPhValStr();
				if(contactPhValStr != null) {
					
					String[] phTokenArr = contactPhValStr.split(Constants.ADDR_COL_DELIMETER);
					String keyStr = "";
					String ValStr = "";
					for (String phToken : phTokenArr) {
						keyStr = phToken.substring(0, phToken.indexOf(Constants.DELIMETER_DOUBLECOLON));
						if(( keyStr.equalsIgnoreCase("[GEN_ContactHomeStore]") ) && (keyStr.equalsIgnoreCase("[GEN_ContactLastPurchasedStore]")) &&  ( keyStr.startsWith("[CC_")) ){
							continue;
						}
						ValStr = phToken.substring(phToken.indexOf(Constants.DELIMETER_DOUBLECOLON)+Constants.DELIMETER_DOUBLECOLON.length());
						
						htmlStr = htmlStr.replace(keyStr, ValStr);
					}
					try {
						Pattern  r = Pattern.compile("<\\w*[^>]*>(.[&nbsp;]+)<\\w*[^>]*>", Pattern.CASE_INSENSITIVE);
						Matcher idm = r.matcher(htmlStr);
					    while(idm.find()){
					    	String matchedSpace = idm.group(0);
							htmlStr = htmlStr.replace(matchedSpace, "<p></p>");
						}
					}catch(Exception e) {
					}
					
			
				}
			}
			htmlStr = htmlStr.replace("[sentId]", EncryptDecryptUrlParameters.encrypt(sentId.toString()));
			htmlStr = htmlStr.replace("[cId]", EncryptDecryptUrlParameters.encrypt(campSent.getContactId().toString()));
			htmlStr = htmlStr.replace("[userId]", EncryptDecryptUrlParameters.encrypt(userId.longValue()+""));
			htmlStr = htmlStr.replace("[email]",  campSent.getEmailId());
			
			
			
			return htmlStr;
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
			return null;
		} 
		
	}
	

		private Set<String> getSubjectSymbolFields(String content) {
			
			content = content.replace("|^", "[").replace("^|", "]");
			
			String cfpattern = "\\[([^\\[]*?)\\]";
			Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
			Matcher m = r.matcher(content);
		
			String ph = null;
			Set<String> subjectSymbolSet = new HashSet<String>();
		
			try {
				while(m.find()) {
		
					ph = m.group(1); //.toUpperCase()
					if(logger.isInfoEnabled()) logger.info("Ph holder :" + ph);
		
					if(ph.startsWith(Constants.SYMBOL_PH_SYM)) {
						subjectSymbolSet.add(ph);
					}else if(ph.startsWith(Constants.DATE_PH_DATE_)){
						subjectSymbolSet.add(ph);
					}else if(ph.startsWith("CC_")){
						subjectSymbolSet.add(ph);
					}
				} // while
				
				//logger.debug("+++ Exiting : "+ totalPhSet);
			} catch (Exception e) {
				logger.error("Exception while getting the symbol place holders ", e);
			}
		
			if(logger.isInfoEnabled()) logger.info("symbol PH  Set : "+ subjectSymbolSet);
		
			return subjectSymbolSet;
		}

		@Override
		public CampaignReportResponse processNotificationClickUpdate(CampaignReportRequest campaignReportRequest)
				throws BaseServiceException {
			CampaignReportResponse campaignReportResponse = new CampaignReportResponse();
			campaignReportResponse.setResponseType(OCConstants.RESPONSE_TYPE_CLICK_REDIRECT);
			Long sentId = null;
			try {
				if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_OLD_REQUESTED_ACTION) == 0) {
					logger.info("Processing Old Action Type ::");
					logger.info("Old Type Notification sent Id** ::"+campaignReportRequest.getSentId());
					sentId = Long.parseLong(campaignReportRequest.getSentId());
				}
				else if(campaignReportRequest.getActionType().compareTo(OCConstants.URL_TYPE_NEW_REQUESTED_ACTION) == 0) {
					logger.info("Processing new Action Type ::");
					logger.info("Before decrypting SentId:"+campaignReportRequest.getSentId() +"CId ::"+campaignReportRequest.getcId());
					sentId = Long.parseLong(EncryptDecryptUrlParameters.decrypt(campaignReportRequest.getSentId()));
					//sentId = Long.parseLong(campaignReportRequest.getSentId());
					logger.info("After decrypting Notification SentId:"+sentId);
				}
			} catch (Exception e) {
				logger.error("CrId ::"+campaignReportRequest.getCrId() +"CId ::"+campaignReportRequest.getcId() +"UserId ::"+campaignReportRequest.getUserId());
				logger.error("** Exception : Invalid sentId ::"+campaignReportRequest.getSentId() +" for the requested action - "+campaignReportRequest.getAction());
				throw new BaseServiceException("Exception occured while processing processClickUpdate::::: ", e);
			} 	
			
			if(campaignReportRequest.getUrl()==null) {
				if(logger.isDebugEnabled()) logger.debug("URL is null");
				campaignReportResponse.setStatus(OCConstants.CAMPAIGN_UPDATE_RESPONSE_STATUS_FAILURE);
				return campaignReportResponse;
			} 
			
			if(campaignReportRequest.getUrl().contains("&amp;")) {

				campaignReportRequest.setUrl(campaignReportRequest.getUrl().replace("&amp;", "&"));
			}
			
			NotificationClicks clicks = new NotificationClicks(sentId, campaignReportRequest.getUrl(), Calendar.getInstance(), campaignReportRequest.getUserAgent());
			
			logger.info("Row clicks"+clicks);
			synchronized (notificationClicksQueue) {

				notificationClicksQueue.add(clicks);
			}
			
			if(campaignReportRequest.getUrl().indexOf("http://") == -1 && (campaignReportRequest.getUrl().indexOf("https://") == -1 )){
				campaignReportRequest.setUrl("http://" + campaignReportRequest.getUrl().trim());
			}
			campaignReportResponse.setUrlStr(campaignReportRequest.getUrl());
			return campaignReportResponse;
		}
		
		
	
}
