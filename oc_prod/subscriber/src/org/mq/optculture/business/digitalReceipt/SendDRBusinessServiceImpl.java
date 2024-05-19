package org.mq.optculture.business.digitalReceipt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mq.marketer.campaign.beans.DigitalReceiptMyTemplate;
import org.mq.marketer.campaign.beans.DigitalReceiptUserSettings;
import org.mq.marketer.campaign.beans.DigitalReceiptsJSON;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.OrganizationZone;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.controller.PrepareFinalHtml;
import org.mq.marketer.campaign.dao.DigitalReceiptMyTemplatesDao;
import org.mq.marketer.campaign.dao.DigitalReceiptUserSettingsDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDao;
import org.mq.marketer.campaign.dao.DigitalReceiptsJSONDaoForDML;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.ZoneDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.digitalReceipt.SendDRResponse;
import org.mq.optculture.model.digitalReceipt.SendDRRequest;
import org.mq.optculture.model.digitalReceipt.SendDRResponseInfo;
import org.mq.optculture.model.digitalReceipt.SendDRStatus;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;


/**
 * It handles digital receipt rest service business logic implementation.
 * 
 * @author Venkata Rathnam D
 *
 */
public class SendDRBusinessServiceImpl implements SendDRBusinessService{

		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		@Override
		public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
			SendDRResponse sendDRResponse=null;
			try {
				SendDRRequest sendDRRequest = (SendDRRequest) baseRequestObject;

				SendDRBusinessService sendDRBusinessService = (SendDRBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.SEND_DR_BUSINESS_SERVICE);
				sendDRResponse = (SendDRResponse) sendDRBusinessService.processSendDRRequest(sendDRRequest, OCConstants.DR_ONLINE_MODE);
			} catch (Exception e) {
				logger.error("Exception ::" , e);
				throw new BaseServiceException("Exception occured while processing processRequest::::: ", e);
			}
			return sendDRResponse;
			
		}	
		@Override
		public SendDRResponse processSendDRRequest(SendDRRequest sendDRRequest, String mode) throws BaseServiceException {
			
			logger.info("Entered into processSendDRRequest service method....");
			SendDRResponse sendDRResponse=null;
			SendDRStatus status = null;
			try {

				sendDRResponse = validateJsonFields(sendDRRequest);
				if(sendDRResponse != null ){
					return sendDRResponse;
				}
				
				UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				boolean isUserFound = false;
		    	String userFullDetails = "";
		    	
		    	if( (sendDRRequest.getUserName() != null && sendDRRequest.getUserName().length() > 1) &&
		    		(sendDRRequest.getUserOrg() != null &&  sendDRRequest.getUserOrg().length() > 1) ) {	
		    		isUserFound = true;
		    		userFullDetails =  sendDRRequest.getUserName() + Constants.USER_AND_ORG_SEPARATOR +
		    				sendDRRequest.getUserOrg();
		    	} else {
		    		isUserFound = false;
		    		/*errorCode = 300001;
		    		jsonMessage ="Given User name or Organization details not found .";
		    	    logger.debug("Required user name and Organization fields are not valid ... returning ");
		    	    return null;*/
		    	}
				
				String jsonValue = sendDRRequest.getJsonValue();
				JSONObject jsonMainObj = null;
				JSONObject jsonHeadUserObj= null;
				JSONObject jsonHeadObj= null;
				JSONObject jsonBodyObj = null;
				JSONObject jsonReceiptObj = null;
				
				if(sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL)){
					jsonBodyObj = (JSONObject)JSONValue.parse(sendDRRequest.getJsonValue());
				}
				else{
					jsonMainObj = (JSONObject)JSONValue.parse(sendDRRequest.getJsonValue());
					
					logger.info(" json = "+sendDRRequest.getJsonValue());
					if(jsonMainObj != null) {
			    		if(!jsonMainObj.containsKey("Items")) {
			    			jsonBodyObj = (JSONObject)jsonMainObj.get("Body");
			    		}
			    		else{
			    			jsonBodyObj = jsonMainObj;
			    		}
					}
					else{
						status = new SendDRStatus("300005", "Unable to parse the JSON request.", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						sendDRResponse = prepareResponseObject(status);
						return sendDRResponse;
					}
				}
				
				if(jsonMainObj != null) {
		    		if(jsonMainObj.containsKey("Head")) {
		    			jsonHeadUserObj = (JSONObject)((JSONObject)(jsonMainObj.get("Head")));
		    			jsonHeadObj = (JSONObject)((JSONObject)(jsonMainObj.get("Head"))).get("user");
		    		}
		    	}
		    	
		    	if(!isUserFound){
		    		if(jsonHeadObj.get("userName") != null && jsonHeadObj.get("userName").toString().trim().length() > 1 && 
		    				jsonHeadObj.get("organizationId") != null && jsonHeadObj.get("organizationId").toString().trim().length() > 1){
		    			isUserFound = true;
			    		userFullDetails =  jsonHeadObj.get("userName").toString().trim() + Constants.USER_AND_ORG_SEPARATOR +
			    				jsonHeadObj.get("organizationId").toString().trim();
		    			
		    		}else{
		    			if(userFullDetails == null || userFullDetails.isEmpty()) {
		    				logger.debug("Required user name and Organization fields are not valid ... returning ");
		    				status = new SendDRStatus("300001", PropertyUtil.getErrorMessage(300001, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		    				 status = new SendDRStatus("300001", "Invalid user-credentials.", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE); 
		    				sendDRResponse = prepareResponseObject(status);
							return sendDRResponse;
		    			}
		    		}
		    	}
				
				
				
				Users user = usersDao.findByUsername(userFullDetails);
				
				if(user == null) {
					status = new SendDRStatus("100011", PropertyUtil.getErrorMessage(100011, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					sendDRResponse = prepareResponseObject(status);
					return sendDRResponse;
					
				}
				//validate disabled user
				if(!user.isEnabled()){
					status = new SendDRStatus("300011", PropertyUtil.getErrorMessage(300011, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					sendDRResponse = prepareResponseObject(status);
					return sendDRResponse;
				}
				jsonReceiptObj = (JSONObject)jsonBodyObj.get("Receipt");
				
				//save DR Json in db for further processing
				DigitalReceiptsJSON digitalReceiptsJSON = saveDRJson(user,sendDRRequest.getJsonValue(), user.getUserId(), jsonReceiptObj.get("DocSID").toString() , mode);
				
				String emailId = (String)jsonReceiptObj.get("BillToEMail");
				if(emailId == null || emailId.trim().length() == 0 ) {
					status = new SendDRStatus("300009", PropertyUtil.getErrorMessage(300009, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					sendDRResponse = prepareResponseObject(status);
					return sendDRResponse;
				}else if(!Utility.validateEmail(emailId)){
					status = new SendDRStatus("300010", PropertyUtil.getErrorMessage(300010, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					sendDRResponse = prepareResponseObject(status);
					return sendDRResponse;
				}
				
				
			    String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
			    sendDRResponse = validateUserDRSettings(userJSONSettings, user.getUserId());
			    if(sendDRResponse != null){
			     return sendDRResponse;
			    }
			    
			    //check for sendEreceipt flag value in DR request
			    if(jsonHeadUserObj !=null){
				    String sendEreceipt = (String)jsonHeadUserObj.get("emailReceipt");
				    if(sendEreceipt != null && sendEreceipt.equals("N")){
				    	logger.info("send DR flag empty/disabled in the request");
				    	status = new SendDRStatus("300004", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						sendDRResponse = prepareResponseObject(status);
						return sendDRResponse;
				    }
			    }
				logger.info("dr body request = "+jsonBodyObj);
				//jsonMainObj = (JSONObject)JSONValue.parse(sendDRRequest.getJsonValue());
				//jsonBodyObj = (JSONObject)jsonMainObj.get("Body");
				//JSONObject jsonBodyObj = null;
				
				if(sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL)){
					if(jsonBodyObj == null || jsonReceiptObj == null){
						status = new SendDRStatus("300005", PropertyUtil.getErrorMessage(300005, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			    		sendDRResponse = prepareResponseObject(status);
						return sendDRResponse;
					}
				}
				else if(sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)){
					if(jsonMainObj == null || jsonBodyObj == null || jsonReceiptObj == null) {
						status = new SendDRStatus("300005", PropertyUtil.getErrorMessage(300005, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						sendDRResponse = prepareResponseObject(status);
						return sendDRResponse;
					}
		    	}
				
				//find user selected template
				/*String userSelectedTemplate = getUserSelectedTemplate(user.getUserId());
				String selTemplateName=userSelectedTemplate;
				if(userSelectedTemplate == null) {
					userSelectedTemplate = "SYSTEM:Corporate_template";
		    	}*/
				
				
				//String selTemplateName=null;
				String templateContent = null;
				String userSelectedTemplate = null;
				String[] templateArr = null;
				DigitalReceiptUserSettings digitalReceiptUserSettings  = null;
								
				if(user.isZoneWise()){
					String storeNo = (String)jsonReceiptObj.get("Store");
					String SubsidiaryNo = (String)jsonReceiptObj.get("SubsidiaryNumber");
					//find a zone of this SBS and store
					digitalReceiptUserSettings  = getZoneMappedTemplate(user, storeNo, SubsidiaryNo );
					
					
					if(digitalReceiptUserSettings != null && !digitalReceiptUserSettings.isSettingEnable()) {
						status = new SendDRStatus("3000012", PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						SendDRResponse posDRResponse  = prepareResponseObject(status);
			    		return posDRResponse;
					}
					
				}else{
					DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
					digitalReceiptUserSettings = digitalReceiptUserSettingsDao.findByUserId(user.getUserId());
				}
				
				if(digitalReceiptUserSettings == null) {
					status = new SendDRStatus("3000011", PropertyUtil.getErrorMessage(3000011, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					sendDRResponse = prepareResponseObject(status);
					return sendDRResponse;
				}
					
				if(!digitalReceiptUserSettings.isEnabled()) {//stop sending if the setting are paused for that zone
					status = new SendDRStatus("3000012", PropertyUtil.getErrorMessage(3000012, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					SendDRResponse posDRResponse  = prepareResponseObject(status);
		    		return posDRResponse;
				}

				userSelectedTemplate = digitalReceiptUserSettings.getSelectedTemplateName();
				
				if(digitalReceiptUserSettings.getMyTemplateId() != null){
				
					templateArr = getTemplateContent(digitalReceiptUserSettings.getMyTemplateId(), user.getUserId());
					templateContent = templateArr[0];
					
					if(templateContent == null ){
						status = new SendDRStatus("300006", PropertyUtil.getErrorMessage(300006, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						sendDRResponse = prepareResponseObject(status);
						return sendDRResponse;
					}
					
				}else{
				
					templateArr = getSelectedSystemTemplateContent(userSelectedTemplate, user.getUserId());
					templateContent = templateArr[0];
					
				
					if(templateContent == null ){
						status = new SendDRStatus("300007", PropertyUtil.getErrorMessage(300007, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						sendDRResponse = prepareResponseObject(status);
						return sendDRResponse;
					}

					
				}
				
				

				//templateID = DigitalReceiptUserSettings.getMyTemplateId();
				//logger.debug("userSelectedTemplate =="+userSelectedTemplate);
								
															
					
					//find user selected template
				/*	userSelectedTemplate = getUserSelectedTemplate(user.getUserId());
					selTemplateName=userSelectedTemplate;
					if(userSelectedTemplate == null) {
						userSelectedTemplate = "SYSTEM:Corporate_template";
			    	}
					
									
					//find template content
					templateContent = getTemplateContent(userSelectedTemplate, user.getUserId());
									
					if(templateContent == null && userSelectedTemplate.indexOf("MY_TEMPLATE:") != -1){
						status = new SendDRStatus("300006", PropertyUtil.getErrorMessage(300006, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						sendDRResponse = prepareResponseObject(status);
						return sendDRResponse;
					}
					else if(templateContent == null && userSelectedTemplate.indexOf("SYSTEM:") != -1){
						status = new SendDRStatus("300007", PropertyUtil.getErrorMessage(300007, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						sendDRResponse = prepareResponseObject(status);
						return sendDRResponse;
					}
				
				*/
				// Add to Email Queue
				String subject = "Your Purchase Receipt";
				/*DigitalReceiptSenderOPT digitalReceiptSender = new DigitalReceiptSenderOPT(subject, templateContent,
						emailId,user, jsonBodyObj ,digitalReceiptsJSON.getDrjsonId(),digitalReceiptUserSettings);
				*/
				DigitalReceiptSenderOPT digitalReceiptSender = new DigitalReceiptSenderOPT(subject, templateContent,
						emailId,templateArr[1],  user, jsonBodyObj ,digitalReceiptsJSON.getDrjsonId(), digitalReceiptUserSettings);
					
				
				if(sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL)){
					digitalReceiptSender.start();
				}else if(sendDRRequest.getAction().equals(OCConstants.DIGITAL_RECEIPT_XML_ACTION_SENDEMAIL)){
					digitalReceiptSender.processDR();
				}
		    	//statusFlag = 1;
				
				status = new SendDRStatus("0", "Digital receipt submitted successfully . Mail would be sent in a moment.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				sendDRResponse = prepareResponseObject(status);
		    	logger.debug("*************** EXITING***********************");
				return sendDRResponse;
			} catch (Exception e) {
				logger.error("Exception ::" , e);
				status = new SendDRStatus("300005", "Unable to parse the JSON request.", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				sendDRResponse = prepareResponseObject(status);
				return sendDRResponse;
				//throw new BaseServiceException("Exception occured while processing processPosDRRequest::::: ", e);
			}
		}
		
		private String getTemplateContent(String userSelectedTemplate, Long userId) throws BaseServiceException {
			
			String templateContent = null;
			String selectedTemplate = null;
			
			try{
				if(userSelectedTemplate.indexOf("MY_TEMPLATE:") != -1) {
		    		
		    		selectedTemplate = userSelectedTemplate.substring(12);
		    		DigitalReceiptMyTemplatesDao digitalReceiptMyTemplatesDao = (DigitalReceiptMyTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_MYTEMPLATES_DAO);
		    		DigitalReceiptMyTemplate digitalReceiptMyTemplates = digitalReceiptMyTemplatesDao.findByUserNameAndTemplateName(userId, selectedTemplate);
		    		
		    		if(digitalReceiptMyTemplates != null){
		    			templateContent =  digitalReceiptMyTemplates.getContent();
		    		}
		    		
		    	} else if (userSelectedTemplate.indexOf("SYSTEM:") != -1) {
		    		
		    		selectedTemplate = userSelectedTemplate.substring(7);
		    		String digiReciptsDir = PropertyUtil.getPropertyValue("DigitalRecieptsDirectory");
		    		File templateFile = new File(digiReciptsDir + "/" + selectedTemplate + "/index.html");
	
		    		FileReader fr = new FileReader(templateFile);
		    		BufferedReader br2 = new BufferedReader(fr);
		    		String line = "";
		    		StringBuffer sb2 = new StringBuffer();
		    		while((line = br2.readLine())!= null) {
		    			sb2.append(line);
		    		}
		    		templateContent = sb2.toString();
		    	}
			}catch(Exception e){
				throw new BaseServiceException("Exception occured while getting template content ", e);
			}
			return templateContent;
		}
		
		private String[] getSelectedSystemTemplateContent(String userSelectedTemplate, Long userId) throws BaseServiceException {
			String selectedTemplate = null;
			String templateContent = null;
			String[] strArr = new String[2];
			try{
				if (userSelectedTemplate.indexOf("SYSTEM:") != -1) {

					selectedTemplate = userSelectedTemplate.substring(7);
					String digiReciptsDir = PropertyUtil.getPropertyValue("DigitalRecieptsDirectory");
					File templateFile = new File(digiReciptsDir + "/" + selectedTemplate + "/index.html");

					FileReader fr = new FileReader(templateFile);
					BufferedReader br2 = new BufferedReader(fr);
					String line = "";
					StringBuffer sb2 = new StringBuffer();
					while((line = br2.readLine())!= null) {
						sb2.append(line);
					}
					templateContent = sb2.toString();
					
					strArr[0] = templateContent;
					strArr[1] = OCConstants.LEGACY_EDITOR;
				}
			}catch(Exception e){
				throw new BaseServiceException("Exception occured while getting template content ", e);
			}

			return strArr;
		}
	
		private String getUserSelectedTemplate(Long userId) throws BaseServiceException {
			
			String selectedTemplate = null; 
			try{
			DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
	    	selectedTemplate = digitalReceiptUserSettingsDao.findUserSelectedTemplate(userId);
	    	
			}catch(Exception e){
				throw new BaseServiceException("Exception occured while getting user selected template ", e);
			}
			return selectedTemplate;
		}
		
		
		private SendDRResponse validateUserDRSettings(String userJSONSettings, Long userId) throws BaseServiceException {
			
			SendDRResponse posDRResponse = null;
			try{
				
				SendDRStatus status = null;
				//String userJSONSettings = PropertyUtil.getPropertyValueFromDB("DigitalReceiptSetting");
		    	if(userJSONSettings == null) {
		    		status = new SendDRStatus("300003", PropertyUtil.getErrorMessage(300003, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
		    		return posDRResponse;
		    	}
		    	
		    	DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
		    	boolean enabled = digitalReceiptUserSettingsDao.findIsUserEnabled(userId);
		    	if(!enabled) {
		    		status = new SendDRStatus("300004", PropertyUtil.getErrorMessage(300004, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					posDRResponse = prepareResponseObject(status);
		    		return posDRResponse;
		    	}
				
			}catch(Exception e){
				throw new BaseServiceException("Exception occured while validating user dr settings ", e);
			}
			return posDRResponse;
		}
		
		
		private DigitalReceiptsJSON saveDRJson(Users user,String jsonValue, Long userId, String docSid, String mode) throws BaseServiceException {
			
			DigitalReceiptsJSON digitalReceiptsJSON = null;
			try{
			DigitalReceiptsJSONDao digitalReceiptsJSONDao = (DigitalReceiptsJSONDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPTS_JSON_DAO);
			DigitalReceiptsJSONDaoForDML digitalReceiptsJSONDaoForDML = (DigitalReceiptsJSONDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DIGITAL_RECEIPTS_JSON_DAOForDML);
			
			digitalReceiptsJSON = new DigitalReceiptsJSON();
	    	digitalReceiptsJSON.setJsonStr(jsonValue);
	    	//digitalReceiptsJSON.setStatus(Constants.DR_JSON_PROCESS_STATUS_NEW);
	    	digitalReceiptsJSON.setStatus(user.isDigitalReceiptExtraction() ? Constants.DR_JSON_PROCESS_STATUS_NEW : OCConstants.DR_STATUS_IGNORED);
	    	//iteration count used in DR to Lty extraction
	    	int iterationCount=0;
	    	digitalReceiptsJSON.setRetryForLtyExtraction(iterationCount);
	    	digitalReceiptsJSON.setUserId(userId);
	    	digitalReceiptsJSON.setCreatedDate(Calendar.getInstance());
            digitalReceiptsJSON.setDocSid(docSid);
	    	digitalReceiptsJSON.setMode(mode);
	    	//digitalReceiptsJSONDao.saveOrUpdate(digitalReceiptsJSON);
	    	digitalReceiptsJSONDaoForDML.saveOrUpdate(digitalReceiptsJSON);
			}catch(Exception e){
				throw new BaseServiceException("Exception occured while saving DR json string ", e);
			}
			return digitalReceiptsJSON;
		}
		
		private SendDRResponse validateJsonFields(SendDRRequest sendDRRequest) throws BaseServiceException {
			
			String userFullDetails = null;
			SendDRStatus status = null;
			SendDRResponse posDRResponse = null;
			
			if( (sendDRRequest.getUserName() != null && sendDRRequest.getUserName().length() > 1) &&
					(sendDRRequest.getUserOrg() != null &&  sendDRRequest.getUserOrg().length() > 1) ) {	
				userFullDetails =  sendDRRequest.getUserName() + Constants.USER_AND_ORG_SEPARATOR + sendDRRequest.getUserOrg();
			} 

			//status = validateUser(userFullDetails);
			if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getSTATUS())){
				posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			JSONObject jsonMainObj = null;
			if(sendDRRequest.getJsonValue() != null && sendDRRequest.getJsonValue().length() > 1) {
				jsonMainObj = (JSONObject)JSONValue.parse(sendDRRequest.getJsonValue());
			} else {
	    		status = new SendDRStatus("300002", PropertyUtil.getErrorMessage(300002, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
	    		posDRResponse = prepareResponseObject(status);
				return posDRResponse;
			}
			
			return null;
		}
		
		
		private SendDRStatus validateUser(String userFullDetails)  throws BaseServiceException  {
			SendDRStatus status = null;
			if(userFullDetails == null || userFullDetails.isEmpty()) {
				logger.debug("Required user name and Organization fields are not valid ... returning ");
				status = new SendDRStatus("300001", PropertyUtil.getErrorMessage(300001, OCConstants.ERROR_DR_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status ;
			}
			return status ;
		}

		
		private SendDRResponse prepareResponseObject(SendDRStatus status) {
			SendDRResponse sendDRResponse = new SendDRResponse();
			SendDRResponseInfo responseInfo = new SendDRResponseInfo(status);
			sendDRResponse.setRESPONSEINFO(responseInfo);
			return sendDRResponse;
		}
		
		
		
		private DigitalReceiptUserSettings  getZoneMappedTemplate(Users user, String storeNo, String SBSNo) throws BaseServiceException {
			try{
			UsersDao usersDao  = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			
			List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());//finding this power user's domain
			logger.debug("==STEP I====");
			if(domainsList == null) return null;
			
			logger.info("==domainsList.get(0).getDomainId()=="+domainsList.get(0).getDomainId());
			//String domainStr = domainsList.get(0).getDomainId().longValue()+Constants.STRING_NILL;
			
			OrganizationStoresDao orgStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			OrganizationStores orgStore = orgStoresDao.findOrgStoreObject(user.getUserOrganization().getUserOrgId(), 
											domainsList.get(0).getDomainId(), SBSNo, storeNo);//find the orgstorerecord to find the which zone this belongs to
			logger.debug("==STEP II====");
			if(orgStore == null) return null;
			
			ZoneDao zoneDao  = (ZoneDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_ZONE_DAO);
			OrganizationZone orgZone = zoneDao.findBy(orgStore.getStoreId());
			
			logger.debug("==STEP III====");
			if(orgZone == null) return null;
			
			DigitalReceiptUserSettings  DigitalReceiptUserSettings = null; 
			
			DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
			DigitalReceiptUserSettings = digitalReceiptUserSettingsDao.findUserSelectedTemplateBy(user.getUserId(), orgZone.getZoneId());//find the template settings for the zone
			//DigitalReceiptUserSettings = zoneDao.findUserSelectedTemplate(userId, storeNo, SBSNo);
			return DigitalReceiptUserSettings;
	    	
			}catch(Exception e){
				throw new BaseServiceException("Exception occured while getting user selected template ", e);
			}
		}
		
		
		private DigitalReceiptUserSettings  getUserSelectedTemplate(Long userId, String storeNo, String SBSNo) throws BaseServiceException {
			
			DigitalReceiptUserSettings  DigitalReceiptUserSettings = null; 
			try{
			DigitalReceiptUserSettingsDao digitalReceiptUserSettingsDao = (DigitalReceiptUserSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_USER_SETTINGS_DAO);
			DigitalReceiptUserSettings = digitalReceiptUserSettingsDao.findUserSelectedTemplate(userId, storeNo, SBSNo);
	    	
			}catch(Exception e){
				throw new BaseServiceException("Exception occured while getting user selected template ", e);
			}
			return DigitalReceiptUserSettings;
		}
		
		
	private String[] getTemplateContent(Long myTemplateId, Long userId) throws BaseServiceException {
			
			String templateContent = null;
			
			String[] strArr = new String[2];
			
			try{
					
		    		DigitalReceiptMyTemplatesDao digitalReceiptMyTemplatesDao = (DigitalReceiptMyTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.DIGITAL_RECEIPT_MYTEMPLATES_DAO);
		    		//DigitalReceiptMyTemplate digitalReceiptMyTemplates = digitalReceiptMyTemplatesDao.findByOrgIdAndTemplateId(orgId, myTemplateId);
		    		DigitalReceiptMyTemplate digitalReceiptMyTemplates = digitalReceiptMyTemplatesDao.findByUserId_TemplateId(userId, myTemplateId);
		    		
					if(digitalReceiptMyTemplates != null){
						templateContent =  digitalReceiptMyTemplates.getContent();
						strArr[0] = templateContent; 
						strArr[1] = digitalReceiptMyTemplates.getEditorType();
					}
		    		
		    		
/*		    		if(digitalReceiptMyTemplates != null && (digitalReceiptMyTemplates.getContent()!=null && !digitalReceiptMyTemplates.getContent().isEmpty())){

		    			StringBuffer contentSb = null;   				
		    			if(digitalReceiptMyTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE)) {
		    				contentSb = new StringBuffer();
		    				Document doc = Jsoup.parseBodyFragment(digitalReceiptMyTemplates.getContent());
		    				//Elements bodyele = doc.getElementsByTag("body");
		    				//Element body = doc.body();
		    				Elements elements = doc.select("body").first().children();
		    				if(elements.isEmpty()){
		    					logger.warn("BEE Html content is null (Body Element not present) in the EmailContent object");
		    					logger.info("This is HTML CONTENT IS EMPTY (Body Element not present)................" );
		    					return null;
		    				}else{
		    					logger.info("this is HTML CONTENT IS NOT EMPTY................" );
		    					for (Element el : elements) {
		    						// System.out.println(el.toString());
		    						contentSb.append(el.toString());
		    					}
		    				}
		    				templateContent=contentSb.toString();
		    			}else
		    				templateContent =  digitalReceiptMyTemplates.getContent();
		    		}*/
		    		
		    		
		    	
			}catch(Exception e){
				throw new BaseServiceException("Exception occured while getting template content ", e);
			}
			return strArr;
		}
		
		

}
