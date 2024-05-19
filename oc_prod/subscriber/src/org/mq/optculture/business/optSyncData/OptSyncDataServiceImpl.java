package org.mq.optculture.business.optSyncData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.mq.marketer.campaign.beans.UpdateOptSyncData;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.ExternalSMTPSupportSender;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.UpdateOptSyncDataDao;
import org.mq.marketer.campaign.dao.UpdateOptSyncDataDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.OptSyncDataRequestObject;
import org.mq.optculture.model.OptSyncDataResponseObject;
import org.mq.optculture.model.couponcodes.StatusInfo;
import org.mq.optculture.model.loyalty.Status;
import org.mq.optculture.model.opySync.OptSyncResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.newrelic.api.agent.Trace;

public class OptSyncDataServiceImpl implements OptSyncDataService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Trace
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		OptSyncDataRequestObject optSyncDataRequestObject = null; 
		OptSyncDataResponseObject optSyncDataResponseObject= null;
		OptSyncResponse optSyncResponse = null;
		try {
			Gson gson = new Gson();
			optSyncDataRequestObject = gson.fromJson(baseRequestObject.getJsonValue(),OptSyncDataRequestObject.class);
			OptSyncDataService optSyncDataService = (OptSyncDataService) ServiceLocator	.getInstance().getServiceByName(OCConstants.OPT_SYNC_UPDATE_SERVICE);
			optSyncDataResponseObject = (OptSyncDataResponseObject) optSyncDataService.processOptSynsDataRequest(optSyncDataRequestObject);
			
			logger.info("resopnse obj is"+optSyncDataResponseObject);

			// object to json
			String json = gson.toJson(optSyncDataResponseObject);
			baseResponseObject.setJsonValue(json);

		} catch (Exception e) {
			throw new BaseServiceException(
					"Exception occured while processing the request......", e);
		}

		return baseResponseObject;
	}


	@Override
	public BaseResponseObject processOptSynsDataRequest(
			OptSyncDataRequestObject optSyncDataRequestObject)
					throws BaseServiceException {
		
		logger.info("enterd in processOptSynsDataRequest " );
		OptSyncDataResponseObject optSyncDataResponseObject= null;
		OptSyncResponse optSyncResponse = null;
		Status status = null;
		try {
			/*
			 * status = new Status("0","", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			 * optSyncDataResponseObject = prepareFinalResponseObject(status);
			 * 
			 * logger.info("ended in processOptSynsDataRequest "+optSyncDataResponseObject
			 * ); if(status.getERRORCODE().equals("0"))return optSyncDataResponseObject;
			 */

			UsersDao usersDao=(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			status = validateRootObject(optSyncDataRequestObject);
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getSTATUS())) {
				optSyncDataResponseObject = prepareFinalResponseObject(status);
				return optSyncDataResponseObject;
			}
			status = validateOptSyncReqObj(optSyncDataRequestObject);

			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getSTATUS())) {
				optSyncDataResponseObject = prepareFinalResponseObject(status);
				return optSyncDataResponseObject;
			}
			String optSyncId = optSyncDataRequestObject.getOPTSYNCREQUEST().getHEAD().getOPTSYNCID();
			
			logger.info(" Got hit from optSync with OptSyncId");
			
			status = validateOptSyncId(optSyncId);


			String userName = optSyncDataRequestObject.getOPTSYNCREQUEST().getUSERDETAILS().getUSERNAME();
			
			String userOrg =  optSyncDataRequestObject.getOPTSYNCREQUEST().getUSERDETAILS().getORGID();
			String token = optSyncDataRequestObject.getOPTSYNCREQUEST().getUSERDETAILS().getTOKEN();
			
			status = validateUserDetails(token,userName,userOrg);
			
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getSTATUS())) {
				optSyncDataResponseObject = prepareFinalResponseObject(status);
				return optSyncDataResponseObject;
			}
			
			Users user = usersDao.findByToken(userName+ Constants.USER_AND_ORG_SEPARATOR +userOrg, token );
			status=validateUser(user);
			
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getSTATUS())) {
				optSyncDataResponseObject = prepareFinalResponseObject(status);
				return optSyncDataResponseObject;
			}
			

			status = validateOptSyncObj(optSyncId,user);
			
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getSTATUS())) {
				optSyncDataResponseObject = prepareFinalResponseObject(status);
				return optSyncDataResponseObject;
			}
			status = new Status("0","", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			optSyncDataResponseObject = prepareFinalResponseObject(status);

			logger.info("ended in processOptSynsDataRequest "+optSyncDataResponseObject );
			return optSyncDataResponseObject;

		} catch (Exception e) {
			logger.error("Exception", e);
			throw new BaseServiceException(
					"exception while updating opt sync data...", e);
		}

	}

	private Status validateRootObject(OptSyncDataRequestObject optSyncDataRequestObject){

		Status status = null;
		if(optSyncDataRequestObject == null){
			status = new Status("9001",PropertyUtil.getErrorMessage(9001,OCConstants.ERROR_OPTSYNC_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);

		}
		else if(optSyncDataRequestObject.getOPTSYNCREQUEST() == null) {
			status = new Status("9002",PropertyUtil.getErrorMessage(9002,OCConstants.ERROR_OPTSYNC_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
		return status;

	}

	private Status validateOptSyncReqObj(OptSyncDataRequestObject optSyncDataRequestObject){

		Status status = null;
		if(optSyncDataRequestObject.getOPTSYNCREQUEST().getHEAD() == null){
			status = new Status("9003",PropertyUtil.getErrorMessage(9003,OCConstants.ERROR_OPTSYNC_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);

		}
		/*else if(optSyncDataRequestObject.getOPTSYNCREQUEST().getBODY() == null) {

			status = new Status("903","Unable to find Opt sync name  in  json",OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}*/else if(optSyncDataRequestObject.getOPTSYNCREQUEST().getUSERDETAILS() == null) {

			status = new Status("9004",PropertyUtil.getErrorMessage(9004,OCConstants.ERROR_OPTSYNC_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		}
		return status;

	}
	
	
	private Status validateOptSyncId(String optSyncId){
		Status status = null;
		if(optSyncId == null || optSyncId.trim().length() == 0){
			logger.debug("Error : OptSync ID of HearderInfo is empty in JSON ");
			status=new Status( "9005", PropertyUtil.getErrorMessage(9005,OCConstants.ERROR_OPTSYNC_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		return status;
		
		
	}
	private Status validateUserDetails(String token,String userName,String userOrg) throws BaseServiceException  {
		Status status=null;
		if(token == null){
			logger.debug("Error : User Token cannot be empty.");
			status=new Status("9006", PropertyUtil.getErrorMessage(9006,OCConstants.ERROR_PROMO_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(userName == null || userOrg == null){
			logger.debug("Error : Username or organisation cannot be empty.");
			status=new Status("9007", PropertyUtil.getErrorMessage(9007,OCConstants.ERROR_OPTSYNC_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		return status;
	}//validateUserDetails
	
	private Status validateUser(Users user) throws BaseServiceException  {
		Status status=null;
		if(user == null){
			logger.debug("Unable to find the user Obj");
			status=new Status("9008", PropertyUtil.getErrorMessage(9008,OCConstants.ERROR_OPTSYNC_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		return status;
	}//validateUser

	private Status validateOptSyncObj(String optSyncId , Users user) throws BaseServiceException{
		try {
			Status status = null;
			UpdateOptSyncDataDao updateOptSyncDataDao =(UpdateOptSyncDataDao)ServiceLocator.getInstance().getDAOByName("updateOptSyncDataDao");
			UpdateOptSyncDataDaoForDML updateOptSyncDataDaoForDML =(UpdateOptSyncDataDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("updateOptSyncDataDaoForDML");
			UpdateOptSyncData updateOptSyncData = updateOptSyncDataDao.findBy(Long.parseLong(optSyncId), user.getUserId());
			if(updateOptSyncData == null){
				status = new Status("9009",PropertyUtil.getErrorMessage(9009,OCConstants.ERROR_OPTSYNC_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			}else{
				
				if(OCConstants.OPT_SYNC_STATUS_DEACTIVE.equals(updateOptSyncData.getStatus()) && Constants.ALERTS_SENDING_AUTOMATICALLY.equals(updateOptSyncData.getOnAlertsBy()))	{
				
					sendOptSyncUpMailToUser(updateOptSyncData , user);				
					updateOptSyncData.setStatus(OCConstants.OPT_SYNC_STATUS_ACTIVE);
				}else{
					
					updateOptSyncData.setStatus(OCConstants.OPT_SYNC_STATUS_ACTIVE);
				}
								
				updateOptSyncData.setDownAlertSentTime(null);
				if(Constants.ALERTS_SENDING_AUTOMATICALLY.equals(updateOptSyncData.getOnAlertsBy())){
				//	logger.info("enterd in::"+updateOptSyncData.getOnAlertsBy());
				
					if(OCConstants.OPT_SYNC_PLUGIN_STATUS_INACTIVE.equals(updateOptSyncData.getPluginStatus())){
					//	logger.info("plug in status::"+updateOptSyncData.getPluginStatus());
						
						updateOptSyncData.setPluginStatus(OCConstants.OPT_SYNC_PLUGIN_STATUS_ACTIVE);
					//	logger.info("plug in status  after::"+updateOptSyncData.getPluginStatus());
						
					}
				}
				
				updateOptSyncData.setOptSyncHitTime(Calendar.getInstance());
				updateOptSyncData.setCount(0);
				updateOptSyncData.setOptSyncModifiedTime(Calendar.getInstance());
				//updateOptSyncDataDao.saveOrUpdate(updateOptSyncData);
				updateOptSyncDataDaoForDML.saveOrUpdate(updateOptSyncData);
				
				
			}
			return status;
		} catch (NumberFormatException e) {
			logger.error("Exception ", e);
			throw new BaseServiceException("Exception while validating Opt sync data",e);
		} catch (Exception e) {
			logger.error("Exception ", e);
			throw new BaseServiceException("Exception while validating Opt sync data",e);
		}

	}
	
	
	private OptSyncDataResponseObject prepareFinalResponseObject(Status status){
		OptSyncDataResponseObject optSyncDataResponseObject = new OptSyncDataResponseObject();
		OptSyncResponse optSyncResponse = new OptSyncResponse();
		optSyncResponse.setSTATUS(status);
		optSyncDataResponseObject.setOPTSYNCRESPONSE(optSyncResponse);
		return optSyncDataResponseObject;
	}
	
	
	public void sendOptSyncUpMailToUser(UpdateOptSyncData updateOptSyncData, Users user ){
		
		String optSyncName=updateOptSyncData.getOptSyncName().trim();
		String optSyncId = ""+updateOptSyncData.getOptSyncId();
		String userName = Utility.getOnlyUserName(user.getUserName());
		String orgId=user.getUserOrganization().getOrgExternalId();
		String senderName=Constants.OPTSYNC_SENDER_NAME_VALUE;
		String messageHeader="";
		String htmlContent="";
		String textContent="";
		String fromEmailId = PropertyUtil.getPropertyValueFromDB(Constants.ALERT_FROM_EMAILID);
		String serverName = PropertyUtil.getPropertyValue("subscriberIp");
		ExternalSMTPSupportSender externalSMTPSupportSender=new ExternalSMTPSupportSender();
		String toEmailId1=Constants.STRING_NILL;
		String[] toEmailId = null;
		
		String downTimeStr= Constants.STRING_NILL;
		String hitTimeStr=Constants.STRING_NILL;
	// hit time string
		String hitDateStr  = MyCalendar.calendarToString(updateOptSyncData.getOptSyncHitTime(), MyCalendar.FORMAT_DATETIME_STYEAR );
		DateFormat df = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
		  DateFormat outputformat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss aa");
	       Date date = null;
	       try{
	    	  date= df.parse(hitDateStr);
	    	  hitTimeStr = outputformat.format(date);
	    	}catch(ParseException e){
	    	    logger.error(" Exception in date format convertion", e);
	    	 }
	       
	     // down time String
	       if(OCConstants.OPT_SYNC_STATUS_DEACTIVE.equalsIgnoreCase(updateOptSyncData.getStatus())){
	    		
				Calendar currentTime=Calendar.getInstance();
				Calendar hitTime=(Calendar) (updateOptSyncData.getOptSyncHitTime() == null ? Calendar.getInstance() : updateOptSyncData.getOptSyncHitTime());
				
				long ctime=currentTime.getTimeInMillis();
				long htime=hitTime.getTimeInMillis();
				long diff=ctime-htime;
				
				//long diffSeconds = diff / 1000 % 60;
				long diffMinutes = diff / (60 * 1000) % 60;
				long diffHours = diff / (60 * 60 * 1000) % 24;
				long diffDays = diff / (24 * 60 * 60 * 1000);
			
				if(diffDays == 0 && diffHours == 0 ){
					downTimeStr = diffMinutes + " minutes";
				}else if(diffDays == 0 && diffHours != 0 ){
					downTimeStr = diffHours + " hours and "+ diffMinutes + " minutes";
				}else			
				if(diffDays != 0  ){
					downTimeStr = diffDays + " days, " + diffHours + " hours and "+ diffMinutes + " minutes";
				}
		
			
			}      
	
	 htmlContent =  PropertyUtil.getPropertyValueFromDB(Constants.OPTSYNC_UP_ALERT_PROPERTY);
	 
	 
		
	htmlContent =	htmlContent.replace(Constants.OPTSYNC_NAME, optSyncName)
				   			   .replace(Constants.OPTSYNC_ID, optSyncId)
				   			   .replace(Constants.OPTSYNC_USER_NAME, userName)
				   			   .replace(Constants.OPTSYNC_ORGID, orgId)
				   			   .replace(Constants.OPTSYNC_SENDER_NAME, senderName)
				   			   .replace(Constants.OPTSYNC_HIT_TIME, hitTimeStr)
				   			   .replace(Constants.OPTSYNC_DOWN_TIME, downTimeStr);
	
	logger.info("html str is::"+htmlContent);
						   
	
		String subject= OCConstants.OPTSYNC_UP_ALERT_MAIL_SUBJECT.replace(OCConstants.OPTSYNC_ALERT_MAIL_SUBJECT_PH, userName); 
		  textContent = htmlContent;
		
		  toEmailId1 = updateOptSyncData.getEmailId();
			toEmailId = new String[5];// Assuming only five emailids are allowed
			toEmailId = toEmailId1.split(","); 
			
		
		//sending email
		//if(user.getVmta().equalsIgnoreCase("SendGridAPI")){
		if(Constants.VMTA_SENDGRIDAPI.equalsIgnoreCase(user.getVmta().getVmtaName())){
			
			try {
				if(user != null) {
					messageHeader =  "{\"unique_args\": {\"userId\": \""+ user.getUserId() +"\" ,\"EmailType\" : \""+OCConstants.OPTINL_MAIL_SENDER +"\",\"ServerName\": \""+ serverName +"\" }}";
				}
				//mail will be sent only if plugin is active 
					
					externalSMTPSupportSender.submitOptSyncMail(messageHeader, htmlContent, textContent,fromEmailId,subject,toEmailId);
					
				} catch (Exception e) {
				logger.debug("Exception while sending through sendGridAPI .. returning ",e);
			}
		
		}
	}
}
