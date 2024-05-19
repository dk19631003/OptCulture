package org.mq.captiway.scheduler;

import java.io.File;
import java.lang.reflect.Method;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.AutoProgram;
import org.mq.captiway.scheduler.beans.AutoProgramComponents;
import org.mq.captiway.scheduler.beans.CampReportLists;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.beans.CampaignSchedule;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.ComponentsAndContacts;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.CouponCodes;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.beans.CustomFieldData;
import org.mq.captiway.scheduler.beans.CustomTemplates;
import org.mq.captiway.scheduler.beans.EmailContent;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.EventTrigger;
import org.mq.captiway.scheduler.beans.MLCustomFields;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.MyTemplates;
import org.mq.captiway.scheduler.beans.OptInReport;
import org.mq.captiway.scheduler.beans.ProgramOnlineReports;
import org.mq.captiway.scheduler.beans.SMSCampaignSchedule;
import org.mq.captiway.scheduler.beans.SMSCampaigns;
import org.mq.captiway.scheduler.beans.SegmentRules;
import org.mq.captiway.scheduler.beans.UserVmta;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.beans.UsersDomains;
import org.mq.captiway.scheduler.beans.Vmta;
import org.mq.captiway.scheduler.dao.CampReportListsDao;
import org.mq.captiway.scheduler.dao.CampReportListsDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignReportDao;
import org.mq.captiway.scheduler.dao.CampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignScheduleDao;
import org.mq.captiway.scheduler.dao.CampaignScheduleDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.CampaignsDaoForDML;
import org.mq.captiway.scheduler.dao.ComponentsAndContactsDao;
import org.mq.captiway.scheduler.dao.ComponentsAndContactsDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.dao.CustomFieldDataDao;
import org.mq.captiway.scheduler.dao.CustomFieldDataDaoForDML;
import org.mq.captiway.scheduler.dao.CustomTemplatesDao;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.EventTriggerDao;
import org.mq.captiway.scheduler.dao.EventTriggerDaoForDML;
import org.mq.captiway.scheduler.dao.MLCustomFieldsDao;
import org.mq.captiway.scheduler.dao.MailingListDao;
import org.mq.captiway.scheduler.dao.MessagesDao;
import org.mq.captiway.scheduler.dao.MessagesDaoForDML;
import org.mq.captiway.scheduler.dao.MyTemplatesDao;
import org.mq.captiway.scheduler.dao.OptInReportDao;
import org.mq.captiway.scheduler.dao.OptInReportDaoForDML;
import org.mq.captiway.scheduler.dao.ProgramOnlineReportsDao;
import org.mq.captiway.scheduler.dao.ProgramOnlineReportsDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignsDao;
import org.mq.captiway.scheduler.dao.SegmentRulesDao;
import org.mq.captiway.scheduler.dao.SegmentRulesDaoForDML;
import org.mq.captiway.scheduler.dao.TempActivityDataDao;
import org.mq.captiway.scheduler.dao.TempActivityDataDaoForDML;
import org.mq.captiway.scheduler.dao.TempComponentsDataDao;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.dao.UsersDaoForDML;
import org.mq.captiway.scheduler.dao.VmtaDao;
import org.mq.captiway.scheduler.services.ExternalSMTPSender;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.CouponProvider;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PrepareFinalHTML;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.captiway.scheduler.utility.QueryGenerator;
import org.mq.captiway.scheduler.utility.SalesQueryGenerator;
import org.mq.captiway.scheduler.utility.TriggerQueryGenerator;
import org.mq.captiway.scheduler.utility.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DeadlockLoserDataAccessException;


public class PmtaMailmergeSubmitter extends Thread {

	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private volatile static boolean isRunning = false;

	private ApplicationContext context;
	private CampaignsDao campaignsDao;
	private CampaignsDaoForDML campaignsDaoForDML;
	private MessagesDao messagesDao;
	private MessagesDaoForDML messagesDaoForDML;
	private PMTAQueue queue;
	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;
	private CampaignScheduleDao campaignScheduleDao;
	private CampaignScheduleDaoForDML campaignScheduleDaoForDML;
	private EventTriggerDao eventTriggerDao; // added for Event Trigger
	private EventTriggerDaoForDML eventTriggerDaoForDML;
	private ContactsDao contactsDao; // added for EventTrigger
	private ContactsDaoForDML contactsDaoForDML;
	private CustomFieldDataDao customFieldDataDao;
	private CustomFieldDataDaoForDML customFieldDataDaoForDML;

	private MLCustomFieldsDao mlCustomFieldsDao;
	private CampReportListsDao campReportListsDao;
	private CampReportListsDaoForDML campReportListsDaoForDML;
	private ProgramOnlineReportsDao programOnlineReportsDao;
	private ProgramOnlineReportsDaoForDML programOnlineReportsDaoForDML;

	private SMSCampaignsDao smsCampaignsDao;
	private TempComponentsDataDao tempComponentsDataDao;
	private SegmentRulesDao segmentRulesDao;
	private SegmentRulesDaoForDML segmentRulesDaoForDML;
	private MailingListDao mailingListDao;
	
	private TempActivityDataDao tempActivityDataDao;//added for Auto Responder
	private TempActivityDataDaoForDML tempActivityDataDaoForDML;//added for Auto Responder
	private ComponentsAndContactsDao componentsAndContactsDao;//added for Auto Responder
	private ComponentsAndContactsDaoForDML componentsAndContactsDaoForDML;//added for Auto Responder

	private boolean isOpensFlagSet;
	private boolean isClicksFlagSet;
	private boolean isCdateFlagSet;
	private boolean isSdateFlagSet;
	//private boolean isMergeCFSet;
	private boolean isSendCampaignFlagSet;
	
	static final String UNSUBSCRIBE_URL = PropertyUtil.getPropertyValue("unSubscribeUrl");

	private CampaignReportDao campaignReportDao;
	private CampaignReportDaoForDML campaignReportDaoForDML;

	private static Class strArg[] = new Class[]{String.class};
	private static Class longArg[] = new Class[]{Long.class};
	private static Class contactArg[] = new Class[]{Contacts.class};
	private static Set<String> totalPhSet;
	private String fromSource; //added for EventTrigger
	
	//public static boolean isCampaignSchedule;  
	
	//EventTrigger sms changes begins
	private SMSQueue smsQueue;

	public SMSQueue getSmsQueue() {
		return smsQueue;
	}

	public void setSmsQueue(SMSQueue smsQueue) {
		this.smsQueue = smsQueue;
	}
	
	//EventTrigger sms changes ends

	private static PmtaMailmergeSubmitter mailSubmitter;
	
	private PmtaMailmergeSubmitter() {
	}
	
	public static synchronized void startPmtaMailmergeSubmitter(ApplicationContext context) {
		
		if(mailSubmitter==null || !mailSubmitter.isRunning()) {
			
			mailSubmitter=new PmtaMailmergeSubmitter(context);
			mailSubmitter.start();
		}
	}
	
	private PmtaMailmergeSubmitter(PMTAQueue queue){
		this.queue = queue;
	}

	//private MultiThreaded senderThread;

	private PmtaMailmergeSubmitter(ApplicationContext context) {

		this.context = context;
		queue = (PMTAQueue)context.getBean("pmtaQueue");
		smsQueue = (SMSQueue)context.getBean("smsQueue"); //added for EventTrigger sms
		campaignsDao = (CampaignsDao)context.getBean("campaignsDao");
		campaignsDaoForDML = (CampaignsDaoForDML)context.getBean("campaignsDaoForDML");
		messagesDao = (MessagesDao)context.getBean("messagesDao");
		messagesDaoForDML = (MessagesDaoForDML)context.getBean("messagesDaoForDML");
		usersDao = (UsersDao)context.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML)context.getBean("usersDaoForDML");
		campaignScheduleDao = (CampaignScheduleDao)context.getBean("campaignScheduleDao");
		campaignScheduleDaoForDML = (CampaignScheduleDaoForDML)context.getBean("campaignScheduleDaoForDML");
		eventTriggerDao = (EventTriggerDao)context.getBean("eventTriggerDao"); //added for Event Trigger
		eventTriggerDaoForDML = (EventTriggerDaoForDML)context.getBean("eventTriggerDaoForDML");
		contactsDao = (ContactsDao)context.getBean("contactsDao"); //added for EventTrigger
		contactsDaoForDML = (ContactsDaoForDML)context.getBean("contactsDaoForDML"); //added for EventTrigger
		//senderThread = new MultiThreaded(context);
		customFieldDataDao = (CustomFieldDataDao)context.getBean("customFieldDataDao");
		customFieldDataDaoForDML = (CustomFieldDataDaoForDML)context.getBean("customFieldDataDaoForDML");

		tempActivityDataDao = (TempActivityDataDao)context.getBean("tempActivityDataDao");
		tempActivityDataDaoForDML = (TempActivityDataDaoForDML)context.getBean("tempActivityDataDaoForDML");
		componentsAndContactsDao = (ComponentsAndContactsDao)context.getBean("componentsAndContactsDao");
		componentsAndContactsDaoForDML = (ComponentsAndContactsDaoForDML)context.getBean("componentsAndContactsDaoForDML");
		campReportListsDao = (CampReportListsDao)context.getBean("campReportListsDao");
		campReportListsDaoForDML = (CampReportListsDaoForDML)context.getBean("campReportListsDaoForDML");
		programOnlineReportsDao = (ProgramOnlineReportsDao)context.getBean("programOnlineReportsDao");
		programOnlineReportsDaoForDML = (ProgramOnlineReportsDaoForDML)context.getBean("programOnlineReportsDaoForDML");

