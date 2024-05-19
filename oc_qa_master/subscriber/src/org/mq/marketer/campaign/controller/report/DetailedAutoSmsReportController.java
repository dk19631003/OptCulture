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
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.AutoSmsQueue;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.AutoSmsQueueDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LineChartEngine;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.zkoss.zk.ui.Component;
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

@SuppressWarnings({ "serial", "rawtypes" })
public class DetailedAutoSmsReportController extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Session sessionScope;
	private AutoSmsQueueDao autoSmsQueueDao;
	private AutoSMSDao autoSmsDao;
	private Users currentUser;

	private Label msgNameLblId, categoryLblId, deliveryReportLbId, deliveryTimeLbId, consolidatedDateLbId, sentLblId,
			deliveredLblId, bouncedLblId, clicksLblId,notSentId, notSentLblId,totalUniqueClicksLbID,totalClicksLbID;
	private MyDatebox fromDateboxId, toDateboxId;
	private Chart autoSMSReportsChartId;
	private String fromDate, endDate;
	private String selectedAutoSms;
	private TimeZone clientTimeZone;
	private Checkbox sentChkId, deleveredChkId, bouncedChkId,clicksChkId,statusPendingChkId;
	private Listbox autoSMSReportsLbId, reportsPerPageLBId,recipientsReportsPerPageLBId,
			statusPendingReportsPerPageLBId, statusPendingReportsLbId;
	private Textbox searchBoxId;
	private Paging reportsLocationsPagingId, recipientsReportsLocationsPagingId, statusPendingReportsLocationsPagingId;
	private A viewSMSBtnId;
	private Div pendingReportsDivId;

	private String smsSatusTobeFetched;
	private Bandbox storeBandBoxId;
	private Textbox recipientsSearchBoxId;
	private Listbox autoSmsControlsLbId,urlsClickedReportsLbId;
	private static Map<String, String> MONTH_MAP = new HashMap<String, String>();

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
	private static Map<String, String> AUTOSMS_MAP = new HashMap<String, String>();

	static {
		AUTOSMS_MAP.put("LoyaltyDetails", "Loyalty Enrollment");
		AUTOSMS_MAP.put("LoyaltyGiftCardIssuance", "Gift-Card Issuance");
		AUTOSMS_MAP.put("LoyaltyTierUpgradation", "Tier Upgradation");
		AUTOSMS_MAP.put("LoyaltyEarningBonus", "Earning Bonus");
		AUTOSMS_MAP.put("LoyaltyRewardExpiry", "Reward Expiration");
		AUTOSMS_MAP.put("LoyaltyMembershipExpiry", "Membership Expiration");
		AUTOSMS_MAP.put("LoyaltyGiftAmountExpiry", "Gift Amount Expiration");
		AUTOSMS_MAP.put("LoyaltyGiftCardExpiry", "Gift-Card Expiration");
		AUTOSMS_MAP.put("feedbackform", "FeedBack Form");
		AUTOSMS_MAP.put("specialRewards", "Special Rewards");
		AUTOSMS_MAP.put("welcomeSms", "Welcome SMS");
	}

	public DetailedAutoSmsReportController() {
		autoSmsQueueDao = (AutoSmsQueueDao) SpringUtil.getBean("autoSmsQueueDao");
		autoSmsDao = (AutoSMSDao) SpringUtil.getBean("autoSMSDao");
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Auto-SMS Reports", "", style, true);
	}

	@SuppressWarnings("unchecked")
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
			String selectedAutoSmsBuilder = (String) sessionScope.getAttribute("selectedAutosmsReport");
			if (selectedAutoSmsBuilder != null && !selectedAutoSmsBuilder.isEmpty()
					&& selectedAutoSmsBuilder.contains(",")) {
				String[] selectedAutoSmsBuilderArray = selectedAutoSmsBuilder.split(",");
				selectedAutoSms = selectedAutoSmsBuilderArray[0];
				String selectedAutoSmsStartDate = selectedAutoSmsBuilderArray[1];
				String selectedAutoSmsEndDate = selectedAutoSmsBuilderArray[2];
				SimpleDateFormat sdf = new SimpleDateFormat( MyCalendar.FORMAT_DATETIME_STYEAR);
				Date fromSelectedDate = sdf.parse(selectedAutoSmsStartDate);
				Date toSelectedDate = sdf.parse(selectedAutoSmsEndDate);
				Calendar calfrom = Calendar.getInstance();
				Calendar calto = Calendar.getInstance();
				calfrom.setTime(fromSelectedDate);
				calto.setTime(toSelectedDate);
				fromDateboxId.setValue(calfrom);
				toDateboxId.setValue(calto);
				fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
				endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
			}
			if (selectedAutoSms == null) {
				Redirect.goTo(PageListEnum.REPORT_AUTO_SMS_REPORT);
			}
			categoryLblId.setValue(AUTOSMS_MAP.get(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0]));
			if (selectedAutoSms.split(Constants.ADDR_COL_DELIMETER).length == 1
					|| selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[1].toString().equalsIgnoreCase("0")) {
				msgNameLblId.setValue("Default Message");
				viewSMSBtnId.setAttribute("isDefault", true);
			}

			// get SMS template name

			else {
				AutoSMS autoSmsTemplate = autoSmsDao.getAutoSmsTemplateById(Long.valueOf(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[1]));
				if (autoSmsTemplate != null) {
					msgNameLblId.setValue(autoSmsTemplate.getTemplateName());
				} else {
					msgNameLblId.setValue("Not Available");
				}
				viewSMSBtnId.setAttribute("isDefault", false);
				if (autoSmsTemplate != null) {
					viewSMSBtnId.setAttribute("smsTemplate", autoSmsTemplate);
				} else {
					viewSMSBtnId.setAttribute("smsTemplate", null);
				}
			}

			defaultSettings();

			String timePeriod = " ("+ MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone)
					+ " - "	+ MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone) + ")";
			deliveryTimeLbId.setValue(timePeriod);
			deliveryReportLbId.setValue(timePeriod);
			consolidatedDateLbId.setValue(timePeriod);
			recipientsReportsLocationsPagingId.setActivePage(0);
			recipientsReportsLocationsPagingId.setPageSize(Integer.valueOf(recipientsReportsPerPageLBId.getSelectedItem().getLabel()));
			statusPendingReportsLocationsPagingId.setPageSize(Integer.valueOf(statusPendingReportsPerPageLBId.getSelectedItem().getLabel()));
			drawUrlClickedReport(0, recipientsReportsLocationsPagingId.getPageSize(), null);
		} catch (Exception e) {
			logger.error("Exception :: ", e);
		}
	}


	private void defaultSettings() {

		autoSMSReportsChartId.setEngine(new LineChartEngine());
		//setDateValues();
		reportsLocationsPagingId.setPageSize(Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()));
		reportsLocationsPagingId.setActivePage(0);
		statusPendingReportsLocationsPagingId.setPageSize(Integer.valueOf(statusPendingReportsPerPageLBId.getSelectedItem().getValue().toString()));
		statusPendingReportsLocationsPagingId.setActivePage(0);
		smsSatusTobeFetched = "'Success','spamreport','dropped','bounce','special_condtion_for_all','bounced','status_sent'";

		List<AutoSmsQueue> currentList = autoSmsQueueDao.getRowsByTemplateId(currentUser.getUserId(),
				selectedAutoSms.split(Constants.ADDR_COL_DELIMETER).length == 1 ? null
						: Long.valueOf(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[1]),
						selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, -1, -1, null,
				smsSatusTobeFetched);

		reportsLocationsPagingId.setTotalSize(currentList.size());

		setConsolidatedMetrics();

		populateAutoSmsControlsLbId();
		populateAutoSmsControlsLbIdAndSetDefaults();

		drawChart();
		drawAutoSmsReport(0, Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()), null);

	}

	@SuppressWarnings("unchecked")
	private void drawAutoSmsReport(int firstResultset, int size, String key) {
		int count = autoSMSReportsLbId.getItemCount();
		for (; count > 0; count--) {
			autoSMSReportsLbId.removeItemAt(count - 1);
		}
		List<AutoSmsQueue> rows = autoSmsQueueDao.getRowsByTemplateId(currentUser.getUserId(),
				selectedAutoSms.split(Constants.ADDR_COL_DELIMETER).length == 1 ? null
						: Long.valueOf(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[1]),
				selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, firstResultset,
				size, key, smsSatusTobeFetched);

		Listitem listItem = null;
		Listcell listCell = null;
		for (AutoSmsQueue eachRow : rows) {
			listItem = new Listitem();

			listCell = new Listcell(MyCalendar.calendarToString(eachRow.getSentDate(),
					MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			listCell.setParent(listItem);
			if (eachRow.getToMobileNo() == null)
				continue;
			if (eachRow.getToMobileNo() != null) {
				listCell = new Listcell(eachRow.getToMobileNo());
			}
			listCell.setParent(listItem);

			String status = eachRow.getDlrStatus();

			if (status != null) {
				if (status.equalsIgnoreCase(Constants.DR_STATUS_DROPPED)
						|| status.equalsIgnoreCase(Constants.DR_STATUS_BOUNCE)
						|| status.equalsIgnoreCase(Constants.DR_STATUS_BOUNCED)) {
					status = Constants.DR_STATUS_BOUNCED;
				} else if (status.equalsIgnoreCase(Constants.DR_STATUS_SPAME)
						|| status.equalsIgnoreCase(Constants.DR_STATUS_SPAMMED)) {
					status = Constants.DR_STATUS_SPAME;
				}

				if (status.equalsIgnoreCase(Constants.DR_STATUS_SUBMITTED)) {
					status = "Not sent";
				}
				if (status.equalsIgnoreCase(Constants.SMS_STATUS_DELIVERED)) {
					status = Constants.SMS_STATUS_DELIVERED;
				}


			} else {
				status = "Not Available";
			}

			listCell = new Listcell(status);
			listCell.setParent(listItem);
			
			listCell = new Listcell(""+eachRow.getClicks());
			listCell.setParent(listItem);

			Hbox hbox = new Hbox();
			Image img = new Image("/img/digi_receipt_Icons/View-receipt_icn.png");
			img.setStyle("margin-right:5px;cursor:pointer;");
			img.setTooltiptext("View Sent SMS");
			img.setAttribute("imageEventName", "view");
			img.addEventListener("onClick", this);
			img.setParent(hbox);

			listCell = new Listcell();
			hbox.setParent(listCell);

			listCell.setParent(listItem);

			listItem.setValue(eachRow.getId());

			listItem.setParent(autoSMSReportsLbId);

		}
	}


	private Chart drawChart() {
		try {
			Date dt = fromDateboxId.getValue();
			int tzOffSet = (clientTimeZone.getOffset(dt.getTime())
					- Calendar.getInstance().getTimeZone().getOffset(dt.getTime())) / (1000 * 60);
			Calendar startDate = getStartCalendar();
			Calendar cendDate = getEndCalendar();
			/*
			 * String startDateStr = MyCalendar.calendarToString(startDate,
			 * MyCalendar.FORMAT_DATETIME_STYEAR); String endDateStr =
			 * MyCalendar.calendarToString(cendDate, MyCalendar.FORMAT_DATETIME_STYEAR);
			 */

			int diffDays = (int) ((cendDate.getTime().getTime() - startDate.getTime().getTime())
					/ (1000 * 60 * 60 * 24));

			int maxDays = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);

			String type = "";

			if (diffDays >= maxDays) {
				type = "Months";
				autoSMSReportsChartId.setXAxis(type);
			} else {
				type = "Days";
				autoSMSReportsChartId.setXAxis(type);
			}

			logger.info("DiffDays==" + diffDays + " Type=" + type + " MaxDays=" + maxDays);
			CategoryModel model = new SimpleCategoryModel();

			Map<String, Integer> sentMap = new HashMap<String, Integer>();
			Map<String, Integer> delMap = new HashMap<String, Integer>();
			Map<String, Integer> bounceMap = new HashMap<String, Integer>();
			Map<String, Integer> statusPending = new HashMap<String, Integer>();
			Map<String, Integer> clicksForChart = new HashMap<String, Integer>();

			boolean isChecked[] = { sentChkId.isChecked(), deleveredChkId.isChecked(), bouncedChkId.isChecked(),
					statusPendingChkId.isChecked(),clicksChkId.isChecked()};
			Long templateId = null;
			if (selectedAutoSms.split(Constants.ADDR_COL_DELIMETER).length != 1) {
				templateId = Long.valueOf(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[1]);
			}

			List<Object[]> list = autoSmsQueueDao.findTotalSmsReportRateByTemplateId(currentUser.getUserId(), isChecked,
					fromDate, endDate, selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0], templateId, type,
					tzOffSet);

			for (Object[] eachRow : list) {
				int index = 1;
				if (sentChkId.isChecked()) {
					sentMap.put(eachRow[0].toString(), Integer.parseInt(eachRow[index].toString()));
					index += 1;
				}
				if(deleveredChkId.isChecked()){
					delMap.put(eachRow[0].toString(), Integer.parseInt(eachRow[index].toString()));
					index +=1 ;
				}
				if(bouncedChkId.isChecked()){
					bounceMap.put(eachRow[0].toString(), Integer.parseInt(eachRow[index].toString()));
					index +=1 ;
				}
				if(statusPendingChkId.isChecked()){
					statusPending.put(eachRow[0].toString(), Integer.parseInt(eachRow[index].toString()));
					index +=1 ;
				}
				if(clicksChkId.isChecked()){
					clicksForChart.put(eachRow[0].toString(), Integer.parseInt(eachRow[index].toString()));
					index +=1 ;
				}


				Calendar tempCal = Calendar.getInstance();
				tempCal.setTime(dt);
				cendDate.setTime(toDateboxId.getValue());
				String currDate = "";

				int monthsDiff = ((cendDate.get(Calendar.YEAR) * 12 + cendDate.get(Calendar.MONTH))
						- (tempCal.get(Calendar.YEAR) * 12 + tempCal.get(Calendar.MONTH))) + 1;

				if (type.equals("Days")) {
					do { // logger.debug("in if days ========");
						currDate = "" + tempCal.get(Calendar.DATE);
						if (sentChkId.isChecked())
							model.setValue("Sent", currDate, sentMap.containsKey(currDate) ? sentMap.get(currDate) : 0);
						if (deleveredChkId.isChecked())
							model.setValue("Delivered", currDate, delMap.containsKey(currDate) ? delMap.get(currDate): 0);
						if (bouncedChkId.isChecked())
							model.setValue("Bounced",currDate,bounceMap.containsKey(currDate) ? bounceMap.get(currDate) : 0);
						if (statusPendingChkId.isChecked())
							model.setValue("Status Pending",currDate,statusPending.containsKey(currDate) ? statusPending.get(currDate) : 0);
						if (clicksChkId.isChecked())
							model.setValue("Clicks",currDate,clicksForChart.containsKey(currDate) ? clicksForChart.get(currDate) : 0);
						tempCal.set(Calendar.DATE, tempCal.get(Calendar.DATE) + 1);

					} while (tempCal.before(cendDate) || tempCal.equals(cendDate));
				}
				if (type.equals("Months")) {
					int i = 1;
					do { //
						logger.debug("executing ========" + monthsDiff);
						i++;

						currDate = "" + (tempCal.get(Calendar.MONTH) + 1);

						if (sentChkId.isChecked())
							model.setValue("Sent", MONTH_MAP.get(currDate),	sentMap.containsKey(currDate) ? sentMap.get(currDate) : 0);
						if (deleveredChkId.isChecked())
							model.setValue("Delivered",MONTH_MAP.get(currDate),delMap.containsKey(currDate) ? delMap.get(currDate) : 0);
						if (bouncedChkId.isChecked())
							model.setValue("Bounced",MONTH_MAP.get(currDate),bounceMap.containsKey(currDate) ? bounceMap.get(currDate) : 0);
						if (statusPendingChkId.isChecked())
							model.setValue("Status Pending",MONTH_MAP.get(currDate),statusPending.containsKey(currDate) ? statusPending.get(currDate) : 0);
						if (clicksChkId.isChecked())
							model.setValue("Clicks",MONTH_MAP.get(currDate),clicksForChart.containsKey(currDate) ? clicksForChart.get(currDate) : 0);
						tempCal.set(Calendar.MONTH, tempCal.get(Calendar.MONTH) + 1);

					} while (i <= monthsDiff);
				}

			}

			autoSMSReportsChartId.setModel(model);
		} catch (Exception e) {
			logger.error("Exception :: ", e);
		}
		return autoSMSReportsChartId;
	}

	private void setConsolidatedMetrics() {
		Long templateId = null;
		if (selectedAutoSms.split(Constants.ADDR_COL_DELIMETER).length != 1) {
			templateId = Long.valueOf(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[1]);
		}

		Object obj[] = autoSmsQueueDao.fetchRecordsByTemplateId(currentUser.getUserId(), fromDate, endDate,
				selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0], templateId);

		sentLblId.setValue("" + obj[0]);
		deliveredLblId.setValue("" + obj[1] + "(" + getPercentage(obj[1], obj[0]) + "%)");
		bouncedLblId.setValue(obj[2] + "");
		clicksLblId.setValue(obj[4] + "(" + getPercentage(obj[4], obj[0]) + "%)");
		notSentLblId.setValue(obj[5].toString());
	}

	private void setDateValues() {
		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :" + cal);
		// cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 29);
		logger.debug("FromDate (server) :" + cal);
		fromDateboxId.setValue(cal);

		fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);

	}

	public void onClick$datesFilterBtnId() {
		Calendar fromCal = getStartDate();
		Calendar endCal = getEndDate();
		if (endCal.before(fromCal)) {
			MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red");
			setValues();
			return;

		}

		int storeSelectedCount = autoSmsControlsLbId.getSelectedCount();
		int storeItemCount = autoSmsControlsLbId.getItemCount();
		if (storeSelectedCount == storeItemCount) {
			storeBandBoxId.setValue("All");
		} else if ((storeSelectedCount > 1) && !(storeSelectedCount == storeItemCount)) {
			storeBandBoxId.setValue("Multiple");
		} else if ((storeSelectedCount == 1) && !(storeSelectedCount == storeItemCount)) {
			storeBandBoxId.setValue(autoSmsControlsLbId.getSelectedItem().getLabel());
		} else if (autoSmsControlsLbId.getSelectedIndex() == -1) {
			storeBandBoxId.setValue("");
		}

		pendingReportsDivId.setVisible(false);

		setRequiredStatusToBeFetched();
		searchBoxId.setValue("");
		fromDate = MyCalendar.calendarToString(fromCal, MyCalendar.FORMAT_DATETIME_STYEAR);
		endDate = MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_DATETIME_STYEAR);
		String timePeriod = " ("
				+ MyCalendar.calendarToString(fromCal, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone) + " - "
				+ MyCalendar.calendarToString(endCal, MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone) + ")";
		deliveryTimeLbId.setValue(timePeriod);
		deliveryReportLbId.setValue(timePeriod);
		consolidatedDateLbId.setValue(timePeriod);
		reportsLocationsPagingId.setActivePage(0);


		List<AutoSmsQueue> currentList = autoSmsQueueDao.getRowsByTemplateId(currentUser.getUserId(),
				selectedAutoSms.split(Constants.ADDR_COL_DELIMETER).length == 1 ? null
						: Long.valueOf(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[1]),
				selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, -1, -1, null,
				smsSatusTobeFetched);

		reportsLocationsPagingId.setTotalSize(currentList.size());

		setConsolidatedMetrics();

		// setRequiredStatusToBeFetched();

		drawChart();
		drawAutoSmsReport(0, Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()), null);

	}

	public void onChanging$searchBoxId(InputEvent event) {
		setValues();
		String key = event.getValue();

		logger.info("got the key ::" + key);

		if (key.trim().length() != 0) {
			reportsLocationsPagingId.setActivePage(0);
			drawAutoSmsReport(0, Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()),
					key.trim());

		}

		else {
			reportsLocationsPagingId.setActivePage(0);
			drawAutoSmsReport(0, Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()), null);
		}

	}// onChanging$searchBoxId

	public void onSelect$recipientsReportsPerPageLBId() {

		int tempCount = Integer.parseInt(recipientsReportsPerPageLBId.getSelectedItem().getLabel());
		recipientsReportsLocationsPagingId.setPageSize(tempCount);
		recipientsReportsLocationsPagingId.setActivePage(0);
		drawUrlClickedReport(0, tempCount, recipientsSearchBoxId.getValue() != null && recipientsSearchBoxId.getValue().trim().length() >0 ?recipientsSearchBoxId.getValue().trim():null);
	}

	private void drawUrlClickedReport(int startIndex, int size, String key){
		
		int count = urlsClickedReportsLbId.getItemCount();
		for (; count > 0; count--) {
			urlsClickedReportsLbId.removeItemAt(count - 1);
		}
		List<Object[]> list = null;
		try {
			list = autoSmsQueueDao.getClicksByTempId(currentUser.getUserId(),selectedAutoSms.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[1]), 
					selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0].toString(), key, startIndex, size);
			
			int clickUrllistCount = autoSmsQueueDao.getClickCountByTempId(currentUser.getUserId(),selectedAutoSms.split(Constants.ADDR_COL_DELIMETER).length == 1 ?null: Long.valueOf(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[1]), 
					selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0].toString(), null);
			recipientsReportsLocationsPagingId.setTotalSize(clickUrllistCount);
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

	public void onSelect$reportsPerPageLBId() {

		int tempCount = Integer.parseInt(reportsPerPageLBId.getSelectedItem().getLabel());

		reportsLocationsPagingId.setPageSize(tempCount);
		reportsLocationsPagingId.setActivePage(0);
		drawAutoSmsReport(0, Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()),
				searchBoxId.getValue() != null && searchBoxId.getValue().trim().length() > 0
						? searchBoxId.getValue().trim()
						: null);

	}

	public void onSelect$statusPendingReportsPerPageLBId() {

		int tempCount = Integer.parseInt(statusPendingReportsPerPageLBId.getSelectedItem().getLabel());

		statusPendingReportsLocationsPagingId.setPageSize(tempCount);
		statusPendingReportsLocationsPagingId.setActivePage(0);

	}

	private Window previewWin;
	private Html previewWin$html;

	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		if (event.getTarget() instanceof Paging) {
			Paging paging = (Paging) event.getTarget();
			int desiredPage = paging.getActivePage();

			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;

			if (paging.getId().equals("reportsLocationsPagingId")) {
				setValues();
				reportsLocationsPagingId.setActivePage(desiredPage);
				drawAutoSmsReport(ofs, (byte) pagingEvent.getPageable().getPageSize(),
						searchBoxId.getValue() != null && searchBoxId.getValue().trim().length() > 0
								? searchBoxId.getValue().trim()
								: null);

			}
			if (paging.getId().equals("recipientsReportsLocationsPagingId")) {
				setValues();
				recipientsReportsLocationsPagingId.setActivePage(desiredPage);
				drawUrlClickedReport(ofs, (byte) pagingEvent.getPageable().getPageSize(), 
						recipientsSearchBoxId.getValue() != null && recipientsSearchBoxId.getValue().trim().length() >0 ?recipientsSearchBoxId.getValue().trim():null);

			}
		} else if (event.getTarget() instanceof Image) {

			Image img = (Image) event.getTarget();
			Listitem item = (Listitem) img.getParent().getParent().getParent();
			Long eqId = (Long) item.getValue();
			AutoSmsQueue autoSmsQueue = null;
			
				autoSmsQueue = autoSmsQueueDao.findById(eqId);

			if (autoSmsQueue == null) {

				MessageUtil.setMessage("Can not process the Html content", "color:red;");
				return;

			}
			String htmlContent = Constants.STRING_NILL;

			if (autoSmsQueue.getMessage() != null) {
				htmlContent = autoSmsQueue.getMessage();

				if (htmlContent.contains("href='")) {
					htmlContent = htmlContent.replaceAll("href='([^\"]+)'",
							"href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none; color=\"color: #000000;\"\"");

				}
				if (htmlContent.contains("href=\"")) {
					htmlContent = htmlContent.replaceAll("href=\"([^\"]+)\"",
							"href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none; color=\"color: #000000;\"\"");
				}

				if (htmlContent.contains("href=")) {
					htmlContent = htmlContent.replaceAll("href=([^>]+)>",
							"href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none; color=\"color: #000000;\">");
				}

				previewWin$html.setContent(htmlContent);
				previewWin.setVisible(true);

			}

		}

	}

	public void onClick$datesResetBtnId() {

		setDateValues();
		reportsLocationsPagingId
				.setPageSize(Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()));
		reportsLocationsPagingId.setActivePage(0);
		smsSatusTobeFetched = "'Success','spamreport','dropped','bounce','special_condtion_for_all','Status Pending','bounced','delivered'";
		List<AutoSmsQueue> currentList = autoSmsQueueDao.getRowsByTemplateId(currentUser.getUserId(),
				selectedAutoSms.split(Constants.ADDR_COL_DELIMETER).length == 1 ? null
						: Long.valueOf(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[1]),
				selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0].toString(), fromDate, endDate, -1, -1, null,
				smsSatusTobeFetched);

		reportsLocationsPagingId.setTotalSize(currentList.size());

		// reportsLocationsPagingId.setTotalSize(size);
		searchBoxId.setValue("");
		String timePeriod = " ("
				+ MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone) + " - "
				+ MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone) + ")";
		deliveryTimeLbId.setValue(timePeriod);
		deliveryReportLbId.setValue(timePeriod);
		consolidatedDateLbId.setValue(timePeriod);
		setConsolidatedMetrics();

		populateAutoSmsControlsLbIdAndSetDefaults();
		autoSmsControlsLbId.selectAll();
		//smsSatusTobeFetched = "'Success','spamreport','dropped','bounce','special_condtion_for_all','Status Pending','bounced','delivered'";

		autoSmsControlsLbId.selectAll();

		int storeSelectedCount = autoSmsControlsLbId.getSelectedCount();
		int storeItemCount = autoSmsControlsLbId.getItemCount();
		if (storeSelectedCount == storeItemCount) {
			storeBandBoxId.setValue("All");
		} else if ((storeSelectedCount > 1) && !(storeSelectedCount == storeItemCount)) {
			storeBandBoxId.setValue("Multiple");
		} else if ((storeSelectedCount == 1) && !(storeSelectedCount == storeItemCount)) {
			storeBandBoxId.setValue(autoSmsControlsLbId.getSelectedItem().getLabel());
		} else if (autoSmsControlsLbId.getSelectedIndex() == -1) {
			storeBandBoxId.setValue("");
		}

		drawChart();
		drawAutoSmsReport(0, Integer.valueOf(reportsPerPageLBId.getSelectedItem().getValue().toString()), null);
		pendingReportsDivId.setVisible(false);

	}

	public Calendar getStartDate() {

		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		Calendar tempClientFromCal = fromDateboxId.getClientValue();
		serverFromDateCal.set(Calendar.HOUR_OF_DAY,
				serverFromDateCal.get(Calendar.HOUR_OF_DAY) - tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(Calendar.MINUTE,
				serverFromDateCal.get(Calendar.MINUTE) - tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);

		return serverFromDateCal;

	}

	public Calendar getEndDate() {

		Calendar serverToDateCal = toDateboxId.getServerValue();

		Calendar tempClientToCal = toDateboxId.getClientValue();

		serverToDateCal.set(Calendar.HOUR_OF_DAY,
				23 + serverToDateCal.get(Calendar.HOUR_OF_DAY) - tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE,
				59 + serverToDateCal.get(Calendar.MINUTE) - tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);

		String endDate = MyCalendar.calendarToString(serverToDateCal, MyCalendar.FORMAT_DATETIME_STYEAR);

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
			if (Math.toIntExact((Long)amount)!=0 && Math.toIntExact((Long)totalAmount)!=0) {
				return Utility.getPercentage(((Long)amount), (Long) totalAmount, 2);
			} else {
				return "0";
			}

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

	private void setValues() {
		fromDateboxId.setValue(getStartCalendar());
		toDateboxId.setValue(getEndCalendar());

	}

	public void onClick$exportBtnId() {
		setValues();
		JdbcResultsetHandler jdbcResultsetHandler = null;
		StringBuffer sb = null;
		BufferedWriter bw = null;
		ResultSet resultSet = null;
		try {
			if (autoSMSReportsLbId.getItemCount() == 0) {
				MessageUtil.setMessage("No records exist in the selected search.", "color:red", "TOP");
				return;
			}

			String ext = "csv";
			String userName = currentUser.getUserName();

			String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");

			File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/");

			if (!downloadDir.exists()) {
				downloadDir.mkdirs();
			}

			String filePath = usersParentDirectory + "/" + userName + "/List/download/Detailed_AutoSMS_Report_"
					+ +System.currentTimeMillis() + "." + ext;

			sb = new StringBuffer();
			File file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(file));
			sb.append("\"SMS Sent Date\",\"Phone Number\",\"Delivery Status\",\"Clicks\"");
			sb.append("\n");
			bw.write(sb.toString());
			Long tempId = selectedAutoSms.split(Constants.ADDR_COL_DELIMETER).length == 1 ? null
					: Long.valueOf(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[1]);

			String query;
			if (!smsSatusTobeFetched.contains("special_condtion_for_all")) {
				query = "SELECT sent_date as sentDate, to_Mobile_No as mobile ,dlr_status as deliveryStatus,clicks FROM auto_sms_queue WHERE user_id="
						+ currentUser.getUserId() + " AND type='"+ selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0].toString()+ "' AND status='Sent' AND delivery_status in (" + smsSatusTobeFetched
						+ ") AND sent_date BETWEEN '" + fromDate + "' AND '" + endDate + "' ";
			} else {
				query = "SELECT sent_date as sentDate, to_Mobile_No as mobile, dlr_status as deliveryStatus,clicks FROM auto_sms_queue WHERE user_id="
						+ currentUser.getUserId() + " AND type='"+ selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0].toString()
						+ "' AND status='Sent'  AND sent_date BETWEEN '" + fromDate + "' AND '" + endDate + "' ";
			}

			if (((smsSatusTobeFetched.contains("dropped") || smsSatusTobeFetched.contains("bounced"))
					&& !smsSatusTobeFetched.contains("special_condtion_for_all"))) {

				if (!smsSatusTobeFetched.contains("Success") && !smsSatusTobeFetched.contains("spamreport")) {
					query += "  AND (dlr_status='bounced' OR delivery_status='dropped')  ";
				} else {
					query += "  AND (dlr_status='bounced' OR delivery_status='dropped')   AND  (dlr_status='spamreport' OR dlr_status='Success')   ";
				}

			}

			query = query + " AND dlr_status NOT IN('" + Constants.DR_STATUS_SUBMITTED + "')";

			if (tempId == null || tempId == 0) {
				query += " AND template_Id is NULL ";
			} else {
				query += " AND template_Id=" + tempId + " ";
			}
			String key = searchBoxId.getValue();
			if (key != null && searchBoxId.getValue().trim().length() > 0) {
				query += " AND to_Mobile_No like '%" + key.trim() + "%'";
			}
			query += " ORDER BY sentDate DESC ";

			logger.info("query " + query);
			jdbcResultsetHandler = new JdbcResultsetHandler();
			jdbcResultsetHandler.executeStmt(query);
			resultSet = jdbcResultsetHandler.getResultSet();
			while (resultSet.next()) {
				sb.setLength(0);
				Calendar cal = null;
				Timestamp t = resultSet.getTimestamp("sentDate");
				if (t != null) {
					if (new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
					}
				}

				sb.append("\"");
				sb.append(MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
				sb.append("\",");
				sb.append("\"");
				sb.append(resultSet.getString("mobile") != null ? resultSet.getString("mobile"):"");
				sb.append("\",");

				String status = resultSet.getString("deliveryStatus");

				if (status != null) {
					if (status.equalsIgnoreCase(Constants.DR_STATUS_DROPPED)
							|| status.equalsIgnoreCase(Constants.DR_STATUS_BOUNCE)
							|| status.equalsIgnoreCase(Constants.DR_STATUS_BOUNCED)) {
						status = Constants.DR_STATUS_BOUNCED;
					} else if (status.equalsIgnoreCase(Constants.DR_STATUS_SPAME)
							|| status.equalsIgnoreCase(Constants.DR_STATUS_SPAMMED)) {
						status = Constants.DR_STATUS_SPAME;
					}
				} else {
					status = "Not Available";
				}

				sb.append("\"");
				sb.append(status);
				sb.append("\",");
				sb.append("\"");
				sb.append(resultSet.getInt("clicks"));
				sb.append("\",");
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

	public void onClick$statusPendingExportBtnId() {
		setValues();
		JdbcResultsetHandler jdbcResultsetHandler = null;
		StringBuffer sb = null;
		BufferedWriter bw = null;
		ResultSet resultSet = null;
		try {
			if (statusPendingReportsLbId.getItemCount() == 0) {
				MessageUtil.setMessage("No records exist in the selected search.", "color:red", "TOP");
				return;
			}

			String ext = "csv";
			String userName = currentUser.getUserName();

			String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");

			File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/");

			if (!downloadDir.exists()) {
				downloadDir.mkdirs();
			}

			String filePath = usersParentDirectory + "/" + userName + "/List/download/Detailed_AutoSms_Report_"
					+ +System.currentTimeMillis() + "." + ext;

			sb = new StringBuffer();
			File file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(file));
			sb.append("\"Phone Number\",\"Sent Date\"");
			sb.append("\n");
			bw.write(sb.toString());
			Long tempId = selectedAutoSms.split(Constants.ADDR_COL_DELIMETER).length == 1 ? null
					: Long.valueOf(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[1]);

			String query;
			query = "SELECT to_email_id as email,sent_date, child_email FROM email_queue WHERE user_id="
					+ currentUser.getUserId() + " AND type='"
					+ selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0].toString()
					+ "' AND status='Sent' AND delivery_status in ('Submitted') AND sent_date BETWEEN '" + fromDate
					+ "' AND '" + endDate + "' ";

			if (tempId == null || tempId == 0) {
				query += " AND cust_temp_id is NULL ";
			} else {
				query += " AND cust_temp_id=" + tempId + " ";
			}
			String key = searchBoxId.getValue();
			if (key != null && searchBoxId.getValue().trim().length() > 0) {
				query += " AND to_email_id like '%" + key.trim() + "%'";
			}
			query += " ORDER BY sent_date DESC ";

			jdbcResultsetHandler = new JdbcResultsetHandler();
			jdbcResultsetHandler.executeStmt(query);
			resultSet = jdbcResultsetHandler.getResultSet();
			while (resultSet.next()) {
				sb.setLength(0);

				sb.append("\"");
				sb.append(resultSet.getString("email") != null ? resultSet.getString("email")
						: resultSet.getString("child_email"));
				sb.append("\",");

				Calendar cal = null;
				Timestamp t = resultSet.getTimestamp("sent_date");
				if (t != null) {
					if (new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
					}
				}

				sb.append("\"");
				sb.append(MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
				sb.append("\",");

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

	public void onClick$viewSMSBtnId() {

		boolean isDefault = (Boolean) viewSMSBtnId.getAttribute("isDefault");
		AutoSMS autoSmsTemp = (AutoSMS) viewSMSBtnId.getAttribute("smsTemplate");

		String messageContent = Constants.STRING_NILL;
		if (!isDefault && autoSmsTemp == null) {

			MessageUtil.setMessage("No content found to display. Seems the template is deleted. ", "color:red;");
			return;
		} else if (!isDefault && autoSmsTemp != null) {
			messageContent = autoSmsTemp.getMessageContent();
			if (autoSmsTemp.getMessageContent() != null && !autoSmsTemp.getMessageContent().isEmpty()) {
				messageContent = autoSmsTemp.getMessageContent();
			}
		} else if (isDefault) {
			String eachType = AUTOSMS_MAP.get(selectedAutoSms.split(Constants.ADDR_COL_DELIMETER)[0]);

			String value = null;
			if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_LOYALTYOPTIN)) {
				value = "LoyaltyOptinMsgTemplate";
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_TIER_UPGRADATION)) {
				value = "tierUpgradationMsgTemplate";
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_REWARD_EXPIRATION)) {
				value = "rewardExpMsgTemplate";
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_MEMBERSHIP_EXPIRATION)) {
				value = "memExpMsgTemplate";
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_EARNED_BONUS)) {
				value = "earnedThresholdBonusMsgTemplate";
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION)) {
				value = "giftAmountExpMsgTemplate";
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
				value = "giftCardExpMsgTemplate";
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE)) {
				value = "giftCardIssMsgTemplate";
			} else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_FEEDBACK_FORM)) {
				value = "feedBackform";
			}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_SPECIAL_REWARDS)) {
				value = "specialRewards";
			}else if (eachType.equals(Constants.CUSTOM_TEMPLATE_TYPE_WELCOME_SMS)) {
				value = "welcomeSms";
			}
			
			messageContent = PropertyUtil.getPropertyValueFromDB(value);

		}
		if (messageContent.contains("href='")) {
			messageContent = messageContent.replaceAll("href='([^\"]+)'",
					"href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");

		}
		if (messageContent.contains("href=\"")) {
			messageContent = messageContent.replaceAll("href=\"([^\"]+)\"",
					"href=\"#\" target=\"_self\" style=\"text-decoration: none;\"");
		}

		if (messageContent.contains("href=")) {
			messageContent = messageContent.replaceAll("href=([^>]+)>",
					"href=\"javascript:void(0)\" target=\"_self\" style=\"text-decoration: none; color=\"color: #000000;\">");
		}

		previewWin$html.setContent(messageContent);
		previewWin.setVisible(true);

	}

	private void populateAutoSmsControlsLbId() {

		Listitem sentChkIdListitem = new Listitem("Sent");
		sentChkIdListitem.setParent(autoSmsControlsLbId);

		Listitem deliveredChkIdListitem = new Listitem("Delivered");
		deliveredChkIdListitem.setParent(autoSmsControlsLbId);

		Listitem nonDeliveryChkIdListitem = new Listitem("Bounced");
		nonDeliveryChkIdListitem.setParent(autoSmsControlsLbId);

		Listitem statusPendingChkIdListitem = new Listitem("Status Pending");
		statusPendingChkIdListitem.setParent(autoSmsControlsLbId);
		
		Listitem uniqueClicksChkIdListitem = new Listitem("Clicks");
		uniqueClicksChkIdListitem.setParent(autoSmsControlsLbId);

		autoSmsControlsLbId.selectAll();

	}

	private void populateAutoSmsControlsLbIdAndSetDefaults() {

		// normal
		sentChkId.setChecked(true);

		// delivery related
		deleveredChkId.setChecked(true);
		// clicksLblId.setChecked(true);
		clicksChkId.setChecked(true);

		// bounce kind of
		bouncedChkId.setChecked(true);

		// status pending
		statusPendingChkId.setChecked(true);
	}

	private void setRequiredStatusToBeFetched() {

		Set<String> requiredStatusSet = new HashSet<String>();
		if (autoSmsControlsLbId.getSelectedItems() != null && autoSmsControlsLbId.getSelectedItems().size() > 0) {

			// normal
			sentChkId.setChecked(false);

			// delivery related
			deleveredChkId.setChecked(false);
			clicksChkId.setChecked(false);

			// bounce kind of
			bouncedChkId.setChecked(false);
			statusPendingChkId.setChecked(false);

			for (Listitem item : autoSmsControlsLbId.getSelectedItems()) {
				if (item.getLabel().equalsIgnoreCase("All")) {

					requiredStatusSet.add("Success");
					requiredStatusSet.add("dropped");
					requiredStatusSet.add("bounce");
					requiredStatusSet.add("bounced");
					requiredStatusSet.add("submitted");
					requiredStatusSet.add("Status Pending");

					requiredStatusSet.add("special_condtion_for_all");

					sentChkId.setChecked(true);
					deleveredChkId.setChecked(true);
					bouncedChkId.setChecked(true);
					clicksChkId.setChecked(true);
					statusPendingChkId.setChecked(true);

				} else if (item.getLabel().equals("Delivered")) {

					requiredStatusSet.add("delivered");
					requiredStatusSet.add("spamreport");
					requiredStatusSet.add("special_condtion_for_all");

					deleveredChkId.setChecked(true);

				} else if (item.getLabel().equals("Bounced")) {

					requiredStatusSet.add("dropped");
					requiredStatusSet.add("bounce");
					requiredStatusSet.add("bounced");
					requiredStatusSet.add("special_condtion_for_all");

					bouncedChkId.setChecked(true);
				} else if (item.getLabel().equals("Clicks")) {

					requiredStatusSet.add("_fetch_clicks_also_");
					clicksChkId.setChecked(true);
					
				} else if (item.getLabel().equals("Status Pending")) {
					requiredStatusSet.add("Status Pending");
					requiredStatusSet.add("special_condtion_for_all");
					statusPendingChkId.setChecked(true);
				} else if (item.getLabel().equals("Sent")) {
					requiredStatusSet.add("status_sent");
					sentChkId.setChecked(true);
				} 
				
			}

			smsSatusTobeFetched = "";

			for (String aStatus : requiredStatusSet) {
				if (smsSatusTobeFetched.trim().length() > 0)
					smsSatusTobeFetched += ",";
				smsSatusTobeFetched += "'" + aStatus + "'";
			}

			if (storeBandBoxId.getValue().equalsIgnoreCase("All")) {

				sentChkId.setChecked(true);
				deleveredChkId.setChecked(true);
				bouncedChkId.setChecked(true);
				// uniqOpensChkId.setChecked(true);
				clicksChkId.setChecked(true);

				smsSatusTobeFetched = "'Success','spamreport','dropped','bounce','special_condtion_for_all','Status Pending','bounced','delivered'";
			}

			logger.info(" status to be fetched >>> " + smsSatusTobeFetched);

		} else {

			MessageUtil.setMessage("Please select appropriate option(s) in drop-down", "color:red");
			return;
		}

	}

	public void onClick$backBtnId() throws Exception {
		//Redirect.goToPreviousPage();
		Redirect.goTo(PageListEnum.REPORT_AUTO_SMS_REPORT);
	}

}
