package org.mq.captiway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.OrganizationStores;
import org.mq.captiway.scheduler.beans.SegmentRules;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.beans.WACampReportLists;

import org.mq.captiway.scheduler.beans.WACampaign;
import org.mq.captiway.scheduler.beans.WACampaignReport;
import org.mq.captiway.scheduler.beans.WACampaignsSchedule;
import org.mq.captiway.scheduler.beans.WATemplates;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.MessagesDaoForDML;
import org.mq.captiway.scheduler.dao.OrganizationStoresDao;
import org.mq.captiway.scheduler.dao.SegmentRulesDao;
import org.mq.captiway.scheduler.dao.SegmentRulesDaoForDML;
import org.mq.captiway.scheduler.dao.WACampReportListsDaoForDML;
import org.mq.captiway.scheduler.dao.WACampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.WACampaignSentDao;
import org.mq.captiway.scheduler.dao.WACampaignsDao;
import org.mq.captiway.scheduler.dao.WACampaignsDaoForDML;
import org.mq.captiway.scheduler.dao.WACampaignsScheduleDaoForDML;
import org.mq.captiway.scheduler.dao.WATemplatesDao;
import org.mq.captiway.scheduler.services.WAMultiThreadedSubmission;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PlaceHolders;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.ReplacePlaceHolders;
import org.mq.captiway.scheduler.utility.SalesQueryGenerator;
import org.mq.captiway.scheduler.utility.Utility;
import org.springframework.context.ApplicationContext;

public class WACampaignSubmitter extends Thread {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	private WAQueue waQueue;
	private ApplicationContext applicationContext;
	private volatile boolean isRunning = false;
	private WACampaignsScheduleDaoForDML waCampaignScheduleDaoForDML;
	private WACampaignsDao waCampaignsDao;
	private WACampaignsDaoForDML waCampaignsDaoForDML;

	private ContactsDao contactsDao;
	private ContactsDaoForDML contactsDaoForDML;
	private MessagesDaoForDML messagesDaoForDML;
	private SegmentRulesDao segmentRulesDao;
	private SegmentRulesDaoForDML segmentRulesDaoForDML;
	private MailingListDao mailingListDao;
	private CampaignsDao campaignsDao;
	private OrganizationStoresDao organizationStoresDao;

	private WACampaignSentDao waCampaignSentDao;
	private WATemplatesDao waTemplatesDao;
	private WACampaignReportDaoForDML waCampaignReportDaoForDML;
	private WACampReportListsDaoForDML waCampReportListsDaoForDML;

	private static WACampaignSubmitter waCampaignSubmitter;

	private WACampaignSubmitter() {
	}

	private static Object currentRunningObj = null;

	public static Object getCurrentRunningObj() {
		return currentRunningObj;
	}

	private static int NUMBER_OF_WA_THREADS;

