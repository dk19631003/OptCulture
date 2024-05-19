package org.mq.marketer.campaign.controller.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.MyTemplates;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.AutoEmailClicksDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.MyTemplatesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LineChartEngine;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;


public class DetailedAutoEmailReportController extends GenericForwardComposer{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Session sessionScope;
	private Label msgNameLblId, categoryLblId, deliveryReportLbId,deliveryTimeLbId,consolidatedDateLbId, sentLblId, deliveredLblId, bouncedLblId, opensLblId, clicksLblId, spamLblId,totalUniqueClicksLbID,totalClicksLbID,
	notSentId,notSentLblId,totalStatusPendingLbID;
	private CustomTemplatesDao customTemplatesDao ;
	private AutoEmailClicksDao autoEmailClicksDao ;
	private EmailQueueDao emailQueueDao;
	private Users currentUser;
	private MyDatebox fromDateboxId,toDateboxId;
	private Chart autoEmailReportsChartId;
	private String fromDate, endDate;
	private String selectedAutoEmail;
	private TimeZone clientTimeZone;
	private Checkbox sentChkId, deleveredChkId, bouncedChkId, uniqOpensChkId, clicksChkId,statusPendingChkId;
	private Listbox autoEmailReportsLbId,reportsPerPageLBId,urlsClickedReportsLbId,recipientsReportsPerPageLBId,statusPendingReportsPerPageLBId,statusPendingReportsLbId;
	private Textbox searchBoxId,recipientsSearchBoxId,statusPendingSearchBoxId;
	private Paging reportsLocationsPagingId,recipientsReportsLocationsPagingId,statusPendingReportsLocationsPagingId;
	private A viewEmailBtnId;
	private Div pendingReportsDivId;
	
	private String emailSatusTobeFetched;
	private Bandbox storeBandBoxId;
	
	private Listbox emailSmsControlsLbId;
	private static Map<String, String> MONTH_MAP = new HashMap<String, String>();
	private MyTemplatesDao myTemplatesDao;
	
	static {

		MONTH_MAP.put("1", "Jan");
		MONTH_MAP.put("2", "Feb");
		MONTH_MAP.put("3", "Mar");
		MONTH_MAP.put("4", "Apr");
		MONTH_MAP.put("5", "May");
		MONTH_MAP.put("6", "Jun");
		MONTH_MAP.put("7", "Jul");
		MONTH_MAP.put("8", "Aug");
		MONTH_MAP.put("9", "Sep");
		MONTH_MAP.put("10", "Oct");
		MONTH_MAP.put("11", "Nov");
		MONTH_MAP.put("12", "Dec");

	}
	private static Map<String, String> AUTOEMAIL_MAP = new HashMap<String, String>();

	static {
		AUTOEMAIL_MAP.put("needtochange", "Double Opt-in");
		AUTOEMAIL_MAP.put("TestParentalMail", "Parental Consent");
		AUTOEMAIL_MAP.put("LoyaltyDetails", "Loyalty Enrollment");
		AUTOEMAIL_MAP.put("WelcomeEmail", "Welcome Message");
		AUTOEMAIL_MAP.put("LoyaltyGiftCardIssuance", "Gift-Card Issuance");
		AUTOEMAIL_MAP.put("LoyaltyTierUpgradation", "Tier Upgradation");
		AUTOEMAIL_MAP.put("LoyaltyEarningBonus", "Earning Bonus");
		AUTOEMAIL_MAP.put("LoyaltyRewardExpiry", "Reward Expiration");
		AUTOEMAIL_MAP.put("LoyaltyMembershipExpiry", "Membership Expiration");
		AUTOEMAIL_MAP.put("LoyaltyGiftAmountExpiry", "Gift Amount Expiration");
		AUTOEMAIL_MAP.put("LoyaltyGiftCardExpiry", "Gift-Card Expiration");
		AUTOEMAIL_MAP.put("FeedBackEmail", "FeedBack Form");
		AUTOEMAIL_MAP.put("specialRewards", "Special Rewards");

	}
	
