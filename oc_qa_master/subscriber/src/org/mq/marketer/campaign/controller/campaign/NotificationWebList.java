package org.mq.marketer.campaign.controller.campaign;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.IosNotification;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Notification;
import org.mq.marketer.campaign.beans.NotificationSchedule;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.PushNotificationAndroid;
import org.mq.marketer.campaign.beans.PushNotificationIos;
import org.mq.marketer.campaign.beans.PushNotificationData;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MultiLineMessageBox;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.NotificationCampaignReportDao;
import org.mq.marketer.campaign.dao.NotificationDao;
import org.mq.marketer.campaign.dao.NotificationDaoForDML;
import org.mq.marketer.campaign.dao.NotificationScheduleDao;
import org.mq.marketer.campaign.dao.NotificationScheduleDaoForDML;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.ClickHouseSalesQueryGenerator;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyTransactionExpiryDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
 
@SuppressWarnings("rawtypes")
public class NotificationWebList extends GenericForwardComposer {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Long userId;
	private NotificationDao notificationDao;
	private NotificationDaoForDML notificationDaoForDML;
	private NotificationScheduleDaoForDML notificationScheduleDaoForDML;
	private NotificationScheduleDao notificationScheduleDao;
	private Users currentUser;
	private Session session;
	private Listbox notificationListlbId;
	private Popup copyNotificationPopupId;
	private Window testNotificationWinId;
	private Textbox copyNotificationNameTbId;
	private int totalSize;
	private Paging campListPaging,campListPaging1;
	private String fromDateStr;
	private String toDateStr;
	private Textbox searchByCmpgnNameTbId;
	private Div searchByCmpgnCreationDateDivId;
	private Div searchByCmpgnStatusDivId;
	private Div searchByCmpgnNameDivId;
	private Listbox srchLbId;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private Listbox notificationCampStatusLb;
	private final String SEARCH_BY_NAME = "Name";
	private final String SEARCH_BY_STATUS = "Status";
	private final String SEARCH_BY_DATE = "Date";
	private Label testNotificationWinId$msgLblId;
	private Session sessionScopeForTimeZone;
	private Bandbox notificationActionsBandBoxId;
	private NotificationCampaignReportDao notificationCampaignReportDao;
	//testNotificationWinId
	//testNotificationWinId
	private Textbox testNotificationWinId$mblNumTxtBoxId;
	private ContactsDao contactsDao;
	private static String USER_DATA_URL;
	private static String IMAGES_URL;
	private static String optcultureFireBaseServerKey;
	
	public NotificationWebList(){
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Notification","",style,true);
		
		
		this.notificationDao = (NotificationDao)SpringUtil.getBean("notificationDao");
		this.notificationScheduleDaoForDML = (NotificationScheduleDaoForDML)SpringUtil.getBean("notificationScheduleDaoForDML");
		this.notificationDaoForDML = (NotificationDaoForDML)SpringUtil.getBean("notificationDaoForDML");
		this.notificationScheduleDao = (NotificationScheduleDao)SpringUtil.getBean("notificationScheduleDao");
		this.notificationCampaignReportDao =(NotificationCampaignReportDao)SpringUtil.getBean("notificationCampaignReportDao"); 
		this.userId = GetUser.getUserId();
		this.session = Sessions.getCurrent();
		this.notificationCampStatusLb =  (Listbox)Executions.getCurrent().getAttribute("notificationCampStatusLb");
		this.contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao"); 
		currentUser = GetUser.getUserObj();
		IMAGES_URL = PropertyUtil.getPropertyValue("ApplicationUrl");
		optcultureFireBaseServerKey = PropertyUtil.getPropertyValueFromDB("optcultureFireBaseServerKey");
		USER_DATA_URL = IMAGES_URL+"UserData/";
		  
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		try {
			super.doAfterCompose(comp);
			logger.debug("----just entered---");
			sessionScopeForTimeZone = Sessions.getCurrent();
			 //itemsSize=0;
			 totalSize = 0;
			 int pSize;
			 totalSize = notificationDao.getCount(currentUser.getUserId(), "All");
			 try
			 {
			 if(notificationCampStatusLb.getSelectedItem().getLabel().equalsIgnoreCase("All"))
			 {
				 pSize = totalSize;
			 }
			 else
			 {
				 pSize = getStatusCount();
			 }
			 }
			 catch(NullPointerException ne)
			 {
				 pSize=totalSize ; 
			 }
			 logger.info("totalsize is ::::"+totalSize);
			 
			 
			 campListPaging.addEventListener("onPaging", this); 
			 campListPaging.setAttribute("onPaging", "topPaging");
			 campListPaging.setTotalSize(pSize);
			 
			 campListPaging1.setTotalSize(pSize);
			 campListPaging1.setAttribute("onPaging", "bottomPaging");
			 campListPaging1.addEventListener("onPaging", this);
			
			 copyNotificationPopupId.addEventListener("onOpen", this);
			 redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
			
		} catch (Exception e) {
			logger.error("** Exception : ",e);
		}
	}
	
	public int getStatusCount()
	{
		 List<Notification> notificationList = getNotificationCampaigns(0, totalSize);
		
		 int itemSize=0;
		 if(notificationList != null && notificationList.size() > 0) {
			 for (Notification notification : notificationList) {
				 String status = getCampaignStatus(notification);
					
					 int index = notificationCampStatusLb.getSelectedIndex();
					 if(!(index <= 0))
					if(status.equalsIgnoreCase(getSelectedStatus()) && !getSelectedStatus().equalsIgnoreCase("All")){
						itemSize ++; 
					}
			 }
		 }
		 logger.info("status size is"+itemSize);
		 return itemSize;
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		logger.debug("------just entered---in onEvent");
		
		super.onEvent(event);
		
		try{
		Object obj = event.getTarget();
		Notification notification ;
		int index = 0;
		if(obj instanceof Image){
			Image img = (Image)obj;
			String imgAttr = (String)img.getAttribute("addEvent");
			
			Listitem li = (Listitem)img.getParent().getParent().getParent();
			notificationListlbId = (Listbox)li.getParent();
			index = li.getIndex();
			notification = (Notification)li.getValue();
			if(imgAttr.equalsIgnoreCase("sendTestNotification")){
				
				if(currentUser!=null && currentUser.getSubscriptionEnable() && notification.getCategory() ==null){
					MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
						"\n Please specify campaign category for this communication and try again.", "color:red;");
					return;
				}
				
				testNotificationWinId$msgLblId.setValue("");
				testNotificationWinId$mblNumTxtBoxId.setValue("");
				
				if(notification == null){
					logger.error("** Exception : Notification Campaign object is null when send test Notification link is clicked**");
					return;
				}
				testNotificationWinId.setAttribute("NotificationCampaign", notification);
				testNotificationWinId.doModal();
			}
			else if(imgAttr.equalsIgnoreCase("sendAgain")) {
				
				
				Set<MailingList> mailingLists = notification.getMailingLists();
				long totalCount = contactsDao.getAllNotificationCount(mailingLists);
				if(currentUser!=null && currentUser.getSubscriptionEnable() && notification.getCategory() ==null){
					MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
						"\n Please specify campaign category for this communication and try again.", "color:red;");
					return;
				}
								
				if(mailingLists == null || mailingLists.size() <= 0) {
					
					MessageUtil.setMessage("Please complete the Notification campaign creation process.","color:red","TOP");
					return;
					
				}
								
				
				String appName = PropertyUtil.getPropertyValueFromDB("ApplicationName");
				
				
				String msg = "";
				List<NotificationSchedule> scheduleList = null;
				NotificationScheduleDao notificationScheduleDao = null;
				//
				try{
					
					notificationScheduleDao = (NotificationScheduleDao)SpringUtil.getBean("notificationScheduleDao");
					
					
					scheduleList = notificationScheduleDao.findActiveOrDraftNotificationSchedules(notification.getNotificationId());
					
					if(scheduleList != null && scheduleList.size() > 0){
						
						Calendar currentCal = Calendar.getInstance();
						int expiredSchedules = 0;
						int activeSchedules = 0;
						
						for(NotificationSchedule campaignSchedule : scheduleList){
							
							if(campaignSchedule.getStatus() == 2 && campaignSchedule.getScheduledDate().before(currentCal)){
								expiredSchedules ++; 
							}
							else if(campaignSchedule.getStatus() == 2 ){
								activeSchedules ++;
							}
						}
						
						
						
						logger.info("Expiry Count is "+expiredSchedules +"\t Active Count "+activeSchedules);
						if(expiredSchedules >0 && activeSchedules >0 ){
							msg = "Note : This action will also activate "+(scheduleList.size() - expiredSchedules)+" schedule(s) and expire "+expiredSchedules+" schedule(s) previously in draft status in this campaign.";
						}
						else if(expiredSchedules <=0 && activeSchedules >0 ){
							msg = "Note : This action will also activate "+(scheduleList.size() - expiredSchedules)+" schedule(s) in this campaign.";
						}
						else if(expiredSchedules >0 && activeSchedules <=0 ){
							msg = "Note : This action will also expire "+expiredSchedules+" schedule(s) previously in draft status in this campaign.";
						}
						
						
					}
					
				}catch(Exception e){
					logger.error(" Exception >>>>>>>> ",e);
				}
				
				//
				
				try {
					
					
					int confirm = Messagebox.show("Are you sure you want to send the campaign now? \n"+msg,
							appName, Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					
					
					
					if(confirm == 1){
						
						MessageUtil.clearMessage();
						int expiredSchedules = 0,activeSchedules=0;
						
						try{
							
							if(scheduleList != null && scheduleList.size() > 0){
								
								Calendar currentCal = Calendar.getInstance();
								
								for(NotificationSchedule campaignSchedule : scheduleList){
									
									if(campaignSchedule.getStatus() == 2 && campaignSchedule.getScheduledDate().before(currentCal)){
										campaignSchedule.setStatus((byte)7);
										expiredSchedules ++; 
									}
									else if(campaignSchedule.getStatus() == 2 ){
										campaignSchedule.setStatus((byte)0);
										activeSchedules ++;
									}

									
								}
								notificationScheduleDaoForDML.saveByCollection(scheduleList);

							}
							
							
						}catch(Exception e){
							logger.error("Exception >>> ",e);
						}
						
						logger.info("Number of Expired Campaigns :"+ expiredSchedules+" & Active Campaings :"+activeSchedules );
						
						ContactsDao contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
						CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
						
						
						String sentType = notification.getListType();
						if(sentType.startsWith("Total")) { 
							Set<MailingList> mlists = notification.getMailingLists();
							String ml_ids_str = "";
							
							for(MailingList mailingList:mlists) {
								
								if(ml_ids_str.length() > 0) ml_ids_str+=",";
								ml_ids_str += mailingList.getListId();
							}
							
							if(logger.isDebugEnabled()) {
								logger.debug(" Got Mailing lists :"+ml_ids_str);
							}
					
						}else {
							SegmentRulesDao segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
							MailingListDao mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
							String segRuleIds = sentType.split(""+Constants.DELIMETER_COLON)[1];
							List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);
							
							if(segmenRules == null) {
								
								MessageUtil.setMessage("Configured segment no longer exists. You might have deleted it.", "color:red;");
								
								return ;
								
								
							}//if
							String tempQry = "";
							logger.debug("size ::"+segmenRules.size());
							for (SegmentRules segmentRules : segmenRules) {
								
								Set<MailingList> mlistSet = new HashSet<MailingList>();
								List<MailingList> mlList = mailingListDao.findByIds(segmentRules.getSegmentMlistIdsStr());
								if(mlList == null) {
									logger.debug("continue");
									continue;
								}
								
								mlistSet.addAll(mlList);
								long mlsbit = Utility.getMlsBit(mlistSet);
								
								//ClickHouse changes
								String qry =  "";
								if(!currentUser.isEnableClickHouseDBFlag())
									qry =  SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_NOTIFICATION, mlsbit);
								else
									qry =  ClickHouseSalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),false, Constants.SEGMENT_ON_NOTIFICATION, mlsbit);

								if(qry == null) {
									MessageUtil.setMessage("Selected invalid segmentation rules.", "color:red", "TOP");
									continue;
								}
								
								if(SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRules.getSegRule())) {
									
									//logger.debug("=====got true =====");
									String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRules.getSegRule());
									
									if(csCampIds != null ) {
										//logger.debug("=====got campIds =====");
										String crIDs = Constants.STRING_NILL;
										//CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
										List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRules.getUserId(), csCampIds);
										if(campList != null) {
											for (Object[] crArr : campList) {
												
												if(!crIDs.isEmpty()) crIDs += Constants.DELIMETER_COMMA;
												crIDs += ((Long)crArr[0]).longValue();
												
											}
										}
										
										qry = qry.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
									}
								}
								//qry = qry.replace("<MOBILEOPTIN>", !(notification.isEnableEntireList()) ? " AND c.mobile_opt_in=1" : "");
								logger.info(" Generated Query :"+qry);
								if(tempQry.length() > 0) tempQry += " UNION ";
								
