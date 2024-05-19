package org.mq.marketer.campaign.controller.campaign;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampReportLists;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.BounceDao;
import org.mq.marketer.campaign.dao.CampReportListsDao;
import org.mq.marketer.campaign.dao.CampaignReportDao;
import org.mq.marketer.campaign.dao.ClicksDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.BounceCategories;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.OCCSVWriter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class CampReportController extends GenericForwardComposer {

	private Session session;
	private String fromPage;
	Desktop desktopScope = null;
	private Long userId;
	private Grid reportGridId;
	private Div metricsid;
	private Listbox campConsolLbId, exportCouponReportLbId, statusLbId, exportStoreRedmLbId;
	private Label campNamelbId, msgLblId;
	private Window custExport;
	private TimeZone clientTimeZone;
	private MyDatebox fromDateboxId;
	private MyDatebox toDateboxId;
	private Calendar fromDateCal, toDateCal;
	private int filter;
	private Listbox userOrgLbId, userNameLbId;
	private Div custExport$chkDivId;
	private String searchTextStr = Constants.STRING_NILL;
	private ClicksDao clicksDao;
	public List categoriesList = null;
	private CampaignReportDao campaignReportDao;
	private boolean isAdmin;
	private Combobox exportCbId;
	String userName = null;
	private Listbox sourceTypeLbId,pageSizeLbId;
	private Listbox emailFilterLbId;
	Listitem selectedItem;
	private BounceDao bounceDao;
	private Listbox campaignListLbId;
	private Textbox searchTbId;
	private boolean isVisible;
	private Listbox consolidatedRepLbId;
	private Toolbarbutton consolidateMetricsToolbarBtnId;
	public String orderby_colName = "sentDate", desc_Asc = "Asc"; // desc_Asc = "desc"
	private Paging campRepListBottomPagingId;
	private static final String allStr = "--All--";

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public CampReportController() {
		session = Sessions.getCurrent();
		campaignReportDao = (CampaignReportDao) SpringUtil.getBean("campaignReportDao");
		desktopScope = Executions.getCurrent().getDesktop();
		clientTimeZone = (TimeZone) session.getAttribute("clientTimeZone");
		userId = GetUser.getUserObj().getUserId();
		bounceDao = (BounceDao) SpringUtil.getBean("bounceDao");
		clicksDao = (ClicksDao) SpringUtil.getBean("clicksDao");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		String style = "font-weight:bold;font-size:15px;color:#313031;" +
				"font-family:Arial,Helvetica,sans-serif;align:left";
			 PageUtil.setHeader("Email Campaign Reports","",style,true);
		

		Campaigns camp = (Campaigns) session.getAttribute("campaign");
		if (camp != null) {

			String campName = camp.getCampaignName();
			campNamelbId.setValue(campName);
			Long eachUserId = camp.getUsers().getUserId();

			// ********************************
			setDateValues();
			fromDateCal = getFromDateCal();
			toDateCal = getToDateCal();
			int totalSize =campaignReportDao.getTotalCount(userId, campName, fromDateCal.toString(), toDateCal.toString());
			int  pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			campRepListBottomPagingId.setTotalSize(totalSize);
			campRepListBottomPagingId.setActivePage(0);
			campRepListBottomPagingId.setPageSize(pageSize);
			campRepListBottomPagingId.addEventListener("onPaging",this);
			if (fromPage.equals("admin/dashBoard"))
				getReports(campName, eachUserId, fromDateCal.toString(), toDateCal.toString());
			else {
				getReportByCampaignName(campName, fromDateCal.toString(), toDateCal.toString(),0,campRepListBottomPagingId.getPageSize());
			}
			// ***********************************,

			if (!fromPage.equals("admin/dashBoard")) {

				if (reportGridId.getPageSize() > 0) {
					metricsid.setVisible(true);
					campConsolLbId.setVisible(true);
					getCampaignConsolidatedReport(campName);
				} else {

					metricsid.setVisible(false);
					campConsolLbId.setVisible(false);
				}

			} // if

			logger.debug("=====done with campaign module=====");

		} // if

	}
public void onSelect$pageSizeLbId() {
		
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		campRepListBottomPagingId.setPageSize(pageSize);
		campRepListBottomPagingId.setPageSize(pageSize);
		campRepListBottomPagingId.addEventListener("onPaging",this);
		Campaigns camp = (Campaigns) session.getAttribute("campaign");
		if (camp != null) {
			String campName = camp.getCampaignName();
		getReportByCampaignName(campName, fromDateCal.toString(), toDateCal.toString(),0,campRepListBottomPagingId.getPageSize());
		}
	}
public void onPaging$campRepListBottomPagingId(PagingEvent event) {
	logger.info(" inside Pagging ======================");
	int desiredPage = campRepListBottomPagingId.getActivePage();
	campRepListBottomPagingId.setActivePage(desiredPage);
	campRepListBottomPagingId.addEventListener("onPaging",this);
	int pSize = event.getPageable().getPageSize();
	int ofs = desiredPage * pSize;
	logger.info("desired page :"+desiredPage);
	Campaigns camp = (Campaigns) session.getAttribute("campaign");
	if (camp != null) {
		String campName = camp.getCampaignName();
	getReportByCampaignName(campName, fromDateCal.toString(), toDateCal.toString(),ofs,campRepListBottomPagingId.getPageSize());
	}
}

	public void setDateValues() {

		Calendar cal = MyCalendar.getNewCalendar();
		toDateboxId.setValue(cal);
		logger.debug("ToDate (server) :" + cal);
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		logger.debug("FromDate (server) :" + cal);
		fromDateboxId.setValue(cal);

		fromDateCal = getFromDateCal();
		toDateCal = getToDateCal();
	}

	public Calendar getFromDateCal() {

		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		Calendar tempClientFromCal = fromDateboxId.getClientValue();

		serverFromDateCal.set(Calendar.HOUR_OF_DAY,
				serverFromDateCal.get(Calendar.HOUR_OF_DAY) - tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(Calendar.MINUTE,
				serverFromDateCal.get(Calendar.MINUTE) - tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);

		return serverFromDateCal;
	}

	public Calendar getToDateCal() {

		Calendar serverToDateCal = toDateboxId.getServerValue();
		Calendar tempClientToCal = toDateboxId.getClientValue();
		serverToDateCal.set(Calendar.HOUR_OF_DAY,
				23 + serverToDateCal.get(Calendar.HOUR_OF_DAY) - tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE,
				59 + serverToDateCal.get(Calendar.MINUTE) - tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);

		return serverToDateCal;

	}

	public void onClick$getBitweenDatesBtnId() {
		fromDateCal = getFromDateCal();
		toDateCal = getToDateCal();
		Campaigns camp = (Campaigns) session.getAttribute("campaign");
		if (camp != null) {
			String campName = camp.getCampaignName();
			campNamelbId.setValue(campName);
			Long eachUserId = camp.getUsers().getUserId();
			int totalSize =campaignReportDao.getTotalCount(userId, campName, fromDateCal.toString(), toDateCal.toString());
			int  pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			campRepListBottomPagingId.setTotalSize(totalSize);
			campRepListBottomPagingId.setActivePage(0);
			campRepListBottomPagingId.setPageSize(pageSize);
			if (fromPage.equals("admin/dashBoard"))
				getReports(campName, eachUserId, fromDateCal.toString(), toDateCal.toString());
			else {
				getReportByCampaignName(campName, fromDateCal.toString(), toDateCal.toString(),0,campRepListBottomPagingId.getPageSize());
			}
		}
	}

	public void onClick$campReportBackBtnId() {

		if (fromPage.equals("admin/dashBoard")) {
			Redirect.goToPreviousPage();
		} else {
			// Redirect.goTo(PageListEnum.CAMPAIGN_VIEW);
			Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
		}
	}

	/*
	 * this method is called from campreport.zul to get the campreports specific
	 * to user
	 */

	public void getReports(String campaignName, Long eachUserId, String fromDate, String todate) {
		MessageUtil.clearMessage();

		reportGridId.setRowRenderer(new ReportGridRenderer());
		List<CampaignReport> campReportList = null;
		try {
			campReportList = campaignReportDao.getReportByCampaignName(eachUserId, campaignName, fromDate, todate,0,1000);
		} catch (Exception e) {
			logger.error("Exception :", e);
		}
		if (campReportList == null || campReportList.size() == 0) {
			// MessageUtil.setMessage("No reports exist between the selected
			// dates.", "color:red;", "top");
			// msgLblId.setValue(" No Reports existed between the selected dates
			// ");
			// return;
		}
		msgLblId.setValue("");
		createRows(campReportList, campaignName);
	}

	public void getReportByCampaignName(String campaignName, String fromDate, String toDate,int startIndx,int count) {
		MessageUtil.clearMessage();
		 logger.info("=========start :"+startIndx);
      logger.info("=========count :"+count);
		reportGridId.setRowRenderer(new ReportGridRenderer());
		List<CampaignReport> campReportList = null;
		try {
			campReportList = campaignReportDao.getReportByCampaignName(userId, campaignName, fromDate, toDate,startIndx,count);
		} catch (Exception e) {
			logger.error("Exception :", e);
		}
		if (campReportList == null || campReportList.size() == 0) {
			// MessageUtil.setMessage("No reports exist.", "color:red;", "top");
			// msgLblId.setValue(" No Reports existed between the selected dates
			// ");
			// return;
		}
		msgLblId.setValue("");
		createRows(campReportList, campaignName);
	}

	public void getCampaignConsolidatedReport(String campaignName) {
		campConsolLbId.setVisible(true);
		MessageUtil.clearMessage();
		try {
			List<Object[]> campReport = campaignReportDao.getCampaignConsolidatedReport(campaignName, userId);
			if (campReport.size() == 0)
				return;
			Object[] obj = campReport.get(0);
			Listitem lItem = null;
			Listcell lCell = null;
			Long sent = (Long) obj[0];
			lItem = new Listitem();
			lCell = new Listcell("Over all");
			lCell.setParent(lItem);
			lCell = new Listcell("" + sent);
			lCell.setParent(lItem);
			/*
			 * lCell = new
			 * Listcell(Utility.getPercentage(((Long)obj[1]),sent,2));
			 * lCell.setParent(lItem); lCell = new
			 * Listcell(Utility.getPercentage(((Long)obj[2]),sent,2));
			 * lCell.setParent(lItem); lCell = new
			 * Listcell(Utility.getPercentage(((Long)obj[3]),sent,2));
			 * lCell.setParent(lItem); lCell = new
			 * Listcell(Utility.getPercentage(((Long)obj[4]),sent,2));
			 * lCell.setParent(lItem);
			 */

			lCell = new Listcell(Utility.getPercentage(((Long) obj[1]), sent - ((Long) obj[5]).longValue(), 2));
			lCell.setParent(lItem);
			lCell = new Listcell(Utility.getPercentage(((Long) obj[2]), ((Long) obj[1]).longValue(), 2));
			lCell.setParent(lItem);
			lCell = new Listcell(Utility.getPercentage(((Long) obj[3]), ((Long) obj[1]).longValue(), 2));
			lCell.setParent(lItem);
			lCell = new Listcell(Utility.getPercentage(((Long) obj[4]), sent - ((Long) obj[5]).longValue(), 2));
			lCell.setParent(lItem);
			// lbl = new Label(Utility.getPercentage(campaignReport.getSpams(),
			// sent - campaignReport.getBounces(),2));
			// lCell = new
			// Listcell(Utility.getPercentage(((Long)obj[1]),sent,2));

			// need to calculate the bouncecategories here itself.
			lItem = setConsolidatedBounceCategories(lItem, campaignName, sent);// over
																				// all

			lItem.setParent(campConsolLbId);
		} catch (Exception e) {
			logger.error(" ** Exception : " + e + " **", e);
		}
	}// getConsolidatedReport()

	public Listitem setConsolidatedBounceCategories(Listitem item, String campName, long sent) {

		Listcell hardBounced = new Listcell("0");
		Listcell softBounced = new Listcell("0");
		Listcell droppedBounces = new Listcell("0");
		Listcell blockedBounces = new Listcell("0");
		Listcell otherBounces = new Listcell("0");

		long hardBounceCount = 0;
		long softBounceCount = 0;

		List<Object[]> bounceCategoryList = null;

		bounceCategoryList = bounceDao.getCampConsolidatedBounceCat(userId, campName);

		// Long objLong = null;
		for (Object object : bounceCategoryList) {
			Object[] obj = (Object[]) object;
			// logger.debug("Object[] : " + obj[0] + " | " + obj[1] + " | " +
			// obj[2]);

			if (((String) obj[0]).startsWith(BounceCategories.SOFT_BOUNCED)) {
				// mFullLbl.setValue(obj[1] + "");
				softBounceCount += ((Long) obj[1]).longValue();
				softBounced.setLabel(Utility.getPercentage(softBounceCount, sent, 2) + "");

			} else if (((String) obj[0]).startsWith(BounceCategories.BLOCKED)) {

				blockedBounces.setLabel(Utility.getPercentage(((Long) obj[1]), sent, 2) + "");

			} else if (((String) obj[0]).startsWith(BounceCategories.DROPPED)) {

				droppedBounces.setLabel(Utility.getPercentage(((Long) obj[1]), sent, 2) + "");
			} else if (((String) obj[0]).startsWith(BounceCategories.BOUNCED)) {
				// nonExistLbl.setValue(obj[1] + "");
				hardBounceCount += ((Long) obj[1]).longValue();
				hardBounced.setLabel(Utility.getPercentage(hardBounceCount, sent, 2) + "");

			}

			else if (((String) obj[0]).startsWith(BounceCategories.OTHERS)) {

				otherBounces.setLabel(Utility.getPercentage(((Long) obj[1]), sent, 2) + "");
			}

		} // for

		droppedBounces.setParent(item);
		hardBounced.setParent(item);
		softBounced.setParent(item);
		blockedBounces.setParent(item);
		otherBounces.setParent(item);

		return item;

	}// setConsolidatedBounceCategories

	private void createRows(List<CampaignReport> campReportList, String campaignName) {
		// MessageUtil.clearMessage();
		BounceDao bounceDao = (BounceDao) SpringUtil.getBean("bounceDao");
		categoriesList = bounceDao.getCategoryPercentageByUser(userId.toString(), campaignName);
		logger.info("display List size :" + categoriesList.size());
		// logger.debug("Got Categories % Info : " + categoriesList);
		// reportGridId.setVisible( (campReportList.size() > 0) );
		reportGridId.setModel(new SimpleListModel(campReportList));
		reportGridId.setActivePage(0);

	}// createRows

	/**
	 * ReportGridRenderer is a row renderer class to populate the reports grid.
	 * 
	 * @author venkateshgoudampalli
	 *
	 */
	public class ReportGridRenderer implements RowRenderer, EventListener {
		long sent = 0;
		Toolbarbutton tb = null;
		Calendar tempCal;
		CampaignReport campaignReport;
		Label lbl;
		String sourceType = null;

		@Override
		public void render(Row row, Object crObj, int arg2) throws Exception {
			campaignReport = (CampaignReport) crObj;

			sent = campaignReport.getSent();
			row.setValue(campaignReport);
			tempCal = campaignReport.getSentDate();

			tb = new Toolbarbutton(
					MyCalendar.calendarToString(tempCal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
			tb.setStyle("color:blue;");
			tb.setAttribute("campaignReport", campaignReport);

			tb.setParent(row);

			// adding event listener to identify onClick and what to do when
			// onClick happens
			tb.addEventListener("onClick", this);

			tb.setParent(row);
			lbl = new Label(campaignReport.getCampaignName());
			lbl.setParent(row);

			lbl = new Label(getCampRepLists(campaignReport.getCrId()));
			lbl.setParent(row);

			lbl = new Label("" + sent);
			lbl.setParent(row);
			lbl = new Label(Utility.getPercentage(campaignReport.getOpens(), sent - campaignReport.getBounces(), 2));
			lbl.setParent(row);
			lbl = new Label(Utility.getPercentage(campaignReport.getClicks(), campaignReport.getOpens(), 2));
			lbl.setParent(row);
			lbl = new Label(Utility.getPercentage(campaignReport.getUnsubscribes(), campaignReport.getOpens(), 2));
			lbl.setParent(row);

			lbl = new Label(Utility.getPercentage(campaignReport.getSpams(), sent - campaignReport.getBounces(), 2));
			lbl.setParent(row);
			setBounceCategories(campaignReport.getCrId(), row);

			/*
			 * lbl = new
			 * Label(Utility.getPercentage(campaignReport.getBounces(),sent,2));
			 * lbl.setParent(row);
			 */
			// setBounceCategories(campaignReport.getCrId(),row);

			sourceType = campaignReport.getSourceType();
			if (sourceType != null) {
				if (sourceType.contains("_")) {
					lbl = new Label(sourceType.substring(0, sourceType.indexOf("_")));
				} else {
					lbl = new Label(sourceType);
				}
			} else {
				lbl = new Label("One Off");
			}
			lbl.setParent(row);
		}

	

		private void setBounceCategories(Long crId, Row row) {

			Label hardBouncedLbl = new Label("0");
			Label softBouncedLbl = new Label("0");
			Label droppedLbl = new Label("0");
			// Label mFullLbl = new Label("0");
			Label blockedLbl = new Label("0");
			/*
			 * Label nonExistLbl = new Label("0"); Label undeliverLbl = new
			 * Label("0");
			 */
			Label othersLbl = new Label("0");
			Long objLong = null;
			long hardBounceCount = 0;
			long softBounceCount = 0;
			long dropped = 0;
			for (Object object : categoriesList) {
				objLong = null;
				Object[] obj = (Object[]) object;
				// logger.debug("Object[] : " + obj[0] + " | " + obj[1] + " | "
				// + obj[2]);
				try {
					objLong = (Long) obj[2];
				} catch (Exception e) {
					logger.error(e);
				}

				if (objLong == null) {
					continue;
				}
				// logger.debug("Found : " + objLong + " | " + crId + " | " +
				// (objLong.equals(crId)) + " | " + obj[0] + " | " + obj[1] + "
				// | " + obj[2]);
				if (!objLong.equals(crId)) {
					continue;
				}

				if (((String) obj[0]).startsWith(BounceCategories.SOFT_BOUNCED)) {
					// mFullLbl.setValue(obj[1] + "");
					softBounceCount += ((Long) obj[1]).longValue();
					softBouncedLbl.setValue(Utility.getPercentage(softBounceCount, sent, 2) + "");

				}

				else if (((String) obj[0]).startsWith(BounceCategories.DROPPED)) {
					// mFullLbl.setValue(obj[1] + "");
					dropped += ((Long) obj[1]).longValue();
					droppedLbl.setValue(Utility.getPercentage(dropped, sent, 2) + "");
				} else if (((String) obj[0]).startsWith(BounceCategories.BLOCKED)) {

					blockedLbl.setValue(Utility.getPercentage(((Long) obj[1]), sent, 2) + "");
				} else if (((String) obj[0]).startsWith(BounceCategories.BOUNCED)) {
					// nonExistLbl.setValue(obj[1] + "");
					hardBounceCount += ((Long) obj[1]).longValue();
					hardBouncedLbl.setValue(Utility.getPercentage(hardBounceCount, sent, 2) + "");

				}

				else if (((String) obj[0]).startsWith(BounceCategories.OTHERS)) {

					othersLbl.setValue(Utility.getPercentage(((Long) obj[1]), sent, 2) + "");
				}

			} // for

			droppedLbl.setParent(row);
			hardBouncedLbl.setParent(row);
			softBouncedLbl.setParent(row);
			// mFullLbl.setParent(row);
			blockedLbl.setParent(row);
			// nonExistLbl.setParent(row);
			// undeliverLbl.setParent(row);
			othersLbl.setParent(row);
		} // setBounceCategories

		@Override
		public void onEvent(Event event) throws Exception {
			Toolbarbutton tb = (Toolbarbutton) event.getTarget();
			
			Session sessionScope = Sessions.getCurrent();
			sessionScope.removeAttribute("campaignReport");
			CampaignReport cr = (CampaignReport) tb.getAttribute("campaignReport");

			if (cr.getSent() == 0) {

				MessageUtil.setMessage("Detailed report cannot be viewed as sent count is :0. ", "color:green;");
				return;

			}
			if (cr.getSent() != 0 || cr.getOpens() != 0 || cr.getClicks() != 0 || cr.getBounces() != 0
					|| cr.getUnsubscribes() != 0) {

				sessionScope.setAttribute("campaignReport", cr);
				Redirect.goTo(PageListEnum.REPORT_DETAILED_REPORT);
			
		    }
				}


	}
	public String getCampRepLists(Long campRepId) {

		String listsName = "";

		CampReportListsDao campReportListsDao = (CampReportListsDao) SpringUtil.getBean("campReportListsDao");

		CampReportLists tempCampRepLists = campReportListsDao.findByCampReportId(campRepId);
		if (tempCampRepLists != null) {

			listsName = tempCampRepLists.getListsName();

		}
		return listsName;

	}

	// export logic

	public void onClick$exportBtnId() {
		try {
			if (isAdmin) {
				if (userNameLbId.getSelectedIndex() != 0) {

					anchorEvent(false);

					custExport.setVisible(true);
					custExport.doHighlighted();
				} else {

					MessageUtil.setMessage("Please select a user", "red");
				}
			} else {
				anchorEvent(false);

				custExport.setVisible(true);
				custExport.doHighlighted();
			}

		} catch (Exception e) {
			logger.error("Exception in onClick$exportBtnId  method ***", e);
		}
	}


	public void anchorEvent(boolean flag) {
		List<Component> chkList = custExport$chkDivId.getChildren();
		Checkbox tempChk = null;
		for (int i = 0; i < chkList.size(); i++) {
			if (!(chkList.get(i) instanceof Checkbox))
				continue;

			tempChk = (Checkbox) chkList.get(i);
			tempChk.setChecked(flag);

		} // for

	}

	

	private Textbox searchByPromoCodeNameTbId;
	private Users users;
	private String fromDate, endDate;

	

	
	public void onClick$resetAnchId() {

		//sourceTypeLbId.setSelectedIndex(0);

		campaignListLbId.setVisible(false);
		setDateValues();
		if (userOrgLbId.isVisible() && userNameLbId.isVisible()) {
			userOrgLbId.setSelectedIndex(0);
			consolidateMetricsToolbarBtnId.setDisabled(true);
		}

		consolidatedRepLbId.setVisible(false);
		consolidateMetricsToolbarBtnId.setLabel("View Consolidated Metrics");
		orderby_colName = "sentDate";
		desc_Asc = "desc";
		fromDateCal = getFromDateCal();
		toDateCal = getToDateCal();
		
		Campaigns camp = (Campaigns) session.getAttribute("campaign");
		if (camp != null) {
			String campName = camp.getCampaignName();
			campNamelbId.setValue(campName);
			Long eachUserId = camp.getUsers().getUserId();
			int totalSize =campaignReportDao.getTotalCount(userId, campName, fromDateCal.toString(), toDateCal.toString());
			int  pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			campRepListBottomPagingId.setTotalSize(totalSize);
			campRepListBottomPagingId.setActivePage(0);
			campRepListBottomPagingId.setPageSize(pageSize);
			if (fromPage.equals("admin/dashBoard"))
				getReports(campName, eachUserId, fromDateCal.toString(), toDateCal.toString());
			else
				getReportByCampaignName(campName, fromDateCal.toString(), toDateCal.toString(),0,campRepListBottomPagingId.getPageSize());
		}
		// getCampaignReports(0,
		// campRepListTopPagingId.getPageSize(),orderby_colName,desc_Asc);
	}

	private void createRows(List<CampaignReport> campReportList) {
		// MessageUtil.clearMessage();

		// categoriesList = bounceDao.getCategoryPercentageByUser(userId);
		StringBuffer crIds = new StringBuffer();// Constants.STRING_NILL;
		if (campReportList != null && campReportList.size() > 0) {
			for (CampaignReport campaignReport : campReportList) {

				if (crIds.length() > 0)
					crIds.append(Constants.DELIMETER_COMMA);

				crIds.append(campaignReport.getCrId());
			}

			categoriesList = bounceDao.getBounceCategories(crIds.toString());
			// categoriesList =
			// bounceDao.getCategoryPercentageByUser(userId,null);
		}

		// logger.debug("Got Categories % Info : " + categoriesList);
		// reportGridId.setVisible( (campReportList.size() > 0) );

		reportGridId.setModel(new SimpleListModel(campReportList));

		reportGridId.setRowRenderer(new ReportGridRenderer());

		// reportGridId.setActivePage(0);

	}// createRows

	

		

	public void onClick$exportBtnStoreRedmId() {
		createWindow();
		custExport.setVisible(true);
		custExport.doHighlighted();

	}

	public void createWindow() {

		try {
			Components.removeAllChildren(custExport$chkDivId);

			Checkbox tempChk2 = new Checkbox("Email Name");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Date Sent");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Subject ");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("List(s)");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Sent");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Opens %");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Clicks %");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Unsubs %");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Marked as Spam %");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Dropped %");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Hard Bounces %");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Soft Bounce %");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Blocked Bounce %");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Other Bounce %");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Unique Opens ");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Unique Clicks");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Unsubs ");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Marked as Spam");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Dropped");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Hard Bounces");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Soft Bounce");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Blocked Bounce");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("Other Bounce");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

			tempChk2 = new Checkbox("URLs");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			tempChk2.setStyle("width:130px");

		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}

	public void onClick$selectAllAnchr$custExport() {
		anchorEvent(true);
	}

	public void onClick$clearAllAnchr$custExport() {
		anchorEvent(false);
	}

	public void onClick$selectFieldBtnId$custExport() {

		custExport.setVisible(false);
		List<Component> chkList = custExport$chkDivId.getChildren();

		int indexes[] = new int[chkList.size()];

		boolean checked = false;

		for (int i = 0; i < chkList.size(); i++) {
			indexes[i] = -1;
		} // for

		Checkbox tempChk = null;

		for (int i = 0; i < chkList.size(); i++) {
			if (!(chkList.get(i) instanceof Checkbox))
				continue;

			tempChk = (Checkbox) chkList.get(i);

			if (tempChk.isChecked()) {
				indexes[i] = 0;
				checked = true;
			} else {
				indexes[i] = -1;
			}

		} // for

		if (((Checkbox) custExport$chkDivId.getLastChild()).isChecked()) {

			checked = true;
		}
		boolean urls = false;
		if (((Checkbox) custExport$chkDivId.getLastChild()).isChecked()) {
			urls = true;
			logger.info("urls :"+urls);
		}
		if (checked) {

			int confirm = Messagebox.show("Do you want to export with selected fields ?", "Confirm",
					Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if (confirm == 1) {
				try {
					String fileFormate = exportStoreRedmLbId.getSelectedItem().getValue().toString();
					/*if (fileFormate.contains("xls")) {
						 exportExcel(fileFormate, indexes); 
					} else */
					if (fileFormate.contains("csv")) {
						exportCSV(fileFormate, indexes, urls);
					}

				} catch (Exception e) {
					logger.error("Exception caught :: ", e);
					logger.info(e);
					System.out.println(e);
				}
			} else {
				custExport.setVisible(true);
			}

		} else {

			MessageUtil.setMessage("Please select atleast one field", "red");
			custExport.setVisible(false);
		}

	}

	/*private void exportCSV(String type, int[] indexes, boolean urls) throws Exception {
		logger.debug("-- just entered into exportCSV --");
		StringBuffer sb = null;
		ResultSet exportresResultSet = null;
		String userName = GetUser.getUserName();
		String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");
		String exportDir = usersParentDirectory + "/" + userName + "/Export/";
		File downloadDir = new File(exportDir);
		if (downloadDir.exists()) {
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception ::", e);

				logger.debug(downloadDir.getName() + " is not deleted");
			}
		}
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}

		String filePath = exportDir + "Campian_Reports_" + System.currentTimeMillis() + "." + type;
		MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
		try {

			logger.debug("Download File path : " + filePath);
			File file = new File(filePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			// String status = statusLbId.getSelectedItem().getLabel();
			// String promotionName =
			// searchByPromoCodeNameTbId.getValue().trim();
			String emailName = null;
			boolean isLike = false;
			if (filter == 1) {
				emailName = searchTextStr;
				isLike = true;
			} else if (filter == 2) {
				emailName = selectedItem.getLabel();
				isLike = false;
			}
			Campaigns camp11 = (Campaigns) session.getAttribute("campaign");
			String campName=null;
			if (camp11!= null) {
				campName = camp11.getCampaignName();
			}
			logger.info("zul value"+sourceTypeLbId.getSelectedItem());
			logger.info(" Campaign Report Dao  :"+campaignReportDao);
			logger.info("User Id :"+userId);
			logger.info(" From date :"+fromDateCal);
			logger.info(" To Date :"+toDateCal);
			int count = campaignReportDao.getReportCount(userId.toString(), campName, isLike,
					"--All--", fromDateCal.toString(), toDateCal.toString());
			logger.info("count :"+count);
			if (count == 0) {
				Messagebox.show("No promotion report found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
				return;
			}
			String udfFldsLabel = "";

			if (indexes[0] == 0) {
				udfFldsLabel = "\"" + "Date Sent" + "\"" + ",";
			}
			if (indexes[1] == 0) {
				udfFldsLabel += "\"" + "Email Name" + "\"" + ",";
			}

			if (indexes[2] == 0) {
				udfFldsLabel += "\"" + "List(s)" + "\"" + ",";
			}
			if (indexes[3] == 0) {
				udfFldsLabel += "\"" + "Sent" + "\"" + ",";
			}
			if (indexes[4] == 0) {
				udfFldsLabel += "\"" + "Opens %" + "\"" + ",";
			}
			if (indexes[5] == 0) {

				udfFldsLabel += "\"" + "Clicks %" + "\"" + ",";
			}
			if (indexes[6] == 0) {

				udfFldsLabel += "\"" + "Unsubs %" + "\"" + ",";
			}
			if (indexes[7] == 0) {

				udfFldsLabel += "\"" + "Marked as Spam " + "\"" + ",";
			}
			if (indexes[8] == 0) {

				udfFldsLabel += "\"" + "Dropped %" + "\"" + ",";
			}
			if (indexes[9] == 0) {

				udfFldsLabel += "\"" + "Hard Bounces %" + "\"" + ",";
			}
			if (indexes[10] == 0) {

				udfFldsLabel += "\"" + "Soft Bounce %" + "\"" + ",";
			}
			if (indexes[11] == 0) {

				udfFldsLabel += "\"" + "Blocked Bounce" + "\"" + ",";
			}
			if (indexes[12] == 0) {

				udfFldsLabel += "\"" + "Other Bounce %" + "\"" + ",";
			}
			if (indexes[13] == 0) {

				udfFldsLabel += "\"" + "Unique Opens  " + "\"" + ",";
			}
			if (indexes[14] == 0) {

				udfFldsLabel += "\"" + "Unique Clicks " + "\"" + ",";
			}
			if (indexes[15] == 0) {

				udfFldsLabel += "\"" + "Unsubs  " + "\"" + ",";
			}
			if (indexes[16] == 0) {

				udfFldsLabel += "\"" + "Marked as Spam  " + "\"" + ",";
			}
			if (indexes[17] == 0) {

				udfFldsLabel += "\"" + "Dropped% " + "\"" + ",";
			}
			if (indexes[18] == 0) {

				udfFldsLabel += "\"" + "Hard Bounces " + "\"" + ",";
			}
			if (indexes[19] == 0) {

				udfFldsLabel += "\"" + "Soft Bounces  " + "\"" + ",";
			}
			if (indexes[20] == 0) {

				udfFldsLabel += "\"" + "Blocked Bounce " + "\"" + ",";
			}
			if (indexes[21] == 0) {

				udfFldsLabel += "\"" + "Other Bounce " + "\"" + ",";
			}

			if (indexes[22] == 0 && urls) {
				udfFldsLabel += "\"" + "Urls " + "\"" + ",";
			}
			sb = new StringBuffer();
			sb.append(udfFldsLabel);
			sb.append("\r\n");
			bw.write(sb.toString());

			int size = 1000;
			List<CampaignReport> campReportList = null;
			for (int i = 0; i < count; i += size) {
				sb = new StringBuffer();

				// pageSize =
				// Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
				//(String) sourceTypeLbId.getSelectedItem().getValue()
				campReportList = campaignReportDao.getAllReports(userId.toString(),campName, fromDateCal.toString(),
						toDateCal.toString(), "--All--", 0, count,
						orderby_colName, desc_Asc);
				logger.info("size    :"+campReportList.size());
				List<Object[]> bounceCategoryList = null;
					bounceCategoryList = bounceDao.getCategoryPercentageByUser(userId.toString(), campName);
				StringBuffer crIdList = new StringBuffer();
				String query = getCampaignReportsCsv(fromDateCal, toDateCal, indexes[3] == 0, urls);
				if (query == null) {
					return;
				}

				// logger.info("query is ::::::::: "+query);
				JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();
				jdbcResultsetHandler.executeStmt(query);

				long totalCount = jdbcResultsetHandler.totalRecordsSize();

				if (totalCount == 0) {
					MessageUtil.setMessage("Campaign reports are not available for selected time-period", "red");
					logger.error("no campaigns");
					return;
				}
				ResultSet resultSet = jdbcResultsetHandler.getResultSet();

				StringBuffer sbb = new StringBuffer();
				while (resultSet.next()) {
					if (sbb.length() > 0)
						sbb.append(Constants.DELIMETER_COMMA);
					sbb.append(resultSet.getLong("cr_id"));
				}
				resultSet.beforeFirst();
				StringBuffer urlHeaderBuf = null;
				Set<String> headerUrls = null;
				List<Object[]> urlList = null;
				List<Object[]> campUrlList = null;
				if (urls) {
					 if(!=null && bounceCategoryList.size()>0){ 
					urlList = clicksDao.getClickedUrls(
							crIdList); 
										 * serverFromDateCal.toString(),
										 * serverToDateCal.toString(),
										 
					
					 * List<String> idleUrls =
					 * clicksDao.getNotClickedUrls(crIdList);
					 
					campUrlList = clicksDao.getCampUrls(crIdList.toString());
					// if(urlList!=null && urlList.size()>0){
					headerUrls = new LinkedHashSet<String>();
					if (campUrlList != null) {

						for (Object[] obj : campUrlList) {
							if (obj[1] != null)
								headerUrls.add((String) obj[1]);
						}

						urlHeaderBuf = new StringBuffer();
						for (String currUrl : headerUrls) {

							if (urlHeaderBuf.length() > 0)
								urlHeaderBuf.append(",");

							urlHeaderBuf.append("\"").append(currUrl).append("\"");
						}

					}
				}

				Map<Long, Long[]> bounceCatMap = getHardBounce(crIdList.toString());
				logger.info("List size :" + bounceCategoryList.size());

				if (campReportList != null && campReportList.size() > 0)
					for (CampaignReport camp : campReportList) {
						if (indexes[0] == 0) {
							sb.append("\"");
							sb.append(MyCalendar.calendarToString(camp.getSentDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
							sb.append("\",");
						}
						if (indexes[1] == 0) {
							sb.append("\"");
							sb.append(camp.getCampaignName());
							sb.append("\",");
						}
						if (indexes[2] == 0) {
							sb.append("\"");
							sb.append(getCampRepLists(camp.getCrId()));
							sb.append("\",");
						}
						if (indexes[3] == 0) {
							sb.append("\"");
							sb.append(camp.getSent());
							sb.append("\",");
						}
						if (indexes[4] == 0) {
							sb.append("\"");
							sb.append(Utility.getPercentage(camp.getOpens(),camp.getSent() - camp.getBounces(),2));
							sb.append("\",");
						}
						if (indexes[5] == 0) {
							sb.append("\"");
							sb.append(Utility.getPercentage(camp.getClicks(),camp.getOpens(),2));
							sb.append("\",");
						}
						if (indexes[6] == 0) {
							sb.append("\"");
							sb.append(Utility.getPercentage(camp.getUnsubscribes(),camp.getOpens(),2));
							sb.append("\",");
						}
						if (indexes[7] == 0) {
							sb.append("\"");
							sb.append(Utility.getPercentage(camp.getSpams(),camp.getSent() -camp.getBounces(),2));
							sb.append("\",");
						}

						long hardBounceCount = 0;
						long softBounceCount = 0;
						long sent = 0;
						Long objLong = null;
						if (bounceCategoryList != null && bounceCategoryList.size() > 0) {
							for (Object object : bounceCategoryList) {
								objLong = null;
								Object[] obj = (Object[]) object;
								try {
									objLong = (Long) obj[2];
								} catch (Exception e) {
									logger.error(e);
								}

								if (objLong == null) {
									continue;
								}
								if (!objLong.equals(camp.getCrId())) {
									continue;
								}
								if (indexes[8] == 0) {
									if (((String) obj[0]).startsWith(BounceCategories.DROPPED)) {
										sb.append("\"");
										sb.append((Utility.getPercentage(((Long) obj[1]), sent, 2) + ""));
										sb.append("\",");
									}
								}

								if (indexes[9] == 0) {
									if (((String) obj[0]).startsWith(BounceCategories.BOUNCED)) {
										hardBounceCount += ((Long) obj[1]).longValue();
										sb.append("\"");
										sb.append(Utility.getPercentage(hardBounceCount, sent, 2) + "");
										sb.append("\",");

									}

									if (indexes[10] == 0) {
										if (((String) obj[0]).startsWith(BounceCategories.SOFT_BOUNCED)) {
											softBounceCount += ((Long) obj[1]).longValue();
											sb.append("\"");
											sb.append(Utility.getPercentage(softBounceCount, sent, 2) + "");
											sb.append("\",");
										}

									}

									if (indexes[11] == 0) {
										if (((String) obj[0]).startsWith(BounceCategories.BLOCKED)) {
											sb.append("\"");
											sb.append(Utility.getPercentage(((Long) obj[1]), sent, 2) + "");
											sb.append("\",");
										}
									}

								}

								if (indexes[12] == 0) {
									if (((String) obj[0]).startsWith(BounceCategories.OTHERS)) {
										sb.append("\"");
										sb.append(Utility.getPercentage(((Long) obj[1]), sent, 2) + "");
										sb.append("\",");
									}

								}
							}
						} else {
							if (indexes[8] == 0) {
								sb.append("\"");
								sb.append(("0"));
								sb.append("\",");
							}

							if (indexes[9] == 0) {
								sb.append("\"");
								sb.append("0");
								sb.append("\",");

							}
							if (indexes[10] == 0) {
								sb.append("\"");
								sb.append("0");
								sb.append("\",");
							}

							if (indexes[11] == 0) {
								sb.append("\"");
								sb.append("0");
								sb.append("\",");
							}

							if (indexes[12] == 0) {
								sb.append("\"");
								sb.append("0");
								sb.append("\",");
							}
						}
						Long[] allBounces = bounceCatMap != null ? bounceCatMap.get(camp.getCrId()) : null;
						
						 * if(indexes[13]==0){ sb.append("\"");
						 * sb.append(Utility.getPercentage(allBounces != null &&
						 * (Long)allBounces[4] != null ? allBounces[4] :
						 * 0,camp.getSent(), 2)); sb.append("\","); }
						 
						if (indexes[13] == 0) {
							sb.append("\"");
							sb.append(camp.getOpens());
							sb.append("\",");
						}
						if (indexes[14] == 0) {
							sb.append("\"");
							sb.append(camp.getClicks());
							sb.append("\",");
						}
						if (indexes[15] == 0) {
							sb.append("\"");
							sb.append(camp.getUnsubscribes());
							sb.append("\",");
						}
						if (indexes[16] == 0) {
							sb.append("\"");
							sb.append(camp.getSpams());
							sb.append("\",");
						}
						if (indexes[17] == 0) {
							sb.append("\"");
							sb.append(String
									.valueOf(allBounces != null && (Long) allBounces[0] != null ? allBounces[0] : 0));
							sb.append("\",");
						}
						if (indexes[18] == 0) {
							sb.append("\"");
							sb.append(String
									.valueOf(allBounces != null && (Long) allBounces[1] != null ? allBounces[1] : 0));
							sb.append("\",");
						}
						if (indexes[19] == 0) {
							sb.append("\"");
							sb.append(String
									.valueOf(allBounces != null && (Long) allBounces[2] != null ? allBounces[2] : 0));
							sb.append("\",");
						}
						if (indexes[20] == 0) {
							sb.append("\"");
							sb.append(String
									.valueOf(allBounces != null && (Long) allBounces[3] != null ? allBounces[3] : 0));
							sb.append("\",");
						}
						if (indexes[21] == 0) {
							sb.append("\"");
							sb.append(String
									.valueOf(allBounces != null && (Long) allBounces[4] != null ? allBounces[4] : 0));
							sb.append("\",");
						}
						StringBuffer innerSB = new StringBuffer();
						if(urls) {
						urlList = clicksDao.getClickedUrls(new StringBuffer(camp.getCrId().toString()));
							for(Object[] row: urlList)
							{
							String url = (String) row[1];
							if(innerSB.length()>0)
							  innerSB.append(";::;");
							  innerSB.append(url);
							}
						}
						if (indexes[22] == 0) {
							sb.append("\"");
							sb.append(innerSB!=null && innerSB.toString().length()>0 ?innerSB.toString():"NA");
							sb.append("\",");
						}
						sb.append("\r\n");
					}

				bw.write(sb.toString());
				campReportList = null;
				sb = null;
				// System.gc();
			}
			bw.flush();
			bw.close();
			Filedownload.save(file, "text/plain");
		} catch (IOException e) {
			logger.error("Exception ::", e);

		}
		logger.debug("-- exit --");

	}*/
	
	
	public void exportCSV(String ext,int[] indexes,boolean urls) throws Exception {
		
		long starttime = System.currentTimeMillis();
		String innerSB = null;
		boolean urlsOnly=true;
		
		/*******************************************************************************/

		/*Calendar serverFromDateCal = getFromDateCal();
		Calendar serverToDateCal = getToDateCal();
		logger.info("from and to date tostring "+serverFromDateCal.toString()+"   "+serverToDateCal);*/
		/*******************************************************************************/
		
		Campaigns camp11 = (Campaigns) session.getAttribute("campaign");
		String campName=null;
		if (camp11!= null) {
			campName = camp11.getCampaignName();
		}
		
		String query =  getCampaignReportsCsv(fromDateCal,toDateCal, indexes[3] == 0, urls,campName);
		if( query == null)
		{
			return ;
		}
		
		//logger.info("query is ::::::::: "+query);
		JdbcResultsetHandler jdbcResultsetHandler = new JdbcResultsetHandler();
		jdbcResultsetHandler.executeStmt(query);
		
		long totalCount = jdbcResultsetHandler.totalRecordsSize();
		
		if(totalCount == 0) {
			MessageUtil.setMessage("Campaign reports are not available for selected time-period", "red");
			logger.error("no campaigns");
			return;
		}
		ResultSet resultSet = jdbcResultsetHandler.getResultSet();
		
		StringBuffer crIdList = new StringBuffer();
		while(resultSet.next())
		{
			if(crIdList.length() > 0) crIdList.append(Constants.DELIMETER_COMMA);
			
			crIdList.append(resultSet.getLong("cr_id")); 
		}
		//crIdList.deleteCharAt(crIdList.length()-2);
		resultSet.beforeFirst();
		StringBuffer urlHeaderBuf = null;
		Set<String> headerUrls = null;
		List<Object[]> urlList = null;
		List<Object[]> campUrlList = null;
		if(urls) {
			urlList = clicksDao.getClickedUrls(crIdList); /*serverFromDateCal.toString(), serverToDateCal.toString(),*/
			/*List<String> idleUrls = clicksDao.getNotClickedUrls(crIdList);*/
			campUrlList = clicksDao.getCampUrls(crIdList.toString());
			
			headerUrls = new LinkedHashSet<String>();
			if(campUrlList!=null){
				
				for(Object[] obj : campUrlList){
					if(obj[1]!=null)
					headerUrls.add((String)obj[1]);
				}
				
				urlHeaderBuf = new StringBuffer();
				for(String currUrl: headerUrls) {
						
					if(urlHeaderBuf.length()>0) urlHeaderBuf.append(",");
					
					urlHeaderBuf.append("\"").append(currUrl).append("\"");
				}
				
				
			}
		}
		
		
		
		Map<Long, Long[]> bounceCatMap = getHardBounce(crIdList.toString());
		int urlColumn = 0;
		for(int i=0;i<indexes.length-1;i++) {
				
				if(indexes[i]==0) {
					urlColumn = urlColumn+1;
					urlsOnly = false;
				}
			}
			
			if( urlsOnly && urlHeaderBuf.length()==0) {
				
				MessageUtil.setMessage("Your campaigns within this time-frame did not contain any URLs. Please change the time-period or include more fields to generate report.", "red");
				return;
			}
			JdbcResultsetHandler exportJdbcResultsetHandler = null;
			String exportQuery = null;
			String usersParentDirectory = null;
			File downloadDir = null; String filePath = null; StringBuffer sb = null; ResultSet exportresResultSet = null;
			try {
		
				ext = ext.trim();
				//String Name = userName;
				usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
				String exportDir = usersParentDirectory + "/" + userName + "/Export/";
				downloadDir = new File(exportDir);
				if(downloadDir.exists()){
					try {
						FileUtils.deleteDirectory(downloadDir);
					} catch (Exception e) {
						logger.error("Exception caught ",e);
					}
				}
				if(!downloadDir.exists()){
					downloadDir.mkdirs();
				}
				filePath = exportDir + "Email_Camp_Report_" + System.currentTimeMillis() + "." + ext;

				File file = new File(filePath);
				BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
				
				
				String emailName = null;
				boolean isLike = false;
				if (filter == 1) {
					emailName = searchTextStr;
					isLike = true;
				} else if (filter == 2) {
					emailName = selectedItem.getLabel();
					isLike = false;
				}
				
				
				int count = campaignReportDao.getReportCount(userId.toString(), campName, isLike,"--All--", fromDateCal.toString(), toDateCal.toString());
				if (count == 0) {
					Messagebox.show("No promotion report found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
					return;
				}
				
				sb = new StringBuffer();
									
				StringBuffer header = new StringBuffer();
				StringBuffer fields = new StringBuffer("select id");
				for(int i=0;i<indexes.length-1;i++) {
						
					if(indexes[i]==0) {
							if(header.length() > 0) header.append(Constants.DELIMETER_COMMA);
							header.append("\"").append(Constants.emailCampaignHeaderFields[i]).append("\"");
							fields.append(", ").append(Constants.emailCampaignDbFields[i]);
							urlsOnly=false;
					}
				}// for
				if(urls){
					fields.append(", ");fields.append("urls");
					if(urlHeaderBuf !=null  ) {
						
						
						//for (String url : headerUrls) {
						if(header.length() > 0) header.append(Constants.DELIMETER_COMMA);
						header.append(urlHeaderBuf.toString());
						
						//}// for
					}
				}
				
				exportQuery = fields.toString();
				exportQuery += " from tempexportreport where 1=2 ";
				sb.append(header);
				//logger.debug("sb==="+sb.toString());
				sb.append("\r\n");
					
				bw.write(sb.toString());
				//System.gc();
				
				
				exportJdbcResultsetHandler = new JdbcResultsetHandler();
				exportJdbcResultsetHandler.executeStmt(exportQuery, true);
				exportresResultSet = exportJdbcResultsetHandler.getResultSet();
				
				logger.info("export query is :::: "+exportQuery);	
				/*Long pk=(long) 1;*/
				int increment = 0;
				double random = 0;
				while(resultSet.next())
				{
					random = Math.random();
					exportresResultSet.moveToInsertRow();
					increment ++;
					
					long sentCount=resultSet.getLong("sent");
					int openCount=resultSet.getInt("opens");
					int clickCount=resultSet.getInt("clicks");
					int unsubCount=resultSet.getInt("unsubscribes");
					int spamCount=resultSet.getInt("spams");
					int bounceCount=resultSet.getInt("bounces");
					
					Long[] allBounces = bounceCatMap != null ? bounceCatMap.get(resultSet.getLong("cr_id")) : null;
					/*if(allBounces != null) {
						for (int i = 0; i < allBounces.length; i++) {
							
							logger.debug("bounces ===>"+allBounces[i]);
						}
					}*/
					exportresResultSet.updateString("id", userId+""+random+""+System.currentTimeMillis());
					
				if(indexes[0]==0)	{
					exportresResultSet.updateString("domain", resultSet.getString("campaign_name"));
				}
					
				if(indexes[1]==0) {
					Calendar cal = null;
					Timestamp t=resultSet.getTimestamp("sent_date");
					if(t!=null)
					{
						if(new Date(t.getTime()) != null) {
							cal = Calendar.getInstance();
							cal.setTime(t);
						}
						String sentDate= cal == null ? "" : MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone);
						exportresResultSet.updateString("first_name", sentDate);
					}
				}
					
				if(indexes[2]==0)	{
						exportresResultSet.updateString("mobile_status", resultSet.getString("subject"));
				}
				if(indexes[3]==0)	{
					exportresResultSet.updateString("subscription_type", resultSet.getString("list_name"));
				}
				if(indexes[4]==0)	{
					exportresResultSet.updateString("external_id", resultSet.getString("sent"));
				}
				if(indexes[5] == 0){
					exportresResultSet.updateString("udf10", Utility.getPercentage(openCount,sentCount - bounceCount,2));
				}
				if(indexes[6]==0){
					exportresResultSet.updateString("udf11", Utility.getPercentage(clickCount,openCount,2));
				}
				if(indexes[7]==0){
					exportresResultSet.updateString("udf12", Utility.getPercentage(unsubCount,openCount,2));
				}
				if(indexes[8]==0){
					exportresResultSet.updateString("udf13", Utility.getPercentage(spamCount, sentCount - bounceCount,2));
				}
				if(indexes[9]==0){
					exportresResultSet.updateString("udf14", Utility.getPercentage(allBounces != null && (Long)allBounces[0] != null ? allBounces[0] : 0,sentCount, 2));
				}
				if(indexes[10]==0){
					exportresResultSet.updateString("udf15", Utility.getPercentage(allBounces != null && (Long)allBounces[1] != null ? allBounces[1] : 0,sentCount, 2));
				}
				if(indexes[11]==0){
					exportresResultSet.updateString("home_store", Utility.getPercentage(allBounces != null && (Long)allBounces[2] != null ? allBounces[2] : 0,sentCount, 2));
				}
				if(indexes[12]==0){
					exportresResultSet.updateString("zip", Utility.getPercentage(allBounces != null && (Long)allBounces[3] != null ? allBounces[3] : 0,sentCount, 2));
				}
				if(indexes[13]==0){
					exportresResultSet.updateString("mobile_phone", Utility.getPercentage(allBounces != null && (Long)allBounces[4] != null ? allBounces[4] : 0,sentCount, 2));
				}
				if(indexes[14]==0){
					exportresResultSet.updateInt("udf1", openCount);
				}
				/*{"Email Name","Date Sent","Subject ","List(s)","Sent","Opens %","Clicks %","Unsubs %","Marked as Spam %","Dropped %","Hard Bounces %","Soft Bounce %","Blocked Bounce %",
				 "Other Bounce %","Unique Opens ","Unique Clicks ","Unsubs ","Marked as Spam ","Dropped","Hard Bounces ","Soft Bounce","Blocked Bounce","Other Bounce"};
				cr_id, campaign_name, sent_date, content, subject, sent, opens, clicks, unsubscribes, bounces, status, user_id, place_holders_str, source_type, spams, configured, preference_count
				{"company_name","label","subject ","configured_ph_val_str","from_name","to_name","reply_email","status","draft_status","editor_type","schedule_type","web_link_text","web_link_urlText",
				"permission_remainder_text","address_one","address_two","city","state","country","phone","content_type","place_holders_type","addrs_type"};*/
				if(indexes[15]==0){
					exportresResultSet.updateInt("udf2", clickCount);
				}
				if(indexes[16]==0){
					exportresResultSet.updateInt("udf3", unsubCount);
				}
				if(indexes[17]==0){
					exportresResultSet.updateInt("udf4", spamCount);
				}
				if(indexes[18]==0){
					exportresResultSet.updateString("udf5", String.valueOf(allBounces != null && (Long)allBounces[0] != null ? allBounces[0] : 0));
				}
				if(indexes[19]==0){
					exportresResultSet.updateString("udf6", String.valueOf(allBounces != null && (Long)allBounces[1] != null ? allBounces[1] : 0));
				}
				if(indexes[20]==0){
					exportresResultSet.updateString("udf7", String.valueOf(allBounces != null && (Long)allBounces[2] != null ? allBounces[2] : 0));
				}
				if(indexes[21]==0){
					exportresResultSet.updateString("udf8", String.valueOf(allBounces != null && (Long)allBounces[3] != null ? allBounces[3] : 0));
				}
				if(indexes[22]==0){
					exportresResultSet.updateString("udf9", String.valueOf(allBounces != null && (Long)allBounces[4] != null ? allBounces[4] : 0));
				}
				Long crId = resultSet.getLong("cr_id");
				//logger.info("cr_id is ::: "+crId);
				//urlList!=null && urlList.size()>0 && urls
				if(urls) {
				innerSB = Constants.STRING_NILL;
					
				for(String eachUrl : headerUrls)
				{
					String urlValue = Constants.STRING_NILL;
					for(Object[] row: urlList)
					{
						String url = (String) row[1];
						if(url == null)continue;	
						if(crId.equals(row[0]) && eachUrl.equals(url) )
						{
						urlValue = row[2].toString();
						break;
						}
					}
					if(urlValue.length()==0)
					{
						
						
					for(Object[] eachCampaignUrl : campUrlList)
					{
						if(eachCampaignUrl[1]==null)continue;
						if(crId.equals(eachCampaignUrl[0]) && eachUrl.equals(eachCampaignUrl[1]))
						{
							urlValue = "0";
							break;
						}
					}
					}
					
					if(innerSB.length() > 0) innerSB = innerSB.concat(";::;");
					innerSB = innerSB.concat(urlValue.length() != 0 ? urlValue  : "N/A"); 
				}
					
					exportresResultSet.updateString("urls", innerSB);
				}
				//logger.debug("before inserting a row ???"+exportresResultSet.getRow());
				exportresResultSet.insertRow();
				exportresResultSet.moveToCurrentRow();
				}//while
				
				
				
				OCCSVWriter csvWriter = null;
				try{
					csvWriter = new OCCSVWriter(bw);
					csvWriter.writeAll(exportresResultSet, false, 1, urlColumn+2);
					
				}
				catch(Exception e){
					logger.info("Exception while writing into file ", e);
				}finally{
					
					bw.flush();
					csvWriter.flush();
					bw.close();
					csvWriter.close();
				}
				
				
				Filedownload.save(file, "text/csv");
				logger.debug("exited");
				
				logger.debug("total time taken :: ReportController :: exportCSV   "
						+ (System.currentTimeMillis() - starttime));
				
			} catch (Exception e) {
				logger.error("** Exception : " , e);
			}finally{
				exportJdbcResultsetHandler.rollback();
				if(jdbcResultsetHandler != null)jdbcResultsetHandler.destroy();
				if(exportJdbcResultsetHandler != null)exportJdbcResultsetHandler.destroy();
				query = null; exportQuery = null; jdbcResultsetHandler = null; exportJdbcResultsetHandler = null;
				userName = null; usersParentDirectory = null;
				downloadDir = null; filePath = null; sb = null;  exportresResultSet = null;
				//System.gc();
			}
			
		
		
	}


	public String getCampaignReportsCsv(Calendar serverFromDateCal, Calendar serverToDateCal, boolean isListChecked,
			boolean isUrlChecked, String campName) {

		long starttime = System.currentTimeMillis();

		String query = null;
		try {

			if (serverToDateCal.compareTo(serverFromDateCal) < 0) {

				MessageUtil.setMessage("'To' date must be later than 'From' date.", "color:red", "TOP");

				logger.debug("total time taken :: ReportController :: getCampaignReportsCsv   "
						+ (System.currentTimeMillis() - starttime));

				return query;
			}

			String tempSourceType = null;

			//tempSourceType = (String) sourceTypeLbId.getSelectedItem().getValue();
			tempSourceType = "--All--";
			String checkCon = "";
			if (userId != null) {
				checkCon += " c.user_id IN (" + userId + ") ";
			}
			if (serverFromDateCal.toString() != null && serverToDateCal.toString() != null) {
				checkCon += " AND  c.sent_date between '" + serverFromDateCal.toString() + "' AND '"
						+ serverToDateCal.toString() + "' ";
			}
			if (tempSourceType != null && !tempSourceType.equalsIgnoreCase("--All--")) {
				checkCon += " AND c.source_type like '" + tempSourceType + "%'  ";
			}
			String listsSubqry = Constants.STRING_NILL;
			String selectClause = Constants.STRING_NILL;
			if (isListChecked) {

				listsSubqry += ", campreports_lists cl ";
				checkCon += " AND c.cr_id=cl.cr_id";
				selectClause += " ,cl.list_name ";
			}

			// if(!isListChecked) {
			if(campName!=null && !campName.isEmpty()) {
				query = " SELECT c.cr_id, c.sent, c.opens, c.clicks, c.unsubscribes, c.spams, c.bounces, c.sent_date, "
						+ "c.campaign_name, c.subject " + selectClause + " FROM campaign_report c " + listsSubqry
						+ " WHERE c.campaign_name = '"+campName+"' AND " + checkCon + " AND c.status ='sent' ORDER BY  c.sent_date DESC";
			}else {
				query = " SELECT c.cr_id, c.sent, c.opens, c.clicks, c.unsubscribes, c.spams, c.bounces, c.sent_date, "
						+ "c.campaign_name, c.subject " + selectClause + " FROM campaign_report c " + listsSubqry
						+ " WHERE " + checkCon + " AND c.status ='sent' ORDER BY  c.sent_date DESC";
			}
			// }else {
			/*
			 * query =
			 * "select c.cr_id, c.sent, c.opens, c.clicks, c.unsubscribes, c.spams, c.bounces, c.sent_date, "
			 * +
			 * "c.campaign_name, c.subject, cl.list_name FROM campaign_report c left outer join "
			 * + "campreports_lists cl on c.cr_id=cl.cr_id  WHERE " + checkCon +
			 * " c.status ='sent' ORDER BY  c.sent_date DESC"; }
			 */

			logger.debug("final qry ::" + query);
			return query;

		} catch (Exception e) {
			logger.error("** Exception :", (Throwable) e);
		}

		logger.debug("total time taken :: ReportController :: getCampaignReportsCsv   "
				+ (System.currentTimeMillis() - starttime));

		return query;

	}

	public Map<Long, Long[]> getHardBounce(String crIdList) {

		long starttime = System.currentTimeMillis();
		Long objLong = null;

		/*
		 * if(categoriesList == null) {
		 */
		List<Object[]> bounceCatList = null;
		try {
			bounceCatList = bounceDao.getBounceCategories(crIdList);
			// categoriesList =
			// bounceDao.getCategoryPercentageByUser(userId,null);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("Exception", e1);
		}
		if (bounceCatList == null)
			return null;
		Map<Long, Long[]> retMap = new HashMap<Long, Long[]>();
		// }
		for (Object[] object : bounceCatList) {
			objLong = null;
			Object[] obj = (Object[]) object;

			/*
			 * try { objLong = (Long)obj[2]; } catch (Exception e) {
			 * logger.error(e);}
			 * 
			 * if(objLong == null) { continue; }
			 * 
			 * if(!objLong.equals(crId)) { continue; }
			 */
			Long[] bounces = (Long[]) retMap.get((Long) obj[2]);

			if (bounces == null) {
				bounces = computeBouncePerc(obj, null);

			} else {
				bounces = computeBouncePerc(obj, bounces);

			}
			retMap.put((Long) obj[2], bounces);
		}
		
		return retMap;

	}

	public Long[] computeBouncePerc(Object[] obj, Long[] categoryArr) {

		long hardBounceCount = 0;
		long softBounceCount = 0;
		long blockedBounceCount = 0;
		long droppedBounceCount = 0;
		long otherBounceCount = 0;

		if (((String) obj[0]).startsWith(BounceCategories.NON_EXISTENT_ADDRESS)) {

			hardBounceCount += ((Long) obj[1]).longValue();

		} else if (((String) obj[0]).startsWith(BounceCategories.UNDELIVERABLE)) {

			hardBounceCount += ((Long) obj[1]).longValue();

		} else if (((String) obj[0]).startsWith(BounceCategories.SOFT_BOUNCED)) {
			// mFullLbl.setValue(obj[1] + "");
			softBounceCount += ((Long) obj[1]).longValue();

		} else if (((String) obj[0]).startsWith(BounceCategories.BLOCKED)) {

			blockedBounceCount = ((Long) obj[1]).longValue();

		} else if (((String) obj[0]).startsWith(BounceCategories.DROPPED)) {

			droppedBounceCount = ((Long) obj[1]).longValue();

		} else if (((String) obj[0]).startsWith(BounceCategories.BOUNCED)) {
			// nonExistLbl.setValue(obj[1] + "");
			hardBounceCount += ((Long) obj[1]).longValue();

		}

		else if (((String) obj[0]).startsWith(BounceCategories.OTHERS)) {

			otherBounceCount = ((Long) obj[1]).longValue();

		}

		Long bounces[] = { droppedBounceCount, hardBounceCount, softBounceCount, blockedBounceCount, otherBounceCount };
		if (categoryArr != null) {

			for (int i = 0; i < categoryArr.length; i++) {

				if (categoryArr[i] > bounces[i]) {
					bounces[i] = categoryArr[i];
				}

			}

		}

		return bounces;
	}

	
	public void onClick$backBtnId() {
		Redirect.goTo(PageListEnum.CAMPAIGN_CAMPAIGN_LIST);
	}

}