	public DetailedAutoEmailReportController(){
		customTemplatesDao = (CustomTemplatesDao) SpringUtil.getBean("customTemplatesDao");
		emailQueueDao = (EmailQueueDao) SpringUtil.getBean("emailQueueDao");
		autoEmailClicksDao = (AutoEmailClicksDao) SpringUtil.getBean("autoEmailclicksDao");
		myTemplatesDao = (MyTemplatesDao)SpringUtil.getBean("myTemplatesDao");
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Auto-Email Reports", "", style, true);
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		try {
			reportsLocationsPagingId.addEventListener("onPaging", this);
			recipientsReportsLocationsPagingId.addEventListener("onPaging", this);
			statusPendingReportsLocationsPagingId.addEventListener("onPaging", this);
			currentUser = GetUser.getUserObj();
			sessionScope = Sessions.getCurrent();
			clientTimeZone = (TimeZone) sessionScope.getAttribute("clientTimeZone");
			selectedAutoEmail = (String) sessionScope.getAttribute("selectedAutoEmail");
			if(selectedAutoEmail == null){
				Redirect.goTo(PageListEnum.REPORT_AUTO_EMAIL_REPORT);
			}
			categoryLblId.setValue(AUTOEMAIL_MAP.get(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0]));
			if(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 || selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1].toString().equalsIgnoreCase("0")  ){
				msgNameLblId.setValue("Default Message");
				viewEmailBtnId.setAttribute("isDefault", true);
			}else{
				List<CustomTemplates> customTempList  = customTemplatesDao.getSubscriptionFormById(Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]));
				msgNameLblId.setValue(customTempList.size() > 0 ?customTempList.get(0).getTemplateName() : "Not Available");
				viewEmailBtnId.setAttribute("isDefault", false);
				viewEmailBtnId.setAttribute("customTemp", customTempList.size() > 0 ?customTempList.get(0) : null);
			}
			defaultSettings();
			
			String timePeriod = " ("+MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+" - "+MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+")";
			deliveryTimeLbId.setValue(timePeriod); deliveryReportLbId.setValue(timePeriod); consolidatedDateLbId.setValue(timePeriod);
			recipientsReportsLocationsPagingId.setActivePage(0);
			recipientsReportsLocationsPagingId.setPageSize(Integer.valueOf(recipientsReportsPerPageLBId.getSelectedItem().getLabel()));
			statusPendingReportsLocationsPagingId.setPageSize(Integer.valueOf(statusPendingReportsPerPageLBId.getSelectedItem().getLabel()));
			int tempCount = autoEmailClicksDao.getClicksCountByTempId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
					selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), null);
			recipientsReportsLocationsPagingId.setTotalSize(tempCount);
			drawUrlClickedReport(0, recipientsReportsLocationsPagingId.getPageSize(), null);
			
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}
	}
	public void onChanging$recipientsSearchBoxId(InputEvent event) {
		String key = event.getValue();

		logger.info("got the key ::" + key);

		if (key.trim().length() != 0) {
			 int size = autoEmailClicksDao.getClicksCountByTempId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
						selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), key.trim());
			 recipientsReportsLocationsPagingId.setTotalSize(size);
			recipientsReportsLocationsPagingId.setActivePage(0);
			drawUrlClickedReport(0, recipientsReportsLocationsPagingId.getPageSize(),key.trim());

		}

		else {
			int size = autoEmailClicksDao.getClicksCountByTempId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
					selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), null);
			recipientsReportsLocationsPagingId.setTotalSize(size);
			recipientsReportsLocationsPagingId.setActivePage(0);
			drawUrlClickedReport(0, recipientsReportsLocationsPagingId.getPageSize(),null);

		}
	}
	private void drawUrlClickedReport(int startIndex, int size, String key){
		
		int count = urlsClickedReportsLbId.getItemCount();
		for (; count > 0; count--) {
			urlsClickedReportsLbId.removeItemAt(count - 1);
		}
		List<Object[]> list = null;
		try {
			list = autoEmailClicksDao.getClicksByTempId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
					selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), key, startIndex, size);
		} catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		Listitem listItem  = null;
		Listcell listCell = null;
		long totalUniqueCliclks=0;
		long totalClicks = 0;
		for(Object[] eachRow : list){
			listItem = new Listitem();
			
			listCell = new Listcell(eachRow[0].toString());
			listCell.setParent(listItem);
			listCell = new Listcell(eachRow[1].toString());
			listCell.setParent(listItem);
			listCell = new Listcell(eachRow[2].toString());
			listCell.setParent(listItem);
			
			totalUniqueCliclks += (Integer)eachRow[1];
			totalClicks += (Integer)eachRow[2];
			
			listItem.setParent(urlsClickedReportsLbId);
		}
		totalUniqueClicksLbID.setValue(totalUniqueCliclks+"");
		totalClicksLbID.setValue(totalClicks+"");
		
	}
	private void defaultSettings(){
		
		autoEmailReportsChartId.setEngine(new LineChartEngine());
		setDateValues();
		reportsLocationsPagingId.setPageSize(Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()));
		reportsLocationsPagingId.setActivePage(0);
		statusPendingReportsLocationsPagingId.setPageSize(Integer.valueOf(statusPendingReportsPerPageLBId.getSelectedItem().getValue().toString()));
		statusPendingReportsLocationsPagingId.setActivePage(0);
		 /*int size = emailQueueDao.getSizeByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
					selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, null);
		reportsLocationsPagingId.setTotalSize(size);*/
		
		
		
		//emailSatusTobeFetched = "'Success','spamreport','dropped','bounce','special_condtion_for_all'";
		emailSatusTobeFetched = "'Success','spamreport','dropped','bounce','special_condtion_for_all'";
		List<EmailQueue> currentList= emailQueueDao.getRowsByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
				selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, -1, -1, null,emailSatusTobeFetched);
		
		reportsLocationsPagingId.setTotalSize(currentList.size());
		
		
		
		setConsolidatedMetrics();
		
		populateEmailSmsControlsLbId();
		populateEmailSmsControlsLbIdAndSetDefaults();
		//setRequiredStatusToBeFetched();
		
		
		
		
		//emailSatusTobeFetched = "'Success','spamreport','dropped','bounce','special_condtion_for_all'";
		
		
		drawChart();
		drawAutoEmailReport( 0,  Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()),null);
		drawStatusPendingAutoEmailReport(0,Integer.valueOf(statusPendingReportsPerPageLBId.getSelectedItem().getValue().toString()),null);
		
	}
	private void drawAutoEmailReport(int firstResultset, int size, String key){
		int count = autoEmailReportsLbId.getItemCount();
		for (; count > 0; count--) {
			autoEmailReportsLbId.removeItemAt(count - 1);
		}
		List<EmailQueue> rows=emailQueueDao.getRowsByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
				selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, firstResultset, size, key,emailSatusTobeFetched);
		
		
		Listitem listItem  = null;
		Listcell listCell = null;
		for(EmailQueue eachRow : rows){
			listItem = new Listitem();
			
			listCell = new Listcell(MyCalendar.calendarToString(eachRow.getSentDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			listCell.setParent(listItem);
			if(eachRow.getToEmailId()== null && eachRow.getChildEmail() == null)continue;
			listCell = new Listcell(eachRow.getToEmailId()!=null ? eachRow.getToEmailId() : eachRow.getChildEmail());
			listCell.setParent(listItem);
			
			String status = eachRow.getDeliveryStatus();
			
			if (status != null){
				if (status.equalsIgnoreCase(Constants.DR_STATUS_DROPPED)
						|| status.equalsIgnoreCase(Constants.DR_STATUS_BOUNCE)|| status.equalsIgnoreCase(Constants.DR_STATUS_BOUNCED)) {
					status = Constants.DR_STATUS_BOUNCED;
				}else if(status.equalsIgnoreCase(Constants.DR_STATUS_SPAME)
						|| status.equalsIgnoreCase(Constants.DR_STATUS_SPAMMED)){
					status = Constants.DR_STATUS_SPAME;
				}
				/*if(status.equalsIgnoreCase(Constants.DR_STATUS_SUBMITTED)){
					status = "Not sent";
				}*/
			}else {
					status = "Not Available";
				}
			
			listCell = new Listcell(status);
			listCell.setParent(listItem);
			
			
			listCell = new Listcell(""+(eachRow.getOpens() == 0 ? "0" : eachRow.getOpens()));
			listCell.setParent(listItem);
			
			
			listCell = new Listcell(""+(eachRow.getClicks() == 0 ? "0" : eachRow.getClicks()));
			listCell.setParent(listItem);
			
			Hbox hbox = new Hbox();
			Image img = new Image(
					"/img/digi_receipt_Icons/View-receipt_icn.png");
			img.setStyle("margin-right:5px;cursor:pointer;");
			img.setTooltiptext("View Sent Email");
			img.setAttribute("imageEventName", "view");
			img.addEventListener("onClick", this);
			img.setParent(hbox);

			listCell = new Listcell();
			hbox.setParent(listCell);

			listCell.setParent(listItem);
			
			listItem.setValue(eachRow.getId());
			
			listItem.setParent(autoEmailReportsLbId);
			
		}
	}
	private void drawStatusPendingAutoEmailReport(int firstResultset, int size, String key){
		int count = statusPendingReportsLbId.getItemCount();
		for (; count > 0; count--) {
			statusPendingReportsLbId.removeItemAt(count - 1);
		}
		List<EmailQueue> rows=emailQueueDao.getStatusPendingRowsByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
				selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, firstResultset, size, key,"'Submitted'");
		logger.info("rows.get(0).getId()"+rows.get(0).getId());
		Long totalCount=rows.get(0).getId();
		if(totalCount!=0){
		Listitem listItem  = null;
		Listcell listCell = null;
		for(EmailQueue eachRow : rows){
			listItem = new Listitem();
			
			listCell = new Listcell(eachRow.getToEmailId()!=null ? eachRow.getToEmailId() : eachRow.getChildEmail());
			listCell.setParent(listItem);
			
			listCell = new Listcell(MyCalendar.calendarToString(eachRow.getSentDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			listCell.setParent(listItem);
						
			listItem.setParent(statusPendingReportsLbId);
			
		}
		if(statusPendingChkId.isChecked()){
			notSentId.setVisible(true);
			notSentLblId.setVisible(true);
			pendingReportsDivId.setVisible(true);
		}
		}
		totalStatusPendingLbID.setValue("Total :"+totalCount);
		logger.info("total:"+totalStatusPendingLbID.getValue());
	}

	private Chart drawChart(){
		try{
			Date dt = fromDateboxId.getValue();
			int tzOffSet =(clientTimeZone.getOffset(dt.getTime()) - Calendar.getInstance().getTimeZone().getOffset(dt.getTime()))/(1000*60);

			Calendar startDate = getStartCalendar();
			Calendar cendDate = getEndCalendar();
			/*String startDateStr = MyCalendar.calendarToString(startDate,
					MyCalendar.FORMAT_DATETIME_STYEAR);
			String endDateStr = MyCalendar.calendarToString(cendDate,
					MyCalendar.FORMAT_DATETIME_STYEAR);*/

			int diffDays = (int) ((cendDate.getTime().getTime() - startDate
					.getTime().getTime()) / (1000 * 60 * 60 * 24));

			int maxDays = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);

			

			String type = "";

			if (diffDays >= maxDays) {
				type = "Months";
				autoEmailReportsChartId.setXAxis(type);
			} else {
				type = "Days";
				autoEmailReportsChartId.setXAxis(type);
			}

			logger.info("DiffDays==" + diffDays + " Type=" + type + " MaxDays="
					+ maxDays);
			CategoryModel model = new SimpleCategoryModel();

			Map<String, Integer> sentMap = new HashMap<String, Integer>();
			Map<String, Integer> delMap = new HashMap<String, Integer>();
			Map<String, Integer> opensMap = new HashMap<String, Integer>();
			Map<String, Integer> clicksMap = new HashMap<String, Integer>();
			Map<String, Integer> bounceMap = new HashMap<String, Integer>();
			Map<String, Integer> statusPendingMap = new HashMap<String, Integer>();
			
			boolean isChecked[] = {sentChkId.isChecked(),deleveredChkId.isChecked(), bouncedChkId.isChecked(), uniqOpensChkId.isChecked(), clicksChkId.isChecked(),statusPendingChkId.isChecked()};
			Long templateId = null;
			if(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length != 1){
				templateId = Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]);
			}
			List<Object[]> list = emailQueueDao.findTotalReportRateByTemplateId(currentUser.getUserId(), isChecked , fromDate, endDate, selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0], templateId, type, tzOffSet);
			
			for(Object[] eachRow : list){
				int index = 1;
				if(sentChkId.isChecked()){
					sentMap.put(eachRow[0].toString(), Integer.parseInt(eachRow[index].toString()));				
					index +=1;
					}
		
			if(deleveredChkId.isChecked()){
				delMap.put(eachRow[0].toString(), Integer.parseInt(eachRow[index].toString()));
				index +=1 ;
			}
			if(bouncedChkId.isChecked()){
				bounceMap.put(eachRow[0].toString(), Integer.parseInt(eachRow[index].toString()));
				index +=1 ;
			}
			if(uniqOpensChkId.isChecked()){
				opensMap.put(eachRow[0].toString(), Integer.parseInt(eachRow[index].toString()));
				index +=1 ;
			}
			if(clicksChkId.isChecked()){
				clicksMap.put(eachRow[0].toString(), Integer.parseInt(eachRow[index].toString()));
				index +=1 ;
			}
			if(statusPendingChkId.isChecked()){
				statusPendingMap.put(eachRow[0].toString(), Integer.parseInt(eachRow[index].toString()));
				index +=1 ;
			}
			Calendar tempCal = Calendar.getInstance();
			tempCal.setTime(dt);
			cendDate.setTime(toDateboxId.getValue()); 
			String currDate = "";
			
			int monthsDiff = ((cendDate.get(Calendar.YEAR) * 12 + cendDate
					.get(Calendar.MONTH)) - (tempCal.get(Calendar.YEAR) * 12 + tempCal
					.get(Calendar.MONTH))) + 1;
			
			if (type.equals("Days")) {
				do {
					// logger.debug("in if days ========");
					currDate = "" + tempCal.get(Calendar.DATE);

					if (sentChkId.isChecked()) {
						// logger.debug("in if days ========"+sentMap.get(currDate));
						model.setValue("Sent", currDate, sentMap
								.containsKey(currDate) ? sentMap.get(currDate)
								: 0);
					}
					if (deleveredChkId.isChecked())
						model.setValue("Delivered", currDate, delMap
								.containsKey(currDate) ? delMap.get(currDate)
								: 0);
					if (bouncedChkId.isChecked())
						model.setValue(
								"Bounced",
								currDate,
								bounceMap.containsKey(currDate) ? bounceMap
										.get(currDate) : 0);
					if (statusPendingChkId.isChecked())
						model.setValue(
								"Status Pending",
								currDate,
								statusPendingMap.containsKey(currDate) ? statusPendingMap
										.get(currDate) : 0);

					if (uniqOpensChkId.isChecked())
						model.setValue("Unique Opens", currDate, opensMap
								.containsKey(currDate) ? opensMap.get(currDate)
								: 0);
					if (clicksChkId.isChecked())
						model.setValue(
								"Unique Clicks",
								currDate,
								clicksMap.containsKey(currDate) ? clicksMap
										.get(currDate) : 0);

					tempCal.set(Calendar.DATE, tempCal.get(Calendar.DATE) + 1);

				} while (tempCal.before(cendDate) || tempCal.equals(cendDate));
			}
			if (type.equals("Months")) {
				int i = 1;
				do {
					// logger.debug("executing ========"+monthsDiff);
					i++;

					currDate = "" + (tempCal.get(Calendar.MONTH) + 1);

					if (sentChkId.isChecked())
						model.setValue("Sent", MONTH_MAP.get(currDate), sentMap
								.containsKey(currDate) ? sentMap.get(currDate)
								: 0);
					if (deleveredChkId.isChecked())
						model.setValue(
								"Delivered",
								MONTH_MAP.get(currDate),
								delMap.containsKey(currDate) ? delMap
										.get(currDate) : 0);
					if (bouncedChkId.isChecked())
						model.setValue(
								"Bounced",
								MONTH_MAP.get(currDate),
								bounceMap.containsKey(currDate) ? bounceMap
										.get(currDate) : 0);
					if (statusPendingChkId.isChecked())
						model.setValue(
								"Status Pending",
								MONTH_MAP.get(currDate),
								statusPendingMap.containsKey(currDate) ? statusPendingMap
										.get(currDate) : 0);

					if (uniqOpensChkId.isChecked())
						model.setValue(
								"Unique Opens",
								MONTH_MAP.get(currDate),
								opensMap.containsKey(currDate) ? opensMap
										.get(currDate) : 0);
					if (clicksChkId.isChecked())
						model.setValue(
								"Unique Clicks",
								MONTH_MAP.get(currDate),
								clicksMap.containsKey(currDate) ? clicksMap
										.get(currDate) : 0);

					tempCal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH) + 1);

				} while (i <= monthsDiff);
			}

			}
			autoEmailReportsChartId.setModel(model);
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		return autoEmailReportsChartId;
	}
	private void setConsolidatedMetrics(){
		Long templateId = null;
		if(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length != 1){
			templateId = Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]);
		}
		Object obj[] = emailQueueDao.findConsolidatedReportByTemplateId(currentUser.getUserId(), fromDate, endDate, selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0], templateId);
	//sentLblId, deliveredLblId, bouncedLblId, opensLblId, clicksLblId, spamLblId;
		//sentLblId, deliveredLblId, bouncedLblId, opensLblId, clicksLblId, spamLblId,notSentLblId;
		sentLblId.setValue(""+obj[0]);
		deliveredLblId.setValue(""+obj[1]+ "("
				+ getPercentage(obj[1], obj[0]) + "%)");
		bouncedLblId.setValue(obj[2]+"");
		opensLblId.setValue(obj[3]+ "("+ getPercentage(obj[3], obj[0]) + "%)");
		clicksLblId.setValue(obj[4]+ "("+ getPercentage(obj[4], obj[0]) + "%)");
		spamLblId.setValue(obj[5]+"");
		if(statusPendingChkId.isChecked() && (Long)obj[6]!=0){
			notSentId.setVisible(true);
			notSentLblId.setVisible(true);
		}
		notSentLblId.setValue(obj[6]+"");
		
	}
	private void setDateValues(){
		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :" + cal);
		//cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 6);
		logger.debug("FromDate (server) :" + cal);
		fromDateboxId.setValue(cal);
		
		fromDate = MyCalendar.calendarToString(getStartDate(),
				MyCalendar.FORMAT_DATETIME_STYEAR);
		endDate = MyCalendar.calendarToString(getEndDate(),
				MyCalendar.FORMAT_DATETIME_STYEAR);
		
	}
	public void onClick$datesFilterBtnId() {
		Calendar fromCal = getStartDate();
		Calendar endCal = getEndDate();
		if (endCal.before(fromCal)) {
			MessageUtil.setMessage("'To' date must be later than 'From' date.",
					"color:red");
			setValues();
			return;
			
		}

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
        
        
        //logger.info("storeSelectedCount >>>>>> "+storeSelectedCount+"    storeItemCount     >>>>>>>>  "+storeItemCount);
        notSentId.setVisible(false);
		notSentLblId.setVisible(false);
		pendingReportsDivId.setVisible(false);
		
		setRequiredStatusToBeFetched();
		searchBoxId.setValue("");
		fromDate = MyCalendar.calendarToString(fromCal,
				MyCalendar.FORMAT_DATETIME_STYEAR);
		endDate = MyCalendar.calendarToString(endCal,
				MyCalendar.FORMAT_DATETIME_STYEAR);
		String timePeriod = " ("+MyCalendar.calendarToString(fromCal, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+" - "+MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+")";
		deliveryTimeLbId.setValue(timePeriod); deliveryReportLbId.setValue(timePeriod); consolidatedDateLbId.setValue(timePeriod);
		reportsLocationsPagingId.setActivePage(0);
		 
		/*int size = emailQueueDao.getSizeByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
					selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, null);*/
		List<EmailQueue> currentList= emailQueueDao.getRowsByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
				selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, -1, -1, null,emailSatusTobeFetched);
		
		reportsLocationsPagingId.setTotalSize(currentList.size());
		
		setConsolidatedMetrics();
		
		//setRequiredStatusToBeFetched();
		
		drawChart();
		drawAutoEmailReport( 0,  Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()),null);
		drawStatusPendingAutoEmailReport(0,Integer.valueOf(statusPendingReportsPerPageLBId.getSelectedItem().getValue().toString()),null);
		
	}
	
	public void onChanging$statusPendingSearchBoxId(InputEvent event) {
		setValues();
		String key = event.getValue();

		logger.info("got the key ::" + key);

		if (key.trim().length() != 0) {
			 int size = emailQueueDao.getStatusPendingByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
						selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, key.trim());
			 statusPendingReportsLocationsPagingId.setTotalSize(size);
			 statusPendingReportsLocationsPagingId.setActivePage(0);
			 //drawStatusPendingAutoEmailReport(0,Integer.valueOf(statusPendingReportsPerPageLBId.getSelectedItem().getValue().toString()),key);
			 int count = statusPendingReportsLbId.getItemCount();
				for (; count > 0; count--) {
					statusPendingReportsLbId.removeItemAt(count - 1);
				}
				List<EmailQueue> rows=emailQueueDao.getStatusPendingRowsByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
						selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, 0, Integer.valueOf(statusPendingReportsPerPageLBId.getSelectedItem().getValue().toString()), key,"'Submitted'");
				
				
				Long totalCount=rows.get(0).getId();
				if(totalCount!=0){
				Listitem listItem  = null;
				Listcell listCell = null;
				for(EmailQueue eachRow : rows){
					listItem = new Listitem();
					
					listCell = new Listcell(eachRow.getToEmailId()!=null ? eachRow.getToEmailId() : eachRow.getChildEmail());
					listCell.setParent(listItem);
					
					listCell = new Listcell(MyCalendar.calendarToString(eachRow.getSentDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
					listCell.setParent(listItem);
								
					listItem.setParent(statusPendingReportsLbId);
					
				}
				}
				totalStatusPendingLbID.setValue("Total :"+totalCount);
				logger.info("total:"+totalStatusPendingLbID.getValue());

		}

		else {
			int size = emailQueueDao.getStatusPendingByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
					selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, null);
			statusPendingReportsLocationsPagingId.setTotalSize(size);
			statusPendingReportsLocationsPagingId.setActivePage(0);
		//drawStatusPendingAutoEmailReport(0,Integer.valueOf(statusPendingReportsPerPageLBId.getSelectedItem().getValue().toString()),null);
			int count = statusPendingReportsLbId.getItemCount();
			for (; count > 0; count--) {
				statusPendingReportsLbId.removeItemAt(count - 1);
			}
			List<EmailQueue> rows=emailQueueDao.getStatusPendingRowsByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
					selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, 0, size, key,"'Submitted'");
			
			
			Listitem listItem  = null;
			Listcell listCell = null;
			for(EmailQueue eachRow : rows){
				listItem = new Listitem();
				
				listCell = new Listcell(eachRow.getToEmailId()!=null ? eachRow.getToEmailId() : eachRow.getChildEmail());
				listCell.setParent(listItem);
							
				listItem.setParent(statusPendingReportsLbId);
				
			}
			Long totalCount=rows.get(0).getId();
			totalStatusPendingLbID.setValue("Total :"+totalCount);
			logger.info("total:"+totalStatusPendingLbID.getValue());

		}

	}// onChanging$searchBoxId
	
	public void onChanging$searchBoxId(InputEvent event) {
		setValues();
		String key = event.getValue();

		logger.info("got the key ::" + key);

		if (key.trim().length() != 0) {
			 int size = emailQueueDao.getSizeByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
						selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, key.trim());
			reportsLocationsPagingId.setTotalSize(size);
			reportsLocationsPagingId.setActivePage(0);
			drawAutoEmailReport(0, Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()), key.trim());

		}

		else {
			int size = emailQueueDao.getSizeByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
					selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, null);
		reportsLocationsPagingId.setTotalSize(size);
		reportsLocationsPagingId.setActivePage(0);
		drawAutoEmailReport(0, Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()), null);

		}

	}// onChanging$searchBoxId

	public void onSelect$recipientsReportsPerPageLBId() {

		int tempCount = Integer.parseInt(recipientsReportsPerPageLBId
				.getSelectedItem().getLabel());
		
		recipientsReportsLocationsPagingId.setPageSize(tempCount);
		recipientsReportsPerPageLBId.setActivePage(0);
		drawUrlClickedReport(0, Integer.valueOf(recipientsReportsPerPageLBId.getSelectedItem().getValue().toString()), recipientsSearchBoxId.getValue() != null && recipientsSearchBoxId.getValue().trim().length() >0 ?recipientsSearchBoxId.getValue().trim():null);
		
	}
	
	public void onSelect$reportsPerPageLBId() {

		int tempCount = Integer.parseInt(reportsPerPageLBId
				.getSelectedItem().getLabel());
		
		reportsLocationsPagingId.setPageSize(tempCount);
		reportsLocationsPagingId.setActivePage(0);
		drawAutoEmailReport(0, Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()), searchBoxId.getValue() != null && searchBoxId.getValue().trim().length() >0 ?searchBoxId.getValue().trim():null);
		
	}
	public void onSelect$statusPendingReportsPerPageLBId() {

		int tempCount = Integer.parseInt(statusPendingReportsPerPageLBId
				.getSelectedItem().getLabel());
		
		statusPendingReportsLocationsPagingId.setPageSize(tempCount);
		statusPendingReportsLocationsPagingId.setActivePage(0);
		drawStatusPendingAutoEmailReport(0,Integer.valueOf(statusPendingReportsPerPageLBId.getSelectedItem().getValue().toString()),null);
		
	}
	private Window previewWin;
	private Html previewWin$html;
	
	public void onEvent(Event event) throws Exception{
		super.onEvent(event);
			if(event.getTarget() instanceof Paging) {
				Paging paging = (Paging) event.getTarget();
				int desiredPage = paging.getActivePage();

				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;

				if (paging.getId().equals("reportsLocationsPagingId")) {
					setValues();
					reportsLocationsPagingId.setActivePage(desiredPage);
					drawAutoEmailReport(ofs, (byte) pagingEvent
							.getPageable().getPageSize(), searchBoxId.getValue() != null && searchBoxId.getValue().trim().length() >0 ?searchBoxId.getValue().trim():null);
					
				}else if(paging.getId().equals("recipientsReportsLocationsPagingId")){
					recipientsReportsLocationsPagingId.setActivePage(desiredPage);
					drawUrlClickedReport(ofs, (byte) pagingEvent.getPageable().getPageSize(), recipientsSearchBoxId.getValue() != null && recipientsSearchBoxId.getValue().trim().length() >0 ?recipientsSearchBoxId.getValue().trim():null);
				}else if(paging.getId().equals("statusPendingReportsLocationsPagingId")){
					statusPendingReportsLocationsPagingId.setActivePage(desiredPage);
					drawStatusPendingAutoEmailReport(ofs, (byte) pagingEvent
							.getPageable().getPageSize(), statusPendingSearchBoxId.getValue() != null && statusPendingSearchBoxId.getValue().trim().length() >0 ?statusPendingSearchBoxId.getValue().trim():null);
				}
			}else if(event.getTarget() instanceof Image){
				
				Image img = (Image) event.getTarget();
				Listitem item = (Listitem) img.getParent().getParent().getParent();
				Long eqId = (Long) item.getValue();
				EmailQueue emailQueue = emailQueueDao.findEqById(eqId);
				
				if (emailQueue == null) {

					MessageUtil.setMessage("Can not process the Html content",
							"color:red;");
					return;

				}
				String htmlContent = Constants.STRING_NILL;
			
					if (emailQueue.getMessage() != null) {
						htmlContent = emailQueue.getMessage();
						
						if(htmlContent.contains("href='")){
							htmlContent = htmlContent.replaceAll("href='([^\"]+)'", "href=\"javascript:void(0)\" target=\"_self\" ");
							
						}
						if(htmlContent.contains("href=\"")){
							htmlContent = htmlContent.replaceAll("href=\"([^\"]+)\"", "href=\"javascript:void(0)\" target=\"_self\" ");
						}
						
						if(htmlContent.contains("href=")){
							htmlContent = htmlContent.replaceAll("href=([^>]+)>", "href=\"javascript:void(0)\" target=\"_self\" >");
						}
				htmlContent = Utility.mergeTagsForPreviewAndTestMail(htmlContent, "preview");	
				previewWin$html.setContent(htmlContent);
				previewWin.setVisible(true);
			
				
			}
					
				//logger.info("htmlContent >>> "+htmlContent);	
			}
				
	}
	public void onClick$datesResetBtnId(){
		
		setDateValues();
		reportsLocationsPagingId.setPageSize(Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()));
		reportsLocationsPagingId.setActivePage(0);
		 /*int size = emailQueueDao.getSizeByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
					selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, null);*/
		
		
		List<EmailQueue> currentList= emailQueueDao.getRowsByTemplateId(currentUser.getUserId(),selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]), 
				selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, -1, -1, null,emailSatusTobeFetched);
		
		reportsLocationsPagingId.setTotalSize(currentList.size());
		
		
		//reportsLocationsPagingId.setTotalSize(size);
		searchBoxId.setValue("");
		String timePeriod = " ("+MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+" - "+MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)+")";
		deliveryTimeLbId.setValue(timePeriod); deliveryReportLbId.setValue(timePeriod); consolidatedDateLbId.setValue(timePeriod);
		setConsolidatedMetrics();
		
		
		populateEmailSmsControlsLbIdAndSetDefaults();
		emailSmsControlsLbId.selectAll();
		emailSatusTobeFetched = "'Success','spamreport','dropped','bounce','special_condtion_for_all'";
		
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
		
		drawChart();
		drawAutoEmailReport( 0,  Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()),null);
		notSentId.setVisible(false);
		notSentLblId.setVisible(false);
		pendingReportsDivId.setVisible(false);
		drawStatusPendingAutoEmailReport(0,Integer.valueOf(statusPendingReportsPerPageLBId.getSelectedItem().getValue().toString()),null);
		
	}
	public Calendar getStartDate() {

		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		Calendar tempClientFromCal = fromDateboxId.getClientValue();
		serverFromDateCal.set(
				Calendar.HOUR_OF_DAY,
				serverFromDateCal.get(Calendar.HOUR_OF_DAY)
						- tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(
				Calendar.MINUTE,
				serverFromDateCal.get(Calendar.MINUTE)
						- tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);
		// String fromDate = MyCalendar.calendarToString(serverFromDateCal,
		// MyCalendar.FORMAT_DATETIME_STYEAR);

		return serverFromDateCal;

	}

	public Calendar getEndDate() {

		Calendar serverToDateCal = toDateboxId.getServerValue();

		Calendar tempClientToCal = toDateboxId.getClientValue();

		// change the time for startDate and endDate in order to consider right
		// from the
		// starting time of startDate to ending time of endDate

		serverToDateCal.set(Calendar.HOUR_OF_DAY,
				23 + serverToDateCal.get(Calendar.HOUR_OF_DAY)
						- tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(
				Calendar.MINUTE,
				59 + serverToDateCal.get(Calendar.MINUTE)
						- tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);

		String endDate = MyCalendar.calendarToString(serverToDateCal,
				MyCalendar.FORMAT_DATETIME_STYEAR);

		return serverToDateCal;

	}
	private Calendar getStartCalendar() {
		Calendar cal = null;

		if (fromDate != null) {
			cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				cal.setTime(sdf.parse(fromDate));
			} catch (ParseException e) {

				logger.error("Exception in setTime() method", e);

			}
		}
		return cal;

	}

	private Calendar getEndCalendar() {
		Calendar cal = null;

		if (endDate != null) {
			cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				cal.setTime(sdf.parse(endDate));
			} catch (ParseException e) {

				logger.error("Exception in setTime() method", e);

			}
		}
		return cal;

	}
	public String getPercentage(Object amount, Object totalAmount) {
		try {

			return Utility.getPercentage(((Long) amount).intValue(),
					(Long)totalAmount, 2);

		} catch (RuntimeException e) {
			logger.error("** Exception ", (Throwable) e);
			return "";
		}
	} // getPercentage
	public void onCheck$uniqOpensChkId() {
		setValues();
		drawChart();
	}

	public void onCheck$bouncedChkId() {
		setValues();
		drawChart();
	}

	public void onCheck$sentChkId() {
		setValues();
		drawChart();
	}

	public void onCheck$deleveredChkId() {
		setValues();
		drawChart();
	}

	public void onCheck$clicksChkId() {
		setValues();
		drawChart();
	}
	public void onCheck$statusPendingChkId() {
		setValues();
		drawChart();
	}
