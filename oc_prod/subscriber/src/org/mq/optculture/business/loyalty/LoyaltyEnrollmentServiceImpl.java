package org.mq.optculture.business.loyalty;

import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.LoyaltyEnrollmentHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.loyalty.AmountDetails;
import org.mq.optculture.model.loyalty.Balances;
import org.mq.optculture.model.loyalty.CustomerInfo;
import org.mq.optculture.model.loyalty.EnrollResponse;
import org.mq.optculture.model.loyalty.EnrollmentInfo;
import org.mq.optculture.model.loyalty.HeaderInfo;
import org.mq.optculture.model.loyalty.LoyaltyEnrollJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyEnrollJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyEnrollRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyEnrollResponseObject;
import org.mq.optculture.model.loyalty.StatusInfo;
import org.mq.optculture.model.loyalty.UserDetails;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.SBToOCJSONTranslator;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;

import com.google.gson.Gson;

/**
 * Loyalty enrolment service which communicates external system sparkbase through API.
 * Input: customerinfo and with/without card info
 * Output: customerinfo and cardinfo
 * Sparkbase response is processed in OC and gives status. 
 * 
 * @author Venkata Rathnam D
 *
 */
public class LoyaltyEnrollmentServiceImpl implements LoyaltyEnrollmentService{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	/**
	 * BaseService Request called by rest service controller.
	 * @return BaseResponseObject
	 */
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)
			throws BaseServiceException {
		logger.info("Started processing baserequest...");
		BaseResponseObject responseObject = null;
		try{
		String serviceRequest = baseRequestObject.getAction();
		String requestJson = baseRequestObject.getJsonValue();
		Gson gson = new Gson();
		LoyaltyEnrollJsonResponse jsonResponseObject = null;
		LoyaltyEnrollResponseObject enrollResponse = null;
		if(serviceRequest == null || !serviceRequest.equals(OCConstants.LOYALTY_SERVICE_ACTION_ENROLMENT)){
			logger.info("servicerquest is null");
			jsonResponseObject = new LoyaltyEnrollJsonResponse();
			enrollResponse = new LoyaltyEnrollResponseObject();
			
			StatusInfo statusInfo = new StatusInfo("101001", ""+PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			enrollResponse.setSTATUS(statusInfo);
			jsonResponseObject.setENROLLMENTRESPONSE(enrollResponse);
			
			String responseJson = gson.toJson(jsonResponseObject);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
			logger.info("Completed processing baserequest... ");
			return responseObject;
		}
		
			LoyaltyEnrollRequestObject enrollRequest = null;
			LoyaltyEnrollJsonRequest jsonRequestObject = null;
			
			try{
				jsonRequestObject = gson.fromJson(requestJson, LoyaltyEnrollJsonRequest.class);
			}catch(Exception e){
				jsonResponseObject = new LoyaltyEnrollJsonResponse();
				enrollResponse = new LoyaltyEnrollResponseObject();
				
				StatusInfo statusInfo = new StatusInfo("101002", ""+PropertyUtil.getErrorMessage(101002, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse.setSTATUS(statusInfo);
				jsonResponseObject.setENROLLMENTRESPONSE(enrollResponse);
				
				String responseJson = gson.toJson(jsonResponseObject);
				responseObject = new BaseResponseObject();
				responseObject.setAction(serviceRequest);
				responseObject.setJsonValue(responseJson);
				logger.info("Completed processing baserequest... ");
				return responseObject;
			}
			enrollRequest = jsonRequestObject.getENROLLMENTREQ();
			
			LoyaltyEnrollmentService loyaltyEnrollService = (LoyaltyEnrollmentService) ServiceLocator.getInstance().getServiceByName(OCConstants.LOYALTY_ENROLMENT_BUSINESS_SERVICE);
			enrollResponse = loyaltyEnrollService.processEnrollmentRequest(enrollRequest, OCConstants.LOYALTY_ONLINE_MODE, baseRequestObject.getTransactionId(), baseRequestObject.getTransactionDate());
			jsonResponseObject = new LoyaltyEnrollJsonResponse();
			jsonResponseObject.setENROLLMENTRESPONSE(enrollResponse);
	
			//Convert Object to JSON string
			String responseJson = gson.toJson(jsonResponseObject);
			responseObject = new BaseResponseObject();
			responseObject.setAction(serviceRequest);
			responseObject.setJsonValue(responseJson);
		}catch(Exception e){
			logger.error("Exception in loyalty base service.");
			throw new BaseServiceException("Server Error.");
		}
			logger.info("Completed processing baserequest... ");
			return responseObject;

	}
	/**
	 * Handles the complete process of Loyalty Enrollment.
	 * 
	 * @param enrollRequest
	 * @return enrollResponse
	 * @throws BaseServiceException
	 */
	@Override
	public LoyaltyEnrollResponseObject processEnrollmentRequest(
			LoyaltyEnrollRequestObject enrollRequest, String mode, String trxID, String trxDate)
			throws BaseServiceException {
		
		logger.info("Started processing enrollment request...");
		LoyaltyEnrollResponseObject enrollResponse = null;
		StatusInfo statusInfo = null;
		Users user = null;
		SparkBaseLocationDetails sparkbaseLocation = null;
		
		try{
			
			statusInfo = validateEnrollmentJsonData(enrollRequest);
			user = getUser(enrollRequest.getUSERDETAILS().getUSERNAME(), enrollRequest.getUSERDETAILS().getORGANISATION(),
					enrollRequest.getUSERDETAILS().getTOKEN());
			if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				enrollResponse = prepareEnrollmentResponse(enrollRequest.getHEADERINFO(), enrollRequest.getUSERDETAILS(),
						enrollRequest.getENROLLMENTINFO(), null, statusInfo, enrollRequest.getCUSTOMERINFO(), null);
				return enrollResponse;
			}
			
			if(user == null){
				statusInfo = new StatusInfo("101013", PropertyUtil.getErrorMessage(101013, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					enrollResponse = prepareEnrollmentResponse(enrollRequest.getHEADERINFO(), enrollRequest.getUSERDETAILS(),
							enrollRequest.getENROLLMENTINFO(), null, statusInfo, enrollRequest.getCUSTOMERINFO(), null);
					return enrollResponse;
				}
			}
			
			//need to redirect the request to OC loyalty service based on the user flag
			if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equals(user.getloyaltyServicetype())){
				
				LoyaltyTransaction loyaltyTrx = findTransaction(trxID);
				if(loyaltyTrx != null) {
					LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
					LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
				    
					loyaltyTrx.setLoyaltyServiceType(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC);
				    //loyaltyTransactionDao.saveOrUpdate(loyaltyTrx);
				    loyaltyTransactionDaoForDML.saveOrUpdate(loyaltyTrx);
					
				}
				
				SBToOCJSONTranslator translator = new SBToOCJSONTranslator();
				Object requestObject = translator.convertSbReqToOC(enrollRequest, OCConstants.LOYALTY_TRANSACTION_ENROLMENT);
				LoyaltyEnrollRequest loyaltyEnrollRequest = (LoyaltyEnrollRequest)requestObject;
				LoyaltyEnrollmentOCService loyaltyEnrollService = (LoyaltyEnrollmentOCService)ServiceLocator.getInstance().getServiceById(OCConstants.LOYALTY_ENROLMENT_OC_BUSINESS_SERVICE);
				LoyaltyEnrollResponse responseObject = loyaltyEnrollService.processEnrollmentRequest(loyaltyEnrollRequest, OCConstants.LOYALTY_OFFLINE_MODE, trxID, trxDate);
				
				EnrollmentInfo enrollInfoRequest = enrollRequest.getENROLLMENTINFO();
				enrollInfoRequest.setTIERNAME(responseObject.getMembership().getTierName());
				
				statusInfo = translator.convertStatus(responseObject.getStatus());
				if(OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
					enrollResponse = prepareEnrollmentResponse(enrollRequest.getHEADERINFO(), enrollRequest.getUSERDETAILS(),
							enrollInfoRequest, null, statusInfo, enrollRequest.getCUSTOMERINFO(), null);
					
					//enrollResponse.setResponseObject(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC);
					return enrollResponse;
				}else{/*
					enrollResponse = translator.convertEnrollmentResponse(responseObject);
					enrollResponse.setHEADERINFO(enrollRequest.getHEADERINFO());
					enrollResponse.setSTATUS(statusInfo);
					EnrollmentInfo enrollmentInfo = prepareEnrollmentInfo(responseObject.getMembership().getCardNumber(), responseObject.getMembership().getCardPin(),
							enrollRequest.getENROLLMENTINFO().getCARDTYPE(), enrollRequest.getENROLLMENTINFO().getEMPID(),
							enrollRequest.getENROLLMENTINFO().getSTORELOCATIONID());
					enrollResponse.setENROLLMENTINFO(enrollmentInfo);
					enrollResponse.setResponseObject(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC);
					return enrollResponse;
					
				*/

					//enrollResponse = translator.convertEnrollmentResponse(responseObject);
					//enrollResponse.setHEADERINFO(enrollRequest.getHEADERINFO());
					//enrollResponse.setSTATUS(statusInfo);
					EnrollmentInfo enrollmentInfo = prepareEnrollmentInfo(responseObject.getMembership().getCardNumber(), responseObject.getMembership().getCardPin(),
							enrollRequest.getENROLLMENTINFO().getCARDTYPE(), enrollRequest.getENROLLMENTINFO().getEMPID(),
							enrollRequest.getENROLLMENTINFO().getSTORELOCATIONID(), responseObject.getMembership().getTierName());
					
					enrollResponse = prepareEnrollmentResponse(enrollRequest.getHEADERINFO(), enrollRequest.getUSERDETAILS(),
							enrollmentInfo, null, statusInfo, enrollRequest.getCUSTOMERINFO(), enrollRequest.getAMOUNTDETAILS());
					enrollResponse.setENROLLMENTINFO(enrollmentInfo);
					//enrollResponse.setResponseObject(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC);
					return enrollResponse;
					
				}
				
			
			
			
			}
			
			statusInfo = validateUserSparkbaseSettings(user, enrollRequest.getUSERDETAILS().getTOKEN(), enrollRequest.getENROLLMENTINFO().getSTORELOCATIONID());
			if(statusInfo != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(statusInfo.getSTATUS())){
				enrollResponse = prepareEnrollmentResponse(enrollRequest.getHEADERINFO(), enrollRequest.getUSERDETAILS(),
						enrollRequest.getENROLLMENTINFO(), null, statusInfo, enrollRequest.getCUSTOMERINFO(), null);
				return enrollResponse;
			}
			// end VALIDATION
			MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
			MailingList mlList = mailingListDao.findPOSMailingList(user);
			
			if(mlList == null){
				statusInfo = new StatusInfo("101007", PropertyUtil.getErrorMessage(101007, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(enrollRequest.getHEADERINFO(), enrollRequest.getUSERDETAILS(),
						enrollRequest.getENROLLMENTINFO(), null, statusInfo, enrollRequest.getCUSTOMERINFO(), null);
				return enrollResponse;
			}
			
			Contacts jsonContact = prepareContactFromJsonData(enrollRequest.getCUSTOMERINFO(), user.getUserId());  
			Contacts dbContact = validateAndSavedbContact(enrollRequest.getCUSTOMERINFO(), jsonContact, mlList, user);
			
			String cardNumber = null;
			Long cardLong = null;
			if(enrollRequest.getENROLLMENTINFO().getCARDNUMBER() != null && !enrollRequest.getENROLLMENTINFO().getCARDNUMBER().trim().isEmpty()){
				cardLong = OptCultureUtils.validateCardNumber(enrollRequest.getENROLLMENTINFO().getCARDNUMBER());
				if(cardLong == null){
					statusInfo = new StatusInfo("100107", PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(enrollRequest.getHEADERINFO(), enrollRequest.getUSERDETAILS(),
							enrollRequest.getENROLLMENTINFO(), null, statusInfo, enrollRequest.getCUSTOMERINFO(), null);
					return enrollResponse;
				}
				cardNumber = ""+cardLong;
			}
			sparkbaseLocation = getSparkbaseLocation(user.getUserOrganization().getUserOrgId());
			if(sparkbaseLocation.isMobileUnique()){
				
				String phone = enrollRequest.getCUSTOMERINFO().getPHONE();
				if(phone == null || phone.isEmpty()){
					statusInfo = new StatusInfo("100106", PropertyUtil.getErrorMessage(100106, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(enrollRequest.getHEADERINFO(), enrollRequest.getUSERDETAILS(),
							enrollRequest.getENROLLMENTINFO(), null, statusInfo, enrollRequest.getCUSTOMERINFO(), null);
					return enrollResponse;
					
				}
			}
			if(cardNumber != null){
				enrollRequest.getENROLLMENTINFO().setCARDNUMBER(cardNumber);
			}
			
			LoyaltyEnrollmentHelper enrollHelper = null;
			EnrollResponse sbenrollResponse = null;
			enrollHelper = new LoyaltyEnrollmentHelper(dbContact, jsonContact,
					cardNumber, enrollRequest.getENROLLMENTINFO().getCARDPIN(),
					enrollRequest.getHEADERINFO().getSTORENUMBER(), enrollRequest.getHEADERINFO().getSUBSIDIARYNUMBER(), user, sparkbaseLocation,
					enrollRequest.getENROLLMENTINFO().getCARDTYPE(), enrollRequest.getENROLLMENTINFO().getEMPID(), mode, enrollRequest.getHEADERINFO().getSOURCETYPE());
			sbenrollResponse = enrollHelper.enroll();
			
			String cardnumberStr = "";
			if(sbenrollResponse.getCardNumber() == null || sbenrollResponse.getCardNumber().trim().isEmpty()){
				cardnumberStr = enrollRequest.getENROLLMENTINFO().getCARDNUMBER();
			}
			else{
				cardnumberStr = sbenrollResponse.getCardNumber();
			}
			
			EnrollmentInfo enrollmentInfo = prepareEnrollmentInfo(cardnumberStr, sbenrollResponse.getCardPin(),
					enrollRequest.getENROLLMENTINFO().getCARDTYPE(), enrollRequest.getENROLLMENTINFO().getEMPID(),
					enrollRequest.getENROLLMENTINFO().getSTORELOCATIONID(), "");
			
			statusInfo = prepareStatusInfo(sbenrollResponse.getErrorCode(), sbenrollResponse.getErrorMessage(),
					sbenrollResponse.getStatus());
			
			enrollResponse = prepareEnrollmentResponse(enrollRequest.getHEADERINFO(), enrollRequest.getUSERDETAILS(),
					enrollmentInfo, null, statusInfo, enrollRequest.getCUSTOMERINFO(), enrollRequest.getAMOUNTDETAILS());
			//send loyalty alert count email
			
			
			
			if(statusInfo.getERRORCODE().equals("0")){
				sendCardCountAlerts(sparkbaseLocation, user);
			}
			//Send Loyalty Registration Email
			if(statusInfo.getERRORCODE().equals("0") && dbContact.getEmailId() != null && mlList.isCheckLoyaltyOptin()){
				sendLoyaltyTemplateMail(mlList.getLoyaltyCutomTempId(), user, sbenrollResponse.getCardNumber(),
						sbenrollResponse.getCardPin(), dbContact.getContactId(), dbContact.getFirstName(), dbContact.getEmailId());
			}
			
			logger.info("Completed processing enrollment request...");
			return enrollResponse;
		}catch(Exception e){
			logger.error("Exception in processing enrollment request...", e);
			statusInfo = new StatusInfo("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			enrollResponse = prepareEnrollmentResponse(enrollRequest.getHEADERINFO(), enrollRequest.getUSERDETAILS(),
					enrollRequest.getENROLLMENTINFO(), null, statusInfo, enrollRequest.getCUSTOMERINFO(), null);
			return enrollResponse;
		}
		
	}
	
	private EnrollmentInfo prepareEnrollmentInfo(String cardNumber, String cardPin, String cardType, String empId, String locationId, String tierName ) throws Exception {
		//logger.info("Started prepareEnrollmentInfo method >>>>>");
		EnrollmentInfo enrollmentInfo = new EnrollmentInfo();
		enrollmentInfo.setCARDNUMBER(cardNumber == null ? "" : cardNumber);
		enrollmentInfo.setCARDPIN(cardPin == null ? "" : cardPin);
		enrollmentInfo.setCARDTYPE(cardType == null ? "" : cardType);
		enrollmentInfo.setEMPID(empId == null ? "" : empId);
		enrollmentInfo.setSTORELOCATIONID(locationId == null ? "" : locationId);
		enrollmentInfo.setTIERNAME(tierName == null ? "" : tierName);
		//logger.info("Exited prepareEnrollmentInfo method >>>>>");
		return enrollmentInfo;
	}
	
	private StatusInfo prepareStatusInfo(String errorCode, String errorMessage, String status) throws Exception {
		//logger.info("Entered prepareStatusInfo method >>>>>");
		StatusInfo statusInfo = new StatusInfo();
		statusInfo.setERRORCODE(errorCode);
		statusInfo.setMESSAGE(errorMessage);
		statusInfo.setSTATUS(status);
		//logger.info("Exited prepareStatusInfo method >>>>>");
		return statusInfo;
	}
	
	private Contacts validateAndSavedbContact(CustomerInfo customerInfo, Contacts jsonContact, MailingList mlList, Users user) throws Exception {
		
		Contacts dbContact = findOCContact(jsonContact, user);
		
		PurgeList purgeList = (PurgeList)ServiceLocator.getInstance().getServiceById(OCConstants.PURGELIST);
		boolean updateMLFlag = false;
		boolean isEnableEvent = false;
		boolean isNewContact = false;
		
		if(dbContact == null) {
			logger.info("New Contact...");
			dbContact = prepareContactFromJsonData(customerInfo, user.getUserId());
			logger.info("In Validate Contact method dbContact = "+dbContact);
			//dbContact.setEmailId(jsonContact.getEmailId());
			dbContact.setEmailId(validateEmailId(dbContact.getEmailId()));
			dbContact.setMlBits(mlList.getMlBit());
			dbContact.setUsers(user);
			dbContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
			dbContact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
			dbContact.setCreatedDate(Calendar.getInstance());
			dbContact.setModifiedDate(Calendar.getInstance());
			isNewContact = true;
			updateMLFlag = true;
			isEnableEvent = true;
			
			dbContact.setPurged(false);
			dbContact.setMobilePhone(dbContact.getMobilePhone() == null ? null : Utility.phoneParse(dbContact.getMobilePhone(), user!=null ? user.getUserOrganization() : null ));
			purgeList.checkForValidDomainByEmailId(dbContact);
			validateMobilePhoneStatus(dbContact);
			
			//mlList.setListSize(mlList.getListSize()+1);
			
		}
		else {
			
			if(dbContact.getExternalId() == null || dbContact.getExternalId().trim().isEmpty())
				dbContact.setExternalId(jsonContact.getExternalId());
			
			logger.info("Existing contact.");
			if(dbContact.getMlBits().longValue() == 0l ) {//marked as deleted
				dbContact.setAddressOne(jsonContact.getAddressOne());
				dbContact.setAddressTwo(jsonContact.getAddressTwo());
				dbContact.setAnniversary(jsonContact.getAnniversary());
				dbContact.setBirthDay(jsonContact.getBirthDay());
				dbContact.setCity(jsonContact.getCity());
				dbContact.setCountry(jsonContact.getCountry());
				dbContact.setEmailId(validateEmailId(dbContact.getEmailId()));
				dbContact.setExternalId(jsonContact.getExternalId());
				dbContact.setFirstName(jsonContact.getFirstName());
				dbContact.setGender(jsonContact.getGender());
				dbContact.setLastName(jsonContact.getLastName());
				dbContact.setMobilePhone(jsonContact.getMobilePhone());
				validateMobilePhoneStatus(dbContact);
				
				isEnableEvent = true;
				updateMLFlag = true;
				
				dbContact.setPurged(false);
				dbContact.setMlBits(mlList.getMlBit());
				dbContact.setUsers(user);
				dbContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_POS);
				dbContact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
				purgeList.checkForValidDomainByEmailId(dbContact);
			}
			else{
				long conMlBits = dbContact.getMlBits();
				if(mlList != null && ( (conMlBits & mlList.getMlBit() ) <= 0) ) { //add existing contact to POS if it is not there in it
					dbContact.setMlBits(conMlBits | mlList.getMlBit());
					updateMLFlag = true;
					mlList.setListSize(mlList.getListSize()+1);
				}
			}
		}
		
		dbContact.setLoyaltyCustomer((byte) 1);
		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		contactsDaoForDML.saveOrUpdate(dbContact);
		
		MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
		MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
		if(updateMLFlag) {
			mlList.setListSize(mlList.getListSize()+1);
			mlList.setLastModifiedDate(Calendar.getInstance());
			mailingListDaoForDML.saveOrUpdate(mlList);
		}
		
		if(isEnableEvent){
			EventTriggerEventsObservable eventTriggerEventsObservable=(EventTriggerEventsObservable) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver=(EventTriggerEventsObserver) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			eventTriggerEventsObservable.notifyForWebEvents(user.getUserId().longValue(),
					dbContact.getContactId().longValue(), dbContact.getContactId().longValue() );
			
		}
		
		if(isNewContact  && !mlList.getCheckDoubleOptin() && mlList.isCheckWelcomeMsg()) {
			sendWelcomeEmail(dbContact, mlList, user);
		}
		//logger.info("Exited validatedbContact method >>>>>");
		return dbContact;
	}
	
	private String validateEmailId(String emailId){
		//logger.info("Entered validateEmailId method >>>>");
		if(emailId != null) {
			if(Utility.validateEmail(emailId)) {
				//logger.info("Exited validateEmailId method >>>>");
				return emailId;
			}
		}
		//logger.info("Exited validateEmailId method >>>>");
		return null;
	}
	/**
	 * Validates mobilephone and sets appropriate message
	 * 
	 * @param dbContact
	 */
	private void validateMobilePhoneStatus(Contacts dbContact) throws Exception{
		//logger.info("Entered validateMobilePhoneStatus method >>>>");
		if(dbContact.getMobilePhone() != null && dbContact.getMobilePhone().trim().length() > 0) {
			//dbContact.setMobileStatus(performMobileOptin(dbContact, dbContact.getUsers()));
			try {
				Users user = dbContact.getUsers();
				String phoneStr = Utility.phoneParse(dbContact.getMobilePhone().toString().trim(), user!=null ? user.getUserOrganization() : null );
				if(phoneStr != null ) {
					dbContact.setMobilePhone(phoneStr);
					if(dbContact.getUsers().isEnableSMS() && dbContact.getUsers().isConsiderSMSSettings()){
						dbContact.setMobileStatus(performMobileOptin(dbContact, dbContact.getUsers()));
					}else{
						dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
					}
				}else {
					dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
				}
			} catch(Exception e) {
				logger.error("Exception in phone parse", e);
			}
		}	
		else{
			dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
			dbContact.setMobileOptin(false);
		}
		//logger.info("Exited validateMobilePhoneStatus method >>>>");
	}
	
	/**
	 * Checks whether given contact is exist in oc. It searches by external id, email id and mobile phone.
	 * If given contact is found in db, it returns db contact object.
	 * 
	 * @param jsonContact
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private Contacts findOCContact(Contacts jsonContact, Users user) throws Exception {
		//logger.info("Entered findOCContact method >>>>");
		POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(user.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
		Contacts dbContact = contactsDao.findContactByUniqPriority(priorMap, jsonContact, user.getUserId(), user);
		//logger.info("Exited findOCContact method >>>>");
		return dbContact;
	}
	
	private StatusInfo validateEnrollmentJsonData(LoyaltyEnrollRequestObject enrollRequest) throws Exception{
		//logger.info("Entered validateEnrollmentJsonData method >>>>");
		StatusInfo statusInfo = null;
		if(enrollRequest == null ){
			statusInfo = new StatusInfo(
					"101003", PropertyUtil.getErrorMessage(101003, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(enrollRequest.getUSERDETAILS() == null){
			statusInfo = new StatusInfo(
					"101011", PropertyUtil.getErrorMessage(101011, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(enrollRequest.getENROLLMENTINFO() == null){
			statusInfo = new StatusInfo(
					"101004", PropertyUtil.getErrorMessage(101004, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(enrollRequest.getUSERDETAILS().getUSERNAME() == null || enrollRequest.getUSERDETAILS().getUSERNAME().trim().length() <=0 || 
				enrollRequest.getUSERDETAILS().getORGANISATION() == null || enrollRequest.getUSERDETAILS().getORGANISATION().trim().length() <=0 || 
				enrollRequest.getUSERDETAILS().getTOKEN() == null || enrollRequest.getUSERDETAILS().getTOKEN().trim().length() <=0) {
			statusInfo = new StatusInfo("101012", PropertyUtil.getErrorMessage(101012, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		
		if((enrollRequest.getCUSTOMERINFO().getCUSTOMERID() == null || enrollRequest.getCUSTOMERINFO().getCUSTOMERID().trim().isEmpty())
				&& (enrollRequest.getCUSTOMERINFO().getEMAIL() == null || enrollRequest.getCUSTOMERINFO().getEMAIL().trim().isEmpty())
				&& (enrollRequest.getCUSTOMERINFO().getPHONE() == null || enrollRequest.getCUSTOMERINFO().getPHONE().trim().isEmpty())){
			statusInfo = new StatusInfo("101302", PropertyUtil.getErrorMessage(101302, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		/*if(enrollRequest.getENROLLMENTINFO().getCARDNUMBER() != null && !enrollRequest.getENROLLMENTINFO().getCARDNUMBER().trim().isEmpty()){
			String card = enrollRequest.getENROLLMENTINFO().getCARDNUMBER();
			Pattern digitPattern = Pattern.compile("(\\d+)");  // %B974174416697245^?;974174416697245=?
			  Matcher matcher = null;
			  Long cardLong = null;
			    try {
			    	String cardNum = "";
			    	  matcher = digitPattern.matcher(card);
				          while (matcher.find()) {
				        	  if(cardNum.length() == 15 || cardNum.length() == 16 ) break;
				        	  cardNum += matcher.group(1).trim();
				          } // while
			          card = cardNum;
			          
			    	//logger.debug("Card NUmber After removing Extra char: "+card);
			    	cardLong = Long.parseLong(card);
			    } catch(NumberFormatException e) {
			    	//logger.debug("card format error");
					statusInfo = new StatusInfo(
							"100107", PropertyUtil.getErrorMessage(100107, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return statusInfo;
			    }	
		}*/
		return statusInfo;
	}
	/**
	 * Validates JSON request user parameters to check whether sparkbase location settings are created in OptCulture or not.
	 * 
	 * @return StatusInfo
	 * @throws Exception
	 */
	private StatusInfo validateUserSparkbaseSettings(Users user, String userToken, String storeLocationId) throws Exception {
		
		StatusInfo statusInfo = null;
		
		SparkBaseLocationDetails sparkbaseLocation = getSparkbaseLocation(user.getUserOrganization().getUserOrgId());
		if(sparkbaseLocation == null) {
			statusInfo = new StatusInfo("101006", PropertyUtil.getErrorMessage(101006, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		if(storeLocationId == null || storeLocationId.trim().length() <= 0 || !sparkbaseLocation.getLocationId().equals(storeLocationId.trim())){
			statusInfo = new StatusInfo("101404", PropertyUtil.getErrorMessage(101404, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}
		return statusInfo;
	}
	/**
	 * Fetches Users object from OC database
	 * 
	 * @param userName
	 * @param orgId
	 * @param userToken
	 * @return Users
	 * @throws Exception
	 */
	private Users getUser(String userName, String orgId, String userToken) throws Exception{
		
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findByToken(completeUserName, userToken);
		return user;
	}
	/**
	 * Finds sparkbase location settings object in OptCulture
	 * 
	 * @param userOrgId
	 * @return SparkBaseLocation
	 * @throws Exception
	 */
	private SparkBaseLocationDetails getSparkbaseLocation(Long userOrgId) throws Exception{
		SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao = (SparkBaseLocationDetailsDao)
				ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_LOCATIONDETAILS_DAO);
		List<SparkBaseLocationDetails> sbDetailsList = sparkBaseLocationDetailsDao.findActiveSBLocByOrgId(userOrgId);
		SparkBaseLocationDetails sparkbaseLocation = (sbDetailsList == null) ? null : sbDetailsList.get(0);
		return sparkbaseLocation;
		
	}
	/**
	 * Finds whether given cardNumber enrolled in OC or not
	 * 
	 * @param cardNumber
	 * @param userId
	 * @return ContactsLoyalty
	 * @throws Exception
	 */
	private ContactsLoyalty findLoyaltyCardInOC(String cardNumber, Long userId) throws Exception {
		ContactsLoyalty contactLoyalty = null;
		String parsedCardNumber = OptCultureUtils.parseCardNumber(cardNumber);
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		contactLoyalty = contactsLoyaltyDao.getContactsLoyaltyByCardId(parsedCardNumber, userId);
		return contactLoyalty;
	}
	/**
	 * Checks whether given card number exists under OptCulture sparkase location repository
	 * 
	 * @param cardNumber
	 * @param ocLocationId
	 * @return StatusInfo
	 * @throws Exception
	 */
	/*private StatusInfo findCardInOCRepository(String cardNumber, Long ocLocationId) throws Exception {

		StatusInfo statusInfo = null;
		SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO);
		List<SparkBaseCard> cardList = sparkBaseCardDao.findByCardId(ocLocationId, Long.valueOf(cardNumber));

		if(cardList == null){
			statusInfo = new StatusInfo("200009", "Card Not Available, Upload Given Card To OptCulture.", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return statusInfo;
		}

		if(cardList != null){
			SparkBaseCard sbCard = cardList.get(0);
			if(Constants.SPARKBASE_CARD_STATUS_INVENTORY.equals(sbCard.getStatus())){
				statusInfo = new StatusInfo("200010", "Card Status Inventory, Please Enroll The Given Card.", OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return statusInfo;
			}
		}
		return statusInfo;
	}*/
	/**
	 * Prepares final JSON Response Object
	 * 
	 * @param headerInfo
	 * @param userDetails
	 * @param enrollInfo
	 * @param balances
	 * @param statusInfo
	 * @return LoyaltyEnrollResponseObject
	 * @throws Exception
	 */
	private LoyaltyEnrollResponseObject prepareEnrollmentResponse(HeaderInfo headerInfo, UserDetails userDetails, EnrollmentInfo enrollInfo,
			List<Balances> balances, StatusInfo statusInfo, CustomerInfo customerInfo, AmountDetails amountDetails) throws BaseServiceException {
		LoyaltyEnrollResponseObject enrollResponse = new LoyaltyEnrollResponseObject();
		enrollResponse.setHEADERINFO(headerInfo);
		enrollResponse.setENROLLMENTINFO(enrollInfo);
		enrollResponse.setUSERDETAILS(userDetails);
		enrollResponse.setSTATUS(statusInfo);
		enrollResponse.setCUSTOMERINFO(customerInfo);
		enrollResponse.setAMOUNTDETAILS(amountDetails);
		return enrollResponse;
	}
	/**
	 * prepares contact object with the enroll request customer info data
	 * 
	 * @param customerInfo
	 * @return
	 * @throws Exception
	 */
	private Contacts prepareContactFromJsonData(CustomerInfo customerInfo, Long userId) throws Exception {
	
		//logger.info("Entered prepareContactFromJsonData method >>>>>");
		Contacts inputContact = new Contacts();
		if(customerInfo.getCUSTOMERID() != null && customerInfo.getCUSTOMERID().trim().length() > 0) {
			inputContact.setExternalId(customerInfo.getCUSTOMERID().trim());
			logger.info("customer id: "+customerInfo.getCUSTOMERID());
		}
		if(customerInfo.getEMAIL() != null && customerInfo.getEMAIL().trim().length() > 0) {
			inputContact.setEmailId(customerInfo.getEMAIL().trim());
			logger.info("email id: "+customerInfo.getEMAIL());
		}
		if(customerInfo.getFIRSTNAME() != null && customerInfo.getFIRSTNAME().trim().length() > 0) {
			inputContact.setFirstName(customerInfo.getFIRSTNAME().trim());
		}
		if(customerInfo.getLASTNAME() != null && customerInfo.getLASTNAME().trim().length() > 0) {
			inputContact.setLastName(customerInfo.getLASTNAME().trim());
		}
		if(customerInfo.getADDRESS1() != null && customerInfo.getADDRESS1().trim().length() > 0) {
			inputContact.setAddressOne(customerInfo.getADDRESS1().trim());
		}
		if(customerInfo.getADDRESS2() != null && customerInfo.getADDRESS2().trim().length() > 0) {
			inputContact.setAddressTwo(customerInfo.getADDRESS2().trim());
		}
		if(customerInfo.getCITY() != null && customerInfo.getCITY().trim().length() > 0) {
			inputContact.setCity(customerInfo.getCITY().trim());
		}
		if(customerInfo.getSTATE() != null && customerInfo.getSTATE().trim().length() > 0) {
			inputContact.setState(customerInfo.getSTATE().trim());
		}
		if(customerInfo.getCOUNTRY() != null && customerInfo.getCOUNTRY().trim().length() > 0) {
			inputContact.setCountry(customerInfo.getCOUNTRY().trim());
		}
		if(customerInfo.getPOSTAL() != null && customerInfo.getPOSTAL().trim().length() > 0) {
			inputContact.setZip(customerInfo.getPOSTAL().trim());
		}
		if(customerInfo.getBIRTHDAY() != null && customerInfo.getBIRTHDAY().trim().length() > 0) {
			Calendar cal = MyCalendar.dateString2Calendar(customerInfo.getBIRTHDAY().trim());
			inputContact.setBirthDay(cal);
		}
		if(customerInfo.getANNIVERSARY() != null && customerInfo.getANNIVERSARY().trim().length() > 0) {
			Calendar cal = MyCalendar.dateString2Calendar(customerInfo.getANNIVERSARY().trim());
			inputContact.setAnniversary(cal);
		}
		if(customerInfo.getGENDER() != null && customerInfo.getGENDER().trim().length() > 0) {
			inputContact.setGender(customerInfo.getGENDER().trim());
		}	
		if( customerInfo.getPHONE() != null && customerInfo.getPHONE().trim().length() > 0) {
			inputContact.setMobilePhone(customerInfo.getPHONE());
			logger.info("phone= "+customerInfo.getPHONE());
		}
		//logger.info("Exited prepareContactFromJsonData method >>>>>");
		return inputContact;
	}
	
	

	public String performMobileOptin(Contacts contact, Users currentUser) throws Exception {
	SMSSettingsDao smsSettingsDao  = null;
	UsersDao usersDao = null;
	UsersDaoForDML usersDaoForDML = null;
	ContactsDaoForDML contactsDaoForDML = null;
	try{
		smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
		usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
		contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
	}catch(Exception e){
		logger.error("Exception in getting smssettingsdao or usersdao", e);
	}
		
		SMSSettings smsSettings = null;
		if(SMSStatusCodes.smsProgramlookupOverUserMap.get(currentUser.getCountryType())) smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
		else  smsSettings = smsSettingsDao.findByOrg(currentUser.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
	
		//SMSSettings smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN );
		
		if(smsSettings == null) {
			contact.setMobileOptin(false);
			return Constants.CON_MOBILE_STATUS_ACTIVE;
			
		}
		Users user = smsSettings.getUserId();
		OCSMSGateway ocsmsGateway = GatewayRequestProcessHelper.getOcSMSGateway(user, 
				SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
		if(ocsmsGateway == null) {
			
			return Constants.CON_MOBILE_STATUS_ACTIVE;
		}
		
		currentUser = smsSettings.getUserId();//to avoid lazy=false from contacts
		//do only when the existing phone number is not same with the entered
		byte optin = 0;
		String phone = contact.getMobilePhone();
		String mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;
		if(contact.getOptinMedium() != null) {
			
			if(contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY) ) {
				optin = 1;
			}
			else if(contact.getOptinMedium().startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM) ) {
				optin = 2;
			}
			else if(contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_POS) ) {
				optin = 4;
			}
		}
		
		Users contactOwner = contact.getUsers();
		Byte userOptinVal =	smsSettings.getOptInMedium();
		
		userOptinVal = ( SMSStatusCodes.userOptinMediumMap.get(contactOwner.getCountryType()) && contactOwner.getOptInMedium() != null) ? 
				contactOwner.getOptInMedium() : userOptinVal;
				CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway)ServiceLocator.getInstance().getServiceById(OCConstants.CAPTIWAYTOSMSAPIGATEWAY);
				
		if(smsSettings.isEnable() && 
				userOptinVal != null && 
				(userOptinVal.byteValue() & optin ) > 0 ) {	
			//TODO after the above todo done consider only one among these two conditions on contact
			if(contact.getLastSMSDate() == null && contact.isMobileOptin() != true) {
				
				mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
				contact.setMobileStatus(mobileStatus);
				contact.setLastSMSDate(Calendar.getInstance());
				if(!ocsmsGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocsmsGateway, 1)) {
					
					logger.debug("low credits with clickatell");
					return mobileStatus;
				}
				
				if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() ) ) >=  1) {
					
					String msgContent = smsSettings.getAutoResponse();
					if(msgContent != null) {
						if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType())){
							
							msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
						}
						//msgContent = smsSettings.getMessageHeader() == null ? Constants.STRING_NILL : smsSettings.getMessageHeader() + " "+ msgContent;
					}
					
					mobileStatus = captiwayToSMSApiGateway.sendSingleMobileDoubleOptin(ocsmsGateway, 
							smsSettings.getSenderId(), phone, msgContent, smsSettings.getUserId());

					if(mobileStatus == null) {
						
						mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
					}
					
					if(!mobileStatus.equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)) {
						contactsDaoForDML.updatemobileStatus(phone, mobileStatus, currentUser);
					}
					
					/*currentUser.setUsedSmsCount( (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount()) +1);
					usersDao.saveOrUpdate(currentUser);*/
					//usersDao.updateUsedSMSCount(currentUser.getUserId(), 1);
					usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), 1);
					
					/**
					 * Update Sms Queue
					 */
					SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
					smsQueueHelper.updateSMSQueue(phone,msgContent,Constants.SMS_MSG_TYPE_OPTIN, user, smsSettings.getSenderId());
				}else {
					logger.debug("low credits with user...");
					return mobileStatus;
				}
			}//if
		}//if
		
		else{
			
			if(contact.getMobilePhone() != null) {
				mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;
				contact.setMobileStatus(mobileStatus);
			}
		}
		
		return mobileStatus;
	}
	
	
	public void sendCardCountAlerts(SparkBaseLocationDetails sparkBaseLoc, Users user){
			String supportEmailId = PropertyUtil.getPropertyValueFromDB("SupportEmailId");
			Calendar currentDate = Calendar.getInstance();
			Calendar dbCurrentDate = sparkBaseLoc.getLoyaltyAlertsSentDate();
			SparkBaseCardDao sparkBaseCardDao = null;
			EmailQueueDao emailQueueDao = null;
			EmailQueueDaoForDML emailQueueDaoForDML = null;
			SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao = null;
			SparkBaseLocationDetailsDaoForDML sparkBaseLocationDetailsDaoForDML = null;

			try{
				sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName("sparkBaseCardDao");
				emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
				emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
				sparkBaseLocationDetailsDao = (SparkBaseLocationDetailsDao)ServiceLocator.getInstance().getDAOByName("sparkBaseLocationDetailsDao");
				sparkBaseLocationDetailsDaoForDML = (SparkBaseLocationDetailsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("sparkBaseLocationDetailsDaoForDML");

			}catch(Exception e){
				logger.error("Exception in sending loyalty card count alerts", e);
				return;
			}
			int dateDiff = 0;
			if(dbCurrentDate != null){
				
				dateDiff = (int)((currentDate.getTimeInMillis() - dbCurrentDate.getTimeInMillis())/(1000*60));
			}
			logger.debug("dateDiff ====="+dateDiff);
			 if(dateDiff > 24*60 || dateDiff==0) {
//				String message=null;
				 Long totalInventoryCards= sparkBaseCardDao.findTotalInventoryCardsByLocId(sparkBaseLoc.getSparkBaseLocationDetails_id());
				String messageToUser = PropertyUtil.getPropertyValueFromDB("loyaltyEmailAlertsUserMsg");
				messageToUser = messageToUser.replace(Constants.LOYALTY_ALERTS_PLACEHOLDERS_FNAME,user.getFirstName()).replace(Constants.LOYALTY_ALERTS_PLACEHOLDERS_NOOFCARDS,totalInventoryCards.toString());
				String messageToSupport = PropertyUtil.getPropertyValueFromDB("loyaltyEmailAlertsSupportMsg");
				messageToSupport = messageToSupport.replace(Constants.LOYALTY_ALERTS_PLACEHOLDERS_USERNAME,user.getUserName()).replace(Constants.LOYALTY_ALERTS_PLACEHOLDERS_LOCATIONID,sparkBaseLoc.getLocationId()).replace(Constants.LOYALTY_ALERTS_PLACEHOLDERS_NOOFCARDS,totalInventoryCards.toString());
				Long totalCards=sparkBaseCardDao.findTotalCardsByLocId( sparkBaseLoc.getSparkBaseLocationDetails_id());
				Long count=Long.parseLong(PropertyUtil.getPropertyValueFromDB("DefaultLoyaltyThreshold"));
				if(sparkBaseLoc.isEnableAlerts()) {
					if(sparkBaseLoc.getCountType().equalsIgnoreCase(Constants.SB_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE)) {//percentage
						
						Long percentOfCards=Long.parseLong(sparkBaseLoc.getCountValue());
						count=(percentOfCards * totalCards)/100;
						
						
					}else if(sparkBaseLoc.getCountType().equalsIgnoreCase(Constants.SB_CARDS_AVAILABLE_COUNT_TYPE_COUNT)){//count
						
						count=Long.parseLong(sparkBaseLoc.getCountValue());
					}
				}
					
				logger.debug("count ====="+count+" totalInventoryCards====>"+totalInventoryCards);
					if(totalInventoryCards <= count ) {
						
						if(sparkBaseLoc.isEnableAlerts()) {
							EmailQueue testEmailQueue = new EmailQueue("Loyalty Email Alerts",messageToUser, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, "Active",user.getEmailId(),MyCalendar.getNewCalendar(), user);
							emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
							sparkBaseLoc.setLoyaltyAlertsSentDate(Calendar.getInstance());
							sparkBaseLocationDetailsDaoForDML.saveOrUpdate(sparkBaseLoc);
						}
						EmailQueue testEmailQueue1= new EmailQueue("Loyalty Email Alerts",messageToSupport, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, "Active",supportEmailId,MyCalendar.getNewCalendar(), user);
						//emailQueueDao.saveOrUpdate(testEmailQueue);
						//emailQueueDao.saveOrUpdate(testEmailQueue1);
						emailQueueDaoForDML.saveOrUpdate(testEmailQueue1);
					}
					
			 }
	
	}//sendCardCountAlerts()
	
	public void sendWelcomeEmail(Contacts contact, MailingList mailingList, Users user) {
		logger.info("Entered sendWelcomeEmail method >>>>>");
		//to send the loyalty related email
		EmailQueueDao emailQueueDao = null;
		EmailQueueDaoForDML emailQueueDaoForDML = null;
		CustomTemplatesDao customTemplatesDao = null;
		try{
		emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
		emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
		customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName("customTemplatesDao");
		}catch(Exception e){
			logger.error("Exception in sending welcome email", e);
			return ;
		}
		CustomTemplates custTemplate = null;
		  String message = PropertyUtil.getPropertyValueFromDB("welcomeMsgTemplate");
		  
		  if(mailingList.getWelcomeCustTempId() != null) {
			   
			  custTemplate = customTemplatesDao.findCustTemplateById(mailingList.getWelcomeCustTempId());
			  if(custTemplate != null) {
				  if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						try {
							MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
							 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
							 if(myTemplates != null) message = myTemplates.getContent();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
			  }
		  }
		  
		  message = message.replace("[OrganisationName]", user.getUserOrganization().getOrganizationName())
				  .replace("[senderReplyToEmailID]", user.getEmailId());
		  
		  EmailQueue testEmailQueue = new EmailQueue(mailingList.getWelcomeCustTempId(),Constants.EQ_TYPE_WELCOME_MAIL, message, "Active",
				  				contact.getEmailId(), user, MyCalendar.getNewCalendar(), " Welcome Mail",
				  				null, contact.getFirstName(), null, contact.getContactId());
				
			//testEmailQueue.setChildEmail(childEmail);
			logger.info("testEmailQueue"+testEmailQueue.getChildEmail());
			
			//emailQueueDao.saveOrUpdate(testEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
		logger.info("Exited sendWelcomeEmail method >>>>>");
		
	}//sendWelcomeEmail
	
	private void sendLoyaltyTemplateMail(Long templateId, Users user, String cardNumber,
			String cardPin, Long contactId, String firstName, String emailId){
		
		try{
		
		CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
		EmailQueueDao emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
		EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			 
		CustomTemplates custTemplate = null;
			  String message = PropertyUtil.getPropertyValueFromDB("loyaltyOptinMsgTemplate");
			  
			  if(templateId != null) {
				  
				  custTemplate = customTemplatesDao.findCustTemplateById(templateId);
				  if(custTemplate != null) {
					  if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
						  message = custTemplate.getHtmlText();
						}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
						  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
						  if(myTemplates != null)message = myTemplates.getContent();
						}
				  }
			  }
			  logger.debug("-----------email----------"+emailId);
			  
			  message = message.replace("<OrganisationName>", user.getUserOrganization().getOrganizationName())
					  .replace("[CardNumber]", ""+cardNumber).replace("[CardPin]", cardPin);
			  
			  EmailQueue testEmailQueue = new EmailQueue(templateId,Constants.EQ_TYPE_TEST_LOYALTY_DETAILS_MAIL, message, "Active",
					  				emailId, user, MyCalendar.getNewCalendar(), "Loyalty Card Details.",
					  				null, firstName, null, contactId);
					
				logger.info("testEmailQueue"+testEmailQueue.getChildEmail());
				
				//emailQueueDao.saveOrUpdate(testEmailQueue);
				emailQueueDaoForDML.saveOrUpdate(testEmailQueue);

		}catch(Exception e){
			logger.error("Exception in sending loyalty template email...", e);
		}
	}
	
	public LoyaltyTransaction findTransaction(String trxID){
		LoyaltyTransaction transaction = null;
		LoyaltyTransactionDao loyaltyTransactionDao = null;
		try {
			loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
			transaction = loyaltyTransactionDao.findById(Long.parseLong(trxID));
		}catch(Exception e){
			logger.error("Exception in find transaction by requestid", e);
		}
		return transaction;
	}
}