	public static void startWACampaignSubmitter(ApplicationContext applicationContext) {

		logger.info("===> started startWACampaignSubmitter ");

		if (waCampaignSubmitter == null || !waCampaignSubmitter.isRunning()) {

			waCampaignSubmitter = new WACampaignSubmitter(applicationContext);
			waCampaignSubmitter.start();

			NUMBER_OF_WA_THREADS = Integer.parseInt(PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_WA_THREAD_COUNT));

		} // if

	}// startWACampaignSubmitter

	private WACampaignSubmitter(ApplicationContext applicationContext) {

		this.applicationContext = applicationContext;
		this.waQueue = (WAQueue) applicationContext.getBean("waQueue");

		contactsDao = (ContactsDao) applicationContext.getBean("contactsDao");
		contactsDaoForDML = (ContactsDaoForDML) applicationContext.getBean("contactsDaoForDML");

		messagesDaoForDML = (MessagesDaoForDML) applicationContext.getBean("messagesDaoForDML");
		waCampaignsDao = (WACampaignsDao) applicationContext.getBean("waCampaignsDao");
		waCampaignsDaoForDML = (WACampaignsDaoForDML) applicationContext.getBean("waCampaignsDaoForDML");
		waCampaignScheduleDaoForDML = (WACampaignsScheduleDaoForDML) applicationContext.getBean("waCampaignsScheduleDaoForDML");
		waCampaignSentDao = (WACampaignSentDao) applicationContext.getBean("waCampaignSentDao");
		waTemplatesDao = (WATemplatesDao) applicationContext.getBean("waTemplatesDao");
		waCampaignReportDaoForDML = (WACampaignReportDaoForDML) applicationContext.getBean("waCampaignReportDaoForDML");
		waCampReportListsDaoForDML = (WACampReportListsDaoForDML) applicationContext
				.getBean("waCampReportListsDaoForDML");
		segmentRulesDao = (SegmentRulesDao) applicationContext.getBean("segmentRulesDao");
		mailingListDao = (MailingListDao) applicationContext.getBean("mailingListDao");
		campaignsDao = (CampaignsDao) applicationContext.getBean("campaignsDao");
		segmentRulesDaoForDML = (SegmentRulesDaoForDML) applicationContext.getBean("segmentRulesDaoForDML");
		organizationStoresDao = (OrganizationStoresDao) applicationContext.getBean("organizationStoresDao");
	}


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

		setIsRunning(true);
		WACampaign tempWACampaign = null;

		WACampaignsSchedule tempWACampaignSchedule;
		Object obj = null;
		String fromSourceType = "";

		logger.debug("started ");

		try {
			if (logger.isInfoEnabled())logger.info("waQueue Size=" + waQueue.getQueueSize());

			while ((currentRunningObj= obj = waQueue.getObjFromQueue()) != null) { //(currentRunningObj = obj = waQueue.getObjFromQueue()) != null

				OrgStoreMap = null;
				OrgStoreAddressMap = null;


				if (obj instanceof WACampaignsSchedule) {

					tempWACampaignSchedule = (WACampaignsSchedule) obj;

					if (checkAnyOtherObjectRunning()) {

						List<WACampaignsSchedule> rePutScheduleList = new ArrayList<WACampaignsSchedule>();
						rePutScheduleList.add(tempWACampaignSchedule);
						waQueue.addCollection(rePutScheduleList); //because waQueue.getObjFromQueue() -> Retrieves and removes the head of queue
						continue;

					} // if

					tempWACampaign = waCampaignsDao.findByCampaignId(tempWACampaignSchedule.getWaCampaignId());
					fromSourceType = Constants.SOURCE_WA_CAMPAIGN;

					sendWACampaign(tempWACampaign, tempWACampaignSchedule, fromSourceType);

					contactsDaoForDML.deleteWATempContacts();
					
					tempWACampaignSchedule.setStatus((byte) 1);
					tempWACampaign.setStatus("Sent");
				} // if
			} // while

		} catch (Exception e) {
			logger.error("Exception ::::", e);
		} finally {
			// isRunning = false;

			setIsRunning(false);
			waQueue.removeObjectFromQueue(obj);
			// TODO need to know whether we should delete the object from the
			// waqueue or not
		}
	} // run

	private boolean checkAnyOtherObjectRunning() {

		int count = contactsDao.getSegmentedContactsCount("SELECT cid FROM wa_tempcontacts");

		logger.debug("before running any object the number of records in the wa temp table are ::" + count);
		if (count > 0)
			return true;
		else
			return false;

	}// checkAnyOtherObjectRunning()

	/**
	 * this method sends the waCampaign to httpClient
	 * 
	 * @param waCampaign
	 * @param waCampaignSchedule
	 * @param fromSource
	 */
	private void sendWACampaign(WACampaign waCampaign, WACampaignsSchedule waCampaignSchedule, String fromSource) {

		try {

			logger.info("-----just entered into WACampaignSubmitter.sendWACampaign()------ " + waCampaignSchedule.getWaCsId().longValue());

			if (waCampaign == null) {

				logger.warn("got WA campaign as null,returning from sending WA....");
				return;
			}

			Users user = waCampaign.getUsers();
			String waTemplateId = waCampaign.getWaTemplateId();

			if (waTemplateId == null || waTemplateId.trim().length() == 0) {

				logger.warn("NO template is configured to the WA campaign : " + waCampaign.getWaCampaignName());
				return;
			}

			WATemplates waTemplates = waTemplatesDao.find(Long.parseLong(waTemplateId));
			if (waTemplates == null) {

				logger.warn("template does not exist for campaign : " + waCampaign.getWaCampaignName());
				return;

			}

			Long orgId = user.getUserOrganization().getUserOrgId();

			String waCampaignName = waCampaign.getWaCampaignName();
			String jsonContent = waTemplates.getJsonContent(); //waCampaign.getMessageContent();
			String templateProvider = waTemplates.getProvider();

			if (jsonContent == null || jsonContent.trim().length() == 0) {

				logger.warn("Json content not found for Template:" + waTemplates.getTemplateName());
				return;

			}else if(waTemplates.getTemplateRegisteredId()!=null && !waTemplates.getTemplateRegisteredId().isEmpty()){
				jsonContent = jsonContent.replace("[WATemplateID]", waTemplates.getTemplateRegisteredId());
			}

			if (logger.isInfoEnabled()) logger.info("WA campaign Name=" + waCampaignName);

			String useMqsStr = PropertyUtil.getPropertyValueFromDB("useMQS");
			boolean useMQS = (useMqsStr == null ? true : useMqsStr
					.equalsIgnoreCase("true"));

			Set<MailingList> mlList = waCampaign.getMailingLists();
			if (mlList == null || mlList.isEmpty()) {

				logger.warn(waCampaignName+" > no list has configured to this campaign(might have deleted)");
				Messages msgs = new Messages("Send WA Campaign", "WA campaign sending failed ",
						"'" + waCampaignName
						+ "' couldn't be sent, no list has configured to this campaign(might have deleted)",
						Calendar.getInstance(), "Inbox", false, "Info", user);
				messagesDaoForDML.saveOrUpdate(msgs);
				return;

			}

			String mlStr = Constants.STRING_NILL;
			String listsName = "";
			String qryStr = null;
			String segmentListQuery = Constants.STRING_NILL;

			for (MailingList mailingList : mlList) {

				if (mlStr.length() > 0)
					mlStr += ",";

				mlStr += mailingList.getListId().longValue();

				if (listsName.length() > 0)
					listsName += ",";

				listsName += mailingList.getListName();

			} // for each mailing list

			contactsDaoForDML.deleteWATempContacts();

			boolean isSegment = false;
			boolean isListEmpty = false;
			int suppressedCount = 0;
			int configuredCount = 0;
			int totalContacts = 0;
			int totalAvailableTempContacts = 0;
			String totCountQry = "";

			int preferenceCount = 0;
			String listType = waCampaign.getListType();
			logger.info("waCampaign ListType :"+listType);

			if (listType == null || listType.equalsIgnoreCase("Total")) {

				isSegment = false;

				qryStr = "INSERT IGNORE INTO wa_tempcontacts(" + Constants.QRY_COLUMNS_TEMP_CONTACTS
						+ ", cf_value, domain, event_source_id)  " + "(SELECT DISTINCT "
						+ Constants.QRY_COLUMNS_CONTACTS + ",NULL,"
						+ " SUBSTRING_INDEX(c.email_id, '@', -1), null FROM contacts c, mailing_lists ml "
						+ " WHERE c.user_id = ml.user_id AND ml.list_id IN(" + mlStr + ")"
						+ " AND (c.mlbits & ml.mlbit) >0 AND c.mobile_phone IS NOT NULL"
						+ " AND c.mobile_phone !='' AND c.mobile_status='" + Constants.CON_MOBILE_STATUS_ACTIVE + "'"
						+ ")";

				totCountQry = "SELECT COUNT(distinct c.cid) FROM contacts c, mailing_lists ml "
						+ " WHERE c.user_id = ml.user_id AND ml.list_id in(" + mlStr + ") "
						+ " AND (c.mlbits & ml.mlbit) >0 AND c.mobile_phone IS NOT NULL AND c.mobile_phone !='' "
						+ " AND c.mobile_status='" + Constants.CON_MOBILE_STATUS_ACTIVE + "'"
						+ "";

				totalAvailableTempContacts = configuredCount = contactsDaoForDML.createWATempContacts(qryStr);
			}
			else {

				isSegment = true;

				if (listType.toLowerCase().trim().startsWith("segment:")) {
					String initiatedStatus = contactsDao.isInitiatedForSegmentedContacts(waCampaign.getWaCampaignId(), 
							waCampaignSchedule.getWaCsId(), "WA");
					if(initiatedStatus == null || initiatedStatus.equalsIgnoreCase("A")) { //initiate if not and return if its still Active
						if(initiatedStatus == null) {
							
							logger.debug("===span the segment execution for cs id ==="+waCampaignSchedule.getWaCsId());
							 ExecutorService executor = Executors.newFixedThreadPool(1);
							 String timestamp = System.currentTimeMillis()+"WA"+waCampaign.getWaCampaignId()+waCampaignSchedule.getWaCsId();
							contactsDaoForDML.initiateSegmentQuery(waCampaign.getWaCampaignId(), waCampaignSchedule.getWaCsId(), "WA");
							 PrepareSegmentedContacts PrepareSegmentedContacts = new PrepareSegmentedContacts(listType, 
									 waCampaign, waCampaignSchedule, "WA", user, waCampaign.getWaCampaignId(), 
									 waCampaignSchedule.getWaCsId(), timestamp);
							
							executor.execute(PrepareSegmentedContacts);
							return;
						}else {
							logger.debug("====segmented contacts are still getting prepared for =="+waCampaignSchedule.getWaCsId());
							return;
						}
						
					}else if(initiatedStatus.equalsIgnoreCase("D")) { 
						
						//proceed if its done
						boolean success = true;
						String msgStr = "";
						Calendar cal = Calendar.getInstance();
						List<SegmentRules> segmentRules = segmentRulesDao.findById(listType.split(":")[1]);
						
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
								List<MailingList> mailinglLists = mailingListDao.findByIds(segmentRule.getSegmentMlistIdsStr());
								if (mailinglLists == null) {
									continue;
								}
								
								mlistSet.addAll(mailinglLists);
								long mlsbit = Utility.getMlsBit(mlistSet);
								
								//TODO convert to generateWAListSegmentQuery if needed
								String segmentQuery = SalesQueryGenerator.generateSMSListSegmentQuery(
										segmentRule.getSegRule(), mlsbit);
								
								if (segmentQuery == null) {
									
									msgStr = "Invalid segments configured to this campaign ";
									continue;
								}
								
								//TODO convert to generateWAListSegmentCountQuery if needed
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
												"<MOBILEOPTIN>","");
								//!waCampaign.isEnableEntireList() ? " AND c.mobile_opt_in=1": ""
								
								segmentCountQry = segmentCountQry
										.replace(
												"<MOBILEOPTIN>","");
								//!waCampaign.isEnableEntireList() ? " AND c.mobile_opt_in=1": ""
								
								// >>>>>>>>>>>>>>>>>updating seg rules
								// starts<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
								/*long startTime = System.currentTimeMillis();
								
								updateSegmentCountAndQuery(segmentRule,
										segmentQuery, segmentCountQry);
								
								logger.info("Elapsed time for this segment rule, id=="
										+ segmentRule.getSegRuleId()
										+ "  name of segment rule==="
										+ segmentRule.getSegRuleName()
										+ " to run  "
										+ (System.currentTimeMillis() - startTime)
										+ " Millisecond");
								*/
								// >>>>>>>>>>>>>>>>>updating seg rules
								// ends<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
								if (!segmentListQuery.isEmpty())
									segmentListQuery += " UNION ";
								segmentListQuery += segmentQuery;
								
								if (totCountQry.length() > 0)
									totCountQry += "UNION";
								totCountQry += segmentCountQry;
								
							}// for
							qryStr = "INSERT IGNORE INTO wa_tempcontacts ("
									+ Constants.QRY_COLUMNS_TEMP_CONTACTS
									+ ", cf_value, domain, event_source_id)  select "+Constants.QRY_COLUMNS_TEMP_CONTACTS
									+ ", cf_value, domain, event_source_id from temp_wa_segmented_contacts WHERE "
									+ " camp_id="+waCampaign.getWaCampaignId()+" "
									+ " AND schedule_id="+waCampaignSchedule.getWaCsId()+" AND type='WA'";;
				
									
							
							totalAvailableTempContacts = configuredCount += contactsDaoForDML.createWATempContacts(qryStr);
							
							
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
								
								logger.warn(">>>>>>>>>>..." + msgStr + " "+ user + " ");
								if(waCampaignSchedule.getStatus() == 0){
									
									waCampaignSchedule.setStatus((byte) 3);
									waCampaignScheduleDaoForDML
									.saveOrUpdate(waCampaignSchedule);
									
									if (msgStr != null && msgStr.isEmpty()) {
										
										msgStr = "WA Campaign send failed due to Some error in server connections.";
									}
									Messages msgs = new Messages("Send WA Campaign",
											"WA campaign sending failed ", msgStr,
											cal, "Inbox", false, "Info", user);
									messagesDaoForDML.saveOrUpdate(msgs);
									// TODO Utility.sendCampaignFailureAlertMailToSupport(user,waCampaign.getWaCampaignName(),"","",waCampaignSchedule.getScheduledDate(),"Configured List of contacts/Segment not available","","","");
								}
								
								
								//del watemp contacts
								contactsDaoForDML.deleteWATempContacts();
								contactsDaoForDML.deleteWASegmentedContacts(waCampaign.getWaCampaignId(), waCampaignSchedule.getWaCsId(),"WA");
							}
							
						}// finally
					}

				}// if

			}

			//totalContacts = contactsDao.getSegmentedContactsCount(totCountQry);
			totalContacts = totalAvailableTempContacts;
			if (totalContacts <= 0) {

				logger.warn("no active contact in contacts " + totalContacts);

				try {

					WACampaignReport waCampaignReport = null;

					waCampaignReport = new WACampaignReport(waCampaign.getUsers(), waCampaignName, jsonContent,
							Calendar.getInstance(), 0, 0, 0, 0, 0, Constants.CR_STATUS_SENDING, fromSource);

					logger.warn(">>>>>>> wa campaign submission is failed found 0 active contacts, campaign id : "
							+ waCampaign.getWaCampaignId());

					String msgStr = "WA Campaign :" + waCampaignName + "\n"
							+ "Found 0 Active contacts in the mailing lists which "
							+ "are configured for the WA campaign.\n"
							+ "Reason may be configured contacts exists in the suppression contacts list "
							+ "or contacts status changed by user/recipient himself unsubscribed";

					Messages messages = new Messages("WA Campaign ", "No Active contacts", msgStr,
							Calendar.getInstance(), "Inbox", false, "Info", user);
					messagesDaoForDML.saveOrUpdate(messages);

					if (!isSegment)
						isListEmpty = true;
					logger.info("isListEmpty" + isListEmpty);
					// TODO Utility..sendCampaignFailureAlertMailToSupport(user, waCampaign.getWaCampaignName(), "", "",waCampaignSchedule.getScheduledDate(), "Configured List of contacts/Segment not available","", "", "");
					waCampaignReport.setStatus(Constants.CR_STATUS_SENT);
					waCampaignReportDaoForDML.saveOrUpdate(waCampaignReport);
					
					WACampReportLists waCampReportLists = new WACampReportLists(
							waCampaignReport.getWaCrId());
					waCampReportLists.setListsName(listsName);
					waCampReportLists.setSegmentQuery(segmentListQuery);
					waCampReportListsDaoForDML.saveOrUpdate(waCampReportLists);
					waCampaignSchedule.setStatus((byte) 1);
					waCampaignSchedule.setCrId(waCampaignReport.getWaCrId());
					waCampaignScheduleDaoForDML.saveOrUpdate(waCampaignSchedule);

					waCampaignsDaoForDML.updateWACampaignStatus(waCampaign.getWaCampaignId());

				} catch (Exception e) {
					logger.error("Exception >>>>>> ", e);
				}

				return;
			}


			if (totalAvailableTempContacts <= 0 || totalAvailableTempContacts < totalContacts) {

				Messages messages = new Messages("WA Campaign", "Some of the contacts are 'Suppressed'",
						"Some of the contacts are 'Suppressed' or \n 'duplicate numbers'," + "WA name '"
								+ waCampaignName + "' can only be sent to " + totalAvailableTempContacts + " of total "
								+ totalContacts + " contacts.",
								Calendar.getInstance(), "Inbox", false, "Info", waCampaign.getUsers());

				messagesDaoForDML.saveOrUpdate(messages);
				if (totalAvailableTempContacts <= 0) {

					// TODO need to create report for it with all 0 counts....
					logger.info("num of contacts in wa_tempcontacts is " + totalAvailableTempContacts);

					try {

						WACampaignReport waCampaignReport = null;

						waCampaignReport = new WACampaignReport(waCampaign.getUsers(), waCampaignName, jsonContent,
								Calendar.getInstance(), 0, 0, 0, 0, 0, Constants.CR_STATUS_SENDING, fromSource);

						logger.warn(">>>>>>> wa campaign submission is failed found 0 active contacts, campaign id : "
								+ waCampaign.getWaCampaignId());

						// TODO Utility..sendCampaignFailureAlertMailToSupport(user, waCampaign.getWaCampaignName(), "", "",waCampaignSchedule.getScheduledDate(), "Configured List of contacts/Segment not available","", "", "");

						waCampaignReport.setStatus(Constants.CR_STATUS_SENT);
						waCampaignReportDaoForDML.saveOrUpdate(waCampaignReport);

						waCampaignSchedule.setStatus((byte) 1);
						waCampaignSchedule.setCrId(waCampaignReport.getWaCrId());
						waCampaignScheduleDaoForDML.saveOrUpdate(waCampaignSchedule);

						waCampaignsDaoForDML.updateWACampaignStatus(waCampaign.getWaCampaignId());
						
						WACampReportLists waCampReportLists = new WACampReportLists(
								waCampaignReport.getWaCrId());
						waCampReportLists.setListsName(listsName);
						waCampReportLists.setSegmentQuery(segmentListQuery);
						//smsCampReportListsDao.saveOrUpdate(smsCampReportLists);
						waCampReportListsDaoForDML.saveOrUpdate(waCampReportLists);

					} catch (Exception e) {
						logger.error("Exception >>>>>> ", e);
					}

					return;
				}

			}

			//jsonContent = jsonContent.replace("|^", "[").replace("^|", "]"); //for SMS			
			jsonContent = jsonContent.replace("|^", "<").replace("^|", ">"); // for whatsapp, bcz [] not worked
			// prepares the totalphset
			getCustomFields(jsonContent);

			Set<String> datePhSet = getDateFields(jsonContent);
			if (datePhSet != null && datePhSet.size() > 0) {

				for (String symbol : datePhSet) {
					if (symbol.startsWith(Constants.DATE_PH_DATE_)) {
						if (symbol
								.equalsIgnoreCase(Constants.DATE_PH_DATE_today)) {
							Calendar cal = MyCalendar.getNewCalendar();
							jsonContent = jsonContent
									.replace(
											"<" + symbol + ">",
											MyCalendar
											.calendarToString(
													cal,
													MyCalendar.FORMAT_DATEONLY_COMPLETE_MONTH));
						} else if (symbol
								.equalsIgnoreCase(Constants.DATE_PH_DATE_tomorrow)) {
							Calendar cal = MyCalendar.getNewCalendar();
							cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
							jsonContent = jsonContent
									.replace(
											"<" + symbol + ">",
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
								jsonContent = jsonContent
										.replace(
												"<" + symbol + ">",
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
			int totalSizeInt = contactsDao
					.getAvailableContactsFromWATempContacts(mlStr,"");

			String msgStr = "";
			Calendar cal = Calendar.getInstance();


			if(!useMQS) {

				boolean success = true;
				boolean canProceed = true;
				boolean cccountflag = false;
				boolean isInvalidCoupFlag = false;
				boolean couponDeleted=false;
				List<Integer> list = new ArrayList<Integer>();
				try {
					/***** CASE1: try to check the wa Count of user ***/ //not required for whatsapp

					/***** CASE2: make a decission whether coupon codes will be sufficient or not ***/

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

								msgStr = "WA campaign Name : "
										+ waCampaign.getWaCampaignName()
										+ "\r\n";
								msgStr = msgStr
										+ " \r\n Status : Could not be sent \r\n";
								msgStr = msgStr
										+ "WA campaign '"
										+ waCampaign.getWaCampaignName()
										+ "' could not be sent as you have added  Promotion: "
										+ eachPh + " \r\n";
								msgStr = msgStr
										+ "This  Promotion no longer exists, you might have deleted it.  \r\n";

								if (logger.isWarnEnabled())
									logger.warn(eachPh
											+ "  Promotion is not avalable: "
											+ eachPh);

								couponDeleted=true;

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
								isInvalidCoupFlag = true;
								msgStr = "WhatsApp campaign Name : "
										+ waCampaign.getWaCampaignName()
										+ "\r\n";
								msgStr = msgStr
										+ " \r\n Status : Could not be sent \r\n";
								msgStr = msgStr
										+ "WhatsApp campaign could not be sent as you have added  Promotion: "
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
									msgStr = "WhatsApp campaign Name : "
											+ waCampaignName + "\r\n";
									msgStr = msgStr
											+ " \r\n Status : Could not be sent \r\n";
									msgStr = msgStr
											+ "WhatsApp campaign could not be sent as you have added  Promotion : "
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

					//TODO	
					/*** Authentication check before submit***/


				} catch (Exception e) {

					if (logger.isErrorEnabled())
						logger.error(
								"** Exception while getting the available count"
										+ " for the user:" + user.getUserId(), e);
					success = false;
					return;
				} finally {

					if (!success && canProceed) {

						if (logger.isInfoEnabled())
							logger.info(">>>>>>>>>>..." + msgStr + " " + user + " ");

						if (cccountflag)
							waCampaignSchedule.setStatus((byte) 4);
						else if (isInvalidCoupFlag)
							waCampaignSchedule.setStatus((byte) 5);
						else if(couponDeleted)
							waCampaignSchedule.setStatus((byte) 8);
						else
							waCampaignSchedule.setStatus((byte) 3);

						waCampaignScheduleDaoForDML.saveOrUpdate(waCampaignSchedule);

						if (msgStr != null && msgStr.isEmpty()) {

							msgStr = "WhatsApp campaign sending failed due to some error in server connections.";
						}
						Messages msgs = new Messages("Send WhatsApp Campaign",
								"WhatsApp campaign sending failed ", msgStr, cal,
								"Inbox", false, "Info", user);

						//del wa temp contacts
						contactsDaoForDML.deleteWATempContacts();
						contactsDaoForDML.deleteWASegmentedContacts(waCampaign.getWaCampaignId(), waCampaignSchedule.getWaCsId(),"WA");
						messagesDaoForDML.saveOrUpdate(msgs);
						//TODO Utility.sendCampaignFailureAlertMailToSupport();

					}

				}// finally

			}//useMQS

			WACampaignReport waCampaignReport = null;

			cal = Calendar.getInstance();
			waCampaignReport = new WACampaignReport(waCampaign.getUsers(), waCampaignName, jsonContent, cal, 0, 0, 0,
					0, 0, Constants.CR_STATUS_SENDING, fromSource);
			try {
				waCampaignReportDaoForDML.saveOrUpdate(waCampaignReport);
				
				WACampReportLists waCampReportLists = new WACampReportLists(
						waCampaignReport.getWaCrId());
				waCampReportLists.setListsName(listsName);
				waCampReportLists.setSegmentQuery(segmentListQuery);
				//smsCampReportListsDao.saveOrUpdate(smsCampReportLists);
				waCampReportListsDaoForDML.saveOrUpdate(waCampReportLists);
			} catch (Exception e) {
				logger.error(" ** Exception while saving the WACampaignReport object");

			}

			//int creditsToBeDeduct = 0;

			WARecipientProvider waRecipientProvider = new WARecipientProvider(applicationContext, waCampaignReport,
					totalSizeInt, totalPhSet);
			//waRecipientProvider.setTempCount(creditsToBeDeduct);
			ReplacePlaceHolders replacePlaceHolders = new ReplacePlaceHolders(applicationContext, orgId,
					waCampaignName);
			// Create threads to submit campaign
			List<Thread> threadsList = new ArrayList<Thread>();
			try {
				logger.info("<<< STARTING THE THREADS >>>>");
				for (int i = 0; i < NUMBER_OF_WA_THREADS; i++) {

					Thread th = new WAMultiThreadedSubmission(waRecipientProvider, user, waCampaign, jsonContent,templateProvider,
							waCampaignReport, totalPhSet,
							applicationContext, replacePlaceHolders);
					th.setName("thread_campaignWA:" + (i));
					threadsList.add(th);
					th.start();
				}

			} catch (Exception e) {
				logger.error("Exception : ERROR OCCURED WHILE CREATING THREADS ...", e);
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

			//creditsToBeDeduct = waRecipientProvider.getTempCount();

			logger.info("<<< ALL THREAD EXECUTED SUCCESSFULLY >>>>");

			waCampaignReport.setStatus(Constants.CR_STATUS_SENT);
			waCampaignReport.setSent(waCampaignSentDao.getSentCount(waCampaignReport.getWaCrId())); //submiited to msgBird
			waCampaignReport.setConfigured(configuredCount); //in wa_tempContacts
			waCampaignReport.setSuppressedCount(suppressedCount);
			waCampaignReport.setPreferenceCount(preferenceCount);
			//logger.info("setting credits count: " + creditsToBeDeduct);
			//waCampaignReport.setCreditsCount(creditsToBeDeduct);
			waCampaignReportDaoForDML.saveOrUpdate(waCampaignReport);
			waCampaignReportDaoForDML.updateBounceReport(waCampaignReport.getWaCrId());
			Long notSubmittedCount = waCampaignSentDao.findCountOfNotSubmitted(waCampaignReport.getWaCrId());

			logger.info("Not submitted count:" + notSubmittedCount + " for this cr id :" + waCampaignReport.getWaCrId());
			logger.debug("bounced count ::" + waCampaignReport.getBounces());

			waCampaignSchedule.setStatus((byte) 1);
			contactsDaoForDML.deleteWASegmentedContacts(waCampaign.getWaCampaignId(), waCampaignSchedule.getWaCsId(),"WA");
			if (notSubmittedCount > 0) {
				waCampaignSchedule.setStatus((byte) 10);
				Messages messages = new Messages("WA Details ", "WA Partially failed", msgStr, cal, "Inbox", false,
						"Info", user);
				messagesDaoForDML.saveOrUpdate(messages);
				//TODO	Utility.sendNotSubmittedAlertMailToSupport(..);
			}
			waCampaignSchedule.setCrId(waCampaignReport.getWaCrId());
			waCampaignScheduleDaoForDML.saveOrUpdate(waCampaignSchedule);
			waCampaignsDaoForDML.updateWACampaignStatus(waCampaign.getWaCampaignId());

			if (!useMQS) {

				msgStr = "WhatsApp Name :" + waCampaignName + "\n"
						+ "Sent successfully to " + waCampaignReport.getSent()
						+ " unique contacts.";

				Messages messages = new Messages("WhatsApp Details ",
						"WhatsApp sent successfully", msgStr, cal, "Inbox", false,
						"Info", user);
				messagesDaoForDML.saveOrUpdate(messages);

				try {
					Utility.updateCouponCodeCounts(applicationContext, totalPhSet);
				} catch (Exception e) {
					logger.error("** Exception while updating the coupons", e);
				}
			}

		} catch (Exception e) {
			logger.error("** Error occured while submitting the WA campaigns", e);
		}
		logger.debug(">>>>>>> Completed WACampaignSubmitter :: sendWACampaign <<<<<<< ");
	} // addWACampaign


	private Set<String> totalPhSet;

	public Set<String> getTotalPhSet() {
		return totalPhSet;
	}

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


	}
	private void updateSegmentCountAndQuery(SegmentRules segmentRule,
			String generatedQuery, String generatedCountQuery) {
		logger.info("<<<<<<<<<<<< updateSegmentCountAndQuery called >>>>>>>>>>>>>>>");
		long size = 0;
		size = contactsDao.getSegmentedContactsCount(generatedCountQuery);

		segmentRule.setTotMobileSize(size);
		segmentRule.setMobileSegQuery(generatedQuery);
		segmentRule.setLastRefreshedOn(Calendar.getInstance());

		segmentRulesDaoForDML.saveOrUpdate(segmentRule);

	}

	private String getCustomFields(String content) {

		//String cfpattern = "\\[([^\\[]*?)\\]"; // for SMS
		String cfpattern = "\\<([^\\[]*?)\\>"; //for whatsapp bcz [] not worked

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
					logger.debug("coupon code found :"+ph);
					totalPhSet.add(ph);

				}else if (ph.startsWith("REF_CC_")) {
					totalPhSet.add(ph);

				}else if (ph.startsWith("CF_")) {
					totalPhSet.add(ph);
					cfNameListStr = cfNameListStr
							+ (cfNameListStr.equals("") ? ""
									: Constants.DELIMETER_COMMA) + "'"
									+ ph.substring(3) + "'";
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
	}//getCustomFields

	private Set<String> getDateFields(String content) {

		//content = content.replace("|^", "[").replace("^|", "]");
		content = content.replace("|^", "<").replace("^|", ">");//for whatsapp [] wont work

		//String cfpattern = "\\[([^\\[]*?)\\]";
		String cfpattern = "\\<([^\\[]*?)\\>";
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

			logger.debug("+++ Exiting : "+ totalPhSet);
		} catch (Exception e) {
			logger.error("Exception while getting the symbol place holders ", e);
		}

		if (logger.isInfoEnabled())
			logger.info("symbol PH  Set : " + dateFieldsSet);

		return dateFieldsSet;
	}//getDateFields

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

			}// if

			else if (cfStr.toLowerCase().startsWith(
					PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE) || 
					cfStr.startsWith(PlaceHolders.CAMPAIGN_ADDRESS_PH_STARTSWITH_HOMESTORE_ADDRESS)
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

	}//examineStorePH

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



} // class
