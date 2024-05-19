
package org.mq.optculture.business.updatecontacts;

import java.awt.image.RescaleOp;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ApplicationProperties;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.ContactsLoyaltyStage;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyMemberSessionID;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Messages;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.SMSSuppressedContacts;
import org.mq.marketer.campaign.beans.SuppressedContacts;
import org.mq.marketer.campaign.beans.Unsubscribes;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDaoForDML;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDao;
import org.mq.marketer.campaign.dao.LoyaltyTransactionDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.MessagesDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDao;
import org.mq.marketer.campaign.dao.SMSSuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.SuppressedContactsDaoForDML;
import org.mq.marketer.campaign.dao.UnsubscribesDao;
import org.mq.marketer.campaign.dao.UnsubscribesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.business.loyalty.LoyaltyEnrollmentOCService;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.digitalReceipt.DRHead;
import org.mq.optculture.model.digitalReceipt.DRJsonRequest;
import org.mq.optculture.model.digitalReceipt.DRReceipt;
import org.mq.optculture.model.digitalReceipt.DigitalReceiptBody;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollResponse;
import org.mq.optculture.model.ocloyalty.LoyaltyUser;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipRequest;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.RequestHeader;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.updatecontacts.ContactRequest;
import org.mq.optculture.model.updatecontacts.ContactResponse;
import org.mq.optculture.model.updatecontacts.Customer;
import org.mq.optculture.model.updatecontacts.Header;
import org.mq.optculture.model.updatecontacts.Loyalty;
import org.mq.optculture.model.updatecontacts.MobileAppPreferences;
import org.mq.optculture.model.updatecontacts.Status;
import org.mq.optculture.model.updatecontacts.Suppress;
import org.mq.optculture.model.updatecontacts.User;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zkplus.spring.SpringUtil;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
// import com.newrelic.api.agent.Response;

public class UpdateContactsBusinessServiceImpl implements UpdateContactsBusinessService {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private boolean isListSizeIncreased = false;
	private boolean isNewList = false;
	private String dateStringStatus=Constants.STRING_NILL;

	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		ContactResponse contactResponse = null;