private void setValues(){
	fromDateboxId.setValue(getStartCalendar());
	toDateboxId.setValue(getEndCalendar());
	
	
}
public void onClick$exportBtnId(){
	setValues();
	JdbcResultsetHandler jdbcResultsetHandler = null;
	StringBuffer sb = null;
	BufferedWriter bw = null;
	ResultSet resultSet = null;
	try {
		if (autoEmailReportsLbId.getItemCount() == 0) {
			MessageUtil.setMessage(
					"No records exist in the selected search.",
					"color:red", "TOP");
			return;
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

		String filePath = usersParentDirectory + "/" + userName
				+ "/List/download/Detailed_AutoEmail_Report_"+
				+ System.currentTimeMillis() + "." + ext;

		sb = new StringBuffer();
		File file = new File(filePath);
		bw = new BufferedWriter(new FileWriter(file));
		sb.append("\"Email Sent Date\",\"Email Address\",\"Delivery Status\",\"Opens\",\"Clicks\"");
		sb.append("\n");
		bw.write(sb.toString());
		Long tempId = selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]);
		//String query = "SELECT sent_date as sentDate, to_email_id as email, child_email ,delivery_status as delStatus,opens,clicks FROM email_queue WHERE user_id="+currentUser.getUserId()+" AND type='"+selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString()+"' AND status='Sent' AND delivery_status in ("+emailSatusTobeFetched+") AND sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' ";
		
		String query;
		if(! emailSatusTobeFetched.contains("special_condtion_for_all") ){
			query = "SELECT sent_date as sentDate, to_email_id as email, child_email ,delivery_status as delStatus,opens,clicks FROM email_queue WHERE user_id="+currentUser.getUserId()+" AND type='"+selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString()+"' AND status='Sent' AND delivery_status in ("+emailSatusTobeFetched+") AND sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' ";
		}else {
			query = "SELECT sent_date as sentDate, to_email_id as email, child_email ,delivery_status as delStatus,opens,clicks FROM email_queue WHERE user_id="+currentUser.getUserId()+" AND type='"+selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString()+"' AND status='Sent'  AND sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' ";
		}
		
		
		
		
		if(((emailSatusTobeFetched.contains("dropped") || emailSatusTobeFetched.contains("bounce")) && ! emailSatusTobeFetched.contains("special_condtion_for_all"))){ 
			
			if( !emailSatusTobeFetched.contains("Success") && !emailSatusTobeFetched.contains("spamreport")){
				query += "  AND (delivery_status='bounce' OR delivery_status='dropped')  ";
			}else {
				query += "  AND (delivery_status='bounce' OR delivery_status='dropped')   AND  (delivery_status='spamreport' OR delivery_status='Success')   ";
			}
			
		}
		
		
		
		
		
		if(emailSatusTobeFetched.contains("_fetch_clicks_also_") && !emailSatusTobeFetched.contains("_fetch_opens_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
			query += " AND clicks is not null AND clicks !=0  ";
			
		}
		else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && !emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
			query += " AND opens is not null AND opens !=0  ";
		}
		else if(emailSatusTobeFetched.contains("_fetch_opens_also_") && emailSatusTobeFetched.contains("_fetch_clicks_also_") && ! emailSatusTobeFetched.contains("special_condtion_for_all")){
			query += " AND ((opens is not null AND opens !=0)   AND (clicks is not null AND clicks !=0))   ";
		}
		
		
		query = query + " AND delivery_status NOT IN('"+Constants.DR_STATUS_SUBMITTED+"')";
		
		
		if(tempId ==null || tempId == 0){
			query += " AND cust_temp_id is NULL ";
		}else{
			query += " AND cust_temp_id="+tempId+" ";
		}
		String key = searchBoxId.getValue();
		if(key != null && searchBoxId.getValue().trim().length() >0 ){
			query += " AND to_email_id like '%"+key.trim()+"%'";
		}
		query += " ORDER BY sentDate DESC ";
		
		logger.info("query "+query);
		/*String query = "SELECT click_url as url, SUM(click_count) as totalClicks, COUNT(click_count) as uniqueClicks  FROM auto_email_clicks ac INNER JOIN  email_queue eq ON ac.eq_id=eq.id " +
				" WHERE user_id=" + currentUser.getUserId() +" AND type ='"+selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString()+"' ";
		Long tempId = selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]);
				if(tempId ==null || tempId == 0){
					query += " AND cust_temp_id is NULL ";
				}else{
					query += " AND cust_temp_id="+tempId+" ";
				}
				String key = recipientsSearchBoxId.getValue();
				if(key != null && key.trim().length() > 0){
					query += " AND click_url like '%"+key.trim()+"%'";
				}
				
				query += " GROUP BY click_url";*/
		jdbcResultsetHandler = new JdbcResultsetHandler();
		jdbcResultsetHandler.executeStmt(query);
		resultSet = jdbcResultsetHandler.getResultSet();
		while (resultSet.next()) {
			sb.setLength(0);
			/*sb.append("\""+(resultSet.getString("templateName") != null ? resultSet
					.getString("templateName") : "Default "
					+ AUTOEMAIL_MAP.get(resultSet.getString("type"))) + "\",");
			sb.append("\""+(AUTOEMAIL_MAP.get(resultSet.getString("type"))) + "\",");*/
			Calendar cal = null;
			Timestamp t = resultSet.getTimestamp("sentDate");
			if (t != null) {
				if (new Date(t.getTime()) != null) {
					cal = Calendar.getInstance();
					cal.setTime(t);
				}
			}
			
			sb.append("\"");sb.append(MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));sb.append("\",");
			sb.append("\"");sb.append(resultSet.getString("email") != null ? resultSet.getString("email"):resultSet.getString("child_email"));sb.append("\",");
			
			String status = resultSet.getString("delStatus");
			
			if (status != null){
				if (status.equalsIgnoreCase(Constants.DR_STATUS_DROPPED)
						|| status.equalsIgnoreCase(Constants.DR_STATUS_BOUNCE)|| status.equalsIgnoreCase(Constants.DR_STATUS_BOUNCED)) {
					status = Constants.DR_STATUS_BOUNCED;
				}else if(status.equalsIgnoreCase(Constants.DR_STATUS_SPAME)
						|| status.equalsIgnoreCase(Constants.DR_STATUS_SPAMMED)){
					status = Constants.DR_STATUS_SPAME;
				}
			}else {
					status = "Not Available";
				}
			
			sb.append("\"");sb.append(status);sb.append("\",");
			sb.append("\"");sb.append(resultSet.getInt("opens"));sb.append("\",");
			sb.append("\"");sb.append(resultSet.getInt("clicks"));sb.append("\",");
			
			/*sb.append("\""+resultSet.getLong("uniopens") + "\",");
			sb.append("\""+resultSet.getLong("uniclicks") + "\"");*/
			
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
public void onClick$statusPendingExportBtnId(){
	setValues();
	JdbcResultsetHandler jdbcResultsetHandler = null;
	StringBuffer sb = null;
	BufferedWriter bw = null;
	ResultSet resultSet = null;
	try {
		if (statusPendingReportsLbId.getItemCount() == 0) {
			MessageUtil.setMessage(
					"No records exist in the selected search.",
					"color:red", "TOP");
			return;
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

		String filePath = usersParentDirectory + "/" + userName
				+ "/List/download/Detailed_AutoEmail_Report_"+
				+ System.currentTimeMillis() + "." + ext;

		sb = new StringBuffer();
		File file = new File(filePath);
		bw = new BufferedWriter(new FileWriter(file));
		sb.append("\"Email Address\",\"Sent Date\"");
		sb.append("\n");
		bw.write(sb.toString());
		Long tempId = selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[1]);
		//String query = "SELECT sent_date as sentDate, to_email_id as email, child_email ,delivery_status as delStatus,opens,clicks FROM email_queue WHERE user_id="+currentUser.getUserId()+" AND type='"+selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString()+"' AND status='Sent' AND delivery_status in ("+emailSatusTobeFetched+") AND sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' ";
		
		String query;
			query = "SELECT to_email_id as email,sent_date, child_email FROM email_queue WHERE user_id="+currentUser.getUserId()+" AND type='"+selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0].toString()+"' AND status='Sent' AND delivery_status in ('Submitted') AND sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' ";
		
		if(tempId ==null || tempId == 0){
			query += " AND cust_temp_id is NULL ";
		}else{
			query += " AND cust_temp_id="+tempId+" ";
		}
		String key = searchBoxId.getValue();
		if(key != null && searchBoxId.getValue().trim().length() >0 ){
			query += " AND to_email_id like '%"+key.trim()+"%'";
		}
		query += " ORDER BY sent_date DESC ";
		
		jdbcResultsetHandler = new JdbcResultsetHandler();
		jdbcResultsetHandler.executeStmt(query);
		resultSet = jdbcResultsetHandler.getResultSet();
		while (resultSet.next()) {
			sb.setLength(0);
			
			sb.append("\"");sb.append(resultSet.getString("email") != null ? resultSet.getString("email"):resultSet.getString("child_email"));sb.append("\",");
			
			Calendar cal = null;
			Timestamp t = resultSet.getTimestamp("sent_date");
			if (t != null) {
				if (new Date(t.getTime()) != null) {
					cal = Calendar.getInstance();
					cal.setTime(t);
				}
			}
			
			sb.append("\"");sb.append(MyCalendar.calendarToString(cal,MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));sb.append("\",");
			
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


public void onClick$viewEmailBtnId() {
	
		
	boolean isDefault = (Boolean) viewEmailBtnId.getAttribute("isDefault");
	CustomTemplates customTemplates =  (CustomTemplates) viewEmailBtnId.getAttribute("customTemp");
		
		 String htmlContent=Constants.STRING_NILL;
		 if(!isDefault && customTemplates==null ) {
			 
			 MessageUtil.setMessage("No content found to display. Seems the template is deleted. ", "color:red;");
			 return;
		 }else if(!isDefault && customTemplates!=null){
			 htmlContent = customTemplates.getHtmlText();
			 if(customTemplates.getHtmlText() != null && !customTemplates.getHtmlText().isEmpty()) {
				 	htmlContent = customTemplates.getHtmlText();
				}else if(customTemplates.getEditorType().equalsIgnoreCase(Constants.EDITOR_TYPE_BEE) && customTemplates.getMyTemplateId()!= null) {
					 MyTemplates myTemplates = myTemplatesDao.getTemplateByMytemplateId(customTemplates.getMyTemplateId());
					 if(myTemplates!=null) {
						 htmlContent = myTemplates.getContent();
					 }else {
						 MessageUtil.setMessage("No template was found configured in this auto-email message. Please edit the message to add a template to it.", "color:red", "TOP");
						 return; 
					 }
				}
		 }else if(isDefault){
			 String eachType =  AUTOEMAIL_MAP.get(selectedAutoEmail.split(Constants.ADDR_COL_DELIMETER)[0]);
			 
			 String value = null;
				if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_DOUBLEOPTIN)) {
					value = "optinMsgTemplate";
								
					
				}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_PARENTALCONSENT)) {
					value="parentalConsentMsgtemplate";
					
				}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN) ){
					
					value="LoyaltyOptinMsgTemplate";
				}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_WEBFORMEMAIL)){
					value ="welcomeMsgTemplate";
				}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION)) {
					value ="tierUpgradationMsgTemplate";
				}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION)) {
					value ="rewardExpMsgTemplate";
				}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION)) {
					value ="memExpMsgTemplate";
				}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS)) {
					value ="earnedThresholdBonusMsgTemplate";
				}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION )) {
					value ="giftAmountExpMsgTemplate";
				}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
					value ="giftCardExpMsgTemplate";
				}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE)) {
					value ="giftCardIssMsgTemplate";
				}else if(eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_SPECIAL_REWARDS)) {
					value ="specialRewards";
				}
				
				htmlContent = PropertyUtil.getPropertyValueFromDB(value);
			
		 }
		 if(htmlContent.contains("href='")){
			 htmlContent = htmlContent.replaceAll("href='([^\"]+)'", "href=\"#\" target=\"_self\" ");
				
			}
			if(htmlContent.contains("href=\"")){
				htmlContent = htmlContent.replaceAll("href=\"([^\"]+)\"", "href=\"#\" target=\"_self\" ");
			}
			
			if(htmlContent.contains("href=")){
				htmlContent = htmlContent.replaceAll("href=([^>]+)>", "href=\"javascript:void(0)\" target=\"_self\" >");
			}
			htmlContent = Utility.mergeTagsForPreviewAndTestMail(htmlContent, "preview");
			previewWin$html.setContent(htmlContent);
			previewWin.setVisible(true);
		 
	
}

