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
import org.mq.marketer.campaign.dao.EmailQueueDao;
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

public class AutoEmailReportsController extends GenericForwardComposer implements EventListener{
	private static final Logger logger = LogManager
			.getLogger(Constants.SUBSCRIBER_LOGGER);
	private MyDatebox fromDateboxId, toDateboxId;
	private String fromDate, endDate;
	private EmailQueueDao emailQueueDao;
	private UserActivitiesDaoForDML userActivitiesDaoForDML = null;
	private Users currentUser;
	private Listbox emailFilterLbId, pageSizeLbId;
	private Listitem giftCardIssuanceId, giftAmountExpirationId, giftCardExpirationId;
	private int selectedIndex;
	private Paging autoEmailRepListBottomPagingId;
	private TimeZone clientTimeZone;
	private Rows reportGridrowsId;
	private Session sessionScope = null;
	private String type = null;

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
		AUTOEMAIL_MAP.put("loyaltyAdjustment","Loyalty Adjustment");
		AUTOEMAIL_MAP.put("loyaltyIssuance","Loyalty Issuance");
		AUTOEMAIL_MAP.put("loyaltyRedemption","Loyalty Redemption");
	}

	public AutoEmailReportsController() {
		sessionScope = Sessions.getCurrent();
		currentUser = GetUser.getUserObj();
		clientTimeZone = (TimeZone) sessionScope.getAttribute("clientTimeZone");
		this.emailQueueDao = (EmailQueueDao) SpringUtil
				.getBean("emailQueueDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Auto-Email Reports", "", style, true);

	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Long userId = GetUser.getUserObj().getUserId();
		if(userActivitiesDaoForDML!=null) {
		UserActivities userActivity = new UserActivities("Visited auto email report page", "Visited pages", Calendar.getInstance(),userId );
		userActivitiesDaoForDML.saveOrUpdate(userActivity);
		}
		autoEmailRepListBottomPagingId.addEventListener("onPaging", this);
		type = "'needtochange', 'TestParentalMail', 'LoyaltyDetails', 'WelcomeEmail', 'LoyaltyGiftCardIssuance', 'LoyaltyTierUpgradation',"
				+ " 'LoyaltyEarningBonus', 'LoyaltyRewardExpiry', 'LoyaltyMembershipExpiry', 'LoyaltyGiftAmountExpiry', 'LoyaltyGiftCardExpiry','FeedBackEmail','specialRewards','loyaltyAdjustment','loyaltyIssuance','loyaltyRedemption' ";
		setDateValues();
		defaultSettings();
		
	if(OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())){
		for (Listitem item : emailFilterLbId.getItems()) {
				if (item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_ISSUANCE) || 
						item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_AMOUNT_EXPIRATION) || 
						item.getLabel().equals(Constants.CUSTOM_TEMPLATE_TYPE_GIFT_CARD_EXPIRATION)) {
					item.setVisible(false);
				}
				
			}
		}
	}

	private void defaultSettings() {
		fromDate = MyCalendar.calendarToString(getStartDate(),
				MyCalendar.FORMAT_DATETIME_STYEAR);
		endDate = MyCalendar.calendarToString(getEndDate(),
				MyCalendar.FORMAT_DATETIME_STYEAR);
		selectedIndex = emailFilterLbId.getSelectedIndex();
		int totalCount = emailQueueDao.findCountByDate(currentUser.getUserId(),
				fromDate, endDate, selectedIndex != 0 ? "'"+emailFilterLbId
						.getSelectedItem().getValue().toString()+"'" : type);
		autoEmailRepListBottomPagingId.setTotalSize(totalCount);
		autoEmailRepListBottomPagingId.setActivePage(0);
		autoEmailRepListBottomPagingId.setPageSize(Integer.valueOf(pageSizeLbId
				.getSelectedItem().getValue().toString()));
		List<Object[]> rowsToDraw = emailQueueDao.fetchRecordsByDate(
				currentUser.getUserId(), fromDate, endDate,
				selectedIndex != 0 ? "'"+emailFilterLbId.getSelectedItem()
						.getValue().toString()+"'" : type, 0,
				autoEmailRepListBottomPagingId.getPageSize(),orderby_colName_keywords,desc_Asc);
		drawRows(rowsToDraw);
	}

	private void drawRows(List<Object[]> rowsToDraw) {

		Components.removeAllChildren(reportGridrowsId);
		for (Object[] obj : rowsToDraw) {
			// SELECT ct.template_name, eq.type, max(eq.sent_date), count(1),
			// SUM(IF(eq.delivery_status ='Success', 1,0)) as delivered,
			// SUM(IF(eq.opens >0, 1,0)) as uniopens,SUM(IF(eq.clicks >0, 1,0))
			// as uniclicks" +
			Row row = new Row();

			Label tempLabel = new Label(!obj[7].toString().equalsIgnoreCase("0") ? obj[8] != null
					&& !obj[8].toString().trim().isEmpty() ? obj[8].toString()
					.trim() : obj[0] != null && !obj[0].toString().trim().isEmpty() ? obj[0].toString() : " Not Available"  : "Default Message" );
			tempLabel
					.setStyle("cursor:pointer;color:blue;text-decoration: underline;");
			tempLabel.addEventListener("onClick", this);

			row.appendChild(tempLabel);
			row.appendChild(new Label(AUTOEMAIL_MAP.get(obj[1].toString())));
			// row.appendChild(new Label("needtochange"));
			row.appendChild(new Label(MyCalendar.calendarToString(
					(Calendar) obj[2], MyCalendar.FORMAT_SCHEDULE_TIME,
					clientTimeZone)));
			row.appendChild(new Label(obj[3].toString()));
			row.appendChild(new Label(obj[4].toString()));
			row.appendChild(new Label(obj[5].toString()));
			row.appendChild(new Label(obj[6].toString()));

			row.setParent(reportGridrowsId);
			row.setValue((obj[1].toString() + Constants.ADDR_COL_DELIMETER)
					+ (obj[7] != null ? obj[7].toString() : ""));
		}

	}

	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		if (event.getTarget() instanceof Label) {
			Label tempLable = (Label) event.getTarget();
			Row tempRow = (Row) tempLable.getParent();
			String selectedTemplate = (String) tempRow.getValue();
			Long userId = GetUser.getUserObj().getUserId();
			if(userActivitiesDaoForDML!=null) {
			UserActivities userActivity = new UserActivities("Visited detailed auto email report page", "Visited pages", Calendar.getInstance(),userId );
			userActivitiesDaoForDML.saveOrUpdate(userActivity);
			}
			sessionScope.setAttribute("selectedAutoEmail", selectedTemplate);
			Redirect.goTo(PageListEnum.REPORT_AUTO_EMAIL_DETAILED_REPORT);

		} else if (event.getTarget() instanceof Paging) {
			Paging paging = (Paging) event.getTarget();
			int desiredPage = paging.getActivePage();

			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			autoEmailRepListBottomPagingId.setActivePage(desiredPage);

			List<Object[]> rowsToDraw = emailQueueDao.fetchRecordsByDate(
					currentUser.getUserId(), fromDate, endDate,
					selectedIndex != 0 ? "'"+emailFilterLbId.getSelectedItem()
							.getValue().toString()+"'" : type, ofs,
					(byte) pagingEvent.getPageable().getPageSize(),orderby_colName_keywords,desc_Asc);
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
	
	
	
	
	
	
	//Sorting Hard coded
	
	public String orderby_colName_keywords="sentCount",desc_Asc="desc"; 
	public List<Object[]> rowsToDraw1;
	 
	 public void desc2ascasc2desc()
	 {
	 	if(desc_Asc=="desc")
				desc_Asc="asc";
			else
				desc_Asc="desc";
		
	 }
		 
		//Promotional Keywords
		public void onClick$sortbyAutoEmailCategory() {
			orderby_colName_keywords = "eq.type";
			desc2ascasc2desc();	
			rowsToDraw1 = emailQueueDao.fetchRecordsByDate(
					currentUser.getUserId(), fromDate, endDate,
					selectedIndex != 0 ? "'"+emailFilterLbId.getSelectedItem()
							.getValue().toString()+"'" : type, 0,
					autoEmailRepListBottomPagingId.getPageSize(),orderby_colName_keywords,desc_Asc);
			drawRows(rowsToDraw1);
		 }
		 
		 public void onClick$sortbyLastSentDate() {
			 orderby_colName_keywords = "latestSentDate";
				desc2ascasc2desc();	
				rowsToDraw1 = emailQueueDao.fetchRecordsByDate(
						currentUser.getUserId(), fromDate, endDate,
						selectedIndex != 0 ? "'"+emailFilterLbId.getSelectedItem()
								.getValue().toString()+"'" : type, 0,
						autoEmailRepListBottomPagingId.getPageSize(),orderby_colName_keywords,desc_Asc);
				drawRows(rowsToDraw1);
			 }
		 public void onClick$sortbyTotalSent() {
				orderby_colName_keywords = "sentCount";
				desc2ascasc2desc();	
				rowsToDraw1 = emailQueueDao.fetchRecordsByDate(
						currentUser.getUserId(), fromDate, endDate,
						selectedIndex != 0 ? "'"+emailFilterLbId.getSelectedItem()
								.getValue().toString()+"'" : type, 0,
						autoEmailRepListBottomPagingId.getPageSize(),orderby_colName_keywords,desc_Asc);
				drawRows(rowsToDraw1);
			 }
			 
		 public void onClick$sortbyDelivered() {
				 orderby_colName_keywords = "delivered";
					desc2ascasc2desc();	
					rowsToDraw1 = emailQueueDao.fetchRecordsByDate(
							currentUser.getUserId(), fromDate, endDate,
							selectedIndex != 0 ? "'"+emailFilterLbId.getSelectedItem()
									.getValue().toString()+"'" : type, 0,
							autoEmailRepListBottomPagingId.getPageSize(),orderby_colName_keywords,desc_Asc);
					drawRows(rowsToDraw1);
				 }
		 
		 public void onClick$sortbyUniqueOpens() {
			 orderby_colName_keywords = "uniopens";
				desc2ascasc2desc();	
				rowsToDraw1 = emailQueueDao.fetchRecordsByDate(
						currentUser.getUserId(), fromDate, endDate,
						selectedIndex != 0 ? "'"+emailFilterLbId.getSelectedItem()
								.getValue().toString()+"'" : type, 0,
						autoEmailRepListBottomPagingId.getPageSize(),orderby_colName_keywords,desc_Asc);
				drawRows(rowsToDraw1);
					
			 }
		 public void onClick$sortbyUniqueClicks() {
				orderby_colName_keywords = "uniclicks";
				desc2ascasc2desc();	
				rowsToDraw1 = emailQueueDao.fetchRecordsByDate(
						currentUser.getUserId(), fromDate, endDate,
						selectedIndex != 0 ? "'"+emailFilterLbId.getSelectedItem()
								.getValue().toString()+"'" : type, 0,
						autoEmailRepListBottomPagingId.getPageSize(),orderby_colName_keywords,desc_Asc);
				drawRows(rowsToDraw1);
			 }
			 
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public void onClick$getBitweenDatesBtnId() {
		Calendar fromCal = getStartDate();
		Calendar endCal = getEndDate();
		if (endCal.before(fromCal)) {
			MessageUtil.setMessage("'To' date must be later than 'From' date.",
					"color:red");
			return;
		}

		defaultSettings();
	}

	public void onClick$resetAnchId() {
		emailFilterLbId.setSelectedIndex(0);
		setDateValues();
		defaultSettings();
	}

	public void onSelect$pageSizeLbId() {
		autoEmailRepListBottomPagingId.setPageSize(Integer.valueOf(pageSizeLbId
				.getSelectedItem().getValue().toString()));
		autoEmailRepListBottomPagingId.setActivePage(0);
		List<Object[]> rowsToDraw = emailQueueDao.fetchRecordsByDate(
				currentUser.getUserId(), fromDate, endDate,
				selectedIndex != 0 ? "'"+emailFilterLbId.getSelectedItem()
						.getValue().toString()+"'" : type, 0,
				autoEmailRepListBottomPagingId.getPageSize(),orderby_colName_keywords,desc_Asc);
		drawRows(rowsToDraw);
	}

	public void onClick$exportBtnId() {
		JdbcResultsetHandler jdbcResultsetHandler = null;
		StringBuffer sb = null;
		BufferedWriter bw = null;
		ResultSet resultSet = null;
		try {
			if (reportGridrowsId.getChildren().size() == 0) {
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

			String filePath = usersParentDirectory + "/" + userName
					+ "/List/download/AutoEmail_Report_"
					+ System.currentTimeMillis() + "." + ext;

			sb = new StringBuffer();
			File file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(file));
			sb.append("\"Message Name\",\"Auto-Email Category\",\"Last Sent Date\",\"Total Sent\",\"Delivered\",\"Unique Opens\",\"Unique Clicks\"\n");
			bw.write(sb.toString());

			String query = "SELECT eq.cust_temp_name as templateName, eq.type, max(eq.sent_date) as latestSentDate, count(1) sentCount, SUM(IF(eq.delivery_status ='Success', 1,0)) as delivered, SUM(IF(eq.opens >0, 1,0)) as uniopens,SUM(IF(eq.clicks >0, 1,0)) as uniclicks, eq.cust_temp_id "
					+ " FROM  email_queue eq  WHERE eq.user_id = "
					+ currentUser.getUserId()
					+ " AND eq.status = 'Sent' AND eq.sent_date BETWEEN '"
					+ fromDate + "' AND '" + endDate + "' ";
			
				query += " AND eq.type in (" + (selectedIndex != 0 ? "'"+emailFilterLbId.getSelectedItem().getValue().toString()+"'" : type) + ") ";

			
			query += " GROUP BY eq.type, eq.cust_temp_id order by sentCount ";
			jdbcResultsetHandler = new JdbcResultsetHandler();
			jdbcResultsetHandler.executeStmt(query);
			resultSet = jdbcResultsetHandler.getResultSet();
			while (resultSet.next()) {
				sb.setLength(0);
				sb.append("\""+(resultSet.getString("templateName") != null ? resultSet
						.getString("templateName") : "Default Message") + "\",");
				sb.append("\""+(AUTOEMAIL_MAP.get(resultSet.getString("type"))) + "\",");
				Calendar cal = null;
				Timestamp t = resultSet.getTimestamp("latestSentDate");
				if (t != null) {
					if (new Date(t.getTime()) != null) {
						cal = Calendar.getInstance();
						cal.setTime(t);
					}
				}
				sb.append("\""+MyCalendar.calendarToString(cal,MyCalendar.FORMAT_SCHEDULE_TIME, clientTimeZone)+ "\",");
				sb.append("\""+resultSet.getLong("sentCount") + "\",");
				sb.append("\""+resultSet.getLong("delivered") + "\",");
				sb.append("\""+resultSet.getLong("uniopens") + "\",");
				sb.append("\""+resultSet.getLong("uniclicks") + "\"");
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
