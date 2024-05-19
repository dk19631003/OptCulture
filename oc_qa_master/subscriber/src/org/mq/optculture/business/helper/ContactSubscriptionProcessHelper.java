package org.mq.optculture.business.helper;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.AutoSmsQueue;
import org.mq.marketer.campaign.beans.ContactParentalConsent;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomFieldData;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.FormMapping;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LoyaltyTransactionExpiry;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.Unsubscribes;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.contacts.CustomFieldValidator;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.AutoSmsQueueDao;
import org.mq.marketer.campaign.dao.AutoSmsQueueDaoForDML;
import org.mq.marketer.campaign.dao.ContactParentalConsentDao;
import org.mq.marketer.campaign.dao.ContactParentalConsentDaoForDML;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.EventTriggerDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UnsubscribesDao;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.EncryptDecryptUrlParameters;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyAutoCommGenerator;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyThresholdAlertsDao;
import org.mq.optculture.data.dao.LoyaltyThresholdAlertsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDao;
import org.mq.optculture.data.dao.LoyaltyTransactionParentDaoForDML;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Listitem;

public class ContactSubscriptionProcessHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	Calendar now = Calendar.getInstance();
	/**
	 * Validates formMapping object
	 * @param formMapping
	 * @return
	 */
	public String validateFormMapping(FormMapping formMapping) {
		logger.debug("---------Entered validateFormMapping-----------");
		String msg = null;
		if (formMapping == null) {
			logger.debug("Error occured while fetch FormMapping Object **");
			msg = "\n given form is Not mapped";
		}
		else{
			if(formMapping.isEnable() == null || !formMapping.isEnable()) {
				logger.debug("form is currently inActive**");
				msg += "\n given form is InActive";
			}
		}
		logger.debug("---------Exiting validateFormMapping-----------");
		return msg;
	}//validateFormMapping
	
	/**
	 * prepares the input map from the formMappings saved in db
	 * @param formMapStr
	 * @return
	 */
	public HashMap<String, List> prepareInputMap(String formMapStr){
		logger.debug("---------Entered prepareInputMap-----------");
		HashMap<String, List> inputMapSettingHM = null;
		if (formMapStr != null) {
			String[] tagMapStrArr = formMapStr.split("\\|");
			String[] tempStr = null;
			if (tagMapStrArr.length > 0) {
				inputMapSettingHM = new HashMap<String, List>();
				List list = null;
				for (int i = 0; i < tagMapStrArr.length; i++) {
					list = new ArrayList();
					tempStr = tagMapStrArr[i].trim().split("_:_");
					if (tempStr.length == 2) {
						list.add(tempStr[1]);
					} else if (tempStr.length == 3) {
						list.add(tempStr[1]);
						list.add(tempStr[2]);
					}
					inputMapSettingHM.put(tempStr[0], list);
				}
			}
		} 
		else {
			logger.error("Exception : Error occured while getting Input Field Mapping **");
		}
		logger.debug("---------Exiting prepareInputMap-----------");
		return inputMapSettingHM;
	}//prepareInputMap

	/**
	 * prepares the form values map from the data entered in the form
	 * @param reqObjFormValuesMap
	 * @param inputMapSettingHM
	 * @return
	 */
	public HashMap<String, String> prepareFormMapValues(HashMap<String, String> reqObjFormValuesMap, HashMap<String, List> inputMapSettingHM) {
		logger.debug("---------Entered prepareFormMapValues-----------");
		HashMap<String, String> formMapValuesHM = new HashMap<String, String>();
		Set<String> inputKeysSet = inputMapSettingHM.keySet();
		for (String inputFieldStr : inputKeysSet) {
			inputFieldStr = inputFieldStr.trim();
			logger.debug("input is::" + inputFieldStr + " AND "	+ reqObjFormValuesMap.get(inputFieldStr));
			if (reqObjFormValuesMap.get(inputFieldStr) == null || reqObjFormValuesMap.get(inputFieldStr).length() == 0) {
				continue;
			}
			String paramValuesStr = reqObjFormValuesMap.get(inputFieldStr);
			formMapValuesHM.put(inputFieldStr,	paramValuesStr);
		}
		logger.debug("---------Exiting prepareFormMapValues-----------");
		return formMapValuesHM;
	}//prepareFormMapValues

	/**
	 * gets the card number and card pin values from the form values map
	 * @param formMapValuesHM
	 * @param inputMapSettingHM
	 * @return
	 */
	public Map<String,String> getCardNumberAndPin(HashMap<String, String> formMapValuesHM, HashMap<String, List> inputMapSettingHM){
		logger.debug("---------Entered getCardNumberAndPin-----------");
		Set<String> valueKeys = formMapValuesHM.keySet();
		String methodNameStr = "";
		String cardNumber = "";
		String cardPin = "";
		for (String vkey : valueKeys) {
			String fieldVal = formMapValuesHM.get(vkey);
			if (!inputMapSettingHM.containsKey(vkey)) {
				logger.debug("Input Field :" + vkey + " Not found");
				continue;
			} // if
			List tempList = inputMapSettingHM.get(vkey);
			if (tempList.size() == 2) {
				continue;
			}
			methodNameStr = tempList.get(0).toString();
			if (methodNameStr.equalsIgnoreCase("CardNumber")) {
				cardNumber = fieldVal.trim();
				continue;
			} else if (methodNameStr.equalsIgnoreCase("CardPin")) {
				cardPin = fieldVal.trim();
				continue;
			}
		}// for
		Map<String,String> cardNumAndPin = new HashMap<String, String>();
		cardNumAndPin.put("cardNumber", cardNumber);
		cardNumAndPin.put("cardPin",cardPin);
		logger.debug("---------Exiting getCardNumberAndPin-----------");
		return cardNumAndPin;
	}//getCardNumberAndPin
	
	public boolean checkMobileOptin(HashMap<String, String> formMapValuesHM, HashMap<String, List> inputMapSettingHM){
		logger.debug("---------Entered checkMobileOptin-----------");
		Set<String> valueKeys = formMapValuesHM.keySet();
		String methodNameStr = "";
		boolean optIn = false;
		for (String vkey : valueKeys) {
			String fieldVal = formMapValuesHM.get(vkey);
			if (!inputMapSettingHM.containsKey(vkey)) {
				logger.debug("Input Field :" + vkey + " Not found");
				continue;
			} // if
			List tempList = inputMapSettingHM.get(vkey);
			methodNameStr = tempList.get(0).toString();
			logger.info("fieldVal : "+fieldVal);
			logger.info("methodNameStr : "+methodNameStr);
			if (fieldVal.equalsIgnoreCase("true") && methodNameStr.equalsIgnoreCase("Loyalty_mobile_optin")) {
				optIn=true;
				continue;
			}
		}// for
		logger.debug("---------Exiting getMobileoptin-----------");
		logger.info("optIn : "+optIn);
		return optIn;
	}//getMobileoptin
	
	/**
	 * create a new input contact object
	 * @param user
	 * @param mlBits
	 * @return
	 */
	public Contacts prepareInputContactObj(Users user,long mlBits) {
		logger.debug("---------Entered prepareInputContactObj-----------");
		Contacts contact = new Contacts();
		contact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
		contact.setMlBits(mlBits);
		contact.setUsers(user);
		contact.setPurged(false);
		contact.setOptin(Byte.valueOf((byte)0));
		contact.setCreatedDate(Calendar.getInstance());
		contact.setModifiedDate(Calendar.getInstance());
		logger.debug("---------Exiting prepareInputContactObj-----------");
		return contact;
	}//prepareInputContactObj

	/**
	 * set data from form to input contact
	 * @param formMapValuesHM
	 * @param inputMapSettingHM
	 * @param user
	 * @param isSbFlag
	 * @param formMapping
	 * @param contact
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> setInputConatctValue(HashMap<String, String> formMapValuesHM, HashMap<String, List> inputMapSettingHM, 
			Users user, boolean isSbFlag, FormMapping formMapping, Contacts contact) throws Exception {
		logger.debug("---------Entered setInputConatctValue-----------");
		Set<String> valueKeys = formMapValuesHM.keySet();
		boolean isCFDataSet = false;
		Method tempMethod = null;
		String msg = "";
		String methodNameStr ="";
		String birthDayYear = null;
		String birthDayMonth = null;
		String birthDayDay = null;
		String parentEmail = null;
		String storeLocation = null;
		String invoiceNumber = null;
		String invoiceAmount = null;
		String loyaltyPoints = null;
		String loyaltyCurrency = null;
		String enroll = null;

		Class strArg[] = new Class[] { String.class };
		Class longArg[] = new Class[] { Long.class };
		Class calArg[] = new Class[] { Calendar.class };
		Class intArg[] = new Class[] { Integer.TYPE };

		CustomFieldData customFieldData = new CustomFieldData();

		for (String vkey : valueKeys) {
			String fieldVal = formMapValuesHM.get(vkey);
			fieldVal = fieldVal.trim();
			if (!inputMapSettingHM.containsKey(vkey)) {
				logger.debug("Input Field :" + vkey + " Not found");
				continue;
			} // if

			List tempList = inputMapSettingHM.get(vkey);

			methodNameStr = tempList.get(0).toString();
			String dateFormat = null;
			if (tempList.size() == 2) {
				dateFormat = tempList.get(1).toString();
			}
			methodNameStr = methodNameStr.replace(" ", "");
			Object[] params = null;
			if (methodNameStr.startsWith("CF_")) { // Invoking the Custom fields method
				if(!isSbFlag){
					String[] cfTokens = methodNameStr.split("_");
					String dataType = cfTokens[cfTokens.length - 2];
					tempMethod = CustomFieldData.class.getMethod("setCust" + cfTokens[cfTokens.length - 1],strArg);
					if (CustomFieldValidator.validate(fieldVal,
							dataType)) {
						params = new Object[] { fieldVal };
						tempMethod.invoke(customFieldData, params);
						isCFDataSet = true;
					}
				}
			}// without Loyality
			else {
				if (isSbFlag && (methodNameStr.equals("CardNumber")	|| methodNameStr.equals("CardPin")
						|| methodNameStr.equals("ParentEmail") || methodNameStr.equals("LoyaltyEnroll")
						|| methodNameStr.equals("StoreLocation"))
						|| methodNameStr.equals("Loyalty_mobile_optin")||(methodNameStr.equals("Rating")||methodNameStr.contains("Rating"))
						|| methodNameStr.equals("FeedBackMessage")||methodNameStr.equals("feedBackStore")
						|| methodNameStr.equals("customerNo")||methodNameStr.equals("DOCSID")) {
					continue;
				}
				else if (fieldVal.length() > 0 && methodNameStr.equals("Email") || methodNameStr.equals("EmailId")) {
					boolean isValidEmail = true;
					if (!isSbFlag) {
						if (Utility.validateEmail(fieldVal) == false) {
							logger.debug("Invalid Email id: " + fieldVal);
							msg += " \n email id is not valid.";
							isValidEmail = false;
							continue;
						}
					} else if (isSbFlag	&& fieldVal.trim().length() > 0) {
						if (Utility.validateWebFormEmail(fieldVal) == false) {
							logger.debug("Invalid Email id: " + fieldVal);
							msg += " \n email id is not valid.";
							isValidEmail = false;
							continue;
						}
					}
					if (isValidEmail) {
						/*String retVal = PurgeList.checkForValidDomainByEmailId(fieldVal);
						if (retVal != null) {
							msg += "\n " + retVal;
							logger.debug("Invalid Domain / not a mail server: "	+ fieldVal);
							msg += " \n Invalid Domain / not a mail server: ";
							continue;
						}*/
						tempMethod = Contacts.class.getMethod("setEmailId", strArg);
						params = new Object[] { fieldVal.trim() };
					}
				} else if (fieldVal.length() > 0 && (methodNameStr.equals("Pin") || methodNameStr.equals("ZIP"))) {
					tempMethod = Contacts.class.getMethod("setZip",strArg);
					try {
						params = new Object[] {fieldVal.trim()};
					} catch (Exception e) {
						continue;
					}
				} else if (fieldVal.length() > 0 && (methodNameStr.equals("Phone") || methodNameStr.equals("MobilePhone"))) {
					tempMethod = Contacts.class.getMethod("setMobilePhone", strArg);
					try {
						UserOrganization organization=  user!=null ? user.getUserOrganization() : null ;
						String 	phone = Utility.phoneParse(fieldVal.trim(),organization);
						if(phone != null) {
							params = new Object[] {phone};
							contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
						}else {
							params = new Object[] {phone};
							contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
						}
					} catch (Exception e) {
						continue;
					}
				} else if (methodNameStr.equals("BirthDay") || methodNameStr.equals("Anniversary")) {
					tempMethod = Contacts.class.getMethod("set"	+ methodNameStr, calArg);
					Calendar cal = stringToCalBday(fieldVal, dateFormat);
					params = new Object[] { cal };
				} else if (methodNameStr.equals("BirthDay_Year")) {
					birthDayYear = fieldVal.trim();
					continue;
				} else if (methodNameStr.equals("BirthDay_Month")) {
					birthDayMonth = fieldVal.trim();
					continue;
				} else if (methodNameStr.equals("BirthDay_Day")) {
					birthDayDay = fieldVal.trim();
					continue;
				} else if (methodNameStr.equals("ParentEmail")) {
					parentEmail = fieldVal.trim();
					continue;
				} else if (methodNameStr.equals("LoyaltyEnroll")) {
					enroll = fieldVal.trim();
					continue;
				} else if (methodNameStr.equals("StoreLocation")) {
					storeLocation = fieldVal.trim();
					continue;
				}else if (methodNameStr.equals("invoiceNumber")) {
					invoiceNumber = fieldVal.trim();
					continue;
				}else if (methodNameStr.equals("invoiceAmount")) {
					invoiceAmount = fieldVal.trim();
					continue;
				}else if (methodNameStr.equals("loyaltyPoints")) {
					loyaltyPoints = fieldVal.trim();
					continue;
				}else if (methodNameStr.equals("loyaltyCurrency")) {
					loyaltyCurrency = fieldVal.trim();
					continue;
				}else if (methodNameStr.startsWith("UDF")) {
					if(dateFormat != null) {
						
						DateFormat formatter ; 
						Date udfdDate ; 
						formatter = new SimpleDateFormat(dateFormat);
						udfdDate = (Date)formatter.parse(fieldVal.trim()); 
						Calendar udfCal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
						udfCal.setTime(udfdDate);
						fieldVal = MyCalendar.calendarToString(udfCal, MyCalendar.dateFormatMap.get(dateFormat));
					}
					String tempStr = "Udf"+ methodNameStr.substring("UDF".length());
					tempMethod = Contacts.class.getMethod("set"	+ tempStr, strArg);
					params = new Object[] { fieldVal.trim() };
				} else {
					tempMethod = Contacts.class.getMethod("set"+ methodNameStr, strArg);
					params = new Object[] { fieldVal.trim() };
				}
				tempMethod.invoke(contact, params);
			} // else
		} // for vkey
		Calendar cal = setBday(birthDayDay, birthDayMonth, birthDayYear);
		if(cal != null) contact.setBirthDay(cal);
		logger.debug("contact obj "+ contact.getLastName());
		Map<String, Object> returnValues = new HashMap<String, Object>();
		returnValues.put("contact", contact);
		returnValues.put("msg", msg);
		if(!isSbFlag){
			returnValues.put("isCFDataSet", isCFDataSet);
			returnValues.put("parentEmail", parentEmail);
			returnValues.put("storeLocation", storeLocation);
			returnValues.put("enroll", enroll);
			returnValues.put("loyaltyPoints", loyaltyPoints);
			returnValues.put("loyaltyCurrency", loyaltyCurrency);
			returnValues.put("invoiceAmount", invoiceAmount);
			returnValues.put("invoiceNumber", invoiceNumber);
		}
		logger.debug("---------Exiting setInputConatctValue-----------");
		return returnValues;
	}//setInputConatctValue

	/**
	 * prepares the bday calendar obj using day, month and year
	 * @param day
	 * @param mon
	 * @param year
	 * @return
	 */
	public Calendar setBday(String day, String mon, String year) {
		Calendar cal = null;
		if (day != null && mon != null	&& year != null) {
			String totalCal = mon + "-" + day + "-"+ year;
			try {
				DateFormat formatter;
				formatter = new SimpleDateFormat("MM-dd-yyyy");
				Date date = (Date) formatter.parse(totalCal);
				cal = Calendar.getInstance();
				cal.setTime(date);
			} catch (ParseException e) {
				logger.error("error while parsing the date value :: "+ totalCal);
			}
		}
		return cal;
	}//setBday

	/**
	 * prepares the calendar obj from the bday string
	 * @param dob
	 * @param dateFormat
	 * @return
	 */
	public Calendar stringToCalBday(String dob, String dateFormat) {
		Calendar cal = null;
		if (dob != null) {
			try {
				DateFormat formatter;
				if (dateFormat != null) {
					formatter = new SimpleDateFormat(dateFormat);
				}
				else {
					formatter = new SimpleDateFormat("MM-dd-yyyy");
				}
				Date date = (Date) formatter.parse(dob);
				cal = Calendar.getInstance();
				cal.setTime(date);
				if(dateFormat.equals("dd/MM")) {
					cal.set(Calendar.YEAR, 0001);
				}
			} catch (ParseException e) {
				logger.error("error while parsing the date value :: "+ dob + "format should be " + dateFormat);
			}
		}
		return cal;
	}//stringToCalBday

	/**
	 * to check if contact exists in db or not acc to the priority
	 * @param userId
	 * @param contact
	 * @return
	 * @throws Exception
	 */
	public Contacts getDbContactWithPrority(Users user, Contacts contact) throws Exception {
		logger.debug("---------Entered getDbContactWithPrority-----------");
		POSMappingDao posMappingDao = (POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(user.getUserId(),Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
		ContactsDao contactsDao =  (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		Contacts dbContact = contactsDao.findContactByUniqPriority(priorMap, contact, user.getUserId(), user);
		logger.debug("---------Exiting getDbContactWithPrority-----------");
		return dbContact;
	}//getDbContactWithPrority

	/**
	 * process the existing db contact in case of loyalty card verification
	 * @param inputContact
	 * @param ltyContact
	 * @param formMapping
	 * @param posList
	 * @param contLtyUser
	 * @param purgeList
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> processExistingContactForLoyalty(Contacts inputContact, Contacts ltyContact, FormMapping formMapping, 
			MailingList posList, Users contLtyUser, PurgeList purgeList) throws Exception {
		logger.debug("---------Entered processExistingContactForLoyalty-----------");
		ServiceLocator locator = ServiceLocator.getInstance();
		MailingListDaoForDML mailingListDao  = (MailingListDaoForDML) locator.getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
//		String dbErrmsg="";
		String emailStatus = ltyContact.getEmailStatus();
		boolean emailFlag = ltyContact.getPurged();
		boolean purgeFlag = false;
		long contactBit = ltyContact.getMlBits().longValue();
		if(inputContact.getEmailId() == null && ltyContact.getEmailId() != null && !ltyContact.getEmailId().isEmpty()){
			UnsubscribesDao unsubscribesDao = (UnsubscribesDao) locator.getDAOByName(OCConstants.UNSUBSCRIBE_DAO);
			Unsubscribes unsubscribeObj = unsubscribesDao.findByUserId(contLtyUser.getUserId(),inputContact.getEmailId());
			// Send Resubscription Link To the Contact for Updating the contact status
			if (unsubscribeObj != null) {
				sendResubscriptionLinkToUser(inputContact.getEmailId(), contLtyUser);
//				dbErrmsg += " \n resubscribing is required";
			}
		}
		/*if((ltyContact.getEmailId() != null && inputContact.getEmailId() != null &&	!ltyContact.getEmailId().equalsIgnoreCase(
				inputContact.getEmailId())) || (ltyContact.getEmailId() == null && inputContact.getEmailId() != null) ) {*/
		if(inputContact.getEmailId() != null) {
			emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
			emailFlag = false;
			purgeFlag = true;
			UnsubscribesDao unsubscribesDao = (UnsubscribesDao) locator.getDAOByName(OCConstants.UNSUBSCRIBE_DAO);
			Unsubscribes unsubscribeObj = unsubscribesDao.findByUserId(contLtyUser.getUserId(),inputContact.getEmailId());
			// Send Resubscription Link To the Contact for Updating the contact status
			if (unsubscribeObj != null) {
				sendResubscriptionLinkToUser(inputContact.getEmailId(), contLtyUser);
//				dbErrmsg += " \n resubscribing is required";
				emailStatus = Constants.CONT_STATUS_UNSUBSCRIBED;
				emailFlag = true;
				purgeFlag = false;
			}
		}
		if( contactBit == 0l ) {//deleted contact ,need to be triggered action
			ltyContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMapping.getFormMappingName());
			ltyContact.setMlBits(posList.getMlBit());
			posList.setListSize(posList.getListSize() + 1);
			posList.setLastModifiedDate(Calendar.getInstance());
			mailingListDao.saveOrUpdate(posList);
			emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
			emailFlag = false;
			purgeFlag = true;
		}
		//perform mobile optin
		if(inputContact.getMobilePhone()!= null ) {
			//ignoring mobile optin for webform enrolled contacts
			if(formMapping.getInputFieldMapping().contains("Loyalty_mobile_optin")){
				ltyContact.setMobileOptin(true);
				ltyContact.setMobileOptinDate(now);
				ltyContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
				ltyContact.setMobileOptinSource(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM);
			}
			else if(contLtyUser.isEnableSMS() && contLtyUser.isConsiderSMSSettings()) performMobileOptIn(ltyContact, inputContact.getMobilePhone());
			else ltyContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
		}
		ltyContact = Utility.mergeContacts(inputContact, ltyContact);
		if(ltyContact.getMobilePhone() == null) {
			ltyContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
			ltyContact.setMobileOptin(false);
			ltyContact.setLastSMSDate(null);
		}
		ltyContact.setEmailStatus(emailStatus);
		ltyContact.setPurged(emailFlag);
		if(purgeFlag) {
			purgeList.checkForValidDomainByEmailIdByForm(ltyContact, formMapping, posList);
		}
		ContactsDaoForDML contactsDaoDml =  (ContactsDaoForDML) locator.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		contactsDaoDml.saveOrUpdate(ltyContact);
		Map<String,Object> retVal = new HashMap<String, Object>();
		retVal.put("ltyContact",ltyContact );
//		retVal.put("dbErrmsg", dbErrmsg);
		logger.debug("---------Exiting processExistingContactForLoyalty-----------");
		return retVal;
	}//processExistingContactForLoyalty

	/**
	 * process a new contat in case of loyalty card verification
	 * @param inputContact
	 * @param posList
	 * @param contLtyUser
	 * @param purgeList
	 * @param formMapping
	 * @return
	 */
	public Map<String,Object> processNewContactForLoyalty(Contacts inputContact,MailingList posList, Users contLtyUser, PurgeList purgeList,
			FormMapping formMapping) {
		logger.debug("---------Entered processNewContactForLoyalty-----------");
		String msg = "";
		ServiceLocator locator = ServiceLocator.getInstance();
		try {
			EventTriggerEventsObservable eventTriggerEventsObservable =  (EventTriggerEventsObservable) locator.getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) locator.getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			//register the observable with the observer
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			inputContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMapping.getFormMappingName());
			//inputContact = setmobilePhoneStatus( inputContact,  contLtyUser);
			inputContact = setmobilePhoneStatus( inputContact,  contLtyUser,formMapping,false);
			purgeList.checkForValidDomainByEmailIdByForm(inputContact, formMapping, posList);
			ContactsDaoForDML contactsDaoForDML =  (ContactsDaoForDML) locator.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			contactsDaoForDML.saveOrUpdate(inputContact);
			//List LastModified Date
			posList.setLastModifiedDate(Calendar.getInstance());
			MailingListDaoForDML  mailingListDao = (MailingListDaoForDML) locator.getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
			
			posList.setListSize(posList.getListSize() + 1);
			
			mailingListDao.saveOrUpdate(posList);
		} catch (DataIntegrityViolationException die) {
			logger.error(inputContact.getEmailId() + " : Email already exists :" + die + " **");
			msg += "\n Email already exists";
		} catch (Exception e) {
			logger.error(" **  Exception :" ,e);
			msg += "\n Exception";
		}
		Map<String, Object> retVal = new HashMap<String, Object>();
		retVal.put("ltyContact", inputContact);
		retVal.put("msg", msg);
		logger.debug("---------Exiting processNewContactForLoyalty-----------");
		return retVal;
	}//processNewContactForLoyalty

	/**
	 * checks if a minor contact is to be created and creates it if required
	 * @param formMapping
	 * @param user
	 * @param contact
	 * @param mailingList
	 * @param parentEmail
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object>  createMinorContact(FormMapping formMapping,Users user, Contacts contact, MailingList mailingList, String parentEmail) throws Exception {
		logger.debug("---------Entered createMinorContact-----------");
		boolean isMinor = false;
		boolean isEnableEvent = false;
		String parentalSucessMsg = "";
		ContactsDao contactsDao =  (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		if (contact.getBirthDay() != null) {
			isMinor = ((Calendar.getInstance().getTimeInMillis() - contact
					.getBirthDay().getTimeInMillis()) / (1000 * 60 * 60 * 24)) <= (365 * 13);
			if(isMinor == true ) {//under age
				Contacts underAgeCon = underAgeContactCreation(contact.getFirstName(), contact.getBirthDay(), user, mailingList, formMapping.getFormMappingName());
				isEnableEvent = true;
				contactsDaoForDML.saveOrUpdate(underAgeCon);
				parentalSucessMsg = "Parental Consent is required.";
				if( parentEmail != null) {
					performParentalConsent(underAgeCon,	mailingList, parentEmail, user,formMapping);
				}
			}
		}
		Map<String, Object> returnVal = new HashMap<String, Object>();
		returnVal.put("isMinor", isMinor);
		returnVal.put("isEnableEvent", isEnableEvent);
		returnVal.put("parentalSucessMsg", parentalSucessMsg);
		logger.debug("---------Exiting createMinorContact-----------");
		return returnVal;
	}//createMinorContact

	/**
	 * creates the under age contact
	 * @param firstName
	 * @param bday
	 * @param user
	 * @param mailingList
	 * @param formMappingName
	 * @return
	 * @throws Exception 
	 */
	public Contacts underAgeContactCreation(String firstName, Calendar bday, Users user, MailingList mailingList, String formMappingName) throws Exception {
		logger.debug("---------Entered underAgeContactCreation-----------");
		Contacts underAgeCon = new Contacts();
		underAgeCon.setFirstName(firstName);
		underAgeCon.setBirthDay(bday);
		underAgeCon.setMlBits(mailingList.getMlBit());
		underAgeCon.setUsers(user);
		underAgeCon.setEmailStatus(Constants.CONT_STATUS_PARENTAL_PENDING);
		underAgeCon.setPurged(false);
		underAgeCon.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMappingName);
		underAgeCon.setOptin(Byte.valueOf((byte)0));
		underAgeCon.setCreatedDate(Calendar.getInstance());
		underAgeCon.setModifiedDate(Calendar.getInstance());
		underAgeCon.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
		
		mailingList.setListSize(mailingList.getListSize() + 1);
		mailingList.setLastModifiedDate(Calendar.getInstance());
		MailingListDao mailingListDao  = (MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
		MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("mailingListDaoForDML");
		mailingListDaoForDML.saveOrUpdate(mailingList);
		logger.debug("---------Exiting underAgeContactCreation-----------");
		return underAgeCon;
	}//underAgeContactCreation

	/**
	 * perform parental consent for minor contacts
	 * @param tempContact
	 * @param mailingList
	 * @param parentEmail
	 * @param user
	 * @param formMapping
	 * @throws Exception
	 */
	public void performParentalConsent(Contacts tempContact, MailingList mailingList, String parentEmail,
			Users user, FormMapping formMapping) throws Exception {
		logger.debug("---------Entered performParentalConsent-----------");
		logger.debug("**********Parental Concent***************");
		ServiceLocator locator = ServiceLocator.getInstance();
		CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao) locator.getDAOByName(OCConstants.CUSTOM_TEMP_DAO);
		EmailQueueDao emailQueueDao = (EmailQueueDao) locator.getDAOByName(OCConstants.EMAILQUEUE_DAO);
		EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) locator.getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
		ContactParentalConsentDao contactParentalConsentDao = (ContactParentalConsentDao) locator.getDAOByName(OCConstants.CONTACT_PARENTAL_CONSENT_DAO);
		ContactParentalConsentDaoForDML contactParentalConsentDaoForDML = (ContactParentalConsentDaoForDML) locator.getDAOForDMLByName(OCConstants.CONTACT_PARENTAL_CONSENT_DAOForDML);
		MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
		ContactParentalConsent contactConsent;
		if (((Calendar.getInstance().getTimeInMillis() - tempContact.getBirthDay().getTimeInMillis()) / (1000 * 60 * 60 * 24)) <= (365 * 13)) {
			contactConsent = contactParentalConsentDao.findByContactId(tempContact.getContactId());
			if (contactConsent != null) {
				logger.debug("email has already been sent to it parent ::"	+ contactConsent.getEmail());
				return;
			}
			contactConsent = new ContactParentalConsent( tempContact.getContactId(), parentEmail, Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL,
					user.getUserId(), Calendar.getInstance(), tempContact.getEmailId());
			contactConsent.setChildFirstName(tempContact.getFirstName());
			contactConsent.setChildDOB(tempContact.getBirthDay());
			//contactParentalConsentDao.saveOrUpdate(contactConsent);
			contactParentalConsentDaoForDML.saveOrUpdate(contactConsent);
			
			CustomTemplates custTemplate = null;
			String message = PropertyUtil.getPropertyValueFromDB(OCConstants.PARENTAL_CONSENT_MAIL_TEMPLATE);
			if (formMapping.getConsentCustTemplateId() != null) {
				custTemplate = customTemplatesDao.findCustTemplateById(formMapping.getConsentCustTemplateId());
				if (custTemplate != null) {

					if(custTemplate != null && custTemplate.getHtmlText()!= null) {
						  message = custTemplate.getHtmlText();
					  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
						  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
						  if(myTemplates != null) message = myTemplates.getContent();
					  }
				}
			}
			EmailQueue testEmailQueue = new EmailQueue(formMapping.getConsentCustTemplateId(), Constants.EQ_TYPE_TEST_PARENTAL_MAIL, message,
					OCConstants.EMAIL_STATUS_ACTIVE, parentEmail, user, MyCalendar.getNewCalendar(), OCConstants.EMAIL_SUBJECT_REQUIRE_PARENTAL_CONSENT, tempContact.getEmailId(),
					tempContact.getFirstName(), MyCalendar.calendarToString(tempContact.getBirthDay(), MyCalendar.FORMAT_DATEONLY), tempContact.getContactId());
			logger.info("testEmailQueue" + testEmailQueue.getChildEmail());
			//emailQueueDao.saveOrUpdate(testEmailQueue);
			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
		}// if
		logger.debug("---------Exiting performParentalConsent-----------");
	}//performParentalConsent

	/**
	 * process existing db contact in case of sign up form type
	 * @param dbContact
	 * @param inputContact
	 * @param mailingList
	 * @param user
	 * @param purgeList
	 * @param formMapping
	 * @param isEnableEvent
	 * @param enroll
	 * @param storeLocation
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> processExistingContact(Contacts dbContact, Contacts inputContact, MailingList mailingList, Users user, 
			PurgeList purgeList, FormMapping formMapping, boolean isEnableEvent,String enroll, String storeLocation,boolean isOptinChecked) throws Exception {
		logger.debug("---------Entered processExistingContact-----------");
		String 	dbErrmsg = "\n contact with given Email id already exist.";
		String msg ="";
		ServiceLocator locator = ServiceLocator.getInstance();
		EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) locator.getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
		EventTriggerEventsObservable eventTriggerEventsObservable =  (EventTriggerEventsObservable) locator.getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
		ContactsDaoForDML contactsDaoForDML =  (ContactsDaoForDML) locator.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		//register the observable with the observer
		eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
		boolean isUnsubContact = false;
		boolean isDeletedCon = false;
		
		if(dbContact.getOptinMedium() == null ) {//if it is deleted contact
			isEnableEvent = true;
			inputContact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMapping.getFormMappingName());
		}
		if(formMapping.isSendEmailToExistingContact()) {//referral program
			//dbContact = updateContact( dbContact,  inputContact, mailingList, user, purgeList, formMapping);
			dbContact = updateContact( dbContact,  inputContact, mailingList, user, purgeList, formMapping,isOptinChecked);
		}//if
		else{
			//Map<String, Object> contactAndDelFlag = mergeContact(dbContact, inputContact, mailingList, formMapping, user, purgeList,dbErrmsg);
			Map<String, Object> contactAndDelFlag = mergeContact(dbContact, inputContact, mailingList, formMapping, user, purgeList,dbErrmsg,isOptinChecked);
			dbContact = (Contacts) contactAndDelFlag.get("dbContact");
			isDeletedCon = (Boolean) contactAndDelFlag.get("isDeletedCon");
			isUnsubContact = (Boolean) contactAndDelFlag.get("isUnsubContact");
			dbErrmsg = (String) contactAndDelFlag.get("dbErrmsg");
		}//else
		contactsDaoForDML.saveOrUpdate(dbContact);
		LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(dbContact, dbContact.getMobilePhone());
		LoyaltyProgramHelper.updateLoyaltyEmailId(dbContact, dbContact.getEmailId());
		if(Constants.WEBFORM_TYPE_LOYALTY_SIGNUP.equalsIgnoreCase(formMapping.getFormType())){
			List<Contacts> dbContactList = findOCContactList(inputContact, user.getUserId(),user);
			msg += performOCLoyaltyEnrollment(enroll, dbContact.getHomeStore(), mailingList, dbContact, user,formMapping,dbContactList,null,null,null,null);
		}
		
		//added this method for updating mobile status if contact is updated.
		dbContact = setmobilePhoneStatus( dbContact,user,formMapping,isOptinChecked);
	if((Utility.isModifiedContact(dbContact,inputContact))  )
		{
		//String db=dbContact+"";
		//if(db.length()>1 )
			logger.info("entered Modified date");
			dbContact.setModifiedDate(Calendar.getInstance());
		}	
		contactsDaoForDML.saveOrUpdate(dbContact);
		//added for referral program(case: for existing )
		boolean isSent = false;
		if(mailingList.isCheckWelcomeMsg() && formMapping.isSendEmailToExistingContact()) {
			isSent = true;
			sendWelcomeEmail(dbContact, mailingList.getWelcomeCustTempId(), user);
		}//if
		if (!isUnsubContact && isDeletedCon && !isSent) {//added to allow send a welcome mail to the deleted contact
			if (mailingList.isCheckWelcomeMsg() && !mailingList.getCheckDoubleOptin()) {
				sendWelcomeEmail(dbContact, mailingList.getWelcomeCustTempId(), user);
			}
		} // if
		//for deleted contact opt-in medium resetting fire an event-notify to observer
		if(isEnableEvent) {
			eventTriggerEventsObservable.notifyForWebEvents(user.getUserId().longValue(),
					dbContact.getContactId().longValue(), dbContact.getContactId().longValue() );
		}//if
		Map<String, Object> messageRetVal = new HashMap<String, Object>();
		messageRetVal.put("dbErrmsg", dbErrmsg);
		messageRetVal.put("msg", msg);
		logger.debug("---------Exiting processExistingContact-----------");
		return messageRetVal;
	}//processExistingContact
	
	private List<Contacts> findOCContactList(Contacts inputContact, Long userId,Users user) throws Exception {
		//logger.info("Entered findOCContact method >>>>");
		POSMappingDao posMappingDao = (POSMappingDao)ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		TreeMap<String, List<String>> priorMap =  Utility.getPriorityMap(userId, Constants.POS_MAPPING_TYPE_CONTACTS, posMappingDao);
		List<Contacts> dbContactList = contactsDao.findMatchedContactListByUniqPriority(priorMap, inputContact, userId,user);
		//logger.info("Exited findOCContact method >>>>");
		return dbContactList;
	}

	/**
	 * update contact as required in case of referral prog
	 * @param dbContact
	 * @param inputContact
	 * @param mailingList
	 * @param user
	 * @param purgeList
	 * @param formMapping
	 * @return
	 * @throws Exception
	 */
	public Contacts updateContact(Contacts dbContact, Contacts inputContact, MailingList mailingList, Users user, 
			PurgeList purgeList, FormMapping formMapping,boolean isOptinChecked) throws Exception {
		logger.debug("---------Entered updateContact-----------");
		//update with new data (for referral programs this is required )
		ServiceLocator locator = ServiceLocator.getInstance();
		MailingListDao mailingListDao  = (MailingListDao) locator.getDAOByName(OCConstants.MAILINGLIST_DAO);
		String emailStatus = dbContact.getEmailStatus();
		boolean emailFlag = dbContact.getPurged();
		boolean purgeFlag = false;
		long contactBit = dbContact.getMlBits().longValue();
		if((dbContact.getEmailId() != null && inputContact.getEmailId() != null &&	! dbContact.getEmailId().equalsIgnoreCase(
				inputContact.getEmailId())) || (dbContact.getEmailId() == null && inputContact.getEmailId() != null) ) {
			emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
			emailFlag = false;
			purgeFlag = true;
		}
		//if( mlSet.size() == 0 ){
		if( contactBit == 0l ) {//deleted contact ,need to be triggered action
			dbContact.setMlBits(mailingList.getMlBit());
			mailingList.setListSize(mailingList.getListSize() + 1);
			mailingList.setLastModifiedDate(Calendar.getInstance());
			MailingListDaoForDML mailingListDaoForDML = (MailingListDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("mailingListDaoForDML");
			mailingListDaoForDML.saveOrUpdate(mailingList);
			emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
			emailFlag = false;
			purgeFlag = true;
		}
		//perform mobile optin
		if(inputContact.getMobilePhone()!= null ) {
			
			//ignoring mobile optin for webform enrolled contacts
			if(formMapping.getInputFieldMapping().contains("Loyalty_mobile_optin") && isOptinChecked){
				dbContact.setMobileOptin(true);
				dbContact.setMobileOptinDate(now);
				dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
				dbContact.setMobileOptinSource(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM);
			}
			else if(user.isEnableSMS() && user.isConsiderSMSSettings()) performMobileOptIn(dbContact, inputContact.getMobilePhone());
			else dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
		}
		dbContact = Utility.mergeContacts(inputContact, dbContact);
		if(dbContact.getMobilePhone() == null) {
			dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
			dbContact.setMobileOptin(false);
			dbContact.setLastSMSDate(null);
		}
		dbContact.setEmailStatus(emailStatus);
		dbContact.setPurged(emailFlag);
		if(purgeFlag) {
			purgeList.checkForValidDomainByEmailIdByForm(dbContact, formMapping, mailingList);
		}
		logger.debug("---------Exiting updateContact-----------");
		return dbContact;
	}//updateContact

	/**
	 * merge contacts as required in case of non referral prog
	 * @param dbContact
	 * @param inputContact
	 * @param mailingList
	 * @param formMapping
	 * @param user
	 * @param purgeList
	 * @param dbErrmsg
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> mergeContact(Contacts dbContact, Contacts inputContact, MailingList mailingList,
			FormMapping formMapping, Users user, PurgeList purgeList, String dbErrmsg, boolean isOptinChecked) throws Exception {
		logger.debug("---------Entered mergeContact-----------");
		ServiceLocator locator = ServiceLocator.getInstance();
		MailingListDaoForDML mailingListDao = (MailingListDaoForDML) locator.getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
		boolean isUnsubContact = false;
		boolean isDeletedCon = false;
		String emailStatus = dbContact.getEmailStatus();
		boolean emailFlag = dbContact.getPurged();
		boolean purgeFlag = false;
		if(dbContact.getEmailId() != null && dbContact.getEmailId().length() > 0){
			UnsubscribesDao unsubscribesDao = (UnsubscribesDao) locator.getDAOByName(OCConstants.UNSUBSCRIBE_DAO);
			Unsubscribes unsubscribeObj = unsubscribesDao.findByUserId(user.getUserId(),dbContact.getEmailId());
			// Send Resubscription Link To the Contact for Updating the contact status
			if (unsubscribeObj != null) {
				sendResubscriptionLinkToUser(dbContact.getEmailId(), user);
				dbErrmsg += " \n resubscribing is required";
				isUnsubContact = true;
			}
		}
		if((dbContact.getEmailId() == null || dbContact.getEmailId().isEmpty()) && inputContact.getEmailId() != null && !inputContact.getEmailId().isEmpty()){
			emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
			emailFlag = false;
			purgeFlag = true;
			UnsubscribesDao unsubscribesDao = (UnsubscribesDao) locator.getDAOByName(OCConstants.UNSUBSCRIBE_DAO);
			Unsubscribes unsubscribeObj = unsubscribesDao.findByUserId(user.getUserId(),inputContact.getEmailId());
			// Send Resubscription Link To the Contact for Updating the contact status
			if (unsubscribeObj != null) {
				sendResubscriptionLinkToUser(inputContact.getEmailId(), user);
				dbErrmsg += " \n resubscribing is required";
				isUnsubContact = true;
				emailStatus = Constants.CONT_STATUS_UNSUBSCRIBED;
				emailFlag = true;
				purgeFlag = false;
			}
		}
		if(dbContact.getMlBits().longValue() == 0l) { //if it is deleted contact
			isDeletedCon = true;
			dbContact.setMlBits(mailingList.getMlBit());
			mailingList.setListSize(mailingList.getListSize() + 1);
			mailingList.setLastModifiedDate(Calendar.getInstance());
			mailingListDao.saveOrUpdate(mailingList);
			emailStatus = Constants.CONT_STATUS_PURGE_PENDING;
			emailFlag = false;
			purgeFlag = true;
		}
		else{
			if( (dbContact.getMlBits().longValue() & mailingList.getMlBit().longValue()) == 0 ) {
				dbContact.setMlBits(dbContact.getMlBits().longValue() | mailingList.getMlBit().longValue());
				mailingList.setListSize(mailingList.getListSize() + 1);
				mailingList.setLastModifiedDate(Calendar.getInstance());
				mailingListDao.saveOrUpdate(mailingList);
			}
		}
		if(dbContact.getMobilePhone() == null && inputContact.getMobilePhone() != null) {
			//ignoring mobile optin for webform enrolled contacts
			if(formMapping.getInputFieldMapping().contains("Loyalty_mobile_optin") && isOptinChecked){
				dbContact.setMobileOptin(true);
				dbContact.setMobileOptinDate(now);
				dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
				dbContact.setMobileOptinSource(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM);
			}
			else if(user.isEnableSMS() && user.isConsiderSMSSettings())dbContact.setMobileStatus(performMobileOptIn(dbContact, inputContact.getMobilePhone()));
			else dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
		}else{
			dbContact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
		}
		if(Constants.FEED_BACK_WEB_FORM.equalsIgnoreCase(formMapping.getFormType())) {
			dbContact = Utility.mergeContacts(inputContact, dbContact);
		}else {
			dbContact = Utility.mergeContactsFromWebForm(inputContact, dbContact);			
		}
		dbContact.setEmailStatus(emailStatus);
		dbContact.setPurged(emailFlag);
		if(purgeFlag) {
			purgeList.checkForValidDomainByEmailIdByForm(dbContact, formMapping, mailingList);
		}
		Map<String, Object> retVal = new HashMap<String, Object>();
		retVal.put("dbContact", dbContact);
		retVal.put("isDeletedCon", isDeletedCon);
		retVal.put("isUnsubContact", isUnsubContact);
		retVal.put("dbErrmsg", dbErrmsg);
		logger.debug("---------Exiting mergeContact-----------");
		return retVal;
	}//mergeContact

	/**
	 * process new contact in case of sign up form type
	 * @param contact
	 * @param mailingList
	 * @param user
	 * @param purgeList
	 * @param formMapping
	 * @param isEnableEvent
	 * @param enroll
	 * @param storeLocation
	 * @param isMinor
	 * @return
	 */
	public String processNewContact(Contacts contact, MailingList mailingList, Users user, PurgeList purgeList, 
			FormMapping formMapping, boolean isEnableEvent,String enroll, String storeLocation, String loyaltyPoints,String loyaltyCurrency, 
			String invoiceAmount, String invoiceNumber, boolean isMinor,boolean isOptinChecked) {
		logger.debug("---------Entered processNewContact-----------");
		String msg = "";
		ServiceLocator locator = ServiceLocator.getInstance();
		try {
			EventTriggerEventsObservable eventTriggerEventsObservable =  (EventTriggerEventsObservable) locator.getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) locator.getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			//register the observable with the observer
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			isEnableEvent = true;
			contact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMapping.getFormMappingName());
			//contact = setmobilePhoneStatus( contact,  user);
			contact = setmobilePhoneStatus( contact,  user,formMapping,isOptinChecked);
			purgeList.checkForValidDomainByEmailIdByForm(contact, formMapping, mailingList);
			ContactsDaoForDML contactsDaoForDML =  (ContactsDaoForDML) locator.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			if(Constants.FEED_BACK_WEB_FORM.equalsIgnoreCase(formMapping.getFormType())) {
				if((contact.getEmailId()!=null && !contact.getEmailId().isEmpty()) || (contact.getMobilePhone()!=null)) {
					contactsDaoForDML.saveOrUpdate(contact);
				}else {
					isEnableEvent = false;
					isMinor =true;
				}
			}else {
				contactsDaoForDML.saveOrUpdate(contact);
			}
			//List LastModified Date
			mailingList.setLastModifiedDate(Calendar.getInstance());
			MailingListDaoForDML  mailingListDao = (MailingListDaoForDML) locator.getDAOForDMLByName(OCConstants.MAILINGLIST_DAO_FOR_DML);
			
			mailingList.setListSize(mailingList.getListSize() + 1);
			
			mailingListDao.saveOrUpdate(mailingList);
			if (!isMinor) {
				// *****************LOYALTY ENROLLMENT********************************
				if(Constants.WEBFORM_TYPE_LOYALTY_SIGNUP.equalsIgnoreCase(formMapping.getFormType())){
					msg += performOCLoyaltyEnrollment(enroll, contact.getHomeStore(), mailingList, contact, user,formMapping,null,loyaltyPoints,loyaltyCurrency,invoiceAmount,invoiceNumber);
				}
				contactsDaoForDML.saveOrUpdate(contact);
				if (mailingList.isCheckWelcomeMsg() && !mailingList.getCheckDoubleOptin()) {
					sendWelcomeEmail(contact, mailingList.getWelcomeCustTempId(), user);
				}
			}// if
			//for a new contact opt-in medium setting fire an event-notify to observer
			if(isEnableEvent) {
				eventTriggerEventsObservable.notifyForWebEvents(user.getUserId().longValue(),
						contact.getContactId().longValue(), contact.getContactId().longValue() );
			}//if
		} catch (DataIntegrityViolationException die) {
			logger.error(contact.getEmailId() + " : Email already exists :" + die + " **");
			msg += "\n Email already exists";
			logger.error("DataIntegrityViolationException",die);
			return msg;
		} catch (Exception e) {
			logger.error(" **  Exception :" ,e);
			msg += "\n Exception";
			return msg;
		}
		logger.debug("---------Exiting processNewContact-----------");
		return msg;
	}//processNewContact

	/**
	 * perform loyalty enrollment in case of loyalty sign up form type
	 * @param enroll
	 * @param storeLocation
	 * @param mailingList
	 * @param tempContact
	 * @param user
	 * @param formMapping
	 * @param dbContactList 
	 * @param invoiceNumber 
	 * @param invoiceAmount 
	 * @param loyaltyPoints 
	 * @return
	 * @throws Exception
	 */
	private String performOCLoyaltyEnrollment(String enroll, String storeLocation, MailingList mailingList,
			Contacts tempContact, Users user, FormMapping formMapping, List<Contacts> dbContactList, String loyaltyPoints,String loyaltyCurrency,String invoiceAmount, String invoiceNumber) throws Exception {
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		logger.debug("---------Entered performOCLoyaltyEnrollment-----------");
		String msg = "";
		logger.debug("Contact has to be enrolled");
		if( enroll != null && !enroll.isEmpty() && !(enroll.equalsIgnoreCase("yes") 
				|| enroll.equalsIgnoreCase("y") || enroll.equalsIgnoreCase("true"))) return msg;
		LoyaltyProgram progObj = loyaltyProgramDao.findById(formMapping.getLoyaltyProgramId());
		if(progObj == null) {
			msg += "\n program not found.";
			return msg;
		}
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		if(dbContactList != null){
			String contIdStr = "";
			for (Contacts contacts : dbContactList) {
				if(contIdStr.isEmpty()){
					contIdStr = ""+contacts.getContactId();
				}
				else{
					contIdStr = contIdStr + Constants.DELIMETER_COMMA + contacts.getContactId();
				}
			}
			ContactsLoyalty contactLoyalty = contactsLoyaltyDao.findByContactIdStrAndPrgmId(user.getUserId(), contIdStr,progObj.getProgramId());
			if (contactLoyalty != null) {
				logger.debug("**********Contact is already enroled ***************"+ contactLoyalty.getCardNumber());
				return msg;
			}
		}
		LoyaltyTransactionParent tranParent = createNewTransaction();
		if(!OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE.equalsIgnoreCase(progObj.getStatus())) {
			sendFailureAlert(tempContact, formMapping, user, "Configured program is not active.");
			updateParentTransaction(tranParent,user,"Configured program is not active.",null);
			msg += "\n Configured program is not active.";
			return msg;
		}
		if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD.equalsIgnoreCase(progObj.getMembershipType())){
			LoyaltyCards loyaltyCard = null;
			LoyaltyCardSet cardSetObj = null;
			String cardSetIdStr = null;
			if(formMapping.getAutoSelectCard() == OCConstants.FLAG_YES) {
				LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
				List<LoyaltyCardSet> activeCardSets = loyaltyCardSetDao.findActiveByProgramId(progObj.getProgramId());
				if(activeCardSets == null){
					msg += "\n No active virtual card-sets found for the configured loyalty program.";
					sendFailureAlert(tempContact,formMapping,user,"No active virtual card-sets found for the configured loyalty program.");
					updateParentTransaction(tranParent,user,"No active virtual card-sets found for the configured loyalty program.",null);
					return msg;
				}

				for(LoyaltyCardSet cardSet : activeCardSets){
					if(cardSetIdStr == null){
						cardSetIdStr = ""+cardSet.getCardSetId();
					}
					else{
						cardSetIdStr += ","+cardSet.getCardSetId();
					}
				}
				LoyaltyCardFinder loyaltyCardFinder = (LoyaltyCardFinder) ServiceLocator.getInstance()
						.getBeanByName(OCConstants.LOYALTY_CARD_FINDER);

				if(!loyaltyCardFinder.letMeIn(user.getUserId())){//APP-1326
					boolean doRecursiveCall = true;
					logger.info("I am not allowed to get a card");
					do{
						doRecursiveCall = !loyaltyCardFinder.letMeIn(user.getUserId());
					}while(doRecursiveCall);
					
					loyaltyCard=loyaltyCardFinder.findInventoryCard(""+progObj.getProgramId(), cardSetIdStr, null, user.getUserId());	
					loyaltyCardFinder.MarkIamDone(user.getUserId());	
					
				}else {
					loyaltyCard=loyaltyCardFinder.findInventoryCard(""+progObj.getProgramId(), cardSetIdStr, null, user.getUserId());
					logger.info("I am done with my job"+Thread.currentThread().getName());
					loyaltyCardFinder.MarkIamDone(user.getUserId());
				}
				
				
				
//				loyaltyCard = LoyaltyCardFinder.findInventoryCard(""+progObj.getProgramId(), cardSetIdStr, null, user.getUserId());

				if(loyaltyCard == null){
					msg += "\n No inventory card found.";
					sendFailureAlert(tempContact, formMapping, user, "No inventory card found.");
					updateParentTransaction(tranParent,user,"No inventory card found.",null);
					return msg;
				}
				logger.info("card = "+loyaltyCard.getCardNumber()+" status = "+loyaltyCard.getStatus());
			}
			else if(formMapping.getAutoSelectCard() == OCConstants.FLAG_NO) {
				LoyaltyCardSetDao loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
				cardSetObj =  loyaltyCardSetDao.findByCardSetId(formMapping.getLoyaltyCardsetId());
				if(!OCConstants.LOYALTY_CARDSET_STATUS_ACTIVE.equalsIgnoreCase(cardSetObj.getStatus())){
					msg += "\n Configured card-set is not active.";
					sendFailureAlert(tempContact, formMapping, user, "Configured card-set is not active.");
					updateParentTransaction(tranParent,user,"Configured card-set is not active.",null);
					return msg;
				}
				cardSetIdStr = ""+formMapping.getLoyaltyCardsetId();

				LoyaltyCardFinder loyaltyCardFinder = (LoyaltyCardFinder) ServiceLocator.getInstance()
						.getBeanByName(OCConstants.LOYALTY_CARD_FINDER);

				if(!loyaltyCardFinder.letMeIn(user.getUserId())){//APP-1326
					boolean doRecursiveCall = true;
					logger.info("I am not allowed to get a card");
					do{
						doRecursiveCall = !loyaltyCardFinder.letMeIn(user.getUserId());
					}while(doRecursiveCall);
					
					loyaltyCard=loyaltyCardFinder.findInventoryCard(""+progObj.getProgramId(), cardSetIdStr, null, user.getUserId());	
					loyaltyCardFinder.MarkIamDone(user.getUserId());	
					
				}else {
					loyaltyCard=loyaltyCardFinder.findInventoryCard(""+progObj.getProgramId(), cardSetIdStr, null, user.getUserId());
					logger.info("I am done with my job"+Thread.currentThread().getName());
					logger.info("HOLA");
					loyaltyCardFinder.MarkIamDone(user.getUserId());
				}
				

				//loyaltyCard = LoyaltyCardFinder.findInventoryCard(""+progObj.getProgramId(), cardSetIdStr, null, user.getUserId());
				if(loyaltyCard == null){
					msg += "\n No inventory card found.";
					sendFailureAlert(tempContact, formMapping, user, "No inventory card found.");
					updateParentTransaction(tranParent,user,"No inventory card found.",null);
					return msg;
				}
				logger.info("card = "+loyaltyCard.getCardNumber()+" status = "+loyaltyCard.getStatus());
			}
			if(progObj.getUniqueMobileFlag() == 'Y'){//like this unique email check must be there no
				String mobilePhone = tempContact.getMobilePhone();
				if(mobilePhone!=null && mobilePhone.trim().length() !=0){
					mobilePhone = mobilePhone.trim();
					//UserOrganization organization=  user!=null ? user.getUserOrganization() : null ;
					//phone = phoneParse(phone, organization);
					if(mobilePhone != null && mobilePhone.startsWith(user.getCountryCarrier()+"")
							 && mobilePhone.length() >user.getUserOrganization().getMaxNumberOfDigits()) {
						mobilePhone =  mobilePhone.replaceFirst(user.getCountryCarrier()+"", "");
						//logger.info("phone is============>"+phone);
					}
					try {
						mobilePhone= Long.parseLong(mobilePhone)+"";
						
					} catch (Exception e) {
						logger.info("OOPs error ");
					}
					}
				String countryCarrier = user.getCountryCarrier()+"";
				String mobileWithCarrier=countryCarrier +mobilePhone;
				ContactsLoyalty existLtyContact = contactsLoyaltyDao.findByMobilePhone(mobilePhone,mobileWithCarrier, progObj.getProgramId(), user.getUserId());
				if(existLtyContact != null){
					msg += "\n Only single membership is allowed using a mobile number for the configured program."; 
					updateLoyaltyCardStatus(loyaltyCard,OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
					sendFailureAlert(tempContact, formMapping, user, "Only single membership is allowed using a mobile number for the configured program.");
					updateParentTransaction(tranParent,user,"Only single membership is allowed using a mobile number for the configured program.",null);
					return msg;
				}
			}
			if (progObj.getUniqueEmailFlag() != '\0' && progObj.getUniqueEmailFlag() == 'Y' && 
					tempContact.getEmailId()!=null && !tempContact.getEmailId().isEmpty()) {
				ContactsLoyalty existLtyContact = contactsLoyaltyDao.findMembershipByEmailInCl( tempContact.getEmailId(), user.getUserId(), progObj.getProgramId());
				if(existLtyContact != null){
					msg += "\n Only single membership is allowed using an email address for the configured program."; 
					updateLoyaltyCardStatus(loyaltyCard,OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
					sendFailureAlert(tempContact, formMapping, user, "Only single membership is allowed using an email address for the configured program.");
					updateParentTransaction(tranParent,user,"Only single membership is allowed using an email address for the configured program.",null);
					return msg;
				}
				
			}

			//loyalty threshold alerts...
			sendLoyaltyThresholdAlerts(cardSetIdStr,user,progObj,formMapping.getFormMappingName());
			
			LoyaltyProgramTier tier = findTier(progObj.getProgramId(), tempContact.getContactId(), user.getUserId());
			if(tier == null){
				msg += "\n Loyalty tier not found.";
				updateLoyaltyCardStatus(loyaltyCard,OCConstants.LOYALTY_CARD_STATUS_INVENTORY);
				sendFailureAlert(tempContact, formMapping, user, "Loyalty tier not found.");
				updateParentTransaction(tranParent,user,"Loyalty tier not found.",null);
				return msg;
			}
			ContactsLoyalty contactsLoyalty = createMembership(invoiceAmount,progObj, loyaltyCard, tier, tempContact, user, mailingList, storeLocation, OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, tranParent.getTransactionId());
			if(contactsLoyalty != null) {
				ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
				tempContact.setLoyaltyCustomer((byte)1);
				contactsDaoForDML.saveOrUpdate(tempContact);
				updateParentTransaction(tranParent,user,null,contactsLoyalty);
				
				if(formMapping.getDoIssuePoints()==OCConstants.FLAG_YES){
					if((loyaltyPoints!=null && !loyaltyPoints.isEmpty())||(loyaltyCurrency!=null && !loyaltyCurrency.isEmpty())) {
						loyaltyPointAndCurrencyAdjustment(loyaltyPoints,loyaltyCurrency,contactsLoyalty,invoiceAmount,invoiceNumber,progObj);
					}
				}
			}
		}else if(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE.equalsIgnoreCase(progObj.getMembershipType())) {
			
			String mobilePhone = tempContact.getMobilePhone();
			if(mobilePhone!=null && mobilePhone.trim().length() !=0){
				mobilePhone = mobilePhone.trim();
				if(mobilePhone != null && mobilePhone.startsWith(user.getCountryCarrier()+"") && mobilePhone.length() >user.getUserOrganization().getMaxNumberOfDigits()) {
					mobilePhone =  mobilePhone.replaceFirst(user.getCountryCarrier()+"", "");
				}
				try {
					mobilePhone= Long.parseLong(mobilePhone)+"";
					
				} catch (Exception e) {
					logger.info("OOPs error ");
					msg += "Invalid phone no. Phone#: "+mobilePhone+"."; 
					return msg;
				}
				}
			
			String countryCarrier = user.getCountryCarrier()+"";
			String mobileWithCarrier=countryCarrier +mobilePhone;
			ContactsLoyalty existLtyContact = contactsLoyaltyDao.findByMembershipNoAndUserId(Long.parseLong(mobilePhone),Long.parseLong(mobileWithCarrier), progObj.getProgramId(), user.getUserId());
			if(existLtyContact != null){
				msg += "Phone already enrolled. Phone#: "+mobilePhone+"."; 
				sendFailureAlert(tempContact, formMapping, user, "Phone already enrolled. Phone#: "+mobilePhone+".");
				updateParentTransaction(tranParent,user,"Phone already enrolled. Phone#: "+mobilePhone+".",null);
				return msg;
			}
			LoyaltyProgramTier tier = findTier(progObj.getProgramId(), tempContact.getContactId(), user.getUserId());
			if(tier == null){
				msg += "\n Loyalty tier not found.";
				sendFailureAlert(tempContact, formMapping, user, "Loyalty tier not found.");
				updateParentTransaction(tranParent,user,"Loyalty tier not found.",null);
				return msg;
			}
			ContactsLoyalty contactsLoyalty = createMembership(invoiceAmount,progObj, null, tier, tempContact, user, mailingList, storeLocation, OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE, tranParent.getTransactionId());
			ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
			tempContact.setLoyaltyCustomer((byte)1);
			contactsDaoForDML.saveOrUpdate(tempContact);
			updateParentTransaction(tranParent,user,null,contactsLoyalty);
			
			if(formMapping.getDoIssuePoints()== OCConstants.FLAG_YES){
				if((loyaltyPoints!=null && !loyaltyPoints.isEmpty())||(loyaltyCurrency!=null && !loyaltyCurrency.isEmpty())) {
					loyaltyPointAndCurrencyAdjustment(loyaltyPoints,loyaltyCurrency,contactsLoyalty,invoiceAmount,invoiceNumber,progObj);
				}
			}
		}
		logger.debug("---------Exiting performOCLoyaltyEnrollment-----------");
		return msg;
	}//performOCLoyaltyEnrollment
	private void loyaltyPointAndCurrencyAdjustment(String loyaltyPoints,String loyaltyCurrency, ContactsLoyalty contactsLoyaltyObj , String invoiceAmount, String invoiceNumber, LoyaltyProgram loyaltyProgram) {
		try {
			boolean isValidNumber = isValidNumber(loyaltyPoints);
			if(loyaltyPoints!=null && !loyaltyPoints.isEmpty()) {
				isValidNumber = isValidNumber(loyaltyPoints);
			}else if(loyaltyCurrency!=null && !loyaltyCurrency.isEmpty()) {
				isValidNumber = checkIfDouble(loyaltyCurrency);
			}
			boolean flag = false;
			//LoyaltyProgram loyaltyProgram = null;
			Double fromLtyBalance = null;
			Double fromAmtBalance = null;
			Double fromLPVBalance = null;
			Double fromCPVBalance = null;
			LoyaltyProgramService ltyPrgmService = null ;
			if (isValidNumber) {
				if(contactsLoyaltyObj!=null) {
					 fromLtyBalance = contactsLoyaltyObj.getTotalLoyaltyEarned();
					 fromAmtBalance = contactsLoyaltyObj.getTotalGiftcardAmount();
				}
				ltyPrgmService = new LoyaltyProgramService();
				if(loyaltyCurrency!=null && !loyaltyCurrency.isEmpty()) {
					//update totalGiftcardAmount,giftcardBalance
					Double totalGiftCardAmount = contactsLoyaltyObj.getTotalGiftcardAmount()== null ? 0.0 : contactsLoyaltyObj.getTotalGiftcardAmount();
					//totalGiftCardAmount = totalGiftCardAmount + balanceToAdd ;
					totalGiftCardAmount = new BigDecimal(totalGiftCardAmount + Double.parseDouble(loyaltyCurrency)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					Double giftCardBalance =  contactsLoyaltyObj.getGiftcardBalance() == null ? 0.0 : contactsLoyaltyObj.getGiftcardBalance();
					//giftCardBalance = giftCardBalance + balanceToAdd ;
					giftCardBalance = new BigDecimal(giftCardBalance + Double.parseDouble(loyaltyCurrency)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					contactsLoyaltyObj.setTotalGiftcardAmount(totalGiftCardAmount);
					contactsLoyaltyObj.setGiftcardBalance(giftCardBalance);
					flag  =  ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
				}else if(loyaltyPoints!=null && !loyaltyPoints.isEmpty()) {
					Double loyaltyBalance =  contactsLoyaltyObj.getLoyaltyBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyBalance();
					Double totalLoyaltyEarned = contactsLoyaltyObj.getTotalLoyaltyEarned() == null ? 0.0 : contactsLoyaltyObj.getTotalLoyaltyEarned();
					logger.info("Previous LoyaltyBalance was ::::::::::::"+loyaltyBalance);
					loyaltyBalance = loyaltyBalance + Double.parseDouble(loyaltyPoints);
					totalLoyaltyEarned = totalLoyaltyEarned +  Long.parseLong(loyaltyPoints);
					contactsLoyaltyObj.setLoyaltyBalance(loyaltyBalance);
					contactsLoyaltyObj.setTotalLoyaltyEarned(totalLoyaltyEarned);
					
					flag = ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
				}
			if(flag){	
				String description2 = Constants.STRING_NILL;
				// CREATE TRANSACTION
				StringBuilder sb = new StringBuilder(Constants.STRING_NILL);
				if( (invoiceNumber!=null && !invoiceNumber.isEmpty())) {
					 sb.append("InvoiceNumber :").append(invoiceNumber).toString();
				}
				if((invoiceAmount!=null && !invoiceAmount.isEmpty())) {
					if(sb.length()>0) sb.append(" and ");
					sb.append("InvoiceAmount :").append(invoiceAmount);
				}
				description2 = sb.toString();
				LoyaltyTransactionChild transactionChild = null;
				Long adjustPoints = 0l;
				Double adjustAmt = 0.0;
				if(loyaltyCurrency!=null && !loyaltyCurrency.isEmpty()) {
					transactionChild = createPurchaseTransaction(contactsLoyaltyObj, Double.parseDouble(loyaltyCurrency), OCConstants.LOYALTY_TYPE_AMOUNT, Constants.STRING_NILL, null, null,OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD,description2,null);
					adjustAmt = Double.parseDouble(loyaltyCurrency);
				}
				else if(loyaltyPoints!=null && !loyaltyPoints.isEmpty()) {
					transactionChild = createPurchaseTransaction(contactsLoyaltyObj, Double.parseDouble(loyaltyPoints), OCConstants.LOYALTY_TYPE_POINTS, Constants.STRING_NILL, null, null,OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD,description2,null);
					adjustPoints = (long) Double.parseDouble(loyaltyPoints);
				}
				// CREATE EXPIRY TRANSACTION
				createExpiryTransaction(contactsLoyaltyObj, adjustPoints, adjustAmt, contactsLoyaltyObj.getOrgId(),	transactionChild.getTransChildId(),null);
				LoyaltyProgramTier loyaltyProgramTier = null;
				if(contactsLoyaltyObj.getProgramTierId() == null) {
					loyaltyProgramTier = findTier(contactsLoyaltyObj);
					if (loyaltyProgramTier == null) {
						// CALL BONUS
						updateThresholdBonus(contactsLoyaltyObj, loyaltyProgram, fromLtyBalance, fromAmtBalance,fromCPVBalance ,fromLPVBalance, null);
						ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
						//updateLoyaltyData(contactsLoyaltyObj);
						return;
					}
					else {
						contactsLoyaltyObj.setProgramTierId(loyaltyProgramTier.getTierId());
					}
				}
				else{
					loyaltyProgramTier = getLoyaltyTier(contactsLoyaltyObj.getProgramTierId());
				}
				
				Long pointsDifference = 0l;
				Double amountDifference = 0.0;
				String[] diffArr = applyConversionRules(contactsLoyaltyObj, loyaltyProgramTier); //0 - amountdiff, 1 - pointsdiff
				logger.info("balances After conversion rules updatation --  points = "+contactsLoyaltyObj.getLoyaltyBalance()+" currency = "+contactsLoyaltyObj.getGiftcardBalance());
				
				String conversionRate = null;
				long convertPoints = 0;
				double convertAmount = 0;
				if(diffArr != null){
					convertAmount = Double.valueOf(diffArr[0].trim());
					convertPoints = Double.valueOf(diffArr[1].trim()).longValue();
					conversionRate = diffArr[2];
				}
				pointsDifference = adjustPoints - convertPoints;
				amountDifference = (double)adjustAmt + (diffArr != null ? Double.parseDouble(diffArr[0].trim()) : 0.0);
				loyaltyProgramTier = applyTierUpgradeRule(contactsLoyaltyObj, loyaltyProgram, transactionChild, loyaltyProgramTier);
				updatePurchaseTransaction(transactionChild, contactsLoyaltyObj, ""+pointsDifference, ""+amountDifference, conversionRate, convertAmount,loyaltyProgramTier);
				logger.info("balances before balance object = "+contactsLoyaltyObj.getLoyaltyBalance()+" currency = "+contactsLoyaltyObj.getGiftcardBalance());

				// CALL BONUS
				updateThresholdBonus(contactsLoyaltyObj, loyaltyProgram, fromLtyBalance, fromAmtBalance, fromCPVBalance,fromLPVBalance, loyaltyProgramTier);
				ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
				LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(contactsLoyaltyObj.getProgramId());
				if(loyaltyAutoComm != null) {
					
					LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
					ContactsLoyalty contactsLoyalty=contactsLoyaltyObj;
					Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
					if (contact != null &&( contact.getEmailId() != null || 
							(contact.getUsers().isEnableSMS() && contactsLoyalty.getMobilePhone() != null)) ) {
						LoyaltyAutoComm autoComm = getLoyaltyAutoComm(contactsLoyaltyObj.getProgramId());
						if(autoComm != null ){
							if(autoComm.getAdjustmentAutoEmailTmplId()!=null ){
								
								if (contact.getEmailId() != null) {
									autoCommGen.sendAdjustmentTemplate(autoComm.getAdjustmentAutoEmailTmplId(),
											Constants.STRING_NILL + contactsLoyalty.getCardNumber(), contactsLoyalty.getCardPin(), contact.getUsers(),
											contact.getEmailId(), contact.getFirstName(), contact.getContactId(),
											contactsLoyalty.getLoyaltyId(),invoiceAmount);
								}
							}
							
							if (autoComm.getAdjustmentAutoSmsTmplId()!=null && contact.getUsers().isEnableSMS() 
									&& contactsLoyalty.getMobilePhone() != null) {
								autoCommGen.sendAdjustmentSMSTemplate(autoComm.getAdjustmentAutoSmsTmplId(), contact.getUsers(),
										contactsLoyalty.getContact().getContactId(), contactsLoyalty.getLoyaltyId(),
										contactsLoyalty.getMobilePhone(),invoiceAmount);
							}
							
						}
						
						
				}
				
					
				}
			}

		}

	} catch (Exception e) {
			e.printStackTrace();
	}

}

	/**
	 * update loyalty card status
	 * @param loyaltyCard
	 * @param status
	 * @throws Exception
	 */
	private void updateLoyaltyCardStatus(LoyaltyCards loyaltyCard, String status) throws Exception {
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
		loyaltyCard.setStatus(status);
		//loyaltyCardsDao.saveOrUpdate(loyaltyCard);
		loyaltyCardsDaoForDML.saveOrUpdate(loyaltyCard);
	}//updateLoyaltyCardStatus

	/**
	 * create a LoyaltyTransactionParent obj
	 * @return
	 */
	private LoyaltyTransactionParent createNewTransaction(){
		logger.debug("---------Entered createNewTransaction-----------");
		LoyaltyTransactionParent tranx  = null; 
		try{
			LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
			LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
			tranx = new LoyaltyTransactionParent();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			tranx.setCreatedDate(cal);
			tranx.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			//parentDao.saveOrUpdate(tranx);
			parentDaoForDML.saveOrUpdate(tranx);
		}catch(Exception e){
			logger.error("Exception while createing new transaction...", e);
		}
		logger.debug("---------Exiting createNewTransaction-----------");
		return tranx;
	}//createNewTransaction

	/**
	 * update the parentTransaction object 
	 * @param tranParent
	 * @param user
	 * @param errMsg
	 * @param contLty
	 * @throws Exception
	 */
	private void updateParentTransaction(LoyaltyTransactionParent tranParent, Users user, String errMsg,
			ContactsLoyalty contLty) throws Exception {
		logger.debug("---------Entered updateParentTransaction-----------");
		tranParent.setUserName(user.getUserName());
		tranParent.setRequestDate(MyCalendar.calendarToString(Calendar.getInstance(), null));
		if(contLty == null){
			tranParent.setStatus(OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			tranParent.setErrorMessage(errMsg);
		}
		else {
			tranParent.setStatus(OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
			tranParent.setMembershipNumber(contLty.getCardNumber().toString());
			tranParent.setMobilePhone(contLty.getMobilePhone());
		}
		LoyaltyTransactionParentDao parentDao = (LoyaltyTransactionParentDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO);
		LoyaltyTransactionParentDaoForDML parentDaoForDML = (LoyaltyTransactionParentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_PARENT_DAO_FOR_DML);
		//parentDao.saveOrUpdate(tranParent);
		parentDaoForDML.saveOrUpdate(tranParent);
		logger.debug("---------Exiting updateParentTransaction-----------");
	}//updateParentTransaction

	/**
	 * sends loyalty threshold alerts
	 * @param cardSetIdStr
	 * @param autoSelectCard
	 * @param user
	 * @param progObj
	 * @param formName 
	 * @param cardSetObj
	 * @throws Exception
	 */
	private void sendLoyaltyThresholdAlerts(String cardSetIdStr, Users user, LoyaltyProgram progObj, String formName) throws Exception {
		logger.debug("---------Entered sendLoyaltyThresholdAlerts-----------");
		
		LoyaltyThresholdAlertsDao loyaltyThresholdAlertsDao = (LoyaltyThresholdAlertsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO);
		LoyaltyThresholdAlertsDaoForDML loyaltyThresholdAlertsDaoForDML = (LoyaltyThresholdAlertsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO_FOR_DML);
		LoyaltyThresholdAlerts alertsObj = loyaltyThresholdAlertsDao.findByUserId(user.getUserId());

		if(alertsObj != null){
			if(alertsObj.getEnableAlerts() == OCConstants.FLAG_NO) return;

			if(alertsObj.getCountType() == null || alertsObj.getCountType().isEmpty() || alertsObj.getCountValue() == null || alertsObj.getCountValue().isEmpty()) return;
		
			String emailId = alertsObj.getAlertEmailId() == null ? "" : alertsObj.getAlertEmailId().trim();
			String phone = alertsObj.getAlertMobilePhn() == null ? "" : alertsObj.getAlertMobilePhn().trim();
			Calendar currentDate = Calendar.getInstance();
			Calendar lastSentDate = alertsObj.getWebformAlertLastSentDate();

			logger.debug("dbCurrentDate "+lastSentDate);

			int dateDiff = 0;
			if(lastSentDate != null){
				logger.debug("currentDate.getTimeInMillis()"+currentDate.getTimeInMillis());
				logger.debug(" dbCurrentDate.getTimeInMillis())"+ lastSentDate.getTimeInMillis());
				dateDiff = (int)((currentDate.getTimeInMillis() - lastSentDate.getTimeInMillis())/(1000*60));
			}

			logger.debug("dateDiff  "+dateDiff);

			if(dateDiff > 24*60 || lastSentDate == null) {

				LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
				Long totalInvCards = loyaltyCardsDao.getCardsCountByCardSetId(cardSetIdStr,true,progObj.getProgramId());
				logger.info("prgmId in ContactSubacriptionProcessHelper is  ::"+progObj.getProgramId());

				Long tresholdCount = null;

				if(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE.equalsIgnoreCase(alertsObj.getCountType())){
					Long totalCards = loyaltyCardsDao.getCardsCountByCardSetId(cardSetIdStr,false,progObj.getProgramId());
					Long percentValue = Long.parseLong(alertsObj.getCountValue());
					tresholdCount = (percentValue * totalCards)/100;
				}
				else if(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_VALUE.equalsIgnoreCase(alertsObj.getCountType())){
					tresholdCount = Long.parseLong(alertsObj.getCountValue());
				}

				if(totalInvCards <= tresholdCount){
					if(emailId != null && !emailId.isEmpty()) {
						String messageStr = PropertyUtil.getPropertyValueFromDB("lowCardThresholdWebformTemplate");
						messageStr = messageStr.replace("[fname]", user.getFirstName() == null ? "" : user.getFirstName());
						messageStr = messageStr.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_USERNAME, Utility.getOnlyUserName(user.getUserName()));
						messageStr = messageStr.replace(Constants.NEW_USER_DETAILS_PLACEHOLDERS_ORGID, Utility.getOnlyOrgId(user.getUserName()));
						messageStr = messageStr.replace("[programName]", progObj.getProgramName());
						messageStr = messageStr.replace("[noofcards]", totalInvCards+"");
						messageStr = messageStr.replace("[webformName]", formName);

						EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
						EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
						EmailQueue email = new EmailQueue("OptCulture Alert: Running Low On Loyalty Cards", messageStr, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS, Constants.EQ_STATUS_ACTIVE, emailId, MyCalendar.getNewCalendar(), user);
						//emailQueueDao.saveOrUpdate(email);
						emailQueueDaoForDML.saveOrUpdate(email);
						alertsObj.setWebformAlertLastSentDate(Calendar.getInstance());
						//loyaltyThresholdAlertsDao.saveOrUpdate(alertsObj);
						loyaltyThresholdAlertsDaoForDML.saveOrUpdate(alertsObj);

						logger.debug("--------loyalty alert date-------------"+alertsObj.getWebformAlertLastSentDate());
					}
					if(phone != null && !phone.isEmpty()){
						if(!user.isEnableSMS()) return;

						String message = PropertyUtil.getPropertyValueFromDB("lowCardThresholdWebformSmsTemplate");
						message = message.replace("[programName]", progObj.getProgramName());
						message = message.replace("[noofcards]", totalInvCards+"");
						message = message.replace("[webformName]", formName);

						//validate loyalty phone no and new phone no
						Map<String, Object> resultMap = null;

						resultMap = LoyaltyProgramHelper.validateMobile(user, phone);
						phone = (String) resultMap.get("phone");
						boolean phoneIsValid = (Boolean) resultMap.get("isValid");

						if(phoneIsValid){
							LoyaltyProgramHelper.sendSmsAlert(user, phone, message);
						}
						alertsObj.setWebformAlertLastSentDate(Calendar.getInstance());
						//loyaltyThresholdAlertsDao.saveOrUpdate(alertsObj);
						loyaltyThresholdAlertsDaoForDML.saveOrUpdate(alertsObj);
						logger.debug("--------loyalty alert date-------------"+alertsObj.getWebformAlertLastSentDate());
					}
				}
			}
		}
		/*
		String message = "";
		if(autoSelectCard == OCConstants.FLAG_YES) {
			message = "<html><head></head><body><font style=\"font-family: Arial ;font-size: 15;line-height: 150%;word-spacing: normal;\">"
					+ "Hi,<br/><br/>Number of available cards in the following user's loyalty program has gone below threshold. <br/><br/>"
					+ "Username = [username]<br/>Organization : [organization]<br/>Program Name : [program]<br/>"
					+ "Number of available cards = [noofcards]<br/><br/>Please add more cards immediately to avoid issue with enrollments.<br/><br/>"
					+ "Regards,<br/>Team OptCulture</font></body></html>";
		}
		else if(autoSelectCard == OCConstants.FLAG_NO) {
			message = "<html><head></head><body><font style=\"font-family: Arial ;font-size: 15;line-height: 150%;word-spacing: normal;\">"
					+ "Hi,<br/><br/>Number of available cards in the following user's loyalty program card-set has gone below threshold. <br/><br/>"
					+ "Username = [username]<br/>Organization : [organization]<br/>Program Name : [program]<br/>Card-set Name : [cardset]<br/>"
					+ "Number of available cards = [noofcards]<br/><br/>Please add more cards immediately to avoid issue with enrollments.<br/><br/>"
					+ "Regards,<br/>Team OptCulture</font></body></html>";
		}
		LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
		Long totalCards = loyaltyCardsDao.getCardsCountByCardSetId(cardSetIdStr,false);
		Long totalInvCards = loyaltyCardsDao.getCardsCountByCardSetId(cardSetIdStr,true);
		Long tresholdCount = (10 * totalCards)/100;
		if(totalInvCards <= tresholdCount) {
			String supportEmailId = PropertyUtil.getPropertyValueFromDB("SupportEmailId");
			message = message.replace("[username]", Utility.getOnlyUserName(user.getUserName()));
			message = message.replace("[organization]", Utility.getOnlyOrgId(user.getUserName()));
			message = message.replace("[program]", progObj.getProgramName());
			if(cardSetObj != null){
				message = message.replace("[cardset]", cardSetObj.getCardSetName());
			}
			EmailQueue eqObj = new EmailQueue("Loyalty Enrollments through Web-form failure",message, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS,
					"Active",supportEmailId,MyCalendar.getNewCalendar(), user);
			EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			emailQueueDao.saveOrUpdate(eqObj);
		}*/
		logger.debug("---------Exiting sendLoyaltyThresholdAlerts-----------");
	}//sendLoyaltyThresholdAlerts

	/**
	 * sends failure alerts in case of loyalty enrollment failures
	 * @param tempContact
	 * @param formMapping
	 * @param user
	 * @param reason
	 * @throws Exception
	 */
	private void sendFailureAlert(Contacts tempContact, FormMapping formMapping, Users user, String reason) throws Exception {
		logger.debug("---------Entered sendFailureAlert-----------");
		String supportEmailId = PropertyUtil.getPropertyValueFromDB("SupportEmailId");
		String message = "<html><head></head><body><font style=\"font-family: Arial ;font-size: 15;line-height: 150%;word-spacing: normal;\">"
				+ "Hi,<br/><br/>A loyalty enrollment through webform failed. The details about the reason for failure, webform, user and "
				+ "contact are as follows: <br/><br/>Firstname : [fname]<br/>Lastname : [lname]<br/>Email Id : [email]<br/>Mobile Phone :[mobile]<br/>"
				+ "Reason for failure :[reason]<br/>Username : [username]<br/>Organization : [organization]<br/>Webform name : [webform]<br/><br/>"
				+ "Regards,<br/>Team OptCulture</font></body></html>";
		message = message.replace("[fname]", tempContact.getFirstName() == null ? "" : tempContact.getFirstName());
		message = message.replace("[lname]", tempContact.getLastName() == null ? "" : tempContact.getLastName());
		message = message.replace("[email]", tempContact.getEmailId() == null ? "" : tempContact.getEmailId());
		message = message.replace("[mobile]", tempContact.getMobilePhone() == null ? "" : tempContact.getMobilePhone());
		message = message.replace("[reason]", reason);
		message = message.replace("[username]", Utility.getOnlyUserName(user.getUserName()));
		message = message.replace("[organization]", Utility.getOnlyOrgId(user.getUserName()));
		message = message.replace("[webform]", formMapping.getFormMappingName());
		EmailQueue eqObj = new EmailQueue("Loyalty Email Alerts",message, Constants.EQ_TYPE_LOYALTY_EMAIL_ALERTS,
				"Active",supportEmailId,MyCalendar.getNewCalendar(), user);
		EmailQueueDao emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
		EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
		//emailQueueDao.saveOrUpdate(eqObj);
		emailQueueDaoForDML.saveOrUpdate(eqObj);
		logger.debug("---------Exiting sendFailureAlert-----------");
	}//sendFailureAlert

	/**
	 * finds the tier into which the contact is to be enrolled
	 * @param programId
	 * @param contactId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private LoyaltyProgramTier findTier(Long programId, Long contactId, Long userId) throws Exception {

		LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		ContactsLoyalty contactsLoyalty = null;
		contactsLoyalty = contactsLoyaltyDao.findByContactId(userId, contactId);
		List<LoyaltyProgramTier> tiersList = loyaltyProgramTierDao.fetchTiersByProgramId(programId);
		if (tiersList == null || tiersList.size() <= 0) {
			logger.info("Tiers list is empty...");
			return null;
		}
		else if (tiersList.size() >= 1) {
			Collections.sort(tiersList, new Comparator<LoyaltyProgramTier>() {
				@Override
				public int compare(LoyaltyProgramTier o1, LoyaltyProgramTier o2) {

					int num1 = Integer.valueOf(o1.getTierType().substring(5)).intValue();
					int num2 = Integer.valueOf(o2.getTierType().substring(5)).intValue();
					if(num1 < num2){
						return -1;
					}
					else if(num1 == num2){
						return 0;
					}
					else{
						return 1;
					}
				}
			});
		}

		/*for(LoyaltyProgramTier tier : tiersList) {//testing purpose
			logger.info("tier level : "+tier.getTierType());
		}*/

		if(!OCConstants.LOYALTY_PROGRAM_TIER1.equals(tiersList.get(0).getTierType())){// if tier 1 not exist return null
			logger.info("selected tier...null...tier1 not found");
			return null;
		}

		//Prepare eligible tiers map
		Iterator<LoyaltyProgramTier> iterTier = tiersList.iterator();
		Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
		LoyaltyProgramTier prevtier = null;
		LoyaltyProgramTier nexttier = null;

		while(iterTier.hasNext()){
			nexttier = iterTier.next();
			if(OCConstants.LOYALTY_PROGRAM_TIER1.equals(nexttier.getTierType())){
				eligibleMap.put(nexttier, null);
			}
			else{
				if((Integer.valueOf(prevtier.getTierType().substring(5))+1) 
						== Integer.valueOf(nexttier.getTierType().substring(5)) && prevtier.getTierUpgdConstraintValue() != null){
					eligibleMap.put(nexttier, prevtier);
					logger.info("eligible tier ="+nexttier.getTierType()+" upgdconstrant value = "+prevtier.getTierUpgdConstraintValue());
				}
			}
			prevtier = nexttier;
		}

		if(OCConstants.LOYALTY_LIFETIME_POINTS.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_POINTS);
			return tiersList.get(0);
		}
		else if(contactId == null){
			logger.info("contactId is null and selected tier..."+tiersList.get(0).getTierType());
			return tiersList.get(0);
		}
		else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);
			
			ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);				

			//List<Map<String, Object>> contactPurcahseList = contactsDao.findContactPurchaseDetails(userId, contactId);
			Double totPurchaseValue = null;
			/*if(contactPurcahseList != null && contactPurcahseList.size() == 1) {
				for (Map<String, Object> eachMap : contactPurcahseList) {
					if(eachMap.containsKey("tot_purchase_amt")){
						totPurchaseValue = Double.valueOf(eachMap.get("tot_purchase_amt") != null ? eachMap.get("tot_purchase_amt").toString() : "0.00");
						logger.info("purchase value = "+totPurchaseValue);
					}
				}
			}*/
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			if(contactsLoyalty != null)//APP-1475
			//totPurchaseValue = Double.valueOf(loyaltyTransactionChildDao.getLifeTimeLoyaltyPurchaseValue(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId()));
			//totPurchaseValue=contactsLoyalty.getLifeTimePurchaseValue();
			totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);

			logger.info("purchase value = "+totPurchaseValue);

			//if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){
			if(totPurchaseValue == null || totPurchaseValue <= 0){
				logger.info("purchase value is empty...");
				return tiersList.get(0);
			}
			else{
				Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
				LoyaltyProgramTier prevKeyTier = null;
				LoyaltyProgramTier nextKeyTier = null;
				while(it.hasNext()){
					nextKeyTier = it.next();
					logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
					logger.info("-------------tiersList.get(0)::"+tiersList.get(0).getTierType());
					if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
						prevKeyTier = nextKeyTier;
						continue;
					}
					if(totPurchaseValue > 0 && totPurchaseValue < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
						if(prevKeyTier == null){
							logger.info("selected tier is currTier..."+tiersList.get(0).getTierType());
							return tiersList.get(0);
						}
						logger.info("selected tier..."+prevKeyTier.getTierType());
						return prevKeyTier;
					}
					else if (totPurchaseValue > 0 && totPurchaseValue >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
						logger.info("selected tier..."+nextKeyTier.getTierType());
						return nextKeyTier;
					}
					prevKeyTier = nextKeyTier;
				}
				return tiersList.get(0);
			}
		}
		else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			Double cumulativeAmount = 0.0;