								tempQry += qry;
							}//for
							int notificationCount = 0;
							if(!currentUser.isEnableClickHouseDBFlag())
								notificationCount = contactsDao.getSegmentedContactsCount(tempQry);
							else
								notificationCount = contactsDao.getSegmentedContactsCountFromCH(tempQry);
								
							if(notificationCount == 0) {
								MessageUtil.setMessage("Your segment returned 0 unique contacts of "+ totalCount + " available contacts." , "color:red", "TOP");
								return;
							}
						}//else
						
						
						NotificationSchedule notificationSchedule = new NotificationSchedule();
						notificationSchedule.setNotificationId(notification.getNotificationId());
						notificationSchedule.setUserId(currentUser.getUserId());
						notificationSchedule.setScheduledDate(Calendar.getInstance());
						notificationSchedule.setStatus((byte)0);
						notificationScheduleDaoForDML.saveOrUpdate(notificationSchedule);
						
						Messagebox.show("Notification will be sent in a moment.", "Information", Messagebox.OK, Messagebox.INFORMATION);
						logger.debug("sent now is submitted for the Notification campaign"+notification.getNotificationName());
						
						String status = getCampaignStatus(notification);
						Listitem  item = (Listitem)img.getParent().getParent().getParent();
						Listcell statusLbl = (Listcell)item.getChildren().get(3);
						statusLbl.setLabel(status == null || status.length() == 0 ? notification.getStatus() : status);
						
						redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
						notification.setStatus(status);
						notificationDaoForDML.saveOrUpdate(notification);
						
					}
				} catch (Exception e) {
					logger.error("Exception ::", e);
				}
				
				
			}else if(imgAttr.equalsIgnoreCase("Edit")){
				session.setAttribute("notificationCampaign", notification);
				session.setAttribute("fromPage", "campaign/NotificationWebList");
				String draftStatus = notification.getDraftStatus();
				if(draftStatus != null) {
					if(draftStatus.equalsIgnoreCase(Constants.SMS_CAMP_DRAFT_STATUS_STEP_COMPLETE)){
						session.setAttribute("editNotification", "edit");
						Redirect.goTo(PageListEnum.NOTIFICATION_WEB);
					}
					else{
						session.setAttribute("editNotification", "view");
						session.setAttribute("notificationDraftStatus", draftStatus);
						Redirect.goTo(PageListEnum.NOTIFICATION_WEB);
					}
				}
				
			}else if(imgAttr.equalsIgnoreCase("Copy")){
				
			}else if(imgAttr.equalsIgnoreCase("Delete")){
				
				List<Notification> clist = new ArrayList<Notification>();
				String res = "";
				String msg = "Are you sure you want to delete selected Notification campaign?";
				String status = notification.getStatus();
				if(status.equalsIgnoreCase(Constants.CAMP_STATUS_ACTIVE) || 
						status.equalsIgnoreCase(Constants.CAMP_STATUS_RUNNING)){
					msg = "Are you sure do you want to delete the '"+status.toLowerCase()+"' Notification campaign?";
				}
				try {
					int confirm = Messagebox.show(msg, "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == 1){
						clist.add(notification);
						res = deleteNotificationCampaigns(clist);
						if(res.equals("success")){
							MessageUtil.setMessage("Selected Notification campaign deleted successfully.","color:blue","TOP");
							notificationListlbId.removeItemAt(index);
						}
						if(res.equals("prolem")){
							MessageUtil.setMessage("Problem experienced while deleting the Notification campaign. Please try again later.","color:red","TOP");
						}
					}//if
				} catch (Exception e) {
					logger.error("Exception ::", e);
				}
			}//else if
			else if(imgAttr.equalsIgnoreCase("Report")) {
				try {
					 
					notification =  (Notification)((Listitem)img.getParent().getParent().getParent()).getValue();
					PageUtil.clearHeader();
					MessageUtil.clearMessage();
					Long userId = GetUser.getUserId();
					long reportCount = notificationCampaignReportDao.getReportCountByCampaign(notification.getNotificationName(), userId);
					if(reportCount < 1){
						MessageUtil.setMessage("No records exist for the Notification : "+notification.getNotificationName(),"color:red", "TOP");
					}else{
						session.setAttribute("notification",notification);
						session.setAttribute("notificationReport",true);
						Redirect.goTo(PageListEnum.NOTIFICATION_REPORT);
					}
				} catch (Exception e) {
				    logger.error("** Exception **" + e);
				}
				
				
			}//else if
		}//if
		else if (event.getTarget() instanceof Popup) {
			try {
				OpenEvent openEvent = (OpenEvent)event;
				Image img = (Image)openEvent.getReference();
				logger.info(img.getParent().getParent().getParent());
				logger.info("" +((Listitem)img.getParent().getParent().getParent()).getValue());
				notification =  (Notification)((Listitem)img.getParent().getParent().getParent()).getValue();
				
				if(currentUser!=null && currentUser.getSubscriptionEnable() && notification.getCategory() ==null){
					MessageUtil.setMessage("You have currently enabled subscriber preference center setting. " +
						"\n Please specify campaign category for this communication and try again.", "color:red;");
					return;
				}
				
				copyNotificationPopupId.setAttribute("notificationCampaign",notification);
				copyNotificationNameTbId.setValue("Copy of " + notification.getNotificationName());
			} catch (Exception e) {
			}
		}
		else if(event.getTarget() instanceof Paging) {
			
			Paging paging = (Paging)event.getTarget();
			
			int desiredPage = paging.getActivePage();
			
			if(paging.getAttribute("onPaging").equals("topPaging")) {
				
				this.campListPaging1.setActivePage(desiredPage);
				
			}else if(paging.getAttribute("onPaging").equals("bottomPaging")) {
				
				this.campListPaging.setActivePage(desiredPage);
				
			}//else if
			
			
			
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
			
			
		}//else if
		}catch (Exception e) {
			logger.error("** Exception ",e);
		}
	}//onevent()
	
	
	
	
	private String getCampaignStatus(Notification campaign) {
	    TimeZone clientTimeZone = (TimeZone)sessionScopeForTimeZone.getAttribute("clientTimeZone");
		String status = campaign.getStatus();
		
		List<NotificationSchedule> scheduleList = notificationScheduleDao.getByNotificationCampaignId(campaign.getNotificationId());
		
		
		if(scheduleList == null || scheduleList.size() == 0) {
					return status;
		}
		
		
		Calendar startCal =null;
		Calendar endCal = null;
		NotificationSchedule latestCampaignSchedule = null;
		
		
		for (NotificationSchedule notificationCampaignSchedule : scheduleList) {
				
			if(latestCampaignSchedule == null) latestCampaignSchedule = notificationCampaignSchedule;

			if(notificationCampaignSchedule.getScheduledDate().after(latestCampaignSchedule.getScheduledDate())){
				latestCampaignSchedule = notificationCampaignSchedule;
			}
				
				if((notificationCampaignSchedule.getStatus() != 0 )) {
					continue;
				}
				
				
				if(startCal== null && endCal== null){
					startCal = notificationCampaignSchedule.getScheduledDate();
					endCal = notificationCampaignSchedule.getScheduledDate();
					
					
				}
				
				
				if(endCal != null && endCal.before(notificationCampaignSchedule.getScheduledDate())){
			    	endCal = notificationCampaignSchedule.getScheduledDate();
			    }
				if(startCal.after(notificationCampaignSchedule.getScheduledDate())){
			    	startCal = notificationCampaignSchedule.getScheduledDate();
			    }
		}//for
		
		
		String scheduledOnOrBetweenDates = null;
		
		if (startCal != null && endCal != null) {
			if (!startCal.equals(endCal)) {
				campaign.setScheduledOccurrence("Recurring");
				scheduledOnOrBetweenDates = MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_STDATE,	clientTimeZone) + " - " + MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_STDATE, clientTimeZone);
				campaign.setScheduledDates(scheduledOnOrBetweenDates);

			} else {
				campaign.setScheduledOccurrence("One-Time");
				campaign.setScheduledDates(
						MyCalendar.calendarToString(startCal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			}
		}
			 if(latestCampaignSchedule.getStatus() == 0 || latestCampaignSchedule.getStatus() == 1 || latestCampaignSchedule.getStatus() == 2) 
				 return latestCampaignSchedule.getStatusStr();
			 else if(latestCampaignSchedule.getStatus() >= 3)
				 return "Schedule Failure";
			 else
				 return "Draft";
		 
	 }
	
	
	/**
	 * This method is to show the message if SPC is enabled and if the NotificationCampaign doesn't have category
	 * @param NotificationCampaign
	 * @return if SPC is enable and NotificationCampaign without category
	 * */
	
	
	@SuppressWarnings("unchecked")
	public void redraw(int startIndex, int size) {
		 
		try{
			
			MessageUtil.clearMessage();
			logger.debug("-- just entered redraw() --");
			int count =  notificationListlbId.getItemCount();
			
			for(; count>0; count--) {
				notificationListlbId.removeItemAt(count-1);
			}
			
			//System.gc();
			 
			List<Notification> campList = null;
			 
			String value = srchLbId.getSelectedItem().getValue().toString();
			List<Notification> finalList = new ArrayList<Notification>();
			if(value.equals(SEARCH_BY_NAME)) {
			    
				campList =  notificationDao.getCampaignsByCampaignName(searchByCmpgnNameTbId.getValue().trim(),currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
				if(campList != null) finalList = campList;
			}
			
			else if(value.equals(SEARCH_BY_DATE)) {
				
				campList =  notificationDao.getCampaignsBetweenCreationDates(fromDateStr, toDateStr,currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
				if(campList != null) finalList = campList;
			}
      
			else if(value.equals(SEARCH_BY_STATUS)) {
				String status = getSelectedStatus();
				if(!status.equals("All")) {
					int sizeTotal = notificationDao.getCount(currentUser.getUserId(), "All");
					campList =  notificationDao.getCampaignsByStatus(currentUser.getUserId(), 0, sizeTotal,orderby_colName,desc_Asc);
					List<Notification> allNotificationCampList=new ArrayList<Notification>();
					if(campList != null) {
						//finalList = campList;
							for (Notification campaigns : campList) {
								String campStatus = getCampaignStatus(campaigns);
								
								
									if(campStatus.equalsIgnoreCase(status) ){
										campaigns.setStatus(campStatus);
										allNotificationCampList.add(campaigns);
									}
															
								
							 
							}
							if(allNotificationCampList.size() > 0){
								 for(int i=startIndex;i<size+startIndex&&i<allNotificationCampList.size();i++)
								 {
									logger.info("setting :LLLLLLLL"+allNotificationCampList.get(i).getCategory());
									finalList.add(allNotificationCampList.get(i));
									
								 }
							}
					}
					campListPaging.setTotalSize(allNotificationCampList.size());
				    //campListPaging.setAttribute("onPaging", "topPaging");
					//campListPaging.addEventListener("onPaging", this); 
					campListPaging1.setTotalSize(allNotificationCampList.size());
				}//if
				else{
				
					List<Notification> tempList = notificationDao.getCampaignsByStatus(currentUser.getUserId(), startIndex, size,orderby_colName,desc_Asc);
					for (Notification campaigns : tempList) {
						String campStatus = getCampaignStatus(campaigns);
						campaigns.setStatus(campStatus);
						finalList.add(campaigns);
					}
				}
				
			}
			
			 TimeZone clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
			 if(finalList != null && finalList.size() > 0) {
				 
				 Listitem item;
				 Listcell lc;
				 for (Notification notification : finalList) {
					
					item = new Listitem();
					
					item.setValue(notification);
					item.setHeight("30px");
					
					lc = new Listcell();
					lc.setParent(item);
					
					lc = new Listcell(notification.getNotificationName());
					lc.setTooltiptext(notification.getNotificationName());
					lc.setParent(item);
					
					String listName = Constants.STRING_NILL;
					SegmentRulesDao segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
					if(notification.getListType() != null && !notification.getListType().equalsIgnoreCase("Total")){
						listName = "Segment of ("+notification.getListNames()+")";
						String segRuleIds = notification.getListType().split(""+Constants.DELIMETER_COLON)[1];
						List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);
						if(segmenRules!=null) listName = "Segment of ("+getListNames(notification)+")";
						else listName="--";
						
					}else if (notification.getListType() == null || notification.getListType().equalsIgnoreCase("Total")){
						listName = getListNames(notification);
						
					}
					
					lc = new Listcell(listName);
					lc.setTooltiptext(listName);
					lc.setParent(item);
					
					
					String status = getCampaignStatus(notification);
					
					
					 int index = notificationCampStatusLb.getSelectedIndex();
					 if(!(index <= 0))
					if(status.equalsIgnoreCase(getSelectedStatus()) && !getSelectedStatus().equalsIgnoreCase("All")){
						logger.info(getSelectedStatus());
					}
					
					if(notificationCampStatusLb.getSelectedItem() != null && 
							!notificationCampStatusLb.getSelectedItem().getLabel().equals("All") && !notificationCampStatusLb.getSelectedItem().getLabel().equals(status)) continue;
									
					lc = new Listcell(status);
					lc.setParent(item);
					
					
					//two new columns in 2.4.7 release one 'Schedule Occurrence'  and another 'Active Schedule On/Between' are added -- STARTS
					lc = new Listcell();
					lc.setLabel(notification.getScheduledOccurrence() != null ? notification.getScheduledOccurrence() : "--");
					lc.setParent(item);
					
					lc = new Listcell();
					lc.setLabel(notification.getScheduledDates() != null ? notification.getScheduledDates() : "--");
					lc.setParent(item);
					
					//two new columns in 2.4.7 release one 'Schedule Occurrence'  and another 'Active Schedule On/Between' are added -- ENDS
					
					lc = new Listcell();
					lc.setLabel(MyCalendar.calendarToString(notification.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
					lc.setParent(item);
					
					lc = new Listcell();
					lc.setLabel(MyCalendar.calendarToString(notification.getModifiedDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
					lc.setParent(item);
					lc = new Listcell();
					
					Hbox hbox = new Hbox();
					hbox.setAlign("center");
					hbox.setStyle("cursor:pointer;margin-right:5px;");
					
					Image img = new Image();
					img.setSrc("/img/email_page.gif");
					img.setTooltiptext("Send Test Notification");
					img.setStyle("cursor:pointer;margin-right:5px;");
					img.setAttribute("addEvent", "sendTestNotification");
					img.addEventListener("onClick", this);
					img.setParent(hbox);
					
					img = new Image();
					img.setSrc("/img/email_go.gif");
					img.setTooltiptext("Send Again");
					img.setStyle("cursor:pointer;margin-right:5px;");
					img.setAttribute("addEvent", "sendAgain");
					img.addEventListener("onClick", this);
					img.setParent(hbox);
					
					img = new Image();
					img.setSrc("/img/email_edit.gif");
					img.setTooltiptext("Edit");
					img.setStyle("cursor:pointer;margin-right:3px;");
					img.setAttribute("addEvent", "Edit");
					img.addEventListener("onClick", this);
					img.setParent(hbox);
					
					img = new Image();
					img.setSrc("/img/copy.gif");
					img.setTooltiptext("Copy");
					img.setStyle("cursor:pointer;");
					img.setPopup(copyNotificationPopupId);
					img.setAttribute("addEvent", "Copy");
					img.addEventListener("onClick", this);
					img.setParent(hbox);
					
					img = new Image();
					img.setSrc("/img/action_delete.gif");
					img.setTooltiptext("Delete");
					img.setStyle("cursor:pointer;");
					img.setAttribute("addEvent", "Delete");
					img.addEventListener("onClick", this);
					img.setParent(hbox);
					
					img = new Image();
					img.setSrc("/img/theme/home/reports_icon.png");
					img.setTooltiptext("Reports");
					img.setStyle("cursor:pointer;");
					img.setAttribute("addEvent", "Report");
					img.addEventListener("onClick", this);
					img.setParent(hbox);
					
					hbox.setParent(lc);
					lc.setParent(item);
					
					item.setHeight("30px");
					item.setParent(notificationListlbId);
				}//if
				
				copyNotificationPopupId.addEventListener("onOpen", this);	
					
				}
		}
		catch(Exception e){
			logger.error("Exception ", e);
		}
		

		 
	 }//redraw
	
	
 public String getListNames(Notification notification) {
		String listNames = "";
		
		Set<MailingList> mailingLists= notification.getMailingLists();
		if(mailingLists.size()>0) {
			for(MailingList ml : mailingLists) {
				
				if(listNames.length() > 0) {
					listNames +=","+ml.getListName();
				}else {
					listNames += ml.getListName();
				}
			} // for
		}else {
			
			listNames = "--";
		}
		return listNames;
	}

public String getSelectedStatus() {
		 int index = notificationCampStatusLb.getSelectedIndex();
			String status = "All";
			if (index != -1)
				status = notificationCampStatusLb.getSelectedItem().getLabel();
			return status;
	 }
	
	
	/**
	 * this method returns the list of Notification Campaigns based on the status selected
	 * @return List<Notification>
	 */
	
	public List<Notification> getNotificationCampaigns(int firstResult, int maxResult){
		logger.debug("-- Just Entered -- ");
		List<Notification> notificationCampList = null;
		String status = "All";
		notificationCampList = notificationDao.getNotificationCampaignsByStatus(userId, status, firstResult, maxResult );
		TimeZone clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
		
		for (Notification notifCampaign : notificationCampList) {
			
			try{
				notifCampaign.setModifiedDate(new MyCalendar(notifCampaign.getModifiedDate(),
						clientTimeZone,MyCalendar.FORMAT_DATETIME_STDATE));
				// need to append 'segmented' if this campaign associated mailing list is segmented
				if(notifCampaign.getListType() != null && !notifCampaign.getListType().equalsIgnoreCase("Total")) {
					logger.info("the campaign is segmented......"+notifCampaign.getListNames()+"(segmented)");
					notifCampaign.setListNames("Segment of("+getListNames(notifCampaign)+")");
					
				} else if (notifCampaign.getListType() == null || notifCampaign.getListType().equalsIgnoreCase("Total")) {
					notifCampaign.setListNames(getListNames(notifCampaign));
				}
				
			} catch (Exception e) {
				
				logger.error(" ** Exception : ",(Throwable)e);
			}
		} // for
		logger.debug("Notification Campaign list size :" + notificationCampList.size());
		return notificationCampList;
		
		
		
	}
	
	
	public void onSelect$notificationListlbId() {
		
		if(notificationListlbId.getSelectedCount() == 0){
			
			notificationActionsBandBoxId.setDisabled(true);
			notificationActionsBandBoxId.setButtonVisible(false);
		}else if(notificationListlbId.getSelectedCount() > 0) {
			
			notificationActionsBandBoxId.setDisabled(false);
			notificationActionsBandBoxId.setButtonVisible(true);
		}
		
		
		
	}//onSelect$campListlbId()
	
	
	
	
	/**
	 * this method sends the Test Notification
	 */

	public void onClick$sendTestNotificationBtnId$testNotificationWinId() {
		try {
			 List<String> notRegisterdNo = new ArrayList<String>();
			final String uri = "https://fcm.googleapis.com/fcm/send";
		    Notification notification = (Notification)testNotificationWinId.getAttribute("NotificationCampaign");
		    String imgUrl = PropertyUtil.getPropertyValue("ImageServerUrl");
		    if(notification!=null) {
		    Contacts contactForNotification = null;
		    String phoneNumbers = testNotificationWinId$mblNumTxtBoxId.getValue();
		 	String[] phoneNumber =   phoneNumbers.split(",");
		 	 for (String phNo :phoneNumber) {
				 contactForNotification = contactsDao.findContactByPhone(phNo, currentUser.getUserId());
				 	if(contactForNotification == null) {
				 		notRegisterdNo.add(phNo);
				 	}
				 	String json = null;
				    if(contactForNotification!=null) {
				    	Set<String> phSet = null;
					    if(contactForNotification.getPushNotification() && (contactForNotification.getInstanceId()!=null && !contactForNotification.getInstanceId().isEmpty()) && contactForNotification.getDeviceType()!=null) {
					    	phSet = Utility.getDRConTentPlaceHolder(notification.getNotificationContent());
					    	Map<String, Object> notiContent  = replacePlaceHoldersFromContact(contactForNotification, notification.getNotificationContent(), phSet, null, currentUser,null);
					    	phSet = Utility.getDRConTentPlaceHolder(notification.getHeader());
					    	Map<String, Object> notiheader  = replacePlaceHoldersFromContact(contactForNotification, notification.getHeader(), phSet, null, currentUser,null);
					    	PushNotificationIos pushNotificationIos = new PushNotificationIos();
					    	PushNotificationAndroid pushNotificationAndroid = new PushNotificationAndroid();
					    	if(contactForNotification.getDeviceType()!=null && contactForNotification.getDeviceType().equals("android")) {
							 	String[] registration_ids = {contactForNotification.getInstanceId()};
							 	pushNotificationAndroid.setRegistration_ids(registration_ids);
								PushNotificationData data = new PushNotificationData();
								data.setImage(USER_DATA_URL+currentUser.getUserName()+"/Notification/bannerImage/"+notification.getNotificationName()+"/"+notification.getBannerImageUrl());
								data.setTitle(notiheader.get("1").toString());
								data.setMutable_content(true);
								data.setMessage(notiContent.get("1").toString());
								data.setBody(notiContent.get("1").toString());
								data.setSummaryText(notiContent.get("1").toString());
								data.setVisibility(0);
								data.setNotId(getRandomNumberInts());
								data.setPriority(2);
								if(data.getImage()!=null && !data.getImage().isEmpty()) {
									data.setStyle("picture");
								}else {
									data.setStyle("inbox");
								}
								pushNotificationAndroid.setNotification(data);
								ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
								json = ow.writeValueAsString(pushNotificationAndroid);
					    	}else if(contactForNotification.getDeviceType()!=null && contactForNotification.getDeviceType().equals("ios")) {
					    		String[] registration_ids = {contactForNotification.getInstanceId()};
					    		pushNotificationIos.setRegistration_ids(registration_ids);
								IosNotification iosNotification = new IosNotification();
								iosNotification.setTitle(notiheader.get("1").toString());
								iosNotification.setBody(notiContent.get("1").toString());
								iosNotification.setSound("default");
								iosNotification.setMutable_content(true);
								PushNotificationData data =  new PushNotificationData();
								data.setNotId(getRandomNumberInts());
								String imagePath = USER_DATA_URL+currentUser.getUserName()+"/Notification/bannerImage/"+notification.getNotificationName()+"/"+notification.getBannerImageUrl();
								String extension = FilenameUtils.getExtension(imagePath);
								if(extension.equalsIgnoreCase("jpg")) {
									data.setImage_url_jpg(imagePath);
								}else if(extension.equalsIgnoreCase("png")) {
									data.setImage_url_png(imagePath);
								}else if(extension.equalsIgnoreCase("gif")) {
									data.setImage_url_gif(imagePath);
								}
								pushNotificationIos.setNotification(iosNotification);
								pushNotificationIos.setData(data);
								ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
								json = ow.writeValueAsString(pushNotificationIos);
								if(data.getImage_url_jpg()!=null && !data.getImage_url_jpg().isEmpty()) {
									json = json.replace("image_url_jpg", "image-url-jpg");
								}else if(data.getImage_url_png()!=null && !data.getImage_url_png().isEmpty()) {
									json = json.replace("image_url_png", "image-url-png");
								}else if(data.getImage_url_gif()!=null && !data.getImage_url_gif().isEmpty()) {
									json = json.replace("image_url_gif", "image-url-gif");
								}
					    	}
					    	else if(contactForNotification.getDeviceType()!=null && contactForNotification.getDeviceType().equals("web")) {
								try {
									String[] registration_ids = {contactForNotification.getInstanceId()};
									pushNotificationIos.setRegistration_ids(registration_ids);
									//used IOS pojo here as the json generated for IOS and WEB are same. 
									IosNotification webNotification = new IosNotification();
									webNotification.setTitle(notiheader.get("1").toString());
									webNotification.setBody(notiContent.get("1").toString());
									webNotification.setSound("default");
									webNotification.setMutable_content(true);
									String bannerimage = notification.getBannerImageUrl();
									if(bannerimage!=null && !bannerimage.isEmpty()) {
										bannerimage = USER_DATA_URL+currentUser.getUserName()+"/Notification/bannerImage/"+notification.getNotificationName()+"/"+notification.getBannerImageUrl();
										bannerimage = bannerimage.replace(IMAGES_URL, imgUrl);
									}else {
										bannerimage = "";
									}
									String logo=notification.getLogoImageUrl();
									if(logo!=null && !logo.isEmpty()) {
										logo=USER_DATA_URL+currentUser.getUserName()+"/Notification/logoImage/"+notification.getNotificationName()+"/"+notification.getLogoImageUrl();
										logo=logo.replace(IMAGES_URL, imgUrl);
									}
									webNotification.setIcon(logo);
									webNotification.setImage(bannerimage);
									pushNotificationIos.setNotification(webNotification);
									ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
									json = ow.writeValueAsString(pushNotificationIos);
									logger.info("web json"+json);
								}catch (Exception e) {
								}
							}
						    DefaultHttpClient httpClient = new DefaultHttpClient();
						    postToURL(uri, json, httpClient);
					    }
				    }
		 	 }
		 	 if(notRegisterdNo!=null && !notRegisterdNo.isEmpty()) {
				 StringBuffer sb = new StringBuffer();
				 	for (String notRegisterd : notRegisterdNo) {
				 		sb.append(notRegisterd);
				 		sb.append(",");
					}
				 	String finalMobileNo = null;
				 	if (sb.toString().endsWith(",")) {
				 		finalMobileNo = sb.substring(0, sb.length() - 1);
				 	}
			    	MessageUtil.setMessage("Not Registered Mobile No List "+ finalMobileNo +".","color:red","TOP");
			    	testNotificationWinId$mblNumTxtBoxId.setValue(Constants.STRING_NILL);
			    	testNotificationWinId$mblNumTxtBoxId.setValue(finalMobileNo);
			}else {
			 testNotificationWinId$mblNumTxtBoxId.setValue(Constants.STRING_NILL);
			 testNotificationWinId$mblNumTxtBoxId.setPlaceholder("Enter Mobile Number(s)...");
			 MessageUtil.setMessage("Test notification sent successfully !","color:blue","TOP");
			 testNotificationWinId.setVisible(false);
			}
		  }
	}catch (Exception e) {
		logger.error("Exception ::"+e);
	}
	}
	
	public Map<String, Object> replacePlaceHoldersFromContact(Contacts contact, String tempHtmlContent,Set<String> totalPhSet, StringBuffer phKeyValueSb, Users users, Map<String, String> udfDtatTypeMap) throws Exception {
		tempHtmlContent = tempHtmlContent.replace("|^", "[").replace("^|", "]");
		String value="";
		String preStr = "";

		if(phKeyValueSb == null) phKeyValueSb = new StringBuffer();
		Map<String, Object> phContentValMap= new HashMap<String, Object>();
		Iterator<String> phSetItr = totalPhSet.iterator();

		while (phSetItr.hasNext()) {
			String cfStr = (String) phSetItr.next();

			preStr = cfStr;
			if(cfStr.startsWith("GEN_")) {

				cfStr = cfStr.substring(4);
				String defVal="";
				int defIndex = cfStr.indexOf('=');

				if(defIndex != -1) {

					defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
					cfStr = cfStr.substring(0,cfStr.indexOf("/")).trim();
				} // if

				
				logger.info("cfStr >>>>>>>>>>>> "+cfStr);
				if(cfStr.equalsIgnoreCase("emailId")) {
					value = (contact.getEmailId() ==null) ?defVal : contact.getEmailId() ;
				}
				else if(cfStr.equalsIgnoreCase("email")) {
					value = (contact.getEmailId() ==null) ? defVal :contact.getEmailId()  ;
				}
				else if(cfStr.equalsIgnoreCase("firstName")) {
					value = (contact.getFirstName() ==null) ?defVal : contact.getFirstName();
				}
				else if(cfStr.equalsIgnoreCase("lastName"))	{
					value = (contact.getLastName() ==null) ?defVal : contact.getLastName()  ;
				}
				else if(cfStr.equalsIgnoreCase("addressOne")) {
					value = (contact.getAddressOne() ==null) ?defVal :contact.getAddressOne() ;
				}
				else if(cfStr.equalsIgnoreCase("addressTwo")) {
					value = (contact.getAddressTwo() ==null) ?defVal : contact.getAddressTwo() ;
				}
				else if(cfStr.equalsIgnoreCase("city"))	{
					value = (contact.getCity() ==null) ?defVal : contact.getCity()  ;
				}
				else if(cfStr.equalsIgnoreCase("state")) {
					value = (contact.getState() ==null) ?defVal : contact.getState() ;
				}
				else if(cfStr.equalsIgnoreCase("country")) {
					value = (contact.getCountry() ==null )?defVal :contact.getCountry() ;
				}
				else if(cfStr.equalsIgnoreCase("pin"))	{
					value = contact.getZip() ==null ?defVal : contact.getZip()  ;
				}
				else if(cfStr.equalsIgnoreCase("phone")) {
					value = contact.getMobilePhone() != null && contact.getMobilePhone().length() != 0 ? contact.getMobilePhone() : Constants.STRING_NILL;
					if(value.isEmpty()) value=defVal;
				}
				else if(cfStr.equalsIgnoreCase("gender")) {
					value = contact.getGender() == null ? defVal : contact.getGender();
				}
				else if(cfStr.equalsIgnoreCase("birthday") ) {					
					if( contact.getBirthDay() != null) {	
						value = MyCalendar.calendarToString(contact.getBirthDay(), MyCalendar.FORMAT_DATEONLY_GENERAL);
					}else{
						if(defVal.isEmpty());
						value=defVal;
					}
				}
				else if(cfStr.equalsIgnoreCase("anniversary") ) {
					if(contact.getAnniversary() != null) {
						value = MyCalendar.calendarToString(contact.getAnniversary(), MyCalendar.FORMAT_DATEONLY_GENERAL);
					}
					else{
						if(defVal.isEmpty()) ;
						value=defVal;
					}
				}
				else if(cfStr.equalsIgnoreCase("createdDate") ) {
					if(contact.getCreatedDate() != null) {
						value = MyCalendar.calendarToString(contact.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);
					}
					else{
						if(defVal.isEmpty()) ;
						value=defVal;
					}
				}
				else if(cfStr.equalsIgnoreCase("organizationName") ) {

					value = getUserOrganization(users, defVal);
				}

				else if(cfStr.toLowerCase().startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LOYALTY)) {

					value = getLoyaltyPlaceholders(contact,cfStr,defVal);

				}

				else if(cfStr.startsWith(PlaceHolders.CAMPAIGN_PH_STARTSWITH_LASETPURCHASE)) {
					value = getlastpurchasePlaceHolders(contact,cfStr,users,defVal);
				}

				else {
					value = "";
				}

				//logger.debug(">>>>>>>>> Gen token <<<<<<<<<<< :" + cfStr + " - Value :" + value);
				try {

					if(value != null && value.trim().length() > 0) {

						//cfStr = cfStr.toLowerCase();
						tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);

					} else {
						value = defVal;
						tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);

					}

				} catch (Exception e) {
					//logger.error("Exception while adding the General Fields as place holders ", e);
				}

				if(phKeyValueSb.length() > 0) phKeyValueSb.append(Constants.ADDR_COL_DELIMETER);

				phKeyValueSb.append("[" + preStr + "]" + Constants.DELIMETER_DOUBLECOLON + value);

				phSetItr.remove();

			} 
			else if(cfStr.startsWith("UDF_")) {

				cfStr = cfStr.substring(4);
				String defVal="";
				int defIndex = cfStr.indexOf('=');

				if(defIndex != -1) {
					defVal = (cfStr.length() == defIndex+1 )  ?  Constants.STRING_NILL : cfStr.substring(defIndex+1);
					cfStr = cfStr.substring(0,cfStr.indexOf("/")).trim();
					/*defVal = cfStr.substring(defIndex+1);
							cfStr = cfStr.substring(0,defIndex);*/
				} // if

				//		logger.info(">>>>> udfDataType Map ::"+udfDtatTypeMap);
				if(!udfDtatTypeMap.containsKey(cfStr)) continue;

				try {
					//value = getConatctCustFields(contact, idx,udfDataTypeStr,defVal);
					if(value==null || value.isEmpty()) value=defVal;
				} catch (Exception e) {
					logger.error("Exception ::" , e);
				}

				if(value != null && value.trim().length() > 0) 
					tempHtmlContent = tempHtmlContent.replace("["+preStr+"]", value);
				else {
					value =defVal;
					tempHtmlContent = tempHtmlContent.replace("["+preStr+"]",value);
				}

				if(phKeyValueSb.length() > 0) phKeyValueSb.append(Constants.ADDR_COL_DELIMETER);

				phKeyValueSb.append("[" + preStr + "]" + Constants.DELIMETER_DOUBLECOLON + value);

				phSetItr.remove();
			}
		}
		//logger.debug(">>>>>>>>>>>>>>>>"+tempHtmlContent);
		phContentValMap.put("1", tempHtmlContent);	
		return phContentValMap;
	}
	
	private String getlastpurchasePlaceHolders(Contacts contact, String placeholder,  Users users,String defVal ) throws Exception {
		String value = "";
		logger.debug("placeholders***************************"+placeholder);
		if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_STOREADDRESS)) {
			////logger.debug("orgid"+orgId);
			if(users.getUserOrganization().getUserOrgId() != null) {
				////logger.debug("last purchase store address >>>>>>>>>>>>>>>>>>>>>.");
				value = getLastPurchaseStoreAddr(contact, users,defVal);
				logger.debug("value 1***************************"+value);
			}

		}else if(placeholder.equals(PlaceHolders.CAMPAIGN_PH_LASTPURCHASE_DATE)) {
			////logger.debug("last purchase date>>>>>>>>>>>>>>>>>>");
			value = getContactLastPurchasedDate(contact,defVal);
			logger.debug("value 2***************************"+value);
		}

		return value;

	}
	
	private String getLastPurchaseStoreAddr(Contacts contact, Users users,String defVal) throws Exception {
		//need to get the contact last puchased store address
		RetailProSalesDao retailProSalesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName("retailProSalesDao");
		OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName("organizationStoresDao");



		String storeAddress =  null;
		Long orgId= users.getUserOrganization().getUserOrgId();
		if(contact.getContactId() != null) {

			String storeNum = retailProSalesDao.findLastpurchasedStore(contact.getContactId(),contact.getUsers().getUserId());
			if(storeNum != null) {
				OrganizationStores organizationStores = organizationStoresDao.findByStoreLocationId(orgId, storeNum);

				if(organizationStores == null) {
					storeAddress=defVal;


				}else {
					if(storeAddress == null) storeAddress = Constants.STRING_NILL;
					String strAddr[] = organizationStores.getAddressStr().split(Constants.ADDR_COL_DELIMETER);
					int count = 0;
					for(String str : strAddr){
						count++;

						if(count == 7 && storeAddress.length()>0 && str.trim().length()>0){
							storeAddress = storeAddress+" | Phone: "+str;
						}
						else if(storeAddress.length()==0 && str.trim().length()>0){
							storeAddress = storeAddress+str;
						}
						else if(storeAddress.length()>0 && str.trim().length()>0){
							storeAddress = storeAddress+", "+str;
						}
					}

				}
			}
			else {
				storeAddress= defVal;
			}
		}

		return storeAddress != null && !storeAddress.trim().isEmpty() ? storeAddress : defVal;
	}
	
	
	private String getContactLastPurchasedDate(Contacts contact,String defVal) throws Exception {

		String date = defVal;
		RetailProSalesDao retailProSalesDao = (RetailProSalesDao)ServiceLocator.getInstance().getDAOByName("retailProSalesDao");
		if(contact.getContactId() != null) {
			Calendar Mycalender = retailProSalesDao.findLastpurchasedDate(contact.getContactId(),contact.getUsers().getUserId());
			if(Mycalender != null){
				date = MyCalendar.calendarToString(Mycalender, MyCalendar.FORMAT_DATEONLY_GENERAL);
			}
			else {
				date=defVal;
			}
		}

		return date== null || date.trim().isEmpty() ? defVal : date;
	}
	
	private String getUserOrganization(Users users, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getUserOrganization");
		String organizationName = defVal;
		try{
			organizationName = users.getUserOrganization().getOrganizationName();
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed getUserOrganization ");
		return organizationName;
	
	}
	
	private String getLoyaltyPlaceholders(Contacts contact, String placeholder,String defVal) {
		try {

			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			String loyaltyPlaceholder = defVal;
			if(contact.getContactId() != null  ){ 

				ContactsLoyalty contactsLoyalty = contactsLoyaltyDao.findByContactId(contact.getUsers().getUserId(), contact.getContactId());
				if(contactsLoyalty != null){
					if(OCConstants.LOYALTY_MEMBERSHIP_STATUS_CLOSED.equalsIgnoreCase(contactsLoyalty.getMembershipStatus()) && contactsLoyalty.getTransferedTo() != null){
						
						contactsLoyalty = contactsLoyaltyDao.findAllByLoyaltyId(contactsLoyalty.getTransferedTo());
					}
					loyaltyPlaceholder =  replaceLoyaltyPlaceHolders(placeholder,contactsLoyalty,defVal);
					logger.info("Replaced Loyalty Place Holder :: "+loyaltyPlaceholder);
				}//if contacts loyalty not equal null
			}
			return loyaltyPlaceholder != null && !loyaltyPlaceholder.trim().isEmpty() ? loyaltyPlaceholder : defVal;
		} catch (Exception e) {
			logger.error("Contacts loyalty place holders: "+e);
			return null;
		}
	}

	public static int getRandomNumberInts(){
	    Random r = new Random( System.currentTimeMillis() );
	    return 10000 + r.nextInt(20000);
	}

	 private static String postToURL(String url, String message, DefaultHttpClient httpClient) throws IOException, IllegalStateException, UnsupportedEncodingException, RuntimeException {
		 StringBuffer totalOutput = new StringBuffer();
		 try {
		 	HttpPost postRequest = new HttpPost(url);
	        StringEntity input = new StringEntity(message,"UTF-8");
	        input.setContentType("application/json; charset=utf-8");
	        postRequest.setEntity(input);
	        postRequest.setHeader("Authorization", optcultureFireBaseServerKey);
	 
	        HttpResponse response = httpClient.execute(postRequest);
	 
	        if (response.getStatusLine().getStatusCode() != 200) {
	        	logger.error("Failed : HTTP error code : "+ response.getStatusLine().getStatusCode());
	        }
	 
	        BufferedReader br = new BufferedReader(
	                new InputStreamReader((response.getEntity().getContent())));
	 
	        String output;
	        while ((output = br.readLine()) != null) {
	            totalOutput.append(output);
	        }
	       }catch (RuntimeException e) {
				 logger.error("Failed : HTTP error code : "+ e);
			}catch (Exception e) {
				 logger.error("Exception while sending test notification "+ e);
			}
	        return totalOutput.toString();
	    }
	
	/**
	 * this method allows to copy the selected Notification Campaign
	 */
	public void onClick$notificationCampCopyBtnId(){
		try{
			MessageUtil.clearMessage();
			Notification notification = (Notification)copyNotificationPopupId.getAttribute("notificationCampaign");
			
			String copyNotificationCampName = copyNotificationNameTbId.getValue();
			if(notification == null){
				logger.debug("selected Notification Campaign is null");
				return;
			}
			if(copyNotificationCampName == null || !(Utility.condense(copyNotificationCampName).length()>0)){
				Messagebox.show("Provide Notification campaign name. Name cannot be left empty.","Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}
			if(!Utility.validateName(copyNotificationCampName)) {
				Messagebox.show("Provide valid Notification campaign name. Name should not contain any special characters.",
						"Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}
			
			if(notificationDao.checkName(copyNotificationCampName, userId)){
				Messagebox.show("Notification campaign name already exists. Please provide another name.","Information",Messagebox.OK,Messagebox.ERROR);
				return;
			}
			
			logger.debug("Notification Campaign is======>"+notification.getNotificationName());
			
			Notification newCampaign = notification.getCopy();
			String oldName = notification.getNotificationName();
			newCampaign.setNotificationName(copyNotificationCampName);
			notificationDaoForDML.saveOrUpdate(newCampaign);
			
			MultiLineMessageBox.doSetTemplate();
			MultiLineMessageBox.show("Copy of '" + oldName + "' Notification is created with '" + copyNotificationCampName 
					+ "' name. \n You have to reschedule the Notification by clicking on edit Notification.", 
					"Information", Messagebox.OK, Messagebox.INFORMATION);
			
			try {
				if(self.getParent() instanceof Include) {
					Include currentInclude = (Include)self.getParent();
					currentInclude.setSrc("/zul/Empty.zul");
					currentInclude.setSrc("/zul/contact/notificationWebList.zul");
				}
			} catch (Exception e) {
				logger.error("** Exception : ",e);
			}
			
			
			
		}catch (Exception e) {
			logger.debug("**Exception while preparing the copy of the Notification Campaign",e);
		}
	}
	
	/**
	 * this method deletes the multiple Selected Notification Campaigns 
	 * @param notificationListlbId
	 */
	public void onClick$delSelectedId()  {
		
		int count = notificationListlbId.getSelectedCount();
		if(logger.isDebugEnabled()) 
			logger.debug("Number of selected campaigns to delete :" + count);
		if(count == 0) {
			Messagebox.show("Select the Notification to delete.", "Information" , 
					Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		String msg = "Are you sure, you want to delete the selected campaign?";
		boolean found = false;
		Set<Listitem> selList = notificationListlbId.getSelectedItems();
		Notification notificationCampaign;
		
		/****make a list of Notification Campaigns to be get deleted********/
		List<Notification> campaignsList = new ArrayList<Notification>();
		
		for (Listitem li : selList) {
			notificationCampaign = (Notification)li.getValue();
			campaignsList.add(notificationCampaign);
			if(notificationCampaign.getStatus().equals(Constants.CAMP_STATUS_ACTIVE) || 
					notificationCampaign.getStatus().equals(Constants.CAMP_STATUS_RUNNING) ) {
				found = true;
			}
			
		}
		if(found) {
			msg = "Some of the selected campaigns are 'Active/Running', do you want to continue?";
		}
		try {
			int confirm=Messagebox.show(msg, "Delete Confirmation",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			 
			/******send the selected list for actual deletion ********/
			
			if(confirm== Messagebox.OK){
			
			if(deleteNotificationCampaigns(campaignsList).equals("success")) {
				
				MessageUtil.setMessage("Selected Notification campaigns have been deleted successfully. ", "color:green;", "top");
				Include xcontents = Utility.getXcontents();
				xcontents.invalidate();
				
			}//if
			
          }//confirm if
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
		notificationListlbId.clearSelection();
	}
	
	
	 private Listbox pageSizeLbId;
	 public void onSelect$pageSizeLbId() {
		 
		 try {
				logger.debug("Just enter here...");
				
				if(notificationListlbId.getItemCount() == 0 ) {
					
					logger.debug("No Notification Campaigns found for this user...");
					return;
				}
				changeRows(pageSizeLbId.getSelectedItem().getLabel(),campListPaging);
			} catch (Exception e) {
				logger.error("Exception :: errorr while getting from the changeRows method",e);
			}
		 
		 
		 
		 
	 }
	
	
	 public void changeRows(String selStr, Paging campListPaging) throws Exception {
			try {
				if(campListPaging!=null){
					int pNo = Integer.parseInt(selStr);
					campListPaging.setPageSize(pNo);
					campListPaging1.setPageSize(pNo);
					redraw(0,pNo);
				}
			} catch (WrongValueException e) {
				logger.error("Exception while getting the contacts...",e);
				
			} catch (NumberFormatException e) {
				logger.error("Exception while gettinf the contacts...",e);
			}
		}
	 
	/**
	 * this method deletes the selected Notification campaign(s) from DB
	 * @param NotificationCampList
	 * @return String var to specify the success or failure of deletion
	 */
	public String deleteNotificationCampaigns(List<Notification> notificationCampList){
		logger.debug("----just entered for deletion----");
		try{
			String notificationCampaignIds = "";
			Map<String,String> notificationCampMap = new HashMap<String, String>();
			for (Notification notification : notificationCampList) {
				notificationCampaignIds = notificationCampaignIds + (notificationCampaignIds.length() > 0 ? "," : "")+notification.getNotificationId();
				notificationCampMap.put(""+notification.getNotificationId(), notification.getNotificationName());
				
			}
			
			
			String idsArray[] = notificationCampaignIds.split(",");
			String delIdStr = "";//will be hold the campaigns that to be deleted
			for (String id : idsArray) {
					delIdStr += delIdStr.equals("")?id: "," + id; 
			}
			
			
			if(delIdStr.length()>0) {
				
				//need to delete the ids of notDelList from delIdStr
				
				logger.debug("-----about to delete the campaign ids=====>"+delIdStr);
				
				
				notificationDaoForDML.deleteByCampaignIdFromIntermediateTable(delIdStr);
				notificationDaoForDML.deleteByNotificationCampaignId(delIdStr);
				notificationScheduleDaoForDML.deleteByCampaignId(delIdStr);
				
				
			} //if
			return "success";
			
			
		}catch (Exception e) {
			logger.error("** Exception while deleting the Notification campains ",e);
			return "problem";
		}
	}
	
	
	
	
	
	public void onClick$cancelsendTestNotificationBtnId$testNotificationWinId() {
		
		logger.info("-----just entered-----to cancel");
		((Label)testNotificationWinId.getFellow("msgLblId")).setValue("");
		 ((Textbox)testNotificationWinId.getFellow("mblNumTxtBoxId")).setValue("");
		testNotificationWinId.setVisible(false);
		
	}
	
	public void onSelect$srchLbId() {
		String value = srchLbId.getSelectedItem().getValue();
		if(value.equals(SEARCH_BY_NAME)) {
			searchByCmpgnNameDivId.setVisible(true);
			searchByCmpgnNameTbId.setText(Constants.STRING_NILL);
			searchByCmpgnNameTbId.setFocus(true);
			
			searchByCmpgnCreationDateDivId.setVisible(false);
			searchByCmpgnStatusDivId.setVisible(false);
			notificationCampStatusLb.setSelectedIndex(0);
			return;
		}
		else
		if(value.equals(SEARCH_BY_DATE)) {
			searchByCmpgnNameDivId.setVisible(false);
			searchByCmpgnCreationDateDivId.setVisible(true);
			fromDateboxId.setText(Constants.STRING_NILL);
			toDateboxId.setText(Constants.STRING_NILL);
			
			
			searchByCmpgnStatusDivId.setVisible(false);
			notificationCampStatusLb.setSelectedIndex(0);
			return;
		}
		else
		if(value.equals(SEARCH_BY_STATUS)) {
			searchByCmpgnNameDivId.setVisible(false);
			searchByCmpgnCreationDateDivId.setVisible(false);
			searchByCmpgnStatusDivId.setVisible(true);
			notificationCampStatusLb.setSelectedIndex(0);
			return;
		}
		
	}//onSelect$srchLbId()
	
	public void onClick$filterBtnId() {
		notificationActionsBandBoxId.setDisabled(true);
    	int pSize = 0;
    	
    	String value = srchLbId.getSelectedItem().getValue().toString();
    	if(value.equals(SEARCH_BY_NAME)) {
   	 	     boolean status = validateSetCampaignName();
   	 	     if(status == false) return;
   	 	     pSize = notificationDao.getCountByCampaignName(searchByCmpgnNameTbId.getValue().trim(), currentUser.getUserId());
			 logger.info("----onClick$submintBtnId()----name----list size="+totalSize);
	    }
    	
    	else  if(value.equals(SEARCH_BY_DATE)) {
			 boolean status = validateSetCreationDate();
			 if(status == false) return;
			 pSize = notificationDao.getCountByCreationDate(fromDateStr, toDateStr, currentUser.getUserId());
			 logger.info("----onClick$submintBtnId()----Creation Date----list size="+pSize);
	    }
       
    	else  if(value.equals(SEARCH_BY_STATUS)) {
    		  pSize = notificationDao.getCount(currentUser.getUserId(), notificationCampStatusLb.getSelectedItem().getLabel());
			
			 logger.info("----onClick$submintBtnId()----Status is----list size="+pSize);
	    }
    	
        logger.info("----filter button pressed and totalSize = "+totalSize);
        
		setSizeOfPageAndCallRedraw(pSize);
       
	}//onClick$filterBtnId()
	
	
    public String orderby_colName="modifiedDate",desc_Asc="desc"; 
    
    public void desc2ascasc2desc()
    {
    	if(desc_Asc=="desc")
			desc_Asc="asc";
		else
			desc_Asc="desc";
	
    }
    public void onClick$sortbyCampaigName() {
    			//campListPaging.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
    			orderby_colName = "notificationName";
    			desc2ascasc2desc();	
    			redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
    }
    public void onClick$sortbyCreadteDate() {
		//campListPaging.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		orderby_colName = "createdDate";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
}
    public void onClick$sortbyModifiedDate() {
		//campListPaging.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		orderby_colName = "modifiedDate";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
}
	
	
	private boolean validateSetCampaignName() {
		if(searchByCmpgnNameTbId.getValue().trim().isEmpty()) {
			MessageUtil.setMessage("Please enter Campaign Name.",
					"color:red", "TOP");
			searchByCmpgnNameTbId.setFocus(true);
			return false;
		}
		return true;
	}//validateSetCampaignName()
	
    private boolean validateSetCreationDate() {
		
		if(fromDateboxId.getValue() == null || toDateboxId.getValue() == null ){
			MessageUtil.setMessage("Please enter the required dates.",
					"color:red", "TOP");
			
			
			return false;
		}
		
		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		Calendar serverToDateCal = toDateboxId.getServerValue();

		Calendar tempClientFromCal = fromDateboxId.getClientValue();
		Calendar tempClientToCal = toDateboxId.getClientValue();
		
		logger.debug("client From :" + tempClientFromCal + ", client To :"
				+ tempClientToCal);

		// change the time for startDate and endDate in order to consider right
		// from the
		// starting time of startDate to ending time of endDate
		serverFromDateCal.set(
				Calendar.HOUR_OF_DAY,
				serverFromDateCal.get(Calendar.HOUR_OF_DAY)
						- tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(
				Calendar.MINUTE,
				serverFromDateCal.get(Calendar.MINUTE)
						- tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);

		serverToDateCal.set(Calendar.HOUR_OF_DAY,
				23 + serverToDateCal.get(Calendar.HOUR_OF_DAY)
						- tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(
				Calendar.MINUTE,
				59 + serverToDateCal.get(Calendar.MINUTE)
						- tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);

		if (serverToDateCal.compareTo(serverFromDateCal) < 0) {
			MessageUtil.setMessage("'To' date must be later than 'From' date.",
					"color:red", "TOP");
			return false;
		}
		
		fromDateStr = serverFromDateCal.toString();
		toDateStr = serverToDateCal.toString();
		
		
		return true;
		
	}//validateSetCreationDate()
    
     private void setSizeOfPageAndCallRedraw(int pSize) {
    	
    	logger.debug("page size is    "+pSize);
    	campListPaging.setTotalSize(pSize);
	    //campListPaging.setAttribute("onPaging", "topPaging");
		//campListPaging.addEventListener("onPaging", this); 
		 
		campListPaging.setActivePage(0);
		campListPaging1.setActivePage(0);
		campListPaging1.setTotalSize(pSize);
		//campListPaging1.setAttribute("onPaging", "bottomPaging");
		//campListPaging1.addEventListener("onPaging", this);

		redraw(0, Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		//copyEmailPopupId.addEventListener("onOpen", this);	
    }
     
     public void onClick$resetAnchId() {
 		
     	int pSize;
     	notificationActionsBandBoxId.setDisabled(true);
     	srchLbId.setSelectedIndex(0);
     	notificationCampStatusLb.setSelectedIndex(0);
    
     	
     	searchByCmpgnNameDivId.setVisible(false);
 		searchByCmpgnCreationDateDivId.setVisible(false);
 		searchByCmpgnStatusDivId.setVisible(true);
     	
 		pSize = notificationDao.getCount(currentUser.getUserId(), "All");
 		logger.info("----onClick$resetAnchId()----Status is----list size="+totalSize);
 		
 		orderby_colName="modifiedDate";
		desc_Asc="desc";
		pageSizeLbId.setSelectedIndex(1);
 		setSizeOfPageAndCallRedraw(pSize);
     	
 		
 	}
    
     private String replaceLoyaltyPlaceHolders(String placeholder,ContactsLoyalty contactsLoyalty,String defVal) {

 		if(contactsLoyalty == null){
 			return defVal;
 		}
 		if(placeholder == null  && defVal == null){
 			logger.error("Value is null placeholder :: "+placeholder+"\t contactsLoyalty "+contactsLoyalty+"\t defVal"+defVal);
 			return defVal;
 		}
 		
 		logger.info("In replaceLoyaltyPlaceHolders :: " +  placeholder);
 		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
 		String loyaltyPlaceholder="";
 		//OC LOYALTY_MEMBERSHIP_PIN
 		if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PIN.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin(): defVal;
 			logger.info("Membership Pin ::"+contactsLoyalty.getCardPin());
 		}
 		//SB LOYALTYCARDPIN
 		if(PlaceHolders.CAMPAIGN_PH_LOYALTYCARDPIN.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getCardPin()!= null ? contactsLoyalty.getCardPin(): defVal;
 			logger.info("Membership Pin ::"+contactsLoyalty.getCardPin());
 		}
 		//REFRESHEDON
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REFRESHEDON.equalsIgnoreCase(placeholder) ){
 			loyaltyPlaceholder = contactsLoyalty.getLastFechedDate() ==  null ? defVal :  MyCalendar.calendarToString(contactsLoyalty.getLastFechedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL);
 		}
 		//OC MEMBERSHIP_NUMBER
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_NUMBER.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber()+"":defVal;
 			logger.info("Membership Number ::"+contactsLoyalty.getCardNumber());
 		}
 		//SB LOYALTY_CARDNUMBER
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_CARDNUMBER.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? contactsLoyalty.getCardNumber()+"":defVal;
 			logger.info("LOYALTY_CARDNUMBER ::"+contactsLoyalty.getCardNumber());
 		}
 		//MEMBER_TIER
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBER_TIER.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getProgramTierId() != null ? getMemberTier(contactsLoyalty.getProgramTierId() , defVal) : defVal; 
 			logger.info("Member Tier ::"+loyaltyPlaceholder);
 		}
 		//MEMBER_STATUS
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBER_STATUS.equalsIgnoreCase(placeholder)){
 			logger.info("MEMBER_STATUS contactsLoyalty.getMembershipStatus() ::"+contactsLoyalty.getMembershipStatus());
 			loyaltyPlaceholder = contactsLoyalty.getMembershipStatus() != null ? contactsLoyalty.getMembershipStatus() : defVal;
 			logger.info("MEMBER_STATUS ::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_ENROLLMENT_DATE
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_DATE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getCreatedDate() != null ?  MyCalendar.calendarToString(contactsLoyalty.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR) : defVal ;
 			logger.info("LOYALTY_ENROLLMENT_DATE ::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_ENROLLMENT_SOURCE
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_SOURCE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getContactLoyaltyType() != null ? getEnrollmentSource(contactsLoyalty.getContactLoyaltyType() , defVal) : defVal;
 			logger.info("LOYALTY_ENROLLMENT_SOURCE ::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_ENROLLMENT_STORE
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_ENROLLMENT_STORE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getPosStoreLocationId() != null ? contactsLoyalty.getPosStoreLocationId()+"":defVal;
 			logger.info("LOYALTY_ENROLLMENT_STORE ::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_REGISTERED_PHONE
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REGISTERED_PHONE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getMobilePhone() != null ? contactsLoyalty.getMobilePhone():defVal;
 			logger.info("LOYALTY_REGISTERED_PHONE ::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_POINTS_BALANCE
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_POINTS_BALANCE.equalsIgnoreCase(placeholder)) {
 			loyaltyPlaceholder = contactsLoyalty.getLoyaltyBalance() != null ? contactsLoyalty.getLoyaltyBalance().longValue()+" Points" : defVal;
 			logger.info("LOYALTY_POINTS_BALANCE ::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_MEMBERSHIP_CURRENCY_BALANCE
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_CURRENCY_BALANCE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getGiftcardBalance() != null ?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) : defVal;
 			logger.info("LOYALTY_MEMBERSHIP_CURRENCY_BALANCE ::"+loyaltyPlaceholder);
 		}
 		//GIFTCARD_BALANCE
 		else if(PlaceHolders.CAMPAIGN_PH_GIFTCARD_BALANCE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getGiftcardBalance() != null ?  decimalFormat.format(contactsLoyalty.getGiftcardBalance()) : defVal;
 			logger.info("GIFTCARD_BALANCE ::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_GIFT_BALANCE
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_BALANCE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getGiftBalance() != null ? decimalFormat.format(contactsLoyalty.getGiftBalance()) : defVal;
 			logger.info("LOYALTY_GIFT_BALANCE ::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_GIFT_CARD_EXPIRATION
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_CARD_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = getGiftCardExpirationDate(contactsLoyalty ,defVal);
 			logger.info("LOYALTY_GIFT_CARD_EXPIRATION ::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_HOLD_BALANCE
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_HOLD_BALANCE.equalsIgnoreCase(placeholder)) {
 			loyaltyPlaceholder = getHoldBalance(contactsLoyalty,defVal);
 			logger.info("LOYALTY_HOLD_BALANCE ::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_REWARD_ACTIVATION_PERIOD
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REWARD_ACTIVATION_PERIOD.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getProgramTierId() != null ? getRewardActivationPeriod(contactsLoyalty.getProgramTierId(),defVal) : defVal ;
 			logger.info("LOYALTY_REWARD_ACTIVATION_PERIOD::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_LAST_EARNED_VALUE
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_EARNED_VALUE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastEarnedValue(contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE,defVal,contactsLoyalty.getUserId()) : defVal;
 			logger.info("LOYALTY_LAST_EARNED_VALUE::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_LAST_REDEEMED_VALUE
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_REDEEMED_VALUE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastRedeemedValue(contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION,defVal,contactsLoyalty.getUserId()) : defVal;
 			logger.info("LOYALTY_LAST_REDEEMED_VALUE::"+loyaltyPlaceholder);
 		}
 		else if(PlaceHolders. CAMPAIGN_PH_LOYALTY_MEMBERSHIP_PASSWORD.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = getMembershipPassword(contactsLoyalty,defVal);
 			logger.info("LOYALTY_MEMBERSHIP_PASSWORD::"+loyaltyPlaceholder);
 		} 
 		//LOYALTY_LOGIN_URL
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LOGIN_URL.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = getLoyaltyURL(contactsLoyalty,defVal);
 			logger.info("LOYALTY_LOGIN_URL::"+loyaltyPlaceholder);
 		}
 		/*//ORGANIZATION_NAME
 		else if(PlaceHolders.CAMPAIGN_PH_ORGANIZATION_NAME.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = getUserOrganization(contactsLoyalty,defVal);
 			logger.info("PH_ORGANIZATION_NAME::"+loyaltyPlaceholder);
 		}*/
 		//REWARD_EXPIRATION_PERIOD
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_REWARD_EXPIRATION_PERIOD.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = getRewardExpirationPeriod(contactsLoyalty ,defVal);
 			logger.info("LOYALTY_REWARD_EXPIRATION_Period ::"+loyaltyPlaceholder);
 		}
 		//MEMBERSHIP_EXPIRATION_DATE
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_MEMBERSHIP_EXPIRATION_DATE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder =getLoyaltyMembershipExpirationDate(contactsLoyalty, defVal);	
 			logger.info("MEMBERSHIP_EXPIRATION_DATE ::"+loyaltyPlaceholder);
 		}
 		//LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = getGiftAmountExpirationPeriod(contactsLoyalty,defVal);
 			logger.info("LOYALTY_GIFT_AMOUNT_EXPIRATION_PERIOD :: "+loyaltyPlaceholder);
 		}
 		//LOYALTY_LAST_BONUS_VALUE
 		else if(PlaceHolders.CAMPAIGN_PH_LOYALTY_LAST_BONUS_VALUE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = contactsLoyalty.getCardNumber() != null ? getLastBonusValue(contactsLoyalty.getLoyaltyId(),OCConstants.LOYALTY_TRANS_TYPE_BONUS,defVal,contactsLoyalty.getUserId()) : defVal;
 			logger.info("LOYALTY_LAST_BONUS_VALUE :: "+loyaltyPlaceholder);
 		}
 		//REWARD_EXPIRING_VALUE
 		else if(PlaceHolders.CAMPAIGN_PH_REWARD_EXPIRING_VALUE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = getRewardExpiringValue(contactsLoyalty,defVal);
 			logger.info("REWARD_EXPIRING_VALUE :: "+loyaltyPlaceholder);
 		}
 		//GIFT_AMOUNT_EXPIRING_VALUE
 		else if(PlaceHolders.CAMPAIGN_PH_GIFT_AMOUNT_EXPIRING_VALUE.equalsIgnoreCase(placeholder)){
 			loyaltyPlaceholder = getGiftAmountExpiringValue(contactsLoyalty,defVal);
 			logger.info("GIFT_AMOUNT_EXPIRING_VALUE :: "+loyaltyPlaceholder);
 		}
 		logger.info("Completed replace holder method");
 		return loyaltyPlaceholder;
 	}
     
     private String getGiftAmountExpiringValue(ContactsLoyalty contactsLoyalty,String defVal) {
 		logger.info("--Start of getGiftAmountExpiringValue--");
 		String giftExpValue = defVal;
 		try {
 			if(contactsLoyalty.getProgramId()== null) return giftExpValue;
 			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
 			LoyaltyProgram program = loyaltyProgramDao.findById(contactsLoyalty.getProgramId());

 			if(OCConstants.FLAG_YES == program.getGiftAmountExpiryFlag() && program.getGiftAmountExpiryDateType() != null 
 					&& program.getGiftAmountExpiryDateValue() != null){

 				Calendar cal = Calendar.getInstance();
 				if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(program.getGiftAmountExpiryDateType())){
 					cal.add(Calendar.MONTH, -(program.getGiftAmountExpiryDateValue().intValue()));
 				}
 				else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(program.getGiftAmountExpiryDateType())){
 					cal.add(Calendar.YEAR, -(program.getGiftAmountExpiryDateValue().intValue()));
 				}
 				String expDate = "";
 				if(cal.get(Calendar.MONTH) == 11) {
 					expDate = cal.get(Calendar.YEAR)+"-12";
 				} 
 				else {
 					cal.add(Calendar.MONTH, 1);
 					expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
 				}
 				logger.info("expDate = "+expDate);

 				Object[] expiryValueArr = fetchExpiryValues(contactsLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G);

 				if(expiryValueArr != null && expiryValueArr[2] != null){
 					DecimalFormat decimalFormat = new DecimalFormat("#0.00");
 					double expGift = Double.valueOf(expiryValueArr[2].toString());
 					if(expGift > 0){
 						giftExpValue = decimalFormat.format(expGift);  
 					}
 				}
 			}
 		}
 		catch(Exception e) {
 			logger.error("Exception ::",e);
 		}
 		logger.info("--Exit of getGiftAmountExpiringValue--");
 		return giftExpValue;
 	}
     
     private String getRewardExpiringValue(ContactsLoyalty contactsLoyalty,String defVal) {
 		logger.info("--Start of getRewardExpiringValue--");
 		String rewardExpVal = defVal ;
 		try {
 			if(contactsLoyalty.getProgramId()== null) return rewardExpVal;
 			LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
 			LoyaltyProgram program = loyaltyProgramDao.findById(contactsLoyalty.getProgramId());

 			if(contactsLoyalty.getProgramTierId()== null) return rewardExpVal;
 			LoyaltyProgramTierDao loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
 			LoyaltyProgramTier loyaltyProgramTier = loyaltyProgramTierDao.findByTierId(contactsLoyalty.getProgramTierId());

 			if(OCConstants.FLAG_YES == program.getRewardExpiryFlag() && loyaltyProgramTier.getRewardExpiryDateType() != null 
 					&& loyaltyProgramTier.getRewardExpiryDateValue() != null){

 				Calendar cal = Calendar.getInstance();
 				if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_MONTH.equals(loyaltyProgramTier.getRewardExpiryDateType())){
 					cal.add(Calendar.MONTH, -(loyaltyProgramTier.getRewardExpiryDateValue().intValue()));
 				}
 				else if(OCConstants.LOYALTY_MEMBERSHIP_EXPIRYDATE_TYPE_YEAR.equals(loyaltyProgramTier.getRewardExpiryDateType())){
 					cal.add(Calendar.YEAR, -(loyaltyProgramTier.getRewardExpiryDateValue().intValue()));
 				}

 				String expDate = "";
 				if(cal.get(Calendar.MONTH) == 11) {
 					expDate = cal.get(Calendar.YEAR)+"-12";
 				} 
 				else {
 					cal.add(Calendar.MONTH, 1);
 					expDate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH);
 				}
 				logger.info("expDate = "+expDate);
 				Object[] expiryValueArr = fetchExpiryValues(contactsLoyalty.getLoyaltyId(), expDate, OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);

 				if(expiryValueArr != null ) { 
 					DecimalFormat decimalFormat = new DecimalFormat("#0.00");
 					if(expiryValueArr[1] != null && Long.valueOf(expiryValueArr[1].toString()) > 0 && expiryValueArr[2] != null
 							&& Double.valueOf(expiryValueArr[2].toString()) >  0.0){
 						rewardExpVal = Long.valueOf(expiryValueArr[1].toString())+" Points"+
 													" & "+decimalFormat.format(Double.valueOf(expiryValueArr[2].toString()));
 					}
 					else if(expiryValueArr[1] != null && Long.valueOf(expiryValueArr[1].toString()) > 0 && (expiryValueArr[2] == null ||
 							Double.valueOf(expiryValueArr[2].toString()) == 0.0)) {
 						rewardExpVal = Long.valueOf(expiryValueArr[1].toString())+" Points";
 					}
 					else if(expiryValueArr[2] != null && Double.valueOf(expiryValueArr[2].toString()) >  0.0
 							&& (expiryValueArr[1] == null || Long.valueOf(expiryValueArr[1].toString()) == 0)){
 						rewardExpVal = decimalFormat.format(Double.valueOf(expiryValueArr[2].toString()));
 					}
 					else {
 						rewardExpVal = defVal;
 					}
 				}
 			}
 		}
 		catch(Exception e) {
 			logger.error("Exception ::",e);
 		}
 		logger.info("--Exit of getRewardExpiringValue--");
 		return rewardExpVal;
 	}
     
     private Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag) throws Exception {
 		logger.info("--Start of fetchExpiryValues--");
 		LoyaltyTransactionExpiryDao expiryDao = (LoyaltyTransactionExpiryDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_EXPIRY_DAO);
 		logger.info("--Exit of fetchExpiryValues--");
 		return expiryDao.fetchOnlyExpiryValues(loyaltyId, expDate, rewardFlag);
 	}
     
     private String getLastBonusValue(Long loyaltyId,String transactionType, String defVal,Long userId) {
 		logger.info("--Start of getLastBonusValue--");
 		String loyaltyPlaceholder = "";
 		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
 		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
 		LoyaltyTransactionChild loyaltyTransactionChild = null;
 		loyaltyTransactionChild = ltyPrgmService.getTransByMembershipNoAndTransType(loyaltyId, transactionType,userId);
 		if(loyaltyTransactionChild != null){
 			if(loyaltyTransactionChild.getEarnedAmount() != null && loyaltyTransactionChild.getEarnedPoints() != null){
 				loyaltyPlaceholder = decimalFormat.format(loyaltyTransactionChild.getEarnedAmount())+" & "+loyaltyTransactionChild.getEarnedPoints().intValue()+" Points";
 			}
 			else if(loyaltyTransactionChild.getEarnedAmount() != null && loyaltyTransactionChild.getEarnedPoints() == null){
 				loyaltyPlaceholder = decimalFormat.format(loyaltyTransactionChild.getEarnedAmount());
 			}
 			else if(loyaltyTransactionChild.getEarnedAmount() == null && loyaltyTransactionChild.getEarnedPoints() != null){
 				loyaltyPlaceholder = loyaltyTransactionChild.getEarnedPoints().intValue()+" Points";
 			}
 			else{
 				loyaltyPlaceholder = defVal;
 			}
 		}
 		else{
 			loyaltyPlaceholder = defVal;
 		}
 		logger.info("--Exit of getLastBonusValue--");
 		return loyaltyPlaceholder;
 	}
     
     private String getMemberTier(Long programTierId,String defValue) {

 		LoyaltyProgramTier loyaltyProgramTier = null;
 		//helper class obj
 		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
 		String tier = "" ,level ="",loyaltyPlaceholder="";

 		loyaltyProgramTier = ltyPrgmService.getTierObj(programTierId);
 		if(loyaltyProgramTier != null){
 			tier = loyaltyProgramTier.getTierName() ;
 			level = " ( Level : "+(loyaltyProgramTier.getTierType() == null ? "" : loyaltyProgramTier.getTierType())+" )";
 			loyaltyPlaceholder = tier + level ; //it will tier name + level
 		}
 		else{
 			loyaltyPlaceholder = defValue; //default value to be replaced
 		}
 		return loyaltyPlaceholder;
 	}
     
     
     private String getEnrollmentSource(String loyaltyType, String defVal) {
 		String loyaltyPH = "";
 		if(Constants.CONTACT_LOYALTY_TYPE_POS.equalsIgnoreCase(loyaltyType)) {
 			loyaltyPH = Constants.CONTACT_LOYALTY_TYPE_STORE;
 		}
 		else {
 			loyaltyPH = loyaltyType;
 		}
 		return loyaltyPH;
 	}
     
     private String getGiftCardExpirationDate(ContactsLoyalty contactsLoyalty, String defVal) {
 		logger.debug(">>>>>>>>>>>>> entered in getGiftCardExpirationDate");
 		String giftCardExpriationDate = defVal;

 		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
 		LoyaltyProgram loyaltyProgram =  null;

 		if(contactsLoyalty.getProgramId() != null){
 			loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
 			if(loyaltyProgram != null && contactsLoyalty.getRewardFlag() != null && OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
 				if(loyaltyProgram.getGiftMembrshpExpiryFlag() == 'Y' && loyaltyProgram.getGiftMembrshpExpiryDateType() != null 
 						&& loyaltyProgram.getGiftMembrshpExpiryDateValue() != null){
 					giftCardExpriationDate = LoyaltyProgramHelper.getGiftMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), 
 							loyaltyProgram.getGiftMembrshpExpiryDateType(), loyaltyProgram.getGiftMembrshpExpiryDateValue());
 				}//if 
 			}
 		}
 		logger.debug("<<<<<<<<<<<<< completed getGiftCardExpirationDate ");
 		return giftCardExpriationDate;
 	}
     
     private String getHoldBalance(ContactsLoyalty contactsLoyalty,String defVal) {
 		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
 		String loyaltyPlaceholder ="";
 		if(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldPointsBalance() != null){
 			loyaltyPlaceholder = decimalFormat.format(contactsLoyalty.getHoldAmountBalance()) +" & "+contactsLoyalty.getHoldPointsBalance().intValue()+ " Points";;
 		}
 		else if(contactsLoyalty.getHoldAmountBalance() != null && contactsLoyalty.getHoldPointsBalance() == null){
 			loyaltyPlaceholder = decimalFormat.format(contactsLoyalty.getHoldAmountBalance());// +" & "+contactsLoyalty.getHoldPointsBalance().intValue();
 		}
 		else if(contactsLoyalty.getHoldAmountBalance() == null && contactsLoyalty.getHoldPointsBalance() != null){
 			loyaltyPlaceholder = contactsLoyalty.getHoldPointsBalance().intValue() + " Points";
 		}
 		else{
 			loyaltyPlaceholder =  defVal;
 		}
 		return loyaltyPlaceholder;
 	}
     
     private String getRewardActivationPeriod(Long programTierId,String defValue) {

 		LoyaltyProgramTier loyaltyProgramTier = null;
 		String loyaltyPlaceholder = defValue;
 		//helper class obj
 		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
 		loyaltyProgramTier = ltyPrgmService.getTierObj(programTierId);
 		if(loyaltyProgramTier != null && loyaltyProgramTier.getActivationFlag() == OCConstants.FLAG_YES){
 			loyaltyPlaceholder = loyaltyProgramTier.getPtsActiveDateValue()+" "+loyaltyProgramTier.getPtsActiveDateType()+OCConstants.MORETHANONEOCCURENCE;
 		}
 		else{
 			loyaltyPlaceholder = defValue ;
 		}
 		return loyaltyPlaceholder;
 	}
     
     private String getLastEarnedValue(Long loyaltyId,String loyaltyTransTypeIssuance,String defValue,Long userId) {
 		String loyaltyPlaceholder = "";
 		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
 		//helper class obj
 		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
 		LoyaltyTransactionChild child = null;
 		child = ltyPrgmService.getTransByMembershipNoAndTransType(loyaltyId, loyaltyTransTypeIssuance,userId);
 		if(child != null){
 			if(child.getEarnedAmount() != null && child.getEarnedPoints() != null){
 				loyaltyPlaceholder = decimalFormat.format(child.getEarnedAmount())+" & "+child.getEarnedPoints().intValue()+" Points";
 			}
 			else if(child.getEarnedAmount() != null && child.getEarnedPoints() == null){
 				loyaltyPlaceholder = decimalFormat.format(child.getEarnedAmount());
 			}
 			else if(child.getEarnedAmount() == null && child.getEarnedPoints() != null){
 				loyaltyPlaceholder = child.getEarnedPoints().intValue()+" Points";
 			}
 			else{
 				loyaltyPlaceholder = defValue;
 			}
 		}
 		else{
 			loyaltyPlaceholder = defValue;
 		}
 		return loyaltyPlaceholder;
 	}//getLastEarnedValue

 	/**
 	 * This method calculate Last Redeemed Value
 	 * @param cardNumber
 	 * @param loyaltyTransTypeIssuance
 	 * @return Last Redeemed Value
 	 */
 	private String getLastRedeemedValue(Long loyaltyId,String loyaltyTransTypeRedemption,String defValue,Long userId) {
 		String loyaltyPlaceholder = "";
 		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
 		//helper class obj
 		LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
 		LoyaltyTransactionChild child = null;
 		child = ltyPrgmService.getTransByMembershipNoAndTransType(loyaltyId, loyaltyTransTypeRedemption,userId);
 		if(child != null){
 			if(child.getEnteredAmount() != null && child.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTSREDEEM)){
 				loyaltyPlaceholder = child.getEnteredAmount().intValue()+" Points";
 			}
 			else if(child.getEnteredAmount() != null && child.getEnteredAmountType().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_AMOUNTREDEEM)){
 				loyaltyPlaceholder = decimalFormat.format(child.getEnteredAmount());
 			}
 			else{
 				loyaltyPlaceholder = defValue;
 			}
 		}
 		else{
 			loyaltyPlaceholder = defValue;
 		}
 		return loyaltyPlaceholder;

 	}//getLastRedeemedValue
 	
 	private String getRewardExpirationPeriod(ContactsLoyalty contactsLoyalty, String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getRewardExpirationPeriod");
		String rewardExpirationPeriod = defVal;

		Long tierId =  contactsLoyalty.getProgramTierId();

		if(tierId != null){
			LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
			LoyaltyProgramTier loyaltyProgramTier = null;
			LoyaltyProgram loyaltyProgram =  null;

			if(contactsLoyalty.getProgramId() != null  && contactsLoyalty.getRewardFlag() != null){
				loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
				loyaltyProgramTier = ltyPrgmService.getTierObj(contactsLoyalty.getProgramTierId());

				if(loyaltyProgram != null && loyaltyProgramTier != null && loyaltyProgram.getRewardExpiryFlag()==OCConstants.FLAG_YES){

					if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) || OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

						if(loyaltyProgramTier != null && loyaltyProgramTier.getRewardExpiryDateValue() != null 
								&& loyaltyProgramTier.getRewardExpiryDateValue() != 0 
								&& loyaltyProgramTier.getRewardExpiryDateType() != null 
								&& !loyaltyProgramTier.getRewardExpiryDateType().isEmpty())
						{
							rewardExpirationPeriod = loyaltyProgramTier.getRewardExpiryDateValue()+" "
									+loyaltyProgramTier.getRewardExpiryDateType()+OCConstants.MORETHANONEOCCURENCE;
						}//if

					}//if oc 
				}//if lty !=null
			}//if cont
		}//tier id
		logger.debug("<<<<<<<<<<<<< completed getRewardExpirationPeriod ");
		return rewardExpirationPeriod;
	}//getRewardExpirationPeriod

 	private String getMembershipPassword(ContactsLoyalty contactsLoyalty,String defVal) {
		logger.debug(">>>>>>>>>>>>> entered in getMembershipPassword");
		String password = defVal;
		try {
			if(!contactsLoyalty.getRewardFlag().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G)) {
				//password = contactsLoyalty.getMembershipPwd() != null ? EncryptDecryptLtyMembshpPwd.decrypt( contactsLoyalty.getMembershipPwd()) : defVal;
			}
		} catch (Exception e) {
			logger.error("Expection while replacing place holder :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed getMembershipPassword ");
		return password;
	}
 	
private String getLoyaltyURL(ContactsLoyalty contactsLoyalty,String defVal) {
		
		logger.debug(">>>>>>>>>>>>> entered in getLoyaltyURL");
		String loyaltyUrl = defVal;
		try {
		LoyaltyProgramService loyaltyProgramService =  new LoyaltyProgramService();
		UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.find(contactsLoyalty.getUserId());
		LoyaltySettings loyaltySettings = loyaltyProgramService.findLoyaltySettingsByOrgId(user.getUserOrganization().getUserOrgId());

		if(loyaltySettings != null){
			loyaltyUrl = loyaltySettings.getUrlStr();
			loyaltyUrl = "<a href="+loyaltyUrl+">"+loyaltyUrl+"</a>";
		}
		logger.debug("<<<<<<<<<<<<< completed getLoyaltyURL ");
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
		return loyaltyUrl;
	}

private String getLoyaltyMembershipExpirationDate(ContactsLoyalty contactsLoyalty, String defVal) {
	logger.debug(">>>>>>>>>>>>> entered in getLoyaltyMembershipExpriationDate");
	String membershipExpriationDate = defVal;
	LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
	LoyaltyProgramTier loyaltyProgramTier = null;
	LoyaltyProgram loyaltyProgram =  null;

	if(contactsLoyalty.getProgramId() != null && contactsLoyalty.getProgramTierId() != null && contactsLoyalty.getRewardFlag() != null){
		loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());
		loyaltyProgramTier = ltyPrgmService.getTierObj(contactsLoyalty.getProgramTierId());

		if(loyaltyProgram != null && loyaltyProgramTier != null){

			if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) || OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){
				////if flag L or GL
				if(loyaltyProgram.getMembershipExpiryFlag() == 'Y' && loyaltyProgramTier.getMembershipExpiryDateType() != null 
						&& loyaltyProgramTier.getMembershipExpiryDateValue() != null){

					boolean upgdReset = loyaltyProgram.getMbrshipExpiryOnLevelUpgdFlag() == 'Y' ? true : false;
					
					membershipExpriationDate = LoyaltyProgramHelper.getMbrshipExpiryDate(contactsLoyalty.getCreatedDate(), contactsLoyalty.getTierUpgradedDate(), 
							upgdReset, loyaltyProgramTier.getMembershipExpiryDateType(), loyaltyProgramTier.getMembershipExpiryDateValue());
				}
			}//if

		}//loyaltyProgram && loyaltyProgramTier 
	}
	logger.debug("<<<<<<<<<<<<< completed getLoyaltyMembershipExpriationDate ");
	return membershipExpriationDate;
}

