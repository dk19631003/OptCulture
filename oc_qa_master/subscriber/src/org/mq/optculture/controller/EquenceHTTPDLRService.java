package org.mq.optculture.controller;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoSmsQueue;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.beans.SMSBounces;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.beans.SMSCampaignSent;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.EquenceSMSGateway.PrepareEquenceJsonResponse;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AutoSmsQueueDao;
import org.mq.marketer.campaign.dao.AutoSmsQueueDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.DRSMSSentDao;
import org.mq.marketer.campaign.dao.DRSMSSentDaoForDML;
import org.mq.marketer.campaign.dao.SMSBouncesDao;
import org.mq.marketer.campaign.dao.SMSBouncesDaoForDML;
import org.mq.marketer.campaign.dao.SMSCampaignReportDao;
import org.mq.marketer.campaign.dao.SMSCampaignReportDaoForDML;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDaoForDML;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.business.gateway.EquenceBusinessService;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.EquenceDLRRequestObject;
import org.mq.optculture.model.EquenceDLRResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;


public class EquenceHTTPDLRService extends AbstractController{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		
		//get all the request parameteres and construct request VO object
	/*	response.setContentType("application/json");
		//EquenceDLRRequestObject newHTTPDLRRequestObject = new EquenceDLRRequestObject();
		String jsonValue = OptCultureUtils.getParameterJsonValue(request);
		Gson gson = new Gson();*/
		//logger.debug(""+jsonValue);
		
		logger.info("request value coming from EquenceDLR is"+request);
		
		
		String status = request.getParameter("sms_delv_status") == null ?  request.getParameter("Sms_delv_status") : request.getParameter("sms_delv_status");
		String deliveredTime = request.getParameter("sms_delv_dttime") == null ?  request.getParameter("Sms_delv_dttime") : request.getParameter("sms_delv_dttime");
		String mobileNumber = request.getParameter("mobile_no") == null ? request.getParameter("Mobile_NO") : request.getParameter("mobile_no");
		String mesagegId = request.getParameter("msg_id");
		String msgId = request.getParameter("MSGID");
		String mrId = request.getParameter("mr_id") == null ? request.getParameter("Mr_id") : request.getParameter("mr_id");
		String reason = request.getParameter("remarks");

		logger.info("status value is"+status);
		logger.info("deliveredTime value is"+deliveredTime);
		logger.info("mobileNumber value is"+mobileNumber);
		logger.info("mesagegId value is"+mesagegId);
		logger.info("msgId value is"+msgId);
		logger.info("mrId value is"+mrId);
		logger.info("reason value is"+reason);
		
