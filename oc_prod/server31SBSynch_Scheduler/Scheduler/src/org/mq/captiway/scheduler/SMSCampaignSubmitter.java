package org.mq.captiway.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.mq.captiway.scheduler.beans.AutoProgram;
import org.mq.captiway.scheduler.beans.AutoProgramComponents;
import org.mq.captiway.scheduler.beans.CampaignSchedule;
import org.mq.captiway.scheduler.beans.ComponentsAndContacts;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.OCSMSGateway;
import org.mq.captiway.scheduler.beans.OrgSMSkeywords;
import org.mq.captiway.scheduler.beans.OrganizationStores;
import org.mq.captiway.scheduler.beans.ProgramOnlineReports;
import org.mq.captiway.scheduler.beans.SMSCampReportLists;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignSchedule;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.beans.SMSCampaignUrls;
import org.mq.captiway.scheduler.beans.SMSCampaigns;
import org.mq.captiway.scheduler.beans.SMSDeliveryReport;
import org.mq.captiway.scheduler.beans.SMSSettings;
import org.mq.captiway.scheduler.beans.SegmentRules;
import org.mq.captiway.scheduler.beans.UrlShortCodeMapping;
import org.mq.captiway.scheduler.beans.UserSMSGateway;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.beans.UsersDomains;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.ComponentsAndContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.dao.EventTriggerDao;
import org.mq.captiway.scheduler.dao.EventTriggerDaoForDML;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.MessagesDao;
import org.mq.captiway.scheduler.dao.MessagesDaoForDML;
import org.mq.captiway.scheduler.dao.OCSMSGatewayDao;
import org.mq.captiway.scheduler.dao.OrgSMSkeywordsDao;
import org.mq.captiway.scheduler.dao.OrganizationStoresDao;
import org.mq.captiway.scheduler.dao.ProgramOnlineReportsDao;
import org.mq.captiway.scheduler.dao.SMSCampReportListsDao;
import org.mq.captiway.scheduler.dao.SMSCampReportListsDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDao;
import org.mq.captiway.scheduler.dao.SMSCampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignScheduleDao;
import org.mq.captiway.scheduler.dao.SMSCampaignScheduleDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignUrlDao;
import org.mq.captiway.scheduler.dao.SMSCampaignUrlDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignsDao;
import org.mq.captiway.scheduler.dao.SMSCampaignsDaoForDML;
import org.mq.captiway.scheduler.dao.SMSDeliveryReportDao;
import org.mq.captiway.scheduler.dao.SMSDeliveryReportDaoForDML;
import org.mq.captiway.scheduler.dao.SMSSettingsDao;
import org.mq.captiway.scheduler.dao.SegmentRulesDao;
import org.mq.captiway.scheduler.dao.SegmentRulesDaoForDML;
import org.mq.captiway.scheduler.dao.TempActivityDataDao;
import org.mq.captiway.scheduler.dao.TempActivityDataDaoForDML;
import org.mq.captiway.scheduler.dao.TempComponentsDataDao;
import org.mq.captiway.scheduler.dao.UrlShortCodeMappingDao;
import org.mq.captiway.scheduler.dao.UserSMSGatewayDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.dao.UsersDaoForDML;
import org.mq.captiway.scheduler.services.CaptiwayToSMSApiGateway;
import org.mq.captiway.scheduler.services.SMSMultiThreadedSubmission;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.CouponCodesGeneration;
import org.mq.captiway.scheduler.utility.MobileCarriers;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PlaceHolders;
import org.mq.captiway.scheduler.utility.PrepareFinalHTML;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.ReplacePlaceHolders;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.captiway.scheduler.utility.SalesQueryGenerator;
import org.mq.captiway.scheduler.utility.TriggerQueryGenerator;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.business.helper.GatewayRequestProcessHelper;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
//import org.zkoss.jsp.zul.Const;

//import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

public class SMSCampaignSubmitter extends Thread {

	private SMSQueue smsQueue;
	private ApplicationContext applicationContext;
	private volatile boolean isRunning= false;
	private SMSCampaignScheduleDao smsCampaignScheduleDao;
	private SMSCampaignScheduleDaoForDML smsCampaignScheduleDaoForDML;
	private SMSCampaignsDao smsCampaignsDao;
	private SMSCampaignsDaoForDML smsCampaignsDaoForDML;
	private CampaignsDao campaignsDao;
	private UsersDao usersDao;
    private UsersDaoForDML usersDaoForDML;
	private static final Logger logger = LogManager
			.getLogger(Constants.SCHEDULER_LOGGER);
	private ContactsDao contactsDao;
	private ContactsDaoForDML contactsDaoForDML;
	private MessagesDao messagesDao;
	private MessagesDaoForDML messagesDaoForDML;
	private SegmentRulesDao segmentRulesDao;
	private SegmentRulesDaoForDML segmentRulesDaoForDML;
	private MailingListDao mailingListDao;
	
	private SMSCampaignSentDao smsCampaignSentDao;
	private SMSCampReportListsDao smsCampReportListsDao;
	private SMSCampReportListsDaoForDML smsCampReportListsDaoForDML;
	private SMSCampaignReportDao smsCampaignReportDao;
	private SMSCampaignReportDaoForDML smsCampaignReportDaoForDML;
	private UserSMSGatewayDao userSMSGatewayDao;
	private EventTriggerDaoForDML eventTriggerDaoForDML;
	private OCSMSGatewayDao OCSMSGatewayDao;
	// private CaptiwayToSMSApiGateway captiwayToSMSApiGateway;
	private SMSDeliveryReportDao smsDeliveryReportDao;
	private TempComponentsDataDao tempComponentsDataDao;
	private ComponentsAndContactsDao componentsAndContactsDao;
	private TempActivityDataDao tempActivityDataDao;
	private TempActivityDataDaoForDML tempActivityDataDaoForDML;
	private EventTriggerDao eventTriggerDao;
	private OrganizationStoresDao organizationStoresDao;

	private ProgramOnlineReportsDao programOnlineReportsDao;
	/*
	 * public SMSCampaignSubmitter(SMSQueue smsQueue) { this.smsQueue =
	 * smsQueue; }
	 */

	private static SMSCampaignSubmitter smsCampaignSubmitter;

	private SMSCampaignSubmitter() {
	}

	private static Object currentRunningObj = null;

	public static Object getCurrentRunningObj() {
		return currentRunningObj;
	}

	private static int NUMBER_OF_SMS_THREADS;

	public static void startSMSCampaignSubmitter(
			ApplicationContext applicationContext) {

		if (smsCampaignSubmitter == null || !smsCampaignSubmitter.isRunning()) {

			smsCampaignSubmitter = new SMSCampaignSubmitter(applicationContext);
			smsCampaignSubmitter.start();

			NUMBER_OF_SMS_THREADS = Integer
					.parseInt(PropertyUtil
							.getPropertyValueFromDB(Constants.PROPS_KEY_SENDGRID_THREAD_COUNT));
			;
		}// if

	}// startSMSCampaignSubmitter

	/*
	 * public static void startPmtaMailmergeSubmitter(ApplicationContext
	 * context) {
	 * 
	 * if(mailSubmitter==null || !mailSubmitter.isRunning()) {
	 * 
	 * mailSubmitter=new PmtaMailmergeSubmitter(context); mailSubmitter.start();
	 * } }
	 */

	private SMSCampaignSubmitter(ApplicationContext applicationContext) {

		this.applicationContext = applicationContext;
		this.smsQueue = (SMSQueue) applicationContext.getBean("smsQueue");

		contactsDao = (ContactsDao) applicationContext.getBean("contactsDao");
		contactsDaoForDML = (ContactsDaoForDML)applicationContext.getBean("contactsDaoForDML");		
		usersDao = (UsersDao) applicationContext.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML)applicationContext.getBean("usersDaoForDML");