		smsCampaignsDao = (SMSCampaignsDao)context.getBean("smsCampaignsDao");
		tempComponentsDataDao = (TempComponentsDataDao)context.getBean("tempComponentsDataDao");
		segmentRulesDao = (SegmentRulesDao)context.getBean("segmentRulesDao");
		mailingListDao = (MailingListDao)context.getBean("mailingListDao");
		segmentRulesDaoForDML = (SegmentRulesDaoForDML)context.getBean("segmentRulesDaoForDML");
	}

	public static Set<String> getTotalPhSet(){
		return totalPhSet;
	}

	public synchronized boolean  isRunning() {
		return isRunning;
	}

	public synchronized void setIsRunning(boolean newIsRunning) {
		isRunning=newIsRunning;
	}

	private static Object currentRunningObj= null;
	
	public static Object getCurrentRunningObj() {
		return currentRunningObj;
	}

	/**
	 * Run method
	 */
	public void run() {
		if(logger.isInfoEnabled()) logger.info("-------------- just entered -----------");
		
		//isRunning = true;
		setIsRunning(true);
		
		campaignReportDao = (CampaignReportDao)context.getBean("campaignReportDao");
		campaignReportDaoForDML = (CampaignReportDaoForDML)context.getBean("campaignReportDaoForDML");
		Object obj = null;
		MailingList mailingList = null;
		

		while((currentRunningObj = obj = queue.getObjFromQueue()) != null) {

			try {

				/*Utility.processExternalSMTPEvents(context);
				
				Utility.processCampaignReportEvents(context);*/
				
				if(obj instanceof CampaignSchedule) {
					//everytime when the campaignscheduler runs its already being processed hence not needed here
					/*try {
						Utility.processExternalSMTPEvents(context);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						logger.error("Exception while updating the bounces");
					}*/
					
					
					CampaignSchedule campSchedule = (CampaignSchedule)obj;
					
					
					if(checkAnyOtherObjectRunning()) { //need to check for safety 
						/*if(currentRunningObj instanceof CampaignSchedule) {
							CampaignSchedule currSchedule = (CampaignSchedule)currentRunningObj; */
						currentRunningObj = new Object();
						//}
						List<CampaignSchedule> rePutScheduleList = new ArrayList<CampaignSchedule>();
						rePutScheduleList.add(campSchedule);
						queue.addCollection(rePutScheduleList);
						continue;
						
					}//if
					
					//TODO check once again Campaign Schedule  Status Object in DB
					
					boolean isScheduledCamp = campaignScheduleDao.isScheduledCampaignByCSId(campSchedule.getCsId());
					if(logger.isInfoEnabled()) logger.info(">>>>>> >>>>>>>>>"+isScheduledCamp +"  for Camapign SchedulId is >>"+campSchedule.getCsId());
					
					if(!isScheduledCamp) {
						Messages messages = new Messages("Campaigns", "Campaign Schedule" , 
								"The scheduled campaign is canceled since it is in draft status..",
								Calendar.getInstance(),  "Inbox", false, "Info", campSchedule.getUser());
						messagesDaoForDML.saveOrUpdate(messages);
						continue;
					}
					
					fromSource = Constants.SOURCE_CAMPAIGN; //added for EventTrigger
					if(logger.isInfoEnabled()) logger.info("fromSource: "+fromSource);
					try {

						Campaigns campaign = campaignsDao.findByCampaignId(campSchedule.getCampaignId());

						if(campaign == null || campaign.getStatus().equals(Constants.CAMP_STATUS_DRAFT)) {
							Utility.sendCampaignFailureAlertMailToSupport(campaign.getUsers(),"",campaign.getCampaignName(),"",campSchedule.getScheduledDate(),
									"Schedule Failure due to Campaign Draft status","","","");
							if(logger.isWarnEnabled()) logger.warn("********** No campaign object found for campaignId :"+	campSchedule.getCampaignId());
							continue;
						}

						if(logger.isInfoEnabled()) logger.info(">>>>>>"+campaign.getCampaignName() +" is started to send");

						//isCampaignSchedule = true;
						contactsDaoForDML.deleteTempContacts();
						processCampaign(campaign, campSchedule);
						contactsDaoForDML.deleteTempContacts();
						if(logger.isInfoEnabled()) logger.info(">>>>>>"+campaign.getCampaignName() +" processing is completed");
					}
					catch (Exception e) {
						logger.error("** Exception while getting the campaign by campaignId :"
								+campSchedule.getCampaignId(), e);
					}
				}
				else if(obj instanceof MailingList) {

					//isCampaignSchedule = false;
					mailingList = (MailingList)obj;

					if(checkAnyOtherObjectRunning()) { //need to check for safety 
						currentRunningObj = new Object();
						List<MailingList> rePutMLList = new ArrayList<MailingList>();
						rePutMLList.add(mailingList);
						queue.addCollection(rePutMLList);
						continue;
						
					}//if
					
					
					// Return if mailing list is under going purge process .
					if(mailingList.getStatus()!= null && 
							mailingList.getStatus().equalsIgnoreCase(Constants.MAILINGLIST_STATUS_PURGING)) {
						Messages messages = new Messages("Double Opt-in", "Double opt-in pending" , 
								"Mailing list is under purge process, Double opt-in mails will be" +
								" sent once purging is completed.",
								Calendar.getInstance(),  "Inbox", false, "Info", mailingList.getUsers());
						messagesDaoForDML.saveOrUpdate(messages);
						return;
					}
					
					String qryStr =
			    		" INSERT IGNORE INTO tempcontacts ("+Constants.QRY_COLUMNS_TEMP_CONTACTS+", cf_value, domain, event_source_id)  "+
			    		" (SELECT distinct "+Constants.QRY_COLUMNS_CONTACTS+",null, SUBSTRING_INDEX(c.email_id, '@', -1), null FROM contacts c WHERE c.user_id="+mailingList.getUsers().getUserId()+
			    		" AND (c.mlbits & "+mailingList.getMlBit()+")>0 AND c.email_status = " +
			    		" '" + Constants.CONT_STATUS_OPTIN_PENDING +"' AND (c.optin IS null OR c.optin<3) " +
			    		" AND (c.last_mail_date is null OR (DATEDIFF(now(), c.last_mail_date) >7)) " +
			    		" ORDER BY c.cid)";

					int count = contactsDaoForDML.createTempContacts(qryStr, null,
							mailingList.getUsers().getUserId());

			    	if(count <= 0) {
			    		continue;
			    	}

					String contentStr = "";
					Users mlUser = mailingList.getUsers();
					/*if(mlUser.getParentUser() != null) {
						
						mlUser = mlUser.getParentUser();
					}*/

					CustomTemplatesDao customTemplatesDao;
					customTemplatesDao = (CustomTemplatesDao)context.getBean("customTemplatesDao");
					
					Long templateId = mailingList.getCustTemplateId();
					CustomTemplates customTemplate = null;
					contentStr = PropertyUtil.getPropertyValueFromDB("optinMsgTemplate");
					if(templateId  != null) {

						customTemplate = customTemplatesDao.findCustTemplateById(templateId);
						if(customTemplate != null) {
							if(customTemplate.getHtmlText()!= null && !customTemplate.getHtmlText().isEmpty()) {
								contentStr = customTemplatesDao.getTemplateHTMLById(templateId);
							}else if(Constants.EDITOR_TYPE_BEE.equalsIgnoreCase(customTemplate.getEditorType()) && customTemplate.getMyTemplateId()!=null) {
							  MyTemplatesDao myTemplatesDao= (MyTemplatesDao)context.getBean("myTemplatesDao");
							  MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplate.getMyTemplateId());
							  if(myTemplates!=null) {
								  contentStr = myTemplates.getContent();
							  }
							}
					  }
						
					}
					String userDomainStr = "";
					if(mlUser != null) {
						List<UsersDomains> domainsList = null;
						
						UsersDao usersDao = (UsersDao)context.getBean("usersDao");;
						UsersDaoForDML usersDaoForDML = (UsersDaoForDML)context.getBean("usersDaoForDML");
						domainsList = usersDao.getAllDomainsByUser(mlUser.getUserId());
						Set<UsersDomains> domainSet = new HashSet<UsersDomains>();//currentUser.getUserDomains();
						if(domainsList != null) {
							domainSet.addAll(domainsList);
							for (UsersDomains usersDomains : domainSet) {
								
								if(userDomainStr.length()>0) userDomainStr+=",";
								userDomainStr += usersDomains.getDomainName();
								
							}
						}
					
					}
					
					
					
					contentStr = PrepareFinalHTML.prepareDoubleOptInStuff(customTemplate, false,contentStr, mlUser, userDomainStr);
					
					
					String senderName = (mlUser.getFirstName() != null ) ?
							mlUser.getFirstName() : mlUser.getUserName();
							if(logger.isDebugEnabled()) {
								logger.info(">>>>>>>>>> senderName to replace as :"+senderName);
							}

							contentStr = contentStr.replace("[senderName]", senderName);
							contentStr = contentStr.replace("[OrganizationName]", mlUser.getUserOrganization().getOrganizationName());
							contentStr = contentStr.replace("[OrganisationName]", mlUser.getUserOrganization().getOrganizationName());
							/*String confirmOptinUrlStr = null;
							if(mailingList.getCustTemplateId() == null) {
								confirmOptinUrlStr =  PropertyUtil.getPropertyValue("confirmOptinUrl");
							}
							
							//TODO need to discuss if needed commented due to error
							else {
								confirmOptinUrlStr = "<a href='" + PropertyUtil.getPropertyValue("confirmOptinUrl") + "'>" +
								PropertyUtil.getPropertyValue("confirmOptinUrl") + "</a>";
							}*/

							
							contentStr = contentStr.replace("[url]", PropertyUtil.getPropertyValue("confirmOptinUrl"));
							if(logger.isInfoEnabled()) logger.info("content Str is===>"+contentStr);
							
							OptInReport optInReport = new OptInReport(contentStr);
							OptInReportDao optInReportDao = (OptInReportDao)context.getBean("optInReportDao");
							OptInReportDaoForDML optInReportDaoForDML = (OptInReportDaoForDML)context.getBean("optInReportDaoForDML");

							//optInReportDao.saveOrUpdate(optInReport);
							optInReportDaoForDML.saveOrUpdate(optInReport);

							
							contentStr = contentStr.replace("[optRepId]", optInReport.getOptRepId()+"");
							
							
							
							getCustomFields(contentStr);
					
							String msgStr = null;
					    	Calendar cal = Calendar.getInstance();
					    	
					    	//make a decission whether coupon codes will be sufficient or not
							CouponCodesDao couponCodesDao = (CouponCodesDao)context.getBean("couponCodesDao");
							CouponsDao couponsDao = (CouponsDao)context.getBean("couponsDao");
							boolean success=true;
							
							if(totalPhSet != null && totalPhSet.size() > 0) {
								try{
								
								Coupons coupon = null;
								Long couponId = null; 
								for (String eachPh : totalPhSet) {
									if(!eachPh.startsWith("CC_") ) continue;
									//only for CC
										
										
										String[] strArr = eachPh.split("_");
										
										if(logger.isDebugEnabled()) logger.debug("Filling  Promo-code with Id = "+strArr[1]);
										try {
											
											couponId = Long.parseLong(strArr[1]);
											
										} catch (NumberFormatException e) {
											
											couponId = null;
										
										}
										
										if(couponId == null) {
											
											//TODO need to delete it from phset or???????????????????????
											continue;
											
										}
										coupon = couponsDao.findById(couponId);
										if(coupon == null) {
											
											msgStr =  "Failed to send Opt-in mails for the mailing List " + mailingList.getListName() + "\r\n"+
													"as you have added  Promotion : "+eachPh +" \r\n ";
											msgStr = msgStr + "This  Promotion is no longer exists, you might have deleted that. : \r\n";
											
											if(logger.isWarnEnabled()) logger.warn(eachPh + "  Promo-code is not avalable: "+ eachPh);
											success = false;
											break;
											
											
										}
										
										
										
										//only for running coupons
//										if(!coupon.getStatus().equals(Constants.COUP_STATUS_RUNNING)) {
										if(coupon.getStatus().equals(Constants.COUP_STATUS_EXPIRED) || 
												coupon.getStatus().equals(Constants.COUP_STATUS_PAUSED)) {
											
											msgStr = "Failed to send Opt-in mails for the mailing List " +
													mailingList.getListName() + ".\r\n " +"as you have added  Promotion : "+coupon.getCouponName() +" \r\n "
													+" This  Promotion's Status :"+coupon.getStatus()+" and  valid period :"+ 
											MyCalendar.calendarToString(coupon.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STDATE) +" to "+
											MyCalendar.calendarToString(coupon.getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STDATE) +" \r\n";
										
											
											/*msgStr =  "Email campaign Name : " + campaign.getCampaignName() + "\r\n";
											msgStr = msgStr +" \r\n Status : Could not sent \r\n";
											msgStr = msgStr + "Email campaign could not be sent as you have added coupon : "+coupon.getCouponName() +" \r\n" ;
											msgStr = msgStr + "This coupon's Status :"+coupon.getStatus()+" and  valid period :"+ 
											MyCalendar.calendarToString(coupon.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STDATE) +" to "+
											MyCalendar.calendarToString(coupon.getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STDATE) +" \r\n";
											*/
											success = false;
											if(logger.isWarnEnabled()) logger.warn(coupon.getCouponName() + "  Promotion is not in running state, Status : "+ coupon.getStatus());
											break;

											
											
										}//if
										
										
										if( coupon.getAutoIncrCheck() == true ) {
											continue;
										}
										else if(coupon.getAutoIncrCheck() == false) {
											//need to decide only when auto is false
											//List<Integer> couponCodesList = couponCodesDao.getInventoryCCCountByCouponId(couponId);
											long couponCodeCount = couponCodesDao.getCouponCodeCountByStatus(couponId, Constants.COUP_CODE_STATUS_INVENTORY);
											if(couponCodeCount < count ) {
												
												
												msgStr = "Failed to send Opt-in mails for the mailing List " +
															mailingList.getListName() + ".\r\n " +"as you have added  Promotion : "+coupon.getCouponName() +" \r\n "
															+" Available  Promo-code you can send :"+couponCodeCount+" \r\n";
												
												
												/*msgStr =  "Email campaign Name : " + campaign.getCampaignName() + "\r\n";
												msgStr = msgStr +" \r\n Status : Could not sent \r\n";
												msgStr = msgStr + "Email campaign could not be sent as you have added coupon : "+coupon.getCouponName() +" \r\n" ;
												msgStr = msgStr + "Available Coupons you can send :"+couponCodeCount+" \r\n";*/
												success = false;
												if(logger.isWarnEnabled()) logger.warn(" Available  Promo-codes  limit is less than the configured contacts count");
												break;
											}
										
										}//else 
									
								}//for
								//success = true;
								
								
							}
							catch (Exception e) {
								
								logger.error("** Exception while getting the available count" +
										" for the user:"+mlUser.getUserId(), e);
								success = false;
								continue;
							}
							
							if(!success && msgStr != null && !msgStr.isEmpty()) {
								//optInReportDao.delete(optInReport);
								optInReportDaoForDML.delete(optInReport);
								String module = "Double Opt-in";
								String subject = "Double opt-in failed";
								//String qry = "FROM Messages WHERE users.userId="+mlUser.getUserId().longValue()+" AND subject='"+subject+"' AND module='"+module+"' AND message='"+StringEscapeUtils.escapeSql(msgStr)+"' AND DAY(createdDate) = DAY(NOW())";
								//logger.info("qry::"+qry);
								boolean isMsgExists = messagesDao.findSameMsgWithInSameDay(mlUser, msgStr, subject, module);
								
								
								//logger.info("ismsgExists::"+isMsgExists);
								if(!isMsgExists) {
									
									logger.info("msg::"+msgStr);
									Messages messages = new Messages(module, subject  , msgStr,
											Calendar.getInstance(),  "Inbox", false, "Info", mlUser);
									messagesDaoForDML.saveOrUpdate(messages);
									
								}
								if(logger.isDebugEnabled()) logger.debug("As  Promo-code's issue this optin mail can not be sent, picking next object.");
								
								continue;//with other QUEUE obkject
							}
							
					    }//if
							
							
					//logger.debug("content Str aftr prepared =====>"+contentStr);
				
					Vmta vmta= mlUser.getVmta();
					//String vmtaName  = vmta.getVmtaName();

					/*if(vmtaName != null && vmtaName.indexOf('.') != -1) {
						vmtaName = vmtaName.substring(0,vmtaName.indexOf('.'));
					}*/
					
					byte statusCode = -1;
					String msg = null;
					boolean flag =false;
					
					UsersDao usersDao = (UsersDao)context.getBean("usersDao");
					VmtaDao vmtaDao = (VmtaDao)context.getBean("vmtaDao");
					UserVmta userVmta = usersDao.findBy(mlUser.getUserId(), Constants.SENDING_TYPE_BULK);
					String vmtaDb = PropertyUtil.getPropertyValueFromDB("VMTA");
					String genericVmta = PropertyUtil.getPropertyValueFromDB("SingleVMTA");
					Vmta vmtaObj = null;
					
					if(userVmta != null) {
							vmtaObj = vmtaDao.find(userVmta.getVmtaId());
							
					}else {
						vmtaObj = vmtaDao.getGenericVmta(vmtaDb, genericVmta);
					}
					if(vmta == null) flag = false;
					else if(vmtaObj != null){
						
					/*	String vmtaDB = PropertyUtil.getPropertyValueFromDB("VMTA");
						if(vmtaDB.equalsIgnoreCase(Constants.SMTP_SENDGRIDAPI)) { // after change.
						
*/		//			if(Constants.SMTP_SENDGRIDAPI.equalsIgnoreCase(vmtaObj.getVmtaName())) {
						// Send From SendGrid API .
						ExternalSMTPSender externalSMTPSender = new ExternalSMTPSender(vmtaObj, context);
						
						flag = externalSMTPSender.submitDoubleOptinEmails(mailingList, count,
								 contentStr, customTemplate,vmtaObj);
						
						if(flag) {
							if(logger.isInfoEnabled()) logger.info("** Confirm optin mails sent successfully for list "+mailingList.getListName());
							msg = "Confirm opt-in mails sent successfully for mailing list " +
									mailingList.getListName();
							
							try {
								Utility.updateCouponCodeCounts(context, totalPhSet);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("** Exception while updating the coupons", e);
							}
							
							
							
						} 
					} 
			//		}
					
					if(flag == false){
						if(logger.isInfoEnabled()) logger.info("** Confirm optin mails sent successfully");
						msg = "Failed to send Opt-in mails for the mailing List " +
								mailingList.getListName();
						
						//optInReportDao.delete(optInReport);
						optInReportDaoForDML.delete(optInReport);

					}
					/*else {	
					

							statusCode = senderThread.submitDoubleOptinMails(mailingList, count,
									vmta, contentStr);
		
							logger.info("deleting from tempContacts ");
							contactsDao.deleteTempContacts(); // Delete the tempcontacts
		
		
							if(statusCode == 0) {
		
								logger.info("** Confirm optin mails sent successfully");
								msg = "Confirm opt-in mails sent successfully for mailing list " +
										mailingList.getListName();
							}
							else {
		
								logger.info("** Confirm optin mails sent successfully");
								msg = "Failed to send Opt-in mails for the mailing List " +
										mailingList.getListName();
								
								optInReportDao.delete(optInReport);
							}
					
					}*/
					
					Messages messages = new Messages("Double Opt-in", (statusCode == 0 || flag == true)?
							"Double opt-in successful.":"Double opt-in failed" , msg,
							Calendar.getInstance(),  "Inbox", false, "Info", mlUser);
					messagesDaoForDML.saveOrUpdate(messages);
					contactsDaoForDML.deleteTempContacts();
				}
				else if (obj instanceof EventTrigger) {

					/*
					 * This part has been added for EventTrigger
					 */
					//isCampaignSchedule = true;
					EventTrigger eventTrigger = (EventTrigger)obj;
					
					if(checkAnyOtherObjectRunning()) { //need to check for safety 
						currentRunningObj = new Object();
						List<EventTrigger> rePutEventTriggerList = new ArrayList<EventTrigger>();
						rePutEventTriggerList.add(eventTrigger);
						queue.addCollection(rePutEventTriggerList);
						continue;
						
					}//if
					
					
					int optionsFlag;
					fromSource = Constants.SOURCE_TRIGGER;

					try {
						
						// validate user_id

						optionsFlag = eventTrigger.getOptionsFlag();
						Users userObj = usersDao.find(eventTrigger.getUsers().getUserId());

						if(logger.isInfoEnabled()) logger.info("optionsFlag = "+optionsFlag);

						if(userObj.getUserName() == null) {
							//this is needed if the user doesnot exists
							if(logger.isWarnEnabled()) logger.warn("**************EventTrigger:  No User object found for the given user_id :"+eventTrigger.getUsers().getUserId()+
							"continuing with the next object in pmtaQueue");
							continue;
						}

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");      
						Date currDate = sdf.parse(sdf.format(new Date()));
					    
					    Calendar cal = userObj.getPackageExpiryDate();
					    Date expiryDate = sdf.parse(sdf.format(cal.getTime()));
						
						
						if(!userObj.isEnabled() || expiryDate.before(currDate)) {
							if(logger.isWarnEnabled()) logger.warn("**************EventTrigger: User expired or deactivated, continuing with the next object in pmtaQueue");
							continue;
						}
						
						if((optionsFlag & Constants.ET_TRIGGER_IS_ACTIVE_FLAG) == Constants.ET_TRIGGER_IS_ACTIVE_FLAG) {
							//this is needed if the trigger is inactive
							if(logger.isInfoEnabled()) logger.info("calling processTrigger");
							contactsDaoForDML.deleteTempContacts();
							processEventTrigger(eventTrigger);

							if(logger.isInfoEnabled()) logger.info("After processTrigger clearing temp contacts table");
							
						
							
							contactsDaoForDML.deleteTempContacts();
						}
						else {
							
							if(logger.isInfoEnabled()) logger.info("the trigger is not active...it must be in draft stage..please make it active");
							continue;
						}

					}catch (Exception e){
						logger.error("** EventTrigger: Exception in eventTrigger object :"+eventTrigger, e);
					}
				}//else if
				
				
				else if(obj instanceof AutoProgramComponents) {
					/************to send the Program based Email Campaign***********/
					try{
						//isCampaignSchedule = true;
						if(logger.isInfoEnabled()) logger.info("got AutoProgramComponents object====>"+obj);
						AutoProgramComponents autoProgramComponents = (AutoProgramComponents)obj;
						fromSource = Constants.SOURCE_MARKETING_PROGRAM+"_"+autoProgramComponents.getComponentWinId();
						
						Campaigns programCampaign = campaignsDao.find(autoProgramComponents.getSupportId());
						
						if(checkAnyOtherObjectRunning()) { //need to check for safety 
							currentRunningObj = new Object();
							List<AutoProgramComponents> rePutProgComponentsList = new ArrayList<AutoProgramComponents>();
							rePutProgComponentsList.add(autoProgramComponents);
							queue.addCollection(rePutProgComponentsList);
							continue;
							
						}//if
						//***********need to get clarify whether to continue with the normal flow or 
						//need to write code for seperate flow *****************
						
						//validate user_id
						Long userId = autoProgramComponents.getAutoProgram().getUser().getUserId();
						if(usersDao.getUserNameById(userId) == null) {

							if(logger.isWarnEnabled()) logger.warn("**************AutoResponder:  No User object found for the given user_id :"
							+userId+" continuing with the next object in pmtaQueue");
							continue;
						}
						
						//run the activity
						processEmailSendingActivity(autoProgramComponents);
						
						if(logger.isInfoEnabled()) logger.info("After proceesing the Email sending Activity");
						
						contactsDaoForDML.deleteTempContacts();
						//TODO need to update components_contacts & delete the contacts from temp table
						
					}catch (Exception e) {
						logger.error("** AutoProgramComponents : Exception  while sending the AutoProgram based Email campaign",e);
					}
				} //else if
				
			}
			catch(Exception e) {
				logger.error("** Root Exception :", e);
			} 
			
			finally {
				contactsDaoForDML.deleteTempContacts();
				
			}
			
			
			if(logger.isInfoEnabled()) logger.info("--------checking if any other obj prsent in the queue----------");
		} // while

		if(logger.isInfoEnabled()) logger.info("--------- before exiting-----------");
		//isCampaignSchedule = false;
		//isRunning = false;
		
		setIsRunning(false);
	} // run

	
	private boolean checkAnyOtherObjectRunning() {
		
		try {
			int count = contactsDao.getSegmentedContactsCount("SELECT cid FROM tempcontacts");
			logger.debug("before running any object the number of records in the temp table are ::"+count);
			
			if(count > 0) {
				
				return true;
			}
			
			else return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception while finding any object in the queue ", e);
			return false;
		}
		
		
		
	}//checkAnyOtherObjectRunning()
	

	/**
	 * added for EventTrigger
	 * ProcessCampaign
	 * @param campaign
	 */
	private void processCampaign(Campaigns campaign, CampaignSchedule campSchedule) {
		boolean suspeciouslylow = false;
		if(logger.isInfoEnabled()) logger.info(">>>> starting the processing of campaign sending :"+campaign.getCampaignName());
		fromSource = Constants.SOURCE_CAMPAIGN;
		String htmlContent = null;
		String textContent = null;
		Set<String> urlSet = null;
		
		Users user = campSchedule.getUser();
		Users mlUser = null;

		if(campaign.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL) &&
				campaign.getLabel()==null) {

			if(logger.isErrorEnabled()) logger.error("EmailFile is not configured for ExternalEmail type campaign: "+campaign.getCampaignName());
			return;
		}
		else if(campaign.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL) &&
				campaign.getLabel().trim().startsWith("SENT_")==false) {  // if editorType == "ExternalEmail"

			String emailFileName = campaign.getLabel();
			//		String userParentDir = PropertyUtil.getPropertyValue("usersParentDirectory");
			String eeProcessFolder = PropertyUtil.getPropertyValue("externalEmailProcessFolder");

			File newEmlFile = new File(eeProcessFolder, "Ready" + File.separator +emailFileName);
			//		logger.info("EmlFile :"+emlFile.getAbsolutePath());

			if(!newEmlFile.exists()) {

				if(logger.isErrorEnabled()) logger.error("EmailFile is not present: "+newEmlFile.getAbsolutePath());
				return;
			}

			//TODO need to fill the below variables
			htmlContent = campaign.getHtmlText();
			textContent =campaign.getTextMessage();

			//** Adding text footer to text message ***

			textContent += "\r\n\r\n This email was sent to |^email^| by |^senderEmail^|  " +
			" \r\n To unsubscribe this mail, (" + UNSUBSCRIBE_URL +")";

			//textContent = textContent.replace("[", "[[").replace("|^", "[").replace("^|", "]");
			textContent = textContent.replace("|^", "[").replace("^|", "]");
			campaign.setTextMessage(textContent);

			//*****************

			urlSet = new HashSet<String>();
		}
		else {

			EmailContent emailContent = campSchedule.getEmailContent();

			if(emailContent == null && campSchedule.getParentId() != null) {

				//if the emailContent is null for the given campaign schedule object
				// and if its parent is not null then get the content from its parent

				emailContent = campaignScheduleDao.getEmailContentByCsId(campSchedule.getParentId());

				if(emailContent != null) {
					htmlContent = PrepareFinalHTML.prepareStuff(campaign, null,	emailContent, false, "");

					/*try{
					logger.info(" Campaign is saving ... at 1");
					campaign.setFinalHtmlText(htmlContent);
					campaign.setPrepared(true);
					campaignsDao.saveOrUpdate(campaign);
				}catch(Exception e) {
					logger.error(" Campaign could not be saved 1 - "+campaign.getCampaignName(), e);
				}*/
				}
			}
			else  {
				htmlContent = PrepareFinalHTML.prepareStuff(campaign, null, emailContent, false, "");

				/*try{
				logger.info(" Campaign is saving ... at 2");
				campaign.setFinalHtmlText(htmlContent);
				campaign.setPrepared(true);
				campaignsDao.saveOrUpdate(campaign);
			}catch(Exception e) {
				logger.error(" Campaign could not be saved 2 - "+campaign.getCampaignName(), e);
			}*/
			}

			if(htmlContent == null) {
				if(!campaign.isPrepared()) {
					htmlContent = PrepareFinalHTML.prepareStuff(campaign);
					campaign.setFinalHtmlText(htmlContent);
					campaign.setPrepared(true);

					try{
						if(logger.isInfoEnabled()) logger.info(" Campaign is saving ... at 3");
						//campaignsDao.saveOrUpdate(campaign);
						campaignsDaoForDML.saveOrUpdate(campaign);
					}catch(Exception e) {
						logger.error(" Campaign could not be saved 3 - "+campaign.getCampaignName(), e);
					}

				}
				else {
					htmlContent = campaign.getFinalHtmlText();
				}
			}

			if(htmlContent == null ) {
				if(logger.isWarnEnabled()) logger.warn("*********** Found conent as null for the campaign :"+
						campaign.getCampaignName());
				
				campSchedule.setStatus((byte)7);
				//campaignScheduleDao.saveOrUpdate(campSchedule);
				campaignScheduleDaoForDML.saveOrUpdate(campSchedule);
				String msgStr = "Failed due to Empty Content";
				Calendar cal = Calendar.getInstance();
				Messages msgs = new Messages("Send Campaign", "Campaign send failed ",
						msgStr, cal, "Inbox", false, "Info", user);

				messagesDaoForDML.saveOrUpdate(msgs);
				
				try {
					sendMailToSupport(user, campaign.getCampaignName(), campSchedule.getScheduledDate());
				} catch (BaseServiceException e) {
					// TODO Auto-generated catch block
					logger.error("***** Exception : Sending a Mail to Support", e);
					//e.printStackTrace();
				}
				
				return;
			}


			if(emailContent != null ) {

				textContent = emailContent.getTextContent();
			}
			else {
				textContent = campaign.getTextMessage();
			}

			if(textContent == null) {
				textContent = "";
			}

			//** Adding text footer to text message ***

			textContent += "\r\n\r\n This email was sent to |^email^| by |^senderEmail^|  " +
					" \r\n To unsubscribe this mail, (" + UNSUBSCRIBE_URL +")";

			//textContent = textContent.replace("[", "[[").replace("|^", "[").replace("^|", "]");
			textContent = textContent.replace("|^", "[").replace("^|", "]");
			campaign.setTextMessage(textContent);


			//*****************
			urlSet = getUrls(htmlContent);

			if(logger.isDebugEnabled())
				logger.info("Url in the content :"+urlSet);

		} // if


		Set<MailingList> mlSet = campaign.getMailingLists();
    	MailingList mailingList;
    	String listIdsStr = "";
    	String listNamesStr = "";
    	String segmentListQuery = Constants.STRING_NILL;

    	for (Iterator<MailingList> iterator = mlSet.iterator(); iterator.hasNext();) {

			mailingList = iterator.next();
			if(mlUser == null) {
				
				mlUser = mailingList.getUsers();
				
			}
			/*if(mlUser.getParentUser() != null) {
				
				mlUser = mlUser.getParentUser();
			}*/

			// Return if campaign associated mailing list is under going purge process .
			if(mailingList.getStatus() != null && mailingList.getStatus().equalsIgnoreCase(Constants.MAILINGLIST_STATUS_PURGING)) {
				Messages messages = new Messages("Email Module", "Email : "+ campaign.getCampaignName() + " could not start." , 
						"Email sending could not start as the associated Contact list(s) is under purge process," +
						" email will start once purging is completed.",
						Calendar.getInstance(),  "Inbox", false, "Info", user);
				messagesDaoForDML.saveOrUpdate(messages);
				return;
			}
			
			if(listIdsStr.length() == 0) {
				listIdsStr += mailingList.getListId();
				listNamesStr += mailingList.getListName();
			}
			else {
				listIdsStr += "," + mailingList.getListId();
				listNamesStr += "," + mailingList.getListName(); 
			}
		}

    	//modified forPOS
    	/*boolean isSegment = (campaign.getListsType() == null ?
    			false:(campaign.getListsType().equals("Total")?false:true));
*/
    	
    	boolean isSegment=false;
    	boolean listEmpty=false;
    	String listType = campaign.getListsType();
    	int totalSizeInt = 0;
    	String qryStr = null;
    	int configured = 0;
    	int suppressed = 0;
    	int preferenceCount=0;
    	String segmentNames=Constants.STRING_NILL;
    	if(listType == null || listType.equalsIgnoreCase("Total")){
    		
    		qryStr = 
    			" INSERT IGNORE INTO tempcontacts("+Constants.QRY_COLUMNS_TEMP_CONTACTS+", cf_value, domain, event_source_id)  " +
    		    " (SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+",null, SUBSTRING_INDEX(c.email_id, '@', -1), null FROM contacts c, mailing_lists ml " +
    		    " WHERE  ml.list_id IN ("+listIdsStr+") AND  c.user_id = ml.user_id " +
    		    " AND ( c.mlbits & ml.mlbit ) >0 AND c.email_status in ('" + Constants.CONT_STATUS_ACTIVE+"','"+Constants.CONT_STATUS_PURGE_PENDING +"') ORDER BY c.cid)";
    		
    		if(logger.isDebugEnabled()) logger.debug("QRY ::"+qryStr);
    		
    		try {
				configured = contactsDaoForDML.executeJDBCInsertQuery(qryStr);
				//same pattern like other alerts.
				if(configured == 0)
		    	{
					listEmpty=true;
		    		Utility.sendCampaignFailureAlertMailToSupport(user,"",campaign.getCampaignName(),"",campSchedule.getScheduledDate(),
						"Configured List of contacts/Segment not available",Integer.toString(configured),"",campaign.getSubject());
		    		/*sendMailToSupport(user, segmentNames, 
		    				campaign.getCampaignName(), campSchedule.getScheduledDate());*/
		    	}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.debug("in catch of Exception"+e.getClass().getName());
				if(e instanceof CannotAcquireLockException || e instanceof DeadlockLoserDataAccessException) {
					logger.debug("in catch of CannotAcquireLockException OR DeadlockLoserDataAccessException");
					currentRunningObj = new Object();
					List<CampaignSchedule> rePutScheduleList = new ArrayList<CampaignSchedule>();
					rePutScheduleList.add(campSchedule);
					queue.addCollection(rePutScheduleList);
					return;
				}
			}
    		
    	}
    	else{ 
    		
			isSegment = true;
			if(listType.toLowerCase().trim().startsWith("segment:")) {
			
				boolean success = true;
				String msgStr = "";
				Calendar cal = Calendar.getInstance();
	    		List<SegmentRules> segmentRules = segmentRulesDao.findById(listType.split(":")[1]);
	    		try {
	    			
					if(segmentRules == null) {
						
						success = false;
						msgStr = "Segments configured to this Campaign is no longer exist.You might have deleted those.";
						return;
					}//if none of configured segments found 
					
					for (SegmentRules segmentRule : segmentRules) {

						try {
							if(segmentRule == null) {
								
								msgStr = "one of the Segments configured to this Campaign is no longer exist.You might have deleted it.";
								continue ;
								
							}//if
							if(segmentRule.getSegRule() == null ){
								
								msgStr = "invalid Segments configured to this Campaign ";
								continue ;
							}
							Set<MailingList> mlistSet = new HashSet<MailingList>();
							List<MailingList> mlList = mailingListDao.findByIds(segmentRule.getSegmentMlistIdsStr());
							if(mlList == null) {
								continue;
							}
							
							mlistSet.addAll(mlList);
							long mlsbit = Utility.getMlsBit(mlistSet);
							
							String segmentQuery = SalesQueryGenerator.generateListSegmentQuery(
									segmentRule.getSegRule(), mlsbit);
							
							if(segmentQuery == null ){
								
								msgStr = "Invalid segments configured to this campaign ";
								continue ;
							}
							
							String segmentCountQry = SalesQueryGenerator.generateListSegmentCountQuery(segmentRule.getSegRule(), mlsbit);
							
							if(segmentCountQry == null ){
								
								msgStr = "Invalid segments configured to this campaign ";
								continue ;
							}
							
							if(SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRule.getSegRule())) {
								String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRule.getSegRule());
								
								if(csCampIds != null ) {
									String crIDs = Constants.STRING_NILL;
									List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRule.getUserId(), csCampIds);
									if(campList != null) {
										for (Object[] crArr : campList) {
											
											if(!crIDs.isEmpty()) crIDs += Constants.DELIMETER_COMMA;
											crIDs += ((Long)crArr[0]).longValue();
											
										}
									}
									
									segmentQuery = segmentQuery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
									segmentCountQry = segmentCountQry.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
								}
							}
							
							
							
							
							//>>>>>>>>>>>>>>>>>updating seg rules starts<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
							long startTime = System.currentTimeMillis();
							
							//updateSegmentCountAndQuery(segmentRule, segmentQuery, segmentCountQry);
							
							Calendar calStartTimeObj = Calendar.getInstance();
				            calStartTimeObj.setTimeInMillis(startTime);
				            String finalStartTime = calStartTimeObj.get(Calendar.DATE)+"-"+(calStartTimeObj.get(Calendar.MONTH)+1)+" "+calStartTimeObj.HOUR_OF_DAY+":"+calStartTimeObj.MINUTE+":"+calStartTimeObj.SECOND;
				            
				            Calendar calEndTimeObj = Calendar.getInstance();
				            calEndTimeObj.setTimeInMillis(System.currentTimeMillis());
				            String finalEndTime = calEndTimeObj.get(Calendar.DATE)+"-"+(calEndTimeObj.get(Calendar.MONTH)+1)+" "+calEndTimeObj.HOUR_OF_DAY+":"+calEndTimeObj.MINUTE+":"+calEndTimeObj.SECOND;
				            
				            logger.info("Elapsed time for this segment rule, id=="+segmentRule.getSegRuleId()+"  name of segment rule==="+segmentRule.getSegRuleName()+" to run  "+(System.currentTimeMillis() - startTime)+" Millisecond");
				            
				            //>>>>>>>>>>>>>>>>>updating seg rules ends<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
							
							if(!segmentListQuery.isEmpty()) segmentListQuery += " UNION ";
							segmentListQuery += segmentQuery;
							
							qryStr = "INSERT IGNORE INTO tempcontacts ("+Constants.QRY_COLUMNS_TEMP_CONTACTS+", cf_value, domain, event_source_id) "+
									segmentQuery;
							
							logger.info("final segmnet query ::"+qryStr);
							//configured += contactsDao.executeJdbcUpdateQuery(qryStr);
							try {
								configured += contactsDaoForDML.executeJDBCInsertQuery(qryStr);
							} catch (Exception e) {
								logger.debug("in catch of Exception"+e.getClass().getName());
								if(e instanceof CannotAcquireLockException || e instanceof DeadlockLoserDataAccessException) {
									logger.debug("in catch of CannotAcquireLockException OR DeadlockLoserDataAccessException");
									currentRunningObj = new Object();
									List<CampaignSchedule> rePutScheduleList = new ArrayList<CampaignSchedule>();
									rePutScheduleList.add(campSchedule);
									queue.addCollection(rePutScheduleList);
									return;
								}
								else{
									logger.error("exception in segmentrules processing ",e);
									continue;
									
								}
							}
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							
							logger.error("exception in segmentrules processing ",e);
							continue;
							
							
						}
			    		
						if(!segmentNames.isEmpty())
						{
							segmentNames += Constants.DELIMETER_COMMA+Constants.STRING_WHITESPACE;
						}
						segmentNames +=segmentRule.getSegRuleName();
						
						
					}//for
					
					
					if(isSegment && configured == 0 && !segmentNames.isEmpty())
			    	{
			    		//same pattern like other alerts.
						Utility.sendCampaignFailureAlertMailToSupport(user,"",campaign.getCampaignName(),"",campSchedule.getScheduledDate(),
								"Configured List of contacts/Segment not available",Integer.toString(configured),"",campaign.getSubject());
						/*sendMailToSupport(user, segmentNames, 
			    				campaign.getCampaignName(), campSchedule.getScheduledDate());*/
			    	}
			    	
					
				} catch (Exception e) {
					success = false;
					logger.error("** Exception while getting the available count" +
							" for the user:"+user.getUserId(), e);
					return;
				}
	    		finally {

	    			if(!success) {
	    				
	    					try{
	    						Utility.sendCampaignFailureAlertMailToSupport(user,"",campaign.getCampaignName(),"",campSchedule.getScheduledDate(),
	    								"Configured List of contacts/Segment not available",Integer.toString(configured),"",campaign.getSubject());
	    						}catch(Exception e){
	    							logger.error("Exception in sending mail to suppport",e);
	    						}
		    			campSchedule.setStatus((byte)3);
						//campaignScheduleDao.saveOrUpdate(campSchedule);
						campaignScheduleDaoForDML.saveOrUpdate(campSchedule);

						
						Messages msgs = new Messages("Send Campaign", "Campaign send failed ",
								msgStr, cal, "Inbox", false, "Info", user);

						messagesDaoForDML.saveOrUpdate(msgs);
						
					}

				}
	    	}//if
			
			
			
    	}//else
    	
    	
    	/*
    	 *  Commented below  code during EventTrigger ContactsDao is already obtained in
    	 *  PmtaMailmergeSubmitter's Application Context
    	 */
    	//ContactsDao contactsDao = (ContactsDao)context.getBean("contactsDao");  

    	short categoryWeight = campaign.getCategoryWeight();


    	/*if(campSchedule.getParentId() == null && isSegment) {

    		segmentListQuery = SalesQueryGenerator.generateListSegmentQuery(
    				listType,Utility.getMlsBit(mlSet));
    		if(logger.isDebugEnabled()) {
    			logger.info(" Generated Query :"+segmentListQuery);
    		}
    		qryStr = "INSERT IGNORE INTO tempcontacts ("+segmentListQuery+")";
    	}
    	else {
    		qryStr =
    			" INSERT IGNORE INTO tempcontacts " +
    			" (SELECT *,null, SUBSTRING_INDEX(c.email_id, '@', -1) FROM contacts c, contacts_mlists cm WHERE  cm.list_id IN ("+listIdsStr+") AND " +
    			" c.email_status LIKE '" + Constants.CONT_STATUS_ACTIVE +"' ORDER BY c.cid)";
    		
    		qryStr = 
    			" INSERT IGNORE INTO tempcontacts " +
    		    " (SELECT DISTINCT c.*,null, SUBSTRING_INDEX(c.email_id, '@', -1) FROM contacts c, mailing_lists ml " +
    		    " WHERE  ml.list_id IN ("+listIdsStr+") AND  c.user_id = ml.user_id " +
    		    " AND ( c.mlbits & ml.mlbit ) >0 AND c.email_status = '" + Constants.CONT_STATUS_ACTIVE +"' ORDER BY c.cid)";
    				
    	}*/


    	String useMqsStr = PropertyUtil.getPropertyValueFromDB("useMQS");
    	boolean useMQS =( useMqsStr == null ? true : useMqsStr.equalsIgnoreCase("true"));

    	/*if(logger.isDebugEnabled()) {
    		logger.info(" Inserting contacts to tempcontacts using the query :"+
    				qryStr);
    	}*/
    	
    	
    	
    	//int configured = contactsDao.executeJdbcUpdateQuery(qryStr);
    	// this means this campaign is re send campaign
    	// hence remove the contacts from tempcontacts based on the re send criteria
    	if(campSchedule.getParentId() != null) {

    		String columnStr = (campSchedule.getCriteria() == 1) ? "opens":"clicks";
    		if(campSchedule.getCriteria() == 1) {
    			columnStr = "opens";
    		}
    		else if(campSchedule.getCriteria() == 2) {
    			columnStr = "clicks";
    		}

    		Long crId = campaignScheduleDao.getCrIdbyCSId(campSchedule.getParentId());

    		String queryStr =
				" DELETE FROM tempcontacts WHERE " +
				" (email_id NOT IN (SELECT email_id FROM campaign_sent WHERE cr_id=" + crId + ")) OR " +
				" (email_id IN (SELECT email_id FROM campaign_sent WHERE cr_id=" + crId + " AND " + columnStr + " >0))";

    		configured = configured - contactsDaoForDML.executeJdbcUpdateQuery(queryStr);
    		/*
    		queryStr =
    				" DELETE FROM tempcontacts WHERE email_id IN ( SELECT email_id" +
    				" FROM campaign_sent WHERE cr_id ="+crId+" AND "+columnStr+" > 0)";

    		totalSizeInt = totalSizeInt - contactsDao.executeJdbcUpdateQuery(queryStr);*/

    	}
    	
    	
    	totalSizeInt = contactsDaoForDML.createTempContacts(categoryWeight,
    			user.getUserId(), configured);
    	suppressed = configured-totalSizeInt;
    	if(logger.isInfoEnabled()) logger.info(" TotalSizeInt :"+totalSizeInt);
    	
    	// Added for subscriber preference count
    	if(user.getSubscriptionEnable()){
    		preferenceCount = contactsDaoForDML.createPreferenceTempContacts( campaign.getCategories());
    		totalSizeInt = totalSizeInt - preferenceCount;
    		
    	}
    	

    	

    	String msgStr = null;
    	Calendar cal = Calendar.getInstance();
    	
    	
    	//shifted to here to have totalphset to be ready 
    	/** cfNameListStr has the list of custom field names with ',' delimiter */
		String cfNameListStr = "";

		if(campaign.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL) &&
				campaign.getLabel().trim().startsWith("SENT_")==false) {   //

			//TODO need to replace the custom field for External Mail
		} // if
		else {
			 String tempSub = campaign.getSubject();
		     //tempSub = tempSub.replace("[", "[[").replace("|^", "[").replace("^|", "]");
			 tempSub = tempSub.replace("|^", "[").replace("^|", "]");
		     if(logger.isInfoEnabled()) logger.info(">>>>>>>>>>>>testing subject personalisation<<<<<<<<<<<<<<"+tempSub);
			 cfNameListStr = getCustomFields(tempSub+htmlContent+textContent);
		} // else

    	if(!useMQS) {

    		boolean success = false;
    		boolean cccountflag = false;
    		boolean creditCountFlag = false;
    		boolean isInvalidCoupFlag = false;
    		boolean ccdelflag=false;
    		List<Integer> list = new ArrayList<Integer>();
    		try {

				list = usersDao.getAvailableCountOfUser(user.getUserId());

				if(list.get(0).intValue() < totalSizeInt ) {
					suspeciouslylow = true;
					/*//shifted after cc count
					Double result = (double) Math.signum(list.get(0).intValue());
			        if(result.equals(-1.0) || result.equals(0.0)) {
						creditCountFlag=true;
						msgStr =  "Email campaign Name : " + campaign.getCampaignName() + "\r\n";
						msgStr = msgStr +" \r\n Status : Could not sent \r\n";
						msgStr = msgStr + "Email campaign could not be sent as you have reached the limit of emails hence email is stopped \r\n" ;
						msgStr = msgStr + "Available Email you can send :"+list.get(0).intValue() +"\r\n";
						msgStr = msgStr+ "You have configured no contacts for the email "+campaign.getCampaignName() +" is :"+totalSizeInt;
						if(logger.isWarnEnabled()) logger.warn(" Available limit is less than the configured contacts count");
						
						//return;
			        }*/
				}
				
				//make a decission whether coupon codes will be sufficient or not
				CouponCodesDao couponCodesDao = (CouponCodesDao)context.getBean("couponCodesDao");
				CouponsDao couponsDao = (CouponsDao)context.getBean("couponsDao");
				
				
				if(totalPhSet != null && totalPhSet.size() > 0) {
					
					Coupons coupon = null;
					Long couponId = null; 
					for (String eachPh : totalPhSet) {
						if(!eachPh.startsWith("CC_") ) continue;
						//only for CC
							
							
							String[] strArr = eachPh.split("_");
							
							if(logger.isDebugEnabled()) logger.debug("Filling  Promo-code with Id = "+strArr[1]);
							try {
								
								couponId = Long.parseLong(strArr[1]);
								
							} catch (NumberFormatException e) {
								
								couponId = null;
							
							}
							
							if(couponId == null) {
								
								//TODO need to delete it from phset or???????????????????????
								continue;
								
							}
							coupon = couponsDao.findById(couponId);
							

							if(coupon == null) {
								
								ccdelflag=true;
								msgStr =  "Email campaign Name : " + campaign.getCampaignName() + "\r\n";
								msgStr = msgStr +" \r\n Status : Could not sent \r\n";
								msgStr = msgStr + "Email campaign Name : '"+campaign.getCampaignName()+"' could not be sent as you have added  Promotion: "+eachPh +" \r\n" ;
								msgStr = msgStr + "This  Promotion is no longer exists, you might have deleted that. : \r\n";
								
								if(logger.isWarnEnabled()) logger.warn(eachPh + "  Promo-code is not avalable: "+ eachPh);
								return;
								
								
							}
							
							
							//only for running coupons
							if(coupon.getStatus().equals(Constants.COUP_STATUS_EXPIRED) || 
									coupon.getStatus().equals(Constants.COUP_STATUS_PAUSED)) {
								isInvalidCoupFlag = true;
								msgStr =  "Email campaign Name : " + campaign.getCampaignName() + "\r\n";
								msgStr = msgStr +" \r\n Status : Could not sent \r\n";
								msgStr = msgStr + "Email campaign could not be sent as you have added  Promotion : "+coupon.getCouponName() +" \r\n" ;
								msgStr = msgStr + "This  Promotion's Status :"+coupon.getStatus()+" and  valid period :"+ 
								MyCalendar.calendarToString(coupon.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STDATE) +" to "+
								MyCalendar.calendarToString(coupon.getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STDATE) +" \r\n";
								
								if(logger.isWarnEnabled()) logger.warn(coupon.getCouponName() + "  Promotion is not in running state, Status : "+ coupon.getStatus());
								return;

								
								
							}//if
							
							
							if( coupon.getAutoIncrCheck() == true ) {
								continue;
							}
							else if(coupon.getAutoIncrCheck() == false) {
								//need to decide only when auto is false
								//List<Integer> couponCodesList = couponCodesDao.getInventoryCCCountByCouponId(couponId);
								long couponCodeCount = couponCodesDao.getCouponCodeCountByStatus(couponId, Constants.COUP_CODE_STATUS_INVENTORY);
								if(couponCodeCount < totalSizeInt ) {
									
									cccountflag = true;
									msgStr =  "Email campaign Name : " + campaign.getCampaignName() + "\r\n";
									msgStr = msgStr +" \r\n Status : Could not sent \r\n";
									msgStr = msgStr + "Email campaign could not be sent as you have added  Promotion : "+coupon.getCouponName() +" \r\n" ;
									msgStr = msgStr + "Available  Promo-codes you can send :"+couponCodeCount+" \r\n";
									
									if(logger.isWarnEnabled()) logger.warn(" Available  Promo-codes  limit is less than the configured contacts count");
									return;
								}
							
							}//else 
						
					}//for
					
				}//if
				
				//shifted
				success = true;
				
				
			}
    		catch (Exception e) {

				logger.error("** Exception while getting the available count" +
						" for the user:"+user.getUserId(), e);
				success = false;
				return;
			}
    		finally {

    			if(!success) {
    				if(cccountflag) campSchedule.setStatus((byte)4);
    				else if(isInvalidCoupFlag) campSchedule.setStatus((byte)5);
    				//else campSchedule.setStatus((byte)3);
    				//else if(creditCountFlag) campSchedule.setStatus((byte)3);
    				else if(ccdelflag)campSchedule.setStatus((byte) 8);
    				//else campSchedule.setStatus((byte)3);
    				
					//campaignScheduleDao.saveOrUpdate(campSchedule);
					campaignScheduleDaoForDML.saveOrUpdate(campSchedule);

					Messages msgs = new Messages("Send Campaign", "Campaign send failed ",
							msgStr, cal, "Inbox", false, "Info", user);

					messagesDaoForDML.saveOrUpdate(msgs);
					try{
					Utility.sendCampaignFailureAlertMailToSupport(user,"",campaign.getCampaignName(),"",campSchedule.getScheduledDate(),
							campSchedule.getStatusStr(),Integer.toString(configured),"",campaign.getSubject());
					}catch(Exception e){
						logger.error("Exception in sending mail to support",e);
					}
				}

			}

		}
		else {
			//TODO
			/**
			 * Need to write a code to contact with MQService class to get the available
			 * limit of the user by giving the mqs Id (Customer id)
			 */

		}// if use mqs


		/*if(totalSizeInt == 0) {

			logger.warn(">>>>>>> campaign submission is failed found 0 active contacts"+campaign);
			msgStr = "Email Name :"+campaign+"\n"+
				"Found 0 Active contacts in the mailing lists which " +
				"are configured for the Email campaign.\n" +
				"Reason may be configured contacts are existed in the suppression contacts list " +
				"or contacts status changed by user/recipient himself unsubscribed";

			Messages messages = new Messages("Email Details ", "No Active contacts",
					msgStr, cal,  "Inbox", false, "Info", user);
			messagesDaoForDML.saveOrUpdate(messages);
			return;
		}*/

		if(cfNameListStr.length() > 0) {

			

			for(Iterator<MailingList> iterator = mlSet.iterator();iterator.hasNext();) {

				mailingList = iterator.next();
				if(!mailingList.isCustField()) continue;

				/**
				 * mlCfList - List of all the tokens which have 'custom_filed_name:field_index'
				 *  for the particular mailing list
				 * */
				List<String> mlCfList = mailingListDao.getCFList(cfNameListStr, mailingList.getListId());

				String tempcfStr = "";
				String defValue = "";

				for (String cf : mlCfList) {

					if(cf.indexOf(Constants.DELIMETER_DOUBLECOLON) > 0) {

						try {
							if(logger.isInfoEnabled()) logger.info("CF TOken :" + cf);
							String[] tokens = cf.split(Constants.DELIMETER_DOUBLECOLON);

							if(tokens.length > 2)  defValue = tokens[2];
							else defValue = "";

							if(tempcfStr.length() != 0)
								tempcfStr = tempcfStr + ",'" + Constants.CF_COL_DELIMETER + "',";

							tempcfStr = tempcfStr + "'" + tokens[0] + Constants.CF_NAME_VALUE_SEPARATOR
							+ "', ifnull(cust_" + tokens[1] + ",'"+ defValue + "')";

						}
						catch (Exception e) {
							logger.error("Exception while getting the custom field name for the list : "
									+ mailingList.getListName(), e);
						}

					} //if(cf.indexOf(":")>0)
				} // for (String cf : mlCfList)

				if(logger.isInfoEnabled()) logger.info(">>>>>>>>>>> tempcfStr =" + tempcfStr);
				if(mlCfList.size()>0) {
					contactsDaoForDML.updateTempContactsWithCF(tempcfStr , mailingList.getListId());
				}
				if(logger.isInfoEnabled()) logger.info("Updated the tempContacts for list Id:" + mailingList.getListId());

			} // for
		} // if(cfNameListStr.length() > 0)
		
		try{
		boolean connectivity=false;
		connectivity=checkVMTAConnectivity(user,campaign,campSchedule);
		logger.info("connectivity "+connectivity);
		if(!connectivity){
			//campSchedule.setStatus((byte) 9);
			//campaignScheduleDaoForDML.saveOrUpdate(campSchedule);
			return;
		}
		}catch(Exception e){
			logger.error("Exception in checkSendgridConnectivity",e);
		}
		/***************** Create Campaign Report Object *****************/

		CampaignReport campaignReport = new CampaignReport( user,
					campaign.getCampaignName(), campaign.getSubject(), htmlContent,
					campSchedule.getScheduledDate(), 0, 0, 0, 0, 0, Constants.CR_STATUS_SENDING, fromSource);
		String placeHoldersStr = "";
		
		if(totalPhSet.size() > 0) {
			
			for(String ph:totalPhSet){
				
				//if(ph.startsWith("GEN_")) {
					
					//ph = ph.substring(4);
 					if(placeHoldersStr.trim().length() > 0) placeHoldersStr += "||";
 					placeHoldersStr += ph;
					
					
					
				//}//if
			}//for
			
			campaignReport.setPlaceHoldersStr(placeHoldersStr);
		}
		if(urlSet.size()>0) {
			campaignReport.setUrls(urlSet);
			if(logger.isDebugEnabled()) logger.info(" Url in the campaign :"+urlSet);
		}

		try {
			//campaignReportDao.saveOrUpdate(campaignReport);
			campaignReportDaoForDML.saveOrUpdate(campaignReport);
			CampReportLists campReportLists = new CampReportLists(campaignReport.getCrId());
			campReportLists.setListsName(listNamesStr);
			campReportLists.setSegmentQuery(segmentListQuery);
			
			//campReportListsDao.saveOrUpdate(campReportLists);
			campReportListsDaoForDML.saveOrUpdate(campReportLists);

			
			
		}
		catch (Exception e) {
			logger.error("***** Exception : while saving the campaign report", e);
			return;
		}

		if(logger.isDebugEnabled()) {
			logger.info("campaign report object created and saved with the status as sending");
		}

		
		if(totalSizeInt <= 0) {
			logger.warn(">>>>>>> campaign submission is failed found 0 active contacts"+campaign.getCampaignId());
			msgStr = "Email Name :"+campaign.getCampaignName()+"\n"+
				"Found 0 Active contacts in the mailing lists which " +
				"are configured for the Email campaign.\n" +
				"Reason may be configured contacts exists in the suppression contacts list " +
				"or contacts status changed by user/recipient himself unsubscribed";

			Messages messages = new Messages("Email Details ", "No Active contacts",
					msgStr, cal,  "Inbox", false, "Info", user);
			//messagesDao.saveOrUpdate(messages);
			messagesDaoForDML.saveOrUpdate(messages);
			campaignReport.setStatus(Constants.CR_STATUS_SENT);
			//campaignReportDao.saveOrUpdate(campaignReport);
			campaignReportDaoForDML.saveOrUpdate(campaignReport);
			campSchedule.setStatus((byte)1);
			campSchedule.setCrId(campaignReport.getCrId());
			//campaignScheduleDao.saveOrUpdate(campSchedule);
			campaignScheduleDaoForDML.saveOrUpdate(campSchedule);
			//campaignsDao.updateCampaignStatus(campaign.getCampaignId());
			campaignsDaoForDML.updateCampaignStatus(campaign.getCampaignId());
			
			
			return;
		}
		

		if(logger.isInfoEnabled()) logger.info(" submitting the campaign to PMTA from Source campaign "+fromSource);
		if(logger.isDebugEnabled()) logger.debug("User Vmta is :"+ user.getVmta());
		
		boolean flag = false;
		byte statusCodeByte = -1;
		Vmta vmta = user.getVmta();
			UsersDao usersDao = (UsersDao)context.getBean("usersDao");
			VmtaDao vmtaDao = (VmtaDao)context.getBean("vmtaDao");
			UserVmta userVmta = usersDao.findBy(user.getUserId(), Constants.SENDING_TYPE_BULK);
			Vmta vmtaObj = null;
			
			if(userVmta != null) {
					vmtaObj = vmtaDao.find(userVmta.getVmtaId());
					
				}
			else {
				vmtaObj = vmta;
			}
		try {
			if(vmtaObj == null) flag = false;
			if(vmtaObj != null){
					
				ExternalSMTPSender ExternalSMTPSender = new ExternalSMTPSender(vmtaObj,context);	

					flag = ExternalSMTPSender.submitCampaign(campaign, htmlContent, textContent, campaignReport, totalSizeInt, null, fromSource,vmtaObj);
						
					if(flag) {
						if(logger.isInfoEnabled()) logger.info("campaign submission is completed ");
						msgStr = "Email Name :"+campaign.getCampaignName()+"\n"+
								"Sent successfully to "+campaignReport.getSent()+" unique contacts";
					}/* else {
						if(logger.isInfoEnabled()) logger.info("campaign submission is failed ");
						msgStr = "Email Name  :"+campaign.getCampaignName()+"\n"+
								//"Status code :"+statusCodeByte+"\n"+
								"Submission is failed, please report the problem to admin";

						campReportListsDao.deleteByCampRepId(campaignReport.getCrId());
						campaignReportDao.delete(campaignReport);
					}*/

				}
				
		//	}//if VMTA not equals to null
			if(flag == false){

				if(logger.isInfoEnabled()) logger.info("campaign submission is failed ");
				msgStr = "Email Name  :"+campaign.getCampaignName()+"\n"+
						//"Status code :"+statusCodeByte+"\n"+
						"Submission is failed, please report the problem to admin";

				//campReportListsDao.deleteByCampRepId(campaignReport.getCrId());
				campReportListsDaoForDML.deleteByCampRepId(campaignReport.getCrId());

				campaignReportDaoForDML.delete(campaignReport);
			}
			 /*else { 
			
			// Send from PMTA API .
			
				statusCodeByte =
				senderThread.submitCampaign(campaign, htmlContent, textContent,	campaignReport, totalSizeInt,null,fromSource);
			
				
				if(statusCodeByte == 0) {
					
					if(campaign.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL) &&
							campaign.getLabel().trim().startsWith("SENT_")==false) {
						
						campaign.setLabel("SENT_"+campaign.getLabel().trim());
						urlSet = getUrls(campaign.getFinalHtmlText());
						campaignsDao.saveOrUpdate(campaign); // update the HtmlContent.
						campaignReport.setContent(campaign.getHtmlText());
						campaignReport.setUrls(urlSet);
					} // if "ExternalEmail"
					
					logger.info("campaign submission is completed ");
					msgStr = "Email Name :"+campaign.getCampaignName()+"\n"+
					"Sent successfully to "+campaignReport.getSent()+" unique contacts";

			}
			else {
				logger.info("campaign submission is failed ");
				msgStr = "Email Name  :"+campaign.getCampaignName()+"\n"+
						 "Status code :"+statusCodeByte+"\n"+
						 "Submission is failed, please report the problem to admin";

				campReportListsDao.deleteByCampRepId(campaignReport.getCrId());
				campaignReportDao.delete(campaignReport);

			}
				
		
				
			} // else 	
*/
			if(msgStr != null && (statusCodeByte == 0 || flag == true )) {

					Messages messages = new Messages("Email Details ",
							(statusCodeByte == 0 || flag == true)?"Email sent successfully":"Email sending failed.",
									msgStr, cal,  "Inbox", false, "Info", user);
					
					// update Used Email Count in Users table
					if(!useMQS) {
						//logger.debug("if !useMQs update user available Email count..."+user.getUserId()+" totalSize ::"+totalSizeInt);
						
						
						/*user.setUsedEmailCount(user.getUsedEmailCount()+totalSizeInt);
						usersDao.saveOrUpdate(user);
						*/
						//usersDao.updateUsedEmailCount(user.getUserId(), totalSizeInt);
						usersDaoForDML.updateUsedEmailCount(user.getUserId(), totalSizeInt);
						
		                int thresholdUserCredit = ((int) Math.ceil((user.getEmailCount() * 10) / 100));
		                List<Integer> usercreditlist = usersDao.getAvailableCountOfUser(user.getUserId());
		                logger.info("new thresholdUserCredit for camp :"+thresholdUserCredit);
		                logger.info("new thresholdUserCredit for camp check:"+usercreditlist.get(0).intValue());
		                if(usercreditlist.get(0).intValue() <= thresholdUserCredit) {
		                	logger.info("suspeciouslylow is true");
		               	 	suspeciouslylow = true;
		                }
						
						if(suspeciouslylow == true) {
							sendMailtoSupportForNegativeEmailCreditScore(user,campaign,campSchedule);
						}
						
						
						 /**********update each coupon issued count***********************************/
						
						try {
							Utility.updateCouponCodeCounts(context, totalPhSet);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("** Exception while updating the coupons", e);
						}
						/*CouponsDao couponsDao = (CouponsDao)context.getBean("couponsDao");
						CouponCodesDao couponCodesDao = (CouponCodesDao)context.getBean("couponCodesDao");
						if(totalPhSet != null && totalPhSet.size() > 0) {
						
			logger.warn(">>>>>>> campaign submission is failed found 0 active contacts"+campaign.getCampaignId());
			msgStr = "Email Name :"+campaign.getCampaignName()+"\n"+
				"Found 0 Active contacts in the mailing lists which " +
				"are configured for the Email campaign.\n" +
				"Reason may be configured contacts exists in the suppression contacts list " +
				"or contacts status changed by user/recipient himself unsubscribed";

			Messages messages = new Messages("Email Details ", "No Active contacts",
					msgStr, cal,  "Inbox", false, "Info", user);
			//messagesDao.saveOrUpdate(messages);
			messagesDaoForDML.saveOrUpdate(messages);
			campaignReport.setStatus(Constants.CR_STATUS_SENT);
			//campaignReportDao.saveOrUpdate(campaignReport);
			campaignReportDaoForDML.saveOrUpdate(campaignReport);
			campSchedule.setStatus((byte)1);
			campSchedule.setCrId(campaignReport.getCrId());
			//campaignScheduleDao.saveOrUpdate(campSchedule);
			campaignScheduleDaoForDML.saveOrUpdate(campSchedule);
			//campaignsDao.updateCampaignStatus(campaign.getCampaignId());
			campaignsDaoForDML.updateCampaignStatus(campaign.getCampaignId());
			
			
			return;
		
							boolean isCoupFound = false;
							
							for (String eachPh : totalPhSet) {
								
								if(!eachPh.startsWith("CC_")) continue;
								
								if(!isCoupFound) {//before updating issued and availble counts flush the couponvector
									
									CouponProvider couponProvider = CouponProvider.getCouponProviderInstance(context);
									couponProvider.flushCouponCodesToDB(true);
									isCoupFound= true;
								}
								
								
								Long couponId = null; 
								String[] strArr = eachPh.split("_");
								
								logger.debug("Filling Coupons with Id = "+strArr[1]);
								try {
									
									couponId = Long.parseLong(strArr[1]);
									
								} catch (NumberFormatException e) {
									
									couponId = null;
								
								}
								
								if(couponId == null) {
									
									//TODO need to delete it from phset or???????????????????????
									continue;
									
								}
								long issuedCount = couponCodesDao.findIssuedCoupCodeByCoup(couponId);
								
								int updateCount = couponsDao.updateIssuedCountByCouponId(couponId, issuedCount);
								
								logger.info("coupon - "+couponId.longValue()+" has updated with issued count : "+issuedCount);
								
								
								
							}
							
							
						}
						*/
						
						/*********************************************/
						
						
						
						
						/*int updateCount = usersDao.updateUsedEmailCount(user.getUserId(), totalSizeInt);
						logger.info("updated Count ::"+updateCount+" user available credits::"+(user.getEmailCount()-user.getUsedEmailCount()));*/
					}
					else {
						//TODO need to write code to call MQS service Process order to update
						// the usage
					}
					//messagesDao.saveOrUpdate(messages);
					messagesDaoForDML.saveOrUpdate(messages);
					campaignReport = campaignReportDao.findById(campaignReport.getCrId());
					
					if(campaignReport == null) {
						
						if(logger.isDebugEnabled()) logger.debug("no report found with the given ID");
						
					}
					else {
						campaignReport.setConfigured(configured);
						campaignReport.setSuppressed(suppressed);
						campaignReport.setSent(totalSizeInt);
						campaignReport.setPreferenceCount(preferenceCount);
						campaignReport.setStatus(Constants.CR_STATUS_SENT);
						//campaignReportDao.saveOrUpdate(campaignReport);
						campaignReportDaoForDML.saveOrUpdate(campaignReport);
						
						//campaignReportDao.updateCampaignReport(campaignReport.getCrId(), sentId, type);
						
						
						
						//set the campaign report id for campaign schedule object and status code 1 (sent)
						campSchedule.setStatus((byte)1);
						//if(listEmpty)campSchedule.setStatus((byte)0);
						campSchedule.setCrId(campaignReport.getCrId());
						//campaignScheduleDao.saveOrUpdate(campSchedule);
						campaignScheduleDaoForDML.saveOrUpdate(campSchedule);
						
						//campaignsDao.updateCampaignStatus(campaign.getCampaignId());
						campaignsDaoForDML.updateCampaignStatus(campaign.getCampaignId());
					}
					
				}
		}	
			
		catch (Exception e) {
				logger.error("** Exception", e);
		}
			
		//System.gc();

		if(logger.isInfoEnabled()) logger.info(">>>>>>>>> Campaign processing is completed");

	} // processCampaign
	
	
	private void sendMailtoSupportForNegativeEmailCreditScore(Users user, Campaigns campaign, CampaignSchedule campSchedule){
            if (user != null) {
            	user = usersDao.find(user.getUserId());
                    try {
                        Utility.sendCampaignEmailNegativeCreditAlertMailToSupport(user,Constants.STRING_NILL, campaign.getCampaignName(), campSchedule != null ? campSchedule.getScheduledDate() : null, "negative email credits", Constants.STRING_NILL, Constants.STRING_NILL,
                            Constants.STRING_NILL);
                    } catch (BaseServiceException e) {
                        //e.printStackTrace();
                    	logger.error("Exception:",e);
                    }
            }
}
	
	
	private void sendMailToSupport(Users user, String segmentNames, 
			 String campaignName, Calendar scheduledDate) throws BaseServiceException{
		try {
			// TODO Auto-generated method stub

			EmailQueueDao emailQueueDao = null;
			EmailQueueDaoForDML emailQueueDaoForDML = null;
			try {
				emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
				emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
			} catch (Exception e) {
				
				logger.error(e);
				throw new BaseServiceException("No dao(s) found in the context");
				
			}
			
			String supportMail = PropertyUtil.getPropertyValueFromDB(Constants.ALERT_TO_EMAILID);
			String supportMailId = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SUPPORT_EMAILID);
			String subject = Constants.NO_SEGMENTED_CONTACTS_SUBJECT;
			String message = PropertyUtil.getPropertyValueFromDB(Constants.NO_SEGMENTED_CONTACTS_EMAIL_CONTENT);
			
			
			String userName= Utility.getOnlyUserName(user.getUserName());
			subject=subject.replace(Constants.ALERT_MESSAGE_PH_USERNAME, userName);
			message=message.replace(Constants.ALERT_MESSAGE_PH_USERNAME, userName);
			message=message.replace(Constants.ALERT_MESSAGE_PH_EMAILCAMPAIGNNAME, campaignName);
			message=message.replace(Constants.ALERT_MESSAGE_PH_SEGMENTNAMES,segmentNames);
			message=message.replace(Constants.ALERT_MESSAGE_PH_ORGNAME,user.getUserOrganization().getOrganizationName());
			message=message.replace(Constants.ALERT_MESSAGE_PH_SCHEDULEDON, MyCalendar.calendarToString(scheduledDate, MyCalendar.FORMAT_DATETIME_STYEAR));
			
			EmailQueue emailQueue = new EmailQueue(subject, message, Constants.EQ_TYPE_SUPPORT_ALERT,
					Constants.EQ_STATUS_ACTIVE, supportMail, new Date());	
			 
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
			
			EmailQueue emailQueueSupport = new EmailQueue(subject, message, Constants.EQ_TYPE_SUPPORT_ALERT,
					Constants.EQ_STATUS_ACTIVE, supportMailId, new Date());
			
			emailQueueDaoForDML.saveOrUpdate(emailQueueSupport);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error while sending a mail to support", e);
			throw new BaseServiceException("Error while sending a mail to support");
		}
		
		}

	/**
	 * sends the program based campaign to those contacts which inserted into the tempContacts from the temp_activity_data table
	 * @param autoProgramComponent
	 */
	private void processEmailSendingActivity(AutoProgramComponents autoProgramComponent) {
		
		Campaigns campaignToBeSent =null;
		
		AutoProgram tempAutoProgram = autoProgramComponent.getAutoProgram();
		
		Users usersObj = tempAutoProgram.getUser();//need to take care of "lazy initialisation"


		Set<String> urlSet = null;
		Set<MailingList> mlSet = null;
		
		String htmlContent = null;
		String textContent = null;
		String campaignName = null;

		short categoryWeight = 1;
		
		String listIdsStr = "";
		
		Long campaignId = null;
		
		campaignId = autoProgramComponent.getSupportId();
		campaignToBeSent = campaignsDao.findByCampaignId(campaignId);
		//validate the campaign that is to be sent
		
		if(campaignToBeSent == null) {
			if(logger.isInfoEnabled()) logger.info("Auro program email sending component have no campaign with the configured campaign id:" +
					""+campaignId+"Email cant be send");
			return;
		}
		//this external email examine is not required actually
		
		/*if(campaignToBeSent.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL)
				&& campaignToBeSent.getLabel()==null){

			logger.error("EmailFile is not configured for ExternalEmail type campaign: "+campaignToBeSent.getCampaignName());
			return;
		}
		else if(campaignToBeSent.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL) &&
				campaignToBeSent.getLabel().trim().startsWith("SENT_")==false) { // if editorType == "ExternalEmail"

			String emailFileName = campaignToBeSent.getLabel();

			//			    String userParentDir = PropertyUtil.getPropertyValue("usersParentDirectory");
			String eeProcessFolder = PropertyUtil.getPropertyValue("externalEmailProcessFolder");

			File newEmlFile = new File(eeProcessFolder, "Ready" + File.separator +emailFileName);

			if(!newEmlFile.exists()) {
				logger.error("EmailFile is not present: "+newEmlFile.getAbsolutePath());
				return;
			}

			//TODO need to fill the below variables
			htmlContent = campaignToBeSent.getHtmlText();
			textContent ="Dummy TEXT Content";
			urlSet = new HashSet<String>();

		}else { // not an external email*/
			
			campaignName = campaignToBeSent.getCampaignName();
			
			if(htmlContent == null){

				if(!campaignToBeSent.isPrepared()){

					htmlContent = PrepareFinalHTML.prepareStuff(campaignToBeSent);
					campaignToBeSent.setFinalHtmlText(htmlContent);
					campaignToBeSent.setPrepared(true);

					try{
						if(logger.isInfoEnabled()) logger.info(" campaignToBeSent is saving ");
						//campaignsDao.saveOrUpdate(campaignToBeSent);
						campaignsDaoForDML.saveOrUpdate(campaignToBeSent);
					}
					catch(Exception e){
						logger.error(" Campaign could not be saved "+campaignName, e);
					}
				}
				else{
					htmlContent = campaignToBeSent.getFinalHtmlText();
				}

			} // html==null
			if(htmlContent == null) {
				if(logger.isWarnEnabled()) logger.warn("*********** Found conent as null for the campaign :"+campaignName);
				return;
			}
			textContent = campaignToBeSent.getTextMessage();

			if(textContent == null) {
				textContent = "";
			}
			//** Adding text footer to text message ***

			textContent += "\r\n\r\n This email was sent to |^email^| by |^senderEmail^|  " +
			" \r\n To unsubscribe this mail, (" + UNSUBSCRIBE_URL +")";

			//textContent = textContent.replace("[", "[[").replace("|^", "[").replace("^|", "]");
			textContent = textContent.replace("|^", "[").replace("^|", "]");
			campaignToBeSent.setTextMessage(textContent);
			urlSet = getUrls(htmlContent);

			if(logger.isDebugEnabled())
				logger.info("Url in the content :"+urlSet);

		
			categoryWeight = campaignToBeSent.getCategoryWeight();
			
			
			
		
		
		/***going with examine of the mailing Lists *********/
		
		//this is also not required here we are taking only the active contacts,
		//and the remaining contacts will anyway comes in further runs  of the program
		
		/*mlSet = autoProgramComponent.getAutoProgram().getMailingLists();
		if(mlSet == null || mlSet.size() <= 0) {
			
			logger.info("Got empty mailing lists....exiting processactivity");
			return;
		}else { // check for purge status of the configured mailinglists 
			
			// Return if Program associated mailing list is under going purge process .
			for (MailingList mailingList2 : mlSet) {
				if(mailingList2.getStatus().equalsIgnoreCase(Constants.MAILINGLIST_STATUS_PURGING)) {
					Messages messages = new Messages("Auto Responder", "program"+ autoProgramComponent.
							getAutoProgram().getProgramName()+ " could not start." , "program Email sending " +
							"could not start as the associated Contact list(s) is under purge process,",
							Calendar.getInstance(),  "Inbox", false, "Info", mailingList2.getUsers());
					messagesDao.saveOrUpdate(messages);
					logger.info("*** Exception : Auto Program associated mailinglist(s) is under purging, Hence returning ..");
					return;
				}// if
			}// for
			
			listIdsStr = getListIdsStr(mlSet);
		}// else
		
		if(listIdsStr.length() == 0) {
			
			logger.info("No List ids are found: ");
			return;
			
		}  // listIdsStr.length() == 0
*/		
		
		//logger.info("list ids found are=======>"+ listIdsStr);
		
		String listNamesStr="";
		mlSet = tempAutoProgram.getMailingLists();
		if(logger.isDebugEnabled()) logger.debug("the mailing lists are====================>"+mlSet+"<===================");
		
		if(mlSet != null || ! (mlSet.size() <= 0)) {
			for (MailingList mailingList : mlSet) {
				
				if(listNamesStr.length() > 0 ) listNamesStr += ",";
				listNamesStr += mailingList.getListName();
				
				if(listIdsStr.length() > 0) listIdsStr += ",";
				listIdsStr += mailingList.getListId();
				
			}
			
			
			
		}
			
		
		/*****getting ready to send email**********/
		
		String qryStr = "";
		int totalSizeInt = 0;
		String useMqsStr = PropertyUtil.getPropertyValueFromDB("useMQS");
		boolean useMQS =( useMqsStr == null ? true : useMqsStr.equalsIgnoreCase("true"));
		Long userId = null;

		userId = usersObj.getUserId();
		
		/*" INSERT IGNORE INTO tempcontacts " +
		" (SELECT *,null FROM contacts WHERE  list_id IN ("+listIdsStr+") AND " +
		" email_status LIKE '" + Constants.CONT_STATUS_ACTIVE +"' ORDER BY cid)";*/
		
		
		contactsDaoForDML.deleteTempContacts();
		
		
		qryStr = " INSERT IGNORE INTO tempcontacts("+Constants.QRY_COLUMNS_TEMP_CONTACTS+", cf_value, domain, event_source_id)  " +
				 " (SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+",null, SUBSTRING_INDEX(c.email_id, '@', -1), null    FROM contacts c WHERE " +
				 " c.email_status LIKE '" + Constants.CONT_STATUS_ACTIVE +"' AND c.cid in" +
		 		 "(SELECT contact_id from temp_activity_data where label = '"+autoProgramComponent.getLabel()+"'" +
 		 		 " AND program_id="+autoProgramComponent.getAutoProgram().getProgramId()+"" +
 		 		 " AND component_id="+autoProgramComponent.getCompId()+") ORDER BY c.cid)";
				
		
		
				/*" FROM TempActivityData where label='"+autoProgramComponent.getLabel()+
				 ", AND programId="+autoProgramComponent.getAutoProgram().getProgramId()+"" +
				 " AND componentId="+autoProgramComponent.getCompId();*/
		
		if(logger.isDebugEnabled()) {

			logger.info(" Inserting contacts to tempcontacts using the query :"+
					qryStr);
		}// if
		
		totalSizeInt = contactsDaoForDML.createTempContacts(qryStr, categoryWeight, userId);

		if(totalSizeInt == 0) {

			if(logger.isInfoEnabled()) logger.info(" Got "+totalSizeInt+" contacts into temp_contacts: exiting processCampaign");
			return;
		}
		if(logger.isInfoEnabled()) logger.info(" TotalSizeInt :"+totalSizeInt);


		/*//logger.info("deleting already sent members from Temp contacts");
		//
		//totalSizeInt = filterTempContacts(eventTrigger, totalSizeInt); // check for args (userId, totalSizeInt)

		if(totalSizeInt < 0){
			logger.info("Exception while deleting from tempcontacts- exiting");
			return;
		}

		if(totalSizeInt == 0){
			logger.info("After deletion from tempcontacts number of records to process are "+totalSizeInt+"...exiting processTrigger");
			return;
		}*/
		
		//tempcontacts is having the contacts same as temp_activity_contacts 
		//these are nothing but filtered contacts so need not to do any filterization like already sent or not 
		
		//check for credits availability
		String msgStr = null;
		String programName = tempAutoProgram.getProgramName(); 
		Calendar cal = Calendar.getInstance();
		if(!useMQS) {
			boolean success = false;
			List<Integer> list = new ArrayList<Integer>();
			try {
	
				list = usersDao.getAvailableCountOfUser(userId);
	
				if(list.get(0).intValue() < totalSizeInt ) {
	
					if(logger.isWarnEnabled()) logger.warn(" Available limit is less than the configured contacts count");
					//return;
				}
				success = true;
			}
			catch (Exception e) {
	
				logger.error("** Exception while getting the available count" +
						" for the user:"+userId, e);
				return;
			}
			finally {
	
				if(!success) {
					//Yet to be done.............unless we make the program object having status 
					
					//eventTrigger.setStatus("Failed due to less user count");
	
					msgStr =  "Program Name : " + programName + "\r\n";
					msgStr = msgStr +" \r\n Status : Could not sent \r\n";
					msgStr = msgStr + "Auto Program could not sent this campaign as you have reached the limit of emails hence email is stopped \r\n" ;
					msgStr = msgStr + "Available Email you can send :"+list.get(0).intValue() +"\r\n";
					msgStr = msgStr+ "You have configured no contacts for the email "+programName +" is :"+totalSizeInt;
					Messages msgs = new Messages("Auto Responder", "Auto Responder based email send failed ",
							msgStr, cal, "Inbox", false, "Info", usersObj);
	
					//messagesDao.saveOrUpdate(msgs);
					messagesDaoForDML.saveOrUpdate(msgs);
				}	//success
	
			}	//finally

	}
	else {
		//TODO
		/**
		 * Need to write a code to contact with MQService class to get the available
		 * limit of the user by giving the mqs Id (Customer id)
		 */

	}// if use mqs
		//TODO need to make setup to replace customfields
		
		
		//need to create campaignReport object with the source autoresponder,
		
		getCustomFields(htmlContent);
		
		CampaignReport campaignReport = new CampaignReport( usersObj,
				programName, campaignToBeSent.getSubject(), htmlContent,
				cal, 0, 0, 0, 0, 0, Constants.CR_STATUS_SENDING,fromSource);
		
		
		String placeHoldersStr = "";
		
		if(totalPhSet.size() > 0) {
			
			for(String ph:totalPhSet){
				
				if(ph.startsWith("GEN_")) {
					
					ph = ph.substring(4);
 					if(placeHoldersStr.trim().length() > 0) placeHoldersStr += "||";
 					placeHoldersStr += ph;
					
					
					
				}//if
			}//for
			
			campaignReport.setPlaceHoldersStr(placeHoldersStr);
		}
		

		if(urlSet.size()>0) {
			campaignReport.setUrls(urlSet);
			if(logger.isDebugEnabled()) logger.info(" Url in the campaign :"+urlSet);
		}
		
		try {
			//campaignReportDao.saveOrUpdate(campaignReport);
			campaignReportDaoForDML.saveOrUpdate(campaignReport);
			
			CampReportLists campReportLists = new CampReportLists(campaignReport.getCrId());
			
			
			logger.debug("the list name is==========>"+listNamesStr+"<=================");
			campReportLists.setListsName(listNamesStr);
			campReportLists.setSegmentQuery(null);
			//campReportListsDao.saveOrUpdate(campReportLists);
			campReportListsDaoForDML.saveOrUpdate(campReportLists);

			//TODO need to do according to programdesigner based reports
			
		}
		catch (Exception e) {
			logger.error("***** Exception : while saving the campaign report", e);
			return;
		}
		
		if(logger.isDebugEnabled()) {
			logger.info("campaign report object created and saved with the status as sending");
		}

		//TODO need to submit the campaign (senderThread.submitCampaign())
		
		if(logger.isInfoEnabled()) logger.info(" submitting the campaign to PMTA from Source campaign "+fromSource);
		
		byte statusCode = -1;
		boolean sendGridflag = false;
		Vmta vmta = campaignToBeSent.getUsers().getVmta();
		String vmtaName = vmta.getVmtaName();
	/*	if(campaignToBeSent.getUsers().getVmta().equalsIgnoreCase("SendGridAPI")) {*/
	//	if(Constants.SMTP_SENDGRIDAPI.equalsIgnoreCase(vmtaName) || vmta.getVmtaName().equalsIgnoreCase("AmazonSES")) {
			// Send From SendGrid API .
		UsersDao usersDao = (UsersDao)context.getBean("usersDao");
		VmtaDao vmtaDao = (VmtaDao)context.getBean("vmtaDao");
		UserVmta userVmta = usersDao.findBy(campaignToBeSent.getUsers().getUserId(), Constants.SENDING_TYPE_BULK);
		String vmtaDb = PropertyUtil.getPropertyValueFromDB("VMTA");
		String genericVmta = PropertyUtil.getPropertyValueFromDB("SingleVMTA");
		Vmta vmtaObj = null;
						
						
		if(userVmta != null) {
			vmtaObj = vmtaDao.find(userVmta.getVmtaId());
		}else {
			vmtaObj = vmtaDao.getGenericVmta(vmtaDb, genericVmta);
		}
			ExternalSMTPSender sendGridSender = new ExternalSMTPSender(context);			
			sendGridflag = sendGridSender.submitCampaign(campaignToBeSent, htmlContent, textContent, campaignReport, totalSizeInt, null, fromSource,vmtaObj);
			
			if(sendGridflag) {
				if(logger.isInfoEnabled()) logger.info("campaign submission is completed ");
				msgStr = "Email Name :"+campaignToBeSent.getCampaignName()+"\n"+
				"Sent successfully to "+campaignReport.getSent()+" unique contacts";
			} else {
				if(logger.isInfoEnabled()) logger.info("campaign submission is failed ");
				msgStr = "Email Name  :"+campaignToBeSent.getCampaignName()+"\n"+
						 //TODO:"Status code :"+statusCodeByte+"\n"+
						 "Submission is failed, please report the problem to admin";

				//campReportListsDao.deleteByCampRepId(campaignReport.getCrId());
				campReportListsDaoForDML.deleteByCampRepId(campaignReport.getCrId());
				campaignReportDaoForDML.delete(campaignReport);
			}
			
//		}
		/*else  {
			//	need to make status code having no value(just for dummy testing)
			statusCode = 
			senderThread.submitCampaign(campaignToBeSent, htmlContent, textContent,	campaignReport, totalSizeInt,null,fromSource);

			if(statusCode == 0) {
				
				logger.info("campaign submission is completed ");
				msgStr = "Email Name :"+campaignName+"\n"+
				"Sent successfully to "+campaignReport.getSent()+" unique contacts";
				
				
			}else {
				logger.info("campaign submission is failed ");
				msgStr = "Email Name  :"+campaignName+"\n"+
				"Status code :"+statusCode+"\n"+
				"Submission is failed, please report the problem to admin";
				campReportListsDao.deleteByCampRepId(campaignReport.getCrId());
				campaignReportDao.delete(campaignReport);
				
				
			}
		}*/
		
		try{
			
			
			if(msgStr != null && (statusCode == 0 || sendGridflag) ) {
				Messages messages = new Messages("Email Details ",
						(statusCode == 0)?"Email sent successfully":"Email sending failed.",
						msgStr, cal,  "Inbox", false, "Info", usersObj);

				// update Used Email Count in Users table
				if(!useMQS) {
					//usersDao.updateUsedEmailCount(userId, totalSizeInt);
					usersDaoForDML.updateUsedEmailCount(userId, totalSizeInt);
				}
				else {
					//TODO need to write code to call MQS service Process order to update
					// the usage
				}
				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);


				campaignReport.setStatus(Constants.CR_STATUS_SENT);
				//campaignReportDao.saveOrUpdate(campaignReport);
				campaignReportDaoForDML.saveOrUpdate(campaignReport);
				//campaignsDao.updateCampaignStatus(campaignId);
				campaignsDaoForDML.updateCampaignStatus(campaignId);

				
				/**
				 * TODO need to follow the below steps
				 * 1.get contact id s from tempContacts for specified size.
				 * 2.get these contacts related entries in components_contacts.
				 * 3.for each component_contact remove the contact id from the list got in first step
				 *   (this step lets this list to be remain with new contacts) .
				 * 4.create  those many entries in components and contacts for each contact id remained 
				 *   in the list after 3rd step and add those compAndContact object to the list got in 2nd step.
			     * 5.for each compANDcontact object of list got in 4th step,update 'path','compId','compwinId','activity_date'
			     * 	 and add to the temp list.
			     * 6.finally save the list by collection.
				 * 
				 */
				
				
				List<Long> cidLst = null;
				List<Contacts> contactList = null;
				StringBuffer contactEmailSb = new StringBuffer();
				int size = 100;
				int strtIndex = 0;
				ComponentsAndContacts componentsAnContacts = null;
				List<ComponentsAndContacts> listTobeSaved = new ArrayList<ComponentsAndContacts>();
				String qry = "select cid from tempcontacts";
				String query = "select * from tempcontacts";
				
				String cidStr = "";
				List<ComponentsAndContacts> retListOfComps = null;
				do{
					/*
					 * this will helps to get the contacts which are not present in components_contacts,
					 * and creates those many new entries in this table (modified on sept_13th when path updation issue araised )
					 */
					listTobeSaved.clear();
					
					/********************STEP:1***********************************/
					
					cidLst = contactsDao.getContactsForProgram(qry, strtIndex, size);
					
					contactList = contactsDao.getSegmentedContacts(query, strtIndex, size);
					if(contactList != null && contactList.size() > 0) {
						
						for(Contacts contact : contactList) {
							
							if(cidStr.length() > 0) cidStr += ",";
							cidStr += contact.getContactId();
							
							
							if(contactEmailSb.length() > 0) contactEmailSb = contactEmailSb.append(",");
							contactEmailSb = contactEmailSb.append("'"+contact.getEmailId()+"'");
							
						}
						
						/******************STEP:2*********************************/
						
						retListOfComps = componentsAndContactsDao.getByContactIds(cidStr,
											autoProgramComponent.getAutoProgram().getProgramId());
						
						/*******************STEP:3************************************/
						
						for (ComponentsAndContacts componentsAndContacts : retListOfComps) {
							
							if(cidLst.contains(componentsAndContacts.getContactId()) ) {//now cidLst will consists only new contacts
								
								
								cidLst.remove(componentsAndContacts.getContactId());
							}//if
							
							
						}//for
						
						/*********************STEP:4**********************************/
						for(Long cid : cidLst) {
							
							
							componentsAnContacts = new ComponentsAndContacts(tempAutoProgram.getProgramId(), userId,
									cid, autoProgramComponent.getCompId(), autoProgramComponent.getStage()
									, autoProgramComponent.getComponentWinId(), "", cal);
							listTobeSaved.add(componentsAnContacts);
							
						}//for
						
						/**********************STEP:5***********************************/
						
						/*for (ComponentsAndContacts compToBeUpdate : retListOfComps) {
							
							compToBeUpdate.setActivityDate(cal);
							compToBeUpdate.setComponentId(autoProgramComponent.getCompId());
							compToBeUpdate.setPath(compToBeUpdate.getPath()+","+autoProgramComponent.getComponentWinId());
							compToBeUpdate.setComponentWinId(autoProgramComponent.getComponentWinId());
							listTobeSaved.add(compToBeUpdate);
							
						}//for
*/						
						/*************************STEP:6********************************/
						
						//componentsAndContactsDao.saveByCollection(listTobeSaved);
						componentsAndContactsDaoForDML.saveByCollection(listTobeSaved);

						if(contactEmailSb.length()>0) {
							
							
							
							String updatesubqueryQuery = "SELECT contact_id FROM temp_activity_data WHERE component_id="+
							autoProgramComponent.getCompId()+" AND contact_id in(SELECT cid " +
							"FROM contacts WHERE list_id in("+listIdsStr+") AND email_id in("+contactEmailSb.toString()+"))";
							
							String cidsTobeUpdated = tempComponentsDataDao.getContactIdsStr(updatesubqueryQuery);
							
							if(cidsTobeUpdated.length() > 0) {
								
								
								/*//String updateQuery =
								componentsAndContactsDao.updateActivityDateAndPath(cal, tempAutoProgram.getProgramId(),
										autoProgramComponent.getComponentWinId(), autoProgramComponent.getCompId(), cidsTobeUpdated);*/
								componentsAndContactsDaoForDML.updateActivityDateAndPath(cal, tempAutoProgram.getProgramId(),
										autoProgramComponent.getComponentWinId(), autoProgramComponent.getCompId(), cidsTobeUpdated);
								
								
								List<ProgramOnlineReports> progOnlineRepList = new ArrayList<ProgramOnlineReports>();
								
								
								
								List<ComponentsAndContacts> tempList = componentsAndContactsDao.getByContactIds(cidsTobeUpdated, tempAutoProgram.getProgramId());
								if(tempList != null && tempList.size() > 0) {
									for (ComponentsAndContacts compContactsId : tempList) {
										
										ProgramOnlineReports programOnlineReports = new ProgramOnlineReports(compContactsId, 
												autoProgramComponent.getComponentWinId(), cal.getTime(),
												tempAutoProgram.getProgramId(), autoProgramComponent.getCompId(), compContactsId.getContactId());
										
										progOnlineRepList.add(programOnlineReports);
										
										
										
									}//for
									
									//programOnlineReportsDao.saveByCollection(progOnlineRepList);
									programOnlineReportsDaoForDML.saveByCollection(progOnlineRepList);

								}//if
								
							}//if
							
						}//if
						
						
					}//if
					
					
					
					/*cidLst = contactsDao.getContactIdsFromTempContacts(autoProgramComponent.getCompId(), strtIndex, size);
					if(cidLst != null && cidLst.size()>0) {
					
						for (Long cid : cidLst) {
							
							
							componentsAnContacts = new ComponentsAndContacts(tempAutoProgram.getProgramId(), userId,
									cid, autoProgramComponent.getCompId(), autoProgramComponent.getStage()
									, autoProgramComponent.getComponentWinId(), "", cal);
			
							listTobeSaved.add(componentsAnContacts);
							
						}//for
						
						componentsAndContactsDao.saveByCollection(listTobeSaved);
						
					}//if
					strtIndex += size;
						
				}while(cidLst.size() == size);
					
				size = 100;
				strtIndex = 0;
				cidLst.clear();
				int updatedCount;
				String qry = "select cid from tempcontacts ";
				String contactIdStr = "";
				do{
					
					 * this will helps to update the components_contacts for those contacts 
					 * for whom this activity has been performed recently
					 
					cidLst = contactsDao.executeQuery(qry, strtIndex, size);
					if(cidLst != null & cidLst.size() >0) {
						
						for(Long cid :cidLst) {
							
							if(contactIdStr.length() > 0) contactIdStr += ",";
							contactIdStr += ""+cid;
							
							
						}
						

						updatedCount = componentsAndContactsDao.updateActivityDateAndPath(cal, 
										tempAutoProgram.getProgramId(), autoProgramComponent.getComponentWinId(),
										autoProgramComponent.getCompId(), contactIdStr);
						
						logger.info("the number of entries updated in components_contacts for this program are..."+updatedCount);
						
					}
					*/
					if(cidLst != null && cidLst.size() > 0) {
						cidLst.clear();
					}
					listTobeSaved.clear();
					if(retListOfComps != null && retListOfComps.size() > 0) {
						retListOfComps.clear();
					}
					
					if(contactList != null && contactList.size() > 0) {
						contactList.clear();
					}
					
					contactEmailSb.setLength(0);
					
				}while(cidLst.size() == size);
			
				/*int size = 0;
				int strtIndex = 0;
				int endIndex = 0;
				ComponentsAndContacts componentsAnContacts = null;
				List<ComponentsAndContacts> listTobeSaved = null;
				
				if(cidLst != null && cidLst.size()>0) {
					size = cidLst.size();
					
					if(!(size <= 500)) size=500; 
					
					listTobeSaved = new ArrayList<ComponentsAndContacts>();
					
					do{
						
						for (int i = strtIndex; i < size; i++) {
							componentsAnContacts = new ComponentsAndContacts(tempAutoProgram.getProgramId(), userId,
													cidLst.get(i), autoProgramComponent.getCompId(), autoProgramComponent.getStage()
													, autoProgramComponent.getComponentWinId(), cal);
							
							listTobeSaved.add(componentsAnContacts);
							endIndex++;
							
						}//for
						
						componentsAndContactsDao.saveByCollection(listTobeSaved);
						
						listTobeSaved.clear();
						
						strtIndex += size;
						
						size = cidLst.size()-endIndex;
						if(!(size <= 500)) size=500; 
						
						
						
					}while(endIndex != cidLst.size());
					
					
				}//if*/
				
				//String qry = "UPDATE components_contacts SET activity_date='"+cal.toString()+"' WHERE program_id="+tempAutoProgram.getProgramId();
				
				
				
				
				/*qry = "DELETE FROM temp_activity_data WHERE label='"+autoProgramComponent.getLabel()+"' " +
						"AND program_id="+tempAutoProgram.getProgramId();
				
				tempActivityDataDaoForDML.executeJdbcUpdateQuery(qry);
				*/
				
				/*String qry = "DELETE FROM temp_activity_data where label ='"+autoProgramComponent.getLabel()+"" +
							 "' AND component_id="+autoProgramComponent.getCompId();
				
				tempActivityDataDaoForDML.executeJdbcUpdateQuery(qry);
				*/
				
				//deleting the records from temp_activity_data
				tempActivityDataDaoForDML.deleteTempActivityContactsData(autoProgramComponent.getLabel(), autoProgramComponent.getCompId());
				
			}//if statuscode=0
			
			
		}catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		

		//System.gc();

		if(logger.isInfoEnabled()) logger.info(">>>>>>>>> Campaign processing is completed");

		
	}// processEmailSendingActivity
	
	/**
	 * new implementation of event trigger
	 * @param eventTrigger
	 */
	private void processEventTrigger(EventTrigger eventTrigger) {
		try{
		boolean suspeciouslylow =false;
		MailingList mailingList;
		Campaigns campaignToBeSent =null;//take this from event trigger
		Users usersObj = eventTrigger.getUsers();//trigger owner
		
		MailingListDao mailingListDao;
		
		Set<String> urlSet = null;
		Set<MailingList> mlSet = null;
		
		String htmlContent = null;
		String textContent = null;
		String listIdsStr = "";
		String listNameStr = "";
		Users user = eventTrigger.getUsers();
		
		int optionsFlag = eventTrigger.getOptionsFlag();
		int sendCampaignOptionsFlag = optionsFlag & Constants.ET_SEND_CAMPAIGN_FLAG;
		//int sendSMSCampaignOptionsFlag = optionsFlag & Constants.ET_SEND_SMS_CAMPAIGN_FLAG;
		
		//TODO need to confirm it once
		if( (sendCampaignOptionsFlag != Constants.ET_SEND_CAMPAIGN_FLAG)  ) {
			
			if(logger.isInfoEnabled()) logger.info("<<<<<<<<< No Campaign has configured as Trigger Action >>>>>>>>>");
			return;
			
		}//if
		
		short categoryWeight = 1;
		
		isSendCampaignFlagSet = ((optionsFlag & Constants.ET_SEND_CAMPAIGN_FLAG)== Constants.ET_SEND_CAMPAIGN_FLAG?
				true:false);
		
		
		mlSet = eventTrigger.getMailingLists();
			
			 // only if send_campaign flag is set

			if(logger.isInfoEnabled()) logger.info("send campaign flag set ");
			campaignToBeSent = campaignsDao.findByCampaignId(eventTrigger.getCampaignId());

			if( campaignToBeSent == null ) { // validating campaignToBeSent

				if(logger.isInfoEnabled()) logger.info("Exiting processTrigger as no campaign was found with campaignId "+eventTrigger.getCampaignId());
				return;
			}

			if(campaignToBeSent.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL)
					&& campaignToBeSent.getLabel()==null){

				if(logger.isErrorEnabled()) logger.error("EmailFile is not configured for ExternalEmail type campaign: "+campaignToBeSent.getCampaignName());
				return;
			}
			else if(campaignToBeSent.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL) &&
					campaignToBeSent.getLabel().trim().startsWith("SENT_")==false) { // if editorType == "ExternalEmail"

				String emailFileName = campaignToBeSent.getLabel();

				//			    String userParentDir = PropertyUtil.getPropertyValue("usersParentDirectory");
				String eeProcessFolder = PropertyUtil.getPropertyValue("externalEmailProcessFolder");

				File newEmlFile = new File(eeProcessFolder, "Ready" + File.separator +emailFileName);

				if(!newEmlFile.exists()) {
					if(logger.isErrorEnabled()) logger.error("EmailFile is not present: "+newEmlFile.getAbsolutePath());
					return;
				}

				//TODO need to fill the below variables
				htmlContent = campaignToBeSent.getHtmlText();
				textContent ="Dummy TEXT Content";
				urlSet = new HashSet<String>();

			}else { // not external email

				if(htmlContent == null){
					htmlContent = PrepareFinalHTML.prepareStuff(campaignToBeSent);
					
					
					
					if(htmlContent == null ) {
						if(logger.isWarnEnabled()) logger.warn("*********** Found conent as null for the campaign :"+campaignToBeSent.getCampaignName());
						return;
					}

					if(!campaignToBeSent.isPrepared()){

						campaignToBeSent.setFinalHtmlText(htmlContent);
						campaignToBeSent.setPrepared(true);

						try{
							if(logger.isInfoEnabled()) logger.info(" campaignToBeSent is saving ");
							//campaignsDao.saveOrUpdate(campaignToBeSent);
							campaignsDaoForDML.saveOrUpdate(campaignToBeSent);
						}
						catch(Exception e){
							logger.error(" Campaign could not be saved "+campaignToBeSent.getCampaignName(), e);
						}
					}
					/*else{
					 * 
						htmlContent = campaignToBeSent.getFinalHtmlText();
					}*/ 

				} // html==null

				if(htmlContent == null ) {
					if(logger.isWarnEnabled()) logger.warn("*********** Found conent as null for the campaign :"+campaignToBeSent.getCampaignName());
					return;
				}
				textContent = campaignToBeSent.getTextMessage();

				if(textContent == null) {
					textContent = "";
				}

				//** Adding text footer to text message ***

				textContent += "\r\n\r\n This email was sent to |^email^| by |^senderEmail^|  " +
				" \r\n To unsubscribe this mail, (" + UNSUBSCRIBE_URL +")";

				textContent = textContent.replace("[", "[[").replace("|^", "[").replace("^|", "]");
				campaignToBeSent.setTextMessage(textContent);
				urlSet = getUrls(htmlContent);

				if(logger.isDebugEnabled())
					logger.info("Url in the content :"+urlSet);

			} // if
			if(mlSet == null || mlSet.size() <= 0) {
				
				logger.error(" No mailing lists found for trigger :"+eventTrigger.getId().longValue());
						
				return;
				
			}
			
			for (Iterator<MailingList> iterator = mlSet.iterator(); iterator.hasNext();) {

				mailingList = iterator.next();
				
				/*if(mlUser.getParentUser() != null) {
					
					mlUser = mlUser.getParentUser();
				}*/

				// Return if campaign associated mailing list is under going purge process .
				if(mailingList.getStatus() != null && mailingList.getStatus().equalsIgnoreCase(Constants.MAILINGLIST_STATUS_PURGING)) {
					Messages messages = new Messages("Event Trigger Module", "Event Trigger : "+ eventTrigger.getTriggerName()+ " could not start." , 
							"Email sending could not start as the associated Contact list(s) is under purge process," +
							" email will start once purging is completed.",
							Calendar.getInstance(),  "Inbox", false, "Info", user);
					//messagesDao.saveOrUpdate(messages);
					messagesDaoForDML.saveOrUpdate(messages);
					return;
				}
				
				if(listIdsStr.length() == 0) {
					listIdsStr += mailingList.getListId();
					listNameStr += mailingList.getListName();
				}
				else {
					listIdsStr += "," + mailingList.getListId();
					listNameStr += "," + mailingList.getListName(); 
				}
			}
			
			
			categoryWeight = campaignToBeSent.getCategoryWeight();//no need

			
			getCustomFields(htmlContent+campaignToBeSent.getSubject()+campaignToBeSent.getTextMessage());
			
			
		
		//generate the trigger query combining with the contacts-event source table-events table
		//String triggerQuery = TriggerQueryGenerator.SALES_ON_PRODUCT_QRY;//TODO need to generalize
		
		String triggerQuery = TriggerQueryGenerator.prepareTriggerQuery(usersObj.getUserId().longValue(), eventTrigger);
		
		
		String queryTobeInserted = " INSERT IGNORE INTO tempcontacts (" +Constants.QRY_COLUMNS_TEMP_CONTACTS+" ,cf_value, domain, event_source_id)" +
									"("+triggerQuery+")";
		
		//find about user credits
		int totalSizeInt = 0;
		String msgStr = null;
		int configured = 0;
		int suppressed = 0;
		int preferenceCount =0;
		Calendar cal = Calendar.getInstance();

		String useMqsStr = PropertyUtil.getPropertyValueFromDB("useMQS");
		boolean useMQS =( useMqsStr == null ? true : useMqsStr.equalsIgnoreCase("true"));
		Long userId = null;

		userId = eventTrigger.getUsers().getUserId();

		if(logger.isInfoEnabled()) {

			logger.info(" Inserting contacts to tempcontacts using the query :"+
					queryTobeInserted);
		}
		
		//configured = contactsDao.executeJdbcUpdateQuery(queryTobeInserted);
		try {
			configured = contactsDaoForDML.executeJDBCInsertQuery(queryTobeInserted);//(queryTobeInserted);
		} catch (Exception e) {
			logger.debug("in catch of Exception");
			if(e instanceof CannotAcquireLockException || e instanceof DeadlockLoserDataAccessException) {
				logger.debug("in catch of CannotAcquireLockException OR DeadlockLoserDataAccessException");
				currentRunningObj = new Object();
				List<EventTrigger> rePutEventTriggerList = new ArrayList<EventTrigger>();
				rePutEventTriggerList.add(eventTrigger);
				queue.addCollection(rePutEventTriggerList);
				return;
			}
		}
		totalSizeInt = contactsDaoForDML.createTempContacts(categoryWeight,  userId, configured);

		suppressed = configured-totalSizeInt;
		
		// Added for subscriber preference count
		if(eventTrigger.getUsers().getSubscriptionEnable()){
			
			
			preferenceCount = contactsDaoForDML.createPreferenceTempContacts(eventTrigger.getCampCategory() != null ? eventTrigger.getCampCategory() : campaignToBeSent.getCategories() );
			totalSizeInt = totalSizeInt - preferenceCount;
		}
    	
		if(totalSizeInt == 0) {

			if(logger.isInfoEnabled()) logger.info(" Got "+totalSizeInt+" contacts into temp_contacts: exiting trigger Campaign");
			return;
		}
		if(logger.isInfoEnabled()) logger.info(" TotalSizeInt :"+totalSizeInt);


		if(logger.isInfoEnabled()) logger.info("deleting already sent members from Temp contacts");

		//no need this
		//totalSizeInt = filterTempContacts(eventTrigger, totalSizeInt); // check for args (userId, totalSizeInt)

		if(totalSizeInt <= 0) {

			if(logger.isWarnEnabled()) logger.warn(">>>>>>> Trigger campaign submission failed, found 0 active contacts"+campaignToBeSent);
			/*msgStr = "Email Name :"+eventTrigger.getTriggerName()+"\n"+
			"Found 0 Active contacts in the mailing lists which " +
			"are configured for the Email campaignToBeSent.\n" +
			"Reason may be configured contacts are existed in the suppression contacts list " +
			"or contacts status changed by user/recipient himself unsubscribed";

			Messages messages = new Messages("Email Details ", "No Active contacts",
					msgStr, cal,  "Inbox", false, "Info", eventTrigger.getUsers());
			messagesDaoForDML.saveOrUpdate(messages);*/
			return;
		}
		/*if(totalSizeInt < 0){
			if(logger.isInfoEnabled()) logger.info("Exception while deleting from tempcontacts- exiting");
			return;
		}

		if(totalSizeInt == 0){
			if(logger.isInfoEnabled()) logger.info("After deletion from tempcontacts number of records to process are "+totalSizeInt+"...exiting processTrigger");
			return;
		}*/
		
		if(!useMQS) {

			boolean success = false;
			List<Integer> list = new ArrayList<Integer>();
			try {

				list = usersDao.getAvailableCountOfUser(userId);

				if(list.get(0).intValue() < totalSizeInt ) {
						suspeciouslylow = true;
						/*	Double result = (double) Math.signum(list.get(0).intValue());
			        if(result.equals(-1.0) || result.equals(0.0)) {
						if(logger.isWarnEnabled()) logger.warn(" Available limit is less than the configured contacts count");
						msgStr =  "Event Trigger Name : " + eventTrigger.getTriggerName() + "\r\n";
						msgStr = msgStr +" \r\n Status : Could not sent \r\n";
						msgStr = msgStr + "Event Trigger could not be sent as you have reached the limit of emails hence email is stopped \r\n" ;
						msgStr = msgStr + "Available Email you can send :"+list.get(0).intValue() +"\r\n";
						msgStr = msgStr+ "You have configured no contacts for the email "+eventTrigger.getTriggerName() +" is :"+totalSizeInt;
						//return;
			        }
				*/}
				
				//make a decission whether coupon codes will be sufficient or not
				CouponCodesDao couponCodesDao = (CouponCodesDao)context.getBean("couponCodesDao");
				CouponsDao couponsDao = (CouponsDao)context.getBean("couponsDao");
				
				
				if(totalPhSet != null && totalPhSet.size() > 0) {
					
						Coupons coupon = null;
						Long couponId = null; 
						for (String eachPh : totalPhSet) {
							if(!eachPh.startsWith("CC_") ) continue;
							//only for CC
								
								
								String[] strArr = eachPh.split("_");
								
								if(logger.isDebugEnabled()) logger.debug("Filling  Promo-code with Id = "+strArr[1]);
								try {
									
									couponId = Long.parseLong(strArr[1]);
									
								} catch (NumberFormatException e) {
									
									couponId = null;
								
								}
								
								if(couponId == null) {
									
									//TODO need to delete it from phset or???????????????????????
									continue;
									
								}
								coupon = couponsDao.findById(couponId);
								
								if(coupon == null) {
									msgStr =  "Event Trigger Name : " + eventTrigger.getTriggerName() + "\r\n";
									msgStr = msgStr +" \r\n Status : Could not sent \r\n";
									msgStr = msgStr + "Email campaign Name : '"+campaignToBeSent.getCampaignName()+"' associated to this event trigger" +
											" could not be sent as you have added  Promotion: "+eachPh +" \r\n" ;
									msgStr = msgStr + "This  Promotion is no longer exists, you might have deleted that. : \r\n";
									
									if(logger.isWarnEnabled()) logger.warn(eachPh + "  Promo-code is not avalable: "+ eachPh);
									return;
									
									
								}
								
								
								//only for running coupons
//								if(!coupon.getStatus().equals(Constants.COUP_STATUS_RUNNING)) {
								if(coupon.getStatus().equals(Constants.COUP_STATUS_EXPIRED) || 
										coupon.getStatus().equals(Constants.COUP_STATUS_PAUSED)) {
									
									msgStr =  "Event Trigger Name : " + eventTrigger.getTriggerName() + "\r\n";
									msgStr = msgStr +" \r\n Status : Could not sent \r\n";
									msgStr = msgStr + "Event Trigger could not be sent as you have added  Promotion : "+coupon.getCouponName() +" \r\n" ;
									msgStr = msgStr + "This  Promotion Status :"+coupon.getStatus()+" and  valid period :"+ 
									MyCalendar.calendarToString(coupon.getCouponCreatedDate(), MyCalendar.FORMAT_DATETIME_STDATE) +" to "+
									MyCalendar.calendarToString(coupon.getCouponExpiryDate(), MyCalendar.FORMAT_DATETIME_STDATE) +" \r\n";
									
									if(logger.isWarnEnabled()) logger.warn(coupon.getCouponName() + "  Promotion is not in running state, Status : "+ coupon.getStatus());
									return;

									
									
								}//if
								
								
								if( coupon.getAutoIncrCheck() == true ) {
									continue;
								}
								else if(coupon.getAutoIncrCheck() == false) {
									//need to decide only when auto is false
									//List<Integer> couponCodesList = couponCodesDao.getInventoryCCCountByCouponId(couponId);
									long couponCodeCount = couponCodesDao.getCouponCodeCountByStatus(couponId, Constants.COUP_CODE_STATUS_INVENTORY);
									if(couponCodeCount < totalSizeInt ) {
										
										
										msgStr =  "Event Trigger Name : " + eventTrigger.getTriggerName() + "\r\n";
										msgStr = msgStr +" \r\n Status : Could not sent \r\n";
										msgStr = msgStr + "Event Trigger could not be sent as you have added  Promotion : "+coupon.getCouponName() +" \r\n" ;
										msgStr = msgStr + "Available  Promo-codes you can send :"+couponCodeCount+" \r\n";
										
										if(logger.isWarnEnabled()) logger.warn(" Available  Promo-codes  limit is less than the configured contacts count");
										return;
									}
								
								}//else 
							
						}//for
					/*} catch (Exception e) {
						// TODO Auto-generated catch block
						if(logger.isErrorEnabled()) logger.error("** Exception while getting the promo count" +
								" for the user:"+userId, e);
						success= false;
					}*/
					
				}//if
				
				//shifted
				success = true;
				
				
				
			}
			catch (Exception e) {

				if(logger.isErrorEnabled()) logger.error("** Exception while getting the available count" +
						" for the user:"+userId, e);
				success= false;
				return;
			}
			finally {

				if(!success  && msgStr != null && !msgStr.isEmpty()) {
					
					String module = "Event Trigger";
					String subject = "Event Trigger send failed";
					//String qry = "FROM Messages WHERE users.userId="+mlUser.getUserId().longValue()+" AND subject='"+subject+"' AND module='"+module+"' AND message='"+StringEscapeUtils.escapeSql(msgStr)+"' AND DAY(createdDate) = DAY(NOW())";
					//logger.info("qry::"+qry);
					boolean isMsgExists = messagesDao.findSameMsgWithInSameDay(user, msgStr, subject, module);
					
					
					//logger.info("ismsgExists::"+isMsgExists);
					if(!isMsgExists) {

						eventTrigger.setStatus("Failed due to less user count");
	
						Messages msgs = new Messages(module, subject,
								msgStr, cal, "Inbox", false, "Info", eventTrigger.getUsers());
	
						//messagesDao.saveOrUpdate(msgs);
						messagesDaoForDML.saveOrUpdate(msgs);
					}
				}	//success

			}	//finally

		}
		else {
			//TODO
			/**
			 * Need to write a code to contact with MQService class to get the available
			 * limit of the user by giving the mqs Id (Customer id)
			 */

		}// if use mqs

		//about to send
		/***************** Create Campaign Report Object *****************/
		CampaignReport campaignReport=null;
		if(logger.isInfoEnabled()) logger.info("fromSource is: "+fromSource);

		//inserting triggername instead of campaign name and adding sourcetype in campiagn_reports table

		campaignReport = new CampaignReport( usersObj, eventTrigger.getTriggerName(),
				campaignToBeSent.getSubject(), htmlContent,cal, 0, 0, 0, 0, 0,
				Constants.CR_STATUS_SENDING, fromSource);
		
		
		String placeHoldersStr = "";
		
		if(totalPhSet.size() > 0) {
			
			for(String ph:totalPhSet){
				
				if(ph.startsWith("GEN_")) {
					
					ph = ph.substring(4);
 					if(placeHoldersStr.trim().length() > 0) placeHoldersStr += "||";
 					placeHoldersStr += ph;
					
				}//if
			}//for
			
			campaignReport.setPlaceHoldersStr(placeHoldersStr);
		}
		

		if(urlSet.size()>0) {
			campaignReport.setUrls(urlSet);
			if(logger.isDebugEnabled()) logger.info(" Url in the campaign :"+urlSet);
		}

		try {
			//campaignReportDao.saveOrUpdate(campaignReport);
			campaignReportDaoForDML.saveOrUpdate(campaignReport);
			CampReportLists campReportLists = new CampReportLists(campaignReport.getCrId());
			campReportLists.setListsName(listNameStr);
			campReportLists.setSegmentQuery(null);
			//campReportListsDao.saveOrUpdate(campReportLists);
			campReportListsDaoForDML.saveOrUpdate(campReportLists);

		}
		catch (Exception e) {
			logger.error("** Exception : while saving the campaign report", e);
			return;
		}

		if(logger.isDebugEnabled()) {
			logger.info("campaign report object created and saved with the status as sending");
		}


		if(logger.isInfoEnabled()) logger.info(" submitting the campaign to PMTA");
		
		boolean sendGridTrigflag = false;
		byte statusCode  = -1;
		//Vmta vmta = campaignToBeSent.getUsers().getVmta(); 
		
	//	Vmta vmta = user.getVmta();
		
		UsersDao usersDao = (UsersDao)context.getBean("usersDao");
		VmtaDao vmtaDao = (VmtaDao)context.getBean("vmtaDao");
		UserVmta userVmta = usersDao.findBy(user.getUserId(), Constants.SENDING_TYPE_BULK);
		Vmta vmta = null;
		
		if(userVmta != null) {
				vmta = vmtaDao.find(userVmta.getVmtaId());
				
			}
		else {
			vmta = user.getVmta();
		}

		//String vmtaName = vmta.getVmtaName();
		if(vmta == null) sendGridTrigflag = false;
		else if(vmta != null){
		//	if(Constants.SMTP_SENDGRIDAPI.equalsIgnoreCase(vmta.getVmtaName()) || user.getVmta().getVmtaName().equalsIgnoreCase("AmazonSES")) {
		//if(vmtaName != null && "SendGridAPI".equalsIgnoreCase(vmtaName)) {
			// Send From SendGrid API .
			
			if(eventTrigger == null) {
				if(logger.isDebugEnabled()) logger.debug("Scheduling of EventTrigger is null");
				return;
			}
			ExternalSMTPSender sendGridSender = new ExternalSMTPSender(vmta, context);			
			sendGridTrigflag = sendGridSender.submitCampaign(campaignToBeSent, htmlContent, textContent, campaignReport, totalSizeInt, eventTrigger, fromSource,vmta);
			
			/*if(sendGridTrigflag) {
				if(logger.isInfoEnabled()) logger.info("campaign submission is completed ");
				msgStr = "Email Name :"+campaignToBeSent.getCampaignName()+"\n"+
				"Sent successfully to "+campaignReport.getSent()+" unique contacts";
			} else {
				if(logger.isInfoEnabled()) logger.info("campaign submission is failed ");
				msgStr = "Email Name  :"+campaignToBeSent.getCampaignName()+"\n"+
						 //TODO:"Status code :"+statusCodeByte+"\n"+
						 "Submission is failed, please report the problem to admin";

				campReportListsDao.deleteByCampRepId(campaignReport.getCrId());
				campaignReportDao.delete(campaignReport);
			}*/
		//	}
		} /*else {
		
			statusCode = senderThread.submitCampaign(campaignToBeSent, htmlContent, textContent,
					campaignReport, totalSizeInt,eventTrigger, fromSource);
		}*/

		String strName=null;

		strName = eventTrigger.getTriggerName();
		if(logger.isInfoEnabled()) logger.info("submitTrigger statusCode ="+statusCode);
		if(statusCode == 0 || sendGridTrigflag) {

			//no need external email kind of stuff
			/*if(campaignToBeSent.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL) &&
					campaignToBeSent.getLabel().trim().startsWith("SENT_")==false) {

				campaignToBeSent.setLabel("SENT_"+campaignToBeSent.getLabel().trim());
				urlSet = getUrls(campaignToBeSent.getFinalHtmlText());
				campaignsDao.saveOrUpdate(campaignToBeSent); // update the HtmlContent.
				campaignReport.setContent(campaignToBeSent.getHtmlText());
				campaignReport.setUrls(urlSet);
			} // if "ExternalEmail"
*/
			if( ((optionsFlag & Constants.ET_ADD_CONTACTS_TO_ML_FLAG) == Constants.ET_ADD_CONTACTS_TO_ML_FLAG)
					|| ((optionsFlag & Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG) == Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG) )  {

				if(logger.isInfoEnabled()) logger.info("===========entered to copy/remove===========");
				
				int updatedCount = copyOrRemoveEtContacts(eventTrigger);
				
				if(logger.isInfoEnabled()) logger.info("===========completed copy/remove==========="+updatedCount);

				if(updatedCount < 0) {
					
					if(logger.isInfoEnabled()) logger.info("Unable to perform copy/remove operations due to some Exception...updatedCount = "+updatedCount);
					//TODO check what has to be written here
				}

				msgStr =  "Event Trigger Name : " + eventTrigger.getTriggerName() + "\r\n";
				msgStr = msgStr +" \r\n Copy/Remove was \r"+(updatedCount > 0 ? "Successful":"unsuccessFul")+" \n";
				Messages msgs = new Messages("Event Trigger", "Contacts copied/moved ",
						msgStr, cal, "Inbox", false, "Info", eventTrigger.getUsers());

				//messagesDao.saveOrUpdate(msgs);
				messagesDaoForDML.saveOrUpdate(msgs);
			}

			if(logger.isInfoEnabled()) logger.info("campaign submission is completed successfully");

			msgStr = "Trigger Email Name :"+strName+"\n"+
			"Sent successfully to "+campaignReport.getSent()+" unique contacts";

		}
		else {

			if(logger.isInfoEnabled()) logger.info("campaign submission is failed ");

			msgStr = "Email Name  :"+strName+"\n"+
			"Status code :"+statusCode+"\n"+
			"Submission is failed, please report the problem to admin";
			//campReportListsDao.deleteByCampRepId(campaignReport.getCrId());
			campReportListsDaoForDML.deleteByCampRepId(campaignReport.getCrId());

			campaignReportDaoForDML.delete(campaignReport);

		}


		if(msgStr != null && (statusCode == 0 || sendGridTrigflag)) {

			Messages messages = new Messages("Email Details ",(statusCode == 0 || sendGridTrigflag) ? "Email sent successfully"
					:"Email sending failed.",msgStr, cal,  "Inbox", false, "Info",usersObj);

			// update Used Email Count in Users table
			if(!useMQS) {

				//usersDao.updateUsedEmailCount(campaign.getUsers().getUserId(), totalSizeInt);
				//usersDao.updateUsedEmailCount(userId, totalSizeInt);
				usersDaoForDML.updateUsedEmailCount(userId, totalSizeInt);
				
				int thresholdUserCredit = ((int) Math.ceil((user.getEmailCount() * 10) / 100));
                List<Integer> usercreditlist = usersDao.getAvailableCountOfUser(user.getUserId());
                if(usercreditlist.get(0).intValue() <= thresholdUserCredit) {
               	 	suspeciouslylow = true;
                }
				
				if(suspeciouslylow == true) {
					sendMailtoSupportForNegativeEmailCreditScore(user, campaignToBeSent, null);
				}
				
				 /**********update each coupon issued count***********************************/
				
				try {
					Utility.updateCouponCodeCounts(context, totalPhSet);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("** Exception while updating the coupons", e);
				}
				
			}
			else {

				//TODO need to write code to call MQS service Process order to update
				// the usage
			}
			//messagesDao.saveOrUpdate(messages);
			messagesDaoForDML.saveOrUpdate(messages);
			campaignReport = campaignReportDao.findById(campaignReport.getCrId());
			
			if(campaignReport == null) {
				
				if(logger.isDebugEnabled()) logger.debug("no report found with the given ID");
				
			}
			else {
				campaignReport.setConfigured(configured);
				campaignReport.setSuppressed(suppressed);
				campaignReport.setSent(totalSizeInt);
				campaignReport.setPreferenceCount(preferenceCount);
				campaignReport.setStatus(Constants.CR_STATUS_SENT);
				//campaignReportDao.saveOrUpdate(campaignReport);
				campaignReportDaoForDML.saveOrUpdate(campaignReport);
				
				//campaignReportDao.updateCampaignReport(campaignReport.getCrId(), sentId, type);
				//set the campaign report id for campaign schedule object and status code 1 (sent)
				int updateCount = 0;
				String fromTable = Constants.STRING_NILL;
				if( (eventTrigger.getTrType() & Constants.ET_TYPE_ON_CONTACT_DATE) <= 0) {
					
					fromTable = "event_trigger_events";
				}else{
					fromTable = "contact_specific_date_events";
				}
				
				//updateCount = eventTriggerDao.updateEmailStatusFromEvents(userId, eventTrigger.getId(),campaignReport.getCrId(), fromTable);
				updateCount = eventTriggerDaoForDML.updateEmailStatusFromEvents(userId, eventTrigger.getId(),campaignReport.getCrId(), fromTable);
				//eventTriggerDao.updateEventsSentId(userId, eventTrigger.getId(), campaignReport.getCrId(), fromTable);
				eventTriggerDaoForDML.updateEventsSentId(userId, eventTrigger.getId(), campaignReport.getCrId(), fromTable);
				
				if(logger.isInfoEnabled()) logger.info(">>>>>>>>> Trigger processing is completed, satus updated ::"+updateCount);
				
				
			}
			
			
			eventTrigger.setLastSentDate(cal);
			//eventTriggerDao.saveOrUpdate(eventTrigger);
			eventTriggerDaoForDML.saveOrUpdate(eventTrigger);
			

		}	//if msgStr != null
		
		
	}
	catch (Exception e)	{
		logger.error("** Exception", e);
	}
	if(logger.isInfoEnabled()) logger.info("just before calling garbage collector");
	//System.gc();

	if(logger.isInfoEnabled()) logger.info(">>>>>>>>> Trigger processing is completed, exiting processTrigger");

		
		
		
	}//processEventTrigger
	
	/**@deprecated
	 * ProcessTrigger
	 * @param eventTrigger
	 * this function is added to include EventTrigger processing.
	 * It is similar to processCampaign() except this is proccesing a triggercampaign
	 */
	private void processTrigger(EventTrigger eventTrigger){

		if(logger.isInfoEnabled()) logger.info(">>>> Inside processTrigger with triggerName :"+eventTrigger.getTriggerName());

		try {
			
			MailingList mailingList;
			Campaigns campaignToBeSent =null;
			Users usersObj = eventTrigger.getUsers();
			
			MailingListDao mailingListDao;
			
			Set<String> urlSet = null;
			Set<MailingList> mlSet = null;
			
			String htmlContent = null;
			String textContent = null;
			String listIdsStr = "";
			String listNameStr = "";
			
			int optionsFlag = eventTrigger.getOptionsFlag();
			int sendCampaignOptionsFlag = optionsFlag & Constants.ET_SEND_CAMPAIGN_FLAG;
			
			short categoryWeight = 1;

			
			isOpensFlagSet = (eventTrigger.getTriggerType().equals(Constants.ET_TYPE_CAMPAIGN_OPEN)?true:false);
			isClicksFlagSet = (eventTrigger.getTriggerType().equals(Constants.ET_TYPE_CAMPAIGN_CLICK)?true:false);
			isCdateFlagSet = (eventTrigger.getTriggerType().equals(Constants.ET_TYPE_CONTACT_DATE)?true:false);
			isSdateFlagSet = (eventTrigger.getTriggerType().equals(Constants.ET_TYPE_SPECIFIC_DATE)?true:false);
			//isMergeCFSet = ((optionsFlag & Constants.ET_MERGE_CUSTOMFIELDS) == Constants.ET_MERGE_CUSTOMFIELDS?true:false);
			isSendCampaignFlagSet = ((optionsFlag & Constants.ET_SEND_CAMPAIGN_FLAG)== Constants.ET_SEND_CAMPAIGN_FLAG?
					true:false);

			if(sendCampaignOptionsFlag == Constants.ET_SEND_CAMPAIGN_FLAG) { // only if send_campaign flag is set

				if(logger.isInfoEnabled()) logger.info("send campaign flag set ");
				campaignToBeSent = campaignsDao.findByCampaignId(eventTrigger.getCampaignId());

				if( campaignToBeSent == null ) { // validating campaignToBeSent

					if(logger.isInfoEnabled()) logger.info("Exiting processTrigger as no campaign was found with campaignId "+eventTrigger.getCampaignId());
					return;
				}

				if(campaignToBeSent.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL)
						&& campaignToBeSent.getLabel()==null){

					if(logger.isErrorEnabled()) logger.error("EmailFile is not configured for ExternalEmail type campaign: "+campaignToBeSent.getCampaignName());
					return;
				}
				else if(campaignToBeSent.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL) &&
						campaignToBeSent.getLabel().trim().startsWith("SENT_")==false) { // if editorType == "ExternalEmail"

					String emailFileName = campaignToBeSent.getLabel();

					//			    String userParentDir = PropertyUtil.getPropertyValue("usersParentDirectory");
					String eeProcessFolder = PropertyUtil.getPropertyValue("externalEmailProcessFolder");

					File newEmlFile = new File(eeProcessFolder, "Ready" + File.separator +emailFileName);

					if(!newEmlFile.exists()) {
						if(logger.isErrorEnabled()) logger.error("EmailFile is not present: "+newEmlFile.getAbsolutePath());
						return;
					}

					//TODO need to fill the below variables
					htmlContent = campaignToBeSent.getHtmlText();
					textContent ="Dummy TEXT Content";
					urlSet = new HashSet<String>();

				}else { // not external email

					if(htmlContent == null){

						if(!campaignToBeSent.isPrepared()){

							htmlContent = PrepareFinalHTML.prepareStuff(campaignToBeSent);
							campaignToBeSent.setFinalHtmlText(htmlContent);
							campaignToBeSent.setPrepared(true);

							try{
								if(logger.isInfoEnabled()) logger.info(" campaignToBeSent is saving ");
								//campaignsDao.saveOrUpdate(campaignToBeSent);
								campaignsDaoForDML.saveOrUpdate(campaignToBeSent);
							}
							catch(Exception e){
								logger.error(" Campaign could not be saved "+campaignToBeSent.getCampaignName(), e);
							}
						}
						else{
							htmlContent = campaignToBeSent.getFinalHtmlText();
						}

					} // html==null

					if(htmlContent == null ) {
						if(logger.isWarnEnabled()) logger.warn("*********** Found conent as null for the campaign :"+campaignToBeSent.getCampaignName());
						return;
					}
					textContent = campaignToBeSent.getTextMessage();

					if(textContent == null) {
						textContent = "";
					}

					//** Adding text footer to text message ***

					textContent += "\r\n\r\n This email was sent to |^email^| by |^senderEmail^|  " +
					" \r\n To unsubscribe this mail, (" + UNSUBSCRIBE_URL +")";

					//textContent = textContent.replace("[", "[[").replace("|^", "[").replace("^|", "]");
					textContent = textContent.replace("|^", "[").replace("^|", "]");
					campaignToBeSent.setTextMessage(textContent);
					urlSet = getUrls(htmlContent);

					if(logger.isDebugEnabled())
						logger.info("Url in the content :"+urlSet);

				} // if

				categoryWeight = campaignToBeSent.getCategoryWeight();

			} // if campaign send is set


			try {

				//mlSet = getMlSet(eventTrigger);

				if (isCdateFlagSet || isSdateFlagSet) {
					
					mlSet = eventTrigger.getMailingLists();
					
					if(mlSet == null || mlSet.size() <= 0) {
						
						if(logger.isInfoEnabled()) logger.info("Got empty mailing lists....exiting processTrigger");
						return;
					}
					else {
						
						// Return if trigger associated mailing list is under going purge process .
						for (MailingList mailingList2 : mlSet) {
							if(mailingList2.getStatus().equalsIgnoreCase(Constants.MAILINGLIST_STATUS_PURGING)) {
									Messages messages = new Messages("Trigger Module", "Trigger : "+ eventTrigger.getTriggerName() + " could not start." , 
											"Trigger sending could not start as the associated Contact list(s) is under purge process,",
											Calendar.getInstance(),  "Inbox", false, "Info", mailingList2.getUsers());
									//messagesDao.saveOrUpdate(messages);
									messagesDaoForDML.saveOrUpdate(messages);
									if(logger.isInfoEnabled()) logger.info("*** Exception : Event Trigger associated mailinglist(s) is under purging, Hence returning ..");
									return;
								}
							
							if(listNameStr.length() > 0) listNameStr += ",";
							listNameStr += mailingList2.getListName();
							
						}
						
						
						
						listIdsStr = getListIdsStr(mlSet);
						
						
					} // if-else mlSet.size() <= 0

					if(listIdsStr.length() == 0) {
							
							if(logger.isInfoEnabled()) logger.info("No List ids are found: ");
							return;
						
					}  // listIdsStr.length() == 0
					
					

				} // CDATE/SDATE
				
				if(logger.isInfoEnabled()) logger.info("List ids obtd are: "+listIdsStr);

			}
			catch (Exception e) {
				logger.error(" ** Exception" ,e);
				return;
			}

			boolean isSegment = false;

			//TODO Need to write similar code for Trigger condition
			//isSegment = (campaign.getListsType() == null ? false:(campaign.getListsType().equals("Total")?false:true));

			int totalSizeInt = 0;
			String qryStr = null;

			if(isSegment){

				//TODO write isSegment similar to campainschedule in process Campaign
			}
			else {

//				logger.info("before calling triggerQryGenerator");
				qryStr = triggerQryGenerator(eventTrigger, campaignToBeSent, listIdsStr, isSegment);//by this time sourceType will be made up perfectly based on the trigger type
				if(logger.isDebugEnabled()) logger.debug("qryStr=================>"+qryStr);
				
			}

			if(qryStr == null){
				if(logger.isInfoEnabled()) logger.info(" Got a null query str due to Exception of empty trigger Name Exiting");
				return;
			}

			if(qryStr.equals("")){

				if(logger.isInfoEnabled()) logger.info("ProcessTrigger:SOURCE_TRIGGER, got a empty query str...Exiting");
				return;
			}
			
			//EventTrigger SMS changes -begins here

			if((optionsFlag & Constants.ET_SEND_SMS_CAMPAIGN_FLAG) == Constants.ET_SEND_SMS_CAMPAIGN_FLAG){

				if(logger.isInfoEnabled()) logger.info("Send sms flag set ");
				String[] tempSmsQryStr = qryStr.split("tempcontacts");
				String smsQryStr = " INSERT IGNORE INTO sms_tempcontacts "+tempSmsQryStr[1];
				smsQryStr += '|'+fromSource;//here no need to bother about rebuilding of source_type(as it is already built)
				
				if(logger.isDebugEnabled()) logger.debug("smsQryStr after replacing appending fromsource "+smsQryStr);

				eventTrigger.setSmsQueryStr(smsQryStr);
				if(logger.isDebugEnabled()) logger.debug(" eventTrigger.getSmsQueryStr() "+eventTrigger.getSmsQueryStr());
				
				if(eventTrigger.getSmsId() == null){
					if(logger.isErrorEnabled()) logger.error(" no sms object is attached to the trigger ");
				}
				else {
					SMSCampaigns smsCampaigns;
					smsCampaigns = smsCampaignsDao.findByCampaignId(eventTrigger.getSmsId()); 
					
					/*if(smsCampaigns != null){
						
						logger.error("adding sms obj to the queue ");
						smsQueue.addObjToQueue(smsCampaigns);
						
					}
					else{
						logger.error("got  a null sms obj");
					}*/
					
					if(smsCampaigns != null){
						
						if(logger.isErrorEnabled()) logger.error("adding sms obj to the queue ");
						
						
						if(smsQueue == null) {
							if(logger.isDebugEnabled()) logger.debug("smsQueue null");
							
							
						}
						else{
							if(logger.isDebugEnabled()) logger.debug("smsQueue not null");
							List<EventTrigger> eventTrigCollection = new ArrayList<EventTrigger>();
							
							//eventTriggerDao.saveOrUpdate(eventTrigger);
							eventTriggerDaoForDML.saveOrUpdate(eventTrigger);
							if(logger.isDebugEnabled()) logger.debug(" after saving eventTrigger.getSmsQueryStr() "+eventTrigger.getSmsQueryStr());
							eventTrigCollection.add(eventTrigger);
							
							smsQueue.addCollection(eventTrigCollection);
							//smsQueue.addObjToQueue(eventTrigger);
							
							
							if(smsQueue.getQueueSize() > 0) {
								
								SMSCampaignSubmitter.startSMSCampaignSubmitter(context);
								
								
							}
						}//else
					}
					else{
						if(logger.isErrorEnabled()) logger.error("got  a null sms obj");
					}	
					
				}
				
			}
			//EventTrigger SMS changes -ends here
			
			String useMqsStr = PropertyUtil.getPropertyValueFromDB("useMQS");
			boolean useMQS =( useMqsStr == null ? true : useMqsStr.equalsIgnoreCase("true"));
			Long userId = null;

			userId = eventTrigger.getUsers().getUserId();

			if(logger.isDebugEnabled()) {

				logger.info(" Inserting contacts to tempcontacts using the query :"+
						qryStr);
			}
			totalSizeInt = contactsDaoForDML.createTempContacts(qryStr, categoryWeight, userId);

			if(totalSizeInt == 0) {

				if(logger.isInfoEnabled()) logger.info(" Got "+totalSizeInt+" contacts into temp_contacts: exiting trigger Campaign");
				return;
			}
			if(logger.isInfoEnabled()) logger.info(" TotalSizeInt :"+totalSizeInt);


			if(logger.isInfoEnabled()) logger.info("deleting already sent members from Temp contacts");

			totalSizeInt = filterTempContacts(eventTrigger, totalSizeInt); // check for args (userId, totalSizeInt)

			if(totalSizeInt < 0){
				if(logger.isInfoEnabled()) logger.info("Exception while deleting from tempcontacts- exiting");
				return;
			}

			if(totalSizeInt == 0){
				if(logger.isInfoEnabled()) logger.info("After deletion from tempcontacts number of records to process are "+totalSizeInt+"...exiting processTrigger");
				return;
			}

			/*
			 * till here the code is common for normal flow(send_campaign) and copy or remove flow...
			 */

			int updatedCount = -1;
			String msgStr = null;
			Calendar cal = Calendar.getInstance();


			//y this has done before
			if( sendCampaignOptionsFlag == 0) { // check if only copy/remove contacts is selected by the user

				if(logger.isInfoEnabled()) logger.info("sendCampaignOptionsFlag is not set ");

				if(((optionsFlag & Constants.ET_ADD_CONTACTS_TO_ML_FLAG) == Constants.ET_ADD_CONTACTS_TO_ML_FLAG)
						|| ((optionsFlag & Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG) == Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG) )  {

					updatedCount = copyOrRemoveContacts(eventTrigger);

					if(updatedCount <= 0) {

						if(logger.isInfoEnabled()) logger.info("Unable to perform copy/remove operations...updatedCount = "+updatedCount);

					}
					else {
						if(logger.isInfoEnabled()) logger.info("Records copy/move was successful ");

					}


					msgStr =  "Event Trigger Name : " + eventTrigger.getTriggerName() + "\r\n";
					msgStr = msgStr +" \r\n Number of records copied/moved are \r"+updatedCount+" \n";
					Messages msgs = new Messages("Event Trigger", "Contacts copied/moved ",
							msgStr, cal, "Inbox", false, "Info", eventTrigger.getUsers());

					//messagesDao.saveOrUpdate(msgs);
					messagesDaoForDML.saveOrUpdate(msgs);
				} // inner if
				return;
			} // outer if

			//we come here only if there is a campaign to be sent...

			if(logger.isInfoEnabled()) logger.info("we come here only if there is a campaign to be sent...");

			if(!useMQS) {

				boolean success = false;
				List<Integer> list = new ArrayList<Integer>();
				try {

					list = usersDao.getAvailableCountOfUser(userId);

					if(list.get(0).intValue() < totalSizeInt ) {

						if(logger.isWarnEnabled()) logger.warn(" Available limit is less than the configured contacts count");
						return;
					}
					success = true;
				}
				catch (Exception e) {

					if(logger.isErrorEnabled()) logger.error("** Exception while getting the available count" +
							" for the user:"+userId, e);
					return;
				}
				finally {

					if(!success) {

						eventTrigger.setStatus("Failed due to less user count");

						msgStr =  "Event Trigger Name : " + eventTrigger.getTriggerName() + "\r\n";
						msgStr = msgStr +" \r\n Status : Could not sent \r\n";
						msgStr = msgStr + "Event Trigger could not be sent as you have reached the limit of emails hence email is stopped \r\n" ;
						msgStr = msgStr + "Available Email you can send :"+list.get(0).intValue() +"\r\n";
						msgStr = msgStr+ "You have configured no contacts for the email "+eventTrigger.getTriggerName() +" is :"+totalSizeInt;
						Messages msgs = new Messages("Event Trigger", "Event Trigger send failed ",
								msgStr, cal, "Inbox", false, "Info", eventTrigger.getUsers());

						//messagesDao.saveOrUpdate(msgs);
						messagesDaoForDML.saveOrUpdate(msgs);
					}	//success

				}	//finally

			}
			else {
				//TODO
				/**
				 * Need to write a code to contact with MQService class to get the available
				 * limit of the user by giving the mqs Id (Customer id)
				 */

			}// if use mqs


			if(totalSizeInt == 0) {

				if(logger.isWarnEnabled()) logger.warn(">>>>>>> Trigger campaign submission failed, found 0 active contacts"+campaignToBeSent);
				msgStr = "Email Name :"+eventTrigger.getTriggerName()+"\n"+
				"Found 0 Active contacts in the mailing lists which " +
				"are configured for the Email campaignToBeSent.\n" +
				"Reason may be configured contacts are existed in the suppression contacts list " +
				"or contacts status changed by user/recipient himself unsubscribed";

				Messages messages = new Messages("Email Details ", "No Active contacts",
						msgStr, cal,  "Inbox", false, "Info", eventTrigger.getUsers());
				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
				return;
			}


			/** cfNameListStr has the list of custom field names with ',' delimiter */
			String cfNameListStr = "";
			if(campaignToBeSent == null)
				if(logger.isInfoEnabled()) logger.info("campaignToBeSent is null");
			else
				if(logger.isInfoEnabled()) logger.info("campaignToBeSent is not null");

			if(campaignToBeSent != null && campaignToBeSent.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL) &&
					campaignToBeSent.getLabel().trim().startsWith("SENT_")==false){

				//TODO need to replace the custom field for External Mail
			}
			else{



				if(isCdateFlagSet || isSdateFlagSet) {

					cfNameListStr = getCustomFields(htmlContent);
				}
			}


			if((isCdateFlagSet || isSdateFlagSet) && cfNameListStr.length() > 0){

				mailingListDao = (MailingListDao)context.getBean("mailingListDao");

				for(Iterator<MailingList> iterator = mlSet.iterator();iterator.hasNext();){
					mailingList = iterator.next();
					if(!mailingList.isCustField()) continue;

					/**
					 * mlCfList - List of all the tokens which have 'custom_filed_name:field_index'
					 *  for the particular mailing list
					 *  below part can be separated as a new function say: getCustFieldTokens() Event Trigger
					 * */
					List<String> mlCfList = mailingListDao.getCFList(cfNameListStr, mailingList.getListId());

					String tempcfStr = "";
					String defValue = "";

					for (String cf : mlCfList) {

						if(cf.indexOf(Constants.DELIMETER_DOUBLECOLON) > 0) {

							try {
								if(logger.isInfoEnabled()) logger.info("CF TOken :" + cf);
								String[] tokens = cf.split(Constants.DELIMETER_DOUBLECOLON);

								if(tokens.length > 2)  defValue = tokens[2];
								else defValue = "";

								if(tempcfStr.length() != 0)
									tempcfStr = tempcfStr + ",'" + Constants.CF_COL_DELIMETER + "',";

								tempcfStr = tempcfStr + "'" + tokens[0] + Constants.CF_NAME_VALUE_SEPARATOR
								+ "', ifnull(cust_" + tokens[1] + ",'"+ defValue + "')";

							}
							catch (Exception e) {
								logger.error("Exception while getting the custom field name for the list : "
										+ mailingList.getListName(), e);
							}

						} //if(cf.indexOf(":")>0)
					} // for (String cf : mlCfList)

					if(logger.isInfoEnabled()) logger.info(">>>>>>>>>>> tempcfStr =" + tempcfStr);
					if(mlCfList.size()>0) {
						contactsDaoForDML.updateTempContactsWithCF(tempcfStr , mailingList.getListId());
					}
					if(logger.isInfoEnabled()) logger.info("Updated the tempContacts for list Id:" + mailingList.getListId());

				} // for

			} // if(cfNameListStr.length() > 0)


			/***************** Create Campaign Report Object *****************/
			CampaignReport campaignReport=null;
			if(logger.isInfoEnabled()) logger.info("fromSource is: "+fromSource);

			//inserting triggername instead of campaign name and adding sourcetype in campiagn_reports table

			campaignReport = new CampaignReport( usersObj, eventTrigger.getTriggerName(),
					campaignToBeSent.getSubject(), htmlContent,cal, 0, 0, 0, 0, 0,
					Constants.CR_STATUS_SENDING, fromSource);
			
			
			String placeHoldersStr = "";
			
			if(totalPhSet.size() > 0) {
				
				for(String ph:totalPhSet){
					
					if(ph.startsWith("GEN_")) {
						
						ph = ph.substring(4);
	 					if(placeHoldersStr.trim().length() > 0) placeHoldersStr += "||";
	 					placeHoldersStr += ph;
						
					}//if
				}//for
				
				campaignReport.setPlaceHoldersStr(placeHoldersStr);
			}
				
				
			

			if(urlSet.size()>0) {
				campaignReport.setUrls(urlSet);
				if(logger.isDebugEnabled()) logger.info(" Url in the campaign :"+urlSet);
			}

			try {
				//campaignReportDao.saveOrUpdate(campaignReport);
				campaignReportDaoForDML.saveOrUpdate(campaignReport);
				CampReportLists campReportLists = new CampReportLists(campaignReport.getCrId());
				campReportLists.setListsName(listNameStr);
				campReportLists.setSegmentQuery(null);
				//campReportListsDao.saveOrUpdate(campReportLists);
				campReportListsDaoForDML.saveOrUpdate(campReportLists);

			}
			catch (Exception e) {
				logger.error("** Exception : while saving the campaign report", e);
				return;
			}

			if(logger.isDebugEnabled()) {
				logger.info("campaign report object created and saved with the status as sending");
			}


			if(logger.isInfoEnabled()) logger.info(" submitting the campaign to PMTA");
			
			boolean sendGridTrigflag = false;
			byte statusCode  = -1;
			Vmta vmta = campaignToBeSent.getUsers().getVmta();
			String vmtaName = campaignToBeSent.getUsers().getVmta().getVmtaName();
			/*if(campaignToBeSent.getUsers().getVmta().equalsIgnoreCase("SendGridAPI")) {*/
			
			if(vmta != null){
				
				if(Constants.SMTP_SENDGRIDAPI.equalsIgnoreCase(vmtaName) || vmta.getVmtaName().equalsIgnoreCase("AmazonSES")) {
					// Send From SendGrid API .
					
					if(eventTrigger == null) {
						if(logger.isDebugEnabled()) logger.debug("Scheduling of EventTrigger is null");
						return;
					}
					ExternalSMTPSender sendGridSender = new ExternalSMTPSender(vmta, context);			
					sendGridTrigflag = sendGridSender.submitCampaign(campaignToBeSent, htmlContent, textContent, campaignReport, totalSizeInt, eventTrigger, fromSource,vmta);
					
					if(sendGridTrigflag) {
						if(logger.isInfoEnabled()) logger.info("campaign submission is completed ");
						msgStr = "Email Name :"+campaignToBeSent.getCampaignName()+"\n"+
						"Sent successfully to "+campaignReport.getSent()+" unique contacts";
					} else {
						if(logger.isInfoEnabled()) logger.info("campaign submission is failed ");
						msgStr = "Email Name  :"+campaignToBeSent.getCampaignName()+"\n"+
								 //TODO:"Status code :"+statusCodeByte+"\n"+
								 "Submission is failed, please report the problem to admin";

						//campReportListsDao.deleteByCampRepId(campaignReport.getCrId());
						campReportListsDaoForDML.deleteByCampRepId(campaignReport.getCrId());

						campaignReportDaoForDML.delete(campaignReport);
					}
					
				}
				if(sendGridTrigflag == false){

					if(logger.isInfoEnabled()) logger.info("campaign submission is failed ");
					msgStr = "Email Name  :"+campaignToBeSent.getCampaignName()+"\n"+
							 //TODO:"Status code :"+statusCodeByte+"\n"+
							 "Submission is failed, please report the problem to admin";

					//campReportListsDao.deleteByCampRepId(campaignReport.getCrId());
					campReportListsDaoForDML.deleteByCampRepId(campaignReport.getCrId());
					campaignReportDaoForDML.delete(campaignReport);
				
				}
			}
			 /*else {
			
				statusCode = senderThread.submitCampaign(campaignToBeSent, htmlContent, textContent,
						campaignReport, totalSizeInt,eventTrigger, fromSource);
			}*/

			String strName=null;

			strName = eventTrigger.getTriggerName();
			if(logger.isInfoEnabled()) logger.info("submitTrigger statusCode ="+statusCode);
		
			if(statusCode == 0 || sendGridTrigflag) {

				if(campaignToBeSent.getEditorType().equalsIgnoreCase(Constants.CAMP_EDTYPE_EXTERNAL_EMAIL) &&
						campaignToBeSent.getLabel().trim().startsWith("SENT_")==false) {

					campaignToBeSent.setLabel("SENT_"+campaignToBeSent.getLabel().trim());
					urlSet = getUrls(campaignToBeSent.getFinalHtmlText());
					//campaignsDao.saveOrUpdate(campaignToBeSent); // update the HtmlContent.
					campaignsDaoForDML.saveOrUpdate(campaignToBeSent);
					campaignReport.setContent(campaignToBeSent.getHtmlText());
					campaignReport.setUrls(urlSet);
				} // if "ExternalEmail"

				if( ((optionsFlag & Constants.ET_ADD_CONTACTS_TO_ML_FLAG) == Constants.ET_ADD_CONTACTS_TO_ML_FLAG)
						|| ((optionsFlag & Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG) == Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG) )  {

					updatedCount = copyOrRemoveContacts(eventTrigger);

					if(updatedCount < 0) {
						
						if(logger.isInfoEnabled()) logger.info("Unable to perform copy/remove operations due to some Exception...updatedCount = "+updatedCount);
						//TODO check what has to be written here
					}

					msgStr =  "Event Trigger Name : " + eventTrigger.getTriggerName() + "\r\n";
					msgStr = msgStr +" \r\n Copy/Remove was \r"+(updatedCount > 0 ? "Successful":"unsuccessFul")+" \n";
					Messages msgs = new Messages("Event Trigger", "Contacts copied/moved ",
							msgStr, cal, "Inbox", false, "Info", eventTrigger.getUsers());

					//messagesDao.saveOrUpdate(msgs);
					messagesDaoForDML.saveOrUpdate(msgs);
				}

				if(logger.isInfoEnabled()) logger.info("campaign submission is completed successfully");

				msgStr = "Trigger Email Name :"+strName+"\n"+
				"Sent successfully to "+campaignReport.getSent()+" unique contacts";

			}
			else {

				if(logger.isInfoEnabled()) logger.info("campaign submission is failed ");

				msgStr = "Email Name  :"+strName+"\n"+
				"Status code :"+statusCode+"\n"+
				"Submission is failed, please report the problem to admin";
				//campReportListsDao.deleteByCampRepId(campaignReport.getCrId());
				campReportListsDaoForDML.deleteByCampRepId(campaignReport.getCrId());

				campaignReportDaoForDML.delete(campaignReport);

			}


			if(msgStr != null && (statusCode == 0 || sendGridTrigflag)) {

				Messages messages = new Messages("Email Details ",(statusCode == 0 || sendGridTrigflag) ? "Email sent successfully"
						:"Email sending failed.",msgStr, cal,  "Inbox", false, "Info",usersObj);

				// update Used Email Count in Users table
				if(!useMQS) {

					//usersDao.updateUsedEmailCount(campaign.getUsers().getUserId(), totalSizeInt);
					//usersDao.updateUsedEmailCount(userId, totalSizeInt);
					usersDaoForDML.updateUsedEmailCount(userId, totalSizeInt);
				}
				else {

					//TODO need to write code to call MQS service Process order to update
					// the usage
				}
				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
				campaignReport.setStatus(Constants.CR_STATUS_SENT);

				eventTrigger.setLastSentDate(cal);
				//eventTriggerDao.saveOrUpdate(eventTrigger);
				eventTriggerDaoForDML.saveOrUpdate(eventTrigger);

				//campaignReportDao.saveOrUpdate(campaignReport);
				campaignReportDaoForDML.saveOrUpdate(campaignReport);

			}	//if msgStr != null
		}
		catch (Exception e)	{
			logger.error("** Exception", e);
		}
		if(logger.isInfoEnabled()) logger.info("just before calling garbage collector");
		System.gc();

		if(logger.isInfoEnabled()) logger.info(">>>>>>>>> Trigger processing is completed, exiting processTrigger");

	} // processTrigger

	/**
	 *
	 * @param eventTrigger
	 * @return
	 *
	 * Here we are getting corresponding mailing lists associated with all the four trigger types
	 * For CDATE/SDATE we get mlists directly from event_trigger table  
	 * but for OPENS and CLICKS these mailing list ids are used only during copying/removing
	 * not for campaign/trigger submission. for OPENS/CLICKS trigger submission we get the
	 * records directly  from campaign_sent...and while copying/removing if those contacts
	 * are present in MLs associated with campaign OPENED/CLICKED then we copy/move them
	 * to the destination ML(s)
	 */
	public Set<MailingList> getMlSet(EventTrigger eventTrigger) {

		Set<MailingList> mlSet = null;
		try{

			if (isCdateFlagSet || isSdateFlagSet) {

				if(logger.isInfoEnabled()) logger.info("inside CDATE/SDATE");
				mlSet = eventTrigger.getMailingLists();
				if(logger.isInfoEnabled()) logger.info("in if CDATE SDATE mlSet "+mlSet);

			}
			else if (eventTrigger.getTriggerType().equalsIgnoreCase(Constants.ET_TYPE_CAMPAIGN_CLICK)){ // EventTrigger2 verison

				if(logger.isInfoEnabled()) logger.info("inside CLICKS");
				String clicksCampaign = "";
				int pipeDelimiterIndex = eventTrigger.getEventField().indexOf(Constants.DELIMITER_PIPE);

				if(pipeDelimiterIndex < 0){
					if(logger.isInfoEnabled()) logger.info("no pipe found");
					return null;
				}
				clicksCampaign = eventTrigger.getEventField().substring(0, pipeDelimiterIndex);

				if(clicksCampaign == null || clicksCampaign.isEmpty()) { 

					if(logger.isInfoEnabled()) logger.info("did not get the campaign name from eventField ");
					return null;
				}

				if(logger.isInfoEnabled()) logger.info("before calling findByCampaignName"+clicksCampaign);

				Campaigns campaignsObj =
					campaignsDao.findByCampaignName(clicksCampaign,eventTrigger.getUsers().getUserId());

				if(campaignsObj != null) {
					mlSet = campaignsObj.getMailingLists();
				}

			}//CLICKS
			else if (eventTrigger.getTriggerType().equalsIgnoreCase(Constants.ET_TYPE_CAMPAIGN_OPEN)) { // EventTrigger2 verison

				String opensCampaign = eventTrigger.getEventField();

				if(logger.isInfoEnabled()) logger.info("before calling findByCampaignName"+opensCampaign);

				Campaigns campaignsObj 
				= campaignsDao.findByCampaignName(opensCampaign,eventTrigger.getUsers().getUserId());

				if(campaignsObj != null) {

					mlSet = campaignsObj.getMailingLists();
				}

			}//OPENS
			else {  // EventTrigger2 verison

				if(logger.isInfoEnabled()) logger.info("triggerType did not match...returning empty mailing list Set");
				return mlSet;

			}

			if(mlSet != null) {  // EventTrigger2 verison

				if(logger.isInfoEnabled()) logger.info("returning from getMlSet with mlSet "+mlSet);
			}
			return mlSet;
		}
	catch(Exception e) {

		logger.error(" **Exception ",e);
		return null;
	}
		
	} // getMlSet

	/**
	 *
	 * @param eventTrigger
	 * @param mlSet
	 * @return
	 *
	 * This method is added for EventTrigger
	 * to get comma separated mailing list ids from a given mailinglist set
	 */
	public String getListIdsStr(Set<MailingList> mlSet){
		//public String getListIdsStr(EventTrigger eventTrigger, Set<MailingList> mlSet){
		
		String listIdsStr = "";
		MailingList mailingList = null;


		if (mlSet != null && mlSet.size() > 0) {

			//logger.info("elements of mlset and size are "+mlSet.toString()+", "+mlSet.size());

			//mlSet = eventTrigger.getMailingLists();
			for (Iterator<MailingList> iterator = mlSet.iterator(); iterator.hasNext();){

				mailingList = iterator.next();
				if(listIdsStr.length() == 0) {

					listIdsStr += mailingList.getListId();
				}
				else {

					listIdsStr += ","+mailingList.getListId();
				}
			}
		}

		//logger.info("returning from getListIdsStr with listIdsStr "+listIdsStr);

		return listIdsStr;

	} // getListIdsStr

	/**
	 *Added for EventTrigger
	 *
	 * @param crIdsList
	 * @param urlListStr
	 * @param offset
	 * @return
	 *
	 * This method is used to for a query for Url clicks criteria
	 * 
	 * user input for offset will be in either hours or days
	 * but in DB it is stored in minutes. 1 day = 1440 mins
	 * so when offset is > 1440 we handle it using DATEDIFF
	 * to get accurate results...
	 * we can also use DATE_ADD /DATE_SUB with 'DAYS' INTERVAL
	 *
	 */
	public String getContactIdsByCrIdsAndClicks(String crIdsList,String urlListStr,int offset){ // added for EventTrigger

		String queryStr = " SELECT DISTINCT c.*,NULL, SUBSTRING_INDEX(c.email_id, '@', -1) "+
		" FROM campaign_sent cs, clicks ck1, contacts c "+
		" WHERE cs.contact_id = c.cid " +
		" AND cs.cr_id IN ("+crIdsList+") "+
		" AND cs.sent_id = ck1.sent_id "+
		" AND c.email_status LIKE '" +Constants.CONT_STATUS_ACTIVE+"' "+
		" AND cs.clicks > 0 "+
		" AND ck1.click_url IN ("+urlListStr+") ";

		//TODO above query is little slow check for optimization in next release EventTrigger

		if(offset >= 1440){

			offset = offset/1440;
			queryStr += " AND DATEDIFF(now(),ck1.click_date) = "+offset+" ";
		}
		else { //0-23 hrs

			//make sure the value stored from subscriber to cust field is of format %d/%m/%Y ex: 10/07/1985

			if(offset > 0){

				offset = offset/60; //converting minutes to hours
				queryStr += " AND now() > ck1.click_date";

			}
			queryStr += " AND HOUR( TIMEDIFF(now(),ck1.click_date) ) = "+offset;

		}

		return queryStr;

	} // getContactIdsByCrIdsAndClicks

	/**
	 *
	 * @param eventTrigger
	 * @param totalSizeInt
	 * @return
	 *
	 * Added for EventTrigger
	 * this method removes all the contacts to whom we have already sent similar type of trigger
	 * from tempContacts table 
	 */
	private int filterTempContacts(EventTrigger eventTrigger, int totalSizeInt) {

		List<Long> crList = null;
		String crListStr = "";
		long userId = eventTrigger.getUsers().getUserId();
		int optionsFlag = eventTrigger.getOptionsFlag();

		try{

			if( (optionsFlag & Constants.ET_FILTER_BY_TRIGGER_TYPE_FLAG)
					== Constants.ET_FILTER_BY_TRIGGER_TYPE_FLAG) {

				crList = campaignReportDao.getCrIdsBySourceType(fromSource,userId);

			}
			else {
				if(logger.isInfoEnabled()) logger.info("----trig name = "+eventTrigger.getTriggerName());

				crList = campaignReportDao.getCrIdsByTriggerTypeOrName(eventTrigger.getTriggerName(),
						fromSource,userId,eventTrigger.getId());
			}


			if(crList == null) {

				if(logger.isInfoEnabled()) logger.info("Got a null crIdsList due to either empty TriggerName or Exception in query");
				return -1;
			}

			if(crList.isEmpty()) {
				if(logger.isInfoEnabled()) logger.info("No campaign reports exists for this Trigger/Campaign..so no records to be deleted");
				if(logger.isInfoEnabled()) logger.info(" returning from delefromtempcontacts with totalsizeInt "+totalSizeInt);
				return totalSizeInt;
			}

			if(logger.isInfoEnabled()) logger.info("just returned getCrIdsBySouceType with crList size "+crList.size());

			for(Iterator<Long> iterator = crList.iterator();iterator.hasNext();){

				if(crListStr.length() == 0) {
					crListStr += iterator.next().toString();
				}
				else {
					crListStr += ","+iterator.next().toString();
				}
			} // for

			if(logger.isInfoEnabled()) logger.info("after for loop crList appended to crListStr "+crListStr);
			if(crListStr.length()>0){

				String delQueryStr =  " DELETE FROM tempcontacts " +
				" WHERE email_id IN (SELECT email_id FROM campaign_sent " +
				" WHERE cr_id IN ("+crListStr+") )";

				if(logger.isInfoEnabled()) logger.info("just before deleting from temp contacts");
				totalSizeInt = totalSizeInt - contactsDaoForDML.executeJdbcUpdateQuery(delQueryStr);
				return totalSizeInt;

			} // crListLength > 0
			else{
				if(logger.isInfoEnabled()) logger.info(" no records to delete from tempcontacts as crListStr.length()= "+crListStr.length());
				return totalSizeInt;
			}

		}
		catch(Exception e) {
			logger.info("Exception while deleting from TempContacts");
			logger.error("Exception ::::" , e);
			return -1;
		}

	} // filterTempContacts

	private int copyOrRemoveEtContacts(EventTrigger eventTrigger) { // added for New eventTrigger
		int optionsFlag = eventTrigger.getOptionsFlag(); 
		/*
		 * 1st performing copying contacts to specified MLs
		 */

		if( (optionsFlag & Constants.ET_ADD_CONTACTS_TO_ML_FLAG) == Constants.ET_ADD_CONTACTS_TO_ML_FLAG ){
			
			int currentSize = 0;
			boolean fetchFlag = true;
			
			if(logger.isInfoEnabled()) logger.info("---- ET_ADD_CONTACTS_TO_ML_FLAG set----");
			//Set<MailingList> copyToMailingListSet;
			MailingList copyToMailingList = eventTrigger.getAddTriggerContactsToMl();

			if(copyToMailingList == null) {

				if(logger.isInfoEnabled()) logger.info("No destination mailings lits are given for the trigger");
				return -1; 
			}
			long mlbits = copyToMailingList.getMlBit().longValue();
			List<Contacts> contactsUpdateList = new ArrayList<Contacts>();
			
			while(fetchFlag) {

				List<Contacts> tempContactsList = null;
				String finalQry = " SELECT cid FROM tempcontacts LIMIT "+currentSize+","+500; 
				
				List<Map<String, Object>> retList = contactsDao.getConatctIds(finalQry);
				if(retList == null || retList.isEmpty()) {
					
					if(logger.isInfoEnabled()) logger.info(" got no contacts from temp contacts ");
					fetchFlag = false;
					//currentSize = 0; 
					break;
				}//if
				String cidStr = Constants.STRING_NILL;
				
				for (Map<String, Object> cidMap : retList) {
					
					Object Cid = cidMap.get("cid");
					
					if(!cidStr.isEmpty()) cidStr += Constants.DELIMETER_COMMA;
					
					cidStr += Cid.toString();
					
				}//for
				//if(logger.isInfoEnabled()) logger.info(" got cids from temp contacts "+cidStr);
				tempContactsList = contactsDao.getContactsByCids(cidStr);	

				if(tempContactsList == null || tempContactsList.isEmpty()) {

					if(logger.isInfoEnabled()) logger.info(" got "+tempContactsList.size()+" records from temp contacts ");
					fetchFlag = false;
					//currentSize = 0; 
					break;
				}//if
				for (Contacts contact : tempContactsList) {
					
					contact.setMlBits(contact.getMlBits() | mlbits);
					contactsUpdateList.add(contact);
					
					
					
				}//for
				
				if(contactsUpdateList.size() > 0) {
					
					contactsDaoForDML.saveByCollection(contactsUpdateList);
					contactsUpdateList.clear();
					
				}//if
				currentSize += 500;
				
			}//while
			
		}//if

		/*
		 * Then check if remove flag is set...this should be separate if { }.
		 */

		if((optionsFlag & Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG)
				== Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG ){

			int currentSize = 0;
			boolean fetchFlag = true;
			List<Contacts> contactsUpdateList = new ArrayList<Contacts>();
			
			Set<MailingList> removeFromMailingListSet;
			removeFromMailingListSet = eventTrigger.getMailingLists();

			if(removeFromMailingListSet == null || removeFromMailingListSet.size() == 0) {

				if(logger.isInfoEnabled()) logger.info("No destination mailings lits are given for the trigger");
				return -1; 
			}
			long mlbits = Utility.getMlsBit(removeFromMailingListSet);
			
			
			
			while(fetchFlag) {

				List<Contacts> tempContactsList = null;
				String finalQry = " SELECT cid FROM tempcontacts LIMIT "+currentSize+","+500; 
				
				List<Map<String, Object>> retList = contactsDao.getConatctIds(finalQry);
				if(retList == null || retList.isEmpty()) {
					
					if(logger.isInfoEnabled()) logger.info(" got no contacts from temp contacts ");
					fetchFlag = false;
					//currentSize = 0; 
					break;
				}//if
				String cidStr = Constants.STRING_NILL;
				
				for (Map<String, Object> cidMap : retList) {
					
					Object Cid = cidMap.get("cid");
					
					if(!cidStr.isEmpty()) cidStr += Constants.DELIMETER_COMMA;
					
					cidStr += Cid.toString();
					
				}//for
				//if(logger.isInfoEnabled()) logger.info(" got cids from temp contacts "+cidStr);
				tempContactsList = contactsDao.getContactsByCids(cidStr);	

				if(tempContactsList == null || tempContactsList.isEmpty()) {

					if(logger.isInfoEnabled()) logger.info(" got "+tempContactsList.size()+" records from temp contacts ");
					fetchFlag = false;
					//currentSize = 0; 
					break;
				}//if
				for (Contacts contact : tempContactsList) {
					
            			
        			contact.setMlBits(contact.getMlBits().longValue()& (~mlbits));
        			//if(contact.getMlSet().size() == 0){
        			if(contact.getMlBits().longValue() == 0l){
        				Utility.setContactFieldsOnDeletion(contact);
        				//contactsDao.saveOrUpdate(contact);
        			}
					
        			contactsUpdateList.add(contact);
					
				}//for
				
				if(contactsUpdateList.size() > 0) {
					
					contactsDaoForDML.saveByCollection(contactsUpdateList);
					contactsUpdateList.clear();
					
				}//if
				currentSize += 500;
				
			}//while
			
			
			
		

		} // Remove
		return 1;
	}//copyOrRemoveEtContacts
	
	
	/**@deprecated
	 * @Param: EventTrigger Object
	 * For all the contacts which satisfy a given trigger conditions this method
	 * 1. copies all the contacts in the current trigger to other user specified ML(s) if ADD flag is set
	 * 2. Remove all the contacts from current mailing List.. if Remove flag is set
	 */
	
	private int copyOrRemoveContacts(EventTrigger eventTrigger) { // added for eventTrigger

		if(logger.isInfoEnabled()) logger.info("----just entered copyOrRemoveContacts----");
		try {
			mlCustomFieldsDao = (MLCustomFieldsDao)context.getBean("mlCustomFieldsDao");
			
			int optionsFlag = eventTrigger.getOptionsFlag() ,currentSize = 0;;
			boolean fetchFlag = true;

			MailingList copyToMailingList;
			Contacts tempContactObj=null;
			Contacts destContactObj=null; // this is the object that will be either overwritten/merged/ignored based on the user's choice
			
			Set<MailingList> copyToMailingListSet;
			
			Set<MailingList> copyFromMailingListSet = null;
			if(!isOpensFlagSet || !isClicksFlagSet) {
				
				copyFromMailingListSet = eventTrigger.getMailingLists();
			}
			
			List<Contacts> contactsList = new ArrayList<Contacts>();
			List<CustomFieldData> cfdAfterSaveList = new ArrayList<CustomFieldData>();
			List<CustomFieldData> customFieldsDataList = new ArrayList<CustomFieldData>();
			List<Contacts> contactsListObj = null;

			HashMap<Long,CustomFieldData> cfdsOfSavedObjsMap = new HashMap<Long,CustomFieldData>();
			
			/*
			 *  <"srcListId-destListId", HashMap<srcFieldIndex, destFieldIndex>>   
			 */
			HashMap<String,HashMap<Integer,Integer>> listIdsFieldIndicesMap = new HashMap<String,HashMap<Integer,Integer>>();
			Map<Long,HashMap<String,Contacts>> listIdAndemailIdContactObjMap = new HashMap<Long, HashMap<String,Contacts>>();
			HashMap<String,Contacts> emailIdAndContactObjMap =null;
			HashMap<String,Contacts> emailIdListIdMap = new HashMap<String,Contacts>();
			HashMap<String,CustomFieldData> emailIdCfdObjMap = new HashMap<String,CustomFieldData>(); 
			/*
			 * if contactIdAndEmailMap has to be discarded the change getCustomFieldsDataByCids to get obj arr of emailId and cfd obj
			 * and populate emailId in emailIdCfdObjMap () customFieldData obj has dummy temp contact obj we have to do all this  
			 */
			HashMap<Long,String> contactIdAndEmailMap = new HashMap<Long,String>(); 


			/*
			 * 1st performing copying contacts to specified MLs
			 */

			if( (optionsFlag & Constants.ET_ADD_CONTACTS_TO_ML_FLAG) == Constants.ET_ADD_CONTACTS_TO_ML_FLAG ){

				if(logger.isInfoEnabled()) logger.info("---- ET_ADD_CONTACTS_TO_ML_FLAG set----");
				copyToMailingListSet = eventTrigger.getAddTriggerContactsToMls();

				if(copyToMailingListSet == null || copyToMailingListSet.size() == 0) {

					if(logger.isInfoEnabled()) logger.info("No destination mailings lits are given for the trigger");
					return -1; 
				}

				String copyToMlListStr = getListIdsStr(copyToMailingListSet);  // get comma separated list_ids
				
				//logger.info(" Before getting copyFromMlListStr ");
				String copyFromMlListStr = (isOpensFlagSet || isClicksFlagSet?contactsDao.getMlsForOpensClicksFromTempContacts():getListIdsStr(copyFromMailingListSet)); // get comma separated list_ids

				if(copyFromMlListStr == null){
				
					if(logger.isInfoEnabled()) logger.info("got a null string from getMlsForOpensClicksFromTempContacts ");
					return -1;
				}
				if(copyFromMlListStr.length() == 0){
					
					if(logger.isInfoEnabled()) logger.info("No src Mailing are obtained -exiting");
					return -1;
				}
				
				/*
				 * listIdAndemailIdContactObjMap will have all destination mailing Lists mapped to 
				 * another hashmap that contains all the temp contacts that are already present in 
				 * destination Ml. We need this map to get contact_ids for overwrite or merge criteria
				 */

				while(fetchFlag) {

					List<Contacts> tempContactsList = null;
					
					tempContactsList = contactsDao.getSegmentedTempContacts("SELECT * FROM tempcontacts", 
							currentSize, 500);	

					if(tempContactsList == null || tempContactsList.isEmpty()) {

						if(logger.isInfoEnabled()) logger.info(" got "+tempContactsList.size()+" records from temp contacts ");
						fetchFlag = false;
						//currentSize = 0; 
						break;
					}
					//logger.info(" tempContactsList.size = "+tempContactsList.size());

					/*
					 * getting all the email addresses of contacts
					 * This will be used for getting existing contacts objs in destn MLs
					 */
					StringBuffer emailIdsList = getContactObjs(tempContactsList);
					List<CustomFieldData> cfdList;

					if(emailIdsList.length() <= 0) {

						if(logger.isInfoEnabled()) logger.info(" Got Empty contact objs List ");
						return -1;
					}
					
					cfdList = customFieldDataDao.getCustomFieldsDataByCids(emailIdsList);
					if(cfdList == null) {
						if(logger.isInfoEnabled()) logger.info(" Problem while fetching customFields ");
						return -1;
					}

					for(Contacts iteratorTempContacts : tempContactsList ){

						tempContactObj = iteratorTempContacts;
						if(tempContactObj == null){
							
							if(logger.isInfoEnabled()) logger.info(" got a null temp contacts obj..continuing with next records ");
							continue;
						}
						/*else 
							logger.info(" not null " +tempContactObj.getMailingList().getListId()+", "+ tempContactObj.getEmailId());
*/
						emailIdListIdMap.put(tempContactObj.getEmailId(),tempContactObj);
						contactIdAndEmailMap.put(tempContactObj.getContactId(),tempContactObj.getEmailId());

						if(emailIdsList.length() == 0 ) {

							emailIdsList.append("'"+tempContactObj.getEmailId()+"'");
						}
						else {

							emailIdsList.append(",");
							emailIdsList.append("'"+tempContactObj.getEmailId()+"'");
						}
					}

				//	logger.info(" cfdList size=" +cfdList.size());
					for (CustomFieldData customFieldData : cfdList) {
						
						if(customFieldData == null){
							continue;
						}

						String str = (String)contactIdAndEmailMap.get(customFieldData.getEmailId().getContactId());
						emailIdCfdObjMap.put(str,customFieldData);
					}

					contactsListObj = contactsDao.getContactsByEmailIdsListAndMlist(emailIdsList,copyToMlListStr); 

					if(contactsListObj == null) {

						if(logger.isInfoEnabled()) logger.info("Exception occured while fetching from contacts table");
						return -1;
					}

					//logger.info(" contactsListObj size = "+contactsListObj.size());

					/*
					 * For each contact in tempContacts list iterate thru each mailing List
					 * and do the required operation (copy/merge/ignore)
					 */

					for (Iterator<MailingList> iterator = copyToMailingListSet.iterator(); iterator.hasNext();){

						copyToMailingList = iterator.next();
						Long mlistId = copyToMailingList.getListId();
						//logger.info(" iterator copyToMailingList = "+mlistId+" fROM mlists "+copyFromMlListStr);

						List<Object[]> cfList = mlCustomFieldsDao.findCommonCustFieldsForMls(copyFromMlListStr,copyToMailingList);

						String listIds = "";
						MLCustomFields mlCustFldsObj = null;
						HashMap<Integer,Integer> indicesMap = new HashMap<Integer,Integer>();

						if(cfList == null){

							if(logger.isInfoEnabled()) logger.info(" Got a null cfList");
							return -1;
						}
						
						if(!cfList.isEmpty()){
							
							if(logger.isInfoEnabled()) logger.info(" CF "+cfList.get(0)[0].toString());
						}
						else {
							if(logger.isInfoEnabled()) logger.info(" Got a empty cfList");
						}
						
						
						for (Object[] mlcustomflds : cfList) {

							mlCustFldsObj = (MLCustomFields)mlcustomflds[0];

							listIds = "";
							listIds += mlCustFldsObj.getMailingList().getListId() + "-" + ((MailingList)mlcustomflds[1]).getListId();

							if(listIdsFieldIndicesMap.containsKey(listIds)) {

								indicesMap = listIdsFieldIndicesMap.get(listIds);
								indicesMap.put(mlCustFldsObj.getFieldIndex(),((Integer)mlcustomflds[2]).intValue());
							}
							else {
								
								indicesMap.put(mlCustFldsObj.getFieldIndex(),((Integer)mlcustomflds[2]).intValue());
							}

							listIdsFieldIndicesMap.put(listIds, indicesMap);

						} // for
						
						


						for(Iterator<Contacts> iteratorExistingContacts = contactsListObj.iterator();
						iteratorExistingContacts.hasNext();){

							Contacts contactObj = (Contacts)iteratorExistingContacts.next(); 

							if(contactObj == null){
								if(logger.isInfoEnabled()) logger.info("contact obj is null ");
								continue;
							}
							
							
							List<MailingList> mlList = mailingListDao.findByContactBit(contactObj.getUsers().getUserId(), contactObj.getMlBits());
														
							//Set<MailingList> mlset = contactObj.getMlSet();
							Set<MailingList> mlset = new HashSet<MailingList>(mlList);
							Iterator<MailingList> mlItr = mlset.iterator();
							MailingList mailingList = null;
							boolean elseFlag = true;
							while(mlItr.hasNext()){
							
							if(mailingList.getListId().longValue() == 
								copyToMailingList.getListId().longValue()) {
								
								if(logger.isInfoEnabled()) logger.info("list ids "+copyToMailingList.getListId().longValue()+"matched adding contact obj with id "+contactObj.getContactId());

								if(listIdAndemailIdContactObjMap.containsKey(copyToMailingList.getListId())) {
									emailIdAndContactObjMap = listIdAndemailIdContactObjMap.get(copyToMailingList.getListId());
								}
								else {
									emailIdAndContactObjMap = new HashMap<String,Contacts>();
								}
							//	logger.debug(" inserting in emailIdAndContactObjMap values emailId "+contactObj.getEmailId()+" contact obj "+contactObj);
								emailIdAndContactObjMap.put(contactObj.getEmailId(),contactObj);
								elseFlag = false;
								break;
							}
							}
							if(elseFlag) {
								if(logger.isInfoEnabled()) logger.info(" list ids "+mailingList.getListId().longValue()+" and "+
										copyToMailingList.getListId().longValue()+" did not match proceeding with next rec ");
								continue;
							}

							listIdAndemailIdContactObjMap.put((Long)copyToMailingList.getListId().longValue(), emailIdAndContactObjMap);

						} // for iteratorExistingContacts

						//logger.info("Final listIdAndemailIdContactObjMap values:: "+listIdAndemailIdContactObjMap);

					} // ml
					//logger.info("Final listIdAndemailIdContactObjMap values:: "+listIdAndemailIdContactObjMap);
					
					// Iteratin through tempContacts
					for(Iterator<Contacts> iteratorTempContacts = tempContactsList.iterator();
					iteratorTempContacts.hasNext();){

						tempContactObj = iteratorTempContacts.next();

						//for each tempcontact iterate thru every Ml and chec if its already exists in it
						for(Iterator<MailingList> iterator = copyToMailingListSet.iterator();iterator.hasNext();){

							copyToMailingList = iterator.next();

							if(copyToMailingList.getListId()!= null) {

								if(logger.isInfoEnabled()) logger.info("copying to mlist "+copyToMailingList.getListId()+"  num of MLs got= "+copyToMailingListSet.size());
							}
							else{
								if(logger.isInfoEnabled()) logger.info("list id is null");
								continue;
							}

							/*
							 * check if the contact already present in dest ML.
							 * So we go to the ml in  listIdAndemailIdContactObjMap map 
							 * and search for current emailId in its value part(which inturn is a hashmap)
							 */
						//	logger.info("Final listIdAndemailIdContactObjMap values:: "+listIdAndemailIdContactObjMap.keySet());
							Object obj = null;
							if(!listIdAndemailIdContactObjMap.isEmpty()) {
								
								obj = (Object)getContactIdIfExists(listIdAndemailIdContactObjMap,tempContactObj,copyToMailingList);
							}

							if (obj == null) { // insert new contact
								
								destContactObj = null;
							}
							else if (obj instanceof Contacts){ // existing obj

								destContactObj = (Contacts)obj;
							}
							else{ // Exception
								
								if(logger.isInfoEnabled()) logger.info("Exception while gettin source contact object -Exiting ");
								return -1;
							}
							
							Contacts finalContactObj = null;//formContactObj(destContactObj,copyToMailingList,tempContactObj,optionsFlag);

							
							if (finalContactObj == null) {

								if(logger.isInfoEnabled()) logger.info(" continuing with next records ");
							}
							else {
								
								contactsList.add(finalContactObj);
							}

							if(contactsList.size()>50) { // insert into Contacts table

								contactsDaoForDML.saveByCollection(contactsList);

								if(logger.isInfoEnabled()) logger.info(" After saveByCollection saved contacts "+contactsList.size());
								
								StringBuffer contactObjs = getContactObjs(contactsList);
								
								if(contactObjs == null ){
									
									if(logger.isInfoEnabled()) logger.info("got a null contactIdsStrBuf due to some exception ");
									return -1;
								}
								
								
								if(!listIdsFieldIndicesMap.isEmpty()){

									if(logger.isInfoEnabled()) logger.info(" got a non empty common field indices of source and destination mlists ");

									//check if recently saved contacts already have any cfds
									cfdAfterSaveList = customFieldDataDao.getCustomFieldsDataByCids(contactObjs);

									for (CustomFieldData cfdObj : cfdAfterSaveList) {

										cfdsOfSavedObjsMap.put(cfdObj.getEmailId().getContactId(),cfdObj);
									}

									customFieldsDataList = getCustomFieldsDataList(contactsList,emailIdCfdObjMap,
											cfdsOfSavedObjsMap,listIdsFieldIndicesMap,emailIdListIdMap,optionsFlag);
								}

								if(customFieldsDataList.size() > 50) {

									if(logger.isInfoEnabled()) logger.info("1before saving customfieldaData objs "+customFieldsDataList);
									
									//customFieldDataDao.saveByCollection(customFieldsDataList);
									customFieldDataDaoForDML.saveByCollection(customFieldsDataList);

									
									if(logger.isInfoEnabled()) logger.info("saveByCollection1- num of cfds obj saved is "+customFieldsDataList.size());
									customFieldsDataList.clear();

								}
								contactsList.clear();

							}//contactsList.size()>50

							if(logger.isInfoEnabled()) logger.info(" proceeding with next destination mailingList");

						} // mlist for

					} // for loop on temp contacts

					if(logger.isInfoEnabled()) logger.info("taking next 500 contacts for processing from tempcontacts ");
					currentSize += 500;

				} // while
				if(customFieldsDataList.size() > 0) { 

					if(logger.isInfoEnabled()) logger.info("2before saving customfieldaData objs "+customFieldsDataList);
				
					//customFieldDataDao.saveByCollection(customFieldsDataList);
					customFieldDataDaoForDML.saveByCollection(customFieldsDataList);

					if(logger.isInfoEnabled()) logger.info("saveByCollection2- num of cfds obj saved is "+customFieldsDataList.size());
					
					customFieldsDataList.clear();

				}

				if(contactsList.size() > 0) { 

					if(logger.isInfoEnabled()) logger.info(" just before saveByCollection ");
				
					contactsDaoForDML.saveByCollection(contactsList); 
					if(logger.isInfoEnabled()) logger.info(" After saveByCollection saved contacts "+contactsList.size());
					
					StringBuffer contactIdsStrBuf = getContactObjs(contactsList);
					
					if(contactIdsStrBuf == null ){
						
						if(logger.isInfoEnabled()) logger.info("got a null contactIdsStrBuf due to some exception ");
						return -1;
					}
					
					if(!listIdsFieldIndicesMap.isEmpty()){

						if(logger.isInfoEnabled()) logger.info(" got a non empty common field indices of source and destination mlists ");

						cfdAfterSaveList = customFieldDataDao.getCustomFieldsDataByCids(contactIdsStrBuf);

						for (CustomFieldData cfdObj : cfdAfterSaveList) {
							cfdsOfSavedObjsMap.put(cfdObj.getEmailId().getContactId(),cfdObj);
						}


						customFieldsDataList = getCustomFieldsDataList(contactsList,emailIdCfdObjMap,
								cfdsOfSavedObjsMap,listIdsFieldIndicesMap,emailIdListIdMap,optionsFlag);
					}

					if(customFieldsDataList == null){
						if(logger.isInfoEnabled()) logger.info(" Unable to get the customFieldsDataList -exiting without saving custom fields");
						return -1;
					}
					if(customFieldsDataList.size() > 0) {

						if(logger.isInfoEnabled()) logger.info("3before saving customfieldaData objs "+customFieldsDataList);
						
						//customFieldDataDao.saveByCollection(customFieldsDataList);
						customFieldDataDaoForDML.saveByCollection(customFieldsDataList);

						if(logger.isInfoEnabled()) logger.info("saveByCollection3- num of cfds obj saved is "+customFieldsDataList.size());
						customFieldsDataList.clear();

					}
					contactsList.clear();
					if(logger.isInfoEnabled()) logger.info("After saveByCollection ");						

				} // contactsList.size()>0 , <50
				if(logger.isInfoEnabled()) logger.info("processing completed for trigger "+ eventTrigger.getTriggerName());

			} // if copy flag set
			

			/*
			 * Then check if remove flag is set...this should be separate if { }.
			 */

			if((optionsFlag & Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG)
					== Constants.ET_REMOVE_FROM_CURRENT_ML_FLAG ){

				/*
				 * Remove option is applicable only for CDATE and SDATE. 
				 * Remove flag should not be set for OPEN/CLICKS.
				 * Hence validating triggerType
				 */

				if(isOpensFlagSet || isClicksFlagSet){

					if(logger.isInfoEnabled()) logger.info("Remove option is not applicable for OPENS/CLICKS. Exiting");
					return 1;
				}

				if(logger.isInfoEnabled()) logger.info("before calling removeContactsFromMlUsingTempContacts ");
				int count = contactsDaoForDML.removeContactsFromMlUsingTempContacts();

				if( count > 0 ) {

					if(logger.isInfoEnabled()) logger.info("Successfully performed Move and/or Copy operations...on "+count+" contacts ");
				}

			} // Remove

			if(logger.isInfoEnabled()) logger.info(" returning from copyOrRemoveContacts ");
			return 1;

		}
		catch(Exception e) {

			if(logger.isInfoEnabled()) logger.info("Exception in copyOrRemoveContacts... ");
			logger.error("Exception ::::" , e);
			return -1;
		}
	} // copyOrRemoveContacts
	
	/**
	 * added for EventTrigger 
	 * 
	 * @param contactsList
	 * @return
	 * 
	 * This method returns a stringbuff with comma separated contacts
	 * for a given contactsList. This code is used at many places so separated into a function for reuse
	 */
	public StringBuffer getContactObjs(List<Contacts> contactsList){
		try{
		StringBuffer contactObjsStr = new StringBuffer();
		
		for (Contacts tempContactObj : contactsList) {
			if(contactObjsStr.length() == 0 ) {

				contactObjsStr.append(tempContactObj.getContactId());
			}
			else {

				contactObjsStr.append(",");
				contactObjsStr.append(tempContactObj.getContactId());
			
			}
		} // for
		
		return contactObjsStr;
	}
	catch(Exception e){
		if(logger.isErrorEnabled()) logger.error("**Exception ",e);
		return null;
	}
	
	} // getContactObjs
	
	/**
	 * added for EventTrigger
	 * 
	 * @param listIdAndemailIdContactMap
	 * @param tempContactObj
	 * @param copyToMailingList
	 * @return
	 * 
	 * This method checks if given object is already present in destination mailingList
	 * it returns 1) null if it is a new entry 
	 * 2) Contact object if contact already present in DB and 
	 * 3) exception object for exception
	 */

	public Object getContactIdIfExists(Map<Long,HashMap<String,Contacts>> listIdAndemailIdContactMap,
			Contacts tempContactObj,MailingList copyToMailingList) {
		try {

			//logger.info(" entered getContactIdIfExists ");
			HashMap<String,Contacts> emailIdAndContactMap = new HashMap<String,Contacts>();

			if(listIdAndemailIdContactMap.containsKey(copyToMailingList.getListId().longValue())) {

				emailIdAndContactMap = listIdAndemailIdContactMap.get(copyToMailingList.getListId());

			}
			else {
				if(logger.isInfoEnabled()) logger.info(" list id "+copyToMailingList.getListId()+"doesn't have any common contacts with src list id ");
				return null; 
			}

			//logger.info(" emailIdAndContactObjMap values "+emailIdAndContactMap);

			if( emailIdAndContactMap.containsKey(tempContactObj.getEmailId()) ){ 
				// contact Exists in DB.

				//logger.info("contact exists in ML-checking for overwrite/merge/ignore cases");
				boolean contactFoundFlag = false;
				Contacts contact = null;

				//logger.info(" emailIdAndContactObjMap.size "+emailIdAndContactMap.size());

				for(String emailId : emailIdAndContactMap.keySet()){

					contactFoundFlag = false;
					contact = null;
					contact = (Contacts)emailIdAndContactMap.get(emailId);
					if(contact == null) {

						//logger.info(" contact obj is null ");
						continue;
					}
					List<MailingList> mlList = mailingListDao.findByContactBit(contact.getUsers().getUserId(), contact.getMlBits());
					
					//Set<MailingList> mlset = contact.getMlSet();
					Set<MailingList> mlset = new HashSet<MailingList>(mlList);
					Iterator<MailingList> mlItr = mlset.iterator();
					while(mlItr.hasNext()){
					if (mlItr.next().getListId().longValue() == copyToMailingList.getListId().longValue() && contact.getEmailId().equals(tempContactObj.getEmailId())){
						if(logger.isInfoEnabled()) logger.info(" contact not null ");
						contactFoundFlag = true;
						break;
					}
					}
					if(contactFoundFlag){
						break;
					}
				} // for

				//logger.info(" came out of for loop contactFoundFlag = "+contactFoundFlag);

				if (contactFoundFlag) {

					if(logger.isInfoEnabled()) logger.info("emailId = "+tempContactObj.getEmailId()+" contactId "+contact.getContactId());
				}
				return contact;

			} // emailIdAndContactObjMap
			else {
				if(logger.isInfoEnabled()) logger.info("inserting New contact ");
				return null;
			}

		}
		catch(Exception e){

			if(logger.isInfoEnabled()) logger.info(" **Exception ",e);
			return e;
		}

	} // getContactIdIfExists