private void populateEmailSmsControlsLbId(){
	
	
	Listitem deliveredChkIdListitem = new Listitem("Delivered");
	deliveredChkIdListitem.setParent(emailSmsControlsLbId);
	
	Listitem nonDeliveryChkIdListitem = new Listitem("Bounced");
	nonDeliveryChkIdListitem.setParent(emailSmsControlsLbId);
	
	Listitem statusPendingChkIdListitem = new Listitem("Status Pending");
	statusPendingChkIdListitem.setParent(emailSmsControlsLbId);
	
	Listitem uniqueOpensChkIdListitem = new Listitem("Opens");
	uniqueOpensChkIdListitem.setParent(emailSmsControlsLbId);
	
	Listitem uniqueClicksChkIdListitem = new Listitem("Clicks");
	uniqueClicksChkIdListitem.setParent(emailSmsControlsLbId);
	
	
	
	emailSmsControlsLbId.selectAll();
	
}


private void populateEmailSmsControlsLbIdAndSetDefaults() {
	
	/*Components.removeAllChildren(emailSmsControlsLbId);
	
	Listitem listitemAll = new Listitem("All");
	listitemAll.setParent(emailSmsControlsLbId);
	
	
	
	Listitem deliveredChkIdListitem = new Listitem("Delivery");
	deliveredChkIdListitem.setParent(emailSmsControlsLbId);
	
	Listitem nonDeliveryChkIdListitem = new Listitem("Bounce");
	nonDeliveryChkIdListitem.setParent(emailSmsControlsLbId);
	
	
	
	Listitem uniqueOpensChkIdListitem = new Listitem("Opens");
	uniqueOpensChkIdListitem.setParent(emailSmsControlsLbId);
	
	Listitem uniqueClicksChkIdListitem = new Listitem("Clicks");
	uniqueClicksChkIdListitem.setParent(emailSmsControlsLbId);
   
	
	//default setting
	emailSmsControlsLbId.setSelectedItem(deliveredChkIdListitem);*/
	
	
	//normal
	sentChkId.setChecked(true);

	//delivery related
	deleveredChkId.setChecked(true);
	uniqOpensChkId.setChecked(true);
	clicksChkId.setChecked(true);

	//bounce kind of
	bouncedChkId.setChecked(true);
	
	//status pending
	statusPendingChkId.setChecked(true);
	
	deleveredChkId.setChecked(true);
	sentChkId.setChecked(true);
	
	/*isemailChecked  = true;
	emailChkId.setChecked(true);
	
	isSmsChecked  = true;
	smsChkId.setChecked(true);*/
	
	
	
	/*uniqOpensChkId.setChecked(false);
	clicksChkId.setChecked(false);
	bouncedChkId.setChecked(false);
	deleveredChkId.setChecked(true);
	sentChkId.setChecked(false);
	sentSmsChkId.setChecked(false);
	receivedSmsChkId.setChecked(true);
	undeliveredSmsChkId.setChecked(false);*/
	
	
	
	}


