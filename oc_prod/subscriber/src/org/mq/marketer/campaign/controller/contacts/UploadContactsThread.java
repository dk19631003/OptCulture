package org.mq.marketer.campaign.controller.contacts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CustomFieldData;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.EventTrigger;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Messages;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.service.CaptiwayToSMSApiGateway;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObservable;
import org.mq.marketer.campaign.controller.service.EventTriggerEventsObserver;
import org.mq.marketer.campaign.custom.MyCalendar;
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
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.MessagesDaoForDML;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.dao.PurgeDao;
import org.mq.marketer.campaign.dao.PurgeDaoForDML;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.PurgeList;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.helper.SmsQueueHelper;
import org.mq.optculture.utils.OCConstants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;

@SuppressWarnings("unchecked")
public class UploadContactsThread implements Runnable, ApplicationContextAware {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public boolean isRunning = false;

	private static Map<String, String> contactFieldMap = new HashMap<String, String>();
	static {

		contactFieldMap.put("Email", "emailId");
		contactFieldMap.put("First Name", "firstName");
		contactFieldMap.put("Last Name", "lastName");
		contactFieldMap.put("Street", "addressOne");
		contactFieldMap.put("Address Two", "addressTwo");
		contactFieldMap.put("City", "city");
		contactFieldMap.put("State", "state");
		contactFieldMap.put("Country", "country");
		contactFieldMap.put("ZIP", "zip");
		contactFieldMap.put("Mobile", "mobilePhone");
		contactFieldMap.put("Customer ID", "externalId");
		contactFieldMap.put("Addressunit ID", "hpId");
		contactFieldMap.put("Gender", "gender");
		contactFieldMap.put("BirthDay", "birthDay");
		contactFieldMap.put("Anniversary", "anniversary");
		contactFieldMap.put("Home Store", "homeStore");
		contactFieldMap.put("Subsidiary Number", "subsidiaryNumber");

	}

	public Queue<Object[]> uploadQueue = new LinkedList();

	Object[] pollObj;

	private ApplicationContext context;

	public void setApplicationContext(ApplicationContext context) throws BeansException {

		this.context = context;
	}

	private ContactsDao contactsDao;
	private ContactsDaoForDML contactsDaoForDML;
	// Changes 2.5.4.0 start
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML;
	// Changes 2.5.4.0 end

	private EventTriggerEventsObserver eventTriggerEventsObserver;
	private EventTriggerEventsObservable eventTriggerEventsObservable;

	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;
	private MessagesDao messagesDao;
	private MessagesDaoForDML messagesDaoForDML;
	private MailingListDao mailingListDao;
	private MailingListDaoForDML mailingListDaoForDML;
	private SMSSettingsDao smsSettingsDao;
	private EventTriggerDao eventTriggerDao;
	private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
	private SMSSettings smsSettings;
	private OCSMSGateway ocsmsGateway;
	private CustomTemplatesDao customTemplatesDao;
	private EmailQueueDao emailQueueDao;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	private PurgeDao purgeDao;
	private PurgeDaoForDML purgeDaoForDML;
	private MyTemplatesDao myTemplatesDao;

	public void run() {

		contactsDao = (ContactsDao) context.getBean("contactsDao");
		contactsDaoForDML = (ContactsDaoForDML) context.getBean("contactsDaoForDML");

		// Changes 2.5.4.0 start
		contactsLoyaltyDao = (ContactsLoyaltyDao) context.getBean("contactsLoyaltyDao");
		contactsLoyaltyDaoForDML = (ContactsLoyaltyDaoForDML) context.getBean("contactsLoyaltyDaoForDML");
		// Changes 2.5.4.0 end

		messagesDao = (MessagesDao) context.getBean("messagesDao");
		messagesDaoForDML = (MessagesDaoForDML) context.getBean("messagesDaoForDML");
		mailingListDao = (MailingListDao) context.getBean("mailingListDao");
		mailingListDaoForDML = (MailingListDaoForDML) context.getBean("mailingListDaoForDML");
		captiwayToSMSApiGateway = (CaptiwayToSMSApiGateway) context.getBean("captiwayToSMSApiGateway");
		smsSettingsDao = (SMSSettingsDao) context.getBean("smsSettingsDao");
		usersDao = (UsersDao) context.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML) context.getBean("usersDaoForDML");
		customTemplatesDao = (CustomTemplatesDao) context.getBean("customTemplatesDao");
		emailQueueDao = (EmailQueueDao) context.getBean("emailQueueDao");
		emailQueueDaoForDML = (EmailQueueDaoForDML) context.getBean("emailQueueDaoForDML");
		eventTriggerDao = (EventTriggerDao) context.getBean("eventTriggerDao");
		purgeDao = (PurgeDao) context.getBean("purgeDao");
		purgeDaoForDML = (PurgeDaoForDML) context.getBean("purgeDaoForDML");
		eventTriggerEventsObserver = (EventTriggerEventsObserver) context.getBean("eventTriggerEventsObserver");
		eventTriggerEventsObservable = (EventTriggerEventsObservable) context.getBean("eventTriggerEventsObservable");

		eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);// adding
		myTemplatesDao = (MyTemplatesDao) context.getBean("myTemplatesDao");
		// observer

		isRunning = true;
		resetFields();
		while (pollQueue()) {
			logger.debug("Thread start here ");
			uploadContactsFromFile();
			logger.debug("Thread end  here ");
			pollObj = null;
			// System.gc();
		} // while
		isRunning = false;
	} // run

	private void resetFields() {
		smsSettings = null;
		// userSMSGateway = null;
		ocsmsGateway = null;

	}

	private int mobileContacts = 0;
	private String noSMSComplaincyMsg = null;
	private String noSMSCredits = null;
	private Set<String> optinMobileSet = null;

	private void uploadContactsFromFile() {
		try {

			if (pollObj == null) {
				return;
			}

			Users currentUser = (Users) pollObj[0];
			Set<MailingList> mailingListSet = (Set) pollObj[1];
			Rows contactRowsId = (Rows) pollObj[2];
			String path = (String) pollObj[3];
			PurgeList purgeList = (PurgeList) context.getBean("purgeList");
			String fileNameStr = path.substring(path.lastIndexOf("/") + 1);

			if (currentUser.isEnableSMS() && currentUser.isConsiderSMSSettings()) {

				if (SMSStatusCodes.smsProgramlookupOverUserMap.get(currentUser.getCountryType()))
					smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(),
							OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				else
					smsSettings = smsSettingsDao.findByOrg(currentUser.getUserOrganization().getUserOrgId(),
							OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);

				// smsSettings =
				// smsSettingsDao.findByUser(currentUser.getUserId(),
				// OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				if (smsSettings != null)
					ocsmsGateway = GatewayRequestProcessHelper.getOcSMSGateway(smsSettings.getUserId(),
							SMSStatusCodes.defaultSMSOptinGatewayTypeMap.get(currentUser.getCountryType()));

			}

			// smsSettings = smsSettingsDao.findByUser(currentUser.getUserId(),
			// OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);

			mobileContacts = 0;
			noSMSComplaincyMsg = null;
			noSMSCredits = null;
			optinMobileSet = new HashSet<String>();

			Long startContactId = null;
			Long endContactId = null;
			List<EventTrigger> etList = eventTriggerDao.findAllUserAutoRespondTriggers(
					currentUser.getUserId().longValue(), (Constants.ET_TYPE_ON_CONTACT_OPTIN_MEDIUM
							+ Constants.DELIMETER_COMMA + Constants.ET_TYPE_ON_CONTACT_ADDED));

			List rowList = contactRowsId.getChildren();
			TreeMap<String, List<String>> prioMap = new TreeMap<String, List<String>>();
			int customerIdIdx = -1, addressUntiIdIndx = -1, emailIdIdx = -1, mobileIdx = -1, firstNameIdx = -1,subsidiaryNumberIdx=-1;
			int lastNameIdx = -1, streetIdx = -1,addressTwoIdx=-1, cityIdx = -1, stateIdx = -1, zipIdx = -1;
			int countryIdx = -1, genderIdx = -1, birthDayIdx = -1, anniversaryIdx = -1, homeStoreIdx = -1,
					createdDateIdx = -1, memberShipNumberIdx = -1;

			String birthDayFormat = "", anniversaryFormat = "", createdDateFormat = "";
			List<String> contactHeadersList = new ArrayList<String>();
			Hashtable udfHashtable = new Hashtable();
			FileReader fileReader = new FileReader(path);
			BufferedReader br = new BufferedReader(fileReader);
			String csvColumnStr = "";
			String lineStr = null;
			String[] lineStrTokens;

			// Getting the Headers Index
			while ((lineStr = br.readLine()) != null) {
				// lineStr += ",\"0\"";
				if (lineStr.trim().length() == 0)
					continue;

				// nextLineStr = parse(lineStr);//
				// lineStr.split(csvDelemiterStr);
				lineStrTokens = parse(lineStr);// lineStr.split(csvDelemiterStr);
				// //logger.debug("lineStrTokens>>>>"+lineStrTokens);

				if (lineStrTokens.length == 0) {
					continue;
				}

				for (Object object : rowList) {
					Row tempRow = (Row) object;
					List mapRowLst = tempRow.getChildren();

					/*
					 * String csvColLabel = ((Textbox)mapRowLst.get(0)).getValue(); String
					 * optCulFiled = ((Listbox)mapRowLst.get(1)).getSelectedItem().getLabel();
					 */

					Div csvDiv = (Div) mapRowLst.get(0);
					String csvColLabel = ((Textbox) csvDiv.getChildren().get(0)).getValue();

					Div optFieldDiv = (Div) mapRowLst.get(1);
					Listbox optCulFiledListbx = (Listbox) optFieldDiv.getChildren().get(0);
					String optCulFiled = optCulFiledListbx.getSelectedItem().getLabel();
					String optCulFiledData = "";
					if(optCulFiledListbx.getSelectedItem().getValue() != null) {
						optCulFiledData = optCulFiledListbx.getSelectedItem().getValue();
					}

					String priorKey = "";
					if (((Intbox) mapRowLst.get(4)).getValue() != null) {

						priorKey = "" + ((Intbox) mapRowLst.get(4)).getValue();
					}
					Listbox tempListBx = (Listbox) ((Div) mapRowLst.get(3)).getChildren().get(0);
					String dataTypeStr = tempListBx.getSelectedItem().getLabel();

					if (dataTypeStr.equals("Date")) {
						dataTypeStr = dataTypeStr + "("
								+ ((Listbox) ((Div) mapRowLst.get(3)).getChildren().get(1)).getSelectedItem().getLabel()
								+ ")";
						// dateFormatStr =
						// ((Listbox)((Div)mapRowLst.get(2)).getChildren().get(1)).getSelectedItem().getLabel();
					}

					for (int j = 0; j < lineStrTokens.length; j++) {

						csvColumnStr = lineStrTokens[j].trim();

						if (contactHeadersList.contains(csvColumnStr)) {
							continue;
						}

						if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Customer ID")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {

							// //logger.debug("customerIdIdx is::"+csvColumnStr
							// + ":: and index is ::"+j );
							customerIdIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						}

						else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Mobile")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("mobileIdx is::"+csvColumnStr +
							// ":: and index is ::"+j );
							mobileIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Email")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("Email is::"+csvColumnStr +
							// ":: and index is ::"+j+" :: priorKey is "+priorKey
							// );
							emailIdIdx = j;
							logger.info("Email is ===>" + csvColumnStr);
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("First Name")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("firstName is::"+csvColumnStr +
							// ":: and index is ::"+j );
							firstNameIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Last Name")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("LastName is::"+csvColumnStr +
							// ":: and index is ::"+j );
							lastNameIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Street")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {

							// //logger.debug("Street is::"+csvColumnStr +
							// ":: and index is ::"+j );
							streetIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("City")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("City is::"+csvColumnStr +
							// ":: and index is ::"+j
							// +" :: priorKey is "+priorKey);
							cityIdx = j;
							contactHeadersList.add(csvColumnStr);

							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("State")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("State is::"+csvColumnStr +
							// ":: and index is ::"+j );
							stateIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("ZIP")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("pin/Zip is::"+csvColumnStr +
							// ":: and index is ::"+j );
							zipIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Subsidiary Number")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("firstName is::"+csvColumnStr +
							// ":: and index is ::"+j );
							subsidiaryNumberIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						}else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Country")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("Country is::"+csvColumnStr +
							// ":: and index is ::"+j );
							countryIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Gender")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("Gender is::"+csvColumnStr +
							// ":: and index is ::"+j );
							genderIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("BirthDay")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							if (dataTypeStr.trim().startsWith("Date")) {

								birthDayFormat = dataTypeStr.substring(dataTypeStr.indexOf("(") + 1,
										dataTypeStr.indexOf(")"));

							}

							birthDayIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Anniversary")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {

							if (dataTypeStr.trim().startsWith("Date")) {
								anniversaryFormat = dataTypeStr.substring(dataTypeStr.indexOf("(") + 1,
										dataTypeStr.indexOf(")"));
							}
							anniversaryIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Created Date")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {

							if (dataTypeStr.trim().startsWith("Date")) {
								createdDateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(") + 1,
										dataTypeStr.indexOf(")"));
							}
							createdDateIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						} else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Home Store")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("Gender is::"+csvColumnStr +
							// ":: and index is ::"+j );
							homeStoreIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						}

						// changes start 2.5.4.0
						else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Membership Number")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("Gender is::"+csvColumnStr +
							// ":: and index is ::"+j );
							memberShipNumberIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						}
						// changes end 2.5.4.0

						//Different
						else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiled.equals("Address Two")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {
							// //logger.debug("Gender is::"+csvColumnStr +
							// ":: and index is ::"+j );
							addressTwoIdx = j;
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiled, dataTypeStr, prioMap);
							}
							continue;
						}
						
						
						
						
						
						// City,State,Country,ZIP,,Gender,BirthDay,Anniversary,
						
						else if (!(contactHeadersList.contains(csvColumnStr)) && optCulFiledData.startsWith("UDF")
								&& csvColLabel.trim().equalsIgnoreCase(csvColumnStr)) {

							udfHashtable.put(optCulFiledData, j + "_" + dataTypeStr);
							contactHeadersList.add(csvColumnStr);
							if (priorKey != null && priorKey.length() > 0) {
								storePriorityVal(j, priorKey, optCulFiledData, dataTypeStr, prioMap);
							}
							// //break;
							continue;
						}

					} // for j
				} // for

				break;
			} // while

			// manditory fields Not exists
			if (mobileIdx == -1 && emailIdIdx == -1) {
				MessageUtil.setMessage("File does not have the mandatory fields header.", "color :red");
				return;
			}

			for (String Test : contactHeadersList) {
				logger.info("Test===>" + Test);
			}

			// if(true) return false;

			// logger.info("PRIORITY MAP ::"+prioMap);
			int totalCount = 0, inValidCount = 0, updatedCount = 0, newContactsCount = 0;
			
			List<MailingList> lastModifiedDatList = new ArrayList<MailingList>();

			// Read Data
			while ((lineStr = br.readLine()) != null) {

				logger.info("lineStr===>" + lineStr);

				try {

					// Long mobilePhoneLong = null;
					// String mobilePhone = null;
					String emailStr = null;
					boolean isLtyCon = false;
					Contacts contactObj = null, contactObjChk = null,contactObjOrg=null;
					ContactsLoyalty contactsLoyaltyObj = null;
					boolean notEmpty = false;
					Method tempMethod = null;
					boolean isEnableEvent = false;
					if (lineStr.trim().length() == 0)
						continue;
					lineStrTokens = parse(lineStr);// lineStr.split(csvDelemiterStr);

					// //logger.debug("lineStr ::"+lineStr+"::: length="+
					// lineStrTokens.length);

					if (lineStrTokens.length == 0) {
						continue;
					}
					totalCount++;
					// Changes start 2.5.4.0
					if (contactHeadersList != null && contactHeadersList.contains("Membership Number")) {
						if(memberShipNumberIdx>-1)
						logger.info("lineStrTokens[memberShipNumberIdx]====>" + lineStrTokens[memberShipNumberIdx]);

						if (lineStrTokens[memberShipNumberIdx] != null
								&& lineStrTokens[memberShipNumberIdx].isEmpty()) {
							inValidCount++;
							continue;

						}

						contactsLoyaltyObj = contactsLoyaltyDao.findByMembershipNoAndUserIdStrictly(
								Long.parseLong(lineStrTokens[memberShipNumberIdx]), currentUser.getUserId());

						if (contactsLoyaltyObj == null || contactsLoyaltyObj.getContact() == null) {
							inValidCount++;
							continue;
}
						contactObjChk = contactsLoyaltyObj.getContact();
						
						isLtyCon = true;
						/*if(contactsLoyaltyObj.getContact()!=null){
								contactObjChk = contactsDao.findById(contactsLoyaltyObj.getContact().getContactId());
								if (contactObjChk == null) {
								logger.info("Found====3");
								continue;
							}
							//Should not be required
							if (!contactObjChk.getContactId().equals(contactsLoyaltyObj.getContact().getContactId())) {
								logger.info("Found====4");
								continue;
							}
						}*/

						//logger.info("contactObjChk.getContactId()==>" + contactObjChk.getContactId());

					}
					// Changes end 2.5.4.0

					if (emailIdIdx > -1) {
						emailStr = lineStrTokens[emailIdIdx].trim();
					}
					boolean isValidCont = false;

					// TODO change this code as per priority
					if (customerIdIdx == -1 && emailIdIdx == -1 && mobileIdx == -1) {
						inValidCount++;
						continue;
					}

					if (customerIdIdx != -1 && lineStrTokens[customerIdIdx].trim().length() > 0) {
						isValidCont = true;
					} else if (emailIdIdx != -1 && lineStrTokens[emailIdIdx].trim().length() > 0
							&& Utility.validateEmail(lineStrTokens[emailIdIdx].trim())) {

						isValidCont = true;

					} else if (mobileIdx != -1 && lineStrTokens[mobileIdx].trim().length() > 0
							&& Utility.phoneParse(lineStrTokens[mobileIdx].trim(),
									currentUser != null ? currentUser.getUserOrganization() : null) != null) {
						isValidCont = true;
					}

					if (!isValidCont) {
						inValidCount++;
						continue;
					}

					// check the contact if already exists
					// //logger.debug("POSMAP=="+prioMap);
					contactObj = contactsDao.findContactByPriority(prioMap, lineStrTokens, currentUser);
					logger.info("contactObj value is "+contactObj);
					if(contactObj!=null) {
					logger.info("entering if");
						contactObjOrg= (Contacts) contactObj.clone();
					}
					logger.info("contactObj value is "+contactObj);
					logger.info("contactObjOrg value is "+contactObjOrg);
					
				//	contactObjOrg = contactsDao.findContactByPriority(prioMap, lineStrTokens, currentUser.getUserId());
					
					if (isLtyCon) {
						logger.info("Entered mebership "+isLtyCon);
						List<Contacts> contactIdList= contactsDao.findContactByPriorityList(prioMap, lineStrTokens, currentUser,currentUser.getUserId());
						
							//logger.info("more than 1 object found 2 :" + contactIdList.size() + " " + cid);
						if(contactIdList!=null) {
							for (int i = 0; i < contactIdList.size(); i++) {
								logger.info("Logs===>"+contactIdList.get(i));
								if (contactObjChk !=null && contactIdList.get(i).getContactId().toString().equals(contactObjChk.getContactId().toString())){
										contactObj =contactIdList.get(i);
										notEmpty = true;
										break;
									}
							}
						}
						
						
						if (!notEmpty ) {
							// Flow differentiate when both have same Cid
							
							Set<String> keySet = prioMap.keySet();
							logger.info("keySet===>" + keySet);
						 outer: for (Map.Entry eachMap : prioMap.entrySet()) {
								for (String eachPrioRecord : (List<String>) eachMap.getValue()) {
											logger.info("mInner==>" + eachPrioRecord);
											String eachPrioRecordArray[] = eachPrioRecord.split("\\|");
											logger.info("eachPrioRecordArray[]==>" + eachPrioRecordArray[2]);
											tempMethod = Contacts.class
													.getMethod("get" + StringUtils.capitalize(eachPrioRecordArray[1]));
											if (tempMethod != null) {
												logger.info("contactObjChk====>" + contactObjChk);
												logger.info("tempMethod==>" + tempMethod.getName());
												if (contactObjChk!=null && tempMethod.invoke(contactObjChk) != null) {
													notEmpty = true;
													break;
												}
										
											}

								}
								
							}

						}

					} 
										

					if(notEmpty && contactObj == null){
						inValidCount ++;
						continue;
					}
					
					if(isLtyCon && contactObj != null && contactObj.getContactId().longValue()  
							!= contactObjChk.getContactId().longValue() && notEmpty) {
						logger.debug("isLtyCon ==="+isLtyCon +" and contact is not same as the lty con "+notEmpty);
						inValidCount ++;
						continue;
					}
					long contactBit = 0l;
					String emailiStatus = null;
					// String mobileStatus = null;
					boolean purgeFlag = false;

					if (contactObj != null) {

						// mobileStatus = contactObj.getMobileStatus();
						emailiStatus = contactObj.getEmailStatus();
						purgeFlag = contactObj.getPurged();
						// mlset = contactObj.getMlSet();
						// mlList =
						// mailingListDao.findByContactBit(currentUser.getUserId(),
						// contactObj.getMlBits());
						contactBit = contactObj.getMlBits();
						if ((emailStr != null && emailStr.trim().length() > 0)
								&& (contactObj.getEmailId() == null || (contactObj.getEmailId() != null
										&& !(contactObj.getEmailId().equalsIgnoreCase(emailStr))))) {

							emailiStatus = Constants.CONT_STATUS_PURGE_PENDING;
							purgeFlag = false;
						}

						// //logger.debug(" Contact already exists and updating it ...");

						try {

							/*
							 * if(contactObj.getOptinMedium() == null){ contactObj.setOptinMedium(Constants.
							 * CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY); }
							 */
							if (contactObj.getMlBits().longValue() == 0l) {

								contactObj.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY);
								isEnableEvent = ((etList != null && etList.size() > 0) && true);
								// these will serves in list purging
								/*
								 * emailStatus = Constants.CONT_STATUS_PURGE_PENDING; emailFlag = false;
								 * purgeFlag = true;
								 */

							}
							logger.info(emailIdIdx + "setContactFieldsData===>" + lastNameIdx);
							contactObj = setContactFieldsData(lineStrTokens, contactObj, customerIdIdx, emailIdIdx,
									mobileIdx, firstNameIdx, lastNameIdx,subsidiaryNumberIdx, streetIdx,addressTwoIdx, cityIdx, stateIdx, zipIdx,
									countryIdx, genderIdx, birthDayIdx, anniversaryIdx, homeStoreIdx, createdDateIdx,
									birthDayFormat, anniversaryFormat, createdDateFormat, udfHashtable, currentUser);
							logger.info("updatedCount====>" + updatedCount);

							updatedCount++;
							/*
							 * if(isEnableEvent) { if(startContactId == null) { startContactId =
							 * contactObj.getContactId(); } endContactId = contactObj.getContactId(); }
							 */
						} catch (Exception e) {
							// logger.debug("Exception while updaing the contact..",e);
							inValidCount++;
							continue;
						}

					} else {

						try {

							// mlset = new HashSet<MailingList>();
							// mlList = new ArrayList<MailingList>();
							// //logger.debug(" new Contact created now");

							// lineStrTokens[emailIdIdx].trim() ,
							// mobilePhoneLong ,
							// lineStrTokens[customerIdIdx].trim()
							// ,mList.getListId()

							// contactObj = new
							// Contacts(ml,Calendar.getInstance(),
							// Constants.CONT_STATUS_PURGE_PENDING);
							contactObj = new Contacts();
							contactObj.setMlBits(0l);

							contactObj.setUsers(currentUser);
							contactObj.setCreatedDate(Calendar.getInstance());
							contactObj.setModifiedDate(Calendar.getInstance());

							// contactObj = new
							// Contacts(ml,Calendar.getInstance(),
							// Constants.CONT_STATUS_PURGE_PENDING);

							contactObj.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY);
							isEnableEvent = ((etList != null && etList.size() > 0) && true);
							contactObj = setContactFieldsData(lineStrTokens, contactObj, customerIdIdx, emailIdIdx,
									mobileIdx, firstNameIdx, lastNameIdx,subsidiaryNumberIdx, streetIdx,addressTwoIdx, cityIdx, stateIdx, zipIdx,
									countryIdx, genderIdx, birthDayIdx, anniversaryIdx, homeStoreIdx, createdDateIdx,
									birthDayFormat, anniversaryFormat, createdDateFormat, udfHashtable, currentUser);

							// set Optin Medium

							emailiStatus = Constants.CONT_STATUS_PURGE_PENDING;
							purgeFlag = false;
							// mobileStatus = contactObj.getMobilePhone()!= null
							// ? Constants.CON_MOBILE_STATUS_ACTIVE
							// :Constants.CON_MOBILE_STATUS_NOT_A_MOBILE;

							for (MailingList ml : mailingListSet) {

								if (!ml.getCheckDoubleOptin() && ml.isCheckWelcomeMsg()) {

									sendWelcomeEmail(contactObj, ml, ml.getUsers());

								}
							} // for each mailingList

							newContactsCount++;

						} catch (Exception e) {
							// logger.debug("Exception while adding the new contact..",e);
							inValidCount++;
						}

					}

					contactObj.setPurged(purgeFlag);
					contactObj.setEmailStatus(emailiStatus);
					if (contactObj.getMobilePhone() == null) {

						contactObj.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);
					}

					// logger.debug("conatcts saved here ...");

					/*
					 * Iterator<MailingList> mlsetIter = mlList.iterator();
					 * 
					 * Set<String> conMlSet = new HashSet<String>(); while(mlsetIter.hasNext()){
					 * conMlSet.add(mlsetIter.next().getListName()); }
					 */

					Iterator<MailingList> mlIter = mailingListSet.iterator();
					// logger.info(" 1>>>>mailingListSet is :"+mailingListSet.size());
					MailingList ml = null;
					while (mlIter.hasNext()) {
						ml = mlIter.next();

						if ((contactObj.getMlBits().longValue() & ml.getMlBit().longValue()) > 0)
							continue;
						// mlSet.add(ml);
						// mlset.add(ml);
						contactObj.setMlBits(contactObj.getMlBits().longValue() | ml.getMlBit().longValue());
						ml.setLastModifiedDate(Calendar.getInstance());
						lastModifiedDatList.add(ml);
					}

					// logger.info(" 2>>>>mailingListSet is :"+mlset.size());
					// contactObj.setMlSet(mlset);
					if(Utility.isModifiedContact(contactObj,contactObjOrg ))
					{
						logger.info("entered Modified date");
						contactObj.setModifiedDate(Calendar.getInstance());
					}	
					contactsDaoForDML.saveOrUpdate(contactObj);

					// Changes 2.5.4.0 start
					if (isLtyCon && !notEmpty ) {
					logger.info("contactObj===>"+contactObj.getContactId());
					    contactsLoyaltyObj.setContact(contactObj);
						contactsLoyaltyDaoForDML.saveOrUpdate(contactsLoyaltyObj);
						if(contactObjChk!=null)
						contactsDaoForDML.delete(contactObjChk);
					
					}
					// Changes 2.5.4.0 end


					if (isEnableEvent) {
						if (startContactId == null) {
							startContactId = contactObj.getContactId();
						}
						endContactId = contactObj.getContactId();
					}
					// contactsDao.merge(contactObj);
				} catch (Exception e) {
					inValidCount++;
					logger.error("Exception ::", e);
				}
			} // While

			fileReader.close();
			br.close();

			// set Purge
			// contactObj.setPurged(false);
			for (MailingList ml : mailingListSet) {

				/*
				 * List<Long> list = new ArrayList<Long>(); // PurgeList purgeList = //
				 * (PurgeList)SpringUtil.getBean("purgeList"); list.add(ml.getListId());
				 */
				try {
					purgeDaoForDML.initiatePurge(ml.getUsers().getUserId(), ml.getListId());
					String purgeUrl = PropertyUtil.getPropertyValue("PurgeUrl");
					Utility.pingSchedulerService(purgeUrl);
				} catch (Exception e) {
					logger.info("Exception :: ", e);
				}
				// purgeList.addAndStartPurging(list);

			} // for each mailingList

			logger.debug(fileNameStr + ":: file processing is completed..");

			// Send Message
			/*
			 * String messagesString =""+ totalCount+" total  contacts ."+ inValidCount
			 * +": invalid and "+updatedCount + " :updated and  "+newContactsCount
			 * +" :added from "+fileNameStr+" file." ;
			 */

			String messagesString = "" + "Succesfully completed uploading of contact file " + " \" " + fileNameStr
					+ " \" " + ". Please review its status here:" + "\n" + "Total contacts found in uploaded file: "
					+ totalCount + "\n" + "New contacts added to the list: " + newContactsCount + "\n"
					+ "Invalid contacts rejected from upload: " + inValidCount + "\n"
					+ "Existing contacts updated in the list: " + updatedCount;

			Messages messages = new Messages("Contact", "Uploaded Successfully", messagesString, Calendar.getInstance(),
					"Inbox", false, "Info", currentUser);
			// messagesDao.saveOrUpdate(messages);
			messagesDaoForDML.saveOrUpdate(messages);

			if (noSMSComplaincyMsg != null) {
				messages = new Messages("Contact", "Mobile contacts may not reachable",
						"Total mobile contacts:" + mobileContacts + noSMSComplaincyMsg, Calendar.getInstance(), "Inbox",
						false, "Info", currentUser);

				// messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
			}
			// we need selected mailing lists
			// Add the newContactsCount to listSize
			// logger.info("lastModifiedDatListlastModifiedDatList :::::"+
			// lastModifiedDatList.size());

			for (MailingList mailingList : lastModifiedDatList) {
				logger.info("Adding listSize " + mailingList.getListName() + " Existing Size "
						+ mailingList.getListSize() + " newContactsCount:" + newContactsCount);
				mailingList.setListSize(mailingList.getListSize() + 1);
			}

			// mailingListDao.saveByCollection(lastModifiedDatList);
			mailingListDaoForDML.saveByCollection(lastModifiedDatList);

			/*
			 * listMappingDivId.setVisible(false); fileUploadDivId.setVisible(false);
			 */

			// uploadResultDivId.setVisible(true);

			// to send the optin for multiple messages.....
			if (optinMobileSet.size() > 0 && smsSettings != null && ocsmsGateway != null) {

				if (!ocsmsGateway.isPostPaid()
						&& !captiwayToSMSApiGateway.getBalance(ocsmsGateway, optinMobileSet.size())) {

					if (logger.isDebugEnabled())
						logger.debug("low credits with clickatell");
					optinMobileSet.clear();
					return;
				}
				if (((currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount())
						- (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount())) >= optinMobileSet
								.size()) {

					// logger.info("=======Sending to multi threaded class======");
					captiwayToSMSApiGateway.sendMultipleMobileDoubleOptin(ocsmsGateway, optinMobileSet, smsSettings);
					// logger.info("=======completed submission=========");
					// clickaTellApi.sendAutoResponse(PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID),
					// mobileStr, msgContent);
					/*
					 * if(currentUser.getParentUser() != null) { currentUser =
					 * currentUser.getParentUser(); }
					 */
					/*
					 * currentUser.setUsedSmsCount((currentUser.getUsedSmsCount() == null ? 0 :
					 * currentUser.getUsedSmsCount())+optinMobileSet.size());
					 * usersDao.saveOrUpdate(currentUser);
					 */
					// usersDao.updateUsedSMSCount(currentUser.getUserId(), optinMobileSet.size());
					usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), optinMobileSet.size());
					/**
					 * Update Sms Queue
					 */
					SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
					String mblNum = getMobileNumber(optinMobileSet);
					smsQueueHelper.updateSMSQueue(mblNum, smsSettings.getAutoResponse(), Constants.SMS_MSG_TYPE_OPTIN,
							currentUser, smsSettings.getSenderId());

				} else {

					// logger.debug("low credits with user...");
					optinMobileSet.clear();

					if (noSMSCredits == null) {

						noSMSCredits = "Please renew your SMS package.";
						messages = new Messages("Contact", "SMS credits are low.",
								"Total mobile contacts:" + mobileContacts + " ," + noSMSCredits, Calendar.getInstance(),
								"Inbox", false, "Info", currentUser);

						// messagesDao.saveOrUpdate(messages);
						messagesDaoForDML.saveOrUpdate(messages);
					}

					return;
				}

				optinMobileSet.clear();

			} // if

			// notify to the notifier about this event
			// as this is one obj saving at a time, no flag can be have to
			// justify is ET enabled or not
			if (etList != null && etList.size() > 0 && startContactId != null && endContactId != null) {
				// inputs are sales records,events list
				// if(logger.isInfoEnabled())
				// logger.info(" >>>>>>>>>>>>>>>observer called>>>>>>>>>>>>>");
				eventTriggerEventsObservable.notifyToObserver(etList, startContactId, endContactId,
						currentUser.getUserId(), Constants.POS_MAPPING_TYPE_CONTACTS);

			} // if

		} catch (FileNotFoundException e) {
			logger.error("Exception ::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	} // uploadContactsFromFile

	/**
	 * Comma Separated Mobile Numbers
	 * 
	 * @param optinMobileSet
	 * @return String
	 */
	private String getMobileNumber(Set<String> optinMobileSet) {
		StringBuilder result = new StringBuilder();
		for (String string : optinMobileSet) {
			result.append(string);
			result.append(",");
		}
		return result.length() > 0 ? result.substring(0, result.length() - 1) : "";
	}

	private void storePriorityVal(int valIndex, String key, String optCulField, String dataTypeStr,
			TreeMap<String, List<String>> prioMap) {

		// //logger.debug(">>>> "+posMapping.getUniquePriority());
		// if(valIndex == 0) return;

		// String key = ""+valIndex;
		logger.debug("key ::" + key);
		if (key == null || key.length() == 0) {
			return;
		}
		List<String> valList = prioMap.get(key);

		if (valList == null)
			valList = new ArrayList<String>();
		String newVal = "";

		if (optCulField.toLowerCase().startsWith("udf")) {
			newVal = optCulField.toLowerCase();
		}
		newVal = contactFieldMap.get(optCulField);

		// String dataTypeStr = dateType.trim().toLowerCase();

		if (dataTypeStr.startsWith("date") || dataTypeStr.startsWith("string")) {
			dataTypeStr = "string";
		}
		// logger.debug("...dataTypeStr>>"+dataTypeStr);

		String tempVal = valIndex + "|" + newVal + "|" + dataTypeStr;
		if (!valList.contains(tempVal)) {
			valList.add(tempVal);
		}

		prioMap.put(key, valList);

	} // storePriorityVal

	private final Pattern csvPattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");
	private ArrayList<String> allMatches = new ArrayList<String>();
	private Matcher matcher = null;

	public String[] parse(String csvLine) {
		matcher = csvPattern.matcher(csvLine);
		allMatches.clear();
		String match;
		while (matcher.find()) {
			match = matcher.group(1);
			if (match != null) {
				allMatches.add(match);
			} else {
				allMatches.add(matcher.group(2));
			}
		}

		if (allMatches.size() > 0) {
			return allMatches.toArray(new String[allMatches.size()]);
		} else {
			return new String[0];
		}
	}

	private Contacts setContactFieldsData(String[] nextLineStr, Contacts contact, int customerIdIdx, int emailIdIdx,
			int mobileIdx, int firstNameIdx, int lastNameIdx,int subsidiaryNumberIdx, int streetIdx,int addressTwoIdx, int cityIdx, int stateIdx, int zipIdx,
			int countryIdx, int genderIdx, int birthDayIdx, int anniversaryIdx, int homeStoreIdx, int createdDateIdx,
			String birthDayFormat, String anniversaryFormat, String createdDateFormat, Hashtable udfHashtable,
			Users currentUser) throws Exception {

		String tempStr = null;

		// set the ExternalId

		// if(contact.getExternalId()==null) contact.setExternalId("-1");

		if (customerIdIdx > -1 && customerIdIdx < nextLineStr.length
				&& (tempStr = nextLineStr[customerIdIdx].trim()).length() != 0 && !tempStr.equals("1")
				&& (contact.getExternalId() == null || contact.getExternalId().equals("-1"))) {

			contact.setExternalId(tempStr);
		}

		/*
		 * //set hpId if(addressUnitIdIndx < nextLineStr.length &&
		 * (tempStr=nextLineStr[addressUnitIdIndx].trim()).length() !=0 ){
		 * contact.setHpId(Long.parseLong(tempStr)); }
		 */

		// set the emailId
		if (emailIdIdx > -1 && emailIdIdx < nextLineStr.length
				&& (Utility.validateEmail(tempStr = nextLineStr[emailIdIdx].trim()))) {

			contact.setEmailId(tempStr);

		}

		// set the Phone

		if (mobileIdx > -1 && mobileIdx < nextLineStr.length
				&& (tempStr = nextLineStr[mobileIdx].trim()).length() != 0) {
			try {
				String mobileStr = Utility.phoneParse(tempStr,
						currentUser != null ? currentUser.getUserOrganization() : null);

				// Long mobilePhone = Long.parseLong(mobileStr);
				// contact.setPhone(mobilePhone);
				// TODO need to set lastSMSdate and mobile optin values to null
				// if it is
				// enabled with smsDouble-optin and medium is matched
				// Long mobilePhone = Long.parseLong(mobileStr);
				/*
				 * if(noSMSComplaincyMsg == null ) {
				 * 
				 * performMobileOptIn(contact, mobileStr); } else{
				 * 
				 * contact.setMobileOptin(false); contact.setMobileStatus(null);
				 * 
				 * }
				 */

				if (mobileStr != null && mobileStr.trim().length() > 0) {

					if (noSMSComplaincyMsg == null) {

						contact.setMobileStatus(performMobileOptIn(contact, currentUser, mobileStr));
					} else {

						contact.setMobileOptin(false);
						contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);

					}

					contact.setMobilePhone(mobileStr);
					LoyaltyProgramHelper.updateLoyaltyMembrshpPhone(contact, mobileStr);
					mobileContacts++;
					// contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
				} else {

					if (contact.getMobilePhone() == null) {

						contact.setMobileStatus(Constants.CON_MOBILE_STATUS_NOT_A_MOBILE);

					}

				}
			} catch (NumberFormatException e) {
				// //logger.info("inavlid Phone number"+tempStr);
			}
		}

		// set the First Name
		if (firstNameIdx > -1 && firstNameIdx < nextLineStr.length
				&& (tempStr = nextLineStr[firstNameIdx].trim()).length() != 0) {

			contact.setFirstName(tempStr);
		}

		// set the Last Name
		if (lastNameIdx > -1 && lastNameIdx < nextLineStr.length
				&& (tempStr = nextLineStr[lastNameIdx].trim()).length() != 0) {
			contact.setLastName(tempStr);
		}
		// set the Subsidiary Number
				if (subsidiaryNumberIdx > -1 && subsidiaryNumberIdx < nextLineStr.length
						&& (tempStr = nextLineStr[subsidiaryNumberIdx].trim()).length() != 0) {

					contact.setSubsidiaryNumber(tempStr);
				}

		// set the Address One
		if (streetIdx > -1 && streetIdx < nextLineStr.length
				&& (tempStr = nextLineStr[streetIdx].trim()).length() != 0) {
			contact.setAddressOne(tempStr);
		}
		// set the Address Two
		if (addressTwoIdx > -1 && addressTwoIdx < nextLineStr.length
				&& (tempStr = nextLineStr[addressTwoIdx].trim()).length() != 0) {
			contact.setAddressTwo(tempStr);
		}

		// set the City
		if (cityIdx > -1 && cityIdx < nextLineStr.length && (tempStr = nextLineStr[cityIdx].trim()).length() != 0) {
			contact.setCity(tempStr);
		}

		// set the State
		if (stateIdx > -1 && stateIdx < nextLineStr.length && (tempStr = nextLineStr[stateIdx].trim()).length() != 0) {
			contact.setState(tempStr);
		}

		// set the Pin

		String countryType = currentUser.getCountryType();
		try {
			if (Utility.zipValidateMap.containsKey(countryType)) {

				if (zipIdx > -1 && zipIdx < nextLineStr.length
						&& (tempStr = nextLineStr[zipIdx].trim()).length() != 0) {

					boolean zipCode = Utility.validateZipCode(tempStr, countryType);
					if (zipCode) {
						contact.setZip("" + tempStr);
					}

				}
			} else {

				if (zipIdx > -1 && zipIdx < nextLineStr.length
						&& (tempStr = nextLineStr[zipIdx].trim()).length() != 0) {

					if (tempStr.trim().length() == 6 || tempStr.trim().length() == 5) {
						int pinNum = Integer.parseInt(tempStr);
						contact.setZip("" + pinNum);
					}
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("Pin num is " + tempStr + " out of range Exception...");
		}

		/*
		 * if(zipIdx > -1 && zipIdx < nextLineStr.length && (tempStr =
		 * nextLineStr[zipIdx].trim()).length() !=0 ){ try {
		 * 
		 * int pinNum = Integer.parseInt(tempStr.substring(0, 5));
		 * contact.setPin(pinNum);
		 * 
		 * if(tempStr.trim().length() == 6 || tempStr.trim().length() == 5) { int pinNum
		 * = Integer.parseInt(tempStr); contact.setZip(""+pinNum);
		 * 
		 * int pinNum = Integer.parseInt(tempStr); contact.setPin(pinNum); }
		 * 
		 * if(tempStr.trim().length() == 6 || tempStr.trim().length() == 5) {
		 * contact.setZip(tempStr.trim());
		 * 
		 * }
		 * 
		 * } catch (Exception e) { // //logger.info("Pin num is "+tempStr+
		 * " out of range Exception..."); logger.error("Exception ::", e); } }
		 */

		// set the Country
		if (countryIdx > -1 && countryIdx < nextLineStr.length
				&& (tempStr = nextLineStr[countryIdx].trim()).length() != 0) {
			contact.setCountry(tempStr);
		}

		// set the Gender
		if (genderIdx > -1 && genderIdx < nextLineStr.length
				&& (tempStr = nextLineStr[genderIdx].trim()).length() != 0) {
			contact.setGender(tempStr);
		}

		// set the BirthDay
		// //logger.debug("birthDayIdx::"+birthDayIdx+">>>>>
		// "+nextLineStr[birthDayIdx].trim());

		if (birthDayIdx > -1 && birthDayIdx < nextLineStr.length
				&& (tempStr = nextLineStr[birthDayIdx].trim()).length() != 0) {

			try {
				DateFormat formatter;
				Date date;
				formatter = new SimpleDateFormat(birthDayFormat);
				date = (Date) formatter.parse(tempStr);
				Calendar dobCal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(birthDayFormat));
				dobCal.setTime(date);
				contact.setBirthDay(dobCal);

			} catch (Exception e) {
				// logger.info("dob date foramt not match with data",e);
			}
		}

		// set the Anniversary
		// tempStr = nextLineStr[anniversaryIdx].trim();
		if (anniversaryIdx > -1 && anniversaryIdx < nextLineStr.length
				&& (tempStr = nextLineStr[anniversaryIdx].trim()).length() != 0) {

			try {

				DateFormat formatter;
				Date date;
				formatter = new SimpleDateFormat(anniversaryFormat);
				date = (Date) formatter.parse(tempStr);
				Calendar anniCal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(anniversaryFormat));

				anniCal.setTime(date);
				contact.setAnniversary(anniCal);
			} catch (Exception e) {
				// //logger.info("Anniversary date foramt not match with data",e);
			}
		}

		// set homeStoreIdx
		// tempStr = nextLineStr[homeStoreIdx].trim();
		if (homeStoreIdx > -1 && homeStoreIdx < nextLineStr.length
				&& (tempStr = nextLineStr[homeStoreIdx].trim()).length() != 0) {

			contact.setHomeStore(tempStr);
		}

		// set Created Date
		if (createdDateIdx > -1 && createdDateIdx < nextLineStr.length
				&& (tempStr = nextLineStr[createdDateIdx].trim()).length() != 0) {

			try {

				DateFormat formatter;
				formatter = new SimpleDateFormat(createdDateFormat);
				Date date = (Date) formatter.parse(tempStr);
				Calendar creatDateCal = new MyCalendar(Calendar.getInstance(), null,
						MyCalendar.dateFormatMap.get(createdDateFormat));
				creatDateCal.setTime(date);

				if (creatDateCal.before(contact.getCreatedDate())) {
					contact.setCreatedDate(creatDateCal);
				}
			} catch (Exception e) {
				logger.info("created date foramt not match with data", e);
			}
		}

		// set the customField data

		if (udfHashtable.size() > 0) {

			String udfDataStr = null;
			String dateFormat = "";
			String dataTypeStr = "";
			Set<String> udfSet = udfHashtable.keySet();

			for (String custFieldName : udfSet) {

				String udfIdxDateFormtStr = (String) udfHashtable.get(custFieldName);
				String[] temArr = udfIdxDateFormtStr.split("_");
				int udfIdx = Integer.parseInt(temArr[0]);
				dataTypeStr = temArr[1];
				if (udfIdx < nextLineStr.length && (udfDataStr = nextLineStr[udfIdx].trim()).length() != 0) {

					if (dataTypeStr.trim().startsWith("Date")) {

						try {
							dateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(") + 1, dataTypeStr.indexOf(")"));
							if (!(CustomFieldValidator.validateDate(udfDataStr, dateFormat))) {
								// //logger.info(" Pos dateFormat not match with value so we ignore the Udf
								// data");
								continue;
							}

							DateFormat formatter;
							Date udfdDate;
							formatter = new SimpleDateFormat(dateFormat);
							udfdDate = (Date) formatter.parse(udfDataStr);
							Calendar udfCal = new MyCalendar(Calendar.getInstance(), null,
									MyCalendar.dateFormatMap.get(dateFormat));
							udfCal.setTime(udfdDate);
							udfDataStr = MyCalendar.calendarToString(udfCal, MyCalendar.dateFormatMap.get(dateFormat));
						} catch (Exception e) {
							logger.error("Exception ::", e);
						}

					}

					if (dataTypeStr.trim().startsWith("Number"))
						udfDataStr = Utility.validateNumberValue(udfDataStr);
					logger.info("udfDataStr number uploadContactThread is -----" + udfDataStr);
					if (dataTypeStr.trim().startsWith("Double"))
						udfDataStr = Utility.validateDoubleValue(udfDataStr);
					logger.info("udfDataStr Double uploadContactThread is -----" + udfDataStr);
					// if(udfDataStr == null && udfDataStr.trim().length() <= 0) continue;

					int UDFIdx = Integer.parseInt(custFieldName.substring("UDF".length()));
					try {

						// skuFile = setSKUCustFielddata(skuFile, UDfIdx,
						// udfDataStr);
						contact = setConatctCustFields(contact, UDFIdx, udfDataStr);
					} catch (Exception e) {
						logger.error("Exception ::", e);
						// //logger.info("Exception error getting while setting the Udf value due to
						// wrong values existed from the sku csv file .. so we ignore the udf data.. ");
					}

				}

			}

		} // if

		return contact;
	} // setContactFieldsData

	private Contacts setConatctCustFields(Contacts contact, int index, String udfData) throws Exception {

		switch (index) {
		case 1: {
			contact.setUdf1(udfData);
			return contact;
		}
		case 2: {
			contact.setUdf2(udfData);
			return contact;
		}
		case 3: {
			contact.setUdf3(udfData);
			return contact;
		}
		case 4: {
			contact.setUdf4(udfData);
			return contact;
		}
		case 5: {
			contact.setUdf5(udfData);
			return contact;
		}
		case 6: {
			contact.setUdf6(udfData);
			return contact;
		}
		case 7: {
			contact.setUdf7(udfData);
			return contact;
		}
		case 8: {
			contact.setUdf8(udfData);
			return contact;
		}
		case 9: {
			contact.setUdf9(udfData);
			return contact;
		}
		case 10: {
			contact.setUdf10(udfData);
			return contact;
		}
		case 11: {
			contact.setUdf11(udfData);
			return contact;
		}
		case 12: {
			contact.setUdf12(udfData);
			return contact;
		}
		case 13: {
			contact.setUdf13(udfData);
			return contact;
		}
		case 14: {
			contact.setUdf14(udfData);
			return contact;
		}
		case 15: {
			contact.setUdf15(udfData);
			return contact;
		}

		}

		return contact;

	} // setConatctCustFields

	public void sendWelcomeEmail(Contacts contact, MailingList mailingList, Users user) {

		// to send the loyalty related email
		CustomTemplates custTemplate = null;
		String message = PropertyUtil.getPropertyValueFromDB("welcomeMsgTemplate");

		if (mailingList.getWelcomeCustTempId() != null) {

			custTemplate = customTemplatesDao.findCustTemplateById(mailingList.getWelcomeCustTempId());
			if (custTemplate != null) {
				if(custTemplate != null && custTemplate.getHtmlText()!= null) {
					  message = custTemplate.getHtmlText();
				  }else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(custTemplate.getEditorType()) && custTemplate.getMyTemplateId()!=null) {
					  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(custTemplate.getMyTemplateId());
					  if(myTemplates != null)message = myTemplates.getContent();
				  }
			}
		}
		// logger.debug("-----------email----------"+tempContact.getEmailId());

		message = message.replace("[OrganisationName]", user.getUserOrganization().getOrganizationName())
				.replace("[senderReplyToEmailID]", user.getEmailId());

		EmailQueue testEmailQueue = new EmailQueue(mailingList.getWelcomeCustTempId(), Constants.EQ_TYPE_WELCOME_MAIL, message, "Active",
				contact.getEmailId(), user, MyCalendar.getNewCalendar(), " Welcome Mail", null, contact.getFirstName(),
				null, contact.getContactId());

		// testEmailQueue.setChildEmail(childEmail);
		// emailQueueDao.saveOrUpdate(testEmailQueue);
		emailQueueDaoForDML.saveOrUpdate(testEmailQueue);

	}// sendWelcomeEmail

	public String performMobileOptIn(Contacts contact, Users currentUser, String mobileStr) {

		if (!currentUser.isEnableSMS() || !currentUser.isConsiderSMSSettings()) {

			contact.setMobileOptin(false);
			return Constants.CON_MOBILE_STATUS_ACTIVE;
		}
		// Users currentUser = contact.getUsers();

		if (smsSettings == null || ocsmsGateway == null) {

			noSMSComplaincyMsg = ". No SMS Settings find for your user Account,"
					+ "SMS may not be sent to the mobile contacts.";

			contact.setMobileOptin(false);
			return Constants.CON_MOBILE_STATUS_ACTIVE;

		}

		// TODO need to set lastSMSdate and mobile optin values to null if it is
		// enabled with smsDouble-optin and medium is matched
		boolean isDifferentMobile = false;
		// String mobile = mPhoneIBoxId.getValue()+"";
		String conMobile = contact.getMobilePhone();
		// to identify whether entered one is same as previous mobile
		if (conMobile != null) {
			if (!mobileStr.equals(conMobile)) {

				if ((mobileStr.length() < conMobile.length() && !conMobile.endsWith(mobileStr))
						|| (conMobile.length() < mobileStr.length() && !mobileStr.endsWith(conMobile))
						|| mobileStr.length() == conMobile.length()) {

					isDifferentMobile = true;

				}

			}

		} // if
		else {
			contact.setMobilePhone(mobileStr);
			isDifferentMobile = true;

		}
		// contact.setMobilePhone(mobileStr);//can be deleted

		// do only when the existing phone number is not same with the entered
		byte optin = 0;
		String phone = contact.getMobilePhone();
		String mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;

		if (!isDifferentMobile && contact.getMobileStatus() != null
				&& contact.getMobileStatus().equals(Constants.CON_MOBILE_STATUS_OPTIN_PENDING)) {

			mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
			return mobileStatus;

		}
		if (contact.getOptinMedium() != null) {

			if (contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY)) {
				optin = 1;
			} else if (contact.getOptinMedium().startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM)) {
				optin = 2;
			} else if (contact.getOptinMedium().equalsIgnoreCase(Constants.CONTACT_OPTIN_MEDIUM_POS)) {
				optin = 4;
			}
		}

		Byte userOptinVal = smsSettings.getOptInMedium();

		userOptinVal = (SMSStatusCodes.userOptinMediumMap.get(currentUser.getCountryType())
				&& currentUser.getOptInMedium() != null) ? currentUser.getOptInMedium() : userOptinVal;

		if (smsSettings.isEnable() && userOptinVal != null && (userOptinVal.byteValue() & optin) > 0) {
			// TODO after the above todo done consider only one among these two
			// conditions on contact
			if ((contact.getLastSMSDate() == null && contact.isMobileOptin() != true) || (isDifferentMobile)) {

				mobileStatus = Constants.CON_MOBILE_STATUS_OPTIN_PENDING;
				contact.setMobileStatus(mobileStatus);
				contact.setLastSMSDate(Calendar.getInstance());
				contact.setMobileOptin(false);
				optinMobileSet.add(mobileStr);

				if (optinMobileSet.size() >= 100) {

					if (!ocsmsGateway.isPostPaid()
							&& !captiwayToSMSApiGateway.getBalance(ocsmsGateway, optinMobileSet.size())) {

						if (logger.isDebugEnabled())
							logger.debug("low credits with clickatell");
						optinMobileSet.clear();
						return mobileStatus;
					}
					if (((currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount())
							- (currentUser.getUsedSmsCount() == null ? 0
									: currentUser.getUsedSmsCount())) >= optinMobileSet.size()) {

						captiwayToSMSApiGateway.sendMultipleMobileDoubleOptin(ocsmsGateway, optinMobileSet,
								smsSettings);

						// clickaTellApi.sendAutoResponse(PropertyUtil.getPropertyValueFromDB(Constants.SMS_SENDERID),
						// mobileStr, msgContent);
						/*
						 * if(currentUser.getParentUser() != null) { currentUser =
						 * currentUser.getParentUser(); }
						 */
						/*
						 * currentUser.setUsedSmsCount((currentUser.getUsedSmsCount () == null ? 0 :
						 * currentUser.getUsedSmsCount()) +optinMobileSet.size());
						 * usersDao.saveOrUpdate(currentUser);
						 */
						// usersDao.updateUsedSMSCount(currentUser.getUserId(),optinMobileSet.size());
						usersDaoForDML.updateUsedSMSCount(currentUser.getUserId(), optinMobileSet.size());
						/**
						 * Update Sms Queue
						 */
						SmsQueueHelper smsQueueHelper = new SmsQueueHelper();
						String mblNum = getMobileNumber(optinMobileSet);
						smsQueueHelper.updateSMSQueue(mblNum, smsSettings.getAutoResponse(),
								Constants.SMS_MSG_TYPE_OPTIN, currentUser, smsSettings.getSenderId());
					} else {
						if (logger.isDebugEnabled())
							logger.debug("low credits with user...");
						optinMobileSet.clear();
						if (noSMSCredits == null) {

							noSMSCredits = "Please renew your SMS package.";
							Messages messages = new Messages("Contact", "SMS credits are low.",
									"Total mobile contacts:" + mobileContacts + " ," + noSMSCredits,
									Calendar.getInstance(), "Inbox", false, "Info", currentUser);

							// messagesDao.saveOrUpdate(messages);
							messagesDaoForDML.saveOrUpdate(messages);
						}

						return mobileStatus;
					}

					optinMobileSet.clear();
				}

				/*
				 * if(!clickaTellApi.getBalance(1)) {
				 * 
				 * logger.debug("low credits with clickatell"); return mobileStatus; }
				 * 
				 * if( ( (currentUser.getSmsCount() == null ? 0 : currentUser.getSmsCount()) -
				 * (currentUser.getUsedSmsCount() == null ? 0 : currentUser.getUsedSmsCount() )
				 * ) >= 1) {
				 * 
				 * //UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");
				 * 
				 * String msgContent = smsSettings.getKeywordResponse(); if(msgContent != null)
				 * {
				 * 
				 * msgContent = smsSettings.getMessageHeader() + " "+ msgContent; }
				 * CouponCodesDao couponCodesDao =
				 * (CouponCodesDao)context.getBean("couponCodesDao");
				 * 
				 * clickaTellApi.sendAutoResponse(PropertyUtil.
				 * getPropertyValueFromDB(Constants.SMS_SENDERID), phone, msgContent,
				 * couponCodesDao);
				 * 
				 * if(currentUser.getParentUser() != null) { currentUser =
				 * currentUser.getParentUser(); }
				 * currentUser.setUsedSmsCount(currentUser.getUsedSmsCount()+1);
				 * usersDao.saveOrUpdate(currentUser); }else {
				 * logger.debug("low credits with user..."); return mobileStatus;
				 * 
				 * }
				 */

			} // if
		} // if

		else {

			if (contact.getMobilePhone() != null && isDifferentMobile) {

				mobileStatus = Constants.CON_MOBILE_STATUS_ACTIVE;
				contact.setMobileStatus(mobileStatus);

				contact.setMobileOptin(false);
			}
		}

		return mobileStatus;

	} // performMobileOptIn

	boolean pollQueue() {
		pollObj = uploadQueue.poll();
		if (pollObj != null) {
			return true;
		} else {
			return false;
		}
	} // pollQueue
}
