package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.Notification;
import org.mq.captiway.scheduler.beans.NotificationCampReportLists;
import org.mq.captiway.scheduler.beans.NotificationCampaignReport;
import org.mq.captiway.scheduler.beans.NotificationSchedule;
import org.mq.captiway.scheduler.beans.SegmentRules;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.MessagesDaoForDML;
import org.mq.captiway.scheduler.dao.NotificationCampReportListsDaoForDML;
import org.mq.captiway.scheduler.dao.NotificationCampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.NotificationCampaignSentDao;
import org.mq.captiway.scheduler.dao.NotificationDao;
import org.mq.captiway.scheduler.dao.NotificationDaoForDML;
import org.mq.captiway.scheduler.dao.NotificationScheduleDaoForDML;
import org.mq.captiway.scheduler.dao.SegmentRulesDao;
import org.mq.captiway.scheduler.dao.SegmentRulesDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.services.NotificationThreadSubmission;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.SalesQueryGenerator;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class NotificationCampSubmitter extends Thread {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private NotificationQueue notificationQueue;
	private volatile boolean isRunning= false;
	private NotificationScheduleDaoForDML notificationScheduleDaoForDML;
	private NotificationDao notificationDao;
	private NotificationDaoForDML notificationDaoForDML;
	private CampaignsDao campaignsDao;
	private UsersDao usersDao;
	private ContactsDao contactsDao;
	private ContactsDaoForDML contactsDaoForDML;
	private MessagesDaoForDML messagesDaoForDML;
	private SegmentRulesDao segmentRulesDao;
	private SegmentRulesDaoForDML segmentRulesDaoForDML;
	private MailingListDao mailingListDao;
	private Set<String> totalPhSet;
	private NotificationCampaignReportDaoForDML notificationCampaignReportDaoForDML;
	private NotificationCampaignSentDao notificationCampaignSentDao;
	private NotificationCampReportListsDaoForDML notificationCampReportListsDaoForDML;
	private ServiceLocator locator ;
	private ApplicationContext applicationContext;

	public Set<String> getTotalPhSet() {
		return totalPhSet;
	}
	
	private static NotificationCampSubmitter notificationCampaignSubmitter;

	private static Object currentRunningObj = null;

	public static Object getCurrentRunningObj() {
		return currentRunningObj;
	}
public NotificationCampSubmitter(ApplicationContext applicationContext){
	try {
		this.applicationContext = applicationContext;
		this.locator = ServiceLocator.getInstance();
		this.notificationQueue = (NotificationQueue) locator.getBeanByName(OCConstants.NOTIFICATION_QUEUE);
		this.contactsDao = (ContactsDao) locator.getDAOByName(OCConstants.CONTACTS_DAO);
		this.contactsDaoForDML = (ContactsDaoForDML)locator.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);		
		this.usersDao = (UsersDao) locator.getDAOByName(OCConstants.USERS_DAO);
		this.messagesDaoForDML = (MessagesDaoForDML) locator.getDAOForDMLByName(OCConstants.MESSAGES_DAO_FOR_DML);
		this.notificationDao = (NotificationDao) locator.getDAOByName(OCConstants.NOTIFICATION_DAO);
		this.notificationDaoForDML = (NotificationDaoForDML) locator.getDAOForDMLByName(OCConstants.NOTIFICATION_DAO_FOR_DML);
		this.notificationScheduleDaoForDML = (NotificationScheduleDaoForDML) locator.getDAOForDMLByName(OCConstants.NOTIFICATION_SCHEDULE_DAO_FOR_DML);
		this.segmentRulesDaoForDML = (SegmentRulesDaoForDML)locator.getDAOForDMLByName(OCConstants.SEGMENTRULES_DAO_FOR_DML);
		this.segmentRulesDao = (SegmentRulesDao)locator.getDAOByName(OCConstants.SEGMENTRULES_DAO);
		this.notificationCampaignReportDaoForDML = (NotificationCampaignReportDaoForDML)locator.getDAOForDMLByName(OCConstants.NOTIFICATION_CAMPAIGNREPORT_DAO_FOR_DML);
		this.notificationCampaignSentDao = (NotificationCampaignSentDao)locator.getDAOByName(OCConstants.NOTIFICATION_CAMPAIGN_SENT_DAO);
		this.mailingListDao = (MailingListDao)locator.getDAOByName(OCConstants.MAILINGLIST_DAO);
		this.notificationCampReportListsDaoForDML = (NotificationCampReportListsDaoForDML)locator.getDAOForDMLByName(OCConstants.NOTIFICATION_CAMP_REPORTLISTS_DAO_FOR_DML);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception", e);
	}
}

	public static void startNotificationCampSubmitter(ApplicationContext applicationContext) {
		if (notificationCampaignSubmitter == null || !notificationCampaignSubmitter.isRunning()) {
			notificationCampaignSubmitter = new NotificationCampSubmitter(applicationContext);
			notificationCampaignSubmitter.start();
		}// if

	}

	private NotificationCampSubmitter() {
		try {
			this.locator = ServiceLocator.getInstance();
			this.notificationQueue = (NotificationQueue) locator.getBeanByName(OCConstants.NOTIFICATION_QUEUE);
			this.contactsDao = (ContactsDao) locator.getDAOByName(OCConstants.CONTACTS_DAO);
			this.contactsDaoForDML = (ContactsDaoForDML)locator.getDAOForDMLByName(OCConstants.CONTACTS_DAO_FOR_DML);		
			this.usersDao = (UsersDao) locator.getDAOByName(OCConstants.USERS_DAO);
			this.messagesDaoForDML = (MessagesDaoForDML) locator.getDAOForDMLByName(OCConstants.MESSAGES_DAO_FOR_DML);
			this.notificationDao = (NotificationDao) locator.getDAOByName(OCConstants.NOTIFICATION_DAO);
			this.notificationDaoForDML = (NotificationDaoForDML) locator.getDAOForDMLByName(OCConstants.NOTIFICATION_DAO_FOR_DML);
			this.notificationScheduleDaoForDML = (NotificationScheduleDaoForDML) locator.getDAOForDMLByName(OCConstants.NOTIFICATION_SCHEDULE_DAO_FOR_DML);
			this.segmentRulesDaoForDML = (SegmentRulesDaoForDML)locator.getDAOForDMLByName(OCConstants.SEGMENTRULES_DAO_FOR_DML);
			this.segmentRulesDao = (SegmentRulesDao)locator.getDAOByName(OCConstants.SEGMENTRULES_DAO);
			this.notificationCampaignReportDaoForDML = (NotificationCampaignReportDaoForDML)locator.getDAOForDMLByName(OCConstants.NOTIFICATION_CAMPAIGNREPORT_DAO_FOR_DML);
			this.notificationCampaignSentDao = (NotificationCampaignSentDao)locator.getDAOByName(OCConstants.NOTIFICATION_CAMPAIGN_SENT_DAO);
			this.mailingListDao = (MailingListDao)locator.getDAOByName(OCConstants.MAILINGLIST_DAO);
			this.notificationCampReportListsDaoForDML = (NotificationCampReportListsDaoForDML)locator.getDAOForDMLByName(OCConstants.NOTIFICATION_CAMP_REPORTLISTS_DAO_FOR_DML);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setIsRunning(boolean newIsRunning) {
		this.isRunning = newIsRunning;
	}


	@Override
	public void run() {
		setIsRunning(true);
		Notification notification = null;

		NotificationSchedule tempNotificationSchedule;
		Object obj = null;
		String fromSourceType = "";

		logger.debug("started ");

		try {
			if (logger.isInfoEnabled())
				logger.info("notificationQueue Size=" + notificationQueue.getQueueSize());
			while ((currentRunningObj = obj = notificationQueue.getObjFromQueue()) != null) {
					if (obj instanceof NotificationSchedule) {
							tempNotificationSchedule = (NotificationSchedule) obj;
					if (checkAnyOtherObjectRunning()) { 
							List<NotificationSchedule> rePutScheduleList = new ArrayList<NotificationSchedule>();
							rePutScheduleList.add(tempNotificationSchedule);
							notificationQueue.addCollection(rePutScheduleList);
							continue;
					}// if
					notification = notificationDao.findByCampaignId(tempNotificationSchedule.getNotificationId());
					fromSourceType = "NotificationCampaignSchedule";
					sendNotificationCampaign(notification, tempNotificationSchedule,fromSourceType);
					contactsDaoForDML.deleteNotificationTempContacts();
					tempNotificationSchedule.setStatus((byte) 1);
					notification.setStatus("Sent");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			setIsRunning(false);
			notificationQueue.removeObjectFromQueue(obj);
		}
		
		
	}


	private void sendNotificationCampaign(Notification notification, NotificationSchedule notificationSchedule,String fromSourceType) throws BaseServiceException {
	try {
		if (notification == null) {
			if (logger.isInfoEnabled())
				logger.info("got notification campaign as null,returning from sending notification....");
			return;
		}
		Users user = usersDao.find(notification.getUserId());
		//Long userId = user.getUserId();
		//Long orgId = user.getUserOrganization().getUserOrgId();
		String notificationCampaignName = notification.getNotificationName();
		String notificationBodyContent = notification.getNotificationContent();
		String notificationHeaderContent = notification.getHeader();
		if (notificationBodyContent == null || notificationBodyContent.trim().length() == 0) {
			if (logger.isErrorEnabled())
				logger.error("No message content found for notification : "+ notificationCampaignName);
				return;
		}
		Set<MailingList> mlList = notification.getMailingLists();
		if (mlList == null || mlList.isEmpty()) {
			Messages msgs = new Messages(
					"Send Notification Campaign",
					"Notification campaign sending failed ",
					"'"	+ notificationCampaignName
							+ "' couldn't be sent, no list has configured to this campaign(might have deleted)",
							Calendar.getInstance(), "Inbox", false, "Info", user);
			messagesDaoForDML.saveOrUpdate(msgs);
			return;
		}
		String mlStr = Constants.STRING_NILL;
		String listsName = "";
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
		contactsDaoForDML.deleteNotificationTempContacts();
		boolean isSegment = false;
		boolean isListEmpty=false;
		//int suppressedCount = 0;
		int configuredCount = 0;
		int totalContacts = 0;
		int totalAvailabeleContacts = 0;
		String tolCountQry = "";

		//int prefernceCount = 0;
		//int creditsCount = 0;
		String listType = notification.getListType();
		if (listType == null || listType.equalsIgnoreCase("Total")) {
			isSegment = false;
			qryStr = "INSERT IGNORE INTO notification_tempcontacts("
					+ Constants.QRY_COLUMNS_TEMP_CONTACTS
					+ ", push_notification, instance_id , device_Type"
					+ ",  event_source_id)  "
					+ "(SELECT DISTINCT "+ Constants.QRY_COLUMNS_CONTACTS +",push_notification, instance_id, device_Type" + ","
					+ "  null FROM contacts c, mailing_lists ml "
					+ " WHERE c.user_id = ml.user_id AND ml.list_id IN(" + mlStr + ")"
					+ " AND (c.mlbits & ml.mlbit) >0 "
					+ " AND c.push_notification IS NOT NULL AND c.push_notification =1 "
					+ " AND c.instance_id IS NOT NULL AND c.instance_id !='' AND c.device_Type IS NOT NULL AND c.device_Type !='' "
					+ " )";
			
			tolCountQry = "SELECT COUNT(distinct c.cid) FROM contacts c, mailing_lists ml "
					+ " WHERE c.user_id = ml.user_id AND ml.list_id in(" + mlStr+ ") "
					+ " AND (c.mlbits & ml.mlbit) >0 AND c.mobile_phone IS NOT NULL AND "
					+ "  c.push_notification IS NOT NULL AND c.push_notification =1 "
					+ " AND c.instance_id IS NOT NULL AND c.instance_id !='' AND c.device_Type IS NOT NULL AND c.device_Type !='' "
					;

			totalAvailabeleContacts = configuredCount = contactsDaoForDML.createNotificationTempContacts(qryStr, null, user, false, user.getCountryCarrier().shortValue());
		} else {
			if (listType.toLowerCase().trim().startsWith("segment:")) {
				isSegment = true;
				boolean success = true;
				String msgStr = "";
				Calendar cal = Calendar.getInstance();
				List<SegmentRules> segmentRules = segmentRulesDao.findById(listType.split(":")[1]);
				try {
					if (segmentRules == null) {
						success = false;
						msgStr = "Segments configured to this campaign no longer exists. You might have deleted them.";
						return;
					} // if none of configured segments found
					for (SegmentRules segmentRule : segmentRules) {
						if (segmentRule == null) {
							msgStr = "One of the segments configured to this campaign no longer exists. You might have deleted it.";
							continue;
						} // if
						if (segmentRule.getSegRule() == null) {
							msgStr = "Invalid segments configured to this campaign ";
							continue;
						}
						Set<MailingList> mlistSet = new HashSet<MailingList>();
						List<MailingList> mailinglLists = mailingListDao.findByIds(segmentRule.getSegmentMlistIdsStr());
						if (mailinglLists == null) {
							continue;
						}

						mlistSet.addAll(mailinglLists);
						long mlsbit = Utility.getMlsBit(mlistSet);

						String segmentQuery = SalesQueryGenerator.generateNotificationListSegmentQuery(segmentRule.getSegRule(),mlsbit);
						if (segmentQuery == null) {
							msgStr = "Invalid segments configured to this campaign ";
							continue;
						}
						String segmentCountQry = SalesQueryGenerator.generateNotificationListSegmentCountQuery(segmentRule.getSegRule(), mlsbit);
						if (segmentCountQry == null) {
							msgStr = "Invalid segments configured to this campaign ";
							continue;
						}
						if (SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRule.getSegRule())) {
							String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRule.getSegRule());
							if (csCampIds != null) {
								String crIDs = Constants.STRING_NILL;
								List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRule.getUserId(), csCampIds);
								if (campList != null) {
									for (Object[] crArr : campList) {
										if (!crIDs.isEmpty())
											crIDs += Constants.DELIMETER_COMMA;
											crIDs += ((Long) crArr[0]).longValue();
									}
								}
								segmentQuery = segmentQuery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH,("AND cr_id in(" + crIDs + ")"));
								segmentCountQry = segmentCountQry.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH,("AND cr_id in(" + crIDs + ")"));
							}
						}
						long startTime = System.currentTimeMillis();
						updateSegmentCountAndQuery(segmentRule, segmentQuery, segmentCountQry);
						Calendar calStartTimeObj = Calendar.getInstance();
						calStartTimeObj.setTimeInMillis(startTime);
						//String finalStartTime = calStartTimeObj.get(Calendar.DATE) + "-"+ (calStartTimeObj.get(Calendar.MONTH) + 1) + " " + calStartTimeObj.HOUR_OF_DAY + ":"+ calStartTimeObj.MINUTE + ":" + calStartTimeObj.SECOND;
						Calendar calEndTimeObj = Calendar.getInstance();
						calEndTimeObj.setTimeInMillis(System.currentTimeMillis());
						//String finalEndTime = calEndTimeObj.get(Calendar.DATE) + "-"+ (calEndTimeObj.get(Calendar.MONTH) + 1) + " " + calEndTimeObj.HOUR_OF_DAY + ":"+ calEndTimeObj.MINUTE + ":" + calEndTimeObj.SECOND;
						logger.info("Elapsed time for this segment rule, id==" + segmentRule.getSegRuleId() + "  name of segment rule===" + segmentRule.getSegRuleName() + " to run  "+ (System.currentTimeMillis() - startTime) + " Millisecond");
						if (!segmentListQuery.isEmpty())
							segmentListQuery += " UNION ";
							segmentListQuery += segmentQuery;
							qryStr = "INSERT IGNORE INTO notification_tempcontacts (" + Constants.QRY_COLUMNS_TEMP_CONTACTS+ ", instance_id, push_notification, device_Type, event_source_id)  " + segmentQuery;
							totalAvailabeleContacts = configuredCount += contactsDaoForDML.createNotificationTempContacts(qryStr,null, user, false, user.getCountryCarrier());
						if (tolCountQry.length() > 0)
							tolCountQry += "UNION";
							tolCountQry += segmentCountQry;
					} // for
				} catch (Exception e) {
					success = false;
					if (logger.isErrorEnabled())
						logger.error("** Exception while getting the available count" + " for the user:" + user.getUserId(),e);
					return;
				} finally {
					if (!success) {
						if (logger.isInfoEnabled())
							logger.info(">>>>>>>>>>..." + msgStr + " " + user + " ");
						if (notificationSchedule.getStatus() == 0) {
							notificationSchedule.setStatus((byte) 3);
							notificationScheduleDaoForDML.saveOrUpdate(notificationSchedule);
							if (msgStr != null && msgStr.isEmpty()) {
								msgStr = "Notification Campaign send failed due to Some error in server connections.";
							}
							Messages msgs = new Messages("Send Notification Campaign", "Notification campaign sending failed ", msgStr,cal, "Inbox", false, "Info", user);
							messagesDaoForDML.saveOrUpdate(msgs);
							Utility.sendCampaignFailureAlertMailToSupport(user, "","",notification.getNotificationName(),	notificationSchedule.getScheduledDate(),"Configured List of contacts/Segment not available", "", "", "");
						}
						contactsDaoForDML.deleteNotificationTempContacts();
					}
				} // finally
			}
		}// else
			totalContacts = contactsDao.getSegmentedContactsCount(tolCountQry);
			if (totalContacts <= 0) {

				// need to create report for it with all 0 counts....
				logger.info("no conatcts in notification_tempcontacts " + totalContacts);

				try {
					String notificationUrl = notification.getRedirectUrl();
					if (notificationUrl.length()>100) {
						notificationUrl = notificationUrl.substring(0, 100);

					}
					NotificationCampaignReport notificationCampaignReport = null;
					notificationCampaignReport = new NotificationCampaignReport(
							notification.getUserId(), notificationCampaignName,
							notificationBodyContent,notificationHeaderContent, Calendar.getInstance(), 0, 0, 0, 0, 0,
							Constants.CR_STATUS_SENDING, fromSourceType,notification.getBannerImageUrl(),notification.getLogoImageUrl(),notificationUrl);

					String msgStr = "Notification Campaign :"
							+ notificationCampaignName
							+ "\n"
							+ "Found 0 Active contacts in the mailing lists which "
							+ "are configured for the Notification campaign.\n"
							+ "Reason may be configured contacts exists in the suppression contacts list "
							+ "or contacts status changed by user/recipient himself unsubscribed";

					Messages messages = new Messages("Notification Campaign ","No Active contacts", msgStr,	Calendar.getInstance(), "Inbox", false, "Info",	user);
					messagesDaoForDML.saveOrUpdate(messages);
					
					if(!isSegment) 
						isListEmpty=true;
					logger.info("isListEmpty" +isListEmpty);
					
					Utility.sendCampaignFailureAlertMailToSupport(user,"","",notification.getNotificationName(),notificationSchedule.getScheduledDate(),
							"Configured List of contacts/Segment not available","","","");
					notificationCampaignReport.setStatus(Constants.CR_STATUS_SENT);
					notificationCampaignReportDaoForDML.saveOrUpdate(notificationCampaignReport);
					notificationSchedule.setStatus((byte) 1);
					notificationSchedule.setNotificationCrId(notificationCampaignReport.getNotificationCrId());
					notificationScheduleDaoForDML.saveOrUpdate(notificationSchedule);
					notificationDaoForDML.updateNotificationStatus(notification.getNotificationId());
					NotificationCampReportLists notificationCampLists = new NotificationCampReportLists(notificationCampaignReport.getNotificationCrId());
					notificationCampLists.setListsName(listsName);
					notificationCampLists.setSegmentQuery(segmentListQuery);
					notificationCampReportListsDaoForDML.saveOrUpdate(notificationCampLists);
				} catch (Exception e) {
					logger.error("Exception >>>>>> ", e);
				}

				return;
			}
			
			//ArrayList<String> bodyContentLst = null;
			//ArrayList<String> headerContentLst = null;
			notificationBodyContent = notificationBodyContent.replace("|^", "[").replace("^|", "]");
			notificationHeaderContent = notificationHeaderContent.replace("|^", "[").replace("^|", "]");
			int totalSizeInt = contactsDao.getAvailableContactsFromNotificationTempContacts(mlStr,"");
			totalPhSet = null;
			getCustomFields(notificationBodyContent);
			getCustomFields(notificationHeaderContent);
			Set<String> datePhSetHeader = getDateFields(notificationHeaderContent);
			Set<String> datePhSet = getDateFields(notificationBodyContent);
			/**************************************************/
			/*****
			 * CASE2: make a decission whether coupon codes will be
			 * sufficient or not
			 ***/

			CouponCodesDao couponCodesDao = (CouponCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
					
			CouponsDao couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			

			if (totalPhSet != null && totalPhSet.size() > 0) {
				logger.info("entered in storing in inbox logic");
				
				boolean success = true;
				boolean couponDeleted=false;
				String msgStr = "";
				boolean cccountflag = false;

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
					logger.info("entered coupon in inbox logic"+coupon);

					if (coupon == null) {
						success = false;
						// isInvalidCoupFlag = true;

						msgStr = "Notification Campaign Name : "
								+ notificationCampaignName
								+ "\r\n";
						msgStr = msgStr
								+ " \r\n Status : Could not be sent \r\n";
						msgStr = msgStr
								+ "Notification Campaign : "
								+ notificationCampaignName
								+ "' could not be sent as you have added  Promotion: "
								+ eachPh + " \r\n";
						msgStr = msgStr
								+ "This  Promotion no longer exists, you might have deleted it.  \r\n";

						if (logger.isWarnEnabled())
							logger.warn(eachPh
									+ "  PNotification Campaign :Promotion is not avalable: "
									+ eachPh);
						Messages messages = new Messages("Notification Details ","deleted Coupon", msgStr, Calendar.getInstance(), "Inbox", false,
								"Info", user);
						messagesDaoForDML.saveOrUpdate(messages);
						couponDeleted=true;
						
						return;
					}
					
					if (coupon.getStatus().equals(
							Constants.COUP_STATUS_EXPIRED)
							|| coupon.getStatus().equals(
									Constants.COUP_STATUS_PAUSED)) {

						success = false;

						msgStr = "Notification Campaign Name : "
								+ notificationCampaignName
								+ "\r\n";
						msgStr = msgStr
								+ " \r\n Status : Could not be sent \r\n";
						msgStr = msgStr
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
						Messages messages = new Messages("Notification Details ","Active Status", msgStr, Calendar.getInstance(), "Inbox", false,
								"Info", user);
						messagesDaoForDML.saveOrUpdate(messages);
						return;

					}
					
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
							msgStr = "Notification Campaign Name : "
									+ notificationCampaignName + "\r\n";
							msgStr = msgStr
									+ " \r\n Status : Could not be sent \r\n";
							msgStr = msgStr
									+ "Notification campaign could not be sent as you have added  Promotion : "
									+ coupon.getCouponName() + " \r\n";
							msgStr = msgStr
									+ "Available  Promo-codes you can send :"
									+ couponCodeCount + " \r\n";

							if (logger.isWarnEnabled())
								logger.warn(" Available  Promo-codes  limit is less than the configured contacts count");
							Messages messages = new Messages("Notification Details ","Discount codes Limit", msgStr, Calendar.getInstance(), "Inbox", false,
									"Info", user);
							messagesDaoForDML.saveOrUpdate(messages);
							return;
						}
					}
				}
			}
					
			if (datePhSet != null && datePhSet.size() > 0) {

				for (String symbol : datePhSet) {
					if (symbol.startsWith(Constants.DATE_PH_DATE_)) {
						if (symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_today)) {
								Calendar cal = MyCalendar.getNewCalendar();
								notificationBodyContent = notificationBodyContent.replace("[" + symbol + "]",MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						} else if (symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)) {
								Calendar cal = MyCalendar.getNewCalendar();
								cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
								notificationBodyContent = notificationBodyContent.replace("[" + symbol + "]",MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						} else if (symbol.endsWith(Constants.DATE_PH_DAYS)) {
							try {
								String[] days = symbol.split("_");
								Calendar cal = MyCalendar.getNewCalendar();
								cal.set(Calendar.DATE, cal.get(Calendar.DATE)+ Integer.parseInt(days[1].trim()));
								notificationBodyContent = notificationBodyContent.replace("[" + symbol + "]",MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							} catch (Exception e) {
								logger.debug("exception in parsing date placeholder");
							}
						}// else if
					}// if
				}// for
			}
			if (datePhSetHeader != null && datePhSetHeader.size() > 0) {
				for (String symbol : datePhSetHeader) {
					if (symbol.startsWith(Constants.DATE_PH_DATE_)) {
						if (symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_today)) {
								Calendar cal = MyCalendar.getNewCalendar();
								notificationHeaderContent = notificationHeaderContent.replace("[" + symbol + "]",MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						} else if (symbol.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)) {
								Calendar cal = MyCalendar.getNewCalendar();
								cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
								notificationHeaderContent = notificationHeaderContent.replace("[" + symbol + "]",MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						} else if (symbol.endsWith(Constants.DATE_PH_DAYS)) {
							try {
								String[] days = symbol.split("_");
								Calendar cal = MyCalendar.getNewCalendar();
								cal.set(Calendar.DATE, cal.get(Calendar.DATE)+ Integer.parseInt(days[1].trim()));
								notificationHeaderContent = notificationHeaderContent.replace("[" + symbol + "]",MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
							} catch (Exception e) {
								logger.debug("exception in parsing date placeholder");
							}
						}// else if
					}// if
				}
			}
			
			
			String msgStr = "";
			Calendar cal = Calendar.getInstance();
			NotificationCampaignReport notificationCampaignReport = null;
			
			String notificationUrl = notification.getRedirectUrl();
			if (notificationUrl.length()>100) {
				notificationUrl = notificationUrl.substring(0, 100);

			}
			notificationCampaignReport = new NotificationCampaignReport(notification.getUserId(),
					notificationCampaignName, notificationBodyContent,notificationHeaderContent, cal, 0, 0, 0, 0, 0,
					Constants.CR_STATUS_SENDING, fromSourceType,notification.getBannerImageUrl(),notification.getLogoImageUrl(),notificationUrl);
			try {
					notificationCampaignReportDaoForDML.saveOrUpdate(notificationCampaignReport);
					NotificationCampReportLists notificationCampLists = new NotificationCampReportLists(notificationCampaignReport.getNotificationCrId());
					notificationCampLists.setListsName(listsName);
					notificationCampLists.setSegmentQuery(segmentListQuery);
					notificationCampReportListsDaoForDML.saveOrUpdate(notificationCampLists);
			} catch (Exception e) {
				logger.error(" ** Exception while saving the notificationCampaignReport object");

			}
			
			if (totalAvailabeleContacts <= 0) {

				// need to create report for it with all 0 counts....
				//NotificationCampaignReport notificationCampaignReport = null;
				
				String notificationreUrl = notification.getRedirectUrl();
				if (notificationreUrl.length()>100) {
					notificationreUrl = notificationreUrl.substring(0, 100);

				}
				notificationCampaignReport = new NotificationCampaignReport(
						notification.getUserId(), notificationCampaignName,
						notificationBodyContent,notificationHeaderContent, Calendar.getInstance(), 0, 0, 0, 0, 0,
						Constants.CR_STATUS_SENDING, fromSourceType,notification.getBannerImageUrl(),notification.getLogoImageUrl(),notificationreUrl);
				
				
				if (logger.isInfoEnabled())
					logger.info("num of contacts in notification_tempcontacts is "+ totalAvailabeleContacts);

				msgStr = "Notification Name :" + notificationCampaignName
						+ "\n"
						+ "Found 0 mobile contacts in the mailing lists which "
						+ "are configured for the Notification campaign.\n"
						+ "Reason may be, configured contacts exists in the suppressed contacts list. ";

				Messages messages = new Messages("Notification Details ","No mobile contacts", msgStr, cal, "Inbox", false,
						"Info", user);
				messagesDaoForDML.saveOrUpdate(messages);
				notificationCampaignReport.setStatus(Constants.CR_STATUS_SENT);
				notificationCampaignReportDaoForDML.saveOrUpdate(notificationCampaignReport);
				notificationSchedule.setStatus((byte) 1);
				notificationSchedule.setNotificationCrId(notificationCampaignReport.getNotificationCrId());
				notificationScheduleDaoForDML.saveOrUpdate(notificationSchedule);
				notificationDaoForDML.updateNotificationStatus(notification.getNotificationId());
				NotificationCampReportLists notificationCampLists = new NotificationCampReportLists(notificationCampaignReport.getNotificationCrId());
				notificationCampLists.setListsName(listsName);
				notificationCampLists.setSegmentQuery(segmentListQuery);
				notificationCampReportListsDaoForDML.saveOrUpdate(notificationCampLists);
				return;
			}
	
				List<Thread> threadsList = new ArrayList<Thread>();
			try {
				NotificationRecipientProvider notificationRecipientProvider = new NotificationRecipientProvider(notificationCampaignReport, totalSizeInt,notification.getNotificationId(), totalPhSet);
				
				Thread th = new NotificationThreadSubmission(user, notification,notificationRecipientProvider,totalPhSet,notificationBodyContent,notificationHeaderContent,"notificationCampaignSchedule");
				th.setName("thread_campaingEmail:" + (1));
				threadsList.add(th);
				th.start();
			} catch (Exception e) {
				logger.error("Exception : ERROR OCCURED WHILE CREATING THREADS ..."+e);
			}
			
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
			notificationSchedule.setStatus((byte) 1);
			notificationSchedule.setNotificationCrId(notificationCampaignReport.getNotificationCrId());
			notificationScheduleDaoForDML.saveOrUpdate(notificationSchedule);
			notificationDaoForDML.updateNotificationStatus(notification.getNotificationId());
			String notificationDate = MyCalendar.calendarToString(notificationCampaignReport.getSentDate(),MyCalendar.FORMAT_DATETIME_STYEAR);
			contactsDaoForDML.updatelastNotificationDate(notificationCampaignReport.getNotificationCrId(),notificationDate);
			notificationCampaignReport.setStatus(Constants.CR_STATUS_SENT);
			notificationCampaignReport.setSent(notificationCampaignSentDao.getSentCount(notificationCampaignReport.getNotificationCrId()));
			notificationCampaignReportDaoForDML.saveOrUpdate(notificationCampaignReport);
			
			try {
				Utility.updateCouponCodeCounts(applicationContext, totalPhSet);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("** Exception while updating the coupons", e);
			}
	}catch (Exception e) {
		logger.error("** Error occured while submitting the notification campaigns", e);
	}
	logger.debug(">>>>>>> Completed NotificationCampaignSubmitter :: sendNotificationCampaign <<<<<<< ");
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

		} catch (Exception e) {
			logger.error("Exception while getting the symbol place holders ", e);
		}

		if (logger.isInfoEnabled())
			logger.info("symbol PH  Set : " + dateFieldsSet);

		return dateFieldsSet;
	}

	private String getCustomFields(String content) {
		// logger.debug("+++++++ Just Entered +++++"+ content);
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern, Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		if(totalPhSet==null || totalPhSet.isEmpty()) {
			totalPhSet = new HashSet<String>();
		}
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
					cfNameListStr = cfNameListStr + (cfNameListStr.equals("") ? "" : Constants.DELIMETER_COMMA) + "'" + ph.substring(3) + "'";
				} 
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

	private void updateSegmentCountAndQuery(SegmentRules segmentRule, String generatedQuery, String generatedCountQuery) {
		long size = 0;
		size = contactsDao.getSegmentedContactsCount(generatedCountQuery);
		segmentRule.setTotMobileSize(size);
		segmentRule.setMobileSegQuery(generatedQuery);
		segmentRule.setLastRefreshedOn(Calendar.getInstance());
		segmentRulesDaoForDML.saveOrUpdate(segmentRule);
	}

	private boolean checkAnyOtherObjectRunning() {
		int count = contactsDao.getSegmentedContactsCount("SELECT cid FROM notification_tempcontacts");
		logger.debug("before running any object the number of records in the Notification temp table are ::" + count);
		if (count > 0)
			return true;
		else
			return false;
	}
}