/**
 *  added for eventTrigger
 * 
 * @param contactsList
 * @param emailIdCfdObjMap
 * @param cfdsOfSavedObjsMap
 * @param listIdsFieldIndicesMap    <"srcListId-destListId", HashMap<srcFieldIndex, destFieldIndex>> 
 * if copying from ml 771 to 770 and cust_2 of 771 is same as cust_3 of 770 the
 * the entry will be <'771-770',<2,3> >
 * @param emailIdListIdMap
 * @return
 * 
 * this method prepares a customfieldData object for all the saved contacts
 * based on overwrite/merge/ignore criteria
 */
	
	public List<CustomFieldData> getCustomFieldsDataList(List<Contacts> contactsList,
			HashMap<String,CustomFieldData> emailIdCfdObjMap,
			HashMap<Long,CustomFieldData> cfdsOfSavedObjsMap,HashMap<String,HashMap<Integer,Integer>> listIdsFieldIndicesMap,
			HashMap<String,Contacts> emailIdListIdMap,int optionsFlag) {
		
		if(logger.isInfoEnabled()) logger.info("Just Entered getCustomFieldsDataList ");
		
		try{
			List<CustomFieldData> customFieldDataList = new ArrayList<CustomFieldData>();
			List<CustomFieldData> cfdsListToBeDeleted = new ArrayList<CustomFieldData>();

			if(emailIdCfdObjMap.isEmpty()){

				if(logger.isInfoEnabled()) logger.info(" No customFields found for contacts in source mailing lists-returning empty list ");
				if(logger.isInfoEnabled()) logger.info("customFieldDataList.size = "+customFieldDataList.size());
				return customFieldDataList;
			}
				
			if(logger.isInfoEnabled()) logger.info("  custom fields exists "+emailIdCfdObjMap);
				
			Method tempMethodDest = null;
			Object [] params;

			Contacts srcTempContact = null;
			CustomFieldData customFieldDataDest = null;
			CustomFieldData cfdToBeDeleted = null;
			HashMap<Integer,Integer> srcIndxDestIndx = new HashMap<Integer,Integer>(); // intermediate map

			Long srcListId = null;
			int destFieldIndex = -1;
			String emailIdListIdStr = "";

			for(Contacts savedContact : contactsList) {

				/*
				 * First Check if current savedContact is having any CFDs in  source map -emailIdCfdObjMap
				 */
				if(!emailIdCfdObjMap.containsKey(savedContact.getEmailId())){

					if(logger.isInfoEnabled()) logger.info(" Current contact doesn't have any custom fields "+savedContact.getEmailId()+" contact_id = "+savedContact.getContactId());
					continue;
				}

				/*
				 * Now check if saved contact already has any cfds obj.
				 * If yes consider MERGE flag and proceed accordingly.
				 * If MERGE is set we will set only those CF columns that are common and not null in source
				 * Else it is Overwrite criteria and we will delete exiting cfd obj 1st
				 * and form a new cfd obj which will have values corresponding to matching columns (other non matching column values will be overwritten by null)
				 */
				if(cfdsOfSavedObjsMap.containsKey(savedContact.getContactId())) { // contacts has cfds

					if((optionsFlag & Constants.ET_MERGE_IF_CONTACTS_EXISTS)
							== Constants.ET_MERGE_IF_CONTACTS_EXISTS ){
						
						if(logger.isInfoEnabled()) logger.info("MERGE CustomFields criteria ");
						
						customFieldDataDest = cfdsOfSavedObjsMap.get(savedContact.getContactId());
					} 
					else if((optionsFlag & Constants.ET_OVERWRITE_IF_CONTACTS_EXISTS)
							== Constants.ET_OVERWRITE_IF_CONTACTS_EXISTS ){ // overwrite
						
						/*
						 * first get the old cfd obj for current contact and add it to delete queue
						 * then form a new cfd obj with same contact id but null cid
						 */
						if(logger.isInfoEnabled()) logger.info("Overwrite CustomFields criteria-delete existing obj and insert with new data ");
						
						cfdToBeDeleted = (CustomFieldData)cfdsOfSavedObjsMap.get(savedContact.getContactId());
						cfdToBeDeleted.setEmailId(savedContact);
						cfdsListToBeDeleted.add(cfdToBeDeleted);
						
						customFieldDataDest = new CustomFieldData(); // now form a new object with cfd id null 
						
					} // overwrite 
					
				} // contact has cfds 
				else { // contact doesnt have cfd.form a new object with cfd id null
					
					customFieldDataDest = new CustomFieldData();
				}
				
				customFieldDataDest.setEmailId(savedContact); //bind newly saved contact object to cfd obj
				
				if(logger.isInfoEnabled()) logger.info(" curr email Id has cfd obj "+emailIdCfdObjMap.containsKey(savedContact.getEmailId()));
				if(logger.isInfoEnabled()) logger.info(" obj is "+emailIdCfdObjMap.get(savedContact.getEmailId()));
					
				/*
				 * getting source cfd obj to copy the values of common fileds
				 */
				
				CustomFieldData customFieldDataSrc =
					(CustomFieldData)emailIdCfdObjMap.get(savedContact.getEmailId());

				if(customFieldDataSrc == null) {
					
					if(logger.isInfoEnabled()) logger.info(" customFieldDataSrc is null for emailId "+savedContact.getEmailId());
					if(logger.isInfoEnabled()) logger.info(" continuing with next record ");
					continue;
					
				}

				srcTempContact = emailIdListIdMap.get(savedContact.getEmailId());
				
				List<MailingList> mlList = mailingListDao.findByContactBit(srcTempContact.getUsers().getUserId(), srcTempContact.getMlBits());
				
				//Set<MailingList> mlset = contactObj.getMlSet();
				Set<MailingList> mlset = new HashSet<MailingList>(mlList);
				
				
				//Set<MailingList> mlset = srcTempContact.getMlSet();
				Iterator<MailingList> mlItr = mlset.iterator();
				MailingList mailingList = null;
				while(mlItr.hasNext()){
				//TODO: all list ids are mapped, it has to be redesigned
				mailingList  = mlItr.next();
				//srcListId = srcTempContact.getMailingList().getListId();

				//emailIdListIdStr = srcListId + "-" +savedContact.getMailingList().getListId(); 
				srcListId = mailingList.getListId();

				emailIdListIdStr = srcListId + "-" +mailingList.getListId(); 

				srcIndxDestIndx = listIdsFieldIndicesMap.get(emailIdListIdStr);  
				}//while iterator
				for(Integer srcFieldIndex : srcIndxDestIndx.keySet()){ // for each common custom field

					destFieldIndex = srcIndxDestIndx.get(srcFieldIndex);

					String custValue = getCustomFieldValue(srcFieldIndex,customFieldDataSrc);
					
					if((optionsFlag & Constants.ET_MERGE_IF_CONTACTS_EXISTS)
							== Constants.ET_MERGE_IF_CONTACTS_EXISTS ){

						String destCustValue = getCustomFieldValue(destFieldIndex,customFieldDataDest);

						if(destCustValue != null && destCustValue.trim().length() > 0 ){
							
							if(logger.isInfoEnabled()) logger.info("continuing with next rec as dest cust field already has a value");
							continue; //continue with the next record.
						}
					}
					
					if(custValue == null || custValue.equals(null) || custValue.trim().length() <= 0){
						
						if(logger.isInfoEnabled()) logger.info("custValue is null or empty continuing with next records");
						continue;
					}
					if(logger.isInfoEnabled()) logger.info("custValue is not null");
					tempMethodDest = CustomFieldData.class.getMethod("setCust" + destFieldIndex, strArg);

					params = new Object[]{custValue};
					if(logger.isInfoEnabled()) logger.info(" custValue "+custValue+" params "+params);
					tempMethodDest.invoke(customFieldDataDest, params);
					if(logger.isInfoEnabled()) logger.info(" contactId invoked "+savedContact.getContactId());

				} // for

				customFieldDataList.add(customFieldDataDest);

			} // contactList loop

			//now before returning list to save, delete cfdsListToBeDeleted if is has any values
			if(cfdsListToBeDeleted.size() > 0) {
				
				if(logger.isInfoEnabled()) logger.info("Before overwriting deleting existing customFieldData ");
				//int deleteCount = customFieldDataDao.deleteFromCfdByList(cfdsListToBeDeleted);
				int deleteCount = customFieldDataDaoForDML.deleteFromCfdByList(cfdsListToBeDeleted);

				
				if(logger.isInfoEnabled()) logger.info(" Successfully deleted "+deleteCount+" proceeding with adding new data ");
				
				if(deleteCount < 0){
					if(logger.isInfoEnabled()) logger.info(" Problem while deleting Cfd objs from customField_data table ");
					return null;
				}
			}
			if(logger.isInfoEnabled()) logger.info("returning cfd list of size "+customFieldDataList.size());
			
			return customFieldDataList;
		}
		catch(Exception e) {

			if(logger.isInfoEnabled()) logger.info("**Exception ",e);
			return null;
		}

	} // getCustomFieldsDataList
	
	/**
	 * 
	 * @param srcFieldIndex
	 * @param customFieldDataObj
	 * @return
	 * added for eventTrigger
	 * 
	 * This method is used in cfd copying/merging.
	 * to get the corresponding getter method dynamically of the common filed indices for 2 MLs 
	 * this has been added. 
	 */
	public String getCustomFieldValue(Integer srcFieldIndex,CustomFieldData customFieldDataObj){
		try{
			String tempCustValue = "";
			switch(srcFieldIndex){

			case 1:
				if(logger.isInfoEnabled()) logger.info(" returning getCust1 ");
				return tempCustValue = customFieldDataObj.getCust1();

			case 2:
				if(logger.isInfoEnabled()) logger.info(" returning getCust2 ");
				return tempCustValue = customFieldDataObj.getCust2();	

			case 3:
				if(logger.isInfoEnabled()) logger.info(" returning getCust3");
				return tempCustValue = customFieldDataObj.getCust3();

			case 4:
				if(logger.isInfoEnabled()) logger.info(" returning getCust4 ");
				return tempCustValue = customFieldDataObj.getCust4();

			case 5:
				if(logger.isInfoEnabled()) logger.info(" returning getCust5 ");
				return tempCustValue = customFieldDataObj.getCust5();

			case 6:
				if(logger.isInfoEnabled()) logger.info(" returning getCust6 ");
				return tempCustValue = customFieldDataObj.getCust6();

			case 7:
				if(logger.isInfoEnabled()) logger.info(" returning getCust7 ");
				return tempCustValue = customFieldDataObj.getCust7();

			case 8:
				if(logger.isInfoEnabled()) logger.info(" returning getCust8 ");
				return tempCustValue = customFieldDataObj.getCust8();

			case 9:
				if(logger.isInfoEnabled()) logger.info(" returning getCust9 ");
				return tempCustValue = customFieldDataObj.getCust9();

			case 10:
				if(logger.isInfoEnabled()) logger.info(" returning getCust10 ");
				return tempCustValue = customFieldDataObj.getCust10();

			case 11:
				if(logger.isInfoEnabled()) logger.info(" returning getCust11 ");
				return tempCustValue = customFieldDataObj.getCust11();

			case 12:
				if(logger.isInfoEnabled()) logger.info(" returning getCust12 ");
				return tempCustValue = customFieldDataObj.getCust12();

			case 13:
				if(logger.isInfoEnabled()) logger.info(" returning getCust13 ");
				return tempCustValue = customFieldDataObj.getCust13();

			case 14:
				if(logger.isInfoEnabled()) logger.info(" returning getCust14 ");
				return tempCustValue = customFieldDataObj.getCust14();

			case 15:
				if(logger.isInfoEnabled()) logger.info(" returning getCust15 ");
				return tempCustValue = customFieldDataObj.getCust15();

			case 16:
				if(logger.isInfoEnabled()) logger.info(" returning getCust16 ");
				return tempCustValue = customFieldDataObj.getCust16();

			case 17:
				if(logger.isInfoEnabled()) logger.info(" returning getCust17 ");
				return tempCustValue = customFieldDataObj.getCust17();

			case 18:
				if(logger.isInfoEnabled()) logger.info(" returning getCust18 ");
				return tempCustValue = customFieldDataObj.getCust18();

			case 19:
				if(logger.isInfoEnabled()) logger.info(" returning getCust19 ");
				return tempCustValue = customFieldDataObj.getCust19();

			case 20:
				if(logger.isInfoEnabled()) logger.info(" returning getCust20 ");
				return tempCustValue = customFieldDataObj.getCust20();

			} // switch 

			if(tempCustValue == null || tempCustValue.equals(null)){

				if(logger.isInfoEnabled()) logger.info("tempCustValue.equals(null)");
			}
			return tempCustValue;
		}
		catch(Exception e){

			if(logger.isInfoEnabled()) logger.info(" **Exception ",e);
			return null;
		}

	} // getCustomFieldValue 
	
	/**
	 * 
	 * @param contactObjInDB
	 * @param copyToMailingList
	 * @param tempContactObj
	 * @param optionsFlag
	 * @return
	 * added for EventTrigger
	 * This method prepares a final contact object that will be added to the database 
	 * based on merge/ overwrite /ignore scenarios
	 */