		EquenceDLRRequestObject requestObject = new EquenceDLRRequestObject();;
		EquenceDLRResponseObject retResponse =new EquenceDLRResponseObject();
		//EquenceDLRResponseObject responseObject=null;
		try{
	//	requestObject = gson.fromJson(jsonValue, EquenceDLRRequestObject.class);
		requestObject.setStatus(status);
		requestObject.setDeliveredTime(deliveredTime);
		requestObject.setMobileNumber(mobileNumber);
		requestObject.setMessageID(mesagegId);
		requestObject.setMSGID(msgId);
		requestObject.setMrId(mrId);
		requestObject.setReason(reason);
		
		requestObject.setAction(OCConstants.REQUEST_PARAM_TYPE_VALUE_DLR);
		//newHTTPDLRRequestObject.setTimeFormat((MyCalendar.FORMAT_DATETIME_STYEAR).toString());
		requestObject.setUserSMSTool(Constants.USER_SMSTOOL_EQUENCE);
		//BaseService eqBaseService = ServiceLocator.getInstance().getServiceByName(OCConstants.EQUENCE_BUSINESS_SERVICE);
		EquenceBusinessService eqBaseService = (EquenceBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.EQUENCE_BUSINESS_SERVICE);
		logger.info("requestObject.getMSGID() "+requestObject.getMSGID());
		logger.info("requestObject.getMessageId() "+requestObject.getMessageID());
		
		logger.debug("=======got the service ======");
		
			logger.debug("=======calling the service ======");
			
			//BaseResponseObject retResponse  = eqBaseService.processRequest(newHTTPDLRRequestObject);
			//logger.info("JSON Response: = "+OptCultureUtils.getParameterJsonValue(request));
			//retResponse = gson.fromJson(jsonValue,EquenceDLRResponseObject.class);
			eqBaseService.processRequest(requestObject);
			logger.debug("=======ending the service ======"+retResponse);
			
			String responseJson = "{\"MESSAGE\":\"Success.\"}";
			//String responseJson = gson.toJson(retResponse);
			retResponse.setAction(requestObject.getAction());
			retResponse.setJsonValue(responseJson);
				logger.info("Response = "+responseJson);
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Completed EquenceHTTPDLRService.");
			
		}catch(Exception e){
			logger.info("Exception",e);
			String responseJson = "{\"MESSAGE\":\"Invalid request.\"}";
			PrintWriter printWriter = response.getWriter();
			printWriter.write(responseJson);
			printWriter.flush();
			printWriter.close();
			logger.info("Response = "+responseJson);
			
			return null;
		}
		processCampaignDLR(requestObject);
		return null;
	}
	
	public void processCampaignDLR(EquenceDLRRequestObject equenceDLRRequestObject) throws  BaseServiceException {
		String mrId=equenceDLRRequestObject.getMrId();
		String mobile=equenceDLRRequestObject.getMobileNumber();
		SMSCampaignSentDao smsCampaignSentDao = null;
		SMSCampaignSentDaoForDML smsCampaignSentDaoForDML  = null;
		AutoSmsQueueDao autoSmsQueueDao =null;
		AutoSmsQueueDaoForDML autoSmsQueueDaoForDML = null;
		DRSMSSentDao drSMSSentDao =null;
		DRSMSSentDaoForDML drSMSSentDaoForDML = null;

				try {
					smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGN_SENT_DAO);
					smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGN_SENT_DAO_FOR_DML);
					
					autoSmsQueueDao = (AutoSmsQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_QUEUE_DAO);
					autoSmsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
					drSMSSentDao = (DRSMSSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DR_SMS_SENT_DAO);
					drSMSSentDaoForDML = (DRSMSSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_SENT_DAO_ForDML);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new BaseServiceException("Exception while fetching smsCampaignSentDao");
				}
				
				if(equenceDLRRequestObject.getMSGID()!=null && !equenceDLRRequestObject.getMSGID().equals("") &&
						equenceDLRRequestObject.getMSGID().startsWith(OCConstants.AutoSMSPrefix)){
					String sentId = (equenceDLRRequestObject.getMSGID()).split(OCConstants.AutoSMSPrefix)[1];
					AutoSmsQueue autoSmsQueue = autoSmsQueueDao.findByMrIdAndMobile(sentId, mobile);
					if(autoSmsQueue == null ) throw new BaseServiceException("no autoSmsQueue "
							+ "found with mrid , mobile"+mrId+", "+mobile);
	
					setDLRStatusForAutoSMS(equenceDLRRequestObject, autoSmsQueue);
				}else if(equenceDLRRequestObject.getMSGID()!=null && !equenceDLRRequestObject.getMSGID().equals("") &&
						equenceDLRRequestObject.getMSGID().startsWith(OCConstants.DRSMSPrefix)){
					String sentId = (equenceDLRRequestObject.getMSGID()).split(OCConstants.DRSMSPrefix)[1];
					DRSMSSent drSMSSent = drSMSSentDao.findById(Long.parseLong(sentId));
					if(drSMSSent == null ) throw new BaseServiceException("no autoSmsQueue "
							+ "found with mrid , mobile"+mrId+", "+mobile);
	
					setDLRStatusForDRSMS(equenceDLRRequestObject, drSMSSent);
	
					
				}else{
					/*SMSCampaignSent smsCampaignSent = smsCampaignSentDao.findByMrIdAndMobile(mrId, mobile);
					if(smsCampaignSent == null ) throw new BaseServiceException("no smsCampaignSent "
							+ "found with mrid , mobile"+mrId+", "+mobile);*/
	
					setDLRBounceStatus(equenceDLRRequestObject, mrId, mobile);
				}
		}
	
	/*public String getDLRStatus(EquenceDLRRequestObject equenceDLRRequestObject) {
		String status = equenceDLRRequestObject.getStatus();		
			logger.info("status "+status);
			logger.info("--"+SMSStatusCodes.equenceStatusCodesMap.get(status));
			return SMSStatusCodes.equenceStatusCodesMap.get(status);		
		
	}*/
	
		public void setDLRBounceStatus(EquenceDLRRequestObject equenceDLRRequestObject, 
					String mrId, String mobile) throws BaseServiceException {
				
			try{
				String status = equenceDLRRequestObject.getStatus();
					SMSCampaignSentDao smsCampaignSentDao = (SMSCampaignSentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGN_SENT_DAO);
					SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGN_SENT_DAO_FOR_DML);
					
					
					String type = SMSStatusCodes.equenceStatusCodesMap.get(status);
					logger.info("type---"+type);
					if(SMSStatusCodes.DeliveredSet.contains(type)){
						logger.info("Delivered");
						status=SMSStatusCodes.EQUENCE_STATUS_DELIVERED;
						smsCampaignSentDaoForDML.updateStatus(status,mrId, mobile);
					}
					else if(SMSStatusCodes.BouncedSet.contains(type)) {
						
						SMSCampaignSent smsCampaignSent = smsCampaignSentDao.findByMrIdAndMobile(mrId, mobile);
						
						logger.info("in bouncedset");
						status = SMSStatusCodes.EQUENCE_STATUS_BOUNCED;
						
						logger.info(" "+smsCampaignSent.getSmsCampaignReport());
						addToBounces(smsCampaignSent.getSmsCampaignReport(), 
								smsCampaignSent.getMobileNumber(), smsCampaignSent, type,type,status);
						
						
						if(SMSStatusCodes.SuppressedSet.contains(SMSStatusCodes.equenceStatusCodesMap.get(status))){
							
							//Users user = smsCampaignReport.getUser();
							
							addToSuppressedContacts(smsCampaignSent.getSmsCampaignReport().getUser(), 
									smsCampaignSent.getMobileNumber(),type,type);
							
							
						}
					}
			}catch(Exception e){
				logger.error("Exception ",e);
			}
					
			}
		
		public void setDLRStatusForAutoSMS(EquenceDLRRequestObject equenceDLRRequestObject, 
				AutoSmsQueue autoSmsQueue) throws BaseServiceException {
			
		try{
			String status = equenceDLRRequestObject.getStatus();
			AutoSmsQueueDaoForDML autoSmsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
				
				String type = SMSStatusCodes.equenceStatusCodesMap.get(status);
				logger.info("type---"+type);
				if(SMSStatusCodes.DeliveredSet.contains(type)){
					logger.info("Delivered");
					status=SMSStatusCodes.EQUENCE_STATUS_DELIVERED;
					autoSmsQueueDaoForDML.updateStatus(status,autoSmsQueue.getId());
				}
				else if(SMSStatusCodes.BouncedSet.contains(type)) {
					
					logger.info("in bouncedset");
					status = SMSStatusCodes.EQUENCE_STATUS_BOUNCED;
					
					autoSmsQueueDaoForDML.updateStatus(status,autoSmsQueue.getId());
					
					/*logger.info(" "+smsCampaignSent.getSmsCampaignReport());
					addToBounces(smsCampaignSent.getSmsCampaignReport(), 
							smsCampaignSent.getMobileNumber(), smsCampaignSent, type,type,status);
					
					
					if(SMSStatusCodes.SuppressedSet.contains(SMSStatusCodes.equenceStatusCodesMap.get(status))){
						
						//Users user = smsCampaignReport.getUser();
						
						addToSuppressedContacts(smsCampaignSent.getSmsCampaignReport().getUser(), 
								smsCampaignSent.getMobileNumber(),type,type);
					}*/
						
						
				}
				/*else{	 
					SMSBouncesDao smsBouncesDao = null;
					SMSBouncesDaoForDML smsBouncesDaoForDML  = null;

					SMSCampaignReportDao smsCampaignReportDao = null;
					SMSCampaignReportDaoForDML smsCampaignReportDaoForDML = null;
					try {
						smsBouncesDao = (SMSBouncesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSBOUNCES_DAO);
						smsBouncesDaoForDML = (SMSBouncesDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMSBOUNCES_DAO_FOR_DML);

						smsCampaignReportDao = (SMSCampaignReportDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_CAMPAIGNREPORT_DAO);
						smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGNREPORT_DAO_FOR_DML);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new BaseServiceException("No dao found smsBouncesDao");
					}
							
					smsCampaignReportDaoForDML.updateBounceReport(smsCampaignSent.getSmsCampaignReport().getSmsCrId());
				}*/
		}catch(Exception e){
			logger.error("Exception ",e);
		}
				
		}
		
		public void setDLRStatusForDRSMS(EquenceDLRRequestObject equenceDLRRequestObject, 
				DRSMSSent drSmsSent) throws BaseServiceException {
			
		try{
			String status = equenceDLRRequestObject.getStatus();
			DRSMSSentDaoForDML drSMSSentDaoForDML = (DRSMSSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_SENT_DAO_ForDML);
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user=usersDao.findByUserId(drSmsSent.getUserId());
				
				String Status = equenceDLRRequestObject.getStatus();
				String mobile=equenceDLRRequestObject.getMobileNumber();
				String type = SMSStatusCodes.equenceStatusCodesMap.get(status);
				logger.info("type---"+type);
				if(SMSStatusCodes.DeliveredSet.contains(type)){
					logger.info("Delivered");
					status=SMSStatusCodes.EQUENCE_STATUS_DELIVERED;
					drSMSSentDaoForDML.updateStatus(status,drSmsSent.getId());
				}
				else if(SMSStatusCodes.BouncedSet.contains(type)) {
					
					logger.info("in bouncedset");
					status = SMSStatusCodes.EQUENCE_STATUS_BOUNCED;
					
					drSMSSentDaoForDML.updateStatus(status,drSmsSent.getId());
					
					//APP-4253
					logger.info("Equence status value is"+SMSStatusCodes.equenceStatusCodesMap.get(Status));
					if(SMSStatusCodes.SuppressedSet.contains(SMSStatusCodes.equenceStatusCodesMap.get(Status))){

						logger.info("Entering DR suppressed contct block");

							addToSuppressedContacts(user,mobile,type,type);
						
					}
					
				}
				
		}catch(Exception e){
			logger.error("Exception ",e);
		}
				
		}
		public void addToBounces(SMSCampaignReport smsCampaignReport, String mobile,
					SMSCampaignSent smsCampaignSent, String status, String msg,String statusInDB ) throws BaseServiceException {
				
				if(smsCampaignSent == null || smsCampaignReport == null) {
					
					if(logger.isErrorEnabled()) logger.error("** got source objects as null");
					return;
					
				}
				
				//need to find the existing object with the same given mobile if any..
				Long crId = smsCampaignReport.getSmsCrId(); 

				try {
					SMSBouncesDao smsBouncesDao = (SMSBouncesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSBOUNCES_DAO);
					SMSBouncesDaoForDML smsBouncesDaoForDML = (SMSBouncesDaoForDML )ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMSBOUNCES_DAO_FOR_DML);
					SMSCampaignSentDaoForDML smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGN_SENT_DAO_FOR_DML);
					SMSCampaignReportDaoForDML smsCampaignReportDaoForDML =  (SMSCampaignReportDaoForDML )ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_CAMPAIGNREPORT_DAO_FOR_DML);
				SMSBounces newBounce = smsBouncesDao.findBymobile(crId, mobile);
				
				if(newBounce == null) {
					
					newBounce = new SMSBounces();
					newBounce.setCrId(crId);
					newBounce.setSentId(smsCampaignSent);
					
					
				}
				
				newBounce.setMessage(msg);
				newBounce.setMobile(mobile);
				newBounce.setCategory(status);
				newBounce.setBouncedDate(Calendar.getInstance());
				//smsBouncesDao.saveOrUpdate(newBounce);
				smsBouncesDaoForDML.saveOrUpdate(newBounce);

				logger.info("added to bounces====");
				if(logger.isDebugEnabled()) logger.debug("updateing report bounce count"+smsCampaignReport.getBounces());
				smsCampaignSentDaoForDML.updateBounceStatus(statusInDB,smsCampaignSent.getSentId());
				//smsCampaignReportDao.updateBounceReport(smsCampaignReport.getSmsCrId());
				smsCampaignReportDaoForDML.updateBounceReport(smsCampaignReport.getSmsCrId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new BaseServiceException("No dao found smsBouncesDao");
				}
			}//addToBounces
		public void addToSuppressedContacts(Users user, String mobile, 
				 String type, String reason) throws BaseServiceException{
			logger.info("entered adding to suppressed====");
			SMSSuppressedContactsDao smsSuppressedContactsDao = null;
			SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML = null;
			ContactsDao contactsDao = null;
			ContactsDaoForDML contactsDaoForDML = null;
			try {
				ServiceLocator locator = ServiceLocator.getInstance();
				smsSuppressedContactsDao = (SMSSuppressedContactsDao)locator.getDAOByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO);
				smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML)locator.getDAOForDMLByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO_FOR_DML);
				contactsDao = (ContactsDao)locator.getDAOByName(OCConstants.CONTACTS_DAO);
				contactsDaoForDML = (ContactsDaoForDML)locator.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			

			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				throw new BaseServiceException("No dao(s) found in the context with id ",e);
			}
			SMSSuppressedContacts suppressedContact = null;
			Long userId = null; 
			
			if(user != null) {
				
				userId = user.getUserId();
			}
			List<SMSSuppressedContacts> retList =  smsSuppressedContactsDao.searchContactsById(user, mobile);
			if(retList != null && retList.size() == 1) {
				suppressedContact = retList.get(0);
				//suppressedContact.setSuppressedtime(Calendar.getInstance());
				
			}
			else {
				
				suppressedContact = new SMSSuppressedContacts();
				suppressedContact.setUser(user);
				suppressedContact.setMobile(mobile);
				suppressedContact.setSuppressedtime(Calendar.getInstance());
				
			}
			//Users users = usersDao.find(userId); 
			suppressedContact.setType(type);
			suppressedContact.setReason(reason == null ? type :reason);
			try{
				
				//smsSuppressedContactsDao.saveOrUpdate(suppressedContact);
				smsSuppressedContactsDaoForDML.saveOrUpdate(suppressedContact);
			}catch (Exception e) {
				if(logger.isErrorEnabled()) logger.error("**Exception : while adding contact to suppress Contacts list :", e);
			}
			String countryCarrier =user.getCountryCarrier()!=null?user.getCountryCarrier()+"":"" ;
			int i=countryCarrier.length();
			if(mobile.startsWith(countryCarrier)) {
				logger.info("entering country carrier block");
					mobile=mobile.substring(i);
			}
			logger.info("mobile after removing country carrier is"+mobile);

			if(user != null)contactsDaoForDML.updatemobileStatus(mobile, type, user);
		}
}