private void setRequiredStatusToBeFetched(){
	
	
	
	Set<String> requiredStatusSet = new HashSet<String>();
	if(emailSmsControlsLbId.getSelectedItems() != null && emailSmsControlsLbId.getSelectedItems().size() > 0 ){
		
		//normal
		sentChkId.setChecked(false);
		
		//delivery related
		deleveredChkId.setChecked(false);
		uniqOpensChkId.setChecked(false);
		clicksChkId.setChecked(false);
		
		//bounce kind of
		bouncedChkId.setChecked(false);
		statusPendingChkId.setChecked(false);
		
		for (Listitem item : emailSmsControlsLbId.getSelectedItems()) {
			if(item.getLabel().equalsIgnoreCase("All")){

				requiredStatusSet.add("Success");
				requiredStatusSet.add("dropped");
				requiredStatusSet.add("bounce");
				requiredStatusSet.add("spamreport");
				requiredStatusSet.add("submitted");
				
				requiredStatusSet.add("special_condtion_for_all");
				
				
				sentChkId.setChecked(true);
				deleveredChkId.setChecked(true);
				bouncedChkId.setChecked(true);
				uniqOpensChkId.setChecked(true);
				clicksChkId.setChecked(true);
				statusPendingChkId.setChecked(true);

			}else if(item.getLabel().equals("Delivered")){

				requiredStatusSet.add("Success");
				requiredStatusSet.add("spamreport");
				
				
				deleveredChkId.setChecked(true);
				uniqOpensChkId.setChecked(true);
				clicksChkId.setChecked(true);

			}else if(item.getLabel().equals("Bounced")){

				requiredStatusSet.add("dropped");
				requiredStatusSet.add("bounce");

				
				bouncedChkId.setChecked(true);
			}else if(item.getLabel().equals("Status Pending")){

				requiredStatusSet.add("Submitted");

				
				statusPendingChkId.setChecked(true);
			}else if(item.getLabel().equals("Opens")){
				
				requiredStatusSet.add("Success");
				requiredStatusSet.add("spamreport");
				
				
				requiredStatusSet.add("_fetch_opens_also_");
				
				deleveredChkId.setChecked(true);
				uniqOpensChkId.setChecked(true);
				
			}else if(item.getLabel().equals("Clicks")){

				requiredStatusSet.add("Success");
				requiredStatusSet.add("spamreport");
				
				
				
				requiredStatusSet.add("_fetch_clicks_also_");
				deleveredChkId.setChecked(true);
				clicksChkId.setChecked(true);
			}

		}
		
		emailSatusTobeFetched = "";
		
		for(String aStatus : requiredStatusSet){
			if(emailSatusTobeFetched.trim().length() > 0) emailSatusTobeFetched += ",";
			emailSatusTobeFetched += "'"+aStatus+"'";
		}
		
		
		
		if(storeBandBoxId.getValue().equalsIgnoreCase("All")){
			
			sentChkId.setChecked(true);
			deleveredChkId.setChecked(true);
			bouncedChkId.setChecked(true);
			uniqOpensChkId.setChecked(true);
			clicksChkId.setChecked(true);

			
			
			emailSatusTobeFetched = "'Success','spamreport','dropped','bounce','special_condtion_for_all'";
		}
		
		
		
		
		logger.info(" status to be fetched >>> "+emailSatusTobeFetched);
		
		
		if(!statusPendingChkId.isChecked()){
			pendingReportsDivId.setVisible(false);
			notSentId.setVisible(false);
			notSentLblId.setVisible(false);
		}
		
		

	}else{
		
		MessageUtil.setMessage("Please select appropriate option(s) in drop-down", "color:red");
		return;
	}
	
	
}







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
		
	}else{
		emailSmsControlsLbId.setSelectedItems(selectedItemsSet);
	}
	
	
	if(selectedItemsSet.size() == 4 && selectedAll == false){
		emailSmsControlsLbId.setSelectedItem(null);
		
	}
	
	//setRequiredStatusToBeFetched();
}*/

public void onClick$backBtnId() throws Exception {
	Redirect.goToPreviousPage();
}

}