		try {
			logger.debug("-------entered processRequest---------");
			// json to object
			Gson gson = new Gson();
			ContactRequest contactRequest = null;
			try {
				contactRequest = gson.fromJson(baseRequestObject.getJsonValue(), ContactRequest.class);
			} catch (JsonSyntaxException e) {
				logger.error("Exception ::", e);
				Status status = new Status("400000",
						PropertyUtil.getErrorMessage(400000, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				contactResponse = prepareFinalResponse(new Header(), status, contactRequest);
				String json = gson.toJson(contactResponse);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.UPDATE_CONTACTS_SERVICE_REQUEST);
				return baseResponseObject;
			}
			UpdateContactsBusinessService updateContactsBusinessService = (UpdateContactsBusinessService) ServiceLocator
					.getInstance().getServiceByName(OCConstants.UPDATE_CONTACTS_BUSINESS_SERVICE);
			contactResponse = (ContactResponse) updateContactsBusinessService
					.processUpdateContactRequest(contactRequest);

			// object to json
			String json = gson.toJson(contactResponse);
			baseResponseObject.setJsonValue(json);
			baseResponseObject.setAction(OCConstants.UPDATE_CONTACTS_SERVICE_REQUEST);
			return baseResponseObject;
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("-------exit  processRequest---------");
		return baseResponseObject;
	}// processRequest

	@Override
	public ContactResponse processUpdateContactRequest(ContactRequest contactRequest) throws BaseServiceException {
		
		
		ContactResponse contactResponse = null;
		User user = null;
		Header header = null;
		Customer customer = null;
		Status status = null;
		String[] ResponseCode = new String[3];// INDEX => 0 = EmailSuppress,1 = PhoneSuppress and 2 = AddContact
		String ContactMessage = "";
		Suppress suppress = contactRequest.getCustomer().getSuppress();
		
		try {
			String FinalMessageResponse = "";
			
			// get user using JSON user details
			customer = contactRequest.getCustomer();
			user = contactRequest.getUser();
			header = contactRequest.getHeader();
			
			String orgId = user.getOrganizationId();
			String token = user.getToken();
			String userName = user.getUserName();

			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users users =null;
			if(token != null && !token.isEmpty()){
				users = usersDao.findByToken(userName + Constants.USER_AND_ORG_SEPARATOR + orgId, token);
				
			}else{
				users = usersDao.findByUsername(userName + Constants.USER_AND_ORG_SEPARATOR + orgId);
			}
			if (users == null) {
				status = new Status("400009", PropertyUtil.getErrorMessage(400009, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return prepareFinalResponse(header, status, contactRequest);
			}

			//APP-2185
			/*
			 * USE-CASE : APP-2267
			 * If Enroll flag is 'Y', go to the different flow.
			 * */
			LoyaltyEnrollResponse loyaltyEnrollResponse = null;
			if(contactRequest.getCustomer() !=null && 
					contactRequest.getCustomer().getLoyalty()!=null&&
							contactRequest.getCustomer().getLoyalty().getEnrollCustomer()!= "\0" &&
							contactRequest.getCustomer().getLoyalty().getEnrollCustomer()!=null  &&
									contactRequest.getCustomer().getLoyalty().getEnrollCustomer().equalsIgnoreCase("Y")) {
				loyaltyEnrollResponse = prepareEnrollFromUpdateContactRequest(contactRequest,users);
				String errorCode = loyaltyEnrollResponse.getStatus().getErrorCode();
				String Message = loyaltyEnrollResponse.getStatus().getMessage();
				int errorCodeInteger = Integer.parseInt(errorCode);
				if(errorCodeInteger == 0) {
					status = new Status("0", Message, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				}
				else {
					status = new Status(errorCode, Message,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				}
				contactResponse = prepareFinalResponse(header, status, contactRequest);
				contactResponse.setMembership(loyaltyEnrollResponse != null ?
					    (loyaltyEnrollResponse.getMembership()!=null ? 
								loyaltyEnrollResponse.getMembership() : null) : null);
				return contactResponse;			
			}
			
			logger.debug("-------entered processUpdateContactRequest---------");
			status = validateRootObject(contactRequest);
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
				contactResponse = prepareFinalResponse(header, status, contactRequest);
				return contactResponse;
			}
			// changes start 2.5.3.0
			
			status = validateCommonObjects(header, user, customer);
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
				contactResponse = prepareFinalResponse(header, status, contactRequest);
				return contactResponse;
			}
			
			
			//validate sessionID for mobileapp
			String sourceType = contactRequest.getHeader().getSourceType();			
			String membership = contactRequest.getCustomer().getMembershipNumber();
			if(sourceType != null && !sourceType.isEmpty() && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) ){
				String sessionID = contactRequest.getUser().getSessionID();
				if(sessionID == null || sessionID.isEmpty()){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					contactResponse = prepareFinalResponse(header, status, contactRequest);
					return contactResponse;
				}
				LoyaltyMemberSessionID loyaltyMemberSessionID = LoyaltyProgramHelper.validateSessionID(sessionID);
				if(loyaltyMemberSessionID == null){
					
					status = new Status("800028", PropertyUtil.getErrorMessage(800028, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					contactResponse = prepareFinalResponse(header, status, contactRequest);
					return contactResponse;
				}
				
				String cardNumber = LoyaltyProgramHelper.getCardFromSesstionID(sessionID);
				if(membership != null && membership.trim().length() > 0 && 
						!membership.trim().equals(cardNumber)){
					status = new Status("800029", PropertyUtil.getErrorMessage(800029, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					contactResponse = prepareFinalResponse(header, status, contactRequest);
					return contactResponse;
					
				}
				
				
			}

			//changes for mobile APP
			Contacts membershipContact = null;
			ContactsLoyalty conMembership=null;
			ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			
			if(membership!=null && !membership.isEmpty()) {
			
				conMembership= contactsLoyaltyDao.findContLoyaltyByCardId(users.getUserId(), membership);
				
				if(conMembership == null){	
					
					status = new Status("400036", PropertyUtil.getErrorMessage(400036, OCConstants.ERROR_CONTACTS_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return prepareFinalResponse(header, status, contactRequest);
				}
				
				//update contact
				if(conMembership.getContact() == null) {
					status = new Status("400037", PropertyUtil.getErrorMessage(400037, OCConstants.ERROR_CONTACTS_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return prepareFinalResponse(header, status, contactRequest);
				} //error
				
				//search directly based on userid , contactid
				membershipContact = contactsDao.findById(conMembership.getContact().getContactId());
				
			}
			
			if(sourceType != null && 
					(sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
							sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)) ) {
				if( conMembership == null){	
					String sessionID = contactRequest.getUser().getSessionID();
					if(membership == null || membership.isEmpty() && (sourceType != null && 
							sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) && 
									sessionID != null && !sessionID.isEmpty() && customer.getLoyalty().getPassword() != null && 
									!customer.getLoyalty().getPassword().isEmpty()) ){
						
						String membershipNumber = LoyaltyProgramHelper.getCardFromSesstionID(sessionID);
						conMembership= contactsLoyaltyDao.findContLoyaltyByCardId(users.getUserId(), membershipNumber);
						
						if(conMembership == null){	
							
							status = new Status("400036", PropertyUtil.getErrorMessage(400036, OCConstants.ERROR_CONTACTS_FLAG),
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							return prepareFinalResponse(header, status, contactRequest);
						}
						
						//update contact
						if(conMembership.getContact() == null) {
							status = new Status("400037", PropertyUtil.getErrorMessage(400037, OCConstants.ERROR_CONTACTS_FLAG),
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							return prepareFinalResponse(header, status, contactRequest);
						} //error
						
						//search directly based on userid , contactid
						membershipContact = contactsDao.findById(conMembership.getContact().getContactId());
						
						
					}
					else{
						
						status = new Status("400036", PropertyUtil.getErrorMessage(400036, OCConstants.ERROR_CONTACTS_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						return prepareFinalResponse(header, status, contactRequest);
					}
				}
				
				Loyalty loyalty = contactRequest.getCustomer().getLoyalty();
				MobileAppPreferences mobileAppPreferences = contactRequest.getCustomer().getLoyalty().getMobileAppPreferences();
				
				//removed under security changes	
				/*if(loyalty != null & loyalty.getPassword() != null && !loyalty.getPassword().isEmpty() && 
						conMembership.getMembershipPwd()!=null && decryptMembrshpPwd(conMembership.getMembershipPwd()).equals(decryptMembrshpPwd(loyalty.getPassword()))) {
					status = new Status("400039", PropertyUtil.getErrorMessage(400039, OCConstants.ERROR_CONTACTS_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return prepareFinalResponse(header, status, contactRequest);
				}*/
				
				if(loyalty !=null && loyalty.getFingerprintValidation()!=null //Changes APP-1972 
						&& !loyalty.getFingerprintValidation().toString().trim().equalsIgnoreCase("false")
						&& !loyalty.getFingerprintValidation().toString().trim().equalsIgnoreCase("true")) {
					status = new Status("400042", PropertyUtil.getErrorMessage(400042, OCConstants.ERROR_CONTACTS_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return prepareFinalResponse(header, status, contactRequest);
					
				}
				
//				
				if(mobileAppPreferences !=null && mobileAppPreferences.getPushNotifications()!=null //Changes 
						&& !mobileAppPreferences.getPushNotifications().toString().trim().equalsIgnoreCase("false")
						&& !mobileAppPreferences.getPushNotifications().toString().trim().equalsIgnoreCase("true")) {
					status = new Status("400041", PropertyUtil.getErrorMessage(400041, OCConstants.ERROR_CONTACTS_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return prepareFinalResponse(header, status, contactRequest);
					
				}
				
				if(loyalty.getPassword() != null && !loyalty.getPassword().isEmpty()){//validate current and new password
					try {
						if(LoyaltyProgramHelper.checkAuthentication(EncryptDecryptLtyMembshpPwd.decryptPwd(loyalty.getPassword()), conMembership.getMembershipPwd())){
							
							
							status = new Status("800030", PropertyUtil.getErrorMessage(800030, OCConstants.ERROR_MOBILEAPP_FLAG),
									OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
							return prepareFinalResponse(header, status, contactRequest);
							
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ", e);
						status = new Status("800030", PropertyUtil.getErrorMessage(800030, OCConstants.ERROR_MOBILEAPP_FLAG),
								OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
						return prepareFinalResponse(header, status, contactRequest);
					}
					
				}
								
			}
						/*--------------------------------DEVIATION*/

			if (header.getContactList() != null && !header.getContactList().isEmpty()) {
				status = validateInnerObjects(header, user, customer);
				if (status == null)
					status = processContactData(users, contactRequest, false,membershipContact,sourceType,conMembership);

				ResponseCode[2] = status.getErrorCode();
				ContactMessage = status.getMessage();
				
				
				if(conMembership!=null){ //Mobile_APP
					Loyalty loyalty = contactRequest.getCustomer().getLoyalty();
					ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
					ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
					
					if(customer.getHomeStore()!=null && !customer.getHomeStore().isEmpty()) conMembership.setPosStoreLocationId(customer.getHomeStore());
					if(customer.getSubsidiaryNumber()!=null && !customer.getSubsidiaryNumber().isEmpty()) conMembership.setSubsidiaryNumber(customer.getSubsidiaryNumber());
					
					contactsLoyaltyDaoForDML.saveOrUpdate(conMembership);
					
					if(loyalty != null 
							&& loyalty.getPassword() != null 
							&& !loyalty.getPassword().isEmpty()){
							
							contactsLoyaltyDaoForDML.updateMembershipPasswordByLoyaltyId(conMembership.getLoyaltyId(),EncryptDecryptLtyMembshpPwd.encrypt(EncryptDecryptLtyMembshpPwd.decryptPwd(loyalty.getPassword())));
						}
						if(loyalty != null && loyalty.getSuspendMembership() != null && 
								!loyalty.getSuspendMembership().isEmpty() && loyalty.getSuspendMembership().equalsIgnoreCase("YES")) {//suspend member n delete the contact
							conMembership.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_SUSPENDED);
							contactsLoyaltyDaoForDML.saveOrUpdate(conMembership);
							
							Contacts contact = contactsDao.findById((conMembership.getContact().getContactId()));
	             			contact.setMlBits(0l);
	             			contact.setModifiedDate(Calendar.getInstance());
	            			if(contact.getMlBits().longValue() == 0l){
	            				Utility.setContactFieldsOnDeletion(contact);
	            			}
	            			
	            			contactsDaoForDML.saveOrUpdate(contact);

						}
						
						if(loyalty.getFingerprintValidation()!=null 
								&& !loyalty.getFingerprintValidation().isEmpty())
							contactsLoyaltyDaoForDML.updateMembershipFingerPrintByLoyaltyId(conMembership.getLoyaltyId(), Boolean.parseBoolean(loyalty.getFingerprintValidation().toLowerCase()));
					
				}
				
				FinalMessageResponse = FinalMessageResponse + (ResponseCode[2].equals("0") ? " " + ContactMessage
						: " " + PropertyUtil.getErrorMessage(Integer.parseInt(ResponseCode[2]),
								OCConstants.ERROR_CONTACTS_FLAG))
						+ "";


			}

			/* EMAIL */
			logger.info("Response Codes ====>" + ResponseCode[2] + " " + ResponseCode[1]);
			
			if (suppress != null) {
				
				if (suppress.getEmail() != null && suppress.getEmail().getIsTrue().toString().equalsIgnoreCase("Y")) {
					ResponseCode[0] = validateEmail(customer.getEmailAddress());
					
					//TODO Unsubscribe
					
					if(ResponseCode[0].equals("0") && 
							suppress.getEmail().getReason()!=null && 
							!suppress.getEmail().getReason().isEmpty() &&
							suppress.getEmail().getReason().equals(Constants.CS_STATUS_UNSUBSCRIBED)) {
						
						
						ResponseCode[0] = addUnsubscribe(contactRequest, users);
					}
					else {
						if (ResponseCode[0].equals("0"))
						ResponseCode[0] = addSuppressedContact(contactRequest, users);
					}
					FinalMessageResponse = FinalMessageResponse
							+ (ResponseCode[0].equals("0") ? " Email suppressed successfully. "
									: " " + PropertyUtil.getErrorMessage(Integer.parseInt(ResponseCode[0]),
											OCConstants.ERROR_CONTACTS_FLAG))
							+ "";

				}
				//APP-1977
				if (suppress.getEmail() != null && suppress.getEmail().getIsTrue().toString().equalsIgnoreCase("N")) {
					String ResponseCodeTemp=validateEmail(customer.getEmailAddress());
					if (ResponseCodeTemp.equals("0"))
					{
								UnsubscribesDao unsubscribesDao = (UnsubscribesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.UNSUBSCRIBES_DAO);
								UnsubscribesDaoForDML unsubscribesDaoForDML = (UnsubscribesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UNSUBSCRIBES_DAO_FOR_DML);
								ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
								
								//Finding whether the contact exist in unsubscribe or not.
								String unsubscribeStatus=unsubscribesDao.isAlreadyUnsubscribedContact(users.getUserId(),customer.getEmailAddress());
								
								if(unsubscribeStatus!=null && unsubscribeStatus.equals(Constants.CONT_STATUS_UNSUBSCRIBED)) {
								//Update the email status to Active.
								contactsDaoForDML.updateEmailStatusByUserId(customer.getEmailAddress(), users.getUserId(), "Active");
			
								//Remove Contact From Unsubscribe
								unsubscribesDaoForDML.deleteByEmailIdUserId(customer.getEmailAddress(), users.getUserId());
								}											
					}
					//No error code needed for success case.
					FinalMessageResponse = FinalMessageResponse
							+ (ResponseCodeTemp.equals("0") || (ResponseCodeTemp.equals("400022")) ? ""
									: " " + PropertyUtil.getErrorMessage(Integer.parseInt(ResponseCodeTemp),
											OCConstants.ERROR_CONTACTS_FLAG))
							+ "";

					
					
				}
			
				/* PHONE */
				if (suppress.getPhone() != null && suppress.getPhone().getIsTrue().toString().equalsIgnoreCase("Y")) {
					ResponseCode[1] = validatePhone(customer.getPhone(), users.getUserOrganization());		
					if (ResponseCode[1].equals("0"))
						ResponseCode[1] = addSuppressedPhoneNumber(contactRequest, users);

					FinalMessageResponse = FinalMessageResponse
							+ (ResponseCode[1].equals("0") ? " Phone number suppressed successfully. "
									: " " + PropertyUtil.getErrorMessage(Integer.parseInt(ResponseCode[1]),
											OCConstants.ERROR_CONTACTS_FLAG));

				}
				//APP-1977
				if (suppress.getPhone() != null && suppress.getPhone().getIsTrue().toString().equalsIgnoreCase("N")) {
					String ResponseCodeTemp=validatePhone(customer.getPhone(),users.getUserOrganization());
					if (ResponseCodeTemp.equals("0")) {
								
						
						
						try {
									ContactsDaoForDML contactsDaoForDML = null;
									SMSSuppressedContactsDao smsSuppressedContactsDao = null;
									SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML = null;
									String mobileNumber=customer.getPhone();
									
									try {
										
										smsSuppressedContactsDao = (SMSSuppressedContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO);
										smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO_FOR_DML);
										contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										throw new BaseServiceException("No dao(s) found in the context with id ");
									}
									
									
									if(smsSuppressedContactsDao.isMobileOptedOut(users.getUserId(),mobileNumber,Constants.SMS_SUPP_TYPE_OPTED_OUT)) {
									
												contactsDaoForDML.updateMobileOptinStatus(mobileNumber, Constants.CON_MOBILE_STATUS_ACTIVE, users );//What to do with source value.Source Denotes "keyword" or "Opt - in" 
												smsSuppressedContactsDaoForDML.deleteFromSuppressedContacts(users, mobileNumber);			
									}
									
									
									
								} catch (Exception e) {
									// TODO Auto-generated catch block
									throw new BaseServiceException("Exception in perform mobile optin");
								}
					}
					//No error code needed for success case.
					FinalMessageResponse = FinalMessageResponse
							+ (ResponseCodeTemp.equals("0") || (ResponseCodeTemp.equals("400025")) ? ""
									: " " + PropertyUtil.getErrorMessage(Integer.parseInt(ResponseCodeTemp),
											OCConstants.ERROR_CONTACTS_FLAG))
							+ " ";
	
				 
				}

			}

			
			// Server error condition
			if (ResponseCode[0] != null && ResponseCode[1] != null
					&& (ResponseCode[0].equals("101000") || ResponseCode[1].equals("101000"))) {
				status = new Status("101000", PropertyUtil.getErrorMessage(101000, OCConstants.ERROR_LOYALTY_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return prepareFinalResponse(header, status, contactRequest);
			}

				logger.info("ResponseCode[0]====>" + ResponseCode[0]);

			// Failure
			if (ResponseCode[0] == null && ResponseCode[1] == null && ResponseCode[2] == null) {
				status = new Status("400028", PropertyUtil.getErrorMessage(400028, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return prepareFinalResponse(header, status, contactRequest);
			}
			
			status = new Status("400030", "Response :" + FinalMessageResponse,
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			// Success
			if ((ResponseCode[0] == null || ResponseCode[0].equals("0"))
					&& (ResponseCode[1] == null || ResponseCode[1].equals("0"))
					&& (ResponseCode[2] == null || ResponseCode[2].equals("0"))
					/*&& (loyaltyEnrollResponse == null || loyaltyEnrollResponse.getStatus().getErrorCode().equals("0"))*/) {
				status.setErrorCode("0");
				status.setStatus(OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			}
			
			logger.info("Final Response R0===>" + ResponseCode[0] + " R1===> " + ResponseCode[1] + " R2===> "
					+ ResponseCode[1] + "R3 ==>" + contactResponse);
			Loyalty loyalty = contactRequest.getCustomer().getLoyalty();
			if(loyalty != null && loyalty.getSuspendMembership() != null && 
					!loyalty.getSuspendMembership().isEmpty() && 
					loyalty.getSuspendMembership().equalsIgnoreCase("YES")) {//suspend member n delete the contact
			
				status = new Status("0", "Contact deleted successfully.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				
			}
			contactResponse = prepareFinalResponse(header, status, contactRequest);
//			contactResponse.setMembership(loyaltyEnrollResponse != null ?
//						    (loyaltyEnrollResponse.getMembership()!=null ? 
//									loyaltyEnrollResponse.getMembership() : null) : null);
			return contactResponse;
		}
		catch(javax.crypto.IllegalBlockSizeException e) {
			logger.info("e===>", e);
			status = new Status("400040", PropertyUtil.getErrorMessage(400040, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return prepareFinalResponse(header, status, contactRequest);
		}
		catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing processUpdateContactRequest::::: ", e);
		}

	}// processUpdateContactRequest

	// Typeform

	@Override
	public ContactResponse processUpdateContactRequest(ContactRequest contactRequest, boolean ignoreMobileValidation)
			throws BaseServiceException {
		ContactResponse contactResponse = null;
		User user = null;
		Header header = null;
		Customer customer = null;
		Status status = null;
		try {
			logger.debug("-------entered processUpdateContactRequest---------");
			status = validateRootObject(contactRequest);
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
				contactResponse = prepareFinalResponse(header, status, contactRequest);
				return contactResponse;
			}

			// get user using json user details
			header = contactRequest.getHeader();
			customer = contactRequest.getCustomer();
			user = contactRequest.getUser();
			// changes start 2.5.3.0

			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			String userName = user.getUserName();
			String orgId = user.getOrganizationId();
			String token = user.getToken();

			Users userObj = usersDao.findByToken(userName + Constants.USER_AND_ORG_SEPARATOR + orgId, token);
			if (userObj == null) {
				status = new Status("400009", PropertyUtil.getErrorMessage(400009, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return prepareFinalResponse(header, status, contactRequest);
			}

			status = validateCommonObjects(header, user, customer);
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
				contactResponse = prepareFinalResponse(header, status, contactRequest);
				return contactResponse;
			}

			// changes end 2.5.3.0

			status = validateInnerObjects(header, user, customer);
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
				contactResponse = prepareFinalResponse(header, status, contactRequest);
				return contactResponse;
			}

			status = processContactData(userObj, contactRequest, ignoreMobileValidation,null,null,null);
			contactResponse = prepareFinalResponse(header, status, contactRequest);
			return contactResponse;

		} catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing processUpdateContactRequest::::: ", e);
		}

	}// processUpdateContactRequest

	private String validateEmail(String email) {
		String Response = "0";
		if (email ==null || email.trim().isEmpty()) {
			Response = "400022";
			return Response;
		} else if (!Utility.validateEmail(email.trim())) {
			Response = "400023";
			return Response;
		}

		return Response;

	}

	private String validatePhone(String phone, UserOrganization userOrganization) {
		String Response = "0";
		if (phone.trim().isEmpty()) {
			Response = "400025";
			return Response;
		} else if ((Utility.phoneParse(phone.trim(), userOrganization) == null)) {
			Response = "400026";
			return Response;
		}
		return Response;

	}

	private Status validateCommonObjects(Header header, User user, Customer customer) throws BaseServiceException {
		Status status = null;
		
		String reason = null;
		String phoneReason = null;
		String emailIsTrue = null;
		String phoneIsTrue = null;
		String emailTimeStamp = null;
		String phoneTimeStamp = null;
		
		
		if(customer.getSuppress() != null) {
			if(customer.getSuppress().getEmail() != null) {
				reason = customer.getSuppress().getEmail().getReason();
				emailTimeStamp = customer.getSuppress().getEmail().getTimestamp();
				emailIsTrue = customer.getSuppress().getEmail().getIsTrue();
			}
			if(customer.getSuppress().getPhone() != null) {
				phoneReason = customer.getSuppress().getPhone().getReason();
				phoneIsTrue = customer.getSuppress().getPhone().getIsTrue();
				phoneTimeStamp = customer.getSuppress().getPhone().getTimestamp();
			}
		}
		
		if (header == null) {
			status = new Status("400003", PropertyUtil.getErrorMessage(400003, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if (header.getRequestId() == null || header.getRequestId().isEmpty()) {
			status = new Status("400004", PropertyUtil.getErrorMessage(400004, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}

		if (header.getRequestDate() == null || header.getRequestDate().isEmpty()) {
			status = new Status("400014", PropertyUtil.getErrorMessage(400014, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}

		if (header.getContactSource() == null || header.getContactSource().isEmpty() ) {
			status = new Status("400015", PropertyUtil.getErrorMessage(400015, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if (header.getContactSource()  != null && !header.getContactSource() .isEmpty() && !header.getContactSource().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE)
				&& !header.getContactSource().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_WEBFORM)
				&& !header.getContactSource().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_E_COMM)
				&& !header.getContactSource().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)
				&& !header.getContactSource().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)) {
			status = new Status("400016", PropertyUtil.getErrorMessage(400016, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if (user == null) {
			status = new Status("400005", PropertyUtil.getErrorMessage(400005, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}

		String userNameStr = user.getUserName();
		if (userNameStr == null || userNameStr.trim().length() == 0) {
			status = new Status("400006", PropertyUtil.getErrorMessage(400006, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		String orgId = user.getOrganizationId();
		if (orgId == null || orgId.trim().length() == 0) {
			status = new Status("400007", PropertyUtil.getErrorMessage(400007, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}

		String tokenStr = user.getToken();
		//no need to have this validation thats y removed
		
		/*if (header.getSourceType() != null && !header.getSourceType().isEmpty() && !header.getSourceType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP) && 
				(tokenStr == null || tokenStr.trim().length() == 0)) {
			status = new Status("400008", PropertyUtil.getErrorMessage(400008, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}*/

		if (customer == null) {
			status = new Status("400010", PropertyUtil.getErrorMessage(400010, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}

		if(customer.getEmailAddress()!=null && validateEmail(customer.getEmailAddress()).equals("400023"))
		{
			status = new Status("400023", PropertyUtil.getErrorMessage(400023, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if(emailIsTrue!=null && emailIsTrue.toString().equalsIgnoreCase("Y") && reason!=null && !reason.isEmpty() && !reason.equals("bouncedcontact") && !reason.equals("useraddedcontact") && !reason.equals("Spammed")&&!reason.equals("Unsubscribed"))
		{
			status = new Status("400031", PropertyUtil.getErrorMessage(400031, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(emailIsTrue!=null && emailIsTrue.toString().equalsIgnoreCase("Y") && reason!=null && reason.isEmpty()){
			status = new Status("400031", PropertyUtil.getErrorMessage(400031, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(phoneIsTrue!=null && phoneIsTrue.toString().equalsIgnoreCase("Y") && phoneReason!=null && phoneReason.isEmpty()){
			status = new Status("400032", PropertyUtil.getErrorMessage(400032, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if(phoneIsTrue!=null && phoneIsTrue.toString().equalsIgnoreCase("Y") && phoneReason!=null && !phoneReason.isEmpty() && !phoneReason.equals("Opted-out") &&  !phoneReason.equals("Invalid Number") && !phoneReason.equals("bouncedcontact") && !phoneReason.equals("User Added Number") )
		{
			status = new Status("400032", PropertyUtil.getErrorMessage(400032, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if (emailIsTrue!=null && emailIsTrue.toString().equalsIgnoreCase("Y") && emailTimeStamp != null && !emailTimeStamp.isEmpty()) {
			String date = emailTimeStamp;
			String regexMMDDYYYY = "((19|20)[0-9]{2})-((0?[1-9])|1[012])-((0?[1-9])|(1[0-9])|(2[0-9])|(3[01]))\\s((0?[0-9])|1[0-9]|2[0-4]):([0-5][0-9]):([0-5][0-9])";
			if (!Pattern.matches(regexMMDDYYYY, date)) {
				status = new Status("400033", PropertyUtil.getErrorMessage(400033, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
		}
		if (phoneIsTrue!=null && phoneIsTrue.toString().equalsIgnoreCase("Y") && phoneTimeStamp != null && !phoneTimeStamp.isEmpty()) {
			String date = phoneTimeStamp;
			String regexMMDDYYYY = "((19|20)[0-9]{2})-((0?[1-9])|1[012])-((0?[1-9])|(1[0-9])|(2[0-9])|(3[01]))\\s((0?[0-9])|1[0-9]|2[0-4]):([0-5][0-9]):([0-5][0-9])";
			if (!Pattern.matches(regexMMDDYYYY, date)) {
				status = new Status("400034", PropertyUtil.getErrorMessage(400034, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
		}
//		String birthday = customer.getBirthday();
//		String anniversary = customer.getAnniversary();
//		
//		//APP-2249
//		if(birthday != null && !birthday.isEmpty()) {
//			
//			try {
//				String dateformat = contactPOSMap.get;
//				DateFormat formatter;
//				Date date;
//				formatter = new SimpleDateFormat(dateformat);
//				date = (Date) formatter.parse(birthday);
//				
//			} catch (Exception e) {
//				status = new Status("400043", PropertyUtil.getErrorMessage(400043, OCConstants.ERROR_CONTACTS_FLAG),
//						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
//				return status;	
//			}	
//		}
//		
//		if(anniversary != null && !anniversary.isEmpty()) {
//			
//			try {
//				String dateformat = "dd/MM/yyyy";
//				DateFormat formatter;
//				Date date;
//				formatter = new SimpleDateFormat(dateformat);
//				date = (Date) formatter.parse(anniversary);
//				
//			} catch (Exception e) {
//				status = new Status("400044", PropertyUtil.getErrorMessage(400044, OCConstants.ERROR_CONTACTS_FLAG),
//						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
//				return status;	
//			}	
//		}
//		
//		//validate sessionID
		if(customer.getAddressLine1()!=null 
				  && !customer.getAddressLine1().isEmpty()
				  && customer.getAddressLine1().length()>200) {
				status = new Status("400047", PropertyUtil.getErrorMessage(400047, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			
			if(customer.getAddressLine2()!=null 
					  && !customer.getAddressLine2().isEmpty()
					  && customer.getAddressLine2().length()>200) {
					status = new Status("400048", PropertyUtil.getErrorMessage(400048, OCConstants.ERROR_CONTACTS_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
		
		
		
		
		return status;

	}

	private Status validateInnerObjects(Header header, User user, Customer customer) throws BaseServiceException {
		Status status = null;
		try {
			logger.debug("-------entered validateInnerObjects---------");


			if (header.getContactList().length() > 50) {
				status = new Status("400018", PropertyUtil.getErrorMessage(400018, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			// this is not mandatory for supp flow
			if (!Utility.validateName(header.getContactList())) {
				status = new Status("400019", PropertyUtil.getErrorMessage(400019, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}

			// not required for supp flow
			if (customer.getCreationDate() == null || customer.getCreationDate().isEmpty()) {
				status = new Status("400002", PropertyUtil.getErrorMessage(400002, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}

			if (customer.getCreationDate() != null && !customer.getCreationDate().isEmpty()) {
				String date = customer.getCreationDate();
				String regexMMDDYYYY = "((19|20)[0-9]{2})-((0?[1-9])|1[012])-((0?[1-9])|(1[0-9])|(2[0-9])|(3[01]))\\s((0?[0-9])|1[0-9]|2[0-4]):([0-5][0-9]):([0-5][0-9])";
				if (!Pattern.matches(regexMMDDYYYY, date)) {
					status = new Status("400020", PropertyUtil.getErrorMessage(400020, OCConstants.ERROR_CONTACTS_FLAG),
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
			}

			logger.debug("-------exit  validateInnerObjects---------");
			return status;
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing validateInnerObjects::::: ", e);
		}
	}// validateInnerObjects

	
	
	public String addUnsubscribe(ContactRequest contactRequest,Users userObj)
	{	String Response = "0";
		try {
		//To find the unique contact
	
		Customer customer = contactRequest.getCustomer();	
		Calendar calTimestamp = null;
		
		String email = customer.getEmailAddress();
		String reason = customer.getSuppress().getEmail().getReason();
		String Timestamp = customer.getSuppress().getEmail().getTimestamp();
		if(Timestamp != null && !Timestamp.isEmpty()){
			calTimestamp = MyCalendar.string2Calendar(Timestamp);
			}
		else{
			calTimestamp=Calendar.getInstance();
		}
			///to change the status to Unsubscribe
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			contactsDaoForDML.updateEmailStatusByUserId(email, userObj.getUserId(), Constants.CS_STATUS_UNSUBSCRIBED);

			//To Add to unsubscribe table
			UnsubscribesDaoForDML unsubscribesDaoForDML = (UnsubscribesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UNSUBSCRIBE_DAO_FOR_DML);			
			Unsubscribes unsubscribes = new Unsubscribes();
			unsubscribes.setEmailId(email);
			unsubscribes.setUserId(userObj.getUserId());
			unsubscribes.setReason(reason);
			unsubscribes.setUnsubcategoriesWeight((short)0);
			unsubscribes.setDate(Calendar.getInstance());
			unsubscribesDaoForDML.saveOrUpdate(unsubscribes);
			
		
	}
		catch (DataIntegrityViolationException dive) {

			Response = "400024";
			logger.info("errrrrrrrrrr......", dive);
		}

		catch (Exception e) {
			logger.error("Exception  ::", e);
			//Response = "101000";// server error
		}
		return Response;

		}

	// changes start 2.5.3.0
	public String addSuppressedContact(ContactRequest contactRequest, Users userObj) throws BaseServiceException {
		logger.info("-------entered  addSuppressedContact---------");
		Calendar calTimestamp = null;
		User user = contactRequest.getUser();
		Header header = contactRequest.getHeader();
		Customer customer = contactRequest.getCustomer();
		Status status = null;
		String email = customer.getEmailAddress();
		String reason = customer.getSuppress().getEmail().getReason();
		String Timestamp = customer.getSuppress().getEmail().getTimestamp();
		if(Timestamp != null && !Timestamp.isEmpty()){
			calTimestamp = MyCalendar.string2Calendar(Timestamp);
			}
		else{
			calTimestamp=Calendar.getInstance();
		}
		String Response = "0";

		try {

			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance()
					.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			SuppressedContactsDaoForDML suppressedContactsDaoForDML = (SuppressedContactsDaoForDML) ServiceLocator
					.getInstance().getDAOForDMLByName(OCConstants.SMS_SUPPRESSED_CONTACTS_DAO_FOR_DML);

			logger.info("SuppressedContact------>>>" + header.getContactSource() + " " + reason);
			SuppressedContacts suppressedContacts = new SuppressedContacts(userObj, email,
					Constants.SUPPTYPE_MAP.get("SUPP_TYPE_BOUNCED"));
			suppressedContacts.setSuppressedtime(calTimestamp);
			suppressedContacts.setSource(header.getContactSource());
			suppressedContacts.setType(reason);//Reason denotes the type -- "not reason in db"
			suppressedContactsDaoForDML.saveOrUpdate(suppressedContacts);
			contactsDaoForDML.updateEmailStatusByStatus("'" + email + "'", userObj.getUserId(),
					Constants.CONT_STATUS_SUPPRESSED, Constants.CONT_STATUS_ACTIVE);
			logger.info("Contact Status changed in contacts table...");
			logger.debug("-------exit  addNewContact---------");

		}

		catch (DataIntegrityViolationException dive) {

			Response = "400024";
			logger.info("errrrrrrrrrr......", dive);
		}

		catch (Exception e) {
			logger.error("Exception  ::", e);
			Response = "101000";// server error
		}
		return Response;

	}

	public String addSuppressedPhoneNumber(ContactRequest contactRequest, Users userObj) throws BaseServiceException {
		Calendar calTimestamp = null;
		User user = contactRequest.getUser();
		Customer customer = contactRequest.getCustomer();
		String phone = customer.getPhone();
		Header header = contactRequest.getHeader();
		Status status = null;
		SMSSuppressedContactsDao smsSuppressedContactsDao = null;
		SMSSuppressedContactsDaoForDML smsSuppressedContactsDaoForDML = null;
		ContactsDaoForDML contactsDaoForDML =null;
		String reason = customer.getSuppress().getPhone().getReason();
		String Timestamp = customer.getSuppress().getPhone().getTimestamp();
		if(Timestamp != null && !Timestamp.isEmpty()){
			calTimestamp = MyCalendar.string2Calendar(Timestamp);
			}
		else{
			calTimestamp=Calendar.getInstance();
		}
		String Response = null;
		try {
			contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance()
					.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			smsSuppressedContactsDao = (SMSSuppressedContactsDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO);
			smsSuppressedContactsDaoForDML = (SMSSuppressedContactsDaoForDML) ServiceLocator.getInstance()
					.getDAOForDMLByName(OCConstants.SMS_SUPPRESSEDCONTACT_DAO_FOR_DML);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.info("error===>"+e1);
		}

		phone = Utility.phoneParse(phone.trim(), userObj.getUserOrganization());
		String countryCarrier = userObj.getCountryCarrier() + Constants.STRING_NILL;
		if (phone.startsWith(countryCarrier) && phone.length() > userObj.getUserOrganization().getMinNumberOfDigits()) {

			phone = phone.substring(countryCarrier.length());

		}
		List<SMSSuppressedContacts> finalList = new ArrayList<SMSSuppressedContacts>();

		try {

			SMSSuppressedContacts smsSuppressedContacts;
			smsSuppressedContacts = new SMSSuppressedContacts(userObj, countryCarrier + phone,
					Constants.SUPP_TYPE_BOUNCED);
			smsSuppressedContacts.setSuppressedtime(calTimestamp);
			smsSuppressedContacts.setType(reason);//Reason denotes the type -- "not reason in db"
			smsSuppressedContacts.setSource(header.getContactSource());
			finalList.add(smsSuppressedContacts);
			contactsDaoForDML.updatemobileStatus (phone,Constants.CONT_STATUS_SUPPRESSED, userObj);

		} catch (Exception e) {
			logger.info("error===>"+e);
		}
		try {
			smsSuppressedContactsDaoForDML.saveByCollection(finalList);
		} catch (DataIntegrityViolationException dive) {
			logger.info("errrrrrrrrrr......", dive);
			Response = "400027";
			return Response;
		}

		logger.info("here---------------");
		logger.debug("-------exit  addNewContact---------");
		Response = "0";
		return Response;

	}

	// changes end 2.5.3.0

	private Status processContactData(Users userObj, ContactRequest contactRequest, boolean ignoreMobileValidation, Contacts loyltyContactsObj,String sourceType, ContactsLoyalty conMembership)
			throws BaseServiceException {
		Status status = null;
		Customer customer = contactRequest.getCustomer();
		Loyalty loyalty = contactRequest.getCustomer().getLoyalty();

		try {
			logger.debug("-------entered processContactData---------");
		
			
			ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.POSMAPPING_DAO);
			List<POSMapping> contactPOSMap = null;
			contactPOSMap = posMappingDao.findByType("'" + Constants.POS_MAPPING_TYPE_CONTACTS + "'",
					userObj.getUserId());
			status = validateContactPOSMap(contactPOSMap, userObj.getUserId());
			
			
			
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
				return status;
			}
			Contacts inputContact = new Contacts();
			Contacts contactObj = null;
			inputContact.setUsers(userObj);

			setContactFields(inputContact, contactPOSMap, contactRequest, ignoreMobileValidation);
			logger.info("dateStringStatus==>"+dateStringStatus);
			if(!dateStringStatus.isEmpty()) {
				status = new Status(dateStringStatus, PropertyUtil.getErrorMessage(Integer.parseInt(dateStringStatus), OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				dateStringStatus=Constants.STRING_NILL;
				return status;
			}
			
			if(sourceType != null && (sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || 
					sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)))
			status = validateInputContact(inputContact);//APP-1586
			
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
				return status;
			}
			// validate emailId and mobile
			TreeMap<String, List<String>> prioMap = null;
			prioMap = Utility.getPriorityMap(userObj.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
			status = validatePriorityMap(prioMap, userObj);
			if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
				return status;
			}
			if ((prioMap != null || prioMap.size() > 0) && loyltyContactsObj == null)  {
				contactObj = contactsDao.findContactByUniqPriority(prioMap, inputContact, userObj.getUserId(), userObj);
			}
			else if(loyltyContactsObj != null) {
				contactObj = loyltyContactsObj; //Have to consider membership as first priority 
			}
			long contactBit = 0l;
			boolean purgeFlag = false;
			MailingList mailingList = createNewMlist(userObj, contactRequest);
			if (mailingList == null && isListSizeIncreased) {
				status = new Status("400021", PropertyUtil.getErrorMessage(400021, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			} 
			
			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			LoyaltyProgram loyaltyProgram =null;
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			if(conMembership==null && contactObj !=null ) {
				conMembership= contactsLoyaltyDao.findByContactId(userObj.getUserId(),contactObj.getContactId());
			}
		if(conMembership !=null) {
			loyaltyProgram = loyaltyProgramDao.findById(conMembership.getProgramId());
			//Unique Mobile Check APP-2292
			if (loyaltyProgram.getUniqueMobileFlag() == 'Y' && contactObj !=null ) {

				ContactsLoyalty contactLoyalty = findMembershpByPhone(contactRequest.getCustomer().getPhone(),
						loyaltyProgram.getProgramId(), userObj.getUserId(), userObj.getCountryCarrier(),
						userObj.getUserOrganization().getMaxNumberOfDigits());
				if (contactLoyalty != null && !contactLoyalty.getContact().getContactId().equals(contactObj.getContactId())) {
					logger.info("1===>"+contactLoyalty.getContact().getContactId());
					logger.info("2===>"+contactObj.getContactId());
					status = new Status("400045",
							PropertyUtil.getErrorMessage(400045, OCConstants.ERROR_CONTACTS_FLAG) + " "
									+ contactLoyalty.getCardNumber() + ".",
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
			}
			if (loyaltyProgram.getUniqueEmailFlag() == 'Y' && contactObj !=null ) {

				ContactsLoyalty contactLoyalty = findMembershpByEmailId(contactRequest.getCustomer().getEmailAddress(),
						loyaltyProgram.getProgramId(), userObj.getUserId(),
						userObj.getUserOrganization().getMaxNumberOfDigits());
				if (contactLoyalty != null && !contactLoyalty.getContact().getContactId().equals(contactObj.getContactId())) {
					logger.info("1===>"+contactLoyalty.getContact().getContactId());
					logger.info("2===>"+contactObj.getContactId());
					status = new Status("400046",
							PropertyUtil.getErrorMessage(400046, OCConstants.ERROR_CONTACTS_FLAG) + " "
									+ contactLoyalty.getCardNumber() + ".",
							OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
					return status;
				}
			}	
		}
			if (contactObj != null) {
				 status = updateExistingContactObject(contactBit, contactObj, inputContact, purgeFlag, userObj,
						 mailingList, contactRequest.getHeader().getContactSource(),
						 contactRequest.getCustomer().getCreationDate(), contactRequest.getCustomer().getHomeStore(),contactRequest);
				
			} else {
					
					status = addNewContact(contactBit, inputContact, purgeFlag, userObj, mailingList,
							contactRequest.getHeader().getContactSource(), contactRequest.getCustomer().getCreationDate(),contactRequest);
			}
				
			logger.debug("-------exit  processContactData---------");
			return status;
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing processContactData::::: ", e);
		}
	}// processContactData

	private MailingList createNewMlist(Users userObj, ContactRequest contactRequest) {
		logger.info("---entered createNewMlist---");
		MailingList mailingList = null;
		try {
			MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.MAILINGLIST_DAO);
			MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML) ServiceLocator.getInstance()
					.getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
			mailingList = mailingListDao.getMailingListByName(userObj.getUserId(),
					contactRequest.getHeader().getContactList());

			if (mailingList == null) {
				isNewList = true;
				mailingList = new MailingList();
				mailingList.setListSize(0L);
				mailingList.setListName(contactRequest.getHeader().getContactList());
				mailingList.setListType(Constants.MAILINGLIST_TYPE_ADDED_MANUALLY);
				mailingList.setCreatedDate(Calendar.getInstance());
				mailingList.setStatus(OCConstants.MAILINGLIST_STATUS_ACTIVE);
				mailingList.setLastModifiedDate(Calendar.getInstance());
				mailingList.setLastStatusChangeDate(Calendar.getInstance());
				mailingList.setUsers(userObj);
				long mlbit = mailingListDao.getNextAvailableMbit(userObj.getUserId());

				if (mlbit == 0l) {
//					logger.error("You have exceeded limit on maximum number of lists(60). " Have to ask
//							+ "Please delete one or more lists to create a new list.", "red");
					isListSizeIncreased = true;
					return null;
				}
				mailingList.setMlBit(mlbit);
				mailingListDaoForDML.saveOrUpdate(mailingList);
			}
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.info("---exit createNewMlist---");
		return mailingList;
	}// createNewMlist()

	private Status addNewContact(long contactBit, Contacts inputContact, boolean purgeFlag, Users user,
			MailingList mailingList, String source, String date,ContactRequest contactRequest) throws BaseServiceException {

		Status status = null;
		try {
			logger.debug("-------entered addNewContact---------");
			EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator
					.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator
					.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance()
					.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			MailingListDaoForDML mailingListDao = (MailingListDaoForDML) ServiceLocator.getInstance()
					.getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
			purgeFlag = true;
			contactBit = mailingList.getMlBit().longValue();

			Calendar createdDate = MyCalendar.string2Calendar(date);
			inputContact.setCreatedDate(createdDate);
			inputContact.setModifiedDate(createdDate);

			inputContact.setPurged(false);
			inputContact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
			inputContact.setUsers(user);
			if (source.toString().equalsIgnoreCase("Store")) {
				source = Constants.CONTACT_OPTIN_MEDIUM_POS;
			} else if (source.toString().equalsIgnoreCase("Webform")) {
				source = Constants.CONTACT_OPTIN_MEDIUM_WEBFORM;
			}
			  else if(source.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)) {
				source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP;
			}
			  else if( source.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)) {
				source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP;
			}		
			else {
				source = Constants.CONTACT_OPTIN_MEDIUM_ECOMMERCE;
			}
			inputContact.setOptinMedium(source);
			// perform mobile status
			if (inputContact.getMobilePhone() != null) {
				performMobileOptIn(inputContact, true, null, user);
			} else {
				inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
			}
			mailingList.setLastModifiedDate(Calendar.getInstance());
			inputContact.setMlBits(contactBit);
			PurgeList purgeList;
			if (purgeFlag) {
				purgeList = (PurgeList) ServiceLocator.getInstance().getBeanByName(OCConstants.PURGE_LIST);
				purgeList.checkForValidDomainByEmailId(inputContact);
			}
			if(contactRequest.getCustomer().getSubsidiaryNumber() !=null && !contactRequest.getCustomer().getSubsidiaryNumber().isEmpty()){
				inputContact.setSubsidiaryNumber(contactRequest.getCustomer().getSubsidiaryNumber());//APP-2327	
			}
			contactsDaoForDML.saveOrUpdate(inputContact);
			mailingList.setListSize(mailingList.getListSize() + 1);
			mailingListDao.saveOrUpdate(mailingList);
			if (mailingList.isCheckWelcomeMsg() && !mailingList.getCheckDoubleOptin()) {
				sendWelcomeEmail(inputContact, mailingList, mailingList.getUsers());
			}
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			eventTriggerEventsObservable.notifyForWebEvents(user.getUserId().longValue(),
					inputContact.getContactId().longValue(), inputContact.getContactId().longValue());
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing addNewContact::::: ", e);
		}
		status = new Status("0", "Contact created successfully", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		logger.debug("-------exit  addNewContact---------");
		return status;
	}// addNewContact

	private Status updateExistingContactObject(long contactBit, Contacts contactObj, Contacts inputContact,
			boolean purgeFlag, Users user, MailingList mailingList, String source, String date, String homeStore,ContactRequest contactRequest)
			throws BaseServiceException {
		Status status = null;
		try {
			logger.debug("-------entered updateExistingContactObject---------");
			EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator
					.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator
					.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			ContactsDao contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance()
					.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			MailingListDao mailingListDao = (MailingListDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.MAILINGLIST_DAO);
			MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML) ServiceLocator.getInstance()
					.getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
			String emailStatus = contactObj.getEmailStatus();
			boolean emailFlag = contactObj.getPurged();
			contactBit = contactObj.getMlBits().longValue();
			boolean isEnableEvent = false;
			boolean isWelcomeEmail = false;
			if ((contactObj.getEmailId() != null && inputContact.getEmailId() != null
					&& !contactObj.getEmailId().equalsIgnoreCase(inputContact.getEmailId()))
					|| (contactObj.getEmailId() == null && inputContact.getEmailId() != null)) {
				emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
				emailFlag = false;
				purgeFlag = true;
			}
			if (contactBit == 0l) {
				if (source.toString().equalsIgnoreCase("Store")) {
					source = Constants.CONTACT_OPTIN_MEDIUM_POS;
				} else if (source.toString().equalsIgnoreCase("Webform")) {
					source = Constants.CONTACT_OPTIN_MEDIUM_WEBFORM;
				}else if(source.toString().equalsIgnoreCase("LoyaltyApp")) {
					source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP;
				}else if(source.toString().equalsIgnoreCase("Mobile_App")) {
					source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP;
				}
				else {
					source = Constants.CONTACT_OPTIN_MEDIUM_ECOMMERCE;
				}
				contactObj.setOptinMedium(source);
				emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
				emailFlag = false;
				purgeFlag = true;
				isEnableEvent = true;
				isWelcomeEmail = true;
			}
			boolean isSet = true;
			// perform mobile status
			if (inputContact.getMobilePhone() != null) {
				if (user.isConsiderSMSSettings()) {
					performMobileOptIn(inputContact, false, contactObj, user);
				} else {
					inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
					isSet = false;
				}
			}

			Calendar createdDate = MyCalendar.string2Calendar(date);
			Calendar dbCreatedDate = contactObj.getCreatedDate();

			if (createdDate.before(dbCreatedDate)) {
				inputContact = mergeContacts(inputContact, contactObj); // Data is not overridden Only null values are
																		// updated
				inputContact.setCreatedDate(createdDate);
				if (source.toString().equalsIgnoreCase("Store")) {
					source = Constants.CONTACT_OPTIN_MEDIUM_POS;
				} else if (source.toString().equalsIgnoreCase("Webform")) {
					source = Constants.CONTACT_OPTIN_MEDIUM_WEBFORM;
				} 
				else if(source.toString().equalsIgnoreCase("LoyaltyApp")) {
					source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP;
				}else if(source.toString().equalsIgnoreCase("Mobile_App")) {
					source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP;
				}
				else {
					source = Constants.CONTACT_OPTIN_MEDIUM_ECOMMERCE;
				}
				inputContact.setOptinMedium(source);
				if (homeStore != null && !homeStore.isEmpty()) {
					inputContact.setHomeStore(homeStore);
				}
			} else {
				inputContact = Utility.mergeContacts(inputContact, contactObj); // Data is overridden
			}
			// }

			if (inputContact.getMobilePhone() == null) {
				inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
			}
			if (!isSet) {
				inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
			}
			inputContact.setEmailStatus(emailStatus);
			inputContact.setPurged(emailFlag);
			contactBit = (contactBit | mailingList.getMlBit().longValue());
			mailingList.setLastModifiedDate(Calendar.getInstance());
			inputContact.setMlBits(contactBit);
			PurgeList purgeList;
			if (purgeFlag) {
				purgeList = (PurgeList) ServiceLocator.getInstance().getBeanByName(OCConstants.PURGE_LIST);
				purgeList.checkForValidDomainByEmailId(inputContact);
			}
			if(contactRequest.getCustomer().getSubsidiaryNumber() !=null && !contactRequest.getCustomer().getSubsidiaryNumber().isEmpty()){
				inputContact.setSubsidiaryNumber(contactRequest.getCustomer().getSubsidiaryNumber());//APP-2327	
			}
			Contacts contactOriginal = contactsDao.findById(contactObj.getContactId());
			if(Utility.isModifiedContact(inputContact,contactOriginal ))
			{
				logger.info("entered Modified date");
				inputContact.setModifiedDate(Calendar.getInstance());
			}	
			
			if(source!=null && !source.isEmpty()) {
				if (source.toString().equalsIgnoreCase("Store")) {
					source = Constants.CONTACT_OPTIN_MEDIUM_POS;
				} else if (source.toString().equalsIgnoreCase("Webform")) {
					source = Constants.CONTACT_OPTIN_MEDIUM_WEBFORM;
				}
				else if(source.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)) {
					source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP;
				}
				else if( source.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)) {
					source = OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP;
				}		
				else {
					source = Constants.CONTACT_OPTIN_MEDIUM_ECOMMERCE;
				}

				
			}
			contactsDaoForDML.saveOrUpdate(inputContact);
			
			if(source != null && 
					(source.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) || source.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP))){ //Mobile_APP	
					MobileAppPreferences mobileAppPreferences = contactRequest.getCustomer().getLoyalty().getMobileAppPreferences();
					Customer customer = contactRequest.getCustomer();
					if(mobileAppPreferences!=null){
						if(mobileAppPreferences.getLanguage()!=null 
							&& !mobileAppPreferences.getLanguage().isEmpty())
							inputContact.setLanguage(mobileAppPreferences.getLanguage());
					
						if(mobileAppPreferences.getPushNotifications()!=null 
							&& !mobileAppPreferences.getPushNotifications().isEmpty())
							inputContact.setPushNotification(Boolean.parseBoolean(mobileAppPreferences.getPushNotifications().toLowerCase()));
					}
					if(customer.getInstanceId() !=null && !customer.getInstanceId().isEmpty()){
						inputContact.setInstanceId(customer.getInstanceId());//APP-1851	
					}
					if(customer.getDeviceType() !=null && !customer.getDeviceType().isEmpty()){
						inputContact.setDeviceType(customer.getDeviceType());//APP-2671	
					}
				//	Contacts contactOriginal = contactsDao.findById(contactObj.getContactId());
					if(Utility.isModifiedContact(inputContact,contactOriginal ))
					{
						logger.info("entered Modified date");
						inputContact.setModifiedDate(Calendar.getInstance());
					}	
					contactsDaoForDML.saveOrUpdate(inputContact);
				
			}
			
			LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(inputContact, inputContact.getMobilePhone());
			LoyaltyProgramHelper.updateLoyaltyEmailId(inputContact, inputContact.getEmailId());
			if (isNewList) {
				mailingList.setListSize(mailingList.getListSize() + 1);
			}
			mailingListDaoForDML.saveOrUpdate(mailingList);
			// send welcome email
			if (isWelcomeEmail) {
				if (mailingList.isCheckWelcomeMsg() && !mailingList.getCheckDoubleOptin()) {
					sendWelcomeEmail(contactObj, mailingList, mailingList.getUsers());
				}
			}
			// event trigger for contact
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			if (isEnableEvent) {
				eventTriggerEventsObservable.notifyForWebEvents(user.getUserId().longValue(),
						inputContact.getContactId().longValue(), inputContact.getContactId().longValue());
			} // if
			
			
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing updateContactObject::::: ", e);
		}

		status = new Status("0", "Contact updated successfully.", OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		logger.debug("-------exit  updateContactObject---------");
		return status;
	}// updateContactObject

	private Contacts mergeContacts(Contacts inputContact, Contacts dbContact) {

		try {
			if (dbContact == null) {
				return inputContact;
			}

			if ((dbContact.getEmailId() == null) || (dbContact.getEmailId().trim().isEmpty())) {
				dbContact.setEmailId(inputContact.getEmailId());
			}
			if ((dbContact.getFirstName() == null) || (dbContact.getFirstName().trim().isEmpty())) {
				dbContact.setFirstName(inputContact.getFirstName());
			}
			if ((dbContact.getLastName() == null) || (dbContact.getLastName().trim().isEmpty())) {
				dbContact.setLastName(inputContact.getLastName());
			}
			if ((dbContact.getAddressOne() == null) || (dbContact.getAddressOne().trim().isEmpty())) {
				dbContact.setAddressOne(inputContact.getAddressOne());
			}
			if ((dbContact.getAddressTwo() == null) || (dbContact.getAddressTwo().trim().isEmpty())) {
				dbContact.setAddressTwo(inputContact.getAddressTwo());
			}
			if ((dbContact.getCity() == null) || (dbContact.getCity().trim().isEmpty())) {
				dbContact.setCity(inputContact.getCity());
			}
			if ((dbContact.getState() == null) || (dbContact.getState().trim().isEmpty())) {
				dbContact.setState(inputContact.getState());
			}
			if ((dbContact.getCountry() == null) || (dbContact.getCountry().isEmpty())) {
				dbContact.setCountry(inputContact.getCountry());
			}
			if ((dbContact.getUdf1() == null) || (dbContact.getUdf1().trim().isEmpty())) {
				dbContact.setUdf1(inputContact.getUdf1());
			}
			if ((dbContact.getUdf2() == null) || (dbContact.getUdf2().trim().isEmpty())) {
				dbContact.setUdf2(inputContact.getUdf2());
			}
			if ((dbContact.getUdf3() == null) || (dbContact.getUdf3().trim().isEmpty())) {
				dbContact.setUdf3(inputContact.getUdf3());
			}
			if ((dbContact.getUdf4() == null) || (dbContact.getUdf4().trim().isEmpty())) {
				dbContact.setUdf4(inputContact.getUdf4());
			}
			if ((dbContact.getUdf5() == null) || (dbContact.getUdf5().trim().isEmpty())) {
				dbContact.setUdf5(inputContact.getUdf5());
			}
			if ((dbContact.getUdf6() == null) || (dbContact.getUdf6().trim().isEmpty())) {
				dbContact.setUdf6(inputContact.getUdf6());
			}
			if ((dbContact.getUdf7() == null) || (dbContact.getUdf7().trim().isEmpty())) {
				dbContact.setUdf7(inputContact.getUdf7());
			}
			if ((dbContact.getUdf8() == null) || (dbContact.getUdf8().trim().isEmpty())) {
				dbContact.setUdf8(inputContact.getUdf8());
			}
			if ((dbContact.getUdf9() == null) || (dbContact.getUdf9().trim().isEmpty())) {
				dbContact.setUdf9(inputContact.getUdf9());
			}
			if ((dbContact.getUdf10() == null) || (dbContact.getUdf10().trim().isEmpty())) {
				dbContact.setUdf10(inputContact.getUdf10());
			}
			if ((dbContact.getUdf11() == null) || (dbContact.getUdf11().trim().isEmpty())) {
				dbContact.setUdf11(inputContact.getUdf11());
			}
			if ((dbContact.getUdf12() == null) || (dbContact.getUdf12().trim().isEmpty())) {
				dbContact.setUdf12(inputContact.getUdf12());
			}
			if ((dbContact.getUdf13() == null) || (dbContact.getUdf13().trim().isEmpty())) {
				dbContact.setUdf13(inputContact.getUdf13());
			}
			if ((dbContact.getUdf14() == null) || (dbContact.getUdf14().trim().isEmpty())) {
				dbContact.setUdf14(inputContact.getUdf14());
			}
			if ((dbContact.getUdf15() == null) || (dbContact.getUdf15().trim().isEmpty())) {
				dbContact.setUdf15(inputContact.getUdf15());
			}
			if ((dbContact.getGender() == null) || (dbContact.getGender().trim().isEmpty())) {
				dbContact.setGender(inputContact.getGender());
			}
			if ((dbContact.getBirthDay() == null)) {
				dbContact.setBirthDay(inputContact.getBirthDay());
			}
			if ((dbContact.getAnniversary() == null)) {
				dbContact.setAnniversary(inputContact.getAnniversary());
			}
			if ((dbContact.getZip() == null) || (dbContact.getZip().trim().isEmpty())) {
				dbContact.setZip(inputContact.getZip());
			}
			if ((dbContact.getMobilePhone() == null) || (dbContact.getMobilePhone().trim().isEmpty())) {
				dbContact.setMobilePhone(inputContact.getMobilePhone());
			}
			if ((dbContact.getExternalId() == null) || (dbContact.getExternalId().trim().isEmpty())) {
				dbContact.setExternalId(inputContact.getExternalId());
			}
			if ((dbContact.getHomeStore() == null) || (dbContact.getHomeStore().trim().isEmpty())) {
				dbContact.setHomeStore(inputContact.getHomeStore());
			}
			if ((dbContact.getHomePhone() == null) || (dbContact.getHomePhone().trim().isEmpty())) {
				dbContact.setHomePhone(inputContact.getHomePhone());
			}
			
			return dbContact;
		} catch (Exception e) {
			logger.error("Exception ::", e);
			return dbContact;
		}
	}

	private void sendWelcomeEmail(Contacts contactObj, MailingList mailingList, Users user)
			throws BaseServiceException {
		// to send the loyalty related email
		try {
			logger.debug("-------entered sendWelcomeEmail---------");
			CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance()
					.getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
			CustomTemplates custTemplate = null;
			String message = PropertyUtil.getPropertyValueFromDB("welcomeMsgTemplate");

			if (mailingList.getWelcomeCustTempId() != null) {

				custTemplate = customTemplatesDao.findCustTemplateById(mailingList.getWelcomeCustTempId());
				if(custTemplate != null) {
				     if(custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
					}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
					}
			  }
			}
			message = message.replace("[OrganisationName]", user.getUserOrganization().getOrganizationName())
					.replace("[senderReplyToEmailID]", user.getEmailId());

			EmailQueue testEmailQueue = new EmailQueue(mailingList.getWelcomeCustTempId(), Constants.EQ_TYPE_WELCOME_MAIL, message, "Active",
					contactObj.getEmailId(), contactObj.getUsers(), Calendar.getInstance(), "Welcome Mail", null,
					contactObj.getFirstName(),
					MyCalendar.calendarToString(contactObj.getBirthDay(), MyCalendar.FORMAT_DATEONLY),
					contactObj.getContactId());

			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
			logger.debug("-------exit  sendWelcomeEmail---------");
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing sendWelcomeEmail::::: ", e);
		}
	}// sendWelcomeEmail

	private Status validatePriorityMap(TreeMap<String, List<String>> prioMap, Users user) throws BaseServiceException {
		Status status = null;
		logger.debug("-------entered validatePriorityMap---------");
		if (prioMap == null || prioMap.size() == 0) {
			logger.info("Unique Priority Map NOT configured to the user: " + user.getUserName());
			status = new Status("400013", PropertyUtil.getErrorMessage(400013, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.debug("-------exit  validatePriorityMap---------");
		return status;
	}

	private Status validateInputContact(Contacts inputContact) throws BaseServiceException {
		Status status = null;
		logger.debug("-------entered validateInputContact---------");
		if ((inputContact.getExternalId() == null || inputContact.getExternalId().trim().isEmpty())
				&& (inputContact.getEmailId() == null || inputContact.getEmailId().trim().isEmpty())
				&& (inputContact.getMobilePhone() == null || inputContact.getMobilePhone().trim().isEmpty())) {
			status = new Status("400012", PropertyUtil.getErrorMessage(400012, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		
			return status;
		}
		logger.debug("-------exit  validateInputContact---------");
		return status;
	}// validateInputContact

	private void setContactFields(Contacts inputContact, List<POSMapping> contactPOSMap, ContactRequest contactRequest,
			boolean ignoreMobileValidation) throws BaseServiceException {
		Class strArg[] = new Class[] { String.class };
		Class calArg[] = new Class[] { Calendar.class };

		logger.debug("-------entered setContactFields---------");
		for (POSMapping posMapping : contactPOSMap) {

			String custFieldAttribute = posMapping.getCustomFieldName();
			String fieldValue = getFieldValue(contactRequest, posMapping);
			if (posMapping.getDataType().equalsIgnoreCase("Number"))
				fieldValue = Utility.validateNumberValue(fieldValue);
			if (posMapping.getDataType().equalsIgnoreCase("Double"))
				fieldValue = Utility.validateDoubleValue(fieldValue);
			if (fieldValue == null || fieldValue.trim().length() <= 0) {
				logger.info("custom field value is empty ...for field : " + custFieldAttribute);
				continue;
			}
			fieldValue = fieldValue.trim();
			String dateTypeStr = null;
			dateTypeStr = posMapping.getDataType();
			if (dateTypeStr == null || dateTypeStr.trim().length() <= 0) {
				continue;
			}
			if (custFieldAttribute.startsWith("UDF") && dateTypeStr.startsWith("Date")) {
				String dateValue = getDateFormattedData(posMapping, fieldValue);
				if (dateValue == null)
					continue;
			}
			Users user = inputContact.getUsers();
			Object[] params = null;
			Method method = null;
			try {

				if (custFieldAttribute.equals(POSFieldsEnum.emailId.getOcAttr()) && fieldValue.length() > 0
						&& Utility.validateEmail(fieldValue)) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.emailId.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.firstName.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.firstName.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.lastName.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.lastName.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.addressOne.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.addressOne.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.addressTwo.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.addressTwo.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.city.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.city.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.state.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.state.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.country.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.country.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.zip.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.zip.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.mobilePhone.getOcAttr())
						&& fieldValue.length() > 0) {

					String phoneParse = fieldValue;
					if (!ignoreMobileValidation) {
						phoneParse = Utility.phoneParse(fieldValue, user != null ? user.getUserOrganization() : null);
					}
					if (phoneParse != null) {
						logger.info("after phone parse: "+phoneParse);
						phoneParse=LoyaltyProgramHelper.preparePhoneNumber(user,phoneParse);//APP-4863
						logger.info("after prepare phone number: "+phoneParse);
						method = Contacts.class.getMethod("set" + POSFieldsEnum.mobilePhone.getPojoField(), strArg);
						params = new Object[] { phoneParse };
					}
				}

				else if (custFieldAttribute.equals(POSFieldsEnum.externalId.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.externalId.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}

				else if (custFieldAttribute.equals(POSFieldsEnum.gender.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.gender.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}

				else if (custFieldAttribute.equals(POSFieldsEnum.birthDay.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.birthDay.getPojoField(), calArg);
					try {
						String dateformat = dateTypeStr.substring(dateTypeStr.indexOf("(") + 1,
								dateTypeStr.indexOf(")"));
						DateFormat formatter;
						Date date;
						formatter = new SimpleDateFormat(dateformat);
						date = (Date) formatter.parse(fieldValue);
						Calendar dobCal = new MyCalendar(Calendar.getInstance(), null,
								MyCalendar.dateFormatMap.get(dateformat));
						dobCal.setTime(date);
						params = new Object[] { dobCal };
					} catch (Exception e) {
						dateStringStatus="400043";
						logger.info("BirthDay date format not matched with data", e);
					}

				}

				else if (custFieldAttribute.equals(POSFieldsEnum.anniversary.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.anniversary.getPojoField(), calArg);
					try {
						String dateformat = dateTypeStr.substring(dateTypeStr.indexOf("(") + 1,
								dateTypeStr.indexOf(")"));
						logger.info("date format==>"+dateformat);
						DateFormat formatter;
						Date date;
						formatter = new SimpleDateFormat(dateformat);
						date = (Date) formatter.parse(fieldValue);
						Calendar dobCal = new MyCalendar(Calendar.getInstance(), null,
								MyCalendar.dateFormatMap.get(dateformat));
						dobCal.setTime(date);
						params = new Object[] { dobCal };
					} catch (Exception e) {
						dateStringStatus="400044";
						logger.info("Anniversary date format not matched with data", e);
						
					}
				} else if (custFieldAttribute.equals(POSFieldsEnum.homeStore.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.homeStore.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.subsidiaryNumber.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.subsidiaryNumber.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf1.getOcAttr()) && fieldValue.length() > 0) {

					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf1.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf2.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf2.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf3.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf3.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf4.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf4.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf5.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf5.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf6.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf6.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf7.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf7.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf8.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf8.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf9.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf9.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf10.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf10.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf11.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf11.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf12.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf12.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf13.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf13.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf14.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf14.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				} else if (custFieldAttribute.equals(POSFieldsEnum.udf15.getOcAttr()) && fieldValue.length() > 0) {
					method = Contacts.class.getMethod("set" + POSFieldsEnum.udf15.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				if (method != null) {
					try {
						method.invoke(inputContact, params);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::", e);
					}
				}

			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
			logger.debug("-------exit  setContactFields---------");
		}
	}// setContactFields

	private String getDateFormattedData(POSMapping posMapping, String fieldValue) throws BaseServiceException {
		String dataTypeStr = posMapping.getDataType();
		String dateFieldValue = null;
		logger.debug("-------entered getDateFormattedData---------");
		if (posMapping.getDataType().trim().startsWith("Date")) {
			try {
				String dateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(") + 1, dataTypeStr.indexOf(")"));
				if (!Utility.validateDate(fieldValue, dateFormat)) {
					return null;
				}
				DateFormat formatter;
				Date date;
				formatter = new SimpleDateFormat(dateFormat);
				date = (Date) formatter.parse(fieldValue);
				Calendar cal = new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
				cal.setTime(date);
				dateFieldValue = MyCalendar.calendarToString(cal, MyCalendar.dateFormatMap.get(dateFormat));
			} catch (Exception e) {
				logger.error("Exception  ::", e);
				throw new BaseServiceException("Exception occured while processing getDateFormattedData::::: ", e);
			}
		}
		logger.debug("-------exit  getDateFormattedData---------");
		return dateFieldValue;
	}// getDateFormattedData

	private String getFieldValue(ContactRequest contactRequest, POSMapping posMapping) throws BaseServiceException {
		String fieldValue = null;
		logger.debug("-------entered getFieldValue---------");
		if (posMapping.getCustomFieldName().equalsIgnoreCase("street")) {
			fieldValue = contactRequest.getCustomer().getAddressLine1();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("address two")) {
			fieldValue = contactRequest.getCustomer().getAddressLine2();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("email")) {
			fieldValue = contactRequest.getCustomer().getEmailAddress();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("mobile")) {
			fieldValue = contactRequest.getCustomer().getPhone();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("first name")) {
			fieldValue = contactRequest.getCustomer().getFirstName();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("last name")) {
			fieldValue = contactRequest.getCustomer().getLastName();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("city")) {
			fieldValue = contactRequest.getCustomer().getCity();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("state")) {
			fieldValue = contactRequest.getCustomer().getState();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("country")) {
			fieldValue = contactRequest.getCustomer().getCountry();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("zip")) {
			fieldValue = contactRequest.getCustomer().getPostal();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("home store")) {
			fieldValue = contactRequest.getCustomer().getHomeStore();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("Subsidiary Number")) {
			fieldValue = contactRequest.getCustomer().getSubsidiaryNumber();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("gender")) {
			fieldValue = contactRequest.getCustomer().getGender();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("customer id")) {
			fieldValue = contactRequest.getCustomer().getCustomerId();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("created date")) {
			fieldValue = contactRequest.getCustomer().getCreationDate();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("birthday")) {
			fieldValue = contactRequest.getCustomer().getBirthday();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("anniversary")) {
			fieldValue = contactRequest.getCustomer().getAnniversary();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf1")) {
			fieldValue = contactRequest.getCustomer().getUDF1();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf2")) {
			fieldValue = contactRequest.getCustomer().getUDF2();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf3")) {
			fieldValue = contactRequest.getCustomer().getUDF3();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf4")) {
			fieldValue = contactRequest.getCustomer().getUDF4();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf5")) {
			fieldValue = contactRequest.getCustomer().getUDF5();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf6")) {
			fieldValue = contactRequest.getCustomer().getUDF6();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf7")) {
			fieldValue = contactRequest.getCustomer().getUDF7();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf8")) {
			fieldValue = contactRequest.getCustomer().getUDF8();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf9")) {
			fieldValue = contactRequest.getCustomer().getUDF9();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf10")) {
			fieldValue = contactRequest.getCustomer().getUDF10();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf11")) {
			fieldValue = contactRequest.getCustomer().getUDF11();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf12")) {
			fieldValue = contactRequest.getCustomer().getUDF12();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf13")) {
			fieldValue = contactRequest.getCustomer().getUDF13();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf14")) {
			fieldValue = contactRequest.getCustomer().getUDF14();
			return fieldValue;
		}
		if (posMapping.getCustomFieldName().equalsIgnoreCase("udf15")) {
			fieldValue = contactRequest.getCustomer().getUDF15();
			return fieldValue;
		}
		logger.debug("-------exit  getFieldValue---------");
		return fieldValue;
	}// getFieldValue

	private Status validateContactPOSMap(List<POSMapping> contactPOSMap, Long userId) throws BaseServiceException {
		Status status = null;
		logger.debug("-------entered validateContactPOSMap---------");
		if (contactPOSMap == null || contactPOSMap.size() == 0) {
			logger.debug("POS Mapping type CONTACTS not exists for the user: " + userId);
			status = new Status("400011", PropertyUtil.getErrorMessage(400011, OCConstants.ERROR_CONTACTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		logger.debug("-------exit  validateContactPOSMap---------");
		return status;
	}// validateContactPOSMap

	private ContactResponse prepareFinalResponse(Header header, Status status, ContactRequest contactRequest)
			throws BaseServiceException {
		logger.debug("-------entered prepareFinalResponse---------");
		ContactResponse contactResponse = new ContactResponse();
		header = new Header();
		if (contactRequest != null && contactRequest.getHeader() != null) {
			header.setRequestId(contactRequest.getHeader().getRequestId());
			header.setRequestDate(contactRequest.getHeader().getRequestDate());
			header.setContactSource(contactRequest.getHeader().getContactSource());
		}
		contactResponse.setHeader(header);
		contactResponse.setStatus(status);
		logger.debug("-------exit  prepareFinalResponse---------");
		return contactResponse;
	}// prepareFinalResponse

	private Status validateRootObject(ContactRequest contactRequest) throws BaseServiceException {
		Status status = null;
		try {
			logger.debug("-------entered validateRootObject---------");

			if (contactRequest == null) {
				status = new Status("400001", PropertyUtil.getErrorMessage(400001, OCConstants.ERROR_CONTACTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing validateRootObject::::: ", e);
		}
		logger.debug("-------exit  validateRootObject---------");
		return status;
	}// validateRootObject

	private void performMobileOptIn(Contacts inputContact, boolean isNew, Contacts contactObj, Users user)
			throws BaseServiceException {

		try {
			logger.debug("-------entered performMobileOptIn---------");
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML) ServiceLocator.getInstance()
					.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings smsSettings = null;
			OCSMSGateway ocsmsGateway = null;
			if (user.isEnableSMS() && user.isConsiderSMSSettings()) {

				if (SMSStatusCodes.smsProgramlookupOverUserMap.get(user.getCountryType()))
					smsSettings = smsSettingsDao.findByUser(user.getUserId(),
							OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				else
					smsSettings = smsSettingsDao.findByOrg(user.getUserOrganization().getUserOrgId(),
							OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				try {
					ocsmsGateway = GatewayRequestProcessHelper.getOcSMSGateway(user,
							SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(user.getCountryType()));
				} catch (BaseServiceException e) {
					// TODO Auto-generated catch block
					logger.error(e);
				}

			}

			if (smsSettings == null || ocsmsGateway == null) {
				String noSMSComplaincyMsg = ". No SMS Settings find for your user Account,"
						+ "SMS may not be sent to the mobile contacts.";
				Messages messages = new Messages("Contact", "Mobile contacts may not reachable", noSMSComplaincyMsg,
						Calendar.getInstance(), "Inbox", false, "Info", inputContact.getUsers());
				MessagesDao messagesDao = (MessagesDao) ServiceLocator.getInstance()
						.getDAOByName(OCConstants.MESSAGES_DAO);
				MessagesDaoForDML messagesDaoForDML = (MessagesDaoForDML) ServiceLocator.getInstance()
						.getDAOForDMLByName(OCConstants.MESSAGES_DAO_FOR_DML);

				messagesDaoForDML.saveOrUpdate(messages);
				inputContact.setMobileOptin(false);
				inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);// TODO need to finalize

				if (contactObj != null) {
					contactObj.setMobileOptin(false);
					contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);// TODO need to finalize
				}
				return;
			}

			String optinMedium = null;
			if (inputContact.getMobilePhone() != null && !inputContact.getMobilePhone().isEmpty()) {
				// if
				boolean isDifferentMobile = false;
				String mobile = inputContact.getMobilePhone();
				if (contactObj != null) {
					String conMobile = contactObj.getMobilePhone();
					optinMedium = contactObj.getOptinMedium();
					// to identify whether entered one is same as previous mobile
					if (conMobile != null) {
						if (!mobile.equals(conMobile)) {
							if ((mobile.length() < conMobile.length() && !conMobile.endsWith(mobile))
									|| (conMobile.length() < mobile.length() && !mobile.endsWith(conMobile))
									|| mobile.length() == conMobile.length()) {
								isDifferentMobile = true;
							} // if
						} // if
					} // if

					else {
						contactObj.setMobilePhone(inputContact.getMobilePhone());
						isDifferentMobile = true;
					}

				} // if
				else {
					optinMedium = inputContact.getOptinMedium();
				}
				Users currentUser = smsSettings.getUserId();

				boolean canProceed = false;
				// do only when the existing phone number is not same with the entered
				byte optin = 0;
				if (optinMedium != null) {

					if (optinMedium.equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY)) {
						optin = 1;
					} else if (optinMedium.startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM)) {
						optin = 2;
					} else if (optinMedium.equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_POS)) {
						optin = 4;
					}

				} // if

				Users contactOwner = inputContact.getUsers();
				Byte userOptinVal = smsSettings.getOptInMedium();

				userOptinVal = (SMSStatusCodes.userOptinMediumMap.get(contactOwner.getCountryType())
						&& contactOwner.getOptInMedium() != null) ? contactOwner.getOptInMedium() : userOptinVal;

				if (smsSettings.isEnable() && userOptinVal != null && (userOptinVal.byteValue() & optin) > 0) {

					if ((contactObj != null
							&& (contactObj.getLastSMSDate() == null && contactObj.isMobileOptin() != true)
							|| (contactObj != null && isDifferentMobile))) {
						contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_OPTIN_PENDING);
						contactObj.setLastSMSDate(Calendar.getInstance());
						contactObj.setMobileOptin(false);
						canProceed = true;
					}
					if (canProceed || isNew) {
						inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_OPTIN_PENDING);
						inputContact.setMobileOptin(false);
						CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway) ServiceLocator
								.getInstance().getBeanByName(OCConstants.CAPTIWAY_TO_SMS_API_GATEWAY);

						if (!ocsmsGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocsmsGateway, 1)) {
							logger.debug("low credits with clickatell");
							return;
						}

						if (((currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount())
								- (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount())) >= 1) {

							UsersDaoForDML usersDaoForDML = (UsersDaoForDML) ServiceLocator.getInstance()
									.getDAOForDMLByName(OCConstants.USERS_DAOForDML);

							String msgContent = smsSettings.getAutoResponse();
							if (msgContent != null) {

								msgContent = smsSettings.getMessageHeader() + " " + msgContent;
							}

							String mobileStatus = captiwayToSMSApiGateway.sendSingleMobileDoubleOptin(ocsmsGateway,
									smsSettings.getSenderId(), mobile, msgContent, smsSettings.getUserId());

							if (mobileStatus == null) {
								mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
							}
							if (!mobileStatus.equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)) {

								contactsDaoForDML.updatemobileStatus(mobile, mobileStatus, currentUser);

							}
							if (canProceed) {
								contactObj.setMobileStatus(mobileStatus);
							}
							if (isNew) {
								inputContact.setMobileStatus(mobileStatus);
							}

							usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), 1);

							/**
							 * Update Sms Queue
							 */
							SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
							smsQueueHelper.updateSMSQueue(mobile, msgContent, Constants.SMS_MSG_TYPE_OPTIN, user,
									smsSettings.getSenderId());

						} else {
							logger.debug("low credits with user...");

							return;

						}

					} // if
				} // if
				else {
					if (contactObj != null) {
						if (contactObj.getMobilePhone() != null && isDifferentMobile) {
							contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
							contactObj.setMobileOptin(false);
						}
					} // if existing contact
					else {
						if (inputContact.getMobilePhone() != null) {
							inputContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
							inputContact.setMobileOptin(false);
						}
					} // if is new contact
				} // else
			}
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing sendWelcomeEmail::::: ", e);
		}
		logger.debug("-------exit  performMobileOptIn---------");
	}// performMobileOptIn

	/*private String decryptMembrshpPwd(String memPwd) throws Exception {  
		String encPwd = "";
			encPwd = EncryptDecryptLtyMembshpPwd.decrypt(memPwd);
		return encPwd;
		}*/

	public LoyaltyEnrollResponse prepareEnrollFromUpdateContactRequest(ContactRequest contactRequest,Users user){
	logger.info("started enrol from UpdateContacts");
	boolean mobileBasedEnroll=false;
	
	ServiceLocator context = ServiceLocator.getInstance();
	POSMappingDao posMappingDao;
	
	Header header=(contactRequest.getHeader()!=null ? contactRequest.getHeader() : null);
	Customer customer=(contactRequest.getCustomer()!=null ? contactRequest.getCustomer() : null);
	Loyalty loyalty =customer!=null?((customer.getLoyalty()!=null ? customer.getLoyalty() : null)):null;
	User users=(contactRequest.getUser()!=null ? contactRequest.getUser() : null);
	LoyaltyEnrollResponse responseObject = null;
	
	LoyaltyEnrollRequest loyaltyEnrollRequest= new LoyaltyEnrollRequest();
	RequestHeader requestHeader =new RequestHeader();
	MembershipRequest membershipRequest = new MembershipRequest();
	org.mq.optculture.model.ocloyalty.Customer customerLoyalty = new org.mq.optculture.model.ocloyalty.Customer();
	LoyaltyUser loyaltyUser = new LoyaltyUser();
	String sourceType = contactRequest.getHeader().getContactSource();
	String requestId = header.getRequestId()+"_"+System.currentTimeMillis();
	requestHeader.setRequestId(requestId);
	requestHeader.setSourceType(sourceType !=null ? sourceType: Constants.STRING_NILL);
	ResponseHeader responseHeader = new ResponseHeader();
	responseHeader.setRequestDate(Constants.STRING_NILL);
	responseHeader.setRequestId(Constants.STRING_NILL);
	
	ContactsLoyaltyStage loyaltyStage = null;
	
	LoyaltyTransactionParent tranParent = createNewTransaction(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);//?
	Date date = tranParent.getCreatedDate().getTime();
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	String transDate = df.format(date);
	
	LoyaltyEnrollResponse enrollResponse = null;
	String userName = null;
	org.mq.optculture.model.ocloyalty.Status status = null;
	String responseJson = Constants.STRING_NILL;
	String reqJson="";
	Gson gson = new Gson();
	
	//matching doc date with his pos mapping date format.
	try {
	requestHeader.setRequestDate(header.getRequestDate());
	} catch (Exception e) {
		logger.info("date format not matched with data",e);
	}
	requestHeader.setStoreNumber(customer.getHomeStore());
	try {
		
	String mobile = contactRequest.getCustomer().getPhone();
	String email = contactRequest.getCustomer().getEmailAddress();
	
	//APP-2267
	
	if(customer.getMembershipNumber() != null && !customer.getMembershipNumber().isEmpty()) {
		membershipRequest.setCardNumber(customer.getMembershipNumber());
	}
	else {
	
		LoyaltyProgramDao prgmDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		
		List<LoyaltyProgram> listOfPrgms = prgmDao.getAllProgramsListByUserId(user.getUserId());
		LoyaltyProgram defaultLoyaltyProgram = null;
		LoyaltyProgram perkProg = null;
		if(listOfPrgms!=  null) {
			
			for(LoyaltyProgram prgm : listOfPrgms) {
				
				if(prgm.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED)) continue;
				if(prgm.getDefaultFlag()== OCConstants.FLAG_YES ) {
					defaultLoyaltyProgram = prgm;
					
				}
				if(prgm.getRewardType() != null && prgm.getRewardType().equals(OCConstants.REWARD_TYPE_PERK) )
					perkProg = prgm;
				
			}
			
		
		}
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		ContactsLoyalty contactsLoyalty = null;
		if(perkProg != null) {
			logger.info("inside perk program not null");
			
			if(mobile != null && !mobile.isEmpty() && perkProg.getUniqueMobileFlag()==OCConstants.FLAG_YES) {			
				String phoneParse = Utility.phoneParse(mobile.trim(),
						user != null ? user.getUserOrganization() : null);
				logger.info("inside perk program not null and phone parse"+phoneParse);
				contactsLoyalty = contactsLoyaltyDao.getLoyaltyByPrgmAndPhone(user,perkProg.getProgramId(), phoneParse);
				logger.info("contactsLoyalty after contactsLoyalty by phone calling"+contactsLoyalty);
				if(contactsLoyalty!=null) {				
					MembershipResponse membershipResponse = setMembershipResponse(contactsLoyalty);
					String msg = PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactsLoyalty.getCardNumber()+", "
							+	"Email Address:"+ (contactsLoyalty.getEmailId()!=null?contactsLoyalty.getEmailId():"")+", "
							+	"Mobile #:"+(contactsLoyalty.getMobilePhone()!=null?contactsLoyalty.getMobilePhone():"")+", "
							+	"Name:"+  (contactsLoyalty.getContact().getFirstName() !=null?contactsLoyalty.getContact().getFirstName():"") +" "+
								 (contactsLoyalty.getContact().getLastName()!=null?contactsLoyalty.getContact().getLastName():"")+".";
					
					
					status = new org.mq.optculture.model.ocloyalty.Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, null, status);
					return enrollResponse;
				}
			}
			logger.info("before calling contatcs loyalty with email ID>>"+email);
			if(contactsLoyalty== null && email != null && !email.isEmpty() && perkProg.getUniqueEmailFlag()==OCConstants.FLAG_YES) {
				
				logger.info("inside email not null condition"+email);
				contactsLoyalty = findMembershpByEmailId(email,
						perkProg.getProgramId(), user.getUserId(), 
						user.getUserOrganization().getMaxNumberOfDigits());
				//contactsLoyalty = contactsLoyaltyDao.getLoyaltyByPrgmAndEmail(user.getUserId(),perkProg.getProgramId(),email);
				logger.info("after fetching contact loyalty>>"+contactsLoyalty);
				if (contactsLoyalty != null) {

					MembershipResponse membershipResponse = setMembershipResponse(contactsLoyalty);
					String msg = PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactsLoyalty.getCardNumber()+", "
							+	"Email Address:"+ (contactsLoyalty.getEmailId()!=null?contactsLoyalty.getEmailId():"")+", "
							+	"Mobile #:"+(contactsLoyalty.getMobilePhone()!=null?contactsLoyalty.getMobilePhone():"")+", "
							+	"Name:"+  (contactsLoyalty.getContact().getFirstName() !=null?contactsLoyalty.getContact().getFirstName():"") +" "+
								 (contactsLoyalty.getContact().getLastName()!=null?contactsLoyalty.getContact().getLastName():"")+".";
					
					
					status = new org.mq.optculture.model.ocloyalty.Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
					enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, null, status);
					return enrollResponse;
					
				}
			}
			
		}
		//LoyaltyProgram loyaltyProgram = findDefaultProgram(user.getUserId());
		if(defaultLoyaltyProgram == null || !defaultLoyaltyProgram.getStatus().equals(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)){
			status = new org.mq.optculture.model.ocloyalty.Status("111610", PropertyUtil.getErrorMessage(111610, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
			return enrollResponse;
		}
		
		if(contactsLoyalty==null && defaultLoyaltyProgram.getProgramType().equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			if(mobile == null || mobile.isEmpty()) { 				
				status = new org.mq.optculture.model.ocloyalty.Status("111609", PropertyUtil.getErrorMessage(111609, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				return enrollResponse;
			}
			contactsLoyalty = contactsLoyaltyDao.findContLoyaltyByCardId(user.getUserId(),mobile);
			if(contactsLoyalty!=null) {				
				MembershipResponse membershipResponse = setMembershipResponse(contactsLoyalty);
				String msg = PropertyUtil.getErrorMessage(111506, OCConstants.ERROR_LOYALTY_FLAG) + " "+contactsLoyalty.getCardNumber()+", "
						+	"Email Address:"+ (contactsLoyalty.getEmailId()!=null?contactsLoyalty.getEmailId():"")+", "
						+	"Mobile #:"+(contactsLoyalty.getMobilePhone()!=null?contactsLoyalty.getMobilePhone():"")+", "
						+	"Name:"+  (contactsLoyalty.getContact().getFirstName() !=null?contactsLoyalty.getContact().getFirstName():"") +" "+
							 (contactsLoyalty.getContact().getLastName()!=null?contactsLoyalty.getContact().getLastName():"")+".";
				
				
				status = new org.mq.optculture.model.ocloyalty.Status("0", msg, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, membershipResponse, null, status);
				return enrollResponse;
			}
			mobileBasedEnroll=true;
		}
		
	}
	}
	catch(Exception e) {
		logger.info("error===>"+e);
	}	
	if(mobileBasedEnroll){
		membershipRequest.setPhoneNumber(customer.getPhone());
		membershipRequest.setCardNumber("");
	}else{	
		if(membershipRequest.getCardNumber() == null || membershipRequest.getCardNumber().trim().length() == 0) {
				
			membershipRequest.setIssueCardFlag("Y");
		}
		membershipRequest.setPhoneNumber("");
	}
	membershipRequest.setFingerprintValidation(loyalty.getFingerprintValidation());
	customerLoyalty.setCustomerId(customer.getCustomerId());
	customerLoyalty.setFirstName(customer.getFirstName());
	customerLoyalty.setLastName(customer.getLastName());
	customerLoyalty.setPhone(customer.getPhone());
	customerLoyalty.setEmailAddress(customer.getEmailAddress());
	customerLoyalty.setBirthday(customer.getBirthday());
	customerLoyalty.setAddressLine1(customer.getAddressLine1());
	customerLoyalty.setAddressLine2(customer.getAddressLine2());
	customerLoyalty.setCity(customer.getCity()); 
	customerLoyalty.setState(customer.getState());
	customerLoyalty.setPostal(customer.getPostal());
	customerLoyalty.setAnniversary(customer.getAnniversary());
	customerLoyalty.setCountry(customer.getCountry());
	customerLoyalty.setGender(customer.getGender());
	customerLoyalty.setInstanceId(customer.getInstanceId());
	customerLoyalty.setDeviceType(customer.getDeviceType());
	customerLoyalty.setSubsidiaryNumber(customer.getSubsidiaryNumber()!=null?customer.getSubsidiaryNumber():Constants.STRING_NILL);
	customerLoyalty.setPassword(loyalty.getPassword());
	loyaltyUser.setUserName(users.getUserName());
	loyaltyUser.setOrganizationId(users.getOrganizationId());
	loyaltyUser.setToken(users.getToken());
	customerLoyalty.setHomeStore(customer.getHomeStore());
	customerLoyalty.setUDF1(customer.getUDF1());
	customerLoyalty.setUDF2(customer.getUDF2());
	customerLoyalty.setUDF3(customer.getUDF3());
	customerLoyalty.setUDF4(customer.getUDF4());
	customerLoyalty.setUDF5(customer.getUDF5());
	customerLoyalty.setUDF6(customer.getUDF6());
	customerLoyalty.setUDF7(customer.getUDF7());
	customerLoyalty.setUDF8(customer.getUDF8());
	customerLoyalty.setUDF9(customer.getUDF9());
	customerLoyalty.setUDF10(customer.getUDF10());
	customerLoyalty.setUDF11(customer.getUDF11());
	customerLoyalty.setUDF12(customer.getUDF12());
	customerLoyalty.setUDF13(customer.getUDF13());
	customerLoyalty.setUDF14(customer.getUDF14());
	customerLoyalty.setUDF15(customer.getUDF15());

	 
	loyaltyEnrollRequest.setHeader(requestHeader);
	loyaltyEnrollRequest.setMembership(membershipRequest);
	loyaltyEnrollRequest.setCustomer(customerLoyalty);
	loyaltyEnrollRequest.setUser(loyaltyUser);
	
	//find duplicate request
	
		try{
			
			responseHeader.setTransactionDate(transDate);
			responseHeader.setTransactionId(Constants.STRING_NILL+tranParent.getTransactionId());
			
			try{
				reqJson = gson.toJson(loyaltyEnrollRequest, LoyaltyEnrollRequest.class);
				logger.info("reqJson : "+reqJson);
			}catch(Exception e){
				status = new org.mq.optculture.model.ocloyalty.Status("101001", PropertyUtil.getErrorMessage(101001, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
				responseJson = gson.toJson(enrollResponse);
				updateEnrollmentTransaction(tranParent, enrollResponse, null);
				logger.info("Response = "+responseJson);
				logger.error("Exception in parsing request json ...",e);
				return enrollResponse;
			}
			
			userName = loyaltyEnrollRequest.getUser().getUserName() + Constants.USER_AND_ORG_SEPARATOR +
					loyaltyEnrollRequest.getUser().getOrganizationId();
			
			
			loyaltyStage = findDuplicateRequest(loyaltyEnrollRequest);
			if(loyaltyStage != null){
				logger.info("Duplicate request......");
				responseJson = "{\"STATUS\":{\"ERRORCODE\":\"101505\",\"MESSAGE\":\"Error 101505: Request is being processed.\",\"STATUS\":\"Failure\"}}";
				enrollResponse = gson.fromJson(responseJson, LoyaltyEnrollResponse.class);
				updateEnrollmentTransaction(tranParent, enrollResponse, userName);
				logger.info("Response = "+responseJson);
				return enrollResponse;
			}
			else{
				loyaltyStage = saveRequestInStageTable(loyaltyEnrollRequest);
			}
	
	LoyaltyTransaction trans = findRequestBycustSidAndReqId(loyaltyEnrollRequest.getUser().getUserName() + "__" +
			loyaltyEnrollRequest.getUser().getOrganizationId(),loyaltyEnrollRequest.getHeader().getRequestId().trim(),
			loyaltyEnrollRequest.getCustomer().getCustomerId().trim());
	if(trans != null){
		logger.info("duplicate transaction found...");
		responseHeader.setRequestId(loyaltyEnrollRequest.getHeader().getRequestId());
		responseHeader.setRequestDate(loyaltyEnrollRequest.getHeader().getRequestDate());
		responseHeader.setSourceType(sourceType !=null ? sourceType: Constants.STRING_NILL);
		status = new org.mq.optculture.model.ocloyalty.Status("111536", PropertyUtil.getErrorMessage(111536, OCConstants.ERROR_LOYALTY_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
		enrollResponse = prepareEnrollmentResponse(responseHeader, null, null, status);
		logger.info("Response = "+responseJson);
		updateEnrollmentTransaction(tranParent, enrollResponse, null);
		return enrollResponse;
	}

//log transaction
if(trans == null){
	trans = logEnrollmentTransactionRequest(loyaltyEnrollRequest, reqJson, OCConstants.LOYALTY_ONLINE_MODE);
}
	LoyaltyEnrollmentOCService enrollService = (LoyaltyEnrollmentOCService)ServiceLocator.getInstance().getServiceById(OCConstants.LOYALTY_ENROLMENT_OC_BUSINESS_SERVICE);
	 responseObject = enrollService.processEnrollmentRequest(loyaltyEnrollRequest, 
			OCConstants.LOYALTY_ONLINE_MODE, ""+tranParent.getTransactionId(), transDate);
	responseJson = new Gson().toJson(responseObject, LoyaltyEnrollResponse.class);	
	logger.info("Response = "+responseJson);
	updateTransactionStatus(trans, responseJson, responseObject);
	
	}catch(Exception e){
		logger.error("Error in DR to lty impl", e);
		responseJson = "{\"ENROLLMENTRESPONSE\":{\"STATUS\":{\"ERRORCODE\":\"101000\",\"MESSAGE\":\"Server error  101000.\",\"STATUS\":\"Failure\"}}}";
		logger.info("Response = "+responseJson);
	}finally{
		if(loyaltyStage != null) deleteRequestFromStageTable(loyaltyStage);
		//send alert mail for failures
		logger.info("Completed prepareEnrolFromDRRequest");
	}
		logger.info("enrol from DR ended");
	
	return responseObject;
}
	private LoyaltyTransactionParent createNewTransaction(String type){
		
		LoyaltyTransactionParent tranx  = null; 
		try{
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			tranx = new LoyaltyTransactionParent();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			tranx.setCreatedDate(cal);
			tranx.setTransactionType(type);
			parentDaoForDML.saveOrUpdate(tranx);

		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		return tranx;
	}
	
	private LoyaltyEnrollResponse prepareEnrollmentResponse(ResponseHeader header, MembershipResponse membershipResponse,
			List<MatchedCustomer> matchedCustomers, org.mq.optculture.model.ocloyalty.Status status) throws BaseServiceException {
		LoyaltyEnrollResponse enrollResponse = new LoyaltyEnrollResponse();
		enrollResponse.setHeader(header);
		if(membershipResponse == null){
			membershipResponse = new MembershipResponse();
			membershipResponse.setCardNumber(Constants.STRING_NILL);
			membershipResponse.setCardPin(Constants.STRING_NILL);
			membershipResponse.setExpiry(Constants.STRING_NILL);
			membershipResponse.setPhoneNumber(Constants.STRING_NILL);
			membershipResponse.setTierLevel(Constants.STRING_NILL);
			membershipResponse.setTierName(Constants.STRING_NILL);
		}
		if(matchedCustomers == null){
			matchedCustomers = new ArrayList<MatchedCustomer>();
		}
		enrollResponse.setMembership(membershipResponse);
		enrollResponse.setMatchedCustomers(matchedCustomers);
		enrollResponse.setStatus(status);
		return enrollResponse;
	}
private void updateEnrollmentTransaction(LoyaltyTransactionParent trans, LoyaltyEnrollResponse enrollResponse, String userName) {
		
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			
			if(userName != null){
				trans.setUserName(userName);
			}
			if(enrollResponse.getStatus() != null) {
				trans.setStatus(enrollResponse.getStatus().getStatus());
				trans.setErrorMessage(enrollResponse.getStatus().getMessage());
			}
			if(enrollResponse.getHeader() != null){
				trans.setRequestId(enrollResponse.getHeader().getRequestId());
				trans.setRequestDate(enrollResponse.getHeader().getTransactionDate());
			}
			if(enrollResponse.getMembership() != null) {
					trans.setMembershipNumber(enrollResponse.getMembership().getCardNumber());
					trans.setMobilePhone(enrollResponse.getMembership().getPhoneNumber());
			}
			parentDaoForDML.saveOrUpdate(trans);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
	}
private static ContactsLoyaltyStage findDuplicateRequest(LoyaltyEnrollRequest requestObject) {
	//find the request in stage
	ContactsLoyaltyStage loyaltyStage = null;

	try{

		ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
		String custId = requestObject.getCustomer().getCustomerId() == null ? "" : requestObject.getCustomer().getCustomerId().trim(); 
		String email = requestObject.getCustomer().getEmailAddress() == null ? "" : requestObject.getCustomer().getEmailAddress().trim();
		String phone = requestObject.getCustomer().getPhone() == null ? "" : requestObject.getCustomer().getPhone().trim(); 		
		String card = requestObject.getMembership().getCardNumber() == null ? "" : requestObject.getMembership().getCardNumber().trim(); 
		String userName = requestObject.getUser().getUserName()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUser().getOrganizationId();  

		ContactsLoyaltyStage requestStage = contactsLoyaltyStageDao.findRequest(custId, email, phone, card, userName,
				OCConstants.LOYALTY_SERVICE_TYPE_OC, OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
		if(requestStage != null){
			return loyaltyStage;
		}

	}catch(Exception e){
		logger.error("Exception in finding loyalty staging duplicate request...");
	}
	return loyaltyStage;
}
private static ContactsLoyaltyStage saveRequestInStageTable(LoyaltyEnrollRequest requestObject){

	ContactsLoyaltyStage loyaltyStage = null;
	try{

		ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
		ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
		String custId = requestObject.getCustomer().getCustomerId() == null ? "" : requestObject.getCustomer().getCustomerId().trim(); 
		String email = requestObject.getCustomer().getEmailAddress() == null ? "" : requestObject.getCustomer().getEmailAddress().trim();
		String phone = requestObject.getCustomer().getPhone() == null ? "" : requestObject.getCustomer().getPhone().trim(); 		
		String card = requestObject.getMembership().getCardNumber() == null ? "" : requestObject.getMembership().getCardNumber().trim(); 
		String userName = requestObject.getUser().getUserName()+Constants.USER_AND_ORG_SEPARATOR +requestObject.getUser().getOrganizationId();  

		logger.info("saving request in stage table...");
		loyaltyStage = new ContactsLoyaltyStage();
		loyaltyStage.setCustomerId(custId);
		loyaltyStage.setEmailId(email);
		loyaltyStage.setPhoneNumber(phone);
		loyaltyStage.setUserName(userName);
		loyaltyStage.setReqType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
		loyaltyStage.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
		loyaltyStage.setStatus(Constants.LOYALTY_STAGE_PENDING);

		contactsLoyaltyStageDaoForDML.saveOrUpdate(loyaltyStage);
	}catch(Exception e){
		logger.error("Exception while saving loyalty request in stage table...", e);
	}
	return loyaltyStage;

}
private LoyaltyTransaction findRequestBycustSidAndReqId(String userName, String requestId, String custSID) throws Exception {
	LoyaltyTransactionDao loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
	return loyaltyTransactionDao.findRequestBycustSidAndReqId(userName, custSID, requestId, OCConstants.LOYALTY_TRANSACTION_ENROLMENT, OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
}
public LoyaltyTransaction logEnrollmentTransactionRequest(LoyaltyEnrollRequest requestObject, String jsonRequest, String mode){
	LoyaltyTransactionDao loyaltyTransactionDao = null;
	LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;

	LoyaltyTransaction transaction = null;
	try {
		loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
		transaction = new LoyaltyTransaction();
		transaction.setJsonRequest(jsonRequest);
		transaction.setRequestId(requestObject.getHeader().getRequestId());
		transaction.setPcFlag(Boolean.valueOf(requestObject.getHeader().getPcFlag()));
		transaction.setMode(mode);//online or offline
		transaction.setRequestDate(Calendar.getInstance());
		transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
		transaction.setType(OCConstants.LOYALTY_TRANSACTION_ENROLMENT);
		transaction.setUserDetail(requestObject.getUser().getUserName()+"__"+requestObject.getUser().getOrganizationId());
		transaction.setCustomerId(requestObject.getCustomer().getCustomerId().trim());
		transaction.setStoreNumber(requestObject.getHeader().getStoreNumber().trim());
		transaction.setEmployeeId(requestObject.getHeader().getEmployeeId()!=null && !requestObject.getHeader().getEmployeeId().trim().isEmpty() ? requestObject.getHeader().getEmployeeId().trim():null);
		transaction.setTerminalId(requestObject.getHeader().getTerminalId()!=null && !requestObject.getHeader().getTerminalId().trim().isEmpty() ? requestObject.getHeader().getTerminalId().trim():null);
		loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
		
	} catch (Exception e) {
		logger.error("Exception in logging transaction", e);
	}
	return transaction;
}
public void updateTransactionStatus(LoyaltyTransaction transaction, String responseJson, LoyaltyEnrollResponse response){
	LoyaltyTransactionDao loyaltyTransactionDao = null;
	LoyaltyTransactionDaoForDML loyaltyTransactionDaoForDML = null;
	try {
		loyaltyTransactionDao = (LoyaltyTransactionDao)ServiceLocator.getInstance().getDAOByName("loyaltyTransactionDao");
		loyaltyTransactionDaoForDML = (LoyaltyTransactionDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("loyaltyTransactionDaoForDML");
		transaction.setStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_PROCESSED);
		transaction.setJsonResponse(responseJson);
		if (response.getMembership() != null && response.getMembership().getCardNumber() != null &&
				!response.getMembership().getCardNumber().trim().isEmpty()) {
			transaction.setCardNumber(response.getMembership().getCardNumber());
		} else {
			transaction.setCardNumber(response.getMembership() == null ? "" : response.getMembership().getPhoneNumber());
		}
		loyaltyTransactionDaoForDML.saveOrUpdate(transaction);
	}catch(Exception e){
		logger.error("Exception in updating transaction", e);
	}
}
private static void deleteRequestFromStageTable(ContactsLoyaltyStage loyaltyStage) {

	try{

		ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
		ContactsLoyaltyStageDaoForDML contactsLoyaltyStageDaoForDML = (ContactsLoyaltyStageDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO_FOR_DML);
		logger.info("deleting loyalty stage record...");
		contactsLoyaltyStageDaoForDML.delete(loyaltyStage);

	}catch(Exception e){
		logger.error("Exception in while deleting request record from staging table...", e);
	}

}
public String getStartDate(String dateStr,ApplicationProperties serveTime ){
	
	int serveTimeStr = Integer.parseInt(serveTime.getValue());
	int timeDiff=0;
	
	String arrDate[]=dateStr.split(" ");
	if(arrDate[2]!=null) {
		String timeZone[]=arrDate[2].substring(3).split(":");
		if(Integer.parseInt(timeZone[0])<0)
			timeDiff=Integer.parseInt(timeZone[0])*60-Integer.parseInt(timeZone[1]);
		else
			timeDiff=Integer.parseInt(timeZone[0])*60+Integer.parseInt(timeZone[1]);
	}
		timeDiff=serveTimeStr-timeDiff;	
	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Calendar cal=MyCalendar.string2Calendar(arrDate[0]+" "+arrDate[1]);  
	cal.add(Calendar.MINUTE, timeDiff);

	return formatter.format(cal.getTime());
}
private LoyaltyProgram findDefaultProgram(Long userId) throws Exception {
	LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance()
			.getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
	return loyaltyProgramDao.findDefaultProgramByUserId(userId);
}	
private ContactsLoyalty findMembershpByPhone(String mobilePhone, Long programId, Long userId, short countryCarrier,
		int maxDigits) throws Exception {

	mobilePhone = mobilePhone.replaceAll("[- ()]", "");// APP-117
	if (mobilePhone != null && mobilePhone.trim().length() != 0) {
		mobilePhone = mobilePhone.trim();
		if (mobilePhone != null && mobilePhone.startsWith(countryCarrier + "")
				&& mobilePhone.length() > maxDigits) {
			mobilePhone = mobilePhone.replaceFirst(countryCarrier + "", "");
		}
		try {
			mobilePhone = Long.parseLong(mobilePhone) + "";

		} catch (Exception e) {
			logger.info("OOPs error ");
		}
	}

	String mobileWithCarrier = countryCarrier + mobilePhone;
	ContactsLoyalty loyalty = null;
	ContactsLoyaltyDao contactLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
			.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
	loyalty = contactLoyaltyDao.findByMobilePhone(mobilePhone, mobileWithCarrier, programId, userId);
	return loyalty;
}
private ContactsLoyalty findMembershpByEmailId(String emailId, Long programId, Long userId,
		int maxDigits) throws Exception {

	ContactsLoyalty loyalty = null;
	ContactsLoyaltyDao contactLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
			.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
	loyalty = contactLoyaltyDao.findMembershipByEmailInCl( emailId, userId,programId);
	return loyalty;
}
private MembershipResponse setMembershipResponse(ContactsLoyalty contactsLoyalty){
	
	MembershipResponse	membershipResponse = new MembershipResponse();
			membershipResponse.setExpiry("");
			membershipResponse.setPhoneNumber(contactsLoyalty!=null && contactsLoyalty.getMobilePhone() !=null ?contactsLoyalty.getMobilePhone():"");
			membershipResponse.setTierLevel("");
			membershipResponse.setTierName("");
			membershipResponse.setCardNumber(contactsLoyalty!=null && contactsLoyalty.getCardNumber() !=null ?contactsLoyalty.getCardNumber() : "");
			//membershipResponse.setPassword("");
			membershipResponse.setCardPin("");
			membershipResponse.setFingerprintValidation("");
			membershipResponse.setEmailAddress(contactsLoyalty!=null && contactsLoyalty.getEmailId() != null ?contactsLoyalty.getEmailId():"");
		return membershipResponse;
}

	
	
}
