package org.mq.optculture.business.ocmedia;

import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SMSCampaignSentUrlShortCode;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.optculture.business.gateway.GatewayBusinessService;
import org.mq.optculture.business.helper.UrlShortCodeRequestProcessHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.OCMediaRequestObject;
import org.mq.optculture.model.SMSInBoundRequestObject;
import org.mq.optculture.model.UrlShortCodeResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.zxing.common.BitMatrix;

public class OCMediaServiceImpl implements OCMediaService{

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private static String errorPage ="<html><head><title>Invalid Code</title></head><body><center>INVALID SHORT CODE</center></body></html>";
	
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		
		String action = baseRequestObject.getAction();
		logger.debug("=======calling the service ======");
		
		
		if(action == null){
			throw new BaseServiceException("No action/type found");
		}
		
		OCMediaService OCMediaServiceObj = (OCMediaService)ServiceLocator.getInstance().getServiceByName(OCConstants.OCMEDIA_SERVICE);
		BaseResponseObject baseResponseObject = new BaseResponseObject();//response to return 
			
		if(action.equals(OCConstants.REQUEST_PARAM_ACTION_SHORTCODE)) {
			
			OCMediaRequestObject OCMediaRequestObj = (OCMediaRequestObject)baseRequestObject;
			baseResponseObject = OCMediaServiceObj.processURLShortCodeRequest(OCMediaRequestObj);
			
		}
		
		return baseResponseObject;
	}
	
	
	@Override
	public BaseResponseObject processURLShortCodeRequest(
			OCMediaRequestObject OCMediaRequestObj) throws BaseServiceException {
		
		String shortCode = OCMediaRequestObj.getShortCode();
		UrlShortCodeResponseObject urlShortCodeResponseObj = new UrlShortCodeResponseObject();
		
		if(shortCode == null || shortCode.trim().isEmpty()) {
			
			if(logger.isDebugEnabled()) logger.debug("requested shortCodeId is null");
			urlShortCodeResponseObj.setResponseObject(errorPage);
			return urlShortCodeResponseObj;
		}
		
		UrlShortCodeRequestProcessHelper urlShortCodeProcessHelper = new UrlShortCodeRequestProcessHelper();
		Object helperRetObj = null;
		boolean isAClickUpdate = false;
		SMSCampaignSentUrlShortCode smsCampaignSentUrlShortCode = null;
		if(shortCode.startsWith(OCConstants.SHORTURL_CODE_PREFIX_S)) {//sent through SMS campaign , need to track
			
			smsCampaignSentUrlShortCode = urlShortCodeProcessHelper.getSMSCampSentUrlObj(shortCode);
			
			if(smsCampaignSentUrlShortCode == null) {
				
				urlShortCodeResponseObj.setResponseObject(errorPage);
				return urlShortCodeResponseObj;
			}
			
		
			//send back an appropriate response
			helperRetObj = urlShortCodeProcessHelper.checkAndPerformAction(smsCampaignSentUrlShortCode.getOriginalShortCode());
			isAClickUpdate = true;
			
			
		}else{
			
			helperRetObj = urlShortCodeProcessHelper.checkAndPerformAction(shortCode);//redirect / display barcode
			
		}	
			
		if(helperRetObj == null){
			urlShortCodeResponseObj.setResponseObject(errorPage);
			return urlShortCodeResponseObj;
		}
		
		if(helperRetObj instanceof String) {
			
			urlShortCodeResponseObj.setRedirectTo(helperRetObj.toString());
			if(isAClickUpdate){
				
				//need to update clicks in DB(give it to a thread)
				smsCampaignSentUrlShortCode.setClickedurl(helperRetObj.toString());
				urlShortCodeProcessHelper.updateSMSClicks(smsCampaignSentUrlShortCode);
				
			}
			
		}
		
		else if(helperRetObj instanceof BitMatrix) {
			
			urlShortCodeResponseObj.setBitMatrix((BitMatrix)helperRetObj);
			urlShortCodeResponseObj.setImageFormat(OCConstants.IMAGE_FORMAT_PNG);
			
			if(isAClickUpdate){
				
				//need to update clicks in DB(give it to a thread)
				smsCampaignSentUrlShortCode.setClickedurl(PropertyUtil.getPropertyValue(Constants.APP_SHORTNER_URL)+
						smsCampaignSentUrlShortCode.getGeneratedShortCode());
				urlShortCodeProcessHelper.updateSMSClicks(smsCampaignSentUrlShortCode);
				
			}
		}	
		
		
		
		return urlShortCodeResponseObj;
	}
	
	
}