/*	public Contacts formContactObj(Contacts contactObjInDB,MailingList copyToMailingList,Contacts tempContactObj,int optionsFlag) {

		Long contactId = null;
		Calendar currDate = Calendar.getInstance();

		//logger.info("Inside formContactObj ");

		try {

			
			 * if contact Obj is not null check all the fields of tempContact obj and if
			 * it is null, check if that value is present in sourceContactsObj. If yes copy it to final obj 
			 * else continue by copying null 
			 
			if(contactObjInDB != null) { // contact already exists check for merge/overwrite options

				contactId = contactObjInDB.getContactId();
				
				//logger.info("contactId obtd is "+contactId);
				
				if( (optionsFlag & Constants.ET_MERGE_IF_CONTACTS_EXISTS)
						== Constants.ET_MERGE_IF_CONTACTS_EXISTS ){ // merge

					if(logger.isInfoEnabled()) logger.info("inside Merge...merging contactId "+contactId);
					
					
					 * for each contact check all the getter methods...
					 * if its value is null search if the data is present in current record in tempcontacts
					 * if data is found then update/set that field for that contacts Id...
					 

					return new Contacts(contactId,
							copyToMailingList, 
							tempContactObj.getEmailId(),
							
							(contactObjInDB.getFirstName() == null || contactObjInDB.getFirstName().trim().length() <= 0 ? 
									tempContactObj.getFirstName() : contactObjInDB.getFirstName()),
							
							(contactObjInDB.getLastName() == null || contactObjInDB.getLastName().trim().length() <=0 ?
									tempContactObj.getLastName() : contactObjInDB.getLastName()),
							
							(contactObjInDB.getCreatedDate() == null ? currDate : contactObjInDB.getCreatedDate()),
							
							(contactObjInDB.getPurged() == null ? tempContactObj.getPurged() : contactObjInDB.getPurged()),
							
							(contactObjInDB.getOptinStatus() == null? tempContactObj.getOptinStatus() : contactObjInDB.getOptinStatus()), 
							
							(contactObjInDB.getEmailStatus() == null || contactObjInDB.getEmailStatus().trim().length() <= 0 ? 
									tempContactObj.getEmailStatus() : contactObjInDB.getEmailStatus()),
							
							currDate,
							
							
							(contactObjInDB.getLastMailDate() == null ? null : contactObjInDB.getLastMailDate()),
							
							(contactObjInDB.getAddressOne() == null || contactObjInDB.getAddressOne().trim().length() <= 0 ?
									tempContactObj.getAddressOne() : contactObjInDB.getAddressOne()),
							
							(contactObjInDB.getAddressTwo() == null || contactObjInDB.getAddressTwo().trim().length() <= 0 ? tempContactObj.getAddressTwo() : contactObjInDB.getAddressTwo()),
							
							(contactObjInDB.getCity() == null || contactObjInDB.getCity().length() <= 0 ?
									tempContactObj.getCity() : contactObjInDB.getCity()),
							
							(contactObjInDB.getState() == null || contactObjInDB.getCity().length() <= 0 ?
									tempContactObj.getState() : contactObjInDB.getState()),
							
							(contactObjInDB.getCountry() == null || contactObjInDB.getCountry().trim().length() <= 0 ?
									tempContactObj.getCountry() : contactObjInDB.getCountry()),
							
							
							
							(contactObjInDB.getOptin() == 0 ? tempContactObj.getOptin() : contactObjInDB.getOptin()),
							
							(contactObjInDB.getSubscriptionType() == null || contactObjInDB.getSubscriptionType().trim().length() <= 0 ?
									tempContactObj.getSubscriptionType() : contactObjInDB.getSubscriptionType()),
							
							(contactObjInDB.getZip() == null || contactObjInDB.getZip().length() <= 0 ?
									tempContactObj.getZip() : contactObjInDB.getZip()),
									
							(contactObjInDB.getMobilePhone() == null || contactObjInDB.getMobilePhone().trim().length() <= 0 ?
									tempContactObj.getMobilePhone() : contactObjInDB.getMobilePhone())		
							
							);
					
				}
				else if ( (optionsFlag & Constants.ET_OVERWRITE_IF_CONTACTS_EXISTS)
						!= Constants.ET_OVERWRITE_IF_CONTACTS_EXISTS ){

					
					 * if overwrite and merge flags are not set then it is 'Ignore' criteria
					 * if contactId is present then we will continue to the next record without
					 * inserting the current one
					 
					
					if(logger.isInfoEnabled()) logger.info("Contact already exits...continuing with next record");
					return null;
					
				} // Ignore if similar entry exists

				if(logger.isInfoEnabled()) logger.info(" Overwriting if similar contact exists "+contactId);

				
				 *  control comes here when the criteria is copy with overwrite.
				 *  i.e. if it is a new entry we will insert 
				 *  and if similar entry exists we overwrite the record
				 

				if(logger.isInfoEnabled()) logger.info("Contact_id = "+contactId+" list_id = "+copyToMailingList+"emailId = "+tempContactObj.getEmailId());

				// we will overwrit everything but created date and last mail date
				
			
			return new Contacts(contactId,copyToMailingList, tempContactObj.getEmailId(),tempContactObj.getFirstName(),
					tempContactObj.getLastName(),contactObjInDB.getCreatedDate(),tempContactObj.getPurged(),tempContactObj.getOptinStatus(), tempContactObj.getEmailStatus(),
					currDate,contactObjInDB.getLastMailDate(),tempContactObj.getAddressOne(),tempContactObj.getAddressTwo(),
					tempContactObj.getCity(),tempContactObj.getState(),tempContactObj.getCountry(),
					tempContactObj.getOptin(),tempContactObj.getSubscriptionType(),tempContactObj.getZip(),tempContactObj.getMobilePhone());
			
			}
			else { // current contact is not there in DB so inserting with null contactId 

				if(logger.isInfoEnabled()) logger.info(" contact not present in the destination ML inserting with null contactId ");

				
				return new Contacts(contactId,copyToMailingList, tempContactObj.getEmailId(),tempContactObj.getFirstName(),
						tempContactObj.getLastName(),currDate,tempContactObj.getPurged(),tempContactObj.getOptinStatus(), tempContactObj.getEmailStatus(),
						currDate,null,tempContactObj.getAddressOne(),tempContactObj.getAddressTwo(),
						tempContactObj.getCity(),tempContactObj.getState(),tempContactObj.getCountry(),
						tempContactObj.getOptin(),tempContactObj.getSubscriptionType(),tempContactObj.getZip(),tempContactObj.getMobilePhone());
			}
			


		}
		catch(Exception e) {

			if(logger.isErrorEnabled()) logger.error(" **Exception ",e);
			return null;
		}

	} // formContactObj
*/

	private String getCustomFields(String content) {
		//logger.debug("+++++++ Just Entered +++++"+ content);
		String cfpattern = "\\[([^\\[]*?)\\]";
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		totalPhSet = new HashSet<String>();
		String cfNameListStr = "";

		try {
			while(m.find()) {

				ph = m.group(1); //.toUpperCase()
//				if(logger.isInfoEnabled()) logger.info("Ph holder :" + ph);

				if(ph.startsWith("GEN_")) {
					totalPhSet.add(ph);
				}else if(ph.startsWith(Constants.UDF_TOKEN)) {
					totalPhSet.add(ph);
				}
				else if(ph.startsWith("CC_")) {
					totalPhSet.add(ph);
				
				}else if(ph.startsWith("REF_CC_")) {
					totalPhSet.add(ph);
				}
				else if(ph.startsWith("MLS_")){
					totalPhSet.add(ph);
					logger.info("milestone placeholders-----------"+ph);
				}
				else if(ph.startsWith("CF_")) {
					totalPhSet.add(ph);
					cfNameListStr = cfNameListStr + (cfNameListStr.equals("") ?  "" : Constants.DELIMETER_COMMA)
											+ "'" + ph.substring(3) + "'";
				} // if(ph.startsWith("CF_"))
			} // while
			
			if(logger.isDebugEnabled()) logger.debug("+++ Exiting : "+ totalPhSet);
		} catch (Exception e) {
			logger.error("Exception while getting the place holders ", e);
		}

		if(logger.isInfoEnabled()) logger.info("CF PH cfNameListStr :" + cfNameListStr);

		return cfNameListStr;
	}


	/**
	 * Get the URLs
	 * @param htmlContent
	 * @return Set of URL strings
	 */
	private Set<String> getUrls(String htmlContent) {


		int options = 0;
		options |= 128; 	//This option is for Case insensitive
		options |= 32;
		Set<String> urlSet = new HashSet<String>();

		if(htmlContent==null) return urlSet;

		try{

		    Pattern p = Pattern.compile(PropertyUtil.getPropertyValue("LinkPattern"), options);
		    Matcher m = p.matcher(htmlContent);
		    String anchorUrl;

	        while (m.find()) {

	        	anchorUrl = m.group(2).trim();
	            if(anchorUrl.indexOf("#") != -1 || anchorUrl.indexOf("mailto") != -1) {
	            	continue;
	            }
	            else if(anchorUrl.contains("action=click")) {
	            	anchorUrl = anchorUrl.substring(anchorUrl.lastIndexOf("url=")+4);
	            	if(logger.isInfoEnabled()) logger.info("URL is: " + anchorUrl);
					urlSet.add(anchorUrl);
	            }
	        } //while

		}
		catch (Exception e) {
			logger.error("** Exception : Problem while getting the URL set ", e);
		}
		return urlSet;

	} // getUrls
	
	/**
	 * Added for EventTrigger
	 * 
	 * @param eventTrigger
	 * @param triggerCampaignObj
	 * @param listIdsStr
	 * @param isSegment
	 * @return
	 * 
	 * This method forms query to insert into temp contacts table based on the trigger type 
	 * and offset conditions given by the user.
	 * 
	 * offset: When Offset is zero => 'immediately'
	 * for this case we take a buffer time of one hour
	 */
	public String triggerQryGenerator(EventTrigger eventTrigger,Campaigns triggerCampaignObj,
			String listIdsStr, boolean isSegment){ // added for EventTrigger

		if(logger.isInfoEnabled()) logger.info("Just entered triggerQryGenerator....");
		MLCustomFieldsDao mlCustomFieldsDao;
		mlCustomFieldsDao = (MLCustomFieldsDao)context.getBean("mlCustomFieldsDao");
		CampaignSentDao campaignSentDao = (CampaignSentDao)context.getBean("campaignSentDao");

		String triggerType = eventTrigger.getTriggerType();
		String eventField = eventTrigger.getEventField();
		String custOrGenFieldName = "";
		String generatedQryStr="";
		String inputCampaignName = "";

		long userId = eventTrigger.getUsers().getUserId();
		int columnIndex = -1;
		int offset = -1;  //1 day = 1440 mins
		int strLen = eventField.length();

		try {

			offset = eventTrigger.getMinutesOffset();

			if(isSegment){

				String segmentListQuery = QueryGenerator.generateListSegmentQuery(triggerCampaignObj.getListsType());

				if(logger.isDebugEnabled()){

					logger.info(" Generated Query :"+segmentListQuery);
				}
				generatedQryStr = "INSERT IGNORE INTO tempcontacts ("+Constants.QRY_COLUMNS_TEMP_CONTACTS+", cf_value, domain, event_source_id)" +
						"("+segmentListQuery+")";
			}
			else{

				if (triggerType.equals(Constants.ET_TYPE_CONTACT_DATE)) { //CDATE = Contact Date

					if(eventField.toUpperCase().startsWith("GEN_")) { //eventField will be GEN_contactsdatefield

						if(strLen <= 4) {

							if(logger.isInfoEnabled()) logger.info("eventField for CDATE, CUST_ is not entered as per reqd notation....exiting triggerQryGenerator");
							return "";
						}

						fromSource += '_'+triggerType +'_'+ eventField.toUpperCase();
						custOrGenFieldName = eventField.substring(4);
						if(logger.isInfoEnabled()) logger.info("GEN_ value of custOrGenFieldName is "+custOrGenFieldName);

						if(offset < 0) {
							
							if(logger.isInfoEnabled()) logger.info("offset cannot be negative for already occurred scenarios like last_sent_date, etc");
							generatedQryStr = "";
							return generatedQryStr;
						}

						generatedQryStr = " INSERT IGNORE INTO tempcontacts ("+Constants.QRY_COLUMNS_TEMP_CONTACTS+", cf_value, domain, event_source_id)  "+
						" (SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+",NULL, SUBSTRING_INDEX(c.email_id, '@', -1), null FROM contacts c, mailing_lists ml "+
						" WHERE c.user_id = ml.user_id AND ml.list_id IN ("+listIdsStr+") " +
						" AND ( c.mlbits & ml.mlbit ) >0  AND c.email_status = '" +Constants.CONT_STATUS_ACTIVE+"' ";

						if(offset >= 1440){ //1440 mins = 24 hrs i.e. 1 day

							offset = offset/1440; //converting minutes into days
							generatedQryStr += " AND DATEDIFF(now(),"+custOrGenFieldName+") = "+offset+" ";
						}
						else { // make sure the value stored from subscriber to cust field is of format %d/%m/%Y ex: 10/07/1985
			
							
							/*
							 * TIMEDIFF() returns the time difference between 2 dates as hh:mm:ss 
							 * Now we use HOUR() finc to extract the hour from obtd timediff
							 * ex: HOUR('-20:34:40') = 20   also  HOUR('20:34:40') = 20 (not -20)
							 * so to check if it is + 20 or - 20 we put additional condition as below for offset >0, <0 
							 */

							
							if(offset > 0) { // offfset could be either positive or zero for GEN_ 
								
								offset = offset/60; //converting minutes to hours
								generatedQryStr += " AND now() > "+custOrGenFieldName+" ";
							}
														
							generatedQryStr += " AND HOUR( TIMEDIFF(now(),"+custOrGenFieldName+") ) = "+offset;

						}
					}
					else if(eventField.toUpperCase().startsWith("CUST_")){ // format: CUST_BDAY_dob / CUST_ANIV_anivrsry

						if(strLen <= 10) {
							
							if(logger.isInfoEnabled()) logger.info("eventField for CDATE, CUST_ is not entered as per reqd notation....exiting triggerQryGenerator");
							return "";
						}

						fromSource += '_'+triggerType +'_'+ eventField.substring(0, 9).toUpperCase();
						
						custOrGenFieldName = eventField.substring(10); // ex: CUST_BDAY_columnName CUST_ANIV_columnName

						if(eventTrigger.getMailingLists().size() != 1 ) {
						
							if(logger.isInfoEnabled()) logger.info(" Found either multiple or no mailing lists, cannot get their custom fields");
							generatedQryStr = "";
							return generatedQryStr;
						}


						Set<MailingList> mailingListSet;
						mailingListSet = eventTrigger.getMailingLists();

						Iterator<MailingList> iterator = mailingListSet.iterator();
						MailingList mailingList = iterator.next();

						columnIndex = mlCustomFieldsDao.findIndexByNameMl(mailingList,custOrGenFieldName);

						if(columnIndex < 0) {
							
							if(logger.isInfoEnabled()) logger.info(" no customfiled exists for column name "+custOrGenFieldName);
							generatedQryStr = "";
							return generatedQryStr;
						}
						custOrGenFieldName = "cfd.cust_"+columnIndex;

						if(logger.isInfoEnabled()) logger.info("CUST_ value of custOrGenFieldName is "+custOrGenFieldName);

						generatedQryStr = " INSERT IGNORE INTO tempcontacts ("+Constants.QRY_COLUMNS_TEMP_CONTACTS+", cf_value, domain, event_source_id)  "+
										  " (SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+",NULL, SUBSTRING_INDEX(c.email_id, '@', -1), null FROM contacts c,mailing_lists ml, customfield_data cfd "+
										  " WHERE c.cid = cfd.contact_id AND c.user_id = ml.user_id " +
										  " AND ml.list_id IN (" +listIdsStr+ ") " +
										  " AND ( c.mlbits & ml.mlbit ) >0  AND c.email_status = '" +Constants.CONT_STATUS_ACTIVE+"' ";

						if(offset == 0) {
						
							generatedQryStr += " AND DATE_FORMAT(now(),'%m-%d %H') = " +
									" DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d %H') ";
						}
						else if( offset >= 1440 || offset <= -1440 ){ //1440 mins = 24 hrs i.e. 1 day

							offset = offset/1440; //converting minutes into days
						
							if(offset > 0) {

								generatedQryStr += " AND now() > STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s') " +
								" AND DATE_FORMAT(DATE_SUB(now(), INTERVAL "+offset+" DAY),'%m-%d') " +
								" = DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d') ";
							}
							else {
								
								offset = -1 * offset;
								generatedQryStr += " AND DATE_FORMAT(DATE_ADD(now(), INTERVAL "+offset+" DAY),'%m-%d') = DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d') ";
							}
							
						}
						else { //Hours offset 
							// AND DATE_FORMAT(DATE_SUB(now(), INTERVAL 12 HOUR),'%m-%d %H') = DATE_FORMAT(STR_TO_DATE(cfd.cust_3,'%d/%m/%Y %H:%i:%s'),'%m-%d %H');
							
							 //hours

							/*
							 * Since the custom date is stored as a string in DB
							 *  and most of the entries don't have time part...
							 *  adding offset hours to current time and checking
							 *  with zeroth hour of the day.(this logic is different
							 *   than the other trigger types which have datetime values)
							 *  ( %H:%i:%s will give 00:00:00)
							 */

							if(offset < 0) {

								offset = -1 * offset/60; //converting minutes to hours
							    generatedQryStr += "  AND DATE_FORMAT(DATE_ADD(now(), INTERVAL "+offset+" HOUR),'%m-%d %H') = " +
							    		" DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d %H') ";

							}
							else if(offset > 0) {

								offset = offset/60; //converting minutes to hours
					
								generatedQryStr += " AND now() > STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s') " +
							    "  AND DATE_FORMAT(DATE_SUB(now(), INTERVAL "+offset+" HOUR),'%m-%d %H') = " +
					    		"  DATE_FORMAT(STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s'),'%m-%d %H') ";
							}
						
						} // hours offset 
						 
						
/*						if( offset >= 1440 || offset <= -1440 ){ //1440 mins = 24 hrs i.e. 1 day

							offset = offset/1440; //converting minutes into days
								
							generatedQryStr += " AND DATEDIFF(now(),STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y')) = "+offset+" ";
						}
						else { //hours

							
							 * Since the custom date is stored as a string in DB
							 *  and most of the entries don't have time part...
							 *  adding offset hours to current time and checking
							 *  with zeroth hour of the day.(this logic is different
							 *   than the other trigger types which have datetime values)
							 *  ( %H:%i:%s will give 00:00:00)
							 

							if(offset < 0) {

								offset = -1 * offset/60; //converting minutes to hours
							    generatedQryStr += " AND now() < " +
							    		"STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s') ";

							}
							else if(offset > 0){

								offset = offset/60; //converting minutes to hours
								generatedQryStr += " AND now() > " +
										"STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s') ";

							}
							
							generatedQryStr += " AND HOUR( TIMEDIFF(now()," +
									"STR_TO_DATE("+custOrGenFieldName+",'%d/%m/%Y %H:%i:%s')) ) = "+offset;

						} // hours
*/
					}//CUST_

				}//CDATE
				else if (triggerType.equals(Constants.ET_TYPE_SPECIFIC_DATE)){

					fromSource += '_'+triggerType +'_'+eventField; //ex: EventTrigger_SDATE_VDAY

					generatedQryStr = " INSERT IGNORE INTO tempcontacts ("+Constants.QRY_COLUMNS_TEMP_CONTACTS+", cf_value, domain, event_source_id)  "+
					" (SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+",NULL, SUBSTRING_INDEX(c.email_id, '@', -1), null FROM contacts c,mailing_lists ml, trigger_custom_events tce "+
					" WHERE c.user_id = ml.user_id AND ml.list_id IN ("+listIdsStr+") " +
					" AND ( c.mlbits & ml.mlbit ) >0  AND c.email_status = '" +Constants.CONT_STATUS_ACTIVE+"' ";

					if(offset >= 1440 || offset <= -1440){ //1440 mins = 24 hrs i.e. 1 day

						offset = offset/1440; //converting minutes into days
						
						generatedQryStr += " AND DATEDIFF(now(),tce.event_date) = "+offset+" ";
					}
					else {

						if(offset < 0) {

							offset = -1 * offset/60; //converting minutes to hours
						    generatedQryStr += " AND now() < tce.event_date  ";
						}
						else if (offset > 0){

							offset = offset/60; //converting minutes to hours
							generatedQryStr += " AND now() > tce.event_date ";
						}
						
						generatedQryStr += " AND HOUR( TIMEDIFF(now(),tce.event_date) ) = "+offset;

					}

				}
				else if (triggerType.equals(Constants.ET_TYPE_CAMPAIGN_OPEN)){

					List<Long> crIdsList;
					fromSource += '_'+triggerType +'_'+eventField; //ex: EventTrigger_SDATE_CampName
					inputCampaignName = eventTrigger.getEventField();
					String crIdsListStr = "";

					if(offset < 0) {

						if(logger.isInfoEnabled()) logger.info("offset cannot be negative for OPENS trigger type" +
						"-returning empty query string");
						generatedQryStr = "";
						return generatedQryStr;
					}

					/*
					 * 1a. get all the crIds for a given campaign for the sourceType
					 * For OPENS bewlo we input eventField not trigger name
					 *
					 */
					crIdsList = campaignReportDao.getOpensClicksCrIds(inputCampaignName,userId,true);

					if(crIdsList == null) {

						if(logger.isInfoEnabled()) logger.info("Got a null crIdsList due to either empty TriggerName or Exception in query");
						return null;
					}
					if(crIdsList.size() <= 0){

						if(logger.isInfoEnabled()) logger.info("No CrIds with trigger name "+inputCampaignName+" with user id "+userId+".. exiting ");
						return "";
					}

					/*
					 * 1b. Forming comma separated crIdsList string
					 */
					for(Iterator<Long> iterator = crIdsList.iterator();iterator.hasNext();) {

						if(crIdsListStr.length() == 0) {

							crIdsListStr += iterator.next();
						}
						else {

							crIdsListStr += ","+iterator.next();
						}
					}

					/*
					 * 2. getting sent_ids of all the contacts (for each sent id getting the latest opened entry)
					 */
					String sentIdsListStr = campaignSentDao.getSentIdsByCrIdsForOpens(crIdsListStr,offset);  // TODO Need to fine tune HERE

					if(sentIdsListStr == null || sentIdsListStr.isEmpty() ){

						if(logger.isInfoEnabled()) logger.info(" got a empty or null senIds list...exiting with empty query");
						return "";
					}

					/*
					 * 3. getting all the contactIds based on above obtained campaign_sent ids
					 *  above 3 steps can be combined in a single query but it is very slow...hence doing in steps
					 */
					String subQryStr = " SELECT DISTINCT c.cid "+
					" FROM contacts c " +
					" WHERE c.cid IN "+
					" (SELECT DISTINCT contact_id "+
					" FROM campaign_sent cs "+
					" WHERE cs.sent_id IN ("+sentIdsListStr+")) ";

					/*
					 * 4. Inserting into temp contacts....
					 *TODO fine tune below query -is very slow
					 */
					generatedQryStr = " INSERT IGNORE INTO tempcontacts ("+Constants.QRY_COLUMNS_TEMP_CONTACTS+", cf_value, domain, event_source_id)  "+
					" (SELECT DISTINCT "+Constants.QRY_COLUMNS_CONTACTS+",NULL, SUBSTRING_INDEX(c.email_id, '@', -1), null " +
					" FROM contacts c " +
					" WHERE c.cid IN ("+subQryStr+") " +
					" AND c.email_status LIKE '" +Constants.CONT_STATUS_ACTIVE+"' ";

				}
				else if ( triggerType.equals(Constants.ET_TYPE_CAMPAIGN_CLICK)){

					String urlListStr = "";
					String crIdsListStr = "";
					String[] urlsStr= eventTrigger.getEventField().split("\\|");
					List<Long> crIdsList;

					fromSource += '_'+triggerType +'_'+eventField; //ex: EventTrigger_SDATE_CampName|url1|url2

					if(offset < 0) {
						
						if(logger.isInfoEnabled()) logger.info("offset cannot be negative for CLICKS trigger type" +
						"-returning empty query string");
						generatedQryStr = "";
						return generatedQryStr;
					}

					inputCampaignName = urlsStr[0];
					if(logger.isInfoEnabled()) logger.info(" inputCampaignName "+inputCampaignName);


					/*
					 * 1. get all the crIds for a given campaign and current sourceType
					 */
					crIdsList = campaignReportDao.getOpensClicksCrIds(inputCampaignName,
							userId,false);

					if(crIdsList == null) {

						if(logger.isInfoEnabled()) logger.info("Got a null crIdsList due to either empty TriggerName or Exception in query");
						return null;
					}

					if(crIdsList.size() <= 0){

						if(logger.isInfoEnabled()) logger.info(" The campaign "+inputCampaignName+" with user id "+userId+" has not been sent yet...exiting ");
						return "";
					}


					/*
					 * Forming comma separated crIdsList string
					 */
					for(Iterator<Long> iterator = crIdsList.iterator();iterator.hasNext();) {

						if(crIdsListStr.length() == 0) {

							crIdsListStr += iterator.next();
						}
						else {

							crIdsListStr += ","+iterator.next();
						}
					}


					/*
					 * Iterate through the urlsStr and for a comma separated Url list
					 * x=0 gives campaign name so start with x=1
					 */
					for (int x=1; x<urlsStr.length; x++) {

						if(urlListStr.length()==0) {

							urlListStr += "\'"+urlsStr[x]+"\'";
						}
						else {
							urlListStr += "," + "\'"+urlsStr[x]+"\'";
						}
					}

					/*
					 * 2. get all the contactIds from CampaignSent and opens tables in a sub query
					 */
					String subQryStr = getContactIdsByCrIdsAndClicks(crIdsListStr,urlListStr,offset);

					if(subQryStr.length() <=0 ) {
						if(logger.isInfoEnabled()) logger.info(" There are no contacts for given URL clicks criteria ");
						return "";
					}


					/*
					 * 3. get all the contacts using above obtained contact_ids
					 */
					generatedQryStr = " INSERT IGNORE INTO tempcontacts ("+Constants.QRY_COLUMNS_TEMP_CONTACTS+", cf_value, domain, event_source_id) "+
					" ("+subQryStr+" ";


					/*			generatedQryStr = " INSERT IGNORE INTO tempcontacts "+
					" (SELECT DISTINCT c.*,NULL " +
					" FROM contacts c " +
					" WHERE c.cid IN ("+subQryStr+") " +
					" AND c.email_status LIKE '" +Constants.CONT_STATUS_ACTIVE+"' ";*/

				}
				else {

					if(logger.isInfoEnabled()) logger.info("Trigger type did not match, exiting with empty query");
					return "";
				}

				generatedQryStr += ") ";
				//generatedQryStr += " ORDER BY c.cid) ";

			}//else of segment

			if(logger.isInfoEnabled()) logger.info("fromSource = "+fromSource);
			if(logger.isInfoEnabled()) logger.info("Exiting triggerQryGenerator with query:" + generatedQryStr);
			return generatedQryStr;

		}
		catch(Exception e) {
			generatedQryStr = "";
			logger.error("** Exception in triggerQryGenerator",e);
			return generatedQryStr;
		}

	}//triggerQryGenerator
	
	private void updateSegmentCountAndQuery(SegmentRules segmentRule, String generatedQuery, String generatedCountQuery){
		
		logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<called 2>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		long size = 0;
		size = contactsDao.getSegmentedContactsCount(generatedCountQuery);
		
		segmentRule.setSize(size);
		//segmentRule.setEmailSegQuery(generatedQuery);
		segmentRule.setLastRefreshedOn(Calendar.getInstance());
		
		segmentRulesDaoForDML.saveOrUpdate(segmentRule);
		
	}
	/*
	 * could be used to check of bulk is there then only can be allow the campigns to proceed.
	 */
	public boolean checkVMTAConnectivity(Users user,Campaigns campaign,CampaignSchedule campSchedule) throws BaseServiceException{
//	Vmta vmta = user.getVmta();
/*	String vmta = PropertyUtil.getPropertyValueFromDB("VMTA"); //SendGridAPI;
	String genericVMTAAccount = PropertyUtil.getPropertyValueFromDB("BulkVMTA"); // Generic
*/
	UsersDao usersDao = (UsersDao)context.getBean("usersDao");
	VmtaDao vmtaDao = (VmtaDao)context.getBean("vmtaDao");
	UserVmta userVmta = usersDao.findBy(user.getUserId(),Constants.SENDING_TYPE_BULK);
	Vmta vmtaObj = null;
	
	if(userVmta != null) {
			vmtaObj = vmtaDao.find(userVmta.getVmtaId());
			
		}
	/*else
		{
		 {
			vmtaObj = vmtaDao.getGenericVmta(vmta, genericVMTAAccount);	
		}*/
	else{
		vmtaObj = user.getVmta();
	}
	/*else {
		Messages messages = new Messages("Campaigns", "Campaign Schedule" , 
				"The scheduled campaign is canceled since has no userVmta enabled for this user ",
				Calendar.getInstance(),  "Inbox", false, "Info", campSchedule.getUser());
		messagesDaoForDML.saveOrUpdate(messages);
		Utility.sendCampaignFailureAlertMailToSupport(campaign.getUsers(),"",campaign.getCampaignName(),"",campSchedule.getScheduledDate(),
				"Schedule Failure due to no userVmta enabled","","","");
		
		logger.debug("<<<<<<<<<<<No bulk vmta found for the specific user to proceed campaign..");
		return false;
	}*/
	if(vmtaObj != null){
	//	if(Constants.SMTP_SENDGRIDAPI.equalsIgnoreCase(vmta.getVmtaName())|| vmta.getVmtaName().equalsIgnoreCase("AmazonSES")) {
			
			//checking connection
			ExternalSMTPSender checkConnectionObj=new ExternalSMTPSender();
			boolean gotConnection=checkConnectionObj.checkConnection(vmtaObj);
				 if(!gotConnection){
					 logger.info("----Connection refused----");
					 logger.info("----sending alert----");
					 Utility.sendCampaignFailureAlertMailToSupport(user,"",campaign.getCampaignName(),"",campSchedule.getScheduledDate(),
							 "Connection or Authentication Failure","","","");
					 
					 return false;
				 }
				 return true;
	//	}
	
	}
	return false;
	}
	private void sendMailToSupport(Users user, String campaignName, Calendar scheduledDate) throws BaseServiceException{
		try {
			// TODO Auto-generated method stub
			
			EmailQueueDao emailQueueDao = null;
			EmailQueueDaoForDML emailQueueDaoForDML = null;
			try {
				emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
				emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
			} catch (Exception e) {
				
				logger.error(e);
				throw new BaseServiceException("No dao(s) found in the context");
				
			}
			
			//String supportMail = PropertyUtil.getPropertyValueFromDB(Constants.ALERT_TO_EMAILID);
			//String supportMailId = PropertyUtil.getPropertyValueFromDB(Constants.PROPS_KEY_SUPPORT_EMAILID);
			String supportMail = PropertyUtil.getPropertyValueFromDB(Constants.Mail_To_Support);
			String subject = PropertyUtil.getPropertyValueFromDB(Constants.NO_HTMLCONTENT_CAMPAIGN_SUBJECT);
			String message = PropertyUtil.getPropertyValueFromDB(Constants.NO_HTMLCONTENT_CAMPAIGN_EMAIL_CONTENT);
			
			String userName= Utility.getOnlyUserName(user.getUserName());
			subject=subject.replace(Constants.ALERT_MESSAGE_PH_USERNAME, userName);
			message=message.replace(Constants.ALERT_MESSAGE_PH_USERNAME, userName);
			message=message.replace(Constants.ALERT_MESSAGE_PH_ORGNAME,user.getUserOrganization().getOrganizationName());
			message=message.replace(Constants.ALERT_MESSAGE_PH_EMAILCAMPAIGNNAME, campaignName);
			
			/*EmailQueue emailQueue = new EmailQueue (subject, message, Constants.EQ_TYPE_SUPPORT_ALERT, 
					Constants.EQ_STATUS_ACTIVE, user.getEmailId(), supportMail, new Date(), user);
			
			emailQueueDaoForDML.saveOrUpdate(emailQueue);*/
			
			EmailQueue emailQueue = new EmailQueue(subject, message, Constants.EQ_TYPE_SUPPORT_ALERT,
					Constants.EQ_STATUS_ACTIVE, user.getEmailId(), new Date());	
			 
			emailQueueDaoForDML.saveOrUpdate(emailQueue);
			
			EmailQueue emailQueueSupport = new EmailQueue(subject, message, Constants.EQ_TYPE_SUPPORT_ALERT,
					Constants.EQ_STATUS_ACTIVE, supportMail, new Date());
			
			emailQueueDaoForDML.saveOrUpdate(emailQueueSupport);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error while sending a mail to client & support", e);
			throw new BaseServiceException("Error while sending a mail to client & support");
		}
		
		}
	
	
} // class PmtaMailMergeSubmitter




/**
 * Holds server name (or IP address) and SMTP port.
 * You could add user name and password for authentication here as well and
 * then have the code that creates the connection use these.
 */
class Server {
    public String name;
    public int port;

    public Server(String theName, int thePort) {
        name = theName;
        port = thePort;
    }
}