private String getGiftAmountExpirationPeriod(ContactsLoyalty contactsLoyalty, String defVal) {
	logger.debug(">>>>>>>>>>>>> entered in getGiftAmountExpirationPeriod");
	String giftAmountExpirationPeriod = defVal;

	LoyaltyProgramService ltyPrgmService =  new LoyaltyProgramService();
	LoyaltyProgram loyaltyProgram =  null;

	if(contactsLoyalty.getProgramId() != null  && contactsLoyalty.getRewardFlag() != null){
		loyaltyProgram = ltyPrgmService.getProgmObj(contactsLoyalty.getProgramId());

		if(loyaltyProgram != null && loyaltyProgram.getGiftMembrshpExpiryFlag() == OCConstants.FLAG_YES){

			if(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_G.equalsIgnoreCase(contactsLoyalty.getRewardFlag()) ||
					OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_GL.equalsIgnoreCase(contactsLoyalty.getRewardFlag())){

				if(loyaltyProgram.getGiftAmountExpiryDateValue() != null  && loyaltyProgram.getGiftAmountExpiryDateValue() != 0 
						&& loyaltyProgram.getGiftAmountExpiryDateType() != null && !loyaltyProgram.getGiftAmountExpiryDateType().isEmpty())
				{
					giftAmountExpirationPeriod = loyaltyProgram.getGiftAmountExpiryDateValue()+" "+loyaltyProgram.getGiftAmountExpiryDateType()+OCConstants.MORETHANONEOCCURENCE;
				}//if

			}//if oc 
		}//if lty !=null
	}//if cont
	logger.debug("<<<<<<<<<<<<< completed getGiftAmountExpirationPeriod ");
	return giftAmountExpirationPeriod;
}

}//class