		messagesDao = (MessagesDao) applicationContext.getBean("messagesDao");
		messagesDaoForDML = (MessagesDaoForDML) applicationContext.getBean("messagesDaoForDML");
		smsCampaignsDao = (SMSCampaignsDao) applicationContext
				.getBean("smsCampaignsDao");
		smsCampaignsDaoForDML = (SMSCampaignsDaoForDML) applicationContext
				.getBean("smsCampaignsDaoForDML");
		smsCampaignScheduleDao = (SMSCampaignScheduleDao) applicationContext
				.getBean("smsCampaignScheduleDao");
		smsCampaignScheduleDaoForDML = (SMSCampaignScheduleDaoForDML) applicationContext
				.getBean("smsCampaignScheduleDaoForDML");
		smsCampaignSentDao = (SMSCampaignSentDao) applicationContext
				.getBean("smsCampaignSentDao");
		smsCampaignReportDao = (SMSCampaignReportDao) applicationContext
				.getBean("smsCampaignReportDao");
		smsCampaignReportDaoForDML = (SMSCampaignReportDaoForDML) applicationContext
				.getBean("smsCampaignReportDaoForDML");
				// captiwayToSMSApiGateway =
		// (CaptiwayToSMSApiGateway)applicationContext.getBean("captiwayToSMSApiGateway");
		smsDeliveryReportDao = (SMSDeliveryReportDao) applicationContext
				.getBean("smsDeliveryReportDao");
		tempComponentsDataDao = (TempComponentsDataDao) applicationContext
				.getBean("tempComponentsDataDao");
		componentsAndContactsDao = (ComponentsAndContactsDao) applicationContext
				.getBean("componentsAndContactsDao");
		tempActivityDataDao = (TempActivityDataDao) applicationContext
				.getBean("tempActivityDataDao");
		tempActivityDataDaoForDML = (TempActivityDataDaoForDML) applicationContext
				.getBean("tempActivityDataDaoForDML");
		smsCampReportListsDao = (SMSCampReportListsDao) applicationContext
				.getBean("smsCampReportListsDao");
		smsCampReportListsDaoForDML = (SMSCampReportListsDaoForDML) applicationContext
				.getBean("smsCampReportListsDaoForDML");
		programOnlineReportsDao = (ProgramOnlineReportsDao) applicationContext
				.getBean("programOnlineReportsDao");
		segmentRulesDao = (SegmentRulesDao) applicationContext
				.getBean("segmentRulesDao");
		mailingListDao = (MailingListDao) applicationContext
				.getBean("mailingListDao");
		eventTriggerDao = (EventTriggerDao) applicationContext
				.getBean("eventTriggerDao");
		eventTriggerDaoForDML = (EventTriggerDaoForDML)applicationContext.getBean("eventTriggerDaoForDML");
		organizationStoresDao = (OrganizationStoresDao) applicationContext
				.getBean("organizationStoresDao");
		campaignsDao = (CampaignsDao) applicationContext
				.getBean("campaignsDao");
		userSMSGatewayDao = (UserSMSGatewayDao) applicationContext
				.getBean("userSMSGatewayDao");
		OCSMSGatewayDao = (OCSMSGatewayDao) applicationContext
				.getBean("OCSMSGatewayDao");
		segmentRulesDaoForDML = (SegmentRulesDaoForDML)applicationContext.getBean("segmentRulesDaoForDML");
		// captiwayToSMSApiGateway =
		// (CaptiwayToSMSApiGateway)applicationContext.getBean("captiwayToSMSApiGateway");
	}

	private SMSGatewaySessionMonitor sessionMonitorService;

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setIsRunning(boolean newIsRunning) {
		isRunning = newIsRunning;
	}

	private static Map<String, OrganizationStores> OrgStoreMap = null;
	private static Map<String, String> OrgStoreAddressMap = null;

	@Override
	public void run() {

		// isRunning = true;
		setIsRunning(true);
		SMSCampaigns tempSMSCampaign = null;

		SMSCampaignSchedule tempSMSCampaignSchedule;
		Object obj = null;
		String fromSourceType = "";

		logger.debug("started ");

		sessionMonitorService = (SMSGatewaySessionMonitor) ServiceLocator
				.getInstance().getBeanByName(
						OCConstants.SMSGATEWAYSESSIONMONITOR);

		if (!sessionMonitorService.isRunning()) {
			logger.debug("processor is not running , try to ping it....");
			sessionMonitorService.run();
		}

		try {
			if (logger.isInfoEnabled())
				logger.info("smsQueue Size=" + smsQueue.getQueueSize());
			// while((currentRunningObj = obj = queue.getObjFromQueue()) !=
			// null) {
			while ((currentRunningObj = obj = smsQueue.getObjFromQueue()) != null) {
				// keep it for testing
				/*
				 * try { Thread.sleep(10000); } catch (Exception e) { // TODO
				 * Auto-generated catch block logger.error("Exception ::::" ,
				 * e); }
				 */
				OrgStoreMap = null;
				OrgStoreAddressMap = null;

				if (obj instanceof SMSCampaignSchedule) {

					tempSMSCampaignSchedule = (SMSCampaignSchedule) obj;

					if (checkAnyOtherObjectRunning()) { // need to check for
														// safety

						List<SMSCampaignSchedule> rePutScheduleList = new ArrayList<SMSCampaignSchedule>();
						rePutScheduleList.add(tempSMSCampaignSchedule);
						smsQueue.addCollection(rePutScheduleList);
						continue;

					}// if

					tempSMSCampaign = smsCampaignsDao
							.findByCampaignId(tempSMSCampaignSchedule
									.getSmsCampaignId());
					fromSourceType = Constants.SOURCE_SMS_CAMPAIGN;

					sendSMSCampaign(tempSMSCampaign, tempSMSCampaignSchedule,
							fromSourceType);

					contactsDaoForDML.deleteSmsTempContacts();
					tempSMSCampaignSchedule.setStatus((byte) 1);
					tempSMSCampaign.setStatus("Sent");
				} // if
				else if (obj instanceof SMSDeliveryReport) {// this is not all
															// required now

					// ****** need to update the smsCampaignsent status based on
					// the requestId given in the reponse text*******
					if (logger.isInfoEnabled())
						logger.info("----just entered for fetching delivery report-----");
					SMSDeliveryReport smsDeliveryReport = (SMSDeliveryReport) obj;

					/*
					 * if(checkAnyOtherObjectRunning()) { //need to check for
					 * safety
					 * 
					 * List<SMSDeliveryReport> rePutDeliveryReportList = new
					 * ArrayList<SMSDeliveryReport>();
					 * rePutDeliveryReportList.add(smsDeliveryReport);
					 * smsQueue.addCollection(rePutDeliveryReportList);
					 * continue;
					 * 
					 * }//if
					 */

					if (logger.isInfoEnabled())
						logger.info("the object in the queue is===>"
								+ smsDeliveryReport.toString());
					// String quickresponseContent =
					// smsDeliveryReport.getRequestId();
					// String requestId = parse(quickresponseContent);
					// captiwayToSMSApiGateway.requestToSMSApi(Constants.USER_SMSTOOL_CLICKATELL,
					// smsDeliveryReport);
					// update the open count in smsCampaignreport based on the
					// status in smsCampaignsent as 'delivered'
					int deliveredCount = smsCampaignSentDao.getDeliveredCount(
							smsDeliveryReport.getSmsCampRepId(),
							smsDeliveryReport.getRequestId(), "Delivered");
					SMSCampaignReport smsCampaignReport = smsCampaignReportDao
							.findByRepId(smsDeliveryReport.getSmsCampRepId());
					smsCampaignReport.setOpens(deliveredCount);
					//smsCampaignReportDao.saveOrUpdate(smsCampaignReport);
					smsCampaignReportDaoForDML.saveOrUpdate(smsCampaignReport);


				} else if (obj instanceof AutoProgramComponents) {
					if (logger.isInfoEnabled())
						logger.info("------picked up the AutoProgramcomponent----");
					AutoProgramComponents autoProgramComponents = (AutoProgramComponents) obj;
					// tempSMSCampaign =
					// smsCampaignsDao.findByCampaignId(autoProgramComponents.getSupportId());
					fromSourceType = Constants.SOURCE_MARKETING_PROGRAM + "_"
							+ autoProgramComponents.getComponentWinId();
					if (checkAnyOtherObjectRunning()) { // need to check for
														// safety

						List<AutoProgramComponents> rePutProgComponentsList = new ArrayList<AutoProgramComponents>();
						rePutProgComponentsList.add(autoProgramComponents);
						smsQueue.addCollection(rePutProgComponentsList);
						continue;

					}// if
					sendProgramSMSCampaign(autoProgramComponents,
							fromSourceType);
					if (logger.isInfoEnabled())
						logger.info(" returned from sendProgramSMSCampaign()...exiting smsCampaignSubmitter ");

				} else if (obj instanceof EventTrigger) { // added for
															// eventTrigger sms
															// changes begins

					/*
					 * This part has been added for EventTrigger
					 */
					// isCampaignSchedule = true;
					EventTrigger eventTrigger = (EventTrigger) obj;

					if (eventTrigger == null) {

						if (logger.isInfoEnabled())
							logger.info(" eventTrigger obj is null ");
						continue;

					}

					if (checkAnyOtherObjectRunning()) { // need to check for
														// safety

						List<EventTrigger> rePutTriggerList = new ArrayList<EventTrigger>();
						rePutTriggerList.add(eventTrigger);
						smsQueue.addCollection(rePutTriggerList);
						continue;

					}// if

					int optionsFlag;
					fromSourceType = Constants.SOURCE_TRIGGER + "_"
							+ eventTrigger.getTriggerName();

					if (logger.isInfoEnabled())
						logger.info(" picked an EventTrigger obj from smsQueue");

					optionsFlag = eventTrigger.getOptionsFlag();

					Users userObj = usersDao.find(eventTrigger.getUsers()
							.getUserId());

					if (logger.isInfoEnabled())
						logger.info("optionsFlag = " + optionsFlag);

					if (userObj.getUserName() == null) {
						// this is needed if the user doesnot exists
						if (logger.isWarnEnabled())
							logger.warn("**************EventTrigger:  No User object found for the given user_id :"
									+ eventTrigger.getUsers().getUserId()
									+ "continuing with the next object in smsQueue");
						continue;
					}

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date currDate = sdf.parse(sdf.format(new Date()));

					Calendar cal = userObj.getPackageExpiryDate();
					Date expiryDate = sdf.parse(sdf.format(cal.getTime()));

					if (!userObj.isEnabled() || expiryDate.before(currDate)) {
						if (logger.isWarnEnabled())
							logger.warn("**************EventTrigger: User expired or deactivated, continuing with the next object in smsQueue");
						continue;
					}

					if ((optionsFlag & Constants.ET_TRIGGER_IS_ACTIVE_FLAG) == Constants.ET_TRIGGER_IS_ACTIVE_FLAG) {
						// this is needed if the trigger is inactive
						if (logger.isInfoEnabled())
							logger.info("calling processEventTrigger ");

						sendEventTriggerSMSCampaign(eventTrigger,
								fromSourceType);
						contactsDaoForDML.deleteSmsTempContacts();
						if (logger.isInfoEnabled())
							logger.info("After processTrigger clearing sms temp contacts table");

					} else {

						if (logger.isInfoEnabled())
							logger.info("the trigger is not active...it must be in draft stage..please make it active");
						continue;
					}

					if (logger.isInfoEnabled())
						logger.info(" returned from sendEventTriggerSMSCampaign()...exiting smsCampaignSubmitter ");
				}
			} // while

		} catch (Exception e) {
			logger.error("Exception ::::", e);
		} finally {
			// isRunning = false;

			setIsRunning(false);
			smsQueue.removeObjectFromQueue(obj);
			// TODO need to know whether we should delete the object from the
			// smsqueue or not
		}
	} // run

	private boolean checkAnyOtherObjectRunning() {

		int count = contactsDao
				.getSegmentedContactsCount("SELECT cid FROM sms_tempcontacts");

		logger.debug("before running any object the number of records in the sms temp table are ::"
				+ count);
		if (count > 0)
			return true;
		else
			return false;

	}// checkAnyOtherObjectRunning()

	/**
	 * this method sends the smsCampaign to captiwaytosmsgateway
	 * 
	 * @param smsCampaign
	 * @param smsCampaignSchedule
	 * @param fromSource
	 */
	private void sendSMSCampaign(SMSCampaigns smsCampaign,
			SMSCampaignSchedule smsCampaignSchedule, String fromSource) {
		logger.debug(">>>>>>> Started SMSCampaignSubmitter :: sendSMSCampaign <<<<<<< ");
		try {

			logger.debug("-----just entered into sendSMSCampaign------ "
					+ smsCampaignSchedule.getSmsCsId().longValue());

			if (smsCampaign == null) {
				if (logger.isInfoEnabled())
					logger.info("got SMS campaign as null,returning from sending SMS....");
				return;

			}

			Users user = smsCampaign.getUsers();
			// String userSMSTool = user.getUserSMSTool();

			Long userId = user.getUserId();
			Long orgId = user.getUserOrganization().getUserOrgId();

			String smsCampaignName = smsCampaign.getSmsCampaignName();
			String msgContent = smsCampaign.getMessageContent();

			if (msgContent == null || msgContent.trim().length() == 0) {

				if (logger.isErrorEnabled())
					logger.error("NO message content found for SMS : "
							+ smsCampaignName);
				return;

			}

			if (!user.isEnableSMS()) {

				Messages messages = new Messages(
						"SMS Campaign",
						"SMS camapign -" + smsCampaignName
								+ " can not be reached",
						"SMS package is not enabled for your user account. Please contact Admin to enable SMS feature."
								+ "SMS campaign could not be sent.",
						Calendar.getInstance(), "Inbox", false, "Info",
						smsCampaign.getUsers());

				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
				return;

			}

			String smsCampaignType = smsCampaign.getMessageType();
			boolean isSendToAll = smsCampaign.isEnableEntireList();

			String accountType = SMSStatusCodes.countryCampValueMap.get(
					user.getCountryType()).get(smsCampaignType);

			if (SMSStatusCodes.optInMap.get(user.getCountryType())
					&& !isSendToAll) {

				accountType = Constants.SMS_SENDING_TYPE_OPTIN;
			}

			UserSMSGateway userSMSGateway = userSMSGatewayDao.findByUserId(
					userId, accountType);

			if (userSMSGateway == null) {

				Messages messages = new Messages(
						"SMS Campaign",
						"SMS can not be reached",
						"No SMS setup found for this SMS type in your user account. Please contact Admin to enable SMS feature."
								+ "SMS campaign could not be sent.",
						Calendar.getInstance(), "Inbox", false, "Info",
						smsCampaign.getUsers());

				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);

				return;

			}

			OCSMSGateway ocgateway = OCSMSGatewayDao.findById(userSMSGateway
					.getGatewayId());

			if (ocgateway == null) {

				Messages messages = new Messages(
						"SMS Campaign",
						"SMS can not be reached",
						"No SMS setup found for this SMS type in your user account. Please contact Admin to enable SMS feature."
								+ "SMS campaign could not be sent.",
						Calendar.getInstance(), "Inbox", false, "Info",
						smsCampaign.getUsers());

				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
				return;

			}

			if (SMSStatusCodes.optOutFooterMap.get(user.getCountryType())) {// if(userSMSTool.equals(Constants.USER_SMSTOOL_CLICKATELL))
																			// {

				SMSSettingsDao smsSettingsDao = (SMSSettingsDao) applicationContext
						.getBean("smsSettingsDao");
				SMSSettings smsSettings = smsSettingsDao.findByUser(userId,
						OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
				// dont allow user from sending campaign
				if (smsSettings == null) {

					Messages messages = new Messages(
							"SMS Campaign",
							"SMS can not be reached",
							"No SMS settings found for your user account. Please contact Admin to have SMS feature."
									+ "SMS campaign could not be sent.",
							Calendar.getInstance(), "Inbox", false, "Info",
							smsCampaign.getUsers());

					//messagesDao.saveOrUpdate(messages);
					messagesDaoForDML.saveOrUpdate(messages);

					return;

				}

				if (smsSettings.getMessageHeader() != null) {
					// hdr ftr---rajeev
					// msgContent =
					// smsSettings.getMessageHeader()+" "+msgContent;
					msgContent = smsSettings.getMessageHeader() + "\n\n"
							+ msgContent; // newline after header
					logger.info("msgContent 2>>>>>>>>>>>>>" + msgContent);

				}

				if (smsCampaign.isEnableEntireList()) {
					SMSSettings optoutSMSSettings = smsSettingsDao
							.findByUser(userId,
									OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT);
					msgContent = msgContent
							+ (optoutSMSSettings != null
									&& optoutSMSSettings.getKeyword() != null ?
							// (" "+"Reply "+optoutSMSSettings.getKeyword()+" 2 Optout.")
							// :
							// PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
							("\n\n" + "Reply " + optoutSMSSettings.getKeyword() + " 2 Optout.")
									: "\n\n"
											+ PropertyUtil
													.getPropertyValueFromDB("SMSFooterContent"));

				}
				logger.info("msgContent 3>>>>>>>>>>>>>" + msgContent);
			}

			if (logger.isInfoEnabled())
				logger.info("SMS Name=" + smsCampaignName);

			// get the user sender id;

			CaptiwayToSMSApiGateway captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway(
					applicationContext);

			String useMqsStr = PropertyUtil.getPropertyValueFromDB("useMQS");
			boolean useMQS = (useMqsStr == null ? true : useMqsStr
					.equalsIgnoreCase("true"));

			Set<MailingList> mlList = smsCampaign.getMailingLists();
			if (mlList == null || mlList.isEmpty()) {

				Messages msgs = new Messages(
						"Send SMS Campaign",
						"SMS campaign sending failed ",
						"'"
								+ smsCampaignName
								+ "' couldn't be sent, no list has configured to this campaign(might have deleted)",
						Calendar.getInstance(), "Inbox", false, "Info", user);

				//messagesDao.saveOrUpdate(msgs);
				messagesDaoForDML.saveOrUpdate(msgs);
				return;

			}
			if (checkForExpiredKeyWords(msgContent, user, smsCampaignName,
					orgId, false)) {
				smsCampaignSchedule.setStatus((byte) 6);

				// smsCampaignScheduleDao.saveOrUpdate(smsCampaignSchedule);
				smsCampaignScheduleDaoForDML.saveOrUpdate(smsCampaignSchedule);

				return;

			}

			String mlStr = Constants.STRING_NILL;
			String listsName = "";
			String listIdsStr = "";
			String segmentListQuery = Constants.STRING_NILL;
			String qryStr = null;

			for (MailingList mailingList : mlList) {

				if (mlStr.length() > 0)
					mlStr += ",";

				mlStr += mailingList.getListId().longValue();

				if (listsName.length() > 0)
					listsName += ",";
				listsName += mailingList.getListName();

			} // for each mailing list

			contactsDaoForDML.deleteSmsTempContacts();

			boolean isSegment = false;
			int suppressedCount = 0;
			int configuredCount = 0;
			int totalContacts = 0;
			int totalAvailabeleContacts = 0;
			String tolCountQry = "";

			int prefernceCount = 0;

			String listType = smsCampaign.getListType();

			if (listType == null || listType.equalsIgnoreCase("Total")) {

				isSegment = false;

				qryStr = "INSERT IGNORE INTO sms_tempcontacts("
						+ Constants.QRY_COLUMNS_TEMP_CONTACTS
						+ ", cf_value, domain, event_source_id)  "
						+ "(SELECT DISTINCT "
						+ Constants.QRY_COLUMNS_CONTACTS
						+ ",NULL,"
						+ " SUBSTRING_INDEX(c.email_id, '@', -1), null FROM contacts c, mailing_lists ml "
						+ " WHERE c.user_id = ml.user_id AND ml.list_id IN("
						+ mlStr
						+ ")"
						+ " AND (c.mlbits & ml.mlbit) >0 AND c.mobile_phone IS NOT NULL"
						+ " AND c.mobile_phone !='' AND c.mobile_status='"
						+ Constants.CON_MOBILE_STATUS_ACTIVE
						+ "'"
						+ (!smsCampaign.isEnableEntireList() ? " AND c.mobile_opt_in=1)"
								: ")");

				tolCountQry = "SELECT COUNT(distinct c.cid) FROM contacts c, mailing_lists ml "
						+ " WHERE c.user_id = ml.user_id AND ml.list_id in("
						+ mlStr
						+ ") "
						+ " AND (c.mlbits & ml.mlbit) >0 AND c.mobile_phone IS NOT NULL AND c.mobile_phone !='' "
						+ " AND c.mobile_status='"
						+ Constants.CON_MOBILE_STATUS_ACTIVE
						+ "'"
						+ (!smsCampaign.isEnableEntireList() ? " AND c.mobile_opt_in=1"
								: "");

				// totalAvailabeleContacts = configuredCount =
				// contactsDao.createSmsTempContacts(qryStr, null, userId ,
				// false, MobileCarriers.mobileCarrierMap.get(userSMSTool) );

				totalAvailabeleContacts = configuredCount = contactsDaoForDML
						.createSmsTempContacts(qryStr, null, user, false, user
								.getCountryCarrier().shortValue());
			} else {

				isSegment = true;

				if (listType.toLowerCase().trim().startsWith("segment:")) {

					boolean success = true;
					String msgStr = "";
					Calendar cal = Calendar.getInstance();
					List<SegmentRules> segmentRules = segmentRulesDao
							.findById(listType.split(":")[1]);

					try {

						if (segmentRules == null) {

							success = false;
							msgStr = "Segments configured to this campaign no longer exists. You might have deleted them.";
							return;
						}// if none of configured segments found

						for (SegmentRules segmentRule : segmentRules) {

							if (segmentRule == null) {

								msgStr = "One of the segments configured to this campaign no longer exists. You might have deleted it.";
								continue;

							}// if
							if (segmentRule.getSegRule() == null) {

								msgStr = "Invalid segments configured to this campaign ";
								continue;
							}
							Set<MailingList> mlistSet = new HashSet<MailingList>();
							List<MailingList> mailinglLists = mailingListDao
									.findByIds(segmentRule
											.getSegmentMlistIdsStr());
							if (mailinglLists == null) {
								continue;
							}

							mlistSet.addAll(mailinglLists);
							long mlsbit = Utility.getMlsBit(mlistSet);

							String segmentQuery = SalesQueryGenerator
									.generateSMSListSegmentQuery(
											segmentRule.getSegRule(), mlsbit);

							if (segmentQuery == null) {

								msgStr = "Invalid segments configured to this campaign ";
								continue;
							}

							String segmentCountQry = SalesQueryGenerator
									.generateSMSListSegmentCountQuery(
											segmentRule.getSegRule(), mlsbit);

							if (segmentCountQry == null) {

								msgStr = "Invalid segments configured to this campaign ";
								continue;
							}

							if (SalesQueryGenerator
									.CheckForIsLatestCamapignIdsFlag(segmentRule
											.getSegRule())) {
								String csCampIds = SalesQueryGenerator
										.getCamapignIdsFroFirstToken(segmentRule
												.getSegRule());

								if (csCampIds != null) {
									String crIDs = Constants.STRING_NILL;
									List<Object[]> campList = campaignsDao
											.findAllLatestSentCampaignsBySql(
													segmentRule.getUserId(),
													csCampIds);
									if (campList != null) {
										for (Object[] crArr : campList) {

											if (!crIDs.isEmpty())
												crIDs += Constants.DELIMETER_COMMA;
											crIDs += ((Long) crArr[0])
													.longValue();

										}
									}

									segmentQuery = segmentQuery
											.replace(
													Constants.INTERACTION_CAMPAIGN_CRID_PH,
													("AND cr_id in(" + crIDs + ")"));
									segmentCountQry = segmentCountQry
											.replace(
													Constants.INTERACTION_CAMPAIGN_CRID_PH,
													("AND cr_id in(" + crIDs + ")"));
								}
							}

							segmentQuery = segmentQuery
									.replace(
											"<MOBILEOPTIN>",
											!smsCampaign.isEnableEntireList() ? " AND c.mobile_opt_in=1"
													: "");
							segmentCountQry = segmentCountQry
									.replace(
											"<MOBILEOPTIN>",
											!smsCampaign.isEnableEntireList() ? " AND c.mobile_opt_in=1"
													: "");

							// >>>>>>>>>>>>>>>>>updating seg rules
							// starts<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
							long startTime = System.currentTimeMillis();

							updateSegmentCountAndQuery(segmentRule,
									segmentQuery, segmentCountQry);

							Calendar calStartTimeObj = Calendar.getInstance();
							calStartTimeObj.setTimeInMillis(startTime);
							String finalStartTime = calStartTimeObj
									.get(Calendar.DATE)
									+ "-"
									+ (calStartTimeObj.get(Calendar.MONTH) + 1)
									+ " "
									+ calStartTimeObj.HOUR_OF_DAY
									+ ":"
									+ calStartTimeObj.MINUTE
									+ ":"
									+ calStartTimeObj.SECOND;

							Calendar calEndTimeObj = Calendar.getInstance();
							calEndTimeObj.setTimeInMillis(System
									.currentTimeMillis());
							String finalEndTime = calEndTimeObj
									.get(Calendar.DATE)
									+ "-"
									+ (calEndTimeObj.get(Calendar.MONTH) + 1)
									+ " "
									+ calEndTimeObj.HOUR_OF_DAY
									+ ":"
									+ calEndTimeObj.MINUTE
									+ ":"
									+ calEndTimeObj.SECOND;

							logger.info("Elapsed time for this segment rule, id=="
									+ segmentRule.getSegRuleId()
									+ "  name of segment rule==="
									+ segmentRule.getSegRuleName()
									+ " to run  "
									+ (System.currentTimeMillis() - startTime)
									+ " Millisecond");

							// >>>>>>>>>>>>>>>>>updating seg rules
							// ends<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

							if (!segmentListQuery.isEmpty())
								segmentListQuery += " UNION ";
							segmentListQuery += segmentQuery;

							qryStr = "INSERT IGNORE INTO sms_tempcontacts ("
									+ Constants.QRY_COLUMNS_TEMP_CONTACTS
									+ ", cf_value, domain, event_source_id)  "
									+ segmentQuery;

							totalAvailabeleContacts = configuredCount += contactsDaoForDML
									.createSmsTempContacts(qryStr, null, user,
											false, user.getCountryCarrier());

							if (tolCountQry.length() > 0)
								tolCountQry += "UNION";
							tolCountQry += segmentCountQry;

						}// for

					} catch (Exception e) {
						success = false;
						if (logger.isErrorEnabled())
							logger.error(
									"** Exception while getting the available count"
											+ " for the user:"
											+ user.getUserId(), e);
						return;
					} finally {

						if (!success) {

							if (logger.isInfoEnabled())
								logger.info(">>>>>>>>>>..." + msgStr + " "
										+ user + " ");
							smsCampaignSchedule.setStatus((byte) 3);
							// smsCampaignScheduleDao.saveOrUpdate(smsCampaignSchedule);
							smsCampaignScheduleDaoForDML
									.saveOrUpdate(smsCampaignSchedule);

							if (msgStr != null && msgStr.isEmpty()) {

								msgStr = "SMS Campaign send failed due to Some error in server connections.";
							}
							Messages msgs = new Messages("Send SMS Campaign",
									"SMS campaign sending failed ", msgStr,
									cal, "Inbox", false, "Info", user);

							//messagesDao.saveOrUpdate(msgs);
							messagesDaoForDML.saveOrUpdate(msgs);

						}

					}// finally

					/*
					 * SegmentRules segmentRules =
					 * segmentRulesDao.findById(listType.split(":")[1]);
					 * 
					 * if(segmentRules == null) {
					 * 
					 * Messages messages = new Messages("SMS Campaign",
					 * "Segment is missed",
					 * "Segment associated to this campaign is not found,You might deleted this before this schedule,"
					 * + "Campaign can not be sent.", Calendar.getInstance(),
					 * "Inbox", false, "Info", smsCampaign.getUsers());
					 * 
					 * messagesDao.saveOrUpdate(messages);
					 * 
					 * return; }
					 * 
					 * if(segmentRules != null) { listType =
					 * segmentRules.getSegRule(); }
					 */
				}// if

			}// else

			// no parent schedules exists for the SMS for the time being
			// if is a segment,based on sms sending criteria mobile_opt_in
			// should be append to qry.
			/*
			 * if(smsCampaignSchedule.getParentId() == null && isSegment) {
			 * 
			 * segmentListQuery =
			 * SalesQueryGenerator.generateSMSListSegmentQuery( listType,
			 * Utility.getMlsBit(mlList)); //if(logger.isinfoEnabled()) {
			 * 
			 * logger.info(" Generated Query :"+segmentListQuery);
			 * 
			 * //} if(segmentListQuery == null) {
			 * 
			 * logger.error("problem in segment query generation"); return;
			 * 
			 * }
			 * 
			 * segmentListQuery = segmentListQuery.replace("<MOBILEOPTIN>",
			 * !smsCampaign.isEnableEntireList() ? " AND c.mobile_opt_in=1" :
			 * ""); qryStr =
			 * "INSERT IGNORE INTO sms_tempcontacts ("+segmentListQuery+")";
			 * 
			 * tolCountQry = segmentListQuery; } else {
			 * 
			 * qryStr =
			 * "INSERT IGNORE INTO sms_tempcontacts(SELECT DISTINCT c.*,NULL," +
			 * " SUBSTRING_INDEX(c.email_id, '@', -1) FROM contacts c, mailing_lists ml "
			 * + " WHERE c.user_id = ml.user_id AND ml.list_id IN("+ mlStr+ ")"
			 * + " AND (c.mlbits & ml.mlbit) >0 AND c.mobile_phone IS NOT NULL"
			 * + " AND c.mobile_phone !='' AND c.mobile_status='"+Constants.
			 * CON_MOBILE_STATUS_ACTIVE+"')"+ (!smsCampaign.isEnableEntireList()
			 * ? " AND c.mobile_opt_in=1" : "");
			 * 
			 * tolCountQry =
			 * "SELECT COUNT(distinct c.cid) FROM contacts c, mailing_lists ml "
			 * + " WHERE c.user_id = ml.user_id AND ml.list_id in("+mlStr+") " +
			 * " AND (c.mlbits & ml.mlbit) >0 AND c.mobile_phone IS NOT NULL AND c.mobile_phone !=''"
			 * + (!smsCampaign.isEnableEntireList() ? " AND c.mobile_opt_in=1" :
			 * ""); }
			 */

			// TODO need to work with messages here........................

			// int totalContacts =
			// contactsDao.getSegmentedContactsCount(tolCountQry);
			totalContacts = contactsDao.getSegmentedContactsCount(tolCountQry);

			if (totalContacts <= 0) {

				// TODO need to create report for it with all 0 counts....
				logger.info("no conatcts in sms_tempcontacts " + totalContacts);

				try {

					SMSCampaignReport smsCampaignReport = null;

					smsCampaignReport = new SMSCampaignReport(
							smsCampaign.getUsers(), smsCampaignName,
							msgContent, Calendar.getInstance(), 0, 0, 0, 0, 0,
							Constants.CR_STATUS_SENDING, fromSource);

					logger.warn(">>>>>>> sms campaign submission is failed found 0 active contacts, campaign id : "
							+ smsCampaign.getSmsCampaignId());

					String msgStr = "SMS Campaign :"
							+ smsCampaignName
							+ "\n"
							+ "Found 0 Active contacts in the mailing lists which "
							+ "are configured for the SMS campaign.\n"
							+ "Reason may be configured contacts exists in the suppression contacts list "
							+ "or contacts status changed by user/recipient himself unsubscribed";

					Messages messages = new Messages("SMS Campaign ",
							"No Active contacts", msgStr,
							Calendar.getInstance(), "Inbox", false, "Info",
							user);
					//messagesDao.saveOrUpdate(messages);
					messagesDaoForDML.saveOrUpdate(messages);

					smsCampaignReport.setStatus(Constants.CR_STATUS_SENT);
					//smsCampaignReportDao.saveOrUpdate(smsCampaignReport);
					smsCampaignReportDaoForDML.saveOrUpdate(smsCampaignReport);

					smsCampaignSchedule.setStatus((byte) 1);
					smsCampaignSchedule.setCrId(smsCampaignReport.getSmsCrId());
					// smsCampaignScheduleDao.saveOrUpdate(smsCampaignSchedule);
					smsCampaignScheduleDaoForDML
							.saveOrUpdate(smsCampaignSchedule);

					// smsCampaignsDao.updateSmsCampaignStatus(smsCampaign.getSmsCampaignId());
					smsCampaignsDaoForDML.updateSmsCampaignStatus(smsCampaign
							.getSmsCampaignId());

					SMSCampReportLists smsCampReportLists = new SMSCampReportLists(
							smsCampaignReport.getSmsCrId());
					smsCampReportLists.setListsName(listsName);
					smsCampReportLists.setSegmentQuery(segmentListQuery);
					//smsCampReportListsDao.saveOrUpdate(smsCampReportLists);
					smsCampReportListsDaoForDML.saveOrUpdate(smsCampReportLists);

				} catch (Exception e) {
					logger.error("Exception >>>>>> ", e);
				}

				return;
			}

			int updatedCount = contactsDaoForDML.updateSmsTempContacts(
					userId,
					smsCampaign.getMessageType().equals(
							Constants.SMS_TYPE_TRANSACTIONAL));
			if (updatedCount > 0) {

				suppressedCount = updatedCount;
				totalAvailabeleContacts = totalAvailabeleContacts
						- updatedCount;

			}
			if (user.getSubscriptionEnable()) {

				prefernceCount = contactsDaoForDML
						.updateSmsPreferenceTempContacts(smsCampaign
								.getCategory());
				if (prefernceCount > 0) {

					totalAvailabeleContacts = totalAvailabeleContacts
							- prefernceCount;

				}
			}

			if (totalAvailabeleContacts <= 0
					|| totalAvailabeleContacts < totalContacts) {
				/*
				 * Messages messages = new
				 * Messages("SMS Campaign","Some of the Contacts are 'Suppressed'"
				 * ,"Some of the contacts are 'Suppressed'," +
				 * "SMS can only be sent to "
				 * +totalAvailabeleContacts+" of total "
				 * +totalContacts+" contacts.", smsCampaign.getUsers());
				 */
				Messages messages = new Messages("SMS Campaign",
						"Some of the contacts are 'Suppressed'",
						"Some of the contacts are 'Suppressed' or \n 'duplicate numbers',"
								+ "SMS name '" + smsCampaignName
								+ "' can only be sent to "
								+ totalAvailabeleContacts + " of total "
								+ totalContacts + " contacts.",
						Calendar.getInstance(), "Inbox", false, "Info",
						smsCampaign.getUsers());

				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
				if (totalAvailabeleContacts <= 0) {

					// TODO need to create report for it with all 0 counts....
					logger.info("num of contacts in sms_tempcontacts is "
							+ totalAvailabeleContacts);

					//
					try {

						SMSCampaignReport smsCampaignReport = null;

						smsCampaignReport = new SMSCampaignReport(
								smsCampaign.getUsers(), smsCampaignName,
								msgContent, Calendar.getInstance(), 0, 0, 0, 0,
								0, Constants.CR_STATUS_SENDING, fromSource);

						logger.warn(">>>>>>> sms campaign submission is failed found 0 active contacts, campaign id : "
								+ smsCampaign.getSmsCampaignId());

						smsCampaignReport.setStatus(Constants.CR_STATUS_SENT);
						//smsCampaignReportDao.saveOrUpdate(smsCampaignReport);
						smsCampaignReportDaoForDML.saveOrUpdate(smsCampaignReport);
						
						smsCampaignSchedule.setStatus((byte) 1);
						smsCampaignSchedule.setCrId(smsCampaignReport
								.getSmsCrId());
						// smsCampaignScheduleDao.saveOrUpdate(smsCampaignSchedule);
						smsCampaignScheduleDaoForDML
								.saveOrUpdate(smsCampaignSchedule);

						// smsCampaignsDao.updateSmsCampaignStatus(smsCampaign.getSmsCampaignId());
						smsCampaignsDaoForDML
								.updateSmsCampaignStatus(smsCampaign
										.getSmsCampaignId());

						SMSCampReportLists smsCampReportLists = new SMSCampReportLists(
								smsCampaignReport.getSmsCrId());
						smsCampReportLists.setListsName(listsName);
						smsCampReportLists.setSegmentQuery(segmentListQuery);
						//smsCampReportListsDao.saveOrUpdate(smsCampReportLists);
						smsCampReportListsDaoForDML.saveOrUpdate(smsCampReportLists);

					} catch (Exception e) {
						logger.error("Exception >>>>>> ", e);
					}

					//

					return;
				}

			}

			// anyway no need to pass the list ids here
			int totalSizeInt = contactsDao
					.getAvailableContactsFromSmsTempContacts(
							mlStr,
							!smsCampaign.isEnableEntireList() ? " AND st.mobile_opt_in=1"
									: "");

			ArrayList<String> msgContentLst = null;

			// to follow standard(actually it was for PMTA )
			msgContent = msgContent.replace("|^", "[").replace("^|", "]");

			// logger.info("msg ::"+msgContent);

			/*
			 * else{
			 * 
			 * PropertyUtil.getPropertyValueFromDB("SMSFooterContent");
			 * 
			 * }
			 */
			// prepares the totalphset
			getCustomFields(msgContent);
			Set<String> datePhSet = getDateFields(msgContent);
			Map<String, String> couponTypeMap = new HashMap<String, String>();
			if (datePhSet != null && datePhSet.size() > 0) {

				for (String symbol : datePhSet) {
					if (symbol.startsWith(Constants.DATE_PH_DATE_)) {
						if (symbol
								.equalsIgnoreCase(Constants.DATE_PH_DATE_today)) {
							Calendar cal = MyCalendar.getNewCalendar();
							msgContent = msgContent
									.replace(
											"[" + symbol + "]",
											MyCalendar
													.calendarToString(
															cal,
															MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						} else if (symbol
								.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)) {
							Calendar cal = MyCalendar.getNewCalendar();
							cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
							msgContent = msgContent
									.replace(
											"[" + symbol + "]",
											MyCalendar
													.calendarToString(
															cal,
															MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						} else if (symbol.endsWith(Constants.DATE_PH_DAYS)) {

							try {
								String[] days = symbol.split("_");
								Calendar cal = MyCalendar.getNewCalendar();
								cal.set(Calendar.DATE, cal.get(Calendar.DATE)
										+ Integer.parseInt(days[1].trim()));
								msgContent = msgContent
										.replace(
												"[" + symbol + "]",
												MyCalendar
														.calendarToString(
																cal,
																MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							} catch (Exception e) {
								logger.debug("exception in parsing date placeholder");
							}
						}// else if
					}// if
				}// for
			}

			// to replace placeholders

			// to split for replacing place holders
			if (totalPhSet != null && totalPhSet.size() > 0) {

				msgContentLst = splitSMSMessage(msgContent, 160);

			} else {

				msgContentLst = splitSMSMessage(msgContent, 170);
			}

			// if(logger.isInfoEnabled())
			// logger.info("is SMS Contains PlaceHolders ???????????? "+placeHolders);

			String msgStr = "";
			Calendar cal = Calendar.getInstance();

			if (!useMQS) {

				boolean success = true;
				boolean canProceed = true;
				boolean cccountflag = false;
				boolean isInvalidCoupFlag = false;
				List<Integer> list = new ArrayList<Integer>();
				try {
					/***** CASE1: try to check the sms Count of user ***/
					list = usersDao.getAvailableSMSCountOfUser(userId);

					if (list == null || list.get(0) == null) {
						if (logger.isInfoEnabled())
							logger.info("the available SMS count is 0 . ");

						success = false;

						msgStr = "SMS campaign name : " + smsCampaignName
								+ "\r\n";
						msgStr = msgStr
								+ " \r\n Status : Could not be sent \r\n";
						msgStr = msgStr
								+ "SMS campaign could not be sent as you have reached the sms credit limit hence campaign is stopped. \r\n";
						msgStr = msgStr
								+ "Please renew your sms credits to avoid sms campaign failure.\r\n";

						return;
					}

					if (logger.isInfoEnabled())
						logger.info("the available SMS count of user is="
								+ list.get(0));

					if (list.get(0).intValue() < (totalSizeInt * msgContentLst
							.size())) {

						if (logger.isInfoEnabled())
							logger.info(" Available limit is less than the configured contacts count");
						success = false;

						msgStr = "SMS campaign Name : " + smsCampaignName
								+ "\r\n";
						msgStr = msgStr
								+ " \r\n Status : Could not be sent \r\n";
						msgStr = msgStr
								+ "SMS campaign could not be sent as you have reached the sms credit limit hence campaign is stopped. \r\n";
						msgStr = msgStr + "Available sms count :"
								+ list.get(0).intValue() + "\r\n";
						msgStr = msgStr
								+ "No. of contacts you have configured for the sms "
								+ smsCampaignName + " are :" + totalSizeInt;

						/*
						 * Messages messages = new
						 * Messages("SMS Campaign","User sms count insufficient"
						 * ,"Your SMS count has insufficient to send SMS, " +
						 * "please renew your sms count to avoid sms campaign failure."
						 * , Calendar.getInstance(), "Inbox", false, "info",
						 * smsCampaign.getUsers());
						 * messagesDao.saveOrUpdate(messages);
						 */

						return;
					}
					// success = true;

					/**************************************************/
					/*****
					 * CASE2: make a decission whether coupon codes will be
					 * sufficient or not
					 ***/

					CouponCodesDao couponCodesDao = (CouponCodesDao) applicationContext
							.getBean("couponCodesDao");
					CouponsDao couponsDao = (CouponsDao) applicationContext
							.getBean("couponsDao");

					if (totalPhSet != null && totalPhSet.size() > 0) {

						Coupons coupon = null;
						Long couponId = null;
						for (String eachPh : totalPhSet) {
							if (!eachPh.startsWith("CC_"))
								continue;
							// only for CC

							String[] strArr = eachPh.split("_");

							if (logger.isInfoEnabled())
								logger.info("Filling  Promo-codes with Id = "
										+ strArr[1]);
							try {

								couponId = Long.parseLong(strArr[1]);

							} catch (NumberFormatException e) {

								couponId = null;

							}

							if (couponId == null) {

								// TODO need to delete it from phset
								// or???????????????????????
								continue;

							}
							coupon = couponsDao.findById(couponId);

							if (coupon == null) {
								success = false;
								// isInvalidCoupFlag = true;

								msgStr = "SMS campaign Name : "
										+ smsCampaign.getSmsCampaignName()
										+ "\r\n";
								msgStr = msgStr
										+ " \r\n Status : Could not be sent \r\n";
								msgStr = msgStr
										+ "SMS campaign '"
										+ smsCampaign.getSmsCampaignName()
										+ "' could not be sent as you have added  Promotion: "
										+ eachPh + " \r\n";
								msgStr = msgStr
										+ "This  Promotion no longer exists, you might have deleted it.  \r\n";

								if (logger.isWarnEnabled())
									logger.warn(eachPh
											+ "  Promotion is not avalable: "
											+ eachPh);
								return;

							}

							// to drop the campaign when it has a barcode link
							// in it and coupon is not as barcode
							String appshortUrl = PropertyUtil
									.getPropertyValue(Constants.APP_SHORTNER_URL);
							if (msgContent.toLowerCase().contains(
									appshortUrl.toLowerCase() + "["
											+ eachPh.toLowerCase() + "]")) {

								if (!coupon.getEnableBarcode()) {
									success = false;
									isInvalidCoupFlag = true;

									msgStr = "SMS campaign Name : "
											+ smsCampaign.getSmsCampaignName()
											+ "\r\n";
									msgStr = msgStr
											+ " \r\n Status : Could not be sent \r\n";
									msgStr = msgStr
											+ "SMS campaign '"
											+ smsCampaign.getSmsCampaignName()
											+ "' could not "
											+ "be sent as you have added a Barcode link: "
											+ (appshortUrl.toLowerCase() + "["
													+ eachPh.toLowerCase() + "]")
											+ " \r\n";
									msgStr = msgStr
											+ "This Promo-code no longer enabled as barcode.  \r\n";

									if (logger.isWarnEnabled())
										logger.warn(eachPh
												+ "  Promo-code is not a barcode: "
												+ eachPh);
									return;

								}

							}

							couponTypeMap.put(Constants.LEFT_SQUARE_BRACKET
									+ eachPh + Constants.RIGHT_SQUARE_BRACKET,
									coupon.getCouponGeneratedType());

							// only for running coupons
							// if(!coupon.getStatus().equals(Constants.COUP_STATUS_RUNNING))
							// {
							if (coupon.getStatus().equals(
									Constants.COUP_STATUS_EXPIRED)
									|| coupon.getStatus().equals(
											Constants.COUP_STATUS_PAUSED)) {

								success = false;
								isInvalidCoupFlag = true;
								msgStr = "SMS campaign Name : "
										+ smsCampaign.getSmsCampaignName()
										+ "\r\n";
								msgStr = msgStr
										+ " \r\n Status : Could not be sent \r\n";
								msgStr = msgStr
										+ "SMS campaign could not be sent as you have added  Promotion: "
										+ coupon.getCouponName() + " \r\n";
								msgStr = msgStr
										+ "This  Promotion's status is "
										+ coupon.getStatus()
										+ " and  valid period is from "
										+ MyCalendar
												.calendarToString(
														coupon.getCouponCreatedDate(),
														MyCalendar.FORMAT_DATETIME_STDATE)
										+ " to "
										+ MyCalendar
												.calendarToString(
														coupon.getCouponExpiryDate(),
														MyCalendar.FORMAT_DATETIME_STDATE)
										+ " \r\n";

								if (logger.isWarnEnabled())
									logger.warn(coupon.getCouponName()
											+ "  Promotion is not in running state, Status : "
											+ coupon.getStatus());
								return;

							}// if

							if (coupon.getAutoIncrCheck() == true) {
								continue;
							} else if (coupon.getAutoIncrCheck() == false) {
								// need to decide only when auto is false
								// List<Integer> couponCodesList =
								// couponCodesDao.getInventoryCCCountByCouponId(couponId);
								long couponCodeCount = couponCodesDao
										.getCouponCodeCountByStatus(
												couponId,
												Constants.COUP_CODE_STATUS_INVENTORY);
								if (couponCodeCount < totalSizeInt) {

									success = false;
									cccountflag = true;
									msgStr = "SMS campaign Name : "
											+ smsCampaignName + "\r\n";
									msgStr = msgStr
											+ " \r\n Status : Could not be sent \r\n";
									msgStr = msgStr
											+ "SMS campaign could not be sent as you have added  Promotion : "
											+ coupon.getCouponName() + " \r\n";
									msgStr = msgStr
											+ "Available  Promo-codes you can send :"
											+ couponCodeCount + " \r\n";

									if (logger.isWarnEnabled())
										logger.warn(" Available  Promo-codes  limit is less than the configured contacts count");
									return;
								}

							}// else

						}// for

						// to have centralized map for all org stores
						examineStorePH(orgId);
					}// if

					// CaptiwayToSMSApiGateway captiwayToSMSApiGateway =
					// (CaptiwayToSMSApiGateway)applicationContext.getBean("captiwayToSMSApiGateway");

					// canProceed =
					// captiwayToSMSApiGateway.getBalance(userSMSTool,
					// (totalAvailabeleContacts*(msgContentLst.size())),
					// smsCampaign.getMessageType());
					/*
					 * canProceed =
					 * captiwayToSMSApiGateway.getBalance(userSMSTool,
					 * (totalAvailabeleContacts*(msgContentLst.size())),
					 * smsCampaignType);
					 */
					if (!ocgateway.isPostPaid()) {

						canProceed = captiwayToSMSApiGateway.getBalance(
								ocgateway,
								(totalAvailabeleContacts * (msgContentLst
										.size())));
					}

					if (!canProceed) {

						if (logger.isInfoEnabled())
							logger.info(ocgateway.getGatewayName()
									+ " no credits with Provider...");
						success = false;
						/*
						 * msgStr = "SMS campaign Name : " + smsCampaignName +
						 * "\r\n"; msgStr = msgStr
						 * +" \r\n Status : Could not sent \r\n"; msgStr =
						 * msgStr +
						 * "SMS campaign could not be sent as you have reached the limit of sms hence Campaign is stopped \r\n"
						 * ; msgStr = msgStr +
						 * "Available sms you can send :"+list.get(0).intValue()
						 * +"\r\n"; msgStr = msgStr+
						 * "You have configured no contacts for the email "
						 * +smsCampaignName +" is :"+totalSizeInt;
						 */

						/*
						 * Messages messages = new
						 * Messages("SMS Campaign","User sms count insufficient"
						 * ,"Your SMS count has insufficient to send SMS, " +
						 * "please renew your sms count to avoid sms campaign failure."
						 * , Calendar.getInstance(), "Inbox", false, "info",
						 * smsCampaign.getUsers());
						 * messagesDao.saveOrUpdate(messages);
						 */

						return;

					}

					/**********************************************************************/

				} catch (Exception e) {

					if (logger.isErrorEnabled())
						logger.error(
								"** Exception while getting the available count"
										+ " for the user:" + userId, e);
					success = false;
					return;
				} finally {

					if (!success && canProceed) {

						if (logger.isInfoEnabled())
							logger.info(">>>>>>>>>>..." + msgStr + " " + user
									+ " ");

						if (cccountflag)
							smsCampaignSchedule.setStatus((byte) 4);
						else if (isInvalidCoupFlag)
							smsCampaignSchedule.setStatus((byte) 5);

						else
							smsCampaignSchedule.setStatus((byte) 3);

						// smsCampaignScheduleDao.saveOrUpdate(smsCampaignSchedule);
						smsCampaignScheduleDaoForDML
								.saveOrUpdate(smsCampaignSchedule);

						if (msgStr != null && msgStr.isEmpty()) {

							msgStr = "SMS campaign sending failed due to some error in server connections.";
						}
						Messages msgs = new Messages("Send SMS Campaign",
								"SMS campaign sending failed ", msgStr, cal,
								"Inbox", false, "Info", user);

						//messagesDao.saveOrUpdate(msgs);
						messagesDaoForDML.saveOrUpdate(msgs);


					}

				}// finally

			} // if(!useMQS)

			SMSCampaignReport smsCampaignReport = null;

			// int msgSequence = 0;
			// List<SMSCampaignSent> smsCampSentList = new
			// ArrayList<SMSCampaignSent>();
			cal = Calendar.getInstance();
			// sms_campaign_name, sent_date, content, sent, opens, clicks,
			// unsubscribes, bounces, status, source_type, user_id
			smsCampaignReport = new SMSCampaignReport(smsCampaign.getUsers(),
					smsCampaignName, msgContent, cal, 0, 0, 0, 0, 0,
					Constants.CR_STATUS_SENDING, fromSource);
			try {

				//smsCampaignReportDao.saveOrUpdate(smsCampaignReport);
				smsCampaignReportDaoForDML.saveOrUpdate(smsCampaignReport);
				SMSCampReportLists smsCampReportLists = new SMSCampReportLists(
						smsCampaignReport.getSmsCrId());
				smsCampReportLists.setListsName(listsName);
				smsCampReportLists.setSegmentQuery(segmentListQuery);
				//smsCampReportListsDao.saveOrUpdate(smsCampReportLists);
				smsCampReportListsDaoForDML.saveOrUpdate(smsCampReportLists);

			} catch (Exception e) {
				logger.error(" ** Exception while saving the SMSCampaignReport object");

			}

			// TODO process & form url set
			List<SMSCampaignUrls> urlList = new ArrayList<SMSCampaignUrls>();
			Object[] retObjArr = getUrls(msgContent, userId);
			urlSet = (Set<String>) retObjArr[0];
			Map<String, String> urlMap = (Map<String, String>) retObjArr[1];
			msgContent = (String) retObjArr[2];
			String shortUrl = PropertyUtil
					.getPropertyValue(Constants.APP_SHORTNER_URL);

			if (urlSet != null && urlSet.size() > 0) {
				SMSCampaignUrls smsCampaignUrl = null;
				for (String url : urlSet) {

					String[] codeTokenArr = url.split(shortUrl);

					String typeOfUrl = null;// codeTokenArr[1].startsWith(OCConstants.SHORTURL_CODE_PREFIX_U)
											// ?
											// OCConstants.SHORTURL_TYPE_SHORTCODE
											// :
					if (codeTokenArr[1]
							.startsWith(OCConstants.SHORTURL_CODE_PREFIX_U))
						typeOfUrl = OCConstants.SHORTURL_TYPE_SHORTCODE;
					else if (couponTypeMap.containsKey(codeTokenArr[1])) {

						if (couponTypeMap.get(codeTokenArr[1]).equals(
								Constants.COUP_GENT_TYPE_MULTIPLE))
							typeOfUrl = OCConstants.SHORTURL_TYPE_BARCODE_TYPE_MULTIPLE;
						else if (couponTypeMap.get(codeTokenArr[1]).equals(
								Constants.COUP_GENT_TYPE_SINGLE))
							typeOfUrl = OCConstants.SHORTURL_TYPE_BARCODE_TYPE_SINGLE;// startsWith(OCConstants.SHORTURL_CODE_PREFIX_CC)
																						// ?);
					}
					smsCampaignUrl = new SMSCampaignUrls(
							smsCampaignReport.getSmsCrId(), null, url,
							codeTokenArr[1], typeOfUrl);
					if (urlMap != null && urlMap.containsKey(url))
						smsCampaignUrl.setOriginalUrl(urlMap.get(url));
					urlList.add(smsCampaignUrl);

				}
				SMSCampaignUrlDao SMSCampaignUrlDao = (SMSCampaignUrlDao) ServiceLocator
						.getInstance().getDAOByName(
								OCConstants.SMSCAMPAIGNURL_DAO);
				SMSCampaignUrlDaoForDML SMSCampaignUrlDaoForDML = (SMSCampaignUrlDaoForDML) ServiceLocator
						.getInstance().getDAOForDMLByName(
								OCConstants.SMSCAMPAIGNURL_DAO_FOR_DML);
				if (urlList.size() > 0) {

					//SMSCampaignUrlDao.saveByCollection(urlList);
					SMSCampaignUrlDaoForDML.saveByCollection(urlList);
					smsCampUrlList.addAll(urlList);
					urlList.clear();
				}

			}// if

			if (totalAvailabeleContacts <= 0) {

				// TODO need to create report for it with all 0 counts....
				if (logger.isInfoEnabled())
					logger.info("num of contacts in sms_tempcontacts is "
							+ totalAvailabeleContacts);

				msgStr = "SMS Name :"
						+ smsCampaignName
						+ "\n"
						+ "Found 0 mobile contacts in the mailing lists which "
						+ "are configured for the SMS campaign.\n"
						+ "Reason may be, configured contacts exists in the suppressed contacts list. ";

				Messages messages = new Messages("SMS Details ",
						"No mobile contacts", msgStr, cal, "Inbox", false,
						"Info", user);
				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);

				smsCampaignReport.setStatus(Constants.CR_STATUS_SENT);
				//smsCampaignReportDao.saveOrUpdate(smsCampaignReport);
				smsCampaignReportDaoForDML.saveOrUpdate(smsCampaignReport);
				smsCampaignSchedule.setStatus((byte) 1);
				smsCampaignSchedule.setCrId(smsCampaignReport.getSmsCrId());
				// smsCampaignScheduleDao.saveOrUpdate(smsCampaignSchedule);
				smsCampaignScheduleDaoForDML.saveOrUpdate(smsCampaignSchedule);
				// smsCampaignsDao.updateSmsCampaignStatus(smsCampaign.getSmsCampaignId());
				smsCampaignsDaoForDML.updateSmsCampaignStatus(smsCampaign
						.getSmsCampaignId());

				return;
			}

			int creditsToBeDeduct = 0;
			SMSRecipientProvider smsRecipientProvider = new SMSRecipientProvider(
					applicationContext, smsCampaignReport, totalSizeInt,
					smsCampaign.getSmsCampaignId(), totalPhSet);
			smsRecipientProvider.setTempCount(creditsToBeDeduct);
			// Create threads to submit campaign
			List<Thread> threadsList = new ArrayList<Thread>();
			try {
				logger.info("<<< STARTING THE THREADS >>>>");
				if (ocgateway.getMode().equals(Constants.SMS_SENDING_MODE_SMPP)) {// if(userSMSTool.equals(Constants.USER_SMSTOOL_MVAYOO))
																					// {//to
																					// have
																					// only
																					// one
																					// thread
																					// when
																					// runs
																					// with
																					// mvaayoo

					// isNeedDeliveryReport = true;
					Thread th = new SMSMultiThreadedSubmission(
							smsRecipientProvider, user, smsCampaign,
							msgContent, smsCampaignReport, totalPhSet, urlSet,
							smsCampUrlList, Constants.SOURCE_SMS_CAMPAIGN,
							applicationContext, captiwayToSMSApiGateway,
							ocgateway);
					th.setName("thread_campaingEmail:" + (1));
					threadsList.add(th);
					th.start();

				} else if (ocgateway.getMode().equals(
						Constants.SMS_SENDING_MODE_HTTP)) {

					/**
					 * Added for MultiThread SMS submission
					 */

					logger.debug("MultiThread SMS submission is Enabled .......:"
							+ ocgateway.isEnableMultiThreadSub());
					CaptiwayToSMSApiGateway captiwayToSMSApiGatewayObj = null;
					logger.debug("Number of Threads created ..........."
							+ NUMBER_OF_SMS_THREADS);

					if (ocgateway.isEnableMultiThreadSub()) {
						for (int i = 0; i < NUMBER_OF_SMS_THREADS; i++) {
							captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway(
									applicationContext);
							Thread th = new SMSMultiThreadedSubmission(
									smsRecipientProvider, user, smsCampaign,
									msgContent, smsCampaignReport, totalPhSet,
									urlSet, smsCampUrlList,
									Constants.SOURCE_SMS_CAMPAIGN,
									applicationContext,
									captiwayToSMSApiGateway, ocgateway);
							th.setName("thread_campaignSMS:" + (i));
							logger.debug(" Current Thread Name "
									+ Thread.currentThread().getName());
							threadsList.add(th);
							th.start();
						}// for
					} else {
						captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway(
								applicationContext);
						Thread th = new SMSMultiThreadedSubmission(
								smsRecipientProvider, user, smsCampaign,
								msgContent, smsCampaignReport, totalPhSet,
								urlSet, smsCampUrlList,
								Constants.SOURCE_SMS_CAMPAIGN,
								applicationContext, captiwayToSMSApiGateway,
								ocgateway);
						th.setName("thread_campaignSMS:" + (1));
						threadsList.add(th);
						th.start();
					}
					// }//for

				}// else if
			} catch (Exception e) {
				logger.error(
						"Exception : ERROR OCCURED WHILE CREATING THREADS ...",
						e);
			}

			// wait for all threads to finish
			Iterator<Thread> iter;
			iter = threadsList.iterator();

			while (iter.hasNext()) {

				try {

					iter.next().join();
				} catch (InterruptedException oops) {

					logger.error("Interrupted: ", oops);
					// return 1;
				}
			}

			creditsToBeDeduct = smsRecipientProvider.getTempCount();

			if (ocgateway.getMode().equals(Constants.SMS_SENDING_MODE_SMPP))
				captiwayToSMSApiGateway.unbindSession(ocgateway);
			/*
			 * while (iter.hasNext()) {
			 * 
			 * try {
			 * 
			 * SMSMultiThreadedSubmission SMSSubmissionThread =
			 * (SMSMultiThreadedSubmission)iter.next(); creditsToBeDeduct +=
			 * SMSSubmissionThread.getTempCount();
			 * 
			 * 
			 * } catch (Exception oops) {
			 * 
			 * logger.error("Interrupted: ", oops); //return 1; } }
			 */

			logger.info("<<< ALL THREAD EXECUTED SUCCESSFULLY >>>>"
					+ creditsToBeDeduct);

			smsCampaignReport.setStatus(Constants.CR_STATUS_SENT);
			smsCampaignReport.setSent(smsCampaignSentDao
					.getSentCount(smsCampaignReport.getSmsCrId()));
			smsCampaignReport.setConfigured(configuredCount);
			smsCampaignReport.setSuppressedCount(suppressedCount);
			smsCampaignReport.setPreferenceCount(prefernceCount);

			//smsCampaignReportDao.saveOrUpdate(smsCampaignReport);
			smsCampaignReportDaoForDML.saveOrUpdate(smsCampaignReport);
			//smsCampaignReportDao.updateBounceReport(smsCampaignReport.getSmsCrId());
			smsCampaignReportDaoForDML.updateBounceReport(smsCampaignReport.getSmsCrId());
			if (ocgateway.isPullReports()) {

				SMSDeliveryReportDao smsDeliveryReportDao = (SMSDeliveryReportDao) applicationContext
						.getBean("smsDeliveryReportDao");
				SMSDeliveryReportDaoForDML smsDeliveryReportDaoForDML = (SMSDeliveryReportDaoForDML) applicationContext
						.getBean("smsDeliveryReportDaoForDML");

				String requestId = captiwayToSMSApiGateway
						.getGatewaySpecificRequestIdToPUllReports(ocgateway
								.getGatewayName());

				SMSDeliveryReport smsDeliveryReport = new SMSDeliveryReport(
						requestId, smsCampaignReport.getSmsCrId(),
						Constants.CR_DLVR_STATUS_ACTIVE, Calendar.getInstance());

				smsDeliveryReport.setUserSMSTool(ocgateway.getId());
				// smsDeliveryReport.setIsTransactional(smsCampaign.getMessageType().toString());
				//smsDeliveryReportDao.saveOrUpdate(smsDeliveryReport);
				smsDeliveryReportDaoForDML.saveOrUpdate(smsDeliveryReport);

			}

			if (logger.isDebugEnabled())
				logger.debug("bounced count ::"
						+ smsCampaignReport.getBounces());

			smsCampaignSchedule.setStatus((byte) 1);
			/*
			 * smsCampaign.setStatus("Sent");
			 * smsCampaignsDao.saveOrUpdate(smsCampaign);
			 */
			smsCampaignSchedule.setCrId(smsCampaignReport.getSmsCrId());
			// smsCampaignScheduleDao.saveOrUpdate(smsCampaignSchedule);
			smsCampaignScheduleDaoForDML.saveOrUpdate(smsCampaignSchedule);
			// smsCampaignsDao.updateSmsCampaignStatus(smsCampaign.getSmsCampaignId());
			smsCampaignsDaoForDML.updateSmsCampaignStatus(smsCampaign
					.getSmsCampaignId());

			// update last smsDate to the contacts
			String smsDate = MyCalendar.calendarToString(
					smsCampaignReport.getSentDate(),
					MyCalendar.FORMAT_DATETIME_STYEAR);
			contactsDaoForDML.updatelastSMSDate(smsCampaignReport.getSmsCrId(),
					smsDate);

			if (!useMQS) {

				msgStr = "SMS Name :" + smsCampaignName + "\n"
						+ "Sent successfully to " + smsCampaignReport.getSent()
						+ " unique contacts.";

				Messages messages = new Messages("SMS Details ",
						"SMS sent successfully", msgStr, cal, "Inbox", false,
						"Info", user);
				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);

				//usersDao.updateUsedSMSCount(smsCampaign.getUsers().getUserId(),creditsToBeDeduct);
				usersDaoForDML.updateUsedSMSCount(smsCampaign.getUsers().getUserId(), creditsToBeDeduct);
				Utility.updateCouponCodeCounts(applicationContext, totalPhSet);

			}

		} catch (Exception e) {
			logger.error("** Error occured while submitting the SMS campaigns",
					e);
		}
		logger.debug(">>>>>>> Completed SMSCampaignSubmitter :: sendSMSCampaign <<<<<<< ");
	} // addSMSCampaign

	private void sendEventTriggerSMSCampaign(EventTrigger eventTrigger,
			String fromSource) {
		try {
			/*
			 * 
			 * 1. Fetch the sms Campaign object. 2. insert into
			 * sms_tempcontacts 3. Filter contacts based on detailed
			 * fromSource.. 4. Get the mesgcomtent of the sms camp obj 5. Based
			 * on mesg type split the mesg.. 6. Get the count of contacts and
			 * compare it with avaibale user sms count 7. If user cnt is <
			 * exitelse cnt..to step 6 8. Form an sms campaign report object
			 * and save it as SENDING 9. Fetch the contacts from the temp table
			 * if got zero contacts exit.. 10. For each contactform
			 * smsCampaignSent obj and save it to db with success 11. Send sms
			 * to resp api based on mesOveroption 12. After sending to all the
			 * contacts update user sms count and 13. repeat above steps for all
			 * the contacts on sms_tepmcontacts table.... 14. After everything
			 * Delete from the temp table
			 */

			if (logger.isInfoEnabled())
				logger.info("----------processing eventTrigger to send sms-------");

			// Fetch the SMS Campaign object
			SMSCampaigns smsCampaign = smsCampaignsDao
					.findByCampaignId(eventTrigger.getSmsId());

			if (smsCampaign == null) {

				if (logger.isInfoEnabled())
					logger.info("configered sms campaign no longer exist");
				return;
			}

			Users user = smsCampaign.getUsers();

			// this is to know prior, to direct to appropriate SMS gateway.

			String userSMSTool = user.getUserSMSTool();

			Long userId = user.getUserId();
			Long orgId = user.getUserOrganization().getUserOrgId();
			String smsCampaignName = smsCampaign.getSmsCampaignName();
			String msgContent = smsCampaign.getMessageContent();

			if (msgContent == null || msgContent.trim().length() == 0) {

				if (logger.isErrorEnabled())
					logger.error("NO message content found for SMS : "
							+ smsCampaignName);
				return;

			}

			if (!user.isEnableSMS()) {

				Messages messages = new Messages(
						"SMS Campaign",
						"SMS camapign name -" + smsCampaignName
								+ " can not be reached",
						"SMS package is not enabled for your user account. Please contact Admin to enable SMS feature."
								+ "SMS campaign could not be sent.",
						Calendar.getInstance(), "Inbox", false, "Info",
						smsCampaign.getUsers());

				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
				return;

			}

			String smsCampaignType = smsCampaign.getMessageType();
			boolean isSendToAll = smsCampaign.isEnableEntireList();

			String accountType = SMSStatusCodes.countryCampValueMap.get(
					user.getCountryType()).get(smsCampaignType);

			if (SMSStatusCodes.optInMap.get(user.getCountryType())
					&& !isSendToAll) {

				accountType = Constants.SMS_SENDING_TYPE_OPTIN;
			}

			UserSMSGateway userSMSGateway = userSMSGatewayDao.findByUserId(
					userId, accountType);

			if (userSMSGateway == null) {

				Messages messages = new Messages(
						"SMS Campaign",
						"SMS can not be reached",
						"No SMS setup found for this SMS type in your user account. Please contact Admin to enable SMS feature."
								+ "SMS campaign could not be sent.",
						Calendar.getInstance(), "Inbox", false, "Info",
						smsCampaign.getUsers());

				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
				return;

			}

			OCSMSGateway ocgateway = OCSMSGatewayDao.findById(userSMSGateway
					.getGatewayId());

			if (ocgateway == null) {

				Messages messages = new Messages(
						"SMS Campaign",
						"SMS can not be reached",
						"No SMS setup found for this SMS type in your user account. Please contact Admin to enable SMS feature."
								+ "SMS campaign could not be sent.",
						Calendar.getInstance(), "Inbox", false, "Info",
						smsCampaign.getUsers());

				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);

				return;

			}

			// if(userSMSTool.equals(Constants.USER_SMSTOOL_CLICKATELL)) {
			if (SMSStatusCodes.optOutFooterMap.get(user.getCountryType())) {// if(user.isConsiderSMSSettings()
																			// ){

				SMSSettingsDao smsSettingsDao = (SMSSettingsDao) applicationContext
						.getBean("smsSettingsDao");

				SMSSettings smsSettings = smsSettingsDao.findByUser(userId,
						OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);

				// dont allow user from sending campaign if he has no complaincy
				// settings
				if (smsSettings == null) {// this will never occur

					Messages messages = new Messages(
							"SMS Campaign",
							"SMS can not be reached",
							"SMS settings not found for your user account. Please contact Admin to enable SMS feature."
									+ "SMS Campaign associated to the Trigger '"
									+ eventTrigger.getTriggerName()
									+ "' can not be sent.",
							Calendar.getInstance(), "Inbox", false, "Info",
							smsCampaign.getUsers());

					//messagesDao.saveOrUpdate(messages);
					messagesDaoForDML.saveOrUpdate(messages);

					return;

				}
				if (smsSettings.getMessageHeader() != null) {

					// msgContent =
					// smsSettings.getMessageHeader()+" "+msgContent;
					msgContent = smsSettings.getMessageHeader() + "\n\n"
							+ msgContent;

				}

				if (smsCampaign.isEnableEntireList()) {
					SMSSettings optoutSMSSettings = smsSettingsDao
							.findByUser(userId,
									OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT);
					if (optoutSMSSettings == null)
						msgContent = msgContent
								+ (optoutSMSSettings != null
										&& optoutSMSSettings.getKeyword() != null ?
								// (" "+"Reply "+optoutSMSSettings.getKeyword()+" 2 Optout.")
								// :
								// PropertyUtil.getPropertyValueFromDB("SMSFooterContent"));
								("\n\n" + "Reply "
										+ optoutSMSSettings.getKeyword() + " 2 Optout.")
										: "\n\n"
												+ PropertyUtil
														.getPropertyValueFromDB("SMSFooterContent"));

				}

			}

			CaptiwayToSMSApiGateway captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway(
					applicationContext);
			/********
			 * get the Trigger associated mailing list names(for
			 * smsCampReportLists)
			 ****************/
			String listsName = "";
			String mlStr = "";
			Set<MailingList> mlList = eventTrigger.getMailingLists();

			if (mlList == null || mlList.isEmpty()) {

				Messages msgs = new Messages(
						"Send Event trigger SMS Campaign",
						"Event trigger SMS campaign sending failed ",
						"SMS associated to trigger '"
								+ eventTrigger.getTriggerName()
								+ "' couldn't be sent, no list has configured to this trigger(might have deleted)",
						Calendar.getInstance(), "Inbox", false, "Info", user);

				//messagesDao.saveOrUpdate(msgs);
				messagesDaoForDML.saveOrUpdate(msgs);
				return;

			}

			if (checkForExpiredKeyWords(msgContent, user,
					eventTrigger.getTriggerName(), orgId, true)) {

				return;

			}
			/*
			 * if(mlList == null || ) {
			 * 
			 * if(logger.isInfoEnabled())
			 * logger.info(" eventTrigger is not configured with any mailing list"
			 * ); return;
			 * 
			 * 
			 * }//if
			 */

			for (MailingList mailingList : mlList) {

				if (mlStr.length() > 0)
					mlStr += ",";

				mlStr += mailingList.getListId().longValue();

				if (listsName.length() > 0) {

					listsName += ",";
				}

				listsName += mailingList.getListName();
			}// for

			int suppressedCount = 0; // all these counts are needed w.r.t sms
										// campaign report startegies
			int configuredCount = 0;
			int totalContacts = 0;
			int totalAvailabeleContacts = 0;
			String tolCountQry = "";
			int preferenceCount = 0;

			// before going to process the SMS just clean the sms_tempcontacts
			contactsDaoForDML.deleteSmsTempContacts();

			// prepare a trigger query W.R.T SMS.

			String eventTriggerSMSQuery = TriggerQueryGenerator
					.prepareTriggerQueryForSMS(userId, eventTrigger);
			if (eventTriggerSMSQuery == null) {

				if (logger.isInfoEnabled())
					logger.info(" did not get the proper trigger query.");
				return;

			}

			eventTriggerSMSQuery = eventTriggerSMSQuery
					.replace(
							"<MOBILEOPTIN>",
							!smsCampaign.isEnableEntireList() ? " AND c.mobile_opt_in=1"
									: "");
			String qryStr = "INSERT IGNORE INTO sms_tempcontacts ("
					+ Constants.QRY_COLUMNS_TEMP_CONTACTS
					+ ", cf_value, domain, event_source_id)  " + "("
					+ eventTriggerSMSQuery + ")";

			logger.debug("eventtrigeer qry " + eventTriggerSMSQuery);

			totalContacts = contactsDao
					.getSegmentedContactsCount(eventTriggerSMSQuery);

			if (totalContacts <= 0) {

				if (logger.isInfoEnabled())
					logger.info(" no contacts to insert.");
				return;

			}// if
			totalAvailabeleContacts = configuredCount += contactsDaoForDML
					.createSmsTempContacts(qryStr, null, user, false,
							user.getCountryCarrier());

			int updatedCount = contactsDaoForDML.updateSmsTempContacts(
					userId,
					smsCampaign.getMessageType().equals(
							Constants.SMS_TYPE_TRANSACTIONAL));
			if (updatedCount > 0) {

				suppressedCount = updatedCount;
				totalAvailabeleContacts = totalAvailabeleContacts
						- updatedCount;

			}

			// Added for subscriber preference count
			if (eventTrigger.getUsers().getSubscriptionEnable()) {

				preferenceCount = contactsDaoForDML
						.updateSmsPreferenceTempContacts(eventTrigger
								.getCampCategory() != null ? eventTrigger
								.getCampCategory() : smsCampaign.getCategory());

				totalAvailabeleContacts = totalAvailabeleContacts
						- preferenceCount;

			}

			if (totalAvailabeleContacts <= 0) {

				if (logger.isInfoEnabled())
					logger.info(" no contacts to insert after deleting suppressed contacts.");
				return;

			}// if

			if (totalAvailabeleContacts < totalContacts) {
				/*
				 * Messages messages = new
				 * Messages("SMS Campaign","Some of the Contacts are 'Suppressed'"
				 * ,"Some of the contacts are 'Suppressed'," +
				 * "SMS can only be sent to "
				 * +totalAvailabeleContacts+" of total "
				 * +totalContacts+" contacts.", smsCampaign.getUsers());
				 */
				Messages messages = new Messages("SMS Campaign",
						"Some of the contacts are 'Suppressed'",
						"Some of the contacts are 'Suppressed' or \n 'duplicate numbers',"
								+ "SMS associated to trigger '"
								+ eventTrigger.getTriggerName()
								+ "' can only be sent to "
								+ totalAvailabeleContacts + " of total "
								+ totalContacts + " contacts.",
						Calendar.getInstance(), "Inbox", false, "Info",
						smsCampaign.getUsers());

				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
			}

			// anyway no need to pass the list ids here
			int totalSizeInt = contactsDao
					.getAvailableContactsFromSmsTempContacts(
							mlStr,
							!smsCampaign.isEnableEntireList() ? " AND st.mobile_opt_in=1"
									: "");

			ArrayList<String> msgContentLst = null;

			// to follow standard(actually it was for PMTA )
			msgContent = msgContent.replace("|^", "[").replace("^|", "]");

			// prepares the totalphset
			getCustomFields(msgContent);
			Set<String> datePhSet = getDateFields(msgContent);
			Map<String, String> couponTypeMap = new HashMap<String, String>();
			if (datePhSet != null && datePhSet.size() > 0) {

				for (String symbol : datePhSet) {
					if (symbol.startsWith(Constants.DATE_PH_DATE_)) {
						if (symbol
								.equalsIgnoreCase(Constants.DATE_PH_DATE_today)) {
							Calendar cal = MyCalendar.getNewCalendar();
							msgContent = msgContent
									.replace(
											"[" + symbol + "]",
											MyCalendar
													.calendarToString(
															cal,
															MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						} else if (symbol
								.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)) {
							Calendar cal = MyCalendar.getNewCalendar();
							cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
							msgContent = msgContent
									.replace(
											"[" + symbol + "]",
											MyCalendar
													.calendarToString(
															cal,
															MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						} else if (symbol.endsWith(Constants.DATE_PH_DAYS)) {

							try {
								String[] days = symbol.split("_");
								Calendar cal = MyCalendar.getNewCalendar();
								cal.set(Calendar.DATE, cal.get(Calendar.DATE)
										+ Integer.parseInt(days[1].trim()));
								msgContent = msgContent
										.replace(
												"[" + symbol + "]",
												MyCalendar
														.calendarToString(
																cal,
																MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							} catch (Exception e) {
								logger.debug("exception in parsing date placeholder");
							}
						}// else if
					}// if
				}// for
			}

			// to replace placeholders
			// CouponCodesGeneration ccGeneration = new
			// CouponCodesGeneration(applicationContext);
			ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders(
					applicationContext, orgId, null, smsCampaignName);

			// for clickatell it is standard type:English
			// int msgType = smsCampaign.getMessageType();

			// if(msgType == 1){//if normal text

			// to split for replacing place holders
			if (totalPhSet != null && totalPhSet.size() > 0) {

				msgContentLst = splitSMSMessage(msgContent, 160);

			} else {

				msgContentLst = splitSMSMessage(msgContent, 170);
			}

			String useMqsStr = PropertyUtil.getPropertyValueFromDB("useMQS");
			boolean useMQS = (useMqsStr == null ? true : useMqsStr
					.equalsIgnoreCase("true"));

			String msgStr = "";
			Calendar cal = Calendar.getInstance();

			if (!useMQS) {

				boolean success = true;
				boolean canProceed = true;
				List<Integer> list = new ArrayList<Integer>();
				try {
					/***** CASE1: try to check the sms Count of user ***/
					list = usersDao.getAvailableSMSCountOfUser(userId);

					if (list == null || list.get(0) == null) {
						if (logger.isInfoEnabled())
							logger.info("the available SMS count is 0 . ");

						success = false;

						msgStr = "SMS campaign Name : " + smsCampaignName
								+ "\r\n";
						msgStr = msgStr
								+ " \r\n Status : Could not be sent \r\n";
						msgStr = msgStr
								+ "SMS campaign associated to trigger '"
								+ eventTrigger.getTriggerName()
								+ "' could not be sent as you have reached the sms credit limit hence campaign is stopped. \r\n";
						msgStr = msgStr
								+ "Please renew your sms credits to avoid sms campaign failure.\r\n";

						return;
					}

					if (logger.isInfoEnabled())
						logger.info("the available SMS count of user is="
								+ list.get(0));

					if (list.get(0).intValue() < (totalSizeInt * msgContentLst
							.size())) {

						if (logger.isInfoEnabled())
							logger.info(" Available limit is less than the configured contacts count");
						success = false;

						msgStr = "SMS campaign Name : " + smsCampaignName
								+ "\r\n";
						msgStr = msgStr
								+ " \r\n Status : Could not be sent \r\n";
						msgStr = msgStr
								+ "SMS campaign associated to trigger '"
								+ eventTrigger.getTriggerName()
								+ "' could not be sent as you have reached the sms credit limit hence campaign is stopped. \r\n";
						msgStr = msgStr + "Available sms you can send :"
								+ list.get(0).intValue() + "\r\n";
						msgStr = msgStr
								+ "No. of contacts you have configured for the sms "
								+ smsCampaignName + " are :" + totalSizeInt;

						/*
						 * Messages messages = new
						 * Messages("SMS Campaign","User sms count insufficient"
						 * ,"Your SMS count has insufficient to send SMS, " +
						 * "please renew your sms count to avoid sms campaign failure."
						 * , Calendar.getInstance(), "Inbox", false, "info",
						 * smsCampaign.getUsers());
						 * messagesDao.saveOrUpdate(messages);
						 */

						return;
					}
					// success = true;

					/**************************************************/
					/*****
					 * CASE2: make a decission whether coupon codes will be
					 * sufficient or not
					 ***/

					CouponCodesDao couponCodesDao = (CouponCodesDao) applicationContext
							.getBean("couponCodesDao");
					CouponsDao couponsDao = (CouponsDao) applicationContext
							.getBean("couponsDao");

					if (totalPhSet != null && totalPhSet.size() > 0) {

						Coupons coupon = null;
						Long couponId = null;
						for (String eachPh : totalPhSet) {
							if (!eachPh.startsWith("CC_"))
								continue;
							// only for CC

							String[] strArr = eachPh.split("_");

							if (logger.isInfoEnabled())
								logger.info("Filling  Promo-codes with Id = "
										+ strArr[1]);
							try {

								couponId = Long.parseLong(strArr[1]);

							} catch (NumberFormatException e) {

								couponId = null;

							}

							if (couponId == null) {

								// TODO need to delete it from phset
								// or???????????????????????
								continue;

							}
							coupon = couponsDao.findById(couponId);
							if (coupon == null) {
								success = false;
								msgStr = "SMS campaign Name : "
										+ smsCampaign.getSmsCampaignName()
										+ "\r\n";
								msgStr = msgStr
										+ " \r\n Status : Could not be sent \r\n";
								msgStr = msgStr
										+ "SMS campaign associated to trigger '"
										+ eventTrigger.getTriggerName()
										+ "' could not be sent as you have added  Promotion: "
										+ eachPh + " \r\n";
								msgStr = msgStr
										+ "This  Promotion no longer exists, you might have deleted it. : \r\n";

								if (logger.isWarnEnabled())
									logger.warn(eachPh
											+ "  Promo-code is not avalable: "
											+ eachPh);
								return;

							}

							// only for running coupons

							// if(!coupon.getStatus().equals(Constants.COUP_STATUS_RUNNING))
							// {
							if (coupon.getStatus().equals(
									Constants.COUP_STATUS_EXPIRED)
									|| coupon.getStatus().equals(
											Constants.COUP_STATUS_PAUSED)) {

								success = false;

								msgStr = "SMS campaign Name : "
										+ smsCampaign.getSmsCampaignName()
										+ "\r\n";
								msgStr = msgStr
										+ " \r\n Status : Could not be sent \r\n";
								msgStr = msgStr
										+ "SMS campaign associated to trigger '"
										+ eventTrigger.getTriggerName()
										+ "' could not be sent as you have added  Promotion: "
										+ coupon.getCouponName() + " \r\n";
								msgStr = msgStr
										+ "This  Promotion's status is "
										+ coupon.getStatus()
										+ " and  valid period is from "
										+ MyCalendar
												.calendarToString(
														coupon.getCouponCreatedDate(),
														MyCalendar.FORMAT_DATETIME_STDATE)
										+ " to "
										+ MyCalendar
												.calendarToString(
														coupon.getCouponExpiryDate(),
														MyCalendar.FORMAT_DATETIME_STDATE)
										+ " \r\n";

								if (logger.isWarnEnabled())
									logger.warn(coupon.getCouponName()
											+ "  Promotion is not in running state, Status : "
											+ coupon.getStatus());
								return;

							}// if

							if (coupon.getAutoIncrCheck() == true) {
								continue;
							} else if (coupon.getAutoIncrCheck() == false) {
								// need to decide only when auto is false
								// List<Integer> couponCodesList =
								// couponCodesDao.getInventoryCCCountByCouponId(couponId);
								long couponCodeCount = couponCodesDao
										.getCouponCodeCountByStatus(
												couponId,
												Constants.COUP_CODE_STATUS_INVENTORY);
								if (couponCodeCount < totalSizeInt) {

									success = false;
									msgStr = "SMS campaign Name : "
											+ smsCampaignName + "\r\n";
									msgStr = msgStr
											+ " \r\n Status : Could not be sent \r\n";
									msgStr = msgStr
											+ "SMS campaign associated to trigger '"
											+ eventTrigger.getTriggerName()
											+ "' could not be sent as you have added  Promotion : "
											+ coupon.getCouponName() + " \r\n";
									msgStr = msgStr
											+ "Available  Promo-codes you can send :"
											+ couponCodeCount + " \r\n";

									if (logger.isWarnEnabled())
										logger.warn(" Available  Promo-codes  limit is less than the configured contacts count");
									return;
								}

							}// else

						}// for

					}// if

					// CaptiwayToSMSApiGateway captiwayToSMSApiGateway =
					// (CaptiwayToSMSApiGateway)applicationContext.getBean("captiwayToSMSApiGateway");

					// canProceed =
					// captiwayToSMSApiGateway.getBalance(userSMSTool,
					// (totalAvailabeleContacts*(msgContentLst.size())),
					// smsCampaign.getMessageType());
					if (!ocgateway.isPostPaid()) {

						canProceed = captiwayToSMSApiGateway.getBalance(
								ocgateway,
								(totalAvailabeleContacts * (msgContentLst
										.size())));
					}

					if (!canProceed) {

						if (logger.isInfoEnabled())
							logger.info(" no credits with CLICKATELL...");
						success = false;
						/*
						 * msgStr = "SMS campaign Name : " + smsCampaignName +
						 * "\r\n"; msgStr = msgStr
						 * +" \r\n Status : Could not sent \r\n"; msgStr =
						 * msgStr +
						 * "SMS campaign could not be sent as you have reached the limit of sms hence Campaign is stopped \r\n"
						 * ; msgStr = msgStr +
						 * "Available sms you can send :"+list.get(0).intValue()
						 * +"\r\n"; msgStr = msgStr+
						 * "You have configured no contacts for the email "
						 * +smsCampaignName +" is :"+totalSizeInt;
						 */

						/*
						 * Messages messages = new
						 * Messages("SMS Campaign","User sms count insufficient"
						 * ,"Your SMS count has insufficient to send SMS, " +
						 * "please renew your sms count to avoid sms campaign failure."
						 * , Calendar.getInstance(), "Inbox", false, "info",
						 * smsCampaign.getUsers());
						 * messagesDao.saveOrUpdate(messages);
						 */

						return;

					}
					/**********************************************************************/

				} catch (Exception e) {

					if (logger.isErrorEnabled())
						logger.error(
								"** Exception while getting the available count"
										+ " for the user:" + userId, e);
					success = false;
					return;
				} finally {

					if (!success && canProceed) {

						if (logger.isInfoEnabled())
							logger.info(">>>>>>>>>>..." + msgStr + " " + user
									+ " ");

						if (msgStr != null && msgStr.isEmpty()) {

							msgStr = "SMS Campaign associated to trigger '"
									+ eventTrigger.getTriggerName()
									+ "' send failed due to Some error in server connections.";
						}

						String module = "Event Trigger";
						String subject = "Event Trigger send failed";
						// String qry =
						// "FROM Messages WHERE users.userId="+mlUser.getUserId().longValue()+" AND subject='"+subject+"' AND module='"+module+"' AND message='"+StringEscapeUtils.escapeSql(msgStr)+"' AND DAY(createdDate) = DAY(NOW())";
						// logger.info("qry::"+qry);
						boolean isMsgExists = messagesDao
								.findSameMsgWithInSameDay(user, msgStr,
										subject, module);

						// logger.info("ismsgExists::"+isMsgExists);
						if (!isMsgExists) {

							eventTrigger
									.setStatus("Failed due to less user count");

							Messages msgs = new Messages("Send SMS Campaign",
									"SMS campaign sending failed ", msgStr,
									cal, "Inbox", false, "Info", user);

							//messagesDao.saveOrUpdate(msgs);
							messagesDaoForDML.saveOrUpdate(msgs);
						}
					}

				}// finally

			} // if(!useMQS)

			/*
			 * byte msgOverSizeOption =smsCampaign.getMessageSizeOption();
			 * String senderId = smsCampaign.getSenderId();
			 * 
			 * int stIndex=0; int size=100; String response = ""; int tempCount
			 * = 0; List<Contacts> retList; String sendingMsg; boolean limit =
			 * false; SMSCampaignSent smsCampaignSent = null; String sentId =
			 * "";
			 */

			SMSCampaignReport smsCampaignReport = null;

			// int msgSequence = 0;
			// List<SMSCampaignSent> smsCampSentList = new
			// ArrayList<SMSCampaignSent>();
			cal = Calendar.getInstance();
			// sms_campaign_name, sent_date, content, sent, opens, clicks,
			// unsubscribes, bounces, status, source_type, user_id
			smsCampaignReport = new SMSCampaignReport(smsCampaign.getUsers(),
					smsCampaignName, msgContent, cal, 0, 0, 0, 0, 0,
					Constants.CR_STATUS_SENDING, fromSource);
			try {

				//smsCampaignReportDao.saveOrUpdate(smsCampaignReport);
				smsCampaignReportDaoForDML.saveOrUpdate(smsCampaignReport);
				SMSCampReportLists smsCampReportLists = new SMSCampReportLists(
						smsCampaignReport.getSmsCrId());
				smsCampReportLists.setListsName(listsName);
				smsCampReportLists.setSegmentQuery(eventTriggerSMSQuery);
				//smsCampReportListsDao.saveOrUpdate(smsCampReportLists);
				smsCampReportListsDaoForDML.saveOrUpdate(smsCampReportLists);

			} catch (Exception e) {
				logger.error(" ** Exception while saving the SMSCampaignReport object");

			}

			// TODO process & form url set
			List<SMSCampaignUrls> urlList = new ArrayList<SMSCampaignUrls>();
			Object[] retObjArr = getUrls(msgContent, userId);
			urlSet = (Set<String>) retObjArr[0];
			Map<String, String> urlMap = (Map<String, String>) retObjArr[1];
			msgContent = (String) retObjArr[2];
			String shortUrl = PropertyUtil
					.getPropertyValue(Constants.APP_SHORTNER_URL);

			if (urlSet != null && urlSet.size() > 0) {
				SMSCampaignUrls smsCampaignUrl = null;
				for (String url : urlSet) {

					String[] codeTokenArr = url.split(shortUrl);

					String typeOfUrl = null;// codeTokenArr[1].startsWith(OCConstants.SHORTURL_CODE_PREFIX_U)
											// ?
											// OCConstants.SHORTURL_TYPE_SHORTCODE
											// :
					if (codeTokenArr[1]
							.startsWith(OCConstants.SHORTURL_CODE_PREFIX_U))
						typeOfUrl = OCConstants.SHORTURL_TYPE_SHORTCODE;
					else if (couponTypeMap.containsKey(codeTokenArr[1])) {

						if (couponTypeMap.get(codeTokenArr[1]).equals(
								Constants.COUP_GENT_TYPE_MULTIPLE))
							typeOfUrl = OCConstants.SHORTURL_TYPE_BARCODE_TYPE_MULTIPLE;
						else if (couponTypeMap.get(codeTokenArr[1]).equals(
								Constants.COUP_GENT_TYPE_SINGLE))
							typeOfUrl = OCConstants.SHORTURL_TYPE_BARCODE_TYPE_SINGLE;// startsWith(OCConstants.SHORTURL_CODE_PREFIX_CC)
																						// ?);
					}
					smsCampaignUrl = new SMSCampaignUrls(
							smsCampaignReport.getSmsCrId(), null, url,
							codeTokenArr[1], typeOfUrl);
					if (urlMap != null && urlMap.containsKey(url))
						smsCampaignUrl.setOriginalUrl(urlMap.get(url));
					urlList.add(smsCampaignUrl);

				}
				SMSCampaignUrlDao SMSCampaignUrlDao = (SMSCampaignUrlDao) ServiceLocator
						.getInstance().getDAOByName(
								OCConstants.SMSCAMPAIGNURL_DAO);
				SMSCampaignUrlDaoForDML SMSCampaignUrlDaoForDML = (SMSCampaignUrlDaoForDML) ServiceLocator
						.getInstance().getDAOForDMLByName(
								OCConstants.SMSCAMPAIGNURL_DAO_FOR_DML);
				if (urlList.size() > 0) {

					//SMSCampaignUrlDao.saveByCollection(urlList);
					SMSCampaignUrlDaoForDML.saveByCollection(urlList);
					smsCampUrlList.addAll(urlList);
					urlList.clear();
				}

			}// if

			if (totalAvailabeleContacts <= 0 || configuredCount <= 0) {// this
																		// never
																		// be
																		// happened

				// TODO need to create report for it with all 0 counts....
				if (logger.isInfoEnabled())
					logger.info("num of contacts in sms_tempcontacts is "
							+ totalAvailabeleContacts);

				msgStr = "SMS name :"
						+ smsCampaignName
						+ "\n"
						+ "Found 0 mobile contacts in the mailing lists which "
						+ "are configured for the SMS campaign.\n"
						+ "Reason may be, configured contacts  exists in the suppressed contacts list ";

				Messages messages = new Messages("SMS Details ",
						"No mobile contacts", msgStr, cal, "Inbox", false,
						"Info", user);
				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
				smsCampaignReport.setStatus(Constants.CR_STATUS_SENT);
				smsCampaignReportDaoForDML.saveOrUpdate(smsCampaignReport);

				smsCampaignReport.setStatus(Constants.CR_STATUS_SENT);
				//smsCampaignReportDao.saveOrUpdate(smsCampaignReport);
				smsCampaignReportDaoForDML.saveOrUpdate(smsCampaignReport);
				
				return;
			}

			// boolean isNeedDeliveryReport = false;
			int creditsToBeDeduct = 0;
			SMSRecipientProvider smsRecipientProvider = new SMSRecipientProvider(
					applicationContext, smsCampaignReport, totalSizeInt,
					smsCampaign.getSmsCampaignId(), totalPhSet);
			smsRecipientProvider.setTempCount(creditsToBeDeduct);
			// Create threads to submit campaign
			List<Thread> threadsList = new ArrayList<Thread>();
			try {
				logger.info("<<< STARTING THE THREADS >>>>");
				if (ocgateway.getMode().equals(Constants.SMS_SENDING_MODE_SMPP)) {// if(userSMSTool.equals(Constants.USER_SMSTOOL_MVAYOO))
																					// {//to
																					// have
																					// only
																					// one
																					// thread
																					// when
																					// runs
																					// with
																					// mvaayoo

					// isNeedDeliveryReport = true;
					Thread th = new SMSMultiThreadedSubmission(
							smsRecipientProvider, user, smsCampaign,
							msgContent, smsCampaignReport, totalPhSet, urlSet,
							smsCampUrlList, Constants.SOURCE_SMS_CAMPAIGN,
							applicationContext, captiwayToSMSApiGateway,
							ocgateway);
					th.setName("thread_campaingEmail:" + (1));
					threadsList.add(th);
					th.start();

				} else if (ocgateway.getMode().equals(
						Constants.SMS_SENDING_MODE_HTTP)) {

					/**
					 * Added for MultiThread SMS submission
					 */

					logger.debug("MultiThread SMS submission is Enabled .......:"
							+ ocgateway.isEnableMultiThreadSub());
					CaptiwayToSMSApiGateway captiwayToSMSApiGatewayObj = null;
					logger.debug("Number of Threads created ..........."
							+ NUMBER_OF_SMS_THREADS);

					if (ocgateway.isEnableMultiThreadSub()) {
						for (int i = 0; i < NUMBER_OF_SMS_THREADS; i++) {
							captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway(
									applicationContext);
							Thread th = new SMSMultiThreadedSubmission(
									smsRecipientProvider, user, smsCampaign,
									msgContent, smsCampaignReport, totalPhSet,
									urlSet, smsCampUrlList,
									Constants.SOURCE_SMS_CAMPAIGN,
									applicationContext,
									captiwayToSMSApiGateway, ocgateway);
							th.setName("thread_campaignSMS:" + (i));
							logger.debug(" Current Thread Name "
									+ Thread.currentThread().getName());
							threadsList.add(th);
							th.start();
						}// for
					} else {
						captiwayToSMSApiGateway = new CaptiwayToSMSApiGateway(
								applicationContext);
						Thread th = new SMSMultiThreadedSubmission(
								smsRecipientProvider, user, smsCampaign,
								msgContent, smsCampaignReport, totalPhSet,
								urlSet, smsCampUrlList,
								Constants.SOURCE_SMS_CAMPAIGN,
								applicationContext, captiwayToSMSApiGateway,
								ocgateway);
						th.setName("thread_campaignSMS:" + (1));
						threadsList.add(th);
						th.start();
					}
					// }//for

				}// else if

			} catch (Exception e) {
				logger.error(
						"Exception : ERROR OCCURED WHILE CREATING THREADS ...",
						e);
			}

			// wait for all threads to finish
			Iterator<Thread> iter;
			iter = threadsList.iterator();

			while (iter.hasNext()) {

				try {

					iter.next().join();
				} catch (InterruptedException oops) {

					logger.error("Interrupted: ", oops);
					// return 1;
				}

			}

			creditsToBeDeduct = smsRecipientProvider.getTempCount();
			if (ocgateway.getMode().equals(Constants.SMS_SENDING_MODE_SMPP))
				captiwayToSMSApiGateway.unbindSession(ocgateway); /*
																 * while
																 * (iter.hasNext
																 * ()) {
																 * 
																 * try {
																 * 
																 * SMSMultiThreadedSubmission
																 * SMSSubmissionThread
																 * = (
																 * SMSMultiThreadedSubmission
																 * )iter.next();
																 * creditsToBeDeduct
																 * +=
																 * SMSSubmissionThread
																 * .
																 * getTempCount(
																 * );
																 * 
																 * 
																 * }catch
																 * (Exception
																 * oops) {
																 * 
																 * logger.error(
																 * "Interrupted: "
																 * , oops);
																 * //return 1; }
																 * 
																 * }
																 */

			logger.info("<<< ALL THREAD EXECUTED SUCCESSFULLY >>>>"
					+ creditsToBeDeduct);

			smsCampaignReport.setStatus(Constants.CR_STATUS_SENT);
			smsCampaignReport.setSent(smsCampaignSentDao
					.getSentCount(smsCampaignReport.getSmsCrId()));
			smsCampaignReport.setConfigured(configuredCount);
			smsCampaignReport.setSuppressedCount(suppressedCount);
			smsCampaignReport.setPreferenceCount(preferenceCount);

			//smsCampaignReportDao.saveOrUpdate(smsCampaignReport);
			smsCampaignReportDaoForDML.saveOrUpdate(smsCampaignReport);
			//smsCampaignReportDao.updateBounceReport(smsCampaignReport.getSmsCrId());
			smsCampaignReportDaoForDML.updateBounceReport(smsCampaignReport.getSmsCrId());


			if (ocgateway.isPullReports()) {
				SMSDeliveryReportDao smsDeliveryReportDao = (SMSDeliveryReportDao) applicationContext
						.getBean("smsDeliveryReportDao");
				SMSDeliveryReportDaoForDML smsDeliveryReportDaoForDML = (SMSDeliveryReportDaoForDML) applicationContext
						.getBean("smsDeliveryReportDaoForDML");

				String requestId = captiwayToSMSApiGateway
						.getGatewaySpecificRequestIdToPUllReports(ocgateway
								.getGatewayName());

				SMSDeliveryReport smsDeliveryReport = new SMSDeliveryReport(
						requestId, smsCampaignReport.getSmsCrId(),
						Constants.CR_DLVR_STATUS_ACTIVE, Calendar.getInstance());

				smsDeliveryReport.setUserSMSTool(ocgateway.getId());
				// smsDeliveryReport.setIsTransactional(smsCampaign.getMessageType().toString());
				//smsDeliveryReportDao.saveOrUpdate(smsDeliveryReport);
				smsDeliveryReportDaoForDML.saveOrUpdate(smsDeliveryReport);

			}

			if (logger.isDebugEnabled())
				logger.debug("bounced count ::"
						+ smsCampaignReport.getBounces());

			// smsCampaignsDao.updateSmsCampaignStatus(smsCampaign.getSmsCampaignId());
			smsCampaignsDaoForDML.updateSmsCampaignStatus(smsCampaign
					.getSmsCampaignId());

			if (!useMQS) {
				String strName = eventTrigger.getTriggerName();
				msgStr = "Trigger SMS Name :" + strName + "\n"
						+ "Sent successfully to " + smsCampaignReport.getSent()
						+ " unique contacts";

				Messages messages = new Messages("SMS Details ",
						"SMS sent successfully", msgStr, cal, "Inbox", false,
						"Info", user);
				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
				//usersDao.updateUsedSMSCount(smsCampaign.getUsers().getUserId(),creditsToBeDeduct);
				usersDaoForDML.updateUsedSMSCount(smsCampaign.getUsers().getUserId(), creditsToBeDeduct);

				Utility.updateCouponCodeCounts(applicationContext, totalPhSet);
			}

			// update last smsDate to the contacts
			String smsDate = MyCalendar.calendarToString(
					smsCampaignReport.getSentDate(),
					MyCalendar.FORMAT_DATETIME_STYEAR);
			contactsDaoForDML.updatelastSMSDate(smsCampaignReport.getSmsCrId(),
					smsDate);

			long optionsFlag = eventTrigger.getOptionsFlag();

			if (((optionsFlag & Constants.ET_ADD_CONTACTS_TO_ML_FLAG) == Constants.ET_ADD_CONTACTS_TO_ML_FLAG)
					|| ((optionsFlag & Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG) == Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG)) {

				if (logger.isInfoEnabled())
					logger.info("===========entered to copy/remove===========");

				int contactUpdatedCount = copyOrRemoveEtContacts(eventTrigger);

				if (logger.isInfoEnabled())
					logger.info("===========completed copy/remove==========="
							+ contactUpdatedCount);

				if (contactUpdatedCount < 0) {

					if (logger.isInfoEnabled())
						logger.info("Unable to perform copy/remove operations due to some Exception...updatedCount = "
								+ updatedCount);
					// TODO check what has to be written here
				}

				msgStr = "Event Trigger Name : "
						+ eventTrigger.getTriggerName() + "\r\n";
				msgStr = msgStr
						+ " \r\n Copy/Remove was \r"
						+ (contactUpdatedCount > 0 ? "successful"
								: "unsuccessFul") + " \n";
				Messages msgs = new Messages("Event Trigger",
						"Contacts copied/moved ", msgStr, cal, "Inbox", false,
						"Info", eventTrigger.getUsers());

				//messagesDao.saveOrUpdate(msgs);
				messagesDaoForDML.saveOrUpdate(msgs);

			}

			int updateCount = 0;
			String fromTable = Constants.STRING_NILL;
			if ((eventTrigger.getTrType() & Constants.ET_TYPE_ON_CONTACT_DATE) <= 0) {

				fromTable = "event_trigger_events";

			} else {
				fromTable = "contact_specific_date_events";

			}

			//updateCount = eventTriggerDao.updateSMSStatusFromEvents(userId,eventTrigger.getId(), smsCampaignReport.getSmsCrId(),fromTable);
			updateCount = eventTriggerDaoForDML.updateSMSStatusFromEvents(userId, eventTrigger.getId(), smsCampaignReport.getSmsCrId(), fromTable);

			//eventTriggerDao.updateEventsSMSSentId(userId, eventTrigger.getId(),smsCampaignReport.getSmsCrId(), fromTable);
			eventTriggerDaoForDML.updateEventsSMSSentId(userId, eventTrigger.getId(), smsCampaignReport.getSmsCrId(), fromTable);		

			if (logger.isInfoEnabled())
				logger.info(">>>>>>>>> Trigger processing is completed, satus updated ::"
						+ updateCount);

		} catch (Exception e) {
			logger.error("** Error occured while submitting the SMS campaigns",
					e);
		}

	}// sendEventTriggerSMSCampaign(-)

	private int copyOrRemoveEtContacts(EventTrigger eventTrigger) { // added for
																	// New
																	// eventTrigger
		int optionsFlag = eventTrigger.getOptionsFlag();
		/*
		 * 1st performing copying contacts to specified MLs
		 */

		if ((optionsFlag & Constants.ET_ADD_CONTACTS_TO_ML_FLAG) == Constants.ET_ADD_CONTACTS_TO_ML_FLAG) {

			int currentSize = 0;
			boolean fetchFlag = true;

			if (logger.isInfoEnabled())
				logger.info("---- ET_ADD_CONTACTS_TO_ML_FLAG set----");
			// Set<MailingList> copyToMailingListSet;
			MailingList copyToMailingList = eventTrigger
					.getAddTriggerContactsToMl();

			if (copyToMailingList == null) {

				if (logger.isInfoEnabled())
					logger.info("No destination mailings lits are given for the trigger");
				return -1;
			}
			long mlbits = copyToMailingList.getMlBit().longValue();
			List<Contacts> contactsUpdateList = new ArrayList<Contacts>();

			while (fetchFlag) {

				List<Contacts> tempContactsList = null;
				String finalQry = " SELECT cid FROM sms_tempcontacts LIMIT "
						+ currentSize + "," + 500;

				List<Map<String, Object>> retList = contactsDao
						.getConatctIds(finalQry);
				if (retList == null || retList.isEmpty()) {

					if (logger.isInfoEnabled())
						logger.info(" got no contacts from temp contacts ");
					fetchFlag = false;
					// currentSize = 0;
					break;
				}// if
				String cidStr = Constants.STRING_NILL;

				for (Map<String, Object> cidMap : retList) {

					Object Cid = cidMap.get("cid");

					if (!cidStr.isEmpty())
						cidStr += Constants.DELIMETER_COMMA;

					cidStr += Cid.toString();

				}// for
					// if(logger.isInfoEnabled())
					// logger.info(" got cids from temp contacts "+cidStr);
				tempContactsList = contactsDao.getContactsByCids(cidStr);

				if (tempContactsList == null || tempContactsList.isEmpty()) {

					if (logger.isInfoEnabled())
						logger.info(" got " + tempContactsList.size()
								+ " records from temp contacts ");
					fetchFlag = false;
					// currentSize = 0;
					break;
				}// if
				for (Contacts contact : tempContactsList) {

					contact.setMlBits(contact.getMlBits() | mlbits);
					contactsUpdateList.add(contact);

				}// for

				if (contactsUpdateList.size() > 0) {

					contactsDaoForDML.saveByCollection(contactsUpdateList);
					contactsUpdateList.clear();

				}// if
				currentSize += 500;

			}// while

		}// if

		/*
		 * Then check if remove flag is set...this should be separate if { }.
		 */

		if ((optionsFlag & Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG) == Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG) {

			int currentSize = 0;
			boolean fetchFlag = true;
			List<Contacts> contactsUpdateList = new ArrayList<Contacts>();

			Set<MailingList> removeFromMailingListSet;
			removeFromMailingListSet = eventTrigger.getMailingLists();

			if (removeFromMailingListSet == null
					|| removeFromMailingListSet.size() == 0) {

				if (logger.isInfoEnabled())
					logger.info("No destination mailings lits are given for the trigger");
				return -1;
			}
			long mlbits = Utility.getMlsBit(removeFromMailingListSet);

			while (fetchFlag) {

				List<Contacts> tempContactsList = null;

				String finalQry = " SELECT cid FROM sms_tempcontacts LIMIT "
						+ currentSize + "," + 500;

				List<Map<String, Object>> retList = contactsDao
						.getConatctIds(finalQry);
				if (retList == null || retList.isEmpty()) {

					if (logger.isInfoEnabled())
						logger.info(" got no contacts from temp contacts ");
					fetchFlag = false;
					// currentSize = 0;
					break;
				}// if
				String cidStr = Constants.STRING_NILL;

				for (Map<String, Object> cidMap : retList) {

					Object Cid = cidMap.get("cid");

					if (!cidStr.isEmpty())
						cidStr += Constants.DELIMETER_COMMA;

					cidStr += Cid.toString();

				}// for

				tempContactsList = contactsDao.getContactsByCids(cidStr);
				if (tempContactsList == null || tempContactsList.isEmpty()) {

					if (logger.isInfoEnabled())
						logger.info(" got " + tempContactsList.size()
								+ " records from temp contacts ");
					fetchFlag = false;
					// currentSize = 0;
					break;
				}// if
				for (Contacts contact : tempContactsList) {

					contact.setMlBits(contact.getMlBits().longValue()
							& (~mlbits));
					// if(contact.getMlSet().size() == 0){
					if (contact.getMlBits().longValue() == 0l) {
						Utility.setContactFieldsOnDeletion(contact);
						// contactsDao.saveOrUpdate(contact);
					}

					contactsUpdateList.add(contact);

				}// for

				if (contactsUpdateList.size() > 0) {

					contactsDaoForDML.saveByCollection(contactsUpdateList);
					contactsUpdateList.clear();

				}// if
				currentSize += 500;

			}// while

		} // Remove
		return 1;
	}// copyOrRemoveEtContacts

	/**
	 * added for EventTrigger sms changes
	 * 
	 * @param eventTrigger
	 */
	/*
	 * private void sendEventTrigSMSCampaign(EventTrigger eventTrigger) {
	 * 
	 * 
	 * try {
	 * 
	 * 
	 * 
	 * 1. Fetch the sms Campaign object. 2. insert into sms_tempcontacts 3.
	 * Filter contacts based on detailed fromSource.. 4. Get the mesgcomtent of
	 * the sms camp obj 5. Based on mesg type split the mesg.. 6. Get the count
	 * of contacts and compare it with avaibale user sms count 7. If user cnt
	 * is < exitelse cnt..to step 6 8. Form an sms campaign report object and
	 * save it as SENDING 9. Fetch the contacts from the temp table if got zero
	 * contacts exit.. 10. For each contactform smsCampaignSent obj and save
	 * it to db with success 11. Send sms to resp api based on mesOveroption 12.
	 * After sending to all the contacts update user sms count and 13. repeat
	 * above steps for all the contacts on sms_tepmcontacts table.... 14. After
	 * everything Delete from the temp table
	 * 
	 * 
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("----------processing eventTrigger to send sms-------");
	 * 
	 * if(eventTrigger == null) {
	 * 
	 * if(logger.isInfoEnabled()) logger.info(" eventTrigger obj is null ");
	 * return;
	 * 
	 * }
	 *//******** get the Trigger associated mailing list names(for smsCampReportLists) ****************/
	/*
	 * String listsName = ""; Set<MailingList> mlList =
	 * eventTrigger.getMailingLists();
	 * 
	 * if(mlList != null && mlList.size() > 0 ) {
	 * 
	 * for (MailingList mailingList : mlList) {
	 * 
	 * if(listsName.length() > 0) {
	 * 
	 * listsName += ","; }
	 * 
	 * listsName += mailingList.getListName(); }//for }//if
	 * 
	 * boolean success = false;
	 * 
	 * String detailedFromSource = ""; String[] smsQryStr=
	 * eventTrigger.getSmsQueryStr().split("\\|"); if(logger.isInfoEnabled())
	 * logger
	 * .info(" eventTrigger.getSmsQueryStr() "+eventTrigger.getSmsQueryStr());
	 * String smsQry = smsQryStr[0]; detailedFromSource = smsQryStr[1];
	 * 
	 * // Fetch the SMS Campaign object SMSCampaigns smsCampaign =
	 * smsCampaignsDao.findByCampaignId(eventTrigger.getSmsId());
	 * 
	 * if(smsCampaign == null){
	 * 
	 * if(logger.isInfoEnabled()) logger.info("got a null sms obj"); return; }
	 * 
	 * //Inserting data into sms_tempcontacts using query smsQry...
	 * 
	 * //TODO need to create message after coming query
	 * 
	 * 
	 * 
	 * 
	 * int totalAvailabeleEtContacts = contactsDao.createSmsTempContacts(smsQry,
	 * null, eventTrigger.getUsers().getUserId(), false);
	 * 
	 * 
	 * if(totalAvailabeleEtContacts <= 0){
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("num of contacts in sms_tempcontacts is "
	 * +totalAvailabeleEtContacts); return; }
	 * 
	 * //TODO filter the contacts joining tables sms_campaign_sent &
	 * sms_campaign_report totalAvailabeleEtContacts = totalAvailabeleEtContacts
	 * - filterSmsTempContacts(smsCampaign,eventTrigger,detailedFromSource);
	 * 
	 * 
	 * if(totalAvailabeleEtContacts <= 0){
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info(" After filtering num of contacts in sms_tempcontacts is o");
	 * return; } if(logger.isInfoEnabled())
	 * logger.info("num of contacts in sms_tempcontacts is "
	 * +totalAvailabeleEtContacts);
	 * 
	 * String useMqsStr = PropertyUtil.getPropertyValueFromDB("useMQS"); boolean
	 * useMQS =( useMqsStr == null ? true : useMqsStr.equalsIgnoreCase("true"));
	 * 
	 * ArrayList<String> msgContentLst = null; boolean placeHolders = false;
	 * String msgContent = smsCampaign.getMessageContent();
	 * 
	 * if(smsCampaign.getMessageType() == 1){//if normal text
	 * 
	 * if(msgContent.contains("|^GEN_") || msgContent.contains("|^CF_")) {
	 * placeHolders = true; msgContentLst = splitSMSMessage(msgContent, 160);
	 * }//if else{ placeHolders = false; msgContentLst =
	 * splitSMSMessage(msgContent, 170);
	 * 
	 * } } else if(smsCampaign.getMessageType() == 2) {//if language sms
	 * 
	 * msgContent = Utility.HexToString(msgContent); placeHolders =
	 * msgContent.contains("|^GEN_") || msgContent.contains("|^CF_");
	 * msgContentLst = splitSMSMessage(msgContent, 70); } // else if
	 * 
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("----totAvailableCounts of the user to send sms is====>"
	 * +totalAvailabeleEtContacts);
	 * 
	 * //find the smscount is available or not if(!useMQS) {
	 * 
	 * success = false; List<Integer> list = new ArrayList<Integer>(); try {
	 *//***** try to check the sms Count of user ***/
	/*
	 * list =
	 * usersDao.getAvailableSMSCountOfUser(smsCampaign.getUsers().getUserId());
	 * 
	 * if(list == null || list.get(0) == null) {
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("the available SMS count is 0 . "); Messages messages = new
	 * Messages
	 * ("SMS Campaign","User sms count limit reached","Your SMS count has reached, "
	 * + "please renew your sms count to avoid sms campaign failure.",
	 * Calendar.getInstance(), "Inbox", false, "info", smsCampaign.getUsers());
	 * messagesDao.saveOrUpdate(messages); return; }
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("the available SMS count of user is="+list.get(0));
	 * 
	 * if(list.get(0).intValue() < (totalAvailabeleEtContacts *
	 * msgContentLst.size()) ) {
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info(" Available limit is less than the configured contacts count"
	 * ); return; } success = true; } catch (Exception e) {
	 * 
	 * if(logger.isErrorEnabled())
	 * logger.error("** Exception while getting the available count" +
	 * " for the user:"+smsCampaign.getUsers().getUserId(), e); return; }
	 * 
	 * } // if(!useMQS)
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("smsCampaign is=====>"+smsCampaign); byte msgOverSizeOption
	 * =smsCampaign.getMessageSizeOption(); String senderId =
	 * smsCampaign.getSenderId();
	 * 
	 * int stIndex = 0; int size = 100; int tempCount = 0;//required to update
	 * the smscount String sendingMsg; boolean limit = false;//it will be used
	 * to specify the NetCoreApi that it has reach the limit boolean fetchFlag =
	 * true; SMSCampaignReport smsCampaignReport = null; SMSCampaignSent
	 * smsCampaignSent = null; String sentId = ""; List<Long>contactIds = new
	 * ArrayList<Long>();//y it is require for ET???????????????/
	 * 
	 * Calendar cal = Calendar.getInstance();
	 * 
	 * smsCampaignReport = new SMSCampaignReport(smsCampaign.getUsers(),
	 * smsCampaign.getSmsCampaignName(), smsCampaign.getMessageContent(), cal ,
	 * 0, 0, 0, 0, 0, Constants.CR_STATUS_SENDING,detailedFromSource); try{
	 * smsCampaignReportDao.saveOrUpdate(smsCampaignReport); SMSCampReportLists
	 * smsCampReportLists = new
	 * SMSCampReportLists(smsCampaignReport.getSmsCrId());
	 * smsCampReportLists.setListsName(listsName);
	 * smsCampReportListsDao.saveOrUpdate(smsCampReportLists);
	 * 
	 * 
	 * }catch (Exception e) {
	 * logger.error("** Exception while saving the SMSCampaignReport object",e);
	 * 
	 * }
	 * 
	 * if(logger.isInfoEnabled()) logger.info("------ready to send sms-------");
	 * 
	 * //get the contacts data from the contacts based on the contacts present
	 * in temp_activity_data table
	 * 
	 * if(logger.isInfoEnabled()) logger.info("fetching contacts*********"); //
	 * String quryforupdateCompCont = ""; String cids="";//y it is require for
	 * ET????????????????????
	 * 
	 * 
	 * while(fetchFlag) {
	 * 
	 * List<Contacts> tempContactsList = null;
	 * 
	 * tempContactsList =
	 * contactsDao.getSegmentedTempContacts("SELECT * FROM sms_tempcontacts",
	 * stIndex, size);
	 * 
	 * if(tempContactsList == null || tempContactsList.isEmpty()) {
	 * 
	 * if(logger.isInfoEnabled()) logger.info(" got "+tempContactsList.size()+
	 * " records from sms temp contacts "); fetchFlag = false; //currentSize =
	 * 0; break; }
	 * 
	 * 
	 * if(tempContactsList.size() <= 0){
	 * 
	 * if(logger.isInfoEnabled()) logger.info(" got zero contacts returning");
	 * return; }
	 * 
	 * for (Contacts contacts : tempContactsList) {//y it is require for
	 * ET???????????????
	 * 
	 * if(cids.length() > 0){
	 * 
	 * cids += ","; cids += contacts.getContactId(); } else { cids +=
	 * contacts.getContactId(); }
	 * 
	 * contactIds.add(contacts.getContactId());
	 * 
	 * } // for
	 * 
	 * int tempMsgType = smsCampaign.getMessageType();// == 1) ? "N" : "L";
	 * 
	 * limit = (tempContactsList.size() == size);//to let the api opens the
	 * connection only for 100 contacts
	 * 
	 * for (Contacts contacts : tempContactsList) {
	 * 
	 * if( (contacts.getMobilePhone()).length() < 10 ) {
	 * 
	 * if(logger.isErrorEnabled()) logger.error(
	 * "Error,got the contact with the mobile number having less than 10 digits...."
	 * ); continue;
	 * 
	 * 
	 * } //msgSequence++; smsCampaignSent = new
	 * SMSCampaignSent(contacts.getMobilePhone(), smsCampaignReport,
	 * Constants.CS_STATUS_SUCCESS,contacts.getContactId());
	 * 
	 * smsCampaignSentDao.saveOrUpdate(smsCampaignSent);
	 * 
	 * sentId = ""+smsCampaignSent.getSentId(); int lengthOfSentId =
	 * sentId.length();
	 * 
	 * for(int j=lengthOfSentId; j<8; j++){
	 * 
	 * sentId = "0"+sentId;
	 * 
	 * }
	 * 
	 * if(logger.isInfoEnabled()) logger.info("sent id is=====>"+sentId);
	 * if(logger.isInfoEnabled())
	 * logger.info(contacts.getMobilePhone()+"  , "+contacts.getEmailId());
	 *//**
	 * if SMS contains place holders we submit individually
	 */
	/*
	 * 
	 * if(placeHolders) { // single sending
	 * 
	 * if(msgOverSizeOption == 1) { // split and send as multiple messages
	 * 
	 * 
	 * for (int i = 0; i < msgContentLst.size(); i++) {
	 * 
	 * tempCount++; sendingMsg = replacePlaceHolders(msgContentLst.get(i),
	 * contacts); String multiPartSentId = 1+sentId; if(msgContentLst.size() >
	 * 1) {
	 * 
	 * sendingMsg = "("+ (i+1) +"/"+ msgContentLst.size() +") " +sendingMsg;
	 * 
	 * multiPartSentId = (i+1)+sentId; }
	 * 
	 * if(logger.isInfoEnabled()) logger.info("msgContent--->"+sendingMsg);
	 * 
	 * captiwayToSMSApiGateway.sendToSMSApi(Constants.USER_SMSTOOL_CLICKATELL,
	 * sendingMsg, contacts.getMobilePhone(), tempMsgType, multiPartSentId,
	 * "9848495956", contacts.getMobilePhone(), "1",
	 * smsCampaignReport.getSmsCrId(),limit,tempContactsList.size(), senderId);
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("sms sent to-----> "+contacts.getFirstName());
	 * 
	 * 
	 * } // for each
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * for (int i = 0; i < msgContentLst.size(); i++) {
	 * 
	 * tempCount++; sendingMsg = replacePlaceHolders(msgContentLst.get(i),
	 * contacts);
	 * 
	 * if(msgContentLst.size() > 1) {
	 * 
	 * sendingMsg = "("+ (i+1) +"/"+ msgContentLst.size() +") " +sendingMsg; }
	 * 
	 * logger.info("msgContent--->"+sendingMsg);
	 * 
	 * captiwayToSMSApiGateway.sendToSMSApi(Constants.USER_SMSTOOL_CLICKATELL,
	 * sendingMsg, ""+contacts.getPhone(), tempMsgType, sentId, "9848495956",
	 * ""+contacts.getPhone(), "1",
	 * smsCampaignReport.getSmsCrId(),limit,tempContactsList.size(),senderId);
	 * 
	 * logger.info("sms sent to-----> "+contacts.getFirstName());
	 * 
	 * } // for each
	 * 
	 * } else if(msgOverSizeOption == 2) { // truncate and send single msg
	 * 
	 * tempCount++; sendingMsg = replacePlaceHolders(msgContentLst.get(0),
	 * contacts);
	 * 
	 * captiwayToSMSApiGateway.sendToSMSApi(Constants.USER_SMSTOOL_CLICKATELL,
	 * sendingMsg, contacts.getMobilePhone(), tempMsgType, 1+sentId,
	 * "9848495956", contacts.getMobilePhone(), "1",
	 * smsCampaignReport.getSmsCrId(),limit,tempContactsList.size(), senderId);
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("sms sent to-----> "+contacts.getFirstName());
	 * 
	 * } else if(msgOverSizeOption == 4) { // do not send the sms if it is too
	 * long
	 * 
	 * if(msgContentLst.size()>1){
	 * 
	 * Messages messages = new
	 * Messages("SMS Campaign","SMS is ignored","SMS size option is-not " +
	 * "sending SMS as it is too long , " +
	 * "the SMS is ignored from sending.",Calendar.getInstance(),"Inbox",false,
	 * "info", smsCampaign.getUsers()); messagesDao.saveOrUpdate(messages);
	 * 
	 * } else{ tempCount++; sendingMsg =
	 * replacePlaceHolders(msgContentLst.get(0), contacts);
	 * 
	 * captiwayToSMSApiGateway.sendToSMSApi(Constants.USER_SMSTOOL_CLICKATELL,
	 * sendingMsg, contacts.getMobilePhone(), tempMsgType, 1+sentId,
	 * "9848495956", contacts.getMobilePhone(), "1",
	 * smsCampaignReport.getSmsCrId(),limit,tempContactsList.size(),senderId);
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("sms sent to-----> "+contacts.getFirstName());
	 * 
	 * } } else if(msgOverSizeOption == 8){ // send as a long message
	 * 
	 * tempCount += msgContentLst.size() ; String msgContentToBeSent =
	 * replacePlaceHolders(msgContent, contacts);
	 * 
	 * captiwayToSMSApiGateway.sendToSMSApi(Constants.USER_SMSTOOL_CLICKATELL,
	 * msgContentToBeSent, contacts.getMobilePhone(), tempMsgType, 1+sentId,
	 * "9848495956", contacts.getMobilePhone(), "1",
	 * smsCampaignReport.getSmsCrId(),limit,tempContactsList.size(),senderId);
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("sms sent to-----> "+contacts.getFirstName());
	 * 
	 * }//else if
	 * 
	 * success = true; } else { // else follow group submission
	 * 
	 * if(msgOverSizeOption == 1){ // split and send as multiple messages
	 * 
	 * for (int i = 0; i < msgContentLst.size(); i++) {
	 * 
	 * tempCount++; sendingMsg = msgContentLst.get(i); String multiPartSentId =
	 * 1+sentId;
	 * 
	 * if(msgContentLst.size()>1) {
	 * 
	 * sendingMsg = "("+ (i+1) +"/"+ msgContentLst.size() +") " +sendingMsg;
	 * 
	 * multiPartSentId = (i+1)+sentId; }
	 * 
	 * 
	 * captiwayToSMSApiGateway.sendToSMSApi(Constants.USER_SMSTOOL_CLICKATELL,
	 * sendingMsg, contacts.getMobilePhone(), tempMsgType, multiPartSentId,
	 * "9848495956", contacts.getMobilePhone(), "1",
	 * smsCampaignReport.getSmsCrId(),limit,tempContactsList.size(), senderId);
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("sms sent to-----> "+contacts.getFirstName());
	 * 
	 * } // for each
	 * 
	 * 
	 * for (int i = 0; i < msgContentLst.size(); i++) {
	 * 
	 * tempCount++; sendingMsg = msgContentLst.get(i);
	 * 
	 * if(msgContentLst.size()>1) {
	 * 
	 * sendingMsg = "("+ (i+1) +"/"+ msgContentLst.size() +") " +sendingMsg; }
	 * 
	 * 
	 * captiwayToSMSApiGateway.sendToSMSApi(Constants.USER_SMSTOOL_CLICKATELL,
	 * sendingMsg, ""+contacts.getPhone(), tempMsgType, sentId, "9848495956",
	 * ""+contacts.getPhone(), "1",
	 * smsCampaignReport.getSmsCrId(),limit,tempContactsList.size(),senderId);
	 * 
	 * logger.info("sms sent to-----> "+contacts.getFirstName());
	 * 
	 * } // for each
	 * 
	 * } else if(msgOverSizeOption == 2) { // truncate and send single msg
	 * 
	 * tempCount++; sendingMsg = msgContentLst.get(0);
	 * 
	 * captiwayToSMSApiGateway.sendToSMSApi(Constants.USER_SMSTOOL_CLICKATELL,
	 * sendingMsg, contacts.getMobilePhone(), tempMsgType, 1+sentId,
	 * "9848495956", contacts.getMobilePhone(), "1",
	 * smsCampaignReport.getSmsCrId(),limit,tempContactsList.size(),senderId);
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("sms sent to-----> "+contacts.getFirstName());
	 * 
	 * 
	 * } else if(msgOverSizeOption == 4) { // do not send the sms if it is too
	 * long
	 * 
	 * if(msgContentLst.size()>1){
	 * 
	 * Messages messages = new
	 * Messages("SMS Campaign","SMS is ignored","SMS size option is-do not " +
	 * "sending SMS, " + "the SMS is ignored from sending.",
	 * Calendar.getInstance(),"Inbox",false, "info", smsCampaign.getUsers());
	 * messagesDao.saveOrUpdate(messages);
	 * 
	 * } else{
	 * 
	 * tempCount++; sendingMsg = msgContentLst.get(0);
	 * 
	 * captiwayToSMSApiGateway.sendToSMSApi(Constants.USER_SMSTOOL_CLICKATELL,
	 * sendingMsg, contacts.getMobilePhone(), tempMsgType, 1+sentId,
	 * "9848495956", contacts.getMobilePhone(), "1",
	 * smsCampaignReport.getSmsCrId(),limit,tempContactsList.size(),senderId);
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("sms sent to-----> "+contacts.getFirstName());
	 * 
	 * }//else
	 * 
	 * } else if(msgOverSizeOption == 8){ // send as a long msg
	 * 
	 * tempCount += msgContentLst.size() ;
	 * 
	 * captiwayToSMSApiGateway.sendToSMSApi(Constants.USER_SMSTOOL_CLICKATELL,
	 * msgContent, contacts.getMobilePhone(), tempMsgType, 1+sentId,
	 * "9848495956", contacts.getMobilePhone(), "1",
	 * smsCampaignReport.getSmsCrId(),limit,tempContactsList.size(),senderId);
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("sms sent to-----> "+contacts.getFirstName());
	 * 
	 * }//else if
	 * 
	 * success = true; } // else
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("****************SMS sent suscessfully***********");
	 * 
	 * } // foreach contact
	 * 
	 * contactIds.clear();//y it is require for ET??????????????
	 * 
	 * stIndex += size;
	 * 
	 * } //while
	 * 
	 * smsCampaignsDao.deleteSmsTempContacts();
	 * 
	 * if(!useMQS) {
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("updating smscount...num of sms used = "+tempCount);
	 * //usersDao.updateUsedSMSCount(smsCampaign.getUsers().getUserId(), tempCount);
	   usersDaoForDML.updateUsedSMSCount(smsCampaign.getUsers().getUserId(),tempCount);
	   }
	 * 
	 * smsCampaignReport.setStatus(Constants.CR_STATUS_SENT);
	 * smsCampaignReport.setSent
	 * (smsCampaignSentDao.getSentCount(smsCampaignReport.getSmsCrId()));
	 * smsCampaignReportDao.saveOrUpdate(smsCampaignReport); return;//y need to
	 * return from here????????????????????/
	 * 
	 * } catch (Exception e) {
	 * 
	 * logger.info("**Exception in sendEventTrigSMSCampaign");
	 * logger.error("Exception ::::" , e); return; }
	 * 
	 * } //sendEventTrigSMSCampaign
	 */
	/**
	 * 
	 * @param eventTrigger
	 * @param totalSizeInt
	 * @return
	 * 
	 *         Added for EventTrigger sms changes this method removes all the
	 *         contacts to whom we have already sent for similar type of trigger
	 *         from sms_tempContacts table
	 */
	/*
	 * private int filterSmsTempContacts(SMSCampaigns smsCampaigns, EventTrigger
	 * eventTrigger, String detailedFromSource) {
	 * 
	 * List<Long> crList = null; String crListStr = ""; long userId =
	 * eventTrigger.getUsers().getUserId(); int optionsFlag =
	 * eventTrigger.getOptionsFlag();
	 * 
	 * //String fromSource = smsCampaigns.getSourceType();
	 * //if(logger.isInfoEnabled()) logger.info("fromSource = "+fromSource);
	 * if(logger.isInfoEnabled())
	 * logger.info("detailedFromSource = "+detailedFromSource);
	 * if(logger.isInfoEnabled()) logger.info("userId = "+userId);
	 * if(logger.isInfoEnabled())
	 * logger.info("event trig name = "+eventTrigger.getTriggerName());
	 * if(logger.isInfoEnabled())
	 * logger.info("event trig id = "+eventTrigger.getId());
	 * 
	 * try{
	 * 
	 * if( (optionsFlag & Constants.ET_FILTER_BY_TRIGGER_TYPE_FLAG) ==
	 * Constants.ET_FILTER_BY_TRIGGER_TYPE_FLAG) {
	 * 
	 * if(logger.isInfoEnabled()) logger.info("filter by trigger type flag");
	 * crList =
	 * smsCampaignReportDao.getCrIdsBySourceType(detailedFromSource,userId);
	 * 
	 * } else { if(logger.isInfoEnabled())
	 * logger.info("----trig name = "+eventTrigger.getTriggerName());
	 * if(logger.isInfoEnabled())
	 * logger.info("filter by trigger type flag not set "); crList =
	 * smsCampaignReportDao
	 * .getCrIdsByTriggerTypeOrName(smsCampaigns.getSmsCampaignName(),
	 * detailedFromSource,userId,smsCampaigns.getSmsCampaignId()); }
	 * 
	 * 
	 * if(crList == null) {
	 * 
	 * if(logger.isInfoEnabled()) logger.info(
	 * "Got a null crIdsList due to either empty TriggerName or Exception in query"
	 * ); return -1; }
	 * 
	 * if(crList.isEmpty()) { if(logger.isInfoEnabled()) logger.info(
	 * "No campaign reports exists for this Trigger/Campaign..so no records to be deleted"
	 * ); if(logger.isInfoEnabled())
	 * logger.info(" returning from delefromtempcontacts with totalsizeInt 0");
	 * return 0; }
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("just returned getCrIdsBySouceType with crList size "
	 * +crList.size());
	 * 
	 * for(Iterator<Long> iterator = crList.iterator();iterator.hasNext();){
	 * 
	 * if(crListStr.length() == 0) { crListStr += iterator.next().toString(); }
	 * else { crListStr += ","+iterator.next().toString(); } } // for
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("after for loop crList appended to crListStr "+crListStr);
	 * if(crListStr.length()>0){
	 * 
	 * String delQueryStr = " DELETE FROM sms_tempcontacts " +
	 * " WHERE cid IN (SELECT contact_id FROM sms_campaign_sent " +
	 * " WHERE sms_cr_id IN ("+crListStr+") )";
	 * 
	 * if(logger.isInfoEnabled())
	 * logger.info("just before deleting from sms_tempcontacts"); return
	 * contactsDao.executeJdbcUpdateQuery(delQueryStr);
	 * 
	 * } // crListLength > 0 else{ if(logger.isInfoEnabled()) logger.info(
	 * " no records to delete from sms_tempcontacts as crListStr.length()= "
	 * +crListStr.length()); return 0; }
	 * 
	 * } catch(Exception e) { if(logger.isInfoEnabled())
	 * logger.info("Exception while deleting from SmsTempContacts");
	 * logger.error("Exception ::::" , e); return -1; }
	 * 
	 * } // filterSmsTempContacts
	 */
	/**
	 * this method will be utilize to send program based SMSCampaigns
	 * 
	 * @param autoProgramComponents
	 * @param fromSource
	 */

	public void sendProgramSMSCampaign(
			AutoProgramComponents autoProgramComponents, String fromSource) {

		try {
			if (logger.isInfoEnabled())
				logger.info("----------processing the components to send sms-------");
			if (autoProgramComponents == null) {

				if (logger.isInfoEnabled())
					logger.info("found the autoProgramComponent as null can not send the SMS");
				return;

			}
			boolean success = false;

			AutoProgram autoProgram = autoProgramComponents.getAutoProgram();
			String userSMSTool = autoProgram.getUser().getUserSMSTool();
			Long userId = autoProgram.getUser().getUserId();
			SMSCampaigns smsCampaign = smsCampaignsDao
					.findByCampaignId(autoProgramComponents.getSupportId());

			if (smsCampaign == null) {

				if (logger.isInfoEnabled())
					logger.info("got the SMSCampaign object as null,returning from sending program based SMS.....");
				return;
			}

			String useMqsStr = PropertyUtil.getPropertyValueFromDB("useMQS");
			boolean useMQS = (useMqsStr == null ? true : useMqsStr
					.equalsIgnoreCase("true"));

			ArrayList<String> msgContentLst = null;
			boolean placeHolders = false;
			String msgContent = smsCampaign.getMessageContent();

			if (smsCampaign.getMessageType().equals(
					Constants.SMS_TYPE_PROMOTIONAL)) {// if normal text

				if (msgContent.contains("|^GEN_")
						|| msgContent.contains("|^CF_")) {
					placeHolders = true;
					msgContentLst = splitSMSMessage(msgContent, 130);
				}// if
				else {
					placeHolders = false;
					msgContentLst = splitSMSMessage(msgContent, 140);

				}

			} else if (smsCampaign.getMessageType().equals(
					Constants.SMS_TYPE_2_WAY)) {// if language sms
				msgContent = Utility.HexToString(msgContent);
				placeHolders = msgContent.contains("|^GEN_")
						|| msgContent.contains("|^CF_");
				msgContentLst = splitSMSMessage(msgContent, 70);
			}// else if

			/********
			 * get the program associated mailing list names(for
			 * smsCampReportLists)
			 ****************/
			String listsName = "";
			String mlStr = "";
			Set<MailingList> mlList = autoProgram.getMailingLists();

			if (mlList != null && mlList.size() > 0) {

				for (MailingList mailingList : mlList) {

					if (listsName.length() > 0) {

						listsName += ",";
					}

					listsName += mailingList.getListName();

					if (mlStr.length() > 0)
						mlStr += ",";
					mlStr += mailingList.getListId();

				}// for
			}// if

			String label = autoProgramComponents.getLabel();
			if (label != null && !label.trim().equals("")) {

				// inserting into different tables will be more if i do so.....
				contactsDaoForDML.deleteSmsTempContacts();
				/**
				 * String qryStr =
				 * "INSERT IGNORE INTO sms_tempcontacts(SELECT DISTINCT c.*,NULL,"
				 * +
				 * " SUBSTRING_INDEX(c.email_id, '@', -1) FROM contacts c WHERE c.list_id IN("
				 * + mlStr+ ") AND phone IS NOT NULL) ";
				 */
				String totalCountQry = " SELECT COUNT(contactId) FROM Contacts WHERE contactId in(SELECT ta.contactId FROM "
						+ "TempActivityData ta WHERE ta.label in('"
						+ label
						+ "') and ta.programId="
						+ autoProgram.getProgramId()
						+ " and ta.componentId="
						+ autoProgramComponents.getCompId()
						+ ")  AND phone IS NOT NULL";

				int totalContacts = contactsDao
						.getTotalCountOfAvailableContacts(totalCountQry);

				String insertQuery = "INSERT IGNORE INTO sms_tempcontacts(SELECT DISTINCT "
						+ Constants.QRY_COLUMNS_CONTACTS
						+ ",NULL,"
						+ " SUBSTRING_INDEX(c.email_id, '@', -1), null FROM contacts c WHERE c.cid IN("
						+ "SELECT DISTINCT ta.contact_id from temp_activity_data ta where ta.label in('"
						+ label
						+ "') and ta.program_id="
						+ autoProgram.getProgramId()
						+ " and ta.component_id="
						+ autoProgramComponents.getCompId()
						+ ") AND phone IS NOT NULL) ";

				int totalAvailabeleContacts = contactsDaoForDML
						.createSmsTempContacts(insertQuery, null, autoProgram
								.getUser(), false,
								MobileCarriers.mobileCarrierMap
										.get(userSMSTool));

				if (totalAvailabeleContacts <= 0) {

					if (logger.isInfoEnabled())
						logger.info("num of contacts in sms_tempcontacts is "
								+ totalAvailabeleContacts);
					return;
				}

				if (totalAvailabeleContacts < totalContacts) {

					Messages messages = new Messages(
							"Marketing Program-SMS Campaign",
							"Some of the Contacts are 'Suppressed'",
							"Some of the contacts are 'Suppressed',"
									+ "SMS can only be sent to "
									+ totalAvailabeleContacts + " of total "
									+ totalContacts + " contacts.",
							Calendar.getInstance(), "Inbox", false, "info",
							smsCampaign.getUsers());

					//messagesDao.saveOrUpdate(messages);
					messagesDaoForDML.saveOrUpdate(messages);
				}

				// get the number of contacts from the contacts based on the
				// contacts present in temp_activity_data table
				/*
				 * String query =
				 * "select count(c.contactId) from Contacts c where c.contactId in"
				 * +
				 * "(select distinct ta.contactId from TempActivityData ta where ta.label in('"
				 * + label+"') and ta.programId=" +
				 * autoProgram.getProgramId()+"and ta.componentId=" +
				 * autoProgramComponents
				 * .getCompId()+") and c.phone is not null";
				 */

				int totalSizeInt = contactsDao
						.getAvailableContactsFromSmsTempContacts(mlStr, "");

				if (logger.isInfoEnabled())
					logger.info("----total available count of the user to send sms is====>"
							+ totalSizeInt);
				// find the smscount is available or not
				if (!useMQS) {

					success = false;
					List<Integer> list = new ArrayList<Integer>();
					try {
						/***** try to check the sms Count of user ***/
						list = usersDao.getAvailableSMSCountOfUser(autoProgram
								.getUser().getUserId());

						if (list == null || list.get(0) == null) {
							if (logger.isInfoEnabled())
								logger.info("the available SMS count is 0 . ");
							Messages messages = new Messages(
									"SMS Campaign",
									"User sms credit limit reached",
									"Your SMS credit limit has reached, "
											+ "please renew your sms credits to avoid sms campaign failure.",
									Calendar.getInstance(), "Inbox", false,
									"info", autoProgram.getUser());
							//messagesDao.saveOrUpdate(messages);
							messagesDaoForDML.saveOrUpdate(messages);
							return;
						}

						if (logger.isInfoEnabled())
							logger.info("the available SMS count of user is="
									+ list.get(0));

						if (list.get(0).intValue() < (totalSizeInt * msgContentLst
								.size())) {

							if (logger.isInfoEnabled())
								logger.info(" Available limit is less than the configured contacts count");
							return;
						}
						success = true;
					} catch (Exception e) {

						if (logger.isErrorEnabled())
							logger.error(
									"** Exception while getting the available count"
											+ " for the user:"
											+ smsCampaign.getUsers()
													.getUserId(), e);
						return;
					}

				} // if(!useMQS)
				if (logger.isInfoEnabled())
					logger.info("smsCampaign is=====>" + smsCampaign);
				byte msgOverSizeOption = smsCampaign.getMessageSizeOption();
				String senderId = smsCampaign.getSenderId();

				int stIndex = 0;
				int size = 100;
				String response = "";// not required
				int tempCount = 0;// required to update the smscount
				List<Contacts> retList;// holds the contacts based on the
										// specified size
				List<ComponentsAndContacts> retCompContList = null;// hold the
																	// componentsAndcontacts(for
																	// the
																	// contacts
																	// which are
																	// already
																	// exist)
				List<ComponentsAndContacts> listToBeUpdated = new ArrayList<ComponentsAndContacts>();// used
																										// for
																										// updating
																										// the
																										// compsANDcontacts
				String sendingMsg;
				boolean limit = false;// it will be used to specify the
										// NetCoreApi tht it has reach the limit
				SMSCampaignReport smsCampaignReport = null;
				SMSCampaignSent smsCampaignSent = null;
				String sentId = "";
				ComponentsAndContacts componentsAndContacts = null;
				List<ComponentsAndContacts> contactIds = new ArrayList<ComponentsAndContacts>();// not
																								// required
				List<Long> contctIds = new ArrayList<Long>();
				String path = "";// not required
				// int msgSequence = 0;
				// List<SMSCampaignSent> smsCampSentList = new
				// ArrayList<SMSCampaignSent>();
				Calendar cal = Calendar.getInstance();

				smsCampaignReport = new SMSCampaignReport(
						smsCampaign.getUsers(), autoProgram.getProgramName(),
						smsCampaign.getMessageContent(), cal, 0, 0, 0, 0, 0,
						Constants.CR_STATUS_SENDING, fromSource);
				try {
					//smsCampaignReportDao.saveOrUpdate(smsCampaignReport);
					smsCampaignReportDaoForDML.saveOrUpdate(smsCampaignReport);
					SMSCampReportLists smsCampReportLists = new SMSCampReportLists(
							smsCampaignReport.getSmsCrId());
					smsCampReportLists.setListsName(listsName);
					//smsCampReportListsDao.saveOrUpdate(smsCampReportLists);
					smsCampReportListsDaoForDML.saveOrUpdate(smsCampReportLists);

				} catch (Exception e) {
					logger.error(
							"** Exception while saving the SMSCampaignReport object",
							e);

				}

				/*
				 * if(logger.isInfoEnabled())
				 * logger.info("------ready to send sms-------");
				 * 
				 * //get the contacts data from the contacts based on the
				 * contacts present in temp_activity_data table query =
				 * "  from Contacts c where c.contactId in" +
				 * " (select distinct ta.contactId from TempActivityData ta where ta.label in('"
				 * + label+"') and ta.programId=" +
				 * autoProgram.getProgramId()+" and ta.componentId=" +
				 * autoProgramComponents
				 * .getCompId()+")  and c.phone is not null";
				 * 
				 * 
				 * String query = "SELECT * FROM sms_tempcontacts";
				 * 
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("fetching contacts*********"); String
				 * quryforupdateCompCont = ""; String cids;
				 * 
				 * do{
				 *//**
				 * need to follow the below steps 1.get the contacts from
				 * contacts which are present in temp_activity_data for
				 * specified size(retList). 2.prepare a list which hold the
				 * contactIds(contctIds).for each contact in the above list add
				 * the contact id to 'contctIds' list and prepare a contact ids
				 * String 'cids'. 3.for the above contact ids(cids) get the list
				 * of objects from components_contacts(retCompContList) 4.for
				 * each compAndConatc object of the above list,remove the
				 * contactid from the list got in 2nd step. (this steps lets the
				 * contctIds list only should contain the new contacts). 5.for
				 * each contact got in got in 1st step perform SMS sending
				 * operation. 6.for each contact left in the list got from 4th
				 * step create new entry in components_contacts. and add this
				 * object to the list got in 3rd step.(this step lets the
				 * 'retCompContList' should remains with the object which are
				 * needed to be update) 7.for each obj of the list got from the
				 * 6th step update 'path','compId','compWinId', 'activitydate'
				 * through the bean's setter methods.
				 * 
				 * 8.update the final list got in the 7th step by save by
				 * collection. 9.delete the entries in the temp_activity_data
				 * table.
				 * 
				 * known problem is there(here if something happend due to which
				 * SMS unable to send we should delete the SMSCampaignReport
				 * object associated with the current sending)
				 */
				/*
				 * 
				 * 
				 * 
				 * 
				 * 
				 * cids = ""; //contactIds = new Vector<String>();
				 * //listToBeUpdated = new ArrayList<ComponentsAndContacts>();
				 *//*********************** STEP:1 ******************************************/
				/*
				 * retList = contactsDao.getSegmentedContacts(query, stIndex,
				 * size);
				 * 
				 * if(retList.size() > 0 ) {
				 *//************************************ STEP:2 **************************************/
				/*
				 * 
				 * for (Contacts contact : retList) {
				 * 
				 * if( (contact.getMobilePhone()).length() < 10 ) {
				 * 
				 * if(logger.isErrorEnabled()) logger.error(
				 * "ERROR got the contact with mobile number less than 10 digits....."
				 * ); continue;
				 * 
				 * 
				 * }
				 * 
				 * 
				 * if(cids.length()>0) cids += ","; cids +=
				 * contact.getContactId();
				 * 
				 * contctIds.add(contact.getContactId()); }//for
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("the contact ids are====>"+cids);
				 * 
				 * //examine which contacts are present in components_contacts
				 * and which are not(new for this program) if(cids.length() > 0)
				 * {
				 * 
				 * 
				 * 
				 * quryforupdateCompCont =
				 * "from ComponentsAndContacts cc where cc.contactId in("
				 * +cids+") and programId="+ autoProgram.getProgramId();
				 * 
				 * if(logger.isInfoEnabled()) logger.info(
				 * "the query to be executed to get the contacts from components_contacts...."
				 * +quryforupdateCompCont);
				 *//**************************************** STEP:3 *********************************************/
				/*
				 * 
				 * retCompContList =
				 * componentsAndContactsDao.executeQuery(quryforupdateCompCont,
				 * stIndex, size);
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("the number of contacts for this program are====>"
				 * +retCompContList.size());
				 *//***************************************** STEP:4 *****************************************/
				/*
				 * 
				 * for (ComponentsAndContacts componentsAndContact :
				 * retCompContList) {
				 * 
				 * componentsAndContact.setActivityDate(smsCampaignReport.
				 * getSentDate()); listToBeUpdated.add(componentsAndContact);
				 * //this was there earlier
				 * 
				 * if(contactIds.contains(componentsAndContact.getContactId()))
				 * continue;
				 * 
				 * contactIds.add(componentsAndContact);
				 * 
				 * if(contctIds.contains(componentsAndContact.getContactId()) )
				 * {//filters the new contacts from existing
				 * 
				 * contctIds.remove(componentsAndContact.getContactId());
				 * 
				 * }
				 * 
				 * 
				 * }//for
				 * 
				 * } }//if
				 *//*************************************** STEP:5 ***********************************************/
				/*
				 * 
				 * int tempMsgType = smsCampaign.getMessageType();// == 1) ? "N"
				 * : "L";
				 * 
				 * limit = (retList.size() == size);//to let the api opens the
				 * connection only for 100 contacts StringBuffer contactPhnSb =
				 * new StringBuffer();
				 *//****** to send an sms to each contact ******/
				/*
				 * for (Contacts contact : retList) {
				 * 
				 * 
				 * //msgSequence++; smsCampaignSent = new
				 * SMSCampaignSent(contact.getMobilePhone(), smsCampaignReport,
				 * Constants.CS_STATUS_SUCCESS,contact.getContactId());
				 * smsCampaignSentDao.saveOrUpdate(smsCampaignSent); sentId =
				 * ""+smsCampaignSent.getSentId(); int lengthOfSentId =
				 * sentId.length(); for(int j=lengthOfSentId; j<8; j++){
				 * 
				 * sentId = "0"+sentId; }
				 * 
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("sent id is=====>"+sentId);
				 * if(logger.isInfoEnabled())
				 * logger.info(contact.getMobilePhone(
				 * )+"  , "+contact.getEmailId()); if(
				 * (contact.getMobilePhone()).length() < 10 ) {
				 * 
				 * if(logger.isErrorEnabled()) logger.error(
				 * "ERROR got the contact with mobile number less than 10 digits....."
				 * ); continue;
				 * 
				 * 
				 * }
				 *//**
				 * if SMS contains place holders we submit individually
				 */
				/*
				 * if(placeHolders) { // single sending if(msgOverSizeOption ==
				 * 1) { // split and send as multiple messages for (int i = 0; i
				 * < msgContentLst.size(); i++) { tempCount++; sendingMsg =
				 * replacePlaceHolders(msgContentLst.get(i), contact); String
				 * multiPartSentId = 1+sentId; if(msgContentLst.size()>1) {
				 * sendingMsg = "("+ (i+1) +"/"+ msgContentLst.size() +") "
				 * +sendingMsg;
				 * 
				 * multiPartSentId = (i+1)+sentId; } if(logger.isInfoEnabled())
				 * logger.info("msgContent--->"+sendingMsg); response =
				 * SMSCountryApi.sendSingleSMS(""+contact.getPhone(),
				 * sendingMsg, smsCampaign.getSenderId(), tempMsgType);
				 * 
				 * captiwayToSMSApiGateway.sendToSMSApi(Constants.
				 * USER_SMSTOOL_CLICKATELL, msgContent, ""+contact.getPhone(),
				 * tempMsgType, sentId, "9848495956", ""+contact.getPhone(),
				 * "1", smsCampaignReport.getSmsCrId(),limit,retList.size(),
				 * senderId);
				 * 
				 * 
				 * captiwayToSMSApiGateway.sendToSMSApi(Constants.
				 * USER_SMSTOOL_CLICKATELL, sendingMsg,
				 * contact.getMobilePhone(), tempMsgType, multiPartSentId,
				 * "9848495956", contact.getMobilePhone(), "1",
				 * smsCampaignReport
				 * .getSmsCrId(),limit,retList.size(),senderId);
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("sms sent to-----> "+contact.getFirstName());
				 * 
				 * } // for each }else if(msgOverSizeOption == 2) { // truncate
				 * and send single msg tempCount++; sendingMsg =
				 * replacePlaceHolders(msgContentLst.get(0), contact);
				 * 
				 * 
				 * response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(),
				 * sendingMsg, smsCampaign.getSenderId(), tempMsgType);
				 * 
				 * captiwayToSMSApiGateway.sendToSMSApi(Constants.
				 * USER_SMSTOOL_CLICKATELL, sendingMsg,
				 * contact.getMobilePhone(), tempMsgType, 1+sentId,
				 * "9848495956", contact.getMobilePhone(), "1",
				 * smsCampaignReport.getSmsCrId(),limit,retList.size(),
				 * senderId);
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("sms sent to-----> "+contact.getFirstName());
				 * 
				 * }else if(msgOverSizeOption == 4) { // do not send the sms if
				 * it is too long if(msgContentLst.size()>1){ Messages messages
				 * = new Messages("SMS Campaign","SMS is ignored",
				 * "SMS size option is-do not " + "sending SMS, " +
				 * "the SMS is ignored from sending.",
				 * Calendar.getInstance(),"Inbox",false, "info",
				 * smsCampaign.getUsers()); messagesDao.saveOrUpdate(messages);
				 * }else{ tempCount++; sendingMsg =
				 * replacePlaceHolders(msgContentLst.get(0), contact);
				 * 
				 * response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(),
				 * sendingMsg, smsCampaign.getSenderId(), tempMsgType);
				 * 
				 * captiwayToSMSApiGateway.sendToSMSApi(Constants.
				 * USER_SMSTOOL_CLICKATELL, sendingMsg,
				 * contact.getMobilePhone(), tempMsgType, 1+sentId,
				 * "9848495956", contact.getMobilePhone(), "1",
				 * smsCampaignReport
				 * .getSmsCrId(),limit,retList.size(),senderId);
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("sms sent to-----> "+contact.getFirstName());
				 * 
				 * } }else if(msgOverSizeOption == 8){ // send as a long message
				 * tempCount += msgContentLst.size() ; String msgContentToBeSent
				 * = replacePlaceHolders(msgContent, contact);
				 * 
				 * response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(),
				 * msgContent, smsCampaign.getSenderId(), tempMsgType);
				 * 
				 * captiwayToSMSApiGateway.sendToSMSApi(Constants.
				 * USER_SMSTOOL_CLICKATELL, msgContentToBeSent,
				 * contact.getMobilePhone(), tempMsgType, 1+sentId,
				 * "9848495956", contact.getMobilePhone(), "1",
				 * smsCampaignReport
				 * .getSmsCrId(),limit,retList.size(),senderId);
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("sms sent to-----> "+contact.getFirstName());
				 * 
				 * }//else if
				 * 
				 * success = true; } else { // else follow group submission
				 * 
				 * if(msgOverSizeOption == 1){ // split and send as multiple
				 * messages for (int i = 0; i < msgContentLst.size(); i++) {
				 * tempCount++; sendingMsg = msgContentLst.get(i); String
				 * multiPartSentId = 1+sentId; if(msgContentLst.size()>1) {
				 * sendingMsg = "("+ (i+1) +"/"+ msgContentLst.size() +") "
				 * +sendingMsg; multiPartSentId = (i+1)+sentId; }
				 * 
				 * response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(),
				 * sendingMsg, smsCampaign.getSenderId(), tempMsgType);
				 * 
				 * captiwayToSMSApiGateway.sendToSMSApi(Constants.
				 * USER_SMSTOOL_CLICKATELL, sendingMsg,
				 * contact.getMobilePhone(), tempMsgType, multiPartSentId,
				 * "9848495956",contact.getMobilePhone(), "1",
				 * smsCampaignReport.
				 * getSmsCrId(),limit,retList.size(),senderId);
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("sms sent to-----> "+contact.getFirstName());
				 * 
				 * } // for each }else if(msgOverSizeOption == 2) { // truncate
				 * and send single msg
				 * 
				 * tempCount++; sendingMsg = msgContentLst.get(0);
				 * 
				 * response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(),
				 * sendingMsg, smsCampaign.getSenderId(), tempMsgType);
				 * 
				 * captiwayToSMSApiGateway.sendToSMSApi(Constants.
				 * USER_SMSTOOL_CLICKATELL, sendingMsg,
				 * contact.getMobilePhone(), tempMsgType, 1+sentId,
				 * "9848495956", contact.getMobilePhone(), "1",
				 * smsCampaignReport
				 * .getSmsCrId(),limit,retList.size(),senderId);
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("sms sent to-----> "+contact.getFirstName());
				 * 
				 * 
				 * }else if(msgOverSizeOption == 4) { // do not send the sms if
				 * it is too long if(msgContentLst.size()>1){ Messages messages
				 * = new Messages("SMS Campaign","SMS is ignored",
				 * "SMS size option is-do not " + "sending SMS, " +
				 * "the SMS is ignored from sending."
				 * ,Calendar.getInstance(),"Inbox",false, "info",
				 * smsCampaign.getUsers()); messagesDao.saveOrUpdate(messages);
				 * }else{ tempCount++; sendingMsg = msgContentLst.get(0);
				 * 
				 * response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(),
				 * sendingMsg, smsCampaign.getSenderId(), tempMsgType);
				 * 
				 * captiwayToSMSApiGateway.sendToSMSApi(Constants.
				 * USER_SMSTOOL_CLICKATELL, sendingMsg,
				 * contact.getMobilePhone(), tempMsgType, 1+sentId,
				 * "9848495956", contact.getMobilePhone(), "1",
				 * smsCampaignReport
				 * .getSmsCrId(),limit,retList.size(),senderId);
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("sms sent to-----> "+contact.getFirstName());
				 * 
				 * }//else }else if(msgOverSizeOption == 8){ // send as a long
				 * msg tempCount += msgContentLst.size() ;
				 * 
				 * response = SMSCountryApi.sendSingleSMS(""+contact.getPhone(),
				 * msgContent, smsCampaign.getSenderId(), tempMsgType);
				 * 
				 * captiwayToSMSApiGateway.sendToSMSApi(Constants.
				 * USER_SMSTOOL_CLICKATELL, msgContent,contact.getMobilePhone(),
				 * tempMsgType, 1+sentId, "9848495956",
				 * contact.getMobilePhone(), "1",
				 * smsCampaignReport.getSmsCrId(),
				 * limit,retList.size(),senderId);
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("sms sent to-----> "+contact.getFirstName());
				 * 
				 * }//else if success = true; } // else
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("****************SMS sent suscessfully***********"
				 * );
				 * 
				 * 
				 * if(contactPhnSb.length() > 0) { contactPhnSb =
				 * contactPhnSb.append(","); } contactPhnSb =
				 * contactPhnSb.append(contact.getMobilePhone());
				 * 
				 * } // for each contact
				 *//******************************** STEP:6 ********************************/
				/*
				 * 
				 * 
				 * for (Long cid : contctIds) {//it now consists the new
				 * contacts which are not present in components_contacts
				 * 
				 * componentsAndContacts = new
				 * ComponentsAndContacts(autoProgram.getProgramId(),
				 * autoProgram.getUser().getUserId(), cid,
				 * autoProgramComponents.getCompId(),
				 * autoProgramComponents.getStage(),
				 * autoProgramComponents.getComponentWinId(),"",
				 * smsCampaignReport.getSentDate());
				 * listToBeUpdated.add(componentsAndContacts);
				 * 
				 * }
				 * 
				 * if(logger.isInfoEnabled())
				 * logger.info("the size of components list is=====>"
				 * +listToBeUpdated.size());
				 *//******************************* STEP:7 **************************************/
				/*
				 * 
				 * for(ComponentsAndContacts compTobeUpdate : retCompContList) {
				 * 
				 * compTobeUpdate.setActivityDate(cal);
				 * compTobeUpdate.setComponentId
				 * (autoProgramComponents.getCompId());
				 * compTobeUpdate.setComponentWinId
				 * (autoProgramComponents.getComponentWinId());
				 * compTobeUpdate.setPath
				 * (compTobeUpdate.getPath()+","+autoProgramComponents
				 * .getComponentWinId()); listToBeUpdated.add(compTobeUpdate);
				 * 
				 * }
				 *//********************************** STEP:8 **********************************/
				/*
				 * componentsAndContactsDao.saveByCollection(listToBeUpdated);
				 *//**
				 * select contact_id from temp_activity_data where
				 * component_id=160 and contact_id in(select c.cid from contacts
				 * c where c.list_id=730 and c.phone
				 * in(9490927928,7893737674,9848495956,9490918537,9908266873)))
				 */
				/*
				 * 
				 * //logger.info(
				 * "my phone number for which i have to search for the contacts are======>"
				 * +contactPhnSb.toString());
				 * if(contactPhnSb.toString().length() > 0) {
				 * 
				 * String updatesubqueryQuery =
				 * "SELECT contact_id FROM temp_activity_data WHERE component_id="
				 * + autoProgramComponents.getCompId()+
				 * " AND contact_id in(SELECT cid " +
				 * "FROM contacts WHERE list_id in("
				 * +mlStr+") AND phone in("+contactPhnSb.toString()+"))";
				 * 
				 * String cidsTobeUpdated =
				 * tempComponentsDataDao.getContactIdsStr(updatesubqueryQuery);
				 * //logger.info("my contacts to be updates are=====>"+
				 * cidsTobeUpdated);
				 * 
				 * 
				 * 
				 * if(cidsTobeUpdated.length() > 0) {
				 * 
				 * componentsAndContactsDao.updateActivityDateAndPath(Calendar.
				 * getInstance(), autoProgram.getProgramId(),
				 * autoProgramComponents.getComponentWinId(),
				 * autoProgramComponents.getCompId(), cidsTobeUpdated);
				 * 
				 * 
				 * List<ProgramOnlineReports> progOnlineRepList = new
				 * ArrayList<ProgramOnlineReports>();
				 * 
				 * 
				 * 
				 * List<ComponentsAndContacts> tempList =
				 * componentsAndContactsDao.getByContactIds(cidsTobeUpdated,
				 * autoProgram.getProgramId());
				 * 
				 * if(tempList != null && tempList.size() > 0 ) {
				 * 
				 * for (ComponentsAndContacts compContactsId : tempList) {
				 * 
				 * ProgramOnlineReports programOnlineReports = new
				 * ProgramOnlineReports(compContactsId,
				 * autoProgramComponents.getComponentWinId(), cal.getTime(),
				 * autoProgram.getProgramId(),
				 * autoProgramComponents.getCompId(),
				 * compContactsId.getContactId());
				 * 
				 * progOnlineRepList.add(programOnlineReports);
				 * 
				 * 
				 * 
				 * }//for
				 * 
				 * programOnlineReportsDao.saveByCollection(progOnlineRepList);
				 * 
				 * 
				 * }//if
				 * 
				 * }//if }//if
				 * 
				 * 
				 * contactIds.clear(); listToBeUpdated.clear();
				 * 
				 * contctIds.clear(); contactPhnSb.setLength(0); stIndex +=
				 * size;
				 * 
				 * } while(retList.size() == size);
				 */// update the activity date for the contacts present in
					// components_contacts for
					// this program(no matter those are newly added or the
					// existing record!)

				// String qry =
				// "UPDATE components_contacts SET activity_date='"+cal.toString()+"' WHERE program_id="+autoProgram.getProgramId();

				/*
				 * int updatedCount =
				 * componentsAndContactsDao.updateActivityDateAndPath(cal,
				 * autoProgram
				 * .getProgramId(),autoProgramComponents.getComponentWinId(),
				 * autoProgramComponents.getCompId());
				 */

				// delete the entries for this activity from temp_activity_data
				// table;
				// qry = "DELETE FROM temp_activity_data WHERE ";

				/*********************************** STEP:9 ******************************************/
				tempActivityDataDaoForDML.deleteTempActivityContactsData(label,
						autoProgramComponents.getCompId());

				if (!useMQS) {
					//usersDao.updateUsedSMSCount(smsCampaign.getUsers().getUserId(), tempCount);
					usersDaoForDML.updateUsedSMSCount(smsCampaign.getUsers()
							.getUserId(), tempCount);
				}

				smsCampaignReport.setStatus(Constants.CR_STATUS_SENT);
				smsCampaignReport.setSent(smsCampaignSentDao
						.getSentCount(smsCampaignReport.getSmsCrId()));
				//smsCampaignReportDao.saveOrUpdate(smsCampaignReport);
				smsCampaignReportDaoForDML.saveOrUpdate(smsCampaignReport);

				// TODO needs to clarify whether need to delete the data from
				// temp table or not
			}// if

		} catch (Exception e) {
			logger.error("Exception while sending Program based smscampaign", e);

		}
	}// sendProgramSMSCampaign

	private Set<String> totalPhSet;

	public Set<String> getTotalPhSet() {
		return totalPhSet;
	}

	private Set<SMSCampaignUrls> smsCampUrlList;

	public Set<SMSCampaignUrls> getSmsCampUrlList() {
		return smsCampUrlList;
	}

	private Set<String> urlSet;

	public Set<String> getUrlSet() {
		return urlSet;
	}

	private String getCustomFields(String content) {
		// logger.debug("+++++++ Just Entered +++++"+ content);
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern, Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		totalPhSet = new HashSet<String>();
		String cfNameListStr = "";

		try {
			while (m.find()) {

				ph = m.group(1); // .toUpperCase()
				if (logger.isInfoEnabled())
					logger.info("Ph holder :" + ph);

				if (ph.startsWith("GEN_")) {
					totalPhSet.add(ph);
				}
				if (ph.startsWith(Constants.UDF_TOKEN)) {
					totalPhSet.add(ph);
				} else if (ph.startsWith("CC_")) {
					totalPhSet.add(ph);
				} else if (ph.startsWith("CF_")) {
					totalPhSet.add(ph);
					cfNameListStr = cfNameListStr
							+ (cfNameListStr.equals("") ? ""
									: Constants.DELIMETER_COMMA) + "'"
							+ ph.substring(3) + "'";
				} // if(ph.startsWith("CF_"))
			} // while

			if (logger.isDebugEnabled())
				logger.debug("+++ Exiting : " + totalPhSet);
		} catch (Exception e) {
			logger.error("Exception while getting the place holders ", e);
		}

		if (logger.isInfoEnabled())
			logger.info("CF PH cfNameListStr :" + cfNameListStr);

		return cfNameListStr;
	}

	public boolean checkForExpiredKeyWords(String messageContent, Users user,
			String smsCampaignName, Long orgId, boolean triggerMsg)
			throws Exception {
		boolean isFound = false;
		OrgSMSkeywordsDao orgSMSkeywordsDao = (OrgSMSkeywordsDao) ServiceLocator
				.getInstance().getDAOByName(OCConstants.ORGSMSKEYWORDS_DAO);
		List<OrgSMSkeywords> retKeyword = orgSMSkeywordsDao
				.findExpKeywordBy(orgId);
		// if(retKeyword == null) return false;

		// for expired keywords if any
		String keywords = Constants.STRING_NILL;
		if (retKeyword != null) {
			for (OrgSMSkeywords OrgSMSkeyword : retKeyword) {

				String shortCode = OrgSMSkeyword.getShortCode();
				boolean isPlus = (shortCode.length() > 10);
				// \\bkeyword\\b\\s+\\bto\\b\\s+[^\\s\\w]*\\b919490927928\\b
				String pattern = "\\b" + OrgSMSkeyword.getKeyword()
						+ "\\b\\s+\\bto\\b\\s+" + (isPlus ? "[^\\s\\w]*" : "")
						+ "\\b" + OrgSMSkeyword.getShortCode();
				int options = 0;
				options |= 128; // This option is for Case insensitive
				options |= 32;
				Pattern p = Pattern.compile(pattern, options);
				Matcher m = p.matcher(messageContent);

				while (m.find()) {

					isFound = true;
				}

				if (isFound) {
					if (!keywords.isEmpty())
						keywords += Constants.DELIMETER_COMMA + " ";

					keywords += "'" + OrgSMSkeyword.getKeyword() + "'";

				}

			}
		}
		// for pending keywords if any
		retKeyword = orgSMSkeywordsDao.findNonActiveKeywordBy(orgId);
		String pendingKeywords = Constants.STRING_NILL;
		isFound = false;
		if (retKeyword != null) {

			for (OrgSMSkeywords OrgSMSkeyword : retKeyword) {
				String shortCode = OrgSMSkeyword.getShortCode();
				boolean isPlus = (shortCode.length() > 10);
				String pattern = "\\b" + OrgSMSkeyword.getKeyword()
						+ "\\b\\s+\\bto\\b\\s+" + (isPlus ? "[^\\s\\w]*" : "")
						+ "\\b" + OrgSMSkeyword.getShortCode();// OrgSMSkeyword.getKeyword()+" to "+(isPlus
																// ? "+" : ""
																// )+OrgSMSkeyword.getShortCode();
				int options = 0;
				options |= 128; // This option is for Case insensitive
				options |= 32;
				Pattern p = Pattern.compile(pattern, options);
				Matcher m = p.matcher(messageContent);

				while (m.find()) {

					isFound = true;
				}

				if (isFound) {
					if (!pendingKeywords.isEmpty())
						pendingKeywords += Constants.DELIMETER_COMMA + " ";

					pendingKeywords += "'" + OrgSMSkeyword.getKeyword() + "'";

				}

			}

		}

		boolean isExist = false;
		String msgStr = Constants.STRING_NILL;
		String module = Constants.STRING_NILL;
		String sub = Constants.STRING_NILL;

		if (!keywords.isEmpty() || !pendingKeywords.isEmpty()) {

			String addStr = Constants.STRING_NILL;
			if (!keywords.isEmpty()) {

				addStr += "as it consists expired keywords " + keywords;
			} else if (!pendingKeywords.isEmpty()) {

				addStr += " and Inactive keywords " + pendingKeywords;

			}

			if (triggerMsg) {

				module = "Event trigger SMS campaign sending failed ";
				msgStr = " SMS associated to trigger '" + smsCampaignName
						+ "' couldn't be sent," + addStr;
				sub = "Send Event trigger SMS Campaign";

			} else {

				module = "Send SMS Campaign";
				msgStr = "'" + smsCampaignName + "' couldn't be sent, "
						+ addStr;
				sub = "SMS campaign sending failed ";

			}

			isExist = true;
		}

		if (isExist == true) {

			Messages msgs = new Messages(module, sub, msgStr,
					Calendar.getInstance(), "Inbox", false, "Info", user);

			//messagesDao.saveOrUpdate(msgs);
			messagesDaoForDML.saveOrUpdate(msgs);

			return isExist;

		}// if
		return false;
	}

	private Set<String> getDateFields(String content) {

		content = content.replace("|^", "[").replace("^|", "]");

		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern, Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String> dateFieldsSet = new HashSet<String>();

		try {
			while (m.find()) {

				ph = m.group(1); // .toUpperCase()
				if (logger.isInfoEnabled())
					logger.info("Ph holder :" + ph);

				if (ph.startsWith(Constants.DATE_PH_DATE_)) {
					dateFieldsSet.add(ph);
				}

			} // while

			// logger.debug("+++ Exiting : "+ totalPhSet);
		} catch (Exception e) {
			logger.error("Exception while getting the symbol place holders ", e);
		}

		if (logger.isInfoEnabled())
			logger.info("symbol PH  Set : " + dateFieldsSet);

		return dateFieldsSet;
	}

	/**
	 * Get the URLs
	 * 
	 * @param htmlContent
	 * @return Set of URL strings
	 */
	private Object[] getUrls(String smsMsgContent, Long userId) {

		int options = 0;
		options |= 128; // This option is for Case insensitive
		options |= 32;

		Object[] retObjArr = new Object[3];

		urlSet = new HashSet<String>();
		smsCampUrlList = new HashSet<SMSCampaignUrls>();

		if (smsMsgContent == null)
			return null;

		try {
			String appshortUrl = PropertyUtil
					.getPropertyValue(Constants.APP_SHORTNER_URL);
			Pattern p = Pattern.compile(
					PropertyUtil.getPropertyValue(OCConstants.SMS_URL_PATTERN),
					options);
			Matcher m = p.matcher(smsMsgContent);
			String linkUrl = null;
			UrlShortCodeMappingDao urlShortCodeMappingDao = null;
			try {
				urlShortCodeMappingDao = (UrlShortCodeMappingDao) ServiceLocator
						.getInstance().getDAOByName(
								OCConstants.URLSHORTCODEMAPPING_DAO);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("No bean found with given id");
				return null;
			}
			Map<String, String> urlMap = new HashMap<String, String>();
			String shortCodeStr = Constants.STRING_NILL;
			while (m.find()) {

				linkUrl = m.group().trim();
				/*
				 * if(!(linkUrl.toLowerCase().startsWith(appshortUrl))) {
				 * 
				 * String mappingUrl =
				 * userId.longValue()+"|"+System.currentTimeMillis
				 * ()+"|"+linkUrl; //String insetedUrl =
				 * PropertyUtil.getPropertyValue("ApplicationShortUrl");
				 * 
				 * List<StringBuffer> retList =
				 * Utility.getSixDigitURLCode(mappingUrl); UrlShortCodeMapping
				 * urlShortCodeMapping = null; if(retList != null &&
				 * retList.size() > 0) {
				 * 
				 * //check whether any returned shordcode exists in DB
				 * 
				 * for (StringBuffer shortCode : retList) {
				 * 
				 * String urlShortCode = "U"+shortCode; urlShortCodeMapping =
				 * new UrlShortCodeMapping(urlShortCode, linkUrl, null, userId);
				 * 
				 * try {
				 * 
				 * urlShortCodeMappingDao.saveOrUpdate(urlShortCodeMapping);
				 * 
				 * String shortUrl = appshortUrl+urlShortCode;
				 * 
				 * smsMsgContent = smsMsgContent.replace(linkUrl, shortUrl);
				 * logger.debug("in urlset the content is=====>"+smsMsgContent);
				 * 
				 * urlSet.add(shortUrl); urlMap.put(shortUrl, linkUrl); break;
				 * }catch (DataIntegrityViolationException e) { // TODO: handle
				 * exception
				 * logger.error("given Short code is already exist in DB....."
				 * ,e); continue;
				 * 
				 * }catch (ConstraintViolationException e) { // TODO: handle
				 * exception
				 * logger.error("given Short code is already exist in DB....."
				 * ,e); continue; }
				 * 
				 * 
				 * 
				 * }//for
				 * 
				 * }//if } else
				 */if ((linkUrl.toLowerCase().startsWith(appshortUrl))) {
					// may be a short url contains the shortcode/coupon
					urlSet.add(linkUrl);

					String code = linkUrl.split(appshortUrl)[1];
					if (code.startsWith(OCConstants.SHORTURL_CODE_PREFIX_U)) {

						if (!shortCodeStr.isEmpty())
							shortCodeStr += Constants.DELIMETER_COMMA;
						shortCodeStr += "'" + code + "'";

					} else if (code
							.startsWith(OCConstants.SHORTURL_CODE_PREFIX_COUPONPH)) {

						urlMap.put(linkUrl, linkUrl);
					}
				}

			} // while
			if (!shortCodeStr.isEmpty()) {

				List<UrlShortCodeMapping> retList = urlShortCodeMappingDao
						.findBy(userId, shortCodeStr);
				if (retList != null) {// to know the actual url of all the
										// shorten urls

					for (UrlShortCodeMapping urlShortCodeMapping : retList) {

						if (urlMap.containsKey(appshortUrl
								+ urlShortCodeMapping.getShortCode()))
							continue;

						urlMap.put(
								appshortUrl
										+ urlShortCodeMapping.getShortCode(),
								urlShortCodeMapping.getUrlContent());

					}

				}

			}

			retObjArr[0] = urlSet;
			retObjArr[1] = urlMap;
			retObjArr[2] = smsMsgContent;
		} catch (Exception e) {
			logger.error("** Exception : Problem while getting the URL set ", e);
		}
		return retObjArr;

	} // getUrls

	/**
	 * this method splits the sms if its exceeds the specified length
	 * 
	 * @param msgContent
	 * @param size
	 * @return
	 */
	private ArrayList<String> splitSMSMessage(String msgContent, int size) {

		try {
			ArrayList<String> retList = new ArrayList<String>();// to store the
																// message
																// tokens

			int skipCounter = 0;// to indicate the max number of message tokens

			do {
				skipCounter++;

				if (msgContent.length() > size - 10) {

					int endInd = msgContent.indexOf(' ', size - 10);

					if (endInd == -1 || endInd > size - 5) {
						endInd = msgContent.lastIndexOf(' ', size - 10);
					}

					if (endInd == -1) {
						if (logger.isInfoEnabled())
							logger.info("No Spaces in the Given Token");
						break;
					}

					/*
					 * While splitting, if it is inside the Place holder then
					 * find out for a new space before/after from that position
					 * for split
					 */

					if (msgContent.lastIndexOf("|^", endInd) != -1
							&& msgContent.indexOf("^|", endInd) != -1
							&& (endInd > msgContent.lastIndexOf("|^", endInd))
							&& (endInd < msgContent.indexOf("^|", endInd))) {

						int phStInd = msgContent.lastIndexOf("|^", endInd);
						int phEnInd = msgContent.indexOf("^|", endInd);

						int tempEndInd = endInd;

						if (msgContent.substring(phStInd + 2, endInd).indexOf(
								"^|") == -1) {
							endInd = msgContent.lastIndexOf(' ', phStInd);
						}

						if (endInd == -1
								&& (msgContent.substring(tempEndInd, phEnInd)
										.indexOf("|^") == -1)) {
							endInd = msgContent.indexOf(' ', phEnInd + 1);
						}
						if (endInd == -1) {
							break;
						}

					} // if

					String tempStr = msgContent.substring(0, endInd);
					retList.add(tempStr);

					msgContent = msgContent.substring(endInd + 1);

				} // if

				if (skipCounter > 40) {
					break;
				}
			} while (msgContent.length() > size - 10);

			if (msgContent.length() > 0) {
				retList.add(msgContent);
			}

			return retList;

		} catch (Exception e) {
			logger.error("** Error occured while submitting the SMS campaigns",
					e);
			return null;
		}

	} // splitSMSMessage

	private static final String plArr[] = { "|^GEN_firstName^|",
			"|^GEN_lastName^|", "|^GEN_phone^|", "|^GEN_emailId^|",
			"|^GEN_addressOne^|", "|^GEN_addressTwo^|", "|^GEN_city^|",
			"|^GEN_state^|", "|^GEN_country^|", "|^GEN_pin^|" };

	/**
	 * this method replaces the placeholder value with the contact value
	 * 
	 * @param orgMsg
	 * @param contact
	 * @return
	 */
	private String replacePlaceHolders(String orgMsg, Contacts contact) {

		String retMsg = orgMsg;

		for (String ph : plArr) {

			String cfStr = ph.substring(ph.indexOf('^') + 1,
					ph.lastIndexOf('^'));
			String value;

			if (cfStr.startsWith("GEN_")) {

				cfStr = cfStr.substring(4);
				if (cfStr.equalsIgnoreCase("emailId"))
					value = contact.getEmailId();
				else if (cfStr.equalsIgnoreCase("firstName"))
					value = contact.getFirstName();
				else if (cfStr.equalsIgnoreCase("lastName"))
					value = contact.getLastName();
				else if (cfStr.equalsIgnoreCase("addressOne"))
					value = contact.getAddressOne();
				else if (cfStr.equalsIgnoreCase("addressTwo"))
					value = contact.getAddressTwo();
				else if (cfStr.equalsIgnoreCase("city"))
					value = contact.getCity();
				else if (cfStr.equalsIgnoreCase("state"))
					value = contact.getState();
				else if (cfStr.equalsIgnoreCase("country"))
					value = contact.getCountry();
				else if (cfStr.equalsIgnoreCase("pin"))
					value = contact.getZip();
				else if (cfStr.equalsIgnoreCase("phone"))
					value = contact.getMobilePhone();
				else if (cfStr.equalsIgnoreCase("zip"))
					value = contact.getZip();
				else if (cfStr.equalsIgnoreCase("mobile"))
					value = contact.getMobilePhone();
				else
					value = "";

				if (logger.isInfoEnabled())
					logger.info("Gen token :" + cfStr + " - Value :" + value);
				try {
					if (value == null || value.trim().length() == 0) {
						retMsg = retMsg.replace(ph, "");
					} else if (value != null && value.trim().length() > 0) {

						retMsg = retMsg.replace(ph, value);
					}

				} catch (Exception e) {
					logger.error("Exception ::::", e);
				}
			} // if(cfStr.startsWith("GEN_"))

		} // for

		return retMsg;
	} // replacePlaceHolders

	private void examineStorePH(Long orgId) {

		for (String cfStr : totalPhSet) {

			if (OrgStoreMap != null && OrgStoreAddressMap != null)
				break;// this helps only when these two type of placeholders
						// exists

			if (!cfStr.startsWith("GEN_"))
				continue;// not to consider other place holders

			cfStr = cfStr.substring(4);

			if (cfStr.toLowerCase().startsWith(
					PlaceHolders.CAMPAIGN_PH_STARTSWITH_STORE)
					|| (cfStr.toLowerCase().startsWith(
							PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE
									.toLowerCase()) && !cfStr
							.startsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS))) {

				if (OrgStoreMap == null) { // to get one-time parameter values

					// OrgStoreMap = new HashMap<String, OrganizationStores>();

					OrgStoreMap = Utility.retOrgStoreMap(orgId,
							organizationStoresDao);
				}// if

				/*
				 * for (OrganizationStores organizationStores : listOfStores) {
				 * 
				 * OrgStoreMap.put(organizationStores.getHomeStoreId(),
				 * organizationStores);
				 * 
				 * }//for
				 */
			}// if

			else if (cfStr.toLowerCase().startsWith(
					PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE)
					|| cfStr.toLowerCase()
							.startsWith(
									PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_LASETPURCHASE)
					|| cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS)) {

				if (OrgStoreAddressMap == null) { // to get one-time parameter
													// values,is this really
													// need????????????

					// OrgStoreAddressMap = new HashMap<String, String>();
					OrgStoreAddressMap = Utility.retOrgStoreAddressMap(orgId,
							organizationStoresDao);

				}// if

			}// else if

		}// for

	}

	public static String getStorePlaceholder(String posLocId, String placeholder) {

		String retVal = null;
		if (posLocId == null || OrgStoreMap == null)
			return retVal;

		OrganizationStores organizationStores = OrgStoreMap.get(posLocId);

		if (organizationStores == null)
			return retVal;

		if (placeholder.toLowerCase().endsWith(
				PlaceHolders.CAMPAIGN_PH_STORENAME.toLowerCase())) {

			retVal = organizationStores.getStoreName();
		} else if (placeholder.toLowerCase().endsWith(
				PlaceHolders.CAMPAIGN_PH_STOREMANAGER.toLowerCase())) {
			retVal = organizationStores.getStoreManagerName();
		} else if (placeholder.toLowerCase().endsWith(
				PlaceHolders.CAMPAIGN_PH_STOREEMAIL.toLowerCase())) {
			retVal = organizationStores.getEmailId();
		} else if (placeholder.toLowerCase().endsWith(
				PlaceHolders.CAMPAIGN_PH_STOREPHONE.toLowerCase())) {
			retVal = organizationStores.getAddress().getPhone();

		} else if (placeholder.toLowerCase().endsWith(
				PlaceHolders.CAMPAIGN_PH_STORESTREET.toLowerCase())) {
			retVal = organizationStores.getAddress().getAddressOne();
		} else if (placeholder.toLowerCase().endsWith(
				PlaceHolders.CAMPAIGN_PH_STORECITY.toLowerCase())) {
			retVal = organizationStores.getAddress().getCity();
		} else if (placeholder.toLowerCase().endsWith(
				PlaceHolders.CAMPAIGN_PH_STORESTATE.toLowerCase())) {
			retVal = organizationStores.getAddress().getState();

		} else if (placeholder.toLowerCase().endsWith(
				PlaceHolders.CAMPAIGN_PH_STOREZIP.toLowerCase())) {
			retVal = organizationStores.getAddress().getPin() != null ? organizationStores
					.getAddress().getPin() + Constants.STRING_NILL
					: Constants.STRING_NILL;

		}

		return retVal;

	}// getStorePlaceholder

	// added for substitution tags
	public static String getStoreAddress(String posLocId) {
		String retVal = null;

		if (posLocId == null || OrgStoreAddressMap == null) {

			return retVal;

		}

		String address = OrgStoreAddressMap.get(posLocId);

		if (address == null) {
			return retVal;

		}

		return address;

	}// getStoreAddress

	public static void main(String[] args) {

		String msg = "http://google.com";
		String pattern = "(http:[A-z0-9./~%]+)";
		int options = 0;
		options |= 128; // This option is for Case insensitive
		options |= 32;
		Pattern p = Pattern.compile(pattern, options);
		Matcher m = p.matcher(msg);

		while (m.find()) {

			String linkUrl = m.group().trim();
			logger.debug("linkUrl ::" + linkUrl);
		}

		/*
		 * String[] tokenarr = msg.split("http://cway.in/");
		 * logger.debug(tokenarr.length); for (String string : tokenarr) {
		 * logger.debug(string);
		 * 
		 * }
		 */
		// String
		// msg="Testingmessageofthesfsdksaflasfas;df;asdjf;asdfasdfklqwejhrtewkjrtsdjkhskjdgsdkfgksdgsdkfgsdfgskdfgksdgfsdkfgjsdfkgsdkfgFromHere|^Gen_FN|HiKris^|skdfgksdkfgasdgsdlfglssdksaflasfas;df;asdjf;asdfasdfklqwejhrtewkjrtsdjkhskjdgsdkfgksdgsdkfgsdfgskdfgksdgfsdkfgjsdfkgsdkfgFromHere|^Gen_FN|HiKris^|skdfgksdkfgasdgsdlfgls";
		/*
		 * List<String> lst = new SMSCampaignSubmitter().splitSMSMessage(msg,
		 * 140); for (String string : lst) { logger.debug("---"+string); }
		 */

	}

	private void updateSegmentCountAndQuery(SegmentRules segmentRule,
			String generatedQuery, String generatedCountQuery) {
		logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<called>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		long size = 0;
		size = contactsDao.getSegmentedContactsCount(generatedCountQuery);

		segmentRule.setTotMobileSize(size);
		segmentRule.setMobileSegQuery(generatedQuery);
		segmentRule.setLastRefreshedOn(Calendar.getInstance());

		segmentRulesDaoForDML.saveOrUpdate(segmentRule);

	}

} // class