//			Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
			ListIterator<LoyaltyProgramTier> it = new ArrayList(eligibleMap.keySet()).listIterator(eligibleMap.size());
//			LoyaltyProgramTier prevKeyTier = null;
			LoyaltyProgramTier nextKeyTier = null;
			while(it.hasPrevious()){
				nextKeyTier = it.previous();
				logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
				logger.info("-------------currTier::"+tiersList.get(0).getTierType());
				if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
//					prevKeyTier = nextKeyTier;
					return tiersList.get(0);
				}
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				logger.info("contactId = "+contactId+" startDate = "+startDate+" endDate = "+endDate);

				/*RetailProSalesDao salesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
				Object[] cumulativeAmountArr = salesDao.getCumulativePurchase(userId, contactId, startDate, endDate);

				cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());*/
				
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				if(contactsLoyalty != null)//APP-1475
				cumulativeAmount = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));


				if(cumulativeAmount == null || cumulativeAmount <= 0){
					logger.info("cumulative purchase value is empty...");
					continue;
				}
				
				if(cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
					return nextKeyTier;
				}
				
			}
			/*while(it.hasNext()){
				nextKeyTier = it.next();
				logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
				logger.info("-------------tiersList.get(0)::"+tiersList.get(0).getTierType());
				if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
					prevKeyTier = nextKeyTier;
					continue;
				}
				Calendar startCal = Calendar.getInstance();
				Calendar endCal = Calendar.getInstance();
				endCal.add(Calendar.MONTH, -((LoyaltyProgramTier) eligibleMap.get(nextKeyTier)).getTierUpgradeCumulativeValue().intValue());

				String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
				logger.info("contactId = "+contactId+" startDate = "+startDate+" endDate = "+endDate);

				Object[] cumulativeAmountArr = getCumulativeValue(startDate, endDate);

				cumulativeAmount = Double.valueOf(cumulativeAmountArr[0].toString());

				if(cumulativeAmount == null || cumulativeAmount <= 0){
					logger.info("cumulative purchase value is empty...");
					continue;
				}
				if(cumulativeAmount > 0 && cumulativeAmount < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
					if(prevKeyTier == null){
						logger.info("selected tier is currTier..."+tiersList.get(0).getTierType());
						return tiersList.get(0);
					}
					logger.info("selected tier..."+prevKeyTier.getTierType());
					return prevKeyTier;
				}
				else if (cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
					logger.info("selected tier..."+nextKeyTier.getTierType());
					return nextKeyTier;
				}
				prevKeyTier = nextKeyTier;
			}*/
			return tiersList.get(0);
		}
		else{
			return null;
		}
	}//findTier

	/**
	 * create a loyalty membership
	 * @param progObj
	 * @param card
	 * @param tier
	 * @param tempContact
	 * @param user
	 * @param mailingList
	 * @param storeNumber
	 * @param membershipType
	 * @param transactionId
	 * @return
	 * @throws Exception
	 */
	private ContactsLoyalty createMembership(String invoiceAmount,LoyaltyProgram progObj, LoyaltyCards card, LoyaltyProgramTier tier, Contacts tempContact,
			Users user, MailingList mailingList, String storeNumber, String membershipType, Long transactionId) throws Exception {
		logger.debug("---------Entered createMembership-----------");
		ContactsLoyalty contactLoyalty = null;
		if(membershipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)){
			contactLoyalty = prepareLoyaltyMembership(card.getCardNumber(), OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD, card.getCardPin(), tempContact.getMobilePhone(),tempContact.getEmailId(),
					Constants.CONTACT_LOYALTY_TYPE_WEBFORM,	storeNumber, progObj, tier, card.getCardSetId());
		}else if (membershipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)){
			contactLoyalty = prepareLoyaltyMembership(tempContact.getMobilePhone(), OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE,Constants.STRING_NILL, tempContact.getMobilePhone(),tempContact.getEmailId(),
					Constants.CONTACT_LOYALTY_TYPE_WEBFORM,	storeNumber, progObj, tier, card!=null?card.getCardSetId():null);
		}
		contactLoyalty.setUserId(user.getUserId());
		contactLoyalty.setOrgId(user.getUserOrganization().getUserOrgId());
