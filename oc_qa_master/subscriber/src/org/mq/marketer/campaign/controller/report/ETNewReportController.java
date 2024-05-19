package org.mq.marketer.campaign.controller.report;
	
    	import java.awt.BasicStroke;
	import java.awt.Color;
	import java.io.BufferedReader;
	import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.FileReader;
	import java.io.IOException;
	import java.lang.reflect.Field;
	import java.sql.ResultSet;
	import java.text.Normalizer.Form;
	import java.util.ArrayList;
	import java.util.Calendar;
	import java.util.Collection;
	import java.util.HashMap;
	import java.util.HashSet;
	import java.util.List;
	import java.util.Map;
	import java.util.Set;
	import java.util.TimeZone;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileWriter;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import org.zkoss.zul.Filedownload;
	import org.mq.optculture.data.dao.ContactsJdbcResultsetHandler;
	import org.mq.optculture.data.dao.JdbcResultsetHandler;
	
	

	import org.apache.logging.log4j.LogManager;
	import org.apache.logging.log4j.Logger;
	import org.mq.marketer.campaign.beans.CampaignSent;
	import org.mq.marketer.campaign.beans.Campaigns;
	import org.mq.marketer.campaign.beans.ContactSpecificDateEvents;
	import org.mq.marketer.campaign.beans.DRSent;
	import org.mq.marketer.campaign.beans.EmailQueue;
    	import org.mq.marketer.campaign.beans.EventTrigger;
    	import org.mq.marketer.campaign.beans.EventTriggerEvents;
    	import org.mq.marketer.campaign.beans.OrganizationStores;
    	import org.mq.marketer.campaign.beans.POSMapping;
    	import org.mq.marketer.campaign.beans.SMSCampaigns;
	import org.mq.marketer.campaign.beans.Users;
	import org.mq.marketer.campaign.controller.GetUser;
	import org.mq.marketer.campaign.controller.PrepareFinalHtml;
	import org.mq.marketer.campaign.controller.campaign.DigitalRecieptsReportsController;
	import org.mq.marketer.campaign.custom.MyCalendar;
	import org.mq.marketer.campaign.custom.MyDatebox;
	import org.mq.marketer.campaign.dao.CampaignSentDao;
	import org.mq.marketer.campaign.dao.CampaignsDao;
	import org.mq.marketer.campaign.dao.ClicksDao;
    	import org.mq.marketer.campaign.dao.ContactSpecificDateEventsDao;
	import org.mq.marketer.campaign.dao.ContactsDao;
	import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
	import org.mq.marketer.campaign.dao.EmailQueueDao;
	import org.mq.marketer.campaign.dao.EventTriggerDao;
    	import org.mq.marketer.campaign.dao.EventTriggerEventsDao;
	import org.mq.marketer.campaign.dao.OpensDao;
	import org.mq.marketer.campaign.dao.OrganizationStoresDao;
	import org.mq.marketer.campaign.dao.POSMappingDao;
	import org.mq.marketer.campaign.dao.RetailProSalesDao;
	import org.mq.marketer.campaign.dao.SMSCampaignsDao;
	import org.mq.marketer.campaign.dao.UsersDao;
	import org.mq.marketer.campaign.general.Constants;
	import org.mq.marketer.campaign.general.LineChartEngine;
	import org.mq.marketer.campaign.general.MessageUtil;
	import org.mq.marketer.campaign.general.PageUtil;
	import org.mq.marketer.campaign.general.PropertyUtil;
	import org.mq.marketer.campaign.general.SMSStatusCodes;
	import org.mq.marketer.campaign.general.SearchFilterEnum;
	import org.mq.marketer.campaign.general.Utility;
	import org.zkoss.zk.ui.Component;
	import org.zkoss.zk.ui.Components;
	import org.zkoss.zk.ui.Desktop;
	import org.zkoss.zk.ui.Executions;
	import org.zkoss.zk.ui.Session;
	import org.zkoss.zk.ui.Sessions;
	import org.zkoss.zk.ui.WrongValueException;
	import org.zkoss.zk.ui.event.Event;
	import org.zkoss.zk.ui.event.EventListener;
	import org.zkoss.zk.ui.event.InputEvent;
	import org.zkoss.zk.ui.util.Clients;
	import org.zkoss.zk.ui.util.GenericForwardComposer;
	import org.zkoss.zkplus.spring.SpringUtil;
	import org.zkoss.zul.A;
	import org.zkoss.zul.Auxhead;
	import org.zkoss.zul.Auxheader;
	import org.zkoss.zul.Bandbox;
	import org.zkoss.zul.CategoryModel;
	import org.zkoss.zul.Chart;
	import org.zkoss.zul.Checkbox;
	import org.zkoss.zul.Div;
	import org.zkoss.zul.Grid;
	import org.zkoss.zul.Hbox;
	import org.zkoss.zul.Html;
	import org.zkoss.zul.Image;
	import org.zkoss.zul.Intbox;
	import org.zkoss.zul.Label;
	import org.zkoss.zul.ListModelList;
	import org.zkoss.zul.Listbox;
	import org.zkoss.zul.Listcell;
	import org.zkoss.zul.Listitem;
	import org.zkoss.zul.ListitemRenderer;
	import org.zkoss.zul.Messagebox;
	import org.zkoss.zul.Paging;
	import org.zkoss.zul.Row;
	import org.zkoss.zul.Rows;
	import org.zkoss.zul.SimpleCategoryModel;
	import org.zkoss.zul.Textbox;
	import org.zkoss.zul.Toolbarbutton;
	import org.zkoss.zul.Window;
	import org.mq.marketer.campaign.general.*;
	import org.zkoss.zul.event.PagingEvent;



	public class ETNewReportController extends GenericForwardComposer implements EventListener{
		
		private OpensDao opensDao;
		private ClicksDao clicksDao;
		private EventTriggerEventsDao eventTriggerEventsDao;
		private ContactSpecificDateEventsDao contactSpecificDateEventsDao;
		private CampaignSentDao campaignSentDao;
		private Listbox reportsPerPageLBId,smsReportsPerPageLBId;
		private Div emailGridDivId,smsGridDivId,emailCheckDivId,emailChartDivId, emailListDivId,smsCheckDivId,smsChartDivId,smsListDivId,emailInfoDivId,smsInfoDivId;
		private MyDatebox fromDateboxId;
		private MyDatebox toDateboxId;
		private Listitem reptypeLbId$emailLitemId,reptypeLbId$smsLiId;
		private TimeZone clientTimeZone ;
		Desktop desktopScope = null;
		Session sessionScope = null;
		private Users currentUser;
		private Paging reportsPagingId,smsReportsPagingId,statusPendingReportsLocationsPagingId;
		private Listbox reptypeLbId, emailSmsControlsLbId;
		private Chart etReportsChartId,etReportsSmsChartId;
		private Window viewAllWinId;
		private Rows viewAllWinId$viewAllEmailRowsId,viewAllWinId$viewAllSmsRowsId;
		private Grid viewAllWinId$viewEmailGridId, viewAllWinId$viewSmsGridId;
		private Label trigNameId, triggerNameLblId, triggerTypeLblId, triggerCreatedLblId, emailNameLblId, smsNameLblId, subjectLblId;
		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		private ContactsDao contactsDao;
		private OrganizationStoresDao organizationStoresDao;
		private ContactsLoyaltyDao contactsLoyaltyDao;
		private RetailProSalesDao retailProSalesDao;
		private POSMappingDao posMappingDao;
		private UsersDao usersDao;
		private EventTrigger eventTrigger;
		private Label sentLblId,deliveredLblId,bouncedLblId,opensLblId,clicksLblId,spamLblId,sentSmsLblId,receivedSmsLblId,pendingSmsLblId,undeliveredSmsLblId,deliveryReportLblId,clickedSmsLblId,consolidatedMetricsLblId,
		notSentLblId,notSentlabelId,totalStatusPendingLbID;
		private Grid etReportsGrId;
		private Listbox etReportsLbId,etReportsLbId1,statusPendingReportsPerPageLBId,statusPendingReportsLbId;
		private final String ET_TYPE_MONTHS="Months";
		private final String ET_TYPE_DAYS="Days";
		boolean isContactDateType,isEmail;
		private Window custExport;
		private Div custExport$chkDivId,pendingReportsDivId;
		private A viewEmailBtnId,viewSmsBtnId;
		boolean isSmsChecked,isemailChecked;
		
		boolean isThruChekbx;
		private Label deliveryOverTimeLabelId;
		private boolean exportEmailReport;
		private boolean exportStatusPendingReport;
		private String emailSatusTobeFetched;
		private String smsSatusTobeFetched;
		private byte considerDeliveredStatus;
		private Bandbox storeBandBoxId;
		private Textbox statusPendingSearchBoxId;
		public ETNewReportController(){
			
			desktopScope = Executions.getCurrent().getDesktop();
			sessionScope = Sessions.getCurrent();
			currentUser = GetUser.getUserObj();
			eventTrigger = (EventTrigger)sessionScope.getAttribute("eventTriggerObj");
			clientTimeZone = (TimeZone)sessionScope.getAttribute("clientTimeZone");
			this.opensDao= (OpensDao)SpringUtil.getBean("opensDao");
			this.clicksDao = (ClicksDao)SpringUtil.getBean("clicksDao");
			this.contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
			this.organizationStoresDao = (OrganizationStoresDao)SpringUtil.getBean("organizationStoresDao");
			this.contactsLoyaltyDao = (ContactsLoyaltyDao)SpringUtil.getBean("contactsLoyaltyDao");
			this.retailProSalesDao = (RetailProSalesDao)SpringUtil.getBean("retailProSalesDao");
			this.posMappingDao = (POSMappingDao)SpringUtil.getBean("posMappingDao");
			this .usersDao = (UsersDao)SpringUtil.getBean("usersDao");
			eventTriggerEventsDao = (EventTriggerEventsDao)SpringUtil.getBean("eventTriggerEventsDao");
			contactSpecificDateEventsDao = (ContactSpecificDateEventsDao)SpringUtil.getBean("contactSpecificDateEventsDao");
			campaignSentDao = (CampaignSentDao)SpringUtil.getBean("campaignSentDao");
			String style="font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
			PageUtil.setHeader("Event Trigger Reports", "", style, true);
		}
		
		
		
		private static Map<String,String> MONTH_MAP = DigitalRecieptsReportsController.MONTH_MAP;
		public static Map<String,String> TRIGGER_TYPE_MAP = new HashMap<String, String>();
		private Set<Listitem> allItemsSet = new HashSet<Listitem>();
		static{
			
			TRIGGER_TYPE_MAP.put("2", "On Purchase");
			TRIGGER_TYPE_MAP.put("1", "Purchase of Product");
			TRIGGER_TYPE_MAP.put("4", "Receipt Amount");
			TRIGGER_TYPE_MAP.put("8", "Contact Date");
			TRIGGER_TYPE_MAP.put("16", "Opt-In Medium");
			TRIGGER_TYPE_MAP.put("32", "Campaign Opened");
			TRIGGER_TYPE_MAP.put("64", "Campaign Clicked");
			TRIGGER_TYPE_MAP.put("128", "Loyalty Balance");
			TRIGGER_TYPE_MAP.put("384", "Loyalty Balance");
			TRIGGER_TYPE_MAP.put("640", "Loyalty Balance");
			TRIGGER_TYPE_MAP.put("896", "Loyalty Balance");
			TRIGGER_TYPE_MAP.put("1152", "Loyalty Balance");
			TRIGGER_TYPE_MAP.put("1664", "Loyalty Balance");
			TRIGGER_TYPE_MAP.put("1920", "Loyalty Balance");
			TRIGGER_TYPE_MAP.put("2048", "Contact Added");
			TRIGGER_TYPE_MAP.put("12288", "Gift Balance");
			TRIGGER_TYPE_MAP.put("20480", "Gift Balance");
		 }
		int totalSize = 0;
		int pendingReportSize = 0;
		@Override
		public void doAfterCompose(Component comp) throws Exception {
			try {
				// TODO Auto-generated method stub
				super.doAfterCompose(comp);
				etReportsChartId.setEngine(new LineChartEngine());
				//Default DateSettings
				exportEmailReport = true;
				exportStatusPendingReport =true;
				
				isThruChekbx = false;
				
				smsChkId.setChecked(true);
				emailChkId.setChecked(true);
				
				
				emailSatusTobeFetched="'Unsubscribed','Spammed','Success','Bounced','special_condtion_for_all'";
				//emailSatusTobeFetched="'Unsubscribed','Spammed','Success','Bounced','Submitted','special_condtion_for_all'";
				smsSatusTobeFetched="'Delivered','Success'";
				considerDeliveredStatus = 2;// indicates that all types of sms status are to be fetched
				
				
				int trType = eventTrigger.getTrType();
				isContactDateType = ( (trType & Constants.ET_TYPE_ON_CONTACT_DATE) == Constants.ET_TYPE_ON_CONTACT_DATE );
				triggerNameLblId.setValue(eventTrigger.getTriggerName());
				triggerTypeLblId.setValue(TRIGGER_TYPE_MAP.get(eventTrigger.getTrType().toString()));
				triggerCreatedLblId.setValue(MyCalendar.calendarToString(eventTrigger.getTriggerCreatedDate(), MyCalendar.FORMAT_STDATE));
				
				if(eventTrigger.getCampaignId() != null) {
					
					CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
					

					
					
				
					
					List<Campaigns> campLst = campaignsDao.getCampaignById(eventTrigger.getCampaignId().longValue()+"");
					if(campLst != null) {
					
						for (Campaigns camp : campLst) {
							
							
							emailNameLblId.setValue(camp != null ? camp.getCampaignName() : Constants.STRING_NILL);
							subjectLblId.setValue(camp != null ? camp.getSubject() : Constants.STRING_NILL );
							subjectLblId.setTooltiptext(camp != null ? camp.getSubject() : Constants.STRING_NILL);
							viewEmailBtnId.setAttribute("campaign", camp);
							break;
						}
					}
				}//i
				
				if(eventTrigger.getSmsId() != null) {
					SMSCampaignsDao campaignsDao = (SMSCampaignsDao)SpringUtil.getBean("smsCampaignsDao");
					SMSCampaigns camp = campaignsDao.findByCampaignId(eventTrigger.getSmsId());
					smsNameLblId.setValue(camp != null ? camp.getSmsCampaignName() : Constants.STRING_NILL);
					
					viewSmsBtnId.setAttribute("campaign", camp);
					
				}
				
				setDateValues();
				
				
				
				String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
				
			    String deliveryReportLblIdStrFromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_MONTHDATE_ONLY,clientTimeZone);
			    String deliveryReportLblIdStrToDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_MONTHDATE_ONLY,clientTimeZone);

			//String deliveryReportLblIdStrFromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_MONTHDATE_ONLY);
			    //String deliveryReportLblIdStrToDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_MONTHDATE_ONLY);
				
				
				if(!isContactDateType) {
					totalSize = eventTriggerEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,null,emailSatusTobeFetched );
					
				}else {
					
					totalSize = contactSpecificDateEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,emailSatusTobeFetched );
				}
				
				pageSize = Integer.parseInt(reportsPerPageLBId.getSelectedItem().getLabel());
				
				reportsPagingId.addEventListener("onPaging", this);
				reportsPagingId.setTotalSize(totalSize);
				reportsPagingId.setPageSize(pageSize);
				reportsPagingId.setActivePage(0);
				reportsPagingId.setAttribute("smsOremail", "email");
				
				long notSentCount=0;
				if(!isContactDateType) {
				pendingReportSize = eventTriggerEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,"'Submitted'" );
				notSentCount = eventTriggerEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SUBMITTED);
				}else{
					pendingReportSize = contactSpecificDateEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,"'Submitted'" );
					notSentCount = contactSpecificDateEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SUBMITTED);
				}
				
				//for sms reports -- rajeev
				if(!isContactDateType) {
					totalSize = eventTriggerEventsDao.findSearchSmsCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,null,considerDeliveredStatus );
				}else {
					
					totalSize = contactSpecificDateEventsDao.findSearchSmsCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null, considerDeliveredStatus );
				}
				pageSize = Integer.parseInt(smsReportsPerPageLBId.getSelectedItem().getLabel());
				
				smsReportsPagingId.addEventListener("onPaging", this);
				smsReportsPagingId.setTotalSize(totalSize);
				smsReportsPagingId.setPageSize(pageSize);
				smsReportsPagingId.setActivePage(0);
				smsReportsPagingId.setAttribute("smsOremail", "sms");
				
				isEmail=true;

				fillTriggerConsolidatedMatrix(fromDate, endDate, isContactDateType, isEmail);
				
				statusPendingReportsLocationsPagingId.addEventListener("onPaging", this);
				statusPendingReportsLocationsPagingId.setTotalSize(pendingReportSize);
				statusPendingReportsLocationsPagingId.setPageSize(Integer.parseInt(statusPendingReportsPerPageLBId.getSelectedItem().getLabel()));
				statusPendingReportsLocationsPagingId.setActivePage(0);
				/*if(pendingReportSize>0){
					pendingReportsDivId.setVisible(true);
				}*/
				//statusPendingReportsLocationsPagingId.setAttribute("smsOremail", "email");
                //2.4.8 - starts 1
				isSmsChecked  = true;
				isemailChecked = true;
				
				uniqOpensChkId = new Checkbox();
				bouncedChkId = new Checkbox();
				sentChkId = new Checkbox();
				deleveredChkId = new Checkbox();
				clicksChkId = new Checkbox();
				sentSmsChkId = new Checkbox(); 
				receivedSmsChkId = new Checkbox();
				undeliveredSmsChkId = new Checkbox();
				statusPendingChkId = new Checkbox();
				
				
				/*emailChkId = new Checkbox();
				smsChkId = new Checkbox();*/
				
				populateEmailSmsControlsLbId();
				populateEmailSmsControlsLbIdAndSetDefaults();
				
				emailSmsControlsLbId.selectAll();
				
				//logger.info("number >>>>>>>>>> "+emailSmsControlsLbId.getSelectedItems().size());
				
				//2.4.8 - ends 1
				
				
				
				
				getEmailReports(fromDate, endDate, 0, reportsPagingId.getPageSize(), isContactDateType, null);
				//for sms reports -- rajeev
				getSmsReports(fromDate, endDate, 0, smsReportsPagingId.getPageSize(), isContactDateType, null);
				
				getStatusPendingReports(fromDate, endDate, 0, statusPendingReportsLocationsPagingId.getPageSize(), isContactDateType, null);
				
				//drawChart(isContactDateType);
				
				processSelectedEmailSmsControls();
				//long notSentCount = contactSpecificDateEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SUBMITTED);
				
				if(statusPendingChkId.isChecked()){
					if(pendingReportSize>0) pendingReportsDivId.setVisible(true);
					if(notSentCount>0){
					notSentlabelId.setVisible(true);
					notSentLblId.setVisible(true);
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}
				 
		}// doAfterCompose
		
		
		public void onClick$backBtnId() {
			
			
			Redirect.goTo(PageListEnum.REPORT_EVENT_TRIGGER_NEW_REPORTS);
			
			
		}
		
		
		public void onClick$viewSmsBtnId() {
			SMSCampaigns campaign = (SMSCampaigns)viewSmsBtnId.getAttribute("campaign");
			if(campaign == null) {
				 
				 MessageUtil.setMessage("No content found to display. Seems no campaign is configured. ", "color:red;");
				 return;
			 }
			if(campaign != null) {
				
				/*Html html = (Html)previewWin.getFellow("contentDivId").getFellow("html");
				html.setContent(campaign.getHtmlText());*/
				// Iframe  html = ( Iframe )previewIframeWin.getFellow("contentDivId").getFellow("html");
				
				 String htmlContent=campaign.getMessageContent();
				 
				 if(htmlContent.contains("href='")){
					 htmlContent = htmlContent.replaceAll("href='([^\"]+)'", "href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");
						
					}
					if(htmlContent.contains("href=\"")){
						htmlContent = htmlContent.replaceAll("href=\"([^\"]+)\"", "href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");
					}
					
					previewWin$html.setContent(htmlContent);
					previewWin.setVisible(true);
	
				 
			}
			
			
		}
		
		public void onClick$viewEmailBtnId() {
			

			Campaigns campaign = (Campaigns)viewEmailBtnId.getAttribute("campaign");
			 if(campaign == null) {
				 
				 MessageUtil.setMessage("No content found to display. Seems no campaign is configured. ", "color:red;");
				 return;
			 }
			
			if(campaign != null) {
				
				/*Html html = (Html)previewWin.getFellow("contentDivId").getFellow("html");
				html.setContent(campaign.getHtmlText());*/
				// Iframe  html = ( Iframe )previewIframeWin.getFellow("contentDivId").getFellow("html");
				
				 String htmlContent=campaign.getHtmlText();
				 if(htmlContent == null) {
					 
					 MessageUtil.setMessage("No content found to display. Seems the campaign creation is not completed. ", "color:red;");
					 return;
				 }
				 if(htmlContent.contains("href='")){
					 htmlContent = htmlContent.replaceAll("href='([^\"]+)'", "href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");
						
					}
					if(htmlContent.contains("href=\"")){
						htmlContent = htmlContent.replaceAll("href=\"([^\"]+)\"", "href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");
					}
					
					previewWin$html.setContent(htmlContent);
					previewWin.setVisible(true);
	
				 
			}
			
			
		
			
			
		}
		
		
		private Window previewWin;
		private Html previewWin$html;
		private boolean prevSettingsForsmsAndEmail=false;

		@Override
		public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
			super.onEvent(event);
			
			String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);	
			
			if(event.getTarget() instanceof Paging) {
				
				
				
				
				Paging paging = (Paging)event.getTarget();
				int desiredPage = paging.getActivePage();
				
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				
				
				if(paging.getAttribute("smsOremail").equals("sms")) {
					smsReportsPagingId.setActivePage(desiredPage);
					getSmsReports(fromDate, endDate,ofs,(byte)pagingEvent.getPageable().getPageSize(),isContactDateType, null);
				}else if(paging.getAttribute("smsOremail").equals("email")){
					reportsPagingId.setActivePage(desiredPage);
					getEmailReports(fromDate, endDate,ofs,(byte)pagingEvent.getPageable().getPageSize(),isContactDateType, null);
				}
				
				/*if(isEmail) {
				    getEmailReports(fromDate, endDate,ofs,(byte)pagingEvent.getPageable().getPageSize(),isContactDateType, null);
				}
				else {
					getSmsReports(fromDate, endDate,ofs,(byte)pagingEvent.getPageable().getPageSize(),isContactDateType, null);
				}*/
			}
			
			if (event.getTarget() instanceof Image) {
				Image img = (Image)event.getTarget();
				Listitem item  = (Listitem)img.getParent().getParent().getParent();
				String eventName = (String)img.getAttribute("imageEventName");
				String smsOrEmail = (String)img.getAttribute("smsOrEmail");
				if(smsOrEmail.equals("email")){
					isEmail = true;
				}else if(smsOrEmail.equals("sms")){
					isEmail = false;
				}
				Object etEventObj = item.getValue();
				logger.info(">>>>>>>>>>>>>>>."+etEventObj);
				ContactSpecificDateEvents contactEvent = null;
				EventTriggerEvents etEvent = null;
				Long eventId = null;
				Long sentId = null;
				
				if(etEventObj instanceof ContactSpecificDateEvents) {
					contactEvent = (ContactSpecificDateEvents)etEventObj; 
					eventId = contactEvent.getEventId();
					sentId = contactEvent.getCampSentId();
					
					logger.info("etEventObj is of ContactSpecificDateEvents type >>> eventId >>>> "+eventId);
				}
				else if(etEventObj instanceof EventTriggerEvents) {
					etEvent = (EventTriggerEvents)etEventObj; 
					eventId = etEvent.getEventId();
					 sentId = etEvent.getCampSentId();
					 
					 logger.info("etEventObj is of EventTriggerEvents type >>> eventId >>>> "+eventId);
				}
				logger.info("eventId>>>>>>>>>>>>>>>>>>>>>>"+eventId);
				String  htmlContent=null;
				if( eventName.equalsIgnoreCase("view") ) {
					if(!isContactDateType) {
					htmlContent = eventTriggerEventsDao.findEtById(isEmail, eventId );
					}
					else {
						htmlContent = contactSpecificDateEventsDao.findEtById(isEmail, eventId);
					}
					if(htmlContent == null) {
						MessageUtil.setMessage("Exception occured while fetching the "+(isEmail ?  "Email content" : "SMS content"), "color:red;");
						return;
					}
				
					if(sentId != null) {
						
						CampaignSent sentObj = campaignSentDao.findById(sentId);
						
						String contactPhValStr = sentObj.getContactPhValStr();
						logger.info("contactPlhValStr is "+contactPhValStr);
						if(contactPhValStr != null) {
							
							String[] phTokenArr = contactPhValStr.split(Constants.ADDR_COL_DELIMETER);
							String keyStr = "";
							String ValStr = "";
							for (String phToken : phTokenArr) {
								
								keyStr = phToken.substring(0, phToken.indexOf(Constants.DELIMETER_DOUBLECOLON));
								ValStr = phToken.substring(phToken.indexOf(Constants.DELIMETER_DOUBLECOLON)+Constants.DELIMETER_DOUBLECOLON.length());
								
								if(keyStr.equals(Constants.CAMPAIGN_PH_UNSUBSCRIBE_LINK)) {
									
									String unsubURL = PropertyUtil.getPropertyValue("unSubscribeUrl").replace("|^", "[").replace("^|", "]");
									String value = "<a href='"+unsubURL+ "' target=\"_blank\">unsubscribe</a>";
									htmlContent = htmlContent.replace(keyStr, value);
									continue;
									//tempTextContent = tempTextContent.replace(Constants.LEFT_SQUARE_BRACKET+cfStr+Constants.RIGHT_SQUARE_BRACKET, value);
								}
								else if(keyStr.equals(Constants.CAMPAIGN_PH_WEBPAGE_VERSION_LINK)){
									
									String webpagelink = PropertyUtil.getPropertyValue("weblinkUrl").replace("|^", "[").replace("^|", "]");
									
									String value = "<a style=\"color: inherit; text-decoration: underline; \"  href='"+webpagelink+"'" +
													" target=\"_blank\">View in web-browser</a>";
									
									htmlContent = htmlContent.replace(keyStr, value);
									continue;
									//value = "<a href="+webpagelink+">Webpage Link</a>";
								}else if(keyStr.equals(Constants.CAMPAIGN_PH_SHARE_TWEET_LINK)){
									
									String webpagelink = PropertyUtil.getPropertyValue("shareTweetLinkUrl").replace("|^", "[").replace("^|", "]");
									
									String value = "<a style=\"color: blue; text-decoration: underline; \"  href='"+webpagelink+"'" +
													" target=\"_blank\">Share on Twitter</a>";
									
									htmlContent = htmlContent.replace(keyStr, value);
									continue;
									//value = "<a href="+webpagelink+">Webpage Link</a>";
								}else if(keyStr.equals(Constants.CAMPAIGN_PH_SHARE_FACEBOOK_LINK)){
									
									String webpagelink = PropertyUtil.getPropertyValue("shareFBLinkUrl").replace("|^", "[").replace("^|", "]");
									
									String value = "<a style=\"color: blue; text-decoration: underline; \"  href='"+webpagelink+"'" +
													" target=\"_blank\">Share on Facebook</a>";
									
									htmlContent = htmlContent.replace(keyStr, value);
									continue;
									//value = "<a href="+webpagelink+">Webpage Link</a>";
								}else if(keyStr.contains(Constants.CAMPAIGN_PH_FORWRADFRIEND_LINK)) {
			    					
			    					String farwardFriendLink = PropertyUtil.getPropertyValue("forwardToFriendUrl").replace("|^", "[").replace("^|", "]");
			    					
			    					 String value = "<a href='"+farwardFriendLink+"' target=\"_blank\">Forward to Friend</a>";
			    					 htmlContent = htmlContent.replace(keyStr, value);
			    					//value = "<a href="+webpagelink+">Webpage Link</a>";
			    				}
								// to add update reference center
								else if(keyStr.contains(Constants.CAMPAIGN_PH__UPDATE_PREFERENCE_LINK)) { 
											    					
		    					String updateSubscrptionLink = PropertyUtil.getPropertyValue("updateSubscriptionLink").replace("|^", "[").replace("^|", "]");
		    					
		    					 String value = "<a href='"+updateSubscrptionLink+"' target=\"_blank\">Subscriber Preference Link</a>";
		    					 htmlContent = htmlContent.replace(keyStr, value);
		    					//value = "<a href="+webpagelink+">Webpage Link</a>";
								}
								htmlContent = htmlContent.replace(keyStr, ValStr);
							}//for
						
						}//if ph exist
						
					}
					
					if(htmlContent.contains("href='")){
						htmlContent = htmlContent.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none; color=\"color: #000000;\"\"");
						
					}
					if(htmlContent.contains("href=\"")){
						htmlContent = htmlContent.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none; color=\"color: #000000;\"\"");
					}
					
					
					
				previewWin$html.setContent(htmlContent);
				previewWin.setVisible(true);
				}
					
				else if( eventName.equalsIgnoreCase("viewAll")){
					List<Object[]> allreportsList;
					Rows parentGridRows = null;
					if(isEmail) {
						Components.removeAllChildren(viewAllWinId$viewAllEmailRowsId);
						viewAllWinId$viewEmailGridId.setVisible(true);
						viewAllWinId$viewSmsGridId.setVisible(false);
						parentGridRows = viewAllWinId$viewAllEmailRowsId;
					}
					else {
						Components.removeAllChildren(viewAllWinId$viewAllSmsRowsId);
						viewAllWinId$viewSmsGridId.setVisible(true);
						viewAllWinId$viewEmailGridId.setVisible(false);
						parentGridRows = viewAllWinId$viewAllSmsRowsId;
					}
					
					if(!isContactDateType){
						 allreportsList = eventTriggerEventsDao.findAllSentRep(isEmail, etEvent,eventTrigger.getId(), currentUser.getUserId(), fromDate, endDate );
					}
					else {
						 allreportsList = contactSpecificDateEventsDao.findAllSentRep(isEmail, contactEvent, eventTrigger.getId(), currentUser.getUserId(),  fromDate, endDate );
					}
					
					if(allreportsList == null) {
						MessageUtil.setMessage("Cannot process the request.", "color:blue;");
						logger.debug("error while fetching all reports");
						return ;
					}
					
					for (Object[] obj : allreportsList) {
					 Row row = new Row();
					 
					 Label lbl = new Label(obj[0].toString());
					 lbl.setParent(row);
					 
					 lbl = new Label(MyCalendar.calendarToString((Calendar)obj[1], MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
					 lbl.setParent(row);
					 
					 String status = obj[2] != null ? obj[2].toString() : "";
					 //lbl = new Label( !isEmail && status.equalsIgnoreCase(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED) ? SMSStatusCodes.CLICKATELL_STATUS_DELIVERY_ERROR : status);
					 lbl = new Label( !isEmail && status.equalsIgnoreCase(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED) ? SMSStatusCodes.CLICKATELL_STATUS_BOUNCED : status);
					 
					 
					 
					 lbl.setParent(row);
					 
					 row.setParent(parentGridRows);
					}
					
					viewAllWinId.setVisible(true);
					viewAllWinId.doHighlighted();
					viewAllWinId.setPosition("center");
					
				}//view all
		
			} // if
			
			
			
			
			
		}//onEvent
		
		public void fillTriggerConsolidatedMatrix(String fromDate, String endDate, boolean isContactDate, boolean isEmail) {
			
			if(isEmail) {
				
				long sentCount = 0;
				long deliveredCount = 0;
				long bouncedCount = 0;
				long spamCount = 0;
				long notSentCount=0;
				String uniqueOpens = "0";
				String uniqueClicks = "0";
				
				if(isContactDate) {
					sentCount = contactSpecificDateEventsDao.findAllEmailSentReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate);
					deliveredCount = contactSpecificDateEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, Constants.CS_STATUS_SUCCESS);
					bouncedCount = contactSpecificDateEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, Constants.CS_STATUS_BOUNCED);
					spamCount = contactSpecificDateEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SPAMMED);
					notSentCount = contactSpecificDateEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SUBMITTED);
					List<Map<String, Object>> retObject = contactSpecificDateEventsDao.findUniqueOpensAndClicks(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate);
					
					if(retObject != null) {
						uniqueOpens = retObject.get(0).get("uniqueOpens") != null ? retObject.get(0).get("uniqueOpens").toString() : "0";
						uniqueClicks = retObject.get(0).get("uniqueClicks")!= null ? retObject.get(0).get("uniqueClicks").toString() : "0";
					}
					
				}
				
				else {
					sentCount = eventTriggerEventsDao.findAllEmailSentReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate);
					deliveredCount = eventTriggerEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, Constants.CS_STATUS_SUCCESS);
					bouncedCount = eventTriggerEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, Constants.CS_STATUS_BOUNCED);
					spamCount = eventTriggerEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SPAMMED);
					notSentCount = eventTriggerEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SUBMITTED);
					List<Map<String, Object>> retObject = eventTriggerEventsDao.findUniqueOpensAndClicks(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate);// tracking 1 --rajeev
					
					if(retObject != null) {
						uniqueOpens = retObject.get(0).get("uniqueOpens") != null ? retObject.get(0).get("uniqueOpens").toString() : "0";
						uniqueClicks = retObject.get(0).get("uniqueClicks")!= null ? retObject.get(0).get("uniqueClicks").toString() : "0";
					}
					
				}
				
				sentLblId.setValue((sentCount+Constants.STRING_NILL));
				deliveredLblId.setValue(deliveredCount+" ("+getPercentage(deliveredCount, sentCount)+"%)");
				bouncedLblId.setValue((bouncedCount+Constants.STRING_NILL));
				spamLblId.setValue((spamCount+Constants.STRING_NILL));
				opensLblId.setValue(uniqueOpens+" ("+getPercentage(Integer.parseInt(uniqueOpens), deliveredCount)+"%)");
				clicksLblId.setValue(uniqueClicks+" ("+getPercentage(Integer.parseInt(uniqueClicks), deliveredCount)+"%)");
				if(notSentCount>0){
				//notSentlabelId.setVisible(true);
				//notSentLblId.setVisible(true);
				notSentLblId.setValue((notSentCount+Constants.STRING_NILL));
				}
				logger.info("notSentCnt--->"+notSentCount);
			} // if
			
			{// unconditional block jussst to keep track of prev code or else had to define new variables-- rajeev
				
				long sentCount = 0;
				long receivedCount = 0;
				//long pendingCount = 0;
				long undeliveredCount = 0;
				
				long clickedCount = 0;
				String uniqueClicks = "0";
				
				if(isContactDate) {
					sentCount = contactSpecificDateEventsDao.findAllSmsSentReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate);
					receivedCount=contactSpecificDateEventsDao.findAllSmsReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,SMSStatusCodes.CLICKATELL_STATUS_RECEIVED);
					//pendingCount=contactSpecificDateEventsDao.findAllSmsReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, SMSStatusCodes.CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT);
					undeliveredCount = contactSpecificDateEventsDao.findAllSmsReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
					List<Map<String, Object>> retObject = contactSpecificDateEventsDao.findUniqueSMSClicks(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate);
					
					if(retObject != null) {
						uniqueClicks = retObject.get(0).get("uniqueClicks")!= null ? retObject.get(0).get("uniqueClicks").toString() : "0";
					}
					
				}
				
				else {
					sentCount = eventTriggerEventsDao.findAllSmsSentReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate);
					receivedCount=eventTriggerEventsDao.findAllSmsReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, SMSStatusCodes.CLICKATELL_STATUS_RECEIVED);
					//pendingCount=eventTriggerEventsDao.findAllSmsReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,SMSStatusCodes.CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT);
					undeliveredCount = eventTriggerEventsDao.findAllSmsReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  SMSStatusCodes.CLICKATELL_STATUS_BOUNCED);
					
					
					List<Map<String, Object>> retObject = eventTriggerEventsDao.findUniqueSMSClicks(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate);
					
					if(retObject != null) {
						uniqueClicks = retObject.get(0).get("uniqueClicks")!= null ? retObject.get(0).get("uniqueClicks").toString() : "0";
					}
				}
				
				sentSmsLblId.setValue((sentCount+Constants.STRING_NILL));
				receivedSmsLblId.setValue(receivedCount+" ("+getPercentage(receivedCount, sentCount)+"%)");
				//pendingSmsLblId.setValue(pendingCount+" ("+getPercentage(pendingCount, sentCount)+"%)");
				undeliveredSmsLblId.setValue((undeliveredCount+Constants.STRING_NILL));
				
				
				clickedSmsLblId.setValue(uniqueClicks+" ("+getPercentage(Integer.parseInt(uniqueClicks), receivedCount)+"%)");
			}//else
		
		}//fillTriggerConsolidatedMatrix
			 
		
		public Calendar getStartDate(){
			Calendar serverFromDateCal = fromDateboxId.getServerValue();
			Calendar tempClientFromCal = fromDateboxId.getClientValue();
			serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
			serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
			serverFromDateCal.set(Calendar.MINUTE, 
			serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
			serverFromDateCal.set(Calendar.SECOND, 0);
			String fromDate = MyCalendar.calendarToString(serverFromDateCal, MyCalendar.FORMAT_DATETIME_STYEAR);
			return serverFromDateCal;
			
		}
	
        public Calendar getEndDate() {
			Calendar serverToDateCal = toDateboxId.getServerValue();
			Calendar tempClientToCal = toDateboxId.getClientValue();
			//change the time for startDate and endDate in order to consider right from the 
			// starting time of startDate to ending time of endDate
			
			serverToDateCal.set(Calendar.HOUR_OF_DAY, 
					23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
			serverToDateCal.set(Calendar.MINUTE, 
					59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
			serverToDateCal.set(Calendar.SECOND, 59);
			
			String endDate = MyCalendar.calendarToString(serverToDateCal, MyCalendar.FORMAT_DATETIME_STYEAR);
			return serverToDateCal;
		}
		
        public void setDateValues(){
        	
        	
        	
        	Calendar cal = MyCalendar.getNewCalendar();
    		toDateboxId.setValue(cal);
    		logger.debug("ToDate (server) :"+cal);
    		cal.set(Calendar.WEEK_OF_MONTH, cal.get(Calendar.WEEK_OF_MONTH)-1);
    		cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
    		logger.debug("FromDate (server) :"+cal);
    		fromDateboxId.setValue(cal);
    		
    		/*String deliveryReportLblIdStrFromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_MONTHDATE_ONLY);
    		String deliveryReportLblIdStrToDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_MONTHDATE_ONLY);*/
    		
    		String deliveryReportLblIdStrFromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_MONTHDATE_ONLY,clientTimeZone);
    		String deliveryReportLblIdStrToDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_MONTHDATE_ONLY,clientTimeZone);
    		
    		consolidatedMetricsLblId.setValue("Consolidated Metrics "+"("+deliveryReportLblIdStrFromDate+" - "+deliveryReportLblIdStrToDate+")");
    		deliveryOverTimeLabelId.setValue("Delivery Over Time "+"("+deliveryReportLblIdStrFromDate+" - "+deliveryReportLblIdStrToDate+")");
    		deliveryReportLblId.setValue("Delivery Report "+"("+deliveryReportLblIdStrFromDate+" - "+deliveryReportLblIdStrToDate+")");
    		
			/*Calendar toCal = Calendar.getInstance();
			toCal.setTimeZone(clientTimeZone);
			toDateboxId.setValue(toCal);
			
			Calendar fromCal = Calendar.getInstance();
			fromCal.setTimeZone(clientTimeZone);
			
			fromCal.set(Calendar.MONTH, fromCal.get(fromCal.MONTH) - 1);
			fromCal.set(Calendar.DATE, fromCal.get(fromCal.DATE) + 1);

			fromDateboxId.setValue(fromCal);*/
		}
		
		public void getEmailReports(String fromDate, String endDate, int startFrom, int size, boolean isContactDateType, String key ) {
			Long userId = currentUser.getUserId();
			Long etId = eventTrigger.getId();
			int count = etReportsLbId.getItemCount();
			
			for(; count>0; count--) {
				etReportsLbId.removeItemAt(count-1);
			}
			
			List<Object> etRepList = new ArrayList<Object>();
			
			if(isContactDateType) {
				List<ContactSpecificDateEvents> conEtRepList = contactSpecificDateEventsDao.findEmailReportsNew(userId, etId, startFrom, size, fromDate, endDate, key, emailSatusTobeFetched);
				if(conEtRepList != null ) etRepList.addAll(conEtRepList);
				
			}
			else{
				List<EventTriggerEvents> eventRepList = eventTriggerEventsDao.findEmailReportsNew(userId, etId, startFrom, size, fromDate, endDate, key, emailSatusTobeFetched);
				if(eventRepList != null) etRepList.addAll(eventRepList); 
			}
			
			if(etRepList == null ){
				logger.info("No Email reports exists");
				return;
			}
			
			for (Object obj : etRepList) {
				
				String email = "";
				Calendar sentDate = null;
				long opens = 0;
				long clicks = 0;
				String status = "";
				long sent = 0;
				long sentId= 0;
				
				Listitem li;
				Listcell lc;
				
				li = new Listitem();
				if(obj instanceof ContactSpecificDateEvents) {
					ContactSpecificDateEvents events = (ContactSpecificDateEvents)obj;
					
					 email = events.getEmailId();
					 sentDate = events.getLastSentDate();
					 opens = events.getUniqueOpens();
					 clicks = events.getUniqueClicks();
					 status = events.getLastStatus();
					 /*sent = events.getSentCount();
					 sentId= events.getEventId();*/
					
					 li.setValue(events);
				}
				
				else if(obj instanceof EventTriggerEvents) {
					EventTriggerEvents events = (EventTriggerEvents)obj;
					
					 email = events.getEmailId();
					 sentDate = events.getLastSentDate();
					 opens = events.getUniqueOpens();
					 clicks = events.getUniqueClicks();
					 status = events.getLastStatus();
					 /*sent = events.getSentCount();
					 sentId= events.getEventId();*/
					
					 li.setValue(events);
				}
			
				
				
				lc = new Listcell(MyCalendar.calendarToString(sentDate, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
				lc.setParent(li);
				
				lc = new Listcell(email);
				lc.setParent(li);
				
				lc = new Listcell(status);
				lc.setParent(li);
				
				/*lc = new Listcell(""+sent);
				lc.setParent(li);*/
				
				lc = new Listcell(""+(opens == 0 ? "0" : opens));
				lc.setParent(li);
				
				
				lc = new Listcell(""+(clicks == 0 ? "0" : clicks));
				lc.setParent(li);
				
				Hbox hbox = new Hbox();
				
				/*Image img = new Image("/img/digi_receipt_Icons/View-receipt_icn.png");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setTooltiptext("View Last Email Sent");
				img.setAttribute("imageEventName", "view");
				img.addEventListener("onClick",this);
				img.setParent(hbox);*/
				
				Image img = new Image("/img/digi_receipt_Icons/View-receipt_icn.png");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setTooltiptext("View Last Email Sent");
				img.setAttribute("imageEventName", "view");
				img.setAttribute("smsOrEmail", "email");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				lc = new Listcell();
				hbox.setParent(lc);
				
				lc.setParent(li);
				li.setParent(etReportsLbId);
			}
			
		}//getEmailReports
		
		public void getStatusPendingReports(String fromDate, String endDate, int startFrom, int size, boolean isContactDateType, String key ) {
			Long userId = currentUser.getUserId();
			Long etId = eventTrigger.getId();
			int count = statusPendingReportsLbId.getItemCount();
			
			for(; count>0; count--) {
				statusPendingReportsLbId.removeItemAt(count-1);
			}
			
			List<Object> etRepList = new ArrayList<Object>();
			
			if(isContactDateType) {
				List<ContactSpecificDateEvents> conEtRepList = contactSpecificDateEventsDao.findEmailStatusPendingReportsNew(userId, etId, startFrom, size, fromDate, endDate, key, null);
				if(conEtRepList != null ) etRepList.addAll(conEtRepList);
				
			}
			else{
				List<EventTriggerEvents> eventRepList = eventTriggerEventsDao.findStatusPendingReportsNew(userId, etId, startFrom, size, fromDate, endDate, key, null);
				if(eventRepList != null) etRepList.addAll(eventRepList); 
			}
			
			if(etRepList == null ){
				logger.info("No reports exists");
				return;
			}
			
			for (Object obj : etRepList) {
				
				String email = "";
				Calendar sentDate = null;
				
				Listitem li;
				Listcell lc;
				
				li = new Listitem();
				if(obj instanceof ContactSpecificDateEvents) {
					ContactSpecificDateEvents events = (ContactSpecificDateEvents)obj;
					
					 email = events.getEmailId();
					 sentDate = events.getLastSentDate();
					 li.setValue(events);
				}
				
				else if(obj instanceof EventTriggerEvents) {
					EventTriggerEvents events = (EventTriggerEvents)obj;
					
					 email = events.getEmailId();
					 sentDate = events.getLastSentDate();
					
					 li.setValue(events);
				}
			
				lc = new Listcell(email);
				lc.setParent(li);
				
				lc = new Listcell(MyCalendar.calendarToString(sentDate, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
				lc.setParent(li);
				
				li.setParent(statusPendingReportsLbId);
			}
			
		}//getStatusPendingReports
		
		public void getSmsReports(String fromDate, String endDate, int startFrom, int size, boolean isContactDateType, String key ) {
			
			Long userId = currentUser.getUserId();
			Long etId = eventTrigger.getId();
			int count = etReportsLbId1.getItemCount();
			
			for(; count>0; count--) {
				etReportsLbId1.removeItemAt(count-1);
			}
			
			List<Object> etRepList = new ArrayList<Object>();
			
			if(isContactDateType) {
				List<ContactSpecificDateEvents> conEtRepList = contactSpecificDateEventsDao.findSmsReportsNew(userId, etId, startFrom, size, fromDate, endDate, key, considerDeliveredStatus);
				if(conEtRepList != null ) etRepList.addAll(conEtRepList);
			}
			else{
				List<EventTriggerEvents> eventRepList = eventTriggerEventsDao.findSmsReportsNew(userId, etId, startFrom, size, fromDate, endDate, key, considerDeliveredStatus);
				if(eventRepList != null) etRepList.addAll(eventRepList); 
			}
			if(etRepList == null ){
				logger.info("No Sms reports exists");
				return;
			}
			
			for (Object obj : etRepList) {
				
				String sms = "";
				Calendar sentDate = null;
				String status = "";
				long sent = 0;
				long sentId= 0;
				Listitem li;
				Listcell lc;
				
				li = new Listitem();
				
				if(obj instanceof ContactSpecificDateEvents) {
					
					ContactSpecificDateEvents events = (ContactSpecificDateEvents)obj;
					 sms = events.getMobileNumber();
					 sentDate = events.getLastSentDate();
					 /*opens = events.getUniqueOpens();
					 clicks = events.getUniqueClicks();*/
					 status = events.getLastStatus();
					/* sent = events.getSentCount();
					 sentId= events.getEventId();*/
					 li.setValue(events);
				}
				
				else if(obj instanceof EventTriggerEvents) {
					
					EventTriggerEvents events = (EventTriggerEvents)obj;
					 sms = events.getMobileNumber();
					 sentDate = events.getLastSentDate();
					 status = events.getLastStatus();
					 /*sent = events.getSentCount();
					 sentId= events.getEventId();*/
					 li.setValue(events);
				}
				
				
				
				lc = new Listcell(MyCalendar.calendarToString(sentDate, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
				lc.setParent(li);
				
				lc = new Listcell(sms);
				lc.setParent(li);
				
				//lc = new Listcell( (status != null && status.equalsIgnoreCase(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED)) ? SMSStatusCodes.CLICKATELL_STATUS_DELIVERY_ERROR : status);
				lc = new Listcell( (status != null && status.equalsIgnoreCase(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED)) ? SMSStatusCodes.CLICKATELL_STATUS_BOUNCED : status);
				lc.setParent(li);
				
				/*
				lc = new Listcell(""+sent);
				lc.setParent(li);*/
				
				Hbox hbox = new Hbox();
				
				/*Image img = new Image("/img/digi_receipt_Icons/View-receipt_icn.png");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setTooltiptext("View Last Sms Sent");
				img.setAttribute("imageEventName", "view");
				img.addEventListener("onClick",this);
				img.setParent(hbox);*/
				
				Image img = new Image("/img/digi_receipt_Icons/View-receipt_icn.png");
				img.setStyle("margin-right:5px;cursor:pointer;");
				img.setTooltiptext("View Last SMS Sent");
				img.setAttribute("imageEventName", "view");
				img.setAttribute("smsOrEmail", "sms");
				img.addEventListener("onClick", this);
				img.setParent(hbox);
				
				lc = new Listcell();
				hbox.setParent(lc);
				
				lc.setParent(li);
				li.setParent(etReportsLbId1);
			}
		} //get SMS Reports
			
		private int pageSize=0;
		private int statusPendingPageSize=0;
		
		public void onSelect$reportsPerPageLBId() {
				
				try {
					if(isEmail) {
						emailSearchBoxId.setText("");
						int tempCount = Integer.parseInt(reportsPerPageLBId.getSelectedItem().getLabel());
						reportsPagingId.setPageSize(tempCount);
						
						String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
						String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
						
						if(!isContactDateType) {
							totalSize = eventTriggerEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,emailSatusTobeFetched );
						}
						else {
							totalSize = contactSpecificDateEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,emailSatusTobeFetched );
						}
						
						reportsPagingId.setTotalSize(totalSize);
						getEmailReports(fromDate, endDate, 0, reportsPagingId.getPageSize(),isContactDateType,null);
					}
					
					
				} catch (WrongValueException e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::" , e);
				}catch(Exception e){
					logger.error("Exception ::" , e);
				}
		}//onSelect$pageSizeLbId() 

		public void onSelect$statusPendingReportsPerPageLBId(){
		try {
			if(isEmail) {
				statusPendingSearchBoxId.setText("");
				int tempCount = Integer.parseInt(statusPendingReportsPerPageLBId.getSelectedItem().getLabel());
				statusPendingReportsLocationsPagingId.setPageSize(tempCount);
				
				String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
				
				if(!isContactDateType) {
					pendingReportSize = eventTriggerEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,"'Submmitted'" );
				}
				else {
					pendingReportSize = contactSpecificDateEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,"'Submitted'" );
				}
				
				statusPendingReportsLocationsPagingId.setTotalSize(pendingReportSize);
				getStatusPendingReports(fromDate, endDate, 0, statusPendingReportsLocationsPagingId.getPageSize(),isContactDateType,null);
			}
			
			
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
		}catch(Exception e){
			logger.error("Exception ::" , e);
		}
		}
		
		public void onSelect$smsReportsPerPageLBId() {
			
			try {
					smsSearchBoxId.setText("");
					int tempCount = Integer.parseInt(smsReportsPerPageLBId.getSelectedItem().getLabel());
					smsReportsPagingId.setPageSize(tempCount);
					
					String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
					String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
					
					if(!isContactDateType) {
						totalSize = eventTriggerEventsDao.findSearchSmsCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,considerDeliveredStatus );
					}
					else {
						totalSize = contactSpecificDateEventsDao.findSearchSmsCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,considerDeliveredStatus );
					}
					
					smsReportsPagingId.setTotalSize(totalSize);
					getSmsReports(fromDate, endDate, 0, smsReportsPagingId.getPageSize(),isContactDateType,null);
				
			} catch (WrongValueException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}catch(Exception e){
				logger.error("Exception ::" , e);
			}
	}//onSelect$pageSizeLbId() 
		
		
		


		public void onClick$datesResetBtnId(){
			
			try {etReportsChartId.setEngine(new LineChartEngine());
			
			/*uniqOpensChkId.setChecked(false);
			clicksChkId.setChecked(false);
			bouncedChkId.setChecked(false);
			deleveredChkId.setChecked(true);
			sentChkId.setChecked(false);
			sentSmsChkId.setChecked(false);
			receivedSmsChkId.setChecked(true);
			undeliveredSmsChkId.setChecked(false);*/
			
			populateEmailSmsControlsLbIdAndSetDefaults();
			
			notSentlabelId.setVisible(false);
			notSentLblId.setVisible(false);
			pendingReportsDivId.setVisible(false);
			
			emailSatusTobeFetched="'Unsubscribed','Spammed','Success','Bounced','special_condtion_for_all'";
			//emailSatusTobeFetched="'Unsubscribed','Spammed','Success','Bounced','Submitted','special_condtion_for_all'";
			considerDeliveredStatus = 2;
			//Default DateSettings
			int trType = eventTrigger.getTrType();
			isContactDateType = ( (trType & Constants.ET_TYPE_ON_CONTACT_DATE) == Constants.ET_TYPE_ON_CONTACT_DATE );
			triggerNameLblId.setValue(eventTrigger.getTriggerName());
			triggerTypeLblId.setValue(TRIGGER_TYPE_MAP.get(eventTrigger.getTrType().toString()));
			triggerCreatedLblId.setValue(MyCalendar.calendarToString(eventTrigger.getTriggerCreatedDate(), MyCalendar.FORMAT_STDATE));
			
			if(eventTrigger.getCampaignId() != null) {
				
				CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
				

				
				
			
				
				List<Campaigns> campLst = campaignsDao.getCampaignById(eventTrigger.getCampaignId().longValue()+"");
				if(campLst != null) {
				
					for (Campaigns camp : campLst) {
						
						
						emailNameLblId.setValue(camp != null ? camp.getCampaignName() : Constants.STRING_NILL);
						subjectLblId.setValue(camp != null ? camp.getSubject() : Constants.STRING_NILL );
						subjectLblId.setTooltiptext(camp != null ? camp.getSubject() : Constants.STRING_NILL);
						viewEmailBtnId.setAttribute("campaign", camp);
						break;
					}
				}
			}//i
			
			if(eventTrigger.getSmsId() != null) {
				SMSCampaignsDao campaignsDao = (SMSCampaignsDao)SpringUtil.getBean("smsCampaignsDao");
				SMSCampaigns camp = campaignsDao.findByCampaignId(eventTrigger.getSmsId());
				smsNameLblId.setValue(camp != null ? camp.getSmsCampaignName() : Constants.STRING_NILL);
				
				viewSmsBtnId.setAttribute("campaign", camp);
				
			}
			
			setDateValues();
			
			String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			
			if(!isContactDateType) {
				totalSize = eventTriggerEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,null,emailSatusTobeFetched );
				
			}else {
				
				totalSize = contactSpecificDateEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,emailSatusTobeFetched );
			}
			
			pageSize = Integer.parseInt(reportsPerPageLBId.getSelectedItem().getLabel());
			reportsPagingId.setTotalSize(totalSize);
			reportsPagingId.setPageSize(pageSize);
			reportsPagingId.setActivePage(0);
			reportsPagingId.addEventListener("onPaging", this);
			
			long notSentCount = 0;
			if(!isContactDateType) {
				pendingReportSize = eventTriggerEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,"'Submitted'" );
				notSentCount=eventTriggerEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SUBMITTED);
				}else{
					pendingReportSize = contactSpecificDateEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,"'Submitted'" );
					notSentCount=contactSpecificDateEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SUBMITTED);
				}
				statusPendingReportsLocationsPagingId.addEventListener("onPaging", this);
				statusPendingReportsLocationsPagingId.setTotalSize(pendingReportSize);
				statusPendingReportsLocationsPagingId.setPageSize(Integer.parseInt(statusPendingReportsPerPageLBId.getSelectedItem().getLabel()));
				statusPendingReportsLocationsPagingId.setActivePage(0);
				if(statusPendingChkId.isChecked() && pendingReportSize>0){
					pendingReportsDivId.setVisible(true);
				}
				//notSentCount=contactSpecificDateEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SUBMITTED);
				if(statusPendingChkId.isChecked() && notSentCount>0){
					notSentlabelId.setVisible(true);
					notSentLblId.setVisible(true);
					notSentLblId.setValue((notSentCount+Constants.STRING_NILL));
					}
				logger.info("notSentCnt--->"+notSentCount);
			isEmail=true;
			fillTriggerConsolidatedMatrix(fromDate, endDate, isContactDateType, isEmail);
			getEmailReports(fromDate, endDate, 0, reportsPagingId.getPageSize(), isContactDateType, null); //check once --rajeev
			getStatusPendingReports(fromDate, endDate, 0, statusPendingReportsLocationsPagingId.getPageSize(), isContactDateType, null);
			

			if(!isContactDateType) {
			 totalSize = eventTriggerEventsDao.findSearchSmsCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,null,considerDeliveredStatus );
			}
			else {
				totalSize = contactSpecificDateEventsDao.findSearchSmsCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,null,considerDeliveredStatus );
			}
			
		
			pageSize = Integer.parseInt(smsReportsPerPageLBId.getSelectedItem().getLabel());
			smsReportsPagingId.setTotalSize(totalSize);
			smsReportsPagingId.setPageSize(pageSize);
			smsReportsPagingId.setActivePage(0);
		
			//fillTriggerConsolidatedMatrix(fromDate, endDate, isContactDateType, isEmail);
			//drawSmsChart(isContactDateType);
			getSmsReports(fromDate, endDate, 0, smsReportsPagingId.getPageSize(), isContactDateType, null);
		
		
			//drawChart(isContactDateType);
			
			
			emailSmsControlsLbId.selectAll();
			
			
			int storeSelectedCount = emailSmsControlsLbId.getSelectedCount();
			int storeItemCount = emailSmsControlsLbId.getItemCount();
	        if(storeSelectedCount == storeItemCount){
	        	storeBandBoxId.setValue("All");
	        }
	        else if((storeSelectedCount>1) && !(storeSelectedCount == storeItemCount)){
				storeBandBoxId.setValue("Multiple");
			}
	        else if((storeSelectedCount==1) && !(storeSelectedCount == storeItemCount)){
	        	storeBandBoxId.setValue(emailSmsControlsLbId.getSelectedItem().getLabel());
	        }
	        else if(emailSmsControlsLbId.getSelectedIndex() == -1){
				storeBandBoxId.setValue("");
			}
			
			processSelectedEmailSmsControls();
			
			
			} catch (Exception e) {
				// TODO: handle exception
				
				logger.error("Exception ::", e);
			}
			
		}
		public void onClick$datesFilterBtnId(){
			
			Calendar fromCal = getStartDate();
			Calendar endCal = getEndDate();
			
			String fromDate = MyCalendar.calendarToString(fromCal, MyCalendar.FORMAT_DATETIME_STYEAR);
			String endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);	
			
			if(endCal.before(fromCal) ){
				MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red");
				return;
			}
			
			int diffDays =  (int) ((endCal.getTime().getTime() - fromCal.getTime().getTime() ) / (1000*60*60*24));
			
			/*if(diffDays > 150 ) {
				MessageUtil.setMessage("Please select Sent Date range within 5 months.", "color:green");
				return;
			} */                    
			if(diffDays > 92 ) { //APP-548
				MessageUtil.setMessage("Please select Sent Date range within 3 months(92 days).", "color:green");
				return;
			}
			
			notSentlabelId.setVisible(false);
			notSentLblId.setVisible(false);
			pendingReportsDivId.setVisible(false);
			
			int storeSelectedCount = emailSmsControlsLbId.getSelectedCount();
			int storeItemCount = emailSmsControlsLbId.getItemCount();
	        if(storeSelectedCount == storeItemCount){
	        	storeBandBoxId.setValue("All");
	        }
	        else if((storeSelectedCount>1) && !(storeSelectedCount == storeItemCount)){
				storeBandBoxId.setValue("Multiple");
			}
	        else if((storeSelectedCount==1) && !(storeSelectedCount == storeItemCount)){
	        	storeBandBoxId.setValue(emailSmsControlsLbId.getSelectedItem().getLabel());
	        }
	        else if(emailSmsControlsLbId.getSelectedIndex() == -1){
				storeBandBoxId.setValue("");
			}

			String deliveryReportLblIdStrFromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_MONTHDATE_ONLY,clientTimeZone);
		    String deliveryReportLblIdStrToDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_MONTHDATE_ONLY,clientTimeZone);
			
			//String deliveryReportLblIdStrFromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_MONTHDATE_ONLY);
		    //String deliveryReportLblIdStrToDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_MONTHDATE_ONLY);
			//if(reptypeLbId.getSelectedItem().getValue().equals("EMAIL")) {
			deliveryReportLblId.setValue("Delivery Report "+"("+deliveryReportLblIdStrFromDate+" - "+deliveryReportLblIdStrToDate+")");
			
			consolidatedMetricsLblId.setValue("Consolidated Metrics "+"("+deliveryReportLblIdStrFromDate+" - "+deliveryReportLblIdStrToDate+")");
    		deliveryOverTimeLabelId.setValue("Delivery Over Time "+"("+deliveryReportLblIdStrFromDate+" - "+deliveryReportLblIdStrToDate+")");
			
				isEmail=true;
				emailGridDivId.setVisible(true);
				emailCheckDivId.setVisible(true);
				emailChartDivId.setVisible(true);
				emailListDivId.setVisible(true);
				//emailInfoDivId.setVisible(true);
				/*smsGridDivId.setVisible(false);
				smsCheckDivId.setVisible(false);
				smsChartDivId.setVisible(false);
				smsListDivId.setVisible(false);
				smsInfoDivId.setVisible(false);*/
				setRequiredStatusToBeFetched();
				if(!isContactDateType) {
					 totalSize = eventTriggerEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,null,emailSatusTobeFetched );
					}
					else {
						totalSize = contactSpecificDateEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,null,emailSatusTobeFetched );
					}
				
				logger.info("totalSize for email >>>>>>>>>>>>>>>>"+totalSize);
				reportsPagingId.setTotalSize(totalSize);
				reportsPagingId.setPageSize(pageSize);
				reportsPagingId.setActivePage(0);
				
				long notSentCount = 0;
				if(!isContactDateType) {
					pendingReportSize = eventTriggerEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,"'Submitted'" );
					notSentCount=eventTriggerEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SUBMITTED);
					}else{
						pendingReportSize = contactSpecificDateEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,"'Submitted'" );
						notSentCount=contactSpecificDateEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SUBMITTED);
					}
				logger.info("totalSize for email >>>>>>>>>>>>>>>>"+pendingReportSize);
				//drawChart(isContactDateType);
				
				//setRequiredStatusToBeFetched();
				processSelectedEmailSmsControls();
				fillTriggerConsolidatedMatrix(fromDate, endDate, isContactDateType, isEmail);
				
				statusPendingReportsLocationsPagingId.setTotalSize(pendingReportSize);
				statusPendingReportsLocationsPagingId.setPageSize(Integer.parseInt(statusPendingReportsPerPageLBId.getSelectedItem().getLabel()));
				statusPendingReportsLocationsPagingId.setActivePage(0);
				if(statusPendingChkId.isChecked() && pendingReportSize>0){
					pendingReportsDivId.setVisible(true);
				}
				
				getEmailReports(fromDate, endDate, 0, reportsPagingId.getPageSize(), isContactDateType, null);
				getStatusPendingReports(fromDate, endDate, 0, statusPendingReportsLocationsPagingId.getPageSize(), isContactDateType, null);
				//notSentCount=contactSpecificDateEventsDao.findAllEmailReport(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,  Constants.CS_STATUS_SUBMITTED);
				if(statusPendingChkId.isChecked() && notSentCount>0){
					notSentlabelId.setVisible(true);
					notSentLblId.setVisible(true);
					notSentLblId.setValue((notSentCount+Constants.STRING_NILL));
					}
				logger.info("notSentCnt--->"+notSentCount);
			//}
			
			//else if(reptypeLbId.getSelectedItem().getValue().equals("SMS")) {
				
				isEmail=false;
				/*smsGridDivId.setVisible(true);
				smsCheckDivId.setVisible(true);
				smsChartDivId.setVisible(true);
				smsListDivId.setVisible(true);
				smsInfoDivId.setVisible(true);
				emailGridDivId.setVisible(false);
				emailCheckDivId.setVisible(false);
				emailChartDivId.setVisible(false);
				emailSearchDivId.setVisible(false);
				emailListDivId.setVisible(false);
				emailInfoDivId.setVisible(false);*/
				
					if(!isContactDateType) {
					 totalSize = eventTriggerEventsDao.findSearchSmsCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,null,considerDeliveredStatus );
					}
					else {
						totalSize = contactSpecificDateEventsDao.findSearchSmsCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,null,considerDeliveredStatus );
					}
					
				logger.info("totalSize for sms >>>>>>>>>>>>>>>>"+totalSize);
				smsReportsPagingId.setTotalSize(totalSize);
				smsReportsPagingId.setPageSize(pageSize);
				smsReportsPagingId.setActivePage(0);
				
				//fillTriggerConsolidatedMatrix(fromDate, endDate, isContactDateType, isEmail);
				//drawSmsChart(isContactDateType);
				getSmsReports(fromDate, endDate, 0, smsReportsPagingId.getPageSize(), isContactDateType, null); //-- once chek this-- rajeev
			//}
			
		} // onClick$datesFilterBtnId
		
		public String getPercentage(long amount,long totalAmount) {
			try {
				return Utility.getPercentage(((Long)amount).intValue(), totalAmount, 2);
			} catch (RuntimeException e) {
				logger.error("** Exception ", (Throwable)e);
				return "";
			}
		} // getPercentage
		
		private Checkbox uniqOpensChkId,clicksChkId,bouncedChkId,deleveredChkId,sentChkId,sentSmsChkId,receivedSmsChkId,pendingSmsChkId,undeliveredSmsChkId,emailChkId,smsChkId,statusPendingChkId;
		private Object startDate;
		
			public Chart drawChart(boolean isContactDate) {
			
			try {
				
				Long etId = eventTrigger.getId();
				Long userId = currentUser.getUserId(); 
				
				Calendar startDate = getStartDate();
				Calendar endDate1 = getEndDate();
				
				String startDateStr1 = MyCalendar.calendarToString(startDate, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDateStr1 = MyCalendar.calendarToString(endDate1, MyCalendar.FORMAT_DATETIME_STYEAR);	
				
				int maxDays = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);
				int diffDays =  (int) ((endDate1.getTime().getTime() - startDate.getTime().getTime() ) / (1000*60*60*24));
				int monthsDiff = (   
						(endDate1.get(Calendar.YEAR)*12 +endDate1.get(Calendar.MONTH))  -(startDate.get(Calendar.YEAR)*12 + startDate.get(Calendar.MONTH)))+1;
				//(int) ((endDate1.getTime().getTime() - startDate.getTime().getTime() ) / (1000*60*60*24*maxDays));
				String type="";
				
				if(diffDays >= maxDays ) {
					 type=ET_TYPE_MONTHS;
					etReportsChartId.setXAxis(type);
				}
				else {
					 type=ET_TYPE_DAYS;
					 etReportsChartId.setXAxis(type);
				}
			
				//logger.info("DiffDays=="+diffDays+" Type="+type +" MaxDays="+maxDays);
				
				CategoryModel model = new SimpleCategoryModel();
				
				Map<String, Integer> sentMap = new HashMap<String, Integer>();
				Map<String, Integer> delMap = new HashMap<String, Integer>();
				Map<String, Integer> opensMap = new HashMap<String, Integer>();
				Map<String, Integer> clicksMap = new HashMap<String, Integer>();
				Map<String, Integer> bounceMap = new HashMap<String, Integer>();
				Map<String, Integer> statusPendingMap = new HashMap<String, Integer>();
				
				
				////new implementation sms+email  -- rajeev
				//sms related -- rajeev
				Map<String, Integer> smsSentMap = new HashMap<String, Integer>();
				Map<String, Integer> recMap = new HashMap<String, Integer>();
				//Map<String, Integer> penMap = new HashMap<String, Integer>();
				Map<String, Integer> undelMap = new HashMap<String, Integer>();
				
				if(sentChkId.isChecked()) {
					
					List<Object[]>  sentRates = null;
					if(	isContactDate ) {
						sentRates = contactSpecificDateEventsDao.findTotEmailRepRate(userId ,etId, startDateStr1, endDateStr1, null  ,type, true);
						
					}
					else {
						sentRates = eventTriggerEventsDao.findTotEmailRepRate(userId, etId, startDateStr1, endDateStr1, null, type, true);
					}
					if(sentRates != null) {
						for(Object[] obj : sentRates) {
							sentMap.put(obj[1].toString(), Integer.parseInt(obj[0].toString()));
							
						} // for 
					} // if
				}// if

				if(deleveredChkId.isChecked()) {
					
					List<Object[]>  delRates = null;
					if(	isContactDate ) {
						delRates = contactSpecificDateEventsDao.findTotEmailRepRate(userId, etId, startDateStr1, endDateStr1, Constants.CS_STATUS_SUCCESS, type, false);
					}
					else {
						delRates = eventTriggerEventsDao.findTotEmailRepRate(userId, etId, startDateStr1, endDateStr1, Constants.CS_STATUS_SUCCESS, type, false);
					}
					if(delRates != null) {
						for(Object[] obj : delRates) {
							delMap.put(obj[1].toString(), Integer.parseInt(obj[0].toString()));
						} // for
					} // if
				} // if
				
				if(bouncedChkId.isChecked()) {
					
					List<Object[]>  bounceRates = null;
					if(	isContactDate ) {
						bounceRates = contactSpecificDateEventsDao.findTotEmailRepRate(userId, etId, startDateStr1,
								endDateStr1, Constants.CS_STATUS_BOUNCED, type, false);
						
						
					}
					else {
						bounceRates = eventTriggerEventsDao.findTotEmailRepRate(userId, etId, startDateStr1, endDateStr1, 
								Constants.CS_STATUS_BOUNCED, type, false);
						
					}
					if(bounceRates != null) {
						for(Object[] obj : bounceRates) {
							bounceMap.put(obj[1].toString(), Integer.parseInt(obj[0].toString()));
						} // for
					} // if
				} // if
				if(statusPendingChkId.isChecked()) {
					
					List<Object[]>  statsuPendingRates = null;
					if(	isContactDate ) {
						statsuPendingRates = contactSpecificDateEventsDao.findTotEmailRepRate(userId, etId, startDateStr1,
								endDateStr1, Constants.CS_STATUS_SUBMITTED, type, false);
						
						
					}
					else {
						statsuPendingRates = eventTriggerEventsDao.findTotEmailRepRate(userId, etId, startDateStr1, endDateStr1, 
								Constants.CS_STATUS_SUBMITTED, type, false);
						
					}
					if(statsuPendingRates != null) {
						for(Object[] obj : statsuPendingRates) {
							statusPendingMap.put(obj[1].toString(), Integer.parseInt(obj[0].toString()));
						} // for
					} // if
				} // if
				
				if(uniqOpensChkId.isChecked() || clicksChkId.isChecked()) {
					
					List<Map<String, Object>>  opensClickRates = null;
					if(	isContactDate ) {
					opensClickRates = contactSpecificDateEventsDao.findUniqueOpensAndClicks(userId, etId, startDateStr1,
										endDateStr1, type);
					}
					else {
						opensClickRates = eventTriggerEventsDao.findUniqueOpensAndClicks(userId, etId, startDateStr1,
								endDateStr1, type);
					}
					if(opensClickRates != null) {
						for (Map<String, Object> map : opensClickRates) {
							if(uniqOpensChkId.isChecked()) {
								opensMap.put(map.get("uniqueOpens") != null ? map.get("type").toString() : "0", Integer.parseInt(map.get("uniqueOpens").toString()));
							}//if
							
							if(clicksChkId.isChecked()) {
								clicksMap.put(map.get("uniqueClicks") != null ? map.get("type").toString() : "0", Integer.parseInt(map.get("uniqueClicks").toString()));
							}
						}
					} 
				}//if
				
				
				////new implementation sms+email  -- rajeev
				//sms related -- rajeev
				if(sentSmsChkId.isChecked()) {
					
					List<Object[]>  sentRates = null;
					if(	isContactDate ) {
						sentRates = contactSpecificDateEventsDao.findTotSmsRepRate(userId ,etId, startDateStr1, endDateStr1, null  ,type, true);
					}
					else {
						sentRates = eventTriggerEventsDao.findTotSmsRepRate(userId, etId, startDateStr1, endDateStr1, null, type, true);
					}
					if(sentRates != null) {
						for(Object[] obj : sentRates) {
							smsSentMap.put(obj[1].toString(), Integer.parseInt(obj[0].toString()));
						} // for 
					}
				} // if

				if(receivedSmsChkId.isChecked()) {
					
					List<Object[]>  recRates = null;
					if(	isContactDate ) {
						recRates = contactSpecificDateEventsDao.findTotSmsRepRate(userId, etId, startDateStr1, endDateStr1,SMSStatusCodes.CLICKATELL_STATUS_RECEIVED, type, false);
					}
					else {
						recRates = eventTriggerEventsDao.findTotSmsRepRate(userId, etId, startDateStr1, endDateStr1,SMSStatusCodes.CLICKATELL_STATUS_RECEIVED, type, false);
					}
					if(recRates != null) {
						for(Object[] obj : recRates) {
							recMap.put(obj[1].toString(), Integer.parseInt(obj[0].toString()));
						} // for
					}
				} // if
				
				/*if(pendingSmsChkId.isChecked()) {
					
					List<Object[]>  penRates = null;
					if(	isContactDate ) {
						penRates = contactSpecificDateEventsDao.findTotSmsRepRate(userId, etId, startDateStr1, endDateStr1,SMSStatusCodes.CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT, type, false);
					}
					else {
						penRates = eventTriggerEventsDao.findTotSmsRepRate(userId, etId, startDateStr1, endDateStr1,SMSStatusCodes.CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT, type, false);
					}
					if(penRates != null) {
						for(Object[] obj : penRates) {
							penMap.put(obj[1].toString(), Integer.parseInt(obj[0].toString()));
						} // for
					}
				} //if */
				
				if(undeliveredSmsChkId.isChecked()) {
					
					List<Object[]> undeliveredRates = null;
					if(	isContactDate ) {
						undeliveredRates = contactSpecificDateEventsDao.findTotSmsRepRate(userId, etId, startDateStr1,
								endDateStr1, SMSStatusCodes.CLICKATELL_STATUS_BOUNCED, type, false);
					}
					else {
						undeliveredRates= eventTriggerEventsDao.findTotSmsRepRate(userId, etId, startDateStr1, endDateStr1, 
								 SMSStatusCodes.CLICKATELL_STATUS_BOUNCED, type, false);
					}
					if(undeliveredRates != null) {
						for(Object[] obj : undeliveredRates) {
							undelMap.put(obj[1].toString(), Integer.parseInt(obj[0].toString()));
						} // for
					}
				}// if
				
				Calendar tempCal = Calendar.getInstance();
				tempCal.setTimeInMillis(startDate.getTimeInMillis());
				String currDate="";
				//int i = 0;
				if(type.equals(ET_TYPE_DAYS)) {
					do {
							//logger.debug("in if days ========");
							currDate  = ""+tempCal.get(startDate.DATE);
							
							if(sentChkId.isChecked()) {
								//logger.debug(" ========"+sentMap.get(currDate));
								model.setValue("Emails Sent", currDate, sentMap.containsKey(currDate) ? sentMap.get(currDate) : 0);
							}
							if(deleveredChkId.isChecked()) model.setValue("Emails Delivered", currDate, delMap.containsKey(currDate) ? delMap.get(currDate) : 0);
							if(bouncedChkId.isChecked()) model.setValue("Emails Bounced", currDate, bounceMap.containsKey(currDate) ? bounceMap.get(currDate) : 0);
							if(uniqOpensChkId.isChecked()) model.setValue("Emails Unique Opens", currDate, opensMap.containsKey(currDate) ? opensMap.get(currDate) : 0);
							if(clicksChkId.isChecked()) model.setValue("Emails Unique Clicks", currDate, clicksMap.containsKey(currDate) ? clicksMap.get(currDate) : 0);
							if(statusPendingChkId.isChecked()) model.setValue("Delivery Status Pending", currDate, statusPendingMap.containsKey(currDate) ? statusPendingMap.get(currDate) : 0);
							
							//sms related -- rajeev
							if(sentSmsChkId.isChecked()) model.setValue("SMS Sent", currDate, smsSentMap.containsKey(currDate) ? smsSentMap.get(currDate) : 0);
							if(receivedSmsChkId.isChecked()) model.setValue("SMS Delivered", currDate, recMap.containsKey(currDate) ? recMap.get(currDate) : 0);
/*							if(pendingSmsChkId.isChecked()) model.setValue("Pending", currDate, penMap.containsKey(currDate) ? penMap.get(currDate) : 0);*/
							if(undeliveredSmsChkId.isChecked()) model.setValue("SMS Bounced", currDate, undelMap.containsKey(currDate) ? undelMap.get(currDate) : 0);
							
							tempCal.set(Calendar.DATE, tempCal.get(Calendar.DATE) + 1);
							//logger.debug("after increasing tempCal ::"+tempCal.get(Calendar.DATE));
					} while(tempCal.before(endDate1) || tempCal.equals(endDate1));
				}
				else if(type.equals(ET_TYPE_MONTHS)) {

					
					/*int i=1;
					logger.debug("executing ========"+monthsDiff);
					do {
						//logger.debug("executing ========"+monthsDiff);
						i++;
						
							//logger.debug("in if months ========");
							currDate = ""+(tempCal.get(startDate.MONTH)+1);
							logger.debug("currDate ========"+currDate);
							logger.debug("MONTH_MAP.get(currDate) ========"+MONTH_MAP.get(currDate));
							
	
							if(sentChkId.isChecked()) model.setValue("Sent", MONTH_MAP.get(currDate), sentMap.containsKey(currDate) ? sentMap.get(currDate) : 0);
							if(deleveredChkId.isChecked()) model.setValue("Delivered", MONTH_MAP.get(currDate), delMap.containsKey(currDate) ? delMap.get(currDate) : 0);
							if(bouncedChkId.isChecked()) model.setValue("Bounced", MONTH_MAP.get(currDate), bounceMap.containsKey(currDate) ? bounceMap.get(currDate) : 0);
							if(uniqOpensChkId.isChecked()) model.setValue("Unique Opens", MONTH_MAP.get(currDate), opensMap.containsKey(currDate) ? opensMap.get(currDate) : 0);
							if(clicksChkId.isChecked()) model.setValue("Unique Clicks", MONTH_MAP.get(currDate), clicksMap.containsKey(currDate) ? clicksMap.get(currDate) : 0);
							
							tempCal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH) + 1);
							logger.debug("after increasing tempCal ::"+tempCal.get(Calendar.MONTH));
							
					} while(i<= monthsDiff);*/
					
					int i=1;
					logger.debug("executing ========"+monthsDiff);
					logger.debug("startDate ========"+startDate);
					do {
						//logger.debug("executing ========"+monthsDiff);
						//i++;
						
							//logger.debug("in if months ========");
						/*if(i==1){
							currDate = ""+(tempCal.get(startDate.MONTH)+1);
						}else{
							currDate = ""+(tempCal.get(startDate.MONTH));
						}*/
							
						
							currDate = ""+(tempCal.get(startDate.MONTH)+1);
							logger.debug("currDate ========"+currDate);
							logger.debug("MONTH_MAP.get(currDate) ========"+MONTH_MAP.get(currDate));
							
	
							if(sentChkId.isChecked()) model.setValue("Emails Sent", MONTH_MAP.get(currDate), sentMap.containsKey(currDate) ? sentMap.get(currDate) : 0);
							if(deleveredChkId.isChecked()) model.setValue("Emails Delivered", MONTH_MAP.get(currDate), delMap.containsKey(currDate) ? delMap.get(currDate) : 0);
							if(bouncedChkId.isChecked()) model.setValue("Emails Bounced", MONTH_MAP.get(currDate), bounceMap.containsKey(currDate) ? bounceMap.get(currDate) : 0);
							if(uniqOpensChkId.isChecked()) model.setValue("Emails Unique Opens", MONTH_MAP.get(currDate), opensMap.containsKey(currDate) ? opensMap.get(currDate) : 0);
							if(clicksChkId.isChecked()) model.setValue("Emails Unique Clicks", MONTH_MAP.get(currDate), clicksMap.containsKey(currDate) ? clicksMap.get(currDate) : 0);
							if(statusPendingChkId.isChecked()) model.setValue("Delivery Status Pending", MONTH_MAP.get(currDate), statusPendingMap.containsKey(currDate) ? statusPendingMap.get(currDate) : 0);
							
							
							//sms related -- rajeev
							if(sentSmsChkId.isChecked()) model.setValue("SMS Sent", MONTH_MAP.get(currDate), smsSentMap.containsKey(currDate) ? smsSentMap.get(currDate) : 0);
							if(receivedSmsChkId.isChecked()) model.setValue("SMS Delivered", MONTH_MAP.get(currDate), recMap.containsKey(currDate) ? recMap.get(currDate) : 0);
/*							if(pendingSmsChkId.isChecked()) model.setValue("Pending", currDate, penMap.containsKey(currDate) ? penMap.get(currDate) : 0);*/
							if(undeliveredSmsChkId.isChecked()) model.setValue("SMS Bounced", MONTH_MAP.get(currDate), undelMap.containsKey(currDate) ? undelMap.get(currDate) : 0);
							
							tempCal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH) + 1);
							logger.debug("after increasing tempCal ::"+tempCal.get(Calendar.MONTH)+1);
							i++;
							
					} while(i<= monthsDiff);
					
				 
				}


				etReportsChartId.setModel(model);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}
				return etReportsChartId;
		} // drawChart
		
		public Chart drawSmsChart(boolean isContactDate) {
			
			try {
				Long etId = eventTrigger.getId();
				Long userId = currentUser.getUserId(); 
				
				Calendar startDate = getStartDate();
				Calendar endDate1 = getEndDate();
				
				String startDateStr1 = MyCalendar.calendarToString(startDate, MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDateStr1 = MyCalendar.calendarToString(endDate1, MyCalendar.FORMAT_DATETIME_STYEAR);	
				
				int diffDays =  (int) ((endDate1.getTime().getTime() - startDate.getTime().getTime() ) / (1000*60*60*24));
				int maxDays = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);
				int monthsDiff = (   
						(endDate1.get(Calendar.YEAR)*12 +endDate1.get(Calendar.MONTH))  -(startDate.get(Calendar.YEAR)*12 + startDate.get(Calendar.MONTH)))+1;
			
				String type="";
				
				if(diffDays >= maxDays ) {
					 type=ET_TYPE_MONTHS;
					 //etReportsSmsChartId.setXAxis(type);
					 etReportsChartId.setXAxis(type);
				}
				else {
					 type=ET_TYPE_DAYS;
					 //etReportsSmsChartId.setXAxis(type);
					 etReportsChartId.setXAxis(type);
				}
			
				//logger.info("DiffDays=="+diffDays+" Type="+type +" MaxDays="+maxDays);
				
				CategoryModel model = new SimpleCategoryModel();
				
				Map<String, Integer> sentMap = new HashMap<String, Integer>();
				Map<String, Integer> recMap = new HashMap<String, Integer>();
				Map<String, Integer> penMap = new HashMap<String, Integer>();
				Map<String, Integer> undelMap = new HashMap<String, Integer>();
				
				if(sentSmsChkId.isChecked()) {
					
					List<Object[]>  sentRates = null;
					if(	isContactDate ) {
						sentRates = contactSpecificDateEventsDao.findTotSmsRepRate(userId ,etId, startDateStr1, endDateStr1, null  ,type, true);
					}
					else {
						sentRates = eventTriggerEventsDao.findTotSmsRepRate(userId, etId, startDateStr1, endDateStr1, null, type, true);
					}
					if(sentRates != null) {
						for(Object[] obj : sentRates) {
							sentMap.put(obj[1].toString(), Integer.parseInt(obj[0].toString()));
						} // for 
					}
				} // if

				if(receivedSmsChkId.isChecked()) {
					
					List<Object[]>  recRates = null;
					if(	isContactDate ) {
						recRates = contactSpecificDateEventsDao.findTotSmsRepRate(userId, etId, startDateStr1, endDateStr1,SMSStatusCodes.CLICKATELL_STATUS_RECEIVED, type, false);
					}
					else {
						recRates = eventTriggerEventsDao.findTotSmsRepRate(userId, etId, startDateStr1, endDateStr1,SMSStatusCodes.CLICKATELL_STATUS_RECEIVED, type, false);
					}
					if(recRates != null) {
						for(Object[] obj : recRates) {
							recMap.put(obj[1].toString(), Integer.parseInt(obj[0].toString()));
						} // for
					}
				} // if
				
				/*if(pendingSmsChkId.isChecked()) {
					
					List<Object[]>  penRates = null;
					if(	isContactDate ) {
						penRates = contactSpecificDateEventsDao.findTotSmsRepRate(userId, etId, startDateStr1, endDateStr1,SMSStatusCodes.CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT, type, false);
					}
					else {
						penRates = eventTriggerEventsDao.findTotSmsRepRate(userId, etId, startDateStr1, endDateStr1,SMSStatusCodes.CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT, type, false);
					}
					if(penRates != null) {
						for(Object[] obj : penRates) {
							penMap.put(obj[1].toString(), Integer.parseInt(obj[0].toString()));
						} // for
					}
				} //if */
				
				if(undeliveredSmsChkId.isChecked()) {
					
					List<Object[]> undeliveredRates = null;
					if(	isContactDate ) {
						undeliveredRates = contactSpecificDateEventsDao.findTotSmsRepRate(userId, etId, startDateStr1,
								endDateStr1, SMSStatusCodes.CLICKATELL_STATUS_BOUNCED, type, false);
					}
					else {
						undeliveredRates= eventTriggerEventsDao.findTotSmsRepRate(userId, etId, startDateStr1, endDateStr1, 
								 SMSStatusCodes.CLICKATELL_STATUS_BOUNCED, type, false);
					}
					if(undeliveredRates != null) {
						for(Object[] obj : undeliveredRates) {
							undelMap.put(obj[1].toString(), Integer.parseInt(obj[0].toString()));
						} // for
					}
				}// if
				
				Calendar tempCal = Calendar.getInstance();
				tempCal.setTimeInMillis(startDate.getTimeInMillis());
				String currDate="";

				if(type.equals(ET_TYPE_DAYS)) {
					do {
							currDate  = ""+tempCal.get(startDate.DATE);
							
							if(sentSmsChkId.isChecked()) model.setValue("Sent", currDate, sentMap.containsKey(currDate) ? sentMap.get(currDate) : 0);
							if(receivedSmsChkId.isChecked()) model.setValue("Received", currDate, recMap.containsKey(currDate) ? recMap.get(currDate) : 0);
/*							if(pendingSmsChkId.isChecked()) model.setValue("Pending", currDate, penMap.containsKey(currDate) ? penMap.get(currDate) : 0);*/
							if(undeliveredSmsChkId.isChecked()) model.setValue("Undelivered", currDate, undelMap.containsKey(currDate) ? undelMap.get(currDate) : 0);
							
							tempCal.set(Calendar.DATE, tempCal.get(Calendar.DATE) + 1);
					}while(tempCal.before(endDate1) || tempCal.equals(endDate1));
				}

				if(type.equals(ET_TYPE_MONTHS)) {
					int i=1;
					do {
						//logger.debug("executing ========"+monthsDiff);
						i++;
				
						currDate = ""+(tempCal.get(startDate.MONTH)+1);

						if(sentSmsChkId.isChecked()) model.setValue("Sent", MONTH_MAP.get(currDate), sentMap.containsKey(currDate) ? sentMap.get(currDate) : 0);
						if(receivedSmsChkId.isChecked()) model.setValue("Received", MONTH_MAP.get(currDate), recMap.containsKey(currDate) ? recMap.get(currDate) : 0);
						//if(pendingSmsChkId.isChecked()) model.setValue("Pending", MONTH_MAP.get(currDate), penMap.containsKey(currDate) ? penMap.get(currDate) : 0);
						if(undeliveredSmsChkId.isChecked()) model.setValue("Undelivered", MONTH_MAP.get(currDate),undelMap.containsKey(currDate) ? undelMap.get(currDate) : 0);

						tempCal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH) + 1);
					} while(i<= monthsDiff);
				}
				

				
				//etReportsSmsChartId.setModel(model);
				etReportsChartId.setModel(model);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}
				return etReportsSmsChartId;
		} // drawSmsChart
			
		private Textbox emailSearchBoxId,smsSearchBoxId;
		
	    public void onChanging$statusPendingSearchBoxId(InputEvent event) {
			
			String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);	
			String key = event.getValue();

			//logger.info("got the key ::"+key);
			
			if (key.trim().length() != 0) {
				
				if(!isContactDateType) {
					pendingReportSize = eventTriggerEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,"'Submitted'" );
					}else{
						pendingReportSize = contactSpecificDateEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,"'Submitted'" );
					}
					statusPendingReportsLocationsPagingId.setTotalSize(pendingReportSize);
					statusPendingReportsLocationsPagingId.setActivePage(0);
					if(pendingReportSize>0){
						pendingReportsDivId.setVisible(true);
					}
				getStatusPendingReports(fromDate, endDate, 0, statusPendingReportsLocationsPagingId.getPageSize(),isContactDateType , key);
			}
			
			else {
				
				if(!isContactDateType) {
					pendingReportSize = eventTriggerEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,"'Submitted'" );
					}else{
						pendingReportSize = contactSpecificDateEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,"'Submitted'" );
					}
					statusPendingReportsLocationsPagingId.setTotalSize(pendingReportSize);
					statusPendingReportsLocationsPagingId.setActivePage(0);
					if(pendingReportSize>0){
						pendingReportsDivId.setVisible(true);
					}
				getStatusPendingReports(fromDate, endDate, 0, statusPendingReportsLocationsPagingId.getPageSize(),isContactDateType , key);
			}

		}//onChanging$emailSearchBoxId
 public void onChanging$emailSearchBoxId(InputEvent event) {
			
			String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);	
			String key = event.getValue();

			//logger.info("got the key ::"+key);
			
			if (key.trim().length() != 0) {
				
				if(!isContactDateType) {
				 totalSize = eventTriggerEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,key,emailSatusTobeFetched );
				}
				else {
					totalSize = contactSpecificDateEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,key,emailSatusTobeFetched );
				}
				
				reportsPagingId.setTotalSize(totalSize);
				reportsPagingId.setActivePage(0);
				getEmailReports(fromDate, endDate, 0, reportsPagingId.getPageSize(),isContactDateType , key);
			}
			
			else {
				
				if(!isContactDateType) {
					 totalSize = eventTriggerEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,null,emailSatusTobeFetched );
					}
					else {
						totalSize = contactSpecificDateEventsDao.findSearchEmailCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,null,emailSatusTobeFetched );
					}
				reportsPagingId.setTotalSize(totalSize);
				reportsPagingId.setActivePage(0);
				getEmailReports( fromDate, endDate, 0, reportsPagingId.getPageSize(),isContactDateType, null);
			}

		}
	    public void onFocus$emailSearchBoxId() {
	    	emailSearchBoxId.setValue("");
	    }//onFocus$emailSearchBoxId
	    public void onFocus$smsSearchBoxId() {
	    	smsSearchBoxId.setValue("");
	    }//onFocus$emailSearchBoxId
	    
        public void onChanging$smsSearchBoxId(InputEvent event) {
        	 
			String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);	
			String key = event.getValue();

			//logger.info("got the key ::"+key);
			
			if (key.trim().length() != 0) {
				   if(!checkIfNumber(key)) {
					   MessageUtil.setMessage("Please provide number value.", "red");
						return;
				   }
				
					if(!isContactDateType) {
						totalSize = eventTriggerEventsDao.findSearchSmsCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, key, considerDeliveredStatus );
					}
					else {
						totalSize = contactSpecificDateEventsDao.findSearchSmsCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, key,considerDeliveredStatus );
					}
					
					smsReportsPagingId.setTotalSize(totalSize);
					smsReportsPagingId.setActivePage(0);
					getSmsReports(fromDate, endDate, 0, smsReportsPagingId.getPageSize(),isContactDateType , key);
				}
			else {
				if(!isContactDateType) {
					totalSize = eventTriggerEventsDao.findSearchSmsCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,considerDeliveredStatus );
				}
				else {
					totalSize = contactSpecificDateEventsDao.findSearchSmsCountNew(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null,considerDeliveredStatus );
				}
				smsReportsPagingId.setTotalSize(totalSize);
				smsReportsPagingId.setActivePage(0);
				getSmsReports( fromDate, endDate, 0, smsReportsPagingId.getPageSize(),isContactDateType, null);
			}

		}//onChanging$smsSearchBoxId
        
        public boolean checkIfNumber(String in) {
            try {
                Long.parseLong(in);
            } catch (NumberFormatException ex) {
                return false;
            }
            return true;
        }// checkIfNumber
		
        public void onClick$resetEmailSearchCriteriaAnchId() {
			
			try {
				String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);	
				
				if(!isContactDateType) {
					totalSize = eventTriggerEventsDao.findSearchEmailCount(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null );
					
				}else {
					
					totalSize = contactSpecificDateEventsDao.findSearchEmailCount(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null );
				}
				emailSearchBoxId.setValue("");
				reportsPagingId.setTotalSize(totalSize);
				reportsPagingId.setActivePage(0);
				getEmailReports(fromDate, endDate, 0,reportsPagingId.getPageSize(),isContactDateType,null);
			} catch (WrongValueException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}
		}//onClick$resetEmailSearchCriteriaAnchId()
		
	
        public void onClick$resetSmsSearchCriteriaAnchId() {
		
			try {
				String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
				String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);	
				
				if(!isContactDateType) {
					totalSize = eventTriggerEventsDao.findSearchSmsCount(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate,null );
					
				}else {
					
					totalSize = contactSpecificDateEventsDao.findSearchSmsCount(currentUser.getUserId(),eventTrigger.getId(), fromDate, endDate, null );
				}
				smsSearchBoxId.setValue("");
				smsReportsPagingId.setTotalSize(totalSize);
				smsReportsPagingId.setActivePage(0);
				getSmsReports(fromDate, endDate, 0,smsReportsPagingId.getPageSize(),isContactDateType,null);
				
			} catch (WrongValueException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
			}
        }//onClick$resetSmsSearchCriteriaAnchId()
	
		public void onCheck$uniqOpensChkId(){
			drawChart(isContactDateType);
		}
		
		public void onCheck$bouncedChkId(){
			drawChart(isContactDateType);
		}
		public void onCheck$statusPendingChkId(){
			drawChart(isContactDateType);
		}
		
		public void onCheck$sentChkId(){
			drawChart(isContactDateType);
		}
		
		public void onCheck$deleveredChkId(){
			drawChart(isContactDateType);
		}
		
		public void onCheck$clicksChkId(){
			drawChart(isContactDateType);
		}
		
		public void onCheck$sentSmsChkId(){
			drawChart(isContactDateType);
		}
		
		public void onCheck$receivedSmsChkId(){
			drawChart(isContactDateType);
			
		}
		public void onCheck$pendingSmsChkId(){
			drawSmsChart(isContactDateType);
		}
		
		public void onCheck$undeliveredSmsChkId(){
			drawChart(isContactDateType);
			
		}
		public void onClick$exportBtnId(){
			try {
				
				Long userId = GetUser.getUserObj().getUserId();
				if(userId!=null){
					exportEmailReport = true;
					createWindow();
					
				anchorEvent(false);
					
					custExport.setVisible(true);
					custExport.doHighlighted();
				}
				else{
					
					MessageUtil.setMessage("Please select a user", "info");
				}
			} catch (Exception e) {
				logger.error("Error occured from the exportCSV method ***",e);
			}
		}
		public void onClick$statusPendingExportBtnId(){
			try {
				
				Long userId = GetUser.getUserObj().getUserId();
				if(userId!=null){
					exportStatusPendingCSV();
				}
				else{
					
					MessageUtil.setMessage("Please select a user", "info");
				}
			} catch (Exception e) {
				logger.error("Error occured from the exportCSV method ***",e);
			}
		}
		
		
		public void onClick$exportSmsBtnId(){
			try {
				
				Long userId = GetUser.getUserObj().getUserId();
				if(userId!=null){
					
					exportEmailReport = false;
					createWindow();
					
					anchorEvent(false);
					
					custExport.setVisible(true);
					custExport.doHighlighted();
				}
				else{
					
					MessageUtil.setMessage("Please select a user", "info");
				}
			} catch (Exception e) {
				logger.error("Error occured from the exportCSV method ***",e);
			}
		}
		
		
		public void createWindow()	{
			
			try {
				
				Components.removeAllChildren(custExport$chkDivId);
				if(exportEmailReport)
				{
					Checkbox tempChk2 = new Checkbox("Recipient");
					tempChk2.setSclass("custCheck");
					tempChk2.setParent(custExport$chkDivId);


					Checkbox tempChk4 = new Checkbox("Sent Date");
					tempChk4.setSclass("custCheck");
					tempChk4.setParent(custExport$chkDivId);


					Checkbox tempChk5 = new Checkbox("Delivery Status");
					tempChk5.setSclass("custCheck");
					tempChk5.setParent(custExport$chkDivId);

					Checkbox tempChk6 = new Checkbox("Opens");
					tempChk6.setSclass("custCheck");
					tempChk6.setParent(custExport$chkDivId);

					Checkbox tempChk7 = new Checkbox("Clicks");
					tempChk7.setSclass("custCheck");
					tempChk7.setParent(custExport$chkDivId);
				}
				else
				{
					custExport$chkDivId.setStyle("margin:5px 0px 0px 150px");
					Checkbox tempChk2 = new Checkbox("Recipient");
					tempChk2.setSclass("smsCheck");
					tempChk2.setParent(custExport$chkDivId);
					
				
					Checkbox tempChk4 = new Checkbox("Sent Date");
					tempChk4.setSclass("smsCheck");
					tempChk4.setParent(custExport$chkDivId);

					
					Checkbox tempChk5 = new Checkbox("Delivery Status");
					tempChk5.setSclass("smsCheck");
					tempChk5.setParent(custExport$chkDivId);
				}
				
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
		}
		
		
	public void anchorEvent(boolean flag) {
		List<Component> chkList = custExport$chkDivId.getChildren();
		Checkbox tempChk = null;
		for (int i = 0; i < chkList.size(); i++) {
			if(!(chkList.get(i) instanceof Checkbox)) continue;
			
			tempChk = (Checkbox)chkList.get(i);
			tempChk.setChecked(flag);
			
		} // for

	}
	public void onClick$selectAllAnchr$custExport() {
		
		anchorEvent(true);

	}

	public void onClick$clearAllAnchr$custExport() {
		
		anchorEvent(false);
	}
	
	
	public void onClick$selectFieldBtnId$custExport(){
		custExport.setVisible(false);
		List<Component> chkList = custExport$chkDivId.getChildren();


		int indexes[]=new int[chkList.size()];
		boolean checked=false;


		for(int i=0;i<chkList.size();i++) {
			indexes[i]=-1;
		} 

		Checkbox tempChk = null;
		
		for(int i=0;i<chkList.size();i++) {
			if( ((Checkbox)chkList.get(i)).isChecked())
			{

				indexes[i]=0;
				checked=true;
			}
		}
		
		if(checked) {
			int confirm=Messagebox.show("Do you want to Export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm==1){
				try{

					exportCSV(indexes);

				}catch(Exception e){
					logger.error("Exception caught :: ",e);
				}
			}
			else{
				custExport.setVisible(true);
			}

		}
		else {

			MessageUtil.setMessage("Please select atleast one field", "red");
			custExport.setVisible(false);
		}
	}
	
	public void exportStatusPendingCSV(){
		logger.info("exportStatusPendingCSV");
		JdbcResultsetHandler jdbcResultsetHandler = null;
		StringBuffer sb = null;
		BufferedWriter bw = null;
		ResultSet resultSet = null;
		try {
			if (statusPendingReportsLbId.getItemCount() == 0) {
				MessageUtil.setMessage(
						"No records exist in the selected search.",
						"color:red", "TOP");
			}

			String ext = "csv";
			String userName = currentUser.getUserName();

			String usersParentDirectory = (String) PropertyUtil
					.getPropertyValue("usersParentDirectory");

			File downloadDir = new File(usersParentDirectory + "/" + userName
					+ "/List/download/");

			if (!downloadDir.exists()) {
				downloadDir.mkdirs();
			}

			String filePath = usersParentDirectory + "/" + userName + "/List/download/Event_Trigger_Report_" + System.currentTimeMillis() + "." + ext;

			sb = new StringBuffer();
			File file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(file));
			sb.append("\"Email Address\",\"Sent Date\"");
			sb.append("\n");
			bw.write(sb.toString());
			
			String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);	
			String query="";
			String subQry="";
			String	key = statusPendingSearchBoxId.getValue();
			if(isContactDateType) {
			subQry="SELECT cs.email_id," +
					" e.campaign_sent_date as lastSent , e.camp_sent_id,e.event_id " +
					" FROM contact_specific_date_events e, campaign_sent cs WHERE e.user_id ="+currentUser.getUserId()+
					" AND e.event_trigger_id="+eventTrigger.getId().longValue()+" AND e.camp_sent_id IS NOT NULL  AND e.camp_cr_id=cs.cr_id " +
					" AND e.camp_sent_id = cs.sent_id AND e.campaign_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' " +
					" AND cs.email_id IS NOT NULL  " +
					" AND cs.status in ('Submitted') ";
			}else{


			subQry="SELECT cs.email_id, " +
				" e.campaign_sent_date as lastSent,"+
				" e.camp_sent_id,"+
				" e.event_id"+
				" FROM event_trigger_events e, campaign_sent cs WHERE e.user_id ="+currentUser.getUserId()+
				" AND e.event_trigger_id="+eventTrigger.getId().longValue()+" AND e.camp_sent_id IS NOT NULL  AND e.camp_cr_id=cs.cr_id " +
				" AND e.camp_sent_id = cs.sent_id AND e.campaign_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' " +
				" AND cs.email_id IS NOT NULL  " +
				" AND cs.status in ('Submitted') ";
			}
			
			if(key != null && key !="" && !key.equals("Search Email")){

				subQry += " AND cs.email_id LIKE '%"+key+"%' ";
			}
			
			query  = subQry +" ORDER BY lastSent DESC ";	
			logger.info("qrryyy "+query);
			jdbcResultsetHandler = new JdbcResultsetHandler();
			jdbcResultsetHandler.executeStmt(query);
			resultSet = jdbcResultsetHandler.getResultSet();
			while (resultSet.next()) {
				sb.setLength(0);
				
				sb.append("\"");sb.append(resultSet.getString("email_id") != null ? resultSet.getString("email_id"):resultSet.getString("child_email"));sb.append("\",");
				sb.append("\"");sb.append(resultSet.getString("lastSent") != null ? resultSet.getString("lastSent"):"");sb.append("\",");
				sb.append("\n");
				bw.write(sb.toString());
				sb.setLength(0);
				
			}
			bw.flush();
			bw.close();

			Filedownload.save(file, "text/csv");

		} catch (Exception e) {
			logger.error("Exception :: ", e);
		} finally {
			if (jdbcResultsetHandler != null)
				jdbcResultsetHandler.destroy();
			jdbcResultsetHandler = null;
			sb = null;
			bw = null;
		}

	}
	public void exportCSV(int[] indexes) {

		try {
	logger.debug("entered ");
	String ext ="csv";
	String userName = GetUser.getUserName();

	String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");

	File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/" );


	if(!downloadDir.exists()){
		downloadDir.mkdirs();
	}
	 
	String filePath = usersParentDirectory + "/" + userName + "/List/download/Event_Trigger_Report_" + System.currentTimeMillis() + "." + ext;

	StringBuffer sb = null;
	File file = new File(filePath);
	BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
	logger.debug("Writing to the file : " + filePath);
	if(exportEmailReport)
	{
		int totalSize =etReportsLbId.getItemCount();
//		int totalSize = eventTriggerDao.findAllCountByUserId(CurrentUser.getUserId());
		 if(totalSize == 0)
		 {
			 MessageUtil.setMessage("No contacts exist in the selected search.", "color:red", "TOP");
			 return;
		 }
		
	String udfFldsLabel= "";

	if(indexes[0]==0) {

		udfFldsLabel = "\""+"Recipient"+"\""+",";
	}

	//Email
	if(indexes[1]==0) {

		udfFldsLabel += "\""+"Sent Date"+"\""+",";
	}
	//Mobile Number
	if(indexes[2]==0) {

		udfFldsLabel += "\""+"Delivery Status"+"\""+",";
	}	
	if(indexes[3]==0) {

		udfFldsLabel += "\""+"Opens"+"\""+",";
	}
	//Mobile Number
	if(indexes[4]==0) {

		udfFldsLabel += "\""+"Clicks"+"\"";
	}
	sb = new StringBuffer();
	
	sb.append(udfFldsLabel);
	
	sb.append("\r\n");
	
	bw.write(sb.toString());
	String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
	String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);	
	
	String	key = statusPendingSearchBoxId.getValue();
	String query;
	if(isContactDateType) {

		String openClicksQry="";
		String subQry="SELECT cs.email_id, cs.status as lastStatus, " +
				" e.campaign_sent_date as lastSent , e.camp_sent_id,e.event_id,cs.opens as uniopens,  cs.clicks as uniclicks" +
				" FROM contact_specific_date_events e, campaign_sent cs WHERE e.user_id ="+currentUser.getUserId()+
				" AND e.event_trigger_id="+eventTrigger.getId().longValue()+" AND e.camp_sent_id IS NOT NULL  AND e.camp_cr_id=cs.cr_id " +
				" AND e.camp_sent_id = cs.sent_id AND e.campaign_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' " +
				" AND cs.email_id IS NOT NULL  " +
				" AND cs.status in ("+emailSatusTobeFetched+") ";
		
		
		
		
		if((emailSatusTobeFetched.contains("Bounced") && ! emailSatusTobeFetched.contains("special_condtion_for_all"))){ 

			if( !emailSatusTobeFetched.contains("Unsubscribed") && !emailSatusTobeFetched.contains("Spammed")  && !emailSatusTobeFetched.contains("Success")){
				subQry += "  AND cs.status='Bounced'  ";
			}else {
				subQry += "  AND cs.status='Bounced'  AND cs.status='Unsubscribed'  AND cs.status='Spammed' AND cs.status='Success'   ";
			}

		}
		
		
		
		
		
		if(emailSatusTobeFetched.contains("_fetch_clicks_also_") && !emailSatusTobeFetched.contains("_fetch_opens_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
			openClicksQry += " AND cs.clicks is not null AND cs.clicks !=0  ";
			
		}
		else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && !emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
			openClicksQry += " AND cs.opens is not null AND cs.opens !=0  ";
		}
		else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
			openClicksQry += " AND ((cs.opens is not null AND cs.opens !=0)   AND (cs.clicks is not null AND cs.clicks !=0))   ";
		}
		
		
		subQry += openClicksQry;
		
		
		if(key != null && key !="" && !key.equals("Search Email")){

			subQry += " AND cs.email_id LIKE '%"+key+"%' ";
		}
		
		query  = subQry +" ORDER BY lastSent DESC ";		
		
		
		
		
		

		//query  = query +" ORDER BY campaign_sent_date DESC" ;
	}
	else{
		/*query="SELECT cs.email_id, e.campaignSentDate, cs.status, IF(cs.opens >0, 1,0) as uniopens," +
				"IF(cs.clicks >0, 1,0) as uniclicks  FROM  EventTriggerEvents e, CampaignSent cs WHERE e.user_id ="+currentUser.getUserId()+
				" AND e.eventTriggerId="+eventTrigger.getId().longValue()+" AND e.campSentId IS NOT NULL  AND e.campCrId=cs.campaignReport.crId " +
				" AND e.campSentId = cs.sentId AND e.campaignSentDate BETWEEN '"+fromDate+"' AND '"+endDate+"' " +
				" AND cs.email_id IS NOT NULL  ";
		query ="SELECT cs.email_id, e.campaign_sent_date, cs.status, IF(cs.opens >0, 1,0) as uniopens," +
				"IF(cs.clicks >0, 1,0) as uniclicks  FROM  event_trigger_events e, campaign_sent cs, campaign_report cr WHERE e.user_id ="+currentUser.getUserId()+
				" AND e.event_trigger_id="+eventTrigger.getId().longValue()+" AND e.camp_sent_id IS NOT NULL  AND e.camp_cr_id=cs.cr_id AND cs.cr_id=cr.cr_id " +
				" AND e.camp_sent_id = cs.sent_id AND e.campaign_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' " +
				" AND cs.email_id IS NOT NULL  ";	

		if(key != null && key !="" && !key.equals("Search Email")){

			query += " AND cs.email_id LIKE '%"+key+"%'";
		}

		query  = query +" ORDER BY e.campaign_sent_date DESC " ;*/
		
		
		
		String openClicksQry ="";
		String subQry="SELECT cs.email_id, " +
				" cs.status as lastStatus," +
				" e.campaign_sent_date as lastSent,"+
				" e.camp_sent_id,"+
				" e.event_id,"+
				" cs.opens as uniopens,"+
				" cs.clicks as uniclicks"+
				" FROM event_trigger_events e, campaign_sent cs WHERE e.user_id ="+currentUser.getUserId()+
				" AND e.event_trigger_id="+eventTrigger.getId().longValue()+" AND e.camp_sent_id IS NOT NULL  AND e.camp_cr_id=cs.cr_id " +
				" AND e.camp_sent_id = cs.sent_id AND e.campaign_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' " +
				" AND cs.email_id IS NOT NULL  " +
				" AND cs.status in ("+emailSatusTobeFetched+") ";
		
		
		
		if((emailSatusTobeFetched.contains("Bounced") && ! emailSatusTobeFetched.contains("special_condtion_for_all"))){ 
			
			if( !emailSatusTobeFetched.contains("Unsubscribed") && !emailSatusTobeFetched.contains("Spammed")  && !emailSatusTobeFetched.contains("Success")){
				subQry += "  AND cs.status='Bounced'  ";
			}else {
				subQry += "  AND cs.status='Bounced'  AND cs.status='Unsubscribed'  AND cs.status='Spammed' AND cs.status='Success'   ";
			}
			
		}
		
		
		
		
		
		if(emailSatusTobeFetched.contains("_fetch_clicks_also_") && !emailSatusTobeFetched.contains("_fetch_opens_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
			openClicksQry += " AND cs.clicks is not null AND cs.clicks !=0  ";
			
		}
		else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && !emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
			openClicksQry += " AND cs.opens is not null AND cs.opens !=0  ";
		}
		else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
			openClicksQry += " AND ((cs.opens is not null AND cs.opens !=0)   AND (cs.clicks is not null AND cs.clicks !=0))   ";
		}
		
		
		subQry += openClicksQry;
		
		
		
		
		if(key != null && key !="" && !key.equals("Search Email")){

			subQry += " AND cs.email_id LIKE '%"+key+"%' ";
		}

		query  = subQry +" ORDER BY lastSent DESC  ";
		
		
		
		
		
		
		
		
	}
	logger.info("query is:"+query);
	JdbcResultsetHandler jdbcResultSetHandler=null;
		try
		{

			jdbcResultSetHandler=new JdbcResultsetHandler();

			jdbcResultSetHandler.executeStmt(query);
			long count=jdbcResultSetHandler.totalRecordsSize();


			//List<DRSent> li=jh.getDRReport((int)count);
			do
			{

				List recordList = jdbcResultSetHandler.getRecords();

				if(recordList == null || recordList.size() == 0) {
					logger.debug("Error getting from jdbcResulSetHandler  ...");
					return;
				}




				for (int i=0; i<recordList.size();i++) {
					String eachRecord = (String)recordList.get(i);
					logger.info("eachRecord  >>> "+eachRecord);
					
					//example eachRecord  >>> email_id=auguest@mailinator.com;status=Success;campaign_sent_date=2014-08-06 07:33:39.0;camp_sent_id=564419;event_id=993;opens=0;clicks=0;
					sb = new StringBuffer();
					String udfFldsLabel1="";
					String[] strArr = eachRecord.split(";");
					String d;
					if(indexes[0]==0) {
						d=strArr[0].replace("email_id=", "");
						if(d!=null)

							udfFldsLabel1 = "\""+strArr[0].replace("email_id=", "")+"\""+",";
						else
							udfFldsLabel1 = "\""+"\""+",";
					}

					//Email
					if(indexes[1]==0) {

						strArr[2]=strArr[2].replace("campaign_sent_date=", "");
						Calendar cal=null;
						if(strArr[2]!=null)
						{
							int l=strArr[2].length();

							strArr[2]=strArr[2].substring(0,l-2);
							if(strArr[2]!= null) {
								cal = Calendar.getInstance();
								cal.setTimeZone(clientTimeZone);
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								try {
									cal.setTime(sdf.parse(strArr[2]));
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									logger.error("Exception in setTime() method", e);

								}
							}
							udfFldsLabel1 += "\""+MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone)+"\""+",";
						}
						else
							udfFldsLabel1 = "\""+"\""+",";
					}
					//Mobile Number
					if(indexes[2]==0) {

						if(strArr[1].replace("status=", "")!=null)
							udfFldsLabel1 += "\""+strArr[1].replace("status=", "")+"\""+",";
						else
							udfFldsLabel1 = "\""+"\""+",";
					}	
					if(indexes[3]==0) {
						if(strArr[5].replace("opens=", "")!=null)
							udfFldsLabel1 += "\""+strArr[5].replace("opens=", "")+"\""+",";
						else
							udfFldsLabel1 = "\""+"\""+",";

					}
					//Mobile Number
					if(indexes[4]==0) {
						if(strArr[6].replace("clicks=", "")!=null)
							udfFldsLabel1 += "\""+strArr[6].replace("clicks=", "")+"\"";
					}

					sb.append(udfFldsLabel1);
					sb.append("\r\n");
					bw.write(sb.toString());
					sb.setLength(0);
				}			


			}while(jdbcResultSetHandler.getCurrentFetchingCount() < count-1);				

		}
		
		catch(Exception e)
		{
			logger.error("** Exception : " , e);
		}
		finally
		{
			jdbcResultSetHandler.destroy();
		}
	bw.flush();
	bw.close();
	}
	else
	{
		int totalSize =etReportsLbId1.getItemCount();
//		int totalSize = eventTriggerDao.findAllCountByUserId(CurrentUser.getUserId());
		 if(totalSize == 0)
		 {
			 MessageUtil.setMessage("No contacts exist in the selected search.", "color:red", "TOP");
			 return;
		 }
		String udfFldsLabel= "";

		if(indexes[0]==0) {

			udfFldsLabel = "\""+"Recipient"+"\""+",";
		}

		//Email
		if(indexes[1]==0) {

			udfFldsLabel += "\""+"Sent Date"+"\""+",";
		}
		//Mobile Number
		if(indexes[2]==0) {

			udfFldsLabel += "\""+"Delivery Status"+"\""+",";
		}	
		
		sb = new StringBuffer();
		
		sb.append(udfFldsLabel);
		
		sb.append("\r\n");
		
		bw.write(sb.toString());
		
		String fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		String endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);	
		String	key = smsSearchBoxId.getValue();
		String query;
		if(isContactDateType) {

			/*query="SELECT scs.mobile_number,e.sms_sent_date, " +
					" e.status " +
					" e.event_id, e.sms_sent_id FROM contact_specific_date_events e, sms_campaign_sent scs WHERE e.user_id ="+currentUser.getUserId()+
					" AND e.event_trigger_id="+eventTrigger.getId().longValue()+" AND e.sms_sent_id IS NOT NULL  AND e.sms_cr_id=scs.sms_cr_id " +
					" AND e.sms_sent_id = scs.sent_id AND e.sms_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' " +
					" AND scs.mobile_number IS NOT NULL ";

			query =" SELECT scs.mobileNumber, e.smsSentDate, scs.status  FROM  contact_specific_date_events e, sms_campaign_sent scs WHERE e.user_id ="+currentUser.getUserId()+
					" AND e.event_trigger_id="+eventTrigger.getId().longValue()+" AND e.sms_sent_id IS NOT NULL  AND e.sms_cr_id=scs.sms_cr_id " +
					" AND e.sms_sent_id = scs.sent_id AND e.sms_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' " +
					" AND scs.mobile_number IS NOT NULL ";
			if(key != null && key !=""){

				query += " AND scs.mobile_number LIKE '%"+key+"%'";
			}

			query  = query +"ORDER BY campaign_sent_date DESC";*/
			
			
			
			String subQry="SELECT scs.mobile_number, e.sms_sent_date as lastSent, " +
					" scs.status as lastStatus, e.event_id, e.sms_sent_id" +
					" FROM contact_specific_date_events e, sms_campaign_sent scs WHERE e.user_id ="+currentUser.getUserId()+
					" AND e.event_trigger_id="+eventTrigger.getId().longValue()+" AND e.sms_sent_id IS NOT NULL  AND e.sms_cr_id=scs.sms_cr_id " +
					" AND e.sms_sent_id = scs.sent_id AND e.sms_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' " +
					" AND scs.mobile_number IS NOT NULL  ";




			if(considerDeliveredStatus == 0){
				subQry += " AND scs.status not in ('Delivered','Success','Status Pending') ";
			}else if(considerDeliveredStatus == 1){
				subQry += " AND scs.status in ('Delivered','Success') ";
			}


			if(key != null && key !="" && !key.equals("Search Phone")){

				subQry += " AND scs.mobile_number LIKE '%"+key+"%' ";
			}

			query  = subQry +" ORDER BY lastSent DESC  ";
			
			
			
			
			
		}
		else{
			/*query="SELECT scs.mobile_number,e.sms_sent_date, " +" e.status, " +
					" e.event_id, e.sms_sent_id FROM event_trigger_events e, sms_campaign_sent scs WHERE e.user_id ="+currentUser.getUserId()+
					" AND e.event_trigger_id="+eventTrigger.getId().longValue()+" AND e.sms_sent_id IS NOT NULL  AND e.sms_cr_id=scs.sms_cr_id  " +
					" AND e.sms_sent_id = scs.sent_id AND e.sms_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' " +
					" AND scs.mobile_number IS NOT NULL  ";
			query=" SELECT scs.mobile_number, e.sms_sent_date, scs.status  FROM  event_trigger_events e, sms_campaign_sent scs, sms_campaign_report cr WHERE e.user_id ="+currentUser.getUserId()+
					" AND e.event_trigger_id="+eventTrigger.getId().longValue()+" AND e.sms_sent_id IS NOT NULL AND" +
					" e.sms_cr_id=scs.sms_cr_id AND scs.sms_cr_id=cr.sms_cr_id  AND e.sms_sent_id = scs.sent_id AND e.sms_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"'";
			if(key != null && key !=""){

				query += " AND scs.mobile_number LIKE '%"+key+"%'";
			} 
			query  = query +"ORDER BY sms_sent_date DESC";*/




			String subQry="SELECT scs.mobile_number,e.sms_sent_date as lastSent, " +
					" scs.status as lastStatus, e.event_id, e.sms_sent_id " +
					" FROM event_trigger_events e, sms_campaign_sent scs WHERE e.user_id ="+currentUser.getUserId()+
					" AND e.event_trigger_id="+eventTrigger.getId().longValue()+" AND e.sms_sent_id IS NOT NULL  AND e.sms_cr_id=scs.sms_cr_id  " +
					" AND e.sms_sent_id = scs.sent_id AND e.sms_sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' " +
					" AND scs.mobile_number IS NOT NULL  ";


			if(considerDeliveredStatus == 0){
				subQry += " AND scs.status not in ('Delivered','Success','Status Pending') ";
			}else if(considerDeliveredStatus == 1){
				subQry += " AND scs.status in ('Delivered','Success') ";
			}







			if(key != null && key !="" && !key.equals("Search Phone")){

				subQry += " AND scs.mobile_number LIKE '%"+key+"%'";
			}

			query  = subQry +" ORDER BY lastSent DESC  ";






		}
		logger.info("query is:"+query);
		JdbcResultsetHandler jdbcResultSetHandler=null;
			try
			{
		
				jdbcResultSetHandler=new JdbcResultsetHandler();
		
				jdbcResultSetHandler.executeStmt(query);
		long count=jdbcResultSetHandler.totalRecordsSize();
		
		
		//List<DRSent> li=jh.getDRReport((int)count);
		do
			{
			
			List recordList = jdbcResultSetHandler.getRecords();
			logger.info("recordList >>> "+recordList.get(0));
			if(recordList == null || recordList.size() == 0) {
				logger.debug("Error getting from jdbcResulSetHandler  ...");
			return;
			}
			
			
			
			
				for (int i=0; i<recordList.size();i++) {
					String eachRecord = (String)recordList.get(i);
					logger.info("eachRecord >>> "+eachRecord);
				    sb = new StringBuffer();
					String udfFldsLabel1="";
					String[] strArr = eachRecord.split(";");
					String d;
					if(indexes[0]==0) {
						d=strArr[0].replace("mobile_number=", "");
						if(d!=null)
						
						udfFldsLabel1 = "\""+strArr[0].replace("mobile_number=", "")+"\""+",";
						else
							udfFldsLabel1 = "\""+"\""+",";
					}

					//Email
					if(indexes[1]==0) {
						
						strArr[1]=strArr[1].replace("sms_sent_date=", "");
						Calendar cal=null;
						if(strArr[1]!=null)
						{
						int l=strArr[1].length();
						
						strArr[1]=strArr[1].substring(0,l-2);
						if(strArr[1]!= null) {
							 cal = Calendar.getInstance();
							 cal.setTimeZone(clientTimeZone);
							    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							    try {
									cal.setTime(sdf.parse(strArr[1]));
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									logger.error("Exception in setTime() method", e);
								
								}
						}
						udfFldsLabel1 += "\""+MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone)+"\""+",";
						}
						else
							udfFldsLabel1 = "\""+"\""+",";
					}
					//Mobile Number
					if(indexes[2]==0) {
						
						if(strArr[2].replace("status=", "")!=null) {
							//String status =	(strArr[2].replace("status=", "") != null && strArr[2].replace("status=", "").equalsIgnoreCase(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED)) ? SMSStatusCodes.CLICKATELL_STATUS_DELIVERY_ERROR : strArr[2].replace("status=", "");
							String status =	(strArr[2].replace("status=", "") != null && strArr[2].replace("status=", "").equalsIgnoreCase(SMSStatusCodes.CLICKATELL_STATUS_BOUNCED)) ? SMSStatusCodes.CLICKATELL_STATUS_BOUNCED : strArr[2].replace("status=", "");
							udfFldsLabel1 += "\""+status+"\""+",";
						}
						else
							udfFldsLabel1 = "\""+"\""+",";
					}	
					

					sb.append(udfFldsLabel1);
					sb.append("\r\n");
					bw.write(sb.toString());
					sb.setLength(0);
					
				}			
			
			}while(jdbcResultSetHandler.getCurrentFetchingCount() < count-1);				
			
			}
			
			catch(Exception e)
			{
				logger.error("** Exception : " , e);
			}
			finally
			{
				jdbcResultSetHandler.destroy();
			}
		bw.flush();
		bw.close();
	}
	Filedownload.save(file, "text/csv");
	logger.debug("exited");
		}
	catch (Exception e) {
	logger.error("** Exception : " , e);
	}
		
	}//end of exportCSV
	
	
	/*public void onSelect$emailSmsControlsLbId(){
		logger.info("event listener called >>> ");
		boolean selectedAll = false;
		Set<Listitem> selectedItemsSet = new HashSet<Listitem>();
		Listitem itemAll = null; 
		
		
		for (Listitem item : emailSmsControlsLbId.getSelectedItems()) {
			if(item.getLabel().equalsIgnoreCase("All")){
				itemAll = item;
				selectedAll = true;
			}
			selectedItemsSet.add(item);
			
		}
		
		if(selectedAll == true){
			emailSmsControlsLbId.selectAll();
			smsChkId.setChecked(true);
			emailChkId.setChecked(true);
		}else{
			emailSmsControlsLbId.setSelectedItems(selectedItemsSet);
		}
		
		
		if(selectedItemsSet.size() == 4 && selectedAll == false){
			emailSmsControlsLbId.setSelectedItem(null);
			smsChkId.setChecked(false);
			emailChkId.setChecked(false);
		}
		
		setRequiredStatusToBeFetched();
	}*/
	
	
	
	private void setRequiredStatusToBeFetched(){
		
		Set<String> requiredStatusSet = new HashSet<String>();
		Set<String> requiredStatusSmsSet = new HashSet<String>();
		if(emailSmsControlsLbId.getSelectedItems() != null && emailSmsControlsLbId.getSelectedItems().size() > 0 ){
			for (Listitem item : emailSmsControlsLbId.getSelectedItems()) {
				if(item.getLabel().equalsIgnoreCase("All")){



					//if(isemailChecked == true){
						requiredStatusSet.add("Unsubscribed");
						requiredStatusSet.add("Spammed");
						requiredStatusSet.add("Success");
						requiredStatusSet.add("Bounced");
						requiredStatusSet.add("Submitted");
					//}

					//if(isSmsChecked == true){
						requiredStatusSmsSet.add("selected_all");
					//}



				}else if(item.getLabel().equals("Delivered")){

					//if(isemailChecked == true){
						requiredStatusSet.add("Unsubscribed");
						requiredStatusSet.add("Spammed");
						requiredStatusSet.add("Success");
					//}

					//if(isSmsChecked == true){
						requiredStatusSmsSet.add("Delivered");
						requiredStatusSmsSet.add("Success");
					//}

				}else if(item.getLabel().equals("Bounced")){

					//if(isemailChecked == true){
						requiredStatusSet.add("Bounced");
					//}

					//if(isSmsChecked == true){
						requiredStatusSmsSet.add("non_delivery_status");
					//}

				}else if(item.getLabel().equals("Opens")){
					
					//if(isemailChecked == true){
						requiredStatusSet.add("Unsubscribed");
						requiredStatusSet.add("Spammed");
						requiredStatusSet.add("Success");
						
						requiredStatusSet.add("_fetch_opens_also_");
					//}
					
				}else if(item.getLabel().equals("Clicks")){

					//if(isemailChecked == true){
						requiredStatusSet.add("Unsubscribed");
						requiredStatusSet.add("Spammed");
						requiredStatusSet.add("Success");
						
						
						requiredStatusSet.add("_fetch_clicks_also_");
						
						requiredStatusSmsSet.add("_sms_fetch_clicks_also_");
					//}
					
				}else if(item.getLabel().equals("Status Pending")){
					requiredStatusSet.add("Submitted");
				}

			}
			
			emailSatusTobeFetched = "";
			
			for(String aStatus : requiredStatusSet){
				if(emailSatusTobeFetched.trim().length() > 0) emailSatusTobeFetched += ",";
				emailSatusTobeFetched += "'"+aStatus+"'";
			}
			
			if(requiredStatusSmsSet.contains("selected_all") || (requiredStatusSmsSet.contains("Delivered") && requiredStatusSmsSet.contains("non_delivery_status"))){
				considerDeliveredStatus = 2;
			}else if( requiredStatusSmsSet.contains("Delivered") ){
				considerDeliveredStatus = 1;
			}else if( requiredStatusSmsSet.contains("non_delivery_status") ){
				considerDeliveredStatus = 0;
			}else {
				considerDeliveredStatus = -1;// indicates that only opens or clicks or both are selected, which are not concerned with sms so we should not fetch sms related report and should not draw chart for sms 
			}
			
			
			
			
			
			if(storeBandBoxId.getValue().equalsIgnoreCase("All")){
				
				emailSatusTobeFetched = "'Unsubscribed','Spammed','Success','Bounced','special_condtion_for_all'";
				
				considerDeliveredStatus = 2;
			}
			
			if(!statusPendingChkId.isChecked()){
				pendingReportsDivId.setVisible(false);
				notSentlabelId.setVisible(false);
				notSentLblId.setVisible(false);
			}
			
			
			logger.info(" status to be fetched >>> "+emailSatusTobeFetched+"  >>> considerDeliveredStatus === "+considerDeliveredStatus);
			

		}else{
			
			MessageUtil.setMessage("Please select appropriate option(s) in drop-down", "color:red");
			return;
		}
		
		
	}
	
	private void populateEmailSmsControlsLbId(){
		
		
		Listitem deliveredChkIdListitem = new Listitem("Delivered");
		deliveredChkIdListitem.setParent(emailSmsControlsLbId);
		
		Listitem nonDeliveryChkIdListitem = new Listitem("Bounced");
		nonDeliveryChkIdListitem.setParent(emailSmsControlsLbId);
		
		Listitem statusPendingDeliveryChkIdListitem = new Listitem("Status Pending");
		statusPendingDeliveryChkIdListitem.setParent(emailSmsControlsLbId);
		
		Listitem uniqueOpensChkIdListitem = new Listitem("Opens");
		uniqueOpensChkIdListitem.setParent(emailSmsControlsLbId);
		
		Listitem uniqueClicksChkIdListitem = new Listitem("Clicks");
		uniqueClicksChkIdListitem.setParent(emailSmsControlsLbId);
		
		
	}
	private void populateEmailSmsControlsLbIdAndSetDefaults() {
		//ch1
		//Components.removeAllChildren(emailSmsControlsLbId);
		
		
		//ch2
		/*Listitem listitemAll = new Listitem("All");
		listitemAll.setParent(emailSmsControlsLbId);*/
		
		
		//ch3
		/*Listitem deliveredChkIdListitem = new Listitem("Delivery");
		deliveredChkIdListitem.setParent(emailSmsControlsLbId);
		
		Listitem nonDeliveryChkIdListitem = new Listitem("Non-Delivery");
		nonDeliveryChkIdListitem.setParent(emailSmsControlsLbId);
		
		
		
		Listitem uniqueOpensChkIdListitem = new Listitem("Opens");
		uniqueOpensChkIdListitem.setParent(emailSmsControlsLbId);
		
		Listitem uniqueClicksChkIdListitem = new Listitem("Clicks");
		uniqueClicksChkIdListitem.setParent(emailSmsControlsLbId);*/
       
		//ch4
		//default setting
		//emailSmsControlsLbId.setSelectedItem(deliveredChkIdListitem);
		
		
		isemailChecked  = true;
		emailChkId.setChecked(true);
		
		isSmsChecked  = true;
		smsChkId.setChecked(true);
		
		
		//ch5
		/*uniqOpensChkId.setChecked(false);
		clicksChkId.setChecked(false);
		bouncedChkId.setChecked(false);
		deleveredChkId.setChecked(true);
		sentChkId.setChecked(false);
		sentSmsChkId.setChecked(false);
		receivedSmsChkId.setChecked(true);
		undeliveredSmsChkId.setChecked(false);*/
		
		uniqOpensChkId.setChecked(true);
		clicksChkId.setChecked(true);
		bouncedChkId.setChecked(true);
		deleveredChkId.setChecked(true);
		sentChkId.setChecked(true);
		sentSmsChkId.setChecked(true);
		receivedSmsChkId.setChecked(true);
		undeliveredSmsChkId.setChecked(true);
		statusPendingChkId.setChecked(true);
		
		
		}
	
	public void onCheck$emailChkId(){
		
		logger.info("isemailChecked starts>>>> "+isemailChecked);	
		if(isemailChecked == true){
			
			isemailChecked  = false;
			
		}else if(isemailChecked == false){
			
			isemailChecked  = true;
			
		}
		
		logger.info("isemailChecked ends>>>> "+isemailChecked);	
		
		//setRequiredStatusToBeFetched();
		processSelectedEmailSmsControls();
	}
	public void onCheck$smsChkId(){
		
		logger.info("isSmsChecked starts>>>> "+isSmsChecked);	
		if(isSmsChecked == true){
			
			isSmsChecked  = false;
			
		}else if(isSmsChecked == false){
			
			isSmsChecked  = true;
			
		}
		logger.info("isSmsChecked ends>>>> "+isSmsChecked);	
		
		//setRequiredStatusToBeFetched();
		processSelectedEmailSmsControls();
	}
	
	private void processSelectedEmailSmsControls(){
		
		
		try{
			
			  uniqOpensChkId.setChecked(false);
			  bouncedChkId.setChecked(false);
			  sentChkId.setChecked(false);
			  deleveredChkId.setChecked(false);
			  clicksChkId.setChecked(false);
			  sentSmsChkId.setChecked(false);
			  receivedSmsChkId.setChecked(false);
			  undeliveredSmsChkId.setChecked(false);
			  statusPendingChkId.setChecked(false);
			
			
			
			for (Listitem item : emailSmsControlsLbId.getSelectedItems()) 
	        {
			  if(item.getLabel().equals("All")){
				  
				  uniqOpensChkId.setChecked(true);
				  bouncedChkId.setChecked(true);
				  sentChkId.setChecked(true);
				  deleveredChkId.setChecked(true);
				  clicksChkId.setChecked(true);
				  statusPendingChkId.setChecked(true);
				  
				  sentSmsChkId.setChecked(true);
				  receivedSmsChkId.setChecked(true);
				  undeliveredSmsChkId.setChecked(true);
				  
				  
				  smsChkId.setChecked(true);
				  emailChkId.setChecked(true);
				  
				  onCheck$uniqOpensChkId();
				  onCheck$bouncedChkId();
				  onCheck$sentChkId();
				  onCheck$deleveredChkId();
				  onCheck$clicksChkId();
				  onCheck$statusPendingChkId();
				  
				  onCheck$sentSmsChkId();
				  onCheck$receivedSmsChkId();
				  onCheck$undeliveredSmsChkId();
			  }else if(item.getLabel().equals("Delivered")){
				  
				  logger.info("isSmsChecked  >>> "+isSmsChecked+" isemailChecked >>> "+isemailChecked);
				  
				  if(isemailChecked == true){
					  
					  uniqOpensChkId.setChecked(true);
					  sentChkId.setChecked(true);
					  deleveredChkId.setChecked(true);
					  clicksChkId.setChecked(true);
					  
					  onCheck$uniqOpensChkId();
					  onCheck$sentChkId();
					  onCheck$deleveredChkId();
					  onCheck$clicksChkId();
				  }
				  
				  if(isSmsChecked == true){
					  
					  sentSmsChkId.setChecked(true);
					  receivedSmsChkId.setChecked(true);
					  
					  
					  onCheck$sentSmsChkId();
					  onCheck$receivedSmsChkId();
				  }
				  
			  }else if(item.getLabel().equals("Bounced")){
				  
				  if(isemailChecked == true){
					  bouncedChkId.setChecked(true);
					  onCheck$bouncedChkId();
				  }
				  
				  
				  if(isSmsChecked == true){
					  undeliveredSmsChkId.setChecked(true);
					  onCheck$undeliveredSmsChkId();
				  }
				 
				  
			  }else if(item.getLabel().equals("Opens")){
				  
				  if(isemailChecked == true){
					  uniqOpensChkId.setChecked(true);
					  deleveredChkId.setChecked(true);
					  
					  
					  onCheck$uniqOpensChkId();
					  onCheck$deleveredChkId();
				  }
				 
			  }else if(item.getLabel().equals("Clicks")){
				  
				  if(isemailChecked == true){
					  deleveredChkId.setChecked(true);
					  clicksChkId.setChecked(true);
					  
					  
					  onCheck$deleveredChkId();
					  onCheck$clicksChkId();
				  }
				  
				  if(isSmsChecked == true){
					  
					  sentSmsChkId.setChecked(true);
					  receivedSmsChkId.setChecked(true);
					  
					  
					  onCheck$sentSmsChkId();
					  onCheck$receivedSmsChkId();
				  }
				  
			  }else if(item.getLabel().equals("Status Pending")){
				  if(isemailChecked == true){
					  statusPendingChkId.setChecked(true);
					  onCheck$statusPendingChkId();
				  }
				  
			  }
			  
			  
			  
			  
			  if(storeBandBoxId.getValue().equalsIgnoreCase("All")){

				  logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> ALL <<<<<<<<<<<<<<<<<<<<<<<<<<<");
				  
				  if(isemailChecked == true){
					  uniqOpensChkId.setChecked(true);
					  bouncedChkId.setChecked(true);
					  sentChkId.setChecked(true);
					  deleveredChkId.setChecked(true);
					  clicksChkId.setChecked(true);
					  statusPendingChkId.setChecked(true);
					  
					  onCheck$uniqOpensChkId();
					  onCheck$bouncedChkId();
					  onCheck$sentChkId();
					  onCheck$deleveredChkId();
					  onCheck$clicksChkId();
					  onCheck$statusPendingChkId();
				  }
				  
				  
				  if(isSmsChecked == true){
					  sentSmsChkId.setChecked(true);
					  receivedSmsChkId.setChecked(true);
					  undeliveredSmsChkId.setChecked(true);
					  
					  
					  
					  onCheck$sentSmsChkId();
					  onCheck$receivedSmsChkId();
					  onCheck$undeliveredSmsChkId();
					  
				  }

				 /* sentSmsChkId.setChecked(true);
				  receivedSmsChkId.setChecked(true);
				  undeliveredSmsChkId.setChecked(true);*/


				  /*smsChkId.setChecked(true);
				  emailChkId.setChecked(true);*/

				 /* onCheck$uniqOpensChkId();
				  onCheck$bouncedChkId();
				  onCheck$sentChkId();
				  onCheck$deleveredChkId();
				  onCheck$clicksChkId();*/

				 /* onCheck$sentSmsChkId();
				  onCheck$receivedSmsChkId();
				  onCheck$undeliveredSmsChkId();*/

			  }
			  
			  
			  
			  
	        }//for ends
				if(!statusPendingChkId.isChecked()){
					pendingReportsDivId.setVisible(false);
					notSentlabelId.setVisible(false);
					notSentLblId.setVisible(false);
				}
			drawChart(isContactDateType);
			
		}catch(Exception e){
			logger.error("Exception >>> ", e);
		}
		
		
		
	}
	
	
	
	
}
