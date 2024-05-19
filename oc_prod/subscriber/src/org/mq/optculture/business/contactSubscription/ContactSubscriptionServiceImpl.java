package org.mq.optculture.business.contactSubscription;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomFieldData;
import org.mq.marketer.campaign.beans.CustomerFeedback;
import org.mq.marketer.campaign.beans.FormMapping;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.service.UpdateLoyaltyTransactionChildForRewardsWebForm;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.CustomFieldDataDao;
import org.mq.marketer.campaign.dao.CustomFieldDataDaoForDML;
import org.mq.marketer.campaign.dao.CustomerFeedbackDao;
import org.mq.marketer.campaign.dao.CustomerFeedbackDaoForDML;
import org.mq.marketer.campaign.dao.FormMappingDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.optculture.business.helper.ContactSubscriptionProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.ContactSubscriptionRequestObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

 
public class ContactSubscriptionServiceImpl implements ContactEnrollmentService {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject)	throws BaseServiceException {
		BaseResponseObject baseResponseObject = null;
		ContactSubscriptionRequestObject contactSubscriptionRequestObject = (ContactSubscriptionRequestObject) baseRequestObject;
		ContactEnrollmentService contactSubscriptionService = (ContactEnrollmentService) ServiceLocator.getInstance().getServiceByName(OCConstants.CONTACT_SUBSCRIPTION_BUSINESS_SERVICE);
		try {
			baseResponseObject = (BaseResponseObject) contactSubscriptionService.processContactSubscriptionRequest(contactSubscriptionRequestObject);
		} catch (Exception e) {
			logger.debug("Exception",e);
		}
		return baseResponseObject;
	}


	@Override
	public BaseResponseObject processContactSubscriptionRequest(ContactSubscriptionRequestObject contactSubscriptionRequestObject)
			throws BaseServiceException {
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		FormMapping formMapping = null;
		PurgeList purgeList = (PurgeList) ServiceLocator.getInstance().getBeanByName(OCConstants.PURGELIST);
		String msg = "";
		String dbErrmsg = "";
		String parentalSucessMsg = "";
		boolean isEnableEvent = false;
		boolean isOptinChecked=false;
		long fmId = Long.parseLong(contactSubscriptionRequestObject.getFormId());
		String hId = fmId+"";
		ContactSubscriptionProcessHelper contactSubscriptionProcessHelper = new ContactSubscriptionProcessHelper();
		try {
			logger.debug("---------Entered -----------");
			ServiceLocator locator = ServiceLocator.getInstance();
			logger.debug("Entered try ");

			FormMappingDao formMappingDao = (FormMappingDao) locator.getDAOByName(OCConstants.FORM_MAPPING_DAO);
			formMapping = formMappingDao.findById(fmId);
			String retMsg = contactSubscriptionProcessHelper.validateFormMapping(formMapping);
			if(retMsg != null) {
				msg += retMsg;
				baseResponseObject.setResponseStr(retMsg);
				return baseResponseObject;
			}

			HashMap<String, List> inputMapSettingHM = contactSubscriptionProcessHelper.prepareInputMap( formMapping.getInputFieldMapping());
			if(inputMapSettingHM == null ) {
				baseResponseObject.setResponseStr("form input fields mapping not found");
				return baseResponseObject;

			}

			HashMap<String, String> formMapValuesHM = new HashMap<String, String>();
			formMapValuesHM = contactSubscriptionProcessHelper.prepareFormMapValues(contactSubscriptionRequestObject.getFormValuesMap() , inputMapSettingHM);
			if(formMapValuesHM == null )  {
				baseResponseObject.setResponseStr("form value map is null");
				return baseResponseObject;
			}

			logger.debug("inputMapSettingHM :" + inputMapSettingHM);
			logger.debug("formMapValuesHM :" + formMapValuesHM);
			logger.debug("formMapValuesHM size :" + formMapValuesHM.size());
			logger.debug("received form  size :" + contactSubscriptionRequestObject.getFormValuesMap());

			if(Constants.WEBFORM_TYPE_LOYALTY_CARD.equalsIgnoreCase(formMapping.getFormType())) {
				String cardNumber = null;
				String cardPin = null;
				Map<String,String> cardDetails = contactSubscriptionProcessHelper.getCardNumberAndPin(formMapValuesHM, inputMapSettingHM);
				ContactsLoyalty contactLoyalty = null;
				Contacts ltyContact = null;
				if(cardDetails !=null ) {
					cardNumber = cardDetails.get("cardNumber");
					cardPin = cardDetails.get("cardPin");
					//if(cardNumber == null || cardNumber.isEmpty() || cardPin == null || cardPin.isEmpty()){
					if(cardNumber == null || cardNumber.isEmpty()){
						msg += " \n card number is null";
						baseResponseObject.setResponseStr(msg);
						return baseResponseObject;
					}
				}
				ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) locator.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
				ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML)locator.getDAOForDMLByName(("contactsLoyaltyDaoForDML"));
				LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) locator.getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
				LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML) locator.getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
				LoyaltyCards ltyCard = loyaltyCardsDao.findByCardNoAndCardPin(cardNumber, cardPin);
				boolean isOCCard = false;
				if(ltyCard != null) isOCCard = true;
				
				contactLoyalty = contactsLoyaltyDao.findContLoyaltyByCardIdAndPin(formMapping.getUsers().getUserId(), Long.parseLong(cardNumber), cardPin,isOCCard);
				if (contactLoyalty == null) {
					msg += " \n no loyalty contact found with given card number";
					baseResponseObject.setResponseStr(msg);
					return baseResponseObject;
				}
				UsersDao usersDao = (UsersDao) locator.getDAOByName(OCConstants.USERS_DAO);
				Users contLtyUser = usersDao.findByUserId(contactLoyalty.getUserId());
				MailingListDao mailingListDao=(MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
				MailingList posList = mailingListDao.findListTypeMailingList(Constants.MAILINGLIST_TYPE_POS, contLtyUser.getUserId());

				Contacts inputContact = contactSubscriptionProcessHelper.prepareInputContactObj(contLtyUser, posList.getMlBit().longValue());
				if(inputContact != null) logger.debug("contact    ");
				Map<String, Object> contactAndDataFlags = contactSubscriptionProcessHelper.setInputConatctValue(formMapValuesHM, inputMapSettingHM, contLtyUser, true, formMapping, inputContact);
				if(contactAndDataFlags != null) {
					inputContact = (Contacts) contactAndDataFlags.get("contact");
					msg += (String) contactAndDataFlags.get("msg");
				}

				ltyContact = contactLoyalty.getContact();
				if (ltyContact == null) {
					if ((inputContact.getEmailId() == null || inputContact.getEmailId().trim().length() == 0) &&
							(inputContact.getMobilePhone() == null || inputContact.getMobilePhone().trim().length() == 0)) {
						msg += "\n No email Id, mobile, card Number ";
						baseResponseObject.setResponseStr(msg);
						return baseResponseObject;
					}
//					if (inputContact.getEmailId() != null || inputContact.getMobilePhone() != null) {
					ltyContact = contactSubscriptionProcessHelper.getDbContactWithPrority(contLtyUser,inputContact);
//					}

					if (ltyContact != null) {
						Map<String, Object> contactWithLoyalty = contactSubscriptionProcessHelper.processExistingContactForLoyalty( inputContact, ltyContact,formMapping, posList,
								contLtyUser, purgeList);
						ltyContact = (Contacts) contactWithLoyalty.get("ltyContact");
//						dbErrmsg += (String) contactWithLoyalty.get("dbErrmsg");
					}// if contact exist
					else {
						Map<String, Object> contactWithLoyalty = contactSubscriptionProcessHelper.processNewContactForLoyalty(inputContact,  posList,  contLtyUser,
								purgeList,  formMapping);
						ltyContact = (Contacts) contactWithLoyalty.get("ltyContact");
						msg += (String) contactWithLoyalty.get("msg");
					}// if new contact
					ltyContact.setLoyaltyCustomer((byte) 1);
					contactLoyalty.setContact(ltyContact);
				}
				else {
					Map<String, Object> contactWithLoyalty = contactSubscriptionProcessHelper.processExistingContactForLoyalty(inputContact,ltyContact,formMapping,
							posList,contLtyUser, purgeList);
					ltyContact = (Contacts) contactWithLoyalty.get("ltyContact");
//					dbErrmsg += (String) contactWithLoyalty.get("dbErrmsg");
				}
				ContactsDaoForDML contactsDaoDml =  (ContactsDaoForDML) locator.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
				contactsDaoDml.saveOrUpdate(ltyContact);
				contactLoyalty.setIsRegistered((byte)1);
				contactsLoyaltyDaoForDML.saveOrUpdate(contactLoyalty);
				
				LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(ltyContact, ltyContact.getMobilePhone());
				LoyaltyProgramHelper.updateLoyaltyEmailId(ltyContact, ltyContact.getEmailId()); 
				if(ltyCard != null) {
					ltyCard.setRegisteredFlag(OCConstants.FLAG_YES);
					//loyaltyCardsDao.saveOrUpdate(ltyCard);
					loyaltyCardsDaoForDML.saveOrUpdate(ltyCard);
				}
			}//if form type loyalty card verification
			else { //form type is sign-up
				Long listId = formMapping.getListId();
				if(listId == null) {
					logger.error("Exception : Error occured while fetch FormMapping Object **");
					msg += "\n form configured List is not found";
					baseResponseObject.setResponseStr(msg);
					return baseResponseObject;
				}
				MailingListDao  mailingListDao = (MailingListDao) locator.getDAOByName(OCConstants.MAILINGLIST_DAO);
				MailingList mailingList = mailingListDao.findByListId(listId);
				if(mailingList == null) {
					logger.error("Exception : mailing list is not found");
					baseResponseObject.setResponseStr("\n mailing list is not found");
					return baseResponseObject;
				}
				UsersDao usersDao = (UsersDao) locator.getDAOByName(OCConstants.USERS_DAO);
				Users user = usersDao.findMlUser(mailingList.getUsers().getUserId());

				Contacts inputContact = contactSubscriptionProcessHelper.prepareInputContactObj(user, mailingList.getMlBit().longValue());

				String parentEmail = null;
				String storeLocation = null;
				String enroll = null;
				String loyaltyPoints = null;
				String loyaltyCurrency = null;
				String invoiceAmount = null;
				String invoiceNumber = null;
				
				CustomFieldData customFieldData = new CustomFieldData();
				boolean isCFDataSet = false;

				Map<String, Object> contactAndDataFlags = contactSubscriptionProcessHelper.setInputConatctValue(formMapValuesHM, inputMapSettingHM, user, false, formMapping, inputContact);
				if(contactAndDataFlags != null) {
					inputContact = (Contacts) contactAndDataFlags.get("contact");
					msg += (String)  contactAndDataFlags.get("msg");
					isCFDataSet = (Boolean) contactAndDataFlags.get("isCFDataSet");
					parentEmail = (String) contactAndDataFlags.get("parentEmail");
					storeLocation = (String) contactAndDataFlags.get("storeLocation");
					enroll = (String) contactAndDataFlags.get("enroll");
					loyaltyPoints = (String) contactAndDataFlags.get("loyaltyPoints");
					invoiceAmount = (String) contactAndDataFlags.get("invoiceAmount");
					invoiceNumber = (String) contactAndDataFlags.get("invoiceNumber");
					loyaltyCurrency = (String)contactAndDataFlags.get("loyaltyCurrency");
				}
				String DOCSID = formMapValuesHM.get("DOCSID");
				String customerNo = formMapValuesHM.get("customerNo");
				if(!Constants.FEED_BACK_WEB_FORM.equalsIgnoreCase(formMapping.getFormType())) {
					if ((inputContact.getEmailId() == null || inputContact.getEmailId().trim().length() == 0) &&
							(inputContact.getMobilePhone() == null || inputContact.getMobilePhone().trim().length() == 0)) {
						msg += "\n No email Id, mobile, card Number ";
						baseResponseObject.setResponseStr(msg);
						return baseResponseObject;
					}
				}else {
					if((DOCSID == null || customerNo == null) && ((inputContact.getEmailId() == null || inputContact.getEmailId().trim().length() == 0) &&
							(inputContact.getMobilePhone() == null || inputContact.getMobilePhone().trim().length() == 0))) {
						msg += "\n No email Id, mobile, card Number,DOCSID ,customerNo";
						baseResponseObject.setResponseStr(msg);
						return baseResponseObject;
					}
				}

				isOptinChecked = contactSubscriptionProcessHelper.checkMobileOptin(formMapValuesHM, inputMapSettingHM);
				
				boolean isMinor = false;
				if(Constants.WEBFORM_TYPE_SIGNUP.equalsIgnoreCase(formMapping.getFormType()) && formMapping.getEnableParentalConsent() == OCConstants.FLAG_YES){
					if (inputContact.getBirthDay() == null){
						msg += "\n No birthday entered";
						baseResponseObject.setResponseStr(msg);
						return baseResponseObject;
					}
					Map<String, Object> minorContactFlags = contactSubscriptionProcessHelper.createMinorContact(formMapping, user, inputContact, mailingList, parentEmail);
					isMinor = (Boolean) minorContactFlags.get("isMinor");
					isEnableEvent = (Boolean) minorContactFlags.get("isEnableEvent");
					parentalSucessMsg = (String) minorContactFlags.get("parentalSucessMsg");
					if(isMinor) return baseResponseObject;
				}
				if(!Constants.FEED_BACK_WEB_FORM.equalsIgnoreCase(formMapping.getFormType()) && DOCSID==null && customerNo==null) {
					if(isMinor == false && inputContact.getEmailId() == null && inputContact.getMobilePhone() == null) {
						msg += "\n No email Id or phone number and is not an underAge";
						baseResponseObject.setResponseStr(msg);
						return baseResponseObject;
					}
				}
				
				
				String validMobileNoforMobileBasedLoyaltyPgm = contactSubscriptionProcessHelper.getMobilePhone(formMapValuesHM, inputMapSettingHM,user,formMapping);
				if(validMobileNoforMobileBasedLoyaltyPgm!=null) {
					baseResponseObject.setResponseStr(validMobileNoforMobileBasedLoyaltyPgm);
					return baseResponseObject;
				}
				
				
				// Check the Contact exists with the unique identifier column value
				Contacts dbContact = null;
//				if (inputContact.getEmailId() != null || inputContact.getMobilePhone() != null) {
				dbContact = contactSubscriptionProcessHelper.getDbContactWithPrority(user,inputContact);
//				}

				if (dbContact != null) {
					//Map<String, Object> messageRetVal = contactSubscriptionProcessHelper.processExistingContact( dbContact, inputContact, mailingList, user, purgeList, formMapping, isEnableEvent, enroll, storeLocation);
					Map<String, Object> messageRetVal = contactSubscriptionProcessHelper.processExistingContact( dbContact, inputContact, mailingList
							, user, purgeList, formMapping, isEnableEvent, enroll, storeLocation,isOptinChecked);
					dbErrmsg += (String) messageRetVal.get("dbErrmsg");
					msg += (String) messageRetVal.get("msg");	
				}// if contact exist
				else {
					//msg += contactSubscriptionProcessHelper.processNewContact(inputContact,  mailingList,  user,  purgeList,  formMapping,  isEnableEvent, enroll, storeLocation, isMinor);
					msg += contactSubscriptionProcessHelper.processNewContact(inputContact,  mailingList,  user,  purgeList,  formMapping,  isEnableEvent, enroll, storeLocation,
							loyaltyPoints,loyaltyCurrency,invoiceAmount, invoiceNumber, isMinor,isOptinChecked);
				}// if new contact
				if (isCFDataSet) {
					try {
						customFieldData.setContact(inputContact);
						CustomFieldDataDao cfDataDao = (CustomFieldDataDao) locator.getDAOByName(OCConstants.CUSTOM_FIELD_DAO);
						CustomFieldDataDaoForDML cfDataDaoForDML = (CustomFieldDataDaoForDML) locator.getDAOForDMLByName(OCConstants.CUSTOM_FIELD_DAO_FORDML);
					//	cfDataDao.saveOrUpdate(customFieldData);
						cfDataDaoForDML.saveOrUpdate(customFieldData);

					} catch (Exception e) {
						logger.debug("Problem while saving custom field : ", e);
						msg += "\n Problem while saving custom field :";
						baseResponseObject.setResponseStr(msg);
						return baseResponseObject;
					}
				}

				if(Constants.FEED_BACK_WEB_FORM.equalsIgnoreCase(formMapping.getFormType())) {
					Map<String,String> customerRatingAndFeedback = contactSubscriptionProcessHelper.getCustomerRatingAndFeedback(formMapValuesHM, inputMapSettingHM);
					String customerRating = null;
					Map<Integer,String> customerRatingMap = new LinkedHashMap<>();
					for(int i=1;i<=20;i++) {
						 customerRating = (String) customerRatingAndFeedback.get("Rating"+i+"");
						 if(customerRating!=null && !customerRating.isEmpty()) {
							 customerRatingMap.put(i, customerRating);
						 }
					}
					String customerFeedbackMessage = (String)  customerRatingAndFeedback.get("FeedBackMessage");
					String customerStore = (String)  customerRatingAndFeedback.get("feedBackStore");
					Contacts weformContact = null;
					if(dbContact!=null && dbContact.getContactId()!=null) {
						weformContact = dbContact;
					}else if(inputContact!=null && inputContact.getContactId()!=null) {
						weformContact = inputContact;
					}
					
					if(formMapping!=null && formMapping.getUsers()!=null) {
						CustomerFeedbackDaoForDML  customerFeedbackDaoForDML = (CustomerFeedbackDaoForDML) locator.getDAOForDMLByName(OCConstants.CUSTOMERFEEDBACK_DAO_FOR_DML);
						CustomerFeedbackDao  customerFeedbackDao = (CustomerFeedbackDao) locator.getDAOByName(OCConstants.CUSTOMERFEEDBACK_DAO);
						CustomerFeedback customerFeedback = null;
						if(DOCSID!=null && !DOCSID.isEmpty() && !customerNo.isEmpty() && customerNo!=null) {
							customerFeedback = customerFeedbackDao.findFeedbackByDocSidCustIdandUserId(DOCSID,customerNo,formMapping.getUsers().getUserId());
						} 
						if(customerFeedback == null) {
							customerFeedback = new CustomerFeedback();
						}
						customerFeedback.setUserId(formMapping.getUsers().getUserId());
						if(weformContact!=null && weformContact.getContactId()!=null) {
							customerFeedback.setContactId(weformContact.getContactId());
						}
						customerFeedback.setFeedBackMessage(customerFeedbackMessage);
						customerFeedback.setStore(customerStore);
						setCustomerFeedBack(customerFeedback,customerRatingMap);
						customerFeedback.setDOCSID(DOCSID);
						customerFeedback.setCustomerNo(customerNo);
						customerFeedback.setSource(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMapping.getFormMappingName());
						customerFeedback.setCreatedDate(Calendar.getInstance());
						customerFeedbackDaoForDML.saveOrUpdate(customerFeedback);
						
						if(weformContact!=null && weformContact.getEmailId() != null && !weformContact.getEmailId().isEmpty() && formMapping.getCheckFeedbackFormEmail() == OCConstants.FLAG_YES ) {
							contactSubscriptionProcessHelper.sendFeedBackMailOnSuccessfulSubmission(formMapping.getFeedBackMailCustTemplateId(),formMapping.getUsers(),weformContact);
						}
						if(weformContact!=null && weformContact.getMobilePhone()!=null && !weformContact.getMobilePhone().isEmpty() && formMapping.getCheckFeedbackFormSms() == OCConstants.FLAG_YES) {
							
							if(customerFeedback.getContactId()!=null) {
								String source = Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMapping.getFormMappingName();
								customerFeedback = customerFeedbackDao.findFeedbackByUserIdandContactId(customerFeedback.getUserId(),customerFeedback.getContactId() ,source);
							// add condition that weformContact has filled the feedback form  in the last 30 days from created date
								if (customerFeedback == null) {
								logger.info("entering customerFeedback null condition");	
								contactSubscriptionProcessHelper.sendFeedBackSMSOnSuccessfulSubmission(formMapping, weformContact.getContactId(),weformContact.getMobilePhone());
								}
							}
						}
						dbErrmsg +="for webFeedBack";
						logger.debug("FeedBack added successfully ::");
						baseResponseObject.setResponseStr(msg);
						
						if(formMapping.getIssueRewardIschecked() == OCConstants.FLAG_YES) {
							logger.info(":: entering checkForMultipleRewardSubmissionBasedonUserIdandContactId ::");
								checkForMultipleRewardSubmissionBasedonUserIdandContactId(formMapping,weformContact.getContactId());
							logger.info(":: exit form checkForMultipleRewardSubmissionBasedonUserIdandContactId ::");
						}
						
						
						return baseResponseObject;
					}
						/*span a thread*/
				}
				if(Constants.WEBFORM_TYPE_SIGNUP.equalsIgnoreCase(formMapping.getFormType())) {
					Contacts contact = null;
					if(dbContact!=null && dbContact.getContactId()!=null) {
						contact = dbContact;
					}else if(inputContact!=null && inputContact.getContactId()!=null) {
						contact = inputContact;
					}
					if(inputContact.getEmailId() != null && !inputContact.getEmailId().isEmpty() && formMapping.getCheckSimpleSignUpForEmail() == OCConstants.FLAG_YES ) {
						contactSubscriptionProcessHelper.sendSimpleSignUpMailOnSuccessfulSubmission(formMapping.getSimpleSignUpCustTemplateId(),formMapping.getUsers(),inputContact);
					}
					if(inputContact.getMobilePhone()!=null && !inputContact.getMobilePhone().isEmpty() && formMapping.getCheckSimpleSignUpFormSms() == OCConstants.FLAG_YES) {
						contactSubscriptionProcessHelper.sendSimpleSignUpSMSOnSuccessfulSubmission(formMapping, contact.getContactId(),inputContact.getMobilePhone());
					}
				}
				logger.debug("Contacts Has Been Saved Successfully .");
				logger.debug("---------Exiting -----------");
			}
		}catch(Exception e){
			logger.error("** Exception: Error occured while returning image for Form Mapping ** ", e);
		}
		finally {
			try {
				logger.debug("**** finally called ********");
				if (formMapping == null) {
					logger.error("No form Mapping found with the given hid");
					baseResponseObject.setResponseStr("No form Mapping found with the given hid");
					baseResponseObject.setAction(OCConstants.FORM_MAPPING_NULL_URL);
					return baseResponseObject;
				}
				baseResponseObject = contactSubscriptionProcessHelper.setResponseObject(msg, dbErrmsg, parentalSucessMsg, hId, formMapping.getURL(), formMapping);
				return baseResponseObject;
			} catch (Exception e) {
				logger.error("****Error occured while redirecting user to URL.",e);
			}
		}
		return baseResponseObject;
	}

	private CustomerFeedback setCustomerFeedBack(CustomerFeedback customerFeedback, Map<Integer, String> customerRatingMap) {
		for( Entry<Integer, String> map:customerRatingMap.entrySet()){
			switch (map.getKey()) {
			case 1:
				customerFeedback.setUdf1(map.getValue());
				break;
			case 2:
				customerFeedback.setUdf2(map.getValue());
				break;
			case 3:
				customerFeedback.setUdf3(map.getValue());
				break;
			case 4:
				customerFeedback.setUdf4(map.getValue());
				break;
			case 5:
				customerFeedback.setUdf5(map.getValue());
				break;
			case 6:
				customerFeedback.setUdf6(map.getValue());
				break;
			case 7:
				customerFeedback.setUdf7(map.getValue());
				break;
			case 8:
				customerFeedback.setUdf8(map.getValue());
				break;
			case 9:
				customerFeedback.setUdf9(map.getValue());
				break;
			case 10:
				customerFeedback.setUdf10(map.getValue());
				break;
			case 11:
				customerFeedback.setUdf11(map.getValue());
				break;
			case 12:
				customerFeedback.setUdf12(map.getValue());
				break;
			case 13:
				customerFeedback.setUdf13(map.getValue());
				break;
			case 14:
				customerFeedback.setUdf14(map.getValue());
				break;
			case 15:
				customerFeedback.setUdf15(map.getValue());
				break;
			case 16:
				customerFeedback.setUdf16(map.getValue());
				break;
			case 17:
				customerFeedback.setUdf17(map.getValue());
				break;
			case 18:
				customerFeedback.setUdf18(map.getValue());
				break;
			case 19:
				customerFeedback.setUdf19(map.getValue());
				break;
			case 20:
				customerFeedback.setUdf20(map.getValue());
				break;
			
			}
		}
		return customerFeedback;
	}


	private void checkForMultipleRewardSubmissionBasedonUserIdandContactId(FormMapping formMapping, Long contactId) {
		try {
		 Calendar curentdate = Calendar.getInstance();
		 //SimpleDateFormat sp = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STYEAR);
		//String curentdateString =  MyCalendar.calendarToString(curentdate, MyCalendar.FORMAT_DATETIME_STYEAR);
		 
	 
	    String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
		int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
		logger.info("ServerTime.... "+serverTimeZoneValInt);
		String timezoneDiffrenceMinutes = formMapping.getUsers().getClientTimeZone();
		int timezoneDiffrenceMinutesInt = 0;
		if(timezoneDiffrenceMinutes != null) 
			timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
		logger.info("ClientTime.... "+timezoneDiffrenceMinutesInt);
		timezoneDiffrenceMinutesInt = serverTimeZoneValInt - timezoneDiffrenceMinutesInt;
		logger.info("Client time to Server Time.."+timezoneDiffrenceMinutesInt);
		curentdate.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt );
		String curentdateString =  MyCalendar.calendarToString(curentdate, MyCalendar.FORMAT_DATETIME_STYEAR);
		 // convert to client time zone(app user time zone) from server time 
		 
		 
		 ContactsLoyalty contactsLoyalty = findLoyaltyListByContactId(formMapping.getUsers().getUserId(),contactId);
		 if(contactsLoyalty!= null && contactsLoyalty.getCardNumber()!=null) {
		 LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		 Object[] loyaltyTransactionChild  = loyaltyTransactionChildDao.getLatestDocsidByCreatedDateAndTransctionType(contactsLoyalty.getUserId(), contactsLoyalty.getLoyaltyId(), timezoneDiffrenceMinutesInt,curentdateString,OCConstants.LOYALTY_TRANSACTION_ISSUANCE,OCConstants.LOYALTY_TYPE_PURCHASE);
		 if(loyaltyTransactionChild == null) 
			return ;
		 //String loyaltyTransactionChilcurentdateString = loyaltyTransactionChild[1].toString();
		 Object[] loyaltyRewardTransaction = loyaltyTransactionChildDao.getLatestDocsidByCreatedDateAndTransctionTypeAndDocSId(formMapping.getUsers().getUserId(),contactsLoyalty.getLoyaltyId(),curentdateString, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REWARD, loyaltyTransactionChild[0].toString(), Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMapping.getFormMappingName(),timezoneDiffrenceMinutesInt);
		 if(loyaltyRewardTransaction != null ) 
			 return ;
		 
		 UpdateLoyaltyTransactionChildForRewardsWebForm thread = new UpdateLoyaltyTransactionChildForRewardsWebForm(formMapping, Long.valueOf(contactId));
		 thread.start();
		 
		 }
		}catch (Exception e) {
			logger.error("checkForMultipleRewardSubmissionBasedonUserIdandContactId ::"+e);
		}
		
	}


	private ContactsLoyalty findLoyaltyListByContactId(Long userId, Long contactId) throws Exception {

		ContactsLoyalty contactLoyalty = null;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance()
				.getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		List<ContactsLoyalty> loyaltyList = contactsLoyaltyDao.findLoyaltyListByContactId(userId,contactId);
		if (loyaltyList != null && loyaltyList.size() > 0) {
			Iterator<ContactsLoyalty> iterList = loyaltyList.iterator();
			ContactsLoyalty latestLoyalty = null;
			ContactsLoyalty iterLoyalty = null;
			while (iterList.hasNext()) {
				iterLoyalty = iterList.next();
				if (latestLoyalty != null && latestLoyalty.getCreatedDate().after(iterLoyalty.getCreatedDate())) {
					continue;
				}
				latestLoyalty = iterLoyalty;
			}
			contactLoyalty = latestLoyalty;
		}
		return contactLoyalty;
	}
	
	
}