//		contactLoyalty.setSubsidiaryNumber(getSBS(storeNumber, user.getUserId(), user.getUserOrganization().getUserOrgId()));

		logger.info("Store Number>>>>>>>>>>>>>>>>>" + storeNumber);
		logger.info("Subsidiary Number>>>>>>>>>>>>>>>>" + tempContact.getSubsidiaryNumber());

		// APP-3983
		//Added code so that correct subsidiary number will be saved in contacts_loyalty table(If subsidiary is present)
		if (tempContact.getSubsidiaryNumber() == null || tempContact.getSubsidiaryNumber().isEmpty())
			contactLoyalty.setSubsidiaryNumber(
					getSBS(storeNumber, user.getUserId(), user.getUserOrganization().getUserOrgId()));
		else
			contactLoyalty.setSubsidiaryNumber(tempContact.getSubsidiaryNumber());
 		
		contactLoyalty.setContact(tempContact);
		contactLoyalty.setCustomerId(tempContact.getExternalId());
		contactLoyalty.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);

		//generate a pwd and encrypt it and save it...
		String memPwd = RandomStringUtils.randomAlphanumeric(6);
		String encPwd = generateMembrshpPwd(memPwd);
		contactLoyalty.setMembershipPwd(encPwd);
		
		ContactsLoyaltyDaoForDML contactsLoyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
		contactsLoyaltyDao.saveOrUpdate(contactLoyalty);

		
		if(membershipType.equals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)){
			card.setMembershipId(contactLoyalty.getLoyaltyId());
			card.setActivationDate(Calendar.getInstance());
			card.setStatus(OCConstants.LOYALTY_CARD_STATUS_ENROLLED);
			LoyaltyCardsDao loyaltyCardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			LoyaltyCardsDaoForDML loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
			//loyaltyCardsDao.saveOrUpdate(card);
			loyaltyCardsDaoForDML.saveOrUpdate(card);
		}
		LoyaltyAutoComm loyaltyAutoComm = getLoyaltyAutoComm(progObj.getProgramId());
		LoyaltyTransactionChild transChild = createSuccessfulTransaction(contactLoyalty, transactionId, user.getUserId(), 
				user.getUserOrganization().getUserOrgId(), progObj.getProgramId(), membershipType, "loyaltyEnroll");
		updateMembershipBalances(contactLoyalty, progObj, tier, transChild.getTransChildId(), loyaltyAutoComm,
				tempContact.getEmailId(), tempContact.getUsers(), tempContact.getFirstName(), tempContact.getContactId());
		contactsLoyaltyDao.saveOrUpdate(contactLoyalty);
		if (loyaltyAutoComm != null) {
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			//Send Loyalty Registration Email
			if (tempContact.getEmailId() != null && loyaltyAutoComm.getRegEmailTmpltId() != null) {
				//email queue
				autoCommGen.sendWebEnrollTemplate(invoiceAmount,loyaltyAutoComm.getRegEmailTmpltId(), ""+ contactLoyalty.getCardNumber(), contactLoyalty.getCardPin(),
						user, tempContact.getEmailId(), tempContact.getFirstName(),	tempContact.getContactId(),contactLoyalty.getLoyaltyId(), memPwd);
			}
			if (user.isEnableSMS()
					&& loyaltyAutoComm.getRegSmsTmpltId() != null) {
				//sms queue
				Long cid = null;
				if (contactLoyalty.getContact() != null	&& contactLoyalty.getContact().getContactId() != null) {
					cid = contactLoyalty.getContact().getContactId();
				}
				autoCommGen.sendWebEnrollSMSTemplate(invoiceAmount,loyaltyAutoComm.getRegSmsTmpltId(), user, cid, contactLoyalty.getLoyaltyId(), 
						tempContact.getMobilePhone(), memPwd);
			}
		}
		logger.debug("---------Exiting createMembership-----------");  
		return contactLoyalty;
	}//createMembership

	private String generateMembrshpPwd(String memPwd) throws Exception {
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		String encPwd = "";
		//do {
			
			encPwd = EncryptDecryptLtyMembshpPwd.encrypt(memPwd); 
		//} while(loyaltyDao.findByMembrshpPwd(encPwd) != null);
		return encPwd;
	}

	/**
	 * updates the membership's balances
	 * @param loyalty
	 * @param program
	 * @param tier
	 * @param transId
	 * @param autoComm
	 * @param emailId
	 * @param user
	 * @param firstName
	 * @param contactId
	 * @throws Exception
	 */
	private void updateMembershipBalances(ContactsLoyalty loyalty, LoyaltyProgram program, LoyaltyProgramTier tier,
			Long transId, LoyaltyAutoComm autoComm, String emailId, Users user, String firstName, Long contactId) throws Exception {
		logger.debug("---------Entered updateMembershipBalances-----------");
		Double fromLtyBalance = loyalty.getTotalLoyaltyEarned();
		Double fromAmtBalance = loyalty.getTotalGiftcardAmount();
		LoyaltyThresholdBonusDao bonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
		List<LoyaltyThresholdBonus> bonusList = bonusDao.getBonusListByPrgmId(program.getProgramId());
		if(bonusList == null || bonusList.size() == 0) return;
		for(LoyaltyThresholdBonus bonus : bonusList){
			if(bonus.getRegistrationFlag() == 'Y'){
				String earnType = null;
				double earnedValue = 0;
				boolean bonusPointsFlag = false;
				if(bonus.getExtraBonusType().equals(OCConstants.LOYALTY_TYPE_POINTS)){
					bonusPointsFlag = true;
					earnType = OCConstants.LOYALTY_TYPE_POINTS;
					earnedValue = bonus.getExtraBonusValue();
					if(loyalty.getTotalLoyaltyEarned() == null){
						loyalty.setTotalLoyaltyEarned(bonus.getExtraBonusValue());
					}
					else{
						loyalty.setTotalLoyaltyEarned(loyalty.getTotalLoyaltyEarned()+bonus.getExtraBonusValue());
					}
					if(loyalty.getLoyaltyBalance() == null){
						loyalty.setLoyaltyBalance(bonus.getExtraBonusValue());
					}
					else{
						loyalty.setLoyaltyBalance(loyalty.getLoyaltyBalance()+bonus.getExtraBonusValue());
					}
				}
				else if(bonus.getExtraBonusType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
					earnType = OCConstants.LOYALTY_TYPE_AMOUNT;
					earnedValue = bonus.getExtraBonusValue();
					if(loyalty.getTotalGiftcardAmount() == null){
						loyalty.setTotalGiftcardAmount(bonus.getExtraBonusValue());
					}
					else{
						loyalty.setTotalGiftcardAmount(loyalty.getTotalGiftcardAmount()+bonus.getExtraBonusValue());
					}
					if(loyalty.getGiftcardBalance() == null){
						loyalty.setGiftcardBalance(bonus.getExtraBonusValue());
					}
					else{
						loyalty.setGiftcardBalance(loyalty.getGiftcardBalance()+bonus.getExtraBonusValue());
					}
				}
				// Send auto communication email
				if(autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null){
					if(emailId != null && autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null){
						LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
						autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+loyalty.getCardNumber(),
								loyalty.getCardPin(), user, emailId, firstName, contactId,loyalty.getLoyaltyId());
					}
				}
				// create a child transaction
				LoyaltyTransactionChild childTxbonus = createBonusTransaction(loyalty, earnedValue, earnType, "Registration Bonus");
				double pointsbonus = 0.0;
				double amountbonus = 0.0;
				if(bonusPointsFlag){
					pointsbonus = bonus.getExtraBonusValue();
				}
				else{
					amountbonus = bonus.getExtraBonusValue();
				}
				createExpiryTransaction(loyalty, (long)pointsbonus, amountbonus, loyalty.getOrgId(), 
						childTxbonus.getTransChildId(),bonus.getThresholdBonusId());
				
				applyConversionRules(loyalty, childTxbonus, program, tier);
				tier = applyTierUpgradeRule(loyalty, program, childTxbonus, tier);
				break;
			}//if bonus flag
		}
		
		updateThresholdBonus(loyalty, program, fromLtyBalance, fromAmtBalance, tier);
		
		logger.debug("---------Exiting updateMembershipBalances-----------");
	}//updateMembershipBalances

	private LoyaltyProgramTier applyTierUpgradeRule(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, LoyaltyTransactionChild transactionChild, LoyaltyProgramTier currTier){
		logger.debug(">>>>>>>>>>>>> entered in applyTierUpgradeRule");
		try{
			boolean tierUpgd = false;

			LoyaltyProgramTier newTier = LoyaltyProgramHelper.applyTierUpgdRules(contactsLoyalty.getContact().getContactId(), contactsLoyalty, currTier);
			if(!newTier.getTierType().equalsIgnoreCase(currTier.getTierType())){
				currTier = newTier;
				tierUpgd = true;
			}
			
			if(tierUpgd){
				contactsLoyalty.setProgramTierId(currTier.getTierId());
				contactsLoyalty.setTierUpgradedDate(Calendar.getInstance());
				contactsLoyalty.setTierUpgradeReason(currTier.getTierUpgdConstraint());
				ContactsLoyaltyDaoForDML contactsLoyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
				contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);

				transactionChild.setTierId(currTier.getTierId());
				LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
				LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
				//loyaltyTransactionChildDao.saveOrUpdate(transactionChild);
				loyaltyTransactionChildDaoForDML.saveOrUpdate(transactionChild);
			}
			
			Contacts contact = null;
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			LoyaltyAutoComm autoComm = getLoyaltyAutoComm(program.getProgramId());
			if(tierUpgd && autoComm != null && autoComm.getTierUpgdEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
					contactsLoyalty.getContact().getContactId() != null){
				contact = findContactById(contactsLoyalty.getContact().getContactId());
				if(contact != null && contact.getEmailId() != null){
					autoCommGen.sendTierUpgdTemplate(autoComm.getTierUpgdEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
							contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(),
							contact.getFirstName(), contact.getContactId(), contactsLoyalty.getLoyaltyId());
				}

			}
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = usersDao.findByUserId(contactsLoyalty.getUserId());
			if(user.isEnableSMS() && tierUpgd && autoComm != null && autoComm.getTierUpgdSmsTmpltId() != null) {
				Long contactId = null;
				if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null){
					contactId = contactsLoyalty.getContact().getContactId();
				}
				autoCommGen.sendTierUpgdSMSTemplate(autoComm.getTierUpgdSmsTmpltId(), user, contactId,
						contactsLoyalty.getLoyaltyId(), null);
			}

			//contactsLoyaltyDao.saveOrUpdate(contactsLoyalty);
		}catch(Exception e){
			logger.error("Exception while upgrading tier...", e);
		}
		logger.debug("<<<<<<<<<<<<< completed applyTierUpgradeRule");
		return currTier;
	}//applyTierUpgradeRule
	
	
	private void updateThresholdBonus(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, Double fromLtyBalance, Double fromAmtBalance, LoyaltyProgramTier tier) throws Exception {
		logger.info(" Entered into updateThresholdBonus method >>>");
		
		try{
			LoyaltyThresholdBonusDao loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			List<LoyaltyThresholdBonus> threshBonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(program.getProgramId(), 'N' );
			List<LoyaltyThresholdBonus> pointsBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> amountBonusList = new ArrayList<LoyaltyThresholdBonus>();
			fromAmtBalance = fromAmtBalance == null ? 0.0 : fromAmtBalance;
			fromLtyBalance = fromLtyBalance == null ? 0.0 : fromLtyBalance;
			
			if(threshBonusList == null) return ;
			
			for(LoyaltyThresholdBonus bonus : threshBonusList){
				if(bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_POINTS)){
					pointsBonusList.add(bonus);
				}
				else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
					amountBonusList.add(bonus);
				}
			}
			
			if(pointsBonusList.isEmpty() && amountBonusList.isEmpty()) return;
			
			List<LoyaltyThresholdBonus> matchedBonusList = new ArrayList<LoyaltyThresholdBonus>();
			
			if(pointsBonusList.size() > 0){
				Collections.sort(pointsBonusList, new Comparator<LoyaltyThresholdBonus>(){
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			if(amountBonusList.size() > 0){
				Collections.sort(amountBonusList, new Comparator<LoyaltyThresholdBonus>(){
					@Override
					public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
						return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
					}
				});
			}
			if(contactsLoyalty.getTotalLoyaltyEarned() != null && contactsLoyalty.getTotalLoyaltyEarned() > 0){
				for(LoyaltyThresholdBonus bonus : pointsBonusList){
					if(contactsLoyalty.getTotalLoyaltyEarned() >= bonus.getEarnedLevelValue() && 
							(fromLtyBalance == null || fromLtyBalance.doubleValue() < bonus.getEarnedLevelValue())){
						matchedBonusList.add(bonus);
					}
				}
			}
			if(contactsLoyalty.getTotalGiftcardAmount() != null && contactsLoyalty.getTotalGiftcardAmount() > 0){
				for(LoyaltyThresholdBonus bonus : amountBonusList){
					if(contactsLoyalty.getTotalGiftcardAmount() >= bonus.getEarnedLevelValue() && 
							(fromAmtBalance == null || fromAmtBalance.doubleValue() < bonus.getEarnedLevelValue())){
						matchedBonusList.add(bonus);
					}
				}
			}
			 
			long bonusPoints = 0;
			double bonusAmount = 0.0;
			String bonusRate = null;
			boolean bonusflag =false;
			
			if(matchedBonusList != null && matchedBonusList.size() > 0){
				for (LoyaltyThresholdBonus matchedBonus : matchedBonusList) {
					if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getExtraBonusType())){
						bonusflag = true;
						logger.info("loyalty bonus type :Points:");
						bonusPoints = matchedBonus.getExtraBonusValue().longValue();
						bonusRate = ""+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
								" --> "+matchedBonus.getExtraBonusValue()+" "+OCConstants.LOYALTY_TYPE_POINTS;
						if(contactsLoyalty.getLoyaltyBalance() == null ) {
							contactsLoyalty.setLoyaltyBalance(matchedBonus.getExtraBonusValue());
						}
						else{
							contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() + matchedBonus.getExtraBonusValue());
						}
						if(contactsLoyalty.getTotalLoyaltyEarned() == null){
							contactsLoyalty.setTotalLoyaltyEarned(matchedBonus.getExtraBonusValue());
						}
						else{
							contactsLoyalty.setTotalLoyaltyEarned(contactsLoyalty.getTotalLoyaltyEarned() + matchedBonus.getExtraBonusValue());
						}
						LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, Double.valueOf(matchedBonus.getExtraBonusValue()), OCConstants.LOYALTY_TYPE_POINTS, bonusRate);
								
						logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
						createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
								childTxbonus.getTransChildId(),matchedBonus.getThresholdBonusId());
						applyConversionRules(contactsLoyalty, childTxbonus, program, tier);
						tier = applyTierUpgradeRule(contactsLoyalty, program, childTxbonus, tier);
						
						
					}
					else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getExtraBonusType())){
						bonusflag = true;
						logger.info("loyalty bonus type :Amount:");
						bonusAmount = matchedBonus.getExtraBonusValue();
						bonusRate = ""+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
								" --> "+matchedBonus.getExtraBonusValue()+" "+OCConstants.LOYALTY_TYPE_AMOUNT;
						if(contactsLoyalty.getGiftcardBalance() == null ) {
							contactsLoyalty.setGiftcardBalance(matchedBonus.getExtraBonusValue());
						}
						else{
							contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + matchedBonus.getExtraBonusValue());
						}
						if(contactsLoyalty.getTotalGiftcardAmount() == null){
							contactsLoyalty.setTotalGiftcardAmount(matchedBonus.getExtraBonusValue());
						}
						else{
							contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + matchedBonus.getExtraBonusValue());
						}
						LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, Double.valueOf(matchedBonus.getExtraBonusValue()), OCConstants.LOYALTY_TYPE_AMOUNT, bonusRate);
								
						logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
						createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
								childTxbonus.getTransChildId(),matchedBonus.getThresholdBonusId());
						applyConversionRules(contactsLoyalty, childTxbonus, program, tier);
						tier = applyTierUpgradeRule(contactsLoyalty, program, childTxbonus, tier);
					}
				}
			}
			
			LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
			LoyaltyAutoComm autoComm = getLoyaltyAutoComm(program.getProgramId());
			if(bonusflag && autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
					contactsLoyalty.getContact().getContactId() != null){
				Contacts contact = findContactById(contactsLoyalty.getContact().getContactId());
				if(contact != null && contact.getEmailId() != null){
					autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
							contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
							contact.getContactId(), contactsLoyalty.getLoyaltyId());
				}
			}
			UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			Users user = userDao.findByUserId(contactsLoyalty.getUserId());
			if (user.isEnableSMS() && bonusflag && autoComm != null && autoComm.getThreshBonusSmsTmpltId() != null){
				Long contactId = null;
				if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
					contactId = contactsLoyalty.getContact().getContactId();
				}
				autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, contactId,
							contactsLoyalty.getLoyaltyId(), null);
			}
			logger.info("bonus ...points = "+bonusPoints+" amount = "+bonusAmount);
		}catch(Exception e){
			logger.error("Exception in update threshold bonus...", e);
			throw new LoyaltyProgramException("Exception in threshold bonus...");
		}
		logger.info("Completed updateThresholdBonus method <<<");
	}
	
	private Contacts findContactById(Long cid) throws Exception {
		ContactsDao contactsDao = (ContactsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
		return contactsDao.findById(cid);
	}
	
	private void applyConversionRules(ContactsLoyalty contactsLoyalty, LoyaltyTransactionChild transaction, LoyaltyProgram program, LoyaltyProgramTier tier){
		logger.info(" Entered into applyConversionRules method >>>");
		String[] differenceArr = null;

		try{
			if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)){
				if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
						&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0 
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints()){
				
					differenceArr = new String[3];
					
					double multipledouble = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
					int multiple = (int)multipledouble;
					double convertedAmount = tier.getConvertToAmount() * multiple;
					double subPoints = multiple * tier.getConvertFromPoints();
					
					differenceArr[0] = ""+convertedAmount;
					differenceArr[1] = ""+subPoints;
					differenceArr[2] = tier.getConvertFromPoints()+" Points -> "+tier.getConvertToAmount();
					
					logger.info("multiple factor = "+multiple);
					logger.info("Conversion amount ="+convertedAmount);
					logger.info("subtract points = "+subPoints);
					
					//update giftcard balance
					if(contactsLoyalty.getGiftcardBalance() == null ) {
						contactsLoyalty.setGiftcardBalance(convertedAmount);
					}
					else{
						contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + convertedAmount);
					}
					if(contactsLoyalty.getTotalGiftcardAmount() == null){
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					}
					else{
						contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount);
					}
					
					transaction.setConversionAmt(convertedAmount);
					//deduct loyalty points
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - subPoints);
					contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? subPoints :
						contactsLoyalty.getTotalLoyaltyRedemption() + subPoints);
					
					logger.info("contactsLoyalty.getGiftcardBalance() = "+contactsLoyalty.getGiftcardBalance());
					transaction.setAmountBalance(contactsLoyalty.getLoyaltyBalance());
					transaction.setPointsBalance(contactsLoyalty.getGiftcardBalance());
					transaction.setGiftBalance(contactsLoyalty.getGiftBalance());
					
					// Deduct points or amount from expiry table
					deductPointsFromExpiryTable(contactsLoyalty.getLoyaltyId(), contactsLoyalty.getUserId(), subPoints, convertedAmount);
					logger.info("After conversion rules...subPoints = "+subPoints+" and convertedAmount = "+convertedAmount);
				}
			}
		
		}catch(Exception e){
			logger.error("Exception while applying auto conversion rules...", e);
		}
		logger.info("Completed applyConversionRules method <<<");
	}
	
	private void deductPointsFromExpiryTable(Long loyaltyId, Long userId, double subPoints, double earnedAmt) throws Exception{
		logger.info(" Entered into deductPointsFromExpiryTable method >>>");
		
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; //expiryDao.fetchExpPointsTrans(""+membershipNumber, 100, userId);
		Iterator<LoyaltyTransactionExpiry> iterList = null; //expiryList.iterator();
		LoyaltyTransactionExpiry expiry = null;
		long remainingPoints = (long)subPoints;
		
		do{
			expiryList = expiryDao.fetchExpLoyaltyPtsTrans(loyaltyId, 100, userId);
			if(expiryList == null) break;
			iterList = expiryList.iterator();
			
			while(iterList.hasNext()){
				
				logger.info("remainingPoints = "+remainingPoints +" earnedAmt = "+earnedAmt);
				expiry = iterList.next();
				
				if((expiry.getExpiryPoints() == null || expiry.getExpiryPoints() <= 0) && 
						(expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0)){
					logger.info("Wrong entry condition...");
				}
				else if(expiry.getExpiryPoints() < remainingPoints){
					logger.info("subtracted points = "+expiry.getExpiryPoints());
					remainingPoints = remainingPoints - expiry.getExpiryPoints().longValue();
					expiry.setExpiryPoints(0l);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					continue;
				}
				else if(expiry.getExpiryPoints() >= remainingPoints){
					logger.info("subtracted points = "+expiry.getExpiryPoints());
					expiry.setExpiryPoints(expiry.getExpiryPoints() - remainingPoints);
					remainingPoints = 0;
					if(expiry.getExpiryAmount() == null){
						expiry.setExpiryAmount(earnedAmt);
					}
					else{
						expiry.setExpiryAmount(expiry.getExpiryAmount() + earnedAmt);
					}
					//logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ " earnedAmt = "+earnedAmt);
					//expiryDao.saveOrUpdate(expiry);
					expiryDaoForDML.saveOrUpdate(expiry);
					//logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ " earnedAmt = "+earnedAmt);
					break;
				}
			}
		
		}while(remainingPoints > 0 && expiryList != null);
		logger.info("Completed deductPointsFromExpiryTable method <<<");
	}
	
	
	/**
	 * create an expiry transaction
	 * @param loyalty
	 * @param expiryPoints
	 * @param expiryAmount
	 * @param orgId
	 * @param transChildId
	 * @param transType
	 * @param tier
	 * @return
	 */
	private LoyaltyTransactionExpiry createExpiryTransaction(ContactsLoyalty loyalty,
			Long expiryPoints, Double expiryAmount, Long orgId, Long transChildId,Long bonusId){
		logger.debug("---------Entered createExpiryTransaction-----------");
		LoyaltyTransactionExpiry transaction = null;
		try{
			transaction = new LoyaltyTransactionExpiry();
			transaction.setTransChildId(transChildId);
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setOrgId(orgId);
			transaction.setUserId(loyalty.getUserId());
			transaction.setExpiryPoints(expiryPoints);
			transaction.setExpiryAmount(expiryAmount);
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			transaction.setBonusId(bonusId);
			
			LoyaltyTransactionExpiryDao loyaltyTransactionExpiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
			LoyaltyTransactionExpiryDaoForDML loyaltyTransactionExpiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
			//loyaltyTransactionExpiryDao.saveOrUpdate(transaction);
			loyaltyTransactionExpiryDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception while logging enroll bonus expiry transaction...",e);
		}
		logger.debug("---------Exiting createExpiryTransaction-----------");
		return transaction;
	}//createExpiryTransaction

	/**
	 * create a bonus transaction
	 * @param loyalty
	 * @param earnedValue
	 * @param earnType
	 * @param transactionId
	 * @return
	 */
	private LoyaltyTransactionChild createBonusTransaction(ContactsLoyalty loyalty,	double earnedValue, String earnType, String bonusRate){
		logger.debug("---------Entered createBonusTransaction-----------");
		LoyaltyTransactionChild transaction = null;
		try{
			transaction = new LoyaltyTransactionChild();
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setEarnType(earnType);
			if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
				transaction.setEarnedPoints((double)earnedValue);
			}
			else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
				transaction.setEarnedAmount((double)earnedValue);
			}
			transaction.setEnteredAmount((double)earnedValue);
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_BONUS);
			transaction.setDescription(bonusRate);
			transaction.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_AUTO);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			
			LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//loyaltyTransactionChildDao.saveOrUpdate(transaction);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		logger.debug("---------Exiting createBonusTransaction-----------");
		return transaction;
	}//createBonusTransaction

	/**
	 * get the auto communication obj configured for the program
	 * @param programId
	 * @return
	 */
	private LoyaltyAutoComm getLoyaltyAutoComm(Long programId){
		try{
			LoyaltyAutoCommDao autoCommDao = (LoyaltyAutoCommDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
			return autoCommDao.findById(programId);
		}catch(Exception e){
			logger.error("Exception in getting auto comm object...", e);
			return null;
		}
	}//getLoyaltyAutoComm

	/**
	 * create a LoyaltyTransactionChild object on successful enrollment
	 * @param contactLoyalty
	 * @param transactionId
	 * @param userId
	 * @param orgId
	 * @param programId
	 * @param membershipType
	 * @param desc
	 * @return
	 */
	private LoyaltyTransactionChild createSuccessfulTransaction(ContactsLoyalty contactLoyalty, Long transactionId, Long userId, Long orgId, 
			Long programId, String membershipType, String desc){
		logger.debug("---------Entered createSuccessfulTransaction-----------");
		LoyaltyTransactionChild transaction = null;
		try{
			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(transactionId);
			transaction.setMembershipNumber(""+contactLoyalty.getCardNumber());
			transaction.setMembershipType(membershipType);
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setOrgId(orgId);
			transaction.setAmountBalance(contactLoyalty.getGiftcardBalance());
			transaction.setPointsBalance(contactLoyalty.getLoyaltyBalance());
			transaction.setGiftBalance(contactLoyalty.getGiftBalance());
			transaction.setProgramId(programId);
			transaction.setTierId(contactLoyalty.getProgramTierId());
			transaction.setUserId(userId);
			transaction.setOrgId(contactLoyalty.getOrgId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			transaction.setStoreNumber(contactLoyalty.getPosStoreLocationId());
			if(contactLoyalty.getPosStoreLocationId() != null)
				transaction.setSubsidiaryNumber(getSBS(contactLoyalty.getPosStoreLocationId(), userId, orgId));
			transaction.setCardSetId(contactLoyalty.getCardSetId());
			transaction.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_WEBFORM);
			transaction.setDescription(desc);
			transaction.setLoyaltyId(contactLoyalty.getLoyaltyId());
			
			LoyaltyTransactionChildDao childDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			LoyaltyTransactionChildDaoForDML childDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			//childDao.saveOrUpdate(transaction);
			childDaoForDML.saveOrUpdate(transaction);
		}catch(Exception e){
			logger.error("Exception while creating transaction in child table...", e);
		}
		logger.debug("---------Exiting createSuccessfulTransaction-----------");
		return transaction;
	}//createSuccessfulTransaction

	/**
	 * set data to the new enrolled loyalty membership
	 * @param cardNumber
	 * @param membershipType
	 * @param cardPin
	 * @param phoneNumber
	 * @param optInMedium
	 * @param storeNumber
	 * @param progObj
	 * @param tier
	 * @param cardSetId
	 * @return
	 */
	private ContactsLoyalty prepareLoyaltyMembership(String cardNumber,	String membershipType, String cardPin, String phoneNumber,String emailId,
			String optInMedium, String storeNumber, LoyaltyProgram progObj, LoyaltyProgramTier tier, Long cardSetId) {
		logger.debug("---------Entered prepareLoyaltyMembership-----------");
		ContactsLoyalty contactLoyalty = new ContactsLoyalty();
		contactLoyalty.setCardNumber(cardNumber);
		contactLoyalty.setMembershipType(membershipType);
		contactLoyalty.setCardPin(cardPin);
		contactLoyalty.setMobilePhone(phoneNumber);
		contactLoyalty.setEmailId(emailId);
		contactLoyalty.setCreatedDate(Calendar.getInstance());
		contactLoyalty.setContactLoyaltyType(optInMedium);
		contactLoyalty.setSourceType(Constants.CONTACT_LOYALTY_TYPE_WEBFORM);
		contactLoyalty.setPosStoreLocationId(storeNumber);
		contactLoyalty.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
		contactLoyalty.setMode(OCConstants.LOYALTY_ONLINE_MODE);
		contactLoyalty.setOptinDate(Calendar.getInstance());
		contactLoyalty.setProgramId(progObj.getProgramId());
		contactLoyalty.setCardSetId(cardSetId);
		if(tier != null){
			contactLoyalty.setProgramTierId(tier.getTierId());
		}
		contactLoyalty.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_OC);
		logger.debug("---------Exiting prepareLoyaltyMembership-----------");
		return contactLoyalty;
	}//prepareLoyaltyMembership

	/**
	 * sends welcome email
	 * @param contact
	 * @param templateId
	 * @param user
	 * @throws Exception
	 */
	public void sendWelcomeEmail(Contacts contact, Long templateId, Users user) throws Exception {
		logger.debug("---------Entered sendWelcomeEmail-----------");
		ServiceLocator locator = ServiceLocator.getInstance();
		// to send the loyalty related email
		CustomTemplates custTemplate = null;
		String message = PropertyUtil.getPropertyValueFromDB(OCConstants.WELCOME_MAIL_TEMPLATE);
		CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao) locator.getDAOByName(OCConstants.CUSTOM_TEMP_DAO);
		EmailQueueDao emailQueueDao = (EmailQueueDao) locator.getDAOByName(OCConstants.EMAILQUEUE_DAO);
		EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) locator.getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
		if (templateId != null) {
			custTemplate = customTemplatesDao.findCustTemplateById(templateId);
			if (custTemplate != null) {

				if(custTemplate != null && custTemplate.getHtmlText()!= null) {
					  message = custTemplate.getHtmlText();
				  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null) message = myTemplates.getContent();
				  }
			}
		}
		message = message.replace(OCConstants.ORG_NAME_PLACEHOLDER,	user.getUserOrganization().getOrganizationName()).replace(OCConstants.REPLY_TO_MAIL, user.getEmailId());
		EmailQueue testEmailQueue = new EmailQueue(templateId, Constants.EQ_TYPE_WELCOME_MAIL, message, OCConstants.EMAIL_STATUS_ACTIVE,
				contact.getEmailId(), user, MyCalendar.getNewCalendar(), OCConstants.EMAIL_SUBJECT_WELCOME_EMAIL, null, contact.getFirstName(), null, contact.getContactId());
		// testEmailQueue.setChildEmail(childEmail);
		logger.info("testEmailQueue" + testEmailQueue.getChildEmail());
		//emailQueueDao.saveOrUpdate(testEmailQueue);
		emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
		logger.debug("---------Exiting sendWelcomeEmail-----------");
	}// sendWelcomeEmail

	/**
	 * sends resubscription link
	 * @param tempContactEmailId
	 * @param user
	 */
	private void sendResubscriptionLinkToUser(String tempContactEmailId, Users user) {
		logger.debug("---------Entered sendResubscriptionLinkToUser-----------");
		ServiceLocator locator = ServiceLocator.getInstance();
		try {
			EmailQueueDao emailQueueDao = (EmailQueueDao) locator.getDAOByName(OCConstants.EMAILQUEUE_DAO);
			EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) locator.getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			String href = PropertyUtil.getPropertyValue(OCConstants.RESUBSCRIBE_LINK);
			href = href + "&emailId=" + EncryptDecryptUrlParameters.encrypt(tempContactEmailId) + "&userId="
					+ EncryptDecryptUrlParameters.encrypt(user.getUserId().toString());
			logger.debug("href >>>" + href + " :: and  tempContact.getEmailId() >>"
					+ tempContactEmailId + " :: and userId is .." + user.getUserId());
			// GetUser
			String sendNameStr = user.getUserName();
			sendNameStr = sendNameStr.substring(0,sendNameStr.lastIndexOf("__org__"));
			String tempLateStr = PropertyUtil.getPropertyValueFromDB(OCConstants.RESUBSCRIPTION_TEMPLATE);
			logger.info("==1====>" + tempLateStr
					+ " >>>replacing emailid ===>" + tempContactEmailId
					+ ">>og Name >>" + user.getUserOrganization().getOrganizationName());
			logger.info("====>" + tempLateStr.contains("SubscriberEmailID"));
			tempLateStr = (((((tempLateStr.replace(OCConstants.SUBSCRIBER_MAIL_ID_PH,tempContactEmailId))
					.replace(OCConstants.ORG_NAME_PLACEHOLDER, user.getUserOrganization().getOrganizationName()))
					.replace(OCConstants.URL_PH,href))
					.replace(OCConstants.SENDER_NAME_PH, sendNameStr))
					.replace(OCConstants.ORG_NAME_PLACEHOLDER, user.getUserOrganization().getOrganizationName()));
			logger.debug("tempLateStr message  >>" + tempLateStr);
			// Prepare EmailQueue Object
			EmailQueue emailQueueObj = new EmailQueue(OCConstants.RESUBSCRIPTION_MAIL_SUBJECT,tempLateStr, OCConstants.RESUBSCRIPTION_MAIL_TYPE, 
					OCConstants.EMAIL_STATUS_ACTIVE, tempContactEmailId, Calendar.getInstance(), user);
			//emailQueueDao.saveOrUpdate(emailQueueObj);
			emailQueueDaoForDML.saveOrUpdate(emailQueueObj);
			logger.debug("return from here..");
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("---------Exiting sendResubscriptionLinkToUser-----------");
	} // sendResubscriptionLinkToUser

	/**
	 * set mobile status for new contacts
	 * @param contact
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Contacts setmobilePhoneStatus(Contacts contact, Users user,FormMapping formMapping,boolean isOptinChecked) throws Exception {
		logger.debug("---------Entered setmobilePhoneStatus-----------");
		if(contact.getMobilePhone() != null  ) {
			//ignoring mobile optin for webform enrolled contacts
			logger.info("isOptinChecked "+isOptinChecked);
			if(formMapping.getInputFieldMapping().contains("Loyalty_mobile_optin") && isOptinChecked){
				contact.setMobileOptin(true);
				contact.setMobileOptinDate(now);
				contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
				contact.setMobileOptinSource(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM);
			}
			if(user.isEnableSMS() && user.isConsiderSMSSettings()) contact.setMobileStatus(performMobileOptIn(contact, user));
			else{
				contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
			}
		}else{
			contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
		}
		logger.debug("---------Exiting setmobilePhoneStatus-----------");
		return contact;
	}//setmobilePhoneStatus

	/**
	 * perform mobile optin for existing contact
	 * @param contact
	 * @param mobileStr
	 * @return
	 * @throws Exception
	 */
	public String performMobileOptIn(Contacts contact, String mobileStr ) throws Exception{
		logger.debug("---------Entered performMobileOptIn-----------");
		Users currentUser = contact.getUsers();
		SMSSettings smsSettings = null;
		ServiceLocator locator = ServiceLocator.getInstance();
		ContactsDaoForDML contactsDaoForDML =  (ContactsDaoForDML) locator.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		//UsersDao usersDao = (UsersDao) locator.getDAOByName(OCConstants.USERS_DAO);
		UsersDaoForDML usersDaoForDML = (UsersDaoForDML) locator.getDAOForDMLByName(OCConstants.USERS_DAOForDML);
		SMSSettingsDao smsSettingsDao = (SMSSettingsDao) locator.getDAOByName(OCConstants.SMSSETTINGS_DAO);
		CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway) locator.getBeanByName(OCConstants.CAPTIWAY_TO_SMS_API_GATEWAY);
		if(SMSStatusCodes.smsProgramlookupOverUserMap.get(currentUser.getCountryType())) smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
		else  smsSettings = smsSettingsDao.findByOrg(currentUser.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
		//SMSSettings smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
		if(smsSettings == null ) {
			/*	noSMSComplaincyMsg = ". No SMS Settings find for your user Account," +
									"SMS may not be sent to the mobile contacts.";*/
			contact.setMobileOptin(false);
			return Constants.CON_MOBILE_STATUS_ACTIVE;
		}
		Users settingsUser = smsSettings.getUserId();
		OCSMSGateway ocsmsGateway = GatewayRequestProcessHelper.getOcSMSGateway(settingsUser, 
				SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(settingsUser.getCountryType()));
		if(ocsmsGateway == null) {
			/*	noSMSComplaincyMsg = ". No SMS Settings find for your user Account," +
								"SMS may not be sent to the mobile contacts.";*/
			contact.setMobileOptin(false);
			return Constants.CON_MOBILE_STATUS_ACTIVE;
		}
		boolean isDifferentMobile = false;
		String mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;
		//String mobile = mPhoneIBoxId.getValue()+"";
		String conMobile = contact.getMobilePhone();
		//to identify whether entered one is same as previous mobile
		if(conMobile != null ) {
			if(!mobileStr.equals(conMobile) ) {
				if( (mobileStr.length() < conMobile.length() && !conMobile.endsWith(mobileStr) ) ||
						(conMobile.length() < mobileStr.length() && !mobileStr.endsWith(conMobile)) || mobileStr.length() == conMobile.length()) {
					isDifferentMobile = true;
				}
			}
		}//if
		else{
			contact.setMobilePhone(mobileStr);
			isDifferentMobile = true;
		}
		//contact.setMobilePhone(mobileStr);//can be deleted
		if(!isDifferentMobile && contact.getMobileStatus() != null && contact.getMobileStatus().equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)){
			mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
			return mobileStatus;
		}
		//do only when the existing phone number is not same with the entered
		byte optin = 0;
		if(contact.getOptinMedium() != null) {
			if(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY.equalsIgnoreCase(contact.getOptinMedium()) ) {
				optin = 1;
			}
			else if(contact.getOptinMedium().startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM) ) {
				optin = 2;
			}
			else if(Constants.CONTACT_OPTIN_MEDIUM_POS.equalsIgnoreCase(contact.getOptinMedium()) ) {
				optin = 4;
			}
		}//if
		Byte userOptinVal =	smsSettings.getOptInMedium();
		userOptinVal = ( SMSStatusCodes.userOptinMediumMap.get(currentUser.getCountryType()) &&currentUser.getOptInMedium() != null) ? currentUser.getOptInMedium() : userOptinVal;
		if(smsSettings.isEnable() && userOptinVal != null && (userOptinVal.byteValue() & optin ) > 0 ) {
			if((contact.getLastSMSDate() == null && contact.isMobileOptin() != true) || ( isDifferentMobile) ) {
				mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
				contact.setMobileStatus(mobileStatus);
				contact.setLastSMSDate(Calendar.getInstance());
				//contact.setMobileOptin(true);
				if(!ocsmsGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocsmsGateway, 1)) {
					logger.debug("low credits with clickatell");
					return mobileStatus;
				}
				if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() ) ) >=  1) {
					//UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
					String msgContent = smsSettings.getAutoResponse();
					if(msgContent != null) {
						if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType())){
							msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
						}
						//msgContent = smsSettings.getMessageHeader() == null ? Constants.STRING_NILL : smsSettings.getMessageHeader()  + " "+ msgContent;
					}
					mobileStatus = captiwayToSMSApiGateway.sendSingleMobileDoubleOptin(ocsmsGateway, 
							smsSettings.getSenderId(), mobileStr, msgContent, smsSettings.getUserId());
					if(mobileStatus == null) {
						mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
					}
					if(!mobileStatus.equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)) {
						contactsDaoForDML.updatemobileStatus(mobileStr, mobileStatus, currentUser);
					}
					contact.setMobileStatus(mobileStatus);
					//usersDao.updateUsedSMSCount(currentUser.getUserId(), 1);
					usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), 1);
				}
				else {
					logger.debug("low credits with user...");
					return mobileStatus;
				}
			}//if
		}//if
		else{
			if(contact.getMobilePhone() != null && isDifferentMobile) {
				mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;
				//contact.setMobileStatus(mobileStatus);
				contact.setMobileOptin(false);
			}
		}
		logger.debug("---------Exiting performMobileOptIn-----------");
		return mobileStatus;
	}//performMobileOptIn

	/**
	 * perform mobile optin for new contact
	 * @param contact
	 * @param currentUser
	 * @return
	 * @throws Exception
	 */
	public String performMobileOptIn(Contacts contact, Users currentUser) throws Exception{
		logger.debug("---------Entered performMobileOptIn-----------");
		//enabled with smsDouble-optin and medium is matched  
		ServiceLocator locator = ServiceLocator.getInstance();
		SMSSettings smsSettings = null;
		//UsersDao usersDao = (UsersDao) locator.getDAOByName(OCConstants.USERS_DAO);
		UsersDaoForDML usersDaoForDML = (UsersDaoForDML) locator.getDAOForDMLByName(OCConstants.USERS_DAOForDML);
		SMSSettingsDao smsSettingsDao = (SMSSettingsDao) locator.getDAOByName(OCConstants.SMSSETTINGS_DAO);
		CaptiwayToSMSApiGateway captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway) locator.getBeanByName(OCConstants.CAPTIWAY_TO_SMS_API_GATEWAY);
		if(SMSStatusCodes.smsProgramlookupOverUserMap.get(currentUser.getCountryType())) smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
		else  smsSettings = smsSettingsDao.findByOrg(currentUser.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
		if(smsSettings == null ) {
			contact.setMobileOptin(false);
			return Constants.CON_MOBILE_STATUS_ACTIVE;
		}
		//SMSSettings smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
		Users settingsUser = smsSettings.getUserId();
		OCSMSGateway ocsmsGateway = GatewayRequestProcessHelper.getOcSMSGateway(settingsUser, 
				SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(settingsUser.getCountryType()));
		ContactsDaoForDML contactsDaoForDML = (ContactsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);
		if( ocsmsGateway == null) {
			contact.setMobileOptin(false);
			return Constants.CON_MOBILE_STATUS_ACTIVE;
		}
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
		Byte userOptinVal =	smsSettings.getOptInMedium();
		userOptinVal = ( SMSStatusCodes.userOptinMediumMap.get(currentUser.getCountryType()) &&currentUser.getOptInMedium() != null) ? currentUser.getOptInMedium() : userOptinVal;
		if(smsSettings.isEnable() && userOptinVal != null && (userOptinVal.byteValue() & optin ) > 0 ) {
			if(contact.getLastSMSDate() == null && contact.isMobileOptin() != true) {
				mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
				contact.setMobileStatus(mobileStatus);
				contact.setLastSMSDate(Calendar.getInstance());
				//contact.setMobileOptin(true);
				if(!ocsmsGateway.isPostPaid() && !captiwayToSMSApiGateway.getBalance(ocsmsGateway, 1)) {
					logger.debug("low credits with clickatell");
					return mobileStatus;
				}
				if( (  (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) - (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() ) ) >=  1) {
					//UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
					String msgContent = smsSettings.getAutoResponse();
					if(msgContent != null) {
						if(SMSStatusCodes.optOutFooterMap.get(currentUser.getCountryType())){
							msgContent = smsSettings.getMessageHeader() + " "+ msgContent;
						}
						//msgContent = smsSettings.getMessageHeader() == null ? Constants.STRING_NILL : smsSettings.getMessageHeader()  + " "+ msgContent;
					}
					mobileStatus = captiwayToSMSApiGateway.sendSingleMobileDoubleOptin(ocsmsGateway, 
							smsSettings.getSenderId(), phone, msgContent, smsSettings.getUserId());
					if(mobileStatus == null) {
						mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
					}
					if(!mobileStatus.equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)) {
						contactsDaoForDML.updatemobileStatus(phone, mobileStatus, currentUser);
					}
					contact.setMobileStatus(mobileStatus);
					//usersDao.updateUsedSMSCount(currentUser.getUserId(), 1);
					usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), 1);
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
		logger.debug("---------Exiting performMobileOptIn-----------");
		return mobileStatus;
	}//performMobileOptIn

	/**
	 * Prepares the final ResponseObject
	 * @param msg
	 * @param dbErrmsg
	 * @param parentalSucessMsg
	 * @param hId
	 * @param url
	 * @param formMapping
	 * @return
	 */
	public BaseResponseObject setResponseObject(String msg, String dbErrmsg, String parentalSucessMsg, String hId, String url,
			FormMapping formMapping) {
		logger.debug("---------Entered setResponseObject-----------");
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		try {
			logger.debug("***** hid ******** "+ hId + " ****** msg ******* "+ msg + " ********** dbErrmsg **** "+ dbErrmsg + "********* parentalSucessMsg ****** " +parentalSucessMsg );
			if (hId != null	&& msg.length() == 0 && (dbErrmsg.length() == 0 || dbErrmsg.contains("for webFeedBack")) && parentalSucessMsg.length() == 0) {
				String returnURLStr = formMapping.getHtmlRedirectURL() != null ? formMapping.getHtmlRedirectURL() : url;
				logger.debug("msg is = " + msg + " "+ "Redirecting user to success:" + returnURLStr);
				baseResponseObject.setAction(returnURLStr);
			} else if (hId != null && parentalSucessMsg.length() > 0) {
				String returnParentalURLStr = formMapping.getHtmlRedirectParentalURL() != null ? formMapping.getHtmlRedirectParentalURL() : url;
				logger.debug("msg is = " + msg + " "+ "Redirecting user to parental success:"+ returnParentalURLStr);
				baseResponseObject.setAction(returnParentalURLStr);
			} else if (hId != null && msg.length() == 0 && dbErrmsg.length() > 0 && parentalSucessMsg.length() == 0) {
				String returnDBFailureUrl = formMapping.getHtmlRedirectDbFailureURL() != null ? formMapping.getHtmlRedirectDbFailureURL() : url;
				logger.debug("msg is = " + msg + " ; And dbError : "+ " ; AND Redirecting user to failure:"	+ returnDBFailureUrl);
				baseResponseObject.setAction(returnDBFailureUrl);
			} else if (hId != null && msg.length() > 0 && parentalSucessMsg.length() == 0) {
				String returnFailureUrl = formMapping.getHtmlRedirectFailureURL() != null ? formMapping.getHtmlRedirectFailureURL() : url;
				logger.debug("msg is = " + msg + " "+ "Redirecting user to failure:"+ returnFailureUrl);
				baseResponseObject.setAction(returnFailureUrl);
			} 
		} catch (Exception e) {
			logger.error("****Error occured while redirecting user to URL.",e);
			baseResponseObject.setResponseStr("****Error occured while redirecting user to URL.");
			return baseResponseObject;
		}
		logger.debug("---------Exiting setResponseObject-----------");
		return baseResponseObject;
	}//setResponseObject
	
	public Map<String, String> getCustomerRatingAndFeedback(HashMap<String, String> formMapValuesHM,HashMap<String, List> inputMapSettingHM) {
		logger.debug("---------Entered getCustomerRatingAndFeedback-----------");
		Set<String> valueKeys = formMapValuesHM.keySet();
		String methodNameStr = "";
		String feedbackMessage = "";
		String feedbackStore = "";
		Map<String,String> ratingAndFeedback = new HashMap<String, String>();
		for (String vkey : valueKeys) {
			String fieldVal = formMapValuesHM.get(vkey);
			if (!inputMapSettingHM.containsKey(vkey)) {
				logger.debug("Input Field :" + vkey + " Not found");
				continue;
			} // if
			List tempList = inputMapSettingHM.get(vkey);
			if (tempList.size() == 2) {
				continue;
			}
			methodNameStr = tempList.get(0).toString();
				for(int i = 0; i <= 20; i++) {
					String rating = "";
					if (methodNameStr.equalsIgnoreCase("Rating"+i+"")) {
						rating = fieldVal.trim();
						ratingAndFeedback.put("Rating"+i+"", rating);
						continue;
					}
				} 
				if (methodNameStr.equalsIgnoreCase("FeedBackMessage")) {
				feedbackMessage = fieldVal.trim();
				continue;
			}
				if (methodNameStr.equalsIgnoreCase("feedBackStore")) {
					feedbackStore = fieldVal.trim();
					continue;
				}
		}// for
		ratingAndFeedback.put("feedBackStore",feedbackStore);	
		ratingAndFeedback.put("FeedBackMessage",feedbackMessage);
		logger.debug("---------Exiting getCustomerRatingAndFeedback-----------");
		return ratingAndFeedback;
	
	}

	public void sendFeedBackMailOnSuccessfulSubmission(Long templateId, Users user, Contacts contact) throws Exception {
		logger.debug("---------Entered FeedBackMailOnSuccessfulSubmission-----------");
		ServiceLocator locator = ServiceLocator.getInstance();
		CustomTemplates custTemplate = null;
		String message = null;
		CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao) locator.getDAOByName(OCConstants.CUSTOM_TEMP_DAO);
		EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) locator.getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
		if (templateId != null) {
			custTemplate = customTemplatesDao.findCustTemplateById(templateId);
			if (custTemplate != null) {
				if(custTemplate != null && custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
				  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null) message = myTemplates.getContent();
				  }
			}
		}
		if(message!=null && !message.isEmpty()) {
			message = message.replace("[Organization Name]",user.getUserOrganization().getOrganizationName()).replace(OCConstants.REPLY_TO_MAIL, user.getEmailId());
			EmailQueue testEmailQueue = new EmailQueue(templateId, Constants.EQ_TYPE_FEEDBACK_MAIL, message, OCConstants.EMAIL_STATUS_ACTIVE,
					contact.getEmailId(), user, MyCalendar.getNewCalendar(), OCConstants.EMAIL_SUBJECT_FEEDBACK_EMAIL, null, contact.getFirstName(), null, contact.getContactId());
			logger.info("testEmailQueue" + testEmailQueue.getChildEmail());
			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
			logger.debug("---------Exiting FeedBackMailOnSuccessfulSubmission-----------");
		}else {
			logger.info("contactSubscriptionProcessHelper :: Email Content is empty in feedbackmail");
		}
			
	}
	
	public void sendSimpleSignUpMailOnSuccessfulSubmission(Long templateId, Users user, Contacts contact) throws Exception {
		logger.debug("---------Entered sendSimpleSignUpMailOnSuccessfulSubmission-----------");
		ServiceLocator locator = ServiceLocator.getInstance();
		CustomTemplates custTemplate = null;
		String message = null;
		CustomTemplatesDao customTemplatesDao = (CustomTemplatesDao) locator.getDAOByName(OCConstants.CUSTOM_TEMP_DAO);
		EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML) locator.getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
		if (templateId != null) {
			custTemplate = customTemplatesDao.findCustTemplateById(templateId);
			if (custTemplate != null) {
				if(custTemplate != null && custTemplate.getHtmlText()!= null && !custTemplate.getHtmlText().isEmpty()) {
					  message = custTemplate.getHtmlText();
				  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplatesDao myTemplatesDao = (MyTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MYTEMPLATES_DAO);
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null) message = myTemplates.getContent();
				  }
			}
		}
		if(message!=null && !message.isEmpty()) {
			message = message.replace("[Organization Name]",user.getUserOrganization().getOrganizationName()).replace(OCConstants.REPLY_TO_MAIL, user.getEmailId());
			EmailQueue testEmailQueue = new EmailQueue(templateId, Constants.EQ_TYPE_WELCOME_MAIL, message, OCConstants.EMAIL_STATUS_ACTIVE,
					contact.getEmailId(), user, MyCalendar.getNewCalendar(), OCConstants.EMAIL_SUBJECT_WELCOME_EMAIL, null, contact.getFirstName(), null, contact.getContactId());
			logger.info("testEmailQueue" + testEmailQueue.getChildEmail());
			emailQueueDaoForDML.saveOrUpdate(testEmailQueue);
			logger.debug("---------Exiting sendSimpleSignUpMailOnSuccessfulSubmission-----------");
		}else {
			logger.info("contactSubscriptionProcessHelper :: Email Content is empty in simple sign up");
		}
			
	}
	
	
	
	

	public void sendFeedBackSMSOnSuccessfulSubmission(FormMapping formMapping,Long contactId,String toContact) {
		logger.info("Entered sendFeedBackSMSOnSuccessfulSubmission...");
		try{
			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(formMapping.getUsers().getUserId());
			AutoSMS autoSms = null;
			OCSMSGateway ocSMSGateway = null;
			UserSMSGateway userSMSGateway = null;
			String message = "";
			String senderId = "";
			
			logger.info("autoSms is "+autoSms);
			if(formMapping != null && formMapping.getFeedBackSmsTemplateId() != null){
				autoSms = autoSmsDao.getAutoSmsTemplateById(formMapping.getFeedBackSmsTemplateId()); 
				senderId = autoSms.getSenderId();
				message = autoSms.getMessageContent();
				logger.info("auto sms template Id : "+autoSms.getTemplateRegisteredId());
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", formMapping.getUsers().getUserOrganization().getOrganizationName());	
					}	
				}

			}

			if(formMapping == null || formMapping.getFeedBackSmsTemplateId() == null || autoSms == null){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(formMapping.getUsers().getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(formMapping.getUsers().getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.FEEDBACK_WEBFORM);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", formMapping.getUsers().getUserOrganization().getOrganizationName());	
				}
			}
			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(formMapping.getUsers().getCountryType());
			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.FEEDBACK_WEBFORM
					, "Active", toContact, 
					accountType, senderId, Calendar.getInstance(), formMapping.getUsers().getUserId(),contactId, formMapping.getId());
			logger.info("auto sms template Id : "+autoSms.getTemplateRegisteredId());
			logger.info("autoSms template Id :"+autoSms.getAutoSmsId());

			if (autoSms != null && autoSms.getTemplateRegisteredId() != null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());
			}
			if (autoSms != null && autoSms.getAutoSmsId() != null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
		}catch(Exception e){
			logger.error("Exception in sendFeedBackSMSOnSuccessfulSubmission.."+e);
		}

	
	}
	
	public void sendSimpleSignUpSMSOnSuccessfulSubmission(FormMapping formMapping,Long contactId,String toContact) {
		logger.info("Entered sendSimpleSignUpOnSuccessfulSubmission...");
		try{
			OCSMSGatewayDao oCSMSGatewayDao = (OCSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.OCSMSGATEWAY_DAO);
			UserSMSGatewayDao userSmsGatewayDao = (UserSMSGatewayDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERSMSGATEWAY_DAO);
			AutoSMSDao autoSmsDao = (AutoSMSDao)ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			AutoSmsQueueDaoForDML smsQueueDaoForDML = (AutoSmsQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.AUTO_SMS_QUEUE_DAO_FOR_DML);
			SMSSettingsDao smsSettingsDao = (SMSSettingsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SMSSETTINGS_DAO);
			SMSSettings usersmsSettings =  smsSettingsDao.findHeaderbyUser(formMapping.getUsers().getUserId());
			AutoSMS autoSms = null;
			OCSMSGateway ocSMSGateway = null;
			UserSMSGateway userSMSGateway = null;
			String message = "";
			String senderId = "";
			
			logger.info("autoSms is "+autoSms);
			if(formMapping != null && formMapping.getSimpleSignUpSmsTemplateId() != null){
				autoSms = autoSmsDao.getAutoSmsTemplateById(formMapping.getSimpleSignUpSmsTemplateId()); 
				senderId = autoSms.getSenderId();
				message = autoSms.getMessageContent();
				if(message.contains("[Organization Name]")){
					if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
					{
						message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
					}else{
						message = message.replace("[Organization Name]", formMapping.getUsers().getUserOrganization().getOrganizationName());	
					}	
				}

			}

			if(formMapping == null || formMapping.getSimpleSignUpSmsTemplateId() == null || autoSms == null){
				String senderIdWithOrWithoutComma = null;

				userSMSGateway = userSmsGatewayDao.findByUserId(formMapping.getUsers().getUserId(), SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(formMapping.getUsers().getCountryType()));
				ocSMSGateway = oCSMSGatewayDao.findById(userSMSGateway.getGatewayId());
				senderIdWithOrWithoutComma =ocSMSGateway.getSenderId();
				String[] senderIdArr;
				senderIdArr = senderIdWithOrWithoutComma.split(",");
				senderId = senderIdArr[0];
				senderId = senderId.trim();
				message = PropertyUtil.getPropertyValueFromDB(OCConstants.WELCOMESMS);
				if(usersmsSettings != null && usersmsSettings.getMessageHeader() != null)
				{
					message = message.replace("[Organization Name]", usersmsSettings.getMessageHeader());	
				}else{
					message = message.replace("[Organization Name]", formMapping.getUsers().getUserOrganization().getOrganizationName());	
				}
			}
			//String senderId = "";
			String accountType = null;

			accountType = SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(formMapping.getUsers().getCountryType());
			AutoSmsQueue autoSmsQueue = new AutoSmsQueue(message, OCConstants.WELCOMESMS
					, "Active", toContact, 
					accountType, senderId, Calendar.getInstance(), formMapping.getUsers().getUserId(),contactId, formMapping.getId());
			if (autoSms != null && autoSms.getTemplateRegisteredId()  != null) {
				autoSmsQueue.setTemplateRegisteredId(autoSms.getTemplateRegisteredId());;
			}
			if (autoSms != null && autoSms.getAutoSmsId() != null) {
				autoSmsQueue.setTemplateId(autoSms.getAutoSmsId());
			}
			smsQueueDaoForDML.saveOrUpdate(autoSmsQueue);
		}catch(Exception e){
			logger.error("Exception in sendSimpleSignUpOnSuccessfulSubmission "+e);
		}

	
	}
	
	private boolean isValidNumber(String loyaltyPoints) {
		try {
			Long.parseLong(loyaltyPoints);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	
	public boolean checkIfDouble(String inputString) {
		try {
			Double.parseDouble(inputString);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}
	private void updateThresholdBonus(ContactsLoyalty contactsLoyalty, LoyaltyProgram program, Double fromLtyBalance, 
			Double fromAmtBalance,Double fromCPVBalance,Double fromLPVBalance, LoyaltyProgramTier loyaltyProgramTier) throws Exception {
		logger.debug(">>>>>>>>>>>>> entered in updateThresholdBonus");
		try{
			LoyaltyThresholdBonusDao loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			List<LoyaltyThresholdBonus> threshBonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(program.getProgramId(), 'N' );
			List<LoyaltyThresholdBonus> pointsBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> amountBonusList = new ArrayList<LoyaltyThresholdBonus>();
			List<LoyaltyThresholdBonus> LPVBonusList = new ArrayList<LoyaltyThresholdBonus>();
			
			fromAmtBalance = fromAmtBalance == null ? 0.0 : fromAmtBalance;
			fromLtyBalance = fromLtyBalance == null ? 0.0 : fromLtyBalance;
			fromLPVBalance = fromLPVBalance == null ? 0.0 : fromLPVBalance;
			fromCPVBalance = fromCPVBalance == null ? 0.0 : fromCPVBalance;

			
			//String[] bonusArr = null; //new String[2];
			if(threshBonusList != null && threshBonusList.size()>0){
				for(LoyaltyThresholdBonus bonus : threshBonusList){
					if(bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_POINTS)){
						pointsBonusList.add(bonus);
					}
					else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
						amountBonusList.add(bonus);
					}
					else if (bonus.getEarnedLevelType().equals(OCConstants.LOYALTY_TYPE_LPV)){
						LPVBonusList.add(bonus);
					}
				}

				List<LoyaltyThresholdBonus> matchedBonusList = new ArrayList<LoyaltyThresholdBonus>();

				
				if (pointsBonusList.size() > 0) {
					Collections.sort(pointsBonusList, new Comparator<LoyaltyThresholdBonus>(){
						@Override
						public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
							return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
						}
					});
				}
				
				if (amountBonusList.size() > 0) {
					Collections.sort(amountBonusList, new Comparator<LoyaltyThresholdBonus>(){
						@Override
						public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
							return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
						}
					});
				}

				if (LPVBonusList.size() > 0) {
					Collections.sort(LPVBonusList, new Comparator<LoyaltyThresholdBonus>() {
						@Override
						public int compare(LoyaltyThresholdBonus ltb1, LoyaltyThresholdBonus ltb2) {
							return ltb1.getEarnedLevelValue().compareTo(ltb2.getEarnedLevelValue());
						}
					});
				}
				matchedBonusList.addAll(LPVBonusList);
				matchedBonusList.addAll(pointsBonusList);
				matchedBonusList.addAll(amountBonusList);
				

				/*if(contactsLoyalty.getTotalLoyaltyEarned() != null && contactsLoyalty.getTotalLoyaltyEarned() > 0){
					for(LoyaltyThresholdBonus bonus : pointsBonusList){
						if(contactsLoyalty.getTotalLoyaltyEarned() >= bonus.getEarnedLevelValue() && 
								(fromLtyBalance == null || fromLtyBalance.doubleValue() < bonus.getEarnedLevelValue())){
							matchedBonusList.add(bonus);
						}
					}

				}
				if(contactsLoyalty.getTotalGiftcardAmount() != null && contactsLoyalty.getTotalGiftcardAmount() > 0){
					for(LoyaltyThresholdBonus bonus : amountBonusList){
						if(contactsLoyalty.getTotalGiftcardAmount() >= bonus.getEarnedLevelValue() && 
								(fromAmtBalance == null || fromAmtBalance.doubleValue() < bonus.getEarnedLevelValue())){
							matchedBonusList.add(bonus);
						}
					}
				}
*/
				long bonusPoints = 0;
				double bonusAmount = 0.0;
				String bonusRate = null;
				boolean bonusflag =false;
				
				if(matchedBonusList != null && matchedBonusList.size() > 0){
					for (LoyaltyThresholdBonus matchedBonus : matchedBonusList) {
						
						bonusflag = false;
						long multiplier = -1;
						
						double afterBalLoyaltyEarned= contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.0 : contactsLoyalty.getTotalLoyaltyEarned();
						double afterBalGiftCardAmt= contactsLoyalty.getTotalGiftcardAmount() == null ? 0.0 : contactsLoyalty.getTotalGiftcardAmount();
						double afterBalLPV= LoyaltyProgramHelper.getLPV(contactsLoyalty);
						double afterBalCPV= contactsLoyalty.getCummulativePurchaseValue() == null ? 0.0 : contactsLoyalty.getCummulativePurchaseValue();
						
						
						if (OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getEarnedLevelType())) {
							logger.info("---------POINTS-----------");
							logger.info("previous points balance (fromLtyBalance)"+fromLtyBalance);
							logger.info("after points balance (getEarnedLevelValue())"+matchedBonus.getEarnedLevelValue());
							
							//This code is for recurring bonus
							if(matchedBonus.isRecurring()){
								
								Double beforeFactor = fromLtyBalance.doubleValue()/matchedBonus.getEarnedLevelValue();
								Double afterFactor = afterBalLoyaltyEarned/matchedBonus.getEarnedLevelValue();
								if(beforeFactor.intValue() < afterFactor.intValue()) {
									bonusflag = true;
									multiplier = afterFactor.intValue()-beforeFactor.intValue();
								}
								logger.info("before factor===="+beforeFactor);
								logger.info("after factor===="+afterFactor);
								logger.info("multiplier===="+multiplier);
							}
							else if (! matchedBonus.isRecurring() && afterBalLoyaltyEarned >= matchedBonus.getEarnedLevelValue()
									&& (fromLtyBalance == null || fromLtyBalance.doubleValue() < matchedBonus.getEarnedLevelValue())) {
								multiplier = 1;
								bonusflag = true;
							}
						}else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getEarnedLevelType())) {
							
							logger.info("---------AMOUNT-----------");
							logger.info("previous points balance (fromAmtBalance)"+fromAmtBalance);
							logger.info("after points balance (getEarnedLevelValue())"+matchedBonus.getEarnedLevelValue());
							
							if(matchedBonus.isRecurring()){
								
								Double beforeFactor = fromAmtBalance.doubleValue()/matchedBonus.getEarnedLevelValue();
								Double afterFactor = afterBalGiftCardAmt/matchedBonus.getEarnedLevelValue();
								if(beforeFactor.intValue() < afterFactor.intValue()){
									bonusflag = true;
									multiplier = afterFactor.intValue()-beforeFactor.intValue();
								}
								logger.info("before factor===="+beforeFactor);
								logger.info("after factor===="+afterFactor);
								logger.info("multiplier===="+multiplier);
							
							}else if (! matchedBonus.isRecurring() && afterBalGiftCardAmt >= matchedBonus.getEarnedLevelValue()
									&& (fromAmtBalance == null || fromAmtBalance.doubleValue() < matchedBonus.getEarnedLevelValue())) {
								
								multiplier = 1;
								bonusflag = true;
							}
							
						}else if(OCConstants.LOYALTY_TYPE_LPV.equals(matchedBonus.getEarnedLevelType())) {
							
							logger.info("---------LPV-----------");
							logger.info("previous points balance (fromLPVBalance)"+fromLPVBalance);
							logger.info("after points balance (getEarnedLevelValue())"+matchedBonus.getEarnedLevelValue());
							
							/*if(matchedBonus.isRecurring()){
								multiplier = LoyaltyProgramHelper.calculateMultiplier(contactsLoyalty, fromCPVBalance, afterBalCPV, matchedBonus.getEarnedLevelValue());
								if(multiplier>0) bonusflag = true;
								logger.info("multiplier===="+multiplier);
							}else if (! matchedBonus.isRecurring() && afterBalCPV >= matchedBonus.getEarnedLevelValue()
									&& (fromCPVBalance == null || fromCPVBalance.doubleValue() < matchedBonus.getEarnedLevelValue())) {
								
								bonusflag = true;
								
							}*/
							multiplier = LoyaltyProgramHelper.doIssueBonus(contactsLoyalty, fromLPVBalance, afterBalLPV, 
									matchedBonus.getEarnedLevelValue(), matchedBonus);
							logger.info("last threshold after bonus ----"+contactsLoyalty.getLastThreshold()==null?0:contactsLoyalty.getLastThreshold());
							if(matchedBonus.isRecurring() && multiplier > 0){
								
								bonusflag = true;
								logger.info("multiplier===="+multiplier);
							
							}else if (! matchedBonus.isRecurring() && multiplier ==0 ) {
								multiplier = 1;
								bonusflag = true;
							}
							
						}
						if(!bonusflag) continue;
						if(OCConstants.LOYALTY_TYPE_POINTS.equals(matchedBonus.getExtraBonusType())){
							

							if (contactsLoyalty.getLoyaltyBalance() == null) {
								contactsLoyalty.setLoyaltyBalance(multiplier*matchedBonus.getExtraBonusValue());
							} else {
								contactsLoyalty.setLoyaltyBalance(
										contactsLoyalty.getLoyaltyBalance() + (multiplier*matchedBonus.getExtraBonusValue()));
							}
							if (contactsLoyalty.getTotalLoyaltyEarned() == null) {
								contactsLoyalty.setTotalLoyaltyEarned(multiplier*matchedBonus.getExtraBonusValue());
							} else {
								contactsLoyalty.setTotalLoyaltyEarned(
										contactsLoyalty.getTotalLoyaltyEarned() +(multiplier* matchedBonus.getExtraBonusValue()));
							}
							bonusPoints = multiplier*matchedBonus.getExtraBonusValue().longValue();
							
						
							bonusRate = Constants.STRING_NILL + matchedBonus.getEarnedLevelValue() + " "
									+ matchedBonus.getEarnedLevelType() + " --> " + matchedBonus.getExtraBonusValue() + " "
									+ matchedBonus.getExtraBonusType();
							
							LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, 
									bonusPoints, OCConstants.LOYALTY_TYPE_POINTS, bonusRate);

							logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
							createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
									childTxbonus.getTransChildId(),matchedBonus.getThresholdBonusId());
							if(loyaltyProgramTier != null){
								// CALL CONVERSION
								//applyConversionRules(contactsLoyaltyObj, childTxbonus, program, loyaltyProgramTier);
								// CALL TIER UPGD
								//loyaltyProgramTier = applyTierUpgradeRule(contactsLoyaltyObj, program, childTxbonus, loyaltyProgramTier);
								Long pointsDifference = 0l;
								Double amountDifference = 0.0;
								String[] diffBonArr = applyConversionRules(contactsLoyalty, loyaltyProgramTier); 
								logger.info("balances After conversion rules updatation --  points = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
								
								String conversionBonRate = null;
								long convertBonPoints = 0;
								double convertBonAmount = 0;
								if(diffBonArr != null){
									convertBonAmount = Double.valueOf(diffBonArr[0].trim());
									convertBonPoints = Double.valueOf(diffBonArr[1].trim()).longValue();
									conversionBonRate = diffBonArr[2];
								}
								pointsDifference = bonusPoints - convertBonPoints;
								amountDifference = (double)bonusAmount + (diffBonArr != null ? Double.parseDouble(diffBonArr[0].trim()) : 0.0);
								loyaltyProgramTier = applyTierUpgradeRule(contactsLoyalty, program, childTxbonus, loyaltyProgramTier);
								String description2 = null;
								updatePurchaseTransaction(childTxbonus, contactsLoyalty, ""+pointsDifference, ""+amountDifference, conversionBonRate, convertBonAmount, loyaltyProgramTier);
							
							
							}
						}
						else if(OCConstants.LOYALTY_TYPE_AMOUNT.equals(matchedBonus.getExtraBonusType())){
							

							
							String result = Utility.truncateUptoTwoDecimal(multiplier*matchedBonus.getExtraBonusValue());
							if (result != null)
								bonusAmount = Double.parseDouble(result);
							bonusRate = Constants.STRING_NILL + matchedBonus.getEarnedLevelValue() + " "
									+ matchedBonus.getEarnedLevelType() + " --> " + matchedBonus.getExtraBonusValue() + " "
									+ matchedBonus.getExtraBonusType();
							if (contactsLoyalty.getGiftcardBalance() == null) {
								// contactsLoyalty.setGiftcardBalance(matchedBonus.getExtraBonusValue());
								contactsLoyalty.setGiftcardBalance(bonusAmount);
							} else {
								// contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() +
								// matchedBonus.getExtraBonusValue());
								contactsLoyalty.setGiftcardBalance(
										new BigDecimal(contactsLoyalty.getGiftcardBalance() + bonusAmount)
												.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							}
							if (contactsLoyalty.getTotalGiftcardAmount() == null) {
								// contactsLoyalty.setTotalGiftcardAmount(matchedBonus.getExtraBonusValue());
								contactsLoyalty.setTotalGiftcardAmount(bonusAmount);
							} else {
								// contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount()
								// + matchedBonus.getExtraBonusValue());
								contactsLoyalty.setTotalGiftcardAmount(
										new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + bonusAmount)
												.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
							}
							
							
							//bonusAmount = matchedBonus.getExtraBonusValue();
							bonusRate = ""+matchedBonus.getEarnedLevelValue()+" "+matchedBonus.getEarnedLevelType()+
									" --> "+matchedBonus.getExtraBonusValue()+" "+OCConstants.LOYALTY_TYPE_AMOUNT;

							
							LoyaltyTransactionChild childTxbonus = createBonusTransaction(contactsLoyalty, 
									bonusAmount, OCConstants.LOYALTY_TYPE_AMOUNT, bonusRate);


							logger.info("balances before balance object = "+contactsLoyalty.getLoyaltyBalance()+" currency = "+contactsLoyalty.getGiftcardBalance());
							createExpiryTransaction(contactsLoyalty, bonusPoints, bonusAmount, contactsLoyalty.getOrgId(), 
									childTxbonus.getTransChildId(),matchedBonus.getThresholdBonusId());
							/*if(loyaltyProgramTier != null){
								// CALL CONVERSION
								applyConversionRules(contactsLoyalty, childTxbonus, program, loyaltyProgramTier);
								// CALL TIER UPGD
								loyaltyProgramTier = applyTierUpgradeRule(contactsLoyalty, program, childTxbonus, loyaltyProgramTier);
							}*/
						}
						
						LoyaltyAutoCommGenerator autoCommGen = new LoyaltyAutoCommGenerator();
						Contacts contact = null;
						LoyaltyAutoComm autoComm = getLoyaltyAutoComm(program.getProgramId());
						if(bonusflag && autoComm != null && autoComm.getThreshBonusEmailTmpltId() != null && contactsLoyalty.getContact() != null &&
								contactsLoyalty.getContact().getContactId() != null){
							contact = findContactById(contactsLoyalty.getContact().getContactId());
							if(contact != null && contact.getEmailId() != null){
								autoCommGen.sendEarnBonusTemplate(autoComm.getThreshBonusEmailTmpltId(), ""+contactsLoyalty.getCardNumber(),
										contactsLoyalty.getCardPin(), contact.getUsers(), contact.getEmailId(), contact.getFirstName(),
										contact.getContactId(), contactsLoyalty.getLoyaltyId());
							}
						}
						UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
						Users user = userDao.findByUserId(contactsLoyalty.getUserId());
						if(user.isEnableSMS() && bonusflag && autoComm != null && autoComm.getThreshBonusSmsTmpltId() != null) { 
							Long contactId = null;	
							if(contactsLoyalty.getContact() != null && contactsLoyalty.getContact().getContactId() != null) {
								contactId = contactsLoyalty.getContact().getContactId();
							}
							autoCommGen.sendEarnBonusSMSTemplate(autoComm.getThreshBonusSmsTmpltId(), user, contactId,
									contactsLoyalty.getLoyaltyId(), null);
						}
						
						
						
						
						
					}
				}

				

			}
			else{
				logger.error("Thershold bonus is Null");
			}
			logger.debug("<<<<<<<<<<<<< completed updateThresholdBonus");
			//return bonusArr;
		}catch(Exception e){
			logger.error("Exception in update threshold bonus...", e);
			throw new LoyaltyProgramException("Exception in threshold bonus...");
		}
	}//updateThresholdBonus

	
	private void updatePurchaseTransaction(LoyaltyTransactionChild transaction, ContactsLoyalty loyalty,
			String ptsDiff, String amtDiff, String conversionRate, double convertAmt, LoyaltyProgramTier tier){
		try{
			transaction.setAmountDifference(Utility.truncateUptoTwoDecimal(Double.parseDouble(amtDiff)));
			transaction.setPointsDifference(ptsDiff);
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setDescription(conversionRate);
			transaction.setConversionAmt(convertAmt);
			transaction.setTierId(tier.getTierId());
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
			
		}
		
		
	}
	private String[] applyConversionRules(ContactsLoyalty contactsLoyalty, LoyaltyProgramTier tier) {
		
		String[] differenceArr = null;

		try{
			
			if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)){
				
				if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0 
						&& contactsLoyalty.getLoyaltyBalance() != null && contactsLoyalty.getLoyaltyBalance() > 0 
						&& contactsLoyalty.getLoyaltyBalance() >= tier.getConvertFromPoints()){
				
					differenceArr = new String[3];
										
					double multipledouble = contactsLoyalty.getLoyaltyBalance()/tier.getConvertFromPoints();
					int multiple = (int)multipledouble;
					//double convertedAmount = tier.getConvertToAmount() * multiple;
					double convertedAmount = 0.0;
					String result = Utility.truncateUptoTwoDecimal(tier.getConvertToAmount() * multiple);
					if(result != null)
						convertedAmount = Double.parseDouble(result);
					//double subPoints = multiple * tier.getConvertFromPoints();
					String res = Utility.truncateUptoTwoDecimal(multiple * tier.getConvertFromPoints());
					double subPoints = 0.0;
					if(res != null)
						subPoints = Double.parseDouble(res);
					
					differenceArr[0] = ""+convertedAmount;
					differenceArr[1] = ""+subPoints;
					differenceArr[2] = tier.getConvertFromPoints().intValue()+" Points -> "+tier.getConvertToAmount().intValue();
					
					logger.info("multiple factor = "+multiple);
					logger.info("Conversion amount ="+convertedAmount);
					logger.info("subtract points = "+subPoints);
					
					
					//update giftcard balance
					if(contactsLoyalty.getGiftcardBalance() == null ) {
						contactsLoyalty.setGiftcardBalance(convertedAmount);
					}
					else{
						//contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + convertedAmount);
						contactsLoyalty.setGiftcardBalance(new BigDecimal(contactsLoyalty.getGiftcardBalance() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					if(contactsLoyalty.getTotalGiftcardAmount() == null){
						contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
					}
					else{
						//contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount);
						contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					
					//deduct loyalty points
					contactsLoyalty.setLoyaltyBalance(contactsLoyalty.getLoyaltyBalance() - subPoints);
					contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? subPoints :
						contactsLoyalty.getTotalLoyaltyRedemption() + subPoints);
					
					logger.info("contactsLoyalty.getGiftcardBalance() = "+contactsLoyalty.getGiftcardBalance());
					
					deductPointsFromExpiryTable(contactsLoyalty, subPoints, convertedAmount);
				}
			}
		
		}catch(Exception e){
			logger.error("Exception while applying auto conversion rules...", e);
			return null;
		}
		return differenceArr;
	}
	
	private void deductPointsFromExpiryTable(ContactsLoyalty contactLoyalty, double subPoints, double earnedAmt) throws Exception{
		logger.debug(">>>>>>>>>>>>> entered in deductPointsFromExpiryTable");
		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
		LoyaltyTransactionExpiryDaoForDML expiryDaoForDML = (LoyaltyTransactionExpiryDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO_FOR_DML);
		List<LoyaltyTransactionExpiry> expiryList = null; //expiryDao.fetchExpPointsTrans(""+membershipNumber, 100, userId);
		Iterator<LoyaltyTransactionExpiry> iterList = null; //expiryList.iterator();
		LoyaltyTransactionExpiry expiry = null;
		long remainingPoints = (long)subPoints;

		do{
			expiryList = expiryDao.fetchExpLoyaltyPtsTrans(contactLoyalty.getLoyaltyId(), 
					100, contactLoyalty.getUserId());
			//logger.info("expiryList size = "+expiryList.size());
			if(expiryList == null || remainingPoints <= 0) break;
			iterList = expiryList.iterator();

			while(iterList.hasNext()){

				logger.info("remainingPoints = "+remainingPoints +" earnedAmt = "+earnedAmt);
				expiry = iterList.next();

				if((expiry.getExpiryPoints() == null || expiry.getExpiryPoints() <= 0) && 
						(expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0)){
					logger.info("Wrong entry condition...");
				}
				else if(expiry.getExpiryPoints() < remainingPoints){
					logger.info("subtracted points = "+expiry.getExpiryPoints());
					remainingPoints = remainingPoints - expiry.getExpiryPoints().longValue();
					expiry.setExpiryPoints(0l);
					expiryDaoForDML.saveOrUpdate(expiry);
					continue;

				}
				else if(expiry.getExpiryPoints() >= remainingPoints){
					logger.info("subtracted points = "+expiry.getExpiryPoints());
					expiry.setExpiryPoints(expiry.getExpiryPoints() - remainingPoints);
					remainingPoints = 0;
					if(expiry.getExpiryAmount() == null){
						expiry.setExpiryAmount(earnedAmt);
					}
					else{
						expiry.setExpiryAmount(expiry.getExpiryAmount() + earnedAmt);
					}
					expiryDaoForDML.saveOrUpdate(expiry);
					break;
				}

			}

		}while(remainingPoints > 0);
		logger.debug("<<<<<<<<<<<<< completed deductPointsFromExpiryTable");
	}//deductPointsFromExpiryTable


	private LoyaltyProgramTier getLoyaltyTier(Long tierId) throws Exception{
		
		LoyaltyProgramTierDao tierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		return tierDao.getTierById(tierId);
		
	}
	
	private LoyaltyProgramTier findTier(ContactsLoyalty contactsLoyalty) throws Exception {

		LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);

		List<LoyaltyProgramTier> tiersList = loyaltyProgramTierDao.fetchTiersByProgramId(contactsLoyalty.getProgramId());
		if (tiersList == null || tiersList.size() <= 0) {
			logger.info("Tiers list is empty...");
			return null;
		}
		else if (tiersList.size() >= 1) {
			Collections.sort(tiersList, new Comparator<LoyaltyProgramTier>() {
				@Override
				public int compare(LoyaltyProgramTier o1, LoyaltyProgramTier o2) {

					int num1 = Integer.valueOf(o1.getTierType().substring(5)).intValue();
					int num2 = Integer.valueOf(o2.getTierType().substring(5)).intValue();
					if(num1 < num2){
						return -1;
					}
					else if(num1 == num2){
						return 0;
					}
					else{
						return 1;
					}
				}
			});
		}

		for(LoyaltyProgramTier tier : tiersList) {//testing purpose
			logger.info("tier level : "+tier.getTierType());
		}

		if(!OCConstants.LOYALTY_PROGRAM_TIER1.equals(tiersList.get(0).getTierType())){// if tier 1 not exist return null
			logger.info("selected tier...null...tier1 not found");
			return null;
		}

		//Prepare eligible tiers map
		Iterator<LoyaltyProgramTier> iterTier = tiersList.iterator();
		Map<LoyaltyProgramTier, LoyaltyProgramTier> eligibleMap = new LinkedHashMap<LoyaltyProgramTier, LoyaltyProgramTier>();
		LoyaltyProgramTier prevtier = null;
		LoyaltyProgramTier nexttier = null;

		while(iterTier.hasNext()){
			nexttier = iterTier.next();
			if(OCConstants.LOYALTY_PROGRAM_TIER1.equals(nexttier.getTierType())){
				eligibleMap.put(nexttier, null);
			}
			else{
				if((Integer.valueOf(prevtier.getTierType().substring(5))+1) 
						== Integer.valueOf(nexttier.getTierType().substring(5)) && prevtier.getTierUpgdConstraintValue() != null){
					eligibleMap.put(nexttier, prevtier);
					logger.info("eligible tier ="+nexttier.getTierType()+" upgdconstrant value = "+prevtier.getTierUpgdConstraintValue());
				}
			}
			prevtier = nexttier;
		}

		if(OCConstants.LOYALTY_LIFETIME_POINTS.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_POINTS);
			if(contactsLoyalty == null) {
				return tiersList.get(0);
			}
			else {

				Double totLoyaltyPointsValue = contactsLoyalty.getTotalLoyaltyEarned() == null ? 0.00 : contactsLoyalty.getTotalLoyaltyEarned();
				logger.info("totLoyaltyPointsValue value = "+totLoyaltyPointsValue);

				if(totLoyaltyPointsValue == null || totLoyaltyPointsValue <= 0){
					logger.info("totLoyaltyPointsValue value is empty...");
					return tiersList.get(0);
				}
				else{
					Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
					LoyaltyProgramTier prevKeyTier = null;
					LoyaltyProgramTier nextKeyTier = null;
					while(it.hasNext()){
						nextKeyTier = it.next();
						logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
						logger.info("-------------currTier::"+tiersList.get(0).getTierType());
						if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
							prevKeyTier = nextKeyTier;
							continue;
						}
						if(totLoyaltyPointsValue > 0 && totLoyaltyPointsValue < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
							if(prevKeyTier == null){
								logger.info("selected tier is currTier..."+tiersList.get(0).getTierType());
								return tiersList.get(0);
							}
							logger.info("selected tier..."+prevKeyTier.getTierType());
							return prevKeyTier;
						}
						else if (totLoyaltyPointsValue > 0 && totLoyaltyPointsValue >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
							logger.info("selected tier..."+nextKeyTier.getTierType());
							return nextKeyTier;
						}
						prevKeyTier = nextKeyTier;
					}
					return tiersList.get(0);
				}//else
			}
		}
		else if(contactsLoyalty.getContact() == null || contactsLoyalty.getContact().getContactId() == null){
			logger.info("contactId is null and selected tier..."+tiersList.get(0).getTierType());
			return tiersList.get(0);
		}
		else if(OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			logger.info("tier condition on :"+OCConstants.LOYALTY_LIFETIME_PURCHASE_VALUE);
			
			Double totPurchaseValue = null;
			totPurchaseValue = LoyaltyProgramHelper.getLPV(contactsLoyalty);
			
			logger.info("purchase value = "+totPurchaseValue);
			
		//	if(contactPurcahseList == null || totPurchaseValue == null || totPurchaseValue <= 0){

			if(totPurchaseValue == null || totPurchaseValue <= 0){
				logger.info("purchase value is empty...");
				return tiersList.get(0);
			}
			else{

				Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
				LoyaltyProgramTier prevKeyTier = null;
				LoyaltyProgramTier nextKeyTier = null;
				while(it.hasNext()){
					nextKeyTier = it.next();
					logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
					logger.info("-------------tiersList.get(0)::"+tiersList.get(0).getTierType());
					if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
						prevKeyTier = nextKeyTier;
						continue;
					}
					if(totPurchaseValue > 0 && totPurchaseValue < eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
						if(prevKeyTier == null){
							logger.info("selected tier is currTier..."+tiersList.get(0).getTierType());
							return tiersList.get(0);
						}
						logger.info("selected tier..."+prevKeyTier.getTierType());
						return prevKeyTier;
					}
					else if (totPurchaseValue > 0 && totPurchaseValue >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue() && !it.hasNext()) {
						logger.info("selected tier..."+nextKeyTier.getTierType());
						return nextKeyTier;
					}
					prevKeyTier = nextKeyTier;
				}
				return tiersList.get(0);
			}//else
		}
		else if(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE.equals(tiersList.get(0).getTierUpgdConstraint())){
			try{
				Double cumulativeAmount = 0.0;
//				Iterator<LoyaltyProgramTier> it = eligibleMap.keySet().iterator();
				ListIterator<LoyaltyProgramTier> it = new ArrayList(eligibleMap.keySet()).listIterator(eligibleMap.size());
//				LoyaltyProgramTier prevKeyTier = null;
				LoyaltyProgramTier nextKeyTier = null;
				while(it.hasPrevious()){
					nextKeyTier = it.previous();
					logger.info("------------nextKeyTier::"+nextKeyTier.getTierType());
					logger.info("-------------currTier::"+tiersList.get(0).getTierType());
					if(OCConstants.LOYALTY_PROGRAM_TIER1.equalsIgnoreCase(nextKeyTier.getTierType())){
//						prevKeyTier = nextKeyTier;
						return tiersList.get(0);
					}
					Calendar startCal = Calendar.getInstance();
					Calendar endCal = Calendar.getInstance();
					endCal.add(Calendar.MONTH, -eligibleMap.get(nextKeyTier).getTierUpgradeCumulativeValue().intValue());

					String startDate = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
					logger.info("contactId = "+contactsLoyalty.getContact().getContactId()+" startDate = "+startDate+" endDate = "+endDate);


					LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
					cumulativeAmount = Double.valueOf(loyaltyTransactionChildDao.getLoyaltyCumulativePurchase(contactsLoyalty.getUserId(), contactsLoyalty.getProgramId(), contactsLoyalty.getLoyaltyId(), startDate, endDate));

					if(cumulativeAmount == null || cumulativeAmount <= 0){
						logger.info("cumulative purchase value is empty...");
						continue;
					}
					
					if(cumulativeAmount > 0 && cumulativeAmount >= eligibleMap.get(nextKeyTier).getTierUpgdConstraintValue()){
						return nextKeyTier;
					}
					
				}
				return tiersList.get(0);
			}catch(Exception e){
				logger.error("Excepion in cpv thread ", e);
				return tiersList.get(0);
			}
		}
		else{
			return null;
		}
	}


	private LoyaltyTransactionChild createPurchaseTransaction(ContactsLoyalty loyalty, Double adjustValue,
			String earnType, String entAmountType, String activationDate, String earnStatus, String adjType,
			String description2, LoyaltyBalance ltyBalance) {
		
		logger.debug(">>>>>>>>>>>>> entered in createPurchaseTransaction");
		LoyaltyTransactionChild transaction = null;
		try{

			transaction = new LoyaltyTransactionChild();
			transaction.setMembershipNumber(""+loyalty.getCardNumber());
			transaction.setMembershipType(loyalty.getMembershipType());
			transaction.setCardSetId(loyalty.getCardSetId());
			transaction.setCreatedDate(Calendar.getInstance());
			transaction.setEarnType(earnType);
			if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
				long adjustValueLong = adjustValue.longValue();//APP-823
				transaction.setEarnedPoints(adjustValue);
				transaction.setPointsDifference(""+(adjType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD)?adjustValueLong:-adjustValueLong));
			}
			else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
				transaction.setEarnedAmount(adjustValue);
				transaction.setAmountDifference(""+(adjType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD)?adjustValue:-adjustValue));

			}else{
				transaction.setRewardBalance((ltyBalance!=null)?(ltyBalance.getBalance().doubleValue()):null);
				transaction.setRewardDifference(""+(adjType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD)?adjustValue:-adjustValue));
				transaction.setEarnedReward(adjustValue);
			}
			if(earnStatus != null) {
				transaction.setEarnStatus(earnStatus);
			}
			if(activationDate != null){
				if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
					transaction.setHoldPoints((double)adjustValue);
				}
				else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
					transaction.setHoldAmount((double)adjustValue);
				}
				transaction.setValueActivationDate(new SimpleDateFormat("yyyy-MM-dd").parse(activationDate));
			}
			transaction.setStoreNumber(loyalty.getPosStoreLocationId());
			transaction.setSubsidiaryNumber(loyalty.getSubsidiaryNumber());
			transaction.setEnteredAmount(adjustValue);
			transaction.setEnteredAmountType(adjType);
			transaction.setOrgId(loyalty.getOrgId());
			transaction.setPointsBalance(loyalty.getLoyaltyBalance());
			transaction.setAmountBalance(loyalty.getGiftcardBalance());
			transaction.setGiftBalance(loyalty.getGiftBalance());
			transaction.setProgramId(loyalty.getProgramId());
			transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(loyalty.getUserId());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT);
			transaction.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MANUAL);
			transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
