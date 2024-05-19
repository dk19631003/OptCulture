package org.mq.marketer.campaign.controller.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UserActivities;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.AutoSmsQueueDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.event.PagingEvent;

@SuppressWarnings({ "serial", "rawtypes" })
public class AutoSmsReportsController extends GenericForwardComposer implements EventListener {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private MyDatebox fromDateboxId, toDateboxId;
	private String fromDate, endDate;
	private AutoSmsQueueDao autoSmsQueueDao;
	private UserActivitiesDaoForDML userActivitiesDaoForDML = null;
	private Users currentUser;
	private Listbox smsFilterLbId, pageSizeLbId;
	private Listitem giftCardIssuanceId, giftAmountExpirationId, giftCardExpirationId;
	private int selectedIndex;
	private Paging autoSmsRepListBottomPagingId;
	private TimeZone clientTimeZone;
	private Rows reportGridrowsId;
	private Session sessionScope = null;
	private String type = null;

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
		AUTOSMS_MAP.put("loyaltyAdjustment","Loyalty Adjustment");
		AUTOSMS_MAP.put("loyaltyIssuance","Loyalty Issuance");
		AUTOSMS_MAP.put("loyaltyRedemption","Loyalty Redemption");
	}

	public AutoSmsReportsController() {
		sessionScope = Sessions.getCurrent();
		currentUser = GetUser.getUserObj();
		clientTimeZone = (TimeZone) sessionScope.getAttribute("clientTimeZone");
		this.autoSmsQueueDao = (AutoSmsQueueDao) SpringUtil.getBean("autoSmsQueueDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Auto-Sms Reports", "", style, true);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Long userId = GetUser.getUserObj().getUserId();
		if(userActivitiesDaoForDML!=null) {
		UserActivities userActivity = new UserActivities("Visited auto sms report page", "Visited pages", Calendar.getInstance(),userId );
		userActivitiesDaoForDML.saveOrUpdate(userActivity);
		}
		autoSmsRepListBottomPagingId.addEventListener("onPaging", this);
		type = "'LoyaltyDetails' , 'LoyaltyGiftCardIssuance', 'LoyaltyTierUpgradation',"
				+ " 'LoyaltyEarningBonus', 'LoyaltyRewardExpiry', 'LoyaltyMembershipExpiry', 'LoyaltyGiftAmountExpiry', 'LoyaltyGiftCardExpiry','feedBackForm','specialRewards','welcomeSms','loyaltyAdjustment','loyaltyIssuance','loyaltyRedemption'";
		setDateValues();
		defaultSettings();
		if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
			for (Listitem item : smsFilterLbId.getItems()) {
					if (item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE) || 
							item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION) || 
							item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
						item.setVisible(false);
					}
					
				}
			}

	}

	private void defaultSettings() {
		fromDate = MyCalendar.calendarToString(getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		endDate = MyCalendar.calendarToString(getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR);
		selectedIndex = smsFilterLbId.getSelectedIndex();
		/*int totalCount = autoSmsQueueDao.findCountByDate(currentUser.getUserId(), fromDate, endDate,
				selectedIndex != 0 ? "'" + smsFilterLbId.getSelectedItem().getValue().toString() + "'" : type);
		autoSmsRepListBottomPagingId.setTotalSize(totalCount);*/
		autoSmsRepListBottomPagingId.setActivePage(0);
		autoSmsRepListBottomPagingId.setPageSize(Integer.valueOf(pageSizeLbId.getSelectedItem().getValue().toString()));
		List<Object[]> rowsToDraw = autoSmsQueueDao.fetchRecordsByDate(currentUser.getUserId(), fromDate, endDate,
				selectedIndex != 0 ? "'" + smsFilterLbId.getSelectedItem().getValue().toString() + "'" : type, 0,
				autoSmsRepListBottomPagingId.getPageSize(), orderby_colName_keywords, desc_Asc);
		drawRows(rowsToDraw);
	}

	@SuppressWarnings("unchecked")
	private void drawRows(List<Object[]> rowsToDraw) {
		Components.removeAllChildren(reportGridrowsId);
		for (Object[] obj : rowsToDraw) {
			Row row = new Row();

			Label tempLabel = new Label(!obj[7].toString().equalsIgnoreCase("0")
					? obj[8] != null && !obj[8].toString().trim().isEmpty() ? obj[8].toString().trim()
							: obj[0] != null && !obj[0].toString().trim().isEmpty() ? obj[0].toString()
									: " Not Available"
					: "Default Message");
			tempLabel.setStyle("cursor:pointer;color:blue;text-decoration: underline;");
			tempLabel.addEventListener("onClick", this);

			row.appendChild(tempLabel);
			row.appendChild(new Label(AUTOSMS_MAP.get(obj[1].toString())));
			row.appendChild(new Label(MyCalendar.calendarToString((Calendar) obj[2], MyCalendar.FORMAT_SCHEDULE_TIME, clientTimeZone)));
			row.appendChild(new Label(obj[3].toString()));
			row.appendChild(new Label(obj[4].toString()));
			row.appendChild(new Label(obj[5].toString()));
			row.appendChild(new Label(obj[6].toString()));
			row.setParent(reportGridrowsId);
			row.setValue((obj[1].toString() + Constants.ADDR_COL_DELIMETER) + (obj[7] != null ? obj[7].toString() : ""));
		}

	}

	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		if (event.getTarget() instanceof Label) {
			Label tempLable = (Label) event.getTarget();
			Row tempRow = (Row) tempLable.getParent();
			String selectedTemplate = (String) tempRow.getValue();
			StringBuilder sb = new StringBuilder();
			sb.append(selectedTemplate).append(",").append(fromDate).append(",").append(endDate);
			Long userId = GetUser.getUserObj().getUserId();
			if(userActivitiesDaoForDML!=null) {
			UserActivities userActivity = new UserActivities("Visited detailed auto sms report page", "Visited pages", Calendar.getInstance(),userId );
			userActivitiesDaoForDML.saveOrUpdate(userActivity);
			}
			sessionScope.setAttribute("selectedAutosmsReport", sb.toString());
			Redirect.goTo(PageListEnum.REPORT_AUTO_SMS_DETAILED_REPORT);

		} else if (event.getTarget() instanceof Paging) {
			Paging paging = (Paging) event.getTarget();
			int desiredPage = paging.getActivePage();

			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			autoSmsRepListBottomPagingId.setActivePage(desiredPage);

			List<Object[]> rowsToDraw = autoSmsQueueDao.fetchRecordsByDate(currentUser.getUserId(), fromDate, endDate,
					selectedIndex != 0 ? "'" + smsFilterLbId.getSelectedItem().getValue().toString() + "'" : type,
					ofs, (byte) pagingEvent.getPageable().getPageSize(), orderby_colName_keywords, desc_Asc);
			drawRows(rowsToDraw);
		}
	}

	private void setDateValues() {
		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :" + cal);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
		logger.debug("FromDate (server) :" + cal);
		fromDateboxId.setValue(cal);
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

		@SuppressWarnings("unused")
		String endDate = MyCalendar.calendarToString(serverToDateCal, MyCalendar.FORMAT_DATETIME_STYEAR);

		return serverToDateCal;

	}

	// Sorting Hard coded

	public String orderby_colName_keywords = "sentCount", desc_Asc = "desc";
	public List<Object[]> rowsToDraw1;

	public void desc2ascasc2desc() {
		if (desc_Asc == "desc")
			desc_Asc = "asc";
		else
			desc_Asc = "desc";

	}

	// Promotional Keywords
	public void onClick$sortbyAutoEmailCategory() {
		orderby_colName_keywords = "eq.type";
		desc2ascasc2desc();
		rowsToDraw1 = autoSmsQueueDao.fetchRecordsByDate(currentUser.getUserId(), fromDate, endDate,
				selectedIndex != 0 ? "'" + smsFilterLbId.getSelectedItem().getValue().toString() + "'" : type, 0,
				autoSmsRepListBottomPagingId.getPageSize(), orderby_colName_keywords, desc_Asc);
		drawRows(rowsToDraw1);
	}

	public void onClick$sortbyLastSentDate() {
		orderby_colName_keywords = "latestSentDate";
		desc2ascasc2desc();
		rowsToDraw1 = autoSmsQueueDao.fetchRecordsByDate(currentUser.getUserId(), fromDate, endDate,
				selectedIndex != 0 ? "'" + smsFilterLbId.getSelectedItem().getValue().toString() + "'" : type, 0,
				autoSmsRepListBottomPagingId.getPageSize(), orderby_colName_keywords, desc_Asc);
		drawRows(rowsToDraw1);
	}

	public void onClick$sortbyTotalSent() {
		orderby_colName_keywords = "sentCount";
		desc2ascasc2desc();
		rowsToDraw1 = autoSmsQueueDao.fetchRecordsByDate(currentUser.getUserId(), fromDate, endDate,
				selectedIndex != 0 ? "'" + smsFilterLbId.getSelectedItem().getValue().toString() + "'" : type, 0,
				autoSmsRepListBottomPagingId.getPageSize(), orderby_colName_keywords, desc_Asc);
		drawRows(rowsToDraw1);
	}

	public void onClick$sortbyDelivered() {
		orderby_colName_keywords = "delivered";
		desc2ascasc2desc();
		rowsToDraw1 = autoSmsQueueDao.fetchRecordsByDate(currentUser.getUserId(), fromDate, endDate,
				selectedIndex != 0 ? "'" + smsFilterLbId.getSelectedItem().getValue().toString() + "'" : type, 0,
				autoSmsRepListBottomPagingId.getPageSize(), orderby_colName_keywords, desc_Asc);
		drawRows(rowsToDraw1);
	}

	public void onClick$sortbyUniqueOpens() {
		orderby_colName_keywords = "uniopens";
		desc2ascasc2desc();
		rowsToDraw1 = autoSmsQueueDao.fetchRecordsByDate(currentUser.getUserId(), fromDate, endDate,
				selectedIndex != 0 ? "'" + smsFilterLbId.getSelectedItem().getValue().toString() + "'" : type, 0,
				autoSmsRepListBottomPagingId.getPageSize(), orderby_colName_keywords, desc_Asc);
		drawRows(rowsToDraw1);

	}

	public void onClick$sortbyUniqueClicks() {
		orderby_colName_keywords = "uniclicks";
		desc2ascasc2desc();
		rowsToDraw1 = autoSmsQueueDao.fetchRecordsByDate(currentUser.getUserId(), fromDate, endDate,
				selectedIndex != 0 ? "'" + smsFilterLbId.getSelectedItem().getValue().toString() + "'" : type, 0,
				autoSmsRepListBottomPagingId.getPageSize(), orderby_colName_keywords, desc_Asc);
		drawRows(rowsToDraw1);
	}

	public void onClick$getBitweenDatesBtnId() {
		Calendar fromCal = getStartDate();
		Calendar endCal = getEndDate();
		if (endCal.before(fromCal)) {
			MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red");
			return;
		}

		defaultSettings();
	}

	public void onClick$resetAnchId() {
		smsFilterLbId.setSelectedIndex(0);
		setDateValues();
		defaultSettings();
	}

	public void onSelect$pageSizeLbId() {
		autoSmsRepListBottomPagingId
				.setPageSize(Integer.valueOf(pageSizeLbId.getSelectedItem().getValue().toString()));
		autoSmsRepListBottomPagingId.setActivePage(0);
		List<Object[]> rowsToDraw = autoSmsQueueDao.fetchRecordsByDate(currentUser.getUserId(), fromDate, endDate,
				selectedIndex != 0 ? "'" + smsFilterLbId.getSelectedItem().getValue().toString() + "'" : type, 0,
				autoSmsRepListBottomPagingId.getPageSize(), orderby_colName_keywords, desc_Asc);
		drawRows(rowsToDraw);
	}

	public void onClick$exportBtnId() {
		JdbcResultsetHandler jdbcResultsetHandler = null;
		StringBuffer sb = null;
		BufferedWriter bw = null;
		ResultSet resultSet = null;
		try {
			if (reportGridrowsId.getChildren().size() == 0) {
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

			String filePath = usersParentDirectory + "/" + userName + "/List/download/AutoSMS_Report_"
					+ System.currentTimeMillis() + "." + ext;

			sb = new StringBuffer();
			File file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(file));
			sb.append(
					"\"Message Name\",\"Auto-Sms Category\",\"Last Sent Date\",\"Total Sent\",\"Delivered\",\"Unique Clicks\",\"Bounced\"\n");
			bw.write(sb.toString());

			String deliveredAndBouncedQuery = "COUNT(CASE dlr_status WHEN 'bounced'  THEN 1 ELSE NULL END) AS bounced ";
			deliveredAndBouncedQuery += ", COUNT(CASE dlr_status WHEN 'delivered' THEN 1 ELSE NULL END) AS delivered ";
			
			String query = "SELECT ct.template_name as templateName, eq.type, max(eq.sent_date) as latestSentDate, count(1) sentCount ,"+deliveredAndBouncedQuery+", eq.template_Id as temp_id , sum(IF(eq.clicks > 0, 1,0)) as clicks " +
			" FROM  auto_sms_queue eq LEFT OUTER JOIN auto_sms ct ON ct.auto_sms_id = eq.template_Id  WHERE  eq.user_id = "+currentUser.getUserId()+" "
					+ " AND eq.sent_date BETWEEN '"+fromDate+"' AND '"+endDate+"' AND eq.status = 'Sent' and dlr_status IS NOT NULL";
			
			query += " AND eq.type in ("
					+ (selectedIndex != 0 ? "'" + smsFilterLbId.getSelectedItem().getLabel().toString() + "'" : type)
					+ ") ";

			query += " GROUP BY eq.type, eq.template_Id order by sentCount ";
			jdbcResultsetHandler = new JdbcResultsetHandler();
			jdbcResultsetHandler.executeStmt(query);
			resultSet = jdbcResultsetHandler.getResultSet();
			while (resultSet.next()) {
				sb.setLength(0);
				sb.append("\"" + (resultSet.getString("templateName") != null ? resultSet.getString("templateName")
						: "Default Message") + "\",");
				sb.append("\"" + (AUTOSMS_MAP.get(resultSet.getString("type"))) + "\",");
				Calendar cal = null;
				Timestamp t = resultSet.getTimestamp("latestSentDate");
				if (t != null) {
					if (new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
					}
				}
				sb.append("\"" + MyCalendar.calendarToString(cal, MyCalendar.FORMAT_SCHEDULE_TIME, clientTimeZone)
						+ "\",");
				sb.append("\"" + resultSet.getLong("sentCount") + "\",");
				sb.append("\"" + resultSet.getLong("delivered") + "\",");
				sb.append("\"" + resultSet.getLong("clicks") + "\",");
				sb.append("\"" + resultSet.getLong("bounced") + "\"");
				sb.append("\n");

				bw.write(sb.toString());
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
}