//			transaction.setEventTriggStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			transaction.setLoyaltyId(loyalty.getLoyaltyId());
			//transaction.setDescription2(subTb2Id.getValue().trim());
			//description2=subTb2Id.getValue().trim();
			transaction.setDescription2(description2);

			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			
			//Event Trigger sending part
			EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
			EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
			eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
			EventTriggerDao eventTriggerDao  = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
			List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(transaction.getUserId(),Constants.ET_TYPE_ON_LOYALTY_ADJUSTMENT);
			
			if(etList != null) {
				eventTriggerEventsObservable.notifyToObserver(etList, transaction.getTransChildId(), transaction.getTransChildId(), 
																transaction.getUserId(), OCConstants.LOYALTY_ADJUSTMENT,Constants.ET_TYPE_ON_LOYALTY_ADJUSTMENT);
			}

		}catch(Exception e){
			logger.error("Exception while logging enroll transaction...",e);
		}
		
		logger.debug("<<<<<<<<<<<<< completed createPurchaseTransaction");
		return transaction;
	}
	
	private String getSBS(String storeNO, Long userID, Long orgID) {
		
		//written to get subsidiary_number from database instead of SBToOC json as plugins are not ready to get subsidiary number in json
				try{
						OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
						UsersDao userDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
						//UsersDomainsDao UsersDomainsDao = (UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
						Long domainId = userDao.findDomainByUserId(userID);
						OrganizationStores orgStores = organizationStoresDao.findOrgByDomain(orgID, domainId, storeNO);
						return orgStores!=null ? orgStores.getSubsidiaryId() : null;
				}catch(Exception e){
					logger.info("Exception::",e);
				}
				return null;
	}
	
	
	public String getMobilePhone(HashMap<String, String> formMapValuesHM, HashMap<String, List> inputMapSettingHM, Users user, FormMapping formMapping){
		try {
		LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		LoyaltyProgram progObj = loyaltyProgramDao.findById(formMapping.getLoyaltyProgramId());
			if(progObj.getMembershipType().contentEquals(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			Set<String> valueKeys = formMapValuesHM.keySet();
			String methodNameStr = "";
			String MobilePhone = null;
			for (String vkey : valueKeys) {
				String fieldVal = formMapValuesHM.get(vkey);
				if (!inputMapSettingHM.containsKey(vkey)) {
					continue;
				} // if
				List tempList = inputMapSettingHM.get(vkey);
				methodNameStr = tempList.get(0).toString();
				if (methodNameStr.equalsIgnoreCase("MobilePhone")) {
					MobilePhone = fieldVal;
				}
			}// for
			
			if(Constants.WEBFORM_TYPE_LOYALTY_SIGNUP.equalsIgnoreCase(formMapping.getFormType()) && MobilePhone==null) {
					return "\n Mobile No is mandatory for mobile based loyalty enrolment.";
			}else if(Constants.WEBFORM_TYPE_LOYALTY_SIGNUP.equalsIgnoreCase(formMapping.getFormType()) && MobilePhone!=null){
				Map<String, Object> resultMap = LoyaltyProgramHelper.validateMobile(user, MobilePhone);
				if(!(Boolean) resultMap.get("isValid")){
					return "\n Invalid Mobile Number.";
					}
				}
			}
		return null;
		}catch (Exception e) {
			return null;
		}
	}
	
}
